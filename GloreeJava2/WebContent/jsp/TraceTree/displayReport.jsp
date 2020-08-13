<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>


<%
	// authentication only
	String displayListReportIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayListReportIsLoggedIn == null) || (displayListReportIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dRIsMember = false;
	Project project= (Project) session.getAttribute("project");
	SecurityProfile dRSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dRSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		dRIsMember = true;
	}
%>

<%if (dRIsMember){ %>


	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	
	<% 
		ArrayList Report = (ArrayList) request.getAttribute("Report");
		
	%>
	 
	
	<div id = 'displayListReportDiv' class='level1Box'>
	<table>
		<tr>
			<td>
				<div id ='reporttActions' class='level2Box'>
				<table align='right'>
					<tr>
						<td class='icons'>
						    <a href='/GloreeJava2/servlet/ReportAction?action=exportReportToExcel' target='_blank'>
						    <img src="/GloreeJava2/images/ExportExcel16.gif"  border="0"></a>
			    		</td>
		        		<td class='icons'>
			        		<a href='/GloreeJava2/servlet/ReportAction?action=exportReportToWord' target='_blank'>
						    <img src="/GloreeJava2/images/ExportWord16.gif"  border="0"></a>
		        		</td>
		        		<td class='icons'>
			        		<a href='/GloreeJava2/servlet/ReportAction?action=exportReportToPDF' target='_blank'>
						    <img src="/GloreeJava2/images/ExportPDF16.gif"  border="0"></a>
		        		</td>
		        	</tr>						
				</table>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<div id ='reportData' class='level2Box'>
				<table id = "Report">				
	
					<%
					    if (Report != null){
					    	Iterator i = Report.iterator();
					    	int j = 0;
					    	String cellStyle = "normalTableCell";
					    	while ( i.hasNext() ) {
					    		Requirement r = (Requirement) i.next();
					    		// a typical uda looks like this 
					    		// Customer:#: SBI:##:Delivery Estimate:#:01/01/12
					    		String uda = r.getUserDefinedAttributes();
								String[] attribs = uda.split(":##:");
								
					    		j++;
					    		// for the first row, print the header and user defined columns etc..
					    		if (j == 1){
					 %>
									<tr>
										<td class='tableHeader' width='350'> Requirement </td>
										<td class='tableHeader'> Owner </td>
										<td class='tableHeader'> Percent Complete</td>
										<td class='tableHeader'> Priority</td>
										<td class='tableHeader'> Status </td>
										<td class='tableHeader'> Trace To</td>
										<td class='tableHeader'> Trace From</td>
										
					<%
									// now to print the custom labels.
									
									for (int k=0; k<attribs.length; k++) {
										String[] attrib = attribs[k].split(":#:");
					%>
					
										<td class='tableHeader'> <%=attrib[0]%></td>
					<%
									}
					%>
									</tr>				 
					<%
					   		 			
					    		}
					    		
					    		// Now for each row in the array list, print the data out.
					    		if ((j%2) == 1){
					    			cellStyle = "normalTableCell";
					    		}
					    		else {
					    			cellStyle = "altTableCell";	
					    		}
					    		String displayRDInReportDiv = "displayRDInReportDiv" + r.getRequirementId();
					    		int cellCount = 7 + attribs.length; 
					 %>
				 				<tr>
							 		<td class='<%=cellStyle%>'>
							 			<span>
							 			<a href="#" onclick= 'displayRequirementDescription(<%=r.getRequirementId()%>,"<%=displayRDInReportDiv%>")'> 
							 			<img src="/GloreeJava2/images/search16.png"  border="0">
							 			</a>
		 								
		 								<a href="#" onClick="displayRequirement(<%=r.getRequirementId()%>)">
		 								<img src="/GloreeJava2/images/puzzle16.gif" border="0">
		 								&nbsp;<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %></a> 
		 								</span>
		 								
		 								
							 		</td>
							 		<td class='<%=cellStyle%>'><%=r.getRequirementOwner()%></td>
							 		<td class='<%=cellStyle%>'><%=r.getRequirementPctComplete()%> %</td>
							 		<td class='<%=cellStyle%>'><%=r.getRequirementPriority()%></td>
							 		<td class='<%=cellStyle%>'><%=r.getApprovalStatus() %></td>
							 		<td class='<%=cellStyle%>'><%=r.getRequirementTraceTo() %></td>
					 				<td class='<%=cellStyle%>'><%=r.getRequirementTraceFrom() %></td>
					 <%
										for (int k=0; k<attribs.length; k++) {
											String[] attrib = attribs[k].split(":#:");
											// To avoid a array out of bounds exception where the attrib value wasn't filled in
											// we print the cell only if array has 2 items in it.
											String attribValue = "";
											if (attrib.length ==2){
												attribValue = attrib[1];
											}
					 %>		
					 						<td class='<%=cellStyle%>'><%=attribValue %></td>
					<%
										}
					%>
					 
				 				</tr>
				 				<tr>
				 					<td colspan='<%=cellCount%>'>
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