package com.gloree.beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

import javax.naming.InitialContext;

public class SecurityProfile {
	private LinkedHashSet projects = new LinkedHashSet();
	private ArrayList projectObjects = new ArrayList();
	private LinkedHashSet roles = new LinkedHashSet();
	private LinkedHashSet privileges = new LinkedHashSet();
	private User user;

	// takes the userId of a user and gets the projects, roles and privileges the user has.
	public SecurityProfile (int userId, String databaseType) {
		
		java.sql.Connection con = null;
		try {
			
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();


			
			// get Projects this user has data in .
			String sql  = "";
			
				sql = "select distinct concat (p.id, ':##:', p.name , ':##:', p.short_name, ':##:', " +
					"	p.description, ':##:', p.created_by) project, " +
					"   p.id, p.short_name, p.name, p.project_type, p.description, " +
					"	p.owner, p.website, p.organization, p.tags, p.restricted_domains," +
					"   p.enable_tdcs, p.enable_agile_scrum, p.billing_organization_id, " +
					"   p.number_of_requirements, p.created_by, p.last_modified_by, p.archived, p.hide_priority" + 
					" from gr_user_roles ur, gr_projects p " +
					" where ur.user_id= ? " +
					" and ur.project_id = p.id" +
					" order by p.archived, p.name ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, userId);
			ResultSet rs = prepStmt.executeQuery();
			
			while (rs.next()){
				String project = rs.getString("project");
				int projectId = rs.getInt("id");
				String shortName = rs.getString("short_name");
				String name = rs.getString("name");
				String projectType = rs.getString("project_type");
				String description = rs.getString("description");
				
				String projectOwner = rs.getString("owner");
				String projectWebsite = rs.getString("website");
				String projectOrganization= rs.getString("organization");
				String projectTags = rs.getString("tags");
				
				
				String restrictedDomains = rs.getString("restricted_domains");
				int enableTDCS = rs.getInt("enable_tdcs");
				int enableAgileScrum = rs.getInt("enable_agile_scrum");
				int billingOrganizationId = rs.getInt("billing_organization_id");
				int numberOfRequirements = rs.getInt("number_of_requirements");
				String createdBy = rs.getString("created_by");
				String lastModifiedBy = rs.getString("last_modified_by");
				int archived = rs.getInt("archived");
				
				int hidePriority = rs.getInt("hide_priority");
				
				
				Project projectObject = new Project(projectId,shortName,name, projectType, description,
					projectOwner, projectWebsite, projectOrganization, projectTags, 
					restrictedDomains,
					enableTDCS, enableAgileScrum, billingOrganizationId, numberOfRequirements, createdBy, lastModifiedBy, archived, hidePriority);
				
				this.projectObjects.add(projectObject);
				this.projects.add(project);
			}
			prepStmt.close();
			rs.close();
			
			
			// get the user data and create a user object.
			// get Roles for this user.
			if (databaseType.equals("mySQL")){
				sql = "select id, ldap_user_id, first_name, last_name, email_id, u.pets_name, u.user_type, " +
					" date_format(u.account_expire_dt, '%d %M %Y ')  'account_expire_dt' , " +
					" ifnull(datediff(u.account_expire_dt, now()),0) 'days_left' , " +
					" u.billing_organization_id , u.number_of_requirements" +
					"   , pref_rows_per_page, pref_hide_projects  " +    
					" from gr_users u " +
					" where u.id= ? ";
			}
			else {
				sql = "select id, ldap_user_id, first_name, last_name, email_id, u.pets_name, u.user_type, " +
				" to_char(u.account_expire_dt, 'DD MON YYYY')  \"account_expire_dt\" , " +
				" nvl((u.account_expire_dt - sysdate),0) \"days_left\" , " +
				" u.billing_organization_id , u.number_of_requirements " +
				"   , pref_rows_per_page, pref_hide_projects  " +    
				" from gr_users u " +
				" where u.id= ? ";				
			}
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, userId);
			rs = prepStmt.executeQuery();
		
			if (rs.next()){
				
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
				
				// WE set the user to expired if he is on trial
				// and his trial date has expired.
				if ( (userType != null) &&  (userType.equals("trial") && (daysLeft <=  0) )) {
					userType = "expired";
				}

				int prefRowsPerPage = rs.getInt("pref_rows_per_page");
				String prefHideProjects = rs.getString("pref_hide_projects");
				

				
				User user = new User (userId, ldapUserId, firstName, lastName, emailId, petsName,
					userType, accountExpireDt,  daysLeft, billingOrganizationId,
					numberOfRequirements , prefRowsPerPage, prefHideProjects);
				this.user = user;
			}
			prepStmt.close();
			rs.close();

			
			
			// get Roles for this user.
			// we do not want 'expired' users to have access to the projects.
			// read only users can only get read access .(so they can only be members 
			// and not admins)
			// read write get every thing.
			sql = "select r.name , r.project_id  , p.billing_organization_id " + 
				" from gr_user_roles ur, gr_roles r , gr_projects p " +
				" where ur.user_id= ? " + 
				" and ur.role_id = r.id " +
				" and r.project_id = p.id ";
		
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, userId);
			rs = prepStmt.executeQuery();
		
