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
		int requirementId = Integer.parseInt(request.getParameter("requirementId"));
		
		ArrayList membersInProject = project.getMembers();
		String ownerDropDownName = "ownerDropDown" + requirementId;
		%>			
		<span class='normalText'>
			Owner <select name='<%=ownerDropDownName%>' id='<%=ownerDropDownName%>'
				onChange='assignRequirementToOwner("<%=ownerDropDownName%>",<%=requirementId%>, <%=sprintId%>);'>
				<option value='-1'> </option>
				<%
				Iterator members = membersInProject.iterator(); 
				while (members.hasNext()){
					User member = (User) members.next();
				%>
					<option value='<%=member.getEmailId() %>'><%=member.getEmailId()%> </option>
				<%} %>
			</select>
		</span>
<%}%>