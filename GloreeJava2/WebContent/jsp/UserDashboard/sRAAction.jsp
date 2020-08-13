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

	 <script src="/GloreeJava2/js/jquery-3.1.1.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap-tour-standalone.min.js"></script>
	
	 <link href="/GloreeJava2/css/bootstrap.min.css" rel="stylesheet" media="screen">
	 <link href="/GloreeJava2/css/bootstrap-tour-standalone.min.css" rel="stylesheet">


<!-- Get the list of my projects by calling the util. -->


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
	
	
</head>
<body  >


<%
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
	
	String mailHost = this.getServletContext().getInitParameter("mailHost");
	String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
	String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
	String smtpPort = this.getServletContext().getInitParameter("smtpPort");
	String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
	String emailUserId = this.getServletContext().getInitParameter("emailUserId");
	String emailPassword = this.getServletContext().getInitParameter("emailPassword");
	
	
	User user = userProjectsSecurityProfile.getUser();
		
	if (user.getEmailId().equals("nathan@tracecloud.com")){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;

		String action =request.getParameter("action");
		int marketingUserId = Integer.parseInt(request.getParameter("marketingUserId"));
		MarketingUser mUser = new MarketingUser(marketingUserId);
		if (action.equals("setHelloCompleted")){
			mUser.setHello("completed");
			%>
			Completed
			<%
		}
		if (action.equals("sendHelloEmail")){
			// lets send the hello email
			String toUser = request.getParameter("to");
			String subject = request.getParameter("subject");
			String messageBody = request.getParameter("body");
			messageBody = messageBody.replace("\n", "<br>");
			
			
			// lets send the email out to the toEmailId;
			ArrayList<String> to = new ArrayList<String>();
			to.add(toUser);
			ArrayList<String> cc = new ArrayList<String>();
			cc.add("nathan@tracecloud.com");
			MessagePacket mP = new MessagePacket(to, cc, subject, messageBody,"");
			EmailUtil.email(mP , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
			
			
			// set hello to completed
			mUser.setHello("completed");
			%>
			Email Sent to <%=to %> 
			
			
			<%
		}
		
		if (action.equals("deleteProject")){
			int projectId = Integer.parseInt(request.getParameter("projectId"));
			String message = ProjectUtil.deleteSampleProject(projectId);
			%>
			<%=message %>
			<%
		}
		
		
		if (action.equals("setNathanHelloCompleted")){
			mUser.setNeedHelp("completed");
			%>
			Completed
			<%
		}
		
		if (action.equals("sendNathanHelloEmail")){

			// lets send the hello email
			String toUser = request.getParameter("to");
			String subject = request.getParameter("subject");
			String messageBody = request.getParameter("body");
			messageBody = messageBody.replace("\n", "<br>");
			
			
			// lets send the email out to the toEmailId;
			ArrayList<String> to = new ArrayList<String>();
			to.add(toUser);
			ArrayList<String> cc = new ArrayList<String>();
			
			MessagePacket mP = new MessagePacket(to, cc, subject, messageBody,"");

			
			EmailUtil.email(mP , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, "nathan@tracecloud.com", this.getServletContext().getInitParameter("testPd") + "me2826");
			
			
			// set hello to completed
			mUser.setHello("completed");
			%>
			Email Sent to <%=to %> 
			
			
			<%
		}
		
		
	}
	else {
		%>
			This is restricted area. Only certain users can get to this page
		<%
	}
			
%>




</body>
</html>	














 