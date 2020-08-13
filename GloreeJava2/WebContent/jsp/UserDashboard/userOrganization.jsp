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
	SecurityProfile userProjectsSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");


	if (userProjectsSecurityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%
	
	// authorization : since we are explicityly checking for and 
	// listing all the projects the user has access to
	// we are OK here. 
	}
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	String installationType = this.getServletContext().getInitParameter("installationType");
	String siteAdministrator = this.getServletContext().getInitParameter("siteAdministrator");
	if (siteAdministrator == null) {
		siteAdministrator = "";
	}

	//if installation type is onSite, then lets redrect to userOrganizationForOnSite.jsp
	if (installationType.toLowerCase().equals("onsite")){
	%>
		<jsp:forward page="/jsp/UserDashboard/userOrganizationForOnSite.jsp"/>
	<%
	}


	// there are times when the security profile could have changed due to the user's license management actions
	// so lets refresh his / her profile.
	userProjectsSecurityProfile = new SecurityProfile(userProjectsSecurityProfile.getUser().getUserId(),this.getServletContext().getInitParameter("databaseType"));
	session.setAttribute("securityProfile",userProjectsSecurityProfile );
	
	User user = userProjectsSecurityProfile.getUser();
	// get the list of projects this user has access to
	int organizationId = user.getMyOwnedOrganization();
	Organization organization = new Organization(organizationId);
	String organizationName = "";
	if (organization.getName() != null){
		organizationName = organization.getName();
	}
	String organizationDescription =  "" ;
	if (organization.getDescription()!= null){
		organizationDescription = organization.getDescription();
	}
	String organizationPhone = "";
	if (organization.getPhoneNumber()!= null){
		organizationPhone = organization.getPhoneNumber();
	}
	int readWriteLicenses = organization.getReadWriteLicenses();
	int readOnlyLicenses = organization.getReadOnlyLicenses();
	
	int usedReadWriteLicenses = organization.getNumOfUsedReadWriteLicenses();
	int usedReadOnlyLicenses = organization.getNumOfUsedReadOnlyLicenses();
	
	int availableReadWriteLicenses = readWriteLicenses - usedReadWriteLicenses;
	int availableReadOnlyLicenses = readOnlyLicenses - usedReadOnlyLicenses;
	
	String disableLicenseButton = "DISABLED='disabled'";
	if (
			(installationType.equals("saas"))
			||
			(installationType.equals("onSite") &&   (siteAdministrator.contains(user.getEmailId()))) 
		)
	{
		// this means that the installation type = saas or installation type=onSite and the user is the site admin.
		// so, he / she can manage licenses.
		disableLicenseButton  = "";
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
		<table width='100%'>
			<tr>
				<td valign='top'>
				
				</td>
				<td>
					<div >
						<table width='100%'>
							<tr>
								<td>
									<table class='paddedTable' align='left' width='640px' >
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
													class = 'nonFocusTab'
													style='cursor:pointer'
													onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/UserDashboard/userProfile.jsp";'
													>
												<span class='normalText' style='color:white'> 
													Account Profile
												</span>
											</td>	
											
											
											
											
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
												if (this.getServletContext().getInitParameter("installationType").equals("onSite")) {
																	
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
	

							<%
								if (installationType.equals("onSite")){
								// lets print a message that the SiteAdmin is the only one who can manage licenses.							
							%>
								<tr>
									<td>
										<span class='normalText'>
										Please note that only <b><%=siteAdministrator %></b> have permissions to do license management for this installation.
										If you need a TraceCloud license, please contact them.
										</span>
									</td>
								</tr>

							<%	}%> 
							<tr>
								<td>
									<form method="post" id="createAccountForm" action="/GloreeJava2/servlet/UserAccountAction">						
									<input type='hidden' name='action' id='action' value=''>
								
									<div id="userDashboardDiv" class="level1Box" >
										<table align="center" class='paddedTable' width='100%'>
											<tr>
												<td>
													<table width='100%' cellspacing='0'>
													<tr>
														<td align='left' bgcolor='#99CCFF'>				
															<span class='subSectionHeadingText'>
															Your Organization
															</span>
														</td>
														<td align='right' bgcolor='#99CCFF'>
																<a class='toolbar' href="/GloreeJava2/servlet/UserAccountAction?action=signOut"	 >
																Log out</a>
																&nbsp;&nbsp;&nbsp;&nbsp;
																<a class='toolbar' 									  				  href='http://<%=request.getServerName()%>/GloreeJava2/documentation/help/userProjectsDashboard.htm'
																target='_blank'> Help ?</a>
														</td>
													</tr>							
													</table>
												</td>
											</tr>

											<tr>
												<td>
													
													<div id ='organizationInfoDiv' class='level1Box'>
													 
													<table class='paddedTable'>
														<tr>
															<td colspan='2' align='left'> 
																<div class="alert alert-info">Licensing Details</div>
															</td>
														</tr>
														<tr>
															<td width='200' align='left'> 
																<span class='normalText'>
																	Organization Name 
																</span>
															</td>
															<td align='left'> 
																<span class='normalText'>
																	<input type='text' name='organizationName' id='organizationName' size='50' 
																	value='<%=organizationName%>'>
																</span> 
															</td>												
														</tr>
														<tr>
															<td width='200' align='left'> 
																<span class='normalText'>
																	Organization Description 
																</span>
															</td>
															<td align='left'> 
																<span class='normalText'>
																	<textarea name='organizationDescription' id='organizationDescription' rows='5' cols='50'><%=organizationDescription%></textarea>
																</span> 
															</td>												
														</tr>			
														<tr>
															<td width='200' align='left'> 
																<span class='normalText'>
																	Contact Phone Number 
																</span>
															</td>
															<td align='left'> 
																<span class='normalText'>
																	<input type='text' name='organizationPhone' id='organizationPhone' size='20'
																	 value='<%=organizationPhone%>'>
																</span> 
															</td>												
														</tr>			
														<tr>
															<td width='200' align='left'> 
																<span class='normalText'>
																	Read Write Licenses 
																</span>
															</td>
															<td align='left'> 
																<span class='normalText'>
																	<input type='text' name='readWriteLicenses' id='readWriteLicenses' size='4' 
																	value='<%=readWriteLicenses %>'>
																	$30 per user per month 
																</span> 
															</td>												
														</tr>
														<tr>
															<td width='200' align='left'> 
																<span class='normalText'>
																	Read Only Licenses 
																</span>
															</td>
															<td align='left'> 
																<span class='normalText'>
																	<input type='text' name='readOnlyLicenses' id='readOnlyLicenses' 
																	value='<%=readOnlyLicenses %>'>
																	$25 per user per month 
																</span> 
															</td>												
														</tr>					
													
														<tr>
															<td align='left'> 
																<span class='normalText'>
																	Project Licenses 
																</span>
															</td>
															<td align='left'> 
																<span class='normalText'>
																<select id='projectLicense' name='projectLicense' MULTIPLE SIZE='5'>
																	<%
																	ArrayList projects = ProjectUtil.getUsersNonLicensedProjects(user);
																	// these are the list of projects that the user is a member of 
																	// and are not yet sponsored by any one.
																	Iterator p = projects.iterator();
																	while (p.hasNext()){
																		Project project = (Project) p.next();
																		%>
																		<option value='<%=project.getProjectId() %>'><%=project.getShortName() %> : <%=project.getProjectName() %></option>
																		<%
																	}
																	%>
																	</select>			
																</span> 
															</td>												
														</tr>					
														
														<tr>
															<td colspan='2'> 
																
																<span class='normalText'>
																	<input type='button' class='btn btn-primary btn-sm'  <%=disableLicenseButton%> name='updateOrganization' id='updateOrganization'  
																	value='Update Licenses'
																	onClick='updateOrganizationInfo(this.form)'>
																</span>
															</td>
														</tr>					
																			
													</table>									
													
													</div>
													
												</td>
											</tr>
											
											
						
											
											
											
											<tr>
												<td>
													
													<div id ='grantLicensesDiv' class='level1Box'>
													 
													<table class='paddedTable'>
														<tr>
															<td colspan='2' align='left'> 
																<div class="alert alert-info">Grant User Licenses</div>
															</td>
														</tr>
						
														<tr>
															<td align='left'> 
																<span class='normalText'>
																	Grant Read Write Licenses (<%=readWriteLicenses - usedReadWriteLicenses%> available)
																	<br> 
																	(Enter Comma separated email Ids)
																</span>
															</td>
															<td align='left'> 
																<span class='normalText'>
																	<textarea name='readWriteInvitees' id='readWriteInvitees' rows='3' cols='50'></textarea>
																</span> 
															</td>												
														</tr>
														<tr>
															<td align='left'> 
																<span class='normalText'>
																	Grant Read Only Licenses (<%=readOnlyLicenses - usedReadOnlyLicenses%> available)
																	<br> 
																	(Enter Comma separated email Ids)
																</span>
															</td>
															<td align='left'> 
																<span class='normalText'>
																	<textarea name='readOnlyInvitees' id='readOnlyInvitees' rows='3' cols='50'></textarea>
																</span> 
															</td>												
														</tr>					
														<tr>
															<td colspan='2'> 
																<span class='normalText'>
																	<input type='button' class='btn btn-primary btn-sm'  <%=disableLicenseButton%> name='grantLicenses' id='grantLicenses'  value='Grant User Licenses'
																	onClick='grantLicensesForm(this.form , <%=availableReadWriteLicenses%>, <%=availableReadOnlyLicenses%>)'>
																</span>
															</td>
															
														</tr>					
																			
													</table>									
													</div>
												</td>
											</tr>
											




											<tr>
												<td>
													
													<div id ='grantLicensesDiv' class='level1Box'>
													 
													<table class='paddedTable'>
														<tr>
															<td colspan='4'> 
																
																<div class="alert alert-info">Granted Project Licenses</div>
															</td>
														</tr>
														<input type='hidden' name='licensedProjectId' id='licensedProjectId' value=''>
														<%
														ArrayList licensedProjects = UserAccountUtil.getMyLicensedProjects(user);
														if (licensedProjects.size() > 0){
														%>
															<tr>
																<td> 
																	<span class='normalText'>
																	Project Prefix															
																	</span>
																</td>
																<td> 
																	<span class='normalText'>
																	Project Name															
																	</span>
																</td>
																<td> 
																	<span class='normalText'>
																	Requirements															
																	</span>
																</td>
																<td> 
																	&nbsp;
																</td>												
															</tr>
														<%	
														}
														Iterator l = licensedProjects.iterator();
														while (l.hasNext()){
															Project project = (Project) l.next();
														%>
															<tr>
																<td> 
																	<span class='normalText'>
																	<%=project.getShortName() %>															
																	</span>
																</td>
																<td> 
																	<span class='normalText'>
																	<%=project.getProjectName() %>															
																	</span>
																</td>
																<td> 
																	<span class='normalText'>
																	<%=project.getNumberOfRequirements()%> 															
																	</span>
																</td>
																<td> 
																	<span class='normalText'>
																	<input type='button' class='btn btn-danger btn-sm'  <%=disableLicenseButton%> name='revokeProjectLicenseButton' id='revokeProjectLicenseButton'
																	 value='Revoke Project License'
																	onClick='revokeProjectLicense(this.form , <%=project.getProjectId()%>)'>
																	</span> 
																</td>												
															</tr>
														<%
														}
														%>	
													</table>									
													
													</div>										
												</td>
											</tr>






											<tr>
												<td>
													
													<div id ='grantLicensesDiv' class='level1Box'>
													 
													<table class='paddedTable'>
														<tr>
															<td colspan='4' align='left'> 
																
																<div class="alert alert-info">Not yet accepted user license grants</div>
															</td>
														</tr>
														<input type='hidden' name='licenseGrantId' id='licenseGrantId' value=''>
														<%
														ArrayList grantees = UserAccountUtil.getMyPendingLicenseGrants(user.getEmailId(), databaseType);
														Iterator g = grantees.iterator();
														while (g.hasNext()){
															LicenseGrant pendingLicenseGrant = (LicenseGrant) g.next();
														%>
															<tr>
																<td align='left'> 
																	<span class='normalText'>
																	<%=pendingLicenseGrant.getGranteeEmailId() %>															
																	</span>
																</td>
																<td align='left'> 
																	<span class='normalText'>
																	<%=pendingLicenseGrant.getLicenseType() %>															
																	</span>
																</td>
																<td align='left'> 
																	<span class='normalText'>
																	<%=pendingLicenseGrant.getGrantDate()%>															
																	</span>
																</td>
																<td align='left'> 
																	<span class='normalText'>
																	<input type='button' class='btn btn-danger btn-sm'  <%=disableLicenseButton%> name='revokeOfferButton' id='revokeOfferButton'  
																	value='Revoke User License'
																	onClick='revokeLicenseOffer(this.form , <%=pendingLicenseGrant.getLicenseGrantId()%>)'>
																	</span> 
																</td>												
															</tr>
														
														<%
														}
														%>	
													</table>									
													
													</div>										
												</td>
											</tr>






											<tr>
												<td>
													
													<div id ='grantLicensesDiv' class='level1Box'>
													 
													<table class='paddedTable'>
														<tr>
															<td colspan='3' align='left'> 
																
																<div class="alert alert-info">Accepted User License Grants</div>
															</td>
														</tr>
														<input type='hidden' name='granteeEmailId' id='granteeEmailId' value=''>
														<%
														grantees =  organization.getAllUsersInOrganization(databaseType);
														g = grantees.iterator();
														while (g.hasNext()){
															User grantee = (User) g.next();
															if (grantee.getUserType().equals("expired")){
																continue;
															}
														%>
															<tr >
																<td align='left'> 
																	<span class='normalText'>
																	<%=grantee.getFirstName()%> <%=grantee.getLastName() %>
																	(<%=grantee.getEmailId() %>)															
																	</span>
																</td>
																<td align='left'> 
																	<span class='normalText'>
																	<%=grantee.getUserType() %>
																	</span> 
																</td>
																<td align='left'>																
																	<span class='normalText'>
																	<input type='button' class='btn btn-danger btn-sm'  <%=disableLicenseButton%> name='revokeGrantedLicenseButton' 
																	id='revokeGrantedLicenseButton'  value='Revoke User License'
																	onClick='revokeGrantedLicense(this.form , "<%=grantee.getEmailId()%>")'>
																	</span> 
																</td>						
															</tr>
														<%
														}
														%>	
													</table>									
													
													</div>										
												</td>
											</tr>

											
											
											
											
											
											
											
										</table>
									</div>
									</form>
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



















  