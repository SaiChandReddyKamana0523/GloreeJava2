<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String createRequirementFormIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((createRequirementFormIsLoggedIn == null) || (createRequirementFormIsLoggedIn.equals(""))){
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
		User user = securityProfile.getUser();
		
%>
	
	
	<%
		
		String parentFullTag = request.getParameter("parentFullTag");
		if (parentFullTag == null){
			parentFullTag = ""; 
		}
		
		String folderIdString = request.getParameter("folderId");
		int folderId = Integer.parseInt(folderIdString);
		Folder folder = new Folder(folderId);	
		String requirementTypeName = folder.getRequirementTypeName();
		
		// if the user does not have 'Create Requirements' priv for this folder
		// we disable both the createRequirements and Import Excel buttons.
		// The rest of the buttons View Report, Create Sub folder, Delete Sub Folder , Edit folder
		// etc.. are available to regular users.
		// Note : Delete Sub folder is controlled by whether the user can delete the underlying 
		// requirements or not.		
		if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" 
				+ folder.getFolderId()))){
	%>
			<div id = 'createRequirementDiv' class='level1Box'>
				<table class='paddedTable'>
					<tr>
						<td>
						<div id='noPermissionsDiv' class='alert alert-success'>
						You do not have privileges to Create Requirements in this Folder. Please contact 
						your project administrator.
						</div>
						</td>
					</tr>
				</table>
			</div>
	<%
		return;
		}
		
	%>
	<div id='createRequirementDiv' class='level1Box'>
	
		<form method="post" id="createRequirementForm" action="">
			<input type="hidden" name="projectId" value= '<%=project.getProjectId() %>'    >
			<input type="hidden" name="folderId" value=<%=request.getParameter("folderId") %> >
			<input type="hidden" name="requirementType" value="<%=folder.getRequirementTypeId()%>">
			
			<input type="hidden" name="requirementTypeName" id='requirementTypeName' value="<%=folder.getRequirementTypeShortName() %>">
			<input type="hidden"  name="requirementExternalUrl" id="requirementExternalUrl" >
			 
			 
			 <%
				// lets get the allocation requirement type root folder id
			
				int allocationRootFolderId = 0;
				try{
				RequirementType allocationRT   = new RequirementType(folder.getProjectId(), "Allocations");
					allocationRootFolderId = allocationRT.getRootFolderId();
				}
			 	catch (Exception e){

			 	}
			
			%>
			<input type="hidden" name="allocationRootFolderId"  id="allocationRootFolderId"  value="<%=allocationRootFolderId %>">
	<div class="panel panel-info"> 	
	<div class="panel-heading " style='text-align:center'> 
		Create A <%=folder.getRequirementTypeName() %>
		
	</div>
	<div id = 'attributeInfo' class="panel-body" >			
		<table class='paddedTable' width='100%'>
			
			<tr>
				<td colspan='2' >				
					<div id='requirementCreationErrorDiv' style='display:none'></div>
				</td>
			</tr>				

			
			<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
				<td width='200px'><span class='headingText'> Name</span><sup><span style="color: #ff0000;">*</span></sup> </td>
				<td > 
					<span class='normalText'> 
						<textarea id="requirementName" name="requirementName"  rows='4' cols='130'></textarea>
					</span>					
				</td>
				
			</tr>
			
			<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
				<td width='200px'>
					<span class='headingText'> Description  </span>
				</td>
				<td >
					<span class='normalText'>
					<textarea id="requirementDescription" name="requirementDescription" 
					rows="7" cols="130" ></textarea>
					</span>
				</td>
			</tr>	
			
			


			




			<%
			// Lets display the attributes
			
			String attributeIdString = "";
			String attributeRequiredIdString = "";
			
			ArrayList attributes = ProjectUtil.getAllAttributes(folder.getRequirementTypeId());
			// let iterate through all the attributes to get the attributeIdString, which will b used
		  	// as a parameter to Javascript . NOTE : do not attempt to do this as part of the 
		  	// the loop that prints the attributes, as the complete attributeIdString won't be ready
		  	// when you print the first attribute.
		  	if (attributes.size() > 0){
		  		%>
		  			<tr><td colspan='2'>
					<div class="alert alert-info" > 
						Custom Attributes 
					</div>
					</td></tr>
				<%
		  	}
		  	 
			Iterator e = attributes.iterator();
		  	while( e.hasNext() ){
	    		RTAttribute a = (RTAttribute) e.next();
	    		if (a.getSystemAttribute() != 1){
	
		    		if (a.getAttributeType().equals("Text Box")){
						attributeIdString += a.getAttributeId() + "#Text##";
						if (a.getAttributeRequired() ==1 ){
							attributeRequiredIdString += a.getAttributeId() + "#" + a.getAttributeName() + "#TextBox" + "##";
						}
		    		}
		    		else if (a.getAttributeType().equals("Date")){
						attributeIdString += a.getAttributeId() + "#Date##";
						if (a.getAttributeRequired() ==1 ){
							attributeRequiredIdString += a.getAttributeId() + "#" + a.getAttributeName() + "#Date" + "##";
						}
					}
		    		else if (a.getAttributeType().equals("URL")){
						attributeIdString += a.getAttributeId() + "#URL##";
						if (a.getAttributeRequired() ==1 ){
							attributeRequiredIdString += a.getAttributeId() + "#" + a.getAttributeName() + "#URL" + "##";
						}
					}
		    		else if (a.getAttributeType().equals("Number")){
						attributeIdString += a.getAttributeId() + "#Number##";
						if (a.getAttributeRequired() ==1 ){
							attributeRequiredIdString += a.getAttributeId() + "#" + a.getAttributeName() + "#Number" + "##";
						}
					}
		    		else if (a.getAttributeType().equals("Drop Down")){
						attributeIdString += a.getAttributeId() + "#DropDown##";
						if (a.getAttributeRequired() ==1 ){
							attributeRequiredIdString += a.getAttributeId() + "#" + a.getAttributeName() + "#DropDown" + "##";
						}
		    		}
		    		else if (a.getAttributeType().equals("Drop Down Multiple")){
						attributeIdString += a.getAttributeId() + "#DropDownMultiple##";
						if (a.getAttributeRequired() ==1 ){
							attributeRequiredIdString += a.getAttributeId() + "#" + a.getAttributeName() + "#DropDownMultiple" + "##";
						}
		    		}
		      	}
		  	}

		  	
		  	
		  	// lets iterate through all the attributes and print them out to screen
			e = attributes.iterator();
		  	while( e.hasNext() ){
	    		RTAttribute a = (RTAttribute) e.next();
	    		if (a.getSystemAttribute() != 1){
	    			// not a system attribute. 
	    			%>
	    			<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
						<td width='200px'><span class='headingText'><%=a.getAttributeName() %>
	    			<%
	    			if (a.getAttributeRequired() ==1 ){
		    			out.print("<sup><span style='color: #ff0000;'>*</span></sup>"); 
		    		}	
	    			%>
	    				</span></td>
	    			<%
	    			
		    		if (a.getAttributeType().equals("Text Box")){
	    			%>
		    			<td >
		    				<span class='normalText'>  
			    				<textarea  rows='4' cols='130' id=<%=a.getAttributeId()%> ><%=a.getAttributeDefaultValue() %></textarea>
			    				<%
		    					if (a.getAttributeName().trim().equals("Keep Me Informed")){
		    						%>
		    						Enter comma separated email addresses.
		    						<%
		    					}
		    					%>
		    				</span>
		    			</td>
		    		<%
		    		}
		    		else if (a.getAttributeType().equals("Date")){
		    			%>
		    			<td>
	    					<span class='normalText'>  
	    						<input type='text' size='10' id=<%=a.getAttributeId()%> value='<%=a.getAttributeDefaultValue()%>'>
	    						(mm/dd/yyyy)
	    					</span> 
	    				</td>
						<%
					}
		    		else if (a.getAttributeType().equals("URL")){
						%>
		    			<td>
	    					<span class='normalText'>  
	    						<input type='text' size='100' id=<%=a.getAttributeId()%>  value='<%=a.getAttributeDefaultValue()%>'>
	    					</span> 
	    				</td>
						<%
						
						
					}
		    		else if (a.getAttributeType().equals("Number")){
					%>
		    			<td >
	    					<span class='normalText'>  
	    						<input type='text' size='10' id=<%=a.getAttributeId()%> <%=a.getAttributeDefaultValue() %> >
	    						Number
	    					</span> 
						</td>
					<%
		 			}
		    		else if (a.getAttributeType().equals("Drop Down Multiple")){
		    			RTAttribute rTAttribute = new RTAttribute(a.getAttributeId());
		    			String [] o = a.getAttributeDropDownOptions().split(",");
		    			
		    			int sizeOfDropDown = o.length;
		    			if (sizeOfDropDown == 0 ) {
		    				sizeOfDropDown = 1;
		    			}
		    			%>
		    			
		    			
		    			<td >
	    					<span class='normalText'   >
								<select multiple='MULTIPLE' id=<%=a.getAttributeId()%> SIZE='<%=sizeOfDropDown%>' >
								<%
								
								for (int i=0 ; i < o.length; i++){
									String optionName = o[i];
										if (optionName.equals(a.getAttributeDefaultValue())){
										%>
										<option value='<%=optionName%>' SELECTED><%=optionName%></option>
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
								<br>(Control+Click to Select)
		    	
							</span>
						</td>
		    			
		    		<%
		    		}
		    		else if (a.getAttributeType().equals("Drop Down")){
			    		
						int parentAttributeId =  a.getParentAttributeId() ;
			    		ArrayList childRTAttributes = a.getChildAttributesIds();
			    		String [] o = a.getAttributeDropDownOptions().split(",");
			    		%>
		    			
						<td>
							<span class='normalText'  >
								<select style='height:25px;' id=<%=a.getAttributeId()%>  
								<%
								if (childRTAttributes.size() > 0 ){
									// since this is a parent attribute, lets put code to change
									// the values of children based on parent change. 
									%>
									onChange="
											// 2. rotate through all the possible values for this object and if the parent value mathes it, then add it.
											var parentObject = document.getElementById('<%=a.getAttributeId()%>');
											var parentValue = parentObject.options[parentObject.selectedIndex].value ;
											// lets add the : at the end of the parent value. This is because in each
											// chhild attribute, the values are stored as parentvalue:option1, parentvalue:option2 etc..
											parentValue = parentValue + ':';
											
											<%
											
											// lets iterate through the children
											Iterator cR = childRTAttributes.iterator();
											while (cR.hasNext()){
												Integer childRTAttributeInteger = (Integer) cR.next();
												RTAttribute childRTAttribute = new RTAttribute(childRTAttributeInteger.intValue());
												// for each child attribute, do the following :
												// 1. Locate the drop down object and clear all the elements
												// 2. rotate through each possible value and see if this should be added.
												%>
												// 1. lets drop all the values from the child dropdown.
												var childAttributeElement = document.getElementById('<%=childRTAttributeInteger.intValue() %>');
												while(childAttributeElement.options.length > 1){childAttributeElement.remove(1);}
												
												
												// At this point the child object is empty. 
												
												
												var location = 0;
												<%
												// now lets go through all the possible drop down options and select only those ones that are permitted by the parent attribute value.
												// eg : if the parent is Porsche, then che possible child values are Porsche:911,Porsche:Panamera etc...
												String [] c = childRTAttribute.getAttributeDropDownOptions().split(",");
												for (int i=0 ; i < c.length; i++){
													String optionName = c[i];
													%>
													var optionName = '<%=optionName%>';
													var pos	 = optionName.indexOf(parentValue);
													if (pos >= 0){
														var oN = optionName.split(':');
														optionName = oN[1];
														childAttributeElement.options[++location] = new Option( optionName, optionName);
													}
													
													<%
												}
											}
											
											%>
										"	
									<%
								}
								%>
								>
									<option value=''></option>
									<%
									for (int i=0 ; i < o.length; i++){
										String optionName = o[i];
										if (optionName != null){
											if (optionName.equals(a.getAttributeDefaultValue())){
											%>
												<option value='<%=optionName%>' SELECTED><%=optionName%></option>
											<%
											}
											else {
												%>
												<option value='<%=optionName%>'><%=optionName%></option>
												<%
													
											}
										}
									}
									%>
						
								</select>
							</span>
						</td>
						<%
		    		}
	    			
	    		}
	    		
	    		
		  	}		  	
		  	%>
			</tr>
			
			
			<%
			String visibleStyle = "style='visibility:visible'";
		  	if ((project.getProjectTags().toLowerCase().contains("school_project"))){
		  		 visibleStyle = "style='visibility:hidden'";
		  	}
		  	%>
		  	
				<tr><td colspan='2'>
				<div class="alert alert-info" <%=visibleStyle %> > 
					General Information
				</div>
				</td></tr>
	
				<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
				 
					<td width='200px' ><span class='headingText'  <%=visibleStyle %>  > Parent </span></td>
					<td > 
						<span class='normalText' <%=visibleStyle %>  >
						<input type="text"  name="parentFullTag" id="parentFullTag" size="20" maxlength="20"
						value='<%=parentFullTag%>'
						onBlur='
						if (document.getElementById("parentFullTag").value.length > 0){
							validateParentTag(<%=folderId%>)
						}
						'> 
						Optional Id of the parent of this <%=requirementTypeName%> (eg : Br-1)
						</span>
					</td>
				</tr>
				<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
				 
					<td colspan='2'>
						<div id='parentInfoDiv' style='display:none;'></div>
					</td>
				</tr>
				
				<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
				 
					
					<td width='200px'  ><span <%=visibleStyle %> > Owner<sup><span style="color: #ff0000;">*</span></sup></span> </td>
					
					
						
					<td >
						<table class='table'>
						<tr>
							<td>
								<div style='float:left' >
								<select name="requirementOwner" id="requirementOwner"  <%=visibleStyle %> >
								<%
								ArrayList users = project.getMembers();
								Iterator u = users.iterator();
								
								while (u.hasNext()){
									User projectMember = (User) u.next();
	
									 if (projectMember.getEmailId().equals(user.getEmailId())){
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
							
								
								</div>
								<div id='requirementOwnerValidateDiv' style='float:left;display:none'></div>
							</td>		
								
							<% if (project.getHidePriority() == 0 ){ %>
							<td><span class='headingText' <%=visibleStyle %> > Priority</span><sup><span style="color: #ff0000;">*</span></sup> </td>
							<td>
								<span class='normalText'>
								<select name="requirementPriority" <%=visibleStyle %> >
									<option value="High">High </option>
									<option value="Medium" SELECTED>Medium</option>
									<option value="Low">Low</option>
								</select>
								</span> 
							</td>
							<%} %>
							<td><span class='headingText' <%=visibleStyle %> > Complete</span><sup><span style="color: #ff0000;">*</span></sup> </td>
							<td>
								<span class='normalText'>
								<input type="text"  <%=visibleStyle %>  name="requirementPctComplete" value='0' size="3" maxlength="3"> %
								</span>
							</td>	
						</tr>
						</table>
					</td>
					
				<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
			<%

			String createObjectName = "Create A ";
			String reqTypeName = folder.getRequirementTypeName();
			 
		
			
			// drop the last s
			if 	(reqTypeName.endsWith("s")){
				reqTypeName = (String) reqTypeName.subSequence(0,reqTypeName.lastIndexOf("s"));
			}
			
			if 	(reqTypeName.endsWith("S")){
				reqTypeName = (String) reqTypeName.subSequence(0,reqTypeName.lastIndexOf("S"));
			}
			
			createObjectName  += reqTypeName;
			%>
				<td width='200px'>
				<td align="left">
					<span class='normalText'>
					<input type="button"  name="Create Requirement" style='height:25px' class='btn btn-sm btn-primary'
					id="createRequirementButton" value=" <%=createObjectName %> " 
					onClick='createRequirement("<%=attributeIdString%>","<%=attributeRequiredIdString%>" )'>
					
				</span>
				</td>
			</tr> 	



		 
		
		</table>
	</div>
	</div>
		</form>
	</div>
	
<%}%>