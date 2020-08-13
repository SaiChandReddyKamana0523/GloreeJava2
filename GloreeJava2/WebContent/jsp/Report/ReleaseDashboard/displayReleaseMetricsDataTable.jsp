<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>


<%
	// authentication only
	String IsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((IsLoggedIn == null) || (IsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean IsMember = false;
	Project project= (Project) session.getAttribute("project");
	SecurityProfile SecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (SecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		IsMember = true;
	}
	
	User user = SecurityProfile.getUser();

%>

<%if (IsMember){ 

%>


	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	
	<%
	String focusOn = request.getParameter("focusOn");
	if ((focusOn == null) || (focusOn.equals(""))){
		focusOn = "All:#:Approval:#:Traceability:#:Testing:#:Completion";
	}
	String levelOfDetail = request.getParameter("levelOfDetail");
	if ((levelOfDetail == null) || (levelOfDetail.equals("") )){
		levelOfDetail = "requirementType";
	}

	
	
	int releaseId = Integer.parseInt(request.getParameter("releaseId"));
	Requirement release = new Requirement(releaseId, databaseType);
	
	String lastDataLoadDt =  ReleaseMetricsUtil.getLastDataLoadDtForReleaseOrProject(releaseId,project.getProjectId(), databaseType);

	// lets get the list of distinct req types in this release tree.	
	ArrayList dataTable = new ArrayList();
	
	if (levelOfDetail.equals("requirementType")){
		dataTable = ReleaseMetricsUtil.getReleaseDataTableArrayForReleaseOrProject(
				releaseId,project.getProjectId(), user, databaseType);
	}
	if (levelOfDetail.equals("folders")){
		dataTable = ReleaseMetricsUtil.getReleaseDataTableArrayByFolder(
				releaseId,project.getProjectId(), user, databaseType);
	}

	// lets set the arraylist of results in session, so that we can re-use them
	// when we export to Excel.
	session.setAttribute("releaseDashboardDataTable", dataTable);
	session.setAttribute("levelOfDetail", levelOfDetail);
	session.setAttribute("focusOn", focusOn);
		
	%>
	 
	
	<div id = 'displayListReportDiv' class='level1Box'>
	<table class='paddedTable' width='100%'>
		<tr>
			<td bgcolor="#99ccff" align="left">				
				<span class="subSectionHeadingText">
					Release Metrics
					<% if(lastDataLoadDt != null) { %>
						 as of '<%=lastDataLoadDt%>' for '<%=release.getRequirementFullTag()%>:<%=release.getRequirementNameForHTML()%>'
					<%} %>
				</span>
			</td>		
		</tr>

		<tr>
			<td>
				<div id ='projectMetricsFilters' class='level2Box'>
					<table>
						<tr>
							<td>
								<span class='normalText'>Level of Detail</span>
							</td>
							<td>
								<span class='normalText'>
								<select name='levelOfDetail<%=releaseId%>' id='levelOfDetail<%=releaseId%>'>
									<%if (levelOfDetail.equals("requirementType")){ %>
										<option SELECTED value='requirementType'>Requirement Type</option>
										<option value='folders'>Folders</option>
									<%}
									else {%>
										<option value='requirementType'>Requirement Type</option>
										<option SELECTED value='folders'>Folders</option>
									<%} %>
									
								</select>
								</span>
							</td>
							
							
							<td>
								<span class='normalText'>Focus On</span>
							</td>
							<td>
								<span class='normalText'>
								<select name='focusOn<%=releaseId%>' id='focusOn<%=releaseId%>' multiple="multiple" size="4">
									<%if (focusOn.contains("Approval")){ %>
										<option SELECTED value='Approval'>Approval</option>
									<%}
									else {%>
										<option  value='Approval'>Approval</option>
									<%} %>
									
									
									<%if (focusOn.contains("Traceability")){ %>
										<option SELECTED value='Traceability'>Traceability</option>
									<%}
									else {%>
										<option  value='Traceability'>Traceability</option>
									<%} %>
									
									
									<%if (focusOn.contains("Testing")){ %>
										<option SELECTED value='Testing'>Testing</option>
									<%}
									else {%>
										<option  value='Testing'>Testing</option>
									<%} %>
									
									
									<%if (focusOn.contains("Completion")){ %>
										<option SELECTED value='Completion'>Completion</option>
									<%}
									else {%>
										<option  value='Completion'>Completion</option>
									<%} %>
									
									
									
								</select>
								</span>
							</td>

							
							<td>
								<span class='normalText'>
									<input type='button' name='Refresh' id='refreshProjectDsahboardButton' value='Refresh'
									onClick='refreshReleaseMetricsDataTable(<%=releaseId%>);'></input> 
								</span>
							</td>
							
							
						</tr>
					</table>
				</div>
			</td>
		</tr>
		
		
		<tr>
			<td>
			<table align='left'>
					<tr>
						<td class='icons'>
						    <a href='/GloreeJava2/servlet/ReportAction?action=exportReleaseDashboardDataTableToExcel'
						     target='_blank'>
						    <img src="/GloreeJava2/images/ExportExcel16.gif"  border="0"></a>
			    		</td>
		        			    	
			    	</tr>
			    	
				</table>
			
			</td>
		</tr>


		<tr>
			<td>
				<div id ='reportData' class='level2Box'>
				<table id = "Report">				
	
					<%
					    if (dataTable != null){
					    	if (dataTable.size() ==0){
				   	%>
						    		<tr>
						    			<td colspan='7'>
						    				<div class='alert alert-success'>
						    					<span class='normalText'> No Requirements were found trace to this Release.
						    					<br><br>Please Note that the Release Metrics get calculated once a night and 
						    					the Metrics calculation script may not have had a chance to run since
						    					you have added Requirements to this Release.
						    					</span>
						    				</div>
						    			</td>
						    		</tr>
						    	
				   	<%
						    	}
					    	Iterator i = dataTable.iterator();
					    	int j = 0;
					    	String cellStyle = "normalTableCell";
					    	while ( i.hasNext() ) {
					    		String dataTableString = (String) i.next();
					    		String[] dataRow = dataTableString.split(":##:");
					    		
								String dataLoadDt = "";
								String requirementTypeShortName = "";
								String folderPath = "";
								String numOfRequirements = "";
								String numOfDraftRequirements = "";
								String numOfInApprovalWorkflowRequirements = "";
								String numOfRejectedRequirements = "";
								String numOfApprovedRequirements = "";
								String numOfDanglingRequirements ="";
								String numOfOrphanRequirements = "";
								String numOfSuspectUpstreamRequirements = "";
								String numOfSuspectDownstreamRequirements ="";
								String numOfCompletedRequirements = "";
								String numOfIncompleteRequirements = "";
								String numOfTestPendingRequirements = "";
								String numOfTestPassRequirements = "";
								String numOfTestFailRequirements = "";
								int folderId = 0;
								
					    		if (dataRow.length > 0) {
									dataLoadDt = dataRow[0];
					    		}
					    		if (dataRow.length > 1) {
									if (levelOfDetail.equals("requirementType")){
										requirementTypeShortName = dataRow[1];
									}
									if (levelOfDetail.equals("folders")){
										requirementTypeShortName = "all";
										folderPath = dataRow[1];
									}
					    		}
					    		if (dataRow.length > 2) {
									numOfRequirements = dataRow[2];
					    		}
					    		if (dataRow.length > 3) {
									numOfDraftRequirements = dataRow[3];
					    		}
					    		if (dataRow.length > 4) {
									numOfInApprovalWorkflowRequirements = dataRow[4];
					    		}
					    		if (dataRow.length > 5) {
									numOfRejectedRequirements = dataRow[5];
					    		}
					    		if (dataRow.length > 6) {
									numOfApprovedRequirements = dataRow[6];
					    		}
					    		if (dataRow.length > 7) {
									numOfDanglingRequirements = dataRow[7];
					    		}
					    		if (dataRow.length > 8) {
									numOfOrphanRequirements = dataRow[8];
					    		}
					    		if (dataRow.length > 9) {
									numOfSuspectUpstreamRequirements = dataRow[9];
					    		}
					    		if (dataRow.length > 10) {
									numOfSuspectDownstreamRequirements = dataRow[10];
					    		}
					    		if (dataRow.length > 11) {
					    			numOfCompletedRequirements = dataRow[11];
					    		}
					    		if (dataRow.length > 12) {
									numOfIncompleteRequirements = dataRow[12];
					    		}
					    		
					    		if (dataRow.length > 13) {
									numOfTestPendingRequirements = dataRow[13];
					    		}
					    		if (dataRow.length > 14) {
									numOfTestPassRequirements = dataRow[14];
					    		}
					    		if (dataRow.length > 15) {
									numOfTestFailRequirements = dataRow[15];
					    		}
					    		if (dataRow.length > 16) {
					    			try {
									folderId = Integer.parseInt(dataRow[16]);
					    			}
					    			catch (Exception e){
					    				folderId = 0;
					    			}
					    		}
					
					    		// lets get the requirementtype object, so we can determine if approval work flow is enabled or not
					    		boolean displayApprovalMetrics = false;
					    		if (levelOfDetail.equals("requirementType")){
					    			// each row in the metrics table is at a req type level, so lets see if the approval work flow is enabled for this or not.
					    			RequirementType requirementType = new RequirementType(project.getProjectId(),requirementTypeShortName,user.getEmailId());
						    		if (requirementType.getRequirementTypeEnableApproval() == 1) {
						    			displayApprovalMetrics = true;
						    		}
					    		}
					    		else {
					    			// this means the level of detail is 'folders'. we need to see if this folder's req type is approval work flow enabled or not.
					    			Folder folder = new Folder(folderPath, project.getProjectId());
					    			int requirementTypeId = folder.getRequirementTypeId();
					    			RequirementType requirementType = new RequirementType(requirementTypeId);
					    			if (requirementType.getRequirementTypeEnableApproval() == 1) {
						    			displayApprovalMetrics = true;
						    		}
					    		}
					    		
					    		j++;
					    		// for the first row, print the header and user defined columns etc..
					    		if (j == 1){
					 %>
					 
														 
									<tr>
										<td class='tableHeader' colspan='2'>&nbsp;</td>
										<% if(focusOn.contains("Approval")){ %>
											<td class='tableHeader' colspan='4' align='center' BGCOLOR="#E5EBFF">
												<span class='sectionHeadingText'>
												Approval
												</span>
											</td>
										<%} %>
										
										<% if(focusOn.contains("Traceability")){ %>
											<td class='tableHeader' colspan='4' align='center' BGCOLOR="#E5FFEC">
												<span class='sectionHeadingText'>
												Traceability
												</span>
											</td>
										<%} %>
										
										<% if(focusOn.contains("Testing")){ %>
											<td class='tableHeader' colspan='3' align='center' BGCOLOR="#99FFFF">
												<span class='sectionHeadingText'>
												Testing
												</span>
											</td>
										<%} %>
										
										<% if(focusOn.contains("Completion")){ %>	
											<td class='tableHeader' colspan='2' align='center' BGCOLOR="#CCCCFF">
												<span class='sectionHeadingText'>
												Completion
												</span>
											</td>
										<%} %>
									</tr>					 
									
									<tr>
										
										<%if (levelOfDetail.equals("requirementType")){%>
										<td class='tableHeader' width='350'>
											<span class='sectionHeadingText'>
											Requirement Type
											</span>
										</td>
										<%} %>
										<%if (levelOfDetail.equals("folders")){%>
										<td class='tableHeader' width='600'>
											<span class='sectionHeadingText'>		
												Folder 
											</span>
										</td>
										<%} %>
									
										<td class='tableHeader'>
											<span class='sectionHeadingText'>
											All
											</span>
										 </td>
									
										<% if(focusOn.contains("Approval")){ %>
											<td class='tableHeader' BGCOLOR="#E5EBFF"> 
												<span class='sectionHeadingText'>
												Draft
												</span>
											</td>	
											<td class='tableHeader' BGCOLOR="#E5EBFF"> 
												<span class='sectionHeadingText'>
												Pending
												</span>
											</td>		
											<td class='tableHeader' BGCOLOR="#E5EBFF"> 
												<span class='sectionHeadingText'>
												Rejected
												</span>
											</td>		
											<td class='tableHeader' BGCOLOR="#E5EBFF"> 
												<span class='sectionHeadingText'>
												Approved
												</span>
											</td>
										<%} %>
									
										<% if(focusOn.contains("Traceability")){ %>
											<td class='tableHeader' BGCOLOR="#E5FFEC"> 
												<span class='sectionHeadingText' >
												Dangling
												</span>
											</td>
											<td class='tableHeader' BGCOLOR="#E5FFEC"> 
												<span class='sectionHeadingText' >
												Orphan
												</span>
											</td>
													
											<td class='tableHeader' BGCOLOR="#E5FFEC"> 
												<span class='sectionHeadingText' >
												Suspect Upstream
												</span>
											</td>				
											<td class='tableHeader' BGCOLOR="#E5FFEC"> 
												<span class='sectionHeadingText' >
												Suspect Downstream
												</span>
											</td>
										<%} %>
										
										<% if(focusOn.contains("Testing")){ %>
											<td class='tableHeader' BGCOLOR="#99FFFF"> 
												<span class='sectionHeadingText'>
												Pending
												</span>
											</td>
											<td class='tableHeader' BGCOLOR="#99FFFF"> 
												<span class='sectionHeadingText'>
												Pass
												</span>
											</td>
											<td class='tableHeader' BGCOLOR="#99FFFF"> 
												<span class='sectionHeadingText'>
												Fail
												</span>
											</td>
										<%} %>
									
										<% if(focusOn.contains("Completion")){ %>
											<td class='tableHeader' BGCOLOR="#CCCCFF"> 
												<span class='sectionHeadingText' >
												Completed 
												</span>
											</td>
											<td class='tableHeader' BGCOLOR="#CCCCFF"> 
												<span class='sectionHeadingText' >
												Incomplete
												</span>
											</td>
										<%} %>																																																
									</tr>				 
					<%
					   		 			
					    		}
					    		
					    		// Now for each row in the array list, print the data out.
					    		if ((j%2) == 0){
					    			cellStyle = "normalTableCell";
					    		}
					    		else {
					    			cellStyle = "altTableCell";	
					    		}
					    		
					 %>
				 				<tr>
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 				<%if (levelOfDetail.equals("requirementType")){%>
												<%=requirementTypeShortName %>
											<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												<%=folderPath %>
											<%} %>
											
		 								</span>
							 		</td>
							 		
							 		
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			 <%if (levelOfDetail.equals("requirementType")){%>
								 			<a href='#' onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "all")' >
								 			<%=numOfRequirements %>
								 			</a>
										<%} %>
										<%if (levelOfDetail.equals("folders")){%>
									 			<a href='#' onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>,"<%=requirementTypeShortName%>", "all")' >
									 			<%=numOfRequirements %>
									 			</a>
										<%} %>
							 			</span>
							 		</td>



							 		<% if(focusOn.contains("Approval")){ %>
								 		<td class='<%=cellStyle%>' >
								 			<%if (displayApprovalMetrics){ %>
										 		<span class='normalText'>
									 			 <%if (levelOfDetail.equals("requirementType")){%>
										 			<a href='#' onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "draft")' >
										 			<%=numOfDraftRequirements %>
										 			</a>
												<%} %>
												<%if (levelOfDetail.equals("folders")){%>
													 <a href='#' onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>,"<%=requirementTypeShortName%>", "draft")' >
										 			<%=numOfDraftRequirements %>
										 			</a>
												<%} %>
									 			</span>
									 		<%} %>
								 		</td>
								 		<td class='<%=cellStyle%>' >
								 			<%if (displayApprovalMetrics){ %>
										 		<span class='normalText'>
									 			 <%if (levelOfDetail.equals("requirementType")){%>
													<a href='#' onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "incomplete")' >
										 			<%=numOfInApprovalWorkflowRequirements%>
										 			</a>
	
												<%} %>
												<%if (levelOfDetail.equals("folders")){%>
													 <a href='#' onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>,"<%=requirementTypeShortName%>", "incomplete")' >
										 			<%=numOfInApprovalWorkflowRequirements%>
										 			</a>
												<%} %>
									 			</span>
											<%} %>							
								 		</td>	
										<td class='<%=cellStyle%>' >
								 			<%if (displayApprovalMetrics){ %>
										 		<span class='normalText'>
									 			 <%if (levelOfDetail.equals("requirementType")){%>
													<a href='#' onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "rejected")' >
										 			<%=numOfRejectedRequirements%>
										 			</a>
												<%} %>
												<%if (levelOfDetail.equals("folders")){%>
													 <a href='#' onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>, "<%=requirementTypeShortName%>", "rejected")' >
										 			<%=numOfRejectedRequirements%>
										 			</a>
												<%} %>
									 			</span>
									 		<%} %>
								 		</td>
								 		<td class='<%=cellStyle%>' >
								 			<%if (displayApprovalMetrics){ %>
										 		<span class='normalText'>
									 			 <%if (levelOfDetail.equals("requirementType")){%>
													<a href='#' onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "approved")' >
										 			<%=numOfApprovedRequirements%>
										 			</a>
												<%} %>
												<%if (levelOfDetail.equals("folders")){%>
													 <a href='#' onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>,"<%=requirementTypeShortName%>", "approved")' >
										 			<%=numOfApprovedRequirements%>
										 			</a>
												<%} %>
									 			</span>
									 		<%} %>
								 		</td>
							 		<%} %>




									<% if(focusOn.contains("Traceability")){ %>
								 		<td class='<%=cellStyle%>' >
								 			<span class='normalText'>
								 			 <%if (levelOfDetail.equals("requirementType")){%>
												<a href='#' onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "dangling")' >
									 			<%=numOfDanglingRequirements%>
									 			</a>
								 			<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												 <a href='#' onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>,"<%=requirementTypeShortName%>", "dangling")' >
									 			<%=numOfDanglingRequirements%>
									 			</a>
											<%} %>
								 			</span>
								 		</td>
								 		<td class='<%=cellStyle%>' >
								 			<span class='normalText'>
								 			 <%if (levelOfDetail.equals("requirementType")){%>
									 			 <a href='#' onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "orphan")' >
									 			<%=numOfOrphanRequirements%>
									 			</a>
											<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												 <a href='#' onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>,"<%=requirementTypeShortName%>", "orphan")' >
									 			<%=numOfOrphanRequirements%>
									 			</a>
											<%} %>
								 			</span>
								 		</td>
								 		<td  class='<%=cellStyle%>'  >
								 			<span class='normalText'>
								 			 <%if (levelOfDetail.equals("requirementType")){%>
												<a href='#' onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "suspectUpstream")' >
									 			<%=numOfSuspectUpstreamRequirements %>
									 			</a>
									 		<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												<a href='#' onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>, "<%=requirementTypeShortName%>", "suspectUpstream")' >
									 			<%=numOfSuspectUpstreamRequirements %>
									 			</a>
											<%} %>
								 			</span>
								 			
								 		</td>
								 		<td class='<%=cellStyle%>'  >
								 			<span class='normalText'>
								 			 <%if (levelOfDetail.equals("requirementType")){%>
												<a href='#' onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "suspectDownstream")' >
									 			<%=numOfSuspectDownstreamRequirements %>
									 			</a>
								 			 <%} %>
											<%if (levelOfDetail.equals("folders")){%>
												<a href='#' onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>, "<%=requirementTypeShortName%>", "suspectDownstream")' >
									 			<%=numOfSuspectDownstreamRequirements %>
									 			</a>
											<%} %>
								 			</span>
								 		</td>
								 	<%} %>	
							 		
							
									
									<% if(focusOn.contains("Testing")){ %>
								 		<td class='<%=cellStyle%>' >
								 			<span class='normalText'>
								 			 <%if (levelOfDetail.equals("requirementType")){%>
												<a href='#' onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "pendingTesting")' >
									 			<%=numOfTestPendingRequirements %>
									 			</a>
											<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												 <a href='#' onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>, "<%=requirementTypeShortName%>", "pendingTesting")' >
									 			<%=numOfTestPendingRequirements %>
									 			</a>
											<%} %>
								 			</span>							 		
								 		
								 		</td>					
								 		<td class='<%=cellStyle%>' >
								 			<span class='normalText'>
								 			 <%if (levelOfDetail.equals("requirementType")){%>
												<a href='#' onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "passedTesting")' >
									 			<%=numOfTestPassRequirements %>
									 			</a>
											<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												 <a href='#' onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>, "<%=requirementTypeShortName%>", "passedTesting")' >
									 			<%=numOfTestPassRequirements %>
									 			</a>
											<%} %>
								 			</span>		
								 		</td>				
								 		<td class='<%=cellStyle%>' >
								 			<span class='normalText'>
								 			 <%if (levelOfDetail.equals("requirementType")){%>
												<a href='#' onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "failedTesting")' >
									 			<%=numOfTestFailRequirements %>
									 			</a>
											<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												<a href='#' onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>, "<%=requirementTypeShortName%>", "failedTesting")' >
									 			<%=numOfTestFailRequirements %>
									 			</a>
											<%} %>
								 			</span>		
								 		</td>							 					 				 		
								 	<%} %>	
							 		
							 		
							 		
									<% if(focusOn.contains("Completion")){ %>
										
										<td class='<%=cellStyle%>' >
								 			<span class='normalText'>
								 			 <%if (levelOfDetail.equals("requirementType")){%>
												<a href='#' onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "completed")' >
									 			<%=numOfCompletedRequirements %>
									 			</a>
											<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												<a href='#' onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>, "<%=requirementTypeShortName%>", "completed")' >
									 			<%=numOfCompletedRequirements %>
									 			</a>
											<%} %>
								 			</span>							 		
								 		</td>				
								 		<td class='<%=cellStyle%>' >
								 			<span class='normalText'>
								 			 <%if (levelOfDetail.equals("requirementType")){%>
												<a href='#' onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "incomplete")' >
									 			<%=numOfIncompleteRequirements%>
									 			</a>
											<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												<a href='#' onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>, "<%=requirementTypeShortName%>", "incomplete")' >
									 			<%=numOfIncompleteRequirements%>
									 			</a>
											<%} %>
								 			</span>								 		
								 			
								 		</td>					
									<%} %>		


				 				</tr>
					 <%
					    	}
					    }
					%>
				
				</table>
				</div>
				
				
			</td>
		</tr>
	</table>
	</div>
	
	
	
<%}%>