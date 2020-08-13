package com.gloree.utils;

//GloreeJava2


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.gloree.beans.*;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;

public class FolderUtil {
	
	//
	// This class is used to run static queries to get 
	// 1. list of subfolders in a project
	// 2. list of requirements in a project. 
	// 3. Delete a folder in the db.
	//
	public static ArrayList getSubFolders(int folderId){
		ArrayList subFolders = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			//
			// This sql gets the list of subfolders for a given folder id . It then creates a folder object and stores them in an Arraylist
			// This array list is then returned to the caller.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "select f.id, f.project_id, f.name, f.description, f.parent_folder_id," +
				" f.folder_level, f.folder_order, f.folder_path, rt.name \"requirement_type_name\", " +
				"rt.id \"requirement_type_id\", f.created_by, f.created_dt, f.last_modified_by, f.last_modified_dt " +
				" from gr_folders f, gr_requirement_types rt " +
				" where f.requirement_type_id = rt.id " +
				" and f.parent_folder_id = ? ";
			
			
			 prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId);
			 rs = prepStmt.executeQuery();
			
			
			int projectId = 0;
			String folderName = "";
			String folderDescription = "";
			int parentFolderId = 0;
			int folderLevel = 0;
			int folderOrder = 0;			
			String folderPath = "" ;
			int requirementTypeId = 0;
			String requirementTypeName = "";
			String createdBy = "";
			// Date createdDt;
			String lastModifiedBy = "";
			// Date lastModifiedDt;
			
			while (rs.next()){
				folderId = rs.getInt("id");
				projectId = rs.getInt("project_id");
				folderName = rs.getString("name");
				folderDescription = rs.getString ("description");
				parentFolderId = rs.getInt("parent_folder_id");
				folderLevel = rs.getInt("folder_level");
				folderOrder = rs.getInt("folder_order");
				folderPath = rs.getString("folder_path");
				requirementTypeId = rs.getInt("requirement_type_id");
				requirementTypeName = rs.getString("requirement_type_name");
				createdBy = rs.getString("created_by");
				//createdDt = rs.getDate("created_dt");
				lastModifiedBy = rs.getString("last_modified_by") ;
				//lastModifiedDt = rs.getDate("last_modified_by");
				
				// creating the folder bean.
				Folder folder = new Folder(folderId, projectId, folderName, folderDescription,
						parentFolderId, folderLevel, folderOrder, folderPath, requirementTypeId, 
						requirementTypeName, createdBy, lastModifiedBy);
				subFolders.add(folder);
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
		return (subFolders);
	}	

	public static boolean doesThisFolderExistInProject(int projectId, String folderPath){
		
		boolean folderExists = false;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = "select count(*) 'folder_count' " +
					" from gr_folders " +
					" where project_id =  ? " +
					" and folder_path = ? ";
			
			
			 prepStmt = con.prepareStatement(sql);
			 prepStmt.setInt(1, projectId);
			 prepStmt.setString(2, folderPath);
			 rs = prepStmt.executeQuery();
			
			
			int folderCount = 0;
			
			
			while (rs.next()){
				folderCount = rs.getInt("folder_count");
				if (folderCount > 0 ){
					folderExists = true;
				}
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
		return (folderExists);
	}	
	
	public static void deleteFolder (int projectId, int folderId, String actorEmailId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// get folderName to be deleted.
			String sql = "select name from gr_folders where id = ? ";
			 prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId);
			 rs = prepStmt.executeQuery();
			String folderName = "";
			while (rs.next()){
				folderName = rs.getString("name");
			}
			
			ProjectUtil.createProjectLog(projectId, folderName, "Delete", 
				"Delete a folder", actorEmailId,  databaseType);
			
			// prior to deleting the folder, we need to delete all the canned reports.
			sql = "delete from gr_reports " +
				"where folder_id = ? ";
		
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId);
			prepStmt.execute();

