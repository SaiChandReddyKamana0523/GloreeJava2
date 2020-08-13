<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xml:lang="en" xmlns="http://www.w3.org/1999/xhtml" lang="en"><head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">


	<title>TraceCloud - SAAS Agile Scrum Requirements Management - Collaborate, Define, Manage and Deliver your Customer Requirements</title>
    <meta name="description" content="Collaboration tools to define, manage and deliver your customer requirements on time and within budget. Significantly improves customer satisfaction ">
	<meta name="keywords" content="free requirements management, saas requirements management tool, online requirements management, doors, requisitepro, customer requirements, shared requirements, tl9000, project management, project requirements, agile, agile requirements management.">


	
	
	<link rel="stylesheet" href="/GloreeJava2/css/greeny.css" type="text/css" >
	<link rel="stylesheet" href="/GloreeJava2/css/common.css" type="text/css">
	<link rel="stylesheet" href="/GloreeJava2/css/sales_global.css" type="text/css">
	<link rel="stylesheet" href="/GloreeJava2/css/sales_home.css" type="text/css" media="screen">
	
	
	<!--  Bootstratp  JS and CSS files -->
	 <script src="/GloreeJava2/js/jquery-3.1.1.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap.min.js"></script>
	 <link href="/GloreeJava2/css/bootstrap.min.css" rel="stylesheet" media="screen">
	
	
	
	<!--  Google Analytics Tracking  -->	
	<script type="text/javascript">
	
	  var _gaq = _gaq || [];
	  _gaq.push(['_setAccount', 'UA-31449327-1']);
	  _gaq.push(['_trackPageview']);
	
	  (function() {
	    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
	    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	  })();
	
	</script>
	
	
		
