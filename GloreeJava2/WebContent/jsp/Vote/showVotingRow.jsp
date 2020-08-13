
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
	   
			
			
	    	String url = ProjectUtil.getURL(request,r.getRequirementId(),"requirement");
			
			
			int votesCast = VoteUtil.getVotesCast(r.getFolderId(), user.getUserId());
			int totalVotingRights = VoteUtil.getTotalVotingRightsForUser(r.getFolderId(), user.getUserId());
			int remainingVotingRights = totalVotingRights - votesCast;
			int totalVotesByThisUserForthisReq = VoteUtil.getVotesCastForARequirementByAUser(requirementId, user.getUserId());
			HashMap<Integer,Integer> votesCastInAFolder = VoteUtil.getVotesCastByAUserInAFolder(user.getUserId(), r.getFolderId());
		
			int totalVotesForThisReq = 0;
			try{
				totalVotesForThisReq= Integer.parseInt(r.getAttributeValueFromUDA("Total Votes Cast"));
			}
			catch (Exception e){
				// do nothing
			}
			
				%>
	   		<div id='voteRow<%=r.getRequirementId()%>'>
	   			<table><tr>
			   		
			   		<td style='width:600px'>
			   			<span id="requirementNameDisplaySpan<%=r.getRequirementId() %>">
						 <ul  class="nav navbar-nav"  >
							<li class="dropdown">
							  <a href="#" class="dropdown-toggle" data-toggle="dropdown"
									
								><b><%=r.getRequirementFullTag()%> </b>:  <%=r.getRequirementNameForHTML() %> </a>
							  <ul class="dropdown-menu">
								<li style='display:block'>
									<a href="#" 
										onClick='window.open ("<%=url%>");'
										>Open in New Tab
									</a>
								</li>
								<% if (totalVotesForThisReq > 0 ){ %>
								<li style='display:block'>
									<a href="#" 
										onClick='showOtherVotes(<%=r.getRequirementId() %>)'
										>Show Previous Votes
									</a>
								</li>
								<%} %>
								
								
								</ul>
							</li>
						  </ul>	
						  </span>						   			
			   			
			   			 
			   			
			   		</td>
			   		<td style='width:200px; text-align:center'><%=totalVotesForThisReq %></td>
			   		<td style='width:200px; text-align:center'>
			   			<% if (totalVotingRights > 0 ) { %>
			   				<input type='text' id='castVote<%=r.getRequirementId() %>' value='<%=totalVotesByThisUserForthisReq %>'>
			   			<%} %>
			   		</td>
			   		<td style='width:200px; text-align:center'>
			   			<% if (totalVotingRights > 0 ) { %>
				   			<input type='button' class='btn btn-xs btn-primary'
				   			onclick='castVote(<%=r.getRequirementId() %>)' value='Vote'>
			   			<%} else {%>
			   				<div class='alert alert-danger'>No Voting Rights</div>
			   			<%} %>
			   		
			   		</td>
			   	</tr>
			   	
			   	<tr><td colspan='4'>
			   		<div class='alert alert-success' id='otherVotes<%=r.getRequirementId() %>' style='display:none'></div>
			   	</td></tr>
			   	
			   	</table>
			   </div>
					
			<%			
	} %>			