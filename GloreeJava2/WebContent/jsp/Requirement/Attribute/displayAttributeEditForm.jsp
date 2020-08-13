<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
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
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
    // This routine is always called with a requirementId parameter.
    int requirementId = Integer.parseInt(request.getParameter("requirementId"));
    Requirement requirement = new Requirement(requirementId, databaseType);
	
    int rowId = Integer.parseInt(request.getParameter("rowId"));
    String widthString = "";
    String colString = "";
    if (rowId<99999){
    	// this is list view
    	widthString =  "width:200px;";
    	colString = "cols='30'";
    }
    else{
    	widthString = "width:300px;";
    	colString = "cols='75'";
    }
    
    String attributeLabel = request.getParameter("attributeLabel");
    RTAttribute rTAttribute = new RTAttribute(requirement.getRequirementTypeId(), attributeLabel );
    
    RAttributeValue a = new RAttributeValue(requirementId, rTAttribute.getAttributeId(), databaseType);
    
    if (a.getAttributeName() == null){
    	// lets send email to support@tracecloud.com
    	
    	String mailHost = this.getServletContext().getInitParameter("mailHost");
		String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
		String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
		String smtpPort = this.getServletContext().getInitParameter("smtpPort");
		String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
		String emailUserId = this.getServletContext().getInitParameter("emailUserId");
		String emailPassword = this.getServletContext().getInitParameter("emailPassword");
		
		
		
    	String toUser = "support@tracecloud.com";
		String subject = "Critical : Missing requirement attribute label";
		String messageBody = "Misssed attribute Label for project id " + requirement.getProjectId() ;
		messageBody += "<br> project  " + project.getProjectName();
		messageBody += "<br> requirement  " + requirement.getRequirementFullTag();
		messageBody += "<br> attribute  " + attributeLabel;
		messageBody += "<br> Will try to get the value from the UDA and fill it attributes tables " ;
		messageBody = messageBody.replace("\n", "<br>");
		
		
		// lets send the email out to the toEmailId;
		ArrayList<String> to = new ArrayList<String>();
		to.add(toUser);
		ArrayList<String> cc = new ArrayList<String>();
		cc.add("nathan@tracecloud.com");
		MessagePacket mP = new MessagePacket(to, cc, subject, messageBody,"");
		EmailUtil.email(mP , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
		
		
    	String attributeValue = requirement.getAttributeValueFromUDA(attributeLabel);
    	requirement.setAttributeLabelFromUDA(rTAttribute.getAttributeId(), attributeValue, securityProfile.getUser().getEmailId() );
    	a = new RAttributeValue(requirementId, rTAttribute.getAttributeId(), databaseType);
    	
    }
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean readPermissions = true;
	if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
			+ requirement.getFolderId()))){
		readPermissions = false;
	}
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	// you need to be a member of this project and have read permissions before you can see this.
	if (isMember && readPermissions){
%>

	<% 
		

		///////////////////////////////SECURITY CODE ////////////////////////////
		// if the requirement worked on, doesn't belong to the project the user is 
		// currently logged into, then a user logged into project x is trying to 
		// hack into a req in project y by useing requirementId parameter.
		if (requirement.getProjectId() != project.getProjectId()) {
			return;
		}
		///////////////////////////////SECURITY CODE ////////////////////////////
		
		// lets determine which all attributes this user has permission to update , based on his roles
	  	String  updateAttributes = securityProfile.getUpdateAttributesForFolder(requirement.getFolderId());
		
		 // if the user does not have 'Update Requirements' priv for this folder
    	// we disable the Update Attributes button.
    	// Note : the user can still update attributes if his role has been given ttribute permission.
    	String updateRequirementsDisableString = "";
    	if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
    			+ requirement.getFolderId()))){
    		updateRequirementsDisableString = "disabled='disabled'";
    	}
    
    	boolean reqLockedByOthers = false;
    	// For all users, if this requirement is locked by some other user, then we need to disabled the update attributes button
    	if (
    		(!(requirement.getRequirementLockedBy().equals(""))
    		&&
    		(!(requirement.getRequirementLockedBy().equals(securityProfile.getUser().getEmailId()))))
    	){
    		
    		reqLockedByOthers = true;
    		updateRequirementsDisableString = "disabled='disabled'";
    	
    	}
    	
    	String color = "white";
		if (requirement.getUserDefinedAttributes().toLowerCase().contains("color:#:")){
			color = requirement.getAttributeValue("color");
		}
		
	    String updatedMessage = "";
	    String updated = (String) request.getAttribute("updated");

	    if ((updated != null) && (updated.equals("yes"))){
	    	updatedMessage = "Your changes have been updated in the system.";
	    }
	    
		String attributeIdString = "";
		String attributeRequiredIdString = "";
		

	    // if req is locked by someone else , lets put that message
	    if (reqLockedByOthers){
	    %>
	    	<div class='alert alert-danger'>
	    		The object is locked by <%=requirement.getRequirementLockedBy() %>
	    	</div>
	    	
	    <%
	    }
	    else if (!(requirement.getUserDefinedAttributes().contains(a.getAttributeName() + ":#:") )){
	    	System.out.println("srt debug  uda does not contain this attribute : uda are :  " + requirement.getUserDefinedAttributes() 
	    	+ " attrib name is " + a.getAttributeName() + ":#:");
	    	%>
	    	<div class='alert alert-danger'>
	    		This field has not been defined for <%=requirement.getRequirementTypeName() %> type objects
	    	</div>
	    <%	
	    }
	    else if (!(updateAttributes.contains(":#:"+ a.getAttributeName() + ":#:"))){
	    // if the user doesn't have update permissions on this req, lets put that message here
	    %>
	    	<div class='alert alert-danger'>
	    		You do not have update permissions on this attribute. Please work with your administrator
	    	</div>
	    <%
	    }
	    else {
	    %>
	    
<div > 	
	
				
				    
				  	 <% 
				  	 
			    		

			    		if (a.getAttributeType().equals("Text Box")){
							attributeIdString += a.getAttributeValueId() + "#Text##";
							if (a.getAttributeRequired() ==1 ){
								attributeRequiredIdString += a.getAttributeValueId() + "#" + a.getAttributeName() + "#TextBox" + "##";
							}
			    		}
			    		else if (a.getAttributeType().equals("Date")){
							attributeIdString += a.getAttributeValueId() + "#Date##";
							if (a.getAttributeRequired() ==1 ){
								attributeRequiredIdString += a.getAttributeValueId() + "#" + a.getAttributeName() + "#Date" + "##";
							}
						}
			    		else if (a.getAttributeType().equals("URL")){
							attributeIdString += a.getAttributeValueId() + "#URL##";
							if (a.getAttributeRequired() ==1 ){
								attributeRequiredIdString += a.getAttributeValueId() + "#" + a.getAttributeName() + "#URL" + "##";
							}
						}
			    		else if (a.getAttributeType().equals("Number")){
							attributeIdString += a.getAttributeValueId() + "#Number##";
							if (a.getAttributeRequired() ==1 ){
								attributeRequiredIdString += a.getAttributeValueId() + "#" + a.getAttributeName() + "#Number" + "##";
							}
						}
			    		else if (a.getAttributeType().equals("Drop Down")){
							attributeIdString += a.getAttributeValueId() + "#DropDown##";
							if (a.getAttributeRequired() ==1 ){
								attributeRequiredIdString += a.getAttributeValueId() + "#" + a.getAttributeName() + "#DropDown" + "##";
							}
			    		}
			    		else if (a.getAttributeType().equals("Drop Down Multiple")){
							attributeIdString += a.getAttributeValueId() + "#DropDownMultiple##";
							if (a.getAttributeRequired() ==1 ){
								attributeRequiredIdString += a.getAttributeValueId() + "#" + a.getAttributeName() + "#DropDownMultiple" + "##";
							}
			    		}
			    		
			    	 
			    	 int countOfAttributes = 0;
			    	 countOfAttributes++;
			    		
			    		
			    		
			    		
			    		
			    		// lets remove any ' from the display attrib value
			    		// we need to do this as we want to display it as part of span title.
			    		String displayAttributeValue = a.getAttributeEnteredValue();
			    		displayAttributeValue.replace("'","");
						
			    		
			    		
						// NOTE this is important : 
						// by default , every attribute is disabled. it gets enabled only if the user
						// has updateAttributes permissions on this attribute
						// in this folder.
						// Once we go through the user privileges and make some attributes updatable
						// we go through the users other permissions / system attributes etc.. and disable them again.
						// These have to be done in this sequence for it to work.
						String disabled = "disabled='DISABLED'";
						boolean isAttributeDisabled = true;
						// If the user does not have updateAttributes permission on this attribute in this folder, then we need to disable it.
				    	// this can happen at an attribute level
				    	
				    	if 	(updateAttributes.contains(":#:"+ a.getAttributeName() + ":#:")){
				    		// SINCE THE USER HAS UPDATE PERMISSIONS ON THIS ATTRIBUTE ON THIS REQ type in this folder, 
				    		// lets enable this attribute for updating.
				    		disabled = "";
				    		isAttributeDisabled = false;
				    	}
			    		
						String dateFormat = "(mm/dd/yyyy)";
						// if the attribute is a shared requirement attribute, then disable it.
						if (a.getSystemAttribute() == 1) {
							disabled = "disabled='DISABLED'";
							isAttributeDisabled = true;
							dateFormat = "<font color='gray'> (mm/dd/yyyy) </font>";
						}
						
						
				    	
				    	// if req is locked by others, then all attributes get set to disabled.
				    	if (reqLockedByOthers){
				    		disabled = "disabled='disabled'";
				    		isAttributeDisabled = true;
				    	}
						
				    	String escapedAttributeEnteredValue = a.getAttributeEnteredValue().replace("\"","&quot");
				    	String formattedAttributeEnteredValue = a.getAttributeEnteredValue().replaceAll("\n","<br>");
				    	
			    		if (a.getAttributeType().equals("Text Box")){
			    			int rowSize = 1;
			    			try {
			    				rowSize = (a.getAttributeEnteredValue().length() / 60);
			    				if (rowSize == 0 ){
			    					rowSize += 2;
			    				}
			    				else {
			    					rowSize += 2;
			    				}
			    			}
			    			catch (Exception ex) {
			    				rowSize = 1;
			    				ex.printStackTrace();
			    			}
			    			%>
			    			
			    				<div id='displayAttributeDiv<%=a.getAttributeId()%>' style='display:none; <%=widthString%>'>
			    					<span class='normalText' 
			    					><%=formattedAttributeEnteredValue%></span>
			    				</div>
			    				<div id='editAttributeDiv<%=a.getAttributeId()%>' style=' <%=widthString%>'> 	
			    					<span class='normalText'>  
			    					<textarea <%=disabled%> rows='<%=rowSize %>' <%=colString %> 
			    						id='<%=requirement.getRequirementId()%>-<%=a.getAttributeValueId()%>' ><%=a.getAttributeEnteredValue() %></textarea>
			    					<%
			    					if (a.getAttributeName().trim().equals("Keep Me Informed")){
			    						%>
			    						Enter comma separated email addresses.
			    						<%
			    					}
			    					%>
			    					</span> 
			    				</div>
							
			    			
			    		<%
			    		}
			    		else if (a.getAttributeType().equals("Date")){
			    			%>
			    		
			    				<div id='displayAttributeDiv<%=a.getAttributeId()%>' style='display:none; <%=widthString%>'>
			    					<span class='normalText' 
			    					><%=a.getAttributeEnteredValue()%></span>
			    				</div>
			    				<div id='editAttributeDiv<%=a.getAttributeId()%>' style=' <%=widthString%>'> 	
			    					<span class='normalText'>  
			    						<input <%=disabled%> type='text' size='10' value='<%=a.getAttributeEnteredValue()%>' 
			    							id='<%=requirement.getRequirementId()%>-<%=a.getAttributeValueId()%>' >
			    						<%=dateFormat %>
			    					</span> 
			    				</div>
							
							<%
							
						}
			    		else if (a.getAttributeType().equals("URL")){
			    			// if the url didn't start with http:// , lets add it.
							String attributeURL = a.getAttributeEnteredValue();
			    			
							
							%>
							
			    			
			    				<div id='displayAttributeDiv<%=a.getAttributeId()%>' style='display:none; <%=widthString%>;'>
			    					<span class='normalText' 
			    					><%=a.getAttributeEnteredValue()%></span>
			    					
									<%if ((a.getAttributeEnteredValue() != null) && (a.getAttributeEnteredValue().length() > 0 )) {
										if (attributeURL.contains("\n")){
											String[] urls = attributeURL.split("\n");
											for (String url : urls){
												%>
												<br>
												<div>
													<a href='#' onclick='window.open("<%=url%>","")'> <%=url %> </a>
												</div>
												<%
											}
										}
										else{
									%>
										<br>
										<div>
											<a href='#' onclick='window.open("<%=attributeURL%>","")'>  Go here</a>
										</div>
										<br>
									<%
										}
									} %>
			    				</div>
			    			
			    			
			    				<div id='editAttributeDiv<%=a.getAttributeId()%>' style=' <%=widthString%>; float:left'> 
									
									<textarea rows='5' cols='45' <%=disabled%> 
										id='<%=requirement.getRequirementId()%>-<%=a.getAttributeValueId()%>' ><%=a.getAttributeEnteredValue() %></textarea> 
									<%if ((a.getAttributeEnteredValue() != null) && (a.getAttributeEnteredValue().length() > 0 )) {
										if (attributeURL.contains("\n")){
											String[] urls = attributeURL.split("\n");
											for (String url : urls){
												%>
												<br>
												<div>
													<a href='#' onclick='window.open("<%=url%>","")'> <%=url %> </a>
												</div>
												<%
											}
										}
										else{
									%>
										<br>
										<div>
											<a href='#' onclick='window.open("<%=attributeURL%>","")'>  Go here</a>
										</div>
									<%}
									}%>
								</div>
								
								
							
							<%
							
							
						}
			    		else if (a.getAttributeType().equals("Number")){
						%>
			    			
			    				<div id='displayAttributeDiv<%=a.getAttributeId()%>' style='display:none; <%=widthString%>'>
			    					<span class='normalText' 
			    					><%=a.getAttributeEnteredValue()%></span>
			    				</div>
			    				<div id='editAttributeDiv<%=a.getAttributeId()%>' style='<%=widthString%>'> 	
			    					<span class='normalText'>  
			    						<input <%=disabled%> type='text' size='10' value='<%=a.getAttributeEnteredValue()%>' 
			    							id='<%=requirement.getRequirementId()%>-<%=a.getAttributeValueId()%>' >
			    						Number
			    					</span> 
			    				</div>
							
						<%
						}
			    		else if (a.getAttributeType().equals("Drop Down Multiple")){
			    			
				    		String [] o = a.getAttributeDropDownOptions().split(",");
			    			
			    			int sizeOfDropDown = o.length;
			    			if (sizeOfDropDown == 0 ) {
			    				sizeOfDropDown = 1;
			    			}
			    			// if this attribute has a parentAttribute, then the dropdown options we display 
			    			// and the size of multiple dropdown is different
			    			// lets try to figure it out
			    			int parentAttributeId =  rTAttribute.getParentAttributeId() ;
							if (parentAttributeId > 0 ){
								sizeOfDropDown = 1;
								RAttributeValue parentRAttributeValue = new RAttributeValue(requirementId, parentAttributeId,databaseType);
					    		String parentAttributeValue = parentRAttributeValue.getAttributeEnteredValue();
					    		
					    		for (int i=0 ; i < o.length; i++){
									String optionName = o[i];
									// Sample : optionName is Porsche:911
									boolean validChild = ProjectUtil.isValidChildOption(optionName,parentAttributeValue);
						    		if (validChild){
										sizeOfDropDown++;
									}
								}
							
							}
			    			
			    			%>
			    			
			    			
			    			
			    				<div id='displayAttributeDiv<%=a.getAttributeId()%>' style='display:none; <%=widthString%>'>
			    					<span class='normalText' 
			    					><%=a.getAttributeEnteredValue()%></span>
			    				</div>
			    			
			    				<div id='editAttributeDiv<%=a.getAttributeId()%>' style='<%=widthString%>'> 	
				    				<span class='normalText'   >
										<select multiple='MULTIPLE' <%=disabled%>  
											id='<%=requirement.getRequirementId()%>-<%=a.getAttributeValueId()%>' SIZE='<%=sizeOfDropDown%>' >
						
										<%
										
										
										for (int i=0 ; i < o.length; i++){
											String optionName = o[i];

											// we have a parent child attributes concept for Drop Down Multiple values.
											// if this is a child attribute, then we only show the options that are driven by the parent attribute's value.
										
											// Sample: parentAtttibuteValues is Porsche,Jaguar  
											// Sample : attributedropdownopations are Porsche:911,Porsche:Carrera,Porsche:Panamera,Porsche:Cayanne,Jaguar:XJ,Jaguar:XK,Jaguar:Vanden Plas,Cadillac:Escalade,Cadillac:CTS,Cadillac:DTS,Volkswagen:Phaeton,Volkswagen:Passat,Volkswagen:Toureg,
													
											if (parentAttributeId > 0 ){
									    		RAttributeValue parentRAttributeValue = new RAttributeValue(requirementId, parentAttributeId,databaseType);
									    		String parentAttributeValue = parentRAttributeValue.getAttributeEnteredValue();
									    		
												// Sample : optionName is Porsche:911
												boolean validChild = ProjectUtil.isValidChildOption(optionName,parentAttributeValue);
									    		
												if (!validChild){
													continue;
												}
												
												// this attribute has a parent attribute.
												// so the dropdown values have a : in them. we need to remove the content before the :, eg : Porsche:911,Porsche:carrera need to be 911,carrera.
												if ((optionName != null) && (optionName.contains(":"))){
													String [] oN = optionName.split(":");
													optionName = oN[1];
												}
											}
											
											
											
											String userEnteredAttributeValue  = a.getAttributeEnteredValue();
											if (userEnteredAttributeValue == null){
												userEnteredAttributeValue = "";
											}
											if (userEnteredAttributeValue.trim().contains(optionName.trim())){
												// this option is in the user selected list.
												%>
												<option value='<%=optionName%>'  SELECTED><%=optionName%></option>
												<%	
											}
											else {
												%>
												<option value='<%=optionName%>'><%=optionName%></option>
												<%
											}
											
										}
										%>									
								
										</select>
										<br>(Control+Click to Select) &nbsp; <a href='#' onclick='
			    							 	getAttributeEditForm(99999, <%=requirement.getRequirementId() %>,<%=requirement.getRequirementTypeId() %> ,"<%=a.getAttributeName() %>");
			    							 '>Reset to Previously Selected</a>
				    	
									</span>
								</div>
							
			    			
			    		<%
			    		}
			    		else if (a.getAttributeType().equals("Drop Down")){
							
				    		String [] o = a.getAttributeDropDownOptions().split(",");
				    		ArrayList childRTAttributes = rTAttribute.getChildAttributesIds();
				    		%>
			    			
							
			    				<div id='displayAttributeDiv<%=a.getAttributeId()%>' style='display:none; <%=widthString%>'>
			    					<span class='normalText' 
			    					><%=a.getAttributeEnteredValue()%></span>
			    				</div>
			    				<div id='editAttributeDiv<%=a.getAttributeId()%>' style=' <%=widthString%>'>
								<span class='normalText'  >
									<select style='height:25px;' <%=disabled%>  
										id='<%=requirement.getRequirementId()%>-<%=a.getAttributeValueId()%>'  
									<%
									// If this is a parent attribute (i.e has more than 1 child attributes
									// then we need to refresh all the child attributes, the moment the parent attrib value is modified.
									if (childRTAttributes.size() > 0){
										// this is a parent attribute.
									%>
										onChange='
											var parentAttributeElementValue = this.options[this.selectedIndex].value;
											<%
											Iterator cRTA = childRTAttributes.iterator();
											while (cRTA.hasNext()){
												Integer childRTAttributeIdIngeter = (Integer) cRTA.next();
												RTAttribute childRTAttribute = new RTAttribute(childRTAttributeIdIngeter.intValue());
												RAttributeValue childRAttributeValue = new RAttributeValue(requirementId, childRTAttributeIdIngeter.intValue(), databaseType );
												%>
												var childAttributeElement = document.getElementById("<%=childRAttributeValue.getAttributeValueId()%>");
												while(childAttributeElement.options.length > 1){childAttributeElement.remove(1);}
												
												var location = 0;
												<%
												// now lets go through all the possible drop down options and select only those ones that are permitted by the parent attribute value.
												// eg : if the parent is Porsche, then che possible child values are Porsche:911,Porsche:Panamera etc...
												String [] c = childRTAttribute.getAttributeDropDownOptions().split(",");
												for (int i=0 ; i < c.length; i++){
													String optionName = c[i];
													%>
													var optionName = "<%=optionName%>";
													if (parentAttributeElementValue.length > 0 ){
													var pos	 = optionName.indexOf(parentAttributeElementValue + ":");
														if (pos >= 0){
															var oN = optionName.split(":");
															optionName = oN[1];
															childAttributeElement.options[++location] = new Option( optionName, optionName);
														}
													}
													<%
												}
												
											}
											%>
										'
									<%	
									}
									%>
									
									>
										<option value=''></option>
										
						
										<%
										
										
										for (int i=0 ; i < o.length; i++){
											String optionName = o[i];
											
											// we have a parent child attributes concept for Drop Down values.
											int parentAttributeId =  rTAttribute.getParentAttributeId() ;
											if (parentAttributeId > 0 ) {
												// if this is a child attribute, then we only show the options that are driven by the parent attribute's value.
												
												// Sample: parentAtttibuteValues is Porsche,Jaguar  
												// Sample : attributedropdownopations are Porsche:911,Porsche:Carrera,Porsche:Panamera,Porsche:Cayanne,Jaguar:XJ,Jaguar:XK,Jaguar:Vanden Plas,Cadillac:Escalade,Cadillac:CTS,Cadillac:DTS,Volkswagen:Phaeton,Volkswagen:Passat,Volkswagen:Toureg,
												
									    		RAttributeValue parentRAttributeValue = new RAttributeValue(requirementId, parentAttributeId,databaseType);
									    		String parentAttributeValue = parentRAttributeValue.getAttributeEnteredValue();
									    		
												// Sample : optionName is Porsche:911
												boolean validChild = ProjectUtil.isValidChildOption(optionName,parentAttributeValue);
									    		
												if (!validChild){
													continue;
												}
												// this attribute has a parent attribute.
												// so the dropdown values have a : in them. we need to remove the content before the :, eg : Porsche:911,Porsche:carrera need to be 911,carrera.
												if ((optionName != null) && (optionName.contains(":"))){
													String [] oN = optionName.split(":");
													optionName = oN[1];
												}
											}
											
											
											
											if (optionName.trim().equals(a.getAttributeEnteredValue().trim())){
												
												%>
												<option value='<%=optionName%>'  SELECTED><%=optionName%></option>
												<%	
											}
											else {
												%>
												<option value='<%=optionName%>'><%=optionName%></option>
												<%
											}
											
										}
										%>
							
									</select>
								</span>
								</div>
							
							<%
			    		}
			    	 
			    	 %>
			    	 	<br>
								<input type='button' class='btn btn-primary btn-xs'  <%=updateRequirementsDisableString%> style='height:25px' 
								name='Update' id='updateAttributesButton' value='  Update  ' 
								onClick='createRequirementSingleAttributeValue(<%=rowId%>, "<%=attributeLabel %>",<%=requirementId%>,"<%=attributeIdString%>","<%=attributeRequiredIdString%>" )'>
								
								<input type='button' class='btn btn-danger btn-xs'   style='height:25px' 
								name='Cancel' id='cancelAttributesButton' value='  Cancel  ' 
								onClick='cancelAttributeEditForm(<%=rowId%>, <%=requirementId %>,"<%=attributeLabel %>" )'>
								
		    
			
</div>
<%		}
	  }%>	