<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
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
	
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	int folderId = Integer.parseInt(request.getParameter("folderId"));
	String reportType = request.getParameter("reportType");
	User user = securityProfile.getUser();

	String subject = "TraceCloud Requirements Report from " + user.getFirstName() + " " + user.getLastName();
	String message = "Hello, \n\n"+
		 "Here is a report from TraceCloud project " +  project.getProjectName() +
		 ". This report was run at " + Calendar.getInstance().getTime() +
		 "\n\nRegards\n" +  user.getFirstName() + " " + user.getLastName() + "\n" +
		 user.getEmailId();
		
%>


					<table>
						<tr>
							<td colspan='2'>
							<span class='normalText'><b>Email a copy of this report</b></span>
							</td>
						</tr>
						
						<tr>
							<td>
							<span class='normalText'>To</span>
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
							<span class='normalText'>Attach data file</span>
							</td>
							<td>
							<span class='normalText'>
								<select id='attachmentType'>
									<option value='excel'>Excel with Requirements</option>
									<option value='excelVersionComments'>Excel with Requirements, Versions and Comments</option>
									<option value='word'>Word Document</option>
									<option value='pdf'>Adobe Acrobat (PDF) file</option>
								</select>
							</span>
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
						<td colspan='2'>
							<a href='#'	onclick= 
							'document.getElementById("emailAttachmentDiv").style.display = "none";'>
							Cancel</a>
							&nbsp;&nbsp;&nbsp;
							<a href='#' onClick='emailReportAsAttachment(<%=folderId%>,"<%=reportType %>");'>Send Email</a>
						</td>								
					</table>
