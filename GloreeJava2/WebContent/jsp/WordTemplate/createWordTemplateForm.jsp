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
			<div id = 'createWordTemplateDiv' class='level1Box'>
				<table class='paddedTable' width='100%'>
					<tr>
						<td align='left' bgcolor='#99CCFF'>				
							<span class='subSectionHeadingText'>
							Create A Word Document
							</span>
						</td>
					</tr>									
					<tr>
						<td>
						<div id='noPermissionsDiv' class='alert alert-success'>
						You do not have privileges to Create Requirements in this Folder. Hence you won't be 
						able to Create A Word Document. Please contact your project administrator.
						</div>
						</td>
					</tr>
				</table>
			</div>
	<%
		return;
		}
		int maxWordTemplateSize = Integer.parseInt(this.getServletContext().getInitParameter("maxWordTemplateSize"));
		
	%>

	
	<div id='createWordTemplateFormDiv' class='level1Box'>
		<form method="post"  ENCTYPE='multipart/form-data' id="createWordTemplateForm" 
		action="/GloreeJava2/servlet/CreateWordTemplateAction">
			
			<input type="hidden" name="folderId" value="<%=request.getParameter("folderId")%>" > 
		<table class='paddedTable' width='100%'>
			<tr>
				<td align='left' bgcolor='#99CCFF'>				
					<span class='subSectionHeadingText'>
					Create A Word Document
					</span>
				</td>
				<td align='right' bgcolor='#99CCFF'>
					<div style='float:right'>
						<span title='Creating Requirements from Word Documents Reference Manual'>
						<a href='/GloreeJava2/documentation/help/createRequirementsFromWord.htm' target='_blank'>
						<img src="/GloreeJava2/images/page.png"   border="0">
						</a>	
						</span>
						&nbsp;&nbsp;
						<span title='Regenerating Word Documents with Embedded Requirements Reference Manual'>
						<a href='/GloreeJava2/documentation/help/regeneratingWordDocumentsWithEmbeddedRequirements.htm' target='_blank'>
						<img src="/GloreeJava2/images/page.png"   border="0">
						</a>	
						</span>
						&nbsp;&nbsp;
						<span title='Creating Requirements from Word Documents Help Video'>
						<a target="_blank" href="http://www.youtube.com/watch?v=8Go1026a0-k">
						<img height="20" border="0" src="/GloreeJava2/images/television.png"/>
						</a>
						</span>
						&nbsp;&nbsp;
						<span title='Regenerating Word Documents with embedded Requirements Help Video'>
						<a target="_blank" href="http://www.youtube.com/watch?v=Qt1NuQAtkcs">
						<img height="20" border="0" src="/GloreeJava2/images/television.png"/>
						</a>
						</span>
					</div>
				</td>
			</tr>							

			<tr>
				<td colspan="2">
					<div >
					
						<br>
						<span class='normalText'>
						This is a powerful feature of TraceCloud using which you can <br>


							&nbsp;a) Create requirements from word documents like BRD, MPR, PRD etc.
						</span>
						<a href='#' onClick='
							document.getElementById("createRequirementsFromDocsDiv").style.display="block";
							'>More Info ...</a>
						<div id='createRequirementsFromDocsDiv' style='display:none'>
							<div style='float:right'>
							<a href='#' onClick='
								document.getElementById("createRequirementsFromDocsDiv").style.display="none";
								'>Close</a></div>
							<span class='normalText'>
							<br>&nbsp;&nbsp;&nbsp;There are multiple ways to create Requirements from a word document.
							<br><br>&nbsp;&nbsp;&nbsp;&nbsp;Step 1: Create a word document in the following format
								<br>
								<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1)      You may have word table with one requirement per row. You may upload Requirement Name, Description, TraceTo and TraceFrom using this style.</li>
								<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2)      One requirement per table. You can download a shell for this format and upload all requirement details including custom attributes.</li>
								<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3)      You may identify text as requirement by marking paragraphs as hyperlinks</li>
								<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4)      You may identify text as requirement by having key words, shall, will, could etc.</li>
								<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;5)      You may identify text as requirement by using particular word styles.</li>
							<br><br></br>&nbsp;&nbsp;&nbsp;&nbsp;Step 2: Upload the word document and click on create requirements and pick the style or format that you have used for your requirements.
							</span>
						</div>
						<br>

							<span class='normalText'>
							&nbsp;b) Embed Requirements, or TraceCloud Reports in any of your word documents like BRD, PRD, Test Plan etc...
							</span>
						<a href='#' onClick='
							document.getElementById("embedRequirementsFromDocsDiv").style.display="block";
							'>More Info ...</a>
						<div id='embedRequirementsFromDocsDiv' style='display:none'>
							<div style='float:right'>
							<a href='#' onClick='
								document.getElementById("embedRequirementsFromDocsDiv").style.display="none";
								'>Close</a></div>
							<span class='normalText'>
							<br>&nbsp;&nbsp;&nbsp;There are multiple ways to embed Requirements in to a word document.
							<br><br>&nbsp;&nbsp;&nbsp;&nbsp;Step 1: Identify requirements you wish to embed in the following format
								<br>
								<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1)      To embed REL-1 requirement name, description or any other attribute just type REL-1:Name and make it into a hyperlink. To embed a 
								<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; requirement including all attributes enter the requirement tag as a hyperlink REL-1. This will result in a table with requirement attributes listed as rows
								<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2)      To embed a TraceCloud Report enter the report id and make it into a hyperlink REPORTID-2026
								<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3)      To embed multiple requirements you may have a comma separated list e.g. TR-1,TR-2,TR-3 or a range of requirements in the following format TR-1..TR-5
								
							<br><br>&nbsp;&nbsp;&nbsp;&nbsp;Step 2:  Upload the word document. This will result in the word document getting saved as a template in the system

							<br><br>&nbsp;&nbsp;&nbsp;&nbsp;Step 3:  Click on Generate Word Report to generate a report with the latest requirement collateral embedded in the word doc.
							</span>
						</div>
						<br>

					</div>	
				</td> 
			</tr>
			<tr>
				<td colspan='2'> &nbsp; </td>
			</tr>
			<tr>
				<td colspan='2'>
					<span class='normalText'>
						To generate a structured empty word template that can be used by your users
						to create Requirements, click 
						<a href='#' onclick='generateEmptyWordTemplateForm()'> here </a> 
					</span> 
				</td>
			</tr>
			<tr>
				<td colspan='2'> &nbsp; </td>
			</tr>
			<tr>
				<td><span class='headingText'>Visibility</span></td>
				<td>
					<span class='headingText'>
					<select 
					style='height:25px;'
					name='templateVisibility' id='templateVisibility'>
						<option value='public' SELECTED> Public </option>
						<option value='private'> Private </option>
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
						size="50" maxlength="100"> 
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
				<td> 
					<span class='headingText'>
					Word File <font color='red'>(Size < <%=maxWordTemplateSize/(1024*1024) %> MB)</font>
					<sup><span style="color: #ff0000;">*</span></sup> 
					</span>
				</td>
			 
				<td>
					<span class='normalText'>
					<INPUT TYPE='file' NAME='importFile'>
					</span>
				</td>
				
			</tr>	
			<tr>
				<td colspan=2 align="center">
					<span class='normalText'>
					<input type="button" name="Upload File" id="uploadWordTemplateButton" value="Upload File"
					onclick='createWordTemplate()' >
					<input type='button' name='Cancel' value='Cancel' 
					onClick='document.getElementById("contentCenterB").innerHTML= "";'>
				</span>
				</td>
			</tr> 	
		</table>
		
		</form>
	</div>
	
<%}%>