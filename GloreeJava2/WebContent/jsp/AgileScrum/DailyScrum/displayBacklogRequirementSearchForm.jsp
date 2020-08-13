<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>
<head>

	<!--  Since this page is likely shown in a different window, we need to load the css and java script files. -->
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/css/common.css"> 
	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userAccount.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userDashboard.js?v=20200630"></script>
	

	<!--  Bootstratp  JS and CSS files -->
	 <script src="/GloreeJava2/js/jquery-3.1.1.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap.min.js"></script>
	 <link href="/GloreeJava2/css/bootstrap.min.css" rel="stylesheet" media="screen">
	
	
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

	int sprintId = Integer.parseInt(request.getParameter("sprintId"));
	Sprint sprint = new Sprint(sprintId, databaseType);
	
	String headerMessage = "Search For Backlog Requirements to add to sprint ;" + sprint.getSprintName() + "'";
	

	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	
	// you need to be a member of this project to see this
	if (isMember ){
%>

	<div id = 'searchForm'  class='level1Box' STYLE="background-color:white;">
		
				<table class='paddedTable'  border="0" width='100%'>
					<tr>
						<td colspan='2' align='left' bgcolor='#99CCFF'>				
							<span class='subSectionHeadingText'>
							<%=headerMessage %>
							</span>
						</td>
					</tr>
					<tr>
						<td colspan='2'>
							<table>
							<tr>
								<td>
								
									<span class='normalText'>Req Id</span>
									&nbsp;&nbsp;&nbsp;<img src="/GloreeJava2/images/search16.png"  border="0">
									<span class='normalText'></span>
									
									
									
									<input type='text' class="input-small" placeholder="Search by Id . Eg: BR-1" 
									 size='30' maxlength='300' name='reqIdSearchString' id='reqIdSearchString'
									 	 onfocus="this.value='';"
										onkeypress=" handleRequirementBacklogSearchkeyPress(event, 'reqId',<%=sprintId%>);"
									>
									<input type='button' class='btn btn-primary btn-sm' style='width:100px' name='Go' value='  Go  ' onClick="RequirementBacklogSearch('reqId',<%=sprintId%>);">
								
								</td>
							</tr>		
							<tr>
								<td>
								
									<span class='normalText'>Keyword</span>
									<img src="/GloreeJava2/images/search16.png"  border="0">
									<input type='text' class="input-small" placeholder="Google like free text search"  
									size='30' maxlength='300' name='googleSearchString' id='googleSearchString'
										onfocus="this.value='';"
										onkeypress=" handleRequirementBacklogSearchkeyPress(event, 'google',<%=sprintId%>); " 
									>
									<input type='button' class='btn btn-primary btn-sm' style='width:100px' name='Go' value='  Go  ' onClick="RequirementBacklogSearch('google',<%=sprintId%>)">
								
								</td>
							</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td colspan='2'>
						<div id = 'searchFormResultsDiv'  class='level2Box'>	
						</td>
					</tr>
				</table>
			
			
		    	 
	    <%	 
	    }
	    %>
	</div>
</body>

