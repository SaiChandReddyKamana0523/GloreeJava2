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
	
	
	<% 
		ArrayList releases = ProjectUtil.getAllReleasesInProject(project.getProjectId(), databaseType);
	%>
	 
	
	<div id = 'displayReleaseMetricsDiv' class='level1Box'>
		<input type='hidden' name='action' value = ''>	
		<table class='paddedTable' width='100%' >
			<tr>
				<td colspan='2' align='left' bgcolor='#99CCFF'>				
					<span class='subSectionHeadingText'>
					Dashboard for Releases in Project : <%=project.getProjectName() %>
					</span>
					<div style='float:right'>
						<span title='Dashboard Help Video'>
						<a target="_blank" href="http://www.youtube.com/watch?v=k9X4kqgQjm8">
						<img height="20" border="0" src="/GloreeJava2/images/television.png"/>
						</a>
						</span>
					</div>											
				</td>
			</tr>	
			<%if (releases.size() <1) { %>
			<tr>
				<td>
				<div class='alert alert-success'>
					<span class='normalText'>
					You do not have any Releases defined. Please work with your Project Administrator to 
					create some Releases and to utilize the power of the Release Dashboard. 
					</span>
				</div>	
				</td>
			</tr>
			<%} %>			
		</table>
		<table class='paddedTable' style='width:100%'>
			<tr>
				<td style='width:200px' >
					<span class='normalText'>
					<b> Step 1 :  Select A Release </b>
					</span>
				</td>
				<td>
					<span class='normalText'>
						<select name='releaseId' id='releaseId' 
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
							<option value='-1'> Release</option> 
						<%
							Iterator i = releases.iterator();
							while (i.hasNext()){
								Requirement requirement = (Requirement) i.next();
								String requirementName = requirement.getRequirementNameForHTML();
								if (requirementName.length() > 30) {
									requirementName = requirementName.substring(0,29);
								}
						%>
							<option value='<%=requirement.getRequirementId()%>'>
								<%=requirementName%>
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
									<a href='#' onclick='displayReleaseMetrics( );'>
									<img src="/GloreeJava2/images/chart_pie16.png" border="0">
									Release Metrics
									</a>
								</span>
							</td>
						</tr>
						<tr>
							<td colspan='2'>
								<span class='headingText'>
									<a href='#' onclick='displayReleaseTrends( );'>
									<img src="/GloreeJava2/images/chart_bar16.png" border="0">
									
									Release Trends
									</a>
								</span>
							</td>
						</tr>
						

						 <tr>						
							<td colspan='2'>
								<span class='headingText'>
									<a href='#' 
									onclick='
									var releaseIdObject = document.getElementById("releaseId");
									var cutOffDateObject = document.getElementById("cutOffDate");
										if (releaseIdObject.selectedIndex == 0) {
											alert ("Please Select a Release to run reports for.");
											releaseIdObject.focus();
											releaseIdObject.style.backgroundColor="#FFCC99";
										}
										else if (!isValidDate(cutOffDateObject.value)) {
											cutOffDateObject.focus();
											cutOffDateObject.style.backgroundColor="#FFCC99";
										}
										else {										
											displayReleaseRequirementsOfAllReqTypes("changedAfter");
										}
									'>
									<img src="/GloreeJava2/images/report16.png" border="0">
									Reqs in Release Changed After
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
											displayReleaseRequirementsOfAllReqTypes("defectStatusGroup");
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
											displayReleaseRequirementsOfAllReqTypes("defectStatusGroup");
										}
										'>
										<option> Defect Status</option>
										<%
										// at this point, since we don't know which release the user has selected
										// lets just get all the defect statuses in this project.
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
						




						
					</table>
				</td>
			</tr>
		</table>
	</div>
	<div id='releaseTrendsFilterDiv' style='display:none;' > 
			<div class='alert alert-info'>
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
			
			<input type='button' class='btn btn-sm btn-primary' value='Refresh Release Trend Chart' 
				onClick='
				displayReleaseTrends();
				document.getElementById("releaseTrendsFilterDiv").style.display="none";
				document.getElementById("hideProjectTrendsButton").style.display="block";
			'>
		</span>
		</div>
	
	</div>
	<div id='hideProjectTrendsButton' style='display:none;'>
		<input type='button' class='btn btn-xs btn-primary' value='Show Trend Filters' 
		onclick='document.getElementById("releaseTrendsFilterDiv").style.display="block";
			document.getElementById("hideProjectTrendsButton").style.display="none";'>
	</div>
		
<%}%>

