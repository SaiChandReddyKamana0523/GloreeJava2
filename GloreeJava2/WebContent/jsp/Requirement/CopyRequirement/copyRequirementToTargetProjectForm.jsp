<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

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

		int targetProjectId = Integer.parseInt(request.getParameter("targetProjectId"));
		
	%>
		<div >
			<form method='post' action='' id="copyRequirementForm">
			<div id='copyRequirementConfirmationMessageDiv' class='alert alert-success' style='display:none'></div>
				<table>
					<tr>
						<td width='150'>
							<span class='normalText'>
							Target Folder
							</span>
						</td>
						<td>
							<span class='normalText'>
							<select name='copyFolder' id='copyFolder'
								onChange='
								
								var copyFolderObject = document.getElementById("copyFolder");
								if (copyFolderObject.selectedIndex > 0){
									var copyFolderValue = copyFolderObject[copyFolderObject.selectedIndex].innerHTML;
									var copyFolderValueArray = copyFolderValue.split("/");
									// the first object in the array has the root folder name, which is the same
									// as the req type name. so lets getit.
									var copyRequirementTypeName = copyFolderValueArray[0]; 
									
									document.getElementById("copyRequirementMessageDiv").style.display="block";
									document.getElementById("copyRequirementMessageDiv").innerHTML = "<span class=normalText>" +
									" <br>Please note that this will create a <b> " + 
									copyRequirementTypeName + 
									"</b> </span> ";
								}
								'>
								<option value=''>Select A Target Folder</option>
							<%
								if (targetProjectId == project.getProjectId()){
									ArrayList folders  = project.getMyFolders();
									Iterator i = folders.iterator();
				
									while (i.hasNext()){
										Folder folder = (Folder) i.next();
										%>
										<option value='<%=folder.getFolderId()%>'>
											<%=folder.getFolderPath()%>
										</option>
										<%	
									}
								}
								else{
									Project targetProject = new Project(targetProjectId, databaseType);
									ArrayList folders  = targetProject.getMyFolders();
									Iterator i = folders.iterator();
				
									while (i.hasNext()){
										Folder folder = (Folder) i.next();
										%>
										<option value='<%=folder.getFolderId()%>'>
											<%=folder.getFolderPath()%>
										</option>
										<%	
									}
								}
							%>
							</select>
							</span>
						</td> 
					</tr>
					<tr>
						<td colspan='2'>
							<div id='copyRequirementMessageDiv' style='display:none'></div>
						</td>
					</tr>
				<tr>
					<td width='150'>
						<span class='normalText'>
						Copy Common Attributes &nbsp;&nbsp;&nbsp;
						</span>
					</td>
					<td>
						<span class='normalText'>
						<select name='copyCommonAttributes' id='copyCommonAttributes'>
							<option value='yes' SELECTED>Yes</option>
							<option value='no'>No</opiton>
						</select>		
						</span>
					</td> 
				</tr>
				
				<tr>
					<td width='150'>
						<span class='normalText'>
						Copy Traceability 
						</span>
					</td>
					<td>
						<span class='normalText'>
						<%if (targetProjectId == project.getProjectId()){ %>
							<select name='copyTraceability' id='copyTraceability'>
								<option value='yes' >Yes</option>
								<option value='no' SELECTED>No</opiton>
							</select>		
						<%}
						else {%>
							<select DISABLED name='copyTraceability' id='copyTraceability'>
								<option value='yes' >Yes</option>
								<option value='no' SELECTED>No</opiton>
							</select>						
						<%} %>
						</span>
					</td> 
				</tr>
				<tr>
					<td colspan='2' align='left'>
					<input type='button' name='CopyButton' id='CopyButton' value='Copy' onClick='
						copyRequirement(<%=requirementId%>,<%=targetProjectId%>)'>
					<input type='button' name='Cancel' value='Cancel' 
						onClick='document.getElementById("copyRequirementPromptDiv").style.display = "none";'>
				</td></tr>
			</table>
			</form>
		</div>
<%}%>