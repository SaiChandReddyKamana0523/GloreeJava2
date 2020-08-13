<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn  == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	else {
		return;
	}
	
	// lets get the From Requirements
   	String fromFolderIdString = request.getParameter("fromFolderId");
   	int fromFolderId = Integer.parseInt(fromFolderIdString);
	Folder	fromFolder = new Folder(fromFolderId);	

	String danglingSearchFrom = request.getParameter("danglingSearchFrom");
	String orphanSearchFrom = request.getParameter("orphanSearchFrom");
	String completedSearchFrom = request.getParameter("completedSearchFrom");
	String incompleteSearchFrom = request.getParameter("incompleteSearchFrom");
	String suspectUpStreamSearchFrom = request.getParameter("suspectUpStreamSearchFrom");
	String suspectDownStreamSearchFrom = request.getParameter("suspectDownStreamSearchFrom");
	String lockedSearchFrom = request.getParameter("lockedSearchFrom");
	String includeSubFoldersSearchFrom = request.getParameter("includeSubFoldersSearchFrom");

	int inRTBaselineSearchFrom = Integer.parseInt(request.getParameter("inRTBaselineSearchFrom"));
	int changedAfterRTBaselineSearchFrom = Integer.parseInt(request.getParameter("changedAfterRTBaselineSearchFrom"));

	//since testingStatusSearchFrom came from a multi select list, it has an extra ,.
	// we need to drop it.
	String testingStatusSearchFrom = request.getParameter("testingStatusSearchFrom");
	if ((testingStatusSearchFrom != null ) && (testingStatusSearchFrom.contains(","))) {
		testingStatusSearchFrom = (String) testingStatusSearchFrom.subSequence(0,testingStatusSearchFrom.lastIndexOf(","));
	}
	
	String nameSearchFrom = request.getParameter("nameSearchFrom");
	String descriptionSearchFrom = request.getParameter("descriptionSearchFrom");
	String ownerSearchFrom = request.getParameter("ownerSearchFrom");
	String externalURLSearchFrom = request.getParameter("externalURLSearchFrom");
	String approvedBySearchFrom = request.getParameter("approvedBySearchFrom");
	String rejectedBySearchFrom = request.getParameter("rejectedBySearchFrom");
	String pendingBySearchFrom = request.getParameter("pendingBySearchFrom");
	String traceToSearchFrom = request.getParameter("traceToSearchFrom");
	String traceFromSearchFrom = request.getParameter("traceFromSearchFrom");
	

	// since statusSearchFrom came from a multi select list, it has an extra ,.
	// we need to drop it.
	String statusSearchFrom = request.getParameter("statusSearchFrom");
	if ((statusSearchFrom != null ) && (statusSearchFrom.contains(","))) {
		statusSearchFrom = (String) statusSearchFrom.subSequence(0,statusSearchFrom.lastIndexOf(","));
	}

	// since prioritySearchFrom came from a multi select list, it has an extra ,.
	// we need to drop it.
	String prioritySearchFrom = request.getParameter("prioritySearchFrom");
	if ((prioritySearchFrom != null ) && (prioritySearchFrom.contains(","))) {
		prioritySearchFrom = (String) prioritySearchFrom.subSequence(0,prioritySearchFrom.lastIndexOf(","));
	}
	
	//  pctCompleteSearch came from a text box
	String pctCompleteSearchFrom = request.getParameter("pctCompleteSearchFrom");


	String sortBy = request.getParameter("sortBy");
	String sortByType = request.getParameter("sortByType");
	
	// Now lets handle the custom attributes.
	// Note , by the time we are done with this block of code, 
	// customAttributeSearch will have 
	//avalue1:--:avalu2sel1:##:avalu2sel1:##:avalu2sel2:--:avalue3
	String customAttributeSearchFrom ="";
	String attributeIdStringFrom = request.getParameter("attributeIdStringFrom");
	// attributeIdString has a string of attribute Ids and values in the following format.
	// id#value##id#value. eg : attributeIdString=2#DropDown##4#URL##3#Date##1#Text##
	// we need to get a list of attribute ids and get the request.getparameter values for these.
	// a typical URL looks like this : 
	//url is /GloreeJava2/servlet/RequirementAction?action=createAttributes&requirementId=1&attributeIdString=2#DropDown##4#URL
	// ##3#Date##1#Text##&2=good%20to%20have&4=external&3=datepromised&1=cost
	

	String [] attributeStrings = attributeIdStringFrom.split("##");
	String reportDefinitionCustomAttributes = "";
	for (int i=0; i<attributeStrings.length; i++ ){
		
		String [] attribute = attributeStrings[i].split("#");
		// Note : id here is the id of the attribute in requirment type. 
		// we will be using it to create an attribute value.
		String id = attribute[0];
		if (id != null){
			String aValue = request.getParameter(id);	
			reportDefinitionCustomAttributes += id + ":--:" + aValue + ":###:";

			// now lets build the custom search string, which should be in the form of
			// label:#:value
			if ((aValue != null) && (!aValue.equals(""))) {
				// id is typically in the format customA38 or customA39 where 38 adn 39 are
				// the custom Attribute Ids. so, we can drop off the customA to get the attribute id.
				if (id.contains("From")){
					id = id.replace("From", "");
				}
				if (id.contains("To")){
					id = id.replace("To", "");
				}
				
				int rTAttributeId = Integer.parseInt(id.replace("customA", ""));
				RTAttribute rTAttribute = new RTAttribute(rTAttributeId);
				
				customAttributeSearchFrom += rTAttribute.getAttributeName() + ":#:" +  aValue + ":--:";
			}

		}
	}
	// drop the last :--:
	if (customAttributeSearchFrom.contains(":--:")){
		customAttributeSearchFrom = (String) customAttributeSearchFrom.subSequence(0,customAttributeSearchFrom.lastIndexOf(":--:"));
	}

	String inRelease = "";
	ArrayList fromRequirements = ReportUtil.runListReport(securityProfile, 
			project.getProjectId(), fromFolderId, "active",
			danglingSearchFrom,orphanSearchFrom,completedSearchFrom,incompleteSearchFrom,
			suspectUpStreamSearchFrom, suspectDownStreamSearchFrom,  lockedSearchFrom, includeSubFoldersSearchFrom,
			inRTBaselineSearchFrom,changedAfterRTBaselineSearchFrom, testingStatusSearchFrom,
			nameSearchFrom, descriptionSearchFrom, ownerSearchFrom, externalURLSearchFrom,
			approvedBySearchFrom, rejectedBySearchFrom, pendingBySearchFrom , 
			traceToSearchFrom,
			traceFromSearchFrom, statusSearchFrom, prioritySearchFrom, pctCompleteSearchFrom, 
			customAttributeSearchFrom, sortBy, sortByType, inRelease, databaseType);
	
	// lets get the To Requirements
   	String toFolderIdString = request.getParameter("toFolderId");
   	int toFolderId = Integer.parseInt(toFolderIdString);
	Folder	toFolder = new Folder(toFolderId);	

	String danglingSearchTo = request.getParameter("danglingSearchTo");
	String orphanSearchTo = request.getParameter("orphanSearchTo");
	String completedSearchTo = request.getParameter("completedSearchTo");
	String incompleteSearchTo = request.getParameter("incompleteSearchTo");
	String suspectUpStreamSearchTo = request.getParameter("suspectUpStreamSearchTo");
	String suspectDownStreamSearchTo = request.getParameter("suspectDownStreamSearchTo");
	String lockedSearchTo = request.getParameter("lockedSearchTo");
	String includeSubFoldersSearchTo = request.getParameter("includeSubFoldersSearchTo");

	int inRTBaselineSearchTo = Integer.parseInt(request.getParameter("inRTBaselineSearchTo"));
	int changedAfterRTBaselineSearchTo = Integer.parseInt(request.getParameter("changedAfterRTBaselineSearchTo"));

	//since testingStatusSearchTo came from a multi select list, it has an extra ,.
	// we need to drop it.
	String testingStatusSearchTo = request.getParameter("testingStatusSearchTo");
	if ((testingStatusSearchTo != null ) && (testingStatusSearchTo.contains(","))) {
		testingStatusSearchTo = (String) testingStatusSearchTo.subSequence(0,testingStatusSearchTo.lastIndexOf(","));
	}
	
	String nameSearchTo = request.getParameter("nameSearchTo");
	String descriptionSearchTo = request.getParameter("descriptionSearchTo");
	String ownerSearchTo = request.getParameter("ownerSearchTo");
	String externalURLSearchTo = request.getParameter("externalURLSearchTo");
	String approvedBySearchTo = request.getParameter("approvedBySearchTo");
	String rejectedBySearchTo = request.getParameter("rejectedBySearchTo");
	String pendingBySearchTo = request.getParameter("pendingBySearchTo");
	String traceToSearchTo = request.getParameter("traceToSearchTo");
	String traceFromSearchTo = request.getParameter("traceFromSearchTo");
	

	// since statusSearchTo came from a multi select list, it has an extra ,.
	// we need to drop it.
	String statusSearchTo = request.getParameter("statusSearchTo");
	if ((statusSearchTo != null ) && (statusSearchTo.contains(","))) {
		statusSearchTo = (String) statusSearchTo.subSequence(0,statusSearchTo.lastIndexOf(","));
	}

	// since prioritySearchTo came from a multi select list, it has an extra ,.
	// we need to drop it.
	String prioritySearchTo = request.getParameter("prioritySearchTo");
	if ((prioritySearchTo != null ) && (prioritySearchTo.contains(","))) {
		prioritySearchTo = (String) prioritySearchTo.subSequence(0,prioritySearchTo.lastIndexOf(","));
	}
	
	//  pctCompleteSearch came from a text box
	String pctCompleteSearchTo = request.getParameter("pctCompleteSearchTo");


	// Now lets handle the custom attributes.
	// Note , by the time we are done with this block of code, 
	// customAttributeSearch will have 
	//avalue1:--:avalu2sel1:##:avalu2sel1:##:avalu2sel2:--:avalue3
	String customAttributeSearchTo ="";
	String attributeIdStringTo = request.getParameter("attributeIdStringTo");
	// attributeIdString has a string of attribute Ids and values in the following format.
	// id#value##id#value. eg : attributeIdString=2#DropDown##4#URL##3#Date##1#Text##
	// we need to get a list of attribute ids and get the request.getparameter values for these.
	// a typical URL looks like this : 
	//url is /GloreeJava2/servlet/RequirementAction?action=createAttributes&requirementId=1&attributeIdString=2#DropDown##4#URL
	// ##3#Date##1#Text##&2=good%20to%20have&4=external&3=datepromised&1=cost
	
	
	String displayHeaderFrom = request.getParameter("displayHeaderFrom");
	if ((displayHeaderFrom == null) || (displayHeaderFrom.equals("") )){
			displayHeaderFrom = "";
	}

	String displayHeaderTo = request.getParameter("displayHeaderTo");
	if ((displayHeaderTo == null) || (displayHeaderTo.equals("") )){
		displayHeaderTo = "";
	}
	
	attributeStrings = attributeIdStringTo.split("##");
	reportDefinitionCustomAttributes = "";
	for (int i=0; i<attributeStrings.length; i++ ){
		
		String [] attribute = attributeStrings[i].split("#");
		// Note : id here is the id of the attribute in requirment type. 
		// we will be using it to create an attribute value.
		String id = attribute[0];
		if (id != null){
			String aValue = request.getParameter(id);	
			reportDefinitionCustomAttributes += id + ":--:" + aValue + ":###:";

			// now lets build the custom search string, which should be in the form of
			// label:#:value
			if ((aValue != null) && (!aValue.equals(""))) {
				// id is typically in the format customA38 or customA39 where 38 adn 39 are
				// the custom Attribute Ids. so, we can drop off the customA to get the attribute id.
				if (id.contains("From")){
					id = id.replace("From", "");
				}
				if (id.contains("To")){
					id = id.replace("To", "");
				}
				
				int rTAttributeId = Integer.parseInt(id.replace("customA", ""));
				RTAttribute rTAttribute = new RTAttribute(rTAttributeId);
				
				customAttributeSearchTo += rTAttribute.getAttributeName() + ":#:" +  aValue + ":--:";
			}

		}
	}
	// drop the last :--:
	if (customAttributeSearchTo.contains(":--:")){
		customAttributeSearchTo = (String) customAttributeSearchTo.subSequence(0,customAttributeSearchTo.lastIndexOf(":--:"));
	}

	
	ArrayList toRequirements = ReportUtil.runListReport(securityProfile, 
			project.getProjectId(), toFolderId, "active",
			danglingSearchTo,orphanSearchTo,completedSearchTo,incompleteSearchTo,
			suspectUpStreamSearchTo, suspectDownStreamSearchTo,  lockedSearchTo, includeSubFoldersSearchTo,
			inRTBaselineSearchTo,changedAfterRTBaselineSearchTo, testingStatusSearchTo,
			nameSearchTo, descriptionSearchTo, ownerSearchTo, externalURLSearchTo,
			approvedBySearchTo, rejectedBySearchTo, pendingBySearchTo , 
			traceToSearchTo,
			traceFromSearchTo, statusSearchTo, prioritySearchTo, pctCompleteSearchTo, 
			customAttributeSearchTo, sortBy, sortByType, inRelease, databaseType);
	
	
	if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" + toFolder.getFolderId()))){
	%>
		<table class='paddedTable' >
			<tr>
				<td align='left' colspan='2'>				
					<div class='alert alert-success'>	
					<span class='subSectionHeadingText'>
					You do not have READ permissions on folder '<%=fromFolder.getFolderPath() %>'. 
					</span>
					</div>
				</td>
			</tr>
		</table>
	<%}
	else if (fromRequirements.size()==0){
	%>
		<table class='paddedTable' >
			<tr>
				<td align='left' colspan='2'>				
					<div class='alert alert-success'>	
					<span class='subSectionHeadingText'>
					There are no requirements in the folder '<%=fromFolder.getFolderPath() %>'. 
					</span>
					</div>
				</td>
			</tr>
		</table>
	<%		
	}
	else if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" + toFolder.getFolderId()))){
	%>
		<table class='paddedTable' >
			<tr>
				<td align='left' colspan='2'>				
					<div class='alert alert-success'>	
					<span class='subSectionHeadingText'>
					You do not have READ permissions on folder '<%=toFolder.getFolderPath() %>'. 
					</span>
					</div>
				</td>
			</tr>
		</table>
	<%}
	else if (toRequirements.size()==0){
	%>
		<table class='paddedTable' >
			<tr>
				<td align='left' colspan='2'>				
					<div class='alert alert-success'>	
					<span class='subSectionHeadingText'>
					There are no requirements in the folder '<%=toFolder.getFolderPath() %>'. 
					</span>
					</div>
				</td>
			</tr>
		</table>
	<%		
	}	
	else { 
		
		// for pagination, lets set the pageSize.
		int toPageSize = 20;
		int fromPageSize = 20;
		
				try {
			toPageSize = Integer.parseInt(request.getParameter("toPageSize"));
		}
		catch (Exception e){
			toPageSize = 20;
		}
		
		
		try {
			fromPageSize = Integer.parseInt(request.getParameter("fromPageSize"));
		}
		catch (Exception e){
			fromPageSize = 20;
		}
				
		// we add 1 to arraysize/pageSize because, int div truncates things.
		int numOfToPages = (toRequirements.size() / toPageSize) + 1 ; 	
		int numOfFromPages = (fromRequirements.size() / fromPageSize) + 1 ; 	
		
				
		int toPageToDisplay = 1;
		if (request.getParameter("toPage") != null){
			toPageToDisplay = Integer.parseInt(request.getParameter("toPage"));	
		}
		
		int fromPageToDisplay = 1;
		if (request.getParameter("fromPage") != null){
			fromPageToDisplay = Integer.parseInt(request.getParameter("fromPage"));	
		}
		
		
		int toPageStartIndex = (toPageToDisplay * toPageSize) - toPageSize;
		int toPageEndIndex = toPageStartIndex + toPageSize;
		if (toPageEndIndex > toRequirements.size()){
			toPageEndIndex = toRequirements.size();
		}
		
		int fromPageStartIndex = (fromPageToDisplay * fromPageSize) - fromPageSize;
		int fromPageEndIndex = fromPageStartIndex + fromPageSize;
		if (fromPageEndIndex > fromRequirements.size()){
			fromPageEndIndex = fromRequirements.size();
		}
		
		
		String toPageString = "<span class='normalText'>Next pages (To) :  </span>";
		for (int i=1;i<=numOfToPages;i++){
			if (i == toPageToDisplay){
				toPageString += "<b>" + i + "</b>&nbsp;&nbsp;|&nbsp;&nbsp;";
			}
			else {
				toPageString += "<a href='#' onclick='fillTracePanel(" + fromPageToDisplay + "," + i + ", \"readWrite\"); '> " + i +" </a> ";
				toPageString += "&nbsp;&nbsp;|&nbsp;&nbsp; ";	
			}
		}
		// drop the last nbsp;
		toPageString = (String) toPageString.subSequence(0,toPageString.lastIndexOf("&nbsp;&nbsp;|&nbsp;&nbsp;"));

		String fromPageString = "<span class='normalText'>Next pages (From) :  </span>";
		for (int i=1;i<=numOfFromPages;i++){
			if (i == fromPageToDisplay){
				fromPageString += "<b>" + i + "</b>&nbsp;&nbsp;|&nbsp;&nbsp;";
			}
			else {
				fromPageString += "<a href='#' onclick='fillTracePanel(" + i+ "," + toPageToDisplay + ",\"readWrite\"); '> " + i +" </a> ";
				fromPageString += "&nbsp;&nbsp;|&nbsp;&nbsp; ";	
			}
		}
		// drop the last nbsp;
		fromPageString = (String) fromPageString.subSequence(0,fromPageString.lastIndexOf("&nbsp;&nbsp;|&nbsp;&nbsp;"));

		if (fromRequirements.size() > fromPageSize){
			%>
			<table class='paddedTable' >
			<tr>
				<td align='left' colspan='2'>				
					<span class='subSectionHeadingText'>
						<%=fromPageString %>
					</span>
				</td>
			</tr>
		</table>
		<%
		}
		
		
		if (toRequirements.size() > toPageSize ){
			%>
			<table class='paddedTable' >
			<tr>
				<td align='left' colspan='2'>				
					<span class='subSectionHeadingText'>
						<%=toPageString%>
					</span>
				</td>
			</tr>
			</table>
		<%
		}

	%>
	
						
						
						
		<form>
		<input type='hidden' name='lastMenuItem' id='lastMenuItem' value='startingValue'>
		<input type='hidden' name='lastTrace' id='lastTrace' value='startingValue'>
		<input type='hidden' name='lastHeaderMenu' id='lastHeaderMenu' value='startingValue'>
		

		</form>
		<table  border=6 class='table' bordercolor='lightgray' >
		<% 
		////////////////////////////////////////////////////////////////////////////////////////////////
		// this is the first row, so lets print all the To Requirements in the table header.       /////
		////////////////////////////////////////////////////////////////////////////////////////////////
		%>
		<tr>
			<td style="width:300px" align='center'>
							
							<div class='alert alert-danger'>
								<span class='normalText'>
								<font color='red'><%=toFolder.getFolderPath() %></font>
								</span>
					
								<span class='sectionHeadingText'>
								
								&nbsp;&nbsp;&nbsp;&nbsp;To Reqs (Up Stream) &nbsp; <img src="/GloreeJava2/images/arrow_right.png"  width="16" border="0">
								</span>
							</div>
						
							<div class='alert alert-danger'>
								<span class='sectionHeadingText'>
								From Reqs  (Down Stream)
								</span>
							<br>
								<img src="/GloreeJava2/images/arrow_down.png"  width="16" border="0">
							<br>
								<span class='normalText'>
								<font color='red'><%=fromFolder.getFolderPath() %></font>
								</span>
							</div>
				</td>
		<%
		int colNum = 0;
		Iterator toI = toRequirements.iterator();
		while (toI.hasNext()){
			colNum ++;
			// remember it's criical to iterate through the to Requirements before you do the continue / break decision.
			Requirement toRequirement = (Requirement) toI.next();
			int toRequirementId = toRequirement.getRequirementId();
			String toRequirementFullTag = toRequirement.getRequirementFullTag();
			
			String toRequirementName = toRequirement.getRequirementNameForHTML();
			if ((toRequirementName!= null) && (toRequirementName.length() > 100)){
				toRequirementName = toRequirementName.substring(0, 100) + "...";
			}
			String toRequirementDescription = toRequirement.getRequirementDescription();
			
			String displayTo = "";
			if (displayHeaderTo.equals("tag")){
				displayTo = toRequirement.getRequirementFullTag();
			}
			else if (displayHeaderTo.equals("name")){
				displayTo = toRequirement.getRequirementNameForHTML();
				if ((displayTo!= null) && (displayTo.length() > 100)){
					displayTo = displayTo.substring(0, 100) + "...";
				}
			}
			else if (displayHeaderTo.equals("description")){
				displayTo = toRequirement.getRequirementDescription();
			}
			else {
				displayTo = displayHeaderTo + " : " + toRequirement.getAttributeValue(displayHeaderTo);
			}
			
			
			
			
			
			if (colNum <= toPageStartIndex ){
				continue;
			}
			if (colNum > toPageEndIndex){
				break;
			}
			String linkTitle = toRequirementFullTag + " : " + toRequirement.getRequirementNameForHTML();
			if (linkTitle.contains("'")){
				linkTitle = linkTitle.replace("'", " ");
			}
			
			// we disable to Traceto ability if the user does not have TraceTo permissions to this requirement.
			String traceToDisabledString = "";
			if 	(!(securityProfile.getPrivileges().contains("traceToRequirementsInFolder" 
							+ toRequirement.getFolderId())))
				
			{
				traceToDisabledString = "disabled='disabled'";
			}
			
			%>
			<td valign='top' style='height:100%; text-align:center;' class='warning'>
				<div 
					onMouseOver=' 	
						// lets hide any individual tracecell background
						var lastMenuItemObject = document.getElementById("lastMenuItem");
						if (document.getElementById(lastMenuItemObject.value) != null){
							document.getElementById(lastMenuItemObject.value).style.visibility="hidden";
						}
						
						// lets hide any header cell background
						var lastHeaderMenuObject = document.getElementById("lastHeaderMenu");
						if (document.getElementById(lastHeaderMenuObject.value) != null){
							document.getElementById(lastHeaderMenuObject.value).style.visibility="hidden";
						}
						
						lastHeaderMenuObject.value = "menu<%=toRequirementFullTag%>";
						document.getElementById(lastHeaderMenuObject.value).style.visibility="visible";
						document.getElementById(lastHeaderMenuObject.value).style.background="lightgray";
					'>
							
				<table>
				<tr><td>
				&nbsp;
				<span class='normalText' title='<%=linkTitle%>'>
					<a href="#" 
						onClick='
							document.getElementById("contentCenterE").style.display = "none";
							document.getElementById("contentCenterF").style.display = "none";
			
						displayFolderInExplorer(<%=toRequirement.getFolderId()%>);
						displayFolderContentCenterA(<%=toRequirement.getFolderId() %>);
						displayFolderContentRight(<%=toRequirement.getFolderId() %>);		 								
						displayRequirement(<%=toRequirementId%>,"Trace Matrix", <%=toRequirement.getFolderId() %>);
						
						// since we are showing the requirement, lets expand the layout to show content right
						//layout.getUnitByPosition("left").expand();
						'		 									
						>
					<%=toRequirementFullTag%></a> <br>	<%=displayTo %>
					
					
								
				</span>
				</td></tr>
				<tr><td>
							<table id='menu<%=toRequirementFullTag%>' style='visibility:hidden' >
								<tr>
									<td align='left'>
										<span class='normalText'>
										<select <%=traceToDisabledString %> id='select<%=toRequirementFullTag%>'
											onChange='bulkTraceInTracePanel("<%=toRequirementFullTag%>");' >
											<option value='-1'>Action</option>
											<option value='createTrace'>Create</option>
											<option value='clearTrace'>Clear</option>
											<option value='suspectTrace'>Suspect</option>
											<option value='deleteTrace'>Delete</option>
											
										</select>
										</span>
									</td>
								</tr>
							</table>
				</td></tr></table>		
				</div>		
			</td>
			<%
		}
		%>
		</tr>
		
		<%

		////////////////////////////////////////////////////////////////////////////////////////////////
		// this is the second row, so lets print all the From Requirements in the table            /////
		////////////////////////////////////////////////////////////////////////////////////////////////		
		int rowNum = 0;
		Iterator fromI = fromRequirements.iterator();
		
		while (fromI.hasNext()){
			Requirement fromRequirement = (Requirement) fromI.next();
			int fromRequirementId = fromRequirement.getRequirementId();
			String fromRequirementFullTag = fromRequirement.getRequirementFullTag();
			
			String fromRequirementName = fromRequirement.getRequirementNameForHTML();
			if ((fromRequirementName!= null) && (fromRequirementName.length() > 100)){
				fromRequirementName = fromRequirementName.substring(0, 100) + "...";
			}
			
			
			String fromRequirementDescription = fromRequirement.getRequirementDescription();
			
			rowNum++;

			
			if (rowNum <= fromPageStartIndex ){
				continue;
			}
			if (rowNum > fromPageEndIndex){
				break;
			}
			toI = toRequirements.iterator();
			String linkTitle = fromRequirementFullTag + " : " + fromRequirement.getRequirementNameForHTML();
			if (linkTitle.contains("'")){
				linkTitle = linkTitle.replace("'", " ");
			}
			
			// we disable to TraceFromo ability if the user does not have TraceFrom permissions from this requirement.
			String traceFromDisabledString = "";
			if 	(!(securityProfile.getPrivileges().contains("traceFromRequirementsInFolder" 
							+ fromRequirement.getFolderId())))
				
			{
				traceFromDisabledString = "disabled='disabled'";
			}
				// Not the first row. 
				%>
				<tr>
					<td style="width:300px; text-align:left" class='warning' >
						<div 
						onMouseOver=' 	
							// lets hide any individual tracecell background
							var lastMenuItemObject = document.getElementById("lastMenuItem");
							if (document.getElementById(lastMenuItemObject.value) != null){
								document.getElementById(lastMenuItemObject.value).style.visibility="hidden";
							}
							
							// lets hide any header cell background
							var lastHeaderMenuObject = document.getElementById("lastHeaderMenu");
							if (document.getElementById(lastHeaderMenuObject.value) != null){
								document.getElementById(lastHeaderMenuObject.value).style.visibility="hidden";
							}
							
							lastHeaderMenuObject.value = "menu<%=fromRequirementFullTag%>";
							document.getElementById(lastHeaderMenuObject.value).style.visibility="visible";
							document.getElementById(lastHeaderMenuObject.value).style.background="lightgray";
						'>		
						<table style="width:300px"><tr><td>
						<span class='normalText' title='<%=linkTitle%>'>
							&nbsp;<a href="#" 
								onClick='
									document.getElementById("contentCenterE").style.display = "none";
									document.getElementById("contentCenterF").style.display = "none";
					
								displayFolderInExplorer(<%=fromRequirement.getFolderId()%>);
								displayFolderContentCenterA(<%=fromRequirement.getFolderId() %>);
								displayFolderContentRight(<%=fromRequirement.getFolderId() %>);		 								
								displayRequirement(<%=fromRequirementId%>,"Trace Matrix", <%=fromRequirement.getFolderId() %>);
								// since we are showing the requirement, lets expand the layout to show content right
								//layout.getUnitByPosition("left").expand();
								'		 									
								>
							<%=fromRequirementFullTag%></a>
							
							<%
								String displayFrom = "";
								if (displayHeaderFrom.equals("tag")){
									displayFrom = fromRequirement.getRequirementFullTag();
								}
								else if (displayHeaderFrom.equals("name")){
									displayFrom = fromRequirement.getRequirementNameForHTML();
									if ((displayFrom!= null) && (displayFrom.length() > 100)){
										displayFrom = displayFrom.substring(0, 100) + "...";
									}
								}
								else if (displayHeaderFrom.equals("description")){
									displayFrom = fromRequirement.getRequirementDescription();
								}
								else {
									displayFrom = displayHeaderFrom + " : " + fromRequirement.getAttributeValue(displayHeaderFrom);
								}
								
							
							%>
							<br>
							<%=displayFrom %>			
							
						</span>
						</td>
						</tr>
						<tr><td> 				
							<table id='menu<%=fromRequirementFullTag%>' style='visibility:hidden' >
								<tr>
									<td align='left'>
										<span class='normalText'>
										<select <%=traceFromDisabledString %>
											id='select<%=fromRequirementFullTag%>'
											onChange='bulkTraceInTracePanel("<%=fromRequirementFullTag%>");' >
											<option value='-1'>Action</option>
											<option value='createTrace'>Create</option>
											<option value='clearTrace'>Clear</option>
											<option value='suspectTrace'>Suspect</option>
											<option value='deleteTrace'>Delete</option>
											
										</select>
										</span>
									</td>
								</tr>
							</table>
						</td></tr></table>	
						</div>									
					</td>
				<%
				colNum = 0;
				toI = toRequirements.iterator();
				while (toI.hasNext()){
					colNum ++;
					String fromRequirementTraceTo = fromRequirement.getRequirementTraceTo() + ",";
					Requirement toRequirement = (Requirement) toI.next();
					
					int toRequirementId = toRequirement.getRequirementId();
					String toRequirementFullTag = toRequirement.getRequirementFullTag();
					
					
					if (colNum <= toPageStartIndex ){
						continue;
					}
					if (colNum > toPageEndIndex){
						break;
					}
					
					String disabledString = "";
					if (
							(!(securityProfile.getPrivileges().contains("traceFromRequirementsInFolder" 
							+ fromRequirement.getFolderId())))
							||
							(!(securityProfile.getPrivileges().contains("traceToRequirementsInFolder" 
									+ toRequirement.getFolderId())))
							)
					{
						disabledString = "disabled='disabled'";
					}
					
					String traceTitle = fromRequirementFullTag + " : " + fromRequirement.getRequirementNameForHTML() +
							" => "+ toRequirementFullTag + " : " + toRequirement.getRequirementNameForHTML() ;
					if (traceTitle.contains("'")){
						traceTitle = traceTitle.replace("'", " ");
					}
							
					String traceCellId = Integer.toString(fromRequirementId) + "-" + 
						Integer.toString(toRequirementId);
					String traceCellMenuId = "Menu" + traceCellId;		
					
					if (fromRequirementTraceTo.toLowerCase().contains("(s)" + toRequirementFullTag.toLowerCase() + ",")){
						// There is a suspect trace, so lets print a suspect arrow.
					%>
					<td align='center' class='danger' >
						<div 
							 title='<%=traceTitle %>' id='<%=fromRequirementId%>-<%=toRequirementId%>'
							onMouseOver=' 	
									var lastTraceObject = document.getElementById("lastTrace");
									if (document.getElementById(lastTraceObject.value) != null){
										document.getElementById(lastTraceObject.value).style.background="white";
									}
									lastTraceObject.value = "<%=fromRequirementId%>-<%=toRequirementId%>";
									
								
									var lastMenuItemObject = document.getElementById("lastMenuItem");
									if (document.getElementById(lastMenuItemObject.value) != null){
										document.getElementById(lastMenuItemObject.value).style.visibility="hidden";
									}
									lastMenuItemObject.value = "<%=traceCellMenuId%>";
									document.getElementById("<%=traceCellMenuId%>").style.visibility="visible";
									lastMenuItemObject.value = "<%=traceCellMenuId%>";
									document.getElementById("<%=traceCellMenuId%>").style.background="lightgray";
								'
						>
						<table class='table'  ><tr class='danger'><td align='center' class='danger'>
							<div >
								<span class='normalText' >
								<img src="/GloreeJava2/images/sTrace1-turned.png"  width="16" border="0">
								</span>
							</div>
							<table id='Menu<%=fromRequirementId%>-<%=toRequirementId%>' style="visibility:hidden">
								<tr>
									<td align='left'>
										<span class='normalText'>
										<select 
										class='<%=fromRequirementFullTag%> <%=toRequirementFullTag %>'
										<%=disabledString %> id='select<%=traceCellId %>' 
										onChange='traceActionInTracePanel(<%=fromRequirementId %>,<%=toRequirementId %>, "<%=toRequirementFullTag %>", "<%=traceCellId%>")'>
											<option value='-1'>Action</option>
											<option value='clearTrace'>Clear</option>
											<option value='deleteTrace'>Delete&nbsp;&nbsp;</option>
										</select>
										</span>
									</td>
								</tr>
							</table>							
						</td></tr></table>	
						</div>
					</td>
					<%
					}
					else if (fromRequirementTraceTo.toLowerCase().contains( toRequirementFullTag.toLowerCase() + ",")){
						// There is a not suspect trace, so lets print a clear trace image.
					%>
					<td align='center' class='success' >
						<div 
							title='<%=traceTitle %>' id='<%=fromRequirementId%>-<%=toRequirementId%>'
							onMouseOver=' 	
									var lastTraceObject = document.getElementById("lastTrace");
									if (document.getElementById(lastTraceObject.value) != null){
										document.getElementById(lastTraceObject.value).style.background="white";
									}
									lastTraceObject.value = "<%=fromRequirementId%>-<%=toRequirementId%>";
									
								
									var lastMenuItemObject = document.getElementById("lastMenuItem");
									if (document.getElementById(lastMenuItemObject.value) != null){
										document.getElementById(lastMenuItemObject.value).style.visibility="hidden";
									}
									lastMenuItemObject.value = "<%=traceCellMenuId%>";
									document.getElementById("<%=traceCellMenuId%>").style.visibility="visible";
									lastMenuItemObject.value = "<%=traceCellMenuId%>";
									document.getElementById("<%=traceCellMenuId%>").style.background="lightgray";
								'
						>
						<table class='table'><tr class='success'><td align='center' class='success'>
							<div >
								<span class='normalText' >
								<img src="/GloreeJava2/images/cTrace1-turned.png"  width="16" border="0">
								</span>
							</div>
							<table id='Menu<%=fromRequirementId%>-<%=toRequirementId%>' style="visibility:hidden">
								<tr>
									<td align='left'>
										<span class='normalText'>
										<select 
										 class='<%=fromRequirementFullTag%> <%=toRequirementFullTag %>'
										 <%=disabledString %> id='select<%=traceCellId %>' 
										onChange='traceActionInTracePanel(<%=fromRequirementId %>,<%=toRequirementId %>, "<%=toRequirementFullTag %>", "<%=traceCellId%>")'>
											<option value='-1'>Action</option>
											<option value='suspectTrace'>Suspect</option>
											<option value='deleteTrace'>Delete</option>
										</select>
										</span>
									</td>
								</tr>
							</table>							
						</td></tr></table>	
						</div>
					</td>

					<%
					} 
					else {
						// There is a no existing traces
					%>
					<td class='active' align='center' >
						<div  
							title='<%=traceTitle %>'  id='<%=fromRequirementId%>-<%=toRequirementId%>'
							onMouseOver=' 	
									var lastTraceObject = document.getElementById("lastTrace");
									if (document.getElementById(lastTraceObject.value) != null){
										document.getElementById(lastTraceObject.value).style.background="white";
									}
									lastTraceObject.value = "<%=fromRequirementId%>-<%=toRequirementId%>";
									
								
									var lastMenuItemObject = document.getElementById("lastMenuItem");
									if (document.getElementById(lastMenuItemObject.value) != null){
										document.getElementById(lastMenuItemObject.value).style.visibility="hidden";
									}
									lastMenuItemObject.value = "<%=traceCellMenuId%>";
									document.getElementById("<%=traceCellMenuId%>").style.visibility="visible";
									lastMenuItemObject.value = "<%=traceCellMenuId%>";
									document.getElementById("<%=traceCellMenuId%>").style.background="lightgray";
								'
						>
						<table class='table' ><tr class='active'><td align='center' class='active'>
							<div >
								<span class='normalText'>
								&nbsp;
								</span>
							</div>
							<table id='Menu<%=fromRequirementId%>-<%=toRequirementId%>' style="visibility:hidden">
								<tr>
									<td align='left'>
										<span class='normalText'>
										<select 
											class='<%=fromRequirementFullTag%> <%=toRequirementFullTag %>'
											<%=disabledString %> 
											id='select<%=traceCellId %>' 
											onChange='traceActionInTracePanel(<%=fromRequirementId %>,<%=toRequirementId %>, "<%=toRequirementFullTag %>", "<%=traceCellId%>")'>
											<option value='-1'>Action</option>
											<option value='createTrace'>Create&nbsp;&nbsp;</option>
										</select>
										</span>
									</td>
								</tr>
							</table>							
						</td></tr></table>	
						</div>
						
					</td>
					
					<%
					} 
				}
				%>
				</tr>
				<%			
		 	
		}
		%>
		
		</table>
	<%
	    }
		%>					
		
	