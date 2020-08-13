<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>


<%
	// authentication only
	String displayListReportIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayListReportIsLoggedIn == null) || (displayListReportIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dRIsMember = false;
	Project dUDProject= (Project) session.getAttribute("project");
	SecurityProfile dRSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dRSecurityProfile.getRoles().contains("MemberInProject" + dUDProject.getProjectId())){
		dRIsMember = true;
	}
	
	User duDUser = dRSecurityProfile.getUser();
%>

<%if (dRIsMember){ 
	
	// SURPRISE!!!!!!!!   Sorry we moved all the code to the displayUserMetricsDataTable.jsp



%>


	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	

	 	
	
<%}%>

	