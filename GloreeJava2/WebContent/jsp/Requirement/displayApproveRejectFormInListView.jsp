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
	Requirement r = new Requirement(requirementId, databaseType);
	
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	
	boolean isUserAnAdmin = false; 
	if (securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
		isUserAnAdmin = true;
	}
	
	
	// you need to be a member of this project to see this
	if (isMember ){
		
		%>
		
		<table class='paddedTable'>
		
		<%
		String nextRoleNameForUser = "";

		boolean userCanApprove = false;
		try {
			// lets see if the role this user is trying to approve for, is the currentRole in approval cycle
			nextRoleNameForUser = r.getNextRoleToApproveForUser(user.getEmailId());
			Role nextRoleToApproveForReq = r.getNextRoleToApprove();
			// get roles at the same level as this role 
			// and if this users' next role is any of these, let them go ahead with approval
			ArrayList<Role> nextRolesToApproveForReq =  ProjectUtil.getRolesAtSameLevel(r.getRequirementId(), r.getFolderId(),nextRoleToApproveForReq );
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
		catch(Exception e){
			e.printStackTrace();
		}
		
%>


					
					<tr>
						<td colspan='2'>
  					<div style='float: left;'>
  						<span class='headingText'>  
  							<b>Approval WorkFlow Action for role : <font color='red'> <%=nextRoleNameForUser %></font> by   
							<font color='red'> <%=user.getFirstName() %>&nbsp;<%=user.getLastName() %></font> </b> 
  						</span> 
  						
  						
  						<%	if (isUserAnAdmin){%>
							&nbsp;&nbsp;
					 		<input type='button' class='btn btn-sm btn-danger' value='Bypass'
					 		onclick="document.getElementById('fixRejectDiv<%=r.getRequirementId()%>').style.display = 'block';">
						<%} %>
  					</div>		        						
						</td>
					</tr>
					
					
					<tr>
						<td colspan='2'>
							<div id='approversTableDivInApproveRejectForm<%=requirementId%>' style='display:none'></div>		        						
						</td>
					</tr>
					
					
					<%if (userCanApprove) { %>
					
						<tr>
							<td>
								<span class='headingText'>
									Notes
								</span>
							</td>
							<td>
								<span class='normalText'>
									<textarea name="approvalNote<%=r.getRequirementId()%>" 
									id="approvalNote<%=r.getRequirementId()%>" rows="4" cols="100" ></textarea>
								</span>
							</td>
						</tr>
						<tr>
							<td colspan='2' align='center'>
							
								
									<input type="button" class="btn btn-primary btn-sm" 
									 style='height:25px; min-width:180px'  
									 name='approve' value='Accept Now'
									onClick='
									handleRequirementActionOther(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,"approve");
									'>
								
								&nbsp;&nbsp;
								
								
									<input type="button" class="btn btn-primary btn-sm" 
									 style='height:25px; min-width:180px'  
									 name='reject' value='Reject Now'
									onClick='
									handleRequirementActionOther(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,"reject");
									'	>
								
								
								&nbsp;&nbsp;
					
								<input type='button' class='btn btn-danger btn-sm'   name='Cancel' value='Cancel'
									onclick="document.getElementById('approveRejectDiv<%=r.getRequirementId()%>').style.display = 'none';">
								
							</td>
						</tr>
					<%} %>
	</table>
					        							
		    	 
   <%	 
   }
   %>
	
</body>

