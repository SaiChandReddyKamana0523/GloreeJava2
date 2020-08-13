<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>
<%@ page import="java.net.*" %>

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

	// API Call 1
	String JID = request.getParameter("JID");
	String JPROJECT = request.getParameter("JPROJECT");
	String JTYPE = request.getParameter("JTYPE");
	String JPRIORITY = request.getParameter("JPRIORITY");
	String JLABELS = request.getParameter("JLABELS");
	String JSTATUS = request.getParameter("JSTATUS");
	String JRESOLUTION = request.getParameter("JRESOLUTION");
	String JAFFECTSV = request.getParameter("JAFFECTSV");
	String JFIXV = request.getParameter("JFIXV");
	String JASSIGNEE = request.getParameter("JASSIGNEE");
	String JREPORTER = request.getParameter("JREPORTER");
	String JCREATED = request.getParameter("JCREATED");
	String JUPDATED = request.getParameter("JUPDATED");
	
	String JURL = request.getParameter("JURL");
	String JTITLE = request.getParameter("JTITLE");
	String JDESCRIPTION = request.getParameter("JDESCRIPTION");
	
	
	
	
	
	if (JID == null ) {JID = "";}
	if (JPROJECT == null ) {JPROJECT = "";}
	if (JURL == null ) {JURL = "";}
	if (JID.equals("")  || (JURL.equals(""))){
		// do nothing and get out.
		%>
		<span class='normalText'><b><font color='red'>Error : </font> : JID and JURL are required</b></span>
		<%
		return;
	}
	
	
	if (JTYPE == null ) {JTYPE = "#EMPTY#";}
	if (JPRIORITY == null ) {JPRIORITY = "#EMPTY#";}
	if (JLABELS == null) {JLABELS = "#EMPTY#";}
	if (JSTATUS == null ) {JSTATUS = "#EMPTY#";}
	if (JRESOLUTION == null ) {JRESOLUTION = "#EMPTY#";}
	if (JAFFECTSV == null ) {JAFFECTSV = "#EMPTY#";}
	if (JFIXV == null ) {JFIXV = "#EMPTY#";}
	if (JASSIGNEE == null ) {JASSIGNEE = "#EMPTY#";}
	if (JREPORTER == null ) {JREPORTER = "#EMPTY#";}
	if (JCREATED == null ) {JCREATED = "#EMPTY#";}
	if (JUPDATED == null ) {JUPDATED = "#EMPTY#";}
	
	if (JTITLE == null ) {JTITLE = "#EMPTY#";}
	if (JDESCRIPTION == null ) {JDESCRIPTION = "#EMPTY#";}
	
	
	// if url , title, desc are longer than 1000 chars, truncate them.
	if (JURL.length() > 1000 ) {JURL = JURL.substring(0, 999);}
	if (JTITLE.length() > 1000 ) {JTITLE = JTITLE.substring(0, 999);}
	if (JDESCRIPTION.length() > 1000 ) {JDESCRIPTION = JDESCRIPTION.substring(0, 999);}
	
	
	// when we send values as hidden params, they will fail if there isa  ' in the name. so lets strip it out.
	if (JPROJECT.contains("'") ) {JPROJECT = JPROJECT.replace("'", " ");}
	if (JURL.contains("'") ) {JURL = JURL.replace("'", " ");}
	if (JTITLE.contains("'") ) {JTITLE = JTITLE.replace("'", " ");}
	if (JDESCRIPTION.contains("'") ) {JDESCRIPTION = JDESCRIPTION.replace("'", " ");}
	
	
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	if (securityProfile == null){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
		String authenticationType = this.getServletContext().getInitParameter("authenticationType");

			if (authenticationType.equals("database")){
				// this means that the user is not yet authenticated, so lets send him to the db log in page.
				
				
				%>
				<jsp:forward page="/jsp/WebSite/startPage.jsp">
					<jsp:param name="callBackURL" value="jira"/>
					<jsp:param name="JID" value="<%=JID %>"/>
					<jsp:param name="JPROJECT" value="<%=JPROJECT %>"/>
					<jsp:param name="JTYPE" value="<%=JTYPE %>"/>
					<jsp:param name="JPRIORITY" value="<%=JPRIORITY %>"/>
					<jsp:param name="JLABELS" value="<%=JLABELS %>"/>
					<jsp:param name="JSTATUS" value="<%=JSTATUS %>"/>
					<jsp:param name="JRESOLUTION" value="<%=JRESOLUTION %>"/>
					<jsp:param name="JAFFECTSV" value="<%=JAFFECTSV %>"/>
					<jsp:param name="JFIXV" value="<%=JFIXV %>"/>
					<jsp:param name="JASSIGNEE" value="<%=JASSIGNEE %>"/>
					<jsp:param name="JREPORTER" value="<%=JREPORTER %>"/>
					<jsp:param name="JCREATED" value="<%=JCREATED %>"/>
					<jsp:param name="JUPDATED" value="<%=JUPDATED %>"/>
					<jsp:param name="JURL" value="<%=JURL %>"/>
					<jsp:param name="JTITLE" value="<%=JTITLE %>"/>
					<jsp:param name="JID" value="<%=JID %>"/>
					<jsp:param name="JDESCRIPTION" value="<%=JDESCRIPTION %>"/>
				</jsp:forward>
				<% 
				return;
			}
			if (authenticationType.equals("ldap")){
				// this means that the user has already been authenticated against ldap, but his profile has not been loaded.
				%>
				<jsp:forward page="/servlet/UserAccountAction?action=signIn&callBackURL=jira&JID=<%=JID%>&JPROJECT=<%=JPROJECT%>&JTYPE=<%=JTYPE%>&JPRIORITY=<%=JPRIORITY%>&JLABELS=<%=JLABELS%>&JSTATUS=<%=JSTATUS%>&JRESOLUTION=<%=JRESOLUTION%>&JAFFECTSV=<%=JAFFECTSV%>&JFIXV=<%=JFIXV%>&JASSIGNEE=<%=JASSIGNEE%>&JREPORTER=<%=JREPORTER%>&JCREATED=<%=JCREATED%>&JUPDATED=<%=JUPDATED%>&JURL=<%=JURL%>&JTITLE=<%=JTITLE%>&JDESCRIPTION=<%=JDESCRIPTION%>"/>
				<% 
				return;
			}
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
													class = 'nonFocusTab'>
												<a href='/GloreeJava2/jsp/UserDashboard/myTasks.jsp'> My Tasks <a>
											</td>
											<td id='projectDasnboardTD' align='center' valign='center'  width='150' height='50'
													class = 'nonFocusTab'>
												<span class='normalText'>
													<a href='/GloreeJava2/jsp/UserDashboard/userProjects.jsp'> My Projects <a>
												</span>
											</td>
											<td id='accountProfileTD' align='center' valign='center'  width='150' height='50'
													class = 'nonFocusTab'>
												<span class='normalText'>
													<a href='/GloreeJava2/jsp/UserDashboard/userProfile.jsp'> Account Profile <a>
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
													class = 'nonFocusTab'>
													<span class='normalText'> 
														<a href='/GloreeJava2/jsp/UserDashboard/userOrganization.jsp'> License Management <a>
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
													class = 'nonFocusTab'>
												<span class='normalText'> 
													<a href='/GloreeJava2/jsp/UserDashboard/siteMetrics.jsp'> Site Metrics<a>
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
										
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
<form method=post action='#'>





	<input type='hidden' name='JID' id='JID' value='<%=JID%>'>
	<input type='hidden' name='JPROJECT' id='JPROJECT' value='<%=JPROJECT%>'>
	
	<input type='hidden' name='JPROJECT' id='JPROJECT' value='<%=JPROJECT%>'>
	<input type='hidden' name='JTYPE' id='JTYPE' value='<%=JTYPE%>'>
	<input type='hidden' name='JPRIORITY' id='JPRIORITY' value='<%=JPRIORITY%>'>
	<input type='hidden' name='JLABELS' id='JLABELS' value='<%=JLABELS%>'>
	<input type='hidden' name='JSTATUS' id='JSTATUS' value='<%=JSTATUS%>'>

	<input type='hidden' name='JRESOLUTION' id='JRESOLUTION' value='<%=JRESOLUTION%>'>
	<input type='hidden' name='JAFFECTSV' id='JAFFECTSV' value='<%=JAFFECTSV%>'>
	<input type='hidden' name='JFIXV' id='JFIXV' value='<%=JFIXV%>'>
	
	<input type='hidden' name='JASSIGNEE' id='JASSIGNEE' value='<%=JASSIGNEE%>'>
	<input type='hidden' name='JREPORTER' id='JREPORTER' value='<%=JREPORTER%>'>
	<input type='hidden' name='JCREATED' id='JCREATED' value='<%=JCREATED%>'>
	
	<input type='hidden' name='JUPDATED' id='JUPDATED' value='<%=JUPDATED%>'>
	<input type='hidden' name='JURL' id='JURL' value='<%=JURL%>'>
	<input type='hidden' name='JTITLE' id='JTITLE' value='<%=JTITLE%>'>
	<input type='hidden' name='JDESCRIPTION' id='JDESCRIPTION' value='<%=JDESCRIPTION%>'>
	
		
	<div align='center'>
		<table  width='100%'  >
				<tr>
					<td colspan='2'>
						<table>
							<tr>
											<td id='jiraObjectTD' align='center' valign='center'  width='150' height='50'
													class = 'focusTab'>
												<span class='normalText'><font color='gray'> Jira Object Id <%=JID %> </font></span>
											</td>
											<td id='jiraDashboardTD' align='center' valign='center'  width='150' height='50'
													class = 'nonFocusTab'>
												<span class='normalText'>
													<a href='#' onClick='
													document.getElementById("jiraObjectTD").className="nonFocusTab";
													document.getElementById("jiraDashboardTD").className="focusTab";
													displayJiraDashboard();'> Jira Dashboard <a>
												</span>
											</td>
							</tr>
						</table>
					</td>	
				</tr>
								
				
			
			<tr >

				<td valign='top' >
					<div id='newProxiesDiv' style="overflow: auto; width: 600px; height: 800px; 
						border-left: 1px  lightgray solid ; border-right: 1px lightgray solid; border-top: 1px lightgray solid;  
						border-bottom: 1px lightgray solid; 
						padding:0px; margin: 1px" >
						<table class='paddedTable'>
							<tr>
								<td colspan='2'>
									<table>
										<tr>
											<td '>
												<br>
												<span class="sectionHeadingText10"><b>Connect Jira Object <%=JID%> to TraceCloud Requirements</b></span>
											</td>
											<td>
												
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td width='100px' align='left'>
									<span class='normalText'>Project</span>
								</td>
								<td align='left'>
									<span class='normalText'>
										<select id='projectId' onChange='displayFoldersInJiraHome();'>
										<option value='-1'>Select A TraceCloud Project to connect to </option>	
										<%
										String prefHideProjects = user.getPrefHideProjects();
										Iterator p = projects.iterator();
										while (p.hasNext()){
											Project project = (Project) p.next();
											boolean isAHiddenProject = false;
											if(prefHideProjects.contains( project.getProjectId() + ":#:" + project.getShortName()))
											{
												continue;
											}
											else {
											%>
											<option value='<%=project.getProjectId()%>'>
											<%=project.getShortName()%> : <%=project.getProjectName() %>
											</option>
											
										<%
											}
										}
										%>
										</select>
									</span>
								</td>
							</tr>
							<tr>
								<td colspan='2' align='left'>	
									<div id='foldersInJiraDiv' style='display:none'></div>
									
								</td>
							</tr>
						</table>
					</div>
				
				</td>
				<td valign='top'   >
					<div id='existingProxiesDiv' style="overflow: auto; width: 750px; height: 800px; 
						border-left: 1px  lightgray solid ; border-right: 1px lightgray solid; border-top: 1px lightgray solid;  
						border-bottom: 1px lightgray solid; 
						padding:0px; margin: 1px" >
						  <%@include file="displayExistingProxies.jsp"%>
					</div>				
				</td>
			</tr>
			<tr>
				<td colspan='2'>
					<div id='jiraDashboardDiv'  style='display:none'></div>
				</td>
			</tr>
		</table>
	</div>
						
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
		<jsp:include page="/jsp/WebSite/Common/footer.jsp" />
	</div>

</body></html>