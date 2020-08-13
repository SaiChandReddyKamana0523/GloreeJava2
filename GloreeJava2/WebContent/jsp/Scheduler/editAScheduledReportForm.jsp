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
	
		int scheduledReportId = Integer.parseInt(request.getParameter("scheduledReportId"));
		ScheduledReport scheduledReport = new ScheduledReport(scheduledReportId);
		Report report = new Report(scheduledReport.getReportId());
		Folder folder  = new Folder(report.getFolderId());
		
		
		User user = securityProfile.getUser();
		
	%>	
		<table   class='paddedTable'>
			<tr>
				<td colspan='2'>
					<table  width='100%' class='paddedTable'>
						<tr>
						<td align='left' bgcolor='#99CCFF'>				
							<span class='subSectionHeadingText'>
							Edit a scheduled recurring report
							</span>
						</td>
						</tr>
					</table>
				</td>
			</tr>

			<tr>
				<td colspan='2' align='left'>
				<a href='#' onClick='displayScheduler();'>Your Recurring Reports</a>
				</td>
			</tr>
			
			<tr>
				<td colspan='2'>
				<table>
					<tr>
						<td align='left' style='width:150px;'>				
							<span class='subSectionHeadingText'>
							Report Location
							</span>
						</td>
		
						<td align='left' >				
							<span class='normalText'>
								<%=folder.getFolderPath() %>
							</span>
						</td>
					</tr>
				</table>
				</td>
			</tr>

			<tr>
				<td colspan='2'>
				<div id='schedulerReportsInFolderDiv'>
	
					<div id = 'reportInFolderDiv' >
				
						<table   >
							
							<tr>
								<td colspan='2'>
								<table>
									<tr>
										<td align='left' style='width:145px;' >				
											<span class='subSectionHeadingText'>
											Report 
											</span>
										</td>
						
										<td align='left' >				
											<span class='normalText'>
											<%=report.getReportName() %>
											</span>
										</td> 	
									</tr>
								</table>
								</td>
								</tr>
							<tr>
								<td colspan='2'>
								<div id='reportInfoDiv'>
									<table  class='paddedTable' >
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
																<input type='text' name='to' id='to' size='80' maxlength='1000' value='<%=scheduledReport.getToEmailAddresses()%>'></input>
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
															<input type='text' name='cc' id='cc' size='80' maxlength='1000' value='<%=scheduledReport.getCcEmailAddresses()%>'></input>
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
																<%
																String excelSelected = "";
																String excelVersionCommentsSelected = "";
																String wordSelected = "";
																String pdfSelected = "";
																if (scheduledReport.getAttachmentType().equals("excel")){
																	excelSelected = "SELECTED";
																}
																if (scheduledReport.getAttachmentType().equals("excelVersionComments")){
																	excelVersionCommentsSelected = "SELECTED";
																}
																if (scheduledReport.getAttachmentType().equals("word")){
																	wordSelected = "SELECTED";
																}
																if (scheduledReport.getAttachmentType().equals("pdf")){
																	pdfSelected = "SELECTED";
																}
																
																
																%>
																<option <%=excelSelected%> value='excel'>Excel with Requirements</option>
																<option <%=excelVersionCommentsSelected%> value='excelVersionComments'>Excel with Requirements, Versions and Comments</option>
																<option <%=wordSelected%> value='word'>Word Document</option>
																<option <%=pdfSelected%> value='pdf'>Adobe Acrobat (PDF) file</option>
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
															value = '<%=scheduledReport.getSubjectValue() %>'></input>
														</span>
														</td>
													</tr>
													<tr>
														<td>
														<span class='normalText'>Message</span>
														</td>
														<td>
														<span class='normalText'>
															<textarea name='message' id='message' rows='10' cols='100'><%=scheduledReport.getMessageValue() %></textarea>
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
																<%
																String mondaySelected = "";
																String tuesdaySelected = "";
																String wednesdaySelected = "";
																String thursdaySelected = "";
																String fridaySelected = "";
																String saturdaySelected = "";
																String sundaySelected = "";
																
																if (scheduledReport.getRunTaskOn().contains("Monday")){
																	mondaySelected = "SELECTED";
																}
																if (scheduledReport.getRunTaskOn().contains("Tuesday")){
																	tuesdaySelected = "SELECTED";
																}
																if (scheduledReport.getRunTaskOn().contains("Wednesday")){
																	wednesdaySelected = "SELECTED";
																}
																if (scheduledReport.getRunTaskOn().contains("Thursday")){
																	thursdaySelected = "SELECTED";
																}
																if (scheduledReport.getRunTaskOn().contains("Friday")){
																	fridaySelected = "SELECTED";
																}
																if (scheduledReport.getRunTaskOn().contains("Saturday")){
																	saturdaySelected = "SELECTED";
																}
																if (scheduledReport.getRunTaskOn().contains("Sunday")){
																	sundaySelected = "SELECTED";
																}
																%>
																
																<option <%=mondaySelected%> value='Monday'>Monday</option>
																<option <%=tuesdaySelected%> value='Tuesday'>Tuesday</option>
																<option <%=wednesdaySelected%> value='Wednesday'>Wednesday</option>
																<option <%=thursdaySelected%> value='Thursday'>Thursday</option>
																<option <%=fridaySelected%> value='Friday'>Friday</option>
																<option <%=saturdaySelected%> value='Saturday'>Saturday</option>
																<option <%=sundaySelected%> value='Sunday'>Sunday</option>
															</select>
														</span>
														</td>
													</tr>	
							
													<tr>
														<td colspan='2'>
														<span class='normalText'>
															<input type='button' name='scheduleReport' value='Update This Scheduled Report' id='scheduleReport'
															onClick='updateAScheduledReport(<%=scheduledReportId%>, <%=scheduledReport.getReportId()%>);'>
															
															&nbsp;&nbsp;&nbsp;&nbsp;
															<input type='button' name='deleteAScheduledReport' value='Delete This Scheduled Report' id='deleteAScheduledReport'
															onClick='deleteAScheduledReport(<%=scheduledReportId%>, <%=scheduledReport.getReportId()%>);'>
															
															
														</span>
														</td>
													</tr>	
									</table>								
								
								
								
								</div>
								</td>
							</tr>
						</table>
					</div>				
				
				</div>
				</td>
			</tr>
						
		</table>
<%}%>