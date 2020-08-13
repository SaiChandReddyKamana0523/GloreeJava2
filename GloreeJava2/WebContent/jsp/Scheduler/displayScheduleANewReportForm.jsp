<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<%
	// authentication only
	String IsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((IsLoggedIn  == null) || (IsLoggedIn.equals(""))){
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
	
	if (isMember){
		
		User user = securityProfile.getUser();
		ArrayList folders = project.getMyFolders();
		
	%>	
		<table   width="100%" align="center" class='paddedTable'>
			<tr>
				<td colspan='2'>
					<table class='paddedTable' width='100%'>
						<tr>
						<td align='left' bgcolor='#99CCFF'>				
							<span class='subSectionHeadingText'>
							Schedule a new recurring report
							</span>
						</td>
						</tr>
					</table>
				</td>
			</tr>

			<tr>
				<td colspan='2' align='left'>
				<a href='#' onClick='displayScheduler();'>Your Recurring Reports</a>
				</td>
			</tr>
			
			<tr>
				<td align='left' style='width:150px;'>				
					<span class='subSectionHeadingText'>
					Report Location
					</span>
				</td>

				<td align='left' >				
					<span class='normalText'>
						<select name='folderId' id='folderId'
							onChange='displaySchedulerReportsInFolder();'>
							<option value='-1'>Select A Folder</option>
						<%
							Iterator i = folders.iterator();
							while (i.hasNext()){
								Folder folder = (Folder) i.next();
								
						%>
							<option value='<%=folder.getFolderId()%>'>
								<%=folder.getFolderPath()%>
							</option>
						<%	
							}
						%>
						</select>
					</span>
				</td>
			</tr>

			<tr>
				<td colspan='2'>
				<div id='schedulerReportsInFolderDiv'></div>
				</td>
			</tr>
						
		</table>
<%}%>