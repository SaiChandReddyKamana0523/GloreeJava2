<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
<%
	// authentication only
	String dRTIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dRTIsLoggedIn == null) || (dRTIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	
	SecurityProfile dRTSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	
%>

<%if (dRTSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){ 
	
	String action = request.getParameter("action");
	if (action==null){action = "";}
	if (action.equals("setRequirementOwnersInProject")){
		String userEmailId = request.getParameter("userEmailId");
		String addRemoveAction = request.getParameter("addRemoveAction");
		
		project.setCanNotBeOwnersInProject(userEmailId, addRemoveAction );
	}

	String canNotBeOwners = project.getCanNotBeOwnersInProject();

	SortedMap<String, ArrayList> usersAndRoles = project.getUsersAndRoles();
	


%>

	
	
	<table  class='table' >
	<tr>
				<td align='left' bgcolor='#99CCFF'>
					User
				</td>
				<td align='left' bgcolor='#99CCFF'>
					Email
				</td>
				<td align='left' bgcolor='#99CCFF'>
					Company
				</td>
				<td align='left' bgcolor='#99CCFF' style='width:400px'>
					Role
					
				</td>
				<td align='left' bgcolor='#99CCFF'>
					Can be an Owner
					
				</td>
			</tr>
		<%
		for (String userString:usersAndRoles.keySet()){
			
			ArrayList<String> roleStrings = usersAndRoles.get(userString);
			String[] userStringArray = userString.split(":#:");
			String userName = userStringArray[0];
			String userEmailId = userStringArray[1];
			String company = userStringArray[2];
			
			%>
			
			<tr>
				<td align='left' >
					<%=userName %>
				</td>
				<td align='left' >
					<%=userEmailId %>
				</td>
				<td align='left' >
					<%=company%>
				</td>
				<td align='left' >
					<%
					for (String roleString:roleStrings){
						
						String[] roleStringArray = roleString.split(":#:");
						

						int roleId = Integer.parseInt(roleStringArray[1]);
						String roleName  = roleStringArray[0];
						%>
						<%=roleName %> &nbsp;
						<%
					}
					%>
					
				</td>
				<td align='left' >
					
						<%
						if (canNotBeOwners.contains(userEmailId)){
							%>
							<ul class="nav navbar-nav" >
								<li class="dropdown">
						          <a href="#" class="dropdown-toggle" data-toggle="dropdown" 
						          onmouseover="this.style.border ='2px solid #85C1E9';this.style.width = '-moz-available'"
						          onmouseout="this.style.border = 'none'" style="border: none;"> No </a>
						          	<ul class="dropdown-menu">
						            <li style="display:block">
						            	<a href="#" onclick="setRequirementOwnersInProject( '<%=userEmailId%>','remove');">Yes
						            	</a>
						            </li>
						            
						            </ul>
						        </li>
					        </ul>
							
							<%
						}
						else {
							%>
							<ul class="nav navbar-nav" >
								<li class="dropdown">
						          <a href="#" class="dropdown-toggle" data-toggle="dropdown" 
						          onmouseover="this.style.border ='2px solid #85C1E9';this.style.width = '-moz-available'"
						          onmouseout="this.style.border = 'none'" style="border: none;"> Yes </a>
						          	<ul class="dropdown-menu">
						            <li style="display:block">
						            	<a href="#" onclick="setRequirementOwnersInProject( '<%=userEmailId%>','add');">No
						            	</a>
						            </li>
						            
						            </ul>
						        </li>
					        </ul>
							<%
						}
						%>
				        
				      
					
				</td>
			</tr>
			
			
			<%
		}
		
		%>
			
		
	</table>
	
<%}%>