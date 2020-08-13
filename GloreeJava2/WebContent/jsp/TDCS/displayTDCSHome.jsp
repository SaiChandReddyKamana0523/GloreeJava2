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
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project dPIProject= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)

	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + dPIProject.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
		
		User user = securityProfile.getUser();
		String tDCSFilter = "project";
		String tDCSFilterValue = "";
		String tDCSSortBy = "documentId";
		
		int maxsize = 200;
		ArrayList tDCSDocuments = TDCSUtil.getTDCSDocuments(tDCSFilter,tDCSFilterValue,tDCSSortBy, dPIProject.getProjectId(),maxsize, databaseType);
		
		String tDCSDisplay = request.getParameter("tDCSDisplay");
		// to avoid a null pointer exception lets add an empty string to tDCSDisplay.
		if (tDCSDisplay == null){
			tDCSDisplay = "title, documentStatus, author";
		}
	%>	
	<div id = 'tDCSInfoDiv' class='level1Box'>

		<table   width="100%" align="center" class='paddedTable'>
			<tr>
				<td colspan='2'>
					<table class='paddedTable' width='100%'>
						<tr>
						<td align='left' bgcolor='#99CCFF'>				
							<span class='subSectionHeadingText'>
							<img src="/GloreeJava2/images/database_refresh16.png"> 
							TraceCloud Document Control System
							</span>
						</td>
						</tr>
					</table>
				</td>
			</tr>

			<tr><td colspan='2'>&nbsp;</td></tr>				

			<tr>
				<td colspan='2'>
					<div>
					<table>
					<tr>
						<td>
							<span class='normalText'>
								Show TDCS Documents
							</span>
							&nbsp;&nbsp;
							<span class='normalText'>
							<select id='tDCSFilter' onChange='
								var filterObject = document.getElementById("tDCSFilter");
								if (filterObject.selectedIndex == 0){
									document.getElementById("tDCSFilterOptionDiv-Folder").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-FileType").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-DocumentStatus").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-ApprovalStatus").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-Title").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-DocumentId").style.display = "none";
								} 
								
								
								if (filterObject.selectedIndex == 1){
									document.getElementById("tDCSFilterOptionDiv-Folder").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-FileType").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-DocumentStatus").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-ApprovalStatus").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-Title").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-DocumentId").style.display = "none";
									
									document.getElementById("tDCSFilterOptionDiv-Folder").style.display = "block";
								} 
								
								if (filterObject.selectedIndex == 2){
									document.getElementById("tDCSFilterOptionDiv-Folder").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-FileType").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-DocumentStatus").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-ApprovalStatus").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-Title").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-DocumentId").style.display = "none";
									
									document.getElementById("tDCSFilterOptionDiv-FileType").style.display = "block";
								} 
								
								if (filterObject.selectedIndex == 3){
									document.getElementById("tDCSFilterOptionDiv-Folder").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-FileType").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-DocumentStatus").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-ApprovalStatus").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-Title").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-DocumentId").style.display = "none";
									
									document.getElementById("tDCSFilterOptionDiv-DocumentStatus").style.display = "block";
								} 
								if (filterObject.selectedIndex == 4){
									document.getElementById("tDCSFilterOptionDiv-Folder").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-FileType").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-DocumentStatus").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-ApprovalStatus").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-Title").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-DocumentId").style.display = "none";
									
									document.getElementById("tDCSFilterOptionDiv-ApprovalStatus").style.display = "block";
								} 
								if (filterObject.selectedIndex == 5){
									document.getElementById("tDCSFilterOptionDiv-Folder").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-FileType").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-DocumentStatus").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-ApprovalStatus").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-Title").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-DocumentId").style.display = "none";
											
									document.getElementById("tDCSFilterOptionDiv-Title").style.display = "block";
								} 
							
								if (filterObject.selectedIndex == 6){
									document.getElementById("tDCSFilterOptionDiv-Folder").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-FileType").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-DocumentStatus").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-ApprovalStatus").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-Title").style.display = "none";
									document.getElementById("tDCSFilterOptionDiv-DocumentId").style.display = "none";
									
									document.getElementById("tDCSFilterOptionDiv-DocumentId").style.display = "block";
								} 
							'>
								<option value='project'>In Entire Project</option>
								<option value='folder'>In Folder</option>
								<option value='fileType'>of File Type</option>
								<option value='documentStatus'>of Document Status</option>
								<option value='approvalStatus'>of Approval Status</option>
								<option value='title'>with Title or Description Like</option>
								<option value='documentId'>with Document Id</option>
								
							</select> 
							</span>
						</td>
						<td>
							
							<div id='tDCSFilterOptionDiv-Folder' style='display:none'>
								<span class='normalText'>
									<select id='tDCSFilterValue-Folder'>
										<%
										ArrayList folders = TDCSUtil.getFoldersWithDocuments(dPIProject.getProjectId());
										Iterator f = folders.iterator();
										while (f.hasNext()){
											Folder folder = (Folder) f.next();
										%>
										<option value='<%=folder.getFolderId()%>'><%=folder.getFolderPath()%></option>
										<%	
										}
										%>
									</select>
								</span>
							</div>
							
							
							<div id='tDCSFilterOptionDiv-FileType' style='display:none'>
								<span class='normalText'>
									<select id='tDCSFilterValue-FileType'>
										<option value='excel'>Excel</option>
										<option value='word'>Word</option>
										<option value='pdf'>PDF</option>
									</select>
								</span>
							</div>
							<div id='tDCSFilterOptionDiv-DocumentStatus' style='display:none'>
								<span class='normalText'>
									<select id='tDCSFilterValue-DocumentStatus'>
										<option value='locked'>Locked</option>
										<option value='unlocked'>Unlocked</option>
									</select>
								</span>
							</div>
							<div id='tDCSFilterOptionDiv-ApprovalStatus' style='display:none'>
								<span class='normalText'>
									<select id='tDCSFilterValue-ApprovalStatus'>
										<option value='draft'>Draft</option>
										<option value='inApprovalWorkflow'>In Approval Workflow</option>
										<option value='rejected'>Rejected</option>
										<option value='approved'>Approved</option>
									</select>
								</span>
							</div>
							<div id='tDCSFilterOptionDiv-Title' style='display:none'>
								<span class='normalText'>
									<Input type='text' id='tDCSFilterValue-Title' size='30' value=''></Input>
								</span>
							</div>

							<div id='tDCSFilterOptionDiv-DocumentId' style='display:none'>
								<span class='normalText'>
									<Input type='text' id='tDCSFilterValue-DocumentId' size='30' value='TDCS-XXX'></Input>
								</span>
							</div>
														
						</td>
						<td>
							<span class='normalText'>
								Display <br>(Ctrl+Click to multi select)
							</span>
						</td>
						<td>
							&nbsp;&nbsp;
							<span class='normalText'>
								<select id='tDCSDisplay' MULTIPLE SIZE='3'>
									<option value='title' SELECTED>Title</option>
									<option value='documentStatus' SELECTED>Document Status</option>
									<option value='author' SELECTED>Author</option>
									<option value='lastModifiedDt' >Last Modified Date</option>
									<option value='approvalStatus' >Approval Status</option>
									<option value='folderPath'>Folder Path</option>
									<option value='notes'>Notes</option>
									<option value='systemLog'>System Log</option>
								</select> 
							</span>
						</td>
						<td>
							<span class='normalText'>
								Sort By 
							</span>
							&nbsp;&nbsp;
							<span class='normalText'>
								<select id='tDCSSortBy'>
									<option value='documentId'>Document Id</option>
									<option value='title'>Title</option>
									<option value='documentStatus'>Document Status</option>
									<option value='approvalStatus'>Approval Status</option>
								</select> 
							</span>
						</td>
						<td>
							<input type = 'button' id='filterTDCSDocumentsButton' value ='Go'
							onClick='filterTDCSDocuments()'></input>
						</td>
					</tr>
					</table>
					</div>
				</td>
			</tr>				
			
			
			
			
			<tr>
				<td colspan='2'>
					<div id='tDCSDocumentsDiv'>
				
					<% 
						if (tDCSDocuments.size() == 0){ 
					%>
					<table>
					<tr>
						<td >
							<span class='normalText'>
								There are no Documents stored in this projects
							</span>	
						</td>
					</tr>		
					</table>
					<%} 
					else {
						int columnCount = 4;
						int j = 0;
				    	String cellStyle = "normalTableCell";
					%>
						<table width='100%'>
							<tr>
								<td width='100'>
									<span class='sectionHeadingText'>Action</span>
								</td>
								<td>
									<span class='sectionHeadingText'>Document Id</span>
								</td>
								<td>
									<span class='sectionHeadingText'>Version</span>
								</td>
								<td>
									<span class='sectionHeadingText'>Title</span>
								</td>
								<% if (tDCSDisplay.contains("documentStatus")){  
									columnCount++;
								%>
									<td>
										<span class='sectionHeadingText'>Document Status</span>
									</td>
								<%} %>
								<% if (tDCSDisplay.contains("author")){   
									columnCount++;
								%>
								<td>
									<span class='sectionHeadingText'>Author</span>
								</td>
								<%} %>
								<% if (tDCSDisplay.contains("lastModifiedDt")){   
									columnCount++;
								%>
								<td>
									<span class='sectionHeadingText'>Date</span>
								</td>
								<%} %>
								<% if (tDCSDisplay.contains("approvalStatus")){   
									columnCount++;
								%>
								<td>
									<span class='sectionHeadingText'>Approval Status</span>
								</td>
								<%} %>
								<% if (tDCSDisplay.contains("folderPath")){   
									columnCount++;
								%>			
								<td>
									<span class='sectionHeadingText'>Folder Path</span>
								</td>
								<%} %>
								<% if (tDCSDisplay.contains("notes")){   
									columnCount++;
								%>
								<td>
									<span class='sectionHeadingText'>Notes</span>
								</td>
								<%} %>
								<% if (tDCSDisplay.contains("systemLog")){   
									columnCount++;
								%>
								<td>
									<span class='sectionHeadingText'>SystemLog</span>
								</td>
								<%} %>
							</tr>	
						
				<%
					Iterator i = tDCSDocuments.iterator();
					int counter = 0;
					while (i.hasNext()){
						j++;
						if ((j%2) == 0){
			    			cellStyle = "normalTableCell";
			    		}
			    		else {
			    			cellStyle = "altTableCell";	
			    		}
						TDCSDocument tDCSDocument = (TDCSDocument) i.next();
							if (counter++ >= maxsize){
							%>
								<tr>
								<td class='<%=cellStyle%>' colspan='8'>
									<div class='alert alert-success'>
									<span class='normalText'>
									Showing the first <%=maxsize %>  of the available documents.
									 To see a specific set of documents , please use a filter condition
									 </span>
									</div>
								</td>
							</tr>
							
							<%return;
							}%>
							<tr>
								<td class='<%=cellStyle%>'>
									<span class='normalText'>
										<select id='tDCSAction<%=tDCSDocument.getDocumentId()%>'
											onChange='
											processTDCSDocumentAction(<%=tDCSDocument.getDocumentId()%>);
											'>
											<option value=''>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
											<% if (tDCSDocument.getCurrentVersionNumber() > 1){ %>
												<option value='showPreviousVersions'>Show Previous Versions</option>
											<%} %>
											<%
												if (
													(tDCSDocument.getCurrentVersionDocumentStatus().equals("unlocked"))
													&&
													((securityProfile.getPrivileges().contains("createRequirementsInFolder" 
															+ tDCSDocument.getFolderId())))
													){
													// This file is unlocked and this user has create permissions
													// hence we can show the lock document option.
											%>
												<option value='lockDocument'>Lock Document</option>
											<%} %>
											<%
												if (
													(tDCSDocument.getCurrentVersionDocumentStatus().equals("locked"))
													&&
													(
															(securityProfile.getRoles().contains("AdministratorInProject" + tDCSDocument.getProjectId()))
															||
															(tDCSDocument.getCurrentVersionDocumentStatusBy().equals(user.getEmailId()))
													)
												){
													// This file is locked and the user is either an admin on the project
													// or was the person who locked this file in the first place.
											%>
												<option value='unlockDocument'>Unlock Document</option>
											<%} %>
				
										</select>
									</span>
								</td>
								<td class='<%=cellStyle%>'>
									<%	if ((securityProfile.getPrivileges().contains("readRequirementsInFolder" 
											+ tDCSDocument.getFolderId()))){
									 %>
									<a 
									title='<%=tDCSDocument.getCurrentVersionNotes() %>'
									target="_blank" href="/GloreeJava2/servlet/TDCSAction?action=downloadDocument&tDCSDocumentId=<%=tDCSDocument.getDocumentId()%>&versionNumber=<%=tDCSDocument.getCurrentVersionNumber() %>">
										<%if (tDCSDocument.getCurrentVersionFileType().equals("excel")){ %>
											<img src="/GloreeJava2/images/ExportExcel16.gif"> 
										<%}%>
										<%if (tDCSDocument.getCurrentVersionFileType().equals("pdf")){ %>
											<img src="/GloreeJava2/images/ExportPDF16.gif"> 
										<%}%>
										<%if (tDCSDocument.getCurrentVersionFileType().equals("word")){ %>
											<img src="/GloreeJava2/images/ExportWord16.gif"> 
										<%}%>
										<%=tDCSDocument.getFullTag() %>
									</a>
									<%}
									else { %>
										<%if (tDCSDocument.getCurrentVersionFileType().equals("excel")){ %>
											<img src="/GloreeJava2/images/ExportExcel16.gif"> 
										<%}%>
										<%if (tDCSDocument.getCurrentVersionFileType().equals("pdf")){ %>
											<img src="/GloreeJava2/images/ExportPDF16.gif"> 
										<%}%>
										<%if (tDCSDocument.getCurrentVersionFileType().equals("word")){ %>
											<img src="/GloreeJava2/images/ExportWord16.gif"> 
										<%}%>
										<span 
										title='<%=tDCSDocument.getCurrentVersionNotes() %>' 
										class='normalText'><%=tDCSDocument.getFullTag() %></span>
									<%} %>
								</td>
								<td class='<%=cellStyle%>'>
									<span class='normalText'><%=tDCSDocument.getCurrentVersionNumber()%> </span>
								</td>
								<td class='<%=cellStyle%>'>
									<span class='normalText'><%=tDCSDocument.getTitle()%> </span>
								</td>
								<% if (tDCSDisplay.contains("documentStatus")){ %>
									<td class='<%=cellStyle%>'>
										<span class='normalText'>
										<% 
										if (tDCSDocument.getCurrentVersionDocumentStatus().equals("unlocked")){ %>
											<img src="/GloreeJava2/images/lockUnlock16.png"> 
											Unlocked
										<%} %>
										<% if (tDCSDocument.getCurrentVersionDocumentStatus().equals("locked")){ %>
											<img src="/GloreeJava2/images/lock16.png"> 
											Locked by <%=tDCSDocument.getCurrentVersionDocumentStatusBy() %>
										<%} %>
										 </span>
									</td>
								<%} %>
								<% if (tDCSDisplay.contains("author")){ %>
									<td class='<%=cellStyle%>'>
										<span class='normalText'><%=tDCSDocument.getCurrentVersionAuthor() %> </span>
									</td>
								<%} %>									
			
			
								<% if (tDCSDisplay.contains("lastModifiedDt")){ %>
									<td class='<%=cellStyle%>'>
										<span class='normalText'><%=tDCSDocument.getCurrentVersionCreatedDt() %></span>
									</td>										
								<%} %>									

	
								<% if (tDCSDisplay.contains("approvalStatus")){ %>
									<% if (tDCSDocument.getCurrentVersionApprovalStatus().equals("draft")){ %>
											<td bgcolor='#FFFF66'' class='<%=cellStyle%>'>
												<span class='normalText'>Draft</span>
											</td>										
									<%} %>
									<% if (tDCSDocument.getCurrentVersionApprovalStatus().equals("inApprovalWorkFlow")){ %>
											<td bgcolor='#99ccff' class='<%=cellStyle%>'>
												<span class='normalText'>In Approval Workflow</span>
											</td>										
									<%} %>
									<% if (tDCSDocument.getCurrentVersionApprovalStatus().equals("approved")){ %>
											<td bgcolor='#CCFF99'' class='<%=cellStyle%>'>
												<span class='normalText'>Approved</span>
											</td>										
									<%} %>
									<% if (tDCSDocument.getCurrentVersionApprovalStatus().equals("rejected")){ %>
											<td bgcolor='#FFA3AF' class='<%=cellStyle%>'>
												<span class='normalText'>In Approval Workflow</span>
											</td>										
									<%} %>
									<%} %>									


									<% if (tDCSDisplay.contains("folderPath")){ %>
											<td class='<%=cellStyle%>'>
												<span class='normalText'><%=tDCSDocument.getFolderPath() %></span>
											</td>										
									<%} %>									
					

									<% if (tDCSDisplay.contains("notes")){ %>
											<td class='<%=cellStyle%>'>
												<span class='normalText'><%=tDCSDocument.getCurrentVersionNotes() %></span>
											</td>										
									<%} %>									
					
									<% if (tDCSDisplay.contains("systemLog")){ %>
											<td class='<%=cellStyle%>'>
												<span class='normalText'><%=tDCSDocument.getCurrentVersionSourceLog() %></span>
											</td>										
									<%} %>									
					
							</tr>	
							
							
							<tr>
								<td class='<%=cellStyle%>'></td>
								<td colspan='<%=columnCount-1%>' class='<%=cellStyle%>'>
									<div id='DocumentDetailsDiv<%=tDCSDocument.getDocumentId()%>' style='display:none'>
									</div>
								</td>
							</tr>
					
				<%		
					}
				%>
				
						</table>
					<%} %>		
				</div>
			</td>
			</tr>			
		</table>
	</div>
<%}%>