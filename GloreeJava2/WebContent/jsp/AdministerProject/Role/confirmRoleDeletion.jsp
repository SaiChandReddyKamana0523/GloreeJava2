<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String confirmRoleDeletionIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((confirmRoleDeletionIsLoggedIn == null) || (confirmRoleDeletionIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }%>

<div id='editUsersFormDiv' class='level1Box'>
	<table>
		<tr>
			<td colspan="2">
			<div id='confirmRoleDeletionMessage' class='alert alert-success'>
			Congratulations! 
			<br>
			This <img src="/GloreeJava2/images/role16.png" border="0">role has been successfully deleted
			from this project.
			</div>
			</td> 
		</tr>
	</table>
</div>