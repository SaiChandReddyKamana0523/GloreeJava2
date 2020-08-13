<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
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

		<% String authType = this.getServletContext().getInitParameter("authenticationType");
		%>
	<div class="wrapper box_theme_login_wrapper">
	<jsp:include page="Common/TCToolbar.jsp" />	
	
	<br>
	<div  style="border-width: 2px; border-style: solid; border-color: lightblue; padding:15px text-align:center; vertical-align:middle">	
	
		<%
			// authentication .
			// authorizaton
			// Ignore the security check, as this is the page where the creates an account .
			// 
		
			String emailIdNotAvailable = (String) request.getAttribute("emailIdNotAvailable");
			// means that the emailId was not available.
			
			String accountCreationMessage = "";
			if (emailIdNotAvailable != null) {
				accountCreationMessage = "<div id='emailIdNotAvailableMessage' class='alert alert-success'> " + 
					" An account already exists with this Email Id. " +  
					" Please use a different email address. </div>";
			}
		
		
		%>
		
	
		<div style=' position:relative; left:250px; top:100px' >
							
			<table >
				<tr>
					<td align='left'>
						<%=accountCreationMessage %>
					<div id='createAnAccountForm' >
						<form method="post" id="createAccountForm" action="/GloreeJava2/servlet/UserAccountAction">
							<%
							if (this.getServletContext().getInitParameter("authenticationType").equals("ldap")  ){
									String emailId  = "";
									
									String ldapAuthenticationPattern = this.getServletContext().getInitParameter("ldapAuthenticationPattern");
									if (ldapAuthenticationPattern.equals("emailId")){
										emailId = request.getRemoteUser();
									}
									if (ldapAuthenticationPattern.equals("userId")){
										// this means that the user must have entered the userId (like aditya) in the  user id section
										// there fore, we must add the emailDomain to get the full email address
										String ldapAuthenticationEmailDomain = this.getServletContext().getInitParameter("ldapAuthenticationEmailDomain");
										emailId = request.getRemoteUser() + ldapAuthenticationEmailDomain;
									}
									
									%>
									<input type="hidden" name="emailId" size="30" maxlength="100" value='<%=emailId %>' >
						<%} %>
					
					
						<input type='hidden' name='petsName' id ='petsName' value='hiddenPetsNameVariable'>		 
						<input type='hidden' name='action' value='createUserAccount'>		 
						<input type="hidden" name="ldapUserId" size="30" maxlength="100"  value="notApplicable">
						
					
						<div class="panel panel-info"> 
							<div class="panel-heading " style='text-align:center'> 
								<h class="panel-title"><b> Creating an account...</b></h3>
								
							</div>
							<div class="panel-body"> 
						<table  class='table' style='text-align:center' >
							<tr> 
								<td style=' border-top: none'>
									<input type="text"   size='25'  class="form-control " name="firstName"  placeholder='First Name'>
								</td>
								<td style=' border-top: none'>
									<input type="text"   size='25' class="form-control " name="lastName"   placeholder='Last Name' >
								</td>
							</tr>	
							<tr>
								<td style=' border-top: none'>
								<%if (this.getServletContext().getInitParameter("authenticationType").equals("database")){	
								%> 					
												<input type="text"  size='25' name="emailId"  class="form-control " placeholder='Email'>
								<%}
								%>
								</td>
								<td  style=' border-top: none'>
									<%
										if (this.getServletContext().getInitParameter("authenticationType").equals("database")) {	
										// the following options do not show up for onSite installation
									%>			
									<input type="text"  size='25'  class="form-control "  id="heardAboutTraceCloud" name="heardAboutTraceCloud" placeholder="How did you hear about us">
									<% 
										}
										else { 
									%>
										<input type="hidden" name="heardAboutTraceCloud" size="50"  value="notApplicable">
									<% 
										} 
									%>
							</td>
							
							
							</tr>
							<%
							if (this.getServletContext().getInitParameter("authenticationType").equals("database")){	
							%> 		
							
								<tr> 
									<td style=' border-top: none'>
										<input type="password"  size='25'  name="password1"  class="form-control "  placeholder='Password' >
									</td>
								
									<td  style=' border-top: none'>
										
										<input type="password"  size='25'  name="password2" class="form-control " placeholder='Re-Enter Password' > 
									</td>
								</tr>
						
							<%}
							else {%>
								<input type="hidden" name="password1" size="20" maxlength="30" value="notApplicable" >
								<input type="hidden" name="password2" size="20" maxlength="30" value="notApplicable">
								<input type="hidden" name="petsName" size="20" maxlength="30" value="notApplicable">
							<%} %>




			



							<tr> 
								<td  style=' border-top: none' colspan='2' style="text-align: center;">
									<input type='button' class='btn btn-large btn-primary' style='width:250px;height:40px'
									name='Create My Account' id="createMyAccountButton" value='Create My Account'
									
									onClick='createAnAccount(this.form)'>
									<%
										if (this.getServletContext().getInitParameter("installationType").equals("saas")) {	
										// the following options do not show up for onSite installation
									%> 		
									
									<div id="cloneProjectMessageDiv" class="actionPrompt" style="display:none;">
										<span class='headingText'>
										<br><br>
										Thank you for creating an account with the TraceCloud system.
										<br></br><br></br>
										To help you come up to speed with all the capabilities of TraceCloud, we 
										are creating a Sample Project for you. This project will have 
										Requirment Types, Requirements, Reports and Word templates that will show 
										you how a typical TraceCloud project can be used .
										<br></br><br></br>
										Please note that it takes a few minutes to create the Sample Project.
										<br><br>
										Once the project is created, you will be asked to log in, and you should find
										your project in your Dashboard.
										<br><br>
										</span>
									</div>
									<%}
									else {%>
										<div id="cloneProjectMessageDiv" class="actionPrompt" style="display:none;">
										<span class='headingText'>
										<br><br>
										Thank you for creating an account with the TraceCloud system.
										<br><br>
										Once the project is created, you will be asked to log in, and you should find
										your project in your Dashboard.
										<br><br>
										</span>
									</div>													
									
									<%} %>													
								</td>
							</tr>
							
						</table>						

							</div>
						</div>
						
						
						

						</form>
						
					</div>
				</td>
			</tr>
		</table>
		<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
					
	</div>

	


</body></html>