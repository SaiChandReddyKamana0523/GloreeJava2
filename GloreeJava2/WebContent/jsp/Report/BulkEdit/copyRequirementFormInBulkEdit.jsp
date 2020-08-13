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

	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){

		int folderId = Integer.parseInt(request.getParameter("folderId"));
		ArrayList folders = project.getMyFolders();
		Iterator i = folders.iterator();
	%>
	
	<div id='copyRequirementPromptDiv' class='alert alert-success'>

		<div id='copyRequirementConfirmationMessageDiv' style='display:none'></div>
		
		<div id='copyAcrossProjectDiv'>
				<table>
					<tr>
						<td width='180'>
						<span class='normalText'>
						Target Project 
						</span>
						</td>
						<td >
						<span class='normalText'>
							<select id='targetProject'
							onBlur='copyRequirementsToTargetProjectFormInBulkEdit(<%=folderId%>);'>
							<%
							ArrayList targetProjects = securityProfile.getProjectObjects();
							Iterator t = targetProjects.iterator();
							while (t.hasNext()){
								Project targetProject = (Project) t.next();
								if (targetProject.getProjectId() == project.getProjectId()){
									%>
									<option SELECTED value='<%=targetProject.getProjectId()%>'>
									<%=targetProject.getShortName()%> : <%=targetProject.getProjectName() %>
									</option>
									<%
								}
								else {
									%>
									<option value='<%=targetProject.getProjectId()%>'>
									<%=targetProject.getShortName()%> : <%=targetProject.getProjectName() %>
									</option>
									<%
								}
							}
							%>
							</select>
						</span>
						</td>
						
					</tr>
				</table>
		</div>
	
		<div id='copyRequirementsToTargetProjectDiv'>
				<table>
					<tr>
						<td width='180'>
						<span class='normalText'>
						Select a folder. &nbsp;&nbsp;&nbsp;
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

								}
								'>
								<option value=''>Select A Target Folder</option>
							<%
								while (i.hasNext()){
									Folder folder = (Folder) i.next();
									
							%>
								<option value='<%=folder.getFolderId()%>'>
									<%=folder.getFolderPath()%>
								</option>
							<%	
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
					<td width='180'>
						<img border="0" src="/GloreeJava2/images/help.png" title="If you want to make multiple copies of an object, increase this number">
						<span class='normalText'>
						Number of Copies
						&nbsp;&nbsp;&nbsp;
						</span>
					</td>
					<td>
						<span class='normalText'>
						<input type='text' name='numOfCopies' id='numOfCopies' value='1' size='2'>
						</span>
					</td> 
				</tr>

				
				<tr>
					<td width='180'>
						<img border="0" src="/GloreeJava2/images/help.png" title="Create a trace from the newly created object to the source object">
						<span class='normalText'>
						Create a Trace to Source  
						&nbsp;&nbsp;&nbsp;
						</span>
					</td>
					<td>
						<span class='normalText'>
						<select name='createTraceToSource' id='createTraceToSource'>
								<option value='yes' SELECTED>Yes</option>
								<option value='no'>No</opiton>
							</select>	
						</span>
					</td> 
				</tr>
				
								
				
				<tr>
					<td width='180'>
						<img border="0" src="/GloreeJava2/images/help.png" title="Copy the attribute values from the source object to the newly created object">
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
					<td width='180'>
						<img border="0" src="/GloreeJava2/images/help.png" title="Make the newly created object trace to the same things as the source object">
						<span class='normalText'>
						Copy Traceability 
						</span>
					</td>
					<td>
						<span class='normalText'>
						<select name='copyTraceability' id='copyTraceability'>
							<option value='yes' >Yes</option>
							<option value='no' SELECTED>No</opiton>
						</select>		
						</span>
					</td> 
				</tr>
				<tr>
				<td colspan='2' align='left'>
				<span class='normalText'>
				<input type='button' class='btn btn-sm btn-success'  name='CopyButton' id='CopyButton' value='Copy' onClick='
					bulkEditActionForm("copyRequirements", "<%=folderId%>");
					'>
				<input type='button'  class='btn btn-sm btn-danger'  name='Cancel' value='Cancel' 
					onClick='document.getElementById("copyRequirementInBulkEditPrompt").style.display = "none";'>
				</span>
				</td></tr>
			</table>
		</div>
	</div>
<%}%>