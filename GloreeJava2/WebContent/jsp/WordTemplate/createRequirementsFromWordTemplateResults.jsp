<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String editWordTemplateFormIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((editWordTemplateFormIsLoggedIn == null) || (editWordTemplateFormIsLoggedIn.equals(""))){
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
		// NOTE : this page can be called when some one tries to edit a wordTemplate.
		
		int templateId = Integer.parseInt(request.getParameter("templateId"));
		WordTemplate wordTemplate = new WordTemplate(templateId, databaseType);
		int folderId = Integer.parseInt(request.getParameter("folderId"));
		String locateProcess = request.getParameter("locateProcess");
		
		ArrayList createdRequirements = (ArrayList) request.getAttribute("createdRequirements");
		ArrayList updatedRequirements = (ArrayList) request.getAttribute("updatedRequirements");
		
		// lets remove the attributes from the request system.
		request.removeAttribute("createdRequirements");
		request.removeAttribute("updatedRequirements");
		
		ArrayList alertRows = (ArrayList) request.getAttribute("alertRows");
			
	%>
	
	<div id='editWordTemplateDiv' >
		<form method="post" id="createRequirementsFromWordTemplateMapForm" action="">
		<table class='paddedTable' width='100%'>
			<tr>
				<td  align="left" colspan='6'>
				<span class='normaltext'>				
				Create Requirements From Word Document
				</span>
				</td> 
			</tr>	
			<%	
			if ((alertRows != null) && (alertRows.size() > 0)){
			%>
			<tr>
				<td  align="left" colspan='6'>
					<table>
						<tr>
							<td> 
								<span class='sectionHeadingText'>
									Requirement Id
								</span>
							</td>
							<td> 
								<span class='sectionHeadingText'>
									Requirement Name
								</span>
							</td>					
							<td> 
								<span class='sectionHeadingText'>
									Alert Message
								</span>
							</td>
						</tr>
						<%
						Iterator a = alertRows.iterator();
						while (a.hasNext()){
							String alertRow = (String) a.next();
							String [] alert = alertRow.split(":##:");
							String requirementFullTag = "";
							String requirementName= "";
							String status = "";
							if (alert.length > 0){
								requirementFullTag = alert[0];
							}
							if (alert.length > 1){
								requirementFullTag = alert[1];
							}
							if (alert.length > 2){
								status = alert[2];
							}
							%>
							<tr>
								<td> 
									<span class='sectionHeadingText'>
										<%=requirementFullTag%>
									</span>
								</td>
								<td> 
									<span class='sectionHeadingText'>
										<%=requirementName%>
									</span>
								</td>					
								<td> 
									<span class='sectionHeadingText'>
										<%=status%>
									</span>
								</td>
							</tr>							
							
						<% } %>
						
						
					</table>
				</td> 
			</tr>	
			
			<%
			}
			if ((createdRequirements == null) || (createdRequirements.size() == 0 )){
			%>
			<tr>
				<td align="left" colspan='6'>				
				<div class='userAlertPrompt'>
					<span class='normalText'>
						We were not able to create any Requirements from this word document. 
					</span>
				</div>
				</td> 
			</tr>				
			<%		
			}
			else {
			%>
			<tr>
				<td align="left" colspan='6'>				
				<div class='alert alert-success'>
					<span class='normalText'>
						<b>The following Requirements were created in the TraceCloud system. </b>
					</span>
				</div>
				</td> 
			</tr>	
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
					Status 
					</span>
				</td>
				<td class='tableHeader'> 
					<span class='sectionHeadingText'>
					Folder
					</span>
				</td>
			</tr>	
			
			<%	
				Iterator i = createdRequirements.iterator();
		    	int j = 1;
		    	String cellStyle = "normalTableCell";			
				while (i.hasNext()){
					Requirement r = (Requirement) i.next();
		    		if ((j%2) == 0){
		    			cellStyle = "normalTableCell";
		    		}
		    		else {
		    			cellStyle = "altTableCell";	
		    		}
		    		j++;

					%>
	 				<tr>
				 		<td class='<%=cellStyle%>'>
				 			<span>
								<a href="#" 
								onClick='
									displayFolderInExplorer("<%=folderId %>");
									displayFolderContentCenterA("<%=folderId %>");
									displayFolderContentRight("<%=folderId %>");
									displayRequirement(<%=r.getRequirementId()%>);'>
								<img src="/GloreeJava2/images/puzzle16.gif" border="0">
								&nbsp;<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %></a> 
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
								<td bgcolor='lightyellow'>
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
				 		<td class='<%=cellStyle%>'>
				 			<span class='normalText'>
				 			<%=r.getFolderPath()%>
				 			</span>
				 		</td>
	 				</tr>						
					<%
				}
			%>	
				
			<%	
			}
			
			
			if ((updatedRequirements != null) && (updatedRequirements.size() > 0 )){
			%>
					<tr>
						<td align="left" colspan='6'>				
						</td>
					</tr>
					<tr>
						<td align="left" colspan='6'>				
						<div class='alert alert-success'>
							<span class='normalText'>
								<b>The following Requirements were updated in the TraceCloud system. </b>
							</span>
						</div>
						</td> 
					</tr>	
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
							Status 
							</span>
						</td>
						<td class='tableHeader'> 
							<span class='sectionHeadingText'>
							Folder
							</span>
						</td>
					</tr>	
			<%	
				Iterator i = updatedRequirements.iterator();
		    	int j = 1;
		    	String cellStyle = "normalTableCell";			
				while (i.hasNext()){
					Requirement r = (Requirement) i.next();
		    		if ((j%2) == 0){
		    			cellStyle = "normalTableCell";
		    		}
		    		else {
		    			cellStyle = "altTableCell";	
		    		}
		    		j++;

					%>
	 				<tr>
				 		<td class='<%=cellStyle%>'>
				 			<span>
								<a href="#" 
								onClick='
									displayFolderInExplorer("<%=folderId %>");
									displayFolderContentCenterA("<%=folderId %>");
									displayFolderContentRight("<%=folderId %>");
									displayRequirement(<%=r.getRequirementId()%>);'>
								<img src="/GloreeJava2/images/puzzle16.gif" border="0">
								&nbsp;<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %></a> 
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
								<td bgcolor='lightyellow'>
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
				 		<td class='<%=cellStyle%>'>
				 			<span class='normalText'>
				 			<%=r.getFolderPath()%>
				 			</span>
				 		</td>
	 				</tr>						
					<%
				}
			%>				
			<%} %>
		</table>
		
		</form>
	</div>
<%}%>