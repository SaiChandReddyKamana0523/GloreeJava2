<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<%
	// authentication only
	String IsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((IsLoggedIn  == null) || (IsLoggedIn.equals(""))){
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
		String createTraceTo = request.getParameter("createTraceTo");
		String createTraceFrom = request.getParameter("createTraceFrom");
		int requirementId = Integer.parseInt(request.getParameter("requirementId"));

		Requirement requirement = new Requirement(requirementId, databaseType);
		///////////////////////////////SECURITY CODE ////////////////////////////
		// if the requirement worked on, doesn't belong to the project the user is 
		// currently logged into, then a user logged into project x is trying to 
		// hack into a req in project y by useing requirementId parameter.
		if (requirement.getProjectId() != project.getProjectId()) {
			return;
		}
		///////////////////////////////SECURITY CODE ////////////////////////////

		// Call RequirementUtil.createTraces
		// Get the error / status message
		String status = RequirementUtil.createTraces(project, requirementId, 
			createTraceTo, createTraceFrom, project.getProjectId(), securityProfile,  databaseType);
		if (status == null){
			status = "";
		}
		if (!(status.equals(""))){
		%>
			<div class='alert alert-success'>
				<div style="float:left">
					<a href='#' onClick='
						document.getElementById("displayCreateTracesMessageDiv<%=requirementId%>").style.display="none";'>
						Close
					</a>
				</div>
				<span class='normalText'><%=status%></span>
			</div>
		<%
		}
	
	}	
%>