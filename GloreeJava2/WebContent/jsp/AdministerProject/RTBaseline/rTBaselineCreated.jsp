<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String rTACIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((rTACIsLoggedIn == null) || (rTACIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }%>


<div id='rTBaselineCreatedDiv' class='level1Box'>
<table  class='paddedTable' align="center" >
	<tr>
		<td>
			<div id='rTBaselineCreatedMessage' class='alert alert-success'>
			The Baseline has 
			been successfully created  
			</div>
		</td>
	</tr>
</table>
</div>
