<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>


<%
	// authentication only
	String dTIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dTIsLoggedIn == null) || (dTIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String dTDatabaseType = this.getServletContext().getInitParameter("databaseType");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dTIsMember = false;
	Project dTProject= (Project) session.getAttribute("project");
	SecurityProfile dTSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dTSecurityProfile.getRoles().contains("MemberInProject" + dTProject.getProjectId())){
		dTIsMember = true;
	}
	
	User user = dTSecurityProfile.getUser();
%>

<%if (dTIsMember){ %>


	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	
	<%
	// we used releaseId == -1 to indicate user level number crunching when we call releaseMetricsUtil routines.
	int releaseId = -1;
	
	
	String lastDataLoadDt =  ReleaseMetricsUtil.getLastDataLoadDtForReleaseOrProject(
			releaseId,dTProject.getProjectId(), dTDatabaseType);

	// lets get the list of distinct req types in this release tree.	
	ArrayList dataTable = ReleaseMetricsUtil.getReleaseDataTableArrayForReleaseOrProject(
			releaseId,dTProject.getProjectId(), user, dTDatabaseType);
	
	%>
	 
	
	<div id = 'displayListReportDiv' class='level1Box'>
	<table class='paddedTable' width='100%'>
		<tr>
			<td  align="left">		
				<a name="TopOfMetrics"></a>		
				<span class="normalText">
					<b><%=user.getFirstName()%> <%=user.getLastName()%>'s Dashboard</b>
					
				</span>
			</td>
					
			<td   style='align:left'>
				<div id='menuExpandDiv' style='display:none;'>
					<img src="/GloreeJava2/images/return.jpg" width="16" border="0">
					&nbsp;
					<a href='#' onClick='
						var reportDataDiv = document.getElementById("reportData");
						if (reportDataDiv != null ){
							reportDataDiv.style.display="none";
						}
						var displayUserMetricsDiv = document.getElementById("displayUserMetricsDiv");
						if (displayUserMetricsDiv != null ){
							displayUserMetricsDiv.style.display="block";
						}
						var menuExpandDiv = document.getElementById("menuExpandDiv");
						if (menuExpandDiv != null ){
							menuExpandDiv.style.display="none";
						}
						document.getElementById("contentCenterF").style.display="none";
						
						'>Return to My Dashboard</a>
				</div>
			</td>
			<td   align='right '>
				&nbsp;	
			</td>			
		</tr>
		<tr>
			<td colspan=3>
				<div id ='reportData' class='level2Box' style='display:none'>
				<table id = "Report">				
	
					<%
					    if (dataTable != null){
					    	if (dataTable.size() ==0){
				   	%>
						    		<tr>
						    			<td colspan='7'>
						    				&nbsp;
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
								
					    		if (dataRow.length > 0) {
									dataLoadDt = dataRow[0];
					    		}
					    		if (dataRow.length > 1) {
									requirementTypeShortName = dataRow[1];
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
	
					    		// lets get the requirementtype object, so we can determine if approval work flow is enabled or not
					    		RequirementType requirementType = new RequirementType(dTProject.getProjectId(),requirementTypeShortName,user.getEmailId());
					    		boolean displayApprovalMetrics = false;
					    		if (requirementType.getRequirementTypeEnableApproval() == 1) {
					    			displayApprovalMetrics = true;
					    		}
					    		
					    		j++;
					    		// for the first row, print the header and user defined columns etc..
					    		if (j == 1){
					 %>
									<tr>
										<td class='tableHeader' colspan='2'>&nbsp;</td>
										<td class='tableHeader' colspan='4' align='center' BGCOLOR="#E5EBFF">
											<span class='sectionHeadingText'>
											Approval
											</span>
										</td>
										<td class='tableHeader' colspan='4' align='center' BGCOLOR="#E5FFEC">
											<span class='sectionHeadingText'>
											Traceability
											</span>
										</td>
										<td class='tableHeader' colspan='3' align='center' BGCOLOR="#E5EBFF">
											<span class='sectionHeadingText'>
											Testing
											</span>
										</td>
										<td class='tableHeader' colspan='2' align='center' BGCOLOR="#E5FFEC">
											<span class='sectionHeadingText'>
											Completion
											</span>
										</td>
									</tr>					 
									
									<tr>
										<td class='tableHeader' width='350'>
											<span class='sectionHeadingText'>
											Requirement Type
											</span>
										</td>
										<td class='tableHeader'>
											<span class='sectionHeadingText'>
											All
											</span>
										 </td>
									
									
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
										
										<td class='tableHeader' BGCOLOR="#E5FFEC"> 
											<span class='sectionHeadingText' >
											Completed 
											</span>
										</td>
										<td class='tableHeader' BGCOLOR="#E5FFEC"> 
											<span class='sectionHeadingText' >
											Incomplete
											</span>
										</td>
																																																											
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
							 			<%=requirementTypeShortName %>
		 								</span>
							 		</td>
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<a href='#' onclick='displayUserRequirements("<%=requirementTypeShortName%>", "all")' >
							 			 <%=numOfRequirements%></a>
							 			</span>
							 		</td>
							 		
							 		
							 		
							 		
							 		<td class='<%=cellStyle%>'>
							 			<%if (displayApprovalMetrics){ %>
								 			<a href='#' onclick='displayUserRequirements("<%=requirementTypeShortName%>", "draft")' >
								 			<%=numOfDraftRequirements %>
								 			</a>
							 			<%}%>
							 			
							 		</td>
							 		<td class='<%=cellStyle%>'>
							 			<%if (displayApprovalMetrics){ %>
								 			<a href='#' onclick='displayUserRequirements("<%=requirementTypeShortName%>", "pending")' >
							 				<%=numOfInApprovalWorkflowRequirements %>
							 				</a>
							 			<%}%>
							 			
							 		</td>	
									<td class='<%=cellStyle%>'>
							 			<%if (displayApprovalMetrics){ %>
									 		<a href='#' onclick='displayUserRequirements("<%=requirementTypeShortName%>", "rejected")' >
								 			<%=numOfRejectedRequirements %>
								 			</a>
							 			<%}%>
							 			
							 		</td>
							 		<td class='<%=cellStyle%>'>
							 			<%if (displayApprovalMetrics){ %>
										 	<a href='#' onclick='displayUserRequirements("<%=requirementTypeShortName%>", "approved")' >
								 			<%=numOfApprovedRequirements %>
								 			</a>
							 			<%}%>
							 			
							 		</td>		
									
							 		
							 		
							 		
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<a href='#' onclick='displayUserRequirements("<%=requirementTypeShortName%>", "dangling")' >
							 			<%=numOfDanglingRequirements %>
							 			</a>
							 			</span>
							 		</td>
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<a href='#' onclick='displayUserRequirements("<%=requirementTypeShortName%>", "orphan")' >
							 			<%=numOfOrphanRequirements %>
							 			</a>
							 			</span>
							 		</td>
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<a href='#' onclick='displayUserRequirements("<%=requirementTypeShortName%>", "suspectUpstream")' >
							 			<%=numOfSuspectUpstreamRequirements %>
							 			</a>
							 			</span>
							 		</td>
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<a href='#' onclick='displayUserRequirements("<%=requirementTypeShortName%>", "suspectDownstream")' >
							 			<%=numOfSuspectDownstreamRequirements %>
							 			</a>
							 			</span>
							 		</td>							 		
				 		
							 		
							 		
							 		
							 		
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<a href='#' onclick='displayUserRequirements("<%=requirementTypeShortName%>", "pendingTesting")' >
							 			<%=numOfTestPendingRequirements %>
							 			</a>
							 			</span>
							 		</td>					
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<a href='#' onclick='displayUserRequirements("<%=requirementTypeShortName%>", "passedTesting")' >
							 			<%=numOfTestPassRequirements %>
							 			</a>
							 			</span>
							 		</td>				
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<a href='#' onclick='displayUserRequirements("<%=requirementTypeShortName%>", "failedTesting")' >
							 			<%=numOfTestFailRequirements %>
							 			</a>
							 			</span>
							 		</td>							 		
							 		
							 		
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<a href='#' onclick='displayUserRequirements("<%=requirementTypeShortName%>", "completed")' >
							 			<%=numOfCompletedRequirements %>
							 			</a>
							 			</span>
							 		</td>				
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<a href='#' onclick='displayUserRequirements("<%=requirementTypeShortName%>", "incomplete")' >
							 			<%=numOfIncompleteRequirements %>
							 			</a>
							 			</span>
							 		</td>					
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		<div id = 'displayUserMetricsDiv' class='level1Box'>
		<input type='hidden' name='action' value = ''>	

		<table class='paddedTable' width='100%'>
			
			
		
			<tr>
				<td valign='top'>
					<table>
						
						<tr>
							<td colspan='2'>
								<span class='sectionHeadingText'>
								General Reports
								</span>
							</td>
						</tr>						

						
						<tr>
							<td colspan='2'>
								<span class='headingText'>
									<a href='#' 
										onclick="
											document.getElementById('reportData').style.display='block';
											document.getElementById('displayUserMetricsDiv').style.display='none';
											document.getElementById('menuExpandDiv').style.display='block';
											
									">
									<img src="/GloreeJava2/images/chart_bar16.png" border="0">
									<img src="/GloreeJava2/images/chart_pie16.png" border="0">						
									My Trends
									</a>
								</span>
							</td>
						</tr>						
						<!-- 
													<tr>
														<td colspan='2'>
															<span class='headingText'>
																<a href='#' onclick='displayUserRequirementsOfAllReqTypes("all");'>
																<img src="/GloreeJava2/images/report16.png" border="0">									
																My Reqs
																</a>
															</span>
														</td>
													</tr>
						-->

						 <tr>						
							<td colspan='2'>
								<span class='headingText'>
									<a href='#' 
									onclick='
									var cutOffDateObject = document.getElementById("cutOffDate");
										if (!isValidDate(cutOffDateObject.value)) {
											cutOffDateObject.focus();
											cutOffDateObject.style.backgroundColor="#FFCC99";
										}
										else {										
											displayUserRequirementsOfAllReqTypes("changedAfter");
										}
									'>
									<img src="/GloreeJava2/images/report16.png" border="0">
									My  Reqs Changed After
									</a>  
									<input type='text' name='cutOffDate' id='cutOffDate' size='8' value='mm/dd/yyyy'>
									
								</span>						
							</td>
						</tr>
						<!-- 
													<tr>
														<td colspan='2'>
															<span class='headingText'>
																<a href='#' onclick='displayUserRequirementsOfAllReqTypes("completed");'>
																<img src="/GloreeJava2/images/report16.png" border="0">									
																My  Completed  Reqs
																</a>
															</span>
														</td>
													</tr>
													<tr>
														<td colspan='2'>
															<span class='headingText'>
																<a href='#' onclick='displayUserRequirementsOfAllReqTypes("incomplete");'>
																<img src="/GloreeJava2/images/report16.png" border="0">									
																My  Incomplete Reqs
																</a>
															</span>
														</td>
													</tr>
					


			
			
			
													<tr><td colspan='2'>
														<span class='sectionHeadingText'>
														Approval Work Flow Reports
														</span>
													</td></tr>
							 						<tr>						
														<td colspan='2'>
															<span class='headingText'>
																<a href='#' 
																onclick='
																	var approvalWorkFlowObject = document.getElementById("approvalWorkFlow");
																	if (approvalWorkFlowObject.selectedIndex == 0) {
																		alert ("Please select an Approval Work Flow Status");
																		approvalWorkFlowObject.focus();
																		approvalWorkFlowObject.style.backgroundColor="#FFCC99";
																	}
																	else {										
																		var approvalWorkFLowValue = approvalWorkFlowObject[approvalWorkFlowObject.selectedIndex].value;
																		displayUserRequirementsOfAllReqTypes(approvalWorkFLowValue );
																	}
																'>
																<img src="/GloreeJava2/images/report16.png" border="0">
																Reqs in Approval Work Flow
																</a>  
																<select name='approvalWorkFlow' id='approvalWorkFlow'
																 onChange='
																 	var approvalWorkFlowObject = document.getElementById("approvalWorkFlow");
																	if (approvalWorkFlowObject.selectedIndex == 0) {
																		alert ("Please select an Approval Work Flow Status");
																		approvalWorkFlowObject.focus();
																		approvalWorkFlowObject.style.backgroundColor="#FFCC99";
																	}
																	else {										
																		var approvalWorkFLowValue = approvalWorkFlowObject[approvalWorkFlowObject.selectedIndex].value;
																		displayUserRequirementsOfAllReqTypes(approvalWorkFLowValue );
																	}
																'>
																		<option>Work Flow Status</option>
																		<option value='draft'>Status = Draft</option>
																		<option value='pending'>Status = Pending</option>
																		<option value='rejected'>Status = Rejected</option>
																		<option value='approved'>Status = Approved</option>
																</select>
															</span>						
														</td>
													</tr>
													
							 						<tr>						
														<td colspan='2'>
															<span class='headingText'>
																<a href='#' 
																onclick='
																	var approvalWorkFlowActionObject = document.getElementById("approvalWorkFlowAction");
																	if (approvalWorkFlowActionObject.selectedIndex == 0) {
																		alert ("Please select an Action Status");
																		approvalWorkFlowActionObject.focus();
																		approvalWorkFlowActionObject.style.backgroundColor="#FFCC99";
																	}
																	else {										
																		var approvalWorkFlowActionValue = approvalWorkFlowActionObject[approvalWorkFlowActionObject.selectedIndex].value;
																		displayUserRequirementsOfAllReqTypes(approvalWorkFlowActionValue );
																	}
																'>
																<img src="/GloreeJava2/images/report16.png" border="0">
																Reqs that are 
																</a>  
																<select name='approvalWorkFlowAction' id='approvalWorkFlowAction'
																 onChange='
																 	var approvalWorkFlowActionObject = document.getElementById("approvalWorkFlowAction");
																	if (approvalWorkFlowActionObject.selectedIndex == 0) {
																		alert ("Please select an Approval Work Flow Status");
																		approvalWorkFlowActionObject.focus();
																		approvalWorkFlowActionObject.style.backgroundColor="#FFCC99";
																	}
																	else {										
																		var approvalWorkFlowActionValue = approvalWorkFlowActionObject[approvalWorkFlowActionObject.selectedIndex].value;
																		displayUserRequirementsOfAllReqTypes(approvalWorkFlowActionValue );
																	}
																'>
																		<option>Work Flow Status</option>
																		<option value='pendingBy'>Pending My  Approval</option>
																		<option value='rejectedBy'>Rejected By Me</option>
																		<option value='approvedBy'>Approved By Me</option>
																</select>
															</span>						
														</td>
													</tr>


						-->


 						<tr>						
							<td colspan='2'>
								<span class='headingText'>
									<a href='#' 
									onclick='
									var defectStatusGroupObject = document.getElementById("defectStatusGroup");
										if (defectStatusGroupObject.selectedIndex == 0) {
											alert ("Please select a Defect Status");
											defectStatusGroupObject.focus();
											defectStatusGroupObject.style.backgroundColor="#FFCC99";
										}
										else {										
											displayUserRequirementsOfAllReqTypes("defectStatusGroup");
										}
									'>
									<img src="/GloreeJava2/images/report16.png" border="0">
									Defects in
									</a>  
									<select name='defectStatusGroup' id='defectStatusGroup'
									onChange='
									 	var defectStatusGroupObject = document.getElementById("defectStatusGroup");
										if (defectStatusGroupObject.selectedIndex == 0) {
											alert ("Please select a Defect Status");
											defectStatusGroupObject.focus();
											defectStatusGroupObject.style.backgroundColor="#FFCC99";
										}
										else {										
											displayUserRequirementsOfAllReqTypes("defectStatusGroup");
										}
										'>
										<option>Defect Status</option>
										<%
										ArrayList defectStatusGroupsForProject = ReleaseMetricsUtil.getCurrentDefectStatusGroupsInProject(dTProject.getProjectId());	
										Iterator j = defectStatusGroupsForProject.iterator();
										while (j.hasNext()){
											String defectStatus = (String)j.next();
										%>
											<option value='<%=defectStatus%>'><%=defectStatus%></option>
										<%
										}
										%>
									</select>
								</span>						
							</td>
						</tr>




						<!-- 

													<tr><td colspan='2'>
														<span class='sectionHeadingText'>
														Traceability Reports
														</span>
													</td></tr>
							 						<tr>						
														<td colspan='2'>
															<span class='headingText'>
																<a href='#' 
																onclick='
																	var traceabilityObject = document.getElementById("traceability");
																	if (traceabilityObject.selectedIndex == 0) {
																		alert ("Please select a Traceability Condition");
																		traceabilityObject.focus();
																		traceabilityObject.style.backgroundColor="#FFCC99";
																	}
																	else {										
																		var traceabilityValue = traceabilityObject[traceabilityObject.selectedIndex].value;
																		displayUserRequirementsOfAllReqTypes(traceabilityValue );
																	}
																'>
																<img src="/GloreeJava2/images/report16.png" border="0">
																My  Reqs
																</a>  
																<select name='traceability' id='traceability'
																 onChange='
																 	var traceabilityObject = document.getElementById("traceability");
																	if (traceabilityObject.selectedIndex == 0) {
																		alert ("Please select a Traceability Condition");
																		traceabilityObject.focus();
																		traceabilityObject.style.backgroundColor="#FFCC99";
																	}
																	else {										
																		var traceabilityValue = traceabilityObject[traceabilityObject.selectedIndex].value;
																		displayUserRequirementsOfAllReqTypes(traceabilityValue );
																	}
																'>
																		<option>Traceability Condition</option>
																		<option value='orphan'>that are Orphan</option>
																		<option value='dangling'>that are Dangling</option>
																		<option value='suspectUpstream'>that have a Suspect UpStream</option>
																		<option value='suspectDownstream'>that have a Suspect DownStream</option>
																</select>
															</span>						
														</td>
													</tr>
													
													
													
													<tr><td colspan='2'>
														<span class='sectionHeadingText'>
														QA (Quality Assurance) Reports
														</span>
													</td></tr>
							 						<tr>						
														<td colspan='2'>
															<span class='headingText'>
																<a href='#' 
																onclick='
																	var testingStatusObject = document.getElementById("testingStatus");
																	if (testingStatusObject.selectedIndex == 0) {
																		alert ("Please select a Testing Status");
																		testingStatusObject.focus();
																		testingStatusObject.style.backgroundColor="#FFCC99";
																	}
																	else {										
																		var testingStatusValue = testingStatusObject[testingStatusObject.selectedIndex].value;
																		displayUserRequirementsOfAllReqTypes(testingStatusValue );
																	}
																'>
																<img src="/GloreeJava2/images/report16.png" border="0">
																My  Reqs that have 
																</a>  
																<select name='testingStatus' id='testingStatus'
																 onChange='
																 	var testingStatusObject = document.getElementById("testingStatus");
																	if (testingStatusObject.selectedIndex == 0) {
																		alert ("Please select a Testing Status");
																		testingStatusObject.focus();
																		testingStatusObject.style.backgroundColor="#FFCC99";
																	}
																	else {										
																		var testingStatusValue = testingStatusObject[testingStatusObject.selectedIndex].value;
																		displayUserRequirementsOfAllReqTypes(testingStatusValue );
																	}
																'>
																		<option>Testing Status</option>
																		<option value='failedTesting'>Failed Testing</option>
																		<option value='passedTesting'>Passed Testing</option>
																		<option value='pendingTesting'>Pending Testing</option>
																		
																</select>
															</span>						
														</td>
													</tr>
						-->
					</table>
				</td>
	
	
	
	
	
				<td valign='top'>
					<div style="overflow: auto; width: 350px; height: 350px; 
						border-left: 1px white solid; border-bottom: 1px gray solid; 
						padding:0px; margin: 0px">
						<table class='paddedTable' width='100%'>
							<tr><td colspan='2'>
								<span class='sectionHeadingText'>
								My Saved Reports
								</span>
							</td></tr>
							<%
							ArrayList reports = ProjectUtil.getUserReports(dTProject.getProjectId(), user);
							int reportCount= 0;
						    if (reports != null){
						    	Iterator i = reports.iterator();
						    	while ( i.hasNext() ) {
						    		Report r = (Report) i.next();
						    		if (
						    				!(r.getReportDescription().startsWith("Canned"))
						    				&&
						    				(r.getReportVisibility().equals("private"))
						    				&&
						    				(r.getCreatedByEmailId().equals(user.getEmailId()))
						    			) 
						    		{
						    			reportCount++;
						    			// this is a user definedreport and is set as a privately visible report
						    			// and is owned by the signed in user.
							%>
										<tr>
											<td colspan='2'>
												<span class='normalText' title='Delete This Report'>
						 							<a href="#" 
						 							onClick='document.getElementById("<%=r.getReportId()%>Div").style.display="block";'>
						 							<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					 							</span>
					 							&nbsp;
						 						<span class='normalText' title="Visibility : <%=r.getReportVisibility()%>;  Description : <%=r.getReportDescription()%>">
						 						<%if (r.getReportType().equals("list")){ %>
						 							<a href="#" 
						 							onClick="navigateToAndDisplayExistingReport(<%=r.getFolderId()%>, <%=r.getReportId()%>,'list')">
						 							<img src="/GloreeJava2/images/report16.png" border="0">
						 							&nbsp;<%=r.getReportName() %></a>	
						 						<%} else { %>
						 							<a href="#" onClick="navigateToAndDisplayExistingReport(<%=r.getFolderId()%>, <%=r.getReportId()%>,'traceTree')">
						 							<img src="/GloreeJava2/images/reportTraceTree.png" border="0">
						 							&nbsp;<%=r.getReportName() %></a>
						 						<%} %>
						 						</span>
	
						 						<div id='<%=r.getReportId()%>Div' class='alert alert-success' Style='display:none;'> 
						 							<span class='normalText'>
						 								Are you sure you want to delete this report?
						 								<br><br> 
						 								<a href='#' 
						 								onClick='deleteReportFromUserDashboard(<%=r.getFolderId()%>, <%=r.getReportId()%>)'>
							 							Delete</a>
							 							&nbsp;
							 							<a href='#' 
						 								onClick='document.getElementById("<%=r.getReportId()%>Div").style.display="none";'>
							 							Cancel</a>
						 							</span>
						 						</div>
						 						
											</td>
										</tr>
							<%
						    		}
						    	}
						    }
							
							
							reportCount= 0;
						    if (reports != null){
						    	Iterator i = reports.iterator();
						    	while ( i.hasNext() ) {
						    		Report r = (Report) i.next();
						    		if (
						    				!(r.getReportDescription().startsWith("Canned"))
						    				&&
						    				(r.getReportVisibility().equals("public"))
						    			)  
						    		{
						    			reportCount++;
						    			// this is a user definedreport and is set as a privately visible report
						    			// and is owned by the signed in user.
							%>
										<tr>
											<td colspan='2'>
												<span class='normalText' title='Delete This Report'>
						 							<a href="#" 
						 							onClick='document.getElementById("<%=r.getReportId()%>Div").style.display="block";'>
						 							<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					 							</span>
					 							&nbsp;
						 						<span class='normalText' title="Visibility : <%=r.getReportVisibility()%>;  Description : <%=r.getReportDescription()%>">
						 						<%if (r.getReportType().equals("list")){ %>
						 							<a href="#" 
						 							onClick="navigateToAndDisplayExistingReport(<%=r.getFolderId()%>, <%=r.getReportId()%>,'list')">
						 							<img src="/GloreeJava2/images/report16.png" border="0">
						 							&nbsp;<%=r.getReportName() %></a>	
						 						<%} else { %>
						 							<a href="#" onClick="navigateToAndDisplayExistingReport(<%=r.getFolderId()%>, <%=r.getReportId()%>,'traceTree')">
						 							<img src="/GloreeJava2/images/reportTraceTree.png" border="0">
						 							&nbsp;<%=r.getReportName() %></a>
						 						<%} %>
						 						</span>
	
						 						<div id='<%=r.getReportId()%>Div' class='alert alert-success' Style='display:none;'> 
						 							<span class='normalText'>
						 								Are you sure you want to delete this report?
						 								<br><br> 
						 								<a href='#' 
						 								onClick='deleteReportFromUserDashboard(<%=r.getFolderId()%>, <%=r.getReportId()%>)'>
							 							Delete</a>
							 							&nbsp;
							 							<a href='#' 
						 								onClick='document.getElementById("<%=r.getReportId()%>Div").style.display="none";'>
							 							Cancel</a>
						 							</span>
						 						</div>
						 						
											</td>
										</tr>
							<%
						    		}
						    	}
						    }
							%>
							
							
							
							
							
							
							
							
							
							
							
						</table>
					</div>
				</td>
				
				
				
				
				
				
				
				
				
				
				
				
				
				<td valign='top'>
					<div style="overflow: auto; width: 350px; height: 350px; 
						border-left: 1px white solid; border-bottom: 1px gray solid; 
						padding:0px; margin: 0px">
						<table class='paddedTable' width='100%'>
							<tr><td colspan='2'>
								<span class='sectionHeadingText'>
								My Word Docs
								</span>
							</td></tr>
							<%			  
								ArrayList wordTemplates = ProjectUtil.getUserWordTemplates(dTProject.getProjectId(), user, dTDatabaseType);
								reportCount = 0;
							    if (wordTemplates != null){
							    	Iterator i = wordTemplates.iterator();
							    	while ( i.hasNext() ) {
							    		WordTemplate wordTemplate = (WordTemplate) i.next();
							    		String templateVisibility = wordTemplate.getTemplateVisibility();
										if (
												(templateVisibility != null) && 
												(templateVisibility.equals("private")) &&
												(wordTemplate.getCreatedBy().equals(user.getEmailId()))){
											reportCount++;
							 %>
							 				<tr id="<%=wordTemplate.getTemplateId() %>">
										 		<td colspan=2>
							 						<span class='normalText' title="Description : <%=wordTemplate.getTemplateDescription() %>">
						 							<a href="#" 
							 							onClick="navigateToAndDisplayWordTemplate(<%=wordTemplate.getFolderId()%>, <%=wordTemplate.getTemplateId() %>)">
							 							<img src="/GloreeJava2/images/ExportWord16.gif" border="0">
							 						&nbsp;<%=wordTemplate.getTemplateName()%></a> 
							 						</span>
							 					</td>			
							 				</tr>
							 <%
							    		}
							    	}
							    }
							  					    
							%>					
							  
							<%			  
								reportCount = 0;
							    if (wordTemplates != null){
							    	Iterator i = wordTemplates.iterator();
							    	while ( i.hasNext() ) {
							    		WordTemplate wordTemplate = (WordTemplate) i.next();
							    		String templateVisibility = wordTemplate.getTemplateVisibility();
										if ((templateVisibility != null) && (templateVisibility.equals("public"))){
											reportCount++;
							 %>
							 				<tr id="<%=wordTemplate.getTemplateId() %>">
										 		<td colspan=2>
							 						<span class='normalText' title="Description : <%=wordTemplate.getTemplateDescription() %>">
						 							<a href="#" 
							 							onClick="navigateToAndDisplayWordTemplate(<%=wordTemplate.getFolderId()%>, <%=wordTemplate.getTemplateId() %>)">
							 							<img src="/GloreeJava2/images/ExportWord16.gif" border="0">
							 						&nbsp;<%=wordTemplate.getTemplateName()%></a> 
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
				</td>				
			</tr>
		</table>
	</div>
	
	
	
<%}%>