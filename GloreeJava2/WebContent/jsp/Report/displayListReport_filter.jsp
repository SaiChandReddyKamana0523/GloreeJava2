<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String dFFIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dFFIsLoggedIn == null) || (dFFIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
%>
		// log in page.
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project dFFproject= (Project) session.getAttribute("project");
	SecurityProfile dFFsecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dFFIsMember = false;
	if (dFFsecurityProfile.getRoles().contains("MemberInProject" + dFFproject.getProjectId())){
		dFFIsMember = true;
	}
	
	if (dFFIsMember){
		
		String databaseType = this.getServletContext().getInitParameter("databaseType");
		ArrayList releases = ProjectUtil.getAllReleasesInProject(dFFproject.getProjectId(), databaseType);
	
		int  dFFFolderId = Integer.parseInt(request.getParameter("folderId"));
		Folder dFFFolder = new Folder(dFFFolderId);
		
		String reportDefinition = "";
		String reportInfo = "";
		String deleteReport = " <span> <font color='gray'> " +
			" Delete </font>";

		String urlToReport = " <span> <font color='gray'> " +
			" URL</font> " +
			" <span>&nbsp;|&nbsp;</span> ";
			
		String reportURLString  = "";

		// Note, this can be called for new reports (where you get only folderId)
		// or for existing reports (WHERE YOU GET BOTH FOLDERID and reportId).
		// we will use reportId in the 'displayListReportFilter' section.
		
		// So, if this is a displayExisting Report request we will need to prefill
		// the fields.
		String danglingValue = "";
		String orphanValue = "";
		String completedValue = "";
		String incompleteValue = "";
		String suspectUpStreamValue = "";
		String suspectDownStreamValue = "";
		String lockedValue = "";
		String includeSubFoldersValue = "";
		
		String inRTBaselineValue = "-1";
		String changedAfterRTBaselineValue = "-1";

		String testingStatusPending = "";
		String testingStatusPass = "";
		String testingStatusFail = "";

		String nameValue = "";
		String descriptionValue = "";
		String ownerValue = "";
		String externalURLValue = "";
		String pctCompleteValue = "";
		String approvedByValue = "";
		String rejectedByValue = "";
		String pendingByValue  = ""; 
		String traceToValue = "";
		String traceFromValue = "";
		
		String statusDraft = "";
		String statusInApprovalWorkFlow = "";
		String statusApproved = "";
		String statusRejected = "";
		
		String priorityHigh = "";
		String priorityMedium = "";
		String priorityLow = "";
		
		// these attributes are used to track whether this report (if saved)
		// had which attributes set to display.
		String standardDisplayDescription = "";
		String standardDisplayComments = "";
		String standardDisplayTestingStatus = "";
		String standardDisplayOwner = "";
		String standardDisplayExternalURL = "";
		String standardDisplayTraceTo = "";
		String standardDisplayTraceFrom = "";
		String standardDisplayApprovedBy = "";
		String standardDisplayRejectedBy = "";
		String standardDisplayPendingBy = "";
		String standardDisplayLockedBy = "";
		String standardDisplayStatus = "";
		String standardDisplayPriority = "";
		String standardDisplayPercentComplete = "";
		String standardDisplayFolderPath = "";
		String standardDisplayBaselines= "";
		String standardDisplayCreatedBy = "";
		String standardDisplayCreatedDate = "";
		String standardDisplayLastModifiedBy = "";
		String standardDisplayLastModifiedDate = "";
		String standardDisplayAttachments = "";
		
		String customAttributesDisplay = "";
		
		String sortBy = "";
		String sortByType = "";

		String inRelease = "";
		
		int rowsPerPage = 500;
		
		String reportIdString = request.getParameter("reportId");
		// note reportIdString can be null if we did not get here from a saved report.
		session.setAttribute("listReportIdStringForFolder" + dFFFolderId , reportIdString);
		
		if ((reportIdString != null) && (!(reportIdString.equals("")))){
			// we need to display some options like deleteReport, urlToReport etc...
			// only if the report alredy exists.
			Report report = new Report (Integer.parseInt(reportIdString));
			// lets store the session object for the report Id here.
			
			
			// see if the user is either admin or is the creator of this report.
			// if so , they can remove this report.
			User user = dFFsecurityProfile.getUser();
			boolean dFFIsAdmin = false;
			if (dFFsecurityProfile.getRoles().contains("AdministratorInProject" + dFFproject.getProjectId())){
				dFFIsAdmin = true;
			}
			if ((dFFIsAdmin) || (report.getCreatedByEmailId().equals(user.getEmailId()))){
				deleteReport = "<a href='#' onClick='deleteReport(" 
						+ dFFFolderId + "," + report.getReportId() + ")'> " +
						 " Delete</a> ";	
			}
			
			
			
			reportURLString = ProjectUtil.getURL(request, report.getReportId(), "report") ;
			
			reportDefinition = report.getReportDefinition();
		}
		else {
			// its possible that this report was called by a dynamicReport link. 
			// i.e. some one came here from chart metrics. In this case, reportDefinition 
			// is a parameter to this call, and not a stored report.
			reportDefinition = request.getParameter("reportDefinition");
		}
		

		if (reportDefinition != null) {
			// this means that we got a report definition, either as a param to request
			// or from a stored report.
			// lets get the checkbox values.
			if (!(reportDefinition.contains("danglingSearch:--:all"))){
				danglingValue = "checked";
			}
			if (!(reportDefinition.contains("orphanSearch:--:all"))){
				orphanValue = "checked";
			}
			if (!(reportDefinition.contains("completedSearch:--:all"))){
				completedValue = "checked";
			}
			if (!(reportDefinition.contains("incompleteSearch:--:all"))){
				incompleteValue = "checked";
			}
			if (!(reportDefinition.contains("suspectUpStreamSearch:--:all"))){
				suspectUpStreamValue = "checked";
			}
			if (!(reportDefinition.contains("suspectDownStreamSearch:--:all"))){
				suspectDownStreamValue = "checked";
			}
			if (reportDefinition.contains("lockedSearch:--:lockedOnly")){
				lockedValue = "checked";
			}
			if (!(reportDefinition.contains("includeSubFoldersSearch:--:no"))){
				includeSubFoldersValue = "checked";
			}
			


			
			// lets get the text box values for standard attributes.
			String [] values = reportDefinition.split(":###:");
			for (int i=0; i<values.length;i++) {
				String value = "";
				value = values[i];
				
				if (value.contains("inRTBaselineSearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1  ){
						inRTBaselineValue = a[1]; 
					}
				}

				if (value.contains("changedAfterRTBaselineSearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1  ){
					 	changedAfterRTBaselineValue = a[1];
					}
				}				
				
				// handling the testing select box.
				if ((value.contains("testingStatusSearch:--:") && (value.contains("Pending")))) {
					testingStatusPending = "SELECTED";
				}
				if ((value.contains("testingStatusSearch:--:") && (value.contains("Pass")))) {
					testingStatusPass = "SELECTED";
				}
				if ((value.contains("testingStatusSearch:--:") && (value.contains("Fail")))) {
					testingStatusFail = "SELECTED";
				}

				
				if (value.contains("nameSearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1  ){
						nameValue = "value = '" + a[1] + "'" ;
					}
				}
				
				if (value.contains("descriptionSearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						descriptionValue = "value = '" + a[1] + "'" ;
					}					
				}
				if (value.contains("ownerSearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						ownerValue = "value = '" + a[1] + "'" ;
					}
				}
				if (value.contains("externalURLSearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						externalURLValue = "value = '" + a[1] + "'" ;
					}
				}
				if (value.contains("pctCompleteSearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						pctCompleteValue = "value = '" + a[1] + "'" ;
					}
				}
				if (value.contains("approvedBySearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						approvedByValue = "value = '" + a[1] + "'" ;
					}
				}
				if (value.contains("rejectedBySearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						rejectedByValue = "value = '" + a[1] + "'" ;
					}
				}
				if (value.contains("pendingBySearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						pendingByValue = "value = '" + a[1] + "'" ;
					}
				}
				
				if (value.contains("traceToSearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						traceToValue = "value = '" + a[1] + "'" ;
					}
				}
				if (value.contains("traceFromSearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						traceFromValue = "value = '" + a[1] + "'" ;
					}
				}
				
				// handling the status select box.
				if ((value.contains("statusSearch:--:") && (value.contains("Draft")))) {
					statusDraft = "SELECTED";
				}
				if ((value.contains("statusSearch:--:") && (value.contains("In Approval WorkFlow")))) {
					statusInApprovalWorkFlow = "SELECTED";
				}
				if ((value.contains("statusSearch:--:") && (value.contains("Approved")))) {
					statusApproved = "SELECTED";
				}
				if ((value.contains("statusSearch:--:") && (value.contains("Rejected")))) {
					statusRejected = "SELECTED";
				}
				
				// handling the priority select box.
				if ((value.contains("prioritySearch:--:") && (value.contains("High")))) {
					priorityHigh = "SELECTED";
				}
				if ((value.contains("prioritySearch:--:") && (value.contains("Medium")))) {
					priorityMedium = "SELECTED";
				}
				if ((value.contains("prioritySearch:--:") && (value.contains("Low")))) {
					priorityLow = "SELECTED";
				}
				
				
				// standard display items.
				if ((value.contains("standardDisplay:--:") && (value.contains("description")))) {
					standardDisplayDescription = "SELECTED";
				}
				
				if ((value.contains("standardDisplay:--:") && (value.contains("comments")))) {
					standardDisplayComments= "SELECTED";
				}
				
				
				if ((value.contains("standardDisplay:--:") && (value.contains("testingStatus")))) {
					standardDisplayTestingStatus = "SELECTED";
				}
				if ((value.contains("standardDisplay:--:") && (value.contains("owner")))) {
					standardDisplayOwner = "SELECTED";
				}

				if ((value.contains("standardDisplay:--:") && (value.contains("externalURL")))) {
					standardDisplayExternalURL = "SELECTED";
				}

				if ((value.contains("standardDisplay:--:") && (value.contains("traceTo")))) {
					standardDisplayTraceTo = "SELECTED";
				}
				if ((value.contains("standardDisplay:--:") && (value.contains("traceFrom")))) {
					standardDisplayTraceFrom = "SELECTED";
				}
				if ((value.contains("standardDisplay:--:") && (value.contains("approvedBy")))) {
					standardDisplayApprovedBy = "SELECTED";
				}
				if ((value.contains("standardDisplay:--:") && (value.contains("rejectedBy")))) {
					standardDisplayRejectedBy = "SELECTED";
				}
				if ((value.contains("standardDisplay:--:") && (value.contains("pendingBy")))) {
					standardDisplayPendingBy = "SELECTED";
				}
				if ((value.contains("standardDisplay:--:") && (value.contains("lockedBy")))) {
					standardDisplayLockedBy = "SELECTED";
				}
				if ((value.contains("standardDisplay:--:") && (value.contains("status")))) {
					standardDisplayStatus = "SELECTED";
				}				
				if ((value.contains("standardDisplay:--:") && (value.contains("priority")))) {
					standardDisplayPriority = "SELECTED";
				}
				if ((value.contains("standardDisplay:--:") && (value.contains("percentComplete")))) {
					standardDisplayPercentComplete = "SELECTED";
				}
				if ((value.contains("standardDisplay:--:") && (value.contains("folderPath")))) {
					standardDisplayFolderPath = "SELECTED";
				}
				if ((value.contains("standardDisplay:--:") && (value.contains("baselines")))) {
					standardDisplayBaselines = "SELECTED";
				}
				if ((value.contains("standardDisplay:--:") && (value.contains("createdBy")))) {
					standardDisplayCreatedBy = "SELECTED";
				}
				if ((value.contains("standardDisplay:--:") && (value.contains("createdDate")))) {
					standardDisplayCreatedDate = "SELECTED";
				}
				if ((value.contains("standardDisplay:--:") && (value.contains("lastModifiedBy")))) {
					standardDisplayLastModifiedBy = "SELECTED";
				}
				
				if ((value.contains("standardDisplay:--:") && (value.contains("lastModifiedDate")))) {
					standardDisplayLastModifiedDate = "SELECTED";
				}
				
				if ((value.contains("standardDisplay:--:") && (value.contains("attachments")))) {
					standardDisplayAttachments = "SELECTED";
				}
				// lets capture the customAttributesDisplay string  and we can use this 
				// to select the filters for custom attributes display.
				if (value.contains("customAttributesDisplay:--:") ) {
					customAttributesDisplay = value;
				}
				
				if (value.contains("sortBy:--:")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						sortBy =  a[1] ;
					}
				}
				
				if (value.contains("sortByType:--:")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						sortByType =  a[1] ;
					}
				}
				
				if (value.contains("rowsPerPage:--:")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						try {
						rowsPerPage =  Integer.parseInt(a[1]) ;
						}
						catch (Exception e){
							e.printStackTrace();
							rowsPerPage = 100;
						}
					}
				}
				
				
				if (value.contains("inRelease:--:")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						inRelease =  a[1] ;
					}
				}
			
			}
		}		
