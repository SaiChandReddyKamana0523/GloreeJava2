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
	
	
	// lets find if this project has 'ST-' (stories) req type
	boolean hasStories = false;
	ArrayList requiremntTypes  = ProjectUtil.getRequirementTypesInAProject(project.getProjectId());
	Iterator rTs = requiremntTypes.iterator();
	while (rTs.hasNext()){
		RequirementType RT = (RequirementType) rTs.next();
		if (RT.getRequirementTypeShortName().equals("ST")){
			hasStories = true;
		}
	}
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)

	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
		
		int sprintId = Integer.parseInt(request.getParameter("sprintId"));
		Sprint sprint = new Sprint(sprintId, databaseType);
		session.setAttribute("sprintId", Integer.toString(sprintId));
		
		ArrayList sprints = project.getProjectSprints(databaseType);
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
					class = 'nonFocusTab' >	
					<div >		 
		        		<a href='#'	onClick='displayAgileSprintInfo(<%=sprint.getProjectId()%>, <%=sprint.getSprintId()%>)'>
						<img src="/GloreeJava2/images/iteration.png"  width="16" border="0">
						 <span class='normalText'>
		        		 Sprint Info
		        		 </span>
		        		 </a>
		        		 
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
					class = 'focusTab' >	
					<div >	
						<span class='normalText'>	 
		        		<img src="/GloreeJava2/images/calendar16.png" border="0">
			    		 Daily Scrum
			    		 </span>
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
						<div id='agildDailyScrumDiv' class='level1Box'>
						
	
						
						<table class='paddedTable' width='100%'>
							<tr>
								<td align='left' colspan='2' bgcolor='#99CCFF'>				
									<span class='subSectionHeadingText'>
									Daily Scrum
									</span>
								</td>
							</tr>	
						
							<tr>
								<td colspan='2'  ><div id='updateAgileSprintMessage' class='alert alert-success' style='display:none'> ></div>
								
								</td>
							</tr>					
							<tr>
								<td colspan='2'>
									<table>
										<tr>
											
											<td>
												<span class='normalText'>
													<input type="text" 
													class="input-small"
													name="requirementsToAdd" id="requirementsToAdd"  size="30" maxlength="100"
													onfocus="this.value='';"
													placeholder='Add Tasks to Sprint (Eg: Br-1, Fr-3))'
													onkeypress=" addRequirementsToSprintKeyPress(event, <%=sprint.getProjectId()%>, <%=sprint.getSprintId()%>);">
												</span>
												
												
													
												<span class='normalText'>
													<input type="button" class='btn btn-primary btn-sm' name="addRequirementsButton" id = "addRequirementsButton"
													style="width:100px"
													value="  Add  " onClick="addRequirementsToSprint(<%=sprint.getProjectId()%>, <%=sprint.getSprintId()%>)">
												</span>
											
											
												<span title='Add Requirement Backlog to this sprint'>
													<input type="button" class='btn btn-primary btn-sm' name="searchBacklog" id = "searchBacklog"
													width='100px' value='  Search Backlog  '
													   onclick="window.open('/GloreeJava2/jsp/AgileScrum/DailyScrum/displayBacklogRequirementSearchForm.jsp?sprintId=<%=sprintId %>', 
													  'windowname1', 
													  'location=0, scrollbars=1, width=1000, height=600'); 
													   return false;">
													 
					 							</span> 
					 							
					 							
											</td>
										</tr>
									</table>
								</td>
							</tr>					
					

							<tr>
								<td colspan='2'>
									<table>
										<tr>
											<td>
												<span class='normalText'>
												 	Move ALL 
													
													<select name='typeOfTasks' id='typeOfTasks'>
														<option value='-1'> </option>
														<option value='Not Started'> Not Started</option>
														<option value='In Progress'> In Progress</option>
														<option value='Blocked'> Blocked</option>
														<option value='Completed'> Completed</option>
													</select>
												
													Tasks to 
													
													<select name='targetSprintId' id='targetSprintId''>
														<option value='-1'> </option> 
													<%
														Iterator i = sprints.iterator();
														while (i.hasNext()){
															Sprint targetSprint = (Sprint) i.next();
															String targetSprintName = targetSprint.getSprintName();
															if (targetSprintName.length() > 30) {
																targetSprintName = targetSprintName.substring(0,29);
															}
															
															if(sprintId == targetSprint.getSprintId()){
																// you can not move tasks from current sprint to current sprint. so skip this
																continue;	
															}
															else {
																%>
																<option  value='<%=targetSprint.getSprintId() %>'>
																	<%=targetSprint.getSprintName()%> (<%=targetSprint.getScrumMaster() %>)
																</option>
															<%	
															}
														}
													%>
													</select>			
												</span>
											
													
												
												&nbsp;&nbsp;&nbsp;
												<span class='normalText'>
													<input type="button"  class='btn btn-primary btn-sm'  name="moveRequirementsButton" id = "moveRequirementsButton"
													value="  Move  " 
													style='width:100px'
													onClick="moveRequirementsToSprint(<%=sprint.getProjectId()%>, <%=sprint.getSprintId()%>)">
												</span>
											
											</td>
											
										</tr>
									</table>
								</td>
							</tr>					
					
					
					
					
							<tr>
								<td colspan='2'>
								<div id='addRequirementsToSprintMessageDiv' class='alert alert-success' style='display:none'>
								</div>
								
								<div id='moveRequirementsToSprintMessageDiv' class='alert alert-success' style='display:none'>
								</div>
								
								
								</td>
								
							</tr>





							<tr>
								<td colspan='2'>
									<table>
										<tr>
											<td width='225px'>
												<span class='normalText'>
												 	Show ONLY tasks owned by 
												</span>
											</td>
											
											<td>
												<span class='normalText'>
													<select name='showOnlyTasksOwnedBy' id='showOnlyTasksOwnedBy'
													onChange = 'displayNarrowedAgileDailyScrum(<%=sprint.getProjectId() %>, <%=sprintId %>)'>
														<option value='-1'> </option>
														<option value='all' SELECTED> All Team Members </option>
														<%
														ArrayList taskOwners = sprint.getTaskOwners();
														Iterator tO = taskOwners.iterator();
														while (tO.hasNext()){
															String owner = (String) tO.next();
															%>
															<option value='<%=owner%>'><%=owner %></option>
															<%
														}
														%>
													
													</select>
												</span>
											</td>
										</tr>
									</table>	
								</td>
							</tr>

