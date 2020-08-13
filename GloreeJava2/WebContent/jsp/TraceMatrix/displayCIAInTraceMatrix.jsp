<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<%
	// this is a cousin of 'DisplayChangeImpactAnalysis.jsp of Requirements folder
	// the diff is that this expects a parameter called DivId, and will close that Div Id
	// the Requirement's DisplayCIA will close a standard div box.
	String  divId = request.getParameter("divId");

	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);
	
	User user = securityProfile.getUser();

	if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
			+ requirement.getFolderId()))){
		return;
	}
	
	ArrayList upStreamCIA = requirement.getUpStreamCIARequirements(securityProfile, 3, 51, databaseType);
	// because upStreamCIA needs to be shown in  a nice trace tree format
	// and because it was built going up the chain, we need to reverse it
	// to get it in the right order.
	Collections.reverse(upStreamCIA);
	ArrayList downStreamCIA = requirement.getDownStreamCIARequirements(securityProfile, 3, 51, databaseType);
	String cellStyle = "normalTableCell";
	int j = 0;
	
	
	
%>
	<div class='alert alert-success'>
		<div style="float: left;" id="closeCommentsDiv">
			<a onclick='document.getElementById("<%=divId%>").style.display="none"' href="#"> Close </a>
	 	</div>
		<table width='100%'>
			<tr>
				<td>
				<span class='normalText'><b>Change Impact Analysis</b></span>
				</td>
			</tr>

			<%if (upStreamCIA.size() > 0){
			%>
			<%
				Iterator i = upStreamCIA.iterator();
				while (i.hasNext()){
					TraceTreeRow traceTreeRow = (TraceTreeRow) i.next();
					Requirement r = traceTreeRow.getRequirement();
					String displayRDInReportDiv = "displayRDInCIADiv" + r.getRequirementId();
					
					j++;
					if ((j%2) == 0){
						cellStyle = "normalTableCell";
					}
					else {
						cellStyle = "altTableCell";	
					}
									
					%>
					<tr>
						<td class='<%=cellStyle %>'>
				 			<%for (int s = 0; s< (3 - traceTreeRow.getLevel()); s++){
				 			// we do this magic to make the spacing appear correct in this trace tree.
							%>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<%}%>
							
							<a href="#" onclick= '
				 				displayRequirementDescription(<%=r.getRequirementId()%>,"<%=displayRDInReportDiv%>")
				 				'> 
								<img src="/GloreeJava2/images/search16.png"  border="0">
							</a> 
							<%if (r.getProjectId()== project.getProjectId()){
								// this req is in this project. so we can make it clickable / navigable.
							%>								   						
					 			<span class='normalText'>
						
								<a href="#" 
									
									
									onClick='
					 						document.getElementById("contentCenterF").style.display = "none";
								
											displayFolderInExplorer(<%=r.getFolderId()%>);
											displayFolderContentCenterA(<%=r.getFolderId() %>);
											displayFolderContentRight(<%=r.getFolderId() %>);		 								
											displayRequirement(<%=r.getRequirementId()%>,"Previous Screen");
											// since we are showing the requirement, lets expand the layout to show content right
											layout.getUnitByPosition("right").expand();
				 						'>
									
								
								<%=r.getRequirementFullTag()%> : Ver-<%=r.getVersion()%> :  <%=r.getRequirementNameForHTML() %></a> 
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
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
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
	 				<tr>
	 					<td  class='<%=cellStyle%>'>
	 						<div id = '<%=displayRDInReportDiv%>'> </div>
	 					</td>
	 				</tr>				 											
					
			<%
				}
			}
			else {%>
			<tr>
				<td>
				<span class='normalText'>No UpStream Requirements exist</span>
				</td>
			</tr>
			
			<%} %>
				
			<tr>
				<td>
				<% for (int k=0; k<=3; k++){
				// we do this magic to make the spacing appear correct in this trace tree.
				%>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<%	
				}
				%>
				<span class='normalText'>
				<b><%=requirement.getRequirementFullTag()%> : Ver-<%=requirement.getVersion()%> :  <%=requirement.getRequirementNameForHTML() %></b>
				</span>
				</td>
			</tr>								


			<%if (downStreamCIA.size() > 0){ 	
				Iterator i = downStreamCIA.iterator();
				while (i.hasNext()){
					TraceTreeRow traceTreeRow = (TraceTreeRow) i.next();
					Requirement r = traceTreeRow.getRequirement();
					String displayRDInReportDiv = "displayRDInCIADiv" + r.getRequirementId();
					
					j++;
					if ((j%2) == 0){
						cellStyle = "normalTableCell";
					}
					else {	
						cellStyle = "altTableCell";	
					}
									
					%>
					<tr>
						<td class='<%=cellStyle %>'>
				 			<%for (int s = 0; s<(4 + traceTreeRow.getLevel()); s++){
				 				// we do this magic to make the spacing appear correct in this trace tree.
							%>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
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
				 			<a href="#" onclick= '
				 				displayRequirementDescription(<%=r.getRequirementId()%>,"<%=displayRDInReportDiv%>")
				 				'> 
								<img src="/GloreeJava2/images/search16.png"  border="0">
							</a> 

							<%if (r.getProjectId()== project.getProjectId()){
								// this req is in this project. so we can make it clickable / navigable.
							%>								   						
					 			<span class='normalText'>
						
								<a href="#" 
									onClick='
					 						document.getElementById("contentCenterF").style.display = "none";
								
											displayFolderInExplorer(<%=r.getFolderId()%>);
											displayFolderContentCenterA(<%=r.getFolderId() %>);
											displayFolderContentRight(<%=r.getFolderId() %>);		 								
											displayRequirement(<%=r.getRequirementId()%>,"Previous Screen");
											// since we are showing the requirement, lets expand the layout to show content right
											layout.getUnitByPosition("right").expand();
				 						'
				 						
				 						>
									
								
								<%=r.getRequirementFullTag()%> : Ver-<%=r.getVersion()%> :  <%=r.getRequirementNameForHTML() %></a> 
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
	   							<%=r.getProjectShortName()%>:<%=r.getRequirementFullTag() %> : Ver-<%=r.getVersion()%> :  <%=r.getRequirementNameForHTML() %>
	   							</a>
	   							</span>
							<%} %>
								

						</td>
					</tr>		
	 				<tr>
	 					<td  class='<%=cellStyle%>'>
	 						<div id = '<%=displayRDInReportDiv%>'> </div>
	 					</td>
	 				</tr>				 											
					
			<%
				}
			}
			else {%>
			<tr>
				<td>
				<span class='normalText'>No DownStream Requirements exist</span>
				</td>
			</tr>
			
			<%} %>
			
		</table>
	</div>