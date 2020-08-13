package com.gloree.utils;

import java.security.MessageDigest;

import sun.misc.BASE64Encoder;

import com.gloree.beans.LicenseGrant;
import com.gloree.beans.MessagePacket;
import com.gloree.beans.Project;
import com.gloree.beans.Role;
import com.gloree.beans.SecurityProfile;
import com.gloree.beans.User;

import oracle.jdbc.*;
import oracle.jdbc.pool.*;

import javax.servlet.http.HttpServletRequest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.servlet.http.HttpSession;


public class UserAccountUtil {


	// checks to see if a user exists in the system with this email id.
	public static boolean userExistsInTraceCloud(String emailId){
		

		boolean exists = false;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql deletes the folder. We assume that the calling routine has validated to make sure that this is not a root level folder.
			//
			String sql = "select count(*) \"matches\" from gr_users " +
				" where lower(email_id) = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, emailId.toLowerCase());
			
			rs = prepStmt.executeQuery();
			while (rs.next()){
				
				int matches = rs.getInt("matches");

				if (matches > 0) {
					// i.e a match already exists. Hence a user already exists with this email Id
					exists = true;
				}
				else {
					exists = false;
				}
			}
			
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


		return exists;
		
	}	

	// checks to see if a user exists in the system with this email id, firstName, lastName combinations.
	// used when resetting passwords.
	public static boolean userExistsInTraceCloud(String emailId, String firstName, String lastName,
			String petsName){
		
		boolean exists = false;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql deletes the folder. We assume that the calling routine has validated to make sure that this is not a root level folder.
			//
			String sql = "select count(*) \"matches\" from gr_users " +
				" where email_id = ? " +
				" and first_name = ? " +
				" and last_name  = ? " +
				" and pets_name = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, emailId);
			prepStmt.setString(2, firstName);
			prepStmt.setString(3, lastName);
			prepStmt.setString(4, petsName);
			
			rs = prepStmt.executeQuery();
			while (rs.next()){
				
				int matches = rs.getInt("matches");
				if (matches > 0) {
					// i.e a match already exists. Hence a user already exists with this email Id
					exists = true;
				}
				else {
					exists = false;
				}
			}
			
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

		return exists;
	}	

	
	// this routine ...
	// 1. creates the user account.
	// 2. see if this user has any invites
	// 3. if so, creates an entry in user role
	// 4. removes the user from the invite list.
	// 5. sees if the user is member of a sample project and if not , clones a project just for him.
	public static void createUser(int sampleProjectId, String sampleProjectPrefix, String sampleProjectCreatedBy, 
			String ldapUserId, String firstName, String lastName, String emailId, String password, 
			String petsName, String heardAboutTraceCloud,
			String installationType, String authenticationType, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;


		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql creates a user account.
			//
			String sql = "";
			
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_users (first_name, last_name, email_id, password, pets_name, " +
					" user_type, account_expire_dt, heard_about_tracecloud)  " +
					" values (?,?,?,des_encrypt(?, 'h0t7:16M0TO'),?, \"trial\", " +
					" date_add(now(), interval 60 day), ? )";
				
			
				prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, firstName);
				prepStmt.setString(2, lastName);
				prepStmt.setString(3, emailId);
				prepStmt.setString(4, password);
				prepStmt.setString(5, petsName);
				prepStmt.setString(6, heardAboutTraceCloud);
				
				
				prepStmt.execute();
				prepStmt.close();
			}

