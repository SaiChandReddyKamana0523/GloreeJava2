<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String IsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((IsLoggedIn == null) || (IsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	int projectId = Integer.parseInt(request.getParameter("projectId"));
	String JID = request.getParameter("JID");
	String JURL = request.getParameter("JURL");
	
	Requirement jiraProxy = JiraUtil.getProxyInProject(JID, JURL, projectId, securityProfile.getUser(), securityProfile, databaseType);
	if (!(securityProfile.getRoles().contains("MemberInProject" + projectId))){
		//User is NOT a member of this project. so do nothing and return.
		return;
	}
	Project project = new Project(projectId, databaseType);
	
	ArrayList requirements = null;
			
	String searchType = request.getParameter("searchType");
	try {
	if (searchType.equals("folder")){
		int inputFolderId = Integer.parseInt(request.getParameter("folderId"));
		Folder folder = new Folder(inputFolderId);
		
		requirements = folder.getMyRequirements(projectId, databaseType);
	}
	else {
		String searchString = request.getParameter("searchString");
		
		if (searchType.equals("reqId")) {
			// if the user is searching by req id , lets replace the spaces with ,
			// so when we split by , we can ignore those with no values in between
			// this logic works with BR-1,BR-2 and BR-1 BR-2 and BR-1,BR-2    BR-3,   Br-5
			
			if ((searchString != null) && (searchString.contains(" "))){
				searchString = searchString.replace(" ",",");
			}
		}
		// get an ArrayList of requirements.
		requirements = ReportUtil.getProjectSearchReport(securityProfile, 
			project.getProjectId(),searchString, searchType, databaseType);		
	}
	}
	catch (Exception e){
			e.printStackTrace();
	}
	
%>
<div>
	<table class='paddedTable' width='1200px'>
		<tr>
			<td bgcolor="#99ccff" align="left">				
				<span class="subSectionHeadingText">
					TraceCloud Requirements 
				</span>
			</td>		
		</tr>
		
		<tr>
			<td>
			<div id ='reportData' class='level2Box'>
				<table id = "Report" width='100%'>				
	
					<%
					    if (requirements != null){
					    	if (requirements.size() ==0){
				   	%>
						    		<tr>
						    			<td >
						    				<div class='alert alert-success'>
						    					<span class='normalText'> There are no requirements that match this criteria.
						    					</span>
						    				</div>
						    			</td>
						    		</tr>
						    	
				   	<%
						    	}
					    	Iterator i = requirements.iterator();
					    	int j = 0;
					    	String cellStyle = "normalTableCell";
					    	while ( i.hasNext() ) {
					    		Requirement r = (Requirement) i.next();
					    		j++;
					    		if (j > 400) {
					 %>
						    		<tr>
						    			<td >
						    				<div class='alert alert-success'>
						    					<span class='normalText'> We are showing the first 400 requirements. To download the
						    					entire Requirement Set please click 
						    					<a href='/GloreeJava2/servlet/ReportAction?action=exportProjectMetricsReportToExcel' target='_blank'>
							    				<img src="/GloreeJava2/images/ExportExcel16.gif"  border="0"></a>
						    					</span>
						    				</div>
						    			</td>
						    		</tr>
					 <%
					 			break;
					    		}
					    		
					    		// lets get the folderId  based on whether the req is deleted or not.
					    	 	String folderId = "";
					    		if (r.getDeleted() == 0 ){
					    			// not deleted. Hence folderId is the realfolder id.
					    			folderId = Integer.toString(r.getFolderId());
					    		}
					    		else {
					    			// this is a deleted Req. here we create a virtual folderid
					    			// which is -1ReqTypeId.
					    			folderId = "-1:" + r.getRequirementTypeId();
					    		}
					    		
					    		String displayRDInReportDiv = "displayRDInReportDiv" + r.getRequirementId();
					 %>
				 				<tr onMouseOver= "  
				 						this.style.background='#E5EBFF'; 
				 						document.getElementById('pushJiraToTraceCloudDiv<%=r.getRequirementId()%>').style.display='block';
				 						"
				 						
				 						onMouseOut=  "
				 						this.style.background='white'; 
				 						document.getElementById('pushJiraToTraceCloudDiv<%=r.getRequirementId()%>').style.display='none';
				 						"
				 						
				 						>
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
							 		
							 			<span>
							 			<a href="#" onclick= 'displayRequirementDescription(<%=r.getRequirementId()%>
							 				,"<%=displayRDInReportDiv%>")'> 
		 								<img src="/GloreeJava2/images/search16.png"  border="0">
		 								</a>
		 								
		 								<%if (!(r.getRequirementLockedBy().equals(""))){
											// this requirement is locked. so lets display a lock icon.
										%>
											<span class='normalText' title='Requirement locked by <%=r.getRequirementLockedBy()%>'> 
					        					<img src="/GloreeJava2/images/lock16.png" border="0"> 
					        				</span>	
										<%
										}
		 								%>
		 								&nbsp;
		 								
		 								<a href="/GloreeJava2/servlet/DisplayAction?dO=req&dReqId=<%= r.getRequirementId() %>" target="_blank" >
		 									<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %></a> 
		 								</span>
							 		
							 			<div id='pushJiraToTraceCloudDiv<%=r.getRequirementId()%>' style='display:none;'>
							 			
								 			<span class='normalText'>
								 			<br>
								 			<%
								 			
								 			boolean traceToPermitted = false;
								 			if (securityProfile.getPrivileges().contains("traceToRequirementsInFolder" 
									    			+ r.getFolderId())){
								 				traceToPermitted = true;
									    	}
								 			String requirementTracesFrom = r.getRequirementTraceFrom() + ",";
								 			String jiraProxyFullTag = "";
								 			if (jiraProxy != null ){
								 				jiraProxyFullTag = jiraProxy.getRequirementFullTag() + ",";
								 			}
								 			if (jiraProxy == null){
								 				// this Jira Proxy  doesn't exist in this project, so lets create one.
								 				if (traceToPermitted){
									 				%>
									 				<input style='width:300px' type='button'  
									 					id='createTrace<%=r.getRequirementId() %>'  
									 					value='Create Jira Proxy and Connect to <%=r.getRequirementFullTag() %>' 
										 				onClick='pushJiraToTraceCloud(<%=r.getProjectId()%>, <%=r.getRequirementId() %>, "<%=r.getRequirementFullTag() %>");' >
									 				<%
								 				}
								 				else {
								 					%>
								 					<span class='normalText' style='border:3px solid red;  padding:8px; margin:8px;'><font color='red'><b>
								 						You do not have permissions to trace to <%=r.getRequirementFullTag()%></b>
								 					</font></span>		
								 					<% 
								 				}
								 			}
								 			else {
								 				// Jira Proxy exists in the target project
								 				if (requirementTracesFrom.contains(jiraProxyFullTag)){
								 					// already connected
									 				%>
									 			
									 				<span class='normalText' style='border:3px solid red;  padding:8px; margin:8px;'><font color='red'><b>
									 					Alread Connected
									 				</span>
									 				<%
									 			}
									 			else {
									 				if (traceToPermitted){
												 		%>
											 			<input style='width:300px' type='button'   
											 				id='createTrace<%=r.getRequirementId() %>'  
											 				value='Connect Jira Proxy <%=jiraProxy.getRequirementFullTag() %> to <%=r.getRequirementFullTag() %>' 
											 				onClick='pushJiraToTraceCloud(<%=r.getProjectId()%>, <%=r.getRequirementId() %>, "<%=r.getRequirementFullTag() %>");' >
											 			<%
									 				}
									 				else {
									 					%>
									 					<span class='normalText' style='border:3px solid red;  padding:8px; margin:8px;'><font color='red'><b>
									 						You do not have permissions to trace to <%=r.getRequirementFullTag()%><b>
									 					</font></span>		
									 					<% 
									 				}
										 		} 
										 		
									 		}%>
								 			</span>
								 			<br>
							 			</div>
							 		</td>										
										
				 				</tr>
				 				<tr>
				 					<td    colspan='2'>
				 						<div id = '<%=displayRDInReportDiv%>'> </div>
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