%>
	<table class='paddedTable'   width='100%' >
		<tr>
			<td colspan='3' align='left' valign='middle' bgcolor='#99CCFF'>
				<div style='float:left'>			
					<span class='subSectionHeadingText'>
					Report and Bulk Edit Requirements
					</span>
				</div>
				<div style='float:right'>
					<span title='Report and Bulk Operation Help Video'>
					<a target="_blank" href="http://www.youtube.com/watch?v=BlIEy-0DnLQ">
					<img height="20" border="0" src="/GloreeJava2/images/television.png"/>
					</a>
					</span>
					&nbsp;&nbsp;
					<span title='Generating and Saving Reports Help Video'>
					<a target="_blank" href="http://www.youtube.com/watch?v=HH0nlI-R52I">
					<img height="20" border="0" src="/GloreeJava2/images/television.png"/>
					</a>
					</span>
					&nbsp;&nbsp; 	
				</div>
			</td>
		</tr>

		<tr>
			<td>
				<div id='showFilterLinkDiv' style='display:none;' >
					<span class='normalText'>
						<input type='button' class='btn btn-sm btn-primary' value='Modify Report'
							onclick="
							document.getElementById('showFilterLinkDiv').style.display = 'none';
							document.getElementById('filterDetailsDiv').style.display = 'block';
							document.getElementById('closeFilterDiv').style.display = 'block';
							document.getElementById('openFilterDiv').style.display = 'none';
							document.getElementById('contentCenterF').style.display = 'none';
							document.getElementById('listReportBulkActionDiv').style.display = 'none';
							"
						>
						
						&nbsp;&nbsp;
						<input type='button' value='  Refresh Report  '  class='btn btn-sm btn-primary'
										onclick="reportAction(<%=dFFFolderId%>,'runReport','list');">
										
						
						<input type='button' id='displayFilterRSButton' value='  Filter Result Set  '  style='height:25px' class='btn btn-sm btn-primary'
							onClick="
								document.getElementById('filterResultsDiv').style.display='block';
								document.getElementById('displayFilterRSButton').style.display='none';
							"
						>
					</span>
					<div 
						id='filterResultsDiv' style='display:none;'
						class="panel panel-info" style="background-color:lightblue; width:500px"
						
						>
							
						  <div class="panel-heading" >Filter Criteria</div>
							<div class="panel-body">
											
								<textarea id='filterCondition' rows=5 cols=100></textarea>
								<br>
								<input type='button' value='  Filter & Refresh Report  '  
									style='height:25px' class='btn btn-sm btn-primary'
									onClick="reportAction(<%=dFFFolderId%>,'runReport','list');"
								>
									
								&nbsp;&nbsp;&nbsp;&nbsp;
								<input type='button' value='  Cancel  '  
									style='height:25px' class='btn btn-sm btn-danger'
									onClick="
										document.getElementById('displayFilterRSButton').style.display='block';
										document.getElementById('filterCondition').value = '';
										document.getElementById('filterResultsDiv').style.display='none';
										reportAction(<%=dFFFolderId%>,'runReport','list');
								
									"
								>
								
								<input type='button' id='showFilterSyntaxButton' value='  Show How to use filter  '  
									style='height:25px' class='btn btn-sm btn-success'
									onClick="
										document.getElementById('filterSyntaxDiv').style.display='block';
										document.getElementById('showFilterSyntaxButton').style.display='none';
										document.getElementById('hideFilterSyntaxButton').style.display='block';
										
									"
								>
								<input type='button' id='hideFilterSyntaxButton' value='  Hide Filter Usage  '  
									style='height:25px; display:none' class='btn btn-sm btn-primary'
									onClick="
										document.getElementById('filterSyntaxDiv').style.display='none';
										document.getElementById('showFilterSyntaxButton').style.display='block';
										document.getElementById('hideFilterSyntaxButton').style.display='none';
										
									"
								>
							
									<div id='filterSyntaxDiv' class='alert alert-primary' style='display:none'>
										<table class='table table-striped' style='width:1000px'>
											<tr><td class='info' style='width:300px'>Filter Example</td> 
												<td class='info'>Filter Definition</td>
											</tr>
											
											<tr><td colspan='2' class='success'> Single Attribute Filter </td></tr>
											
											<tr><td >Customer = Cisco</td> 
												<td >Objects with Customer attribute set up CISCO or cisco or Cisco </td>
											</tr>
											<tr><td>Customer LIKE isco </td> 
												<td >Customer matches TISCO,  CISCO or cisco  Systems or IsCoolCommpany </td>
											</tr>
											<tr><td >Customer = Cisco | Apple </td> 
												<td >Objects with Customer attribute set up Cisco OR Apple </td>
											</tr>
											<tr><td>Customer LIKE isco | pple </td> 
												<td >Customer matches TISCO,  CISCO or Apple or Topple </td>
											</tr>
											<tr><td>Customer NOT Cisco  </td> 
												<td >Customer has a value that is NOT Cisco </td>
											</tr>
											<tr><td colspan='2' class='success'> Multiple Attribute Filter </td></tr>
											<tr><td>Customer = Cisco AND Location = New York  </td> 
												<td >Customer attribute is Cisco and Location attribute is New York </td>
											</tr>
											<tr><td>Customer = Cisco OR  Location = New York  </td> 
												<td >Customer attribute is Cisco OR  Location attribute is New York </td>
											</tr>
											<tr><td>Mix and Match </td> 
												<td >You can have unlimited AND / OR statements chained together </td>
											</tr>
											<tr class='danger'><td>Warning </td> 
												<td >You can ONLY use All AND statements or ALL OR statements. You can not Mix and Match And / Or</td>
											</tr>
											<tr><td>Mix and Match </td> 
												<td >You can run complex queries like the following : <br>
													Deliverability = Desirable and customer like cisco | ple and Impact to customer = NOT no	
												</td>
											</tr>
											
										</table>
									</div>
							
							</div>
						
						
					</div>
				</div>
					
				</div>				
				<div id='filterDetailsDiv'>
					<table width='100%'>
			
						<tr>
							<td align='left' >
								<div style="font-size:8pt; float:left">
									<div id="closeFilterDiv" style="float: left;">
										<span class='normalText'>
										<a href='#' onclick="
											document.getElementById('showFilterLinkDiv').style.display = 'block';
											document.getElementById('filterDetailsDiv').style.display = 'none';
											document.getElementById('closeFilterDiv').style.display = 'none';
											document.getElementById('openFilterDiv').style.display = 'block';
											">
										Close Filter</a> 
										</span>
									</div>
									<div id="openFilterDiv" style="float: left; display:none;">
										<span class='normalText'>
										<a href='#' onclick="
											document.getElementById('filterDetailsDiv').style.display = 'block';
											document.getElementById('closeFilterDiv').style.display = 'block';
											document.getElementById('openFilterDiv').style.display = 'none';
											">
										Show Filter</a> 
										</span>
									</div>
									
									<span>&nbsp;|&nbsp;</span>
									
									<a href='#' 
									onclick="
										reportAction(<%=dFFFolderId%>,'runReport','list');">
										Run 
									</a>
									<span>&nbsp;|&nbsp;</span>
													
													
									<a href='#' onClick='
										document.getElementById("saveReportDiv").style.display = "block";
										document.getElementById("filterDetailsDiv").style.display = "block";
										document.getElementById("closeFilterDiv").style.display = "block";
										document.getElementById("openFilterDiv").style.display = "none";						
									'>
									 Save
									</a> 
											 
									<span>&nbsp;|&nbsp;</span>
														
									<!--  Remove the report if the user is either the admin or creator of report. -->
									<%=deleteReport %>
									<span>&nbsp;|&nbsp;</span>
													
													
									<a href='#' onClick='
										clearReportFilter();
										document.getElementById("filterDetailsDiv").style.display = "block";
										document.getElementById("closeFilterDiv").style.display = "block";
										document.getElementById("openFilterDiv").style.display = "none";					
										'>
									Clear All Filters
									</a>
								</div>	
												
								<div  style="float: right;">
									<span class='normalText'>
									Keep Filter Open &nbsp; 
									<input type='checkbox' name='keepFilterOpen' id = 'keepFilterOpen'>
									</span>
								</div>
				
							</td>
						</tr>





						<!--  The hidden divs that will be used to display URL / Save options -->
						
						
						
						<tr>
							<td align="left">
								<div id ='saveReportResultDiv' class='alert alert-success' style="display:none;"></div>
								<div id ='saveReportDiv' class='alert alert-success' style="display:none;">
								<%
								
								if ((reportIdString != null) && (!(reportIdString.equals("")))){
									// we need to display some options like deleteReport, urlToReport etc...
									// only if the report alredy exists.
									Report report = new Report (Integer.parseInt(reportIdString));
									
									Folder reportInFolder = new Folder(report.getFolderId());
								%>
									<table class='table'>
									<tr><td colspan='2' class='info'> Update Existing Report</td></tr>
									<tr >
											<td style='width:100'><span class='normalText'>Report Id</span></td>
											<td><span class='normalText'><%=report.getReportId() %></span></td>
										</tr>
										<tr>
											<td style='width:100'><span class='normalText'>Visibility</span></td>
											<td><span class='normalText'><%=report.getReportVisibility() %></span></td>
										</tr>
										<tr>
											<td style='width:100'><span class='normalText'>Name</span></td>
											<td><span class='normalText'><%=report.getReportName() %></span></td>
										</tr>
										<tr >
											<td style='width:100'><span class='normalText'>Description</span></td>
											<td><span class='normalText'><%=report.getReportDescription() %></span></td>
										</tr>
										<tr >
											<td style='width:100'><span class='normalText'>Created  By</span></td>
											<td><span class='normalText'><%=report.getCreatedByEmailId() %></span></td>
										</tr>
										<tr >
											<td style='width:100'><span class='normalText'>Report Location</span></td>
											<td><span class='normalText'><%=reportInFolder.getFolderPath() %></span></td>
										</tr>
										
										<tr >
											<td style='width:100'><span class='normalText'>Direct Link to this Report</span></td>
											<td><span class='normalText'><a href='<%=reportURLString%>' target='_blank'><%=reportURLString%></a></span></td>
										</tr>
										<tr>
											<td>
												<a href='#' onClick="reportAction(<%=dFFFolderId%>,'updateReport:#:<%=report.getReportId()%>','list')"> Update Existing Report </a>
											</td>
											<td>
												<a href='#' onClick='document.getElementById("saveReportDiv").style.display = "none";'>
												Cancel </a>			 
											</td>
										</tr>	
									</table>
									<br>
								<%} %>
								
								<table class='table'>
									<tr><td colspan='2' class='info'> Create New Report</td></tr>
									<tr>
										<td><span class='headingText'>Visibility</span></td>
										<td>
											<span class='headingText'>
											<select name='reportVisibility' id='reportVisibility'>
												<option value='public' SELECTED> Public </option>
												<option value='private'> Private </option>
											</select>
											</span>
										</td>
									</tr> 								
									<tr>
										<td><span class='headingText'>Report Name </span></td>
										<td><input type="text"  name="reportName" id="reportName" size="60"
											 maxlength="100"> </td>
									</tr> 
									<tr>
										<td><span class='headingText'>Report Description </span></td>
										<td>
										<textarea name="reportDescription" id="reportDesciption" rows="3" cols="60"></textarea> 
										</td>
									</tr>
									<tr>
										<td>
											<a href='#' onClick="reportAction(<%=dFFFolderId%>,'saveReport','list')"> Save Report </a>
										</td>
										<td>
											<a href='#' onClick='document.getElementById("saveReportDiv").style.display = "none";'>
											Cancel </a>			 
										</td>
									</tr>						
															
								</table>
								</div> 
							</td>
						</tr>													
						
			
						<%
						
						if ((reportIdString != null) && (!(reportIdString.equals("")))){
							// we need to display some options like deleteReport, urlToReport etc...
							// only if the report alredy exists.
							Report report = new Report (Integer.parseInt(reportIdString));
							Folder reportInFolder = new Folder(report.getFolderId());
						%>
						<tr>
							<td colspan='3'>
								<div class='alert alert-success' style='float:center;' >
									<table style='width:100%'>
										<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
											<td style='width:100'><span class='normalText'>Report Id</span></td>
											<td><span class='normalText'><%=report.getReportId() %></span></td>
										</tr>
										<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
											<td style='width:100'><span class='normalText'>Visibility</span></td>
											<td><span class='normalText'><%=report.getReportVisibility() %></span></td>
										</tr>
										<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
											<td style='width:100'><span class='normalText'>Name</span></td>
											<td><span class='normalText'><%=report.getReportName() %></span></td>
										</tr>
										<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
											<td style='width:100'><span class='normalText'>Description</span></td>
											<td><span class='normalText'><%=report.getReportDescription() %></span></td>
										</tr>
										<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
											<td style='width:100'><span class='normalText'>Created  By</span></td>
											<td><span class='normalText'><%=report.getCreatedByEmailId() %></span></td>
										</tr>
										<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
											<td style='width:100'><span class='normalText'>Report Location</span></td>
											<td><span class='normalText'><%=reportInFolder.getFolderPath() %></span></td>
										</tr>
										
										<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
											<td style='width:100'><span class='normalText'>Direct Link to this Report</span></td>
											<td><span class='normalText'><a href='<%=reportURLString%>' target='_blank'><%=reportURLString%></a></span></td>
										</tr>
										
									</table>
								</div>
							</td>
						</tr>
						<%} %>
						<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
							<td  align='left'>
								<span class='headingText'> 
									<b>Step 1 (Optional) : Apply Filters 
									<select id='addAFilter' name='addAFilter'
										onChange='addReportFilterCondition();'>
										<option value='selectAFilter'></option>
										<option value='includeSubFoldersFilter'>Include Requirements in Subfolders</option>
										<option value='danglingFilter'>Dangling Requirements</option>
										<option value='orphanFilter'>Orphan Requirements</option>
										<option value='completedFilter'>Completed Requirements</option>
										<option value='incompleteFilter'>Incomplete Requirements</option>
										<option value='suspectUpStreamFilter'>Suspect UpStream Requirements</option>
										<option value='suspectDownStreamFilter'>Suspect DownStream Requirements</option>
										<option value='lockedFilter'>Locked Requirements</option>
										<option value=''></option>
										<option value='inBaselineFilter'>In Baseline</option>
										<option value='changedAfterBaselineFilter'>Changed After Baseline</option>
										<option value=''></option>
										<option value='nameFilter'>Name like </option>
										<option value='descriptionFilter'>Description like </option>
										<option value='ownerFilter'>Owner Email id like </option>
										<option value='externalURLFilter'>External URL like </option>
										<option value=''></option>
										<option value='pctCompleteFilter'>Percent Complete less than</option>
										<option value='statusFilter'>Approval Status in </option>
										<option value='priorityFilter'>Priority in </option>
										<option value='testingStatusFilter'>Testing Status in </option>
										<option value=''></option>
										<option value='approvedByFilter'>Approved By</option>
										<option value='rejectedByFilter'>Rejected By</option>
										<option value='approvalPendingByFilter'>Approval Pending By</option>
										<option value=''></option>
										<option value='traceToFilter'>Trace To</option>
										<option value='traceFromFilter'>Traces From</option>
										<%
										ArrayList attributes = (ArrayList) ProjectUtil.getAllAttributes(dFFFolder.getRequirementTypeId());
										if (attributes.size() > 0){
										%>
											<option value=''></option>
										<%
											// lets print the option list for custom attributes.
											Iterator j = attributes.iterator();
											while (j.hasNext()){
												RTAttribute a = (RTAttribute) j.next();
										%>
												<option value='customA<%=a.getAttributeId()%>Div'> <%=a.getAttributeName() %></option>
										<%
											}
										}
										%>					
									</select>
								</span>
								
							</td>
						</tr>
						<tr>
							<td align='left'>
								
								<%if (includeSubFoldersValue.equals("")){ %>
								<div id="includeSubFoldersFilterDiv" style="display:none;">
								<%} else { %>
								<div id="includeSubFoldersFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
									<td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("includeSubFoldersSearch").checked = false;
										document.getElementById("includeSubFoldersFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> Include Requirements in Sub Folders</span>
									</td><td>
									<input type='checkbox' name='includeSubFoldersSearch' id = 'includeSubFoldersSearch'
									<%=includeSubFoldersValue%> >
									</td></tr></table>				
								</div>
								
								
								<%if (danglingValue.equals("")){ %>
								<div id="danglingFilterDiv" style="display:none;">
								<%} else { %>
								<div id="danglingFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
									<td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("danglingSearch").checked = false;
										document.getElementById("danglingFilterDiv").style.display="none";
										'>
					 				<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> Dangling (No Downstream) Requirements</span>
									</td><td>
									<input type='checkbox' name='danglingSearch' id = 'danglingSearch' <%=danglingValue%> >
									</td></tr></table>
								</div>
								
								<%if (orphanValue.equals("")){ %>
								<div id="orphanFilterDiv" style="display:none;">
								<%} else { %>
								<div id="orphanFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
									<td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("orphanSearch").checked = false;
										document.getElementById("orphanFilterDiv").style.display="none";
										'>
					 				<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> Orphan (No Upstream) Requirements</span>
									</td><td>
									<input type='checkbox' name='orphanSearch' id = 'orphanSearch' <%=orphanValue%> >
									</td></tr></table>
								</div>
								
								<%if (completedValue.equals("")){ %>
								<div id="completedFilterDiv" style="display:none;">
								<%} else { %>
								<div id="completedFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
									<td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("completedSearch").checked = false;
										document.getElementById("completedFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> Completed Requirements</span>
									</td><td>
									<input type='checkbox' name='completedSearch' id = 'completedSearch' <%=completedValue%> >
									</td></tr></table> 
								</div>
				
								<%if (incompleteValue.equals("")){ %>
								<div id="incompleteFilterDiv" style="display:none;">
								<%} else { %>
								<div id="incompleteFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
									<td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("incompleteSearch").checked = false;
										document.getElementById("incompleteFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> Incomplete Requirements</span>
									</td><td>
									<input type='checkbox' name='incompleteSearch' id = 'incompleteSearch' <%=incompleteValue%> >
									</td></tr></table> 	
								</div>
						
								<%if (suspectUpStreamValue.equals("")){ %>
								<div id="suspectUpStreamFilterDiv" style="display:none;">
								<%} else { %>
								<div id="suspectUpStreamFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
									<td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("suspectUpStreamSearch").checked = false;
										document.getElementById("suspectUpStreamFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> Requirements with Suspect Up Stream</span>
									</td><td>
									<input type='checkbox' name='suspectUpStreamSearch' id = 'suspectUpStreamSearch'
									<%=suspectUpStreamValue%> >
									</td></tr></table>
								</div> 	
								
								
								<%if (suspectDownStreamValue.equals("")){ %>
								<div id="suspectDownStreamFilterDiv" style="display:none;">
								<%} else { %>
								<div id="suspectDownStreamFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
									<td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("suspectDownStreamSearch").checked = false;
										document.getElementById("suspectDownStreamFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> Requirements with Suspect  Down Stream </span>
									</td><td>
									<input type='checkbox' name='suspectDownStreamSearch' id = 'suspectDownStreamSearch'
									<%=suspectDownStreamValue%> >
									</td></tr></table>
								</div> 	
				
								<%if (lockedValue.equals("")){ %>
								<div id="lockedFilterDiv" style="display:none;">
								<%} else { %>
								<div id="lockedFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
									<td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("lockedSearch").checked = false;
										document.getElementById("lockedFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> Show Only locked Requirements</span>
									</td><td>
									<input type='checkbox' name='lockedSearch' id = 'lockedSearch'
									<%=lockedValue%> >
									</td></tr></table>				
								</div>
													
							
								
								<%if ((inRTBaselineValue != null) && (!inRTBaselineValue.equals("-1"))){%>
								<div id="inRTBaselineFilterDiv" style="display:block;">
								<%} else { %>
								<div id="inRTBaselineFilterDiv" style="display:none;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
									<td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("inRTBaselineSearch").selectedIndex= null;
										document.getElementById("inRTBaselineFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> 
									Requirements In Baseline 
									<img src="/GloreeJava2/images/baseline16.png" border="0">
									</span>
									</td><td>
										<span class='headingText'> 
										<select name="inRTBaselineSearch" id='inRTBaselineSearch'>
											<option value='-1'></option>
											<%
											ArrayList baselines = ProjectUtil.getEligibleBaselinesForRequirementType(dFFFolder.getRequirementTypeId());
											Iterator m = baselines.iterator();
											while (m.hasNext()){
												RTBaseline rTBaseline = (RTBaseline) m.next();
												
												// if the stored report's baseline value is same one of the drop down
												// values, lets have the pre selected.
												String inRTBaselineSelected = "";
												if ((inRTBaselineValue != null) && (!inRTBaselineValue.equals(""))){
													int storedinRTBaselineValue = Integer.parseInt(inRTBaselineValue);
													if (storedinRTBaselineValue == rTBaseline.getBaselineId()){
														inRTBaselineSelected = "SELECTED";	
													}
												}
											%>
												<option value='<%=rTBaseline.getBaselineId()%>'  <%=inRTBaselineSelected%>>
													<%=rTBaseline.getBaselineName() %>
													<%if (rTBaseline.getLocked() == 1 ){ %>
														(Locked)
													<%}
													else { %>
														(Unlocked)
													<%} %>
												</option>
											<%	
											}
											%>	
										</select>
										</span>
									</td></tr></table>
								</div>
								
								<%if ((changedAfterRTBaselineValue != null) && (!changedAfterRTBaselineValue.equals("-1"))){%>
								<div id="changedAfterRTBaselineFilterDiv" style="display:block;">
								<%} else { %>
								<div id="changedAfterRTBaselineFilterDiv" style="display:none;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
									<td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("changedAfterRTBaselineSearch").selectedIndex= null;
										document.getElementById("changedAfterRTBaselineFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> 
									Requirements changed After Baseline
									<img src="/GloreeJava2/images/baseline16.png" border="0">
									</span>
									</td><td>
									<span class='headingText'> 
										<select name="changedAfterRTBaselineSearch" id='changedAfterRTBaselineSearch'>
											<option value='-1'></option>
											<%
											Iterator n = baselines.iterator();
											while (n.hasNext()){
												RTBaseline rTBaseline = (RTBaseline) n.next();
												
												// if the stored report's baseline value is same one of the drop down
												// values, lets have the pre selected.
												String changedAfterRTBaselineSelected = "";
												if ((changedAfterRTBaselineValue != null) && (!changedAfterRTBaselineValue.equals(""))){
													int storedChangedAfterRTBaselineValue = Integer.parseInt(changedAfterRTBaselineValue);
													if (storedChangedAfterRTBaselineValue == rTBaseline.getBaselineId()){
														changedAfterRTBaselineSelected = "SELECTED";	
													}
												}
											%>
												<option value='<%=rTBaseline.getBaselineId()%>'  <%=changedAfterRTBaselineSelected%> >
													<%=rTBaseline.getBaselineName() %>
													<%if (rTBaseline.getLocked() == 1 ){ %>
														(Locked)
													<%}
													else { %>
														(Unlocked)
													<%} %>
												</option>
											<%	
											}
											%>	
										</select>
										</span>
									</td></tr></table>
								</div>





								<%if (nameValue.equals("")){ %>
								<div id="nameFilterDiv" style="display:none;">
								<%} else { %>
								<div id="nameFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
									 <td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("nameSearch").value= "";
										document.getElementById("nameFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> 
									Name like
									</span>
									</td>
										<td>
											<span class='headingText'> 
												<input type="text"  name="nameSearch" id="nameSearch" size="50"
										 			maxlength="100" <%=nameValue%>>
											</span>
											
											<a href="#" 
											onMouseOver='document.getElementById("nameHelpDiv").style.display="block";'
											onMouseOut='document.getElementById("nameHelpDiv").style.display="none";'
											>
												&nbsp;&nbsp;&nbsp;<img src="/GloreeJava2/images/help.png" border="0">&nbsp;&nbsp;&nbsp;
											</a>
											<div id='nameHelpDiv' style='display:none'>
												<span class='normalText'><font color='red'>
													Use #OR# to apply more than one Name filter
													<br> Example : To search where Name has X or Y, use 'X #OR# Y'
													</font>
												</span>
												<br>
												<span class='normalText'><font color='red'>
													Use = to make an exact search. 
													<br> Example :   =XYZ To search where Requirement's name exactly maches XYZ
													</font>
												</span>
												
											</div>
										</td>
									</tr></table>
								</div>

								<%if (descriptionValue.equals("")){ %>
								<div id="descriptionFilterDiv" style="display:none;">
								<%} else { %>
								<div id="descriptionFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
									<td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("descriptionSearch").value= "";
										document.getElementById("descriptionFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> 
									Description like
									</span>
									</td>
										<td>
											<span class='headingText'> 
											<input type="text"  name="descriptionSearch" id="descriptionSearch" size="50"
													 maxlength="100" <%=descriptionValue%>> 				
											</span>
											<a href="#" 
											onMouseOver='document.getElementById("descriptionHelpDiv").style.display="block";'
											onMouseOut='document.getElementById("descriptionHelpDiv").style.display="none";'
											>
												&nbsp;&nbsp;&nbsp;<img src="/GloreeJava2/images/help.png" border="0">&nbsp;&nbsp;&nbsp;
											</a>
											<div id='descriptionHelpDiv' style='display:none'>
												<span class='normalText'><font color='red'>
													Use #OR# to apply more than one Description filter
													<br> Example : To search where Description has X or Y, use 'X #OR# Y'
													</font>
												</span>
											</div>
										</td>
									</tr></table>
								</div>
								
								<%if (ownerValue.equals("")){ %>
								<div id="ownerFilterDiv" style="display:none;">
								<%} else { %>
								<div id="ownerFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
									 <td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("ownerSearch").value= "";
										document.getElementById("ownerFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> Owner Email Id like</span>
									</td>
										<td>
											<span class='headingText'> 
											<input type="text"   name="ownerSearch" id="ownerSearch" size="50"
													 maxlength="100" <%=ownerValue%>> 				
											</span>
											<a href="#" 
											onMouseOver='document.getElementById("ownerHelpDiv").style.display="block";'
											onMouseOut='document.getElementById("ownerHelpDiv").style.display="none";'
											>
												&nbsp;&nbsp;&nbsp;<img src="/GloreeJava2/images/help.png" border="0">&nbsp;&nbsp;&nbsp;
											</a>
											<div id='ownerHelpDiv' style='display:none'>
												<span class='normalText'><font color='red'>
													Use #OR# to apply more than one Owner filter
													</font>
												</span>
											</div>
										</td>
									</tr></table>
								</div>
								
								<%if (externalURLValue.equals("")){ %>
								<div id="externalURLFilterDiv" style="display:none;">
								<%} else { %>
								<div id="externalURLFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
									<td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("externalURLSearch").value= "";
										document.getElementById("externalURLFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> External URL like</span>
									</td><td>
									<span class='headingText'> 
									<input type="text"  name="externalURLSearch" id="externalURLSearch" size="50"
											 maxlength="100" <%=externalURLValue%>> 
									</span>
									</td></tr></table>
								</div>
					
					
								
								<%if (pctCompleteValue.equals("")){ %>
								<div id="pctCompleteFilterDiv" style="display:none;">
								<%} else { %>
								<div id="pctCompleteFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
									<td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("pctCompleteSearch").value= "";
										document.getElementById("pctCompleteFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> Completed less than</span>
									</td><td>
									<span class='headingText'> 
									<input type="text"  name="pctCompleteSearch" id="pctCompleteSearch" size="3" maxlength="3" <%=pctCompleteValue%>> %
									</span>
									</td></tr></table>
								</div>
								
								
								<%if (
										(statusInApprovalWorkFlow.equals("")) 
										&&
										(statusApproved.equals(""))
										&&
										(statusRejected.equals(""))
									){ %>
								<div id="statusFilterDiv" style="display:none;">
								<%} else { %>
								<div id="statusFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
									<td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("statusSearch").selectedIndex= "-1";
										document.getElementById("statusFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									
									<span class='headingText'> Approval Status in (Ctrl+Click to select)</span>
									</td><td>
									<span class='headingText'> 
									<select MULTIPLE SIZE='3' name="statusSearch" id="statusSearch">
										<option value="Draft" <%=statusDraft%> >Draft</option>
										<option value="In Approval WorkFlow"    <%=statusInApprovalWorkFlow%> >In Approval WorkFlow</option>
										<option value="Approved"  <%=statusApproved%> >Approved</option>
										<option value="Rejected"  <%=statusRejected%> >Rejected</option>
									</select>
									</span>
									</td></tr></table>
								</div>
								
								
								<%if (
										(priorityHigh.equals("")) 
										&&
										(priorityMedium.equals(""))
										&&
										(priorityLow.equals(""))
									){ %>
								<div id="priorityFilterDiv" style="display:none;">
								<%} else { %>
								<div id="priorityFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
									 <td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("prioritySearch").selectedIndex= "-1";
										document.getElementById("priorityFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
	
									<span class='headingText'> Priority in (Ctrl+Click to select)</span>
									</td><td>
									<span class='headingText'> 
									<select MULTIPLE SIZE='3' name="prioritySearch" id="prioritySearch">
										<option value="High" <%=priorityHigh%>>High</option>
										<option value="Medium" <%=priorityMedium%>>Medium</option>
										<option value="Low" <%=priorityLow%> >Low </option>
									</select>
									</span>
									</td></tr></table>
								</div>
								
								<%if (
										(testingStatusPending.equals("")) 
										&&
										(testingStatusPass.equals(""))
										&&
										(testingStatusFail.equals(""))
									){ %>
								<div id="testingStatusFilterDiv" style="display:none;">
								<%} else { %>
								<div id="testingStatusFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
									<td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("testingStatusSearch").selectedIndex= "-1";
										document.getElementById("testingStatusFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>

									<span class='headingText'> Testing Status in (Ctrl+Click to select)</span>
									</td><td>
									<span class='headingText'> 
									<select MULTIPLE SIZE='3' name="testingStatusSearch" id="testingStatusSearch">
										<option value="Pending" <%=testingStatusPending%>>Pending</option>
										<option value="Pass" <%=testingStatusPass%>>Pass</option>
										<option value="Fail" <%=testingStatusFail%> >Fail</option>
									</select>
									</span>
									</td></tr></table>
								</div>
											
							
								<%if (approvedByValue.equals("")){ %>
									<div id="approvedByFilterDiv" style="display:none;">
									<%} else { %>
									<div id="approvedByFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
									<td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("approvedBySearch").value= "";
										document.getElementById("approvedByFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>

									<span class='headingText'> Approved By (Email Id)</span>
									</td><td>
									<span class='headingText'> 
									<input type="text"  name="approvedBySearch" id="approvedBySearch" size="50"
											 maxlength="100" <%=approvedByValue%>> 				
									</span>
									</td></tr></table>
								</div>

								<%if (rejectedByValue.equals("")){ %>
								<div id="rejectedByFilterDiv" style="display:none;">
								<%} else { %>
								<div id="rejectedByFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
									 <td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("rejectedBySearch").value= "";
										document.getElementById("rejectedByFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>

									<span class='headingText'> Rejected By (Email Id)</span>
									</td><td>
									<span class='headingText'> 
									<input type="text"  name="rejectedBySearch" id="rejectedBySearch" size="50"
											 maxlength="100" <%=rejectedByValue%>>
									</span>
									</td></tr></table> 			
								</div>
								
								<%if (pendingByValue.equals("")){ %>
								<div id="pendingByFilterDiv" style="display:none;">
								<%} else { %>
								<div id="pendingByFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
									 <td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("pendingBySearch").value= "";
										document.getElementById("pendingByFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>

									<span class='headingText'> Approval Pending By (Email Id)</span>
									</td><td>
									<span class='headingText'> 
									<input type="text"  name="pendingBySearch" id="pendingBySearch" size="50"
											 maxlength="100" <%=pendingByValue%>> 	
									</span>
									</td></tr></table>
								</div>
								
								<%if (traceToValue.equals("")){ %>
								<div id="traceToFilterDiv" style="display:none;">
								<%} else { %>
								<div id="traceToFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
									<td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("traceToSearch").value= "";
										document.getElementById("traceToFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>

									<span class='headingText'> Trace To (eg : BR-1)</span>
									</td><td>
									<span class='headingText'> 
									<input type="text"  name="traceToSearch" id="traceToSearch" size="50"
											 maxlength="100" <%=traceToValue%>> 	 				
									</span>
									<a href="#" 
									onMouseOver='document.getElementById("traceToHelpDiv").style.display="block";'
									onMouseOut='document.getElementById("traceToHelpDiv").style.display="none";'
									>
										&nbsp;&nbsp;&nbsp;<img src="/GloreeJava2/images/help.png" border="0">&nbsp;&nbsp;&nbsp;
									</a>
									<div id='traceToHelpDiv' style='display:none'>
										<span class='normalText'><font color='red'>
											Use comma separated list to apply more than one Trace To filter
											<br> Example : To search where object traces to BR-1 or BR-2 or BR-3, use 'BR-1,BR-2,BR-3'
											</font>
										</span>
									</div>
									</td></tr></table>
								</div>
								
								<%if (traceFromValue.equals("")){ %>
								<div id="traceFromFilterDiv" style="display:none;">
								<%} else { %>
								<div id="traceFromFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
									 <td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("traceFromSearch").value= "";
										document.getElementById("traceFromFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									
									<span class='headingText'> Trace From (eg : TR-1)</span>
									</td><td>
									<span class='headingText'> 
									<input type="text"  name="traceFromSearch" id="traceFromSearch" size="50"
											 maxlength="100" <%=traceFromValue %>>
									</span>
									<a href="#" 
									onMouseOver='document.getElementById("traceFromHelpDiv").style.display="block";'
									onMouseOut='document.getElementById("traceFromHelpDiv").style.display="none";'
									>
										&nbsp;&nbsp;&nbsp;<img src="/GloreeJava2/images/help.png" border="0">&nbsp;&nbsp;&nbsp;
									</a>
									<div id='traceFromHelpDiv' style='display:none'>
										<span class='normalText'><font color='red'>
											Use comma separated list to apply more than one Trace From filter
											<br> Example : To search where object traces from FR-1 or FR-2 or FR-3, use 'BR-1,BR-2,BR-3'
											</font>
										</span>
									</div>
									</td></tr></table> 	 			
								</div>
								
							</td>
							
						</tr>		
							
						
					<!--  Lets display the custom attributes for filtering. -->		
					<% 	 
				
					String attributeIdString = "";
					if (attributes != null) {
						
					%>
						<tr>
							<td>
							
					<%
						Iterator i = attributes.iterator();
						while (i.hasNext()){
							RTAttribute a = (RTAttribute) i.next();
							String divId = "customA" + a.getAttributeId() + "Div";
							if (a.getAttributeType().contains("Drop Down")){
								attributeIdString += "customA" + a.getAttributeId() + "#DropDown##";
								
								// lets iterate through the reportDefinition string, till we find an
								// attribute with the custom attributeId and then gets the previously
								// saved value for this attribute.
								String storedAttributeValue = "-1";
								if ((reportIdString != null) && (!(reportIdString.equals("")))){
									// lets get the values for all attributes.
									String[] values = reportDefinition.split(":###:");
							
									for (int k=0; k< values.length;k++) {
										// iterating through the attributes / values.
										String value = "";
										value = values[k];
										
										if (value.contains(":--:")){
											String [] b = value.split(":--:");
											String savedCustomAttributeId = b[0];
											if (savedCustomAttributeId.equals("customA" + a.getAttributeId())){
												// at this point b[0] has the value of the saved custom attribute.
												if (b.length > 1  ){
													storedAttributeValue = b[1]  ;
												}
												
											}
										}
									}
								}
								%>
									<%if (storedAttributeValue.equals("-1")){ %>
									<div id="<%=divId%>" style="display:none;">
									<%} else { %>
									<div id="<%=divId%>" style="display:block;">
									<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
									<td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("customA<%=a.getAttributeId()%>").selectedIndex= "-1";
										document.getElementById("<%=divId%>").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> <%=a.getAttributeName()%> <br>(Ctrl+Click to Select)</span> 
									</td>
									<td>
										<span class='headingText'> 
										<select MULTIPLE SIZE='3' name='<%=a.getAttributeId()%>'  id='customA<%=a.getAttributeId()%>'>
										<%
										
										int parentAttributeId = a.getParentAttributeId();
										String [] o = a.getAttributeDropDownOptions().split(",");
									
										for (int j=0 ; j < o.length; j++){
											String optionName = o[j];
											if (parentAttributeId > 0){
												// this is a child attribute and the attribute values are like Porsche:911,Porsche:Panamera,Porsche:Carrera
												// where Porsche is the parent attribute value and 911,Panamera and Carrera are the potential child values.
												// so we need to strip out the first portion .
												if ((optionName != null) && (optionName.contains(":"))){
													String [] oN = optionName.split(":");
													optionName = oN[1];
												}
											}
											if (
													(!storedAttributeValue.equals("-1")) && 
													(storedAttributeValue.contains(optionName))
												) {
												// storedAttributeValue is -1 if the attribute is a pull down and no values are selected.
												// however, if you have an attribute pull down, whose values are like 1,2,3,4 (eg : severity)
												// it was clashign with -1. So we put this clause above.
												// if there is a preselected value and this pre selected value is this current option,
												// then show it as selected. 
												// this attribute value is in the prev selected list for this attribute
												// so , lets make it selected.
											%>
												<option value='<%=optionName%>'  SELECTED><%=optionName%></option>
											<%
											}
											else{
											%>
												<option value='<%=optionName%>' ><%=optionName%></option>
											<%
											}
										}
										%>
										</select>
										</span>
									</td> </tr></table>
									</div>
								<%
							}
							else{
								attributeIdString += "customA" + a.getAttributeId() + "#Text##";
								// lets iterate through the reportDefinition string, till we find an
								// attribute with the custom attributeId and then gets the previously
								// saved value for this attribute.
								String storedAttributeValue = "";
								if ((reportIdString != null) && (!(reportIdString.equals("")))){
									Report report = new Report (Integer.parseInt(reportIdString));
									reportDefinition = report.getReportDefinition();
									// lets get the values for all attributes.
									String [] values = reportDefinition.split(":###:");
									
									for (int k=0; k< values.length;k++) {
										// iterating through the attributes / values.
										String value = "";
										value = values[k];

										if (value.contains("customA" + a.getAttributeId())) {
											String [] b = value.split(":--:");
											if (b.length > 1  ){
												storedAttributeValue = "value = '" + b[1] + "'" ;
											}
										}
									}
								}
								%>
									<%if (storedAttributeValue.equals("")){ %>
										<div id="<%=divId%>" style="display:none;">
										<%} else { %>
										<div id="<%=divId%>" style="display:block;">
									<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
									 <td width='330'>
									<a href="#" 
									onClick='
										document.getElementById("customA<%=a.getAttributeId()%>").value= "";
										document.getElementById("<%=divId%>").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> <%=a.getAttributeName()%></span> 
									</td>
									<td>
										<span class='headingText'> 
										
										 <%
										 
										 if (a.getAttributeType().equals("Number")){ %>
										 <input type="text"  
										 name="<%=a.getAttributeId()%>" id="customA<%=a.getAttributeId()%>" size="10"
										 maxlength="20" <%=storedAttributeValue%>>
										 	(Number)
										 	<a href="#" 
											onMouseOver='document.getElementById("attribute<%=a.getAttributeId()%>HelpDiv").style.display="block";'
											onMouseOut='document.getElementById("attribute<%=a.getAttributeId()%>HelpDiv").style.display="none";'
											>
												&nbsp;&nbsp;&nbsp;<img src="/GloreeJava2/images/help.png" border="0">&nbsp;&nbsp;&nbsp;
											</a>
											<div id='attribute<%=a.getAttributeId()%>HelpDiv' style='display:none'>
												<span class='normalText'><font color='red'>
													You can filter this Number type attribute by  >, < , or = 
													<br> Example : 
													<br>To search where attribute > 10, use '>10'
													<br>To search where attribute < 10.5, use '<10.5'
													<br>To search where attribute =35, use '=35'
													
													</font>
												</span>
											</div>
										 <%}
										 else {%>
											 <input type="text"  name="<%=a.getAttributeId()%>" id="customA<%=a.getAttributeId()%>" size="50"
											 maxlength="100" <%=storedAttributeValue%>>
											 <a href="#" 
												onMouseOver='document.getElementById("attrib<%=a.getAttributeId()%>HelpDiv").style.display="block";'
												onMouseOut='document.getElementById("attrib<%=a.getAttributeId()%>HelpDiv").style.display="none";'
												>
													&nbsp;&nbsp;&nbsp;<img src="/GloreeJava2/images/help.png" border="0">&nbsp;&nbsp;&nbsp;
												</a>
												<div id='attrib<%=a.getAttributeId()%>HelpDiv' style='display:none'>
													<span class='normalText'><font color='red'>
														To use wild card searchs, enclose text like this :  %searchtext%. 
														<br> Example : To search where <%=a.getAttributeName()%> contains "test", enter %test% in the search box'
														</font>
													</span>
												</div>
												
										 <%} %>
										 </span>
									</td></tr></table>
									</div>
								<%				
							}
								
						}
					}
						
					%>
					<input type='hidden' name='attributeIdString' value='<%=attributeIdString%>'>							
							</td>
						</tr>
						
						
						
						
						<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
						
							<td  align='left'>
								<span class='headingText'>
									
									<b>Step 2 (Optional) : In Release </b> 
									<select  id="inRelease">
							 		<option value='-1'> </option> 
									<%
										Iterator i = releases.iterator();
										while (i.hasNext()){
											Requirement requirement = (Requirement) i.next();
											String requirementName = requirement.getRequirementNameForHTML();
											if (requirementName.length() > 30) {
												requirementName = requirementName.substring(0,29);
											}
											
											if (inRelease.equals(Integer.toString((requirement.getRequirementId())))){
												%>
													<option SELECTED value='<%=requirement.getRequirementId()%>'>
														<%=requirementName%>
													</option>
												<%				
											}
											else {
												%>
													<option value='<%=requirement.getRequirementId()%>'>
														<%=requirementName%>
													</option>
												<%	
											}
									
										}
									%>
												
									</select>
								</span>
							</td>
						</tr>							
						
						<!--  lets display the 'Attribtue to Display' section -->
						<tr>
							<td  align='left'>
								<span class='headingText'>
									<br> 
									<b>Step 3 (Optional) : </b> Fields to Display
								</span>
							</td>
						</tr>							
						<tr>
							<td >
								<table class='paddedTable' >
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
										
										<td >
											<span class='normalText'> 
											<b>Standard Attributes </b><br>
											<select MULTIPLE SIZE='22' name="standardDisplay">
												<option value="description" <%=standardDisplayDescription%>>Description</option>
												<option value="comments" <%=standardDisplayComments%>>Comments</option>
												
												<option value="owner" <%=standardDisplayOwner%>>Owner</option>
												<option value=""></option>
												<option value="traceTo" <%=standardDisplayTraceTo%>>Trace To</option>
												<option value="traceFrom" <%=standardDisplayTraceFrom%>>Trace From</option>
												<option value=""></option>
												<option value="status" <%=standardDisplayStatus%>>Approval Status</option>
												<option value="approvedBy" <%=standardDisplayApprovedBy%>>Approved By</option>
												<option value="rejectedBy" <%=standardDisplayRejectedBy%>>Rejected By</option>
												<option value="pendingBy" <%=standardDisplayPendingBy%>>Pending By</option>
												<option value="dynamicRole"> Dynamic Approval Role</option>
												
												<option value=""></option>
												<option value="percentComplete" <%=standardDisplayPercentComplete%>>Percent Complete</option>
												<option value="testingStatus" <%=standardDisplayTestingStatus%>>Testing Status</option>
												<option value=""></option>
												<option value="folderPath" <%=standardDisplayFolderPath%>>Folder Path</option>
												<option value="lockedBy" <%=standardDisplayLockedBy%>>Locked By</option>
												<option value="externalURL" <%=standardDisplayExternalURL%>>External URL</option>
												<option value="priority" <%=standardDisplayPriority%>>Priority</option>
												<option value="baselines" <%=standardDisplayBaselines%>>Baselines</option>
												
												<option value="createdBy" <%=standardDisplayCreatedBy%>>Created By</option>
												<option value="createdDate" <%=standardDisplayCreatedDate%>>Created Date</option>
												<option value="lastModifiedBy" <%=standardDisplayLastModifiedBy%>>Last Modified By</option>
												<option value="lastModifiedDate" <%=standardDisplayLastModifiedDate%>>Last Modified Date</option>
												<option value="attachments" <%=standardDisplayAttachments%>>Attachments</option>
											</select>
											</span>
										</td>
										
										<td align='left'>
											<%if (attributes.size() > 0) { 
												Iterator o = attributes.iterator(); %>
											<span class='normalText'> 
											<b>Custom Attributes </b><br>
											<select MULTIPLE SIZE='22' name="customAttributesDisplay" id="customAttributesDisplay">
												<%
													while (o.hasNext()){
														RTAttribute  rTA = (RTAttribute) o.next();
														if (customAttributesDisplay.contains(rTA.getAttributeName())) {
												%>
														<option value="<%=rTA.getAttributeName()%>" SELECTED><%=rTA.getAttributeName()%></option>
												<%
														}
														else {
												%>
														<option value="<%=rTA.getAttributeName()%>"><%=rTA.getAttributeName()%></option>
												<%
														}
													}
												%>
											</select>
											</span>
											<%} %>
										</td>
										
									</tr>
									<tr>
										<td colspan='2'>
											<span class='normalText'>Use <i><b>Ctrl + Mouse Click</b></i> 
											to select / deselect values </span>
											<br>
										</td>
									</tr>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
										<td width='100'>
											<span class='normalText'><b>Sort By </b></span>
										</td>
										<td >
											<span class='normalText'> 
											<select name="sortBy" id="sortBy">
												<option value="tag">Requirement Id</option>
												<% if ((sortBy != null) && (sortBy.equals("name"))){%>
													<option value="name" SELECTED>Name</option>
												<% }else {%>
													<option value="name" >Name</option>												
												<%}%>

												<% if ((sortBy != null) && (sortBy.equals("description"))){%>
													<option value="description" SELECTED>Description</option>
												<% }else {%>
													<option value="description" >Description</option>												
												<%}%>
												
												<% if ((sortBy != null) && (sortBy.equals("owner"))){%>
													<option value="owner" SELECTED>Owner</option>
												<% }else {%>
													<option value="owner" >Owner</option>												
												<%}%>
												
												<% if ((sortBy != null) && (sortBy.equals("external_url"))){%>
													<option value="external_url" SELECTED>External URL</option>
												<% }else {%>
													<option value="external_url" >External URL</option>												
												<%}%>
												
													
												
												<% if ((sortBy != null) && (sortBy.equals("approval_status"))){%>
													<option value="approval_status" SELECTED>Approval Status</option>
												<% }else {%>
													<option value="approval_status" >Approval Status</option>												
												<%}%>												
												
												<% if ((sortBy != null) && (sortBy.equals("priority"))){%>
													<option value="priority" SELECTED>Priority</option>
												<% }else {%>
													<option value="priority" >Priority</option>												
												<%}%>												
												
												<% if ((sortBy != null) && (sortBy.equals("pct_complete"))){%>
													<option value="pct_complete" SELECTED>Percent Complete</option>
												<% }else {%>
													<option value="pct_complete" >Percent Complete</option>												
												<%}%>												
												
												<% if ((sortBy != null) && (sortBy.equals("testing_status"))){%>
													<option value="testing_status" SELECTED>Testing Status</option>
												<% }else {%>
													<option value="testing_status" >Testing Status</option>												
												<%}%>												
												
												
												<% if ((sortBy != null) && (sortBy.equals("folder_path"))){%>
													<option value="folder_path" SELECTED>Folder Path</option>
												<% }else {%>
													<option value="folder_path" >Folder Path</option>												
												<%}%>												
												
												<% if ((sortBy != null) && (sortBy.equals("created_by"))){%>
													<option value="created_by" SELECTED>Created By</option>
												<% }else {%>
													<option value="created_by" >Created By</option>												
												<%}%>												
												
												<% if ((sortBy != null) && (sortBy.equals("created_dt"))){%>
													<option value="created_dt" SELECTED>Created Date</option>
												<% }else {%>
													<option value="created_dt" >Created Date</option>												
												<%}%>												
												
												<% if ((sortBy != null) && (sortBy.equals("last_modified_by"))){%>
													<option value="last_modified_by" SELECTED>Last Modified  By</option>
												<% }else {%>
													<option value="last_modified_by" >Last Modified By</option>												
												<%}%>												
												
												<% if ((sortBy != null) && (sortBy.equals("last_modified_dt"))){%>
													<option value="last_modified_dt" SELECTED>Last Modified Date</option>
												<% }else {%>
													<option value="last_modified_dt" >Last Modified Date</option>												
												<%}%>												
												
												
												
													<option value="" >------------</option>												
												
												
												<%	Iterator o = attributes.iterator(); 
													while (o.hasNext()){
														RTAttribute  rTA = (RTAttribute) o.next();
														if ((sortBy != null) && (sortBy.equals("CustomAttribute" + rTA.getAttributeName()+":#:"))){
														%>
															<option value="CustomAttribute<%=rTA.getAttributeName()%>:#:" SELECTED><%=rTA.getAttributeName()%></option>
														<%
														}
														else {
														%>
															<option value="CustomAttribute<%=rTA.getAttributeName()%>:#:" ><%=rTA.getAttributeName()%></option>
														<%}
													}%>														
												
											</select>

											&nbsp;&nbsp;
											<select name="sortByType" id="sortByType">
												<% if ((sortByType != null) && (sortByType.equals("ascending"))){%>
													<option value="ascending" SELECTED>Ascending</option>
													<option value="descending" >Descending</option>
												<% }
												else if ((sortByType != null) && (sortByType.equals("descending"))) {%>
													<option value="ascending" >Ascending</option>
													<option value="descending" SELECTED>Descending</option>												
												<%} else { %>
													<option value="ascending" >Ascending</option>
													<option value="descending" >Descending</option>												
												<%} %>
											</select>
											</span>
										</td>
										
									</tr>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
										<td width='100'>
											<span class='normalText'><b> Rows Per Page </b></span>
										</td>
										<td  align='left' >
											<span class='normalText'> 
											<input type='text' name='rowsPerPage' id='rowsPerPage' value='<%=rowsPerPage%>' maxlength='4'  size='4'
											onchange='
											var rowsPerPageValue = document.getElementById("rowsPerPage").value ;
											
											if (rowsPerPageValue <= 500){
												document.getElementById("rowsPerPageMessageDiv").style.display="block";
												document.getElementById("rowsPerPageMessageDiv").innerHTML="";
											}
											if ((rowsPerPageValue > 500) && (rowsPerPageValue <= 1000)){
												document.getElementById("rowsPerPageMessageDiv").style.display="block";
												document.getElementById("rowsPerPageMessageDiv").innerHTML="<span class=normalText><b>Please note that a large page size can slow your browser down</b></span>";
											}
											if ((rowsPerPageValue > 1000) && (rowsPerPageValue <= 2000)){
												document.getElementById("rowsPerPageMessageDiv").style.display="block";
												document.getElementById("rowsPerPageMessageDiv").innerHTML="<span class=normalText><font color=red>Please note  that 500+ Reirements per Page can  REALLY slow your browser down. You may want to consider Excel download to manipulate your data.</font></span>";
											}
											if (rowsPerPageValue > 2000){
												document.getElementById("rowsPerPageMessageDiv").style.display="block";
												document.getElementById("rowsPerPageMessageDiv").innerHTML="<span class=normalText><font color=red>To manipulate such a large set of data, you may want to download it to Excel. Even if you set Rows Per Page to  100, your Excel download will have the entire data set</span>";
											}
											
											'></input>
											</span>
										</td>
									</tr>
									<tr>
										<td colspan='4'>
											<div id='rowsPerPageMessageDiv' style='display:none'>
											</div>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td  align='left'>
								<span class='headingText'>
									<br> 
									<b>Step 4 : </b> <input type='button' value='  Run Report  '  class='btn btn-sm btn-primary'
										onclick="reportAction(<%=dFFFolderId%>,'runReport','list');">
								</span>
							</td>
						</tr>						
					</table>
			 	</div>
			</td>
		</tr>									
	</table>
	
<%}%>