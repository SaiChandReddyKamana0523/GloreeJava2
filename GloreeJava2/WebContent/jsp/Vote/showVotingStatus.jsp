
	<!-- GloreeJava2 -->
	<!-- pageEncoding -->
	<%@page contentType="text/html;charset=UTF-8"%>
	
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<%
		// authentication only
		String displayAllRequirementsInRealFIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
		if ((displayAllRequirementsInRealFIsLoggedIn  == null) || (displayAllRequirementsInRealFIsLoggedIn.equals(""))){
			// this means that the user is not logged in. So lets forward him to the 
			// log in page.
	%>
			<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
	<% }
		String databaseType = this.getServletContext().getInitParameter("databaseType");
		Project project= (Project) session.getAttribute("project");
		SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
		User user = securityProfile.getUser();
		String userEmailId = user.getEmailId();

		
		
		// lets see if this user is a member of this project.
		// we are leaving this page open to member of this project (which includes admins also)
		boolean isMember = false;
		if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
			isMember = true;
		} 
		
		if (isMember){
	


	    	String requirementIdString = request.getParameter("requirementId");
	    	int requirementId = Integer.parseInt(requirementIdString);
	    	Requirement r  = new Requirement(requirementId, "");	
	   
			int folderId = r.getFolderId();
			
			
			int votesCast = VoteUtil.getVotesCast(folderId, user.getUserId());
			int totalVotingRights = VoteUtil.getTotalVotingRightsForUser(folderId, user.getUserId());
			int remainingVotingRights = totalVotingRights - votesCast;
			
			%>
				<div id='votingStatusDiv'>
					
					<%
					
					if (totalVotingRights <= 0 ) {
						%>
							<div class='alert alert-danger'>
								You do not have voting rights. 
								If you feel you should be able to vote, please work with your administrator.
							</div>
						<%
						}
					
					if (remainingVotingRights > 0 ) {
					%>
						<div class='alert alert-primary'>
							You have consumed <%=VoteUtil.getVotesCast(folderId, user.getUserId()) %> out of 
							<%=VoteUtil.getTotalVotingRightsForUser(folderId, user.getUserId()) %> Voting Rights
						</div>
						
					<%}
					
					
					if ((totalVotingRights > 0 ) && (remainingVotingRights == 0 )) {
					%>
						<div class='alert alert-success'>
							Congratulations !!! You have completed your voting exercise. 
						</div>
					<%
					}
					if (remainingVotingRights < 0 ) {%>
					<div class='alert alert-danger'>
						You have consumed <%=VoteUtil.getVotesCast(folderId, user.getUserId()) %> out of 
						<%=VoteUtil.getTotalVotingRightsForUser(folderId, user.getUserId()) %> Voting Rights.
						
						Please reduce your votes cast
					</div>
					
					<%}%>
				</div>				   		
					
			<%			
	} %>			