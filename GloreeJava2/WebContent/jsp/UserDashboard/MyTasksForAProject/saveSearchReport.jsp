<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*"%>
<%@ page import="java.sql.Date"%>
<%@ page import="com.gloree.beans.*"%>
<%@ page import="com.gloree.utils.*"%>



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



<%
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	if (securityProfile == null) {
%>
<jsp:forward page="/jsp/WebSite/startPage.jsp" />
<%
	}

	try {


		int targetRequirementTypeId  = 0;
		int targetFolderId = 0;
		
		try {
			targetRequirementTypeId = Integer.parseInt(request.getParameter("targetRequirementTypeId"));
		}
		catch (Exception e){}
		
		try {
			targetFolderId = Integer.parseInt(request.getParameter("targetFolderId"));
		}
		catch (Exception e){}


		String databaseType = this.getServletContext().getInitParameter("databaseType");
		Project project = (Project) session.getAttribute("project");
		User user = securityProfile.getUser();
		String searchString = request.getParameter("searchString");
		String searchProjects = "";
		
		
		// make a string of the report definition
		String reportDefinition = "";
		reportDefinition += "targetRequirementTypeId:#:" + targetRequirementTypeId;
		reportDefinition += ":##:" +  "targetFolderId:#:" + targetFolderId;
		reportDefinition += ":##:" +  "searchString:#:" + searchString;
		
		// lets get a random folder in this project so store this report in.
		ArrayList folders = project.getMyFolders();
		Folder folder = (Folder) folders.get(0);
		String reportVisibility  = "public";
		String reportTitle = request.getParameter("reporTitle");
		String reportType = "stringSearch";
		int traceTreeDepth = 1;
	
		
		
		ReportUtil.saveReport(project.getProjectId(), folder.getFolderId(), reportVisibility, reportTitle, reportTitle, reportType, 
				traceTreeDepth, reportDefinition, user.getEmailId(), databaseType);
		
		/*
		ArrayList requirements = new ArrayList();
		// if this was a 'save request', lets save the report and then display the report saved message. 
		if ((searchString != null) && !(searchString.equals(""))) {
			requirements = ReportUtil.getglobalSearchReport(securityProfile, searchProjects, searchString,
					securityProfile.getUser(), databaseType, project.getProjectId(), targetRequirementTypeId, targetFolderId);

		}
		*/
%>


<div class='alert alert-info'>
	Congratulations. Your report has been saved
</div>


<%
	} catch (Exception e) {

	}
%>




