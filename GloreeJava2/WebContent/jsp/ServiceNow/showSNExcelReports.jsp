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

<form method="post"  action="/GloreeJava2/servlet/WordTemplateAction">
<input type='hidden' name='action' value='generateSNExcel'>
<table class='table'>
	<tr>
		<td> ServiceNow Project </td>
		<td>
			<span class='normalText'>
			<select id='sNProjectId' name='sNProjectId'>
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
			</span>
		</td>
	</tr>
	<tr>
		<td> 
		
		<span class='normalText'>
			<select id='reportType' name='reportType' >
				  <option value ='reqTypeName'>Requirement Type Name </option>
				  <option value ='folderPath'>Folder Path </option>
				  <option value ='reportId'>ReportId  </option>
		   </select>
			</span>
		 </td>
		<td>
			<span class='normalText'>
			<input type='text' id='sourceName' name='sourceName' size='50' 
			placeholder='Please enter the Req Type Name or Folder Path or Report Id'></input>
			</span>
		</td>
	</tr>
	
	
	
	
		
	<tr>
			<td colspan=2 align="center">
				<span class='normalText'>
				<input type='submit' class='btn btn-xs btn-primary' 
						value='Generate Excel' >
					</input>
				</input>
				</span>
			</td>
			
	</tr>
	
	
	

</table>
</form>

<%} %>
<br></br>
<div class='alert alert-info' style='display:none' id='getProjectSNTemplatesDiv'>
</div>
















  