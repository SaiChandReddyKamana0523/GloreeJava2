<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String displayVirtualFolderIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayVirtualFolderIsLoggedIn  == null) || (displayVirtualFolderIsLoggedIn.equals(""))){
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
	
	
	
	
	<% 
		// Note since, the call is coming from a virtual folder id, the folder id will be of the format 
		// -1:requirement_type_id
	    String folderIdString = request.getParameter("folderId");
	    String[] virtualFolder = folderIdString.split(":");
	    int requirementTypeId = Integer.parseInt(virtualFolder[1]);
		RequirementType requirementType = new RequirementType(requirementTypeId);
	 	
	    
	%>

	<div id='folderCoreDiv' class='level1Box' STYLE="background-color:white">	
		<table  class='paddedTable' width='100%'>
			<tr>
				<td align='left' bgcolor='#99CCFF'>				
					<span class='subSectionHeadingText'>
					Virtual Folder 
					</span>
				</td>
			</tr>
			<tr>
				<td>				
					<span class='normalText'>
					This is a virtual folder , used to hold Deleted Requirements of type 
					'<%=requirementType.getRequirementTypeName()%>' 
					</span>
				</td>
			</tr>
			
		</table>
	</div>
<%}%>