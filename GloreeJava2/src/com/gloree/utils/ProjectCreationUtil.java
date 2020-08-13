package com.gloree.utils;

import java.sql.*;

import com.gloree.utils.RoleUtil;
import com.gloree.beans.RTAttribute;
import com.gloree.beans.RequirementType;
import com.gloree.beans.Role;

import javax.naming.InitialContext;

public class ProjectCreationUtil {
	
	
	
	//
	// This class is used to support the New Project Creation Wizard tool. It takes in the Project Name,
	// Project Prefix (short name), Project Description, and creates a project , along with it's default
	// Requirement Types and Folder structures. 
	// 
	// It also creates 2 roles , called Administrator and User and 
	// adds the createdBy as the administrator and
	// grants all role privs on all folders to the User. Administrator gets this behavior by default.
	//
	// If the project prefix is not unique, it returns a flag to the controller servlet, so that the
	// correct error message can be displayed in the JSP.
	
	// All new projects created will have by default, 'Agile Scrum enabled' 
	public static int crateNewProjectResourceManagement(String createdByEmailId, String projectName,
		String shortName, 
		String projectDescription, 
		String projectOwner, String projectWebsite, String projectOrganization, String projectTags,
		String restrictedDomains, String databaseType){

		int projectId = 0;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			

			if ((projectOwner == null ) || projectOwner.equals("")){
				projectOwner = createdByEmailId;
			}
			// Now lets create the project in the db.
			String sql  = "";
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_projects (short_name, name, project_type, description," +
				" owner, website, organization, tags, " +
				" restricted_domains, enable_agile_scrum, " +
				" created_by, created_dt,last_modified_by, last_modified_dt) " +
				" values (?, ?, 'resource management', ?," +
				" ?, ?, ?, ?, " +
				" ?, 1, " +
				" ?, now(), ? , now()) " ;
				
			}
			else {
				sql = " insert into gr_projects (short_name, name, description," +
				" owner, website, organization, tags, " +
				" restricted_domains, enable_agile_scrum, " +
				" created_by, created_dt,last_modified_by, last_modified_dt) " +
				" values (?, ?, ?," +
				" ?, ?, ?, ?, " +
				" ?, 1,  " +
				" ?, sysdate, ? , sysdate) " ;
				
			}
			
			// occassionally users tend to put a space between commas in restricted_domains.
			restrictedDomains  = restrictedDomains.replace(" ", "");
		 	prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, shortName);
			prepStmt.setString(2, projectName);
			prepStmt.setString(3, projectDescription);
			
			prepStmt.setString(4, projectOwner);
			prepStmt.setString(5, projectWebsite);
			prepStmt.setString(6, projectOrganization);
			prepStmt.setString(7, projectTags);
			
			
			prepStmt.setString(8, restrictedDomains);
			
			prepStmt.setString(9, createdByEmailId );
			prepStmt.setString(10, createdByEmailId );
			prepStmt.execute();
			
