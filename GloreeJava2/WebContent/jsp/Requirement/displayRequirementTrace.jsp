<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String displayRequirementTraceIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayRequirementTraceIsLoggedIn == null) || (displayRequirementTraceIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	// This routine is always called with a requirementId parameter.
    int requirementId = Integer.parseInt(request.getParameter("requirementId"));
    Requirement requirement = new Requirement(requirementId, databaseType);
    Folder f = new Folder(requirement.getFolderId());
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	boolean readPermissions = true;
	if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
			+ requirement.getFolderId()))){
		readPermissions = false;
	}
	// you need to be a member of this project and have read permissions before you can see this.
	if (isMember && readPermissions){%>
		<% 

			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////
		
		    // The status attribute has the list of ReqTags that the user tried to traceto, but didn't exist in the system.
		    String status = (String) request.getAttribute("status");
		
		    // if this is a deleted req, then display the 'traceability not applicable to del reqs' 
		    // message.
			if (requirement.getDeleted() == 1){
		%>
				request.setAttribute("requirement",requirement);
			<div id = 'traceInfo' >
				<fieldset id="requirementTraces">
					<table class='paddedTable' align="center" >
						<tr>
							<td>Upon delete, all Traces to and from this Requirement are removed. 
							However the Traceability information at the time of delete is attached to the
							 'Requirement Description'.</td>
						</tr>				
					</table>
				</fieldset>
			</div> 
		
		
		<%
			}
			else{
				// if this is a non deleted req, then show the traceability section.
				
		    	// if the user does not have 'traceToRequirementsInFolder' priv for this c
		    	// we disable the Create Traces To  button and disable the pull downs
		    	// for trace actions.
		    	boolean canTraceToFromThisReq = true;
		    	if (!(securityProfile.getPrivileges().contains("traceToRequirementsInFolder" 
		    			+ requirement.getFolderId()))){
		    		canTraceToFromThisReq = false;
		    	}
		    	
		    	
		    	
			
		%>

		<div class="panel panel-info"> 	
			<div class="panel-heading " > 
				<%=requirement.getRequirementFullTag() %> Traceability 
				&nbsp;&nbsp;&nbsp;&nbsp;
				<img
														
					title=' Trace Map ' 
					src="/GloreeJava2/images/tree.gif"  style='cursor:pointer; height:30px' border="0"
				  onclick="window.open('/GloreeJava2/jsp/Requirement/CIA/traceMap.jsp?requirementId=<%=requirementId %>')">
													  
				
				&nbsp;&nbsp;&nbsp;&nbsp;
				
				<%if (canTraceToFromThisReq){%>
					<button type="button" class="btn btn-outline-info btn-xs"
					style="border-color:blue; color:blue; background-color:white;"
					 data-toggle="modal" data-target="#myModal"
						onclick='fillCreateTracesModal(<%=requirement.getRequirementId() %>, <%=requirement.getProjectId() %>, "Requirement");'
					>Create Traces</button>
				<%} %>
				&nbsp;&nbsp;
				<%
					String traceTo = requirement.getRequirementTraceTo();
					String traceFrom = requirement.getRequirementTraceFrom();
					if 
					(
							!(traceFrom.equals("")) 
							||
							!(traceTo.equals("")) 
					){			
				%>
				<div class="btn-group" style='margin-left:0px'>
					  
					  <button type="button" class="btn btn-outline-primary btn-xs" 
					  style="border-color:blue; color:blue; background-color:white;"
					 	>Trace (Connect) Actions</button>
					  <button type="button" class="btn btn-outline-primary btn-xs dropdown-toggle" 
					  style="border-color:blue; color:blue; background-color:white;"
					  data-toggle="dropdown" aria-expanded="false">
						<span class="caret"></span>
						<span class="sr-only">Toggle Dropdown</span>
					  </button>
					  <ul class="dropdown-menu" style='text-align:left;' role="menu">
						<%
						if (!canTraceToFromThisReq){
						%>
							<li>
									No Trace Permissions
							</li>
						<%
						}
						else {
						%>
						
								<li role="presentation" class="divider"></li>
							
										<%
										
											if (!(traceTo.equals(""))){
												if(traceTo.contains("(s)")){ %>
													<li>
													<a href='#' 
														onclick='modifyTracesInBulk(<%=requirement.getRequirementId()%>,"clearAllTracesTo");'
													> 
														Clear All Upstream Traces 
													</a>
													</li>
												<%} %>
												<%
												  	if (!(project.getProjectTags().toLowerCase().contains("school_project"))){
												%>
													<li>
													<a href='#' 
															onclick='modifyTracesInBulk(<%=requirement.getRequirementId()%>,"makeSuspectAllTracesTo");'
													> 
														Make Suspect All Upstream Traces  
													</a>
													</li>
												<%} %>
												<li>
												<a href='#' 
													onclick='modifyTracesInBulk(<%=requirement.getRequirementId()%>,"deleteAllTracesTo");'
												> 
													Delete All Upstream Traces  
												</a>
												</li>
												<li role="presentation" class="divider"></li>
												
											<%}
										
										
										
											if (!(traceFrom.equals(""))){
											%>
												<% if(traceFrom.contains("(s)")){ %>
													<li>
													<a href='#' 
														onclick='modifyTracesInBulk(<%=requirement.getRequirementId()%>,"clearAllTracesFrom");'
													> 
														Clear All Downstream Traces
													</a>
													</li>
												<%} %>
												<%
												  	if (!(project.getProjectTags().toLowerCase().contains("school_project"))){
												%>
													<li>
													<a href='#' 
															onclick='modifyTracesInBulk(<%=requirement.getRequirementId()%>,"makeSuspectAllTracesFrom");'
													> 
														Make Suspect All Downstream Traces 
													</a>
													</li>
												<%} %>
												<li>
												<a href='#' 
													onclick='modifyTracesInBulk(<%=requirement.getRequirementId()%>,"deleteAllTracesFrom");'
												> 
													Delete All Downstream Traces  
												</a>
												</li>
											<%}%>
																						  
									

							 </ul>
						<%}%>
					</div> 
				<%} %>

				
			</div>
		    <div id='quickTraceDiv' style='display:none;'></div>
			<div id = 'traceInfo' class="panel-body" >
				<form method="post" id="requirementTrace" action="">
					<fieldset id="requirementTraces">
						<table  class='paddedTable' align="left"  width='100%'>
							<tr> 
								<td>
									<div id = 'traceActions' class='level2Box'>
									<table  align='left' class='paddedTable' width='100%'>
										<% 
										// Display the list of invalid tags.
										if ((status != null ) && !(status.equals(""))) {
										%>
											<tr>
												<td colspan=2>
													<div class='alert alert-danger'>
														<span class='normalText'>    
														The following Requirements had problems. <%=status%> 
														</span>
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
							<%
							String reqTraceTo = requirement.getRequirementTraceTo();
							String reqTraceFrom = requirement.getRequirementTraceFrom();
							
								// we make the effort to display the traces only if there are some traces
								// otherwise we save the processing power and screen real estate.
							%>
							<tr> 
								<td>
									<div  id = 'traceDetails' class='level2Box'>
									<table   align='center'  class='table ' border='0'>
								    	<tr> 
											<td colspan='4' align='center' style=' border-top: none ' >
											
											
												<table border='0'   class='table'>
										<%
										ArrayList tracesTo = RequirementUtil.getTraceTo(requirementId);
								    	if (tracesTo != null){
									    	Iterator i = tracesTo.iterator();
								    		while ( i.hasNext() ) {
									    		String t = (String) i.next();
								    			String [] traceDetails = t.split(":#:");
								    			String traceId = traceDetails[0];
								    			String traceToId = traceDetails[1];
								    			String traceToFullTag = traceDetails[2];
								    			String suspectString = traceDetails[3];
								    			
								    			// we have a crazy scenario where an empty string after : is not picked up
									    		// so we have to see if we have 5 elements in the array, before referrign to it.
									    		String requirementName = "";
									    		if (traceDetails.length > 4){
									    			requirementName = traceDetails[4];
									    		}
									    		String traceDescription = "";
									    		if (traceDetails.length > 5){
								    				traceDescription = traceDetails[5];
								    			}
									    		if ((traceDescription != null) && (traceDescription.contains("\""))){
									    			traceDescription = traceDescription.replace("\"", "'");
									    		}
									    		String projectShortName = "";
								    			if (traceDetails.length > 6){
								    				projectShortName = traceDetails[6];
								    			}
								    			String traceReason = "";
								    			if (traceDetails.length > 7){
								    				traceReason = traceDetails[7];
								    			}

								    			if ( (traceReason == null) || (traceReason.equals("null"))){
								    				traceReason = "";
								    			}
								    			int suspect = Integer.parseInt(suspectString);
												
												String traceActionId = "traceAction" + traceId;
												String divId = "Div" + traceId;    	
												Requirement traceToRequirement = new Requirement( Integer.parseInt(traceToId), databaseType);
												// lets make sure we have read permissions on the traceto req.
												if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
														+ traceToRequirement.getFolderId()))){
													traceToRequirement.redact();
													traceDescription = "No READ permission";
												}
												
												String trClass = "success";
												if (suspect != 0) { 
													trClass = "danger";
												}
												
												// to reduce code size I am making some HTML text in to strings that I reuse.
												String noPermission = "<li><a href='#' > No Permission to Trace</a></li>";
									  	%>
									
								
												
								   					<tr >
									   					
															<%
																// if the req name has a " in it, Javascript tooltip is freaking out. so lets escape them
																String traceToRequirementName = traceToRequirement.getRequirementNameForHTML();
																String traceToRequirementShortName = traceToRequirementName;
																if ((traceToRequirementShortName!= null) && (traceToRequirementShortName.length() > 50)){
																	traceToRequirementShortName = traceToRequirementShortName.substring(0, 50) + "...";
																}
																if (traceToRequirementName.contains("\"")){
																	traceToRequirementName = traceToRequirementName.replace("\"","'");
																}
																
																// this req is in an external project. Curently tracecloud can not
																// support more than 1 project per browser.
																String url = ProjectUtil.getURL(request,traceToRequirement.getRequirementId() ,"requirement");
				    										%>
																<td align="left"  style="width:400px; border-top:none; " >
																
																	<ul  class="nav navbar-nav" 
																			title="Name : <%=traceToRequirementName %>" >
																	        <li class="dropdown">
																	          <a href="#" class="dropdown-toggle" data-toggle="dropdown"
																				style='color:black;'
																				onmouseover="this.style.border ='2px solid #85C1E9';this.style.width = '-moz-available'"
																				onmouseout="this.style.border = 'none'"
																				> 
																				 <%if (traceToRequirement.getProjectId()!= project.getProjectId()){ %>
																				<%=projectShortName%>:
																				<%}%>
																				<%=traceToFullTag%> : <%=traceToRequirementShortName %>
																				
																				</a>
																	          <ul class="dropdown-menu">
																	            
																	            <%if (traceToRequirement.getProjectId()== project.getProjectId()){ %>
																			           <li style='display:block'>
																		            	<a href="#"
																		            		onClick='
															 									displayFolderInExplorer(<%=traceToRequirement.getFolderId()%>);
															 									displayFolderContentCenterA(<%=traceToRequirement.getFolderId() %>);
															 									displayRequirement(<%=traceToId%>)
														 									'>
											 												Open <%=traceToFullTag%>
																		            	</a>
																		            </li>
																	            <%}
																	            else {%>
																		            <li style='display:block'>
																		            	<a href="#"
																		            		onClick='
																   							alert("Since this Requirement is in an external project please paste this URL" +
																	   						" in a different browser (IE, FireFox).          " +
																	   						"<%=url%>");
																	   						'>
											 												Open <%=traceToFullTag%>
																		            	</a>
																		            </li>
																	            <%} %>
																	            <%if (!canTraceToFromThisReq){ %> 
																				  		<%=noPermission %>
																				 <%}
																				 else{
																					 // user can do traceability
																					 if (suspect == 0){
																						%>
																						<li>
																							<a href='#' onclick="modifyTrace2('makeSuspect','<%=divId%>','<%=traceId%>',<%=requirementId%>)"> 
																								Mark Suspect
																							</a>
																						</li>
																						<%
																					 }
																					 else {
																					 %>
																						 <li>
																							<a href='#' onclick="modifyTrace2('clearSuspect','<%=divId%>','<%=traceId%>',<%=requirementId%>)"> 
																								Clear Suspect
																							</a>
																						</li>
																					 <%
																					 }
																					 %>
																					 	<li>
																							<a href='#' 	onclick="modifyTrace2('deleteTrace','<%=divId%>','<%=traceId%>',<%=requirementId%>)"> 
																								Delete Trace
																							</a>
																						</li>
																						
																						<%if ((traceReason.length() == 0) || (traceReason.equals("null"))){%>
																							<li>
																								<a href='#' 	
																									onclick="
																										document.getElementById('addEditReasonDiv<%=traceId %>').style.display='block';
																										document.getElementById('showReasonDiv<%=traceId %>').style.display='none';
																										
																										"> 
																									Add Reason
																								</a>
																							</li>
																						
																						<% 	
																						}
																						else {
																						%>
																							<li>
																								<a href='#' 	
																									onclick="
																										document.getElementById('addEditReasonDiv<%=traceId %>').style.display='block';
																										document.getElementById('showReasonDiv<%=traceId %>').style.display='none';
																										">  
																									Modify Reason
																								</a>
																							</li>
																						<%}
																					}
																				 %>
																	            
																	           </ul>
																	           
																			</li>
																		</ul>
																	
										   							
																</td>
															
								   							
									   				<% if (suspect == 0) { %>
									   						<td align="center" style='border-top:none;' >
									   							<img src="/GloreeJava2/images/cTrace2.jpg" border="0" title="<%=traceDescription%>">
									   						</td>
									   						
									   				<%} 
									   				else {
									   				%>
									   						<td align="center" style='border-top:none;'  >
									   							<img src="/GloreeJava2/images/sTrace2.jpg" border="0" title="<%=traceDescription%>">
									   						</td>
										   			<%
										   			} 
										   			%>
									   					
									   				</tr>
									    	
									    			<tr >
									    				<td colspan='3' style='border-top-style:none'>
									    					<div id='traceReasonDiv<%=traceId%>'>
									   							<%if (traceReason.length() > 0 ){ %>
										   							<div id='showReasonDiv<%=traceId%>' style='border:2px dotted red; border-radius:10px; padding:10px 10px 10px 10px; '>
										   								<%=traceReason %>
										   							</div>
									   							<%} %>
								   								<div id='addEditReasonDiv<%=traceId %>' style='display:none; border:2px dotted red; border-radius:10px; padding:10px 10px 10px 10px; '>
									   								<input type='text' id='addEditReasonTextBox<%=traceId %>' value='<%=traceReason %>'
									   									 placeholder='Reason for this Trace' style='width:300px'>
									   								&nbsp;&nbsp;
									   								<input type='button' class='btn btn-sm btn-primary' value='Go' onClick='updateTraceReason(<%=traceId%>, <%=requirementId%>);'>
									   							 </div>
									   						</div>
									    				</td>
									    			</tr>
									    	<%
									    	}
											if ((tracesTo.size()  == 0 ) && (f.canBeReportedOrphan() )) {
											%>
												<%
												  	if (!(project.getProjectTags().toLowerCase().contains("school_project"))){
												%>
												<tr>
													<td colspan='3' class='danger' align='center'>
														<%=requirement.getRequirementFullTag() %> is Orphan <br> (It Is <b>NOT</b>  connected to any upstream work)
													</td>
												</tr>
												<%} %>
											<%
											}
									    }
										
										%>
											</table>
											
											</td>
										</tr>
										
										<!---







										ABOVE this all upstream requirements 






										-->
										<tr>
											<td colspan='4' align="center" style='border-top:none' >
												<div class='alert alert-info' style='border: 2px dotted blue; border-radius: 10px; '>
												<span class='normalText'>												
													<%=requirement.getRequirementFullTag()%>
												</span>
												</div>
											</td>
											
										</tr>
										
										
										<!---







										BELOW this all downstream requirements 






										-->
										
										<tr> 
											<td colspan='4' align='center' style=' border-top: none ' >
											
												<table class='table '>
										<%
										ArrayList tracesFrom = RequirementUtil.getTraceFrom(requirementId);
									    if (tracesFrom != null){
									    	Iterator i = tracesFrom.iterator();
									    	while ( i.hasNext() ) {
									    		String t = (String) i.next();
									    		String [] traceDetails = t.split(":#:");
									    		String traceId = traceDetails[0];
									    		String traceFromId = traceDetails[1];
									    		String traceFromFullTag = traceDetails[2];
									    		String suspectString = traceDetails[3];
									    		
									    		// we have a crazy scenario where an empty string after : is not picked up
									    		// so we have to see if we have 5 elements in the array, before referrign to it.
									    		String requirementName = "";
									    		if (traceDetails.length > 4){
									    			requirementName = traceDetails[4];
									    		}
									    		String traceDescription= "";
									    		if (traceDetails.length > 5){
									    			traceDescription = traceDetails[5];
									    		}
									    		if ((traceDescription != null) && (traceDescription.contains("\""))){
									    			traceDescription = traceDescription.replace("\"", "'");
									    		}
								    			String projectShortName = "";
								    			if (traceDetails.length > 6){
								    				projectShortName = traceDetails[6];
								    			}
								    			String traceReason = "";
								    			if (traceDetails.length > 7){
								    				traceReason = traceDetails[7];
								    			}

								    			if ( (traceReason == null) || (traceReason.equals("null"))){
								    				traceReason = "";
								    			}

									    		int suspect = Integer.parseInt(suspectString);
									    		
												String traceActionId = "traceAction" + traceId;
												String divId = "Div" + traceId;    		
												
												Requirement traceFromRequirement = new Requirement(Integer.parseInt(traceFromId), databaseType);
												// lets make sure we have read permissions on the traceto req.
												if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
														+ traceFromRequirement.getFolderId()))){
													traceFromRequirement.redact();
													traceDescription = "No READ permissions";
												}
												String trClass = "success";
												if (suspect != 0) { 
													trClass = "danger";
												}
												

												// to reduce code size I am making some HTML text in to strings that I reuse.
												String noPermission = "<li><a href='#' > No Permission To Trace</a></li>";
									    %>
									   			<tr >
									   		
										   		<% 
										   			// if the req name has a " in it, Javascript tooltip is freaking out. so lets escape them
													String traceFromRequirementName = traceFromRequirement.getRequirementNameForHTML();
											   		String traceFromRequirementShortName = traceFromRequirementName;
													if ((traceFromRequirementShortName!= null) && (traceFromRequirementShortName.length() > 50)){
														traceFromRequirementShortName = traceFromRequirementShortName.substring(0, 50) + "...";
													}
												
													if (traceFromRequirementName.contains("\"")){
														traceFromRequirementName = traceFromRequirementName.replace("\"","'");
													}
										   			
													
												%>
												<%
													if (suspect == 0) {
										   		%>
									   				<td align="center"  style='border-top:none;' >
									   					<img src="/GloreeJava2/images/cTrace1.jpg" border="0" title="<%=traceDescription%>" >
									   				</td>
													
												<%}
												else {%>
													<td align="center"  style='border-top:none;' >
									   					<img src="/GloreeJava2/images/sTrace1.jpg" border="0" title="<%=traceDescription%>">
									   				</td>
												<%} %>	
													
													<%////////////////srt %>
									   				<td align="left"  style="width:400px; border-top:none;" >
									   				<ul  class="nav navbar-nav" 
														title="Name : <%=traceFromRequirementName %>" >
												        <li class="dropdown">
												          <a href="#" class="dropdown-toggle" data-toggle="dropdown"
															style='color:black;'
															onmouseover="this.style.border ='2px solid #85C1E9';this.style.width = '-moz-available'"
															onmouseout="this.style.border = 'none'"
															>
															 <%if (traceFromRequirement.getProjectId()!= project.getProjectId()){ %>
																<%=projectShortName%>:
															<%}%>
															<%=traceFromFullTag%> : <%=traceFromRequirementShortName %></a>
												          <ul class="dropdown-menu">
												            
												            <%if (traceFromRequirement.getProjectId()== project.getProjectId()){ %>
												            
												            
														           <li style='display:block'>
													            	<a href="#"
													            		onClick='
										 									displayFolderInExplorer(<%=traceFromRequirement.getFolderId()%>);
										 									displayFolderContentCenterA(<%=traceFromRequirement.getFolderId() %>);
										 									displayRequirement(<%=traceFromId%>)
									 									'>
						 												Open <%=traceFromFullTag%>
													            	</a>
													            	
													            </li>
												            <%}
												            else {
												            	String url = ProjectUtil.getURL(request,traceFromRequirement.getRequirementId() ,"requirement");
					    									%>
													            <li style='display:block'>
													            	<a href="#"
													            		onClick='
											   							alert("Since this Requirement is in an external project please paste this URL" +
												   						" in a different browser (IE, FireFox).          " +
												   						"<%=url%>");
												   						'>
						 												Open <%=traceFromFullTag%>
													            	</a>
													            	
													            	
													            </li>
												            <%} %>
												            <%if (!canTraceToFromThisReq){ %> 
															  		<%=noPermission %>
															 <%}
															 else{
																 // user can do traceability
																 if (suspect == 0){
																	%>
																	<li>
																		<a href='#' onclick="modifyTrace2('makeSuspect','<%=divId%>','<%=traceId%>',<%=requirementId%>)"> 
																			Mark Suspect
																		</a>
																	</li>
																	<%
																 }
																 else {
																 %>
																	 <li>
																		<a href='#' onclick="modifyTrace2('clearSuspect','<%=divId%>','<%=traceId%>',<%=requirementId%>)"> 
																			Clear Suspect
																		</a>
																	</li>
																 <%
																 }
																 %>
																 	<li>
																		<a href='#' 	onclick="modifyTrace2('deleteTrace','<%=divId%>','<%=traceId%>',<%=requirementId%>)"> 
																			Delete Trace
																		</a>
																	</li>
																	
																	<%if ((traceReason.length() == 0) || (traceReason.equals("null"))){%>
																		<li>
																			<a href='#' 	
																				onclick="
																					document.getElementById('addEditReasonDiv<%=traceId %>').style.display='block';
																					document.getElementById('showReasonDiv<%=traceId %>').style.display='none';
																					
																					"> 
																				Add Reason
																			</a>
																		</li>
																	
																	<% 	
																	}
																	else {
																	%>
																		<li>
																			<a href='#' 	
																				onclick="
																					document.getElementById('addEditReasonDiv<%=traceId %>').style.display='block';
																					document.getElementById('showReasonDiv<%=traceId %>').style.display='none';
																					">  
																				Modify Reason
																			</a>
																		</li>
																	<%}
																}
															 %>
												            
												           </ul>
												           
														</li>
													</ul>
													</td>
									   				<%/////////////////srt  %>
													
									   				
									   			</tr>
									    	
									    		<tr >
									    				<td colspan='3' style='border-top-style:none'>
									    					<div id='traceReasonDiv<%=traceId%>'>
									   							<%if (traceReason.length() > 0 ){ %>
										   							<div id='showReasonDiv<%=traceId%>' style='border:2px dotted red; border-radius:10px; padding:10px 10px 10px 10px; '>
										   								<%=traceReason %>
										   							</div>
									   							<%} %>
								   								<div id='addEditReasonDiv<%=traceId %>' style='display:none; border:2px dotted red; border-radius:10px; padding:10px 10px 10px 10px; '>
									   								<input type='text' id='addEditReasonTextBox<%=traceId %>' value='<%=traceReason %>'
									   								 placeholder='Reason for this Trace' style='width:300px'>
									   								 &nbsp;&nbsp;
									   								<input type='button' class='btn btn-sm btn-primary' value='Go' onClick='updateTraceReason(<%=traceId%>, <%=requirementId%>);'>
									   							 </div>
									   						</div>
									    				</td>
									    			</tr>
									    	
									    	<%
									    	}
									    }
										%>
												
												<%if ( (tracesFrom.size()  == 0 ) && (f.canBeReportedDangling())) {
													%>
													<%
													  	if (!(project.getProjectTags().toLowerCase().contains("school_project"))){
													%>
													<tr>
														<td colspan='3' class='danger' align='center'>
															<%=requirement.getRequirementFullTag() %> is Dangling <br> (It Is <b>NOT</b> connected to any Downstream work)
														</td>
													</tr>
													<%} %>
												<%
												} %>
												</table>
											
											</td>
										</tr>
										
										
										</table>
										</div>
									</td>
								</tr>
											
							</table>
					</fieldset>
				</form>
			</div>
		</div>	
	<%}%>
<%}%>