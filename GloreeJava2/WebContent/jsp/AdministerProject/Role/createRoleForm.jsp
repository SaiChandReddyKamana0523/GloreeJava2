<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String createRoleFormIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((createRoleFormIsLoggedIn == null) || (createRoleFormIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }

	Project project= (Project) session.getAttribute("project");
	// lets see if this user is an admin of this project.
	boolean cRFIsAdmin = false;
	SecurityProfile cRFSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	String powerUserSettings = project.getPowerUserSettings();
	if (
			(cRFSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())
			||
			(
			(cRFSecurityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
				&&
				(powerUserSettings.contains("Manage Roles"))
			)
		)){
		cRFIsAdmin = true;
	}
	
%>


<!--  A user needs to be an admin to createRole -->
<%if (cRFIsAdmin){ %>
	<%@ page import="java.util.*" %>
	<%@ page import="javax.servlet.http.HttpSession"  %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<%
		String status = (String) request.getAttribute("status");
	%>
	
	<div id='createRoleDiv' class='level1Box'>
	
	<form method="post" id="createRoleForm" action="">
		
	<table class='paddedTable' width='100%'>
		<tr>
			<td align='left' colspan='2' bgcolor='#99CCFF'>				
				<span class='subSectionHeadingText'>
				Create A New Role
				</span>
				<div style='float:right'>
					<a href='/GloreeJava2/documentation/help/administerAProject.htm' target='_blank'>
					<img src="/GloreeJava2/images/page.png"   border="0">
					</a>	
					&nbsp;&nbsp;
				</div>
			</td>
		</tr>	
		<%
		if ((status != null) && (status.equals("roleName already used"))) {
		%>
		<tr>
			<td colspan="2">
			<div id='roleNameAlreadyUsedMessage' class='alert alert-success'>
			Another <img src="/GloreeJava2/images/role16.png" border="0"> Role already exists with 
			the same Name. Please choose a different Name.
			</div>
			</td> 
		</tr>
		<% 
		} 
		%>
		<tr> 
			<td>
				<span class='headingText'>Name</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td> 
				<input type="text" name="roleName" size="30" maxlength="100"> 
			</td>
		</tr>
		<tr> 
			<td>
				<span class='headingText'>Description</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td>
				<textarea name="roleDescription" rows="5" cols="50" ></textarea>
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
					<option value="ApprovalByAll" SELECTED> All Members Must Respond</option>
					<option value="ApprovalByAny"> Any One Response is sufficient</option>
				</select>
				</span>
				<a href='#' onClick='document.getElementById("approvalTypeDiv").style.display="block"'>
				More Info </a>
				<br>
				<div id='approvalTypeDiv' style='width:600px; display:none' class='alert alert-danger' >
					<div style='float:right'>
						<input type='button' class='btn btn-xs btn-danger' value='Close'
						onclick='document.getElementById("approvalTypeDiv").style.display="none"'>
					</div>
					<span class='headingText'>
						If this role is set as an APPROVER role for any folder,
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
				<input type="text" name="approvalRank" value='1' size="3" maxlength="3"> 
				</span>
				<a href='#' onClick='document.getElementById("approvalRankDiv").style.display="block"'>
				More Info </a>
				<br>
				<div id='approvalRankDiv'  style='width:600px; display:none' class='alert alert-danger' >
					<div style='float:right; '>
						<input type='button' class='btn btn-xs btn-danger' value='Close'
						onclick='document.getElementById("approvalRankDiv").style.display="none"'>
					</div>
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
					<input type="button"  class='btn btn-sm btn-primary' name="Create New Role" 
					value="Create Role" onClick="createRole(this.form)">
					
				</span>
			</td>
		</tr> 		
	</table>
	
	</form>
	</div>
<%}%>