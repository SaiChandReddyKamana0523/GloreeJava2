<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>
<%@ page import="java.net.*" %>
<%@ page import="com.rational.clearquest.cqjni.*" %>
<%
String CQID = request.getParameter("CQID");

// Now connect to CQ, make the query call and get me the mastership url.
/*###########
# CQ Info #
###########
*/

String dbname = "DBD";
String dbsetname = "CQMS.DIEBOLD.DNA"; 


// START CQ STUFF

CQSession cQSession = new CQSession();
cQSession.UserLogon("TCAdmin","8888888",dbname,dbsetname);

CQQueryDef cqQueryDef = cQSession.BuildQuery("Public Queries/TraceCloud Queries/SCR- By ID");
		
CQResultSet cqResultSet = cQSession.BuildResultSet(cqQueryDef);
		
long count = cqResultSet.ExecuteAndCountRecords();


cqResultSet.MoveNext();


String redirectURL = cqResultSet.GetColumnValue(5);

response.sendRedirect(redirectURL);


%>

