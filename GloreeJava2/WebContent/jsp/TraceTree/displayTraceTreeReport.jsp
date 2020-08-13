<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>


<%
	// authentication only
	String dTTRIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dTTRIsLoggedIn == null) || (dTTRIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dTTRIsMember = false;
	Project project= (Project) session.getAttribute("project");
	SecurityProfile dTTRSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dTTRSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		dTTRIsMember = true;
	}
%>

<%if (dTTRIsMember){ %>


	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	
	<% 
		ArrayList traceTreeReport = (ArrayList) request.getAttribute("traceTreeReport");
		
	%>
	 
	
	<div id = 'displaytraceTreeReportDiv' class='level1Box'>
	<table>
		<tr>
			<td>
				<div id ='traceTreeReporttActions' class='level2Box'>
				<table align='right'>
					<tr>
						<td class='icons'>
						    <a href='/GloreeJava2/servlet/ReportAction?action=exportTraceTreeReportToExcel' target='_blank'>
						    <img src="/GloreeJava2/images/ExportExcel16.gif"  border="0"></a>
			    		</td>
		        		<td class='icons'>
			        		<a href='/GloreeJava2/servlet/ReportAction?action=exportTraceTreeReportToWord' target='_blank'>
						    <img src="/GloreeJava2/images/ExportWord16.gif"  border="0"></a>
		        		</td>
		        		<td class='icons'>
			        		<a href='/GloreeJava2/servlet/ReportAction?action=exportTraceTreeReportToPDF' target='_blank'>
						    <img src="/GloreeJava2/images/ExportPDF16.gif"  border="0"></a>
		        		</td>
		        	</tr>						
				</table>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<div id ='traceTreeReportData' class='level2Box'>
				<table id = "traceTreeReport">				
					<tr>
						<td class='tableHeader'>Level 1</td>
						<td class='tableHeader'>Level 2</td>
						<td class='tableHeader'>Level 3</td>
						<td class='tableHeader'>Level 4</td>
						<td class='tableHeader' width='350'> Requirement </td>
						<td class='tableHeader'> Owner </td>
						<td class='tableHeader'> Percent Complete</td>
						<td class='tableHeader'> Priority</td>
						<td class='tableHeader'> Status </td>
						<td class='tableHeader'> Trace To</td>
						<td class='tableHeader'> Trace From</td>
						<td class='tableHeader'> User Defined Attributes</td>
					</tr>				 
	
					<%
					    if (traceTreeReport != null){
					    	Iterator i = traceTreeReport.iterator();
					    	int j = 0;
					    	String cellStyle = "normalTableCell";
					    	while ( i.hasNext() ) {
					    		TraceTreeRow tTR = (TraceTreeRow) i.next();
					    		Requirement r = tTR.getRequirement();
					    		j++;
					    		
					    		// Now for each row in the array list, print the data out.
					    		if ((j%2) == 1){
					    			cellStyle = "normalTableCell";
					    		}
					    		else {
					    			cellStyle = "altTableCell";	
					    		}
					    		
					 %>
				 				<tr>
				 				<%if (tTR.getLevel() == 1){ %>
				 					<td class='<%=cellStyle%>'><%=r.getRequirementFullTag() %></td>
				 					<td class='<%=cellStyle%>'>&nbsp;</td>
				 					<td class='<%=cellStyle%>'>&nbsp;</td>
				 					<td class='<%=cellStyle%>'>&nbsp;</td>
				 				<%}
				 				else if (tTR.getLevel() == 2) {
				 					if (tTR.getTracesToSuspectRequirement() == 0){ %>
				 						<!--  does not trace to suspect reqs. -->
				 						<td class='<%=cellStyle%>'> 
				 							<img src="/GloreeJava2/images/cTrace1.jpg" border="0"> 
				 						</td>
				 						<td class='<%=cellStyle%>'>
				 						<font color='green'>
				 						<%=r.getRequirementFullTag() %>
				 						</font>
				 						</td>
				 					<%}
				 					else {%>
				 						<!--  Traces to suspect reqs. -->
				 						<td class='<%=cellStyle%>'> 
				 							<img src="/GloreeJava2/images/sTrace1.jpg" border="0"> 
				 						</td>
				 						<td class='<%=cellStyle%>'>
				 						<font color='red'>
				 						<%=r.getRequirementFullTag() %>
				 						</font>
				 						</td>
				 					<%} %>
				 					
				 					<td class='<%=cellStyle%>'>&nbsp;</td>
				 					<td class='<%=cellStyle%>'>&nbsp;</td>
				 				
				 				<%}
				 				else if (tTR.getLevel() == 3) {
				 				%>
				 					<td class='<%=cellStyle%>'>&nbsp;</td>
				 				<%	if (tTR.getTracesToSuspectRequirement() == 0){ %>
				 						<!--  does not trace to suspect reqs. -->
				 						<td class='<%=cellStyle%>'> 
				 							<img src="/GloreeJava2/images/cTrace1.jpg" border="0"> 
				 						</td>
				 						<td class='<%=cellStyle%>'>
				 							<font color='green'>
				 							<%=r.getRequirementFullTag() %>
				 							</font>
				 						</td>
				 					<%}
				 					else {%>
				 						<!--  Traces to suspect reqs. -->
				 						<td class='<%=cellStyle%>'> 
				 							<img src="/GloreeJava2/images/sTrace1.jpg" border="0"> 
				 						</td>
				 						<td class='<%=cellStyle%>'>
				 							<font color='red'>
				 							<%=r.getRequirementFullTag() %>
				 							</font>
				 						</td>
				 					<%} %>
				 					<td class='<%=cellStyle%>'>&nbsp;</td>
				 				
				 				<%}
				 				else if (tTR.getLevel() == 4) {
					 				%>
					 					<td class='<%=cellStyle%>'>&nbsp;</td>
					 					<td class='<%=cellStyle%>'>&nbsp;</td>
					 				<%	if (tTR.getTracesToSuspectRequirement() == 0){ %>
					 						<!--  does not trace to suspect reqs. -->
					 						<td class='<%=cellStyle%>'> 
					 							<img src="/GloreeJava2/images/cTrace1.jpg" border="0"> 
					 						</td>
					 						<td class='<%=cellStyle%>'>
					 							<font color='green'>
					 							<%=r.getRequirementFullTag() %>
					 							</font>
				 							</td>
					 					<%}
					 					else {%>
					 						<!--  Traces to suspect reqs. -->
					 						<td class='<%=cellStyle%>'> 
					 							<img src="/GloreeJava2/images/sTrace1.jpg" border="0"> 
					 						</td>
					 						<td class='<%=cellStyle%>'>
					 							<font color='red'>
				 								<%=r.getRequirementFullTag() %>
				 								</font>
				 							</td>
					 					<%} %>
					 	
								<%}
				 				String displayRDInReportDiv = "displayRDInReportDiv" + r.getRequirementId();
								%>				 				


									<!--  Now lets fill the regular columns. -->				 				
							 		<td class='<%=cellStyle%>'>
							 			<span>
							 			<a href="#" onclick= 'displayRequirementDescription(<%=r.getRequirementId()%>,"<%=displayRDInReportDiv%>")'> 
							 			<img src="/GloreeJava2/images/search16.png"  border="0">
							 			</a>
		 								
		 								<a href="#" onClick="displayRequirement(<%=r.getRequirementId()%>)">
		 								<img src="/GloreeJava2/images/puzzle16.gif" border="0">
		 								&nbsp;<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %></a> 
		 								</span>
		 								<br>
		 								
							 		</td>
							 		<td class='<%=cellStyle%>'><%=r.getRequirementOwner()%></td>
							 		<td class='<%=cellStyle%>'><%=r.getRequirementPctComplete()%> %</td>
							 		<td class='<%=cellStyle%>'><%=r.getRequirementPriority()%></td>
							 		<td class='<%=cellStyle%>'><%=r.getApprovalStatus() %></td>
							 		<td class='<%=cellStyle%>'><%=r.getRequirementTraceTo() %></td>
					 				<td class='<%=cellStyle%>'><%=r.getRequirementTraceFrom() %></td>
					 				<td class='<%=cellStyle%>'><%=r.getUserDefinedAttributes() %></td>
				 				</tr>
				 				<tr>
				 					<td colspan='4'>
				 						&nbsp;
				 					</td>
				 					<td colspan='8'>
				 						<div id = '<%=displayRDInReportDiv%>'> </div>
				 					</td>
				 					
				 				</tr>
					 <%
					    	}
					    }
					%>
				
				</table>
				</div>
			</td>
		</tr>
	</table>
	</div>
<%}%>