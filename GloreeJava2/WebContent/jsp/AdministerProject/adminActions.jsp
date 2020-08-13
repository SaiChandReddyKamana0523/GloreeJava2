<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String adminActionsIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((adminActionsIsLoggedIn == null) || (adminActionsIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is an admin of this project.
	boolean isAdmin = false;
	String powerUserSettings = project.getPowerUserSettings();
	SecurityProfile aASecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (
		(aASecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))
		||
		(aASecurityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
		){
		isAdmin = true;
	}


%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<!-- The action buttons get displayed, only if the user is an admin. -->
<div id = 'adminInfoDiv'  class='alert alert-danger'>
	
	<table   width="100%" align="center" >
	<!--  lets get the folder details displayed -->
				
		<tr>
			<td colspan='2' align='center' valign='bottom'>
				<span class='sectionHeadingText'>
					<img src="/GloreeJava2/images/admin16.png" border="0" >
					&nbsp;&nbsp;
					<h1>Project Configuration Tool</h1>
					&nbsp;&nbsp;
					<img src="/GloreeJava2/images/admin16.png" border="0" >
				</span>
			</td>
		</tr>
		<tr>
			<td colspan='2' align='center' valign='bottom'>
				<span class='sectionHeadingText'><b>Project </b> </span>
				<span class='normalText'>
				<%=project.getShortName()%> :<%=project.getProjectName() %>
				</span>
			</td>
		</tr>
		
		
		
		
		
	</table>
	
</div>
	
	
