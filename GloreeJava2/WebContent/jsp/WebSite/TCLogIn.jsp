<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xml:lang="en" xmlns="http://www.w3.org/1999/xhtml" lang="en"><head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">



	<!-- ^^^content104^^^ -->

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
<body >

		
	<div class="wrapper box_theme_login_wrapper">
	<jsp:include page="Common/TCToolbar.jsp" />	
	
	<div  style="border-width: 2px; border-style: solid; border-color: lightblue; padding:15px">	
		<div class="citation">
			"Our team loves to use Excel, however, Excel doesn't have collaboration. TraceCloud gives really nice integration with Excel and solves the collaboration problem."
			<span>&mdash; A happy customer</span>
		</div>
		<jsp:include page="Common/TCNextStepsAndSolutions.jsp" />
		
		
		
		<div>
			
		
		<!-- 
				<div class='alert alert-success'>
		
					<div class='bs-callout bs-callout-danger'>
						<span class='normalText'><b><font color='red'>
						We are in the process of replacing our SSL Certificate. 
						Please note that the system is in maintenance till 1:00 PM PST on Saturday  (09/24/2016). 
							<br>
							You may be occasionally forced to re-login in to the system during this period.
							</font>
						<b></span>
					</div>
				</div>
				
				-->
						<br>
						<br>
						<span class="ttitle">User Log In</span> 
						<br>
						<br>
						<br>
						
						<%
							//authorization
							// since we need authorization as well as authenticaiton we will use the 
							// security profile object.
							SecurityProfile userProjectsSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");

							if (userProjectsSecurityProfile == null){
								String testDrive = request.getParameter("testDrive");
								
						%>
								
							
							<div>
								
								<div>
										<form method="post" id="loginForm" action="https://<%=request.getServerName()%>/GloreeJava2/servlet/UserAccountAction">
										<input name="action" value="signIn" type="hidden">
										<table style="text-align: left;">
										<tbody>
											<%		
													String testDriveUserId = "";
													String testDrivePassword = "";
													if ((testDrive != null)){
														testDriveUserId = "trial@tracecloud.com";
														testDrivePassword = "trial";
													
													}
													if (this.getServletContext().getInitParameter("authenticationType").equals("ldap")) {	
											%> 
												<tr>
													<td class="normalTableCell">
														<span class="headingText"> User Id </span>
														<sup><span style="color: rgb(255, 0, 0);">*</span></sup>
													</td>
													<td class="normalTableCell">
															<input name="ldapUserId" size="20" maxlength="100" type="text"  style='width:200px;'>	   						
															<input name="emailId" size="20" maxlength="100" 
															type="hidden" 
															style='width:200px;'
															value="notApplicable" >
														</td>
												</tr>
											<%}
												else {%>
													<tr>
														<td class="normalTableCell">
														<span class="headingText"> Email Id </span>
														<sup><span style="color: rgb(255, 0, 0);">*</span></sup>
														</td>
														<td class="normalTableCell">
														<input name="emailId" value="<%=testDriveUserId%>" 
															size="20" maxlength="100" type="text" 
															style='width:200px;'>	   						
														<input name="ldapUserId"  size="20" 
														maxlength="100" type="hidden" value="notApplicable">
													</td>
													</tr>
											<%} %>	
											
											<tr>
												<td class="normalTableCell">
													<span class="headingText"> Password</span> 
													<sup><span style="color: rgb(255, 0, 0);">*</span></sup>
												</td>
												<td class="normalTableCell">
													<input name="password" value="<%=testDrivePassword%>" 
													size="20" maxlength="30"  type="password" style='width:200px;'
													onkeypress=" handleSignInkeyPress(event, this.form);">
												</td>
											</tr>
											<tr>
												<td class="normalTableCell" colspan="2" style="text-align: center;"> 
													<input name="Sign in" value="Sign in"  type="submit" class='btn btn-primary btn-sm'  style='width:100px;'>
												</td>
											</tr>
											<%
											if (this.getServletContext().getInitParameter("authenticationType").equals("database")) {	
											%> 		
											<tr>
												<td class="normalTableCell" colspan="2" > 
													<span class="headingText"> Forgot Your Password ? Click 
													<a href="/GloreeJava2/jsp/WebSite/TCResetPassword.jsp">
													here </a>. 
													<br>
												 	To Create a TraceCloud account click  
												 	<a href='/GloreeJava2/jsp/WebSite/TCCreateAnAccountForm.jsp'> here </a>.
													</span>
												</td>
											</tr>
											<%} %>
										</tbody>
										</table>
									</form>
								</div>
							</div>
						<%}
						else {
						%>
							<div id="tryItFreeDiv">
								<div class="menu-title">TraceCloud<br>
								</div>
								<div class="menu-item"><img src="/GloreeJava2/images/WebSite/arrow.gif"
									alt="arrow" align="left" hspace="10" vspace="5">
									<a
									href="https://<%=request.getServerName()%>/GloreeJava2/jsp/UserDashboard/userProjects.jsp">
									Return to Dashboard
									</a>
								</div>
								<div class="menu-item"><img src="/GloreeJava2/images/WebSite/arrow.gif"
									alt="arrow" align="left" hspace="10" vspace="5">
									<a
									href="https://<%=request.getServerName()%>/GloreeJava2/servlet/UserAccountAction?action=signOut">
									Log Out
									</a>
								</div>
							</div>
						<%}%>
		
					</div>
	
					<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>

</body></html>