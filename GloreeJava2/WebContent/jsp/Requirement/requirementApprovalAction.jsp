<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->    
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="java.sql.Date" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<html>
  <head>
	  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
	  <title>TraceCloud.com</title>
	  	<link rel="stylesheet" type="text/css" href="/GloreeJava2/css/common.css"></link>
	  	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
		<script src="/GloreeJava2/js/userAccount.js?v=20200630"></script>
		<script src="/GloreeJava2/js/userDashboard.js?v=20200630"></script> 
	
	</head>
	<body>
		<div id='requirementApprovalActionDiv'>
			<div class='alert alert-success' style='width:1000px'>
<%

	String approvalAction = request.getParameter("approvalAction");
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));


	// When a user comes to this page, he is not initially authenticated. So
	// we have to use the followng logic.
	// if the authorization is ldap, then the user is already autenticated. We just build his security profile. 
	// if the authorization is database driven, then the user need sto authenticate himself.
	String authenticationType = this.getServletContext().getInitParameter("authenticationType");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	if ((authenticationType.equals("ldap")) && (securityProfile == null)){
		// this is an ldap user and already authenticated by app servers's ldap authentication. However
		// we don't know all the user's authorizations. So we go to userAccountAction to build out his security profile
		session.setAttribute("redirectAction", "approvalAction");
		RequestDispatcher dispatcher =	request.getRequestDispatcher("/servlet/UserAccountAction?action=signIn&requirementId=" + requirementId + "&approvalAction=" + approvalAction);
		dispatcher.forward(request, response);
		return;		
				
	}
	if (authenticationType.equals("database")){
		// if the user is not logged in, lets send him to the sign in page.
		String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
		if ((isLoggedIn == null) || (isLoggedIn.equals(""))){
			// not logged in . so redirect.
			// we need to change the startPage logic so that after successful log in the user
			// get redirected to displayAction.
			session.setAttribute("redirectAction", "approvalAction");
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/WebSite/startPage.jsp?requirementId=" + requirementId + "&approvalAction=" + approvalAction );
			dispatcher.forward(request, response);
			return;
		}
	}
			
	// if we are here, then the user is authenticated, and we have his entire profile with us.

	securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	User user = securityProfile.getUser();
	String databaseType = this.getServletContext().getInitParameter("databaseType");

	Requirement requirement = new Requirement(requirementId, databaseType);
	String url = ProjectUtil.getURL(request,requirementId,"requirement");
	
	// lets make sure that this user is a member in this project and 
	// he / she has approval permissions on this requirements.
	
	
	int projectId = requirement.getProjectId();
	Project project = new Project(projectId, databaseType);
	

	String colorCodedApprovers = "";
	String approvalStatusColor = "";
	
	
	
	
	if (securityProfile.getRoles().contains("MemberInProject" + projectId)){
		// we know that the user is a member of this project.
		if (approvalAction.equals("approve")){
			// lets make sure that the user has accept / reject permissions.
			if (securityProfile.getPrivileges().contains("approveRequirementsInFolder" 	+ requirement.getFolderId())){
				
				if ((requirement.getApprovalStatus().equals("In Approval WorkFlow"))){
					// approve the requirement
					RequirementUtil.approvalWorkFlowAction(requirementId, approvalAction, "", user, request, databaseType);	
					

					String serverName = request.getServerName();
					
					String mailHost = this.getServletContext().getInitParameter("mailHost");
					String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
					String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
					String smtpPort = this.getServletContext().getInitParameter("smtpPort");
					String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
					String emailUserId = this.getServletContext().getInitParameter("emailUserId");
					String emailPassword = this.getServletContext().getInitParameter("emailPassword");
					
					
					RequirementUtil.remindPendingApproversImmediately(requirementId,  serverName, request,
							mailHost,  transportProtocol,  smtpAuth,  smtpPort,  smtpSocketFactoryPort,  emailUserId,  emailPassword);
					
					
					// since the underlying requirement has changed, lets get a new object.
					requirement = new Requirement(requirementId, databaseType);
					///////////////////////////////////////Has to be repeated, as its values change based on action./////////////////////
					// if I were smart, I would make the following code a function, instead of repeating it 3 times.
					// lets make a color coded string of remaining approvers.
					
					String approversString = requirement.getApprovers();
					if ((approversString != null) && (approversString.contains(","))){
						String [] approvers = approversString.split(",");
						String color="";
						for (int i=0;i<approvers.length;i++){
							if (approvers[i].contains("(P)")){
								color="purple";
							}
							if (approvers[i].contains("(A)")){
								color="green";
							}
							if (approvers[i].contains("(R)")){
								color="red";
							}
							colorCodedApprovers += "  <font color='" +
								color + "'>" + 
								approvers[i] + 
								"</font>,";
						}
						// drop the last ,
						colorCodedApprovers = (String) colorCodedApprovers.subSequence(0,colorCodedApprovers.lastIndexOf(","));
					}
					if (colorCodedApprovers.equals("")){
						colorCodedApprovers = "N/A";
					}
					
					
					
					// lets calculate the color of approval status
					if (requirement.getApprovalStatus().equals("Draft")){ 
						approvalStatusColor="background-color:#FFFF66";	
					} 
					if (requirement.getApprovalStatus().equals("In Approval WorkFlow")){ 
						approvalStatusColor="background-color:#99ccff";	
					} 
					if (requirement.getApprovalStatus().equals("Approved")){ 
						approvalStatusColor="background-color:#CCFF99";	
					} 
					if (requirement.getApprovalStatus().equals("Rejected")){ 
						approvalStatusColor="background-color:#FFA3AF";	
					} 	
					///////////////////////////////////////Has to be repeated, as its values change based on action./////////////////////
					
				
				
											
%>
					<table class='paddedTable' width='900px'>
						<tr>
							<td colspan=2><span class='normalText'> You have  <font color='green'><b> Approved </b></font> this requirement</span></td>
						</tr>
						<tr>
							<td style='width:100px'><span class='normalText'> Requirement </span></td>
							<td><span class='normalText'> <b> <a href='<%=url%>' > <%=requirement.getRequirementFullTag() %></a></b>: <%=requirement.getRequirementName() %></span></td>
						</tr>
						<tr>
							<td style='width:100px'><span class='normalText'> Approval Status </span></td>
							<td ><span class='normalText' style='<%=approvalStatusColor%>'> <%=requirement.getApprovalStatus() %></span></td>
						</tr>
						<tr>
							<td style='width:100px'><span class='normalText'> Approvers </span></td>
							<td><span class='normalText'> <%=colorCodedApprovers %></span></td>
						</tr>
						
					</table>				
<%
				}
				else {
				%>
					This requirement  <b>  <a href='<%=url%>' > <%=requirement.getRequirementFullTag() %> </a></b> : "<%=requirement.getRequirementName() %> " is <font color='red'> Not </font>  in Approval Work Flow</b> status and is not eligible for Approval.
				<%
				}
				
			}
			else {
				%>
				You do not have <b>Approval / Rejection </b> permissions on this requirement  <b>  <a href='<%=url%>' > <%=requirement.getRequirementFullTag() %> </a> </b> : "<%=requirement.getRequirementName() %> " 
				<%
			}
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		if (approvalAction.equals("reject")){
			String approvalNote = request.getParameter("approvalNote");
			if ((approvalNote == null ) || (approvalNote.equals(""))){
				// To reject a requirement, a note has to be provided. 

				
				///////////////////////////////////////Has to be repeated, as its values change based on action./////////////////////
				// lets make a color coded string of remaining approvers.
				
				String approversString = requirement.getApprovers();
				if ((approversString != null) && (approversString.contains(","))){
					String [] approvers = approversString.split(",");
					String color="";
					for (int i=0;i<approvers.length;i++){
						if (approvers[i].contains("(P)")){
							color="purple";
						}
						if (approvers[i].contains("(A)")){
							color="green";
						}
						if (approvers[i].contains("(R)")){
							color="red";
						}
						colorCodedApprovers += "  <font color='" +
							color + "'>" + 
							approvers[i] + 
							"</font>,";
					}
					// drop the last ,
					colorCodedApprovers = (String) colorCodedApprovers.subSequence(0,colorCodedApprovers.lastIndexOf(","));
				}
				if (colorCodedApprovers.equals("")){
					colorCodedApprovers = "N/A";
				}
				
				
				
				// lets calculate the color of approval status
				if (requirement.getApprovalStatus().equals("Draft")){ 
					approvalStatusColor="background-color:#FFFF66";	
				} 
				if (requirement.getApprovalStatus().equals("In Approval WorkFlow")){ 
					approvalStatusColor="background-color:#99ccff";	
				} 
				if (requirement.getApprovalStatus().equals("Approved")){ 
					approvalStatusColor="background-color:#CCFF99";	
				} 
				if (requirement.getApprovalStatus().equals("Rejected")){ 
					approvalStatusColor="background-color:#FFA3AF";	
				} 	
				///////////////////////////////////////Has to be repeated, as its values change based on action./////////////////////
				
				
				%>
				
					<table class='paddedTable' width='900px'>
						
						
						<tr>
							<td style='width:100px'><span class='normalText'> Requirement </span></td>
							<td><span class='normalText'> <b> <a href='<%=url%>' > <%=requirement.getRequirementFullTag() %></a></b>: <%=requirement.getRequirementName() %></span></td>
						</tr>
						<tr>
							<td style='width:100px'><span class='normalText'> Approval Status </span></td>
							<td ><span class='normalText' style='<%=approvalStatusColor%>'> <%=requirement.getApprovalStatus() %></span></td>
						</tr>
						<tr>
							<td style='width:100px'><span class='normalText'> Approvers </span></td>
							<td><span class='normalText'> <%=colorCodedApprovers %></span></td>
						</tr>
						
						<tr><td colspan='2'>&nbsp;</td></tr>
						<tr>
							
							<td ></td>
							<td><span class='normalText'><b><font color='red'>Please enter a reason for rejection</font></b></input>
							</span></td>
						</tr>
						<tr>
							
							<td><span class='normalText'> Reason for rejection </span></td>
							<td>
								<span class='normalText'><input type='text' name='approvalNote' id='approvalNote' size='100'></input>
								&nbsp;
								<input type='button' name='Reject' value='Reject' onclick='requirementApprovalAction(<%=requirementId%>, "reject");'></input></span>
							</td>
							
						</tr>
						
					</table>					
				
				
				<%
			}
			else {
				// this reject request came with a rejection note. So
				
				// lets make sure that the user has accept / reject permissions.
				if (securityProfile.getPrivileges().contains("approveRequirementsInFolder" 	+ requirement.getFolderId())){
				
					if ((requirement.getApprovalStatus().equals("In Approval WorkFlow"))){
						// reject the requirement
						RequirementUtil.approvalWorkFlowAction(requirementId, approvalAction, approvalNote , user, request, databaseType);
						// since the underlying requirement has changed, lets get a new object.
						requirement = new Requirement(requirementId, databaseType);
						
						
						
						///////////////////////////////////////Has to be repeated, as its values change based on action./////////////////////
						// lets make a color coded string of remaining approvers.
						
						String approversString = requirement.getApprovers();
						if ((approversString != null) && (approversString.contains(","))){
							String [] approvers = approversString.split(",");
							String color="";
							for (int i=0;i<approvers.length;i++){
								if (approvers[i].contains("(P)")){
									color="purple";
								}
								if (approvers[i].contains("(A)")){
									color="green";
								}
								if (approvers[i].contains("(R)")){
									color="red";
								}
								colorCodedApprovers += "  <font color='" +
									color + "'>" + 
									approvers[i] + 
									"</font>,";
							}
							// drop the last ,
							colorCodedApprovers = (String) colorCodedApprovers.subSequence(0,colorCodedApprovers.lastIndexOf(","));
						}
						if (colorCodedApprovers.equals("")){
							colorCodedApprovers = "N/A";
						}
						
						
						
						// lets calculate the color of approval status
						if (requirement.getApprovalStatus().equals("Draft")){ 
							approvalStatusColor="background-color:#FFFF66";	
						} 
						if (requirement.getApprovalStatus().equals("In Approval WorkFlow")){ 
							approvalStatusColor="background-color:#99ccff";	
						} 
						if (requirement.getApprovalStatus().equals("Approved")){ 
							approvalStatusColor="background-color:#CCFF99";	
						} 
						if (requirement.getApprovalStatus().equals("Rejected")){ 
							approvalStatusColor="background-color:#FFA3AF";	
						} 	
						///////////////////////////////////////Has to be repeated, as its values change based on action./////////////////////
						
			 %>
					<table class='paddedTable' width='900px'>
						<tr>
							<td colspan=2><span class='normalText'> You have  <font color='red'><b> Rejected </b></font> this requirement</span></td>
						</tr>
						<tr>
							<td style='width:100px'><span class='normalText'> Requirement </span></td>
							<td><span class='normalText'> <b> <a href='<%=url%>' > <%=requirement.getRequirementFullTag() %></a></b>: <%=requirement.getRequirementName() %></span></td>
						</tr>
						<tr>
							<td style='width:100px'><span class='normalText'> Approval Status </span></td>
							<td ><span class='normalText' style='<%=approvalStatusColor%>'> <%=requirement.getApprovalStatus() %></span></td>
						</tr>
						<tr>
							<td style='width:100px'><span class='normalText'> Approvers </span></td>
							<td><span class='normalText'> <%=colorCodedApprovers %></span></td>
						</tr>
						
					</table>							
			<%
					}
					else {
						%>
						This requirement  <b>  <a href='<%=url%>' > <%=requirement.getRequirementFullTag() %> </a> </b> : "<%=requirement.getRequirementName() %> " is <b><font color='red'> Not </font> in Approval Work Flow</b> status and is not eligible for Approval.
						<%
					}
				}
				else {
					%>
					You do not have <b>Approval / Rejection </b> permissions on this requirement  <b>  <a href='<%=url%>' > <%=requirement.getRequirementFullTag() %> </a> </b> : "<%=requirement.getRequirementName() %> " 
					<%
				}
			}
			
		}
		
%>






<%} %>
		</div>
	</div>
</body>

</html>
