<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String cRTACIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((cRTACIsLoggedIn   == null) || (cRTACIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }

	// authorization  : only Admins can do this.
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is an admin of this project.
	boolean isAdmin = false;
	SecurityProfile eRPFSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (
		(eRPFSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))
		||
		(eRPFSecurityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
	){
		isAdmin = true;
	}
	

%>


<%if (isAdmin) { %>

	<%@ page import="java.util.*" %>
	<%@ page import="javax.servlet.http.HttpSession"  %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<%
		// NOTE : this page can be called under this scenarios. 
		// 1. when we want to create a new Baseline. In that case, it gets ONLY requirementTypeId as a param.
		
		int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
		RequirementType requirementType = new RequirementType(requirementTypeId);
		String status = (String) request.getAttribute("status");
		
		String baselineName = "";
		String baselineDescription = "";
		String statusMessage = "";
		
		if ((status != null) && (status.equals("baselineName already used"))) {
			// if we have a status message, that means, this request was already submitted and we have some request values that we can display.
			statusMessage = "	<tr> " + 		
				" <td colspan='2'> <b> <font color='red'> " + 		
				"	Another Baseline already exists with the same Name. Please choose another a different Name for this Baseline.	</font></b> " + 
				" </td>	</tr>";
			baselineName = request.getParameter("baselineName");
			baselineDescription = request.getParameter("baselineDescription");
		} 
			 
		String	baselineFormButton = "<input type='button' name='Create Baseline' class = 'btn btn-sm btn-primary' value='Create New Baseline (Snapshot)' onClick='createRTBaseline(" ;
		baselineFormButton +=  requirementTypeId + ")'>";
	
		// Delete Baseline button  is displayed, only if the baseline already exists. i.e rTBaselineIDString is not null
		String deleteBaselineButton = "";
	%>
	
	<div id='createBaselineDiv' class='level1Box'>	
		<input type="hidden" name="requirementTypeId" value="<%=requirementTypeId %>"  > 
	<table width='100%'>
		<tr>
			<td colspan="2"  align='left' > 
				<span class='subSectionHeadingText'>
				<b></>Add a Custom Baseline</b>  
				</span> 
			</td>
		</tr>
		<%=statusMessage %>
		<tr>
			<td class='normalTableCell' >
				<span class='headingText'> Name</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td class='normalTableCell' > 
				<input type="text"  name="baselineName" id="baselineName"  value='<%=baselineName%>' size="30" maxlength="100"> 
			</td>
		</tr>
		
		<tr> 
			<td>
				<span class='headingText'>Locked</span> 
			</td>
			<td>
				<span class='headingText'>
				<select name="locked" id="locked">
					<option value="0" SELECTED> No</option>
					<option value="1"> Yes</option>
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
			<td class='normalTableCell' >
				<span class='headingText'> Description</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td  class='normalTableCell' >
				<textarea name="baselineDescription" id="baselineDescription"  rows="5" cols="50" ><%=baselineDescription%></textarea>
			</td>
		</tr>	
		<tr>
			<td  class='normalTableCell' colspan="2" align="left">
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