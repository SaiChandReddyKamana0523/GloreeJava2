<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String displayAllRequirementsIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayAllRequirementsIsLoggedIn == null) || (displayAllRequirementsIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
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
		//Note since, the call is coming from a virtual folder id, the folder id will be of the format 
		// -1:requirement_type_id
		String folderId = request.getParameter("folderId");
		int requirementTypeId = Integer.parseInt(folderId.substring(3));
		RequirementType rT  = new RequirementType (requirementTypeId);
		ArrayList requirements = ProjectUtil.getAllRequirementsInRT(requirementTypeId,"deleted", databaseType);
		
		
		// for pagination, lets set the pageSize.
		int pageSize = 100;
		// we add 1 to arraysize/pageSize because, int div truncates things.
		int numOfPages = (requirements.size() / pageSize) + 1 ; 	
		
				
		int pageToDisplay = 1;
		if (request.getParameter("page") != null){
			pageToDisplay = Integer.parseInt(request.getParameter("page"));	
		}
		int pageStartIndex = (pageToDisplay * pageSize) - pageSize;
		int pageEndIndex = pageStartIndex + pageSize;
		if (pageEndIndex > requirements.size()){
			pageEndIndex = requirements.size();
		}
		
		
		String pageString = "";
		for (int i=1;i<=numOfPages;i++){
			if (i == pageToDisplay){
				pageString += "<b>" + i + "</b>&nbsp;&nbsp;|&nbsp;&nbsp;";
			}
			else {
				pageString += "<a href='#' onclick='reportPagination(\"requirementsInVirtualFolder\",\"" 
						+ folderId + "\"," +
						i +  ")'> " + i + " </a>" ;
				pageString += "&nbsp;&nbsp;|&nbsp;&nbsp; ";	
			}
		}
		// drop the last nbsp;
		pageString = (String) pageString.subSequence(0,pageString.lastIndexOf("&nbsp;&nbsp;|&nbsp;&nbsp;"));

	
	%>
	<div id='displayAllRequirementsDiv' class='invisibleLevel1Box'> 
		<table class='table table-striped' >
			<tr>
				<td colspan="5"> 
				<b>Deleted Requirements
				<%if ((rT.getRequirementTypeName() != null) && (!(rT.getRequirementTypeName().equals("")))) { %> 
				 in <img src="/GloreeJava2/images/folder.png"> '<%=rT.getRequirementTypeName()%>'
				 <%} %>
				</b></td>
			</tr>
			<tr>
				<td>
					<table id = "requirements" class='table table-striped'>
						<tr>
							<td colspan='5'>
								<span class='headingText'> Page : </span>
									<%=pageString%>
							</td>
						</tr>
					
			<%
				
		    	if (requirements != null){
			    	for (int i=pageStartIndex; i<pageEndIndex;i++){
			    		Requirement r = (Requirement) requirements.get(i);

						
			    		String targetDiv = "requirementPromptDiv" + r.getRequirementId();
			    		String displayRDInFolderDiv = "displayRDInFolderDiv" + r.getRequirementId();
			 %>
		 				<tr id="<%=r.getRequirementId()%>">
					 		<td >
		 						<%
		 						// lets put spacers here for child requirements.
		 						  String req = r.getRequirementFullTag();
		 					   	  int start = req.indexOf(".");
					    		  while (start != -1) {
					    	            start = req.indexOf(".", start+1);
										out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");				    	         
					  	          }
		 						%>							 		
					 		
		 						<span class='normalText' >
		 						<a href="#" 
		 						onclick= 'displayRequirementDescription(<%=r.getRequirementId()%>,"<%=displayRDInFolderDiv%>")'> 
		 						<img src="/GloreeJava2/images/search16.png"  border="0">
		 						</a> 
											 						
		 						<a href="#" onClick="displayRequirement(<%=r.getRequirementId()%>)">
		 						&nbsp;<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %>
		 						</a> 
		 						</span>
		 						<div id = '<%=displayRDInFolderDiv%>'> </div>
		 						<div id='<%=targetDiv%>' style='display:none;'></div>
		 					</td>	
		 					
		 					<td >
		 						<span class='normalText' >
		 						<%=r.getFolderPath() %> 
		 						</span>
		 					</td>	
		 					<td>
		 						
								<%String url = ProjectUtil.getURL(request,r.getRequirementId(),"requirement"); %>
								<a href="#" style='color:white' class='btn btn-sm btn-primary' 
									onclick="window.open('<%=url%>');">Open in a New Tab</a>
		 					</td>
		 					<td>
		 						<a href="#"  style='color:white' class='btn btn-sm btn-success' 
		 							 onclick="restoreRequirement('<%=r.getRequirementId()%>','<%=r.getFolderId()%>')">Restore</a>
		 					</td>
		 					<%
								if (securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
							%>
		 					<td>
						 		<%	String virtualFolderId = "-1:" + r.getRequirementTypeId(); %>
		 						<a href='#' style='color:white' class='btn btn-sm btn-danger'  
		 						onClick='purgeRequirementForm("<%=r.getRequirementId() %>","<%=virtualFolderId%>")'> Purge </a>
		 					</td>
		 					<%} %>
		 							
		 				</tr>
			 <%
			    	}
			    }
			%>
					</table>
				</td>
			</tr>
		</table>
	</div>
		
<%} %>		