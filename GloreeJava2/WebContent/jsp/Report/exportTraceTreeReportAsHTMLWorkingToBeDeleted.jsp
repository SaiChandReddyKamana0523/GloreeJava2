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
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
%>
	
	<% 
	
		
		String maxRowsInTraceTreeExceeded = (String) request.getAttribute("maxRowsInTraceTreeExceeded");
		String maxRowsInTraceTreeExceededMessage = "";
		
		
		
		int folderId = Integer.parseInt(request.getParameter("folderId"));
		ArrayList traceTreeReport = (ArrayList) session.getAttribute("traceTreeReportForFolder" + folderId);




		String foldersThatnCanBeReportedDangling = FolderUtil.getFoldersThatCanBeReportedDangling(project.getProjectId());
		String foldersThatnCanBeReportedOrphan = FolderUtil.getFoldersThatCanBeReportedOrphan(project.getProjectId());

		
		// lets get standardDisplay and customAttrib display String from the session attribute
		String standardDisplay = (String) session.getAttribute("traceTreeReportStandardDisplay" + folderId );
		
		// lets get the customAttributes to display
		ArrayList<String> attributeNames = ReportUtil.getAllAttributesInAProject(project);
		ArrayList<String> attributeNamesToDisplay = new ArrayList<String>();
		for (String aN : attributeNames){
			if (standardDisplay.contains( aN + ",")){
				attributeNamesToDisplay.add(aN);
			}
		}
		
		
		String displayRequirementType = (String) session.getAttribute("displayRequirementType" + folderId);
		if (displayRequirementType == null ){
			displayRequirementType = "all";
		}
		
		
		// for pagination, lets set the pageSize.
		int pageSize = 50;
		Integer traceTreeReportRowsPerPage = (Integer) session.getAttribute("traceTreeReportRowsPerPage");
		if (traceTreeReportRowsPerPage != null){
			pageSize = traceTreeReportRowsPerPage.intValue();
		}
		
		// we add 1 to arraysize/pageSize because, int div truncates things.
		int numOfPages = (traceTreeReport.size() / pageSize) + 1 ; 	
		
				
		int pageToDisplay = 1;
		if (request.getParameter("page") != null){
			pageToDisplay = Integer.parseInt(request.getParameter("page"));	
		}
		int pageStartIndex = (pageToDisplay * pageSize) - pageSize;
		int pageEndIndex = pageStartIndex + pageSize;
		if (pageEndIndex > traceTreeReport.size()){
			pageEndIndex = traceTreeReport.size();
		}
		
		String pageString = "";
		for (int i=1;i<=numOfPages;i++){
			if (i == pageToDisplay){
				pageString += "<b>" + i + "</b>&nbsp;&nbsp;|&nbsp;&nbsp;";
			}
			else {
				pageString += "<a href='#' onclick='reportPagination(\"traceTree\"," + folderId + "," +
						i +  ")'> " + i + " </a>" ;
				pageString += "&nbsp;&nbsp;|&nbsp;&nbsp; ";	
			}
		}
		// drop the last nbsp;
		pageString = (String) pageString.subSequence(0,pageString.lastIndexOf("&nbsp;&nbsp;|&nbsp;&nbsp;"));
	
		
	%>
