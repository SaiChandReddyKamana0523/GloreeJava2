<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="com.gloree.beans.*" %>

<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>
<%@ page import="java.util.*" %>




<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	int requirementId = Integer.parseInt((request.getParameter("requirementId")));

	// if we are here, that means the user was tryign to locate the requirement and it couldn't be found
	// 
	%>

<div class='alert alert-danger'>
	We are unable to find this object in the system. It looks like it was purged by another user. To get more details you can use the 'Project Change Log' feature. 
	<br>
	Click on 'Home' button, In the 'I Want to ' tab, look for a drop down called 'Use Tools'. You will find 'Project Change Log' there.
	
	


</div>
