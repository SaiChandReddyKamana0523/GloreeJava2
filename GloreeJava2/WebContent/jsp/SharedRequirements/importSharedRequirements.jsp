<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
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
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
%>
	
	
		
	<div id='publishSharedRequirementsFormDiv' class='level2BoxColored'>	
		<table class='paddedTable' width='100%'>
			<tr>
				<td colspan='2' align='left' bgcolor='#99CCFF'>				
					<span class='subSectionHeadingText'>
					Import Shared Requirements
					</span>
					<div style='float:right'>
						<span title='Shared Requirements Help Video'>
						<a target="_blank" href="http://www.youtube.com/watch?v=CdJNHFOnSBU&hd=1">
						<img height="20" border="0" src="/GloreeJava2/images/television.png"/>
						</a>
						</span>
					</div>	
				</td>
			</tr>		

			<tr> 
				<td width='190' valign='top'>
					<span class='normalText'>
					 Source Project
					 </span>
				</td>
				<td> 
					<span class='normalText'>
						<select name='sharedProjectId' id='sharedProjectId'
						onChange='
							var sharedProjectIdObject = document.getElementById("sharedProjectId");
							if (sharedProjectIdObject.selectedIndex > 0){
								displayImportSharedRequirementTypeForm();
							}
							'>
						<option value=0> Select A Source Project</option>
						<%
						ArrayList sourceProjects = SharedRequirementUtil.getSharedProjects(); 	
						Iterator i = sourceProjects.iterator();
						while (i.hasNext()){
							Project sourceProject = (Project) i.next();
							if (sourceProject.getProjectId() == project.getProjectId()){
								// you can not import into the same project.
								continue;
							}
							if (!(securityProfile.getRoles().contains("MemberInProject" + sourceProject.getProjectId()))){
								// the user is not a member of the project he is trying to import form. hence ignore this
								continue;
							}
						%>
							<option value='<%=sourceProject.getProjectId() %>'><%=sourceProject.getShortName()%> : <%=sourceProject.getProjectName() %></option>
						<%}%>
						 </select>
					</span>
				</td>
			</tr>
		</table>
	</div>
	<div id='importSharedRequirementTypeDiv'></div>
<%}%>