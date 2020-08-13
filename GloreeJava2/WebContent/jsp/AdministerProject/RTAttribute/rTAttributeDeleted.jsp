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

<div id='rTAttributeDeletedtDiv' class='level1Box'>
<table  class='paddedTable'  >
	<tr>
		<td align="left" >
			<div id='rTAttributeDeletedMessage' class='alert alert-success'>
			<span class='normalText'>
			Congratulations. Your <img src="/GloreeJava2/images/rubyAttribute16.png" border="0"> 
			Attribute has been successfully deleted from the system.
			</span>
			</div>
		</td>
	</tr>
</table>
</div>