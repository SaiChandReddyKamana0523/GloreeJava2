
<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>


<%
	// authentication only
	String addUsersToRoleIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((addUsersToRoleIsLoggedIn == null) || (addUsersToRoleIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is an admin of this project.
	boolean aUTRFIsAdmin = false;
	SecurityProfile aUTRFSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (
			(aUTRFSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))
			||
			(aUTRFSecurityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
		){
		aUTRFIsAdmin = true;
	}
%>


<% if(aUTRFIsAdmin){ %>
	<%@ page import="java.util.*" %>
	<%@ page import="javax.servlet.http.HttpSession"  %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	
	<%
		
		Role role = null;
		
		try {
			int roleId = 0;
			String roleIdString = (String) request.getParameter("roleId");
			roleId = Integer.parseInt(roleIdString);
			role = new Role(roleId);	
		}
		catch (Exception e){
			e.printStackTrace();
		}

		try {
			if (role == null){
				// that means this call from 'UpdateRoleNameandDescription' process. In that case Role is sent as an object.
				role = (Role) request.getAttribute("Role");
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		String roleName = role.getRoleName();
		String roleDescription = role.getRoleDescription();
		
		if (roleName.contains("'")){
			roleName = roleName.replace("'","");
		}
		

		if (roleDescription.contains("'")){
			roleDescription = roleDescription.replace("'","");
		}
		
	%>
	
	<div id='addUserToRoleDiv' class='level1Box'>
	
	<form method="post" id="editRoleForm" action="">
		<input type='hidden' name='roleId' value='<%=role.getRoleId() %>'>	
	<table class='paddedTable' width='100%'>
		<tr>
			<td colspan="2" align='left'  >
				<span class='subSectionHeadingText'>
				<b>Edit Role</b>
				</span>
			</td> 
		</tr>
	
		
		<tr> 
			<td>
				<span class='headingText'>Name</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td> 
				<input type="text" name="roleName" value="<%=roleName%>" size="30" maxlength="100"> 
			</td>
		</tr>
		<tr> 
			<td>
				<span class='headingText'>Description</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td>
				<textarea name="roleDescription" rows="5" cols="50" ><%=roleDescription %></textarea>
				</td>
		</tr>	
		<tr>
			<td colspan=2 align="left">
				<span class='normalText'>
					<input type="button" name="Update Role" 
					value="Update Role" onClick="updateRole(this.form)">
					
				</span>
			</td>
		</tr> 	

	</table>
	
	</form>
	</div>
<%}%>