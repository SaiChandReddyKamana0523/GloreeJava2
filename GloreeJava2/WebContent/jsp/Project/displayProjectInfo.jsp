<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String displayProjectInfoIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayProjectInfoIsLoggedIn  == null) || (displayProjectInfoIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project dPIProject= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)

	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + dPIProject.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
	%>	
	<div id = 'projectInfoDiv' class='level1Box'>

		<%
		// lets see if there is a message for the user. if so, we will display it.
		String message = (String) session.getAttribute("message");
		if ((message != null) && (!(message.equals("")))){
			session.removeAttribute("message");
		%>
			<table   width="100%" align="center" class='paddedTable'>
			<tr>
				<td>
					<div class='alert alert-success'>
						<span class='normalText'><br><%=message%></span>
					</div>
				</td>
			</tr>
			</table>
		<%}
		else {%>		
			<table   width="100%" align="center" class='paddedTable'>
				<!--  lets get the project details displayed -->
				<tr>
					<td colspan='2'>
						<table class='paddedTable' width='100%'>
							<tr>
							<td align='left' bgcolor='#99CCFF'>				
								<span class='subSectionHeadingText'>
								<img src="/GloreeJava2/images/project16.png"> 
								<%=dPIProject.getShortName()%> : <%=dPIProject.getProjectName()%>
								</span>
							</td>
							</tr>
						</table>
					</td>
				</tr>

				<tr><td colspan='2'>&nbsp;</td></tr>				

				<tr>
					<td align='left'   colspan='2'>
						<span class='sectionHeadingText'>
							<img src="/GloreeJava2/images/project16.png">
							<b>Project Core Info </b>
						</span>
					</td>
				</tr>				
					
				<tr>
					<td   width='150'>
						<span class='headingText'>
							Prefix
						</span>
					</td>
					<td align='left'  >
						<span class='normalText'>
							<%=dPIProject.getShortName() %>
						</span>
					</td>
				</tr>
				<tr>
					<td    >
						<span class='headingText'>
							Name
						</span>
					</td>
					<td align='left'  >
						<span class='normalText'>
							<%=dPIProject.getProjectName() %>
						</span>
					</td>
				</tr>				
				<tr>
					<td  >
						<span class='headingText'>
							Description
						</span>
					</td>
					<td align='left'  >
						<span class='normalText'>
							<%=dPIProject.getProjectDescription() %>
						</span>
					</td>
				</tr>				
				<tr>
					<td  >
						<span class='headingText'>
							Owner
						</span>
					</td>
					<td align='left'  >
						<span class='normalText'>
							<%=dPIProject.getProjectOwner() %>
						</span>
					</td>
				</tr>				
				<tr>
					<td  >
						<span class='headingText'>
							Website 
						</span>
					</td>
					<td align='left'>
						<% if ((dPIProject.getProjectWebsite() != null) && !(dPIProject.getProjectWebsite().equals (""))){
							if (!(dPIProject.getProjectWebsite().startsWith("http://"))){
								// lets add http:// to the beginning of url
						%>
								<a href='http://<%=dPIProject.getProjectWebsite() %>' TARGET='_blank'><%=dPIProject.getProjectWebsite() %></a>

						<%
							}
							else {
						%>
							<a href='<%=dPIProject.getProjectWebsite() %>' TARGET='_blank'><%=dPIProject.getProjectWebsite() %></a>
						<%
							}
						} %>
					</td>											
				</tr>				
				<tr>
					<td  >
						<span class='headingText'>
							Organization
						</span>
					</td>
					<td align='left'  >
						<span class='normalText'>
							<%=dPIProject.getProjectOrganization() %>
						</span>
					</td>
				</tr>				
				<tr>
					<td  >
						<span class='headingText'>
							Tags
						</span>
					</td>
					<td align='left'  >
						<span class='normalText'>
							<%=dPIProject.getProjectTags() %>
						</span>
					</td>
				</tr>				



				<tr>
					<td  >
						<span class='headingText'>
							Created By 				
						</span>
					</td>
					<td align='left'  >
						<span class='normalText'>
							<%=dPIProject.getCreatedBy()%>				
						</span>
					</td>
				</tr>				
				
				
				
				
				<tr><td colspan='2'>&nbsp;</td></tr>							
				<tr>
					<td align='left'   colspan='2'>
						<span class='sectionHeadingText'>
							<img src="/GloreeJava2/images/project16.png">
							<b>Project Administrators </b>
						</span>
					</td>
				</tr>				
				<tr>
					<td  >
						<span class='headingText'>
							Administrators
						</span>
					</td>
					<td align='left'  >
						<span class='normalText'>
							<%
							ArrayList administrators = ProjectUtil.getProjectAdministrators(dPIProject.getProjectId());
							Iterator i = administrators.iterator();
							while(i.hasNext()){
								String admin = (String) i.next();
							%>
								<%=admin%> &nbsp;&nbsp;
							<%	
							}
								
							%>
						</span>
					</td>
				</tr>				
				


				<tr><td colspan='2'>&nbsp;</td></tr>
				<tr>
					<td   colspan='2'>
						<span class='sectionHeadingText'>
							<img src="/GloreeJava2/images/project16.png">
							<b>Requirement Types </b>
						</span>
					</td>
				</tr>				
				
				<%
				ArrayList requirementTypes = dPIProject.getMyRequirementTypes(); 	
				Iterator j = requirementTypes.iterator();
				
				while (j.hasNext()){
					RequirementType rt = (RequirementType) j.next();
				%>
				<tr>
					<td  >
						<span class='headingText'>
							<%=rt.getRequirementTypeShortName()%>
							: 
							<%=rt.getRequirementTypeName() %>
						</span>
					</td>
					<td align='left'  >
						<span class='normalText'>
							<%=rt.getRequirementTypeDescription()%>
						</span>
					</td>
				</tr>				
				<%} %>
			</table>
		<%} %>
	</div>
<%}%>