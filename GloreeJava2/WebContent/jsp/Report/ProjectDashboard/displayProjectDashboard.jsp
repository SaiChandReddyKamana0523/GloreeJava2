<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>


<%
	// authentication only
	String displayListReportIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayListReportIsLoggedIn == null) || (displayListReportIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dRIsMember = false;
	Project project= (Project) session.getAttribute("project");
	SecurityProfile dRSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dRSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		dRIsMember = true;
	}
%>

<%if (dRIsMember){ %>


	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	

	<div id = 'displayReleaseMetricsDiv' class='alert alert-info'>
		<span class='subSectionHeadingText'>
		Dashboard for Project : <%=project.getProjectName() %>
		</span>
	</div>


				
	<div>				
				<ul class="nav nav-tabs">
				  <li class="active">
				  	<a   data-toggle="tab" href="#reportsAndWordDiv"
					 		onclick='
					 			document.getElementById("reportsAndWordDiv").style.display="block";
								document.getElementById("projectMetricsDiv").style.display="none";
								document.getElementById("projectTrendsFilterDiv").style.display="none";
								document.getElementById("projectTrendsDiv").style.display="none";
							'
					>Saved Reports & Word Docs</a></li>
				  <li>
				  	<a data-toggle="tab" href="#projectMetricsDiv" 	
				  			onclick='
					 			document.getElementById("reportsAndWordDiv").style.display="none";
								document.getElementById("projectMetricsDiv").style.display="block";
								document.getElementById("projectTrendsFilterDiv").style.display="none";
								document.getElementById("projectTrendsDiv").style.display="none";
								displayProjectMetricsDataTable();
							'
					>Project Metrics</a></li>
				  
				 	 <li>
				  	<a data-toggle="tab" href="#projectTrendsDiv" 	
				  			onclick='
					 			document.getElementById("reportsAndWordDiv").style.display="none";
								document.getElementById("projectMetricsDiv").style.display="none";
								document.getElementById("projectTrendsFilterDiv").style.display="block";
								document.getElementById("projectTrendsDiv").style.display="block";
								displayProjectMetrics( "trends");
								'
							
					>Project Trends</a></li>
					
					
					
					
					
				  </ul>				
	 
				<div id='reportsAndWordDiv' class="tab-pane fade in active" > 
				
					<table class='paddedTable' width='100%'>
						
						
					
						<tr>
							<td valign='top'>
								<DIV class='alert alert-info'>
								<table  class='table' >
									
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
												onclick='
												var cutOffDateObject = document.getElementById("cutOffDate");
													if (!isValidDate(cutOffDateObject.value)) {
														cutOffDateObject.focus();
														cutOffDateObject.style.backgroundColor="#FFCC99";
													}
													else {										
														displayProjectRequirementsOfAllReqTypes("changedAfter");
													}
												'>
												<img src="/GloreeJava2/images/report16.png" border="0">
												 Reqs in Project Changed After
												</a>  
												<input type='text' name='cutOffDate' id='cutOffDate' size='8' value='mm/dd/yyyy'>
												
											</span>						
										</td>
									</tr>
									
									
			
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
														displayProjectRequirementsOfAllReqTypes("defectStatusGroup");
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
														displayProjectRequirementsOfAllReqTypes("defectStatusGroup");
													}
													'>
													<option> Defect Status</option>
													<%
													ArrayList defectStatusGroupsForProject = ReleaseMetricsUtil.getCurrentDefectStatusGroupsInProject(project.getProjectId());	
													Iterator i = defectStatusGroupsForProject.iterator();
													while (i.hasNext()){
														String defectStatus = (String)i.next();
													%>
														<option value='<%=defectStatus%>'><%=defectStatus%></option>
													<%
													}
													%>
												</select>
											</span>						
										</td>
									</tr>
			
			
			
			
								</table>
								</DIV>
							</td>
							<td valign='top'>
								<DIV class='alert alert-info'>
								<table  class='table' >
									<tr><td width='250' >
										<span class='sectionHeadingText'>
										Public Saved Reports
										</span>
									</td></tr>
									<%			  
										ArrayList reports = ProjectUtil.getAllReportsInProject(project.getProjectId());
										if (reports != null){
									    	Iterator rI = reports.iterator();
									    	while ( rI.hasNext() ) {
									    		Report r = (Report) rI.next();
									    		if (
									    				!(r.getReportDescription().startsWith("Canned"))
									    				&&
									    				(r.getReportVisibility().equals("public"))
									    			)  
									    		{
													
									 %>
									 			<tr>
													<td width='250'>
														<span class='normalText' title="Created By : <%=r.getCreatedByEmailId() %>;  Description : <%=r.getReportDescription()%>">
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
							<DIV class='alert alert-info'>

								<table  class='table'>
									<tr><td width='250' >
										<span class='sectionHeadingText'>
										Public Word Templates
										</span>
									</td></tr>
									<%			  
										ArrayList wordTemplates = ProjectUtil.getAllWordTemplatesInProject(project.getProjectId(), databaseType);
										if (wordTemplates != null){
									    	Iterator wTI = wordTemplates.iterator();
									    	while ( wTI.hasNext() ) {
									    		WordTemplate wordTemplate = (WordTemplate) wTI.next();
									    		String templateVisibility = wordTemplate.getTemplateVisibility();
												if (
														(templateVisibility != null) && 
														(templateVisibility.equals("public"))
													){
													
									 %>
									 				<tr id="<%=wordTemplate.getTemplateId() %>">
												 		<td width=250>
									 						<span class='normalText' title="Created By : <%=wordTemplate.getCreatedBy() %>; Description : <%=wordTemplate.getTemplateDescription() %>">
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
				
				<div id='projectMetricsDiv' class="tab-pane fade in " > </div>
				
				<div id='projectTrendsFilterDiv' class="tab-pane fade in " > 
					<div id='refreshProjectTrendsButtonDiv' class='alert alert-info'>
						<span class='normalText'>
						Show Only &nbsp;&nbsp;&nbsp;
						<select multiple size=5 name="displayRequirementType" id="displayRequirementType">
							<%
							ArrayList requirementTypes = project.getMyRequirementTypes();
							Iterator rTs = requirementTypes.iterator();
							while (rTs.hasNext()){
								RequirementType rT  = (RequirementType) rTs.next();
								%>
									<option value='<%=rT.getRequirementTypeShortName() %>'>  <%=rT.getRequirementTypeName()%></option>
							<%	
							}
							%>
						</select>
						&nbsp;&nbsp;&nbsp;
						Trend From (mm/dd/yyyy) <input type='text' id='fromDate'>
						&nbsp;&nbsp;&nbsp;
						Trend To (mm/dd/yyyy) <input type='text' id='toDate'>
						&nbsp;&nbsp;&nbsp;
						<input type='button' class='btn btn-sm btn-primary' value='Refresh Project Trend Chart' 
							onClick='displayProjectMetrics();
								document.getElementById("projectTrendsFilterDiv").style.display="none";
								document.getElementById("hideProjectTrendsButton").style.display="block";'>
					</span>
					</div>
					
				
				</div>
				<div id='hideProjectTrendsButton' style='display:none;'>
					<input type='button' class='btn btn-xs btn-primary' value='Show Trend Filters' 
					onclick='document.getElementById("projectTrendsFilterDiv").style.display="block";
						document.getElementById("hideProjectTrendsButton").style.display="none";'>
				</div>
				
				<div id='projectTrendsDiv' class="tab-pane fade in " > </div>
				
	
	
	
	
	</div>
	
	
<%}%>

