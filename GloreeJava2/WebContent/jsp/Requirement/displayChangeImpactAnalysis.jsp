<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<% 
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	String foldersThantCanBeReportedDangling = FolderUtil.getFoldersThatCanBeReportedDangling(project.getProjectId());
	String foldersThantCanBeReportedOrphan = FolderUtil.getFoldersThatCanBeReportedOrphan(project.getProjectId());
	String enabledForApprovalFolders = FolderUtil.getFoldersThatAreEnabledForApproval(project.getProjectId());
	
	
	
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);
	
	String folderView = request.getParameter("folderView");
	if (folderView == null ){
		folderView = "no";
	}

	
	
	String rDDivName = request.getParameter("rDDivName");
	if (rDDivName == null ){
		rDDivName = "";
	}
	
	int upStreamDepth = 1;
	int downStreamDepth = 1;
	try {
		upStreamDepth = Integer.parseInt(request.getParameter("upStreamDepth") );
		downStreamDepth = Integer.parseInt(request.getParameter("downStreamDepth") );
		
	}
	catch (Exception e){
		// do nothing
	}

	int bustcache = 0;
	try {
		bustcache = Integer.parseInt(request.getParameter("bustcache"));
	}
	catch (Exception e){
		bustcache = 0;
	}
	
	User user = securityProfile.getUser();

	if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
			+ requirement.getFolderId()))){
		return;
	}
	
	int numberOfUpstreamReqsToShow = 1000;
	int numberOfDownstreamReqsToShow = 1000;
	
	ArrayList upStreamCIA = requirement.getUpStreamCIARequirements(securityProfile, upStreamDepth, numberOfUpstreamReqsToShow, databaseType);
	// because upStreamCIA needs to be shown in  a nice trace tree format
	// and because it was built going up the chain, we need to reverse it
	// to get it in the right order.
	Collections.reverse(upStreamCIA);
	ArrayList downStreamCIA = requirement.getDownStreamCIARequirements(securityProfile, downStreamDepth, numberOfDownstreamReqsToShow, databaseType);
	String cellStyle = "normalTableCell";
	int j = 0;
	
	int counter = 0;
	
