 <!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String cRTACIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((cRTACIsLoggedIn   == null) || (cRTACIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }

	// authorization  : only Admins can do this.
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is an admin of this project.
	boolean isAdmin = false;
	SecurityProfile eRPFSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (
			(eRPFSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))
			||
			(eRPFSecurityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
		){
		isAdmin = true;
	}

%>


<%if (isAdmin) { %>

	<%@ page import="java.util.*" %>
	<%@ page import="javax.servlet.http.HttpSession"  %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<%
		// NOTE : this page can be called under this scenarios. 
		// 1. when we want to create a new Attribute. In that case, it gets ONLY requirementTypeId as a param.
		
		int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
		RequirementType requirementType = new RequirementType(requirementTypeId);
		String status = (String) request.getAttribute("status");
		
		String attributeName = "";
		String attributeSortOrder = "";
		String attributeDefaultValue = "";
		String attributeDropDownOptions = "";
		String attributeDescription = "";
		String attributeRequired = " <option value='1' > Yes </option> <option value='0' SELECTED > No </option>	";
		String attributeDefaultDisplay = " <option value='1' SELECTED> Yes </option> <option value='0'  > No </option>	";
		
		String attributeType = "<option value='Text Box' SELECTED > Text Box </option> " + 
		" <option value='Drop Down' > Drop Down </option>  " +
		" <option value='Drop Down Multiple' > Drop Down - Multiple </option>  " +
		" <option value='URL' > URL </option> " + 
		" <option value='Number' > Number </option> " + 
		" <option value='Date'> Date </option>";
	
		String attributeImpactsVersion = " <option value='1' > Yes </option> <option value='0' SELECTED > No </option>	";
		String attributeImpactsTraceability = " <option value='1' > Yes </option> <option value='0' SELECTED > No </option>	";
		String attributeImpactsApprovalWorkflow = " <option value='1' > Yes </option> <option value='0' SELECTED > No </option>	";
		
		String statusMessage = "";
		
		if ((status != null) && (status.equals("attributeName already used"))) {
			// if we have a status message, that means, this request was already submitted and we have some request values that we can display.
			statusMessage = "	<tr> " + 		
				" <td colspan='2'> <div class='alert alert-success'>" + 		
				"	Another Attribute already exists with the same Name. Please choose another a different Name for this Attribute.	</div> " + 
				" </td>	</tr>";
			attributeName = request.getParameter("attributeName");
			attributeSortOrder = request.getParameter("attributeSortOrder");
			attributeDefaultValue = request.getParameter("attributeDefaultValue");
			attributeDropDownOptions = request.getParameter("attributeDropDownOptions");
			attributeDescription = request.getParameter("attributeDescription");
		
			if (request.getParameter("attributeType").equals("Drop Down")){
				attributeType = "<option value='Text Box' > Text Box </option> " + 
				" <option value='Drop Down' SELECTED > Drop Down </option>  " + 
				" <option value='Drop Down Multiple' > Drop Down - Multiple </option>  " +
				" <option value='URL' > URL </option> " + 
				" <option value='Date'> Date </option>";
			}
			if (request.getParameter("attributeType").equals("Drop Down Multiple")){
				attributeType = "<option value='Text Box' > Text Box </option> " + 
				" <option value='Drop Down' > Drop Down </option>  " + 
				" <option value='Drop Down Multiple' SELECTED> Drop Down - Multiple </option>  " +
				" <option value='URL' > URL </option> " + 
				" <option value='Date'> Date </option>";
			}
			else if (request.getParameter("attributeType").equals("URL")){
				attributeType = "<option value='Text Box' > Text Box </option> " + 
				" <option value='Drop Down' > Drop Down </option>  " + 
				" <option value='Drop Down Multiple' > Drop Down - Multiple </option>  " +
				" <option value='URL' SELECTED > URL </option> " + 
				" <option value='Date'> Date </option>";
			}
			else if (request.getParameter("attributeType").equals("Date")){
				attributeType = "<option value='Text Box' > Text Box </option> " + 
				" <option value='Drop Down' > Drop Down </option>  " + 
				" <option value='Drop Down Multiple' > Drop Down - Multiple </option>  " +
				" <option value='URL' > URL </option> " + 
				" <option value='Date' SELECTED > Date </option>";
			}
	
			if (request.getParameter("attributeRequired").equals("0")) {
				attributeRequired = " <option value='1' > Yes </option> <option value='0' SELECTED> No </option>	";
			}
			else {
				attributeRequired = " <option value='1' SELECTED > Yes </option> <option value='0' > No </option>	";
			}
			
			
	
			if (request.getParameter("attributeImpactsVersion").equals("0")) {
				attributeImpactsVersion = " <option value='1' > Yes </option> <option value='0' SELECTED> No </option>	";
			}
			else {
				attributeImpactsVersion = " <option value='1' SELECTED > Yes </option> <option value='0' > No </option>	";
			}

			
			
			
			if (request.getParameter("attributeImpactsTraceability").equals("0")) {
				attributeImpactsTraceability = " <option value='1' > Yes </option> <option value='0' SELECTED> No </option>	";
			}
			else {
				attributeImpactsTraceability = " <option value='1' SELECTED > Yes </option> <option value='0' > No </option>	";
			}

			
			
			
			
			if (request.getParameter("attributeImpactsApprovalWorkflow").equals("0")) {
				attributeImpactsApprovalWorkflow = " <option value='1' > Yes </option> <option value='0' SELECTED> No </option>	";
			}
			else {
				attributeImpactsApprovalWorkflow = " <option value='1' SELECTED > Yes </option> <option value='0' > No </option>	";
			}
			
		} 
			
		String	attributeFormButton = "<input type='button' class= 'btn btn-sm btn-primary 'id='createAttributeButton' " + 
			" name='Create Attribute' value='Create Attribute' onClick='createRTAttribute(" ;
		attributeFormButton +=  requirementTypeId + ")'>";
	
		// Delete Attribute button  is displayed, only if the attribute already exists. i.e rTAttributeIDString is not null
		String deleteAttributeButton = "";
	%>
	
	<div id='createAttributeDiv' class='level1Box'>	
	<form method="post" id="createRTAttributeForm" action="">
		<input type="hidden" name="requirementTypeId" value="<%=requirementTypeId %>"  > 
	<table width='100%'>
		<tr>
			<td colspan="4"  align='left' > 
				<span class='subSectionHeadingText'>
				<b>Add a Custom Attribute</b>  
				</span> 
			</td>
		</tr>
		<%=statusMessage %>
	
		<tr>
			<td class='normalTableCell' width='200px' >
				<span class='headingText'> Name</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td class='normalTableCell' >
				<span class='normalText'> 
				<input type="text"  name="attributeName" value='<%=attributeName%>' size="30" maxlength="100">
				</span> 
			</td>
			<td class='normalTableCell' > 
				<span class='headingText'> Type </span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td> 
			<td class='normalTableCell' >
				<span class='normalText'>
				<select name="attributeType" id="attributeType"
					onchange='
					if(document.getElementById("attributeType").selectedIndex > 2){
						// selectedIndex = 1 is drop down and 2 is drop down Multiple.
						document.getElementById("attributeDropDownOptions").value="";
						document.getElementById("attributeDropDownOptions").disabled=true;
						
						document.getElementById("parentAttributeId").selectedIndex = 0;
						document.getElementById("parentAttributeId").disabled=true;
						
					}
					else {
						document.getElementById("attributeDropDownOptions").disabled=false;
						document.getElementById("parentAttributeId").disabled=false;
					}
					'
				><%=attributeType %></select>
				</span>
			</td>
		</tr>
		<tr>
			<td class='normalTableCell' >
				<span class='headingText'> Sort Order </span>
				<sup><span style="color: #ff0000;">*</span></sup>
			</td> 
			<td class='normalTableCell' >
				<span class='normalText'> 
				<input type="text"  name="attributeSortOrder"  value='<%=attributeSortOrder%>'  size="3" maxlength="3">
				</span> 
			</td>
			<td class='normalTableCell' > 
				<span class='headingText'>Required </span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td> 
			<td class='normalTableCell' >
				<span class='normalText'> 
				<select name="attributeRequired"><%=attributeRequired%></select>
				</span>
			</td>
		</tr>
		<tr> 
			<td class='normalTableCell' > 
				<span class='headingText'> Display in Folder View </span>
			</td>
			<td class='normalTableCell' colspan='3'>
				<span class='normalText'> 
				<select name="attributeDefaultDisplay"><%=attributeDefaultDisplay%></select>
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
			<td class='normalTableCell' > 
				<span class='headingText'> Default Value </span>
			</td>
			<td class='normalTableCell' colspan='3'>
				<span class='normalText'>
				<input type="text"  name="attributeDefaultValue" value='<%=attributeDefaultValue%>' >
				</span>
				<br>
				<span class='normalText'>
				<font color='red'>
				If you are using Parent / Child attributes (Eg: Manufacturer:Porche,Jaguar, Model:Porsche:911,Porsche:Panamera,Jaguar:XJ,Jaguar:XK etc..)
				Please set your 'Default Value' to Sub Values . In this example, for the attribute 'Model', the default value can be one of 911,Panamera, XJ, XK etc...
				</font>
				</span> 
			</td>		
		</tr>
		
		<tr> 
			<td class='normalTableCell'  >
				<span class='headingText'> Parent Attribute </span>
			</td>
			<td class='normalTableCell'  colspan="3">
				<span class='normalText'>
				 <select DISABLED id='parentAttributeId' name='parentAttributeId'>
				 	<option value='0'> </option>
				 	<%
				 	ArrayList rTAttributes = requirementType.getAllAttributesInRequirementType();
				 	Iterator rTAS = rTAttributes.iterator();
				 	while (rTAS.hasNext()){
				 		RTAttribute parentRTAttribute = (RTAttribute) rTAS.next();
				 		if (parentRTAttribute.getAttributeType().equals("Drop Down")){
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
				 
				 </div>
			</td>		
		</tr>		
		<tr> 
			<td class='normalTableCell'  >
				<span class='headingText'> Drop Down Options <br>(Comma separated)</span>
			</td>
			<td class='normalTableCell'  colspan="3">
				<span class='normalText'>
				<%
				if ((attributeDropDownOptions != null ) && (attributeDropDownOptions.length() > 0)){
					// if there is a value in the dropdownoptiosn field, lets make it editable.
				%>
					<textarea id="attributeDropDownOptions" name="attributeDropDownOptions" rows='10' cols='100'><%=attributeDropDownOptions%></textarea>	 
				<%
				}
				else{
				%>
					<textarea DISABLED id="attributeDropDownOptions" name="attributeDropDownOptions" rows='10' cols='100'><%=attributeDropDownOptions%></textarea>
				<%} %>
				 </span> 
			</td>		
					
		</tr>
			
		<tr> 
			<td class='normalTableCell' >
				<span class='headingText'> Description</span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td  class='normalTableCell' colspan="3">
				<span class='normalText'>
				<textarea name="attributeDescription" rows="5" cols="50" ><%=attributeDescription%></textarea>
				</span>
			</td>
		</tr>
		
	</table>
	
	<table>
		
		<tr>
			<td class='normalTableCell' colspan='2' > 
				<div class='alert alert-info'>Change to this attribute ...</div>
			</td>
		</tr>
		
		<tr>
			<td class='normalTableCell' style='width:250px' > 
				<span class='headingText'>Upgrades Requirement Version </span>
			</td>
			
			<td class='normalTableCell'  >
				<span class='normalText'>
				<select name="attributeImpactsVersion" id="attributeImpactsVersion"><%=attributeImpactsVersion%></select>
				</span>
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
			<td class='normalTableCell' > 
				<span class='headingText'>Causes Suspect Traces </span>
			</td>
			<td class='normalTableCell'  >
				<span class='normalText'>
				<select name="attributeImpactsTraceability"  id="attributeImpactsTraceability"><%=attributeImpactsTraceability%></select>
				</span>
			</td>
		</tr>
		
		<tr>
			<td class='normalTableCell' > 
				<span class='headingText'>Resets Approval Workflow Status to Draft</span>
			</td>
			<td class='normalTableCell' >
				<span class='normalText'>
				<select name="attributeImpactsApprovalWorkflow" id="attributeImpactsApprovalWorkflow"><%=attributeImpactsApprovalWorkflow%></select>
				</span>
			</td>
		</tr>
		
		
			
		
		
		
		<tr>
			<td  class='normalTableCell' colspan="4" align="left">
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