<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String eUFIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((eUFIsLoggedIn   == null) || (eUFIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is an admin of this project.
	boolean isAdmin = false;
	SecurityProfile aASecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	String powerUserSettings = project.getPowerUserSettings();
	if (
			(aASecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())
			||
			(
			(aASecurityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
				&&
			(powerUserSettings.contains("Manage Roles"))
			)
		)){
		isAdmin  = true;
	}
	
	boolean isMember = false;
	if (aASecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
%>

<!--  A user needs to be an admin or member to be able to view this page.-->
<% if (isMember) { %>
	<%@ page import="java.util.*" %>
	<%@ page import="javax.servlet.http.HttpSession"  %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<%
		int roleId = Integer.parseInt(request.getParameter("roleId"));
		ArrayList users = RoleUtil.getAllUsersInRole(roleId, databaseType);
		
	%>
	
	<div id='editUsersFormDiv' class='level1Box'>
	
	<form method="post" id="editUsersForm" action="">
	<input type='hidden' name='roleId' value='<%=roleId%>'>	
	<table class='paddedTable' width='100%'>
		<tr>
			<td colspan="7" align='left'  >
				<span class='subSectionHeadingText'>
				<b>Edit Users in this Role.</b> 
				</span>
			</td> 
		</tr>
		<tr> 
			<td class='tableHeader'>
				<span class='sectionHeadingText'>
				<input type='checkbox' name='editUsers' value='-1'
				onclick= 'if (this.form.editUsers[0].checked == true){
					for (i=1; i<this.form.editUsers.length;i++){
						this.form.editUsers[i].checked = true;
					}
				}
				else {
					for (i=1; i<this.form.editUsers.length;i++){
						this.form.editUsers[i].checked = false;
					}
				
				}'
				>
				  </span>
			</td>
			<td class='tableHeader'>
				<span class='sectionHeadingText'> User Name</span>
			</td>
			<td class='tableHeader'>
				<span class='sectionHeadingText'> Email </span>
			</td>	
		</tr>
		<%
		String tdClass = "normalTableCell";
		int j = 0;
	    if (users != null){
	    	Iterator i = users.iterator();
	    	while ( i.hasNext() ) {
	    		User u = (User) i.next();
				j++;
				if ((j % 2) == 0 ) {
					tdClass  = "normalTableCell";
				}
				else{
					tdClass  = "altTableCell";
				}
		
		%>
			<tr>
				<td class='<%=tdClass %>'>				
					<input type='checkbox' name='editUsers' value='<%=u.getUserId()%>'> 
				</td>
				<td class='<%=tdClass %>'>				
					<span class='normalText'>
					<img src="/GloreeJava2/images/user16.png" border="0"> 
					<%=u.getFirstName() %>&nbsp; <%=u.getLastName()%> 
					</span>
				</td>
				<td class='<%=tdClass %>'>				
					<span class='normalText'> 
					<%=u.getEmailId()%> 
					</span>
				</td>
			</tr>
			
		<%
			}
		}
		%>
		
			<tr>
			<td colspan='3' align="left">
				<span class='normalText'>
						<%
						String disabled = "disabled='DISABLED'";
						if (isAdmin){
							disabled = "";
						}
						%>
						<input type="button" '<%=disabled%>' class="btn btn-sm btn-danger" name="Delete Users" 
						value="Delete Users" onClick="deleteUsersFunction(this.form)">
						
						<input type="button" '<%=disabled%>' name="Move Users to -->" 
						value="Move Users To --> " onClick="moveUsersFunction(this.form)">
						
						<select '<%=disabled%>' name='moveRole'>
							<%
							// lets get a list of roles in this project.
							ArrayList roles = RoleUtil.getRoles(project.getProjectId()); 
							Iterator r = roles.iterator();
							while (r.hasNext()){
								Role role = (Role) r.next();
								if (role.getRoleId() != roleId){
							%> 
									<option name='<%=role.getRoleId()%>' value='<%=role.getRoleId()%>' >
									<%=role.getRoleName()%>
									</option>
								
							<%	}
							}%>
						</select>
					</span>						
				</td>
		</tr> 	
	</table>
	
	</form>
	</div>
<%}%>