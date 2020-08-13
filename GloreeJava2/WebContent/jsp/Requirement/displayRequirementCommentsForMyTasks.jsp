<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String dRCIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dRCIsLoggedIn == null) || (dRCIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	// authorizatoin 
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);

	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dRCIsMember = false;
	SecurityProfile dRCSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dRCSecurityProfile.getRoles().contains("MemberInProject" + requirement.getProjectId())){
		dRCIsMember = true;
	}
	%>

<%if(dRCIsMember){ 
	
	

%>
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<div id='displayRequirementComments' class='alert alert-success' style='width:100%'> 
		<%
		String printable = request.getParameter("printable");
		if ((printable == null) || (printable.equals(""))) {
			%>
			<%
		}
		%>


			<%
			if ((printable == null) || (printable.equals(""))) {
			%>
			<!-- 
			<table>
				<tr>
					<td align='center'>
						<textarea name="comment_note<%=requirementId %>" id="comment_note<%=requirementId %>" rows="3" cols="50"></textarea>
						<br>
						<input type='button' class='btn btn-primary btn-sm' name='addComment<%=requirementId%>' id='addComment<%=requirementId%>' value='  Add a Comment  ' 
							onclick='						
							addRequirementCommentForMyTask(<%=requirementId%>,<%=requirement.getFolderId() %>)
							'>
							
						&nbsp;&nbsp;
						<input type='button' class='btn btn-danger btn-sm' name='closeComment<%=requirementId%>' id='closeComment<%=requirementId%>' value='  Close  ' 
							onclick='document.getElementById("displayCommentsDiv<%=requirement.getRequirementId() %>").style.display="none"'	
						>
							
							
					</td>
				</tr>
			</table>
			-->
			<%} %>
		<div id='commentListDiv<%=requirementId%>'>
			<table class='paddedTable' width='100%' >
	
				<%
					ArrayList comments = RequirementUtil.getRequirementComments(requirementId, databaseType);
						    if (comments != null){
						    	Iterator i = comments.iterator();
						    	int j = 0;
						    	String cellStyle = "normalTableCell";
						    	
						    	while ( i.hasNext() ) {
						    		j++;
							    		
						    		// Now for each row in the array list, print the data out.
						    		if ((j%2) == 1){
						    			cellStyle = "normalTableCell";
						    		}
						    		else {
						    			cellStyle = "altTableCell";	
						    		}
						    		
						    		Comment commentObject = (Comment) i.next();
				%>
			 	<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
			onmouseover=  "this.style.background='#E5EBFF';" onmouseout=  "this.style.background='white';">
			 		<td >
			 			<span class='normalText'">
			 		
				 		
				 		<%=commentObject.getCommenterEmailId() %> : <%=commentObject.getCommentDate() %>
			 			<br>
			 			<img src="/GloreeJava2/images/comment16.png" border="0">
			 			<%=commentObject.getHTMLFriendlyCommentNote() %>
			 			</span>
			 		</td>		
			 	</tr>
				 <%
				    	}
				    }
				%>
			</table>
		</div>
	</div>
	
<%}%>  