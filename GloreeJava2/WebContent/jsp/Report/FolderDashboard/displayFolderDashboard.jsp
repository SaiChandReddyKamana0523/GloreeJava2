<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>


<%
	// authentication only
	String displayListReportIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayListReportIsLoggedIn == null) || (displayListReportIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");

	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dRIsMember = false;
	Project project= (Project) session.getAttribute("project");
	SecurityProfile dRSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dRSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		dRIsMember = true;
	}
	
	int folderId = Integer.parseInt(request.getParameter("folderId"));
	Folder folder = new Folder(folderId);
	
	User user= dRSecurityProfile.getUser();   
	
	String displaySavedReportsAndTemplates = request.getParameter("displaySavedReportsAndTemplates");
	if (displaySavedReportsAndTemplates == null){
		displaySavedReportsAndTemplates = "";
	}
%>

<%if (dRIsMember){ %>


	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	

	 
	
	<div id = 'displayReleaseMetricsDiv' class='level1Box'>
		<input type='hidden' name='action' value = ''>	
		
		
		<table >
			<tr>
				<td>
					<div id='folderMetricsDataTableDiv' style='display:none;'></div>
				</td>
			</tr>
		</table>	
		

		<% if (displaySavedReportsAndTemplates.equals("true")) {%>
		<div id='folderSavedReportsAndTemplatesDiv'  >	
			
		<table class='paddedTable' width='100%'>
			
			
		
			<tr>
				<td valign='top'>
					<table>
						
						<tr>
							<td colspan='2'>
								<span class='sectionHeadingText'>
								General Reports
								</span>
							</td>
						</tr>		
						<tr>
							<td colspan='2'>
								<span class='headingText'>
									<a href='#' onclick='displayFolderMetrics2( "trends", <%=folder.getFolderId()%>);'>
									<img src="/GloreeJava2/images/chart_bar16.png" border="0">
									<img src="/GloreeJava2/images/chart_pie16.png" border="0">
									Folder Trends
									</a>
								</span>
							</td>
						</tr>
										
						<%
							ArrayList reports = folder.getMyReports(project.getProjectId());
						    if (reports != null){
						    	Iterator i = reports.iterator();
						    	while ( i.hasNext() ) {
						    		Report r = (Report) i.next();
						    		if (r.getReportDescription().startsWith("Canned")){
						    			// this is a canned report.
						 %>
						 				
									 		
						 						<%if (r.getReportType().equals("list")){

													// we have decided to not show the list reports at a folder level
													// as all the info is already in the metrics line report%>
						 							<!-- 
													<tr id="<%=r.getReportId()%>">
														<td colspan=2>
															<span class='normalText' title="Description : <%=r.getReportDescription()%>">
															<a href="#" 
															onClick="displayExistingReport(<%=folder.getFolderId()%>, <%=r.getReportId()%>,'list')">
															<img src="/GloreeJava2/images/report16.png" border="0">
															&nbsp;<%=r.getReportName() %></a>
															</span>
														</td>			
													</tr>
													-->
						 						<%} else { %>
													<tr id="<%=r.getReportId()%>">
														<td colspan=2>
															<span class='normalText' title="Description : <%=r.getReportDescription()%>">
						 					
															<a href="#" onClick="displayExistingReport(<%=folder.getFolderId()%>, <%=r.getReportId()%>,'traceTree')">
															<img src="/GloreeJava2/images/reportTraceTree.png" border="0">
															&nbsp;<%=r.getReportName() %></a>
															</span>
														</td>			
													</tr>
						 						<%} %>
						 						
						 <%
						    		}
						    	}
						    }
						  %>						
								
					</table>
				</td>
				<td valign='top'>
					<table class='paddedTable' width='100%'>
						<tr><td colspan='2'>
							<span class='sectionHeadingText'>
							Saved Reports
							</span>
						</td></tr>
						  <%
						  	int reportCount= 0;
						    if (reports != null){
						    	Iterator j = reports.iterator();
						    	while ( j.hasNext() ) {
						    		Report r = (Report) j.next();
						    		if (
						    				!(r.getReportDescription().startsWith("Canned"))
						    				&&
						    				(r.getReportVisibility().equals("private"))
						    				&&
						    				(r.getCreatedByEmailId().equals(user.getEmailId()))
						    			) 
						    		
						    		
						    		{
						    			reportCount++;
						    			// this is a user definedreport and is set as a privately visible report
						    			// and is owned by the signed in user.
						   			 %>
						 				<tr id="<%=r.getReportId()%>">
									 		<td colspan=2>
						 						<span class='normalText' title="Description : <%=r.getReportDescription()%>">
						 						<%if (r.getReportType().equals("list")){ %>
						 							<a href="#" 
						 							onClick="displayExistingReport(<%=folder.getFolderId()%>, <%=r.getReportId()%>,'list')">
						 							<img src="/GloreeJava2/images/report16.png" border="0">
						 							&nbsp;<%=r.getReportName() %></a> 
						 						<%} else { %>
						 							<a href="#" onClick="displayExistingReport(<%=folder.getFolderId()%>, <%=r.getReportId()%>,'traceTree')">
						 							<img src="/GloreeJava2/images/reportTraceTree.png" border="0">
						 							&nbsp;<%=r.getReportName() %></a> 
						 						<%} %>
						 						
						 						</span>
						 					</td>			
						 				</tr>
						 <%
						    		}
						    	}
						    }
						
						%>


						  <%
						  	reportCount = 0;
						    if (reports != null){
						    	Iterator j = reports.iterator();
						    	while ( j.hasNext() ) {
						    		Report r = (Report) j.next();
						    		if (
						    				!(r.getReportDescription().startsWith("Canned"))
						    				&&
						    				(r.getReportVisibility().equals("public"))
						    			) 
						    		
						    		
						    		{
						    			reportCount++;
						    			// this is a user definedreport and is set as a Publicly visible report
						   			 %>
						 				<tr id="<%=r.getReportId()%>">
									 		<td colspan=2>
						 						<span class='normalText' title="Description : <%=r.getReportDescription()%>">
						 						<%if (r.getReportType().equals("list")){ %>
						 							<a href="#" 
						 							onClick="displayExistingReport(<%=folder.getFolderId()%>, <%=r.getReportId()%>,'list')">
						 							<img src="/GloreeJava2/images/report16.png" border="0">
						 							&nbsp;<%=r.getReportName() %></a> 
						 						<%} else { %>
						 							<a href="#" onClick="displayExistingReport(<%=folder.getFolderId()%>, <%=r.getReportId()%>,'traceTree')">
						 							<img src="/GloreeJava2/images/reportTraceTree.png" border="0">
						 							&nbsp;<%=r.getReportName() %></a> 
						 						<%} %>
						 						
						 						</span>
						 					</td>			
						 				</tr>
						 <%
						    		}
						    	}
						    }	
						   						    
						 %>
			  
					</table>
				</td>




				<td valign='top'>
					<table class='paddedTable' width='100%'>
						<tr><td colspan='2'>
							<span class='sectionHeadingText'>
							Word Templates
							</span>
						</td></tr>
						<%			  
							ArrayList wordTemplates = folder.getMyWordTemplates(project.getProjectId(), databaseType);
							reportCount = 0;
						    if (wordTemplates != null){
						    	Iterator i = wordTemplates.iterator();
						    	while ( i.hasNext() ) {
						    		WordTemplate wordTemplate = (WordTemplate) i.next();
						    		String templateVisibility = wordTemplate.getTemplateVisibility();
									if (
											(templateVisibility != null) && 
											(templateVisibility.equals("private")) &&
											(wordTemplate.getCreatedBy().equals(user.getEmailId()))){
										reportCount++;
						 %>
						 				<tr id="<%=wordTemplate.getTemplateId() %>">
									 		<td colspan=2>
						 						<span class='normalText' title="Description : <%=wordTemplate.getTemplateDescription() %>">
					 							<a href="#" 
						 							onClick="displayWordTemplate(<%=folder.getFolderId()%>, <%=wordTemplate.getTemplateId() %>)">
						 							<img src="/GloreeJava2/images/ExportWord16.gif" border="0">
						 						&nbsp;<%=wordTemplate.getTemplateName()%></a> 
						 						</span>
						 					</td>			
						 				</tr>
						 <%
						    		}
						    	}
						    }
						   						    
						%>					
					
					
					
					
					
					
					
					
						  
						<%			  
							reportCount = 0;
						    if (wordTemplates != null){
						    	Iterator i = wordTemplates.iterator();
						    	while ( i.hasNext() ) {
						    		WordTemplate wordTemplate = (WordTemplate) i.next();
						    		String templateVisibility = wordTemplate.getTemplateVisibility();
									if ((templateVisibility != null) && (templateVisibility.equals("public"))){
										reportCount++;
						 %>
						 				<tr id="<%=wordTemplate.getTemplateId() %>">
									 		<td colspan=2>
						 						<span class='normalText' title="Description : <%=wordTemplate.getTemplateDescription() %>">
					 							<a href="#" 
						 							onClick="displayWordTemplate(<%=folder.getFolderId()%>, <%=wordTemplate.getTemplateId() %>)">
						 							<img src="/GloreeJava2/images/ExportWord16.gif" border="0">
						 						&nbsp;<%=wordTemplate.getTemplateName()%></a> 
						 						</span>
						 					</td>			
						 				</tr>
						 <%
						    		}
						    	}
						    }
						  						    
						%>			  
					</table>
				</td>				


			</tr>
		</table>
		</div>
		
		<%} %>
		
		
	</div>
<%}%>

