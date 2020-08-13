<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String dRTACIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dRTACIsLoggedIn   == null) || (dRTACIsLoggedIn.equals(""))){
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
	if (
			(aASecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))
			||
			(aASecurityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
		){
		isAdmin = true;
	}
	
%>


<%if (isAdmin){ %>
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<table  class='paddedTable' align="left" >
	
	<% 
		int rTAttributeId = Integer.parseInt(request.getParameter("rTAttributeId"));
		RTAttribute a = new RTAttribute(rTAttributeId);
		
		int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
	%>
		<tr>
			<td colspan="4"  align='left'  > 
				<span class='subSectionHeadingText'>
				<b>Delete Custom Attribute</b>
				</span> 
			</td>
		</tr>
	
		<tr>
			<td>
				
				<span class='normalText'>
					If you delete this Custom Attribute, all
					attribute values of requirements that have this attribute will be deleted.
				 </span>
				
			</td>
		</tr>
		
		<tr>
			<td align='left'>
				<span class='normalText'>
					<br><br><input type="button" name="Delete Attribute" id='deleteAttributeButton' 
					value="Delete Attribute" 
					onClick='deleteRTAttribute("<%=rTAttributeId%>", "<%=requirementTypeId%>")'>
					
				</span>
			</td>
		</tr>				
	</table>
<%}%>