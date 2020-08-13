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
	int projectId = Integer.parseInt(request.getParameter("projectId"));
	if (!(userProjectsSecurityProfile.getRoles().contains("MemberInProject" + projectId))){
		return;
	}
	
	// lets build the string that will be used to get the Google Org Chart.
	
	String orgChartBuilder = "";
	
		
	Project project = new Project(projectId, databaseType);
	String projectName = project.getProjectName();
	
	projectName = projectName.replace(",", " ");
	projectName = projectName.replace("'", " ");
	projectName = projectName.replace("\"", " ");
	projectName = projectName.replace(")", " ");
	projectName = projectName.replace("(", " ");
	projectName = projectName.replace("]", " ");
	projectName = projectName.replace("[", " ");
	projectName = projectName.replace("{", " ");
	projectName = projectName.replace("}", " ");
	
	projectName = projectName.replace("\n", " ");
	
	
	String projectDescription = project.getProjectDescription();
	projectDescription = "Owner : " + project.getProjectOwner() + "   Description: " + projectDescription;
	projectDescription = projectDescription.replace(",", " ");
	projectDescription = projectDescription.replace("'", " ");
	projectDescription = projectDescription.replace("\"", " ");
	projectDescription = projectDescription.replace(")", " ");
	projectDescription = projectDescription.replace("(", " ");
	projectDescription = projectDescription.replace("]", " ");
	projectDescription = projectDescription.replace("[", " ");
	projectDescription = projectDescription.replace("{", " ");
	projectDescription = projectDescription.replace("}", " ");
	
	projectDescription = projectDescription.replace("\n", " ");
	
	
	
	// orgChartBuilder takes the data in this format ['name', 'parentname', 'tool tip'],
	
	orgChartBuilder += "['"+ projectName + "','','"+ projectDescription +"'],";	
		
	System.out.println ("srt adding at top level " + projectName);
		
	ArrayList projectRelationsLight1 = project.getProjectRelationsLight(databaseType);
	ArrayList myProjects = userProjectsSecurityProfile.getProjectObjects();
	// lets remove any projects in projectRelations that aren't in my project's list . ie i can only see projects i am a member of
	
	Iterator pR = projectRelationsLight1.iterator();
	while(pR.hasNext()){
		ProjectRelationLight ProjectRelationLight = (ProjectRelationLight) pR.next();
		// is the related project in my list?
		boolean canRead = false;
		// loop through all myProjects
		Iterator mP = myProjects.iterator();
		while (mP.hasNext()){
			Project p = (Project) mP.next();
			if (p.getProjectId() == ProjectRelationLight.getRelatedProjectId()){
				canRead = true;
			}
		}
		
		if (canRead){
			// keep
			System.out.println("trying to keep " + ProjectRelationLight.getRelatedProjectName());
		}
		else {
			System.out.println("trying to remove " + ProjectRelationLight.getRelatedProjectName());
			projectRelationsLight1.remove(ProjectRelationLight);
		}
	}
	// lets get the next level of projects.
	String level1ParentProjectName = projectName;
	Iterator level1Iterator = projectRelationsLight1.iterator();
	while (level1Iterator.hasNext() ){
		ProjectRelationLight projectRelationLight1 = (ProjectRelationLight) level1Iterator.next();
		
		
		String level1ProjectName = projectRelationLight1.getRelatedProjectName();
		level1ProjectName = level1ProjectName.replace(",", " ");
		level1ProjectName = level1ProjectName.replace("'", " ");
		level1ProjectName = level1ProjectName.replace("\"", " ");
		level1ProjectName = level1ProjectName.replace(")", " ");
		level1ProjectName = level1ProjectName.replace("(", " ");
		level1ProjectName = level1ProjectName.replace("]", " ");
		level1ProjectName = level1ProjectName.replace("[", " ");
		level1ProjectName = level1ProjectName.replace("{", " ");
		level1ProjectName = level1ProjectName.replace("}", " ");
		
		level1ProjectName = level1ProjectName.replace("\n", " ");
		
		
		String level1ProjectDescription = projectRelationLight1.getRelatedProjectDescription();
		level1ProjectDescription = "Owner : " + projectRelationLight1.getRelatedProjectOwner() + "   Description: " + level1ProjectDescription;
		level1ProjectDescription = level1ProjectDescription.replace(",", " ");
		level1ProjectDescription = level1ProjectDescription.replace("'", " ");
		level1ProjectDescription = level1ProjectDescription.replace("\"", " ");
		level1ProjectDescription = level1ProjectDescription.replace(")", " ");
		level1ProjectDescription = level1ProjectDescription.replace("(", " ");
		level1ProjectDescription = level1ProjectDescription.replace("]", " ");
		level1ProjectDescription = level1ProjectDescription.replace("[", " ");
		level1ProjectDescription = level1ProjectDescription.replace("{", " ");
		level1ProjectDescription = level1ProjectDescription.replace("}", " ");
		
		level1ProjectDescription = level1ProjectDescription.replace("\n", " ");
		
		
		// lets add this project to the orgChartBuilder.
		orgChartBuilder += "['"+ level1ProjectName + "','"+ level1ParentProjectName +"','"+ level1ProjectDescription +"'],";	
	System.out.println ("srt adding at  level1  " + level1ProjectName);
		
		
		// lets get the next level (Level 2) of projects
		String level2ParentProjectName = level1ProjectName;
		ArrayList projectRelationsLight2 = ProjectUtil.getProjectRelationsLight(projectRelationLight1.getRelatedProjectId() , databaseType);
		Iterator level2Iterator  = projectRelationsLight2.iterator();
		
		
		while (level2Iterator.hasNext()){
			
			ProjectRelationLight projectRelationLight2 = (ProjectRelationLight) level2Iterator.next();
			if (projectRelationLight2.getRelatedProjectId() == projectRelationLight1.getProjectId()){
				// Project 1 has relations, and for each of the relations, if we serach for relations, we are bound to come across Project 1. 
				// in these scenarios, we are skipping them.
				//continue;
			}
			
			String level2ProjectName = projectRelationLight2.getRelatedProjectName();
			level2ProjectName = level2ProjectName.replace(",", " ");
			level2ProjectName = level2ProjectName.replace("'", " ");
			level2ProjectName = level2ProjectName.replace("\"", " ");
			level2ProjectName = level2ProjectName.replace(")", " ");
			level2ProjectName = level2ProjectName.replace("(", " ");
			level2ProjectName = level2ProjectName.replace("]", " ");
			level2ProjectName = level2ProjectName.replace("[", " ");
			level2ProjectName = level2ProjectName.replace("{", " ");
			level2ProjectName = level2ProjectName.replace("}", " ");
			
			level2ProjectName = level2ProjectName.replace("\n", " ");
			
			
			String level2ProjectDescription = projectRelationLight2.getRelatedProjectDescription();
			level2ProjectDescription = "Owner : " + projectRelationLight2.getRelatedProjectOwner() + "   Description: " + level2ProjectDescription;
			
			
			level2ProjectDescription = level2ProjectDescription.replace(",", " ");
			level2ProjectDescription = level2ProjectDescription.replace("'", " ");
			level2ProjectDescription = level2ProjectDescription.replace("\"", " ");
			level2ProjectDescription = level2ProjectDescription.replace(")", " ");
			level2ProjectDescription = level2ProjectDescription.replace("(", " ");
			level2ProjectDescription = level2ProjectDescription.replace("]", " ");
			level2ProjectDescription = level2ProjectDescription.replace("[", " ");
			level2ProjectDescription = level2ProjectDescription.replace("{", " ");
			level2ProjectDescription = level2ProjectDescription.replace("}", " ");
			
			level2ProjectDescription = level2ProjectDescription.replace("\n", " ");
			
			// lets add this project to the orgChartBuilder.
				
			// we are putting a hack in place
			// google charts org chart has  bug , where a child can not report to more than one parent. 
			// since we can have that happen in our model, the only way we can solve it is by having every child name be a little differet (adding extra space) at end. 
			// this will make 1 child with 2 parents appear as 2 children with 1 parent each
			// the way we do is it by giving each child in level2 a unique name , by making the childname (parentname) be the child name.
			level2ProjectName = level2ProjectName + "(" + level2ParentProjectName + ")" ;

			orgChartBuilder += "['"+ level2ProjectName + "','"+ level2ParentProjectName +"','"+ level2ProjectDescription +"'],";	
			System.out.println ("srt adding at  level 2 " + level2ProjectName);
		}
	}
	
	// lets drop the last , 
		
	if (orgChartBuilder.contains(",")){
		orgChartBuilder = (String) orgChartBuilder.subSequence(0,orgChartBuilder.lastIndexOf(","));
	}	
	
%>	
	
	
  <head>
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
	
	


















  