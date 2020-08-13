package batchJobs.Temp;
import java.util.ArrayList;
import java.util.Iterator;

import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.FieldStart;
import com.aspose.words.FieldType;
import com.aspose.words.Font;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeList;
import com.aspose.words.NodeType;
import com.aspose.words.Run;
import com.aspose.words.Section;
import com.aspose.words.SectionCollection;
import com.gloree.beans.Hyperlink;
import com.gloree.beans.Requirement;

    public class testSNWordDoc {

        // API Called: https://<instance>.service-now.com/api/now/table/incident?sysparm_limit=1

        public static void main(String[] args) {

            try {

               System.out.println("srt starting testSNWordDoc");
               
               // lets find and open the file.
   			
             String inputFilePath = "E:\\inputFile.docx";
   			Document doc = new Document(inputFilePath);
   			DocumentBuilder builder = new DocumentBuilder(doc);

   			
   			// lets loop through sections
   			SectionCollection sections = doc.getSections();
   			for (Section sec: sections){
   				// lets print the section content
   				if (sec.getText().contains("TCREPEAT")){
   					System.out.println("\n\n\n------> content of section is " + sec.getText());
   					
   					// lets make 10 copies of it. 
   					for (int i=0; i<10;i++){
   						Section clone = sec;
   						
   						// look for URLs and replace them
   						String cloneText = sec.getText();
   						cloneText.replace("req:name", "This is the req " + i + " name");
   						Section c = new Section(cloneText);
   						
   						
   						sec.prependContent(clone);
   					}
   				}
   				
   			}
   			
   			/*
   			///////////////////////////////////////////////////////////////////////////////////
   			///////////////////////////////////////////////////////////////////////////////////
   			//
   			// SRT
   			// 
   			// THIS WILL WORK : SEE THE createRequirementsFromWordTemplateByStyleUpdatable code
   			// this will show you how you can read a doc with all the text of a particular style
   			// find a way to insert the run multiple times 
   			// Then find if the run text has any req: and parse them and replace them with apropriate code
   			//
   			//
   			///////////////////////////////////////////////////////////////////////////////////
   			///////////////////////////////////////////////////////////////////////////////////

   			// srt loop through all the styles and find the run with style intense emphasis
   			
			// Get all runs from the document.
			NodeCollection runs = doc.getChildNodes(NodeType.RUN, true);
			// Look through all paragraphs to find those with the specified
			// style.
			Iterator i = runs.iterator();
			String lastRunStyle = "";
			String lastRunText = "";
			// this array list containts a list of Runs that comprise the lastRunStyle.
			// we will use them to empty out all the runs, if we decide that the lastRunStyle
			// was chosen by the user.
			ArrayList lastRuns = new ArrayList();
			
			
			// we declare the run variable outside the while loop
			// so that the value of the last current run is available
			// at the end of while loop for processing.
			// NOTE
			// NOTE
			// NOTE : we are no longer using the firstruninlaststyle and firstrunincurrentsytle
			// as we are getting lastRuns[0] to give us the first run object
			// NOTE
			// NOTE
			// NOTE
			Run run = null ;
			Run firstRunInCurrentStyle = null;
			Run firstRunInLastStyle = null;
			
			while (i.hasNext()) {
				// we run into a problem where runs of the same style are split
				// into different nodes. so we have this logic that 
				// combines different adjacent runs of same style into one string. 
				run = (Run) i.next();
				String runText = run.getText();
				String runStyle = run.getFont().getStyleName();

				
				if (runStyle.equals(lastRunStyle)){
					// continueing the same old run style.
					// so lets add the run text to lastRunText and capture this object
					// in the lastRun array list.
					// will come in handy when we decide to 
					lastRunText += runText;
					lastRuns.add(run);
					firstRunInLastStyle = firstRunInCurrentStyle;
				}
				else {
					// a new run style has begun.
					firstRunInCurrentStyle = run;
					
					
					if (!(lastRunText.equals(""))){
						// since a new run style has started, lets process
						// the old run style and its text.
						System.out.println("srt lastRunStyle is " + lastRunStyle );
						if (lastRunStyle.equals("Intense Emphasis")) {
							System.out.println("srt lastRunStyle matches . so lets preocess this :  " + lastRunStyle );
							// the last run style matches the styleName chosen by user to
							// create the req.
							
							Run firstRun = (Run) lastRuns.get(0);
							if (firstRun != null ){
								builder.moveTo(firstRun);
								builder.writeln();
								builder.writeln();
								builder.write("##SRT ADding text"  + "##" + " ");
								System.out.println("srt last runText is " + lastRunText);
								builder.write(lastRunText);
								builder.write("##SRT finished text"  + "##" + " ");
								builder.writeln();
								builder.writeln();
								
							}
							
						}
					}
					
					
					// lets loop through all the lastRuns and add them to the doc
					Iterator rI = lastRuns.iterator();
					while (rI.hasNext()){
						Run currentRun = (Run) rI.next();
						//builder. (currentRun);
						
					}
					
					// lets re-set the lastRunStyle / Text values to current run Style and text so that
					// we can catch all runs of this style.
					lastRunStyle = runStyle;
					lastRunText = runText;
					lastRuns = new ArrayList();
					lastRuns.add(run);
				}
				
			}

			*/
   			
   			String outputFilePath = "E:\\outputFile.docx";
				outputFilePath.replace(' ', '_');

				doc.save(outputFilePath);

            }  catch (Exception e) {

                e.printStackTrace();

            }

        }

    }