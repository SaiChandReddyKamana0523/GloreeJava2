<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>
<head>

	<!--  Since this page is likely shown in a different window, we need to load the css and java script files. -->
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/css/common.css"> 
	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userAccount.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userDashboard.js?v=20200630"></script>
	
	
</head>
<body onLoad='document.getElementById("googleSearchString").focus();'>
<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	User user = securityProfile.getUser();
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);
	
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	
	// you need to be a member of this project to see this
	if (isMember ){
%>



	<table class='paddedTable'>
	
	
	<%
	// If this user has already rejected this req ,we display a note to that effect
	// and hide the 'reject' button
	boolean alreadyRejected = false;
	boolean stillPending = false;
	if ((requirement.getApprovers().contains("(R)" + user.getEmailId()))){
		alreadyRejected = true;
	}
	if ((requirement.getApprovers().contains("(P)" + user.getEmailId()))){
		stillPending = true;
	}
	
	String nextRoleNameForUser = "";

	boolean userCanApprove = false;
	try {
		// lets see if the role this user is trying to approve for, is the currentRole in approval cycle
		nextRoleNameForUser = requirement.getNextRoleToApproveForUser(user.getEmailId());
		Role nextRoleToApproveForReq = requirement.getNextRoleToApprove();
		
		
		// get roles at the same level as this role 
		// and if this users' next role is any of these, let them go ahead with approval
		ArrayList<Role> nextRolesToApproveForReq =  ProjectUtil.getRolesAtSameLevel(requirement.getRequirementId(), requirement.getFolderId(),nextRoleToApproveForReq );
		// lets iterate through all these roles and see if this user can approve.
		// set userCanApprove to false. loop through all the roles 
		// if this user's next role is in any of thenextRolesTo, then set it true
		
		
		for (Role role : nextRolesToApproveForReq){
			
			if (nextRoleNameForUser.equals(role.getRoleName())){
				
				userCanApprove = true;
			}
		}
		
		
		if (!(userCanApprove)){
			if (nextRoleToApproveForReq!= null){
			%>
			<tr>
				<td colspan='2'><div class='alert alert-danger'>
					This requirement is still Pending Approval by Role : <%=nextRoleToApproveForReq.getRoleName() %>
				</div></td></tr>
			<%
			}
		}
		
	}
	catch (Exception e){
		e.printStackTrace();
	}
	%>
		<tr>
			<td colspan='2'>
				<div style='float: left;'>
					<span class='headingText'>  
						<b>Acceptance WorkFlow Action for role : <font color='red'> <%=nextRoleNameForUser %></font> by   
						<font color='red'> <%=user.getFirstName() %>&nbsp;<%=user.getLastName() %></font> </b> 
					</span> 
				</div>		        						
			</td>
		</tr>
		<tr>
			<td colspan='2'>
				<div id='approversTableDivInApproveRejectForm' style='display:none'></div>		        						
			</td>
		</tr>
		<%if (userCanApprove){ %>
			<tr>
				<td>
					<span class='headingText'>
						Notes
					</span>
				</td>
				<td>
					
					<textarea  name="approvalNote<%=requirement.getRequirementId()%>" id="approvalNote<%=requirement.getRequirementId()%>" rows='4' cols='150'></textarea>
					</td>
				</tr>
				<tr>
					<td colspan='2' align='center'>
						<span class='normalText'>
							<input type='button' class='btn btn-primary btn-sm'   name='approve' value='Accept Now'
							onclick='
							var approvalNoteValue = document.getElementById("approvalNote<%=requirement.getRequirementId()%>").value;
							approvalWorkFlowAction(approvalNoteValue,<%=requirement.getRequirementId()%>,"approve");'>
						</span>	
						&nbsp;&nbsp;
						<%
						// we show the Reject link , only if the user has not already rejected it.
						// i.e its still pending.
						if (stillPending){
						%>
						<span class='normalText'>
								<input type='button' class='btn btn-primary btn-sm'   name='reject' value='Reject Now'
								onclick='
									var approvalNoteValue = document.getElementById("approvalNote<%=requirement.getRequirementId()%>").value;
									approvalWorkFlowAction(approvalNoteValue,<%=requirement.getRequirementId()%>,"reject");'>
						</span> 
						
						<%} %>
						
						&nbsp;&nbsp;
						
						<input type='button' class='btn btn-danger btn-sm'   name='Cancel' value='Cancel'
								onclick="document.getElementById('approveRejectDiv').style.display = 'none';">
					</td>
				</tr>
		<%} %>
	</table>
		        							
		    	 
   <%	 
   }
   %>
	
</body>

