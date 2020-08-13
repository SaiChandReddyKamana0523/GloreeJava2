package com.gloree.beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.naming.InitialContext;
import com.gloree.utils.UserAccountUtil;

// This class is used to store a User object

public class User {

	/**
	 * 
	 */
	private int userId;
	private String ldapUserId;
	private String firstName;
	private String lastName;
	private String emailId;
	private String petsName;
	private String userType;
	private String accountExpireDt;
	private int daysLeft;
	private int billingOrganizationId;
	private int numberOfRequirements;
	private String lastLogonDt;
	
	private int prefRowsPerPage;
	private String prefHideProjects;
	private String reqViewPreference;

	
	
	// The following method is called when the project core values are known and the system is only
	// interested in them. 
	public User (int userId, String ldapUserId, String firstName, String lastName, String emailId, String petsName, 
		String userType, String accountExpireDt, int daysLeft, int billingOrganizationId,
		int numberOfRequirements, int prefRowsPerPage, String prefHideProjects){
		
		this.userId = userId;
		this.ldapUserId = ldapUserId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailId = emailId;
		this.petsName = petsName;
		this.userType = userType;
		this.accountExpireDt = accountExpireDt;
		this.daysLeft = daysLeft;
		this.billingOrganizationId = billingOrganizationId;
		this.numberOfRequirements = numberOfRequirements;
		
		this.prefRowsPerPage = prefRowsPerPage;
		this.prefHideProjects = prefHideProjects;
		
	}
	
