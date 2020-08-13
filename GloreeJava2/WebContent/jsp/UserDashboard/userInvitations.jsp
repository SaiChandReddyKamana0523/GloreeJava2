<%@ page import="java.util.*" %>
<%@ page import="java.sql.Date" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

	
	

	

<%


SecurityProfile userInvitationSP = (SecurityProfile) session.getAttribute("securityProfile") ;
String invitorEmailId= userInvitationSP.getUser().getEmailId();

	ArrayList<String> invitations = ProjectUtil.getInvitedUserByMe(invitorEmailId);
	if (invitations.size() > 0 ){
%>
	<table class='table' style='width:100%'>
		<tr>
			<td colspan='7'> 
				<div class='alert alert-danger'>
					Invitations sent by  you 
					
				</div>
			</td>
		</tr>
		<tr class='info'>
			<td>Project</td>
			<td>Role</td>
			<td>Invitee</td>
			<td style='min-width:100px'>Invite Date</td>
			<td style='min-width:130px'>Last Mail Date</td>
			<td> Reminders </td>
			<td> Action</td>
		</tr>
		<%
		Iterator<String> inv = invitations.iterator();
		while (inv.hasNext()){
			String invitation = (String) inv.next();
			// lets split the invitation
			String[] invitationA = invitation.split(":##:");
			String invitee = invitationA[0];
			String invitor = invitationA[1];
			String inviteDate = invitationA[2];
			String lastEmailDt = invitationA[3];
			
			int emailsSent = Integer.parseInt(invitationA[4]);

			String roleName = invitationA[5];
			int inviteId = Integer.parseInt(invitationA[6]);

			int projectId = Integer.parseInt(invitationA[7]);
			String projectName = invitationA[8];
			%>
			<tr>
				<td><%=projectName%></td>
				<td><%=roleName %></td>
				<td><%=invitee%></td>
				<td><%=inviteDate %></td>
				<td><%=lastEmailDt %></td>
				<td><%=emailsSent %></td>
				<td><input type='button' class='btn btn-sm btn-danger' onclick='withdrawInvitation(<%=inviteId %>);' value='Withdraw Invitation'></input></td>	
			
			
			</tr>
			<%
		}
		%>	
	
	</table>
<%
	}
	
%>












