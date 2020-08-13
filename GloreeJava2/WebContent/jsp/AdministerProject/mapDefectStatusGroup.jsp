<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String createRoleFormIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((createRoleFormIsLoggedIn == null) || (createRoleFormIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }

	Project project= (Project) session.getAttribute("project");
	// lets see if this user is an admin of this project.
	boolean cRFIsAdmin = false;
	SecurityProfile cRFSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (cRFSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
		cRFIsAdmin = true;
	}
%>


<!--  A user needs to be an admin to createRole -->
<%if (cRFIsAdmin){ %>
	<%@ page import="java.util.*" %>
	<%@ page import="javax.servlet.http.HttpSession"  %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<%
		String status = request.getParameter("status");
	%>
	
	<div id='mapDefectStatusDiv' class='level1Box'>
	
	<form method="post" id="mapDefectStatusForm" action="">
		
	<table class='paddedTable' width='100%'>
		<tr>
			<td align='left' colspan='2' bgcolor='#99CCFF'>				
				<span class='subSectionHeadingText'>
				Defect Status Group Mapping
				</span>
				<div style='float:right'>
					<a href='/GloreeJava2/documentation/help/administerAProject.htm' target='_blank'>
					<img src="/GloreeJava2/images/page.png"   border="0">
					</a>
					&nbsp;&nbsp;	
				</div>
			</td>
		</tr>
		<%		if ((status != null) && (status.equals("updateSuccessful"))) {
		%>
		<tr>
			<td align='left' colspan='2' >				
				<div id='userPrompt' class='alert alert-success' align='left'> 
				<span class='normalText'>Your changes have been applied. </span> 
				</div> 
			</td>
		</tr>
		
		 <%} %>
			
		<%
		ArrayList defectStatusGroups = project.getDefectStatusGroups();
		if (defectStatusGroups.size() == 0){
		%>
		<tr>
			<td align='left' colspan='2' >				
				<span class='subSectionHeadingText'>
				To leverage Integrated Defect Management functionality, please create a Requirement 
				Type called 'Defects' with
				prefix 'DEF' and a custom attribute of type 'drop down' called 'Defect Status' 
				</span>
			</td>
		</tr>
		<%	
		}
		else {
		%>
			<tr>
				<td align='left' colspan='2' >				
					<span class='subSectionHeadingText'>
					You can consolidate Defect Statuses under new headers for reporting purpose. 
					Dashboards use Defect Status Groups
					for Trend graphs and Reports
					</span>
				</td>
			</tr>
			<tr><td colspan='2'>&nbsp;&nbsp;</td></tr>
			<tr> 
				<td>
					<span class='headingText'>Defect Status</span>
				</td>
				<td> 
					 <span class='headingText'>Defect Status Group</span>
				</td>
			</tr>

		<%
			Iterator i = defectStatusGroups.iterator();
			String statusGroupIdString = "";
			while (i.hasNext()){
				DefectStatus defectStatus = (DefectStatus) i.next();
				statusGroupIdString += defectStatus.getDefectStatusGroupId() + ","; 
			%>
				<tr> 
					<td>
						<span class='headingText'><%=defectStatus.getDefectStatus()%></span>
						<sup><span style="color: #ff0000;">*</span></sup> 
					</td>
					<td>
						<span class='normalText'> 
						<input type="text" name="defectStatusGroupId<%=defectStatus.getDefectStatusGroupId()%>" 
						id="defectStatusGroupId<%=defectStatus.getDefectStatusGroupId()%>" 
						value="<%=defectStatus.getDefectStatusGroup()%>" size="30" maxlength="100">
						</span> 
					</td>
				</tr>
			<%}
			// lets drop the last ,
			if (statusGroupIdString.contains(",")){
				statusGroupIdString = (String) statusGroupIdString.subSequence(0,statusGroupIdString.lastIndexOf(","));
			}	
			%>
			<tr>
				<td colspan=2 align="left">
					<span class='normalText'>
						<input type="button" name="Update Defect Status Groups" 
						value="Update Defect Status Groups" onClick="mapDefectStatusGroup(this.form, '<%=statusGroupIdString%>')">
						
					</span>
				</td>
			</tr>
		<%} %> 	
	</table>
	
	</form>
	</div>
<%}%>