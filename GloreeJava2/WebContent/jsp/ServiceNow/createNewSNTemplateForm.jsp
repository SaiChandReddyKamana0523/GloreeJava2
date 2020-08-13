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
%>


<form method="post"  ENCTYPE='multipart/form-data' id="createWordTemplateForm" 
		action="/GloreeJava2/servlet/CreateSNWordTemplateAction">
		<input type="hidden"  name="fileType" id="fileType" value="templateFile"> </input>
<table class='table'>
	
	<tr>
		<td> 
			<span class='headingText'>
			Word File <font color='red'>(Size < 10 MB)</font>
			<sup><span style="color: #ff0000;">*</span></sup> 
			</span>
		</td>
	 
		<td>
			
			<INPUT TYPE='file' NAME='importFile' id='importFile'></INPUT>
			
		</td>
	</tr>	
		<tr> 
			<td>
				<span class='headingText'> 
					Template Name
				</span>
				<sup>
				<span style="color: #ff0000;">*</span>
				</sup> 
			</td>
			<td> 
				<span class='normalText'>
					<input type="text"  name="templateName" id="templateName" size="50" maxlength="100"> </input>
				</span>
			</td>
		</tr>
		<tr> 
			<td>
				<span class='headingText'> Template Description </span>
				<sup><span style="color: #ff0000;">*</span></sup> 
			</td>
			<td colspan="3">
				<span class='normalText'>
				<textarea id="templateDescription" name="templateDescription" 
				rows="5" cols="80" ></textarea>
				</span>
			</td>		
		</tr>
		
		
		<tr>
			<td colspan=2 align="center">
				
				<input type='button' class='btn btn-xs btn-primary' 
					value='Create Template' onClick='createNewSNTemplate(this.form)'>
				
				</input>
			</td>
		</tr> 	
	</tr>
	
	
	


</table>

</form>















  