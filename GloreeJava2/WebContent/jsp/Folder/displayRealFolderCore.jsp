<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*"%>
<%@ page import="com.gloree.beans.*"%>
<%@ page import="com.gloree.utils.*"%>

<%
	// authentication only
	String displayRealFolderIsLoggedIn = (String) session.getAttribute("isLoggedIn");
	if ((displayRealFolderIsLoggedIn == null) || (displayRealFolderIsLoggedIn.equals(""))) {
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
<jsp:forward page="/jsp/WebSite/startPage.jsp" />
<%
	}
	Project project = (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User user = securityProfile.getUser();

	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)

	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())) {
		isMember = true;
	}

	if (isMember) {
%>



<%
	Folder folder = (Folder) request.getAttribute("folder");
		// if the folder object exists, it means a call was made to FolderAction with a request to create a folder.
		// the folder object now contains the data for the newly created folder.
		if (folder == null) {
			// This means that no new folders were created prior to this call.
			String folderIdString = request.getParameter("folderId");
			int folderId = Integer.parseInt(folderIdString);
			folder = new Folder(folderId);
		}

		// lets get the req type this folder supports.
		RequirementType requirementType = new RequirementType(folder.getRequirementTypeId());
%>

<div id='folderCoreDiv' class='level1Box'
	STYLE="background-color: white">
	<table class='paddedTable' width='100%'>
		<tr>
			<td colspan='2'>&nbsp;</td>
		</tr>
		<%
			if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" + folder.getFolderId()))) {
		%>
		<tr>
			<td align='left' colspan='2'>
				<div class='alert alert-success'>
					<span class='subSectionHeadingText'> You do not have READ
						permissions on this folder. Please work with your Project
						Administrator to get access to this folder. </span>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan='2'>&nbsp;</td>
		</tr>
		<%
			}
		%>

		<tr>
			<td><span class='sectionHeadingText'> Your Roles </span></td>
			<td><span class='normalText'> <%
 	ArrayList yourRoles = user.getUserRolesInProject(project.getProjectId());
 		Iterator yR = yourRoles.iterator();
 		while (yR.hasNext()) {
 			Role role = (Role) yR.next();
 %> <a href='#'
					onclick='displayAllUsersInRole(<%=role.getRoleId()%>)'> '<%=role.getRoleName()%>'
				</a> &nbsp;&nbsp;&nbsp; <%
 	}
 %>
			</span></td>
		</tr>

		<tr>
			<td colspan='2'>&nbsp;</td>
		</tr>
		<tr>
			<td colspan='2'><span class='sectionHeadingText'> Folder
					Details </span></td>
		</tr>
		<tr>
			<td width='150'><span class='normalText'> Folder Name </span></td>
			<td><span class='normalText'> <%=folder.getFolderName()%></span>
			</td>
		</tr>
		<tr>
			<td><span class='normalText'> Folder Path </span></td>
			<td><span class='normalText'> <%=folder.getFolderPath()%></span>
			</td>
		</tr>
		<tr>
			<td><span class='normalText'> Folder Description </span></td>
			<td><span class='normalText'> <%=folder.getFolderDescription()%></span>
			</td>
		</tr>
		<tr>
			<td colspan='2'>&nbsp;</td>
		</tr>
		<tr>
			<td colspan='2'><span class='sectionHeadingText'>
					Requirement Type </span></td>
		</tr>
		<tr>
			<td colspan='2' align='left'><span class='normalText'>
					This folder can store Requirements of type '<%=requirementType.getRequirementTypeName()%>'.
					<br> All these Requirements will have tags that start with '<%=requirementType.getRequirementTypeShortName()%>'
			</span></td>
		</tr>
		<tr>
			<td colspan='2'>&nbsp;</td>
		</tr>
		<tr>
			<td><span class='sectionHeadingText'> Permissions </span></td>
			<td><span class='sectionHeadingText'> Roles with this
					Permission </span></td>

		</tr>
		<tr>
			<td><span class='normalText'> Create </span></td>
			<td><span class='normalText'> <%
 	ArrayList createRequirementRoles = folder.getCreateRequirementRoles(project.getProjectId());
 		Iterator cR = createRequirementRoles.iterator();
 		while (cR.hasNext()) {
 			Role role = (Role) cR.next();
 %> <a href='#'
					onclick='displayAllUsersInRole(<%=role.getRoleId()%>)'> <%=role.getRoleName()%>
				</a> &nbsp;&nbsp;&nbsp; <%
 	}
 %>
			</span></td>
		</tr>

		<tr>
			<td><span class='normalText'> Read </span></td>
			<td><span class='normalText'> <%
 	ArrayList readRequirementRoles = folder.getReadRequirementRoles(project.getProjectId());
 		Iterator rR = readRequirementRoles.iterator();
 		while (rR.hasNext()) {
 			Role role = (Role) rR.next();
 %> <a href='#'
					onclick='displayAllUsersInRole(<%=role.getRoleId()%>)'> <%=role.getRoleName()%>
				</a> &nbsp;&nbsp;&nbsp; <%
 	}
 %>
			</span></td>
		</tr>

		<tr>
			<td><span class='normalText'> Update </span></td>
			<td><span class='normalText'> <%
 	ArrayList updateRequirementRoles = folder.getUpdateRequirementRoles(project.getProjectId());
 		Iterator uR = updateRequirementRoles.iterator();
 		while (uR.hasNext()) {
 			Role role = (Role) uR.next();
 %> <a href='#'
					onclick='displayAllUsersInRole(<%=role.getRoleId()%>)'> <%=role.getRoleName()%>
				</a> &nbsp;&nbsp;&nbsp; <%
 	}
 %>
			</span></td>
		</tr>

		<tr>
			<td><span class='normalText'> Delete </span></td>
			<td><span class='normalText'> <%
 	ArrayList deleteRequirementRoles = folder.getDeleteRequirementRoles(project.getProjectId());
 		Iterator dR = deleteRequirementRoles.iterator();
 		while (dR.hasNext()) {
 			Role role = (Role) dR.next();
 %> <a href='#'
					onclick='displayAllUsersInRole(<%=role.getRoleId()%>)'> <%=role.getRoleName()%>
				</a> &nbsp;&nbsp;&nbsp; <%
 	}
 %>
			</span></td>
		</tr>

		<tr>
			<td><span class='normalText'> Trace To / From </span></td>
			<td><span class='normalText'> <%
 	ArrayList tracetoRequirementRoles = folder.getTraceToRequirementRoles(project.getProjectId());
 		Iterator tTR = tracetoRequirementRoles.iterator();
 		while (tTR.hasNext()) {
 			Role role = (Role) tTR.next();
 %> <a href='#'
					onclick='displayAllUsersInRole(<%=role.getRoleId()%>)'> <%=role.getRoleName()%>
				</a> &nbsp;&nbsp;&nbsp; <%
 	}
 %>
			</span></td>
		</tr>

		<tr>
			<td><span class='normalText'> Approve </span></td>
			<td><span class='normalText'> <%
 	ArrayList approveRequirementRoles = folder.getApproveRequirementRoles(project.getProjectId());
 		Iterator aR = approveRequirementRoles.iterator();
 		while (aR.hasNext()) {
 			Role role = (Role) aR.next();

 			String approvalType = role.getApprovalType();
 			String approvalString = "";
 			if (approvalType.equals("ApprovalByAll")) {
 				approvalString = "All Members MUST Respond";
 			}
 			if (approvalType.equals("ApprovalByAny")) {
 				approvalString = "Any ONE Response is sufficient";
 			}
 %> <a href='#'
					onclick='displayAllUsersInRole(<%=role.getRoleId()%>)'> <%=role.getRoleName()%>
						(<%=approvalString%>)
				</a> &nbsp;&nbsp;&nbsp; <%
 	}
 %>
			</span></td>
		</tr>

		<tr>
			<td colspan='2'>
				<div id='userRoleDiv' class='alert alert-info' style='display: none'>
				</div>

			</td>
		</tr>
	</table>
</div>
<%
	}
%>