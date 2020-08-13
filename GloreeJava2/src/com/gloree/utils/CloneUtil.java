package com.gloree.utils;

//GloreeJava2


import java.text.SimpleDateFormat;
import java.util.Calendar;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.gloree.beans.Role;
import com.gloree.beans.User;


public class CloneUtil {

	
	// It clones the project and makes the cloner an admin on this project.
	public static void cloneSampleProject(java.sql.Connection  con, int sampleProjectId, 
		String sampleProjectPrefix, String sampleProjectCreatedBy, String clonerEmailId, String databaseType, User user) {
		
		try {
			// if sample project id exists and the user is not a member of the sampleproject
			// already, then we clone the sample project for this user.
			int sourceProjectId = 0;
			String sql = "select id from gr_projects where id = ? and short_name = ? and created_by = ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sampleProjectId);
			prepStmt.setString(2, sampleProjectPrefix);
			prepStmt.setString(3, sampleProjectCreatedBy);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				sourceProjectId = rs.getInt("id");
				
				// lets see if the user is already a member of the sample project.
				sql = "select count(*) \"matches\" " +
					" from gr_user_roles ur, gr_users u " +
					" where ur.user_id = u.id" +
					" and ur.project_id = ? " +
					" and u.email_id = ? ";
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1, sourceProjectId);
				prepStmt2.setString(2, clonerEmailId);
				ResultSet rs2 = prepStmt2.executeQuery();
				int matches = 0;
				while (rs2.next()){
					matches = rs2.getInt("matches");	
				}
				if (matches == 0){
					// this user does not have member ship to the sample project.
					// so lets clone one for him.
					boolean cloneUsers = true;
					boolean cloneRequirements = true;
					boolean cloneTraceability = true;
					boolean cloneMetrics = true;
					int clonedProjectId = cloneProject(con ,sourceProjectId, cloneUsers, cloneRequirements,
						cloneTraceability, cloneMetrics, clonerEmailId,  databaseType, user);
					// after the project is cloned, it an exact replica of 
					// the one created by sami. 
					// we need to do the following to make is more meaning ful to the cloner.
					// 1. change the project title
					// 2. change the ownership of all requirements
					// 3. change the ownsership of all reports
					// 4. change the user metrics
					sanitizeClonedProject(con, clonedProjectId, clonerEmailId,  databaseType);


				}
			}
			rs.close();
			prepStmt.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	// It sanitizes the cloned project 
	// after the project is cloned, it an exact replica of 
	// the one created by sami. 
	// we need to do the following to make is more meaning ful to the cloner.
	// 1. change the project title
	// 2. change the ownership of all requirements
	// 3. change the ownership of all reports
	// 4. change the user metrics
	public static void sanitizeClonedProject(java.sql.Connection  con, int clonedProjectId, 
		String clonerEmailId, String databaseType) {
		
		try {
			// lets change the project title.
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "update gr_projects set name = (" +
					" 	select concat (first_name, ' ', last_name , '''s Sample project') " +
					"	from gr_users" +
					"	where email_id = ?) " +
					" where id = ? ";
			}
			else {
				sql = "update gr_projects set name = (" +
				" 	select  first_name || ' ' || last_name || '''s Sample project' " +
				"	from gr_users" +
				"	where email_id = ?) " +
				" where id = ? ";
			}
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, clonerEmailId);
			prepStmt.setInt(2, clonedProjectId);
			prepStmt.execute();
			prepStmt.close();
			
			// lets change the ownership of all requirements to the cloner.
			sql = " update gr_requirements set owner = ? " +
				" where project_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, clonerEmailId);
			prepStmt.setInt(2, clonedProjectId);
			prepStmt.execute();
			prepStmt.close();
			
			// lets update the owenership of all saved non-canned reports
			sql = " update gr_reports set created_by = ? " +
				" where project_id = ? " +
				" and description not like 'Canned%' ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, clonerEmailId);
			prepStmt.setInt(2, clonedProjectId);
			prepStmt.execute();
			prepStmt.close();
			

