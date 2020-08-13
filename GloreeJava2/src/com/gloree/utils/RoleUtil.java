package com.gloree.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.naming.InitialContext;

import com.gloree.beans.Project;
import com.gloree.beans.Role;
import com.gloree.beans.User;
import com.gloree.beans.RolePriv;

public class RoleUtil {

	// returns 1 is the roleName does not exist in this project. else 1.
	 public static int isUniqueRoleName(int projectId, String roleName){
		int status = 0;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			// 	TODO : first see if the role name  is already used. 
			// if so we need to return a warning.
			String sql = "select count(*) \"matches\" " +
					" from gr_roles where project_id = ? and name = ?";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, roleName);
			rs = prepStmt.executeQuery();
			int matches = 0;
			if (rs.next()){
				matches = rs.getInt("matches");
				if (matches == 0 ){
					status = 1;
				}
			}
			rs.close();
			prepStmt.close();
			con.close();
		} catch (Exception e) {
				
				e.printStackTrace();
		}   finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}

		return status;
	}
	 

	 
	 public static void cancelInvitation (int inviteId){
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			// 	TODO : first see if the role name  is already used. 
			// if so we need to return a warning.
			String sql = "delete from gr_invitations where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, inviteId);
			prepStmt.execute();
			prepStmt.close();
			con.close();
		} catch (Exception e) {
				
				e.printStackTrace();
		}   finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
	}
	 
	 
	 

	 	// takes the projectId as a param, and returns an array list of Roles.
		public static ArrayList getRoles(int projectId){

			ArrayList roles = new ArrayList();
			PreparedStatement prepStmt = null;
			ResultSet rs = null;
			java.sql.Connection con = null;
			try {
			
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();

				//
				// This sql gets the core project info for all projects int the system,
				// creates a project object for each row and puts them the array list.
				// TODO : change this SQL so that we return ONLY the project to which the user has access.
				//
				String sql = "select id, name, description " +
						" from gr_roles where project_id = ?  ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				rs = prepStmt.executeQuery();
				
				while (rs.next()){
					// we already have project Id
					int roleId = rs.getInt("id");
					String roleName = rs.getString("name");
					String roleDescription = rs.getString ("description");
					
					//TODO : at some point see how we can make DATE fields works.
					Role role = new Role(roleId, projectId, roleName, roleDescription);
					roles.add(role);
				}
				prepStmt.close();
				rs.close();
				con.close();
			} catch (Exception e) {
				
				e.printStackTrace();
			}   finally {
				if (prepStmt !=null) { 
					try {prepStmt.close();} catch (Exception e) {}
				} 
				if (rs != null) { 
					try {rs.close();} catch (Exception e) {}
				} 
				if (con != null) {
					try {con.close();} catch (Exception e) {}
					con = null;
				}
			}

			return (roles);
		}
		
		
		// takes a roleId as a param and returns an array list of users in that role.
		
		public static ArrayList<User> getAllUsersInRole(int roleId, String databaseType){

			ArrayList<User> users = new ArrayList<User>();
			PreparedStatement prepStmt = null;
			ResultSet rs = null;
			java.sql.Connection con = null;
			try {
			
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();

				String sql = "";
				if (databaseType.equals("mySQL")){
					sql = " select u.id , u.ldap_user_id, ifnull(u.first_name,email_id) \"first_name\" , " +
						" u.last_name, u.email_id, u.pets_name, u.user_type, " +
						" date_format(u.account_expire_dt, '%d %M %Y ')  \"account_expire_dt\" , " +
						" ifnull(datediff(u.account_expire_dt, now()),0) \"days_left\", " +
						" u.billing_organization_id, u.number_of_requirements " +
						"  , pref_rows_per_page, pref_hide_projects  " +  
						" from gr_user_roles ur, gr_users u " +
						" where ur.user_id = u.id " + 
						" and ur.role_id = ? " +
						" order by first_name, last_name ";
				}
				else {
					sql = " select u.id , u.ldap_user_id, nvl(u.first_name,email_id) \"first_name\" , " +
					" u.last_name, u.email_id, u.pets_name, u.user_type, " +
					" to_char(u.account_expire_dt, 'DD MON YYYY')  \"account_expire_dt\" , " +
					" nvl((u.account_expire_dt - sysdate),0) \"days_left\", " +
					" u.billing_organization_id, u.number_of_requirements " +
					"  , pref_rows_per_page, pref_hide_projects  " +  
					" from gr_user_roles ur, gr_users u " +
					" where ur.user_id = u.id " + 
					" and ur.role_id = ? " +
					" order by first_name, last_name ";
				}
				
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, roleId);
				rs = prepStmt.executeQuery();
				
				while (rs.next()){
					// we already have project Id
					int userId = rs.getInt("id");
					String ldapUserId = rs.getString("ldap_user_id");
					String firstName = rs.getString("first_name");
					String lastName = rs.getString("last_name");
					String emailId = rs.getString("email_id");
					String petsName = rs.getString("pets_name");
					String userType = rs.getString("user_type");
					String accountExpireDt = rs.getString("account_expire_dt");
					int daysLeft = rs.getInt("days_left");
					int billingOrganizationId = rs.getInt("billing_organization_id");
					int numberOfRequirements = rs.getInt("number_of_requirements");
					
					int prefRowsPerPage = rs.getInt("pref_rows_per_page");
					String prefHideProjects = rs.getString("pref_hide_projects");
					

					
					// WE set the user to expired if he is on trial
					// and his trial date has expired.
					
					if ( (userType != null) &&  (userType.equals("trial") && (daysLeft <=  0) )) {
						userType = "expired";
					}

					User user = new User (userId, ldapUserId, firstName, lastName, emailId, petsName,
						userType, accountExpireDt,  daysLeft, billingOrganizationId ,
						numberOfRequirements , prefRowsPerPage, prefHideProjects);
					users.add(user);
				}
				prepStmt.close();
				rs.close();
				con.close();
			} catch (Exception e) {
				
				e.printStackTrace();
			}  finally {
				if (prepStmt !=null) { 
					try {prepStmt.close();} catch (Exception e) {}
				} 
				if (rs != null) { 
					try {rs.close();} catch (Exception e) {}
				} 
				if (con != null) {
					try {con.close();} catch (Exception e) {}
					con = null;
				}
			}

			return (users);
		}	 


		// call addUsersToRole, which returns 3 # delimited strings incorrectDomain, 
		// addedUsers, invitedUsers . Each one of these strings is a , delimited
		// list of email Ids.
		//
		// 1. split the emmail ids
		// 2. for each emailId, check for 
		// 3. is it in allowed domains.
		// 4. it it in tracecloud already, if so add to user_role and log it and invite.
		// 5. add it to invite list, and send an email.
		
	 public static String addUsersToRole(Project project, int roleId, String emailIds, 
			 String createdByEmailId, String databaseType, String serverName){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		String result = "";
		String inCorrectDomainEmailIds = "";
		String successfullyAddedEmailIds = "";
		String invitedEmailIds = "";
		
		try {
			
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();


			//lets get the role object.
			Role role = new Role (roleId);
			String restrictedDomains = project.getRestrictedDomains();
			
			// following code converts an email id (one or comma separated) 
			// into a string array.
			String [] emailIdArray = {emailIds};
			// the email ids may be seperated by spaces or ; . So lets convert them to ,
			emailIds = emailIds.trim();
			if (emailIds.contains(";")){
				emailIds = emailIds.replace(";", ",");
			}
			
			if (emailIds.contains(" ")){
				emailIds = emailIds.replace(" ", ",");
			}
			
			if (emailIds.contains(",")){
				emailIdArray = emailIds.split(",");
			}
			
			for (int i=0; i<emailIdArray.length ; i++) {
				// see if this email id is in the domain.
				// do this test only if the admins have entered a value in the project's
				// restrictedDomains fields.
				
				String targetEmailId = emailIdArray[i];
				// targetEmailId could have extra spaces in beginning and end.
				if ((targetEmailId != null) && (targetEmailId.contains(" "))) {
					targetEmailId = targetEmailId.replace(" ", "");
				}
				
					if ((restrictedDomains != null)  && (!(restrictedDomains.equals("")))){
			
						if (!(emailIdIsValid(targetEmailId, restrictedDomains))){
							// this email Id is not valid 
							// i.e not in the restricted domains.
							// So, we add this to our invalid list and skip processing of this row.
							inCorrectDomainEmailIds += targetEmailId + ",";
							continue;
						}
					}
					// see if this user already has an account in the tracecloud system.
					if (UserAccountUtil.userExistsInTraceCloud(targetEmailId)){

						// this is a valid user in the trace cloud system.
						// so lets add him / her to the right role.
	
						// get user id of the user we are adding to this role (not the person logged in).
						int targetUserId = 0 ;
						String sql = "select id from gr_users where lower(email_id) = ? ";
						prepStmt = con.prepareStatement(sql);
						prepStmt.setString(1, targetEmailId.toLowerCase());
						rs = prepStmt.executeQuery();
						while (rs.next()){
							targetUserId = rs.getInt("id");
						}
						rs.close();
						prepStmt.close();
					
						
						// now do the insert into role.
						
						// here is the user already exists in this role, and the admin screws up
						// there is potential for the db unique key to explode. So, lets catch it.
						try {
							sql = "insert into gr_user_roles(user_id, project_id, role_id) values (?,?,?)";
							prepStmt = con.prepareStatement(sql);
										
							prepStmt.setInt(1, targetUserId);
							prepStmt.setInt(2, project.getProjectId());
							prepStmt.setInt(3, roleId);
							prepStmt.execute();
							prepStmt.close();
						}
						catch (Exception e) {
							// do nothing.
						}
						// at this point, lets make a call to make a project log entry.
						ProjectUtil.createProjectLog(project.getProjectId(), role.getRoleName(), "Add a User", 
							"Adding User " + targetEmailId + " to role ", createdByEmailId,  databaseType);
						
						
						// now lets send an email to the user.
						String projectName = project.getProjectName();
						String toEmailId = targetEmailId;
						String messageType = "existingUserAddedToProject";
						String messageBody =  
							"<br><br>You have been granted access to project '" + projectName +"'." +
							" You can access the project by logging into your Tracecloud account at " + 
							"<br><br>http://" + serverName;					
						
						EmailUtil.storeMessage(projectName, toEmailId, messageType, messageBody,   databaseType);
						
						successfullyAddedEmailIds += targetEmailId + ",";
						continue;
						
					}
					else {
						// means user does not exist in the system, and we need to send 
						// him / her an invite.
						
						// first lets make an entry in the gr_invitations table
						// so that when the user creates an account, we can grant  him / her 
						// access to that role.
						// now do the insert into role.
						
						// lets put this in a try / catch block, to handle db unique exception
						// when admins  try to invite some one multiple times to a role.
						// this will ensure that the other records get processed properly.
						
						try {
							String sql = "";
							if (databaseType.equals("mySQL")){
								 sql = "insert into gr_invitations(invitee_email_id, invitor_email_id," +
									" project_id, role_id, event_dt, emails_sent, last_email_sent_on) " +
									" values (?,?,?, ? , now(), 1, now())";
							}
							else {
								 sql = "insert into gr_invitations(invitee_email_id, invitor_email_id," +
								" project_id, role_id, event_dt, emails_sent, last_email_sent_on ) " +
								" values (?,?,?, ? , sysdate, 1 , sysdate)";
							}
							prepStmt = con.prepareStatement(sql);
										
							prepStmt.setString(1, targetEmailId);
							prepStmt.setString(2, createdByEmailId);
							prepStmt.setInt(3, project.getProjectId());
							prepStmt.setInt(4, roleId);
							prepStmt.execute();
							prepStmt.close();
						}
						catch (Exception e) {
							// do nothing
							e.printStackTrace();
						}
						// at this point, lets make a call to make a project log entry.
						ProjectUtil.createProjectLog(project.getProjectId(), role.getRoleName(), "Invited a User", 
							"Invited User " + targetEmailId+ " to role ", createdByEmailId,   databaseType);
	
	
						
						// now lets send an email to the user.
						String projectName = project.getProjectName();
						String toEmailId = targetEmailId;
						String messageType = "newUserAddedToProject";
						String messageBody =  "<br><br>You have been granted access to project '" + projectName + "'." +
						" You can access this project by creating a FREE account at " + 
						"<br><br>http://" + serverName +  
						"<br><br>NOTE : PLEASE REMEMBER TO USE '" + targetEmailId + 
						"' WHEN YOU CREATE YOUR TRACECLOUD ACCOUNT." + 
						"<br><br> This will ensure that you will have access to this project." +
						"<br><br> This is the First of Five reminders "	;		
						EmailUtil.storeMessage(projectName, toEmailId, messageType, messageBody,   databaseType);
						
						invitedEmailIds += targetEmailId + ",";
						continue;
					}
			}
			
			
			// drop the last , from all the three email strings.
			
			if (inCorrectDomainEmailIds.contains(",") ) {
				inCorrectDomainEmailIds = (String) inCorrectDomainEmailIds.subSequence(0,inCorrectDomainEmailIds.lastIndexOf(","));
			}
			if (successfullyAddedEmailIds.contains(",") ) {
				successfullyAddedEmailIds = (String) successfullyAddedEmailIds.subSequence(0,successfullyAddedEmailIds.lastIndexOf(","));
			}
			if (invitedEmailIds.contains(",") ) {
				invitedEmailIds = (String) invitedEmailIds.subSequence(0,invitedEmailIds.lastIndexOf(","));
			}
			result = inCorrectDomainEmailIds + "#" + 
				successfullyAddedEmailIds + "#" +
				invitedEmailIds;
			
			con.close();
		} catch (Exception e) {
		
			e.printStackTrace();
		}  finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		return result;
	}
	 
	// takes an email id and restrictedDomains string (comma separated)
	// splits all the domains and sees if the email Id is in at least one of them.
	// returns true is email is valid.
	public static boolean emailIdIsValid(String emailId, String restrictedDomains) {
		boolean isValid = false;
		try {
			String [] domains = {restrictedDomains};
			if (restrictedDomains.contains(",")) {
				domains = restrictedDomains.split(",");
			}
			for (int i=0; i<domains.length; i++) {
				if (emailId.contains(domains[i].trim())) {
					isValid = true;
				}
			}
		}
		catch (Exception e) {
			// do Nothing

			e.printStackTrace();
		}
		return isValid;
	}
	 
	 
	 // Takes a projectId, roleId as a param, and returns an array list of rolePrivs.
	public static ArrayList getRolePrivs(int projectId, int roleId, String databaseType){

		ArrayList rolePrivs = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			//
			// This sql gets the role privileges for the roleId.
			// and creates a rolePriv object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "";
				sql = " select f.id \"folder_id\", f.folder_path, " +
					" ifnull(create_requirement, 0) \"create_requirement\" , " +
					" ifnull(read_requirement, 0) \"read_requirement\" , " +
					" ifnull(update_requirement, 0) \"update_requirement\" , " +
					" ifnull(delete_requirement, 0) \"delete_requirement\" , " +
					" ifnull(trace_requirement, 0) \"trace_requirement\" , " +
					" ifnull(approve_requirement, 0) \"approve_requirement\", " +
					" voting_rights , " + 
					" update_attributes " +
					" from gr_folders f , gr_role_privs rp " +
					" where rp.folder_id = f.id " +
					" and rp.role_id = ? " +
					" and f.project_id =  ? " +
					" order by f.folder_path"; 
			
			prepStmt = con.prepareStatement(sql);
			
			
			prepStmt.setInt(1, roleId);
			prepStmt.setInt(2, projectId);
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				// we already have project Id
				int folderId = rs.getInt("folder_id");
				String folderPath = rs.getString("folder_path");
				int createRequirement = rs.getInt("create_requirement");
				int readRequirement = rs.getInt("read_requirement");
				int updateRequirement = rs.getInt("update_requirement");
				int deleteRequirement = rs.getInt("delete_requirement");
				int traceRequirement = rs.getInt("trace_requirement");
				int approveRequirement = rs.getInt("approve_requirement");
				int votingRights = rs.getInt("voting_rights");
				String updateAttributes = rs.getString("update_attributes");
				
				RolePriv rolePriv = new RolePriv(roleId , folderId, folderPath,
						 createRequirement , readRequirement , updateRequirement,
						 deleteRequirement ,traceRequirement , 
						 approveRequirement, votingRights, updateAttributes);
				
				
				rolePrivs.add(rolePriv);
			}
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}

		return (rolePrivs);
	}	 

	
	// called when an admin edits a role's privileges to a folder.
	// note createRequirement is a : separated list of folderIds to which this role should
	// get createRequirement privs.
	public static void updateRolePrivs(int projectId, int roleId, String createRequirement,
		String readRequirement, String updateRequirement, String deleteRequirement, 
		String traceRequirement, String approveRequirement, String votingRightsString, String updateAttributesString){

		// For performance sake, we are making this call, where
		// for every folder / role combo in the project, there exists a row in the
		// rolePrivTable.

		updateRolePrivTable(projectId);

		// now before we update the role privileges, we need to reset every thing to empty.
		// this is for efficiency. Instead of figuring out which checkboxes the user unchecked
		// we simply empty out all the privs for this role, and activate only those that the user
		// has actively selected.
		removeAllPrivsForRole(roleId);
		
		
		// lets drop the last : in the string name.
		// in this case createRequirement has a list of folders like 5:15:22:23:24:18:25:27:26:
		if (createRequirement.contains(":") ) {
			createRequirement = (String) createRequirement.subSequence(0,createRequirement.lastIndexOf(":"));
		}
		if (readRequirement.contains(":") ) {
			readRequirement = (String) readRequirement.subSequence(0,readRequirement.lastIndexOf(":"));
		}
		if (updateRequirement.contains(":") ) {
			updateRequirement = (String) updateRequirement.subSequence(0,updateRequirement.lastIndexOf(":"));
		}
		if (deleteRequirement.contains(":") ) {
			deleteRequirement = (String) deleteRequirement.subSequence(0,deleteRequirement.lastIndexOf(":"));
		}
		if (traceRequirement.contains(":") ) {
			traceRequirement = (String) traceRequirement.subSequence(0,traceRequirement.lastIndexOf(":"));
		}

		if (approveRequirement.contains(":") ) {
			approveRequirement = (String) approveRequirement.subSequence(0,approveRequirement.lastIndexOf(":"));
		}
		
		// in this case createRequirement has a list of folders like 5:15:22:23:24:18:25:27:26
		
		
			
			
		// Update CreateRequirement Permissions.
		// createRequirement has a list of folder Ids for which we need to set the
		// permissions for this role and project.
		if (createRequirement.contains(":") ) {
			// in this case createRequirement has a list of folders like 5:15:22:23:24:18:25:27:26
			String [] creates = createRequirement.split(":");
			for (int  i=0; i < creates.length ; i++){
				int folderId = Integer.parseInt(creates[i]);
				setPrivileges(roleId,folderId,"createRequirement");
			}
		}
		else if (createRequirement.length() >0 ){
			// this catches scenarios where only a single folder Id was sent in. eg --> 5
			int folderId = Integer.parseInt(createRequirement);
			setPrivileges(roleId,folderId,"createRequirement");
		}
		
		// Update ReadRequirement Permissions.
		// readRequirement has a list of folder Ids for which we need to set the
		// permissions for this role and project.
		if (readRequirement.contains(":") ) {
			String [] reads = readRequirement.split(":");
			for (int  i=0; i < reads.length ; i++){
				int folderId = Integer.parseInt(reads[i]);
				setPrivileges(roleId,folderId,"readRequirement");
			}
		}
		else if (readRequirement.length() >0 ){
			// this catches scenarios where only a single folder Id was sent in. eg --> 5
			int folderId = Integer.parseInt(readRequirement);
			setPrivileges(roleId,folderId,"readRequirement");
		}
		
		// Update UpdateRequirement Permissions.
		// updateRequirement has a list of folder Ids for which we need to set the
		// permissions for this role and project.
		if (updateRequirement.contains(":") ) {
			String [] updates = updateRequirement.split(":");
			for (int  i=0; i < updates.length ; i++){
				int folderId = Integer.parseInt(updates[i]);
				setPrivileges(roleId,folderId,"updateRequirement");
			}
		}
		else if (updateRequirement.length() >0 ){
			// this catches scenarios where only a single folder Id was sent in. eg --> 5
			int folderId = Integer.parseInt(updateRequirement);
			setPrivileges(roleId,folderId,"updateRequirement");
		}
		
		// Update deleteRequirement Permissions.
		// deleteRequirement has a list of folder Ids for which we need to set the
		// permissions for this role and project.
		if (deleteRequirement.contains(":") ) {
			String [] deletes = deleteRequirement.split(":");
		
			for (int  i=0; i < deletes.length ; i++){
				int folderId = Integer.parseInt(deletes[i]);
				setPrivileges(roleId,folderId,"deleteRequirement");
			}
		}
		else if (deleteRequirement.length() >0 ){
			// this catches scenarios where only a single folder Id was sent in. eg --> 5
			int folderId = Integer.parseInt(deleteRequirement);
			setPrivileges(roleId,folderId,"deleteRequirement");
		}
		
		
		// Update tracetoRequirement Permissions.
		// tracetoRequirement has a list of folder Ids for which we need to set the
		// permissions for this role and project.
		if (traceRequirement.contains(":") ) {
			String [] tracetos = traceRequirement.split(":");
			for (int  i=0; i < tracetos.length ; i++){
				int folderId = Integer.parseInt(tracetos[i]);
				setPrivileges(roleId,folderId,"traceRequirement");
			}
		}
		else if (traceRequirement.length() >0 ){
			// this catches scenarios where only a single folder Id was sent in. eg --> 5
			int folderId = Integer.parseInt(traceRequirement);
			setPrivileges(roleId,folderId,"traceRequirement");
		}
		
		
		// Update approveRequirement Permissions.
		// approveRequirement has a list of folder Ids for which we need to set the
		// permissions for this role and project.
		if (approveRequirement.contains(":") ) {
			String [] approves = approveRequirement.split(":");
			for (int  i=0; i < approves.length ; i++){
				int folderId = Integer.parseInt(approves[i]);
				setPrivileges(roleId,folderId,"approveRequirement");
			}
		}
		else if (approveRequirement.length() >0 ){
			// this catches scenarios where only a single folder Id was sent in. eg --> 5
			int folderId = Integer.parseInt(approveRequirement);
			setPrivileges(roleId,folderId,"approveRequirement");
		}

		
		if ((votingRightsString != null) && (votingRightsString.contains("###") )) {
			String [] votingRightsObjects = votingRightsString.split("###");
			for (int  i=0; i < votingRightsObjects.length ; i++){
				String votingRightsRow = votingRightsObjects[i];
				
				// at this point updateAttribuesRow looks like 115##:#:collateral:#:Customer:#:Deliverability:#:
				if ((votingRightsRow != null) && (votingRightsRow.contains("##") )) {
					String [] votingRightsRowObjects = votingRightsRow.split("##");
					if (votingRightsRowObjects.length > 1){
						int folderId = Integer.parseInt(votingRightsRowObjects[0]);
						int votingRights=0;
						try{
							votingRights = Integer.parseInt(votingRightsRowObjects[1]);
						}
						catch (Exception e){
							e.printStackTrace();
						}
						setVotingRights (roleId, folderId, votingRights);
					}
				}
			}
		}
		//updateAttributesString are sent as a string of folder id , updateAttributes. 
		// for every role id , folder id combo, we set the 'updateAttributes' column value to this string.
		// eg input string is like 
		// 115##:#:collateral:#:Customer:#:Deliverability:#:###133##:#:Customer:#:Deliverability:#:###
		//135##:#:Agile Effort Remaining (hrs):#:Agile Total Effort (hrs):#:###
		
		
		// now lets split by ### to get a row of role id / folder id and its update string values.
		if ((updateAttributesString != null) && (updateAttributesString.contains("###") )) {
			String [] updateAttributesObjects = updateAttributesString.split("###");
			for (int  i=0; i < updateAttributesObjects.length ; i++){
				String updateAttribuesRow = updateAttributesObjects[i];
				
				// at this point updateAttribuesRow looks like 115##:#:collateral:#:Customer:#:Deliverability:#:
				if ((updateAttribuesRow != null) && (updateAttribuesRow.contains("##") )) {
					String [] updateAttributesRowObjects = updateAttribuesRow.split("##");
					if (updateAttributesRowObjects.length > 1){
						int folderId = Integer.parseInt(updateAttributesRowObjects[0]);
						String updateAttributes = updateAttributesRowObjects[1];
						setUpdateAttributes (roleId, folderId, updateAttributes);
					}
				}
			}
		}
				
	}

	// ensures that for every folder / role combo in the project, there exists a row in the
	// rolePrivTable.
	public static void updateRolePrivTable(int projectId){	
	
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

	
			String sql = "insert into gr_role_privs (role_id,folder_id,create_requirement, " + 
				" read_requirement, update_requirement, delete_requirement, " + 
				" trace_requirement, " +
				" approve_requirement) " + 
				" select r.id,f.id,0,0,0,0,0,0 " + 
				" from gr_folders f, gr_roles r " +
				" where f.project_id = ? " +
				" and r.project_id = ? " +
				" and (f.id, r.id) not in " +
				" (select rp.folder_id, rp.role_id " +
				" from gr_role_privs rp, gr_folders ff, gr_roles rr " + 
				" where rp.role_id = rr.id " + 
				" and rp.folder_id = ff.id " + 
				" and ff.project_id =?  " +  
				" and rr.project_id = ?) ";
			

			System.out.println("srt in updateRolePrivTable projectid id  is  " + projectId );
			
			System.out.println("srt in updateRolePrivTable sql is  " + sql );
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,projectId);
			prepStmt.setInt(2,projectId);
			prepStmt.setInt(3,projectId);
			prepStmt.setInt(4,projectId);
			prepStmt.execute();
			
			prepStmt.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}

	}	 

	// This method removes all privileges for this role
	public static void removeAllPrivsForRole(int roleId){	
	
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

	
			String sql = "update gr_role_privs set create_requirement = 0 ," +
				" read_requirement = 0 , update_requirement = 0 , delete_requirement = 0 , " + 
				" trace_requirement = 0 , " +
				" approve_requirement = 0 , update_attributes = '' " +
				" where role_id = ? " ;
					
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, roleId);
			prepStmt.execute();
			
			prepStmt.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}

	}	 

	// updates the create_requirement flag for the role_id, folder_id combinations.
	public static void setPrivileges(int roleId, int folderId, String type){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			
						
			if (type.equals ("createRequirement")) {
				// SQL for updating create_requireemnt.
				sql = "update gr_role_privs set create_requirement = 1 " +
					"  where role_id = ? and folder_id = ? ";
			}

			if (type.equals ("readRequirement")) {
				// SQL for updating read_requireemnt.
				sql = "update gr_role_privs set read_requirement = 1 " +
					"  where role_id = ? and folder_id = ? ";
			}

			if (type.equals ("updateRequirement")) {
				// SQL for updating update_requireemnt.
				sql = "update gr_role_privs set update_requirement = 1 " +
					"  where role_id = ? and folder_id = ? ";
			}

			if (type.equals ("deleteRequirement")) {
				// SQL for updating delete_requireemnt.
				sql = "update gr_role_privs set delete_requirement = 1 " +
					"  where role_id = ? and folder_id = ? ";
			}
			
			if (type.equals ("traceRequirement")) {
				// SQL for updating delete_requireemnt.
				sql = "update gr_role_privs set trace_requirement = 1 " +
					"  where role_id = ? and folder_id = ? ";
			}
						
			if (type.equals ("approveRequirement")) {
				// SQL for updating delete_requireemnt.
				sql = "update gr_role_privs set approve_requirement = 1 " +
					"  where role_id = ? and folder_id = ? ";
			}			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, roleId);
			prepStmt.setInt(2, folderId);
			prepStmt.execute();
				
			
			prepStmt.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}

	}	 

	// sets the update_attributes column of a row in the gr_role_privs table
	// takes the role Id, folder Id as an input (this is the unique combo)
	public static void setUpdateAttributes(int roleId, int folderId, String updateAttributes){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// SQL for updating create_requireemnt.
			String sql = "update gr_role_privs set update_attributes = ? " +
				"  where role_id = ? and folder_id = ? ";
		
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, updateAttributes);
			prepStmt.setInt(2, roleId);
			prepStmt.setInt(3, folderId);
			prepStmt.execute();
				
			
			prepStmt.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}

	}
	
	
	public static void setVotingRights(int roleId, int folderId, int votingRights){
		System.out.println("srt calling voting rights for role " + roleId + " folder " + folderId + "  rights = " + votingRights);
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// SQL for updating create_requireemnt.
			String sql = "update gr_role_privs set voting_rights = ? " +
				"  where role_id = ? and folder_id = ? ";
		
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, votingRights);
			prepStmt.setInt(2, roleId);
			prepStmt.setInt(3, folderId);
			prepStmt.execute();
				
			
			prepStmt.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}

	}
	
	// This gives read privs to all roles for this folderId
	public static void setReadPrivileges(int folderId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
				// SQL for updating read_requireemnt.
			String sql = "update gr_role_privs set read_requirement = 1 " +
				"  where folder_id = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId);
			prepStmt.execute();
				
			
			prepStmt.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}

	}	 
	
	// called with a string of users ids that need to be removed from a role.
	public static void deleteUsersFromRole(int projectId, int roleId, 
			String deleteUsers , String actorEmailId, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// get Role Name to make the log entry.
			String sql = "select name from gr_roles where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, roleId);
			rs = prepStmt.executeQuery();
			String roleName = "";
			while (rs.next()){
				roleName = rs.getString("name");
			}
				
			sql = " delete from gr_user_roles " +
					" where user_id = ? " +
					" and project_id = ? " +
					" and role_id = ? "; 
			prepStmt = con.prepareStatement(sql);
			// split the string and for each user id, delete it.
			if (deleteUsers.length() > 0) {
				String [] deletes = deleteUsers.split(":");
				
				for (int  i=0; i < deletes.length ; i++){

					if (deletes[i] != null) {
						int userId = Integer.parseInt(deletes[i]);
						prepStmt.setInt(1, userId);
						prepStmt.setInt(2, projectId);
						prepStmt.setInt(3, roleId);
					
						prepStmt.execute();
						
						// lets get the deleted user's email id so that we use it for logging
	
						sql = "select email_id from gr_users where id = ? ";
						PreparedStatement prepStmt2 = con.prepareStatement(sql);
						prepStmt2.setInt(1, userId);
						ResultSet rs2 = prepStmt2.executeQuery();
						String userEmailId = "";
						while (rs2.next()){
							userEmailId = rs2.getString("email_id");
						}
	

						// at this point, lets make a call to make a project log entry.
						ProjectUtil.createProjectLog(projectId, roleName , "Delete a User From Role", 
							"Deleting " + userEmailId + " from role ", actorEmailId,   databaseType);
					}
				}
			}
			
			prepStmt.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}

	}

	// called with a string of users ids that need to be moved to a new role ,i.e moveRoleId.
	public static void moveUsersToNewRole(int projectId, int roleId,int moveRoleId, 
			String moveUsers, String actorEmailId, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();


			// get Role Name to make the log entry.
			String sql = "select name from gr_roles where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, moveRoleId);
			rs = prepStmt.executeQuery();
			String roleName = "";
			while (rs.next()){
				roleName = rs.getString("name");
			}
			rs.close();
			prepStmt.close();

			

	

			prepStmt = con.prepareStatement(sql);
			// split the string and for each user id, move him to the new role.
			if (moveUsers.length() > 0) {
				String [] moves = moveUsers.split(":");
				for (int  i=0; i < moves.length ; i++){
					if (moves[i] != null) {
						int userId = Integer.parseInt(moves[i]);
						if (userId <= 0 ){
							// we may get -1 from the header column (multi select / deselect checkbox)
							continue;
						}
						// we will delete user from the current role and try to create a new 
						// role for him. 
						// NOTE : we don't want to update route, as we are running into a bug
						// i.e. if the user a has roles x, and y, and you try to move him from x to y
						// by updaing his role , since there will now be 2 Y roles for htis user
						// the integrity constraint is crapping out.
						
						sql = "delete from gr_user_roles " + 
							" where user_id = ? " +
							" and project_id = ? " +
							" and role_id = ? "; 
					
						prepStmt = con.prepareStatement(sql);
						
						prepStmt.setInt(1, userId);
						prepStmt.setInt(2, projectId);
						prepStmt.setInt(3, roleId);
						prepStmt.execute();
					
						
						// lets insert him in his new role.
						// first lets check to ensure he isn't already a member of this role.
						sql = " select count(*) \"match\" from gr_user_roles " + 
							" where user_id = ? and project_id = ? and role_id = ?  ";
						prepStmt = con.prepareStatement(sql);
						prepStmt.setInt(1, userId);
						prepStmt.setInt(2, projectId);
						prepStmt.setInt(3, moveRoleId);
						rs = prepStmt.executeQuery();
						int match = 0;
						while (rs.next()) {
							match = rs.getInt("match");
						}

						
						
						if (match == 0 ) {
							// this user role doesn't exists already. so lets create a new one.
							sql = " insert into gr_user_roles (user_id, project_id, role_id) " +
								" values (?, ?, ?)" ;
							prepStmt = con.prepareStatement(sql);
							prepStmt.setInt(1, userId);
							prepStmt.setInt(2, projectId);
							prepStmt.setInt(3, moveRoleId);
							prepStmt.execute();

						}
											
					
					
					
						// lets get the deleted user's email id so that we use it for logging
						sql = "select email_id from gr_users where id = ? ";
						prepStmt = con.prepareStatement(sql);
						prepStmt.setInt(1, userId);
						rs = prepStmt.executeQuery();
						String userEmailId = "";
						while (rs.next()){
							userEmailId = rs.getString("email_id");
						}
						rs.close();
						prepStmt.close();
						
						// at this point, lets make a call to make a project log entry.
						ProjectUtil.createProjectLog(projectId, roleName , "Move a User To new Role", 
							"Moving" + userEmailId + " to role ", actorEmailId,   databaseType);
						
					}
				}
			}
			prepStmt.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}

	}


	// called with RoleId that needs to be deleted from this project.
	public static void deleteRole(int projectId, int roleId, String actorEmailId, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			
			con = dataSource.getConnection();
	
			// get Role Name to make the log entry.
			String sql = "select name from gr_roles where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, roleId);
			rs = prepStmt.executeQuery();
			String roleName = "";
			while (rs.next()){
				roleName = rs.getString("name");
			}
			
			
			// first all all privileges for this role.
			sql = " delete from gr_role_privs  where role_id = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, roleId);
			prepStmt.execute();
			
			
			// we don't need to use all users from this role
			// as we want the admins to either move them manually to a different role 
			// or delete them prior  to role deletion.
			sql = " delete from gr_roles  where id = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, roleId);
			
			// at this point, lets make a call to make a project log entry.
			ProjectUtil.createProjectLog(projectId, roleName , "Deleting Role", 
				"Deleting the Role from the Project", actorEmailId,   databaseType);

			
			prepStmt.execute();
			prepStmt.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}

	}

	
	// called with projectId and returns the Id of the admin role for this project.
	public static int getAdminRoleId(int projectId) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		int adminRoleId = 0; 
		try {			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			
			con = dataSource.getConnection();
	
			// get Role Name to make the log entry.
			String sql = "select id " +
				" from gr_roles " +
				" where project_id = ? " +
				" and name = 'Administrator'";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				adminRoleId = rs.getInt("id");
			}
			
			prepStmt.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
		return adminRoleId;
	}
	
	public static boolean canCreateObjects(String key, int folderId) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		boolean canCreate = false;
		try {			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			
			con = dataSource.getConnection();
	
			// get Role Name to make the log entry.
			String sql = "select count(*) 'matches' " +
				" from gr_role_privs rp, gr_roles r, gr_user_roles ur, gr_users u " + 
				" where u.api_key = ? " +
				" and rp.folder_id = ? " +
				" and rp.role_id = r.id " +
				" and r.id = ur.role_id " +
				" and ur.user_id = u.id " +
				" and rp.create_requirement = 1 ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, key);
			prepStmt.setInt(2, folderId);
			rs = prepStmt.executeQuery();
			
			int matches = 0 ; 
			while (rs.next()){
				matches  = rs.getInt("matches");
			}
			
			if (matches == 1){
				canCreate = true;
			}
			prepStmt.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
		return canCreate;
	}
	

}
