<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String rTADIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((rTADIsLoggedIn == null) || (rTADIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }%>

<div id='rTBaselineDeletedtDiv' class='level1Box'>
<table  class='paddedTable'  >
	<tr>
		<td align="left" >
			<div id='rTBaselineDeletedMessage' class='alert alert-success'>
			<span class='normalText'>
			Congratulations. Your  
			Baseline and its association with Requirements has been successfully deleted from the system.
			</span>
			</div>
		</td>
	</tr>
</table>
</div>