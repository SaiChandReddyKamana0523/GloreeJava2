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


<div id='rTAttributeCreatedDiv' class='level1Box'>
<table  class='paddedTable' align="center" >
	<tr>
		<td>
			<div id='rTAttributeCreatedMessage' class='alert alert-success'>
			The <img src="/GloreeJava2/images/rubyAttribute16.png" border="0"> Attribute has 
			been successfully created  
			</div>
		</td>
	</tr>
</table>
</div>