</head>
<body >

		
	<div class="wrapper box_theme_login_wrapper">
	<jsp:include page="Common/TCToolbar.jsp" />	
	
	<div  style="border-width: 2px; border-style: solid; border-color: lightblue; padding:15px">	
		<div class="citation">
			"Amazingly responsive, and very quick turn around. Thank you !!!."
			<span>&mdash; A happy customer</span>
		</div>
		<jsp:include page="Common/TCNextStepsAndSolutions.jsp" />
		
		
		
		<div >
						<br><br>
						<span class="ttitle">TraceCloud Web Services Documentation</span>
		
						<br>
						<br>
		
						<table >
							<tr> 
								<td bgcolor='#dae6ac' color='white' colspan='2'>
								<span class='sectionHeadingText10'><b> What is a REST API</b></span>
								</td>
								
							</tr>
								<tr> 
									<td  colspan='2'>
									<span class='normalText10'>
									<br>
									REST (representational state transfer) is an approach for getting information 
									content from a Web site by reading a designated Web page that contains an 
									XML (Extensible Markup Language) file that describes and includes the desired 
									content.<br><br> 
									
									As described in a dissertation by Roy Fielding, REST is an "architectural style" that basically
									 exploits the existing technology and protocols of the Web, including HTTP (Hypertext Transfer Protocol)
									  and XML. For more information on REST, please read this article at 
									  <a href='http://en.wikipedia.org/wiki/Representational_State_Transfer'> wikipedia </a> 
									</span>
									<br><br>
									</td>							
								</tr>
							<tr>
								<td bgcolor='#dae6ac' color='white' colspan='2'>
								<span class='sectionHeadingText10'><b> What is JSON </b></span>
								</td>
								
							</tr>
								<tr> 
									<td  colspan='2'>
									<span class='normalText10'>
									<br>
									JSON, short for JavaScript Object Notation, is a lightweight computer data interchange format. 
									It is a text-based, human-readable format for representing simple data structures and associative arrays 
									(called objects).<br><br>
									
									We are using JSON, because its more human readable than XML, is more light weight
									and easier to use in AJAX applications. For more information on JSON please visit
									the <a href='http://json.org/'> JSON web site </a> 
									</span>
									<br><br>
									</td>							
								</tr>				
						
							<tr>
								<td bgcolor='#dae6ac' color='white' colspan='2'>
								<span class='sectionHeadingText10'><b> How to call the API</b></span>
								</td>
								
							</tr>
								<tr> 
									<td  colspan='2'>
									<br>
									<span class='normalText10'>
									Before you can call the Web services , you will need to have an API Key. Please reach out
									to the TraceCloud sales team to receive your API Key. Each key comes with a daily
									limit on how many calls you can make, so please work with your TraceCloud team 
									to figure out the expected number of daily API calls. 
									</span>
									<br><br>
									</td>							
								</tr>				
							<tr>
								<td bgcolor='#dae6ac' color='white' colspan='2'>
								<span class='sectionHeadingText10'><b> Web services usage</b></span>
								</td>
								
							</tr>
								<tr> 
									<td  colspan='2'>
									<br>
									<span class='normalText10'>
									The Web services can be used to programmatically manipulate your Requirements data. 
									For examples you can a) Get the list of projects you have access too and b) for these
									projects get the list of Requirements Types, Folder, Reports and Users and c) get the
									list of Requirements in each of those sets and d) manipulate the Requirements.
									<br><br>
									
									Every call returns 2 mandatory parameters, a) responseStatus which is set to 'success' or 
									'error' and b) errorMessage which is either empty or has an error message. 
									</span>
									<br><br>
									</td>							
								</tr>				
							<tr>
								<td bgcolor='#dae6ac' color='white' colspan='2'>
								<span class='sectionHeadingText10'><b> API Calls</b></span>
								</td>
								
							</tr>
								
								
								
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Get My Projects						
		////////////////////////////////////////////////////////////////////////////////////////-->						
								
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='getProjectsA'></a>
									<a href='#getProjectsA' onClick='
										document.getElementById("getMyProjectsDiv").style.display = "block";'> Get My Projects </a> 
									<div class='alert alert-success'  id='getMyProjectsDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#getProjectsA' onClick='document.getElementById("getMyProjectsDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Title</b><br>
													Get My projects
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Returns a list of projects
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>URL</b><br>
													/RESTAPI
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Method</b><br>
													GET
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													</span>
													<table  border='2'>
														<tr>
															<td>Query String Parameter</td>
															<td>Required/Optional</td>
															<td>Description</td>
															<td>Type</td>
															<td>Notes</td>
														</tr>
														<tr>
															<td>Key</td>
															<td>Required</td>
															<td>the API key</td>
															<td>String</td>
															<td></td>
														</tr>
														<tr>
															<td>Action</td>
															<td>Required</td>
															<td>Action name to return the list of projects</td>
															<td>String</td>
															<td>Value : getMyProjects</td>
														</tr>
													</table>
													
												</td>
											</tr>
											
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQO&action=getMyProjects
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre>
		{
		   "errorMessage": "",
		   "projects": [
		      {
		         "createdBy": "sami@tracecloud.com",
		         "description": "Account Summary Tool updating",
		         "name": "Account Summary Tool - Sanbox",
		         "prefix": "AST",
		         "lastModifiedBy": "sami@tracecloud.com",
		         "projectId": 30,
		         "restrictedDomains": "tracecloud.com,verisign.com,cisco.com"},
		      {
		         "createdBy": "sami@tracecloud.com",
		         "description": "we are using this projec to track the requirements
		          for developing the TraceCloud system. It's part of our effort to
		          bootstrap our application and to feel the pain points experienced
		          by a typical Requirements Management user..",
		         "name": "Trace Cloud Development",
		         "prefix": "TCD",
		         "lastModifiedBy": "sami@tracecloud.com",
		         "projectId": 19,
		         "restrictedDomains": "tracecloud.com,yahoo.com,gmail.com,hotmail.com"}],
		   "responseStatus": "success"}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
						
							
							
							
							
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Get Project Details					
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='getProjectDetailsA'></a>
									<a href='#getProjectDetailsA' onClick='
										document.getElementById("getProjectDetailsDiv").style.display = "block";'> Get Project Details </a> 
									<div class='alert alert-success' id='getProjectDetailsDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#getProjectDetailsA' onClick='document.getElementById("getProjectDetailsDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											
											
											<tr>
												<td>
													<span class='normalText10'>
													<b>Title</b><br>
													Get Project Details
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Returns project details
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>URL</b><br>
													/RESTAPI
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Method</b><br>
													GET
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													</span>
													<table  border='2'>
														<tr>
															<td>Query String Parameter</td>
															<td>Required/Optional</td>
															<td>Description</td>
															<td>Type</td>
															<td>Notes</td>
														</tr>
														<tr>
															<td>Key</td>
															<td>Required</td>
															<td>the API key</td>
															<td>String</td>
															<td></td>
														</tr>
														<tr>
															<td>Action</td>
															<td>Required</td>
															<td>Action name to return the details of a specified project</td>
															<td>String</td>
															<td>Value : getProjectDetails</td>
														</tr>
														<tr>
															<td>projectId</td>
															<td>Required</td>
															<td>The id of the project</td>
															<td>Number</td>
															<td></td>
														</tr>
													</table>
													
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQ&action=getProjectDetails&projectId=19
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre>
		{
		   "errorMessage": "",
		   "projectDetails": {
		      "createdBy": "sami@tracecloud.com",
		      "description": "we are using this projec to track the requirements for developing
		       the TraceCloud system. It's part of our effort to bootstrap our application and
		       to feel the pain points experienced by a typical Requirements Management user..",
		      "name": "Trace Cloud Development",
		      "prefix": "TCD",
		      "lastModifiedBy": "sami@tracecloud.com",
		      "projectId": 19,
		      "restrictedDomains": "tracecloud.com,yahoo.com,gmail.com,hotmail.com"},
		   "responseStatus": "success"}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
							
							
							
							
							
							
							
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Get All Requirement Types in a Project						
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='getRequirementTypesA'></a>
									<a href='#getRequirementTypesA' onClick='
										document.getElementById("getProjectRequirementTypesDiv").style.display = "block";'> Get All Requirement Types in a Project</a> 
									<div class='alert alert-success' id='getProjectRequirementTypesDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#getRequirementTypesA' onClick='document.getElementById("getProjectRequirementTypesDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Title</b><br>
													Get Requirement Types
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Returns requirement types
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>URL</b><br>
													/RESTAPI
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Method</b><br>
													GET
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													</span>
													<table  border='2'>
														<tr>
															<td>Query String Parameter</td>
															<td>Required/Optional</td>
															<td>Description</td>
															<td>Type</td>
															<td>Notes</td>
														</tr>
														<tr>
															<td>Key</td>
															<td>Required</td>
															<td>the API key</td>
															<td>String</td>
															<td></td>
														</tr>
														<tr>
															<td>Action</td>
															<td>Required</td>
															<td>Action name to return the requirement types of a specified project</td>
															<td>String</td>
															<td>Value : getProjectRequirementTypes</td>
														</tr>
														<tr>
															<td>projectId</td>
															<td>Required</td>
															<td>The id of the project</td>
															<td>Number</td>
															<td></td>
														</tr>
													</table>
													
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQ&action=getProjectRequirementTypes&projectId=39	
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre>{
		   "errorMessage": "",
		   "responseStatus": "success",
		   "requirementTypes": [
		      {
		         "createdBy": "a@b.com",
		         "description": "pew",
		         "name": "pew",
		         "prefix": "pew",
		         "requirementTypeId": 361,
		         "lastModifiedBy": "a@b.com",
		         "enableApproval": 0,
		         "rootFolderId": 442,
		         "attributes": [
		],
		         "projectId": 39},
		
		      {
		         "createdBy": "a@b.com",
		         "description": "d",
		         "name": "d",
		         "prefix": "d",
		         "requirementTypeId": 360,
		         "lastModifiedBy": "a@b.com",
		         "enableApproval": 0,
		         "rootFolderId": 441,
		         "attributes": [
		            {
		               "attributeDefaultValue": "bug",
		               "attributeDescription": "type",
		               "attributeImpactsApprovalWorkflow": 0,
		               "attributeImpactsTraceability": 0,
		               "attributeId": 45,
		               "attributeType": "Drop Down",
		               "attributeName": "type",
		               "attributeDropDownOptions": "enhancement,bug",
		               "createdBy": "a@b.com",
		               "attributeSortOrder": "1",
		               "requirementTypeId": 360,
		               "lastModifiedBy": "a@b.com",
		               "attributeRequired": 1,
		               "attributeImpactsVersion": 0},
		            {
		               "attributeDefaultValue": "3",
		               "attributeDescription": "sev",
		               "attributeImpactsApprovalWorkflow": 0,
		               "attributeImpactsTraceability": 0,
		               "attributeId": 47,
		               "attributeType": "Drop Down",
		               "attributeName": "severity",
		               "attributeDropDownOptions": "1,2,3,4,5",
		               "createdBy": "a@b.com",
		               "attributeSortOrder": "2",
		               "requirementTypeId": 360,
		               "lastModifiedBy": "a@b.com",
		               "attributeRequired": 1,
		               "attributeImpactsVersion": 0},
		            {
		               "attributeDefaultValue": "fsd",
		               "attributeDescription": "fdafadattributeImpactsVersion=0",
		               "attributeImpactsApprovalWorkflow": 0,
		               "attributeImpactsTraceability": 0,
		               "attributeId": 48,
		               "attributeType": "Text Box",
		               "attributeName": "test",
		               "attributeDropDownOptions": "",
		               "createdBy": "a@b.com",
		               "attributeSortOrder": "2",
		               "requirementTypeId": 360,
		               "lastModifiedBy": "a@b.com",
		               "attributeRequired": 0,
		               "attributeImpactsVersion": 0},
		            {
		               "attributeDefaultValue": "",
		               "attributeDescription": "fdsattributeImpactsVersion=1",
		               "attributeImpactsApprovalWorkflow": 0,
		               "attributeImpactsTraceability": 0,
		               "attributeId": 49,
		               "attributeType": "Text Box",
		               "attributeName": "test2",
		               "attributeDropDownOptions": "",
		               "createdBy": "a@b.com",
		               "attributeSortOrder": "2",
		               "requirementTypeId": 360,
		               "lastModifiedBy": "a@b.com",
		               "attributeRequired": 0,
		               "attributeImpactsVersion": 0},
		            {
		               "attributeDefaultValue": "yes",
		               "attributeDescription": "impact",
		               "attributeImpactsApprovalWorkflow": 0,
		               "attributeImpactsTraceability": 0,
		               "attributeId": 46,
		               "attributeType": "Drop Down",
		               "attributeName": "impact to customer",
		               "attributeDropDownOptions": "yes,no",
		               "createdBy": "system",
		               "attributeSortOrder": "3",
		               "requirementTypeId": 360,
		               "lastModifiedBy": "system",
		               "attributeRequired": 0,
		               "attributeImpactsVersion": 0},
		            {
		               "attributeDefaultValue": "",
		               "attributeDescription": "test",
		               "attributeImpactsApprovalWorkflow": 1,
		               "attributeImpactsTraceability": 0,
		               "attributeId": 50,
		               "attributeType": "Text Box",
		               "attributeName": "test3",
		               "attributeDropDownOptions": "",
		               "createdBy": "a@b.com",
		               "attributeSortOrder": "f",
		               "requirementTypeId": 360,
		               "lastModifiedBy": "a@b.com",
		               "attributeRequired": 0,
		               "attributeImpactsVersion": 1}],
		         "projectId": 39},
		         "projectId": 39},
		      {
		         "createdBy": "a@b.com",
		         "description": "Default Test Result type created by the system. 
		         These are usually created by Test Engineers prior to Go Live",
		         "name": "Test Results",
		         "prefix": "TR",
		         "requirementTypeId": 215,
		         "lastModifiedBy": "a@b.com",
		         "enableApproval": 0,
		         "rootFolderId": 272,
		         "attributes": [
		            {
		               "attributeDefaultValue": "",
		               "attributeDescription": "testbustcache=1248999937062",
		               "attributeImpactsApprovalWorkflow": 0,
		               "attributeImpactsTraceability": 0,
		               "attributeId": 37,
		               "attributeType": "Text Box",
		               "attributeName": "test",
		               "attributeDropDownOptions": "",
		               "createdBy": "a@b.com",
		               "attributeSortOrder": "a",
		               "requirementTypeId": 215,
		               "lastModifiedBy": "a@b.com",
		               "attributeRequired": 0,
		               "attributeImpactsVersion": 0}],
		         "projectId": 39}]}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
		
		
		
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Get All Folders in a Project
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='getFoldersA'></a>
									<a href='#getFoldersA' onClick='
										document.getElementById("getProjectFoldersDiv").style.display = "block";'> Get All Folders in a Project</a> 
									<div class='alert alert-success' id='getProjectFoldersDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#getFoldersA' onClick='document.getElementById("getProjectFoldersDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Title</b><br>
													Get folders
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Returns folders
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>URL</b><br>
													/RESTAPI
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Method</b><br>
													GET
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													</span>
													<table  border='2'>
														<tr>
															<td>Query String Parameter</td>
															<td>Required/Optional</td>
															<td>Description</td>
															<td>Type</td>
															<td>Notes</td>
														</tr>
														<tr>
															<td>Key</td>
															<td>Required</td>
															<td>the API key</td>
															<td>String</td>
															<td></td>
														</tr>
														<tr>
															<td>Action</td>
															<td>Required</td>
															<td>action name to return the folders of a specified project </td>
															<td>String</td>
															<td>Value : getProjectFolders</td>
														</tr>
														<tr>
															<td>projectId</td>
															<td>Required</td>
															<td>The id of the project</td>
															<td>Number</td>
															<td></td>
														</tr>
													</table>
													
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQ&action=getProjectFolders&projectId=19
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre>{
		   "errorMessage": "",
		   "responseStatus": "success",
		   "folders": [
		      {
		         "errorMessage": "",
		         "responseStatus": "success",
		         "folderDetails": {
		            "createdBy": "system",
		            "requirementTypeName": "Release",
		            "folderOrder": 1,
		            "description": "Default folder used to storeRelease",
		            "requirementTypeId": 113,
		            "name": "Release",
		            "lastModifiedBy": "system",
		            "folderPath": "Release",
		            "parentFolderId": 0,
		            "projectId": 19,
		            "folderLevel": 1,
		            "folderId": 114}},
		      {
		         "errorMessage": "",
		         "responseStatus": "success",
		         "folderDetails": {
		            "createdBy": "system",
		            "requirementTypeName": "Business Requirements",
		            "folderOrder": 2,
		            "description": "Default folder used to storeBusiness Requirements",
		            "requirementTypeId": 114,
		            "name": "Business Requirements",
		            "lastModifiedBy": "system",
		            "folderPath": "Business Requirements",
		            "parentFolderId": 0,
		            "projectId": 19,
		            "folderLevel": 1,
		            "folderId": 115}},
		      
		      {
		         "errorMessage": "",
		         "responseStatus": "success",
		         "folderDetails": {
		            "createdBy": "sami@tracecloud.com",
		            "requirementTypeName": "Test Results",
		            "folderOrder": 0,
		            "description": "Y",
		            "requirementTypeId": 118,
		            "name": "TEST",
		            "lastModifiedBy": "sami@tracecloud.com",
		            "folderPath": "Test Results\/TEST",
		            "parentFolderId": 119,
		            "projectId": 19,
		            "folderLevel": 2,
		            "folderId": 251}}]}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
							
		
		
		
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Get All Users in a Project					
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='getUsersA'></a>
									<a href='#getUsersA' onClick='
										document.getElementById("getProjectUsersDiv").style.display = "block";'> Get All Users in a Project</a> 
									<div class='alert alert-success' id='getProjectUsersDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#getUsersA' onClick='document.getElementById("getProjectUsersDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Title</b><br>
													Get users
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Returns users in a project
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>URL</b><br>
													/RESTAPI
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Method</b><br>
													GET
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													</span>
													<table  border='2'>
														<tr>
															<td>Query String Parameter</td>
															<td>Required/Optional</td>
															<td>Description</td>
															<td>Type</td>
															<td>Notes</td>
														</tr>
														<tr>
															<td>Key</td>
															<td>Required</td>
															<td>the API key</td>
															<td>String</td>
															<td></td>
														</tr>
														<tr>
															<td>Action</td>
															<td>Required</td>
															<td>action name to return the users of a specified project </td>
															<td>String</td>
															<td>Value : getProjectUsers</td>
														</tr>
														<tr>
															<td>projectId</td>
															<td>Required</td>
															<td>The id of the project</td>
															<td>Number</td>
															<td></td>
														</tr>
													</table>
													
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQ&action=getProjectUsers&projectId=19
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre>{
		   "errorMessage": "",
		   "users": [
		      {
		         "lastName": "Developer",
		         "emailId": "qa_dev_1@tracecloud.com",
		         "userId": 4,
		         "firstName": "Josh",
		         "userType": "readWrite"},
		      {
		         "lastName": "Marketing",
		         "emailId": "qa_mktg_1@tracecloud.com",
		         "userId": 2,
		         "firstName": "Richard ",
		         "userType": "readWrite"},
		      {
		         "lastName": "Roy",
		         "emailId": "sami@tracecloud.com",
		         "userId": 1,
		         "firstName": "Shambhavi",
		         "userType": "readWrite"},
		      {
		         "lastName": "sdfd",
		         "emailId": "shambhaviroy@gmail.com",
		         "userId": 7,
		         "firstName": "sdfd",
		         "userType": "expired"},
		      {
		         "lastName": "Reddy",
		         "emailId": "sreenatht1@yahoo.com",
		         "userId": 11,
		         "firstName": "San",
		         "userType": "expired"}],
		   "responseStatus": "success"}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>
		
		
		
		
		
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Get All Reports in a Project					
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='getReportsA'></a>
									<a href='#getReportsA' onClick='
										document.getElementById("getProjectReportsDiv").style.display = "block";'> Get All Reports in a Project</a> 
									<div class='alert alert-success' id='getProjectReportsDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#getReportsA' onClick='document.getElementById("getProjectReportsDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Title</b><br>
													Get project reports
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Returns reports
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>URL</b><br>
													/RESTAPI
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Method</b><br>
													GET
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													</span>
													<table  border='2'>
														<tr>
															<td>Query String Parameter</td>
															<td>Required/Optional</td>
															<td>Description</td>
															<td>Type</td>
															<td>Notes</td>
														</tr>
														<tr>
															<td>Key</td>
															<td>Required</td>
															<td>the API key</td>
															<td>String</td>
															<td></td>
														</tr>
														<tr>
															<td>Action</td>
															<td>Required</td>
															<td>action name to return the reports of a specified project </td>
															<td>String</td>
															<td>Value : getProjectReports</td>
														</tr>
														<tr>
															<td>projectId</td>
															<td>Required</td>
															<td>The id of the project</td>
															<td>Number</td>
															<td></td>
														</tr>
													</table>
													
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQ&action=getProjectReports&projectId=19
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre>{
		   "errorMessage": "",
		   "reports": [
		      {
		         "reportType": "list",
		         "createdBy": "qa_mktg_1@tracecloud.com",
		         "reportDefinition": "projectId:--:19:###:folderId:--:121:###:active:--:
		         active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:completedSearch
		         :--:all:###:incompleteSearch:--:all:###:suspectUpStreamSearch:--:all:
		         ###:suspectDownStreamSearch:--:all:###:includeSubFoldersSearch:--:no:
		         ###:nameSearch:--::###:descriptionSearch:--::###:ownerSearch:--::###:
		         externalURLSearch:--::###:traceToSearch:--::###:traceFromSearch:--::
		         ###:statusSearch:--::###:prioritySearch:--::###:pctCompleteSearch:--:",
		         "reportDescription": "Canned report created by the system to display  ALL 
		         the Requirements in this folder.",
		         "traceTreeDepth": 1,
		         "projectId": 19,
		         "reportName": "All Requirements",
		         "reportId": 802,
		         "reportVisibility": "public",
		         "folderId": 121},
		      {
		         "reportType": "list",
		         "createdBy": "qa_mktg_1@tracecloud.com",
		         "reportDefinition": "projectId:--:19:###:folderId:--:122:###:active:--:
		         active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:completed
		         Search:--:all:###:incompleteSearch:--:all:###:suspectUpStreamSearch:--
		         :all:###:suspectDownStreamSearch:--:all:###:includeSubFoldersSearch:--
		         :no:###:nameSearch:--::###:descriptionSearch:--::###:ownerSearch:--:
		         :###:externalURLSearch:--::###:traceToSearch:--::###:traceFromSearch
		         :--::###:statusSearch:--::###:prioritySearch:--::###:pctCompleteSearch:--:",
		         "reportDescription": "Canned report created by the system to display  
		         ALL the Requirements in this folder.",
		         "traceTreeDepth": 1,
		         "projectId": 19,
		         "reportName": "All Requirements",
		         "reportId": 810,
		         "reportVisibility": "public",
		         "folderId": 122},
		      {
		         "reportType": "traceTree",
		         "createdBy": "qa_mktg_1@tracecloud.com",
		         "reportDefinition": "projectId:--:19:###:folderId:--:136:###:active:--:
		         active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:completedSearch
		         :--:all:###:incompleteSearch:--:all:###:suspectUpStreamSearch:--:all:###:
		         suspectDownStreamSearch:--:all:###:includeSubFoldersSearch:--:no:###:nameSearch
		         :--::###:descriptionSearch:--::###:ownerSearch:--::###:externalURLSearch:--::
		         ###:traceToSearch:--::###:traceFromSearch:--::###:statusSearch:--::###:
		         prioritySearch:--::###:pctCompleteSearch:--:",
		         "reportDescription": "Canned report created by the system to 
		         display  ALL the Requirements in this folder.",
		         "traceTreeDepth": 3,
		         "projectId": 19,
		         "reportName": "All Requirements",
		         "reportId": 929,
		         "reportVisibility": "public",
		         "folderId": 136}],
		   "responseStatus": "success"}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>
		
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Get Details of a Requirement Type					
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='getRequirementTypeDetailsA'></a>
									<a href='#getRequirementTypeDetailsA' onClick='
										document.getElementById("getRequirementTypeDetailsDiv").style.display = "block";'> Get Details of a Requirement Type</a> 
									<div class='alert alert-success' id='getRequirementTypeDetailsDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#getRequirementTypeDetailsA' onClick='document.getElementById("getRequirementTypeDetailsDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Title</b><br>
													Get requirement type details
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Returns the details of a requirement type
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>URL</b><br>
													/RESTAPI
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Method</b><br>
													GET
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													</span>
													<table  border='2'>
														<tr>
															<td>Query String Parameter</td>
															<td>Required/Optional</td>
															<td>Description</td>
															<td>Type</td>
															<td>Notes</td>
														</tr>
														<tr>
															<td>Key</td>
															<td>Required</td>
															<td>the API key</td>
															<td>String</td>
															<td></td>
														</tr>
														<tr>
															<td>Action</td>
															<td>Required</td>
															<td>action name to return the details of a requirement type</td>
															<td>String</td>
															<td>Value : getRequirementTypeDetails</td>
														</tr>
														<tr>
															<td>RequirementTypeId</td>
															<td>Required</td>
															<td>The id of the requirement type</td>
															<td>Number</td>
															<td></td>
														</tr>
													</table>
													
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQ&action=getRequirementTypeDetails&requirementTypeId=114
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre>{
		   "errorMessage": "",
		   "responseStatus": "success",
		   "requirementTypeDetails": {
		      "createdBy": "sami@tracecloud.com",
		      "description": "Default Business Requirement type created by the system.
		       These are high level requirements usually created by business analysts
		       prior  to Business Commit",
		      "name": "Business Requirements",
		      "prefix": "BR",
		      "requirementTypeId": 114,
		      "lastModifiedBy": "sami@tracecloud.com",
		      "enableApproval": 1,
		      "rootFolderId": 115,
		      "attributes": [
		         {
		            "attributeDefaultValue": "Bug",
		            "attributeDescription": "To describe a bug or enhancement",
		            "attributeImpactsApprovalWorkflow": 0,
		            "attributeImpactsTraceability": 0,
		            "attributeId": 6,
		            "attributeType": "Drop Down",
		            "attributeName": "Type",
		            "attributeDropDownOptions": "Bug, Enhancement",
		            "createdBy": "system",
		            "attributeSortOrder": "1",
		            "requirementTypeId": 114,
		            "lastModifiedBy": "system",
		            "attributeRequired": 1,
		            "attributeImpactsVersion": 0},
		         {
		            "attributeDefaultValue": "3",
		            "attributeDescription": "Severity describes the severity of the bug
		            or enhancement, 1 is the highest severity and 6 is the lowest. ",
		            "attributeImpactsApprovalWorkflow": 0,
		            "attributeImpactsTraceability": 0,
		            "attributeId": 7,
		            "attributeType": "Drop Down",
		            "attributeName": "Severity",
		            "attributeDropDownOptions": "1,2,3,4,5,6",
		            "createdBy": "sami@tracecloud.com",
		            "attributeSortOrder": "2",
		            "requirementTypeId": 114,
		            "lastModifiedBy": "sami@tracecloud.com",
		            "attributeRequired": 1,
		            "attributeImpactsVersion": 0},
		         {
		            "attributeDefaultValue": "yes",
		            "attributeDescription": "iimpact to customer",
		            "attributeImpactsApprovalWorkflow": 0,
		            "attributeImpactsTraceability": 0,
		            "attributeId": 8,
		            "attributeType": "Drop Down",
		            "attributeName": "Impact to customer",
		            "attributeDropDownOptions": "yes,no",
		            "createdBy": "sami@tracecloud.com",
		            "attributeSortOrder": "3",
		            "requirementTypeId": 114,
		            "lastModifiedBy": "sami@tracecloud.com",
		            "attributeRequired": 0,
		            "attributeImpactsVersion": 0}],
		      "projectId": 19}}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>
		
		
		
		
		
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Get All Requirements in a Requirement Type					
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='getAllRequirementsInRequirementType'></a>
									<a href='#getAllRequirementsInRequirementType' onClick='
										document.getElementById("getRequirementsInRequirementTypeDiv").style.display = "block";'> Get All Requirements in a Requirement Type</a> 
									<div class='alert alert-success' id='getRequirementsInRequirementTypeDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#getAllRequirementsInRequirementType' onClick='document.getElementById("getRequirementsInRequirementTypeDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Title</b><br>
													Get requirements of a requirement type
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Returns requirements of a requirement type
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>URL</b><br>
													/RESTAPI
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Method</b><br>
													GET
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													</span>
													<table  border='2'>
														<tr>
															<td>Query String Parameter</td>
															<td>Required/Optional</td>
															<td>Description</td>
															<td>Type</td>
															<td>Notes</td>
														</tr>
														<tr>
															<td>Key</td>
															<td>Required</td>
															<td>the API key</td>
															<td>String</td>
															<td></td>
														</tr>
														<tr>
															<td>Action</td>
															<td>Required</td>
															<td>action name to return the requirements of a requirement type </td>
															<td>String</td>
															<td>Value : getRequirementsInRequirementType</td>
														</tr>
														<tr>
															<td>requirementTypeId</td>
															<td>Required</td>
															<td>The id of the requirement type</td>
															<td>Number</td>
															<td></td>
														</tr>
													</table>
													
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQ&action=getRequirementsInRequirementType&requirementTypeId=114
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre>{
		   "errorMessage": "",
		   "responseStatus": "success",
		   "requirementTypeDetails": {
		      "createdBy": "sami@tracecloud.com",
		      "description": "Default Business Requirement type created by the system.
		       These are high level requirements usually created by business analysts
		       prior  to Business Commit",
		      "name": "Business Requirements",
		      "prefix": "BR",
		      "requirementTypeId": 114,
		      "lastModifiedBy": "sami@tracecloud.com",
		      "enableApproval": 1,
		      "projectId": 19,
		      "requirements": [
		         {
		            "approvers": "",
		            "externalURL": "",
		            "status": "Approved",
		            "tag": 2,
		            "userDefinedAttributes": "Type:#:Bug:##:Severity:#:3:##:Impact to
		             customer:#:yes",
		            "folderPath": "Business Requirements\/Word Integration",
		            "traceFrom": "FR-324",
		            "projectId": 19,
		            "deleted": 0,
		            "pctComplete": 100,
		            "requirementId": 1517,
		            "traceTo": "REL-10",
		            "approvedByAllDt": "16 August 2009 03:43:58 PM ",
		            "folderId": 136,
		            "version": 1,
		            "fullTag": "BR-2",
		            "createdBy": "qa_mktg_1@tracecloud.com",
		            "requirementTypeName": "Business Requirements",
		            "description": "A user should be able to click on the link called
		             \"Generate Report\" and generate a report with the latest data for
		              all the embedded reports or requirements. The user should have the
		             ability to select a format for download (HTML, WORD 97-2003, WORD 2007). ",
		            "priority": "Medium",
		            "requirementTypeId": 114,
		            "name": "A user should be able to click on the link called \"Generate
		             Report\" and generate a report with the",
		            "lastModifiedBy": "sami@tracecloud.com",
		            "owner": "qa_mktg_1@tracecloud.com"},
		         {
		            "approvers": "",
		            "externalURL": "",
		            "status": "Draft",
		            "tag": 508,
		            "userDefinedAttributes": "Type:#:Bug:##:Severity:#:3:##:Impact
		             to customer:#:yes",
		            "folderPath": "Business Requirements\/Ideas",
		            "traceFrom": "",
		            "projectId": 19,
		            "deleted": 1,
		            "pctComplete": 0,
		            "requirementId": 66005,
		            "traceTo": "",
		            "approvedByAllDt": "N\/A",
		            "folderId": 130,
		            "version": 1,
		            "fullTag": "BR-508",
		            "createdBy": "sami@tracecloud.com",
		            "requirementTypeName": "Business Requirements",
		            "description": "test<br>   --Trace From :   --Trace To :",
		            "priority": "Medium",
		            "requirementTypeId": 114,
		            "name": "test",
		            "lastModifiedBy": "sami@tracecloud.com",
		            "owner": "sami@tracecloud.com"}]}}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>
		
		
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Get Details of a Folder					
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='getFolderDetailsA'></a>
									<a href='#getFolderDetailsA' onClick='
										document.getElementById("getFolderDetailsDiv").style.display = "block";'> Get Details of a Folder</a> 
									<div class='alert alert-success' id='getFolderDetailsDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#getFolderDetailsA' onClick='document.getElementById("getFolderDetailsDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Title</b><br>
													Get Folder Details
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Returns folder details
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>URL</b><br>
													/RESTAPI
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Method</b><br>
													GET
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													</span>
													<table  border='2'>
														<tr>
															<td>Query String Parameter</td>
															<td>Required/Optional</td>
															<td>Description</td>
															<td>Type</td>
															<td>Notes</td>
														</tr>
														<tr>
															<td>Key</td>
															<td>Required</td>
															<td>the API key</td>
															<td>String</td>
															<td></td>
														</tr>
														<tr>
															<td>Action</td>
															<td>Required</td>
															<td>action name to return the details of a folder</td>
															<td>String</td>
															<td>Value : getFolderDetails</td>
														</tr>
														<tr>
															<td>folderId</td>
															<td>Required</td>
															<td>The id of the folder</td>
															<td>Number</td>
															<td></td>
														</tr>
													</table>
													
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQ&action=getFolderDetails&folderId=130
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre>{
		   "errorMessage": "",
		   "responseStatus": "success",
		   "folderDetails": {
		      "errorMessage": "",
		      "responseStatus": "success",
		      "folderDetails": {
		         "createdBy": "qa_mktg_1@tracecloud.com",
		         "requirementTypeName": "Business Requirements",
		         "folderOrder": 0,
		         "description": "This folder is used to track the new Business Requirement
		          Ideas and we can use the collaboration mechanism to review these ideas,
		          before they can be assigned to a release. We use this to track both
		          bugs and new enhancement requests.",
		         "requirementTypeId": 114,
		         "name": "Ideas",
		         "lastModifiedBy": "qa_mktg_1@tracecloud.com",
		         "folderPath": "Business Requirements\/Ideas",
		         "parentFolderId": 115,
		         "projectId": 19,
		         "folderLevel": 2,
		         "folderId": 130}}}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>
		
		
							
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Get All Requirements in a Folder					
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='getAllRequirementsInFolderA'></a>
									<a href='#getAllRequirementsInFolderA' onClick='
										document.getElementById("getAllRequirementsInFolderDiv").style.display = "block";'> Get All Requirements in a Folder</a> 
									<div class='alert alert-success' id='getAllRequirementsInFolderDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#getAllRequirementsInFolderA' onClick='document.getElementById("getAllRequirementsInFolderDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Gets all the Requirements in a Folder
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													The API Key and Folder Id
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be a member of the project in which this Folder exists 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQ&action=getRequirementsInFolder&folderId=130
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre>{
		   "errorMessage": "",
		   "responseStatus": "success",
		   "folderDetails": {
		      "parentFolderId": 115,
		      "folderPath": "Business Requirements\/Ideas",
		      "projectId": 19,
		      "folderLevel": 2,
		      "requirements": [
		         {
		            "approvers": "",
		            "externalURL": "",
		            "status": "Approved",
		            "tag": 325,
		            "userDefinedAttributes": "Type:#:Bug:##:Severity:#:3:##:Impact 
		            to customer:#:yes",
		            "folderPath": "Business Requirements\/Ideas",
		            "traceFrom": "",
		            "projectId": 19,
		            "deleted": 1,
		            "pctComplete": 0,
		            "requirementId": 2178,
		            "traceTo": "REL-12",
		            "approvedByAllDt": "23 August 2009 09:34:16 AM ",
		            "folderId": 130,
		            "version": 1,
		            "fullTag": "BR-325",
		            "createdBy": "qa_mktg_1@tracecloud.com",
		            "requirementTypeName": "Business Requirements",
		            "description": "I showed this to you before.   --Trace From :   --Trace To :REL-12",
		            "priority": "Medium",
		            "name": "When the search result for text id comes, the side scroll bar
		             disappears even when the result is lon",
		            "lastModifiedBy": "qa_mktg_1@tracecloud.com",
		            "owner": "qa_mktg_1@tracecloud.com"},
		         {
		            "approvers": "",
		            "externalURL": "",
		            "status": "Draft",
		            "tag": 565,
		            "userDefinedAttributes": "Type:#: Enhancement:##:Severity:#:1:##:Impact
		             to customer:#:yes",
		            "folderPath": "Business Requirements\/Ideas",
		            "traceFrom": "",
		            "projectId": 19,
		            "deleted": 0,
		            "pctComplete": 0,
		            "requirementId": 93466,
		            "traceTo": "REL-13",
		            "approvedByAllDt": "N\/A",
		            "folderId": 130,
		            "version": 1,
		            "fullTag": "BR-565",
		            "createdBy": "sami@tracecloud.com",
		            "requirementTypeName": "Business Requirements",
		            "description": "chnage comparision report that tracks which traces
		             have become suspect<br>",
		            "priority": "Medium",
		            "name": "chnage comparision report that tracks which traces have
		             become suspect",
		            "lastModifiedBy": "sami@tracecloud.com",
		            "owner": "sami@tracecloud.com"}],
		      "folderId": 130,
		      "createdBy": "qa_mktg_1@tracecloud.com",
		      "description": "This folder is used to track the new Business Requirement Ideas
		       and we can use the collaboration mechanism to review these ideas, before they
		        can be assigned to a release. We use this to track both bugs and new 
		        enhancement requests.",
		      "folderOrder": 0,
		      "requirementTypeName": "Business Requirements",
		      "name": "Ideas",
		      "requirementTypeId": 114,
		      "lastModifiedBy": "qa_mktg_1@tracecloud.com"}}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>
							
		
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Get All Requirements Owned by a user					
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='getUserRequirementsA'></a>
									<a href='#getUserRequirementsA' onClick='
										document.getElementById("getUserRequirementsDiv").style.display = "block";'> Get All Requirements owned by a user</a> 
									<div class='alert alert-success' id='getUserRequirementsDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#getUserRequirementsA' onClick='document.getElementById("getUserRequirementsDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Gets all the Requirements owned by a user
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													The API Key and User Id
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be a member of the project in which this Folder exists 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQ&action=getUsersRequirements&userId=10&projectId=39
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre>{
		   "errorMessage": "",
		   "responseStatus": "success",
		   "folderDetails": {
		      "lastName": "Roy",
		      "emailId": "sami@tracecloud.com",
		      "userId": 1,
		      "firstName": "Shambhavi",
		      "requirements": [
		         {
		            "approvers": "",
		            "externalURL": "",
		            "status": "Approved",
		            "tag": 1,
		            "userDefinedAttributes": "",
		            "folderPath": "Test Cases",
		            "traceFrom": "TR-1",
		            "projectId": 19,
		            "deleted": 0,
		            "pctComplete": 100,
		            "requirementId": 1505,
		            "traceTo": "",
		            "approvedByAllDt": "15 August 2009 05:26:15 PM ",
		            "folderId": 118,
		            "version": 1,
		            "fullTag": "TC-1",
		            "createdBy": "sami@tracecloud.com",
		            "requirementTypeName": "Test Cases",
		            "description": "test",
		            "priority": "Medium",
		            "name": "test",
		            "lastModifiedBy": "sami@tracecloud.com",
		            "owner": "sami@tracecloud.com"},
		         {
		            "approvers": "",
		            "externalURL": "",
		            "status": "Draft",
		            "tag": 565,
		            "userDefinedAttributes": "Type:#: Enhancement:##:Severity:#:1:
		            ##:Impact to customer:#:yes",
		            "folderPath": "Business Requirements\/Ideas",
		            "traceFrom": "",
		            "projectId": 19,
		            "deleted": 0,
		            "pctComplete": 0,
		            "requirementId": 93466,
		            "traceTo": "REL-13",
		            "approvedByAllDt": "N\/A",
		            "folderId": 130,
		            "version": 1,
		            "fullTag": "BR-565",
		            "createdBy": "sami@tracecloud.com",
		            "requirementTypeName": "Business Requirements",
		            "description": "chnage comparision report that tracks which
		             traces have become suspect<br>",
		            "priority": "Medium",
		            "name": "chnage comparision report that tracks which traces
		             have become suspect",
		            "lastModifiedBy": "sami@tracecloud.com",
		            "owner": "sami@tracecloud.com"}],
		      "userType": "readWrite"}}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
					
					
		
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Get All Requirements In a Report					
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='getReportRequirementsA'></a>
									<a href='#getReportRequirementsA' onClick='
										document.getElementById("getReportRequirementsDiv").style.display = "block";'> Get All Requirements in a Report</a> 
									<div class='alert alert-success' id='getReportRequirementsDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#getReportRequirementsA' onClick='document.getElementById("getReportRequirementsDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Gets all the Requirements from executing a stored report 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													The API Key and Report Id
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be a member of the project in which this Report exists. Also, he needs to be the owner 
													of this report, or this is a public report created by someone else. 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQ&action=getReportRequirements&reportId=905
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre>{
		   "errorMessage": "",
		   "responseStatus": "success",
		   "reportDetails": {
		      "reportType": "traceTree",
		      "createdBy": "qa_mktg_1@tracecloud.com",
		      "reportDefinition": "projectId:--:19:###:folderId:--:133:###:active:--:active
		      :###:danglingSearch:--:all:###:orphanSearch:--:all:###:completedSearch:--:all
		      :###:incompleteSearch:--:all:###:suspectUpStreamSearch:--:all:###:suspectDown
		      StreamSearch:--:all:###:includeSubFoldersSearch:--:no:###:nameSearch:--::###:
		      descriptionSearch:--::###:ownerSearch:--::###:externalURLSearch:--::###:
		      traceToSearch:--::###:traceFromSearch:--::###:statusSearch:--::###:prioritySearch
		      :--::###:pctCompleteSearch:--:",
		      "reportDescription": "Canned report created by the system to display  ALL 
		      the Requirements in this folder.",
		      "traceTreeDepth": 3,
		      "projectId": 19,
		      "requirements": [
		         {
		            "approvers": "",
		            "externalURL": "",
		            "status": "Approved",
		            "tag": 240,
		            "userDefinedAttributes": "Type:#:Bug:##:Severity:#:3:##:Impact to 
		            customer:#:yes",
		            "folderPath": "Business Requirements\/Folder Metrics",
		            "traceFrom": "FR-288",
		            "projectId": 19,
		            "deleted": 0,
		            "pctComplete": 100,
		            "requirementId": 1755,
		            "traceTo": "REL-8",
		            "approvedByAllDt": "16 August 2009 03:44:06 PM ",
		            "folderId": 133,
		            "version": 1,
		            "fullTag": "BR-240",
		            "createdBy": "qa_mktg_1@tracecloud.com",
		            "requirementTypeName": "Business Requirements",
		            "description": "A should be able to see Folder metrics by clicking on 
		            Folder Metrics button on the right and then selecting one of various 
		            types of Folder metrics report",
		            "priority": "Medium",
		            "name": "A should be able to see Folder metrics by clicking on Folder 
		            Metrics button on the right and then ",
		            "lastModifiedBy": "sami@tracecloud.com",
		            "owner": "qa_mktg_1@tracecloud.com"}],
		      "reportName": "All Requirements",
		      "reportId": 905,
		      "reportVisibility": "public",
		      "folderId": 133}}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
					
								
					
					
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Get Requirements By Id					
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='getRequirementByIdA'></a>
									<a href='#getRequirementByIdA' onClick='
										document.getElementById("getRequirementsByidDiv").style.display = "block";'> Get Requirement Details By Requirement Id</a> 
									<div class='alert alert-success' id='getRequirementsByidDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#getRequirementByIdA' onClick='document.getElementById("getRequirementsByidDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Gets the details of a Requirement from a Requirement Id 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													The API Key and Requirement Id
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be a member of the project in which this Requirement exists. And he/she needs to have read permissions
													on the requirement 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQ&action=getRequirementById&requirementId=2026
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre>{
		   "errorMessage": "",
		   "requirementDetails": {
		      "approvers": "",
		      "externalURL": "",
		      "status": "Approved",
		      "tag": 237,
		      "userDefinedAttributes": "",
		      "folderPath": "Functional Requirements\/Trace Tree Report",
		      "traceFrom": "",
		      "projectId": 19,
		      "deleted": 0,
		      "pctComplete": 100,
		      "requirementId": 2026,
		      "traceTo": "BR-195",
		      "approvedByAllDt": "16 August 2009 10:26:34 PM ",
		      "folderId": 129,
		      "version": 1,
		      "fullTag": "FR-237",
		      "createdBy": "qa_dev_1@tracecloud.com",
		      "requirementTypeName": "Functional Requirements",
		      "description": "A User should be able to create and run a report based on criteria 
		      selected in Trace Tree Report tab.",
		      "priority": "Medium",
		      "name": "A User should be able to create and run a report based on criteria selected
		      in Trace Tree Report t",
		      "lastModifiedBy": "sami@tracecloud.com",
		      "owner": "qa_dev_1@tracecloud.com"},
		   "responseStatus": "success"}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
					
								
					
					
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Get Requirements By Tag				
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='getRequirementsByFullTagA'></a>
									<a href='#getRequirementsByFullTagA' onClick='
										document.getElementById("getRequirementsByFullTagDiv").style.display = "block";'> Get Requirement Details By Requirement Tag (eg: BR-16)</a> 
									<div class='alert alert-success' id='getRequirementsByFullTagDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#getRequirementsByFullTagA' onClick='document.getElementById("getRequirementsByFullTagDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Gets the details of a Requirement from a Requirement Tag (eg : BR-16)
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													The API Key , Project Id, and Requirement Tag (BR-16)
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be a member of the project in which this Requirement exists. And he/she needs to have read permissions
													on the requirement 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQ&action=getRequirementByFullTag&projectId=39&fullTag=REL-1395
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre>{
		   "errorMessage": "",
		   "requirementDetails": {
		      "approvers": "",
		      "externalURL": "",
		      "status": "Approved",
		      "tag": 237,
		      "userDefinedAttributes": "",
		      "folderPath": "Functional Requirements\/Trace Tree Report",
		      "traceFrom": "",
		      "projectId": 19,
		      "deleted": 0,
		      "pctComplete": 100,
		      "requirementId": 2026,
		      "traceTo": "BR-195",
		      "approvedByAllDt": "16 August 2009 10:26:34 PM ",
		      "folderId": 129,
		      "version": 1,
		      "fullTag": "FR-237",
		      "createdBy": "qa_dev_1@tracecloud.com",
		      "requirementTypeName": "Functional Requirements",
		      "description": "A User should be able to create and run a report based on
			 criteria selected in Trace Tree Report tab.",
		      "priority": "Medium",
		      "name": "A User should be able to create and run a report based on criteria
		       selected in Trace Tree Report t",
		      "lastModifiedBy": "sami@tracecloud.com",
		      "owner": "qa_dev_1@tracecloud.com"},
		   "responseStatus": "success"}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
					
										
					
					
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Get Requirements TraceTo				
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='getRequirementTraceToA'></a>
									<a href='#getRequirementTraceToA' onClick='
										document.getElementById("getRequirementTraceToDiv").style.display = "block";'>
										 Get the list of Requirements that a Requirement Traces to</a> 
									<div class='alert alert-success' id='getRequirementTraceToDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#getRequirementTraceToA' onClick='document.getElementById("getRequirementTraceToDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Gets a list of Requirement this Requirement Traces to 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													The API Key , Requirement Id
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be a member of the project in which this Requirement exists. And he/she needs to have read permissions
													on the requirement 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQ&action=getRequirementTraceTo&requirementId=1584
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre>{
		   "errorMessage": "",
		   "requirementDetails": {
		      "approvers": "",
		      "externalURL": "",
		      "status": "Approved",
		      "tag": 69,
		      "userDefinedAttributes": "Type:#: Enhancement:##:Severity:#:3:
		      ##:Impact to customer:#:yes",
		      "folderPath": "Business Requirements\/Administration Requirements",
		      "traceFrom": "FR-19,FR-27",
		      "projectId": 19,
		      "deleted": 0,
		      "pctComplete": 100,
		      "requirementId": 1584,
		      "traceTo": "(s)REL-2",
		      "approvedByAllDt": "16 August 2009 03:44:01 PM ",
		      "folderId": 121,
		      "version": 1,
		      "fullTag": "BR-69",
		      "createdBy": "qa_mktg_1@tracecloud.com",
		      "requirementTypeName": "Business Requirements",
		      "description": "Administrator should be able to update comma separated
		       list of domains in Project Core Info Page. Only members whose emails 
		       end in restricted domains should be able to become members of this
		        project.",
		      "priority": "Medium",
		      "name": "Administrator should be able to update comma separated list
		       of domains in Project Core Info Page. ",
		      "lastModifiedBy": "sami@tracecloud.com",
		      "owner": "qa_mktg_1@tracecloud.com"},
		   "responseStatus": "success",
		   "requirementTraceTo": [
		      {
		         "requirementDetails": {
		            "approvers": "",
		            "externalURL": "msnbc.com",
		            "status": "Draft",
		            "tag": 2,
		            "userDefinedAttributes": "",
		            "folderPath": "Release",
		            "traceFrom": "BR-12,BR-13,(s)BR-14,(s)BR-15,(s)BR-16,(s)BR-17,
		            (s)BR-18,(s)BR-19,(s)BR-20,(s)BR-21,(s)BR-22,(s)BR-23,(s)BR-24,
		            (s)BR-80,(s)BR-81,(s)BR-82,(s)BR-83,(s)BR-84,(s)BR-85,(s)BR-86,
		            ",
		            "projectId": 19,
		            "deleted": 0,
		            "pctComplete": 0,
		            "requirementId": 1507,
		            "traceTo": "",
		            "approvedByAllDt": "N\/A",
		            "folderId": 114,
		            "version": 2,
		            "fullTag": "REL-2",
		            "createdBy": "qa_mktg_1@tracecloud.com",
		            "requirementTypeName": "Release",
		            "description": "Admin Requirements",
		            "priority": "High",
		            "name": "Admin Requirements",
		            "lastModifiedBy": "sami@tracecloud.com",
		            "owner": "qa_mktg_1@tracecloud.com"},
		         "traceToRequirementId": 1507,
		         "traceFromRequirementId": 1584,
		         "traceId": 307,
		         "suspect": 1}]}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
					
										
								
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Get Requirements TraceFrom				
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='getRequirementTraceFromA'></a>
									<a href='#getRequirementTraceFromA' onClick='
										document.getElementById("getRequirementTraceFromDiv").style.display = "block";'>
										 Get the list of Requirements that this Requirement has a Trace From</a> 
									<div class='alert alert-success' id='getRequirementTraceFromDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#getRequirementTraceFromA' onClick='document.getElementById("getRequirementTraceFromDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Get the list of Requirements that this Requirement has a Trace From 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													The API Key , Requirement Id
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be a member of the project in which this Requirement exists. And he/she needs to have read permissions
													on the requirement 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQ&action=getRequirementTraceFrom&requirementId=19749
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre>{
		   "errorMessage": "",
		   "requirementDetails": {
		      "approvers": "",
		      "externalURL": "",
		      "status": "Approved",
		      "tag": 69,
		      "userDefinedAttributes": "Type:#: Enhancement:##:Severity:#:3:
		      ##:Impact to customer:#:yes",
		      "folderPath": "Business Requirements\/Administration Requirements",
		      "traceFrom": "FR-19,FR-27",
		      "projectId": 19,
		      "deleted": 0,
		      "pctComplete": 100,
		      "requirementId": 1584,
		      "traceTo": "(s)REL-2",
		      "approvedByAllDt": "16 August 2009 03:44:01 PM ",
		      "folderId": 121,
		      "version": 1,
		      "fullTag": "BR-69",
		      "createdBy": "qa_mktg_1@tracecloud.com",
		      "requirementTypeName": "Business Requirements",
		      "description": "Administrator should be able to update comma separated
		       list of domains in Project Core Info Page. Only members whose emails
		       end in restricted domains should be able to become members of this
		       project.",
		      "priority": "Medium",
		      "name": "Administrator should be able to update comma separated list 
		      of domains in Project Core Info Page. ",
		      "lastModifiedBy": "sami@tracecloud.com",
		      "owner": "qa_mktg_1@tracecloud.com"},
		   "requirementTraceFrom": [
		      {
		         "requirementDetails": {
		            "approvers": "",
		            "externalURL": "",
		            "status": "Approved",
		            "tag": 19,
		            "userDefinedAttributes": "",
		            "folderPath": "Functional Requirements\/Administration Feature",
		            "traceFrom": "",
		            "projectId": 19,
		            "deleted": 0,
		            "pctComplete": 100,
		            "requirementId": 1808,
		            "traceTo": "BR-69",
		            "approvedByAllDt": "16 August 2009 04:25:43 PM ",
		            "folderId": 120,
		            "version": 1,
		            "fullTag": "FR-19",
		            "createdBy": "qa_dev_1@tracecloud.com",
		            "requirementTypeName": "Functional Requirements",
		            "description": "Administrator should be able to enter a comma separated 
		            list of domains in Project Core Info page. Only members whose emails 
		            names end in these restricted domains should be able to become members 
		            of this project.",
		            "priority": "Medium",
		            "name": "Administrator should be able to enter a comma separated list 
		            of domains in Project Core Info page.",
		            "lastModifiedBy": "sami@tracecloud.com",
		            "owner": "qa_dev_1@tracecloud.com"},
		         "traceToRequirementId": 1584,
		         "traceFromRequirementId": 1808,
		         "traceId": 518,
		         "suspect": 0},
		      {
		         "requirementDetails": {
		            "approvers": "",
		            "externalURL": "",
		            "status": "Approved",
		            "tag": 27,
		            "userDefinedAttributes": "",
		            "folderPath": "Functional Requirements\/Administration Feature",
		            "traceFrom": "",
		            "projectId": 19,
		            "deleted": 0,
		            "pctComplete": 100,
		            "requirementId": 1816,
		            "traceTo": "BR-69",
		            "approvedByAllDt": "16 August 2009 04:25:44 PM ",
		            "folderId": 120,
		            "version": 1,
		            "fullTag": "FR-27",
		            "createdBy": "qa_dev_1@tracecloud.com",
		            "requirementTypeName": "Functional Requirements",
		            "description": "Administrator should be able to update comma separated 
		            list of domains in Project Core Info Page. Only members whose emails 
		            end in restricted domains should be able to become members of this 
		            project.",
		            "priority": "Medium",
		            "name": "Administrator should be able to update comma separated list 
		            of domains in Project Core Info Page. ",
		            "lastModifiedBy": "sami@tracecloud.com",
		            "owner": "qa_dev_1@tracecloud.com"},
		         "traceToRequirementId": 1584,
		         "traceFromRequirementId": 1816,
		         "traceId": 526,
		         "suspect": 0}],
		   "responseStatus": "success"}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
					
					
					
					
					
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Create Requirement Type			
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br><br><br>
									<a name='createRequirementTypeA'></a> 
									<a href='#createRequirementTypeA' onClick='
										document.getElementById("createRequirementTypeDiv").style.display = "block";'>
										 Create A Requirement Type</a> 
									<div class='alert alert-success' id='createRequirementTypeDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#createRequirementTypeA' onClick='document.getElementById("createRequirementTypeDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Create A new Requirement Type 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													The API Key , projectId, TPrefix=tes, rTName, , rTDescription, rTDisplaySequence (Optional)
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be an Administrator in the Project where the Requirement Type
													is being created
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQ&action=createRequirementType&projectId=39&rTApprovalWorkflow=enable&rTPrefix=tes&rTName=from api&rTDescription=created from api
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre></pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
					
					
								
					
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Create Folder			
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='createFolderA'></a>
									<a href='#createFolderA' onClick='
										document.getElementById("createFolderDiv").style.display = "block";'>
										 Create A Folder</a> 
									<div class='alert alert-success' id='createFolderDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#createFolderA' onClick='document.getElementById("createFolderDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Create A new Folder
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													The API Key , parentFolderId, Folder Name, Folder Description
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be a member where the parentFolder resides
													and he/she has to have write privileges on the parentFolder
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQ&action=createFolder&parentFolderId=267&folderName=test3&folderDescription= test folder created by api
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre></pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
					
					
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Create Attribute			
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='createRequirementTypeAttributeA'></a>
									<a href='#createRequirementTypeAttributeA' onClick='
										document.getElementById("createRequirementTypeAttributeDiv").style.display = "block";'>
										 Create A Requirement Type Attribute</a> 
									<div class='alert alert-success' id='createRequirementTypeAttributeDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#createRequirementTypeAttributeA' onClick='document.getElementById("createRequirementTypeAttributeDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Create A Requirement Type Attribute
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be an administrator
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre></pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
					
					
		
		
		
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Create Trace			
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br><br><br>
									<a name='createTraceA'></a>
									<a href='#createTraceA' onClick='
										document.getElementById("createTraceDiv").style.display = "block";'>
										 Create A Trace</a> 
									<div class='alert alert-success' id='createTraceDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#createTraceA' onClick='document.getElementById("createTraceDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Create A Trace to / from this requirement.
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													requirementId (for which the operation is taking place eg : 19234), 
													traceTo(FullTag of the requirement to which the trace should go eg :REL-3)
													traceFrom (FullTag of the requirement from which the trace should come eg : FR-1)
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be a member of the project where these Requirements reside,
													and have Trace permissions on both the Requirements
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<br>
													<b>Sample Call to Trace 'requirementId=106787' To a requirement called 'REL-3'</b><br>
													<pre>https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQO&action=createTrace&requirementId=106787&traceTo=rel-3</pre>
													<br><br>
													<b>Sample Call to Trace From a requirement called 'FR-332' to requirementId=106787' </b><br>
													<pre>https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQO&action=createTrace&requirementId=106787&traceFrom=FR-332</pre>
													<br><br>
													<b>To create trace to / from across project, simply attach the prefix of the project before the requirement tag. </b><br>
													In this example, we are tracing to a requirement in another project, called 'AST'.
													<pre>https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQO&action=createTrace&requirementId=106787&traceTo=AST:rel-1</pre>
													
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
													<br>
													<pre>{
		   "errorMessage": "",
		   "requirementDetails": {
		      "approvers": "",
		      "externalURL": "",
		      "status": "Approved",
		      "tag": 237,
		      "userDefinedAttributes": "",
		      "folderPath": "Functional Requirements\/Trace Tree Report",
		      "traceFrom": "",
		      "projectId": 19,
		      "deleted": 0,
		      "pctComplete": 100,
		      "requirementId": 2026,
		      "traceTo": "BR-195",
		      "approvedByAllDt": "16 August 2009 10:26:34 PM ",
		      "folderId": 129,
		      "version": 1,
		      "fullTag": "FR-237",
		      "createdBy": "qa_dev_1@tracecloud.com",
		      "requirementTypeName": "Functional Requirements",
		      "description": "A User should be able to create and run a report based on criteria 
		      selected in Trace Tree Report tab.",
		      "priority": "Medium",
		      "name": "A User should be able to create and run a report based on criteria selected
		      in Trace Tree Report t",
		      "lastModifiedBy": "sami@tracecloud.com",
		      "owner": "qa_dev_1@tracecloud.com"},
		   "responseStatus": "success"}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
		
		
		
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Delete Trace			
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='deleteTraceA'></a>
									<a href='#deleteTraceA' onClick='
										document.getElementById("deleteTraceDiv").style.display = "block";'>
										 Delete A Trace</a> 
									<div class='alert alert-success' id='deleteTraceDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#deleteTraceA' onClick='document.getElementById("deleteTraceDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Deletes a A Trace between two requirements 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													requirementId (for which the operation is taking place eg : 19234) and  
													traceTo(FullTag of the requirement to which this requirementId is tracing eg :REL-3)
													<br><br>
													or 
													<br><br>
													requirementId (for which the operation is taking place eg : 19234) and  
													traceFrom (FullTag of the requirement from which the trace should come eg : FR-1)
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be a member of the project where these Requirements reside
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<br>
													<b>Sample Call to Delete a Trace from 'requirementId=106787' To a requirement called 'REL-3'</b><br>
													<pre>https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQO&action=deleteTrace&requirementId=106787&traceTo=rel-3</pre>
													<br><br>
													<b>Sample Call to Delete a Trace From a requirement called 'FR-332' to requirementId=106787' </b><br>
													<pre>https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQO&action=deleteTrace&requirementId=106787&traceFrom=FR-332</pre>
													<br><br>
													<b>To Delete a trace to / from across projects, simply attach the prefix of the project before the requirement tag. </b><br>
													In this example, we are delete a trace to a requirement in another project, called 'AST'.
													<pre>https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQO&action=deleteTrace&requirementId=106787&traceTo=AST:rel-1</pre>
													
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
													<br>
													<pre>{
		   "errorMessage": "",
		   "requirementDetails": {
		      "approvers": "",
		      "externalURL": "",
		      "status": "Approved",
		      "tag": 237,
		      "userDefinedAttributes": "",
		      "folderPath": "Functional Requirements\/Trace Tree Report",
		      "traceFrom": "",
		      "projectId": 19,
		      "deleted": 0,
		      "pctComplete": 100,
		      "requirementId": 2026,
		      "traceTo": "BR-195",
		      "approvedByAllDt": "16 August 2009 10:26:34 PM ",
		      "folderId": 129,
		      "version": 1,
		      "fullTag": "FR-237",
		      "createdBy": "qa_dev_1@tracecloud.com",
		      "requirementTypeName": "Functional Requirements",
		      "description": "A User should be able to create and run a report based on criteria 
		      selected in Trace Tree Report tab.",
		      "priority": "Medium",
		      "name": "A User should be able to create and run a report based on criteria selected
		      in Trace Tree Report t",
		      "lastModifiedBy": "sami@tracecloud.com",
		      "owner": "qa_dev_1@tracecloud.com"},
		   "responseStatus": "success"}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
		
		
		
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Clear Trace			
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='clearTraceA'></a>
									<a href='#clearTraceA' onClick='
										document.getElementById("clearTraceDiv").style.display = "block";'>
										 Clear A Trace</a> 
									<div class='alert alert-success' id='clearTraceDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#clearTraceA' onClick='document.getElementById("clearTraceDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Clears  a A Suspect Trace between two requirements 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													requirementId (for which the operation is taking place eg : 19234) and  
													traceTo(FullTag of the requirement to which this requirementId is tracing eg :REL-3)
													<br><br>
													or 
													<br><br>
													requirementId (for which the operation is taking place eg : 19234) and  
													traceFrom (FullTag of the requirement from which the trace should come eg : FR-1)
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be a member of the project where these Requirements reside
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<br>
													<b>Sample Call to Clear a Suspect Trace from 'requirementId=106787' To a requirement called 'REL-3'</b><br>
													<pre>https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQO&action=clearSuspectTrace&requirementId=106787&traceTo=rel-3</pre>
													<br><br>
													<b>Sample Call to Clear a Suspect Trace From a requirement called 'FR-332' to requirementId=106787' </b><br>
													<pre>https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQO&action=clearSuspectTrace&requirementId=106787&traceFrom=FR-332</pre>
													<br><br>
													<b>To Clear a Suspect trace to / from across projects, simply attach the prefix of the project before the requirement tag. </b><br>
													In this example, we are delete a trace to a requirement in another project, called 'AST'.
													<pre>https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQO&action=clearSuspectTrace&requirementId=106787&traceTo=AST:rel-1</pre>
													
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
													<br>
													<pre>{
		   "errorMessage": "",
		   "requirementDetails": {
		      "approvers": "",
		      "externalURL": "",
		      "status": "Approved",
		      "tag": 237,
		      "userDefinedAttributes": "",
		      "folderPath": "Functional Requirements\/Trace Tree Report",
		      "traceFrom": "",
		      "projectId": 19,
		      "deleted": 0,
		      "pctComplete": 100,
		      "requirementId": 2026,
		      "traceTo": "BR-195",
		      "approvedByAllDt": "16 August 2009 10:26:34 PM ",
		      "folderId": 129,
		      "version": 1,
		      "fullTag": "FR-237",
		      "createdBy": "qa_dev_1@tracecloud.com",
		      "requirementTypeName": "Functional Requirements",
		      "description": "A User should be able to create and run a report based on criteria 
		      selected in Trace Tree Report tab.",
		      "priority": "Medium",
		      "name": "A User should be able to create and run a report based on criteria selected
		      in Trace Tree Report t",
		      "lastModifiedBy": "sami@tracecloud.com",
		      "owner": "qa_dev_1@tracecloud.com"},
		   "responseStatus": "success"}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
		
		
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Set Suspect Trace			
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='setSuspectTrace'></a>
									<a href='#setSuspectTrace' onClick='
										document.getElementById("setSuspectTraceDiv").style.display = "block";'>
										 Set A Trace to Suspect</a> 
									<div class='alert alert-success' id='setSuspectTraceDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#setSuspectTrace' onClick='document.getElementById("setSuspectTraceDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Sets a Trace between two requirements to suspect 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													requirementId (for which the operation is taking place eg : 19234) and  
													traceTo(FullTag of the requirement to which this requirementId is tracing eg :REL-3)
													<br><br>
													or 
													<br><br>
													requirementId (for which the operation is taking place eg : 19234) and  
													traceFrom (FullTag of the requirement from which the trace should come eg : FR-1)
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be a member of the project where these Requirements reside
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<br>
													<b>Sample Call to make Suspect a Trace from 'requirementId=106787' To a requirement called 'REL-3'</b><br>
													<pre>https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQO&action=makeTraceSuspect&requirementId=106787&traceTo=rel-3</pre>
													<br><br>
													<b>Sample Call to make Suspect a Trace From a requirement called 'FR-332' to requirementId=106787' </b><br>
													<pre>https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQO&action=makeTraceSuspect&requirementId=106787&traceFrom=FR-332</pre>
													<br><br>
													<b>To make Suspect a  trace to / from across projects, simply attach the prefix of the project before the requirement tag. </b><br>
													In this example, we are delete a trace to a requirement in another project, called 'AST'.
													<pre>https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQO&action=makeTraceSuspect&requirementId=106787&traceTo=AST:rel-1</pre>
													
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
													<br>
													<pre>{
		   "errorMessage": "",
		   "requirementDetails": {
		      "approvers": "",
		      "externalURL": "",
		      "status": "Approved",
		      "tag": 237,
		      "userDefinedAttributes": "",
		      "folderPath": "Functional Requirements\/Trace Tree Report",
		      "traceFrom": "",
		      "projectId": 19,
		      "deleted": 0,
		      "pctComplete": 100,
		      "requirementId": 2026,
		      "traceTo": "BR-195",
		      "approvedByAllDt": "16 August 2009 10:26:34 PM ",
		      "folderId": 129,
		      "version": 1,
		      "fullTag": "FR-237",
		      "createdBy": "qa_dev_1@tracecloud.com",
		      "requirementTypeName": "Functional Requirements",
		      "description": "A User should be able to create and run a report based on criteria 
		      selected in Trace Tree Report tab.",
		      "priority": "Medium",
		      "name": "A User should be able to create and run a report based on criteria selected
		      in Trace Tree Report t",
		      "lastModifiedBy": "sami@tracecloud.com",
		      "owner": "qa_dev_1@tracecloud.com"},
		   "responseStatus": "success"}</pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
		
		
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Create Requirement			
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br><br><br>
									<a name='createRequirementA'></a>
									<a href='#createRequirementA' onClick='
										document.getElementById("createRequirementDiv").style.display = "block";'>
										 Create A Requirement </a> 
									<div class='alert alert-success' id='createRequirementDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#createRequirementA' onClick='document.getElementById("createRequirementDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Create A Requirement 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be have Create Requirements privileges on the parent folder
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre></pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
		
		
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Delete Requirement			
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='deleteRequirementA'></a>
									<a href='#deleteRequirementA' onClick='
										document.getElementById("deleteRequirementDiv").style.display = "block";'>
										 Delete A Requirement </a> 
									<div class='alert alert-success' id='deleteRequirementDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#deleteRequirementA' onClick='document.getElementById("deleteRequirementDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Delete (Can be restored) A Requirement 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													API Key and Requirement id
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be have Delete Requirements privileges on the parent folder
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre></pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
		
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Purge Requirement			
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='purgeRequirementA'></a>
									<a href='#purgeRequirementA' onClick='
										document.getElementById("purgeRequirementDiv").style.display = "block";'>
										 Purge A Requirement </a> 
									<div class='alert alert-success' id='purgeRequirementDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#purgeRequirementA' onClick='document.getElementById("purgeRequirementDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Purge (Permanently Delete) A Requirement 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													API Key and Requirement id
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be have Delete Requirements privileges on the parent folder
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre></pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
		
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Move Requirement			
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='moveRequirementA'></a>
									<a href='#moveRequirementA' onClick='
										document.getElementById("moveRequirementDiv").style.display = "block";'>
										 Move A Requirement </a> 
									<div class='alert alert-success' id='moveRequirementDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#moveRequirementA' onClick='document.getElementById("moveRequirementDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Move A Requirement to a different folder 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													API Key and Requirement id and Target Folder Id
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be have Edit Requirements privileges on the parent folder 
													of the called Requirement and on the Target Folder
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre></pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
		
		
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Change Owner Requirement			
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='changeOwnerA'></a>
									<a href='#changeOwnerA' onClick='
										document.getElementById("changeOwnerDiv").style.display = "block";'>
										 Change Owner for Requirement </a> 
									<div class='alert alert-success' id='changeOwnerDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#changeOwnerA' onClick='document.getElementById("changeOwnerDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Change Requirement owner i.e assign someone else as the responsible
													person for this requirement 
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													API Key and Requirement id and new Owner Id (user Id) or new Owner Email Id
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be have Edit Requirements privileges on the parent folder 
													of the called Requirement and the new Owner should be a member of this project.
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre></pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
		
		
		<!--////////////////////////////////////////////////////////////////////////////////////////
		// Change Attribute			
		////////////////////////////////////////////////////////////////////////////////////////-->						
							
								<tr> 
									<td  colspan='2'>
									<br>
									<a name='changeAttributeA'></a>
									<a href='#changeAttributeA' onClick='
										document.getElementById("changeAttributeDiv").style.display = "block";'>
										 Change Requirement Attribute</a> 
									<div class='alert alert-success' id='changeAttributeDiv' style="display:none;">
										<table class='paddedTable' >
											<tr>
												<td align='right'>
												<a href='#changeAttributeA' onClick='document.getElementById("changeAttributeDiv").style.display = "none";'>
												Close </a>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Description</b><br>
													Change Requirement Attribute value
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Parameters</b><br>
													API Key and Requirement id and attribute label and attribute value
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Permissions</b><br>
													The caller has to be have Edit Requirements privileges on the parent folder 
													of the called Requirement and the attribute label should match existing
													standard or custom attributes. Also the value has to match whats acceptable
													for this attribute. i.e Priority has to be High, Medium or Low etc..
													</span>
												</td>
											</tr>
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Call</b><br>
													</span>
												</td>
											</tr>									
											<tr>
												<td>
													<span class='normalText10'>
													<b>Sample Result</b><br>
												<pre></pre>
													</span>
												</td>
											</tr>									
											
										</table>
									</div>
									</td>							
								</tr>					
		
		
		
		
		
		
		
																
						</table>
						
						
						
					</div>
	


</body></html>