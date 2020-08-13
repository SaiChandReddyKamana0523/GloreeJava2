<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<%
	// authentication only
	String IsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((IsLoggedIn  == null) || (IsLoggedIn.equals(""))){
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
		User user = securityProfile.getUser();
		int reportId = Integer.parseInt(request.getParameter("reportId"));
		Report report = new Report(reportId);

		String subject = "TraceCloud Requirements Report from " + user.getFirstName() + " " + user.getLastName();
		String message = "Hello, \n\n"+
			 "Here is a report from TraceCloud project " +  project.getProjectName() +"." +
			 "\n\nReport Name : "+ report.getReportName() + 
			 "\nReport Description : " + report.getReportDescription() +
			 "\n\nRegards\n" +  user.getFirstName() + " " + user.getLastName() + "\n" +
			 user.getEmailId();
		
	%>	
		<table  class='paddedTable'>
			<tr>
				<td align='left' style='width:140px;'>				
					<span class='subSectionHeadingText'>
					Report Type
					</span>
				</td>
				<td>				
					<span class='subSectionHeadingText'>
					<%=report.getReportType() %>
					</span>
				</td>
			</tr>
			<tr>
				<td align='left' style='width:140px;'>				
					<span class='subSectionHeadingText'>
					Report Owner
					</span>
				</td>
				<td>				
					<span class='subSectionHeadingText'>
					<%=report.getCreatedByEmailId() %>
					</span>
				</td>
			</tr>
			<tr>
				<td align='left' style='width:140px;'>				
					<span class='subSectionHeadingText'>
					Report Owner
					</span>
				</td>
				<td>				
					<span class='subSectionHeadingText'>
					<%=report.getReportDescription() %>
					</span>
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
							<span class='normalText'>Attach Report as</span>
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
							</td>
							<td>
							<span class='normalText'>
								<textarea name='message' id='message' rows='10' cols='100'><%=message%></textarea>
							</span>
							</td>
						</tr>	
						<tr>
							<td>
							<span class='normalText'>Send Report Every
							<br>(Ctrl+Click to select / deselect)
							</span>
							</td>
							<td>
							<span class='normalText'>
								<select name='runTaskOn' id='runTaskOn' MULTIPLE SIZE='7'>
									<option value='Monday'>Monday</option>
									<option value='Tuesday'>Tuesday</option>
									<option value='Wednesday'>Wednesday</option>
									<option value='Thursday'>Thursday</option>
									<option value='Friday'>Friday</option>
									<option value='Saturday'>Saturday</option>
									<option value='Sunday'>Sunday</option>
								</select>
							</span>
							</td>
						</tr>	

						<tr>
							<td colspan='2'>
							<span class='normalText'>
								<input type='button' name='scheduleReport' value='Schedule A New Report' id='scheduleReport'
								onClick='scheduleReport();'>
							</span>
							</td>
						</tr>	
		</table>
<%}%>