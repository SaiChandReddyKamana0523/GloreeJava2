<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String moveRequirementFormIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((moveRequirementFormIsLoggedIn == null) || (moveRequirementFormIsLoggedIn.equals(""))){
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
%>

	<%
		// NOTE : this page can be called when some one tries to edit a requirement.
		
		int requirementId = Integer.parseInt(request.getParameter("requirementId"));
		int folderId = Integer.parseInt(request.getParameter("folderId"));
		
		Requirement requirement = new Requirement(requirementId, databaseType);

		///////////////////////////////SECURITY CODE ////////////////////////////
		// if the requirement worked on, doesn't belong to the project the user is 
		// currently logged into, then a user logged into project x is trying to 
		// hack into a req in project y by useing requirementId parameter.
		if (requirement.getProjectId() != project.getProjectId()) {
			return;
		}
		///////////////////////////////SECURITY CODE ////////////////////////////

		ArrayList baselines = RequirementUtil.getEligibleBaselinesForRequirement(requirementId);
		Iterator i = baselines.iterator();
	%>
	
	<div id='addToBaselinePromptDiv' class='alert alert-success'>
		<div style='float:right'>
		<a href='#' onClick='document.getElementById("addToBaselinePromptDiv").style.display = "none";'>Close </a>
		</div>
		
		<form method='post' action='' id="moveRequirementForm">
		
		<span class='normalText'>
		Select the Baseline (Snapshot) to add this Requirement to. <br> 
			<input type='hidden' name='requirementId' value='<%=requirementId%>'>
			<input type='hidden' name='folderId' value='<%=folderId%>'>
			<select name='baseline'>
			<%
				while (i.hasNext()){
					RTBaseline rTBaseline = (RTBaseline) i.next();
					
			%>
				<option value='<%=rTBaseline.getBaselineId()%>'>
					<%=rTBaseline.getBaselineName() %>
					<%if (rTBaseline.getLocked() == 1 ){ %>
					(Locked)
					<%}
					else { %>
					(Unlocked)
					<%} %>
					
				</option>
			<%	
				}
			%>
			</select>
			</span>
			<br><br>
			<input type='button' name='addToBaselineButton' id='addToBaselineButton' value='Add To Baseline' 
			onClick='
				document.getElementById("addToBaselineButton").disabled=true;
				addToBaseline(this.form)'>
		</form>
	</div>
<%}%>