<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String displayRequirementAttributeIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayRequirementAttributeIsLoggedIn == null) || (displayRequirementAttributeIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
    // This routine is always called with a requirementId parameter.
    int requirementId = Integer.parseInt(request.getParameter("requirementId"));
    Requirement requirement = (Requirement)	request.getAttribute("requirement");

	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean readPermissions = true;
	if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
			+ requirement.getFolderId()))){
		readPermissions = false;
	}

	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember && readPermissions){
%>
	
	<% 
		///////////////////////////////SECURITY CODE ////////////////////////////
		// if the requirement worked on, doesn't belong to the project the user is 
		// currently logged into, then a user logged into project x is trying to 
		// hack into a req in project y by useing requirementId parameter.
		if (requirement.getProjectId() != project.getProjectId()) {
			return;
		}
		///////////////////////////////SECURITY CODE ////////////////////////////
	
		ArrayList attributeValues = (ArrayList) request.getAttribute("attributeValues");
	%>
	
	<div id = 'attributeInfo' class='level1Box'>
		<form method="post" id="createRequirementAttributeForm" action="">
			<fieldset id="requirementAttributes">
				<legend><b>Attributes</b></legend>
				<table align="center" class='paddedTable'  border="0">
				    
				  	 <%
				  	 Iterator i = attributeValues.iterator();
				  	 while (i.hasNext()){
				  		 RAttributeValue a = (RAttributeValue) i.next();
			    	%>
			    		<tr>
			    			<th align='left'> <%=a.getAttributeName() %> </th>
			    			<td> <%=a.getAttributeEnteredValue()%></td>
			    		</tr>
			    	<%} %> 
				</table>
			<fieldset>	
		</form>    	 
	</div>
<%}%>