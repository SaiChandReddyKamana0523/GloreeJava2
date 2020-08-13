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


	String displayRequirementType = request.getParameter("displayRequirementType");
	if (displayRequirementType == null){
		displayRequirementType = "";
	}
	
	// lets get the list of distinct req types in this Project tree.
	// we use the releaseId = 0 flag when we want to run ProjectMetricsUtil at a project level.
	int releaseId = 0;
	String fromDate = request.getParameter("fromDate");
	if (fromDate == null ){fromDate = "";}
	String toDate = request.getParameter("toDate");
	if (toDate == null ){toDate = "";}
	
	ArrayList reqTypes = ReleaseMetricsUtil.getRequirementTypesForReleaseOrProject(releaseId,project.getProjectId(), displayRequirementType);
	
	String allRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForReleaseOrProject2(releaseId,project, "numOfRequirements", displayRequirementType, fromDate, toDate);
	String draftRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForReleaseOrProject2(releaseId,project, "numOfDraftRequirements", displayRequirementType, fromDate, toDate);
	String inApprovalWorkflowRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForReleaseOrProject2(releaseId,project, "numOfInApprovalWorkflowRequirements", displayRequirementType, fromDate, toDate);
	String rejectedRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForReleaseOrProject2(releaseId,project, "numOfRejectedRequirements", displayRequirementType, fromDate, toDate);
	String approvedRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForReleaseOrProject2(releaseId,project, "numOfApprovedRequirements", displayRequirementType, fromDate, toDate);
	
	String danglingRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForReleaseOrProject2(releaseId,project, "numOfDanglingRequirements", displayRequirementType, fromDate, toDate);
	String orphanRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForReleaseOrProject2(releaseId,project, "numOfOrphanRequirements", displayRequirementType, fromDate, toDate);
	
	
	String suspectUpRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForReleaseOrProject2(releaseId,project, "numOfSuspectUpstreamRequirements", displayRequirementType, fromDate, toDate);
	String suspectDownRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForReleaseOrProject2(releaseId,project, "numOfSuspectDownstreamRequirements", displayRequirementType, fromDate, toDate);
	
	
	

	String completedRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForReleaseOrProject2(releaseId,project, "numOfCompletedRequirements", displayRequirementType, fromDate, toDate);
	String incompleteRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForReleaseOrProject2(releaseId,project, "numOfIncompleteRequirements", displayRequirementType, fromDate, toDate);
	
	
	
	String testPendingRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForReleaseOrProject2(releaseId,project, "numOfTestPendingRequirements", displayRequirementType, fromDate, toDate);
	String testPassRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForReleaseOrProject2(releaseId,project, "numOfTestPassRequirements", displayRequirementType, fromDate, toDate);
	String testFailRequirementsDataString = ReleaseMetricsUtil.getTrendDataStringForReleaseOrProject2(releaseId,project, "numOfTestFailRequirements", displayRequirementType, fromDate, toDate);
	
	
