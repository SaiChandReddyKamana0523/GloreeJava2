<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<% 
	//authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
	%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
	<% }
	
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);
	
	User user = securityProfile.getUser();

	if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
			+ requirement.getFolderId()))){
		requirement.redact();
	}
	
	String subject = "TraceCloud Requirement " + requirement.getRequirementFullTag() +" -- " + user.getFirstName() + " " + user.getLastName();
	String message = 	 "Please take a look at this .\n\n" + "Requirement Details will be inserted here by the system" ; 
		 
%>
	<div class='alert alert-success'>
		
							
							

					<table>
						<tr>
							<td>
							<span class='normalText'>To </span>
							</td>
							<td>
							<span class='normalText'>
								<input type='text' name='to' id='to' size='80' maxlength='1000'></input>
								&nbsp;
								
								<select id='roleId' onChange='displayEasyAddEmailIds();'>
									<option value='-1'>Select Role to Display Email Ids</option>
								<%
									ArrayList roles = RoleUtil.getRoles(project.getProjectId()); 
									Iterator rI = roles.iterator();
									while (rI.hasNext()){
										Role role = (Role) rI.next();
										%>
										<option value='<%=role.getRoleId()%>'><%=role.getRoleName() %></option>
									<%}	%>
								</select>
							</span>
							</td>
						</tr>
						<tr>
							<td>
							<span class='normalText'>CC</span>
							</td>
							<td>
							<span class='normalText'>
								<input type='text' name='cc' id='cc' size='80' maxlength='1000'></input>
							</span>
							</td>
						</tr>
						<tr>
							<td>
							</td>
							<td>
								<div id='displayEasyAddEmailIdsDiv' style='display:none' class='level1Box'></div>
							</td>
						</tr>							
						<tr>
							<td>
							<span class='normalText'>Subject</span>
							<sup><span style="color: #ff0000;">*</span></sup>
							</td>
							<td>
							<span class='normalText'>
								<input type='text' name='subject' id='subject' size='80' maxlength='1000'
								value = '<%=subject %>'></input>
							</span>
							</td>
						</tr>
						<tr>
							<td>
							<span class='normalText'>Message</span>
							<sup><span style="color: #ff0000;">*</span></sup>
							</td>
							<td>
							<span class='normalText'>
								<textarea name='message' id='message' rows='10' cols='100'><%=message %></textarea>
							</span>
							</td>
						</tr>
						<tr>
							<td colspan='2' align='left'>
								<span class='normalText'>
								<input type='button' class='btn btn-primary btn-sm'   name='sendEmailButton' id='sendEmailButton'
								value='  Send Email  '
								onClick='
									document.getElementById("sendEmailButton").disabled=true;
									emailRequirement(<%=requirement.getRequirementId() %>);
								'>
								
								&nbsp;&nbsp;
								<input type='button' class='btn btn-danger btn-sm'   name='closeEmailButton' id='closeEmailButton'
								value='  Close  '
								onClick='document.getElementById("requirementPromptDiv").style.display = "none";'>
								
								
								
								
								</span>
							</td>
						</tr>								
					</table>

				</div>