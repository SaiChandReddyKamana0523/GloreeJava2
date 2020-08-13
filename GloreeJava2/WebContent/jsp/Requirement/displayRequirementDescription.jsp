<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);
	int projectId = requirement.getProjectId();
	
	// authentication only
	String displayRequirementCoreIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayRequirementCoreIsLoggedIn == null) || (displayRequirementCoreIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + projectId)){
		isMember = true;
	}
	
	if (isMember){
%>

	<% 
	   	
	   	String hideString = request.getParameter("hideString");
	   	if (hideString == null){
	   		hideString = "";
	   	}
	 	
	 	// if the user does not have read permissions on this requirement,
		// lets redact it. i.e. remove all sensitive infor from it.
		if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
				+ requirement.getFolderId()))){
			requirement.redact();
		}
		///////////////////////////////SECURITY CODE ////////////////////////////
		// if the requirement worked on, doesn't belong to the project the user is 
		// currently logged into, then a user logged into project x is trying to 
		// hack into a req in project y by useing requirementId parameter.
		// NOTE : 	WE ARE REMOVING THIS SECURITY RESTRICTION AS AT THIS POINT WE KNOW THAT
		// A) THE USER IS LOGGED IN
		// B) THE USER HAS READ REQUIRMENTS IN FOLDER PERMISSIONS ON THIS REQUIREMENT
		// C) TO SUPPORT EXTERNAL PROJECT CONNECTIVITY WE ARE SHOWING THE DATA.
		//if (requirement.getProjectId() != project.getProjectId()) {
		//	return;  
		//}
		///////////////////////////////SECURITY CODE ////////////////////////////
	   	
	   	String divId = request.getParameter("divId");
	%>
	<div class='alert alert-success' >
	<table  class='table table-striped'  border='1' width='100%'>
	<!--  lets get the requirement details displayed -->
		<tr>
			<td colspan='2' align='right'>
				<input type='button' class='btn btn-sm btn-danger' style='width:100px' value='Close' 
					onclick='document.getElementById("<%=divId%>").style.display = "none"'> 
			</td>
		</tr>
		<%if (requirement.getDeleted() == 1){ %>
		<tr>
			<td colspan='2'>
				<span class='normalText'> This is a <b> <font color='red'> DELETED </font></b> Requirement. Please restore it prior to working on it.  </span>
			</td>
		</tr>	
		<%} %>		
		<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
			<td width='200' >
				<span class='headingText'> ID  / Version</span>
			</td>
			<td align='left'  >				
				<a href="#" 				
				onClick='
				 			document.getElementById("contentCenterE").style.display = "none";
							document.getElementById("contentCenterF").style.display = "none";
							
							displayFolderInExplorer(<%=requirement.getFolderId()%>);
							displayFolderContentCenterA(<%=requirement.getFolderId() %>);
							displayFolderContentRight(<%=requirement.getFolderId() %>);		 								
							displayRequirement(<%=requirement.getRequirementId()%>);
							// since we are showing the requirement, lets expand the layout to show content right
							layout.getUnitByPosition("right").expand();
						'
				
					>
					
				<img src="/GloreeJava2/images/puzzle16.gif" border="0">&nbsp;<%=requirement.getRequirementFullTag()%> </a> 
				&nbsp;&nbsp;&nbsp;&nbsp;
				<span class='normalText'>Ver-<%=requirement.getVersion() %>
				</span>
			</td>
		</tr>	
		
	
		<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
			<td >
				<span class='headingText'> Completion  / Owner </span>
			</td>
			<td align='left' >				
				<span class='normalText'><%=requirement.getRequirementPctComplete()%>% Complete &nbsp;&nbsp;&nbsp;&nbsp;
				<%=requirement.getRequirementOwner() %>
				</span>
			</td>
		</tr>	
		
		<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
			<td >
				<span class='headingText'> Testing Status </span>
			</td>
			
				
			<%if (requirement.getTestingStatus().equals("Pending")){ %>
		 		<td  >
		 		<span class='normalText' style="background-color:#FFFF66" >
		 			&nbsp;&nbsp;&nbsp;&nbsp;Pending&nbsp;&nbsp;&nbsp;&nbsp;
		 		</span>
		 		</td>
			<%} %> 
			<%if (requirement.getTestingStatus().equals("Pass")){ %>
				<td >
				<span class='normalText' style="background-color:#CCFF99" >
					&nbsp;&nbsp;&nbsp;&nbsp;Pass&nbsp;&nbsp;&nbsp;&nbsp;
				</span>
				</td>
			<%} %> 
			<%if (requirement.getTestingStatus().equals("Fail")){ %>
				<td >
				<span class='normalText' style="background-color:#FFa3AF" >
					&nbsp;&nbsp;&nbsp;&nbsp;Fail&nbsp;&nbsp;&nbsp;&nbsp;
				</span>
				</td>
			<%} %> 
			
		</tr>
		<%
		RequirementType requirementType = new RequirementType(requirement.getRequirementTypeId());
		
		if (requirementType.getRequirementTypeEnableApproval() == 1) {
			/* we print the approval status info only if the req type is enable for approval.*/
		%>
			<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
				<td >
					<span class='headingText'> Approval Status </span>
				</td>
				<% if (requirement.getApprovalStatus().equals("Draft")){ %>
					<td align='left'   >
						<span class='normalText' style="background-color:#FFFF66">
							&nbsp;&nbsp;&nbsp;&nbsp;<%=requirement.getApprovalStatus() %>&nbsp;&nbsp;&nbsp;&nbsp;
						</span>
					</td>										
				<%} %>			
				<% if (requirement.getApprovalStatus().equals("In Approval WorkFlow")){ %>
					<td align='left'  >
						<span class='normalText' style="background-color:#99ccff">												
							&nbsp;&nbsp;&nbsp;&nbsp;<%=requirement.getApprovalStatus() %>&nbsp;&nbsp;&nbsp;&nbsp;
						</span>
					</td>
				<%} %>
				<% if (requirement.getApprovalStatus().equals("Approved")){ %>
					<td align='left'  >
						<span class='normalText' style="background-color:#CCFF99">
							&nbsp;&nbsp;&nbsp;&nbsp;<%=requirement.getApprovalStatus()%>&nbsp;&nbsp;&nbsp;&nbsp;
						</span>
					</td>
				<%} %>
				<% if (requirement.getApprovalStatus().equals("Rejected")){ %>
					<td align='left'   >
						<span class='normalText' style="background-color:#FFA3AF">
							&nbsp;&nbsp;&nbsp;&nbsp;<%=requirement.getApprovalStatus()%>&nbsp;&nbsp;&nbsp;&nbsp;
						</span>
					</td>
				<%} %>
	
			</tr>						
			
			<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
				<td >
					<span class='headingText'> Approvals </span>
				</td>
				<td  >
				<%
					String colorCodedApprovers = requirement.getColorCodedApprovers();
					%>
					<span class='normalText'>
					<%=colorCodedApprovers%>
					</span>
				</td>
			</tr>						
		<%} %>

		<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
			<td >
				<span class='headingText'> Priority </span>
			</td>
			<td align='left'   >				
				<span class='normalText'><%=requirement.getRequirementPriority() %>
				</span>
			</td>
		</tr>
		
		
		
		<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white">
			<td width='200' > <span class='normalText'>Traceability</span></td>
			<td ><div id='displayCIADiv<%=requirementId%>'> </div></td>
			
		</tr>		
		
		<%
		
		ArrayList attributeValues = requirement.getUserDefinedAttributesArrayList();
		Iterator aV = attributeValues.iterator();
		int attributeCount = 0;
		while (aV.hasNext()){
			RAttributeValue a = (RAttributeValue) aV.next();
			%>
			<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
				<td >
					<span class='headingText'> <%=a.getAttributeName() %> </span>
				</td>
				<td align='left'  >				
					<span class='normalText'><%=a.getAttributeEnteredValue() %>
					</span>
				</td>
			</tr>	
			<%
		}
		%>
						 												
								
								
		<%
		if (!(hideString.contains("name:##:") )){
			// the user did NOT request to hide name. so lets display it.
			%>
			
			<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
				<td >
					<span class='headingText'>Name</span>
				</td>
				<td align='left'   >				
					<span class='normalText'><%=requirement.getRequirementNameForHTML()  %></span>
				</td>
			</tr>	
		<%} 
			if (!(hideString.contains("description:##:") )){
			// the user did NOT request to hide description. so lets display it.
			%>
		<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
			<td >
				<span class='headingText'>Description</span>
			</td>
			<td align='left' >				
				<span class='normalText'><%=requirement.getRequirementDescription()  %></span>
			</td>
		</tr>
		<%
			}
		ArrayList comments = RequirementUtil.getRequirementComments(requirementId, databaseType);
		if (comments.size() > 0 ){
		%>
			<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white">
				
				<td colspan='2' align='left' >				
					<table width='100%'>
						<%
						Iterator i = comments.iterator();
						while ( i.hasNext() ) {
							Comment commentObject = (Comment) i.next();
						%>
							<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
						 		<td >
						 			<span class='normalText'">
						 			<%=commentObject.getCommentDate() %>
						 			</span>
						 		</td>
						 		<td >
						 			<span class='normalText'">
						 			Version-<%=commentObject.getVersion() %>
						 			</span>
						 		</td>
								<td >
						 			<span class='normalText'">
						 			<%=commentObject.getCommenterEmailId() %>
						 			</span>
						 		</td>		
						 		
								<td >
						 			<span class='normalText'">
						 			<img src="/GloreeJava2/images/comment16.png" border="0">
						 			<%=commentObject.getHTMLFriendlyCommentNote() %>
						 			</span>
						 		</td>		
						 	</tr>
											
						
						<%} %>
					</table>
				</td>
			</tr>	
		<%} %>	


		<% ArrayList attachments = requirement.getRequirementAttachments(databaseType);
		if (attachments.size() > 0){  %>
			<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white">
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
<%}%>