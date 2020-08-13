<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->    
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="java.sql.Date" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>




<%

	


	


	String serverName = request.getServerName();
	SecurityProfile userProfileSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (userProfileSecurityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%
	
	
	}
	
	
	userProfileSecurityProfile = new SecurityProfile(userProfileSecurityProfile.getUser().getUserId(),this.getServletContext().getInitParameter("databaseType"));
	session.setAttribute("securityProfile",userProfileSecurityProfile );
	 
	User user = userProfileSecurityProfile.getUser();
	
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

		<div class='alert alert-info'>ServiceNow Word Generator</div>
		<br></br>
		
		
		<div class='alert alert-info'>
		<table  class='table'>
		
			<tr><td colspan='2'> 
			
			<input type='button' class='btn btn-primary btn-xs' value='Create New Template' 
			onclick='createNewSNTemplateForm()'>  </input> 
			&nbsp;&nbsp;
			<input type='button' class='btn btn-primary btn-xs' value='Create New Data File' 
			onclick='createNewSNDataFileForm()'>  </input> 
			&nbsp;&nbsp;
			<input type='button' class='btn btn-primary btn-xs' value='Show Existing Templates' 
			onclick='getSNTemplates()'>  </input> 
			&nbsp;&nbsp;
			<input type='button' class='btn btn-primary btn-xs' value='Excel Reports' 
			onclick='showSNExcelReports()'>  </input> 
			
			</td></tr>
		</table>
		</div>
		
		<br></br><br></br>
		<div class='alert alert-info' id='sNTemplatesDiv'></div>
		<jsp:include page="/jsp/WebSite/Common/footer.jsp" />
	</div>
</body>
</html>	



















  