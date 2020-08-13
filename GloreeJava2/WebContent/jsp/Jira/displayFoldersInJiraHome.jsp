<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String moveRequirementFormIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((moveRequirementFormIsLoggedIn == null) || (moveRequirementFormIsLoggedIn.equals(""))){
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
			<td width='95px'>
				<span class='normalText'>
					Folder 
				</span>
			</td>
			<td>
				<span class='normalText'>
					<select id='folderId' onChange='displayRequirementsInJiraHome(<%=projectId%>,"folder");'>
						<option value='-1'> Select A Folder to display requirements </option>
						<%
						Iterator f = folders.iterator();
						while (f.hasNext()){
							Folder folder = (Folder) f.next();
							%>	
								
								<option value='<%=folder.getFolderId()%>'>
									<%=folder.getFolderPath() %>
								</option>
						<%	
						}%>
					</select>
					<!--  
					<input type='button'  style='width:150px' name='Go' value='Go' onClick='displayRequirementsInJiraHome(<%=projectId%>,"folder");'>
					-->
						
				</span>
			</td>
		</tr>
		<tr>
			<td colspan='2'>
				<a href='#' onClick="document.getElementById('moreSearchOptionsDiv').style.display='block'">More Search Options</a>
			</td>
		</tr>
		<tr>
			<td colspan='2'>
				<div id='moreSearchOptionsDiv' style='display:none'>
				<table>
					<tr>   
						<td>
							<span class='normalText'>Req Id</span>
						</td>
						<td>
							<span class='normalText'></span>
							<input type='text' size='8' value='BR-1,fr-2' maxlength='300' name='reqIdSearchString' id='reqIdSearchString'
								onkeypress='handleDisplayRequirementsInJiraHomeKeyPress(event, <%=projectId%>,"reqId");'
							>
							<input type='button' style='width:150px' name='Go' value='Go' onClick='displayRequirementsInJiraHome(<%=projectId%>,"reqId");'>
						
						</td>
					</tr>		
					<tr>
						<td>
							<span class='normalText'>Keywords</span>
						</td>
						<td>
							<input type='text' size='20' maxlength='300' name='googleSearchString' id='googleSearchString'
								onkeypress='handleDisplayRequirementsInJiraHomeKeyPress(event, <%=projectId%>,"keyword");'
							>
							<input type='button'  style='width:150px' name='Go' value='Go' onClick='displayRequirementsInJiraHome(<%=projectId%>,"keyword");'>
						
						</td>
					</tr>			
				</table>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan='2'>	
				<div id='displayRequirementsInJiraHomeDiv' style='display:none'></div>
			</td>
		</tr>
	</table>
</div>