<%@page contentType="text/html;charset=UTF-8"%>

	<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
	<html>
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>TraceCloud - SAAS Agile Scrum Requirements Management - Collaborate, Define, Manage and Deliver your Customer Requirements</title>
 	 <meta name="description" content="Collaboration tools to define, manage and deliver your customer requirements on time and within budget. Significantly improves customer satisfaction ">
	<meta name="keywords" content="free requirements management, saas requirements management tool, online requirements management, doors, requisitepro, customer requirements, shared requirements, tl9000, project management, project requirements, agile, agile requirements management.">

		<!-- Gloree JS and CSS files -->
	
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/css/common.css"> 
	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	
	<script src="/GloreeJava2/js/userAccount.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userDashboard.js?v=20200630"></script>\
	
	
	<!--  Bootstratp  JS and CSS files -->

 	 <script src="/GloreeJava2/js/jquery-3.1.1.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap-tour-standalone.min.js"></script>
	
	 <link href="/GloreeJava2/css/bootstrap.min.css" rel="stylesheet" media="screen">
	
	 
	<link href="/GloreeJava2/css/bootstrap-tour-standalone.min.css" rel="stylesheet">
	 
	</head>
	<body> 
	

	<div id = 'displaytraceTreeReportDiv' class="level2Box" style="background-color:white;	border:2px F9F7E0 ;">
	<table width='100%' >
		<tr>
			<td>
				
				<div id ='traceTreeReporttActions' class='level2Box' style='float: left'>
				<table align='left'>
					<tr>
						<td >
		        			&nbsp;&nbsp;&nbsp;	      
		        			<span title='Number of Requirements returned by this query' class='normalText'>
			        		No Of Reqs : <%=traceTreeReport.size() %>
						    </span>
						    &nbsp;&nbsp;&nbsp;	      
		        		</td>

		        		
		        	</tr>						
				</table>
				</div>
				
			</td>
		</tr>
		<tr>
       		<td >
       			<div id='emailAttachmentDiv' style="display:none;" class='alert alert-success'>
				</div>
       		</td>
       	</tr>			

		<%/* if (!(displayRequirementType.contains("all"))){ 
			session.setAttribute("displayRequirementType", displayRequirementType);
		%>
		<tr>
			<td   valign='bottom'>
			<div id ='listReportBulkActionDiv' class='level2Box' 
				STYLE="background-color:white; " >
				<%@ include file="displayTraceTreeReport_action.jsp"%>
			</div>
			</td>
		</tr>
		
		<%}*/ %>
		<tr>
			<td>
				<div id ='traceTreeReportData' class='level2Box'>
				
				<table class='table ' id = "traceTreeReport" width='100%' STYLE="table-layout:fixed" >				
	
					<%
					if (traceTreeReport != null){
				    	if (traceTreeReport.size() == 0) {
					    	%>
				    		<tr>
				    			<td colspan='6'>
				    				<div class='alert alert-success'>
				    					<span class='normalText'> Your search did not return any Requirements. Please change your search criteria
				    					and try again.
				    					</span>
				    				</div>
				    			</td>
				    		</tr>					    	
					    	
					    	<%
					    		
					    	}
						int j = 0;
				    							

						
						
				    	// lets iterate through the TTR and find all distinct req types.
				    	
				    	/*
				    	ArrayList<String> headers = null;
				    	
				    	if (
				    			(standardDisplay.contains("customAttributes")) 
				    		
				    		){
				    		headers = ReportUtil.getColumnHeadersInTraceTreeReport(traceTreeReport);
				    	}
				    		
				    	*/
				    	
						for (int i=pageStartIndex; i<pageEndIndex;i++){
					   		TraceTreeRow tTR = (TraceTreeRow) traceTreeReport.get(i);
					   		Requirement r = tTR.getRequirement();



					   	
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
							
					   		j++;
					   		
				    		// for the first row, print the header and user defined columns etc..
				    		if (j == 1){
							    			
							%>	    	
								<tr style='border-top-style:none'>
									<% /*if (!(displayRequirementType.contains("all"))){ %>
										<td class='tableHeader' width='40' align='center' valign='top'>
											
										</td>
									<%} */ %>			
									<td class='tableHeader' width='180'>
										<span class='sectionHeadingText'>
										&nbsp;
										</span>
									</td>
									<td class='tableHeader' width='110'>
										<span class='sectionHeadingText'>
										&nbsp;
										</span>
									</td>
									
									<td class='tableHeader' width='250'>
									
									<td class='tableHeader' width='300'> 
										<span class='sectionHeadingText'>
										Requirement (Tag : Version : Name) 
										</span>
									</td>
									<% if (standardDisplay.contains("description")) { %>						
										<td class='tableHeader' width='300'> 
											<span class='sectionHeadingText'>
											Description 
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
									<% if (standardDisplay.contains("createdDate")) {%>
										<td class='tableHeader' width='200'> 
											<span class='sectionHeadingText'>
											Created Date
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
									
									for (String aN : attributeNamesToDisplay){
									%>
									<td class='tableHeader' width='200'> 
										<span class='sectionHeadingText'>
										<%=aN %>
										</span>
									</td>
									<%
									}
									%>
									
																																	
								</tr>				 
			    			
			    			
				    	<%		
				    		}
					   		
				    		
				    		// lets skip displaying this row, if this req was not in the display list
				    		System.out.println("srt dealing with " + r.getRequirementFullTag() + " displayRequirementType is " + displayRequirementType);
				    		boolean shouldDisplay = false;
							if (displayRequirementType.contains("all")) {
								shouldDisplay = true;
								System.out.println("srt added because of all " + r.getRequirementFullTag() ); 
							} else {
								
								// means some display restrictions are in place
								if (displayRequirementType.contains(r.getRequirementTypeId() + ",")){
									shouldDisplay = true;
									System.out.println("srt added because of matching display specific one " + r.getRequirementFullTag() );
								}
								else {
									System.out.println("srt display did not match so wont add " + r.getRequirementFullTag() );
								}
							}
					   		if (!shouldDisplay){continue;}
					   		
				    		// lets color code the traceTo and traceFrom values.
				    		String[] traces = r.getRequirementTraceTo().split(",");
				    		String url = "";
							

							
					    	int cellCount = 0;
					    	
					    	
					    	// since a requirementId can get repeated (Imaging a TR-1 tracing to Fr-1 and fr-2, in this trace tree
					    	// TR-1 will get repeated twice. we want displayRDInReportDiv to hvae unique ids
					    	// so, lets add a row num to it.
					    	// 
							String displayRDInReportDiv = "displayRDInReportDiv" + r.getRequirementId() + "-" + j;
					
						 %>
						 
						 <%if (tTR.getLevel()==1){ %>
						 	<!--  This is a root level Req. So, we won't show the arrow	or color-->
						 	<tr>
						 
						 <%} %>
						 
						<%if ((tTR.getLevel() > 1) && (tTR.getTracesToSuspectRequirement() == 0)){%> 
				 			<!--  This is a non-root level Req with a clear trace. -->
				 			<tr class='success' >
				 			
						<%}
			 			if ((tTR.getLevel()>1) && (tTR.getTracesToSuspectRequirement() != 0)){
			 			%>
			 				<!--  This is a non-root level Req with a suspect trace. -->
					 		<tr class='danger' >
						<%}%> 





							<%/* if (!(displayRequirementType.contains("all"))){ %>
								<%cellCount++;%>
								<td width='40' align='center'  style="border-top-style:none" >
				 					<input type='checkbox' name='requirementId' value='<%=r.getRequirementId()%>'>
			 					</td>
		 					<%} */ %>
		 					<%cellCount++;%>
								
								<td style='border-top-style:none'>
									<table class='paddedTable' border='1' style='background-color:<%=color%>; bordercolor=white' >
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

											

												

												
												
												
												<% if (r.getRequirementTraceTo().length() == 0 ) { %>
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
													<td title='Testing is Pending' width='20px' align='center' class='warning'>
														&nbsp;&nbsp;&nbsp;&nbsp; 
													</td>
												<%}
												else if (r.getTestingStatus().equals("Pass")){ %>
													<td title='Testing Passed' width='20px' align='center' class='success'>
														<img src="/GloreeJava2/images/testingPassed.png"> 
													</td>
												<%}
												else {%>
													<td title='Testing Failed' width='20px' align='center' class='danger'>
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
							

								<%url = ProjectUtil.getURL(request,r.getRequirementId(),"requirement"); %>
								<td style='border-top-style:none'>
									
								</td>							
						
							
							<%cellCount++;%>
				 			<td style="border-top-style:none" width='350' NOWRAP>
				 			<%
				 			String spacer = "";

				 			if (tTR.getLevel() >= 1) {
				 				for (int k= 0; k<tTR.getLevel();k++){

					 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
				 				}
				 					
				 			}
				 			
				 			if (tTR.getLevel()==1){
				 			%>
				 			<!--  This is a root level Req. So, we won't show the arrow	or color-->
				 				<%=spacer%> 
				 				<span class='normalText'>
				 					<font class='normalText'>
									<%if (r.getProjectId()== project.getProjectId()){
										out.print(r.getRequirementFullTag());
									}
									else {
										out.print(r.getProjectShortName() + ":" + r.getRequirementFullTag());
									}%>								   						
				 					</font>
				 				</span>
				 			<%}
				 			if ((tTR.getLevel() > 1) && (tTR.getTracesToSuspectRequirement() == 0)){
				 			%> 
				 				<!--  This is a non-root level Req with a clear trace. -->
				 				
				 				<div id='arrowDiv<%=j%>'>
				 					<%=spacer%>
					 				<img src="/GloreeJava2/images/cTrace1.jpg" border="0" title='<%=tTR.getTraceDescriptionWithSafetyInSingleQuotes()%>'>
					 				<span class='normalText'> 
					 					<font color='green'>
										<%if (r.getProjectId()== project.getProjectId()){
											out.print(r.getRequirementFullTag());
										}
										else {
											out.print(r.getProjectShortName() + ":" + r.getRequirementFullTag());
										}%>								   						
					 					</font>
					 				</span>
				 				</div>
				 			<%}
				 			if ((tTR.getLevel()>1) && (tTR.getTracesToSuspectRequirement() != 0)){
				 			%>
				 				<!--  This is a non-root level Req with a suspect trace. -->
				 				<div id='arrowDiv<%=j%>'>
					 				<%=spacer%>
					 				<img src="/GloreeJava2/images/sTrace1.jpg" border="0" title='<%=tTR.getTraceDescriptionWithSafetyInSingleQuotes() %>'>
					 				<span class='normalText'> 
					 					<font color='red'>
										<%if (r.getProjectId()== project.getProjectId()){
											out.print(r.getRequirementFullTag());
										}
										else {
											out.print(r.getProjectShortName() + ":" + r.getRequirementFullTag());	
										}%>								   						
					 					</font>
					 				</span>
				 				</div>
				 			<%}%>
				 			
					 		</td>
												 				


									<!--  Now lets fill the regular columns. -->
									<%cellCount++;%>				 				
							 		<td style="border-top-style:none" >
				 						<%
				 						// lets put spacers here for child requirements.
				 						  String req = r.getRequirementFullTag();
				 					   	  int start = req.indexOf(".");
							    		  while (start != -1) {
							    	            start = req.indexOf(".", start+1);
												out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");				    	         
							  	          }
				 						%>			
											<table width='100%' border='0'>
												<tr>
													<td>
													<span class='normalText'>
														<%=r.getRequirementFullTag()%> : Ver-<%=r.getVersion()%> :  <%=r.getRequirementNameForHTML() %> 
													</span>
													</td>						   						
											</tr>
											</table>
		 								
		 								
							 		</td>
							 		<% if (standardDisplay.contains("description")) {%>
							 			<%cellCount++;%>
								 		<td style="border-top-style:none">
								 			<span class='normalText' title='Requirement Description'>
								 				<%=r.getRequirementDescription() %>
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
								 		<td style="border-top-style:none">
								 			<span class='normalText' title='Owner'>
								 				<%=requirementOwner%>
								 			</span>
								 		</td>
									<%} %>
									<% if (standardDisplay.contains("testingStatus")) {%>
										<%cellCount++;%>							 											
										<%if (r.getTestingStatus().equals("Pending")){ %>
									 		<td  class='warning'>
									 		<span class='normalText' >Pending</span>
									 		</td>
										<%} %> 
										<%if (r.getTestingStatus().equals("Pass")){ %>
											<td  class='success'>
											<span class='normalText' >Pass</span>
											</td>
										<%} %> 
										<%if (r.getTestingStatus().equals("Fail")){ %>
											<td  class='danger'>
											<span class='normalText' >Fail</span>
											</td>
										<%} %> 								 									 		
								 	<%} %>

									
									<% if (standardDisplay.contains("externalURL")) {%>
										<%cellCount++;%>
								 		<td style="border-top-style:none">
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
													<span class='normalText' title='Owner'>												
														<%=r.getApprovalStatus() %>
													</span>
												</td>
											<%} %>
											
											<% if (r.getApprovalStatus().equals("Approved")){ %>
												<%cellCount++;%>
												<td bgcolor='#CCFF99''>
													<span class='normalText' title='Owner'>
														<%=r.getApprovalStatus()%>
													</span>
												</td>
											<%} %>
											<% if (r.getApprovalStatus().equals("Rejected")){ %>
												<%cellCount++;%>
												<td bgcolor='#FFA3AF'>
													<span class='normalText' title='Owner'>
														<%=r.getApprovalStatus()%>
													</span>
												</td>
											<%} %>
										<%} %>
									<%} %>
									


									<% if (standardDisplay.contains("priority")) {%>
										<%cellCount++;%>							 											
								 		<td style="border-top-style:none">
								 			<span class='normalText' title='Priority'>
								 				<%=r.getRequirementPriority()%>
								 			</span>
								 		</td>
								 	<%} %>



									<% if (standardDisplay.contains("percentComplete")) {%>			
										<%cellCount++;%>				 											
								 		<td style="border-top-style:none">
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
												traceToReq = new Requirement(traces[l].replace("(s)", "") , r.getProjectId(), databaseType);
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
								 		<td style="border-top-style:none" >
								 			<span class='normalText' title='Trace To'>
								 				<%=coloredTraceTo %>
								 			</span>
								 		</td>
								 	<%} %>
									<% if (standardDisplay.contains("traceFrom")) {
										String coloredTraceFrom = "<table class='table'>";
							    		traces = r.getRequirementTraceFrom().split(",");
							    		Requirement traceFromReq = null;
							    		for (int l=0;l<traces.length;l++){
							    			
							    			try {
												// if you can get the requirement object, then print more details. If you hit exception print what you have
												traceFromReq = new Requirement(traces[l].replace("(s)", "") , r.getProjectId(), databaseType);
												
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
														coloredTraceFrom += "<tr class='danger'><td> <a href='"+ url + "' target='_blank'>" + traces[l] + "</a> :"+ traceFromReq.getRequirementNameForHTML() +" </td></tr>";
									    			}
									    			else {
									    				coloredTraceFrom += "<tr class='success'> <td> <a href='"+ url + "' target='_blank'>" + traces[l] + "</a> :"+ traceFromReq.getRequirementNameForHTML() +" </td></tr>";
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
						 				<td style="border-top-style:none">
						 					<span class='normalText' title='Trace From'>
						 						<%=coloredTraceFrom %>
						 					</span>
						 				</td>
					 				<%} %>
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
						 				<td style="border-top-style:none">
						 					<span class='normalText' title='Approved By'>
						 						<%=approvedApprovers %>
						 					</span>
						 				</td>
					 				<%} %>
									<% if (standardDisplay.contains("rejectedBy")) {%>	
										<%cellCount++;%>
						 				<td style="border-top-style:none">
						 					<span class='normalText' title='Rejected By'>
						 						<%=rejectedApprovers%>
						 					</span>
						 				</td>
					 				<%} %>
									<% if (standardDisplay.contains("pendingBy")) {%>
										<%cellCount++;%>
						 				<td style="border-top-style:none">
						 					<span class='normalText' title='Pending By'>
						 						<%=pendingApprovers%>
						 					</span>
						 				</td>
					 				<%} %>
					 				<% if (standardDisplay.contains("folderPath")) {%>
										<%cellCount++;%>
						 				<td style="border-top-style:none" title='Folder Path'>
						 					<span class='normalText'>
						 						<%=r.getFolderPath() %>
						 					</span>
						 				</td>
					 				<%} %>
					 				
					 				
					 				<% if (standardDisplay.contains("baselines")) {%>
										<%cellCount++;%>
						 				<td style="border-top-style:none" title='Baselines'>
						 					<span class='normalText'>
						 						<%=r.getRequirementBaselineString(databaseType)%>
						 					</span>
						 				</td>
					 				<%} %>
					 				
					 				
					 				<% if (standardDisplay.contains("createdDate")) {%>
										<%cellCount++;%>
						 				<td style="border-top-style:none" title='Created Date'>
						 					<span class='normalText'>
						 						<%=r.getCreatedDt() %>
						 					</span>
						 				</td>
					 				<%} %>
					 				
					 				<% if (standardDisplay.contains("attachments")) {%>
										<%cellCount++;%>
						 				<td style="border-top-style:none" title='Created Date'>
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

									<% if (standardDisplay.contains("customAttributes")) {
										/*
										for every row : {

											make a map of key  values
											iterate through the 'headers'{
												for each , find the value for this key from this map and print
												REMOVE fro mmap
											}

										}

										After all rows are printed, print the custome header in Excel output
										*/
									
							 				HashMap<String, String> aMap = r.getUserDefinedAttributesHashMap();
							 				
							 				// iterate through known custom headers
							 				//Iterator<String> hI = headers.iterator();
							 				String aValue = "";
							 				for (String aName : attributeNamesToDisplay){
							 					cellCount++;
							 					//aName = hI.next().trim();
							 					boolean attributeExists = false;
							 					if (aMap.containsKey(aName)){
							 						attributeExists = true;
							 					}
							 					aValue = aMap.get(aName);
							 					if (aValue==null){aValue=" &nbsp;&nbsp;&nbsp;";}
							 					String thisAttribLabel = aName;
							 					String attribValue = aValue;
							 					
							 					if (attributeExists){
							 					%>
							 					
							 					<td >
								 					
								 					<div id='attributeDiv-<%=j %>-<%=r.getRequirementId()%>-<%=thisAttribLabel%>' 
								 						class='alert alert-info' title="<%=r.getRequirementFullTag() %>'s  attribute : <%=aName%>"> 
								 						
														<div >
							 								<span class='normalText' >
							 								
							 								<%=attribValue %> &nbsp;
							 								</span>
							 							</div>
						 							</div>
								 					
								 				</td>
								 				
								 				
							 					<%
							 					}
							 					else {
							 						%>
							 						<td >
								 					
								 					
								 				</td>
							 						<%
							 					}
							 				}
							 				%>
									<%} %>
					 						
					 				

				 				</tr>
				 				<tr>
				 					
				 					<td style="border-top-style:none"  colspan='<%=cellCount + 1%>'>
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
	</div> 
	
			
		<script>
		var toggler = document.getElementsByClassName("caret");
		var i;
		
		for (i = 0; i < toggler.length; i++) {
		  toggler[i].addEventListener("click", function() {
		    this.parentElement.querySelector(".nested").classList.toggle("active");
		    this.classList.toggle("caret-down");
		  });
		}
		</script>
	</body>
	</html>
	
<%}%>