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
	int scheduledReportId = Integer.parseInt(request.getParameter("scheduledReportId"));
	ScheduledReport scheduledReport = new ScheduledReport(scheduledReportId);
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
		User user = securityProfile.getUser();
		if (user.getEmailId().equals(scheduledReport.getOwner()) ){
			int reportId = Integer.parseInt(request.getParameter("reportId"));
			String attachmentType = request.getParameter("attachmentType");
			if (attachmentType == null){
				attachmentType = "";
			}
			
			String toEmailAddresses = request.getParameter("toEmailAddresses");
			if (toEmailAddresses == null){
				toEmailAddresses = "";
			}
			
			String ccEmailAddresses = request.getParameter("ccEmailAddresses");
			if (ccEmailAddresses == null){
				ccEmailAddresses = "";
			}
			
			String subjectValue = request.getParameter("subjectValue");
			if (subjectValue == null){
				subjectValue = "";
			}
			
			String messageValue = request.getParameter("messageValue");
			if (messageValue == null){
				messageValue = "";
			}
			
			String runTaskOn = request.getParameter("runTaskOn");
			if (runTaskOn == null){
				runTaskOn = "";
			}
			
			scheduledReport = new ScheduledReport(scheduledReportId, project.getProjectId() , reportId, attachmentType, toEmailAddresses,
				ccEmailAddresses, subjectValue, messageValue,runTaskOn,user.getEmailId());
		%>	
			<table  class='paddedTable'>
				<tr>
					<td>
						<div class='alert alert-success'>				
						<span class='subSectionHeadingText'>
						The report has been successfully updated.
						</span>
						</div>
					</td>
				</tr>
			</table>
	<%	}
		else {
		%>
			<table  class='paddedTable'>
				<tr>
					<td>
						<div class='alert alert-success'>				
						<span class='subSectionHeadingText'>
						Only the owner of this scheduled report '<%=scheduledReport.getOwner() %>' can modify this.
						</span>
						</div>
					</td>
				</tr>
			</table>		
	<%}
}%>