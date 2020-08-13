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
	
	
	<% 
		ArrayList baselines = ProjectUtil.getAllBaselinesInProject(project.getProjectId());
	%>
	 
	
	<div id = 'displayBaselineMetricsDiv' class='level1Box'>
		<input type='hidden' name='action' id='action'  value = ''>	
		<table class='paddedTable' width='100%' >
			<tr>
				<td colspan='2' align='left' bgcolor='#99CCFF'>				
					<span class='subSectionHeadingText'>
					Dashboard for Baselines in Project : <%=project.getProjectName() %>
					</span>
					<div style='float:right'>
						<span title='Dashboard Help Video'>
						<a target="_blank" href="http://www.youtube.com/watch?v=k9X4kqgQjm8">
						<img height="20" border="0" src="/GloreeJava2/images/television.png"/>
						</a>
						</span>
						&nbsp;&nbsp;
						<span title='Baselining Requirements Help Video'>
						<a target="_blank" href="http://www.youtube.com/watch?v=BVbYAtXMqdk">
						<img height="20" border="0" src="/GloreeJava2/images/television.png"/>
						</a>
						</span>
					</div>											
				</td>
			</tr>	
			<%if (baselines.size() <1) { %>
			<tr>
				<td>
				<div class='alert alert-success'>
					<span class='normalText'>
					You do not have any Baselines defined. Please work with your Project Administrator to 
					create some Baselines and to utilize the power of the Baseline Dashboard. 
					</span>
				</div>	
				</td>
			</tr>
			<%} %>
		</table>
		<table class='paddedTable' width='100%'>
			<tr>
				<td style='width:200px' >
					<span class='normalText'>
					<b> Step 1 :  Select A Baseline </b>
					</span>
				</td>
				<td  >
					<span class='normalText'>
					<select name='rTBaselineId' id='rTBaselineId'
					onChange='
					var contentCenterB = document.getElementById("contentCenterB");
					if (contentCenterB != null){
						contentCenterB.innerHTML = "";
						contentCenterB.style.display = "none";
					}
					var contentCenterE = document.getElementById("contentCenterE");
					if (contentCenterE != null){
						contentCenterE.innerHTML = "";
						contentCenterE.style.display = "none";
					}
					var contentCenterF = document.getElementById("contentCenterF");
					if (contentCenterF != null){
						contentCenterF.innerHTML = "";
						contentCenterF.style.display = "none";
					}
					'>
						<option value='-1'></option> 
					<%
						Iterator i = baselines.iterator();
						while (i.hasNext()){
							RTBaseline baseline = (RTBaseline) i.next();
							String baselineName = baseline.getBaselineName();
							if (baselineName.length() > 30) {
								baselineName = baselineName.substring(0,29);
							}
					%>
						<option value='<%=baseline.getBaselineId() %>'>
							<%=baseline.getRequirementType().getRequirementTypeShortName() %> : <%=baselineName%>
						</option>
					<%	
						}
					%>
					</select>			
					</span>	
				</td>
			</tr>
			
			
		
			<tr>
				<td  valign='top'>
					<span class='normalText'>
					<b> Step 2 :  Select on of these reports </b>
					</span>
				</td>
				<td valign='top'>
					<table>
						<tr>
							<td colspan='2'>
								<span class='headingText'>
									<a href='#' onclick='displayBaselineReport();'>
									<img src="/GloreeJava2/images/report16.png" border="0">									
									Baseline Report
									</a>
								</span>
							</td>
						</tr>
									
						<tr>
							<td colspan='2'>
								<span class='headingText'>
									<a href='#' onclick='displayBaselineMetrics2( "trends");'>
									<img src="/GloreeJava2/images/chart_bar16.png" border="0">
									<img src="/GloreeJava2/images/chart_pie16.png" border="0">
									Baseline Trends
									</a>
								</span>
							</td>
						</tr>
						
						
						
					
						<tr>
							<td colspan='2'>
								<span class='headingText'>
									<a href='#'  id='baselineChangeComparisionLink' onclick='displayBaselineRequirementsChangeComparision();'>
									<img src="/GloreeJava2/images/ExportExcel16.gif" border="0">
									Change Comparison: Baseline vs Current
									</a>
								</span>
							</td>
						</tr>

						<tr>
							<td colspan='2'>
								<span class='headingText'>
									<a href='#'  id='baselineChangeComparisionVsAnotherBaselineLink' onclick='displayBaselineRequirementsChangeComparisionVsAnotherBaseline();'>
									<img src="/GloreeJava2/images/ExportExcel16.gif" border="0">
									Change Comparison: Baseline vs Baseline
									</a>
	
									<select name='compareAgainstRTBaselineId' id='compareAgainstRTBaselineId'
									onChange='displayBaselineRequirementsChangeComparisionVsAnotherBaseline();'>
											<option value='-1'>Select A Baseline</option> 
										<%
											i = baselines.iterator();
											while (i.hasNext()){
												RTBaseline baseline = (RTBaseline) i.next();
												String baselineName = baseline.getBaselineName();
												if (baselineName.length() > 30) {
													baselineName = baselineName.substring(0,29);
												}
										%>
											<option value='<%=baseline.getBaselineId() %>'>
												<%=baseline.getRequirementType().getRequirementTypeShortName() %> : <%=baselineName%>
											</option>
										<%	
											}
										%>
									</select>			
										
								</span>
							</td>
						</tr>						
						 <tr>						
							<td colspan='2'>
								<span class='headingText'>
									<a href='#' 
									onclick='
									var baselineIdObject = document.getElementById("rTBaselineId");
									var cutOffDateObject = document.getElementById("cutOffDate");
										if (baselineIdObject.selectedIndex == 0) {
											alert ("Please Select a Baseline to run reports for.");
											baselineIdObject.focus();
											baselineIdObject.style.backgroundColor="#FFCC99";
										}
										else if (!isValidDate(cutOffDateObject.value)) {
											cutOffDateObject.focus();
											cutOffDateObject.style.backgroundColor="#FFCC99";
										}
										else {										
											displayBaselineRequirementsOfAllReqTypes("changedAfter");
										}
									'>
									<img src="/GloreeJava2/images/report16.png" border="0">
									Reqs in Baseline Changed After
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
											displayBaselineRequirementsOfAllReqTypes("defectStatusGroup");
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
											displayBaselineRequirementsOfAllReqTypes("defectStatusGroup");
										}
										'>
										<option> Defect Status</option>
										<%
										ArrayList defectStatusGroupsForProject = ReleaseMetricsUtil.getCurrentDefectStatusGroupsInProject(project.getProjectId());	
										Iterator j = defectStatusGroupsForProject.iterator();
										while (j.hasNext()){
											String defectStatus = (String)j.next();
										%>
											<option value='<%=defectStatus%>'><%=defectStatus%></option>
										<%
										}
										%>
									</select>
								</span>						
							</td>
						</tr>
						
						<tr>						
							<td colspan='2'>
							<div id='baselineReqsDiv' style='display:none' class='alert alert-info'>
							</div>
							</td>
						</tr>


			


					</table>
				</td>
			</tr>
		</table>
	</div>
<%}%>

