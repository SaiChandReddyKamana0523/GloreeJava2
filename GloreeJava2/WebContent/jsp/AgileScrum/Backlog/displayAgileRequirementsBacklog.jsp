<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn  == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User user = securityProfile.getUser();
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)

	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
		
		int maxPageSize = 200;
		int sprintId = Integer.parseInt(request.getParameter("sprintId"));
		Sprint sprint = new Sprint(sprintId, databaseType);
		int folderId = Integer.parseInt(request.getParameter("folderId"));
		Folder folder = new Folder(folderId);
		ArrayList requirements = folder.getMyRequirements(folder.getProjectId(), databaseType);
		// lets sort the requirements array by task weight desc.
		Collections.sort(requirements, new SortSprintByTaskWeightDesc());
		
	%>

		
		<table class='table table-striped table-hover' width='100%'>
			
			<tr> 
				<td colspan='9'>
					<br></br>
					<span class='headingText'><b>Backlog Requirements</b> are defined as Requirements that are not complete and are not assigned to an Agile Sprint.</span>
					<br></br>
				</td>
			</tr>
						
		<%
		
		Iterator i = requirements.iterator();
		int j = 0;
		String cellStyle = "normalTableCell";
		while ( i.hasNext() ) {
			Requirement r = (Requirement) i.next();
			
			String requirementsAgileSprint = r.getAttributeValue("Agile Sprint");
			if (
				((requirementsAgileSprint== null) || (requirementsAgileSprint.equals("")))
				&&
				(r.getRequirementPctComplete()!= 100 )
				){
					// this means that the req is not part of any valid sprint and is not completed yet and we can display it.
					
					// if the user does not have read permissions, lets redact them.
					if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" + r.getFolderId()))) {
						r.redact();
					}
					// if the req is in a folder on which the user does not have update permissions or if there is a lock onit
					// we gray it out.
					String buttonDisabledString = "";
					if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" + r.getFolderId()))) {
						buttonDisabledString = "DISABLED";
					}
					
					// if this requirement is locked and its locked by someone other than this user, then all updates to this req are disabled.
					if (
						(!(r.getRequirementLockedBy().equals("")))
						&&
						(!(r.getRequirementLockedBy().equals(user.getEmailId())))
						){
						buttonDisabledString = "DISABLED";
					}
					
					
					int taskWeight = 0;
					try {
						taskWeight = Integer.parseInt(r.getAttributeValue("Agile Task Weight"));
					}
					catch (Exception e){
					}
					
			   		int effortRemaining = 0;
			   		try {
			   			effortRemaining = Integer.parseInt(r.getAttributeValue("Agile Effort Remaining (hrs)"));
			   		}
			   		catch (Exception e){
			   		}
			   		
			   		
					String displayRDInReportDiv = "displayRDInReportDiv" + r.getRequirementId();
					String displayBacklogRequirementDiv = "displayBacklogRequirementDiv" + r.getRequirementId();
					
					
					j++;
					if (j == maxPageSize){
						%>
						<tr> 
							<td colspan='9'>
								<span class='headingText'>We are limiting the display to <%=maxPageSize %> requirements</span>
								
							</td>
						</tr>
						<%
						// we have reached the max displayable results. lets exit the while loop.
						break;
					}
					
					// for the first row, print the header and user defined columns etc..
					if (j == 1){
					%>
					<tr>
						<td class='tableHeader' width='90'>
							<span class='sectionHeadingText'>
							Action 
							</span>
						</td>
						<td class='tableHeader' width='350'>
							<span class='sectionHeadingText'>
							Requirement 
							</span>
						</td>
						<td class='tableHeader' width='150'>
							<span class='sectionHeadingText'>
							Owner
							</span>
						 </td>
						<td class='tableHeader' width='90'> 
							<span class='sectionHeadingText'>
							Task Weight
							</span>
						</td>
						<td class='tableHeader' width='90'> 
							<span class='sectionHeadingText'>
							Effort Remaining (hrs)
							</span>
						</td>
						
						
						<td class='tableHeader' width='90'> 
							<span class='sectionHeadingText'>
							Percent Complete
							</span>
						</td>
						<td class='tableHeader' width='90'> 
							<span class='sectionHeadingText'>
							Priority
							</span>
						</td>
						<td class='tableHeader' width='90'> 
							<span class='sectionHeadingText'>
							Approval Status 
							</span>
						</td>
						<td class='tableHeader' width='90'> 
							<span class='sectionHeadingText'>
							Testing Status 
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
					cellStyle = "normalTableCell";	
				}
				%>
				<tr>
					<td colspan='9'>
						<div id='<%=displayBacklogRequirementDiv %>'>
							<table>
								<tr>
									<td class='<%=cellStyle%>' width='90'>
										<span class='normalText'>
					 					<input type='button' class='btn btn-primary btn-sm' <%=buttonDisabledString%> name='addRequirementToSprint' id='addRequirementToSprint' value=' Add to Sprint '
					 					onclick='
					 					addRequirementBacklogToSprint(<%=project.getProjectId() %>,<%=folderId %>, <%=sprintId %>, <%=r.getRequirementId() %>,"<%=displayRDInReportDiv%>","<%=displayBacklogRequirementDiv %>")'>
					 					</span>
									</td>
							 		<td class='<%=cellStyle%>' width='350'>
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
												displayRequirement(<%=r.getRequirementId()%>,"Agile Scrum Workflow");
												document.getElementById("contentCenterF").style.display="none";
												'>
											<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %></a> 
											</span>
								 		</td>
								 		<td class='<%=cellStyle%>' width='150'>
								 			<span class='normalText'>
								 			<%=r.getRequirementOwner()%>
								 			</span>
								 		</td>
								 		
								 		<td class='<%=cellStyle%>' width='90'>
								 			<span class='normalText'>
								 			<%=taskWeight%>
								 			</span>
								 		</td>
								 		
								 		<td class='<%=cellStyle%>' width='90'>
								 			<span class='normalText'>
								 			<%=effortRemaining%>
								 			</span>
								 		</td>
								 		
								 		<td class='<%=cellStyle%>' width='90'>
								 			<span class='normalText'>
								 			<%=r.getRequirementPctComplete()%> %
								 			</span>
								 		</td>
								 		<td class='<%=cellStyle%>' width='90'>
								 			<span class='normalText'>
								 			<%=r.getRequirementPriority()%>
								 			</span>
								 		</td>
										<% if (r.getApprovalStatus().equals("Draft")){ %>
											<td bgcolor='#FFFF66'' width='90'>
												<span class='normalText'>
													<%=r.getApprovalStatus() %>
												</span>
											</td>										
										<%} %>
										<% if (r.getApprovalStatus().equals("In Approval WorkFlow")){ %>
											<td bgcolor='#99ccff' width='90'>
												<span class='normalText'>												
													<%=r.getApprovalStatus() %>
												</span>
											</td>
										<%} %>
										<% if (r.getApprovalStatus().equals("Approved")){ %>
											<td bgcolor='#CCFF99'' width='90'>
												<span class='normalText'>
													<%=r.getApprovalStatus()%>
												</span>
											</td>
										<%} %>
										<% if (r.getApprovalStatus().equals("Rejected")){ %>
											<td bgcolor='#FFA3AF' width='90'>
												<span class='normalText'>
													<%=r.getApprovalStatus()%>
												</span>
											</td>
										<%} %>
										
										
										
							
										<% if (r.getTestingStatus().equals("Pending")){ %>
											<td bgcolor='#FFFF66' width='90'>
												<span class='normalText'>
													<%=r.getTestingStatus() %>
												</span>
											</td>										
										<%} %>
										<% if (r.getTestingStatus().equals("Pass")){ %>
											<td bgcolor='#CCFF99' width='90'>
												<span class='normalText'>												
													<%=r.getTestingStatus() %>
												</span>
											</td>
										<%} %>
										<% if (r.getTestingStatus().equals("Fail")){ %>
											<td bgcolor='#FFA3AF' width='90'>
												<span class='normalText'>
													<%=r.getTestingStatus()%>
												</span>
											</td>
										<%} %>
									</tr>
									<tr>
										<td  class='<%=cellStyle%>'  colspan='9'>
											<div id = '<%=displayRDInReportDiv%>'> </div>
										</td>
									</tr>
								</table>
						</div>
						
					</td>
				</tr>
			

		<%	}
		}
		
		if (j == 0){
			%>
			<tr> 
				<td colspan='9'>
					<div class='alert alert-success'>
					<br></br>
					<span class='headingText'>There are no Backlog (Incomplete and Unassigned to a sprint) requirements in this folder.</span>
					<br></br>
					</div>
				</td>
			</tr>
			<%
		}
		%>
	
	</table>
	
	
<%}%>