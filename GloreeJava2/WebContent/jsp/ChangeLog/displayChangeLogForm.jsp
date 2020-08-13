<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<%
	// authentication only
	String IsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((IsLoggedIn  == null) || (IsLoggedIn.equals(""))){
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
		ArrayList folders = project.getMyFolders();
		ArrayList projectActors = ProjectUtil.getProjectActors(project.getProjectId(), databaseType);

		
	if (!(securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))){
	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	// TODO : There is a potential for user without read access to get to folder info
	// so locking this down 
	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	
		%>
			<div class='alert alert-danger'>
			Only Administrators are allowed to access the project change log
			</div>
		<%
		return;
	}
	%>
	<div id = 'showFiltersDiv' class='level1Box'>

		<table   width="100%" align="center" class='paddedTable'>
			<tr>
				<td colspan='2' align='left' bgcolor='#99CCFF'>				
					<span class='subSectionHeadingText'>
					Project Change Log
					</span>
				</td>
			</tr>

			<tr>
				<td align='left' >				
					<span class='sectionHeadingText'>
					Changed by
					</span>
				</td>
			
				<td align='left' >				
					<span class='normalText'>
						<select name='actorEmailId' id='actorEmailId'>
							<option value='all'>All Members of Project</option>
						<%
							Iterator pa = projectActors.iterator();
							while (pa.hasNext()){
								String actorEmailId = (String) pa.next();
								
						%>
							<option value='<%=actorEmailId%>'>
								<%=actorEmailId%>
							</option>
						<%	
							}
						%>
						</select>
					</span>
				</td>
			</tr>

			<tr>
				<td align='left' >				
					<span class='sectionHeadingText'>
					Changed Type
					</span>
				</td>
			
				<td align='left' >				
					<span class='normalText'>
						<select name='changeType' id='changeType'>
							<option value='all'>All Change</option>
							<option value='Comments'>Comments</option>
							<option value='Traceability'>Traceability</option>
							<option value='Approval'>Approval Work Flow</option>
							<option value='all'></option>
							<option value='CreatedRequirement'>Created Requirement</option>
							<option value='UpdatedName'>Updated Name</option>
							<option value='UpdatedDescription'>Updated Description</option>
							<option value='UpdatedAttributes'>Updated Attributes</option>
							<option value='File'>Attached / Removed Files</option>
							<option value='Completion'>Changed Completion Level</option>
							<option value='all'></option>
							<option value='Owner'>Change Owner</option>
							<option value='Deleted'>Deleted Requirement</option>
							<option value='Restored'>Restored Requirement</option>
							<option value='Moved'>Moved Requirement</option>
							
							
						</select>
					</span>
				</td>
			</tr>

			<tr>
				<td align='left' >				
					<span class='sectionHeadingText'>
					Changes in the past
					</span>
				</td>
			
				<td align='left' >				
					<span class='normalText'>
						<input type='text' value='10' size='4' style='width:40px' name='changedSince' id='changedSince'>
					</span>
					<span class='sectionHeadingText'>
					days
					</span>
				</td>
			</tr>

			<tr>
				<td align='left' >				
					<span class='sectionHeadingText'>
					Changes in Folder
					</span>
				</td>
			
				<td align='left' >				
					<span class='normalText'>
						<select name='changeFolderId' id='changeFolderId'>
							<option value='-1'>All Folders in Project</option>
						<%
							Iterator i = folders.iterator();
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
				<td colspan='2' align='left'>
					<input type='button' style='width:250px' 
						name='Go' id='Go' value='  Get Project Change Log  ' 
						onClick='
							document.getElementById("showFiltersDiv").style.display="none";
							document.getElementById("hideFiltersDiv").style.display="block";
							displayChangeLog();
						'>
						
					<hr>	
				</td>
			</tr>
			<tr>
				<td colspan='2' valign='center'>
				</td>
			</tr>						
		</table>
	</div>
	<div id="hideFiltersDiv" style='display:none;'>
		<input type='button' style='width:150px; height:25px' value='Show Filters'
		onclick='
			document.getElementById("showFiltersDiv").style.display="block";
			document.getElementById("hideFiltersDiv").style.display="none";
		'>
	</div>
	<div id='changeLogDiv' style='display:none' class='level2Box'></div>
<%}%>