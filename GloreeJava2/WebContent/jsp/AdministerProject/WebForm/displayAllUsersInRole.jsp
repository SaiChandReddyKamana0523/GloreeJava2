<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String displayAllUsersInRoleIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayAllUsersInRoleIsLoggedIn == null) || (displayAllUsersInRoleIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	String installationType = this.getServletContext().getInitParameter("installationType");
	

	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean cRTFIsMember = false;
	SecurityProfile cRTFSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (cRTFSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		cRTFIsMember = true;
	}
%>

<!--  do this only if the user is an admin or a Member (remember every admin is a member-->
<% if(cRTFIsMember){ %>


	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<%
		// Called when trying to display all users in a role.
		String roleIdString = request.getParameter("roleId");

	   	int roleId = Integer.parseInt(roleIdString);
	   	Role role = new Role(roleId);
	%> 
	<div id='displayAllUsersInRole' class='invisibleLevel1Box'> 
		<table class='paddedTable' >
			<tr>
				<td colspan="2"> 
				<span class='sectionHeadingText'>
				<b>Users in <img src="/GloreeJava2/images/role16.png" border="0">
				'<%=role.getRoleName() %>'
				</b>
				</span>
				</td>
			</tr>
			<%
				ArrayList users = RoleUtil.getAllUsersInRole(roleId, databaseType);
					    if (users != null){
					    	Iterator i = users.iterator();
					    	while ( i.hasNext() ) {
					    		User u = (User) i.next();
			%>
		 	<tr>
		 		<td colspan=2 >
				<% 
				if 
					(
					(installationType.toLowerCase().equals("onsite")  )
					&&
					(u.getUserType().equals("expired"))
					){
					// If this is an onsite install and the user has an expired license
					// he / she doesn't even show up on the list.
					continue;
				}
					    
				else if (
						u.getUserType().equals("expired")
						&&
						(project.getBillingOrganizationId() == 0 )
					){
					// expired user on a project withour a generic project license
				%>
					
					<span class='normalText' title="This user's license has expired. He / She will not be able to access this project. Please restore his / her license or convert this project to a PROJECT license.">
					<a href="#" onClick='editUsersForm("<%=role.getRoleId()%>")'>
					<img src="/GloreeJava2/images/flag_red.png" border="0" >&nbsp;<%=u.getFirstName() %>  <%=u.getLastName() %>
					</a> 
					</span>
				
				<%}
				else {%>
		 		<span class='normalText' title='<%=u.getEmailId() %>'">
		 		<a href="#" onClick='editUsersForm("<%=role.getRoleId()%>")'>
		 		<img src="/GloreeJava2/images/user16.png" border="0">
		 		&nbsp;<%=u.getFirstName() %>  <%=u.getLastName() %>
		 		</a> 
		 		</span>
				<%}%>
		 		</td>			
		 	</tr>
			 <%
			    	}
			    }
			%>
		</table>
	</div>
<%} %>
	