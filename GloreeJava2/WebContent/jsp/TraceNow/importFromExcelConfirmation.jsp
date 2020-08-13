<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


	
	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userAccount.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userDashboard.js?v=20200630"></script>  
	

	
	<!--  Bootstratp  JS and CSS files -->
	 <script src="/GloreeJava2/js/jquery-3.1.1.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap.min.js"></script>
	 <link href="/GloreeJava2/css/bootstrap.min.css" rel="stylesheet" media="screen">
	
	
	<% 
	String message = (String) request.getAttribute("message");
	if (message == null ) {message = "";}
	if (message != ""){
		%>
		<%=message %>
		<%
	}
	else {
	
	String fileCode = (String) request.getAttribute("fileCode") ;
	
	%>
	
	<br>
	<input type='button'  class='btn btn-primary btn-lg' value='Generate JSON From Excel' onclick='generateJSONFromExcel("<%=fileCode%>")'>
	<br>		
		<h2>JSON from Excel </h2>
		<textarea id='jsonCode' rows='600' cols='200'>Your generated JSON will show up here</textarea>
	<%}%>