<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String uARRTIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((uARRTIsLoggedIn == null) || (uARRTIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	// its possible that we may have updated some requirement type info here. So 
	// its best to refresh the project  object.
	project = new Project(project.getProjectId(), databaseType);
	session.setAttribute("project", project);
	
	// lets see if this user is an admin of this project.
	boolean cIIsAdmin = false;
	SecurityProfile cISecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	String powerUserSettings = project.getPowerUserSettings();
	String hideFromHealthBar = project.getHideFromHealthBar();
	// if the user is an admin in this project, 
	// or if the user is a poweruser and the poweruser roles has been granted Manage Core Info
	// then this page can be modified.
	if (
		(cISecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))
		||
		(
				(cISecurityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
				&&
				(powerUserSettings.contains("Manage Core Info"))
				)
		){
		cIIsAdmin = true;
	}
%>

<% 
 	String disabled="DISABLED";
	if(cIIsAdmin) {
		disabled = "";
	}
	 
	String updatedCoreInfoMessage = "";
	String updatedCoreInfo = (String) request.getAttribute("updatedCoreInfo");
	if ((updatedCoreInfo != null) && (updatedCoreInfo.equals("true")))	{
		updatedCoreInfoMessage = "" +
		" 	<div id='userPrompt' class='alert alert-success' align='left'> " +
		"	<span class='normalText'>Your changes have been applied. </span>" + 
		"	</div> " ; 
	}
	
	String userPrompt = "";
	String status = (String) request.getAttribute("status");
	if (status != null) {
		if (status.equals("shortNameExists")){
			userPrompt = "" +
				" 	<div id='userPrompt' class='alert alert-success' align='left'> " +
				"	<span class='normalText'> The Project Prefix '"+  request.getParameter("shortName")+  "' has already been taken. " +
				" Please choose another one. </span>" +
				"	</div> " ; 
		}
		if (status.equals("atRiskUsers")){
			ArrayList atRiskUsers = (ArrayList) request.getAttribute("atRiskUsers");
			userPrompt = "" +
				" 	<div id='userPrompt' class='alert alert-success' align='left'> " +
				"	<span class='normalText'> The following user's Email Ids do not match the Restricted Domain List '"+
				request.getParameter("restrictedDomains")+ "' <br> " + 
				"	Please add more Domains or Remove these users from this project before attempting " +
				"	to enfore the restriction. </span>" +  
				"	<table class='paddedTable'> " + 
				"	<tr> " + 
				"		<td> <span class='headingText'> User Name </span> </td> " +
				"		<td> <span class='headingText'> Email Id </span></td> " +
				"	</tr> "	;
			Iterator i = atRiskUsers.iterator();
			while (i.hasNext()){
				User user = (User) i.next();
				userPrompt += " <tr> " + 
				"		<td> <span class='normalText'>" +  user.getFirstName() + "  " + user.getLastName() + " </span> </td> " +
				"		<td> <span class='normalText'> " + user.getEmailId() + "</span></td> " +
				"	</tr> "	; 	
				
			}
 			userPrompt += "</table></div>";
		}
		
	}
%>
	<%@ page import="java.util.*" %>
	<%@ page import="javax.servlet.http.HttpSession"  %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
<form method="post" action="/GloreeJava2/servlet/CNPWAction" id="form1">
<div id="cNPW" class="level1Box">

<table class='paddedTable' width='100%'>

	<tr>
		<td align='left' colspan='2' bgcolor='#99CCFF'>				
			<span class='subSectionHeadingText'>
			Project Core Information
			</span>
			<div style='float:right'>
				<a href='/GloreeJava2/documentation/help/administerAProject.htm' target='_blank'>
				<img src="/GloreeJava2/images/page.png"   border="0">
				</a>	
				&nbsp;&nbsp;
			</div>
		</td>
	</tr>
	<tr>
		<td colspan='2' align='center'>
			<!--  only one of these 2 message appear at any one time. -->
			<div id='userAlertMessageDiv' class='alert alert-success' style='display:none'></div>
			<%=updatedCoreInfoMessage%>
			<%=userPrompt%>
		</td>
	</tr>
	
	<tr>
		<td>
			<div id="projectDetails" class="level2Box">
			<table class='paddedTable'>
					<tr>
						<td>
							<span class='headingText'>
							Project Name</span>
							<sup><span style="color: #ff0000;">*</span></sup>
						</td>
						<td>
							<span class='normalText'>
							<input type="text" '<%=disabled%>' name="projectName" size="30" maxlength="100" 
							value= '<%=project.getProjectName()%>'>
							</span>
						</td>
					</tr>
					<tr>
						<td>
							<span class='headingText'>
							Project Prefix
							</span>
							<sup><span style="color: #ff0000;">*</span></sup>
						</td>
						<td>
							<span class='normalText'>
							<input type="text" '<%=disabled%>' name="shortName" size="10" maxlength="10" 
							value='<%=project.getShortName()%>'>
							</span>
						</td>
					</tr>
					<tr>
						<td>
							<span class='headingText'>
							Project Description
							<sup><span style="color: #ff0000;">*</span></sup>
						</td>
						<td>
							<span class='normalText'>
							<textarea name="projectDescription"  '<%=disabled%>'
							 rows="5" cols="50" ><%=project.getProjectDescription()%></textarea>
							 </span>
						</td>
					</tr>
					<tr>
						<td>
							<span class='headingText'>
							Project Owner
							</span>
						</td>
						<td>
							<span class='normalText'>
							<input type="text" '<%=disabled%>' name="projectOwner" size="60" maxlength="100" 
							value= '<%=project.getProjectOwner()%>'>
							</span>
						</td>
					</tr>
					<tr>
						<td>
							<span class='headingText'>
							Project Website (Eg : PST)
							</span>
						</td>
						<td>
							<span class='normalText'>
							<input type="text" '<%=disabled%>' name="projectWebsite" size="60" maxlength="1000" 
							value= '<%=project.getProjectWebsite()%>'>
							</span>
						</td>
					</tr>
					<tr>
						<td>
							<span class='headingText'>
							Time Zone (Eg : PST)
							</span>
						</td>
						<td>
							<span class='normalText'>
							<input type="text" '<%=disabled%>' name="projectTimeZone" size="60" maxlength="1000" 
							value= '<%=project.getProjectTimeZone()%>'>
							</span>
						</td>
					</tr>	
					<tr>
						<td>
							<span class='headingText'>
							PST Delta (Eg : -300 for GMT)
							</span>
						</td>
						<td>
							<span class='normalText'>
							<input type="text" '<%=disabled%>' name="gmtDelta" size="60" maxlength="1000" 
							value= '<%=project.getProjectGMTDelta()%>'>
							How many minutes away from SanFrancisco time is your time zone
							</span>
						</td>
					</tr>				
					<tr>
						<td>
							<span class='headingText'>
							Project Organization
							</span>
						</td>
						<td>
							<span class='normalText'>
							<input type="text" '<%=disabled%>' name="projectOrganization" size="60" maxlength="100" 
							value= '<%=project.getProjectOrganization()%>'>
							</span>
						</td>
					</tr>					
					<tr>
						<td>
							<span class='headingText'>
							Project Tags
							</span>
						</td>
						<td>
							<span class='normalText'>
							<input type="text" '<%=disabled%>' name="projectTags" size="60" maxlength="1000" 
							value= '<%=project.getProjectTags()%>'>
							</span>
							<a onclick='document.getElementById("supportedTagsDiv").style.display="block";'>Supported Tags</a>
							<div id='supportedTagsDiv' style='display:none'>
								<div style='float:right'>
									<input type='button' class='btn btn-xs btn-danger' value='Cancel'
									onclick='document.getElementById("supportedTagsDiv").style.display="none";'>
								</div>
								 hide_statusbar_in_explorer hide_scrum hide_tour hide_homepage hide_statusbar hide_download hide_foldermetrics
							</div>
						
						</td>
					</tr>
					<tr><td colspan='2'><hr></td></tr>
					
	
					<tr>
						<td>
							<span class='headingText'>
							Use Priority Standard Attribute
							</span>
						</td>
						<td>
							<span class='normalText'>
								<select name="hidePriority" id="hidePriority">
									<% if (project.getHidePriority() == 0){ %>
										<option value="0" SELECTED> Yes </option>
										<option value="1"> No </option>
									<%}
									else {
									%>
										<option value="0" > Yes </option>
										<option value="1" SELECTED> No </option>
									<%} %>
								</select>
							</span>
						</td>
					</tr>									
					
					
					
					<tr><td colspan='2'><hr></td></tr>
						
					<%
					String siteEnabledForAgile = this.getServletContext().getInitParameter("siteEnabledForAgile");
					if (siteEnabledForAgile.equals("true")){
						// this site is enabled for Agile. So, we will display Agile functionality
					%>				
					<tr>
						<td>
							<span class='headingText'>
							Agile Scrum Workflow
							</span>
						</td>
						<td>
							<span class='normalText'>
								<select name="enableAgileScrum" id="enableAgileScrum">
									<% if (project.getEnableAgileScrum() == 0){ %>
										<option value="0" SELECTED> Disable</option>
										<option value="1"> Enable</option>
									<%}
									else {
									%>
										<option value="0" > Disable</option>
										<option value="1" SELECTED> Enable</option>
									<%} %>
								</select>
							</span>
						</td>
					</tr>									
					<%} %>		
					
					
					<tr>
						<td>
							<span class='headingText'>
							Agile Scrum Req Types
							</span>
						</td>
						<td>
							<span class='normalText'>
								<select MULTIPLE SIZE='4' name="enableAgileScrumRequirementTypeIds" id="enableAgileScrumRequirementTypeIds">
										<%
										ArrayList requirementTypes = project.getMyRequirementTypes();
										Iterator rTs = requirementTypes.iterator();
										while (rTs.hasNext()){
											RequirementType rT = (RequirementType) rTs.next();
											if (rT.getRequirementTypeEnableAgileScrum() == 1 ){
										%>
											<option SELECTED value="<%=rT.getRequirementTypeId() %>" > <%=rT.getRequirementTypeShortName() %> : <%=rT.getRequirementTypeName() %> </option>
										<%	}
											else {
											%>
											<option value="<%=rT.getRequirementTypeId() %>" > <%=rT.getRequirementTypeShortName() %> : <%=rT.getRequirementTypeName() %> </option>
										<%	}
										}%>	
								</select>
							</span>
						</td>
					
					</tr>	
					<tr>
						<td colspan='2'>
							<span class='headingText'>
							(The Requirement Types enabled for Agile Scrum will get the following new custom attributes : 'Agile Sprint', 'Agile Task Weight', 'Agile Task Status', 'Agile Total Effort ', 'Agile Effort Remaining')
							</span>
						</td>
					</tr>	
			</table>
			</div>
		</td>
	</tr>
	
	<tr>
		<td><hr></td>
	</tr>
	<tr>
		<td>
			<div id='votes' class='level2Box'>
			<table class='paddedTable'>
			
				<tr>
					<td>
						<span class='headingText'>
						Voting Enabled Req Types
						</span>
					</td>
					<td>
						<span class='normalText'>
							<select MULTIPLE SIZE='4' name="enableVotesRequirementTypesIds" id="enableVotesRequirementTypesIds">
									<% rTs = requirementTypes.iterator();
									while (rTs.hasNext()){
										RequirementType rT = (RequirementType) rTs.next();
										if (rT.getRequirementTypeEnableVotes() == 1 ){
									%>
										<option SELECTED value="<%=rT.getRequirementTypeId() %>" > <%=rT.getRequirementTypeShortName() %> : <%=rT.getRequirementTypeName() %> </option>
									<%	}
										else {
										%>
										<option value="<%=rT.getRequirementTypeId() %>" > <%=rT.getRequirementTypeShortName() %> : <%=rT.getRequirementTypeName() %> </option>
									<%	}
									}%>	
							</select>
						</span>
					</td>
				
				</tr>	
				<tr>
					<td colspan='2'>
						<span class='headingText'>
						(The Requirement Types enabled for Votes will get the following new custom attributes : Total Votes)
						</span>
					</td>
				</tr>
			
			</table>
			
			</div>
		</td>
	</tr>
	<tr>
		<td><hr></td>
	</tr>
	<tr>
		<td>
			<div id="Roles" class="level2Box">
			<table class='paddedTable'>
				<tr>
					<td>
						<span class='headingText'>
						Restricted Domains (Eg : cisco.com,microsoft.com)
						</span>
					</td>
					<td>
						<span class='normalText'>
						<input type="text" '<%=disabled%>' name="restrictedDomains" size="50"
						 maxlength="1000" 
						value='<%=project.getRestrictedDomains()%>'>
						</span>
					</td>
				</tr>
				<tr>
					<td colspan='2'>
						<span class='headingText'>
						(This will ensure that only members whose emails end in the Restricted Domains
						can become members of this project.)
						</span>
					</td>
				</tr>
				<tr><td colspan='2'>&nbsp;</td></tr>
			</table>
			</div>
		</td>
	</tr>

	<tr>
		<td><hr></td>
	</tr>

	<tr>
		<td>
			<div id="PctComplteDiv" class="level2Box">
			<table class='paddedTable'>
				<tr>
					<td>
						<span class='headingText'>
						Percent Complete Driver
						</span>
					</td>
					<td>
						<span class='normalText'>
							<select name="percentCompleteDriver" id="percentCompleteDriver">
								<option value="-1"></option>
									<%
									rTs = requirementTypes.iterator();
									while (rTs.hasNext()){
										RequirementType rT = (RequirementType) rTs.next();
										if (rT.getRequirementTypeId() == project.getPercentageCompletedDriverReqTypeId()){
										%>
											<option SELECTED value="<%=rT.getRequirementTypeId() %>" > <%=rT.getRequirementTypeShortName() %> : <%=rT.getRequirementTypeName() %> </option>
										<%	}
										else {
										%>
											<option value="<%=rT.getRequirementTypeId() %>" > <%=rT.getRequirementTypeShortName() %> : <%=rT.getRequirementTypeName() %> </option>
										<%	
										}
									}%>	
							</select>
						</span>
						<a href="#" onclick="document.getElementById('percentCompleteDriverMoreInfoDiv').style.display='block';" >More Info</a>
					</td>
				</tr>
				<tr>
				<td>
				</td>
				<td>
					<div style="display: none;" id="percentCompleteDriverMoreInfoDiv">
						<div style="float:right">
							<a href="#" onclick="document.getElementById('percentCompleteDriverMoreInfoDiv').style.display='none';" >
							Close
							</a>
						</div>
					<br>
					<span class="normalText">
						This feature automatically calculates the percentage completion of requirements, based on the rate of completion
						of downstream requirements.
						<br>
						For example, if Test Result is set as the driver, then
							<br> <br>
							<b>1)</b>
							Just like Testing Status, at set intervals a job runs, based on the number of requirements and how complete they are, these values are rolled up the Trace Tree.
							
							<br>
							<b>2)</b>
							If the there are 3 test results, TR-1, TR-2, TR-3, and each of them are 0%, 40% , 100% complete
    then if all 3 trace to a Functional Requirement (FR-1), then FR-1 is considered to be 47% complete (average of 0, 40 and 100).
    						
    						<br>
    						<b>3)</b>
    						If this FR-1 is tracing up to BR-1, then BR-1 is also 47% complete
   							
   							<br>
   							<b>4)</b>
   							If there is a release called REL-1 and it has BR-1 (47% complete) and BR-2 (53% complete), then REL-1 is 50% complete.
    				
    					<br><br>
						<font color='red'>Note: </font>
						If a project is 'Percent Complete Roll Up enabled' then the 'Percent Complete' value can ONLY be set for those requirements that drive the roll up values
					</span>
					</div>
				</td>
			</tr>
			</table>
			</div>
		</td>
	</tr>



	<tr>
		<td><hr></td>
	</tr>




	<tr>
		<td>
			<div id="PowerUserDiv" class="level2Box">
			<table class='paddedTable'>
				<tr>
					<td>
						<span class='headingText'>
						Show Health Bar For 
						</span>
					</td>
					<td>
						<span class='normalText'>
							<select MULTIPLE size='9' name="healthBar" id="healthBar">
								<%
								if (!hideFromHealthBar.contains("pendingYourApproval")){
									%>
									<option SELECTED  value="pendingYourApproval" > Pending your Approval</option>
									<%	
								}
								else {
									%>
									<option   value="pendingYourApproval" > Pending your Approval</option>
									<%	
								}
								
								
								if (!hideFromHealthBar.contains("yourRejected")){
									%>
									<option SELECTED  value="yourRejected" > Your Rejected</option>
									<%	
								}
								else {
									%>
									<option   value="yourRejected" > Your Rejected</option>
									<%	
								}
								
								if (!hideFromHealthBar.contains("pendingOthersApproval")){
									%>
									<option SELECTED  value="pendingOthersApproval" > Pending Other's Approval</option>
									<%	
								}
								else {
									%>
									<option   value="pendingOthersApproval" >  Pending Other's Approval</option>
									<%	
								}
								
								

								if (!hideFromHealthBar.contains("dangling")){
									%>
									<option SELECTED  value="dangling" > Your Dangling Objects</option>
									<%	
								}
								else {
									%>
									<option   value="dangling" > Your Dangling Objects</option>
									<%	
								}
								
								
								if (!hideFromHealthBar.contains("orphan")){
									%>
									<option SELECTED  value="orphan" > Your Orphan Objects</option>
									<%	
								}
								else {
									%>
									<option   value="orphan" > Your Orphan Objects</option>
									<%	
								}
								
								
								if (!hideFromHealthBar.contains("suspectDown")){
									%>
									<option SELECTED  value="suspectDown" > Your Suspect Downstream Objects</option>
									<%	
								}
								else {
									%>
									<option   value="suspectDown" > Your Suspect Downstream Objects</option>
									<%	
								}
								
								
								if (!hideFromHealthBar.contains("suspectUp")){
									%>
									<option SELECTED  value="suspectUp" > Your Suspect Upstream Objects</option>
									<%	
								}
								else {
									%>
									<option   value="suspectUp" > Your Suspect Upstream Objects</option>
									<%	
								}
								
								if (!hideFromHealthBar.contains("testFailed")){
									%>
									<option SELECTED  value="testFailed" > Your Test Failed Objects</option>
									<%	
								}
								else {
									%>
									<option   value="testFailed" > Your  Test Failed Objects</option>
									<%	
								}
								
								
								if (!hideFromHealthBar.contains("incomplete")){
									%>
									<option SELECTED  value="incomplete" > Your Incomplete Objects</option>
									<%	
								}
								else {
									%>
									<option   value="incomplete" > Your  InComplete Objects</option>
									<%	
								}
								
								
								%>
								
							</select>
						</span>
						<a href="#" onclick="document.getElementById('healthBarMoreInfoDiv').style.display='block';" >More Info</a>
					</td>
				</tr>
				<tr>
				<td>
				</td>
				<td>
					<div style="display: none;" id="healthBarMoreInfoDiv">
						<div style="float:right">
							<a href="#" onclick="document.getElementById('healthBarMoreInfoDiv').style.display='none';" >
							Close
							</a>
						</div>
					<br>
					<span class="normalText">
						This setting controls the whether your project's users see a customized healthbar to indicate different metrics
						<br>
					
					</span>
						
						<br>
						<span class="glyphicon glyphicon-fire " style=" color: red; font-size:1.5em "></span>
						<span class="glyphicon glyphicon-heart " style=" color: green; font-size:1.5em "></span>
						<span class="glyphicon glyphicon-fire " style=" color: red; font-size:1.5em "></span>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<span class="glyphicon glyphicon-fire " style=" color: red; font-size:1.5em "></span>
						<span class="glyphicon glyphicon-heart " style=" color: green; font-size:1.5em "></span>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<span class="glyphicon glyphicon-fire " style=" color: red; font-size:1.5em "></span>
						<span class="glyphicon glyphicon-heart " style=" color: green; font-size:1.5em "></span>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<span class="glyphicon glyphicon-fire " style=" color: red; font-size:1.5em "></span>
						<span class="glyphicon glyphicon-heart " style=" color: green; font-size:1.5em "></span>
					</div>
				</td>
			</tr>
			</table>
			</div>
		</td>
	</tr>


	<tr><td><hr></td></tr>

	<tr>
		<td>
			<div id="PowerUserDiv" class="level2Box">
			<table class='paddedTable'>
				<tr>
					<td>
						<span class='headingText'>
						PowerUser Settings
						</span>
					</td>
					<td>
						<span class='normalText'>
							<select MULTIPLE size='3' name="powerUserSettings" id="powerUserSettings">
								<%
								if (powerUserSettings.contains("Manage Core Info")){
									%>
									<option SELECTED value="Manage Core Info" > Manage Core Info</option>
									<%	
								}
								else {
									%>
									<option value="Manage Core Info" > Manage Core Info</option>
									<%	
								}
								
								
								if (powerUserSettings.contains("Manage Roles")){
									%>
									<option SELECTED value="Manage Roles" > Manage Roles</option>
									<%	
								}
								else {
									%>
									<option value="Manage Roles" > Manage Roles</option>
									<%	
								}
								
								
								
								if (powerUserSettings.contains("Manage Requirement Types")){
									%>
									<option SELECTED value="Manage Requirement Types" > Manage Requirement Types</option>
									<%	
								}
								else {
									%>
									<option value="Manage Requirement Types" > Manage Requirement Types</option>
									<%	
								}
								
								
								%>
								
							</select>
						</span>
						<a href="#" onclick="document.getElementById('powerUserSettingsMoreInfoDiv').style.display='block';" >More Info</a>
					</td>
				</tr>
				<tr>
				<td>
				</td>
				<td>
					<div style="display: none;" id="powerUserSettingsMoreInfoDiv">
						<div style="float:right">
							<a href="#" onclick="document.getElementById('powerUserSettingsMoreInfoDiv').style.display='none';" >
							Close
							</a>
						</div>
					<br>
					<span class="normalText">
						This setting controls the permissions given to a role named PowerUser 
						<br>
						For example, if you want to have a group of users that don't get all the permissions of an Administrator (like Delete a Project)
						<br>
						but some features of the Administrator (like Create Roles, Create Requirement Types), then you do the following...
							<br><br>
							<b>1)</b>
							Create a Role called 'PowerUser'
							<br>
							<b>2)</b>
							Add some users to this role
    						<br>
    						<b>3)</b>
    						Come to this screen and set Power User Settings to 'Manage Roles' and 'Manage Requirement Types'.
    					<br><br>
					</span>
					</div>
				</td>
			</tr>
			</table>
			</div>
		</td>
	</tr>

	
	<tr>
		<td align='left'>
			
			<table class='paddedTable' align='left'>
				<tr>
					<td colspan=2 align="left">
					
						<span class='normalText'>
						
							<input type="button" '<%=disabled%>' class='btn btn-sm btn-primary' name="updateCoreInfoButton" id="updateCoreInfoButton"
							 value="Update Core Info" 
							onClick = 'updateCoreInfo(this.form)'>
							
						</span>
					</td>
					</tr>	
			</table>
			
		</td>
	</tr>
		
