<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<% 
String databaseType = this.getServletContext().getInitParameter("databaseType");
// authorizatoin 
Project project= (Project) session.getAttribute("project");
// lets see if this user is a member of this project.
// we are leaving this page open to member of this project (which includes admins also)
boolean isMember = false;
SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
	isMember = true;
}


%>

<%if(isMember){

	int roleId = Integer.parseInt(request.getParameter("roleId"));
	Role role = new Role(roleId);
	ArrayList users = RoleUtil.getAllUsersInRole(roleId, databaseType);
	Iterator uI = users.iterator();
	
	String allUsersInRole = "";
	String closeAllToCCDivs = "";
	while (uI.hasNext()){
		User user = (User) uI.next();
		allUsersInRole += user.getEmailId() + ",";
		
		closeAllToCCDivs += "document.getElementById('toEmailId"+ user.getUserId() + "Div').style.display='none';  ";
		closeAllToCCDivs += "document.getElementById('cCEmailId"+ user.getUserId() + "Div').style.display='none';  ";
		
	}
	
	uI = users.iterator();
%>
	<table class='paddedTable' width='100%' >
	<tr>
		<td colspan='3'>
			<div>
				<div style='float:left'>
					<a href='#' onClick="document.getElementById('displayEasyAddEmailIdsDiv').style.display= 'none';">Close</a>
				</div>
			</div>
		</td>
	</tr>

	<tr>
		<td> <span class='normalText'>All <%=role.getRoleName()%></span>
		<td width='100'> 
			<div  id='toEmailIdForAllUsersDiv'> 
				<a href='#' 
					onClick="
						document.getElementById('to').value = '<%=allUsersInRole %>' + ',' + document.getElementById('to').value;
						document.getElementById('toEmailIdForAllUsersDiv').style.display='none';
						document.getElementById('ccEmailIdForAllUsersDiv').style.display='none';
						<%=closeAllToCCDivs %>
						"
				> Add All </a> 
			</div>
		</td>
		<td > 
			<div  id='ccEmailIdForAllUsersDiv'> 
				<a href='#' 
					onClick="
						document.getElementById('cc').value = '<%=allUsersInRole %>' + ',' + document.getElementById('cc').value;
						document.getElementById('toEmailIdForAllUsersDiv').style.display='none';
						document.getElementById('ccEmailIdForAllUsersDiv').style.display='none';
						<%=closeAllToCCDivs %>
						"
				> CC All </a> 
			</div>
		</td>
		
		
		
	</tr>
<%
	while (uI.hasNext()){
		User user = (User) uI.next();
	%>
		<tr>
			<td> <span class='normalText'><%=user.getFirstName() %> <%=user.getLastName() %></span>
			<td width='100'> 
				<div  id='toEmailId<%=user.getUserId()%>Div'> 
					<a href='#' 
						onClick="
							document.getElementById('to').value = '<%=user.getEmailId() %>' + ',' + document.getElementById('to').value;
							document.getElementById('toEmailId<%=user.getUserId()%>Div').style.display='none';
							document.getElementById('cCEmailId<%=user.getUserId()%>Div').style.display='none';
							"
					> Add</a> 
				</div>
			</td>
			<td>
				<div id='cCEmailId<%=user.getUserId()%>Div'> 
					<a href='#' 
						onClick="
							document.getElementById('cc').value = '<%=user.getEmailId() %>' + ',' + document.getElementById('cc').value;
							document.getElementById('cCEmailId<%=user.getUserId()%>Div').style.display='none';
							document.getElementById('toEmailId<%=user.getUserId()%>Div').style.display='none';
							"
					> CC </a> 
				</div>
			</td>			
			
		</tr>
	<%		
	}
	%>
	</table>
<%	
}
%>