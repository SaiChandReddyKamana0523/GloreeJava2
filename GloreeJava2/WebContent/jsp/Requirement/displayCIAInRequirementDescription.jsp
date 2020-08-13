<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<% 
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	int thisProjectId = 0;
	try {
		thisProjectId = project.getProjectId();
	}
	catch (Exception e){
		
	}
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	
	int currentSprintId = 0;
	try {
		currentSprintId = Integer.parseInt(request.getParameter("currentSprintId"));
	}
	catch (Exception e){
		// do nothing
	}
	
	Sprint currentSprint = new Sprint(currentSprintId, databaseType);
	
	
	Requirement requirement = new Requirement(requirementId, databaseType);
	
	User user = securityProfile.getUser();

	if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
			+ requirement.getFolderId()))){
		return;
	}
	
	int numberOfUpstreamReqsToShow = 100;
	int numberOfDownstreamReqsToShow = 100;
	
	int upCIADepth  = 1; 
	int downCIADepth = 1; 
	if (currentSprintId > 0 ){
		// this must be an ask coming from Agile. so lets increaes the depth
		downCIADepth = 4;
	}
	
	ArrayList upStreamCIA = requirement.getUpStreamCIARequirements(securityProfile, upCIADepth , numberOfUpstreamReqsToShow, databaseType);
	// because upStreamCIA needs to be shown in  a nice trace tree format
	// and because it was built going up the chain, we need to reverse it
	// to get it in the right order.
	Collections.reverse(upStreamCIA);
	ArrayList downStreamCIA = requirement.getDownStreamCIARequirements(securityProfile, downCIADepth , numberOfDownstreamReqsToShow, databaseType);
	String cellStyle = "normalTableCell";
	int j = 0;
	
	
	
