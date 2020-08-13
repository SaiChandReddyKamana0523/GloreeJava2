<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>
<head>

	<!--  Since this page is likely shown in a different window, we need to load the css and java script files. -->
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/css/common.css"> 
	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userAccount.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userDashboard.js?v=20200630"></script>
	
	
</head>
<body onLoad='document.getElementById("googleSearchString").focus();'>
<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String displayRequirementIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayRequirementIsLoggedIn == null) || (displayRequirementIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	int callingRequirementId = Integer.parseInt(request.getParameter("callingRequirementId"));
	Requirement callingRequirement = new Requirement(callingRequirementId, databaseType);

	int searchProjectId = 0;
	try {
		searchProjectId = Integer.parseInt(request.getParameter("searchProjectId"));
	}
	catch (Exception e){
		// nothing.
	}
	// if no search project id was sent in, then the current project is the search project.
	if (searchProjectId == 0 ){
		searchProjectId = project.getProjectId();
	}
	
	
	
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	
	// you need to be a member of this project to see this
	if (isMember ){
%>

	<div id = 'searchForm'  class='alert alert-success'
	style='border-color:blue; border-style:dotted solid; border-radius:5px ' >
				
				<table  border="0" >
					
					<%
					// if the user doesn't have traceto or tracefrom privs on this folder, lets show him / her that message.
					if (!(
						(securityProfile.getPrivileges().contains("traceToRequirementsInFolder" + callingRequirement.getFolderId()))
						||
						(securityProfile.getPrivileges().contains("traceFromRequirementsInFolder" + callingRequirement.getFolderId()))
						)){
						// this user has neither traceto nor trace from permissions. 
						%>
						<tr>
						<td colspan='2' align='left' bgcolor='#99CCFF'>				
							<span class='subSectionHeadingText'>
							You do not have Trace permissions on this folder. Please work with your project administrator to get these permissions.
							</span>
						</td>
						</tr>
						
						
						<%
					}
					else {
					%>
					
					<tr>
						<td colspan='2'>
							
							<span class='normalText'>Search By Id</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<span class='normalText'>
								<input type='text' size='30' 
								value=' An object id like BR-1,fr-2' maxlength='300' name='reqTraceIdQuick' id='reqTraceIdQuick'
								onFocus='this.value=""'
								onkeypress=" handleRequirementSearchQuickkeyPress(event, 'reqId',<%=searchProjectId %>, <%=callingRequirementId %>); "
								>
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;
							
								<a href='#' class='btn btn-sm btn-primary' style='color:white'
								 onClick="RequirementSearchQuick('reqId',<%=searchProjectId %>, <%=callingRequirementId %>);">
								 Search
								 </a>
							
							&nbsp;&nbsp;&nbsp;&nbsp;
							<a href='#' 
								class='btn btn-sm btn-danger' 
								style='color:white'
								onClick='
									document.getElementById("quickTraceDiv").style.display="none";
								'
							>Cancel</a>
						
						</td>
					</tr>
					<tr>
						<td colspan='2'>
						<div id = 'searchFormResultsQuickDiv'  class='level2Box'>	</div>
						</td>
					</tr>
					<%
					} %>
				</table>
			
			
		    	 
	    <%	 
	    }
	    %>
	</div>
</body>

