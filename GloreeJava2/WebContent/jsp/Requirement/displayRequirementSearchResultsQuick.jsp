<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>


<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	int callingRequirementId  = Integer.parseInt(request.getParameter("callingRequirementId"));
	Requirement callingRequirement = new Requirement(callingRequirementId, databaseType);
	String callingRequirementFullTag = callingRequirement.getRequirementFullTag();
	String callingRequirementTraceTo = callingRequirement.getRequirementTraceTo();
	String callingRequirementTraceFrom = callingRequirement.getRequirementTraceFrom();
	
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dPSRIsMember = false;
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		dPSRIsMember = true;
	}
%>

<%if (dPSRIsMember){ %>


	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	
	<%
				String searchString = request.getParameter("searchString");
				String originalSearchString = searchString;
				String searchType = "";
				searchType = request.getParameter("searchType");
				if (searchType == null ){
					searchType = "";
				}
				
				int searchRequirementTypeId = 0;
				try {
					searchRequirementTypeId = Integer.parseInt(request.getParameter("searchRequirementTypeId"));
				}
				catch (Exception e){
					// do nothing.
				}

				int searchProjectId = 0;
				String searchProjectPrefix = "";
				try {
					searchProjectId = Integer.parseInt(request.getParameter("searchProjectId"));
					searchProjectPrefix =  ProjectUtil.getProjectShortName(searchProjectId);
				}
				catch (Exception e){
					// do nothing
				}
				// if no search project id was sent in, then the current project is the search project.
				if (searchProjectId == 0 ){
					searchProjectId = project.getProjectId();
					searchProjectPrefix = project.getShortName();
				}

				
				if (searchType.equals("reqId")) {
					// if the user is searching by req id , lets replace the spaces with ,
					// so when we split by , we can ignore those with no values in between
					// this logic works with BR-1,BR-2 and BR-1 BR-2 and BR-1,BR-2    BR-3,   Br-5
					
					if ((searchString != null) && (searchString.contains(" "))){
						searchString = searchString.replace(" ",",");
					}
				}
				// get an ArrayList of requirements.
				ArrayList searchResults  = new ArrayList();
				Folder f = null;
				if (searchType.equals("folderId") ){
					int folderId = Integer.parseInt(request.getParameter("folderId"));
					f = new Folder(folderId);
					searchResults = f.getMyRequirements(f.getProjectId(), databaseType);
				}
				else {
					searchResults = ReportUtil.getProjectSearchReport(securityProfile, 
					searchProjectId,searchString, searchType, databaseType);
				}
			%>
	 
	
	<div id = 'displayListReportDiv' >
	<table class='paddedTable' width='100%'  >
		
		<tr>
			<td>
				<div id ='reportData' class='level2Box'>
				<table id = "Report"   >				
	
					<%
					    if (searchResults!= null){	
					    	if (searchResults.size() ==0){
				   	%>
						    		<tr>
						    			<td >
						    				<div class='alert alert-success'>
						    					<span class='normalText'> Your search did not return any Requirements.
						    					</span>
						    				</div>
						    			</td>
						    		</tr>
						    	
				   	<%
						    	}
					    	Iterator i = searchResults.iterator();
					    	int j = 0;
					    	while ( i.hasNext() ) {
					    		Requirement r = (Requirement) i.next();
					    		if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
					    				+ r.getFolderId()))){
					    			r.redact();
					    		}
					    		
					    		String thisRequirementFullTag = r.getRequirementFullTag();
					    		if (r.getProjectId() != project.getProjectId()){
					    			thisRequirementFullTag = searchProjectPrefix + ":" + r.getRequirementFullTag();
					    		}
					    		String displayRDInReportDiv = "displayRDInReportDiv" + r.getRequirementId();
					    		
					    		
					    		// for the first row, print the header and user defined columns etc..
					    		
					 %>
				 				<tr >
							 		<td align='left' style='width:500px'>
							 			<%if (r.getProjectId() == project.getProjectId()){ %>
							 				<a href="#" onclick="
							 					document.getElementById('createTracesDiv').style.display='none';
							 					displayFolderInExplorer(<%=r.getFolderId() %>);
												displayFolderContentCenterA(<%=r.getFolderId() %>);
												displayRequirement(<%=r.getRequirementId()%>,'');
							 				">
			 									<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %></a> 
			 							<%}
							 			else { %>
							 				<span class='normalText'>
							 				<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %>
							 				</span>
							 			<%} %>
			 								
		 									<div id='traceActionDiv<%=r.getRequirementId() %>' >
		 										<div id='messageDivId<%=r.getRequirementId() %>' style='display:block'>
		 											
													<%
													String disabledString  = "";
													if (callingRequirementTraceTo.contains(thisRequirementFullTag)){
														disabledString = "DISABLED=DISABLED";
														%>
														<div class='alert alert-danger' >
															<span class='normalText'>
																<%=callingRequirementFullTag%> <b>Currently</b> Traces To <%=thisRequirementFullTag %>
															</span>
														</div>
														<%
													}
													if (callingRequirementTraceFrom.contains(thisRequirementFullTag)){
														disabledString = "DISABLED=DISABLED";
														%>
														<div class='alert alert-danger' >
															<span class='normalText'>
																<%=thisRequirementFullTag%> <b>Currently</b>Traces To <%=callingRequirementFullTag %>
															</span>
														</div>
														<%
													}
													%>
												
		 										
		 										</div>
		 										
		 										<div>
		 											<table class='table'  width='100%'>
		 												<tr>
		 												
		 													<td bgcolor="white" 
		 														onmouseover="this.bgColor='#d3d3d3'" 
		 														onmouseout="this.bgColor='white'" 
		 														onClick='createTraceFromCallingRequirementId(<%=callingRequirementId%>, <%=r.getRequirementId()%>,"<%=thisRequirementFullTag %>")' 
		 														style="cursor:pointer" 
		 														title="<%=callingRequirementFullTag %> TRACES UPSTREAM TO <%=thisRequirementFullTag %>  ">
		 														<table  borde='1'>
		 															<tr>
		 																<td> <%=thisRequirementFullTag %></td>
		 																<td></td>
		 																<td></td>
		 															</tr>
		 															<tr>
		 																<td> </td>
		 																<td>
		 																	<img src="/GloreeJava2/images/cTrace2.jpg"  >
		 																</td>
		 																<td></td>
		 															</tr>
		 															<tr>
		 																<td> </td>
		 																<td></td>
		 																<td><%=callingRequirementFullTag %></td>
		 															</tr>
		 															
		 														</table>
		 													</td>
		 													
		 													
		 													<td bgcolor="white" 
		 														onmouseover="this.bgColor='#d3d3d3'" 
		 														onmouseout="this.bgColor='white'" 
		 														onClick='createTraceToCallingRequirementId(<%=callingRequirementId%>, <%=r.getRequirementId()%>,"<%=thisRequirementFullTag %>")'
		 														style="cursor:pointer" 
		 														title="<%=thisRequirementFullTag %> TRACES UPSTREAM TO <%=callingRequirementFullTag %> ">
		 														<table >
		 															<tr>
		 																<td> <%=callingRequirementFullTag %></td>
		 																<td></td>
		 																<td></td>
		 															</tr>
		 															<tr>
		 																<td> </td>
		 																<td>
		 																	<img src="/GloreeJava2/images/cTrace2.jpg" >
		 																</td>
		 																<td></td>
		 															</tr>
		 															<tr>
		 																<td> </td>
		 																<td></td>
		 																<td><%=thisRequirementFullTag %></td>
		 															</tr>
		 															
		 														</table>		 														
		 													</td>
		 												</tr>
		 												
			 											</table>
			 										</div>
								 			
								 			</div>
								 		
							 		</td>
							 	</tr>
							 	
				 				
					 <%
					    	}
					    }
					%>
				
				</table>
				</div>
			</td>
		</tr>
	</table>
	</div>
<%}%>