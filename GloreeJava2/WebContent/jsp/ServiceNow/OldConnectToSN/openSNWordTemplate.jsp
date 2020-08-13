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


	//authorization
	// since we need authorization as well as authenticaiton we will use the 
	// security profile object.
	SecurityProfile userProfileSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	if (userProfileSecurityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%
	}
	

	userProfileSecurityProfile = new SecurityProfile(userProfileSecurityProfile.getUser().getUserId(),this.getServletContext().getInitParameter("databaseType"));
	session.setAttribute("securityProfile",userProfileSecurityProfile );
	 
	User user = userProfileSecurityProfile.getUser();
	
	
	
	
	int sNTemplateId = Integer.parseInt(request.getParameter("sNTemplateId"));
	WordTemplateSN wordTemplateSN = new WordTemplateSN(sNTemplateId); 
	
	String instance = (String) session.getAttribute("instance");
	String snuser = (String) session.getAttribute("snuser");
	String snpwd = (String) session.getAttribute("snpwd");
	
	
	
	/////////////////////////////////////////////////////////////
	//
	//  Get All Projects this user has access to 
	//
	/////////////////////////////////////////////////////////////

	String urlString = "https://ven02634.service-now.com/api/x_tracl_tracecloud/getprojects";
	String projects = WordTemplateUtilSN.makeAPICallToSN(urlString, snuser, snpwd);
	JSONArray projectArray  = new JSONArray();
	try{
		JSONObject projectsJSON = new JSONObject(projects);	
		JSONObject projectsJSONObject =  projectsJSON.getJSONObject("result") ;
		projectArray = projectsJSONObject.getJSONArray("projects");
	}
	catch (Exception e){
		e.printStackTrace();
	}

	
	/////////////////////////////////////////////////////////////
	//
	//  Get All Attributes in this project
	//
	/////////////////////////////////////////////////////////////
	urlString =  "https://ven02634.service-now.com/api/x_tracl_tracecloud/projectattributes?projectId=" 
	         + wordTemplateSN.getSNProjectId();
	String attributes = WordTemplateUtilSN.makeAPICallToSN(urlString, snuser, snpwd);
	JSONArray attributesArray  = new JSONArray();
	try{
		JSONObject attributesJSON = new JSONObject(attributes);	
		attributesArray =  attributesJSON.getJSONArray("result") ;
	}
	catch (Exception e){
		e.printStackTrace();
	}

	 
// check that the templateId belongs to one of the project this user isa  member of.

boolean validTemplate = false;
String projectName = "";

for (int i = 0; i < projectArray.length(); i++) {
	
    JSONObject project = projectArray.getJSONObject(i);
    String projectId = project.getString("sys_id");
 	
    if (projectId.equals(wordTemplateSN.getSNProjectId())){
    	validTemplate = true;
    	projectName = project.getString("name");
    
    }
}


if (!validTemplate ){
%>
	<div class='alert alert-danger'>
		You do not have access to this template
	</div>
<%	
}
else {
%>

<form method="post" id="generateWordReportForm" action="/GloreeJava2/servlet/WordTemplateAction">
		<input type='hidden' name='action' value='generateReportSN'>
		<input type='hidden' name='templateId' value='<%=wordTemplateSN.getTemplateId() %>'>
<table class='table'>
	<tr>
		<td> Target Project </td>
		<td> <%=projectName %></td>
			
		</td>
	</tr>
	
	<tr>
		<td> 
			Name
		</td>
	 
		<td>
			<%=wordTemplateSN.getTemplateName() %>
			
		</td>	
		
		<tr> 
			<td>
				Template Description 
			</td>
			<td >
				<%=wordTemplateSN.getTemplateDescription() %>
			</td>		
		</tr>
		
		<tr>
			<td> 
				<span class='headingText'> Display Attributes 
				<br> (Ctrl+Click to Select / Un Select)</span> 
			</td>
			<td >
				<span class='normalText'>
				<select  SIZE='19' name='displayAttributes' id='displayAttributes' multiple>
					<option value='name' selected> Name</option>
					<option value='description' selected>  Description</option>
					
					
					<option value='owner' > Owner</option>
					
					<option value='approvers' > Approvers</option>
					<option value='approvalDate' > Approval Date</option>
					<option value='approvalStatus' > Approval Status</option>
					
					<option value='fileAttachments' > File Attachments</option>
					
					<option value='traceTo' > Traces To (Up)</option>
					<option value='traceFrom' > Traces From (Down)</option>
					
					<option value='customAttributes' > All Custom Attributes</option>
					<option value='customAttributesNonEmpty' selected> Only Custom Attributes with a value</option>
					
					
					<%
					for (int i = 0; i < attributesArray.length(); i++) {
					    String attribute = attributesArray.getString(i);
					   %>
						<option value='attribute=<%=attribute %>' > Attribute - <%=attribute %></option>
					   <%
					}

					
					%>
					
				</select>
				</span>
			</td>		
		</tr>
		
		<tr>
				<td colspan=2 align="center">
					<span class='normalText'>
					
					<input type='submit' class='btn btn-xs btn-primary' 
						value='Generate Report' >
					</input>
				</span>
				</td>
			</tr> 	
	</tr>
	
	
	


</table>

</form>
<%} %>















  