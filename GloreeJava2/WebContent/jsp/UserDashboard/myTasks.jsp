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
	
	
	
<%

	String serverName = request.getServerName();
	//authorization
	// since we need authorization as well as authenticaiton we will use the 
	// security profile object.
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	if (securityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%
	
	// authorization : since we are explicityly checking for and 
	// listing all the projects the user has access to
	// we are OK here. 
	}
	
try {	
	User user = securityProfile.getUser();
	
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
<body onload='fillMyTasks();'>

	
<div class="wrapper box_theme_login_wrapper">
		<jsp:include page="/jsp/WebSite/Common/TCToolbar.jsp" />	
	
		<div style="border-width: 2px; border-style: solid; border-color: white; padding:15px ">
		<table width='100%' >
			<tr>
				<td valign='top'>
				
				</td>
				<td>
					<div >
						<table width='100%'>
							<tr>
								<td>
									<table class='paddedTable' align='left' width='800px'>
										<tr>
											<td id='globalSearchTD' align='center' valign='center'  width='150' height='50'
													class = 'focusTab'>
												<span class='normalText'> 
													<font color='gray'>	My Tasks </font>
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

												<td id='organizationTD' align='center' valign='center'  width='150' height='50'
													class = 'nonFocusTab'
													style='cursor:pointer'
													onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/UserDashboard/jiraIntegration.jsp";'
													
													>
													<span class='normalText' style='color:white'> 
														Jira Integration 
													</span>
												</td>	


	
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
								<td align='left'>
									<div id="myTasksDiv" class="level1Box" width='100%'>
									
										<table width='100%'  border=0>

											
											<tr>
												<td colspan='2' align='left'>
													<%
													int targetProjectId = 0 ;
													try {
														targetProjectId = Integer.parseInt(request.getParameter("targetProjectId"));	
													}
													catch (Exception e){
													}
													
													String ownedBy = request.getParameter("ownedBy");
													String dashboardType= request.getParameter("dashboardType");
													// in case the values didn't come in, lets default them.
													if ((ownedBy == null ) || (ownedBy.equals(""))){
														ownedBy = user.getEmailId();
													}
													if ((dashboardType == null ) || (dashboardType.equals(""))){
														dashboardType = "ProjectDashboard";
													}
													%>
													
													
												<div style='float:left'>
													<span class='normalText'> Project
														<select id='targetProject' name='targetProject'
														onchange='fillMyTasks();' >
															<%if (targetProjectId == 0 ) { %>
																<option value='0' selected >All Projects </option>
															<%}
															else {%>
																<option value='0'>All Projects </option>
															<%} %>
																<%
																ArrayList targetProjects = securityProfile.getProjectObjects();
																Iterator t = targetProjects.iterator();
																while (t.hasNext()){
																	Project targetProject = (Project) t.next();
																	// lets not show archived projects
																	if (targetProject.getArchived() == 1 ){
																		continue;
																	}
																	
																	// lets not show projects this user has hidden
																	if (user.getPrefHideProjects().contains( targetProject.getProjectId() + ":#:" + targetProject.getShortName())){
																		continue;
																	}
																	if (targetProject.getProjectId() == targetProjectId) {
																	%>
																		<option value='<%=targetProject.getProjectId()%>' selected>
																			<%=targetProject.getShortName()%> : <%=targetProject.getProjectName() %>
																		</option>
																	<%
																	}
																	else {
																	%>
																	<option value='<%=targetProject.getProjectId()%>'>
																		<%=targetProject.getShortName()%> : <%=targetProject.getProjectName() %>
																	</option>
																	<%
																	}
																}
																%>
															
														
														
														</select>													
													
													</span>
												</div>
												
												
												</td>
											</tr>
											<tr>
												<td colspan='2'>
													<%
													if (targetProjectId == 0 ){
													%>
													<div id='myTasksScopeDiv' style='display:none;'>
													</div>
													<%
													}
													else {
													%>
													<div id='myTasksScopeDiv' style='display:block;'>
														<%@ include file="myTasksScope.jsp" %>
													</div>
													<%} %>
												</td>
											</tr>
											<tr>
												<td colspan='2'>
															<table width='100%' border=0>
																<tr>
																	<td style='height:100px; width:16.7%' align='center' >
																		<table border=1 width='100%'><tr><td align='center' 
																		onmouseover=  "this.style.background='lightblue'; document.getElementById('myDanglingDiv').style.background='lightblue';" 
																		onmouseout=  "this.style.background='white';  document.getElementById('myDanglingDiv').style.background='white';"
																		title='Items that are dangling i.e do not have any trace from a downstream item'>
																		
																		<div class="level1Box" id='myDanglingDiv' >
																			<span class='normalText'>Dangling</span>
																		</div>
																		</td></tr></table>
																	</td>
																	<td style='height:100px; width:16.7% ' align='center' >
																		<table border=1 width='100%'><tr><td align='center' 
																		onmouseover=  "this.style.background='lightblue'; document.getElementById('myOrphanDiv').style.background='lightblue';" 
																		onmouseout=  "this.style.background='white';  document.getElementById('myOrphanDiv').style.background='white';"
																		title='Items that are Orphan, i.e do not trace to any upstream item' >
																		<div class="level1Box" id='myOrphanDiv'>
																			<span class='normalText'> Orphan</span>
																		</div>
																		</td></tr></table>
																	</td>
																	<td style='height:100px; width:16.7% ' align='center' >
																		<table border=1 width='100%'><tr><td align='center'  
																		onmouseover=  "this.style.background='lightblue'; document.getElementById('suspectUpDiv').style.background='lightblue';" 
																		onmouseout=  "this.style.background='white';  document.getElementById('suspectUpDiv').style.background='white';"
																		title='Items with a suspect trace Downstream (Something has changed Upstream) '>
																		<div class="level1Box" id='suspectUpDiv'>
																			<span class='normalText'> Suspect Up</span> 
																		</div>
																		</td></tr></table>
																	</td>
																	<td style='height:100px; width:16.7% ' align='center'>
																		<table border=1 width='100%'><tr><td align='center' 
																		onmouseover=  "this.style.background='lightblue'; document.getElementById('suspectDownDiv').style.background='lightblue';" 
																		onmouseout=  "this.style.background='white';  document.getElementById('suspectDownDiv').style.background='white';"
																		title='Items with a suspect trace Downstream (Something has changed Downstream)'>
																		<div class="level1Box"  id ='suspectDownDiv' >
																			<span class='normalText'> Suspect Down</span>
																		</div>
																		</td></tr></table>
																	</td>																	
																	<td style='height:100px; width:16.7%  ' align='center' >
																		<table border=1 width='100%'><tr><td align='center' 
																		onmouseover=  "this.style.background='lightblue'; document.getElementById('myReqsPendingApprovalDiv').style.background='lightblue';" 
																		onmouseout=  "this.style.background='white';  document.getElementById('myReqsPendingApprovalDiv').style.background='white';"
																		title="Items that are wainting for other's approval" >
																		<div class="level1Box" id='myReqsPendingApprovalDiv'>
																			<span class='normalText'> Pending Approval</span>
																		</div>
																		</td></tr></table>
																	</td>
																	<td style='height:100px; width:16.7%  ' align='center' >
																		<table border=1 width='100%'><tr><td align='center' 
																		onmouseover=  "this.style.background='lightblue'; document.getElementById('myReqsRejectedlDiv').style.background='lightblue';" 
																		onmouseout=  "this.style.background='white';  document.getElementById('myReqsRejectedlDiv').style.background='white';"
																		title='Items that have been rejected by other approvers'>
																		<div class="level1Box" id='myReqsRejectedlDiv'>
																			<span class='normalText'> Rejected</span>
																		</div>
																		</td></tr></table>
																	</td>

																	
																</tr>
															</table>
												
												</td>
											</tr>




										
											<tr>
												<td width='60%' valign='top'>
												<table width='100%'  border=0>
													<tr>
														<td valign='top'>  
															<table width='100%'  border=0 cellspacing=0>
																<tr>
																	<td style='height:70px; width:25%;' valign='top' align='center' >
																		<table border=1 width='100%'><tr><td align='center' 
																		onmouseover=  "this.style.background='lightblue'; document.getElementById('pendingMyApprovalDiv').style.background='lightblue';" 
																		onmouseout=  "this.style.background='white';  document.getElementById('pendingMyApprovalDiv').style.background='white';"
																		title='Items that are pending my approval'>
																		<div class="level1Box" id='pendingMyApprovalDiv' >
																			<span class='normalText'> Pending your approval</span>
																		</div>
																		</td></tr></table>
																	</td>
																	<td style='height:70px; width:25%;' valign='top' align='center'>
																		<table border=1 width='100%'><tr><td align='center'  
																		onmouseover=  "this.style.background='lightblue'; document.getElementById('incompleteDiv').style.background='lightblue';" 
																		onmouseout=  "this.style.background='white';  document.getElementById('incompleteDiv').style.background='white';"
																		title='Items that are Incomplete'>
																		<div class="level1Box" id='incompleteDiv'>
																			<span class='normalText'> Incomplete</span>
																		</div>
																		</td></tr></table>
																	</td>
																	<td style='height:70px; width:25%;' valign='top' align='center' >
																		<table border=1 width='100%'><tr><td align='center' 
																		onmouseover=  "this.style.background='lightblue'; document.getElementById('testPendingDiv').style.background='lightblue';" 
																		onmouseout=  "this.style.background='white';  document.getElementById('testPendingDiv').style.background='white';"
																		title='Items that have not been tested'>
																		<div class="level1Box" id='testPendingDiv'>
																			<span class='normalText'> Test Pending </span>
																		</div>
																		</td></tr></table>
																	</td>
																	<td style='height:70px; width:25%;' valign='top' align='center' >
																		<table border=1 width='100%'><tr><td align='center' 
																		onmouseover=  "this.style.background='lightblue'; document.getElementById('testFailedDiv').style.background='lightblue';" 
																		onmouseout=  "this.style.background='white';  document.getElementById('testFailedDiv').style.background='white';"
																		title='Items that have Failed testing'>
																		<div class="level1Box" id='testFailedDiv'>
																			<span class='normalText'> Test Failed </span>
																		</div>
																		</td></tr></table>
																	</td>
																	
																</tr>
															</table>
														</td>
												
													</tr>
													<tr>
														<td style='height:540px' valign='top'>
																<table width="100%" border="1"><tr><td valign='top'>
																	<table style="width:570px; height:530px;">
																		<tr>
																			<td align='left' valign='top' bgcolor='#99CCFF'>
																				<span class='normalText'>Changed in the last  
																				<input type='text' name='changedSince' id='changedSince' value='3' size='3' style="width:20px" onchange='fillRecentlyChangedReqs();'></input> days
																				</span>		
																			</td>
																		</tr>
																		<tr>
																			<td style='height:515px;'>
																				<div class="level1Box" id='recentlyChangedReqsDiv'>
																					
																				</div>
																			</td>
																		</tr>
																	</table>
																</td></tr></table>
														</td>
													</tr>
												
												</table>
												</td>
												<td width='40%' valign='top'>
													<table  width='100%'>
														<tr>
															<td>
																<table width='100%'  border=1>
																	<tr>
																		<td colspan='2' align='center' valign='center' style='height:50px'
																			onmouseover=  "this.style.background='lightblue'; document.getElementById('findItNowlDiv').style.background='lightblue';" 
																			onmouseout=  "this.style.background='white';  document.getElementById('findItNowlDiv').style.background='white';"
																			title='Google like search across all your projects. Search on comments, name, description, attributes etc...'>
																			
																			<div class="level1Box" id='findItNowlDiv'>
																			<span class='normalText'>
																				<input type='text' name='searchString' id='searchString' value='Search across projects' style="width:300px" 
																				onfocus='this.value=""'
																				onkeypress='
																					var keyCode = event.keyCode;
																					if (keyCode == 13) {
																						findItNow();
																					}
																				'>
																				<br><br>
																				<input type='button'  class='btn btn-primary btn-sm'   name='Search Now' value='Search Now' 
																				onclick='findItNow();'>
																			</span>
																			</div>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
													
													
														<tr>
															<td>
																<table width='100%'  border=1>
																	<tr>
																		<td style='height:540px'>
																			<table width="100%" border="0"><tr><td>
																				<table style="width:420px; height:530px;">
																					<tr>
																						<td align='left' bgcolor='#99CCFF'>
																							<span class='normalText'>Commented in the last  
																							<input type='text' name='commentedSince' id='commentedSince' value='7' size='3' style="width:20px" 
																							onchange='fillRecentlyCommentedReqs();'></input> days
																							</span>		
																						</td>
																					</tr>
																					<tr>
																					
																						<td style='height:515px;'>
																							<div class="level1Box" id='recentlyCommentedReqsDiv'>
																								
																							</div>
																						</td>
																					</tr>
																				</table>
																			</td></tr></table>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</td>
											</tr>							
											
																					
										</table>
									</div>
								</td>
							</tr>

							<%for (int i=0;i<25;i++){
								// if there are < 25 rows this pages doesn't look nice. so adding some fillers.
							%>
							<tr><td>&nbsp;</td></tr>
							<%} %>
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




<%
}
catch (Exception e) {

}

%>
 















  