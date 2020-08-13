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
		ArrayList sharedRequirements = (ArrayList) request.getAttribute("filteredSharedRequirements");
		SharedRequirementType sRT = (SharedRequirementType) request.getAttribute("sharedRequirementType");
		ArrayList sharedAttributes = sRT.getAllSharedAttributesInRequirementType();
		RequirementType rT = (RequirementType) request.getAttribute("requirementType");
		
		int sRRTBaselineId = Integer.parseInt(request.getParameter("sRRTBaselineId"));
		String onlyNewOrChanged = request.getParameter("onlyNewOrChanged");
%>
	<form id='importSharedRequirementsForm' action='#' method='post'>
	<div class='level2BoxColored' >
	<table width='100%'>
		<tr> 
			<td >
			<div id='displayFiltersLinkDiv' style='display:block'>
				<a href='#' onClick='
				document.getElementById("sharedRequirementsFilterSectionDiv").style.display="block";
				document.getElementById("displayFiltersLinkDiv").style.display="none";

				'>Display Filters</a>
			</div>
			</td>
		</tr>
		<tr>
			<td>
			<div style="height:0;font:0/0 serif;border-bottom:1px dashed #f00"></div>
			</td>
		</tr>
		<tr> 
			<td >
				<div id='filterRequirementsMessageDiv'></div>
			</td>
		</tr>
		<%
		// here we need to display a message if no reqs met the filter crireria
		int matches = 0;
		Iterator sR  = sharedRequirements.iterator();
		while (sR.hasNext()){
			SharedRequirement sharedRequirement = (SharedRequirement) sR.next();
			// if the user has asked that the onlyNewOrChanged is set to yes, then we 
			// count only the diff rows
			if ((onlyNewOrChanged != null) && (onlyNewOrChanged.equals("yes"))){
				if (sharedRequirement.getSourceRequirementVersion() != sharedRequirement.getTargetRequirementVersion()){
					matches++;
				}
			}
			else {
				// we count every row.
				matches++;
			}
		}
		if (matches == 0 ) {
		%>
			<tr> 
			<td >
				<span class='normalText'>
				No Shared Requirements matched your filter criteria.
				</span>
			</td>
			</tr>
		<%
		}
		else {
		%>
			<tr>
				<td>
					<div style="overflow:auto; width:2000px; ">
					<table>
						<tr>
							<td colspan='<%=sharedAttributes.size() + 4 %>'>
							<span class='normalText'>
								<input type='button' id='importUpdateSharedRequirementsButton' value='Import / Update Requirements'
								onClick='importUpdateSharedRequirements(<%=sRT.getSRTId() %>,  <%=sRRTBaselineId%>)'>
							</span>
							</td>
						</tr>
					
						<tr>
							<td class='tableHeader' width='50'>
								<div id='selectAllRequirementsDiv'>
									<span class='sectionHeadingText'>
									<a href='#' onClick="
										document.getElementById('deSelectAllRequirementsDiv').style.display = 'block';
										document.getElementById('selectAllRequirementsDiv').style.display = 'none';
										selectAllRequirementInImportSharedRequirements();">
									Select </a>
									</span>
								</div>
								<div id='deSelectAllRequirementsDiv' style="display:none;">
									<span class='sectionHeadingText'>
									<a href='#' onClick="
										document.getElementById('deSelectAllRequirementsDiv').style.display = 'none';
										document.getElementById('selectAllRequirementsDiv').style.display = 'block';
										deSelectAllRequirementInImportSharedRequirements();">
									Deselect </a>
									</span>
								</div>
								
								 
							</td>
						
							<td  class='tableHeader' width='350'>
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
							<td  class='tableHeader' width='150'>
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
						Iterator i = sharedRequirements.iterator();
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
							
							// if the user has asked that the onlyNewOrChanged is set to yes, then we skip
							// all rows where the source req and target req have the same version id.
							if ((onlyNewOrChanged != null) && (onlyNewOrChanged.equals("yes"))){
								if (sharedRequirement.getSourceRequirementVersion() == sharedRequirement.getTargetRequirementVersion()){
									// the source and target versions are the same. so lets not show this row.
									continue;
								}
							}
							%>
							<tr>
			 					<td class='<%=cellStyle%>'>
				 					
				 					<%
				 					if (
				 							(sRT.getSRMandatoryNotification() == 1)
				 							&&
				 							(sourceRequirement.getUserDefinedAttributes().toLowerCase().contains("mandatory:#:yes"))
				 							&&
				 							(sharedRequirement.getTargetRequirementId() == 0)
				 						){
				 						// this is a mandatory requirement that has not been imported yet. So lets make it mandatory
				 						// also for this shared req type, the enforce mandatory has been set to Yes
				 					%>
				 						<input type='checkbox' CHECKED DISABLED name='requirementId' value='<%=sourceRequirement.getRequirementId()%>'>
				 						<font size='2' color='red'><b>
				 						<span title='The publisher has set this as a Mandatory Requirement. It means if you import any requirement in this set, it is Mandatory for your to import this Requirement'>M</span>
				 						</b></font>
				 					<%
				 					}
				 					else {
				 					%>
				 						<input type='checkbox' name='requirementId' value='<%=sourceRequirement.getRequirementId()%>'>
				 					<%
				 					}
				 					%>
			 					</td>
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
										&nbsp;<%=sourceRequirement.getRequirementFullTag() %> : Ver-<%=sharedRequirement.getSourceRequirementVersion()%> :  <%=sharedRequirement.getSourceRequirementName() %></a> 
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
										&nbsp;<%=targetRequirement.getRequirementFullTag() %> : Ver-<%=sharedRequirement.getTargetRequirementVersion()%> :  <%=sharedRequirement.getTargetRequirementName() %></a> 
									</span>
								</td>
								<%}
								else {%>
							 		<td style='border-left: 1px solid rgb(256, 256, 256);' class='<%=cellStyle%>'><span class='normalText'>&nbsp;</span></td>
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
								<td class='<%=cellStyle%>'></td>
								<td colspan=<%=4%> class='<%=cellStyle%>'>
									<div id='displayRequirementDescriptionDiv<%=sourceRequirement.getRequirementId()%>' style='display:none'>
										<div style='float:right'> 
											<a href='#' onClick='
												document.getElementById("displayRequirementDescriptionDiv<%=sourceRequirement.getRequirementId()%>").style.display="none"'>Close </a>
										</div>
										<br>
										<div>
										<span class='normalText'>
										<b>Source Description : </b><%=sharedRequirement.getSourceRequirementDescription() %>
										<br><br>
										<b>Target Description : </b><%=sharedRequirement.getTargetRequirementDescription() %>
										 <br><br>
										</span>
										</div>
									</div>
								</td>
								<td colspan=<%=sharedAttributes.size()%> class='<%=cellStyle%>'>
								</td>
								
							</tr>
					    <%	
						}
						%>
						<tr>
							<td colspan='<%=sharedAttributes.size() + 4%>'>
							<span class='normalText'>
								<input type='button' id='importUpdateSharedRequirementsButton' value='Import / Update Requirements'
								onClick='importUpdateSharedRequirements(<%=sRT.getSRTId() %>, <%=sRRTBaselineId%>)'>
							</span>
							</td>
						</tr>
					</table>
					</div>
				</td>
			</tr>
		<%} %>
	</table>
	</div>
	
	</form>
<%}%>