%>
	<div class='alert alert-info' style=' border: 2px dotted blue; border-radius: 10px;'>
		
		<%
		String printable = request.getParameter("printable");
		if ((printable == null) || (printable.equals(""))) {
		%>
			<div style="float: right;" id="closeCommentsDiv">
				<%if (folderView.equals("no")) { %>
					<a onclick='document.getElementById("requirementPromptDiv").style.display="none"' href="#" class='btn btn-xs btn-danger' style='color:white' > Close </a>
				<%}
				else {%>
					<a onclick='document.getElementById("<%=rDDivName %>").style.display="none"' href="#"  class='btn btn-xs btn-danger' style='color:white'> Close </a>
				<%} %>
				
		 	</div>
		<%} %>
		<table width='100%' class='table '>
			<tr>
				<td  colspan='4' >
					<table  class='table '>
						<tr>
							<td colspan='4' class='info'>
									<b>
									 Change Impact Analysis of <%=requirement.getRequirementFullTag() %>
									 </b> 
									 <a href='/GloreeJava2/jsp/Requirement/CIA/traceMap.jsp?requirementId=<%=requirementId %>' 
									 target='_blank' class='btn btn-xs btn-primary'
									 style='color:white'>Trace Map</a>
								 
							</td>
						</tr>
						<tr>
							<td >
								<span class='normalText'> Up Stream Depth  </span> 
							</td>
							<td >
								<span class='normalText'> <%=upStreamDepth %> Levels </span> 
							</td>
							<td>
								<span class='normalText'>
								
									<%if (folderView.equals("no")) { %>
				 						<select onchange='
											var upStreamDepth = this.options[this.selectedIndex].value;
											displayChangeImpactAnalysisFlex(upStreamDepth, <%=downStreamDepth%>, <%=requirement.getRequirementId()%>)
										'>
			 						<%}
			 						else {%>
				 						<select onchange='
											var upStreamDepth = this.options[this.selectedIndex].value;
											displayChangeImpactAnalysisFlexAllRequirementsInFolder(upStreamDepth, <%=downStreamDepth%>,"<%=rDDivName %>", <%=requirement.getRequirementId()%>)
										'>
		 							<%} %>
									
										<option value='1'>Change Upstream Depth</option>
										<option value='1'>1</option>
										<option value='2'>2</option>
										<option value='3'>3</option>
										<option value='4'>4</option>
									</select>
								
								 </span>
							</td>
						</tr>
						<tr>
							<td >
								<span class='normalText'> Down Stream   </span>
							</td>
							<td >
								<span class='normalText'> <%=downStreamDepth %> Levels </span>
							</td>
							<td>
								<span class='normalText'>  
									<%if (folderView.equals("no")) { %>
										<select onchange='
											var downStreamDepth = this.options[this.selectedIndex].value;
											displayChangeImpactAnalysisFlex(<%=upStreamDepth%>, downStreamDepth, <%=requirement.getRequirementId()%>)
										'>
			 						<%}
			 						else {%>
				 						<select onchange='
											var downStreamDepth = this.options[this.selectedIndex].value;
											displayChangeImpactAnalysisFlexAllRequirementsInFolder(<%=upStreamDepth%>, downStreamDepth, "<%=rDDivName %>", <%=requirement.getRequirementId()%>)
										'>
		 							<%} %>

										<option value='1'>Change Downstream Depth</option>
										<option value='1'>1</option>
										<option value='2'>2</option>
										<option value='3'>3</option>
										<option value='4'>4</option>
									</select>
								
								</span>
							</td>
						</tr>
					</table>
				</td>
			</tr>


			<%if (upStreamCIA.size() > 0){
			%>
			<%
				Iterator i = upStreamCIA.iterator();
				while (i.hasNext()){
					TraceTreeRow traceTreeRow = (TraceTreeRow) i.next();
					Requirement r = traceTreeRow.getRequirement();
					counter++;
					String displayRDInReportDiv = "displayRDInCIADiv" + r.getRequirementId();
					
					
					String spacer = "";
					int spaceCount = 4-traceTreeRow.getLevel();
					for (int s = 0; s<spaceCount; s++){
		 				// we do this magic to make the spacing appear correct in this trace tree.
		 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
					}
					
					
					
					j++;
					if ((j%2) == 0){
						cellStyle = "normalTableCell";
					}
					else {
						cellStyle = "normalTableCell";	
					}

					
			
					boolean updateDisabled = false ;
					if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
							+ r.getFolderId()))){
					
						updateDisabled = true;
					}
					
					boolean percentageCompleteDisabled = false;
					int percentageCompletedDriverReqTypeId = project.getPercentageCompletedDriverReqTypeId();
					if (
						(percentageCompletedDriverReqTypeId > 0 ) 
						&&
						(project.getPercentageCompletedDriverReqTypeId() != r.getRequirementTypeId())
					){
						// If this project has a percentageCompletedDriverReqTypeId value set (>0) 
						// and this requirement does not belong to the percentage complete driver
						// the update should be disabled. 
						percentageCompleteDisabled = true;
					}
					
					
					boolean canBeReportedDangling = false;
					if (foldersThantCanBeReportedDangling.contains("," + r.getFolderId() + ",")){
						canBeReportedDangling  = true;
					}
					boolean canBeReportedOrphan = false;
					if (foldersThantCanBeReportedOrphan.contains("," + r.getFolderId() + ",")){
						canBeReportedOrphan  = true;
					}
					
					boolean folderEnabledForApproval = false;
					if (enabledForApprovalFolders.contains("," + r.getFolderId() + ",")){
						folderEnabledForApproval = true;
					}
					
					
					%>
					<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"	>
						
						
						<td class='<%=cellStyle %>'>
							
							
						
							<%=spacer %><span class='normalText' style="width:50px;">
							<%=r.getRequirementFullTag() %>
							</span>
							
							&nbsp;&nbsp;
							
							
							
						 	<%if (r.getTestingStatus().equals("Pending")){ %>
						 		<span style='background-color: #FFFF66;' class='normalText' >Test Pending </span>
						 	<%} %> 
							<%if (r.getTestingStatus().equals("Pass")){ %>
								<span style='background-color: #CCFF99;' class='normalText' > Test Pass   </span>
							<%} %> 
							<%if (r.getTestingStatus().equals("Fail")){ %>
								<span style='background-color: #FFa3AF;' class='normalText' > Test Fail   </span>
							<%} %> 
			
							&nbsp;&nbsp;							
							
							<%if (r.getProjectId()== project.getProjectId()){
								// this req is in this project. so we can make it clickable / navigable.
							%>								   						
					 			<span class='normalText'>
									<%=r.getRequirementNameForHTML() %> 
								</span>
   							<%}
							else {
								// this req is in an external project. Curently tracecloud can not
								// support more than 1 project per browser.
								String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");
										%>
								<span class='normalText' title="Requirement Name : <%=r.getRequirementNameForHTML() %>">
	   							<a href="#" onClick='
		   							alert("Since this Requirement is in an external project please paste this URL" +
			   						" in a different browser (IE, FireFox).          " +
			   						"<%=url%>");'>
	   							<%=r.getProjectShortName()%>:<%=r.getRequirementFullTag() %> : Ver-<%=r.getVersion()%> :  <%=r.getRequirementNameForHTML() %></a>
	   							
	   							</span>
							<%} %>
								
							<br>
							
							<%=spacer %><span id='spanId<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>'   class='normalText'>
		 						<img height="16" width="16" src="/GloreeJava2/images/folder.png" border="0"> 
		 						 <%=r.getFolderPath() %>
		 						&nbsp;&nbsp;
		 						
		 						
		 						&nbsp;&nbsp;
		 						
		 						<%
		 						String URL = ProjectUtil.getURL(request,r.getRequirementId(),"requirement"); 
								%>
	 						</span>
	 				
	 						
							
							
							<br>
							<div id='arrowDiv<%=requirement.getRequirementFullTag() %>-<%=j%>'>
							<%=spacer %>
							<%
				 			if (traceTreeRow.getTracesToSuspectRequirement() == 0){
					 		%>
					 			<img src="/GloreeJava2/images/cTrace1.jpg" border="0" title='<%=traceTreeRow.getTraceDescriptionWithSafetyInSingleQuotes()%>'>
							<%}
				 			if (traceTreeRow.getTracesToSuspectRequirement() != 0){
						 	%>
						 		<img src="/GloreeJava2/images/sTrace1.jpg" border="0" title='<%=traceTreeRow.getTraceDescriptionWithSafetyInSingleQuotes()%>'>
				 		 	<%} %>
				 		 	</div>
							
						</td>

						<td style='width:180px; align='center' colspan='2'>
							<div id='requirementApprovalStatusDiv<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>'>
								
								
								<%	
										if (folderEnabledForApproval) {
										if (r.getApprovalStatus().equals("Draft")){ 
										
											if (!updateDisabled){ 
					        				%>
					        					
													<input type='button' class='btn btn-warning btn-xs'
												  name='Submit For Acceptance' value=' Submit For Acceptance '
													onClick='
														handleRequirementActionOtherInCIA("<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>",<%=r.getRequirementId()%>,<%=r.getFolderId()%>,"submitForApproval");
													'>
													
											<%
											}
											else {
												%>
												
												<span style='width:180px; background-color:#FFFF66' >
													&nbsp;&nbsp;Draft&nbsp;&nbsp;
												</span>
											
												<%		
											}
										}
										
										
									
										if (r.getApprovalStatus().equals("In Approval WorkFlow")){ 
											r.setDaysSinceSubmittedForApproval(databaseType);
											int daysPending  = r.getDaysSinceSubmittedForApproval();
											
											if (r.getApprovers().contains("(P)" + user.getEmailId())){
												// if the user hasn't acted on this requirement, show the 'Pending by You' button
												%>
																								
														<span style='width:180px; background-color:#99ccff'>
															<input type='button' 
															 style='height:25px; width:180px'  
															 value='Accept / Reject (<%=daysPending %> days old)' 
															onClick='document.getElementById("approveRejectDiv<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>").style.display="block"'>
														</span>
														 
												<%
											}
											else if (r.getApprovers().contains("(R)" + user.getEmailId())){
												// if the user has rejected the req, show the 'Approve' button.
												// this might help if the user has additional info and can now approve it.
												%>
																									
														<span style='width:180px; background-color:#99ccff'>
															<input type='button' 
															 style='height:25px; width:180px'  
															 value='Rejected by you' 
															onClick='document.getElementById("approveRejectDiv<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>").style.display="block"'>
														</span>
														 
												<%
											}
											else if (r.getApprovers().contains("(R)" + user.getEmailId())){
												%>
												
													<span style='width:180px; background-color:#99ccff' >												
													&nbsp;&nbsp;Rejected By Me&nbsp;&nbsp; 
													</span>
												
												<%
											}
											
											else {
											%>
												
													<span style='width:180px; background-color:#99ccff'>												
													Pending Others : <%=daysPending %>days 
												</span>
												
												<%
											}
										
										} 
										if (r.getApprovalStatus().equals("Approved")){ %>
											
												<span style='width:180px; background-color:#CCFF99' >
												&nbsp;&nbsp;Accepted By All&nbsp;&nbsp;
											</span>
											
										<%} 
										if(r.getApprovalStatus().equals("Rejected")){ %>
											
												<span style='width:180px; background-color:#FFA3AF' >
												&nbsp;&nbsp;Rejected By All&nbsp;&nbsp;
											</span>
											
										<%} 
									}
									
								%>
							
							</div>
							
							<div class="btn-group">
															
									  <button type="button" class="btn btn-primary btn-xs dropdown-toggle" data-toggle="dropdown">
									    Action <span class="caret"></span>
									  </button>
									  <ul class="dropdown-menu" role="menu">
									    <li><a href="#" onclick='
											handleRequirementActionInCIA2("CIA","<%=URL %>","<%=folderView%>","<%=rDDivName%>","<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>",<%=r.getRequirementId()%>,<%=r.getFolderId()%>);
										'>Change Impact of <%=r.getRequirementFullTag() %></a></li>
									    
										<li><a href="#" onclick='
											handleRequirementActionInCIA2("OpenInNewTab","<%=URL %>","<%=folderView%>","<%=rDDivName%>","<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>",<%=r.getRequirementId()%>,<%=r.getFolderId()%>);
										' > Open In New Tab </a></li>
										
									   	<li class="divider"></li>
										
										<%
										if (!(updateDisabled || percentageCompleteDisabled )) {
											// this means that the user has update permissions on this req
											// and this req type is eligible for setting percentage complete
											// i.e. this is not driven by the system.
											if (r.getRequirementPctComplete() == 100){
												%>
												<li><a href="#" onclick='
													handleRequirementActionInCIA2("MarkInComplete","<%=URL %>","<%=folderView%>","<%=rDDivName%>","<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>",<%=r.getRequirementId()%>,<%=r.getFolderId()%>);
												' > Mark Incomplete </a></li>
												
												<%	
											}
											else {
												%>
												<li><a href="#" onclick='
													handleRequirementActionInCIA2("MarkComplete","<%=URL %>","<%=folderView%>","<%=rDDivName%>","<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>",<%=r.getRequirementId()%>,<%=r.getFolderId()%>);
												' > Mark Completed </a></li>
												
												<%
											}
											
										}
										%>	
											<%if (traceTreeRow.getTracesToSuspectRequirement() == 0){%> 
								 				<li><a href="#" 
										    		onclick= "modifyTraceInTraceTree('arrowDiv<%=requirement.getRequirementFullTag() %>-<%=j%>','<%=traceTreeRow.getLevel() %>','markSuspectTraceInTraceTree', <%=traceTreeRow.getTraceId() %>, <%=r.getRequirementId() %>)" 
										    		> Mark Suspect </a>
										    	</li>
											<%}
										    else { %>
												<li><a href="#" 
										    		onclick= "modifyTraceInTraceTree('arrowDiv<%=requirement.getRequirementFullTag() %>-<%=j%>','<%=traceTreeRow.getLevel() %>','markClearTraceInTraceTree', <%=traceTreeRow.getTraceId() %>, <%=r.getRequirementId() %>)" 
										    		> Mark Clear</a>
										    	</li>
										    <%} %>
										    
									    	<li><a href="#" 
									    		onclick= "modifyTraceInTraceTree('arrowDiv<%=requirement.getRequirementFullTag() %>-<%=j%>','<%=traceTreeRow.getLevel() %>','deleteTraceInTraceTree', <%=traceTreeRow.getTraceId() %>, <%=r.getRequirementId() %>)" 
									    		> Delete</a>
									    	</li>
									    
									   </ul>

							</div>
							
						</td>
						
					</tr>
					
	 						
	 				<tr>
						<td   colspan = '3'  class='<%=cellStyle%>'>
	 						<div id = '<%=displayRDInReportDiv%>'> </div>
	        				<div id='approveRejectDiv<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>' class='alert alert-success' style='display:none'>
	        					<div style='float:right'>
	        						<a href='#' 
	        						onclick="document.getElementById('approveRejectDiv<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>').style.display = 'none';">
	        						close
	        						</a>
	        					</div>
	        					<br>
	        					<table class='table'>
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
	        								<input type="text"  name="approvalNote<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>" id="approvalNote<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>" size="100" maxlength="100">
	        							</td>
	        						</tr>
	        						<tr>
	        							<td colspan='2' align='center'>
	        								<span class='normalText'>
												<input type='button' 
												 style='height:25px; width:180px'  
												 name='approve' value='Accept Now'
												onClick='												
												handleRequirementActionOtherInCIA("<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>",<%=r.getRequirementId()%>,<%=r.getFolderId()%>,"approve");
												'>
	        								</span>	
	        								&nbsp;&nbsp;
	        								<%
	        								// we show the Reject link , only if the user has not already rejected it.
	        								// i.e its still pending.
	        								if (!(alreadyRejected)){
	        								%>
	        								&nbsp;
											<span class='normalText'>
												<input type='button' 
												 style='height:25px; width:180px'  
												 name='reject' value='Reject Now'
												onClick='
												handleRequirementActionOtherInCIA("<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>", <%=r.getRequirementId()%>,<%=r.getFolderId()%>,"reject");
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
							
 					
			<%
				}
			}
			else {%>
			<tr >
				<td>
				</td>
				<td>
				<span class='normalText'><font color='red'>ORPHAN</font> : No UpStream Requirements exist</span>
				</td>
			</tr>
			
			<%} %>
				














































			<tr style="background-color:#99CCFF">
				
									
				
				
				<td colspan='3'>
					<table>
					<tr >
						<td style="background-color:#99CCFF">
							<% for (int k=0; k<=3; k++){
							// we do this magic to make the spacing appear correct in this trace tree.
							%>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<%	
							}
							%>
							
							<span style="width:50px;" class='normalText'>
							<b>
							<%=requirement.getRequirementFullTag() %> 
							</b>
							</span>
				
					</td>
					<td >
				
						<%if (requirement.getTestingStatus().equals("Pending")){ %>
							<span style='background-color: #FFFF66;' class='normalText' >Test Pending </span>
						<%} %> 
						<%if (requirement.getTestingStatus().equals("Pass")){ %>
							<span style='background-color: #CCFF99;' class='normalText' > Test Pass   </span>
						<%} %> 
						<%if (requirement.getTestingStatus().equals("Fail")){ %>
							<span style='background-color: #FFa3AF;' class='normalText' > Test Fail   </span>
						<%} %> 
						
						&nbsp;&nbsp;
										
						<span class='normalText'>
						<b><u><%=requirement.getRequirementNameForHTML() %></u></b>
						</span>
					</td>
					</tr>
					</table>
				</td>
			</tr>
			
			<tr>
				<td   colspan = '3' style='border-top:none;'  class='<%=cellStyle%>'>
					<div id = 'displayRDInCIADiv<%=requirement.getRequirementId()%>'> </div>
				</td>
			</tr>				 											
											




































			<%if (downStreamCIA.size() > 0){ 	
				Iterator i = downStreamCIA.iterator();
				while (i.hasNext()){
					TraceTreeRow traceTreeRow = (TraceTreeRow) i.next();
					Requirement r = traceTreeRow.getRequirement();
					counter++;
					String displayRDInReportDiv = "displayRDInCIADiv" + r.getRequirementId();
					
					String spacer = "";
					for (int s = 0; s<(4 + traceTreeRow.getLevel()); s++){
		 				// we do this magic to make the spacing appear correct in this trace tree.
		 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
					}
					
					j++;
					if ((j%2) == 0){
						cellStyle = "normalTableCell";
					}
					else {	
						cellStyle = "normalTableCell";	
					}
		
			
			
					boolean updateDisabled = false ;
					if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
							+ r.getFolderId()))){
					
						updateDisabled = true;
					}
					
					boolean percentageCompleteDisabled = false;
					int percentageCompletedDriverReqTypeId = project.getPercentageCompletedDriverReqTypeId();
					if (
						(percentageCompletedDriverReqTypeId > 0 ) 
						&&
						(project.getPercentageCompletedDriverReqTypeId() != r.getRequirementTypeId())
					){
						// If this project has a percentageCompletedDriverReqTypeId value set (>0) 
						// and this requirement does not belong to the percentage complete driver
						// the update should be disabled. 
						percentageCompleteDisabled = true;
					}
					
					
					boolean canBeReportedDangling = false;
					if (foldersThantCanBeReportedDangling.contains("," + r.getFolderId() + ",")){
						canBeReportedDangling  = true;
					}
					boolean canBeReportedOrphan = false;
					if (foldersThantCanBeReportedOrphan.contains("," + r.getFolderId() + ",")){
						canBeReportedOrphan  = true;
					}
					boolean folderEnabledForApproval = false;
					if (enabledForApprovalFolders.contains("," + r.getFolderId() + ",")){
						folderEnabledForApproval = true;
					}
					
					%>
					<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white">
						
						
						
						
						
						
						
						<td class='<%=cellStyle %>'>
							<div id='arrowDiv<%=requirement.getRequirementFullTag() %>-<%=j%>'>
					 			<%=spacer %>
					 			<%
	
					 			
					 			if (traceTreeRow.getTracesToSuspectRequirement() == 0) {
						 		%>
						 			<img src="/GloreeJava2/images/cTrace1.jpg" border="0" title='<%=traceTreeRow.getTraceDescriptionWithSafetyInSingleQuotes()%>'>
								<%}
					 			if (traceTreeRow.getTracesToSuspectRequirement() != 0){
							 	%>
							 		<img src="/GloreeJava2/images/sTrace1.jpg" border="0" title='<%=traceTreeRow.getTraceDescriptionWithSafetyInSingleQuotes()%>'>
					 		 	<%} %>				 				
					 		 	
								<span class='normalText' style="width:50px;">
								<%=r.getRequirementFullTag() %>
								</span>
							</div>
				

							<%=spacer %>
						 	<%if (r.getTestingStatus().equals("Pending")){ %>
						 		<span style='background-color: #FFFF66;' class='normalText' >Test Pending </span>
						 	<%} %> 
							<%if (r.getTestingStatus().equals("Pass")){ %>
								<span style='background-color: #CCFF99;' class='normalText' > Test Pass   </span>
							<%} %> 
							<%if (r.getTestingStatus().equals("Fail")){ %>
								<span style='background-color: #FFa3AF;' class='normalText' > Test Fail   </span>
							<%} %> 
							
							
							&nbsp;&nbsp;
							<%if (r.getProjectId()== project.getProjectId()){
								// this req is in this project. so we can make it clickable / navigable.
							%>								   						
					 			<span class='normalText'>
									<%=r.getRequirementNameForHTML() %> 
								</span>
   							<%}
							else {
								// this req is in an external project. Curently tracecloud can not
								// support more than 1 project per browser.
								String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");
										%>
								<span class='normalText' title="Requirement Name : <%=r.getRequirementNameForHTML() %>">
	   							<a href="#" onClick='
		   							alert("Since this Requirement is in an external project please paste this URL" +
			   						" in a different browser (IE, FireFox).          " +
			   						"<%=url%>");'>
	   							<%=r.getProjectShortName()%>:<%=r.getRequirementFullTag() %> : Ver-<%=r.getVersion()%> :  <%=r.getRequirementNameForHTML() %>
	   							</a>
	   							</span>
							<%} %>
								

							<br>
							
							<%=spacer%><span id='spanId<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>'  class='normalText'>
	 							<img height="16" width="16" src="/GloreeJava2/images/folder.png" border="0"> 
	 							 <%=r.getFolderPath() %>
	 							&nbsp;&nbsp;
		 						
								<%
								String URL = ProjectUtil.getURL(request,r.getRequirementId(),"requirement"); 
								%>
		 					</span>
						</td>
						
						<td colspan='2' style='width:180px; '>
							<div id='requirementApprovalStatusDiv<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>'>
							
								<%	
										if (folderEnabledForApproval) {
										if (r.getApprovalStatus().equals("Draft")){ 
										
											if (!updateDisabled){ 
					        				%>
					        					
													<input type='button' class='btn btn-warning btn-xs'
												  name='Submit For Acceptance' value=' Submit For Acceptance '
													onClick='
														handleRequirementActionOtherInCIA("<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>",<%=r.getRequirementId()%>,<%=r.getFolderId()%>,"submitForApproval");
													'>
												 	
											<%
											}
											else {
												%>
												
												<span  style='width:180px; background-color:#FFFF66' >
													&nbsp;&nbsp;Draft&nbsp;&nbsp;
												</span>
												<%		
											}
										}
										
										
									
										if (r.getApprovalStatus().equals("In Approval WorkFlow")){ 
											r.setDaysSinceSubmittedForApproval(databaseType);
											int daysPending  = r.getDaysSinceSubmittedForApproval();
											
											if (r.getApprovers().contains("(P)" + user.getEmailId())){
												// if the user hasn't acted on this requirement, show the 'Pending by You' button
												%>
																								
														<span style='width:180px; background-color:#99ccff'>
															<input type='button' 
															 style='height:25px; width:180px'  
															 value='Accept / Reject (<%=daysPending %> days old)' 
															onClick='document.getElementById("approveRejectDiv<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>").style.display="block"'>
														</span>
														 
												<%
											}
											else if (r.getApprovers().contains("(R)" + user.getEmailId())){
												// if the user has rejected the req, show the 'Approve' button.
												// this might help if the user has additional info and can now approve it.
												%>
													
																									
														<span style='width:180px; background-color:#99ccff'>
															<input type='button' 
															 style='height:25px; width:180px'  
															 value='Rejected by you' 
															onClick='document.getElementById("approveRejectDiv<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>").style.display="block"'>
														</span>
														 
												<%
											}
											else if (r.getApprovers().contains("(R)" + user.getEmailId())){
												%>
												<span style='width:180px; background-color:#99ccff' >												
													&nbsp;&nbsp;Rejected By Me&nbsp;&nbsp; 
												</span>
											
												<%
											}
											
											else {
											%>
												
												<span style='width:180px; background-color:#99ccff'>												
													&nbsp;&nbsp;Pending By Others for <%=daysPending %> days &nbsp;&nbsp; 
												</span>
												<%
											}
										
										} 
										if (r.getApprovalStatus().equals("Approved")){ %>
											
											<span style='width:180px; background-color:#CCFF99'  >
												&nbsp;&nbsp;Accepted By All&nbsp;&nbsp;
											</span>
																					<%} 
										if(r.getApprovalStatus().equals("Rejected")){ %>
											
											<span style='width:180px; background-color:#FFA3AF' >
												&nbsp;&nbsp;Rejected By All&nbsp;&nbsp;
											</span>
											
										<%} 
									}
								
								%>
						
							</div>
							

							<div class="btn-group">
															
									  <button type="button" class="btn btn-primary btn-xs dropdown-toggle" data-toggle="dropdown">
									    Action <span class="caret"></span>
									  </button>
									  <ul class="dropdown-menu" role="menu">
									    <li><a href="#" onclick='
											handleRequirementActionInCIA2("CIA","<%=URL %>","<%=folderView%>","<%=rDDivName%>","<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>",<%=r.getRequirementId()%>,<%=r.getFolderId()%>);
										'>Change Impact of <%=r.getRequirementFullTag() %></a></li>
									    
										<li><a href="#" onclick='
											handleRequirementActionInCIA2("OpenInNewTab","<%=URL %>","<%=folderView%>","<%=rDDivName%>","<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>",<%=r.getRequirementId()%>,<%=r.getFolderId()%>);
										' > Open In New Tab </a></li>
										
									   	<li class="divider"></li>
										
										<%
										if (!(updateDisabled || percentageCompleteDisabled )) {
											// this means that the user has update permissions on this req
											// and this req type is eligible for setting percentage complete
											// i.e. this is not driven by the system.
											if (r.getRequirementPctComplete() == 100){
												%>
												<li><a href="#" onclick='
													handleRequirementActionInCIA2("MarkInComplete","<%=URL %>","<%=folderView%>","<%=rDDivName%>","<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>",<%=r.getRequirementId()%>,<%=r.getFolderId()%>);
												' > Mark Incomplete </a></li>
												
												<%	
											}
											else {
												%>
												<li><a href="#" onclick='
													handleRequirementActionInCIA2("MarkComplete","<%=URL %>","<%=folderView%>","<%=rDDivName%>","<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>",<%=r.getRequirementId()%>,<%=r.getFolderId()%>);
												' > Mark Completed </a></li>
												
												<%
											}
											
										}
										%>	
										
											<%if (traceTreeRow.getTracesToSuspectRequirement() == 0){%> 
								 				<li><a href="#" 
										    		onclick= "modifyTraceInTraceTree('arrowDiv<%=requirement.getRequirementFullTag() %>-<%=j%>','<%=traceTreeRow.getLevel() %>','markSuspectTraceInTraceTree', <%=traceTreeRow.getTraceId() %>, <%=r.getRequirementId() %>)" 
										    		> Mark Suspect </a>
										    	</li>
											<%}
										    else { %>
												<li><a href="#" 
										    		onclick= "modifyTraceInTraceTree('arrowDiv<%=requirement.getRequirementFullTag() %>-<%=j%>','<%=traceTreeRow.getLevel() %>','markClearTraceInTraceTree', <%=traceTreeRow.getTraceId() %>, <%=r.getRequirementId() %>)" 
										    		> Mark Clear</a>
										    	</li>
										    <%} %>
										    
									    	<li><a href="#" 
									    		onclick= "modifyTraceInTraceTree('arrowDiv<%=requirement.getRequirementFullTag() %>-<%=j%>','<%=traceTreeRow.getLevel() %>','deleteTraceInTraceTree', <%=traceTreeRow.getTraceId() %>, <%=r.getRequirementId() %>)" 
									    		> Delete</a>
									    	</li>
										
									   </ul>

							</div>
							
				
						</td>
		
					</tr>		
	 				<tr>
	 					<td  style='border-top:none;' colspan = '3'  class='<%=cellStyle%>'>
	 						<div id = '<%=displayRDInReportDiv%>'> </div>
	 	       				<div id='approveRejectDiv<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>' class='alert alert-success' style='display:none'>
	        					<div style='float:right'>
	        						<a href='#' 
	        						onclick="document.getElementById('approveRejectDiv<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>').style.display = 'none';">
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
	        								<input type="text"  name="approvalNote<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>" id="approvalNote<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>" size="100" maxlength="100">
	        							</td>
	        						</tr>
	        						<tr>
	        							<td colspan='2' align='center'>
	        								<span class='normalText'>
												<input type='button' 
												 style='height:25px; width:180px'  
												 name='approve' value='Accept Now'
												onClick='
												handleRequirementActionOtherInCIA("<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>",<%=r.getRequirementId()%>,<%=r.getFolderId()%>,"approve");
												'>
	        								</span>	
	        								&nbsp;&nbsp;
	        								<%
	        								// we show the Reject link , only if the user has not already rejected it.
	        								// i.e its still pending.
	        								if (!(alreadyRejected)){
	        								%> 
	        								&nbsp;
											<span class='normalText'>
												<input type='button' 
												 style='height:25px; width:180px'  
												 name='reject' value='Reject Now'
												onClick='
												handleRequirementActionOtherInCIA("<%=counter%>-<%=bustcache%>-<%=r.getRequirementId()%>",<%=r.getRequirementId()%>,<%=r.getFolderId()%>,"reject");
												'
	        									>
	        								</span> 
	        								
	        								<%}
	        								%>
	        							</td>
	        						</tr>
	        					</table>
							</div>
	 						
	 					</td>
	 				</tr>				 											
					
			<%
				}
			}
			else {%>
			<tr>
				<td></td>
				<td>
				<span class='normalText'><font color='red'>DANGLING</font> : No DownStream Requirements exist</span>
				</td>
			</tr>
			
			<%} %>
			
		</table>
	</div>