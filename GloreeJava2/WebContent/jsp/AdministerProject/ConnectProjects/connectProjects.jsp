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
	if ((isLoggedIn == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	ArrayList projectRelations = project.getProjectRelations(databaseType);
	ArrayList userProjects = securityProfile.getProjectObjects();
	
	
	String disabled = "DISABLED='DISABLED'";
	if (securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
		disabled = "";
	}
		
%>
	
<div id="cNPW" class="level1Box">

<table class='paddedTable' width='100%'>

	<tr>
		<td align='left' colspan='6' bgcolor='#99CCFF'>				
			<span class='subSectionHeadingText'>
			Connect Projects
			</span>
			<div style='float:right'>
				<a href='/GloreeJava2/documentation/help/administerAProject.htm' target='_blank'>
				<img src="/GloreeJava2/images/page.png"   border="0">
				</a>	
				&nbsp;&nbsp;
			</div>
		</td>
	</tr>
	<tr><td colspan='6'>&nbsp;</td></tr>
	<tr>
		<td align='left' colspan='6' >				
			<table>
				<tr>
					<td valign='top'>
						<span class='normalText' width='100'>Connect To</span>
					</td>
					<td>
						<span class='normalText'>
							<select id='connectToProjectId'>
							<%
								// lets get the list of projects where the user is an admin and then load them in
								// to the option list.
								Iterator p = userProjects.iterator();
								while (p.hasNext()){
									Project userProject = (Project) p.next();
									if (securityProfile.getRoles().contains("AdministratorInProject" + userProject.getProjectId())){
										// this user is an admin in this project. So we can let him connect the current project to it.
										// one last check we need to make sure is that this is not already a member of the 
										// connected projects list.
										boolean alreadyConnected = false;
										Iterator pr = projectRelations.iterator();
										while (pr.hasNext()){
											ProjectRelation projectRelation = (ProjectRelation) pr.next();
											if (userProject.getProjectId() == projectRelation.getRelatedProjectId()){
												alreadyConnected = true;
											}
										}
										if ((project.getProjectId() != userProject.getProjectId()) && (!alreadyConnected)){
											// at this point, we have a project that is not already connected to this
											// project and in which the current user is an admin.
											// we can give the connection option.
										%>
											<option value='<%=userProject.getProjectId() %>'> 
											<%=userProject.getShortName()%> : <%=userProject.getProjectName() %></option>
										<%
										}
									}
								}
							%>
							</select>
							<a href='#' onClick='document.getElementById("connectProjectsMoreInfoDiv").style.display ="block";'>More Info</a>
							<div id='connectProjectsMoreInfoDiv' style='display:none'>
								<div style='float:right'>
									<a href='#' onClick='document.getElementById("connectProjectsMoreInfoDiv").style.display ="none";'>Close</a>
								</div>
								<span class='normalText'>
								<br>
								Once two projects are connected, the users (based on their traceability permissions) 
								can create trace relationships between requirements within these two projects.
								<br><br>
								Please note that to connect two Projects you will need to be an administrator in both the projects. 
								The drop down list above lists projects in which you are a member. 
								</span>
							
							</div>
						</span>
						
					</td>
				</tr>
				<tr>
					<td>
						<span class='normalText'>Description</span>
						<sup><span style="color: #ff0000;">*</span></sup>
					</td>
					<td>
						<span class='normalText'>
						<textarea name="connectionDescription" id ="connectionDescription" rows="5" cols="50" ></textarea>
						</span>
					</td>
				</tr>
				<tr>
					<td colspan='2' align='left'>
						<span class='normalText'>
						<input type='button' <%=disabled%> id='connectProjectButton' value='Connect Projects' class='btn btn-sm btn-primary' 
						onClick='
							document.getElementById("connectProjectButton").disabled=true;
							connectProjects();'> </input>
						</span>
					</td>
				</tr>
				
			</table>
		</td>
	</tr>
	<tr><td colspan='6'>&nbsp;</td></tr>
	<tr>
		<td align='left' colspan='6' >				
			<span class='normalText'>
			<b>Connected Projects</b>
			</span>
		</td>
	</tr>	
	<tr><td colspan='6'>&nbsp;</td></tr>
<%if (projectRelations.size() == 0){ %>
	<tr>
		<td align='left' colspan='6' >				
			<span class='normalText'>
			This project is not connected to any other projects
			</span>
		</td>
	</tr>

<%} 
else {
%>
	<tr>
		<td >				
			<span class='normalText'>Project Prefix</span>
		</td>
		<td >				
			<span class='normalText'>Project Name</span>
		</td>
		<td >				
			<span class='normalText'>Connected By</span>
		</td>
		<td >				
			<span class='normalText'>Connected Date</span>
		</td>
		<td width='200' >				
			<span class='normalText'>Connection Description</span>
		</td>
		<td >				
			<span class='normalText'>Action</span>
		</td>
	</tr>	
<%
	Iterator i = projectRelations.iterator();
	while (i.hasNext()){
		ProjectRelation projectRelation = (ProjectRelation) i.next(); 
		Project relatedProject = projectRelation.getRelatedProject();
		
%>
	<tr>
		<td >				
			<span class='normalText'><%=projectRelation.getRelatedProjectShortName()%></span>
		</td>
		<td >				
			<span class='normalText' title ='<%=relatedProject.getProjectDescription()%>'>
			<%=relatedProject.getProjectName()%></span>
		</td>
		<td >				
			<span class='normalText'><%=projectRelation.getRelationMadeBy() %></span>
		</td>
		<td >				
			<span class='normalText'><%=projectRelation.getRelationMadeDt() %></span>
		</td>
		<td width='200' >				
			<span class='normalText'><%=projectRelation.getRelationDescription() %></span>
		</td>
		<td align='left'>				
			<span class='normalText'>
			<%
			if (
					(securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))
					&&
					(securityProfile.getRoles().contains("AdministratorInProject" + relatedProject.getProjectId()))
				){
				// the logged in user is an admin of the current project and an admin of the project he is trying
				// to disconnect.
			%>
				<span class='normalText'>
				<input type='button' id='disconnectProjectButton' value='Disconnect' class='btn btn-sm btn-danger' 
				onClick='
					document.getElementById("disconnectProjectButton").disabled=true;
					disconnectProjects(<%=relatedProject.getProjectId() %>);'> </input>
				</span>
			<%
			}

			%></span>
		</td>
	</tr>	

<%
	}
} %>
</table>
</div>

	