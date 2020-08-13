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
		int folderId = Integer.parseInt(request.getParameter("folderId"));
		Folder folder = new Folder(folderId);
		ArrayList reports = folder.getMyReports(folder.getProjectId());
	
	%>	
	
	<div id = 'reportInFolderDiv' class='level2Box'>

		<table   width="100%" align="center" class='paddedTable'>
		
			<tr>
				<td align='left' style='width:145px;'>				
					<span class='subSectionHeadingText'>
					Report 
					</span>
				</td>

				<td align='left' >				
					<span class='normalText'>
						<select name='reportId' id='reportId'
							onChange='displaySchedulerReportInfo();'>
							<option value='-1'>Select A Report</	option>
							
						<%
							// first lets display the system defined reports (canned)
							if (reports != null){
								%>
								<option value='-1'></option>
								<option value='-1'>System Defined Reports</option>
								<option value='-1'>-------------------------------------------------------------------</option>
								<%
								Iterator r = reports.iterator();
								while (r.hasNext()){
									Report report = (Report) r.next();
									if (report.getReportDescription().startsWith("Canned")){
										String reportType = "List";
										if (report.getReportType().equals("traceTree")){
											reportType = "Trace Tree";
										}
							    			
									%>
										<option value='<%=report.getReportId()%>'>
											(<%=reportType %>) <%=report.getReportName() %>
										</option>
									<%
									}
								}
								
								%>
								<option value='-1'></option>
								<option value='-1'><%=user.getFirstName()%> <%=user.getLastName()%>'s Private Reports</option>
								<option value='-1'>-------------------------------------------------------------------</option>
								<%
								r = reports.iterator();
								while (r.hasNext()){
									Report report = (Report) r.next();
									if (
						    				!(report.getReportDescription().startsWith("Canned"))
						    				&&
						    				(report.getReportVisibility().equals("private"))
						    				&&
						    				(report.getCreatedByEmailId().equals(user.getEmailId()))
						    			) 
						    		
						    		
						    		{	
										String reportType = "List";
										if (report.getReportType().equals("traceTree")){
											reportType = "Trace Tree";
										}
									%>
										<option value='<%=report.getReportId()%>'>
											(<%=reportType %>) <%=report.getReportName() %>
										</option>
									<%
									}
								}
								%>
								<option value='-1'></option>
								<option value='-1'>Public Reports</option>
								<option value='-1'>-------------------------------------------------------------------</option>
								<%
								r = reports.iterator();
								while (r.hasNext()){
									Report report = (Report) r.next();
									if (
						    				!(report.getReportDescription().startsWith("Canned"))
						    				&&
						    				(report.getReportVisibility().equals("public"))
						    			) 
						    		
						    		
						    		{	
										String reportType = "List";
										if (report.getReportType().equals("traceTree")){
											reportType = "Trace Tree";
										}
									%>
										<option value='<%=report.getReportId()%>'>
											(<%=reportType %>) <%=report.getReportName() %>
										</option>
									<%
									}
								}
							}
							%>
						</select>
					</span>
				</td>
			</tr>

			<tr>
				<td colspan='2'>
				<div id='reportInfoDiv'></div>
				</td>
			</tr>
		</table>
	</div>
<%}%>