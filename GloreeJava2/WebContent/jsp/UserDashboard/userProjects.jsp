<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->    
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="java.sql.Date" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>


<!-- Get the list of my projects by calling the util. -->

	<!--  Google Analytics Tracking  -->	
	<script type="text/javascript">
	
	  var _gaq = _gaq || [];
	  _gaq.push(['_setAccount', 'UA-31449327-1']);
	  _gaq.push(['_trackPageview']);
	
	  (function() {
	    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
	    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	  })();
	
	</script>
	
	
	
	

<%
	String installationType = this.getServletContext().getInitParameter("installationType");
	
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
	
	// there are times when the security profile could have changed due to the user's license management actions
	// so lets refresh his / her profile.
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	userProjectsSecurityProfile = new SecurityProfile(userProjectsSecurityProfile.getUser().getUserId(),this.getServletContext().getInitParameter("databaseType"));
	session.setAttribute("securityProfile",userProjectsSecurityProfile );
	 
	User user = userProjectsSecurityProfile.getUser();
	Organization organization = new Organization(user.getMyOwnedOrganization());
	ArrayList sponsoredProjects = organization.getSponsoredProjects();
	// get the list of projects this user has access to
	ArrayList projects = userProjectsSecurityProfile.getProjectObjects();
	
	String projectCreated = (String) request.getAttribute("projectCreated");
	String projectCreatedMessage = "";
	 if (projectCreated != null) {
		 projectCreatedMessage = " " + 
		 	"<tr>" +
			"	<td width='100%' align='left'> <div class='alert alert-success'>" +
	 		" 	<span class='normalText'> Your Project has been successfully created </span>" +
	 		" 	</div></td> " +
	 		"</tr> ";
	 }
	 
		// lets display the invited message.
		// NOTE : we don't need to worry about NOT IN DOMAIN email Ids message
		// as the front end Javascript will prevent any non-domain emaild Id submittals.
		String invitedEmailIds = (String) request.getAttribute("invitedEmailIds");
		String invitedMessage = "";
		if ((invitedEmailIds != null) && (!(invitedEmailIds.equals("")))) {
			invitedMessage = "<tr> " +
				" <td colspan='2' align='left' width='100%'> " +
				" <div alert alert-danger> " +
				" <span class='normalText'> " + 
				" The user you added is currently not in the tracecloud system." +
				  "We have sent them an email and when they open an account  " +
				" they will automatically have access to this project." ;
				
			String [] emailIds = {invitedEmailIds};
			if (invitedEmailIds.contains(",")){
				emailIds = invitedEmailIds.split(",");
			}
			for (int i=0; i<emailIds.length; i++){
				invitedMessage += "<br>"    +  emailIds[i]  ;  
			}
			invitedMessage += " </span> </div> " + 
				" </td> " + 
				" </tr>";
		}

		
		

		
	 String notAMemberOfProject = (String) request.getAttribute("notAMemberOfProject");
	 String notAMemberOfProjectMessage = "";
	 if (!((notAMemberOfProject == null) || (notAMemberOfProject.equals("")))){
		 
		 // i.e. notAMemberOfProject is not empty / null. 
		 // the user got here because he tried to open a req that exists in a 
		 // project of which the user is notAMember.
		
		 notAMemberOfProjectMessage = " " + 
		 	"<tr>" +
			"	<td width='100%'> " +
	 		" 	<div id='projectCreatedMessage' class='alert alert-danger'> " +
	 		" 	You are not a member of the project in which this requirement exists." +
	 		" 	</div> " +
	 		" 	</td> " +
	 		"</tr> ";
	 }
	 

	 // lets see if this user is a member of any paid projects.
	 boolean isMemberOfSponsoredProject = false;
	 Iterator ip = projects.iterator();
	 while (ip.hasNext()){
		 Project p = (Project) ip.next();
		 if (p.getBillingOrganizationId() > 0) {
			 isMemberOfSponsoredProject = true;
		 }
	 }
	 
	 // lets prepare the welcome message based on the userType. (AccountStatus).
	 String welcomeDiv = "";
	 String accessDisabled = "";
	 if (
			 (user.getUserType() != null) && 
			 (user.getUserType().equals("trial")) &&
			 (sponsoredProjects.size() == 0) &&
			 (!isMemberOfSponsoredProject)
		){
		 // Free trial user and no sponsored projects and is not a member of a sponsored project
		 welcomeDiv = "	" +
		 	" <div id ='userAccountInfoDiv' class='alert alert-danger'  > " +
			"		<span class='normalText'> " +
			"			Welcome <b>" + user.getFirstName() +  "&nbsp;" + user.getLastName()  + " </b> . " +
			"			<br><br> To help you come up to speed, we have created a sample project with some test data." +
			"			Please explore the project. <br><br><br>Inside the project you will see a <a href='#' class='btn btn-sm btn-info' style='color:white'>Start Tour</a>  "+
			"  button. This tour will quickly familiarize you with the tool.  <br><br> If you have any questions please reach us at support@tracecloud.com "+
			" <br><br><br>You have " + user.getDaysLeft() + " days left in your trial period." +
			"			<br>" +
			"		</span> " +
			" </div>";
	 }
	 else if (
			 (user.getUserType() != null) && 
			 (user.getUserType().equals("expired")) &&
			 (sponsoredProjects.size() == 0)
		){
		 // expired user with no sponsored projects 
		 welcomeDiv = "	" +
		 	" <div id ='userAccountInfoDiv' class='alert alert-danger' > " +
			"		<span class='normalText'> " + 
			"			Welcome <b>" + user.getFirstName() +  "&nbsp;" + user.getLastName()+ " </b> . " +
			"			Your free trial period is over. " +
			"			To sign up for paid User / Project account please "+
			"			<a href=/GloreeJava2/jsp/UserDashboard/userOrganization.jsp>Click Here </a> . " + 
			"		</span> " +
			" </div>";
			
	 }
	 
	 
	 if (
			 (user.getUserType() != null) && 
			 (user.getUserType().equals("expired")) 
		){
		accessDisabled = "DISABLED";
	 }
	  
	 
	 // if this is an onsite installation, and the user does not have a read write licnse, we just diable access.
	 if (
			(installationType.toLowerCase().equals("onsite") )
				&&
			(!
					(user.getUserType().equals("readWrite") || (user.getUserType().equals("readOnly")))
					
			) 
		)
	 {
		 accessDisabled = "DISABLED";
		 welcomeDiv = "	" +
				 	" <div id ='userAccountInfoDiv' class='alert alert-success' > " +
					"	<br>	<span class='normalText'> " + 
					"			You do not have a valid TraceCloud License. Please work with your onsite license administrator" + 
					"		</span> </br> " +
					" </div>";
				 
	 }
	
	 String prefHideProjects = user.getPrefHideProjects();
	 String showHiddenProjects = request.getParameter("showHiddenProjects");
	 if (showHiddenProjects == null ){
		 showHiddenProjects = "";
	 }
	 
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
<body >

	
<div class="wrapper box_theme_login_wrapper">
		<jsp:include page="/jsp/WebSite/Common/TCToolbar.jsp" />	

		<div style="border-width: 2px; border-style: solid; border-color: white; padding:15px ">
		<table width='100%'>
			<tr>
				<td valign='top'>
				
				</td>
				<td>
					<div align='center' >
						<table width='100%'>
							<tr>
								<td valign='top'>
									<table align='left' valign='top' width='800px'>
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
													class = 'focusTab'>
												<span class='normalText'>
													<font color='gray'>
													My Projects
													</font>
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
											
											<td id='siteMetricsTD' align='center' valign='center'  width='150' height='50'
													class = 'nonFocusTab'
													style='cursor:pointer'
													onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/ServiceNow/serviceNow.jsp";'
													>
												<span class='normalText' style='color:white'> 
													ServiceNow 
												</span>
											</td>													
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<div id="userDashboardDiv" class="level1Box" >
										<table align="center" class='paddedTable' width='100%'>
											<tr>
												<td align='left'>
												<%=welcomeDiv%>
												</td>
											</tr>				

											





										<%
											if (siteAdministrator.contains(user.getEmailId())){
											// If the user is a site admin, lets show him / her the site health.				
												PreparedStatement prepStmt = null;
												ResultSet rs = null;
												java.sql.Connection con = null;
												
												String projectMetricsRunDate = "";
												float projectMetricsDaysSinceLastRun = 0;
												String batchMailerRunDate = "";
												float batchMailerDaysSinceLastRun = 0;
												
		
												try {
												
													
													javax.naming.Context context =  new javax.naming.InitialContext();
													javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
													con = dataSource.getConnection();
													
													String sql = "";
													if (databaseType.toLowerCase().equals("oracle")){
														sql = "select max(data_load_dt) 'projectMetricsRunDate' , now() -  max(data_load_dt)  'projectMetricsDaysSinceLastRun' from  gr_project_metrics;";
													}
													else{
														sql = "select max(data_load_dt) 'projectMetricsRunDate' , datediff(now(),  max(data_load_dt) ) 'projectMetricsDaysSinceLastRun' from  gr_project_metrics;";
															
													}
													prepStmt = con.prepareStatement(sql);
													rs = prepStmt.executeQuery();
													while (rs.next()){
														projectMetricsRunDate = rs.getString("projectMetricsRunDate");
														projectMetricsDaysSinceLastRun = rs.getFloat("projectMetricsDaysSinceLastRun");
														
													}
													
													
													if (databaseType.toLowerCase().equals("oracle")){
														sql = "select max(message_sent_dt) 'batchMailerRunDate', now() -  max(message_sent_dt)  'batchMailerDaysSinceLastRun' from gr_messages;";
													}
													else{
														sql = "select max(message_sent_dt) 'batchMailerRunDate', datediff(now(), max(message_sent_dt) ) 'batchMailerDaysSinceLastRun'  from gr_messages;";
															
													}
													prepStmt = con.prepareStatement(sql);
													rs = prepStmt.executeQuery();
													while (rs.next()){
														batchMailerRunDate = rs.getString("batchMailerRunDate");
														batchMailerDaysSinceLastRun = rs.getFloat("batchMailerDaysSinceLastRun");
													}
													
															
													con.close();
												} catch (Exception e) {
													
													e.printStackTrace();
												}  finally {
													if (prepStmt !=null) { 
														try {prepStmt.close();} catch (Exception e) {}
													} 
													if (rs != null) { 
														try {rs.close();} catch (Exception e) {}	
													} 
													if (con != null) {
														try {con.close();} catch (Exception e) {}
														con = null;
													}
												}
											
										%>
											<tr>
												<td style="border:solid 2px #060">
												<table width='100%' >
													<tr>
														<td colspan='3' align='left' bgcolor='#99CCFF' >
															<span class='normalText'>Site Health (Only visible to Site Admins)</span>
														</td>
													</tr>
													<tr>
														<td align='left' style='width:150px'><span class='normalText' > <b>Job Name</b> </span></td>
														<td align='left' style='width:150px'><span class='normalText'> <b>Last Run On </b></span></td>
														<td align='left' style='width:150px'><span class='normalText' > <b> Days since last run </b></span></td>
														
													</tr>
													<tr>
														<td align='left' ><span class='normalText' > Batch Metrics </span></td>
														<td align='left'><span class='normalText'> <%=projectMetricsRunDate%> </span></td>
														<td align='left'><span class='normalText'> 
														<%
														if (projectMetricsDaysSinceLastRun > 1){
															%>
															<img src="/GloreeJava2/images/strobe.gif" title='Please restart these jobs or reach out to support@tracecloud.com for help' border="0" ></img>
															<%
														}
														%>
														<%=projectMetricsDaysSinceLastRun%> </span></td>
														
													</tr>
													<tr>
														<td align='left' ><span class='normalText' > Batch Mailer </span></td>
														<td align='left'> <span class='normalText'> <%=batchMailerRunDate%> </span></td>
														<td align='left'> <span class='normalText'> 
														<%
														if (batchMailerDaysSinceLastRun > 1){
															%>
															<img src="/GloreeJava2/images/strobe.gif" title='Please restart these jobs or reach out to support@tracecloud.com for help' border="0" ></img>
															<%
														}
														%>
														<%=batchMailerDaysSinceLastRun%> </span></td>
													</tr>
												</table>
												</td>
											</tr>
			
										<%	}%>												
											
											
											
											
										<%
											if (this.getServletContext().getInitParameter("installationType").equals("onSite")) {
											// lets print a message that the SiteAdmin is the only one who can manage licenses.							
										%>
												<tr>
													<td>
														
														<div class='alert alert-success'>
															<br></br>
															<span class='normalText'>
															 Please reach out to <b><%=siteAdministrator %></b> if you have any technical / license questions about TraceCloud.
															</span>
															<br></br>
														</div>
														
													</td>
												</tr>
												
												<%
												// lets see if this site has a valid license key
												String licenseString = this.getServletContext().getInitParameter("licenseString") ;
												if (licenseString == null){
													licenseString  = "";
												}
												boolean validLicenseString = ProjectUtil.isLicenseValid(licenseString);
												int daysSinceInstallation = ProjectUtil.daysSinceInstallation(databaseType);
												if (!(validLicenseString)){
													// this is an onsite instalaltion and does not have a valid license string
													if ((daysSinceInstallation > 210) && (daysSinceInstallation < 240)){
														// OnSite, no valid license string and it has been > 180 and < 210 days since installation
														%>
														<tr>
															<td>
																
																<div class='alert alert-success'>
																	<br></br>
																	<span class='normalText'>
																		<font color='red'>
																		 Thank you for evaluating TraceCloud. 
																		 <br></br>
																		 This site does not have a valid production license. Please reach out to support@tracecloud.com
																		 to install a valid license in the next <b><font size='20pt'><%=240-daysSinceInstallation %></font></b> days. 
																		</font>
																	</span>
																	<br></br>
																</div>
																
															</td>
														</tr>
																
														<%
													}
													if (daysSinceInstallation >= 240){
														// OnSite, no valid license string and it has been > 210 days since installation
														%>
														<tr>
															<td>
																
																<div class='alert alert-success'>
																	<br></br>
																	<span class='normalText'>
																		<font color='red'>
																		 Thank you for evaluating TraceCloud. 
																		 <br></br>
																		 This site does not have a valid production license. Please reach out to support@tracecloud.com
																		 to install a valid license  
																		</font>
																	</span>
																	<br></br>
																</div>
																
															</td>
														</tr>
																
														<%
														return;
													}
												}
												
												%>
											
											
			
										<%	}%>												
											<tr>
												<td align='left'>
													<div id ='cNPWAction' >
														<table><tr><td>
														<FORM METHOD="LINK" ACTION="/GloreeJava2/jsp/CNPW/cnpw.jsp">
														<INPUT TYPE="submit"   class='btn btn-success ' VALUE="Create A New Project">
														</FORM>
														</td><td>
														
														
														<%	
														if ((showHiddenProjects.equals("")) && (prefHideProjects.contains(":#:"))){
															// there are some hidden projects for this user
															// and we are asked to show hidden projects
														%>
															
																<a href='/GloreeJava2/jsp/UserDashboard/userProjects.jsp?showHiddenProjects=true'>
																	Show my hidden projects</a>
															
														<%
														}
														%>
														</td></tr></table>
													</div>
														
												</td>
											</tr>

											
																			
											
											<%
												if (this.getServletContext().getInitParameter("installationType").equals("onSite")) {	
												// the following options do not show up for SAAS installation.
												// in an onsite model, its ok for the entire user base to search for a project.
											%>
												<tr>
													<td align='left'>
														<span class='normalText'>
														Search for Project 
														<input type='text' name='projectSearchString' id='projectSearchString'
														onkeypress=' 
															if (event.keyCode == 13) {
																findProjects();
															}'>
														<input type='button'  class='btn btn-primary btn-sm' name='Find' id='findButton' value='Go' 
														onClick='findProjects();'></input>
														</span>
														<b>
														<a href='#' onClick='findProjects();'> Show ALL Projects </a>
														</b>
														<div id='foundProjectsDiv' style="display:none"></div>
													</td>
												</tr>												
											<%} %>
											
											<%=projectCreatedMessage %>
											<%=invitedMessage %>
											<%=notAMemberOfProjectMessage %>
											
											<tr>
												<td>
													<br></br>
													
													<div class='alert alert-info'>Do you need help? Contact support@tracecloud.com</div>
													
												
													
													<br></br>
													<div id ='listOfProjects' class='level2Box'>
													<form method="post" action="/GloreeJava2/servlet/ProjectAction" id="form1"> 
													<input type="hidden" name="projectId" id="projectId" value= "" >
													<input type="hidden" name="projectPrefix"  id="projectPrefix" value= "" >
													
													<input type="hidden" name="action" value="">
													
													<%
													if (projects.size() > 0 ){
														
														
													%>
														<table class='table' align='left' width='100%'>
														
															<tr><td colspan='8'>&nbsp;</td></tr>										
															<tr class='danger'>
																<td width='120'> 
																	<span class='headingText'>
																	<b>Prefix</b>
																	</span>
																</td>
																<td>
																	<span class='headingText'> 
																	<b>Project Name </b>
																	</span>
																</td>
																<td>
																	<span class='headingText'> 
																	<b>Size </b>
																	</span>
																</td>
																<td align='center'>
																	<span class='headingText'> 
																	<b>Website</b>
																	</span>
																</td>
															<td align='center'>
																	
																</td>
																
																<td align='center' style='width:100px'> 
																	<span class='headingText'>
																	
																	</span>
																</td>
																<td align='center' style='width:100px'> 
																	<span class='headingText'>
																	
																	</span>
																</td>
																<td align='center' style='width:100px'> 
																	<span class='headingText'>
																	
																	</span>
																</td>
																
															</tr>
															<%
															Iterator iP = projects.iterator();
															while (iP.hasNext()){
																Project project = (Project) iP.next();
																boolean isAHiddenProject = false;
																if
																	(prefHideProjects.contains( project.getProjectId() + ":#:" + project.getShortName()))
															{
																	isAHiddenProject = true;
																}
																
																// we are asked to not show hidden projects  and this project is a hidden project, so don't show it
																
																if ((showHiddenProjects.equals("")) && (isAHiddenProject)){
																	continue;
																}
																String title = "";
																String projectOwner = project.getProjectOwner();
																if ((projectOwner != null) && (projectOwner.length() > 0)) {
																	title += " OWNER : " + projectOwner;
																}
																
																String projectOrganization = project.getProjectOrganization();
																if ((projectOrganization  != null) && (projectOrganization .length() > 0)) {
																	title += " ORGANIZATION : " + projectOrganization ;
																}
																
																title += " DESCRIPTION : " + project.getProjectDescription();
																
																if (title.contains("'")){
																	title = title.replace("'"," ");
																}
																
																ArrayList projectRelationsLight = project.getProjectRelationsLight(databaseType);
																
																String projectClass = "info";
																if (project.getProjectType().equals("resource management") ){
																	projectClass = "warning";
																}
																
																if (project.getBillingOrganizationId() > 0){
																	
																	// all sponsored projects go here.
															%>
																<tr class='<%=projectClass %>'
																	onmouseover=
																		"
																			this.style.background='#E5EBFF';
																			document.getElementById('inviteOthersToProject<%=project.getProjectId()%>Div').style.display='block';
																			document.getElementById('hideProject<%=project.getProjectId()%>Div').style.display='block';
																		" 
																	onmouseout=
																		"
																			this.style.background='white';
																			document.getElementById('inviteOthersToProject<%=project.getProjectId()%>Div').style.display='none';
																			document.getElementById('hideProject<%=project.getProjectId()%>Div').style.display='none';
																		"
																>
																	<td align='left'> 
																		<img src="/GloreeJava2/images/project16.png" border="0">
																		
																		<span class='normalText' title='<%=title%>'>
																			<%=project.getShortName() %> 
																		</span>
																	</td>
																	<td align='left'> 
																		<span class='normalText' 
																		title='<%=title%>'>
																		<%
																			if(isAHiddenProject){
																		%>
																			<font color='red'>(Hidden)</font>
																		<%} %>
																		<%=project.getProjectName() %> 
																		</span>
																	</td>
																	<td align='left'> 
																		<span class='normalText' 
																		title='There are <%=project.getNumberOfRequirements() %> Requirements in this Project'>
																		<%=project.getNumberOfRequirements() %>
																		</span>
																	</td>
																	
																	<td align='center'>
																		<% if ((project.getProjectWebsite() != null) && !(project.getProjectWebsite().equals (""))){
																			if (!(project.getProjectWebsite().startsWith("http://"))){
																				// lets add http:// to the beginning of url
																		%>
																				<a href='http://<%=project.getProjectWebsite() %>' TARGET='_blank'>
																					<img src="/GloreeJava2/images/link16.png" border="0">
																				</a>

																		<%
																			}
																			else {
																		%>
																			<a href='<%=project.getProjectWebsite() %>' TARGET='_blank'>
																				<img src="/GloreeJava2/images/link16.png" border="0">
																			</a>
																		<%
																			}
																		} 
																		%>
																	</td>
																	<td></td>
																	<!-- 	
																		
																	<td align='center'>
																		<%
																		if (projectRelationsLight.size() > 0){
																		%>
																				<a href='/GloreeJava2/jsp/UserDashboard/showRelatedProjects.jsp?projectId=<%=project.getProjectId()%>' target='_blank' 
																				title='Show relationships between projects'>
																				<img src="/GloreeJava2/images/organization16.png" border="0">
																				</a>
																		<%
																		}
																		else {
																		%>
																		&nbsp;
																		<%} %>
																	</td>
																	-->
																	<%
																	if (project.getArchived() == 1) {
																		// this is an archived project.
																		%>
																		<td colspan='3' align='center'>
																			<%
																			if ((userProjectsSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))){
																				// user is an admin of this projet. So, can un archive.
																				%>
																				<a href='/GloreeJava2/servlet/ProjectAction?action=reActivateProject&projectId=<%=project.getProjectId() %>'> 
																				Re activate archived Project </a>
																				<%
																			}
																			else {
																				// not an admin of this project . So just gets to see only the 'Archived message'
																			%>
																			<span class='normalText' title='This project has been archived by the administrator of this project'>
																				Archived Project		
																			</span>
																			<%} %>
																		</td>
																		<%
																	}
																	else {
																		// this is an active project
																		%>
																		<td align='center'>
																			<div id='openProject<%=project.getProjectId()%>Div' >
																				<input type='button'  class='btn btn-primary btn-sm' value='Open Project' onClick= 'this.disabled=true;  openProject("<%=project.getProjectId() %>")' >
																				</input>
																			</div>
																		</td>
																		<td align='center'>
																			<div id='inviteOthersToProject<%=project.getProjectId()%>Div' style='display:none;'>
																			<input type='button'  class='btn btn-primary btn-sm' value='Invite Others' onClick= 'inviteOthersToProject("<%=project.getProjectId() %>")' >
																			</input></div>
																		</td>
																		<td align='center'>
																			<div id='hideProject<%=project.getProjectId()%>Div' style='display:none;'>
																				<%
																				if(isAHiddenProject){
																				%>
																					<input type='button'  class='btn btn-primary btn-sm' value='UnHide Project'
																						title='Un Hide this project' 
																						onClick='unHideProject(<%=project.getProjectId()%>,"<%=project.getShortName() %>")'>
																					</input>
																				<%}
																				else {
																				%>
																					<input type='button'  class='btn btn-primary btn-sm' value='Hide Project'
																						title='Temporarily hide this project (You can always un hide it)' 
																						onclick='hideProject(<%=project.getProjectId()%>,"<%=project.getShortName() %>")'>
																					</input>
																				<%} %>
																			</div>
																		</td>
																		
																		<%
																	}
																	%>

																	
																	
																	
																	</tr>
																	<tr>
																		<td colspan='8'>
																			<div id='inviteOthersDiv<%=project.getProjectId()%>' style='display:none'>
																			</div>
																			<div id='showRelatedProjectsSuperDiv<%=project.getProjectId()%>' style='display:none'>
																				<div id='closeShowRelatedProjectsDiv<%=project.getProjectId()%>' style="float:right">
																		  			<a href='#' 
																		  				onClick='
																		  					document.getElementById("showRelatedProjectsDiv<%=project.getProjectId()%>").style.display="none";
																		  					document.getElementById("showRelatedProjectsSuperDiv<%=project.getProjectId()%>").style.display="none";
																		  					document.getElementById("closeShowRelatedProjectsDiv<%=project.getProjectId()%>").style.display="none"'
																		  			>Close</a>
																		  		</div>
																				<div id='showRelatedProjectsDiv<%=project.getProjectId()%>' style='display:none'></div>
																			</div>
																		</td>
																	</tr>
																
														<%}
														else {
															// non sponsored project. To be added only if the user has 
															// a read write / read only / trial account.
															if ((user.getUserType() != null) && (user.getUserType().equals("expired"))){
																// we do not permit trial users to log into any non sponsored projects
																accessDisabled = "DISABLED";
															}
															else {
																// these are either read / read write users trying to get to non-sponsored projects.
																accessDisabled = "";
															}
															
														%>
																<tr class='<%=projectClass %>'
																	onmouseover=
																		"
																			this.style.background='#E5EBFF';
																			document.getElementById('inviteOthersToProject<%=project.getProjectId()%>Div').style.display='block';
																			document.getElementById('hideProject<%=project.getProjectId()%>Div').style.display='block';
																		" 
																	onmouseout=
																		"
																			this.style.background='white';
																			document.getElementById('inviteOthersToProject<%=project.getProjectId()%>Div').style.display='none';
																			document.getElementById('hideProject<%=project.getProjectId()%>Div').style.display='none';
																		"
																>
																<td align='left'> 
																	
																	
																	<img src="/GloreeJava2/images/project16.png" border="0">
																	<span class='normalText' title='<%=title %>'>
																		<%=project.getShortName() %> 
																	</span>
																</td>
																<td align='left'> 
																	<span class='normalText' 
																	title='<%=title%>'>
																	<%
																			if(isAHiddenProject){
																	%>
																		<font color='red'>(Hidden)</font>
																	<%} %>
																	<%=project.getProjectName() %> 
																	</span>
																</td>
																	
																<td align='left'> 
																	<span class='normalText' 
																	title='There are <%=project.getNumberOfRequirements() %> Requirements in this Project'>
																	<%=project.getNumberOfRequirements() %>
																	</span>
																</td>
																	
																	<td align='center'>
																		<% if ((project.getProjectWebsite() != null) && !(project.getProjectWebsite().equals (""))){
																			if (!(project.getProjectWebsite().startsWith("http://"))){
																				// lets add http:// to the beginning of url
																		%>
																				<a href='http://<%=project.getProjectWebsite() %>' TARGET='_blank'>
																					<img src="/GloreeJava2/images/link16.png" border="0">
																				</a>

																		<%
																			}
																			else {
																		%>
																			<a href='<%=project.getProjectWebsite() %>' TARGET='_blank'>
																				<img src="/GloreeJava2/images/link16.png" border="0">
																			</a>
																		<%
																			}
																		} 
																		
																		%>
																	</td>
																	
																	<td></td>	
																	<!-- 
																	<td align='center'>
																		<%
																		if (projectRelationsLight.size() > 0){
																		%>
																				<a href='/GloreeJava2/jsp/UserDashboard/showRelatedProjects.jsp?projectId=<%=project.getProjectId()%>' target='_blank' 
																				title='Show relationships between projects'>
																				<img src="/GloreeJava2/images/organization16.png" border="0">
																				</a>
																		<%
																		}
																		else {
																		%>
																		&nbsp;
																		<%} %>
																	</td>
																	-->
																
																	<% if (project.getArchived() == 1 ) {
																		// archived project
																	%>
																		<td colspan='2' align='center'>
																			<%
																			if ((userProjectsSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))){
																				// user is an admin of this projet. So, can un archive.
																				%>
																				<a href='/GloreeJava2/servlet/ProjectAction?action=reActivateProject&projectId=<%=project.getProjectId() %>'>
																				Re activate archived Project </a>
																				<%
																			}
																			else {
																				// not an admin of this project . So just gets to see only the 'Archived message'
																			%>
																				<span class='normalText' title='This project has been archived by the administrator of this project'>
																					Archived Project		
																				</span>
																			<%} %>
																		</td>
																	<%	
																	}
																	else {
																		// active project
																	%>
																		<td align='center'>
																			<div id='openProject<%=project.getProjectId()%>Div' >
																			<% 
																				if (accessDisabled.equals("")) {
																			%>
																				<input type='button'  class='btn btn-primary btn-sm' value='Open Project' onClick= 'this.disabled=true; openProject("<%=project.getProjectId() %>")' >
																				</input>
																			<%}%>	
																			</div>
																		</td>
																		
																		<td align='center'>
																			<div id='inviteOthersToProject<%=project.getProjectId()%>Div' style='display:none;'>
																			<input type='button'  class='btn btn-primary btn-sm' value='Invite Others' onClick= 'inviteOthersToProject("<%=project.getProjectId() %>")' >
																			</input>
																			</div>
																		</td>
																		<td align='center'>
																			<div id='hideProject<%=project.getProjectId()%>Div' style='display:none;'>
																				<%
																				if(isAHiddenProject){
																				%>
																					<input type='button'   class='btn btn-primary btn-sm' value='UnHide Project'
																						title='Un Hide this project' 
																						onClick='unHideProject(<%=project.getProjectId()%>,"<%=project.getShortName() %>")'>
																					</input>
																				<%}
																				else {
																				%>
																					<input type='button'  class='btn btn-primary btn-sm' value='Hide Project'
																						title='Temporarily hide this project (You can always un hide it)' 
																						onClick='hideProject(<%=project.getProjectId()%>,"<%=project.getShortName() %>") '>
																					</input>
																				<%} %>
																			</div>
																		</td>
																	<%} %>
																</tr>	
																
																<tr>
																	<td colspan='7'>
																		<div id='inviteOthersDiv<%=project.getProjectId()%>' style='display:none'>
																		</div>
																		<div id='showRelatedProjectsSuperDiv<%=project.getProjectId()%>' style='display:none'>
																			<div id='closeShowRelatedProjectsDiv<%=project.getProjectId()%>' style="float:right">
																	  			<a href='#' 
																	  				onClick='
																	  					document.getElementById("showRelatedProjectsDiv<%=project.getProjectId()%>").style.display="none";
																	  					document.getElementById("showRelatedProjectsSuperDiv<%=project.getProjectId()%>").style.display="none";
																	  					document.getElementById("closeShowRelatedProjectsDiv<%=project.getProjectId()%>").style.display="none"'
																	  			>Close</a>
																	  		</div>
																			<div id='showRelatedProjectsDiv<%=project.getProjectId()%>' style='display:none'></div>
																		</div>
																	</td>
																</tr>				
														<%
														}
													}%>
														</table>	
														
														
														
													
													<%} %>
														
														
													<div id='userInvitationsDiv'>
													<jsp:include page="/jsp/UserDashboard/userInvitations.jsp" />
													</div>
														
														
														<table>
															<%
															// if there are < 25 rows of data, the web pagee doesn't look nice
															// so lets put some spacers here.
															int desiredRows = 25;
															int remainingRows = desiredRows - projects.size();
															for (int i =0; i< remainingRows; i++){
															%>
																	<tr><td> &nbsp; </td></tr>
															<%} %>
														</table>

													</form>
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
	
	
	
    <script type="text/javascript">
      var capterra_vkey = '89967b5249786b459ae5275265efaa39',
      capterra_vid = '2095568',
      capterra_prefix = (('https:' == document.location.protocol) ? 'https://ct.capterra.com' : 'http://ct.capterra.com');

      (function() {
        var ct = document.createElement('script'); ct.type = 'text/javascript'; ct.async = true;
        ct.src = capterra_prefix + '/capterra_tracker.js?vid=' + capterra_vid + '&vkey=' + capterra_vkey;
        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ct, s);
      })();
    </script>
      
      
      
</body>
</html>	



















 