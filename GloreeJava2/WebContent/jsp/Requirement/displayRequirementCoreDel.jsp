<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String displayRequirementCoreDelIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayRequirementCoreDelIsLoggedIn == null) || (displayRequirementCoreDelIsLoggedIn.equals(""))){
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

	
	
		Requirement requirement = (Requirement) request.getAttribute("requirement");
		
		// if the user does not have read permissions on the folder where this req resides, lets
		// redact it.
		if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
				+ requirement.getFolderId()))){
			requirement.redact();
		}

		///////////////////////////////SECURITY CODE ////////////////////////////
		// if the requirement worked on, doesn't belong to the project the user is 
		// currently logged into, then a user logged into project x is trying to 
		// hack into a req in project y by useing requirementId parameter.
		if (requirement.getProjectId() != project.getProjectId()) {
			return;
		}
		///////////////////////////////SECURITY CODE ////////////////////////////
	
		boolean deleteDisabled = false;
		if (!(securityProfile.getPrivileges().contains("deleteRequirementsInFolder" 
				+ requirement.getFolderId()))){
			deleteDisabled = true;
		}
		// since this is a purged requirement, it's not in a real folder.
		// it's virtualfodlerId is -1 : + the requiremene type id.
		// we need this to refresh content right when this is purged.
		String virtualFolderId = "-1:" + requirement.getRequirementTypeId();
	%>
	
	
	<div id='requirementInfoDiv' class='level1Box'>
		
			<fieldset id="requirementCore">
				<legend><b>Requirement</b></legend>
				<table   width="100%" align="center" >
					<tr>
						<td align='left' colspan='4'>
							<div id ='requirementActions' class='level2Box'>

		        			<% if (deleteDisabled){ %>
		        				<span class='normalText'> <font color='gray'> Restore </font></span>
		        			<%}
		        			else {%>		        			 		        			
			        			<a href='#'  
			        			onClick='restoreRequirement("<%=request.getParameter("requirementId")%>", "<%=requirement.getFolderId()%>")'>
			        			Restore</a>
							<%} %>		        			

		        			&nbsp;&nbsp;|&nbsp;&nbsp;
		        			<% if (deleteDisabled){ %>
		        				<span class='normalText'> <font color='gray'> Purge </font></span>
		        			<%}
		        			else {%>		        			 
		        				<a href='#' onClick='purgeRequirementForm("<%=request.getParameter("requirementId")%>",
		        			 	"<%=virtualFolderId%>")'> Purge </a>
							<%} %>		        			


		        			&nbsp;&nbsp;|&nbsp;&nbsp;
		        			<%
		        			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
		        			String url = ProjectUtil.getURL(request,requirementId,"requirement");
		        			%>
		        			<a href='#' onClick='displayURL("<%=url%>")'> URL </a>
							</div>        																        									
						</td>
					</tr>
					<tr>
						<td align='left' colspan='4'>
							<div id = 'requirementPromptDiv<%=requirement.getRequirementId() %>' class='level2Box' style="display:none;"></div>
						</td>
					</tr>							
					<tr>
						<td align='center' colspan='4'>
							<div id='deletedRequirementPrompt' class='alert alert-success'>
		        			Note : This is a deleted Requirement that you can restore to active status.						
							</div>        									
							        									
						</td>
					</tr>
			
					<!--  lets get the requirement details displayed -->
					<tr>
						<td colspan=4>
						<div id = 'requirementPromptDiv' class='level2Box'>
						</div>
						</td>
					</tr>
					<tr>
						<td align='left' colspan='4'>				
							<div id ='requirementCoreDiv' class='level2Box'>
		        				<table width='100%'>
									<tr> 
										<th width='80'> 
										<span class='headingText'>
											Unique Id
										</span></th>
										<td colspan='3'> 
										<span class='normalText'>
											<%=requirement.getRequirementFullTag() %>
										</span>
										</td>
									</tr>
									<tr>
										<th> 
										<span class='headingText'>
											Baselines 
										</span>
										</th>
										<td colspan='3'> 
										<span class='normalText'>
											<%=RequirementUtil.getRequirementBaselineString(requirement.getRequirementId(), databaseType) %>
										</span>
										</td>
									</tr>
									<tr>
										<th>
										<span class='headingText'>
											 Approvers 
										</span>
										</th>
										<td colspan='3'>
										<span class='normalText'>
											 <%=requirement.getApprovers() %>
										</span>
										</td>
									</tr>
									<tr>
										<th>
										<span class='headingText'>
											 Approval Dt 
										</span>
										</th>
										<td width='200'> 
										<span class='normalText'>
											<%=requirement.getApprovedByAllDt() %>
										</span>
										</td>
										<th align="right"> 
										<span class='headingText'>
											Folder
										</span>
										</th>
										<td> 
											<span class='normalText'>
											<%=new Folder(requirement.getFolderId()).getFolderPath() %>
											</span>
										</td>
									</tr>
									<tr>
										<th> 
										<span class='headingText'>
											Requirement Type 
										</span>
										</th>
										<td> 
											<span class='normalText'>
											<%=requirement.getRequirementTypeName()%>
											</span>
										</td>
										<th align="right"> 
										<span class='headingText'>
											Status
										</span>
										</th>
										<td> 
											<span class='normalText'>
											<%=requirement.getApprovalStatus() %>
											</span>
										</td>
									</tr>
									<tr>
										<th>
										<span class='headingText'>
											 Name
										</span>
										</th>
										<td> 
											<span class='normalText'>
											<%=requirement.getRequirementNameForHTML()%>
											</span>
										</td>
										<th align="right">
										<span class='headingText'>
											 Priority
										</span>
										</th>
										<td> 
											<span class='normalText'>
											<%=requirement.getRequirementPriority()%>
											</span>
										</td>
									</tr>
									<tr> 
										<th> 
										<span class='headingText'>
											Owner
										</span>
										</th>
										<td> <%=requirement.getRequirementOwner()%></td>
										<th align="right">
										<span class='headingText'>
											 Percent Complete 
										</span>
										</th>
										<td> 
											<span class='normalText'>
											<%=requirement.getRequirementPctComplete()%> %
											</span>
										<td>		
									</tr>
									<tr> 
										<th> 
										<span class='headingText'>
											External Url
										</span>
										</th>										
										<td colspan="3">
											<a href="http://<%=requirement.getRequirementExternalUrl()%>" target="_blank"> <%=requirement.getRequirementExternalUrl()%> </a>
										</td>		
									</tr>
									<tr> 
										<th>
										<span class='headingText'>
											 Description
										</span>
										</th>
										<td colspan="3">
										<span class='normalText'>
											<%=requirement.getRequirementDescription()%>
										</span>
										</td>			
									</tr>	
									<tr>
										<th>
										<span class='headingText'>
											Comments
										</span>
										<td colspan='3'>
											<div class='level1Box'>
											<table class='paddedTable>'>
											<% 
											ArrayList comments = RequirementUtil.getRequirementComments(requirementId, databaseType);
											if (comments.size() > 0 ){
											Iterator i = comments.iterator();
											int counter = 0;
											while ( i.hasNext() ) {
												Comment commentObject = (Comment) i.next();
												counter++;
												if (counter == 1 ){
													%>
													<tr>
												 		<td >
												 			<span class='normalText'">
															<b>Comment Dt</b>
															</span>
												 		</td>
												 		<td >
												 			<span class='normalText'">
												 			<b>Commenter</b>
												 			</span>
												 		</td>		
												 		<td >
												 			<span class='normalText'">
												 			<b>Req Version</b>
												 			</span>
												 		</td>
														<td >
												 			<span class='normalText'">
												 			<b>Comment</b>
												 			</span>
												 		</td>		
												 	</tr>
													
													<%
												}
												%>
													<tr>
												 		<td >
												 			<span class='normalText'">
												 			<%=commentObject.getCommentDate() %>
												 			</span>
												 		</td>
												 		<td >
												 			<span class='normalText'">
												 			<%=commentObject.getCommenterEmailId() %>
												 			</span>
												 		</td>		
												 		<td >
												 			<span class='normalText'">
												 			<%=commentObject.getVersion() %>
												 			</span>
												 		</td>
														<td >
												 			<span class='normalText'">
												 			<img src="/GloreeJava2/images/comment16.png" border="0">
												 			<%=commentObject.getHTMLFriendlyCommentNote() %>
												 			</span>
												 		</td>		
												 	</tr>
												<%} %>
											<%} %>			
											</table>
											</div>
										</td>
									</tr>									
								</table>
							</div>
						</td>
					</tr>
				</table>
			</fieldset>
		
	</div>
<%}%>