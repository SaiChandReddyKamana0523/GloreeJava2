<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String eRTACIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((eRTACIsLoggedIn   == null) || (eRTACIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	
	// authorization : only admins can do this stuff.
	Project project= (Project) session.getAttribute("project");
	SecurityProfile aASecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
	RequirementType requirementType = new RequirementType(requirementTypeId);
	String status = (String) request.getAttribute("status");
	String rTAttributeIdString = request.getParameter("rTAttributeId");
	int rTAttributeId = Integer.parseInt(rTAttributeIdString);
	RTAttribute a = new RTAttribute(rTAttributeId);
	
	
	String disabled = " disabled='disabled' ";
	// lets see if this user is an member of this project.
	boolean isMember = false;
	if (aASecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	String powerUserSettings = project.getPowerUserSettings();
	
	// lets see if this user is an admin of this project.
	// unless the user is an admin, the create / delete buttons stay disabled.
	if (
			(aASecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))
			||
			(
				(aASecurityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
				&&
				(powerUserSettings.contains("Manage Requirement Types"))
				)
		){
		disabled = "";
	}
	
	// if the req type is Agile Scrum enabled, then some scrum attributes like Agile Sprint, Task Status , Effort Remaining and Priority can not be modified.
	if (
			(requirementType.getRequirementTypeEnableAgileScrum() == 1 )
			&&
			(
				(a.getAttributeName().equals("Agile Sprint"))
				||
				(a.getAttributeName().equals("Agile Task Weight"))
				||
				(a.getAttributeName().equals("Agile Task Status"))
				||
				(a.getAttributeName().equals("Agile Total Effort (hrs)"))
				||
				(a.getAttributeName().equals("Agile Effort Remaining (hrs)"))
			)
		){
		disabled = " disabled='disabled' ";
	}
	
	
%>


<% if (isMember) { %>
	<%@ page import="java.util.*" %>
	<%@ page import="javax.servlet.http.HttpSession"  %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<%
		// NOTE : this page can be called under 2 scenarios. 
		// 1. when some one tried to create an attribute in the prev step, and used the same prefix . resulting
		// in an error STATUS. Which we use to display an error message.
		// 2. When someon clicked on an attribute name , in order to diplay / edit it.
		// in this case, we show the same form, but send a hidden atribute called attributeId.
		

		
		String attributeName = "";
		String attributeSortOrder = "";
		String attributeType = "";
		String attributeRequired = " ";
		String attributeDefaultDisplay = "";
		String attributeDefaultValue = "";
		String attributeDropDownOptions = "";
		String attributeDescription = "";
		
		String attributeImpactsVersion = "";
		String attributeImpactsTraceability = "";
		String attributeImpactsApprovalWorkflow = "";

		
		String	attributeFormButton = "";
		String deleteAttributeButton = "";
		
		attributeName = a.getAttributeName();
		attributeSortOrder = a.getAttributeSortOrder();
	
		String disableDropDownOptions = "DISABLED";
		if (a.getAttributeType().equals("Drop Down")){
			attributeType = "<option value='Text Box' > Text Box </option> " + 
			" <option value='Drop Down' SELECTED > Drop Down </option>  " + 
			" <option value='Drop Down Multiple' > Drop Down - Multiple </option>  " +
			" <option value='URL' > URL </option> " + 
			" <option value='Number' > Number </option> " + 
			" <option value='Date'> Date </option>";
			
			disableDropDownOptions = ""; 
		}
		if (a.getAttributeType().equals("Drop Down Multiple")){
			attributeType = "<option value='Text Box' > Text Box </option> " + 
			" <option value='Drop Down' > Drop Down </option>  " + 
			" <option value='Drop Down Multiple' SELECTED > Drop Down - Multiple </option>  " +
			" <option value='URL' > URL </option> " + 
			" <option value='Number' > Number </option> " + 
			" <option value='Date'> Date </option>";
			
			disableDropDownOptions = ""; 
		}		
		if (a.getAttributeType().equals("URL")){
			attributeType = "<option value='Text Box' > Text Box </option> " + 
			" <option value='Drop Down' > Drop Down </option>  " + 
			" <option value='Drop Down Multiple' > Drop Down - Multiple </option>  " +
			" <option value='URL' SELECTED > URL </option> " + 
			" <option value='Number' > Number </option> " + 
			" <option value='Date'> Date </option>";
		}
		if (a.getAttributeType().equals("Number")){
			attributeType = "<option value='Text Box' > Text Box </option> " + 
			" <option value='Drop Down' > Drop Down </option>  " +
			" <option value='Drop Down Multiple' > Drop Down - Multiple </option>  " +
			" <option value='URL' > URL </option> " + 
			" <option value='Number' SELECTED > Number </option> " + 
			" <option value='Date'  > Date </option>";
		}		
		if (a.getAttributeType().equals("Date")){
			attributeType = "<option value='Text Box' > Text Box </option> " + 
			" <option value='Drop Down' > Drop Down </option>  " +
			" <option value='Drop Down Multiple' > Drop Down - Multiple </option>  " +
			" <option value='URL' > URL </option> " + 
			" <option value='Number' > Number </option> " + 
			" <option value='Date' SELECTED > Date </option>";
		}
		if (a.getAttributeType().equals("Text Box")) {
			attributeType = "<option value='Text Box' SELECTED> Text Box </option> " + 
			" <option value='Drop Down' > Drop Down </option>  " + 
			" <option value='Drop Down Multiple' > Drop Down - Multiple </option>  " +
			" <option value='URL' > URL </option> " + 
			" <option value='Number' > Number </option> " + 
			" <option value='Date'> Date </option>";
		}
		
		if (a.getAttributeRequired() == 0) {
			attributeRequired = " <option value='1' > Yes </option> <option value='0' SELECTED> No </option>	";
		}
		else {
			attributeRequired = " <option value='1' SELECTED > Yes </option> <option value='0' > No </option>	";			
		}
		
		
		if (a.getAttributeDefaultDisplay() == 0) {
			attributeDefaultDisplay = " <option value='1' > Yes </option> <option value='0' SELECTED> No </option>	";
		}
		else {
			attributeDefaultDisplay = " <option value='1' SELECTED > Yes </option> <option value='0' > No </option>	";			
		}
		
		
		if ( a.getAttributeImpactsVersion() == 0 ) {
			attributeImpactsVersion = " <option value='1' > Yes </option> <option value='0' SELECTED> No </option>	";
		}
		else {
			attributeImpactsVersion = " <option value='1' SELECTED > Yes </option> <option value='0' > No </option>	";
		}

		
		if (a.getAttributeImpactsTraceability() == 0  ) {
			attributeImpactsTraceability = " <option value='1' > Yes </option> <option value='0' SELECTED> No </option>	";
		}
		else {
			attributeImpactsTraceability = " <option value='1' SELECTED > Yes </option> <option value='0' > No </option>	";
		}

		
		
		if (a.getAttributeImpactsApprovalWorkflow() == 0 ) {
			attributeImpactsApprovalWorkflow = " <option value='1' > Yes </option> <option value='0' SELECTED> No </option>	";
		}
		else {
			attributeImpactsApprovalWorkflow = " <option value='1' SELECTED > Yes </option> <option value='0' > No </option>	";
		}
		
		
		
		attributeDefaultValue = a.getAttributeDefaultValue();
		attributeDropDownOptions = a.getAttributeDropDownOptions();
		attributeDescription = a.getAttributeDescription();
		attributeFormButton = "<input id='attributeSubmitBtn' type='button' class='btn btn-sm btn-primary' " + 
			disabled + " name='Update Attribute' value='Update Attribute' onClick='editRTAttribute(" ;
		attributeFormButton +=  requirementTypeId + ")'>";
		
		deleteAttributeButton = "<input type='button' class='btn btn-sm btn-danger' " +  
			disabled + " name='Delete Attribute' value='Delete Attribute' ";
		deleteAttributeButton += " onClick='deleteRTAttributeForm(" + rTAttributeIdString + "," + requirementTypeId + ")'>";
		
		

		
		String createdAttributeMessage = "";
		String createdAttribute = (String) request.getAttribute("createdAttribute");
		if ((createdAttribute != null) && (createdAttribute.equals("true"))){
			createdAttributeMessage = " " +
				" <tr> " + 
				"	<td colspan='2'> " +
				"		<div class='alert alert-success'> <span class='normalText'>" + 
				"			Your Attribute has been created" + 
				"		</span> </div>" + 
				"	</td> " +
				" </tr> ";
		}
		String updatedAttributeMessage = "";
		String updatedAttribute = (String) request.getAttribute("updatedAttribute");
		if ((updatedAttribute != null) && (updatedAttribute.equals("true"))){
			updatedAttributeMessage = " " +
				" <tr> " + 
				"	<td colspan='2'> " +
				"		<div class='alert alert-success'> " + 
				"			Your changes have been applied " + 
				"		</div>" + 
				"	</td> " +
				" </tr> ";
		}
		
	%>
	
	<div id='editAttributeDiv' class='level1Box'>
	<form method="post" id="editRTAttributeForm" action="">
		<input type="hidden" name="requirementTypeId" value="<%=requirementTypeId %>"  >
		<input type="hidden" name="rTAttributeId" value="<%=rTAttributeIdString%>"> 
	<table class='paddedTable' width='100%'>
		<tr>
			<td colspan="4"  align='left'  > 
				<span class='subSectionHeadingText'>
				<b>Edit Custom Attribute</b>
				</span> 
			</td>
		</tr>
		<%=createdAttributeMessage  %>
		<%=updatedAttributeMessage  %>
		<%
		if ((status != null) && (status.equals("attributeName already used"))) {
		%>
		<tr>
			<td colspan="2">
				<div id='attributeNameAlreadyUsedMessage' class='alert alert-success'>
				Another Attribute already exists with the same Name. 
				Please choose a different Name for this Attribute. 
				</div>
			</td> 
		</tr>
		<% 
		} 
		%>
	
		<tr>
			<td width='200px'>
				<span class='headingText'> Name</span>
				<sup><span  style="color: #ff0000;">*</span></sup> 
			</td>
			
			<td> 
				<span class='normalText'>
				<input type="text"  <%=disabled%> name="attributeName" value='<%=attributeName%>' size="30" 
				maxlength="100"> 
				</span>
			</td>
			<td> 
				<span class='headingText'>Type</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td> 
			<td>
				<span class='normalText'>
				<select <%=disabled%> name="attributeType" id="attributeType"
				onchange='
					var selectedAttributeType = document.getElementById("attributeType").selectedIndex;
					console.log("srt selectedAttributeType " + selectedAttributeType );
					if (
						(selectedAttributeType == 1)
						||
						(selectedAttributeType == 2)
						)
					{
						console.log("srt dropdown type");
						// these are the dropdowns. Lets support parent / child
						document.getElementById("attributeDropDownOptions").disabled=false;
						document.getElementById("parentAttributeId").disabled=false;
						
					}
					else {
						console.log("srt NOT dropdown type");
						document.getElementById("attributeDropDownOptions").value="";
						document.getElementById("attributeDropDownOptions").disabled=true;
						
						document.getElementById("parentAttributeId").selectedIndex = 0;
						document.getElementById("parentAttributeId").disabled=true;
					}'
					
				>
					<%=attributeType%>
				</select>
				</span> 
			</td>
		</tr>
		<tr>
			<td>
				<span class='headingText'> Sort Order</span>
				<sup><span style="color: #ff0000;">*</span></sup>
			</td> 
			<td> 
				<span class='normalText'>
				<input type="text"  <%=disabled%> name="attributeSortOrder"  
				value='<%=attributeSortOrder%>'  size="3" maxlength="3">
				</span> 
			</td>
			<td> 
				<span class='headingText'>Required </span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td> 
			<td>
				<span class='normalText'>
				<select <%=disabled%> name="attributeRequired">
					<%=attributeRequired%>
				</select> 
				</span>
			</td>
		</tr>
		<tr> 
			<td> 
				<span class='headingText'> Display in Folder View</span>
			</td>
			<td colspan='3'>
				<span class='normalText'>
				<select <%=disabled%> name="attributeDefaultDisplay">
					<%=attributeDefaultDisplay%>
				</select> 
				</span>
				
				
				&nbsp;
				<a href='#' onClick='document.getElementById("defaultDisplayMoreInfoDiv").style.display="block";'>More Info</a>
				 <br>
				 <div id='defaultDisplayMoreInfoDiv' style='display:none'>
				 	<div style='float:right'>
				 		<a href='#' onClick='document.getElementById("defaultDisplayMoreInfoDiv").style.display="none";'>Close</a>
				 	</div>
				 	<div>
				 		<span class='normalText'>
							<br>
							This flag will ensure that this attribute and it's value is displayed for every requirement when a user opens a folder.  
							<br>	
				 		</span>
				 	</div>
				 
				 </div>
				
			</td>		
		</tr>
		<tr> 
			<td> 
				<span class='headingText'> Default Value </span>
			</td>
			<td colspan='3'>
				<span class='normalText'>
				
				 
				 <textarea <%=disabled%> name="attributeDefaultValue" rows="5" cols="100" ><%=attributeDefaultValue%></textarea>
				 
				 </span> 
				 
			</td>		
		</tr>


		<tr> 
			<td class='normalTableCell'  >
				<span class='headingText'> Parent Attribute </span>
			</td>
			<td class='normalTableCell'  colspan="3">
				<span class='normalText'>
				 <%if (a.getAttributeType().contains("Drop Down")){ 
				 	// this is a drop down attribute, so lets show the parent attribute drop down.
				 %>
				 	<select  id='parentAttributeId' name='parentAttributeId'>
				 <%}
				 else {
				 	// this is a non drop down type, so lets disable the parentAttribute drop down.
				 %>
				 	<select DISABLED id='parentAttributeId' name='parentAttributeId'>
				 <%} %>
				 	<option value='0'> </option>
				 	<%
				 	ArrayList rTAttributes = requirementType.getAllAttributesInRequirementType();
				 	Iterator rTAS = rTAttributes.iterator();
				 	while (rTAS.hasNext()){
				 		RTAttribute parentRTAttribute = (RTAttribute) rTAS.next();
				 		if (parentRTAttribute.getAttributeId() == rTAttributeId){
				 			// an attrib can not be set a parent of itself, so skip this option.
				 			continue;
				 		}
				 		if (!(parentRTAttribute.getAttributeType().contains("Drop Down"))){
					 		continue;
				 		}
				 		if (parentRTAttribute.getAttributeId() ==  a.getParentAttributeId()){
				 			// this parent attribute had been selected as parent previously. so lets preselect it
				 		%>
				 			<option SELECTED value='<%=parentRTAttribute.getAttributeId()%>'><%=parentRTAttribute.getAttributeName() %></option>
				 		<%
				 		}
					 	else {
					 		%>
				 			<option value='<%=parentRTAttribute.getAttributeId()%>'><%=parentRTAttribute.getAttributeName() %></option>
				 		<%
					 	}	
				 	}
				 	%>
				 </select>
				 </span> 
				 
				  &nbsp;<a href='#' onClick='document.getElementById("parentAttributeMoreInfoDiv").style.display="block";'>More Info</a>
				 <br>
				 <div id='parentAttributeMoreInfoDiv' style='display:none'>
				 	<div style='float:right'>
				 		<a href='#' onClick='document.getElementById("parentAttributeMoreInfoDiv").style.display="none";'>Close</a>
				 	</div>
				 	<div>
				 		<span class='normalText'>
							A 'Parent Attribute' values drive the values in this Attribute. For example:
							<br><br>if you have a Parent Drop Down Attribute called 'Manufacturer' with values Volkswagen,Porsche
							<br>and another attribute called 'Model' with values Volkswagen:Phaeton, Volkswagen:Passat, Volkswagen:CC, 
							<br>Porsche:911,Porsche:Panamera 
							
							<br><br>Now if a user selects
							<br><br>
							Volkswagen in the Parent attribute, the user will be shown Phaeton , Passat and CC in the Model drop down
							<br><br>
							Porsche in the Parent attribute, the user will be shown 911 and Panamera in the Model drop down
								
				 		</span>
				 	</div>
					
					<div class='alert alert-danger'>
					
						If you are using Parent / Child attributes (Eg: Manufacturer:Porche,Jaguar, Model:Porsche:911,Porsche:Panamera,Jaguar:XJ,Jaguar:XK etc..)
						Please set your 'Default Value' to Sub Values . In this example, for the attribute 'Model', the default value can be one of 911,Panamera, XJ, XK etc...
						 
					</div>
				 
				 </div>
			</td>		
		</tr>	

		
		<tr> 
			<td class='normalTableCell'  >
				<span class='headingText'> Drop Down Options <br>(Comma separated)</span>
			</td>
			<td class='normalTableCell'  colspan="3">
				<span class='normalText'>
				  <textarea <%=disabled%> <%=disableDropDownOptions%> rows='5' cols='100'
				  name="attributeDropDownOptions" id="attributeDropDownOptions"><%=attributeDropDownOptions%></textarea>
				  </span>
			</td>		
		</tr>
			
		<tr> 
			<td	> 
				<span class='headingText'>Description</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td colspan="3">
				<span class='normalText'>
				<textarea <%=disabled%> name="attributeDescription" rows="5" cols="100" ><%=attributeDescription%></textarea>
				</span>
			</td>
		</tr>
	</table>
	<br><br>
	<table >	
		<tr>
			<td class='normalTableCell' colspan='2' > 
				<div class='alert alert-info'>Change to this attribute ...</div>
			</td>
		</tr>
		
		<tr>	
			<td style='width:250px' class='normalTableCell'  > 
				<span class='headingText'>Upgrades Requirement Version </span>
			
			</td>
			<td>
				<span class='normalText'>
				<select name="attributeImpactsVersion" id="attributeImpactsVersion"><%=attributeImpactsVersion%></select>
				
				</span>
				<br><br>
				
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
			<div class='alert alert-danger'>Please note that when the version of an abject changes, Approval Workflow 
				gets re-set. i.e The current approval flow for that object is abandoned
				and it will need to start the approval flow again</div>
			</td>
		</tr>
		<tr>
			<td class='normalTableCell'>
				<span class='headingText'>Causes Suspect Traces </span>
			</td>
			<td>
				<span class='normalText'>
				<select name="attributeImpactsTraceability"  id="attributeImpactsTraceability"><%=attributeImpactsTraceability%></select>
				</span>
			</td>
		</tr>
		
		<tr>
			<td class='normalTableCell' > 
				<span class='headingText'>Resets Approval Workflow Status to Draft</span>
			</td>
			<td>
				<span class='normalText'>
				<select name="attributeImpactsApprovalWorkflow" id="attributeImpactsApprovalWorkflow"><%=attributeImpactsApprovalWorkflow%></select>
				</span>
			</td>
		</tr>

		
		<tr>
			<td colspan="4" align="left">
				<span class='normalText'>
					<%=attributeFormButton%>
					&nbsp;&nbsp;
					<%=deleteAttributeButton %>
				</span>
			</td>
		</tr> 	
	 
	</table>
	
	</form>
	
	</div>
<%}%>