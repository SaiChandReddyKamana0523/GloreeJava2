<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<link rel="stylesheet" type="text/css" href="/GloreeJava2/css/common.css"> 
<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	
<%
	///////////////////////////////////// SECURITY /////////////////////////////////
	// Because this is an Mobile REST API call, and there will be a lot of them
	// and since we aren't holding security profile in a session, we don't want to 
	// create a security profile for each call. 
	// So, we want to have a minimal just to make sure that they can do the action they are supposed to. 
	//
	///////////////////////////////////// SECURITY /////////////////////////////////
	// Doe the rest key have the permissions to create a requirement in this folder???
	//
	
	int folderId = Integer.parseInt(request.getParameter("folderId"));
	Folder folder = new Folder(folderId);
	
	String key = request.getParameter("key");
	// lets check if this key has create permissions on this folder.
	boolean canCreate = RoleUtil.canCreateObjects(key, folderId);
	if (!canCreate){
		return;
	}
	else {
			
%>

	
		<div id='createRequirementDiv' class='level1Box'>
		
			<form method="post" id="createRequirementForm" action="">
				<input type="hidden" name="projectId" value= '<%=folder.getProjectId() %>'    >
				<input type="hidden" name="folderId" value=<%=folder.getFolderId() %> >
				<input type="hidden" name="requirementType" value="<%=folder.getRequirementTypeId()%>">
				<input type="hidden"  name="requirementExternalUrl" id="requirementExternalUrl" >
				
				
		<table class='paddedTable' >
				<tr>
					<td  >				
						<span class='anchorClass'><strong>
						Create A <%=folder.getRequirementTypeName() %>
						</strong>
						</span>
					</td>
				</tr>

			
			<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
				<td ><span class='anchorClass'> Name</span><sup><span style="color: #ff0000;">*</span></sup> </td>
			</tr>
			
			<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
				<td > 
					<span class='normalText'> 
						<textarea id="requirementName" name="requirementName"  rows='5' cols='35'></textarea>
					</span>					
				</td>
			</tr>
			
			<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
				<td >
					<span class='anchorClass'> Description  </span>
				</td>
			</tr>	

			<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
				<td >
					<span class='normalText'>
					<textarea id="requirementDescription" name="requirementDescription" 
					rows="5" cols="35" ></textarea>
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
		  	 
			Iterator e = attributes.iterator();
		  	while( e.hasNext() ){
	    		RTAttribute a = (RTAttribute) e.next();

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

		  	
		  	
		  	// lets iterate through all the attributes and print them out to screen
			e = attributes.iterator();
		  	while( e.hasNext() ){
	    		RTAttribute a = (RTAttribute) e.next();
	    		if (a.getSystemAttribute() != 1){
	    			// not a system attribute. 
	    			%>
	    			<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
						<td ><span class='anchorClass'><%=a.getAttributeName() %>
	    			<%
	    			if (a.getAttributeRequired() ==1 ){
		    			out.print("<sup><span style='color: #ff0000;'>*</span></sup>"); 
		    		}	
	    			%>
	    				</span></td>
					</tr>
					
					<tr>
	    			<%
	    			
		    		if (a.getAttributeType().equals("Text Box")){
	    			%>
		    			<td >
		    				<span class='normalText'>  
			    				<textarea  rows='5' cols='35' id=<%=a.getAttributeId()%> ></textarea>
			    				<%
		    					if (a.getAttributeName().trim().equals("Keep Me Informed")){
		    						%>
									<br>
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
	    						<input type='text' size='10' id=<%=a.getAttributeId()%> >
	    						(mm/dd/yyyy)
	    					</span> 
	    				</td>
						<%
					}
		    		else if (a.getAttributeType().equals("URL")){
						%>
		    			<td>
	    					<span class='normalText'>  
	    						<input type='text' size='35' id=<%=a.getAttributeId()%> >
	    					</span> 
	    				</td>
						<%
						
						
					}
		    		else if (a.getAttributeType().equals("Number")){
					%>
		    			<td >
	    					<span class='normalText'>  
	    						<input type='text' size='10' id=<%=a.getAttributeId()%> >
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
										%>
										<option value='<%=optionName%>'><%=optionName%></option>
										<%
								}
								%>									
						
								</select>
								<br>(Control+Click to Select)
		    	
							</span>
						</td>
		    			
		    		<%
		    		}
		    		else if (a.getAttributeType().equals("Drop Down")){
			    		
			    		String [] o = a.getAttributeDropDownOptions().split(",");
			    		%>
		    			
						<td>
							<span class='normalText'  >
								<select style='height:25px;' id=<%=a.getAttributeId()%>  >
									<option value=''></option>
									<%
									for (int i=0 ; i < o.length; i++){
										String optionName = o[i];
										if (optionName != null){
											%>
											<option value='<%=optionName%>'><%=optionName%></option>
											<%
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
			
			

			<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
				<td><span class='anchorClass'> Priority</span><sup><span style="color: #ff0000;">*</span></sup> </td>
			</tr>	
			
			<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
				<td>
					<span class='normalText'>
					<select name="requirementPriority">
						<option value="High">High </option>
						<option value="Medium" SELECTED>Medium</option>
						<option value="Low">Low</option>
					</select>
					</span> 
				</td>
			</tr>	



			<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
				<td><span class='anchorClass'> Complete</span><sup><span style="color: #ff0000;">*</span></sup> </td>
			</tr>	
			
			<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
				<td>
					<span class='normalText'>
					<input type="text"   name="requirementPctComplete" value='0' size="3" maxlength="3"> %
					</span>
				</td>	
			</tr>	


				
			
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
			<tr><td>&nbsp;</td></tr>
			
			<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
				<td align="left">
					<span class='normalText'>
					<input type="button"  name="Create Requirement" style='height:25px'
					id="createRequirementButton" value=" <%=createObjectName %> " 
					onClick='createRequirementInMobileForm("<%=attributeIdString%>","<%=attributeRequiredIdString%>" )'>
					
				</span>
				</td>
			</tr> 	

			<tr><td><br><br><br>&nbsp;</td></tr>

		 
		
		</table>
		
		</form>
	</div>
<%
	}
%>