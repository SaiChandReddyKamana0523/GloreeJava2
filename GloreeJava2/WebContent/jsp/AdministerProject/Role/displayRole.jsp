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
		Role role = (Role) request.getAttribute("Role");
		// if the Role object exists, it means a call was made to 
		//RoleAction with a request to create a Role.	
		// the folder object now contains the data for the newly created folder.
	    if (role == null) {
	    	// This means that no new folders were created prior to this call.
	    	int roleId = Integer.parseInt(request.getParameter("roleId"));
	    	role = new Role(roleId);	
	    }
	%>
	
	<div id = 'roleInfoDiv' class='level1Box'>
	<table   align="center" width='100%' >
		<tr>
			<td colspan='2' align='left'  bgcolor='#99CCFF'>
				<span class='subSectionHeadingText' 
					title="Description : 
					<%=role.getRoleDescription()%>">
					Role <img src="/GloreeJava2/images/role16.png" border="0">  
					<%=role.getRoleName()%>
				</span>
				<div style='float:right'>
					<a href='/GloreeJava2/documentation/help/administerAProject.htm' target='_blank'>
					<img src="/GloreeJava2/images/page.png"   border="0">
					</a>	
					&nbsp;&nbsp;
				</div>
			</td>
		</tr>	
	
		<!--  RoleActions get displayed only if the user is an admin -->
		<%if (dRIsAdmin){%>
			<tr>
				<td colspan='2' align='left' valign='bottom'>
				<div id ='roleActions' class='level2Box'>	
					
					<a href='#' onClick='updateRoleForm("<%=role.getRoleId()%>")'>
						Edit Role
					</a>
					
					&nbsp;&nbsp;|&nbsp;&nbsp;
					
					<a href='#' id='editPrivilegesTab' onClick='editRolePrivilegesForm("<%=role.getRoleId()%>")'>
						Edit Privileges
					</a>
					
					&nbsp;&nbsp;|&nbsp;&nbsp;
										
					<a href='#' id='addUsersTab' onClick='addUserToRoleForm("<%=role.getRoleId()%>")'>
						Add Users
					</a>
					&nbsp;&nbsp;|&nbsp;&nbsp;
					
				    <a href='#' onClick='editUsersForm("<%=role.getRoleId()%>")'>
				    	Edit Users
				    </a>
				    
					 

				    
				    <!--  Administrator role can not be deleted -->
				    <% if (!(role.getRoleName().equals("Administrator"))) { 
				    	// non admin role
				    %>
					
						&nbsp;&nbsp;|&nbsp;&nbsp;
						    
					    <a href='#'  onClick='deleteRoleForm("<%=role.getRoleId()%>")'>
					    	Delete <img src="/GloreeJava2/images/role16.png" border="0"> Role
					    </a>
					    
					<%} %>				     
		        </div>
				</td>
			</tr>
		<%}
		else {
		%>
			<tr>
				<td colspan='2' align='center' valign='bottom'>
				<div id ='roleActions' class='level2Box'>	
					<span class='headingText'>					
					<font color='gray'>
						Edit Role Privileges
						&nbsp;&nbsp;|&nbsp;&nbsp;
						Add <img src="/GloreeJava2/images/user16.png" border="0"> Users
						&nbsp;&nbsp;|&nbsp;&nbsp;
				    	Edit <img src="/GloreeJava2/images/user16.png" border="0">  Users
				    	&nbsp;&nbsp;|&nbsp;&nbsp;
					    Delete <img src="/GloreeJava2/images/role16.png" border="0"> Role
					 </font>
					</span>     
		        </div>
				</td>
			</tr>
		
		
		<%} %>
		<tr>
			<td colspan='2' >
				<div id ='roleDisplayDiv'></div>
			</td>
		</tr>
	</table>
	</div>
<%}%>