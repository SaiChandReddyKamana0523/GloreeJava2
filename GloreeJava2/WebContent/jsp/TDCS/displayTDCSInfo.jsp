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
		String tDCSDocumentFullTag = request.getParameter("tDCSDocumentFullTag");
		TDCSDocument tDCSDocument = new TDCSDocument(tDCSDocumentFullTag, dPIProject.getProjectId(), databaseType);
		if (tDCSDocument.getProjectId() != dPIProject.getProjectId()){
		%>	
		<table   width="100%" align="center" class='paddedTable'>
		<tr>
			<td>
				<div class='alert alert-success'>
				<span class='normalText'>
					This document does not exist in this project. Please check your document Id.
				</span>
				</div>
			</td>
		</tr>
		</table>
		<%}
		else if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" + tDCSDocument.getFolderId() ))){
		%>
		<table   width="100%" align="center" class='paddedTable'>
		<tr>
			<td>
				<div class='alert alert-success'>
				<span class='normalText'>
					You do not have Create permissions in the Folder where the Document resides. Please work with your administrator to
					get Create Permissions or choose another document Id.
				</span>
				</div>
			</td>
		</tr>
		</table>	
		<%}
		else if ((securityProfile.getPrivileges().contains("createRequirementsInFolder" + tDCSDocument.getFolderId() ))){
		%>
		<table   width="100%" align="center" >
			<tr>
				<td width='100'><span class='normalText'>Title</span></td>
				<td><span class='normalText'><%=tDCSDocument.getTitle() %></span></td>
			</tr>
			<tr>
				<td><span class='normalText'>Description</span></td>
				<td><span class='normalText'><%=tDCSDocument.getCurrentVersionNotes() %></span></td>
			</tr>
			<tr>
				<td><span class='normalText'>File Type</span></td>
				<td><span class='normalText'><%=tDCSDocument.getCurrentVersionFileType() %></span></td>
			</tr>
			<tr>
				<td><span class='normalText'>Document Status</span></td>
				<td><span class='normalText'><%=tDCSDocument.getCurrentVersionDocumentStatus() %></span></td>
			</tr>
			<tr>
				<td><span class='normalText'>Approval Status</span></td>
				<td><span class='normalText'><%=tDCSDocument.getCurrentVersionApprovalStatus() %></span></td>
			</tr>
			<% if (tDCSDocument.getCurrentVersionDocumentStatus().equals("locked")){
				// this is a locked document. lets get the user to get the document unlocked.
				if (tDCSDocument.getCurrentVersionDocumentStatusBy().equals(securityProfile.getUser().getEmailId() )){ 
					// this means that the lock was put in place by this user.		
			%>
					<tr>
						<td>&nbsp;</td>
						<td>
							<div class='alert alert-success'>
								<span class='normalText'>This document has been Locked by you on <%=tDCSDocument.getCurrentVersionLastModifiedDt() %>.
								 Please Unlock it at 
								<a href='#' onClick='displayTDCSHome()'><img src='/GloreeJava2/images/database_refresh16.png'> TDCS</a> 
								prior to adding a new version to it. </span>
							</div>
						</td>
					</tr>
				<%} 
				else {%>
					<tr>
						<td>&nbsp;</td>
						<td>
							<div class='alert alert-success'>
								<span class='normalText'>This document has been Locked by <%=tDCSDocument.getCurrentVersionDocumentStatusBy()%>
								on <%=tDCSDocument.getCurrentVersionLastModifiedDt()%>. Please work with this person or the project administrators
								to Unlock this document at 
								<a href='#' onClick='displayTDCSHome()'><img src='/GloreeJava2/images/database_refresh16.png'> TDCS</a> 
								prior to adding a new version to it. </span>
							</div>
						</td>
					</tr>
			<%	}
			}%>
		</table>	
		
		<%} %>
<%}%>