			while (rs.next()){
				String roleName = rs.getString("name");
				int projectId = rs.getInt("project_id");
				int billingOrganizationId = rs.getInt("billing_organization_id");

				
				// if this is a Sponsored project, i.e some one  is paying for it every month
				// the we simply read the roles and add them to the list. 
				if (billingOrganizationId > 0 ){
					if (roleName.equals("Administrator")){
						// An Admin gets both the MemberInProject and AdminInProject entries.
						this.roles.add("AdministratorInProject" + projectId);
						this.roles.add("MemberInProject" + projectId);
					}
					else if (roleName.equals("PowerUser")){
						// An Admin gets both the MemberInProject and AdminInProject entries.
						this.roles.add("PowerUserInProject" + projectId);
						this.roles.add("MemberInProject" + projectId);
					}
					else{
						//A user gets only the MemberInProject entry
						this.roles.add("MemberInProject" + projectId);
					}
				}
				else {
					// for all non sponsored projects, we see if the user has a readonly or readwrite
					// or an expired license and set the roles accordingly.
					// for read write users, set roles as it states in the database..
					// trial users , during the trial period get the same settings as read/write users.
					// Once the trial period expires, they become 'expired' users, i. ie. user.getUserTyep
					// will return 'expired'.
					if ((user.getUserType().equals("readWrite")) || (user.getUserType().equals("trial"))){
						if (roleName.equals("Administrator")){
							// An Admin gets both the MemberInProject and AdminInProject entries.
							this.roles.add("AdministratorInProject" + projectId);
							this.roles.add("MemberInProject" + projectId);
						}
						else if (roleName.equals("PowerUser")){
							// An Admin gets both the MemberInProject and AdminInProject entries.
							this.roles.add("PowerUserInProject" + projectId);
							this.roles.add("MemberInProject" + projectId);
						}
						else{
							//A user gets only the MemberInProject entry
							this.roles.add("MemberInProject" + projectId);
						}
					}
					
					// for readOnly users, they can never become admins. so lets not give them the 
					// admin options.
					if (user.getUserType().equals("readOnly")){
						if (roleName.equals("Administrator")){
							// An Admin gets both the MemberInProject and AdminInProject entries.
							// however, since this is a read only account, we don't make them admins.
							this.roles.add("MemberInProject" + projectId);
						}
						else if (roleName.equals("PowerUser")){
							this.roles.add("MemberInProject" + projectId);
						}
						else{
							//A user gets only the MemberInProject entry
							this.roles.add("MemberInProject" + projectId);
						}
					}
				}
			}
			prepStmt.close();
			rs.close();
			