			if (databaseType.equals("oracle")){
				sql = "insert into gr_users (first_name, last_name, email_id, password, pets_name, " +
					" user_type, account_expire_dt, heard_about_tracecloud)  " +
					" values (?,?,?,?,?, 'trial', " +
					" sysdate + 60 , ? )";
				
			
				MessageDigest md = null;
			    try
			    {
			      md = MessageDigest.getInstance("SHA");
			      md.update(password.getBytes("UTF-8"));
			    }
			    catch(Exception e)
			    {
			      e.printStackTrace();
			    }
			    
			    byte raw[] = md.digest(); //step 4
			    String encryptedPassword = (new BASE64Encoder()).encode(raw); 

			
			    
			    prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, firstName);
				prepStmt.setString(2, lastName);
				prepStmt.setString(3, emailId);
				prepStmt.setString(4, encryptedPassword);
				prepStmt.setString(5, petsName);
				prepStmt.setString(6, heardAboutTraceCloud);
				
				
				prepStmt.execute();
				prepStmt.close();
			}

			
			User user = new User (emailId, databaseType);
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_user_history (user_id, old_user_type, new_user_type, event_dt) " +
				" values (?,?,? , now()) " ;
			}
			else {
				sql = "insert into gr_user_history (user_id, old_user_type, new_user_type, event_dt) " +
				" values (?,?,? , sysdate) " ;
			}
		
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, user.getUserId() );
			prepStmt.setString(2, "N/A");
			prepStmt.setString(3, "trial");
		
			prepStmt.execute();
			prepStmt.close();
			
			// lets make an entry in the history table.
			
			
			// lets handle the invites. i.e. if this user had any invites, we need to give him access to those
			// invites.
			
			// first get a list of invites.
			sql = "select id, project_id, role_id from gr_invitations where invitee_email_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, emailId);
			
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				int inviteId = rs.getInt("id");
				int projectId = rs.getInt("project_id");
				int roleId = rs.getInt("role_id");
				
			
				// grant access to this user to this role.
				// to prevent the unique index blowing up , try / catch it.
				
				try {
					String sql2 = "insert into gr_user_roles (user_id, project_id, role_id) " +
						" values(?,?,?) ";
					PreparedStatement prepStmt2 = con.prepareStatement(sql2);
					prepStmt2.setInt(1, user.getUserId());
					prepStmt2.setInt(2, projectId);
					prepStmt2.setInt(3, roleId);
					prepStmt2.execute();
					
					prepStmt2.close();
				}
				catch (Exception e) {
					// do nothing.
				}
			}
			
			rs.close();
			prepStmt.close();
						
			// now that we have processed all the invitations for this email id, lets delete the invitations.			
			sql = "delete from gr_invitations where invitee_email_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, emailId);
			prepStmt.execute();
			
			// lets see if this user has any license grants pending.
			LicenseGrant pendingLicenseGrant = user.getMyPendingLicenseGrant(databaseType);
			if (pendingLicenseGrant != null){
				user.acceptLicenseGrant(databaseType);
			}
			
			// Lets call the CloneUtil's cloneSampleProject method. 
			// this will clone the sample project for this user and 
			// change ownership of all requirements to this owner.
			
			// cloning happens only for installation type = saas.
			if (installationType.equals("saas")){
				CloneUtil.cloneSampleProject(con, sampleProjectId, sampleProjectPrefix,  sampleProjectCreatedBy, emailId,  databaseType, user);
			}
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

	
	
	public static void updateUserProfile(HttpSession session, User user , String firstName, String lastName,
			String emailId, String password, String petsName, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql creates a user account.
			//
			String sql = "update gr_users " +
				" set first_name = ? , " +
				" last_name = ? , " +
				" email_id = ? ," +
				" password = des_encrypt(?, 'h0t7:16M0TO')," +
				" pets_name = ? " +
				" where id = ?  ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, firstName);
			prepStmt.setString(2, lastName);
			prepStmt.setString(3, emailId);
			prepStmt.setString(4, password);
			prepStmt.setString(5, petsName);
			prepStmt.setInt(6, user.getUserId());
			
			prepStmt.execute();
			prepStmt.close();
			
			con.close();
			
			// since the user bean's core info (like account status) has changed, lets create new bean
			// and add to the session. The way we do that is we create a new securityProfile.
			// now we get the security profile and add it session.
			SecurityProfile securityProfile = new SecurityProfile(user.getUserId(),databaseType);
			session.setAttribute("securityProfile", securityProfile);

			
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
	
	
	/*
	 * we are forcing all the users to go to License grants page and grant themseleves a license. This way, we don't
	 * need the activateUserAccount section.
	public static void activateUserAccount(HttpSession session, User user, String userType){
		java.sql.Connection con =  null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String oldUserType = user.getUserType();
			String newUserType = userType;
			//
			// This sql creates a user account.
			//
			String sql = "update gr_users " +
				" set user_type = ? " +
				" where id = ? " ;
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, newUserType);	
			prepStmt.setInt(2, user.getUserId());
			
			prepStmt.execute();
			prepStmt.close();
			
			// lets make an entry in the history table.
			sql = "insert into gr_user_history (user_id, old_user_type, new_user_type, event_dt) " +
			" values (?,?,?,now()) " ;
		
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, user.getUserId() );
			prepStmt.setString(2, oldUserType);
			prepStmt.setString(3, newUserType);
		
			prepStmt.execute();
			prepStmt.close();
			
			// since the user bean's core info (like account status) has changed, lets create new bean
			// and add to the session. The way we do that is we create a new securityProfile.
			// now we get the security profile and add it session.
			SecurityProfile securityProfile = new SecurityProfile(user.getUserId());
			session.setAttribute("securityProfile", securityProfile);

						
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

	*/

	
	public static void genericLog(int projectId, int objectId, String objectType , String description, String actorEmailId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;


		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql creates a user account.
			//
			String sql = "";
			
				sql = "insert into gr_generic_log(project_id, object_id, object_type, description, actor_email_id, action_dt) "+
					" values (?,?,?,?,?, now() ) ";
					
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1,projectId);
				prepStmt.setInt(2, objectId);
				prepStmt.setString(3, objectType);
				prepStmt.setString(4, description);
				prepStmt.setString(5, actorEmailId);
				
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

	public static void updateBillingInfo(HttpSession session, User user, String ccNumber,
			String ccType, String ccExpireMonth, String ccExpireYear, String ccFullName,
			String ccVerificationNumber, String ccBillingAddress, String ccBillingZipcode,
			String ccBillingCountry){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// This sql creates a user account.
			//
			String sql = "update gr_users " +
				" set cc_full_name = des_encrypt(?, 'h0t7:16M0TO')," +
				" cc_type = des_encrypt(?, 'h0t7:16M0TO') , " +
				" cc_number = des_encrypt(?, 'h0t7:16M0TO'), " +
				" cc_expire_month = des_encrypt(?, 'h0t7:16M0TO'), " +
				" cc_expire_year = des_encrypt(?, 'h0t7:16M0TO'), " +
				" cc_verification_number = des_encrypt(?, 'h0t7:16M0TO'), " +
				" cc_billing_address = ?, " +
				" cc_billing_zipcode = ?,  " +
				" cc_billing_country = ? " +
				" where id = ? " ;
			
			prepStmt = con.prepareStatement(sql);	
			prepStmt.setString(1, ccFullName);
			prepStmt.setString(2, ccType);
			prepStmt.setString(3, ccNumber);
			prepStmt.setString(4, ccExpireMonth);
			prepStmt.setString(5, ccExpireYear);
			prepStmt.setString(6, ccVerificationNumber);
			prepStmt.setString(7, ccBillingAddress);
			prepStmt.setString(8, ccBillingZipcode);
			prepStmt.setString(9, ccBillingCountry);
			prepStmt.setInt(10, user.getUserId());
			
			prepStmt.execute();
			prepStmt.close();
			
			// we don't need to make a history entry as we are only chanign the 
			// cc info, and not the user profile itself.
			
			// we don't need to update the user bean as the core info has not changed.
						
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

	
	// this routine updates the org infor a user (# of read / read write licenses etc..)
	// it also activates some projects for project Licensing.
	public static void updateOrganizationInfo(HttpSession session, 
			User user, SecurityProfile securityProfile,
			String organizationName,
			String organizationDescription , String organizationPhone, 
			int readWriteLicenses, int readOnlyLicenses, String[] projectLicense, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// when a user is created, he does not have an organization. 
			// so, at this point, the user may / may not have an org. So first lets check. 
			
			String sql = " select id from gr_organizations where contact_id = ?  ";
			int organizationId = 0;
			prepStmt = con.prepareStatement(sql);	
			prepStmt.setInt(1, user.getUserId());
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				organizationId = rs.getInt("id");
			}
			rs.close();
			prepStmt.close();
			
			if (organizationId == 0) {
				// lets create a new org for this user.
				sql = "insert into gr_organizations(contact_id, contact_email_id, name, description," +
					" phone_number, read_write_licenses, read_only_licenses) " +
					" values(?,?,?,?," +
					"?,?,?)";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, user.getUserId());
				prepStmt.setString(2, user.getEmailId());
				prepStmt.setString(3, organizationName);
				prepStmt.setString(4, organizationDescription);
				prepStmt.setString(5, organizationPhone);
				prepStmt.setInt(6, readWriteLicenses);
				prepStmt.setInt(7, readOnlyLicenses);
				
				prepStmt.execute();
				prepStmt.close();
				
				// now that we have created the org, lets get the organization_id for this user.
				sql = " select id from gr_organizations where contact_id = ?  ";
				prepStmt = con.prepareStatement(sql);	
				prepStmt.setInt(1, user.getUserId());
				rs = prepStmt.executeQuery();
				while (rs.next()) {
					organizationId = rs.getInt("id");
				}
				rs.close();
				prepStmt.close();
					
			}
			
			// this  user already has an org entry. we just need to update it.
			sql = "update gr_organizations" +
				" set name = ? , " +
				" description = ? , " +
				" phone_number = ? ," +
				" read_write_licenses = ? , " +
				" read_only_licenses = ? " +
				" where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, organizationName);
			prepStmt.setString(2, organizationDescription);
			prepStmt.setString(3, organizationPhone);
			prepStmt.setInt(4, readWriteLicenses);
			prepStmt.setInt(5, readOnlyLicenses);
			
			prepStmt.setInt(6, organizationId);
			prepStmt.execute();
			prepStmt.close();
			
			// lets make an entry into the org history table for billing purposes.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_organization_history(organization_id, event_dt, event_by, " +
					" contact_id, contact_email_id, name, description," +
					" phone_number, read_write_licenses, read_only_licenses) " +
					" values(?, now(), ? , " +
					"?,?,?,?," +
					"?,?,?)";
			}
			else {
				sql = "insert into gr_organization_history(organization_id, event_dt, event_by, " +
				" contact_id, contact_email_id, name, description," +
				" phone_number, read_write_licenses, read_only_licenses) " +
				" values(?, sysdate, ? , " +
				"?,?,?,?," +
				"?,?,?)";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, organizationId);
			prepStmt.setString(2, user.getEmailId());
			
			
			prepStmt.setInt(3, user.getUserId());
			prepStmt.setString(4, user.getEmailId());
			prepStmt.setString(5, organizationName);
			prepStmt.setString(6, organizationDescription);
			
			prepStmt.setString(7, organizationPhone);
			prepStmt.setInt(8, readWriteLicenses);
			prepStmt.setInt(9, readOnlyLicenses);
			
			prepStmt.execute();
			prepStmt.close();
			
			// lets loop through all the projects in the projectLicense array
			// and grant them project license.
			
			if (projectLicense != null){
				int numOfProjects = projectLicense.length;
				for (int j=0; j < numOfProjects; j++ ){
					
					int projectId = Integer.parseInt(projectLicense[j]);
						
					// this user is a member of this project and can pay for this license.
					sql =" update gr_projects set billing_organization_id = ? " +
						" where id = ? ";
					prepStmt = con.prepareStatement(sql);
					prepStmt.setInt(1, organizationId);
					prepStmt.setInt(2, projectId);
					prepStmt.execute();
					prepStmt.close();
					
					// lets add the project license event to the gr_organization_history 
					// for billing purposes.
					// lets make an entry into the org history table for billing purposes.
					if (databaseType.equals("mySQL")){
						sql = "insert into gr_organization_history(organization_id, event_dt, event_by, " +
							" contact_id, contact_email_id, project_license) " +
							" values(?, now(), ? , " +
							" ?,?, ? )";
					}
					else {
						sql = "insert into gr_organization_history(organization_id, event_dt, event_by, " +
						" contact_id, contact_email_id, project_license) " +
						" values(?, sysdate, ? , " +
						" ?,?, ? )";
					}
					prepStmt = con.prepareStatement(sql);
					prepStmt.setInt(1, organizationId);
					prepStmt.setString(2, user.getEmailId());
					
					
					prepStmt.setInt(3, user.getUserId());
					prepStmt.setString(4, user.getEmailId());
					prepStmt.setString(5, "Adding Project Id" + projectId + " to billinng");
					
					prepStmt.execute();
					prepStmt.close();
				
				}  
			}
						
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

	public static void grantOnSiteLicense(HttpSession session, User user, String granteeEmailId, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets split the readWriteInvitees.
			if ((granteeEmailId!=null) && (!(granteeEmailId.equalsIgnoreCase("")))){
				granteeEmailId = granteeEmailId.trim();
				// lets add an entry int the gr_license grants table.
				String sql = "";
				if (databaseType.equals("mySQL")){
					sql = "insert into gr_license_grants (" +
						" license_type, grantee_email_id, grantor_email_id, grant_dt, notification_sent,grant_state )" +
						" values ('readWrite', ? , ? , now(), 0, 'accepted') ";
				}
				else {
					sql = "insert into gr_license_grants (" +
					" license_type, grantee_email_id, grantor_email_id, grant_dt, notification_sent, grant_state )" +
					" values ('readWrite', ? , ? , sysdate, 0, 'accepted') ";
				}
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, granteeEmailId);
				prepStmt.setString(2, user.getEmailId());
				prepStmt.execute();
				prepStmt.close();
				
				if (UserAccountUtil.userExistsInTraceCloud(granteeEmailId)) {
					// lets make the user accept this license.
					sql = "update gr_users set user_type = 'readWrite' where email_id = ? ";
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, granteeEmailId);
					prepStmt.execute();
					prepStmt.close();
				}
			}
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



	// called when an org admin decides to grant read only / read write licenses out.
	// involves the following steps.
	// 1. add each invite to the gr_license_invitations table
	// 2. later on  a cron job will send the invites out and update the table to reflect this info.
	// 3. Once the user creates an account, he/she is automatically given this license.
	// , then the entry will be removed from the table
	// if the user is already a member of the project, he / she will immediately get the license
	public static void grantLicenses(HttpSession session, User user, String readWriteInvitees,
			String readOnlyInvitees, String databaseType,
			String serverName, String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort, final String emailUserId, final String emailPassword) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets split the readWriteInvitees.
			if ((readWriteInvitees!=null) && (!(readWriteInvitees.equalsIgnoreCase("")))){
				String [] inviteeEmailIds;
				if (readWriteInvitees.contains(",")){
					inviteeEmailIds = readWriteInvitees.split(",");
				}
				else {
					inviteeEmailIds = new String [1];
					inviteeEmailIds[0] = readWriteInvitees;
				}
				for (int i=0; i< inviteeEmailIds.length; i++) {
					
					String inviteeEmailId = inviteeEmailIds[i].trim();
					// lets add an entry int the gr_license grants table.
					String sql = "";
					if (databaseType.equals("mySQL")){
						sql = "insert into gr_license_grants (" +
							" license_type, grantee_email_id, grantor_email_id, grant_dt, notification_sent )" +
							" values ('readWrite', ? , ? , now(), 0) ";
					}
					else {
						sql = "insert into gr_license_grants (" +
						" license_type, grantee_email_id, grantor_email_id, grant_dt, notification_sent )" +
						" values ('readWrite', ? , ? , sysdate, 0) ";
					}
					
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, inviteeEmailId);
					prepStmt.setString(2, user.getEmailId());
					prepStmt.execute();
					prepStmt.close();
					
					if (UserAccountUtil.userExistsInTraceCloud(inviteeEmailId)) {
						// the invited user is already a member of tracecloud.
						// so lets conver this license over to the granted one.
						User invitedUser = new User(inviteeEmailId, databaseType);
						invitedUser.acceptLicenseGrant(databaseType);
					}
				}
					
				
			}
			
			// lets split the readOnlyInvitees.
			if ((readOnlyInvitees!=null) && (!(readOnlyInvitees.equalsIgnoreCase("")))){
				String [] inviteeEmailIds;
				if (readOnlyInvitees.contains(",")){
					inviteeEmailIds = readOnlyInvitees.split(",");
				}
				else {
					inviteeEmailIds = new String [1];
					inviteeEmailIds[0] = readOnlyInvitees;
				}
				for (int i=0; i< inviteeEmailIds.length; i++) {
					String inviteeEmailId = inviteeEmailIds[i].trim();
					
					// lets add an entry int the gr_license grants table.
					String sql = "";
					if (databaseType.equals("mySQL")){
						sql = "insert into gr_license_grants (" +
							" license_type, grantee_email_id, grantor_email_id, grant_dt, notification_sent )" +
							" values ('readOnly', ? , ? , now(), 0) ";
					}
					else {
						sql = "insert into gr_license_grants (" +
						" license_type, grantee_email_id, grantor_email_id, grant_dt, notification_sent )" +
						" values ('readOnly', ? , ? , sysdate, 0) ";
					}
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, inviteeEmailId);
					prepStmt.setString(2, user.getEmailId());
					prepStmt.execute();
					prepStmt.close();
					
					if (UserAccountUtil.userExistsInTraceCloud(inviteeEmailId)) {
						// the invited user is already a member of tracecloud.
						// so lets conver this license over to the granted one.
						User invitedUser = new User(inviteeEmailId,databaseType);
						invitedUser.acceptLicenseGrant(databaseType);
					}
				}
				
			}
			// now lets call send out license grants, so that the email gets sent out.
			sendOutLicenseGrants(serverName,  mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
		    
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

	private static void sendOutLicenseGrants(String  serverName,
			String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort, final String emailUserId, final String emailPassword) 
	throws SQLException{
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		
		

		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the list of users with pending emails.
			String sql = "select license_type, grantee_email_id, grantor_email_id 	" + 
				" from gr_license_grants" + 
				" where notification_sent = 0 and grant_state='pending'";
				
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				try {
					// each one of these users gets a seperate email. so it constitutes one email message.
					String licenseType = rs.getString("license_type");
					String grantorEmailId = rs.getString("grantor_email_id");
					String granteeEmailId = rs.getString("grantee_email_id");
					
					
					// drop any extra spaces at the beginning or end.
					if ((grantorEmailId != null) && (grantorEmailId.contains(" "))) {
						grantorEmailId = grantorEmailId.replace(" ", "");
					}
					if ((granteeEmailId != null) && (granteeEmailId.contains(" "))) {
						granteeEmailId = granteeEmailId.replace(" ", "");
					}
					
					
					// lets build the message body.
					String messageBody = "Hi, \n\n" +
						"You have been granted a " + licenseType + " License by " + grantorEmailId + 
						" for using the TraceCloud system. Please go to " + serverName +" and create an account to activate this license. " + 
						" \n\nPlease remember to use " + granteeEmailId + " as your account Id . \n" + 
						"\n\nBest Regards \n\nTrace Cloud System. ";
					
					
					// lets send the email out to the toEmailId;
					ArrayList to = new ArrayList();
					to.add(granteeEmailId);
					ArrayList cc = new ArrayList();
					cc.add(grantorEmailId);
					MessagePacket mP = new MessagePacket(to, cc, "A TraceCloud License has been assigned to you ", messageBody, "");
					EmailUtil.email(mP , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
					System.out.println("Granting license to " + granteeEmailId);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			prepStmt.close();
			rs.close();

			// now lets set the notification_sent  flag to a value , so that we won't process this row again.
			// once table gets big, we may consider, removing the rows that have been processed.
			sql = " update gr_license_grants  " +
				" set notification_sent = 1 " +
				" where notification_sent = 0 and grant_state='pending'";
			
			prepStmt = con.prepareStatement(sql);
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

	// revokes a license offer made to a user.
	public static void revokeProjectLicense(HttpSession session, User user, int licensedProjectId, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "update gr_projects " +
				" set billing_organization_id = null " +
				" where id= ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, licensedProjectId);
			prepStmt.execute();
			prepStmt.close();
			
			// lets add the project license event to the gr_organization_history 
			// for billing purposes.
			// lets make an entry into the org history table for billing purposes.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_organization_history(organization_id, event_dt, event_by, " +
					" contact_id, contact_email_id, project_license) " +
					" values(?, now(), ? , " +
					" ?,?, ? )";
			}
			else {
				sql = "insert into gr_organization_history(organization_id, event_dt, event_by, " +
				" contact_id, contact_email_id, project_license) " +
				" values(?, sysdate, ? , " +
				" ?,?, ? )";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, user.getMyOwnedOrganization());
			prepStmt.setString(2, user.getEmailId());
			
			
			prepStmt.setInt(3, user.getUserId());
			prepStmt.setString(4, user.getEmailId());
			prepStmt.setString(5, "Removing Project Id" + licensedProjectId + " from billinng");
			
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
	
	
	// revokes a license offer made to a user.
	public static void revokeLicensesOffer(HttpSession session, User user, int licenseGrantId) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "update gr_license_grants " +
				" set grant_state = 'revoked' " +
				" where grantor_email_id = ? " +
				"  and id = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, user.getEmailId());
			prepStmt.setInt(2, licenseGrantId);
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

	// revokes a license grant made to a user.
	public static void revokeGrantedLicense(HttpSession session, User user, String granteeEmailId) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "update gr_license_grants " +
				" set grant_state = 'revoked' " +
				" where grantor_email_id = ? " +
				"  and grantee_email_id = ?  ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, user.getEmailId());
			prepStmt.setString(2, granteeEmailId);
			prepStmt.execute();
			prepStmt.close();
			
			// also, lets update gr_users and expire the users current license.
			// NOTE : remember to expire the grantee and not the user (the grantor).
			sql = "update gr_users  " +
				" set user_type = 'expired', billing_organization_id = null  " +
				" where email_id = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, granteeEmailId);
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
	
	
	
	
	public static void closeUserAccount(HttpSession session, User user, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String oldUserType = user.getUserType();
			String newUserType = "expired";
			//
			// This sql creates a user account.
			//
			String sql = "update gr_users " +
				" set user_type = ? " +
				" where id = ? " ;
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, newUserType);	
			prepStmt.setInt(2, user.getUserId());
			
			prepStmt.execute();
			prepStmt.close();
			
			// lets make an entry in the history table.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_user_history (user_id, old_user_type, new_user_type, event_dt) " +
				" values (?,?,?,now()) " ;
			}
			else {
				sql = "insert into gr_user_history (user_id, old_user_type, new_user_type, event_dt) " +
				" values (?,?,?,sysdate) " ;
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, user.getUserId() );
			prepStmt.setString(2, oldUserType);
			prepStmt.setString(3, newUserType);
		
			prepStmt.execute();
			prepStmt.close();
			
			// since the user bean's core info (like account status) has changed, lets create new bean
			// and add to the session. The way we do that is we create a new securityProfile.
			// now we get the security profile and add it session.
			SecurityProfile securityProfile = new SecurityProfile(user.getUserId(), databaseType);
			session.setAttribute("securityProfile", securityProfile);

						
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

	
	public static void signInToDatabase(String emailId, String password, HttpServletRequest request, String databaseType){
		
		String signInStatus = "";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql compares the user password with what's in the db.
			//
			if (databaseType.equals("mySQL")){
				String sql = "select id " +
					" from gr_users " +
					" where lower(email_id) = ? and " +
					" des_decrypt(password, 'h0t7:16M0TO') = ?";
	
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, emailId.toLowerCase());
				prepStmt.setString(2, password);
				
				rs = prepStmt.executeQuery();
				
				// we already have the email
				int userId = 0;
				
				if (rs.next()){
					userId  = rs.getInt("id");
	
				}
				if (userId != 0 ){
					// this means the user's login / pwd matched and he / she is a valid user..
					HttpSession session = request.getSession(true);
					
					// now we get the security profile and add it session.
					SecurityProfile securityProfile = new SecurityProfile(userId, databaseType);
					session.setAttribute("securityProfile", securityProfile);
					session.setAttribute("isLoggedIn", "true");
					
				}
				rs.close();
				prepStmt.close();
			}
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

	public static String getAccessKey(String emailId, String password, HttpServletRequest request, String databaseType){
		
		String accessKey = "";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql compares the user password with what's in the db.
			//
			System.out.println("srt1  in getAccessKey");
			boolean validUser = false;
			int userId = 0;
			
			if (databaseType.equals("mySQL")){
				String sql = "select id " +
					" from gr_users " +
					" where email_id = ? and " +
					" des_decrypt(password, 'h0t7:16M0TO') = ?";
	
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, emailId);
				prepStmt.setString(2, password);
				
				rs = prepStmt.executeQuery();
				
				// we already have the email
				
				
				if (rs.next()){
					userId  = rs.getInt("id");
					System.out.println("srt user id is " + userId);
				}
				if (userId != 0 ){
					validUser = true;
				}
				rs.close();
				prepStmt.close();
			}
			if (databaseType.equals("oracle")){
				// oracle doesn't support des_encrypt / decrypt. so we use java to do the encryption
				// and store data in db.
				String sql = "select id " +
					" from gr_users " +
					" where email_id = ? and " +
					" to_char(password) = ?";
	
			    MessageDigest md = null;
			    try
			    {
			      md = MessageDigest.getInstance("SHA");
			      md.update(password.getBytes("UTF-8"));
			    }
			    catch(Exception e)
			    {
			      e.printStackTrace();
			    }
			    
			    byte raw[] = md.digest(); //step 4
			    String encryptedPassword = (new BASE64Encoder()).encode(raw); 

				prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, emailId);
				prepStmt.setString(2, encryptedPassword);
				
				rs = prepStmt.executeQuery();
				
				if (rs.next()){
					userId  = rs.getInt("id");
	
				}
				if (userId != 0 ){
					validUser = true;
				}
				rs.close();
				prepStmt.close();
			}
						
			
			
			System.out.println("valid user is " + validUser);
			// if valid user, then lets try to get his / her accessKey.
			if (validUser){

				System.out.println("srt is valid user");
				String sql = "select api_key from gr_users where id = ? ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, userId);
				
				rs = prepStmt.executeQuery();
				while (rs.next()){
					accessKey = rs.getString("api_key");
				}
				

				System.out.println("srt1  is valid user . access key is " + accessKey);
			}

			// if the user is a valid user and his / her accessKey is empty or null, then, lets try to assign a new accessKey to the user.
			if (
					validUser 
					&& 
					((accessKey == null) || (accessKey.equals("")))
				){
				HttpSession session = request.getSession(true);
				accessKey = session.getId();
				Random randomGenerator = new Random();
			    int randomInt = randomGenerator.nextInt(100);
				accessKey += Integer.toString(randomInt);
				
				String sql = "update gr_users set api_key= ? , api_calls_allowed_daily= 5000 where id = ?  ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, accessKey);
				prepStmt.setInt(2, userId);
				
				prepStmt.execute();
				

				System.out.println("srt2  setting calls sllowed");

				System.out.println("srt1  set a new access key to users . key is " + accessKey);
			}
			
			
			
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
		
		return (accessKey);
	}	

	public static void signInToLdap(String emailId, HttpServletRequest request, String databaseType){
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			

			
	    	/*
			// lets do ldap authentication.
			
			We are not doing authentication agains LDAP using Java, as we are using Tomcat credentials to do that.
			Hashtable authEnv = new Hashtable(11);
	    	
	    	String dn = "uid=" + ldapUserId  + "," + ldapBase;
			
	    	authEnv.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
	   		authEnv.put(Context.PROVIDER_URL, ldapURL);
	   		authEnv.put(Context.SECURITY_AUTHENTICATION, security);
	   		authEnv.put(Context.SECURITY_PRINCIPAL, dn);
	   		authEnv.put(Context.SECURITY_CREDENTIALS, password);
	 
			
    		DirContext authContext = new InitialDirContext(authEnv);
    		*/
			
    		// lets get the user's user id based on his log in name.

			//
			// This sql compares the user password with what's in the db.
			//
			String sql = "select id " +
				" from gr_users " +
				" where email_id  = ? ";
			
    			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, emailId);
			rs = prepStmt.executeQuery();
			
			int userId = 0;
			if (rs.next()){
				userId  = rs.getInt("id");
			}
			if (userId != 0 ){
				HttpSession session = request.getSession(true);
				
				// now we get the security profile and add it session.
				SecurityProfile securityProfile = new SecurityProfile(userId, databaseType);
				session.setAttribute("securityProfile", securityProfile);
			}
			rs.close();
			prepStmt.close();
			con.close();
			
			// for ldap , since the user is already authenticated, lets set the isLogged in to true.
			HttpSession session = request.getSession(true);
			session.setAttribute("isLoggedIn", "true");
			
    		
    	} catch (AuthenticationException authEx) {
    		authEx.printStackTrace();
    	} catch (NamingException namEx) {
    		namEx.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
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

	
	public static void resetPassWord(String emailId, HttpServletRequest request,
			String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort, final String emailUserId, final String emailPassword ) {
		
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets build the new password.
			// we do this by gettign a session object, and taking the first 6 characters.
			HttpSession session = request.getSession(true);
			String newPassWord = session.getId().substring(0,5);
			
			//
			// Lets update the db with the new password.
			//
			String sql = "update gr_users " +
				" set password = des_encrypt(?,'h0t7:16M0TO') " +
				" where email_id = ? ";

			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, newPassWord );
			prepStmt.setString(2, emailId);
			
			prepStmt.execute();
			
			
			
			prepStmt.close();

			// Now lets email the user.
			String toEmailId = emailId;
			String serverName = request.getServerName();
			String messageBody =  
			"\n\nThank You for resetting your password. Your new Password is              " + newPassWord + 
			"\n\nYou can access the system at http://" + serverName + " ";  
		
			//EmailUtil.storeMessage(projectName, toEmailId, messageType, messageBody);
			// lets send the email out to the toEmailId;
			ArrayList to = new ArrayList();
			to.add(toEmailId);
			ArrayList cc = new ArrayList();
			MessagePacket mP = new MessagePacket(to, cc, "Your new TraceCloud Password", messageBody,"");
	
			System.out.println("Sent the Reset Password message : To:  " + toEmailId);
			System.out.println("Sent the Reset Password message : Message :  " + messageBody);
	
			EmailUtil.email(mP , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword );
			
			
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

	
	public static void resetAnotherUsersPassword(String emailId, String newPassword, HttpServletRequest request,
			String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort, final String emailUserId, final String emailPassword ) {
		
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			//
			// Lets update the db with the new password.
			//
			String sql = "update gr_users " +
				" set password = des_encrypt(?,'h0t7:16M0TO') " +
				" where email_id = ? ";

			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, newPassword );
			prepStmt.setString(2, emailId);
			
			prepStmt.execute();
			
			
			
			prepStmt.close();

			// Now lets email the user.
			String toEmailId = emailId;
			String serverName = request.getServerName();
			String messageBody =  
			"\n\nThank You for resetting your password. Your new Password is              " + newPassword + 
			"\n\nYou can access the system at http://" + serverName + " ";  
		
			//EmailUtil.storeMessage(projectName, toEmailId, messageType, messageBody);
			// lets send the email out to the toEmailId;
			ArrayList to = new ArrayList();
			to.add(toEmailId);
			ArrayList cc = new ArrayList();
			MessagePacket mP = new MessagePacket(to, cc, "Your new TraceCloud Password", messageBody,"");
	
			System.out.println("Sent the Reset Password message : To:  " + toEmailId);
			System.out.println("Sent the Reset Password message : Message :  " + messageBody);
	
			EmailUtil.email(mP , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword );
			
			
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

	// returns the last 2 digits of the cc of the user.
	public static String getCCLastTwoDigits(int userId){
		String lastTwo = "";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = "select substr(des_decrypt(cc_number,'h0t7:16M0TO'), -2) 'lastTwo'" +
				" from gr_users " +
				" where id = ? ";

			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, userId);
			rs = prepStmt.executeQuery();
			
			 
			while (rs.next()) {
				lastTwo = rs.getString("lastTwo");
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
		return lastTwo;
	}	
	
	public static String getCCExpiry(int userId){
		String ccExpiry = "";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = "select " +
				" substr(des_decrypt(cc_expire_month,'h0t7:16M0TO'), -2) 'expireMonth' , " +
				" substr(des_decrypt(cc_expire_year,'h0t7:16M0TO'), -2) 'expireYear' " +
				" from gr_users " +
				" where id = ? ";

			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, userId);
			rs = prepStmt.executeQuery();
			
			String expireMonth = "";
			String expireYear = "";
			while (rs.next()) {
				expireMonth = rs.getString("expireMonth");
				expireYear = rs.getString("expireYear");
			}

			ccExpiry = expireMonth + "-" + expireYear;
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
		return ccExpiry;
	}	

	// takes a projectId and userId as a param and returns an arraylist of roles this
	// user has in this project.
	public static ArrayList getRolesInProject(int projectId, int userId) {
		
		ArrayList userRoles = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of Roles that this user is a member of in this project. 
			// Create a Role object for every 
			// report row and pump them into the array list
			// called createRequirementRoles 
			
			String sql = " select r.id, r.project_id, r.name, r.description "  +
				" from gr_user_roles ur, gr_roles r " +
				" where ur.user_id = ?  " + 
				" and ur.project_id = ? " + 
				" and ur.role_id = r.id " ; 

			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, userId);
			prepStmt.setInt(2, projectId);
			rs = prepStmt.executeQuery();
			while (rs.next()){

				int roleId = rs.getInt("id");
				//int projectId = rs.getInt("project_id");
				//we use the folderId we got as a parameter to this constructor.
				// int folderId
				String roleName = rs.getString("name");
				String roleDescription = rs.getString ("description");
				
				Role role = new Role(roleId ,projectId, roleName,roleDescription);
				userRoles.add(role);
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
		return userRoles;
	}
	
	
	
	// returns the list of License Grants that havent' yet been accepted 
	public static ArrayList getMyPendingLicenseGrants(String userEmailId, String databaseType) {
		
		ArrayList grantees = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of users that havent' accepted my offer yet.
		
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "select lg.id, license_type, grantee_email_id, grantor_email_id, " +
					" date_format(grant_dt, '%d %M %Y %r ') \"grant_dt\", notification_sent, grant_state," +
					" o.id \"organization_id\" , o.name, o.description, o.phone_number " +
					" from gr_license_grants lg, gr_organizations o " +
					" where lg.grantor_email_id = ? " +
					" and lg.grantor_email_id = o.contact_email_id " +
					" and lg.grant_state='pending'";
			}
			else {
				sql = "select lg.id, license_type, grantee_email_id, grantor_email_id, " +
				" to_char(grant_dt, 'DD MON YYYY') \"grant_dt\", notification_sent, grant_state," +
				" o.id \"organization_id\" , o.name, o.description, o.phone_number " +
				" from gr_license_grants lg, gr_organizations o " +
				" where lg.grantor_email_id = ? " +
				" and lg.grantor_email_id = o.contact_email_id " +
				" and lg.grant_state='pending'";
			}
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, userEmailId);
			rs = prepStmt.executeQuery();
			while (rs.next()){

				
				int licenseGrantId = rs.getInt("id");
				String licenseType = rs.getString("license_type");
				String grantorEmailId = rs.getString("grantor_email_id");
				String granteeEmailId = rs.getString("grantee_email_id");
				String grantDate  = rs.getString("grant_dt");
				int notificationSent = rs.getInt("notification_sent");
				String grantState = rs.getString("grant_state");
				int grantOrganizationId = rs.getInt("organization_id");
				String grantOrganizationName = rs.getString("name");
				String grantOrganizationDescription = rs.getString("description");
				String grantOrganizationContactPhone = rs.getString("phone_number");
				
				LicenseGrant pendingLicenseGrant = new LicenseGrant(licenseGrantId,licenseType,grantorEmailId,granteeEmailId,
					grantDate, notificationSent, grantState, grantOrganizationId, grantOrganizationName,
					grantOrganizationDescription, grantOrganizationContactPhone);		
				
				grantees.add(pendingLicenseGrant);
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
		return grantees;
	}

	
	
	
	// returns the list of projects that have been licensed by this user.
	public static ArrayList getMyLicensedProjects(User user) {
		
		ArrayList projects = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of users that havent' accepted my offer yet.
			
			String sql = "select  p.id, p.short_name, p.name, p.project_type, p.description," +
				" p.owner, p.website, p.organization, p.tags,  p.restricted_domains," +
				" p.enable_tdcs, p.enable_agile_scrum, p.billing_organization_id, " +
				" p.number_of_requirements, " +
				" p.created_by, p.created_dt, p.last_modified_by, p.last_modified_dt , p.archived, p.hide_priority  " +
				" from gr_projects  p " +
				" where p.billing_organization_id = ? " +
				" order by p.short_name ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, user.getMyOwnedOrganization() );
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int projectId = rs.getInt("id");
				String shortName = rs.getString("short_name");
				String projectName = rs.getString("name");

				String projectType = rs.getString("project_type");
				String projectDescription = rs.getString ("description");
				
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
				//Date createdDt = rs.getDate("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by") ;
				//Date lastModifiedDt = rs.getDate("last_modified_by");
				int archived = rs.getInt("archived");
				int hidePriority = rs.getInt("hide_priority");
				
				
				
				Project project = new Project(projectId, shortName, projectName	, projectType,
					projectDescription, projectOwner, projectWebsite, projectOrganization, projectTags, 
					restrictedDomains, enableTDCS, enableAgileScrum, billingOrganizationId,
					numberOfRequirements, createdBy, lastModifiedBy, archived, hidePriority);
				projects.add(project);
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
		return projects;
	}


	
	public static void captureLogonAttempt(String userEmailId, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "";
			
			if (databaseType.equals("mySQL")){
				sql = " update gr_users set last_logon_dt = now() where email_id = ?   ";
				
			}
			else {
				sql = " update gr_users set last_logon_dt = sysdate where email_id = ?   ";
			}
			
			prepStmt = con.prepareStatement(sql);	
			prepStmt.setString(1, userEmailId);
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
	
	
	
}
