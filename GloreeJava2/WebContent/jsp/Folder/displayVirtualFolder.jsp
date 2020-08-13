<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String displayVirtualFolderIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayVirtualFolderIsLoggedIn  == null) || (displayVirtualFolderIsLoggedIn.equals(""))){
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
	
	
	
	
	<% 
		// Note since, the call is coming from a virtual folder id, the folder id will be of the format 
		// -1:requirement_type_id
	    String folderId = request.getParameter("folderId");
		String[] temp = folderId.split(":");
		String requirementTypeIdString = temp[1];
	 
	%>
	
	<div id = 'folderInfoDiv' class='level1Box'>
		<fieldset id="folder">
			<table   align="center" >
				<tr>
					<td colspan=2 align=center valign=bottom>
					<div id ='folderActions' class='level2Box'></div>
					</td>
				</tr>
			
				
				<!--  lets get the folder details displayed -->
				<tr>
					<td colspan='2' align='left'>
						<div id = 'folderDetails' class='level2Box'>
						<table cellpadding='5' cellspacing='5'>
						    <tr>
						    	<td colspan='2'> This is a virtual folder created to hold all the Deleted Requirements. <br>
						    	
						    	<%
									if (securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
								%>
							    	
							    	<a onclick="document.getElementById('purgeAllDeletedRequirementsPromptDiv').style.display = 'block';" 
							    	href="#"> Purge all Deleted Requirements </a>
								    <div class="actionPrompt" id="purgeAllDeletedRequirementsPromptDiv" style="display:none;">
						    		<br/><br/><b> 
						    		<span class="headingText">
						    			Are you sure you want to permanently remove All Deleted Requirements in this folder? 
										<br/>Please note that this will permanently remove all the (already deleted) Requirements,
										 their Attributes and Traces.</b><br/><br/>
										<input type="button" onclick='purgeAllDeletedRequirementsInRequirementType(<%=requirementTypeIdString %>)' value="Purge" name="Purge"/>
										<input type="button" onclick="document.getElementById('purgeAllDeletedRequirementsPromptDiv').style.display = 'none';" value="Cancel" name="Cancel"/>
										<br/>
										</span>
									</div>
								<%} %>
						    	 </td>
							</tr>
						</table>
						</div>
					</td>
				</tr>	
			</table>
		</fieldset>
	</div>
<%}%>