			// get Privileges for this user.
			// we need to control this based on the user's account type.
			// if the account is 'expired', they get no permissions.
			// if the account is ' trial' or 'readWrite' they get permissions
			// per the settings in the database.
			// if the account is 'readOnly' they get ONLy read permissions, if such
			// permissions exist for this user in the db.
			sql = "select p.billing_organization_id, rp.folder_id, rp.create_requirement, rp.read_requirement, " +
				" rp.update_requirement, rp.delete_requirement, " + 
				" rp.trace_requirement, " +
				" rp.approve_requirement , rp.update_attributes " + 
				" from gr_user_roles ur, gr_role_privs rp , gr_projects p" +
				" where ur.user_id= ? " + 
				" and ur.role_id = rp.role_id" +
				" and ur.project_id = p.id ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, userId);
			rs = prepStmt.executeQuery();
		
			
			while (rs.next()){
				int folderId = rs.getInt("folder_id");
				int createRequirement = rs.getInt("create_requirement");
				int readRequirement = rs.getInt("read_requirement");
				int updateRequirement = rs.getInt("update_requirement");
				int deleteRequirement = rs.getInt("delete_requirement");
				int traceRequirement = rs.getInt("trace_requirement");
				int approveRequirement = rs.getInt("approve_requirement");
				String updateAttributes = rs.getString("update_attributes");
				
				int billingOrganizationId = rs.getInt("billing_organization_id");

				
				// if this is a Sponsored project, i.e some one  is paying for it every month
				// the we simply read the privs and add them to the list. 
				if (billingOrganizationId > 0 ){
					if (createRequirement == 1 ){
						this.privileges.add("createRequirementsInFolder" + folderId);
					}
					
					if (readRequirement == 1 ){
						this.privileges.add("readRequirementsInFolder" + folderId);
					}
					
					if (updateRequirement == 1 ){
						this.privileges.add("updateRequirementsInFolder" + folderId);
					}
					
					if (deleteRequirement == 1 ){
						this.privileges.add("deleteRequirementsInFolder" + folderId);
					}
					
					if (traceRequirement == 1 ){
						this.privileges.add("traceToRequirementsInFolder" + folderId);
						this.privileges.add("traceFromRequirementsInFolder" + folderId);
					}

	
					if (approveRequirement == 1 ){
						this.privileges.add("approveRequirementsInFolder" + folderId);
					}
					this.privileges.add(":#:updateAttributes" + updateAttributes + folderId + ":#:");
				}
				else {
					// for all non sponsored projects, we differentiate based on the type of license.
					// for read write users, set permissions as it states in the database..
					// trial users , during the trial period get the same settings as read/write users.
					// Once the trial period expires, they become 'expired' users, i. ie. user.getUserTyep
					// will return 'expired'.
					if ((user.getUserType().equals("readWrite")) || (user.getUserType().equals("trial"))){
						if (createRequirement == 1 ){
							this.privileges.add("createRequirementsInFolder" + folderId);
						}
						
						if (readRequirement == 1 ){
							this.privileges.add("readRequirementsInFolder" + folderId);
						}
						
						if (updateRequirement == 1 ){
							this.privileges.add("updateRequirementsInFolder" + folderId);
						}
						
						if (deleteRequirement == 1 ){
							this.privileges.add("deleteRequirementsInFolder" + folderId);
						}
						
						if (traceRequirement == 1 ){
							this.privileges.add("traceToRequirementsInFolder" + folderId);
							this.privileges.add("traceFromRequirementsInFolder" + folderId);
						}
	
						if (approveRequirement == 1 ){
							this.privileges.add("approveRequirementsInFolder" + folderId);
						}
						this.privileges.add(":#:updateAttributes" + updateAttributes + folderId + ":#:");
					}
					// for readOnly users, they can never become admins. so lets not give them the 
					// admin options.
					if (user.getUserType().equals("readOnly")){
						if (readRequirement == 1 ){
							this.privileges.add("readRequirementsInFolder" + folderId);
						}
					}
				}
			}
			prepStmt.close();
			rs.close();

			
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}   finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
			
	}
	
	
	public LinkedHashSet getProjects () {
		return this.projects;
	}
	public ArrayList getProjectObjects () {
		return this.projectObjects;
	}
	
	public LinkedHashSet getRoles() {
		return this.roles;
	}

	public LinkedHashSet getPrivileges () {
		return this.privileges;
	}
	
	public User getUser(){
		return this.user;
	}
	
	// Iterates through all the privileges this user has and
	// for the given folder, returns the string that defines which attributes
	// this user can update
	// since this user can belong to more than one role, we keep on concatenating
	// all the update attribute permissions on this folder for each role this user is a member of.
	
	public String getUpdateAttributesForFolder(int folderId){
		String updateAttributes = "";
		Iterator p = this.privileges.iterator();
		while (p.hasNext()){
			String privilege = (String) p.next();
			if (	
				(privilege.contains(":#:updateAttributes:#:"))
				&&
				(privilege.contains(":#:"+ folderId +":#:"))){
					updateAttributes += privilege;
			}
		}
		return updateAttributes ;
	}

}
