<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->    
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="java.sql.Date" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>



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
	
	
	
<!-- Get the list of my projects by calling the util. -->

<%

	String serverName = request.getServerName();


	//authorization
	// since we need authorization as well as authenticaiton we will use the 
	// security profile object.
	SecurityProfile userProfileSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	if (userProfileSecurityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%
	
	// authorization : since we are explicityly checking for and 
	// listing all the projects the user has access to
	// we are OK here. 
	}
	
	
	// there are times when the security profile could have changed due to the user's license management actions
	// so lets refresh his / her profile.
	userProfileSecurityProfile = new SecurityProfile(userProfileSecurityProfile.getUser().getUserId(),this.getServletContext().getInitParameter("databaseType"));
	session.setAttribute("securityProfile",userProfileSecurityProfile );
	 
	User user = userProfileSecurityProfile.getUser();
	Organization organization = new Organization(user.getMyOwnedOrganization());
	ArrayList sponsoredProjects = organization.getSponsoredProjects();

	
	String prefHideProjects = user.getPrefHideProjects();
	
	String prefHealthCheckDays = user.getPrefHealthCheckDays();
	String prefHealthCheckHideProjects = user.getPrefHealthCheckHideProjects();
			
	String emailIdNotAvailable = (String) request.getAttribute("emailIdNotAvailable");
	// means that the emailId was not available.
	String accountCreationMessage = "";
    if (emailIdNotAvailable != null) {
    	accountCreationMessage = "<tr><td><div id='emailIdNotAvailableMessage' style='width:400px' class='alert alert-danger'> " + 
    		" An account already exists with this Email Id. " +  
    		" Please use a different email address. </div></td></tr>";
    }	
    
    String profileUpdated = (String) request.getAttribute("profileUpdated");
	// means that the emailId was not available.
	
    if (profileUpdated != null) {
    	accountCreationMessage = "<tr><td><div id='emailIdNotAvailableMessage' style='width:400px' class='alert alert-success'> " + 
    		" <span class='normalText'> Your Profile has been successfully updated. </span> </div></td></tr>";
    }
	
    String accessDisabled = "";
    if ((user.getUserType() != null) && (user.getUserType().equals("expired"))){
    	accessDisabled = "DISABLED";
    }
    
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
									<table class='paddedTable' align='left' width='640px'>
										<tr>
											<td id='projectDasnboardTD' align='center' valign='center'  width='150' height='50'
													class = 'nonFocusTab'
													style='cursor:pointer'
													onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/UserDashboard/myTasks.jsp";'
													>
													<span class='normalText' style='color:white'>
														My Tasks
													</span>
											</td>
											
											
											<td id='projectDasnboardTD' align='center' valign='center'  width='150' height='50'
													class = 'nonFocusTab'
													style='cursor:pointer'
													onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/UserDashboard/userProjects.jsp";'
													>
												<span class='normalText' style='color:white'>
													My Projects
												</span>
											</td>
											
											
											
											
											
											
											<td id='accountProfileTD' align='center' valign='center'  width='150' height='50'
													class = 'focusTab'>
												<span class='normalText'> 
													<font color='gray'>	Account Profile </font>
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
													class = 'nonFocusTab'
													style='cursor:pointer'
													onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/UserDashboard/userOrganization.jsp";'
													>
													<span class='normalText' style='color:white'> 
														 License Management
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
													class = 'nonFocusTab'
													style='cursor:pointer'
													onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/UserDashboard/siteMetrics.jsp";'
													>
												<span class='normalText' style='color:white'> 
													Site Metrics
												</span>
											</td>
											<%	}%> 
																		
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<div id="userDashboardDiv" class="level1Box">
										<table width='100%' class='paddedTable'>
											<tr>
												<td>
													<table width='100%' cellspacing='0'>
													<tr>
														<td align='left' bgcolor='#99CCFF'>				
															<span class='subSectionHeadingText'>
															Your Account Details
															</span>
														</td>
														<td align='right' bgcolor='#99CCFF'>
																<a class='toolbar' href="/GloreeJava2/servlet/UserAccountAction?action=signOut"	 >
																Log out</a>
																&nbsp;&nbsp;&nbsp;&nbsp;
																<a class='toolbar' href='http://<%=request.getServerName()%>/GloreeJava2/documentation/help/userProfileDashboard.htm'
																target='_blank'> Help ?</a>
														</td>
													</tr>							
													</table>
												</td>
											</tr>							
											<%=accountCreationMessage %>
											<tr>
												<td>
													<div class='level1Box'>
														<form method="post" id="createAccountForm" action="/GloreeJava2/servlet/UserAccountAction">						
														<input type='hidden' name='action' id='action' value='updateUserProfile'>
														<input type="hidden" name="ldapUserId" id="ldapUserId" value="notApplicable">
														<table class='paddedTable'>
															<tr class='success'>
																<td class='normalTableCell' align='left' colspan="2">
																	<div class="alert alert-info">Profile Info</div>
																</td> 
															</tr>
															<tr> 
																<td class='normalTableCell' align='left'>
																	<span class='headingText'> First Name</span>
																	<sup><span style="color: #ff0000;">*</span></sup>
																</td>
																<td class='normalTableCell' align='left'>
																	<input type="text"  name="firstName" size="30" maxlength="100"
																	value='<%=user.getFirstName() %>' >
																</td>
															</tr>
															<tr> 
																<td class='normalTableCell' align='left'>
																	<span class='headingText'> Last Name</span>
																	<sup><span style="color: #ff0000;">*</span></sup>
																</td>
																<td class='normalTableCell' align='left'>
																	<input type="text" name="lastName" size="30" maxlength="100" 
																	value='<%=user.getLastName() %>'>
																</td>
															</tr>						
															<tr> 
																<td class='normalTableCell' align='left'>
																	<span class='headingText'> Email Id </span>
																	<sup><span style="color: #ff0000;">*</span></sup>
																</td>
																<td class='normalTableCell' align='left'>
																	<input type="text" name="emailId" size="30" maxlength="100" 
																	value='<%=user.getEmailId()%>'>
																</td>
															</tr>
															<tr> 
																<td class='normalTableCell' align='left'>
																	<span class='headingText'> Password </span>
																	<sup><span style="color: #ff0000;">*</span></sup>
																</td>
																<td class='normalTableCell' align='left'>
																	<input type="password" name="password1" size="20" maxlength="30" >
																</td>
															</tr>
															<tr> 
																<td class='normalTableCell' align='left'>
																	<span class='headingText'>Reenter Password </span>
																	<sup><span style="color: #ff0000;">*</span></sup>
																</td>
																<td class='normalTableCell' align='left'>
																	<input type="password" name="password2" size="20" maxlength="30" >
																</td>
															</tr>
															<tr> 
																<td class='normalTableCell' align='left' colspan='2' align='left'>
																	&nbsp;
																</td>
															</tr>
																
															<tr> 
																<td class='normalTableCell' align='left'>
																	<span class='headingText'>Your Favourite Pet's Name</span>
																	<sup><span style="color: #ff0000;">*</span></sup>
																</td>
																<td class='normalTableCell' align='left'>
																	<input type="text" name="petsName" size="20" maxlength="30">
																</td>
															</tr>
															<tr> 
																<td class='normalTableCell' align='left' colspan='2' align='left'>
																	<span class='headingText'>If you forget your password, we can use this to verify you</span>
																</td>
															</tr>
															<tr> 
																<!--  since we have the same fields as createAnAccount
																 we use the same javascript method to validate the fields.
																 // do not be confused by the JS Method name. -->
																<td  class='normalTableCell' colspan='2' style="text-align: center;">
																	<input type='button' class='btn btn-primary btn-sm'  name='Update My Profile' value='Update My Profile'
																	id="createMyAccountButton"  onClick='createAnAccount(this.form)'>
																</td>
															</tr>
														</table>
														</form>
													</div>
												</td>
											</tr>
											
											<tr>
												<td><hr></hr></td>
											</tr>
											
											<tr>
												<td>
													<div class='level1Box'>
															<form method="post" id="emailPreferencesForm" action="/GloreeJava2/servlet/UserAccountAction">
															<input type='hidden' name='action' id='action' value='emailPreferences'>
															<input type='hidden' name='prefHealthCheckDaysHidden' id='prefHealthCheckDaysHidden' value=''>
															<input type='hidden' name='prefHealthCheckHideProjectsHidden' id='prefHealthCheckHideProjectsHidden' value=''></input>
																
																<table class='paddedTable'>
																	<tr>
																		<td align='left'colspan='2' class='normalTableCell' colspan="2">
																			<div class="alert alert-info">Email Preferences</div>
																		</td>  
																	</tr>
																	<tr>
																		<td>
																		<span class="normalText">Send TraceCloud Status Reports only on 
																		<br>(Ctrl+Click to select / deselect)
																		</span>
																		</td>
																		<td>
																		<span class="normalText">
																			<select name="prefHealthCheckDays" id="prefHealthCheckDays" multiple="" size="7">
																				<%if (prefHealthCheckDays.contains("Monday")) {%>
																					<option value="Monday" SELECTED>Monday</option>
																				<% }
																				else { %>
																					<option value="Monday" >Monday</option>
																				<%} %>
																				
																				<%if (prefHealthCheckDays.contains("Tuesday")) {%>
																					<option value="Tuesday" SELECTED>Tuesday</option>
																				<% }
																				else { %>
																					<option value="Tuesday">Tuesday</option>
																				<%} %>
																				
																				
																				<%if (prefHealthCheckDays.contains("Wednesday")) {%>
																					<option value="Wednesday" SELECTED >Wednesday</option>
																				<% }
																				else { %>
																					<option value="Wednesday">Wednesday</option>
																				<%} %>
																				
																				<%if (prefHealthCheckDays.contains("Thursday")) {%>
																					<option value="Thursday" SELECTED>Thursday</option>
																				<% }
																				else { %>
																					<option value="Thursday">Thursday</option>
																				<%} %>
																				
																				
																				<%if (prefHealthCheckDays.contains("Friday")) {%>
																					<option value="Friday" SELECTED>Friday</option>
																				<% }
																				else { %>
																					<option value="Friday">Friday</option>
																				<%} %>
																				
																				<%if (prefHealthCheckDays.contains("Saturday")) {%>
																					<option value="Saturday" SELECTED>Saturday</option>
																				<% }
																				else { %>
																					<option value="Saturday">Saturday</option>
																				<%} %>
																				
																				
																				<%if (prefHealthCheckDays.contains("Sunday")) {%>
																					<option value="Sunday" SELECTED> Sunday</option>
																				<% }
																				else { %>
																					<option value="Sunday">Sunday</option>
																				<%} %>
																				
																			</select>
																		</span>
																		</td>
																	</tr>
																	<tr>
																		<td>
																		<span class="normalText">DO NOT send reports on these projects 
																		<br>(Ctrl+Click to select / deselect)
																		</span>
																		</td>
																		<td>
																		<%
																		ArrayList projects = userProfileSecurityProfile.getProjectObjects();
																		%>
																		<span class="normalText">
																			<select name="prefHealthCheckHideProjects" id="prefHealthCheckHideProjects" multiple="" size="15">
																				<%
																					Iterator p = projects.iterator();
																					while (p.hasNext()){
																						Project project = (Project) p.next();
																						if (project.getArchived() == 1){
																							continue;
																						}
																						
																						
																						if (prefHideProjects.contains(project.getProjectId() + ":#:" + project.getShortName()  )){
																							continue;
																						}
																					
																						
																						String projectString = project.getProjectId() + ":#:" + project.getShortName() ;
																						if (prefHealthCheckHideProjects.contains(projectString)){
																							%>
																							<option value="<%=projectString%>" SELECTED> <%=project.getProjectName() %></option>
																							<%
																						}
																						else {
																							%>
																							<option value="<%=projectString%>" > <%=project.getProjectName() %></option>
																							<%
																						}
																					}
																				%>			
																				
																			</select>
																		</span>
																		</td>
																	</tr>
																	
																	<tr> 
																		<td align='center' class='normalTableCell' colspan='2' style="text-align: center;">
																			<input type='button' class='btn btn-primary btn-sm'  name='updateEmailPreferences' value='Update Email Preferences'
																			onClick='updateEmailPref(this.form)'>
																		</td>
																	</tr>
																									
																</table>
															</form>
																
														
													</div>
												</td>
											</tr>
											
											
											
											<tr>
												<td><hr></hr></td>
											</tr>
											
											
											
											<%
												if (this.getServletContext().getInitParameter("installationType").equals("saas")) {	
												// the following options do not show up for onSite installation
											%> 		
	
												<tr> 
													<td>
														<div class='level1Box'>
															<form method="post" id="updateBillingInfoForm" action="/GloreeJava2/servlet/UserAccountAction">
															<input type='hidden' name='action' value='updateBillingInfo'>
															
															<table class='paddedTable'>
																<tr>
																	<td align='left'colspan='2' class='normalTableCell' colspan="2">
																		<div class="alert alert-info">Credit Card Info</div>
																	</td>  
																</tr>
																<tr>
																	<td align='left'colspan='2'> 
																		<span class='normalText'>
																			Your Credit Card on File is : <b>**** **** **** **<%=UserAccountUtil.getCCLastTwoDigits(user.getUserId()) %> </b>
																			It is expiring on <b><%=UserAccountUtil.getCCExpiry(user.getUserId())%></b>
																			<br>If you would like to change it, please do so below.
																		</span>
																	</td>
																</tr>
																<tr>
																	<td align='left'nowrap="">
																		<span class='headingText' > Credit Card Number</span>
																		<sup><span style="color: #ff0000;">*</span></sup>
																	</td>
																	<td align='left'nowrap="">
																		<span class='headingText' > 
																		<input type="text"  value="" name="ccNumber"/>
																		</span>
																	</td>
																   </tr>
																   <tr>
																		<td align='left'nowrap="">
																			<span class='headingText' > Type </span>
																			<sup><span style="color: #ff0000;">*</span></sup>
																		</td>
																		<td align='left'nowrap="">
																			<span class='headingText' > 
																			<select name="ccType">
																				<option selected="" value="">--Select One--</option>
																				<option value="Visa">Visa</option>
																				<option value="MC">MasterCard</option>
																				<option value="Amex">American Express</option>
																			</select>
																			</span>
																		</td>
																	</tr>
																	<tr>
																		<td align='left'nowrap="">
																			<span class='headingText' > Expires (month/year)</span>
																			<sup><span style="color: #ff0000;">*</span></sup>
																		</td>
																		<td align='left'nowrap="">
																			<span class='headingText' > 
																			<select name="ccExpireMonth">
																				<option selected="" value="">--</option>
																				<option value="01">1 - Jan</option>
																				<option value="02">2 - Feb</option>
																				<option value="03">3 - Mar</option>
																				<option value="04">4 - Apr</option>
																				<option value="05">5 - May</option>
																				<option value="06">6 - Jun</option>
																				<option value="07">7 - Jul</option>
																				<option value="08">8 - Aug</option>
																				<option value="09">9 - Sep</option>
																				<option value="10">10 - Oct</option>
																				<option value="11">11 - Nov</option>
																				<option value="12">12 - Dec</option>
																			</select>
																			
																			<select name="ccExpireYear">
																				<option selected="" value="">--</option>
																				<option value="2013">2013</option>
																				<option value="2014">2014</option>
																				<option value="2015">2015</option>
																				<option value="2016">2016</option>
																				<option value="2017">2017</option>
																				<option value="2018">2018</option>
																				<option value="2019">2019</option>
																				<option value="2020">2020</option>
																				<option value="2021">2021</option>
																				<option value="2022">2022</option>
																				<option value="2023">2023</option>
																				<option value="2024">2024</option>
																			</select>
																			</span>			
																		</td>						
																  </tr>
																  <tr>
																		<td align='left'nowrap="">
																			<span class='headingText' > 
																			Full Name on Credit Card
																			</span>
																			<sup><span style="color: #ff0000;">*</span></sup> 
																		</td>
																		<td align='left'nowrap="">
																			<span class='headingText' > 
																			<input type="text" size='30' value="" name="ccFullName"/>
																			</span>
																		</td>
																	</tr>
																	<tr>	
																		<td align='left'nowrap="" >
																			<span class='headingText' > 
																			Card Verification Number (
																			<a target="_blank" href="/GloreeJava2/images/WebSite/cvv.gif">
																			What is this?</a>)
																			</span>
																			<sup><span style="color: #ff0000;">*</span></sup>
																		</td>
																		<td align='left'nowrap="">
																			<span class='headingText' > 
																			<input type="text"  value="" maxlength="4" size='4' name="ccVerificationNumber"/>
																		</td>
																  </tr>
																  <tr>
																		<td align='left'nowrap="" >
																			<span class='headingText' > 
																			Credit Card Billing Street Address 
																			</span>
																			<sup><span style="color: #ff0000;">*</span></sup>
																		</td>
																		<td align='left'nowrap="" colspan="2">
																			<span class='headingText' > 
																			<input type="text" value="" name="ccBillingAddress"/>
																			</span>
																		</td>
																	</tr>
																	<tr>
																		<td align='left'nowrap="">
																			<span class='headingText' > Zip/Postal Code
																			</span>
																			<sup><span style="color: #ff0000;">*</span></sup>
																		</td>
																		<td align='left'nowrap="">
																			<span class='headingText' > 
																			<input type="text" value="" maxlength="15"  name="ccBillingZipcode"/>
																			</span>
																		</td>
																  </tr>
																  <tr>
																		<td align='left'nowrap="">
																			<span class='headingText' > 
																			Country
																			</span>
																			<sup><span style="color: #ff0000;">*</span></sup>
																		</td>
																		<td align='left'nowrap="">
																			<span class='headingText' > 
																			<select  name="ccBillingCountry">
																				<option selected="" value="">--Select One--</option>
																				<option value="United States">United States</option>
																				<option value="Australia">Australia</option>
																				<option value="Canada">Canada</option>
																				<option value="Germany">Germany</option>
																				<option value="Great Britain">Great Britain</option>
																				<option value="India">India</option>
																				<option value="Russia">Russia</option>
																				<option value="Ireland">Ireland</option>
																				<option value="Israel">Israel</option>
																				<option value="Italy">Italy</option>
																				<option value="Japan">Japan</option>
																				<option value="Jordan">Jordan</option>
																				<option value="New Zealand">New Zealand</option>
																				<option value="Ukraine">Ukraine</option>
																				<option value="Other">Other</option>
																				
																				
																			</select>
																			</span>
																		</td>
																	</tr>
																	<tr> 
																		<td align='left' class='normalTableCell' colspan='2' style="text-align: center;">
																			<input type='button' class='btn btn-primary btn-sm'  name='updateCCInfo' value='Update Billing Info'
																			onClick='updateBillingInfo(this.form)'>
																		</td>
																	</tr>
																								
															</table>
															</form>
														</div>
													</td>
												</tr>
											<%} %>															
										</table>
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
</body>
</html>	



















  