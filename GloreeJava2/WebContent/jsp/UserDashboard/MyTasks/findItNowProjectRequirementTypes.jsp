<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->    
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


	
	
<%
	//authorization
	// since we need authorization as well as authenticaiton we will use the 
	// security profile object.
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	if (securityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%
	
	// authorization : since we are explicityly checking for and 
	// listing all the projects the user has access to
	// we are OK here. 
	}

try {	
	int targetProjectId = Integer.parseInt(request.getParameter("targetProjectId"));
			
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project = new Project(targetProjectId, databaseType);
	
%>
		
		<span class='normalText'> 
		and Requirement Type 
		<select id='targetRequirementType' name='targetRequirementType' >
				<option value='0'>All RequirementTypes </option>
				<%
				ArrayList requirementTypes = project.getMyRequirementTypes();
				Iterator rt = requirementTypes.iterator();
				while (rt.hasNext()){
					RequirementType requirementType = (RequirementType) rt.next();
					%>
					<option value='<%=requirementType.getRequirementTypeId() %>'>
						<%=requirementType.getRequirementTypeShortName() %> : <%=requirementType.getRequirementTypeName() %>
					</option>
					<%
				}
				%>
			
		
		
		</select> </span>
<%}

catch (Exception e) {
}
%>
  