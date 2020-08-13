<!-- GloreeJava2 -->-
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
		Sprint currentSprint = new Sprint(sprintId, databaseType);
		String taskStatus = request.getParameter("taskStatus");
		
		if (taskStatus.equals("completed")){
			// if we are displaying the completed column, then
			// we need to set all req's that are 100% complete, but in Not Started, Blocked , In Progress state to completed
			// and all Completed reqs that are less than 100% complete to 100% complete.
			currentSprint.refreshCompleted(user, databaseType);
		}
		
		String showOnlyTasksOwnedBy = request.getParameter("showOnlyTasksOwnedBy");
		if (showOnlyTasksOwnedBy == null){
			showOnlyTasksOwnedBy = "all";
		}
		ArrayList requirements = SprintUtil.getRequirementsInSprint(taskStatus, currentSprint, showOnlyTasksOwnedBy, project, securityProfile, databaseType);
		String borderColor="white";
		String subBorderColor = "white";
		
		
		// lets calculae the number of tasks and hours in this phase of sprint. 
		Iterator reqs = requirements.iterator();
		int totalTasks = 0;
		int sumOfTotalEffort = 0;
		int sumOfEffortRemaining = 0;
		while (reqs.hasNext()){
			Requirement req = (Requirement) reqs.next();
			
			int totalEffort = 0;
			try {
	   			totalEffort = Integer.parseInt(req.getAttributeValue("Agile Total Effort (hrs)"));
	   		}
	   		catch (Exception e){
	   			
	   		}
			
	   		int effortRemaining = 0;
	   		try {
	   			effortRemaining = Integer.parseInt(req.getAttributeValue("Agile Effort Remaining (hrs)"));
	   		}
	   		catch (Exception e){
	   			
	   		}
			totalTasks = totalTasks+1;
			sumOfEffortRemaining = sumOfEffortRemaining + effortRemaining;
			sumOfTotalEffort = sumOfTotalEffort + totalEffort;
			
		}
