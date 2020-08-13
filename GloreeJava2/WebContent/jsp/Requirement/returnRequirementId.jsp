
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>
<%
	// authentication only
	String returnRequirementIdIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((returnRequirementIdIsLoggedIn == null) || (returnRequirementIdIsLoggedIn.equals(""))){
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
		// this jsp is called only from RequirementAction servlet and that too just after a requirement was created. Hence a requirement object exists in request.
		// This JSP's job is to return a JSON object with the requiremntId
		
		Requirement requirement = (Requirement) request.getAttribute("requirement");
	%>{"requirementId" : "<%=requirement.getRequirementId()%>"}<%}%>