<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String createRoleFormIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((createRoleFormIsLoggedIn == null) || (createRoleFormIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }

	Project project= (Project) session.getAttribute("project");
	// lets see if this user is an admin of this project.
	boolean cRFIsAdmin = false;
	SecurityProfile cRFSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	String powerUserSettings = project.getPowerUserSettings();
	if (
			(cRFSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())
			||
			(
			(cRFSecurityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
				&&
				(powerUserSettings.contains("Manage Roles"))
			)
		)){
		cRFIsAdmin = true;
	}
	
%>


<!--  A user needs to be an admin to createRole -->
<%if (cRFIsAdmin){ %>
	<%@ page import="java.util.*" %>
	<%@ page import="javax.servlet.http.HttpSession"  %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<%
		String status = (String) request.getAttribute("status");
	%>
	
	<div id='createRoleDiv' class='level1Box'>
	
	<form method="post" id="createRoleForm" action="">
		
	<table class='paddedTable' width='100%'>
		<tr>
			<td align='left' colspan='2' bgcolor='#99CCFF'>				
				<span class='subSectionHeadingText'>
				Create A New Web Form
				</span>
				
			</td>
		</tr>	
		
		
		<tr> 
			<td>
			</td>
			<td> 
				<span class='normalText'>
					<br>
					Web Forms let you easily share a web page, where Non-TraceCloud users can create objects in TraceCloud. For example, you can have a Object Type called
					<b> New Ideas </b>, create a web form for this, and stick it on your organization's site. 
					<br><br>
					With this feature, users who can fill a form 
					<br><br>
					You can then configure the system to notify you when new ideas are submitted, and then submit it through the approval workflow system.
					<br><br>
				</span> 
			</td>
		</tr>


		<tr onmouseover="this.style.background='lightblue';" onmouseout="this.style.background='white';"> 
			<td style='width:200px' >
				<span class='headingText'>Target Folder</span>
			</td>
			<td >
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
					%>
						<option value='<%=folderId%>'>
							<%=folderPath%>
						</option>
					<%	
						}
					%>
					</select>
						<br>
						<font color='red'>Target Folder determines the type of the object that will be created, and the folder where it will be stored</font>
					</span>

			</td>
		</tr>
		
		<tr onmouseover="this.style.background='lightblue';" onmouseout="this.style.background='white';"> 
			<td>
				<span class='headingText'>Name</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td> 
				<input type="text" name="name"  id="name"  size="100" maxlength="100"> 
			</td>
		</tr>
		<tr onmouseover="this.style.background='lightblue';" onmouseout="this.style.background='white';"> 
			<td>
				<span class='headingText'>Description</span>
			</td>
			<td>
				<textarea name="description" id="description"  rows="4" cols="100" ></textarea>
				</td>
		</tr>	
		
		<tr onmouseover="this.style.background='lightblue';" onmouseout="this.style.background='white';"> 
			<td>
				<span class='headingText'>Introduction</span>
			</td>
			<td>
				<textarea name="introduction" id="introduction"  rows="8" cols="100" >
				
				Put any custom html content here. This will be displayed at the top of your webform 
				
				You can also use Bootstrap CSS to make the messages stand out
				
				</textarea>
				</td>
		</tr>	
		
		
		

		<tr  onmouseover="this.style.background='lightblue';" onmouseout="this.style.background='white';"> 
			<td>
				<span class='headingText'>Default Owner</span>
			</td>
			<td> 
				<%
				ArrayList users = project.getMembers();
				Iterator u = users.iterator();
				%>
				
				<span class='normalText'>
				<select name="owner" id="owner" >
				<%
				while (u.hasNext()){
					User projectMember = (User) u.next();
					%>
						<option value='<%=projectMember.getEmailId()%>'><%=projectMember.getLastName() %>  <%=projectMember.getFirstName() %></option>
					<%
				}
				%>
				</select>
				<font color='red'>All objects created using this form will be owned by this person</font>
											
				</span>
				</td>
		</tr>


		<tr onmouseover="this.style.background='lightblue';" onmouseout="this.style.background='white';"> 
			<td>
				<span class='headingText'>Notify On Creation </span>
			</td>
			<td> 
				<input type="text" name="notifyOnCreation" id="notifyOnCreation" size="100" maxlength="100"> 
			</td>
		</tr>
		
		
		<tr onmouseover="this.style.background='lightblue';" onmouseout="this.style.background='white';"> 
			<td>
				<span class='headingText'>Auto Submit for Approval On Creation </span>
			</td>
			<td>
				<span class="normalText">
					<select name="submitForApprovalOnCreation" id="submitForApprovalOnCreation">
					<option value="0" selected=""> Disable</option>
					<option value="1"> Enable</option>
					</select>
				</span>
			</td>
				
		</tr>
		
		<tr onmouseover="this.style.background='lightblue';" onmouseout="this.style.background='white';"> 
			<td>
				<span class='headingText'>Enable Lookup for Traceability </span>
			</td>
			<td>
				<span class="normalText">
					<select name="enableLookup" id="enableLookup">
					<option value="0" selected=""> Disable</option>
					<option value="1"> Enable</option>
					</select>
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
		
		
		
		<tr>
			<td colspan=2 align="left">
				<span class='normalText'>
					<input type="button" name="Create Web Form" 
					value="Create Web Form" onClick="createWebForm(this.form)">
					
				</span>
			</td>
		</tr> 	
	</table>
	
	</form>
	</div>
<%}%>