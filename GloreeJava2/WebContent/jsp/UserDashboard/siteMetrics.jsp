<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->    
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="java.sql.Date" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<!-- Get the list of my projects by calling the util. -->


<%
	String serverName = request.getServerName();
	//authorization
	// since we need authorization as well as authenticaiton we will use the 
	// security profile object.
	SecurityProfile userProjectsSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");


	if (userProjectsSecurityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%
	
	// authorization : since we are explicityly checking for and 
	// listing all the projects the user has access to
	// we are OK here. 
	}
	
	User user = userProjectsSecurityProfile.getUser();
			
	if (this.getServletContext().getInitParameter("installationType").equals("onSite")) {
	
		String databaseType = this.getServletContext().getInitParameter("databaseType");
	
%>


<html xml:lang="en" xmlns="http://www.w3.org/1999/xhtml" lang="en"><head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">



	<title>TraceCloud - SAAS Agile Scrum Requirements Management - Collaborate, Define, Manage and Deliver your Customer Requirements</title>
    <meta name="description" content="Collaboration tools to define, manage and deliver your customer requirements on time and within budget. Significantly improves customer satisfaction ">
	<meta name="keywords" content="free requirements management, saas requirements management tool, online requirements management, doors, requisitepro, customer requirements, shared requirements, tl9000, project management, project requirements, agile, agile requirements management.">


	

	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userAccount.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userDashboard.js?v=20200630"></script>  
		
	<link rel="stylesheet" href="/GloreeJava2/css/greeny.css" type="text/css" >
	<link rel="stylesheet" href="/GloreeJava2/css/common.css" type="text/css">
	<link rel="stylesheet" href="/GloreeJava2/css/sales_global.css" type="text/css">
	<link rel="stylesheet" href="/GloreeJava2/css/sales_home.css" type="text/css" media="screen">
	
	<!--  Bootstratp  JS and CSS files -->
	 <script src="/GloreeJava2/js/jquery-3.1.1.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap.min.js"></script>
	 <link href="/GloreeJava2/css/bootstrap.min.css" rel="stylesheet" media="screen">
	
	
	
	
</head>
<body onLoad='loadSiteMetrics();' >

	
<div class="wrapper box_theme_login_wrapper">
		<jsp:include page="/jsp/WebSite/Common/TCToolbar.jsp" />	

		<div style="border-width: 2px; border-style: solid; border-color: white; padding:15px ">
		<table width='100%' >
			<tr>
				<td valign='top'>
				
				</td>
				<td>
					<div  >
						<table width='100%'>
							<tr>
								<td valign='top'>
									<table class='paddedTable' align='left' width='640px' >
									
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
													class = 'focusTab'>
												<span class='normalText'> 
													<a href='/GloreeJava2/jsp/UserDashboard/siteMetrics.jsp'> Site Metrics<a>
												</span>
											</td>
											<%	}%> 													
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<div id="userDashboardDiv" class="level1Box" >
										<table align="center" class='paddedTable' width='100%'>
												<tr><td>
													<div id='totalsDiv'></div>
												</td></tr>		
												
												<tr><td>
													<div id='usersDiv'></div>
												</td></tr>		
													
												<tr><td>
													<div id='activityByMonthMetricsDiv'></div>
												</td></tr>		
												
														
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
</body>
</html>	


<%} %>
















 