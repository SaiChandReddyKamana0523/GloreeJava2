<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String IsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((IsLoggedIn == null) || (IsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String dDatabaseType = this.getServletContext().getInitParameter("databaseType");
	SecurityProfile dSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User dUser = dSecurityProfile.getUser();
	String dJID = request.getParameter("JID");
	String dJURL = request.getParameter("JURL");
	if (dJID == null ){dJID = "";}
	if (dJURL == null ){dJURL = "";}
	
	String dJPROJECT = request.getParameter("JPROJECT");
	String dJTYPE = request.getParameter("JTYPE");
	String dJPRIORITY = request.getParameter("JPRIORITY");
	String dJLABELS = request.getParameter("JLABELS");
	String dJSTATUS = request.getParameter("JSTATUS");
	String dJRESOLUTION = request.getParameter("JRESOLUTION");
	String dJAFFECTSV = request.getParameter("JAFFECTSV");
	String dJFIXV = request.getParameter("JFIXV");
	String dJASSIGNEE = request.getParameter("JASSIGNEE");
	String dJREPORTER = request.getParameter("JREPORTER");
	String dJCREATED = request.getParameter("JCREATED");
	String dJUPDATED = request.getParameter("JUPDATED");
	
	String dJTITLE = request.getParameter("JTITLE");
	String dJDESCRIPTION = request.getParameter("JDESCRIPTION");
	
	
	ArrayList jiraProxies = null;
		
	jiraProxies = JiraUtil.getProxies(dJID, dJURL, dUser, dSecurityProfile, dDatabaseType);
	
	
	
%>
<div>
	<table>
		<tr>
			<td>
				<table>
					<tr>
						<td style='width:300px'>
							<br>
							<span class="sectionHeadingText10"><b>Jira (<%=dJID %>) Proxy in TraceCloud</b></span>
						</td>
						<td >
							
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>
	
	<%
	Iterator jPI = jiraProxies.iterator();
	if (jiraProxies.size() == 0 ){
		%>
		<div style='border:3px solid lightgray;   padding:8px; margin:8px;'>
			<span class='normalText' >
				This Jira object doesn't yet exist in TraceCloud. 
				As you connect this to Requirements in TraceCloud
			the relationships will start showing here.
			</span>
		</div>
		<%
	}
	
	int previousProjectId = 0;
	while (jPI.hasNext()){
		Requirement jiraProxy = (Requirement) jPI.next();
	
		RequirementType dJRT = new RequirementType(jiraProxy.getRequirementTypeId());
		Folder dJiraFolder = new Folder(jiraProxy.getFolderId());
		JiraUtil.updateJiraProxy(jiraProxy, dJRT, dJiraFolder, dUser, dJID, dJPROJECT, dJTYPE, dJPRIORITY, dJLABELS, dJSTATUS,
				dJRESOLUTION, dJAFFECTSV, dJFIXV, dJASSIGNEE, dJREPORTER, dJCREATED, dJUPDATED, dJURL, dJTITLE, dJDESCRIPTION,
				dSecurityProfile, request, session, dDatabaseType);
		jiraProxy = new Requirement(jiraProxy.getRequirementId(), dDatabaseType);
	
		
		if (!(dSecurityProfile.getRoles().contains("MemberInProject" + jiraProxy.getProjectId()))){
			// this user is not a member of the project where this TR Proxy exists. So, don't show it.
			continue;
		}
		ArrayList upStreamCIA = jiraProxy.getUpStreamCIARequirements(dSecurityProfile, 1, 51, dDatabaseType);
		// because upStreamCIA needs to be shown in  a nice trace tree format
		// and because it was built going up the chain, we need to reverse it
		// to get it in the right order.
		Collections.reverse(upStreamCIA);
		ArrayList downStreamCIA = jiraProxy.getDownStreamCIARequirements(dSecurityProfile, 1, 51, dDatabaseType);
		String cellStyle = "normalTableCell";
		int j = 0;
		
		
		
		%>
		<table class='paddedTable' >
			<tr>
				<td >				
					<div class='alert alert-success'>
						<table width='900px'>
							
				
							<%
							if (jiraProxy != null){
								if (previousProjectId != jiraProxy.getProjectId()){
									// new project. lets print the label
									%>
									<tr>
										<td>
											<span class='normalText'><b>Project : <%=jiraProxy.getProjectShortName() %></b></span>
										</td>
									</tr>
									<%
									previousProjectId = jiraProxy.getProjectId();
								}
							}
							
							if (upStreamCIA.size() > 0){
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
										<td align='left' class='<%=cellStyle %>'>
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
																			   						
								 			<span class='normalText'>
											<a href="/GloreeJava2/servlet/DisplayAction?dO=req&dReqId=<%=r.getRequirementId() %>" target="_blank" >
		 									<%=r.getProjectShortName()%> - <%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %></a> 
		 									</span>
			   							
												
											
											
											
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
					 					<td  class='<%=cellStyle%>' >
					 						<div id = '<%=displayRDInReportDiv%>'> </div>
					 					</td>
					 				</tr>				 											
									
							<%
								}
							}
							else {%>
							<tr>
								<td align='left'>
								<span class='normalText'>No UpStream Requirements exist</span>
								</td>
							</tr>
							
							<%} %>
								
							<tr>
								<td align='left' style='background-color:lightpink'>
								<% for (int k=0; k<=3; k++){
								// we do this magic to make the spacing appear correct in this trace tree.
								%>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<%	
								}
								%>
								<span class='normalText'>
								<a href="/GloreeJava2/servlet/DisplayAction?dO=req&dReqId=<%=jiraProxy.getRequirementId() %>" target="_blank" >
		 									<%=jiraProxy.getProjectShortName()%> - <%=jiraProxy.getRequirementFullTag()%> : Ver-<%=jiraProxy.getVersion()%> :  <%=jiraProxy.getRequirementNameForHTML() %></a> 
		 									
								<b></b>
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
										<td align='left' class='<%=cellStyle %>'>
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
								 			<span class='normalText'>
											<a href="/GloreeJava2/servlet/DisplayAction?dO=req&dReqId=<%=r.getRequirementId() %>" target="_blank" >
		 									<%=r.getProjectShortName()%> - <%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %></a> 
											</span>
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
								<td align='left'>
								<span class='normalText'>No DownStream Requirements exist</span>
								</td>
							</tr>
							
							<%} %>
							
						</table>
					</div>					
					
					
					
				</td>		
		</table>
		
		
		
		<%
	}
	%>
		</td>
	</tr>
	</table>
</div>



