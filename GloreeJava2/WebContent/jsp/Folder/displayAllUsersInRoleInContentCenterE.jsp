<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String displayRealFolderIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayRealFolderIsLoggedIn  == null) || (displayRealFolderIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User user = securityProfile.getUser();
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)

	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
%>

 

	<% 
	
		int roleId = Integer.parseInt(request.getParameter("roleId"));
		Role role = new Role (roleId);
						
	%>
	
	<div id='folderCoreDiv' class='level1Box' STYLE="background-color:white">		
		<table  class='paddedTable' width='100%'>
			<tr>
				<td align='left' bgcolor='#99CCFF'>				
					<span class='subSectionHeadingText'>
					Users in Role : <%=role.getRoleName() %> 
					</span>
				</td>
			</tr>
			<tr><td >&nbsp;</td></tr>
			<tr>
				<td>
					<table class='paddedTable'>
						<tr>
							<td>
								<span class='sectionHeadingText'> Name </span>
							</td>
							<td>
								<span class='sectionHeadingText'> Email Id</span>
							</td>
					<%
						ArrayList users = RoleUtil.getAllUsersInRole(roleId, databaseType);
							    if (users != null){
							    	Iterator i = users.iterator();
							    	while ( i.hasNext() ) {
							    		User u = (User) i.next();
					%>
				 	<tr>
				 		<td>
				 		<span class='normalText'> 
				 			<%=u.getFirstName() %> &nbsp; <%=u.getLastName() %>
				 		</span>
				 		</td>
				 		<td>
				 		<span class='normalText'>
				 			<%=u.getEmailId() %>
				 		</span>
				 		</td>			
				 	</tr>
					 <%
					    	}
					    }
					%>
					</table>
				</td>
			</tr>
		</table>
	</div>
<%}%>