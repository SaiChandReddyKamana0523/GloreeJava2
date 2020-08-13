<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="com.gloree.beans.*" %>

<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>
<%@ page import="java.util.*" %>




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
	int requirementId = Integer.parseInt((request.getParameter("requirementId")));
	Requirement requirement = new Requirement(requirementId, databaseType);
	Project project = new Project(requirement.getProjectId(), databaseType);
	
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
    
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean readPermissions = true;
	if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
			+ requirement.getFolderId()))){
		readPermissions = false;
	}
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + requirement.getProjectId())){
		isMember = true;
	}
	// you need to be a member of this project and have read permissions before you can see this.
	if (isMember && readPermissions){
%>

<html>
<body style='font-size:80%' >
				<%=requirement.getRequirementDescription() %>
</body>
</html>
<%}%>