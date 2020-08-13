<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);
	int projectId = requirement.getProjectId();
	
	// authentication only
	String displayRequirementCoreIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayRequirementCoreIsLoggedIn == null) || (displayRequirementCoreIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	String action = request.getParameter("action");
	if (action == null ) {action = "";}
		
		
	boolean isMember = false;
	boolean isAdmin = false;
	boolean isUpdater = false;
	if (securityProfile.getRoles().contains("AdministratorInProject" + projectId)){
		isAdmin = true;
	}
	
	if (securityProfile.getPrivileges().contains("updateRequirementsInFolder" + requirement.getFolderId())){
		isUpdater = true;
	}
	
	if(isAdmin || isUpdater){
		// can add dynamic roles
		if (action.equals("setDynamicApprover")){
			// lets set the dynamic approver for this req. 
			int dynamicApprovalRole = Integer.parseInt(request.getParameter("dynamicApprovalRole"));
			int dynamicApprovalRank = Integer.parseInt(request.getParameter("dynamicApprovalRank"));
			Role role = new Role(dynamicApprovalRole);
			RequirementUtil.addDynamicApprovalRoleShell( requirement, role , dynamicApprovalRank);
			RequirementUtil.refreshRequirementApprovalHistory(requirementId,  databaseType);
		}
	}
	if (isAdmin){
		// can remove dynamic roles
		if (action.equals("removeDynamicApprover")){
			// lets set the dynamic approver for this req. 
			int dynamicApprovalRole = Integer.parseInt(request.getParameter("dynamicApprovalRole"));
			Role role = new Role(dynamicApprovalRole);
			RequirementUtil.removeDynamicApprovalRoleShell(requirement, role);
			RequirementUtil.refreshRequirementApprovalHistory(requirementId,  databaseType);
		}
		
	}
	if (securityProfile.getRoles().contains("MemberInProject" + projectId)){
		isMember = true;
	}
	
	if (isMember){		
		ArrayList roles = RoleUtil.getRoles(requirement.getProjectId()); 
%>

	<% 
	
	   	String divId = request.getParameter("divId");
	%>
	<div class='alert alert-success' >
	
	
	<table class='table' border='1'>
		<tr>
			<td colspan='2'>
				<div class='alert alert-info'>Please note that only users with update permissions on this folder can add dynamic approvers. Only administrators can remove dynamic approvers from a requirement</div>
			</td>
		</tr>
		<% if (isAdmin || isUpdater) { %>
		<tr >
			<td title="Use this feature to dynamically add approver roles to individual requirements" >
     					<span class="normalText">Set Dynamic Approval Role <font color='red'>
     					 
			</td>
			<td >
				<span class='normalText'>
				<select name="setDynamicApprovalRole<%=requirement.getRequirementId() %>" id="setDynamicApprovalRole<%=requirement.getRequirementId() %>">
					<%
						
						Iterator r = roles.iterator();
						while (r.hasNext()){
							Role role = (Role) r.next();
						%>
							<option value='<%=role.getRoleId() %>'><%=role.getRoleName()%></option>
						<%
					}
					%>
				</select>
			
			&nbsp;&nbsp;&nbsp;&nbsp;
				Approval Rank &nbsp;&nbsp;<input type='text' value='1' id='dynamicApprovalRank<%=requirement.getRequirementId() %>' size='3'>				
			
												
			
			&nbsp;&nbsp;&nbsp;&nbsp;
				<input type='button' class='btn btn-xs btn-primary' value='Update' 
					onclick='setDynamicApprover(<%=requirement.getRequirementId() %> )'>
				
				</span>
			</td>
		</tr>
		<%} %>
		<%if (isAdmin){ %>
		<tr>
			<td title="Use this feature to dynamically add approver roles to individual requirements style='width:150px'">
     					<span class="normalText">Remove Dynamic Approval Role</span>
			</td>
			<td>
				<span class='normalText'>
				<select name="removeDynamicApprovalRole<%=requirement.getRequirementId() %>" id="removeDynamicApprovalRole<%=requirement.getRequirementId() %>">
					<%
						
						Iterator r = roles.iterator();
						while (r.hasNext()){
							Role role = (Role) r.next();
						%>
							<option value='<%=role.getRoleId() %>'><%=role.getRoleName()%></option>
						<%
					}
					%>
				</select>					
				
				&nbsp;&nbsp;&nbsp;&nbsp;								
			
				<a href='#'  class='btn btn-xs btn-primary'  style='color:white' 
					onclick='removeDynamicApprover(<%=requirement.getRequirementId() %> )'>
				Update</a> 
				</span>
			</td>
		</tr>
		<%} %>				
	</table>
	
	
	<div class="panel panel-primary">
      <div class="panel-heading">Dynamic Approval Roles <%=requirement.getRequirementFullTag() %></div>
      <div class="panel-body">
      
      <%
		ArrayList<Role> dynamicRoles = requirement.getDynamicApprovalRoles();
		for (Role role : dynamicRoles){
			%>
			<%=role.getRoleName() %>&nbsp;&nbsp;(Approval Rank : <%=role.getApprovalRank() %>)	<br>					 							
			<%
		}
		%>
      
      
      </div>
    </div>
    
		
	</div>
<%}%>