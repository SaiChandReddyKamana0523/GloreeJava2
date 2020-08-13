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
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is an admin of this project.
	boolean uRTIsAdmin = false;
	String powerUserSettings = project.getPowerUserSettings();
	SecurityProfile uRTSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (
			(uRTSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())
					||
			(
				(uRTSecurityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
				&&
				(powerUserSettings.contains("Manage Requirement Types"))
				)
		)){
		uRTIsAdmin = true;
	}
%>

<% if(uRTIsAdmin) { %>
	<%@ page import="java.util.*" %>
	<%@ page import="javax.servlet.http.HttpSession"  %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<%
		String status = (String) request.getAttribute("status");
		ArrayList otherRequirementTypes = project.getMyRequirementTypes();
	
		int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
		RequirementType rT = new RequirementType (requirementTypeId);
		
		String updatedRequirementType = (String) request.getAttribute("updatedRequirementType");
		String updatedRequirementTypeMessage = "";
		if ((updatedRequirementType != null) && (updatedRequirementType.equals("true"))){
			updatedRequirementTypeMessage = " " +
				" <div class='alert alert-success'> " + 
				"	<span class='normalText'> " + 
				"	Your changes have been applied" +
				"	</span> " + 
				" </div>";
		}
		
	%>
	
	<div id='updateRTDiv' class='level1Box'>
	<table class='paddedTable' width='100%'>
		<tr>
			<td colspan="2"  align='left'> 
				<span class='subSectionHeadingText'>
				<b>Edit an Object Type</b> 
				</span> 
			</td>
		</tr>
		<tr>
			<td colspan="2"  align='left'> 
				 <%=updatedRequirementTypeMessage %>
			</td>
		</tr>
		<%
		if ((status != null) && (status.equals("shortName already used"))) {
		%>
		<tr>
			<td colspan="2">
			<div id='rTAlreadyUsedMessage' class='alert alert-success'>
			Another Requirement Type already exists with the same Prefix. Please choose another a different Prefix.
			</div>
			</td> 
		</tr>
	
		<% 
		} 
		%>
		<tr> 
			<td>
				<span class='headingText'>Prefix</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td> 
				<span class='headingText'>
				<%=rT.getRequirementTypeShortName()%> 
				&nbsp;&nbsp;
				<b><font color=red>Note : Prefix value can not be changed. 
				</font></b>
				</span>
			</td>
		</tr>	
		<tr> 
			<td>
				<span class='headingText'>Approval WorkFlow</span> 
			</td>
			<td>
				<span class='headingText'>
				<select name="requirementTypeEnableApproval" id="requirementTypeEnableApproval">
					<% if (rT.getRequirementTypeEnableApproval() == 0){ %>
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
		<tr> 
			<td>
				<span class='headingText'>Send Approval Reminder Emails on
				<br>
				<b><i>Ctrl+Mouse Click to select</i></b></span> 
			</td>
			<td>
				<span class='normalText'>
					<%
					String remindApproversOn = rT.getRemindApproversOn(); %>
					<select name='remindApproversOn' id='remindApproversOn' MULTIPLE SIZE='7'>
						<%
						String mondaySelected = "";
						String tuesdaySelected = "";
						String wednesdaySelected = "";
						String thursdaySelected = "";
						String fridaySelected = "";
						String saturdaySelected = "";
						String sundaySelected = "";
						
						if (remindApproversOn.contains("Monday")){
							mondaySelected = "SELECTED";
						}
						if (remindApproversOn.contains("Tuesday")){
							tuesdaySelected = "SELECTED";
						}
						if (remindApproversOn.contains("Wednesday")){
							wednesdaySelected = "SELECTED";
						}
						if (remindApproversOn.contains("Thursday")){
							thursdaySelected = "SELECTED";
						}
						if (remindApproversOn.contains("Friday")){
							fridaySelected = "SELECTED";
						}
						if (remindApproversOn.contains("Saturday")){
							saturdaySelected = "SELECTED";
						}
						if (remindApproversOn.contains("Sunday")){
							sundaySelected = "SELECTED";
						}
						%>
						
						<option <%=mondaySelected%> value='Monday'>Monday</option>
						<option <%=tuesdaySelected%> value='Tuesday'>Tuesday</option>
						<option <%=wednesdaySelected%> value='Wednesday'>Wednesday</option>
						<option <%=thursdaySelected%> value='Thursday'>Thursday</option>
						<option <%=fridaySelected%> value='Friday'>Friday</option>
						<option <%=saturdaySelected%> value='Saturday'>Saturday</option>
						<option <%=sundaySelected%> value='Sunday'>Sunday</option>
					</select>
				</span>
			</td>
		</tr>		
		<tr> 
			<td>
				<span class='headingText'>Display Sequence (Number)</span> 
			</td>
			<td>
				<span class='headingText'>
				<input type='text' name="requirementTypeDisplaySequence" id="requirementTypeDisplaySequence" 
				size="2" maxlength="2"
				value="<%=rT.getRequirementTypeDisplaySequence()%>">
				</span>
			</td>
		</tr>
		
		<tr> 
			<td valign='top' width='150'>
				<span class='headingText'>Can be Reported as Orphan</span> 
			</td>
			<td>
				<span class='headingText'>
				<select name="requirementTypeCanBeOrphan" id="requirementTypeCanBeOrphan">
					<% if (rT.getRequirementTypeCanBeOrphan() == 1){ %>
						<option value="1" SELECTED> Yes</option>
						<option value="0"> No</option>
					<%}
					else {
					%>
						<option value="1" > Yes </option>
						<option value="0" SELECTED> No</option>
					<%} %>
				</select>
				</span>
				<a href='#' onClick='document.getElementById("canBeOrphanDiv").style.display="block"'>
				More Info </a>
				<br>
				<div id='canBeOrphanDiv' style='display:none;'>
					<span class='headingText'>
					When set to 'NO', Reqs of this type will NOT be reported as ORPHAN even
					if they do not trace to any higher level Requirements.
					For Reqs of type 'Release' that typically don't traceUP to any requirements, you want to set this to 'No', so they don't show up as Orphans
					in Dasboard Reports
					
					</span>
				</div> 
			</td>
		</tr>		
		<tr> 
			<td valign='top' width='150'>
				<span class='headingText'>Can be Reported as Dangling</span> 
			</td>
			<td>
				<span class='headingText'>
				<select name="requirementTypeCanBeDangling" id="requirementTypeCanBeDangling">
					<% if (rT.getRequirementTypeCanBeDangling() == 1){ %>
						<option value="1" SELECTED> Yes</option>
						<option value="0"> No</option>
					<%}
					else {
					%>
						<option value="1" > Yes </option>
						<option value="0" SELECTED> No</option>
					<%} %>
				</select>
				</span>
				<a href='#' onClick='document.getElementById("canBeDanglingDiv").style.display="block"'>
				More Info </a>
				<br>
				<div id='canBeDanglingDiv' style='display:none;'>
					<span class='headingText'>
					When set to 'NO', Reqs of this type will NOT be reported as DANGLING even if
	 				they do not have traceFrom lower level Reqs. For Reqs of type 'Test Results' that typically don't have any traces coming from below,
					you want to set this to 'No', so that they don't show as Dangling Reqs in Dashboard Reports.
					</span>
				</div>
			</td>
		</tr>		


		<tr> 
			<td valign='top' width='150'>
				<span class='headingText'>Notify on Owner change</span>
			</td>
			<td>
				<span class='headingText'>
				<select name="notifyOnOwnerChange" id="notifyOnOwnerChange">
					<% if (rT.getNotifyOnOwnerChange() == 1){ %>
						<option value="1" SELECTED> Yes</option>
						<option value="0"> No</option>
					<%}
					else {
					%>
						<option value="1" > Yes </option>
						<option value="0" SELECTED> No</option>
					<%} %>
				</select>
				</span>
				<a href='#' onClick='document.getElementById("notifyOnOwnerChangeDiv").style.display="block"'>
				More Info </a>
				<br>
				<div id='notifyOnOwnerChangeDiv' style='display:none;'>
					<span class='headingText'>
					When set to 'YES', any time the Req Owner is changed, an email is sent to the new owner.
					</span>
				</div>
			</td>
		</tr>		

		<tr> 
			<td valign='top' width='150'>
				<span class='headingText'>Notify on Approval Status change</span>
			</td>
			<td>
				<span class='headingText'>
				<select name="notifyOnApprovalChange" id="notifyOnApprovalChange">
					<% if (rT.getNotifyOnApprovalChange() == 1){ %>
						<option value="1" SELECTED> Yes</option>
						<option value="0"> No</option>
					<%}
					else {
					%>
						<option value="1" > Yes </option>
						<option value="0" SELECTED> No</option>
					<%} %>
				</select>
				</span>
				<a href='#' onClick='document.getElementById("notifyOnApprovalChangeDiv").style.display="block"'>
				More Info </a>
				<br>
				<div id='notifyOnApprovalChangeDiv' style='display:none;'>
					<span class='headingText'>
					When set to 'YES', any time the Req's approval status is changed,
					an email is sent to all the pending approvers.
					</span>
				</div>
			</td>
		</tr>

		<tr> 
			<td valign='top' width='150'>
				<span class='headingText'>Can NOT DIRECTLY Trace to</span> 
			</td>
			<td>
				<span class='headingText'>
				<select name="requirementTypeCanNotTraceTo" id="requirementTypeCanNotTraceTo"  multiple size='5'>
					<%
					Iterator oRTS = otherRequirementTypes.iterator();
					while (oRTS.hasNext()){
						RequirementType otherRT = (RequirementType) oRTS.next();
						if (rT.getRequirementTypeCanNotTraceTo().contains(":#:" + otherRT.getRequirementTypeShortName())){
							%>
							<option  SELECTED value="<%=otherRT.getRequirementTypeShortName()%>" > <%=otherRT.getRequirementTypeName() %></option>
							<%
						}
						else {
							%>
							<option  value="<%=otherRT.getRequirementTypeShortName()%>" > <%=otherRT.getRequirementTypeName() %></option>
							<%
						}
					}
					%>
				</select>
				</span>
				<a href='#' onClick='document.getElementById("canNotTraceToDiv").style.display="block"'>
				More Info </a>
				<br>
				<div id='canNotTraceToDiv' style='display:none;'>
					<span class='headingText'>
					Use this selection to architecturally enforce which other Requirement Types this Requirement Type
					can not Trace To.  For example, you want to ensure that 'Test Results' requirement types can 
					never directly trace to 'Product Requirements', then in the 'Product Requirements' form, 
					select 'Test Results' as a 'Can Not Trace To' value.
					</span>
				</div>
			</td>
		</tr>		
		
		<tr> 
			<td>
				<span class='headingText'>Name</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td> 
				<span class='headingText'>
				<input type="text" name="requirementTypeName" id="requirementTypeName" size="50" maxlength="100" value='<%=rT.getRequirementTypeName()%>'>
				</span> 
			</td>
		</tr>
		<tr> 
			<td valign='top'>
				<span class='headingText'>Description</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td>
				<span class='headingText'>
				<textarea name="requirementTypeDescription" id="requirementTypeDescription" rows="5" cols="50" ><%=rT.getRequirementTypeDescription() %></textarea>
				</span>
			</td>
		</tr>	
		<tr>
			<td colspan=2 align="left">
				<span class='normalText'>
					<input type="button" name="Update Requirement Type" class='btn btn-sm btn-primary' 
					value="Update Requirement Type" onClick="updateRequirementType(<%=project.getProjectId()%>, <%=requirementTypeId%>)">
					
				</span>
			</td>
			
			
		</tr> 	
	</table>
	
	</div>
<%}%>