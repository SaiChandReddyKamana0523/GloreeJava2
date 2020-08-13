<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>



<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String displayRealFolderIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayRealFolderIsLoggedIn  == null) || (displayRealFolderIsLoggedIn.equals(""))){
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
		Folder folder = (Folder) request.getAttribute("folder");
		// if the folder object exists, it means a call was made to FolderAction with a request to create a folder.
		// the folder object now contains the data for the newly created folder.
	    if (folder == null) {
	    	// This means that no new folders were created prior to this call.
	    	String folderIdString = request.getParameter("folderId");
	    	int folderId = Integer.parseInt(folderIdString);
	    	folder = new Folder(folderId);	
	    }
		
		// if the user does not have 'Create Requirements' priv for this folder
		// we disable both the createRequirements and Import Excel buttons.
		// The rest of the buttons View Report, Create Sub folder, Delete Sub Folder , Edit folder
		// etc.. are available to regular users.
		// Note : Delete Sub folder is controlled by whether the user can delete the underlying 
		// requirements or not.		
		String createRequirementsDisableString = "";
		boolean readRequirements = true;
		boolean createRequirements = true;
		if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" 
				+ folder.getFolderId()))){
			createRequirementsDisableString = "disabled='disabled'";
			createRequirements = false;
		}
		
		RequirementType requirementType = new RequirementType(folder.getRequirementTypeId());

		// LICENSE MANAGEMENT
		// createRequirements boolean controls Create Reqs, Edit Folders, Import Reqs and New Template tab
		// so lets leverage that to control the system.
		// Algorithm:
		// For 'Project License' projects, this is not applicable.
		//  
		// if (the user is on trial and
		//		this is a 'non Project License project' and 
		// 			(either this project has more than 500 requirements
		//			or
		//			The user has more than 5000 requirements in TraceCloud)
		// then createRequiremets is off.
		User user = securityProfile.getUser();
		
			
		// lets see if the user has read permissions on this folder. if he does not
		// then we turn off both read Requirements and create Requiremetns permissions
		// which will effectively gray out all the read accesses.
		if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
				+ folder.getFolderId()))){
			createRequirements = false;
			readRequirements = false;
		}
		
	%>
	

	<!-- 
		<div class='alert alert-success'>
		
			<div class='bs-callout bs-callout-danger'>
				<span class='normalText'><b><font color='red'>
				We are in the process of replacing our SSL Certificate. 
				Please note that the system is in maintenance till 1:00 PM PST on Saturday  (09/24/2016). 
					<br>
					You may be occasionally forced to re-login in to the system during this period.
					</font>
				<b></span>
			</div>
		</div>
	-->
	<div id = 'folderInfoDiv' class='alert alert-info' >
		<a name="TopOfFolder" id='TopOfFolder'></a>	
		


			<table   width="100%" align="center" >
				<!--  lets get the folder details displayed -->
				<tr>
					<td  align='left'>
					  <div >
					 
						<div id = 'folderDetails' class='level2Box' style='float:left'>
							<span class='normalText' title="Description : <%=folder.getFolderDescription() %>">
									<img height='16' width='16' src="/GloreeJava2/images/folder.png" border="0"> <b> <%=folder.getFolderPath() %> </b>
							</span>
							
						
							<div class="btn-group" style='margin-left:0px'>
							  <button type="button"  
							  	class="btn btn-xs  btn-outline-primary"  
								style="border-color:white; color:blue;  background-color:white;"
								>Actions</button>
							  <button type="button" 
							  	class="btn btn-xs btn-outline-primary dropdown-toggle"  
								style="border-color:white; color:blue;"
							 	data-toggle="dropdown" aria-expanded="false">
								<span class="caret"></span>
								<span class="sr-only">Toggle Dropdown</span>
							  </button>
							  <ul class="dropdown-menu" role="menu">
								<% 
								
								String shortenedReqTypePrefix = requirementType.getRequirementTypeShortName();
								if (shortenedReqTypePrefix.length() > 10){
									shortenedReqTypePrefix = shortenedReqTypePrefix.substring(0,8);
								}
								%>
								
							   
							   
								<%if (createRequirements) { %>		
									
									<li>
										<a href="#" 
											onClick='createRequirementForm("<%=folder.getFolderId()%>"); 
														document.getElementById("folderMenuCreateNewDiv").style.display="none" 
													'
										>
											Create New <%=folder.getRequirementTypeName() %>
										</a>
									</li>
										
								  	<li>
										<a href="#"  
											onClick='createBulkRequirementForm("<%=folder.getFolderId()%>"); document.getElementById("folderMenuCreateNewDiv").style.display="none" '
										>
											Create multiple <%=folder.getRequirementTypeName() %>
										</a>
									</li>
										
								  	<li>
										<a href="#"  
											onClick='importFromExcelForm("<%=folder.getFolderId()%>"); document.getElementById("folderMenuCreateNewDiv").style.display="none"'
										>
											
											Import <%=folder.getRequirementTypeName() %> from Excel 
										</a>
									</li>
							  
							  		<%
								  	if (!(project.getProjectTags().toLowerCase().contains("school_project"))){
								  	%>
								  		<li>
											<a href="#"  
												onClick='createWordTemplateForm("<%=folder.getFolderId()%>"); document.getElementById("folderMenuCreateNewDiv").style.display="none"'
											>
												
												Import <%=folder.getRequirementTypeName() %> from Word   
											</a>
										</li>
							  		<%} %>
							  
							  		 <li role="presentation" class="divider"></li>	
														
							  		<li>
										<a href="#"  
											onClick='editFolderForm("<%=folder.getFolderId()%>");  document.getElementById("folderMenuCreateNewDiv").style.display="none" '
										>
											Edit Folder   
										</a>
									</li>
											
									<li>
										<a href="#"  
											onClick='createFolderForm("<%=folder.getFolderId()%>");  document.getElementById("folderMenuCreateNewDiv").style.display="none" '
										>
											Create a Sub-Folder   
										</a>
									</li>
											
											
													
											
									
									
									<%}%>
									 <li role="presentation" class="divider"></li>	
											
									<%if (readRequirements) { %>		
								  		<li>
											<a href="#"  
												onClick='
													displaySavedReportsAndTemplates("<%=folder.getFolderId()%>");
												'
											>
												Saved Reports   
											</a>
										</li>
															
										<li>
											<a href="#"  
												onClick='
													createNewListReport("<%=folder.getFolderId()%>"); document.getElementById("folderMenuReportsDiv").style.display="none" 
												'
											>
												Create A Report   
											</a>
										</li>
															
										<li>
											<a href="#"  
												onClick='createNewTraceTreeReport("<%=folder.getFolderId()%>");  document.getElementById("folderMenuReportsDiv").style.display="none" '
											>
												Create A Trace Tree   
											</a>
										</li>
									
										
								  		<li>
											<a href="#"  
												onClick='createWordTemplateForm("<%=folder.getFolderId()%>"); document.getElementById("folderMenuCreateNewDiv").style.display="none"'
											>
												
												Create A Word Template Based Report   
											</a>
										</li>
										
											
										<li role="presentation" class="divider"></li>	
									
										<li>
											<a href="#"  
												onclick='
													displayFolderCore("<%=folder.getFolderId()%>");  document.getElementById("folderMenuReportsDiv").style.display="none" ;
													document.getElementById("contentCenterC").style.display = "none";
													document.getElementById("contentCenterD").style.display = "none";
													document.getElementById("contentCenterE").style.display = "none";
													document.getElementById("contentCenterF").style.display = "none";
												' 
											>
												Folder Info   
											</a>
										</li>
										<li role="presentation" class="divider"></li>	
											
										<li>
											<a href="#"  
												onclick='displayFolderMetrics(<%=folder.getFolderId()%>); document.getElementById("folderMenuReportsDiv").style.display="none" ; '
											>
												Folder Dashboard   
											</a>
										</li>		
								<%}%>									
																		
							  </ul>
							</div>
						
							&nbsp;&nbsp;&nbsp;&nbsp;
							<a href="#"    id='createRequirementFormButton' 
								class="btn btn-xs btn-outline-primary"  
								style="border-color:white; color:blue; background-color:white;"
					 			onClick='createRequirementForm("<%=folder.getFolderId()%>"); 
											document.getElementById("folderMenuCreateNewDiv").style.display="none" 
										'
							>
								Create <%=folder.getRequirementTypeShortName() %>
							</a>
						</div>					 	
						
					
						
						
						<div id='returnDiv' style='display:none; '></div> 
						
						
						
					</div>
					</td>
				</tr>	

				<% if(project.getArchived() == 1) { %>
				<tr>
					<td align='center'>
						<div class='alert alert-success'>
						<b><font color='red'>This is an ARCHIVED project. Please work with your project administrator to RE ACTIVATE it before making any changes in the project </font></b></div>
					</td>
				</tr>	
				<%} %>

			</table>
		
	</div>
<%}%>