
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
		
		String roleId = (String) request.getParameter("roleId");

		// lets display the incorrectEmailId error message.
		String inCorrectDomainEmailIds = (String) request.getAttribute("inCorrectDomainEmailIds");
		String statusMessage = "";
		if ((inCorrectDomainEmailIds != null) && (!(inCorrectDomainEmailIds.equals("")))) {
			statusMessage = "<tr> " +
				" <td colspan='2'> " +
				" <div  class='alert alert-success'> " +
				" <span class='normalText'> " + 
				" The following email Id do not fit the Restricted Domain '" +
				 project.getRestrictedDomains() + "' .<br> Please try a different " +
				" email id or work with the Administrator of this Project to add additional domains. <br>" ;
				
			String [] emailIds = {inCorrectDomainEmailIds};
			if (inCorrectDomainEmailIds.contains(",")){
				emailIds = inCorrectDomainEmailIds.split(",");
			}
			for (int i=0; i<emailIds.length; i++){
				statusMessage += "<br>" + emailIds[i];  
			}
			statusMessage += " </span> </div> " + 
				" </td> " + 
				" </tr>";
		}

		

		// lets display the invited message.
		String invitedEmailIds = (String) request.getAttribute("invitedEmailIds");
		
		if ((invitedEmailIds != null) && (!(invitedEmailIds.equals("")))) {
			statusMessage += "<tr> " +
				" <td colspan='2'> " +
				" <div  class='alert alert-success'> " +
				" <span class='normalText'> " + 
				" The user you added is currently not in the tracecloud system." +
				  "<br> We have sent them an email and when they open an account  " +
				" they will automatically have access to this project.<br>" ;
				
			String [] emailIds = {invitedEmailIds};
			if (invitedEmailIds.contains(",")){
				emailIds = invitedEmailIds.split(",");
			}
			for (int i=0; i<emailIds.length; i++){
				statusMessage += "<br>" + emailIds[i];  
			}
			statusMessage += " </span> </div> " + 
				" </td> " + 
				" </tr>";
		}

		// lets display the successfullyAdded message.
		String successfullyAddedEmailIds = (String) request.getAttribute("successfullyAddedEmailIds");
		
		if ((successfullyAddedEmailIds != null) && (!(successfullyAddedEmailIds.equals("")))) {
			statusMessage += "<tr> " +
				" <td colspan='2'> " +
				" <div  class='alert alert-success'> " +
				" <span class='normalText'> " + 
				" This user is already a member of the the TraceCloud system." +
				"<br> We have sent them an email and when they log in" +
				" they should see these projects in their dashboards.<br>" ;
				
			String [] emailIds = {successfullyAddedEmailIds};
			if (successfullyAddedEmailIds.contains(",")){
				emailIds = successfullyAddedEmailIds.split(",");
			}
			for (int i=0; i<emailIds.length; i++){
				statusMessage += "<br>" + emailIds[i];  
			}
			statusMessage += " </span> </div> " + 
				" </td> " + 
				" </tr>";
		}		
		
	%>
	
	<div id='addUserToRoleDiv' class='level1Box'>
	
	<form method="post" id="addUserToRoleForm" action="">
		<input type='hidden' name='roleId' value='<%=roleId%>'>	
	<table class='paddedTable' width='100%'>
		<tr>
			<td colspan="2" align='left'  >
				<span class='subSectionHeadingText'>
				<b>Add a User to this Role</b>
				</span>
			</td> 
		</tr>
	
		<%=statusMessage %>

		<tr> 
			<td>
				<span class='headingText'>Email Addresses (Eg: jack@apple.com,jill@apple.com)</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td> 
				<input type="text" name="emailIds" size="60" > 
			</td>
		</tr>
		<tr>
			<td colspan=2 align="left">
				<span class='normalText'>
					<input type="button"  class="btn btn-sm btn-primary" name="Add User" 
					value="Add Users" onClick="addUsersToRole(this.form)">
					
				</span>
			</td>
		</tr> 	
	</table>
	
	</form>
	</div>
<%}%>