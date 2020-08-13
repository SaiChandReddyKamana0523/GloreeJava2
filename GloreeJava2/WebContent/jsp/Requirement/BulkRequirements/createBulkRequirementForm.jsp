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
	if ((isLoggedIn == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
%>
	
	
	
	<%
		ArrayList requirementTypes = project.getMyRequirementTypes();
		
		String folderIdString = request.getParameter("folderId");
		int folderId = Integer.parseInt(folderIdString);
		Folder folder = new Folder(folderId);	
	
		RequirementType  currentRequirementType = new RequirementType(folder.getRequirementTypeId());
		if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" 
				+ folder.getFolderId()))){
	%>
			<div class="actionPrompt">	
				<span class="subSectionHeadingText">
				You do not have CREATE permissions on this folder. Please work with your Project Administrator
				to get access to this folder.
				</span>
			</div>
	<%
			return;
		} 
	%>
	<form method='post' action='/GloreeJava2/servlet/RequirementAction'>
	<div  id='createBulkRequirementsFormDiv' >
		<input type="hidden" name="action" id="action" value="createBulkRequirements">
		<input type="hidden" name="projectId" id="projectId" value= "<%=project.getProjectId()%>">
		<input type="hidden" name="requirementTypeId" id="requirementTypeId" value= "<%=folder.getRequirementTypeId()%>">
		<input type="hidden" name="currentFolderId" id="currentFolderId" value="<%=request.getParameter("folderId") %>" >
		<input type="hidden" name="requirementDescriptionSeqStart" id="requirementDescriptionSeqStart" value="0">
		<input type="hidden" name="requirementPrioritySeqStart" id="requirementPrioritySeqStart" value="0">
		<input type="hidden" name="requirementOwnerSeqStart" id="requirementOwnerSeqStart" value="0">
		<input type="hidden" name="requirementPctCompleteSeqStart" id="requirementPctCompleteSeqStart" value="0">
		<input type="hidden" name="requirementExternalURLSeqStart" id="requirementExternalURLSeqStart" value="0">
		<input type="hidden" name="requirementParentSeqStart" id="requirementParentSeqStart" value="0">
		<input type="hidden" name="requirementTraceToSeqStart" id="requirementTraceToSeqStart" value="0">
		<input type="hidden" name="requirementTraceFromSeqStart" id="requirementTraceFromSeqStart" value="0">
		<%
		String mandatoryAttributeNames = "";
		String mandatoryDateAttributeNames = "";
		// lets put hidden seq ids for all custom attributes in this req type.
		ArrayList rTAttributes = currentRequirementType.getAllAttributesInRequirementType();
		Iterator rTAs = rTAttributes.iterator();
		while (rTAs.hasNext()){
			RTAttribute rTAttribute = (RTAttribute) rTAs.next();
			%>
			<input type="hidden" name="<%=rTAttribute.getAttributeName()%>SeqStart" id="<%=rTAttribute.getAttributeName()%>SeqStart" value="0">
			<%
		}
		
		%>
		<table class='table'  >
			<tr>
				<td colspan='2' align='left' bgcolor='#99CCFF'>				
					<span class='subSectionHeadingText'>
					Create New <%=folder.getRequirementTypeName() %>
					</span>
				</td>
			</tr>	
		
			<%
			// lets see if we got here after createing some reqs. if so we need to display the success message.
			ArrayList errorMessages = (ArrayList) session.getAttribute("errorMessages");
			ArrayList createdRequirements = (ArrayList) session.getAttribute("createdRequirements");
			
			// lets remove the attribs from session.
			session.removeAttribute("errorMessages");
			session.removeAttribute("createdRequirements");
			if ((errorMessages != null) && (errorMessages.size() > 0)){
			%>
			<tr>
				<td colspan='2' >				
					<table>
						<tr>
							<td>
							<span class='sectionHeadingText'>
							Error Messages : 
							</span>
							</td>
						</tr>
						<%
						Iterator eM = errorMessages.iterator();
						while (eM.hasNext()){
							String errorMessage = (String) eM.next();
							%>
							<tr>
								<td>
								<span class='normalText'>
								<%=errorMessage %>
								</span>
								</td>
							</tr>
							
							<%
						}
						%>
					</table>
				</td>
			</tr>	
			
			<%	
			}
			%>


			<%
			if ((createdRequirements != null) && (createdRequirements.size() > 0)){
			%>
			<tr>
				<td colspan='2' >				
					<table>
						<tr>
							<td colspan='7'>
							<span class='sectionHeadingText'>
							Newly Created Requirements :<br>
							</span>
							</td>
						</tr>
						<tr>
							<td class='tableHeader' width='350'>
								<span class='sectionHeadingText'>
								Requirement 
								</span>
							</td>
							<td class='tableHeader'>
								<span class='sectionHeadingText'>
								Owner
								</span>
							 </td>
							<td class='tableHeader'> 
								<span class='sectionHeadingText'>
								% Complete
								</span>
							</td>
							<td class='tableHeader'> 
								<span class='sectionHeadingText'>
								Priority
								</span>
							</td>
							<td class='tableHeader'> 
								<span class='sectionHeadingText'>
								Approval Status 
								</span>
							</td>
							<td class='tableHeader'> 
								<span class='sectionHeadingText'>
								Testing Status 
								</span>
							</td>
							<td class='tableHeader'> 
								<span class='sectionHeadingText'>
								Folder
								</span>
							</td>
						</tr>				 
						
						<%
						Iterator cR = createdRequirements.iterator();
						int j = 0;
				    	String cellStyle = "normalTableCell";
				    	
						while (cR.hasNext()){
							j++;
				    		if ((j%2) == 0){
				    			cellStyle = "normalTableCell";
				    		}
				    		else {
				    			cellStyle = "altTableCell";	
				    		}

							Requirement r = (Requirement) cR.next();
							String displayRDInReportDiv = "displayRDInReportDiv" + r.getRequirementId();
							%>
				 				<tr>
							 		<td class='<%=cellStyle%>'>
				 						<%
				 						// lets put spacers here for child requirements.
				 						  String req = r.getRequirementFullTag();
				 					   	  int start = req.indexOf(".");
							    		  while (start != -1) {
							    	            start = req.indexOf(".", start+1);
												out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");				    	         
							  	          }
				 						%>							 		
							 		
							 			<span>
							 			<a href="#" onclick= 'displayRequirementDescription(<%=r.getRequirementId()%>
							 				,"<%=displayRDInReportDiv%>")'> 
		 								<img src="/GloreeJava2/images/search16.png"  border="0">
		 								</a>
		 								
		 								<a href="#" 
		 								onClick='
		 									displayFolderInExplorer("<%=folderId %>");
		 									displayFolderContentCenterA("<%=folderId %>");
		 									displayFolderContentRight("<%=folderId %>");
		 									displayRequirement(<%=r.getRequirementId()%>);'>
		 								<img src="/GloreeJava2/images/puzzle16.gif" border="0">
		 								&nbsp;<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %></a> 
		 								</span>
							 		</td>
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<%=r.getRequirementOwner()%>
							 			</span>
							 		</td>
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<%=r.getRequirementPctComplete()%> %
							 			</span>
							 		</td>
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<%=r.getRequirementPriority()%>
							 			</span>
							 		</td>
										<% if (r.getApprovalStatus().equals("Draft")){ %>
											<td bgcolor='#FFFF66''>
												<span class='normalText'>
													<%=r.getApprovalStatus() %>
												</span>
											</td>										
										<%} %>
										<% if (r.getApprovalStatus().equals("In Approval WorkFlow")){ %>
											<td bgcolor='#99ccff'>
												<span class='normalText'>												
													<%=r.getApprovalStatus() %>
												</span>
											</td>
										<%} %>
										<% if (r.getApprovalStatus().equals("Approved")){ %>
											<td bgcolor='#CCFF99''>
												<span class='normalText'>
													<%=r.getApprovalStatus()%>
												</span>
											</td>
										<%} %>
										<% if (r.getApprovalStatus().equals("Rejected")){ %>
											<td bgcolor='#FFA3AF'>
												<span class='normalText'>
													<%=r.getApprovalStatus()%>
												</span>
											</td>
										<%} %>	
										
										
										<% if (r.getTestingStatus().equals("Pending")){ %>
											<td bgcolor='#FFFF66'>
												<span class='normalText'>
													<%=r.getTestingStatus() %>
												</span>
											</td>										
										<%} %>
										<% if (r.getTestingStatus().equals("Pass")){ %>
											<td bgcolor='#CCFF99'>
												<span class='normalText'>												
													<%=r.getTestingStatus() %>
												</span>
											</td>
										<%} %>
										<% if (r.getTestingStatus().equals("Fail")){ %>
											<td bgcolor='#FFA3AF'>
												<span class='normalText'>
													<%=r.getTestingStatus()%>
												</span>
											</td>
										<%} %>
										
										
										
																 		
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<%=r.getFolderPath()%>
							 			</span>
							 		</td>
				 				</tr>
				 				<tr>
				 					<td  class='<%=cellStyle%>'  colspan='6'>
				 						<div id = '<%=displayRDInReportDiv%>'> </div>
				 					</td>
				 				</tr>				 				
							<%
						}
						%>
					</table>
				</td>
			</tr>	
			
			<%	
			}
			if ((errorMessages == null) && (createdRequirements == null)){
			%>

			<tr>
				<td colspan='2' align='left'>
					<span class='normalText'>
					Requirement Type
					</span>
					&nbsp;&nbsp;&nbsp;
					<span class='normalText'>
					<select id='folderId' 
					onChange='changeFolderAndDisplayBulkRequirementForm();'>
						<%
						Iterator rT = requirementTypes.iterator();
						while (rT.hasNext()){
							RequirementType requirementType = (RequirementType) rT.next();
							if (requirementType.getRequirementTypeId() == folder.getRequirementTypeId()){
							%>
								<option SELECTED value='<%=requirementType.getRootFolderId() %>'><%=requirementType.getRequirementTypeName() %></option>	
							<%
							}
							else {
								%>
								<option value='<%=requirementType.getRootFolderId()%>'><%=requirementType.getRequirementTypeName() %></option>	
								<%								
							}
						}
						
						%>
						
					</select>
				</span>
				</td>
			</tr>

			<tr> 
				<td colspan='2' align='left'>
					<span class='normalText'>
					More Attributes&nbsp;
					</span>
					
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<span class='normalText'>
					<select id='selectColumnsToDisplay' onChange='
						createBulkRequirementsFormTable(<%=folder.getRequirementTypeId()%>);'
						>
						<option></option>
						<option value='requirementDescription'> Description</option>
						<option value='requirementParent'> Parent</option>
						<option value='requirementOwner'> Owner</option>
						<option value='requirementPctComplete'> Percent Complete</option>
						<option value='requirementExternalURL'>External URL</option>
						<option value='requirementFolder'>Folder</option>
						<%
						// lets put hidden seq ids for all custom attributes in this req type.
						rTAs = rTAttributes.iterator();
						while (rTAs.hasNext()){
							RTAttribute rTAttribute = (RTAttribute) rTAs.next();
							// lets display only the non mandatory and non system custom attribs in the add/remove pull down
							if ((rTAttribute.getAttributeRequired() ==0) && (rTAttribute.getSystemAttribute() == 0)){
								%>
								<option value='<%=rTAttribute.getAttributeName()%>'><%=rTAttribute.getAttributeName()%></option>
								<%
							}
						}
						
						%>
					</select>
				</span>
				</td>
			</tr>
			
			
			<tr> 
				<td colspan='2'>
					<table class='table table-striped' id='newRequirementsTable' >
						<tr>
							<td colspan='2' ><sup><span style="color: #ff0000;">*</span></sup>
							<span class='normalText'><%=currentRequirementType.getRequirementTypeName()%> Name</span>
							 </td>



							<td width='200px' class="requirementTraceTo">
								<span class='normalText' >						
								<a href="#" 
										onClick='
											var allElements = document.getElementsByTagName("*");
											for (j = 0; j< allElements.length; j++){
												if (allElements[j].className == "requirementTraceTo"){
													allElements[j].style.display = "none";
												}
											}
											// lets bump up the displaysequence number
											increaseDisplaySequence("requirementTraceToSeqStart");
											var selectColumnsToDisplayObject = document.getElementById("selectColumnsToDisplay");
											selectColumnsToDisplayObject.selectedIndex = 0;
											selectColumnsToDisplayObject.options[selectColumnsToDisplayObject.length] = new Option("Trace To","requirementTraceTo");
										'>
										<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									Trace To
								</span>
							</td>

							<td   class="requirementTraceFrom">
								<span class='normalText' >						
								<a href="#" 
										onClick='
											var allElements = document.getElementsByTagName("*");
											for (j = 0; j< allElements.length; j++){
												if (allElements[j].className == "requirementTraceFrom"){
													allElements[j].style.display = "none";
												}
											}
											// lets bump up the displaysequence number
											increaseDisplaySequence("requirementTraceFromSeqStart");
											var selectColumnsToDisplayObject = document.getElementById("selectColumnsToDisplay");
											selectColumnsToDisplayObject.selectedIndex = 0;
											selectColumnsToDisplayObject.options[selectColumnsToDisplayObject.length] = new Option("Trace From","requirementTraceFrom");
										'>
										<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									Trace From
								</span>
							</td>

							<td colspan='2' class="requirementPriority">
								<span class='normalText' >						
								<a href="#" 
										onClick='
											var allElements = document.getElementsByTagName("*");
											for (j = 0; j< allElements.length; j++){
												if (allElements[j].className == "requirementPriority"){
													allElements[j].style.display = "none";
												}
											}
											// lets bump up the displaysequence number
											increaseDisplaySequence("requirementPrioritySeqStart");
											var selectColumnsToDisplayObject = document.getElementById("selectColumnsToDisplay");
											selectColumnsToDisplayObject.selectedIndex = 0;
											selectColumnsToDisplayObject.options[selectColumnsToDisplayObject.length] = new Option("Priority","requirementPriority");
										'>
										<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									Priority
								</span>
							</td>


						
		
							<%
							// lets print all the mandatory custom attributes.
							rTAs = rTAttributes.iterator();
							while (rTAs.hasNext()){
								RTAttribute rTAttribute = (RTAttribute) rTAs.next();
								if ((rTAttribute.getAttributeRequired() == 1) && (rTAttribute.getSystemAttribute() == 0)) {
									// required and not a system attribute
								%>
								<td  colspan='2'>
								
								<sup><span style="color: #ff0000;">*</span></sup>
								<span class='normalText'><%=rTAttribute.getAttributeName()%></span>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								 </td>
								<%			
								}
							}
							%> 
						
						</tr>
						
						<%for (int i=0; i<10; i++){ %>
							<tr>
								<td >
								
									<span class='normalText'>
									<input type="text"  name="requirementName<%=i%>" id="requirementName<%=i%>" size="50" 
									onChange='
										// current descriptionObjects name is actually reqDescription + its current start seq + i
										var descriptionSeqStart = document.getElementById("requirementDescriptionSeqStart").value ;
										var descriptionCellId = (descriptionSeqStart * 1) + <%=i%>;
										var descriptionObject = document.getElementById("requirementDescription" + descriptionCellId);
										var nameObject = document.getElementById("requirementName<%=i%>");
										
										if (descriptionObject.value == ""){
											descriptionObject.value = nameObject.value;
										}
									'>
									</span>
								</td>
								<td>
									<a href="#" 
						 				onClick='
							 				var currentValue = document.getElementById("requirementName<%=i%>").value;
							 				for (i = <%=i%>; i<10; i++){
							 					document.getElementById("requirementName" + i).value = currentValue;
							 				}
							 			'>
						 				<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
						 			
									
								</td>



								<td  class="requirementTraceTo" align='right'  >
									<div id='requirementTraceTo<%=i%>Div'>
										<table><tr>
										<td>
										<div id="validateTraceToDiv<%=i%>" style="float:left"></div>	
										</td>
										<td>
										<span class='normalText'>
										<input type="text" class="requirementTraceTo" name="requirementTraceTo<%=i%>" 
										id="requirementTraceTo<%=i%>" size="10"
										onfocus='document.getElementById("validateTraceToDiv<%=i%>").style.display="none";'
										onblur='
											var traceTo = document.getElementById("requirementTraceTo<%=i%>").value;
											if (traceTo!= ""){
												validateTraceToInBulkEdit(traceTo,"validateTraceToDiv<%=i%>");
											}
										'>
										</td>
										<td>
										<a href="#"  onClick='copyDynamicCellValuesDown("textBox","requirementTraceTo", <%=i%>)'>
											<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
										</td>
										</tr></table>
									</div>
								</td>


								<td  class="requirementTraceFrom" align='right'>
									<div id='requirementTraceFrom<%=i%>Div'>
										<table><tr>
										<td>
										<div id='validateTraceFromDiv<%=i%>' style="float:left"></div>
										</td>
										<td>
										<span class='normalText'>
										<input type="text" class="requirementTraceFrom" name="requirementTraceFrom<%=i%>" 
										id="requirementTraceFrom<%=i%>" size="10" 
										onfocus='document.getElementById("validateTraceFromDiv<%=i%>").style.display="none";'
										onblur='
											var traceFrom = document.getElementById("requirementTraceFrom<%=i%>").value;
											if (traceFrom != ""){
												validateTraceFromInBulkEdit(traceFrom,"validateTraceFromDiv<%=i%>");
											}
										'>
										</span>
										</td>
										<td>
										<a href="#"  onClick='copyDynamicCellValuesDown("textBox","requirementTraceFrom", <%=i%>)'>
											<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
										</td>
										</tr></table>
									</div>
								</td>

								<td  class="requirementPriority">
									<span class='normalText'>
									<select class="requirementPriority" name="requirementPriority<%=i%>" 
									id="requirementPriority<%=i%>">
										<option value="High">High </option>
										<option value="Medium" SELECTED>Medium</option>
										<option value="Low">Low</option>
									</select>
									</span>
								</td>
								<td>
									<a href="#"  onClick='copyDynamicCellValuesDown("dropDown","requirementPriority", <%=i%>)'>
											<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
									
								</td>


									
								<%
								// lets print all the mandatory custom attributes.
								
								rTAs = rTAttributes.iterator();
								while (rTAs.hasNext()){
									RTAttribute rTAttribute = (RTAttribute) rTAs.next();
									
									
									if ((rTAttribute.getAttributeRequired() == 1) && (rTAttribute.getSystemAttribute() == 0)){
										// since the attributes are looped through i = 1..10, lets just catch them in one iteration
										if (i == 1){
											mandatoryAttributeNames += rTAttribute.getAttributeName()  + ":#:";
										}
										// this is a required and non system attribute.
										if (rTAttribute.getAttributeType().equals("Drop Down")){
											// this is a drop down. so lets display a drop down.
										%>
											<td >
											<span class='normalText'>
											<select class="<%=rTAttribute.getAttributeName() %>" name="<%=rTAttribute.getAttributeName() %><%=i%>" 
											id="<%=rTAttribute.getAttributeName() %><%=i%>">
												<option value=""></option>
												<%
												String attributeDropDownOptions = rTAttribute.getAttributeDropDownOptions();
												String [] options = attributeDropDownOptions.split(",");
												for (int j=0; j< options.length; j++){
													if (options[j].equals(rTAttribute.getAttributeDefaultValue())){
													%>
														<option SELECTED value="<%=options[j]%>"><%=options[j]%></option>
													<%
													}
													else{
													%>
														<option value="<%=options[j]%>"><%=options[j]%></option>
													<%	
													}
												}
												%>
											</select>
											</span>
											</td>
											<td>
											<a href="#"  onClick='copyDynamicCellValuesDown("dropDown","<%=rTAttribute.getAttributeName() %>", <%=i%>)'>
													<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
												
											</td>
										<%		
										}
										else if (rTAttribute.getAttributeType().equals("Drop Down Multiple")){
											%>
											<td>
											<span class='normalText'>
											<select multiple='multiple' class="<%=rTAttribute.getAttributeName()%>" name="<%=rTAttribute.getAttributeName()%><%=i%>" size='5' 
											id="<%=rTAttribute.getAttributeName()%><%=i%>">
												
												<%
												String attributeDropDownOptions = rTAttribute.getAttributeDropDownOptions();
												String [] options = attributeDropDownOptions.split(",");
												for (int j=0;j < options.length; j++){
													if (options[j].equals(rTAttribute.getAttributeDefaultValue())){
													%>
														<option SELECTED value="<%=options[j]%>"><%=options[j]%></option>
													<%
													}
													else{
													%>
														<option value="<%=options[j]%>"><%=options[j]%></option>
													<%	
													}
												}
												%>
											</select>
											</span>
											</td>
											<td>
											<a href="#"  onClick='copyDynamicCellValuesDown("dropDownMultiple","<%=rTAttribute.getAttributeName()%>", <%=i%>)'>
													<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
											</td>
											<%
										}
										else if (rTAttribute.getAttributeType().equals("Date")){
											// since the attributes are looped through i = 1..10, lets just catch them in one iteration
											if (i == 1){
												mandatoryDateAttributeNames += rTAttribute.getAttributeName()  + ":#:";
											}
											
											// lets put some javascript validation for date format.
									%>
										<td >
											<div id='<%=rTAttribute.getAttributeName() %><%=i%>Div'>	
											<span class='normalText'>
											<input type="text" class="<%=rTAttribute.getAttributeName() %>" name="<%=rTAttribute.getAttributeName() %><%=i%>" 
											id="<%=rTAttribute.getAttributeName() %><%=i%>" value="mm/dd/yyyy"'  size="10" maxlength="100"
											onfocus='
												var attribute = document.getElementById("<%=rTAttribute.getAttributeName() %><%=i%>");
												attribute.value="";
												attribute.style.backgroundColor="#FFFFFF";
												'
											onblur='
												var attribute = document.getElementById("<%=rTAttribute.getAttributeName() %><%=i%>");
												if (attribute.value != ""){
													if (isValidDate(attribute.value)==false){
														attribute.focus()
														attribute.style.backgroundColor="#FFCC99";
													}
												}'>
											</span>
											
											</span>
											</div>
										</td>
										<td>
											<a href="#"  onClick='copyDynamicCellValuesDown("textBox","<%=rTAttribute.getAttributeName() %>", <%=i%>)'>
												<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
										</td>
																	
										
								<%	
										}
										else {
									%>
										<td >
										<div id='<%=rTAttribute.getAttributeName() %><%=i%>Div'>	
										<span class='normalText'>
										<input type="text"  class="<%=rTAttribute.getAttributeName() %>" name="<%=rTAttribute.getAttributeName() %><%=i%>" 
										id="<%=rTAttribute.getAttributeName() %><%=i%>" value="<%=rTAttribute.getAttributeDefaultValue() %>" size="50" ">
										
										
										</span>
										</div>
										</td>
										<td>
											<a href="#"  onClick='copyDynamicCellValuesDown("textBox","<%=rTAttribute.getAttributeName() %>", <%=i%>)'>
											<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
										</td>
								<%	
										}
									}
								}
								%>
							</tr>
						<%} %>
					</table>
					
				</td>
			</tr>
			
			<tr>
				<td colspan='2' align='left'>
					<span class='normalText'>
						<input type='button' class='btn btn-xl btn-primary' name='createBulkRequirementsButton' id='createBulkRequirementsButton'
						value='Create Requirements' onClick='createBulkRequirements(this.form, "<%=mandatoryAttributeNames%>","<%=mandatoryDateAttributeNames%>")'>
					</span>
				</td>
			</tr>
			<%} %>		
		</table>
		
	</div>
	</form>
	<% } %>
