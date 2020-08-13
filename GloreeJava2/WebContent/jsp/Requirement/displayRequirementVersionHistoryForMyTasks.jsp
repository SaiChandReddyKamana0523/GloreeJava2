<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String dPCLIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dPCLIsLoggedIn == null) || (dPCLIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	// authorizatoin 
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);
	%>

<%if (securityProfile.getPrivileges().contains("readRequirementsInFolder" 
		+ requirement.getFolderId())){ 
	
	
	String displayRDInReportDiv = "displayRDInReportDiv" + requirementId;
	// this user has read permissions. so we can show him this page.
%>
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<div id='displayVersionHistory' class='alert alert-success'> 
	<b>Requirement Change Log</b>
		<table class='paddedTable' width='100%' >
			<tr>
				<td colspan='5' align='right'>
					<%
					String printable = request.getParameter("printable");
					if ((printable == null) || (printable.equals(""))) {
					%>
						<a href='#' class='btn btn-sm btn-danger' 
							onclick='document.getElementById("<%=displayRDInReportDiv %>").style.display = "none"'
						> Close </a>
					<%} %>
				</td>
			</tr>
			<%
				ArrayList requirementVersionHistory = RequirementUtil.getRequirementVersions(requirementId, databaseType);
					    if (requirementVersionHistory != null){
					    	Iterator i = requirementVersionHistory.iterator();
					    	int j = 1;
					    	String cellStyle = "normalTableCell";
					    	
					    	while ( i.hasNext() ) {
					    		j++;
						    		
					    		// Now for each row in the array list, print the data out.
					    		if ((j%2) == 1){
					    			cellStyle = "normalTableCell";
					    		}
					    		else {
					    			cellStyle = "altTableCell";	
					    		}
					    		
					    		RequirementVersion version = (RequirementVersion) i.next();
					    		
					    		
					    		String uda = version.getVersionUserDefinedAttributes();
					    		if (uda == null){
					    			uda = "";
					    		}
					    		uda = uda.replace(":##:", "<br>");
					    		uda = uda.replace(":#:", "&nbsp;&nbsp;=&nbsp;&nbsp;");
					    	
								%>
							 	<tr>
	
							 		<td>
							 			<table>
							 				<tr>
							 					<td class='<%=cellStyle%>' style='width:100px'>
										 			<span class='normalText'">
										 				Version
										 			</span>
										 		</td>

										 		<td class='<%=cellStyle%>'>
										 			<span class='normalText'">
										 			
										 			<%=version.getVersion() %>
										 			<%
											 			if (requirement.getVersion() == version.getVersion()) {
											    		%>
											    		<font color='blue'><b>Latest</b></font>
											    		<%	
											    		}
										 			%>
										 			</span>
										 		</td>		
										 	</tr>
										 	<tr>				
							 					<td class='<%=cellStyle%>'>
										 			<span class='normalText'">
										 				Changed By
										 			</span>
										 		</td>
										 		<td class='<%=cellStyle%>'>
										 			<span class='normalText'">
										 			<%=version.getVersionCreatedBy() %> <br>
										 			</span>
										 		</td>										 		
													 		
											</tr>
										 	<tr>				
							 					<td class='<%=cellStyle%>'>
										 			<span class='normalText'">
										 				Changed On
										 			</span>
										 		</td>
										 		<td class='<%=cellStyle%>'>
										 			<span class='normalText'">
										 			<%=version.getVersionCreatedDt() %>
										 			</span>
										 		</td>										 		
													 		
											</tr>											
											<tr>
							 					<td class='<%=cellStyle%>'>
										 			<span class='normalText'">
										 				Name
										 			</span>
										 		</td>
							 					<td class='<%=cellStyle%>'>
										 			<span class='normalText'">
										 			<%=version.getVersionName() %>
										 			</span>
										 		</td>
							 				</tr>
							 				<tr>
										 		<td class='<%=cellStyle%>'>
										 			<span class='normalText'">
										 			Description
										 			</span>
										 		</td>		
							 				
										 		<td class='<%=cellStyle%>'>
										 			<span class='normalText'">
										 			<%=version.getVersionDescription() %>
										 			</span>
										 		</td>		
									 		</tr>
							 				<tr>
										 		<td class='<%=cellStyle%>'>
										 			<span class='normalText'">
										 			Attributes
										 			</span>
										 		</td>		

										 		<td class='<%=cellStyle%>'>
										 			<span class='normalText'">
										 			<%=uda%>
										 			</span>
										 		</td>		

							 				</tr>
							 			</table>
							 		</td>						 			 	
							 		

							 	</tr>
								 <%
			    			}
			   	 }
			%>
		</table>
	</div>
<%}%>