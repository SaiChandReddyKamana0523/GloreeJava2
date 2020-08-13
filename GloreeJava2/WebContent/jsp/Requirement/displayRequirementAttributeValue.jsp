<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String displayRequirementIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayRequirementIsLoggedIn == null) || (displayRequirementIsLoggedIn.equals(""))){
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
		
		ArrayList attributeValues = RequirementUtil.getAttributeValuesInRequirement(requirementId);
		
		// if this is a deleted req, forward to the displayRequirementCoreDel.jsp
		if (requirement.getDeleted() == 1){
			request.setAttribute("requirement",requirement);
			request.setAttribute("attributeValues",attributeValues);
	%>
	<jsp:forward page="/jsp/Requirement/displayRequirementAttributeValueDel.jsp"/>
	<%
		}
		
	    if (attributeValues.size() == 0  ){
	    %>
	    	<div id = 'attributeInfo'  STYLE="background-color:white">	
				<table class='paddedTable'  border="0" width='100%'>
					
					<tr>
						<td colspan='2'>
							<span class='normalText'>
							No Custom Attributes have been defined for this Requirement Type.
							</span>
						</td>
					</tr>
				</table>
			</div>
	   <% 	
	    }
	    else {
	    	
	    	

	    	
	    %>
	    
<div class="panel panel-info"> 	
	<div class="panel-heading " > 
		<%=requirement.getRequirementFullTag() %>  Attributes 
		
	</div>
	<div id = 'attributeInfo' class="panel-body" >
				<table class='paddedTable'>
					<tr>
						<td colspan='2' align='center' >		
							<%if (!(requirement.getRequirementLockedBy().equals(""))){
								// this requirement is locked. so lets display a lock icon.
							%>
								<span class='normalText' title='Requirement locked by <%=requirement.getRequirementLockedBy()%>'> 
		        					<img src="/GloreeJava2/images/lock16.png" border="0"> Locked by <%=requirement.getRequirementLockedBy() %>
		        				</span>	
							<%
							}
							%>		
							
						</td>
					</tr>
					
				</table>
				<table class='table ' style='background-color:<%=color%>'  border="0" width='100%'>
					
				    
				  	 <% 
				  	 
				  	 // let iterate through all the attributes to get the attributeIdString, which will b used
				  	 // as a parameter to Javascript . NOTE : do not attempt to do this as part of the 
				  	 // the loop that prints the attributes, as the complete attributeIdString won't be ready
				  	 // when you print the first attribute.
				  	 Iterator e = attributeValues.iterator();
				  	 while( e.hasNext() ){
			    		RAttributeValue a = (RAttributeValue) e.next();
			    		

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
			    		
			    	 } 
				  	 
			    	 e = attributeValues.iterator();
				  	 int countOfAttributes = 0;
			    	 while( e.hasNext() ){
			    		countOfAttributes++;
			    		RAttributeValue a = (RAttributeValue) e.next();
			    		
			    		if (a.getAttributeName().trim().toLowerCase().equals("color")){
			    			%>
			    			<tr style='background:<%=color %>' 
			    			onMouseOver='
			    					document.getElementById("displayAttributeDiv<%=a.getAttributeId()%>").style.display="none";
		    						document.getElementById("editAttributeDiv<%=a.getAttributeId()%>").style.display="block";
		    					'
		    					>
			    			<%
			    		}
			    		else {
			    			%>
			    			<tr >
			    			<%
			    		}
			    		
			    	
			    		
			    		%>
		    				
			    		<%
			    		
			    		
			    		
			    		if (a.getSystemAttribute() == 1) {
			    			out.print("<td class='info' align='left' style='width:150px; vertical-align:middle; font-size:80%'> <font color='gray'> " + a.getAttributeName() + "</font>") ;
			    		}
			    		else {
			    			out.print("<td class='info' align='left' style='width:150px; vertical-align:middle; font-size:80% '> " + a.getAttributeName() );
				    		if (a.getAttributeType().equals("URL")){
			    				%>
			    				(URL)
			    				<%
			    			}
			    		}
			    		if (a.getAttributeRequired() ==1 ){
			    			out.print("<sup><span style='color: #ff0000;'>*</span></sup>"); 
			    		}	
			    		
			    				 
			    		out.print("</th>");
			    		
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
				    	if (formattedAttributeEnteredValue.length() < 5){
				    		formattedAttributeEnteredValue = formattedAttributeEnteredValue +  "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" 
				    		 + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" 
				    		;
							    
				    	}
				    	%>
				    	<td  >
		    				<div id='attributeDiv-99999-<%=requirement.getRequirementId()%>-<%=a.getAttributeName() %>' style='width:300px'  > 
																		
		    					
		    					
							        <%if (!isAttributeDisabled){ %>
							        
							          <a href="#"  style='color:black;'
											onclick='
			    							 	getAttributeEditForm(99999, <%=requirement.getRequirementId() %>,<%=requirement.getRequirementTypeId() %> ,"<%=a.getAttributeName() %>");
			    							 '> <%=formattedAttributeEnteredValue%></a>
								       
								    <%}else {%>
								    	<span class='normalText'>
							          <%=formattedAttributeEnteredValue%>
							          </span>
								       
								    <%}	%>
								 
						         
																		           
		    				</div>
		    					
		    			</td>
				    	<%
				    	
			    	 }
			    	 %>
			    	 </tr>
			    	 
				
				</table>
			
		    	 
	    <%	 
	    }
	    %>
	</div>
				
			
</div>
<%}%>	