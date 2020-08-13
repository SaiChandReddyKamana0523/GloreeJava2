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
		
		
		ArrayList requirementTypes = project.getMyRequirementTypes();
		
		// lets see if this project has any agileEnabledRequirementType
		int agileEnabledRequirementTypes = 0;
		Iterator rT = requirementTypes.iterator();
		
		while (rT.hasNext()){
			RequirementType requirementType = (RequirementType) rT.next();
			if (requirementType.getRequirementTypeEnableAgileScrum() == 1){
				agileEnabledRequirementTypes++;
			}
		}
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
					class = 'focusTab' >	
					<div >		 
		        		<img src="/GloreeJava2/images/tasks.png" border="0">
			    		<span class='normalText'>
			    		Backlog
			    		</span>
		        		
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
		<table width='100%'>	
			<tr>
				<td >
					<div>
						<div id='createRequirementTypeDiv' class='level1Box'>
						
	
						<form method="post" id="displayBacklogForm" action="">
							
						<table class='paddedTable' width='100%'>
							<tr>
								<td align='left' colspan='2' bgcolor='#99CCFF' colspan='2'>				
									<span class='subSectionHeadingText'>
									Add Tasks from Backlog to this Sprint
									</span>
								</td>
							</tr>	
						
							<tr>
								<td colspan='2'  >
								</td>
							</tr>					
			
							<%
							if (agileEnabledRequirementTypes > 0 ) {
							%>
							<tr> 
								<td colspan='2'>
									<span class='headingText'>Requirement Type &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									
									
									<select id='backlogRequirementType' 
										onChange='displayFoldersInAgileBacklog(<%=project.getProjectId() %>, <%=sprint.getSprintId() %>);'>
										<option value='-1'></option>
										<%
										
										rT = requirementTypes.iterator();
									
										while (rT.hasNext()){
											RequirementType requirementType = (RequirementType) rT.next();
											if (requirementType.getRequirementTypeEnableAgileScrum() == 1){
												%>
												<option value='<%=requirementType.getRequirementTypeId()%>'>
												<%=requirementType.getRequirementTypeShortName()%> : <%=requirementType.getRequirementTypeName() %>
												</option>	
												<%		
											}
										}
										
										%>
									</select>
									</span> 
								</td>
							</tr>
							<%}
							else {
								// there are no Agile enabled Requriement Types.
							%>
								<tr> 
								<td colspan='2'>
									<div class='alert alert-success'>
									<span class='headingText'>You need to first enable your Requirement Types, so they can be added to Agile Sprints. Here are the steps.
									
										<br><br>
										1.Launch the Administration Module (Click on the 'Administer' link on top right hand side of the page)<br>
										2.In the 'Core Info' tab, Agile Work Flow section, select the Requirement Types you want to be enabled for Agile and click 'Update Core Info'<br>
										3.This will add some special attributes to these Requirement Types, so they can be tracked as Agile tasks.<br>
										4.Return to this project and you should be able to add these Requirements to the Agile Sprint.
									
									</span>
									</div>
								</td>
								</tr>
							<%} %>
							<tr> 
								<td colspan='2'>
									<div id='foldersInAgileBacklogDiv' style='display:none'>
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