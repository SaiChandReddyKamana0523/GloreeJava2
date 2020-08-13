<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String oPFooterIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((oPFooterIsLoggedIn == null) || (oPFooterIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }%>


<span style="float:right;"> TraceCloud version 0.1 March 21st 2009 </span>