<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String createFolderFormIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((createFolderFormIsLoggedIn == null) || (createFolderFormIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isAdmin = false;
	if (securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
		isAdmin = true;
	}
	
	if (isAdmin){
%>
	
	
	
	<%
		ArrayList requirementTypes = project.getMyRequirementTypes();
		
		String folderIdString = request.getParameter("folderId");
		int folderId = Integer.parseInt(folderIdString);
		Folder folder = new Folder(folderId);	
	
		if (folder.getFolderLevel() == 4 ){
	%>
		<tr>
			<td>
				<img src="/GloreeJava2/images/folder.png" border="0">&nbsp;<%=folder.getFolderName() %> is a level 4 folder. You can not create sub folders below this level.
			</td>
		</tr>
	<%}
	else {
	%>
	<div id='createSubFolderDiv' class='level1Box'>
		<table class='paddedTable' width='100%'>
			<tr>
				<td colspan='2' align='left' bgcolor='#99CCFF'>				
					<span class='subSectionHeadingText'>
					Create A Sub Folder
					</span>
				</td>
			</tr>		

			<tr> 
				<td>
					<span class='normalText'> Folder Name
					</span>
					<sup><span style="color: #ff0000;">*</span></sup> </td>
				<td> 
					
					<textarea name="folderName" id="folderName" rows="4" cols="80" ></textarea>
				</td>
			</tr>
			<tr> 
				<td> 
					<span class='normalText'>Folder Description</span>
					<sup><span style="color: #ff0000;">*</span></sup> </td>
				<td><textarea name="folderDescription" id="folderDescription" rows="10" cols="80" ></textarea></td>
			</tr>	
			<tr>
				<td></td>
				<td  >
					<div class='alert alert-info'>
						To create multiple folders, please enter multiple folder names separated by a <b>'#'</b> in Name field.
						<br>
						You can also use <b>'/'</b> to create a folder hierarchy
						<br> 
						<br>
						Some examples
						<ul>
							<li>
								"Admin#Reporting#Executive" will create 3 folders named Admin, Reporting and Executive respectively.
							</li>
							<li>
								"Admin/Reporting#Executive" will create Folder Admin, Folder Admin/Reporting and Folder Executive respectively
							</li>
							<li>
								"Admin/Reporting#Admin/Executive" will create Folder Admin, Folder Admin/Reporting and Folder Admin/Executive respectively
							</li>
						</ul> 
						<br>
						You can also enter # separated folder description values 
					</div>
					<span class='normalText'>
					<input type="button" name="Create Folder" value="Create A Folder" onClick="createFolder(<%=project.getProjectId()%>, <%=request.getParameter("folderId") %>, 'single')">
					&nbsp;&nbsp;&nbsp;
					<input type="button" name="Create Multiple Folders" value="Create Multiple Folders" onClick="createFolder(<%=project.getProjectId()%>, <%=request.getParameter("folderId") %>, 'multiple')">
					&nbsp;&nbsp;&nbsp;
					<input type='button' name='Cancel' value='Cancel' 
					onClick='document.getElementById("contentCenterB").innerHTML= "";'>
					</span>
				</td>
				
			</tr> 	
		 
		
		</table>
		
		
	</div>
	<% } %>
<%}
	else { 
%>

	<div class='alert alert-danger'>
	
		Only Administrators are allowed to change the structure of a project. Please work with this Project's administrator.
	
	</div>



<%}%>