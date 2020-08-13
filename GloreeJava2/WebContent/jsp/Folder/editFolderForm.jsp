<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String editFolderFormIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((editFolderFormIsLoggedIn  == null) || (editFolderFormIsLoggedIn.equals(""))){
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
		String folderIdString = request.getParameter("folderId");
		int folderId = Integer.parseInt(folderIdString);
		Folder folder = new Folder(folderId);
		
	    String updatedMessage = "";
	    String updated = (String) request.getAttribute("updated");

	    if ((updated != null) && (updated.equals("yes"))){
	    	updatedMessage = "Your changes have been updated in the system.";
	    }
	    
	%>
		
	<div id='editFolderFormDiv' class='level1Box'>	
			<input type='hidden' name='folderId' id='folderId' value='<%=request.getParameter("folderId")%>' > 
		<table class='paddedTable' width='100%'>
			<tr>
				<td colspan='2' align='left' bgcolor='#99CCFF'>				
					<span class='subSectionHeadingText'>
					Edit Folder
					</span>
				</td>
			</tr>		

			<tr>
				<td colspan='2' align='left' >				
					<span class='normalText'>
						<a href='#' onClick="deleteFolderForm(<%=folderId%>)"> Delete This Folder</a>
		        		
						&nbsp;|&nbsp;	
							
						<a href='#' onClick="createFolderForm(<%=folderId%>)"> Create SubFolder</a>					
					
					</span>
				</td>
			</tr>		
			<%
			if (!(updatedMessage.equals(""))) {
			%>  		 
				<tr> <td colspan='2'> 
				<div class='alert alert-success'><span class='normalText'> 
				<%=updatedMessage %> 
				</span></div> </td></tr>
			<%	}
			%>
			<tr> 
				<td width='100' valign='top'>
					<span class='normalText'>
					 Folder Name
					 </span>
					 <sup><span style="color: #ff0000;">*</span></sup> 
				</td>
				<td> 
					<span class='normalText'>
					<%if (folder.getFolderLevel()==1){ %>
						<input type="text" name="folderName"  id="folderName" size="50" maxlength="100" DISABLED value="<%=folder.getFolderName()%>">
						<br>
						To change Root Folder Name, please change Requirement Type Name in Admin screen.
						</br> 
					<%
					}
					else {
					%>
						<input type="text" name="newFolderName" id="newFolderName" size="50" maxlength="100" value="<%=folder.getFolderName()%>">
					<%} %>
					</span>
				</td>
			</tr>
			<tr> 
				<td>
					<span class='normalText'>
					 Folder Description
					 </span>
						<sup><span style="color: #ff0000;">*</span></sup> 
				</td>
				<td>
					<span class='normalText'>
					<textarea name="newFolderDescription" id="newFolderDescription"  rows="10" cols="80" ><%=folder.getFolderDescription()%></textarea>
					</span>
				</td>
			</tr>	
			<tr>
				<td colspan=2 align="center">
				<span class='normalText'>
				<input type="button" name="Update Folder" value="Update Folder" onClick="editFolder()">
				<input type='button' name='Cancel' value='Cancel' 
				onClick='document.getElementById("contentCenterB").innerHTML= "";'>
				</span>
				</td>
			</tr> 	
		 
		
		</table>
		
	
	</div>
<%}
	else {
%>

	<div class='alert alert-danger'>
	
		Only Administrators are allowed to change the structure of a project. Please work with this Project's administrator.
	
	</div>



<%}%>