%>
	<div >
		
		<table>
			
			<%if (upStreamCIA.size() > 0){
			%>
			<%
				Iterator i = upStreamCIA.iterator();
				while (i.hasNext()){
					TraceTreeRow traceTreeRow = (TraceTreeRow) i.next();
					Requirement r = traceTreeRow.getRequirement();
					
									
					%>
					<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
						
						
						
						<td colspan='3'>
				 			<%for (int s = 0; s< (3 - traceTreeRow.getLevel()); s++){
				 			// we do this magic to make the spacing appear correct in this trace tree.
							%>
								&nbsp;&nbsp;&nbsp;
							<%}%>
							
							
						
							<span class='normalText' style="width:50px;">
							<%=r.getRequirementFullTag() %>
							</span>
							
							&nbsp;&nbsp;
							
							
							
							
						 	<%if (r.getTestingStatus().equals("Pending")){ %>
						 		<span style='background-color: #FFFF66;' class='normalText' >Test Pending </span>
						 	<%} %> 
							<%if (r.getTestingStatus().equals("Pass")){ %>
								<span style='background-color: #CCFF99;' class='normalText' > Test Pass   </span>
							<%} %> 
							<%if (r.getTestingStatus().equals("Fail")){ %>
								<span style='background-color: #FFa3AF;' class='normalText' > Test Fail   </span>
							<%} %> 
			
							&nbsp;&nbsp;							
							
							<%if (r.getProjectId()== thisProjectId){
								// this req is in this project. so we can make it clickable / navigable.
							%>								   						
					 			<span class='normalText'>
						
								<a href="#" onClick='
									document.getElementById("contentCenterF").style.display="none";
									displayFolderInExplorer(<%=r.getFolderId()%>);
									displayFolderContentCenterA(<%=r.getFolderId() %>);
									displayRequirement(<%=r.getRequirementId()%>)'>
									
								
								<%=r.getRequirementNameForHTML() %></a> 
								</span>
   							<%}
							else {
								// this req is in an external project. Curently tracecloud can not
								// support more than 1 project per browser.
								String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");
										%>
								<span class='normalText' title="Requirement Name : <%=r.getRequirementNameForHTML() %>">
	   							<a href="#" onClick='
		   							alert("Since this Requirement is in an external project please paste this URL" +
			   						" in a different browser (IE, FireFox).          " +
			   						"<%=url%>");'>
	   							<%=r.getProjectShortName()%>:<%=r.getRequirementFullTag() %> : Ver-<%=r.getVersion()%> :  <%=r.getRequirementNameForHTML() %></a>
	   							
	   							</span>
							<%} %>
								
							
							
							
							<br>
							<%for (int s = 0; s< (4 - traceTreeRow.getLevel()); s++){
				 			// we do this magic to make the spacing appear correct in this trace tree.
							%>
								&nbsp;&nbsp;&nbsp;
							<%}%>
							<%
				 			if (traceTreeRow.getTracesToSuspectRequirement() == 0){
					 		%>
					 			<img src="/GloreeJava2/images/cTrace1.jpg" border="0" title='<%=traceTreeRow.getTraceDescriptionWithSafetyInSingleQuotes()%>'>
							<%}
				 			if (traceTreeRow.getTracesToSuspectRequirement() != 0){
						 	%>
						 		<img src="/GloreeJava2/images/sTrace1.jpg" border="0" title='<%=traceTreeRow.getTraceDescriptionWithSafetyInSingleQuotes()%>'>
				 		 	<%} %>
							
						</td>
					</tr>		
	 				
			<%
				}
			}
			else {%>
			<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
				<td colspan='3'>
				<span class='normalText'>No UpStream Requirements exist</span>
				</td>
			</tr>
			
			<%} %>
				









			<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
				<td colspan='3' >
					<table><tr>
						<td>
							<% for (int k=0; k<=3; k++){
							// we do this magic to make the spacing appear correct in this trace tree.
							%>
								&nbsp;&nbsp;&nbsp;
							<%	
							}
							%>
							
							<span style="width:50px;" class='normalText'>
							<b>
							<%=requirement.getRequirementFullTag() %> 
							</b>
							</span>
				
						</td>
						<td >
					
							<%if (requirement.getTestingStatus().equals("Pending")){ %>
								<span style='background-color: #FFFF66;' class='normalText' >Test Pending </span>
							<%} %> 
							<%if (requirement.getTestingStatus().equals("Pass")){ %>
								<span style='background-color: #CCFF99;' class='normalText' > Test Pass   </span>
							<%} %> 
							<%if (requirement.getTestingStatus().equals("Fail")){ %>
								<span style='background-color: #FFa3AF;' class='normalText' > Test Fail   </span>
							<%} %> 
							
							&nbsp;&nbsp;
											
							<span class='normalText'>
							<b><u><%=requirement.getRequirementNameForHTML() %></u></b>
							</span>
						</td>
					</tr>
					</table>
				</td>
			</tr>
			



			<%if (downStreamCIA.size() > 0){ 	
				Iterator i = downStreamCIA.iterator();
				while (i.hasNext()){
					TraceTreeRow traceTreeRow = (TraceTreeRow) i.next();
					Requirement r = traceTreeRow.getRequirement();
					
									
					%>
					<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
						
						
						<td >
				 			<%for (int s = 0; s<(4 + traceTreeRow.getLevel()); s++){
				 				// we do this magic to make the spacing appear correct in this trace tree.
							%>
								&nbsp;&nbsp;&nbsp;
							<%	
							}

				 			
				 			if (traceTreeRow.getTracesToSuspectRequirement() == 0) {
					 		%>
					 			<img src="/GloreeJava2/images/cTrace1.jpg" border="0" title='<%=traceTreeRow.getTraceDescriptionWithSafetyInSingleQuotes()%>'>
							<%}
				 			if (traceTreeRow.getTracesToSuspectRequirement() != 0){
						 	%>
						 		<img src="/GloreeJava2/images/sTrace1.jpg" border="0" title='<%=traceTreeRow.getTraceDescriptionWithSafetyInSingleQuotes()%>'>
				 		 	<%} %>				 				
				 		 	
							<span class='normalText' style="width:50px;">
							<%=r.getRequirementFullTag() %>
							</span>
							
				
							&nbsp;&nbsp;

							
				 		 	
							
						 	<%if (r.getTestingStatus().equals("Pending")){ %>
						 		<span style='background-color: #FFFF66;' class='normalText' >Test Pending </span>
						 	<%} %> 
							<%if (r.getTestingStatus().equals("Pass")){ %>
								<span style='background-color: #CCFF99;' class='normalText' > Test Pass   </span>
							<%} %> 
							<%if (r.getTestingStatus().equals("Fail")){ %>
								<span style='background-color: #FFa3AF;' class='normalText' > Test Fail   </span>
							<%} %> 
							
							&nbsp;&nbsp;
							
							<%if (r.getProjectId()== thisProjectId){
								// this req is in this project. so we can make it clickable / navigable.
							%>								   						
					 			<span class='normalText'>
						
								<a href="#" onClick='
									document.getElementById("contentCenterF").style.display="none";
									displayFolderInExplorer(<%=r.getFolderId()%>);
									displayFolderContentCenterA(<%=r.getFolderId() %>);
									displayRequirement(<%=r.getRequirementId()%>)'>
									
								
								<%=r.getRequirementNameForHTML() %></a> 
								</span>
								
								
   							<%}
							else {
								// this req is in an external project. Curently tracecloud can not
								// support more than 1 project per browser.
								String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");
										%>
								<span class='normalText' title="Requirement Name : <%=r.getRequirementNameForHTML()%>">
	   							<a href="#" onClick='
		   							alert("Since this Requirement is in an external project please paste this URL" +
			   						" in a different browser (IE, FireFox).          " +
			   						"<%=url%>");'>
	   							<%=r.getProjectShortName()%>:<%=r.getRequirementFullTag() %> : Ver-<%=r.getVersion()%> :  <%=r.getRequirementNameForHTML() %>
	   							</a>
	   							</span>
							<%} %>
								

						</td>
						
						<%
								// if this req is agile enabled, and if it's not already in this sprint, lets show add button.
								if ((r.getProjectId()== thisProjectId) && (currentSprintId > 0 )){
									
									if (r.getUserDefinedAttributes().contains("Agile Sprint")){
										// this object is agile enabled, so we show the current agile sprint name
										String sprintName = r.getAttributeValue("Agile Sprint");
										if ((sprintName == null) || (sprintName.equals(""))){
											// Object doesn't belong to any sprint. Lets add it. 
											%>
											<td>
												<span class='normalText'>In Backlog</span>
											</td>
											<td>	
												<div id='addToSprintButtonDiv<%=r.getRequirementId() %>'>	
													<input type='button' id='addToSprint<%=r.getRequirementId() %>' class='btn btn-sm btn-primary'
														 value='Add To This Sprint'
														 onclick='addRequirementToSprint(<%=r.getProjectId() %>, <%=r.getFolderId() %>, <%=currentSprint.getSprintId() %>, <%=r.getRequirementId() %>)'
													 >
												</div>
											 </td>
											 
											<%
										}
										else if (!(sprintName.equals(currentSprint.getSprintName()))) {
											// belongs to some other sprint. Lets give it the option to move it here.
											%>
											
											<td>
												<span class='normalText'><%=sprintName %></span>
											</td>
											<td>
												<div id='addToSprintButtonDiv<%=r.getRequirementId() %>'>
												<input type='button' id='addToSprint<%=r.getRequirementId() %>' class='btn btn-sm btn-primary'
													value='Move To This Sprint'
													onclick='addRequirementToSprint(<%=r.getProjectId() %>, <%=r.getFolderId() %>, <%=currentSprint.getSprintId() %>, <%=r.getRequirementId() %>)'
												>
												</div>
											</td>
											<%
										}
										else {
											// must be in this sprint
											%>
											<td>
											<span class='normalText'><%=sprintName %></span>
											</td>
											<td></td>
											<%
										}
									}
								}
								%>
						
					</tr>		
	 				
			<%
				}
			}
			else {%>
			<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
				<td colspan='3'>
				<span class='normalText'>No DownStream Requirements exist</span>
				</td>
			</tr>
			
			<%} %>
			
		</table>
	</div>