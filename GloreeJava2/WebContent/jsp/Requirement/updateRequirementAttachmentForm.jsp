<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn == null) || (isLoggedIn.equals(""))){
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
	boolean isMember = false;
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	%>

<%if(isMember){ 
	int attachmentId = Integer.parseInt(request.getParameter("attachmentId"));
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);
	RequirementAttachment attachment =  new RequirementAttachment(attachmentId, databaseType);
	
	///////////////////////////////SECURITY CODE ////////////////////////////
	// if the requirement worked on, doesn't belong to the project the user is 
	// currently logged into, then a user logged into project x is trying to 
	// hack into a req in project y by useing requirementId parameter.
	if (requirement.getProjectId() != project.getProjectId()) {
		return;
	}
	///////////////////////////////SECURITY CODE ////////////////////////////

	int folderId = Integer.parseInt(request.getParameter("folderId"));
%>
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<div id='updateRequirementAttachmentsDiv' class='alert alert-success'> 
		
		<form method="post"  ENCTYPE='multipart/form-data' id="addRequirementAttachment" 
		action="/GloreeJava2/servlet/AddRequirementAttachmentAction">
			<input type="hidden" name="actionToDo"  value="updateRequirementAttachment" >
			<input type="hidden" name="folderId" value="<%=folderId%>" >
			<input type="hidden" name="attachmentId" value="<%=attachmentId%>" >
			
	 		<input type="hidden" name="requirementId" value="<%=requirementId%>" >
	 	
	 		<input type="hidden" id="addExistingFilesHidden" name="addExistingFilesHidden" value="" >
	 	
		<br><br>
	 	<span class="sectionHeadingText"><b>Edit Attachment</b></span>
		
		
		<table class='paddedTable' width='100%' >
				
			<tr>
				<td  style="width:200px"> 
					<span class='headingText'>
					Attachment <font color='red'>(Size < 2MB)</font>
					<sup><span style="color: #ff0000;">*</span></sup> 
					</span>
				</td>
			 
				<td>
					<span class='normalText'>
					<INPUT TYPE='file'  NAME='attachment'>
					</span>
				</td>
				
			</tr>	
			<tr>
				<td></td>
				<td >
					<span class='normalText'>
					<input type="button"  class='btn btn-primary btn-sm'  name="Update Attachment"  id="uploadFileButton" value="  Update Attachment " 
					onClick='updateRequirementAttachment(this.form);'>
					
					&nbsp;&nbsp;
					
					<input type='button'  class='btn btn-danger btn-sm'  name='Cancel' value=' Close ' 
					onClick='document.getElementById("updateAttachment<%=attachment.getRequirementAttachmentId()%>Div").style.display="none"'>
					
					
					</span>
				</td>
			</tr> 			
		</table>
		</form>
	</div>
	
<%}%>