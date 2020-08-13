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
	orgChartBuilder += "[{v:'"+ reqFullTag + "', f:'<div onMouseOver=\"loadLD(" 
		+ requirementId 
		+ ")\"><a target=\"_blank\" href=\"" + url + "\">"
		+ reqLabel 
		+ "</a><div style=\"display:none\" id=\"lDiv" + requirementId + "\"></div></div>'},'','"
		+ reqName +"'],";	
		
	System.out.println ("srt adding at top level " + reqFullTag );
		
	ArrayList childRequirementsLevel1 = requirement.getImmediateChildRequirements(databaseType);
	
	// lets get the next level of projects.
	String level1ParentReqFullTag = reqFullTag;
	Iterator level1Iterator = childRequirementsLevel1.iterator();
	while (level1Iterator.hasNext() ){
		Requirement requirementLevel1 = (Requirement) level1Iterator.next();
	
		String reqFullTagLevel1 = requirementLevel1.getRequirementFullTag();
		String reqNameLevel1 = requirementLevel1.getRequirementName();
		

		
		reqNameLevel1 = reqNameLevel1.replace(",", " ");
		reqNameLevel1 = reqNameLevel1.replace("'", " ");
		reqNameLevel1 = reqNameLevel1.replace("\"", " ");
		reqNameLevel1 = reqNameLevel1.replace(")", " ");
		reqNameLevel1 = reqNameLevel1.replace("(", " ");
		reqNameLevel1 = reqNameLevel1.replace("]", " ");
		reqNameLevel1 = reqNameLevel1.replace("[", " ");
		reqNameLevel1 = reqNameLevel1.replace("{", " ");
		reqNameLevel1 = reqNameLevel1.replace("}", " ");
		reqNameLevel1 = reqNameLevel1.replace("\n", " ");
		
		String reqLabel1 = reqFullTagLevel1 + " : " + reqNameLevel1;
		if (reqLabel1.length() > 100 ){
			reqLabel1 = reqLabel1.substring(1,100) + "...";
		}
		

		
		
		// lets add this project to the orgChartBuilder.
		
		String url1 = ProjectUtil.getURL(request,requirementLevel1.getRequirementId(),"requirement");
	
		orgChartBuilder += "[{v:'"+ reqFullTagLevel1 + "', f:'<div onMouseOver=\"loadLD(" 
			+ requirementLevel1.getRequirementId() 
			+ ")\"><a target=\"_blank\"  href=\"" + url1 + "\">"
			+ reqLabel1 
			+ "</a><div style=\"display:none\" id=\"lDiv" + requirementLevel1.getRequirementId() + "\"></div></div>'},'"
			+ level1ParentReqFullTag +"','"
			+ reqNameLevel1 +"'],";	

		
		// lets get the next level (Level 2) of projects
		String level2ParentReqFullTag = reqFullTagLevel1;
		ArrayList childRequirementsLevel2 = requirementLevel1.getImmediateChildRequirements(databaseType);
		Iterator level2Iterator  = childRequirementsLevel2.iterator();
		
		
		while (level2Iterator.hasNext()){
		
			Requirement requirementLevel2 = (Requirement) level2Iterator.next();
			
			String reqFullTagLevel2 = requirementLevel2.getRequirementFullTag();
			String reqNameLevel2 = requirementLevel2.getRequirementName();
		
			
			reqNameLevel2 = reqNameLevel2.replace(",", " ");
			reqNameLevel2 = reqNameLevel2.replace("'", " ");
			reqNameLevel2 = reqNameLevel2.replace("\"", " ");
			reqNameLevel2 = reqNameLevel2.replace(")", " ");
			reqNameLevel2 = reqNameLevel2.replace("(", " ");
			reqNameLevel2 = reqNameLevel2.replace("]", " ");
			reqNameLevel2 = reqNameLevel2.replace("[", " ");
			reqNameLevel2 = reqNameLevel2.replace("{", " ");
			reqNameLevel2 = reqNameLevel2.replace("}", " ");
			reqNameLevel2 = reqNameLevel2.replace("\n", " ");
			
			String reqLabel2 = reqFullTagLevel2 + " : " + reqNameLevel2;
			if (reqLabel2.length() > 100 ){
				reqLabel2 = reqLabel2.substring(1,100) + "...";
			}
			
			String url2 = ProjectUtil.getURL(request,requirementLevel2.getRequirementId(),"requirement");
			
			// lets add this project to the orgChartBuilder.
			orgChartBuilder += "[{v:'"+ reqFullTagLevel2 + "', f:'<div onMouseOver=\"loadLD(" 
					+ requirementLevel2.getRequirementId() 
					+ ")\"><a target=\"_blank\"  href=\"" + url2 + "\">"
					+ reqLabel2 
					+ "</a><div style=\"display:none\" id=\"lDiv" + requirementLevel2.getRequirementId() + "\"></div></div>'},'"
					+ level2ParentReqFullTag +"','"
					+ reqNameLevel2 +"'],";	


		
			
		}
	}
	
	// lets drop the last , 
		
	if (orgChartBuilder.contains(",")){
		orgChartBuilder = (String) orgChartBuilder.subSequence(0,orgChartBuilder.lastIndexOf(","));
	}	
	
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
        data.addRows([
          <%=orgChartBuilder%>
        ]);
        var chart = new google.visualization.OrgChart(document.getElementById('chart_div'));
        chart.draw(data, {allowHtml:true});
		
		

      }
    </script>
  </head>

  <body>
  		
    <div id='chart_div'></div>
  </body>
</html>
	
	


















  