</table>
</div>
</form>




<form method="post" action="/GloreeJava2/servlet/ProjectAction" id="form2">
<input type='hidden' name='action' value='archiveProject'>
<div id="archiveProject" class="level1Box">
<table class='paddedTable' width='100%'>
		<tr>
			<td align='left' bgcolor='#99CCFF'>				
				<span class='subSectionHeadingText'>
				Archive This Project
				</span>
			</td>
		</tr>

		<tr>
			<td>
				<span class='headingText'>
				Please note that you can re-activate this project at any time. However, while its archived, it is unaccessible and its metrics are not reported.
				
				<br><br>
				<input type='button' class='btn btn-sm btn-danger' value='Archive Project' id='ArchiveProjectButton1' 
				onclick='document.getElementById("archiveProjectConfirmDiv").style.display = "block";
					document.getElementById("ArchiveProjectButton1").disabled=true;'>
				</span>	
				<br><br><br>
				
				<div id='archiveProjectConfirmDiv' class='alert alert-success' style='display:none'>
					<table>
						<tr>
							<td colspan='2'>
								<span class='normalText'>
									Are you absolutely sure that you want to archive this project?
								</span>
							</td>
						</tr>
						<tr>
							<td>
								<input type='submit' value='Archive Project Now'  class='btn btn-sm btn-danger'>
							</td>
							<td>
								<input type='button' value='Cancel' class='btn btn-sm btn-primary'
								onclick='
									document.getElementById("ArchiveProjectButton1").disabled=false;
									document.getElementById("archiveProjectConfirmDiv").style.display = "none";'>
							</td>
						</tr>
					</table>
					
				</div>
			</td>
		</tr>
						
