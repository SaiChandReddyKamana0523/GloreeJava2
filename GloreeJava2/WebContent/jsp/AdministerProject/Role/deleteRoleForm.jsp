<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String deleteRoleFormIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((deleteRoleFormIsLoggedIn  == null) || (deleteRoleFormIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is an admin of this project.
	boolean dRFIsAdmin = false;
	SecurityProfile dRFSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dRFSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
		dRFIsAdmin = true;
	}
%>

<!--  A user needs to be admin before he can delete a Role -->
<% if (dRFIsAdmin){ %>
	<%@ page import="java.util.*" %>
	<%@ page import="javax.servlet.http.HttpSession"  %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<%
		int roleId = Integer.parseInt(request.getParameter("roleId"));
		ArrayList users = RoleUtil.getAllUsersInRole(roleId, databaseType);
		String userAlert = "";
	%>
	
	<div id='editUsersFormDiv' class='level1Box'>
	
	<form method="post" id="editUsersForm" action="">
	<input type='hidden' name='roleId' value='<%=roleId%>'>	
	<table class='paddedTable' width='100%'>
		<tr>
			<td colspan="2" align='left'  >
				<span class='subSectionHeadingText'>
				<b>Delete Role</b>
				</span>
			</td>
		</tr>	
			
		
		
		<% if (users.size() > 0){ %> 
		
			<tr>
			<td colspan="2" align='left'>
			<div id='deleteAllUsersInRoleMessage' class='actionPrompt'>
			Please delete all Users in this Role, 
			or move them to a different Role before attempting deletion.
			</div>
			</td> 
		</tr>
		<tr>
			<td colspan='2' align="left">
				<input type="button" name="Cancel" 
				value="Cancel" onClick='document.getElementById("contentCenterE").innerHTML ="" '>
			</td>
		</tr>	
		<%} 
		else {
		%>
		<tr>
			<td colspan='2' align="left">
			<span class='normalText'> 
			Yes, I want to delete this <img src="/GloreeJava2/images/role16.png" border="0"> role.
			</span>
			</td>
		</tr>
		
		<tr>
			<td colspan='2' align="left">
				<span class='normalText'>
					<input type="button" name="Delete Role" 
					value="Delete Role" onClick="deleteRole(this.form)">
							
					<input type="button" name="Cancel" 
					value="Cancel" onClick='document.getElementById("contentCenterE").innerHTML ="" '>
				</span>
			</td>
		</tr>
		
		<%} %> 	
	</table>
	
	</form>
	</div>
<%}%>