%>

	<table class='paddedTable' >
		<tr>
			
				<%if (taskStatus.equals("notStarted")){
					
					borderColor = "gray"; 
					subBorderColor = "#CCCCCC";
				%>
					<td align='left' colspan='2' bgcolor='gray'>
						<div id='notStartedHeaderDiv'>				
							<div style='float:left'>
								<span class='subSectionHeadingText'
								title='<%=totalTasks%> NOT STARTED Tasks with <%=sumOfEffortRemaining %> hrs of work remaining out of initial estimate of <%=sumOfTotalEffort %> hrs'>
								Not Started 
								</span>
							</div>
							<div style='float:right'>
								<span class='subSectionHeadingText'
								title='<%=totalTasks%> NOT STARTED Tasks with <%=sumOfEffortRemaining %> hrs of work remaining out of initial estimate of <%=sumOfTotalEffort %> hrs'>
								<%=totalTasks %> Tasks &nbsp;&nbsp;&nbsp;<%=sumOfEffortRemaining %>/<%=sumOfTotalEffort %> Hours
								</span>
							</div>
						</div>
					</td>
				<%} %>
				<%if (taskStatus.equals("inProgress")){ 
					
					borderColor = "orange"; 
					subBorderColor = "#FFCC66";
				%>
					<td align='left' colspan='2' bgcolor='orange'>	
						<div id='inProgressHeaderDiv'>					
							<div style='float:left'>
								<span class='subSectionHeadingText'
								title='<%=totalTasks%> IN PROGRESS Tasks with <%=sumOfEffortRemaining %> hrs of work remaining out of initial estimate of <%=sumOfTotalEffort %> hrs'>
								In Progress</span>
							</div>
							<div style='float:right'>
								<span class='subSectionHeadingText'
								title='<%=totalTasks%> IN PROGRESS Tasks with <%=sumOfEffortRemaining %> hrs of work remaining out of initial estimate of <%=sumOfTotalEffort %> hrs'>
								<%=totalTasks %> Tasks &nbsp;&nbsp;&nbsp;<%=sumOfEffortRemaining %>/<%=sumOfTotalEffort %> Hours</span>
							</div>
						</div>
					</td>
				<%} %>
				<%if (taskStatus.equals("blocked")){ 
					borderColor = "red"; 
					subBorderColor = "#FF9966";
				%>
					<td align='left' colspan='2' bgcolor='red'>
						<div id='blockedHeaderDiv'>			
							<div style='float:left'>
								<span class='subSectionHeadingText' 
								title='<%=totalTasks%> BLOCKED Tasks with <%=sumOfEffortRemaining %> hrs of work remaining out of initial estimate of <%=sumOfTotalEffort %> hrs'>
								Blocked</span>
							</div>
							<div style='float:right'>
								<span class='subSectionHeadingText'
								title='<%=totalTasks%> BLOCKED Tasks with <%=sumOfEffortRemaining %> hrs of work remaining out of initial estimate of <%=sumOfTotalEffort %> hrs'>
								<%=totalTasks %> Tasks &nbsp;&nbsp;&nbsp;<%=sumOfEffortRemaining %>/<%=sumOfTotalEffort %> Hours</span>
							</div>
						</div>				
					</td>
				<%} %>		
				<%if (taskStatus.equals("completed")){ 
					
					borderColor = "green"; 
					subBorderColor = "#99FF99";
				
				%>
					<td align='left' colspan='2' bgcolor='green'>	
						<div id='completedHeaderDiv'>					
							<div style='float:left'>
								<span class='subSectionHeadingText'
								title='<%=totalTasks%> COMPLETED Tasks with <%=sumOfEffortRemaining %> hrs of work remaining out of initial estimate of <%=sumOfTotalEffort %> hrs'>
								Completed</span>
							</div>
							<div style='float:right'>
								<span class='subSectionHeadingText'
								title='<%=totalTasks%> COMPLETED Tasks with <%=sumOfEffortRemaining %> hrs of work remaining out of initial estimate of <%=sumOfTotalEffort %> hrs'>
								<%=totalTasks %> Tasks &nbsp;&nbsp;&nbsp;<%=sumOfEffortRemaining %>/<%=sumOfTotalEffort %> Hours</span>
							</div>	
						</div>				
					</td>
					
				<%} %>	
				
		</tr>
		<tr>
			<td colspan='4'>&nbsp;</td>
		</tr>
		<%
		ArrayList membersInProject = project.getMembers();
		
		// lets sort the requirements array by task weight desc.
		Collections.sort(requirements, new SortSprintByTaskWeightDesc());
		
		
		Iterator requirementsIterator = requirements.iterator();
		while (requirementsIterator.hasNext()){
			Requirement requirement = (Requirement) requirementsIterator.next();
			String displayRDInReportDiv = "displayRDInReportDiv" + requirement.getRequirementId();

			String url = ProjectUtil.getURL(request,requirement.getRequirementId(),"requirement"); 
			
			
			// lets get the display color
			String color = "#99CCFF";
			if (requirement.getUserDefinedAttributes().toLowerCase().contains("color:#:")){
				color = requirement.getAttributeValue("color");
				if ((color == null ) || (color.trim().equals("") )){
					color = "#99CCFF";
				}
			}


			  String req = requirement.getRequirementFullTag();
		   	  
	   		
	   		  String disabledString = "";
		   		if (!( securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
		    			+ requirement.getFolderId()))){
		   			disabledString = "DISABLED";					
				}
		   		// if the requirement is locked and the lock is by some other user, then the action drop down is disabled.
		   		// even if a user has update permissions on a folder,  we still check to see
		   		//  if this requirement is locked by some other user, then we need to set update , delete and purge to Disabled.
		   		if (
		   			(!(requirement.getRequirementLockedBy().equals(""))
		   			&&
		   			(!(requirement.getRequirementLockedBy().equals(user.getEmailId()))))
		   		){
		   			// this req is locked and its locked by someone other that the person currently logged in. hence disabled button is off.
		   			disabledString = "DISABLED";	
		   		}
		   		
		   		String scrumActionDropDownName = "scrumAction" + requirement.getRequirementId();
		   		String requirementDivName = "requirementDiv" + requirement.getRequirementId();
		   		String requirementShowButton = "requirementShowButtonId" + requirement.getRequirementId();
		   		
		   		
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
		<tr>
			<td align='left' colspan='2' >
				<div id = "<%=requirementDivName %>" name="<%=requirementDivName%>" style="border-color:<%=color%>; border-width:4px; border-style: solid;" >
					<div style='z-index:5;position:absolute;border-color:#99CCFF; border-width:10px; border-style: solid;display:none' id = '<%=displayRDInReportDiv%>'> </div>
					<table class='paddedTable' border='0' bordercolor="<%=borderColor%>" >
					
						<tr>
							<td align='left' bgcolor='<%=subBorderColor%>' class='description'>	
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
								onChange='processScrumAction("<%=taskStatus%>",<%=project.getProjectId()%>,<%=sprintId%>,<%=requirement.getRequirementId() %>, "<%=url%>");'>
								
								<option value='-1'> </option> 
								<option value='openInNewTab'>Open in New Tab </option>
								
							
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
										
							<div id='<%=actionSubDivName %>' name='<%=actionSubDivName %>' style='display:none;'></div>		
							
							 
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
											if (sprint.getSprintId() == sprintId ) {
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
											}';
									>  </input>	
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
											}';
									>  </input>	
									<input type='button' name='Go' value='Go' 
										onClick='setRequirementTaskWeight("<%=taskStatus%>","<%=taskWeightName%>",<%=requirement.getRequirementId()%>, <%=sprintId%>);'>	
								</span>
							</div>			
							<br></br>
							
							<div>		

																
								<span class='normalText'>
								
								<%if (!(requirement.getRequirementLockedBy().equals(""))){
										// this requirement is locked. so lets display a lock icon.
									%>
										<span class='normalText' title='Requirement locked by <%=requirement.getRequirementLockedBy()%>'> 
				        					<img src="/GloreeJava2/images/lock16.png" border="0"> 
				        				</span>	
									<%
								}%>
								&nbsp;
								<a href="#" 
									onClick='
									document.getElementById("contentCenterF").style.display = "none";
									
									
									displayFolderInExplorer(<%=requirement.getFolderId()%>);
									displayFolderContentCenterA(<%=requirement.getFolderId() %>);
									displayFolderContentRight(<%=requirement.getFolderId() %>);		 								
									displayRequirement(<%=requirement.getRequirementId()%>,"Agile Scrum Workflow");
									// since we are showing the requirement, lets expand the layout to show content right
									layout.getUnitByPosition("right").expand();'>
									
								<%=requirement.getRequirementFullTag()%> : Ver-<%=requirement.getVersion()%> :  <%=requirement.getRequirementNameForHTML() %></a> 
								</span>
								<br>
								<div class='description'>
									<%=requirement.getRequirementDescription() %>
								</div>
							</div>							
						</td></tr>
					</table>
				</div>
			</td>
		</tr>
		<%
		}
		
		%>
	</table>

<%}%>