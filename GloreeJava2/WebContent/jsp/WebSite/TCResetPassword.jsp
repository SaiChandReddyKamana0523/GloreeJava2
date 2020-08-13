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
		
		
			<%
				// authentication .
				// authorizaton
				// Ignore the security check, as this is the page where the creates an account .
				// 
			
				String passWordReset = (String) request.getAttribute("passWordReset");
				// means that the emailId was not available.
				
				String passWordResetMessage = "";
			    if ((passWordReset != null) && (passWordReset.equals("false"))){
			    	passWordResetMessage = "<div style='width:400px' class='alert alert-success'> " + 
			    		" <span class='normalText'> We are unable to locate an account with this First Name, " +
			    		" Last Name, Pet's Name and Email Id  " +  
			    		" Combination. Please check your values and try again.</span></div>";
			    }
			
				String emailId  = request.getParameter("emailId") ;
				if (emailId == null){
					emailId = "";
				}

							
			%>
		
		<div >
		<table >
			<tr>
				<td valign='top'>
				
				</td>
				<td>
					<div >			
						<table >
							<tr>
								<td align='left'>
									<%=passWordResetMessage %>
		
								<div id='createAnAccountForm' >
									<form method="post" id="createAccountForm" action="/GloreeJava2/servlet/UserAccountAction">
								
									<input type='hidden' name='action' value='resetPassWord'>		 
									<table  >
										<tr>
											<td class='normalTableCell' colspan="2">
												<span class='sectionHeadingText'>
												 Reset Password ...
												 </span>
											</td> 
										</tr>

													
										
										<tr> 
											<td class='normalTableCell'>
												<span class='headingText'> Email Id </span>
												<sup><span style="color: #ff0000;">*</span></sup>
											</td>
											<td class='normalTableCell'>
												<input type="text" name="emailId" size="30" maxlength="100" 
												value = '<%=emailId%>'>
											</td>
										</tr>
																	
										<tr> 
											<td></td>
											<td  class='normalTableCell'  >
												<input type='button' name='Reset Password' value='Reset Password'
												onClick='resetPassWord(this.form)'>
												<br>
												<span class='normalText'>
													If you need help, please 
													<a href="mailto:support@tracecloud.com?subject=Please help reset my password">
					Contact Us</a>
												
												</span>
											</td>
										</tr>
										
										
									</table>
									</form>
									<br><br><br><br><br>
									<br><br><br><br><br>
									<br><br><br><br><br>
									<br><br><br><br><br>
									<br><br><br><br><br>
									<br><br><br><br><br>
									<br><br><br><br><br>
									<br><br><br><br><br>
								</div>
							</td>
						</tr>
						</table>
					</div>
		
		

</body></html>