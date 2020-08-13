<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<% 
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	String dataType = request.getParameter("dataType");
	
	User user = securityProfile.getUser();

	String subject = "TraceCloud Report  from " + user.getFirstName() + " " + user.getLastName();
		String message = "Hello, \n\n"+
		 "Here is a report from TraceCloud project " +  project.getProjectName() +
		 ".\n" +
		 "This report was run at " + Calendar.getInstance().getTime() +
		 "\n\nRegards\n" +  user.getFirstName() + " " + user.getLastName() + "\n" +
		 user.getEmailId();
%>


					<table>
						<tr>
							<td>
							<span class='normalText'>To</span>
							</td>
							<td>
							<span class='normalText'>
								<input type='text' name='to' id='to' size='80' maxlength='1000'></input>
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
								<textarea name='message' id='message' rows='5' cols='80'><%=message %></textarea>
							</span>
							</td>
						</tr>	
						<td colspan='2'>
							<a href='#'	onclick= 
							'document.getElementById("emailExcelDiv").style.display = "none";'>
							Cancel</a>
							&nbsp;&nbsp;&nbsp;
							<a href='#' onClick='emailExcelAsAttachment("<%=dataType %>");'>Send Email</a>
						</td>								
					</table>
