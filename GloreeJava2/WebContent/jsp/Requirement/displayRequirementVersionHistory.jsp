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
	
	
	boolean userCanUpdateThisReq = false;
	if ((securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
			+ requirement.getFolderId()))){
	
		userCanUpdateThisReq = true;
	}
	
	boolean locked  = false;
	if (!(requirement.getRequirementLockedBy().equals(""))){
		locked = true;
	}
	
	
%>

<%if (securityProfile.getPrivileges().contains("readRequirementsInFolder" 
		+ requirement.getFolderId())){ 
	
	// this user has read permissions. so we can show him this page.
%>
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<div id='displayVersionHistory' class='alert alert-success'> 
	<b>Requirement Version Change Log</b>
		<table class='paddedTable' width='100%' >
			<tr>
				<td colspan='5' align='right'>
					<%
					String printable = request.getParameter("printable");
					if ((printable == null) || (printable.equals(""))) {
					%>
						<a href='#' 
							onclick='document.getElementById("displayVersionHistory").style.display = "none"'
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
					    			cellStyle = "normalTableCell";	
					    		}
					    		
					    		RequirementVersion version = (RequirementVersion) i.next();
					    		
					    		
					    		String uda = version.getVersionUserDefinedAttributes();
					    		if (uda == null){
					    			uda = "";
					    		}
					    		uda = uda.replace(":##:", "<br>");
					    		uda = uda.replace(":#:", "&nbsp;&nbsp;=&nbsp;&nbsp;");
					    	
								%>
								<tr
									onMouseOver=  "this.style.background='#E5EBFF';" 
									onMouseOut=  "this.style.background='white';"
				 				>
				 				<td colspan='3'>
				 					<table style='width:100%' >
									 	<tr>
									 		<td>
									 			<span class='normalText'>
									 			<b>Version</b> :
									 			<%=version.getVersion() %>
									 			<%
										 			if (requirement.getVersion() == version.getVersion()) {
										    		%>
										    		<font color='blue'><b>Latest</b></font>
										    		<%	
										    		}
										 			else {
										 				if (userCanUpdateThisReq){
										 					// This is not a locked req and the user can update this req, so, lets show 
										 					// the Rollback button
										 					if (!(locked)){
										 						// this can be rolled back
										 						%>
													 				&nbsp;&nbsp;
													 				<input type='button' class='btn btn-sm btn-primary' value=' Rollback to Version <%=version.getVersion() %>' 
													 					onclick='rollbackRequirement(<%=requirement.getRequirementId() %>, <%=version.getVersion() %>)'>
													 			<%	
										 					}
										 					else {
										 						%>
													 				&nbsp;&nbsp;
													 				<input type='button' title='Please unlock this object to enable Rollback' class='btn btn-sm btn-primary' DISABLED='DISABLED' value=' Rollback to Version <%=version.getVersion() %>' onclick=''>
													 			<%
										 					}
										 					
										 				}
										 				else {
										 					%>
												 				&nbsp;&nbsp;
												 				<input type='button' title='You do not have update permission on this object' class='btn btn-sm btn-primary' DISABLED='DISABLED' value=' Rollback to Version <%=version.getVersion() %>' onclick=''>
												 			<%
										 				}
										 			
										 			}
									 			%>
									 			</span>
									 		</td>
									 	</tr>
									 	<tr>
									 		<td>
									 			<span class='normalText'>
									 				<b>Created By</b> : <%=version.getVersionCreatedBy() %> 
									 			</span>
									 		</td>
									 	</tr>
									 	<tr>
									 		<td>
									 			<span class='normalText'>
									 			<b>Created On </b> : <%=version.getVersionCreatedDt() %>
									 			</span>
									 		</td>
									 	</tr>
						 				<tr>
											<td bgcolor='pink' width='200'> 
												<span class='headingText'>
												<b>Name</b>
												</span>
											</td>
											<td bgcolor='pink' width='200' > 
												<span class='headingText'>
												<b>Description</b>
												</span>
											</td>
											<td bgcolor='pink' width='200'> 
												<span class='headingText'>
												<b>Attributes</b>
												</span>
											</td>								
										</tr>
									 	<tr>							 			 	
									 		<td   width='200' valign='top'>
									 			<span class='normalText'">
									 			<%=version.getVersionName() %>
									 			</span>
									 		</td>
									 		<td width='200' valign='top' >
									 			<span class='normalText'">
									 			<%=version.getVersionDescription() %>
									 			</span>
									 		</td>		
									 		<td width='200' valign='top'>
									 			<span class='normalText'">
									 			<%=uda%>
									 			</span>
									 		</td>		
									 	</tr>
									 	<tr><td colspan='3'><hr></td></tr>
							 		</table>
							 	</td></tr>
							 	
								 <%
			    			}
			   	 }
			%>
		</table>
	</div>
<%}%>