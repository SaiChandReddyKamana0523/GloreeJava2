<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String generateWordReportFormIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((generateWordReportFormIsLoggedIn == null) || (generateWordReportFormIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");

	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	ArrayList<String> attributeNames = ReportUtil.getAllAttributesInAProject(project);
	
	
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
	
	<div id='generateWordReportDiv' class='level1Box'>
		<form method="post" id="generateWordReportForm" action="/GloreeJava2/servlet/WordTemplateAction">
		<input type='hidden' name='action' value='generateReport'>
		<input type='hidden' name='folderId' value='<%=folderId%>'>
		<input type='hidden' name='templateId' value='<%=templateId%>'>
		
		<!--  this was originally a select box with doc, docx , pdf and html values. -->
		<!--we pulled the plug as docx , html were giving grief. See if you can rewrite it. -->
		<input type='hidden' name='reportFormat' value='doc'></input>
		
		<table class='paddedTable' width='100%'>
			<tr>
				<td colspan="2"> 
					<span class='normalText'> 
					Generate Word Report
					</span>
				</td> 
			</tr>
			<tr> 
				<td width='200'>
					<span class='headingText'> 
						Requirement Output Format
					</span>
					<sup>
					</sup> 
				</td>
				<td> 
					<span class='normalText'>
						<select  
						style='height:25px;'
						 name='requirementOutputFormat' id='requirementOutputFormat' 
						onchange ='
							if (document.getElementById("requirementOutputFormat").selectedIndex == 1){
								document.getElementById("reqPerTableRowMessage").style.display = "block";
							}
							else {
								document.getElementById("reqPerTableRowMessage").style.display = "none";
							}
						'>
						<option value='reqPerTable' SELECTED > One Requirements per Table</option>
						<option value='reqPerTableRow' > One Requirement per Row</option>
					</select>
					<div id='reqPerTableRowMessage' style='display:none'>
						
							Please note that if you print each Requirement in a single row, then this row can get quiet wide and is best viewed in a <b>'Web' or 'Outline' 
							or 'Draft' layout</b>. If your Word document is in 'Normal' layout, wide tables will look messy.
							<br><br>
							<b>Please Note that MS Word has a limitation, where your page width can not exceed 22 inches and you can not have more than 63 columns.</b>
							If you try to cross either of these limits, MS Word does not display your tables gracefully. 
						
					</div>
					</span>
				</td>
			</tr>	
			<tr> 
				<td width='200'>
					<span class='headingText'> 
						Template Name
					</span>
					<sup>
					</sup> 
				</td>
				<td> 
					<span class='normalText'>
						<%=wordTemplate.getTemplateName()%> 
					</span>
				</td>
			</tr>
			<tr> 
				<td>
					<span class='headingText'> Template Description </span> 
				</td>
				<td >
					<span class='normalText'>
					<%=wordTemplate.getTemplateDescription()%>
					</span>
				</td>		
			</tr>

				<td>
			<tr>
				<td> 
					<span class='headingText'> Display Attributes 
					<br> (Ctrl+Click to Select / Un Select)</span> 
				</td>
				<td >
					<span class='normalText'>
					<select  SIZE='19' name='displayAttributes' id='displayAttributes' multiple>
						<option value='approvers' > Approvers</option>
						<option value='approvalDate' > Approval Date</option>
						<option value='approvalStatus' > Approval Status</option>
						<option value='requirementBaselines' > Baselines</option>
						<option value='comments' > Comments</option>
						<option value='customAttributes' > All Custom Attributes</option>
						<option value='customAttributesNonEmpty' selected> Only Custom Attributes with a value</option>
						<option value='description' selected>  Description</option>
						<option value='externalURl' > External URL</option>
						<option value='fileAttachments' > File Attachments</option>
						<option value='folderPath' > Folder Path</option>
						<option value='name' selected> Name</option>
						<option value='owner' > Owner</option>
						<option value='pctComplete' > Percent Complete</option>
						<option value='priority' > Priority</option>
						<option value='type' > Requirement Type</option>
						<option value='testingStatus' > Testing Status</option>
						<option value='traceTo' > Trace To</option>
						<option value='traceFrom' > Trace From</option>
						<option value='version' > Version</option>
						<option value='url' > URL</option>
						<option value="">-----------------</option>
						<option value="">Custom Attributes</option>
						<option value="">-----------------</option>
												
						<%
						for (String attributeName : attributeNames){
						%>
							<option value="individuallySelectedCA_<%=attributeName%>"  ><%=attributeName%></option>
						<%
						}
						%>
						
					</select>
					</span>
				</td>		
			</tr>
			
						<tr>
				<td> 
					<span class='headingText'> Format as <b>Bold</b> 
					<br> (Curently works for only for Name & Description)</span> 
				</td>
				<td >
					<span class='normalText'>
					<input type='text' name='formatBoldAttribute' id='formatBoldAttribute' placeholder='name,description'>
				
					</span>
				</td>		
			</tr>			
			
			<tr>
				<td></td>
				<td >
					<span class='normalText'>
					<input type="button" id="generateReportButton"  value="Generate Report" class='btn btn-sm btn-success'
					onClick='
						document.getElementById("generateReportButton").disabled=true;
						this.form.submit();
					'>
					<input type="button" name="Cancel" value="Cancel" class='btn btn-sm btn-danger'
					onClick='document.getElementById("templateCoreDiv").innerHTML= ""'>
					</span>
				</td>
			</tr> 	
		 
		
		</table>
		
		</form>
	</div>
<%}%>