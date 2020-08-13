<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String aPExplorerIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((aPExplorerIsLoggedIn  == null) || (aPExplorerIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean aPXIsMember = false;
	SecurityProfile aPXSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (aPXSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		aPXIsMember = true;
	}
	
%>

<% if(aPXIsMember){ %>
	
	<div id='requirementTypes' class='invisibleLevel1Box'>
	<table class='paddedTable' width='100%'>
		<tr>
			<td id='projectCoreTD'
				style="background: white; cursor: pointer;" 
				onmouseover="this.style.background='lightblue';" 
				onmouseout="this.style.background='white';" 
				onClick="displayProjectCoreInfo()"
			> 
				<span class='normalText'><font color='blue'>
				<b><img src="/GloreeJava2/images/project16.png" border="0">
				Project Core Information</b>
				</font>
				</span>
			</td>
		</tr>				
		<tr>
			<td
				id='connectProjectsTD'
				style="background: white; cursor: pointer;" 
				onmouseover="this.style.background='lightblue';" 
				onmouseout="this.style.background='white';" 
				onClick="connectProjectsForm()";
			> 
				<span class='normalText'><font color='blue'>
				<b><img src="/GloreeJava2/images/link.png" border="0">
				Connect Projects</b>
				</font></span>
			</td>
		</tr>				
		<tr>
			<td
				style="background: white; cursor: pointer;" 
				onmouseover="this.style.background='lightblue';" 
				onmouseout="this.style.background='white';"
				onClick="integrationMenuForm()";
			>
			 	<span class='normalText'><font color='blue'>
				<b><img src="/GloreeJava2/images/menu_dropdown.png" border="0">
				Integration Menu</b>
				</font></span>
			</td>
		</tr>				

		<tr>
			<td
				style="background: white; cursor: pointer;" 
				onmouseover="this.style.background='lightblue';" 
				onmouseout="this.style.background='white';" 
				onClick="mapDefectStatusGroupForm()"
			> 
				<span class='normalText'><font color='blue'>
				<b><img src="/GloreeJava2/images/log16.png" border="0">
				Defect Status Grouping </b>
				</font></span>
			</td>
		</tr>	
		<tr><td><br><br></td></tr>

		<tr>
			<td>
				<input type='button'
					id ='mangeObjectTypesButton'
					value='Manage Object Types'
					class='btn btn-sm btn-primary'  
					style="width:130px;" 
					onClick='
						document.getElementById("objectTypesDiv").style.display="block";
						document.getElementById("RolesDiv").style.display="none";
						document.getElementById("FormsDiv").style.display="none";
					'
				> 
				
			</td>
		</tr>	
		<tr>
			<td>
				<input type='button'
				id = 'manageRolesButton'
				value='Manage Roles'
				class='btn btn-sm btn-primary'
				style="width:130px;" 
				onClick='
					document.getElementById("objectTypesDiv").style.display="none";
					document.getElementById("RolesDiv").style.display="block";
					document.getElementById("FormsDiv").style.display="none";
				'
				>	 
				
			</td>
		</tr>	
		
		<tr>
			<td>
				<input type='button'
					value='Manage Users'
					class='btn btn-sm btn-primary'
					style="width:130px;" 
					onClick='
						manageUsers();
					'
				> 
				
			</td>
		</tr>	
		

		<tr>
			<td>
				<input type='button'
					value='Manage Web Forms'
					class='btn btn-sm btn-primary'
					style="width:130px;" 
					onClick='
						document.getElementById("objectTypesDiv").style.display="none";
						document.getElementById("RolesDiv").style.display="none";
						document.getElementById("FormsDiv").style.display="block";
					'
				> 
				
			</td>
		</tr>	


		<tr><td><br><br></td></tr>
		<tr>
			<td>
				<div id='objectTypesDiv' style='display:none' class='alert alert-success'>
					<table>
						<tr>
							<td> 
							<span class='sectionHeadingText'>
							<b>Object Types</b>
							</span>
							</td>
						</tr>		
						<tr>
							<td> 
							<span class='normal'>
								<input type='button'  id='createNewObjectTypeButton' value='Create New Object Type' style='width:200px' class='btn btn-sm btn-primary'
								onClick='createRequirementTypeForm();'>
							</span>
							</td>
						</tr>		
						
						<%
						ArrayList requirementTypes = project.getMyRequirementTypes(); 	
						Iterator i = requirementTypes.iterator();
						
						while (i.hasNext()){
							RequirementType rt = (RequirementType) i.next();
							%>
							<tr>
								<td
									style="background: white; cursor: pointer;" 
									onmouseover="this.style.background='lightblue';" 
									onmouseout="this.style.background='white';" 
									onClick="displayRequirementType(<%=rt.getRequirementTypeId()%>)"
								> 
									<span class='normalText' title="<%=rt.getRequirementTypeDescription()%> ">
									<font color='blue'>
										<img src="/GloreeJava2/images/puzzle16.gif" border="0">
									 	<%=rt.getRequirementTypeShortName()%>  : <%=rt.getRequirementTypeName()%>
									</font>
									 </span>
								</td> 
							</tr>
						<%}%>
					</table>				
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<div id='RolesDiv' style='display:none' class='alert alert-success'>
					<table>
						<tr>
							<td> 
							<span class='sectionHeadingText'>
							<b>Roles</b>
							</span>
							</td>
						</tr>		
						<tr>
							<td> 
							<span class='normal'>
								<input type='button' value='Create New Role' style='width:200px' class='btn btn-sm btn-primary'
								onClick='createRoleForm();'>
							</span>
							</td>
						</tr>								
						<%
						ArrayList roles = RoleUtil.getRoles(project.getProjectId()); 
						Iterator r = roles.iterator();
					
						while (r.hasNext()){
							Role role = (Role) r.next();
							%>
							<tr>
								<td
									style="background: white; cursor: pointer;" 
									onmouseover="this.style.background='lightblue';" 
									onmouseout="this.style.background='white';" 
									onClick="displayRole(<%=role.getRoleId() %>)"
								> 
									<span class='normalText' title="<%=role.getRoleDescription() %> ">
									<font color='blue'>
										<img src="/GloreeJava2/images/role16.png" border="0">
									 	<%=role.getRoleName() %>
									 </font>
									 </span>
								</td> 
							</tr>
						<%}%>
					</table>				
				</div>
			</td>
		</tr>
	
		<tr>
			<td>
				<div id='FormsDiv' style='display:none' class='alert alert-success'>
					<table>
						<tr>
							<td> 
							<span class='sectionHeadingText'>
							<b>Web Forms</b>
							</span>
							</td>
						</tr>		
						<tr>
							<td> 
							<span class='normal'>
								<input type='button' value='Create New WebForm' style='width:200px' class='btn btn-sm btn-primary'
								onClick='createWebFormForm();'>
							</span>
							</td>
						</tr>								
						<%
						ArrayList webforms  = ProjectUtil.getWebForms(project.getProjectId()); 
						Iterator w = webforms.iterator();
					
						while (w.hasNext()){
							WebForm webform = (WebForm) w.next();
							%>
							<tr>
								<td
									style="background: white; cursor: pointer;" 
									onmouseover="this.style.background='lightblue';" 
									onmouseout="this.style.background='white';" 
									onClick="displayWebForm(<%=webform.getId() %>)"
								> 
									<span class='normalText' title="<%=webform.getDescription() %> ">
									<font color='blue'>
										<img src="/GloreeJava2/images/form.png" border="0">
									 	<%=webform.getName() %>
									 </font>
									 </span>	
								</td> 
							</tr>
						<%}%>
					</table>				
				</div>
			</td>
		</tr>
		
		
	</table>
	</div>
<%}%>