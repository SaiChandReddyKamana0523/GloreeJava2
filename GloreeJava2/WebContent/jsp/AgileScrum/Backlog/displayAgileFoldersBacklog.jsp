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
		Sprint sprint = new Sprint(sprintId, databaseType);
		int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
		
		
%>

	<table class='paddedTable' width='100%'>
		<tr> 
			<td colspan='2'>
				<span class='headingText'>Objects in Folder
				
				<select id='backlogFolderId' 
					onChange='displayRequirementsInAgileBacklog(<%=project.getProjectId() %>, <%=sprint.getSprintId() %>);'>
					<option value='-1'></option>
					<%
					
					ArrayList folders = project.getMyFolders();
					Iterator f = folders.iterator();
				
					while (f.hasNext()){
						
						Folder folder = (Folder) f.next();
						if (folder.getRequirementTypeId() == requirementTypeId){
		
							%>
							<option value='<%=folder.getFolderId()%>'>
							<%=folder.getFolderPath() %>
							</option>	
							<%	
						}
					}
					
					%>
				</select>
				</span> 
			</td>
		</tr>
		<tr> 
			<td colspan='2'>
				<div id='requirementsInBacklogDiv' style='display:none'>
				</div>
			</td>
			
		</tr>	
	</table>
	
	
<%}%>