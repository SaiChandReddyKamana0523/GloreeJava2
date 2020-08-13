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
	
	ArrayList<String> attributeNames = ReportUtil.getAllAttributesInAProject(dFFproject);
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dFFIsMember = false;
	if (dFFsecurityProfile.getRoles().contains("MemberInProject" + dFFproject.getProjectId())){
		dFFIsMember = true;
	}
	
	if (dFFIsMember){
		
		int  dFFFolderId = Integer.parseInt(request.getParameter("folderId"));
		Folder dFFFolder = new Folder(dFFFolderId);

		String reportDefinition = "";
		String reportInfo = "";
		String deleteReport = " <span class='normalText'> <font color='gray'>" + 
			" Delete </font>";
			
		String reportURLString  = "";
			
		// Note, this can be called for new reports (where you get only folderId)
		// or for existing reports (WHERE YOU GET BOTH FOLDERID and reportId).
		// we will use reportId in the 'displayTraceTreeReportFilter' section.
		
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
		
		String traceTreeDepth1 = "";
		String traceTreeDepth2 = "";
		String traceTreeDepth3 = "SELECTED";
		String traceTreeDepth4 = "";
		String traceTreeDepth5 = "";
		String traceTreeDepth6 = "";
		String traceTreeDepth7 = "";
		String traceTreeDepth8 = "";
		String traceTreeDepth9 = "";
		String traceTreeDepth10 = "";
		// these attributes are used to track whether this report (if saved)
		// had which attributes set to display.
		String standardDisplayDescription = "";
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
		String standardDisplayCustomAttributes = "";
		String standardDisplayFolderPath = "";
		String standardDisplayBaselines= "";
		String standardDisplayCreatedDate = "";
		String standardDisplayAttachments = "";
		
		String displayRequirementType = "all";

		int rowsPerPage = 500;

		String reportIdString = request.getParameter("reportId");
		// note reportIdString can be null if we did not get here from a saved report.
		session.setAttribute("traceTreeReportIdStringForFolder" + dFFFolderId , reportIdString);

		if ((reportIdString != null) && (!(reportIdString.equals("")))){
			// this means we are displaying an existing report (and not a new one)
			// hence there may be predefined values for this report.
			// we will get teh report definition from db, and try to 
			// reconstruct the filter here.
			Report report = new Report (Integer.parseInt(reportIdString));
			
			// lets the get traceTreeDepth definition for this report
			if (report.getTraceTreeDepth() == 1 ){
				// change the default.
				traceTreeDepth3 = "";
				traceTreeDepth1 = "SELECTED";
			}
			else if (report.getTraceTreeDepth() == 2 ){
				// change the default.
				traceTreeDepth3 = "";
				traceTreeDepth2 = "SELECTED";
			}
			else if (report.getTraceTreeDepth() == 3 ){
				traceTreeDepth3 = "SELECTED";
			} 
			else if (report.getTraceTreeDepth() == 4 ){
				// change the default.
				traceTreeDepth3 = "";traceTreeDepth4 = "SELECTED";
			} 
			else if (report.getTraceTreeDepth() == 5 ){
				// change the default.
				traceTreeDepth3 = "";
				traceTreeDepth5 = "SELECTED";
			} 
			else if (report.getTraceTreeDepth() == 6 ){
				// change the default.
				traceTreeDepth3 = "";
				traceTreeDepth6 = "SELECTED";
			} 
			else if (report.getTraceTreeDepth() == 7 ){
				// change the default.
				traceTreeDepth3 = "";
				traceTreeDepth7 = "SELECTED";
			} 
			else if (report.getTraceTreeDepth() == 8 ){
				// change the default.
				traceTreeDepth3 = "";
				traceTreeDepth8 = "SELECTED";
			} 
			else if (report.getTraceTreeDepth() == 9 ){
				// change the default.
				traceTreeDepth3 = "";
				traceTreeDepth9 = "SELECTED";
			} 
			else if (report.getTraceTreeDepth() == 10 ){
				// change the default.
				traceTreeDepth3 = "";
				traceTreeDepth10 = "SELECTED";
			} 
			else {
				// if the user is coming here for the first time to create a traceTreeReport
				// then we default it to 3.
				traceTreeDepth3 = "SELECTED";
			}

			// see if the user is either admin or is the creator of this report.
			// if so , they can remove this report.
			User user = dFFsecurityProfile.getUser();
			boolean dFFIsAdmin = false;
			if (dFFsecurityProfile.getRoles().contains("AdministratorInProject" + dFFproject.getProjectId())){
				dFFIsAdmin = true;
			}
			if ((dFFIsAdmin) || (report.getCreatedByEmailId().equals(user.getEmailId()))){
				deleteReport = "<a href='#' onClick='deleteReport(" 
						+ dFFFolderId + "," + report.getReportId() + ")'>" +
						" Delete <img src='/GloreeJava2/images/reportTraceTree.png' border='0'></a> ";	
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
				
				
				if (value.contains("displayRequirementType")) {
					
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						displayRequirementType =  a[1]  ;
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
				if ((value.contains("standardDisplay:--:") && (value.contains("customAttributes")))) {
					standardDisplayCustomAttributes = "SELECTED";
				}
				if ((value.contains("standardDisplay:--:") && (value.contains("folderPath")))) {
					standardDisplayFolderPath = "SELECTED";
				}
				if ((value.contains("standardDisplay:--:") && (value.contains("baselines")))) {
					standardDisplayBaselines = "SELECTED";
				}
				if ((value.contains("standardDisplay:--:") && (value.contains("createdDate")))) {
					standardDisplayCreatedDate = "SELECTED";
				}
				if ((value.contains("standardDisplay:--:") && (value.contains("attachments")))) {
					standardDisplayAttachments = "SELECTED";
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
				
				
			}

		}
		
%>
	<table class='paddedTable'  width='100%' >
		<tr>
			<td colspan='3' align='left' valign='middle' bgcolor='#99CCFF'>		
				<div style='float:left'>				
					<span class='subSectionHeadingText'>
					Trace Tree Report
					</span>
				</div>
				<div style='float:right'>
					<span title='Trace Tree Report Help Video'>
					<a target="_blank" href="http://www.youtube.com/watch?v=ArYbGkgELM0">
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
						<input type='button' value='Modify Report' class='btn btn-sm btn-primary'
							onclick="
								document.getElementById('showFilterLinkDiv').style.display = 'none';
								document.getElementById('filterDetailsDiv').style.display = 'block';
								document.getElementById('closeFilterDiv').style.display = 'block';
								document.getElementById('openFilterDiv').style.display = 'none';
								"
						> 
						
						&nbsp;&nbsp;
						<input type='button' value='  Refresh Report  '  style='height:25px' class='btn btn-sm btn-primary'
													onClick="
														reportAction(<%=dFFFolderId%>,'runReport','traceTree');">
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
									onClick="reportAction(<%=dFFFolderId%>,'runReport','traceTree');"
								>
									
								&nbsp;&nbsp;&nbsp;&nbsp;
								<input type='button' value='  Cancel  '  
									style='height:25px' class='btn btn-sm btn-danger'
									onClick="
										document.getElementById('displayFilterRSButton').style.display='block';
										document.getElementById('filterCondition').value = '';
										document.getElementById('filterResultsDiv').style.display='none';
										reportAction(<%=dFFFolderId%>,'runReport','traceTree');
								
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
				
								
				<div id='filterDetailsDiv'>
					<table width='100%'>

						<tr>
							<td  align='left' >
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
										onClick="
											reportAction(<%=dFFFolderId%>,'runReport','traceTree')
											">
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






						<tr>
							<td align="left">
								<div id ='saveReportResultDiv' class='alert alert-success' style="display:none;"></div>
								<div id ='saveReportDiv' class='alert alert-success' style="display:none;">
								<table>
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
										<td><input type="text"  name="reportName" id="reportName" size="25"
											 maxlength="100"> </td>
									</tr> 
									<tr>
										<td><span class='headingText'>Report Description </span></td>
										<td>
										<textarea name="reportDescription" id="reportDesciption" rows="3" cols="25"></textarea> 
										</td>
									</tr>
									<tr>
										<td>
											<a href='#' onClick="reportAction(<%=dFFFolderId%>,'saveReport','traceTree')"> Save Report </a>
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
							  <table>
							  	<tr>
							  		<td style='width:315px'>
							  			<span class='headingText' style='width:315px'><b>Step 1 (Optional) :</b> Apply Filters</span>
									</td>
							  		<td>
							  			<span class='normalText'>  
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
							  </table>
									
							</td>
						</tr>

						<tr>
							<td >
								
												
								<%if (includeSubFoldersValue.equals("")){ %>
								<div id="includeSubFoldersFilterDiv" style="display:none;">
								<%} else { %>
								<div id="includeSubFoldersFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
									<td width='230'>
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
									 <td width='230'>
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
									 <td width='230'>
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
									 <td width='230'>
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
									<td width='230'>
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
								 <td width='230'>
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
									<td width='230'>
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
									<td width='230'>
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
									<td width='230'>
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
									<td width='230'>
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
									<td width='230'>
									<a href="#" 
									onClick='
										document.getElementById("nameSearch").value= "";
										document.getElementById("nameFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> 
									Name like
									</span>
									</td><td>
									<span class='headingText'> 
									<input type="text"  name="nameSearch" id="nameSearch" size="50"
								 			maxlength="100" <%=nameValue%>>
									</span>
									</td></tr></table>
								</div>

								<%if (descriptionValue.equals("")){ %>
								<div id="descriptionFilterDiv" style="display:none;">
								<%} else { %>
								<div id="descriptionFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
									<td width='230'>
									<a href="#" 
									onClick='
										document.getElementById("descriptionSearch").value= "";
										document.getElementById("descriptionFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> 
									Description like
									</span>
									</td><td>
									<span class='headingText'> 
									<input type="text"  name="descriptionSearch" id="descriptionSearch" size="50"
											 maxlength="100" <%=descriptionValue%>> 				
									</span>
									</td></tr></table>
								</div>
								
								<%if (ownerValue.equals("")){ %>
								<div id="ownerFilterDiv" style="display:none;">
								<%} else { %>
								<div id="ownerFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
									<td width='230'>
									<a href="#" 
									onClick='
										document.getElementById("ownerSearch").value= "";
										document.getElementById("ownerFilterDiv").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> Owner Email Id like</span>
									</td><td>
									<span class='headingText'> 
									<input type="text"   name="ownerSearch" id="ownerSearch" size="50"
											 maxlength="100" <%=ownerValue%>> 				
									</span>
									</td></tr></table>
								</div>
								
								<%if (externalURLValue.equals("")){ %>
								<div id="externalURLFilterDiv" style="display:none;">
								<%} else { %>
								<div id="externalURLFilterDiv" style="display:block;">
								<%} %>
									<table>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
									<td width='230'>
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
									<td width='230'>
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
									<td width='230'>
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
									<td width='230'>
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
									<td width='230'>
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
									<td width='230'>
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
									<td width='230'>
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
									<td width='230'>
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
									<td width='230'>
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
									<td width='230'>
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
							if (a.getAttributeType().equals("Drop Down")){
								attributeIdString += "customA" + a.getAttributeId() + "#DropDown##";
							
								// lets iterate through the reportDefinition string, till we find an
								// attribute with the custom attributeId and then gets the previously
								// saved value for this attribute.
								String storedAttributeValue = "-1";
								if ((reportIdString != null) && (!(reportIdString.equals("")))){
									Report report = new Report (Integer.parseInt(reportIdString));
									reportDefinition = report.getReportDefinition();
									// lets get the values for all attributes.
									String [] values = reportDefinition.split(":###:");
							
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
										<td width='230'>
										<a href="#" 
										onClick='
											document.getElementById("customA<%=a.getAttributeId()%>").selectedIndex= "-1";
											document.getElementById("<%=divId%>").style.display="none";
											'>
										<img src="/GloreeJava2/images/delete16.png" border="0"></a>
										<span class='headingText'> <%=a.getAttributeName()%> <br>(Ctrl+Click to Select)</span> 
										</td>
										<td>
										<span class='normalText'> 
										<select MULTIPLE SIZE='3' name='<%=a.getAttributeId()%>'  id='customA<%=a.getAttributeId()%>'>
										<% 
										String [] o = a.getAttributeDropDownOptions().split(",");
										for (int j=0 ; j < o.length; j++){
											if (
													(!storedAttributeValue.equals("-1")) && 
													(storedAttributeValue.contains(o[j]))
												) {
												// storedAttributeValue is -1 if the attribute is a pull down and no values are selected.
												// however, if you have an attribute pull down, whose values are like 1,2,3,4 (eg : severity)
												// it was clashign with -1. So we put this clause above.
												// if there is a preselected value and this pre selected value is this current option,
												// then show it as selected. 
												// this attribute value is in the prev selected list for this attribute
												// so , lets make it selected.											
											%>
												<option value='<%=o[j]%>'  SELECTED><%=o[j]%></option>
											<%
											}
											else{
											%>
												<option value='<%=o[j]%>' ><%=o[j]%></option>
											<%
											}
										}
										%>
										</select>
										</span>
									</td></tr></table>
									</div>	
								<%
							}
							else{
								attributeIdString +=  "customA" + a.getAttributeId() + "#Text##";
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
									<td width='230'>
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
										<input type="text"  name="<%=a.getAttributeId()%>" id="customA<%=a.getAttributeId()%>" size="50"
										 maxlength="100" <%=storedAttributeValue%>>
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
						
						

						<!--  lets display the 'Attribtue to Display' section -->
						
						<tr>
							<td >
								<table class='paddedTable' width='100%'>
									<tr id='displayRequirementTypeRow' onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
						
										<td style='width:315px'>
											<span class='normalText'>
											<b>Step 2  (Optional) : </b> Select Object Types to display
											<br><br>
											Note : To bulk edit the objects in a Trace Tree, select a specific object type here. Otherwise, ignore this field
											<br>
											</span>
										</td>
										<td  >
											<span class='normalText'> 
											<select multiple size=10 name="displayRequirementType" id="displayRequirementType">
												<% if (displayRequirementType.equals("all")){ %>
													<option value="all" SELECTED>All</option>
												<%}
												else {%>
													<option value="all" >All</option>
												<%} 
												ArrayList requirementTypes = dFFproject.getMyRequirementTypes();
											
									
												Iterator rTs = requirementTypes.iterator();
												while (rTs.hasNext()){
													RequirementType rT  = (RequirementType) rTs.next();
													
													if (displayRequirementType.equals(rT.getRequirementTypeShortName())){ %>
														<option value='<%=rT.getRequirementTypeId() %>' SELECTED> Only <%=rT.getRequirementTypeName() %></option>
													<%}
													else {
													%>
														<option value='<%=rT.getRequirementTypeId() %>'> Only <%=rT.getRequirementTypeName() %></option>
													<%} 
												}
												SortedMap<String,Integer> externalReqTypes = dFFproject.getExternalReqTypes();
												for (String rTString : externalReqTypes.keySet()){
													
													Integer rTId = externalReqTypes.get(rTString);
													if (displayRequirementType.equals(rTString)){ %>
														<option value='<%=rTId %>' SELECTED> Only <%=rTString %></option>
													<%}
													else {
													%>
														<option value='<%=rTId %>'> Only <%=rTString%></option>
													<%} 
												}
												
												
												%>
											</select>											
											</span>
										</td>
									</tr>								
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
						
										<td style='width:315px'>
											<span class='normalText'>
											<b>Step 3 (Optional) : </b> Select Attributes to display 
											<br>
											<font color='red'>(Control+Click to Select / Deselect)</font> 
											</span>
										</td>
										<td >
											<span class='normalText'> 
											<select MULTIPLE SIZE='23' name="standardDisplay">
												<option value="description" <%=standardDisplayDescription%>>Description</option>
												<option value="owner" <%=standardDisplayOwner%>>Owner</option>
												<option value=""></option>
												<option value="traceTo" <%=standardDisplayTraceTo%>>Trace To</option>
												<option value="traceFrom" <%=standardDisplayTraceFrom%>>Trace From</option>
												<option value=""></option>
												<option value="status" <%=standardDisplayStatus%>>Approval Status</option>
												<option value="approvedBy" <%=standardDisplayApprovedBy%>>Approved By</option>
												<option value="rejectedBy" <%=standardDisplayRejectedBy%>>Rejected By</option>
												<option value="pendingBy" <%=standardDisplayPendingBy%>>Pending By</option>
												<option value=""></option>
												<option value="percentComplete" <%=standardDisplayPercentComplete%>>Percent Complete</option>
												<option value="testingStatus" <%=standardDisplayTestingStatus%>>Testing Status</option>
												<option value=""></option>
												<option value="folderPath" <%=standardDisplayFolderPath%>>Folder Path</option>
												<option value="lockedBy" <%=standardDisplayLockedBy%>>Locked By</option>
												<option value="externalURL" <%=standardDisplayExternalURL%>>External URL</option>
												<option value="priority" <%=standardDisplayPriority%>>Priority</option>
												<option value="baselines" <%=standardDisplayBaselines%>>Baselines</option>
												<option value="createdDate" <%=standardDisplayCreatedDate%>>Created Date</option>
												<option value="attachments" <%=standardDisplayAttachments%>>Attachments</option>
												<option value=""></option>
												
												<%
												for (String attributeName : attributeNames){
												%>
													<option value="customAttributes_<%=attributeName%>"  ><%=attributeName%></option>
												<%
												}
												%>
											</select>
											
											</span>
										</td>
									</tr>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
						
										<td style='width:315px'>
											<span class='normalText'>
											<b>Step 4 : </b> How deep should the Trace Tree go ?
											</span>
										</td>										
										<td align='left'>
											<select name="traceTreeDepth">
												<option value="1" <%=traceTreeDepth1 %>>1</option>
												<option value="2" <%=traceTreeDepth2 %> >2</option>
												<option value="3" <%=traceTreeDepth3 %> >3</option>
												<option value="4" <%=traceTreeDepth4 %>>4</option>
												<option value="5" <%=traceTreeDepth5 %>>5</option>
												<option value="6" <%=traceTreeDepth6 %>>6</option>
												<option value="7" <%=traceTreeDepth7 %>>7</option>
												<option value="8" <%=traceTreeDepth8 %>>8</option>
												<option value="9" <%=traceTreeDepth9 %>>9</option>
												<option value="10" <%=traceTreeDepth10 %>>10</option>
												
												
											</select>																							
										</td>
									</tr>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';"> 
						
										<td style='width:315px'>
											<span class='normalText'>
											<b>Step 5  : </b> How many rows per page?
											</span>
										</td>
										<td >
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
											<div id='rowsPerPageMessageDiv' style='display:none'></div>
										</td>
									</tr>
									<tr onmouseover="this.style.background='#E5EBFF';" onmouseout="this.style.background='white';">
										<td  width='315px'>
											<span class='headingText'>
												<b>Step 6 : </b> 
											</span>
										</td>
										<td  align='left'>
											<span class='headingText'>
												<input type='button' value='  Run Report  '  style='height:25px'  class='btn btn-sm btn-primary' 
													onClick="
														reportAction(<%=dFFFolderId%>,'runReport','traceTree');">
													
											</span>
										</td>
						</tr>						
									
								</table>
							</td>
						</tr>
					</table>
			 	</div>
			</td>
		</tr>									
	</table>
	
<%}%>