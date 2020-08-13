<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String displayListReportIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayListReportIsLoggedIn == null) || (displayListReportIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dRIsMember = false;
	Project project= (Project) session.getAttribute("project");
	SecurityProfile dRSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dRSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		dRIsMember = true;
	}
%>

<%if (dRIsMember){ 
	int rTBaselineId = Integer.parseInt(request.getParameter("rTBaselineId"));
	
	// lets get the list of distinct req types in this release tree. 
	ArrayList reqTypes = ReleaseMetricsUtil.getRequirementTypesForBaseline(rTBaselineId,project.getProjectId());
	
	String allRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForBaseline(rTBaselineId,project.getProjectId(), "numOfRequirements", databaseType);
	String draftRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForBaseline(rTBaselineId,project.getProjectId(), "numOfDraftRequirements", databaseType);
	String inApprovalWorkflowRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForBaseline(rTBaselineId,project.getProjectId(), "numOfInApprovalWorkflowRequirements", databaseType);
	String rejectedRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForBaseline(rTBaselineId,project.getProjectId(), "numOfRejectedRequirements", databaseType);
	String approvedRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForBaseline(rTBaselineId,project.getProjectId(), "numOfApprovedRequirements", databaseType);
	String danglingRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForBaseline(rTBaselineId,project.getProjectId(), "numOfDanglingRequirements", databaseType);
	String orphanRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForBaseline(rTBaselineId,project.getProjectId(), "numOfOrphanRequirements", databaseType);
	String suspectUpstreamRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForBaseline(rTBaselineId,project.getProjectId(), "numOfSuspectUpstreamRequirements", databaseType);
	String suspectDownstreamRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForBaseline(rTBaselineId,project.getProjectId(), "numOfSuspectDownstreamRequirements", databaseType);
	String completedRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForBaseline(rTBaselineId,project.getProjectId(), "numOfCompletedRequirements", databaseType);
	String incompleteRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForBaseline(rTBaselineId,project.getProjectId(), "numOfIncompleteRequirements", databaseType);
	
	String testPendingRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForBaseline(rTBaselineId,project.getProjectId(), "numOfTestPendingRequirements", databaseType);
	String testPassRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForBaseline(rTBaselineId,project.getProjectId(), "numOfTestPassRequirements", databaseType);
	String testFailRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForBaseline(rTBaselineId,project.getProjectId(), "numOfTestFailRequirements", databaseType);
	
	
	
	String defectRequirementsDataString = ReleaseMetricsUtil.getDefectTrendDataString("baseline",rTBaselineId, databaseType);
	ArrayList defectStatusGroupsForProject = ReleaseMetricsUtil.getDefectStatusGroupsInMetrics("baseline", rTBaselineId);	
	
	
	
	// lets build the response schema and seriesDefinition for allRequirements.
	// this invovles going through all the req types in this relase and buildign a custom string.
	String allRequirementsSeriesDef = "{ type : \"line\", yField: \"totalReqs\", displayName:\"Total Requirements\" } , ";
	String allRequirementsResponseSchema = "\"dataLoadDt\", \"totalReqs\",  ";	
	Iterator i = reqTypes.iterator();
	while (i.hasNext()){
		String reqTypeShortName = (String) i.next();
		allRequirementsResponseSchema += "\"" + reqTypeShortName + "\",";
		
		
		allRequirementsSeriesDef +=  "	{ " +
				" yField: \""+ reqTypeShortName +"\", " +
				" displayName: \""+ reqTypeShortName +"\" " +
				" }, ";
	}
	// drop the last ,
	if (allRequirementsResponseSchema.contains(",")) {
		allRequirementsResponseSchema = (String) allRequirementsResponseSchema.subSequence(0,allRequirementsResponseSchema.lastIndexOf(","));
	}	
	if (allRequirementsSeriesDef.contains(",")) {
		allRequirementsSeriesDef = (String) allRequirementsSeriesDef.subSequence(0,allRequirementsSeriesDef.lastIndexOf(","));
	}	
	
	// since seriesDef and response schema are the same for every chart, we can reuse the same one.

	// Defect Requirements work out of a seperate table and are structured differently.
	// hence we have a seperate series def / response schema structure
	String defectRequirementsSeriesDef = "";
	String defectRequirementsResponseSchema = "\"dataLoadDt\",";
	Iterator j = defectStatusGroupsForProject.iterator();
	while (j.hasNext()){
		
		String statusGroup = (String) j.next();
		defectRequirementsResponseSchema += "\"" + statusGroup + "\",";
		
		defectRequirementsSeriesDef +=  "	{ " +
		" yField: \""+ statusGroup +"\", " +
		" displayName: \""+ statusGroup +"\" " +
		" }, ";
	}
	// drop the last , 
	if (defectRequirementsResponseSchema.contains(",")) {
		defectRequirementsResponseSchema = (String) defectRequirementsResponseSchema.subSequence(0,defectRequirementsResponseSchema.lastIndexOf(","));
	}
	
	if (defectRequirementsSeriesDef.contains(",")) {
		defectRequirementsSeriesDef = (String) defectRequirementsSeriesDef.subSequence(0,defectRequirementsSeriesDef.lastIndexOf(","));
	}
	
%>



<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/yahoo-dom-event/yahoo-dom-event.js"></script> 
<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/element/element-min.js"></script> 
<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/json/json-min.js"></script> 
<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/datasource/datasource-min.js"></script> 
<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/swf/swf-min.js"></script> 
<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/charts/charts-min.js"></script>
<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/tabview/tabview-min.js"></script> 


<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/tabview/assets/skins/sam/tabview.css"> 

	
	<!-- Gloree JS and CSS files -->
	
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/css/common.css"> 
	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userAccount.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userDashboard.js?v=20200630"></script>
	
<style type="text/css">

	.chart
	{
		width: 700px;
		height: 250px;
		margin-bottom: 5px;
	}

	.chart_title
	{
		display: block;
		font-size: 1.2em;
		font-weight: bold;
		margin-bottom: 0.4em;
	}

	#tabContainer
	{
		width: 800px;
	}
	
</style>



</head>
<body id="yahoo-com" class=" yui-skin-sam">

	<div class='level1Box' style="text-align:left">		
		<span class="chart_title">Baseline Trends
		
								
			<a href='/GloreeJava2/servlet/ReportAction?action=exportBaselineTrendData&rTBaselineId=<%=rTBaselineId%>'
						target='_blank'  title='Excel file of Baseline Trends Data'>
				<img src="/GloreeJava2/images/ExportExcel16.gif"  border="0">
			</a> 
			
		</span>

			
		<div id="tabContainer" style="width:100%"> 
			<span class='normalText'>
			For best performance, please ensure  that you have the latest 
			<a href="http://www.adobe.com/go/getflashplayer" TARGET="_blank">
 			Adobe Flash Player </a> installed.
 			</span>
 		</div>

		<script type="text/javascript">

			YAHOO.widget.Chart.SWFURL = "/GloreeJava2/js/yui.2.7.0/build/charts/assets/charts.swf";
			
		
		//--- data
		
			YAHOO.example.allRequirementsDataString = <%=allRequirementsDataString%>
			var allRequirementsDataSource = new YAHOO.util.DataSource( YAHOO.example.allRequirementsDataString );
			allRequirementsDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
			allRequirementsDataSource.responseSchema = { fields: [ <%=allRequirementsResponseSchema%> ] };
		
			YAHOO.example.draftRequirementsDataString = <%=draftRequirementsDataString%>
			var draftRequirementsDataSource = new YAHOO.util.DataSource( YAHOO.example.draftRequirementsDataString );
			draftRequirementsDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
			draftRequirementsDataSource.responseSchema = { fields: [ <%=allRequirementsResponseSchema%> ] };
		
			YAHOO.example.inApprovalWorkflowRequirementsDataString = <%=inApprovalWorkflowRequirementsDataString%>
			var inApprovalWorkflowRequirementsDataSource = new YAHOO.util.DataSource( YAHOO.example.inApprovalWorkflowRequirementsDataString );
			inApprovalWorkflowRequirementsDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
			inApprovalWorkflowRequirementsDataSource.responseSchema = { fields: [ <%=allRequirementsResponseSchema%> ] };
		
			YAHOO.example.rejectedRequirementsDataString = <%=rejectedRequirementsDataString%>
			var rejectedRequirementsDataSource = new YAHOO.util.DataSource( YAHOO.example.rejectedRequirementsDataString );
			rejectedRequirementsDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
			rejectedRequirementsDataSource.responseSchema = { fields: [ <%=allRequirementsResponseSchema%> ] };
			
			YAHOO.example.approvedRequirementsDataString = <%=approvedRequirementsDataString%>
			var approvedRequirementsDataSource = new YAHOO.util.DataSource( YAHOO.example.approvedRequirementsDataString );
			approvedRequirementsDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
			approvedRequirementsDataSource.responseSchema = { fields: [ <%=allRequirementsResponseSchema%> ] };
		
			YAHOO.example.danglingRequirementsDataString = <%=danglingRequirementsDataString%>
			var danglingRequirementsDataSource = new YAHOO.util.DataSource( YAHOO.example.danglingRequirementsDataString );
			danglingRequirementsDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
			danglingRequirementsDataSource.responseSchema = { fields: [ <%=allRequirementsResponseSchema%> ] };
		
			YAHOO.example.orphanRequirementsDataString = <%=orphanRequirementsDataString%>
			var orphanRequirementsDataSource = new YAHOO.util.DataSource( YAHOO.example.orphanRequirementsDataString );
			orphanRequirementsDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
			orphanRequirementsDataSource.responseSchema = { fields: [ <%=allRequirementsResponseSchema%> ] };
		
			YAHOO.example.suspectUpstreamRequirementsDataString = <%=suspectUpstreamRequirementsDataString%>
			var suspectUpstreamRequirementsDataSource = new YAHOO.util.DataSource( YAHOO.example.suspectUpstreamRequirementsDataString );
			suspectUpstreamRequirementsDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
			suspectUpstreamRequirementsDataSource.responseSchema = { fields: [ <%=allRequirementsResponseSchema%> ] };
		
			YAHOO.example.suspectDownstreamRequirementsDataString = <%=suspectDownstreamRequirementsDataString%>
			var suspectDownstreamRequirementsDataSource = new YAHOO.util.DataSource( YAHOO.example.suspectDownstreamRequirementsDataString );
			suspectDownstreamRequirementsDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
			suspectDownstreamRequirementsDataSource.responseSchema = { fields: [ <%=allRequirementsResponseSchema%> ] };
		
			YAHOO.example.completedRequirementsDataString = <%=completedRequirementsDataString%>
			var completedRequirementsDataSource = new YAHOO.util.DataSource( YAHOO.example.completedRequirementsDataString );
			completedRequirementsDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
			completedRequirementsDataSource.responseSchema = { fields: [ <%=allRequirementsResponseSchema%> ] };
		
			YAHOO.example.incompleteRequirementsDataString = <%=incompleteRequirementsDataString%>
			var incompleteRequirementsDataSource = new YAHOO.util.DataSource( YAHOO.example.incompleteRequirementsDataString );
			incompleteRequirementsDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
			incompleteRequirementsDataSource.responseSchema = { fields: [ <%=allRequirementsResponseSchema%> ] };
		
			YAHOO.example.testPendingRequirementsDataString = <%=testPendingRequirementsDataString%>
			var testPendingRequirementsDataSource = new YAHOO.util.DataSource( YAHOO.example.testPendingRequirementsDataString );
			testPendingRequirementsDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
			testPendingRequirementsDataSource.responseSchema = { fields: [ <%=allRequirementsResponseSchema%> ] };

			YAHOO.example.testPassRequirementsDataString = <%=testPassRequirementsDataString%>
			var testPassRequirementsDataSource = new YAHOO.util.DataSource( YAHOO.example.testPassRequirementsDataString );
			testPassRequirementsDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
			testPassRequirementsDataSource.responseSchema = { fields: [ <%=allRequirementsResponseSchema%> ] };

			YAHOO.example.testFailRequirementsDataString = <%=testFailRequirementsDataString%>
			var testFailRequirementsDataSource = new YAHOO.util.DataSource( YAHOO.example.testFailRequirementsDataString );
			testFailRequirementsDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
			testFailRequirementsDataSource.responseSchema = { fields: [ <%=allRequirementsResponseSchema%> ] };
			
			YAHOO.example.defectRequirementsDataString = <%=defectRequirementsDataString%>
			var defectRequirementsDataSource = new YAHOO.util.DataSource( YAHOO.example.defectRequirementsDataString );
			defectRequirementsDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
			defectRequirementsDataSource.responseSchema = { fields: [ <%=defectRequirementsResponseSchema%> ] };

			//--- tabView
		
			//Create a TabView
			var tabView = new YAHOO.widget.TabView();
		
			//Add a tab for the all reqs Chart
			tabView.addTab( new YAHOO.widget.Tab({
					label: 'All',
					content: '<span class="chart_title">All Requirements in Baseline</span><div class="chart" id="allRequirementsChart"></div>',
					active: true
			}));
		



			//Add a tab for the draft reqs Chart
			tabView.addTab( new YAHOO.widget.Tab({
					label: 'Draft',
					content: '<span class="chart_title">Draft Requirements in Baseline</span><div class="chart" id="draftRequirementsChart"></div>'
			}));
		
			//Add a tab for the in approval work flow Chart
			tabView.addTab( new YAHOO.widget.Tab({
					label: 'Pending',
					content: '<span class="chart_title">In Approval Workflow Requirements in Baseline</span><div class="chart" id="inApprovalWorkflowRequirementsChart"></div>'
			}));
		
			//Add a tab for the rejected reqs Chart
			tabView.addTab( new YAHOO.widget.Tab({
					label: 'Rejected',
					content: '<span class="chart_title">Rejected Requirements in Baseline</span><div class="chart" id="rejectedRequirementsChart"></div>'
			}));
		
			//Add a tab for the approved reqs Chart
			tabView.addTab( new YAHOO.widget.Tab({
					label: 'Approved',
					content: '<span class="chart_title">Approved Requirements in Baseline</span><div class="chart" id="approvedRequirementsChart"></div>'
			}));



			
			
			//Add a tab for the dangling reqs Chart
			tabView.addTab( new YAHOO.widget.Tab({
					label: 'Dangling',
					content: '<span class="chart_title">Dangling (without a child) Requirements in Baseline</span><div class="chart" id="danglingRequirementsChart"></div>'
			}));
		
			//Add a tab for the orphan reqs Chart
			tabView.addTab( new YAHOO.widget.Tab({
					label: 'Orphan',
					content: '<span class="chart_title">Orphan (without a parent) Requirements in Baseline</span><div class="chart" id="orphanRequirementsChart"></div>'
			}));
			//Add a tab for the suspectUpstream reqs Chart
			tabView.addTab( new YAHOO.widget.Tab({
					label: 'Suspect Upstream',
					content: '<span class="chart_title">Requirements with a suspect trace upstream in Baseline</span><div class="chart" id="suspectUpstreamRequirementsChart"></div>'
			}));
		
			//Add a tab for the suspectDownstream reqs Chart
			tabView.addTab( new YAHOO.widget.Tab({
					label: 'Suspect Downstream',
					content: '<span class="chart_title">Requirements with a suspect trace downstream in Baseline</span><div class="chart" id="suspectDownstreamRequirementsChart"></div>'
			}));
		


			tabView.addTab( new YAHOO.widget.Tab({
				label: 'Defects',
				content: '<span class="chart_title">Defects in Baseline</span><div class="chart" id="defectRequirementsChart"></div>'
			}));

			
		
			//Add a tab for the Test Pending reqs Chart
			tabView.addTab( new YAHOO.widget.Tab({
					label: 'Test Pending',
					content: '<span class="chart_title">Requirements Pending Testing in Project</span><div class="chart" id="testPendingRequirementsChart"></div>'
			}));

			//Add a tab for the Test Pass reqs Chart
			tabView.addTab( new YAHOO.widget.Tab({
					label: 'Test Pass',
					content: '<span class="chart_title">Requirements Passed Testing in Project</span><div class="chart" id="testPassRequirementsChart"></div>'
			}));


			//Add a tab for the Test Fail reqs Chart
			tabView.addTab( new YAHOO.widget.Tab({
					label: 'Test Fail',
					content: '<span class="chart_title">Requirements Failed Testing in Project</span><div class="chart" id="testFailRequirementsChart"></div>'
			}));



			//Add a tab for the completed reqs Chart
			tabView.addTab( new YAHOO.widget.Tab({
					label: 'Completed',
					content: '<span class="chart_title">Completed Requirements in Baseline</span><div class="chart" id="completedRequirementsChart"></div>'
			}));
		
		
			//Add a tab for the incomplete reqs Chart
			tabView.addTab( new YAHOO.widget.Tab({
					label: 'Incomplete',
					content: '<span class="chart_title">Incomplete Requirements in Baseline</span><div class="chart" id="incompleteRequirementsChart"></div>'
			}));
			
			
			//Append TabView to its container div
			tabView.appendTo('tabContainer');
		
		
			
		//--- chart
		
			var allRequirementsSeriesDef = [<%=allRequirementsSeriesDef%> ];
			var defectRequirementsSeriesDef = [<%=defectRequirementsSeriesDef%> ];
		
		
			//Numeric Axis for our currency
			var currencyAxis = new YAHOO.widget.NumericAxis();
			currencyAxis.stackingEnabled = true;
			var styleDef = 
			{
				legend:
				{
					display: "bottom",
					padding: 10,
					spacing: 5,
					font:
					{
						family: "Arial",
						size: 11
					}
				}
			};			
			
		

			var allRequirementschart = new YAHOO.widget.StackedColumnChart( "allRequirementsChart", allRequirementsDataSource,
					{
						style: styleDef,
						series: allRequirementsSeriesDef,
						xField: "dataLoadDt",
						yAxis: currencyAxis,
						//only needed for flash player express install
						expressInstall: "assets/expressinstall.swf"
					});
				
					var draftRequirementschart = new YAHOO.widget.StackedColumnChart( "draftRequirementsChart", draftRequirementsDataSource,
						{
							style: styleDef,
							series: allRequirementsSeriesDef,
							xField: "dataLoadDt",
							yAxis: currencyAxis,
							//only needed for flash player express install
							expressInstall: "assets/expressinstall.swf"
						});
				
					var inApprovalWorkflowRequirementschart = new YAHOO.widget.StackedColumnChart( "inApprovalWorkflowRequirementsChart", inApprovalWorkflowRequirementsDataSource,
						{
							style: styleDef,
							series: allRequirementsSeriesDef,
							xField: "dataLoadDt",
							yAxis: currencyAxis,
							//only needed for flash player express install
							expressInstall: "assets/expressinstall.swf"
						});
					
					var rejectedRequirementschart = new YAHOO.widget.StackedColumnChart( "rejectedRequirementsChart", rejectedRequirementsDataSource,
						{
							style: styleDef,				
							series: allRequirementsSeriesDef,
							xField: "dataLoadDt",
							yAxis: currencyAxis,
							//only needed for flash player express install
							expressInstall: "assets/expressinstall.swf"
						});
				
					var approvedRequirementschart = new YAHOO.widget.StackedColumnChart( "approvedRequirementsChart", approvedRequirementsDataSource,
						{
							style: styleDef,				
							series: allRequirementsSeriesDef,
							xField: "dataLoadDt",
							yAxis: currencyAxis,
							//only needed for flash player express install
							expressInstall: "assets/expressinstall.swf"
						});
				
					var danglingRequirementschart = new YAHOO.widget.StackedColumnChart( "danglingRequirementsChart", danglingRequirementsDataSource,
						{
							style: styleDef,				
							series: allRequirementsSeriesDef,
							xField: "dataLoadDt",
							yAxis: currencyAxis,
							//only needed for flash player express install
							expressInstall: "assets/expressinstall.swf"
						});
			
					var orphanRequirementschart = new YAHOO.widget.StackedColumnChart( "orphanRequirementsChart", orphanRequirementsDataSource,
							{
								style: styleDef,				
								series: allRequirementsSeriesDef,
								xField: "dataLoadDt",
								yAxis: currencyAxis,
								//only needed for flash player express install
								expressInstall: "assets/expressinstall.swf"
							});
				
					var suspectUpstreamRequirementschart = new YAHOO.widget.StackedColumnChart( "suspectUpstreamRequirementsChart", suspectUpstreamRequirementsDataSource,
							{
								style: styleDef,				
								series: allRequirementsSeriesDef,
								xField: "dataLoadDt",
								yAxis: currencyAxis,
								//only needed for flash player express install
								expressInstall: "assets/expressinstall.swf"
							});
				
					var suspectDownstreamRequirementschart = new YAHOO.widget.StackedColumnChart( "suspectDownstreamRequirementsChart", suspectDownstreamRequirementsDataSource,
							{
								style: styleDef,
								series: allRequirementsSeriesDef,
								xField: "dataLoadDt",
								yAxis: currencyAxis,
								//only needed for flash player express install
								expressInstall: "assets/expressinstall.swf"
							});
					
					var completedRequirementschart = new YAHOO.widget.StackedColumnChart( "completedRequirementsChart", completedRequirementsDataSource,
							{
								style: styleDef,
								series: allRequirementsSeriesDef,
								xField: "dataLoadDt",
								yAxis: currencyAxis,
								//only needed for flash player express install
								expressInstall: "assets/expressinstall.swf"
							});
				
					var incompleteRequirementschart = new YAHOO.widget.StackedColumnChart( "incompleteRequirementsChart", incompleteRequirementsDataSource,
							{
								style: styleDef,
								series: allRequirementsSeriesDef,
								xField: "dataLoadDt",
								yAxis: currencyAxis,
								//only needed for flash player express install
								expressInstall: "assets/expressinstall.swf"
							});
				
					var testPendingRequirementschart = new YAHOO.widget.StackedColumnChart( "testPendingRequirementsChart", testPendingRequirementsDataSource,
							{
								style: styleDef,
								series: allRequirementsSeriesDef,
								xField: "dataLoadDt",
								yAxis: currencyAxis,
								//only needed for flash player express install
								expressInstall: "assets/expressinstall.swf"
							});

					var testPassRequirementschart = new YAHOO.widget.StackedColumnChart( "testPassRequirementsChart", testPassRequirementsDataSource,
							{
								style: styleDef,
								series: allRequirementsSeriesDef,
								xField: "dataLoadDt",
								yAxis: currencyAxis,
								//only needed for flash player express install
								expressInstall: "assets/expressinstall.swf"
							});		

					var testFailRequirementschart = new YAHOO.widget.StackedColumnChart( "testFailRequirementsChart", testFailRequirementsDataSource,
							{
								style: styleDef,
								series: allRequirementsSeriesDef,
								xField: "dataLoadDt",
								yAxis: currencyAxis,
								//only needed for flash player express install
								expressInstall: "assets/expressinstall.swf"
							});		
		

					var defectRequirementschart = new YAHOO.widget.LineChart( "defectRequirementsChart", defectRequirementsDataSource,
							{
								style: styleDef,
								series: defectRequirementsSeriesDef,
								xField: "dataLoadDt",
								yAxis: currencyAxis,
								//only needed for flash player express install
								expressInstall: "assets/expressinstall.swf"
							});		

					
		</script>	

	</div>
</body>
<%}%>