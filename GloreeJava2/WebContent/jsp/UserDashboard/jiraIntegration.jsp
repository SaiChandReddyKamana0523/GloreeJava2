<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<html xml:lang="en" xmlns="http://www.w3.org/1999/xhtml" lang="en"><head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">

	
	<title>TraceCloud - SAAS Agile Scrum Requirements Management - Collaborate, Define, Manage and Deliver your Customer Requirements</title>
    <meta name="description" content="Collaboration tools to define, manage and deliver your customer requirements on time and within budget. Significantly improves customer satisfaction ">
	<meta name="keywords" content="free requirements management, saas requirements management tool, online requirements management, doors, requisitepro, customer requirements, shared requirements, tl9000, project management, project requirements, agile, agile requirements management.">


	
	
	<link rel="stylesheet" href="/GloreeJava2/css/greeny.css" type="text/css" >
	<link rel="stylesheet" href="/GloreeJava2/css/common.css" type="text/css">
	<link rel="stylesheet" href="/GloreeJava2/css/sales_global.css" type="text/css">
	<link rel="stylesheet" href="/GloreeJava2/css/sales_home.css" type="text/css" media="screen">
	
	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userAccount.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userDashboard.js?v=20200630"></script>
	<script src="/GloreeJava2/js/jira.js"></script>
	
	
			
	
</head>
<body >


<%

	String serverName = request.getServerName();
	
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");


	
	if (securityProfile == null){
	%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
	<%
	}
	String databaseType = this.getServletContext().getInitParameter("databaseType");

	
	User user = securityProfile.getUser();
	ArrayList projects = securityProfile.getProjectObjects();
	
	
	

	
%>

	
<div class="wrapper box_theme_login_wrapper">
		<jsp:include page="/jsp/WebSite/Common/TCToolbar.jsp" />	

		<div style="border-width: 2px; border-style: solid; border-color: white; padding:15px ">
		<table width='100%' >
			<tr>
				<td valign='top'>
				
				</td>
				<td>
					<div>
						<table width='100%'>
							<tr>
								<td>
									<table class='paddedTable' align='left'>
										<tr>
										
											<td id='projectDasnboardTD' align='center' valign='center'  width='150' height='50'
													class = 'nonFocusTab'
													style='cursor:pointer'
													onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/UserDashboard/myTasks.jsp";'
													>
													<span class='normalText' style='color:white'>
														My Tasks
													</span>
											</td>
											
											
											<td id='projectDasnboardTD' align='center' valign='center'  width='150' height='50'
													class = 'nonFocusTab'
													style='cursor:pointer'
													onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/UserDashboard/userProjects.jsp";'
													>
												<span class='normalText' style='color:white'>
													My Projects
												</span>
											</td>
											
											
											
											
											<td id='accountProfileTD' align='center' valign='center'  width='150' height='50'
													class = 'nonFocusTab'
													style='cursor:pointer'
													onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/UserDashboard/userProfile.jsp";'
													>
												<span class='normalText' style='color:white'> 
													Account Profile
												</span>
											</td>	
											
											
													


											<%
											// For onsite intallations, if hideLicenseManagementTab is yes, and the user is not an admin, then we
											// we should not show the license management tab.
											String siteAdministrator = this.getServletContext().getInitParameter("siteAdministrator");
											if (siteAdministrator == null) {
												siteAdministrator = "";
											}
											String hideLicenseManagementTab = "";
											hideLicenseManagementTab = this.getServletContext().getInitParameter("hideLicenseManagementTab");
											if (hideLicenseManagementTab == null)  {
												hideLicenseManagementTab = "";
											}
											if (
													(this.getServletContext().getInitParameter("installationType").equals("onSite")) 
													&&
													(hideLicenseManagementTab.toLowerCase().equals("yes"))
													&&
													(!(siteAdministrator.contains(user.getEmailId())))
												){
												// do not show the License Management Tab. 
											}
											else {
												// so the License Management Tab.
												%>
											
											
											<td id='organizationTD' align='center' valign='center'  width='150' height='50'
													class = 'nonFocusTab'
													style='cursor:pointer'
													onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/UserDashboard/userOrganization.jsp";'
													>
													<span class='normalText' style='color:white'> 
														 License Management
													</span>
												</td>	
													
												
												<%
											}
											
											%>


									
											<%
												if (this.getServletContext().getInitParameter("installationType").equals("onSite")) {
												// lets print a message that the SiteAdmin is the only one who can manage licenses.							
											%>
											
											<td id='siteMetricsTD' align='center' valign='center'  width='150' height='50'
													class = 'nonFocusTab'
													style='cursor:pointer'
													onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/UserDashboard/siteMetrics.jsp";'
													>
												<span class='normalText' style='color:white'> 
													Site Metrics
												</span>
											</td>
																						<%	}%> 
											<td id='accountProfileTD' align='center' valign='center'  width='150' height='50'
													class = 'focusTab'>
												<span class='normalText'>
													<font color='gray'>Jira Integration</font>
												</span>
											</td>
																		
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<div id="userDashboardDiv" class="level1Box">
											<table  width='100%'  >
													<tr>
														<td colspan='2'>
															<table>
																<tr>			
																	<td id='projectDasnboardTD' align='center' valign='center'  width='150' height='50'
																			class = 'focusTab'>
																		<span class='normalText'>
																			All Jira Objects
																		</span>
																	</td>
																</tr>
															</table>
														</td>	
													</tr>
																	
													
													<tr>
													<td colspan='2'>
														<div id='jiraDashboardDiv'  style='display:block'>
														<%@include file="/jsp/Jira/displayJiraDashboard.jsp"%>
														</div>
													</td>
												</tr>
											</table>
									</div>
								</td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
		</table>
		</div>
		<jsp:include page="/jsp/WebSite/Common/footer.jsp" />
	</div>

</body></html>