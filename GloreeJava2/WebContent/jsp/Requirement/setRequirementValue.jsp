<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

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
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User user = securityProfile.getUser();
	

	String serverName = request.getServerName();
	
	String mailHost = this.getServletContext().getInitParameter("mailHost");
	String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
	String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
	String smtpPort = this.getServletContext().getInitParameter("smtpPort");
	String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
	String emailUserId = this.getServletContext().getInitParameter("emailUserId");
	String emailPassword = this.getServletContext().getInitParameter("emailPassword");
	
	
	// NOTE : this page can be called when some one tries to edit a requirement.
	
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);
	String targetAttribute = request.getParameter("targetAttribute");
	String targetValue = request.getParameter("targetValue");

	if (!(securityProfile.getRoles().contains("MemberInProject" + requirement.getProjectId()))){
		//User is NOT a member of this project. so do nothing and return.
		return;
	}	

	if (targetAttribute.equals("submitForApproval")){
		
		RequirementUtil.submitRequirementForApproval(requirementId,user.getEmailId(),  databaseType, serverName);
		RequirementUtil.remindPendingApproversImmediately(requirementId,  serverName, request,
				mailHost,  transportProtocol,  smtpAuth,  smtpPort,  smtpSocketFactoryPort,  emailUserId,  emailPassword);
	}	
	
	if (targetAttribute.equals("approve")){
		String approvalNote = request.getParameter("approvalNoteValue");
		RequirementUtil.approvalWorkFlowAction(requirementId, "approve", approvalNote, user, request, databaseType);
		RequirementUtil.remindPendingApproversImmediately(requirementId,  serverName, request,
				mailHost,  transportProtocol,  smtpAuth,  smtpPort,  smtpSocketFactoryPort,  emailUserId,  emailPassword);
	}	
	
	if (targetAttribute.equals("reject")){
		String approvalNote = request.getParameter("approvalNoteValue");
		RequirementUtil.approvalWorkFlowAction(requirementId, "reject", approvalNote, user, request, databaseType);
		
		//RequirementUtil.remindPendingApproversImmediately(requirementId,  serverName, request,
		//	mailHost,  transportProtocol,  smtpAuth,  smtpPort,  smtpSocketFactoryPort,  emailUserId,  emailPassword);
	}	
	

	if (targetAttribute.equals("cancelMyRejection")){
		String approvalNote = request.getParameter("approvalNoteValue");
		RequirementUtil.approvalWorkFlowAction(requirementId, targetAttribute, approvalNote, user, request, databaseType);
		RequirementUtil.remindPendingApproversImmediately(requirementId,  serverName, request,
				mailHost,  transportProtocol,  smtpAuth,  smtpPort,  smtpSocketFactoryPort,  emailUserId,  emailPassword);
	}	
	
	if (
			(targetAttribute.equals("requestApprovalFromRejector"))
			||
			(targetAttribute.equals("bypassRejector"))
			||
			(targetAttribute.equals("bypassAllApprovers"))
			||
			(targetAttribute.contains("bypassAnApprover"))
			)
			{
		// we proceed only if the user is an admin or an owner
		boolean isUserAnAdmin = false; 
		if (securityProfile.getRoles().contains("AdministratorInProject" + requirement.getProjectId())){
			isUserAnAdmin = true;
		}
		if (isUserAnAdmin){
			
			String approvalNote = request.getParameter("approvalNoteValue");
			RequirementUtil.approvalWorkFlowAction(requirementId, targetAttribute, approvalNote, user, request, databaseType);
			RequirementUtil.remindPendingApproversImmediately(requirementId,  serverName, request,
					mailHost,  transportProtocol,  smtpAuth,  smtpPort,  smtpSocketFactoryPort,  emailUserId,  emailPassword);
		}
	}
	
	///////////////////////////////SECURITY CODE ////////////////////////////
	// if the user doesn't hae update permissions on this folder, then return.
	if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
		+ requirement.getFolderId()))){
		return;
	}
	
	// SRT TO DO the next four lines of code looks stupid. If the above 4 lines executed, shouldn't updateDisabled be always false???? Dumb fuck !!!.
	boolean updateDisabled = false ;
	if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
			+ requirement.getFolderId()))){
	
		updateDisabled = true;
	}
	
	// even if a user has update permissions on a folder,  we still check to see
	//  if this requirement is locked by some other user, then we need to set update , delete and purge to Disabled.
	if (
		(!(requirement.getRequirementLockedBy().equals(""))
		&&
		(!(requirement.getRequirementLockedBy().equals(user.getEmailId()))))
	){
		// this req is locked and its locked by someone other that the person currently logged in. hence disabled button is off.
		updateDisabled = true;
	}

	///////////////////////////////SECURITY CODE ////////////////////////////
	

	
	if (targetAttribute.equals("percentComplete")){
		int percentComplete = 0;
		try {
			percentComplete = Integer.parseInt(targetValue);
		}
		catch (Exception e){
			// do nothing
			percentComplete = 0;
		}
		requirement.setPercentComplete(percentComplete, user, databaseType);
		requirement = new Requirement(requirement.getRequirementId(),databaseType);
		%>
			<span class='subSectionHeadingText'>
			&nbsp;&nbsp;&nbsp;
			<% if (updateDisabled) {
				%>
				<%=requirement.getRequirementPctComplete()%>
				<%
			}
			else {
			%>
				<input type='text' name='requirementPercentComplete' id='requirementPercentComplete' style='width:20px' size='3'  
					value='<%=requirement.getRequirementPctComplete()%>'
					onChange='
						var percentCompleteObject = document.getElementById("requirementPercentComplete")
						 if(
						 	( percentCompleteObject.value == null) || 
						 	(isNaN(percentCompleteObject.value)) ||
						 	(percentCompleteObject.value < 0) ||
						 	(percentCompleteObject.value > 100 )
						 	){
							alert ("Please enter a valid Number between 0 and 100 for percent complete");
							percentCompleteObject.focus();
							percentCompleteObject.style.backgroundColor="#FFCC99";
							return;
						}
						setRequirementPercentComplete(<%=requirement.getRequirementId() %>)
					'></input>
				<%} %>
				% Completed	
			</span>
		<%
		
	}

	
	


	
	if (targetAttribute.equals("priority")){
		String priority = targetValue;
		String newRequirementName = requirement.getRequirementNameForHTML();
		String newRequirementDescription = requirement.getRequirementDescription();
		String newRequirementPriority = targetValue;
		String newRequirementOwner = requirement.getRequirementOwner();
		int newRequirementPctComplete = requirement.getRequirementPctComplete();
		String newRequirementExternalURL = requirement.getRequirementExternalUrl();

		// Note , to update the requirement, we just need to re
		// create the
		// requirement bean, , but this time giving the
		// requirement id , along
		// with old values for unchanged stuff, and the new
		// value for the stuff
		// you want to change.
		requirement = new Requirement(requirementId,newRequirementName, newRequirementDescription,
				newRequirementPriority, newRequirementOwner,newRequirementPctComplete,
				newRequirementExternalURL, user.getEmailId(),request,  databaseType);
		%>
			<% if (updateDisabled) {
				%>
					<%=requirement.getRequirementPriority()%>
				<%
			}
			else {
				String requirementPriority = "";
				if (requirement.getRequirementPriority().equals("High")){
					requirementPriority = "<option value='High' SELECTED>High </option><option value='Medium'>Medium</option><option value='Low'>Low</option>";
				}
				else if (requirement.getRequirementPriority().equals("Medium")){
					requirementPriority = "<option value='High'>High </option><option value='Medium' SELECTED>Medium</option><option value='Low'>Low</option>";
				}
				else {
					requirementPriority = "<option value='High'>High </option><option value='Medium'>Medium</option><option value='Low' SELECTED>Low</option>";
				}
			%>
				<div id='requirementPriorityStringDiv'
					onMouseOver='
						document.getElementById("requirementPriorityStringDiv").style.display="none";
						document.getElementById("requirementPriorityDropDownDiv").style.display="block";
					' 
					>
					<span class='normalText' >
						<%=requirement.getRequirementPriority()%>
					</span>
				</div>
				<div id='requirementPriorityDropDownDiv' style='display:none'>
					<span class='normalText'> 
						<select name="requirementPriority" id="requirementPriority" 
							onChange='
								setRequirementPriority(<%=requirement.getRequirementId() %>)
							'>
							<%=requirementPriority %>
						</select>
					</span>
				</div>											
			<%} %>

		<%
		
	}	
	
	
	
	
	
	
	if (targetAttribute.equals("requirementName")){

	
		String newRequirementName = targetValue;
		Requirement newRequirement = null;
		if ((targetValue != null) && (!(targetValue.equals(requirement.getRequirementName() )))){
			
			newRequirement = new Requirement( requirementId, newRequirementName, 
					requirement.getRequirementDescription(),
					requirement.getRequirementPriority(), 
					requirement.getRequirementOwner(), 
					requirement.getRequirementPctComplete(), 
					requirement.getRequirementExternalUrl(), user.getEmailId() , request, databaseType);
				
		}
		
		if (newRequirement == null){
			newRequirement = requirement;
		}
		%>
			<% if (updateDisabled) {	%>
				<span class='normalText' >
					<%=newRequirement.getRequirementNameForHTML()%>
				</span>
			<%
			}
			else { %>
				<div id='requirementNameStringDiv'>
					<span class='normalText' >
						<%=newRequirement.getRequirementNameForHTML()%>
					</span>
				</div>
				<div id='requirementNameTextBoxDiv' style='display:none'>
					<table>
						<tr>
							<td style="vertical-align:middle">
								<span class='normalText'> 
									<textarea id="requirementName" name="requirementName"  rows='4' cols='100'><%=newRequirement.getRequirementNameForHTML()%></textarea>
								</span>							
							</td>
							<td style="vertical-align:middle">
								<span class='normalText'> 
									<input type='button' name='Update Name' value='Update Name' 
									onClick='setRequirementName(<%=newRequirement.getRequirementId() %>, <%=newRequirement.getFolderId() %>);'></input>
									
									<input type='button' name='Cancel' value='Cancel' onClick='
										document.getElementById("requirementNameStringDiv").style.display="block";
										document.getElementById("requirementNameTextBoxDiv").style.display="none";
									'> 
								</span>							
							</td>
						</tr>
					</table>

				</div>											
													
			<%} %>

<%
	}	

	if (targetAttribute.equals("requirementNameInList")){

		
		String newRequirementName = targetValue;
		Requirement newRequirement = null;
		if ((targetValue != null) && (!(targetValue.equals(requirement.getRequirementName() )))){
			
			newRequirement = new Requirement( requirementId, newRequirementName, 
					requirement.getRequirementDescription(),
					requirement.getRequirementPriority(), 
					requirement.getRequirementOwner(), 
					requirement.getRequirementPctComplete(), 
					requirement.getRequirementExternalUrl(), user.getEmailId() , request, databaseType);
				
		}
		
		if (newRequirement == null){
			newRequirement = requirement;
		}
		%>
			<% if (updateDisabled) {	%>
				<div class='alert alert-danger'>You do not have update permissions on this folder</div>
				<span class='normalText' >
					<%=newRequirement.getRequirementNameForHTML()%>
				</span>
			<%
			}
			else { %>
				<span class='normalText' >
					<%=newRequirement.getRequirementNameForHTML()%>
				</span>						
													
			<%} %>

<%
	}		
	if (targetAttribute.equals("requirementDescription")){

	
		String newRequirementDescription = targetValue;
		Requirement newRequirement = null;
		if ((targetValue != null) && (!(targetValue.equals(requirement.getRequirementDescription() )))){
			
			newRequirement = new Requirement( requirementId, requirement.getRequirementName(),
					newRequirementDescription, 
					requirement.getRequirementPriority(), 
					requirement.getRequirementOwner(), 
					requirement.getRequirementPctComplete(), 
					requirement.getRequirementExternalUrl(), user.getEmailId() , request, databaseType);
				
		}
		
		if (newRequirement == null){
			newRequirement = requirement;
		}
		%>
			<% if (updateDisabled) {	%>
				<span class='normalText' >
					<%=requirement.getRequirementDescription()%>
				</span>
			<%
			}
			else { %>
				<div id='requirementDescriptionStringDiv'>
					<span class='normalText' >
						<%=requirement.getRequirementDescription()%>
					</span>
				</div>
				<div id='requirementDescriptionTextBoxDiv' style='display:none'>
					<table>
						<tr>
							<td style="vertical-align:middle">
								<span class='normalText'> 
									<textarea id="requirementDescription" name="requirementDescription"  rows='10' cols='100'><%=requirement.getRequirementDescriptionBRToNewLine()%></textarea>
								</span>							
							</td>
							<td style="vertical-align:middle">
								<span class='normalText'> 
									<input type='button' name='Update Description' value='Update Description' 
									onClick='setRequirementDescription(<%=requirement.getRequirementId() %>, <%=requirement.getFolderId() %>);'></input>
									
									<input type='button' name='Cancel' value='Cancel' onClick='
										document.getElementById("requirementDescriptionStringDiv").style.display="block";
										document.getElementById("requirementDescriptionTextBoxDiv").style.display="none";
									'> 
								</span>							
							</td>
						</tr>
					</table>

				</div>											
													
			<%} %>


<%
	}	
	

	if (targetAttribute.equals("requirementNameAndDescription")){

		String newRequirementName = request.getParameter("requirementName");
		String newRequirementDescription = targetValue;
		
		if (newRequirementName == null) {
			newRequirementName = "";
		}
		
		if (newRequirementDescription == null) {
			newRequirementDescription = "";
		}
		
		System.out.println("srt reqwuirementDescription in setReqNameAndDesc is " + newRequirementDescription);
		
		
		Requirement newRequirement = null;
		if 	(
						(!(newRequirementDescription.equals(requirement.getRequirementDescription() )))
						||
						(!(newRequirementName.equals(requirement.getRequirementName() )))
						
			)
			{
			
			newRequirement = new Requirement( requirementId, newRequirementName,
					newRequirementDescription, 
					requirement.getRequirementPriority(), 
					requirement.getRequirementOwner(), 
					requirement.getRequirementPctComplete(), 
					requirement.getRequirementExternalUrl(), user.getEmailId() , request, databaseType);
				
			System.out.println("req is updated " + newRequirement.getRequirementFullTag());
			
		}
		
		if (newRequirement == null){
			newRequirement = requirement;
		}
		%>
			<% if (updateDisabled) {	%>
				<span class='normalText' >
					<%=requirement.getRequirementDescription()%>
				</span>
			<%
			}
			else { %>
				<div id='requirementDescriptionStringDiv'>
					<span class='normalText' >
						<%=requirement.getRequirementDescription()%>
					</span>
				</div>
				<div id='requirementDescriptionTextBoxDiv' style='display:none'>
					<table>
						<tr>
							<td style="vertical-align:middle">
								<span class='normalText'> 
									<textarea id="requirementDescription" name="requirementDescription"  rows='10' cols='100'><%=requirement.getRequirementDescriptionBRToNewLine()%></textarea>
								</span>							
							</td>
							<td style="vertical-align:middle">
								<span class='normalText'> 
									<input type='button' name='Update Description' value='Update Description' 
									onClick='setRequirementDescription(<%=requirement.getRequirementId() %>, <%=requirement.getFolderId() %>);'></input>
									
									<input type='button' name='Cancel' value='Cancel' onClick='
										document.getElementById("requirementDescriptionStringDiv").style.display="block";
										document.getElementById("requirementDescriptionTextBoxDiv").style.display="none";
									'> 
								</span>							
							</td>
						</tr>
					</table>

				</div>											
													
			<%} %>


<%
	}	
	

	if (targetAttribute.equals("owner")){

		

		
		String newRequirementOwner = targetValue;
		requirement.setOwner(request, newRequirementOwner, user, databaseType, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
		requirement = new Requirement(requirementId, databaseType);
		
		%>
			<% if (updateDisabled) {
				%>
					<%=requirement.getRequirementOwner()%>
				<%
			}
			else {
			%>
			
				<div id='requirementOwnerStringDiv'>
					<span class='normalText' style='cursor:pointer'
					<% if (!(updateDisabled)){%>
							onClick='
								if (document.getElementById("requirementOwnerDropDownDiv").style.display == "none"){
									document.getElementById("requirementOwnerStringDiv").style.display="none";
									displayRequirementOwners(<%=requirement.getRequirementId() %>)
								};
							'
						<%} %>
						 >
						<%=requirement.getRequirementOwner()%>
					</span>
				</div>
				<div id='requirementOwnerDropDownDiv' style='display:none'>
					
				</div>	
			<%} %>
	<%		
	}	
	%>
	
	