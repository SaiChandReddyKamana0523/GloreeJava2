<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn  == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User user = securityProfile.getUser();
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)

	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
		
		int sprintId = Integer.parseInt(request.getParameter("sprintId"));
		Sprint sprint = new Sprint(sprintId, databaseType);
%>

	<table>
		<tr>
		<td>
		<table >
			<tr>
				<td  align='left'>
					<div >&nbsp;</div>
				</td>
			</tr>	

			<tr>
				<td  align='center' valign='center'  width='160' height='50'
					class = 'focusTab' >	
					<div >		 
		        		
						<img src="/GloreeJava2/images/iteration.png"  width="16" border="0">
						 <span class='normalText'>
		        		 Sprint Info
		        		 </span>
		        		 
		        	</div>
        		</td>

 				<td  align='center' valign='center'  width='160' height='50'
					class = 'nonFocusTab' >	
					<div >		 
		        		<a href='#'	onClick='displayAgileBacklog(<%=sprint.getProjectId()%>, <%=sprint.getSprintId()%>)'>
		        		<img src="/GloreeJava2/images/tasks.png" border="0">
			    		 Backlog
		        		 </a> 
		        	</div>
        		</td>

        		
 				<td  align='center' valign='center'  width='160' height='50'
					class = 'nonFocusTab' >	
					<div >		 
		        		<a href='#'	onClick='displayAgileDailyScrum(<%=sprint.getProjectId()%>, <%=sprint.getSprintId()%>)'>
		        		<img src="/GloreeJava2/images/calendar16.png" border="0">
			    		 Daily Scrum
		        		 </a> 
		        	</div>
        		</td>

 				<td align='center' valign='center'  width='160' height='50'
					class = 'nonFocusTab' >	
					<div >		 
		        		<a href='#'	onClick='displayAgileScrumNotes(<%=sprint.getProjectId()%>, <%=sprint.getSprintId()%>)'>
		        		<img src="/GloreeJava2/images/page.png" border="0">
		        		 Scrum Notes
		        		 </a> 
		        	</div>
        		</td> 		
        		
        		 <td  align='center' valign='center'  width='160' height='50'
					class = 'nonFocusTab' >	
					<div >		 
		        		<a href='#'	onClick='displayAgileScrumDashboard(<%=sprint.getProjectId()%>, <%=sprint.getSprintId()%>)'>
		        		<img src="/GloreeJava2/images/color_swatch16.png" border="0">
		        		 Dashboard
		        		 </a> 
		        	</div>
        		</td>		
			</tr>
		</table>
		</td>
		</tr>
		
		
		<tr>
		<td>
		<table>	
			<tr>
				<td >
					<div>
						<div id='createRequirementTypeDiv' class='level1Box'>
						
	
						<form method="post" id="updateSprintForm" action="">
						<input type='hidden' name='projectId' id='projectId' value='<%=project.getProjectId()%>'></input>
						<input type='hidden' name='sprintId' id='sprintId' value='<%=sprint.getSprintId() %>'></input>
							
						<table class='paddedTable' width='100%'>
							<tr>
								<td align='left' colspan='2' bgcolor='#99CCFF'>				
									<span class='subSectionHeadingText'>
									Sprint Information
									</span>
								</td>
							</tr>	
						
							<tr>
								<td colspan='2'  ><div id='updateAgileSprintMessage' class='alert alert-success' style='display:none'> ></div>
								
								</td>
							</tr>					
			
							<tr> 
								<td>
									<span class='headingText'>Sprint Name</span>
									<sup><span style="color: #ff0000;">*</span></sup> 
								</td>
								<td> 
									<span class='headingText'>
									<input type="text" name="sprintName" size="30" maxlength="100" value="<%=sprint.getSprintName()%>">
									</span> 
								</td>
							</tr>
							<tr> 
								<td>
									<span class='headingText'>Sprint Description</span>
									<sup><span style="color: #ff0000;">*</span></sup> 
								</td>
								<td>
									<span class='headingText'>
									<textarea name="sprintDescription" rows="10" cols="100" ><%=sprint.getSprintDescription() %></textarea>
									</span>
								</td>
							</tr>	
							
							<tr> 
								<td>
									<span class='headingText'>Scrum Master</span>
									<sup><span style="color: #ff0000;">*</span></sup> 
								</td>
								<td> 
									<span class='headingText'>
									<input type="text" name="scrumMaster" value="<%=sprint.getScrumMaster() %>" size="30" maxlength="100">
									</span> 
								</td>
							</tr>
							
							<tr> 
								<td>
									<span class='headingText'>Sprint Start Date</span>
									<sup><span style="color: #ff0000;">*</span></sup> 
								</td>
								<td> 
									<span class='headingText'>
									<input type="text" name="sprintStartDt"  size="10" maxlength="10" value="<%=sprint.getSprintStartDt()%>"> (MM/DD/YYYY)
									</span> 
								</td>
							</tr>
							
							
							<tr> 
								<td>
									<span class='headingText'>Sprint End Date</span>
									<sup><span style="color: #ff0000;">*</span></sup> 
								</td>
								<td> 
									<span class='headingText'>
									<input type="text" name="sprintEndDt"  size="10" maxlength="10" value="<%=sprint.getSprintEndDt() %>"> (MM/DD/YYYY)
									</span> 
								</td>
							</tr>
							<tr>
								<td colspan=2 align="left">
									<span class='normalText'>
										<input type="button"  class='btn btn-primary btn-sm' name="updateAgileSprintButton" id = "updateAgileSprintButton"
										value="Update Sprint" onClick="updateAgileSprint()">
										
										&nbsp;&nbsp;&nbsp;&nbsp;
										
										<input type="button" class='btn btn-danger btn-sm' name="deleteAgileSprintButton" id = "deleteAgileSprintButton"
										value="Delete Sprint" 
										onClick='document.getElementById("deleteAgileSprintConfirmationDiv").style.display = "block";'>
										
									</span>
								</td>
							</tr> 	
						 	<tr>
								<td colspan=2 align="left">
									<div id='deleteAgileSprintConfirmationDiv' style='display:none'>
										<%if (
												(sprint.getScrumMaster().equals(user.getEmailId())
												||
												(securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())))
											) {
											// the user is a scrum master of this sprint or an admin of this project. so can delete the sprint.
										%>
											<span class='normalText'>
											Please note that when a sprint is deleted, all the Notes and Metrics for this Sprint are removed, and any associated 
											Requirements will be removed from this Sprint.
											<br></br>
											<input type="button" class='btn btn-danger btn-sm' name="confirmDeleteAgileSprintButton" id = "confirmDeleteAgileSprintButton"
											value="Confirm Delete Sprint" 
											onClick='deleteAgileSprint()'>
											</span>
										<%} 
										else {%>
											<span class='normalText'>
											Please note that only the Scrum Master (<%=sprint.getScrumMaster() %>) or the project administrator 
											can delete a Sprint.
											</span>
										<%} %>
										
									</div>
								</td>
							</tr> 	
						
						</table>
						
						</form>
						</div>
					</div>
				</td>
			</tr>				
		</table>
		</td>
		</tr>
		</table>
	
	
<%}%>