	public User (int userId, String databaseType){
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
				sql = "select ldap_user_id, first_name, last_name, email_id, pets_name, user_type, " +
					" date_format(account_expire_dt, '%d %M %Y ') \"account_expire_dt\"," +
					" ifnull(datediff(account_expire_dt, now()),0) \"days_left\"," +
					" billing_organization_id , number_of_requirements, pref_rows_per_page, pref_hide_projects " +
					" from gr_users where id = ? ";
			}
			else {
				sql = "select ldap_user_id, first_name, last_name, email_id, pets_name, user_type, " +
				" to_char(account_expire_dt, 'DD MON YYYY') \"account_expire_dt\"," +
				" nvl((account_expire_dt - sysdate),0) \"days_left\"," +
				" billing_organization_id , number_of_requirements , pref_rows_per_page, pref_hide_projects " +
				" from gr_users where id = ? ";
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,userId);
			ResultSet rs = prepStmt.executeQuery();
			// Only one row should be returned.
			while (rs.next()){
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
				
				// lets create the bean.
				this.ldapUserId = ldapUserId;
				this.userId = userId;
				this.firstName = firstName;
				this.lastName = lastName;
				this.emailId = emailId;
				this.petsName = petsName;
				this.userType = userType;
				this.accountExpireDt = accountExpireDt;
				this.daysLeft = daysLeft;
				this.billingOrganizationId = billingOrganizationId;
				this.numberOfRequirements = numberOfRequirements;
				
				this.prefRowsPerPage = prefRowsPerPage;
				this.prefHideProjects = prefHideProjects;
			
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


	
	// using an extra input argument to differentiate this creator from others. User this 
	// to create a user by his / her access key
	public User (String key , boolean getUserByAccessKey){
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql creates a user account.
			//
			String sql =  "select ldap_user_id, first_name, last_name, email_id, pets_name, user_type, " +
					" date_format(account_expire_dt, '%d %M %Y ') \"account_expire_dt\"," +
					" ifnull(datediff(account_expire_dt, now()),0) \"days_left\"," +
					" billing_organization_id , number_of_requirements, pref_rows_per_page, pref_hide_projects " +
					" from gr_users where api_key = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1,key);
			ResultSet rs = prepStmt.executeQuery();
			// Only one row should be returned.
			while (rs.next()){
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
				
				// lets create the bean.
				this.ldapUserId = ldapUserId;
				this.userId = userId;
				this.firstName = firstName;
				this.lastName = lastName;
				this.emailId = emailId;
				this.petsName = petsName;
				this.userType = userType;
				this.accountExpireDt = accountExpireDt;
				this.daysLeft = daysLeft;
				this.billingOrganizationId = billingOrganizationId;
				this.numberOfRequirements = numberOfRequirements;
				
				this.prefRowsPerPage = prefRowsPerPage;
				this.prefHideProjects = prefHideProjects;
			
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

	
	
	public User (String emailId, String databaseType){
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
				sql = "select id, ldap_user_id, first_name, last_name, pets_name, user_type, " +
					" date_format(account_expire_dt, '%d %M %Y ') \"account_expire_dt\"," +
					" ifnull(datediff(account_expire_dt, now()),0) \"days_left\",  " +
					" billing_organization_id , pref_rows_per_page, pref_hide_projects " +
					" from gr_users where lower(email_id) = ? ";
			}
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1,emailId.toLowerCase());
			ResultSet rs = prepStmt.executeQuery();
			// Only one row should be returned.
			while (rs.next()){
				int userId = rs.getInt("id");
				String ldapUserId = rs.getString("ldap_user_id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String petsName = rs.getString("pets_name");
				String userType = rs.getString("user_type");
				String accountExpireDt = rs.getString("account_expire_dt");
				int daysLeft = rs.getInt("days_left");
				int billingOrganizationId = rs.getInt("billing_organization_id");
				
				int prefRowsPerPage = rs.getInt("pref_rows_per_page");
				String prefHideProjects = rs.getString("pref_hide_projects");
				
				
				// WE set the user to expired if he is on trial
				// and his trial date has expired.
				
				if ( (userType != null) &&  (userType.equals("trial") && (daysLeft <=  0) )) {
					userType = "expired";
				}
				
				// lets create the bean.
				this.ldapUserId = ldapUserId;
				this.userId = userId;
				this.firstName = firstName;
				this.lastName = lastName;
				this.emailId = emailId;
				this.daysLeft = daysLeft;
				this.petsName = petsName;
				this.userType = userType;
				this.accountExpireDt = accountExpireDt;
				
				this.daysLeft = daysLeft;

				this.billingOrganizationId = billingOrganizationId;
				
				this.prefRowsPerPage = prefRowsPerPage;
				this.prefHideProjects = prefHideProjects;
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
	

	// get user by other means. eg ldap use Id
	// since this is clasing with user(emailId) signature, put ina  dummy variable.
	// the smarter thing is to rewrite user(searchstring,searchtype) mode, but
	// no time :( 
	public User (String searchString, String databaseType, String searchType){
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql creates a user account.
			//
			PreparedStatement prepStmt = null;
			ResultSet rs = null;
			if (searchType.equals("ldapUserId")){
				String sql = "";
				if (databaseType.equals("mySQL")){
					sql = "select id, ldap_user_id, first_name, last_name, email_id, pets_name, user_type, " +
						" date_format(account_expire_dt, '%d %M %Y ') \"account_expire_dt\"," +
						" ifnull(datediff(account_expire_dt, now()),0) \"days_left\",  " +
						" billing_organization_id  , pref_rows_per_page, pref_hide_projects  " +
						" from gr_users where ldap_user_id = ? ";
				}
				else {
					sql = "select id, ldap_user_id, first_name, last_name, email_id, pets_name, user_type, " +
					" to_char(account_expire_dt, 'DD MON YYYY') \"account_expire_dt\"," +
					" nvl((account_expire_dt - sysdate),0) \"days_left\",  " +
					" billing_organization_id  , pref_rows_per_page, pref_hide_projects  " +
					" from gr_users where ldap_user_id = ? ";
				}
				prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1,searchString);
				rs = prepStmt.executeQuery();
			}
			// Only one row should be returned.
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
				

				int prefRowsPerPage = rs.getInt("pref_rows_per_page");
				String prefHideProjects = rs.getString("pref_hide_projects");
				
				
				// WE set the user to expired if he is on trial
				// and his trial date has expired.
				
				if ( (userType != null) &&  (userType.equals("trial") && (daysLeft <=  0) )) {
					userType = "expired";
				}
				
				// lets create the bean.
				this.ldapUserId = ldapUserId;
				this.userId = userId;
				this.firstName = firstName;
				this.lastName = lastName;
				this.emailId = emailId;
				this.daysLeft = daysLeft;
				this.petsName = petsName;
				this.userType = userType;
				this.accountExpireDt = accountExpireDt;
				
				this.daysLeft = daysLeft;

				this.billingOrganizationId = billingOrganizationId;
				
				this.prefRowsPerPage = prefRowsPerPage;
				this.prefHideProjects = prefHideProjects;
			
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
	

	public int getUserId(){
		return this.userId;
	}
	
	public String getLdapUserId(){
		return this.ldapUserId;
	}
	
	public String getFirstName(){
		return this.firstName;
	}
	
	
	public String getLastName() {
		return this.lastName;
	}
	
	public String getEmailId() {
		return this.emailId;
	}
	
	public String getPetsName() {
		return this.petsName;
	}
	
	public String getUserType() {
		return this.userType;
	}
	
	public String getAccountExpireDt() {
		return this.accountExpireDt;
	}
	
	
	public int getDaysLeft() {
		return this.daysLeft;
	}
	
	public ArrayList getUserRolesInProject(int projectId) {
		ArrayList rolesInProject = UserAccountUtil.getRolesInProject(projectId, this.userId );
		return rolesInProject;
	}	

	public int getBillingOrganizationId() {
		return this.billingOrganizationId;
	}
	
	public int getNumberOfRequirements() {
		return this.numberOfRequirements;
	}
	
	public void setLastLogonDt(String lastLogonDt) {
		this.lastLogonDt = lastLogonDt;
	}
	
	
	public String getLastLogonDt() {
		if (this.lastLogonDt == null ){
			this.lastLogonDt = "";
		}
		return this.lastLogonDt;
	}
	
	public int getPrefRowsPerPage(){
		return this.prefRowsPerPage;
	}
	
	public String getPrefHideProjects(){
		if (this.prefHideProjects == null ){
			this.prefHideProjects = "";
		}
		return this.prefHideProjects;
	}

	public String  getPrefHealthCheckDays (){
		String prefHealthCheckDays = "";
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select pref_healthcheck_days " + 
				" from gr_users where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,this.userId);
			ResultSet rs = prepStmt.executeQuery();
			// Only one row should be returned.
			while (rs.next()){
				prefHealthCheckDays = rs.getString("pref_healthcheck_days");
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
		return prefHealthCheckDays;
	}


	public void  setPrefHealthCheckDays (String prefHealthCheckDays){
		
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " update gr_users set pref_healthcheck_days = '"+ prefHealthCheckDays +"' " + 
				" where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,this.userId);
			prepStmt.execute();
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
	
	public String  getReqViewPreference (){
		if (this.reqViewPreference == null || this.reqViewPreference.equals("")){
			// lets get from db
			String reqViewPreference = "";
			java.sql.Connection con =  null;
			try {
				
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();

				String sql = "select reqViewPreference " + 
					" from gr_users where id = ? ";
				
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1,this.userId);
				ResultSet rs = prepStmt.executeQuery();
				// Only one row should be returned.
				while (rs.next()){
					reqViewPreference = rs.getString("reqViewPreference");
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
			this.reqViewPreference = reqViewPreference;
		}
		
		
		return this.reqViewPreference;
	}


	public void  setReqViewPreference (String reqViewPreference){
		
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " update gr_users set reqViewPreference = '"+ reqViewPreference +"' " + 
				" where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,this.userId);
			prepStmt.execute();
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
		this.reqViewPreference = reqViewPreference; 
	}
	

	public String  getPrefHealthCheckHideProjects (){
		String prefHealthCheckHideProjects = "";
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select pref_healthcheck_hide_projects " + 
				" from gr_users where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,this.userId);
			ResultSet rs = prepStmt.executeQuery();
			// Only one row should be returned.
			while (rs.next()){
				prefHealthCheckHideProjects = rs.getString("pref_healthcheck_hide_projects");
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
		if (prefHealthCheckHideProjects == null ){
			prefHealthCheckHideProjects = "";
		}
		return prefHealthCheckHideProjects;
	}

	

	public void  setPrefHealthCheckHideProjects(String prefHealthCheckHideProjects){
		
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " update gr_users set pref_healthcheck_hide_projects = '"+ prefHealthCheckHideProjects +"' " + 
				" where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,this.userId);
			prepStmt.execute();
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
	

	
	// returns the id of the org the user owns.
	// it can be 0 too , if the user is not an owner of an Org.
	public int  getMyOwnedOrganization (){
		int organizationId = 0;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the org owned by this user. which is different from the billing or.
			//
			
			String sql = "select id " + 
				" from gr_organizations where contact_id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,this.userId);
			ResultSet rs = prepStmt.executeQuery();
			// Only one row should be returned.
			while (rs.next()){
				organizationId = rs.getInt("id");
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
		return organizationId;
	}
	
	// If someone has granted me a license that I have not accepted, it returns 
	// a LicenseGrant object. It may return null too.
	public LicenseGrant getMyPendingLicenseGrant (String databaseType){
		LicenseGrant pendingLicenseGrant = null; 
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the org owned by this user. which is different from the billing or.
			//
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "select lg.id, license_type, grantee_email_id, grantor_email_id, " +
					" date_format(grant_dt, '%d %M %Y %r ') \"grant_dt\", notification_sent, grant_state," +
					" o.id \"organization_id\" , o.name, o.description, o.phone_number " +
					" from gr_license_grants lg, gr_organizations o " +
					" where lg.grantee_email_id = ? " +
					" and lg.grantor_email_id = o.contact_email_id " +
					" and lg.grant_state='pending'";
			}
			else {
				sql = "select lg.id, license_type, grantee_email_id, grantor_email_id, " +
				" to_char(grant_dt, 'DD MON YYYY') \"grant_dt\", notification_sent, grant_state," +
				" o.id \"organization_id\" , o.name, o.description, o.phone_number " +
				" from gr_license_grants lg, gr_organizations o " +
				" where lg.grantee_email_id = ? " +
				" and lg.grantor_email_id = o.contact_email_id " +
				" and lg.grant_state='pending'";
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1,this.emailId);
			ResultSet rs = prepStmt.executeQuery();
			// Only one row should be returned.
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
				
				pendingLicenseGrant = new LicenseGrant(licenseGrantId,licenseType,grantorEmailId,granteeEmailId,
					grantDate, notificationSent, grantState, grantOrganizationId, grantOrganizationName,
					grantOrganizationDescription, grantOrganizationContactPhone);		
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
		return pendingLicenseGrant ;
	}
	
	
	// rejects a license granted by a grantor
	public void rejectLicenseGrant (){
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = " update  gr_license_grants lg " +
				" set grant_state = 'rejected' " +
				" where grantee_email_id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1,this.emailId);
			prepStmt.execute();
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
	
	
	// accepts a license granted by a grantor
	public void acceptLicenseGrant (String databaseType){
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			
			// lets get this user's pending license grant info
			LicenseGrant pendingLicenseGrant = this.getMyPendingLicenseGrant(databaseType);
			String sql = " update  gr_users  " +
				" set user_type = ? , " +
				" cc_full_name = null ," +
				" cc_type = null, " +
				" cc_number = null, " +
				" cc_expire_month = null, " +
				" cc_expire_year = null, " +
				" cc_verification_number = null, " +
				" cc_billing_address = null, " +
				" cc_billing_zipcode = null, " +
				" cc_billing_country = null , " +
				" billing_organization_id = ? " +
				" where email_id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1,pendingLicenseGrant.getLicenseType());
			prepStmt.setInt(2, pendingLicenseGrant.getGrantOrganizationId());
			prepStmt.setString(3, this.emailId);
			prepStmt.execute();
			prepStmt.close();
			
			
			// lets wipe out all the pending offers for this user.
			sql = " update  gr_license_grants lg " +
				" set grant_state = 'accepted' " +
				" where grantee_email_id = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1,this.emailId);
			prepStmt.execute();
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
	
	
	public ArrayList getAllUsersOnSite (String databaseType, String licenseType){
		
		ArrayList members = new ArrayList();
		
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql get all users who have an account on this site installation and have a certain licenseType
			//
			
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "select distinct u.id, u.ldap_user_id, u.first_name, u.last_name, u.email_id," +
					" u.pets_name, u.user_type, " +
					" date_format(u.account_expire_dt, '%d %M %Y ')  \"account_expire_dt\" , " +
					" ifnull(datediff(u.account_expire_dt, now()),0) \"days_left\", " +
					" u.billing_organization_id , u.number_of_requirements , u.last_logon_dt " +
					" , pref_rows_per_page, pref_hide_projects " +
					" from  gr_users u " + 
					" order by u.email_id ";
			}
			else {
				sql = "select distinct u.id, u.ldap_user_id, u.first_name, u.last_name, u.email_id," +
				" u.pets_name, u.user_type, " +
				" to_char(u.account_expire_dt, 'DD MON YYYY')  \"account_expire_dt\" , " +
				" nvl((u.account_expire_dt - sysdate),0) \"days_left\", " +
				" u.billing_organization_id , u.number_of_requirements, u.last_logon_dt " +
				" , pref_rows_per_page, pref_hide_projects  " +
				" from  gr_users u " + 
				" order by u.email_id ";
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
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
				String lastLogonDt = rs.getString("last_logon_dt");
				
				int prefRowsPerPage = rs.getInt("pref_rows_per_page");
				String prefHideProjects = rs.getString("pref_hide_projects");
				
				// WE set the user to expired if he is on trial
				// and his trial date has expired.
				
				
				
				if ( (userType != null) &&  (userType.equals("trial") && (daysLeft <=  0) )) {
					userType = "expired";
				}
	
				if (licenseType.equals(userType)){
					User user = new User (userId, ldapUserId, firstName, lastName, emailId, petsName,
						userType, accountExpireDt,  daysLeft, billingOrganizationId, 
						numberOfRequirements, prefRowsPerPage, prefHideProjects);
					user.setLastLogonDt(lastLogonDt);
					
					members.add(user);
				}
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
	
	public int setPrefRowsPerPage (int prefRowsPerPage){
		
		
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql =  " update gr_users set pref_rows_per_page = ? " +
					" where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, prefRowsPerPage);
			prepStmt.setInt(2, this.getUserId());
			prepStmt.execute();
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
		
		return prefRowsPerPage;
	}	

	public void setPrefHideProjects (String prefHideProjects){
		
		
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql =  " update gr_users set pref_hide_projects = ? " +
					" where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, prefHideProjects);
			prepStmt.setInt(2, this.getUserId());
			prepStmt.execute();
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
		
		return ;
	}	
	
}
