<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->    
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="java.sql.Date" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<%
	//authorization
	// since we need authorization as well as authenticaiton we will use the 
	// security profile object.
	SecurityProfile userProjectsSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	if (userProjectsSecurityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%
	
	// authorization : since we are explicityly checking for and 
	// listing all the projects the user has access to
	// we are OK here. 
	}
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);
	if (!(userProjectsSecurityProfile.getRoles().contains("MemberInProject" + requirement.getProjectId()))){
		return;
	}
	
	// lets build the string that will be used to get the Google Org Chart.
	
	String orgChartBuilder = "";
	
	String reqFullTag = requirement.getRequirementFullTag();
	String reqName = requirement.getRequirementName();

	reqName = reqName.replace(",", " ");
	reqName = reqName.replace("'", " ");
	reqName = reqName.replace("\"", " ");
	reqName = reqName.replace(")", " ");
	reqName = reqName.replace("(", " ");
	reqName = reqName.replace("]", " ");
	reqName = reqName.replace("[", " ");
	reqName = reqName.replace("{", " ");
	reqName = reqName.replace("}", " ");
	reqName = reqName.replace("\n", " ");
	
	String reqLabel =  reqFullTag + " : " + reqName;
	if (reqLabel.length() > 100 ){
		reqLabel = reqLabel.substring(1,100) + "...";
	}
	
	
	
	// orgChartBuilder takes the data in this format ['name', 'parentname', 'tool tip'],
	
	String url = ProjectUtil.getURL(request,requirementId,"requirement");
/*	orgChartBuilder += "[{v:'"+ reqFullTag + "', f:'<div onMouseOver=\"loadLD(" 
		+ requirementId 
		+ ")\"><a target=\"_blank\" href=\"" + url + "\">"
		+ reqLabel 
		+ "</a><div style=\"display:none\" id=\"lDiv" + requirementId + "\"></div></div>'},'','"
		+ reqName +"'],";	
	*/

	orgChartBuilder = "[ " +
	                  "  ['"+ requirement.getRequirementFullTag() + "','','' ],";
	System.out.println ("srt adding at top level " + reqFullTag );
	
	
	int downStreamDepth = 3;
	int numberOfDownstreamReqsToShow = 500;
	ArrayList downStreamCIA = requirement.getDownStreamCIARequirements(userProjectsSecurityProfile, downStreamDepth, numberOfDownstreamReqsToShow, databaseType);
	
	
	// lets get the next level of projects.
	String level1ParentReqFullTag = reqFullTag;
	
	Iterator i = downStreamCIA.iterator();
	while (i.hasNext()){
		TraceTreeRow traceTreeRow = (TraceTreeRow) i.next();
		Requirement r = traceTreeRow.getRequirement();
	
		orgChartBuilder += " ['"+ traceTreeRow.getTraceFromFullTag() +"','"+  traceTreeRow.getTraceToFullTag() +"',''],";
					
	}
	
	// lets drop the last , 
		
	if (orgChartBuilder.contains(",")){
		orgChartBuilder = (String) orgChartBuilder.subSequence(0,orgChartBuilder.lastIndexOf(","));
	}	

	orgChartBuilder += "]";
	
	System.out.println("srt org chart builder is \n\n" + orgChartBuilder);
%>	
	
	
  <head>
  	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	<script type='text/javascript' src='https://www.google.com/jsapi'></script>
    <link rel="stylesheet" type="text/css" href="/GloreeJava2/css/common.css"> 
    <script type='text/javascript'>
      google.load('visualization', '1', {packages:['orgchart']});
      google.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Name');
        data.addColumn('string', 'Manager');
        data.addColumn('string', 'ToolTip');
        data.addRows(
          <%=orgChartBuilder%>
        );
        var chart = new google.visualization.OrgChart(document.getElementById('chart_div'));
        chart.draw(data, {allowHtml:true});
		
		

      }
    </script>
  </head>

  <body>
  		
    <div id='chart_div'></div>
  </body>
</html>
	
	


















  