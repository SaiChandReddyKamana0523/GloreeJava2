
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String returnFolderIdIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((returnFolderIdIsLoggedIn  == null) || (returnFolderIdIsLoggedIn.equals(""))){
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
		Folder folder = (Folder) request.getAttribute("folder");
	
	%>
	{
	"folderId" : "<%=folder.getFolderId()%>",
	"folderName" : "<%=folder.getFolderName()%>",
	"folderDescription" : "<%=folder.getFolderDescription() %>",
	"parentFolderId" : "<%=folder.getParentFolderId()%>"
	}

<%}%>