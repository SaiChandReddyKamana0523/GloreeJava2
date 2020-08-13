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

	
<%
	//authorization
	// since we need authorization as well as authenticaiton we will use the 
	// security profile object.
	SecurityProfile userProjectsSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	
	String serverName = request.getServerName();


	if (userProjectsSecurityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%
	}
	User user = userProjectsSecurityProfile.getUser();
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	String installationType = this.getServletContext().getInitParameter("installationType");
	String siteAdministrator = this.getServletContext().getInitParameter("siteAdministrator");
	if (siteAdministrator == null) {
		siteAdministrator = "";
	}

	//if installation type is onSite, then lets redrect to userOrganizationForOnSite.jsp
	if (installationType.toLowerCase().equals("saas")){
	%>
		<jsp:forward page="/jsp/UserDashboard/userOrganization.jsp"/>
	<%
	}

	int permittedReadWriteLicenses = 0;
	try{
		permittedReadWriteLicenses = Integer.parseInt(this.getServletContext().getInitParameter("readWriteLicenses"));
	}
	catch (Exception e){
		permittedReadWriteLicenses = 0;
	}

	
	// if this page was called with some action, lets take care of it.
	
	String action = request.getParameter("action");
	String targetUser = request.getParameter("targetUser");
		
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
									<table align='left' valign='top' width='840px'>
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
													style='cursor:pointer; color:white;'
													onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/UserDashboard/userProjects.jsp";'
													>
													My Projects
													</font>
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
													class = 'focusTab'>
												<span class='normalText'> 
													<a href='/GloreeJava2/jsp/UserDashboard/userOrganization.jsp'> License Management<a>
												</span>
											</td>	
											<%
												if (this.getServletContext().getInitParameter("installationType").equals("onSite")) {
																	
											%>
											<td id='siteMetricsTD' align='center' valign='center'  width='150' height='50'
													class = 'nonFocusTab'>
												<span class='normalText'> 
													<a href='/GloreeJava2/jsp/UserDashboard/siteMetrics.jsp'> Site Metrics<a>
												</span>
											</td>
											<%	}%> 											
										</tr>
									</table>
								</td>
							</tr>

							<%	
							if ((action != null) && (targetUser != null)){
								if (action.equals("grantReadWriteLicense")){
									UserAccountUtil.grantOnSiteLicense(session, user, targetUser, databaseType);
								%>
								<tr>
									<td>
										<div class='alert alert-success'>
										<span class='normalText'>
										A TraceCloud Read Write license has been <b> GRANTED </b>to <%=targetUser %>
										</span>
										</div>
									</td>
								</tr>
								
								<%
								}
								if (action.equals("revokeReadWriteLicense")){
									UserAccountUtil.revokeGrantedLicense(session, user, targetUser);
								%>
								<tr>
									<td>
										<div class='alert alert-success'>
										<span class='normalText'>
										A TraceCloud Read Write license has been <b> REVOKED </b> from <%=targetUser %>
										</span>
										</div>
									</td>
								</tr>
								
								<%
								}
							}	
							%>

							<tr>
								<td>
									<span class='normalText'>
									Please note that only <b><%=siteAdministrator %></b> have permissions to do license management for this installation.
									If you need a TraceCloud license, please contact them.
									</span>
								</td>
							</tr>

							<tr>
								<td>
								
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
																<span class='sectionHeadingText'>
																	Licensing Info... 
																</span>
															</td>
														</tr>
														<tr>
															<td width='200' align='left'> 
																<span class='normalText'>
																	Permitted Read Write Licenses 
																</span>
															</td>
															<td align='left'> 
																<span class='normalText'>
																	<%=permittedReadWriteLicenses%>
																</span> 
															</td>												
														</tr>
														<tr>
															<td width='200' align='left'> 
																<span class='normalText'>
																	Users with Read Write Licenses 
																</span>
															</td>
															<td align='left'> 
																<span class='normalText'>
																<%=ProjectUtil.getNumberOfUsersByLicenseType(databaseType, "readWrite") %>
																</span> 
															</td>												
														</tr>			
														<tr>
															<td width='200' align='left'> 
																<span class='normalText'>
																	Users with Trial Licenses 
																</span>
															</td>
															<td align='left'> 
																<span class='normalText'>
																<%=ProjectUtil.getNumberOfUsersByLicenseType(databaseType, "trial") %>
																</span> 
															</td>												
														</tr>			
														<tr>
															<td width='200' align='left'> 
																<span class='normalText'>
																	Users with Expired Licenses 
																</span>
															</td>
															<td align='left'> 
																<span class='normalText'>
																<%=ProjectUtil.getNumberOfUsersByLicenseType(databaseType, "expired") %>
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
															<td colspan='4' align='left'> 
																<span class='sectionHeadingText'>
																	<br></br>
																	<b>Users with Expired / Revoked License</b>
																	<br></br>
																</span>
															</td>
														</tr>
														<tr>
															<td>
																<span class='normalText'>
																<b>User</b>
																</span>
															</td>
															<td>
																<span class='normalText'>
																<b>License State</b>
																</span>
															</td>
															<td>
																<span class='normalText'>
																<b>Last Log on Date</b>
																</span>
															</td>
															<td>
																<span class='normalText'>
																<b>Action</b>
																</span>
															</td>
														</tr>
														
														<%
														ArrayList members = new ArrayList();
														if ((siteAdministrator.contains(user.getEmailId())
															&&
															(installationType.toLowerCase().equals("onsite"))
															)){															// this user is a site admin and this is an onsite installation
															members = user.getAllUsersOnSite(databaseType, "expired");
														}
														Iterator m = members.iterator();
														while (m.hasNext()){
															User member = (User) m.next();
														%>
															<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';" style="background-color: white; background-position: initial initial; background-repeat: initial initial;">
																<td align='left'> 
																	<span class='normalText'>
																	<%=member.getFirstName()%> <%=member.getLastName() %>
																	(<%=member.getEmailId() %>)															
																	</span>
																</td>
																<td > 
																	<span class='normalText'>
																	Expired License
																	</span> 
																</td>
																<td>
																	<span class='normalText'>
																	<%=member.getLastLogonDt() %>
																	</span>
																</td>
																<td >																		
																	<a 
																		class='btn btn-sm btn-primary' 
																		style=' color:white;'
																		href='/GloreeJava2/jsp/UserDashboard/userOrganizationForOnSite.jsp?action=grantReadWriteLicense&targetUser=<%=member.getEmailId() %>'> Grant A Read Write License <a>
																</td>						
															</tr>
														<%
														}
														%>	

















														<tr>
															<td colspan='3' align='left'> 
																<span class='sectionHeadingText'>
																	<br></br>
																	<b>Users with Trial License </b>
																	<br></br>
																</span>
															</td>
														</tr>
														
														<tr>
															<td>
																<span class='normalText'>
																<b>User</b>
																</span>
															</td>
															<td>
																<span class='normalText'>
																<b>License State</b>
																</span>
															</td>
															<td>
																<span class='normalText'>
																<b>Last Log on Date</b>
																</span>
															</td>
															<td>
																<span class='normalText'>
																<b>Action</b>
																</span>
															</td>
														</tr>
														
														<%
														if ((siteAdministrator.contains(user.getEmailId())
															&&
															(installationType.toLowerCase().equals("onsite"))
															)){															// this user is a site admin and this is an onsite installation
															members = user.getAllUsersOnSite(databaseType, "trial");
														}
														m = members.iterator();
														while (m.hasNext()){
															User member = (User) m.next();
															String memberFullName = member.getFirstName() + " "  + member.getLastName();
														%>
															<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';" style="background-color: white; background-position: initial initial; background-repeat: initial initial;">
																<td align='left'> 
																	<span class='normalText'>
																	<%=member.getFirstName()%> <%=member.getLastName() %>
																	(<%=member.getEmailId() %>)															
																	</span>
																</td>
																<td align='left'> 
																	<span class='normalText'>
																	Trial License
																	</span> 
																</td>
																<td>
																	<span class='normalText'>
																	<%=member.getLastLogonDt() %>
																	</span>
																</td>
																
																<td align='left'>																
																	<a
																			class='btn btn-sm btn-primary'
																			href='/GloreeJava2/jsp/UserDashboard/userOrganizationForOnSite.jsp?action=grantReadWriteLicense&targetUser=<%=member.getEmailId() %>'> Grant A Read Write License <a>
																			
																			<input type='button' class='btn btn-sm btn-primary'
																		value='Reset Password'
																		onclick='resetAnotherUsersPassword("<%=member.getUserId()%>", "<%=memberFullName%> ");'
																	>
																	
																</td>						
															</tr>
														<%
														}
														%>	










														<tr>
															<td colspan='3' align='left'> 
																<span class='sectionHeadingText'>
																	<br></br>
																	<b>Users with Read / Write License</b>
																	<br></br>
																</span>
															</td>
														</tr>
														<tr>
															<td>
																<span class='normalText'>
																<b>User</b>
																</span>
															</td>
															<td>
																<span class='normalText'>
																<b>License State</b>
																</span>
															</td>
															<td>
																<span class='normalText'>
																<b>Last Log on Date</b>
																</span>
															</td>
															<td>
																<span class='normalText'>
																<b>Action</b>
																</span>
															</td>
														</tr>
														
														<%
														members = new ArrayList();
														if ((siteAdministrator.contains(user.getEmailId())
															&&
															(installationType.toLowerCase().equals("onsite"))
															)){															// this user is a site admin and this is an onsite installation
															members = user.getAllUsersOnSite(databaseType, "readWrite");
														}
														m = members.iterator();
														while (m.hasNext()){
															User member = (User) m.next();
															String memberFullName = member.getFirstName() + " " + member.getLastName();
														%>
															<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';" style="background-color: white; background-position: initial initial; background-repeat: initial initial;">

																<td align='left'> 
																	<span class='normalText'>
																	<%=member.getFirstName()%> <%=member.getLastName() %>
																	(<%=member.getEmailId() %>)															
																	</span>
																</td>
																<td align='left'> 
																	<span class='normalText'>
																	Read Write License
																	</span> 
																</td>
																<td>
																	<span class='normalText'>
																	<%=member.getLastLogonDt() %>
																	</span>
																</td>
																<td align='left'>																
																	<a 
																	class='btn btn-sm btn-danger'
																	style=' color:white;'
																	href='/GloreeJava2/jsp/UserDashboard/userOrganizationForOnSite.jsp?action=revokeReadWriteLicense&targetUser=<%=member.getEmailId() %>'> Revoke License <a>
																	
																	
																	<input type='button' class='btn btn-sm btn-primary'
																		value='Reset Password'
																		onclick='resetAnotherUsersPassword("<%=member.getUserId()%>", "<%=memberFullName%> ");'
																	>
																	
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



















  