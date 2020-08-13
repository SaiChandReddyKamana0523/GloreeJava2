<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<% 
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	int folderId = Integer.parseInt(request.getParameter("folderId"));
	String reportType = request.getParameter("reportType");
	User user = securityProfile.getUser();

	String subject = "TraceCloud Requirements Report from " + user.getFirstName() + " " + user.getLastName();
	String message = "";
	if(reportType.equals("list")){
		message = 
		 "This is a List report from TraceCloud project " +  project.getProjectName() +
		 ". This report was run at " + Calendar.getInstance().getTime() +
		 " by " +  user.getFirstName() + " " + user.getLastName() + " (" +
		 user.getEmailId() + ")";
	}
	if(reportType.equals("traceTree")){
		message = 
		 "This is a TraceTree report from TraceCloud project " +  project.getProjectName() +
		 ". This report was run at " + Calendar.getInstance().getTime() +
		 " by " +  user.getFirstName() + " " + user.getLastName() + " (" +
		 user.getEmailId() + ")";
	}
	
%>


					<table>
						
						<tr>
							<td colspan='2'>
							<span class='normalText'><b>TraceCloud Document Control System</b></span>
							</td>
						</tr>
						
						<tr>
							<td style="width:100px">
							<span class='normalText'>&nbsp;&nbsp;Create </span>
							</td>
							<td style="width:400px">
							<span class='normalText'>
								<select name='tDCSAction' id='tDCSAction' onChange='
								var actionObject = document.getElementById("tDCSAction");
								if (actionObject.selectedIndex == 1) {
									document.getElementById("newTDCSDocumentDiv").style.display = "block";
									document.getElementById("existingTDCSDocumentDiv").style.display = "none";
								}
								if (actionObject.selectedIndex == 2) {
									document.getElementById("newTDCSDocumentDiv").style.display = "none";
									document.getElementById("existingTDCSDocumentDiv").style.display = "block";
								}
								'>
									<option value=''>Select One</option>
									<option value='new'>A new TDCS Document</option>
									<option value='existing'>A new version to an existing TDCS Document</option>
								</select>
							</span>
							</td>
						</tr>
						<tr>
							<td colspan='2' style="width:500px">
								<div id = 'newTDCSDocumentDiv' style='display:none'>
									<table>
										<tr>
											<td style="width:100px">
											<span class='normalText'>Title </span>
											</td>
											<td style="width:400px">
											<span class='normalText'>
											<input type='text' name='title' id='title' size='80' maxlength='1000'></input>
											</span>
											</td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
						<tr>
							<td colspan='2' style="width:500px">
								<div id = 'existingTDCSDocumentDiv' style='display:none'>
									<table>
										<tr>
											<td style="width:100px">
												<span class='normalText'>TDCS Document Id</span>
											</td>
											<td style="width:400px">
												<span class='normalText'>
												<input type='text' name='tDCSDocumentFullTag' id='tDCSDocumentFullTag' size='11' maxlength='11' value='TDCS-XXX'
												onBlur='
												var tDCSDocumentFullTagValue = document.getElementById("tDCSDocumentFullTag").value;
												getTDCSDocumentInfo(tDCSDocumentFullTagValue);
												'> </input>
												</span>
											</td>
										</tr>
										<tr>
											<td colspan='2' style="width:500px">
											<div id='displayTDCSDocumentInfoDiv' style='display:none;'>
											</div>
											</td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
						<tr>
							<td style="width:100px">
							<span class='normalText'>&nbsp;&nbsp;Description</span>
							</td>
							
							<td style="width:400px">
							<span class='normalText'>
							<textarea name='description' id='description' rows='5' cols='80'><%=message %></textarea>
							</span>
							</td>
						</tr>						
						
						
						
						
						<tr>
							<td style="width:100px">
							<span class='normalText'>&nbsp;&nbsp;Save this report as </span>
							</td>
							<td style="width:400px">
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
						<td colspan='2' style="width:500px">
							<a href='#'	onclick= 
							'document.getElementById("emailAttachmentDiv").style.display = "none";'>
							Cancel</a>
							&nbsp;&nbsp;&nbsp;
							<a href='#' onClick='saveTDCSDocument(<%=folderId%>,"<%=reportType %>");'>Save to TDCS</a>
						</td>								
					</table>
