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

	<form name='createNewRequirementsFromExcelMapForm' id ='createNewRequirementsFromExcelMapForm' method='post' action='#' >
	<div id='createNewRequirementsFromExcelMapFormDiv' class='level1Box'>		
		<table  width="100%" align="center" class='table table-borderless' >
			<tr>
				<td style='border-top:none'colspan='2' align='left' bgcolor='#99CCFF'>				
					<span class='subSectionHeadingText'>
					Update Existing Requirements from Uploaded Excel File
					</span>
				</td>
			</tr>		
			<tr>
				<td style='border-top:none'align='left' colspan='2'>
					<span class='headingText'>Please Map your Excel file columns to Requirement Attributes. 
					 </span>				
				</td>
			</tr>

			<tr><td style='border-top:none'colspan='2'> &nbsp;</td></tr>
			<tr id='processSelectedColumnRow'>
				<td style='border-top:none'width='200'>
					<span class='normalText'> Process Selected Rows</span>
				</td>
				<td style='border-top:none'>
					<span class='normalText'> 
					<select  name='processSelectedColumn' id='processSelectedColumn' 
						onchange='colorCodeExcelUpdatMap("processSelectedColumn")'>
						<%=optionString%>
					</select>
					</span>
					<a href='#' onClick='document.getElementById("processSelectedMoreInfoDiv").style.display="block"'>More Info</a>
				</td>
			</tr>
			<tr>
				<td style='border-top:none'>
				</td>
				<td style='border-top:none'>
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
								<td style='border-top:none'bgcolor='gray'><span class='sectionHeadingText'>Excel Row Numbers</span></td>
								<td style='border-top:none'><span class='sectionHeadingText'>Process Selected Rows</span></td>
								<td style='border-top:none'><span class='sectionHeadingText'>Name</span></td>
							</tr>
							<tr>
								<td style='border-top:none'bgcolor='gray'><span class='normalText'>1</span></td>
								<td style='border-top:none'><span class='normalText'></span></td>
								<td style='border-top:none'><span class='normalText'>Build a Car</span></td>
							</tr>
							<tr>
								<td style='border-top:none'bgcolor='gray'><span class='normalText'>2</span></td>
								<td style='border-top:none'><span class='normalText'>Yes</span></td>
								<td style='border-top:none'><span class='normalText'>Car should have a Chassis</span></td>
							</tr>
							<tr>
								<td style='border-top:none'bgcolor='gray'><span class='normalText'>3</span></td>
								<td style='border-top:none'><span class='normalText'>Yes</span></td>
								<td style='border-top:none'><span class='normalText'>Car Chassis should support 5 seats</span></td>
							</tr>
							<tr>
								<td style='border-top:none'bgcolor='gray'><span class='normalText'>4</span></td>
								<td style='border-top:none'><span class='normalText'>Yes</span></td>
								<td style='border-top:none'><span class='normalText'>Car Chassis should have Crumple Zones</span></td>
							</tr>
							
							<tr>
								<td style='border-top:none'bgcolor='gray'><span class='normalText'>5</span></td>
								<td style='border-top:none'><span class='normalText'></span>Yes</td>
								<td style='border-top:none'><span class='normalText'>Car should have wheels</span></td>
							</tr>
							<tr>
								<td style='border-top:none'bgcolor='gray'><span class='normalText'>6</span></td>
								<td style='border-top:none'><span class='normalText'></span></td>
								<td style='border-top:none'><span class='normalText'>Build a Bus</span></td>
							</tr>
							
						</table>
						
					</span>
					</div>
				</td>
			</tr>

			<tr><td style='border-top:none'colspan='2'> &nbsp;</td></tr>
			
			<tr id='idColumnRow' class='<%=ReportUtil.getExcelImportTRClass(columnNames, "tag")%>'>
				<td style='border-top:none'width='160'>
					<span class='normalText'> Tag (eg : BR-25)</span>
					<sup><span style="color: #ff0000;">*</span></sup> 
				</td>
				<td style='border-top:none'>
					<span class='normalText'> 
					<select  name='idColumn' id='idColumn' 
						onchange='colorCodeExcelUpdatMap("idColumn")'>
						<%
						String preSelectedOptionString = "<option value='-1'> Select a Column Name </option> \n";
					    if (columnNames != null){
					    	Iterator c = columnNames.iterator();
					    	while ( c.hasNext() ) {
					    		String columnNameString = (String) c.next();
					    		String[] columnName = columnNameString.split(":##:");
					    		if (columnName[0].trim().toLowerCase().equals("tag")){
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
			<tr><td style='border-top:none'colspan='2'> &nbsp;</td></tr>
			<tr id='nameColumnRow'  class='<%=ReportUtil.getExcelImportTRClass(columnNames, "name")%>'>
				<td style='border-top:none'width='160'>
					<span class='normalText'> Name </span>
				</td>
				<td style='border-top:none'>
					<span class='normalText'> 
					<select  name='nameColumn' id='nameColumn'  onchange='colorCodeExcelUpdatMap("nameColumn")' >
						<%
					 	preSelectedOptionString = "<option value='-1'> Select a Column Name </option> \n";
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
			<tr id='descriptionColumnRow'  class='<%=ReportUtil.getExcelImportTRClass(columnNames, "description")%>'>
				<td style='border-top:none'width='160'>
					
					<span class='normalText'> Description </span>
				</td>
				<td style='border-top:none'>
					<span class='normalText'> 
					<select name='descriptionColumn' id='descriptionColumn' 
						onchange='colorCodeExcelUpdatMap("descriptionColumn")'>
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
			<tr id='priorityColumnRow' class='<%=ReportUtil.getExcelImportTRClass(columnNames, "priority")%>'>
				<td style='border-top:none'width='160'>
					<span class='normalText'> Priority</span>
				</td>
				<td style='border-top:none'>
					<span class='normalText'> 
					<select name='priorityColumn' id='priorityColumn'
						onchange='colorCodeExcelUpdatMap("priorityColumn")'>
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
			<tr id='ownerColumnRow' class='<%=ReportUtil.getExcelImportTRClass(columnNames, "owner")%>'>
				<td style='border-top:none'width='160'>
					<span class='normalText'> Owner </span>
				</td>
				<td style='border-top:none'>
					<span class='normalText'> 
					<select name='ownerColumn' id='ownerColumn'
						onchange='colorCodeExcelUpdatMap("ownerColumn")'>
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
			<%
			int percentageCompletedDriverReqTypeId = project.getPercentageCompletedDriverReqTypeId();
			if (
				(percentageCompletedDriverReqTypeId > 0 ) 
				&&
				(percentageCompletedDriverReqTypeId  != requirementType.getRequirementTypeId())
			){
				// If this project has a percentageCompletedDriverReqTypeId value set (>0) 
				// and this requirement does not belong to the percentage complete driver
				// the update precent complete is not permitted
			%>
			<tr>
				<td style='border-top:none'width='160'>
					<span class='normalText'> Percent Complete </span>
				</td>
				<td style='border-top:none'>
					<span class='normalText'> 
					<select name='pctCompleteColumn'>
						<option value='-1'> System Generated</option> 
					</select>
					</span>
				</td>
			</tr>			
			<%
			}
			else {
				// the user can update percent complete.
			%>			
			<tr id='pctCompleteColumnRow' 
				class='<%=ReportUtil.getExcelImportTRClass(columnNames, "percent complete")%>'>
				<td style='border-top:none'width='160'>
					<span class='normalText'> Percent Complete </span>
				</td>
				<td style='border-top:none'>
					<span class='normalText'> 
					<select name='pctCompleteColumn'  id='pctCompleteColumn'
						onchange='colorCodeExcelUpdatMap("pctCompleteColumn")'>
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
			<%} %>			
			<tr id='externalURLColumnRow' 
				class='<%=ReportUtil.getExcelImportTRClass(columnNames, "external url")%>'>
				<td style='border-top:none'width='160'>
					<span class='normalText'> External URL</span>
				</td>
				<td style='border-top:none'>
					<span class='normalText'> 
					<select name='externalURLColumn'  id='externalURLColumn'
						onchange='colorCodeExcelUpdatMap("externalURLColumn")'>
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
			<tr><td style='border-top:none'align='left' colspan='2'>&nbsp;</td></tr>
			<tr id='commentColumnRow'
				class='<%=ReportUtil.getExcelImportTRClass(columnNames, "comment")%>'>
				<td style='border-top:none'width='160'>
					<span class='normalText'> Comment</span>
				</td>
				<td style='border-top:none'>
					<span class='normalText'> 
					<select name='commentColumn' id='commentColumn'
						onchange='colorCodeExcelUpdatMap("commentColumn")'>
						<%
					 	preSelectedOptionString = "<option value='-1'> Select a Column Name </option> \n";
					    if (columnNames != null){
					    	Iterator c = columnNames.iterator();
					    	while ( c.hasNext() ) {
					    		String columnNameString = (String) c.next();
					    		String[] columnName = columnNameString.split(":##:");
					    		if (columnName[0].trim().toLowerCase().equals("comment")){
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
			<tr id='commentByColumnRow'
				class='<%=ReportUtil.getExcelImportTRClass(columnNames, "comment by")%>'>
				<td style='border-top:none'width='160'>
					<span class='normalText'> Comment By</span>
				</td>
				<td style='border-top:none'>
					<span class='normalText'> 
					<select name='commentByColumn' id='commentByColumn'
						onchange='colorCodeExcelUpdatMap("commentByColumn")'>
						<%
					 	preSelectedOptionString = "<option value='-1'> Select a Column Name </option> \n";
					    if (columnNames != null){
					    	Iterator c = columnNames.iterator();
					    	while ( c.hasNext() ) {
					    		String columnNameString = (String) c.next();
					    		String[] columnName = columnNameString.split(":##:");
					    		if (columnName[0].trim().toLowerCase().equals("comment by")){
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
					<a href='#' onClick='document.getElementById("commentByInfoDiv").style.display="block"'>More Info</a>
					</span>
				</td>
			</tr>			
			<tr>
				<td style='border-top:none'>
				</td>
				<td style='border-top:none'>
					<div id='commentByInfoDiv' style='display:none;'>
						<div style='float:right'>
							<a href='#' onClick='document.getElementById("commentByInfoDiv").style.display="none"'>
							Close
							</a>
						</div>
					<br>
					<span class='normalText'>
						You can use this feature to import (add) comments to requirements on behalf of other users. This feature works only if you are an admin.
						Please ensure that you are using the commenter's TraceCloud log in id (generally the user's email) for this field value
						
						
					</span>
					</div>
				</td>
			</tr>

			<tr><td style='border-top:none'align='left' colspan='2'>&nbsp;</td></tr>

			<tr id='folderPathColumnRow' >
				<td style='border-top:none'width='200'>
					<span class='normalText'> Folder Path</span> 
				</td>
				<td style='border-top:none'>
					<span class='normalText'
						> 
					<select name='folderPathColumn' id='folderPathColumn'
						onchange='colorCodeExcelUpdatMap("folderPathColumn")'>
						<%=optionString%>
					</select>
					</span>
				</td>
			</tr>			


			<tr id='existingParentColumnRow'>
				<td style='border-top:none'width='200'>
					<span class='normalText'>New Parent Tag </span>
				</td>
				<td style='border-top:none'>
					<span class='normalText'> 
					<select  name='existingParentColumn' id='existingParentColumn'
						onchange='colorCodeExcelUpdatMap("existingParentColumn")'>
						<%=optionString%>
					</select>
					</span>
					<a href='#' onClick='document.getElementById("existingParentMoreInfoDiv").style.display="block"'>More Info</a>
				</td>
			</tr>
			<tr>
				<td style='border-top:none'>
				</td>
				<td style='border-top:none'>
					<div id='existingParentMoreInfoDiv' style='display:none;'>
						<div style='float:right'>
							<a href='#' onClick='document.getElementById("existingParentMoreInfoDiv").style.display="none"'>
							Close
							</a>
						</div>
					<br>
					<span class='normalText'>
						You can use this feature to create make objects a child of another existing object.
						<br>For example, if you have 6 existing Business Requirements (BR-1, BR-2, BR-3,BR-4, BR-5, BR-6) and you want them to be created as children of existing Requirement BR-1 
						<br><br>BR-1 : Build a Car <font color='red'><b>(Gets new children)</b></font>
						<br>&nbsp;&nbsp;&nbsp;BR-1.1 : Car should have a Chassis  <font color='red'><b>(Moved to a new parent)</b></font>
						<br>&nbsp;&nbsp;&nbsp;BR-1.2 : Car Chassis should support 5 Seats  <font color='red'><b>(Moved to a new parent)</b></font>
						<br>&nbsp;&nbsp;&nbsp;BR-1.3 : Car Chassis should have Crumple Zones  <font color='red'><b>(Moved to a new parent)</b></font>
						<br>&nbsp;&nbsp;&nbsp;BR-1.4 : Car should have a Wheels  <font color='red'><b>(Moved to a new parent)</b></font>
						<br>BR-6 : Build a Bus  <font color='red'><b>(No changes here)</b></font>
						<br><br> Your input should look like this
						<table border=2 cellpadding=5>
							<tr>
								<td style='border-top:none'><span class='sectionHeadingText'>Tag</span></td>
								<td style='border-top:none'><span class='sectionHeadingText'>New Parent Tag</span></td>
								<td style='border-top:none'><span class='sectionHeadingText'>Name</span></td>
							</tr>
							<tr>
								<td style='border-top:none'><span class='normalText'>BR-1</span></td>
								<td style='border-top:none'><span class='normalText'></span></td>
								<td style='border-top:none'><span class='normalText'>Car should have a Chassis</span></td>
							</tr>
							<tr>
								<td style='border-top:none'><span class='normalText'>BR-2</span></td>
								<td style='border-top:none'><span class='normalText'>BR-1</span></td>
								<td style='border-top:none'><span class='normalText'>Car Chassis should support 5 seats</span></td>
							</tr>
							<tr>
								<td style='border-top:none'><span class='normalText'>BR-3</span></td>
								<td style='border-top:none'><span class='normalText'>BR-1</span></td>
								<td style='border-top:none'><span class='normalText'>Car Chassis should have Crumple Zones</span></td>
							</tr>
							
							<tr>
								<td style='border-top:none'><span class='normalText'>BR-4</span></td>
								<td style='border-top:none'><span class='normalText'></span>BR-1</td>
								<td style='border-top:none'><span class='normalText'>Car should have wheels</span></td>
							</tr>
							<tr>
								<td style='border-top:none'><span class='normalText'>BR-6</span></td>
								<td style='border-top:none'><span class='normalText'></span></td>
								<td style='border-top:none'><span class='normalText'>Build a Bus</span></td>
							</tr>
							
						</table>
						
					</span>
					</div>
				</td>
			</tr>

			<tr><td style='border-top:none'colspan='2'>&nbsp;</td></tr>			
			<tr id='traceToColumnRow'>
				<td style='border-top:none'width='160'>
					<span class='normalText'> Trace To</span>
				</td>
				<td style='border-top:none'>
					<span class='normalText'> 
					<select name='traceToColumn' id='traceToColumn'
						onchange='colorCodeExcelUpdatMap("traceToColumn")'>
						<%=optionString%>
					</select>
					</span>
				</td>
			</tr>
			<tr  id='traceFromColumnRow'>
				<td style='border-top:none'width='160'>
					<span class='normalText'> Trace From</span>
				</td>
				<td style='border-top:none'>
					<span class='normalText'> 
					<select name='traceFromColumn'  id='traceFromColumn'
						onchange='colorCodeExcelUpdatMap("traceFromColumn")'>
						<%=optionString%>
					</select>
					</span>
				</td>
			</tr>			
			
				
			<!--  Lets display the custom attributes. -->
			<%
			// to capture attributeIds and their column mapping we need to do this 
			// more smartly.
			String attributeIdString = "";
		    if ((attributes != null) && (attributes.size() > 0) ){
		    %>
			<tr>
				<td style='border-top:none'align='left' colspan='2'><div class='alert alert-primary'>Custom Attributes</div></td>
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
			<tr id='<%=a.getAttributeId()%>Row' class='<%=ReportUtil.getExcelImportTRClass(columnNames, a.getAttributeName().trim().toLowerCase())%>'>
				<td style='border-top:none'width='160'>
					<span class='normalText'> <%=a.getAttributeName() %></span>
				</td>
				<td style='border-top:none'>
					<span class='normalText'> 
					<select name='<%=a.getAttributeId()%>' id='<%=a.getAttributeId()%>' 
					onchange='colorCodeExcelUpdatMap("<%=a.getAttributeId()%>")' >
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
					
					
					<%if (
							(a.getAttributeType().equals("Text Box"))
							||
							(a.getAttributeType().equals("Drop Down Multiple"))
							||
							(a.getAttributeType().equals("URL"))
						) {%>
						<select  name='appendReplace<%=a.getAttributeId()%>' id='appendReplace<%=a.getAttributeId()%>'>
							<option>Append or Replace?</option>
							<option value='append'>Append To Existing Value</option>
							<option value='replace'>Replace Existing Value</option>
						</select>
					<%}
					else {%>
						<span class='primary'> Replace </span>
					<%} %>
					</span>
				</td>
			</tr>			
			

			<%
				}
			}%>			


			<tr><td style='border-top:none'colspan='2'>&nbsp;</td></tr>			
		
			<% if (folder.getRequirementTypeName().equals("Test Results")){ %>
				
				<tr>
					<td style='border-top:none'width='160'>
						<span class='normalText'> Testing Status</span>
					</td>
					<td style='border-top:none'>
						<span class='normalText'> 
						<select name='testingStatusColumn'>
							<%=optionString%>
						</select>
						</span>
					</td>
				</tr>			
			<%} %>		
			<tr>
				<td style='border-top:none'></td>
				<td style='border-top:none'>
					<input type='hidden' name='attributeIdString' id='attributeIdString'  
					value='<%=attributeIdString%>'>
					
					<input type="button" name="Update Existing Requirements" value="Update Existing Requirements" class='btn btn-sm btn-success' 
					onClick="updateExistingRequirementsFromExcel(this.form, <%=folderId%>)">
					&nbsp;&nbsp;
					<input type='button' name='Cancel' value='Cancel' class='btn btn-sm btn-danger'
					onClick='importFromExcelForm(<%=folderId%>)'>
				</td>
			</tr>
					
		</table>
	</div>
</form>
	
	
<%}%>