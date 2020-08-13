<!-- GloreeJava2 -->
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

	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);
	int requirementTypeId = requirement.getRequirementTypeId();
	RequirementType rt = new RequirementType(requirementTypeId);
	User user = securityProfile.getUser();
	String userEmailId = user.getEmailId();
	
	if ((securityProfile.getPrivileges().contains("readRequirementsInFolder" + requirement.getFolderId()))){
			
		
	%>
	<div class='alert alert-success'>	

		
		
		
		<div>
			<span class='normalText'>
				<%=requirement.getUserDefinedAttributesFormatted("HTMLNONEMPTY") %>
			</span>
		</div>	
		<hr>
		<div>
			<table>
				<tr>
					<td style='width:100px'>
						<span class='normalText'>Description</span>
					</td>
					<td>
						<span class='normalText'><%=requirement.getRequirementDescription()  %></span>		
					</td>
				</tr>
				
				
		<% ArrayList attachments = requirement.getRequirementAttachments(databaseType);
		if (attachments.size() > 0){  %>
			<tr >
			<th  ><span class='headingText'> Attachments </span></th>
			<td  >
				<span class='normalText'>
				<% Iterator atachmentIterator = attachments.iterator();
				while (atachmentIterator.hasNext()) {
					%>
					<br>
					<%
					RequirementAttachment attachment = (RequirementAttachment) atachmentIterator.next();
					if (
							(attachment.getFileName().toLowerCase().endsWith(".jpg"))
							||
							(attachment.getFileName().toLowerCase().endsWith(".jpeg"))
							||
							(attachment.getFileName().toLowerCase().endsWith(".jpe"))
							||
							(attachment.getFileName().toLowerCase().endsWith(".jfif"))
							||
							(attachment.getFileName().toLowerCase().endsWith(".gif"))
							||
							(attachment.getFileName().toLowerCase().endsWith(".tif"))
							||
							(attachment.getFileName().toLowerCase().endsWith(".tiff"))
							||
							(attachment.getFileName().toLowerCase().endsWith(".png"))
						){
						
						// if this is a jpg file, lets display it.
						%>
						
						<a href='/GloreeJava2/servlet/RequirementAction?action=downloadAttachment&attachmentId=<%=attachment.getRequirementAttachmentId()%>'
						target='_blank'>
						<img border='1' style='width:600px' src="/GloreeJava2/servlet/Image?<%=attachment.getFilePath() %>" >
						</a>
						<br>
						<%
					}
				%>	
					<span title='<%=attachment.getTitle()%>'> 
					<a href='/GloreeJava2/servlet/RequirementAction?action=downloadAttachment&attachmentId=<%=attachment.getRequirementAttachmentId()%>'
					target='_blank'>
					<%=attachment.getTitle() %>
					</a>
					</span>
					&nbsp;&nbsp;&nbsp;&nbsp;	
					<div id='deleteAttachment<%=attachment.getRequirementAttachmentId()%>Div' style="display:none;" class= 'userSuccessAlert'>
						<span class='normalText'>
						<br><br>Are you sure you want to permanently remove this attachment?
						<br>
						<br>							
						<input type='button' name='deleteAttachmentButton' id='deleteAttachmentButton' value='Delete'
							onclick='deleteRequirementAttachment(<%=attachment.getRequirementId()%>,
							<%=attachment.getRequirementAttachmentId()%>)'>
						<input type='button' name='Cancel' id='Cancel' value='Cancel' 
							onclick='document.getElementById(
							"deleteAttachment<%=attachment.getRequirementAttachmentId()%>Div").style.display="none";'>
						 </span>
						 <br><br>
					</div>						
				<% }%>
				</span>
			</td>
			</tr>
		<%} %>
				
			
			</table>
			
		</div>						
		
	</div>

<%
	}
%>