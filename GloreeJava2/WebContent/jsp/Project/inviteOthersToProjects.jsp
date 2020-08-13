<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->    
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="java.sql.Date" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>
<%@ page import="java.io.PrintWriter;" %>

<!-- Get the list of my projects by calling the util. -->

<%
	//authorization
	// since we need authorization as well as authenticaiton we will use the 
	// security profile object.
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	if (securityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%
	}
	User user = securityProfile.getUser();
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	
	String action = request.getParameter("action");
	int projectId = Integer.parseInt(request.getParameter("projectId"));
	Project project = new Project(projectId, databaseType);
	if (securityProfile.getRoles().contains("MemberInProject" + projectId)){
		
%>

<%
		if (action.equals("displayOptions")){
			
			int cancelInviteId = 0;
			try {
				cancelInviteId = Integer.parseInt(request.getParameter("cancelInviteId"));
			}
			catch (Exception e){
				e.printStackTrace();
			}
			if (cancelInviteId >  0 ){
				RoleUtil.cancelInvitation(cancelInviteId);
			}
			
			ArrayList roles = RoleUtil.getRoles(project.getProjectId()); 
			Iterator r = roles.iterator();
			
			%>
			<div class= 'userSuccessAlert'>
				<table width='100%' >
					<tr>
						<td >
							<span class='normalText'>
								Invitee Email Address 
							</span>
						</td>
						<td >
							<span class='normalText'>
								<input type='text' name='inviteeEmailIds<%=projectId%>' id='inviteeEmailIds<%=projectId%>' size='50' value='Enter comma separated emaild ids' onClick="this.value=''"></input>
							</span>
						</td>
						<td >
							<span class='normalText'>
							Role
							</span>
						</td>
						<td >
							<span class='normalText'>
								<select name='roles<%=projectId%>' id='roles<%=projectId%>'>
									<% 
									while (r.hasNext()){
										Role role = (Role) r.next();
										%>
										<option value='<%=role.getRoleId()%>'><%=role.getRoleName() %></option>
										<%
									}
									%>
								</select>
							</span>
						</td>
						<td >
							<span class='normalText'>
								<input type='button'  class="btn btn-primary btn-sm" name='inviteOthersButton' id='inviteOtheresButton' value='Invite Now' onClick='inviteOthersToProjectNow(<%=projectId%>)'></input>
								
								&nbsp;&nbsp;&nbsp;
								
								<input type='button'  class="btn btn-danger btn-sm" name='cancel' id='cancel' value='Close' onClick="document.getElementById('inviteOthersDiv<%=project.getProjectId() %>').style.display='none';"></input>
								
							</span>
						
						</td>
					</tr>
					
					<% 
					
						ArrayList invitedList = ProjectUtil.getInvitedUsers(projectId);
						Iterator iL = invitedList.iterator();
					
					%>
					<tr>
						<td colspan='5'>
							<table class='table' width='100%'>
							
								<tr>
									<td bgcolor="#99CCFF">
										<span class='sectionHeadingText'>Invitee</span>
									</td>
									<td bgcolor="#99CCFF">
										<span class='sectionHeadingText'>Role Name </span>
									</td>
									<td bgcolor="#99CCFF">
										<span class='sectionHeadingText'>Invited By</span>
									</td>
									
									<td bgcolor="#99CCFF">
										<span class='sectionHeadingText'>Invited On</span>
									</td>
									<td bgcolor="#99CCFF">
										<span class='sectionHeadingText'>Last Email Sent On</span>
									</td>
									<td>
									&nbsp;
									</td>
								</tr>
									
								<%
								while (iL.hasNext()){
									String iLString = (String) iL.next();
									String[] iLArray = iLString.split("##");
									
									String invitee = iLArray[0];
									String invitor = iLArray[1];
									String inviteDt = iLArray[2];
									String lastEmailDt = iLArray[3];
									String roleName = iLArray[4];
									String inviteId = iLArray[5];
									
									%>
									<tr>
										<td>
											<span class='normalText'><%=invitee %></span>
										</td>
										<td>
											<span class='normalText'><%=roleName%></span>
										</td>
										
										<td>
											<span class='normalText'><%=invitor %></span>
										</td>
										<td>
											<span class='normalText'><%=inviteDt%></span>
										</td>
										<td>
											<span class='normalText'><%=lastEmailDt%></span>
										</td>
										<td>
											<span class='normalText'>
												<input type='button' class='btn btn-sm btn-danger' name='cancelInvite<%=inviteId%>' value='Cancel Invitation' 
												onclick='inviteOthersToProject(<%=projectId %>, "<%=inviteId %>")'
												></input>
											</span>
										</td>
									</tr>
									<%
								}
								
								%>
							
							</table>
						</td>
					</tr>
					
					
					<tr>
						<td colspan='5'>
							<table class='table' width='100%'>
								<tr>
									<td bgcolor="#99CCFF" style='width:200px'>
										<span class='sectionHeadingText'>Role Name </span>
									</td>
									<td bgcolor="#99CCFF">
										<span class='sectionHeadingText'>Existing Members</span>
									</td>
							
								</tr>
								<%
								Iterator rIP = roles.iterator();
				
								while (rIP.hasNext()){
									Role role = (Role) rIP.next();
								%>
									<tr>
										<td style='width:200px'>
											<span class='sectionHeadingText'><%=role.getRoleName() %></span>
										</td>
										<td >
											<span class='sectionHeadingText'></span>
										</td>
								
									</tr>
									
								<%
									ArrayList usersInRole = RoleUtil.getAllUsersInRole(role.getRoleId(), databaseType);
									Iterator users = usersInRole.iterator();
									while (users.hasNext()) {
										User currentUser = (User) users.next();	
									
								%>
									<tr>
										<td style='width:200px'>
											<span class='normalText'></span>
										</td>
										<td >
											<span class='normalText'><%=currentUser.getEmailId() %></span>
										</td>
								
									</tr>
								
								<%
									}
								} %>
							</table>	
						</td>
					</tr>
				</table>
			</div>
		
	<%} %>













<%
		if (action.equals("inviteOthersToProjectNow")){
			
			
			String roles = request.getParameter("roles");
			String emailIdString = request.getParameter("emailIds");
			if (securityProfile.getRoles().contains("AdministratorInProject" + projectId)){
				// since the person performing this action is an admin of this project, 
				// lets grant the access and get the message out.
				// roles are comma separated roleIds and may have extra comma at the end. 
				

				String inCorrectDomainEmailIds = "";
				String successfullyAddedEmailIds = "";
				String invitedEmailIds = "";
				
				String [] rolesArray = roles.split(",");
				for(int i = 0;i< rolesArray.length; i++){
					try {
					if (rolesArray[i] != null ){
						int roleId = Integer.parseInt(rolesArray[i]);
						// call addUsersToRole, which returns 3 # delimited strings incorrectDomain, 
						// addedUsers, invitedUsers . Each one of these strings is a , delimited
						// list of email Ids.
						// 
						String serverName = request.getServerName();

						String result = RoleUtil.addUsersToRole(project, roleId, emailIdString, user.getEmailId(), databaseType, serverName);
						if (result.contains("#")){
							String [] resultArray = result.split("#");
							
							
							// NOTE : for some reason, a split of #mkt@gmail.com# gives only 2 rows in teh array.
							// hence forcing a check.
							if (resultArray.length > 0) {
								// i.e we are sure 1st element is there . i.e item[0]
								inCorrectDomainEmailIds +=  resultArray[0];
							}
							if (resultArray.length > 1) {
								// i.e we are sure 2nd element is there . i.e item[1]
								successfullyAddedEmailIds += resultArray[1];
							}
							if (resultArray.length > 2) {
								// i.e we are sure 3rd element is there . i.e item[2]
								invitedEmailIds += resultArray[2];	
							}
						}

					}
					}
					catch (Exception e){
						e.printStackTrace();
					}
				}
				
				
				
				// lets display the incorrectEmailId error message.
				String statusMessage = "";
				if ((inCorrectDomainEmailIds != null) && (!(inCorrectDomainEmailIds.equals("")))) {
					statusMessage = "<tr> " +
						" <td colspan='2'> " +
						" <div  class='alert alert-success'> " +
						" <span class='normalText'> " + 
						" The following email Id do not fit the Restricted Domain '" +
						 project.getRestrictedDomains() + "' .<br> Please try a different " +
						" email id or work with the Administrator of this Project to add additional domains. <br>" ;
						
					String [] emailIds = {inCorrectDomainEmailIds};
					if (inCorrectDomainEmailIds.contains(",")){
						emailIds = inCorrectDomainEmailIds.split(",");
					}
					for (int i=0; i<emailIds.length; i++){
						statusMessage += "<br>" + emailIds[i];  
					}
					statusMessage += " </span> </div> " + 
						" </td> " + 
						" </tr>";
				}

				

				// lets display the invited message.
				
				if ((invitedEmailIds != null) && (!(invitedEmailIds.equals("")))) {
					statusMessage += "<tr> " +
						" <td colspan='2'> " +
						" <div  class='alert alert-success'> " +
						" <span class='normalText'> " + 
						" The user you added is currently not in the tracecloud system." +
						  "<br> We have sent them an email and when they open an account  " +
						" they will automatically have access to this project.<br>" ;
						
					String [] emailIds = {invitedEmailIds};
					if (invitedEmailIds.contains(",")){
						emailIds = invitedEmailIds.split(",");
					}
					for (int i=0; i<emailIds.length; i++){
						statusMessage += "<br>" + emailIds[i];  
					}
					statusMessage += " </span> </div> " + 
						" </td> " + 
						" </tr>";
				}

				// lets display the successfullyAdded message.
				
				if ((successfullyAddedEmailIds != null) && (!(successfullyAddedEmailIds.equals("")))) {
					statusMessage += "<tr> " +
						" <td colspan='2'> " +
						" <div  class='alert alert-success'> " +
						" <span class='normalText'> " + 
						" This user is already a member of the the TraceCloud system." +
						"<br> We have sent them an email and when they log in" +
						" they should see these projects in their dashboards.<br>" ;
						
					String [] emailIds = {successfullyAddedEmailIds};
					if (successfullyAddedEmailIds.contains(",")){
						emailIds = successfullyAddedEmailIds.split(",");
					}
					for (int i=0; i<emailIds.length; i++){
						statusMessage += "<br>" + emailIds[i];  
					}
					statusMessage += " </span> </div> " + 
						" </td> " + 
						" </tr>";
				}		
				

			%>
			
			<div>
					<table>
						<%=statusMessage %>
					</table>
					
						
			</div>
			<div>
					<input type='button'  class="btn btn-danger btn-sm" name='cancel' id='cancel' value='Close' onClick="document.getElementById('inviteOthersDiv<%=project.getProjectId() %>').style.display='none';"></input>
				
			</div>
			<%} 
			else {
				// since the person performing this action is NOT an admin of this project, 
				// lets just send an email to the project administrator, with this request.
				
				
				// lets get the list of roles names
				String rolesName = "";
				String [] rolesArray = roles.split(",");
				for(int i = 0;i< rolesArray.length; i++){
					try {
					if (rolesArray[i] != null ){
						int roleId = Integer.parseInt(rolesArray[i]);
						Role role = new Role(roleId);
						rolesName += role.getRoleName() + ", ";
					}
					}
					catch (Exception e){
						e.printStackTrace();
					}
				}
						
				// lets drop the last ,
				if (rolesName.contains(",")){
					rolesName = (String) rolesName.subSequence(0,rolesName.lastIndexOf(","));
				}
				
				
				// get the project admins email ids
				int targetProjectId = projectId;
				Project targetProject = new Project(targetProjectId, databaseType);
				ArrayList administrators = ProjectUtil.getProjectAdministrators(targetProjectId);
				String to = "";
				Iterator a = administrators.iterator();
				while (a.hasNext()){
					String adminEmailId = (String) a.next();
					to += adminEmailId + ",";
				}
				String cc = user.getEmailId();
				String subject = "Request for project access";
				String message = "" +
						"<br><br><br>Hello, " +
						"<br><br>I am reaching out to you as you are an Administrator of this project. I would like to invite the following users to this project. Here are the details." +
						"<br><br>My Name : " + user.getFirstName() + " " + user.getLastName() + 
						"<br>My Email Address :" + user.getEmailId() + 
						"<br><br>Here are the project details  " +
						"<br><br>Project Short Name  : " + targetProject.getShortName() +
						"<br>Project Name        : " + targetProject.getProjectName() + 
						"<br>Project Description : " + targetProject.getProjectDescription() + 
						"<br><br>Roles	: " + rolesName + 
						"<br><br>Invitee Email Ids : " + emailIdString + 
						"<br><br>You are recieving this email becuase you are an Administrator for this project. " +
						"<br><br>Regards" +
						"<br><br>" + user.getFirstName() + " " + user.getLastName();
				
				
				// lets send the email out to the toEmailId;
				ArrayList toArrayList = new ArrayList();
				if (to != null){
					to = to.trim();
					if (!to.equals("")){
						if (to.contains(",")){
							String [] toEmails = to.split(",");
							for (int i=0; i < toEmails.length; i++ ){
								toArrayList.add(toEmails[i]);
							}
						}
						else {
							toArrayList.add(to);
						}
					}
				}
				
				ArrayList ccArrayList = new ArrayList();
				if (cc != null){
					cc = cc.trim();
					if (!cc.equals("")){
						if (cc.contains(",")){
							String [] ccEmails = cc.split(",");
							for (int i=0; i < ccEmails.length; i++ ){
								ccArrayList.add(ccEmails[i]);
							}
						}
						else {
							ccArrayList.add(cc);
						}
					}
				}
				

				
				MessagePacket mP = new MessagePacket(toArrayList, ccArrayList, subject, message, "");
				
				String mailHost = this.getServletContext().getInitParameter("mailHost");
				String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
				String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
				String smtpPort = this.getServletContext().getInitParameter("smtpPort");
				String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
				String emailUserId = this.getServletContext().getInitParameter("emailUserId");
				String emailPassword = this.getServletContext().getInitParameter("emailPassword");
				
				try {
				EmailUtil.email(mP, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword );
				}
				catch (Exception e){
					e.printStackTrace();
				}
			%>
			<div class= 'userSuccessAlert'>
				<table width='100%' >
					<tr>
						<td >
							<span class='normalText'> An email has been sent to the Project Administrators.
			    			You have been CC'd on this email. Please check your mail box.
			    		</td>
					</tr>
				</table>
			</div>
			<div>
					<input type='button'  class="btn btn-danger btn-sm" name='cancel' id='cancel' value='Close' onClick="document.getElementById('inviteOthersDiv<%=project.getProjectId() %>').style.display='none';"></input>
				
			</div>
			
			<%} %>
	<%} %>










<%} %>
