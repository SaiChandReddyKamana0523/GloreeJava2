<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
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
%>

	<div id = 'sharedRequirementsMenuDiv' >
		
		<table >
			<tr>
				<td  align='left'>
					<div >&nbsp;</div>
				</td>
			</tr>	

			<tr>
				<td id='menuPublishSharedRequirements' align='center' valign='center'  width='95' height='50'
					class = 'focusTab' 
					>	
					<div >		 
		        		<a href='#'	onClick='displaySharedRequirements()'>
						<img src="/GloreeJava2/images/publish.png" border="0">
						 <br>
		        		 Publish
		        		 </a> 
		        	</div>
        		</td>

 				<td id='menuImportSharedRequirements' align='center' valign='center'  width='95' height='50'
					class = 'nonFocusTab' 
					>	
					<div >		 
		        		<a href='#'	onClick='displayImportSharedRequirements()'>
		        		<img src="/GloreeJava2/images/import.png" border="0">
			    		 <br>
		        		 Import
		        		 </a> 
		        	</div>
        		</td>
 				
			</tr>				
		</table>
	
	</div>
<%}%>