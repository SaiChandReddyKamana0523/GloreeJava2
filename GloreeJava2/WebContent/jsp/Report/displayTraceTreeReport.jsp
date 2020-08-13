<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String displayListReportIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayListReportIsLoggedIn == null) || (displayListReportIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
%>
		// log in page.
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
%>

	
	
	<% 

		// Note, this can be called for new reports (where you get only folderId)
		// or for existing reports (WHERE YOU GET BOTH FOLDERID and reportId).
		// we will use reportId in the 'displayListReportFilter' section.
	   	int folderId = Integer.parseInt(request.getParameter("folderId"));
	   	Folder folder = new Folder(folderId);	
		
	%>
	
	<div id = 'reportInfoDiv' class="level2Box" style="background-color:white;	border:2px F9F7E0 ;">
		<form method="post" id="reportFilterForm" action="">
			<table  class='paddedTable' width='100%' >
				<tr>
					<td valign='bottom'> 
					<div id ='filterDiv' class='level2Box'> 
					<%@ include file="displayTraceTreeReport_filter.jsp" %>	
					</div> 
					<div id ='bulkEditActionResponse' style="display:none;"></div>
			
					</td>
				</tr>
			</table>
		</form>
	</div>
<%}%>