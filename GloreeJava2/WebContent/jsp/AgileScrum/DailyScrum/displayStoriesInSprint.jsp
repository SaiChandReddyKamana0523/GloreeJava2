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
		
		String showOnlyTasksOwnedBy =  "all";
		String taskStatus = "";
		
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
			
			if (!(req.getRequirementFullTag().startsWith("ST-"))){
				continue;
			}
			
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

	<table class='paddedTable' width='250'>
		<tr>
			
				<%
					borderColor = "#99CCFF"; 
					subBorderColor = "#99CCFF";
				%>
					<td align='left' colspan='2' bgcolor='#99CCFF'>
						<div id='storiesHeaderDiv'>				
							<div style='float:left'>
								<span class='subSectionHeadingText'>
								Stories
								</span>
							</div>
							<div style='float:right'>
								<span class='subSectionHeadingText'>
								<%=totalTasks %> Stories &nbsp;&nbsp;&nbsp;
								</span>
							</div>
						</div>
					</td>
			
					
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
			
			if (!(requirement.getRequirementFullTag().startsWith("ST-"))){
				continue;
			}
			
			
			String displayRDInReportDiv = "displayStoryRDInReportDiv" + requirement.getRequirementId();

			
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
		   		
		   		String requirementDivName = "storiesDiv" + requirement.getRequirementId();
		   		String storyShowButton = "storyShowButtonId" + requirement.getRequirementId();
		   		String effortRemainingSubDivName = "effortRemainingSubDiv" + requirement.getRequirementId();
		   		String totalEffortSubDivName = "totalEffortSubDiv" + requirement.getRequirementId();
		   		String taskWeightSubDivName = "taskWeightSubDiv" + requirement.getRequirementId();
		   		
		   		
		   		
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
					<table class='paddedTable' border='0' bordercolor="<%=borderColor%>" width='240'>
					
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
					
						<tr><td >	
				
							<div
								onmouseover='document.getElementById("<%=storyShowButton%>").style.visibility="visible"';
								onmouseout='document.getElementById("<%=storyShowButton%>").style.visibility="hidden"';
							>		
							 	<input type='button' name='<%=storyShowButton%>' id='<%=storyShowButton%>' value='Preview'
								class='btn btn-primary btn-sm' style='visibility:hidden'
								onclick= 'displayRequirementDescription(<%=requirement.getRequirementId()%>
								,"<%=displayRDInReportDiv%>" , <%=currentSprint.getSprintId() %>)'>	
								
								
								<br>
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
								<%=requirement.getRequirementDescription() %>
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