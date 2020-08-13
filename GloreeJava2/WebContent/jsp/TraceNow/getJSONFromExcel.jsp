
<%@page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>
	<% 
	
	String fileCode = request.getParameter("fileCode");
	String jsonString = RESTAPIUtil.getJSONFromExcel(fileCode);
	%><%=jsonString%>