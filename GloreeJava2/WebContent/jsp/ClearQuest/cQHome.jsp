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
	<script src="/GloreeJava2/js/clearQuest.js"></script>
	
	
			
	
</head>
<body >


<%

	// API Call 1
	String CTCID = request.getParameter("CTCID");
	String CTCHEADLINE = request.getParameter("CTCHEADLINE");
	String TESTCASEID = request.getParameter("TESTCASEID");
	String TESTCASEHEADLINE = request.getParameter("TESTCASEHEADLINE");
	String CTCWEBLINK = request.getParameter("CTCWEBLINK");
	// Optional
	String RELATEDSCRID = request.getParameter("RELATEDSCRID");
	String RELATEDSCRNAME= request.getParameter("RELATEDSCRNAME");

	
	// API Call 2
	String SCRID = request.getParameter("SCRID");
	String SCRTITLE = request.getParameter("SCRTITLE");
	String SCRWEBLINK = request.getParameter("SCRWEBLINK");
	
	
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if (CTCHEADLINE == null ) {CTCHEADLINE = "";}
	if (TESTCASEHEADLINE == null ) {TESTCASEHEADLINE = "";}
	if (CTCWEBLINK == null ) {CTCWEBLINK = "";}
	if (SCRTITLE == null ) {SCRTITLE = "";}
	if (SCRWEBLINK == null ) {SCRWEBLINK = "";}
	if (RELATEDSCRNAME == null) {RELATEDSCRNAME = "";}
	
	// when we send values as hidden params, they will fail if there isa  ' in the name. so lets strip it out.
	if (CTCHEADLINE.contains("'") ) {CTCHEADLINE = CTCHEADLINE.replace("'", " ");}
	if (TESTCASEHEADLINE.contains("'") ) {TESTCASEHEADLINE = TESTCASEHEADLINE.replace("'", " ");}
	if (CTCWEBLINK.contains("'") ) {CTCWEBLINK = CTCWEBLINK.replace("'", " ");}
	if (SCRTITLE.contains("'") ) {SCRTITLE = SCRTITLE.replace("'", " ");}
	if (SCRWEBLINK.contains("'") ) {SCRWEBLINK = SCRWEBLINK.replace("'", " ");}
	if (RELATEDSCRNAME.contains("'")) {RELATEDSCRNAME = RELATEDSCRNAME.replace("'", " ");}
	
		
	// we use encoded values where we need to send them as url parameters.
	String encodedCTCHEADLINE = URLEncoder.encode(CTCHEADLINE,"UTF-8");
	String encodedTESTCASEHEADLINE = URLEncoder.encode(TESTCASEHEADLINE,"UTF-8");
	String encodedCTCWEBLINK = URLEncoder.encode(CTCWEBLINK,"UTF-8");

	String encodedSCRTITLE = URLEncoder.encode(SCRTITLE,"UTF-8");
	String encodedSCRWEBLINK = URLEncoder.encode(SCRWEBLINK,"UTF-8");
	String encodedRELATEDSCRNAME = URLEncoder.encode(RELATEDSCRNAME, "UTF-8");
	
	
	if ((isLoggedIn == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
		String authenticationType = this.getServletContext().getInitParameter("authenticationType");

			if (authenticationType.equals("database")){
				// this means that the user is not yet authenticated, so lets send him to the db log in page.
				
				
				%>
				<jsp:forward page="/jsp/WebSite/startPage.jsp?callBackURL=clearQuest&CTCID=<%=CTCID%>&TESTCASEID=<%=TESTCASEID%>&TESTCASEHEADLINE=<%=encodedTESTCASEHEADLINE%>&CTCHEADLINE=<%=encodedCTCHEADLINE%>&CTCWEBLINK=<%=encodedCTCWEBLINK%>&SCRID=<%=SCRID%>&SCRTITLE=<%=encodedSCRTITLE%>&SCRWEBLINK=<%=encodedSCRWEBLINK%>&RELATEDSCRID=<%=RELATEDSCRID%>&RELATEDSCRNAME=<%=encodedRELATEDSCRNAME%>"/>
				<% 
				return;
			}
			if (authenticationType.equals("ldap")){
				// this means that the user has already been authenticated against ldap, but his profile has not been loaded.
				%>
				<jsp:forward page="/servlet/UserAccountAction?action=signIn&callBackURL=clearQuest&CTCID=<%=CTCID%>&TESTCASEID=<%=TESTCASEID%>&TESTCASEHEADLINE=<%=encodedTESTCASEHEADLINE%>&CTCHEADLINE=<%=encodedCTCHEADLINE%>&CTCWEBLINK=<%=encodedCTCWEBLINK%>&SCRID=<%=SCRID%>&SCRTITLE=<%=encodedSCRTITLE%>&SCRWEBLINK=<%=encodedSCRWEBLINK%>&RELATEDSCRID=<%=RELATEDSCRID%>&RELATEDSCRNAME=<%=encodedRELATEDSCRNAME%>"/>
				<% 
				return;
			}
	}
	
	// at this point, the user has been authenticated and his profile has been loaded.
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (securityProfile == null){
		%>
		Please log into TraceCloud system and then come back to this page. 
		<%
		return;
	}
	
	String databaseType = this.getServletContext().getInitParameter("databaseType");

	
	User user = securityProfile.getUser();
	ArrayList projects = securityProfile.getProjectObjects();
	

	
%>
		
	<div class="wrapper box_theme_login_wrapper">
	
	<div  style="border-width: 2px; border-style: solid; border-color: lightblue; padding:15px">	
		
		<div class='level1Box'>
			<br>
			<br>
			<span class="ttitle">TraceCloud ClearQuest Integration</span> 
			<br>
			<br>
			
			
			<div>
				<span class="sectionHeadingText10"><b>Clear Quest Test Info</b></span> 
				<br>
				<br>
					<table class='paddedTable'>
						<% if ((CTCID != null) && (!(CTCID.equals("")))) { %>
							<tr>
								<td><span class='normalText'>Test Result ID</span></td>
								<td><span class='normalText'><%=CTCID %></span></td>
							</tr>
						<%}%>
						
						<% if ((CTCHEADLINE != null) && (!(CTCHEADLINE.equals("")))) { %>
							<tr>
								<td><span class='normalText'>Test Result Name</span></td>
								<td><span class='normalText'><%=CTCHEADLINE %></span></td>
							</tr>
						<%}%>
						
						<% if  ((TESTCASEID != null) && (!(TESTCASEID.equals(""))))  { %>
							<tr>
								<td><span class='normalText'>Test Case ID</span></td>
								<td><span class='normalText'><%=TESTCASEID %></span></td>
							</tr>	
						<%}%>
						
						<% if  ((CTCHEADLINE != null) && (!(CTCHEADLINE.equals(""))))  { %>
							<tr>
								<td><span class='normalText'>Test Case NAME</span></td>
								<td><span class='normalText'><%=TESTCASEHEADLINE %></span></td>
							</tr>	
						<%}%>
						
						<% if ((CTCWEBLINK != null) && (!(CTCWEBLINK.equals(""))))  { %>									
							<tr>
								<td><span class='normalText'>CQ URL</span></td>
								<td><span class='normalText'><%=CTCWEBLINK%></span></td>
							</tr>
						<%}%>




						<% if ((RELATEDSCRID != null) && (!(RELATEDSCRID.equals(""))))  { %>									
							<tr>
								<td><span class='normalText'>RELATEDSCRID</span></td>
								<td><span class='normalText'><%=RELATEDSCRID%></span></td>
							</tr>
						<%}%>
						<% if ((RELATEDSCRNAME != null) && (!(RELATEDSCRNAME.equals(""))))  { %>									
							<tr>
								<td><span class='normalText'>RELATEDSCRNAME</span></td>
								<td><span class='normalText'><%=RELATEDSCRNAME%></span></td>
							</tr>
						<%}%>



						<% if  ((SCRID != null) && (!(SCRID.equals(""))))  { %>
							<tr>
								<td><span class='normalText'>SCR ID</span></td>
								<td><span class='normalText'><%=SCRID %></span></td>
							</tr>	
						<%}%>
						
						<% if ((SCRTITLE != null) && (!(SCRTITLE.equals(""))))  { %>
							<tr>
								<td><span class='normalText'>SCR NAME</span></td>
								<td><span class='normalText'><%=SCRTITLE %></span></td>
							</tr>	
						<%}%>
						
						<% if ((SCRWEBLINK != null) && (!(SCRWEBLINK.equals(""))))  { %>									
							<tr>
								<td><span class='normalText'>SCR URL</span></td>
								<td><span class='normalText'><%=SCRWEBLINK%></span></td>
							</tr>
						<%}%>						
						
						
						
		
		
						
					</table>				
				<form method=post action='#'>
					<input type='hidden' name='CTCID' id='CTCID' value='<%=CTCID%>'>
					<input type='hidden' name='CTCHEADLINE' id='CTCHEADLINE' value='<%=CTCHEADLINE%>'>
					<input type='hidden' name='TESTCASEID' id='TESTCASEID' value='<%=TESTCASEID%>'>
					<input type='hidden' name='TESTCASEHEADLINE' id='TESTCASEHEADLINE' value='<%=TESTCASEHEADLINE%>'>
					<input type='hidden' name='CTCWEBLINK' id='CTCWEBLINK' value='<%=CTCWEBLINK%>'>
				
					<input type='hidden' name='RELATEDSCRID' id='RELATEDSCRID' value='<%=RELATEDSCRID%>'>
					<input type='hidden' name='RELATEDSCRNAME' id='RELATEDSCRNAME' value='<%=RELATEDSCRNAME%>'>
					
					<input type='hidden' name='SCRID' id='SCRID' value='<%=SCRID%>'>
					<input type='hidden' name='SCRTITLE' id='SCRTITLE' value='<%=SCRTITLE%>'>
					<input type='hidden' name='SCRWEBLINK' id='SCRWEBLINK' value='<%=SCRWEBLINK%>'>
					
					

					<br>
					<br>
					<span class="sectionHeadingText10"><b>Existing ClearQuest to TraceCloud Relationships</b></span> 
					<br>
					<br>				
					<div id='existingProxiesDiv'>
						<%@ include file="displayExistingProxies.jsp"%>
					</div>
					<div id='openExistingProxiesDiv' style='display:none;'>
						<a 	onclick='
							document.getElementById("existingProxiesDiv").style.display="block";
							document.getElementById("openExistingProxiesDiv").style.display="none";
						' href="#"> Show Change Impact Analysis</a>
					</div>
					
					
					
					
					<br>
					<br>
					<span class="sectionHeadingText10"><b>Create New ClearQuest to TraceCloud Relationships</b></span> 
					<br>
					<br>					
					<div id='newProxiesDiv'>
						<table class='paddedTable'>
							<tr>
								<td width='100px'>
									<span class='normalText'>Project</span>
								</td>
								<td>
									<span class='normalText'>
										<select id='projectId' onChange='displayFoldersInCQHome();'>
										<option value='-1'>Select A Project </option>	
										<%
										Iterator p = projects.iterator();
										while (p.hasNext()){
											Project project = (Project) p.next();
											%>
											<option value='<%=project.getProjectId()%>'>
											<%=project.getShortName()%> : <%=project.getProjectName() %>
											</option>
										<%
										}
										%>
										</select>
									</span>
								</td>
							</tr>
							<tr>
								<td colspan='2'>	
									<div id='foldersInCQDiv' style='display:none'></div>
									
								</td>
							</tr>
						</table>
					</div>
				</form>
		
				</div>	
			</div>				
		</div>
	</div>



</body></html>