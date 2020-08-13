
<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>


<%
	// authentication only
	String addUsersToRoleIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((addUsersToRoleIsLoggedIn == null) || (addUsersToRoleIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is an admin of this project.
	boolean aUTRFIsAdmin = false;
	SecurityProfile aUTRFSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (
			(aUTRFSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))
			||
			(aUTRFSecurityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
		){
		aUTRFIsAdmin = true;
	}
%>


<% if(aUTRFIsAdmin){ %>
	<%@ page import="java.util.*" %>
	<%@ page import="javax.servlet.http.HttpSession"  %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	
	<%
		
		Role role = null;
		
		try {
			int roleId = 0;
			String roleIdString = (String) request.getParameter("roleId");
			roleId = Integer.parseInt(roleIdString);
			role = new Role(roleId);	
		}
		catch (Exception e){
			e.printStackTrace();
		}

		
		try {
			if (role == null){
				// that means this call from 'UpdateRoleNameandDescription' process. In that case Role is sent as an object.
				role = (Role) request.getAttribute("Role");
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		String roleName = role.getRoleName();
		String roleDescription = role.getRoleDescription();
		String approvalType = role.getApprovalType();
		int approvalRank = role.getApprovalRank();
		
		if (roleName.contains("'")){
			roleName = roleName.replace("'","");
		}
		

		if (roleDescription.contains("'")){
			roleDescription = roleDescription.replace("'","");
		}
		
		String approvalByAllSelected = "";
		String approvalByAnySelected = "";
		if (approvalType.equals("ApprovalByAll")){
			approvalByAllSelected = "SELECTED";
		}
		if (approvalType.equals("ApprovalByAny")){
			approvalByAnySelected = "SELECTED";
		}
		
	%>
	
	<div id='addUserToRoleDiv' class='level1Box'>
	
	<form method="post" id="editRoleForm" action="">
		<input type='hidden' name='roleId' value='<%=role.getRoleId() %>'>	
	<table class='paddedTable' width='100%'>
		<tr>
			<td colspan="2" align='left'  >
				<span class='subSectionHeadingText'>
				<b>Edit Role</b>
				</span>
			</td> 
		</tr>
	
		
		<tr> 
			<td>
				<span class='headingText'>Name</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td> 
				<input type="text" name="roleName" value="<%=roleName%>" size="30" maxlength="100"> 
			</td>
		</tr>
		<tr> 
			<td>
				<span class='headingText'>Description</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td>
				<textarea name="roleDescription" rows="5" cols="50" ><%=roleDescription %></textarea>
			</td>
			
		</tr>	
		
		

		<tr> 
			<td>
				<span class='headingText'>Approval Type</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td> 
				<span class='headingText'>
				<select name="approvalType" id="approvalType">
				
					<option value="ApprovalByAll" <%=approvalByAllSelected %>> All Members Must Respond</option>
					<option value="ApprovalByAny" <%=approvalByAnySelected %>> Any One Response is sufficient</option>
				</select>
				</span>
				<a href='#' onClick='document.getElementById("approvalTypeDiv").style.display="block"'>
				More Info </a>
				<br>
				<div id='approvalTypeDiv'  class='alert alert-danger' style='display:none;'>
					<span class='headingText'>
						If this role is set as an Approver role for a folder, 
						then this flag controls whether ALL members must respond
						or if its sufficient to have ANY ONE member's response
					</span>
				</div>
							
			</td>
		</tr>	
		
		

		<tr> 
			<td>
				<span class='headingText'>Approval Rank</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td> 
				<span class='headingText'>
				<input type="text" name="approvalRank" value="<%=approvalRank%>" size="3" maxlength="3"> 
				</span>
				<a href='#' onClick='document.getElementById("approvalRankDiv").style.display="block"'>
				More Info </a>
				<br>
				<div id='approvalRankDiv'  class='alert alert-danger' style='display:none;'>
					<span class='headingText'>
						When a folder is configured for Approval Work Flow, then  approval is required from members of different roles.
						<br><br>
						TraceCloud uses the Role's Approval Rank to determine which roles should approve before others.
						<br><br>
						If all roles have the same Approval Rank, then all the roles are treated as equal and they all get the request to approve
						at the same time.
						<br><br>
						Otherwise, roles with lowest rank are asked to approve first. 	
					<br><br>
						<b>If a lower level group rejects the approval, then the approval process is terminated and the object is 
						marked as Rejected. This prevents un-necessary approval requests up the chain.</b>
						 
						 
					</span>
				</div>
							
			</td>
		</tr>				
		<tr>
			<td colspan=2 align="left">
				<span class='normalText'>
					<input type="button" class="btn btn-sm btn-primary" name="Update Role" 
					value="Update Role" onClick="updateRole(this.form)">
					
				</span>
			</td>
		</tr> 	

	</table>
	
	</form>
	</div>
<%}%>