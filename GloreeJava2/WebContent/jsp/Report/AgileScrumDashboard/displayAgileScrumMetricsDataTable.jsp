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
	// we used releaseId == 0 to indicate project level number crunching when we call releaseMetricsUtil routines.
	int sprintId = Integer.parseInt(request.getParameter("sprintId"));
	
	String lastDataLoadDt =  ReleaseMetricsUtil.getLastDataLoadDtForAgileSprint(sprintId,project.getProjectId(), databaseType);

	// lets get the list of distinct req types in this release tree.	
	ArrayList dataTable = ReleaseMetricsUtil.getReleaseDataTableArrayForAgileSprint(
			sprintId,project.getProjectId(), user, databaseType);
	
	%>
	 
	
	<div id = 'displayListReportDiv' class='level1Box'>
	<table class='paddedTable' width='100%'>
		<tr>
			<td bgcolor="#99ccff" align="left">				
				<span class="subSectionHeadingText">
					Agile Sprint Metrics
					<% if(lastDataLoadDt != null) { %>
						 as of '<%=lastDataLoadDt%>'
					<%} %> 
				</span>
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
						    					<span class='normalText'> No Requirements were found in this Sprint.
						    					<br><br>Please Note that the Sprint Metrics get calculated once a night and 
						    					the Metrics calculation script may not have had a chance to run since
						    					you have added Requirements to this Sprint.
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
							 			<a href='#' onclick='displayAgileScrumRequirements("<%=requirementTypeShortName%>", <%=sprintId%>, "all")' >
							 			 <%=numOfRequirements%></a>
							 			</span>
							 		</td>
							 		
							 		
							 		
							 		
							 		<td class='<%=cellStyle%>' >
							 			<span class='normalText'>
							 			<a href='#' onclick='displayAgileScrumRequirements("<%=requirementTypeShortName%>", <%=sprintId%>,"draft")' >
							 			<%=numOfDraftRequirements %>
							 			</a>
							 			</span>
							 		</td>
							 		<td class='<%=cellStyle%>' >
							 			<span class='normalText'>
							 			<a href='#' onclick='displayAgileScrumRequirements("<%=requirementTypeShortName%>", <%=sprintId%>,"pending")' >
							 			<%=numOfInApprovalWorkflowRequirements %>
							 			</a>
							 			</span>
							 		</td>	
									<td class='<%=cellStyle%>' >
							 			<span class='normalText'>
							 			<a href='#' onclick='displayAgileScrumRequirements("<%=requirementTypeShortName%>", <%=sprintId%>,"rejected")' >
							 			<%=numOfRejectedRequirements %>
							 			</a>
							 			</span>
							 		</td>
							 		<td class='<%=cellStyle%>' >
							 			<span class='normalText'>
							 			<a href='#' onclick='displayAgileScrumRequirements("<%=requirementTypeShortName%>", <%=sprintId%>,"approved")' >
							 			<%=numOfApprovedRequirements %>
							 			</a>
							 			</span>
							 		</td>
							 		
							 		
							 		
							 		
							 		<td class='<%=cellStyle%>' >
							 			<span class='normalText'>
							 			<a href='#' onclick='displayAgileScrumRequirements("<%=requirementTypeShortName%>", <%=sprintId%>,"dangling")' >
							 			<%=numOfDanglingRequirements %>
							 			</a>
							 			</span>
							 		</td>
							 		<td class='<%=cellStyle%>' >
							 			<span class='normalText'>
							 			<a href='#' onclick='displayAgileScrumRequirements("<%=requirementTypeShortName%>", <%=sprintId%>,"orphan")' >
							 			<%=numOfOrphanRequirements %>
							 			</a>
							 			</span>
							 		</td>
							 		<td  class='<%=cellStyle%>'  >
							 			<span class='normalText'>
							 			<a href='#' onclick='displayAgileScrumRequirements("<%=requirementTypeShortName%>", <%=sprintId%>,"suspectUpstream")' >
							 			<%=numOfSuspectUpstreamRequirements %>
							 			</a>
							 			</span>
							 		</td>
							 		<td class='<%=cellStyle%>'  >
							 			<span class='normalText'>
							 			<a href='#' onclick='displayAgileScrumRequirements("<%=requirementTypeShortName%>", <%=sprintId%>,"suspectDownstream")' >
							 			<%=numOfSuspectDownstreamRequirements %>
							 			</a>
							 			</span>
							 		</td>
							 		
							 		
							 		
							 		
							 		<td class='<%=cellStyle%>' >
							 			<span class='normalText'>
							 			<a href='#' onclick='displayAgileScrumRequirements("<%=requirementTypeShortName%>", <%=sprintId%>,"pendingTesting")' >
							 			<%=numOfTestPendingRequirements %>
							 			</a>
							 			</span>
							 		</td>					
							 		<td class='<%=cellStyle%>' >
							 			<span class='normalText'>
							 			<a href='#' onclick='displayAgileScrumRequirements("<%=requirementTypeShortName%>", <%=sprintId%>,"passedTesting")' >
							 			<%=numOfTestPassRequirements %>
							 			</a>
							 			</span>
							 		</td>				
							 		<td class='<%=cellStyle%>' >
							 			<span class='normalText'>
							 			<a href='#' onclick='displayAgileScrumRequirements("<%=requirementTypeShortName%>", <%=sprintId%>,"failedTesting")' >
							 			<%=numOfTestFailRequirements %>
							 			</a>
							 			</span>
							 		</td>							 					 				 		
							 		
							 		
							 		<td class='<%=cellStyle%>' >
							 			<span class='normalText'>
							 			<a href='#' onclick='displayAgileScrumRequirements("<%=requirementTypeShortName%>", <%=sprintId%>,"completed")' >
							 			<%=numOfCompletedRequirements %>
							 			</a>
							 			</span>
							 		</td>				
							 		<td class='<%=cellStyle%>' >
							 			<span class='normalText'>
							 			<a href='#' onclick='displayAgileScrumRequirements("<%=requirementTypeShortName%>", <%=sprintId%>,"incomplete")' >
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
<%}%>