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

	 <script src="/GloreeJava2/js/jquery-3.1.1.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap-tour-standalone.min.js"></script>
	
	 <link href="/GloreeJava2/css/bootstrap.min.css" rel="stylesheet" media="screen">
	 <link href="/GloreeJava2/css/bootstrap-tour-standalone.min.css" rel="stylesheet">


<!-- Get the list of my projects by calling the util. -->


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
	
	
</head>
<body  >


<%
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
	
	User user = userProjectsSecurityProfile.getUser();
		
	if (user.getEmailId().equals("nathan@tracecloud.com")){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		
		try {		
			javax.naming.Context context =  new javax.naming.InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
	
			// lets print the number of users
			
			%>
				<table class='table' >
					<tr>
						<td colspan='2' bgcolor='#99CCFF'> <span class='normalText'>Totals </span> </td>
					</tr>
					
			<%

			String sql = "select count(*) 'users' from gr_users";
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int users = rs.getInt("users");
					%>
					<tr > 
						<td width='200px' align='left'> <span class='normalText'>Number of Users</span></td>
						<td align='left'> <span class='normalText'><%=users %></span></td>
					</tr>
					<%
				}
				prepStmt.close();
				rs.close();
			%>
				</table>		
			<%
			
			
			
			
			
		
			
			
			
			
			
			// lets print most recent 100 users
			%>





	<ul class="nav nav-tabs">
			  <li class="active">
			  	<a   data-toggle="tab" href="#recentUsersDiv"
				 		onclick='
				 			document.getElementById("recentUsersDiv").style.display="block";
							document.getElementById("recentProjectsDiv").style.display="none";
							document.getElementById("recentViewDiv").style.display="none";
							document.getElementById("changesDiv").style.display="none";
							document.getElementById("genericChangesDiv").style.display="none";
							document.getElementById("recentInvitationsDiv").style.display="none";
							document.getElementById("recentCommentsDiv").style.display="none";
							
						'
				>Recent Users</a></li>
			  <li>
			  	<a data-toggle="tab" href="#recentProjectsDiv" 	
			  			onclick='
				 			document.getElementById("recentUsersDiv").style.display="none";
							document.getElementById("recentProjectsDiv").style.display="block";
							document.getElementById("recentViewDiv").style.display="none";
							document.getElementById("changesDiv").style.display="none";
							document.getElementById("genericChangesDiv").style.display="none";
							document.getElementById("recentInvitationsDiv").style.display="none";
							document.getElementById("recentCommentsDiv").style.display="none";
						'
				>Recent Projects</a></li>
			  
			 	 <li>
			  	<a data-toggle="tab" href="#recentViewDiv" 	
			  			onclick='
				 			document.getElementById("recentUsersDiv").style.display="none";
							document.getElementById("recentProjectsDiv").style.display="none";
							document.getElementById("recentViewDiv").style.display="block";
							document.getElementById("changesDiv").style.display="none";
							document.getElementById("genericChangesDiv").style.display="none";
							document.getElementById("recentInvitationsDiv").style.display="none";
							document.getElementById("recentCommentsDiv").style.display="none";
						'
						
				>Recent Views </a></li>
				
				
				 <li>
			  	<a data-toggle="tab" href="#changesDiv" 	
			  			onclick='
				 			document.getElementById("recentUsersDiv").style.display="none";
							document.getElementById("recentProjectsDiv").style.display="none";
							document.getElementById("recentViewDiv").style.display="none";
							document.getElementById("changesDiv").style.display="block";
							document.getElementById("genericChangesDiv").style.display="none";
							document.getElementById("recentInvitationsDiv").style.display="none";
							document.getElementById("recentCommentsDiv").style.display="none";
						'
				>Recent Changes</a></li>
				
				 <li>
			  	<a data-toggle="tab" href="#genericChangesDiv" 	
			  			onclick='
				 			document.getElementById("recentUsersDiv").style.display="none";
							document.getElementById("recentProjectsDiv").style.display="none";
							document.getElementById("recentViewDiv").style.display="none";
							document.getElementById("changesDiv").style.display="none";
							document.getElementById("genericChangesDiv").style.display="block";
							document.getElementById("recentInvitationsDiv").style.display="none";
							document.getElementById("recentCommentsDiv").style.display="none";
						'
				>Recent Generic Changes</a></li>
				
				<li>
			  	<a data-toggle="tab" href="#recentInvitationsDiv" 	
			  			onclick='
				 			document.getElementById("recentUsersDiv").style.display="none";
							document.getElementById("recentProjectsDiv").style.display="none";
							document.getElementById("recentViewDiv").style.display="none";
							document.getElementById("changesDiv").style.display="none";
							document.getElementById("genericChangesDiv").style.display="none";
							document.getElementById("recentInvitationsDiv").style.display="block";
							document.getElementById("recentCommentsDiv").style.display="none";
						'
				>Recent Invitations</a></li>
				
				<li>
			  	<a data-toggle="tab" href="#recentCommentsDiv" 	
			  			onclick='
				 			document.getElementById("recentUsersDiv").style.display="none";
							document.getElementById("recentProjectsDiv").style.display="none";
							document.getElementById("recentViewDiv").style.display="none";
							document.getElementById("changesDiv").style.display="none";
							document.getElementById("genericChangesDiv").style.display="none";
							document.getElementById("recentInvitationsDiv").style.display="none";
							document.getElementById("recentCommentsDiv").style.display="block";
						'
				>Recent Comments</a></li>
				
			  </ul>				
			
		
			
			
			<div id='recentUsersDiv' class="tab-pane fade in active" > 			
						<br><br>
						<b>Most recent 1000 users</b>
						<br><br>
					
						<%
							ArrayList<Project> sampleProjects = ProjectUtil.getSampleProjects();
						%>
						<div class='alert alert-danger'>
							<table class='table table-striped'>
									<tr>
									<td>Id</td>
										<td>Prefix</td>
										<td>Created By</td>
										<td>Number of reqs</td>
										<td>Description</td>
										<td>Name</td>
										<td>Action</td>
									</tr>
									
								<%
									for (Project project:sampleProjects){
								%>
									
									<tr>
										<td>
												<%=project.getProjectId() %>
										</td>
										<td>
											<%=project.getShortName() %>
										</td>
										<td>
											<%=project.getCreatedBy()%>
										</td>
										<td>
											<%=project.getNumberOfRequirements() %>
										</td>
										<td>
											<%=project.getProjectDescription() %>
										</td>
										
										<td>
											<%=project.getProjectName() %>
										</td>
										<td>
											<div id='deleteProjectDiv<%=project.getProjectId()%>'>
											<input type='button' class='btn btn-sm btn-danger' 
											onclick='sRAAction("deleteProject",<%=project.getProjectId() %>,"deleteProjectDiv<%=project.getProjectId() %>");'
											value='Delete Project'>
											
											</input>
										</div>
										</td>
										
									</tr>
								<%	
									}
								%>
							
							</table>
						</div>
					
						<table class='table' >
					<tr>
						<td bgcolor='#99CCFF'> <span class='normalText'> User Id</span> </td>
						<td bgcolor='#99CCFF'> <span class='normalText'> User Type</span> </td>
						<td bgcolor='#99CCFF'> <span class='normalText'> Account Expire Dt </span> </td>
						
						<td bgcolor='#99CCFF'> <span class='normalText'> First Name </span> </td>
						<td bgcolor='#99CCFF'> <span class='normalText'> Last  Name </span> </td>
						<td bgcolor='#99CCFF'> <span class='normalText'> Email Id </span> </td>
						<td bgcolor='#99CCFF'> <span class='normalText'> Heard About TraceCloud  </span> </td>
						
						<td bgcolor='#99CCFF'> <span class='normalText'> Hello </span> </td>
						
						
					</tr>
					
			<%

			sql = "select u.id, u.user_type, u.account_expire_dt, u.first_name, u.last_name, u.email_id, u.heard_about_tracecloud, " +
				" ifnull(m.hello,'no') \"hello\", ifnull(m.need_help,'no') \"nathan_hello\" " +
				" from gr_users u left join gr_marketing m on u.id = m.user_id " +
				" order by id desc limit 50 ";
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			while (rs.next()){
					int id = rs.getInt("id");
					String userType = rs.getString("user_type");
					String accountExpireDt = rs.getString("account_expire_dt");
					String firstName = rs.getString("first_name");
					String lastName = rs.getString("last_name");
					String emailId = rs.getString("email_id");
					String heardAboutTraceCloud = rs.getString("heard_about_tracecloud");
					
					String helloDone  = rs.getString("hello");
					String nathanHelloDone = rs.getString("nathan_hello");
					
					String classStyle ="info";
					if (userType.equals("readWrite")){
						classStyle = "success";
					}
					if (userType.equals("expired")){
						classStyle = "danger";
					}
					
				%>
					<tr class='<%=classStyle %>' >
					
						<td > <span class='normalText'> <%=id%> </span> </td>
						<td > <span class='normalText'> <%=userType%> </span> </td>
						<td > <span class='normalText'> <%=accountExpireDt%> </span> </td>
						
						<td > <span class='normalText'> <%=firstName%> </span> </td>
						<td > <span class='normalText'> <%=lastName%> </span> </td>
						<td > <span class='normalText'> <%=emailId%></span> </td>
						<td > <span class='normalText'> <%=heardAboutTraceCloud%> </span> </td>
						
					</tr>
					<tr>
						<td colspan='7'>
							<table class='table'>
							<tr>
						<%
						if (
								(userType.equals("readWrite"))
										|| 
								(helloDone.equals("completed"))
							) {
						%>
							<td  style='width:300px'>
							
							</td>
						<%
						}
						else {
						%>
							<td  style='width:300px'>
								<div id='helloAction<%=id%>'>
									<input type='button' class='btn btn-sm btn-danger' 
										onclick='sRAAction("setHelloCompleted",<%=id %>,"helloAction<%=id %>");'
										value='Completed'>
									</input>
									<input type='button' class='btn btn-sm btn-success' 
										onclick='document.getElementById("sendHelloMail<%=id %>").style.display="block";'
										value='Send Hello'>
									</input>
								
									<input type='button' class='btn btn-sm btn-success' 
										onclick='document.getElementById("sendHelloMailOld<%=id %>").style.display="block";'
										value='Send Old Hello'>
									</input>
								
								</div>
							</td>
						<%} %>
						
						
						<%
						if (
								(userType.equals("readWrite"))
										|| 
								(nathanHelloDone.equals("completed"))
							) {
						%>
							<td ></td>
						<%
						}
						else {
						%>
							<td >
								<div id='nathanHelloAction<%=id%>'>
									<input type='button' class='btn btn-sm btn-danger' 
										onclick='sRAAction("setNathanHelloCompleted",<%=id %>,"nathanHelloAction<%=id%>");'
										value='Nathan Completed'>
									</input>
									<input type='button' class='btn btn-sm btn-success' 
										onclick='document.getElementById("sendNathanHelloMail<%=id%>").style.display="block";'
										value='Send Nathan Hello'>
									</input>
								
								
								</div>
							</td>
						<%} %>
							</tr>
							</table>
						
						</td>
					</tr>
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					<tr>
						<td colspan='7'>
							<div id='sendHelloMail<%=id%>' style='display:none'>
								To  <input type='text' id='helloTo<%=id %>' value='<%=emailId%>'></input>
								<br></br>
								Subject <input type='text' id='helloSubject<%=id %>' value='Managing your Project Requirements and Traceability with TraceCloud'></input>
								<br></br>
								<textarea id="helloBody<%=id %>"  rows="7" cols="130">
Hello <%=firstName %>,


We are reaching out to you because of your interest in Requirements Management with TraceCloud.

TraceCloud is designed to solve your Change Management , Collaboration and Traceability problems in an <b>Intuitive </b> way. A lot of our design focus is on <b>Ease of Use</b>

Here is a 30 minute workshop that will help you come up to speed : <a href='https://www.tracecloud.com/GloreeJava2/jsp/WebSite/TCDocumentation.jsp'>Launch Workshop</a>


We are very interested in showing how TraceCloud can help your organization gather, organize and manage your project's requirements.

We have assigned Nathan (cc'ed in this email) as your dedicated support engineer. If you have questions or need a quick walk through, please send him an email.


Best Regards

The TraceCloud Team.
								</textarea>
								<br></br>
								<input type='button' class='btn btn-sm btn-danger' 
									onclick='document.getElementById("sendHelloMail<%=id %>").style.display="none";'
									 value='Cancel'>
								</input>
								<input type='button' class='btn btn-sm btn-success' 
										onclick='
											document.getElementById("sendHelloMail<%=id %>").style.display="none";
											sRAAction("sendNathanHelloEmail",<%=id %>,"helloAction<%=id %>");'
										value='Send Hello Email'>
								</input>
							</div>
							
							<div id='sendHelloMailOld<%=id%>' style='display:none'>
								To  <input type='text' id='helloToOld<%=id %>' value='<%=emailId%>'></input>
								<br></br>
								Subject <input type='text' id='helloSubjectOld<%=id %>' value='Managing your Project Requirements and Traceability with TraceCloud'></input>
								<br></br>
								<textarea id="helloBodyOld<%=id %>"  rows="7" cols="130">
Hello <%=firstName %>,


We are reaching out to you because of your interest in Requirements Management with TraceCloud.

TraceCloud is designed to solve your Change Management , Collaboration and Traceability problems in an <b>Intuitive </b> way. A lot of our design focus is on <b>Ease of Use</b>

Here is a 30 minute workshop that will help you come up to speed : <a href='https://www.tracecloud.com/GloreeJava2/jsp/WebSite/TCDocumentation.jsp'>Launch Workshop</a>


We are very interested in showing how TraceCloud can help your organization gather, organize and manage your project's requirements.

We have assigned Nathan (cc'ed in this email) as your dedicated support engineer. If you have questions or need a quick walk through, please send him an email.


Best Regards

The TraceCloud Team.
								</textarea>
								<br></br>
								<input type='button' class='btn btn-sm btn-danger' 
									onclick='document.getElementById("sendHelloMailOld<%=id %>").style.display="none";'
									 value='Cancel'>
								</input>
								<input type='button' class='btn btn-sm btn-success' 
										onclick='
											document.getElementById("sendHelloMailOld<%=id %>").style.display="none";
											sRAAction("sendHelloEmailOld",<%=id %>,"helloAction<%=id %>");'
										value='Send Hello Email'>
								</input>
							</div>	
							
							
							
							
							
							
							
							
							
							<div id='sendNathanHelloMail<%=id%>' style='display:none'>
								To  <input type='text' id='NathanHelloTo<%=id %>' value='<%=emailId%>'></input>
								<br></br>
								Subject <input type='text' id='NathanHelloSubject<%=id %>' value='Managing your Project Requirements and Traceability with TraceCloud'></input>
								<br></br>
								<textarea id="NathanHelloBody<%=id %>"  rows="7" cols="130">
Hello <%=firstName %>,


Please let me know if you are free for a 15 minute call. I work out of US West Coast , but am flexible with my timings.


Best Regards

Nathan

								</textarea>
								<br></br>
								<input type='button' class='btn btn-sm btn-danger' 
									onclick='document.getElementById("sendNathanHelloMail<%=id %>").style.display="none";'
									 value='Cancel'>
								</input>
								<input type='button' class='btn btn-sm btn-success' 
										onclick='
											document.getElementById("sendNathanHelloMail<%=id %>").style.display="none";
											sRAAction("sendNathanHelloEmail",<%=id %>,"nathanHelloAction<%=id %>");'
										value='Nathan Hello'>
								</input>
							</div>							
							
							
							
							
							
							
							
							
							
							
							
													
						</td>
						
					</tr>
					<%
				}
				prepStmt.close();
				rs.close();
			%>
				</table>		
		
			</div>
			<%
			

			
			
			
			
			
			
			
			
			
			
			
			
			// lets print most recent 100 projects
			%>
			
			
				<div id='recentProjectsDiv' class="tab-pane fade in active" > 
							<br><br>
							<b>Most recent 100 projects</b>
							<br><br>
							<table class='table' >
								<tr>
									<td bgcolor='#99CCFF'> <span class='normalText'> Project Id</span> </td>
									<td bgcolor='#99CCFF'> <span class='normalText'> Short Name </span> </td>
									<td bgcolor='#99CCFF'> <span class='normalText'> Name  </span> </td>
									
									<td bgcolor='#99CCFF'> <span class='normalText'> Description </span> </td>
									<td bgcolor='#99CCFF'> <span class='normalText'> Owner </span> </td>
									<td bgcolor='#99CCFF'> <span class='normalText'> Created Date</span> </td>
									
									
								</tr>
								
						<%
			
						sql = "select p.id, p.short_name, p.name, p.description, p.owner, p.created_dt from gr_projects p order by id desc limit 100";
						prepStmt = con.prepareStatement(sql);
						rs = prepStmt.executeQuery();
						while (rs.next()){
								int id = rs.getInt("id");
								String shortName = rs.getString("short_name");
								String name = rs.getString("name");
								String description = rs.getString("description");
								String owner = rs.getString("owner");
								String createdDt = rs.getString("created_dt");
								
								String classStyle ="success";
								if (shortName.equals("SUN")){
									classStyle = "danger";
								}
								
								
								
							%>
								<tr class='<%=classStyle%>'>
								
									<td > <span class='normalText'> <%=id%> </span> </td>
									<td > <span class='normalText'> <%=shortName%> </span> </td>
									<td > <span class='normalText'> <%=name%> </span> </td>
									
									<td > <span class='normalText'> <%=description%> </span> </td>
									<td > <span class='normalText'> <%=owner%> </span> </td>
									<td > <span class='normalText'> <%=createdDt%></span> </td>
									
								</tr>
								<%
							}
							prepStmt.close();
							rs.close();
						%>
							</table>		

				
				</div>
				
				
				
				
				<div id='recentViewDiv' class="tab-pane fade in active" > 
		
								<br><br>
								<b>Most recent 500 View Events</b>
								<br><br>
								<table class='paddedTable' >
									<tr>
										<td bgcolor='#99CCFF'> <span class='normalText'> Project Name</span> </td>
										<td bgcolor='#99CCFF'> <span class='normalText'> Req Tag</span> </td>
										<td bgcolor='#99CCFF'> <span class='normalText'> Req Name </span> </td>
										
										<td bgcolor='#99CCFF'> <span class='normalText'> Viewer </span> </td>
										<td bgcolor='#99CCFF'> <span class='normalText'> Heard About TraceCloud </span> </td>
										<td bgcolor='#99CCFF'> <span class='normalText'> View Date</span> </td>
										
										
									</tr>
									
							<%
				
							sql = "select p.name, r.full_tag, r.name, u.email_id, u.heard_about_tracecloud, vl.view_dt  "
						+	" from gr_view_log vl , gr_users u, gr_requirements r, gr_projects p "
				 		+	"		where vl.user_id = u.id "
						+	"		and vl.requirement_id = r.id "
				  		+	"		and r.project_id = p.id "
						+	"		order by vl.view_dt desc limit 100";
							prepStmt = con.prepareStatement(sql);
							rs = prepStmt.executeQuery();
							while (rs.next()){
									String projectName = rs.getString("name");
									String fullTag = rs.getString("full_tag");
									String reqName = rs.getString("name");
									String emailId = rs.getString("email_id");
									String heardAboutTraceCloud = rs.getString("heard_about_tracecloud");
									String viewDt = rs.getString("view_dt");
								%>
									<tr >
									
										<td > <span class='normalText'> <%=projectName%> </span> </td>
										<td > <span class='normalText'> <%=fullTag%> </span> </td>
										<td > <span class='normalText'> <%=reqName%> </span> </td>
										
										<td > <span class='normalText'> <%=emailId%> </span> </td>
										<td > <span class='normalText'> <%=heardAboutTraceCloud%> </span> </td>
										<td > <span class='normalText'> <%=viewDt%></span> </td>
										
									</tr>
									<%
								}
								prepStmt.close();
								rs.close();
							%>
								</table>	
								</br></br></br></br>
				
				
				</div>	
			<%
			

			
			
			
			
			
		
			
			
			
			

			
			
			
			// lets print most recent 500 View events
			
			
			%>
				<div id='changesDiv' class="tab-pane fade in active" > 
				<br><br>
				<b>Most recent 500 Log  Events</b>
				<br><br>
				<table class='paddedTable' >
					<tr>
						<td bgcolor='#99CCFF'> <span class='normalText'> Description</span> </td>
						<td bgcolor='#99CCFF'> <span class='normalText'> Actor</span> </td>
						<td bgcolor='#99CCFF'> <span class='normalText'> Date</span> </td>
						
						
					</tr>
					
			<%

			sql = " select rl.description, rl.actor_email_id, rl.action_dt " + 
				"	from gr_requirement_log rl " +
				"	order by  rl.id desc " +
				"	limit 500;";
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			while (rs.next()){
					String description = rs.getString("description");
					String actor = rs.getString("actor_email_id");
					String actionDate = rs.getString("action_dt");
				%>
					<tr >
					
						<td > <span class='normalText'> <%=description%> </span> </td>
						<td > <span class='normalText'> <%=actor%> </span> </td>
						<td > <span class='normalText'> <%=actionDate%> </span> </td>
						
					</tr>
					<%
				}
				prepStmt.close();
				rs.close();
			%>
				</table>
				</br></br></br></br>
				
				</div>		
			
			
				<div id='genericChangesDiv' class="tab-pane fade in active" > 
				<br><br>
				<b>Most recent 1000 Generic Log  Events</b>
				<br><br>
				<table class='paddedTable' >
					<tr>
						<td bgcolor='#99CCFF'> <span class='normalText'> Project </span> </td>
						
						<td bgcolor='#99CCFF'> <span class='normalText'> Project Id</span> </td>
						<td bgcolor='#99CCFF'> <span class='normalText'> Object Id</span> </td>
						<td bgcolor='#99CCFF'> <span class='normalText'> Object Type</span> </td>
						
						<td bgcolor='#99CCFF'> <span class='normalText'> Description</span> </td>
						<td bgcolor='#99CCFF'> <span class='normalText'> Actor</span> </td>
						<td bgcolor='#99CCFF'> <span class='normalText'> Date</span> </td>
						
						
					</tr>
					
			<%
			
			// lets change all the folder id's in folder names
			/*sql = " update gr_generic_log gl1 " 
				+	" set gl1.object_type= ( select f.folder_path from gr_folders f where f.id = gl1.object_id) " 
				+	" where gl1.object_type='Folder' "
				+	" order by gl1.id desc limit 500";

			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			*/
			
			sql = " select p.name, project_id, object_id, object_type, gl.description, actor_email_id, action_dt " + 
				"	from gr_generic_log gl left join gr_projects p on gl.project_id = p.id  " +
				"	order by  gl.id desc " +
				"	limit 1000;";
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				
					String projectName = rs.getString("name");
					int projectId = rs.getInt("project_id");
					int objectId = rs.getInt("object_id");
					String objectType= rs.getString("object_type");
					
					String description = rs.getString("description");
					String actor = rs.getString("actor_email_id");
					String actionDate = rs.getString("action_dt");
				%>
					<tr >
					
					
						<td > <span class='normalText'> <%=projectName%> </span> </td>
						<td > <span class='normalText'> <%=projectId%> </span> </td>
						<td > <span class='normalText'> <%=objectId%> </span> </td>
						<td > <span class='normalText'> <%=objectType%> </span> </td>
						<td > <span class='normalText'> <%=description%> </span> </td>
						<td > <span class='normalText'> <%=actor%> </span> </td>
						<td > <span class='normalText'> <%=actionDate%> </span> </td>
						
					</tr>
					<%
				}
				prepStmt.close();
				rs.close();
			%>
				</table>
				</br></br></br></br>
				
				</div>		
			
	
	
				<div id='recentInvitationsDiv' class="tab-pane fade in active" > 			
						<br><br>
						<b>Most recent 100 Invitations</b>
						<br><br>
					
					
					
						<table class='table' >
					<tr>
						<td bgcolor='#99CCFF'> <span class='normalText'> Project</span> </td>
						<td bgcolor='#99CCFF'> <span class='normalText'> Role</span> </td>
						<td bgcolor='#99CCFF'> <span class='normalText'> Invitee</span> </td>
						
						<td bgcolor='#99CCFF'> <span class='normalText'> Invitor</span> </td>
						<td bgcolor='#99CCFF'> <span class='normalText'> Invite Date </span> </td>
						<td bgcolor='#99CCFF'> <span class='normalText'> Last Email Sent On</span> </td>
						<td bgcolor='#99CCFF'> <span class='normalText'> Total Emails Sent </span> </td>
						
						
					</tr>
					
			<%

			sql = "select p.name \"project_name\", r.name \"role_name\", i.invitee_email_id, i.invitor_email_id, i.event_dt, i.last_email_sent_on, i.emails_sent "+
					 " from gr_invitations i, gr_projects p, gr_roles r "+
					" where i.project_id = p.id "+
					" and i.role_id = r.id "+
					" order by i.id desc limit 100";
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			while (rs.next()){
					String projectName = rs.getString("project_name");
					String roleName = rs.getString("role_name");
					String invitee = rs.getString("invitee_email_id");
					String invitor = rs.getString("invitor_email_id");
					String eventDt = rs.getString("event_dt");
					String lastEmailSentOn = rs.getString("last_email_sent_on");
					int emailsSent = rs.getInt("emails_sent");
					String classStyle ="info";
					
					
				%>
					<tr class='<%=classStyle %>' >
					
						<td > <span class='normalText'> <%=projectName%> </span> </td>
						<td > <span class='normalText'> <%=roleName%> </span> </td>
						<td > <span class='normalText'> <%=invitee%> </span> </td>
						
						<td > <span class='normalText'> <%=invitor%> </span> </td>
						<td > <span class='normalText'> <%=eventDt%> </span> </td>
						<td > <span class='normalText'> <%=lastEmailSentOn%></span> </td>
						<td > <span class='normalText'> <%=emailsSent%> </span> </td>
						
					</tr>
					<%
				}
				prepStmt.close();
				rs.close();
			%>
				</table>		
		
			</div>
			
			
			
				<div id='recentCommentsDiv' class="tab-pane fade in active" > 			
						<br><br>
						<b>Most recent 100 Comments</b>
						<br><br>
					
					
					
						<table class='table' >
					<tr>
						<td bgcolor='#99CCFF'> <span class='normalText'> Project </span> </td>
						<td bgcolor='#99CCFF'> <span class='normalText'> Requirement</span> </td>
						<td bgcolor='#99CCFF'> <span class='normalText'> Comment </span> </td>
						
						<td bgcolor='#99CCFF'> <span class='normalText'> Commenter </span> </td>
						
						<td bgcolor='#99CCFF'> <span class='normalText'> Comment dt</span> </td>
						
						
					</tr>
					
			<%

			sql = "select p.name, r.full_tag, c.comment_note, c.commenter_email_id, c.comment_dt from gr_requirement_comments c , gr_requirements r, gr_projects p "+
					" where c.requirement_id = r.id "+
				"	and r.project_id = p.id "+
					" order by c.comment_dt  desc "+
					" limit 30; ";
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			while (rs.next()){

				String projectName = rs.getString("name");
				String fullTag = rs.getString("full_tag");
				String commentNote = rs.getString("comment_note");
				String commenterEmailId = rs.getString("commenter_email_id");
				String commentDt = rs.getString("comment_dt");
				
				String classStyle ="info";
					
				%>
					<tr class='<%=classStyle %>' >
					
						<td > <span class='normalText'> <%=projectName%> </span> </td>
						<td > <span class='normalText'> <%=fullTag%> </span> </td>
						<td > <span class='normalText'> <%=commentNote%> </span> </td>
						
						<td > <span class='normalText'> <%=commenterEmailId%> </span> </td>
						<td > <span class='normalText'> <%=commentDt%> </span> </td>
						
					</tr>
					<%
				}
				prepStmt.close();
				rs.close();
			%>
				</table>		
		
			</div>				
			
			
			
			<%
			
			

			
			
			
			
		}
		catch (Exception e){
			e.printStackTrace();
			%>
			<%=e.getMessage() %>
			<% 
		}
	}
	else {
		%>
			This is restricted area. Only certain users can get to this page
		<%
	}
			
%>




</body>
</html>	














 