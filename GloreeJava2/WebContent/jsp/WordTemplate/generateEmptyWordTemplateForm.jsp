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
		
		ArrayList requirementTypes = project.getMyRequirementTypes(); 	
		Iterator i = requirementTypes.iterator();
		
		String rTOptionString = "";
		while (i.hasNext()){
			RequirementType rt = (RequirementType) i.next();
			rTOptionString += " <option value='"+ rt.getRequirementTypeId() +
			"'> "+ rt.getRequirementTypeName() +" </option>";
		}
	%>

	
	<div id='createEmptyWordTemplateFormDiv' class='level1Box'>
		<form method="post" action="/GloreeJava2/servlet/WordTemplateAction" id="generateEmptyWordTemplateForm">
			
		<input type='hidden' name='action' value='generateEmptyWordTemplate'>	 
		<table class='paddedTable' width='100%'>
			<tr>
				<td align='left' bgcolor='#99CCFF' colspan='2'>				
					<span class='subSectionHeadingText'>
					Generate An Empty Word Document
					</span>
				</td>
				
			</tr>							

			<tr>
				<td colspan="2">
					<span class='normalText'>
						<br>
						This form can be used to generate an empty word template with Requirements Templates
						that can be used to create Requirements and upload into the TraceCloud system.
					</span> 
						
				</td> 
			</tr>
			<tr>
				<td colspan='2'> &nbsp; </td>
			</tr>
			<tr>
				<td colspan='2'>
					<table>
						<tr>
							<td>
								<span class='sectionHeadingText'>
									Requirement Type
								</span>
							</td>
							<td>
								<span class='sectionHeadingText'>
									Expected number of Requirements
								</span>
							</td>
						</tr>
						<tr>
							<td>
								<span class='sectionHeadingText'>
									<select
									style='height:25px;'
									 name='requirementType1' id='requirementType1'>
									<%=rTOptionString%>
									</select>
								</span>
							</td>
							<td>
								<span class='sectionHeadingText'>
									<input type='text' name='numberOfRequirements1' id='numberOfRequirements1'>
								</span>
							</td>
						</tr>
						<tr>
							<td>
								<span class='sectionHeadingText'>
									<select name='requirementType2' id='requirementType2'>
									<%=rTOptionString%>
									</select>
								</span>
							</td>
							<td>
								<span class='sectionHeadingText'>
									<input type='text' name='numberOfRequirements2' id='numberOfRequirements2'>
								</span>
							</td>
						</tr>						
						<tr>
							<td>
								<span class='sectionHeadingText'>
									<select name='requirementType3' id='requirementType3'>
									<%=rTOptionString%>
									</select>
								</span>
							</td>
							<td>
								<span class='sectionHeadingText'>
									<input type='text' name='numberOfRequirements3' id='numberOfRequirements3'>
								</span>
							</td>
						</tr>						
						<tr>
							<td>
								<span class='sectionHeadingText'>
									<select name='requirementType4' id='requirementType4'>
									<%=rTOptionString%>
									</select>
								</span>
							</td>
							<td>
								<span class='sectionHeadingText'>
									<input type='text' name='numberOfRequirements4' id='numberOfRequirements4'>
								</span>
							</td>
						</tr>						
						<tr>
							<td>
								<span class='sectionHeadingText'>
									<select name='requirementType5' id='requirementType5'>
									<%=rTOptionString%>
									</select>
								</span>
							</td>
							<td>
								<span class='sectionHeadingText'>
									<input type='text' name='numberOfRequirements5' id='numberOfRequirements5'>
								</span>
							</td>
						</tr>	
											
					</table> 
				</td>
			</tr>
			
			
			<tr> 
				<td>
					<span class='headingText'> Report Format </span> 
				</td>
				<td >
					<span class='normalText'>
					<select name='reportFormat' id='reportFormat'>
						<option value='docx' 'SELECTED'> Word 2007 (.docx)</option>
						<option value='doc' > Word 97-2003 (.doc)</option>
					</select>
					</span>
				</td>		
			</tr>
			
			<tr>
				<td colspan=2 align="center">
					<span class='normalText'>
					<input type="submit" name="Generate Empty Word Template" id="uploadWordTemplateButton" value="Generate Empty Word Template">
					<input type='button' name='Cancel' value='Cancel' 
					onClick='document.getElementById("contentCenterB").innerHTML= "";'>
				</span>
				</td>
			</tr> 	
		</table>
		
		</form>
	</div>
	
<%}%>