<tr>
		<td colspan=2>
			<a href='#' 
				onclick="
					var descElements = document.getElementsByClassName('description');
				
			 		for (var i = descElements.length - 1; i >= 0; i--) {
						var selectObject = descElements[i];
				 		selectObject.style.display='none';
			 		}	
				"
			>Compact View</a>
			<a href='#' 
				onclick="
					var descElements = document.getElementsByClassName('description');
				
			 		for (var i = descElements.length - 1; i >= 0; i--) {
						var selectObject = descElements[i];
				 		selectObject.style.display='block';
			 		}	
				"
			>Full View</a>
			
			
		</td>
</tr>

							<tr>
								<td colspan='2'>
								<div id='sprintTasksDiv' class='level1Box' >
								<table class='paddedTable' width='100%'>
									<tr>
										<% 
											int widthPCT = 25;
											if (hasStories) {
												widthPCT = 20;
												
										%>
												<td valign='top' width='<%=widthPCT%>%'>
													<div id='storiesDiv' class='level2Box' ></div>
												</td>
												<td height="100%" style="background-color:#99CCFF" width="5px">
												</td>		
										<%} %>
										<td valign='top' width='<%=widthPCT%>%'>
											<div id='notStartedTasksDiv' class='level2Box' ></div>
										</td>
										
										<td  valign='top' width='<%=widthPCT%>%'>
											<div id='inProgressTasksDiv' class='level2Box' > </div>
										</td>
										
										<td  valign='top' width='<%=widthPCT%>%'>
											<div id='blockedTasksDiv' class='level2Box' ></div>
										</td>
										
										<td  valign='top' width='<%=widthPCT%>%'>
											<div id='completedTasksDiv' class='level2Box' ></div>
										</td>
									
									</tr>
									
					
								</table>
								</div>
								
								</td>
							</tr>						
						</table>
						
						
						</div>
					</div>
				</td>
			</tr>				
		</table>
		</td>
		</tr>
		</table>
	
	
<%}%>