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
		User user = securityProfile.getUser();
		int reportId = Integer.parseInt(request.getParameter("reportId"));
		String attachmentType = request.getParameter("attachmentType");
		String toEmailAddresses = request.getParameter("toEmailAddresses");
		String ccEmailAddresses = request.getParameter("ccEmailAddresses");
		String subjectValue = request.getParameter("subjectValue");
		String messageValue = request.getParameter("messageValue");
		String runTaskOn = request.getParameter("runTaskOn");
		
		ScheduledReport scheduledReport = new ScheduledReport(project.getProjectId() , reportId, attachmentType, toEmailAddresses,
			ccEmailAddresses, subjectValue, messageValue,runTaskOn,user.getEmailId());
	%>	
		<table  class='paddedTable'>
			<tr>
				<td>
					<div class='alert alert-success'>				
					<span class='subSectionHeadingText'>
					The report has been successfully scheduled.
					</span>
					</div>
				</td>
			</tr>
		</table>
<%}%>