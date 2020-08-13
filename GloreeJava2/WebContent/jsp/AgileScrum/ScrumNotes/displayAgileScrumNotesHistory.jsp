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
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	
	int sprintId = Integer.parseInt(request.getParameter("sprintId"));
	Sprint sprint = new Sprint(sprintId, databaseType);

	
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	User user = securityProfile.getUser();
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)

	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + sprint.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
		ArrayList scrumNotes = sprint.getScrumNotes();
		
		%>

	<table class='paddedTable' >
		<tr>
			<td width='200px'> <span class='normalText'><b>Date</b></span></td>
			<td width='200px'> <span class='normalText'><b>Noted By</b></span></td>
			<td width='600px'> <span class='normalText'><b>Note</b></span></td>
		</tr>
		<% 
		Iterator sNI = scrumNotes.iterator();
		while (sNI.hasNext()){
			
			String sN = (String) sNI.next();
			String [] data = sN.split(":##:");
			String firstName = data[0];
			String lastName = data[1];
			String logDt = data[2];
			String scrumNote  = data[3];
			
			%>
				<tr>
					<td><span class='normalText'><%=logDt%></span></td>
					<td><span class='normalText'> <%=firstName %>  <%=lastName %> </span></td>
					<td><span class='normalText'><%=scrumNote %> </span></td>										
				</tr>
			<%
		}
	%>
	</table>
<%}%>