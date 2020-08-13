<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="com.gloree.beans.*" %>
<%
	// authentication only
	String requirementDeleteCofirmIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((requirementDeleteCofirmIsLoggedIn  == null) || (requirementDeleteCofirmIsLoggedIn .equals(""))){
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
<div id='rDeletedtDiv' class='level1Box'>
	<table    >
		<tr>
			<td align="left">
				<div id='purgeRequirementConfirmation' class='alert alert-success'>
				<span class='normalText'>
				Congratulations. Your Requirement has been deleted from the system. <br>
				Please note that this Requirement can be Restored by locating 
				it in the 'Deleted Requirements' folder and choosing the Restore option.
				</span>
				<br>
				</div>
			</td>
		</tr>
	</table>
</div>
<%}%>