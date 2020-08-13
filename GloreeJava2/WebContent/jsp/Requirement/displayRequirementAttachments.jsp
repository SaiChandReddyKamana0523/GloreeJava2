<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String dRCIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dRCIsLoggedIn == null) || (dRCIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	// authorizatoin 
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dRCIsMember = false;
	SecurityProfile dRCSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dRCSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		dRCIsMember = true;
	}
	
	
	User user = dRCSecurityProfile.getUser();
	String userEmailId = user.getEmailId();
	
	boolean isUserAnAdmin = false; 
	if (dRCSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
		isUserAnAdmin = true;
	}
	
	
	
	%>

<%if(dRCIsMember){ 
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);
	int folderId = requirement.getFolderId();
	
	boolean updateDisabled = false ;
	if (!(dRCSecurityProfile.getPrivileges().contains("updateRequirementsInFolder" 
			+ requirement.getFolderId()))){
	
		updateDisabled = true;
	}

	///////////////////////////////SECURITY CODE ////////////////////////////
	// if the requirement worked on, doesn't belong to the project the user is 
	// currently logged into, then a user logged into project x is trying to 
	// hack into a req in project y by useing requirementId parameter.
	if (requirement.getProjectId() != project.getProjectId()) {
		return;
	}
	///////////////////////////////SECURITY CODE ////////////////////////////

%>
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>

<br>
<div class="panel panel-info" > 	
		
	<div class="panel-heading " > 
		<%=requirement.getRequirementFullTag() %>  Attachments 
		
		<div style='float:right'>
			<input type='button' class='btn btn-primary btn-xs' value='Attach a new file' 
			onClick='addRequirementAttachmentForm(<%=requirement.getRequirementId()%>, <%=requirement.getFolderId()%>);'>
		</div>
		
	</div>
	
		
	<div id='displayRequirementAttachments' class='panel-body' >
		<div id='addRequirementAttachmentsDiv'></div> 
		<table class='table'>
		<% ArrayList attachments = requirement.getRequirementAttachments(databaseType);
		  %>
				
			
			<% Iterator atachmentIterator = attachments.iterator();
			while (atachmentIterator.hasNext()) {
				

				RequirementAttachment attachment = (RequirementAttachment) atachmentIterator.next();
				%>
				<tr >
					<td >
					<%=attachment.getTitle() %>
					</td>
				
					<td >
					<div class="btn-group ">
	
	
					  <button type="button" class="btn btn-info btn-sm dropdown-toggle" data-toggle="dropdown">
					   <span class="glyphicon glyphicon-cog " style=" color: white"></span> 
					  </button>
					  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					  <ul class="dropdown-menu" role="menu" style="text-align:left;">
					  
						<% if (!(updateDisabled)){ 
							// Users need to have edit privs on requireemnts in this folder
							// before they can remove attachments. 
						%>
					 
					   	<li style='display:block'>
					   		<a href="#" 
					   			onclick='document.getElementById("deleteAttachment<%=attachment.getRequirementAttachmentId()%>Div").style.display="block";'
					   		> Delete File</a></li>
					   	<li style='display:block'>
					   		<a href="#" 
					   			onclick='updateRequirementAttachmentForm(<%=attachment.getRequirementAttachmentId() %>, <%=requirement.getRequirementId() %>,<%=requirement.getFolderId()%>);'
					   		> Change File</a></li>
					   	<li style='display:block'>
					   		<a href="#" 
					   			onclick='document.getElementById("updateAttachmentDescription<%=attachment.getRequirementAttachmentId()%>Div").style.display="block"'
					   		> Update Title File</a></li>
					   	
					   <%} %>
					   	<li style='display:block'>
					   		<a href='/GloreeJava2/servlet/RequirementAction?action=downloadAttachment&attachmentId=<%=attachment.getRequirementAttachmentId()%>'
							target='_blank'>
					 		Download File</a></li>
					   </ul>
					</div>
				</td>
				</tr>
				<tr>
					<td colspan='2' style='border-top:none'>		
					<div id='updateAttachment<%=attachment.getRequirementAttachmentId()%>Div' style="display:none;" class= 'alert alert-success'>
						
					</div>						
						
						
					&nbsp;&nbsp;&nbsp;&nbsp;	
					<div id='deleteAttachment<%=attachment.getRequirementAttachmentId()%>Div' style="display:none;" class= 'alert alert-success'>
						<span class='normalText'>
						<br><br>Are you sure you want to permanently remove this attachment?
						<br>
						<br>							
						<input type='button' name='deleteAttachmentButton' id='deleteAttachmentButton' value='Delete File'  
							class="btn btn-xs btn-outline-primary"  
							style="border-color:red; color:red; background-color:white; "
							onclick='deleteRequirementAttachment(<%=attachment.getRequirementId()%>,
							<%=attachment.getRequirementAttachmentId()%>)'>
						<input type='button' name='Cancel' id='Cancel' value='Cancel' 
							class="btn btn-xs btn-outline-primary"  
							style="border-color:red; color:red; background-color:white; "
						 	onclick='document.getElementById(
						 	"deleteAttachment<%=attachment.getRequirementAttachmentId()%>Div").style.display="none";'>
						 </span>
						 <br><br>
					</div>	
					
					&nbsp;&nbsp;&nbsp;&nbsp;	
					<div id='updateAttachmentDescription<%=attachment.getRequirementAttachmentId()%>Div' style="display:none;" class= 'alert alert-success'>
						<br><br>Update Title
						<br>
						<br>
						<table class='table'>			
							<tr> 
							<td  style="width:200px"> 
									<span class='normalText'>
									Title <sup><span style="color: #ff0000;">*</span></sup> 
									</span>
								</td>
								<td>
									 <span class='normalText'>
									<textarea id="requirementAttachmentDescription" name="requirementAttachmentDescription" rows="5" cols="80" ></textarea>
									</span> 
								</td>
							</tr>
							<tr>
								<td>
								</td>
								<td>
									<input type='button' name='updateAttachmentDescriptionButton' id='updateAttachmentDescriptionButton' value='Update Title' 
									 class="btn btn-xs btn-outline-primary"  
									style="border-color:blue; color:blue; background-color:white; "
									onclick='updateRequirementAttachmentDescription(<%=attachment.getRequirementId() %>,<%=attachment.getRequirementAttachmentId() %>);'>
							
								<input type='button' name='Cancel' id='Cancel' value='Cancel' 
								 class="btn btn-xs btn-outline-primary"  
									style="border-color:blue; color:blue; background-color:white; "
								 	onclick='document.getElementById(
								 	"updateAttachmentDescription<%=attachment.getRequirementAttachmentId()%>Div").style.display="none";'>
								
								</td>
							</tr>			
						</table>	
						
						 <br><br>
					</div>	
					
					
				</div>
				</td>
			</tr>
									
			<% }%>
	
</table>
	
			
			
	</div>
</div>
<%}%>