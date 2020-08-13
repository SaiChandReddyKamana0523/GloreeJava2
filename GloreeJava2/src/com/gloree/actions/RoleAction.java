package com.gloree.actions;

import com.gloree.beans.*;
import com.gloree.utils.RoleUtil;
import com.gloree.utils.SecurityUtil;

import java.io.IOException;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/////////////////////////////////////////////Purpose ///////////////////////////////////////////
//
//	This servlet is used to create , update, deleted Roles and Users in those Roles.
//  Access restricted to Admins only.
//
///////////////////////////////////////////Purpose ///////////////////////////////////////////




public class RoleAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public RoleAction() {
        super();
    }

    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost (request,response);
    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String databaseType = this.getServletContext().getInitParameter("databaseType");

		
		///////////////////////////////SECURITY//////////////////////////////
		// Security  Note:
		// user has to be logged in by the time he is here. 
		// And he needs to be an Administrator 
		// of this project.
		///////////////////////////////SECURITY//////////////////////////////
		
		// see if the user is logged in. If he is not, the method below will
		// redirect him to the log in page.
		if (!(SecurityUtil.authenticationPassed(request, response))){
			return;
		}
		
		// now check if this users should be in this project.
		HttpSession session = request.getSession(true);
		SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
		Project project = (Project) session.getAttribute("project");
		if (!(
				(securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))
				||
				(securityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
		)){
			//User is NOT a member of this project. so do nothing and return.
			return;
		}
		///////////////////////////////SECURITY//////////////////////////////

		
		
		
		String action = request.getParameter("action");
		int projectId = project.getProjectId();
		User user = securityProfile.getUser();
		
		
		if ( action.equals("createRole")){
				
			String roleName = request.getParameter("roleName");
			String roleDescription = request.getParameter("roleDescription");
			String approvalType = request.getParameter("approvalType");
			int approvalRank = Integer.parseInt(request.getParameter("approvalRank"));
			
			
			
			
			
			int status = RoleUtil.isUniqueRoleName(projectId, roleName);
			if (status == 0){
				// this means the roleName is not unique to this project.
				request.setAttribute("status", "roleName already used");
				RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/Role/createRoleForm.jsp");
				dispatcher.forward(request, response);
				return;
			}

			// the Role object creates a new role in the db
			// and as part of it , makes a project log.
			Role role = new Role(projectId, roleName, roleDescription,user.getEmailId(),  databaseType);
			role.setRoleApprovalTypeAndRank(approvalType, approvalRank, user.getEmailId(), databaseType);
			
			
			request.setAttribute("Role", role);
			
			//we will try forwarding to start page where we will ask the user to log in.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/Role/displayRole.jsp");
			dispatcher.forward(request, response);
			return;
		}

		if ( action.equals("updateRole")){
			try {
			int roleId = Integer.parseInt(request.getParameter("roleId"));
			String roleName = request.getParameter("roleName");
			String roleDescription = request.getParameter("roleDescription");
			String approvalType = request.getParameter("approvalType");
			int approvalRank = Integer.parseInt(request.getParameter("approvalRank"));
			
			
			

			Role role= new Role(roleId);
			if (!(roleName.equals(role.getRoleName()))){
				role.setRoleName(roleName, user.getEmailId(), databaseType);
			}
			if (!(roleDescription.equals(role.getRoleDescription()))){
				role.setRoleDescription(roleDescription,  user.getEmailId(), databaseType);
			}
				
			// lets refresh  it.
			role = new Role(roleId);
			role.setRoleApprovalTypeAndRank(approvalType, approvalRank, user.getEmailId(), databaseType);
			
			
			request.setAttribute("Role", role);
			
			//we will try forwarding to start page where we will ask the user to log in.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/Role/editRoleForm.jsp");
			dispatcher.forward(request, response);
			return;
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}		
		if ( action.equals("addUsers")){
			
			String emailIds = request.getParameter("emailIds");
			int roleId = Integer.parseInt(request.getParameter("roleId"));
			

			// call addUsersToRole, which returns 3 # delimited strings incorrectDomain, 
			// addedUsers, invitedUsers . Each one of these strings is a , delimited
			// list of email Ids.
			// 
			String serverName = request.getServerName();

			String result = RoleUtil.addUsersToRole(project, roleId, emailIds, user.getEmailId(), databaseType, serverName);
			
			String inCorrectDomainEmailIds = "";
			String successfullyAddedEmailIds = "";
			String invitedEmailIds = "";
			
			if (result.contains("#")){
				String [] resultArray = result.split("#");
				
				
				// NOTE : for some reason, a split of #mkt@gmail.com# gives only 2 rows in teh array.
				// hence forcing a check.
				if (resultArray.length > 0) {
					// i.e we are sure 1st element is there . i.e item[0]
					inCorrectDomainEmailIds = resultArray[0];
				}
				if (resultArray.length > 1) {
					// i.e we are sure 2nd element is there . i.e item[1]
					successfullyAddedEmailIds = resultArray[1];
				}
				if (resultArray.length > 2) {
					// i.e we are sure 3rd element is there . i.e item[2]
					invitedEmailIds = resultArray[2];	
				}
			}
			
			
			request.setAttribute("inCorrectDomainEmailIds", inCorrectDomainEmailIds);
			request.setAttribute("successfullyAddedEmailIds", successfullyAddedEmailIds);
			request.setAttribute("invitedEmailIds", invitedEmailIds);
			
			RequestDispatcher dispatcher =	
				request.getRequestDispatcher("/jsp/AdministerProject/Role/addUserToRoleForm.jsp");
			dispatcher.forward(request, response);
			return;

			
		}

		if ( action.equals("updateRolePrivs")){
			
			int roleId = Integer.parseInt(request.getParameter("roleId"));
			String createRequirement =  request.getParameter("createRequirement");
			String readRequirement =  request.getParameter("readRequirement");
			String updateRequirement =  request.getParameter("updateRequirement");
			String deleteRequirement =  request.getParameter("deleteRequirement");
			String traceRequirement =  request.getParameter("traceRequirement");
			String approveRequirement =  request.getParameter("approveRequirement");
			String votingRightsString = request.getParameter("votingRightsString");
			String updateAttributesString = request.getParameter("updateAttributesString");
			
			System.out.println("srt votingRightsStrig is " + votingRightsString);
			
			RoleUtil.updateRolePrivs(projectId, roleId, createRequirement,readRequirement,updateRequirement,
				deleteRequirement, traceRequirement, approveRequirement,votingRightsString, updateAttributesString);
			
			// Since we have updated the role privss, and since this user (the one making the change)
			// may belong to one of those roles, lets update his security profile, so that this user
			// can work using thse new privs.
			securityProfile = new SecurityProfile(user.getUserId(),this.getServletContext().getInitParameter("databaseType"));
			session.setAttribute("securityProfile", securityProfile);			
						
			request.setAttribute("updatedRolePrivs", "true");
			RequestDispatcher dispatcher =	
				request.getRequestDispatcher("/jsp/AdministerProject/Role/editRolePrivilegesForm.jsp?roleId" + roleId);
			dispatcher.forward(request, response);
			return;
		}

		if ( action.equals("deleteUsersFromRole")){
			
			int roleId = Integer.parseInt(request.getParameter("roleId"));
			String deleteUsers =  request.getParameter("deleteUsers");
			
			RoleUtil.deleteUsersFromRole(projectId, roleId, deleteUsers, user.getEmailId(),  databaseType);
			
			RequestDispatcher dispatcher =	
				request.getRequestDispatcher("/jsp/AdministerProject/Role/editUsersForm.jsp?roleId" + roleId);
			dispatcher.forward(request, response);
			return;
		}

		if ( action.equals("moveUsersToNewRole")){
			
			int roleId = Integer.parseInt(request.getParameter("roleId"));
			int moveRoleId = Integer.parseInt(request.getParameter("moveRoleId"));
			String moveUsers =  request.getParameter("moveUsers");
			
			RoleUtil.moveUsersToNewRole(projectId, roleId, moveRoleId, moveUsers, user.getEmailId(),  databaseType);
		
			
			RequestDispatcher dispatcher =	
				request.getRequestDispatcher("/jsp/AdministerProject/Role/editUsersForm.jsp?roleId" + roleId);
			dispatcher.forward(request, response);
			return;
		}

		if ( action.equals("deleteRole")){
			
			int roleId = Integer.parseInt(request.getParameter("roleId"));
			
			RoleUtil.deleteRole(projectId, roleId, user.getEmailId(),  databaseType);
		
			
			RequestDispatcher dispatcher =	
				request.getRequestDispatcher("/jsp/AdministerProject/Role/confirmRoleDeletion.jsp");
			dispatcher.forward(request, response);
			return;
		}


	}

}