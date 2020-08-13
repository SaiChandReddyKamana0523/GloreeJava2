<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>
<html xml:lang="en" xmlns="http://www.w3.org/1999/xhtml" lang="en"><head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">



	

	<title>TraceCloud - Online Requirements Management - Collaborate, Define, Manage and Deliver your Customer Requirements, Online</title>
    <meta name="description" content="Collaboration tools to define, manage and deliver your customer requirements on time and within budget. Significantly improves customer satisfaction ">
	<meta name="keywords" content="free requirements management, saas requirements management tool, online requirements management, doors, requisitepro, customer requirements, shared requirements, tl9000, project management, project requirements, agile, agile requirements management.">


	
	
	<link rel="stylesheet" href="/GloreeJava2/css/greeny.css" type="text/css" >
	<link rel="stylesheet" href="/GloreeJava2/css/common.css" type="text/css">
	<link rel="stylesheet" href="/GloreeJava2/css/sales_global.css" type="text/css">
	<link rel="stylesheet" href="/GloreeJava2/css/sales_home.css" type="text/css" media="screen">
	
	
	<!--  Bootstratp  JS and CSS files -->
	 <script src="/GloreeJava2/js/jquery-3.1.1.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap.min.js"></script>
	 <link href="/GloreeJava2/css/bootstrap.min.css" rel="stylesheet" media="screen">
	
	
	
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
	
	

</head>
<%
	//For LDAP type authentication, since the user is already authenticated by the time he comes to this page
	//lets forward him to the  User Account uti sign in.
	String authenticationType = this.getServletContext().getInitParameter("authenticationType");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	if ((authenticationType.equals("ldap")) && (securityProfile == null)){
%>
	<jsp:forward page="/servlet/UserAccountAction?action=signIn"/>
<%
	
	// authorization : since we are explicityly checking for and 
	// listing all the projects the user has access to
	// we are OK here. 
	}
%>
<body bgcolor="#FFFFFF">

		
		
 		<div class="wrapper box_theme_login_wrapper">
		<jsp:include page="Common/TCToolbar.jsp" />	
		

		
		
			<div>
			<iframe src='https://onedrive.live.com/embed?cid=DF8A460687C5ED54&resid=DF8A460687C5ED54%216054&authkey=&em=2&wdAr=1.7777777777777777' width='962px' height='691px' frameborder='0'>This is an embedded <a target='_blank' href='http://office.com'>Microsoft Office</a> presentation, powered by <a target='_blank' href='http://office.com/webapps'>Office Online</a>.</iframe>
			</div>





	
<div  style="border-width: 2px; border-style: solid; border-color: white; ">

	
		<table>
				
		<tr>
			<td   width='100%' align='center'>
					<table  >
						<tr>
							<td><img src="/GloreeJava2/jsp/WebSite/TCHome2/easyToUse.jpg" height='180px'></td>
						
							<td align='left'>
								
								<FORM METHOD="GET" ACTION="/GloreeJava2/jsp/WebSite/TCLogIn.jsp">
									<input type='hidden' name='testDrive' id='testDrive' value='yes'>
									<INPUT TYPE="submit" class='btn btn-danger btn-lg' VALUE="TEST DRIVE TRACECLOUD">
								</FORM>
								
							</td>
						</tr>
					</table>
			</td>
			
		</tr>	
		
		
		<tr><td colspan='3' width="1px" height="100%" style="background-color:#99CCFF"></td></tr>



		

		<tr>
			<td colspan='3'><div>
			<jsp:include page="TCScreenShots.jsp" />	
			</div></td>
		</tr>
		
		
	</table>
	


	
</div>	

	
	


		
		
	
			
	
		


</body></html>