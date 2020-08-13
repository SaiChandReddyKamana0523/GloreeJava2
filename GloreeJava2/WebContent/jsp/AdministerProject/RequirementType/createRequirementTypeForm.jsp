<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String cRTFIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((cRTFIsLoggedIn == null) || (cRTFIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is an admin of this project.
	boolean cRTFIsAdmin = false;
	String powerUserSettings = project.getPowerUserSettings();
	SecurityProfile cRTFSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (
			(cRTFSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())
			||
			(
			(cRTFSecurityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
				&&
				(powerUserSettings.contains("Manage Requirement Types"))
				)
		)){
		cRTFIsAdmin = true;
	}
	
%>

<!--  do this only if the user is an admin -->
<% if(cRTFIsAdmin){ %>
	<%@ page import="java.util.*" %>
	<%@ page import="javax.servlet.http.HttpSession"  %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<%  
		String status = (String) request.getAttribute("status");
	%>
	
	<div id='createRequirementTypeDiv' class='level1Box'>
	
	<form method="post" id="createRequirementTypeForm" action="">
		<input type="hidden" name="projectId" value= "<%=project.getProjectId()%>">
	<table class='paddedTable' width='100%'>
		<tr>
			<td align='left' colspan='2' bgcolor='#99CCFF'>				
				<span class='subSectionHeadingText'>
				Create A New Object  Type
				</span>
				<div style='float:right'>
					<a href='/GloreeJava2/documentation/help/administerAProject.htm' target='_blank'>
					<img src="/GloreeJava2/images/page.png"   border="0">
					</a>
					&nbsp;&nbsp;	
				</div>
			</td>
		</tr>	
		<%
		if ((status != null) && (status.equals("shortName already used"))) {
		%>
		<tr>
			<td colspan="2">
			<div id='rTAlreadyUsedMessage' class='alert alert-success'>
			Another Requirement Type already exists with the same Name or Prefix. Please choose another a different Name and Prefix.
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
				<input type="text" name="requirementTypeShortName" size="3" maxlength="3">
				</span> 
			</td>
		</tr>
		<tr> 
			<td>
				<span class='headingText'>Approval WorkFlow</span> 
			</td>
			<td>
				<span class='headingText'>
				<select name="requirementTypeEnableApproval">
					<option value="0" SELECTED> Disable</option>
					<option value="1"> Enable</option>
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
				size="2" maxlength="2">
				</span>
			</td>
		</tr>
		<tr> 
			<td valign='top' width='150'>
				<span class='headingText'>Can be Reported as Orphan</span> 
			</td>
			<td>
				<span class='headingText'>
				<select name="requirementTypeCanBeOrphan">
					<option value="1" SELECTED> Yes</option>
					<option value="0"> No</option>
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
				<select name="requirementTypeCanBeDangling">
					<option value="1" SELECTED> Yes</option>
					<option value="0"> No</option>
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
					<option value="1" SELECTED> Yes</option>
					<option value="0"> No</option>
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
				<select name="notifyOnApprovalChange"  id="notifyOnApprovalChange">
					<option value="1" SELECTED> Yes</option>
					<option value="0"> No</option>
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
					ArrayList requirementTypes = project.getMyRequirementTypes();
					Iterator rTS = requirementTypes.iterator();
					while (rTS.hasNext()){
						RequirementType rT = (RequirementType) rTS.next();
					%>
						<option value="<%=rT.getRequirementTypeShortName()%>" > <%=rT.getRequirementTypeName() %></option>
					<%
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
				<input type="text" name="requirementTypeName" size="30" maxlength="100">
				</span> 
			</td>
		</tr>
		<tr> 
			<td>
				<span class='headingText'>Description</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td>
				<span class='headingText'>
				<textarea name="requirementTypeDescription" rows="5" cols="50" ></textarea>
				</span>
			</td>
		</tr>	
		<tr>
			<td colspan=2 align="left">
				<span class='normalText'>
					<input type="button" name="Create Requirement Type" class='btn btn-sm btn-primary' 
					value="Create Requirement Type" onClick="createRequirementType()">
					
				</span>
			</td>
		</tr> 	
	 
	
	</table>
	
	</form>
	</div>
<%}%>