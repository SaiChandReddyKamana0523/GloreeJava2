<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>

<%@ page import="com.gloree.utils.*" %>


<%
	// authentication only
	String displayListReportDataIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayListReportDataIsLoggedIn == null) || (displayListReportDataIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");

	String foldersEnabledForApprovalWorkFlow = project.getFoldersEnabledForApprovalWorkFlow();

	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	User user = securityProfile.getUser();
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
%> 

	
	
	
	<% 
	
	Date date = new Date();
	
		int folderId = Integer.parseInt(request.getParameter("folderId"));
		ArrayList listReport = (ArrayList) session.getAttribute("listReportForFolder" + folderId);


		
		String foldersThatnCanBeReportedDangling = FolderUtil.getFoldersThatCanBeReportedDangling(project.getProjectId());
		String foldersThatnCanBeReportedOrphan = FolderUtil.getFoldersThatCanBeReportedOrphan(project.getProjectId());
		
		
		
		// lets get standardDisplay and customAttrib display String from the session attribute
		String standardDisplay = (String) session.getAttribute("listReportStandardDisplay" + folderId );
		String customAttributesDisplay = (String) session.getAttribute("listReportCustomAttributesDisplay" + folderId );
		if (customAttributesDisplay == null ) customAttributesDisplay = "";
		String [] customAttributesToDisplay = customAttributesDisplay.split(",");
		
		// for pagination, lets set the pageSize.
		int pageSize = 500;
		Integer listReportRowsPerPage = (Integer) session.getAttribute("listReportRowsPerPage");
		if (listReportRowsPerPage != null){
			pageSize = listReportRowsPerPage.intValue();
		}
		
		// we add 1 to arraysize/pageSize because, int div truncates things.
		int numOfPages = (listReport.size() / pageSize) + 1 ; 	
		
				
		int pageToDisplay = 1;
		if (request.getParameter("page") != null){
			pageToDisplay = Integer.parseInt(request.getParameter("page"));	
		}
		int pageStartIndex = (pageToDisplay * pageSize) - pageSize;
		int pageEndIndex = pageStartIndex + pageSize;
		if (pageEndIndex > listReport.size()){
			pageEndIndex = listReport.size();
		}
		
		String pageString = "";
		for (int i=1;i<=numOfPages;i++){
			if (i == pageToDisplay){
				pageString += "<b>" + i + "</b>&nbsp;&nbsp;|&nbsp;&nbsp;";
			}
			else {
				pageString += "<a href='#' onclick='reportPagination(\"list\"," + folderId + "," +
						i +  ")'> " + i + " </a>" ;
				pageString += "&nbsp;&nbsp;|&nbsp;&nbsp; ";	
			}
		}
		// drop the last nbsp;
		pageString = (String) pageString.subSequence(0,pageString.lastIndexOf("&nbsp;&nbsp;|&nbsp;&nbsp;"));
		
		 date = new Date(); 

	%>
	 
	
	<div id = 'displayListReportDiv' class="level2Box" style="background-color:white;	border:2px F9F7E0 ;">
	<form id='displayListReportDataForm' action='#' method='post'>
	<table width='100%'>
		<tr>
			<td>
				<div id ='requirementActions' class='level2Box' style='float: left;'>
				<table align='left'>
					<tr>
						<td class='icons'>
							<span title='Export the Requirements of this report to MS Excel'>
						    <a href='/GloreeJava2/servlet/ReportAction?action=exportListReportToExcel&includeRevisionHistory=no&folderId=<%=folderId%>'
						     target='_blank'>
						    <img src="/GloreeJava2/images/ExportExcel16.gif"  border="0"></a>
						    </span>
			    		</td>
						<td class='icons'>
							<span title='Export the Requirements , Versions and Comments of this report to MS Excel'>
						    <a href='/GloreeJava2/servlet/ReportAction?action=exportListReportToExcel&includeRevisionHistory=yes&folderId=<%=folderId%>'
						     target='_blank'>
						    <img src="/GloreeJava2/images/calendar16.png"   border="0"></a>
						    </span>
			    		</td>
			    		
		        		<td class='icons'>
		        			<span title='Export the Requirements of this report to MS Word'>
			        		<a href='/GloreeJava2/servlet/ReportAction?action=exportListReportToWord&folderId=<%=folderId%>'
			        		 target='_blank'>
						    <img src="/GloreeJava2/images/ExportWord16.gif"  border="0"></a>
						    </span>
		        		</td>
		        		<td class='icons'>
		        			<span title='Export the Requirements of this report to Adobe PDF'>
			        		<a href='/GloreeJava2/servlet/ReportAction?action=exportListReportToPDF&folderId=<%=folderId%>'
			        		 target='_blank'>
						    <img src="/GloreeJava2/images/ExportPDF16.gif"  border="0"></a>
						    </span>
		        		</td>
		        		<td class='icons'>
		        			<span title='Email this data file as an attachment'>
			        		<a href='#' onClick='displayEmailAttachmentDiv(<%=folderId%>,"list")'>
			        		<img src="/GloreeJava2/images/email16.png"  border="0"></a>
						    </span>
		        		</td>
		        		<% if (project.getEnableTDCS() == 1){
		        		%>
        					<td class='icons'>
			        			<span title='Add to TDCS (TraceCloud Document Control System)'>
				        		<a href='#' onClick='displayTDCSDiv(<%=folderId%>,"list")'>
				        		<img src="/GloreeJava2/images/database_refresh16.png"  border="0"></a>
							    </span>
			        		</td>
				       <%} %>
		        		
		        		<td >
		        			&nbsp;&nbsp;&nbsp;	      
		        			<span title='Number of Requirements returned by this query' class='normalText'>
			        		No Of Reqs : <%=listReport.size() %>
						    </span>
						    &nbsp;&nbsp;&nbsp;	      
		        		</td>
		          		
		        	</tr>
		        						
				</table>
				</div>			
				<div id ='traceTreeReportPagination' class='level2Box' style='float: left;'>
					<span class='headingText'> Page : </span>
					<%=pageString%>
				</div>
			
			<br><br>
       		<div id='displayMorePagesAlertDiv' class='alert alert-danger' style="display:none;">
				Please note that there are <%=numOfPages %> pages of data in this report and you are selecting only 1 page
				<br><br>
				To make all data appear on 1 page, go to 'Modify Report' and increase your 'Rows Per Page' number
			</div>
			

			</td>
		</tr>
		<tr>
       		<td >
       			<div id='emailAttachmentDiv' style="display:none;" class='alert alert-success'>
       			
				</div>
       		</td>
       	</tr>			
       	
		<tr>
			<td>
				<div id ='requirementActions' class='level2Box'>
				
				<table id = "listReport"  width='100%' STYLE="table-layout:fixed">	
					
	
					<%	
					    if (listReport != null){

					    	if (listReport.size() == 0) {
					    	%>
				    		<tr>
				    			<td>
				    				<div class='alert alert-success'>
				    					<span class='normalText'> Your search did not return any Requirements. Please change your search criteria
				    					and try again.
				    					</span>
				    				</div>
				    			</td>
				    		</tr>					    	
					    	
					    	<%
					    		
					    	}
					    	%>
					    	
					    	<%
					    	int j = 0;
					    	String cellStyle = "normalTableCell";
							for (int i=pageStartIndex; i<pageEndIndex;i++){
						   		Requirement r = (Requirement) listReport.get(i);
					    	
						   		String color = "white";
								if (r.getUserDefinedAttributes().toLowerCase().contains("color:#:")){
									color = r.getAttributeValue("color");
									if ((color == null ) || (color.trim().equals("") )){
										color = "white";
									}
								}
								
					    		// a typical uda looks like this 
					    		// Customer:#: SBI:##:Delivery Estimate:#:01/01/12
					    		String uda = r.getUserDefinedAttributes();
								String[] attribs = uda.split(":##:");
								HashMap attribsHashMap = ProjectUtil.getHashMapUDA(r.getUserDefinedAttributes());
								
					    		j++;
					    		// for the first row, print the header and user defined columns etc..
					    		if (j == 1){
					 %>
									<tr >
										<td class='tableHeader' width='40' align='center' valign='top'>
											<div id='selectAllRequirementsDiv'>
												<span class='sectionHeadingText'>
												<% if (numOfPages  == 1 ){ %>
													<a href='#' onClick="
														document.getElementById('deSelectAllRequirementsDiv').style.display = 'block';
														document.getElementById('selectAllRequirementsDiv').style.display = 'none';
														selectAllRequirementInDisplayReportData();">
													Select </a>
												<%} else { %>
													<a href='#' onClick="
															document.getElementById('deSelectAllRequirementsDiv').style.display = 'block';
															document.getElementById('selectAllRequirementsDiv').style.display = 'none';
															selectAllRequirementInDisplayReportData();
															document.getElementById('displayMorePagesAlertDiv').style.display='block';
															">
															
														Select </a>
												<%} %>
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
										<td class='tableHeader' width='100'>	</td>
										<td class='tableHeader' width='180'>	</td>
										
										<td class='tableHeader' width='350'>
											<span class='sectionHeadingText'>
											Requirement (Tag : Version : Name)
											</span>
										 </td>
										

										<% if (standardDisplay.contains("description")) { %>
											<td class='tableHeader' width='350'> 
												<span class='sectionHeadingText'>
												Description 
												</span>
											</td>
										<%} %>
										
										<% if (standardDisplay.contains("comments")) { %>
											<td class='tableHeader' width='350'> 
												<span class='sectionHeadingText'>
												Comments 
												</span>
											</td>
										<%} %>
										<% if (standardDisplay.contains("owner")) { %>
											<td class='tableHeader' width='200'> 
												<span class='sectionHeadingText'>
												Owner 
												</span>
											</td>
										<%} %>
										<% if (standardDisplay.contains("testingStatus")) { %>
											<td class='tableHeader' width='100'>
												<span class='sectionHeadingText'>
												 Testing Status
												 </span>
											</td>
										<%} %>											
										<% if (standardDisplay.contains("externalURL")) { %>
											<td class='tableHeader' width='200'> 
												<span class='sectionHeadingText'>
												External URL 
												</span>
											</td>
										<%} %>										
										<% if (standardDisplay.contains("status")) { %>
											<td class='tableHeader' width='100'> 
												<span class='sectionHeadingText'>
												Approval Status 
												</span>
											</td>
										<%} %>
										<% if (standardDisplay.contains("priority")) { %>
											<td class='tableHeader' width='100'>
												<span class='sectionHeadingText'>
												 Priority
												 </span>
											</td>
										<%} %>										
										<% if (standardDisplay.contains("percentComplete")) { %>										
											<td class='tableHeader' width='100'>
												<span class='sectionHeadingText'>
												 Percent Complete
												 </span>
											</td>
										<%} %>
										<% if (standardDisplay.contains("traceTo")) {%>										
											<td class='tableHeader' width='200'> 
												<span class='sectionHeadingText'>
												Trace To
												</span>
											</td>
										<%} %>
										<% if (standardDisplay.contains("traceFrom")) {%>
											<td class='tableHeader' width='200'> 
												<span class='sectionHeadingText'>
												Trace From
												</span>
											</td>
										<%} %>
										<% if (standardDisplay.contains("approvedBy")) {%>
											<td class='tableHeader' width='200'> 
												<span class='sectionHeadingText'>
												Approved By
												</span>
											</td>
										<%} %>										
										<% if (standardDisplay.contains("rejectedBy")) {%>
											<td class='tableHeader' width='200'> 
												<span class='sectionHeadingText'>
												Rejected By
												</span>
											</td>
										<%} %>			
										<% if (standardDisplay.contains("pendingBy")) {%>
											<td class='tableHeader' width='200'> 
												<span class='sectionHeadingText'>
												Pending By
												</span>
											</td>
										<%} %>
										
										<% if (standardDisplay.contains("dynamicRole")) {%>
											<td class='tableHeader' width='200'> 
												<span class='sectionHeadingText'>
												Dynamic Approval Role
												</span>
											</td>
										<%} %>
										<% if (standardDisplay.contains("lockedBy")) {%>
											<td class='tableHeader' width='200'> 
												<span class='sectionHeadingText'>
												Locked By
												</span>
											</td>
										<%} %>
										
										<% if (standardDisplay.contains("folderPath")) {%>
											<td class='tableHeader' width='200'> 
												<span class='sectionHeadingText'>
												Folder Path
												</span>
											</td>
										<%} %>				
										<% if (standardDisplay.contains("baselines")) {%>
											<td class='tableHeader' width='200'> 
												<span class='sectionHeadingText'>
												Baselines
												</span>
											</td>
										<%} %>			
										<% if (standardDisplay.contains("createdBy")) {%>
											<td class='tableHeader' width='200'> 
												<span class='sectionHeadingText'>
												Created By
												</span>
											</td>
										<%} %>																		

										<% if (standardDisplay.contains("createdDate")) {%>
											<td class='tableHeader' width='200'> 
												<span class='sectionHeadingText'>
												Created Date
												</span>
											</td>
										<%} %>				
										<% if (standardDisplay.contains("lastModifiedBy")) {%>
											<td class='tableHeader' width='200'> 
												<span class='sectionHeadingText'>
												Last Modified By
												</span>
											</td>
										<%} %>																		
																								
										<% if (standardDisplay.contains("lastModifiedDate")) {%>
											<td class='tableHeader' width='200'> 
												<span class='sectionHeadingText'>
												Last Modified Date
												</span>
											</td>
										<%} %>																		
										
										<% if (standardDisplay.contains("attachments")) {%>
											<td class='tableHeader' width='200'> 
												<span class='sectionHeadingText'>
												Attachments
												</span>
											</td>
										<%} %>																		

					<%
									// now to print the custom labels.
									
									for (int k=0; k<customAttributesToDisplay.length; k++) {									
					%>
											<td class='tableHeader' width='200'> 
												<span class='sectionHeadingText'>
												<%=customAttributesToDisplay[k]%>
												</span>
											</td>
					<%										
									}
					%>


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
					    		
					    		// lets color code the traceTo and traceFrom values.
					    		String[] traces = r.getRequirementTraceTo().split(",");
								String url = "";
								
								
					    		
					    		
					    		String displayRDInReportDiv = "displayRDInReportDiv" + r.getRequirementId();
					    		int cellCount = 0;
					 %>
				 				<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  "
									this.style.background='#E5EBFF';
								" 
									onMouseOut=  "
										this.style.background='white';
								"
									
								>
				 					<%cellCount++;%>
									<td width='40' align='center' >
					 					<input type='checkbox' name='requirementId' value='<%=r.getRequirementId()%>'>
				 					</td>
				 					<%cellCount++;%>
									
									
									<%url = ProjectUtil.getURL(request,r.getRequirementId(),"requirement"); %>
									<td >
								
											
									<div class="btn-group">
										
										<!-- 					
									  <button type="button" class="btn btn-primary btn-sm dropdown-toggle" data-toggle="dropdown">
									    Action <span class="caret"></span>
									  </button>
									 	-->
									  <button type="button" class="btn btn-info btn-sm dropdown-toggle" data-toggle="dropdown">
									   <span class="glyphicon glyphicon-cog " style=" color: white"></span> 
									  </button>
									  <ul class="dropdown-menu" role="menu">
									    <li><a href="#" onClick='window.open ("<%=url%>");'>Open in a New Tab</a></li>
									    <li><a href="#" onclick= 'displayRequirementDescription(<%=r.getRequirementId()%>,"<%=displayRDInReportDiv%>")' > Preview Here</a></li>
									    <li><a href="#" onClick='generateReqTemplateReport(<%=r.getRequirementId()%>, "<%=displayRDInReportDiv%>")'>Generate Word Template Report</a></li>
									   </ul>
									   </div>
									</td>
									
									
									
							 		






									<td  align='left'  class='<%=cellStyle%>' width='150px'>
										<table class='paddedTable' border='1' style='background-color:<%=color%>; bordercolor=white'   >
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

												
												<!-- 
												<td width='20px' align='center'>
													<a href="#" onclick= 'displayRequirementDescription(<%=r.getRequirementId()%>
														,"<%=displayRDInReportDiv%>")' title='Preview the Requirement'> 
													<img src="/GloreeJava2/images/search16.png"  border="0">
													</a>
													
												</td>
												 -->
											

												

												
												
												
												<% if (r.getRequirementTraceTo().length() == 0 ) { 
												%>
													<%if (foldersThatnCanBeReportedOrphan.contains(","+ r.getFolderId() + ",")){ %>
														<td title='This requirement is an Orphan, i.e does not trace to Requirements upstream ' width='20px' align='center' style="background-color:lightgray">
															<b><font size='4' color='red'>O</font></b>
														</td>
													<%}
													else {%>
														<td width='20px'>&nbsp;</td>
													<%} %>
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
													<%if (foldersThatnCanBeReportedDangling.contains(","+ r.getFolderId() + ",")){ %>
														<td title='This requirement is a Dangling Requirement i.e does not have downstream traces' width='20px' align='center' style="background-color:lightgray">
															<b><font  size='4' color='red'>D</font></d> 
														</td>
													<%}
													else {%>
														<td width='20px'>&nbsp;</td>
													<%} %>
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
												
												
												<% if (project.getHidePriority() != 1){ %>
													
													<%if (r.getRequirementPriority().equals("High")){%>
													
														<td title='Requirement Priority is High'  align='center' style="background-color:lightgray">
														<font color='red'><b>H</b></font>
														</td>
													<%}
													else if (r.getRequirementPriority().equals("Medium")){%>
														<td title='Requirement Priority is Medium'  align='center' style="background-color:lightgray">
															<font color='blue'><b>M</b></font>
														</td>
													
													<%}
													else {%>
														<td title='Requirement Priority is Low'  align='center'style="background-color:lightgray">
															<font color='black'><b>L</b></font>
														</td>
													<%}%>												
												<%} %>												
												
											</tr>
										</table>
									</td>




										
											
									<td style='cursor:pointer' 
		 									onClick='
						 						document.getElementById("contentCenterE").style.display = "none";
						 						document.getElementById("contentCenterF").style.display = "none";
												displayFolderInExplorer(<%=r.getFolderId()%>);
												displayFolderContentCenterA(<%=r.getFolderId() %>);
												displayRequirement(<%=r.getRequirementId()%>,"Report and Bulk Edit", <%=folderId %>);
											'	
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
				 						<span class='normalText'>		 			
							 			<font color='blue'>
		 								<%=r.getRequirementFullTag()%> : Ver-<%=r.getVersion()%> :  <%=r.getRequirementNameForHTML() %></font>
		 								</span>
							 		</td>
							 	
							 	

							 	
							 		<% if (standardDisplay.contains("description")) {%>
										<%cellCount++;%>
								 		<td class='<%=cellStyle%>'>
								 			<span class='normalText' title='Requirement Description'>
								 				<%=r.getRequirementDescription() %>
								 			</span>
								 		</td>
							 		<%} %>
							 	
							 		<% if (standardDisplay.contains("comments")) {%>
										<%cellCount++;%>
								 		<td class='<%=cellStyle%>'>
								 			<span class='normalText' title='Requirement Description'>
								 				<%=r.getRequirementCommentsTable(databaseType) %>
								 			</span>
								 		</td>
							 		<%} %>
							 	
							 	
									
							 	
							 		<%
							 			// since req Owner is the only user entered field in this table
							 			// we are showing only the first 30 chars.
							 			String requirementOwner = r.getRequirementOwner();
							 			if ((requirementOwner != null) && (requirementOwner.length()>30)){
							 				requirementOwner = requirementOwner.substring(0,20);
							 			}
							 		%>						
									<% if (standardDisplay.contains("owner")) {%>
										<%cellCount++;%>
								 		<td class='<%=cellStyle%>'>
								 			<span class='normalText' title='Owner'>
								 				<%=requirementOwner%>
								 			</span>
								 		</td>
							 		<%} %>
							 		

									<% if (standardDisplay.contains("testingStatus")) {%>
										<%cellCount++;%>							 											
							 			
										 	<%if (r.getTestingStatus().equals("Pending")){ %>
										 		<td bgcolor='#FFFF66'>
										 		<span class='normalText' >Pending</span>
										 		</td>
											<%} %> 
											<%if (r.getTestingStatus().equals("Pass")){ %>
												<td bgcolor='#CCFF99'>
												<span class='normalText' >Pass</span>
												</td>
											<%} %> 
											<%if (r.getTestingStatus().equals("Fail")){ %>
												<td  bgcolor='#FFa3AF'>
												<span class='normalText' >Fail</span>
												</td>
											<%} %> 
								 	<%} %>



									<% if (standardDisplay.contains("externalURL")) {%>
										<%cellCount++;%>
								 		<td class='<%=cellStyle%>'>
								 			<span class='normalText' title='External URL'>
								 				<%=r.getRequirementExternalUrl()%>
								 			</span>
								 		</td>
							 		<%} %>

							 		
									<% if (standardDisplay.contains("status")) {%>							 		
	
										<%
								 		// lets see if this requirement is in a folder that is enabled for approval work flow
								 		String folderIdApprovalCheck = "#" + r.getFolderId() + "#";
								 		if (!(foldersEnabledForApprovalWorkFlow.contains(folderIdApprovalCheck))){
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
												<%cellCount++;%>
												<td bgcolor='#FFFF66''>
													<span class='normalText' title='Status'>
														<%=r.getApprovalStatus() %>
													</span>
												</td>										
											<%} %>
											<% if (r.getApprovalStatus().equals("In Approval WorkFlow")){ %>
												<%cellCount++;%>
												<td bgcolor='#99ccff'>
													<span class='normalText' title='Status'>												
														<%=r.getApprovalStatus() %>
													</span>
												</td>
											<%} %>
											
											<% if (r.getApprovalStatus().equals("Approved")){ %>
												<%cellCount++;%>
												<td bgcolor='#CCFF99''>
													<span class='normalText' title='Status'>
														<%=r.getApprovalStatus()%>
													</span>
												</td>
											<%} %>
											<% if (r.getApprovalStatus().equals("Rejected")){ %>
												<%cellCount++;%>
												<td bgcolor='#FFA3AF'>
													<span class='normalText' title='Status'>
														<%=r.getApprovalStatus()%>
													</span>
												</td>
											<%} %>
										<%} %>
									<%} %>
									


									<% if (standardDisplay.contains("priority")) {%>
										<%cellCount++;%>							 											
								 		<td class='<%=cellStyle%>'>
								 			<span class='normalText' title='Priority'>
								 				<%=r.getRequirementPriority()%>
								 			</span>
								 		</td>
								 	<%} %>



									<% if (standardDisplay.contains("percentComplete")) {%>
										<%cellCount++;%>							 											
								 		<td class='<%=cellStyle%>'>
								 			<span class='normalText' title='Percent Complete'>
								 				<%=r.getRequirementPctComplete()%> %
								 			</span>
								 		</td>
								 	<%} %>

										
									<% if (standardDisplay.contains("traceTo")) {
							    		String coloredTraceTo = "<table class='table'>";
							    		Requirement traceToReq = null;
							    		for (int l=0;l<traces.length;l++){
							    			
							    			
							    			try {
												// if you can get the requirement object, then print more details. If you hit exception print what you have
												traceToReq = new Requirement(traces[l].replace("(s)", "") , project.getProjectId(), databaseType);
												url = ProjectUtil.getURL(request, traceToReq.getRequirementId(),"requirement");
												if (traceToReq.getProjectId() != project.getProjectId()){
													// External Project Trace
													
				 									String alertMessage = "Since this Requirement is in an external project please paste this URL" +
									   						" in a different browser (IE, FireFox).          " + url ;
									   						
									   						
													if (traces[l].contains("(s)")){
														traces[l] = traces[l].replace("(s)", "");
									    				coloredTraceTo += "<tr class='danger'><td style='cursor:pointer' onClick='alert(\" " + alertMessage +  " \");'> "
									    				 + traces[l] + " :" + 	traceToReq.getRequirementNameForHTML() +" </td></tr>";
									    			}
									    			else {
									    				coloredTraceTo += "<tr class='success'><td style='cursor:pointer' onClick='alert(\" " + alertMessage +  " \");'> "
											    				 + traces[l] + " :" + 	traceToReq.getRequirementNameForHTML() +" </td></tr>";
									    			}
												}
												else {
													if (traces[l].contains("(s)")){
														traces[l] = traces[l].replace("(s)", "");
									    				coloredTraceTo += "<tr class='danger'><td> <a href='"+ url + "' target='_blank'>" + traces[l] + "</a> :"+ traceToReq.getRequirementNameForHTML() +" </td></tr>";
									    			}
									    			else {
									    				coloredTraceTo += "<tr class='success'><td> <a href='"+ url + "' target='_blank'>" + traces[l] + "</a> :"+ traceToReq.getRequirementNameForHTML() +" </td></tr>";
									    			}
												}
												
											}
											catch (Exception e){
												// if you run into exception, do the simple way. 
												e.printStackTrace();
												if (traces[l].contains("(s)")){
													traces[l] = traces[l].replace("(s)", "");
								    				coloredTraceTo += "<tr class='danger'> <td>" + traces[l] + "</td></tr>";
								    			}
								    			else {
								    				coloredTraceTo += "<tr class='success'> <td>" + traces[l] + "</td></tr>";
								    			}
											}
							    			
							    			
							    		}
										coloredTraceTo += "</table>";									
									
									
									%>
										<%cellCount++;%>									
								 		<td class='<%=cellStyle%>'>
								 			<span class='normalText' title='Trace To'>
								 				<%=coloredTraceTo %>
								 			</span>
								 		</td>
								 	<%} %>
									<% if (standardDisplay.contains("traceFrom")) {
										
										// there are times when Traceto and TraceFrom strings in the requirement
										// go out of whack with the actual requirements tracing to and tracing from requirements
										// So, we have put in this self correcting code, which gets triggered
										// when a user chooses to run a List Report, with tracefrom selected. 
										// so , hopefully this won't run very often, but when run, it will be a little slow
										// but will self heal.
		
										RequirementUtil.updateTraceInfoForRequirement(r.getRequirementId());
		
											
										String coloredTraceFrom = "<table class='table'>";
							    		traces = r.getRequirementTraceFrom().split(",");
							    		Requirement traceFromReq = null;
							    		

											
							    		for (int l=0;l<traces.length;l++){
							    			
							    			try {
												// if you can get the requirement object, then print more details. If you hit exception print what you have
												
												traceFromReq = new Requirement(traces[l].replace("(s)", "") , project.getProjectId(), databaseType);
												
												
												url = ProjectUtil.getURL(request, traceFromReq.getRequirementId(),"requirement");
												if (traceFromReq.getProjectId() != project.getProjectId()){
													// External Project Trace
													
				 									String alertMessage = "Since this Requirement is in an external project please paste this URL" +
									   						" in a different browser (IE, FireFox).          " + url ;
									   						
									   						
													if (traces[l].contains("(s)")){
														traces[l] = traces[l].replace("(s)", "");
														coloredTraceFrom += "<tr class='danger'><td style='cursor:pointer' onClick='alert(\" " + alertMessage +  " \");'> "
									    				 + traces[l] + " :" + 	traceFromReq.getRequirementNameForHTML() +" </td></tr>";
									    			}
									    			else {
									    				coloredTraceFrom += "<tr class='success'><td style='cursor:pointer' onClick='alert(\" " + alertMessage +  " \");'> "
											    				 + traces[l] + " :" + 	traceFromReq.getRequirementNameForHTML() +" </td></tr>";
									    			}
												}
												else {
													if (traces[l].contains("(s)")){
														traces[l] = traces[l].replace("(s)", "");
														coloredTraceFrom += "<tr class='danger'><td> <a href='"+ url +
																"' target='_blank'>" + traces[l] + "</a> :"+ traceFromReq.getRequirementNameForHTML() +" </td></tr>";
									    			}
									    			else {
									    				coloredTraceFrom += "<tr class='success'> <td> <a href='"+ url +
									    						"' target='_blank'>" + traces[l] + "</a> :"+ traceFromReq.getRequirementNameForHTML() +" </td></tr>";
									    			}
												}
												
											}
											catch (Exception e){
												// if you run into exception, do the simple way. 
												e.printStackTrace();
												if (traces[l].contains("(s)")){
													traces[l] = traces[l].replace("(s)", "");
								    				coloredTraceFrom += "<tr class='danger'> <td>" + traces[l] + "</td></tr>";
								    			}
								    			else {
								    				coloredTraceFrom += "<tr class='success'> <td>" + traces[l] + "</td></tr>";
								    			}
								    			
											}
							    			
							    		}
											

											
										coloredTraceFrom += "</table>";									
									
									
									%>
										<%cellCount++;%>
						 				<td class='<%=cellStyle%>'>
						 					<span class='normalText' title='Trace From'>
						 						<%=coloredTraceFrom %>
						 					</span>
						 				</td>
					 				<%
					 				
									} %>
					 				<%
									    // lets handle the approvers.
									    String pendingApprovers = "";
									    String approvedApprovers = "";
									    String rejectedApprovers = "";
									    
									    String[] approvers = new String[0] ;
									    if ((r.getApprovers()!= null) && (r.getApprovers().contains(","))){
									    	approvers = r.getApprovers().split(",");
									    }
									    
									    
									    for (int k=0;k<approvers.length;k++){
									    	if (approvers[k].contains("(P)")){
									    		pendingApprovers += approvers[k].replace("(P)","") + ", ";
									    	}	
									    	if (approvers[k].contains("(A)")){
									    		approvedApprovers += approvers[k].replace("(A)","") + ", ";
									    	}
									    	if (approvers[k].contains("(R)")){
									    		rejectedApprovers += approvers[k].replace("(R)","") + ", ";
									    	}
									    }
									    
									    // lets drop the last ,
									    if (pendingApprovers.contains(",")){
									    	pendingApprovers = (String) pendingApprovers.subSequence(0,pendingApprovers.lastIndexOf(","));
									    }			    
									    if (approvedApprovers.contains(",")){
									    	approvedApprovers = (String) approvedApprovers.subSequence(0,approvedApprovers.lastIndexOf(","));
									    }
									    if (rejectedApprovers.contains(",")){
									    	rejectedApprovers = (String) rejectedApprovers.subSequence(0,rejectedApprovers.lastIndexOf(","));
									    }
					 				%>
									<% if (standardDisplay.contains("approvedBy")) {%>
										<%cellCount++;%>
						 				<td class='<%=cellStyle%>'>
						 					<span class='normalText' title='Approved By'>
						 						<%=approvedApprovers %>
						 					</span>
						 				</td>
					 				<%} %>
									<% if (standardDisplay.contains("rejectedBy")) {%>
										<td class='<%=cellStyle%>'>
										<%cellCount++;%>
						 					<span class='normalText' title='Rejected By'>
						 						<%=rejectedApprovers%>
						 					</span>
						 				</td>
					 				<%} %>
									<% if (standardDisplay.contains("pendingBy")) {%>
										<%cellCount++;%>
						 				<td class='<%=cellStyle%>' title='Pending By'>
						 					<span class='normalText'>
						 						<%=pendingApprovers%>
						 					</span>
						 				</td>
					 				<%} %>
					 				<% if (standardDisplay.contains("dynamicRole")) {%>
										<%cellCount++;%>
						 				<td class='<%=cellStyle%>' title='Pending By'>
						 					<span class='normalText'>
						 						<%
						 						ArrayList<Role> dynamicRoles = r.getDynamicApprovalRoles();
						 						for (Role role : dynamicRoles){
						 							%>
						 							<%=role.getRoleName() %>&nbsp;&nbsp;(Approval Rank : <%=role.getApprovalRank() %>)						 							<%
						 						}
						 						%>
						 					</span>
						 				</td>
					 				<%} %>
					 				
					 				<% if (standardDisplay.contains("lockedBy")) {%>
										<%cellCount++;%>
						 				<td class='<%=cellStyle%>' title='Locked By'>
						 					<span class='normalText'>
						 						<%=r.getRequirementLockedBy() %>
						 					</span>
						 				</td>
					 				<%} %>
					 				<% if (standardDisplay.contains("folderPath")) {%>
										<%cellCount++;%>
						 				<td class='<%=cellStyle%>' title='Folder Path'>
						 					<span class='normalText'>
						 						<%=r.getFolderPath() %>
						 					</span>
						 				</td>
					 				<%} %>
					 				
					 				
					 				<% if (standardDisplay.contains("baselines")) {%>
										<%cellCount++;%>
						 				<td class='<%=cellStyle%>' title='Baselines'>
						 					<span class='normalText'>
						 						<%=r.getRequirementBaselineString(databaseType)%>
						 					</span>
						 				</td>
					 				<%} %>
					 				

					 				<% if (standardDisplay.contains("createdBy")) {%>
										<%cellCount++;%>
						 				<td class='<%=cellStyle%>' title='Created By'>
						 					<span class='normalText'>
						 						<%=r.getCreatedBy() %>
						 					</span>
						 				</td>
					 				<%} %>
					 				
					 									 				
					 				<% if (standardDisplay.contains("createdDate")) {%>
										<%cellCount++;%>
						 				<td class='<%=cellStyle%>' title='Created Date'>
						 					<span class='normalText'>
						 						<%=r.getCreatedDt() %>
						 					</span>
						 				</td>
					 				<%} %>

					 				<% if (standardDisplay.contains("lastModifiedBy")) {%>
										<%cellCount++;%>
						 				<td class='<%=cellStyle%>' title='Last Modified By'>
						 					<span class='normalText'>
						 						<%=r.getLastModifiedBy() %>
						 					</span>
						 				</td>
					 				<%} %>

					 				
					 				<% if (standardDisplay.contains("lastModifiedDate")) {%>
										<%cellCount++;%>
						 				<td class='<%=cellStyle%>' title='Last Modified Date'>
						 					<span class='normalText'>
						 						<%=r.getLastModifiedDt() %>
						 					</span>
						 				</td>
					 				<%} %>

					 				<% if (standardDisplay.contains("attachments")) {%>
										<%cellCount++;%>
						 				<td class='<%=cellStyle%>' title='Created Date'>
						 					<span class='normalText'>
											<% ArrayList attachments = r.getRequirementAttachments(databaseType);
											if (attachments.size() > 0){  
												Iterator atachmentIterator = attachments.iterator();
												while (atachmentIterator.hasNext()) {
													RequirementAttachment attachment = (RequirementAttachment) atachmentIterator.next();
													if (!(project.getProjectTags().toLowerCase().contains("hide_download"))){ 
													%>	
													<span title='<%=attachment.getTitle()%>'> 
													<a href='/GloreeJava2/servlet/RequirementAction?action=downloadAttachment&attachmentId=<%=attachment.getRequirementAttachmentId()%>'
								     				target='_blank'>
				        					 		<%=attachment.getFileName()%>&nbsp;&nbsp; : &nbsp;&nbsp; <%=attachment.getTitle() %>
				        							</a>
													</span>
													<br>
													<%
													}
													else {
													%>
													<span title='<%=attachment.getTitle()%>'> 
				        					 		<%=attachment.getFileName()%>&nbsp;&nbsp; : &nbsp;&nbsp; <%=attachment.getTitle() %>
													</span>
													<br>
																					
													<% 
													}
												}%>													
													</span>
											<%} %>	
						 					</span>
						 				</td>
					 				<%} %>
					 				

					 <%
										for (int k=0; k<customAttributesToDisplay.length; k++) {
											String thisAttribLabel = customAttributesToDisplay[k];
											String attribValue = (String) attribsHashMap.get(thisAttribLabel.trim());
										
											if ((thisAttribLabel == null || thisAttribLabel.equals(""))){
												// display nothing
											}
											else {
													
											
					 %>		
					 							<%cellCount++;%>
						 						<td class='<%=cellStyle%>'>
						 							
						 							
						 							
						 							<div id='attributeDiv-<%=j %>-<%=r.getRequirementId()%>-<%=thisAttribLabel%>'  > 
														<div onmouseover="document.getElementById('edit-<%=j %>-<%=r.getRequirementId()%>-<%=thisAttribLabel%>').style.visibility='visible'" 
					 										onmouseout="document.getElementById('edit-<%=j %>-<%=r.getRequirementId()%>-<%=thisAttribLabel%>').style.visibility='hidden'">
							 								<span class='normalText' >
							 								<a  style='visibility:hidden' 
							 									id='edit-<%=j %>-<%=r.getRequirementId()%>-<%=thisAttribLabel%>' 
							 									onclick='getAttributeEditForm(<%=j%>, <%=r.getRequirementId() %>,<%=r.getRequirementTypeId() %> ,"<%=thisAttribLabel%>");'>
							 									<img src="/GloreeJava2/images/edit.jpg" border="0" width='25px'>
							 									</a>
							 								<%=attribValue %>
							 								</span>
							 							</div>
						 							</div>
						 						</td>
					<%
											}
										}
					%>
					 
				 				</tr>
				 				<tr>
				 					<td  class='<%=cellStyle%>'  colspan='4'>
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
		<tr>
			<td>
				<div id ='traceTreeReportPagination2' class='level2Box' style='float: left;'>
					<span class='headingText'> Page : </span>
					<%=pageString%>
				</div>
			</td>
		</tr>
	</table>
	</form>
	</div>
<%

	}%>