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
	
	
	String instance = request.getParameter("instance");
	String snuser = request.getParameter("snuser");
	String snpwd = request.getParameter("snpwd");
	
	session.setAttribute("instance", instance);
	session.setAttribute("snuser", snuser);
	session.setAttribute("snpwd", snpwd);
	
	 String projects = "";
	
	 try {

         URL url = new URL(
                 "https://ven02634.service-now.com/api/x_tracl_tracecloud/getprojects");
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("GET");
         conn.setRequestProperty("Accept", "application/json");
         conn.setRequestProperty("Content-Type", "application/json");
         
         String userCredentials = snuser + ":" + snpwd;
         String basicAuth = "Basic " + new String(Base64Encoder.encode(userCredentials.getBytes()));

         conn.setRequestProperty("Authorization",
         		basicAuth);
         

         if (conn.getResponseCode() != 200) {
             throw new RuntimeException("Failed : HTTP error code : "
                     + conn.getResponseCode());
         }

         BufferedReader br = new BufferedReader(new InputStreamReader(
                 (conn.getInputStream())));

         String output; 
        
         while ((output = br.readLine()) != null) {
             projects += output;
         }

         conn.disconnect();

     } catch (Exception e) {

         e.printStackTrace();

     } 
    
	
	 if (projects.equals("")){
		 projects = "{}";
	 }
	 
	 JSONArray projectArray  = new JSONArray();
	try{
		JSONObject projectsJSON = new JSONObject(projects);	
		JSONObject projectsJSONObject =  projectsJSON.getJSONObject("result") ;
		projectArray = projectsJSONObject.getJSONArray("projects");
	}
	catch (Exception e){
		e.printStackTrace();
	}
%>


<form method="post"  ENCTYPE='multipart/form-data' id="createWordTemplateForm" 
		action="/GloreeJava2/servlet/CreateSNWordTemplateAction">
		
<%
if (projectArray.length() == 0 ){
%>
	<div class='alert alert-danger'>
		We were not able to find any ServiceNow projects associated with these credentials.
		Please check your ServiceNow instance URL, UserId  and Password
	</div>
<%	
}
else {
%>
<table class='table'>
	<tr>
		<td> Target Project </td>
		<td>
			<select id='sNProjectId'>
				<%
				for (int i = 0; i < projectArray.length(); i++) {
		        	
		            JSONObject project = projectArray.getJSONObject(i);
		            String projectId = project.getString("sys_id");
		            String projectName = project.getString("name");
		           	%>
		           		<option value ='<%=projectId%>'><%=projectName%></option>
		           	<% 
		           
				}
				
				%>
			</select>
		</td>
	</tr>
	
	<tr>
		<td> 
			<span class='headingText'>
			Word File <font color='red'>(Size < 10 MB)</font>
			<sup><span style="color: #ff0000;">*</span></sup> 
			</span>
		</td>
	 
		<td>
			
			<INPUT TYPE='file' NAME='importFile'></INPUT>
			
		</td>	
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
					<input type="text"  name="templateName" id="templateName" size="50" maxlength="100"> 
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
					<span class='normalText'>
					
					<input type="text"  style='visibility:hidden' 
					name="sNProjectIdHidden" id="sNProjectIdHidden" size="50" maxlength="100"> 
			
					<input type='button' class='btn btn-xs btn-primary' 
						value='Create Template' onClick='createNewSNTemplate(this.form)'>
					
					</input>
				</span>
				</td>
			</tr> 	
	</tr>
	
	
	


</table>
<%} %>
</form>















  