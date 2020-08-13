<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

	<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
	<html>
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>TraceCloud - SAAS Agile Scrum Requirements Management - Collaborate, Define, Manage and Deliver your Customer Requirements</title>
 	 <meta name="description" content="Collaboration tools to define, manage and deliver your customer requirements on time and within budget. Significantly improves customer satisfaction ">
	<meta name="keywords" content="free requirements management, saas requirements management tool, online requirements management, doors, requisitepro, customer requirements, shared requirements, tl9000, project management, project requirements, agile, agile requirements management.">

	
	
	<!-- Individual YUI CSS files--> 

	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/autocomplete/assets/skins/sam/autocomplete.css">	
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/reset-fonts-grids/reset-fonts-grids.css"> 
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/resize/assets/skins/sam/resize.css"> 
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/layout/assets/skins/sam/layout.css">
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/button/assets/skins/sam/button.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/menu/assets/skins/sam/menu.css"> 
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/treeview/assets/skins/sam/treeview.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/container/assets/skins/sam/container.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/editor/assets/skins/sam/simpleeditor.css" />
	
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/calendar/assets/skins/sam/calendar.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/editor/assets/skins/sam/editor.css" />
	
	



	<!-- Individual YUI JS files --> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/yahoo-dom-event/yahoo-dom-event.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/animation/animation-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/dragdrop/dragdrop-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/element/element-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/button/button-min.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/resize/resize-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/layout/layout-min.js"></script> 
	
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/treeview/treeview-min.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/container/container-min.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/container/container_core-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/menu/menu-min.js"></script> 
	
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/utilities/utilities.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/calendar/calendar-min.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/editor/simpleeditor-min.js"></script>

	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/datasource/datasource-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/autocomplete/autocomplete-min.js"></script> 

	
	<!-- Gloree JS and CSS files -->
	
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/css/common.css"> 
	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	
	<script src="/GloreeJava2/js/userAccount.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userDashboard.js?v=20200630"></script>\
	
	
	<!--  Bootstratp  JS and CSS files -->
	 <script src="/GloreeJava2/js/jquery-3.1.1.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap-tour-standalone.min.js"></script>
	
	 <link href="/GloreeJava2/css/bootstrap.min.css" rel="stylesheet" media="screen">
	 <link href="/GloreeJava2/css/bootstrap-tour-standalone.min.css" rel="stylesheet">
	 
	
	<!--  cdn for ckeditor 
	 <script src="https://cdn.ckeditor.com/4.6.2/standard/ckeditor.js"></script>
	 -->
	 <script src="https://cdn.ckeditor.com/4.6.2/full-all/ckeditor.js"></script>
    
	
	</head>
	
	<body class=" yui-skin-sam" style='background-color:white'> 

	
	
<%
	// No authentication for this. Any one can come and create requirements
	// we just check for the correct auth code
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, "mySQL");
	int webFormId = Integer.parseInt(request.getParameter("webFormId"));
	WebForm webForm = new WebForm(webFormId);

	String url = ProjectUtil.getURL(request,requirementId,"requirement");
	String webFormURL = webForm.getAccessURL(request);

%>

			<div class='alert alert-into'>
			
					Requirement <a target='_blank' href='<%=url%>'><%=requirement.getRequirementFullTag()%> </a> has been created 

			
					<br>To create another <%=requirement.getRequirementTypeName() %> <a href='<%=webFormURL%>'> Click Here </a>
			
					<br>To attach a file  <a href='#' onclick='addRequirementAttachmentFormOpen(<%=requirement.getRequirementId()%>,<%=requirement.getFolderId()%>,<%=webFormId%>)'> Click Here </a>
			
				<br><br>
				
				<div id='requirementPromptDiv'></div>
			</div>			
		
		
			
	

</body>
</html>