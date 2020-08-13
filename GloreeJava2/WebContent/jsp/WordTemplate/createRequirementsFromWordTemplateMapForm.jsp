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
			
	%>
	
	<div id='editWordTemplateDiv'>
		<form method="post" id="createRequirementsFromWordTemplateMapForm" action="">
		<table class='paddedTable' width='100%'>
			<tr>
				<td align="left" colspan='2'>
				<span class='normalText'>				
				Create Requirements From Word Document
				</span>
				</td> 
			</tr>	
			<tr><td width='200'>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			
			<tr> 
				<td width='200'>
					<span class='headingText'> 
						Locate Requirements By
					</span>
					<sup>
					<span style="color: #ff0000;">*</span>
					</sup> 
				</td>
				<td> 
					<span class='normalText'>
						<select 
						style='height:25px;'
						onchange ='displayLocationOptions(<%=templateId%>, <%=folderId%>);' 
						id='locateProcess' name='locateProcess'>
							<option value='tables-multipleReqs'> Requirement Tables (Multiple Reqs per Table) </option>
							<option value='tables-singleReq'> Requirement Tables (Single Req per Table) </option>
							<option value='paragraphs'> Locate Paragraphs </option>						
							<option value='styles'> Word Styles </option>
							<option value='styles-updatable'> Word Styles (Create & Update Requirements)</option>
							<option value='hyperlinks'> Hyperlinks </option>							
						</select>	
					</span>
				</td>
			</tr>
			<tr>
				<td colspan='2'>
					<div id='locationOptionsDiv' style='display:none'>
						<table>    
							<tr> 
							<td> 
								<span class='normalText'>   
									Please enter the words you want to search for.
								</span>  
							</td> 
							<td>   
								<span class='normalText'>   				
									<input type="text" name="paragraphSearch"
									id="paragraphSearch" size="100" value="Shall, Will, Must, May, Can, Should"> 
								</span> 	
							</td>
							</tr>   
					   </table> 					
					</div>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="left">
					<span class='normalText'>
					<input type="button"  name="TagRequirements" value="Identify Requirements" class='btn btn-sm btn-success'
					id = "identifyRequirementsInWordTemplateButton" 
					onClick="createRequirementsFromWordTemplateConfirm(<%=folderId%>,<%=templateId%>) ">
					<input type="button"   name="Cancel" value="Cancel" class='btn btn-sm btn-danger'
					onClick='document.getElementById("templateCoreDiv").innerHTML= ""' >
					</span>
				</td>
			</tr> 	
			
		</table>
		
		</form>
	</div>
<%}%>