			// find the Id of this project, so that we can create default requirement types and projects.
			sql = " select id from gr_projects where short_name = ?" +
				" and  name = ? " +
				" and description = ? " +
				" and  created_by = ? " +
				" order by id desc";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, shortName);
			prepStmt.setString(2, projectName);
			prepStmt.setString(3, projectDescription);
			prepStmt.setString(4, createdByEmailId);
			
			
			rs = prepStmt.executeQuery();
			rs.next();
			projectId = rs.getInt("id");
			rs.close();
			prepStmt.close();
			

			// Now create the Administrator Role in the db.
			Role administratorRole = new Role(projectId, "Administrator", 
					"Default Administrator Role created by the system" , createdByEmailId,  databaseType);
			Role userRole = new Role(projectId, "User", 
					"Default User Role created by the system" , createdByEmailId,  databaseType);
		
			int requirementTypeId = 0;
			// create Rel structure
			// this method creates the default RT, Folder and gives the user group the privileges.
			String rTDescription = "Default Program type created by the system. " +
			" These are the Programs or Program Releases and Resources are assigned to these Programs and tracked from a cost and availability perspective";
			int requirementTypeDisplaySequence = 1;
			// Call procedure to create the default requirement type, folder and give userRole
			// all privs on this folder.
			int requirementTypeCanBeDangling = 1;
			int requirementTypeCanBeOrphan = 0;
			int rTEnableAgileScrum = 0; 
			requirementTypeId =  createDefaultStructure(projectId, "PGM", "Program", rTDescription, rTEnableAgileScrum,
				requirementTypeDisplaySequence, requirementTypeCanBeDangling, requirementTypeCanBeOrphan, 
				administratorRole, userRole, 1, createdByEmailId,  databaseType);
			
			RequirementType programReqType = new RequirementType(requirementTypeId);
			
			// any time a new requirement type is created, lets by default create a new attribute for notifying the stake holders.
			int parentAttributeId = 0 ;
			int systemAttribute = 0;
			String attributeName = "Keep Me Informed";
			String attributeType = "Text Box";
			String attributeSortOrder = "a";
			int attributeRequired = 0;
			String attributeDefaultValue = "";
			String attributeDropDownOptions = "";
			String attributeDescription = "Comma separated list of email addresses that will be notified of any changes to objects";
			int attributeImpactsVersion = 0;
			int attributeImpactsTraceability = 0;
			int attributeImpactsApprovalWorkflow = 0;
			
			RTAttribute rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, programReqType.getRequirementTypeId(), attributeName , 
				attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
				attributeDropDownOptions, attributeDescription,
				attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
				createdByEmailId, databaseType);
			
			systemAttribute = 0;
			attributeName = "Start Date";
			attributeType = "Date";
			attributeSortOrder = "b";
			attributeDescription = "Starting Date for this Program";
			attributeDefaultValue = "";
			attributeDropDownOptions = "";
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, programReqType.getRequirementTypeId(), attributeName , 
					attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
					attributeDropDownOptions, attributeDescription,
					attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
					createdByEmailId, databaseType);
			
			systemAttribute = 0;
			attributeName = "End Date";
			attributeType = "Date";
			attributeSortOrder = "c";
			attributeDescription = "Ending Date for this program";
			attributeDefaultValue = "";
			attributeDropDownOptions = "";
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, programReqType.getRequirementTypeId(), attributeName , 
					attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
					attributeDropDownOptions, attributeDescription,
					attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
					createdByEmailId, databaseType);
			

			
			
			
			
			
			
			
			
			
			// Call procedure to create the default requirement type. 
			// This returns the requirement type id. use it to create the default folder.
			rTDescription = "Default Resources type created by the system. " +
				" These are the available assets in the organization and can be allocated to Programs. ";
			requirementTypeDisplaySequence = 2;
			// Call procedure to create the default requirement type, folder and give userRole
			// all privs on this folder.
			requirementTypeCanBeDangling = 1;
			requirementTypeCanBeOrphan = 1;
			rTEnableAgileScrum = 0;
			requirementTypeId =  createDefaultStructure(projectId, "RES", "Resources", rTDescription, rTEnableAgileScrum,
				requirementTypeDisplaySequence, 
				requirementTypeCanBeDangling, requirementTypeCanBeOrphan, 
				administratorRole, userRole ,2,  createdByEmailId,  databaseType);
			RequirementType resourceReqType = new RequirementType(requirementTypeId);

			
			// any time a new requirement type is created, lets by default create a new attribute for notifying the stake holders.
			systemAttribute = 0;
			attributeName = "Keep Me Informed";
			attributeType = "Text Box";
			attributeSortOrder = "a";
			attributeDescription = "Comma separated list of email addresses that will be notified of any changes to objects";
			attributeDefaultValue = "";
			attributeDropDownOptions = "Person,You can modify,These Attribute values,In the Configuration Module";
			
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, resourceReqType.getRequirementTypeId(), attributeName , 
				attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
				attributeDropDownOptions, attributeDescription,
				attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
				createdByEmailId, databaseType);
						
			systemAttribute = 0;
			attributeName = "Type";
			attributeType = "Drop Down";
			attributeSortOrder = "b";
			attributeDescription = "Resource Type";
			attributeDefaultValue = "";
			attributeDropDownOptions = "Person,You can modify,These Attribute values,In the Configuration Module";
			
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, resourceReqType.getRequirementTypeId(), attributeName , 
					attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
					attributeDropDownOptions, attributeDescription,
					attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
					createdByEmailId, databaseType);
			
			systemAttribute = 0;
			attributeName = "Cost Per Day";
			attributeType = "Number";
			attributeSortOrder = "c";
			attributeDescription = "Cost Per Day for thie resource";
			attributeDefaultValue = "1";
			attributeDropDownOptions = "";
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, resourceReqType.getRequirementTypeId(), attributeName , 
					attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
					attributeDropDownOptions, attributeDescription,
					attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
					createdByEmailId, databaseType);
			
			systemAttribute = 0;
			attributeName = "Unit of Currency";
			attributeType = "Text Box";
			attributeSortOrder = "d";
			attributeDescription = "Unit of Currency to calculate resource cost";
			attributeDefaultValue = "USD";
			attributeDropDownOptions = "";
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, resourceReqType.getRequirementTypeId(), attributeName , 
					attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
					attributeDropDownOptions, attributeDescription,
					attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
					createdByEmailId, databaseType);
			
			
			systemAttribute = 0;
			attributeName = "Allocatable on Saturdays";
			attributeType = "Drop Down";
			attributeSortOrder = "e";
			attributeDescription = "Can this resource be allocated on Saturdays?";
			attributeDefaultValue = "Yes";
			attributeDropDownOptions = "Yes,No";
			
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, resourceReqType.getRequirementTypeId(), attributeName , 
					attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
					attributeDropDownOptions, attributeDescription,
					attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
					createdByEmailId, databaseType);
					
			
			
			systemAttribute = 0;
			attributeName = "Allocatable on Sundays";
			attributeType = "Drop Down";
			attributeSortOrder = "f";
			attributeDescription = "Can this resource be allocated on Sundays?";
			attributeDefaultValue = "Yes";
			attributeDropDownOptions = "Yes,No";
			
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, resourceReqType.getRequirementTypeId(), attributeName , 
					attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
					attributeDropDownOptions, attributeDescription,
					attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
					createdByEmailId, databaseType);
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			// Call procedure to create the default requirement type. This returns the requirement type id.
			// use it to create the default folder.
			rTDescription = "Allocations track the allocation of Resources to Programs. ";
			requirementTypeDisplaySequence = 3;
			// Call procedure to create the default requirement type, folder and give userRole
			// all privs on this folder.
			requirementTypeCanBeDangling = 1;
			requirementTypeCanBeOrphan = 1;
			rTEnableAgileScrum = 0;
			requirementTypeId = createDefaultStructure(projectId, "ALC", "Allocations", rTDescription,rTEnableAgileScrum, 
				requirementTypeDisplaySequence, 
				requirementTypeCanBeDangling, requirementTypeCanBeOrphan, 
				administratorRole, userRole , 3, createdByEmailId,  databaseType);
			RequirementType allocationReqType = new RequirementType(requirementTypeId);
			
			// any time a new requirement type is created, lets by default create a new attribute for notifying the stake holders.
			attributeName = "Keep Me Informed";
			attributeType = "Text Box";
			attributeSortOrder = "a";
			attributeDescription = "Comma separated list of email addresses that will be notified of any changes to objects";
			attributeDefaultValue = "";
			attributeDropDownOptions = "Person,You can modify,These Attribute values,In the Configuration Module";

			
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, allocationReqType.getRequirementTypeId(), attributeName , 
				attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
				attributeDropDownOptions, attributeDescription,
				attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
				createdByEmailId, databaseType);
			systemAttribute = 0;
			attributeName = "Program Name";
			attributeType = "Text Box";
			attributeSortOrder = "b";
			attributeDescription = "Program this alloccation is used for";
			attributeDefaultValue = "";
			attributeDropDownOptions = "";
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, allocationReqType.getRequirementTypeId(), attributeName , 
					attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
					attributeDropDownOptions, attributeDescription,
					attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
					createdByEmailId, databaseType);
			
			systemAttribute = 0;
			attributeName = "Resource";
			attributeType = "Text Box";
			attributeSortOrder = "c";
			attributeDescription = "Resource this allocation is using";
			attributeDefaultValue = "";
			attributeDropDownOptions = "";
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, allocationReqType.getRequirementTypeId(), attributeName , 
					attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
					attributeDropDownOptions, attributeDescription,
					attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
					createdByEmailId, databaseType);
			
			systemAttribute = 0;
			attributeName = "Start Date";
			attributeType = "Date";
			attributeSortOrder = "d";
			attributeDescription = "Starting date for this allocation";
			attributeDefaultValue = "";
			attributeDropDownOptions = "";
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, allocationReqType.getRequirementTypeId(), attributeName , 
					attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
					attributeDropDownOptions, attributeDescription,
					attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
					createdByEmailId, databaseType);
			
			systemAttribute = 0;
			attributeName = "End Date";
			attributeType = "Date";
			attributeSortOrder = "e";
			attributeDescription = "Ending date for this allocation";
			attributeDefaultValue = "";
			attributeDropDownOptions = "";
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, allocationReqType.getRequirementTypeId(), attributeName , 
					attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
					attributeDropDownOptions, attributeDescription,
					attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
					createdByEmailId, databaseType);
			
			
			systemAttribute = 0;
			attributeName = "Percent of Resource Allocated";
			attributeType = "Number";
			attributeSortOrder = "f";
			attributeDescription = "Percent of this resource allocated";
			attributeDefaultValue = "";
			attributeDropDownOptions = "";
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, allocationReqType.getRequirementTypeId(), attributeName , 
					attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
					attributeDropDownOptions, attributeDescription,
					attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
					createdByEmailId, databaseType);
			
			systemAttribute = 0;
			attributeName = "Cost Per Day";
			attributeType = "Number";
			attributeSortOrder = "g";
			attributeDescription = "Cost of this allocation";
			attributeDefaultValue = "";
			attributeDropDownOptions = "";
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, allocationReqType.getRequirementTypeId(), attributeName , 
					attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
					attributeDropDownOptions, attributeDescription,
					attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
					createdByEmailId, databaseType);
			
			systemAttribute = 0;
			attributeName = "Unit of Currency";
			attributeType = "Text Box";
			attributeSortOrder = "h";
			attributeDescription = "Unit of Currency to calculate resource cost";
			attributeDefaultValue = "";
			attributeDropDownOptions = "";
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, allocationReqType.getRequirementTypeId(), attributeName , 
					attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
					attributeDropDownOptions, attributeDescription,
					attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
					createdByEmailId, databaseType);
			
			
			systemAttribute = 0;
			attributeName = "Allocatable on Saturdays";
			attributeType = "Drop Down";
			attributeSortOrder = "i";
			attributeDescription = "Can this resource be allocated on Saturdays?";
			attributeDefaultValue = "Yes";
			attributeDropDownOptions = "Yes,No";
			
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, allocationReqType.getRequirementTypeId(), attributeName , 
					attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
					attributeDropDownOptions, attributeDescription,
					attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
					createdByEmailId, databaseType);
					
			
			
			systemAttribute = 0;
			attributeName = "Allocatable on Sundays";
			attributeType = "Drop Down";
			attributeSortOrder = "j";
			attributeDescription = "Can this resource be allocated on Sundays?";
			attributeDefaultValue = "Yes";
			attributeDropDownOptions = "Yes,No";
			
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, allocationReqType.getRequirementTypeId(), attributeName , 
					attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
					attributeDropDownOptions, attributeDescription,
					attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
					createdByEmailId, databaseType);
			
			
			// lets ensure that the newly created attribute 'keep me notified' is editable by the admin
			RoleUtil.setUpdateAttributes(administratorRole.getRoleId(), programReqType.getRootFolderId(), ":#:Keep Me Informed:#:");
			RoleUtil.setUpdateAttributes(administratorRole.getRoleId(), resourceReqType.getRootFolderId(), ":#:Keep Me Informed:#:");
			RoleUtil.setUpdateAttributes(administratorRole.getRoleId(), allocationReqType.getRootFolderId(), ":#:Keep Me Informed:#:");
			
			// ASSETTODO 
			// RoleUtil set update attributes for normal users and for all the attributes added here.
			
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
		return (projectId);
	}
	
	//
	// This class is used to support the New Project Creation Wizard tool. It takes in the Project Name,
	// Project Prefix (short name), Project Description, and creates a project , along with it's default
	// Requirement Types and Folder structures. 
	// 
	// It also creates 2 roles , called Administrator and User and 
	// adds the createdBy as the administrator and
	// grants all role privs on all folders to the User. Administrator gets this behavior by default.
	//
	// If the project prefix is not unique, it returns a flag to the controller servlet, so that the
	// correct error message can be displayed in the JSP.
	
	// All new projects created will have by default, 'Agile Scrum enabled' 
	public static int crateNewProject(String createdByEmailId, String projectName,
		String shortName, 
		String projectDescription, 
		String projectOwner, String projectWebsite, String projectOrganization, String projectTags,
		String restrictedDomains, String databaseType){

		int projectId = 0;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			

			if ((projectOwner == null ) || projectOwner.equals("")){
				projectOwner = createdByEmailId;
			}
			// Now lets create the project in the db.
			String sql  = "";
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_projects (short_name, name, description," +
				" owner, website, organization, tags, " +
				" restricted_domains, enable_agile_scrum, " +
				" created_by, created_dt,last_modified_by, last_modified_dt) " +
				" values (?, ?, ?," +
				" ?, ?, ?, ?, " +
				" ?, 1, " +
				" ?, now(), ? , now()) " ;
				
			}
			else {
				sql = " insert into gr_projects (short_name, name, description," +
				" owner, website, organization, tags, " +
				" restricted_domains, enable_agile_scrum, " +
				" created_by, created_dt,last_modified_by, last_modified_dt) " +
				" values (?, ?, ?," +
				" ?, ?, ?, ?, " +
				" ?, 1,  " +
				" ?, sysdate, ? , sysdate) " ;
				
			}
			
			// occassionally users tend to put a space between commas in restricted_domains.
			restrictedDomains  = restrictedDomains.replace(" ", "");
		 	prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, shortName);
			prepStmt.setString(2, projectName);
			prepStmt.setString(3, projectDescription);
			
			prepStmt.setString(4, projectOwner);
			prepStmt.setString(5, projectWebsite);
			prepStmt.setString(6, projectOrganization);
			prepStmt.setString(7, projectTags);
			
			
			prepStmt.setString(8, restrictedDomains);
			
			prepStmt.setString(9, createdByEmailId );
			prepStmt.setString(10, createdByEmailId );
			prepStmt.execute();
			
			// find the Id of this project, so that we can create default requirement types and projects.
			sql = " select id from gr_projects where short_name = ?" +
				" and  name = ? " +
				" and description = ? " +
				" and  created_by = ? " +
				" order by id desc";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, shortName);
			prepStmt.setString(2, projectName);
			prepStmt.setString(3, projectDescription);
			prepStmt.setString(4, createdByEmailId);
			
			
			rs = prepStmt.executeQuery();
			rs.next();
			projectId = rs.getInt("id");
			rs.close();
			prepStmt.close();
			

			// Now create the Administrator Role in the db.
			Role administratorRole = new Role(projectId, "Administrator", 
					"Default Administrator Role created by the system" , createdByEmailId,  databaseType);
			Role userRole = new Role(projectId, "User", 
					"Default User Role created by the system" , createdByEmailId,  databaseType);
		
			int requirementTypeId = 0;
			// create Rel structure
			// this method creates the default RT, Folder and gives the user group the privileges.
			String rTDescription = "Default Release Requirement type created by the system. " +
			" These are the Releases (also knows as Sprints, FCS, Go Live) requirements " +
			" usually created by the Release Manager prior to track the progress of this release ";
			int requirementTypeDisplaySequence = 1;
			// Call procedure to create the default requirement type, folder and give userRole
			// all privs on this folder.
			int requirementTypeCanBeDangling = 1;
			int requirementTypeCanBeOrphan = 0;
			int rTEnableAgileScrum = 0; 
			requirementTypeId =  createDefaultStructure(projectId, "REL", "Release", rTDescription, rTEnableAgileScrum,
				requirementTypeDisplaySequence, requirementTypeCanBeDangling, requirementTypeCanBeOrphan, 
				administratorRole, userRole, 1, createdByEmailId,  databaseType);
			
			RequirementType releaseReqType = new RequirementType(requirementTypeId);
			
			// any time a new requirement type is created, lets by default create a new attribute for notifying the stake holders.
			int parentAttributeId = 0 ;
			int systemAttribute = 0;
			String attributeName = "Keep Me Informed";
			String attributeType = "Text Box";
			String attributeSortOrder = "a";
			int attributeRequired = 0;
			String attributeDefaultValue = "";
			String attributeDropDownOptions = "";
			String attributeDescription = "Comma separated list of email addresses that will be notified of any changes to objects";
			int attributeImpactsVersion = 0;
			int attributeImpactsTraceability = 0;
			int attributeImpactsApprovalWorkflow = 0;
			
			RTAttribute rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, releaseReqType.getRequirementTypeId(), attributeName , 
				attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
				attributeDropDownOptions, attributeDescription,
				attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
				createdByEmailId, databaseType);
			
			
			// Call procedure to create the default requirement type. 
			// This returns the requirement type id. use it to create the default folder.
			rTDescription = "Default Business Requirement type created by the system. " +
				" These are high level requirements usually created by business analysts prior " +
				" to Business Commit";
			requirementTypeDisplaySequence = 2;
			// Call procedure to create the default requirement type, folder and give userRole
			// all privs on this folder.
			requirementTypeCanBeDangling = 1;
			requirementTypeCanBeOrphan = 1;
			rTEnableAgileScrum = 0;
			requirementTypeId =  createDefaultStructure(projectId, "BR", "Business Requirements", rTDescription, rTEnableAgileScrum,
				requirementTypeDisplaySequence, 
				requirementTypeCanBeDangling, requirementTypeCanBeOrphan, 
				administratorRole, userRole ,2,  createdByEmailId,  databaseType);
			RequirementType businessReqType = new RequirementType(requirementTypeId);
			
			// any time a new requirement type is created, lets by default create a new attribute for notifying the stake holders.
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, businessReqType.getRequirementTypeId(), attributeName , 
				attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
				attributeDropDownOptions, attributeDescription,
				attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
				createdByEmailId, databaseType);
						
			
			
			// Call procedure to create the default requirement type. This returns the requirement type id.
			// use it to create the default folder.
			rTDescription = "Default Functional Requirement type created by the system." +
					" These are detail level requirements usually created by design engineers / developers " +
					" prior to Execute Commit";
			requirementTypeDisplaySequence = 3;
			// Call procedure to create the default requirement type, folder and give userRole
			// all privs on this folder.
			requirementTypeCanBeDangling = 1;
			requirementTypeCanBeOrphan = 1;
			rTEnableAgileScrum = 0;
			requirementTypeId = createDefaultStructure(projectId, "FR", "Functional Requirements", rTDescription,rTEnableAgileScrum, 
				requirementTypeDisplaySequence, 
				requirementTypeCanBeDangling, requirementTypeCanBeOrphan, 
				administratorRole, userRole , 3, createdByEmailId,  databaseType);
			RequirementType functionalReqType = new RequirementType(requirementTypeId);
			
			// any time a new requirement type is created, lets by default create a new attribute for notifying the stake holders.
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, functionalReqType.getRequirementTypeId(), attributeName , 
				attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
				attributeDropDownOptions, attributeDescription,
				attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
				createdByEmailId, databaseType);
			
			
			
			// Call procedure to create the default requirement type. This returns the requirement type id.
			// use it to create the default folder.
			rTDescription = "Default Test Case type created by the system. These are Test Cases usually" +
					" created by test engineers prior to Development Commit";
			requirementTypeDisplaySequence = 4;
			// Call procedure to create the default requirement type, folder and give userRole
			// all privs on this folder.
			requirementTypeCanBeDangling = 1;
			requirementTypeCanBeOrphan = 1;
			rTEnableAgileScrum = 0;
			requirementTypeId = createDefaultStructure(projectId, "TC", "Test Cases", rTDescription, rTEnableAgileScrum,
				requirementTypeDisplaySequence, requirementTypeCanBeDangling, requirementTypeCanBeOrphan, 
				administratorRole, userRole, 4, createdByEmailId,  databaseType);
			RequirementType testCaseReqType = new RequirementType(requirementTypeId);
			
			// any time a new requirement type is created, lets by default create a new attribute for notifying the stake holders.
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, testCaseReqType.getRequirementTypeId(), attributeName , 
				attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
				attributeDropDownOptions, attributeDescription,
				attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
				createdByEmailId, databaseType);
			
			
			// Call procedure to create the default requirement type. This returns the requirement type id.
			// use it to create the default folder.
			rTDescription = "Default Test Result type created by the system. These are usually created" +
					" by Test Engineers prior to Go Live";
			requirementTypeDisplaySequence = 5;
			// Call procedure to create the default requirement type, folder and give userRole
			// all privs on this folder.
			requirementTypeCanBeDangling = 0;
			requirementTypeCanBeOrphan = 1;
			rTEnableAgileScrum = 0;
			requirementTypeId = createDefaultStructure(projectId, "TR", "Test Results", rTDescription, rTEnableAgileScrum,
				requirementTypeDisplaySequence, requirementTypeCanBeDangling, requirementTypeCanBeOrphan, 
				administratorRole, userRole, 5, createdByEmailId,  databaseType);
			RequirementType testResultReqType = new RequirementType(requirementTypeId);
			
			// any time a new requirement type is created, lets by default create a new attribute for notifying the stake holders.
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, testResultReqType.getRequirementTypeId(), attributeName , 
				attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
				attributeDropDownOptions, attributeDescription,
				attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
				createdByEmailId, databaseType);
			
			
			// Call procedure to create the default requirement type. This returns the requirement type id.
			// use it to create the default folder.
			rTDescription = "Default Defect type created by the system. These are usually created" +
					" by Test Engineers as they find defects during testing";
			requirementTypeDisplaySequence = 6;
			// Call procedure to create the default requirement type, folder and give userRole
			// all privs on this folder.
			requirementTypeCanBeDangling = 1;
			requirementTypeCanBeOrphan = 1;
			rTEnableAgileScrum = 0;
			requirementTypeId = createDefaultStructure(projectId, "DEF", "Defects", rTDescription, rTEnableAgileScrum,
				requirementTypeDisplaySequence, requirementTypeCanBeDangling, requirementTypeCanBeOrphan, 
				administratorRole, userRole, 6, createdByEmailId,  databaseType);
			RequirementType defectReqType = new RequirementType(requirementTypeId);
			
			// any time a new requirement type is created, lets by default create a new attribute for notifying the stake holders.
			rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, defectReqType.getRequirementTypeId(), attributeName , 
				attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
				attributeDropDownOptions, attributeDescription,
				attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
				createdByEmailId, databaseType);
			
			// lets add some custom attributes for Defects Requirement Type.
			rTAttribute = new RTAttribute(projectId,parentAttributeId, 0, requirementTypeId, "Defect Status" , 
					"Drop Down" , "1",	1, "New", 
					"New,Assigned,Junk,Resolved,Verified,Closed", "Attribute that tracks the status of Defect as it moves through its lifecycle.",
					1, 0, 0, createdByEmailId, databaseType);
			rTAttribute = new RTAttribute(projectId,0, parentAttributeId, requirementTypeId, "Severity" , 
					"Drop Down" , "1",	1, "1", 
					"1,2,3,4,5,6", "Attribute that tracks severity (criticality) of the defect.",
					1, 0, 0, createdByEmailId, databaseType);
			
			// lets ensure that the newly created attribute 'keep me notified' is editable by the admin
			RoleUtil.setUpdateAttributes(administratorRole.getRoleId(), releaseReqType.getRootFolderId(), ":#:Keep Me Informed:#:");
			RoleUtil.setUpdateAttributes(administratorRole.getRoleId(), businessReqType.getRootFolderId(), ":#:Keep Me Informed:#:");
			RoleUtil.setUpdateAttributes(administratorRole.getRoleId(), functionalReqType.getRootFolderId(), ":#:Keep Me Informed:#:");
			RoleUtil.setUpdateAttributes(administratorRole.getRoleId(), testCaseReqType.getRootFolderId(), ":#:Keep Me Informed:#:");
			RoleUtil.setUpdateAttributes(administratorRole.getRoleId(), testResultReqType.getRootFolderId(), ":#:Keep Me Informed:#:");
			RoleUtil.setUpdateAttributes(administratorRole.getRoleId(), defectReqType.getRootFolderId(), ":#:Keep Me Informed:#:Defect Status:#:Severity:#:");
			
						
			
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
		return (projectId);
	}
	
	
	// this procedure creates a requirement type , then creates a folder to hold 
	// requirements of this type at the project root level
	// and gives the userRole all privileges to create / read / update /delete
	// trace to / trace from requirements in the folder we just created.
	
	// this method also creates canned reports for every folder created.
	static int createDefaultStructure(int projectId, String requirementTypePrefix,
			String requirementTypeName, String requirementTypeDescription, int requirementTypeEnableAgileScrum,
			int requirementTypeDisplaySequence,
			int requirementTypeCanBeDangling, int requirementTypeCanBeOrphan,
			Role administratorRole, Role userRole, int folderOrder, String createdByEmailId, String databaseType){
		
		int requirementTypeId = 0;
		try {
			
			// Call procedure to create the default requirement type. 
			// This returns the requirement type id. use it to create the default folder.
			// by default, we disable requirement type approval.
			int requirementTypeEnableApproval = 0;
			String requirementTypeCanNotTraceTo = "";
			
			
			requirementTypeId = ProjectUtil.createARequirementType(projectId, requirementTypePrefix, 
				requirementTypeName, requirementTypeDescription , requirementTypeDisplaySequence,
				requirementTypeEnableApproval,requirementTypeEnableAgileScrum,
				requirementTypeCanBeDangling, requirementTypeCanBeOrphan, requirementTypeCanNotTraceTo,
				createdByEmailId,  databaseType);
			
			// Now call the routine to make the corresponding Folder.
			int folderId = ProjectUtil.createAFolder(projectId, requirementTypeId,
					requirementTypeName, folderOrder, createdByEmailId,  databaseType);
			
			
			// NOTE : this is critical. The call to updateRolePriv table will ensure
			// that a row is created in the rolepriv table for this newly created folder
			// for all roles in this project.
			RoleUtil.updateRolePrivTable(projectId);
			

			// Now grant all role privileges on this folder to the administrator role.
			RoleUtil.setPrivileges(administratorRole.getRoleId(), folderId, "createRequirement");
			RoleUtil.setPrivileges(administratorRole.getRoleId(), folderId, "readRequirement");
			RoleUtil.setPrivileges(administratorRole.getRoleId(), folderId, "updateRequirement");
			RoleUtil.setPrivileges(administratorRole.getRoleId(), folderId, "deleteRequirement");
			RoleUtil.setPrivileges(administratorRole.getRoleId(), folderId, "traceRequirement");
			RoleUtil.setPrivileges(administratorRole.getRoleId(), folderId, "approveRequirement");
			
			// Now grant all role privileges on this folder to the user role.
			RoleUtil.setPrivileges(userRole.getRoleId(), folderId, "createRequirement");
			RoleUtil.setPrivileges(userRole.getRoleId(), folderId, "readRequirement");
			RoleUtil.setPrivileges(userRole.getRoleId(), folderId, "updateRequirement");
			RoleUtil.setPrivileges(userRole.getRoleId(), folderId, "deleteRequirement");
			RoleUtil.setPrivileges(userRole.getRoleId(), folderId, "traceRequirement");
			RoleUtil.setPrivileges(userRole.getRoleId(), folderId, "approveRequirement");
			
			// method to create a bunch of canned reports for every folder.
			FolderUtil.createCannedReports(projectId, folderId, requirementTypeName, createdByEmailId,  databaseType);
		} catch (Exception e) {
			e.printStackTrace();
		} 	
		
		return requirementTypeId;
	}
			
	static void debug (Connection con, String message) {
		String sql = "insert into gr_debug (message) values (?)";
		try {
		PreparedStatement prepStmt = con.prepareStatement(sql);
		prepStmt.setString(1, message);
		prepStmt.execute();
		prepStmt.close();
		} catch (Exception e){
			
			e.printStackTrace();
		} 
		return;
	}	
}