%>
	<html>
   <head>
      <title>Google Charts Tutorial</title>
      <script type = "text/javascript" src = "https://www.gstatic.com/charts/loader.js">
      </script>
      <script type = "text/javascript">
         google.charts.load('current', {packages: ['corechart']});     
      </script>
      
      <script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	
      
      	<!--  Bootstratp  JS and CSS files -->

 	 <script src="/GloreeJava2/js/jquery-3.1.1.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap-tour-standalone.min.js"></script>
	
	 <link href="/GloreeJava2/css/bootstrap.min.css" rel="stylesheet" media="screen">
	<link href="/GloreeJava2/css/bootstrap-tour-standalone.min.css" rel="stylesheet">
      
      
   </head>
   
   <body>
   
   
				
			<a href='/GloreeJava2/servlet/ReportAction?action=exportProjectTrendData'
						target='_blank'  title='Excel file of Project Trends Data'>
				Download Trends<img src="/GloreeJava2/images/ExportExcel16.gif"  border="0">
			</a> 
			<%
			
			
			%>
			
	<br><br>
	
	
	
		
				 
      <div id = "allRequirements" style = "width: 1000px; height: 400px; margin: 0 auto"  ></div>
       
      <div id = "draftRequirements" style = " width: 1000px; height: 400px; margin: 0 auto"  ></div>
       <div id = "inApprovalWorkFlowRequirements" style = " width: 1000px; height: 400px; margin: 0 auto"  ></div>
	   <div id = "rejectedRequirements" style = " width: 1000px; height: 400px; margin: 0 auto"  ></div>
	   <div id = "approvedRequirements" style = "  width: 1000px; height: 400px; margin: 0 auto"  ></div>
	   
     
     	<div id = "orphanRequirements" style = "  width: 1000px; height: 400px; margin: 0 auto"  ></div>
	   <div id = "danglingRequirements" style = " width: 1000px; height: 400px; margin: 0 auto"  ></div>
	   
	   <div id = "suspectUpRequirements" style = "  width: 1000px; height: 400px; margin: 0 auto"  ></div>
	   <div id = "suspectDownRequirements" style =  " width: 1000px; height: 400px; margin: 0 auto"  ></div>
	   
	   
	   <div id = "completedRequirements" style = " width: 1000px; height: 400px; margin: 0 auto"  ></div>
	   <div id = "incompleteRequirements" style = " width: 1000px; height: 400px; margin: 0 auto"  ></div>
	   
	   <div id = "testPendingRequirements" style = " width: 1000px; height: 400px; margin: 0 auto"  ></div>
	   <div id = "testFailRequirements" style = " width: 1000px; height: 400px; margin: 0 auto"  ></div>
	   <div id = "testPassRequirements" style = " width: 1000px; height: 400px; margin: 0 auto"  ></div>
	   
	
	
	
	
	   
      <script language = "JavaScript">
         function drawChart() {
            var allData = google.visualization.arrayToDataTable(<%=allRequirementsDataString%>);
            var allOptions = {title: 'Total Requirements', isStacked:true, width:1000, height:400 };  
            var allChart = new google.visualization.ColumnChart(document.getElementById('allRequirements'));
            allChart.draw(allData, allOptions);
            
                      
            var draftData = google.visualization.arrayToDataTable(<%=draftRequirementsDataString%>);
            var draftOptions = {title: 'Draft Requirements', isStacked:true, width:1000, height:400};  
            var draftChart = new google.visualization.ColumnChart(document.getElementById('draftRequirements'));
            draftChart.draw(draftData, draftOptions);
            
            
            var inApprovalWorkFlowData = google.visualization.arrayToDataTable(<%=inApprovalWorkflowRequirementsDataString%>);
            var inApprovalWorkFlowOptions = {title: 'In Approval WorkFlow Requirements', isStacked:true, width:1000, height:400 };  
            var inApprovalWorkFlowChart = new google.visualization.ColumnChart(document.getElementById('inApprovalWorkFlowRequirements'));
            inApprovalWorkFlowChart.draw(inApprovalWorkFlowData, inApprovalWorkFlowOptions);
            
            var rejectedData = google.visualization.arrayToDataTable(<%=rejectedRequirementsDataString%>);
            var rejectedOptions = {title: 'Rejected Requirements', isStacked:true, width:1000, height:400 };  
            var rejectedChart = new google.visualization.ColumnChart(document.getElementById('rejectedRequirements'));
            rejectedChart.draw(rejectedData, rejectedOptions);
            
            var approvedData = google.visualization.arrayToDataTable(<%=approvedRequirementsDataString%>);
            var approvedOptions = {title: 'Approved Requirements', isStacked:true, width:1000, height:400 };  
            var approvedChart = new google.visualization.ColumnChart(document.getElementById('approvedRequirements'));
            approvedChart.draw(approvedData, approvedOptions);
            
            
            var orphanData = google.visualization.arrayToDataTable(<%=orphanRequirementsDataString%>);
            var orphanOptions = {title: 'Orphan Requirements', isStacked:true, width:1000, height:400 };  
            var orphanChart = new google.visualization.ColumnChart(document.getElementById('orphanRequirements'));
            orphanChart.draw(orphanData, orphanOptions);
            
            
            
            var danglingData = google.visualization.arrayToDataTable(<%=danglingRequirementsDataString%>);
            var danglingOptions = {title: 'Dangling Requirements', isStacked:true, width:1000, height:400 };  
            var danglingChart = new google.visualization.ColumnChart(document.getElementById('danglingRequirements'));
            danglingChart.draw(danglingData, danglingOptions);
            
            var suspectUpData = google.visualization.arrayToDataTable(<%=suspectUpRequirementsDataString%>);
            var suspectUpOptions = {title: 'Suspect Upstream Requirements', isStacked:true, width:1000, height:400 };  
            var suspectUpChart = new google.visualization.ColumnChart(document.getElementById('suspectUpRequirements'));
            suspectUpChart.draw(suspectUpData, suspectUpOptions);
            
            

            var suspectDownData = google.visualization.arrayToDataTable(<%=suspectDownRequirementsDataString%>);
            var suspectDownOptions = {title: 'Suspect Downstream Requirements', isStacked:true, width:1000, height:400 };  
            var suspectDownChart = new google.visualization.ColumnChart(document.getElementById('suspectDownRequirements'));
            suspectDownChart.draw(suspectDownData, suspectDownOptions);
            
            var completedData = google.visualization.arrayToDataTable(<%=completedRequirementsDataString%>);
            var completedOptions = {title: 'Completed Requirements', isStacked:true, width:1000, height:400 };  
            var completedChart = new google.visualization.ColumnChart(document.getElementById('completedRequirements'));
            completedChart.draw(completedData, completedOptions);
            
            
            var incompleteRequirementsData = google.visualization.arrayToDataTable(<%=incompleteRequirementsDataString%>);
            var incompleteRequirementsOptions = {title: 'Incomplete Requirements', isStacked:true, width:1000, height:400 };  
            var incompleteRequirementsChart = new google.visualization.ColumnChart(document.getElementById('incompleteRequirements'));
            incompleteRequirementsChart.draw(incompleteRequirementsData, incompleteRequirementsOptions);
            
            var testPendingData = google.visualization.arrayToDataTable(<%=testPendingRequirementsDataString%>);
            var testPendingOptions = {title: 'Test Pending Requirements', isStacked:true, width:1000, height:400 };  
            var testPendingChart = new google.visualization.ColumnChart(document.getElementById('testPendingRequirements'));
            testPendingChart.draw(testPendingData, testPendingOptions);
            
            
            var testFailData = google.visualization.arrayToDataTable(<%=testFailRequirementsDataString%>);
            var testFailOptions = {title: 'Test Failed Requirements', isStacked:true, width:1000, height:400 };  
            var testFailChart = new google.visualization.ColumnChart(document.getElementById('testFailRequirements'));
            testFailChart.draw(testFailData, testFailOptions);
            
            
            var testPassData = google.visualization.arrayToDataTable(<%=testPassRequirementsDataString%>);
            var testPassOptions = {title: 'Test Passed Requirements', isStacked:true, width:1000, height:400 };  
            var testPassChart = new google.visualization.ColumnChart(document.getElementById('testPassRequirements'));
            testPassChart.draw(testPassData, testPassOptions);
            
            
            
            
         }
         google.charts.setOnLoadCallback(drawChart);
      </script>
   </body>
</html>



<%}%>