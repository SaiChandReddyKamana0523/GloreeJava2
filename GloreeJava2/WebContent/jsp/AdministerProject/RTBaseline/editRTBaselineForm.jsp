<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String eRTACIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((eRTACIsLoggedIn   == null) || (eRTACIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	
	// authorization : only admins can do this stuff.
	Project project= (Project) session.getAttribute("project");
	SecurityProfile aASecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	
	String disabled = " disabled='disabled' ";
	// lets see if this user is an member of this project.
	boolean isMember = false;
	if (aASecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	String powerUserSettings = project.getPowerUserSettings();
	// lets see if this user is an admin of this project.
	// unless the user is an admin, the create / delete buttons stay disabled.
	if (
			(aASecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))
			||
			(
					(aASecurityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
					&&
					(powerUserSettings.contains("Manage Requirement Types"))
			)
		)
		{
		disabled = "";
	}
	
	
%>


<% if (isMember) { %>
	<%@ page import="java.util.*" %>
	<%@ page import="javax.servlet.http.HttpSession"  %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<%
		// NOTE : this page can be called under 2 scenarios. 
		// 1. when some one tried to create an baseline in the prev step, and used the same prefix . resulting
		// in an error STATUS. Which we use to display an error message.
		// 2. When someon clicked on an baseline name , in order to diplay / edit it.
		// in this case, we show the same form, but send a hidden atribute called baselineId.
		
		int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
		RequirementType requirementType = new RequirementType(requirementTypeId);
		String status = (String) request.getAttribute("status");
		String rTBaselineIdString = request.getParameter("rTBaselineId");
		int rTBaselineId = Integer.parseInt(rTBaselineIdString);
		RTBaseline a = new RTBaseline(rTBaselineId);
		
		String baselineName = "";
		String baselineDescription = "";
		String	baselineFormButton = "";
		String deleteBaselineButton = "";
		
		baselineName = a.getBaselineName();
		
		baselineDescription = a.getBaselineDescription();
		baselineFormButton = "<input type='button' " + 
			disabled + " name='Update Baseline' value='Update Baseline' onClick='editRTBaseline(" ;
		baselineFormButton +=  requirementTypeId + ")'>";
		
		deleteBaselineButton = "<input type='button' " +  
			disabled + " name='Delete Baseline' value='Delete Baseline' ";
		deleteBaselineButton += " onClick='deleteRTBaselineForm(" + rTBaselineIdString + "," + requirementTypeId + ")'>";
		
		
		
		String updatedBaselineMessage = "";
		String updatedBaseline = (String) request.getAttribute("updatedBaseline");
		if ((updatedBaseline != null) && (updatedBaseline.equals("true"))){
			updatedBaselineMessage = " " +
				" <tr> " + 
				"	<td colspan='2'> " +
				"		<div class='alert alert-success'> " + 
				"			Your changes have been applied " + 
				"		</div>" + 
				"	</td> " +
				" </tr> ";
		}
		
	%>
	
	<div id='editBaselineDiv' class='level1Box'>
		<input type="hidden" name="requirementTypeId" id="requirementTypeId"  value="<%=requirementTypeId %>"  >
		<input type="hidden" name="rTBaselineId" id="rTBaselineId"  value="<%=rTBaselineIdString%>"> 
	<table class='paddedTable' width='100%'>
		<tr>
			<td colspan="2"  align='left'  > 
				<span class='subSectionHeadingText'>
				<b>Edit Baseline</b>
				</span> 
			</td>
		</tr>
		<%=updatedBaselineMessage  %>
		<%
		if ((status != null) && (status.equals("baselineName already used"))) {
		%>
		<tr>
			<td colspan="2">
				<div id='baselineNameAlreadyUsedMessage' class='alert alert-success'>
				Another Baseline already exists with the same Name. 
				Please choose a different Name for this Baseline. 
				</div>
			</td> 
		</tr>
		<% 
		} 
		%>
	
		<tr>
			<td>
				<span class='headingText'> Name</span>
				<sup><span  style="color: #ff0000;">*</span></sup> 
			</td>
			
			<td>
				<span class='normalText'> 
				<input type="text"  <%=disabled%> name="baselineName" id="baselineName"  value='<%=baselineName%>' size="30" 
				maxlength="100"> 
				</span>
			</td>
		</tr>
		
				<tr> 
			<td>
				<span class='headingText'>Locked</span> 
			</td>
			<td>
				<span class='headingText'>
				<select name="locked" id="locked">
					<%if (a.getLocked() == 0 ){ %>
						<option value="0" SELECTED> No</option>
						<option value="1"> Yes</option>
					<%}
					else {%>
						<option value="0" > No</option>
						<option value="1" SELECTED> Yes</option>
					<%} %>
				</select>
				</span>
				<a href='#' onClick='document.getElementById("lockedMoreInfoDiv").style.display="block"'>More Info</a>
			</td>
		</tr>
		<tr>
			<td >
			</td>
			<td>
				<div id='lockedMoreInfoDiv' style='display:none;'>
					<div style='float:right'>
						<a href='#' onClick='document.getElementById("lockedMoreInfoDiv").style.display="none"'>
						Close
						</a>
					</div>
				<br>
				<span class='normalText'>
					If you lock a Baseline, then users can no longer add or remove Requirements from this Baseline.
					The system also prevents users from deleting or purging Requirements that are members of a 
					locked Baseline.
				</span>
				</div>
			</td>
		</tr>
		<tr> 
			<td> 
				<span class='headingText'>Description</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td >
				<span class='normalText'>
				<textarea <%=disabled%> name="baselineDescription" id="baselineDescription"  rows="5" cols="50" ><%=baselineDescription%></textarea>
				</span>
			</td>
		</tr>	
		<tr>
			<td colspan="2" align="left">
				<span class='normalText'>
					<%=baselineFormButton%>
					&nbsp;&nbsp;
					<%=deleteBaselineButton %>
				</span>
			</td>
		</tr> 	
	 
	</table>
	
	
	</div>
<%}%>