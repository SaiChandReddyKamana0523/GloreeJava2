<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String IsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((IsLoggedIn == null) || (IsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String jDBDatabaseType = this.getServletContext().getInitParameter("databaseType");
	SecurityProfile jDBSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User jDBUser = jDBSecurityProfile.getUser();
	ArrayList jDBProjects = jDBSecurityProfile.getProjectObjects();
	
	
	
%>
<div>
	<table>
							<tr>
								<td width='150px' align='left'>
									<span class='normalText'>TraceCloud Project</span>
								</td>
								<td align='left'>
									<span class='normalText'>
										<select id='traceCloudProjectId' onChange='displayJiraProjectFolders();'>
										<option value='-1'>Select A TraceCloud Project to connect to </option>	
										<%
										String prefHideProjects = jDBUser.getPrefHideProjects();
										Iterator p = jDBProjects.iterator();
										while (p.hasNext()){
											Project project = (Project) p.next();
											boolean isAHiddenProject = false;
											if(prefHideProjects.contains( project.getProjectId() + ":#:" + project.getShortName()))
											{
												continue;
											}
											else {
											%>
											<option value='<%=project.getProjectId()%>'>
											<%=project.getShortName()%> : <%=project.getProjectName() %>
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
								<td colspan='2' align='left'>	
									<div id='jiraProjectFoldersDiv' style='display:none'></div>
									
								</td>
							</tr>
	</table>
</div>



