<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->    


<!-- AssetJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>



<%
	// authentication only
	String cNPWIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((cNPWIsLoggedIn == null) || (cNPWIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	// authorizatoin : since the user is just creating a new project, we don't need to check 
	// for isMember.
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User user = securityProfile.getUser();

	String shortNameExistsMessage = "";
	String shortNameExists = (String) request.getAttribute("shortNameExists");
	if (shortNameExists != null) {
		shortNameExistsMessage = "" +
			" <tr> " +
			"	<td colspan=2 align='left'>" +
			" 	<div id='shortNameExistsMessage' class='alert alert-success'> <span class='headingText'> " +
			"	This Project Prefix has already been used. Please choose another one. You may want to consider " +  
			" using unique starting characters for all your Organization's project . Eg. CS_.. for all Cisco projects." +
			"	</span></div> " + 
			" 	</td> " +
			" </tr>";
	}
	
 	String projectName = "";
 	String projectDescription = "";
 	String projectOwner = "";
 	String projectWebsite = "";
 	String projectOrganization = "";
 	String projectTags = "";
 	String restrictedDomains = "";
 	String shortName = "";
 	String administrators = "";
 	String users = "";
 	if (request.getParameter("projectName") != null){
 		projectName = request.getParameter("projectName");
 	}
 	if (request.getParameter("projectDescription") != null){
 		projectDescription = request.getParameter("projectDescription");
 	}
 	
 	if (request.getParameter("projectOwner") != null){
 		projectOwner = request.getParameter("projectOwner");
 	}
 	if (request.getParameter("projectWebsite") != null){
 		projectWebsite = request.getParameter("projectWebsite");
 	}
 	if (request.getParameter("projectOrganization") != null){
 		projectOrganization = request.getParameter("projectOrganization");
 	}
 	if (request.getParameter("projectTags") != null){
 		projectTags = request.getParameter("projectTags");
 	}
 	
 	if (request.getParameter("restrictedDomains") != null) {
 		restrictedDomains = request.getParameter("restrictedDomains");
 	}
 	if (request.getParameter("shortName") != null){
 		shortName = request.getParameter("shortName");
 	}
 	if (request.getParameter("administrators") != null){
 		administrators = request.getParameter("administrators");
 	}
 	if (request.getParameter("users") != null){
 		users = request.getParameter("users");
 	}
 	
 	if (administrators.equals("")){
 		administrators = user.getEmailId();
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

	
		
	<form method="post" id="createProjectForm" action="https://<%=request.getServerName()%>/GloreeJava2/servlet/ProjectAction">
	<div >
	
	
		<input type='hidden' name='action' value='createProject'>
		<input type='hidden' name='type' value='resourceManagement'>
		
		<input type="hidden" name="projectOwner" size="30" maxlength="100" 	value= '<%=projectOwner%>'>
		<input type="hidden" name="projectWebsite" size="30" maxlength="1000" value= '<%=projectWebsite%>'>
		<input type="hidden" name="projectOrganization" size="30" maxlength="100"  value= '<%=projectOrganization%>'>
		<input type="hidden" name="projectTags" size="30" maxlength="1000"  value= '<%=projectTags%>'>
		<input type="hidden" name="restrictedDomains" size="50" maxlength="100"  value='<%=restrictedDomains%>'>
		<input type="hidden" name="administrators" size="50" maxlength="100" value='<%=administrators%>'>
		<input type="text" name="users" size="50" maxlength="100" 	value='<%=users%>'>
														
																	
																																			
																		
																		
																		
		<div style="border-width: 2px; border-style: solid; border-color: white; padding:15px ">
		<table width='100%'>
			<tr>
				<td valign='top'>
				
				</td>
				<td>
					<div>
						<table >
							<tr>
								<td>
									<table class='paddedTable' align='left' width='470px'>
										<tr>
											<td id='projectDasnboardTD' align='center' valign='center'  width='95' height='50'
													class = 'focusTab'>
												<span class='headingText'>
													<a href='/GloreeJava2/jsp/UserDashboard/userProjects.jsp'> User Projects <a>
												</span>
											</td>
											<td id='accountProfileTD' align='center' valign='center'  width='95' height='50'
													class = 'nonFocusTab'>	
												<span class='headingText'>
													<a href='/GloreeJava2/jsp/UserDashboard/userProfile.jsp'> Account Profile <a>
												</span>
											</td>
											<td id='organizationTD' align='center' valign='center'  width='95' height='50'
													class = 'nonFocusTab'>
												<span class='headingText'> 
													<a href='/GloreeJava2/jsp/UserDashboard/userOrganization.jsp'> License Management <a>
												</span>
											</td>								
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>
								<div id="CreateProject" class="level1Box">



					
									<ul class="nav nav-tabs">
									  <li class="active">
									  	<a   data-toggle="tab" href="#createNewProjectDiv"
										 		onclick='
										 			document.getElementById("createNewProjectDiv").style.display="block";
													document.getElementById("cloneProjectDiv").style.display="none";
												'
										>Create New Project</a></li>
									  <li>
									  	<a data-toggle="tab" href="#cloneProjectDiv" 	
									  			onclick='
										 			document.getElementById("createNewProjectDiv").style.display="none";
													document.getElementById("cloneProjectDiv").style.display="block";
												'
										>Clone an Existing Project</a></li>
									  
										
									  </ul>				
									
												
									<div id='createNewProjectDiv' class="tab-pane fade in active" > 
									
						<div id="cNPW" class="level1Box" >	
							
										<div class='alert alert-info'> Create a New Project</div>							
											<table class='paddedTable' width='100%'>
												<%=shortNameExistsMessage %>
												<tr>
													<td align='left'>
														<div id="projectDetails" class="level2Box">
														<table class='paddedTable'>
																<tr>
																	<td width=100 align='left'>
																		<span class='headingText'>
																		Project Name</span>
																		<sup><span style="color: #ff0000;">*</span></sup>
																	</td>
																	<td align='left'>
																		<span class='normalText'>
																		<input type="text" name="projectName" size="30" maxlength="100" 
																		value= '<%=projectName%>'>
																		</input>
																		</span>
																	</td>
																</tr>
																<tr>
																	<td align='left'>
																		<span class='headingText'>
																		Project Prefix
																		</span>
																		<sup><span style="color: #ff0000;">*</span></sup>
																	</td>
																	<td align='left'>
																		<span class='normalText'>
																		<input type="text" name="shortName" size="10" maxlength="10" 
																		value='<%=shortName%>'>
																		</input>
																		</span>
																	</td>
																</tr>
																<tr>
																	<td align='left'>
																		<span class='headingText'>
																		Project Description
																		<sup><span style="color: #ff0000;">*</span></sup>
																	</td>
																	<td align='left'>
																		<span class='normalText'>
																		<textarea name="projectDescription" rows="5" cols="50" ><%=projectDescription %></textarea>
																		</span>
																	</td>
																</tr>
																
																
																
																							
														</table>
														</div>
													</td>
												</tr>
												<tr>
													<td align='left'>
														
														<table class='paddedTable' align='left'>
															<tr>
																<td colspan=2 align="left">
																	
																		<span class='normalText'>
																		<input type="button" class='btn btn-primary btn-sm'  name="Create New Project" value="Create New Project" 
																		id="createNewProjectButton"
																		onClick = 'createNewProjectWizard(this.form,"<%=user.getEmailId()%>")'>
																		</input>
																		&nbsp;&nbsp;&nbsp;&nbsp;
																		<input type='button' class='btn btn-danger btn-sm'  name='Cancel' value = 'Cancel' 
																		onClick="location.href='/GloreeJava2/jsp/UserDashboard/userProjects.jsp'" >
																		</input>
																		</span>
																</td>
																</tr>	
														</table>
														
													</td>
												</tr>	
											</table>
										</div>
													
									
									</div>
											
									<div id='cloneProjectDiv' class="tab-pane fade"> 
										<div class='alert alert-info'> Clone an Existing Project</div>
										<div id="cloneProjectDiv" class="level1Box" >
											
											<table class='paddedTable' width='100%'>
												<tr>
													<td align='left'>
														<span class='headingText'>
														Here is a list of projects that you are an 'Administrator' of. You can clone any one of these projects.</span>
													</td>								
												</tr>
												<tr>
													<td align='left'>
														<span class='headingText'>
														<select name='sourceProjectId' id = 'sourceProjectId'>
															<option value='0'>Please Select A Project</option>
														<%
														LinkedHashSet projects =  securityProfile.getProjects();
														if (projects.size() > 0 ){
															// project is a LinkedHashSet of project strings concatenated like :
															// 1:##:Lovely Mona:##:LVLYMONA:##:Mona is very lovely:##:sreenatht@gmail.com 
															// projectId:##:project name :##:short name:##:description:##:created_by
															Iterator iP = projects.iterator();
															while (iP.hasNext()){
																String projectString = (String) iP.next();
																String[] projectStringArray = projectString.split(":##:");
																String projectId = projectStringArray[0];
																if (securityProfile.getRoles().contains("AdministratorInProject" + projectId)){
																	// You can clone only those projects that you are an admin of.
																	%>
																	<option value='<%=projectId%>'><%= projectStringArray[2]%> : <%= projectStringArray[1]%></option>
																	<%
																}
															}
														}
														%>
														</select>
														</span>
													</td>								
												</tr>
												<tr>
													<td align='left'>
														<span class='headingText'>
														<input type='checkbox' name='cloneProjectStructure' id='cloneProjctStructure' value='cloneProjectStructure'
														CHECKED DISABLED></input>
														&nbsp;&nbsp;Clone Project Structure (Requirement Types, Folders, Roles and Permissions)
														</span>
													</td>
												</tr>

												<tr>
													<td align='left'>
														<span class='headingText'>
														<input type='checkbox' name='cloneUsers' id='cloneUsers' value='cloneUsers'
														onClick ='
															if (document.getElementById("cloneUsers").checked==false){
																document.getElementById("cloneRequirements").checked=false;
																document.getElementById("cloneTraceability").checked=false;
															}	
														
														'></input>
														&nbsp;&nbsp;Clone Users and Project Structure
														</span>
													</td>
												</tr>
												<tr>
													<td align='left'>
														<span class='headingText'>
														<input type='checkbox' name='cloneRequirements' id='cloneRequirements' value='cloneRequirements'
														onClick ='
															if (document.getElementById("cloneRequirements").checked==true){
																document.getElementById("cloneUsers").checked=true;
															}
															if (document.getElementById("cloneRequirements").checked==false){
																document.getElementById("cloneTraceability").checked=false;														
															}
														
														'></input>
														&nbsp;&nbsp;Clone Requirements, Users and Project Structure
														</span>
													</td>
												</tr>
												<tr>
													<td align='left'>
														<span class='headingText'>
														<input type='checkbox' name='cloneTraceability' id='cloneTraceability' value='cloneTraceability'
														onClick ='
															if (document.getElementById("cloneTraceability").checked==true){
																document.getElementById("cloneUsers").checked=true;
																document.getElementById("cloneRequirements").checked=true;
															}	
														
														'></input>
														&nbsp;&nbsp;Clone Traceability, Requirements, Users and Project Structure
														</span>
													</td>
												</tr>		
												<tr>
													<td align='left'>
														<span class='headingText'>
															<input type='button' class='btn btn-primary btn-sm'  name='cloneProjectButton' id='cloneProjectButton' value='Clone Project'
																onClick='cloneProject(this.form);' 
															></input>
															&nbsp;&nbsp;&nbsp;&nbsp;
															<input type='button' class='btn btn-danger btn-sm'  name='Cancel' value = 'Cancel' 
																onClick="location.href='/GloreeJava2/jsp/UserDashboard/userProjects.jsp'" >
															</input>
														</span>
														<div id="cloneProjectMessageDiv" class="actionPrompt" style="display:none;">
															<span class='headingText'>
															<br><br>
															Please note that Cloning a project is  a complex process.
															Hence it will take a few minutes to complete. 
															<br><br>
															Thank you for your patience.
															<br><br>
															Once the Cloning process completes, your cloned project will appear in your User Dashboard.
															<br><br>
															</span>
														</div>
														
													</td>
												</tr>
											</table>
										</div>

									</div>	
									
					
					

								</div>
								</td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
		</table>
		</div>
		<div class="clear"></div>
		<div class="footer">
			<span>Home | Contact </span><span
		 	style="margin-left: 50px;">Copyright www.tracecloud.com 2008. All
			rights reserved</span> <br>
		</div>
	</div>
	</form>
		
		

</body></html>