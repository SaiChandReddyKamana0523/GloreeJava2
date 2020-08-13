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
	Project project= (Project) session.getAttribute("project");
	User user = securityProfile.getUser();

	String dashboardType  = "ProjectDashboard";
	String ownedBy = user.getEmailId();
	
	int myDanglingRequirements = ProjectUtil.getDanglingRequriementsCount(user.getEmailId(), project.getProjectId(), dashboardType, ownedBy);
	if (myDanglingRequirements == 0){
	%>
		<span style="color:gray; font-size:15pt"><b><%=myDanglingRequirements %></b></SPAN> <br></br>Dangling
	<%}
		else {%>
		<a href='#' onclick='myDanglingReqsDetailsForAProject()'>
		<span style="color:red; font-size:15pt"><b><%=myDanglingRequirements %></b></SPAN> <br></br>Dangling</a>
	<%} %>

<%
}
catch (Exception e) {

}

%>
 





  