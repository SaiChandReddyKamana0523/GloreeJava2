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
	boolean isMember = false;
	Project project= (Project) session.getAttribute("project");
	
	String foldersEnabledForApprovalWorkFlow = project.getFoldersEnabledForApprovalWorkFlow();
	
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	User user = securityProfile.getUser();

if (isMember){ 
%>
	
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	<%@ page import="java.text.SimpleDateFormat" %>
	<%@ page import="java.util.Calendar" %>
	
	
	
	<%
		int releaseId = Integer.parseInt(request.getParameter("releaseId"));
		Requirement release = new Requirement(releaseId, databaseType);
	
		String requirementTypeShortName = request.getParameter("requirementTypeShortName");
		String showReturn = request.getParameter("showReturn");
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
		ArrayList releaseRequirements = null;
		String defectStatusGroup = "";
		if (dataType.equals("defectStatusGroup")){
			defectStatusGroup = request.getParameter("defectStatusGroup");
			releaseRequirements = ReleaseMetricsUtil.getRequirementsForDefectStatusGroup(
				securityProfile, defectStatusGroup ,"release",releaseId, 
				project.getProjectId(), user, databaseType);
		}
		else {
			releaseRequirements = ReleaseMetricsUtil.getRequirementsForReleaseOrProject(securityProfile,
			releaseId,
			requirementTypeShortName, dataType , project.getProjectId(), cutOffDate, user, databaseType);
		}
		
		RequirementType requirementType = new RequirementType(project.getProjectId(), requirementTypeShortName, user.getEmailId());
		int rootFolderId = requirementType.getRootFolderId();
		Folder rootFolder = new Folder(rootFolderId);
		ArrayList defaultDisplayAttributes = rootFolder.getDefaultDisplayAttributes();
		int folderEnabledForApproval = rootFolder.getIsFolderEnabledForApproval();
		// lets set the arraylist of results in session, so that we can re-use them
		// when we export to Excel.
		session.setAttribute("releaseRequirements", releaseRequirements);
		Date now = new Date();
	    SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm zzz"); 
	    String parsedDate = formatter.format(now); 
	    

	    String releaseTitle = release.getRequirementNameForHTML();
	    if (releaseTitle.length()>50){
	    	releaseTitle = releaseTitle.substring(0,49);
	    }
		String reportTitle = "";
		if ((dataType != null) && (requirementTypeShortName != null)){
			if ((dataType.equals("all")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Release "+ releaseTitle +" as of " 
				+ parsedDate ;
			}
			if ((dataType.equals("changedAfter")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Release "+ releaseTitle +"  that changed after "
				+ cutOffDate + " as of " + parsedDate;
			}
			if ((dataType.equals("draft")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Draft Requirements in Release "+ releaseTitle +" as of "
				+ parsedDate ;
			}
			if ((dataType.equals("pending")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Pending Requirements in Release "+ releaseTitle +"  as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("rejected")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Rejected Requirements in Release "+ releaseTitle +"  as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("approved")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Approved Requirements in Release "+ releaseTitle +"  as of  "
				+ parsedDate ;
			}
			
			
			if ((dataType.equals("pendingBy")) && (requirementTypeShortName.equals("all"))){
				reportTitle = "All Requirements in Release "+ releaseTitle +"  Pending Approval as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("rejectedBy")) && (requirementTypeShortName.equals("all"))){
				reportTitle = "All Rejected Requirements in Release "+ releaseTitle +"  as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("approvedBy")) && (requirementTypeShortName.equals("all"))){
				reportTitle = "All Approved Requirements in Release "+ releaseTitle +"  as of  "
				+ parsedDate ;
			}
			
			
			
			
			if ((dataType.equals("defectStatusGroup")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All " + "Defects in Status " + defectStatusGroup 
				+" in Release "+ releaseTitle +"  as of  " + parsedDate ;
			}
			
			
			if ((dataType.equals("completed")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Completed Requirements in Release "+ releaseTitle +"  as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("incomplete")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All InComplete Requirements in Release "+ releaseTitle +"  as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("orphan")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Orphan Requirements in Release "+ releaseTitle +"  as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("dangling")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Dangling Requirements in Release "+ releaseTitle +"   as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("suspectUpstream")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Release "+ releaseTitle +"  with a Suspect Upstream as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("suspectDownstream")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Release "+ releaseTitle +"  with a Suspect Downstream as of  "
				+ parsedDate ;
			}
			
			
			
			if ((dataType.equals("failedTesting")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Release "+ releaseTitle +"  that have Failed Testing as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("passedTesting")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Release "+ releaseTitle +"  that have Passed Testing as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("pendingTesting")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Release "+ releaseTitle +"  that are Pending Testing as of  "
				+ parsedDate ;
			}


			// lets build report tiles for links coming from Metrics table. These have a Req Type associated with them.
			if ((dataType.equals("all")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All " + requirementTypeShortName  +" Requirements in Release "+ releaseTitle +"  as of " 
				+ parsedDate ;
			}
			if ((dataType.equals("changedAfter")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All " + requirementTypeShortName  +" Requirements in Release "+ releaseTitle +"  that changed after"
				+ cutOffDate + " as of " + parsedDate;
			}
			if ((dataType.equals("draft")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Draft " + requirementTypeShortName  +" Requirements in Release "+ releaseTitle +"  as of "
				+ parsedDate ;
			}
			
			if ((dataType.equals("pending")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Pending " + requirementTypeShortName  +" Requirements in Release "+ releaseTitle +"  as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("rejected")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Rejected " + requirementTypeShortName  +" Requirements in Release "+ releaseTitle +"   as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("approved")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Approved " + requirementTypeShortName  +" Requirements in Release "+ releaseTitle +"  as of  "
				+ parsedDate ;
			}
			
			
			
			
			
			
			if ((dataType.equals("completed")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Completed "+ requirementTypeShortName  +"  Requirements in Release "+ releaseTitle +"  as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("incomplete")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All InComplete "+ requirementTypeShortName  +"  Requirements in Release "+ releaseTitle +"  as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("orphan")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Orphan "+ requirementTypeShortName  +"  Requirements in Release "+ releaseTitle +"  as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("dangling")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Dangling "+ requirementTypeShortName  +"  Requirements in Release "+ releaseTitle +"  as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("suspectUpstream")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All "+ requirementTypeShortName  +"  Requirements in Release "+ releaseTitle +"  with a Suspect Upstream as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("suspectDownstream")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All " + requirementTypeShortName  +"  Requirements in Release "+ releaseTitle +"  with a Suspect Downstream as of  "
				+ parsedDate ;
			}
			
			
			
			if ((dataType.equals("failedTesting")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All " +  requirementTypeShortName  +"  Requirements in Release "+ releaseTitle +"  that have Failed Testing as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("passedTesting")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All " +  requirementTypeShortName  +"  Requirements in Release "+ releaseTitle +"  that have Passed Testing as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("pendingTesting")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All " +  requirementTypeShortName  +"  Requirements in Release "+ releaseTitle +"  that are Pending Testing as of  "
				+ parsedDate ;
			}
		}

	%>
	 
	
	<div id = 'displayListReportDiv' class='level1Box'>
	<table class='paddedTable' width='100%'>
		<tr>
		<td align='center'>
			<%if (showReturn != null ){ %>
				<img src="/GloreeJava2/images/return.jpg" width="16" border="0"> &nbsp; 
				<a href='#' 
				onClick='
					document.getElementById("contentCenterB").style.display = "none";
					document.getElementById("contentCenterC").style.display = "none";
					document.getElementById("contentCenterD").style.display = "none";
					document.getElementById("contentCenterE").style.display = "none";
					document.getElementById("contentCenterG").style.display = "none";
					document.getElementById("contentCenterF").style.display = "block";	
				'
				>
					Return to Release Metrics
				</a>
			<%} %>
		</td>
		</tr>
		<tr>
			<td bgcolor="#99ccff" align="left">				
				<span class="subSectionHeadingText">
					<%=reportTitle %>  
					<a name="TopOfDisplayRequirements"></a>
					
				</span>
				
			</td>		
		</tr>
		<tr>
			<td >
			
				<div id ='requirementActions' class='level2Box'>
				<table align='left'>
					<tr>
						<td class='icons'>
						    <a href='/GloreeJava2/servlet/ReportAction?action=exportReleaseMetricsReportToExcel'
						     target='_blank'>
						    <img src="/GloreeJava2/images/ExportExcel16.gif"  border="0"></a>
			    		</td>
		        		<td class='icons'>
		        			<span title='Email this data file as an attachment'>
			        		<a href='#' onClick='displayEmailExcelDiv("releaseRequirements")'>
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
				<div id ='reportData' class='level2Box'>
				<table id = "Report">				
	
					<%
					    if (releaseRequirements != null){
					    	if (releaseRequirements.size() ==0){
				   	%>
						    		<tr>
						    			<td colspan='7'>
						    				<div class='alert alert-success'>
						    					<span class='normalText'> There are no requirements that match this criteria.
						    					</span>
						    				</div>
						    			</td>
						    		</tr>
						    	
				   	<%
						    }
					    	else {
					 %>
							<tr>
					    		<td style='width:200px; text-align:center'  onmouseover="this.style.backgroundColor='pink';" onmouseout="this.style.backgroundColor='white'">
					    			<span class='sectionHeadingText'>Status</span>
					    		</td>
					    		<td style='width:100px; text-align:center' onmouseover="this.style.backgroundColor='pink';" onmouseout="this.style.backgroundColor='white'">
					    			<span class='sectionHeadingText'>Comments</span>
					    		</td>
					    		<td style='width:600px; text-align:left' onmouseover="this.style.backgroundColor='pink';" onmouseout="this.style.backgroundColor='white'">
					    			<span class='sectionHeadingText'><%=rootFolder.getRequirementTypeName()%> Name</span>
					    		</td>
					    		<%
					    		int columnCount = 3 + defaultDisplayAttributes.size();	
								
					    		if (folderEnabledForApproval == 1) {
									columnCount++;
								%>
						    		<td style='width:180px; text-align:center' onmouseover="this.style.backgroundColor='pink';" onmouseout="this.style.backgroundColor='white'">
						    			<span class='sectionHeadingText'>Approval Status</span>
						    		</td>
						    		
					    		<%} %>
					    		<%
					    		// lets add a column for open in a new tab
					    		%>
					    		<td style='width:100px; text-align:center' onmouseover="this.style.backgroundColor='pink';" onmouseout="this.style.backgroundColor='white'">
					    			<span class='sectionHeadingText'>Action</span></td>
					    		<%
								if (defaultDisplayAttributes.size() > 0 ){
									Iterator ddA  = defaultDisplayAttributes.iterator();
									while (ddA.hasNext()){
										String a = (String) ddA.next();
										String[] aStuff = a.split(":##:");
										String aId = aStuff[0];
										String aName = aStuff[1];
										
										 %>		
					 					<td style='width:150px; text-align:left' onmouseover="this.style.backgroundColor='pink';" onmouseout="this.style.backgroundColor='white'">
							    			<span class='sectionHeadingText'><%=aName %></span>
							    		</td>	
									<%
									}
								}
								%>
					    		
					    	</tr>
						 		
					 <%
					    	}
					    	Iterator i = releaseRequirements.iterator();
					    	int j = 0;
					    	String cellStyle = "normalTableCell";
					    	while ( i.hasNext() ) {
					    		Requirement r = (Requirement) i.next();

								String url = ProjectUtil.getURL(request,r.getRequirementId(),"requirement"); 
								
					    		String displayRequirementInFolderDiv = "displayRequirementInFolderDiv" + r.getRequirementId();
					    		j++;
					    		if (j > 400) {
					 %>
						    		<tr>
						    			<td >
						    				<div class='alert alert-success'>
						    					<span class='normalText'> We are showing the first 400 requirements . To download the
						    					entire Requirement Set please click 
						    					<a href='/GloreeJava2/servlet/ReportAction?action=exportProjectMetricsReportToExcel' target='_blank'>
							    				<img src="/GloreeJava2/images/ExportExcel16.gif"  border="0"></a>
						    					</span>
						    				</div>
						    			</td>
						    		</tr>
									 <%
									break;
								}					    		
					    		%>
					    		<tr>
					    				
					    				
					    				<!-- BEGIN COPY PASTING COMMON CODE FROM displayARequirementInRealFolder.jsp -->
									<%
									 if (securityProfile.getPrivileges().contains("readRequirementsInFolder" + r.getFolderId())){
							    		String displayRDInFolderDiv = "displayRDInFolderDiv" + r.getRequirementId();
							
							    		Folder folder = new Folder (r.getFolderId());
							    		int rootFolderEnabledForApproval = folder.getIsFolderEnabledForApproval();
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
													style="cursor:pointer"
													onmouseover="this.style.backgroundColor='pink';" onmouseout="this.style.backgroundColor='white'"
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
									        					<td style='width:180px; background-color:#FFFF66' align='left' style='vertical-align:middle'>
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
			
			
												<td  style='text-align:middle' onmouseover="this.style.backgroundColor='pink';" onmouseout="this.style.backgroundColor='white'"> 
													<span class='normalText'>
														<input type='button'  class='btn btn-sm btn-primary'
															id='openButtion<%=r.getRequirementId()%>' 
															value=' Open In New Tab ' 
															onclick="window.open ('<%=url%>');" 
														>
													</span>
												</td>
													
										<%
		
											if (defaultDisplayAttributes.size() > 0 ){
												Iterator ddA  = defaultDisplayAttributes.iterator();
												while (ddA.hasNext()){
													String a = (String) ddA.next();
													String[] aStuff = a.split(":##:");
													String aId = aStuff[0];
													String aName = aStuff[1];
													
													String uda = r.getUserDefinedAttributes();
													String[]   attribs = uda.split(":##:");
													
		
													for (int k=0; k<attribs.length; k++) {
														String[] attrib = attribs[k].split(":#:");
														// To avoid a array out of bounds exception where the attrib value wasn't filled in
														// we print the cell only if array has 2 items in it.
														String attribValue = "";
														if (attrib.length ==2){
															attribValue = attrib[1];
														}
														if (a.contains(attrib[0])) {
															String title = r.getRequirementFullTag() + "'s " + attrib[0];
														 %>		
								 							<td style='width:200px; text-align:left' onmouseover="this.style.backgroundColor='pink';" onmouseout="this.style.backgroundColor='white'" title="<%=title%>">
									 							<span class='normalText' >
									 								<%=attribValue %>
									 							</span>
									 						</td>
														<%
														}
													}
													
												}
											}
											%>
												</tr>
					
					
												<tr>
												<td colspan='2'></td>
												<td colspan='4'>
													<div id = '<%=displayRDInFolderDiv%>' style='display:none'> </div>
											
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
										
										 <%
										   }
										 %> 
									
					    		<%
					 		}
					    }
					%>
				
		
	</table>
	</div>
<%}%>