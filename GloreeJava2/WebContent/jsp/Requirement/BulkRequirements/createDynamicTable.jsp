<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<% 

SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

String columnToDisplay = request.getParameter("columnToDisplay");
int rowNum = Integer.parseInt(request.getParameter("rowNum"));
int columnNameSeqStart = Integer.parseInt(request.getParameter("columnNameSeqStart"));
int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
int currentFolderId = Integer.parseInt(request.getParameter("currentFolderId"));
RequirementType requirementType = new RequirementType(requirementTypeId);
ArrayList rTAttributes = requirementType.getAllAttributesInRequirementType();

if (columnToDisplay.equals("requirementDescription")){
	if (rowNum == 0){
		// lets print the header column
	%>
			<span class='normalText' >						
			<a href="#" 
 				onClick='
 					var allElements = document.getElementsByTagName("*");
						for (j = 0; j< allElements.length; j++){
							if (allElements[j].className == "<%=columnToDisplay%>"){
								allElements[j].style.display = "none";
							}
						}
						// lets bump up the displaysequence number
						increaseDisplaySequence("requirementDescriptionSeqStart");
						var selectColumnsToDisplayObject = document.getElementById("selectColumnsToDisplay");
						selectColumnsToDisplayObject.selectedIndex = 0;
						selectColumnsToDisplayObject.options[selectColumnsToDisplayObject.length] = new Option("Description","requirementDescription");
					'>
 				<img src="/GloreeJava2/images/delete16.png" border="0"></a>
 			Description
			</span>
	<%
	}
	else {
		// lets print the data cells
		// since we want rows from 0 to 10, lets subtrack 1 from row num
		rowNum--;
		// our cell name needs to change every time we pring these cells. 
		// otherwise, Javaascript tends to remember the original cells even after you delete them from html
		int cellSeq = columnNameSeqStart + rowNum;
	%>
		<div id='<%=columnToDisplay%><%=cellSeq%>Div'>	
		<span class='normalText'>
		<input type="text" class="<%=columnToDisplay%>" name="<%=columnToDisplay%><%=cellSeq%>" 
		id="<%=columnToDisplay%><%=cellSeq%>" size="80" maxlength="2000">
		
		<a href="#"  onClick='copyDynamicCellValuesDown("textBox","<%=columnToDisplay%>", <%=cellSeq%>)'>
			<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
		</span>
		</div>
<%	
	}
}

