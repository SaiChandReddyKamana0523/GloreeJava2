<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>


<%
	// authentication only
	String dPSRIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dPSRIsLoggedIn == null) || (dPSRIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dPSRIsMember = false;
	Project project= (Project) session.getAttribute("project");
	SecurityProfile dPSRSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dPSRSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
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
				
				if (searchType.equals("reqId")) {
					// if the user is searching by req id , lets replace the spaces with ,
					// so when we split by , we can ignore those with no values in between
					// this logic works with BR-1,BR-2 and BR-1 BR-2 and BR-1,BR-2    BR-3,   Br-5
					
					if ((searchString != null) && (searchString.contains(" "))){
						searchString = searchString.replace(" ",",");
					
					}
					
				}
				// get an ArrayList of requirements.
				ArrayList projectSearchReport = ReportUtil.getProjectSearchReport(dPSRSecurityProfile, 
					project.getProjectId(),searchString, searchType, databaseType);
			%>
	 
	
	<div id = 'displayListReportDiv' class='level1Box'>
	<table class='paddedTable' width='100%'>
		<tr>
			<td bgcolor="#99ccff" align="left">				
				<span class="subSectionHeadingText">
					Search Results for : <%=originalSearchString %>
				</span>
			</td>		
		</tr>
		<tr>
			<td>
				<div id ='reportData' class='level2Box'>
				<table id = "Report">				
	
					<%
					    if (projectSearchReport != null){
					    	if (projectSearchReport.size() ==0){
				   	%>
						    		<tr>
						    			<td colspan='7'>
						    				<div class='alert alert-success'>
						    					<span class='normalText'> Your search did not return any Requirements. Please change your search string
						    					and try again.
						    					</span>
						    				</div>
						    			</td>
						    		</tr>
						    	
				   	<%
						    	}
					    	Iterator i = projectSearchReport.iterator();
					    	int j = 0;
					    	String cellStyle = "normalTableCell";
					    	while ( i.hasNext() ) {
					    		Requirement r = (Requirement) i.next();
					    		String displayRDInReportDiv = "displayRDInReportDiv" + r.getRequirementId();
					    		j++;
					    		// for the first row, print the header and user defined columns etc..
					    		if (j == 1){
					 %>
									<tr>
										<td class='tableHeader' width='350'>
											<span class='sectionHeadingText'>
											Requirement 
											</span>
										</td>
										<td class='tableHeader'>
											<span class='sectionHeadingText'>
											Owner
											</span>
										 </td>
										<td class='tableHeader'> 
											<span class='sectionHeadingText'>
											Percent Complete
											</span>
										</td>
										<td class='tableHeader'> 
											<span class='sectionHeadingText'>
											Priority
											</span>
										</td>
										<td class='tableHeader'> 
											<span class='sectionHeadingText'>
											Approval Status 
											</span>
										</td>
										<td class='tableHeader'> 
											<span class='sectionHeadingText'>
											Testing Status 
											</span>
										</td>
										<td class='tableHeader'> 
											<span class='sectionHeadingText'>
											Folder
											</span>
										</td>
									</tr>				 
					<%
					   		 			
					    		}
					    		
					    		// Now for each row in the array list, print the data out.
					    		if ((j%2) == 0){
					    			cellStyle = "normalTableCell";
					    		}
					    		else {
					    			cellStyle = "altTableCell";	
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
					 %>
				 				<tr>
							 		<td class='<%=cellStyle%>'>
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
		 								
		 								<a href="#" 
		 								onClick='
		 									displayFolderInExplorer("<%=folderId %>");
		 									displayFolderContentCenterA("<%=folderId %>");
		 									displayFolderContentRight("<%=folderId %>");
		 									displayRequirement(<%=r.getRequirementId()%>);'>
		 								<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %></a> 
		 								</span>
							 		</td>
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<%=r.getRequirementOwner()%>
							 			</span>
							 		</td>
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<%=r.getRequirementPctComplete()%> %
							 			</span>
							 		</td>
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<%=r.getRequirementPriority()%>
							 			</span>
							 		</td>
										<% if (r.getApprovalStatus().equals("Draft")){ %>
											<td bgcolor='#FFFF66''>
												<span class='normalText'>
													<%=r.getApprovalStatus() %>
												</span>
											</td>										
										<%} %>
										<% if (r.getApprovalStatus().equals("In Approval WorkFlow")){ %>
											<td bgcolor='#99ccff'>
												<span class='normalText'>												
													<%=r.getApprovalStatus() %>
												</span>
											</td>
										<%} %>
										<% if (r.getApprovalStatus().equals("Approved")){ %>
											<td bgcolor='#CCFF99''>
												<span class='normalText'>
													<%=r.getApprovalStatus()%>
												</span>
											</td>
										<%} %>
										<% if (r.getApprovalStatus().equals("Rejected")){ %>
											<td bgcolor='#FFA3AF'>
												<span class='normalText'>
													<%=r.getApprovalStatus()%>
												</span>
											</td>
										<%} %>
										
										
										

										<% if (r.getTestingStatus().equals("Pending")){ %>
											<td bgcolor='#FFFF66'>
												<span class='normalText'>
													<%=r.getTestingStatus() %>
												</span>
											</td>										
										<%} %>
										<% if (r.getTestingStatus().equals("Pass")){ %>
											<td bgcolor='#CCFF99'>
												<span class='normalText'>												
													<%=r.getTestingStatus() %>
												</span>
											</td>
										<%} %>
										<% if (r.getTestingStatus().equals("Fail")){ %>
											<td bgcolor='#FFA3AF'>
												<span class='normalText'>
													<%=r.getTestingStatus()%>
												</span>
											</td>
										<%} %>
										
										
																	 		
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<%=r.getFolderPath()%>
							 			</span>
							 		</td>
				 				</tr>
				 				<tr>
				 					<td  class='<%=cellStyle%>'  colspan='7'>
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
<%}%>