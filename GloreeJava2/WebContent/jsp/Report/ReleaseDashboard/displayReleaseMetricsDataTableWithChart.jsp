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

	String reCalculateMetrics = request.getParameter("reCalculateMetrics");
	if ((reCalculateMetrics != null) && (reCalculateMetrics.equals("yes")) ){
		// send a mail before calculating
		ArrayList toArrayList = new ArrayList();
		toArrayList.add(user.getEmailId());
		ArrayList ccArrayList = new ArrayList();
		String subject = "Starting Project Metrics Calculation";
		String message = "Hello <br><br> We have started metrics calculation <br> <br><b> Project Name </b> : " + project.getProjectName() + 
				"<br><br> Best Regards <br><br> TraceCloud Admin Team " ;
		MessagePacket mP = new MessagePacket(toArrayList, ccArrayList, subject, message, "");
			
		String mailHost = this.getServletContext().getInitParameter("mailHost");
		String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
		String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
		String smtpPort = this.getServletContext().getInitParameter("smtpPort");
		String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
		String emailUserId = this.getServletContext().getInitParameter("emailUserId");
		String emailPassword = this.getServletContext().getInitParameter("emailPassword");
		
		
		EmailUtil.email(mP, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
		
		MetricsUtil.captureProjectMetrics(project, databaseType);	
		// send a mail after calculating

		subject = "Finished  Project Metrics Calculation";
		 message = "Hello <br><br> We have completed metrics calculation <br><br><b> Project Name </b> : " + project.getProjectName() + 
				"<br><br> Best Regards <br><br> TraceCloud Admin Team " ;
		 mP = new MessagePacket(toArrayList, ccArrayList, subject, message, "");
		 EmailUtil.email(mP, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
			
	}

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
						 as of '<%=lastDataLoadDt%>' 
					<%} %>
					
					<input type="button" name="runMetrics" value=" Re Calculate Project Metrics " 
						onclick="
							document.getElementById('reCalculateProjectMetricsDiv<%=releaseId %>').style.display='block';
							refreshReleaseMetricsDataTableWithChart(<%=releaseId%>,'yes');
						"> 
					</span>
					<div id="reCalculateProjectMetricsDiv<%=releaseId %>" style="display:none;">
					<span class='normalText'>
					Working...<br><font style='color:red'>Please note this may take a few minutes.</font>
					</span></div>
				
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
								<select name='levelOfDetail<%=releaseId%>' id='levelOfDetail<%=releaseId%>'
								onChange='refreshReleaseMetricsDataTableWithChart(<%=releaseId%>,"no");'
								>
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
							</td>
							<td>
							</td>

							
							<td>
								
								
								
						    	<a href='/GloreeJava2/servlet/ReportAction?action=exportReleaseDashboardDataTableToExcel'
							     target='_blank'>
						    	<img src="/GloreeJava2/images/ExportExcel16.gif"  border="0"> Download to Excel </a>
			    		
							</td>
							
							
						</tr>
					</table>
				</div>
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
					    	
					    	
					    	// lets get the totals and build the chart.
							
					    	int numOfReqsInRel = 0;
					    	
					    	int numOfDraftReqsInRel = 0;
					    	int numOfInApprovalWorkFlowReqsInRel = 0;
					    	int numOfRejectedReqsInRel = 0 ;
					    	int numOfApprovedReqsInRel = 0 ;
					    	
					    	int numOfDanglingReqsInRel = 0;
					    	int numOfOrphanReqsInRel = 0;
					    	int numOfSuspectUpReqsInRel = 0;
					    	int numOfSuspectDownReqsInRel = 0;
					    	
					    	

					    	int numOfTestPendingReqsInRel = 0;
					    	int numOfTestPassReqsInRel = 0;
					    	int numOfTestFailReqsInRel = 0;
					    	
					    	int numOfCompletedReqsInRel = 0;
					    	int numOfIncompleteReqsInRel = 0;
					    	
					    	
					    	Iterator i = dataTable.iterator();
					    	
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
					    		
					    		// Lets try to get the totals for thsi release
					    		try {	numOfReqsInRel  += Integer.parseInt(numOfRequirements);  } catch (Exception e ){		}
					    		
					    		try {	numOfDraftReqsInRel  +=  Integer.parseInt(numOfDraftRequirements);  } catch (Exception e ){		}
					    		try {	numOfInApprovalWorkFlowReqsInRel  +=  Integer.parseInt(numOfInApprovalWorkflowRequirements);  } catch (Exception e ){		}
					    		try {	numOfRejectedReqsInRel  +=  Integer.parseInt(numOfRejectedRequirements);  } catch (Exception e ){		}
					    		try {	numOfApprovedReqsInRel  +=  Integer.parseInt(numOfApprovedRequirements);  } catch (Exception e ){		}
					    		
					    		try {	numOfDanglingReqsInRel  +=  Integer.parseInt(numOfDanglingRequirements);  } catch (Exception e ){		}
					    		try {	numOfOrphanReqsInRel  +=  Integer.parseInt(numOfOrphanRequirements);  } catch (Exception e ){		}
					    		try {	numOfSuspectUpReqsInRel  +=  Integer.parseInt(numOfSuspectUpstreamRequirements);  } catch (Exception e ){		}
					    		try {	numOfSuspectDownReqsInRel  +=  Integer.parseInt(numOfSuspectDownstreamRequirements);  } catch (Exception e ){		}
					    		
					    		try {	numOfTestPendingReqsInRel  +=  Integer.parseInt(numOfTestPendingRequirements);  } catch (Exception e ){		}
					    		try {	numOfTestPassReqsInRel  +=  Integer.parseInt(numOfTestPassRequirements);  } catch (Exception e ){		}
					    		try {	numOfTestFailReqsInRel  +=  Integer.parseInt(numOfTestFailRequirements);  } catch (Exception e ){		}
					    		
					    		try {	numOfCompletedReqsInRel  +=  Integer.parseInt(numOfCompletedRequirements);  } catch (Exception e ){		}
					    		try {	numOfIncompleteReqsInRel  +=  Integer.parseInt(numOfIncompleteRequirements);  } catch (Exception e ){		}
					    		
							}    	
					    	
					    	// now that we have the totals, lets print the chart.
						
							// lets build the url parameters for making a chart
				    		String chartURLParam = "total=" + numOfReqsInRel;

							chartURLParam += "&draft=" + numOfDraftReqsInRel;
				    		chartURLParam += "&pending=" + numOfInApprovalWorkFlowReqsInRel;
				    		chartURLParam += "&rejected=" + numOfRejectedReqsInRel;
				    		chartURLParam += "&approved=" + numOfApprovedReqsInRel;
				    		
				    		chartURLParam += "&dangling=" + numOfDanglingReqsInRel;
				    		chartURLParam += "&orphan=" + numOfOrphanReqsInRel;

				    		chartURLParam += "&suspectUp=" + numOfSuspectUpReqsInRel;
				    		chartURLParam += "&suspectDown=" + numOfSuspectDownReqsInRel;

				    		chartURLParam += "&testPending=" + numOfTestPendingReqsInRel;
				    		chartURLParam += "&testPass=" + numOfTestPassReqsInRel;
				    		chartURLParam += "&testFail=" + numOfTestFailReqsInRel;

				    		chartURLParam += "&completed=" + numOfCompletedReqsInRel;
				    		chartURLParam += "&incomplete=" + numOfIncompleteReqsInRel;
							%>
									
							<tr>
			    				<td colspan='16'>
			    				<div id="showReleaseMetricsChartDiv" style="display: block; ">
								<iframe src='/GloreeJava2/jsp/Report/ReleaseDashboard/displayReleaseMetricsChart.jsp?<%=chartURLParam%>' width='1000', height='215' ></iframe>
								</td>
							</tr>					    	
				    	
					    	<% 
					    	// Now lets print the details 
							i = dataTable.iterator();
					    	
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
											<td class='tableHeader' colspan='3' align='center' BGCOLOR="#E5EBFF">
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
											<td class='tableHeader' BGCOLOR="#E5EBFF"> 
												<span class='sectionHeadingText'>
												Pending
												</span>
											</td>
											<td class='tableHeader' BGCOLOR="#E5EBFF"> 
												<span class='sectionHeadingText'>
												Pass
												</span>
											</td>
											<td class='tableHeader' BGCOLOR="#E5EBFF"> 
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
					 				<td
										bgColor='white'   
							 			onmouseover="this.bgColor='#d3d3d3'" 
							 			onmouseout="this.bgColor='white'"
							 		>
										<span class='normalText'>
							 				<%if (levelOfDetail.equals("requirementType")){%>
												<%=requirementTypeShortName %>
											<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												<%=folderPath %>
											<%} %>
											
		 								</span>
							 		</td>
							 		
							 		
							 			 <%if (levelOfDetail.equals("requirementType")){%>
							 			 	<td
							 			 		title='Total objects of "<%=requirementTypeShortName%>" type '
												bgColor='white'   
									 			onmouseover="this.bgColor='#d3d3d3'" 
									 			onmouseout="this.bgColor='white'"
									 			style="cursor:pointer; " 
											 	onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "all","yes")' 		
									 		>
												<span class='normalText'>
									 			<%=numOfRequirements %>
								 				</span>
								 			</td>
										<%} %>
										<%if (levelOfDetail.equals("folders")){%>
											<td
												title='Total objects in Folder "<%=folderPath%>" '
												bgColor='white'   
									 			onmouseover="this.bgColor='#d3d3d3'" 
									 			onmouseout="this.bgColor='white'"
									 			style="cursor:pointer; " 
											 	onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>,"<%=requirementTypeShortName%>", "all","yes")' 		
									 		>
												<span class='normalText'>
									 			<%=numOfRequirements %>
									 			</span>
									 		</td>
									 	<%} %>
							 		


							 		<% if(focusOn.contains("Approval")){ %>
							 			<%if (displayApprovalMetrics) { %>
							 				 <%if (levelOfDetail.equals("requirementType")){%>
									 		 	<td
									 		 		title='DRAFT objects of "<%=requirementTypeShortName%>" type '
													bgColor='white'   
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='white'"
										 			style="cursor:pointer; " 
												 	onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "draft","yes")' 		
										 		>
										 			<span class='normalText'>
									 				<%=numOfDraftRequirements %>&nbsp;
									 				</span>
									 			</td>
											<%} %>
											
											
											
											<%if (levelOfDetail.equals("folders")){%>
												<td
													title='DRAFT objects in Folder "<%=folderPath%>" '
													bgColor='white'   
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='white'"
										 			style="cursor:pointer; " 
												 	onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>,"<%=requirementTypeShortName%>", "draft","yes")'  		
										 		>
										 			<span class='normalText'>
									 			 	<%=numOfDraftRequirements %>
									 				</span>
									 			</td>
									 				
											<%} %>
								 			</span>
								 		
								 		
							 				 <%if (levelOfDetail.equals("requirementType")){%>
											 	<td
											 		title='Objects PENDING APPROVAL of "<%=requirementTypeShortName%>" type '
													bgColor='white'   
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='white'"
										 			style="cursor:pointer; " 
												 	onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "pending","yes")'
										 		>
										 			<span class='normalText'>
														<%=numOfInApprovalWorkflowRequirements%>
									 				</span>
									 			</td>

											<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												<td
													title='Objects PENDING APPROVAL in Folder "<%=folderPath%>" '
													bgColor='white'   
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='white'"
										 			style="cursor:pointer; " 
												 	onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>,"<%=requirementTypeShortName%>", "pending","yes")' 
										 		>
										 			<span class='normalText'>
													<%=numOfInApprovalWorkflowRequirements%>
									 				</span>
									 			</td>
											<%} %>
									
										
												 <%if (levelOfDetail.equals("requirementType")){%>
												 	<td
												 		title='REJECTED objects of "<%=requirementTypeShortName%>" type '
														bgColor='pink'   
											 			onmouseover="this.bgColor='#d3d3d3'" 
											 			onmouseout="this.bgColor='pink'"
											 			style="cursor:pointer; " 
											 			onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "rejected","yes")'
											 		>
											 			<span class='normalText'>
														<%=numOfRejectedRequirements%>
											 			</span>
										 			</td>
												<%} %>
												<%if (levelOfDetail.equals("folders")){%>
													<td
														title='REJECTED objects in Folder "<%=folderPath%>" '
														bgColor='pink'   
											 			onmouseover="this.bgColor='#d3d3d3'" 
											 			onmouseout="this.bgColor='pink'"
											 			style="cursor:pointer; " 
											 			onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>, "<%=requirementTypeShortName%>", "rejected","yes")' 
											 		>
											 			<span class='normalText'>
														<%=numOfRejectedRequirements%>
										 				</span>
										 			</td>
												<%} %>
									 	
								 		
								 		
								 		 		 <%if (levelOfDetail.equals("requirementType")){%>
								 		 		 	<td 
								 		 		 		title='APPROVED objects of "<%=requirementTypeShortName%>" type '  
									 			 		bgColor='lightgreen'
											 			onmouseover="this.bgColor='#d3d3d3'" 
											 			onmouseout="this.bgColor='lightgreen'"
											 			style="cursor:pointer; " 
											 			onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "approved","yes")'
											 		>
											 			<span class='normalText'>
														<%=numOfApprovedRequirements%>

										 				</span>
										 			</td>
												<%} %>
												<%if (levelOfDetail.equals("folders")){%>
													<td   
														title='APPROVED objects in Folder "<%=folderPath%>" '
									 			 		bgColor='lightgreen'
											 			onmouseover="this.bgColor='#d3d3d3'" 
											 			onmouseout="this.bgColor='lightgreen'"
											 			style="cursor:pointer; " 
											 			onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>,"<%=requirementTypeShortName%>", "approved","yes")'
											 		>
											 			<span class='normalText'>
														<%=numOfApprovedRequirements%>

										 				</span>
										 			</td>
												<%} %>
									 	<%} 
									 	else {
									 		if (levelOfDetail.equals("requirementType")){
									 	%>
									 		<td
									 			title='Approval Workflow not enabled for  "<%=requirementTypeShortName%>" type '  
												bgColor='white'   
									 			onmouseover="this.bgColor='#d3d3d3'" 
									 			onmouseout="this.bgColor='white'"
									 		>
									 			<span class='normalText'>NA</span>
									 		</td>
									 		<td
									 			title='Approval Workflow not enabled for  "<%=requirementTypeShortName%>" type '  
												bgColor='white'   
									 			onmouseover="this.bgColor='#d3d3d3'" 
									 			onmouseout="this.bgColor='white'"
									 		>
									 			<span class='normalText'>NA</span>
									 		</td>
									 		<td
									 			title='Approval Workflow not enabled for  "<%=requirementTypeShortName%>" type '  
												bgColor='white'   
									 			onmouseover="this.bgColor='#d3d3d3'" 
									 			onmouseout="this.bgColor='white'"
									 		>
									 			<span class='normalText'>NA</span>
									 		</td>
									 		<td
									 			title='Approval Workflow not enabled for  "<%=requirementTypeShortName%>" type '  
												bgColor='white'   
									 			onmouseover="this.bgColor='#d3d3d3'" 
									 			onmouseout="this.bgColor='white'"
									 		>
									 			<span class='normalText'>NA</span>
									 		</td>
									 		
									 	<% 	}
									 		if (levelOfDetail.equals("folders")){
									 	%>
									 		<td
									 			title='Approval Workflow not enabled for  "<%=requirementTypeShortName%>" type '  
												bgColor='white'   
									 			onmouseover="this.bgColor='#d3d3d3'" 
									 			onmouseout="this.bgColor='white'"
									 		>
									 			<span class='normalText'>NA</span>
									 		</td>
									 		<td
									 			title='Approval Workflow not enabled for  "<%=requirementTypeShortName%>" type '  
												bgColor='white'   
									 			onmouseover="this.bgColor='#d3d3d3'" 
									 			onmouseout="this.bgColor='white'"
									 		>
									 			<span class='normalText'>NA</span>
									 		</td>
									 		<td
									 			title='Approval Workflow not enabled for  "<%=requirementTypeShortName%>" type '  
												bgColor='white'   
									 			onmouseover="this.bgColor='#d3d3d3'" 
									 			onmouseout="this.bgColor='white'"
									 		>
									 			<span class='normalText'>NA</span>
									 		</td>
									 		<td
									 			title='Approval Workflow not enabled for  "<%=requirementTypeShortName%>" type '  
												bgColor='white'   
									 			onmouseover="this.bgColor='#d3d3d3'" 
									 			onmouseout="this.bgColor='white'"
									 		>
									 			<span class='normalText'>NA</span>
									 		</td>
									 											 		
									 	<%
									 		}
									 	} %>
							 		<%} %>




									<% if(focusOn.contains("Traceability")){ %>
								 			 <%if (levelOfDetail.equals("requirementType")){%>
								 			 	<td
								 			 		title='DANGLING (No Downstream Actiity) objects of "<%=requirementTypeShortName%>" type '  
													bgColor='white'   
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='white'"
										 			style="cursor:pointer; " 
												 	onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "dangling","yes")'  		
										 		>
													<span class='normalText'>
										 			<%=numOfDanglingRequirements%>
									 				</span>
									 			</td>
								 			<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												<td
													title='DANGLING (No Downstream Actiity) objects in Folder "<%=folderPath%>" '
													bgColor='white'   
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='white'"
										 			style="cursor:pointer; " 
												 	onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>,"<%=requirementTypeShortName%>", "dangling","yes")'   		
										 		>
													<span class='normalText'>
										 			<%=numOfDanglingRequirements%>
									 				</span>
									 			</td>
											<%} %>
								 		
								 		
								 		
								 			 <%if (levelOfDetail.equals("requirementType")){%>
								 			 	<td
								 			 		title='ORPHAN (No Upstream Actiity) objects of "<%=requirementTypeShortName%>" type '  
													bgColor='white'   
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='white'"
										 			style="cursor:pointer; " 
												 	onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "orphan","yes")'    		
										 		>
													<span class='normalText'>
										 			<%=numOfOrphanRequirements%>
									 				</span>
									 			</td>
											<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												<td
													title='ORPHAN (No Upstream Actiity) objects in Folder "<%=folderPath%>" '
													bgColor='white'   
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='white'"
										 			style="cursor:pointer; " 
												 	onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>,"<%=requirementTypeShortName%>", "orphan","yes")'     		
										 		>
													<span class='normalText'>
										 			<%=numOfOrphanRequirements%>
									 				</span>
									 			</td>
											<%} %>
								 		
								 		
								 		
								 			 <%if (levelOfDetail.equals("requirementType")){%>
								 			 	<td   
								 			 		title='Objects with Suspect UpStream Activity of "<%=requirementTypeShortName%>" type '  
								 			 		bgColor='pink'
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='pink'"
										 			style="cursor:pointer; " 
										 			onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "suspectUpstream","yes")' 
										 		>
										 			<span class='normalText'>
													<%=numOfSuspectUpstreamRequirements %>
									 				</span>
									 			</td>
									 		<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												<td   
													title='Objects with Suspect UpStream Activity in Folder "<%=folderPath%>" '
								 			 		bgColor='pink'
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='pink'"
										 			style="cursor:pointer; " 
										 			onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>, "<%=requirementTypeShortName%>", "suspectUpstream","yes")' 
										 		>
										 			<span class='normalText'>
													<%=numOfSuspectUpstreamRequirements %>
									 				</span>
									 			</td>
											<%} %>
								 		
								 		
								 			 <%if (levelOfDetail.equals("requirementType")){%>
								 			 	<td   
								 			 		title='Objects with Suspect DownStream Activity of "<%=requirementTypeShortName%>" type '  
								 			 		bgColor='pink'
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='pink'"
										 			style="cursor:pointer; " 
										 			onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "suspectDownstream","yes")' 
										 		>
										 			<span class='normalText'>
													<%=numOfSuspectDownstreamRequirements %>
									 				</span>
									 			</td>
								 			 <%} %>
								 			 
								 			 
											<%if (levelOfDetail.equals("folders")){%>
												<td  
													title='Objects with Suspect DownStream Activity in Folder "<%=folderPath%>" ' 
								 			 		bgColor='pink'
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='pink'"
										 			style="cursor:pointer; " 
										 			onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>, "<%=requirementTypeShortName%>", "suspectDownstream","yes")' 
										 		>
										 			<span class='normalText'>
													<%=numOfSuspectDownstreamRequirements %>
									 				</span>
									 			</td>
											<%} %>
								 	<%} %>	
							 		
							
									
									<% if(focusOn.contains("Testing")){ %>
								 			 <%if (levelOfDetail.equals("requirementType")){%>
								 			 	<td
								 			 		title='Objects that are PENDING TESTING of "<%=requirementTypeShortName%>" type '  
													bgColor='white'   
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='white'"
										 			style="cursor:pointer; " 
												 	onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "pendingTesting","yes")'      		
										 		>
													<span class='normalText'>
										 			<%=numOfTestPendingRequirements %>
									 				</span>
									 			</td>
											<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												<td
													title='Objects that are PENDING TESTING in Folder "<%=folderPath%>" ' 
													bgColor='white'   
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='white'"
										 			style="cursor:pointer; " 
												 	onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>, "<%=requirementTypeShortName%>", "pendingTesting","yes")'       		
										 		>
													<%=numOfTestPendingRequirements %>
									 				</span>
									 			</td>
											<%} %>
								 		
								 		
								 		
								 			 <%if (levelOfDetail.equals("requirementType")){%>
								 			 	<td   
								 			 		title='TESTING PASSED objects of "<%=requirementTypeShortName%>" type '  
								 			 		bgColor='lightgreen'
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='lightgreen'"
										 			style="cursor:pointer; " 
										 			onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "passedTesting","yes")' 
										 		>
										 			<span class='normalText'>
													<%=numOfTestPassRequirements %>
													</span>
									 			</td>
											<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												<td  
													title='TESTING PASSED objects in Folder "<%=folderPath%>" '  
								 			 		bgColor='lightgreen'
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='lightgreen'"
										 			style="cursor:pointer; " 
										 			onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>, "<%=requirementTypeShortName%>", "passedTesting","yes")' 
										 		>
										 			<span class='normalText'>
													<%=numOfTestPassRequirements %>
									 				</span>
									 			</td>
											<%} %>
								 		 
								 		 
								 		 	 <%if (levelOfDetail.equals("requirementType")){%>
								 			 	<td   
								 			 		title='TESTING FAILED objects of "<%=requirementTypeShortName%>" type '  
								 			 		bgColor='pink'
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='pink'"
										 			style="cursor:pointer; " 
										 			onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "failedTesting","yes")' 
										 		>
										 			<span class='normalText'>
													<%=numOfTestFailRequirements %>
									 				</span>
									 			</td>
											<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												<td   
													title='TESTING FAILED objects in Folder "<%=folderPath%>" '  
								 			 		bgColor='pink'
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='pink'"
										 			style="cursor:pointer; " 
										 			onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>, "<%=requirementTypeShortName%>", "failedTesting","yes")' 
										 		>
										 			<span class='normalText'>
													<%=numOfTestFailRequirements %>
									 				</span>
									 			</td>
											<%} %>
								 									 					 				 		
								 	<%} %>	
							 		
							 		
							 		
									<% if(focusOn.contains("Completion")){ %>
											 <%if (levelOfDetail.equals("requirementType")){%>
											 	<td   
											 		title='COMPLETED objects of "<%=requirementTypeShortName%>" type '  
								 			 		bgColor='lightgreen'
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='lightgreen'"
										 			style="cursor:pointer; " 
										 			 onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "completed","yes")' 
										 		>
										 			<span class='normalText'>
													<%=numOfCompletedRequirements %>
									 				</span>
									 			</td>
											<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												<td   
													title='COMPLETED objects in Folder "<%=folderPath%>" '  
								 			 		bgColor='lightgreen'
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='lightgreen'"
										 			style="cursor:pointer; " 
										 			onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>, "<%=requirementTypeShortName%>", "completed","yes")'
										 		>
										 			<span class='normalText'>
													<%=numOfCompletedRequirements %>
										 			</span>
										 		</td>
											<%} %>
								 		
								 			 <%if (levelOfDetail.equals("requirementType")){%>
								 			 	<td  
								 			 		title='INCOMPLETE objects of "<%=requirementTypeShortName%>" type '   
								 			 		bgColor='pink'
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='pink'"
										 			style="cursor:pointer; " 
										 			onclick='displayReleaseRequirements(<%=releaseId%>,"<%=requirementTypeShortName%>", "incomplete","yes")'
										 		>
										 			<span class='normalText'>
													<%=numOfIncompleteRequirements%>
										 			</span>
										 		</td>
											<%} %>
											<%if (levelOfDetail.equals("folders")){%>
												<td
													title='INCOMPLETE objects in Folder "<%=folderPath%>" '  
													bgColor='pink'   
										 			onmouseover="this.bgColor='#d3d3d3'" 
										 			onmouseout="this.bgColor='pink'"
										 			style="cursor:pointer; " 
										 			onclick='displayReleaseRequirementsInFolder(<%=releaseId%>,<%=folderId %>, "<%=requirementTypeShortName%>", "incomplete","yes")' 
										 		>
										 			<span class='normalText'>
													<%=numOfIncompleteRequirements%>
									 				</span>
									 			</td>
									 			
											<%} %>							 		
								 						
									<%} %>		


				 				</tr>
					 <%
					    	}
					    	
					    %>
									<tr>
										
										<td class='tableHeader' width='350'>
											<span class='sectionHeadingText'>
											Totals
											</span>
										</td>
									
										<td class='tableHeader'>
											<span class='sectionHeadingText'>
											<%=numOfReqsInRel %> 
											</span>
										 </td>
									
										<% if(focusOn.contains("Approval")){ %>
											<td class='tableHeader' BGCOLOR="#E5EBFF"> 
												<span class='sectionHeadingText'>
												<%=numOfDraftReqsInRel %> 
												</span>
											</td>	
											<td class='tableHeader' BGCOLOR="#E5EBFF"> 
												<span class='sectionHeadingText'>
												<%=numOfInApprovalWorkFlowReqsInRel %>
												</span>
											</td>		
											<td class='tableHeader' BGCOLOR="#E5EBFF"> 
												<span class='sectionHeadingText'>
												<%=numOfRejectedReqsInRel %>
												</span>
											</td>		
											<td class='tableHeader' BGCOLOR="#E5EBFF"> 
												<span class='sectionHeadingText'>
												<%=numOfApprovedReqsInRel %>
												</span>
											</td>
										<%} %>
									
										<% if(focusOn.contains("Traceability")){ %>
											<td class='tableHeader' BGCOLOR="#E5FFEC"> 
												<span class='sectionHeadingText' >
												<%=numOfDanglingReqsInRel %>
												</span>
											</td>
											<td class='tableHeader' BGCOLOR="#E5FFEC"> 
												<span class='sectionHeadingText' >
												<%=numOfOrphanReqsInRel %>
												</span>
											</td>
													
											<td class='tableHeader' BGCOLOR="#E5FFEC"> 
												<span class='sectionHeadingText' >
												<%=numOfSuspectUpReqsInRel %>
												</span>
											</td>				
											<td class='tableHeader' BGCOLOR="#E5FFEC"> 
												<span class='sectionHeadingText' >
												<%=numOfSuspectDownReqsInRel %>
												</span>
											</td>
										<%} %>
										
										<% if(focusOn.contains("Testing")){ %>
											<td class='tableHeader' BGCOLOR="#E5EBFF"> 
												<span class='sectionHeadingText'>
												<%=numOfTestPendingReqsInRel %>
												</span>
											</td>
											<td class='tableHeader' BGCOLOR="#E5EBFF"> 
												<span class='sectionHeadingText'>
												<%=numOfTestPassReqsInRel %>
												</span>
											</td>
											<td class='tableHeader' BGCOLOR="#E5EBFF"> 
												<span class='sectionHeadingText'>
												<%=numOfTestFailReqsInRel %>
												</span>
											</td>
										<%} %>
									
										<% if(focusOn.contains("Completion")){ %>
											<td class='tableHeader' BGCOLOR="#CCCCFF"> 
												<span class='sectionHeadingText' >
												<%=numOfCompletedReqsInRel %> 
												</span>
											</td>
											<td class='tableHeader' BGCOLOR="#CCCCFF"> 
												<span class='sectionHeadingText' >
												<%=numOfIncompleteReqsInRel %>
												</span>
											</td>
										<%} %>																																																
									</tr>	

					</div>
					    <%
					    	
					    	
					    }
					%>
				
				</table>
				</div>
			</td>
		</tr>
	</table>
	</div>
<%}%>