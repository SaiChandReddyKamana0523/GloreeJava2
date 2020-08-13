<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

	<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
	<html>
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>TraceCloud - SAAS Agile Scrum Requirements Management - Collaborate, Define, Manage and Deliver your Customer Requirements</title>
 	 <meta name="description" content="Collaboration tools to define, manage and deliver your customer requirements on time and within budget. Significantly improves customer satisfaction ">
	<meta name="keywords" content="free requirements management, saas requirements management tool, online requirements management, doors, requisitepro, customer requirements, shared requirements, tl9000, project management, project requirements, agile, agile requirements management.">

	
	
	<!-- Individual YUI CSS files--> 

	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/autocomplete/assets/skins/sam/autocomplete.css">	
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/reset-fonts-grids/reset-fonts-grids.css"> 
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/resize/assets/skins/sam/resize.css"> 
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/layout/assets/skins/sam/layout.css">
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/button/assets/skins/sam/button.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/menu/assets/skins/sam/menu.css"> 
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/treeview/assets/skins/sam/treeview.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/container/assets/skins/sam/container.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/editor/assets/skins/sam/simpleeditor.css" />
	
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/calendar/assets/skins/sam/calendar.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/editor/assets/skins/sam/editor.css" />
	
	



	<!-- Individual YUI JS files --> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/yahoo-dom-event/yahoo-dom-event.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/animation/animation-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/dragdrop/dragdrop-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/element/element-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/button/button-min.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/resize/resize-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/layout/layout-min.js"></script> 
	
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/treeview/treeview-min.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/container/container-min.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/container/container_core-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/menu/menu-min.js"></script> 
	
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/utilities/utilities.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/calendar/calendar-min.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/editor/simpleeditor-min.js"></script>

	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/datasource/datasource-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/autocomplete/autocomplete-min.js"></script> 

	
	<!-- Gloree JS and CSS files -->
	
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/css/common.css"> 
	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	
	<script src="/GloreeJava2/js/userAccount.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userDashboard.js?v=20200630"></script>\
	
	
	<!--  Bootstratp  JS and CSS files -->
	 <script src="/GloreeJava2/js/jquery-3.1.1.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap-tour-standalone.min.js"></script>
	
	 <link href="/GloreeJava2/css/bootstrap.min.css" rel="stylesheet" media="screen">
	 <link href="/GloreeJava2/css/bootstrap-tour-standalone.min.css" rel="stylesheet">
	 
	
	<!--  cdn for ckeditor 
	 <script src="https://cdn.ckeditor.com/4.6.2/standard/ckeditor.js"></script>
	 -->
	 <script src="https://cdn.ckeditor.com/4.6.2/full-all/ckeditor.js"></script>
    
	
	</head>
	
	<body class=" yui-skin-sam" style='background-color:white'> 

	
	
<%
	// No authentication for this. Any one can come and create requirements
	// we just check for the correct auth code
	
	int webFormId = Integer.parseInt(request.getParameter("webFormId"));
	WebForm webForm = new WebForm(webFormId);
	
			
	String accessCode = request.getParameter("accessCode");
	if (webForm.getAccessCode().equals(accessCode)){
	
		Folder folder = new Folder(webForm.getFolderId());
		String introduction = webForm.getIntroduction();
		if (introduction.contains("Put any custom html content here")){
			introduction  = "";
		}
%>

		<div class='alert alert-info'>
			<%=introduction%>
		</div>
	
		<div id='createRequirementDiv' >
		
			<form method="post" id="createRequirementForm" action="">
				<input type="hidden" name="projectId" value= '<%=webForm.getProjectId() %>'    >
				<input type="hidden" name="folderId" value=<%=webForm.getFolderId() %> >
				<input type="hidden" name="requirementType" value="<%=folder.getRequirementTypeId()%>">
				<input type="hidden"  name="requirementExternalUrl" id="requirementExternalUrl" >
				
				<input type="hidden" name="webFormId" value= '<%=webFormId %>'    >
				
				
		<table  class="table table-striped"  style="text-align:left" >
				<tr>
					<td colspan='2' align='left' bgcolor='#99CCFF'>				
						<span class='subSectionHeadingText'>
						Create A <%=folder.getRequirementTypeName() %>
						</span>
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
			<tr>










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
			    				<textarea  rows='4' cols='130' id=<%=a.getAttributeId()%> ></textarea>
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
	    						<input type='text' size='100' id=<%=a.getAttributeId()%> >
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
			 
				
				<td width='200px' ><span class='headingText'> Default Owner</span><sup><span style="color: #ff0000;">*</span></sup> </td>
					
				<td >
					<table>
					<tr>
						<td>
							<div style='float:left'>
							<span class='normalText'>
							<input type="text" DISABLED="DISABLED" name="requirementOwner" id="requirementOwner" size="20" maxlength="100"
							value="<%=webForm.getDefaultOwner() %>" > 
							</span>
							</div>
							<div id='requirementOwnerValidateDiv' style='float:left;display:none'></div>
						</td>		
							
						<td><span class='headingText'> Priority</span><sup><span style="color: #ff0000;">*</span></sup> </td>
						<td>
							<span class='normalText'>
							<select name="requirementPriority">
								<option value="High">High </option>
								<option value="Medium" SELECTED>Medium</option>
								<option value="Low">Low</option>
							</select>
							</span> 
						</td>
						<td><span class='headingText'> Complete</span><sup><span style="color: #ff0000;">*</span></sup> </td>
						<td>
							<span class='normalText'>
							<input type="text"   DISABLED="DISABLED"  name="requirementPctComplete" value='0' size="3" maxlength="3"> %
							</span>
						</td>	
					</tr>
					</table>
				</td>
			</tr>
			<tr> 
				<td colspan='2' class='info'>
					<span class='headingText'> Trace to / from other objects  </span>
				</td>
			</tr>	
			
			<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
				<td width='200px'>
					<span class='headingText'> Trace To (UpStream)  </span>
				</td>
				<td >
					<span class='normalText'>
					<input type='text' name='traceTo' id='traceTo' size='100' placeholder='Enter Comma separated reqs like REL-1,REL2 here'
					onchange = 'validateTraceToInWebForm(<%=webForm.getProjectId() %> , <%=webForm.getId()%>)'>
					&nbsp;&nbsp;
					</span>
					<div id='validateTraceToDiv' class='alert alert-info' style='display:none'></div>
				</td>
			</tr>	
			
			<tr onmouseover="this.style.background='#E5EBFF'; " onmouseout="this.style.background='white';"> 
				<td width='200px'>
					<span class='headingText'> Trace From (DownStream)  </span>
				</td>
				<td >
					<span class='normalText'>
					<input type='text' name='traceFrom' id='traceFrom' size='100' placeholder='Enter Comma separated reqs like FR-1,FR-2 here'
					onchange = 'validateTraceFromInWebForm(<%=webForm.getProjectId() %>, <%=webForm.getId()%>)'>
					&nbsp;&nbsp;
					</span>
					<div id='validateTraceFromDiv' class='alert alert-info' style='display:none'></div>
				</td>
			</tr>	
			
				
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
					<input type="button"  name="Create Requirement" style='height:25px'
					id="createRequirementButton" value=" <%=createObjectName %> " 
					onClick='createRequirementInWebForm("<%=attributeIdString%>","<%=attributeRequiredIdString%>")'>
					
				</span>
				</td>
			</tr> 	



		 
		
		</table>
		
		</form>
	</div>
<%	
}%>

</body>
</html>