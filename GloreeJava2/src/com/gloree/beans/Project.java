package com.gloree.beans;
//GloreeJava2

import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;

import com.gloree.utils.ProjectUtil;

//
// This class is used to create an instance of Project and has all the data related to the core project.
//

//TODO : at some point enhance this so that myFolders lists ALL folders (along with their levels and parent folder ids, so that the navigator can display them
// or they can be used in Create Requirement or move Folder options.

public class Project implements java.io.Serializable  {

	private int projectId;
	private String shortName = "" ;
	private String projectName = "" ;
	private String projectType = "";
	private String projectDescription = "" ;
	private String projectOwner = "" ;
	private String projectWebsite = "" ;
	private String projectOrganization = "" ;
	private String projectTags = "" ;
	private int percentageCompleteDriverReqTypeId = -1;
	private String powerUserSettings = "";
	private String restrictedDomains = "" ;
	private int enableTDCS ;
	private int enableAgileScrum ;
	private int billingOrganizationId;
	private int numberOfRequirements;
	private String createdBy = "" ;
	//private Date createdDt;
	private String lastModifiedBy = "" ;
	//private Date lastModifiedDt;
	private ArrayList myFolders ;
	private ArrayList myRequirementTypes;
	private ArrayList<User> members;
	private ArrayList integrationMenus;
	private int archived;
	private int hidePriority;
	
	private String hideFromHealthBar;
	
	private String foldersEnabledForApprovalWorkFlow ; 
	
	// The following method is called when the project core values are known and the system is only
	// interested in them. 
	public Project(int projectId, String shortName, String projectName, String projectType, String projectDescription, 
		String projectOwner, String projectWebsite, String projectOrganization, String projectTags,
		String restrictedDomains, int enableTDCS, int enableAgileScrum,
		int billingOrganizationId, int numberOfRequirements, 
		String createdBy, String lastModifiedBy, int archived, int hidePriority){
		
		this.projectId = projectId;
		this.shortName = shortName;
		this.projectType = projectType;
		this.projectName = projectName;
		this.projectDescription = projectDescription;
		
		this.projectOwner = projectOwner;
		this.projectWebsite = projectWebsite;
		this.projectOrganization = projectOrganization;
		this.projectTags = projectTags;
		
		
		this.restrictedDomains = restrictedDomains;
		this.enableTDCS = enableTDCS;
		this.enableAgileScrum = enableAgileScrum;
		this.billingOrganizationId = billingOrganizationId;
		this.numberOfRequirements = numberOfRequirements;
		
		this.createdBy = createdBy;
		//this.createdDt = createdDt;
		this.lastModifiedBy = lastModifiedBy;
		this.archived = archived;
		this.hidePriority = hidePriority;
	}
	//this.lastModifiedDt = lastModifiedDt;

