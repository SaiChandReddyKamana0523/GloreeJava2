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
		// lets get the filterable params.
		int tDCSDocumentId = Integer.parseInt(request.getParameter("tDCSDocumentId"));
		TDCSDocument tDCSDocument = new TDCSDocument(tDCSDocumentId, databaseType);
		if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
				+ tDCSDocument.getFolderId()))){
		%>
			<table   width="100%" align="center" class='paddedTable'>
		<tr>
			<td >
				<div class='alert alert-success'>
				<span class='normalText'>
					You do not have Read permission on the folder where the document resides.
				</span>	
				</div>
			</td>
		</tr>		
		</table>
		<%
		return;
		}
		ArrayList tDCSDocumentVersions = tDCSDocument.getPreviousVersions(databaseType);
		String tDCSDisplay = request.getParameter("tDCSDisplay");
	%>	
	
	
	
	
	
	
		<div class='alert alert-success'>
				<div style='float:right'>
					<a href='#' onClick=
					'document.getElementById("DocumentDetailsDiv"+<%=tDCSDocument.getDocumentId()%>).style.display="none"'
					>Close </a>
				</div>
			
				<table   width="100%" align="center" class='paddedTable'>
					<tr>
						<td>
							<span class='sectionHeadingText'>Document Id</span>
						</td>
						<td>
							<span class='sectionHeadingText'>Version</span>
						</td>
						<td>
							<span class='sectionHeadingText'>Title</span>
						</td>

						<% if (tDCSDisplay.contains("author")){%>
						<td>
							<span class='sectionHeadingText'>Author</span>
						</td>
						<%} %>
						<% if (tDCSDisplay.contains("lastModifiedDt")){%>
						<td>
							<span class='sectionHeadingText'>Date</span>
						</td>
						<%} %>
						<% if (tDCSDisplay.contains("approvalStatus")){%>
						<td>
							<span class='sectionHeadingText'>Approval Status</span>
						</td>
						<%} %>
						<% if (tDCSDisplay.contains("folderPath")){%>			
						<td>
							<span class='sectionHeadingText'>Folder Path</span>
						</td>
						<%} %>
						<% if (tDCSDisplay.contains("notes")){%>
						<td>
							<span class='sectionHeadingText'>Notes</span>
						</td>
						<%} %>
						<% if (tDCSDisplay.contains("systemLog")){%>
						<td>
							<span class='sectionHeadingText'>SystemLog</span>
						</td>
						<%} %>
								
					</tr>	
		
				
		<%
			Iterator i = tDCSDocumentVersions.iterator();
			while (i.hasNext()){
				TDCSDocumentVersion tDCSDocumentVersion = (TDCSDocumentVersion) i.next();
		%>
		
					<tr>
						<td>
							<%	if ((securityProfile.getPrivileges().contains("readRequirementsInFolder" 
									+ tDCSDocumentVersion.getFolderId()))){
							 %>
							<a 
							title='<%=tDCSDocumentVersion.getVersionNotes() %>'
							target="_blank" 
							href="/GloreeJava2/servlet/TDCSAction?action=downloadDocument&tDCSDocumentId=<%=tDCSDocumentVersion.getDocumentId()%>&versionNumber=<%=tDCSDocumentVersion.getVersionNumber() %>">
								<%if (tDCSDocumentVersion.getVersionFileType().equals("excel")){ %>
									<img src="/GloreeJava2/images/ExportExcel16.gif"> 
								<%}%>
								<%if (tDCSDocumentVersion.getVersionFileType().equals("pdf")){ %>
									<img src="/GloreeJava2/images/ExportPDF16.gif"> 
								<%}%>
								<%if (tDCSDocumentVersion.getVersionFileType().equals("word")){ %>
									<img src="/GloreeJava2/images/ExportWord16.gif"> 
								<%}%>
								<%=tDCSDocumentVersion.getFullTag() %>
							</a>
							<%}
							else {%>
								<%if (tDCSDocumentVersion.getVersionFileType().equals("excel")){ %>
									<img src="/GloreeJava2/images/ExportExcel16.gif"> 
								<%}%>
								<%if (tDCSDocumentVersion.getVersionFileType().equals("pdf")){ %>
									<img src="/GloreeJava2/images/ExportPDF16.gif"> 
								<%}%>s
								<%if (tDCSDocumentVersion.getVersionFileType().equals("word")){ %>
									<img src="/GloreeJava2/images/ExportWord16.gif"> 
								<%}%>
								<span 
								title='<%=tDCSDocumentVersion.getVersionNotes() %>' 
								class='normalText'><%=tDCSDocumentVersion.getFullTag() %></span>
							<%} %>
						</td>
						<td>
							<span class='normalText'><%=tDCSDocumentVersion.getVersionNumber()%> </span>
						</td>
						<td>
							<span class='normalText'><%=tDCSDocumentVersion.getTitle()%> </span>
						</td>

				<% if (tDCSDisplay.contains("author")){ %>				
					<td>
						<span class='normalText'><%=tDCSDocumentVersion.getVersionAuthor() %> </span>
					</td>
				<%} %>					
					
					
				<% if (tDCSDisplay.contains("lastModifiedDt")){ %>
					<td >
						<span class='normalText'><%=tDCSDocumentVersion.getVersionCreatedDt() %></span>
					</td>										
				<%} %>		
					
				<% if (tDCSDisplay.contains("approvalStatus")){ %>
					<% if (tDCSDocumentVersion.getVersionApprovalStatus().equals("draft")){ %>
							<td bgcolor='#FFFF66''>
								<span class='normalText'>Draft</span>
							</td>										
					<%} %>
					<% if (tDCSDocumentVersion.getVersionApprovalStatus().equals("inApprovalWorkFlow")){ %>
							<td bgcolor='#99ccff'>
								<span class='normalText'>In Approval Workflow</span>
							</td>										
					<%} %>
					<% if (tDCSDocumentVersion.getVersionApprovalStatus().equals("approved")){ %>
							<td bgcolor='#CCFF99''>
								<span class='normalText'>Approved</span>
							</td>										
					<%} %>
					<% if (tDCSDocumentVersion.getVersionApprovalStatus() .equals("rejected")){ %>
							<td bgcolor='#FFA3AF'>
								<span class='normalText'>In Approval Workflow</span>
							</td>										
					<%} %>
				<%} %>
				<% if (tDCSDisplay.contains("folderPath")){ %>
						<td >
							<span class='normalText'><%=tDCSDocumentVersion.getFolderPath() %></span>
						</td>										
				<%} %>									


				<% if (tDCSDisplay.contains("notes")){ %>
						<td >
							<span class='normalText'><%=tDCSDocumentVersion.getVersionNotes() %></span>
						</td>										
				<%} %>									

				<% if (tDCSDisplay.contains("systemLog")){ %>
						<td >
							<span class='normalText'><%=tDCSDocumentVersion.getVersionSourceLog() %></span>
						</td>										
				<%} %>									
				
						
					</tr>
					
		 
		<%		
			}
		%>
				</table>
		</div>			
		
<%}%>