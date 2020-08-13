<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String moveRequirementFormIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((moveRequirementFormIsLoggedIn == null) || (moveRequirementFormIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	// Note : folder Id can be a string in this context , eg when you are coming from  a deleted req.
	String folderId = request.getParameter("folderId");
	
	
	String source = request.getParameter("source");
	if (source == null){
		source  = "";
	}
	String messageDisplayDiv = "";
	if (source.equals("requirementList")){
		messageDisplayDiv = "displayRDInFolderDiv" + requirementId;
	}
	else {
		messageDisplayDiv = "purgeRequirementPromptDiv";
	}	
	
	
	
	
	if (RequirementUtil.getAllChildrenInFamilyRequirementIds(requirementId).size()>0){
%>
		<div id='purgeRequirementPromptDiv' class='alert alert-success'>
			<div style='float:right'>
			<a href='#' onClick='
				document.getElementById("<%=messageDisplayDiv%>").style.display = "none";
				if (document.getElementById("requirementAction<%=requirementId%>") != null){
					document.getElementById("requirementAction<%=requirementId%>").selectedIndex = "0";
				}	
		'>Close </a>
			</div>
			
			<span class='normalText'>This Requirement has Children associated with it. 
			<br>Please purge the Children or Assign them to a 
			different Parent, or make them Independent before attempting purge.
			</span>
			<br>
		</div>
		<br>
	<%}
	else if(RequirementUtil.requirementInLockedBaseline(requirementId)){
		%>
				<div id='purgeRequirementPromptDiv' class='alert alert-success'>
				<div style='float:right'>
				<a href='#' onClick='
				document.getElementById("<%=messageDisplayDiv%>").style.display = "none";
				if (document.getElementById("requirementAction<%=requirementId%>") != null){
					document.getElementById("requirementAction<%=requirementId%>").selectedIndex = "0";
				}	
				'>Close </a>
				</div>
						
				<span class='normalText'>This Requirement is in a locked Baseline and cannot be purged. Please work with your Project Administrator to unlock the Baseline first.
				</span>
				<br></div><br>

	<% 
	}
	else{
		%>
		
		
		<div id='purgeRequirementPromptDiv' class='alert alert-success'>
			
			<span class='headingText'>Are you sure you want to permanently delete this requirement ?<br>
			Please note that this will permanently remove the Requirement, it's Attributes and Traces.
			</span><br><br>
			<span class='normalText'>
				
				<input type='button' class='btn btn-primary btn-sm' name='Purge' value='  Purge  ' 
				onClick='purgeRequirement(<%=requirementId%> , "<%=folderId%>", "<%=source%>" )'>
			
			
				&nbsp;&nbsp;
			
				<input type='button' class='btn btn-danger btn-sm' name=' Close ' value='  Close  ' 
				onClick='
				document.getElementById("<%=messageDisplayDiv%>").style.display = "none";
				if (document.getElementById("requirementAction<%=requirementId%>") != null){
							document.getElementById("requirementAction<%=requirementId%>").selectedIndex = "0";
						}	
				'>
				
			
			</span>
			
			<br>
		</div>
		<br><br>
<%} %>		