<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String moveRequirementFormIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((moveRequirementFormIsLoggedIn == null) || (moveRequirementFormIsLoggedIn.equals(""))){
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
%>

	<%
		// NOTE : this page can be called when some one tries to edit a requirement.
		
		int requirementId = Integer.parseInt(request.getParameter("requirementId"));
		Requirement requirement = new Requirement(requirementId, databaseType);
	
		String source = request.getParameter("source");
		if (source == null){
			source  = "";
		}
		String messageDisplayDiv = "";
		if (source.equals("requirementList")){
			messageDisplayDiv = "displayRDInFolderDiv" + requirementId;
		}
		else {
			messageDisplayDiv = "moveRequirementPromptDiv";
		}

		
		///////////////////////////////SECURITY CODE ////////////////////////////
		// if the requirement worked on, doesn't belong to the project the user is 
		// currently logged into, then a user logged into project x is trying to 
		// hack into a req in project y by useing requirementId parameter.
		if (requirement.getProjectId() != project.getProjectId()) {
			return;
		}
		///////////////////////////////SECURITY CODE ////////////////////////////

		int folderId = Integer.parseInt(request.getParameter("folderId"));

	
		
		//ArrayList eligibleFolders = RequirementUtil.getEligibleFolderToMoveTo(requirementId);
		ArrayList eligibleFolders = project.getMyFolders();
		Iterator i = eligibleFolders.iterator();
	%>
	
	<div id='moveRequirementPromptDiv' class='alert alert-success'>
		
		<form method='post' action='' id="moveRequirementForm">
		
			<input type='hidden' name='requirementId' value='<%=requirementId%>'>
			<input type='hidden' name='currentFolderId' value='<%=folderId%>'>
			Target Folder &nbsp;&nbsp;&nbsp;
			<select name='moveFolder'>
			<%
				while (i.hasNext()){
					String moveFolderDisabled = "";
					String moveFolderDisabledReason = "";
					Folder folder = (Folder) i.next();
					if (requirement.getFolderId() == folder.getFolderId()){
						// skip this folder, as the requirement is already in this folder
						moveFolderDisabled = "DISABLED";
						moveFolderDisabledReason  = " (Current Folder)";
					}
				
					// lets see if this user has permissions to move requirements to this folder.
					if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" 
							+ folder.getFolderId()))){
						// not permitted to move stuff to / create stuff in this folder
						moveFolderDisabled = "DISABLED";
						moveFolderDisabledReason  = " (Not Permitted)";
					}
					%>					
					<option value='<%=folder.getFolderId()%>' <%=moveFolderDisabled %>>
						<%=folder.getFolderPath()%> <%=moveFolderDisabledReason  %>
					</option>
					<%
					}
					%>
			</select>
			<br><br>
				<div class='alert alert-danger'><b>Please note that MOVING an object from one type to a different type, DELETES the source object 
				and creates a new copy in another object type. 
				<br><br>
				If the target object type has the same attributes as the source, then you get an exact replica.
				
				<br><br> If you have any doubts on how this works, please try 
				the COPY function and then the DELETE function. You get the same effect , in a more controlled way</b></div>
			<br><br>
			<span class='normalText'>
				<input type='button' class='btn btn-primary btn-sm'  name='moveButton<%=requirementId%>' id = 'moveButton<%=requirementId%>' value=' Move  ' 
				onClick='
				moveRequirement(this.form,"<%=source%>")'>
				
				&nbsp;&nbsp;
			
				<input type='button' class='btn btn-danger btn-sm'  name=' Cancel ' value='  Cancel  ' 
				onClick='
				document.getElementById("<%=messageDisplayDiv%>").style.display = "none";
				if (document.getElementById("requirementAction<%=requirementId%>") != null){
							document.getElementById("requirementAction<%=requirementId%>").selectedIndex = "0";
						}	
				'>
				
			</span>
			<br>
		</form>
	</div>
<%}%>