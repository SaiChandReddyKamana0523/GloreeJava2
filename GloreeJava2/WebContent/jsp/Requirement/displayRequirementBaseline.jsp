<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String displayRequirementCoreIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayRequirementCoreIsLoggedIn == null) || (displayRequirementCoreIsLoggedIn.equals(""))){
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
		int baselineId = Integer.parseInt(request.getParameter("baselineId"));		
		RequirementBaseline requirementBaseline = new RequirementBaseline(baselineId, databaseType);

		Requirement requirement = new Requirement(requirementBaseline.getRequirementId(), databaseType);

		///////////////////////////////SECURITY CODE ////////////////////////////
		// if the requirement worked on, doesn't belong to the project the user is 
		// currently logged into, then a user logged into project x is trying to 
		// hack into a req in project y by useing requirementId parameter.
		if (requirement.getProjectId() != project.getProjectId()) {
			return;
		}
		///////////////////////////////SECURITY CODE ////////////////////////////
	
		
	%>
	<div class='alert alert-success'>
	<table  class='paddedTable' width='100%' >
	<!--  lets get the requirement details displayed -->
		<tr>
			<td align='right'>
				<a href='#' onclick='document.getElementById("displayRequiermentBaselineDiv").style.display = "none"'> close </a>
			</td>
		</tr>
		<tr>
			<td align='left' >				
				<table class='paddedTable'>

					<tr>
						<td> 
							<span class='headingText'> Baseline Name </span>
						</td>
						<td> 
							<span class='normalText'><%=requirementBaseline.getRTBaselineName() %> </span>
							
							<%if (requirementBaseline.getLocked() == 1 ){ %>
								<span class='normalText'>
								(Locked)
								&nbsp;&nbsp;
		        				<font color='gray'> 
		        				Remove From Baseline
		        				</font>
		        				</span>
							<%}
							else { %>
								<span class='normalText'>
								(Unlocked)
								</span>
								&nbsp;&nbsp;
		        				<a href ='#' onClick='
		        				removeRequirementFromBaseline(<%=requirementBaseline.getRequirementId()%>,<%=requirementBaseline.getRequirementBaselineId()%>)'> 
		        				Remove From Baseline
		        				</a>
							<%} %>
							

						</td>						
					</tr>
					<tr>
						<td> 
							<span class='headingText'> Baselined Date</span>
						</td>
						<td> 
							<span class='normalText'><%=requirementBaseline.getRequirementBaselinedDt() %> </span>
						</td>						
					</tr>
					<tr>
						<td> 
							<span class='headingText'> Baselined Version</span>
						</td>
						<td> 
							<span class='normalText'><%=requirementBaseline.getRequirementBaselinedVersion() %> </span>
						</td>						
					</tr>
					<tr>
						<td> 
							<span class='headingText'> Requirement Name </span>
						</td>
						<td> 
							<span class='normalText'><%=requirementBaseline.getRequirementBaselinedName() %> </span>
						</td>						
					</tr>
					<tr>
						<td> 
							<span class='headingText'> Requirement Description</span>
						</td>
						<td> 
							<span class='normalText'><%=requirementBaseline.getRequirementBaselinedDescription() %> </span>
						</td>						
					</tr>
				</table>
			</td>
		</tr>
	</table>
	</div>
<%}%>