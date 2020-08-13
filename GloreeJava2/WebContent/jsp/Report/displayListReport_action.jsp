<!-- GloreeJava2 -->
<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String dRAIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dRAIsLoggedIn == null) || (dRAIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
%>
		// log in page.
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project dRAProject= (Project) session.getAttribute("project");
	SecurityProfile dRAsecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dRAIsMember = false;
	if (dRAsecurityProfile.getRoles().contains("MemberInProject" + dRAProject.getProjectId())){
		dRAIsMember = true;
	}
	
	if (dRAIsMember){
		User dRAUser = dRAsecurityProfile.getUser();
		int  dRAFolderId = Integer.parseInt(request.getParameter("folderId"));
		Folder dRAFolder = new Folder(dRAFolderId);
		RequirementType requirementType = new RequirementType(dRAFolder.getRequirementTypeId());
		ArrayList eligibleFolders = dRAProject.getMyFolders();
		Iterator i = eligibleFolders.iterator();
		
		// lets get the customAttributes and keep them here.
		// if its non empty, we display the custom attribs link.
		ArrayList dRAttributes = (ArrayList) ProjectUtil.getAllAttributes(dRAFolder.getRequirementTypeId());
		
%>
	<form method='post' action='#' id='bulkEditActionForm'>
	<table class='paddedTable'  width='100%' >
		
		<tr>
			<td>
				<div id ='bulkEditActionResponse' style="display:none;"></div>
			</td>
		</tr>
	
		<tr>
			<td align='left'>
				<div style="font-size:8pt; float: left;">			
					<a href='#' 
					class='btn btn-sm btn-primary'
					style='color:white'
					onclick="
						document.getElementById('bulkActionCloseDiv').style.display = 'block';
						document.getElementById('standardAttributesDiv').style.display = 'block';
						document.getElementById('customAttributesDiv').style.display = 'none';
						document.getElementById('traceabilityDiv').style.display = 'none'; 
						document.getElementById('workFlowDiv').style.display = 'none';
						document.getElementById('bulkEditActionResponse').style.display = 'none';
						">
					Standard Actions
					</a>
					&nbsp;&nbsp;|&nbsp;&nbsp;
					<%
					if (dRAttributes == null){
					%>
					<font color='gray'> Custom Attributes </font>
					<%	
					}
					else{
					%>
					<a href='#' 
						class='btn btn-sm btn-primary'
						style='color:white'
						onclick="
						document.getElementById('bulkActionCloseDiv').style.display = 'block';
						document.getElementById('standardAttributesDiv').style.display = 'none';
						document.getElementById('customAttributesDiv').style.display = 'block';
						document.getElementById('traceabilityDiv').style.display = 'none'; 
						document.getElementById('workFlowDiv').style.display = 'none';
						document.getElementById('bulkEditActionResponse').style.display = 'none'; 
						">
					Custom Attributes
					</a>
					<%}%>
					&nbsp;&nbsp;|&nbsp;&nbsp;
					<a href='#' 
						class='btn btn-sm btn-primary'
						style='color:white'
						onclick="
						document.getElementById('bulkActionCloseDiv').style.display = 'block';
						document.getElementById('standardAttributesDiv').style.display = 'none';
						document.getElementById('customAttributesDiv').style.display = 'none';
						document.getElementById('traceabilityDiv').style.display = 'block';
						document.getElementById('workFlowDiv').style.display = 'none';
						document.getElementById('bulkEditActionResponse').style.display = 'none'; 
						">
						Traceability Actions
					</a>
					&nbsp;&nbsp;|&nbsp;&nbsp;
					<%
					if  (requirementType.getRequirementTypeEnableApproval() == 0 ){
					%>
					<font color='gray'> Approval WorkFlow Actions </font>
					<%	
					}
					else{
					%>
					<a href='#' 
						class='btn btn-sm btn-primary'
						style='color:white'
						onclick="
						document.getElementById('bulkActionCloseDiv').style.display = 'block';
						document.getElementById('standardAttributesDiv').style.display = 'none';
						document.getElementById('customAttributesDiv').style.display = 'none';
						document.getElementById('traceabilityDiv').style.display = 'none';
						document.getElementById('workFlowDiv').style.display = 'block';
						document.getElementById('bulkEditActionResponse').style.display = 'none'; 
						">
						Approval WorkFlow Actions
					</a>
					<%} %>
				</div>
				
				<div id='bulkActionCloseDiv' style="display:block">
				
					<a href='#' 
						class='btn btn-sm btn-danger'
						style='color:white'
						onclick="
						document.getElementById('bulkActionCloseDiv').style.display = 'none';
						document.getElementById('standardAttributesDiv').style.display = 'none';
						document.getElementById('customAttributesDiv').style.display = 'none';
						document.getElementById('traceabilityDiv').style.display = 'none';
						document.getElementById('workFlowDiv').style.display = 'none';" 
					> 
						Close
					</a>										
				</div>
								
			</td>
		</tr>
		<tr>
			<td>					
				<div id ='standardAttributesDiv' style="display:none;" >
				
					<table style='width:900px' class='table table-striped table-hover table-bordered'  align='left' >
						<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
							<td width='200' style="width:200px">
							 	<span class='headingText'>Add Comment
							 	<img src="/GloreeJava2/images/comments16.png" border="0">
							 	</span>				
							</td>
							<td width='150' style="width:150px">
								<span class='normalText'> 
								<input type='text' id='bulkCommentNote' placeholder='Enter a comment note here ' size='100'>
								</span>															
							
							</td>
							<td align='left'>
								<a href='#'  class='btn btn-sm btn-danger'  style='color:white' onclick='bulkEditActionForm("addComment", "<%=dRAFolderId%>")'>
								Update</a> 
								
							</td>
						</tr>
						<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
							<td width='200' style="width:200px">
							 	<span class='headingText'>Add Requirement To Baseline
							 	<img src="/GloreeJava2/images/baseline16.png" border="0">
							 	</span>				
							</td>
							<td width='150' style="width:150px">
								<span class='normalText'> 
								<select name="addRequirementToBaseline" id='addRequirementToBaseline'>
									<option value='-1'></option>
								<%
								ArrayList baselines = ProjectUtil.getEligibleBaselinesForRequirementType(dRAFolder.getRequirementTypeId());
								Iterator m = baselines.iterator();
								while (m.hasNext()){
									RTBaseline rTBaseline = (RTBaseline) m.next();
								%>
									
									<%if (rTBaseline.getLocked() == 0 ){ %>
										<option value='<%=rTBaseline.getBaselineId()%>'>
										<%=rTBaseline.getBaselineName()%>
										(Unlocked)
										</option>
									<%} %>
									
								<%	
								}
								%>	
								</select>
								</span>															
							
							</td>
							<td align='left'>
								<a href='#'  class='btn btn-sm btn-danger'  style='color:white' onclick='bulkEditActionForm("addRequirementToBaseline", "<%=dRAFolderId%>")'>
								Update</a> 
								
							</td>
						</tr>
						<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
							<td width='200' style="width:200px">
							 	<span class='headingText'>Remove Requirements From Baseline
							 	<img src="/GloreeJava2/images/baseline16.png" border="0">
							 	</span>				
							</td>
							<td width='150' style="width:150px">
								<span class='normalText'> 
								<select name="removeRequirementsFromBaseline" id='removeRequirementsFromBaseline'>
									<option value='-1'></option>
								<%
								Iterator n = baselines.iterator();
								while (n.hasNext()){
									RTBaseline rTBaseline = (RTBaseline) n.next();
								%>
									
									<%if (rTBaseline.getLocked() == 0 ){ %>
										<option value='<%=rTBaseline.getBaselineId()%>'>
										<%=rTBaseline.getBaselineName()%>
										(Unlocked)
										</option>
									<%} %>
								<%	
								}
								%>	
								</select>
								</span>															
							
							</td>
							<td align='left'>
								<a href='#'  class='btn btn-sm btn-danger'  style='color:white' onclick='bulkEditActionForm("removeRequirementsFromBaseline", "<%=dRAFolderId%>")'>
								Update</a> 
								
							</td>
						</tr>					
											
						<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
							<td width='200' style="width:200px">
							 	<span class='headingText'>Set Owner To</span>				
							</td>
							<td width='150' style="width:150px">
								<%
									String canNotBeOwners = dRAProject.getCanNotBeOwnersInProject();

									ArrayList users = dRAProject.getMembers();
									Iterator u = users.iterator();
									
								%>
								
						
								<span class='normalText'>
								<select name="requirementOwner" id="setOwner">
									<%
									while (u.hasNext()){
										User projectMember = (User) u.next();
										if (canNotBeOwners.contains(projectMember.getEmailId())){
											continue;
										}

								
										
										if (projectMember.getEmailId().equals(dRAUser.getEmailId())){
										%>
											<option SELECTED value='<%=projectMember.getEmailId()%>'><%=projectMember.getLastName() %>  <%=projectMember.getFirstName() %></option>
										<%			
										}
										else {
										%>
											<option value='<%=projectMember.getEmailId()%>'><%=projectMember.getLastName() %>  <%=projectMember.getFirstName() %></option>
										<%
										}
									}
									%>
									</select>					
								</span>
																
							
							</td>
							<td align='left'>
								<a href='#'  class='btn btn-sm btn-danger'  style='color:white' onclick='bulkEditActionForm("setOwner", "<%=dRAFolderId%>")'>
								Update</a> 
								
							</td>
						</tr>
						<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
							<td width='200' style="width:200px">
							 	<span class='headingText'>Set Priority To</span>				
							</td>
							<td width='150' style="width:150px">
								<span class='normalText'>
								<select name="setPriority" id="setPriority">
									<option value="High" SELECTED>High </option>
									<option value="Medium" >Medium</option>
									<option value="Low">Low</option>
								</select>
								</span>
							</td>
							<td align='left'>
								<a href='#'  class='btn btn-sm btn-danger'  style='color:white' onclick='bulkEditActionForm("setPriority", "<%=dRAFolderId%>")'>
								Update </a>
							</td>
						</tr>					
						<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
							<td width='200' style="width:200px">
							 	<span class='headingText'>Set External URL To</span>				
							</td>
							<td width='150' style="width:150px">
								<span class='normalText'>
								<input type="text" id="setExternalURL" name="setExternalURL" size="50" maxlength="1000"> 
								</span>
							</td>
							<td align='left'>
								<a href='#'  class='btn btn-sm btn-danger'  style='color:white' onclick='bulkEditActionForm("setExternalURL", "<%=dRAFolderId%>")'>
								Update </a>
							</td>
						</tr>					
						<%
								int percentageCompletedDriverReqTypeId = dRAProject.getPercentageCompletedDriverReqTypeId();
								if (
									(percentageCompletedDriverReqTypeId > 0 ) 
									&&
									(percentageCompletedDriverReqTypeId != requirementType.getRequirementTypeId())
								){
									// If this project has a percentageCompletedDriverReqTypeId value set (>0) 
									// and this requirement does not belong to the percentage complete driver
									// the update should be disabled.
									// Since the req type's percentage complete can not be updated by the suer
									// don't show this section.
									
								}
								else {
						%>
							<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
								<td width='200' style="width:200px">
								 	<span class='headingText'>Set Percent Complete To</span>				
								</td>
								<td width='150' style="width:150px">
																	<span class='normalText' >
									<input type="text"   id="setPctComplete" name="setPctComplete" value='0' size="3" maxlength="3"> %
									</span>
								</td>
								<td align='left'> 
									<a href='#' class='btn btn-sm btn-danger'  style='color:white'onclick='bulkEditActionForm("setPctComplete", "<%=dRAFolderId%>")'>
									Update </a>
								</td>
							</tr>					
						<%
								}
								
						%>

						<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
							<td width='200' style="width:200px">
							 	<span class='headingText'>Move Requirements To</span>				
							</td>
							<td width='150' style="width:150px">
								<span class='normalText'>
								<select name="setFolder" id="setFolder">
									<%
									while (i.hasNext()){
										Folder i2Folder = (Folder) i.next();
										String moveFolderDisabled = "";
										String moveFolderDisabledReason = "";
										
										// lets see if this user has permissions to move requirements to this folder.
										if (!(dRAsecurityProfile.getPrivileges().contains("createRequirementsInFolder" 
												+ i2Folder.getFolderId()))){
											// not permitted to move stuff to / create stuff in this folder
											moveFolderDisabled = "DISABLED";
											moveFolderDisabledReason  = " (Not Permitted)";
										}
									%>
										<option value='<%=i2Folder.getFolderId()%>' <%=moveFolderDisabled %>>
											<%=i2Folder.getFolderPath()%> <%=moveFolderDisabledReason %>
										</option>
									<%	
										}
									%>
								</select> 
								</span>
								<div id='moveConfirmDiv' style='display:none'>
									<div class='alert alert-danger'><b>Please note that MOVING an object from one type to a different type, DELETES the source object 
										and creates a new copy in another object type. 
										<br><br>
										If the target object type has the same attributes as the source, then you get an exact replica.
										
										<br><br> If you have any doubts on how this works, please try 
										the COPY function and then the DELETE function. You get the same effect , in a more controlled way</b>
									</div>
									<br><br>
									<a href='#' 
										onclick='document.getElementById("moveConfirmDiv").style.display="none";bulkEditActionForm("setFolder", "<%=dRAFolderId%>")' 
										class='btn btn-sm btn-danger'  style='color:white' style="color:white">Move to New Type & Delete Source </a>
									<a href='#' 
										onclick='document.getElementById("moveConfirmDiv").style.display="none"; ' 
										class='btn btn-sm btn-success' style="color:white">Cancel</a>
									
								</div>
							</td>
							<td align='left'> 
								<a href='#'  class='btn btn-sm btn-danger'  style='color:white'  onclick='document.getElementById("moveConfirmDiv").style.display="block"'>
								Update </a>
							</td>
						</tr>
						<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
							<td width='200' style="width:200px">
							 	<span class='headingText'>Set Testing Status To</span>				
							</td>
							<% if (dRAFolder.getRequirementTypeName().equals("Test Results")){ %>
								<td width='150' style="width:150px">
									<span class='normalText'>
									<select name="testingStatus" id="testingStatus">
										<option value='Pending'>Pending</option>
										<option value='Pass'>Pass</option>
										<option value='Fail'>Fail</option>
									</select> 
									</span>
								</td>
								<td align='left'> 
									<a href='#'  class='btn btn-sm btn-danger'  style='color:white'  onclick='bulkEditActionForm("setTestingStatus", "<%=dRAFolderId%>")'>
									Update </a>
								</td>
							<%}
							else {%>
								<td width='150' style="width:150px">
									<span class='normalText'>
										Testing Status can only be set for Test Results. 
									</span>
								</td>
								<td align='left'> 
									&nbsp;
								</td>
							<%} %>							
						</tr>					
											
											
						<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
							<td width='200' style="width:200px">
							 	<span class='headingText'>Replace </span>				
							</td>
								<td >
									<span class='normalText'>
								
									<select name="case" id="case">
										<option value='exactText'>Exact Text</option>
										<option value='ignoreCase'>Ignore Case</option>
									</select>
									
									in
								
									<select name="replaceIn" id="replaceIn">
										<option value='nameAndDescription'>Name & Description</option>
										<option value='name'>Name only</option>
										<option value='description'>Description only</option>
w
									</select>
									from 
									<input type="text" id="searchString" name="searchString" size="10" maxlength="1000">
									
									to
									
									<input type="text" id="replaceString" name="replaceString" size="10" maxlength="1000">
									 
									</span>
								</td>
								<td align='left'> 
									<a href='#'  class='btn btn-sm btn-danger'  style='color:white'  onclick='bulkEditActionForm("replaceText", "<%=dRAFolderId%>")'>
									Update </a>
								</td>
														
						</tr>					
											
											
						<%
						boolean deleteDisabled = false;
						if (!(dRAsecurityProfile.getPrivileges().contains("deleteRequirementsInFolder" 
								+ dRAFolderId))){
							deleteDisabled = true;
						}
						%>
						<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
							<td colspan='3' align='left'>
								
		        				<% if (deleteDisabled){ %>
		        					 <font color='gray'> Delete</font>
		        				<%}
		        				else {%>
		        					<a href='#'  
		        					onclick='
		        					document.getElementById("bulkDeleteRequirementsPrompt").style.display="block";
		        					document.getElementById("bulkPurgeRequirementsPrompt").style.display="none";
		        					document.getElementById("copyRequirementInBulkEditPrompt").style.display="none";
		        					bulkEditActionForm("deleteRequirementsPrompt", "<%=dRAFolderId%>");
		        					'>
									Delete Requirements</a>
		        				<%} %>
		        				
		        				<span class='normalText'>&nbsp;|&nbsp;</span>
							 	
		        				<% if (deleteDisabled){ %>
		        					 <font color='gray'> Purge</font>
		        				<%}
		        				else {%>
		        					<a href='#'  
		        					onclick='
		        					document.getElementById("bulkDeleteRequirementsPrompt").style.display="none";
		        					document.getElementById("bulkPurgeRequirementsPrompt").style.display="block";
		        					document.getElementById("copyRequirementInBulkEditPrompt").style.display="none";
		        					bulkEditActionForm("purgeRequirementsPrompt", "<%=dRAFolderId%>");
		        					'>
									Purge Requirements</a>
		        				<%} %>
		        			
		        				<span class='normalText'>&nbsp;|&nbsp;</span>

								<span title='Copy this Requirement to another location'>
		        				<a href ='#' 
		        				onClick='
			        				copyRequirementFormInBulkEdit(<%=dRAFolderId%>);
			        				document.getElementById("bulkDeleteRequirementsPrompt").style.display="none";
			        				document.getElementById("bulkPurgeRequirementsPrompt").style.display="none";
			        				document.getElementById("copyRequirementInBulkEditPrompt").style.display="block";
			        				
	        					'> 
								Copy 
								<img src="/GloreeJava2/images/copy.png" border="0">
								</a>
								<span class='normalText'>&nbsp;|&nbsp;</span>		
			        			</span>
			        			
			        			<span title='Lock these Requirements'>
		        				<a href ='#' 
		        				onClick='bulkEditActionForm("lockRequirements",<%=dRAFolderId %>);'> 
								<img src="/GloreeJava2/images/lock16.png" border="0"> Lock
								</a>
								</span>
								<span class='normalText'>&nbsp;|&nbsp;</span>
								
								<span title='Unlock these Requirements'>
		        				<a href ='#' 
		        				onClick='bulkEditActionForm("unlockRequirements",<%=dRAFolderId %>);'> 
								<img src="/GloreeJava2/images/lockUnlock16.png" border="0"> Unlock
								</a>
			        			</span>
							 	
							 	<span class='normalText'>&nbsp;|&nbsp;</span>
								
								<span title='Rebuild the Glossary for these objects'>
		        				<a href ='#' 
		        				onClick='bulkEditActionForm("reGlossarize",<%=dRAFolderId %>);'> 
								 Rebuild Glossary
								</a>
			        			</span>
							 	
							</td>
						</tr>									
						<tr>
							<td colspan='3' align='left'>
								<div id='bulkDeleteRequirementsPrompt' ></div>
								<div id='bulkPurgeRequirementsPrompt' ></div>
								<div id='copyRequirementInBulkEditPrompt' ></div>
							</td>
						</tr>							
					</table>
				</div>
						
					<!--  Lets try to display the custom attributes for this req type. -->
					<%
					
					String dRAttributeIdString = "";
					if (dRAttributes != null) {
						// this means there are custom attributes for the RT. and we need to give
						// bulk actions for them too...
					%>
						<div id ='customAttributesDiv' style="display:none;">


							<div class='alert alert-info'>
								Custom Attribute to Update <select id='customAttributeDisplaySelector' onchange='showCustomAttributeToUpdate()'>
									<option value=''> </option>
									<%
										i = dRAttributes.iterator();
										// lets iterate through all the attributes in this RT.
										while (i.hasNext()){
											RTAttribute a = (RTAttribute) i.next();
											if (a.getSystemAttribute() == 1){
												// we do not want the users to set the system attributes here.
												continue;
											}
											%>
											<option value='<%=a.getAttributeId()%>'> <%=a.getAttributeName() %></option>
											<%
										}
									%>
								</select>
							</div>
							<%
							i = dRAttributes.iterator();
							// lets iterate through all the attributes in this RT.
							while (i.hasNext()){
								RTAttribute a = (RTAttribute) i.next();
								if (a.getSystemAttribute() == 1){
									// we do not want the users to set the system attributes here.
									continue;
								}
								%>
								<div id='updateAttributeDiv<%=a.getAttributeId() %>' class='updateAttributesDiv' style='display:none'>
								<table style='width:100%' class='table table-striped table-hover table-bordered'  align='left'>
								<%
								if (a.getAttributeType().equals("Drop Down")){
									%>
									<tr   onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
										<td style="width:200px">
											<span class='headingText'> Set <b><%=a.getAttributeName()%></b> to</span> 
											<%
												String required = "";
												if (a.getAttributeRequired() ==1 ){
													required = "Required";
											%>
												
			    									<sup><span style='color: #ff0000;'>*</span></sup> 
			    							<%
			    								}
			    							%>
										</td>
										<td style="width:500px">
											<span class='normalText'>
											<select class='form-control' name='set<%=a.getAttributeId()%>' id='set<%=a.getAttributeId()%>'>
											<option value=''></option>
											<% 
											int parentAttributeId = a.getParentAttributeId();
											String [] o = a.getAttributeDropDownOptions().split(",");
											for (int j=0 ; j < o.length; j++){
												String optionName = o[j];
												if (parentAttributeId > 0){
													// this is a child attribute and the attribute values are like Porsche:911,Porsche:Panamera,Porsche:Carrera
													// where Porsche is the parent attribute value and 911,Panamera and Carrera are the potential child values.
													// so we need to strip out the first portion .
													if ((optionName != null) && (optionName.contains(":"))){
														String [] oN = optionName.split(":");
														optionName = oN[1];
													}
												}
												%>
												<option value='<%=optionName%>' ><%=optionName%></option>
												
												<%
											}
											%>
											</select>
											</span>
										</td>
										<td align='left'> 
											<a href='#'   class='btn btn-sm btn-danger'  style='color:white' onclick='bulkEditActionForm("setCustomDropDownSingle<%=required%>:##:<%=a.getAttributeId()%>", "<%=dRAFolderId%>")'>
											Update </a>
										</td>
										 				
									</tr>	
									<%
								}
								else if (a.getAttributeType().equals("Drop Down Multiple")){
									String [] o = a.getAttributeDropDownOptions().split(",");
									
									int dropDownMultipleSize = 3;
									if (o.length > 10 ){
										dropDownMultipleSize = 10;
									}
									else {
										dropDownMultipleSize = o.length;
									}
									
									%>
									<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
										<td style="width:200px">
											<span class='headingText'> Set <b><%=a.getAttributeName()%></b> to</span>
											<%
												String required = "";
												if (a.getAttributeRequired() ==1 ){
													required = "Required";
											%>
												
			    									<sup><span style='color: #ff0000;'>*</span></sup> 
			    							<%
			    								}
			    							%> 
										</td>
										<td style="width:500px">
											<span class='normalText'>
											<select class='form-control' multiple='MULTIPLE' size='<%=dropDownMultipleSize %>' name='set<%=a.getAttributeId()%>' id='set<%=a.getAttributeId()%>'>
											<% 
											
											for (int j=0 ; j < o.length; j++){
												String optionName = o[j];
												%>
												<option value='<%=optionName%>' ><%=optionName%></option>
												<%
											}
											%>
											</select>
											</span>
										</td>
										<td align='left'> 
											<a href='#'   class='btn btn-sm btn-danger'  style='color:white' onclick='bulkEditActionForm("setCustomDropDownMultiple<%=required%>:##:<%=a.getAttributeId()%>", "<%=dRAFolderId%>")'>
											Update </a>
										</td>
										 				
									</tr>	
									<%
									
									// Drop down multiple is a special case of DropDown. We want to give additional functionality to this
									// for example, let users add an extra value to the value set. 
									// for for all the requirements, for severity, add '3' to the existing values. 
									// or for all the requirements, if severity '3' exists, remove that. etc..
									
									// lets make an attempt to add an dropdown-multiple value 
									%>
									<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
										<td style="width:200px">
											<span class='headingText'> Add a value to <b><%=a.getAttributeName()%></b></span>
											 
										</td>
										<td style="width:500px">
											<span class='normalText'>
											<select class='form-control'  name='addValue<%=a.getAttributeId()%>' id='addValue<%=a.getAttributeId()%>'>
											<% 
											
											for (int j=0 ; j < o.length; j++){
												String optionName = o[j];
												%>
												<option value='<%=optionName%>' ><%=optionName%></option>
												<%
											}
											%>
											</select>
											</span>
										</td>
										<td align='left'> 
											<a href='#'  class='btn btn-sm btn-danger'  style='color:white' onclick='bulkEditActionForm("addValueToCustomDDM:##:<%=a.getAttributeId()%>", "<%=dRAFolderId%>")'>
											Append </a>
										</td>
									</tr>	
									<%
									// lets make an attempt to remove a drop down value.
									%>
									<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
										<td  style="width:200px">
											<span class='headingText'> Remove a value from <b><%=a.getAttributeName()%></b></span>
											 
										</td>
										<td style="width:500px">
											<span class='normalText'>
											<select  class='form-control' name='removeValue<%=a.getAttributeId()%>' id='removeValue<%=a.getAttributeId()%>'>
											<% 
											
											for (int j=0 ; j < o.length; j++){
												String optionName = o[j];
												%>
												<option value='<%=optionName%>' ><%=optionName%></option>
												<%
											}
											%>
											</select>
											</span>
										</td>
										<td align='left'> 
											<a href='#'  class='btn btn-sm btn-danger'  style='color:white' onclick='bulkEditActionForm("removeValueFromCustomDDM:##:<%=a.getAttributeId()%>", "<%=dRAFolderId%>")'>
											Remove </a>
										</td>
									</tr>	
								<%
								}
								
								else if (a.getAttributeType().equals("Date")){
									%>
									<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
										<td style="width:200px">
											<span class='headingText'> Set <b><%=a.getAttributeName()%></b> to</span>
											<%
												if (a.getAttributeRequired() ==1 ){
													// date fild js checker already checks for empty fields. so
													// we don't need the logic we have for the text box.
											%>
			    									<sup><span style='color: #ff0000;'>*</span></sup> 
			    							<%	}%> 
										</td>
										<td style="width:500px">
											<span class='normalText'>
											<input  class='form-control' type="text"  name="set<%=a.getAttributeId()%>" id="set<%=a.getAttributeId()%>" size="10"
											 maxlength="100">
											 </span>
										</td>
										<td align='left'> 
											<a href='#'  class='btn btn-sm btn-danger'  style='color:white' onclick='bulkEditActionForm("setCustomDate:##:<%=a.getAttributeId()%>", "<%=dRAFolderId%>")'>
											Update </a>
										</td> 				
									</tr>	
									<%		
								}
								else if (a.getAttributeType().equals("Number")){
									%>
									<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
										<td style="width:200px">
											<span class='headingText'> Set <b><%=a.getAttributeName()%></b> to</span>
											<%
												if (a.getAttributeRequired() ==1 ){
													// date fild js checker already checks for empty fields. so
													// we don't need the logic we have for the text box.
											%>
			    									<sup><span style='color: #ff0000;'>*</span></sup> 
			    							<%	}%> 
										</td>
										<td style="width:500px">
											<span class='normalText'>
											<input class='form-control' type="text"  name="set<%=a.getAttributeId()%>" id="set<%=a.getAttributeId()%>" size="10"
											 maxlength="100">
											 </span>
										</td>
										<td align='left'> 
											<a href='#'  class='btn btn-sm btn-danger'  style='color:white'  onclick='bulkEditActionForm("setCustomNumber:##:<%=a.getAttributeId()%>", "<%=dRAFolderId%>")'>
											Update </a>
										</td> 				
									</tr>	
									<%		
								}								
								else {
									
									// Text is a special case of custom attribute. Similar to dropdown multiple, 
									// lets give text attributes the ability to add / remove text
								%>
									<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
										<td style="width:200px">
											<span class='headingText'> Set <b><%=a.getAttributeName()%></b> to</span>
											<%
												String required = "";
												if (a.getAttributeRequired() ==1 ){
													required = "Required";
											%>
			    									<sup><span style='color: #ff0000;'>*</span></sup> 
			    							<%	}%> 
										</td>
										<td style="width:500px">
											<span class='normalText'>
											 <textarea  class='form-control' name="set<%=a.getAttributeId()%>" id="set<%=a.getAttributeId()%>" rows='2' cols='50'></textarea>
											 </span>
										</td>
										<td align='left'> 
											<a href='#'  class='btn btn-sm btn-danger'  style='color:white' onclick='bulkEditActionForm("setCustomText<%=required%>:##:<%=a.getAttributeId()%>", "<%=dRAFolderId%>")'>
											Update </a>
										</td> 				
									</tr>	
								
								
								
									<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
											<td style="width:200px">
												<span class='headingText'> Append <b><%=a.getAttributeName()%></b> to</span>
												<%
													required = "";
													if (a.getAttributeRequired() ==1 ){
														required = "Required";
												%>
				    									<sup><span style='color: #ff0000;'>*</span></sup> 
				    							<%	}%> 
											</td>
											<td style="width:500px">
												<span class='normalText'>
												 <textarea  class='form-control' name="append<%=a.getAttributeId()%>" id="append<%=a.getAttributeId()%>" rows='2' cols='50'></textarea>
												 </span>
											</td>
											<td align='left'> 
												<a href='#'  class='btn btn-sm btn-danger'  style='color:white' onclick='bulkEditActionForm("appendCustomText<%=required%>:##:<%=a.getAttributeId()%>", "<%=dRAFolderId%>")'>
												Append </a>
											</td> 				
										</tr>	
										
									<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
											<td style="width:200px">
												<span class='headingText'> Replace <b><%=a.getAttributeName()%></b> to</span>
												<%
													required = "";
													if (a.getAttributeRequired() ==1 ){
														required = "Required";
												%>
				    									<sup><span style='color: #ff0000;'>*</span></sup> 
				    							<%	}%> 
											</td>
											<td style="width:500px">
												<span class='normalText'>
												 <textarea  class='form-control' name="replace<%=a.getAttributeId()%>" id="replace<%=a.getAttributeId()%>" rows='1' cols='30'></textarea>
												 &nbsp;&nbsp; With &nbsp;&nbsp;
												 <textarea  class='form-control' name="replaceWith<%=a.getAttributeId()%>" id="replaceWith<%=a.getAttributeId()%>" rows='1' cols='30'></textarea>
												 
												 </span>
											</td>
											<td align='left'> 
												<a href='#'  class='btn btn-sm btn-danger'  style='color:white' onclick='bulkEditActionForm("replaceCustomText<%=required%>:##:<%=a.getAttributeId()%>", "<%=dRAFolderId%>")'>
												Replace </a>
											</td> 				
										</tr>
								<%
								}
								%>
								</table>
								</div>
								<%
							}
							%>
							
						</div>
				
					<!--  Traceability and Other Misc actions -->
						
						<div id ='traceabilityDiv'  style="display:none;">
							<table style='width:900px' class='table table-striped table-hover table-bordered' align='left' >							
								<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
									<td width='300' style="width:300px" align='left' >
									 	<span class='headingText'>Set TraceTo To (Eg : PR-1,br-2)</span>				
									</td>
									<td width='150' style="width:150px">
										<input type='text' name='setTraceTo' id='setTraceTo' size="10"  maxlength="100">
									</td>
									<td align='left'> 
										<a href='#'  class='btn btn-sm btn-danger'  style='color:white' onclick='bulkEditActionForm("setTraceTo", "<%=dRAFolderId%>")'>
										Update </a>
									</td>
								</tr>
								<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
									<td width='300' style="width:300px" align='left' >
									 	<span class='headingText'>Set TraceFrom To (Eg : PR-1,br-2)</span>				
									</td>
									<td width='150' style="width:150px">
										<input type='text' name='setTraceFrom' id='setTraceFrom' size="10"  maxlength="100">
									</td>
									<td align='left'> 
										<a href='#'  class='btn btn-sm btn-danger'  style='color:white' onclick='bulkEditActionForm("setTraceFrom", "<%=dRAFolderId%>")'>
										Update </a>
									</td>
								</tr>
								<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
									<td align='left' colspan='3'>
										<a href='#' onclick='bulkEditActionForm("clearSuspectTraceTo", "<%=dRAFolderId%>")'>
										Clear Suspect Trace To </a>
									</td>
								</tr>
								<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
									<td align='left' colspan='3'>
										<a href='#' onclick='bulkEditActionForm("clearSuspectTraceFrom", "<%=dRAFolderId%>")'>
										Clear Suspect Trace From 
										</a>
									</td>
								</tr>
								<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
									<td align='left' colspan='3'>
										<a href='#' onclick='bulkEditActionForm("deleteAllTraceTo", "<%=dRAFolderId%>")'>
										Delete All Trace To
										</a>
									</td>
								</tr>
								<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
									<td align='left' colspan='3'>
										<a href='#' onclick='bulkEditActionForm("deleteAllTraceFrom", "<%=dRAFolderId%>")'>
										Delete All Trace From
										</a>
									</td>
								</tr>
								
							</table>
						</div>
				
				<div id ='workFlowDiv' style="display:none;">
					<table  style='width:900px' class='table table-striped table-hover table-bordered'  align='left' >
						<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
							<td colspan='3' onclick='bulkEditActionForm("submitRequirementForApproval", "<%=dRAFolderId%>")'
							 style="cursor:pointer" title="Start Approval Flow for these objects"
							>					
								<span class="normalText"><font color="blue">Submit  For Approval</font></span>
							</td>
						</tr>					
						<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
							<td colspan='3' onclick='bulkEditActionForm("approveRequirement", "<%=dRAFolderId%>")'
							style="cursor:pointer" title="I Approve these objects"
							>	
								<span class="normalText"><font color="blue">I Approve</font></span>
							</td>
						</tr>
						<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
							<td colspan='3' onclick='
	        						var approvalNote=prompt("Please enter a reason for rejection (Required)","");
									if ((approvalNote!=null) && (approvalNote.length > 0 )){
										bulkEditActionForm("rejectRequirement" + ":##:" + approvalNote, "<%=dRAFolderId%>");
									}
									else {
	        							alert("You must provide a reason for rejecting these requirements.");
	        						}
	        					'
	        					style="cursor:pointer" title="I Do NOT Approve these objects"
	        					>
	        					<span class="normalText"><font color="blue">I DO NOT Approve</font></span>
							</td>
						</tr>
						
						<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
							<td colspan='3' onclick='bulkEditActionForm("refreshApproverList", "<%=dRAFolderId%>")'
							style="cursor:pointer" title="Refresh the list of approvers for these objects"
	        					
							>					
								<span class="normalText"><font color="blue">Refresh Approver List</font></span>
							</td>
						</tr>					
						
						<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
							<td colspan='3' 
							onclick='bulkEditActionForm("remindApprovers", "<%=dRAFolderId%>")'
							style="cursor:pointer" title="Remind pending approvers"
	        				
							>				
								<span class="normalText"><font color="blue">Remind Pending Approvers</font></span>
							</td>
						</tr>	
						<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
							<td title="Use this feature to dynamically add approver roles to individual requirements">
	        					<span class="normalText">Set Dynamic Approval Role <font color='red'><br>
	        					 
							</td>
							<td width='150' style="width:150px">
								<span class='normalText'>
								<select name="setDynamicApprovalRole" id="setDynamicApprovalRole">
									<%
										ArrayList roles = RoleUtil.getRoles(dRAProject.getProjectId()); 
										Iterator r = roles.iterator();
										while (r.hasNext()){
											Role role = (Role) r.next();
										%>
											<option value='<%=role.getRoleId() %>'><%=role.getRoleName()%></option>
										<%
									}
									%>
								</select>	
								<br>
								Approval Rank &nbsp;&nbsp;<input type='text' value='1' id='dynamicApprovalRank' size='3'>				
								</span>
																
							
							</td>
							<td align='left'>
								<a href='#'  class='btn btn-sm btn-danger'  style='color:white' 
									onclick='bulkEditActionForm("setDynamicApprover", "<%=dRAFolderId%>")'>
								Update</a> 
								
							</td>
						</tr>
						<tr onmouseout="this.style.background='white';" onmouseover="this.style.background='#E5EBFF';">
							<td title="Use this feature to dynamically add approver roles to individual requirements">
	        					<span class="normalText">Remove Dynamic Approval Role</span>
							</td>
							<td width='150' style="width:150px">
								<span class='normalText'>
								<select name="removeDynamicApprovalRole" id="removeDynamicApprovalRole">
									<%
										
										r = roles.iterator();
										while (r.hasNext()){
											Role role = (Role) r.next();
										%>
											<option value='<%=role.getRoleId() %>'><%=role.getRoleName()%></option>
										<%
									}
									%>
								</select>					
								</span>
																
							
							</td>
							<td align='left'>
								<a href='#'  class='btn btn-sm btn-danger'  style='color:white' 
									onclick='bulkEditActionForm("removeDynamicApprover", "<%=dRAFolderId%>")'>
								Update</a> 
								
							</td>
						</tr>				
					</table>
				</div>
			</td>
		</tr>
		
		<%
		}
		%>
	</table>
	
  
  </form>
<%}%>