	<!-- GloreeJava2 -->
	<!-- pageEncoding -->
	<%@page contentType="text/html;charset=UTF-8"%>
	
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<%
		// authentication only
		String displayAllRequirementsInRealFIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
		if ((displayAllRequirementsInRealFIsLoggedIn  == null) || (displayAllRequirementsInRealFIsLoggedIn.equals(""))){
			// this means that the user is not logged in. So lets forward him to the 
			// log in page.
	%>
			<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
	<% }
		String databaseType = this.getServletContext().getInitParameter("databaseType");
		Project project= (Project) session.getAttribute("project");
		SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
		User user = securityProfile.getUser();
		String userEmailId = user.getEmailId();
		
		// lets see if this user is a member of this project.
		// we are leaving this page open to member of this project (which includes admins also)
		boolean isMember = false;
		if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
			isMember = true;
		} 
		
		if (isMember){
	
			Folder folder = (Folder) request.getAttribute("folder");
			
			
			// if the folder object exists, it means a call was made to FolderAction with a request to create a folder.
			// the folder object now contains the data for the newly created folder.
		    if (folder == null) { 
		    	// This means that no new folders were created prior to this call.
		    	String folderIdString = request.getParameter("folderId");
		    	int folderId = Integer.parseInt(folderIdString);
		    	folder = new Folder(folderId);	
		    }
	
			// Delete and Purge buttons are controlled by the 'Delete Requirement' privilege.
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
			
			
			boolean canBeReportedDangling = folder.canBeReportedDangling();
			boolean canBeReportedOrphan = folder.canBeReportedOrphan();
			
			ArrayList requirements = folder.getMyRequirements(project.getProjectId(), databaseType);
			
			
			// for pagination, lets set the pageSize.
			int pageSize = 20;
			
			try {
				// get the session object prefRowsPerPageInteger
				Integer prefRowsPerPageInteger = (Integer) session.getAttribute("prefRowsPerPage");
				
				if (prefRowsPerPageInteger == null ){
					// if null, go ahead get it from db and then set it in the session.
					int prefRowsPerPage = user.getPrefRowsPerPage();
					
					if (prefRowsPerPage == 0 ){
						// if the db brings back 0, then update db to a default 20 value.
						// lets update the db with some default pref value
						prefRowsPerPage = 20;
						user.setPrefRowsPerPage(prefRowsPerPage);
					}
					// lets set the session attribute.
					session.setAttribute("prefRowsPerPage", new Integer(prefRowsPerPage));
					
					pageSize = prefRowsPerPage;
				}
				else {
					// at this point the session attribute has some value.
					pageSize = prefRowsPerPageInteger.intValue();
				}
			}
			catch (Exception e){
				pageSize = 20;
				e.printStackTrace();
			}
			
			try {
				// lets see if we were sent in a newPreferenceFor rowsPerPage.
				int newRowsPerPagePref = 0;
				try {
					newRowsPerPagePref = Integer.parseInt(request.getParameter("newRowsPerPagePref"));
				}
				catch (Exception e){
					// do nothing.
					
				}
				if ((newRowsPerPagePref > 0 ) && (newRowsPerPagePref != pageSize )){
					// oh oh. we were sent in a new pref
				
					user.setPrefRowsPerPage(newRowsPerPagePref);
					
					session.setAttribute("prefRowsPerPage", new Integer(newRowsPerPagePref));
					pageSize = newRowsPerPagePref;
				}
				
			}
			catch (Exception e){
				e.printStackTrace();
			}
			
			// we add 1 to arraysize/pageSize because, int div truncates things.
			int numOfPages = (requirements.size() / pageSize) + 1 ; 	
			
					
			int pageToDisplay = 1;
			if (request.getParameter("page") != null){
				pageToDisplay = Integer.parseInt(request.getParameter("page"));	
			}
			int pageStartIndex = (pageToDisplay * pageSize) - pageSize;
			int pageEndIndex = pageStartIndex + pageSize;
			if (pageEndIndex > requirements.size()){
				pageEndIndex = requirements.size();
			}
			
			
			String pageString = "";
			for (int i=1;i<=numOfPages;i++){
				if (i == pageToDisplay){
					pageString += "<b>" +  i + "</b>&nbsp;&nbsp;";
				}
				else {
					pageString += "<a href='#' onclick='reportPagination(\"requirementsInRealFolder\"," 
						+ folder.getFolderId() + "," +
						i +  ")'> " + i + " </a>" ;
					pageString += "&nbsp;&nbsp;";	
				}
			}
			// drop the last nbsp;
			pageString = (String) pageString.subSequence(0,pageString.lastIndexOf("&nbsp;&nbsp;"));
			
			// lets handle the case of virtual folder parent where the folder does not exist.
			// we don't want to display NULL . so this work around.
			String folderName = "";
			folderName = folder.getFolderName();
			if (folderName == null){
				folderName = "";
			}
			
			
			int folderEnabledForApproval = folder.getIsFolderEnabledForApproval();
		%>
		<div id='displayAllRequirementsDiv' class='invisibleLevel1Box' 
		onMouseOver='
			if (document.getElementById("folderMenuCreateNewDiv") != null) {document.getElementById("folderMenuCreateNewDiv").style.display="none"; }
			if (document.getElementById("folderMenuReportsDiv") != null) {document.getElementById("folderMenuReportsDiv").style.display="none"; }
		'>
			<% if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
					+ folder.getFolderId()))){
				%>
				<table class='paddedTable' >
				
				<tr>
					<td align='left' colspan='2'>				
						<div class='alert alert-success'>	
						<span class='subSectionHeadingText'>
						You do not have READ permissions on this folder. 
						</span>
						</div>
					</td>
				</tr>
				</table>
			<%}
			else {%>
			 
			<table class='paddedTable'   width=100% >
			   <tr>
					
					<td  > 
						<% if (requirements.size() > 0 ){ %>
						<div id = 'folderDetails' class='level2Box' style='float:left'>
							<span class='normalText'>
							
									 <select id='newRowsPerPagePref' 
									 	onChange='displayAllRequirementsInRealFolder(<%=folder.getFolderId() %>);'
									 	style='height:25px;'
									 >
										<%
										for(int i=1;i<11;i++){
											int newRowsPerPagePref = i*10;
												if (newRowsPerPagePref==pageSize){
												%>
													<option value='<%=newRowsPerPagePref%>' SELECTED><%=newRowsPerPagePref %> rows per page </option>
										 		<%
												}
												else {
												%>
													<option value='<%=newRowsPerPagePref%>' ><%=newRowsPerPagePref %> rows per page </option>
										 		<%
												}
										}
										for(int i=2;i<6;i++){
											int newRowsPerPagePref = i*100;
												if (newRowsPerPagePref==pageSize){
												%>
													<option value='<%=newRowsPerPagePref%>' SELECTED><%=newRowsPerPagePref %> rows per page </option>
										 		<%
												}
												else {
												%>
													<option value='<%=newRowsPerPagePref%>' ><%=newRowsPerPagePref %> rows per page </option>
										 		<%
												}
										}
										%>
										</select>
									 Page <%=pageString%>
							</span>
						</div>
						
						<div style='float:right'>
							<span  onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
								<a href='#' onClick='displayFolderMetricsDataTableOnly("<%=folder.getFolderId() %>"); '>
									&nbsp;&nbsp;&nbsp;
									<img border="0" src="/GloreeJava2/images/chart_bar16.png">
									Folder Metrics&nbsp;&nbsp;&nbsp;
								</a>
							</span>
							<span onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
								<a href='#' onClick='createNewListReport("<%=folder.getFolderId() %>"); '>
									&nbsp;&nbsp;&nbsp;
									<img border="0" src="/GloreeJava2/images/report16.png">
									Advanced Filters&nbsp;&nbsp;&nbsp;
								</a>
							</span>
						</div>
						<%} %>
				 
					</td>				
					
				</tr>
			</table>
			<table class='paddedTable'   width=100% >
				<tr>
					<td colspan='3'>
						<div id='displayRequirementsSectionInFolderDiv'>
						
						<table cellpadding=0 cellspacing=0 width='100%'>
				<%
				
				
				    if (requirements != null){
	
				    	for (int i=pageStartIndex; i<pageEndIndex;i++){
				    		Requirement r = (Requirement) requirements.get(i);
							
				    		String color = "black";
							if (r.getUserDefinedAttributes().toLowerCase().contains("color:#:")){
								color = r.getAttributeValue("color");
								if ((color == null ) || (color.trim().equals("") )){
									color = "black";
								}
							}
				    		String displayRDInFolderDiv = "displayRDInFolderDiv" + r.getRequirementId();
				    		String displayRequirementInFolderDiv = "displayRequirementInFolderDiv" + r.getRequirementId();
				    		
				 %>
				 			<tr>
				 				<td>
				 					<div id='<%=displayRequirementInFolderDiv %>'  
					 					style="background-color:white; border-width:thin; border-style:solid; border-color:white"
										onMouseOver=  "
											this.style.background='#E5EBFF';
					 						document.getElementById('requirementActionDiv<%=r.getRequirementId()%>').style.display='block';
					 						document.getElementById('showHideDetailsDiv<%=r.getRequirementId()%>').style.display='block';
					 						document.getElementById('openRequirementButtonDiv<%=r.getRequirementId()%>').style.display='block';
					 						
					 					" 
										onMouseOut=  "
											this.style.background='white';
											document.getElementById('requirementActionDiv<%=r.getRequirementId()%>').style.display='none';
					 						document.getElementById('showHideDetailsDiv<%=r.getRequirementId()%>').style.display='none';
					 						document.getElementById('openRequirementButtonDiv<%=r.getRequirementId()%>').style.display='none';
					 					" 
				 					>
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
													
	
													
	
													
													
													
													<% if (r.getRequirementTraceTo().length() == 0 ) { 
														if (canBeReportedOrphan){
													%>
														<td title='This requirement is an Orphan, i.e does not trace to Requirements upstream ' width='20px' align='center' style="background-color:lightgray">
															<b><font size='4' color='red'>O</font></b>
														</td>
													<%
														}
														else {
														%>
														<td width='20px'></td>
														<%	
														}
													}
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
													
													
													
													<% if (r.getRequirementTraceFrom().length() == 0 ) { 
														if (canBeReportedDangling) {%>
														<td title='This requirement is a Dangling Requirement i.e does not have downstream traces' width='20px' align='center' style="background-color:lightgray">
															<b><font  size='4' color='red'>D</font></d> 
														</td>
													<%
														}
														else {
															%>
															<td width='20px'></td>
															<%
														}
													}
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
									<td style='width:40px' title='View or Add Comments' align='center'  
									onmouseover="style.backgroundColor='pink';" onmouseout="style.backgroundColor='white'" >
										<a href='#' 
											onClick='
												handleRequirementComment("<%=r.getRequirementId() %>","<%=r.getFolderId()%>")'>
											<%=requirementCommentsCount%> <img src="/GloreeJava2/images/comments16.png" border="0">
										 </a>
									</td>							
	
	
									<td>
										<div id='openRequirementButtonDiv<%=r.getRequirementId() %>' style='display:none; float:left'>
					 							<span class='normalText'>
					 								<input type='button' name=' Open ' value='  Open  '
					 								onclick="
					 									document.getElementById('contentCenterF').style.display='none';
					 									displayRequirement(<%=r.getRequirementId()%>,'List Folder Contents');"
					 								>
					 							</span>
					 						</div>		 						
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
					 						<a href="#" onClick="
					 						document.getElementById('contentCenterF').style.display='none';
					 						displayRequirement(<%=r.getRequirementId()%>,'List Folder Contents');
					 						"
					 						style='color:<%=color%>'
					 						
					 						>
					 						
					 						<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %></a> 
					 						</span>
					 						
					 						
									</td>
		 								
		 							<td style='width:180px' align='left'>
		 								<div>
		 								<%
		 								if (folderEnabledForApproval == 1) {
											if (r.getApprovalStatus().equals("Draft")){ 
											%>
												<span class='normalText' style="background-color:#FFFF66">
												&nbsp;&nbsp;Draft&nbsp;&nbsp;
												</span>
											<%} 
											if (r.getApprovalStatus().equals("In Approval WorkFlow")){ 
												r.setDaysSinceSubmittedForApproval(databaseType);
												int daysPending  = r.getDaysSinceSubmittedForApproval();
												
												if (r.getApprovers().contains("(P)" + userEmailId)){
													// if the user hasn't acted on this requirement, show the 'Pending by You' button
													%>
														<span class='normalText' style="background-color:#99ccff">												
															&nbsp;&nbsp;Pending By Me for <%=daysPending %> days &nbsp;&nbsp; 
														</span>
													<%
												}
												else if (r.getApprovers().contains("(R)" + userEmailId)){
													%>
													<span class='normalText' style="background-color:#99ccff">												
														&nbsp;&nbsp;Rejected By Me&nbsp;&nbsp; 
													</span>
													<%
												}
												
												else {
												%>
													<span class='normalText' style="background-color:#99ccff">												
														&nbsp;&nbsp;Pending By Others for <%=daysPending %> days &nbsp;&nbsp; 
													</span>
													<%
												}
											
											} 
											if (r.getApprovalStatus().equals("Approved")){ %>
												<span class='normalText' style="background-color:#CCFF99">
													&nbsp;&nbsp;Approved By All&nbsp;&nbsp;
												</span>
											<%} 
											if(r.getApprovalStatus().equals("Rejected")){ %>
												<span class='normalText' style="background-color:#FFA3AF">
													&nbsp;&nbsp;Rejected By All&nbsp;&nbsp;
												</span>
											<%} 
										}%>
										</div>
		 							</td>	
		 							<td style='width:100px' >
										<div id='showHideDetailsDiv<%=r.getRequirementId()%>' style='display:none'>
			 								<div id='showDetailsButtionDiv<%=r.getRequirementId()%>' >
			 									<span class='normalText'>
													<input type='button' 
														value=' Show Details  ' 
														onClick='displayRequirementDetails(<%=r.getRequirementId()%>, <%=r.getFolderId() %>);'
													>
												</span>
			 								</div>
			 								<div id='hideDetailsButtionDiv<%=r.getRequirementId()%>' style='display:none' >
			 									<span class='normalText'>
													<input type='button' 
														value=' Hide Details  ' 
														onClick='hideRequirementDetails(<%=r.getRequirementId()%>);'
													>
												</span>
			 								</div>
			 								
			 								
			 							</div>
		 							</td>	
									<td style='width:100px' align='right'>
										<div id='requirementActionDiv<%=r.getRequirementId()%>' style='display:none;'>
											<span class='normalText'>
												<select id='requirementAction<%=r.getRequirementId()%>' 
												onChange='handleRequirementAction(<%=r.getRequirementId()%>,<%=r.getFolderId()%>);'>
													<option value='-1'>Action</option>
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
													%>
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
											</div>
										</td>
										
		 							</tr>
		 							<tr>
										<td colspan='2'></td>
										<td>
											<div id = '<%=displayRDInFolderDiv%>' style='display:none'> </div>
										</td>
										<td colspan='2'></td>
									</tr>
		 							<tr>
										<td colspan='2'></td>
										<td>
											<div id = 'requirementDetailsDiv<%=r.getRequirementId()%>' style='display:none'> </div>
										</td>
										<td colspan='2'></td>
									</tr>
									
									
									
									
									
									
									
		 							</table>
		 							</div>
		 						</td>
		 					</tr>
	
				 <%
				    	}
				    }
					if (requirements.size() == 0) {
					
						boolean createRequirements = true;
						if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" + folder.getFolderId()))){
							createRequirements = false;
						}
						
						// lets get the Create Object Name
						
						String createObjectName = "Create ";
						String reqTypeName = folder.getRequirementTypeName();
						if (reqTypeName.contains("Requirement")){
							reqTypeName = reqTypeName.replace("Requirement", "Req");
						}
						
						if (reqTypeName.length() > 16){
							//reqTypeName = reqTypeName.substring(0, 15)  + "...";
							reqTypeName = folder.getRequirementTypeShortName() + " Objects";
						}
						if (reqTypeName.endsWith("s") || reqTypeName.endsWith("S")){
							//do nothing
						}
						else {
							reqTypeName += "s";
						}
						createObjectName  += reqTypeName;
	
					%>
							<tr>
								<td>
									<table>
										<tr>
											<td valign='top'>
												<table><tr><td>
												<span class='normalText'>
													No requirements were found in this folder. 										
												</span>
												</td></tr></table>
											</td>
											<td valign='top'>
												<table>
													<tr>
														<td valign='top'>
															
															<a href='#' 
																onClick='
																	document.getElementById("folderMenuNoReqsCreateNewDiv").style.display="block";'> 
																 <%=createObjectName %>  &nbsp;<img height='12' width='12' src="/GloreeJava2/images/dropDown.jpg" border="0"> </a>
	
														</td>
													</tr>
													<tr>
														<td valign='top'>
															<div  id='folderMenuNoReqsCreateNewDiv' class="folderMenuClass"  style='display:none; 	z-index: 32; background-color: lightblue; border-width: thin; border-color: blue; border-style:solid;'; >
																
																	
																	<table width='100%' class='paddedTable' >
																	
																   
																   
																	<%if (createRequirements) { %>		
																		<tr><td onmouseover="style.backgroundColor='lightyellow';" onmouseout="style.backgroundColor='lightblue'">
																			
																			<a href='#'	onClick='
																				createRequirementForm("<%=folder.getFolderId()%>"); 
																				document.getElementById("folderMenuCreateNewDiv").style.display="none" '>
																				 <img src="/GloreeJava2/images/puzzle16.gif" border="0">
																				 Create a <%=folder.getRequirementTypeShortName() %> 
																		</a>
																		</td></tr>
																  
																  
																  
																		<tr><td onmouseover="style.backgroundColor='lightyellow';" onmouseout="style.backgroundColor='lightblue'">
																			
																			<a href='#'	onClick='createBulkRequirementForm("<%=folder.getFolderId()%>"); document.getElementById("folderMenuCreateNewDiv").style.display="none" '>
																				 <img src="/GloreeJava2/images/table.png" border="0">
																				 Create multiple <%=folder.getRequirementTypeShortName() %> s
																		</a>
																		</td></tr>
																		
	
																		<tr><td onmouseover="style.backgroundColor='lightyellow';" onmouseout="style.backgroundColor='lightblue'">
																			
																			<a href='#'	onClick='importFromExcelForm("<%=folder.getFolderId()%>"); document.getElementById("folderMenuCreateNewDiv").style.display="none"'>
																				 <img src="/GloreeJava2/images/ExportExcel16.gif">
																				 Import <%=folder.getRequirementTypeShortName() %>s  from Excel</a>
																		</td></tr>
	
																		<tr><td onmouseover="style.backgroundColor='lightyellow';" onmouseout="style.backgroundColor='lightblue'">
																			
																			<a href='#'	onClick='createWordTemplateForm("<%=folder.getFolderId()%>"); document.getElementById("folderMenuCreateNewDiv").style.display="none"'>
																				 <img src="/GloreeJava2/images/ExportWord16.gif">
																				 Import <%=folder.getRequirementTypeShortName() %>s from Word 
																				 </a>
																			
																		</td></tr>
																		
																										
																	<%}%>								
																
																	</table>
																
															</div>
	
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
					
					
					<%
					}
				%>
						</table>
						</div>
					</td>
				</tr>
			</table>
			<%} %>
		</div>
		
	<%} %>		