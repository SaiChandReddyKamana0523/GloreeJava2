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
	String dEPDatabaseType = this.getServletContext().getInitParameter("databaseType");
	SecurityProfile dEPSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User dEPUser = dEPSecurityProfile.getUser();
	String dEPCTCID = request.getParameter("CTCID");
	String dEPSCRID = request.getParameter("SCRID");
	ArrayList tRProxies = null;
	if ((dEPCTCID != null ) && (!(dEPCTCID.equals(""))) && (!(dEPCTCID.equals("null"))) ){	
		
		tRProxies = ClearQuestUtil.getProxies(dEPCTCID, "Test Result", dEPUser, dEPSecurityProfile, dEPDatabaseType);
	}
	if ((dEPSCRID != null ) && (!(dEPSCRID.equals(""))) && (!(dEPSCRID.equals("null")))){
		tRProxies = ClearQuestUtil.getProxies(dEPSCRID, "SCR", dEPUser, dEPSecurityProfile, dEPDatabaseType);
	}
	
	
	
%>
<div>
	<%
	Iterator tRP = tRProxies.iterator();
	while (tRP.hasNext()){
		Requirement tRProxy = (Requirement) tRP.next();
		
		if (!(dEPSecurityProfile.getRoles().contains("MemberInProject" + tRProxy.getProjectId()))){
			// this user is not a member of the project where this TR Proxy exists. So, don't show it.
			continue;
		}
		ArrayList upStreamCIA = tRProxy.getUpStreamCIARequirements(dEPSecurityProfile, 3, 51, dEPDatabaseType);
		// because upStreamCIA needs to be shown in  a nice trace tree format
		// and because it was built going up the chain, we need to reverse it
		// to get it in the right order.
		Collections.reverse(upStreamCIA);
		ArrayList downStreamCIA = tRProxy.getDownStreamCIARequirements(dEPSecurityProfile, 3, 51, dEPDatabaseType);
		String cellStyle = "normalTableCell";
		int j = 0;
		
		
		
		%>
		<table class='paddedTable' >
			<tr>
				<td >				
					<div class='alert alert-success'>
						<div style="float: right;" id="closeCIADiv">
							<a 	onclick='
									document.getElementById("existingProxiesDiv").style.display="none";
									document.getElementById("openExistingProxiesDiv").style.display="block";
								' href="#"> Close </a>
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
								<a href="/GloreeJava2/servlet/DisplayAction?dO=req&dReqId=<%=tRProxy.getRequirementId() %>" target="_blank" >
		 									<%=tRProxy.getProjectShortName()%> - <%=tRProxy.getRequirementFullTag()%> : Ver-<%=tRProxy.getVersion()%> :  <%=tRProxy.getRequirementNameForHTML() %></a> 
		 									
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
								<td>
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
</div>



