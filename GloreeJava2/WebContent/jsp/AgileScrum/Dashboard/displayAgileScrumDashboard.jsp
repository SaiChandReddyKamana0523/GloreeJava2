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
					class = 'focusTab' >	
					<div >		 
						<span class='normalText'>
		        		<img src="/GloreeJava2/images/color_swatch16.png" border="0">
		        		 Dashboard
		        		 </span>
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
									Sprint Dashboard
									</span>
								</td>
							</tr>	
						
					
			
							<tr>
								<td colspan='2'  >
									<div id='agileScrumMetricsDiv'  style='display:none'> ></div>
								</td>
							</tr>					
						 
							<tr>
								<td colspan='2'  >
									<div id='agileScrumDataTableDiv'  style='display:none'> ></div>
								</td>
							</tr>						 

							<tr>
								<td colspan='2'  >
									<div id='agileScrumRequirementsDiv'  style='display:none'> ></div>
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