	// the following method is used when the system knows only the projectId and wants this bean
	// to go and get the project core info along with package and requirement type values of this project.
	public Project (int projectId, String databaseType) {

		java.sql.Connection con = null;
		ArrayList myFolders = new ArrayList();
		ArrayList myRequirementTypes = new ArrayList();
		ArrayList<User> members = new ArrayList<User>();
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select id, short_name, name, project_type, description, " +
				" owner, website, organization, tags,  pct_complete_driver_requirement_type_id, power_user_settings, " +
				" restricted_domains, enable_tdcs, enable_agile_scrum, " +
				" billing_organization_id, " +
				" number_of_requirements, created_by, created_dt, last_modified_by, last_modified_dt, archived,"
				+ "  hide_priority, hide_from_health_bar  " +
				" from gr_projects " +
				" where id = ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			ResultSet rs = prepStmt.executeQuery();
			
			if (rs.next()){
				this.projectId = rs.getInt("id");
				this.shortName = rs.getString("short_name");
				this.projectName = rs.getString("name");
				this.projectType = rs.getString("project_type");
				this.projectDescription = rs.getString ("description");
				
				this.projectOwner = rs.getString("owner");
				this.projectWebsite = rs.getString("website");
				this.projectOrganization = rs.getString("organization");
				this.projectTags = rs.getString("tags");
				this.percentageCompleteDriverReqTypeId = rs.getInt("pct_complete_driver_requirement_type_id");
				this.powerUserSettings = rs.getString("power_user_settings");
				
				this.restrictedDomains = rs.getString("restricted_domains");
				this.enableTDCS = rs.getInt("enable_tdcs");
				this.enableAgileScrum = rs.getInt("enable_agile_scrum");
				this.billingOrganizationId = rs.getInt("billing_organization_id");
				this.numberOfRequirements = rs.getInt("number_of_requirements");
				this.createdBy = rs.getString("created_by");
				//this.createdDt = rs.getDate("created_dt");
				this.lastModifiedBy = rs.getString("last_modified_by") ;
				//this.lastModifiedDt = rs.getDate("last_modified_by");
				this.archived = rs.getInt("archived");
				
				this.hidePriority = rs.getInt("hide_priority");
				this.hideFromHealthBar = rs.getString("hide_from_health_bar");
				
			}

			// LEVEL1 Folder fill up starts here.
			// get the list of folders for this project, create folder objects for each folder and put them in the array list
			// called Folders.
			sql = "SELECT f.id, f.project_id, f.name, f.description, f.parent_folder_id, f.folder_level," +
				" f.folder_order, f.folder_path, "  + 
				" f.requirement_type_id, rt.name \"requirement_type_name\", f.created_by, f.created_dt, f.last_modified_by, " +
				" f.last_modified_dt " +
				" FROM gr_folders f, gr_requirement_types rt" +
				" where f.requirement_type_id = rt.id and  f.project_id = ? and f.folder_level = 1 " +
				" order by rt.display_sequence, f.folder_order, f.name";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int folderId = rs.getInt("id");
				// we use the projectId we got as a parameter to this constructor.
				String folderName = rs.getString("name");
				String folderDescription = rs.getString ("description");
				int parentFolderId = rs.getInt("parent_folder_id");
				int folderLevel = rs.getInt("folder_level");
				int folderOrder = rs.getInt("folder_order");
				String folderPath = rs.getString("folder_path");
				int requirementTypeId = rs.getInt("requirement_type_id");
				String requirementTypeName = rs.getString("requirement_type_name");
				String createdBy = rs.getString("created_by");
				//Date createdDt = rs.getDate("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by") ;
				//Date lastModifiedDt = rs.getDate("last_modified_by");
				
				//TODO : at some point see how we can make DATE fields works.
				Folder folder = new Folder(folderId, projectId, folderName, folderDescription, 
						parentFolderId, folderLevel, folderOrder, folderPath,
						requirementTypeId, requirementTypeName, createdBy, lastModifiedBy);
				myFolders.add(folder);
				
				
			
				
				// LEVEL2 Folder fill up starts here.				
				// Now for each one of these Folders i.e. level1FolderId, find children and add them to the myFolders list.
				// reset the parentFolder Id
				parentFolderId = folderId;
				
				sql = "SELECT f.id, f.project_id, f.name, f.description, f.parent_folder_id," +
				" f.folder_level, f.folder_order,  f.folder_path, "  + 
				" f.requirement_type_id, rt.name \"requirement_type_name\", f.created_by, f.created_dt, f.last_modified_by, " +
				" f.last_modified_dt FROM gr_folders f, gr_requirement_types rt" +
				" where f.requirement_type_id = rt.id " +
				" and f.parent_folder_id = ? " +
				" order by f.folder_order, f.name";
			
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, parentFolderId);
				ResultSet rs2 = prepStmt.executeQuery();
				while (rs2.next()){
					folderId = rs2.getInt("id");
					// we use the projectId we got as a parameter to this constructor.
					folderName = rs2.getString("name");
					folderDescription = rs2.getString ("description");
					parentFolderId = rs2.getInt("parent_folder_id");
					folderLevel = rs2.getInt("folder_level");
					folderOrder = rs2.getInt("folder_order");
					folderPath = rs2.getString("folder_path");
					requirementTypeId = rs2.getInt("requirement_type_id");
					requirementTypeName = rs2.getString("requirement_type_name");
					createdBy = rs2.getString("created_by");
					//	Date createdDt = rs2.getDate("created_dt");
					lastModifiedBy = rs2.getString("last_modified_by") ;
					//Date lastModifiedDt = rs2.getDate("last_modified_by");
				
					//TODO : at some point see how we can make DATE fields works.
					folder = new Folder(folderId, projectId, folderName, folderDescription, 
							parentFolderId, folderLevel, folderOrder, folderPath,
							requirementTypeId, requirementTypeName, createdBy, lastModifiedBy);
					
					myFolders.add(folder);
					

					// LEVEL3 Folder fill up starts here.
					// Now for each one of these Folders i.e. level1FolderId, find children and add them to the myFolders list.
					
					// reset the parentFolder Id
					parentFolderId = folderId;
					
					sql = "SELECT f.id, f.project_id, f.name, f.description, f.parent_folder_id," +
					" f.folder_level, f.folder_order, f.folder_path, "  + 
					" f.requirement_type_id, rt.name \"requirement_type_name\", f.created_by, f.created_dt, f.last_modified_by, " +
					" f.last_modified_dt FROM gr_folders f, gr_requirement_types rt" +
					" where f.requirement_type_id = rt.id " +
					" and f.parent_folder_id = ? " +
					" order by f.folder_order, f.name";
				
				
					prepStmt = con.prepareStatement(sql);
					prepStmt.setInt(1, parentFolderId);
					ResultSet rs3 = prepStmt.executeQuery();
					while (rs3.next()){
						folderId = rs3.getInt("id");
						// we use the projectId we got as a parameter to this constructor.
						folderName = rs3.getString("name");
						folderDescription = rs3.getString ("description");
						parentFolderId = rs3.getInt("parent_folder_id");
						folderLevel = rs3.getInt("folder_level");
						folderOrder = rs3.getInt("folder_order");
						folderPath = rs3.getString("folder_path");
						requirementTypeId = rs3.getInt("requirement_type_id");
						requirementTypeName = rs3.getString("requirement_type_name");
						createdBy = rs3.getString("created_by");
						//	Date createdDt = rs3.getDate("created_dt");
						lastModifiedBy = rs3.getString("last_modified_by") ;
						//Date lastModifiedDt = rs3.getDate("last_modified_by");
					
						//TODO : at some point see how we can make DATE fields works.
						folder = new Folder(folderId, projectId, folderName, folderDescription, 
								parentFolderId, folderLevel, folderOrder, folderPath,
								requirementTypeId, requirementTypeName, createdBy, lastModifiedBy);
						myFolders.add(folder);

					
						// LEVEL4 Folder fill up starts here.
						// Now for each one of these Folders i.e. level1FolderId, find children and add them to the myFolders list.
						
						// reset the parentFolder Id
						parentFolderId = folderId;
						
						
						sql = "SELECT f.id, f.project_id, f.name, f.description, f.parent_folder_id," +
						" f.folder_level, f.folder_order, f.folder_path, "  + 
						" f.requirement_type_id, rt.name \"requirement_type_name\", f.created_by, f.created_dt, f.last_modified_by, " +
						" f.last_modified_dt FROM gr_folders f, gr_requirement_types rt" +
						" where f.requirement_type_id = rt.id " +
						" and f.parent_folder_id = ? " +
						" order by f.name";
						prepStmt = con.prepareStatement(sql);
						prepStmt.setInt(1, parentFolderId);
						ResultSet rs4 = prepStmt.executeQuery();
						while (rs4.next()){
							folderId = rs4.getInt("id");
							// we use the projectId we got as a parameter to this constructor.
							folderName = rs4.getString("name");
							folderDescription = rs4.getString ("description");
							parentFolderId = rs4.getInt("parent_folder_id");
							folderLevel = rs4.getInt("folder_level");
							folderOrder = rs4.getInt("folder_order");
							folderPath = rs4.getString("folder_path");
							requirementTypeId = rs4.getInt("requirement_type_id");
							requirementTypeName = rs4.getString("requirement_type_name");
							createdBy = rs4.getString("created_by");
							//	Date createdDt = rs4.getDate("created_dt");
							lastModifiedBy = rs4.getString("last_modified_by") ;
							//Date lastModifiedDt = rs4.getDate("last_modified_by");
						
							//TODO : at some point see how we can make DATE fields works.
							folder = new Folder(folderId, projectId, folderName, folderDescription,
									parentFolderId, folderLevel, folderOrder, folderPath,
									requirementTypeId, requirementTypeName, createdBy, lastModifiedBy);

							myFolders.add(folder);

							
						}
						// LEVEL4 Folder fill up ends here.
					}
					// LEVEL3 Folder fill up ends here.
				}
				// LEVEL2 Folder fill up ends here.
				
			}
			// LEVEL1 Folder fill up ends here.
			
			this.myFolders = myFolders;
			
			// get the list of requirement types for this project, create requirement type objects 
			// for each folder and put them in the array list
			// called myRequirmentTypes.
			
			// NOTE : to get the Requirement Types sorted in the same order as folder orders, 
			// we have this query. it takes advantage of the business rule that every req type
			// has a corresponding root level folder.
		
			sql = "SELECT rt.id, rt.project_id, rt.short_name, rt.name, rt.description," +
				" rt.display_sequence, rt.created_by, rt.enable_approval, rt.enable_agile_scrum, rt.can_be_dangling, " +
				" rt.can_be_orphan, rt.can_not_trace_to,  " +
				" rt.created_dt, rt.last_modified_by, rt.last_modified_dt " + 
				" FROM gr_requirement_types rt, gr_folders f " +
				" where rt.project_id = ? " +
				" and rt.id = f.requirement_type_id " + 
				" and f.folder_level =1 " + 
				" order by rt.display_sequence, f.folder_order ";
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int requirementTypeId = rs.getInt("id");
				// we use the projectId we got as a parameter to this constructor.
				String requirementTypeShortName = rs.getString("short_name");
				String requirementTypeName = rs.getString("name");
				String requirementTypeDescription = rs.getString ("description");
				int requirementTypeDisplaySequence = rs.getInt("display_sequence");
				int requirementTypeEnableApproval = rs.getInt("enable_approval");
				int requirementTypeEnableAgileScrum = rs.getInt("enable_agile_scrum");
				
				int requirementTypeCanBeDangling = rs.getInt("can_be_dangling");
				int requirementTypeCanBeOrphan = rs.getInt("can_be_orphan");
				String requirementTypeCanNotTraceTo = rs.getString("can_not_trace_to");
				String createdBy = rs.getString("created_by");
				//Date createdDt = rs.getDate("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by") ;
				//Date lastModifiedDt = rs.getDate("last_modified_by");
				
				RequirementType requirementType = new RequirementType(requirementTypeId, projectId,
				requirementTypeShortName, 
				requirementTypeName, requirementTypeDescription, requirementTypeDisplaySequence, 
				requirementTypeEnableApproval, requirementTypeEnableAgileScrum,
				requirementTypeCanBeDangling, requirementTypeCanBeOrphan, requirementTypeCanNotTraceTo,
				createdBy, lastModifiedBy);
				myRequirementTypes.add(requirementType);
			}
			
			this.myRequirementTypes = myRequirementTypes;
			prepStmt.close();
			rs.close();

			
			// lets get all the members of this project.
			if (databaseType.equals("mySQL")){
				sql = "select distinct u.id, u.ldap_user_id, u.first_name, u.last_name, u.email_id, u.pets_name, u.user_type, " +
					" date_format(u.account_expire_dt, '%d %M %Y ')  \"account_expire_dt\" , " +
					" ifnull(datediff(u.account_expire_dt, now()),0) \"days_left\", " +
					" u.billing_organization_id , u.number_of_requirements " +
					" , pref_rows_per_page, pref_hide_projects " +
					" from gr_user_roles ur, gr_users u " + 
					" where ur.project_id = ?  " +
					" and ur.user_id = u.id " +
					" order by u.last_name, u.first_name ";
			}
			else {
				sql = "select distinct u.id, u.ldap_user_id, u.first_name, u.last_name, u.email_id, u.pets_name, u.user_type, " +
				" to_char(u.account_expire_dt, 'DD MON YYYY')  \"account_expire_dt\" , " +
				" nvl((u.account_expire_dt - sysdate),0) \"days_left\", " +
				" u.billing_organization_id , u.number_of_requirements  " +
				" , pref_rows_per_page, pref_hide_projects " +
				" from gr_user_roles ur, gr_users u " + 
				" where ur.project_id = ?  " +
				" and ur.user_id = u.id " +
				" order by  u.last_name, u.first_name";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();
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
				members.add(user);
			}
			this.members = members;	
				
			// lets get all the integration menus for this project.
			sql = "select id, project_id, menu_type, menu_label, menu_value" +
				" from gr_project_integration_menu " +
				" where project_id = ? ";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.projectId);
			rs = prepStmt.executeQuery();
			ArrayList integrationMenus = new ArrayList();
			while (rs.next()){
				int id = rs.getInt("id");
				String menuType = rs.getString("menu_type");
				String menuLabel = rs.getString("menu_label");
				String menuValue = rs.getString("menu_value");
				
				IntegrationMenu integrationMenu = new IntegrationMenu(id, projectId, menuType, menuLabel, menuValue);
				integrationMenus.add(integrationMenu);
			}
			this.integrationMenus = integrationMenus;	
						
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
	public int getProjectId(){
		return this.projectId;
	}
	
	public String getShortName(){
		return this.shortName;
	}
	
	public String getProjectName () {
		return this.projectName;
	}
	
	

	public String getProjectType () {
		return this.projectType;
	}
	
	
	public String getProjectDescription () {
		return this.projectDescription;
	}

	public String getProjectOwner() {
		if (this.projectOwner == null){
			this.projectOwner = "";
		}
		return this.projectOwner;
	}
	

	public String getProjectWebsite() {
		if (this.projectWebsite == null){
			this.projectWebsite = "";
		}
		return this.projectWebsite;
	}
	

	public String getProjectOrganization() {
		if (this.projectOrganization == null){
			this.projectOrganization = "";
		}
		return this.projectOrganization;
	}
	

	public String getProjectTags() {
		if (this.projectTags == null){
			this.projectTags = "";
		}
		return this.projectTags;
	}
	
	public String getRestrictedDomains() {
		if (this.restrictedDomains == null) {
			this.restrictedDomains = "";
		}
		return this.restrictedDomains;
	}
	
	public int getEnableTDCS() {
		return this.enableTDCS ;
	}
	
	public int getEnableAgileScrum() {
		return this.enableAgileScrum ;
	}	
	
	public int getBillingOrganizationId() {
		return this.billingOrganizationId;
	}
	
	public int getNumberOfRequirements() {
		return this.numberOfRequirements;
	}
	
	public String getCreatedBy () {
		return this.createdBy;
	}
	
	//public Date getCreatedDt () {
	//	return this.createdDt;
	//}
	
	public String getLastModifiedBy () {
		return this.lastModifiedBy;
	}
	
	public int getArchived() {
		return this.archived;
	}
	
	public int getHidePriority(){
		return this.hidePriority;
	}
	//public Date getLastModifiedDt () {
	//	return this.lastModifiedDt;
	//}
	
	public ArrayList getMyFolders () {
		return this.myFolders;
	}
	
	public ArrayList getMyRequirementTypes () {
		return this.myRequirementTypes;
	}
	
	public ArrayList getMembers () {
		return this.members;
	}

	public SortedMap<String, Integer> getExternalReqTypes () {
		
		SortedMap<String, Integer> externalReqTypes = new TreeMap();

		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;

		
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
				// for each user, get an array list of String of Roles. 
				String  sql = "select relatedP.short_name, rt.name, rt.id   " + 
					" from gr_project_relations pr, gr_requirement_types rt, gr_projects relatedP " + 
					" where pr.project_id = ? " +
					" and pr.related_project_id = relatedP.id " + 
					" and pr.related_project_id = rt.project_id order by relatedP.short_name, rt.display_sequence ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				rs = prepStmt.executeQuery();
				while (rs.next()){
					
					int reqTypeId = rs.getInt("id");
					String projectPrefix = rs.getString("short_name");
					String rTName = rs.getString("name");
				
					externalReqTypes.put(projectPrefix + " : " + rTName , reqTypeId);
				}
				prepStmt.close();
				rs.close();
			
			
			
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
		
	return(externalReqTypes);
}

	public SortedMap<String, ArrayList> getUsersAndRoles () {
		
			SortedMap<String, ArrayList> usersAndRoles = new TreeMap();

			PreparedStatement prepStmt = null;
			ResultSet rs = null;
			java.sql.Connection con =  null;

			
			try {
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();

				
					// for each user, get an array list of String of Roles. 
					String  sql = "select distinct u.id, u.first_name, u.last_name, u.email_id ,"
							+ " (SUBSTRING_INDEX(SUBSTR(email_id, INSTR(email_id, '@') + 1),'.',1)) 'company' " +
							" from gr_users u, gr_projects p, gr_user_roles ur  "
							+ " where p.id = ? "
							+ " and p.id = ur.project_id "
							+ " and ur.user_id = u.id "
							+ " order by company , u.first_name ";
					prepStmt = con.prepareStatement(sql);
					prepStmt.setInt(1, projectId);
					rs = prepStmt.executeQuery();
					while (rs.next()){
						
						int userId = rs.getInt("id");
						String firstName = rs.getString("first_name");
						String lastName = rs.getString("last_name");
						String emailId = rs.getString("email_id");
						
						String company = rs.getString("company");
						
						String key = firstName + " " + lastName + ":#:" + emailId  + ":#:" + company ;
						
						// for this user , get the list of role names  he or she is a member of.
						ArrayList<String> rolesForUser = new ArrayList<String>();
						// srt run a query to get all the roles for this user. and put them in an arraylist
						sql = " select r.name, r.id "
								+ " from gr_user_roles ur, gr_roles r "
								+ " where ur.role_id = r.id "
								+ " and ur.user_id = ? "
								+ " and ur.project_id = ?  ";
						PreparedStatement prepStmt2 = con.prepareStatement(sql);
						prepStmt2.setInt(1, userId);
						prepStmt2.setInt(2, projectId);
						ResultSet rs2 = prepStmt2.executeQuery();
						while (rs2.next()){
							String roleName = rs2.getString("name");
							int roleId = rs2.getInt("id");
							
							String roleString = roleName + ":#:" + roleId;
							rolesForUser.add(roleString);
							
						}
						rs2.close();
						prepStmt2.close();
						usersAndRoles.put(key, rolesForUser);
						
					}
					prepStmt.close();
					rs.close();
				
				
				
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
			
		return(usersAndRoles);
	}

	public ArrayList getUserRequirements(User user, String databaseType){
		ArrayList requirements = ProjectUtil.getUserRequirements(this.projectId, user,  databaseType);
		return requirements;
	}
	
	public ArrayList getDefectStatusGroups(){
		ArrayList requirements = ProjectUtil.getDefectStatusGroups(this.projectId);
		return requirements;
	}
	
	public ArrayList getProjectRelations(String  databaseType){
		ArrayList projectRelations = ProjectUtil.getProjectRelations(this.projectId,  databaseType);
		return projectRelations;
	}
	
	// relatedProjects object of the projectRelationsLite arraylist is null. Makes it faster than
	// getProjectRelations
	public ArrayList getProjectRelationsLite(String  databaseType){
		ArrayList projectRelationsLite = ProjectUtil.getProjectRelationsLite(this.projectId,  databaseType);
		return projectRelationsLite;
	}
	
	
	public ArrayList getProjectRelationsLiteWithACL(int userId){
		ArrayList projectRelationsLite = ProjectUtil.getProjectRelationsLiteWithACL(this.projectId,  userId);
		return projectRelationsLite;
	}
	
	// same as getProjectRelatiosn, just that the projectRelations object doesn't have the RelatedProject object
	// so this is much faster.
	public ArrayList getProjectRelationsLight(String  databaseType){
		ArrayList projectRelationsLight = ProjectUtil.getProjectRelationsLight(this.projectId,  databaseType);
		return projectRelationsLight;
	}
	
	
	
	public ArrayList getIntegrationMenus(){
		return this.integrationMenus;
	}
	
	public ArrayList getProjectSprints(String databaseType){
		ArrayList sprints = ProjectUtil.getProjectSprints(this.projectId,  databaseType);
		return sprints;
	}
	
	public String getCanNotBeOwnersInProject(){
	
		String canNOTBeOwners = "";
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select can_not_be_owners  " +
				" from gr_projects " +
				" where id = ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				canNOTBeOwners = rs.getString("can_not_be_owners");
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
		if (canNOTBeOwners == null){canNOTBeOwners = "";}
		return(canNOTBeOwners);
		
	}
	
	
	public void setCanNotBeOwnersInProject(String userEmailId, String addRemoveAction){
		
		
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String canNOTBeOwners = this.getCanNotBeOwnersInProject();
			if (addRemoveAction.equals("remove")){
				// get current owner list, remove this user from that list and store.
				canNOTBeOwners = canNOTBeOwners.replace(userEmailId, "");
				canNOTBeOwners = canNOTBeOwners.replace(",,", ",");
			}
			if (addRemoveAction.equals("add")){
				// get current owner list, remove this use from that list , add user to that list and store.
				canNOTBeOwners = canNOTBeOwners.replace(userEmailId, "");
				canNOTBeOwners = canNOTBeOwners + "," + userEmailId;
			}
			
			// lets store the latest can be owners
			String sql = "update gr_projects set can_not_be_owners =  ?   " +
				" where id = ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, canNOTBeOwners);
			prepStmt.setInt(2, projectId);
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
	
	public void setProjectTimeZone(String timeZone){
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// lets store the latest can be owners
			String sql = "update gr_projects set time_zone =  ?   " +
				" where id = ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, timeZone);
			prepStmt.setInt(2, projectId);
			
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

	public void setGMTDelta(Double gmtDelta){
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// lets store the latest can be owners
			String sql = "update gr_projects set gmt_delta =  ?   " +
				" where id = ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setDouble(1, gmtDelta);
			prepStmt.setInt(2, projectId);
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

	
	public int getPercentageCompletedDriverReqTypeId(){
		if (this.percentageCompleteDriverReqTypeId != -1){
			return (this.percentageCompleteDriverReqTypeId);
		}
		else {
			int percentageCompleteDriverReqTypeId = 0;
			java.sql.Connection con = null;
			try {
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();
	
				String sql = "select pct_complete_driver_requirement_type_id  " +
					" from gr_projects " +
					" where id = ? ";
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				ResultSet rs = prepStmt.executeQuery();
				while (rs.next()){
					percentageCompleteDriverReqTypeId = rs.getInt("pct_complete_driver_requirement_type_id");
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
			return (percentageCompleteDriverReqTypeId);
		}
	}

	public String getFoldersEnabledForApprovalWorkFlow(){
		if (this.foldersEnabledForApprovalWorkFlow == null ){
			this.foldersEnabledForApprovalWorkFlow = ProjectUtil.getFoldersEnabledForApprovalWorkFlow(this.projectId);
		}
		return (this.foldersEnabledForApprovalWorkFlow);
	}
	
	
	
	public String getPowerUserSettings(){
		if (
			(this.powerUserSettings != null) 
			&& 
			(!(this.powerUserSettings.equals("")))
		){
			return (this.powerUserSettings);
		}
		else {
			java.sql.Connection con = null;
			try {
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();
	
				String sql = "select power_user_settings  " +
					" from gr_projects " +
					" where id = ? ";
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				ResultSet rs = prepStmt.executeQuery();
				while (rs.next()){
					this.powerUserSettings = rs.getString("power_user_settings");
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
			
			if (this.powerUserSettings == null ){
				this.powerUserSettings = "";
			}
			return (this.powerUserSettings);
		}
	}
	
	
	public String getProjectTimeZone(){
		String timeZone = "";
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select time_zone  " +
				" from gr_projects " +
				" where id = ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				timeZone = rs.getString("time_zone");
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
		
		return (timeZone);
	
	}
	

	public Double getProjectGMTDelta(){
		Double gmtDelta = 0.0;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select gmt_delta  " +
				" from gr_projects " +
				" where id = ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				gmtDelta = rs.getDouble("gmt_delta");
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
		
		return (gmtDelta);
	
	}

	public int getRelatedProjectId(String relatedProjectPrefix){
		return (ProjectUtil.getRelatedProjectId(this.projectId, relatedProjectPrefix));
	}
	public String getHideFromHealthBar(){
		if (this.hideFromHealthBar == null){
			this.hideFromHealthBar = "";
		}
		return (this.hideFromHealthBar);
	}


}
