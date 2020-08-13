<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String deleteFolderFormILoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((deleteFolderFormILoggedIn == null) || (deleteFolderFormILoggedIn.equals(""))){
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
	boolean isAdmin = false;
	if (securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
		isAdmin = true;
	}
	
	if (isAdmin){
	
%>
	
	<div id='deleteFolderFormDiv' class='level1Box'>	
	<table   align="center"  width='100%'>
		<tr>
			<td colspan='2' align='left' bgcolor='#99CCFF'>				
				<span class='subSectionHeadingText'>
				Delete Folder
				</span>
			</td>
		</tr>	
	
	
	
		<% 
	    	String folderIdString = request.getParameter("folderId");
	    	int folderId = Integer.parseInt(folderIdString);
	    	Folder folder = new Folder(folderId);	
	    
	    	//
	    	//TODO : Taking out a root level folder , guarantees that all the sub folders and requirement of the type of root level folder (BR)
	    	// have already been deleted. Since we are enforcing the logic that only reqs of a type can exist in a root folder hierarchy, this 
	    	// should hold true. So, as part of the root folder deletion logic, we need to delete the requirement type (other wise there will not
	    	// be a way to re link the requirement type with the root folder. 
	    	// Also as part of this logic we should request that the user purge all deleted requirements of this type before we permit the root
	    	// level deletion.
	    	//
	    	if (folder.getFolderLevel() == 1 ){
	    %>
		<tr>
			<td>
				<div id='deleteFolderPromptDiv' class='alert alert-success'>
				<img src="/GloreeJava2/images/folder.png" border="0">&nbsp;<%=folder.getFolderName() %> is a Root level folder. This can not be deleted.
				</div>
			</td>
		</tr>
	    
	    <%
        	}
           	else {
           		ArrayList myFolders = FolderUtil.getSubFolders(folderId);
           		int numOfSubFolders = myFolders.size();
           	
       	    	ArrayList myRequirements = (ArrayList) folder.getMyRequirements(project.getProjectId(), databaseType);
	        	int numOfRequirements = myRequirements.size();
       		
	        	ArrayList myDeletedRequirements = (ArrayList) folder.getMyDeletedRequirements(project.getProjectId(), databaseType) ;
	        	int numOfDeletedRequirements = myDeletedRequirements.size();

           		ArrayList myWordTemplates = folder.getMyWordTemplates(project.getProjectId(), databaseType);
           		int numOfWordTemplates = myWordTemplates.size();
           	
				String deleteDisabled = "";
				if (
						(numOfSubFolders > 0) || 
						(numOfRequirements > 0) || 
						(numOfDeletedRequirements > 0)||
						(numOfWordTemplates > 0)
					){
					deleteDisabled = "DISABLED";
				}
	        	if (numOfSubFolders > 0) {
	        		// there are soem sub fodlers that will need to be deleted first.
	        %>
					<tr>
						<td>
							<div id='deleteFolderPromptDiv' class='alert alert-success'>
							<span class='normalText'>
							You need to delete the following sub folders before you can delete This Folder&nbsp;<%=folder.getFolderName() %>
							</span>
							</div>
						</td>
					</tr>
				<%
					Iterator i = myFolders.iterator();
					while (i.hasNext()){
						Folder sub = (Folder) i.next();
				%>
						<tr><td>
						<input type="button" name="Delete" value="Delete" onClick='deleteFolderForm("<%=sub.getFolderId()%>")'>
						&nbsp;<img src="/GloreeJava2/images/folder.png" border="0">&nbsp;<%=sub.getFolderName()%></td></tr>
				<% 	
					}
						
				}
				if (numOfWordTemplates > 0) {
					// There are some word templates in this folder that need to be purged first.
			%>
					<tr>
						<td>
						<div id='deleteFolderPromptDiv' class='alert alert-success'>
							<span class='normalText'>
							You need to Delete the following Word Templates before you can delete this Folder&nbsp;<%=folder.getFolderName() %>
							</span>
						</div>
						</td>
					</tr>
				<%
					Iterator i = myWordTemplates.iterator();
					while ( i.hasNext()){
						WordTemplate wordTemplate = (WordTemplate) i.next();
				%>
						<tr><td>
	 						<span class='normalText' title="Description : <%=wordTemplate.getTemplateDescription() %>">
 							<a href="#" 
	 							onClick="navigateToAndDisplayWordTemplate(<%=wordTemplate.getFolderId()%>, <%=wordTemplate.getTemplateId() %>)">
	 							<img src="/GloreeJava2/images/ExportWord16.gif" border="0">
	 						&nbsp;<%=wordTemplate.getTemplateName()%></a> 
	 						</span>
						</td></tr>
				<% 	
					}
				}

	        	if (numOfRequirements > 0) {
					// There are some reqs in this folder that need to be purged first.
			%>
					<tr>
						<td>
						<div id='deleteFolderPromptDiv' class='alert alert-success'>
							<span class='normalText'>
							You need to Purge (not Delete) the following requirements before you can delete this Folder&nbsp;<%=folder.getFolderName() %>
							</span>
						</div>
						</td>
					</tr>
				<%
					Iterator i = myRequirements.iterator();
					while ( i.hasNext()){
						Requirement requirement = (Requirement) i.next();
				%>
						<tr><td>
						<span class='normalText'>
						<img src="/GloreeJava2/images/puzzle16.gif" border="0">&nbsp;<%=requirement.getRequirementFullTag()%> : <%=requirement.getRequirementNameForHTML()%>
						</span>
						</td></tr>
				<% 	
					}
				}
				else if (numOfDeletedRequirements > 0) {
					// There are some reqs in this folder that need to be purged first.
			%>
					<tr>
						<td>
						<div id='deleteFolderPromptDiv' class='alert alert-success'>
							<span class='normalText'>
							You need to Purge the following Deleted Requirements before you can delete this Folder&nbsp;<%=folder.getFolderName() %>
							</span>
						</div>
						</td>
					</tr>
				<%
					Iterator i = myDeletedRequirements.iterator();
					while ( i.hasNext()){
						Requirement requirement = (Requirement) i.next();
				%>
						<tr><td>
						<span class='normalText'>
						<img src="/GloreeJava2/images/puzzle16.gif" border="0">&nbsp;<%=requirement.getRequirementFullTag()%> : <%=requirement.getRequirementNameForHTML()%>
						</span>
						</td></tr>
				<% 	
					}
				}
	        	
				else {
					// we can now let the user delete the folder.
					if (!deleteDisabled.equals("DISABLED")){
		%>
		
					<tr>
						<td>
							<span class='normalText'>
							Yes. I want to Delete the Folder &nbsp;<%=folder.getFolderName() %>
							</span>
						</td>
					</tr>
					<tr>
						<td>
							<span class='normalText'>
							<input type="button" '<%=deleteDisabled%>' name="Delete Folder" value="Delete Folder" onClick='deleteFolder("<%=request.getParameter("folderId")%>")'>
							</span>
						</td>
					</tr>				
		<%
					}
				}
		    }
		%>	
	</table>
	</div>
<%}
	else {
%>

	<div class='alert alert-danger'>
	
		Only Administrators are allowed to change the structure of a project. Please work with this Project's administrator.
	
	</div>



<%}%>