</table>
</div>
</form>




<form method="post" action="/GloreeJava2/servlet/ProjectAction" id="form2">
<input type='hidden' name='action' value='deleteProject'>
<div id="deleteProject" class="level1Box">
<table class='paddedTable' width='100%'>
		<tr>
			<td align='left' bgcolor='#99CCFF'>				
				<span class='subSectionHeadingText'>
				Delete This Project
				</span>
			</td>
		</tr>

		<tr>
			<td>
				<span class='headingText'>
				Pleae note that this action <font color='red'> <b>PERMANENTLY</b></font> removes this project from the system. 
				There is NO way to recover this data after this action. 
				<br><br>It's highly recommended that you take a 'Project Export' prior to Deleting this project.
				
				<br><br>
				<input type='button' class='btn btn-sm btn-danger' value='Delete Project' id='DeleteProjectButton1' 
				onclick='document.getElementById("deleteProjectConfirmDiv").style.display = "block";
					document.getElementById("DeleteProjectButton1").disabled=true;'>
				</span>	
				<br><br><br>
				
				<div id='deleteProjectConfirmDiv' class='alert alert-success' style='display:none'>
					<table>
						<tr> 
							<td colspan=2>
							<span class='normalText'>
							Are you absolutely sure that you want to delete this project?
							<span>
							</td>
						</tr>
						<tr>
							<td>
								<input type='submit' value='Delete Project' class='btn btn-sm btn-danger'>
							</td>
							<td>
									<input type='button' value='Cancel' class='btn btn-sm btn-primary'
									onclick='
										document.getElementById("DeleteProjectButton1").disabled=false;
										document.getElementById("deleteProjectConfirmDiv").style.display = "none";'
									>
							</td>
						</tr>
					
					
					
				</div>
			</td>
		</tr>
						
</table>
</div>
</form>



	