<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String editWordTemplateFormIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((editWordTemplateFormIsLoggedIn == null) || (editWordTemplateFormIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");

	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
%>
	
	
	<%
		// NOTE : this page can be called when some one tries to edit a wordTemplate.
		
		int templateId = Integer.parseInt(request.getParameter("templateId"));
		WordTemplate wordTemplate = new WordTemplate(templateId, databaseType);
		int folderId = Integer.parseInt(request.getParameter("folderId"));
			
		int maxWordTemplateSize = Integer.parseInt(this.getServletContext().getInitParameter("maxWordTemplateSize"));
		
	%>
	
	<div id='editWordTemplateDiv'>
		<form method="post"  ENCTYPE='multipart/form-data' id="editWordTemplateForm" 
		action="/GloreeJava2/servlet/EditWordTemplateAction">
			
		<input type="hidden" name="folderId" value="<%=folderId%>" > 
		<input type="hidden" name="templateId" value="<%=templateId%>" >	
		
		<table class='paddedTable' width='100%'>
			<tr>
				<td align="left" colspan='2'>
				<span class='normalText'>
				Edit Word Document
				</span>  
				</td> 
			</tr>
			<tr>
				<td><span class='headingText'>Visibility</span></td>
				<td>
					<span class='headingText'>
					<select
						style='height:25px;'
					 name='templateVisibility' id='templateVisibility'>
					<%if (wordTemplate.getTemplateVisibility().equals("public")){ %>					
						<option value='public' SELECTED> Public </option>
						<option value='private'> Private </option>
					<%}
					else {%>
						<option value='public' > Public </option>
						<option value='private' SELECTED> Private </option>					
					<%} %>
					</select>
					</span>
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
						<input type="text"  name="templateName" id="templateName" 
						size="50" maxlength="100" value='<%=wordTemplate.getTemplateName()%>'> 
					</span>
				</td>
			</tr>
			<tr> 
				<td>
					<span class='headingText'> Template Description </span>
					<sup><span style="color: #ff0000;">*</span></sup> 
				</td>
				<td >
					<span class='normalText'>
					<textarea id="templateDescription" name="templateDescription" 
					rows="10" cols="80" ><%=wordTemplate.getTemplateDescription()%></textarea>
					</span>
				</td>		
			</tr>
			<tr>
				<td> 
					<span class='headingText'>
					Replace Template with Word File <font color='red'>(Size < <%=maxWordTemplateSize/(1024*1024) %> MB)</font>
					</span>
				</td>
			 
				<td>
					<span class='normalText'>
					<INPUT TYPE='file' NAME='importFile'>
					</span>
				</td>
				
			</tr>			
			<tr>
				<td colspan="2" align="center">
					<span class='normalText'>
					<input type="button" name="Update Word Document" id="updateWordTemplateButton" class='btn btn-sm btn-success'
					value="Update Word Document" 
					onClick="editWordTemplate()">
					<input type="button" name="Cancel" value="Cancel" class='btn btn-sm btn-danger'
					onClick='document.getElementById("templateCoreDiv").innerHTML= ""'>
					</span>
				</td>
			</tr> 	
		 
		
		</table>
		
		</form>
	</div>
<%}%>