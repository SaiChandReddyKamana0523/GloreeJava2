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
	String upStream = requirement.getRequirementTraceTo() + ",";
	String downStream = requirement.getRequirementTraceFrom() + ",";
	
	String objectType = "requirement";
	
	String direction  = request.getParameter("direction");
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
		
		Random rand = new Random();
%>









<% 

TraceTreeRow traceTreeRow = null;
Requirement r = null;


	String foldersThantCanBeReportedDangling = FolderUtil.getFoldersThatCanBeReportedDangling(project.getProjectId());
	String foldersThantCanBeReportedOrphan = FolderUtil.getFoldersThatCanBeReportedOrphan(project.getProjectId());
	String enabledForApprovalFolders = FolderUtil.getFoldersThatAreEnabledForApproval(project.getProjectId());
	
	
	
	

	
	
	
	int upStreamDepth = 1;
	int downStreamDepth = 1;

	User user = securityProfile.getUser();

	if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
			+ requirement.getFolderId()))){
		return;
	}
	
	int numberOfUpstreamReqsToShow = 1000;
	int numberOfDownstreamReqsToShow = 1000;
	
	if (direction.equals("up")){
			ArrayList upStreamCIA = requirement.getUpStreamCIARequirements(securityProfile, upStreamDepth, numberOfUpstreamReqsToShow, databaseType);
			// because upStreamCIA needs to be shown in  a nice trace tree format
			// and because it was built going up the chain, we need to reverse it
			// to get it in the right order.
			Collections.reverse(upStreamCIA);			
			int counter = 0;
		%>

			<table class='table' border='0'>
			<%
			// Level 1 Up Stream
			Iterator i = upStreamCIA.iterator();
			while (i.hasNext()){
				traceTreeRow = (TraceTreeRow) i.next();
				r = traceTreeRow.getRequirement();
				
				// lets get a random number for the div
				String divName = rand.nextInt() + "-" + r.getRequirementId() + "up";
				%>
				<tr>
					
					<td style= 'border-top:none; vertical-align:middle'>
						<br><br>
						<div id='<%=divName%>'></div>
					</td>
					<td style= 'border-top:none; vertical-align:middle'>
						<% if (r.getRequirementTraceTo().length() > 0 ) {%>
							<a id='<%=divName %>PlusButton' class='btn btn-xs btn-success' 
									onClick='
										fillNextLevelCIA("<%=divName %>", <%=r.getRequirementId() %> ,"up");
										document.getElementById("<%=divName %>PlusButton").style.display="none";
										document.getElementById("<%=divName %>MinusButton").style.display="block";
								
								'>
								<img src="/GloreeJava2/images/plus.jpg" style="width:30px; height:30px;" border="0">
							</a>
							<a id='<%=divName %>MinusButton' class='btn btn-xs btn-danger' style='display:none;' 
									onClick='
										document.getElementById("<%=divName %>").style.display="none";
										document.getElementById("<%=divName %>PlusButton").style.display="block";
										document.getElementById("<%=divName %>MinusButton").style.display="none";
								
								'>
								<img src="/GloreeJava2/images/minus.jpg" style="width:30px; height:30px;" border="0">
							</a>
							
						<%} %>
					</td>
					<td style= 'border-top:none; vertical-align:middle'>
						
							
					<div 
						class='alert alert-info' 
						style='margin:auto; width:300px; border: 2px dotted red; border-radius: 10px;cursor:pointer'
						onclick="window.open('<%=ProjectUtil.getURL(request,r.getRequirementId(),objectType)%>')"
					>								
						<span class='normalText'>
							<%=r.getRequirementFullTag() %>  : <%=r.getRequirementNameForHTML() %> 
						</span>
						
						<div class='descriptionDiv' style='display:none'>
							<%=r.getRequirementDescription() %>
						</div>
						<div class='attributesDiv' style='display:none'>
							<table class='table table-striped'>
							<% 
							ArrayList attributeValues = RequirementUtil.getAttributeValuesInRequirement(r.getRequirementId());
							Iterator v = attributeValues.iterator();
						  	while (v.hasNext()){
						  		 RAttributeValue a = (RAttributeValue) v.next();
						  		if (a.getAttributeEnteredValue().length() > 0){
					    	%>
								<tr>
									<td class='info'>
										 <span class='normalText'><%=a.getAttributeName() %></span> 
									</td>
								</tr>
								<tr>
									<td>
										<span class='normalText'><%=a.getAttributeEnteredValue()%></span>
									</td>
								</tr>
					    	<%} 
						  	}
					    	%> 
							</table>
						</div>
						<div class='commentsDiv' style='display:none'>
							<span class='normalText' title='Requirement Description'>
				 				<%=r.getRequirementCommentsTable2(databaseType) %>
				 			</span>
						</div>
					</div>
					</td>
					<td style= 'border-top:none; vertical-align:middle'>
						<%
						// if the trace is suspect, then red arrow. else green
						if (upStream.contains("(s)" + r.getRequirementFullTag() + ",")){
							%>
							<img src="/GloreeJava2/images/redLeftArrow.png" style='width:25px;'  border="0">
							<%
						}
						else {
							%>
							<img src="/GloreeJava2/images/greenLeftArrow.png" style='width:25px;'  border="0">
							<%
						}
						%>
						
					</td>
				</tr>
				
			<%}%>
			</table>

<%		}
	else if (direction.equals("down")){
		ArrayList downStreamCIA = requirement.getDownStreamCIARequirements(securityProfile, downStreamDepth, numberOfUpstreamReqsToShow, databaseType);
				
		int counter = 0;
	%>

		<table class='table' border='0'>
		<%
		// Level 1 Up Stream
		Iterator i = downStreamCIA.iterator();
		while (i.hasNext()){
			traceTreeRow = (TraceTreeRow) i.next();
			r = traceTreeRow.getRequirement();
			
			// lets get a random number for the div
			String divName = rand.nextInt() + "-" + r.getRequirementId() + "down";
			%>
			<tr>
				
				<td style= 'border-top:none; vertical-align:middle'>
					<%
					// if the trace is suspect, then red arrow. else green
					if (downStream.contains("(s)" + r.getRequirementFullTag() + ",")){
						%>
						<img src="/GloreeJava2/images/redLeftArrow.png" style='width:25px;'  border="0">
						<%
					}
					else {
						%>
						<img src="/GloreeJava2/images/greenLeftArrow.png" style='width:25px;'  border="0">
						<%
					}
					%>
					
				</td>
				
				
				<td style= 'border-top:none; vertical-align:middle'>
					
						
				<div 
					class='alert alert-info' 
					style='margin:auto; width:300px; border: 2px dotted red; border-radius: 10px;cursor:pointer'
					onclick="window.open('<%=ProjectUtil.getURL(request,r.getRequirementId(),objectType)%>')"
				>							
					<span class='normalText'>
						<%=r.getRequirementFullTag() %>  : <%=r.getRequirementNameForHTML() %> 
					</span>
					
					<div class='descriptionDiv' style='display:none'>
						<%=r.getRequirementDescription() %>
					</div>
					<div class='attributesDiv' style='display:none'>
						<table class='table table-striped'>
						<% 
						ArrayList attributeValues = RequirementUtil.getAttributeValuesInRequirement(r.getRequirementId());
						Iterator v = attributeValues.iterator();
					  	while (v.hasNext()){
					  		 RAttributeValue a = (RAttributeValue) v.next();
					  		if (a.getAttributeEnteredValue().length() > 0){
				    	%>
							<tr>
								<td class='info'>
									 <span class='normalText'><%=a.getAttributeName() %></span> 
								</td>
							</tr>
							<tr>
								<td>
									<span class='normalText'><%=a.getAttributeEnteredValue()%></span>
								</td>
							</tr>
				    	<%} 
				    	}%> 
						</table>
					</div>
					<div class='commentsDiv' style='display:none'>
						<span class='normalText' title='Requirement Description'>
			 				<%=r.getRequirementCommentsTable2(databaseType) %>
			 			</span>
					</div>
				</div>
				</td>
				
				<td style= 'border-top:none; vertical-align:middle'>
					<% if (r.getRequirementTraceFrom().length() > 0 ) {%>
						<a id='<%=divName %>PlusButton' class='btn btn-xs btn-success' 
								onClick='
									fillNextLevelCIA("<%=divName %>", <%=r.getRequirementId() %> ,"down");
									document.getElementById("<%=divName %>PlusButton").style.display="none";
									document.getElementById("<%=divName %>MinusButton").style.display="block";
							
							'>
							<img src="/GloreeJava2/images/plus.jpg" style="width:30px; height:30px;" border="0">
						</a>
						<a id='<%=divName %>MinusButton' class='btn btn-xs btn-danger' style='display:none;' 
								onClick='
									document.getElementById("<%=divName %>").style.display="none";
									document.getElementById("<%=divName %>PlusButton").style.display="block";
									document.getElementById("<%=divName %>MinusButton").style.display="none";
							
							'>
							<img src="/GloreeJava2/images/minus.jpg" style="width:30px; height:30px;" border="0">
						</a>
						
					<%} %>
				</td>
				
				
				<td style= 'border-top:none; vertical-align:middle'>
					<br><br>
					<div id='<%=divName%>'></div>
				</td>
			</tr>
			
		<%}%>
		</table>

<%		}
}
else{%>
	<h1><font color=red">You don't have read permissions on this object. Please work with your project administrator</font></h1>

<%}%>