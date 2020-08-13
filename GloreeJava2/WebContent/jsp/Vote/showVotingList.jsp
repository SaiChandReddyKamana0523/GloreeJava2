
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

		boolean isUserAnAdmin = false; 
		if (securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
			isUserAnAdmin = true;
		}
		
		
		// lets see if this user is a member of this project.
		// we are leaving this page open to member of this project (which includes admins also)
		boolean isMember = false;
		if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
			isMember = true;
		} 
		
		if (isMember){
	
	
	    	String folderIdString = request.getParameter("folderId");
	    	int folderId = Integer.parseInt(folderIdString);
	    	Folder folder = new Folder(folderId);	
	   	
			//ArrayList<Requirement> requirements = folder.getMyRequirements(folder.getProjectId(), databaseType);
				
			ArrayList<HashMap<String,String>> votedRequirements = VoteUtil.getVotedRequirements(folder);
			// for pagination, lets set the pageSize.
			int pageSize = 2000;
			
			
			
			// we add 1 to arraysize/pageSize because, int div truncates things.
			int numOfPages = (votedRequirements.size() / pageSize) + 1 ; 	
			
					
			int pageToDisplay = 1;
			if (request.getParameter("page") != null){
				pageToDisplay = Integer.parseInt(request.getParameter("page"));	
			}
			int pageStartIndex = (pageToDisplay * pageSize) - pageSize;
			int pageEndIndex = pageStartIndex + pageSize;
			if (pageEndIndex > votedRequirements.size()){
				pageEndIndex = votedRequirements.size();
			}
			
			
			String pageString = "";
			for (int i=1;i<=numOfPages;i++){
				if (i == pageToDisplay){
					pageString += "<b>" +  i + "</b>&nbsp;&nbsp;";
				}
				else {
					pageString += "<a href='#' onclick='reportPagination(\"requirementsInRealFolder\"," 
						+ folder.getFolderId() + "," +
						i +  ", \"" + "" + "\")'> " + i + " </a>" ;
					pageString += "&nbsp;&nbsp;";	
				}
			}
			// drop the last nbsp;
			pageString = (String) pageString.subSequence(0,pageString.lastIndexOf("&nbsp;&nbsp;"));
			
			
			int votesCast = VoteUtil.getVotesCast(folderId, user.getUserId());
			int totalVotingRights = VoteUtil.getTotalVotingRightsForUser(folderId, user.getUserId());
			int remainingVotingRights = totalVotingRights - votesCast;
			
			String disabledString = "";
			
			HashMap<Integer,Integer> votesCastInAFolder = VoteUtil.getVotesCastByAUserInAFolder(user.getUserId(), folderId);
			%>
			<div class="panel panel-info"> 	
			<div class="panel-heading " style='text-align:center'> 
				Vote For <%=folder.getFolderName() %>
				<br>
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
			</div>
			<div id = 'voteBody' class="panel-body" >	
				<table class='table table-striped'>
					<tr class='info'>
						<td>
						<div><table>
							<tr>
						<td style='width:600px'> Object </td> 
						<td style='width:200px; text-align:center'>Total Votes Cast </td>
						<td style='width:200px; text-align:center'>Your Vote</td>
						<td style='width:200px; text-align:center'>Vote Now</td>
						</tr></table></div>
					</tr>
				
				<%
				int counter = 0;
				for (HashMap<String,String> voteReq : votedRequirements){
					counter++;
					if (counter < pageStartIndex){
						continue;
					}
					if (counter > pageEndIndex){
						break;
					}
				
					
					
					int requirementId = 0;
					try {
						requirementId = Integer.parseInt(voteReq.get("requirementId"));
					}
					catch (Exception e){}
					String fullTag = voteReq.get("fullTag");
					String name = voteReq.get("name");
					
					int totalVotesForThisReq = 0;
					try {
						totalVotesForThisReq = Integer.parseInt(voteReq.get("vote"));
					}
					catch (Exception e){}
					
					int totalVotesByThisUserForthisReq = 0;
					try{
						totalVotesByThisUserForthisReq= votesCastInAFolder.get(requirementId);
					}
					catch (Exception e){
						// do nothing
					}
					
					String url = ProjectUtil.getURL(request,requirementId,"requirement");

				%>
				   <tr>
				   		<td>
				   		<div id='voteRow<%=requirementId%>'>
				   			<table><tr>
						   		
						   		<td style='width:600px'>
						   			<span id="requirementNameDisplaySpan<%=requirementId %>">
									 <ul  class="nav navbar-nav"  >
										<li class="dropdown">
										  <a href="#" class="dropdown-toggle" data-toggle="dropdown"
												
											><b><%=fullTag%> </b>:  <%=name %> </a>
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
													onClick='showOtherVotes(<%=requirementId %>)'
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
						   				<input type='text' id='castVote<%=requirementId%>' value='<%=totalVotesByThisUserForthisReq %>'>
						   			<%} %>
						   		</td>
						   		<td style='width:200px; text-align:center'>
						   			<% if (totalVotingRights > 0 ) { %>
							   			<input type='button' class='btn btn-xs btn-primary'
							   			onclick='castVote(<%=requirementId %>)' value='Vote'>
						   			<%} else {%>
						   				<div class='alert alert-danger'>No Voting Rights</div>
						   			<%} %>
						   		
						   		</td>
						   	</tr>
						   	
						   	<tr><td colspan='4'>
						   		<div class='alert alert-success' id='otherVotes<%=requirementId %>' style='display:none'></div>
						   	</td></tr>
						   	
						   	</table>
						   </div>
					</tr>
				<%
				}%>
				
				</table>
			</div>
			</div>
			<%			
	} %>			