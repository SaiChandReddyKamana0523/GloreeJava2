<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>
<%@ page import="java.text.DateFormat"%>
<%@ page import="java.text.SimpleDateFormat"%>



<%
	// authentication only
	String displayRequirementCoreIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayRequirementCoreIsLoggedIn == null) || (displayRequirementCoreIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
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
	 
	User user = securityProfile.getUser();
	if (isMember){
%>

	<% 
	
		String ua = request.getHeader( "User-Agent" );
		boolean isMSIE = false;
		if (
			( ua != null && ua.indexOf( "MSIE" ) != -1 )
			||
			( ua != null && ua.indexOf( "Trident" ) != -1 )
			){
			isMSIE = true;
		}
			
																
																
		int calledFromFolderId = 0;
		try {
			calledFromFolderId = Integer.parseInt(request.getParameter("calledFromFolderId"));
		}
		catch (Exception e) {
			calledFromFolderId = 0;
		}
		String calledFrom = request.getParameter("calledFrom");
		if (calledFrom == null){
			calledFrom = "";
		}
	
		Requirement requirement = (Requirement) request.getAttribute("requirement");
		
		
		// if the requirement object exists, it means a call was made to RequirementAction with a request to create a requirement.
		// the requirement object now contains the data for the newly created requirement.
	    if (requirement == null) {
	    	// This means that no new requirements were created prior to this call.
	    	String requirementIdString = request.getParameter("requirementId");
	    	int requirementId = Integer.parseInt(requirementIdString);
	    	requirement = new Requirement(requirementId, databaseType);	
	    }

	 	// if the user does not have read permissions on the folder where this req resides, lets
		// redact it.
		if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
				+ requirement.getFolderId()))){
			requirement.redact();
		}
		
		///////////////////////////////SECURITY CODE ////////////////////////////
		// if the requirement worked on, doesn't belong to the project the user is 
		// currently logged into, then a user logged into project x is trying to 
		// hack into a req in project y by useing requirementId parameter.
		if (requirement.getProjectId() != project.getProjectId()) {
			return;
		}
		///////////////////////////////SECURITY CODE ////////////////////////////
		
		// lets also get the requirement type object for this requirement
		RequirementType requirementType = new RequirementType(requirement.getRequirementTypeId());
		// if this req just got restored, we want to give a success message.
		String restoredMessage = "";
		String restored = (String) request.getAttribute("restored");
		
		
		// if this is a deleted req, forward to the displayRequirementCoreDel.jsp
		if (requirement.getDeleted() == 1){
			request.setAttribute("requirement",requirement);
	%>
	<jsp:forward page="/jsp/Requirement/displayRequirementCoreDel.jsp"/>
	<%
		}
	%>
	
	<%
	// if the user does not have 'Update Requirements' priv for this folder
	// we disable both the Edit Requirements and Move Requirements buttons.
	// this is because, we don't want the user to move the req to a different folder
	// and start messing around with it there.
	
	// Delete and Purge buttons are controlled by the 'Delete Requirement' privilege.
	boolean deleteDisabled = false;
	if (!(securityProfile.getPrivileges().contains("deleteRequirementsInFolder" 
			+ requirement.getFolderId()))){
		deleteDisabled = true;
	}
	
	boolean createDisabled = false;
	if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" 
			+ requirement.getFolderId()))){
		createDisabled = true;
	}
	
	
	boolean updateDisabled = false ;
	if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
			+ requirement.getFolderId()))){
	
		updateDisabled = true;
	}
	
	// even if a user has update permissions on a folder,  we still check to see
	//  if this requirement is locked by some other user, then we need to set update , delete and purge to Disabled.
	if (
		(!(requirement.getRequirementLockedBy().equals(""))
		&&
		(!(requirement.getRequirementLockedBy().equals(user.getEmailId()))))
	){
		// this req is locked and its locked by someone other that the person currently logged in. hence disabled button is off.
		updateDisabled = true;
		deleteDisabled = true;
	}

	 
	String disabledString = "";
	if (updateDisabled) {	
		disabledString = "DISABLED=DISABLED";
	}

	
	String addedToBaselineMessage = "";
	String addedToBaseline = (String) request.getAttribute("addedToBaseline");
	if ((addedToBaseline != null) && (addedToBaseline.equals("true"))){
		addedToBaselineMessage = " " +
		"<div id='baselineMessageDiv' class= 'userSuccessAlert'> " + 
		"	<div  style='float: right;'> " +  
		"		<a href='#' onclick= \" " +
		"	 	document.getElementById('baselineMessageDiv').style.display = 'none'; \"> " +
		" 		Close </a> " + 
		"	</div> " +
		"	Congratulations. " + 
		"		 This Requirement has been added to the baseline. " + 
		"</div>";
	}
 
	String requirementBaselineLockedMessage = "";
	String requirementBaselineLocked = (String) request.getAttribute("requirementBaselineLocked");
	if ((requirementBaselineLocked != null) && (requirementBaselineLocked.equals("true"))){
		requirementBaselineLockedMessage = "" + 
			" <div id='requirementBaselineLockedDiv' class='alert alert-success'> " +
			" 	<div style='float:right'> " +
			"	<a href='#' onclick='document.getElementById(\"requirementBaselineLockedDiv\").style.display = \"none\";'> Close </a> " + 
			" 	</div> " +
			"	<br>This Baseline is locked. Please work with your Project Administrator to unlock it.  " +
			" </div>";
	}
	
	
	
	
	String requirementBaselineAlreadyExistsMessage = "";
	String requirementBaselineAlreadyExists = (String) request.getAttribute("requirementBaselineAlreadyExists");
	if ((requirementBaselineAlreadyExists != null) && (requirementBaselineAlreadyExists.equals("true"))){
		requirementBaselineAlreadyExistsMessage = "" + 
			" <div id='requirementBaselineAlreadyExistsDiv' class='alert alert-success'> " +
			" 	<div style='float:right'> " +
			"	<a href='#' onclick='document.getElementById(\"requirementBaselineAlreadyExistsDiv\").style.display = \"none\";'> Close </a> " + 
			" 	</div> " +
			"	<br>This Requirement is already a member of this baseline. If you would like to add the latest" +
			" Version of this Requirement to this baseline, please remove it from this Baseline, and then Re-Add it " +
			" </div>";
	}

	String removedFromBaselineMessage = "";
	String removedFromBaseline = (String) request.getAttribute("removedFromBaseline");
	if ((removedFromBaseline != null) && (removedFromBaseline.equals("true"))){
		removedFromBaselineMessage = " " +
		"<div id='baselineMessageDiv' class= 'userSuccessAlert'> " + 
		"	<div  style='float: right;'> " +  
		"		<a href='#' onclick= \" " +
		"	 	document.getElementById('baselineMessageDiv').style.display = 'none'; \"> " +
		" 		Close </a> " + 
		"	</div> " +
		"	Congratulations. " + 
		"		 This Requirement has been removed from the baseline. " + 
		"</div>";
	}

	
	if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
			+ requirement.getFolderId()))){
	%>
		<div class='level1Box' STYLE="background-color:white">
			<div class='alertPrompt'>
			<span class='normalText'>
			You do not have READ permissions on this folder.
			</span>
			</div>
		</div>
	<%}
	else {%>
	
<%
 			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
 			String url = ProjectUtil.getURL(request,requirementId,"requirement");
 			%>
 			
	<div class="panel panel-info"> 	
		<div class="panel-heading " style='text-align:center'> 
		<table class='able' width='100%'>
			<tr>
			<td align='left'  width='150px'>	
				<span class='subSectionHeadingText'>
				
				<%if (!(requirement.getRequirementLockedBy().equals(""))){
					// this requirement is locked. so lets display a lock icon.
				%>
					<span class='normalText' title='Requirement locked by <%=requirement.getRequirementLockedBy()%>'> 
       					<img src="/GloreeJava2/images/lock16.png" border="0"> 
       				</span>	
				<%
				}
				%>
				
				<span class='normalText'><%=requirement.getRequirementFullTag() %></span>
				<span class='normalText' title='Display Version History'>
					<a href='#' 
					onclick='displayRequirementVersionHistory(<%=requirement.getRequirementId()%>);'
					style='color:black;'
					>(V-<%=requirement.getVersion()%>)</a>
				</span>
			</span>
			</td>
			<td>
				&nbsp;&nbsp;&nbsp;
				<div class="btn-group ">
						
						  <button type="button" class="btn btn-info btn-sm dropdown-toggle" data-toggle="dropdown">
						   <span class="glyphicon glyphicon-cog " style=" color: white"></span> 
						  </button>
								  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								  <ul class="dropdown-menu" role="menu" style="text-align:left;">
								  
								 
								    <li style='display:block'><a href="#" onclick="displayChangeImpactAnalysis(<%=requirement.getRequirementId() %>);">Change Impact Analysis</a></li>
								   	
								   	<!-- 
								   	<li style='display:block'>
								   		<a href="#" 
								   			onclick='
								   				displayRequirementComments("<%=request.getParameter("requirementId")%>","<%=requirement.getFolderId()%>")
								   				'
								   		> <%=requirement.getRequirementComments(databaseType).size()%>
											<img src="/GloreeJava2/images/comments16.png" border="0">&nbsp; Add A Comment</a></li>
											
										-->	
								    <li class="divider"></li>
									<% if (updateDisabled){ %>
									<%}
									else {%>
										<li style='display:block'>
											<a 
												href="#" 
												title='Move this Requirement to another Folder in this Project'  
												onclick='moveRequirementForm("<%=request.getParameter("requirementId")%>","<%=requirement.getFolderId()%>");'
												><img width=16 src="/GloreeJava2/images/move_to_folder.png" border="0">&nbsp; Move to another folder</a></li>
										
									<%} %>	

								    
								    <li style='display:block'>
								    	<a 
								    		href="#" 
								    		title='Copy this requirement to another location'
								    		onclick='copyRequirementForm("<%=request.getParameter("requirementId")%>")'
								    	><img src="/GloreeJava2/images/copy.png" border="0">&nbsp; Make a Copy</a></li>

									<% if (deleteDisabled){ %>
									<%}
									else {%>
											<li style='display:block'>
											<a href='#' 
												title='Delete this Requirement (Can be Restored)'
												onclick='deleteRequirementForm("<%=request.getParameter("requirementId")%>","<%=requirement.getFolderId()%>")'
											> <img src="/GloreeJava2/images/delete16.png" border="0">&nbsp; Delete (Can be restored) </a>
											</li>
											<%
											if (securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
											%>
												<li style='display:block'>
												<a href='#' 
													title='Purge this Requirement (Can NOT be Restored)'
													onclick='purgeRequirementForm2("<%=request.getParameter("requirementId")%>","<%=requirement.getFolderId()%>")'>  
													<img width=16 src="/GloreeJava2/images/fire.png" border="0">&nbsp; Purge (Can NOT be restored) </a>
												</li>
											<%} %>
											
									<%} %>									
									
									<!-- 
									<% if (updateDisabled){ %>
									<%}
									else {%>
										<li style='display:block'>
										<a 
											href='#' 
											title='Attach a file to this Requirement'
											onclick='addRequirementAttachmentForm("<%=request.getParameter("requirementId")%>","<%=requirement.getFolderId()%>");'>
										 <img src="/GloreeJava2/images/attach16.png" border="0">&nbsp; Attach a file
										 </a>
										 </li>
									
									<%} %>
									-->
									
									 <li class="divider"></li>
									<li style='display:block'>
									 	<a 
											href='#' 
											title='Audit log for this Requirement'
											onclick='displayRequirementChangeLog("<%=request.getParameter("requirementId")%>","<%=requirement.getFolderId()%>")'>
										  <img src="/GloreeJava2/images/hourglass.png" border="0">&nbsp; Change Log 
										  </a>
									</li>
									<li class="divider"></li>
									<li style='display:block'>
										<a href='/GloreeJava2/jsp/Requirement/displayPrintableRequirement.jsp?requirementId=<%=requirement.getRequirementId()%>' 
											target="_blank"
											title="A Printable Copy of the Requirement and all its collateral"  
											onclick='document.getElementById("requirementActionDiv").style.display="none"';>
											 <img src="/GloreeJava2/images/printer.png" border="0">&nbsp; Make a Printable page	</a>
									</li>
									<li style='display:block'>
										<a href='#' 
											title='Generate Word Template Report' 
											onClick='
									       generateReqTemplateReport(<%=requirement.getRequirementId()%>, "requirementPromptDiv")
									       '
										  >
										<img src="/GloreeJava2/images/ExportWord16.gif"  border="0">&nbsp;Word Template Report</a>
								    </li>		
											
											
											
											
																		
									<li style='display:block'>
										<a href='#'
											title='URL to this Requirement'  
											onclick='
												displayURL("<%=url%>")
											'>
											<img src="/GloreeJava2/images/link.png" border="0">&nbsp; URL to this object 
										</a> 
									</li>
								    <li style='display:block'>
										<a href='#' 
												title='Email a link to this Requirement' 
												onclick='
												displayEmailRequirementDiv(<%=requirement.getRequirementId() %>)
											'>
										<img src="/GloreeJava2/images/email16.png"  border="0">&nbsp; Send this in Email</a>
								    </li>
								    <li class="divider"></li>
							
								<%if (updateDisabled){
	        						// the user does not have permissions to edit this req. so show grayed out.
	        						if (requirement.getRequirementLockedBy().equals("")){
										// the user does not have update permission and the req is not locked. The user can do nothing. ie not take a lock.
		        						%>
										<%
	        						}
	        						else {
										// the req is locked and the user does not have update permissions. 
										// the user is an admin, show the unlock option.
									
	        							// if the user is an admin, lets show the unlock button.
	        							if (securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
	        								%>
												<li style='display:block'>
												<a href ='#'
													title='Unlock this Requirements' 
													onclick='
														displayLockUnlockForm("<%=requirement.getRequirementId() %>","<%=requirement.getFolderId()%>");
														document.getElementById("requirementActionDiv").style.display="none";
													'> 
						        					 <img src="/GloreeJava2/images/lockUnlock16.png" border="0">&nbsp; Unlock</a>
						        				</li>
						        			 <%	
	        							}
	        						}
	        					}	
								else{
									if (requirement.getRequirementLockedBy().equals("")){
			        					// this is an unlocked requirement and the user has update permissions. so we can show the LOCK icon
			        					%>
												<li style='display:block'>
												<a href ='#' 
													title='Lock this Requirement' 
													onclick='
														displayLockUnlockForm("<%=requirement.getRequirementId() %>","<%=requirement.getFolderId()%>");
														document.getElementById("requirementActionDiv").style.display="none";
													'> 
													 <img src="/GloreeJava2/images/lock16.png" border="0">&nbsp; Lock </a>
												</li>
											
					        			<%
			        					
			        				}
									else {
		        							// Locked requirement. Lets show the unlock key and pass on the next screen. 
		        							%>
												<li style='display:block'>
												<a href ='#'
													title='Unlock this Requirement'  
													onclick='
														displayLockUnlockForm("<%=requirement.getRequirementId() %>","<%=requirement.getFolderId()%>")
														document.getElementById("requirementActionDiv").style.display="none";
														'> 
						        					  <img src="/GloreeJava2/images/lockUnlock16.png" border="0">&nbsp; Unlock </a>
						        				</li>
						        				
					        				 <%
		        						}
		        				}
							 %>				


  							

								
								<% if (updateDisabled){ %>
								<%}
								else {%>
									<li style='display:block'>
									<a href ='#'
										title='Add Requirement to a Baseline'  
										onclick='
											addToBaselineForm("<%=request.getParameter("requirementId")%>","<%=requirement.getFolderId()%>")
											document.getElementById("requirementActionDiv").style.display="none";
											'>
									 <img src="/GloreeJava2/images/baseline16.png" border="0">&nbsp; Add to a Baseline </a>
									</li>
								
								<%} %>	
								

								<li class="divider"></li>
								
								
								
																							
								<% if (createDisabled){}
								else {%>
										<li style='display:block'>
											<a href= '#'
												title='Create a Child Object'
												onClick='
														displayCreateChildForm(<%=requirement.getFolderId() %>,"<%=requirement.getRequirementFullTag()%>");
														document.getElementById("requirementActionDiv").style.display="none";
													'>
											 <img src="/GloreeJava2/images/child3.jpg" style='height:25px; width:25px' border="0">&nbsp; Create a Child 
											</a>
										</li>
										
										<li style='display:block'>
											<a href ='#'
												title='Create a Sibling Object'  
												onClick='
														displayCreateChildForm(<%=requirement.getFolderId() %>,"<%=requirement.getParentFullTag()%>")
														document.getElementById("requirementActionDiv").style.display="none";
													'>
											
											 <img src="/GloreeJava2/images/child3.jpg" style='height:15px; width:15px' border="0"> 
											 <img src="/GloreeJava2/images/child3.jpg" style='height:15px; width:15px' border="0">&nbsp; Create a Sibling</a>
										</li>
										
										
								<%} %>						
								
								
								<% if (updateDisabled){ %>
								<%}
								else {%>
										<li style='display:block'>
										<a href ='#' 
											title='Parent / Child Requirements' 
											onclick='
												displayParentingForm("<%=request.getParameter("requirementId")%>","<%=requirement.getFolderId()%>")
												document.getElementById("requirementActionDiv").style.display="none";	
											'>
										 <img src="/GloreeJava2/images/organization16.png" border="0">&nbsp; Parent / Child  </a>
										</li>
									
								<%} %>						
								
								
								  
								  </ul>
								</div>
				&nbsp;&nbsp;&nbsp;
			</td>
			<td>
			
				<%
				if (user.getReqViewPreference().equals("extended")){
					// show the compact option
					String compactURL = url + "?reqViewPreference=compact";
					// SRT TODO : we will do the extended / contact view option change here.
					%>
					
					<%
				}
				else {
					// show the extended option
					String compactURL = url + "?reqViewPreference=extended";
					%>
					
					<%
				}
				%>
				
				
				      <a  href='#' title='Previous' class="btn btn-info btn-xs" style='color:white'
				      	onclick='displayPreviousRequirementInFolder(<%=requirement.getFolderId()%> , <%=requirement.getRequirementId() %>)' 
				      >
				       <<<
				      </a>
				      &nbsp;&nbsp;&nbsp;
				    
				  	  
				      <a  href='#' title='Return to folder' class='btn btn-info btn-xs'
					     onclick='
								$("#folderTab").trigger("click");
								document.getElementById("contentCenterB").style.display="none";
								document.getElementById("contentCenterC").style.display="none";
								document.getElementById("contentCenterD").style.display="none";
								document.getElementById("contentCenterE").style.display="none";
								document.getElementById("contentCenterComments").style.display="none";
								document.getElementById("contentCenterAttachments").style.display="none";
								document.getElementById("contentCenterF").style.display="block";
								document.getElementById("reqTabs").style.display = "none";
								
								
							' 
							>
				        <img src="/GloreeJava2/images/folder.png">
				      </a>&nbsp;&nbsp;&nbsp;
				   
				   	
				      <a  href='#' title='Next' class="btn btn-info btn-xs" style='color:white'
				     	 onclick='displayNextRequirementInFolder(<%=requirement.getFolderId()%> , <%=requirement.getRequirementId() %>)'
				      >
				       >>>
				      </a>
				     
				     
				    
				   
				
			</td>
			<td align='center' >
				<span class='subSectionHeadingText'>
				<%
				if (project.getIntegrationMenus().size() > 0){
				%>
					<select id='integrationMenu'
					onChange='

						var integrationMenuObject = document.getElementById("integrationMenu");
						var integrationMenuValue = integrationMenuObject[integrationMenuObject.selectedIndex].value
						if (integrationMenuValue.length > 0){
							window.open (integrationMenuValue);
						}'>
						<option value=''>Menu</option>
					<% 
					Iterator im = project.getIntegrationMenus().iterator();
					while (im.hasNext()){
						IntegrationMenu menu = (IntegrationMenu) im.next();
						String menuURL = menu.getMenuValue();
						if (!menuURL.trim().startsWith("http://")){
							menuURL = "http://" + menuURL;
						}
						if (!menuURL.trim().contains("?")){
						 	menuURL = menuURL + "?";
						}
						menuURL = menuURL + "&requirementId=" + requirement.getRequirementId();
						menuURL = menuURL + "&folderId=" + requirement.getFolderId();
						menuURL = menuURL + "&projectId=" + requirement.getProjectId();
						menuURL = menuURL + "&projectShortName=" + project.getShortName();
						menuURL = menuURL + "&user=" + user.getEmailId();
						
						menuURL = menuURL + "&requirementFullTag=" + requirement.getRequirementFullTag();
						menuURL = menuURL + "&requirementOwner=" + requirement.getRequirementOwner();
						
						%>
						<option value='<%=menuURL%>'><%=menu.getMenuLabel() %></option>
						<%
					}
					
					%>
					</select>
				<%
				}
				%>
				</span>
			</td>
			<td align='right' >	
				<div>
				
					<div id='requirementPercentCompleteDiv' style='float:left'>
						
						<% 
						String titleString = "";
						boolean percentageCompleteDisabled = false;
						int percentageCompletedDriverReqTypeId = project.getPercentageCompletedDriverReqTypeId();
						if (
							(percentageCompletedDriverReqTypeId > 0 ) 
							&&
							(project.getPercentageCompletedDriverReqTypeId() != requirement.getRequirementTypeId())
						){
							// If this project has a percentageCompletedDriverReqTypeId value set (>0) 
							// and this requirement does not belong to the percentage complete driver
							// the update should be disabled. 
							percentageCompleteDisabled = true;
							titleString = "This Requirements percentage complete is system generated, based on the completion of Requirements that trace to it.";
						}
						
						if (updateDisabled || percentageCompleteDisabled ) {
							%>
							<span title='<%=titleString %>' class='subSectionHeadingText'>
								&nbsp;&nbsp;&nbsp;<%=requirement.getRequirementPctComplete()%>
								% Completed	
							</span>
							<%
						}
						else {
						%>
							<span class='subSectionHeadingText' title='<%=titleString%>'>
								<input  type='text'  name='requirementPercentComplete' id='requirementPercentComplete' style='width:25px' size='3'  
									value='<%=requirement.getRequirementPctComplete()%>'
									onChange='
										var percentCompleteObject = document.getElementById("requirementPercentComplete")
										 if(
										 	( percentCompleteObject.value == null) || 
										 	(isNaN(percentCompleteObject.value)) ||
										 	(percentCompleteObject.value < 0) ||
										 	(percentCompleteObject.value > 100 )
										 	){
											alert ("Please enter a valid Number between 0 and 100 for percent complete");
											percentCompleteObject.focus();
											percentCompleteObject.style.backgroundColor="#FFCC99";
											return;
										}
										setRequirementPercentComplete(<%=requirement.getRequirementId() %>)
									'></input>
									% Completed	
								</span>
							<%}  %>
							
					</div>
					<span class='subSectionHeadingText'>
				 
				<%
				// if this project has 'Defects' requirement type , then we want to 
				//give the users the option to create a defect when they fail a Test Result
				boolean defectsExists = false;
				ArrayList requirementTypes = project.getMyRequirementTypes();
				Iterator rt = requirementTypes.iterator();
				while (rt.hasNext()){
					RequirementType tempRT = (RequirementType) rt.next();
					if (tempRT.getRequirementTypeName().equals("Defects")){
						defectsExists = true;
					}
				}
				%>								
				<% if (requirement.getRequirementTypeName().equals("Test Results")){ %>
					<%if (requirement.getTestingStatus().equals("Pending")){ %>									
						Test: 
						<select name='manualTestingStatus' id='manualTestingStatus'
							ONCHANGE='
							<%if (defectsExists){ 
								// this project has defects req type and this tr is chaing to fail
								// so lets display the create Defects option
							%>
								var manualTestingStatusObject = document.getElementById("manualTestingStatus");
								if ( manualTestingStatusObject.selectedIndex  == 1){
									// a pending tr is being set to pass.
									updateRequirementTestingStatus(<%=requirement.getRequirementId()%>,"no");
								}
								if ( manualTestingStatusObject.selectedIndex  == 2){
									// a pending tr is being set to fail.
									document.getElementById("createDefectDiv").style.display="block";
								}												
							<%
							}
							else {
								// this project does not have defects req type. So lets 
								// go and update the test status.
							%>
								updateRequirementTestingStatus(<%=requirement.getRequirementId()%>,"no");
							<%} %>
							' >
							<option value='Pending' SELECTED> Pending </option>
							<option value='Pass'> Pass </option>
							<option value='Fail'> Fail </option>	
						</select>
						
					<%} %> 
					<%if (requirement.getTestingStatus().equals("Pass")){ %>
						Test: 
						<select name='manualTestingStaatus' id='manualTestingStatus' 
							ONCHANGE='
							<%if (defectsExists){ 
								// this project has defects req type and this tr is chaing to fail
								// so lets display the create Defects option
							%>
								var manualTestingStatusObject = document.getElementById("manualTestingStatus");
								if ( manualTestingStatusObject.selectedIndex  == 0){
									// a passed tr is being set to pending.
									updateRequirementTestingStatus(<%=requirement.getRequirementId()%>,"no");
								}
								if ( manualTestingStatusObject.selectedIndex  == 2){
									// a passed tr is being set to fail.
									document.getElementById("createDefectDiv").style.display="block";
								}
							<%
							}
							else {
								// this project does not have defects req type. So lets 
								// go and update the test status.
							%>
								updateRequirementTestingStatus(<%=requirement.getRequirementId()%>,"no");
							<%} %>
							' >
							<option value='Pending'> Pending </option>
							<option value='Pass' SELECTED> Pass </option>
							<option value='Fail'> Fail </option>												
						</select>
						
					<%} %> 
					<%if (requirement.getTestingStatus().equals("Fail")){ %>
						Test : 
						<select name='manualTestingStatus' id='manualTestingStatus'
							ONCHANGE='updateRequirementTestingStatus(<%=requirement.getRequirementId()%>,"no");'>
							<option value='Pending'> Pending </option>
							<option value='Pass'> Pass </option>
							<option value='Fail' SELECTED> Fail </option>	
						</select>
						
					<%} %>
								
				<%}
				else {%>
					<%if (requirement.getTestingStatus().equals("Pending")){ %>
						Test : Pending
					<%} %> 
					<%if (requirement.getTestingStatus().equals("Pass")){ %>
						Test : Pass
					<%} %> 
					<%if (requirement.getTestingStatus().equals("Fail")){ %>
						Test : Fail
					<%} %> 
				<%} %>
				</span>
				
				</div>
			</td>
			</tr>
		</table>
												
			
			
			
		</div>
	<div id='requirementInfoDiv' class="panel-body">			
			<fieldset id="requirementCore">
				<table  width="100%" align="left" style="font-size:8pt;" >
					
					
					<tr>
						<td>
							<div id='createDefectDiv' class='alert alert-success' style='display:none;'>
								<span class='normalText'>
								Would you like to create a Defect for this failed Test Result?
								<br>
								<input type='button' name='yesDefect' style='height:25px; width:100px' id='yesDefect' value='   Yes   '
								onclick='updateRequirementTestingStatus(<%=requirement.getRequirementId()%>,"yes");';></input>
								&nbsp;&nbsp;
								<input type='button' name='noDefect' style='height:25px; width:100px' id='noDefect' value='   No   '
								onclick='updateRequirementTestingStatus(<%=requirement.getRequirementId()%>,"no");';></input>
								</span>
							</div>
						</td>
					</tr>
				
					<%
					// we get the session variable to display the error / success message of addRequirementOperation in display req core
					String attachRequirementStatus = (String) session.getAttribute("attachRequirementStatus");
					session.removeAttribute("attachRequirementStatus");
					if (attachRequirementStatus != null) {
						if (attachRequirementStatus.equals("success")){
						%>
							<tr>
								<td colspan='4' >
								<div id='attachmentMessageDiv' class='alert alert-success'>
									<div style='float:right'>
										<a href='#' onclick='document.getElementById("attachmentMessageDiv").style.display="none";'>
										Close
										</a>
									</div>
									<br>
									Congratulations. Your File has been successfully attached to this Requirement.								
								</div>
								</td>
							</tr>
						<%
						}						
					}
					%>
				
					<%if (restored != null){%>
					<tr>
						<td colspan='4' >
						<div id='restoredReqDiv' class='alert alert-success'>
							<div style='float:right'>
								<a href='#' onclick='document.getElementById("restoredReqDiv").style.display="none";'>
								Close
								</a>
							</div>
						<br>
						Congratulations. Your Requirement has been successfully restored.
						
						<br><br> Please note that we did not restore the Traceability, however 
						the Traceability information was appended to the Requirement Description
						at the time of deletion.
						</div>
						</td>
					</tr>
					<%} %>
					<tr>
						<td align='left' colspan='4'>
						
							<%
							String printable = request.getParameter("printable");
							if ((printable == null) || (printable.equals(""))) {
								%>
								<div id ='requirementActions2' class='level2Box' style='display:block;'>
		        				<%
							}
							else {
								%>
								<div id ='requirementActions2' class='level2Box' style='display:none;'>
		        				<%
							}
							%>
							
							<table width='100%'>
								<tr>
						

									
					
							 
		        			
		        			
		        			
					
							
								





							



	

		        			
		        			

		     			</tr>
					</table>
		        		
						
								        						        			
					
		        			

		        	 		

		        			<%
		        			if (
		        					(
		        						(requirement.getApprovalStatus().equals("In Approval WorkFlow"))
		        						||
		        						(requirement.getApprovalStatus().equals("Rejected"))
		        					)
		        					&&
		        					(
		        						(requirement.getApprovers().contains("(P)" + user.getEmailId()))
		        						||
		        						(requirement.getApprovers().contains("(R)" + user.getEmailId()))
		        					)		        				
		        				){
		        					// the req is in the approval work flow or in Rejected state 
		        					// and the approver has 
		        					// either Rejected or is in Pending state for this req.
		        					// under these conditions, we show the approver the 'accept / reject' button.
		        				%>
		        				
							<%}%>
		        									
							</div>  
							        								
						</td>
					</tr>
								
					<!--  lets get the requirement details displayed -->
					<tr>
						<td colspan='4'>
						<div id = 'requirementPromptDiv' class='level2Box' style="display:none;"></div>
						<%=addedToBaselineMessage%>
						<%=removedFromBaselineMessage%>
						<%=requirementBaselineLockedMessage%>
						<%=requirementBaselineAlreadyExistsMessage%>
						</td>
					</tr>
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
					<tr>
						<td align='left' colspan='4'>				
							<div id ='requirementCoreDiv' class='level2Box'>
		        				<table width=100% class='table'>
		        					<tr id='nameRow'> 
										
									
										<td style='border-top:none;' colspan='4'>
										<div   id='requirementNameDiv' >
											<% 
												int rowSize = 0;
												int colSize = 180;
												
								    			
								    			
								    			try {
								    				int rowLength = requirement.getRequirementName().length() ;
								    				if (rowLength < colSize){
								    					rowSize = 1 ;
								    				}
								    				else {
								    					rowSize = (requirement.getRequirementName().length() / colSize);
									    				rowSize = rowSize + 1; 	
								    				}
								    			}
								    			catch (Exception ex) {
								    				rowSize = 1;
								    				ex.printStackTrace();
								    			}
								    			// if <br> happens then, we want to add to rowSize.
								    			int brHappensCount = requirement.getRequirementNameForHTML().toLowerCase().split("<br>").length ;
								    			
								    		    rowSize = rowSize + brHappensCount;
								    			
												String formattedName = requirement.getRequirementName();
												if ((formattedName != null ) && (formattedName.toLowerCase().contains("<br>"))){
													formattedName = formattedName.replace("<br>", "\n");
													formattedName = formattedName.replace("<BR>", "\n");
												}
												
												String nameToDisplay = requirement.getRequirementName();
												
												if (
													(formattedName == null)  
													|| 
													(formattedName.length() == 0) 
													){
													nameToDisplay = "Name is empty. Please enter a value here!!!";
												}
											
											%>
												<div id='requirementNameTextDiv' style='display:block'>
													<span class='normalText' style='cursor:pointer;'
													onClick='
															document.getElementById("requirementNameTextBoxDiv").style.display="block";
															document.getElementById("requirementNameTextDiv").style.display="none";
														'
													>
														<%=nameToDisplay %>
														
													</span>
													
												</div>
												<div id='requirementNameTextBoxDiv' style='display:none'>
													<table>
														<tr>
															<td style="vertical-align:middle">
																<span class='normalText'> 
																	<textarea class="form-control"
																		onfocus='
																			document.getElementById("requirementNameSubmitDiv").style.display="block";'
																		
																		<%=disabledString %>
																		id="requirementName" name="requirementName"  
																		rows='<%=rowSize %>' cols='<%=colSize %>'
																	><%=formattedName%></textarea>
																</span>	
																<div id='requirementNameSubmitDiv' style='display:none'>
																	<br>
																	<input type='button'  class="btn btn-xs btn-primary"  
																		value='Update Name'
																		onclick='
																			setRequirementName(<%=requirement.getRequirementId() %>, <%=requirement.getFolderId() %>);' >
																	<input type='button'  class="btn btn-xs btn-danger"  
																		value='Cancel'
																		onclick='
																			document.getElementById("requirementNameSubmitDiv").style.display="none";
																			document.getElementById("requirementNameTextDiv").style.display="block";
																			document.getElementById("requirementNameTextBoxDiv").style.display="none";
																		' >
																</div>						
															</td>
														</tr>
															
														
													</table>
								
												</div>											
																					
											

										</div>
										

										</td>
									</tr>
									
									
							








									<tr id='descriptionRow' > 
										
										<td style='border-top:none;' colspan='4'>
										
										<div  id='requirementDescriptionDiv' >
												
												<div id='requirementDescriptionTextBoxDiv' style='display:block'	>
														<%
														
														int rTEHeight = 0;
														int newRTEHeight = 0;
														String newRTEHeightS = "";
														try {
															newRTEHeightS = request.getParameter("newRTEHeight");
															rTEHeight= Integer.parseInt(newRTEHeightS);
														}
														catch (Exception e){
															e.printStackTrace();														}
														if (rTEHeight == 0){
															rTEHeight = 100;
														}
														newRTEHeight = rTEHeight + 400;
														
														
														if (isMSIE){
														%>
															<span class='normalText'><font color='red'>If you need to use the Rich Text Editor , please use Chrome or Firefox </font></span>
															<br>
														<%}%>
														
														
														
														<div  style='display:block;' id='requirementDescriptionSpan'>
															<textarea class="form-control" id="requirementDescription" 
															name="requirementDescription"  rows='3' cols='250' >
															<%=requirement.getRequirementDescription()  %></textarea>
															
															<div id='requirementDescriptionUpdateBtnDiv' style='display:none;' >
															<input type='button' name='UpdateDescriptionBtn' id='UpdateDescriptionBtn' 
																value=' Update Description '
																 class="btn btn-xs btn-primary"  
																<%=disabledString %>
																onclick='
																<%if (isMSIE){ %>
																	setRequirementNameAndDescription(<%=requirement.getRequirementId() %>, <%=requirement.getFolderId() %>, "MSIE");
																<%}
																else {%>
																	setRequirementNameAndDescription(<%=requirement.getRequirementId() %>, <%=requirement.getFolderId() %>, "NOTMSIE");
																<%} %>
																'
															>
															<input type='button' id='updateDescriptionCancelBtn'  class="btn btn-xs btn-danger"  
															value='Cancel'
															onclick='
																document.getElementById("requirementDescriptionUpdateBtnDiv").style.display="none";
															' >
															</div>
														</div>							
												</div>											
													

										</div>
										

										</td>
									</tr>
									
									
									
									
									
									
									
									
									
									
									<%
									ArrayList requirementBaselines = RequirementUtil.getRequirementBaselines(requirementId, databaseType);
								    if ((requirementBaselines != null) && (requirementBaselines.size() > 0)){
									%>
										<tr> 
											<th   class="info" align="left" style="width:150px" > Baselines  </th>
											<td style='border-top:none;' colspan='3'>
												<span class='normalText'>
												<%
										    	Iterator i = requirementBaselines.iterator();
										    	while ( i.hasNext() ) {
										    		RequirementBaseline requirementBaseline = (RequirementBaseline) i.next();
											    %>										    			
									    			<a href='#'
									    				style='color:black;'
									    				onclick='
										    			displayRequirementBaseline(
										    			<%=requirementBaseline.getRequirementBaselineId() %>,
										    			 <%=requirementBaseline.getRequirementId() %>)'>
										    			 <%if (requirementBaseline.getLocked() == 1){ %>
										    			 	<img src="/GloreeJava2/images/lock16.png" border="0">
										    			 <%}
										    			 else {%>
										    			 	<img src="/GloreeJava2/images/lockUnlock16.png" border="0">
										    			 <%} %>
									    				
									    				<%=requirementBaseline.getRTBaselineName()%>
									    				(V-<%=requirementBaseline.getRequirementBaselinedVersion()%>)
									    			</a>
									    			&nbsp;&nbsp;
											    <%
										    	}
											    %>
												</span>
								    			<div id='displayRequiermentBaselineDiv'>
								    			</div>											
											</td>
										</tr>									
										<%
										}
								    	if (requirementType.getRequirementTypeEnableApproval() == 1) {
								    		if ((requirement.getApprovers() != null) && (!(requirement.getApprovers().equals("")))) {
								    			
								    	%>
												<tr >
												<th class='info' ><span class='headingText'> Approvers </span> </th>
												<td  colspan='3' style='border-top:none;'>
													<input id='showApproversButton' type='button'
													 class="btn btn-xs btn-outline-primary"  
													style="border-color:white; color:blue; background-color:white; "
													 value='Show'
														onClick='showApproversTable("approversTableDiv",<%=requirement.getRequirementId() %>);
															document.getElementById("showApproversButton").style.display="none";
															document.getElementById("hideApproversButton").style.display="block";
													'>
													<input id='hideApproversButton' type='button' 
													class="btn btn-xs btn-outline-primary"  
													style="border-color:white; color:blue; background-color:white; display:none;"
													value='Hide' style='display:none'
														onClick='
															document.getElementById("approversTableDiv").style.display="none";
															document.getElementById("showApproversButton").style.display="block";
															document.getElementById("hideApproversButton").style.display="none";
													'>
													<div id='approversTableDiv' style='display:none'>
													</div>
												</td>
												</tr>
										<%	}
										}
										if (requirementType.getRequirementTypeEnableApproval() == 1) {%>
											<tr >									
											<th  class="info" align="left" style="width:150px"> 
												Acceptance Status  
											</th>
											<td style='border-top:none;' colspan='3'>
												<% if (requirement.getApprovalStatus().equals("Draft")){ %>
													<span class='normalText'> </span>Draft&nbsp;&nbsp;</span>
													<%
													if (!updateDisabled){ 
							        				%> 
															<input type='button'  class="btn btn-xs btn-outline-primary"  
															style="border-color:blue; color:blue; background-color:white; "
															 style='height:25px' name='Submit For Acceptance' value=' Submit  For Acceptance '
																onclick='
																	submitRequirementForApproval("<%=request.getParameter("requirementId")%>","<%=requirement.getFolderId()%>");
																	document.getElementById("requirementActionDiv").style.display="none";
																'> 	
													<%
													}
													%>
													<a href='#' title='Display the approvers for this requirement' onclick='displayApproversForThisRequirement("<%=request.getParameter("requirementId")%>")'> <img src="/GloreeJava2/images/help.png" border="0"></a>
													<%
												
												} %>
												<% if (requirement.getApprovalStatus().equals("In Approval WorkFlow")){ %>
														<span class='normalText' style="background-color:#99ccff">												
															<%=requirement.getApprovalStatus() %>
															for
															<%=requirement.getDaysSinceSubmittedForApproval() %> days 
														</span>
														&nbsp;
														<% int daysSinceLastApprovalReminder = requirement.getDaysSinceLastApprovalReminder(); %>
														<span class='normalText' title='Reminder sent <%=daysSinceLastApprovalReminder%> days ago' >
															<input type='button'  class="btn btn-xs btn-outline-primary"  
													style="border-color:white; color:blue; background-color:white; "  
															name='Remind Approvers' value=' Remind ' 
															onclick='remindApprovers(<%=requirementId%>)'>
															
														</span>
														<%
														if 	(requirement.getApprovers().contains("(P)" + user.getEmailId())){
															%>
															&nbsp;&nbsp;<span class='normalText'><input type='button'  class="btn btn-xs btn-outline-primary"  
													style="border-color:blue; color:blue; background-color:white; " 
															 value='Accept / Reject' onclick='showApproveRejectDiv("approveRejectDiv",<%=requirement.getRequirementId()%>)'>
															 </span>
															<%
														}
												} %>
												<% if (requirement.getApprovalStatus().equals("Approved")){ %>
													
														<span class='normalText' style="background-color:#CCFF99">
															Accepted
														</span>
												<%} %>
												<% if (requirement.getApprovalStatus().equals("Rejected")){ %>
													
														<span class='normalText' style="background-color:#FFA3AF">
															<%=requirement.getApprovalStatus()%>
														</span>
														<%
														if (requirement.getApprovers().contains("(R)" + user.getEmailId())){
															%>
															&nbsp;&nbsp;<span class='normalText'>
															<input type='button'  class="btn btn-xs btn-outline-primary"  
													style="border-color:blue; color:blue; background-color:white; " 
															 value='Cancel My Rejection' onclick='
																 handleRequirementActionOther(<%=requirement.getRequirementId()%>,<%=requirement.getFolderId()%>,"cancelMyRejection");
															'></span>
															<%
														}
												} %>
												
												
												<%if (requirement.getApprovalStatus().equals("In Approval WorkFlow")){%>
													<div id='approveRejectDiv' class='alert alert-success' style='display:none'></div>	
														
												<%}%>
												
												
											
											</td>
											</tr>
										<%}%>


																	
									
									<%
									if (requirementType.getRequirementTypeEnableApproval() == 1) {
									
										if (
												(requirement.getApprovedByAllDt() != null) && 
												(!(requirement.getApprovedByAllDt().trim().equals(""))) &&
												(!(requirement.getApprovedByAllDt().trim().equals("N/A")))
											){
											// this reqtype is eenable for approval work flow and the req has been approved by all . Sow the date.
											
										%>
											<tr onmouseover="this.style.background='#E5EBFF'"					
										onmouseout="this.style.background='white';">
												<th   class="info" align="left" style="width:150px"> Acceptance Date</th>
												<td style='border-top:none;' colspan='3' >
													<span class='normalText'>
													<%=requirement.getApprovedByAllDt()%> 
													</span>
												</td>
											</tr>
										<%} 
									}%>
									


									<% if (project.getHidePriority() != 1){ %>
									<tr >
										<th  class="info" align="left" style="width:150px">
											 Priority 
										</th>
										<td style='border-top:none;' colspan='3'>
																									
										
										
											<div id='requirementPriorityDiv' >
												
													<% 
														String requirementPriority = "";
														String priorityColor = "black";
														if (requirement.getRequirementPriority().equals("High")){
															priorityColor = "red";
															requirementPriority = "<option value='High' SELECTED>High </option><option value='Medium'>Medium</option><option value='Low'>Low</option>";
														}
														else if (requirement.getRequirementPriority().equals("Medium")){
															priorityColor = "blue";
															requirementPriority = "<option value='High'>High </option><option value='Medium' SELECTED>Medium</option><option value='Low'>Low</option>";
														}
														else {
															requirementPriority = "<option value='High'>High </option><option value='Medium'>Medium</option><option value='Low' SELECTED>Low</option>";
														}
													%>
														<div id='requirementPriorityStringDiv'>
															<span class='normalText' style='cursor:pointer'
															onClick='
																	document.getElementById("requirementPriorityStringDiv").style.display="none";
																document.getElementById("requirementPriorityDropDownDiv").style.display="block";
															' 
															 >
															
																<font color='<%=priorityColor%>'>
																<%=requirement.getRequirementPriority()%></font>
															</span>
														</div>
														<div id='requirementPriorityDropDownDiv' style='display:none'>
															<span class='normalText'> 
																<select
																	<%=disabledString %> 
																	name="requirementPriority" id="requirementPriority" 
																	onChange='
																		setRequirementPriority(<%=requirement.getRequirementId() %>)
																	'>
																	<%=requirementPriority %>
																</select>
															</span>
														</div>											
												
												
											</div>
										</td>
										
									</tr>
									<%} %>
									
									
									
									<tr> 
										<th  class="info" align="left" style="width:150px">
											 Owner 
										</th>
										<td  style='border-top:none;' colspan="3">
										<div id='requirementOwnerDiv'>
											<div id='requirementOwnerStringDiv'>
												<span class='normalText' style='cursor:pointer'
												<% if (!(updateDisabled)){%>
													onClick='
														if (document.getElementById("requirementOwnerDropDownDiv").style.display == "none"){
															document.getElementById("requirementOwnerStringDiv").style.display="none";
															displayRequirementOwners(<%=requirement.getRequirementId() %>)
														};
													'
												<%} %>
												 >
													<%=requirement.getRequirementOwner()%>
												</span>
											</div>
											<div id='requirementOwnerDropDownDiv' style='display:none'>
												
											</div>											
												
										</div>	

										</td>		
									</tr>									
									
									
									
									
									<%
									// if this is a not a glossary req (eg GL-1, GL-2 etc..) 
									// and this requirement has some glossary elements, then lets print it.
									
									if (	(!(requirementType.getRequirementTypeName().toLowerCase().equals("glossary")))
											&&
											(requirement.getGlossary() != null) 
											&& 
											!(requirement.getGlossary().equals(""))
										 ) {
											
									%>
										<tr > 
											<th  class="info" align="left" style="width:150px"> Glossary</th>
											<td  style='border-top:none;'colspan="3"><span class='normalText'>
												<%
												String glossary  = requirement.getGlossary();
												// lets split the glossary
												String [] glossaryItems  = glossary.split(":###:");
															
												for (int i=0;i<glossaryItems  .length;i++){
													String glossaryLine = glossaryItems[i];
													if ((glossaryLine != null) && (!glossaryLine.equals(""))){
														String [] glossaryLineItems = glossaryLine.split(":##:");
														String glossaryName = glossaryLineItems [0];
														String glossaryReqId = glossaryLineItems [1];
														%>
														<a href='/GloreeJava2/jsp/Requirement/displayPrintableRequirement.jsp?isGlossary=true&requirementId=<%=glossaryReqId%>' 
								        				target="_blank"
								        				style='color:black;' >
								        				<%=glossaryName %>	 
														 </a>
														&nbsp;&nbsp;&nbsp;		
														<%	
													}
												}
												%>
												
												<input type='button'  class="btn btn-xs btn-outline-primary"  
													style="border-color:white; color:blue; background-color:white; " 
													name='New Glossary Item' value='New Glossary Item'
													onclick="document.getElementById('createGlossaryDiv').style.display = 'block';"
															
												>			
												</span>
												<div id='createGlossaryDiv' class='alert alert-success' style='display:none'>
													<table class='paddedTable'>
														<tr>
															<td> <span class='normalText'> Glossary Term </span></td>
															<td><span class='normalText'>
																<input type='text' id='glossaryName' size='50'>
															</span></td>
														</tr>
														<tr>
															<td> <span class='normalText'> Glossary Definition </span></td>
															<td><span class='normalText'>
																<input type='text' id='glossaryDescription' size='100'>
															</span></td>
														</tr>
														<tr>
															<td colspan="2" align="left">
															<input type="button"  class="btn btn-xs btn-outline-primary"  
																style="border-color:blue; color:blue; background-color:white; " 
																name="createGlossary" id="createGlossaryButton" value="  Create New Glossary Term  " 
																onclick="createNewGlosaryItem(<%=requirement.getRequirementId() %>);">
															
															&nbsp;&nbsp;
															<input type="button" 
															 class="btn btn-xs btn-outline-primary"  
															style="border-color:red; color:red; background-color:white; "
															 name="closeButton" id="closeButton" value="  Close  " 
																onclick="document.getElementById('createGlossaryDiv').style.display = 'none';">
															
															</td>
														</tr>
													</table>
												
												</div>
											</td>		
										</tr>
									<%} %>

									
									
									
									
									
									<%if (	(requirement.getRequirementExternalUrl() != null) && 
											!(requirement.getRequirementExternalUrl().equals(""))
										 ) {
									%>
										<tr onmouseover="this.style.background='#E5EBFF'"					
										onmouseout="this.style.background='white';"> 
											<th><span class='headingText'> External Url </span></th>
											<!--  put http:// if it doesn't already exist infront of URLs -->
											<td style='border-top:none;'  colspan="3"><span class='normalText'>
												<%
												String requirementExternalURL = requirement.getRequirementExternalUrl();
												if ((requirementExternalURL != null) && (!(requirementExternalURL.contains("http")))){
													requirementExternalURL =  "http://" + requirementExternalURL; 
												}
												%>
												<a href='#'  onclick='window.open ("<%=requirementExternalURL%>");'>
													<%=requirement.getRequirementExternalUrl()%>
												</a>
												</span>
											</td>		
										</tr>
									<%} %>





									
									<%
									String traceFrom = requirement.getRequirementTraceFrom();
									if (	(traceFrom != null) && 
											(traceFrom.contains("DEF"))
										 ) {
										// this req has a trace coming from a Defect object. so we can 
										// show the defects.
										ArrayList traceFromObjects = requirement.getRequirementTraceFromObjects();
									%>								
										<tr onmouseover="this.style.background='#E5EBFF'"					
										onmouseout="this.style.background='white';"> 
											<th><span class='headingText'> Defects </span></th>
											<td style='border-top:none;' colspan="3">
												<span class='normalText'>
												<%
												Iterator i = traceFromObjects.iterator();
												int counter = 0;
												while (i.hasNext()){
													Trace trace = (Trace) i.next();
													Requirement defectRequirement = new Requirement( trace.getFromRequirementId(), databaseType);
													if (defectRequirement.getRequirementTypeName().equals("Defects")){
														counter++;
														if (counter > 1){
														%>
														,&nbsp;
														<%
														}
												%>
														<span class='normalText'title="Name : <%=defectRequirement.getRequirementNameForHTML() %>">
								   							<a href="#" onclick='
							 									displayFolderInExplorer(<%=defectRequirement.getFolderId()%>);
							 									displayFolderContentCenterA(<%=defectRequirement.getFolderId() %>);
							 									displayFolderContentRight(<%=defectRequirement.getFolderId() %>);
							 									displayFolderContentsInExplorer(<%=defectRequirement.getFolderId() %>);									   							
									   							displayRequirement(<%=defectRequirement.getRequirementId() %>)'>
								   							<%=defectRequirement.getRequirementFullTag() %>
								   							</a>
									   					</span>
									   					
								   				<%	}
												}%>
												</span>
											</td>		
										</tr>
									<%} %>




									<% ArrayList attachments = requirement.getRequirementAttachments(databaseType);
									if (attachments.size() > 0){  %>
										<tr >
										<th  class="info" align="left" style="width:150px" > Images </th>
										<td  colspan="3">
										
												
											<span class='normalText'>
											<% Iterator atachmentIterator = attachments.iterator();
											while (atachmentIterator.hasNext()) {
												RequirementAttachment attachment = (RequirementAttachment) atachmentIterator.next();
												if (
														(attachment.getFileName().toLowerCase().endsWith(".jpg"))
														||
														(attachment.getFileName().toLowerCase().endsWith(".jpeg"))
														||
														(attachment.getFileName().toLowerCase().endsWith(".jpe"))
														||
														(attachment.getFileName().toLowerCase().endsWith(".jfif"))
														||
														(attachment.getFileName().toLowerCase().endsWith(".gif"))
														||
														(attachment.getFileName().toLowerCase().endsWith(".tif"))
														||
														(attachment.getFileName().toLowerCase().endsWith(".tiff"))
														||
														(attachment.getFileName().toLowerCase().endsWith(".png"))
													){
													// if this is a jpg file, lets display it.
													%>
														
														
													<div class="panel panel-info"> 
														<div class="panel-heading " style='text-align:left'> 
															<h class="panel-title">
																<%=attachment.getTitle() %>
															</h>
															
														</div>
														<div class="panel-body"> 
																				
															
															<a href='/GloreeJava2/servlet/RequirementAction?action=downloadAttachment&attachmentId=<%=attachment.getRequirementAttachmentId()%>'
															target='_blank'>
															<img style='border:3px solid lightblue; width:600px' src="/GloreeJava2/servlet/Image?<%=attachment.getFilePath() %>" >
															</a>
															<br>
															
														</div>
													</div>
											
													<%
													}
													%>
									
										
																	
											<% }%>
											</span>
											
											
											<br><br>
										</td>
										</tr>
									<%} %>
								</table>
							</div>
						</td>
					</tr>
					
					
				</table>


					
						

				
			</fieldset>
	</div>

	</div>
<%	
	}
}%>