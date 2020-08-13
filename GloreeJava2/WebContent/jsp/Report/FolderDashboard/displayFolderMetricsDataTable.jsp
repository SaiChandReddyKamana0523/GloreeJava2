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

<%if (IsMember){ %>


	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	
	<%
	int folderId  = 0;
	try {
		folderId = Integer.parseInt(request.getParameter("folderId"));
	}
	catch (Exception e){
		folderId = 0;
	}
	if (folderId == 0 ){
		return;
	}
	Folder folder = new Folder (folderId);
	String defaultDisplayAttributes =  "";
	ArrayList defaultDisplayAttributesArray = folder.getDefaultDisplayAttributes();
	Iterator dDA = defaultDisplayAttributesArray.iterator();
	while (dDA.hasNext()){
		String temp = (String) dDA.next();
		defaultDisplayAttributes += ":#:" + temp;
	}

	
	int noOfReqsInThisFolder = 0;
	int isFolderEnabledForApproval = folder.getIsFolderEnabledForApproval() ;
	// lets get the list of distinct req types in this release tree.	
	//ArrayList dataTable = ReleaseMetricsUtil.getReleaseDataTableArrayForFolder(
	//		folderId,project.getProjectId(), user, databaseType);
	
	String chartURLParam = "";
	 
	noOfReqsInThisFolder = folder.getCountOfRequirements();
	if (noOfReqsInThisFolder > 0 ) {
		// This whole dashboard gets displayed only if the folder has more than 0 reqs

		
		String allRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
		"includeSubFoldersSearch:--:no";
		
		String draftRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
		"includeSubFoldersSearch:--:no:###:statusSearch:--:Draft";
		
		
		String inApprovalWorkFlowRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
		"includeSubFoldersSearch:--:no:###:statusSearch:--:In Approval WorkFlow";
		
		String approvedRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
		"includeSubFoldersSearch:--:no:###:statusSearch:--:Approved";
		
		String rejectedRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
		"includeSubFoldersSearch:--:no:###:statusSearch:--:Rejected";
		
		
		String danglingRequirementsRD = "active:--:active:###:danglingSearch:--:danglingOnly:###:orphanSearch:--:all:###:" +
		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
		"includeSubFoldersSearch:--:no";

		String orphanRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:orphanOnly:###:" +
		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
		"includeSubFoldersSearch:--:no";

		String suspectUpStreamRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
		"suspectUpStreamSearch:--:suspectUpStreamOnly:###:suspectDownStreamSearch:--:all:###:"  +
		"includeSubFoldersSearch:--:no";

		String suspectDownStreamRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:suspectDownStreamOnly:###:"  +
		"includeSubFoldersSearch:--:no";

		String completedRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
		"completedSearch:--:completedOnly:###:incompleteSearch:--:all:###:" +
		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
		"includeSubFoldersSearch:--:no";
		
		String incompleteRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
		"completedSearch:--:all:###:incompleteSearch:--:incompleteOnly:###:" +
		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
		"includeSubFoldersSearch:--:no";
	
		String testPendingRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
		"includeSubFoldersSearch:--:no:###:testingStatusSearch:--:Pending";
		
		String testPassRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
		"includeSubFoldersSearch:--:no:###:testingStatusSearch:--:Pass";
		
		String testFailRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
		"includeSubFoldersSearch:--:no:###:testingStatusSearch:--:Fail";
		
	
		%>
	 
	 
	 
	 <br><br>
	
			<div id = 'displayListReportDiv' >
			<table class='table' width='100%'>
				<tr>
					<td style=" border-top: none">
						<div id ='reportData'>
						<table id = "Report" class='table'>				
			
					<%
					       	int j = 0;
					    	String cellStyle = "normalTableCell";
					    	
					    		// String[] dataRow = dataTableString.split(":##:");
					    		String requirementTypeShortName = "";
								String numOfRequirements = Integer.toString(noOfReqsInThisFolder);
								
								
								
								int noOfPending = 0; 
								int noOfApproved = 0 ;
								int noOfRejected = 0 ;
								int noOfDraft = 0;
								if (isFolderEnabledForApproval == 1){
									noOfPending = folder.getFolderMetric_NoOfApprovalPendingRequirements();
									noOfApproved = folder.getFolderMetric_NoOfApprovedRequirements();
									noOfRejected = folder.getFolderMetric_NoOfRejectedRequirements();
									noOfDraft = noOfReqsInThisFolder - (noOfPending + noOfRejected + noOfApproved);
								}
								
								String numOfInApprovalWorkflowRequirements = Integer.toString(noOfPending);
								String numOfRejectedRequirements = Integer.toString(noOfRejected);;
								String numOfApprovedRequirements = Integer.toString(noOfApproved);;
								String numOfDraftRequirements = Integer.toString(noOfDraft);;
								
								
								int noOfDangling = 0;
								if (folder.canBeReportedDangling()){
									noOfDangling = folder.getFolderMetric_NoOfDanglingRequirements();
								}
								String numOfDanglingRequirements = Integer.toString(noOfDangling);
								
								
								int noOfOrphan = 0;
								if (folder.canBeReportedOrphan()){
									noOfOrphan = folder.getFolderMetric_NoOfOrphanRequirements();
								}
								String numOfOrphanRequirements = Integer.toString(noOfOrphan);
								
								
								String numOfSuspectUpstreamRequirements = Integer.toString(folder.getFolderMetric_NoOfSuspectUpstreamRequirements());
								String numOfSuspectDownstreamRequirements = Integer.toString(folder.getFolderMetric_NoOfSuspectDownstreamRequirements());
								
								
								int noOfCompletes = folder.getFolderMetric_NoOfCompletedRequirements();
								String numOfCompletedRequirements = Integer.toString(noOfCompletes);
								
								int noOfIncompletes =  noOfReqsInThisFolder - noOfCompletes;
								String numOfIncompleteRequirements =Integer.toString( noOfIncompletes);
								
								int noOfTestPending = folder.getFolderMetric_NoOfTestPendingRequirements();
								String numOfTestPendingRequirements = Integer.toString(noOfTestPending);
								
								int noOfTestPass = folder.getFolderMetric_NoOfTestPassRequirements();
								String numOfTestPassRequirements = Integer.toString(noOfTestPass);
								
								int noOfTestFail = noOfReqsInThisFolder - (noOfTestPending + noOfTestPass );
								
								String numOfTestFailRequirements = Integer.toString(noOfTestFail);

								
								requirementTypeShortName = folder.getRequirementTypeShortName();
					    		//if (dataRow.length > 1) {
								//	requirementTypeShortName = dataRow[1];
					    		//}
					    		//if (dataRow.length > 2) {
								//	numOfRequirements = dataRow[2];
					    		//}
					    		//if (dataRow.length > 3) {
								//	numOfDraftRequirements = dataRow[3];
					    		//}
					    		//if (dataRow.length > 4) {
								//	numOfInApprovalWorkflowRequirements = dataRow[4];
					    		//}
					    		//if (dataRow.length > 5) {
								//	numOfRejectedRequirements = dataRow[5];
					    		//}
					    		//if (dataRow.length > 6) {
								//	numOfApprovedRequirements = dataRow[6];
					    		//}
					    		//if (dataRow.length > 7) {
								//	numOfDanglingRequirements = dataRow[7];
					    		//}
					    		//if (dataRow.length > 8) {
								//	numOfOrphanRequirements = dataRow[8];
					    		//}
					    		//if (dataRow.length > 9) {
								//	numOfSuspectUpstreamRequirements = dataRow[9];
					    		//}
					    		//if (dataRow.length > 10) {
								//	numOfSuspectDownstreamRequirements = dataRow[10];
					    		//}
					    		//if (dataRow.length > 11) {
					    		//	numOfCompletedRequirements = dataRow[11];
					    		//}
					    		//if (dataRow.length > 12) {
								//	numOfIncompleteRequirements = dataRow[12];
					    		//}
					    		//if (dataRow.length > 13) {
								//	numOfTestPendingRequirements = dataRow[13];
					    		//}
					    		//if (dataRow.length > 14) {
								//	numOfTestPassRequirements = dataRow[14];
					    		//}
					    		//if (dataRow.length > 15) {
								//	numOfTestFailRequirements = dataRow[15];
					    		//}	
					    		
					    		
					    		//try {
					    		//	noOfReqsInThisFolder = Integer.parseInt(numOfRequirements);
					    		//}
					    		//catch (Exception e) {
					    		//	noOfReqsInThisFolder = 0;
					    		//}
					    		
					    		// lets build the chartURL string that can be used to display a google chart.
					    		chartURLParam += "&total=" + numOfRequirements;

					    		chartURLParam += "&isFolderEnabledForApproval=" + isFolderEnabledForApproval;
					    		chartURLParam += "&draft=" + numOfDraftRequirements;
					    		chartURLParam += "&pending=" + numOfInApprovalWorkflowRequirements;
					    		chartURLParam += "&rejected=" + numOfRejectedRequirements;
					    		chartURLParam += "&approved=" + numOfApprovedRequirements;
					    		
					    		chartURLParam += "&dangling=" + numOfDanglingRequirements;
					    		chartURLParam += "&orphan=" + numOfOrphanRequirements;

					    		chartURLParam += "&suspectUp=" + numOfSuspectUpstreamRequirements;
					    		chartURLParam += "&suspectDown=" + numOfSuspectDownstreamRequirements;

					    		chartURLParam += "&testPending=" + numOfTestPendingRequirements;
					    		chartURLParam += "&testPass=" + numOfTestPassRequirements;
					    		chartURLParam += "&testFail=" + numOfTestFailRequirements;

					    		chartURLParam += "&completed=" + numOfCompletedRequirements;
					    		chartURLParam += "&incomplete=" + numOfIncompleteRequirements;
					    		
					    		
					    		// lets get the requirementtype object, so we can determine if approval work flow is enabled or not
					    		RequirementType requirementType = new RequirementType(project.getProjectId(),requirementTypeShortName,user.getEmailId());
					    		boolean displayApprovalMetrics = false;
					    		if (requirementType.getRequirementTypeEnableApproval() == 1) {
					    			displayApprovalMetrics = true;
					    		}
					    		
					    		j++;
					    		// for the first row, print the header and user defined columns etc..
					    		if (j == 1){
					 %>
					 
									<tr>
										<td class='tableHeader' colspan='1' BGCOLOR="#E5FFEC">&nbsp;</td>
										<%if (displayApprovalMetrics){ %>
											<td class='tableHeader' colspan='4' align='center' BGCOLOR="#E5EBFF" 
											>
												<span class='sectionHeadingText'>
												Approval
												</span>
											</td>
										<%} %>
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
										<td class='tableHeader' BGCOLOR="#E5FFEC">
											<span class='sectionHeadingText'>
											Total
											</span>
										 </td>
									
									
										<%if (displayApprovalMetrics){ %>
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
							 		
							 		<td   border-top: none
							 			bgcolor=white onmouseover="this.bgColor='#d3d3d3'" onmouseout="this.bgColor='white'" 
										 onClick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=allRequirementsRD%>")'
										 Style="cursor:pointer; border-top: none"
										 title="Total number of objects in this folder"
									>
										<span class='normalText'><font color='blue'><%=noOfReqsInThisFolder %></font></span> 
							 		</td>


									<%if (displayApprovalMetrics){ %>
									
									
										<td  
										 bgcolor=white onmouseover="this.bgColor='#d3d3d3'" onmouseout="this.bgColor='white'" 
										 onClick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=draftRequirementsRD%>")'
										 Style="cursor:pointer; border-top: none"
										 title="Objects in 'Draft' approval status"
										>
										 <span class='normalText'><font color='blue'><%=numOfDraftRequirements%></font></span>
										</td>
								 	
								 		
								 		<td 
							 			 bgcolor=white onmouseover="this.bgColor='#d3d3d3'" onmouseout="this.bgColor='white'" 
										 onClick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=inApprovalWorkFlowRequirementsRD%>")'
										 Style="cursor:pointer; border-top: none"
										 title="Objects that are 'Pending' approval "
										>
										<span class='normalText'><font color='blue'><%=numOfInApprovalWorkflowRequirements %></font></span>
										</td>	
									
									
										<td 
							 			 bgcolor=pink onmouseover="this.bgColor='#d3d3d3'" onmouseout="this.bgColor='pink'" 
										 onClick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=rejectedRequirementsRD%>")'
										 Style="cursor:pointer; border-top: none"
										 title="Objects that have been 'Rejected' "
										>
										<span class='normalText'><font color='blue'><%=numOfRejectedRequirements %></font></span>
							 			</td>
								 		
								 		
								 		
								 		<td 
							 			 bgcolor=lightgreen onmouseover="this.bgColor='#d3d3d3'" onmouseout="this.bgColor='lightgreen'" 
										 onClick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=approvedRequirementsRD%>")'
										 Style="cursor:pointer; border-top: none"
										 title="Objects that have been 'Approved' "
										>
										<span class='normalText'><font color='blue'><%=numOfApprovedRequirements %></font></span>
							 			</td>
							 			
							 			
								 		
									<%} %>
									


									<td 
						 			 bgcolor=white onmouseover="this.bgColor='#d3d3d3'" onmouseout="this.bgColor='white'" 
									 onClick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=danglingRequirementsRD%>")'
									 Style="cursor:pointer; border-top: none"
									 title="Objects that do not have a trace from a Downstream object "
									>
									<span class='normalText'><font color='blue'><%=numOfDanglingRequirements %></font></span>
						 			</td>
							 		
							 		
							 		<td 
						 			 bgcolor=white onmouseover="this.bgColor='#d3d3d3'" onmouseout="this.bgColor='white'" 
									 onClick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=orphanRequirementsRD%>")'
									 Style="cursor:pointer; border-top: none"
									 title="Objects that do not trace to an UpStream object "
									>
									<span class='normalText'><font color='blue'><%=numOfOrphanRequirements %></font></span>
						 			</td>
							 		
							 		
							 		<td 
						 			 bgcolor=pink onmouseover="this.bgColor='#d3d3d3'" onmouseout="this.bgColor='pink'" 
									 onClick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=suspectUpStreamRequirementsRD%>")'
									 Style="cursor:pointer; border-top: none"
									 title="Objects with 'Suspect' relationship to an Upstream object "
									>
									<span class='normalText'><font color='blue'><%=numOfSuspectUpstreamRequirements %></font></span>
						 			</td>
							 		
							 		
							 		
							 		<td 
						 			 bgcolor=pink onmouseover="this.bgColor='#d3d3d3'" onmouseout="this.bgColor='pink'" 
									 onClick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=suspectDownStreamRequirementsRD%>")'
									 Style="cursor:pointer; border-top: none"
									 title="Objects with 'Suspect' relationship to an Downstream object "
									>
									<span class='normalText'><font color='blue'><%=numOfSuspectDownstreamRequirements %></font></span>
						 			</td>
							 							 		






							 		<td 
						 			 bgcolor=white onmouseover="this.bgColor='#d3d3d3'" onmouseout="this.bgColor='white'" 
									 onClick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=testPendingRequirementsRD%>")'
									 Style="cursor:pointer; border-top: none"
									 title="Objects that have not been tested yet "
									>
									<span class='normalText'><font color='blue'><%=numOfTestPendingRequirements %></font></span>
						 			</td>
						 			
						 			
						 			<td 
						 			 bgcolor=lightgreen onmouseover="this.bgColor='#d3d3d3'" onmouseout="this.bgColor='lightgreen'" 
									 onClick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=testPassRequirementsRD%>")'
									 Style="cursor:pointer; border-top: none"
									 title="Objects that have PASSED Testing "
									>
									<span class='normalText'><font color='blue'><%=numOfTestPassRequirements %></font></span>
						 			</td>
						 			
						 			<td 
						 			 bgcolor=pink onmouseover="this.bgColor='#d3d3d3'" onmouseout="this.bgColor='pink'" 
									 onClick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=testFailRequirementsRD%>")'
									 Style="cursor:pointer; border-top: none"
									 title="Objects that have FAILED Testing "
									>
									<span class='normalText'><font color='blue'><%=numOfTestFailRequirements %></font></span>
						 			</td>
						 			
						 							 					 				 		




							 		<td 
						 			 bgcolor=lightgreen onmouseover="this.bgColor='#d3d3d3'" onmouseout="this.bgColor='lightgreen'" 
									 onClick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=completedRequirementsRD%>")'
									 Style="cursor:pointer; border-top: none"
									 title="Objects that have been Completed "
									>
									<span class='normalText'><font color='blue'><%=numOfCompletedRequirements %></font></span>
						 			</td>
						 			
						 			<td 
						 			 bgcolor=pink onmouseover="this.bgColor='#d3d3d3'" onmouseout="this.bgColor='pink'" 
									 onClick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=incompleteRequirementsRD%>")'
									 Style="cursor:pointer; border-top: none"
									 title="Objects that are still Incomplete "
									>
									<span class='normalText'><font color='blue'><%=numOfIncompleteRequirements %></font></span>
						 			</td>
						 			
						 										
				 				</tr>
					 <%
					    	
					    }
					%>
					
					</table>
					</div>
					<%
					// we show the chart only if the number of requirements > 0 
					// some time we don't want to display the chart. check for the chart=yes / no param.
					
					String chart =  request.getParameter("chart");
					if (chart == null){
						chart = "yes";
					}
					
					// lets not display the chart for Release objects, as it does not make business sense
					if (folder.getRequirementTypeShortName().equals("REL")){
						chart = "no";
					}
					
					if ((chart.equals("yes") && (noOfReqsInThisFolder > 0) )){
					%>
					
					
					<br><br>
					<div id="showFolderMetricsChartDiv" >
						<iframe src='/GloreeJava2/jsp/Report/FolderDashboard/displayFolderMetricsChart.jsp?folderId=<%=folderId%><%=chartURLParam%>' width='1500', height='500' ></iframe>
		
					</div>
					<%} %>
					</td>
				</tr>
			</table>
		</div>
<%
	}%>