<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String displayRequirementCoreIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayRequirementCoreIsLoggedIn == null) || (displayRequirementCoreIsLoggedIn.equals(""))){
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
		
	int folderId = Integer.parseInt(request.getParameter("folderId"));
	Folder folder = new Folder(folderId);
	RequirementType requirementType = new RequirementType(folder.getRequirementTypeId());
	ArrayList attributes = ProjectUtil.getAllAttributes(folder.getRequirementTypeId());
	
	ArrayList columnNames = (ArrayList) session.getAttribute("columnNames");
	String optionString = "<option value='-1'> Select a Column Name </option> \n";
    if (columnNames != null){
    	Iterator c = columnNames.iterator();
    	while ( c.hasNext() ) {
    		String columnNameString = (String) c.next();
    		String[] columnName = columnNameString.split(":##:");
    		optionString += "<option value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
    	}
    }
	
%>

	<form name='importFromExcelMapForm' id ='importFromExcelMapForm' method='post' action='#' >
	<div id='importFromExcelMap' class='level1Box'>		
		<table  width="100%" align="center" class='paddedTable' >
			<tr>
				<td colspan='2' align='left' bgcolor='#99CCFF'>				
					<span class='subSectionHeadingText'>
					Create New Requirements from Uploaded Excel File
					</span>
				</td>
			</tr>							
			<tr>
				<td align='left' colspan='2'>
					<span class='headingText'>Please Map your Excel file columns to Requirement Attributes.</span>				
				</td>
			</tr>
			
			<tr>
				<td width='200'>
					<span class='normalText'> Process Selected Rows</span>
				</td>
				<td>
					<span class='normalText'> 
					<select  name='processSelectedColumn'>
						<%=optionString%>
					</select>
					</span>
					<a href='#' onClick='document.getElementById("processSelectedMoreInfoDiv").style.display="block"'>More Info</a>
				</td>
			</tr>
			<tr>
				<td >
				</td>
				<td>
					<div id='processSelectedMoreInfoDiv' style='display:none;'>
						<div style='float:right'>
							<a href='#' onClick='document.getElementById("processSelectedMoreInfoDiv").style.display="none"'>
							Close
							</a>
						</div>
					<br>
					<span class='normalText'>
						This mapping allows you to process only selected rows in your excel file.
						If you would like only some rows in your Excel file to be processed, 
						Enter 'Yes' for the rows that should be processed and map the Excel Column Name to 
						'Process Selected Rows'
						<br>
						If you want to process the entire file, just ignore this option.
						
						<br><br> To control which rows should be processed, your input should like below. In the example below
						only rows 2,3,4 and 5 will be processed
						<table border=2 cellpadding=5>
							<tr>
								<td bgcolor='gray'><span class='sectionHeadingText'>Excel Row Numbers</span></td>
								<td><span class='sectionHeadingText'>Process Selected Rows</span></td>
								<td><span class='sectionHeadingText'>Name</span></td>
							</tr>
							<tr>
								<td bgcolor='gray'><span class='normalText'>1</span></td>
								<td><span class='normalText'></span></td>
								<td><span class='normalText'>Build a Car</span></td>
							</tr>
							<tr>
								<td bgcolor='gray'><span class='normalText'>2</span></td>
								<td><span class='normalText'>Yes</span></td>
								<td><span class='normalText'>Car should have a Chassis</span></td>
							</tr>
							<tr>
								<td bgcolor='gray'><span class='normalText'>3</span></td>
								<td><span class='normalText'>Yes</span></td>
								<td><span class='normalText'>Car Chassis should support 5 seats</span></td>
							</tr>
							<tr>
								<td bgcolor='gray'><span class='normalText'>4</span></td>
								<td><span class='normalText'>Yes</span></td>
								<td><span class='normalText'>Car Chassis should have Crumple Zones</span></td>
							</tr>
							
							<tr>
								<td bgcolor='gray'><span class='normalText'>5</span></td>
								<td><span class='normalText'></span>Yes</td>
								<td><span class='normalText'>Car should have wheels</span></td>
							</tr>
							<tr>
								<td bgcolor='gray'><span class='normalText'>6</span></td>
								<td><span class='normalText'></span></td>
								<td><span class='normalText'>Build a Bus</span></td>
							</tr>
							
						</table>
						
					</span>
					</div>
				</td>
			</tr>
			<tr> <td colspan=2>&nbsp;</td></tr>			
			<tr>
				<td width='200'>
					<span class='normalText'> Name </span>
					<sup><span style="color: #ff0000;">*</span></sup> 
				</td>
				<td>
					<span class='normalText'> 
					<select  name='nameColumn'>
					<%
					String preSelectedOptionString = "<option value='-1'> Select a Column Name </option> \n";
				    if (columnNames != null){
				    	Iterator c = columnNames.iterator();
				    	while ( c.hasNext() ) {
				    		String columnNameString = (String) c.next();
				    		String[] columnName = columnNameString.split(":##:");
				    		if (columnName[0].trim().toLowerCase().equals("name")){
				    			preSelectedOptionString += "<option SELECTED value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
						   }
				    		else {
				    			preSelectedOptionString += "<option value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
				    		}
				    	}
				    }					
    				%>
    				<%=preSelectedOptionString %>
					</select>
					</span>
				</td>
			</tr>
			<tr>
				<td width='200'>
					<span class='normalText'> Description </span>
					<sup><span style="color: #ff0000;">*</span></sup> 
				</td>
				<td>
					<span class='normalText'> 
					<select name='descriptionColumn'>
					<%
					preSelectedOptionString = "<option value='-1'> Select a Column Name </option> \n";
				    if (columnNames != null){
				    	Iterator c = columnNames.iterator();
				    	while ( c.hasNext() ) {
				    		String columnNameString = (String) c.next();
				    		String[] columnName = columnNameString.split(":##:");
				    		if (columnName[0].trim().toLowerCase().equals("description")){
				    			preSelectedOptionString += "<option SELECTED value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
						   }
				    		else {
				    			preSelectedOptionString += "<option value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
				    		}
				    	}
				    }					
    				%>
    				<%=preSelectedOptionString %>
						
					</select>
					</span>
				</td>
			</tr>
			<tr>
				<td width='200'>
					<span class='normalText'> Priority</span>
				</td>
				<td>
					<span class='normalText'> 
					<select name='priorityColumn'>
					<%
					preSelectedOptionString = "<option value='-1'> Select a Column Name </option> \n";
				    if (columnNames != null){
				    	Iterator c = columnNames.iterator();
				    	while ( c.hasNext() ) {
				    		String columnNameString = (String) c.next();
				    		String[] columnName = columnNameString.split(":##:");
				    		if (columnName[0].trim().toLowerCase().equals("priority")){
				    			preSelectedOptionString += "<option SELECTED value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
						   }
				    		else {
				    			preSelectedOptionString += "<option value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
				    		}
				    	}
				    }					
    				%>
    				<%=preSelectedOptionString %>
					</select>
					</span>
				</td>
			</tr>			
			<tr>
				<td width='200'>
					<span class='normalText'> Owner </span> 
				</td>
				<td>
					<span class='normalText'> 
					<select name='ownerColumn'>
					<%
					preSelectedOptionString = "<option value='-1'> Select a Column Name </option> \n";
				    if (columnNames != null){
				    	Iterator c = columnNames.iterator();
				    	while ( c.hasNext() ) {
				    		String columnNameString = (String) c.next();
				    		String[] columnName = columnNameString.split(":##:");
				    		if (columnName[0].trim().toLowerCase().equals("owner")){
				    			preSelectedOptionString += "<option SELECTED value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
						   }
				    		else {
				    			preSelectedOptionString += "<option value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
				    		}
				    	}
				    }					
    				%>
    				<%=preSelectedOptionString %>
					</select>
					</span>
				</td>
			</tr>			
			<tr>
				<td width='200'>
					<span class='normalText'> Percent Complete </span>
				</td>
				<td>
					<span class='normalText'> 
					<select name='pctCompleteColumn'>
					<%
					preSelectedOptionString = "<option value='-1'> Select a Column Name </option> \n";
				    if (columnNames != null){
				    	Iterator c = columnNames.iterator();
				    	while ( c.hasNext() ) {
				    		String columnNameString = (String) c.next();
				    		String[] columnName = columnNameString.split(":##:");
				    		if (columnName[0].trim().toLowerCase().equals("percent complete")){
				    			preSelectedOptionString += "<option SELECTED value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
						   }
				    		else {
				    			preSelectedOptionString += "<option value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
				    		}
				    	}
				    }					
    				%>
    				<%=preSelectedOptionString %>
					</select>
					</span>
				</td>
			</tr>			
			<tr>
				<td width='200'>
					<span class='normalText'> External URL</span> 
				</td>
				<td>
					<span class='normalText'> 
					<select name='externalURLColumn'>
					<%
					preSelectedOptionString = "<option value='-1'> Select a Column Name </option> \n";
				    if (columnNames != null){
				    	Iterator c = columnNames.iterator();
				    	while ( c.hasNext() ) {
				    		String columnNameString = (String) c.next();
				    		String[] columnName = columnNameString.split(":##:");
				    		if (columnName[0].trim().toLowerCase().equals("external url")){
				    			preSelectedOptionString += "<option SELECTED value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
						   }
				    		else {
				    			preSelectedOptionString += "<option value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
				    		}
				    	}
				    }					
    				%>
    				<%=preSelectedOptionString %>
					</select>
					</span>
				</td>
			</tr>			

			<tr><td align='left' colspan='2'>&nbsp;</td></tr>

			<tr>
				<td width='200'>
					<span class='normalText'> Folder Path</span> 
				</td>
				<td>
					<span class='normalText'> 
					<select name='folderPathColumn'>
					<%=optionString%>
					</select>
					</span>
				</td>
			</tr>			
			<tr>
				<td width='200'>
					<span class='normalText'> Parent Child Mapping </span>
				</td>
				<td>
					<span class='normalText'> 
					<select  name='parentChildColumn'>
						<%=optionString%>
					</select>
					</span>
					<a href='#' onClick='document.getElementById("parentChildMoreInfoDiv").style.display="block"'>More Info</a>
				</td>
			</tr>



			<tr>
				<td width='200'>
					<span class='normalText'>Existing Parent Tag </span>
				</td>
				<td>
					<span class='normalText'> 
					<select  name='existingParentColumn'>
						<%=optionString%>
					</select>
					</span>
					<a href='#' onClick='document.getElementById("existingParentMoreInfoDiv").style.display="block"'>More Info</a>
				</td>
			</tr>
			<tr>
				<td >
				</td>
				<td>
					<div id='existingParentMoreInfoDiv' style='display:none;'>
						<div style='float:right'>
							<a href='#' onClick='document.getElementById("existingParentMoreInfoDiv").style.display="none"'>
							Close
							</a>
						</div>
					<br>
					<span class='normalText'>
						You can use this feature to create Child Objects of Objects that already exist in the system.
						<br>For example, if you have 6 Business Requirements and you want them to be created as children of existing Requirement BR-1 
						<br><br>BR-1 : Build a Car <font color='red'><b>(Already Existing in the system)</b></font>
						<br>&nbsp;&nbsp;&nbsp;BR-1.1 : Car should have a Chassis  <font color='red'><b>(Newly imported child object)</b></font>
						<br>&nbsp;&nbsp;&nbsp;BR-1.2 : Car Chassis should support 5 Seats  <font color='red'><b>(Newly imported child object)</b></font>
						<br>&nbsp;&nbsp;&nbsp;BR-1.3 : Car Chassis should have Crumple Zones  <font color='red'><b>(Newly imported child object)</b></font>
						<br>&nbsp;&nbsp;&nbsp;BR-1.4 : Car should have a Wheels  <font color='red'><b>(Newly imported child object)</b></font>
						<br>BR-2 : Build a Bus  <font color='red'><b>(Newly imported independent object)</b></font>
						<br><br> Your input should look like this
						<table border=2 cellpadding=5>
							<tr>
								<td><span class='sectionHeadingText'>Existing Parent Tag</span></td>
								<td><span class='sectionHeadingText'>Name</span></td>
							</tr>
							<tr>
								<td><span class='normalText'>BR-1</span></td>
								<td><span class='normalText'>Car should have a Chassis</span></td>
							</tr>
							<tr>
								<td><span class='normalText'>BR-1</span></td>
								<td><span class='normalText'>Car Chassis should support 5 seats</span></td>
							</tr>
							<tr>
								<td><span class='normalText'>BR-1</span></td>
								<td><span class='normalText'>Car Chassis should have Crumple Zones</span></td>
							</tr>
							
							<tr>
								<td><span class='normalText'></span>BR-1</td>
								<td><span class='normalText'>Car should have wheels</span></td>
							</tr>
							<tr>
								<td><span class='normalText'></span></td>
								<td><span class='normalText'>Build a Bus</span></td>
							</tr>
							
						</table>
						
					</span>
					</div>
				</td>
			</tr>

			<tr>
				<td >
				</td>
				<td>
					<div id='parentChildMoreInfoDiv' style='display:none;'>
						<div style='float:right'>
							<a href='#' onClick='document.getElementById("parentChildMoreInfoDiv").style.display="none"'>
							Close
							</a>
						</div>
					<br>
					<span class='normalText'>
						This mapping helps you create parent-child hierarchy of Requirements as you import them from Excel.
						<br><font color='red' ><b>
						Please take care to have a parent row appear before a child row</b></font><br>
						<br>For example, if you have 6 Business Requirements and you want them to be created as 
						<br><br>BR-1 : Build a Car
						<br>&nbsp;&nbsp;&nbsp;BR-1.1 : Car should have a Chassis
						<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;BR-1.1.1 : Car Chassis should support 5 Seats
						<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;BR-1.1.2 : Car Chassis should have Crumple Zones
						<br>&nbsp;&nbsp;&nbsp;BR-1.2 : Car should have a Wheels
						<br>BR-2 : Build a Bus
						<br><br> Your input should look like this
						<table border=2 cellpadding=5>
							<tr>
								<td bgcolor='gray'><span class='sectionHeadingText'>Excel Row Numbers</span></td>
								<td><span class='sectionHeadingText'>Parent Row Number</span></td>
								<td><span class='sectionHeadingText'>Name</span></td>
							</tr>
							<tr>
								<td bgcolor='gray'><span class='normalText'>1</span></td>
								<td><span class='normalText'></span></td>
								<td><span class='normalText'>Build a Car</span></td>
							</tr>
							<tr>
								<td bgcolor='gray'><span class='normalText'>2</span></td>
								<td><span class='normalText'>1</span></td>
								<td><span class='normalText'>Car should have a Chassis</span></td>
							</tr>
							<tr>
								<td bgcolor='gray'><span class='normalText'>3</span></td>
								<td><span class='normalText'>2</span></td>
								<td><span class='normalText'>Car Chassis should support 5 seats</span></td>
							</tr>
							<tr>
								<td bgcolor='gray'><span class='normalText'>4</span></td>
								<td><span class='normalText'>2</span></td>
								<td><span class='normalText'>Car Chassis should have Crumple Zones</span></td>
							</tr>
							
							<tr>
								<td bgcolor='gray'><span class='normalText'>5</span></td>
								<td><span class='normalText'></span>1</td>
								<td><span class='normalText'>Car should have wheels</span></td>
							</tr>
							<tr>
								<td bgcolor='gray'><span class='normalText'>6</span></td>
								<td><span class='normalText'></span></td>
								<td><span class='normalText'>Build a Bus</span></td>
							</tr>
							
						</table>
						
					</span>
					</div>
				</td>
			</tr>
	
	
			<tr><td align='left' colspan='2'>&nbsp;</td></tr>
			<tr>
				<td width='200'>
					<span class='normalText'> Trace To</span>
				</td>
				<td>
					<span class='normalText'> 
					<select name='traceToColumn'>
						<%
						preSelectedOptionString = "<option value='-1'> Select a Column Name </option> \n";
					    if (columnNames != null){
					    	Iterator c = columnNames.iterator();
					    	while ( c.hasNext() ) {
					    		String columnNameString = (String) c.next();
					    		String[] columnName = columnNameString.split(":##:");
					    		if (columnName[0].trim().toLowerCase().equals("trace to")){
					    			preSelectedOptionString += "<option SELECTED value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
							   }
					    		else {
					    			preSelectedOptionString += "<option value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
					    		}
					    	}
					    }					
	    				%>
	    				<%=preSelectedOptionString %>
					</select>
					</span>
				</td>
			</tr>
			<tr>
				<td width='200'>
					<span class='normalText'> Trace From</span>
				</td>
				<td>
					<span class='normalText'> 
					<select name='traceFromColumn'>
						<%
						preSelectedOptionString = "<option value='-1'> Select a Column Name </option> \n";
					    if (columnNames != null){
					    	Iterator c = columnNames.iterator();
					    	while ( c.hasNext() ) {
					    		String columnNameString = (String) c.next();
					    		String[] columnName = columnNameString.split(":##:");
					    		if (columnName[0].trim().toLowerCase().equals("trace from")){
					    			preSelectedOptionString += "<option SELECTED value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
							   }
					    		else {
					    			preSelectedOptionString += "<option value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
					    		}
					    	}
					    }					
	    				%>
	    				<%=preSelectedOptionString %>
					</select>
					</span>
				</td>
			</tr>
			<% if (folder.getRequirementTypeName().equals("Test Results")) {%>			
				<tr><td align='left' colspan='2'>&nbsp;</td></tr>
							<tr>
				<td width='200'>
					<span class='normalText'> Testing Status</span>
				</td>
				<td>
					<span class='normalText'> 
					<select name='testingStatusColumn'>
						<%=optionString%>
					</select>
					</span>
				</td>
			</tr>
				
			<%} %>
	
			<!--  Lets display the custom attributes. -->
			<%
			// to capture attributeIds and their column mapping we need to do this 
			// more smartly.
			String mandatoryAttributeNames = "";
			String attributeIdString = "";
		    if ((attributes != null) && (attributes.size() > 0) ){
		    %>
			<tr>
				<td align='left' colspan='2'>&nbsp;</td>
			</tr>		    
		    <%
		    	Iterator i = attributes.iterator();
		    	while ( i.hasNext() ) {
		    		RTAttribute a = (RTAttribute) i.next();
					if (a.getSystemAttribute() ==1){
						// we do not want the users to set the system attributes here.
						continue;
					}

		    		attributeIdString += a.getAttributeId() + "::"; 
			%>
					<tr>
						<td width='200'>
							<span class='normalText'> <%=a.getAttributeName() %></span>
							<%if (a.getAttributeRequired() == 1){
								mandatoryAttributeNames += a.getAttributeId() + ":-:" + a.getAttributeName()  + ":#:";
							%>
								<sup><span style="color: #ff0000;">*</span></sup> 
							<%} %>
						</td>
						<td>
							<span class='normalText'> 					
							<select name='<%=a.getAttributeId()%>' id='<%=a.getAttributeId()%>'  >
								<%
								preSelectedOptionString = "<option value='-1'> Select a Column Name </option> \n";
							    if (columnNames != null){
							    	Iterator c = columnNames.iterator();
							    	while ( c.hasNext() ) {
							    		String columnNameString = (String) c.next();
							    		String[] columnName = columnNameString.split(":##:");
							    		if (columnName[0].trim().toLowerCase().equals(a.getAttributeName().trim().toLowerCase())){
							    			preSelectedOptionString += "<option SELECTED value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
									   }
							    		else {
							    			preSelectedOptionString += "<option value='" + columnName[1] + "'> " + columnName[0] + "</option> \n";
							    		}
							    	}
							    }					
			    				%>
			    				<%=preSelectedOptionString %>

							</select>
							</span>
						</td>
					</tr>			
			

			<%
				}
			}%>			
	
	
	
					
			<tr>
				<td></td>
				<td >
					<span class='normalText'> 
						<input type='hidden' name='attributeIdString' id='attributeIdString' 
						value='<%=attributeIdString%>'>
						&nbsp;&nbsp;
						<input type="button" name="Create New Requirements" value="Create New Requirements" class='btn btn-sm btn-success'
						id = "createNewRequirementsFromExcelButton" 
						onClick="createNewRequirementsFromExcel(this.form, <%=folderId%>, '<%=mandatoryAttributeNames%>')">
						<input type='button' name='Cancel' value='Cancel' class='btn btn-sm btn-danger'
						onClick='importFromExcelForm(<%=folderId%>)'>
					</span>
				</td>
			</tr>
					
		</table>
	</div>
</form>
	
	
<%}%>