<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }

	String databaseType = this.getServletContext().getInitParameter("databaseType");
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

	<%
		// NOTE : this page can be called when some one tries to edit a requirement.
		
		int requirementId = Integer.parseInt(request.getParameter("requirementId"));
		int folderId = Integer.parseInt(request.getParameter("folderId"));
		
		Requirement requirement = new Requirement(requirementId, databaseType);

		///////////////////////////////SECURITY CODE ////////////////////////////
		// if the requirement worked on, doesn't belong to the project the user is 
		// currently logged into, then a user logged into project x is trying to 
		// hack into a req in project y by useing requirementId parameter.
		if (requirement.getProjectId() != project.getProjectId()) {
			return;
		}
		///////////////////////////////SECURITY CODE ////////////////////////////

	%>
	
	<div id='submitForApprovalDiv' class='alert alert-success'>
		<div style='float:right'>
		<a href='#' onClick='document.getElementById("submitForApprovalDiv").style.display = "none";'>Close </a>
		</div>
		
		<span class='normalText'>
			(Optional) Approval Due Date :  
			<input type='text' name='approvalDueDate' id='approvalDueDate' value=''> (mm/dd/yyyy)
		</span>
		&nbsp;&nbsp;
		<a href='#' onClick='submitRequirementForApproval(<%=requirementId%>, <%=folderId%>);'>Submit For Acceptance </a>	
		
	</div>
<%}%>