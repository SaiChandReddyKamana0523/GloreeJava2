<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<%
	// authentication only
	String displayProjectInfoIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayProjectInfoIsLoggedIn  == null) || (displayProjectInfoIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)

	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
		
		User user = securityProfile.getUser();
		ArrayList sprints = project.getProjectSprints(databaseType);
		// we may get a sprintId, if we came to this page, after a sprint just got created. 
		int sprintId = 0;
		try {
			sprintId = Integer.parseInt(request.getParameter("sprintId"));
		}
		catch (Exception e) {
			// do nothing
		}
		
		// if sprint Id is 0, it means we just got to this page, and no sprint was created just prior to 
		// coming here.
		// in this case, lets see if the user had previously visited any sprints, and we can set 
		// the display to this sprint
		String sprintIdString = (String) session.getAttribute("sprintId");
		if (sprintId == 0){
			if (sprintIdString != null){
				sprintId = Integer.parseInt(sprintIdString);
			}
		}
	
	%>	
	<div id = 'agileScrumHomeDiv' class='level1Box'>

		<table   width="100%" align="center" class='paddedTable'>
			<tr>
				<td colspan='2'>
					<table class='paddedTable' width='100%'>
						<tr>
						<td align='left' bgcolor='#99CCFF' valign='bottom' width='50px'>	
							<%
							if (sprintId == 0){
							%>
								<img src="/GloreeJava2/images/iteration.png"  height="25" border="0">
							<%}
							else {
							%>
								<img src="/GloreeJava2/images/iteration.png"  height="25" border="0"
								onLoad='displayAgileDailyScrum(<%=project.getProjectId() %>, <%=sprintId %>)'>
							<%
							}
							%>	
						</td>
						<td align='left' bgcolor='#99CCFF'  valign='center'>
							<span class='subSectionHeadingText'>
							&nbsp;&nbsp;Scrum Workflow
							</span>
						</td>
						</tr>
					</table>
				</td>
			</tr>

			<tr>
				<td colspan='2'>
					<%
					if (sprints.size() > 0 ){
					
					
					%>
						<span class='normalText'>
						Select a Sprint 
						<select name='sprintId' id='sprintId'
						onChange='displaySelectedAgileSprint(<%=project.getProjectId() %>);'>
							<option value='-1'> </option> 
						<%
							Iterator i = sprints.iterator();
							while (i.hasNext()){
								Sprint sprint = (Sprint) i.next();
								String sprintName = sprint.getSprintName();
								if (sprintName.length() > 30) {
									sprintName = sprintName.substring(0,29);
								}
								
								if(sprintId == sprint.getSprintId()){
									%>
										<option SELECTED value='<%=sprint.getSprintId() %>'>
											<%=sprint.getSprintName()%> (<%=sprint.getScrumMaster() %>)
										</option>
									<%	
								}
								else {
									%>
									<option  value='<%=sprint.getSprintId() %>'>
										<%=sprint.getSprintName()%> (<%=sprint.getScrumMaster() %>)
									</option>
								<%	
								}
							}
						%>
						</select>			
						</span>	
					<%
					}
					else{ 
					%>
					<span class='normalText'>No Sprints (Iterations) have been defined for this project. Please</span>		
					<%} %>	
					
					<input type='button' class="btn btn-primary btn-sm"   onclick='displayCreateAgileSprint()' value=' Create A New Sprint '
					 name=' Create A New Sprint ' id=' Create A New Sprint ' 
					>
								
				</td>
			</tr>
	
			<tr>
				<td colspan='2'>
					<div id='agileSprintDiv' style='dislplay:none'>
					
					</div>
				</td>
			</tr>			
		</table>
	</div>
<%}%>