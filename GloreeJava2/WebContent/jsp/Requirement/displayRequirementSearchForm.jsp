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
	User user = securityProfile.getUser();
	
	int callingRequirementId = Integer.parseInt(request.getParameter("callingRequirementId"));
	Requirement callingRequirement = new Requirement(callingRequirementId, databaseType);
	
	String source = request.getParameter("source");
	if (source==null){source="Requirement";}

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
	
	
	String headerMessage =  "Search For Objects to connect with " + callingRequirement.getRequirementFullTag();
	
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	
	// you need to be a member of this project to see this
	if (isMember ){
%>

	<div id = 'searchForm'  class='alert alert-success'
	style='border-color:blue; border-style:dotted solid; border-radius:5px ' >
		
				<table class='table'  border="0" width='100%'>

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
						<td align='center'>				
							<div class='alert alert-danger'>
							<%=headerMessage %>
							</div>
						</td>
					</tr>
					<tr>
						<td colspan='2'>
							<table width='100%' style='text-align:left'>
	
							<tr >
								<td style='align:left' >
									<span class='normalText'>Project</span>
								</td>
							</tr>
							<tr>
								<td >
									<span class='normalText'>
										<select name='searchProjectId' id='searchProjectId'
											onChange="loadCreateTrace2(<%=callingRequirementId%> , '<%=source%>');">
											<%if (project.getProjectId() == searchProjectId) {
											%>
												<option value='<%=project.getProjectId()%>' SELECTED><%=project.getProjectName() %></option>
											<%
											}
											else{
											%>
												<option value='<%=project.getProjectId()%>'><%=project.getProjectName() %></option>
											<%
											}
											ArrayList projectRelationsLite = project.getProjectRelationsLiteWithACL(user.getUserId());
		
											Iterator i = projectRelationsLite.iterator();
											while (i.hasNext()){
												ProjectRelation projectRelation = (ProjectRelation) i.next(); 
												if (searchProjectId == projectRelation.getRelatedProjectId()) {
												%>
												<option value='<%=projectRelation.getRelatedProjectId()%>' SELECTED><%=projectRelation.getRelatedProjectName() %>
												<%											
												}
												else{
												%>
												<option value='<%=projectRelation.getRelatedProjectId()%>' ><%=projectRelation.getRelatedProjectName() %>
												<%
												}	
											}
											%>
										</select>
									</span>
								</td>
							</tr>
																
							<tr >
								<td  style='align:left'>
									<span class='normalText'>Show objects in Folder</span>
								</td>
							</tr>
							<tr>
								<td >
									<%
									ArrayList folders =  ProjectUtil.getFolderInAProjectLite(searchProjectId);
									i = folders.iterator();
									%>
									<span class='normalText'>
										<select name='searchFolder' id='searchFolder'
											onChange="RequirementSearch('folderId',<%=searchProjectId %>, <%=callingRequirementId %>, '<%=source %>');">
											<option value=''>Select A Search Folder</option>
										<%
											while (i.hasNext()){
												String folderString = (String) i.next();
												int folderId = 0 ;
												String folderPath = "";
												try {
													String [] folderStringSplit = folderString.split(":##:");
													folderId = Integer.parseInt(folderStringSplit[0]);
													folderPath = folderStringSplit[1];
												}
												catch (Exception e){
													e.printStackTrace();
												}
										%>
											<option value='<%=folderId%>'>
												<%=folderPath%>
											</option>
										<%	
											}
										%>
										</select>
										</span>

								</td>
								
							</tr>								
														
							<tr>
								<td  style='align:left'>
									<span class='normalText'>Search By Keyword</span>
								</td>
							</tr>
							<tr>
								<td>
									<span class='normalText'>
										<input type='text' size='20' maxlength='300' name='reqTraceSearchString' id='reqTraceSearchString'
										value=' Any keyword' 
										onFocus='this.value=""'
										onkeypress=" handleRequirementSearchkeyPress(event, 'google',<%=searchProjectId %>, <%=callingRequirementId %>, '<%=source %>'); " 
										>
										in
										<%
										ArrayList requirementTypes = ProjectUtil.getRequirementTypesInAProject(searchProjectId);
										i = requirementTypes.iterator();
										%>
											<select name='searchRequirementTypeId' id='searchRequirementTypeId'
												onChange="RequirementSearch('google',<%=searchProjectId %>, <%=callingRequirementId %>);"
											>
												<option value='0'>All Requirement Types</option>
											<%
												while (i.hasNext()){
													RequirementType requirementType = (RequirementType) i.next();
											%>
												<option value='<%=requirementType.getRequirementTypeId() %>'>
													<%=requirementType.getRequirementTypeName() %>
												</option>
											<%	
												}
											%>
											</select>
									</span>
	&nbsp;&nbsp;&nbsp;&nbsp;
									<span class='normalText'>
										<input type='button' name='Go' value='  Go  ' class='btn btn-primary btn-sm' style='height:25px; width:70px'
										 onClick="RequirementSearch('google',<%=searchProjectId %>, <%=callingRequirementId %>, '<%=source %>')">
									</span>
								</td>
							</tr>
							
							<tr >
								<td  style='align:left'>
									<span class='normalText'>Search By Id</span>
								</td>
							</tr>
							<tr>
								<td >
									<span class='normalText'>
										<input type='text' size='30' 
										value=' An object id like BR-1,fr-2' maxlength='300' name='reqTraceIdId' id='reqTraceId'
										onFocus='this.value=""'
										onkeypress=" handleRequirementSearchkeyPress(event, 'reqId',<%=searchProjectId %>, <%=callingRequirementId %>,'<%=source %>'); " >
									</span>
								&nbsp;&nbsp;&nbsp;&nbsp;
									<span class='normalText'>
										<input type='button' name='Go' value='  Go  '  class='btn btn-primary btn-sm' style='height:25px; width:70px'
										 onClick="RequirementSearch('reqId',<%=searchProjectId %>, <%=callingRequirementId %>, '<%=source %>');">
									</span>
								</td>
							</tr>		

							</table>
						</td>
					</tr>
					<tr>
						<td >
						<div id = 'searchFormResultsDiv'  class='level2Box'>	
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

