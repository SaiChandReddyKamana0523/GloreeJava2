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
		
		int requirementId = Integer.parseInt(request.getParameter("requirementId"));
		Requirement requirement = new Requirement (requirementId, databaseType);
		
		String borderColor="white";
		String subBorderColor = "white";
		
		String taskStatus = requirement.getAttributeValue("Agile Task Status");
		if (taskStatus.equals("Not Started")){
			taskStatus = "notStarted";
			borderColor = "gray"; 
			subBorderColor = "#CCCCCC";
		} 
		if (taskStatus.equals("In Progress")){ 
			taskStatus = "inProgress";
			borderColor = "orange"; 
			subBorderColor = "#FFCC66";
		}
		if (taskStatus.equals("Blocked")){ 
			taskStatus = "blocked";
			borderColor = "red"; 
			subBorderColor = "#FF9966";
		}
		if (taskStatus.equals("Completed")){ 
			taskStatus = "completed";
			borderColor = "green"; 
			subBorderColor = "#99FF99";
		}

		ArrayList membersInProject = project.getMembers();
		
	
		String displayRDInReportDiv = "displayRDInReportDiv" + requirement.getRequirementId();
	
		// lets put spacers here for child requirements.
		  String req = requirement.getRequirementFullTag();
	   	  
   		
   		  String disabledString = "";
	   		if (!( securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
	    			+ requirement.getFolderId()))){
	   			disabledString = "DISABLED";					
			}
	   		String scrumActionDropDownName = "scrumAction" + requirement.getRequirementId();
	   		String requirementDivName = "requirementDiv" + requirement.getRequirementId();
	   		
	   		String  moveToAnotherSprintSubDivName = "moveToAnotherSprintSubDiv" + requirement.getRequirementId();
	   		String actionSubDivName = "actionSubDiv" + requirement.getRequirementId();
	   		String effortRemainingSubDivName = "effortRemainingSubDiv" + requirement.getRequirementId();
	   		String totalEffortSubDivName = "totalEffortSubDiv" + requirement.getRequirementId();
	   		String taskWeightSubDivName = "taskWeightSubDiv" + requirement.getRequirementId();
	   		
	   		String moveToAnotherSprintName = "moveToAnotherSprint" + requirement.getRequirementId();
	   		String ownerDropDownName = "ownerDropDown" + requirement.getRequirementId();
	   		String effortRemainingName = "effortRemaining" + requirement.getRequirementId();
	   		String totalEffortName = "totalEffort" + requirement.getRequirementId();
	   		
	   		String taskWeightName = "taskWeight" + requirement.getRequirementId();
	   		String taskStatusName = "taskStatus" + requirement.getRequirementId();
			
	   		int taskWeight = 0;
			try {
				taskWeight = Integer.parseInt(requirement.getAttributeValue("Agile Task Weight"));
			}
			catch (Exception e){
			}
			
	   		int effortRemaining = 0;
	   		try {
	   			effortRemaining = Integer.parseInt(requirement.getAttributeValue("Agile Effort Remaining (hrs)"));
	   		}
	   		catch (Exception e){
	   		}
	   		
	   		int totalEffort = 0;
	   		try {
	   			totalEffort = Integer.parseInt(requirement.getAttributeValue("Agile Total Effort (hrs)"));
	   		}
	   		catch (Exception e){
	   			
	   		}		   		
			%>			

				<div id = "<%=requirementDivName %>" name="<%=requirementDivName%>">
					<table class='paddedTable' border='2' bordercolor="<%=borderColor%>" width='240'>
					
						<tr>
							<td align='left' bgcolor='<%=subBorderColor%>'>	
								<div style='float:left;'>	
									<span class='normalText' title='<%=effortRemaining %> hrs of work remaining out of initial estimate of <%=totalEffort %> hrs'>
									(<%=taskWeight %>)&nbsp;
									<%=requirement.getRequirementOwner() %>	
									</span>
								</div>
								<div style='float:right;'>	
									<%if (!(taskStatus.equals("completed"))) { %>
									<span class='normalText' title='<%=effortRemaining %> hrs of work remaining out of initial estimate of <%=totalEffort %> hrs'>
									<%=effortRemaining%>/<%=totalEffort %> hrs	
									</span>
									<%} %>
								</div>
							</td>
						</tr>	
					
						<tr><td>	
				
							<span class='normalText'>
							Action
							<select <%=disabledString %> name='<%=scrumActionDropDownName%>' id='<%=scrumActionDropDownName%>'
								onChange='processScrumAction("<%=taskStatus%>",<%=project.getProjectId()%>,<%=sprintId%>,<%=requirement.getRequirementId() %>);'>
								<option value='-1'> </option> 
							
								<option value='removeRequirementFromSprint'>Remove from this sprint</option>
								<option value='displayMoveToAnotherSprint'>Move to another sprint</option>
								
								
								<option value='-1'> </option> 
								
								<option value='displayAssignToNewOwner'>Assign To New Owner</option>	
								
								<option value='-1'> </option> 
								
								<option value='displaySetTaskWeight'>Set task weight (importance)</option>		
								
								<option value='-1'> </option> 
								
								
								<% if (taskStatus.equals("notStarted")){ %>
									<option value='setStatusToInProgress'>Set Status to In Progress</option>
									<option value='setStatusToBlocked'>Set Status to Blocked</option>
									<option value='setStatusToCompleted'>Set Status to Completed</option>
								<%} %>
								
								<% if (taskStatus.equals("inProgress")){ %>
									<option value='setStatusToNotStarted'>Set Status to Not Started</option>
									<option value='setStatusToBlocked'>Set Status to Blocked</option>
									<option value='setStatusToCompleted'>Set Status to Completed</option>
								<%} %>
								
								<% if (taskStatus.equals("blocked")){ %>
									<option value='setStatusToNotStarted'>Set Status to Not Started</option>
									<option value='setStatusToInProgress'>Set Status to In Progress</option>
									<option value='setStatusToCompleted'>Set Status to Completed</option>
								<%} %>
									
								
								<% if (taskStatus.equals("completed")){ %>
									<option value='setStatusToNotStarted'>Set Status to Not Started</option>
									<option value='setStatusToInProgress'>Set Status to In Progress</option>
									<option value='setStatusToBlocked'>Set Status to Blocked</option>
								<%} %>
										
										
								<option value='-1'> </option> 
								
								<option value='displaySetTotalEffort'>Set total effort (hrs)</option>		
								<option value='displaySetEffortRemaining'>Set effort remaining (hrs)</option>		
								
								<option value='-1'> </option> 
							</select>			
							</span>	
											
							
						
										
							<div id='<%=actionSubDivName %>' name='<%=actionSubDivName %>' style='display:none;'>
								<span class='normalText'>
									<select name='<%=ownerDropDownName%>' id='<%=ownerDropDownName%>'
										onChange='assignRequirementToOwner("<%=ownerDropDownName%>",<%=requirement.getRequirementId() %>);'>
									</select>
								</span>
							</div>		
							
							 
							<div id='<%=moveToAnotherSprintSubDivName %>' name='<%=moveToAnotherSprintSubDivName %>' style='display:none;'>
								<span class='normalText'>
									Move to
									<select name='<%=moveToAnotherSprintName %>' id='<%=moveToAnotherSprintName %>'
									onchange='moveToAnotherSprint("<%=taskStatus%>","<%=moveToAnotherSprintName%>",<%=requirement.getRequirementId()%>, <%=sprintId%>);'>
										<%
										ArrayList sprints = project.getProjectSprints(databaseType);
										Iterator s = sprints.iterator();
										while (s.hasNext()){
											Sprint sprint = (Sprint) s.next();
											if (sprint.getSprintId() == sprintId) {
												%>
												<option SELECTED value='<%=sprint.getSprintId() %>'><%=sprint.getSprintName() %></option>
												<%
											}
											else {
												%>
												<option value='<%=sprint.getSprintId() %>'><%=sprint.getSprintName() %></option>
												<%
											}
										}
										%>
									</select>
								</span>
							</div>
														
							<div id='<%=totalEffortSubDivName %>' name='<%=totalEffortSubDivName %>' style='display:none;'>
								<span class='normalText'>
									Total Effort (hours) 
									<input type='text' name='<%=totalEffortName %>' id='<%=totalEffortName %>' size='4' value='<%=totalEffort %>'
										onkeypress='if (event.keyCode == 13) {
											setRequirementTotalEffort("<%=taskStatus%>","<%=totalEffortName%>",<%=requirement.getRequirementId()%>, <%=sprintId%>);
											}';
									>  </input>	
									<input type='button' name='Go' value='Go' 
										onClick='setRequirementTotalEffort("<%=taskStatus%>","<%=totalEffortName%>",<%=requirement.getRequirementId()%>, <%=sprintId%>);'>	
								</span>
							</div>
								 		
							
							<div id='<%=effortRemainingSubDivName %>' name='<%=effortRemainingSubDivName %>' style='display:none;'>
								<span class='normalText'>
									Effort Remaining (hours) 
									<input type='text' name='<%=effortRemainingName %>' id='<%=effortRemainingName %>' size='4' value='<%=effortRemaining %>'
										onkeypress='if (event.keyCode == 13) {
											setRequirementEffortRemaining("<%=taskStatus%>","<%=effortRemainingName%>",<%=requirement.getRequirementId()%>, <%=sprintId%>);
											}'	>  </input>
									<input type='button' name='Go' value='Go' 
										onClick='setRequirementEffortRemaining("<%=taskStatus%>","<%=effortRemainingName%>",<%=requirement.getRequirementId()%>, <%=sprintId%>);'>	
								</span>
							</div>			 		
							<div id='<%=taskWeightSubDivName %>' name='<%=taskWeightSubDivName %>' style='display:none;'>
								<span class='normalText'>
									Task Weight (Importance) 
									<input type='text' name='<%=taskWeightName %>' id='<%=taskWeightName %>' size='4' value='<%=taskWeight %>'
										onkeypress='if (event.keyCode == 13) {
											setRequirementTaskWeight("<%=taskStatus%>","<%=taskWeightName%>",<%=requirement.getRequirementId()%>, <%=sprintId%>);
											}'
									>  </input>
									<input type='button' name='Go' value='Go' 
										onClick='setRequirementTaskWeight("<%=taskStatus%>","<%=taskWeightName%>",<%=requirement.getRequirementId()%>, <%=sprintId%>);'>	
								</span>
							</div>
							<br></br>				
												 			
							<span class='normalText'>
							<a href="#" onclick= 'displayRequirementDescriptionSubSet(<%=requirement.getRequirementId()%>
							,"<%=displayRDInReportDiv%>", "name:##:description:##:")'> 
							<img src="/GloreeJava2/images/search16.png"  border="0">
							</a> 
					
							<a href="#" 
								onClick='
								document.getElementById("contentCenterF").style.display = "none";
								
								displayFolderInExplorer(<%=requirement.getFolderId()%>);
								displayFolderContentCenterA(<%=requirement.getFolderId() %>);
								displayFolderContentRight(<%=requirement.getFolderId() %>);		 								
								displayRequirement(<%=requirement.getRequirementId()%>,"Agile Scrum Workflow");
								// since we are showing the requirement, lets expand the layout to show content right
								layout.getUnitByPosition("right").expand();'>
								
							<img src="/GloreeJava2/images/puzzle16.gif" border="0">
							&nbsp;<%=requirement.getRequirementFullTag()%> : Ver-<%=requirement.getVersion()%> :  <%=requirement.getRequirementNameForHTML() %></a> 
							</span>
							<br>
							<%=requirement.getRequirementDescription() %>
							<div id = '<%=displayRDInReportDiv%>'> </div>
						</td></tr>
					</table>
				</div>
			

<%}%>