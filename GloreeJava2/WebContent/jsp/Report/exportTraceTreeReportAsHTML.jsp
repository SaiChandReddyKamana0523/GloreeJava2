<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>
<%@ page import="org.json.JSONArray" %>
<%@ page import="org.json.JSONObject" %>


<%

	JSONObject reqTracesFrom = new JSONObject();

	// authentication only
	String displayListReportDataIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayListReportDataIsLoggedIn == null) || (displayListReportDataIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");

	String foldersEnabledForApprovalWorkFlow = project.getFoldersEnabledForApprovalWorkFlow();

	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
		String reportType = request.getParameter("type");
		
%>
	
	<% 
		
		
		String maxRowsInTraceTreeExceeded = (String) request.getAttribute("maxRowsInTraceTreeExceeded");
		String maxRowsInTraceTreeExceededMessage = "";
		
		
		
		int folderId = Integer.parseInt(request.getParameter("folderId"));
		ArrayList traceTreeReport = (ArrayList) session.getAttribute("traceTreeReportForFolder" + folderId);




		String foldersThatnCanBeReportedDangling = FolderUtil.getFoldersThatCanBeReportedDangling(project.getProjectId());
		String foldersThatnCanBeReportedOrphan = FolderUtil.getFoldersThatCanBeReportedOrphan(project.getProjectId());

		
		// lets get standardDisplay and customAttrib display String from the session attribute
		String standardDisplay = (String) session.getAttribute("traceTreeReportStandardDisplay" + folderId );
		
		// lets get the customAttributes to display
		ArrayList<String> attributeNames = ReportUtil.getAllAttributesInAProject(project);
		ArrayList<String> attributeNamesToDisplay = new ArrayList<String>();
		for (String aN : attributeNames){
			if (standardDisplay.contains( aN + ",")){
				attributeNamesToDisplay.add(aN);
			}
		}
		
		
		String displayRequirementType = (String) session.getAttribute("displayRequirementType" + folderId);
		if (displayRequirementType == null ){
			displayRequirementType = "all";
		}
		
		
	%>
<%@page contentType="text/html;charset=UTF-8"%>

	<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
	<html>
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>TraceCloud - SAAS Agile Scrum Requirements Management - Collaborate, Define, Manage and Deliver your Customer Requirements</title>
 	 <meta name="description" content="Collaboration tools to define, manage and deliver your customer requirements on time and within budget. Significantly improves customer satisfaction ">
	<meta name="keywords" content="free requirements management, saas requirements management tool, online requirements management, doors, requisitepro, customer requirements, shared requirements, tl9000, project management, project requirements, agile, agile requirements management.">

		<!-- Gloree JS and CSS files -->
	
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/css/common.css"> 
	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	
	<script src="/GloreeJava2/js/userAccount.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userDashboard.js?v=20200630"></script>\
	
	
	<!--  Bootstrap  JS and CSS files Begin -->
	
 	 <script src="/GloreeJava2/js/jquery-3.1.1.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap-tour-standalone.min.js"></script>
	 <link href="/GloreeJava2/css/bootstrap.min.css" rel="stylesheet" media="screen">
	<link href="/GloreeJava2/css/bootstrap-tour-standalone.min.css" rel="stylesheet">
	 
	<!--  Bootstrap  JS and CSS files End -->
	
	 
	</head>
	<body> 
	
	<div class='alert alert-info'>
     		No Of Reqs : <%=traceTreeReport.size() %>
    </div>
    <div>
    <table><tr>
    
    <td>
      <input type='button' id='expandAllButton' class='btn btn-sm btn-primary' value='Expand All' 
    onclick='expandAll()'>
    	</td>
    	<td>
    
    <input type='button' id='collapseAllButton' class='btn btn-sm btn-primary' value='Collapse All' 
    style='display:none'
    onclick='collapseAll()'>
    </td>
    <td>
    <input type='button' id='hideAttributesButton'  class='btn btn-sm btn-primary' value='Hide Attributes' 
    onclick='hideAttributes()'>
  	
  	
    <input type='button' id= 'showAttributesButton' class='btn btn-sm btn-primary' value='Show Attributes' 
    style='display:none'
    onclick='showAttributes()'>
    </td>
    </tr></table>
    </div>

	
		
				
				
					<%
					if (traceTreeReport != null){
				    	if (traceTreeReport.size() == 0) {
					    	%>
				    				<div class='alert alert-success'>
				    					<span class='normalText'> Your search did not return any Requirements. Please change your search criteria
				    					and try again.
				    					</span>
				    				</div>
					    	<%
					    		
					    	}
												

						
						
				    	// lets iterate through the TTR and find all distinct req types.
				    	
				    	
				    	// lets get the list of all unique reqs in this tracetree
				    	HashSet<String> displayedReqs = new HashSet<String>();
				    	for (int i=0; i<traceTreeReport.size()  ;i++){	
				    		TraceTreeRow tTR = (TraceTreeRow) traceTreeReport.get(i);
				    		Requirement r = tTR.getRequirement();
				    		displayedReqs.add(r.getProjectShortName() + ":" + r.getRequirementFullTag());
				    	}
						for (int i=0; i<traceTreeReport.size()  ;i++){
					   		TraceTreeRow tTR = (TraceTreeRow) traceTreeReport.get(i);
					   		Requirement r = tTR.getRequirement();

					   		// lets get all the tracesFrom for this req, make a JSON object and put it in the hashmap
					   		// this will be used in the Javascript code
					   		
					   		ArrayList<String> tracesFrom = r.getRequirementTraceFromArrayList();
					   		if (tracesFrom.size() > 0 ){
						   		JSONArray tJSON = new JSONArray();
						   		for (String t: tracesFrom){
						   			tJSON.put(t);
						   		}
						   		reqTracesFrom.put(r.getProjectShortName() + ":"+ r.getRequirementFullTag(), tJSON);
					   		}		
					   		
					   	
					   		String color = "white";
							if (r.getUserDefinedAttributes().toLowerCase().contains("color:#:")){
								color = r.getAttributeValue("color");
								if ((color == null ) || (color.trim().equals("") )){
									color = "white";
								}
							}
						
				    		// a typical uda looks like this 
				    		// Customer:#: SBI:##:Delivery Estimate:#:01/01/12
				    		String uda = r.getUserDefinedAttributes();
							String[] attribs = uda.split(":##:");
							
					   		
					   		
				    		
				    		// lets skip displaying this row, if this req was not in the display list
				    		System.out.println("srt dealing with " + r.getRequirementFullTag() + " displayRequirementType is " + displayRequirementType);
				    		boolean shouldDisplay = false;
							if (displayRequirementType.contains("all")) {
								shouldDisplay = true;
								System.out.println("srt added because of all " + r.getRequirementFullTag() ); 
							} else {
								
								// means some display restrictions are in place
								if (displayRequirementType.contains(r.getRequirementTypeId() + ",")){
									shouldDisplay = true;
									System.out.println("srt added because of matching display specific one " + r.getRequirementFullTag() );
								}
								else {
									System.out.println("srt display did not match so wont add " + r.getRequirementFullTag() );
								}
							}
					   		if (!shouldDisplay){continue;}
					   		
				    		// lets color code the traceTo and traceFrom values.
				    		String[] traces = r.getRequirementTraceTo().split(",");
				    		String url = "";
							

							
					    	int cellCount = 0;
					    	
					    	
					
						 %>
						 
						 <%
						 if (tTR.getLevel()==1){ %>
						 	<!--  This is a root level Req. So, we won't show the arrow	or color-->
						 	<div class='alert rootLevelDiv <%=r.getProjectShortName() %>:<%=r.getRequirementFullTag()%>'>
						 
						 <%} %>
						 
						<%if ((tTR.getLevel() > 1) && (tTR.getTracesToSuspectRequirement() == 0)){
						%> 
				 			<!--  This is a non-root level Req with a clear trace. -->
				 			<div class='alert  alert-success nonRootLevelDiv <%=r.getProjectShortName() %>:<%=r.getRequirementFullTag()%>' style='display:none' >
				 			
						<%}
			 			if ((tTR.getLevel()>1) && (tTR.getTracesToSuspectRequirement() != 0)){
			 			%>
			 				<!--  This is a non-root level Req with a suspect trace. -->
					 		<div class='alert alert-danger  nonRootLevelDiv <%=r.getProjectShortName() %>:<%=r.getRequirementFullTag()%>' style='display:none' %>
						<%}%> 


							<table border='0'>
								<tr>
									<td style='width:500px'>
										<%url = ProjectUtil.getURL(request,r.getRequirementId(),"requirement"); %>
							 			<%
							 			String spacer = "";
							 			if (tTR.getLevel() >= 1) {
							 				for (int k= 0; k<tTR.getLevel();k++){
								 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
							 				}
							 			}
							 			
							 			if (tTR.getLevel()==1){
							 			%>
							 			<!--  This is a root level Req. So, we won't show the arrow	or color-->
							 				<%=spacer%> 
							 				<span class='normalText'>
							 					<font class='normalText'>
							 						<%=r.getProjectShortName()%>:<%=r.getRequirementFullTag() %>
												</font>
							 				</span>
							 			<%}
							 			if ((tTR.getLevel() > 1) && (tTR.getTracesToSuspectRequirement() == 0)){
							 			%> 
							 				<!--  This is a non-root level Req with a clear trace. -->
							 				
							 					<%=spacer%>
								 				<img src="/GloreeJava2/images/cTrace1.jpg" border="0" title='<%=tTR.getTraceDescriptionWithSafetyInSingleQuotes()%>'>
								 				<span class='normalText'> 
								 					<font color='green'>
														<%=r.getProjectShortName()%>:<%=r.getRequirementFullTag() %>
													</font>
								 				</span>
							 				
							 			<%}
							 			if ((tTR.getLevel()>1) && (tTR.getTracesToSuspectRequirement() != 0)){
							 			%>
							 				<!--  This is a non-root level Req with a suspect trace. -->
							 					<%=spacer%>
								 				<img src="/GloreeJava2/images/sTrace1.jpg" border="0" title='<%=tTR.getTraceDescriptionWithSafetyInSingleQuotes() %>'>
								 				<span class='normalText'> 
								 					<font color='red'>
														<%=r.getProjectShortName()%>:<%=r.getRequirementFullTag() %>	
													</font>
								 				</span>
							 			<%}%>
				 			
				 					</td>
				 					
									<td>
										&nbsp;
										<%
										// Lets add a collapse button if this req's traceFrom are actually being displayed
										// for example, a req may have traceFrom, but the depth of this tree prevents it from beng displayed.
										boolean displayCollapse = false;
										// if any one of the traceFrom exist in the display list, then display the collapse button
										for (String t : tracesFrom){
											for (String displayedReq :displayedReqs ){
												if (t.equals(displayedReq)){
													displayCollapse = true;
												}
											}
										}
										
										if(displayCollapse){
											
											
											if (tTR.getLevel() == 1) {
												%>
												<img title=" Collapse " id='collapseButton<%=i %>'
								 				src="/GloreeJava2/images/collapse.png" 
								 				style="cursor:pointer; height:30px; display:none" border="0" 
								 				onClick='collapse(<%=i %>, "<%=r.getProjectShortName() %>:<%=r.getRequirementFullTag()%>")'>
												
								 				<img title=" Expand " id='expandButton<%=i %>'
												src="/GloreeJava2/images/expand.png" 
												style="cursor:pointer; height:30px" border="0" 
												onClick='expand(<%=i %>, "<%=r.getProjectShortName() %>:<%=r.getRequirementFullTag()%>")'>
								 			<%
												}
											else{
												%>
												
								 				
								 				<img title=" Collapse " id='collapseButton<%=i %>' 
								 				src="/GloreeJava2/images/collapse.png" 
								 				style="cursor:pointer; height:30px" border="0" 
												onClick='collapse(<%=i %>, "<%=r.getProjectShortName() %>:<%=r.getRequirementFullTag()%>")'>
												
								 				<img title=" Expand "  id='expandButton<%=i %>' 
								 				src="/GloreeJava2/images/expand.png" 
								 				style="cursor:pointer; height:30px; display:none" border="0"
								 				onClick='expand(<%=i %>, "<%=r.getProjectShortName() %>:<%=r.getRequirementFullTag()%>")'>
								 			<%
											}
										} %>
									</td>
									<td>
									<%if (reportType.equals("dynamic")){ %>
										<img title=" Trace Map " src="/GloreeJava2/images/tree.gif" style="cursor:pointer; height:30px" border="0" 
										onclick="window.open('/GloreeJava2/jsp/Requirement/CIA/traceMap.jsp?requirementId=<%=r.getRequirementId()%>')">
									
										<%} %>
									</td>
									<td colspan='2' style='width:1000px'>
										<span class='normalText'>
											<%if (reportType.equals("dynamic")){ %>
											<a href='<%=url%>' target='_blank'>
												<%=r.getRequirementFullTag()%> : Ver-<%=r.getVersion()%> :  <%=r.getRequirementNameForHTML() %> 
											</a>
											<%}
											else {%>
												<%=r.getRequirementFullTag()%> : Ver-<%=r.getVersion()%> :  <%=r.getRequirementNameForHTML() %> 
											<%} %>
										</span>
									</td>						   						
									
									
									
									
		 					</tr>
			 					<% if (standardDisplay.contains("description")) {%>
								 		<tr>
								 			<td colspan='3' style='width:500px'></td>
								 			<td colspan='2' style='width:1000px'><%=r.getRequirementDescription() %></td>
									 	</tr>
								<%} %>
								<%
						 			// since req Owner is the only user entered field in this table
						 			// we are showing only the first 30 chars.
						 			String requirementOwner = r.getRequirementOwner();
						 			if ((requirementOwner != null) && (requirementOwner.length()>30)){
						 				requirementOwner = requirementOwner.substring(0,20);
						 			}
						 		%>
						 		<% if (standardDisplay.contains("owner")) {%>
						 			<tr>
							 			<td colspan='3'></td>
							 			<td style='width:150px'><span class='normalText'>Owner</span> </td>
							 			<td ><%=requirementOwner %></td>
									 </tr>
						 			
								<%} %>
								<% if (standardDisplay.contains("testingStatus")) {%>
									<tr>
							 			<td colspan='3'></td>
							 			<td style='width:150px'><span class='normalText'>Testing Status <span></span></td>
							 			<td ><%=r.getTestingStatus() %></td>
									 </tr>							 											
								<%} %>
									
									
								<% if (standardDisplay.contains("customAttributes")) {
								%>
									<tr>
						 			<td colspan='3'></td>
						 			<td colspan='2'>
						 			<div class='attributeRow'>
						 				<table class='table'>
									<%
									/*
									for every row : {
										make a map of key  values
										iterate through the 'headers'{
											for each , find the value for this key from this map and print
											REMOVE fro mmap
										}
									}
									After all rows are printed, print the custome header in Excel output
									*/
										HashMap<String, String> aMap = r.getUserDefinedAttributesHashMap();
						 				// iterate through known custom headers
						 				//Iterator<String> hI = headers.iterator();
						 				String aValue = "";
						 				for (String aName : attributeNamesToDisplay){
						 					cellCount++;
						 					//aName = hI.next().trim();
						 					boolean attributeExists = false;
						 					if (aMap.containsKey(aName)){
						 						attributeExists = true;
						 					}
						 					aValue = aMap.get(aName);
						 					if (aValue==null){aValue=" &nbsp;&nbsp;&nbsp;";}
						 					String thisAttribLabel = aName;
						 					String attribValue = aValue;
						 					
						 					if (attributeExists){
							 					%>
							 					
							 					<tr>
									 			<td class='info' style='width:150px'><span class='normalText'><%=aName%></span> </td>
							 					<td >
								 					<span class='normalText' >
					 									<%=attribValue %> &nbsp;
					 								</span>
								 				</td>
								 				</tr>
								 				<%
						 					}
						 				}
						 				%>
						 					</table>
						 				</div>
						 				</td>
						 				</tr>
								<%} %>
		 					
		 					
		 					</table>
						</div>	 		
					 <%
					 }
					    }
					%>
				
				
	
	
	<%
		// lets try to build the javascript code here
		JSONObject json = new JSONObject();
				
	
	%>
		
		<script>
		var reqTracesFrom = <%=reqTracesFrom.toString() %>;
		console.log(reqTracesFrom);
		function collapse(buttonId, parent){
			try{
				document.getElementById('collapseButton' + buttonId).style.display='none';
				document.getElementById('expandButton' + buttonId).style.display='block';
				
				var allChildren = Object.keys(getAllChildren(parent));
				console.log("children are " + allChildren);
				for (var i = 0; i < allChildren.length; i++) {
					hideItem(allChildren[i]);
				}
			}
			catch (error){
				console.log(error );
			}
		}
		
		function expand(buttonId, parent){
			try{
				document.getElementById('collapseButton' + buttonId).style.display='block';
				document.getElementById('expandButton' + buttonId).style.display='none';
				
				var allChildren = Object.keys(getAllChildren(parent));
				console.log("children are " + allChildren);
				for (var i = 0; i < allChildren.length; i++) {
					showItem(allChildren[i]);
				}
			}
			catch (error){
				console.log(error );
			}
		}
		
		
		
		function getAllChildren(parent){
			console.log("reqTracesfrom is " + reqTracesFrom);
			var children  = {}
			try{
				
				var l1Children = reqTracesFrom[parent];
				if (l1Children != null){
					for (var i = 0; i < l1Children.length; i++) {
						console.log("adding level 1 child to parent " + parent +  " --> " + l1Children[i]);
						
						children[l1Children[i]] = l1Children[i] ;
						var l2Children = reqTracesFrom[l1Children[i]];
						if (l2Children != null){
						
							for (var j = 0; j < l2Children.length; j++) {
								console.log("adding level 2 child to parent " + parent +  " --> " + l2Children[j]);
								children[l2Children[j]] = l2Children[j] ;
								
								var l3Children = reqTracesFrom[l2Children[j]];
								if (l3Children == null){break;}
								for (var k = 0; k < l3Children.length; k++) {
									console.log("adding level 3 child to parent " + parent +  " --> " + l3Children[k]);
									children[l3Children[k]] = l3Children[k] ;
									
									var l4Children = reqTracesFrom[l3Children[k]];
									if (l4Children == null){break;}
									for (var l = 0; l < l4Children.length; l++) {
										console.log("adding level 4 child to parent " + parent +  " --> " + l4Children[l]);
										children[l4Children[l]] = l4Children[l] ;
										
										var l5Children = reqTracesFrom[l4Children[l]];
										if (l5Children == null){break;}
										for (var m = 0; m < l5Children.length; m++) {
											console.log("adding level 5 child to parent " + parent +  " --> " + l5Children[m]);
											children[l5Children[m]] = l5Children[m] ;
											
											var l6Children = reqTracesFrom[l5Children[m]];
											if (l6Children == null){break;}
											for (var n = 0; n < l6Children.length; n++) {
												children[l6Children[n]] = l6Children[n] ;
												
												var l7Children = reqTracesFrom[l6Children[n]];
												if (l7Children == null){break;}
												for (var o = 0; o < l7Children.length; o++) {
													children[l7Children[o]] = l7Children[o] ;
													
													var l8Children = reqTracesFrom[l7Children[o]];
													if (l8Children == null){break;}
													for (var p = 0; p < l8Children.length; p++) {
														children[l8Children[p]] = l8Children[p] ;
														
														var l9Children = reqTracesFrom[l8Children[p]];
														if (l9Children == null){break;}
														for (var q = 0; q < l9Children.length; q++) {
															children[l9Children[q]] = l9Children[q] ;
															
															var l10Children = reqTracesFrom[l9Children[q]];
															if (l10Children == null){break;}
															for (var r = 0; r < l10Children.length; r++) {
																children[l10Children[r]] = l10Children[r] ;
															}
														}
													}
												}
											}
										}
									}
								
								}
							}
						}
					}
				}
			}
			catch (error){
				console.log(error );
			}
			return(children);
		}
		
		function hideItem(cName){
			try {
				var childElements = document.getElementsByClassName(cName);
				for (var j = 0; j < childElements.length; j++) {
					var childElement = childElements[j];
					childElement.style.display = "none";
				}
			}
			catch (error){
				console.log("In Hide Item " + cName + "error is "  + error );
			}
		}
		
		function showItem(cName){
			var childElements = document.getElementsByClassName(cName);
			for (var j = 0; j < childElements.length; j++) {
				var childElement = childElements[j];
				childElement.style.display = "block";
			}
		}
		
		function collapseAll(){
			try {
				document.getElementById('expandAllButton').style.display='block';
				document.getElementById('collapseAllButton').style.display='none';
				
				var childElements = document.getElementsByClassName("nonRootLevelDiv");
				for (var j = 0; j < childElements.length; j++) {
					var childElement = childElements[j];
					childElement.style.display = "none";
				}
			}
			catch (error){
				console.log("In CollapseAll Item " + cName + "error is "  + error );
			}
		}
		function expandAll(){
			try {
				document.getElementById('expandAllButton').style.display='none';
				document.getElementById('collapseAllButton').style.display='block';
				
				var childElements = document.getElementsByClassName("nonRootLevelDiv");
				for (var j = 0; j < childElements.length; j++) {
					var childElement = childElements[j];
					childElement.style.display = "block";
				}
			}
			catch (error){
				console.log("In CollapseAll Item " + cName + "error is "  + error );
			}
		}
		
		
		function hideAttributes(){
			try {
				document.getElementById('hideAttributesButton').style.display='none';
				document.getElementById('showAttributesButton').style.display='block';
				
				var childElements = document.getElementsByClassName("attributeRow");
				for (var j = 0; j < childElements.length; j++) {
					var childElement = childElements[j];
					childElement.style.display = "none";
				}
			}
			catch (error){
				console.log("In CollapseAll Item " + cName + "error is "  + error );
			}
		}
		
		function showAttributes(){
			try {
				document.getElementById('hideAttributesButton').style.display='block';
				document.getElementById('showAttributesButton').style.display='none';
				
				var childElements = document.getElementsByClassName("attributeRow");
				for (var j = 0; j < childElements.length; j++) {
					var childElement = childElements[j];
					childElement.style.display = "block";
				}
			}
			catch (error){
				console.log("In CollapseAll Item " + cName + "error is "  + error );
			}
		}
		</script>
	</body>
	</html>
	
<%}%>