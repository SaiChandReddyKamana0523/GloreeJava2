<!-- GloreeJava2 -->

<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>


<%
	// authentication only
	String displayRoleIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayRoleIsLoggedIn == null) || (displayRoleIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is an admin of this project.
	boolean dRIsAdmin = false;
	SecurityProfile dRSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	String powerUserSettings = project.getPowerUserSettings();
	if (
			(dRSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())
			||
			(
			(dRSecurityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
				&&
				(powerUserSettings.contains("Manage Roles"))
				)
		)){
		dRIsAdmin = true;
	}
	

	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	// NOTE : we are still restricting the roleActions section of the page to admins only.
	boolean dRIsMember = false;
	if (dRSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		dRIsMember = true;
	}
%>

<!--  display this page only if the user is a member of this project. -->
<% if(dRIsMember) { %>
	
	<%
		int webFormId = Integer.parseInt(request.getParameter("webFormId"));
		WebForm webForm = new WebForm(webFormId);
		Folder targetFolder = new Folder(webForm.getFolderId());
		%>
	<form method="post" id="createRoleForm" action="">
	
	<div id = 'roleInfoDiv' class='level1Box'>
		<table>
			<tr>
				<td colspan="2" align="left" bgcolor="#99CCFF">
				<span class="subSectionHeadingText" >
					Web Form : <%=webForm.getName() %>
				</span>
			</td>
				
			</tr>
			
			<tr onmouseover="this.style.background='lightblue';" onmouseout="this.style.background='white';">
				<td  style='width:200px' align='left'  >
					<span class='normalText'>
						Target Folder
					</span>
				</td>
				<td  align='left'  >
						
					<%
					ArrayList folders = ProjectUtil.getFolderInAProjectLite(project.getProjectId());
					Iterator i = folders.iterator();
					%>
					<span class='normalText'>
						<select name='folderId' id='folderId'>
						<%
							while (i.hasNext()){
								String folderString = (String) i.next();
								int folderId = 0 ;
								String folderPath = "";
								try {
									String [] folderStringSplit = folderString.split(":##:");
									folderId = Integer.parseInt(folderStringSplit[0]);
									folderPath = folderStringSplit[1];
								}
								catch (Exception e){
									e.printStackTrace();
								}
								if (folderId == webForm.getFolderId()){
									%>
									<option SELECTED value='<%=folderId%>'>
										<%=folderPath%>
									</option>
									<%	
								}
								else {
									%>
									<option  value='<%=folderId%>'>
										<%=folderPath%>
									</option>
									<%
								}
							
							}
						%>
						</select>
						<br>
						<font color='red'>Target Folder determines the type of the object that will be created, and the folder where it will be stored</font>
						</span>
	
				</td>
			</tr>	

			<tr onmouseover="this.style.background='lightblue';" onmouseout="this.style.background='white';">
				<td  align='left'  >
					<span class='normalText'>
						Name
					</span>
				</td>
				<td  align='left'  >
					<span class='normalText'>
						<input type="text" name="name"  id="name"  size="100" maxlength="100" value="<%=webForm.getName() %>"> 
					</span>
				</td>
			</tr>	
			<tr onmouseover="this.style.background='lightblue';" onmouseout="this.style.background='white';">
				<td  align='left'  >
					<span class='normalText'>
						Description
					</span>
				</td>
				<td  align='left'  >
					<span class='normalText'>
						<textarea name="description" id="description"  rows="4" cols="100" ><%=webForm.getDescription() %></textarea>
					</span>
				</td>
			</tr>	
			<tr onmouseover="this.style.background='lightblue';" onmouseout="this.style.background='white';">
				<td  align='left'  >
					<span class='normalText'>
						Introductory Blurb
					</span>
				</td>
				<td  align='left'  >
					<textarea name="introduction" id="introduction"  rows="8" cols="100" ><%=webForm.getIntroduction() %> </textarea>
				</td>
			</tr>	
			<tr onmouseover="this.style.background='lightblue';" onmouseout="this.style.background='white';">
				<td  align='left'  >
					<span class='normalText'>
						Default Owner
					</span>
				</td>
				<td  align='left'  >
						<%
						ArrayList users = project.getMembers();
						Iterator u = users.iterator();
						%>
						
						<span class='normalText'>
						<select name="owner" id="owner" >
						<%
						while (u.hasNext()){
							User projectMember = (User) u.next();
							if (projectMember.getEmailId() == webForm.getDefaultOwner()) {
							%>
								<option SELECTED value='<%=projectMember.getEmailId()%>'><%=projectMember.getLastName() %>  <%=projectMember.getFirstName() %></option>
							<%
							}
							else {
								%>
								<option  value='<%=projectMember.getEmailId()%>'><%=projectMember.getLastName() %>  <%=projectMember.getFirstName() %></option>
								<%	
							}
						}
						%>
						</select>
						<font color='red'>All objects created using this form will be owned by this person</font>
											
						</span>
				</td>
			</tr>	
			<tr onmouseover="this.style.background='lightblue';" onmouseout="this.style.background='white';">
				<td  align='left'  >
					<span class='normalText'>
						Notify on Creation
					</span>
				</td>
				<td  align='left'  >
					<span class='normalText'>
						<input type="text" name="notifyOnCreation" id="notifyOnCreation" size="100" maxlength="100" value='<%=webForm.getNotifyOnCreation() %>'> 
						
					</span>
				</td>
			</tr>	
			<tr onmouseover="this.style.background='lightblue';" onmouseout="this.style.background='white';">
				<td  align='left'  >
					<span class='normalText'>
						Auto Submit on Approval
					</span>
				</td>
				<td  align='left'  >
					<span class='normalText'>
						<%if (webForm.getSubmitForApprovalOnCreation() == 0) {%>
						<select name="submitForApprovalOnCreation" id="submitForApprovalOnCreation">
							<option selected value="0" > Disable</option>
							<option value="1"> Enable</option>
						</select>
						<%}
						else {
						%>
						<select name="submitForApprovalOnCreation" id="submitForApprovalOnCreation">
							<option value="0"> Disable</option>
							<option selected value="1"> Enable</option>
						</select>
						
						<%} %>
						
					</span>
				</td>
			</tr>
			<tr onmouseover="this.style.background='lightblue';" onmouseout="this.style.background='white';">
				<td  align='left'  >
					<span class='normalText'>
						Enable Look up for Traceability
					</span>
				</td>
				<td  align='left'  >
					<span class='normalText'>
						<%if (webForm.getEnableLookup() == 0) {%>
						<select name="enableLookup" id="enableLookup">
							<option selected value="0" > Disable</option>
							<option value="1"> Enable</option>
						</select>
						<%}
						else {
						%>
						<select name="enableLookup" id="enableLookup">
							<option value="0"> Disable</option>
							<option selected value="1"> Enable</option>
						</select>
						
						<%} %>
						
					</span>
					
				</td>
			</tr>	
			<tr>
				<td></td>
				<td>
				<div class='alert alert-danger'>
						Please note that Enabling lookup, lets any non-authorized user look at (read) requirements in your project.
						For example, they can enter REL-1 in to TraceTo or Trace From box and the system will 
						return the name of the REL-1 object.
						<br><br>
						Using this method, any non-logged in user of this webform, can  enter different object ids 
						and read the name of the object. 
						
						<br><br>
						If this field is disabled, then the users of the webeform can Trace Up and Down by entering object ids (REL-1,FR-2),
						but the system will not show the NON-Logged in users, what REL-2, FR-2 are.
						
						
					
					</div>
				</td>
			</tr>
			<tr onmouseover="this.style.background='lightblue';" onmouseout="this.style.background='white';">
				<td  align='left'  >
					<span class='normalText'>
						Access URL to Web Form
					</span>
				</td>
				<td  align='left'  >
					<span class='normalText'>
						<a href='<%=webForm.getAccessURL(request) %>' target='_blank' > <%=webForm.getAccessURL(request) %> </a>
					</span>
				</td>
			</tr>	
			
			<tr>
				
				<td></td>
				
				<td  align='center'> 
				<span class='normal'>
				
				<input type='button' id='deleteWebFormButton' class="btn btn-sm btn-primary" value=' Update ' 
					onClick='
						updateWebForm(this.form, <%=webForm.getId()%>);
					'>
					&nbsp;&nbsp;
					<input type='button' id='deleteWebFormButton' class="btn btn-sm btn-danger" value='Delete this WebForm' 
					onClick='
						document.getElementById("deleteWebFormButton").disabled=true;
						document.getElementById("deleteWebFormDiv").style.display="block";
					'>
				</span>
				</td>
			</tr>	
			<tr>
				<td colspan='2' align='center'> 
				<div id='deleteWebFormDiv' style="display:none" class='alert alert-success'>
					<span class='normal'>
						<br>
						Are you sure you want to delete this Web Form
						<br><br>
						<input type='button'  id ='confirmDeleteButton' value='Yes, Delete This Web Form' 
							onClick='
								document.getElementById("confirmDeleteButton").disabled=true;
								deleteWebForm(<%=webForm.getId() %>);
							'
						>
						<input type='button'  id ='cancelButton' value='Cancel' 
							onClick='
								document.getElementById("deleteWebFormButton").disabled=false;
								document.getElementById("deleteWebFormDiv").style.display="none";
							'
						>
					</span>
				</div>
				</td>
			</tr>	
				
		
		</table>
	</div>
	</form>

<%}%>