<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn  == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page. 
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User user = securityProfile.getUser();
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	} 
	
	if (isMember){

		int requirementId = Integer.parseInt(request.getParameter("requirementId"));
		
		Requirement r = new Requirement(requirementId, databaseType);

		
		
		
		
		
		
		
		
		
		
		
		// <!-- BEGIN COPY PASTING COMMON CODE FROM displayARequirementInRealFolder.jsp -->
		 if (securityProfile.getPrivileges().contains("readRequirementsInFolder" + r.getFolderId())){
			String displayRDInFolderDiv = "displayRDInFolderDiv" + r.getRequirementId();
			String displayRDAlertInFolderDiv = "displayRDAlertInFolderDiv" + r.getRequirementId();

			
    		Folder folder = new Folder (r.getFolderId());
    		int folderEnabledForApproval = folder.getIsFolderEnabledForApproval();
    	
    		String userEmailId = securityProfile.getUser().getEmailId();

    		boolean deleteDisabled = false;
    		if (!(securityProfile.getPrivileges().contains("deleteRequirementsInFolder" 
    				+ folder.getFolderId() ))){
    			deleteDisabled = true;
    		}


    		boolean updateDisabled = false ;
    		if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
    				+ folder.getFolderId()))){
    		
    			updateDisabled = true;
    		}

    		boolean percentageCompleteDisabled = false;
    		int percentageCompletedDriverReqTypeId = project.getPercentageCompletedDriverReqTypeId();
    		if (
    			(percentageCompletedDriverReqTypeId > 0 ) 
    			&&
    			(project.getPercentageCompletedDriverReqTypeId() != folder.getRequirementTypeId())
    		){
    			// If this project has a percentageCompletedDriverReqTypeId value set (>0) 
    			// and this requirement does not belong to the percentage complete driver
    			// the update should be disabled. 
    			percentageCompleteDisabled = true;
    		}
    		    		
			 %>

			<table cellpadding=0 cellspacing=0 class='paddedTable'  width='100%' border='0'>
								
				
 				<tr>

				
				
				
				<td style='width:150px' align='left' valign='top' >
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
									<a href="#" onclick= 'displayRequirementDescription(<%=r.getRequirementId()%>,"<%=displayRDInFolderDiv%>")'
										title='Preview the Requirement'> 
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
				
				






			<%
      				int requirementCommentsCount = r.getRequirementCommentsCount(databaseType);
			%>
			
				<td style='width:40px; cursor:pointer;' 
					title='View or Add Comments' align='center'
					onmouseover="this.style.backgroundColor='pink';" onmouseout="this.style.backgroundColor='white'" 
					onClick='
						handleRequirementComment("<%=r.getRequirementId() %>","<%=r.getFolderId()%>")
					'
				>
						<%=requirementCommentsCount%> <img src="/GloreeJava2/images/comments16.png" border="0">
					
				</td>				
				

				<td
					style="width:600px; cursor:pointer"
					onClick="
						document.getElementById('contentCenterF').style.display='none';
						displayRequirement(<%=r.getRequirementId()%>,'List Folder Contents');
						"
					>
					<%
 						// lets put spacers here for child requirements.
 						  String req = r.getRequirementFullTag();
 					   	  int start = req.indexOf(".");
			    		  while (start != -1) {
			    	            start = req.indexOf(".", start+1);
								out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");				    	         
			  	          }
 						%>
	
 						<span class='normalText' >
 						
						&nbsp;
 						<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %> 
 						</span>
				</td>

						<%
						if (folderEnabledForApproval == 1) {
						if (r.getApprovalStatus().equals("Draft")){ 
						
							if (!updateDisabled){ 
	        				%>
	        					<td style='width:180px; background-color:#FFFF66' align='left'>
									<input type='button'
								 style='height:25px; width:180px'   
								 name='Submit Draft For Approval' value=' Submit Draft For Approval '
									onClick='
										handleRequirementActionOther(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,"submitForApproval");
									'>
								</td> 	
							<%
							}
							else {
								%>
								<td style='width:180px; background-color:#FFFF66' align='left'>
									<span class='normalText' >
								&nbsp;&nbsp;Draft&nbsp;&nbsp;
								</span>
								</td>
								<%		
							}
						}
						
						
					
						if (r.getApprovalStatus().equals("In Approval WorkFlow")){ 
							r.setDaysSinceSubmittedForApproval(databaseType);
							int daysPending  = r.getDaysSinceSubmittedForApproval();
							
							if (r.getApprovers().contains("(P)" + userEmailId)){
								// if the user hasn't acted on this requirement, show the 'Pending by You' button
								%>
									<td style='width:180px; background-color:#99ccff' align='left'>
										<span class='normalText' >												
										<span class='normalText'>
											<input type='button' 
											style='height:25px; width:180px'  
											value='Accept / Reject (<%=daysPending %> days old)' 
											onClick='document.getElementById("approveRejectDiv<%=r.getRequirementId()%>").style.display="block"'>
										</span>
										 
									</span>
									</td>
								<%
							}
							else if (r.getApprovers().contains("(R)" + user.getEmailId())){
								// if the user has rejected the req, show the 'Approve' button.
								// this might help if the user has additional info and can now approve it.
								%>
									<td style='width:180px; background-color:#99ccff' align='left'>
										<span class='normalText' >												
										<span class='normalText'>
											<input type='button' 
											style='height:25px; width:180px'  
											value='Rejected by you' 
											onClick='document.getElementById("approveRejectDiv<%=r.getRequirementId()%>").style.display="block"'>
										</span>
										 
									</span>
									</td>
								<%
							}
							else if (r.getApprovers().contains("(R)" + userEmailId)){
								%>
								<td style='width:180px; background-color:#99ccff' align='left'>
									<span class='normalText' >												
									&nbsp;&nbsp;Rejected By Me&nbsp;&nbsp; 
								</span>
								</td>
								<%
							}
							
							else {
							%>
								<td style='width:180px; background-color:#99ccff' align='left'>
									<span class='normalText'>												
									&nbsp;&nbsp;Pending By Others for <%=daysPending %> days &nbsp;&nbsp; 
								</span>
								</td>
								<%
							}
						
						} 
						if (r.getApprovalStatus().equals("Approved")){ %>
							<td style='width:180px; background-color:#CCFF99' align='left'>
								<span class='normalText' >
								&nbsp;&nbsp;Accepted By All&nbsp;&nbsp;
							</span>
							</td>
						<%} 
						if(r.getApprovalStatus().equals("Rejected")){ %>
							<td style='width:180px; background-color:#FFA3AF' align='left'>
								<span class='normalText'>
								&nbsp;&nbsp;Rejected By All&nbsp;&nbsp;
							</span>
							</td>
						<%} 
					}%>


				<td style='width:200px' style='vertical-alignment:middle' >
				<span class='normalText'>
					<input type='button'  
						id='openButtion<%=r.getRequirementId()%>' style='visibility:hidden'
						value=' Open ' 
						onClick="
	 						document.getElementById('contentCenterF').style.display='none';
	 						displayRequirement(<%=r.getRequirementId()%>,'List Folder Contents');
							"
					
					>
				</span>
					
				<span class='normalText'>
					<input type='button'  
						id='showDetailsButtion<%=r.getRequirementId()%>' style='visibility:hidden'
						value=' Show ' 
						onClick='displayRequirementDetails(<%=r.getRequirementId()%>, <%=r.getFolderId() %>);'
					>
				</span>
					<span class='normalText'>
					<input type='button' id='hideDetailsButtion<%=r.getRequirementId()%>' style='visibility:hidden'
						value=' Hide ' 
						onClick='hideRequirementDetails(<%=r.getRequirementId()%>);'
					>
				</span>
				</td>	
				<td style='width:100px' align='right'>
						<span class='normalText'>
							<select id='requirementAction<%=r.getRequirementId()%>'  
							style='visibility:hidden; width:200px'
							onChange='handleRequirementAction(<%=r.getRequirementId()%>,<%=r.getFolderId()%>);'>
								<option value='-1'>Action</option>
													
								<option value=''></option>
								
								<option value='CIA'>Change Impact Analysis</option>
								
								<option value=''></option>
								<%
								if (!(updateDisabled || percentageCompleteDisabled )) {
									// this means that the user has update permissions on this req
									// and this req type is eligible for setting percentage complete
									// i.e. this is not driven by the system.
									if (r.getRequirementPctComplete() == 100){
										%>
										<option value='MarkInComplete'>Mark Incomplete </option>
										<%	
									}
									else {
										%>
										<option value='MarkComplete'>Mark Completed </option>
										<%
									}
									
								}
								String traceTo = r.getRequirementTraceTo();
								String traceFrom = r.getRequirementTraceFrom();
								if (!(traceTo.equals(""))){
									if(traceTo.contains("(s)")){ %>
									<option value='clearAllTracesTo'>Clear All Upstream Traces </option>
								<% 
									}
								%>
									<option value='deleteAllTracesTo'>Delete All Upstream Traces </option>
								<%
								}
								if (!(traceFrom.equals(""))){
									if(traceFrom.contains("(s)")){ %>
									<option value='clearAllTracesFrom'>Clear All Downstream Traces </option>
								<% 
									}
								%>
									<option value='deleteAllTracesFrom'>Delete All Downstream Traces </option>
								<%
								}
								%>	
								<option value=''></option>
								
								
								<%if (!(deleteDisabled)){
									// the Delete and Purge options show up only if the user is delete enabled.	
								%>
									<option value='Delete'>Delete</option>
									<option value='Purge'>Purge</option>
								<%} %>
								
								<%if (!(updateDisabled)){
									// the Delete and Purge options show up only if the user is delete enabled.	
								%>
									<option value='Move'>Move</option>
								<%} %>
							</select>
						</span>
					</td>
					
					</tr>
					<tr>
					<td colspan='2'></td>
					<td colspan='4'>
						<div id = '<%=displayRDInFolderDiv%>' style='display:none'> </div>
						<div id = '<%=displayRDAlertInFolderDiv%>' style='display:none'> </div>
				
						<div id = 'requirementDetailsDiv<%=r.getRequirementId()%>' style='display:none'> </div>
																	
        				<div id='approveRejectDiv<%=r.getRequirementId()%>' class='alert alert-success' style='display:none'>
        					<div style='float:right'>
        						<a href='#' 
        						onclick="document.getElementById('approveRejectDiv<%=r.getRequirementId()%>').style.display = 'none';">
        						close
        						</a>
        					</div>
        					<br>
        					

        					<table class='paddedTable'>
        					<tr>
        						<td colspan='2'>
		        					<div style='float: left;'>
		        						<span class='headingText'>  
		        							<b>Approval WorkFlow Action</b>
		        						</span> 
		        					</div>		        						
        						</td>
        					</tr>
        					
        					<%
        					// If this user has already rejected this req ,we display a note to that effect
        					// and hide the 'reject' button
        					boolean alreadyRejected = false;
        					boolean stillPending = false;
        					if ((r.getApprovers().contains("(R)" + user.getEmailId()))){
        						alreadyRejected = true;
        					}
        					if ((r.getApprovers().contains("(P)" + user.getEmailId()))){
        						stillPending = true;
        					}
        					
        					%>
        					
        						<tr>
        							<td>
        								<span class='headingText'>
        									Notes
        								</span>
        							</td>
        							<td>
        								<input type="text"  name="approvalNote<%=r.getRequirementId()%>" id="approvalNote<%=r.getRequirementId()%>" size="100" maxlength="100">
        							</td>
        						</tr>
        						<tr>
        							<td colspan='2' align='center'>
        								<span class='normalText'>
											<input type='button' 
											style='height:25px; width:180px'  
											name='approve' value='Accept Now'
											onClick='
											handleRequirementActionOther(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,"approve");
											'>
        								</span>	
        								&nbsp;&nbsp;
        								<%
        								// we show the Reject link , only if the user has not already rejected it.
        								// i.e its still pending.
        								if (stillPending){
        								%>
										<span class='normalText'>
											<input type='button' 
											style='height:25px; width:180px'  
											name='reject' value='Reject Now'
											onClick='
											handleRequirementActionOther(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,"reject");
											'
        									>
        								</span> 
        								
        								<%} %>
        							</td>
        						</tr>
        					</table>
        						
						</div>
							
						
						
					</td>
				</tr>
				
				
				
				
				
				
				
			</table>
	 							


			 <%
			   }
			 %>
<%
		//<!-- END COPY PASTING COMMON CODE FROM displayARequirementInRealFolder.jsp -->
	























	
	} %>		