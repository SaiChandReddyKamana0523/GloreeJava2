<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn   == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }

	// authorization : only admins can do this.
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is an admin of this project.
	boolean isAdmin = false;
	SecurityProfile aASecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (aASecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
		isAdmin = true;
	}
	
%>


<%if (isAdmin){ %>
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<div  id='deleterTBaselineDiv' class='level1Box'>
	<table  class='paddedTable' align="center" >
	
	<% 
		int rTBaselineId = Integer.parseInt(request.getParameter("rTBaselineId"));
		RTBaseline a = new RTBaseline(rTBaselineId);
		
		int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
	%>
		<tr>
			<td>
				<div id='rTBaselineAlertMessage1' class='alert alert-success'>
				<span class='normalText'>
				If you delete this Baseline
				"<%=a.getBaselineName() %>", 
				 the association of Requirements to this Baseline will be removed. <br>
				 Please note that this does not impact the Requirements themselves, it just de-links those
				 Requirements from this Baseline.
				 </span>
				</div> 
			</td>
		</tr>
		
		<tr>
			<td align='left'>
				<span class='normalText'>
					<br><br><input type="button" name="Delete Baseline" id='deleteBaselineButton' 
					value="Delete Baseline" 
					onClick='deleteRTBaseline("<%=rTBaselineId%>", "<%=requirementTypeId%>")'>
					
					
				</span>
			</td>
		</tr>				
	</table>
	</div>
<%}%>