if (columnToDisplay.equals("requirementPriority")) {
	if (rowNum == 0){
		// lets print the header column
	%>
		<span class='normalText' >						
		<a href="#" 
				onClick='
					var allElements = document.getElementsByTagName("*");
					for (j = 0; j< allElements.length; j++){
						if (allElements[j].className == "<%=columnToDisplay%>"){
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
	
	<%
	}
	else {
		// since we want rows from 0 to 10, lets subtrack 1 from row num
		rowNum--;
		// our cell name needs to change every time we pring these cells. 
		// otherwise, Javaascript tends to remember the original cells even after you delete them from html
		int cellSeq = columnNameSeqStart + rowNum;

	%>	
		<span class='normalText'>
		<select class="<%=columnToDisplay%>" name="<%=columnToDisplay%><%=cellSeq%>" 
		id="<%=columnToDisplay%><%=cellSeq%>">
			<option value="High">High </option>
			<option value="Medium" SELECTED>Medium</option>
			<option value="Low">Low</option>
		</select>
		<a href="#"  onClick='copyDynamicCellValuesDown("dropDown","<%=columnToDisplay%>", <%=cellSeq%>)'>
				<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
			</span>
		
<%
	}
}

%>

<%
if (columnToDisplay.equals("requirementOwner")) {
	if (rowNum == 0){
		// lets print the header column
	%>
		<span class='normalText' >						
		<a href="#" 
				onClick='
					var allElements = document.getElementsByTagName("*");
					for (j = 0; j< allElements.length; j++){
						if (allElements[j].className == "<%=columnToDisplay%>"){
							allElements[j].style.display = "none";
						}
					}
					// lets bump up the displaysequence number
					increaseDisplaySequence("requirementOwnerSeqStart");
					var selectColumnsToDisplayObject = document.getElementById("selectColumnsToDisplay");
					selectColumnsToDisplayObject.selectedIndex = 0;
					selectColumnsToDisplayObject.options[selectColumnsToDisplayObject.length] = new Option("Owner","requirementOwner");
				'>
				<img src="/GloreeJava2/images/delete16.png" border="0"></a>
			Owner
		</span>
	
	<%
	}
	else {
		// since we want rows from 0 to 10, lets subtrack 1 from row num
		rowNum--;
		// our cell name needs to change every time we pring these cells. 
		// otherwise, Javaascript tends to remember the original cells even after you delete them from html
		int cellSeq = columnNameSeqStart + rowNum;

	%>	
		<span class='normalText'>
		<input class="<%=columnToDisplay%>" 
		type="text"  name="<%=columnToDisplay%><%=cellSeq%>" id="<%=columnToDisplay%><%=cellSeq%>" size="80" 
		value="<%=securityProfile.getUser().getEmailId() %>">
		<a href="#"  onClick='copyDynamicCellValuesDown("textBox","<%=columnToDisplay%>", <%=cellSeq%>)'>
			<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
		</span>
		
<%
	}
}
%>





<%
if (columnToDisplay.equals("requirementPctComplete")) {
	if (rowNum == 0){
		// lets print the header column
	%>
		<span class='normalText' >						
		<a href="#" 
				onClick='
					var allElements = document.getElementsByTagName("*");
					for (j = 0; j< allElements.length; j++){
						if (allElements[j].className == "<%=columnToDisplay%>"){
							allElements[j].style.display = "none";
						}
					}
					// lets bump up the displaysequence number
					increaseDisplaySequence("requirementPctCompleteSeqStart");
					var selectColumnsToDisplayObject = document.getElementById("selectColumnsToDisplay");
					selectColumnsToDisplayObject.selectedIndex = 0;
					selectColumnsToDisplayObject.options[selectColumnsToDisplayObject.length] = new Option("Percent Complete","requirementPctComplete");
				'>
				<img src="/GloreeJava2/images/delete16.png" border="0"></a>
		 % Complete
		</span>
	
	<%
	}
	else {
		// since we want rows from 0 to 10, lets subtrack 1 from row num
		rowNum--;
		// our cell name needs to change every time we pring these cells. 
		// otherwise, Javaascript tends to remember the original cells even after you delete them from html
		int cellSeq = columnNameSeqStart + rowNum;

	%>	
		<span class='normalText'>
		<input type="text"  class="<%=columnToDisplay%>" name="<%=columnToDisplay%><%=cellSeq%>"
		 id="<%=columnToDisplay%><%=cellSeq%>" size="3" maxlength="3" value="0" 
		 onChange='
		 var pctCompleteObject = document.getElementById("<%=columnToDisplay%><%=cellSeq%>" );
		 var pctCompleteValue = pctCompleteObject.value;
		 if (isNaN(pctCompleteValue)){
				alert ("Please enter a valid number for percent complete");
				pctCompleteObject.style.backgroundColor="#FFCC99";
				pctCompleteObject.focus();
		}
		
		if ((pctCompleteValue < 0) || (pctCompleteValue > 100) ) {
			alert ("Please enter a valid number between 0 and 100 for percent complete");
			pctCompleteObject.style.backgroundColor="#FFCC99";
			pctCompleteObject.focus();
		}
		 '>
		<a href="#"  onClick='copyDynamicCellValuesDown("textBox", "<%=columnToDisplay%>", <%=cellSeq%>)'>
			<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
		</span>
		
<%
	}
}
%>







<%
if (columnToDisplay.equals("requirementExternalURL")) {
	if (rowNum == 0){
		// lets print the header column
	%>
		<span class='normalText' >						
		<a href="#" 
			onClick='
				var allElements = document.getElementsByTagName("*");
				for (j = 0; j< allElements.length; j++){
					if (allElements[j].className == "<%=columnToDisplay%>"){
						allElements[j].style.display = "none";
					}
				}
				// lets bump up the displaysequence number
				increaseDisplaySequence("requirementExternalURLSeqStart");
				var selectColumnsToDisplayObject = document.getElementById("selectColumnsToDisplay");
				selectColumnsToDisplayObject.selectedIndex = 0;
				selectColumnsToDisplayObject.options[selectColumnsToDisplayObject.length] = new Option("External URL","requirementExternalURL");
			'>
			<img src="/GloreeJava2/images/delete16.png" border="0"></a>
			External URL
		</span>
	
	<%
	}
	else {
		// since we want rows from 0 to 10, lets subtrack 1 from row num
		rowNum--;
		// our cell name needs to change every time we pring these cells. 
		// otherwise, Javaascript tends to remember the original cells even after you delete them from html
		int cellSeq = columnNameSeqStart + rowNum;

	%>	
		<span class='normalText'>
		<input type="text"  class="<%=columnToDisplay%>" name="<%=columnToDisplay%><%=cellSeq%>"
		 id="<%=columnToDisplay%><%=cellSeq%>" size="80" >
		<a href="#"  onClick='copyDynamicCellValuesDown("textBox", "<%=columnToDisplay%>", <%=cellSeq%>)'>
			<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
		</span>
		
<%
	}
}
%>





	<%
	if (columnToDisplay.equals("requirementParent")){
		if (rowNum == 0){
			// lets print the header column
		%>
			<span class='normalText' >						
			<a href="#" 
					onClick='
						var allElements = document.getElementsByTagName("*");
						for (j = 0; j< allElements.length; j++){
							if (allElements[j].className == "<%=columnToDisplay%>"){
								allElements[j].style.display = "none";
							}
						}
						// lets bump up the displaysequence number
						increaseDisplaySequence("requirementParentSeqStart");
						var selectColumnsToDisplayObject = document.getElementById("selectColumnsToDisplay");
						selectColumnsToDisplayObject.selectedIndex = 0;
						selectColumnsToDisplayObject.options[selectColumnsToDisplayObject.length] = new Option("Parent","requirementParent");
					'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>
				Parent
			</span>
		<%
		}
		else {
			// lets print the data cells
			// since we want rows from 0 to 10, lets subtrack 1 from row num
			rowNum--;
			// our cell name needs to change every time we pring these cells. 
			// otherwise, Javaascript tends to remember the original cells even after you delete them from html
			int cellSeq = columnNameSeqStart + rowNum;
		%>
			
			<div id='<%=columnToDisplay%><%=cellSeq%>Div'>
				<div id="validateParentDiv<%=rowNum%>" style="float:left"></div>	
				<span class='normalText'>
				<input type="text" class="<%=columnToDisplay%>" name="<%=columnToDisplay%><%=cellSeq%>" 
				id="<%=columnToDisplay%><%=cellSeq%>" size="80" 
				onfocus='document.getElementById("validateParentDiv<%=rowNum%>").style.display="none";'
				onblur='
					var parentTag = document.getElementById("requirementParent<%=rowNum%>").value;
					var folderObject = document.getElementById("requirementFolder<%=rowNum%>");
					var folderId = 0;
					if (folderObject != null) {
						folderId = folderObject.value;
					}
					if (folderId == 0){
					 	folderId = <%=currentFolderId%>
					}
					if (parentTag != ""){
						validateParentTagInBulkEdit(parentTag,"validateParentDiv<%=rowNum%>", folderId );
					}
				'>
				
				<a href="#"  onClick='copyDynamicCellValuesDown("textBox","<%=columnToDisplay%>", <%=cellSeq%>)'>
					<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
				</span>
			</div>
			
			
	<%	
		}
	}
	%>








	<%
	if (columnToDisplay.equals("requirementTraceTo")){
		if (rowNum == 0){
			// lets print the header column
		%>
			<span class='normalText' >						
			<a href="#" 
					onClick='
						var allElements = document.getElementsByTagName("*");
						for (j = 0; j< allElements.length; j++){
							if (allElements[j].className == "<%=columnToDisplay%>"){
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
		<%
		}
		else {
			// lets print the data cells
			// since we want rows from 0 to 10, lets subtrack 1 from row num
			rowNum--;
			// our cell name needs to change every time we pring these cells. 
			// otherwise, Javaascript tends to remember the original cells even after you delete them from html
			int cellSeq = columnNameSeqStart + rowNum;
		%>
			<div id='<%=columnToDisplay%><%=cellSeq%>Div'>
				<div id="validateTraceToDiv<%=cellSeq%>" style="float:left"></div>	
				<span class='normalText'>
				<input type="text" class="<%=columnToDisplay%>" name="<%=columnToDisplay%><%=cellSeq%>" 
				id="<%=columnToDisplay%><%=cellSeq%>" size="80" 
				onfocus='document.getElementById("validateTraceToDiv<%=rowNum%>").style.display="none";'
				onblur='
					var traceTo = document.getElementById("<%=columnToDisplay%><%=cellSeq%>").value;
					if (traceTo!= ""){
						validateTraceToInBulkEdit(traceTo,"validateTraceToDiv<%=cellSeq%>");
					}
				'>
				
				<a href="#"  onClick='copyDynamicCellValuesDown("textBox","<%=columnToDisplay%>", <%=cellSeq%>)'>
					<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
				</span>
			</div>
	<%	
		}
	}
	%>






	<%
	if (columnToDisplay.equals("requirementTraceFrom")){
		if (rowNum == 0){
			// lets print the header column
		%>
			<span class='normalText' >						
			<a href="#" 
					onClick='
						var allElements = document.getElementsByTagName("*");
						for (j = 0; j< allElements.length; j++){
							if (allElements[j].className == "<%=columnToDisplay%>"){
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
		<%
		}
		else {
			// lets print the data cells
			// since we want rows from 0 to 10, lets subtrack 1 from row num
			rowNum--;
			// our cell name needs to change every time we pring these cells. 
			// otherwise, Javaascript tends to remember the original cells even after you delete them from html
			int cellSeq = columnNameSeqStart + rowNum;
		%>
			<div id='<%=columnToDisplay%><%=cellSeq%>Div'>
				<div id='validateTraceFromDiv<%=cellSeq%>' style="float:left"> </div>
				<span class='normalText'>
				<input type="text" class="<%=columnToDisplay%>" name="<%=columnToDisplay%><%=cellSeq%>" 
				id="<%=columnToDisplay%><%=cellSeq%>" size="80" 
				onfocus='document.getElementById("validateTraceFromDiv<%=rowNum%>").style.display="none";'
				onblur='
					var traceFrom = document.getElementById("<%=columnToDisplay%><%=cellSeq%>").value;
					if (traceFrom != ""){
						validateTraceFromInBulkEdit(traceFrom,"validateTraceFromDiv<%=cellSeq%>");
					}
				'>
				
				<a href="#"  onClick='copyDynamicCellValuesDown("textBox","<%=columnToDisplay%>", <%=cellSeq%>)'>
					<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
				</span>
			</div>
	<%	
		}
	}
	%>



<%
	if (columnToDisplay.equals("requirementFolder")) {
	if (rowNum == 0){
		// lets print the header column
	%>
		<span class='normalText' >						
		<a href="#" 
				onClick='
					var allElements = document.getElementsByTagName("*");
					for (j = 0; j< allElements.length; j++){
						if (allElements[j].className == "<%=columnToDisplay%>"){
							allElements[j].style.display = "none";
						}
					}
					// lets bump up the displaysequence number
					increaseDisplaySequence("requirementFolderSeqStart");
					var selectColumnsToDisplayObject = document.getElementById("selectColumnsToDisplay");
					selectColumnsToDisplayObject.selectedIndex = 0;
					selectColumnsToDisplayObject.options[selectColumnsToDisplayObject.length] = new Option("Folder","requirementFolder");
				'>
				<img src="/GloreeJava2/images/delete16.png" border="0"></a>
			Folder
		</span>
	
	<%
	}
	else {
		// since we want rows from 0 to 10, lets subtrack 1 from row num
		rowNum--;
		// our cell name needs to change every time we pring these cells. 
		// otherwise, Javaascript tends to remember the original cells even after you delete them from html
		int cellSeq = columnNameSeqStart + rowNum;

	%>	
		<span class='normalText'>
		<select class="<%=columnToDisplay%>" name="<%=columnToDisplay%><%=cellSeq%>" 
		id="<%=columnToDisplay%><%=cellSeq%>">
			<%
			ArrayList folders = RequirementUtil.getEligibleFoldersForRequirementType(requirementTypeId);
			Iterator f = folders.iterator();
			while (f.hasNext()){
				Folder folder = (Folder) f.next();
				if (folder.getFolderId() == currentFolderId){
				%>
				<option SELECTED value="<%=folder.getFolderId()%>"><%=folder.getFolderPath() %></option>		
				<%
				}
				else {
				%>
				<option value="<%=folder.getFolderId()%>"><%=folder.getFolderPath() %></option>
				<%
				}
			}
			%>
		</select>
		<a href="#"  onClick='copyDynamicCellValuesDown("dropDown","<%=columnToDisplay%>", <%=cellSeq%>)'>
				<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
			</span>
		
<%
	}
}

%>




	<%
	Iterator rTAs = rTAttributes.iterator();
	while (rTAs.hasNext()){
		RTAttribute rTAttribute = (RTAttribute) rTAs.next();
		if (rTAttribute.getAttributeName().equals(columnToDisplay)){
		if (rowNum == 0){
			// lets print the header column
		%>
			<span class='normalText' >						
			<a href="#" 
					onClick='
						var allElements = document.getElementsByTagName("*");
						for (j = 0; j< allElements.length; j++){
							if (allElements[j].className == "<%=columnToDisplay%>"){
								allElements[j].style.display = "none";
							}
						}
						// lets bump up the displaysequence number
						increaseDisplaySequence("<%=columnToDisplay%>SeqStart");
						var selectColumnsToDisplayObject = document.getElementById("selectColumnsToDisplay");
						selectColumnsToDisplayObject.selectedIndex = 0;
						selectColumnsToDisplayObject.options[selectColumnsToDisplayObject.length] = new Option("<%=rTAttribute.getAttributeName() %>","<%=rTAttribute.getAttributeName() %>");
					'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>
				<%=rTAttribute.getAttributeName() %>
			</span>
		<%
		}
		else {
			// lets print the data cells
			// since we want rows from 0 to 10, lets subtrack 1 from row num
			rowNum--;
			// our cell name needs to change every time we pring these cells. 
			// otherwise, Javaascript tends to remember the original cells even after you delete them from html
			int cellSeq = columnNameSeqStart + rowNum;
			
			if (rTAttribute.getAttributeType().equals("Drop Down")){
				// this is a drop down. so lets display a drop down.
			%>
				<span class='normalText'>
				<select class="<%=columnToDisplay%>" name="<%=columnToDisplay%><%=cellSeq%>" 
				id="<%=columnToDisplay%><%=cellSeq%>">
					<option value=""></option>
					<%
					String attributeDropDownOptions = rTAttribute.getAttributeDropDownOptions();
					String [] options = attributeDropDownOptions.split(",");
					for (int i=0;i < options.length; i++){
						if (options[i].equals(rTAttribute.getAttributeDefaultValue())){
						%>
							<option SELECTED value="<%=options[i]%>"><%=options[i]%></option>
						<%
						}
						else{
						%>
							<option value="<%=options[i]%>"><%=options[i]%></option>
						<%	
						}
					}
					%>
				</select>
				<a href="#"  onClick='copyDynamicCellValuesDown("dropDown","<%=columnToDisplay%>", <%=cellSeq%>)'>
						<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
					</span>
			<%		
			}
			else if (rTAttribute.getAttributeType().equals("Drop Down Multiple")){
				%>
				<span class='normalText'>
				<select multiple='multiple' class="<%=columnToDisplay%>" name="<%=columnToDisplay%><%=cellSeq%>" size='5' 
				id="<%=columnToDisplay%><%=cellSeq%>">
					
					<%
					String attributeDropDownOptions = rTAttribute.getAttributeDropDownOptions();
					String [] options = attributeDropDownOptions.split(",");
					for (int i=0;i < options.length; i++){
						if (options[i].equals(rTAttribute.getAttributeDefaultValue())){
						%>
							<option SELECTED value="<%=options[i]%>"><%=options[i]%></option>
						<%
						}
						else{
						%>
							<option value="<%=options[i]%>"><%=options[i]%></option>
						<%	
						}
					}
					%>
				</select>
				<a href="#"  onClick='copyDynamicCellValuesDown("dropDownMultiple","<%=columnToDisplay%>", <%=cellSeq%>)'>
						<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
					</span>
			<%	
			}
				
			else if (rTAttribute.getAttributeType().equals("Date")){
				// lets put some javascript validation for date format.
		%>
			<div id='<%=columnToDisplay%><%=cellSeq%>Div'>	
			<span class='normalText'>
			<input type="text" class="<%=columnToDisplay%>" name="<%=columnToDisplay%><%=cellSeq%>" 
			id="<%=columnToDisplay%><%=cellSeq%>" size="12" maxlength="10" value="mm/dd/yyyy"' 
			onfocus='
			var attribute = document.getElementById("<%=columnToDisplay%><%=cellSeq%>");
			attribute.value="";
			attribute.style.backgroundColor="#FFFFFF";
			'
			onblur='
			var attribute = document.getElementById("<%=columnToDisplay%><%=cellSeq%>");
			if (attribute.value != ""){
				if (isValidDate(attribute.value)==false){
					attribute.focus()
					attribute.style.backgroundColor="#FFCC99";
				}
			}
					'>
			
			<a href="#"  onClick='copyDynamicCellValuesDown("textBox","<%=columnToDisplay%>", <%=cellSeq%>)'>
				<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
			</span>
			</div>
	<%	
			}
			else {
				// this must be a text box
				%>
				<div id='<%=columnToDisplay%><%=cellSeq%>Div'>	
				<span class='normalText'>
				<input type="text" class="<%=columnToDisplay%>" name="<%=columnToDisplay%><%=cellSeq%>" 
				id="<%=columnToDisplay%><%=cellSeq%>" value="<%=rTAttribute.getAttributeDefaultValue() %>" size="80" >
				
				<a href="#"  onClick='copyDynamicCellValuesDown("textBox","<%=columnToDisplay%>", <%=cellSeq%>)'>
					<img src="/GloreeJava2/images/arrow_down.png" border="0"></a>
				</span>
				</div>
		<%	
				
			}
		}
		}
	
	}
%>