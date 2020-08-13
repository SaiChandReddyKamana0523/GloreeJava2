<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String iFEIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((iFEIsLoggedIn == null) || (iFEIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
		Folder folder = new Folder (Integer.parseInt(request.getParameter("folderId")));
		// if the user does not have 'Create Requirements' priv for this folder
		// we disable both the createRequirements and Import Excel buttons.
		// The rest of the buttons View Report, Create Sub folder, Delete Sub Folder , Edit folder
		// etc.. are available to regular users.
		// Note : Delete Sub folder is controlled by whether the user can delete the underlying 
		// requirements or not.		
		if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" 
				+ folder.getFolderId()))){
	%>
			<div id = 'importFromExcelDiv' class='level1Box'>
				<table class='paddedTable' width='100%'>
					<tr>
						<td  align='left' bgcolor='#99CCFF'>				
							<span class='subSectionHeadingText'>
							Import <%=folder.getRequirementTypeName() %>From Excel
							</span>
							&nbsp;&nbsp;&nbsp;
							<div style='float:right'>
								<span title='Updating Requirements in Bulk using Excel Help Video'>
								<a target="_blank" href="http://www.youtube.com/watch?v=zgYRS7VpzL8">
								<img height="20" border="0" src="/GloreeJava2/images/youTube.jpeg"/>
								</a>
								</span>
							</div>							
						</td>
					</tr>					
					<tr>
						<td>
						<div id='noPermissionsDiv' class='alert alert-success'>
						You do not have privileges to Create Requirements in this Folder. Hence you won't be 
						able to import requirements from Excel. Please contact your project administrator.
						</div>
						</td>
					</tr>
				</table>
			</div>
	<%
		return;
		}
		
		int maxImportExcelFileSize = Integer.parseInt(this.getServletContext().getInitParameter("maxImportExcelFileSize"));

	%>

	
	<div id='importFromExcelFormDiv' class='level1Box'>
		<form method="post"  ENCTYPE='multipart/form-data' id="importFromExcelForm" 
		action="/GloreeJava2/servlet/ImportFromExcelAction">
			<input type="hidden" name="action" value="uploadFile" >
			<input type="hidden" name="folderId" value="<%=request.getParameter("folderId")%>" >
			 
		<table class='paddedTable' width='100%'>
			<tr>
				<td colspan='2' align='left' bgcolor='#99CCFF'>				
					<span class='subSectionHeadingText'>
					Import <%=folder.getRequirementTypeName() %> From Excel
					</span>
					<div style='float:right'>
						<span title='Updating Requirements in Bulk using Excel Help Video'>
						<a target="_blank" href="http://www.youtube.com/watch?v=zgYRS7VpzL8">
						<img height="20" border="0" src="/GloreeJava2/images/television.png"/>
						</a>
						</span>
					</div>													
				</td>
			</tr>							

			<tr>
				<td colspan="2">
					<div >
					<span class='normalText'>
					<br>
					<font color='red'> For a successful upload, Please follow these guidelines : </font> 
					<br><br>&nbsp;&nbsp;&nbsp;&nbsp; Ensure that the 1st sheet of your Excel file has the data .
					<br><br>&nbsp;&nbsp;&nbsp;&nbsp; Ensure that your 1st row of your 1st sheet has column headers
					(This will come in handy for mapping column names to attributes).
					<br><br>&nbsp;&nbsp;&nbsp;&nbsp; Excel file is smaller than <%=maxImportExcelFileSize/(1024*1024) %> MB .
					<br><br>&nbsp;&nbsp;&nbsp;&nbsp; Only the first 20,000 rows are processed.
					<br><br>&nbsp;&nbsp;&nbsp;&nbsp; If you have a large excel file, you may want to break it up into smaller files.
					<br><br>&nbsp;&nbsp;&nbsp;&nbsp; We currently support ONLY .XLS files (Microsoft  pre 2003 format). If you have a .XLX file, please save it as a .XLS
					
					
					</span>
					</div>	
				</td> 
			</tr>
			<tr>
				<td colspan='2'> &nbsp; </td>
			</tr>
			<tr> 
				<td> 
					<span class='normalText'>
					Requirement Type
					</span>
				</td>
				<td>
					 <span class='headingText'>
					<%=folder.getRequirementTypeName() %>
					</span> 
				</td>
			</tr>
				
			<tr> 
				<td style='width:200px'> 
					<span class='headingText'>
					Upload Action <sup><span style="color: #ff0000;">*</span></sup> 
					</span>
				</td>
				<td>
					<span class='normalText'>
					<select name='uploadAction'>
						<option value='createNewRequirements'>Create New Requirements</option>
						<option value='updateExistingRequirements'>Update Existing Requirements</option>
					</select> 
					</span>
				</td>
			</tr>
			<tr>
				<td> 
					<span class='headingText'>
					Excel File <font color='red'>(Size < <%=maxImportExcelFileSize/(1024*1024) %>MB)</font>
					<sup><span style="color: #ff0000;">*</span></sup> 
					</span>
				</td>
			 
				<td>
					<span class='normalText'>
					<INPUT TYPE='file' NAME='importFile' class='btn btn-sm btn-primary'>
					</span>
				</td>
				
			</tr>	
			
			<tr>
				<td></td>
				<td >
					<span class='normalText'>
					<input type="button" name="Upload File"  id="uploadFileButton" value="Upload File" class='btn btn-sm btn-success' 
					onClick='uploadExcel(this.form);'>
					&nbsp;&nbsp;
					<input type='button' name='Cancel' value='Cancel' class='btn btn-sm btn-danger'
					onClick='document.getElementById("contentCenterB").innerHTML= "";'>
					</span>
				</td>
			</tr> 	
		</table>
		
		</form>
	</div>
	
<%}%>