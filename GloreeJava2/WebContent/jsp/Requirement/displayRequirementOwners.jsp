<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	String installationType = this.getServletContext().getInitParameter("installationType");
	
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User user = securityProfile.getUser();
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);
	
	///////////////////////////////SECURITY CODE ////////////////////////////
	Project project= (Project) session.getAttribute("project");
	String canNotBeOwners = project.getCanNotBeOwnersInProject();
	ArrayList users = project.getMembers();
	Iterator u = users.iterator();
	%>
		<span class='normalText'>
			<select name="requirementOwner" id="requirementOwner" 
				onChange='setRequirementOwner(<%=requirement.getRequirementId() %>)'>
	<%
	while (u.hasNext()){
		User projectMember = (User) u.next();
		if (canNotBeOwners.contains(projectMember.getEmailId())){
			continue;
		}

		if (
				(installationType.toLowerCase().equals("onsite")  )
				&&
				(projectMember.getUserType().equals("expired"))
			){
			// If this is an onsite install and the user has an expired license
			// he / she doesn't even show up on the list.
			continue;
		}

		else if (projectMember.getEmailId().equals(requirement.getRequirementOwner())){
		%>
			<option SELECTED value='<%=projectMember.getEmailId()%>'><%=projectMember.getLastName() %>  <%=projectMember.getFirstName() %></option>
		<%			
		}
		else {
		%>
			<option value='<%=projectMember.getEmailId()%>'><%=projectMember.getLastName() %>  <%=projectMember.getFirstName() %></option>
		<%
		}
	}
	%>
			</select>					
		</span>
	
	