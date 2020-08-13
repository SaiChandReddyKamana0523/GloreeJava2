<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="com.gloree.beans.*" %>
<%
	// authentication only
	String requirementPurgedConfirmIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((requirementPurgedConfirmIsLoggedIn  == null) || (requirementPurgedConfirmIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
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

	<div class='level1Box'>
	<table width='100%'>
		<tr>
			<td align="left">
				<div id='purgeRequirementConfirmation' class='alert alert-success'>
				<span class='headingText'>
				Congratulations. Your Requirement has been permanently deleted from the system.
				</span>
				<br>
				</div>
			</td>
		</tr>
	</table>
	</div>
<%}%>