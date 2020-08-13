<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->    
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="java.sql.Date" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>



<%@ page  import="java.io.IOException" %>

<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.net.HttpURLConnection" %>
<%@ page import="java.net.MalformedURLException" %>
<%@ page import="java.net.URL" %>

<%@ page import="org.json.JSONObject" %>
<%@ page import="org.json.JSONArray" %>

<%@ page import="com.oreilly.servlet.Base64Encoder" %>



<%

	


	
	String serverName = request.getServerName();
	SecurityProfile userProfileSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	if (userProfileSecurityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%
	
	}
	
	
	userProfileSecurityProfile = new SecurityProfile(userProfileSecurityProfile.getUser().getUserId(),this.getServletContext().getInitParameter("databaseType"));
	session.setAttribute("securityProfile",userProfileSecurityProfile );
	 
	User user = userProfileSecurityProfile.getUser();
	
	ArrayList<WordTemplateSN> wordTemplatesSN = ProjectUtil.getSNWordTemplatesCreatedBy(user.getEmailId());
	 
    
%>


		
<table class='table'>
	<tr>
		<td> Name </td>
		<td> Description</td>
		<td> Created By </td>
		<td></td>
		<td></td>
		<td></td>
	</tr>
	<%for (WordTemplateSN wTSN : wordTemplatesSN){ %>
		<tr>
		<td> <%=wTSN.getTemplateName() %> </td>
		<td> <%=wTSN.getTemplateDescription() %></td>
		<td> <%=wTSN.getCreatedBy() %> </td>
		<td> 
		
		
		<input type='button' class='btn btn-xs btn-primary' value='Run Template' onclick='openSNWordTemplate(<%=wTSN.getTemplateId() %>)'></input>
		
		</td>
		
		<td>
		
		<a class='btn btn-xs btn-primary' style='color:white'
		href="/GloreeJava2/servlet/WordTemplateAction?action=downloadTemplateSN&sNTemplateId=<%=wTSN.getTemplateId() %>"  
		target="_blank">
		        	 Download Template
		    </a>
		</td>
		<td>
		<input type='button' class='btn btn-xs btn-primary' value='Delete Template' onclick='downloadSNWordTemplate(<%=wTSN.getTemplateId() %>)'></input>
			
		 </td>
		</tr>
	<%} %>
	

</table>
<br></br>
<div class='alert alert-info' style='display:none' id='openSNWordTemplateDiv'>
</div>
</form>















  