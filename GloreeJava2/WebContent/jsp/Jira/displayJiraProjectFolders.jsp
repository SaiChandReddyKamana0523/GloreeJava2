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
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	int projectId = Integer.parseInt(request.getParameter("projectId"));
	if (!(securityProfile.getRoles().contains("MemberInProject" + projectId))){
		//User is NOT a member of this project. so do nothing and return.
		return;
	}
	Project project = new Project(projectId, databaseType);
	ArrayList folders = project.getMyFolders();
	
%>
<div>
	<table class='paddedTable'>
		
		<tr>
			<td width='150px'>
				<span class='normalText'>
					Jira Project 
				</span>
			</td>
			<td>
				<span class='normalText'>
					<select id='folderId' onChange='displayRequirementsInJiraDashboard(<%=projectId%>,"folder");'>
						<option value='-1'> Jira Project </option>
						<%
						Iterator f = folders.iterator();
						while (f.hasNext()){
							Folder folder = (Folder) f.next();
							if (folder.getFolderPath().startsWith("Jira Proxy")){	
							%>	
								
								<option value='<%=folder.getFolderId()%>'>
									<%=folder.getFolderPath() %>
								</option>
						<%	
							}
						}%>
					</select>
				
						
				</span>
			</td>
		</tr>


		<tr>
			<td colspan='2'>	
				<div id='displayRequirementsInJiraDashboardDiv' style='display:none'></div>
			</td>
		</tr>
	</table>
</div>



