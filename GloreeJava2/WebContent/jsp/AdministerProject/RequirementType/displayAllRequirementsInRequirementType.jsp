<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String dARRTIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dARRTIsLoggedIn == null) || (dARRTIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is an admin of this project.
	boolean dARTIsAdmin = false;
	SecurityProfile dARTSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dARTSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
		dARTIsAdmin = true;
	}
%>

<% if (dARTIsAdmin){ %>
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<% 
	   	// Called when trying to delete a requirement type. This displays requirements of this type in the explorer window.
	   	int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
	   	RequirementType requirementType = new RequirementType(requirementTypeId);	
		
	%> 
	<div id='displayAllReqsInRT' class='invisibleLevel1Box'>
		<table class='paddedTable'>
			<tr>
				<td colspan="2"> 
				<span class='sectionHeadingText'>
				<b>Requirements in <img src="/GloreeJava2/images/puzzle16.gif" border="0">
				<%=requirementType.getRequirementTypeName() %>
				</b>
				</span>
				</td>
			</tr>	
			<%
				ArrayList requirements = ProjectUtil.getAllRequirementsInRT(requirementTypeId,"all",databaseType);
			    if (requirements != null){
			    	Iterator i = requirements.iterator();
			    	while ( i.hasNext() ) {
			    		Requirement r = (Requirement) i.next();
			 %>
		 	<tr>
		 		<td colspan=2>
		 		<span class='normalText'>
		 		<a href="#" onClick="displayRequirement(<%=r.getRequirementId()%>)">
		 		<img src="/GloreeJava2/images/puzzle16.gif" border="0">
		 		&nbsp;<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %>
		 		</a> 
		 		</span>
		 		</td>			
		 	</tr>
			 <%
			    	}
			    }
			%>
		</table>
	</div>
<%}%>