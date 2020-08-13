<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="com.gloree.beans.*" %>

<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>
<%@ page import="java.util.*" %>




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
	int requirementId = Integer.parseInt((request.getParameter("requirementId")));
	Requirement requirement = new Requirement(requirementId, databaseType);
	Project project = new Project(requirement.getProjectId(), databaseType);
	
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
    
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean readPermissions = true;
	if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
			+ requirement.getFolderId()))){
		readPermissions = false;
	}
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + requirement.getProjectId())){
		isMember = true;
	}
	// you need to be a member of this project and have read permissions before you can see this.
	if (isMember && readPermissions){
%>



<%


int folderId = requirement.getFolderId();

String isGlossary = request.getParameter("isGlossary");
if (isGlossary == null){
	isGlossary = "";
}
%>
<html>
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>TraceCloud - SAAS Agile Scrum Requirements Management - Collaborate, Define, Manage and Deliver your Customer Requirements</title>
 	 <meta name="description" content="Collaboration tools to define, manage and deliver your customer requirements on time and within budget. Significantly improves customer satisfaction ">
	<meta name="keywords" content="free requirements management, saas requirements management tool, online requirements management, doors, requisitepro, customer requirements, shared requirements, tl9000, project management, project requirements, agile, agile requirements management.">

	
	
	<!-- Individual YUI CSS files--> 

	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/autocomplete/assets/skins/sam/autocomplete.css">	
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/reset-fonts-grids/reset-fonts-grids.css"> 
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/resize/assets/skins/sam/resize.css"> 
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/layout/assets/skins/sam/layout.css">
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/button/assets/skins/sam/button.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/menu/assets/skins/sam/menu.css"> 
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/treeview/assets/skins/sam/treeview.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/container/assets/skins/sam/container.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/editor/assets/skins/sam/simpleeditor.css" />
	
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/calendar/assets/skins/sam/calendar.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/editor/assets/skins/sam/editor.css" />
	
	



	<!-- Individual YUI JS files --> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/yahoo-dom-event/yahoo-dom-event.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/animation/animation-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/dragdrop/dragdrop-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/element/element-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/button/button-min.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/resize/resize-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/layout/layout-min.js"></script> 
	
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/treeview/treeview-min.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/container/container-min.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/container/container_core-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/menu/menu-min.js"></script> 
	
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/utilities/utilities.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/calendar/calendar-min.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/editor/simpleeditor-min.js"></script>

	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/datasource/datasource-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/autocomplete/autocomplete-min.js"></script> 

	
	<!-- Gloree JS and CSS files -->
	
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/css/common.css"> 
	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userAccount.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userDashboard.js?v=20200630"></script>
	
	
	
	
	
	<!--  Bootstratp  JS and CSS files -->
	 <script src="/GloreeJava2/js/jquery-3.1.1.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap-tour-standalone.min.js"></script>
	
	 <link href="/GloreeJava2/css/bootstrap.min.css" rel="stylesheet" media="screen">
	 <link href="/GloreeJava2/css/bootstrap-tour-standalone.min.css" rel="stylesheet">
	
	     
	</head>

<body onLoad='displayPrintableRequirement(<%=requirementId %>, <%=folderId%>, "<%=isGlossary%>");'>

<br>
<br>
<br>
<br>
	<table align='center' style='width:1000px' class='table table-striped'>

		<tr>
			<td class='warning' colspan='2' style='width:200px'>
				Printable Copy of <%=requirement.getRequirementFullTag() %> in Project <%=project.getProjectName() %>
			
			</td>
		</tr>
		
		<tr>
			<td class='info' style='width:200px'>
				Name
			</td>
			<td>
				<%=requirement.getRequirementNameForHTML() %>
			
			</td>
		</tr>
		
		<tr>
			<td class='info' style='width:200px'>
				Description
			</td>
			<td>
				<%=requirement.getRequirementDescription() %>
			
			</td>
		</tr>
		
		<tr>
			<%
				String url = ProjectUtil.getURL(request,requirementId,"requirement");
			 %>
			<td class='info' style='width:200px'>
				URL to this object
			</td>
			<td>
				<a href='<%=url%>' target='_blank'> <%=url %></a>
			</td>
		</tr>

		        		
		        		
		        		
		<tr>
			<td class='warning' colspan='2' style='width:200px'>
			
			</td>
		</tr>

		<tr>
			<td class='info' style='width:200px'>
				Object Type
			</td>
			<td>
				<%=requirement.getRequirementTypeName() %>
			
			</td>
		</tr>
		<tr>
			<td class='info' style='width:200px'>
				Owner
			</td>
			<td>
				<%=requirement.getRequirementOwner()	 %>
			
			</td>
		</tr>
		<tr>
			<td class='info' style='width:200px'>
				Locked By
			</td>
			<td>
				<%=requirement.getRequirementLockedBy()	 %>
			
			</td>
		</tr>
		
		
		
		<tr>
			<td class='info' style='width:200px'>
				Folder
			</td>
			<td>
				<%=requirement.getFolderPath() %>
			
			</td>
		</tr>

		<tr>
			<td class='info' style='width:200px'>
				Baselines
			</td>
			<td>
				<%=requirement.getRequirementBaselineString(databaseType) %>
			
			</td>
		</tr>




		<tr>
			<td class='warning' colspan='2' style='width:200px'>
				Status
			</td>
		</tr>
		<tr>
		
			<%
			
				String approvalClass = "Info" ;
				if (requirement.getApprovalStatus().equals("Approved")){
					approvalClass  = "Success";
				}
				if (requirement.getApprovalStatus().equals("Rejected")){
					approvalClass  = "Danger";
				}
				if (requirement.getApprovalStatus().equals("In Approval WorkFlow")){
					approvalClass  = "Info";
				}
				if (requirement.getApprovalStatus().equals("Draft")){
					approvalClass  = "Warning";
				}
				
				
				
				
			%>		
			<td class='info' style='width:200px'>
				Approval Status
			</td>
			<td class='<%=approvalClass%>'>
				<%=requirement.getApprovalStatus() %>
			</td>
		</tr>
		<tr>
			<td class='info' style='width:200px'>
				Completion
			</td>
			<td>
				<%=requirement.getRequirementPctComplete() %>%
			
			</td>
		</tr>
		
		<tr>
			<td class='info' style='width:200px'>
				Testing Status
			</td>
			
			<%
			
				String testingClass = "Warning" ;
				if (requirement.getTestingStatus().equals("Pass")){
					testingClass = "Success";
				}
				if (requirement.getTestingStatus().equals("Fail")){
					testingClass = "Danger";
				}
			
			%>
			<td class='<%=testingClass%>'>
				<%=requirement.getTestingStatus() %>
			
			</td>
		</tr>
		
		

	



		<tr>
			<td class='warning' colspan='2' style='width:200px'>
				Attributes
			</td>
		</tr>
		
	<% 
		ArrayList attributeValues = RequirementUtil.getAttributeValuesInRequirement(requirementId);
	%>
				    
				  	 <%
				  	 Iterator i = attributeValues.iterator();
				  	 while (i.hasNext()){
				  		 RAttributeValue a = (RAttributeValue) i.next();
			    	%>
						<tr>
							<td class='info' style='width:200px'>
								 <%=a.getAttributeName() %> 
							</td>
							<td>
								<%=a.getAttributeEnteredValue()%>
							</td>
						</tr>
			    	<%} %> 
	







		

	
	</table>
	<table align='center' style="width:1000px ">
	

	<% ArrayList attachments = requirement.getRequirementAttachments(databaseType);
	if (attachments.size() > 0){  %>
		<tr><td>&nbsp;</td></tr>
		
		<% Iterator atachmentIterator = attachments.iterator();
		while (atachmentIterator.hasNext()) {
			RequirementAttachment attachment = (RequirementAttachment) atachmentIterator.next();
			
			%>
			<tr><td>
			<%
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
				<img style='border:3px solid blue; width:1000px' src="/GloreeJava2/servlet/Image?<%=attachment.getFilePath() %>" >
				</a>
				<%
			}
			%>	
											
			<br><br>
			<%=attachment.getTitle() %>
			
			<br><br>
			<a href='/GloreeJava2/servlet/RequirementAction?action=downloadAttachment&attachmentId=<%=attachment.getRequirementAttachmentId()%>'
 				target='_blank' class='btn btn-sm btn-primary' style='color:white'>
					Download File
			</a>
			<br><br><br><br>
			</td></tr>						
		<% }
		}%>
		<tr><td>&nbsp;</td></tr>
		<tr>
			<td>
			<div id='requirementCommentsDiv' style="background-color:white" style="background-color:white" >
			</div>
			</td>
		</tr>
		<tr><td>&nbsp;</td></tr>
		<tr>
			<td>
			<div id='requirementCIADiv' style="background-color:white" >
			</div>
			</td>
		</tr>
		<tr><td>&nbsp;</td></tr>
		<tr>
			<td>
			<div id='requirementVersionHistoryDiv' style="background-color:white" >
			</div>
			</td>
		</tr>
		


	</table>
</body>
</html>
<%}
else{%>
	<h1><font color=red">You don't have read permissions on this object. Please work with your project administrator</font></h1>

<%}%>