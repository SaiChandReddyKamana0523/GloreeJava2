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
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dRCIsMember = false;
	SecurityProfile dRCSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dRCSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		dRCIsMember = true;
	}
	
	
	User user = dRCSecurityProfile.getUser();
	String userEmailId = user.getEmailId();
	
	boolean isUserAnAdmin = false; 
	if (dRCSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
		isUserAnAdmin = true;
	}
	
	
	%>

<%if(dRCIsMember){ 
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);
	String timeZone = project.getProjectTimeZone();
	Double gmtDelta = project.getProjectGMTDelta();
	
	
	
	int folderId = requirement.getFolderId();
	
	String source = request.getParameter("source");
	if (source == null){
		source  = "";
	}
	String messageDisplayDiv = "";
	if (source.equals("requirementList")){
		messageDisplayDiv = "displayRDInFolderDiv" + requirementId;
	}
	else {
		messageDisplayDiv = "displayRequirementComments" + requirementId ;
	}

	///////////////////////////////SECURITY CODE ////////////////////////////
	// if the requirement worked on, doesn't belong to the project the user is 
	// currently logged into, then a user logged into project x is trying to 
	// hack into a req in project y by useing requirementId parameter.
	if (requirement.getProjectId() != project.getProjectId()) {
		return;
	}
	///////////////////////////////SECURITY CODE ////////////////////////////

%>
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>

<br>
<div class="panel panel-info" > 	
		
	<div class="panel-heading " > 
		<%=requirement.getRequirementFullTag() %>  Comments 
		
	</div>
	
		
	<div id='displayRequirementComments<%=requirementId%>' class='panel-body' > 
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
			<table>
				<tr>
					<td align='left'>
						<span class='normalText'>
							<textarea name="comment_note<%=requirementId%>" id="comment_note<%=requirementId%>" rows="4" cols="100" ></textarea>
						</span>
					</td>	
				</tr>
				<tr>
					<td align='center'>
					
						<span class='normalText'>
							<input type='Button' class='btn btn-primary btn-xs'  name='addComment<%=requirementId%>' id='addComment<%=requirementId%>' value='  Add Comment ' 
							onclick='addRequirementComment(<%=requirementId%>,<%=folderId%>,"<%=source%>" )'>
							
							
							<%if (source.equals("requirementList")){ %>
								<input type='Button' class='btn btn-danger btn-xs'  
									value='  Cancel ' 
									onclick='document.getElementById("displayRDInFolderDiv<%=requirementId %>").style.display="none";'>
									
							<%} %>
						
						
						</span>
						
					</td>

				</tr>
			</table>
			<%}	
			ArrayList comments = RequirementUtil.getRequirementCommentsGMTDelta(requirementId, databaseType, gmtDelta);
			
			if (comments.size()==0){
				// do nothing
				%>
				<div id='commentListDiv<%=requirementId%>'>
				</div>
				<%
			}
			else {
			
			%>
				<div id='commentListDiv<%=requirementId%>'>
						<table class='table'>
							<tr class='info'>
								<td><span class='normalText'><b>Date</b></span> </td>
								<td><span class='normalText'><b>Comment By </b></span></td>
								<td> <span class='normalText'><b>Comment</b></span></td>
								
							</tr>
							<%
							
								    if (comments != null){
								    	Iterator i = comments.iterator();
								    	while ( i.hasNext() ) {
								    	Comment commentObject = (Comment) i.next();
								    	
								    	
							%>
										<tr >
											<td><span class='normalText'><%=commentObject.getCommentDate() %> (<%=timeZone %>) </span></td>
											<td><span class='normalText'><%=commentObject.getCommenterEmailId() %></span> </td>
											<td><span class='normalText'>
												<%
									 			if (
									 				(isUserAnAdmin == true)
									 					||
									 				(user.getEmailId().equals(commentObject.getCommenterEmailId()))
									 			){
									 			%>
									 				<a  onClick='deleteComment(<%=commentObject.getId() %>, <%=commentObject.getRequirementId() %>, "<%=source%>")'>
									 				<span class='glyphicon glyphicon-trash'></span></a>
									 				
									 			<%} %>
												
												
												<%=commentObject.getComment_note() %></span>
											</td>
											
										</tr>
							
							<%			 }
								 }%>
							
							
						</table>
						
				</div>
	
			<%} %>
	</div>
</div>
<%}%>