<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String moveRequirementCIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((moveRequirementCIsLoggedIn == null) || (moveRequirementCIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
		User user = securityProfile.getUser();
%>

	<%
	
		int requirementId = Integer.parseInt(request.getParameter("requirementId"));
		Requirement requirement = new Requirement(requirementId, databaseType);
	
		///////////////////////////////SECURITY CODE ////////////////////////////
		// if the requirement worked on, doesn't belong to the project the user is 
		// currently logged into, then a user logged into project x is trying to 
		// hack into a req in project y by useing requirementId parameter.
		if (requirement.getProjectId() != project.getProjectId()) {
			return;
		}
		///////////////////////////////SECURITY CODE ////////////////////////////
		
		
		// instead of move, lets use copy / purge
		int targetFolderId = Integer.parseInt(request.getParameter("folderId"));
		Folder targetFolder = new Folder(targetFolderId);
		int targetProjectId = project.getProjectId();
		

		String status = "";
		// if source requirementtype is same as target requirement type, then use move. else use copy & delete.
		if (requirement.getRequirementTypeId() == targetFolder.getRequirementTypeId()){
			// move within the same req type, so use Move
			
			RequirementUtil.moveRequirementToAnotherFolder(requirement, targetFolderId, user.getEmailId(), databaseType);
			status = "Congratulations. This  Requirement is now in <b> " + targetFolder.getFolderPath() + " </b> folder.";
		}
		else {
			// move to a new req type. so use Copy to new / purge old 
			RequirementType targetRequirementType = new RequirementType(targetFolder.getRequirementTypeId());
			// TODO : when you clone requirements, copy the comments and version and attachments
			Requirement targetRequirement  = RequirementUtil.cloneRequirement(
					requirement,targetRequirementType, targetProjectId, targetFolderId, 
					true, true, user, securityProfile,  databaseType );

			
			// lets also add a log at the new requirement level that it was creaed by moving another requirement
			String log = "This requirement " + targetRequirement.getRequirementFullTag() + 
			" was created by moving " + requirement.getRequirementFullTag() + " From folder <b> " +
			requirement.getFolderPath()	+ "</b>";
						
						
						
			ProjectUtil.purgeRequirementExceptAttachments(requirement.getRequirementId(),  databaseType);
			ProjectUtil.createProjectLog(requirement.getProjectId(), requirement.getRequirementFullTag(), " Move ",
					"Moving Requirement : " + requirement.getRequirementFullTag() + " : " + requirement.getRequirementName() + " to  " + targetRequirement.getRequirementFullTag(), user.getEmailId(),  databaseType);
			
			
			RequirementUtil.createRequirementLog(targetRequirement.getRequirementId(), log,user.getEmailId(),  databaseType);

			
			status = "Congratulations. This  Requirement is now in <b> " + targetFolder.getFolderPath() + " </b> folder. New id is " + targetRequirement.getRequirementFullTag();
		}
	
	%>
<div id='rMoveDiv' class='level1Box'>	
	<table width='100%'>
		<tr>
			<td align="left" >
				<div id='moveRequirementConfirmation' class='alert alert-success'>

					<div id="closeCommentsDiv" style="float: right;">
						<a href="#" onclick="document.getElementById('displayRequirementInFolderDiv<%=requirementId%>').style.display=&quot;none&quot;"> Close </a>
					</div>

					<br>
					<span class='normalText'>
						<%=status %>
					</span>
					<br>
				</div>
			</td>
		</tr>
	</table>
	
</div>
<%}%>