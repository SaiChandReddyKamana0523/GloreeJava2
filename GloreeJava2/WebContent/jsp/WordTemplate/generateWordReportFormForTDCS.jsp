
<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String generateWordReportFormIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((generateWordReportFormIsLoggedIn == null) || (generateWordReportFormIsLoggedIn.equals(""))){
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
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
%>
	
	
	<%
		// NOTE : this page can be called when some one tries to edit a wordTemplate.
		
		int templateId = Integer.parseInt(request.getParameter("templateId"));
		WordTemplate wordTemplate = new WordTemplate(templateId, databaseType);
		int folderId = Integer.parseInt(request.getParameter("folderId"));
		
		User user = securityProfile.getUser();
		String message = 
		 "This is a copy of the document "+ wordTemplate.getTemplateName().toUpperCase() +
		 " the TraceCloud project " +  project.getProjectName() +
		 ". This copy was generated at " + Calendar.getInstance().getTime() +
		 " by " +  user.getFirstName() + " " + user.getLastName() + " (" +
		 user.getEmailId() + ")";

		TDCSDocument  tDCSDocument = null;
		String tDCSAction = "";
		String tDCSDocumentFullTag = "";
	%>
	
	<div id='generateWordReportDiv' class='level1Box'>
		<form method="post" id="pushWordReportToTDCS" action="/GloreeJava2/servlet/WordTemplateAction">
		<input type='hidden' name='action' value='pushReportToTDCS'>
		<input type='hidden' name='folderId' value='<%=folderId%>'>
		<input type='hidden' name='templateId' value='<%=templateId%>'>

		<% if (wordTemplate.getTDCSDocumentId() == 0){ 
			tDCSAction = "new";%>
			<input type='hidden' name='tDCSAction' value='new'>
		<%
		}
		else {
			tDCSAction = "existing";
			tDCSDocument = new TDCSDocument(wordTemplate.getTDCSDocumentId(), databaseType);
			tDCSDocumentFullTag = tDCSDocument.getFullTag();
			%>
			<input type='hidden' name='tDCSAction' value='existing'>
			<input type='hidden' name='tDCSDocumentFullTag' value='<%=tDCSDocument.getFullTag()%>'>
		<%} %>
		
		
		<table class='paddedTable' width='100%'>
			<tr>
				<td colspan="2"> 
					<span class='normalText'> 
					Generate Word Report and Push to TDCS (TraceCloud Document Control System)
					</span>
				</td> 
			</tr>	
			

			<tr>
				<td colspan="2"> 
					<div id='pushWordTemplateReportToTDCSResponse' style='display:none'>
					</div>
				</td> 
			</tr>	
			
			
					<% if (wordTemplate.getTDCSDocumentId() == 0){
						// we are trying to add a new doc to TDCS %>
						<tr>
							<td width='200'>
							<span class='normalText'>TDCS Document Title </span>
							</td>
							<td>
							<span class='normalText'>
							<input type='text' name='title' id='title' size='80' maxlength='1000' value='<%=wordTemplate.getTemplateName()%> from project <%=project.getProjectName() %>'></input>
							</span>
							</td>
						</tr>
					<%}
					else {
						// we are trying to add a new version to an existing doc.
						
						if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" + tDCSDocument.getFolderId() ))){
					%>
							<tr>
								<td colspan='2'>
									<div class='alert alert-success'>
									<span class='normalText'>
										You do not have Create permissions in the Folder where the Document resides. Please work with your administrator to
										get Create Permissions .
									</span>
									</div>
								</td>
							</tr>
										
					<%
							return;
						}
						else if ((securityProfile.getPrivileges().contains("createRequirementsInFolder" + tDCSDocument.getFolderId() ))){
					%>
							<tr>
								<td width='200'><span class='normalText'>TDCS Document Id</span></td>
								<td><span class='normalText'><%=tDCSDocument.getFullTag() %></span></td>
							</tr>
							<tr>
								<td width='200'><span class='normalText'>TDCS Document Title</span></td>
								<td><span class='normalText'><%=tDCSDocument.getTitle() %></span></td>
							</tr>
							<tr>
								<td width='200'><span class='normalText'>TDCS Document Status</span></td>
								<%if (tDCSDocument.getCurrentVersionDocumentStatus().equals("unlocked")){ %>
									<td><span class='normalText'>
										<img src="/GloreeJava2/images/lockUnlock16.png"> 
										Unlocked
									</span></td>
								<%} %>
								<% if (tDCSDocument.getCurrentVersionDocumentStatus().equals("locked")){ %>
									<td><span class='normalText'>
										<img src="/GloreeJava2/images/lock16.png"> 
										Locked by <%=tDCSDocument.getCurrentVersionDocumentStatusBy() %>
									</span></td>
								<%} %>
							</tr>
							<tr>
								<td width='200'><span class='normalText'>TDCS Approval Status</span></td>
								<% if (tDCSDocument.getCurrentVersionApprovalStatus().equals("draft")){ %>
										<td >
											<span style="background-color:#FFFF66" class='normalText'>Draft</span>
										</td>										
								<%} %>
								<% if (tDCSDocument.getCurrentVersionApprovalStatus().equals("inApprovalWorkFlow")){ %>
										<td >
											<span style="background-color:#99ccff" class='normalText'>In Approval Workflow</span>
										</td>										
								<%} %>
								<% if (tDCSDocument.getCurrentVersionApprovalStatus().equals("approved")){ %>
										<td >
											<span style="background-color:#CCFF99" class='normalText'>Approved</span>
										</td>										
								<%} %>
								<% if (tDCSDocument.getCurrentVersionApprovalStatus().equals("rejected")){ %>
										<td >
											<span style="background-color:#FFa3AF" class='normalText'>In Approval Workflow</span>
										</td>										
								<%} %>
							</tr>
					<%
						}
					%>
						<% if (tDCSDocument.getCurrentVersionDocumentStatus().equals("locked")){
							// this is a locked document. lets get the user to get the document unlocked.
							if (tDCSDocument.getCurrentVersionDocumentStatusBy().equals(securityProfile.getUser().getEmailId() )){ 
								// this means that the lock was put in place by this user.		
						%>
								<tr>
									<td colspan='2'>
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
									<td colspan='2'>
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
				
					<%} %>


				<tr>
					<td width='100'>
					<span class='normalText'>&nbsp;&nbsp;Description</span>
					</td>
					
					<td>
					<span class='normalText'>
					<textarea name='description' id='description' rows='5' cols='80'><%=message %></textarea>
					</span>
					</td>
				</tr>						
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			<tr> 
				<td width='200'>
					<span class='headingText'> 
						Requirement Output Format
					</span>
					<sup>
					</sup> 
				</td>
				<td> 
					<span class='normalText'>
						<select   name='requirementOutputFormat' id='requirementOutputFormat' 
						onchange ='
							if (document.getElementById("requirementOutputFormat").selectedIndex == 1){
								document.getElementById("reqPerTableRowMessage").style.display = "block";
							}
							else {
								document.getElementById("reqPerTableRowMessage").style.display = "none";
							}
						'>
						<option value='reqPerTable' SELECTED > One Requirements per Table</option>
						<option value='reqPerTableRow' > One Requirement per Row</option>
					</select>
					<div id='reqPerTableRowMessage' style='display:none'>
						
							Please note that if you print each Requirement in a single row, then this row can get quiet wide and is best viewed in a <b>'Web' or 'Outline' 
							or 'Draft' layout</b>. If your Word document is in 'Normal' layout, wide tables will look messy.
							<br><br>
							<b>Please Note that MS Word has a limitation, where your page width can not exceed 22 inches and you can not have more than 63 columns.</b>
							If you try to cross either of these limits, MS Word does not display your tables gracefully. 
						
					</div>
					</span>
				</td>
			</tr>
			
			
			<tr> 
				<td>
					<span class='headingText'> Display Attributes 
					<br> (Ctrl+Click to Select / Un Select)</span> 
				</td>
				<td >
					<span class='normalText'>
					<select  SIZE='19' name='displayAttributes' id='displayAttributes' multiple>
						<option value='approvers' > Approvers</option>
						<option value='approvalDate' > Approval Date</option>
						<option value='approvalStatus' > Approval Status</option>
						<option value='requirementBaselines' > Baselines</option>
						<option value='customAttributes' selected> Custom Attributes</option>
						<option value='description' selected>  Description</option>
						<option value='externalURl' > External URL</option>
						<option value='fileAttachments' > File Attachments</option>
						<option value='folderPath' > Folder Path</option>
						<option value='name' selected> Name</option>
						<option value='owner' > Owner</option>
						<option value='pctComplete' > Percent Complete</option>
						<option value='priority' > Priority</option>
						<option value='type' > Requirement Type</option>
						<option value='testingStatus' > Testing Status</option>
						<option value='traceTo' > Trace To</option>
						<option value='traceFrom' > Trace From</option>
						<option value='version' > Version</option>
						<option value='url' > URL</option>
					</select>
					</span>
				</td>		
			</tr>			
			
			<tr>
				<td></td>
				<td >
					<span class='normalText'>
					<input type="button" id="pushReportToTDCSButton"  value="Push Report to TDCS" class='btn btn-sm btn-success'
					onClick='
						document.getElementById("pushReportToTDCSButton").disabled=true;
						pushWordTemplateReportToTDCS(<%=folderId %>,<%=templateId %>,"<%=tDCSAction %>","<%=tDCSDocumentFullTag %>","doc");
						'>
					<input type="button" name="Cancel" value="Cancel" class='btn btn-sm btn-danger'
					onClick='document.getElementById("templateCoreDiv").innerHTML= ""'>
					</span>
				</td>
			</tr> 	
		 
		
		</table>
		
		</form>
	</div>
<%}%>