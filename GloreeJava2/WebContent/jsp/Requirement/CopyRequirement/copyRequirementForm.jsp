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
	
		///////////////////////////////SECURITY CODE ////////////////////////////
		// if the requirement worked on, doesn't belong to the project the user is 
		// currently logged into, then a user logged into project x is trying to 
		// hack into a req in project y by useing requirementId parameter.
		if (requirement.getProjectId() != project.getProjectId()) {
			return;
		}
		///////////////////////////////SECURITY CODE ////////////////////////////

		ArrayList folders = project.getMyFolders();
		Iterator i = folders.iterator();

	%>
	
	<div id='copyRequirementPromptDiv' class='alert alert-success'>

		<div id='copyRequirementConfirmationMessageDiv' style='display:none'></div>
		
		<div id='copyAcrossProjectDiv'>
				<table class='table'>
					<tr>
						<td width='300'>
						<span class='normalText'>
						Target Project 
						</span>
						</td>
						<td >
						<span class='normalText'>
							<select id='targetProject'
							onChange='copyRequirementsToTargetProjectForm(<%=requirementId%>);'>
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
			<form method='post' action='' id="copyRequirementForm">
				<table class='table' >
					<tr>
						<td width='300'>
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
									"</b></span> ";
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
					<td width='300' valign='bottom'>
						<img border="0" src="/GloreeJava2/images/help.png" title="Create a trace from the newly created object to <%=requirement.getRequirementFullTag() %>">
						<span class='normalText'>
						Create a Trace from New Object to <%=requirement.getRequirementFullTag() %> 
						&nbsp;&nbsp;&nbsp;
						</span>
					</td>
					<td>
							<span class='normalText'>
							<select name='createTraceToSource' id='createTraceToSource' 
							onchange='
								var createTraceToSourceObject = document.getElementById("createTraceToSource");
								
								var	createTraceToSource= createTraceToSourceObject[createTraceToSourceObject.selectedIndex].value;
								
								var createTraceFromSourceObject = document.getElementById("createTraceFromSource");
								
								var	createTraceFromSource= createTraceFromSourceObject[createTraceFromSourceObject.selectedIndex].value;
								
								if (createTraceToSource == "yes"){
									createTraceFromSourceObject.selectedIndex = 1;	
								}
							'
							>
								<option value='yes' SELECTED>Yes</option>
								<option value='no'>No</opiton>
							</select>	
							
								&nbsp;&nbsp;&nbsp;<b><%=requirement.getRequirementFullTag() %> </b> &nbsp;&nbsp;	<img src="/GloreeJava2/images/cTrace2.jpg" border="0"> &nbsp;&nbsp;<b> New Object</b>
							</span>
							
						
					</td> 
				</tr>
				<tr>
					<td width='300' valign='bottom'>
						<img border="0" src="/GloreeJava2/images/help.png" title="Create a trace from <%=requirement.getRequirementFullTag() %> to the newly created object">
						<span class='normalText'>
						Create a Trace from <%=requirement.getRequirementFullTag() %> to the New Object
						&nbsp;&nbsp;&nbsp;
						</span>
					</td>
					<td>
							<span class='normalText'>
							<select name='createTraceFromSource' id='createTraceFromSource'
							onchange='
								var createTraceToSourceObject = document.getElementById("createTraceToSource");
								
								var	createTraceToSource= createTraceToSourceObject[createTraceToSourceObject.selectedIndex].value;
								
								var createTraceFromSourceObject = document.getElementById("createTraceFromSource");
								
								var	createTraceFromSource= createTraceFromSourceObject[createTraceFromSourceObject.selectedIndex].value;
								
								if (createTraceFromSource == "yes"){
									createTraceToSourceObject.selectedIndex = 1;	
								}
							'>
								<option value='yes' >Yes</option>
								<option value='no' SELECTED >No</opiton>
							</select>	
							
								&nbsp;&nbsp;&nbsp;<b>New Object</b> &nbsp;&nbsp;	<img src="/GloreeJava2/images/cTrace2.jpg" border="0"> &nbsp;&nbsp;<b><%=requirement.getRequirementFullTag() %></b>
							</span>
							
						
					</td> 
				</tr>
								
				<tr>
					<td width='300'>
						<img border="0" src="/GloreeJava2/images/help.png" title="Copy the attribute values from the source object to the newly created object">
						<span class='normalText'>
						Copy Common Attributes 
						&nbsp;&nbsp;&nbsp;
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
					<td width='300'>
						<img border="0" src="/GloreeJava2/images/help.png" title="Make the newly created object trace to the same things as the source object">
						<span class='normalText'>
						Copy Traceability 
						&nbsp;&nbsp;
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
				<input type='button' class='btn btn-primary btn-sm'   name='CopyButton' id='CopyButton' value='  Copy  ' onClick='
					copyRequirement(<%=requirementId%>, <%=project.getProjectId() %>)'>
				
				&nbsp;&nbsp;
				<input type='button' class='btn btn-danger btn-sm'   name='closeButton' id='closeButton' value='  Close  ' 
				onClick='document.getElementById("copyRequirementPromptDiv").style.display = "none";'>
				
				</td></tr>
			</table>
			</form>
		</div>
	</div>
<%}%>