			// lets update the ownership of all user metrics 
			sql = "update gr_user_metrics set owner = ? " +
				" where project_id = ? " ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, clonerEmailId);
			prepStmt.setInt(2, clonedProjectId);
			prepStmt.execute();
			prepStmt.close();
			

			// lets update the ownership of all word templates
			sql = "update gr_word_templates set created_by = ? " +
				" where project_id = ? " ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, clonerEmailId);
			prepStmt.setInt(2, clonedProjectId);
			prepStmt.execute();
			prepStmt.close();
			
			// lets remove all users other than cloner from this project.
			sql = "delete " +
				" from gr_user_roles " +
				" where user_id not in(select id from gr_users where email_id = ? )  " +
				" and project_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, clonerEmailId);
			prepStmt.setInt(2, clonedProjectId);
			prepStmt.execute();
			prepStmt.close();
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		
	}


	
	// It clones the project and makes the cloner an admin on this project.
	public static int cloneProject(java.sql.Connection con ,int sourceProjectId, boolean cloneUsers, boolean  cloneRequirements,
			boolean  cloneTraceability, boolean cloneMetrics, String clonerEmailId, String databaseType, User user) {
		int clonedProjectId = 0;
		try {
			 
			
			clonedProjectId = cloneProject_createProject(con,sourceProjectId, clonerEmailId,  databaseType);
			
			cloneProject_createRequirementTypeAttributesFoldersBaselines(con,sourceProjectId,clonedProjectId, clonerEmailId,  databaseType);
			
			cloneProject_createRolesUsersPermissions(con,sourceProjectId,clonedProjectId, cloneUsers, clonerEmailId);
			
			
			
			// we need to clone canned reports, irrespective of whether we have cloneUsers flag or not.
			cloneProject_createReports(con,sourceProjectId, clonedProjectId, clonerEmailId, "Canned",  databaseType);
		
			
			if (cloneUsers){
				// we can create User Defined Reports only if Clone Users is turned on.
				cloneProject_createReports(con,sourceProjectId, clonedProjectId, clonerEmailId, "UserDefined",  databaseType);
			
				cloneProject_createTemplates(con,sourceProjectId, clonedProjectId, clonerEmailId,  databaseType);
				
				if (cloneRequirements){
					
					// we can careate Requirements only if Clone Users and Clone Requirements are ON.
					cloneProject_createRequirements(con,sourceProjectId, clonedProjectId, clonerEmailId,  databaseType);
				
					cloneProject_createAttributeValues(con,sourceProjectId, clonedProjectId, clonerEmailId,  databaseType);
					
					cloneProject_createRequirementBaselines(con,sourceProjectId, clonedProjectId, clonerEmailId, databaseType);
		
					cloneProject_createRequirementComments(con,sourceProjectId, clonedProjectId, clonerEmailId, databaseType);
		
					cloneProject_createRequirementAttachments(con,sourceProjectId, clonedProjectId, clonerEmailId, databaseType);

					
					if (cloneTraceability){
						// we can create Traceability only if clone Users,and clone Requirements and clone Traceability are ON.
						cloneProject_createTraces(con,sourceProjectId, clonedProjectId, clonerEmailId);
					}
				}
				
			}

			if (!(cloneUsers)){
				// if the user chose to NOT clone users, you end up with a scenario where the project is created
				// but no one can access it. Not Good. So in that scenario, lets make the person who is 
				// cloning the project its Administrator.
				Role adminRole = new Role (clonedProjectId,"Administrator");

				String sql = "insert into gr_user_roles(user_id, project_id, role_id) values (?,?,?)";
				PreparedStatement prepStmt = con.prepareStatement(sql);
							
				prepStmt.setInt(1, user.getUserId());
				prepStmt.setInt(2, clonedProjectId);
				prepStmt.setInt(3, adminRole.getRoleId());
				prepStmt.execute();
				prepStmt.close();
				
				
				
			}
			
			if (cloneMetrics){
				// we can create Metrics only if Clone Metrics is turned on.
				cloneProject_createProjectAndUserMetrics(con,sourceProjectId, clonedProjectId, clonerEmailId);

				cloneProject_createReleaseMetrics(con,sourceProjectId, clonedProjectId, clonerEmailId);

				cloneProject_createReleaseRequirements(con,sourceProjectId, clonedProjectId, clonerEmailId);
				
				cloneProject_createFolderMetrics(con,sourceProjectId, clonedProjectId, clonerEmailId);

				cloneProject_createBaselineMetrics(con,sourceProjectId, clonedProjectId, clonerEmailId);

			}
		
			
			
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return clonedProjectId;
	}

	
	
	
	// Part of cloneProject. Creates the Project entry.
	public static int cloneProject_createProject(java.sql.Connection con ,int sourceProjectId, String clonerEmailId, String databaseType) {
		int clonedProjectId = 0;
		try {
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_projects (short_name, name, description, " +
					" owner, website, organization, tags, restricted_domains, enable_agile_scrum, " +
					" created_by, created_dt, last_modified_by, last_modified_dt)" +
					" select short_name, substr(concat('Clone - ', name),1,98) \"name\" , description," +
					" owner, website, organization, tags, " +
					" restricted_domains, 1,  ?, now(), ?, now() " +
					" from gr_projects where id = ?	 ";
			}
			else {
				sql = "insert into gr_projects (short_name, name, description, " +
					" owner, website, organization, tags, restricted_domains, enable_agile_scrum, " +
					" created_by, created_dt, last_modified_by, last_modified_dt)" +
					" select short_name, substr(concat('Clone - ', name),1,98) \"name\" , description," +
					" owner, website, organization, tags, " +
					" restricted_domains, 1,  ?, sysdate, ?, sysdate " +
					" from gr_projects where id = ?	 ";
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			
			prepStmt.setString(1, clonerEmailId);
			prepStmt.setString(2, clonerEmailId);
			prepStmt.setInt(3, sourceProjectId);
			prepStmt.execute();
			prepStmt.close();
			
			// lets get the project id so that we can work on it.		
			sql = "select max(id) \"id\" from gr_projects where created_by = ?  ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, clonerEmailId);
			
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				clonedProjectId = rs.getInt("id");
			}
			rs.close();
			prepStmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clonedProjectId;
	}

	// Part of cloneProject. returns the cloned Requirement type for a req type prefix.
	public static int cloneProject_getClonedRequirementTypeId(java.sql.Connection con , int clonedProjectId, String sourceRequirementTypePrefix) {
		int clonedRequirementTypeId = 0;
		try {
			// requirement type short names are unique per project.
			String sql = "select id from gr_requirement_types where project_id = ? and short_name =  ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, clonedProjectId);
			prepStmt.setString(2, sourceRequirementTypePrefix);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()){
				clonedRequirementTypeId = rs.getInt("id");
			}
			prepStmt.close();
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clonedRequirementTypeId;
	}
	
	
	// Part of cloneProject. returns the cloned Attribute Id fro a cloned project, source requirement type prefix and source attribute name
	public static int cloneProject_getClonedAttributeId(java.sql.Connection con , int clonedProjectId, String sourceRequirementTypePrefix,
			String sourceRTAttributeName) {
		int clonedAttributeId = 0;
		try {
			int clonedRequirementTypeId = cloneProject_getClonedRequirementTypeId(con, clonedProjectId, sourceRequirementTypePrefix);
			// requirement type short names are unique per project.
			String sql = "select id from gr_rt_attributes where requirement_type_id = ? and name =  ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, clonedRequirementTypeId);
			prepStmt.setString(2, sourceRTAttributeName);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()){
				clonedAttributeId = rs.getInt("id");
			}
			prepStmt.close();
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clonedAttributeId;
	}
		
	// Part of cloneProject. returns the cloned RTBaseline Id from a cloned project, source requirement type prefix and source RTBaseline name
	public static int cloneProject_getClonedRTBaselineId(java.sql.Connection con , int clonedProjectId, String sourceRequirementTypePrefix,
			String sourceRTBaselineName) {
		int clonedRTBaselineId = 0;
		try {
			int clonedRequirementTypeId = cloneProject_getClonedRequirementTypeId(con, clonedProjectId, sourceRequirementTypePrefix);
			// requirement type short names are unique per project.
			String sql = "select id from gr_rt_baselines where requirement_type_id = ? and name =  ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, clonedRequirementTypeId);
			prepStmt.setString(2, sourceRTBaselineName);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()){
				clonedRTBaselineId = rs.getInt("id");
			}
			prepStmt.close();
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clonedRTBaselineId;
	}
	
	
	// Part of cloneProject. returns the cloned Parent Folder Id for a folder name , level combo.
	public static int cloneProject_getClonedFolderId(java.sql.Connection con , int clonedProjectId, 
		String sourceFolderName, String sourceFolderPath) {
		int clonedFolderId = 0;
		try {
			// FolderName is unique at a folder level within a project.
			String sql = "select id from gr_folders where project_id = ? and name = ? and folder_path= ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, clonedProjectId);
			prepStmt.setString(2, sourceFolderName);
			prepStmt.setString(3, sourceFolderPath);
		
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				clonedFolderId = rs.getInt("id");
			}
			rs.close();
			prepStmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clonedFolderId;
	}
	
	// Part of cloneProject. returns the cloned Role Idfor a Role Name.
	public static int cloneProject_getClonedRoleId(java.sql.Connection con , int clonedProjectId, 
		String sourceRoleName) {
		int clonedRoleId = 0;
		try {
			// lets get the cloned Role id for this Role.
			// Role names are unique per project.
			String sql = "select id from gr_roles where project_id = ? and name =  ? ";
			PreparedStatement prepStmt2 = con.prepareStatement(sql);
			prepStmt2.setInt(1, clonedProjectId);
			prepStmt2.setString(2, sourceRoleName);
			ResultSet rs2 = prepStmt2.executeQuery();
			if (rs2.next()){
				clonedRoleId = rs2.getInt("id");
			}
			prepStmt2.close();
			rs2.close();
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clonedRoleId;
	}
	

	// Part of cloneProject. returns the cloned Requiremnent Id for a Role Name.
	public static int cloneProject_getClonedRequirementId(java.sql.Connection con , int clonedProjectId, 
		String sourceRequirementFullTag) {
		int clonedRequirementId = 0;
		try {
			// lets get the cloned Role id for this Role.
			// Role names are unique per project.
			String sql = "select id from gr_requirements where project_id = ? and full_tag =  ? ";
			PreparedStatement prepStmt2 = con.prepareStatement(sql);
			prepStmt2.setInt(1, clonedProjectId);
			prepStmt2.setString(2, sourceRequirementFullTag);
			ResultSet rs2 = prepStmt2.executeQuery();
			if (rs2.next()){
				clonedRequirementId = rs2.getInt("id");
			}
			prepStmt2.close();
			rs2.close();
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clonedRequirementId;
	}
	
	// Part of cloneProject. Creates the Requirements Types and for each RequirementType, its Attributes, folders and baselines..
	public static void cloneProject_createRequirementTypeAttributesFoldersBaselines(java.sql.Connection con ,int sourceProjectId,
		int clonedProjectId , String clonerEmailId, String databaseType) {
		try {
			String sql = "";
			
			// lets create the Requirement Types in the cloned project.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_requirement_types(project_id, short_name, name, description, " +
					" display_sequence, enable_approval, enable_agile_scrum,  can_be_dangling, can_be_orphan, " +
					" created_by, created_dt, last_modified_by, last_modified_dt)" +
					" select "+ clonedProjectId +", short_name, name, description, " +
					" display_sequence, enable_approval, enable_agile_scrum,  can_be_dangling, can_be_orphan,  ?, now(), ?, now() " +
					" from gr_requirement_types where project_id = ?	 ";
			}
			else {
				sql = "insert into gr_requirement_types(project_id, short_name, name, description, " +
				" display_sequence, enable_approval, enable_agile_scrum,  can_be_dangling, can_be_orphan, " +
				" created_by, created_dt, last_modified_by, last_modified_dt)" +
				" select "+ clonedProjectId +", short_name, name, description, " +
				" display_sequence, enable_approval, enable_agile_scrum,  can_be_dangling, can_be_orphan,  ? , sysdate, ? , sysdate " +
				" from gr_requirement_types where project_id = ?	 ";
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, clonerEmailId);
			prepStmt.setString(2, clonerEmailId);
			prepStmt.setInt(3, sourceProjectId);
			prepStmt.execute();
			prepStmt.close();
			
			//Now loop through all these requirement types for the source project for each requirement type
			// create attributes, folders and baselines.
			sql = " select id, short_name from gr_requirement_types where project_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceProjectId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				int sourceRequirementTypeId = rs.getInt("id");
				String sourceRequirementTypePrefix = rs.getString("short_name");
				
				// lets get the cloned requirement type id for this requirement type.
				int clonedRequirementTypeId = cloneProject_getClonedRequirementTypeId(con, clonedProjectId, sourceRequirementTypePrefix);
				
				
				
				// lets create the baselines by querying for the baselines of the sourceRequirementType
				// and inserting them for clonedRequirementType
				if (databaseType.equals("mySQL")){
					sql = "insert into gr_rt_baselines(requirement_type_id, name, description, " +
						" created_by, created_dt, last_modified_by, last_modified_dt)" +
						" select "+ clonedRequirementTypeId +", name, description,   ?, now(), ?, now() " +
						" from gr_rt_baselines where requirement_type_id = ?	 ";
				}
				else {
					sql = "insert into gr_rt_baselines(requirement_type_id, name, description, " +
					" created_by, created_dt, last_modified_by, last_modified_dt)" +
					" select "+ clonedRequirementTypeId +", name, description,   ?, sysdate, ?, sysdate " +
					" from gr_rt_baselines where requirement_type_id = ?	 ";
				}
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setString(1, clonerEmailId);
				prepStmt2.setString(2, clonerEmailId);
				prepStmt2.setInt(3, sourceRequirementTypeId);
				prepStmt2.execute();
				prepStmt2.close();
				
				
				
				// lets create the attributes by querying for the attributes of the sourceRequirementType
				// and inserting them for clonedRequirementType
				if (databaseType.equals("mySQL")){
					
					sql = "insert into gr_rt_attributes(requirement_type_id, name, description, type, " +
						" options, required,default_display, default_value, sort_order, " +
						" impacts_version, impacts_traceability, impacts_approval_workflow, " +
						" created_by, created_dt, last_modified_by, last_modified_dt)" +
						" select "+ clonedRequirementTypeId +", name, description, type,  " +
						" options, required,default_display, default_value, sort_order,   " +
						" impacts_version, impacts_traceability, impacts_approval_workflow, ?, now(), ?, now() " +
						" from gr_rt_attributes where requirement_type_id = ?	 ";
				}
				else {
					sql = "insert into gr_rt_attributes(requirement_type_id, name, description, type, " +
					" options, required,default_display, default_value, sort_order, " +
					" impacts_version, impacts_traceability, impacts_approval_workflow, " +
					" created_by, created_dt, last_modified_by, last_modified_dt)" +
					" select "+ clonedRequirementTypeId +", name, description, type,  " +
					" options, required,default_display, default_value, sort_order,   " +
					" impacts_version, impacts_traceability, impacts_approval_workflow, ?, sysdate, ?, sysdate " +
					" from gr_rt_attributes where requirement_type_id = ?	 ";
				}
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setString(1, clonerEmailId);
				prepStmt2.setString(2, clonerEmailId);
				prepStmt2.setInt(3, sourceRequirementTypeId);
				prepStmt2.execute();
				prepStmt2.close();
				

				
				
				// lets create the folders
				// Since folders have a hierarchy, this is very very tricky. 
				// if we blindly copy all folders from project a, to b , then the sub folders in project b
				// will be tagged as children of root folders in project a. So we have to walk through the hierarchy here....
				
				// create teh parents first and then the children.
				
				// first we will loop through all the folders. It's CRITICAL to order by level. ie. 
				sql = "select f.id,  f.name, f.description, f.parent_folder_id, f.folder_level, f.folder_order, f.folder_path," +
					" parent.name \"parent_folder_name\", parent.folder_path \"parent_folder_path\"  " +
					" from gr_folders f left join gr_folders parent on (f.parent_folder_id = parent.id) " +
					" where f.requirement_type_id = ? " +
					" order by f.folder_level , f.id";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1, sourceRequirementTypeId);
				ResultSet rs2 = prepStmt2.executeQuery();
				
				while(rs2.next()) {
					int sourceFolderId = rs2.getInt("id");
					String sourceName = rs2.getString("name");
					String sourceDescription = rs2.getString("description");
					int sourceParentFolderId = rs2.getInt("parent_folder_id");
					int sourceFolderLevel = rs2.getInt("folder_level");
					int sourceFolderOrder = rs2.getInt("folder_order");
					String sourceFolderPath = rs2.getString("folder_path");
					String sourceParentFolderName = rs2.getString("parent_folder_name");
					String sourceParentFolderPath = rs2.getString("parent_folder_path");
				
					// the parentfolderId of the newly created folder, has to be the parent folder in the newly created 
					// clone project.
					// lets get that, if the current folder is a sub folder.
					int clonedParentFolderId = 0;
					if (sourceParentFolderId > 0 ){
						// lets see what the id of a folder with sourceParentFolderName and sourceParentFolderLevel 
						// in cloned project is.
						clonedParentFolderId = cloneProject_getClonedFolderId(con, clonedProjectId, sourceParentFolderName, sourceParentFolderPath);
						
					}
					
					// now lets create the folder in the target project.
					if (databaseType.equals("mySQL")){
						sql = "insert into gr_folders (project_id, name, description, parent_folder_id, " +
							" folder_level, folder_order, folder_path, requirement_type_id, " +
							" created_by, created_dt, last_modified_by, last_modified_dt )" +
							" values (?,?,?,?," +
							" ?,?,?,?," +
							" ?,now(), ?,now()) ";
					}
					else {
						sql = "insert into gr_folders (project_id, name, description, parent_folder_id, " +
						" folder_level, folder_order, folder_path, requirement_type_id, " +
						" created_by, created_dt, last_modified_by, last_modified_dt )" +
						" values (?,?,?,?," +
						" ?,?,?,?," +
						" ?,sysdate, ?, sysdate) ";
					}
					PreparedStatement prepStmt4 = con.prepareStatement(sql);
					prepStmt4.setInt(1, clonedProjectId);
					prepStmt4.setString(2, sourceName);
					prepStmt4.setString(3, sourceDescription);
					prepStmt4.setInt(4, clonedParentFolderId);
					
					prepStmt4.setInt(5, sourceFolderLevel);
					prepStmt4.setInt(6, sourceFolderOrder);
					prepStmt4.setString(7, sourceFolderPath);
					prepStmt4.setInt(8, clonedRequirementTypeId);
					
					prepStmt4.setString(9, clonerEmailId);
					prepStmt4.setString(10, clonerEmailId);


					prepStmt4.execute();
					
				}
				rs2.close();
				prepStmt2.close();
				
			}
			rs.close();
			prepStmt.close();
		
			// THIS IS CRITICAL. We need to set the gr_requirement_seq for all the source requirement types
			// into cloned requirement types.
			// This field is used to get the tag of the next requirement created in the cloned project.
			sql = "select rs.tag, rt.short_name " +
				" from gr_requirements_seq rs , gr_requirement_types rt" +
				" where rs.requirement_type_id = rt.id " +
				" and rt.project_id =  ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceProjectId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int sourceTagSequence =  rs.getInt("tag");
				String sourceRequirementTypePrefix = rs.getString("short_name");
				int clonedRequirementTypeId = cloneProject_getClonedRequirementTypeId(con, clonedProjectId, sourceRequirementTypePrefix);
				
				sql = "insert into gr_requirements_seq (requirement_type_id, tag) " +
					" values (?,?)";
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,clonedRequirementTypeId);
				prepStmt2.setInt(2, 0);
				prepStmt2.execute();
				prepStmt2.close();
		
			}
				
			rs.close();
			prepStmt.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	
	// Part of cloneProject. Creates the Project entry.
	public static void cloneProject_createRolesUsersPermissions(java.sql.Connection con ,int sourceProjectId,
		int clonedProjectId, boolean cloneUsers, String clonerEmailId) {
		try {

			// lets create the Roles in the cloned project.
			String sql = "insert into gr_roles (project_id,name, description)" +
				" select "+ clonedProjectId +" , name, description " +
				" from gr_roles where project_id = ?	 ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceProjectId);
			prepStmt.execute();
			prepStmt.close();
			
			
			// Now loop through all these Roles for the source project for Role
			// add users and set permissions
			sql = " select id, name from gr_roles where project_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceProjectId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				int sourceRoleId = rs.getInt("id");
				String sourceRoleName = rs.getString("name");


				
				int clonedRoleId = cloneProject_getClonedRoleId(con, clonedProjectId, sourceRoleName);
				

				
				// for each source role, lets get a list of source folders and permissions 
				// and try to calculate thew cloned role id, cloned folder Id and then set the same permissions.
				sql = " select rp.folder_id, rp.create_requirement, rp.read_requirement, rp.update_requirement, " +
					" rp.delete_requirement, rp.trace_requirement, rp.approve_requirement, rp.update_attributes, " +
					" f.name \"source_folder_name\", f.folder_path \"source_folder_path\" " +
					" from gr_role_privs rp, gr_folders f" +
					" where rp.role_id = ? " +
					" and rp.folder_id = f.id ";
				
				PreparedStatement prepStmt2 = con.prepareCall(sql);
				prepStmt2.setInt(1, sourceRoleId);
				ResultSet rs2 = prepStmt2.executeQuery();
				while (rs2.next()){
					int sourceFolderId = rs2.getInt("folder_id");
					int sourceCreateRequirement = rs2.getInt("create_requirement");
					int sourceReadRequirement = rs2.getInt("read_requirement");
					int sourceUpdateRequirement = rs2.getInt("update_requirement");
					int sourceDeleteRequirement = rs2.getInt("delete_requirement");
					int sourceTraceRequirement = rs2.getInt("trace_requirement");
					int sourceApproveRequirement = rs2.getInt("approve_requirement");
					String sourceUpdateAttributes = rs2.getString("update_attributes");
				
					String sourceFolderName = rs2.getString("source_folder_name");
					String sourceFolderPath = rs2.getString("source_folder_path");
					

					
					int clonedFolderId = cloneProject_getClonedFolderId(con, clonedProjectId, sourceFolderName, sourceFolderPath);
				

					// now that we have the cloned folder Id, lets insert the rolepriv row.
					sql = "insert into gr_role_privs (role_id, folder_id, " +
						" create_requirement,  read_requirement,  update_requirement, " +
						" delete_requirement, trace_requirement, approve_requirement, update_attributes) " +
						" values (?,?, " +
						" ?,?,?," +
						" ?,?,?,?) ";
					PreparedStatement prepStmt3 = con.prepareStatement(sql);
					prepStmt3.setInt(1, clonedRoleId);
					prepStmt3.setInt(2, clonedFolderId);
				
					prepStmt3.setInt(3, sourceCreateRequirement);
					prepStmt3.setInt(4, sourceReadRequirement);
					prepStmt3.setInt(5, sourceUpdateRequirement);
					
					prepStmt3.setInt(6, sourceDeleteRequirement);
					prepStmt3.setInt(7, sourceTraceRequirement);
					prepStmt3.setInt(8, sourceApproveRequirement);
					prepStmt3.setString(9, sourceUpdateAttributes);
					

					// looks like if a project has had a deleted folder, and at that time gr_role_priv's wasn't cleaned up
					// in the new cloned project, we try to make an entry for this in the new role_privs table, but it can't find the folder. 
					try {
					prepStmt3.execute();
					}
					catch (Exception e){
						e.printStackTrace();
					}
					prepStmt3.close();
				}
				rs2.close();
				prepStmt2.close();
				
				
				// lets add users to the Role.
				if (cloneUsers){
					// lets create the Roles in the cloned project.
					sql = "insert into gr_user_roles (user_id, project_id, role_id)" +
						" select user_id, "+ clonedProjectId +" , " + clonedRoleId + 
						" from gr_user_roles where role_id = ?	 ";
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1, sourceRoleId);
					prepStmt2.execute();
					prepStmt2.close();
				}
				
				// if cloner email id is not already a member of gr_user_role table
				// lets add him as a member.
				sql = "select count(*) \"matches\" " +
					" from gr_projects p, gr_user_roles ur, gr_users u " +
					" where p.id = ? " +
					" and ur.project_id = p.id " + 
					" and ur.user_id = u.id " +
					" and u.email_id = ? ";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1, clonedProjectId);
				prepStmt2.setString(2, clonerEmailId);
				rs2 = prepStmt2.executeQuery();
				int matches = 0;
				while (rs2.next()) {
					matches = rs2.getInt("matches");
				}
				prepStmt2.close();
				rs2.close();
				
				if (matches ==0 ) {
					// this means that the cloner is not a member of the project.
					// so lets add him to all the roles.
					// lets first get the cloner\"s id.
					sql = "select id from gr_users where email_id =  ? ";
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setString(1, clonerEmailId);
					rs2 = prepStmt2.executeQuery();
					int clonerId = 0;
					while (rs2.next()) {
						clonerId = rs2.getInt("id");
					}
					prepStmt2.close();
					rs2.close();	
					
					
					sql = "insert into gr_user_roles (user_id, project_id, role_id)" +
						" values (?, ?, ? ) " ;
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1, clonerId);
					prepStmt2.setInt(2, clonedProjectId);
					prepStmt2.setInt(3, clonedRoleId);
					
					prepStmt2.execute();
					prepStmt2.close();
				}
				
			}
			prepStmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	// Part of cloneProject. Creates the ReportsCan only be called
	// if cloneUsers is true.

	public static void cloneProject_createReports(java.sql.Connection con ,int sourceProjectId,
		int clonedProjectId, String clonerEmailId , String reportType, String databaseType) {
		try {

			// lets create the Saved Reports in the cloned project.
			// we need to loop through every saved report and insert them into the cloned project.
			// we need to loop through, because we need to calculate the cloned folder Id.
			String sql = "";
			if (reportType.equals("Canned")){
				sql = "select  r.name, r.description, r.report_type, " +
					" r.visibility, r.trace_tree_depth, r.report_sql, r.report_definition , " +
					" r.created_by, r.last_modified_by, " +
					" f.name \"source_folder_name\" , f.folder_path \"source_folder_path\"" +
					" from gr_reports r, gr_folders f" +
					" where r.project_id = ? " +
					" and r.folder_id = f.id " +
					" and r.description like 'Canned%'";
			}
			else {
				sql = "select  r.name, r.description, r.report_type, " +
				" r.visibility, r.trace_tree_depth, r.report_sql, r.report_definition , " +
				" r.created_by, r.last_modified_by, " +
				" f.name \"source_folder_name\" , f.folder_path \"source_folder_path\"" +
				" from gr_reports r, gr_folders f" +
				" where r.project_id = ? " +
				" and r.folder_id = f.id " +
				" and r.description not like 'Canned%'";
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceProjectId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				String sourceReportName = rs.getString("name");
				String sourceReportDescription = rs.getString("description");
				String sourceReportType = rs.getString("report_type");
				
				
				String sourceReportVisibility = rs.getString("visibility");
				int sourceReportTraceTreeDepth = rs.getInt("trace_tree_depth");
				String sourceReportSQL = rs.getString("report_sql");
				String sourceReportDefinition = rs.getString("report_definition");
				
				
				String sourceReportCreatedBy = rs.getString("created_by");
				String sourceReportLastModifiedBy = rs.getString("last_modified_by");
				
				String sourceFolderName = rs.getString("source_folder_name");
				String sourceFolderPath = rs.getString("source_folder_path");
				
				int clonedFolderId = cloneProject_getClonedFolderId(con, clonedProjectId, sourceFolderName, sourceFolderPath);
				
				if (databaseType.equals("mySQL")){	
					sql = " insert into gr_reports (project_id, folder_id, name, description, " +
						" report_type, visibility, trace_tree_depth, report_sql, report_definition , " +
						" created_by, created_dt, last_modified_by, last_modified_dt) " +
						" values (?,?,?,?," +
						" ?,?,?,?,?," +
						" ?,now(), ?, now())";
				}
				else {
					sql = " insert into gr_reports (project_id, folder_id, name, description, " +
					" report_type, visibility, trace_tree_depth, report_sql, report_definition , " +
					" created_by, created_dt, last_modified_by, last_modified_dt) " +
					" values (?,?,?,?," +
					" ?,?,?,?,?," +
					" ?,sysdate, ?, sysdate)";
				}
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1, clonedProjectId);
				prepStmt2.setInt(2, clonedFolderId);
				prepStmt2.setString(3, sourceReportName);
				prepStmt2.setString(4, sourceReportDescription);
				
				prepStmt2.setString(5, sourceReportType);
				prepStmt2.setString(6, sourceReportVisibility);
				prepStmt2.setInt(7, sourceReportTraceTreeDepth);
				prepStmt2.setString(8, sourceReportSQL);
				prepStmt2.setString(9, sourceReportDefinition);
				
				// since we want these reports to show up under the correct owners
				// we will clone the owner permissions also.
				prepStmt2.setString(10, sourceReportCreatedBy);
				prepStmt2.setString(11, sourceReportLastModifiedBy);
				
				// looks like when a folder is deleted in a project, we don't clean out gr_reports. So 
				// when a clone project is created, we try to create the new reports here, but
				// the source project folder doesn't exist
				try {
				prepStmt2.execute();
				}
				catch (Exception e){
					e.printStackTrace();
				}
				prepStmt2.close();
				
			}
			rs.close();
			prepStmt.close();
			
			// this is some thing of a cludge.
			// All reports have their report definition stored in a special.
			// This definition gives the filteration and display criteria.
			// while this is not a problem for standard attributes, for custom attributes 
			// we need to do some processsing.
			// all custom attribute filtering is stored as 
			//  :###:customA663:--: delhi:##: san jose:##::###:customA500:--::###:
			// where 663 is the requirement type attribute id of the source project.
			// for all reports, we need to convert this to the cloned project attribute id.
			// to do this we 
			// a) Loop through all the attributes in the source project
			// b) calculate the cloned attribute id
			// c) run a sql that changes the report definition for all reports in this project
			// from sourceCustomA to clonedCustomA
			sql = "select rt.short_name , a.name, a.id" +
				" from gr_rt_attributes a, gr_requirement_types rt" +
				" where a.requirement_type_id = rt.id" +
				" and rt.project_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceProjectId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				String sourceRequirementTypePrefix = rs.getString("short_name");
				String sourceRTAttributeName = rs.getString("name");
				int sourceAttributeId = rs.getInt("id");
				
				// lets get the clonedAttributeId.
				int clonedAttributeId = cloneProject_getClonedAttributeId(con, clonedProjectId, sourceRequirementTypePrefix, sourceRTAttributeName);
			
				// Our goal is to now loop through all the reports in the cloned project
				// and replace every occurance of CustomASourceRTAttributeId with CustomAClonedRTAttributeId.
				sql = "update gr_reports " +
					" set report_definition = replace(report_definition, ?,?)" +
					" where project_id = ?  ";
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setString(1, ":###:customA" + sourceAttributeId);
				prepStmt2.setString(2, ":###:customA" + clonedAttributeId);
				prepStmt2.setInt(3, clonedProjectId);
				prepStmt2.execute();
				
			}
			prepStmt.close();
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


	// Part of cloneProject. Creates the Word Documents. Can only be called
	// if cloneUsers is true.

	public static void cloneProject_createTemplates(java.sql.Connection con ,int sourceProjectId,
		int clonedProjectId, String clonerEmailId, String databaseType) {
		try {

			// lets create the Word Documents in the cloned project.
			// we need to loop through every template, copy it to a new location 
			// and insert them into the cloned project.
			// we need to loop through, because we need to calculate the cloned folder Id
			// and to copy the file to its new location.
			String sql = "select  t.name, t.visibility,  t.description, t.file_path , " +
				" t.created_by, t.last_modified_by, " +
				" f.name \"source_folder_name\" , f.folder_path \"source_folder_path\"" +
				" from gr_word_templates t , gr_folders f" +
				" where t.project_id = ? " +
				" and t.folder_id = f.id ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceProjectId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				String sourceTemplateName = rs.getString("name");
				String sourceTemplateVisibility = rs.getString("visibility");
				String sourceTemplateDescription = rs.getString("description");
				String sourceTemplateFilePath = rs.getString("file_path");
				
				String sourceTemplateCreatedBy = rs.getString("created_by");
				String sourceTemplateLastModifiedBy = rs.getString("last_modified_by");
				
				String sourceFolderName = rs.getString("source_folder_name");
				String sourceFolderPath = rs.getString("source_folder_path");
				

				int clonedFolderId = cloneProject_getClonedFolderId(con, clonedProjectId, sourceFolderName, sourceFolderPath);
				
				// we need to locate and clone the word template file to its new location.
				
				// lets get the source file name and traceCloudRootPath from the sourceTemplateFile.
				String sourceTemplateFileName = (String) sourceTemplateFilePath.substring(sourceTemplateFilePath.lastIndexOf("\\")+1);
				// lets get traceCloudRoot path. You get this by going back 4 levels in the folder hierarrhy
				// of sourceTempalteFilePath.
				String traceCloudRootPath = sourceTemplateFilePath; 
				traceCloudRootPath = 	(String) traceCloudRootPath.subSequence(0, traceCloudRootPath.lastIndexOf("\\"));
				traceCloudRootPath = (String) traceCloudRootPath.subSequence(0,traceCloudRootPath.lastIndexOf("\\"));
				traceCloudRootPath = (String) traceCloudRootPath.subSequence(0,traceCloudRootPath.lastIndexOf("\\"));
				traceCloudRootPath = (String) traceCloudRootPath.subSequence(0,traceCloudRootPath.lastIndexOf("\\"));
				// at this point traceCloudRootPath is something like 'E:/TraceCloud'.

				String clonedTemplateFilePath  = "";
				try{
										
					// if rootDataDirectory/TraceCloud/ProjectId does not exist, lets create it.
					File projectRoot  = new File (traceCloudRootPath  + "/" + clonedProjectId);
					if (!(projectRoot.exists() )){
					    new File(traceCloudRootPath +  "/" +clonedProjectId).mkdir();
					}

					// lets create a template folder in the project.
					File projectTemplateRoot  = new File (traceCloudRootPath + "/" + clonedProjectId + "/Template");
					if (!(projectTemplateRoot.exists() )){
					    new File(traceCloudRootPath + "/" + clonedProjectId + "/Template").mkdir();
					}
					
					// lets create a unique director within the ProjectRoot to store
					// the attachment.
					Calendar cal = Calendar.getInstance();
					String clonedTemplateFolderPath; 
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy-hhmm-ss");
					String today =  sdf.format(cal.getTime());
					clonedTemplateFolderPath = traceCloudRootPath + "\\" + clonedProjectId + "\\Template\\" + 100 + "-" + today ;	    
				    new File(clonedTemplateFolderPath).mkdir();
				    
				    clonedTemplateFilePath = clonedTemplateFolderPath+ "\\" + sourceTemplateFileName;
				    File clonedTemplateFile = new File (clonedTemplateFilePath);
				    File sourceTemplateFile = new File (sourceTemplateFilePath);
					
				    // here is the code that copies the source file to cloned file.
				    FileInputStream from = null;
				    FileOutputStream to = null;
				    try {
				    	from = new FileInputStream(sourceTemplateFile);
				    	to = new FileOutputStream(clonedTemplateFile);
				    	byte[] buffer = new byte[4096];
				    	int bytesRead;

				    	while ((bytesRead = from.read(buffer)) != -1)
				        to.write(buffer, 0, bytesRead); // write
				    } finally {
				      if (from != null)
				        try {
				          from.close();
				        } catch (Exception e) {
				          
				        }
				      if (to != null)
				        try {
				          to.close();
				        } catch (Exception e) {
				          
				        }
				    }
				}
				catch (Exception e){
				
				}
				
				// we need to make the entry in the gr_word_templates table to reflect the new file location.
				if (databaseType.equals("mySQL")){
					sql = " insert into gr_word_templates(project_id, folder_id, name, visibility, description, " +
						" file_path , " +
						" created_by, created_dt, last_modified_by, last_modified_dt) " +
						" values (?,?,?,?,?," +
						" ?," +
						" ?,now(), ?, now())";
				}
				else {
					sql = " insert into gr_word_templates(project_id, folder_id, name, visibility, description, " +
					" file_path , " +
					" created_by, created_dt, last_modified_by, last_modified_dt) " +
					" values (?,?,?,?,?," +
					" ?," +
					" ?,sysdate, ?, sysdate)";
				}
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1, clonedProjectId);
				prepStmt2.setInt(2, clonedFolderId);
				prepStmt2.setString(3, sourceTemplateName);
				prepStmt2.setString(4, sourceTemplateVisibility);
				prepStmt2.setString(5, sourceTemplateDescription);
				
				prepStmt2.setString(6, clonedTemplateFilePath);
				
				// since we want these reports to show up under the correct owners
				// we will clone the owner permissions also.
				prepStmt2.setString(7, sourceTemplateCreatedBy);
				prepStmt2.setString(8, sourceTemplateLastModifiedBy);
				
				prepStmt2.execute();
				prepStmt2.close();					
			}
			rs.close();
			prepStmt.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		

	

	// Part of cloneProject. Creates the Requirements. Can only be called
	// if cloneUsers  and cloneRequirements is true.

	public static void cloneProject_createRequirements(java.sql.Connection con ,int sourceProjectId,
		int clonedProjectId, String clonerEmailId, String databaseType) {
		try {

			// lets create the Saved Reports in the cloned project.
			// we need to loop through every requirement and insert them into the cloned project.
			// we need to loop through, because we need to calculate the cloned folder Id, cloned requirement type id etc....
			String sql = "select  r.name, r.description, r.tag, r.full_tag, " +
				" r.parent_full_tag, r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4, " +
				" r.version, r.approved_by_all_dt, r.approvers, r.status, " +
				" r.priority, r.owner, r.pct_complete, r.external_url, " +
				" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status," +
				" r.deleted, r.created_by, r.last_modified_by , " +
				" f.name \"source_folder_name\", f.folder_path \"source_folder_path\", " +
				" rt.short_name " +
				" from gr_requirements r , gr_folders f , gr_requirement_types rt " +
				" where r.project_id = ? " +
				" and r.folder_id = f.id " +
				" and r.requirement_type_id = rt.id ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceProjectId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				String sourceRequirementName = rs.getString("name");
				String sourceRequirementDescription = rs.getString("description");
				String sourceRequirementTag =  rs.getString("tag");
				String sourceRequirementFullTag = rs.getString("full_tag");
				
				String sourceRequirementParentFullTag = rs.getString("parent_full_tag");
				int tagLevel1 = rs.getInt("tag_level1");
				int tagLevel2 = rs.getInt("tag_level2");
				int tagLevel3 = rs.getInt("tag_level3");
				int tagLevel4 = rs.getInt("tag_level4");
				
				
				int sourceRequirementVersion = rs.getInt("version");
				String sourceRequirementApprovedByAllDt = rs.getString("approved_by_all_dt");
				String sourceRequirementApprovers = rs.getString("approvers");
				String sourceRequirementStatus = rs.getString("status");
				
				String sourceRequirementPriority = rs.getString("priority");
				String sourceRequirementOwner  = rs.getString("owner");
				int sourceRequirementPctComplete = rs.getInt("pct_complete");
				String sourceRequirementExternalURL = rs.getString("external_url");
				
				
				String sourceRequirementTraceTo = rs.getString("trace_to");
				String sourceRequirementTraceFrom = rs.getString("trace_from");
				String sourceRequirementUserDefinedAttributes = rs.getString("user_defined_attributes");
				String sourceRequirementTestingStatus = rs.getString("testing_status");
				
				int sourceRequirementDeleted = rs.getInt("deleted");
				String sourceRequirementCreatedBy = rs.getString("created_by");
				String sourceRequirementLastModifiedBy = rs.getString("last_modified_by");
				
				String sourceFolderName = rs.getString("source_folder_name");
				String sourceFolderPath = rs.getString("source_folder_path");
				
				String sourceRequirementTypePrefix = rs.getString("short_name");
				
				int clonedFolderId = cloneProject_getClonedFolderId(con, clonedProjectId, sourceFolderName, sourceFolderPath);
				int clonedRequirementTypeId = cloneProject_getClonedRequirementTypeId(con, clonedProjectId, sourceRequirementTypePrefix);
				
				
				
				// lets insert the requirement.
				if (databaseType.equals("mySQL")){
					sql = " insert into gr_requirements (requirement_type_id, folder_id, project_id, name, description, " +
						" tag, full_tag, version, approved_by_all_dt, " +
						" parent_full_tag, tag_level1, tag_level2, tag_level3, tag_level4, " +
						" approvers, status, priority, owner, " +
						" pct_complete, external_url, trace_to, trace_from, " +
						" user_defined_attributes, testing_status, deleted, " +
						" created_by, created_dt, last_modified_by, last_modified_dt) " +
						" values (?,?,?,?,?," +
						" ?,?,?,?," +
						" ?,?,?,?,?,"+
						" ?,?,?,?," +
						" ?,?,?,?,"+
						" ?,?,?,"+
						" ?,now(), ?, now())";
				}
				else {
					sql = " insert into gr_requirements (requirement_type_id, folder_id, project_id, name, description, " +
					" tag, full_tag, version, approved_by_all_dt, " +
					" parent_full_tag, tag_level1, tag_level2, tag_level3, tag_level4, " +
					" approvers, status, priority, owner, " +
					" pct_complete, external_url, trace_to, trace_from, " +
					" user_defined_attributes, testing_status, deleted, " +
					" created_by, created_dt, last_modified_by, last_modified_dt) " +
					" values (?,?,?,?,?," +
					" ?,?,?,?," +
					" ?,?,?,?,?,"+
					" ?,?,?,?," +
					" ?,?,?,?,"+
					" ?,?,?,"+
					" ?, sysdate, ?, sysdate)";
					
				}
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1, clonedRequirementTypeId);
				prepStmt2.setInt(2, clonedFolderId);
				prepStmt2.setInt(3, clonedProjectId);
				prepStmt2.setString(4, sourceRequirementName);
				prepStmt2.setString(5, sourceRequirementDescription);
				
				prepStmt2.setString(6, sourceRequirementTag);
				prepStmt2.setString(7, sourceRequirementFullTag);
				prepStmt2.setInt(8, sourceRequirementVersion);
				prepStmt2.setString(9, sourceRequirementApprovedByAllDt);
				
				prepStmt2.setString(10, sourceRequirementParentFullTag);
				prepStmt2.setInt(11, tagLevel1);
				prepStmt2.setInt(12, tagLevel2);
				prepStmt2.setInt(13, tagLevel3);
				prepStmt2.setInt(14, tagLevel4);
				
				
				prepStmt2.setString(15, sourceRequirementApprovers);
				prepStmt2.setString(16, sourceRequirementStatus);
				prepStmt2.setString(17, sourceRequirementPriority);
				prepStmt2.setString(18, sourceRequirementOwner);
				
				prepStmt2.setInt(19, sourceRequirementPctComplete);
				prepStmt2.setString(20, sourceRequirementExternalURL);
				prepStmt2.setString(21, sourceRequirementTraceTo);
				prepStmt2.setString(22, sourceRequirementTraceFrom);
				
				prepStmt2.setString(23, sourceRequirementUserDefinedAttributes);
				prepStmt2.setString(24, sourceRequirementTestingStatus);
				prepStmt2.setInt(25, sourceRequirementDeleted);
				
				// since we want these reports to show up under the correct owners
				// we will clone the owner permissions also.
				prepStmt2.setString(26, sourceRequirementCreatedBy);
				prepStmt2.setString(27, sourceRequirementLastModifiedBy);
				
				prepStmt2.execute();
				prepStmt2.close();
				
			}
			
			// THIS IS CRITICAL. We need to set the gr_requirement_seq for all the source requirement types
			// into cloned requirement types.
			// This field is used to get the tag of the next requirement created in the cloned project.
			
			// first lets drop the already inserted seq numbers.
			sql = "select rs.tag, rt.short_name " +
				" from gr_requirements_seq rs , gr_requirement_types rt" +
				" where rs.requirement_type_id = rt.id " +
				" and rt.project_id =  ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceProjectId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				String sourceTagSequence =  rs.getString("tag");
				String sourceRequirementTypePrefix = rs.getString("short_name");
				int clonedRequirementTypeId = cloneProject_getClonedRequirementTypeId(con, clonedProjectId, sourceRequirementTypePrefix);
				
				sql = "update gr_requirements_seq set tag = ? where requirement_type_id = ? ";
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1, Integer.parseInt(sourceTagSequence));
				prepStmt2.setInt(2,clonedRequirementTypeId);
				prepStmt2.execute();
				prepStmt2.close();
		
			}
				
			rs.close();
			prepStmt.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	// Part of cloneProject. Creates the Traces. Can only be called
	// if cloneUsers  and cloneRequirements and clone Traces is true.

	public static void cloneProject_createTraces(java.sql.Connection con ,int sourceProjectId,
		int clonedProjectId, String clonerEmailId) {
		try {

			// lets create the Traces in the cloned project.
			// we need to loop through every trace in the source project and insert them into the cloned project.
			// we need to loop through, because we need to calculate the cloned requirement Id,for both from and to requirements of the trace.
			String sql = "select  t.description, t.suspect, from_req.full_tag \"from_requirement_tag\", to_req.full_tag  \"to_requirement_tag\",  "
					+ "to_req.project_id  \"to_project_id\", to_req.id \"to_requirement_id\" " +
				" from gr_traces t , gr_requirements from_req, gr_requirements to_req " +
				" where t.from_requirement_id = from_req.id " +
				" and from_req.project_id = ? " +
				" and t.to_requirement_id = to_req.id" ;
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceProjectId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				String sourceTraceDescription = rs.getString("description");
				int sourceTraceSuspect = rs.getInt("suspect");
				String sourceFromRequirementFullTag = rs.getString("from_requirement_tag");
				String sourceToRequirementFullTag = rs.getString("to_requirement_tag");
				int toProjectId = rs.getInt("to_project_id");
				int toRequirementId = rs.getInt("to_requirement_id");
								
				
				try{
					// lets insert the requirement.
					sql = " insert into gr_traces (description, from_requirement_id, to_requirement_id, suspect)" +
						" values (?,?,?,?) ";
					
					PreparedStatement prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setString(1, sourceTraceDescription);

					if (toProjectId != sourceProjectId){
						// this means that this requirement in the source project, was actually tracing to 
						// a requirement in an external project
						// in this case, we need to create a trace from the cloned requirement to the external requirement , which 
						// has not been cloned
						int clonedFromRequirementId = cloneProject_getClonedRequirementId(con, clonedProjectId, sourceFromRequirementFullTag);

						prepStmt2.setInt(2, clonedFromRequirementId);
						prepStmt2.setInt(3, toRequirementId);

					}
					else {
						// this means that the source and target requireemnt are in the same project and both have 
						// been cloned

						int clonedFromRequirementId = cloneProject_getClonedRequirementId(con, clonedProjectId, sourceFromRequirementFullTag);
						int clonedToRequirementId = cloneProject_getClonedRequirementId(con, clonedProjectId, sourceToRequirementFullTag);

						prepStmt2.setInt(2, clonedFromRequirementId);
						prepStmt2.setInt(3, clonedToRequirementId);
							
					}

					
					prepStmt2.setInt(4, sourceTraceSuspect);
					
					
					prepStmt2.execute();
					prepStmt2.close();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			rs.close();
			prepStmt.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// Part of cloneProject. Creates the Attribute Values of Requirements. Can only be called
	// if cloneUsers  and cloneRequirements is true.

	public static void cloneProject_createAttributeValues(java.sql.Connection con ,int sourceProjectId,
		int clonedProjectId, String clonerEmailId, String databaseType) {
		String errorMessageString = "";
		try {

			// lets create the attribute values in the cloned project.
			// we need to loop through every attribute value in the source project and insert them into the cloned project.
			// we need to loop through, because we need to calculate the cloned requirement type attribute Id,for these attribute values .
			String sql = "select  av.value , r.full_tag , rt.short_name , a.name " +
				" from gr_r_attribute_values av, gr_requirements r, gr_requirement_types rt , gr_rt_attributes a " +
				" where av.requirement_id = r.id " +
				" and r.project_id = ? " +
				" and r.requirement_type_id = rt.id " +
				" and av.attribute_id = a.id " ;
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceProjectId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				try{
				String sourceAttributeValue= rs.getString("value");
				String sourceRequirementFullTag = rs.getString("full_tag");
				String sourceRequirementTypePrefix = rs.getString("short_name");
				String sourceRTAttributeName = rs.getString("name");
				
				int clonedRequirementId = cloneProject_getClonedRequirementId(con, clonedProjectId, sourceRequirementFullTag);
				
				int clonedAttributeId = cloneProject_getClonedAttributeId(con, clonedProjectId, sourceRequirementTypePrefix, sourceRTAttributeName);
				
				
				// lets insert the attribute value.
				if (databaseType.equals("mySQL")){
					sql = " insert into gr_r_attribute_values (requirement_id, attribute_id, value, " +
						" created_by, created_dt, last_modified_by, last_modified_dt)" +
						" values (?,?,?," +
						" ?,now(), ?, now()) ";
				}
				else {
					sql = " insert into gr_r_attribute_values (requirement_id, attribute_id, value, " +
					" created_by, created_dt, last_modified_by, last_modified_dt)" +
					" values (?,?,?," +
					" ?, sysdate , ?, sysdate) ";
				}
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1, clonedRequirementId);
				prepStmt2.setInt(2, clonedAttributeId);
				prepStmt2.setString(3, sourceAttributeValue);
				
				prepStmt2.setString(4, clonerEmailId);
				prepStmt2.setString(5, clonerEmailId);
				
				prepStmt2.execute();
				prepStmt2.close();
				}
				catch(Exception insertRAVException){
					
					insertRAVException.printStackTrace();
					System.out.println("srt error happened here " + errorMessageString);
				}
				
			}
			rs.close();
			prepStmt.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// Part of cloneProject. Creates the Attribute Values of Requirements. Can only be called
	// if cloneUsers  and cloneRequirements is true.

	public static void cloneProject_createRequirementBaselines(java.sql.Connection con ,int sourceProjectId,
		int clonedProjectId, String clonerEmailId, String databaseType) {
		try {

			
			// lets create the Requirement Baselines in the cloned project.
			// prior to creating the baselines, we have to ensure that the requirement logs are also copied over.
			// this is because the baselines refer to a version of the requirement.
			// lets loop through all the versions and then insert them in to the cloned project.
			// we need to loop through, as we need to get the cloned requirement Id.
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "select  v.version, v.name, v.description, v.approvers," +
					" v.status, v.priority, v.owner, v.pct_complete, " +
					" v.external_url, v.trace_to, v.trace_from, v.user_defined_attributes," +
					" v.created_by, v.created_dt, r.full_tag " +
					" from gr_requirement_versions v, gr_requirements r " +
				" where v.requirement_id = r.id " +
				" and r.project_id = ? ";
			}
			else {
				sql = "select  v.version, v.name, v.description, v.approvers," +
					" v.status, v.priority, v.owner, v.pct_complete, " +
					" v.external_url, v.trace_to, v.trace_from, v.user_defined_attributes," +
					" v.created_by, to_char(v.created_dt, 'DD MON YYYY') \"created_dt\", r.full_tag " +
					" from gr_requirement_versions v, gr_requirements r " +
				" where v.requirement_id = r.id " +
				" and r.project_id = ? ";
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceProjectId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				
				int sourceVersion= rs.getInt("version");
				String sourceVersionName = rs.getString("name");
				String sourceVersionDescription = rs.getString("description");
				String sourceVersionApprovers  = rs.getString("approvers");
				String sourceVersionStatus = rs.getString("status");
				String sourceVersionPriority = rs.getString("priority");
				String sourceVersionOwner = rs.getString("owner");
				int sourceVersionPctComplete = rs.getInt("pct_complete");
				String sourceVersionExternalURL = rs.getString("external_url");
				String sourceVersionTraceTo= rs.getString("trace_to");
				String sourceVersionTraceFrom = rs.getString("trace_from");
				String sourceVersionUDA = rs.getString("user_defined_attributes");
				String sourceVersionCreatedBy= rs.getString("created_by");
				String sourceVersionCreatedDt = rs.getString("created_dt");
				String sourceRequirementFullTag = rs.getString("full_tag");
				
				int clonedRequirementId = cloneProject_getClonedRequirementId(con, clonedProjectId, sourceRequirementFullTag);
				
				// lets insert the version.
				if (databaseType.equals("mySQL")){
					sql = " insert into gr_requirement_versions(requirement_id, version, name, description, " +
						" approvers, status, priority, owner, " +
						" pct_complete, external_url, trace_to, trace_from, " +
						" user_defined_attributes , created_by, created_dt )" +
					" values (?,?,?,?," +
					" ?,?,?,?," +
					" ?,?,?,?, " +
					" ?,?,? ) ";
				}
				else {
					sql = " insert into gr_requirement_versions(requirement_id, version, name, description, " +
						" approvers, status, priority, owner, " +
						" pct_complete, external_url, trace_to, trace_from, " +
						" user_defined_attributes , created_by, created_dt )" +
					" values (?,?,?,?," +
					" ?,?,?,?," +
					" ?,?,?,?, " +
					" ?,?, to_date(?,'DD MON YYYY' )) ";
				}
					
				
				
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1, clonedRequirementId);
				prepStmt2.setInt(2, sourceVersion);
				prepStmt2.setString(3, sourceVersionName);
				prepStmt2.setString(4, sourceVersionDescription);
				
				prepStmt2.setString(5, sourceVersionApprovers);
				prepStmt2.setString(6, sourceVersionStatus);
				prepStmt2.setString(7, sourceVersionPriority);
				prepStmt2.setString(8, sourceVersionOwner);
				
				prepStmt2.setInt(9, sourceVersionPctComplete);
				prepStmt2.setString(10, sourceVersionExternalURL);
				prepStmt2.setString(11, sourceVersionTraceTo);
				prepStmt2.setString(12, sourceVersionTraceFrom);

				prepStmt2.setString(13, sourceVersionUDA);
				prepStmt2.setString(14, sourceVersionCreatedBy);
				prepStmt2.setString(15, sourceVersionCreatedDt);
				
				
				
				prepStmt2.execute();
				prepStmt2.close();
				
			}
			rs.close();
			prepStmt.close();
			
			
			// we need to loop through every Baseline in the source project and insert them into the cloned project.
			// we need to loop through, because we need to calculate the cloned requirement id and cloned baseline id
			if (databaseType.equals("mySQL")){
				sql = "select  b.version_id,  b.baselined_dt, r.full_tag , rt.short_name , rtb.name \"rt_baseline_name\" " +
				" from gr_requirement_baselines b, gr_requirements r, gr_requirement_types rt , gr_rt_baselines rtb " +
				" where b.requirement_id = r.id " +
				" and r.project_id =  " + sourceProjectId + 
				" and r.requirement_type_id = rt.id " +
				" and b.rt_baseline_id = rtb.id" ;
			}
			else {
				sql = "select  b.version_id,  to_char(b.baselined_dt, 'DD MON YYYY') \"baselined_dt\" , r.full_tag , rt.short_name , rtb.name \"rt_baseline_name\" " +
				" from gr_requirement_baselines b, gr_requirements r, gr_requirement_types rt , gr_rt_baselines rtb " +
				" where b.requirement_id = r.id " +
				" and r.project_id =  " + sourceProjectId + 
				" and r.requirement_type_id = rt.id " +
				" and b.rt_baseline_id = rtb.id" ;
			}
				
			
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				
				int sourceBaselineVersionId= rs.getInt("version_id");
				String sourceBaselinedDt = rs.getString("baselined_dt");
				String sourceRequirementFullTag = rs.getString("full_tag");
				String sourceRequirmentTypePrefix = rs.getString("short_name");
				String sourceRTBaselineName = rs.getString("rt_baseline_name");
				
				int clonedRequirementId = cloneProject_getClonedRequirementId(con, clonedProjectId, sourceRequirementFullTag);
				int clonedRTBaselineId = cloneProject_getClonedRTBaselineId(con, clonedProjectId, sourceRequirmentTypePrefix, sourceRTBaselineName);
				
				
				// lets insert the attribute value.
				if (databaseType.equals("mySQL")){
					sql = " insert into gr_requirement_baselines(requirement_id, version_id, rt_baseline_id, baselined_dt) " +
					" values (?,?,?,?) ";
				}
				else {
					sql = " insert into gr_requirement_baselines(requirement_id, version_id, rt_baseline_id, baselined_dt) " +
					" values (?,?,?,to_date(?,'DD MON YYYY' )) ";
					
				}
				
				
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1, clonedRequirementId);
				prepStmt2.setInt(2, sourceBaselineVersionId);
				prepStmt2.setInt(3, clonedRTBaselineId);
				prepStmt2.setString(4, sourceBaselinedDt);
				
				prepStmt2.execute();
				prepStmt2.close();
				
			}
			rs.close();
			prepStmt.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// Part of cloneProject. Creates the Comments of Requirements. Can only be called
	// if cloneUsers  and cloneRequirements is true.

	public static void cloneProject_createRequirementComments(java.sql.Connection con ,int sourceProjectId,
		int clonedProjectId, String clonerEmailId, String databaseType) {
		try 	{

			
			// lets create the Requirement Comments in the cloned project.
			// lets loop through all the comments and then insert them in to the cloned project.
			// we need to loop through, as we need to get the cloned requirement Id.
			String sql = "";
			
			
			if (databaseType.equals("mySQL")){
				sql = "select  c.version,c.commenter_email_id, c.comment_note, c.comment_dt, r.full_tag" + 
					" from gr_requirement_comments c, gr_requirements r " +
				" where c.requirement_id = r.id " +
				" and r.project_id = ? ";
			}
			else {
				sql = "select  c.version,c.commenter_email_id, c.comment_note,to_char( c.comment_dt, 'DD MON YYYY') \"comment_dt\" , r.full_tag" + 
					" from gr_requirement_comments c, gr_requirements r " +
				" where c.requirement_id = r.id " +
				" and r.project_id = ? ";
			}
				
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceProjectId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				
				int sourceVersion= rs.getInt("version");
				String sourceCommenterEmailId = rs.getString("commenter_email_id");
				String sourceComment_note = rs.getString("comment_note");
				String sourceCommentDt  = rs.getString("comment_dt");
				String sourceRequirementFullTag = rs.getString("full_tag");
				
				int clonedRequirementId = cloneProject_getClonedRequirementId(con, clonedProjectId, sourceRequirementFullTag);
				
				// lets insert the version.
				if (databaseType.equals("mySQL")){
					sql = " insert into gr_requirement_comments(requirement_id, version, commenter_email_id, comment_note, comment_dt ) " +
					" values (?,?,?,?,?) ";
				}
				else {
					sql = " insert into gr_requirement_comments(requirement_id, version, commenter_email_id, comment_note, comment_dt ) " +
					" values (?,?,?,?,to_date(?,'DD MON YYYY' )) ";
					
				}
				
				
				
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1, clonedRequirementId);
				prepStmt2.setInt(2, sourceVersion);
				prepStmt2.setString(3, sourceCommenterEmailId);
				prepStmt2.setString(4, sourceComment_note);
				prepStmt2.setString(5, sourceCommentDt);
				
				prepStmt2.execute();
				prepStmt2.close();
				
			}
			rs.close();
			prepStmt.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	// Part of cloneProject. Creates the Requirement Attachments Can only be called
	// if cloneUsers and clone requirements  is true.

	public static void cloneProject_createRequirementAttachments(java.sql.Connection con ,int sourceProjectId,
		int clonedProjectId, String clonerEmailId, String databaseType) {
		try {

			// lets create the Requirement Attachments in the cloned project.
			// we need to loop through every atachment , copy it to a new location 
			// and insert them into the cloned project.
			// we need to loop through, because we need to calculate the cloned requirement Id
			// and to copy the file to its new location.
			String sql = "";
			
			
			
			if (databaseType.equals("mySQL")){
				sql = "select  a.file_name, a.file_path, a.title, a.created_by, a.created_dt, r.full_tag " +
				" from gr_requirement_attachments a , gr_requirements r" +
				" where a.requirement_id = r.id " +
				" and r.project_id = ? ";
			}
			else {
				sql = "select  a.file_name, a.file_path, a.title, a.created_by, to_char( a.created_dt, 'DD MON YYYY') \"created_dt\", r.full_tag " +
				" from gr_requirement_attachments a , gr_requirements r" +
				" where a.requirement_id = r.id " +
				" and r.project_id = ? ";
							
			}
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceProjectId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				String sourceAttachmentFilePath  = rs.getString("file_path");
				String sourceTitle = rs.getString("title");
				String sourceCreatedBy = rs.getString("created_by");
				
				String sourceCreatedDt = rs.getString("created_dt");
				String sourceRequirementFullTag = rs.getString("full_tag");
				
				int clonedRequirementId = cloneProject_getClonedRequirementId(con, clonedProjectId, sourceRequirementFullTag);
				
				// we need to locate and clone the attachment file to its new location.
				
				// lets get the source file name and traceCloudRootPath from the sourceAttachmentFile.
				String sourceAttachmentFileName = (String) sourceAttachmentFilePath.substring(sourceAttachmentFilePath.lastIndexOf("\\")+1);
				// lets get traceCloudRoot path. You get this by going back 4 levels in the folder hierarrhy
				// of sourceTempalteFilePath.
				String traceCloudRootPath = sourceAttachmentFilePath; 
				traceCloudRootPath = 	(String) traceCloudRootPath.subSequence(0, traceCloudRootPath.lastIndexOf("\\"));
				traceCloudRootPath = (String) traceCloudRootPath.subSequence(0,traceCloudRootPath.lastIndexOf("\\"));
				traceCloudRootPath = (String) traceCloudRootPath.subSequence(0,traceCloudRootPath.lastIndexOf("\\"));
				traceCloudRootPath = (String) traceCloudRootPath.subSequence(0,traceCloudRootPath.lastIndexOf("\\"));
				// at this point traceCloudRootPath is something like 'E:/TraceCloud'.

				String clonedAttachmentFilePath  = "";
				try{
										
					// if rootDataDirectory/TraceCloud/ProjectId does not exist, lets create it.
					File projectRoot  = new File (traceCloudRootPath  + "/" + clonedProjectId);
					if (!(projectRoot.exists() )){
					    new File(traceCloudRootPath +  "/" +clonedProjectId).mkdir();
					}

					// lets create a Attachments folder in the project.
					File projectTemplateRoot  = new File (traceCloudRootPath + "/" + clonedProjectId + "/Attachment");
					if (!(projectTemplateRoot.exists() )){
					    new File(traceCloudRootPath + "/" + clonedProjectId + "/Attachment").mkdir();
					}
					
					// lets create a unique directory within the ProjectRoot to store
					// the attachment.
					Calendar cal = Calendar.getInstance();
					String clonedTemplateFolderPath; 
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy-hhmm-ss");
					String today =  sdf.format(cal.getTime());
					String clonedAttachmentFolderPath = traceCloudRootPath + "\\" + clonedProjectId + "\\Attachment\\" + 100 + "-" + today ;	    
				    new File(clonedAttachmentFolderPath).mkdir();
				    
				    clonedAttachmentFilePath = clonedAttachmentFolderPath+ "\\" + sourceAttachmentFileName;
				    File clonedAttachmentFile = new File (clonedAttachmentFilePath);
				    File sourceAttachmentFile = new File (sourceAttachmentFilePath);
					    
				    // here is the code that copies the source file to cloned file.
				    FileInputStream from = null;
				    FileOutputStream to = null;
				    try {
				    	from = new FileInputStream(sourceAttachmentFile);
				    	to = new FileOutputStream(clonedAttachmentFile);
				    	byte[] buffer = new byte[4096];
				    	int bytesRead;

				    	while ((bytesRead = from.read(buffer)) != -1)
				        to.write(buffer, 0, bytesRead); // write
				    } finally {
				      if (from != null)
				        try {
				          from.close();
				        } catch (Exception e) {
				          
				        }
				      if (to != null)
				        try {
				          to.close();
				        } catch (Exception e) {
				          
				        }
				    }
				}
				catch (Exception e){
				
				}
				
				// we need to make the entry in the gr_word_templates table to reflect the new file location.
				if (databaseType.equals("mySQL")){
					sql = " insert into gr_requirement_attachments(requirement_id, file_name, file_path, title, created_by, created_dt) " +
					" values (?,?,?,?,?,?)";
				}
				else {
					sql = " insert into gr_requirement_attachments(requirement_id, file_name, file_path, title, created_by, created_dt) " +
					" values (?,?,?,?,?,to_date(?,'DD MON YYYY' ))";
					
				}
				
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1, clonedRequirementId);
				prepStmt2.setString(2, sourceAttachmentFileName);
				prepStmt2.setString(3, clonedAttachmentFilePath);
				prepStmt2.setString(4, sourceTitle);
				prepStmt2.setString(5, sourceCreatedBy);
				prepStmt2.setString(6, sourceCreatedDt);
				
				prepStmt2.execute();
				prepStmt2.close();					
			}
			rs.close();
			prepStmt.close();						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	

	// Part of cloneProject. Creates the Project Metrics. Can only be called
	// if cloneMetrics is true.

	public static void cloneProject_createProjectAndUserMetrics(java.sql.Connection con ,int sourceProjectId,
		int clonedProjectId, String clonerEmailId) {
		try 	{
			// lets insert the project metrics.
			String sql = " insert into gr_project_metrics(" +
				" project_id, requirement_type_short_name, data_load_dt," +
				" num_of_requirements, num_of_test_pending_reqs, num_of_test_pass_reqs , " +
				" num_of_test_fail_reqs, num_of_draft_reqs, num_of_in_workflow_reqs, " +
				" num_of_rejected_reqs, num_of_approved_reqs, num_of_dangling_reqs, " +
				" num_of_orphan_reqs, num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
				" num_of_completed_reqs, num_of_incomplete_reqs) " +
				" select  " + clonedProjectId + "," + 
				" requirement_type_short_name, data_load_dt," +
				" num_of_requirements, num_of_test_pending_reqs, num_of_test_pass_reqs , " +
				" num_of_test_fail_reqs, num_of_draft_reqs, num_of_in_workflow_reqs, " +
				" num_of_rejected_reqs, num_of_approved_reqs, num_of_dangling_reqs, " +
				" num_of_orphan_reqs, num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
				" num_of_completed_reqs, num_of_incomplete_reqs" +
				" from gr_project_metrics where project_id = ? ";
			
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceProjectId);
			prepStmt.execute();
			prepStmt.close();
			
			
			// lets insert the user metrics.
			sql = " insert into gr_user_metrics(" +
				" owner, project_id, requirement_type_short_name, data_load_dt," +
				" num_of_requirements, num_of_test_pending_reqs, num_of_test_pass_reqs , " +
				" num_of_test_fail_reqs, num_of_draft_reqs, num_of_in_workflow_reqs, " +
				" num_of_rejected_reqs, num_of_approved_reqs, num_of_dangling_reqs, " +
				" num_of_orphan_reqs, num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
				" num_of_completed_reqs, num_of_incomplete_reqs) " +
				" select  owner, " + clonedProjectId + "," + 
				" requirement_type_short_name, data_load_dt," +
				" num_of_requirements, num_of_test_pending_reqs, num_of_test_pass_reqs , " +
				" num_of_test_fail_reqs, num_of_draft_reqs, num_of_in_workflow_reqs, " +
				" num_of_rejected_reqs, num_of_approved_reqs, num_of_dangling_reqs, " +
				" num_of_orphan_reqs, num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
				" num_of_completed_reqs, num_of_incomplete_reqs" +
				" from gr_user_metrics where project_id = ? ";
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceProjectId);
			prepStmt.execute();
			prepStmt.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void cloneProject_createReleaseMetrics(java.sql.Connection con ,int sourceProjectId,
			int clonedProjectId, String clonerEmailId) {
			try 	{
				
				// lets get the list of release id in the source project release metrics.
				// then for each one of these, we will calculate the cloned release object
				// then insert metrics for each one of these releases.
				String sql = "select distinct r.id, r.full_tag " +
					" from gr_release_metrics rm, gr_requirements r " +
					" where rm.project_id = ? " +
					" and rm.release_id = r.id  ";
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, sourceProjectId);
				ResultSet rs = prepStmt.executeQuery();
				while (rs.next()){
					// for each source release object, lets get the cloned release requirement id.
					int sourceReleaseRequirementId = rs.getInt("id");
					String sourceReleaseRequirementFullTag = rs.getString("full_tag");
					

					int clonedReleaseRequirementId = cloneProject_getClonedRequirementId(con, clonedProjectId, sourceReleaseRequirementFullTag);
					
					// now for each source project , source release combos, lets get the metrics
					// and insert them into the cloned project, cloned release combos.
					// lets insert the release metrics.
					sql = " insert into gr_release_metrics(" +
							" project_id, release_id, release_full_tag," +
							" requirement_type_short_name, data_load_dt," +
							" num_of_requirements, num_of_test_pending_reqs, num_of_test_pass_reqs , " +
							" num_of_test_fail_reqs, num_of_draft_reqs, num_of_in_workflow_reqs, " +
							" num_of_rejected_reqs, num_of_approved_reqs, num_of_dangling_reqs, " +
							" num_of_orphan_reqs, num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
							" num_of_completed_reqs, num_of_incomplete_reqs) " +
							" select  " + clonedProjectId + ", " + clonedReleaseRequirementId + "," +
							"\"" + sourceReleaseRequirementFullTag + "\"," + 
							" requirement_type_short_name, data_load_dt," +
							" num_of_requirements, num_of_test_pending_reqs, num_of_test_pass_reqs , " +
							" num_of_test_fail_reqs, num_of_draft_reqs, num_of_in_workflow_reqs, " +
							" num_of_rejected_reqs, num_of_approved_reqs, num_of_dangling_reqs, " +
							" num_of_orphan_reqs, num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
							" num_of_completed_reqs, num_of_incomplete_reqs" +
							" from gr_release_metrics where project_id = ? and release_id = ?  ";
						
						
						PreparedStatement prepStmt2 = con.prepareStatement(sql);
						prepStmt2.setInt(1, sourceProjectId);
						prepStmt2.setInt(2, sourceReleaseRequirementId);
						
						prepStmt2.execute();
						prepStmt2.close();
					
				}
				rs.close();
				prepStmt.close();
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	
	public static void cloneProject_createReleaseRequirements(java.sql.Connection con ,int sourceProjectId,
			int clonedProjectId, String clonerEmailId) {
			try 	{
				
				// lets get the list of release requirements in the source project .
				// then for each one of these, we will calculate the cloned requirement id 
				// then insert metrics for each one of these releases.
				String sql = "select rr.release_id, rr.release_full_tag, rr.requirement_id, rr.requirement_full_tag, " +
					" rr.requirement_type_short_name, rr.data_load_dt " +
					" from gr_release_requirements rr " +
					" where rr.project_id = ? ";
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, sourceProjectId);
				ResultSet rs = prepStmt.executeQuery();
				while (rs.next()){
					// for each source release object, lets get the cloned release requirement id.
					int sourceReleaseRequirementId = rs.getInt("release_id");
					String sourceReleaseRequirementFullTag = rs.getString("release_full_tag");
					
					int sourceRequirementId = rs.getInt("requirement_id");
					String sourceRequirementFullTag = rs.getString("requirement_full_tag");
					
					String sourceRequirementTypeShortName = rs.getString("requirement_type_short_name");
					String sourceDataLoadDt = rs.getString("data_load_dt");
			
					
					int clonedReleaseRequirementId = cloneProject_getClonedRequirementId(con, clonedProjectId, sourceReleaseRequirementFullTag);
					int clonedRequirementId = cloneProject_getClonedRequirementId(con, clonedProjectId, sourceRequirementFullTag);
					
					// lets insert the release requirements.
					sql = " insert into gr_release_requirements(" +
							" project_id, release_id, release_full_tag," +
							" requirement_id, requirement_full_tag, requirement_type_short_name, data_load_dt )" +
							" values (?,?,?, " +
							" ?,?,?,? ) ";
					PreparedStatement prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1, clonedProjectId);
					prepStmt2.setInt(2, clonedReleaseRequirementId);
					prepStmt2.setString(3, sourceReleaseRequirementFullTag);
					
					prepStmt2.setInt(4, clonedRequirementId);
					prepStmt2.setString(5, sourceRequirementFullTag);
					prepStmt2.setString(6, sourceRequirementTypeShortName);
					prepStmt2.setString(7, sourceDataLoadDt);
					
					prepStmt2.execute();
					prepStmt2.close();
				
				}
				rs.close();
				prepStmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	public static void cloneProject_createFolderMetrics(java.sql.Connection con ,int sourceProjectId,
			int clonedProjectId, String clonerEmailId) {
			try 	{
				
				// lets get the list of folders id in the source project folder metrics.
				// then for each one of these, we will calculate the cloned folder object
				// then insert metrics for each one of these folders .
				String sql = "select distinct f.id, f.name, f.folder_path " +
					" from gr_folder_metrics fm, gr_folders f " +
					" where fm.project_id = ? " +
					" and fm.folder_id = f.id  ";
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, sourceProjectId);
				ResultSet rs = prepStmt.executeQuery();
				while (rs.next()){
					// for each source release object, lets get the cloned release requirement id.
					int sourceFolderId = rs.getInt("id");
					String sourceFolderName= rs.getString("name");
					String sourceFolderPath= rs.getString("folder_path");
		
					int clonedFolderId = cloneProject_getClonedFolderId(con, clonedProjectId, sourceFolderName, sourceFolderPath);
					
					// now for each source project, source folder combos, lets get the metrics
					// and insert them into the cloned project, cloned folder combos.
					// lets insert the folder metrics.
					sql = " insert into gr_folder_metrics(" +
							" project_id, folder_id," +
							" requirement_type_short_name, data_load_dt," +
							" num_of_requirements, num_of_test_pending_reqs, num_of_test_pass_reqs , " +
							" num_of_test_fail_reqs, num_of_draft_reqs, num_of_in_workflow_reqs, " +
							" num_of_rejected_reqs, num_of_approved_reqs, num_of_dangling_reqs, " +
							" num_of_orphan_reqs, num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
							" num_of_completed_reqs, num_of_incomplete_reqs) " +
							" select  " + clonedProjectId + ", " + clonedFolderId + "," +
							" requirement_type_short_name, data_load_dt," +
							" num_of_requirements, num_of_test_pending_reqs, num_of_test_pass_reqs , " +
							" num_of_test_fail_reqs, num_of_draft_reqs, num_of_in_workflow_reqs, " +
							" num_of_rejected_reqs, num_of_approved_reqs, num_of_dangling_reqs, " +
							" num_of_orphan_reqs, num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
							" num_of_completed_reqs, num_of_incomplete_reqs" +
							" from gr_folder_metrics where project_id = ? and folder_id= ?  ";
						
						
						PreparedStatement prepStmt2 = con.prepareStatement(sql);
						prepStmt2.setInt(1, sourceProjectId);
						prepStmt2.setInt(2, sourceFolderId);
						
						prepStmt2.execute();
						prepStmt2.close();
					
				}
				rs.close();
				prepStmt.close();
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	public static void cloneProject_createBaselineMetrics(java.sql.Connection con ,int sourceProjectId,
			int clonedProjectId, String clonerEmailId) {
			try 	{
				
				// lets get the list of Baseline id in the source project Baseline metrics.
				// then for each one of these, we will calculate the cloned Baseline object
				// then insert metrics for each one of these folders .
				String sql = "select distinct b.id, b.name, rt.short_name " +
					" from gr_baseline_metrics bm, gr_rt_baselines b, gr_requirement_types rt " +
					" where bm.project_id = ? " +
					" and bm.rt_baseline_id = b.id " +
					" and b.requirement_type_id = rt.id  ";
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, sourceProjectId);
				ResultSet rs = prepStmt.executeQuery();
				while (rs.next()){
					// for each source release object, lets get the cloned release requirement id.
					int sourceRTBaselineId = rs.getInt("id");
					String sourceRTBaselineName= rs.getString("name");
					String sourceRequirementTypePrefix= rs.getString("short_name");	
					int clonedRTBaselineId = cloneProject_getClonedRTBaselineId(con, clonedProjectId, sourceRequirementTypePrefix, sourceRTBaselineName);
					
					// now for each source project, source rt baseline combos, lets get the metrics
					// and insert them into the cloned project, cloned rt baseline combos.
					// lets insert the baseline metrics.
					sql = " insert into gr_baseline_metrics(" +
							" project_id, rt_baseline_id," +
							" requirement_type_short_name, data_load_dt," +
							" num_of_requirements, num_of_test_pending_reqs, num_of_test_pass_reqs , " +
							" num_of_test_fail_reqs, num_of_draft_reqs, num_of_in_workflow_reqs, " +
							" num_of_rejected_reqs, num_of_approved_reqs, num_of_dangling_reqs, " +
							" num_of_orphan_reqs, num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
							" num_of_completed_reqs, num_of_incomplete_reqs) " +
							" select  " + clonedProjectId + ", " + clonedRTBaselineId + "," +
							" requirement_type_short_name, data_load_dt," +
							" num_of_requirements, num_of_test_pending_reqs, num_of_test_pass_reqs , " +
							" num_of_test_fail_reqs, num_of_draft_reqs, num_of_in_workflow_reqs, " +
							" num_of_rejected_reqs, num_of_approved_reqs, num_of_dangling_reqs, " +
							" num_of_orphan_reqs, num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
							" num_of_completed_reqs, num_of_incomplete_reqs" +
							" from gr_baseline_metrics where project_id = ? and rt_baseline_id= ?  ";
						
						
						PreparedStatement prepStmt2 = con.prepareStatement(sql);
						prepStmt2.setInt(1, sourceProjectId);
						prepStmt2.setInt(2, sourceRTBaselineId);
						
						prepStmt2.execute();
						prepStmt2.close();
					
				}
				rs.close();
				prepStmt.close();
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
}
