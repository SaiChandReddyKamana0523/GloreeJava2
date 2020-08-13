<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn  == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
		int sharedProjectId = Integer.parseInt(request.getParameter("sharedProjectId"));
%>
	
<div class='level2BoxColored'>	
	<table width='100%'>
		<tr> 
			<td width='190' valign='top'>
				<span class='normalText'>
				 Source Requirement Type
				 </span>
			</td>
			<td> 
				<span class='normalText'>
					<select name='sharedRequirementTypeId' id='sharedRequirementTypeId'
					onChange='
						var sharedRequirementTypeIdObject = document.getElementById("sharedRequirementTypeId");
						if (sharedRequirementTypeIdObject.selectedIndex > 0){
							displayImportSharedRequirementAttributeFilterForm();
						}
						'>
					<option value=0> Select A Source Requirement Type</option>
					<%
					ArrayList sharedRequirementTypes = SharedRequirementUtil.getSharedRequirementTypes(sharedProjectId); 	
					Iterator i = sharedRequirementTypes.iterator();
					while (i.hasNext()){
						SharedRequirementType sRT = (SharedRequirementType) i.next();
						RequirementType rT = sRT.getRequirementType();
					%>
						<option value='<%=rT.getRequirementTypeId() %>'><%=rT.getRequirementTypeShortName() %> : <%=rT.getRequirementTypeName() %></option>
					<%}%>
					 </select>
				</span>
			</td>
		</tr>
	</table>
</div>
<div id='importSharedRequirementAttributeFilterDiv'></div>

<%}%>