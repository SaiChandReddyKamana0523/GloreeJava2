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
		ArrayList scheduledReports = ProjectUtil.getMyScheduledReports(project.getProjectId(),user.getEmailId());
		
	%>	
		<table   width="100%" align="center" class='paddedTable'>
			<tr>
				<td colspan='2'>
					<table class='paddedTable' width='100%'>
						<tr>
						<td align='left' bgcolor='#99CCFF'>				
							<span class='subSectionHeadingText'>
							Your scheduled recurring reports
							</span>
						</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td colspan='2'>
					<%
					if (scheduledReports.size() == 0 ){
					%>
					<div class='alert alert-success'>		
						<table class='paddedTable' width='100%'>
								<tr>
									<td colspan='3' align='left'>
										<span class='subSectionHeadingText'>
										You do not have any scheduled reports
										</span>
									</td>
								</tr>
								<tr>
									<td colspan='3' align='left'>
									<a href='#' onClick='displayScheduleANewReportForm();'>Schedule a new report</a>
									</td>
								</tr>
						
						</table>		
						
					</div>
					<%} 
					else {
						%>
						<table class='paddedTable' width='100%'>
							<tr>
								<td colspan='3' align='left'>
								<a href='#' onClick='displayScheduleANewReportForm();'>Schedule a new report</a>
								</td>
							</tr>
							<tr>
								<td >				
									<span class='sectionHeadingText'>
									Report Name
									</span>
								</td>							
								<td >				
									<span class='sectionHeadingText'>
									Report Location
									</span>
								</td>

								<td >				
									<span class='sectionHeadingText'>
									Send Report On
									</span>
								</td>
							</tr>
							
					
						<% 
						Iterator sR = scheduledReports.iterator();
						int j = 0;
				    	String cellStyle = "normalTableCell";
						
						while (sR.hasNext()){
							ScheduledReport scheduledReport = (ScheduledReport) sR.next();
							Report report = new Report(scheduledReport.getReportId());
							Folder folder = new Folder(report.getFolderId());

							j++;	
							if ((j%2) == 0){
				    			cellStyle = "normalTableCell";
				    		}
				    		else {
				    			cellStyle = "altTableCell";	
				    		}
				    		
							%>
							<tr>
								<td class='<%=cellStyle%>'>				
									<a href='#' onClick='editASceduledReportForm(<%=scheduledReport.getScheduledReportId()%>)'>
									<%=report.getReportName() %>
									</a>
								</td>							
								<td  class='<%=cellStyle%>'>				
									<span class='normalText'>
									<%=folder.getFolderPath() %>
									</span>
								</td>
								

								<td class='<%=cellStyle%>'>				
									<span class='normalText'>
									<%=scheduledReport.getRunTaskOn() %>
									</span>
								</td>
							</tr>
							
							<%
						}
						
					%>
					<%} %>
					</table>
				</td>
			</tr>
		</table>
<%}%>