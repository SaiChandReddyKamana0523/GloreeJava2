<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.net.*" %>


<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
  <title>TraceCloud.com</title>
  	
  	<link href="/GloreeJava2/css/greeny.css" rel="stylesheet" type="text/css"></link>
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/css/common.css"> </link>
	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userAccount.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userDashboard.js?v=20200630"></script>  
</head>
<body class="yui-skin-sam">
	<div id="tot">
		
			<%
				// authentication .
				// authorizaton
				// Ignore the security check, as this is the page where the user logs in.
				// 
				String accountCreated = (String) request.getAttribute("accountCreated");
				// if accountCreated is not null, it means that we just created a user account 
				// and asking him to log in now.
				
				String accountCreationMessage = "";
			    if (accountCreated != null) {
			    	accountCreationMessage = "<div id='accountCreatedMessage' style='width:400px' class='alert alert-success'> " + 
			    		"<span class='normalText'>Congratulations. Your account has been created.</span>" +  
			    		"</div>";
			    }
			
				String signInFailed = (String) request.getAttribute("signInFailed");
				// if signInFailed is not null, it means that the user failed to signIn 
				// we need to give him / her an alert and asking him / her to log in again.
				
			    if (signInFailed != null) {
			    	accountCreationMessage = "<div id='signInFailedMessage' style='width:400px' class='alert alert-success'> " + 
			    		" <span class='normalText'>Your email / password did not match." +  
			    		" Please try again. </span></div>";
			    }
				
				if ((accountCreated == null) && (signInFailed == null)){
			    	accountCreationMessage = "<div id='signInFailedMessage' style='width:400px' class='alert alert-success'> " + 
		    		" <span class='normalText'> Your Session has been inactive for sometime. For security purposes, we have Signed you out of the system." +  
		    		" Please Sign in again. </span> </div>";
				}
				
				String callBackURL = request.getParameter("callBackURL");
		    	if (callBackURL == null){
		    		callBackURL = "";
		    	}
		    	
		    	// The following parameters are for ClearQuest Integration users
		    	String CTCID = request.getParameter("CTCID");
		    	if (CTCID == null){
		    		CTCID = "";
		    	}
		    	String TESTCASEID = request.getParameter("TESTCASEID");
		    	if (TESTCASEID == null){
		    		TESTCASEID = "";
		    	}
		    	String CTCHEADLINE = request.getParameter("CTCHEADLINE");
		    	if (CTCHEADLINE == null){
		    		CTCHEADLINE = "";
		    	}
		    	String TESTCASEHEADLINE = request.getParameter("TESTCASEHEADLINE");
		    	if (TESTCASEHEADLINE == null){
		    		TESTCASEHEADLINE = "";
		    	}
		    	String CTCWEBLINK = request.getParameter("CTCWEBLINK");
		    	if (CTCWEBLINK == null){
		    		CTCWEBLINK = "";
		    	}
		    			 
		    	String RELATEDSCRID = request.getParameter("RELATEDSCRID");
		    	if (RELATEDSCRID == null){
		    		RELATEDSCRID = "";
		    	}
		    	
		    	String RELATEDSCRNAME = request.getParameter("RELATEDSCRNAME");
		    	if (RELATEDSCRNAME == null){
		    		RELATEDSCRNAME = "";
		    	}
		    	
		    
		    	String SCRID = request.getParameter("SCRID");
		    	if (SCRID == null){
		    		SCRID = "";
		    	}
		    	String SCRTITLE = request.getParameter("SCRTITLE");
		    	if (SCRTITLE == null){
		    		SCRTITLE = "";
		    	}
		    	String SCRWEBLINK = request.getParameter("SCRWEBLINK");
		    	if (SCRWEBLINK == null){
		    		SCRWEBLINK = "";
		    	}	

		    	// when we send values as hidden params, they will fail if there isa  ' in the name. so lets strip it out.
		    	if (CTCHEADLINE.contains("'") ) {CTCHEADLINE = CTCHEADLINE.replace("'", " ");}
		    	if (TESTCASEHEADLINE.contains("'") ) {TESTCASEHEADLINE = TESTCASEHEADLINE.replace("'", " ");}
		    	if (CTCWEBLINK.contains("'") ) {CTCWEBLINK = CTCWEBLINK.replace("'", " ");}
		    	if (SCRTITLE.contains("'") ) {SCRTITLE = SCRTITLE.replace("'", " ");}
		    	if (SCRWEBLINK.contains("'") ) {SCRWEBLINK = SCRWEBLINK.replace("'", " ");}
		    	if (RELATEDSCRNAME.contains("'")) {RELATEDSCRNAME = RELATEDSCRNAME.replace("'", " ");}
		    	
		    	
		    	
		    	
		    	
		    	// The following parameters are for Jira Integration users
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
				
				
				// authentication only
				String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
				
				
				
				if (JID == null ) {JID = "";}
				if (JPROJECT == null ) {JPROJECT = "";}
				
				if (JTYPE == null ) {JTYPE = "";}
				if (JPRIORITY == null ) {JPRIORITY = "";}
				if (JLABELS == null) {JLABELS = "";}
				if (JSTATUS == null ) {JSTATUS = "";}
				if (JRESOLUTION == null ) {JRESOLUTION = "";}
				if (JAFFECTSV == null ) {JAFFECTSV = "";}
				if (JFIXV == null ) {JFIXV = "";}
				if (JASSIGNEE == null ) {JASSIGNEE = "";}
				if (JREPORTER == null ) {JREPORTER = "";}
				if (JCREATED == null ) {JCREATED = "";}
				if (JUPDATED == null ) {JUPDATED = "";}
				
				if (JURL == null ) {JURL = "";}
				if (JTITLE == null ) {JTITLE = "";}
				if (JDESCRIPTION == null ) {JDESCRIPTION = "";}

		    	
		    	int requirementId = 0;
		    	try {
		    		requirementId = Integer.parseInt(request.getParameter("requirementId"));
		    	}
		    	catch (Exception e){
		    		
		    	}
		    	String approvalAction = request.getParameter("approvalAction");
		    %>
		
		<div id="corp" align='center' style='background:#EDEDFA; height:800px'>
			<div id="userProjectstDiv" class="level1Box" style='width:600px; height:700px'>
				<table align="center" width="350" class='paddedTable'>
				  <tbody>
				  	<tr>
				  		<td class='normalTableCell'>
					  		<div style='float:right'>
								<a href='/GloreeJava2/jsp/WebSite/TCHome.jsp'>Back to Home Page</a>
							</div>				      
				  		</td>
				  	</tr>
				    <tr>
				      <td class="normalTableCell">
				      
				      <div id="createRequirementDiv" class="level1Box">
						
				      	<%=accountCreationMessage %>
				      <form method="post" id="loginForm" 
				      	action="https://<%=request.getServerName()%>/GloreeJava2/servlet/UserAccountAction">
				      	
				      	
				      	
				      	<input type='hidden' name='action' value='signIn'>
				      	<input type='hidden' name='callBackURL' id='callBackURL' value='<%=callBackURL%>'>
				      	
				      	
				      	<!--  following values are for CQ Integration  -->
				      	<input type='hidden' name='CTCID' id='CTCID' value='<%=CTCID%>'>
				      	<input type='hidden' name='TESTCASEID' id='TESTCASEID' value='<%=TESTCASEID%>'>
				      	<input type='hidden' name='CTCHEADLINE' id='CTCHEADLINE' value='<%=CTCHEADLINE%>'>
				      	<input type='hidden' name='TESTCASEHEADLINE' id='TESTCASEHEADLINE' value='<%=TESTCASEHEADLINE%>'>
				      	<input type='hidden' name='CTCWEBLINK' id='CTCWEBLINK' value='<%=CTCWEBLINK%>'>
				      	
				      	<input type='hidden' name='RELATEDSCRID' id='RELATEDSCRID' value='<%=RELATEDSCRID%>'>
				      	<input type='hidden' name='RELATEDSCRNAME' id='RELATEDSCRNAME' value='<%=RELATEDSCRNAME%>'>
				      	
				      	
				      	<input type='hidden' name='SCRID' id='SCRID' value='<%=SCRID%>'>
				      	<input type='hidden' name='SCRTITLE' id='SCRTITLE' value='<%=SCRTITLE%>'>
				      	<input type='hidden' name='SCRWEBLINK' id='SCRWEBLINK' value='<%=SCRWEBLINK%>'>
				      	
				      	
				      	
				      	<!--  following values are for Jira Integration  -->
					      		
						<input type='hidden' name='JID' id='JID' value='<%=JID%>'>
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
						
					      	
				      	
				      	
				      	<input type='hidden' name='requirementId' id='requirementId' value='<%=requirementId%>'>
				      	<input type='hidden' name='approvalAction' id='approvalAction' value='<%=approvalAction%>'>
				      	
				      	
				      	
				        <table style="width: 350px; text-align: left;  height: 82px;">
				          <tbody>
				            <tr>
				              <td class="normalTableCell" colspan="2"> 
				              	<span class="sectionHeadingText"> Sign in to your TraceCloud Account </span> 
				              </td>
				            </tr>
				            	<%if (this.getServletContext().getInitParameter("authenticationType").equals("database"))
									{	
								%>
									<input name="ldapUserId" size="30" maxlength="100" value="notApplicable" type="hidden">
									<tr>
					            	  <td class="normalTableCell">
										<span class="headingText"> User Id </span>
										<sup><span style="color: #ff0000;">*</span></sup>
						              </td>
						              <td class="normalTableCell"> 
						              	<input name="emailId" size="30" maxlength="100" 
						              	type="text"
						              	style='width:200px;'
						              	> 
						              </td>
						             </tr>
				            		
								<%}
								else {%>
									<input name="emailId" size="30" maxlength="100" value="notApplicable" type="hidden">
									<tr>
					            	  <td class="normalTableCell">
										<span class="headingText"> Email Id </span>
										<sup><span style="color: #ff0000;">*</span></sup>
						              </td>
						              <td class="normalTableCell"> 
						              	<input name="ldapUser" size="30" maxlength="100" type="text"
						              	style='width:200px;'> 
						              </td>
						             </tr>
				            
								<%} %> 		
					           
				            <tr>
				              <td class="normalTableCell">
				              	<span class="headingText"> Password </span>
				              	<sup><span style="color: #ff0000;">*</span></sup>
				              </td>
				              <td class="normalTableCell"> 
				              	<input name="password" size="20" maxlength="30" 
				              	type="password"
				              	style='width:200px;'> 
				              </td>
				            </tr>
				            <tr>
				              <td class="normalTableCell" colspan="2" style="text-align: center;"> 
							  	<input name="Sign in" value="Sign in"  type="submit">
							  </td>
				            </tr>
				            <%if (this.getServletContext().getInitParameter("installationType").equals("saas"))
								{	
							%>
				            <tr>
				              <td class="normalTableCell" colspan="2"> 
				              	<span class="normalText"> 
								 	Forgot Your Password ? Click 
								 	<a href='/GloreeJava2/jsp/WebSite/TCResetPassword.jsp'> here </a>.
								 	<br>
								 	To Create a TaceCloud account click  
								 	<a href='/GloreeJava2/jsp/WebSite/TCCreateAnAccountForm.jsp'> here </a>.
								</span> 
							  </td>
				            </tr>
				            <%} %>
				            
				          </tbody>
				        </table>
				      </form>
				      </div>
				      </td>
				    </tr>
				  </tbody>
				</table>
			</div>
		</div>
		<img src="/GloreeJava2/images/spacer.png" border="0" onload='closeLayout();'>
		
	</div>
</body>
</html>
