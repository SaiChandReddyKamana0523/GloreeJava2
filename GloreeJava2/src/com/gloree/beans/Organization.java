 package com.gloree.beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.naming.InitialContext;



// This class is used to store a Organization object

public class Organization {

	/**
	 * 
	 */
	private int organizationId;
	private int contactId;
	private String contactEmailId;
	private String name;
	private String description;
	private String phoneNumber;
	private int readWriteLicenses;
	private int readOnlyLicenses; 
		
	
	// The following method is called when the Org core values are known and the system is only
	// interested in them. 
	public Organization (int organizationId, int contactId, String contactEmailId,
		String name, String description, String phoneNumber, int readWriteLicenses,
		int readOnlyLicenses){
		this.organizationId = organizationId;
		this.contactId = contactId;
		this.contactEmailId = contactEmailId;
		this.name = name;
		this.description = description;
		this.phoneNumber = phoneNumber;
		this.readWriteLicenses = readWriteLicenses;
		this.readOnlyLicenses = readOnlyLicenses;
		
	}
	
	public Organization (int organizationId){
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql creates a organization account.
			//
			String sql = "select id, contact_id, contact_email_id, name, description, " +
				"phone_number, read_write_licenses, read_only_licenses" +
				" from gr_organizations where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,organizationId);
			ResultSet rs = prepStmt.executeQuery();
			// Only one row should be returned.
			while (rs.next()){				
				
				this.organizationId = rs.getInt("id");
				this.contactId = rs.getInt("contact_id");
				this.contactEmailId = rs.getString("contact_email_id");
				this.name = rs.getString("name");
				this.description = rs.getString("description");
				this.phoneNumber = rs.getString("phone_number");
				this.readWriteLicenses = rs.getInt("read_write_licenses");
				this.readOnlyLicenses = rs.getInt("read_only_licenses");				
			
			}
		
			rs.close();
			prepStmt.close();
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


	public int getOrganizationId(){
		return this.organizationId;
	}

	public int getContactId(){
		return this.contactId;
	}
	
	public String getContactEmailId () {
		return this.contactEmailId;
	}
	
	public String getName () {
		return this.name;
	}
	
	public String getDescription () {
		return this.description;
	}
	
	public String getPhoneNumber () {
		return this.phoneNumber;
	}
	
	public int getReadWriteLicenses () {
		return this.readWriteLicenses;
	}
	
	public int getReadOnlyLicenses () {
		return this.readOnlyLicenses;
	}
	
	public User getContactUser (String databaseType){
		User user = new User (this.getContactId(),  databaseType);
		return user;
	}
	
	public ArrayList getAllUsersInOrganization (String databaseType){
		
		ArrayList members = new ArrayList();
		
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql get all users who have this licenses paid by this organizaation.
			//
			
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "select distinct u.id, u.ldap_user_id, u.first_name, u.last_name, u.email_id," +
					" u.pets_name, u.user_type, " +
					" date_format(u.account_expire_dt, '%d %M %Y ')  \"account_expire_dt\" , " +
					" ifnull(datediff(u.account_expire_dt, now()),0) \"days_left\", " +
					" u.billing_organization_id , u.number_of_requirements " +
					" , pref_rows_per_page, pref_hide_projects" +
					" from  gr_users u " + 
					" where u.billing_organization_id = ?  " +
					" order by u.email_id ";
			}
			else {
				sql = "select distinct u.id, u.ldap_user_id, u.first_name, u.last_name, u.email_id," +
				" u.pets_name, u.user_type, " +
				" to_char(u.account_expire_dt, 'DD MON YYYY')  \"account_expire_dt\" , " +
				" nvl((u.account_expire_dt - sysdate),0) \"days_left\", " +
				" u.billing_organization_id , u.number_of_requirements " +
				" , pref_rows_per_page, pref_hide_projects" +
				" from  gr_users u " + 
				" where u.billing_organization_id = ?  " +
				" order by u.email_id ";
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.organizationId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
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
					userType, accountExpireDt,  daysLeft, billingOrganizationId, 
					numberOfRequirements , prefRowsPerPage, prefHideProjects);
				members.add(user);
			}
			rs.close();
			prepStmt.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}   finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
		return members;
	}
	
	//returns the number of used Read Write licenese. 
	// this is the sum of accepted + offered licesnse.
	public int getNumOfUsedReadWriteLicenses (){
		
		int numOfUsedReadWriteLicenses = 0; 
		
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql get all users who have this licenses paid by this organizaation.
			//
			
			int acceptedReadWriteLicenses = 0;
			int offeredReadWriteLicenses = 0;
			
			String sql = "select count(*) \"acceptedLicenses\"" +
				" from gr_users " +
				" where billing_organization_id = ? " +
				" and user_type = ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.organizationId);
			prepStmt.setString(2, "readWrite");
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				acceptedReadWriteLicenses  = rs.getInt("acceptedLicenses");
			}
			rs.close();
			prepStmt.close();
			
			// lets get offeredReadWriteLicenses
			// get the list of ReadWrite licenses offered by this org's contact person.
			sql = "select count(*) \"offeredLicenses\" from gr_organizations o, " + 
				" gr_license_grants lg " +
				" where o.contact_email_id = lg.grantor_email_id " +
				" and o.id =  ? "+ 
				" and grant_state = 'pending' " +
				" and license_type = ? "; 
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.organizationId);
			prepStmt.setString(2, "readWrite");
			rs = prepStmt.executeQuery();
			while (rs.next()){
				offeredReadWriteLicenses  = rs.getInt("offeredLicenses");
			}
			rs.close();
			prepStmt.close();
			
			numOfUsedReadWriteLicenses = acceptedReadWriteLicenses + offeredReadWriteLicenses ;
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}   finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
		return numOfUsedReadWriteLicenses;
	}	

	//returns the number of used Read Only licenese.
	public int getNumOfUsedReadOnlyLicenses (){
		
		int numOfUsedReadOnlyLicenses = 0; 
		
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql get all users who have this licenses paid by this organizaation.
			//
			
			int acceptedReadOnlyLicenses = 0;
			int offeredReadOnlyLicenses = 0;
			
			String sql = "select count(*) \"usedLicenses\"" +
				" from gr_users " +
				" where billing_organization_id = ? " +
				" and user_type = ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.organizationId);
			prepStmt.setString(2, "readOnly");
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				acceptedReadOnlyLicenses = rs.getInt("usedLicenses");
			}
			rs.close();
			prepStmt.close();
			
			// lets get offeredReadOnlyLicenses
			// get the list of ReadOnlylicenses offered by this org's contact person.
			sql = "select count(*) \"offeredLicenses\" from gr_organizations o, " + 
				" gr_license_grants lg " +
				" where o.contact_email_id = lg.grantor_email_id " +
				" and o.id =  ? "+ 
				" and grant_state = 'pending' " +
				" and license_type = ? "; 
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.organizationId);
			prepStmt.setString(2, "readOnly");
			rs = prepStmt.executeQuery();
			while (rs.next()){
				offeredReadOnlyLicenses  = rs.getInt("offeredLicenses");
			}
			rs.close();
			prepStmt.close();
			
			numOfUsedReadOnlyLicenses = acceptedReadOnlyLicenses + offeredReadOnlyLicenses ;

			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}   finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
		return numOfUsedReadOnlyLicenses;
	}		

	//returns an arraylist of projectid of projects sponsored by this organization.
	public ArrayList getSponsoredProjects (){
		
		ArrayList sponsoredProjects = new ArrayList();
		
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// all projects that are sponsored by this ord.
			
			String sql = "select id " +
				" from gr_projects  " +
				" where billing_organization_id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.organizationId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				int projectId = rs.getInt("id");
				sponsoredProjects.add(new Integer(projectId));
			}
			rs.close();
			prepStmt.close();
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}   finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
		return sponsoredProjects;
	}		

}
