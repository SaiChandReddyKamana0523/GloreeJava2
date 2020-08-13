<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

 
<%
	// authentication only
	String dPSRIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dPSRIsLoggedIn == null) || (dPSRIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dPSRIsMember = false;
	Project project= (Project) session.getAttribute("project");
	
	String foldersEnabledForApprovalWorkFlow = project.getFoldersEnabledForApprovalWorkFlow();
	
	SecurityProfile dPSRSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dPSRSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		dPSRIsMember = true;
	}
	User user = dPSRSecurityProfile.getUser();
	
	// lets see if there is a session variable called bulkEditResponseString in session memory
	String bulkEditResponseString = (String) session.getAttribute("bulkEditResponseString");
	if ((bulkEditResponseString != null ) && !(bulkEditResponseString.equals(""))){
		// if there was a session variable value for bulkEditResponseString, lets empty it, as we will display it here and get rid of it.
		session.removeAttribute("bulkEditResponseString");
	}
	
%>

<%if (dPSRIsMember){ %>


	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	<%@ page import="java.text.SimpleDateFormat" %>
	<%@ page import="java.util.Calendar" %>
	
	
	
	<%
		// we use the same subroutines in releseMetricsUtil, but use releaseId == -1  to indicate userlevel
		// crunching.
		int releaseId = -1;

	
		String requirementTypeShortName = request.getParameter("requirementTypeShortName");
		String dataType = request.getParameter("dataType");
		// NOTE : if dataType == changedAfter then cutOffDate comes with a date string.
		String cutOffDate = request.getParameter("cutOffDate");

		////////////////////////////////////////SECURITY//////////////////////////
		//
		// We ensure that the project Id is used as a filter in the Release Metrics Util
		// routine. This project id comes from the user's session, hence the user is 
		// logged in and is a member of this project. 
		//
		////////////////////////////////////////SECURITY//////////////////////////
		
		// get an ArrayList of requirements. 
		String defectStatusGroup = "";
		ArrayList userRequirements = null;
		if (dataType.equals("defectStatusGroup")){
			defectStatusGroup = request.getParameter("defectStatusGroup");
			userRequirements = ReleaseMetricsUtil.getRequirementsForDefectStatusGroup(
				dPSRSecurityProfile, defectStatusGroup ,"user",user.getUserId(), 
				project.getProjectId(), user, databaseType);
		}
		else {
			userRequirements = ReleaseMetricsUtil.getRequirementsForReleaseOrProject(dPSRSecurityProfile, 
			releaseId,
			requirementTypeShortName, dataType , project.getProjectId(), cutOffDate, user, databaseType);
		}
		// lets set the arraylist of results in session, so that we can re-use them
		// when we export to Excel.
		session.setAttribute("userRequirements", userRequirements);
		Date now = new Date();
	    SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm zzz"); 
	    String parsedDate = formatter.format(now); 
	    

		String reportTitle = "";
		if ((dataType != null) && (requirementTypeShortName != null)){
			if ((dataType.equals("all")) && (requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Requirements as of " 
				+ parsedDate ;
			}
			if ((dataType.equals("changedAfter")) && (requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Requirements that changed after "
				+ cutOffDate + " as of " + parsedDate;
			}
			if ((dataType.equals("draft")) && (requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Requirements in Draft State as of "
				+ parsedDate ;
			}
			if ((dataType.equals("draft")) && (requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Requirements in Draft State as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("pending")) && (requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Requirements in Pending State as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("rejected")) && (requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Requirements in Rejected State as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("approved")) && (requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Requirements in Approved State as of  "
				+ parsedDate ;
			}
			
			
			if ((dataType.equals("pendingBy")) && (requirementTypeShortName.equals("all"))){
				reportTitle = "Requirements Pending " + user.getFirstName() + " " +  user.getLastName() + "'s Approval as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("rejectedBy")) && (requirementTypeShortName.equals("all"))){
				reportTitle = "Requirements Rejected by " + user.getFirstName() + " " +  user.getLastName() + " as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("approvedBy")) && (requirementTypeShortName.equals("all"))){
				reportTitle = "Requirements Approved by " + user.getFirstName() + " " +  user.getLastName() + " as of  "
				+ parsedDate ;
			}
			
			
			
			
			if ((dataType.equals("defectStatusGroup")) && (requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Defects in Status " + defectStatusGroup 
				+" as of  " + parsedDate ;
			}
			
			
			if ((dataType.equals("completed")) && (requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Completed Requirements as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("incomplete")) && (requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s InComplete Requirements as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("orphan")) && (requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Orphan Requirements as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("dangling")) && (requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Dangling Requirements as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("suspectUpstream")) && (requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Requirements with a Suspect Upstream as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("suspectDownstream")) && (requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Requirements with a Suspect Downstream as of  "
				+ parsedDate ;
			}
			
			
			
			if ((dataType.equals("failedTesting")) && (requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Requirements that have Failed Testing as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("passedTesting")) && (requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Requirements that have Passed Testing as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("pendingTesting")) && (requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Requirements that are Pending Testing as of  "
				+ parsedDate ;
			}


			// lets build report tiles for links coming from Metrics table. These have a Req Type associated with them.
			if ((dataType.equals("all")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s "+ requirementTypeShortName  +" Requirements as of " 
				+ parsedDate ;
			}
			if ((dataType.equals("changedAfter")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s "+ requirementTypeShortName  +" Requirements that changed after"
				+ cutOffDate + " as of " + parsedDate;
			}
			if ((dataType.equals("draft")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s "+ requirementTypeShortName  +" Requirements in Draft State as of "
				+ parsedDate ;
			}
			if ((dataType.equals("draft")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s "+ requirementTypeShortName  +" Requirements in Draft State as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("pending")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s "+ requirementTypeShortName  +" Requirements in Pending State as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("rejected")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s "+ requirementTypeShortName  +" Requirements in Rejected State as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("approved")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s "+ requirementTypeShortName  +" Requirements in Approved State as of  "
				+ parsedDate ;
			}
			
			
			if ((dataType.equals("pendingBy")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = requirementTypeShortName  +" Requirements Pending " + user.getFirstName() + " " +  user.getLastName() + "'s Approval as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("rejectedBy")) && (!requirementTypeShortName.equals("all"))){
				reportTitle =  requirementTypeShortName  +" Requirements Rejected by " + user.getFirstName() + " " +  user.getLastName() + " as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("approvedBy")) && (!requirementTypeShortName.equals("all"))){
				reportTitle =  requirementTypeShortName  +" Requirements Approved by " + user.getFirstName() + " " +  user.getLastName() + " as of  "
				+ parsedDate ;
			}
			
			
			
			
			if ((dataType.equals("completed")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Completed "+ requirementTypeShortName  +"  Requirements as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("incomplete")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s InComplete "+ requirementTypeShortName  +"  Requirements as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("orphan")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Orphan "+ requirementTypeShortName  +"  Requirements as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("dangling")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s Dangling "+ requirementTypeShortName  +"  Requirements as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("suspectUpstream")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s "+ requirementTypeShortName  +"  Requirements with a Suspect Upstream as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("suspectDownstream")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s "+ requirementTypeShortName  +"  Requirements with a Suspect Downstream as of  "
				+ parsedDate ;
			}
			
			
			
			if ((dataType.equals("failedTesting")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s "+ requirementTypeShortName  +"  Requirements that have Failed Testing as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("passedTesting")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s "+ requirementTypeShortName  +"  Requirements that have Passed Testing as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("pendingTesting")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = user.getFirstName() + " " +  user.getLastName() + "'s "+ requirementTypeShortName  +"  Requirements that are Pending Testing as of  "
				+ parsedDate ;
			}

			
		}
		
	%>
	
	<form id='displayListReportDataForm' action='#' method='post'>	 
	<div id = 'displayListReportDiv' class='level1Box'>
	<table class='paddedTable' width='100%'>
		<tr>
			<td align="left">				
				<span class="normalText">
					<b><%=reportTitle%></b>
				</span>
			</td>		
		</tr>

		<tr>
			<td>
			
				<div id ='requirementActions' class='level2Box'>
				<table align='left'>
					<tr>
						<td class='icons'>
						    <a href='/GloreeJava2/servlet/ReportAction?action=exportUserMetricsReportToExcel'
						     target='_blank'>
						    <img src="/GloreeJava2/images/ExportExcel16.gif"  border="0"></a>
			    		</td>
			    	
		        		<td class='icons'>
		        			<span title='Email this data file as an attachment'>
			        		<a href='#' onClick='displayEmailExcelDiv("userRequirements")'>
			        		<img src="/GloreeJava2/images/email16.png"  border="0"></a>
						    </span>
		        		</td>			    		
			    	</tr>
				</table>
				</div>
			</td>
		</tr>		
		<tr>
       		<td >
       			<div id='emailExcelDiv' style="display:none;" class='alert alert-success'>
				</div>
       		</td>
       	</tr>			

		
		
		
		
		
		
		
		
		
		
		
		<tr>
			<td>
	<table class='paddedTable'  width='100%' >
		
		<tr>
			<td>
				<%
				if ((bulkEditResponseString != null ) && !(bulkEditResponseString.equals(""))){
				%>
					<div id ='bulkEditActionResponse' style="display:block;"><%=bulkEditResponseString %></div>
				<%}
				else {%>
					<div id ='bulkEditActionResponse' style="display:none;"></div>
				<%} %>
			</td>
		</tr>
	
		<tr>
			<td align='left'>
				<div style="font-size:8pt; float: left;">			
					<a href='#' onclick="
						document.getElementById('bulkActionCloseDiv').style.display = 'block';
						document.getElementById('workFlowDiv').style.display = 'block';
						document.getElementById('bulkEditActionResponse').style.display = 'none'; 
						">
						Approval WorkFlow Actions
					</a>
					
				</div>
				<div id='bulkActionCloseDiv' style="float: right; display:none">
					<a href='#' onclick="
						document.getElementById('bulkActionCloseDiv').style.display = 'none';
						document.getElementById('workFlowDiv').style.display = 'none';" 
					> 
						Close
					</a>										
				</div>
								
			</td>
		</tr>
		<tr>
			<td>					
						
				<!--  Approval Work Flow  -->
					
				<div id ='workFlowDiv' style="display:none;">
					<table class='paddedTable'  align='left' >
						<tr>
							<td colspan='3' >		
								<input type='button' style="width:150px;height:30px" value='Submit for Acceptance' onclick='bulkEditActionFormForUserDashboard("submitRequirementForApproval","<%=dataType%>","<%=requirementTypeShortName%>")'>
								
							</td>
						</tr>					
						<tr>
							<td colspan='3' >
	        					<input type='button' style="width:150px;height:30px" value='Approve Requirements'  onclick='bulkEditActionFormForUserDashboard("approveRequirement","<%=dataType%>","<%=requirementTypeShortName%>")'>
							</td>
						</tr>
						<tr>
							<td colspan='3' >
								<input type='button' style="width:150px;height:30px" value='Reject Requirements' 
									onclick='
										var approvalNote=prompt("Please enter a reason for rejection (Required)","");
										if ((approvalNote!=null) && (approvalNote.length > 0 )){
											bulkEditActionFormForUserDashboard("rejectRequirement" + ":##:" + approvalNote,"<%=dataType%>","<%=requirementTypeShortName%>")
										}
										else {
		        							alert("You must provide a reason for rejecting these requirements.");
	        							}
										
										'>
							</td>
						</tr>
					</table>
				</div>
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
					    if (userRequirements != null){
					    	if (userRequirements.size() ==0){
				   	%>
						    		<tr>
						    			<td colspan='6'>
						    				<div class='alert alert-success'>
						    					<span class='normalText'> There are no requirements that match this criteria.
						    					</span>
						    				</div>
						    			</td>
						    		</tr>
						    	
				   	<%
						    	}
					    	Iterator i = userRequirements.iterator();
					    	int j = 0;
					    	String cellStyle = "normalTableCell";
					    	while ( i.hasNext() ) {
					    		Requirement r = (Requirement) i.next();
					    		j++;
					    		if (j > 400) {
					 %>
						    		<tr>
						    			<td colspan='7'>
						    				<div class='alert alert-success'>
						    					<span class='normalText'> We are showing the first 400 requirements. To download the
						    					entire Requirement Set please click 
						    					<a href='/GloreeJava2/servlet/ReportAction?action=exportUserMetricsReportToExcel' target='_blank'>
							    				<img src="/GloreeJava2/images/ExportExcel16.gif"  border="0"></a>
						    					</span>
						    				</div>
						    			</td>
						    		</tr>
					 <%
					 			break;
					    		}
					    		// for the first row, print the header and user defined columns etc..
					    		if (j == 1){
					 %>
									<tr>
										<td class='tableHeader' width='40' align='center' valign='top'>
											<div id='selectAllRequirementsDiv'>
												<span class='sectionHeadingText'>
												<a href='#' onClick="
													document.getElementById('deSelectAllRequirementsDiv').style.display = 'block';
													document.getElementById('selectAllRequirementsDiv').style.display = 'none';
													selectAllRequirementInDisplayReportData();">
												Select </a>
												</span>
											</div>
											<div id='deSelectAllRequirementsDiv' style="display:none;">
												<span class='sectionHeadingText'>
												<a href='#' onClick="
													document.getElementById('deSelectAllRequirementsDiv').style.display = 'none';
													document.getElementById('selectAllRequirementsDiv').style.display = 'block';
													deSelectAllRequirementInDisplayReportData();">
												Deselect </a>
												</span>
											</div>
										</td>

										<td></td>
										<td class='tableHeader' width='350'>
											<span class='sectionHeadingText'>
											Requirement 
											</span>
										</td>
										<td class='tableHeader'>
											<span class='sectionHeadingText'>
											Owner
											</span>
										 </td>
										
										<td class='tableHeader'> 
											<span class='sectionHeadingText'>
											Priority
											</span>
										</td>
										<td class='tableHeader'> 
											<span class='sectionHeadingText'>
											Approval Status 
											</span>
										</td>
										<td class='tableHeader'> 
											<span class='sectionHeadingText'>
											Testing Status 
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
					    			cellStyle = "normalTableCell";	
					    		}
					    		
					    		
					    		// lets get the folderId  based on whether the req is deleted or not.
					    	 	String folderId = "";
					    		if (r.getDeleted() == 0 ){
					    			// not deleted. Hence folderId is the realfolder id.
					    			folderId = Integer.toString(r.getFolderId());
					    		}
					    		else {
					    			// this is a deleted Req. here we create a virtual folderid
					    			// which is -1ReqTypeId.
					    			folderId = "-1:" + r.getRequirementTypeId();
					    		}
					    		
					    		String displayRDInReportDiv = "displayRDInReportDiv" + r.getRequirementId();
					 %>
				 				<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>

								
									<td width='40' align='center' >
					 					<input type='checkbox' name='requirementId' value='<%=r.getRequirementId()%>'>
				 					</td>
				 													


									<td  align='left' valign='top' class='<%=cellStyle%>' width='150px'>
										<table class='paddedTable' border='1' bordercolor='white' >
											<tr>

											
												<td width='20px' align='center'>
													<%if (!(r.getRequirementLockedBy().equals(""))){
													// this requirement is locked. so lets display a lock icon.
													%>
														
														<span class='normalText' title='Requirement locked by <%=r.getRequirementLockedBy()%>'> 
															<img src="/GloreeJava2/images/lock16.png" border="0"> 
														</span>
														
													<%
													}
													else {
													%>
														<span class='normalText' title='Requirement not locked'> 
															&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
														</span>
														
													
													<%
													}
													%>
												</td>

												
												<td width='20px' align='center'>
													<a href="#" onclick= 'displayRequirementDescription(<%=r.getRequirementId()%>
														,"<%=displayRDInReportDiv%>")' title='Preview the Requirement'> 
													<img src="/GloreeJava2/images/search16.png"  border="0">
													</a>
													
												</td>
												

												

												
												
												
												<% if (r.getRequirementTraceTo().length() == 0 ) { %>
													<td title='This requirement is an Orphan, i.e does not trace to Requirements upstream ' width='20px' align='center' style="background-color:lightgray">
														<b><font size='4' color='red'>O</font></b>
													</td>
												<%}
												else if(r.getRequirementTraceTo().contains("(s)")) { %>
													<td title='There is a suspect upstream trace' width='20px' align='center' style="background-color:pink">
														<img src="/GloreeJava2/images/arrow_up.png"> 
													</td>
												<%}
												else { %>
													<td title='All upstream traces are clear' width='20px' align='center' style="background-color:lightgreen">
														<img src="/GloreeJava2/images/arrow_up.png"> 
													</td>
												
												<%} %>
												
												
												
												<% if (r.getRequirementTraceFrom().length() == 0 ) { %>
													<td title='This requirement is a Dangling Requirement i.e does not have downstream traces' width='20px' align='center' style="background-color:lightgray">
														<b><font  size='4' color='red'>D</font></d> 
													</td>
												<%}
													else if(r.getRequirementTraceFrom().contains("(s)")) { %>
													<td title='There is a suspect downstream trace' width='20px' align='center' style="background-color:pink">
														<img src="/GloreeJava2/images/arrow_down.png"> 
													</td>
												<%}
													else {%>
													<td title='All downstream traces are clear' width='20px' align='center' style="background-color:lightgreen">
														<img src="/GloreeJava2/images/arrow_down.png"> 
													</td>
												
												<%} %>
												
												
												
												<%if (r.getTestingStatus().equals("Pending")){ %>		
													<td title='Testing is Pending' width='20px' align='center' style="background-color:lightgray">
														&nbsp;&nbsp;&nbsp;&nbsp;
													</td>
												<%}
												else if (r.getTestingStatus().equals("Pass")){ %>
													<td title='Testing Passed' width='20px' align='center' style="background-color:lightgreen">
														<img src="/GloreeJava2/images/testingPassed.png"> 
													</td>
												<%}
												else {%>
													<td title='Testing Failed' width='20px' align='center' style="background-color:pink">
														<img src="/GloreeJava2/images/testingFailed.png"> 
													</td>
												
												<%} %>
												
												
												
												
												
												<%if (r.getRequirementPctComplete() == 100){%>
												
													<td title='Percent of work completed'  align='center' style="background-color:lightgreen">
														<%=r.getRequirementPctComplete()%>%
													</td>
												<%}
												else if (r.getRequirementPctComplete() == 0){ %>
													<td title='Percent of work completed'  align='center' style="background-color:lightgray">
														&nbsp;&nbsp;&nbsp;<%=r.getRequirementPctComplete()%>%
													</td>
												
												<%}
												else {%>
													<td title='Percent of work completed'  align='center'style="background-color:pink">
														&nbsp;&nbsp;<%=r.getRequirementPctComplete()%>%
													</td>
												<%}%>												
												
											</tr>
										</table>
									</td>

									
									
									
									




								<td class='<%=cellStyle%>'>
				 						<%
				 						// lets put spacers here for child requirements.
				 						  String req = r.getRequirementFullTag();
				 					   	  int start = req.indexOf(".");
							    		  while (start != -1) {
							    	            start = req.indexOf(".", start+1);
												out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");				    	         
							  	          }
				 						%>							 		
							 		
									
							 			
		 								<a href="#" 
		 								onClick='
											displayFolderInExplorer("<%=folderId %>");
		 									displayFolderContentCenterA("<%=folderId %>");
		 									displayRequirement(<%=r.getRequirementId()%>,"User Dashboard Requirements",<%=folderId %>);
		 									document.getElementById("contentCenterF").style.display="none";
											'>
											<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %></a> 
		 								</span>
							 		</td>
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<%=r.getRequirementOwner()%>
							 			</span>
							 		</td>
							 		
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<%=r.getRequirementPriority()%>
							 			</span>
							 		</td>
							 		
							 		<%
							 		// lets see if this requirement is in a folder that is enabled for approval work flow
							 		String folderIdApprovalCheck = "#" + r.getFolderId() + "#";
							 		if (!(foldersEnabledForApprovalWorkFlow.contains(folderIdApprovalCheck))){
							 			System.out.println("srt not aplicable req  " +r.getRequirementFullTag() );
								 		
							 			%>
							 				<td bgcolor='EBE4F2''>
												<span class='normalText'>
													Not Applicable
												</span>
											</td>
							 			<%
							 		}
							 		else {
							 		%>
										<% if (r.getApprovalStatus().equals("Draft")){ %>
											<td bgcolor='#FFFF66''>
												<span class='normalText'>
													<%=r.getApprovalStatus() %>
												</span>
											</td>										
										<%} %>
										<% if (r.getApprovalStatus().equals("In Approval WorkFlow")){ %>
											<td bgcolor='#99ccff'>
												<span class='normalText'>												
													<%=r.getApprovalStatus() %>
												</span>
											</td>
										<%} %>
										<% if (r.getApprovalStatus().equals("Approved")){ %>
											<td bgcolor='#CCFF99''>
												<span class='normalText'>
													<%=r.getApprovalStatus()%>
												</span>
											</td>
										<%} %>
										<% if (r.getApprovalStatus().equals("Rejected")){ %>
											<td bgcolor='#FFA3AF'>
												<span class='normalText'>
													<%=r.getApprovalStatus()%>
												</span>
											</td>
										<%} %>
										
									<%} 
									%>																			
										<% if (r.getTestingStatus().equals("Pending")){ %>
											<td bgcolor='#FFFF66'>
												<span class='normalText'>
													<%=r.getTestingStatus() %>
												</span>
											</td>										
										<%} %>
										<% if (r.getTestingStatus().equals("Pass")){ %>
											<td bgcolor='#CCFF99'>
												<span class='normalText'>												
													<%=r.getTestingStatus() %>
												</span>
											</td>
										<%} %>
										<% if (r.getTestingStatus().equals("Fail")){ %>
											<td bgcolor='#FFA3AF'>
												<span class='normalText'>
													<%=r.getTestingStatus()%>
												</span>
											</td>
										<%} %>	
									
				 				</tr>
				 				<tr>
				 					<td  class='<%=cellStyle%>'  colspan='7'>
				 						<div id = '<%=displayRDInReportDiv%>'> </div>
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
	</form>
<%}%>