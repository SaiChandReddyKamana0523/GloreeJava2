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
		String taskStatus = request.getParameter("taskStatus");
		
		String showOnlyTasksOwnedBy = request.getParameter("showOnlyTasksOwnedBy");
		if (showOnlyTasksOwnedBy == null){
			showOnlyTasksOwnedBy = "all";
		}
		
		ArrayList requirements = SprintUtil.getRequirementsInSprint(taskStatus, sprint,showOnlyTasksOwnedBy, project, securityProfile, databaseType);
		
		
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

				<%	
					if (taskStatus.equals("notStarted")){
				%>
							<div style='float:left'>
								<span class='subSectionHeadingText'>Not Started </span>
							</div>
							<div style='float:right'>
								<span class='subSectionHeadingText'><%=totalTasks %> Tasks &nbsp;&nbsp;&nbsp;<%=sumOfEffortRemaining %>/<%=sumOfTotalEffort %> Hours</span>
							</div>
				<%} 
					if (taskStatus.equals("inProgress")){ 
				%>
							<div style='float:left'>
								<span class='subSectionHeadingText'>In Progress</span>
							</div>
							<div style='float:right'>
								<span class='subSectionHeadingText'><%=totalTasks %> Tasks &nbsp;&nbsp;&nbsp;<%=sumOfEffortRemaining %>/<%=sumOfTotalEffort %> Hours</span>
							</div>
				<%} 
					if (taskStatus.equals("blocked")){ 
				%>
							<div style='float:left'>
								<span class='subSectionHeadingText'>Blocked</span>
							</div>
							<div style='float:right'>
								<span class='subSectionHeadingText'><%=totalTasks %> Tasks &nbsp;&nbsp;&nbsp;<%=sumOfEffortRemaining %>/<%=sumOfTotalEffort %> Hours</span>
							</div>
				<%} 
					if (taskStatus.equals("completed")){ 
				%>
							<div style='float:left'>
								<span class='subSectionHeadingText'>Completed</span>
							</div>
							<div style='float:right'>
								<span class='subSectionHeadingText'><%=totalTasks %> Tasks </span>
							</div>	
				<%} 
					
	}%>