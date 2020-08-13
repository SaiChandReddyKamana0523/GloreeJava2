<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String dRTFIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dRTFIsLoggedIn == null) || (dRTFIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<div id='createRequirementDiv' class='level1Box'>
<table  class='paddedTable' align="center" >
	<tr>
		<td>
			<div id='requirementTypeDeletedMessage' class='alert alert-success'>
			<span class='normalText'>
				Your Requirement Type Sequence number has been Reset. Your next object with start with 1 (Example : BR-1, FR-1, TC-1 etc...).
			</span> 
			</div>
		</td>
	</tr>
</table>
</div>