			// prior to deleting the folder, we need to delete all the role privs for this folder.
			sql = "delete from gr_role_privs  " +
				"where folder_id = ? ";
		
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId);
			prepStmt.execute();
	
			
			//
			// This sql deletes the folder. We assume that the calling routine has validated to make sure that this is not a root level folder.
			//
			sql = "delete from gr_folders " +
				"where id = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId);
			prepStmt.execute();
		
			rs.close();
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
	
	// this method creates a bunch of canned reports for a newly created folder.
	public static void createCannedReports(int projectId, int folderId, String requirementTypeName, 
			String createdByEmailId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
				
			String report_sql = " " ;

			//
			// Lets created a canned ALL Requirements Attribute list report. 
			//
			// this is the definition for All Requirements report.
			String reportDefinition = "projectId:--:" + projectId + ":###:folderId:--:" + folderId + ":###:";
			reportDefinition += "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
					"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
					"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:" +
					"includeSubFoldersSearch:--:no:###:nameSearch:--::###:descriptionSearch:--::###:" +
					"ownerSearch:--::###:externalURLSearch:--::###:traceToSearch:--::###:" +
					"traceFromSearch:--::###:statusSearch:--::###:prioritySearch:--::###:" +
					"pctCompleteSearch:--:";

			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
						" report_definition, " +
						" report_type, trace_tree_depth, report_sql  , created_by, created_dt, last_modified_by," +
						" last_modified_dt) values (?,?,?,?,?,?,?,?,?, now(), ?, now())";
			}
			else {
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
				" report_definition, " +
				" report_type, trace_tree_depth, report_sql  , created_by, created_dt, last_modified_by," +
				" last_modified_dt) values (?,?,?,?,?,?,?,?,?, sysdate, ?, sysdate)";
			}
			 prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
			prepStmt.setString(3,"All Requirements");
			prepStmt.setString(4,"Canned report created by the system to display " +
					" ALL the Requirements in this folder.");
			prepStmt.setString(5, reportDefinition);
			prepStmt.setString(6, "list");
			prepStmt.setInt(7, 1);
			prepStmt.setString(8, report_sql);
			prepStmt.setString(9, createdByEmailId);
			prepStmt.setString(10, createdByEmailId);
			prepStmt.execute();
		
			ProjectUtil.createProjectLog(projectId, requirementTypeName + " -- All Requirements", 
					"Create", "Created a canned report", createdByEmailId,  databaseType);

			//
			// Lets created a canned Dangling Requirements Attribute list report. 
			//
			
			report_sql = " " ;

			//
			// Lets created a canned Dangling Requirements Attribute list report. 
			//
			// this is the definition for Dangling Requirements report.
			reportDefinition = "projectId:--:" + projectId + ":###:folderId:--:" + folderId + ":###:";
			reportDefinition += "active:--:active:###:danglingSearch:--:danglingOnly:###:" +
					"orphanSearch:--:all:###:completedSearch:--:all:###:incompleteSearch:--:all:###:" +
					"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:" +
					"includeSubFoldersSearch:--:no:###:nameSearch:--::###:descriptionSearch:--::###:" +
					"ownerSearch:--::###:externalURLSearch:--::###:traceToSearch:--::###:" +
					"traceFromSearch:--::###:statusSearch:--::###:prioritySearch:--::###:" +
					"pctCompleteSearch:--:";

			if (databaseType.equals("mySQL")){
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
					" report_definition, " +
					" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
					" last_modified_by," +
					" last_modified_dt) values (?,?,?,?,?,?,?,?,?, now(), ?, now())";
			}
			else {
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
				" report_definition, " +
				" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
				" last_modified_by," +
				" last_modified_dt) values (?,?,?,?,?,?,?,?,?, sysdate, ?, sysdate)";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
			prepStmt.setString(3,"Dangling Requirements");
			prepStmt.setString(4,"Canned report created by the system to display " +
					" the DANGLING Requirements in this folder. i.e. Requirements " +
					" in this folder that do have a trace coming to them from " +
					" other requirements.");
			prepStmt.setString(5, reportDefinition);
			prepStmt.setString(6, "list");
			prepStmt.setInt(7, 1);
			prepStmt.setString(8, report_sql);
			prepStmt.setString(9, createdByEmailId);
			prepStmt.setString(10, createdByEmailId);
			prepStmt.execute();
			
			ProjectUtil.createProjectLog(projectId, requirementTypeName + " -- Dangling Requirements", 
					"Create", "Created a canned report", createdByEmailId,  databaseType);
			
			//
			// Lets created a canned Orphan Requirements Attribute list report. 
			//
			
			report_sql = " " ;
			// this is the definition for All Requirements report.
			reportDefinition = "projectId:--:" + projectId + ":###:folderId:--:" + folderId + ":###:";
			reportDefinition += "active:--:active:###:danglingSearch:--:all:###:" +
					"orphanSearch:--:orphanOnly:###:completedSearch:--:all:###:" +
					"incompleteSearch:--:all:###:suspectUpStreamSearch:--:all:###:" +
					"suspectDownStreamSearch:--:all:###:includeSubFoldersSearch:--:no:###:" +
					"nameSearch:--::###:descriptionSearch:--::###:ownerSearch:--::###:" +
					"externalURLSearch:--::###:traceToSearch:--::###:traceFromSearch:--::###:" +
					"statusSearch:--::###:prioritySearch:--::###:pctCompleteSearch:--:";
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_reports (project_id, folder_id, name, description," +	
					" report_definition, " +
					" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
					" last_modified_by," +
					" last_modified_dt) values (?,?,?,?,?,?,?,?,?, now(), ?, now())";
			}
			else {
				sql = " insert into gr_reports (project_id, folder_id, name, description," +	
				" report_definition, " +
				" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
				" last_modified_by," +
				" last_modified_dt) values (?,?,?,?,?,?,?,?,?, sysdate, ?, sysdate)";				
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
			prepStmt.setString(3,"Orphan Requirements");
			prepStmt.setString(4,"Canned report created by the system to display " +
					" the Orphan Requirements in this folder. i.e. Requirements in this folder " +
					" that do not trace to any other requirements.");
			prepStmt.setString(5, reportDefinition);
			prepStmt.setString(6, "list");
			prepStmt.setInt(7, 1);
			prepStmt.setString(8, report_sql);
			prepStmt.setString(9, createdByEmailId);
			prepStmt.setString(10, createdByEmailId);
			prepStmt.execute();
			
			ProjectUtil.createProjectLog(projectId, requirementTypeName + " -- Orphan Requirements", 
					"Create", "Created a canned report", createdByEmailId,  databaseType);
			
						
			//
			// Lets created a canned Completed Requirements Attribute list report. 
			//
			
			report_sql = " " ;				
			reportDefinition = "projectId:--:" + projectId + ":###:folderId:--:" + folderId + ":###:";
			reportDefinition += "active:--:active:###:danglingSearch:--:all:###:" +
					"orphanSearch:--:all:###:completedSearch:--:completedOnly:###:" +
					"incompleteSearch:--:all:###:suspectUpStreamSearch:--:all:###:" +
					"suspectDownStreamSearch:--:all:###:includeSubFoldersSearch:--:no:###:" +
					"nameSearch:--::###:descriptionSearch:--::###:ownerSearch:--::###:" +
					"externalURLSearch:--::###:traceToSearch:--::###:traceFromSearch:--::###:" +
					"statusSearch:--::###:prioritySearch:--::###:pctCompleteSearch:--:";


			if (databaseType.equals("mySQL")){
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
					" report_definition, " +
					" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
					" last_modified_by," +
					" last_modified_dt) values (?,?,?,?,?,?,?,?,?, now(), ?, now())";
			}
			else {
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
				" report_definition, " +
				" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
				" last_modified_by," +
				" last_modified_dt) values (?,?,?,?,?,?,?,?,?, sysdate, ?, sysdate)";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
			prepStmt.setString(3,"Completed Requirements");
			prepStmt.setString(4,"Canned report created by the system to display " +
					" the Completed Requirements in this folder. i.e. Requirements in this folder " +
					" that have percent completed = 100%");
			prepStmt.setString(5, reportDefinition);
			prepStmt.setString(6, "list");
			prepStmt.setInt(7, 1);
			prepStmt.setString(8, report_sql);
			prepStmt.setString(9, createdByEmailId);
			prepStmt.setString(10, createdByEmailId);
			prepStmt.execute();
			
			ProjectUtil.createProjectLog(projectId, requirementTypeName + " -- Completed Requirements", 
					"Create", "Created a canned report", createdByEmailId,  databaseType);

			//
			// Lets created a canned Incomplete Requirements Attribute list report. 
			//
			
			report_sql = " " ;
			
			reportDefinition = "projectId:--:" + projectId + ":###:folderId:--:" + folderId + ":###:";
			reportDefinition += "active:--:active:###:danglingSearch:--:all:###:" +
					"orphanSearch:--:all:###:completedSearch:--:all:###:" +
					"incompleteSearch:--:incompleteOnly:###:suspectUpStreamSearch:--:all:###:" +
					"suspectDownStreamSearch:--:all:###:includeSubFoldersSearch:--:no:###:" +
					"nameSearch:--::###:descriptionSearch:--::###:ownerSearch:--::###:" +
					"externalURLSearch:--::###:traceToSearch:--::###:traceFromSearch:--::###:" +
					"statusSearch:--::###:prioritySearch:--::###:pctCompleteSearch:--:";

			if (databaseType.equals("mySQL")){
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
					" report_definition, " +
					" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
					" last_modified_by," +
					" last_modified_dt) values (?,?,?,?,?,?,?,?,?, now(), ?, now())";
			}
			else {
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
				" report_definition, " +
				" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
				" last_modified_by," +
				" last_modified_dt) values (?,?,?,?,?,?,?,?,?, sysdate, ?, sysdate)";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
			prepStmt.setString(3,"Incomplete Requirements");
			prepStmt.setString(4,"Canned report created by the system to display " +
					" the InComplete Requirements in this folder. i.e. Requirements in this folder " +
					" that have percent completed < 100%");
			prepStmt.setString(5, reportDefinition);
			prepStmt.setString(6, "list");
			prepStmt.setInt(7, 1);
			prepStmt.setString(8, report_sql);
			prepStmt.setString(9, createdByEmailId);
			prepStmt.setString(10, createdByEmailId);
			prepStmt.execute();
		
			ProjectUtil.createProjectLog(projectId, requirementTypeName + " -- Incomplete Requirements", 
					"Create", "Created a canned report", createdByEmailId,  databaseType);
			
			//
			// Lets created a canned Suspect Upstream Requirements Attribute list report. 
			//
			
			report_sql = " " ;
			reportDefinition = "projectId:--:" + projectId + ":###:folderId:--:" + folderId + ":###:";
			reportDefinition += "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
					"completedSearch:--:all:###:incompleteSearch:--:all:###:suspectUpStreamSearch:--:" +
					"suspectUpStreamOnly:###:suspectDownStreamSearch:--:all:###:" +
					"includeSubFoldersSearch:--:no:###:nameSearch:--::###:" +
					"descriptionSearch:--::###:ownerSearch:--::###:externalURLSearch:--::###:" +
					"traceToSearch:--::###:traceFromSearch:--::###:statusSearch:--::###:" +
					"prioritySearch:--::###:pctCompleteSearch:--:";

			if (databaseType.equals("mySQL")){
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
					" report_definition, " +
					" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
					" last_modified_by," +
					" last_modified_dt) values (?,?,?,?,?,?,?,?,?, now(), ?, now())";
			}
			else {
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
				" report_definition, " +
				" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
				" last_modified_by," +
				" last_modified_dt) values (?,?,?,?,?,?,?,?,?, sysdate, ?, sysdate)";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
			prepStmt.setString(3,"Suspect Upstream Requirements");
			prepStmt.setString(4,"Canned report created by the system to display " +
					" the Requirements in this folder that has SUSPECT TRACES TO other Requirements.");
			prepStmt.setString(5, reportDefinition);
			prepStmt.setString(6, "list");
			prepStmt.setInt(7, 1);
			prepStmt.setString(8, report_sql);
			prepStmt.setString(9, createdByEmailId);
			prepStmt.setString(10, createdByEmailId);
			prepStmt.execute();
			
			ProjectUtil.createProjectLog(projectId, requirementTypeName + " -- Suspect Upstream Requirements", 
					"Create", "Created a canned report", createdByEmailId,  databaseType);
			
			//
			// Lets created a canned Suspect Downstream Requirements Attribute list report. 
			//
			
			report_sql = " " ;
			reportDefinition = "projectId:--:" + projectId + ":###:folderId:--:" + folderId + ":###:";
			reportDefinition += "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
					"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
					"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:" +
					"suspectDownStreamOnly:###:includeSubFoldersSearch:--:no:###:" +
					"nameSearch:--::###:descriptionSearch:--::###:ownerSearch:--::###:" +
					"externalURLSearch:--::###:traceToSearch:--::###:traceFromSearch:--::###:" +
					"statusSearch:--::###:prioritySearch:--::###:pctCompleteSearch:--:";

			if (databaseType.equals("mySQL")){
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
					" report_definition, " +
					" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
					" last_modified_by," +
					" last_modified_dt) values (?,?,?,?,?,?,?,?,?, now(), ?, now())";
			}
			else{
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
				" report_definition, " +
				" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
				" last_modified_by," +
				" last_modified_dt) values (?,?,?,?,?,?,?,?,?, sysdate, ?, sysdate)";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
			prepStmt.setString(3,"Suspect Downstream Requirements");
			prepStmt.setString(4,"Canned report created by the system to display " +
				" the Requirements in this folder that has SUSPECT TRACES FROM other Requirements.");
			prepStmt.setString(5, reportDefinition);
			prepStmt.setString(6, "list");
			prepStmt.setInt(7, 1);
			prepStmt.setString(8, report_sql);
			prepStmt.setString(9, createdByEmailId);
			prepStmt.setString(10, createdByEmailId);
			prepStmt.execute();
			
			ProjectUtil.createProjectLog(projectId, requirementTypeName + " -- Suspect Downstream Requirements", 
					"Create", "Created a canned report", createdByEmailId,  databaseType);
			
			
			
			//
			// Lets created a canned 'Pending Testing' Requirements Attribute list report. 
			//
			
			report_sql = " " ;
			reportDefinition = "projectId:--:" + projectId + ":###:folderId:--:" + folderId + ":###:";
			reportDefinition += "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
					"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
					"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"+
					"includeSubFoldersSearch:--:no:###:" +
					"nameSearch:--::###:descriptionSearch:--::###:ownerSearch:--::###:" +
					"externalURLSearch:--::###:traceToSearch:--::###:traceFromSearch:--::###:" +
					"statusSearch:--::###:prioritySearch:--::###:pctCompleteSearch:--::###:"+
					"testingStatusSearch:--:Pending";

			if (databaseType.equals("mySQL")){
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
					" report_definition, " +
					" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
					" last_modified_by," +
					" last_modified_dt) values (?,?,?,?,?,?,?,?,?, now(), ?, now())";
			}
			else {
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
				" report_definition, " +
				" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
				" last_modified_by," +
				" last_modified_dt) values (?,?,?,?,?,?,?,?,?, sysdate, ?, sysdate)";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
			prepStmt.setString(3,"Pending Testing");
			prepStmt.setString(4,"Canned report created by the system to display " +
				" the Requirements in this folder that are Pending Testing.");
			prepStmt.setString(5, reportDefinition);
			prepStmt.setString(6, "list");
			prepStmt.setInt(7, 1);
			prepStmt.setString(8, report_sql);
			prepStmt.setString(9, createdByEmailId);
			prepStmt.setString(10, createdByEmailId);
			prepStmt.execute();
			
			ProjectUtil.createProjectLog(projectId, requirementTypeName + " -- Pending Testing Requirements", 
					"Create", "Created a canned report", createdByEmailId,  databaseType);
			
			
			
			
			//
			// Lets created a canned 'Passed Testing' Requirements Attribute list report. 
			//
			
			report_sql = " " ;
			reportDefinition = "projectId:--:" + projectId + ":###:folderId:--:" + folderId + ":###:";
			reportDefinition += "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
					"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
					"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"+
					"includeSubFoldersSearch:--:no:###:" +
					"nameSearch:--::###:descriptionSearch:--::###:ownerSearch:--::###:" +
					"externalURLSearch:--::###:traceToSearch:--::###:traceFromSearch:--::###:" +
					"statusSearch:--::###:prioritySearch:--::###:pctCompleteSearch:--::###:"+
					"testingStatusSearch:--:Pass";
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
					" report_definition, " +
					" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
					" last_modified_by," +
					" last_modified_dt) values (?,?,?,?,?,?,?,?,?, now(), ?, now())";
			}
			else {
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
				" report_definition, " +
				" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
				" last_modified_by," +
				" last_modified_dt) values (?,?,?,?,?,?,?,?,?, sysdate, ?, sysdate)";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
			prepStmt.setString(3,"Passed Testing");
			prepStmt.setString(4,"Canned report created by the system to display " +
				" the Requirements in this folder that have Passed Testing.");
			prepStmt.setString(5, reportDefinition);
			prepStmt.setString(6, "list");
			prepStmt.setInt(7, 1);
			prepStmt.setString(8, report_sql);
			prepStmt.setString(9, createdByEmailId);
			prepStmt.setString(10, createdByEmailId);
			prepStmt.execute();
			
			ProjectUtil.createProjectLog(projectId, requirementTypeName + " -- Passed Testing Requirements", 
					"Create", "Created a canned report", createdByEmailId,  databaseType);
						
			
			
			//
			// Lets created a canned 'Failed Testing' Requirements Attribute list report. 
			//
			
			report_sql = " " ;
			reportDefinition = "projectId:--:" + projectId + ":###:folderId:--:" + folderId + ":###:";
			reportDefinition += "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
					"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
					"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"+
					"includeSubFoldersSearch:--:no:###:" +
					"nameSearch:--::###:descriptionSearch:--::###:ownerSearch:--::###:" +
					"externalURLSearch:--::###:traceToSearch:--::###:traceFromSearch:--::###:" +
					"statusSearch:--::###:prioritySearch:--::###:pctCompleteSearch:--::###:"+
					"testingStatusSearch:--:Fail";
			
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
					" report_definition, " +
					" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
					" last_modified_by," +
					" last_modified_dt) values (?,?,?,?,?,?,?,?,?, now(), ?, now())";
			}
			else {
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
				" report_definition, " +
				" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
				" last_modified_by," +
				" last_modified_dt) values (?,?,?,?,?,?,?,?,?, sysdate, ?, sysdate)";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
			prepStmt.setString(3,"Failed Testing");
			prepStmt.setString(4,"Canned report created by the system to display " +
				" the Requirements in this folder that have Failed Testing.");
			prepStmt.setString(5, reportDefinition);
			prepStmt.setString(6, "list");
			prepStmt.setInt(7, 1);
			prepStmt.setString(8, report_sql);
			prepStmt.setString(9, createdByEmailId);
			prepStmt.setString(10, createdByEmailId);
			prepStmt.execute();
			
			ProjectUtil.createProjectLog(projectId, requirementTypeName + " -- Failed Testing Requirements", 
					"Create", "Created a canned report", createdByEmailId,  databaseType);
						
			//
			// Lets created a canned TraceTree  report. 
			//
			
			// this is the definition for All Requirements Trace Tree report.
			reportDefinition = "projectId:--:" + projectId + ":###:folderId:--:" + folderId + ":###:";
			reportDefinition += "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
					"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
					"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:" +
					"includeSubFoldersSearch:--:no:###:nameSearch:--::###:descriptionSearch:--::###:" +
					"ownerSearch:--::###:externalURLSearch:--::###:traceToSearch:--::###:" +
					"traceFromSearch:--::###:statusSearch:--::###:prioritySearch:--::###:" +
					"pctCompleteSearch:--:";
			
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
					" report_definition, " +
					" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
					" last_modified_by," +
					" last_modified_dt) values (?,?,?,?,?,?,?,?,?, now(), ?, now())";
			}
			else {
				sql = " insert into gr_reports (project_id, folder_id, name, description," +
				" report_definition, " +
				" report_type, trace_tree_depth, report_sql  , created_by, created_dt," +
				" last_modified_by," +
				" last_modified_dt) values (?,?,?,?,?,?,?,?,?, sysdate, ?, sysdate)";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
			prepStmt.setString(3,"Trace Tree");
			prepStmt.setString(4,"Canned report created by the system to display " +
					" ALL the Requirements in this folder.");
			prepStmt.setString(5, reportDefinition);
			prepStmt.setString(6, "traceTree");
			prepStmt.setInt(7, 3);
			prepStmt.setString(8, report_sql);
			prepStmt.setString(9, createdByEmailId);
			prepStmt.setString(10, createdByEmailId);
			prepStmt.execute();

			ProjectUtil.createProjectLog(projectId, requirementTypeName + " Requirements Trace Tree", 
					"Create", "Created a canned report", createdByEmailId,  databaseType);

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
	
	
	
	// takes a folderId as a param and returns an arraylist of Requirements
	public static ArrayList getRequirementsInFolder(int projectId, int folderId, int deletedFlag, String databaseType) {
		
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of requirements in this folder. Create a Requirement object for every requirement row and pump them into the array list
			// called myRequirements and add them to this Folder bean.
			
			String sql = "";
			if (databaseType.equals("mySQL")){
			 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\" ," +
					" r.approvers, " + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from , " +
					" r.user_defined_attributes, r.testing_status, r.deleted, f.folder_path, r.created_by," +
					" date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\" , " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM gr_requirements r , gr_requirement_types rt, gr_folders f " +
					" where r.requirement_type_id = rt.id and r.deleted = " + deletedFlag + " and r.folder_id = ? " +
					" and r.project_id = ? " +
					" and r.folder_id = f.id " +
					" order by tag_level1, tag_level2,tag_level3,  r.tag " ;
			}
			else {
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
				" r.name, substr(to_char(r.description),1,4000) \"description\", r.tag, r.full_tag, " +
				" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\" ," +
				" r.approvers, " + 
				" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
				" substr(to_char(r.trace_to),1,4000) \"trace_to\"," +
				" substr(to_char(r.trace_from),1,4000) \"trace_from\" , " +
				" substr(to_char(r.user_defined_attributes),1,4000) \"user_defined_attributes\"," +
				" r.testing_status, r.deleted, f.folder_path, r.created_by," +
				" to_char(r.created_dt, 'DD MON YYYY') \"created_dt\" , " +
				" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
				" FROM gr_requirements r , gr_requirement_types rt, gr_folders f " +
				" where r.requirement_type_id = rt.id and r.deleted = " + deletedFlag + " and r.folder_id = ? " +
				" and r.project_id = ? " +
				" and r.folder_id = f.id " +
				" order by tag_level1, tag_level2,tag_level3,  r.tag " ;
		
			}
			

			
			 prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId);
			prepStmt.setInt(2, projectId);
			 rs = prepStmt.executeQuery();
			 
			while (rs.next()){
				//we use the folderId we got as a parameter to this constructor.
				int requirementId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				//int projectId = rs.getInt("project_id");
				String parentFullTag = rs.getString("parent_full_tag");
				
				String requirementName = rs.getString("name");
				String requirementDescription = rs.getString ("description");
				String requirementTag = rs.getString("tag");
				String requirementFullTag = rs.getString("full_tag");
				int version = rs.getInt("version");
				String approvedByAllDt = rs.getString("approved_by_all_dt");
				String approvers = rs.getString("approvers");
				String requirementStatus = rs.getString("status");
				String requirementPriority = rs.getString("priority");
				String requirementOwner = rs.getString("owner");
				String requirementLockedBy = rs.getString("locked_by");
				int requirementPctComplete = rs.getInt("pct_complete");
				String requirementExternalUrl = rs.getString("external_url");
				String traceTo = rs.getString("trace_to");
				String traceFrom = rs.getString("trace_from");
				String userDefinedAttributes = rs.getString("user_defined_attributes");
				String testingStatus = rs.getString("testing_status");
				int deleted = rs.getInt("deleted");
				String folderPath = rs.getString("folder_path");
				String createdBy = rs.getString("created_by");
				String createdDt = rs.getString("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by") ;
				
				//	Date lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId, 
						projectId, requirementName, requirementDescription,	 requirementTag, 
						requirementFullTag, version, approvedByAllDt, approvers,  
						requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
						requirementPctComplete, requirementExternalUrl ,traceTo, traceFrom, 
						userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy,
						requirementTypeName, createdDt);
				requirements.add(requirement);
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
		return requirements;
	}

	
	public static ArrayList<Requirement> getRequirementsInFolderSorted
		(int projectId, int folderId, int deletedFlag, String databaseType, String sortBy,
		HashMap<String, String> folderFilters) {
		
		ArrayList<Requirement> requirements = new ArrayList<Requirement>();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of requirements in this folder. Create a Requirement object for every requirement row and pump them into the array list
			// called myRequirements and add them to this Folder bean.
			
			String sql =  " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\" ," +
					" r.approvers, " + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from , " +
					" r.user_defined_attributes, r.testing_status, r.deleted, f.folder_path, r.created_by," +
					" date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\" , " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\"  " +
					" FROM gr_requirements r , gr_requirement_types rt, gr_folders f " +
					" where r.requirement_type_id = rt.id and r.deleted = " + deletedFlag + " and r.folder_id = ? " +
					" and r.project_id = ? " +
					" and r.folder_id = f.id " ;
			
			
			if ((sortBy == null) || (sortBy.equals(""))){
				sql += "  order by   tag_level1, tag_level2,tag_level3, tag_level4,  r.tag ";	
					
			}
			else {
				if (sortBy.equals("name")){
					sql += " order by r.name ";
				}
				if (sortBy.equals("approvalStatus")){
					sql += " order by r.status,tag_level1, tag_level2,tag_level3,  r.tag  ";
				}
				if (sortBy.startsWith("CustomAttribute")){
					String customAttribute = sortBy.replace("CustomAttribute", "");
					
					sql  += "order by ltrim(substr(user_defined_attributes,locate('"+ 
							customAttribute + "', user_defined_attributes)+ "+ customAttribute.length() + ", 6 ))  "; 
					
				}
				
			}

			
			 prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId);
			prepStmt.setInt(2, projectId);
			 rs = prepStmt.executeQuery();
			 
			while (rs.next()){
				//we use the folderId we got as a parameter to this constructor.
				int requirementId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				//int projectId = rs.getInt("project_id");
				String parentFullTag = rs.getString("parent_full_tag");
				
				String requirementName = rs.getString("name");
				String requirementDescription = rs.getString ("description");
				String requirementTag = rs.getString("tag");
				String requirementFullTag = rs.getString("full_tag");
				int version = rs.getInt("version");
				String approvedByAllDt = rs.getString("approved_by_all_dt");
				String approvers = rs.getString("approvers");
				String requirementStatus = rs.getString("status");
				String requirementPriority = rs.getString("priority");
				String requirementOwner = rs.getString("owner");
				String requirementLockedBy = rs.getString("locked_by");
				int requirementPctComplete = rs.getInt("pct_complete");
				String requirementExternalUrl = rs.getString("external_url");
				String traceTo = rs.getString("trace_to");
				String traceFrom = rs.getString("trace_from");
				String userDefinedAttributes = rs.getString("user_defined_attributes");
				String testingStatus = rs.getString("testing_status");
				int deleted = rs.getInt("deleted");
				String folderPath = rs.getString("folder_path");
				String createdBy = rs.getString("created_by");
				String createdDt = rs.getString("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by") ;
				
				//	Date lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				Requirement r = new Requirement(requirementId, requirementTypeId, folderId, 
						projectId, requirementName, requirementDescription,	 requirementTag, 
						requirementFullTag, version, approvedByAllDt, approvers,  
						requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
						requirementPctComplete, requirementExternalUrl ,traceTo, traceFrom, 
						userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy,
						requirementTypeName, createdDt);
				
				
				
	    		// lets filter through each of the folder filters and figure out if the req should be excluded. 
				boolean includeThisReq = true;
				

	    		String attributeString = r.getUserDefinedAttributes() + ":##:";
				for (Map.Entry<String, String> entry : folderFilters.entrySet()) {
				    String filterValue = entry.getKey();
				    Object filterType = entry.getValue();
		    		if ( (filterType != null) && (filterType.equals("showMatching"))){
		    			filterValue = filterValue + ":##:";
		    			if (!(attributeString.contains(filterValue))){
		    				// filterType is show Matching and the Req's attribute value is NOT matching
		    				// so we can not show this.
		    				includeThisReq = false;
		    			}
		    		}				    
				    
		    		if ( (filterType != null) && (filterType.equals("filterOut"))){
		    			filterValue = filterValue + ":##:";
		    			if (attributeString.contains(filterValue)){
		    				// filterType is filterOut and the Req's attribute value is  matching
		    				// so we can NOT show this.
		    				includeThisReq = false;
		    			}

		    		}				    
				}
				

	    		

	    		
	    		
	    		if (includeThisReq){
	    			requirements.add(r);
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
		
		if (sortBy.startsWith("CustomAttribute")){
			String customAttribute = sortBy.replace("CustomAttribute", "");
			// if the custom attribute is of type number, then we have to sort the arraylist manually
			Folder folder = new Folder(folderId);
			RTAttribute attribute = new RTAttribute(folder.getRequirementTypeId(),customAttribute);
			if (attribute.getAttributeType().equals("Number")){
				// this is an attribute of type number, and our alphanumeric sorting doesn't work.
				// So we will have to manually sort. 
				requirements = sortRequirementsInArrayNumerically(requirements,attribute, "ascending");
			}
		
			
		}
		return requirements;
	}

	
public static ArrayList<Requirement> sortRequirementsInArrayNumerically(ArrayList<Requirement>requirements, RTAttribute attribute, String sortByType) {
		
		System.out.println("srt in sort function");
		ArrayList<Requirement> sortedRequirements = new ArrayList<Requirement>();
		
		try {
			if (sortByType.equals("ascending")){
				int l = requirements.size();
				for (int i=0; i<l ; i++){
					// loop through the requirements.
					// find the smallest element , based on atttributevalue
					// put that to sortedRequirements and remove from original list
					double currentSmallest = 99999999.0;
					Requirement smallestReq = null;
					for (Requirement r : requirements){
						double aValueDouble = 0.0;
						try{
							String aValue = r.getAttributeValueFromUDA(attribute.getAttributeName() );
							aValueDouble = Double.parseDouble(aValue);
						}
						catch(Exception e1){
							// do nothing
						}
						
						System.out.println("srt avalue is  " + aValueDouble);
						System.out.println("srt currentSmallest is  " + currentSmallest);
						
						if (aValueDouble < currentSmallest){
							currentSmallest = aValueDouble;
							smallestReq = r;
						}
					}
					
					
					// when we are done looping, currentSmallest has the smallestValue and smallestReq is the req with that value
					if (smallestReq != null){
						System.out.println("srt picked smallest req " + smallestReq.getRequirementFullTag());

						sortedRequirements.add(smallestReq);
						requirements.remove(smallestReq);
					}
				}
				// if there are still any objects left in requirements, lets add them to sorted enmass
				for (Requirement r : requirements){
					sortedRequirements.add(r);
				}
			}
			else{
				int l = requirements.size();
				for (int i=0; i<l ; i++){
					// loop through the requirements.
					// find the smallest element , based on atttributevalue
					// put that to sortedRequirements and remove from original list
					double currentLargest = 0.0;
					Requirement largestReq = null;
					for (Requirement r : requirements){
						System.out.println("=======================>srt req  is  " + r.getRequirementFullTag());
						
						double aValueDouble = 0.0;
						try{
							String aValue = r.getAttributeValueFromUDA(attribute.getAttributeName() );
							aValueDouble = Double.parseDouble(aValue);
						}
						catch(Exception e1){
							// do nothing
						}
						
						System.out.println("srt avalue is  " + aValueDouble  + " and currentLargest is  " + currentLargest);
						
						if (aValueDouble > currentLargest){
							System.out.println("srt since aValue is larger than currentLargest, switching.  " );
							currentLargest = aValueDouble;
							largestReq = r;
							System.out.println("srt after switch avalue is  " + aValueDouble  + " and currentLargest is  " + currentLargest);
						}
					}
					
					
					// when we are done looping, currentSmallest has the smallestValue and smallestReq is the req with that value
					if (largestReq != null){
						System.out.println("srt adding largest  req " + largestReq.getRequirementFullTag());

						sortedRequirements.add(largestReq);
						requirements.remove(largestReq);
					}
					
				}
				// if there are still any objects left in requirements, lets add them to sorted enmass
				for (Requirement r : requirements){
					sortedRequirements.add(r);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}   finally {
			
		}
		return sortedRequirements;
	}
	
	// takes a folderId as a param and returns an arraylist of Requirements
	public static ArrayList getRequirementsInFolderPath(int projectId, String folderPath, int deletedFlag, String databaseType) {
		
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of requirements in this folder. Create a Requirement object for every requirement row and pump them into the array list
			// called myRequirements and add them to this Folder bean.
			
			String sql = "";
			if (databaseType.equals("mySQL")){
			 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\" ," +
					" r.approvers, " + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from , " +
					" r.user_defined_attributes, r.testing_status, r.deleted, f.folder_path, r.created_by," +
					" date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\" , " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM gr_requirements r , gr_requirement_types rt, gr_folders f " +
					" where r.requirement_type_id = rt.id and r.deleted = " + deletedFlag + 
					" and f.folder_path like   '%" + folderPath + "%' " +
					" and r.project_id = ? " +
					" and r.folder_id = f.id " +
					" order by f.folder_path, tag_level1, tag_level2,tag_level3, tag_level4, r.tag " ;
			}
			else {
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
				" r.name, substr(to_char(r.description),1,4000) \"description\", r.tag, r.full_tag, " +
				" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\" ," +
				" r.approvers, " + 
				" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
				" substr(to_char(r.trace_to),1,4000) \"trace_to\"," +
				" substr(to_char(r.trace_from),1,4000) \"trace_from\" , " +
				" substr(to_char(r.user_defined_attributes),1,4000) \"user_defined_attributes\"," +
				" r.testing_status, r.deleted, f.folder_path, r.created_by," +
				" to_char(r.created_dt, 'DD MON YYYY') \"created_dt\" , " +
				" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
				" FROM gr_requirements r , gr_requirement_types rt, gr_folders f " +
				" where r.requirement_type_id = rt.id and r.deleted = " + deletedFlag + 
				" and f.folder_path like   '%" + folderPath + "%' " +
				" and r.project_id = ? " +
				" and r.folder_id = f.id " +
				" order by f.folder_path, tag_level1, tag_level2,tag_level3, tag_level4, r.tag " ;
		
			}
			 prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			 rs = prepStmt.executeQuery();
			while (rs.next()){
				//we use the folderId we got as a parameter to this constructor.
				int requirementId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				int folderId = rs.getInt("folder_id");
				//int projectId = rs.getInt("project_id");
				String parentFullTag = rs.getString("parent_full_tag");
				
				String requirementName = rs.getString("name");
				String requirementDescription = rs.getString ("description");
				String requirementTag = rs.getString("tag");
				String requirementFullTag = rs.getString("full_tag");
				int version = rs.getInt("version");
				String approvedByAllDt = rs.getString("approved_by_all_dt");
				String approvers = rs.getString("approvers");
				String requirementStatus = rs.getString("status");
				String requirementPriority = rs.getString("priority");
				String requirementOwner = rs.getString("owner");
				String requirementLockedBy = rs.getString("locked_by");
				int requirementPctComplete = rs.getInt("pct_complete");
				String requirementExternalUrl = rs.getString("external_url");
				String traceTo = rs.getString("trace_to");
				String traceFrom = rs.getString("trace_from");
				String userDefinedAttributes = rs.getString("user_defined_attributes");
				String testingStatus = rs.getString("testing_status");
				int deleted = rs.getInt("deleted");
				folderPath = rs.getString("folder_path");
				String createdBy = rs.getString("created_by");
				String createdDt = rs.getString("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by") ;
				
				//	Date lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId, 
						projectId, requirementName, requirementDescription,	 requirementTag, 
						requirementFullTag, version, approvedByAllDt, approvers,  
						requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
						requirementPctComplete, requirementExternalUrl ,traceTo, traceFrom, 
						userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy,
						requirementTypeName, createdDt);
				requirements.add(requirement);
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
		return requirements;
	}
	
	// takes a folderId as a param and returns an arraylist of Reports
	public static ArrayList getReportsInFolder(int projectId, int folderId) {
		
		ArrayList reports = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of Reports in this folder. Create a Report object for every 
			// report row and pump them into the array list
			// called myReports and add them to this Folder bean.
			
			String sql = " select r.id, r.project_id, r.folder_id, r.name, r.description," +
				" r.report_definition, " +
				" r.report_type, r.visibility, r.trace_tree_depth, " + 
				" r.report_sql, r.created_by " + 
				" from gr_reports r " + 
				" where r.folder_id = ? " +
				" and r.project_id = ? " +
				" order by r.report_type, r.name ";
			
			 prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId);
			prepStmt.setInt(2, projectId);
			 rs = prepStmt.executeQuery();
			while (rs.next()){

				int reportId = rs.getInt("id");
				//int projectId = rs.getInt("project_id");
				//we use the folderId we got as a parameter to this constructor.
				// int folderId
				String reportName = rs.getString("name");
				String reportDescription = rs.getString ("description");
				String reportDefinition = rs.getString ("report_definition");
				String reportType = rs.getString("report_type");
				String reportVisibility = rs.getString("visibility");
				int traceTreeDepth = rs.getInt("trace_tree_depth");
				String reportSQL = rs.getString("report_sql");
				String createdByEmailId = rs.getString("created_by");
				//	Date lastModifiedDt = rs.getDate("last_modified_by");
				
				//	TODO : at some point see how we can make DATE fields works.
				
				Report report = new Report (reportId ,projectId,folderId, reportName,
					 	reportDescription, reportDefinition, reportType, reportVisibility, 
					 	traceTreeDepth, reportSQL, 
					 	createdByEmailId);
				reports.add(report);
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
		return reports;
	}

	
	// takes a folderId as a param and returns an arraylist of word templates.
	public static ArrayList getWordTemplatesInFolder(int projectId, int folderId, String databaseType) {
		
		ArrayList wordTemplates = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			if (databaseType.equals("mySQL")){
				 sql = "select wt.id, wt.tdcs_document_id, wt.project_id, wt.folder_id, wt.name, wt.visibility," +
					" wt.description, wt.file_path, wt.created_by," +
					" date_format(wt.created_dt, '%d %M %Y %r ') \"created_dt\" ," +
					" wt.last_modified_by," +
					" date_format(wt.last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
					" from gr_word_templates wt, gr_folders f" +
					" where wt.folder_id = f.id" +
					" and f.id = ? " +
					" and wt.project_id = ? ";
			}
			else {
				sql = "select wt.id, wt.tdcs_document_id, wt.project_id, wt.folder_id, wt.name, wt.visibility," +
				" wt.description, wt.file_path, wt.created_by," +
				" to_char(wt.created_dt, 'DD MON YYYY') \"created_dt\" ," +
				" wt.last_modified_by," +
				" to_char(wt.last_modified_dt, 'DD MON YYYY') \"last_modified_dt\" " +
				" from gr_word_templates wt, gr_folders f" +
				" where wt.folder_id = f.id" +
				" and f.id = ? " +
				" and wt.project_id = ? ";	
			}
			  prepStmt = con.prepareStatement(sql);
	
			 prepStmt.setInt(1,folderId);
			 prepStmt.setInt(2,projectId);
			  rs = prepStmt.executeQuery();
			
			while (rs.next()) {
				int templateId = rs.getInt("id");
				int tDCSDocumentId = rs.getInt("tdcs_document_id");
				//int projectId = rs.getInt("project_id");
				//int folderId = rs.getInt("folder_id");
				String templateName = rs.getString("name");
				String templateVisibility = rs.getString("visibility");
				String templateDescription = rs.getString("name");
				String templateFilePath = rs.getString("file_path");
				String createdBy = rs.getString("created_by");
				String createdDt = rs.getString("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by");
				String lastModifiedDt = rs.getString("last_modified_dt");
				
				WordTemplate wordTemplate = new WordTemplate(templateId, tDCSDocumentId, projectId,folderId,
					templateName, templateVisibility, templateDescription,templateFilePath,
					createdBy,createdDt,
					lastModifiedBy,lastModifiedDt);
				wordTemplates.add(wordTemplate);
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
		return wordTemplates;
	}
	

	
	

	// takes a folderId and privilegetype as a param and returns an arraylist of roles that have create 
	// permissions on this folder
	public static ArrayList<Role> getPrivilegedRolesForFolder(int projectId, int folderId, String privilegeType) {
		
		ArrayList<Role> privilegedRoles = new ArrayList<Role>();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of Roles that have create REq permissions on this folder. 
			// Create a Role object for every 
			// report row and pump them into the array list
			// called createRequirementRoles 
			
			String sql = " select r.id, r.project_id, r.name, r.description, r.approval_type, r.approval_rank "  +
				" from gr_role_privs g, gr_roles r " +
				" where g.folder_id = ?  " + 
				" and g.role_id = r.id " +
				" and r.project_id = ? "; 
			if (privilegeType.equals("createRequirement")) {
				sql += " and g.create_requirement =1 " ;
			}
			if (privilegeType.equals("readRequirement")) {
				sql += " and g.read_requirement =1 " ;
			}
			if (privilegeType.equals("updateRequirement")) {
				sql += " and g.update_requirement =1 " ;
			}
			if (privilegeType.equals("deleteRequirement")) {
				sql += " and g.delete_requirement =1 " ;
			}
			if (privilegeType.equals("traceRequirement")) {
				sql += " and g.trace_requirement =1 " ;
			}

			if (privilegeType.equals("approveRequirement")) {
				sql += " and g.approve_requirement =1 " ;
				
				sql += " order by r.approval_rank ";
			}

			
			 prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId);
			prepStmt.setInt(2, projectId);
			 rs = prepStmt.executeQuery();
			while (rs.next()){

				int roleId = rs.getInt("id");
				//int projectId = rs.getInt("project_id");
				//we use the folderId we got as a parameter to this constructor.
				// int folderId
				String roleName = rs.getString("name");
				String roleDescription = rs.getString ("description");

				String approvalType = rs.getString("approval_type");
				int approvalRank = rs.getInt("approval_rank");
				
				Role role = new Role(roleId ,projectId, roleName,roleDescription, approvalType, approvalRank);
				privilegedRoles.add(role);
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
		return privilegedRoles;
	}

	
	
		public static void setUserFolderPreferences(int userId, int folderId, Object folderFilters, Object showAttributes) {
			
			
			PreparedStatement prepStmt = null;
			ResultSet rs = null;
			java.sql.Connection con = null;
			try {
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();

				String sql = " INSERT INTO GR_USER_FOLDER_PREFERENCES (user_id , folder_id, folder_filters, show_attributes)  " + 
						" VALUES (?, ?, ?, ? )" +
						" ON DUPLICATE KEY UPDATE folder_filters = ?, show_attributes = ?  " ;
				

				
				prepStmt = con.prepareStatement(sql);
				
				prepStmt.setInt(1, userId);
				prepStmt.setInt(2,  folderId);
				prepStmt.setObject(3, folderFilters);
				prepStmt.setObject(4, showAttributes);

				prepStmt.setObject(5, folderFilters);
				prepStmt.setObject(6, showAttributes);
				
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
			return ;
		}

		
		public static HashMap<String, Object> getUserFolderPreferences(int userId, int folderId) {
			
			HashMap<String, Object> folderPreferences = new HashMap<String, Object>();
			
			PreparedStatement prepStmt = null;
			ResultSet rs = null;
			java.sql.Connection con = null;
			try {
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();

				String sql = " select folder_filters, show_attributes  " + 
						" from GR_USER_FOLDER_PREFERENCES " +
						" where user_id = ? and folder_id = ?   " ;
				

				
				prepStmt = con.prepareStatement(sql);
				
				prepStmt.setInt(1, userId);
				prepStmt.setInt(2,  folderId);
				
				
				rs =  prepStmt.executeQuery();
				
			
				while (rs.next()){
					System.out.println("srt found some filters  " );
					Blob folderFiltersBlob = (Blob) rs.getBlob("folder_filters");
					
					InputStream is = folderFiltersBlob.getBinaryStream();
					ObjectInputStream ois= new ObjectInputStream(is);
					Object fFObject = (Object)ois.readObject();
					HashMap<String,String> folderFiltersMap = (HashMap<String, String>) fFObject;
					
					
					Blob showAttributesBlob = (Blob) rs.getBlob("show_attributes");
					
					is = showAttributesBlob.getBinaryStream();
					ois= new ObjectInputStream(is);
					Object sAObject = (Object)ois.readObject();
					ArrayList<String> showAttributes = (ArrayList<String>) sAObject;
					
					
			         
			         
					folderPreferences.put("folderFilters", folderFiltersMap);
					folderPreferences.put("showAttributes", showAttributes);
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
			return folderPreferences ;
		}

	public static ArrayList<Role> getStackedApprovalRolesForFolder(int folderId) {
		
		ArrayList<Role> privilegedRoles = new ArrayList<Role>();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of Roles that have create REq permissions on this folder. 
			// Create a Role object for every 
			// report row and pump them into the array list
			// called createRequirementRoles 
			
			String sql = " select r.id, r.project_id, r.name, r.description, r.approval_type, r.approval_rank "  +
				" from gr_role_privs g, gr_roles r " +
				" where g.folder_id = ?  " + 
				" and g.role_id = r.id " +
				" and g.approve_requirement =1 " + 
				" order by r.approval_rank " ;
			

			
			 prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId);
			 rs = prepStmt.executeQuery();
			while (rs.next()){

				int roleId = rs.getInt("id");
				int projectId = rs.getInt("project_id");
				String roleName = rs.getString("name");
				String roleDescription = rs.getString ("description");
				String approvalType = rs.getString("approval_type");
				int approvalRank = rs.getInt("approval_rank");
				
				Role role = new Role(roleId ,projectId, roleName,roleDescription, approvalType, approvalRank);
				privilegedRoles.add(role);
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
		return privilegedRoles;
	}


	public static ArrayList<String> getDefaultDisplayAttributesForFolder(int folderId) {
		
		ArrayList<String> defaultDisplayAttributes = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of Roles that have create REq permissions on this folder. 
			// Create a Role object for every 
			// report row and pump them into the array list
			// called createRequirementRoles 
			
			String sql = " select rta.id, rta.name "  +
				" from gr_folders f , gr_rt_attributes rta " +
				" where f.id = ?  " + 
				" and f.requirement_type_id  = rta.requirement_type_id " +
				" and rta.default_display = 1 " + 
				" order by rta.sort_order "; 
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId);
			 rs = prepStmt.executeQuery();
			while (rs.next()){

				int id = rs.getInt("id");
				String name = rs.getString("name");
				defaultDisplayAttributes.add(id + ":##:" + name);
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
		return defaultDisplayAttributes;
	}
	
	 public static boolean isValidFolderPathForRequirementType(int projectId, String folderPath, int requirementTypeId){
			boolean isValid = false; 
			PreparedStatement prepStmt = null;
			ResultSet rs = null;
			java.sql.Connection con =  null;
			try {
				
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();
			
				
				
				// see if this req type exists in this project.
				String sql = "select count(*) \"matches\" " +
					" from gr_folders " +
					" where project_id = ? " +
					" and requirement_type_id = ? " +
					" and lower(folder_path) =  ? ";
				 prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.setInt(2, requirementTypeId);
				prepStmt.setString(3, folderPath.toLowerCase().trim());
				 rs = prepStmt.executeQuery();
				int matches = 0;
				if (rs.next()){
					matches = rs.getInt("matches");
					if (matches == 1 ){
						isValid = true;
					}
				}
				rs.close();
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
			return isValid;
		}
	 
	 // returns folderId , given a folder path.
	 public static int getFolderId(int projectId, String folderPath){
		int folderId = 0; 
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			
			// see if this req type exists in this project.
			String sql = "select id " +
				" from gr_folders " +
				" where project_id = ? " +
				" and lower(folder_path) =  ? ";
			 prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, folderPath.toLowerCase().trim());
			 rs = prepStmt.executeQuery();
			int matches = 0;
			if (rs.next()){
				folderId = rs.getInt("id");
			}
			rs.close();
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
		return folderId;
	}	 

	 
	 public static int getRequirementTypeId(int folderId){
		int requirementTypeId = 0; 
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			
			// see if this req type exists in this project.
			String sql = "select requirement_type_id " +
				" from gr_folders " +
				" where id = ? ";
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId);
			rs = prepStmt.executeQuery();
			if (rs.next()){
				requirementTypeId = rs.getInt("requirement_type_id");
			}
			rs.close();
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
		return requirementTypeId;
	}	 

	 
	// returns true if this parentFolderId already has a subfolder of this name.
	public static boolean subFolderExists(int parentFolderId, String folderName){
		boolean subFolderExists = true; 
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			// see if a subfolder exists under this parentFolder
			String sql = "select count(*) \"matches\" " +
				" from gr_folders " +
				" where parent_folder_id = ? " +
				" and name = ? ";
			 prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, parentFolderId);
			prepStmt.setString(2, folderName);
			 rs = prepStmt.executeQuery();
			int matches = 0;
			if (rs.next()){
				matches = rs.getInt("matches");
			}
			
			if (matches == 0 ) {
				subFolderExists = false;
			}
			else {
				subFolderExists = true;
			}
			rs.close();
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
		return subFolderExists;
	}	 

	
	public static boolean canBeReportedOrphan( int folderId) {

		boolean canBeReportedOrphan = true;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "	select can_be_orphan " +
					" from gr_requirement_types rt, gr_folders f " +
					" where f.requirement_type_id = rt.id  " +
					" and  f.id = ?";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId );
			ResultSet rs = prepStmt.executeQuery();
			int canBeOrphan = 0;
			while (rs.next()){
				canBeOrphan= rs.getInt("can_be_orphan");
			}
			prepStmt.close();
			
			con.close();
			
			if (canBeOrphan == 1) {
				canBeReportedOrphan = true;
			}
			else {
				canBeReportedOrphan = false;
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (canBeReportedOrphan);
	}

	public static String getFoldersThatCanBeReportedOrphan( int projectId) {


		String canBeReportedOrphanFolders = ",";
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "	select f.id " +
					" from gr_requirement_types rt, gr_folders f " +
					" where rt.project_id = ? and f.requirement_type_id = rt.id  " +
					" and can_be_orphan = 1 ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId );
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				canBeReportedOrphanFolders +=  rs.getInt("id") + ",";
			}
			prepStmt.close();
			
			con.close();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (canBeReportedOrphanFolders);
	}


	public static boolean canBeReportedDangling(int folderId) {

		boolean canBeReportedDangling = true;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "	select can_be_dangling " +
					" from gr_requirement_types rt, gr_folders f " +
					" where f.requirement_type_id = rt.id  " +
					" and  f.id = ?";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId );
			ResultSet rs = prepStmt.executeQuery();
			int canBeDangling = 0;
			while (rs.next()){
				canBeDangling = rs.getInt("can_be_dangling");
			}
			prepStmt.close();
			
			con.close();
			
			if (canBeDangling == 1) {
				canBeReportedDangling = true;
			}
			else {
				canBeReportedDangling = false;
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (canBeReportedDangling);
	}
	
	public static String getFoldersThatCanBeReportedDangling( int projectId) {


		String canBeReportedDanglingFolders = ",";
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "	select f.id " +
					" from gr_requirement_types rt, gr_folders f " +
					" where rt.project_id = ? and f.requirement_type_id = rt.id  " +
					" and can_be_dangling = 1 ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId );
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				canBeReportedDanglingFolders += rs.getInt("id") + ",";
			}
			prepStmt.close();
			
			con.close();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (canBeReportedDanglingFolders);
	}
	

	public static String getFoldersThatAreEnabledForApproval( int projectId) {


		String enabledForApprovalFolders = ",";
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "	select f.id " +
					" from gr_requirement_types rt, gr_folders f " +
					" where rt.project_id = ? and f.requirement_type_id = rt.id  " +
					" and enable_approval = 1 ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId );
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				enabledForApprovalFolders +=  rs.getInt("id") + ",";
			}
			prepStmt.close();
			
			con.close();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (enabledForApprovalFolders);
	}

	public static int getFolderMetric_NoOfCompletedRequirements( int folderId) {


		int noOfCompletedRequirements = 0;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = " select  count(*) noOfCompletedRequirements " + 
				" from gr_requirements r " + 
				" where r.folder_id = ? " +
				" and r.deleted = 0 " +
				" and r.pct_complete = 100 ";
		
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId );
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				noOfCompletedRequirements = rs.getInt("noOfCompletedRequirements");
			}
			

			rs.close();
			prepStmt.close();
			
			con.close();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (noOfCompletedRequirements);
	}
	
	
	public static int getFolderMetric_NoOfTestPendingRequirements( int folderId) {


		int noOfTestPendingRequirements = 0;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = " select  count(*) noOfTestPendingRequirements " + 
				" from gr_requirements r " + 
				" where r.folder_id = ? " +
				" and r.deleted = 0 " +
				" and r.testing_status = 'Pending'";
		
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId );
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				noOfTestPendingRequirements = rs.getInt("noOfTestPendingRequirements");
			}
			

			rs.close();
			prepStmt.close();
			
			con.close();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (noOfTestPendingRequirements);
	}
	
	
	public static int getFolderMetric_NoOfTestPassRequirements( int folderId) {


		int noOfTestPassRequirements = 0;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = " select  count(*) noOfTestPassRequirements " + 
				" from gr_requirements r " + 
				" where r.folder_id = ? " +
				" and r.deleted = 0 " +
				" and r.testing_status = 'Pass'";
		
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId );
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				noOfTestPassRequirements = rs.getInt("noOfTestPassRequirements");
			}
			

			rs.close();
			prepStmt.close();
			
			con.close();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (noOfTestPassRequirements);
	}

	public static int getFolderMetric_NoOfDanglingRequirements( int folderId) {


		int noOfDanglingRequirements = 0;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = " select  count(*) noOfDanglingRequirements " + 
				" from gr_requirements r " + 
				" where r.folder_id = ? " +
				" and r.deleted = 0 " +
				" and (r.trace_from is null or r.trace_from = '' )";
		
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId );
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				noOfDanglingRequirements = rs.getInt("noOfDanglingRequirements");
			}
			

			rs.close();
			prepStmt.close();
			
			con.close();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (noOfDanglingRequirements);
	}
	

	public static int getFolderMetric_NoOfOrphanRequirements( int folderId) {


		int noOfOrphanRequirements = 0;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = " select  count(*) noOfOrphanRequirements " + 
				" from gr_requirements r " + 
				" where r.folder_id = ? " +
				" and r.deleted = 0 " +
				" and (r.trace_to is null or r.trace_to = '' )";
		
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId );
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				noOfOrphanRequirements = rs.getInt("noOfOrphanRequirements");
			}
			

			rs.close();
			prepStmt.close();
			
			con.close();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (noOfOrphanRequirements);
	}
	

	public static int getFolderMetric_NoOfSuspectUpstreamRequirements( int folderId) {


		int noOfSuspectUpstreamRequirements = 0;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = " select  count(*) noOfSuspectUpstreamRequirements " + 
				" from gr_requirements r " + 
				" where r.folder_id = ? " +
				" and r.deleted = 0 " +
				" and r.trace_to like '%(s)%'";
		
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId );
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				noOfSuspectUpstreamRequirements = rs.getInt("noOfSuspectUpstreamRequirements");
			}
			

			rs.close();
			prepStmt.close();
			
			con.close();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (noOfSuspectUpstreamRequirements);
	}
	

	public static int getFolderMetric_NoOfSuspectDownstreamRequirements( int folderId) {


		int noOfSuspectDownstreamRequirements = 0;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = " select  count(*) noOfSuspectDownstreamRequirements " + 
				" from gr_requirements r " + 
				" where r.folder_id = ? " +
				" and r.deleted = 0 " +
				" and r.trace_from like '%(s)%'";
		
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId );
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				noOfSuspectDownstreamRequirements = rs.getInt("noOfSuspectDownstreamRequirements");
			}
			

			rs.close();
			prepStmt.close();
			
			con.close();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (noOfSuspectDownstreamRequirements);
	}


	public static int getFolderMetric_NoOfApprovalPendingRequirements( int folderId) {


		int noOfApprovalPendingRequirements = 0;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = " select  count(*) noOfApprovalPendingRequirements " + 
				" from gr_requirements r " + 
				" where r.folder_id = ? " +
				" and r.deleted = 0 " +
				" and r.status = 'In Approval WorkFlow' ";
		
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId );
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				noOfApprovalPendingRequirements = rs.getInt("noOfApprovalPendingRequirements");
			}
			

			rs.close();
			prepStmt.close();
			
			con.close();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (noOfApprovalPendingRequirements);
	}
	
	

	public static int getFolderMetric_NoOfRejectedRequirements( int folderId) {


		int noOfApprovalRejectedRequirements = 0;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = " select  count(*) noOfApprovalRejectedRequirements " + 
				" from gr_requirements r " + 
				" where r.folder_id = ? " +
				" and r.deleted = 0 " +
				" and r.status = 'Rejected' ";
		
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId );
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				noOfApprovalRejectedRequirements = rs.getInt("noOfApprovalRejectedRequirements");
			}
			

			rs.close();
			prepStmt.close();
			
			con.close();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (noOfApprovalRejectedRequirements);
	}
	
	public static int getFolderMetric_NoOfApprovedRequirements( int folderId) {


		int noOfApprovalApprovedRequirements = 0;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = " select  count(*) noOfApprovalApprovedRequirements " + 
				" from gr_requirements r " + 
				" where r.folder_id = ? " +
				" and r.deleted = 0 " +
				" and r.status = 'Approved' ";
		
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId );
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				noOfApprovalApprovedRequirements = rs.getInt("noOfApprovalApprovedRequirements");
			}
			

			rs.close();
			prepStmt.close();
			
			con.close();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (noOfApprovalApprovedRequirements);
	}
	
}
