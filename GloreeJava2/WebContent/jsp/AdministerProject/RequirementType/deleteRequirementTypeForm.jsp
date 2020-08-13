<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String dRTIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dRTIsLoggedIn == null) || (dRTIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is an admin of this project.
	boolean isAdmin = false;
	String powerUserSettings = project.getPowerUserSettings();

	SecurityProfile dRTFSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (
			(dRTFSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())
			||
			(
				(dRTFSecurityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
				&&
				(powerUserSettings.contains("Manage Requirement Types"))
				)
		)){
		isAdmin = true;
	}
	
	
%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<!--  this page is displayed only if the user is an admin of this project.  -->
<% if (isAdmin){ %>
	<div id='deletRequirementTypeFormDiv' class='level1Box'>
	<table class='paddedTable'  width='100%' align="center" >
		<tr>
			<td  align='left'> 
				<span class='subSectionHeadingText'>
				<b>Delete A Requirement Type</b> 
				</span> 
			</td>
		</tr>
	
	<% 
		int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
		RequirementType requirementType = new RequirementType(requirementTypeId);	
	    
		int numOfRequirements = ProjectUtil.getNumOfRequirements(requirementTypeId);
		
		int numOfWordTemplates = ProjectUtil.getNumberOfWordTemplatesInRequirementType(requirementTypeId);
		
		if (numOfRequirements > 0) {
	%>
		<tr>
			<td>
				<div id='rTHasReqs' class='alert alert-success'>
				<span class='normalText'>
				<br><br>You have <%=numOfRequirements%>  Requirements of this 
				Requirement Type :  "<%=requirementType.getRequirementTypeName() %>" .<br><br>
				Please Purge (Hard Delete) them all before attempting to delete this Requirement Type.<br><br>
				Please note that you may have some 'SOFT Deleted' Requirements. You may want to 'Purge' 
				these Requirements, before attempting to delete the Requirement Type.
				
				<br>
				To locate your 'Soft Deleted' Requirements, please go to 'Deleted Requirements' folder and then select
				<%=requirementType.getRequirementTypeName() %> . There should be a 'Purge All' button at the top of this page. 
				</span>
				</div>
			</td>
		</tr>
		<tr>
			<td align='left'>
				<br><br>
				
			</td>
		</tr>				
		
	<%
		}
		else if (numOfWordTemplates > 0) {
	%>
		<tr>
			<td>
				<div id='rTHasReqs' class='alert alert-success'>
				<span class='normalText'>
				<br><br>You have <%=numOfWordTemplates%>  Word Document Templates in folders of the 
				Requirement Type :  "<%=requirementType.getRequirementTypeName() %>" .<br><br>
				Please delete them all before attempting to delete this Requirement Type.<br><br>
				</span>
				</div>
			</td>
		</tr>
		<tr>
			<td align='left'>
				<br><br>
				
			</td>
		</tr>				
		
	<%
		}
		else {
	%>
	
		<tr>
			<td>
				<span class='headingText'>
				Yes. I want to Delete Requirement Type
				</span> 
				<span class='normalText' title='Prefix : 
				<%=requirementType.getRequirementTypeShortName()%>  
				Description : <%=requirementType.getRequirementTypeDescription() %>'>
					"<%=requirementType.getRequirementTypeName()%>"
				</span>
			</td>
		</tr>
		<tr>
			<td align='left'>
				<span class='normalText'>
					<input type="button" name="Delete RequirementType" value="Delete RequirementType" 
					onClick='deleteRequirementType("<%=request.getParameter("requirementTypeId")%>")'>
				</span>
			</td>
		</tr>				
	<%
		}
	%>	
	</table>
	</div>
<%}%>