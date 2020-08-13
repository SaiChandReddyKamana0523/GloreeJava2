<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn  == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){

		SharedRequirementType sRT = (SharedRequirementType) session.getAttribute("sharedRequirementType");
		RequirementType rT = (RequirementType) session.getAttribute("requirementType");
		ArrayList sharedAttributes = sRT.getAllSharedAttributesInRequirementType();

		ArrayList noUpdatePermissions = (ArrayList) session.getAttribute("noUpdatePermissions");
		ArrayList createdRequirements = (ArrayList) session.getAttribute("createdRequirements");
		ArrayList updatedRequirements = (ArrayList) session.getAttribute("updatedRequirements");
		
		TDCSDocument targetTDCSDocument = (TDCSDocument) session.getAttribute("targetTDCSDocument");
		TDCSDocument sourceTDCSDocument = (TDCSDocument) session.getAttribute("sourceTDCSDocument");
		
%>
	<form id='importSharedRequirementsForm' action='#' method='post'>
	<div class='level2BoxColored'>
	<table width='1000'>
		<tr> 
			<td >
			<div id='displayFilterLinkDiv'>
			<a href='#' onClick='
				document.getElementById("sharedRequirementsFilterSectionDiv").style.display="block";
				document.getElementById("displayFilterLinkDiv").style.display="none";
			'>Display Filters</a>
			</div>
			</td>
		</tr>
		<tr><td >&nbsp;</td></tr>
		<tr> 
			<td >
				<div class='alert alert-success'>
				<span class='normalText'>
				A history of this import has been saved in TDCS of this project.
				 
				<br> Target Document Id : <%=targetTDCSDocument.getFullTag() %>
				<br> Target Document Version : <%=targetTDCSDocument.getCurrentVersionNumber() %>
				<br> Target Document Title : <%=targetTDCSDocument.getTitle() %>
				
				<br><br>
				A similar copy is stored in the TDCS of the source project.
				<br> Source Document Id : <%=sourceTDCSDocument.getFullTag() %>
				<br> Source Document Version : <%=sourceTDCSDocument.getCurrentVersionNumber() %>
				<br> Source Document Title : <%=sourceTDCSDocument.getTitle() %>
				
				</span>
				
				
				</div>
			</td>
		</tr>

		<tr><td >&nbsp;</td></tr>
		<% if (noUpdatePermissions.size() > 0){ %>
			<tr> 
				<td >
					<span class='normalText'><b>
					<font color='red'>You do not have permissions to updated the following Requirements
					</font></b></span>
				</td>
			</tr>
			<tr>
				<td>
					<div style="overflow:auto; width:2000px; ">
					<table>
						<tr>
							<td class='tableHeader' width='350'>
								<span class='sectionHeadingText'>
								Source Requirement (Tag : Version : Name)
								</span>
							</td>
							<td class='tableHeader' width='150'>
								<span class='sectionHeadingText'>
								Source Baseline
								</span>
							</td>
							<td class='tableHeader' width='350'>
								<span class='sectionHeadingText'>
								Target Requirement (Tag : Version : Name)
								</span>
							</td>
							<td class='tableHeader' width='150'>
								<span class='sectionHeadingText'>
								Target Baseline
								</span>
							</td>
							<%
							Iterator s = sharedAttributes.iterator();

							while (s.hasNext()){
								SharedRequirementTypeAttribute sA = (SharedRequirementTypeAttribute) s.next();
								if (sA.getSRADisplayable() == 1){
								%>
								<td class='tableHeader'>
									<span class='sectionHeadingText'>
									<%=sA.getRTAttribute().getAttributeName() %>
									</span>
								</td>
								<%	
								}
							}
							%>		
						
						</tr>		
						<%
						int counter = 0;
						Iterator i = noUpdatePermissions.iterator();
						int j = 0;
						String cellStyle = "normalTableCell";
						while (i.hasNext()){
							j++;
				    		if ((j%2) == 0){
				    			cellStyle = "normalTableCell";
				    		}
				    		else {
				    			cellStyle = "altTableCell";	
				    		}
				
							SharedRequirement sharedRequirement = (SharedRequirement) i.next();
							Requirement sourceRequirement = new Requirement(sharedRequirement.getSourceRequirementId(), databaseType);
							Requirement targetRequirement = new Requirement(sharedRequirement.getTargetRequirementId(), databaseType);
							%>
							<tr>
						 		<td style='border-left: 1px solid rgb(256, 256, 256);' class='<%=cellStyle%>'>
										<%
										// lets put spacers here for child requirements.
										String req = sourceRequirement.getRequirementFullTag();
									   	int start = req.indexOf(".");
						    		  	while (start != -1) {
						    	            start = req.indexOf(".", start+1);
											out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");				    	         
						  	          	}
										%>							 			
						 			<span class='normalText' >
							 			<a href="#" onclick= '
											document.getElementById("displayRequirementDescriptionDiv"+<%=sourceRequirement.getRequirementId()%>).style.display="block";
								 		'> 
								 		<img src="/GloreeJava2/images/search16.png"  border="0">
		 								</a>
										<img src="/GloreeJava2/images/puzzle16.gif" border="0">
										&nbsp;<%=sourceRequirement.getRequirementFullTag() %> : Ver-<%=sourceRequirement.getVersion()%> :  <%=sharedRequirement.getSourceRequirementName() %></a> 
									</span>
								</td>
						 		<td style='border-left: 1px solid rgb(256, 256, 256);' class='<%=cellStyle%>'>
						 			<span class='normalText'>
										<%=sharedRequirement.getSourceRequirementBaselineName() %> 
									</span>
								</td>
								
				
									
								<% if (targetRequirement.getRequirementId() > 0 ){ %>
						 		<td style='border-left: 1px solid rgb(256, 256, 256);' class='<%=cellStyle%>'>
						 			<%
						 				// its possible that the target req doesn't exist yet.
						 				// we we need to watch out for null pointers.
										// lets put spacers here for child requirements.
										req = targetRequirement.getRequirementFullTag();
						 				if (req!= null){
										   	start = req.indexOf(".");
								    		while (start != -1) {
								    	        start = req.indexOf(".", start+1);
												out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");				    	         
								    		}
						 				}
									%>							 			
						 			<span class='normalText' >
		 								
							 			<a href="#" onclick= '
											document.getElementById("displayRequirementDescriptionDiv"+<%=sourceRequirement.getRequirementId()%>).style.display="block";
								 		'>
								 		<img src="/GloreeJava2/images/search16.png"  border="0">
		 								</a> 					 			
										<img src="/GloreeJava2/images/puzzle16.gif" border="0">
										&nbsp;<%=targetRequirement.getRequirementFullTag() %> : Ver-<%=targetRequirement.getVersion()%> :  <%=sharedRequirement.getTargetRequirementName() %></a> 
									</span>
								</td>
								<%}
								else {%>
							 		<td class='<%=cellStyle%>'><span class='normalText'>&nbsp;</span></td>
								<%} %>
						 		<td style='border-left: 1px solid rgb(256, 256, 256);' class='<%=cellStyle%>'>
						 			<span class='normalText'>
										<%=sharedRequirement.getTargetRequirementBaselineName() %> 
									</span>
								</td>
				
				
								<%
								s = sharedAttributes.iterator();
								while (s.hasNext()){
									SharedRequirementTypeAttribute sA = (SharedRequirementTypeAttribute) s.next();
									if (sA.getSRADisplayable() == 1){
									%>
							 		<td style='border-left: 1px solid rgb(256, 256, 256);' class='<%=cellStyle%>'>
							 			<span class='normalText'>
										<%=sourceRequirement.getAttributeValue(sA.getRTAttribute().getAttributeId())%>
										</span>
									</td>
									<%	
									}
								}
								%>		
				
							</tr>
							<tr>	
								
								<td colspan=<%=sharedAttributes.size() + 4 %> class='<%=cellStyle%>'>
									<div id='displayRequirementDescriptionDiv<%=sourceRequirement.getRequirementId()%>' style='display:none'>
										<div style='float:right'> 
											<a href='#' onClick='
												document.getElementById("displayRequirementDescriptionDiv<%=sourceRequirement.getRequirementId()%>").style.display="none"'>Close </a>
										</div>
										<br>
										<div>
										<span class='normalText'>
										<b>Source Description : </b><%=sharedRequirement.getSourceRequirementBaselineName()%>
										<br><br>
										<b>Target Description : </b><%=sharedRequirement.getTargetRequirementBaselineName()%>
										 <br><br>
										</span>
										</div>
									</div>
								</td>
								
							</tr>
					    <%	
						}
						%>
					</table>
					</div>
				</td>
			</tr>
		<%} %>
		
		











		
		
		<% if (createdRequirements.size() > 0) {%>
			<tr> 
				<td >
					<span class='normalText'><b>
					The following Requirements have been created in the system.
					</b></span>
				</td>
			</tr>
			<tr>
				<td>
					<div style="overflow:auto; width:2000px; ">
					<table>
						<tr>
							<td class='tableHeader' width='350'>
								<span class='sectionHeadingText'>
								Source Requirement (Tag : Version : Name)
								</span>
							</td>
							<td class='tableHeader' width='150'>
								<span class='sectionHeadingText'>
								Source Baseline
								</span>
							</td>
							<td class='tableHeader' width='350'>
								<span class='sectionHeadingText'>
								Target Requirement (Tag : Version : Name)
								</span>
							</td>
							<td class='tableHeader' width='150'>
								<span class='sectionHeadingText'>
								Target Baseline
								</span>
							</td>
							<%
							Iterator s = sharedAttributes.iterator();
							while (s.hasNext()){
								SharedRequirementTypeAttribute sA = (SharedRequirementTypeAttribute) s.next();
								if (sA.getSRADisplayable() == 1){
								%>
								<td class='tableHeader'>
									<span class='sectionHeadingText'>
									<%=sA.getRTAttribute().getAttributeName() %>
									</span>
								</td>
								<%	
								}
							}
							%>		
						
						</tr>		
						<%
						int counter = 0;
						Iterator i = createdRequirements.iterator();
						int j = 0;
						String cellStyle = "normalTableCell";
						while (i.hasNext()){
							j++;
				    		if ((j%2) == 0){
				    			cellStyle = "normalTableCell";
				    		}
				    		else {
				    			cellStyle = "altTableCell";	
				    		}
				
							SharedRequirement sharedRequirement = (SharedRequirement) i.next();
							Requirement sourceRequirement = new Requirement(sharedRequirement.getSourceRequirementId(), databaseType);
							Requirement targetRequirement = new Requirement(sharedRequirement.getTargetRequirementId(), databaseType);
							%>
							<tr>
						 		<td class='<%=cellStyle%>'>
										<%
										// lets put spacers here for child requirements.
										String req = sourceRequirement.getRequirementFullTag();
									   	int start = req.indexOf(".");
						    		  	while (start != -1) {
						    	            start = req.indexOf(".", start+1);
											out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");				    	         
						  	          	}
										%>							 			
						 			<span class='normalText' >
							 			<a href="#" onclick= '
											document.getElementById("displayRequirementDescriptionDiv"+<%=sourceRequirement.getRequirementId()%>).style.display="block";
								 		'> 
								 		<img src="/GloreeJava2/images/search16.png"  border="0">
		 								</a>
										<img src="/GloreeJava2/images/puzzle16.gif" border="0">
										&nbsp;<%=sourceRequirement.getRequirementFullTag() %> : Ver-<%=sharedRequirement.getSourceRequirementVersion() %> :  <%=sharedRequirement.getSourceRequirementName() %></a> 
									</span>
									
								</td>
						 		<td class='<%=cellStyle%>'>
						 			<span class='normalText'>
										<%=sharedRequirement.getSourceRequirementBaselineName() %> 
									</span>
								</td>
								
				
									
								<% if (targetRequirement.getRequirementId() > 0 ){ %>
						 		<td class='<%=cellStyle%>'>
						 			<%
						 				// its possible that the target req doesn't exist yet.
						 				// we we need to watch out for null pointers.
										// lets put spacers here for child requirements.
										req = targetRequirement.getRequirementFullTag();
						 				if (req!= null){
										   	start = req.indexOf(".");
								    		while (start != -1) {
								    	        start = req.indexOf(".", start+1);
												out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");				    	         
								    		}
						 				}
									%>							 			
						 			<span class='normalText' >
		 								
							 			<a href="#" onclick= '
											document.getElementById("displayRequirementDescriptionDiv"+<%=sourceRequirement.getRequirementId()%>).style.display="block";
								 		'>
								 		<img src="/GloreeJava2/images/search16.png"  border="0">
		 								</a> 					 			
										<img src="/GloreeJava2/images/puzzle16.gif" border="0">
										&nbsp;<%=targetRequirement.getRequirementFullTag() %> : Ver-<%=sharedRequirement.getTargetRequirementVersion() %> :  <%=sharedRequirement.getTargetRequirementName() %></a> 
									</span>
								</td>
								<%}
								else {%>
							 		<td class='<%=cellStyle%>'><span class='normalText'>&nbsp;</span></td>
								<%} %>
						 		<td class='<%=cellStyle%>'>
						 			<span class='normalText'>
										<%=sharedRequirement.getTargetRequirementBaselineName() %> 
									</span>
								</td>
				
				
								<%
								s = sharedAttributes.iterator();
								while (s.hasNext()){
									SharedRequirementTypeAttribute sA = (SharedRequirementTypeAttribute) s.next();
									if (sA.getSRADisplayable() == 1){
									%>
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
										<%=sourceRequirement.getAttributeValue(sA.getRTAttribute().getAttributeId())%>
										</span>
									</td>
									<%	
									}
								}
								%>		
				
							</tr>
							<tr>	
								
								<td colspan=<%=sharedAttributes.size() + 4%> class='<%=cellStyle%>'>
									<div id='displayRequirementDescriptionDiv<%=sourceRequirement.getRequirementId()%>' style='display:none'>
										<div style='float:right'> 
											<a href='#' onClick='
												document.getElementById("displayRequirementDescriptionDiv<%=sourceRequirement.getRequirementId()%>").style.display="none"'>Close </a>
										</div>
										<br>
										<div>
										<span class='normalText'>
										<b>Source Description : </b><%=sharedRequirement.getSourceRequirementBaselineName()%>
										<br><br>
										<b>Target Description : </b><%=sharedRequirement.getTargetRequirementBaselineName()%>
										 <br><br>
										</span>
										</div>
									</div>
								</td>
								
							</tr>
					    <%	
						}
						%>
					</table>
					</div>
				</td>
			</tr>		
		<%} %>
		
		
		
		
		
		
		
		
		
		<%if (updatedRequirements.size() > 0 ){ %>
			<tr> 
				<td >
					<span class='normalText'><b>
					The following Requirements have been updated in the system.
					</b></span>
				</td>
			</tr>
			<tr>
				<td>
					<div style="overflow:auto; width:2000px; ">
					<table>
						<tr>
							<td class='tableHeader' width='350'>
								<span class='sectionHeadingText'>
								Source Requirement (Tag : Version : Name)
								</span>
							</td>
							<td class='tableHeader' width='150'>
								<span class='sectionHeadingText'>
								Source Baseline
								</span>
							</td>
							<td class='tableHeader' width='350'>
								<span class='sectionHeadingText'>
								Target Requirement (Tag : Version : Name)
								</span>
							</td>
							<td class='tableHeader' width='150'>
								<span class='sectionHeadingText'>
								Target Baseline
								</span>
							</td>
							<%
							Iterator s = sharedAttributes.iterator();
							while (s.hasNext()){
								SharedRequirementTypeAttribute sA = (SharedRequirementTypeAttribute) s.next();
								if (sA.getSRADisplayable() == 1){
								%>
								<td class='tableHeader'>
									<span class='sectionHeadingText'>
									<%=sA.getRTAttribute().getAttributeName() %>
									</span>
								</td>
								<%	
								}
							}
							%>		
						
						</tr>		
						<%
						int counter = 0;
						Iterator i = updatedRequirements.iterator();
						int j = 0;
						String cellStyle = "normalTableCell";
						while (i.hasNext()){
							j++;
				    		if ((j%2) == 0){
				    			cellStyle = "normalTableCell";
				    		}
				    		else {
				    			cellStyle = "altTableCell";	
				    		}
				
							SharedRequirement sharedRequirement = (SharedRequirement) i.next();
							Requirement sourceRequirement = new Requirement(sharedRequirement.getSourceRequirementId(), databaseType);
							Requirement targetRequirement = new Requirement(sharedRequirement.getTargetRequirementId(), databaseType);
							%>
							<tr>
						 		<td class='<%=cellStyle%>'>
										<%
										// lets put spacers here for child requirements.
										String req = sourceRequirement.getRequirementFullTag();
									   	int start = req.indexOf(".");
						    		  	while (start != -1) {
						    	            start = req.indexOf(".", start+1);
											out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");				    	         
						  	          	}
										%>							 			
						 			<span class='normalText' >
							 			<a href="#" onclick= '
											document.getElementById("displayRequirementDescriptionDiv"+<%=sourceRequirement.getRequirementId()%>).style.display="block";
								 		'> 
								 		<img src="/GloreeJava2/images/search16.png"  border="0">
		 								</a>
										<img src="/GloreeJava2/images/puzzle16.gif" border="0">
										&nbsp;<%=sourceRequirement.getRequirementFullTag() %> : Ver-<%=sourceRequirement.getVersion()%> :  <%=sharedRequirement.getSourceRequirementName() %></a> 
									</span>
								</td>
						 		<td class='<%=cellStyle%>'>
						 			<span class='normalText'>
										<%=sharedRequirement.getSourceRequirementBaselineName() %> 
									</span>
								</td>
								
				
									
								<% if (targetRequirement.getRequirementId() > 0 ){ %>
						 		<td class='<%=cellStyle%>'>
						 			<%
						 				// its possible that the target req doesn't exist yet.
						 				// we we need to watch out for null pointers.
										// lets put spacers here for child requirements.
										req = targetRequirement.getRequirementFullTag();
						 				if (req!= null){
										   	start = req.indexOf(".");
								    		while (start != -1) {
								    	        start = req.indexOf(".", start+1);
												out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");				    	         
								    		}
						 				}
									%>							 			
						 			<span class='normalText' >
		 								
							 			<a href="#" onclick= '
											document.getElementById("displayRequirementDescriptionDiv"+<%=sourceRequirement.getRequirementId()%>).style.display="block";
								 		'>
								 		<img src="/GloreeJava2/images/search16.png"  border="0">
		 								</a> 					 			
										<img src="/GloreeJava2/images/puzzle16.gif" border="0">
										&nbsp;<%=targetRequirement.getRequirementFullTag() %> : Ver-<%=targetRequirement.getVersion()%> :  <%=sharedRequirement.getTargetRequirementName() %></a> 
									</span>
								</td>
								<%}
								else {%>
							 		<td class='<%=cellStyle%>'><span class='normalText'>&nbsp;</span></td>
								<%} %>
						 		<td class='<%=cellStyle%>'>
						 			<span class='normalText'>
										<%=sharedRequirement.getTargetRequirementBaselineName() %> 
									</span>
								</td>
				
				
								<%
								s = sharedAttributes.iterator();
								while (s.hasNext()){
									SharedRequirementTypeAttribute sA = (SharedRequirementTypeAttribute) s.next();
									if (sA.getSRADisplayable() == 1){
									%>
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
										<%=sourceRequirement.getAttributeValue(sA.getRTAttribute().getAttributeId())%>
										</span>
									</td>
									<%	
									}
								}
								%>		
				
							</tr>
							<tr>	
								
								<td colspan=<%=sharedAttributes.size() + 4%> class='<%=cellStyle%>'>
									<div id='displayRequirementDescriptionDiv<%=sourceRequirement.getRequirementId()%>' style='display:none'>
										<div style='float:right'> 
											<a href='#' onClick='
												document.getElementById("displayRequirementDescriptionDiv<%=sourceRequirement.getRequirementId()%>").style.display="none"'>Close </a>
										</div>
										<br>
										<div>
										<span class='normalText'>
										<b>Source Description : </b><%=sharedRequirement.getSourceRequirementBaselineName()%>
										<br><br>
										<b>Target Description : </b><%=sharedRequirement.getTargetRequirementBaselineName()%>
										 <br><br>
										</span>
										</div>
									</div>
								</td>
								
							</tr>
					    <%	
						}
						%>
					</table>
					</div>
				</td>
			</tr>		
		<%} %>
	</table>
	</div>
	</form>
<%}%>