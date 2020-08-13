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
	

	String source = request.getParameter("source");
	if (source==null){source="Requirement";}
	
	
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
	 
	
	<div id = 'displayListReportDiv' class='level1Box'>
	<table class='paddedTable' width='100%' >
		<tr>
			<td bgcolor="#99ccff" align="left">				
				<span class="subSectionHeadingText">
					<%
					String searchHeading  = "";
					if (searchType.equals("google")){
						searchHeading = "Keyword search for '" + searchString + "' ";
					}
					if (searchType.equals("reqId")){
						searchHeading = "ID search for '" + searchString + "' ";
					}
					if (searchType.equals("folderId")){
						searchHeading = "Folder '" + f.getFolderPath() + "' ";
					}
					%>
					Search Results for : <%=searchHeading %>
				</span>
			</td>		
		</tr>
		<tr>
			<td>
				<div id ='reportData' class='level2Box'>
				<table class='table' id = "Report"  style='text-align:left' >				
	
					<%
					    if (searchResults!= null){	
					    	if (searchResults.size() ==0){
				   	%>
						    		<tr>
						    			<td >
						    				<div class='alert alert-success'>
						    					<span class='normalText'> Your search did not return any Requirements. Please change your search string
						    					and try again.
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
					    		j++;
					    		if (j > 500){
					    			// get out of the loop
					    			%>
					    			<tr>
					    				<td>
					    					<span class='normalText'>
					    					<font color='red'>
					    					There are more than 500 requirements in this folder. Showing the first 500.
					    					</font>
					    					</span>
					    				</td>
					    			</tr>
					    			<%
					    			break;
					    		}
					    		
					    		// for searchType = keyword ,then narrow the display to the requested req type.
					    		if (searchType.equals("google") && (searchRequirementTypeId > 0 )){
					    			// we have been asked to show only type of requirements.
					    			if (r.getRequirementTypeId() != searchRequirementTypeId  ){
					    				continue;
					    			}
					    		}
					    		// for the first row, print the header and user defined columns etc..
					    		
					 %>
				 				<tr >
							 		<td align='left'>
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
			 						</td>
			 					</tr>
			 					<tr>
									<td align='left'>
		 									<div id='traceActionDiv<%=r.getRequirementId() %>' style='display:block'>
		 										<div id='messageDivId<%=r.getRequirementId() %>' style='display:block'>
		 											<table style='width:100%'>
		 												
		 												<tr>
		 													
		 														<%
		 														String disabledString  = "";
		 														if (
		 																(callingRequirementTraceTo.contains(thisRequirementFullTag))
		 																||
		 																(callingRequirementTraceFrom.contains(thisRequirementFullTag))
		 															){
		 															// lets print the ALREADY exists messaeg
		 															
		 															if (callingRequirementTraceTo.contains(thisRequirementFullTag)){
		 																%>
				 														<td colspan='2'>
				 														<div class='alert alert-success' >
					 														<span class='normalText'>
					 															<%=callingRequirementFullTag%> ALREADY Traces To <%=thisRequirementFullTag %>
					 														</span>
				 														</div>
				 														</td>
				 														<%	
		 															}
		 															
		 															if (callingRequirementTraceFrom.contains(thisRequirementFullTag)){
				 														%>
				 														<td colspan='2'>
				 														<div class='alert alert-success' >
					 														<span class='normalText'>
					 															<%=thisRequirementFullTag%> ALREADY Traces To <%=callingRequirementFullTag %>
					 														</span>
				 														</div>
				 														</td>
				 														<%
				 													}
		 															
		 														}
		 														else {
																	%>

				 													<td bgcolor="white" 
				 														onmouseover="this.bgColor='#d3d3d3'" 
				 														onmouseout="this.bgColor='white'" 
				 														onClick='createTraceFromCallingRequirementId(<%=callingRequirementId%>, <%=r.getRequirementId()%>,"<%=thisRequirementFullTag %>","<%=source %>")' 
				 														style="cursor:pointer" 
				 														title="<%=callingRequirementFullTag %> TRACES UPSTREAM TO <%=thisRequirementFullTag %>  ">
				 														<table >
				 															<tr>
				 																<td> <%=thisRequirementFullTag %></td>
				 																<td></td>
				 																<td></td>
				 															</tr>
				 															<tr>
				 																<td> </td>
				 																<td>
				 																	<img src="/GloreeJava2/images/cTrace2.jpg" border="0" >
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
				 														onClick='createTraceToCallingRequirementId(<%=callingRequirementId%>, <%=r.getRequirementId()%>,"<%=thisRequirementFullTag %>","<%=source %>")'
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
				 																	<img src="/GloreeJava2/images/cTrace2.jpg" border="0" >
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
		 															
		 															<%
		 														}
		 													
		 														%>
		 													
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