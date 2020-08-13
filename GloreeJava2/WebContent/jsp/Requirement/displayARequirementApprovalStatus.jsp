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
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User user = securityProfile.getUser();
	
	String uniqueIdentifier = request.getParameter("uniqueIdentifier");
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	} 
	
	if (isMember){

		String requirementApprovalStatusDiv = request.getParameter("requirementApprovalStatusDiv");
		int requirementId = Integer.parseInt(request.getParameter("requirementId"));
		
		Requirement r = new Requirement(requirementId, databaseType);
		
		boolean updateDisabled = false ;
		if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
				+ r.getFolderId()))){
		
			updateDisabled = true;
		}
		
		Folder f = new Folder (r.getFolderId());
		
		
		boolean folderEnabledForApproval = false;
		if (f.getIsFolderEnabledForApproval() == 1){
			folderEnabledForApproval = true;
		}
		
		
		%>
		
		
							<table><tr>
								
								
								<%	
										if (folderEnabledForApproval) {
										if (r.getApprovalStatus().equals("Draft")){ 
										
											if (!updateDisabled){ 
					        				%>
					        					<td style='width:180px; background-color:#FFFF66' align='left'>
													<input type='button'
												 style='height:25px; width:180px'  name='Submit Draft For Approval' value=' Submit Draft For Approval '
													onClick='
														handleRequirementActionOtherInCIA("<%=uniqueIdentifier %>",<%=r.getRequirementId()%>,<%=r.getFolderId()%>,"submitForApproval");
													'>
												</td> 	
											<%
											}
											else {
												%>
												<td style='width:180px; background-color:#FFFF66' align='left'>
													<span class='normalText' >
												&nbsp;&nbsp;Draft&nbsp;&nbsp;
												</span>
												</td>
												<%		
											}
										}
										
										
									
										if (r.getApprovalStatus().equals("In Approval WorkFlow")){ 
											r.setDaysSinceSubmittedForApproval(databaseType);
											int daysPending  = r.getDaysSinceSubmittedForApproval();
											
											if (r.getApprovers().contains("(P)" + user.getEmailId())){
												// if the user hasn't acted on this requirement, show the 'Pending by You' button
												%>
													<td style='width:180px; background-color:#99ccff' align='left'>
														<span class='normalText' >												
														<span class='normalText'>
															<input type='button' 
															 style='height:25px; width:180px'  
															 value='Accept / Reject (<%=daysPending %> days old)' 
															onClick='document.getElementById("approveRejectDiv<%=uniqueIdentifier%>").style.display="block"'>
														</span>
														 
													</span>
													</td>
												<%
											}
											else if (r.getApprovers().contains("(R)" + user.getEmailId())){
												// if the user has rejected the req, show the 'Approve' button.
												// this might help if the user has additional info and can now approve it.
												%>
													
													<td style='width:180px; background-color:#99ccff' align='left'>
														<span class='normalText' >												
														<span class='normalText'>
															<input type='button' 
															 style='height:25px; width:180px'  
															 value='Rejected by you' 
															onClick='document.getElementById("approveRejectDiv<%=uniqueIdentifier%>").style.display="block"'>
														</span>
														 
													</span>
													</td>
												<%
											}
											else if (r.getApprovers().contains("(R)" + user.getEmailId())){
												%>
												<td style='width:180px; background-color:#99ccff' align='left'>
													<span class='normalText' >												
													&nbsp;&nbsp;Rejected By Me&nbsp;&nbsp; 
												</span>
												</td>
												<%
											}
											
											else {
											%>
												<td style='width:180px; background-color:#99ccff' align='left'>
													<span class='normalText'>												
													&nbsp;&nbsp;Pending By Others for <%=daysPending %> days &nbsp;&nbsp; 
												</span>
												</td>
												<%
											}
										
										} 
										if (r.getApprovalStatus().equals("Approved")){ %>
											<td style='width:180px; background-color:#CCFF99' align='left'>
												<span class='normalText' >
												&nbsp;&nbsp;Accepted By All&nbsp;&nbsp;
											</span>
											</td>
										<%} 
										if(r.getApprovalStatus().equals("Rejected")){ %>
											<td style='width:180px; background-color:#FFA3AF' align='left'>
												<span class='normalText'>
												&nbsp;&nbsp;Rejected By All&nbsp;&nbsp;
											</span>
											</td>
										<%} 
									}
									else {
										%>
										<td></td>
										<%
									}
								%>
							</tr>
							</table>		
	




		<%
	} %>		