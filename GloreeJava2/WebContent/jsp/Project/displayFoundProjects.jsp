<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->    
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="java.sql.Date" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<!-- Get the list of my projects by calling the util. -->

<%
	//authorization
	// since we need authorization as well as authenticaiton we will use the 
	// security profile object.
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	if (securityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%
	
	// authorization : 
	// this module works only for onSite installation. else log out.
	}
	if (!(this.getServletContext().getInitParameter("installationType").equals("onSite"))) {
		return;
	}
	String projectSearchString = request.getParameter("projectSearchString");
		
	
	// there are times when the security profile could have changed due to the user's license management actions
	// so lets refresh his / her profile.
	securityProfile = new SecurityProfile(securityProfile.getUser().getUserId(),this.getServletContext().getInitParameter("databaseType"));
	session.setAttribute("securityProfile",securityProfile );
	 
	User user = securityProfile.getUser();
	
	// get the list of projects this user has access to
	ArrayList foundProjects = ProjectUtil.findProjects(projectSearchString);
	
%>
	<div class='alert alert-success'>
		<div style='float:right'>
			<a href='#' onClick='document.getElementById("foundProjectsDiv").style.display="none"'>Close</a>
		</div>
		<form method="post" action="/GloreeJava2/servlet/ProjectAction" id="form3"> 
		<input type="hidden" name="projectId" value= "" >
		<input type="hidden" name="action" value="">
		<table>			
		<%
		if (foundProjects.size() > 0 ){
		%>
			<tr><td colspan='5'>&nbsp;</td></tr>										
			<tr>
				<td width='120'> 
					<span class='headingText'>
					Project Prefix
					</span>
				</td>
				<td>
					<span class='headingText'> 
					Project Name 
					</span>
				</td>
				<td>
					<span class='headingText'> 
					Owner
					</span>
				</td>
				<td>
					<span class='headingText'> 
					Organization
					</span>
				</td>
				<td>
					<span class='headingText'> 
					Website
					</span>
				</td>
				<td> 
					<span class='headingText'>
					Created By 
					</span>
				</td>
				<td> 
					<span class='headingText'>
					Action 
					</span>
				</td>
			</tr>
			<%
			Iterator iP = foundProjects.iterator();
			while (iP.hasNext()){
				Project project = (Project) iP.next();
				String accessDisabled = "";
				if (!(securityProfile.getRoles().contains("MemberInProject" + project.getProjectId() ))){
					accessDisabled = "DISABLED";
				}
			%>
				<tr>
				<td align='left'> 
					<span class='normalText' title='<%=project.getProjectDescription() %>'>
					<img src="/GloreeJava2/images/project16.png" border="0">
					<%=project.getShortName() %> 
					</span>
				</td>
				<td align='left'> 
					<span class='normalText' title='<%=project.getProjectDescription() %>'>
					<%=project.getProjectName() %> 
					</span>
				</td>
				<td align='left'> 
					<span class='normalText'>
					<%=project.getProjectOwner() %>
					</span> 
				</td>
				<td align='left'>
					<span class='normalText'> 
					<%=project.getProjectOrganization() %>
					</span> 
				</td>
				<td align='left'>
					<% if ((project.getProjectWebsite() != null) && !(project.getProjectWebsite().equals (""))){
						if (!(project.getProjectWebsite().startsWith("http://"))){
							// lets add http:// to the beginning of url
					%>
							<a href='http://<%=project.getProjectWebsite() %>' TARGET='_blank'>Website</a>

					<%
						}
						else {
					%>
						<a href='<%=project.getProjectWebsite() %>' TARGET='_blank'>Website</a>
					<%
						}
					} %>
				</td>																	
			
				<td align='left'> 
					<span class='normalText'>
					<%=project.getCreatedBy() %>  
					</span>
				</td>
				<%if (accessDisabled.equals("") ){ %>
					<td align='left'>
						<a href='#' onClick= 'openProject("<%=project.getProjectId() %>")'>Open Project</a>
					</td>				
				<%}
				else {%>
					<td align='left'>
						<a href='#' onClick= 'requestAccessToProject(<%=project.getProjectId() %>)'>RequestAccess</a>
					</td>				
				<%} %>
				</tr>	
				<tr>
					<td colspan='7'>
						<div id='requestAccessDiv<%=project.getProjectId()%>' class='alert alert-success' style='display:none'>
						</div>
					</td>
				</tr>				
			<%} %>
		<%}
		else {%>
			 <tr><td colspan='5'>
			 	<span class='normalText'>
			 	No projects were found for your search criteria.
			 	</span>
			 </td></tr>
		<%} %>														
	</table>	
	</form>
</div>
