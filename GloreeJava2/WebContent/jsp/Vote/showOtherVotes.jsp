
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
	   
			
			ArrayList<Vote> votes = VoteUtil.getOtherVotes(r.getRequirementId());
			
			
				%>
		   		<table class='table'>
		   			<tr class='info'>
				   		<td style='width:400px'>
				   			Voter
				   		</td>
				   		<td style='width:200px'>Date</td>
				   		<td style='width:100px'> Votes</td>
				   	</tr>
			<%	
			
			for (Vote vote:votes){
			%>
				<tr>
			   		<td style='width:400px'>
			   			<%=vote.getVoterEmailId() %>
			   		</td>
			   		<td style='width:200px'><%=vote.getVoteDate() %></td>
			   		<td style='width:100px'> <%=vote.getVotesCast() %></td>
			   	</tr>
			<%
			}
			%>
			</table>
			
			<%
	} %>			