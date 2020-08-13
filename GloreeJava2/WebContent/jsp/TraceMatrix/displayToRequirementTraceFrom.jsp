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
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	else {
		return;
	}
	int requirementId = Integer.parseInt(request.getParameter("requirementId") );
	Requirement r = new Requirement(requirementId, databaseType);
		
	if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" + r.getFolderId()))){
	%>
		<table class='paddedTable' >
			<tr>
				<td align='left' colspan='2'>				
					<div class='alert alert-success'>	
					<span class='subSectionHeadingText'>
					You do not have READ permissions on this folder. 
					</span>
					</div>
				</td>
			</tr>
		</table>
	<%}
	else { %>
		<table class='paddedTable'>
	<%
   		// lets color code the traceTo and traceFrom values.
   		String[] traces = r.getRequirementTraceFrom().split(",");
	for (int i=0;i<traces.length;i++){
		if (traces[i].contains("(s)")){
			%>
			<tr><td><span class='normalText'><font color='red'><%=traces[i]%></font></span></td></tr>
			<%
		}
		else {
			%>
			<tr><td><span class='normalText'><font color='green'><%=traces[i]%></font></span></td></tr>
			<%
		}
	}

		%>
		</table>
		<%
	    }
		%>
					
		
	