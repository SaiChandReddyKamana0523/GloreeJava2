<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String oPRightNewIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((oPRightNewIsLoggedIn == null) || (oPRightNewIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }%>
