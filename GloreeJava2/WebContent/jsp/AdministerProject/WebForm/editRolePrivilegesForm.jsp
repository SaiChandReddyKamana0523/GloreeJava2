<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String eRPFIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((eRPFIsLoggedIn  == null) || (eRPFIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	// authorization : Only admins can do this.
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is an admin of this project.
	boolean isAdmin = false;
	String powerUserSettings = project.getPowerUserSettings();
	
	SecurityProfile eRPFSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (
			(eRPFSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))
			||
			(
				eRPFSecurityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
				&&
				(powerUserSettings.contains("Manage Roles")
			)
		)
		{
		isAdmin = true;
	}
%>


<!--  Only admins can edit role privileges  -->

<%if (isAdmin){ 

	String updatedRolePrivsMessage = "";
	String updatedRolePrivs = (String) request.getAttribute("updatedRolePrivs");
	if ((updatedRolePrivs != null) && (updatedRolePrivs.equals("true")))	{
		updatedRolePrivsMessage = "" +
		" 	<div id='userPrompt' class='alert alert-success' align='left'> " +
		"	<span class='normalText'>Your changes have been applied. </span>" + 
		"	</div> " ; 
	}
%>
	<%@ page import="java.util.*" %>
	<%@ page import="javax.servlet.http.HttpSession"  %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<%
		int roleId = Integer.parseInt(request.getParameter("roleId"));
		ArrayList rolePrivs = RoleUtil.getRolePrivs(project.getProjectId(), roleId, databaseType);
		Iterator i = rolePrivs.iterator();
	%>
	
	<div id='editRolePrivilegesDiv' class='level1Box'>
	
	<form method="post" id="editRolePrivilegesForm" action="">
	<input type='hidden' name='roleId' value='<%=roleId%>'>	
	<table class='paddedTable'>
		<tr>
			<td colspan="8"  align='left'  >
				<span class='subSectionHeadingText'>
				<b>Edit Permissions for Users in Role</b>
				</span>
			</td> 
		</tr>
		<tr>
			<td colspan="8">
				<%=updatedRolePrivsMessage %>
			</td> 
		</tr>
		<tr>
			<td class='tableHeader'>
				<span class='sectionHeadingText'> Folder Path </span>
			</td>
			<td class='tableHeader'>
				<span class='sectionHeadingText'> 
				<input type='checkbox' name='createRequirement' id='createRequirement' value='-1'
				onClick='selectDeselectAllRoles(this.form.createRequirement)'>
				Create Requirement </span>
			</td>	
			<td class='tableHeader'>
				<span class='sectionHeadingText'>
				<input type='checkbox' name='readRequirement' id='readRequirement'  value='-1'
				onClick='selectDeselectAllRoles(this.form.readRequirement)'>
				 Read Requirement </span>
			</td>
			<td class='tableHeader'>
				<span class='sectionHeadingText'>
				<input type='checkbox' name='updateRequirement' id='updateRequirement'   value='-1'
				onClick='selectDeselectAllRoles(this.form.updateRequirement)'
				>
				 Update Requirement </span>
			</td>
			<td class='tableHeader'>
				<span class='sectionHeadingText'>
				<input type='checkbox' name='deleteRequirement' id='deleteRequirement'   value='-1'
				onClick='selectDeselectAllRoles(this.form.deleteRequirement)'>
				 Delete Requirement </span>
			</td>
			<td class='tableHeader'>
				<span class='sectionHeadingText'>
				<input type='checkbox' name='traceRequirement' id='traceRequirement'   value='-1'
				onClick='selectDeselectAllRoles(this.form.traceRequirement)'>
				 Trace Requirement </span>
			</td>

			<td class='tableHeader'>
				<span class='sectionHeadingText'>
				<input type='checkbox' name='approveRequirement' id='approveRequirement'  value='-1'
				onClick='selectDeselectAllRoles(this.form.approveRequirement)'>
				 Approve Requirement </span>
			</td>
			
			<td class='tableHeader' width='200'>
				<div>
					<div style='float:left'>
						<input type='checkbox' name='updateAllAttributes' id='updateAllAttributes'  value='-1'
						onClick='selectDeselectAllUpdatableAttributes(this.form)'>
						
						
						<span class='sectionHeadingText'>
						 Updatable Attributes </span>
					
					</div>
					<div style='float:right'>
						<div id='expandAllUpdatableAttributesDiv' style='display:block'>
							<a href='#' onClick='expandAllUpdatableAttributes();'> &nbsp;(+)&nbsp; </a>
						</div>
						<div id='collapseAllUpdatableAttributesDiv' style='display:none'>
							<a href='#' onClick='collapseAllUpdatableAttributes();'> &nbsp;(-)&nbsp; </a>
						</div>
					
					</div>
				</div>
			</td>			
			
		</tr>
		<%
		String tdClass = "normalTableCell";
		int j = 0;
		String folderString = "";
		while ( i.hasNext() ) {
			j++;
			if ((j % 2) == 0 ) {
				tdClass  = "normalTableCell";
			}
			else{
				tdClass  = "altTableCell";
			}
		
			RolePriv rolePriv = (RolePriv) i.next();
			folderString += rolePriv.getFolderId() + "#";
		%>
			<tr>
				<td class='<%=tdClass %>'>
					<input type='checkbox' id='horizontalSelectDeselect<%=j%>' 
					onClick='
					if (this.form.horizontalSelectDeselect<%=j%>.checked == true){
						this.form.createRequirement[<%=j%>].checked=true;
						this.form.readRequirement[<%=j%>].checked=true;
						this.form.updateRequirement[<%=j%>].checked=true;
						this.form.deleteRequirement[<%=j%>].checked=true;
						this.form.traceRequirement[<%=j%>].checked=true;
						this.form.approveRequirement[<%=j%>].checked=true;
						selectAllUpdateAttributes(<%=rolePriv.getFolderId()%>);
					}
					else {
						this.form.createRequirement[<%=j%>].checked=false;
						this.form.readRequirement[<%=j%>].checked=false;
						this.form.updateRequirement[<%=j%>].checked=false;
						this.form.deleteRequirement[<%=j%>].checked=false;
						this.form.traceRequirement[<%=j%>].checked=false;
						this.form.approveRequirement[<%=j%>].checked=false;
						deSelectAllUpdateAttributes(<%=rolePriv.getFolderId()%>);
					}
					'></input>
					<img src='/GloreeJava2/images/folder.png' border='0'>
					<span class='headingText'>
					<%=rolePriv.getFolderPath() %>
					</span>
				</td>
				<td class='<%=tdClass %>'>				
					<% if (rolePriv.getCreateRequirement() == 0){  %>
						<input type='checkbox' name='createRequirement' id='createRequirement' value='<%=rolePriv.getFolderId()%>'> 
					<%}
					else{
					%>
						<input type='checkbox' name='createRequirement' id='createRequirement' CHECKED='yes' value='<%=rolePriv.getFolderId()%>'>
					<%} %>
				</td>
				<td class='<%=tdClass %>'>				
					<% if (rolePriv.getReadRequirement() == 0){%>
						<input type='checkbox' name='readRequirement'  id='readRequirement'  value='<%=rolePriv.getFolderId()%>'> 
					<%}
					else{
					%>
						<input type='checkbox' name='readRequirement' id='readRequirement'  CHECKED='yes' value='<%=rolePriv.getFolderId()%>'>
					<%} %>
				</td>
				<td class='<%=tdClass %>'>				
					<% if (rolePriv.getUpdateRequirement() == 0){  %>
						<input type='checkbox' name='updateRequirement' id='updateRequirement'  value='<%=rolePriv.getFolderId()%>'> 
					<%}
					else{
					%>
						<input type='checkbox' name='updateRequirement' id='updateRequirement'  CHECKED='yes' value='<%=rolePriv.getFolderId()%>'>
					<%} %>
				</td>
				<td class='<%=tdClass %>'>				
					<% if (rolePriv.getDeleteRequirement() == 0){  %>
						<input type='checkbox' name='deleteRequirement' id='deleteRequirement'  value='<%=rolePriv.getFolderId()%>'> 
					<%}
					else{
					%>
						<input type='checkbox' name='deleteRequirement' id='deleteRequirement'  CHECKED='yes' value='<%=rolePriv.getFolderId()%>'>
					<%} %>
				</td> 
				<td class='<%=tdClass %>'>				
					<% if (rolePriv.getTraceRequirement() == 0){  %>
						<input type='checkbox' name='traceRequirement' id='traceRequirement'  value='<%=rolePriv.getFolderId()%>'> 
					<%}
					else{
					%>
						<input type='checkbox' name='traceRequirement' id='traceRequirement'  CHECKED='yes' value='<%=rolePriv.getFolderId()%>'>
					<%} %>
				</td>

				<td class='<%=tdClass %>'>				
					<% if (rolePriv.getApproveRequirement() == 0){  %>
						<input type='checkbox' name='approveRequirement' id='approveRequirement'  value='<%=rolePriv.getFolderId()%>'> 
					<%}
					else{
					%>
						<input type='checkbox' name='approveRequirement' id='approveRequirement'  CHECKED='yes' value='<%=rolePriv.getFolderId()%>'>
					<%} %>
				</td>
				
				<td class='<%=tdClass %>'>	
					<div>	
						<div style='float:left'>
							<a href='#' onClick='
								document.getElementById("updateAttributesDiv<%=rolePriv.getFolderId()%>").style.display="block";
								document.getElementById("updateAttributesMenuOptionsDiv<%=rolePriv.getFolderId()%>").style.display="block";
								'>Attributes</a>
						</div>
						<div id='updateAttributesMenuOptionsDiv<%=rolePriv.getFolderId()%>' style='float:right; display:none'>
							&nbsp;&nbsp;
							<a href='#' onClick='selectAllUpdateAttributes(<%=rolePriv.getFolderId()%>);' >All</a> 
							&nbsp;
							<a href='#' onClick='deSelectAllUpdateAttributes(<%=rolePriv.getFolderId()%>);'>None</a>
							&nbsp;
							<a href='#' onClick='
								document.getElementById("updateAttributesDiv<%=rolePriv.getFolderId()%>").style.display="none";
								document.getElementById("updateAttributesMenuOptionsDiv<%=rolePriv.getFolderId()%>").style.display="none";
								'>X</a>
						</div>
					</div>
						<div id='updateAttributesDiv<%=rolePriv.getFolderId()%>' style='display:none'>
							
							<span class='normalText'> 
							<%
							Folder f = new Folder(rolePriv.getFolderId());
							RequirementType rT = new RequirementType(f.getRequirementTypeId());
							ArrayList rTAttributes = rT.getAllAttributesInRequirementType();
							Iterator a = rTAttributes.iterator();
							String updateAttributes = rolePriv.getUpdateAttributes();
							%>
							<select MULTIPLE SIZE='5' name='updateAttributes<%=rolePriv.getFolderId()%>'  id='updateAttributes<%=rolePriv.getFolderId()%>'>
								<%
								while (a.hasNext()){
									RTAttribute rTAttribute = (RTAttribute) a.next();
									if (updateAttributes.contains(":#:" + rTAttribute.getAttributeName() + ":#")){
										%>
										<option value='<%=rTAttribute.getAttributeName()%>' SELECTED ><%=rTAttribute.getAttributeName()%></option>
										<%
									}
									else {
										%>
										<option value='<%=rTAttribute.getAttributeName()%>'  ><%=rTAttribute.getAttributeName()%></option>
										<%	
									}
								
								}
								%>
								
							</select>
							</span>
			
					
						</div>
				</td>				
				
				
			</tr>
			
		<%}%>
		
			<tr>
			<td colspan='7' align="left">
				<span class='normalText'>
					<input type='hidden' name='folderIdString' id='folderIdString' value='<%=folderString%>'></input>
					<input type="button" name="Submit Changes" 
					value="Submit Changes" onClick="editRolePrivileges(this.form)">
					
				</span>
			</td>
		</tr> 	
	</table>
	
	</form>
	</div>
<%}%>