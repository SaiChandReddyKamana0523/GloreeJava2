package com.gloree.beans;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aspose.words.FieldStart;
import com.aspose.words.FieldType;
import com.aspose.words.Node;
import com.aspose.words.NodeType;
import com.aspose.words.Run;

/// <summary>
/// This "facade" class makes it easier to work with a hyperlink field in a Word document.
///
/// A hyperlink is represented by a HYPERLINK field in a Word document. A field in Aspose.Words
/// consists of several nodes and it might be difficult to work with all those nodes directly.
/// Note this is a simple implementation and will work only if the hyperlink code and name
/// each consist of one Run only.
///
/// [FieldStart][Run - field code][FieldSeparator][Run - field result][FieldEnd]
///
/// The field code contains a string in one of these formats:
/// HYPERLINK "url"
/// HYPERLINK \l "bookmark name"
///
/// The field result contains text that is displayed to the user.
/// </summary>
public class Hyperlink
{
  public Hyperlink(FieldStart fieldStart) throws Exception
  {
      if (fieldStart == null)
          throw new Exception("Argument 'fieldStart' is null");

      if (fieldStart.getFieldType() != FieldType.FIELD_HYPERLINK)
          throw new Exception("Field start type must be FieldHyperlink.");

      mFieldStart = fieldStart;

      // Find the field separator node.
      mFieldSeparator = FindNextSibling(mFieldStart, NodeType.FIELD_SEPARATOR);
      if (mFieldSeparator == null)
          throw new Exception("Cannot find field separator.");

      // Find the field end node. Normally field end will always be found, but in the example document
      // there happens to be a paragraph break included in the hyperlink and this puts the field end
      // in the next paragraph. It will be much more complicated to handle fields which span several
      // paragraphs correctly, but in this case allowing field end to be null is enough for our purposes.
      mFieldEnd = FindNextSibling(mFieldSeparator, NodeType.FIELD_END);

      // Field code looks something like [ HYPERLINK "http:\\www.myurl.com" ], but it can consist of several runs.
      String fieldCode = GetTextSameParent(mFieldStart.getNextSibling(), mFieldSeparator);
      Matcher match = gRegex.matcher(fieldCode.trim());
      if (match.matches())
      {
          mIsLocal = match.group(1) != null;    //The link is local if \l is present in the field code.
          mTarget = match.group(2);
      }
  }

  /// <summary>
  /// Gets or sets the display name of the hyperlink.
  /// </summary>
  public String getName() throws Exception
  {
      return GetTextSameParent(mFieldSeparator, mFieldEnd);
  }

  public void setName(String value) throws Exception
  {
      // Hyperlink display name is stored in the field result which is a Run
      // node between field separator and field end.
      Run fieldResult = (Run)mFieldSeparator.getNextSibling();
      fieldResult.setText(value);

      // But sometimes the field result can consist of more than one run, delete these runs.
      RemoveSameParent(fieldResult.getNextSibling(), mFieldEnd);
  }


  /// <summary>
  /// Gets or sets the target url or bookmark name of the hyperlink.
  /// </summary>
  public String getTarget()
  {
      return mTarget;
  }



  public String setTarget(String value) throws Exception
  {
      mTarget = value;
      return mTarget;
  }



  /// <summary>
  /// True if the hyperlink's target is a bookmark inside the document. False if the hyperlink is a url.
  /// </summary>
  /// <summary
  /// Goes through siblings starting from the start node until it finds a node of the specified type or null.
  /// </summary>
  private static Node FindNextSibling(Node startNode, int nodeType)
  {
      for (Node node = startNode; node != null; node = node.getNextSibling())
      {
          if (node.getNodeType() == nodeType)
              return node;
      }
      return null;
  }



  /// <summary>
  /// Retrieves text from start up to but not including the end node.
  /// </summary>
  private static String GetTextSameParent(Node startNode, Node endNode) throws Exception
  {
      if ((endNode != null) && (startNode.getParentNode() != endNode.getParentNode()))
          throw new Exception("Start and end nodes are expected to have the same parent.");

      StringBuilder builder = new StringBuilder();
      for (Node child = startNode; child != endNode; child = child.getNextSibling())
          builder.append(child.getText());
     
      return builder.toString();
  }



  /// <summary>
  /// Removes nodes from start up to but not including the end node.
  /// Start and end are assumed to have the same parent.
  /// </summary>

  private static void RemoveSameParent(Node startNode, Node endNode) throws Exception
  {
      if ((endNode != null) && (startNode.getParentNode() != endNode.getParentNode()))
          throw new Exception("Start and end nodes are expected to have the same parent.");

      Node curChild = startNode;
      while ((curChild != null) && (curChild != endNode))
      {
          Node nextChild = curChild.getNextSibling();
          curChild.remove();
          curChild = nextChild;
      }
  }



  private final Node mFieldStart;
  private final Node mFieldSeparator;
  private final Node mFieldEnd;
  private String mTarget;
  private boolean mIsLocal;



  /// <summary>
  /// RK I am notoriously bad at regexes. It seems I don't understand their way of thinking.
  /// </summary>

  private static Pattern gRegex = Pattern.compile(
      "\\S+" +            // one or more non spaces HYPERLINK or other word in other languages
      "\\s+" +            // one or more spaces
      "(?:\"\"\\s+)?" +    // non capturing optional "" and one or more spaces, found in one of the customers files.
      "(\\\\l\\s+)?" +    // optional \l flag followed by one or more spaces
      "\"" +                // one apostrophe
      "([^\"]+)" +        // one or more chars except apostrophe (hyperlink target)
      "\""                // one closing apostrophe
  );	 
 
}
