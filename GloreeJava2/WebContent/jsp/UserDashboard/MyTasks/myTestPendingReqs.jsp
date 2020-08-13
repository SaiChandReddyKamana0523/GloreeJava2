<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->    
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="java.sql.Date" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>



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

	if (securityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%
	}

try {	
	int targetProjectId = Integer.parseInt(request.getParameter("targetProjectId"));
	User user = securityProfile.getUser();

	// if a specific projectId was sent in, lets make sure that the user is a valid member of this project.
	String ownedBy = "";
	if (targetProjectId > 0 ){
		if (!(securityProfile.getRoles().contains("MemberInProject" + targetProjectId))){
			// the user is not a member in this project. So lets get out.
			return;
		}
		ownedBy = request.getParameter("ownedBy");
	}
	else {
		// When targetProjectId == 0, that means all projects, lets force ownedby to be the user who is logged in.
		// ALL PROJECTS --> Forces owned by to be the logged user.
		ownedBy = user.getEmailId();		
	}
	String dashboardType= request.getParameter("dashboardType");
	
	// in case the values didn't come in, lets default them.
	if ((ownedBy == null ) || (ownedBy.equals(""))){
		ownedBy = user.getEmailId();
	}
	if ((dashboardType == null ) || (dashboardType.equals(""))){
		dashboardType = "ProjectDashboard";
	}
	
	

	
	
	int myTestPendingRequirements = ProjectUtil.getTestPendingRequriementsCount(user.getEmailId(),targetProjectId,  dashboardType, ownedBy);

	if (myTestPendingRequirements == 0 ) {
%>
	<span style="color:gray; font-size:15pt"><b><%=myTestPendingRequirements %></b>  </span><span class='normalText' style='color:gray'>  <br></br>Test Pending</span>
<%
	}
	else {
%>
	<a href='#' onclick='myTestPendingReqsDetails(<%=targetProjectId%>,"<%=dashboardType%>","<%=ownedBy%>")'>
	<span style="color:red; font-size:15pt"><b><%=myTestPendingRequirements %></b></span> <br></br>Test Pending</a>

<%		
	}
		
}

catch (Exception e) {

}

%>



  