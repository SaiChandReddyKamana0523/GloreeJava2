package com.gloree.utils;

//GloreeJava2

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;

import com.gloree.beans.ChangeLog;
import com.gloree.beans.DefectStatus;
import com.gloree.beans.Folder;
import com.gloree.beans.Project;
import com.gloree.beans.ProjectRelation;
import com.gloree.beans.ProjectRelationLight;
import com.gloree.beans.RTAttribute;
import com.gloree.beans.RTBaseline;
import com.gloree.beans.Report;
import com.gloree.beans.Requirement;
import com.gloree.beans.RequirementAttachment;
import com.gloree.beans.RequirementType;
import com.gloree.beans.Role;
import com.gloree.beans.ScheduledReport;
import com.gloree.beans.SecurityProfile;
import com.gloree.beans.Sprint;
import com.gloree.beans.User;
import com.gloree.beans.WebForm;
import com.gloree.beans.WordTemplate;
import com.gloree.beans.WordTemplateSN;

public class ProjectUtil {
	
	public static boolean isLicenseValid(String licenseString){
		if (licenseString.equals("X5K346L12K8DH4GF4G56FGXPPP236")){
			return(true);
		}
		else {
			return (false);
		}
	}

	public static String deleteSampleProject(int projectId){

		String message= "";
		String databaseType = "mySQL";
		
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// get the project and make sure that this is a SUN sample project
			
			Project project = new Project(projectId, databaseType);
			String projectPrefix = project.getShortName();
			String projectName = project.getProjectName();
			if (!(projectPrefix.equals("SUN") && (projectName.contains("Sample project")))){
				return "<div class='alert alert-danger'>NOT A SAMPLE PROJECT. NOT DELETING </div>";
			}
			
			message = "Deleting ... <br>project id --> " + projectId + "<br> Project Name " + projectName;
			
			
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--STARTING--");
			// lets delete the req baselines
			String sql = "delete from gr_requirement_baselines " +
				" where requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" ) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED baselines--");
			
			Thread.sleep(10);
			
			// we have to do the deletes in a certain sequence, due to Foreign / Primary key relationships.
			// lets delete the req versions
			sql = "delete from gr_requirement_versions " +
				" where requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" ) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED versions--");
			Thread.sleep(10);
			// lets delete the req comments
			sql = "delete from gr_requirement_comments " +
				" where requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" ) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED comments--");
			Thread.sleep(10);
			
			// lets delete the req log
			sql = "delete from gr_requirement_log " +
				" where requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" ) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED log--");
			Thread.sleep(10);

			
			
			// lets delete the req approval history 
			sql = "delete from gr_requirement_approval_h" +
				" where requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" ) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED approval_history--");
			Thread.sleep(10);
			// lets delete the req attribute value
			sql = "delete from gr_r_attribute_values " +
				" where requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" ) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED attribute values--");
			Thread.sleep(10);
			// lets delete the req traces
			sql = "delete from gr_traces " +
				" where from_requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" )  " +
				"or " +
				" to_requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" )  ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED traces--");
			Thread.sleep(10);
			
			// lets iterate through all the attachments attachments and drop them.
			sql = "select file_path " +
				" from gr_requirement_attachments a, gr_requirements r" +
				" where a.requirement_id = r.id " +
				" and r.project_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				Thread.sleep(5);
				String attachmentFilePath = rs.getString("file_path");
				File file = new File(attachmentFilePath);
				if (file != null){
					File dir = file.getParentFile();
					// lets drop the file.
					file.delete();
					
					if (dir != null) {
						dir.delete();
					}
				}
			}
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED files--");
			Thread.sleep(10);
			
			// now lets delete all requirement attachment entries in the db.
			sql = " delete from gr_requirement_attachments " +
				" where requirement_id  in " +
				"	(select id from gr_requirements where project_id = ? )";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.execute();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED files db records--");
			Thread.sleep(10);

			
			// lets delete the requirements 
			sql = "delete from gr_requirements " +
				" where project_id = " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED requirements--");
			Thread.sleep(10);
		
			// lets delete the role privs
			sql = "delete from gr_reports " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();	
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED reports--");
			Thread.sleep(10);
			
			// lets delete the role privs
			sql = "delete from gr_role_privs " +
				" where folder_id in (select id from gr_folders where project_id= " + projectId  + ")";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED roles privs--");
			Thread.sleep(10);
			
			// lets delete the word templates 
			sql = "delete from gr_word_templates " +
				" where project_id = " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED word templates--");
			Thread.sleep(10);
			
			// lets delete the folders
			sql = "delete from gr_folders " +
				" where project_id = " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED folders--");
			Thread.sleep(10);
			
			
			// lets delete the invitations 
			sql = "delete from gr_invitations " +
				" where project_id = " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED invitations--");
			Thread.sleep(10);
			
			// lets delete the user_roles 
			sql = "delete from gr_user_roles " +
				" where project_id = " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED user roles--");
			Thread.sleep(10);
			
			// lets delete the roles 
			sql = "delete from gr_roles" +
				" where project_id = " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED roles--");
			Thread.sleep(10);
			
			// lets delete the requirements_seq
			sql = "delete from gr_requirements_seq" +
				" where requirement_type_id in " +
				" (select id from gr_requirement_types where project_id= " + projectId  + ")";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED req seq--");
			prepStmt.close();			
			
			Thread.sleep(10);
			// lets delete the rt_attributes
			sql = "delete from gr_rt_attributes " +
				" where requirement_type_id in " +
				" (select id from gr_requirement_types where project_id= " + projectId  + ")";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED rt attributes--");
			Thread.sleep(10);
			
			// lets delete the rt_baselines
			sql = "delete from gr_rt_baselines " +
				" where requirement_type_id in " +
				" (select id from gr_requirement_types where project_id= " + projectId  + ")";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED rt baselines--");
			Thread.sleep(10);
									
			// lets delete the requirement_types
			sql = "delete from gr_requirement_types  " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED requirement types--");
			Thread.sleep(10);
			
			// lets delete the project_log
			sql = "delete from gr_project_log " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED project log--");
			Thread.sleep(10);
			
			// lets delete the gr-search
			sql = "delete from gr_search " +
				" where project_id= " + projectId  ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();	
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED search--");
			Thread.sleep(10);
			
			// we are deliberately not deleteing messages here.
			/*
			// lets delete the gr_messages
			sql = "delete from gr_messages " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			*/
			
			
			// lets delete the release requirements
			sql = "delete from gr_release_requirements " +
				" where project_id= " + projectId  ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED release requirements--");
			Thread.sleep(10);
			
			// lets delete the release_metrics
			sql = "delete from gr_release_metrics  " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();	
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED release metrics--");
			Thread.sleep(10);
			
			// lets delete the project_metrics
			sql = "delete from gr_project_metrics " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED project metrics--");
			Thread.sleep(10);
			
			// lets delete the user_metrics
			sql = "delete from gr_user_metrics " +
				" where project_id= " + projectId  ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();	
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED user metrics--");
			Thread.sleep(10);
			
			// lets delete the baseline metrics 
			sql = "delete from gr_baseline_metrics  " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();	
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED baseline metrics--");
			Thread.sleep(10);
			
			// lets delete the folder metrics
			sql = "delete from gr_folder_metrics " +
				" where project_id= " + projectId  ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED folder metrics--");
			Thread.sleep(10);
			
			// lets delete the requirement_types
			sql = "delete from gr_projects " +
				" where id  = " + projectId  ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			System.out.println("SRT deleting project " + java.util.Calendar.getInstance().getTime() 
					+ " project " + project.getProjectName() + "--DELETED project--\n\n\n\n");
			prepStmt.close();			
			
			Thread.sleep(10);
			
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
		return (message);
	}

	
	public static boolean isValidChildOption(String optionName, String parentAttributeValue){
		boolean isValidChild = false;
		
		// Sample: parentAtttibuteValues is Porsche,Jaguar  
		// Sample : optionName is Porsche:911
		
		// lets split the parentAttributeValues and see if any of them is contained in the optionName
		parentAttributeValue = parentAttributeValue + ",";
		String[] parents = parentAttributeValue.split(",");
		for(String p : parents){
			if (p == null){
				continue;
			}
			if (optionName.contains(p)){
				isValidChild = true;
			}
		}
				
		System.out.println(" in isValidChildOption for child option "  + optionName + " for parents " + parentAttributeValue + " response is " + isValidChild);
		
		return isValidChild;
	}
	
	public static ArrayList<Project> getSampleProjects(){

		ArrayList<Project> projects = new ArrayList<Project>();
		
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
			String sql = "";
			
			
				sql = " select id from gr_projects where short_name like 'SUN' "
						+ "and description like '%Sandbox Project%' and name like '%Sample%' "
						+ " and number_of_requirements < 100 "
						+ " order by id  limit 10 ";
			
			
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int projectId = rs.getInt("id");
				Project project = new Project(projectId, "mySQL");
				projects.add(project);
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
		return (projects);
	}

	
	public static ArrayList<Role> getRolesAtSameLevel(int requirementId, int folderId, Role matchRole){

		ArrayList<Role> roles = new ArrayList<Role>();
		
		if (matchRole == null){
			return (roles);
		}
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
			String sql = "";
			
			// find all the roles at the same level that have approval for this folder and requirement
				sql = " select rl.id, rl.project_id, rl.name, rl.description " +
						" from gr_role_privs rp  , gr_roles rl " 
						+ " where rp.role_id = rl.id " 
						+ " and rp.approve_requirement = 1 "
						+ " and rp.folder_id =  ? "
						+ " and rl.approval_rank =  ?  "
						+ " union "
						+ "  select rl.id, rl.project_id, rl.name, rl.description "
						+ " from gr_dynamic_roles dr, gr_roles rl "
						+ " where dr.requirement_id = ? "
						+ " and dr.role_id = rl.id "
						+ " and dr.approval_rank =  ?  "
				;
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId);
			prepStmt.setInt(2, matchRole.getApprovalRank());
			prepStmt.setInt(3, requirementId);
			prepStmt.setInt(4, matchRole.getApprovalRank());
			
			
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int id = rs.getInt("id");
				int projectId = rs.getInt("project_id");
				String name = rs.getString("name");
				String description = rs.getString("description");
				
				
				
				Role role = new Role(id, projectId, name, description);
				roles.add(role);
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
		return (roles);
	}

	
	public static int getRelatedProjectId(int currentProjectId, String relatedProjectPrefix){

		int relatedProjectId = 0;
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
			String sql = "";
			
			
				sql = " select related_project_id "
						+ " from gr_project_relations "
						+ " where project_id = ?  "
						+ " and related_project_short_name = ? ";
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, currentProjectId);
			prepStmt.setString(2, relatedProjectPrefix);
			
			
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				relatedProjectId = rs.getInt("related_project_id");
				
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
		return (relatedProjectId);
	}

	public static ArrayList<WordTemplateSN> getSNWordTemplatesCreatedBy(String createdBy) {
		
		ArrayList<WordTemplateSN> wordTemplatesSN = new ArrayList<WordTemplateSN>();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = "select id, file_type,  name,  description,  file_path, "
					+ " output_format, display_attributes,"
					+ " created_by," +
				" date_format(created_dt, '%d %M %Y %r ') \"created_dt\" ," +
				" last_modified_by," +
				" date_format(last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
				" from gr_sn_word_templates " +
				" where created_by  = ? and file_type = 'templateFile' ";
		
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, createdBy);
			
			
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				
				int templateId = rs.getInt("id");
				String fileType = rs.getString("file_type");
			    String templateName = rs.getString("name");
				String templateDescription = rs.getString("name");
				String templateFilePath = rs.getString("file_path");
				
				//this.sNProjectId = rs.getString("sn_project_id");
				String outputFormat = rs.getString("output_format");
				String displayAttributes  = rs.getString("display_attributes");
				
				String createdDt = rs.getString("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by");
				String lastModifiedDt = rs.getString("last_modified_dt");	
				
				
				System.out.println("srt adding template " + templateName);
				WordTemplateSN wTSN = new  WordTemplateSN(templateId,  fileType,  templateName,
						 templateDescription,  templateFilePath, 
						  outputFormat,  displayAttributes,
						 createdBy,  createdDt,  lastModifiedBy,  lastModifiedDt);
				
				wordTemplatesSN.add(wTSN);
				
				
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
		return wordTemplatesSN;
	}
	
	public static ArrayList<WordTemplateSN> getSNDataFilesCreatedBy(String createdBy) {
		
		ArrayList<WordTemplateSN> dataFilesSN = new ArrayList<WordTemplateSN>();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = "select id, file_type,  name,  description,  file_path, "
					+ " output_format, display_attributes,"
					+ " created_by," +
				" date_format(created_dt, '%d %M %Y %r ') \"created_dt\" ," +
				" last_modified_by," +
				" date_format(last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
				" from gr_sn_word_templates " +
				" where created_by  = ? and file_type = 'dataFile' ";
		
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, createdBy);
			
			
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				
				int templateId = rs.getInt("id");
				String fileType = rs.getString("file_type");
			    String templateName = rs.getString("name");
				String templateDescription = rs.getString("name");
				String templateFilePath = rs.getString("file_path");
				
				//this.sNProjectId = rs.getString("sn_project_id");
				String outputFormat = rs.getString("output_format");
				String displayAttributes  = rs.getString("display_attributes");
				
				String createdDt = rs.getString("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by");
				String lastModifiedDt = rs.getString("last_modified_dt");	
				
				
				System.out.println("srt adding template " + templateName);
				WordTemplateSN wTSN = new  WordTemplateSN(templateId,  fileType,  templateName,
						 templateDescription,  templateFilePath, 
						  outputFormat,  displayAttributes,
						 createdBy,  createdDt,  lastModifiedBy,  lastModifiedDt);
				
				dataFilesSN.add(wTSN);
				
				
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
		return dataFilesSN;
	}

	public static ArrayList<WordTemplateSN> getSNWordTemplates(String sNProjectId) {
		
		ArrayList<WordTemplateSN> wordTemplatesSN = new ArrayList<WordTemplateSN>();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = "select id,   name,  description,  file_path, "
					+ "  sn_project_id, output_format, display_attributes,"
					+ " created_by," +
				" date_format(created_dt, '%d %M %Y %r ') \"created_dt\" ," +
				" last_modified_by," +
				" date_format(last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
				" from gr_sn_word_templates " +
				" where sn_project_id  = ? ";
		
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, sNProjectId);
			
			
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				
				int templateId = rs.getInt("id");
			    String templateName = rs.getString("name");
				String templateDescription = rs.getString("name");
				String templateFilePath = rs.getString("file_path");
				
				//this.sNProjectId = rs.getString("sn_project_id");
				String outputFormat = rs.getString("output_format");
				String displayAttributes  = rs.getString("display_attributes");
				
				String createdBy = rs.getString("created_by");
				String createdDt = rs.getString("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by");
				String lastModifiedDt = rs.getString("last_modified_dt");	
				
				
				System.out.println("srt adding template " + templateName);
				WordTemplateSN wTSN = new  WordTemplateSN(templateId,    templateName,
						 templateDescription,  templateFilePath, 
						  outputFormat,  displayAttributes,
						 createdBy,  createdDt,  lastModifiedBy,  lastModifiedDt);
				
				wordTemplatesSN.add(wTSN);
				
				
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
		return wordTemplatesSN;
	}
	
	public static int daysSinceInstallation(String databaseType){

		int daysSinceInstallation = 0;
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
			String sql = "";
			
			if (databaseType.equals("mySQL")){
				sql = " select max(datediff(now() , created_dt)) 'daysSinceInstallation' " +
					" from gr_projects ";
			}
			else {
				sql = " select max( sysdate - created_dt) 'daysSinceInstallation' " +
					" from gr_projects";
				
			}
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				daysSinceInstallation = rs.getInt("daysSinceInstallation");
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
		return (daysSinceInstallation);
	}

	
	public static String getProjectShortName(int projectId){

		String projectShortName = "";
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
			String sql = "select short_name from gr_projects where id = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				projectShortName = rs.getString("short_name");
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
		return (projectShortName);
	}


	
	public static String withdrawInvitation(String withdrawRequestor, int inviteId){

		String message = "";
		
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets see if the invitor is same guy who sent original invitation
			//
			String sql = "select  i.invitor_email_id "
					+ " from gr_invitations i "
					+ " where  i.id =  ?   ";
			
			
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, inviteId);
			rs = prepStmt.executeQuery();
			String invitor = "";
			while (rs.next()){
				
				 invitor = rs.getString("invitor_email_id");
				
			}
			prepStmt.close();
			rs.close();
			
			if (withdrawRequestor.equals(invitor)){
				sql = "delete from gr_invitations  where id =    " + inviteId;
				
				
				prepStmt = con.prepareStatement(sql);
				
				prepStmt.execute();
				
				message = "success";
				
			}
			else {
				message = "<div class='alert alert-danger'> You do not have permissions to remove this invitation</div>";
			}
			
			
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
		return (message);
	}
	
	public static ArrayList<String> getInvitedUserByMe(String invitorEmailId){

		
		ArrayList<String> invitedUsers = new ArrayList<String>() ;
		
		
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
			String sql = "select i.id 'invite_id', i.invitee_email_id, i.invitor_email_id, i.event_dt, "
					+ " i.last_email_sent_on, i.emails_sent, r.name, i.id, p.id 'project_id', p.name 'project_name' "
					+ " from gr_invitations i , gr_roles r, gr_projects p"
					+ " where i.invitor_email_Id = ? and i.project_id = p.id  "
					+ " and i.role_id = r.id  "
					+ "   order by p.name, event_dt desc ";
			
			
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, invitorEmailId);
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				String invitee = rs.getString("invitee_email_id");
				String invitor = rs.getString("invitor_email_id");
				String inviteDt = rs.getString("event_dt");
				String lastEmailDt = rs.getString("last_email_sent_on");
				int emailsSent = rs.getInt("emails_sent");
				String roleName = rs.getString("name");
				int inviteId = rs.getInt("id");
				int projectId = rs.getInt("project_id");
				String projectName = rs.getString("project_name");
				invitedUsers.add(invitee + ":##:"  + invitor + ":##:" + inviteDt + ":##:"  
				+ lastEmailDt + ":##:" + emailsSent + ":##:" +
				roleName + ":##:" + inviteId + ":##:" + projectId + ":##:" + projectName );
				
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
		return (invitedUsers);
	}
	
	public static ArrayList getInvitedUsers(int projectId){

		
		ArrayList invitedUsers = new ArrayList();
		
		
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
			String sql = "select i.invitee_email_id, i.invitor_email_id, i.event_dt, i.last_email_sent_on, r.name, i.id "
					+ " from gr_invitations i , gr_roles r"
					+ " where i.project_id = ? "
					+ " and i.role_id = r.id ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				String invitee = rs.getString("invitee_email_id");
				String invitor = rs.getString("invitor_email_id");
				String inviteDt = rs.getString("event_dt");
				String lastEmailDt = rs.getString("last_email_sent_on");
				String roleName = rs.getString("name");
				int inviteId = rs.getInt("id");
				invitedUsers.add(invitee + "##" + invitor + "##" + inviteDt + "##" + lastEmailDt + "##" + roleName + ":##" + inviteId);
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
		return (invitedUsers);
	}
	
	public static ArrayList getRequirementTypesInAProject(int projectId){

		ArrayList myRequirementTypes = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// NOTE : to get the Requirement Types sorted in the same order as folder orders, 
			// we have this query. it takes advantage of the business rule that every req type
			// has a corresponding root level folder.
		
			String sql = "SELECT rt.id, rt.project_id, rt.short_name, rt.name, rt.description," +
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
		return (myRequirementTypes);
	}

	//
	// This class is used to generate a list of all projects a user has access to
	// TODO : currently it lists all projects. once we implement security, this needs to be modified.
	//
	public static ArrayList getMyProjects(){

		ArrayList myProjects = new ArrayList();
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
			String sql = "select id, short_name, name, project_type, description," +
				" owner, website, organization, tags,  restricted_domains, enable_tdcs, enable_agile_scrum," +
				" billing_organization_id, " +
				" number_of_requirements, created_by, created_dt, last_modified_by, last_modified_dt , archived, hide_priority " +
				" from gr_projects ";
			prepStmt = con.prepareStatement(sql);
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
				
				//TODO : at some point see how we can make DATE fields works.
				Project project = new Project(projectId, shortName, projectName	, projectType,
					projectDescription, 
					projectOwner, projectWebsite, projectOrganization, projectTags, 
					restrictedDomains, enableTDCS, enableAgileScrum,
					billingOrganizationId,
					numberOfRequirements, createdBy, lastModifiedBy, archived, hidePriority);
				myProjects.add(project);
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
		return (myProjects);
	}

	public static ArrayList getFolderInAProject(int projectId){

		ArrayList myFolders = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// LEVEL1 Folder fill up starts here.
			// get the list of folders for this project, create folder objects for each folder and put them in the array list
			// called Folders.
			String sql = "SELECT f.id, f.project_id, f.name, f.description, f.parent_folder_id, f.folder_level," +
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
		return (myFolders);
	}

	public static ArrayList getFolderInAProjectLite(int projectId){

		ArrayList myFolders = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// LEVEL1 Folder fill up starts here.
			// get the list of folders for this project, create folder objects for each folder and put them in the array list
			// called Folders.
			String sql = "SELECT f.id, f.parent_folder_id, f.folder_path " +
				" FROM gr_folders f, gr_requirement_types rt" +
				" where f.requirement_type_id = rt.id and  f.project_id = ? and f.folder_level = 1 " +
				" order by rt.display_sequence, f.folder_order, f.name";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int folderId = rs.getInt("id");
				int parentFolderId = rs.getInt("parent_folder_id");
				String folderPath = rs.getString("folder_path");
				myFolders.add(folderId + ":##:" + folderPath);
				
				
				
				// LEVEL2 Folder fill up starts here.				
				// Now for each one of these Folders i.e. level1FolderId, find children and add them to the myFolders list.
				// reset the parentFolder Id
				parentFolderId = folderId;
				
				sql = "SELECT f.id,  f.parent_folder_id, f.folder_path " +
				" FROM gr_folders f, gr_requirement_types rt" +
				" where f.requirement_type_id = rt.id " +
				" and f.parent_folder_id = ? " +
				" order by f.folder_order, f.name";
			
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, parentFolderId);
				ResultSet rs2 = prepStmt.executeQuery();
				while (rs2.next()){
					folderId = rs2.getInt("id");
					parentFolderId = rs2.getInt("parent_folder_id");
					folderPath = rs2.getString("folder_path");
					myFolders.add(folderId + ":##:" + folderPath);
					
					// LEVEL3 Folder fill up starts here.
					// Now for each one of these Folders i.e. level1FolderId, find children and add them to the myFolders list.
					
					// reset the parentFolder Id
					parentFolderId = folderId;
					
					sql = "SELECT f.id, f.parent_folder_id, f.folder_path " +
					" FROM gr_folders f, gr_requirement_types rt" +
					" where f.requirement_type_id = rt.id " +
					" and f.parent_folder_id = ? " +
					" order by f.folder_order, f.name";
				
				
					prepStmt = con.prepareStatement(sql);
					prepStmt.setInt(1, parentFolderId);
					ResultSet rs3 = prepStmt.executeQuery();
					while (rs3.next()){
						folderId = rs3.getInt("id");
						parentFolderId = rs3.getInt("parent_folder_id");
						folderPath = rs3.getString("folder_path");
						myFolders.add(folderId + ":##:" + folderPath);
						
						// LEVEL4 Folder fill up starts here.
						// Now for each one of these Folders i.e. level1FolderId, find children and add them to the myFolders list.
						
						// reset the parentFolder Id
						parentFolderId = folderId;
						
						
						sql = "SELECT f.id, f.parent_folder_id, f.folder_path " +
						" FROM gr_folders f, gr_requirement_types rt" +
						" where f.requirement_type_id = rt.id " +
						" and f.parent_folder_id = ? " +
						" order by f.name";
						prepStmt = con.prepareStatement(sql);
						prepStmt.setInt(1, parentFolderId);
						ResultSet rs4 = prepStmt.executeQuery();
						while (rs4.next()){
							folderId = rs4.getInt("id");
							// we use the projectId we got as a parameter to this constructor.
							parentFolderId = rs4.getInt("parent_folder_id");
							folderPath = rs4.getString("folder_path");
							myFolders.add(folderId + ":##:" + folderPath);
							
						}
						
						// LEVEL4 Folder fill up ends here.
					}
					// LEVEL3 Folder fill up ends here.
				}
				// LEVEL2 Folder fill up ends here.
				
			}
			// LEVEL1 Folder fill up ends here.
			
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
		return (myFolders);
	}


	public static ArrayList getFolderInAProjectLiteWithLevel(int projectId){

		ArrayList myFolders = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// LEVEL1 Folder fill up starts here.
			// get the list of folders for this project, create folder objects for each folder and put them in the array list
			// called Folders.
			String sql = "SELECT f.id, f.parent_folder_id, f.name, f.folder_path " +
				" FROM gr_folders f, gr_requirement_types rt" +
				" where f.requirement_type_id = rt.id and  f.project_id = ? and f.folder_level = 1 " +
				" order by rt.display_sequence, f.folder_order, f.name";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int folderId = rs.getInt("id");
				int parentFolderId = rs.getInt("parent_folder_id");
				String folderPath = rs.getString("folder_path");
				String folderName = rs.getString("name");
				
				myFolders.add("1:##:" + folderId + ":##:" + folderName + ":##:" + folderPath);
				
				
				
				// LEVEL2 Folder fill up starts here.				
				// Now for each one of these Folders i.e. level1FolderId, find children and add them to the myFolders list.
				// reset the parentFolder Id
				parentFolderId = folderId;
				
				sql = "SELECT f.id,  f.parent_folder_id, f.name, f.folder_path " +
				" FROM gr_folders f, gr_requirement_types rt" +
				" where f.requirement_type_id = rt.id " +
				" and f.parent_folder_id = ? " +
				" order by f.folder_order, f.name";
			
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, parentFolderId);
				ResultSet rs2 = prepStmt.executeQuery();
				while (rs2.next()){
					folderId = rs2.getInt("id");
					parentFolderId = rs2.getInt("parent_folder_id");
					folderPath = rs2.getString("folder_path");
					folderName = rs2.getString("name");
					
					myFolders.add("2:##:" + folderId + ":##:" + folderName + ":##:" +  folderPath);
					
					// LEVEL3 Folder fill up starts here.
					// Now for each one of these Folders i.e. level1FolderId, find children and add them to the myFolders list.
					
					// reset the parentFolder Id
					parentFolderId = folderId;
					
					sql = "SELECT f.id, f.parent_folder_id, f.name, f.folder_path " +
					" FROM gr_folders f, gr_requirement_types rt" +
					" where f.requirement_type_id = rt.id " +
					" and f.parent_folder_id = ? " +
					" order by f.folder_order, f.name";
				
				
					prepStmt = con.prepareStatement(sql);
					prepStmt.setInt(1, parentFolderId);
					ResultSet rs3 = prepStmt.executeQuery();
					while (rs3.next()){
						folderId = rs3.getInt("id");
						parentFolderId = rs3.getInt("parent_folder_id");
						folderPath = rs3.getString("folder_path");
						folderName = rs3.getString("name");
						
						myFolders.add("3:##:" + folderId + ":##:" + folderName + ":##:" +  folderPath);
						
						// LEVEL4 Folder fill up starts here.
						// Now for each one of these Folders i.e. level1FolderId, find children and add them to the myFolders list.
						
						// reset the parentFolder Id
						parentFolderId = folderId;
						
						
						sql = "SELECT f.id, f.parent_folder_id, f.name, f.folder_path " +
						" FROM gr_folders f, gr_requirement_types rt" +
						" where f.requirement_type_id = rt.id " +
						" and f.parent_folder_id = ? " +
						" order by f.name";
						prepStmt = con.prepareStatement(sql);
						prepStmt.setInt(1, parentFolderId);
						ResultSet rs4 = prepStmt.executeQuery();
						while (rs4.next()){
							folderId = rs4.getInt("id");
							// we use the projectId we got as a parameter to this constructor.
							parentFolderId = rs4.getInt("parent_folder_id");
							folderPath = rs4.getString("folder_path");
							folderName = rs4.getString("name");
							
							myFolders.add("4:##:" + folderId + ":##:" + folderName + ":##:" + folderPath);
							
						}
						
						// LEVEL4 Folder fill up ends here.
					}
					// LEVEL3 Folder fill up ends here.
				}
				// LEVEL2 Folder fill up ends here.
				
			}
			// LEVEL1 Folder fill up ends here.
			
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
		return (myFolders);
	}
	
	public static ArrayList getMyScheduledReports(int projectId, String ownerEmailId){

		ArrayList myScheduledReports= new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select sr.id, sr.project_id, sr.report_id, sr.attachment_type, sr.to_email_addresses, sr.cc_email_addresses," +
				" sr.subject_value, sr.message_value, sr.run_task_on, sr.owner " +
				" from gr_scheduled_reports sr, gr_reports r " +
				" where sr.report_id = r.id " +
				" and sr.project_id = ?  " +
				" and sr.owner= ?" +
				" order by r.name  ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,projectId);
			prepStmt.setString(2, ownerEmailId);
			rs = prepStmt.executeQuery();
			// Only one row should be returned.
			while (rs.next()){				
				int scheduledReportId = rs.getInt("id");
				int reportId = rs.getInt("report_id");
				String attachmentType = rs.getString("attachment_type");
				String toEmailAddresses = rs.getString("to_email_addresses");
				String ccEmailAddresses = rs.getString("cc_email_addresses");
				String subjectValue = rs.getString("subject_value");
				String messageValue = rs.getString("message_value");
				String runTaskOn =  rs.getString("run_task_on");
				
				ScheduledReport scheduledReport = new ScheduledReport(scheduledReportId, projectId, reportId, attachmentType, toEmailAddresses,
						ccEmailAddresses, subjectValue, messageValue,runTaskOn,ownerEmailId);
				myScheduledReports.add(scheduledReport);
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
		return (myScheduledReports);
	}
	
	public static ArrayList findProjects(String projectSearchString){

		ArrayList projects = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// this sql does a wild card search and gets all the projects in the system
			// that match the test string.
			String sql = "select id, short_name, name, project_type, description," +
				" owner, website, organization, tags,  restricted_domains, enable_tdcs, enable_agile_scrum," +
				" billing_organization_id, " +
				" number_of_requirements, created_by, created_dt, last_modified_by, last_modified_dt, archived, hide_priority  " +
				" from gr_projects " +
				" where (upper(short_name) like '%"+ projectSearchString.toUpperCase()  +"%'" +
				" 	or name like '%"+ projectSearchString  +"%' " +
				" 	or upper(description) like '%"+ projectSearchString.toUpperCase()  +"%' " +
				" 	or upper(owner) like '%"+ projectSearchString.toUpperCase()  +"%' " +
				" 	or upper(website) like '%"+ projectSearchString.toUpperCase()  +"%' " +
				"	or upper(organization) like '%"+ projectSearchString.toUpperCase()  +"%' " +
				"	or upper(tags) like '%"+ projectSearchString.toUpperCase()  +"%' " +
				"	or upper(created_by) like '%"+ projectSearchString.toUpperCase()  +"%' " +
				"	or upper(last_modified_by) like '%"+ projectSearchString.toUpperCase()  +"%' " +
				"	or upper(restricted_domains) like '%"+ projectSearchString.toUpperCase()  +"%'" +
				"	)";

			prepStmt = con.prepareStatement(sql);
			
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
				
				
				//TODO : at some point see how we can make DATE fields works.
				Project project = new Project(projectId, shortName, projectName	, projectType,
					projectDescription, 
					projectOwner, projectWebsite, projectOrganization, projectTags, 
					restrictedDomains, enableTDCS, enableAgileScrum,
					billingOrganizationId,
					numberOfRequirements, createdBy, lastModifiedBy, archived, hidePriority);
				projects.add(project);
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
		return (projects);
	}


	// returns an arraylist of  project administrators.
	public static ArrayList getProjectAdministrators(int projectId){

		ArrayList administrators = new ArrayList();
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
			String sql = "select u.email_id " +
				" from gr_user_roles ur, gr_roles r, gr_users u " + 
				" where ur.project_id = ? " +
				" and ur.role_id = r.id " +
				" and r.name = 'Administrator' " +
				" and ur.user_id = u.id "; 
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				String emailId = rs.getString("email_id");
				administrators.add(emailId);
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
		return (administrators);
	}


	// connect a project to another project.
	public static void relateProjects(Project project ,int connectToProjectId,
		String connectionDescription, String actorEmailId, String databaseType){

		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			Project relatedProject = new Project(connectToProjectId,  databaseType);
			
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_project_relations " +
					"(project_id, project_short_name," +
					" related_project_id, related_project_short_name," +
					" relation_made_by, relation_made_dt, relation_description )" +
					" values (?,?,?,?,?,now(),?)";
			}
			else {
				sql = "insert into gr_project_relations " +
				"(project_id, project_short_name," +
				" related_project_id, related_project_short_name," +
				" relation_made_by, relation_made_dt, relation_description )" +
				" values (?,?,?,?,?, sysdate,?)";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, project.getProjectId());
			prepStmt.setString(2, project.getShortName());
			prepStmt.setInt(3, relatedProject.getProjectId());
			prepStmt.setString(4, relatedProject.getShortName());
			prepStmt.setString(5, actorEmailId);
			prepStmt.setString(6, connectionDescription);
			
			prepStmt.execute();
			
			
			// lets update the project log with this connection information.
			ProjectUtil.createProjectLog(project.getProjectId(), relatedProject.getShortName(), "Connect to Project",
				"Project '" + project.getProjectName() + "' is connected to '" +
				relatedProject.getProjectName() , actorEmailId ,  databaseType);
			
			// every time project A is connected to project B, we also need to make an entry connecting
			// project B to project A
			prepStmt.setInt(1, relatedProject.getProjectId());
			prepStmt.setString(2, relatedProject.getShortName());
			prepStmt.setInt(3, project.getProjectId());
			prepStmt.setString(4, project.getShortName());
			prepStmt.setString(5, actorEmailId);
			prepStmt.setString(6, connectionDescription);
			
			prepStmt.execute();
			
			
			// lets update the project log with this connection information.
			ProjectUtil.createProjectLog(relatedProject.getProjectId(), project.getShortName(), "Connect to Project",
				"Project '" + relatedProject.getProjectName() + "' is connected to '" +
				project.getProjectName() , actorEmailId,  databaseType);
			
			
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

	

	// dis connect a project from another project.
	public static void unrelateProjects(Project project ,int disconnectProjectId,
		 String actorEmailId, String databaseType){

		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			Project relatedProject = new Project(disconnectProjectId,  databaseType);
			
			String sql = "delete from gr_project_relations " +
				" where project_id = ? " +
				" and related_project_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, project.getProjectId());
			prepStmt.setInt(2,disconnectProjectId);
			prepStmt.execute();
			
			
			// lets update the project log with this connection information.
			ProjectUtil.createProjectLog(project.getProjectId(), relatedProject.getShortName(), "Disonnect from Project",
				"Project '" + project.getProjectName() + "' is  now disconnected from '" +
				relatedProject.getProjectName() , actorEmailId,  databaseType);
			
			// every time project A is disconnected from project B, we also need to make an entry disconnecting
			// project B to project A
			prepStmt.setInt(1, relatedProject.getProjectId());
			prepStmt.setInt(2, project.getProjectId());
			prepStmt.execute();
			
			
			// lets update the project log with this connection information.
			ProjectUtil.createProjectLog(relatedProject.getProjectId(), project.getShortName(), "Dicconnect from Project",
				"Project '" + relatedProject.getProjectName() + "' is disconnected from '" +
				project.getProjectName() , actorEmailId,  databaseType);
			
			
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

	
	
	public static void rebuildSearchIndex(Project project , String actorEmailId){

			PreparedStatement prepStmt = null;
			ResultSet rs = null;
			java.sql.Connection con = null;
			try {
				System.out.println("srt in rebuildSearchIndex for project " + project.getProjectId());
			
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();
			    // lets create the gr_search table.
			    String sql = " delete from  gr_search where project_id = ? ";
			    prepStmt = con.prepareStatement(sql);
			    prepStmt.setInt(1, project.getProjectId());
			    
			    prepStmt.execute();
			    prepStmt.close();
			    
			    
			    // lets set the session global_concat length to long
			    
			    
			    sql = " SET SESSION group_concat_max_len=150000";
			    prepStmt = con.prepareStatement(sql);
			    
			    prepStmt.execute();
			    prepStmt.close();
			    
			    System.out.println("Emptied the project search table for projectId " + project.getProjectId() + " @ " + Calendar.getInstance().getTime());
			    
			    // lets populate the gr_search table.
			    
			    	sql = " insert into gr_search (project_id, object_id, object_type, object_text) " + 
			    	" 		select r.project_id, r.id, 'Requirement', " +
			    	" 			concat(r.full_tag, ' ',  r.name, ' ', r.description, ' '," +
			    	"		    r.owner, ' ',   r.status, ' ', r.priority,' ', " +
			    	"	 		ifnull(r.external_url,' '), ' ', r.user_defined_attributes, ' ',  r.created_by, ' ', " +
			    	"			r.last_modified_by, ' ', " +
			    	"			ifnull(c.comment_note,' '), ' ',  ifnull(c.commenter_email_id,' ')) " +
			    	"		from gr_requirements r left join " +
			    	"			(select requirement_id, group_concat(distinct comment_note separator ' ') 'comment_note', " +
			    	"				group_concat(distinct commenter_email_id separator ' ') 'commenter_email_id' " +
			    	"			from gr_requirement_comments group by requirement_id) c " +
			    	"		on  (r.id = c.requirement_id) " + 
			    	" where r.project_id = ? ";
				 
			    	System.out.println("srt sql for rebuild index is \n" + sql );
			   prepStmt = con.prepareStatement(sql);
			   prepStmt.setInt(1, project.getProjectId());
			   prepStmt.execute();
			    prepStmt.close();
			    System.out.println("Insered rows into the gr_search table for project id +  " + project.getProjectId() + " @ " + Calendar.getInstance().getTime());
			    
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

		
	// take a requirementType short name, name, desc anc project Id and creates a requirement type. 
	// This method returns a requirementtypeid
	// that is used to immediately create a root level folder.
	// Part of the requirement type creation is the seeding the gr_requirements_seq table with the starting value.
	
	
	public static int createARequirementType(int projectId, String rTShortName,String rTName,
			String rTDescription, int rTDisplaySequence, 
			int rTEnableApproval, int rTEnableAgileScrum, int rTCanBeDangling, 
			int rTCanBeOrphan, String requirementTypeCanNotTraceTo, String createdByEmailId, 
			String databaseType){
		// Create Business Requirement Type
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		
		String sql = "";
		if (databaseType.equals("mySQL")){
			sql = " insert into gr_requirement_types (project_id,short_name,name," +
				" description, display_sequence, enable_approval, enable_agile_scrum, can_be_dangling, can_be_orphan, " +
				" can_not_trace_to, created_by,created_dt,last_modified_by,last_modified_dt)" +
				" values (?,?,?," +
				"?,?, ? ,?, ?,?," +
				"?, ? ,now(),? ,now()) ";
		}
		else {
			sql = " insert into gr_requirement_types (project_id,short_name,name," +
			" description, display_sequence, enable_approval, enable_agile_scrum, can_be_dangling, can_be_orphan, " +
			" can_not_trace_to, created_by,created_dt,last_modified_by,last_modified_dt)" +
			" values (?,?,?," +
			"?,?, ? ,?,?, ?," +
			"?, ? , sysdate,? ,sysdate) ";
		}
		int requirementTypeId = 0 ;
		
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			prepStmt = con.prepareStatement(sql);

			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, rTShortName);
			prepStmt.setString(3, rTName);
			prepStmt.setString(4, rTDescription);
			prepStmt.setInt(5, rTDisplaySequence);
			prepStmt.setInt(6, rTEnableApproval);
			prepStmt.setInt(7, rTEnableAgileScrum);
			prepStmt.setInt(8, rTCanBeDangling);
			prepStmt.setInt(9, rTCanBeOrphan);
			prepStmt.setString(10, requirementTypeCanNotTraceTo);
			prepStmt.setString(11, createdByEmailId);
			prepStmt.setString(12, createdByEmailId);
			prepStmt.execute();
			prepStmt.close();
			
			// 	get the requirement type id from for BR so that we can create the corresponding 
			// folder.
			sql = "select id from gr_requirement_types where project_id = ? and short_name = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, rTShortName);
		
			rs = prepStmt.executeQuery();
			while (rs.next()){
				requirementTypeId = rs.getInt("id");
			}
			
			rs.close();
			prepStmt.close();

			// NOTE : this is critical. we need to seed the requirement_seq table 
			// with the requirement type id and
			// a starting value of 0. Hence the first tag wil be BR-1, BR-2 ..
			sql="insert into gr_requirements_seq (requirement_type_id,tag) values (?,0)";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId );
			prepStmt.execute();
			
			// once you have requirementTypeId, call the createARootFolder method 
			// in the calling routine.
			ProjectUtil.createProjectLog(projectId, rTName, "Create", 
				"Create A default Requirement Type as part of project creation process", 
				createdByEmailId,  databaseType);
			
			con.close();
			}
			catch (Exception e) {
				// 	TODO Auto-generated catch block
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
		return requirementTypeId ;
	}
	
	// called when a user tries to update a RequirementType. This routine only updates name and description.
	// and requirementTypeEnableApproval. we have decided against letting a user change a prefix 
	// because that can lead to complications with 
	// a. auditing , b. ensuring that the req full tags are unique.
	// Also note that when a requirement type name is changed, we also need to change the name 
	// of the Requirement Type's root folder.
	public static void updateRequirementType(int projectId, int requirementTypeId, 
		String requirementTypeName, String requirementTypeDescription, int requirementTypeDisplaySequence, 
		int requirementTypeEnableApproval, int requirementTypeCanBeDangling, int requirementTypeCanBeOrphan, 
		String requirementTypeCanNotTraceTo, String actorEmailId, String databaseType){
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			RequirementType requirementType = new RequirementType(requirementTypeId);
			int oldRTEnableApproval = requirementType.getRequirementTypeEnableApproval();
			
			String oldRequirementTypeName = requirementType.getRequirementTypeName();
			
			String sql = "update gr_requirement_types set name = ? , description = ? , " +
				" display_sequence = ? , enable_approval=?, can_be_dangling = ? , can_be_orphan =?," +
				" can_not_trace_to = ?   " +
				" where id = ? ";
			prepStmt = con.prepareStatement(sql);
			
			// Do the update. 
			
			prepStmt.setString(1, requirementTypeName);
			prepStmt.setString(2, requirementTypeDescription);
			
			prepStmt.setInt(3, requirementTypeDisplaySequence);
			prepStmt.setInt(4, requirementTypeEnableApproval);
			prepStmt.setInt(5, requirementTypeCanBeDangling);
			prepStmt.setInt(6, requirementTypeCanBeOrphan);
			prepStmt.setString(7, requirementTypeCanNotTraceTo);
			prepStmt.setInt(8,requirementTypeId);
			prepStmt.execute();
			prepStmt.close();
			
			String log = "Updating the Requirement Type <br> Name : " + requirementTypeName + 
				"<br> Description : " + requirementTypeDescription +
				" <br> Display Order : " + requirementTypeDisplaySequence +
				" <br> Enable Approval : " + requirementTypeEnableApproval +
				" <br> Enable Approval : " + requirementTypeEnableApproval +
				" <br> Can be Dangling : " + requirementTypeCanBeDangling + 
				" <br> Can be Orphan : " + requirementTypeCanBeOrphan + 
				" <br> Can not Trace To : " + requirementTypeCanNotTraceTo;
				
			
			
			// When a requirement type's Approval Workflow is disabled, lets identify all requirements
			// that currently are in the middle of the approval workflow and update them as follows : 
			// 1. Set the requirement's approval status to 'Draft'
			// 2. Set the 'approvers' field to empty
			// 3. Set the 'submitted_for_approval' field to empty
			// 4. Set the 'last_aprpoval_for_reminder_sent_dt' to empty
			
			if (
				(oldRTEnableApproval == 1)
				&&
				(requirementTypeEnableApproval == 0)
				){
				
				// lets wipe out the entries in gr_Requirement table related to approval work flow
				System.out.println("srt : This Req type was previously enabled for approval , and it now being set to disabled. So I will identify all requirements and clear out their approval status ");
				sql = "update gr_requirements " + 
						" set status = 'Draft',   approvers = null,  submitted_for_approval_dt = null,  last_approval_reminder_sent_dt = null " + 
						" where requirement_type_id = " + requirementTypeId;
				prepStmt = con.prepareStatement(sql);
				prepStmt.execute();
				prepStmt.close();
				
				
				// lets wipe out the entries in the gr_requirement_approval_history table related to approval work flow.
				sql = "delete from gr_requirement_approval_h where requirement_id in (" + 
						" select id " +
						" from gr_requirements " +
						" where requirement_type_id = " + requirementTypeId +  
						" ) ";
				
				System.out.println("srt : Deleting Requirement_approval_h for this req type. SQL is : \n\n" + sql );
				prepStmt = con.prepareStatement(sql);
				prepStmt.execute();
				prepStmt.close();
				
			}
			
			
			
			ProjectUtil.createProjectLog(projectId, requirementTypeName , "Update ReqType",
				log,  actorEmailId,  databaseType);
			
			if (!(oldRequirementTypeName.equals(requirementTypeName))){
				// this means that the new requirement type name is different from the old
				// requirement type name. 
				// Since the root folder name for a requirement type is same as the requirement type name
				// we need to change the name of the root folder. 
				// Also note that since the root folder's name is changing, it will change the path of a
				// all the sub folders in this root folder.
				Folder rootFolder = new Folder(requirementType.getRootFolderId());
				rootFolder.setNameAndDescription(rootFolder.getFolderId(), requirementTypeName, rootFolder.getFolderDescription());
				
				// lets log the folder name change event.
				log = "Updating the Root Folder <br> Name : " + requirementTypeName + 
				"<br> becuase the Requirement Type's name has changed" ;
				
				ProjectUtil.createProjectLog(projectId, requirementTypeName , "Update ReqType",
						log,  actorEmailId,  databaseType);
			
			}
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
	
	
	// take a requirement type id, project id and creates root level folder that's linked to a requirementtype.
	// returns the id of the folder just created.
	
	public static int createAFolder(int projectId,  int requirementTypeId, String folderName,
		int folderOrder, String createdByEmailId, String databaseType) {
		// this will create the default folder for the Requirement Types
		//TODO remove 'system' and put in the user name once we implement security.
		
		int folderIdReturned = 0;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			ProjectUtil.createProjectLog(projectId, folderName, "Create", 
					"Created A Folder", createdByEmailId,  databaseType);
			
			// Note : parentFolderId is set to 0 as these are all root level folders in this project.
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_folders (project_id,name,description,parent_folder_id," +
					" folder_level, folder_order, " + 
					"folder_path, requirement_type_id, created_by,created_dt,last_modified_by,last_modified_dt)" +
					 " values (?,?,?,0,1,?,?,?,'system',now(),'system',now()) ";
			}
			else {
				sql = " insert into gr_folders (project_id,name,description,parent_folder_id," +
				" folder_level, folder_order, " + 
				"folder_path, requirement_type_id, created_by,created_dt,last_modified_by,last_modified_dt)" +
				 " values (?,?,?,0,1,?,?,?,'system',sysdate,'system',sysdate) ";
			}
			prepStmt = con.prepareStatement(sql);
			
			// Create the folder 
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, folderName);
			prepStmt.setString(3, "Default folder used to store" + folderName);
			prepStmt.setInt(4, folderOrder);
			prepStmt.setString(5, folderName);
			prepStmt.setInt(6,requirementTypeId);
			prepStmt.execute();
			prepStmt.close();
	
			sql = "select id from gr_folders where project_id = ? and parent_folder_id = 0 and name = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, folderName);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				folderIdReturned = rs.getInt("id");
			}
			
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
		return folderIdReturned;
	}

	// returns 1 is the rtShortName does not exist in this project. else 1.
	 public static int isUniqueRequirementType(int projectId, String requirementTypeShortName , String requirementTypeName){
		System.out.println("srt req Type Prefix " +requirementTypeShortName + " req Type Name " + requirementTypeName );
		 int unique = 1;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			// 	TODO : first see if the RequirementType Prefix is already used. If so we need to return a warning.
			String sql = "select count(*) \"matches\" from gr_requirement_types where project_id = ? and short_name = ?";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, requirementTypeShortName);
			rs = prepStmt.executeQuery();
			int matches = 0;
			if (rs.next()){
				matches = rs.getInt("matches");
				if (matches > 0 ){
					unique = 0;
				}
			}
			prepStmt.close();
			rs.close();
			
			sql = "select count(*) \"matches\" from gr_requirement_types where project_id = ? and name = ?";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, requirementTypeName);
			rs = prepStmt.executeQuery();
			
			
			if (rs.next()){
				matches = rs.getInt("matches");
				if (matches > 0 ){
					unique = 0;
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
		return unique;
	}

	 
		// returns true if a Requirement type of this name exists in this project.
	 public static boolean requirementTypeExits(int projectId, String requirementTypeName){
		boolean exists = false; 
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			// see if this req type exists in this project.
			String sql = "select count(*) \"matches\" " +
				" from gr_requirement_types " +
				" where project_id = ? " +
				" and lower(name) =  ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, requirementTypeName.toLowerCase());
			rs = prepStmt.executeQuery();
			int matches = 0;
			if (rs.next()){
				matches = rs.getInt("matches");
				if (matches == 1 ){
					exists = true;
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
		return exists;
	}
	
	 
	// returns the number of requirements of a type .
	 public static int getNumOfRequirements(int requirementTypeId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		int numOfRequirements = 0;
		try {
					
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			// 	TODO : first see if the RequirementType Prefix is already used. If so we need to return a warning.
			String sql = "select count(*) \"numOfRequirements\" from gr_requirements  where requirement_type_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			
			rs = prepStmt.executeQuery();
			if (rs.next()){
				numOfRequirements = rs.getInt("numOfRequirements");
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
		return numOfRequirements;
	}

 
	 
	// this will permanently delete the req. i.e purge
	// This also removes the entry from the gr_search table.
	public static void purgeRequirement(int requirementId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			Requirement requirement = new Requirement(requirementId,  databaseType);
		
			// first lets iterate through all the attachments and drop them.
			ArrayList attachments = requirement.getRequirementAttachments( databaseType);
			Iterator a = attachments.iterator();
			while (a.hasNext()) {
				RequirementAttachment attachment = (RequirementAttachment) a.next();
				File file = new File(attachment.getFilePath());
				if (file != null){
					File dir = file.getParentFile();
					// lets drop the file.
					file.delete();
					
					if (dir != null) {
						dir.delete();
					}
				}
				
			}
			
			// now lets delete all requirement attachment entries in the db.
			String sql = " delete from gr_requirement_attachments where requirement_id = ? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.execute();

			
			//First delete all attribute values for this req.
			sql = " delete from gr_r_attribute_values where requirement_id = ? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.execute();

			
			// Traces
			// this is a little tricky. if we run updateTraceInfo for all the related req and then 
			// do the purge of this req, then the traceTo / TraceFrom will still have ref to this req
			// if we do the purge of this req first and then run the update TraceInfo , we won't 
			// have any related reqs (but they will continue to have this req in their traceto/tracefrom fields/
			// so we will 
			// a) first get all the related reqs, put them in an array list,
			// b) then purge this req
			// c) and then run the updateTraceInfo of the related reqs by iterating through the arraylist.
			
			ArrayList relatedReqs = new ArrayList();
			// lets get all the requirements this req trace to or from.
			sql = " 	select to_requirement_id \"target_requirement_id\" from gr_traces where from_requirement_id = ? "+
				"   union " +
				"	select from_requirement_id \"target_requirement_id\"  from gr_traces where to_requirement_id = ? " ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.setInt(2, requirementId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int targetRequirementId = rs.getInt("target_requirement_id");
				relatedReqs.add(new Integer(targetRequirementId));
			}
			prepStmt.close();
			
			// now that we have all the related reqs, lets drop this req in the gr_traces table
			//now drop all traces for this Req type. Both the from traces and to traces.
			sql = " delete from gr_traces where (from_requirement_id = ? or to_requirement_id = ?) ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.setInt(2, requirementId);
			prepStmt.execute();
			prepStmt.close();
			
			// now that we have lost the traces, lets iterate through the related reqs and 
			// and reset the traces.
			Iterator r = relatedReqs.iterator();
			while (r.hasNext()){
				Integer relatedRequirementIdInteger = (Integer) r.next();
				int relatedRequirementId = relatedRequirementIdInteger.intValue();
				RequirementUtil.updateTraceInfoForRequirement(relatedRequirementId);
			}

			prepStmt.close();
			

			// now delete the reference in gr_search table.
			sql = " delete from gr_search where object_id = ? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.execute();

			
			// now delete the reference in gr_requriements_baselines table.
			sql = " delete from gr_requirement_baselines where requirement_id = ? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.execute();
			
			// now delete the reference in gr_requriements_version table.
			sql = " delete from gr_requirement_versions where requirement_id = ? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.execute();

			// now delete the reference in gr_requriement_approval_history table.
			sql = " delete from gr_requirement_approval_h where requirement_id = ? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.execute();
			
			// now delete the reference in gr_requriement_comments table.
			sql = " delete from gr_requirement_comments where requirement_id = ? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.execute();
			
			
			// Now delete the reference in gr_requirement_log table.
			sql = " delete from gr_requirement_log where requirement_id = ? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.execute();

			
			// Now delete the requirement itself.
			sql = " delete from gr_requirements where id = ? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
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

	 
	 
		// this will permanently delete the req. i.e purge
		// This also removes the entry from the gr_search table.
		public static void purgeRequirementExceptAttachments(int requirementId, String databaseType){
			PreparedStatement prepStmt = null;
			ResultSet rs = null;
			java.sql.Connection con =  null;
			try {
				
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();
				
				Requirement requirement = new Requirement(requirementId,  databaseType);
				
				// now lets delete all requirement attachment entries in the db.
				String sql = " delete from gr_requirement_attachments where requirement_id = ? ";				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirementId);
				prepStmt.execute();
				prepStmt.close();
				
				
				//First delete all attribute values for this req.
				sql = " delete from gr_r_attribute_values where requirement_id = ? ";				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirementId);
				prepStmt.execute();
				prepStmt.close();
				
				
				// Traces
				// this is a little tricky. if we run updateTraceInfo for all the related req and then 
				// do the purge of this req, then the traceTo / TraceFrom will still have ref to this req
				// if we do the purge of this req first and then run the update TraceInfo , we won't 
				// have any related reqs (but they will continue to have this req in their traceto/tracefrom fields/
				// so we will 
				// a) first get all the related reqs, put them in an array list,
				// b) then purge this req
				// c) and then run the updateTraceInfo of the related reqs by iterating through the arraylist.
				
				ArrayList relatedReqs = new ArrayList();
				// lets get all the requirements this req trace to or from.
				sql = " 	select to_requirement_id \"target_requirement_id\" from gr_traces where from_requirement_id = ? "+
					"   union " +
					"	select from_requirement_id \"target_requirement_id\"  from gr_traces where to_requirement_id = ? " ;
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirementId);
				prepStmt.setInt(2, requirementId);
				rs = prepStmt.executeQuery();
				while (rs.next()){
					int targetRequirementId = rs.getInt("target_requirement_id");
					relatedReqs.add(new Integer(targetRequirementId));
				}
				
				// now that we have all the related reqs, lets drop this req in the gr_traces table
				//now drop all traces for this Req type. Both the from traces and to traces.
				sql = " delete from gr_traces where (from_requirement_id = ? or to_requirement_id = ?) ";				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirementId);
				prepStmt.setInt(2, requirementId);
				prepStmt.execute();
				
				// now that we have lost the traces, lets iterate through the related reqs and 
				// and reset the traces.
				Iterator r = relatedReqs.iterator();
				while (r.hasNext()){
					Integer relatedRequirementIdInteger = (Integer) r.next();
					int relatedRequirementId = relatedRequirementIdInteger.intValue();
					RequirementUtil.updateTraceInfoForRequirement(relatedRequirementId);
				}

				prepStmt.close();
				

				// now delete the reference in gr_search table.
				sql = " delete from gr_search where object_id = ? ";				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirementId);
				prepStmt.execute();

				
				// now delete the reference in gr_requriements_baselines table.
				sql = " delete from gr_requirement_baselines where requirement_id = ? ";				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirementId);
				prepStmt.execute();
				
				// now delete the reference in gr_requriements_version table.
				sql = " delete from gr_requirement_versions where requirement_id = ? ";				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirementId);
				prepStmt.execute();

				// now delete the reference in gr_requriement_approval_history table.
				sql = " delete from gr_requirement_approval_h where requirement_id = ? ";				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirementId);
				prepStmt.execute();
				
				// now delete the reference in gr_requriement_comments table.
				sql = " delete from gr_requirement_comments where requirement_id = ? ";				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirementId);
				prepStmt.execute();
				
				
				// Now delete the reference in gr_requirement_log table.
				sql = " delete from gr_requirement_log where requirement_id = ? ";				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirementId);
				prepStmt.execute();

				
				// Now delete the requirement itself.
				sql = " delete from gr_requirements where id = ? ";				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirementId);
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

		
	

	// this will permanently delete the reqs. i.e purge
	// This also removes the entry from the gr_search table.
	// this is designed to be efficient when purging a lot of requirements in bulk. 
	//
	public static void purgeRequirementsInBulk(ArrayList requirementsToPurge){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			if (requirementsToPurge.size() == 0 ) {
				return;
			}
			String requirementString = "";
			Iterator r = requirementsToPurge.iterator();
			
			
			while (r.hasNext()){
				Integer RequirementId = (Integer) r.next();
				requirementString = requirementString + RequirementId.toString() + ",";
			}
			
			
			// let drop the last ,
			if (requirementString.contains(",")){
				requirementString = (String) requirementString.subSequence(0,requirementString.lastIndexOf(","));
			}
			
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			
			
			// lets iterate through all the requirements attachments and drop them.
			String sql = "select file_path " +
				" from gr_requirement_attachments a " +
				" where a.requirement_id in ("+ requirementString +")";
			prepStmt = con.prepareStatement(sql);
			
			
			
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				String attachmentFilePath = rs.getString("file_path");
				File file = new File(attachmentFilePath);
				if (file != null){
					File dir = file.getParentFile();
					// lets drop the file.
					file.delete();
					
					if (dir != null) {
						dir.delete();
					}
				}
			}
			
			// now lets delete all requirement attachment entries in the db.
			sql = " delete from gr_requirement_attachments " +
				" where requirement_id  in (" + requirementString + ")";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			
			
			
			//First delete all attribute values for this req.
			sql = " delete from gr_r_attribute_values where requirement_id in " +
			"	("+ requirementString + ")";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();


			
			
			
			
			// Traces
			// this is a little tricky. if we run updateTraceInfo for all the related req and then 
			// do the purge of this req, then the traceTo / TraceFrom will still have ref to this req
			// if we do the purge of this req first and then run the update TraceInfo , we won't 
			// have any related reqs (but they will continue to have this req in their traceto/tracefrom fields/
			// so we will 
			// a) first get all the related reqs, put them in an array list,
			// b) then purge this req
			// c) and then run the updateTraceInfo of the related reqs by iterating through the arraylist.
			
			ArrayList relatedReqs = new ArrayList();
			// lets get all the requirements this req trace to or from.
			sql = " 	select to_requirement_id \"target_requirement_id\" from gr_traces where from_requirement_id in " +
				"	("+ requirementString + ") " +
				"   union " +
				"	select from_requirement_id \"target_requirement_id\"  from gr_traces where to_requirement_id in " +
				"	("+ requirementString + ")";
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int targetRequirementId = rs.getInt("target_requirement_id");
				relatedReqs.add(new Integer(targetRequirementId));
			}
			
			// now that we have all the related reqs, lets drop this req in the gr_traces table
			//now drop all traces for this Req type. Both the from traces and to traces.
			sql = " delete from gr_traces where " +		
					" (" +
					" from_requirement_id in	("+ requirementString + ")" +	
					" or " +
					" to_requirement_id in 	("+ requirementString + ")" +
					" )";
							
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			
			// now that we have lost the traces, lets iterate through the related reqs and 
			// and reset the traces.
			Iterator rr = relatedReqs.iterator();
			while (rr.hasNext()){
				Integer relatedRequirementIdInteger = (Integer) rr.next();
				int relatedRequirementId = relatedRequirementIdInteger.intValue();
				RequirementUtil.updateTraceInfoForRequirement(relatedRequirementId);
			}

			prepStmt.close();
			
			
			
			// now delete the reference in gr_requriements_baselines table.
			sql = " delete from gr_requirement_baselines where requirement_id in " +
			"	("+ requirementString + ")";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			
			// now delete the reference in gr_requriements_version table.
			sql = " delete from gr_requirement_versions where requirement_id in " +
			"	("+ requirementString + ")";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();

			
			// now delete the reference in gr_requriement_approval_history table.
			sql = " delete from gr_requirement_approval_h where requirement_id in " +
			"	("+ requirementString + ")";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			
			// now delete the reference in gr_requriement_comments table.
			sql = " delete from gr_requirement_comments where requirement_id in " +
			"	("+ requirementString + ")";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			
			
			// Now delete the reference in gr_requirement_log table.
			sql = " delete from gr_requirement_log where requirement_id in " +
			"	("+ requirementString + ")";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();

			
			// Now delete the requirement itself.
			sql = " delete from gr_requirements where id in " +
			"	("+ requirementString + ")";				
			prepStmt = con.prepareStatement(sql);
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

	// this will soft delete the req so that it can be restored
	// Soft Delete will also remove all Trace To and Trace From references. i.e you soft Delete
	// you lose your traceability information
	// However, we take the TraceTo and TraceFrom field values and add them to the end of Req Text
	// so that when you restore, you know what traceability you had when you deleted them.

	public static void deleteRequirement(int requirementId, String actorEmailId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			Requirement requirement = new Requirement(requirementId,  databaseType);
		
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "update gr_requirements " +
					" set description = concat (ifnull(description,'') , '   --Trace From :' , " +
					" ifnull(trace_from,'') , '   --Trace To :' , ifnull(trace_to,'')) " +
					" where id = ? ";
			}
			else {
				sql = "update gr_requirements " +
				" set description = nvl(description,'') || '   --Trace From :' || nvl(trace_from,'')  ||  '   --Trace To :' || nvl(trace_to,'') " +
				" where id = ? ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.execute();
			
			

			// we also need to drop any traceTo and TraceFrom fields where this req tag exists.
			//  This is a little tricky. the traceTo/ TraceFrom can have values like either BR-4 
			// or BR-1,BR-2,BR-4,BR-5 or BR-1,BR-4,BR-5. If we want to delete BR-4, we have to 
			// handle scenarios like BR-4, and BR-4 (with and without ,)
			
			// Traces
			// this is a little tricky. if we run updateTraceInfo for all the related req and then 
			// do the purge of this req, then the traceTo / TraceFrom will still have ref to this req
			// if we do the purge of this req first and then run the update TraceInfo , we won't 
			// have any related reqs (but they will continue to have this req in their traceto/tracefrom fields/
			// so we will 
			// a) first get all the related reqs, put them in an array list,
			// b) then purge this req
			// c) and then run the updateTraceInfo of the related reqs by iterating through the arraylist.
			
			ArrayList relatedReqs = new ArrayList();
			// lets get all the requirements this req trace to or from.
			sql = " 	select to_requirement_id \"target_requirement_id\" from gr_traces where from_requirement_id = ? "+
				"   union " +
				"	select from_requirement_id \"target_requirement_id\"  from gr_traces where to_requirement_id = ? " ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.setInt(2, requirementId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int targetRequirementId = rs.getInt("target_requirement_id");
				relatedReqs.add(new Integer(targetRequirementId));
			}
			
			// now that we have all the related reqs, lets drop this req in the gr_traces table
			//now drop all traces for this Req type. Both the from traces and to traces.
			sql = " delete from gr_traces where (from_requirement_id = ? or to_requirement_id = ?) ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.setInt(2, requirementId);
			prepStmt.execute();
			
			// now that we have lost the traces, lets iterate through the related reqs and 
			// and reset the traces.
			Iterator r = relatedReqs.iterator();
			while (r.hasNext()){
				Integer relatedRequirementIdInteger = (Integer) r.next();
				int relatedRequirementId = relatedRequirementIdInteger.intValue();
				RequirementUtil.updateTraceInfoForRequirement(relatedRequirementId);
			}

			prepStmt.close();
			

			
			// Now delete the requirement itself. Also we will need to empty out the 'traceTo' and 'traceFrom' fields of this requirement
			// This is because, in the above steps, we have wiped out all traces to and from this req as part of delete proceedings
			sql = " update gr_requirements set deleted = 1, trace_to = '' , trace_from='' where id = ? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.execute();

			

			// log the requirement deletion event.
			RequirementUtil.createRequirementLog(requirementId, "Requirement Deleted", actorEmailId,  databaseType);
			
			

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

	// This method will restore a Deleted Req.
	// The design works on the basis  that the deleted req ,is just flagged as Deleted, so it doesn't 
	// need to be moved around
	// TODO : we can consider restoring the traces, based on the traceability info that has been
	// appended to the req description at time of deletion. 
	
	public static void restoreRequirement(int requirementId, String actorEmailId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			// Now restore the requirement .
			String sql = " update gr_requirements set deleted = 0 where id = ? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.execute();
			
			// log the requirement restore event.
			RequirementUtil.createRequirementLog(requirementId, "Requirement Restored", actorEmailId,  databaseType);
			
		
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

	// when called with a requirement type id, it returns an array list of requireemnts of this type.
	// depending on the filter (active, deleted, all), it returns either active reqs, deleted reqs or all reqs.
	public static ArrayList getAllRequirementsInRT(int requirementTypeId, String filter, String databaseType){
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			//
			// This sql gets the list of requirements of this type and puts them in the arrray list.
			// creates a requirement object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "";
			if (databaseType.equals("mySQL")){
				 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
					" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
					" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM gr_requirements r , gr_requirement_types rt , gr_folders f" +
					" where rt.id = ?  and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " ;
			}
			else {
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
				" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
				" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
				" r.approvers ," + 
				" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
				" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
				" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
				" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
				" FROM gr_requirements r , gr_requirement_types rt , gr_folders f" +
				" where rt.id = ?  and r.requirement_type_id = rt.id " +
				" and r.folder_id = f.id " ;
			}
			// Lets add custom sql so that we pick up only reqs of a certain status.
			if (filter.equals("active")){
				sql += " and r.deleted = 0  ";
			}
			
			if (filter.equals("deleted")){
				sql += " and r.deleted = 1  ";
			}
			
			if (filter.equals("all")){
				sql += "  ";
			}


			if (filter.equals("deleted")){
				sql += " order by r.deleted,  r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
			}else{
				sql += " order by r.deleted, f.folder_path, r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
			}
	
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				// we use the requirementTypeId that came in as a parameter.
				int folderId = rs.getInt("folder_id");
				int projectId = rs.getInt("project_id");
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
				//lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId,
					projectId, 
					requirementName, requirementDescription, requirementTag, requirementFullTag,
					version, approvedByAllDt, approvers ,
					requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
					requirementPctComplete, requirementExternalUrl , traceTo, traceFrom, 
					userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy, 
					requirementTypeName, createdDt);
		
				requirements.add(requirement);
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
		return (requirements);
	}
	
	public static ArrayList getAllRequirementsInRTOrderByFullTag(int requirementTypeId, String filter, String databaseType){
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			//
			// This sql gets the list of requirements of this type and puts them in the arrray list.
			// creates a requirement object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "";
			if (databaseType.equals("mySQL")){
				 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
					" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
					" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM gr_requirements r , gr_requirement_types rt , gr_folders f" +
					" where rt.id = ?  and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " ;
			}
			else {
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
				" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
				" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
				" r.approvers ," + 
				" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
				" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
				" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
				" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
				" FROM gr_requirements r , gr_requirement_types rt , gr_folders f" +
				" where rt.id = ?  and r.requirement_type_id = rt.id " +
				" and r.folder_id = f.id " ;
			}
			// Lets add custom sql so that we pick up only reqs of a certain status.
			if (filter.equals("active")){
				sql += " and r.deleted = 0  ";
			}
			
			if (filter.equals("deleted")){
				sql += " and r.deleted = 1  ";
			}
			
			if (filter.equals("all")){
				sql += "  ";
			}

			sql += " order by r.deleted, r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
			
	
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				// we use the requirementTypeId that came in as a parameter.
				int folderId = rs.getInt("folder_id");
				int projectId = rs.getInt("project_id");
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
				//lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId,
					projectId, 
					requirementName, requirementDescription, requirementTag, requirementFullTag,
					version, approvedByAllDt, approvers ,
					requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
					requirementPctComplete, requirementExternalUrl , traceTo, traceFrom, 
					userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy, 
					requirementTypeName, createdDt);
		
				requirements.add(requirement);
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
		return (requirements);
	}
	
	// when called with a requirement type id, it deletes the requirementType
	// Note : this first deletes all Folders associated with this requirement type and then deletes the Requirement Type itself.
	public static void deleteRequirementType(int projectId, int requirementTypeId, 
			String actorEmailId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			// lets get the requirement type name to be deleted.
			String sql = " select name from gr_requirement_types where id =? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			rs = prepStmt.executeQuery();
			String requirementTypeName = "";
			while (rs.next()){
				requirementTypeName = rs.getString("name");
			}
			
			// Now lets log the deletion effort.
			ProjectUtil.createProjectLog(projectId, requirementTypeName, "Delete",
					"Deleting a Requirement Type", actorEmailId,  databaseType);
			
			
			// First delete all roles privs for all the folders associated to this requirement type.
			sql = " delete from gr_role_privs where folder_id in " +
					"(select id from gr_folders where requirement_type_id = ?) ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			prepStmt.execute();
			
			// now drop all reports in the folders associated to this req type.
			sql = " delete from gr_reports where folder_id in " +
			"(select id from gr_folders where requirement_type_id = ?) ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			prepStmt.execute();
			
			
			//First delete all folders associated to this Req type.
			sql = " delete from gr_folders where requirement_type_id = ? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			prepStmt.execute();

			//Now delete all RTBaselines associated to this Req type.
			sql = " delete from gr_rt_baselines where requirement_type_id = ? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			prepStmt.execute();

			//now drop all attributes for this Req type.
			sql = " delete from gr_rt_attributes where requirement_type_id = ? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			prepStmt.execute();
			
			// now drop the requirement seq value from the gr_requirements_seq table
			sql = " delete from gr_requirements_seq where requirement_type_id = ? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			prepStmt.execute();
			
			// Now delete the requirement type itself.
			sql = " delete from gr_requirement_types where id = ? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
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
	
	public static void resetRequirementTypeSeq(RequirementType reqType, int projectId, int requirementTypeId, 
			String actorEmailId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			// lets get the requirement type name to be deleted.
			String sql = " select count(*) 'noOfRequirements' from gr_requirements where requirement_type_id =? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			rs = prepStmt.executeQuery();
			int noOfRequirements  = 0;
			while (rs.next()){
				noOfRequirements = rs.getInt("noOfRequirements");
			}
		
			rs.close();
			prepStmt.close();
			
			if (noOfRequirements == 0 ){
				// lets reset the sequence number
			
				ProjectUtil.createProjectLog(projectId, reqType.getRequirementTypeName(), "Reset Sequence Number",
						"Resetting the starting requirement number ", actorEmailId,  databaseType);
				sql = "update gr_requirements_seq set tag = 0 where requirement_type_id = ?  ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirementTypeId);
				prepStmt.execute();
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
	


	// returns true if the baselineName does not exist in this requirement type. else false
	 public static boolean isUniqueBaseline(int requirementTypeId, String baselineName){
		boolean isUnique = true;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
					
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			// 	TODO : first see if the RequirementType Prefix is already used. If so we need to return a warning.
			String sql = "select count(*) \"matches\" " +
				" from gr_rt_baselines " +
				" where requirement_type_id = ? and name = ?";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			prepStmt.setString(2, baselineName);
			rs = prepStmt.executeQuery();
			int matches = 0;
			if (rs.next()){
				matches = rs.getInt("matches");
			}
			if (matches > 0 ){
				isUnique = false;
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
		return isUnique;
	}
	
	 
	 
	// when called with a requirement type id, it returns an array list of baselines in this Requirement Type
	public static ArrayList getAllBaselines(int requirementTypeId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		ArrayList baselines = new ArrayList();
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			//
			// This sql gets the list of baselines of this type and puts them in the arrray list.
			// creates a baseline object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "select id, requirement_type_id, name,locked, description, " +
				" created_by, created_dt, last_modified_by , last_modified_dt " + 
				" from gr_rt_baselines " +
				" where requirement_type_id = ? " +
				" order by name ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
		
			rs = prepStmt.executeQuery();
				while (rs.next()){
				int baselineId = rs.getInt("id");
				// we will use the requirementTypeId that came in as parameter.
				String baselineName = rs.getString("name");
				int locked = rs.getInt("locked");
				String baselineDescription = rs.getString ("description");
				String createdBy = rs.getString("created_by");
				//this.createdDt = rs.getDate("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by") ;
				//this.lastModifiedDt = rs.getDate("last_modified_by");
				
						
				RTBaseline rTBaseline = new RTBaseline(baselineId, requirementTypeId, baselineName,  locked,
					baselineDescription,  createdBy, lastModifiedBy);
		
				baselines.add(rTBaseline);
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
		return (baselines);
	}
	 

	public static ArrayList getAllAttachmentsInProject(int projectId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		ArrayList attachments = new ArrayList();
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			
			String sql = "select ra.id, ra.requirement_id, ra.file_name, ra.file_path, ra.title, ra.created_by, ra.created_dt " +  
				" from gr_requirement_attachments ra, gr_requirements r " +
				" where ra.requirement_id = r.id " +
				" and r.project_id = ? " +
				" order by ra.file_name ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
		
			rs = prepStmt.executeQuery();
				while (rs.next()){
				int requirementAttachmentId = rs.getInt("id");
				int requirementId = rs.getInt("requirement_id");
				String fileName = rs.getString("file_name");
				String filePath = rs.getString("file_path");
				
				String title = rs.getString ("title");
				String createdBy = rs.getString("created_by");
				String createdDt = rs.getString("created_dt");
				
						
				RequirementAttachment requirementAttachment = new RequirementAttachment(requirementAttachmentId, requirementId,
						fileName, filePath, 
						title, createdBy,  createdDt);
		
						
				attachments.add(requirementAttachment);
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
		return (attachments);
	}
	 
	
	// when called with a projectId, it returns an array list of releases in this project
	public static ArrayList getAllReleasesInProject(int projectId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		ArrayList releases = new ArrayList();
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			//
			// This sql gets the list of releases in a  project and puts them in an arraylist.
			
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
					" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
					" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM gr_requirements r , gr_requirement_types rt , gr_folders f" +
					" where rt.project_id = ?  " +
					" and rt.short_name = 'REL' " +
					" and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.deleted = 0 " +
					" order by substring_index(r.tag,'.',2)+0 , r.full_tag " ;
			}
			else {
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
				" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
				" r.version, to_date(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
				" r.approvers ," + 
				" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
				" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
				" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
				" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
				" FROM gr_requirements r , gr_requirement_types rt , gr_folders f" +
				" where rt.project_id = ?  " +
				" and rt.short_name = 'REL' " +
				" and r.requirement_type_id = rt.id " +
				" and r.folder_id = f.id " +
				" and r.deleted = 0 " +
				" order by substr(r.tag, 1,instr(r.tag, '.',1, 2)-1)+0 , r.full_tag " ;
			
			}
	
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				int folderId = rs.getInt("folder_id");
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
				//lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId,
					projectId,
					requirementName, requirementDescription, requirementTag, requirementFullTag,
					version, approvedByAllDt, approvers ,
					requirementStatus, requirementPriority, requirementOwner,requirementLockedBy,
					requirementPctComplete, requirementExternalUrl , traceTo, traceFrom, 
					userDefinedAttributes,testingStatus, deleted, folderPath, createdBy, lastModifiedBy, 
					requirementTypeName, createdDt);			
			
				releases.add(requirement);
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
		return (releases);
	}
	 

	 
	// returns 1 if the attributeName does not exist in this requirement type. else 1.
	 public static int isUniqueAttribute(int requirementTypeId, String attributeName){
		int status = 0;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
					
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			// 	TODO : first see if the RequirementType Prefix is already used. If so we need to return a warning.
			String sql = "select count(*) \"matches\" from gr_rt_attributes where requirement_type_id = ? and name = ?";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			prepStmt.setString(2, attributeName);
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
		return status;
	}
	

	// when called with a requirement type id, it returns an array list of attributes in this Requirement Type
	public static ArrayList<RTAttribute> getAllAttributes(int requirementTypeId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		ArrayList attributes = new ArrayList();
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			//
			// This sql gets the list of attributes of this type and puts them in the arrray list.
			// creates a attribute object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "select id, parent_attribute_id, system_attribute, requirement_type_id, name, description, type , " +
				" options , required , default_value , sort_order, " +
				" impacts_version, impacts_traceability, impacts_approval_workflow, " +
				" created_by, created_dt, last_modified_by , last_modified_dt " + 
				" from gr_rt_attributes " +
				" where requirement_type_id = ? " +
				" order by sort_order ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
		
			rs = prepStmt.executeQuery();
				while (rs.next()){
				int attributeId = rs.getInt("id");
				int parentAttributeId = rs.getInt("parent_attribute_id");
				int systemAttribute = rs.getInt("system_attribute");
				// we will use the requirementTypeId that came in as parameter.
				String attributeName = rs.getString("name");
				String attributeDescription = rs.getString ("description");
				String attributeType = rs.getString("type");
				String attributeDropDownOptions = rs.getString("options");
				int attributeRequired = rs.getInt("required"); 
				String attributeDefaultValue = rs.getString("default_value");
				String attributeSortOrder = rs.getString("sort_order");
				
				int attributeImpactsVersion  = rs.getInt("impacts_version"); 
				int attributeImpactsTraceability = rs.getInt("impacts_traceability");
				int attributeImpactsApprovalWorkflow = rs.getInt("impacts_approval_workflow");

				
				String createdBy = rs.getString("created_by");
				//this.createdDt = rs.getDate("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by") ;
				//this.lastModifiedDt = rs.getDate("last_modified_by");
				
			
				RTAttribute rTAttribute = new RTAttribute(attributeId, parentAttributeId, systemAttribute, requirementTypeId, attributeName,  
						attributeType,
						attributeRequired, attributeDefaultValue, attributeDropDownOptions,    
						attributeDescription, attributeSortOrder, 
						attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
						createdBy, lastModifiedBy);
		
				attributes.add(rTAttribute);
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
		return (attributes);
	}
	
	
	// Takes a rTAttribute id as a param, and deletes it in the db. Also, prior to deletion, 
	// it removes all attribute values in any requirement.
	// Since the the attribute values of the req are changing, we will also 
	// run a proc to reset the userDefinedAttribute value for this req.
	public static void deleteRTAttribute(int projectId, int rTAttributeId,
			String actorEmailId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			

			// lets get the attribute name to be deleted.
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " select concat(rt.name , ' --  ' , rta.name) name " +
					" from gr_rt_attributes rta, gr_requirement_types rt " +
					" where rta.id = ? and rta.requirement_type_id = rt.id ";
			}
			else {
				sql = " select rt.name || ' --  ' || rta.name \"name\" " +
				" from gr_rt_attributes rta, gr_requirement_types rt " +
				" where rta.id = ? and rta.requirement_type_id = rt.id ";
			}
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTAttributeId);
			rs = prepStmt.executeQuery();
			String rTAttributeName = "";
			while (rs.next()){
				rTAttributeName = rs.getString("name");
			}
			
			ProjectUtil.createProjectLog(projectId, rTAttributeName, "Delete",
					"Deleted Requirement Attribute" + rTAttributeName , actorEmailId,  databaseType);
			// first delete all the attribute values for this attribute id.
			sql = " delete from gr_r_attribute_values where attribute_id = ? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTAttributeId);
			prepStmt.execute();

			//Get the Req type for this attribute.
			sql = "select requirement_type_id from gr_rt_attributes where id = ?";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTAttributeId);
	
			rs = prepStmt.executeQuery();
			
			int requirementTypeId = 0;
			while (rs.next()){
				requirementTypeId = rs.getInt("requirement_type_id");
			}
			// call the method that resets the userDefinedAttribs for all reqs using the 
			//deleted attrib
			RequirementUtil.setUserDefinedAttributesForAllRequirementsInRT(requirementTypeId,  databaseType);
				
			// 	Now delete the attribute from the requirement type.
			sql = " delete from gr_rt_attributes where id = ?";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTAttributeId);
			prepStmt.execute();
			
				
			prepStmt.close();
			rs.close();
			con.close();
			}
			catch (Exception e) {
				// 	TODO Auto-generated catch block
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
	
	
	// permanently purges all the previously deleted requirements in a requirement type.
	public static void purgeAllDeletedRequirementsInRequirementType(int projectId, int requirementTypeId,
			String actorEmailId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			String sql = " select id from gr_requirements " +
					" where project_id = ? " +
					" and requirement_type_id = ? " +
					" and deleted = 1";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, requirementTypeId);
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				purgeRequirement(rs.getInt("id"),  databaseType);
			}
			rs.close();
			prepStmt.close();
			
			RequirementType requirementType = new RequirementType(requirementTypeId);
			ProjectUtil.createProjectLog(projectId, requirementType.getRequirementTypeName() , "Purge All Deleted Requirements",
					"Purged all deleted Requirements in this Requirement Type" + requirementType.getRequirementTypeName() ,
					actorEmailId,  databaseType);
			prepStmt.close();
			con.close();
			}
			catch (Exception e) {
				// 	TODO Auto-generated catch block
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

	// returns true if the user is a member of this project.
	public static boolean  isValidUserInProject(String emailId, Project project){
		boolean isValid = false;
		
		ArrayList members = project.getMembers();
		Iterator i = members.iterator();
		while (i.hasNext()){
			User member = (User) i.next();
			if (member.getEmailId().equals(emailId)) {
				isValid = true;
			}
		}
		return isValid;
	}
	 
	// returns true is this folder path is valid in this requirement type
	 public static boolean isValidFolderPathForRequirementType(String folderPath, int requirementTypeId){
		boolean isValid = false;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
				
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			// Now, lets see if this email id exists in the system.
			String sql = "select count(*) \"matches\" " +
			" from gr_folders   " +
			" where requirement_type_id = ? " + 
			" and lower(folder_path) = ?  ";
						
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			prepStmt.setString(2, folderPath.trim().toLowerCase());
			rs = prepStmt.executeQuery();
			int folderPathMatches = 0;
			if (rs.next()){
				folderPathMatches = rs.getInt("matches");
			}
			if (folderPathMatches == 0 ){
				// this means , no valid folder path in this req type
				isValid= false;
			}
			else {
				// this means, an valid folder path exists in this role 
				isValid = true;
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


		
		// returns 1 if the user exists in the system, 0 otherwise.
	 public static int doesUserExistInRole(int roleId, String emailId){
		int status = 0;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
				
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			// Now, lets see if this email id exists in the system.
			String sql = "select count(*) \"matches\" " +
			" from gr_user_roles ur, gr_users u  " +
			" where ur.user_id = u.id " + 
			" and ur.role_id = ? and u.email_id = ? ";
						
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, roleId);
			prepStmt.setString(2, emailId);
			rs = prepStmt.executeQuery();
			int emailIdRoleMatches = 0;
			if (rs.next()){
				emailIdRoleMatches = rs.getInt("matches");
			}
			if (emailIdRoleMatches == 0 ){
				// this means , no such email Id in this role
				status = 0;
			}
			else {
				// this means, an email id exists in this role 
				status = 1;
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
			return status;
		}		


	 public static int getAdministratorRoleId(int projectId){
		int administratorRoleId= 0;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
				
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "select id from gr_roles " +
					" where project_id = ? " +
					" and name = 'Administrator' " ;						
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();
			if (rs.next()){
				administratorRoleId = rs.getInt("id");
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
			return administratorRoleId;
		}		


	// Any time a custom attribute is created, we want to give the admin the default update this attribute 
	// in all folders privilege.
	// To do this
	// 1. Itereate and find all folders in this req type
	// 2. for each folder, get the updateable attributes value for the Administrator role
	// 3. to each folder, for admin role, give update permissions on this new attribute.
	
	 public static void giveRoleUpdateCustomAttributesPrivilege(int roleId, int requirementTypeId,
			 String customAttributeName, String databaseType){
		PreparedStatement prepStmt = null;
		java.sql.Connection con =  null;
		ResultSet rs = null;
		
		try {
				
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			// lets get all role_priv enties for a role and requirement type combination.
			String sql = "select rp.id, rp.update_attributes " +
					" from gr_role_privs rp, gr_folders f" +
					" where rp.folder_id = f.id " +
					" and rp.role_id =  ? " +
					" and f.requirement_type_id = ? ";
						
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, roleId);
			prepStmt.setInt(2, requirementTypeId);
			
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int rolePrivId = rs.getInt("id");
				String updateAttributes = rs.getString("update_attributes");
				
				System.out.println("srt about to update role priv id " + rolePrivId + " update_attibutes "  + updateAttributes);
				
				updateAttributes = updateAttributes  + ":#:" + customAttributeName + ":#:";
				
				// occassionally we end up with 2 :#: . so we change it back to one.
				if (updateAttributes.contains(":#::#:")){
					updateAttributes = updateAttributes.replace(":#::#:", ":#:");
				}
				System.out.println("srt about to update role priv id " + rolePrivId + " update_attibutes "  + updateAttributes);
				
				String sql2 = "update gr_role_privs " +
					" set update_attributes = ? " +
					" where id = ? ";
				PreparedStatement prepStmt2 = con.prepareStatement(sql2);
				prepStmt2.setString(1, updateAttributes);
				prepStmt2.setInt(2, rolePrivId);
				prepStmt2.execute();
				prepStmt2.close();
				
				
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
				if (con != null) {
					try {con.close();} catch (Exception e) {}
					con = null;
				}
				if (rs != null) { 
					try {rs.close();} catch (Exception e) {}
				} 
			}
			return ;
		}		

	 
	 // this method creates an entry in the project log table.
	 public static void createProjectLog(int projectId, String artifactName, String actionType,
		 String description, String actorEmailId, String databaseType){
		 
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
					
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "";
			// Now, lets see if this email id exists in the system.
			if (databaseType.equals("mySQL")){
				sql = " Insert into gr_project_log (project_id, artifact_name, action_type," +
					" description, actor_email_id, action_dt)" +
					"values (?, ?, ? , ? , ? ,now()) ";
			}
			else {
				sql = " Insert into gr_project_log (project_id, artifact_name, action_type," +
				" description, actor_email_id, action_dt)" +
				"values (?, ?, ? , ? , ? ,sysdate) ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, artifactName);
			prepStmt.setString(3, actionType);
			prepStmt.setString(4, description);
			prepStmt.setString(5, actorEmailId);
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

	 
		public static ArrayList getWebForms(int projectId){

			ArrayList webforms = new ArrayList();
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
				String sql = "select id, project_id, folder_id, name, description, introduction, default_owner, notify_on_creation, " +
						" submit_for_approval_on_creation, access_code " +
						" from gr_webforms where project_id = ?  ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				rs = prepStmt.executeQuery();
				
				while (rs.next()){
					// we already have project Id
					
					int id = rs.getInt("id");
					projectId = rs.getInt("project_id");
					int folderId = rs.getInt("folder_id");
					String name = rs.getString("name");
					String description = rs.getString("description");
					String introduction = rs.getString("introduction");
					String defaultOwner = rs.getString("default_owner");
					String notifyOnCreation = rs.getString("notify_on_creation");
					int submitForApprovalOnCreation  = rs.getInt("submit_for_approval_on_creation");
					String accessCode = rs.getString("access_code");
					
					WebForm webform = new WebForm(id, projectId, folderId, name, description, introduction, defaultOwner,
							 notifyOnCreation, submitForApprovalOnCreation, accessCode);
					webforms.add(webform);
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

			return (webforms);
		}
		

	 
		// when called with a projectId, it returns an array list of Strings containing the project change logs.
		public static ArrayList getProjectChangeLog(int projectId, String databaseType){
			
			ArrayList projectChangeLog = new ArrayList();
			PreparedStatement prepStmt = null;
			ResultSet rs = null;
			java.sql.Connection con =  null;
			try {
			
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();
				
				//
				// This sql gets the list changelogs for this project. 
				// it then creates a string objects and adds it to the arraylist.
				// 
				//
				String sql = "";
				if (databaseType.equals("mySQL")){
					sql = "select concat(action_dt, ':##:',actor_email_id,':##:',artifact_name, ':##:', " +
						" action_type, ':##:', description) \"log\" " + 
						" from gr_project_log " + 
						" where project_id = ? " +
						" order by action_dt desc ";
				}
				else {
					sql = "select action_dt || ':##:' || actor_email_id || ':##:' || artifact_name ||  ':##:' || " +
					" action_type || ':##:' || description \"log\" " + 
					" from gr_project_log " + 
					" where project_id = ? " +
					" order by action_dt desc ";
				}
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
			
				rs = prepStmt.executeQuery();
					while (rs.next()){
					String log = rs.getString("log");
					projectChangeLog.add(log);
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
			return (projectChangeLog);
		}

		public static ArrayList getProjectCompleteChangeLog
			(int projectId, String databaseType, int changeFolderId, String actorEmailId, int changedSince, String changeType, 
			String sortBy ){
			
			ArrayList projectChangeLog = new ArrayList();
			PreparedStatement prepStmt = null;
			ResultSet rs = null;
			java.sql.Connection con =  null;
			try {
			
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();
				
				//
				// This sql gets the list changelogs for this project. 
				// it then creates a string objects and adds it to the arraylist.
				// 
				//
				String sql = "";
				String changeFolderSQL = "";
				String eliminateSQL = "";
				if (changeFolderId > 0){
					changeFolderSQL = " and r.folder_id = " + changeFolderId;
					eliminateSQL = " and 1=2 ";
				}
				
				
				String bigWhere = "";
				if (!(actorEmailId.equals("all"))){
					bigWhere =  " where  actor_email_id = '" + actorEmailId + "' " ;
				}
				
				
				
				

				
				if (!changeType.equals("all")){
					
					if (changeType.equals("Comments")){
						if (bigWhere.equals("")){
							bigWhere = " where  log_type = 'Comment'  ";
						}
						else {
							bigWhere += " and   log_type = 'Comment'  " ;
						}
					}
					
					if (changeType.equals("Traceability")){
						if (bigWhere.equals("")){
							bigWhere = " where   description like '%Trace%'  ";
						}
						else {
							bigWhere += " and    description like '%Trace%'" ;
						}
					}
					
					if (changeType.equals("UpdateAttributes")){
						if (bigWhere.equals("")){
							bigWhere = " where   description like '%Updated attribute%'  ";
						}
						else {
							bigWhere += " and    description like '%Updated attribute%'" ;
						}
					}
					
					if (changeType.equals("CreatedRequirement")){
						if (bigWhere.equals("")){
							bigWhere = " where   description like '%Created Requirement ...%'  ";
						}
						else {
							bigWhere += " and    description like '%Created Requirement ...%'" ;
						}
					}
					
					
					if (changeType.equals("UpdatedName")){
						if (bigWhere.equals("")){
							bigWhere = " where   description like '%Updated Name%'  ";
						}
						else {
							bigWhere += " and    description like '%Updated Name%'" ;
						}
					}
					
					if (changeType.equals("UpdatedDescription")){
						if (bigWhere.equals("")){
							bigWhere = " where   description like '%Updated Description%'  ";
						}
						else {
							bigWhere += " and    description like '%Updated Description%'" ;
						}
					}
					
					if (changeType.equals("UpdatedAttributes")){
						if (bigWhere.equals("")){
							bigWhere = " where   description like '%Updated attribute%'  ";
						}
						else {
							bigWhere += " and    description like '%Updated attribute%'" ;
						}
					}
					
					if (changeType.equals("File")){
						if (bigWhere.equals("")){
							bigWhere = " where   (description like '%Attached file%' or description like '%Deleted file%')  ";
						}
						else {
							bigWhere += " and    (description like '%Attached file%' or description like '%Deleted file%')   " ;
						}
					}
					
					if (changeType.equals("Completion")){
						if (bigWhere.equals("")){
							bigWhere = " where   description like '%Setting Requirement Completed%'  ";
						}
						else {
							bigWhere += " and    description like '%Setting Requirement Completed%'" ;
						}
					}
					
					if (changeType.equals("Owner")){
						if (bigWhere.equals("")){
							bigWhere = " where   description like '%Setting Requirement Owner to%'  ";
						}
						else {
							bigWhere += " and    description like '%Setting Requirement Owner to%'" ;
						}
					}
					
					if (changeType.equals("Deleted")){
						if (bigWhere.equals("")){
							bigWhere = " where   description like '%Requirement Deleted%'  ";
						}
						else {
							bigWhere += " and    description like '%Requirement Deleted%'" ;
						}
					}
					
					if (changeType.equals("Restored")){
						if (bigWhere.equals("")){
							bigWhere = " where   description like '%Requirement Restored%'  ";
						}
						else {
							bigWhere += " and    description like '%Requirement Restored%'" ;
						}
					}
					
					if (changeType.equals("Moved")){
						if (bigWhere.equals("")){
							bigWhere = " where   description like '%Moved Requirement to%'  ";
						}
						else {
							bigWhere += " and    description like '%Moved Requirement to%'" ;
						}
					}
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					if (changeType.equals("Approval")){
						if (bigWhere.equals("")){
							bigWhere = " where   ( description like '%Submitting Requirement for Approval%' or description like '%Approved Version%' or description like '%Rejected Version%'  or description like '%has finally been Approved%' or description like '%has finally been Rejected%')   ";
						}
						else {
							bigWhere += " and    ( description like '%Submitting Requirement for Approval%' or description like '%Approved Version%' or description like '%Rejected Version%'  or description like '%has finally been Approved%' or description like '%has finally been Rejected%') " ;
						}
					}
				}
				if (databaseType.equals("mySQL")){
					sql = " select * from ( " +
									" select " + 
									" project_id 'project_id', 0 'folder_id' , 0 'requirement_id', \"Project\" as 'log_type', \"NA\" as 'full_tag', " + 
									" actor_email_id, action_dt, concat (description, \" : \" , artifact_name) 'description' " +
									" from gr_project_log " +
									" where project_id = " + projectId +
									" and datediff(curdate() , action_dt) <   " + changedSince + 
									eliminateSQL + 
							" 	union " +
									" select  r.project_id , r.folder_id , r.id 'requirement_id', \"Requirement Change\"  as 'log_type', r.full_tag , " + 
									" rl.actor_email_id, rl.action_dt, rl.description " +
									" from gr_requirement_log rl, gr_requirements r " +
									" where r.project_id = " + projectId +
		 							" and rl.requirement_id  = r.id " +
		 							" and datediff(curdate() , rl.action_dt) <   " + changedSince + 
									changeFolderSQL + 
							" 	union " +
									" select r.project_id, r.folder_id, r.id 'requirement_id', \"Comment\"  as 'log_type',  r.full_tag, " + 
									" rc.commenter_email_id as 'actor_email_id', rc.comment_dt as 'action_dt' , rc.comment_note as 'description' " +
									" from gr_requirement_comments rc, gr_requirements r " +
									" where r.project_id = " + projectId +
									" and r.id= rc.requirement_id " +
									" and datediff(curdate() , rc.comment_dt) <   " + changedSince + 
									changeFolderSQL + 
							" ) total_log "   ;
					
					
					sql += bigWhere ;
					
					
					
					
					if (sortBy.equals("")){
						sql += " order by action_dt asc " +
							" limit 5000 ";
					}
					else {
						if (sortBy.equals("changedByUp")){
							sql += " order by actor_email_id asc , action_dt asc " +
								" limit 5000 ";
						}
						if (sortBy.equals("changedByDown")){
							sql += " order by actor_email_id desc , action_dt asc " +
								" limit 5000 ";
						}
						if (sortBy.equals("changedObjectUp")){
							sql += " order by full_tag asc , action_dt asc " +
								" limit 5000 ";
						}
						if (sortBy.equals("changedObjectDown")){
							sql += " order by full_tag desc , action_dt asc " +
								" limit 5000 ";
						}
						if (sortBy.equals("changedDtUp")){
							sql += " order by  action_dt asc " +
								" limit 5000 ";
						}
						if (sortBy.equals("changedDtDown")){
							sql += " order by  action_dt desc " +
								" limit 5000 ";
						}
					}
				}
				prepStmt = con.prepareStatement(sql);
				
				
				rs = prepStmt.executeQuery();
					while (rs.next()){
					int folderId = rs.getInt("folder_id");
					int requirementId = rs.getInt("requirement_id");
					String logType = rs.getString("log_type");
					String fullTag = rs.getString("full_tag");
					String tempActorEmailId  = rs.getString("actor_email_id");
					String actionDt = rs.getString("action_dt");
					String description = rs.getString("description");
					
					
					ChangeLog changeLog = new ChangeLog(projectId,folderId,requirementId,logType,fullTag,tempActorEmailId,actionDt,description);
					projectChangeLog.add(changeLog);
					
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
			return (projectChangeLog);
		}

		public static ArrayList getProjectActors(int projectId, String databaseType){
			
			ArrayList projectActors = new ArrayList();
			PreparedStatement prepStmt = null;
			ResultSet rs = null;
			java.sql.Connection con =  null;
			try {
			
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();
				
				//
				// This sql gets the list changelogs for this project. 
				// it then creates a string objects and adds it to the arraylist.
				// 
				//
				String sql = "";
				
				if (databaseType.equals("mySQL")){
					sql = " select distinct actor_email_id from ( " +
									" select " + 
									" distinct  actor_email_id " +
									" from gr_project_log " +
									" where project_id = " + projectId +
							" 	union " +
									" select distinct rl.actor_email_id " +
									" from gr_requirement_log rl, gr_requirements r " +
									" where r.project_id = " + projectId +
		 							" and rl.requirement_id  = r.id " +
							" 	union " +
									" select distinct  rc.commenter_email_id as 'actor_email_id' " +
									" from gr_requirement_comments rc, gr_requirements r " +
									" where r.project_id = " + projectId +
									" and r.id= rc.requirement_id " +
							" ) total_log " +
							" order by 1 desc";
				}
				prepStmt = con.prepareStatement(sql);
				
				
				rs = prepStmt.executeQuery();
					while (rs.next()){
					String actorEmailId  = rs.getString("actor_email_id");
					projectActors.add(actorEmailId);
					
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
			return (projectActors);
		}
		
		// takes a requirementId as input and returns the projectId and folder Id as an int array.
		public static String getProjectIdFolderIdForRequirement(int requirementId){
			String projectIdFolderId = "";
			PreparedStatement prepStmt = null;
			ResultSet rs = null;
			java.sql.Connection con =  null;
			try {
				
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();
				
				// Now, lets see if this email id exists in the system.
				String sql = " select project_id, folder_id from gr_requirements where id = ? ";
							
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirementId);
				rs = prepStmt.executeQuery();
				while (rs.next()){
					projectIdFolderId  = rs.getInt("project_id") + ":##:" + rs.getInt("folder_id"); 
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
			return projectIdFolderId;
		}

		// takes an objectId, request object and objectType as input and
		// returns a URL string to that object.
	public static String getURL(HttpServletRequest request, int objectId, String objectType){
		String objectURL = "";
		
		try {
			String serverName = request.getServerName();

			if (objectType.equals("requirement")){
       			objectURL = "https://" + serverName +  
       				"/GloreeJava2/servlet/DisplayAction?dO=req&dReqId=" + objectId ;
			}
			if (objectType.equals("report")){
       			objectURL = "https://" + serverName +  
       				"/GloreeJava2/servlet/DisplayAction?dO=rep&dRepId=" +	objectId ;
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}  
		return objectURL;
	}

	
	// returns a URL string to that object.
	public static HashMap<String,String> getHashMapUDA( String uda){
	String objectURL = "";
	
	HashMap<String,String> uDAMap = new HashMap<String,String>();
	if (uda == null )
		{
		return uDAMap;
		}
	try {
		String[] attribs = uda.split(":##:");
		for (int k=0; k<attribs.length; k++) {
			String[] attrib = attribs[k].split(":#:");	
			String label = attrib[0];
			String value = "";
			if (attrib.length > 1){
				value = attrib[1];
			}
			if (label == null) label = "";
			if (value == null ) value = "";
			if (!label.equals("")){
				uDAMap.put(label.trim(), value.trim())  ;
			}
			
		}
	}
	catch (Exception e) {
		e.printStackTrace();
	}  
	return uDAMap;
}
	// returns a URL to the requirement attachment
	public static String getRequirementAttachmentURL(HttpServletRequest request, int requirementId, int attachmentId){
		String objectURL = "";
		try {
			String serverName = request.getServerName();
			objectURL = "https://" + serverName +  
  				"/GloreeJava2/servlet/RequirementAction?action=downloadAttachment&attachmentId=" + attachmentId;
		}
		catch (Exception e) {
			e.printStackTrace();
		}  
		return objectURL;
	}


	// takes an attributeId and returns a string of permitted values in this attribute
	// for date, url, Text Box, a value of 'all' is returned. For drop downs, the option value is
	// returned.
	public static String getPermittedValuesInAttribute(int attributeId){
		String permittedValuesInAttribute = "";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			// Now, lets see if this email id exists in the system.
			String sql = " SELECT type, options FROM gr_rt_attributes  where id = ? ";
						
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, attributeId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				String type  = rs.getString("type");
				String options = rs.getString("options");
				
				if (type.equals("Drop Down")){
					permittedValuesInAttribute = options;
				}
				else {
					permittedValuesInAttribute = "all";
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
		
		
		return permittedValuesInAttribute;
	}



	// returns true if the testValue is permitted in the attribute, else false.
	public static boolean isPermittedValueInAttribute(int attributeId, String testValue){
		boolean isPermitted = false;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			// Now, lets see if this email id exists in the system.
			String sql = " SELECT parent_attribute_id, type, options FROM gr_rt_attributes  where id = ? ";
						
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, attributeId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				String type  = rs.getString("type");
				String options = rs.getString("options");
				int parentAttributeId = rs.getInt("parent_attribute_id");
				
				if (type.equals("Drop Down")){
					String [] optionsArray = null ;
					if (options.contains(",")){
						optionsArray = options.split(",");
					}
					else {
						optionsArray = new String[1];
						optionsArray[0] = options;
					}
					for (int i=0; i<optionsArray.length; i++) {
						String optionName = optionsArray[i].trim();;
						if (parentAttributeId > 0){
							// this is a child attribute and the attribute values are like Porsche:911,Porsche:Panamera,Porsche:Carrera
							// where Porsche is the parent attribute value and 911,Panamera and Carrera are the potential child values.
							// so we need to strip out the first portion .
							
							
							if ((optionName != null) && (optionName.contains(":"))){
								String [] oN = optionName.split(":");
								optionName = oN[1];
							}
						}
						
						if (optionName.equals(testValue.trim())){
							isPermitted = true;
						}
					}
				}
				
				else if (type.equals("Drop Down Multiple")){
					String inputValue  = testValue;
					System.out.println("srt checking validity of Drop down multiple");

					if ((inputValue == null) || (inputValue.equals(""))){
						return(true);
					}
					
					String [] optionsArray = null ;
					if (options.contains(",")){
						optionsArray = options.split(",");
					}
					else {
						optionsArray = new String[1];
						optionsArray[0] = options;
					}
					
					// testValue can be like Bus,Car
					// Options can be like [Bus,Truck,Ship,Car]
					// only if each of the test values exists in Options can this be considered a valid value
					// set isPermitted to be True. Split each of the testValues. If any of the 
					// testValues are not in the valid list, then set isPermitted ToFalse.
					isPermitted = true;
					String [] inputValuesArray = null ;
					if (inputValue.contains(",")){
						inputValuesArray = inputValue.split(",");
					}
					else {
						inputValuesArray = new String[1];
						inputValuesArray[0] = inputValue;
					}
					System.out.println("srt inputValuesString is " + inputValue);
					System.out.println("srt inputValuesArray is " + inputValuesArray);
					

					System.out.println("srt options is " + options);
					System.out.println("srt optionsArray is " + optionsArray);
					
					for (String input: inputValuesArray){
						boolean found = false;
						for (String option:optionsArray){
							if (option.equals(input)){
								found = true;
								System.out.println("srt input " + input + "  is found ");
							}
						}
						System.out.println("srt input " + input + "  found value is  " + found);
						if (!found){
							isPermitted = false;
						}
					}
					
				}
				else {
					isPermitted  = true;
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
		return isPermitted;
	}

	
	// returns the list of user objects that don't have an email id ending in the restricted domain.
	public static ArrayList getAtRiskUsers(int projectId, String restrictedDomains, String databaseType){
		
		ArrayList atRiskUsers = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			//
			// This sql gets the list changelogs for this project. 
			// it then creates a string objects and adds it to the arraylist.
			// 
			//
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "select u.id, u.ldap_user_id, u.first_name, u.last_name, u.email_id, u.pets_name, u.user_type, " +
					" date_format(u.account_expire_dt, '%d %M %Y ')  \"account_expire_dt\" , " +
					" ifnull(datediff(u.account_expire_dt, now()),0) \"days_left\" , " +
					" u.billing_organization_id, u.number_of_requirements " +
					"  , pref_rows_per_page, pref_hide_projects  " + 
					" from gr_roles r, gr_user_roles ur, gr_users u " + 
					" where r.project_id = ? " +
					" and r.id = ur.role_id " +
					" and ur.user_id = u.id " ;
			}
			else {
				sql = "select u.id, u.ldap_user_id, u.first_name, u.last_name, u.email_id, u.pets_name, u.user_type, " +
				" to_char(u.account_expire_dt, 'DD MON YYYY')  \"account_expire_dt\" , " +
				" nvl((u.account_expire_dt -  sysdate),0) \"days_left\" , " +
				" u.billing_organization_id, u.number_of_requirements " +
				"  , pref_rows_per_page, pref_hide_projects  " + 
				" from gr_roles r, gr_user_roles ur, gr_users u " + 
				" where r.project_id = ? " +
				" and r.id = ur.role_id " +
				" and ur.user_id = u.id " ;	
			}
			
			// the following code taks of when restrictedDomains is a single value (i.e. cisco.com) or 
			// multiple comma separated values. By the time we are done, domains will contain an array
			// of strings with domains in them.
			String [] domains = {restrictedDomains};
			if (restrictedDomains.contains(",")){
				domains = restrictedDomains.split(",");
			}
			
			for (int i=0 ; i < domains.length ; i++) {
				sql += " and u.email_id not like ('%"+   domains[i] +  "') ";
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
					atRiskUsers.add(user);
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
		return (atRiskUsers);
	}
	

	
	
	// This routine is used to convert a projec to the Agile model
	// it does 
	// 1. for every req type, it creates the Scrum attributes.
	// 2. it then refreshes the agileSprints attributes with all the sprints in the system.
	public static void   setUpAgileScrumAttributesInReqType(int projectId, int requirementTypeId, String updatedByEmailId,String databaseType){
		

		try {
				int parentAttributeId = 0;
				// for each requirement types, lets create the scrum attributes.
				// Agile Sprint - Drop down
				int isUnique = ProjectUtil.isUniqueAttribute(requirementTypeId, "Agile Sprint");
				if (isUnique == 1){
					// the attribute Agile Sprint is not taken. so we can create it.
					RTAttribute rTAttribute = new RTAttribute(projectId,parentAttributeId, 0, requirementTypeId, "Agile Sprint" , 
							"Drop Down" , "za",	0, "", 
							"", "Defines the Sprint this requirements is part of",
							0, 0, 0,
							updatedByEmailId, databaseType);
				}
				// Agile Task Weight - Number
				isUnique = ProjectUtil.isUniqueAttribute(requirementTypeId, "Agile Task Weight");
				if (isUnique == 1){
					// the attribute Agile Sprint is not taken. so we can create it.
					RTAttribute rTAttribute = new RTAttribute(projectId,parentAttributeId, 0, requirementTypeId, "Agile Task Weight" , 
							"Number" , "zb",	0, "", 
							"", "Defines the priority or weight of this requierment",
							0, 0, 0,
							updatedByEmailId, databaseType);
				}
				// Agile Task Weight - Number
				isUnique = ProjectUtil.isUniqueAttribute(requirementTypeId, "Agile Task Status");
				if (isUnique == 1){
					// the attribute Agile Sprint is not taken. so we can create it.
					RTAttribute rTAttribute = new RTAttribute(projectId,parentAttributeId, 0, requirementTypeId, "Agile Task Status" , 
							"Drop Down" , "zc",	0, "Not Started", 
							"Not Started,In Progress,Blocked,Completed", "Defines the status of this requirement",
							0, 0, 0,
							updatedByEmailId, databaseType);
				}
				// Agile Effort Remaining - Number
				isUnique = ProjectUtil.isUniqueAttribute(requirementTypeId, "Agile Total Effort (hrs)");
				if (isUnique == 1){
					// the attribute Agile Sprint is not taken. so we can create it.
					RTAttribute rTAttribute = new RTAttribute(projectId,parentAttributeId, 0, requirementTypeId, "Agile Total Effort (hrs)" , 
							"Number" , "zd",	0, "", 
							"", "Defines the original estimate in hours to complete this requirement",
							0, 0, 0,
							updatedByEmailId, databaseType);
				}
				
				// Agile Effort Remaining - Number
				isUnique = ProjectUtil.isUniqueAttribute(requirementTypeId, "Agile Effort Remaining (hrs)");
				if (isUnique == 1){
					// the attribute Agile Sprint is not taken. so we can create it.
					RTAttribute rTAttribute = new RTAttribute(projectId, parentAttributeId, 0, requirementTypeId, "Agile Effort Remaining (hrs)" , 
							"Number" , "zd",	0, "", 
							"", "Defines the remaining in hours to complete this requirement",
							0, 0, 0,
							updatedByEmailId, databaseType);
				}
				
				// lets refresh all the agile scrum sprints in this req type
				RequirementType requirementType = new RequirementType (requirementTypeId);
				requirementType.refreshAgileScrumSprints(databaseType);
				
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
		}
		return;
	}
	
	public static void   setUpEnableVotesAttributesInReqType(int projectId, int requirementTypeId, String updatedByEmailId,String databaseType){
		

		try {
				int parentAttributeId = 0;
				// for each requirement types, lets create the votes attributes.
				
				// Total Votes Cast - Number
				int isUnique = ProjectUtil.isUniqueAttribute(requirementTypeId, "Total Votes Cast");
				if (isUnique == 1){
					// the attribute Agile Sprint is not taken. so we can create it.
					RTAttribute rTAttribute = new RTAttribute(projectId,parentAttributeId, 1, requirementTypeId, "Total Votes Cast" , 
							"Number" , "a",	0, "", 
							"", "Sum of all the votes cast for this Requirement",
							0, 0, 0,
							updatedByEmailId, databaseType);
				}
				
				// lets refresh all the agile scrum sprints in this req type
				RequirementType requirementType = new RequirementType (requirementTypeId);
				
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
		}
		return;
	}
	
		
	
	// this routine is called to update project core info.
	// Prior to this call the calling routine must validate to ensure that there are no users
	// not satisfying the restrictedDomains
	// this routine ...
	// 1. checks if the new prefix is unique
	// 2. updates project core info in db.
	public static void updateProjectCoreInfo(Project project, String updatedByEmailId, String projectName,
			String shortName, 
			String projectDescription,
			String projectOwner, String projectWebsite, String projectOrganization, String projectTags, int enableAgileScrum,
			String powerUserSettings, int percentageCompleteDriverReqTypeId, String restrictedDomains, String databaseType){

		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets log the update.
			if (!(shortName.equals(project.getShortName()))){
				// project prefix changed.
				ProjectUtil.createProjectLog(project.getProjectId(), "Project Prefix", "Update",
					"New Prefix : " + shortName , updatedByEmailId,  databaseType);
			}
			if (!(projectName.equals(project.getProjectName()))){
				// project name changed.
				ProjectUtil.createProjectLog(project.getProjectId(), "Project Name", "Update",
					"New Name : " + projectName , updatedByEmailId,  databaseType);
			}
			if (!(projectDescription.equals(project.getProjectDescription()))){
				// project Description changed.
				ProjectUtil.createProjectLog(project.getProjectId(), "Project Description", "Update",
					"New Description : " + projectDescription , updatedByEmailId,  databaseType);
			}

			if (!(projectOwner.equals(project.getProjectOwner()))){
				// project name changed.
				ProjectUtil.createProjectLog(project.getProjectId(), "Project Owner", "Update",
					"New Owner : " + projectOwner , updatedByEmailId,  databaseType);
			}

			if (!(projectWebsite.equals(project.getProjectWebsite()))){
				// project name changed.
				ProjectUtil.createProjectLog(project.getProjectId(), "Project Website", "Update",
					"New Website : " + projectWebsite , updatedByEmailId,  databaseType);
			}

			if (!(projectOrganization.equals(project.getProjectOrganization()))){
				// project name changed.
				ProjectUtil.createProjectLog(project.getProjectId(), "Project Organization", "Update",
					"New Organization : " + projectOrganization , updatedByEmailId,  databaseType);
			}

			if (!(projectTags.equals(project.getProjectTags()))){
				// project name changed.
				ProjectUtil.createProjectLog(project.getProjectId(), "Project Tags", "Update",
					"New Tags : " + projectTags , updatedByEmailId,  databaseType);
			}
			
			if (!(restrictedDomains.equals(project.getRestrictedDomains()))){
				// project name changed.
				ProjectUtil.createProjectLog(project.getProjectId(), "Restricted Domains", "Update",
					"New Restricted Domains : " + restrictedDomains , updatedByEmailId,  databaseType);
			}
			
			

			if (percentageCompleteDriverReqTypeId != project.getPercentageCompletedDriverReqTypeId()){
				// driver req type has changed.
				RequirementType reqType = new RequirementType(percentageCompleteDriverReqTypeId);
				ProjectUtil.createProjectLog(project.getProjectId(), "Percentage Complete Driver", "Update",
					"New Driver for Percentage Complte is : " + reqType.getRequirementTypeName() , updatedByEmailId,  databaseType);
			}

			if (!(powerUserSettings.equals(project.getPowerUserSettings()))){
				// power use settings has changed.
				ProjectUtil.createProjectLog(project.getProjectId(), "Powe User Settings ", "Update",
					"New Power User Setting is : " + powerUserSettings , updatedByEmailId,  databaseType);
			}
			// Now lets  update project in the db.
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " update gr_projects " +
					" set short_name = ?, " +
					" name =  ? ," +
					" description = ? ," +
					" owner = ? , " +
					" website = ? , " +
					" organization = ? , " +
					" tags = ? , " +
					" enable_agile_scrum = ? , " +
					" pct_complete_driver_requirement_type_id = ? , " +
					" power_user_settings = ? , " +
					" restricted_domains = ? ," +
					" last_modified_by = ? , " +
					" last_modified_dt = now() " +
					" where id = ? ";
			}
			else {
				sql = " update gr_projects " +
				" set short_name = ?, " +
				" name =  ? ," +
				" description = ? ," +
				" owner = ? , " +
				" website = ? , " +
				" organization = ? , " +
				" tags = ? , " +
				" enable_agile_scrum = ? , " +
				" pct_complete_driver_requirement_type_id = ? ,  " +
				" power_user_settings = ? , " +
				" restricted_domains = ? ," +
				" last_modified_by = ? , " +
				" last_modified_dt = sysdate " +
				" where id = ? ";
			}
			
		 	prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, shortName);
			prepStmt.setString(2, projectName);
			prepStmt.setString(3, projectDescription);
			
			prepStmt.setString(4, projectOwner);
			prepStmt.setString(5, projectWebsite);
			prepStmt.setString(6, projectOrganization);
			prepStmt.setString(7, projectTags);
			prepStmt.setInt(8, enableAgileScrum);
			
			prepStmt.setInt(9, percentageCompleteDriverReqTypeId);
			
			prepStmt.setString(10, powerUserSettings);
			
			prepStmt.setString(11, restrictedDomains);
			prepStmt.setString(12, updatedByEmailId );
			prepStmt.setInt(13, project.getProjectId());
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
	public static void updateHidePriority(Project project,int hidePriority, String  updatedByEmailId, String  databaseType ){

		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets log the update.
			ProjectUtil.createProjectLog(project.getProjectId(), "Project Hide Priority", "Update",
				"New Hide Priority : " +  hidePriority , updatedByEmailId,  databaseType);
		
			

					String sql = "";
				sql = " update gr_projects " +
					" set hide_priority = ? " +
					" where id = ? ";
			
		 	prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, hidePriority);
			prepStmt.setInt(2, project.getProjectId());
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
	
	
	
	public static void updateHealthBarSettings(Project project,String showHealth, String  updatedByEmailId){
		System.out.println("SRT in updateHealthBarSettings function ");
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets log the update.
			ProjectUtil.createProjectLog(project.getProjectId(), "Project Hide Priority", "Update",
				"New healthBarSettings: " +  showHealth  , updatedByEmailId,  "mySQL");
		
			String hideFromHealthBar = "pendingYourApproval,yourRejected,pendingOthersApproval,dangling,orphan,"
					+ "suspectUp,suspectDown,testFailed,incomplete";
			
			
			System.out.println("SRT in hideFromHealthBar is  " + hideFromHealthBar);
			
			System.out.println("SRT in updateHealthBarSettings . showHealth is  " + showHealth);
			
			if (showHealth.contains("pendingYourApproval")){
				hideFromHealthBar = hideFromHealthBar.replace("pendingYourApproval", "");
			}
			if (showHealth.contains("yourRejected")){
				hideFromHealthBar = hideFromHealthBar.replace("yourRejected", "");
			}
			if (showHealth.contains("pendingOthersApproval")){
				hideFromHealthBar = hideFromHealthBar.replace("pendingOthersApproval", "");
			}
			
			
			if (showHealth.contains("dangling")){
				hideFromHealthBar = hideFromHealthBar.replace("dangling", "");
			}
			if (showHealth.contains("orphan")){
				hideFromHealthBar = hideFromHealthBar.replace("orphan", "");
			}
			
			
			if (showHealth.contains("suspectUp")){
				hideFromHealthBar = hideFromHealthBar.replace("suspectUp", "");
			}
			if (showHealth.contains("suspectDown")){
				hideFromHealthBar = hideFromHealthBar.replace("suspectDown", "");
			}
			
			
			
			if (showHealth.contains("testFailed")){
				hideFromHealthBar = hideFromHealthBar.replace("testFailed", "");
			}
			

			if (showHealth.contains("incomplete")){
				hideFromHealthBar = hideFromHealthBar.replace("incomplete", "");
			}
			
				String sql = "";
				sql = " update gr_projects " +
					" set hide_from_health_bar = ? " +
					" where id = ? ";
			
		 	prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, hideFromHealthBar);
			prepStmt.setInt(2, project.getProjectId());
			prepStmt.execute();
			System.out.println("SRT in hideFromHealthBar is  " + hideFromHealthBar);
			
			System.out.println("SRT in updateHealthBarSettings ");
			
			
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

	public static boolean isPrefixAvailable(Project project, String shortName){

		boolean prefixAvailable = false;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select count(*) \"count\"  " +
				" from gr_projects " +
				" where short_name = ? " +
				" and id != ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, shortName);
			prepStmt.setInt(2, project.getProjectId());
			rs = prepStmt.executeQuery();
			if (rs.next()){
				if ( rs.getInt("count") ==  0) {
					prefixAvailable = true;
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
		return (prefixAvailable);
	}

	public static int getProjectId(String shortName){

		int projectId = 0;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select id " +
				" from gr_projects " +
				" where short_name = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, shortName);
			rs = prepStmt.executeQuery();
			if (rs.next()){
				projectId = rs.getInt("id");
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
		return (projectId);
	}

	
	
	// called when a req needs to be added to a baseline. We take in the req id as a param, and
	// return a list of rTbaseline objects.
	// This will be displayed as a pull
	// down for the user to select from.
	public static ArrayList getEligibleBaselinesForRequirementType(int requirementTypeId) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList rTBaselines = new ArrayList();
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the list of folders that support the requirement
			// type of the requirement parameter.
			//
			
			String sql = "select b.id, b.requirement_type_id, b.name, b.locked, b.description,  " +
			" b.created_by, b.created_dt, b.last_modified_by , b.last_modified_dt " + 
			" from gr_rt_baselines b" +
			" where b.requirement_type_id = ? " +
			" order by b.name";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			rs = prepStmt.executeQuery();

			while (rs.next()) {
				int baselineId = rs.getInt("id");
				//int requirementTypeId = rs.getInt("requirement_type_id");
				String baselineName = rs.getString("name");
				int locked = rs.getInt("locked");
				String baselineDescription = rs.getString ("description");
				String createdBy = rs.getString("created_by");
				//this.createdDt = rs.getDate("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by") ;
				//this.lastModifiedDt = rs.getDate("last_modified_by");
				
				RTBaseline rTBaseline = new RTBaseline(baselineId, requirementTypeId, 
					baselineName,locked, baselineDescription, createdBy, lastModifiedBy);
				rTBaselines.add(rTBaseline);
			}

			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
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
		return (rTBaselines);
	}


	
	
	
	
	
	// Takes a rTBaselineid as a param, and deletes it in the db. Also, prior to deletion, 
	// it removes all the associations between Requiremetns and this Baseline 

	public static void deleteRTBaseline(int projectId, int rTBaselineId,
			String actorEmailId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			RTBaseline rTBaseline = new RTBaseline(rTBaselineId);

			// lets delete all the Requirement-Baseline associations. 
			// NOTE we will NOT delete the REquirements them seleves, only the associations.
			String sql = " delete from gr_requirement_baselines" +
				" where rt_baseline_id = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			prepStmt.execute();
			
			
			
			
			
			// lets delete this baseline .
			sql = " delete from gr_rt_baselines where id = ? ";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			prepStmt.execute();

			// lets log this event.
			ProjectUtil.createProjectLog(projectId, rTBaseline.getBaselineName(), "Delete",
					"Deleted Baseline" + rTBaseline.getBaselineName(), actorEmailId,  databaseType);
						
			prepStmt.close();
			con.close();
			}
			catch (Exception e) {
				// 	TODO Auto-generated catch block
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
	
	
	
	// takes a projectId, and user object as a param and returns an arraylist of Reports
	// owned by this user.
	public static ArrayList getUserReports(int projectId, User user) {
		
		ArrayList reports = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of Reports in this project , created by this user. Create a Report object for every 
			// report row and pump them into the array list
			// called myReports and add them to this Folder bean.
			
			String sql = " select r.id, r.project_id, r.folder_id, r.name, r.description," +
				" r.report_definition, " +
				" r.report_type, r.visibility, r.trace_tree_depth, " + 
				" r.report_sql, r.created_by " + 
				" from gr_reports r " + 
				" where  r.project_id = ? " +
				" and r.created_by = ? " +
				" order by r.report_type, r.name ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, user.getEmailId());
			rs = prepStmt.executeQuery();
			while (rs.next()){

				int reportId = rs.getInt("id");
				//int projectId = rs.getInt("project_id");
				int folderId = rs.getInt("folder_id");
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

	// takes a projectId, and user object as a param and returns an arraylist of Requirements
	// owned by this user.
	public static ArrayList getUserRequirements(int projectId, User user, String databaseType) {
		
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of Requirements in this project , created by this user. 
			// Create a Requirements object for every 
			// row and pump them into the array list
			// called myRequirements and add them to this Folder bean.
			
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag," +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\" ," +
					" r.approvers, " + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from , " +
					" r.user_defined_attributes, r.testing_status, r.deleted, f.folder_path, r.created_by, " +
					" date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM gr_requirements r , gr_requirement_types rt, gr_folders f " +
					" where r.requirement_type_id = rt.id and r.owner = ? " +
					" and r.project_id = ? " +
					" and r.folder_id = f.id " +
					" order by substring_index(r.tag,'.',2)+0 , r.full_tag " ;
			}
			else {
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag," +
				" r.name, r.description, r.tag, r.full_tag, " +
				" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\" ," +
				" r.approvers, " + 
				" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
				" r.trace_to, r.trace_from , " +
				" r.user_defined_attributes, r.testing_status, r.deleted, f.folder_path, r.created_by, " +
				" to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
				" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
				" FROM gr_requirements r , gr_requirement_types rt, gr_folders f " +
				" where r.requirement_type_id = rt.id and r.owner = ? " +
				" and r.project_id = ? " +
				" and r.folder_id = f.id " +
				" order by substr(r.tag, 1,instr(r.tag, '.',1, 2)-1)+0 , r.full_tag " ;
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, user.getEmailId());
			prepStmt.setInt(2, projectId);
			
			rs = prepStmt.executeQuery();
			while (rs.next()){
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
				String folderPath = rs.getString("folder_path");
				String createdBy = rs.getString("created_by");
				String createdDt = rs.getString("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by") ;
				
				//	Date lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				//	TODO : at some point see how we can make DATE fields works.
				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId, 
						projectId,requirementName, requirementDescription,	 requirementTag, 
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
	
	

	// we normally crunch metrics based on a Defects Status Groups. 
	// since 'Defects Status' is a custom attribute of the Defects req type
	// we need to make sure there is a ReqType called Defects and that 
	// there is an attribute called DefectsStatus
	// NOTE : A clone of this exists in the BatchMetrics file
	// and is run every night before we crunch defect trends etc...

	private static void updateDefectStatusGrouping(java.sql.Connection con, int projectId) 
	throws SQLException{
		
		// we need to make sure that the defect status grouping is up to date with defect status
		// but we shouldn't lose any old mapping info.
		
		// so step 1 is to build an array list of current status.
		String sql = "select options " +
			" from gr_rt_attributes rta, gr_requirement_types rt " +
			" where rta.requirement_type_id = rt.id " +
			" and rta.name = 'Defect Status' " +
			" and rt.name ='Defects' " +
			" and rt.project_id = ? ";
		
		PreparedStatement prepStmt = con.prepareStatement(sql);
		prepStmt.setInt(1, projectId);
		ResultSet rs = prepStmt.executeQuery();
		String defectStatusString = "";
		while (rs.next()){
			defectStatusString = rs.getString("options");
		}
		rs.close();
		prepStmt.close();
		
		
		if ((defectStatusString != null ) && (!defectStatusString.equals(""))){
			// this project has some defect status values. so lets crunch them.
			String[] status = null;
			if (defectStatusString.contains(",")){
				status = defectStatusString.split(",");
			}
			else {
				status = new String[1];
				status[0] = defectStatusString;
			}
			
			// at this point the Status string array has all the Defect Statuses.
			for (int i=0; i<status.length; i++) {
				String currentStatus = status[i];
				// if current status is not int this project's status groupings , lets add it.
				sql = "select count(*) \"matches\" " +
				 " from gr_defect_status_grouping " +
				 " where project_id = ?  and defect_status = ? ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.setString(2, currentStatus);
				rs = prepStmt.executeQuery();
				int matches = 0;
				while (rs.next()){
					matches = rs.getInt("matches");
				}
				if (matches == 0){
					// this current defects status is not in the gr_defects_status table. 
					// so lets insert it
					sql = " insert into gr_defect_status_grouping " +
						" (project_id, defect_status,defect_status_group) " +
						" values (?,?,?) ";
					PreparedStatement prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1, projectId);
					prepStmt2.setString(2, currentStatus);
					prepStmt2.setString(3, currentStatus);
					prepStmt2.execute();
					prepStmt2.close();
				}
				rs.close();
				prepStmt.close();
			}
			
			// at this point gr_defect_status_group has all the values in teh current defects status field
			// but the values that were there previously , but no longer there will need to be removed.
			String validStatusSQLString = "";
			for (int i=0; i<status.length; i++) {
				validStatusSQLString += "'" + status[i] + "',";
			}
			// lets drop the last ,
			if (validStatusSQLString.contains(",")){
				validStatusSQLString = (String) validStatusSQLString.subSequence(0,validStatusSQLString.lastIndexOf(","));
			}			
			// lets remove any entries in the gr_defect_status group that aren't in teh valid status sql string
			sql = "delete from gr_defect_status_grouping " +
				" where project_id = ?  " +
				" and defect_status not in ("+ validStatusSQLString  +")";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.execute();
			prepStmt.close();
		}
		else {
			// this project doesn't have a defects status defined. so lets drop them.
			sql = "delete from gr_defect_status_grouping where project_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.execute();
			prepStmt.close();
		}
		
	}


	
	// takes a projectId, as a param and returns an arraylist of Defect status groups
	// owned by this user.
	public static ArrayList getDefectStatusGroups(int projectId) {
		
		ArrayList defectStatusGroups = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			updateDefectStatusGrouping(con, projectId);

			// now lets go to the defect_status_groupings table and get the latest set.
			String sql = " SELECT id , project_id, defect_status, defect_status_group " +
			" FROM gr_defect_status_grouping " +
			" where project_id = ? " +
			" order by id ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int defectStatusGroupId = rs.getInt("id");
				String defectStatus = rs.getString("defect_status");
				String defectStatusGroup = rs.getString("defect_status_group");
				
				DefectStatus defectStatusObject = new DefectStatus(defectStatusGroupId, projectId, defectStatus, defectStatusGroup);
				defectStatusGroups.add(defectStatusObject);
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
		return defectStatusGroups;
	}
	


	// takes a projectId, as a param and returns an arraylist of all related projects
	// for this project.
	public static ArrayList getProjectRelations(int projectId, String databaseType) {
		
		ArrayList projectRelations = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			// now lets go to the defect_status_groupings table and get the latest set.
			String sql = " SELECT pr.id, pr.project_id, pr.project_short_name, " +
				" pr.related_project_id , pr.related_project_short_name, rp.name, rp.description,  " +
				" pr.relation_made_by, pr.relation_made_dt, pr.relation_description " +
			" FROM gr_project_relations pr , gr_projects rp  " +
			" where pr.project_id = ? " +
			" and pr.related_project_id  = rp.id " ;
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int id = rs.getInt("id");
				String projectShortName = rs.getString("project_short_name");
				int relatedProjectId = rs.getInt("related_project_id");
				String relatedProjectShortName = rs.getString("related_project_short_name");
				String relatedProjectName = rs.getString("name");
				String relatedProjectDescription = rs.getString("description");
				String relationMadeBy = rs.getString("relation_made_by");
				String relationMadeDt = rs.getString("relation_made_dt");
				String relationDescription = rs.getString("relation_description");
				
				Project relatedProject = new Project(relatedProjectId,  databaseType);
				ProjectRelation projectRelation = new ProjectRelation(id, projectId, projectShortName,
						relatedProjectId, relatedProjectShortName,  relatedProjectName, relatedProjectDescription,
						relationMadeBy, relationMadeDt, relationDescription, 
						relatedProject);
				projectRelations.add(projectRelation); 
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
		return projectRelations;
	}
	
	// same as getProjectRelations, just that the 'RelatedObject' is null. 
	// makes it faster. 
	public static ArrayList getProjectRelationsLite(int projectId, String databaseType) {
		
		ArrayList projectRelations = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			// now lets go to the defect_status_groupings table and get the latest set.
			String sql = " SELECT pr.id, pr.project_id, pr.project_short_name, " +
				" pr.related_project_id , pr.related_project_short_name, rp.name, rp.description,  " +
				" pr.relation_made_by, pr.relation_made_dt, pr.relation_description " +
			" FROM gr_project_relations pr , gr_projects rp  " +
			" where pr.project_id = ? " +
			" and pr.related_project_id  = rp.id " ;
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int id = rs.getInt("id");
				String projectShortName = rs.getString("project_short_name");
				int relatedProjectId = rs.getInt("related_project_id");
				String relatedProjectShortName = rs.getString("related_project_short_name");
				String relatedProjectName = rs.getString("name");
				String relatedProjectDescription = rs.getString("description");
				String relationMadeBy = rs.getString("relation_made_by");
				String relationMadeDt = rs.getString("relation_made_dt");
				String relationDescription = rs.getString("relation_description");
				
				Project relatedProject = null;
				ProjectRelation projectRelation = new ProjectRelation(id, projectId, projectShortName,
						relatedProjectId, relatedProjectShortName,  relatedProjectName, relatedProjectDescription,
						relationMadeBy, relationMadeDt, relationDescription, 
						relatedProject);
				projectRelations.add(projectRelation); 
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
		return projectRelations;
	}
	
	public static ArrayList getProjectRelationsLiteWithACL(int projectId, int userId) {
		
		ArrayList projectRelations = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			// now lets go to the defect_status_groupings table and get the latest set.
			String sql = " SELECT distinct pr.id, pr.project_id, pr.project_short_name, " +
				" pr.related_project_id , pr.related_project_short_name, rp.name, rp.description,  " +
				" pr.relation_made_by, pr.relation_made_dt, pr.relation_description " +
			" FROM gr_project_relations pr , gr_projects rp, gr_user_roles ur   " +
			" where pr.project_id = ? " +
			" and pr.related_project_id  = rp.id "
			+ " and pr.related_project_id = ur.project_id "
			+ " and ur.user_id = ? " ;
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2,userId);
			
			System.out.println("srt in getProjectRelationsLiteWithACL  sql is " + sql + " projetId is " + projectId + " user id is " + userId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int id = rs.getInt("id");
				String projectShortName = rs.getString("project_short_name");
				int relatedProjectId = rs.getInt("related_project_id");
				String relatedProjectShortName = rs.getString("related_project_short_name");
				String relatedProjectName = rs.getString("name");
				String relatedProjectDescription = rs.getString("description");
				String relationMadeBy = rs.getString("relation_made_by");
				String relationMadeDt = rs.getString("relation_made_dt");
				String relationDescription = rs.getString("relation_description");
				
				
				System.out.println("srt adding related project  = is " + relatedProjectName );

				Project relatedProject = null;
				ProjectRelation projectRelation = new ProjectRelation(id, projectId, projectShortName,
						relatedProjectId, relatedProjectShortName,  relatedProjectName, relatedProjectDescription,
						relationMadeBy, relationMadeDt, relationDescription, 
						relatedProject);
				projectRelations.add(projectRelation); 
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
		return projectRelations;
	}

	// takes a projectId, as a param and returns an arraylist of all related projects
	// for this project.
	// NOTE : Same of getProjectRelations, just that it doesn't send back relatedProject object.
	public static ArrayList getProjectRelationsLight(int projectId, String databaseType) {
		
		ArrayList projectRelationsLight = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			// now lets go to the defect_status_groupings table and get the latest set.
			String sql = " SELECT pr.id, pr.project_id, pr.project_short_name, " +
				" pr.related_project_id , pr.related_project_short_name, rp.name, rp.description, rp.owner, " +
				" pr.relation_made_by, pr.relation_made_dt, pr.relation_description " +
			" FROM gr_project_relations pr , gr_projects rp  " +
			" where pr.project_id = ? " +
			" and pr.related_project_id  = rp.id " ;
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int id = rs.getInt("id");
				String projectShortName = rs.getString("project_short_name");
				int relatedProjectId = rs.getInt("related_project_id");
				String relatedProjectShortName = rs.getString("related_project_short_name");
				String relatedProjectName = rs.getString("name");
				String relatedProjectDescription = rs.getString("description");
				String relatedProjectOwner = rs.getString("owner");
				String relationMadeBy = rs.getString("relation_made_by");
				String relationMadeDt = rs.getString("relation_made_dt");
				String relationDescription = rs.getString("relation_description");
				
				
				ProjectRelationLight projectRelationLight = new ProjectRelationLight(id, projectId, projectShortName,
						relatedProjectId, relatedProjectShortName,  relatedProjectName, relatedProjectDescription, relatedProjectOwner,
						relationMadeBy, relationMadeDt, relationDescription);
				projectRelationsLight.add(projectRelationLight); 
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
		return projectRelationsLight;
	}
	
	// takes a user object, and returns an array list of unsponsored projects
	// that this user is a member 
	public static ArrayList getUsersNonLicensedProjects(User user) {
		ArrayList projects = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of projects that do not have a billing organization id
			// in which this user is a member of.
			
			String sql = "select distinct p.id, p.short_name, p.name, p.project_type, p.description, " +
				" p.owner, p.website, p.organization, p.tags, p.restricted_domains," +
				" p.enable_tdcs, p.enable_agile_scrum, p.billing_organization_id, " +
				" p.number_of_requirements, " +
				" p.created_by, p.created_dt, p.last_modified_by, p.last_modified_dt , p.archived , p.hide_priority " +
				" from gr_projects p, gr_roles r, gr_user_roles ur " +
				" where p.id = r.project_id " +
				" and r.id = ur.role_id " +
				" and ur.user_id = ? " +
				" and p.billing_organization_id is null " +
				" order by p.short_name";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, user.getUserId());
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
					projectDescription, 
					projectOwner, projectWebsite, projectOrganization, projectTags, 
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

	
	// takes a projectId, and user object as a param and returns an arraylist of WordTemplates
	// owned by this user.
	public static ArrayList getUserWordTemplates(int projectId, User user, String databaseType) {
		
		ArrayList wordTemplates = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of wordTemplates in this project , created by this user. 
			// Create a wordTemplatesobject for every 
			// report row and pump them into the array list
			// 
		
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "select wt.id, wt.tdcs_document_id, wt.project_id, wt.folder_id, wt.name, wt.visibility," +
					" wt.description, wt.file_path, wt.created_by," +
					" date_format(wt.created_dt, '%d %M %Y %r ') \"created_dt\" ," +
					" wt.last_modified_by," +
					" date_format(wt.last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
					" from gr_word_templates wt " +
					" where wt.created_by = ? " +
					" and wt.project_id = ? " +
					" order by wt.name ";
			}
			else {
				sql = "select wt.id, wt.tdcs_document_id, wt.project_id, wt.folder_id, wt.name, wt.visibility," +
				" wt.description, wt.file_path, wt.created_by," +
				" to_char(wt.created_dt, 'DD MON YYYY') \"created_dt\" ," +
				" wt.last_modified_by," +
				" to_char(wt.last_modified_dt, 'DD MON YYYY') \"last_modified_dt\" " +
				" from gr_word_templates wt " +
				" where wt.created_by = ? " +
				" and wt.project_id = ? " +
				" order by wt.name ";
			}
			
			
			
			
			
			
			 prepStmt = con.prepareStatement(sql);
	
			 prepStmt.setString(1,user.getEmailId());
			 prepStmt.setInt(2,projectId);
			 rs = prepStmt.executeQuery();
			
			while (rs.next()) {
				int templateId = rs.getInt("id");
				int tDCSDocumentId = rs.getInt("tdcs_document_id");
				//int projectId = rs.getInt("project_id");
				int folderId = rs.getInt("folder_id");
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

	
	public static int getNumberOfWordTemplatesInRequirementType(int requirementTypeId) {
		
		int numerOfWordTemplates = 0;
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of wordTemplates in this project , created by this user. 
			// Create a wordTemplatesobject for every 
			// report row and pump them into the array list
			// 
		
			String sql = "select count(*) 'numerOfWordTemplates' " +
					" from gr_word_templates wt, gr_folders f " +
					" where wt.folder_id = f.id and f.requirement_type_id = ?  " ;
			
			
			 prepStmt = con.prepareStatement(sql);
	
			 prepStmt.setInt(1, requirementTypeId);
			
			 rs = prepStmt.executeQuery();
			while (rs.next()) {
				numerOfWordTemplates  = rs.getInt("numerOfWordTemplates");
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
		return numerOfWordTemplates;
	}
	
	// takes a projectId, and user object as a param and returns an arraylist of Reports
	// owned by this user.
	public static ArrayList getAllReportsInProject(int projectId) {
		
		ArrayList reports = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of Reports in this project , created by this user. Create a Report object for every 
			// report row and pump them into the array list
			// called myReports and add them to this Folder bean.
			
			String sql = " select r.id, r.project_id, r.folder_id, r.name, r.description," +
				" r.report_definition, " +
				" r.report_type, r.visibility, r.trace_tree_depth, " + 
				" r.report_sql, r.created_by " + 
				" from gr_reports r " + 
				" where  r.project_id = ? " +
				" order by r.report_type, r.name ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();
			while (rs.next()){

				int reportId = rs.getInt("id");
				//int projectId = rs.getInt("project_id");
				int folderId = rs.getInt("folder_id");
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

	
	// takes a projectId, and user object as a param and returns an arraylist of WordTemplates
	// owned by this user.
	public static ArrayList getAllWordTemplatesInProject(int projectId,  String databaseType) {
		
		ArrayList wordTemplates = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of wordTemplates in this project , 
			// Create a wordTemplatesobject for every 
			// report row and pump them into the array list
			// 
		
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "select wt.id, wt.tdcs_document_id, wt.project_id, wt.folder_id, wt.name, wt.visibility," +
					" wt.description, wt.file_path, wt.created_by," +
					" date_format(wt.created_dt, '%d %M %Y %r ') \"created_dt\" ," +
					" wt.last_modified_by," +
					" date_format(wt.last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
					" from gr_word_templates wt " +
					" where  wt.project_id = ? " +
					" order by wt.name ";
			}
			else {
				sql = "select wt.id, wt.tdcs_document_id, wt.project_id, wt.folder_id, wt.name, wt.visibility," +
				" wt.description, wt.file_path, wt.created_by," +
				" to_char(wt.created_dt, 'DD MON YYYY') \"created_dt\" ," +
				" wt.last_modified_by," +
				" to_char(wt.last_modified_dt, 'DD MON YYYY') \"last_modified_dt\" " +
				" from gr_word_templates wt " +
				" where wt.project_id = ? " +
				" order by wt.name ";
			}
			
			
			
			
			 prepStmt = con.prepareStatement(sql);
	
			 prepStmt.setInt(1,projectId);
			 rs = prepStmt.executeQuery();
			
			while (rs.next()) {
				int templateId = rs.getInt("id");
				int tDCSDocumentId = rs.getInt("tdcs_document_id");
				//int projectId = rs.getInt("project_id");
				int folderId = rs.getInt("folder_id");
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


	public static int getPercentageCompletedDriverReqTypeId(int projectId) {
		
		int pct_complete_driver_requirement_type_id = 0;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of wordTemplates in this project , 
			// Create a wordTemplatesobject for every 
			// report row and pump them into the array list
			// 
		
			String sql = "";
			
				sql = "select pct_complete_driver_requirement_type_id  " +
					" from gr_projects " +
					" where  id = ? ";
			
			
			
			
			 prepStmt = con.prepareStatement(sql);
	
			 prepStmt.setInt(1,projectId);
			 rs = prepStmt.executeQuery();
			
			while (rs.next()) {
				pct_complete_driver_requirement_type_id = rs.getInt("pct_complete_driver_requirement_type_id");
				
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
		return pct_complete_driver_requirement_type_id;
	}


	// takes a projectId, and returns all the sprints in this project.
	public static ArrayList getProjectSprints(int projectId,  String databaseType) {
		
		ArrayList sprints = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now get the list of sprints in this project , created by this user. 
			// Create a Sprint object for every 
			// report row and pump them into the array list
			// 
		
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "select id, project_id, name, description, scrum_master, " +
					" date_format(start_dt, '%m/%d/%Y')  \"start_dt\" , " +
					" date_format(end_dt, '%m/%d/%Y')  \"end_dt\"  " +
					" from gr_sprints " + 
					" where project_id  = ?  ";
			}
			else {
				sql = "select id, project_id, name, description, scrum_master, " +
				" to_char(start_dt, 'MM/DD/YYYY') \"start_dt\" , " +
				" to_char(end_dt, 'MM/DD/YYYY') \"end_dt\"  " +
				" from gr_sprints " + 
				" where project_id  = ?  ";
			}
			
			
			
			
			
			
			 prepStmt = con.prepareStatement(sql);
			 prepStmt.setInt(1,projectId);
			 rs = prepStmt.executeQuery();
			
			while (rs.next()) {
				int sprintId = rs.getInt("id");
				String sprintName = rs.getString("name");
				String sprintDescription = rs.getString("description");
				String scrumMaster = rs.getString("scrum_master");
				String sprintStartDt = rs.getString("start_dt");
				String sprintEndDt = rs.getString("end_dt");
				
				Sprint sprint = new Sprint(  sprintId, projectId, sprintName , sprintDescription,scrumMaster,sprintStartDt,sprintEndDt);
				
				sprints.add(sprint);
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
		return sprints;
	}

	// when called with a projectId, it returns an array list of baselines in this project
	public static ArrayList getAllBaselinesInProject(int projectId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		ArrayList baselines = new ArrayList();
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			//
			// This sql gets the list of baselines in a  project and puts them in an arraylist.
			
			
			String sql = " select b.id, b.requirement_type_id, b.name,b.locked, b.description,  " +
				" b.created_by, b.created_dt, b.last_modified_by , b.last_modified_dt " + 
				" from gr_rt_baselines b, gr_requirement_types rt" +
				" where rt.project_id = ? " +
				" and b.requirement_type_id = rt.id " +
				" order by rt.short_name, b.name ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();
	
			while (rs.next()){
				int baselineId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				String baselineName = rs.getString("name");
				int locked = rs.getInt("locked");
				String baselineDescription = rs.getString ("description");
				String createdBy = rs.getString("created_by");
				//this.createdDt = rs.getDate("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by") ;
				//this.lastModifiedDt = rs.getDate("last_modified_by");
			
				RTBaseline baseline = new RTBaseline(baselineId, requirementTypeId, baselineName,locked,
					baselineDescription, createdBy, lastModifiedBy);			
			
				baselines.add(baseline);
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
		return (baselines);
	}

	
	// removes the project and all its related components. 
	public static void deleteProject(int projectId, User user , String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			
			
			// lets notify all the users of the project that this project has been deleted .
			Calendar cal = Calendar.getInstance();
			Project project = new Project(projectId,  databaseType);
			String messageBody = "Project '" + project.getProjectName() + "' has been deleted by " + 
			 user.getFirstName() + " " + user.getLastName() + " (" + user.getEmailId() + ") at " + 
			 cal.getTime() + ". ";  
			 
			
			ArrayList members = project.getMembers();
			Iterator m = members.iterator();
			while (m.hasNext()){
				User member = (User) m.next();
				if (databaseType.equals("mySQL")){
					sql = "insert into gr_messages (project_name, to_email_id, message_type, message_body," +
						" message_created_dt)" +
						" values (?,?,'deletedProject',?,now()) ";
				}
				else {
					sql = "insert into gr_messages (project_name, to_email_id, message_type, message_body," +
					" message_created_dt)" +
					" values (?,?,'deletedProject',?, sysdate) ";
				}
				prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, project.getProjectName());
				prepStmt.setString(2, member.getEmailId());
				prepStmt.setString(3, messageBody);
				prepStmt.execute();
				prepStmt.close();
			}
			
			// lets delete the req baselines
			sql = "delete from gr_requirement_baselines " +
				" where requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" ) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			Thread.sleep(10);
			
			
			// we have to do the deletes in a certain sequence, due to Foreign / Primary key relationships.
			// lets delete the req versions
			sql = "delete from gr_requirement_versions " +
				" where requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" ) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			Thread.sleep(10);
			
			
			// lets delete the req comments
			sql = "delete from gr_requirement_comments " +
				" where requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" ) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			Thread.sleep(10);
			
			// lets delete the req log
			sql = "delete from gr_requirement_log " +
				" where requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" ) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			Thread.sleep(10);

			
			
			// lets delete the req approval history 
			sql = "delete from gr_requirement_approval_h" +
				" where requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" ) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			Thread.sleep(10);
			
			// lets delete the req attribute value
			sql = "delete from gr_r_attribute_values " +
				" where requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" ) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			Thread.sleep(10);
			
			// lets delete the req traces
			sql = "delete from gr_traces " +
				" where from_requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" )  " +
				"or " +
				" to_requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" )  ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			Thread.sleep(10);
			
			// lets iterate through all the attachments attachments and drop them.
			sql = "select file_path " +
				" from gr_requirement_attachments a, gr_requirements r" +
				" where a.requirement_id = r.id " +
				" and r.project_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				Thread.sleep(100);
				String attachmentFilePath = rs.getString("file_path");
				File file = new File(attachmentFilePath);
				if (file != null){
					File dir = file.getParentFile();
					// lets drop the file.
					file.delete();
					
					if (dir != null) {
						dir.delete();
					}
				}
			}
			prepStmt.close();
			rs.close();
			
			// now lets delete all requirement attachment entries in the db.
			sql = " delete from gr_requirement_attachments " +
				" where requirement_id  in " +
				"	(select id from gr_requirements where project_id = ? )";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.execute();
			prepStmt.close();
			Thread.sleep(10);
			
			// lets delete the requirements 
			sql = "delete from gr_requirements " +
				" where project_id = " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			Thread.sleep(10);
			
			// lets delete the role privs
			sql = "delete from gr_reports " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			Thread.sleep(10);
			
			// lets delete the role privs
			sql = "delete from gr_role_privs " +
				" where folder_id in (select id from gr_folders where project_id= " + projectId  + ")";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			Thread.sleep(10);
			
			// lets delete the word templates 
			sql = "delete from gr_word_templates " +
				" where project_id = " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			Thread.sleep(10);
			
			// lets delete the folders
			sql = "delete from gr_folders " +
				" where project_id = " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			Thread.sleep(10);
			
			// lets delete the invitations 
			sql = "delete from gr_invitations " +
				" where project_id = " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			Thread.sleep(10);
			
			// lets delete the user_roles 
			sql = "delete from gr_user_roles " +
				" where project_id = " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			Thread.sleep(10);
			
			// lets delete the roles 
			sql = "delete from gr_roles" +
				" where project_id = " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			Thread.sleep(10);
			
			// lets delete the requirements_seq
			sql = "delete from gr_requirements_seq" +
				" where requirement_type_id in " +
				" (select id from gr_requirement_types where project_id= " + projectId  + ")";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			Thread.sleep(10);
						
			// lets delete the rt_attributes
			sql = "delete from gr_rt_attributes " +
				" where requirement_type_id in " +
				" (select id from gr_requirement_types where project_id= " + projectId  + ")";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			Thread.sleep(10);
			
			
			// lets delete the rt_baselines
			sql = "delete from gr_rt_baselines " +
				" where requirement_type_id in " +
				" (select id from gr_requirement_types where project_id= " + projectId  + ")";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();	
			Thread.sleep(10);
									
			// lets delete the requirement_types
			sql = "delete from gr_requirement_types  " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			Thread.sleep(10);
			
			// lets delete the project_log
			sql = "delete from gr_project_log " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			Thread.sleep(10);
			
			// lets delete the gr-search
			sql = "delete from gr_search " +
				" where project_id= " + projectId  ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			Thread.sleep(10);
			
			// we are deliberately not deleteing messages here.
			/*
			// lets delete the gr_messages
			sql = "delete from gr_messages " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			*/
			
			
			// lets delete the release requirements
			sql = "delete from gr_release_requirements " +
				" where project_id= " + projectId  ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();		
			Thread.sleep(10);
			
			// lets delete the release_metrics
			sql = "delete from gr_release_metrics  " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			Thread.sleep(10);
			
			// lets delete the project_metrics
			sql = "delete from gr_project_metrics " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();		
			Thread.sleep(10);
			
			// lets delete the user_metrics
			sql = "delete from gr_user_metrics " +
				" where project_id= " + projectId  ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();	
			Thread.sleep(10);
			
			// lets delete the baseline metrics 
			sql = "delete from gr_baseline_metrics  " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();	
			Thread.sleep(10);
			
			// lets delete the folder metrics
			sql = "delete from gr_folder_metrics " +
				" where project_id= " + projectId  ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();	
			Thread.sleep(10);
			
			// lets delete the requirement_types
			sql = "delete from gr_projects " +
				" where id  = " + projectId  ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();	
			Thread.sleep(10);
			
							
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

	// when called with a projectId, it returns an array list of baselines in this project
	public static void deleteAllIntegrationMenus(int projectId, String menuType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			
			String sql = " delete from gr_project_integration_menu" +
				" where project_id = ? " +
				" and menu_type = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2,menuType);
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

	public static void archiveProject(Project project, User user, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			
			String sql = " update gr_projects set archived = 1 " +
				" where id = ? " ;
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, project.getProjectId());
			prepStmt.execute();
			
			prepStmt.close();
			con.close();

			ProjectUtil.createProjectLog(project.getProjectId(), project.getShortName() , "Archiving Project ",
				"Project '" + project.getProjectName() + "' Archived '" , user.getEmailId() ,  databaseType);
				 
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

	public static void reActivateProject(Project project, User user, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			
			String sql = " update gr_projects set archived = 0 " +
				" where id = ? " ;
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, project.getProjectId());
			prepStmt.execute();
			
			prepStmt.close();
			con.close();

			ProjectUtil.createProjectLog(project.getProjectId(), project.getShortName() , "Re Activating Project ",
				"Project '" + project.getProjectName() + "' Re Activated '" , user.getEmailId() ,  databaseType);
				 
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



	// when called with a projectId, it returns an array list of baselines in this project
	public static boolean isOwnerActiveMemberOfProject(String owner, int projectId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		boolean ownerIsActiveMemberOfProject = false;
		
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "select count(*) 'count' " +
					" from gr_user_roles ur, gr_projects p, gr_users u " + 
					" where ur.project_id = p.id " +
					" and ur.user_id = u.id " +
					" and u.email_id = ? " +
					" and p.id = ? ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, owner);
				prepStmt.setInt(2, projectId);
				int count = 0;
				rs = prepStmt.executeQuery();
				while (rs.next()){
					count = rs.getInt("count");
				}
				rs.close();
				prepStmt.close();
				
				if (count > 0 ){
					ownerIsActiveMemberOfProject = true;
				}
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
		return (ownerIsActiveMemberOfProject);
	}
	
	
	public static int getRequirementsPendingApprovalByApproverCount(String owner, int projectId, String dashboardType, String ownedBy){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;

		int myPendingApprovalRequirements = 0; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			
			
			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					// i.e you can never have ALL Users calling All Projects.
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					String sql = " select count(*) 'myPendingApprovalRequirements'  " +
							" from gr_requirements r, gr_projects p " +
							" where r.status = 'In Approval WorkFlow' " +
							" and r.deleted = 0 " +
							" and r.project_id = p.id " +
							" and p.archived = 0 " +
							" and r.project_id = " + projectId ;
					prepStmt = con.prepareStatement(sql);
					rs = prepStmt.executeQuery();
					while (rs.next()){
						myPendingApprovalRequirements = rs.getInt("myPendingApprovalRequirements");
					}
				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					String sql = " select count(*) 'myPendingApprovalRequirements'  " +
							" from gr_requirement_approval_h rah , gr_requirements r, gr_projects p " +
							" where rah.requirement_id = r.id " +
							" and rah.version = r.version " +
							" and rah.approver_email_id = ? " +
							" and rah.response  = 'Pending' " +
							" and r.deleted = 0 " +
							" and r.project_id = p.id " +
							" and p.archived = 0 ";
					if (projectId > 0 ){
						sql += " and r.project_id = " + projectId ;
					}
					

					prepStmt = con.prepareStatement(sql);
					
					prepStmt.setString(1, ownedBy);
					rs = prepStmt.executeQuery();
					while (rs.next()){
						myPendingApprovalRequirements = rs.getInt("myPendingApprovalRequirements");
					}
					
				}
				
			}
			
			rs.close();
			prepStmt.close();
				
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
		return (myPendingApprovalRequirements);
	}

	public static ArrayList getRequirementsPendingApprovalByApprover(SecurityProfile securityProfile, String owner, String databaseType, int projectId, String dashboardType, String ownedBy ){
		
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			//
			// This sql gets the list of requirements of this type and puts them in the arrray list.
			// creates a requirement object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "";
			
			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f, gr_projects p  " +
							" where  r.status = 'In Approval WorkFlow' " + 
							" and r.requirement_type_id = rt.id " +
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and r.project_id = p.id " +
							" and p.archived = 0 +" +
							" and r.project_id = " + projectId ;
							
							
					}
					else {
						
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f, gr_projects p  " +
							" where  r.status = 'In Approval WorkFlow' " + 
							" and r.requirement_type_id = rt.id " +
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and r.project_id = p.id " +
							" and p.archived = 0 " + 
							" and r.project_id = " + projectId ;
						
					}
					sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";

					
					prepStmt = con.prepareStatement(sql);
				
				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_requirement_approval_h rah, gr_folders f, gr_projects p  " +
							" where  r.requirement_type_id = rt.id " +
							" and r.id = rah.requirement_id " +
							" and r.version = rah.version " +
							" and r.folder_id = f.id " +
							" and  rah.approver_email_id = ? " +
							" and rah.response  = 'Pending' " +
							" and r.deleted = 0 " +
							" and r.project_id = p.id " +
							" and p.archived = 0 ";
						 	
						 	if (projectId > 0 ){
								sql += " and r.project_id = " + projectId ;
							}
							
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_requirement_approval_h rah, gr_folders f , gr_projects p " +
						" where  r.requirement_type_id = rt.id " +
						" and r.id = rah.requirement_id " +
						" and r.version = rah.version " +
						" and r.folder_id = f.id " +
						" and  rah.approver_email_id = ? " +
						" and rah.response  = 'Pending' " +
						" and r.deleted = 0 " +
						" and r.project_id = p.id " +
						" and p.archived = 0 ";
						
						if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
					}
					sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";

					

					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, ownedBy);
				}

			}
			
			
			
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				int folderId = rs.getInt("folder_id");
				projectId = rs.getInt("project_id");
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
				//lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId,
					projectId, 
					requirementName, requirementDescription, requirementTag, requirementFullTag,
					version, approvedByAllDt, approvers ,
					requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
					requirementPctComplete, requirementExternalUrl , traceTo, traceFrom, 
					userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy, 
					requirementTypeName, createdDt);
				
				// if the user does not have read permissions on this requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}

				
				requirements.add(requirement);
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
		return (requirements);

	}
	
	
	public static int getIncompleteRequriementsCount(String owner,  int projectId, String dashboardType, String ownedBy){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;

		int myIncompleteRequirements = 0; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			if (ownedBy.equals("All Users")){
				// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
				
				// i.e you can never have ALL Users calling All Projects.
				
				// then we show all the requirements in this project, owned by ALL users.
				// we are trying to find all requirements in this project (or across all projects)
				// that are pending approval by this user.
				String sql = " select count(*) 'myIncompleteRequirements'  " +
						" from gr_requirements r, gr_projects p " +
						" where r.pct_complete < 100  " +
						" and r.deleted = 0 " +
						" and r.project_id = p.id " +
						" and p.archived  = 0 " + 
						" and r.project_id = " + projectId ;
					
					
					prepStmt = con.prepareStatement(sql);
					rs = prepStmt.executeQuery();
					while (rs.next()){
						myIncompleteRequirements = rs.getInt("myIncompleteRequirements");
					}
					rs.close();
					prepStmt.close();

			}
			else {
				// this is a for a particular user. So we need to narrow the scope .
				// we are trying to find all requirements in this project (or across all projects)
				// that are pending approval by this user.
				String sql = " select count(*) 'myIncompleteRequirements'  " +
						" from gr_requirements r, gr_projects p " +
						" where r.owner  = ? " +
						" and r.pct_complete < 100  " +
						" and r.deleted = 0 " +
						" and r.project_id = p.id " +
						" and p.archived  = 0 ";
					if (projectId > 0 ){
						sql += " and r.project_id = " + projectId ;
					}
					
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, ownedBy);
					rs = prepStmt.executeQuery();
					while (rs.next()){
						myIncompleteRequirements = rs.getInt("myIncompleteRequirements");
					}
					rs.close();
					prepStmt.close();

			}

				
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
		return (myIncompleteRequirements);
	}	

	public static int getTotalRequriementsCount(String owner, int projectId, String dashboardType, String ownedBy){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;

		int myTotalRequirements = 0; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		// this is a for a particular user. So we need to narrow the scope .
		// we are trying to find all requirements in this project (or across all projects)
		// that are pending approval by this user.
			String sql = " select count(*) 'myTotalRequirements'  " +
				" from gr_requirements r " +
				" where r.owner  = ? " +
				" and r.deleted = 0 " +
				" and r.project_id = " + projectId ;
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, ownedBy);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				myTotalRequirements = rs.getInt("myTotalRequirements");
			}
			rs.close();
			prepStmt.close();					


			
				
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
		return (myTotalRequirements);
	}	


	
	public static int getDanglingRequriementsCount(String owner, int projectId, String dashboardType, String ownedBy){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;

		int myDanglingRequirements = 0; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					// i.e you can never have ALL Users calling All Projects.
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					String sql = " select count(*) 'myDanglingRequirements'  " +
							" from gr_requirements r, gr_requirement_types rt , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and rt.can_be_dangling = 1" + 
							" and (r.trace_from is null or r.trace_from = '')" +
							" and r.deleted = 0 " +
							" and r.project_id = p.id " +
							" and p.archived = 0 "+
							" and r.project_id = " + projectId ;
					prepStmt = con.prepareStatement(sql);
					rs = prepStmt.executeQuery();
					while (rs.next()){
						myDanglingRequirements = rs.getInt("myDanglingRequirements");
					}
					rs.close();
					prepStmt.close();
				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
						String sql = " select count(*) 'myDanglingRequirements'  " +
							" from gr_requirements r, gr_requirement_types rt , gr_projects p " +
							" where r.owner  = ? " +
							" and r.requirement_type_id = rt.id " +
							" and rt.can_be_dangling = 1" + 
							" and (r.trace_from is null or r.trace_from = '')" +
							" and r.deleted = 0 " +
							" and r.project_id = p.id " +
							" and p.archived = 0 ";
						if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						
						
						prepStmt = con.prepareStatement(sql);
						prepStmt.setString(1, ownedBy);
						rs = prepStmt.executeQuery();
						while (rs.next()){
							myDanglingRequirements = rs.getInt("myDanglingRequirements");
						}
						rs.close();
						prepStmt.close();					
				}

			}
			
			
				
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
		return (myDanglingRequirements);
	}	

	 
	public static int getReqsPendingApprovalCount(String owner, int projectId , String dashboardType, String ownedBy){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;

		int myReqsPendingApproval  = 0; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			

			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					// i.e you can never have ALL Users calling All Projects.
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					String sql = " select count(*) 'myReqsPendingApproval'  " +
							" from gr_requirements r,  gr_projects p " +
							" where r.status = ?  " +
							" and r.deleted = 0 " +
							" and r.project_id = p.id " +
							" and p.archived = 0 "+
							" and r.project_id = " + projectId ;
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1,"In Approval WorkFlow");
					rs = prepStmt.executeQuery();
					while (rs.next()){
						myReqsPendingApproval = rs.getInt("myReqsPendingApproval");
					}
					rs.close();
					prepStmt.close();
				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
						
					String sql = " select count(*) 'myReqsPendingApproval'  " +
							" from gr_requirements r , gr_projects p " +
							" where r.owner  = ? " +
							" and r.status = ? " +
							" and r.deleted = 0  " +
							" and r.project_id = p.id " +
							" and p.archived = 0 ";
						if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						prepStmt = con.prepareStatement(sql);
						prepStmt.setString(1, ownedBy);
						prepStmt.setString(2,"In Approval WorkFlow");
						rs = prepStmt.executeQuery();
						while (rs.next()){
							myReqsPendingApproval = rs.getInt("myReqsPendingApproval");
						}
						rs.close();
						prepStmt.close();
						

				}			
			}
			
			
				
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
		return (myReqsPendingApproval);
	}	
	
	
	public static ArrayList getRequirementsThatArePendingApproval(SecurityProfile securityProfile, String owner, String databaseType, int projectId , String dashboardType, String ownedBy){
		
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			//
			// This sql gets the list of requirements of this type and puts them in the arrray list.
			// creates a requirement object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "";
			
			
			
			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					// i.e you can never have ALL Users calling All Projects.
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\",  " +
							" datediff(now() , submitted_for_approval_dt) 'daysSinceSubmittedForApproval' , " +
							" datediff(now() , last_approval_reminder_sent_dt) 'daysSinceLastApprovalReminder' " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and r.status = ? " +
							" and r.project_id = p.id " +
							" and p.archived  = 0 " +
							" and r.project_id = " + projectId ;
						 sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
						 sql += " limit 5000 ";
						 
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\", " +
						" sysdate - submitted_for_approval_dt 'daysSinceSubmittedForApproval' , " +
						" sysdate - last_approval_reminder_sent_dt 'daysSinceLastApprovalReminder'  " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f , gr_projects p " +
						" where  r.requirement_type_id = rt.id " +
						" and r.folder_id = f.id " +
						" and r.deleted = 0 " +
						" and r.status = ? " +
						" and rownum < 5000 " +
						" and r.project_id = p.id " +
						" and p.archived  = 0 "+
						" and r.project_id = " + projectId ;
						
						sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
					}
				
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1,"In Approval WorkFlow");

					
				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\",  " +
							" datediff(now() , submitted_for_approval_dt) 'daysSinceSubmittedForApproval' , " +
							" datediff(now() , last_approval_reminder_sent_dt) 'daysSinceLastApprovalReminder' " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and r.owner = ? " +
							" and r.status = ? " +
							" and r.project_id = p.id " +
							" and p.archived  = 0 " ;
						 if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						 sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
						 sql += " limit 5000 ";
						 
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\", " +
						" sysdate - submitted_for_approval_dt 'daysSinceSubmittedForApproval' , " +
						" sysdate - last_approval_reminder_sent_dt 'daysSinceLastApprovalReminder'  " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f , gr_projects p " +
						" where  r.requirement_type_id = rt.id " +
						" and r.folder_id = f.id " +
						" and r.deleted = 0 " +
						" and r.owner = ? " +
						" and r.status = ? " +
						" and rownum < 5000 " +
						" and r.project_id = p.id " +
						" and p.archived  = 0 " ;
						if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
					}
					
					

					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, ownedBy);
					prepStmt.setString(2,"In Approval WorkFlow");
					
				}

			}

			
			
			
			
			
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				int folderId = rs.getInt("folder_id");
				projectId = rs.getInt("project_id");
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
				//lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				int daysSinceSubmittedForApproval = rs.getInt("daysSinceSubmittedForApproval");
				int daysSinceLastApprovalReminder = rs.getInt("daysSinceLastApprovalReminder");
				
				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId,
					projectId, 
					requirementName, requirementDescription, requirementTag, requirementFullTag,
					version, approvedByAllDt, approvers ,
					requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
					requirementPctComplete, requirementExternalUrl , traceTo, traceFrom, 
					userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy, 
					requirementTypeName, createdDt);
				
				
				requirement.setDaysSinceSubmittedForApproval(daysSinceSubmittedForApproval);
				requirement.setDaysSinceLastApprovalReminder(daysSinceLastApprovalReminder);
				
				// if the user does not have read permissions on this requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}

				
				requirements.add(requirement);
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
		return (requirements);

	}


	public static int getReqsRejectedCount(String owner, int projectId, String dashboardType, String ownedBy){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;

		int myRejectedRequirements = 0; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();


			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					// i.e you can never have ALL Users calling All Projects.
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					String sql = " select count(*) 'myRejectedRequirements'  " +
							" from gr_requirements r,  gr_projects p " +
							" where r.status = ?  " +
							" and r.deleted = 0 " +
							" and r.project_id = p.id " +
							" and p.archived = 0 "+
							" and r.project_id = " + projectId ;
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1,"Rejected");
					rs = prepStmt.executeQuery();
					while (rs.next()){
						myRejectedRequirements = rs.getInt("myRejectedRequirements");
					}
					rs.close();
					prepStmt.close();
				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
						
					String sql = " select count(*) 'myRejectedRequirements'  " +
							" from gr_requirements r, gr_projects p " +
							" where r.owner  = ? " +
							" and r.status = ? " +
							" and r.deleted = 0 " +
							" and r.project_id = p.id " +
							" and p.archived = 0 ";
						if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						prepStmt = con.prepareStatement(sql);
						prepStmt.setString(1, ownedBy);
						prepStmt.setString(2,"Rejected");
						rs = prepStmt.executeQuery();
						while (rs.next()){
							myRejectedRequirements = rs.getInt("myRejectedRequirements");
						}
						rs.close();
						prepStmt.close();
				}			
			}
			
				
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
		return (myRejectedRequirements);
	}	
	
	
	public static ArrayList getRequirementsThatAreRejected(SecurityProfile securityProfile, String owner, String databaseType, int projectId, String dashboardType, String ownedBy){
		
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			//
			// This sql gets the list of requirements of this type and puts them in the arrray list.
			// creates a requirement object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "";
			
			
			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					// i.e you can never have ALL Users calling All Projects.
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\",  " +
							" datediff(now() , submitted_for_approval_dt) 'daysSinceSubmittedForApproval' , " +
							" datediff(now() , last_approval_reminder_sent_dt) 'daysSinceLastApprovalReminder' " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and r.status = ? " +
							" and r.project_id = p.id " +
							" and p.archived  = 0 " +
							" and r.project_id = " + projectId ;
						 sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
						 sql += " limit 5000 ";
						 
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\", " +
						" sysdate - submitted_for_approval_dt 'daysSinceSubmittedForApproval' , " +
						" sysdate - last_approval_reminder_sent_dt 'daysSinceLastApprovalReminder'  " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f , gr_projects p " +
						" where  r.requirement_type_id = rt.id " +
						" and r.folder_id = f.id " +
						" and r.deleted = 0 " +
						" and r.status = ? " +
						" and rownum < 5000 " +
						" and r.project_id = p.id " +
						" and p.archived  = 0 "+
						" and r.project_id = " + projectId ;
						
						sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
					}
				
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1,"Rejected");

					
				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f, gr_projects p  " +
							" where  r.requirement_type_id = rt.id " +
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and r.owner = ? " +
							" and r.status = ? " +
							" and r.project_id = p.id " +
							" and p.archived = 0 " ;
						 if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						 sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
						 sql += " limit 5000 ";
						 
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f, gr_projects p  " +
						" where  r.requirement_type_id = rt.id " +
						" and r.folder_id = f.id " +
						" and r.deleted = 0 " +
						" and r.owner = ? " +
						" and r.status = ? " +
						" and rownum < 5000 "+
						" and r.project_id = p.id " +
						" and p.archived = 0 " ;
						if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
					}
					
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, ownedBy);
					prepStmt.setString(2,"Rejected");
					
					
				}

			}
			
			
			

			
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				int folderId = rs.getInt("folder_id");
				projectId = rs.getInt("project_id");
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
				//lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId,
					projectId, 
					requirementName, requirementDescription, requirementTag, requirementFullTag,
					version, approvedByAllDt, approvers ,
					requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
					requirementPctComplete, requirementExternalUrl , traceTo, traceFrom, 
					userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy, 
					requirementTypeName, createdDt);
		
				// if the user does not have read permissions on this requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}

				requirements.add(requirement);
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
		return (requirements);

	}

	
	public static int getOrphanRequriementsCount(String owner, int projectId, String dashboardType, String ownedBy){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;

		int myOrphanRequirements = 0; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					// i.e you can never have ALL Users calling All Projects.
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					String sql = " select count(*) 'myOrphanRequirements'  " +
							" from gr_requirements r, gr_requirement_types rt , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and rt.can_be_orphan = 1" + 
							" and (r.trace_to is null or r.trace_to = '')" +
							" and r.deleted = 0 " +
							" and r.project_id = p.id " +
							" and p.archived = 0 "+
							" and r.project_id = " + projectId ;
					prepStmt = con.prepareStatement(sql);
					rs = prepStmt.executeQuery();
					while (rs.next()){
						myOrphanRequirements = rs.getInt("myOrphanRequirements");
					}
					rs.close();
					prepStmt.close();
				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
						
					String sql = " select count(*) 'myOrphanRequirements'  " +
							" from gr_requirements r, gr_requirement_types rt, gr_projects p  " +
							" where r.owner  = ? " +
							" and r.requirement_type_id = rt.id " +
							" and rt.can_be_orphan = 1 " + 
							" and (r.trace_to is null or r.trace_to = '')" +
							" and r.deleted = 0 " +
							" and r.project_id = p.id " +
							" and p.archived = 0 ";
						if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						prepStmt = con.prepareStatement(sql);
						prepStmt.setString(1, ownedBy);
						rs = prepStmt.executeQuery();
						while (rs.next()){
							myOrphanRequirements = rs.getInt("myOrphanRequirements");
						}
						rs.close();
						prepStmt.close();
						
				}			
			}
				
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
		return (myOrphanRequirements);
	}	
	
	
	public static int getTestFailedRequriementsCount(String owner, int projectId, String dashboardType, String ownedBy){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;

		int myTestFailedRequirements = 0; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					// i.e you can never have ALL Users calling All Projects.
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					String sql = " select count(*) 'myTestFailedRequirements'  " +
							" from gr_requirements r, gr_projects p " +
							" where  r.testing_status = 'Fail' " +
							" and r.deleted = 0 " +
							" and r.project_id = p.id " +
							" and p.archived = 0 "+ 
							" and r.project_id = " + projectId ;
					
					
					prepStmt = con.prepareStatement(sql);
					rs = prepStmt.executeQuery();
					while (rs.next()){
						myTestFailedRequirements = rs.getInt("myTestFailedRequirements");
					}
					rs.close();
					prepStmt.close();

				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
						
					String sql = " select count(*) 'myTestFailedRequirements'  " +
							" from gr_requirements r, gr_projects p " +
							" where r.owner  = ? " +
							" and r.testing_status = 'Fail' " +
							" and r.deleted = 0 " +
							" and r.project_id = p.id " +
							" and p.archived = 0 ";
						if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						
						prepStmt = con.prepareStatement(sql);
						prepStmt.setString(1, ownedBy);
						rs = prepStmt.executeQuery();
						while (rs.next()){
							myTestFailedRequirements = rs.getInt("myTestFailedRequirements");
						}
						rs.close();
						prepStmt.close();
						
				}			
			}
			
				
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
		return (myTestFailedRequirements);
	}	
	
	
	public static int getTestPendingRequriementsCount( String owner, int projectId , String dashboardType, String ownedBy){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;

		int myTestPendingRequirements = 0; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					// i.e you can never have ALL Users calling All Projects.
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					String sql = " select count(*) 'myTestPendingRequirements'  " +
							" from gr_requirements r, gr_projects p" +
							" where  r.testing_status = 'Pending' " +
							" and r.deleted = 0 " +
							" and r.project_id = p.id " +
							" and p.archived = 0 " + 
							" and r.project_id = " + projectId ;
				
					prepStmt = con.prepareStatement(sql);
					rs = prepStmt.executeQuery();
					while (rs.next()){
						myTestPendingRequirements = rs.getInt("myTestPendingRequirements");
					}
					rs.close();
					prepStmt.close();

				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
						
					String sql = " select count(*) 'myTestPendingRequirements'  " +
							" from gr_requirements r, gr_projects p" +
							" where r.owner  = ? " +
							" and r.testing_status = 'Pending' " +
							" and r.deleted = 0 " +
							" and r.project_id = p.id " +
							" and p.archived = 0 ";
						if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						prepStmt = con.prepareStatement(sql);
						prepStmt.setString(1, ownedBy);
						rs = prepStmt.executeQuery();
						while (rs.next()){
							myTestPendingRequirements = rs.getInt("myTestPendingRequirements");
						}
						rs.close();
						prepStmt.close();

						
				}			
			}
			
				
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
		return (myTestPendingRequirements);
	}	
		
	
	public static int getSuspectUpStreamRequriementsCount(String owner, int projectId , String dashboardType, String ownedBy){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;

		int mysuspectUpStreamRequirements = 0; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			
			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					// i.e you can never have ALL Users calling All Projects.
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					String sql = " select count(*) 'mySuspectUp'  " +
							" from  gr_requirements r, gr_projects p " +
							" where r.deleted = 0 " +
							" and r.trace_to like '%(s)%' " +
							" and r.project_id = p.id " +
							" and p.archived = 0 " + 
							" and r.project_id = " + projectId ;
					prepStmt = con.prepareStatement(sql);
					rs = prepStmt.executeQuery();
					while (rs.next()){
						mysuspectUpStreamRequirements = rs.getInt("mySuspectUp");
					}
					rs.close();
					prepStmt.close();
				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					String sql = " select count(*) 'mySuspectUp'  " +
						" from  gr_requirements r, gr_projects p " +
						" where r.owner = ? " +
						" and r.deleted = 0 " +
						" and r.trace_to like '%(s)%' " +
						" and r.project_id = p.id " +
						" and p.archived = 0 ";
					if (projectId > 0 ){
						sql += " and r.project_id = " + projectId ;
					}
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, ownedBy);
					rs = prepStmt.executeQuery();
					while (rs.next()){
						mysuspectUpStreamRequirements = rs.getInt("mySuspectUp");
					}
					rs.close();
					prepStmt.close();

				}

			}


			
				
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
		return (mysuspectUpStreamRequirements);
	}

	public static ArrayList getSuspectUpRequriements(SecurityProfile securityProfile, String owner, String databaseType, int projectId , String dashboardType, String ownedBy){
		
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			//
			// This sql gets the list of requirements of this type and puts them in the arrray list.
			// creates a requirement object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "";
			
			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					// i.e you can never have ALL Users calling All Projects.
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and r.trace_to like '%(s)%' " +
							" and r.project_id = p.id " +
							" and p.archived = 0 " + 
							" and r.project_id = " + projectId ;
						 sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
						 sql += " limit 5000 ";
						 
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f , gr_projects p " +
						" where  r.requirement_type_id = rt.id " +
						" and r.folder_id = f.id " +
						" and r.deleted = 0 " +
						" and r.trace_to like '%(s)%' " +
						" and rownum < 5000 " +
						" and r.project_id = p.id " +
						" and p.archived = 0 " + 
						" and r.project_id = " + projectId ;
						
						sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
					}
					
					prepStmt = con.prepareStatement(sql);
			
					
					
				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and r.trace_to like '%(s)%' " +
							" and r.owner = ? " +
							" and r.project_id = p.id " +
							" and p.archived = 0 ";
						 if (projectId > 0 ){
								sql += " and r.project_id = " + projectId ;
						}
						 sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
						 sql += " limit 5000 ";
						 
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f , gr_projects p " +
						" where  r.requirement_type_id = rt.id " +
						" and r.folder_id = f.id " +
						" and r.deleted = 0 " +
						" and r.trace_to like '%(s)%' " +
						" and r.owner = ? " +
						" and rownum < 5000 " +
						" and r.project_id = p.id " +
						" and p.archived = 0 ";
						if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
					}
					
					
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, ownedBy);

					
				}

			}

			
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				int folderId = rs.getInt("folder_id");
				projectId = rs.getInt("project_id");
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
				//lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId,
					projectId, 
					requirementName, requirementDescription, requirementTag, requirementFullTag,
					version, approvedByAllDt, approvers ,
					requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
					requirementPctComplete, requirementExternalUrl , traceTo, traceFrom, 
					userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy, 
					requirementTypeName, createdDt);
				
				
		
				// if the user does not have read permissions on this requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}
				
				requirements.add(requirement);
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
		return (requirements);

	}
	
	public static ArrayList getIncompleteRequriements(SecurityProfile securityProfile, String owner, String databaseType, int projectId, String dashboardType, String ownedBy){
		
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			//
			// This sql gets the list of requirements of this type and puts them in the arrray list.
			// creates a requirement object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "";
			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					// i.e you can never have ALL Users calling All Projects.
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and r.pct_complete < 100 " +
							" and r.project_id = p.id " +
							" and p.archived = 0  " +
							" and r.project_id = " + projectId ;
						
						 sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
						 sql += " limit 5000 ";
						 
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f, gr_projects p  " +
						" where  r.requirement_type_id = rt.id " +
						" and r.folder_id = f.id " +
						" and r.deleted = 0 " +
						" and r.pct_complete < 100  " +
						" and r.project_id = p.id " +
						" and p.archived = 0 " + 				
						" and rownum < 5000 "  + 
						" and r.project_id = " + projectId ;
						
						sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
					}
					
					
					prepStmt = con.prepareStatement(sql);
					rs = prepStmt.executeQuery();
					
				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and r.pct_complete < 100 " +
							" and r.owner = ? " +
							" and r.project_id = p.id " +
							" and p.archived = 0 ";
						 if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						
						 sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
						 sql += " limit 5000 ";
						 
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f, gr_projects p  " +
						" where  r.requirement_type_id = rt.id " +
						" and r.folder_id = f.id " +
						" and r.deleted = 0 " +
						" and r.pct_complete < 100  " +
						" and r.owner = ? " +
						" and r.project_id = p.id " +
						" and p.archived = 0 " + 				
						" and rownum < 5000 " ;
						if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						
						sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
					}
					
					
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, ownedBy);
					rs = prepStmt.executeQuery();
					
				}

			}
			
			
			
			

			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				int folderId = rs.getInt("folder_id");
				projectId = rs.getInt("project_id");
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
				//lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId,
					projectId, 
					requirementName, requirementDescription, requirementTag, requirementFullTag,
					version, approvedByAllDt, approvers ,
					requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
					requirementPctComplete, requirementExternalUrl , traceTo, traceFrom, 
					userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy, 
					requirementTypeName, createdDt);
		
				// if the user does not have read permissions on this requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}

				
				requirements.add(requirement);
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
		return (requirements);

	}

	
	public static ArrayList getDanglingRequriements(SecurityProfile securityProfile, String owner, String databaseType, int projectId , String dashboardType, String ownedBy){
		
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			//
			// This sql gets the list of requirements of this type and puts them in the arrray list.
			// creates a requirement object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "";
			
			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					// i.e you can never have ALL Users calling All Projects.
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and rt.can_be_dangling = 1" + 
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and (r.trace_from is null or r.trace_from = '')" +
							" and r.project_id = p.id " +
							" and p.archived = 0 " + 
							" and r.project_id = " + projectId ;
						 sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
						 sql += " limit 5000 ";
						 
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f , gr_projects p " +
						" where  r.requirement_type_id = rt.id " +
						" and rt.can_be_dangling = 1" + 
						" and r.folder_id = f.id " +
						" and r.deleted = 0 " +
						" and (r.trace_from is null or r.trace_from = '')" +
						" and rownum < 5000 " +
						" and r.project_id = p.id " +
						" and p.archived = 0 " + 
						" and r.project_id = " + projectId ;
						
						sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
					}
					
					prepStmt = con.prepareStatement(sql);
			
					
					
				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and rt.can_be_dangling = 1" + 
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and (r.trace_from is null or r.trace_from = '')" +
							" and r.owner = ? " +
							" and r.project_id = p.id " +
							" and p.archived = 0 ";
						 if (projectId > 0 ){
								sql += " and r.project_id = " + projectId ;
						}
						 sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
						 sql += " limit 5000 ";
						 
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f , gr_projects p " +
						" where  r.requirement_type_id = rt.id " +
						" and rt.can_be_dangling = 1" + 
						" and r.folder_id = f.id " +
						" and r.deleted = 0 " +
						" and (r.trace_from is null or r.trace_from = '')" +
						" and r.owner = ? " +
						" and rownum < 5000 " +
						" and r.project_id = p.id " +
						" and p.archived = 0 ";
						if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
					}
					
					
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, ownedBy);

					
				}

			}
			
			
			

			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				int folderId = rs.getInt("folder_id");
				projectId = rs.getInt("project_id");
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
				//lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId,
					projectId, 
					requirementName, requirementDescription, requirementTag, requirementFullTag,
					version, approvedByAllDt, approvers ,
					requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
					requirementPctComplete, requirementExternalUrl , traceTo, traceFrom, 
					userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy, 
					requirementTypeName, createdDt);
		
				// if the user does not have read permissions on this requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}
				
				
				requirements.add(requirement);
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
		return (requirements);

	}

	public static ArrayList getOrphanRequriements(SecurityProfile securityProfile, String owner, String databaseType, int projectId, String dashboardType, String ownedBy){
		
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			//
			// This sql gets the list of requirements of this type and puts them in the arrray list.
			// creates a requirement object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "";

			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					// i.e you can never have ALL Users calling All Projects.
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and rt.can_be_orphan = 1" + 
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and (r.trace_to is null or r.trace_to = '')" +
							" and r.project_id = p.id " +
							" and p.archived = 0 " + 
							" and r.project_id = " + projectId ;
						 sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
						 sql += " limit 5000 ";
						 
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f , gr_projects p " +
						" where  r.requirement_type_id = rt.id " +
						" and rt.can_be_orphan = 1" + 
						" and r.folder_id = f.id " +
						" and r.deleted = 0 " +
						" and (r.trace_to is null or r.trace_to = '')" +
						" and rownum < 5000 " +
						" and r.project_id = p.id " +
						" and p.archived = 0 " + 
						" and r.project_id = " + projectId ;
						
						sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
					}
					
					prepStmt = con.prepareStatement(sql);
			
					
					
				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and rt.can_be_orphan = 1" + 
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and (r.trace_to is null or r.trace_to = '')" +
							" and r.owner = ? " +
							" and r.project_id = p.id " +
							" and p.archived = 0 ";
						 if (projectId > 0 ){
								sql += " and r.project_id = " + projectId ;
						}
						 sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
						 sql += " limit 5000 ";
						 
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f , gr_projects p " +
						" where  r.requirement_type_id = rt.id " +
						" and rt.can_be_orphan = 1" + 
						" and r.folder_id = f.id " +
						" and r.deleted = 0 " +
						" and (r.trace_to is null or r.trace_to  = '')" +
						" and r.owner = ? " +
						" and rownum < 5000 " +
						" and r.project_id = p.id " +
						" and p.archived = 0 ";
						if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
					}
					
					
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, ownedBy);

					
				}

			}
			
			
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				int folderId = rs.getInt("folder_id");
				projectId = rs.getInt("project_id");
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
				//lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId,
					projectId, 
					requirementName, requirementDescription, requirementTag, requirementFullTag,
					version, approvedByAllDt, approvers ,
					requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
					requirementPctComplete, requirementExternalUrl , traceTo, traceFrom, 
					userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy, 
					requirementTypeName, createdDt);
		
				// if the user does not have read permissions on this requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}
				
				
				requirements.add(requirement);
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
		return (requirements);

	}
	
	
	public static ArrayList getTestPendingRequriements(SecurityProfile securityProfile, String owner, String databaseType, int projectId, String dashboardType, String ownedBy){
		
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			//
			// This sql gets the list of requirements of this type and puts them in the arrray list.
			// creates a requirement object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "";
			
			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					// i.e you can never have ALL Users calling All Projects.
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and r.testing_status = 'Pending' " +
							" and r.project_id = p.id " +
							" and p.archived = 0 " + 
							" and r.project_id = " + projectId ;
						
						 sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
						 sql += " limit 5000 ";
						 
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f , gr_projects p " +
						" where  r.requirement_type_id = rt.id " +
						" and r.folder_id = f.id " +
						" and r.deleted = 0 " +
						" and r.testing_status = 'Pending'  " +
						" and r.project_id = p.id " +
						" and p.archived = 0 " +
						" and rownum < 5000 " + 
						" and r.project_id = " + projectId ;
						
						sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
					}
					
					

					prepStmt = con.prepareStatement(sql);
				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and r.testing_status = 'Pending' " +
							" and r.owner = ? " +
							" and r.project_id = p.id " +
							" and p.archived = 0 ";
						 if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						 sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
						 sql += " limit 5000 ";
						 
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f , gr_projects p " +
						" where  r.requirement_type_id = rt.id " +
						" and r.folder_id = f.id " +
						" and r.deleted = 0 " +
						" and r.testing_status = 'Pending'  " +
						" and r.owner = ? " +
						" and r.project_id = p.id " +
						" and p.archived = 0 " +
						" and rownum < 5000 ";
						if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
					}
					
					

					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, ownedBy);
				}

			}
			
			
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				int folderId = rs.getInt("folder_id");
				projectId = rs.getInt("project_id");
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
				//lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId,
					projectId, 
					requirementName, requirementDescription, requirementTag, requirementFullTag,
					version, approvedByAllDt, approvers ,
					requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
					requirementPctComplete, requirementExternalUrl , traceTo, traceFrom, 
					userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy, 
					requirementTypeName, createdDt);
		
				
				// if the user does not have read permissions on this requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}

				
				requirements.add(requirement);
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
		return (requirements);

	}

	
	public static ArrayList getTestFailedRequriements(SecurityProfile securityProfile, String owner, String databaseType, int projectId, String dashboardType, String ownedBy){
		
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			//
			// This sql gets the list of requirements of this type and puts them in the arrray list.
			// creates a requirement object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "";
			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					// i.e you can never have ALL Users calling All Projects.
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and r.testing_status = 'Fail' " +
							" and r.project_id = p.id " +
							" and p.archived = 0 " + 
							" and r.project_id = " + projectId ;
						
						
						 sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
						 sql += " limit 5000 ";
						 
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f , gr_projects p " +
						" where  r.requirement_type_id = rt.id " +
						" and r.folder_id = f.id " +
						" and r.deleted = 0 " +
						" and r.testing_status = 'Fail'  " +
						" and r.project_id = p.id " +
						" and p.archived = 0 " + 
						" and rownum < 5000 " + 
						" and r.project_id = " + projectId ;
						
						sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
					}
					
					

					prepStmt = con.prepareStatement(sql);
					
				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and r.testing_status = 'Fail' " +
							" and r.owner = ? "+
							" and r.project_id = p.id " +
							" and p.archived = 0 ";
						 if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						
						 sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
						 sql += " limit 5000 ";
						 
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f , gr_projects p " +
						" where  r.requirement_type_id = rt.id " +
						" and r.folder_id = f.id " +
						" and r.deleted = 0 " +
						" and r.testing_status = 'Fail'  " +
						" and r.owner = ? " +
						" and r.project_id = p.id " +
						" and p.archived = 0 " + 
						" and rownum < 5000 ";
						if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						
						sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
					}
					
					

					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, ownedBy);				}

			}
			

			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				int folderId = rs.getInt("folder_id");
				projectId = rs.getInt("project_id");
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
				//lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId,
					projectId, 
					requirementName, requirementDescription, requirementTag, requirementFullTag,
					version, approvedByAllDt, approvers ,
					requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
					requirementPctComplete, requirementExternalUrl , traceTo, traceFrom, 
					userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy, 
					requirementTypeName, createdDt);
		
				// if the user does not have read permissions on this requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}

				
				
				requirements.add(requirement);
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
		return (requirements);

	}

	
	public static ArrayList getMyRecentlyChangedRequriements(String owner, int changedSince, String databaseType, int projectId){
		
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			//
			// This sql gets the list of requirements of this type and puts them in the arrray list.
			// creates a requirement object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			
			// A stake holder is defined as one who has owned, commented on or is an approver of a requirement. 
			// So this is 3 sqls unioned.
			String sql = "";
			if (databaseType.equals("mySQL")){

				 
				 sql = " SELECT distinct r.id, r.requirement_type_id, r.folder_id, " +
					" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
					" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM gr_requirements r , gr_requirement_types rt , gr_folders f, gr_projects p  " +
					" where  r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.deleted = 0 " +
					//" and r.version > 1 " + 
					" and datediff(curdate() , r.last_modified_dt) <  ? " +
					" and r.owner = ? " +
					" and r.project_id = p.id " +
					" and p.archived = 0 " ;
				 
				 if (projectId > 0 ){
					sql += " and r.project_id = " + projectId ;
				}
				 
				 sql += " union ";
				 
				 sql += " SELECT distinct r.id, r.requirement_type_id, r.folder_id, " +
					" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
					" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_requirement_approval_h rah, gr_projects p  " +
					" where  r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.deleted = 0 " +
					" and r.version > 1 " + 
					" and datediff(curdate() , r.last_modified_dt) <  ? " +
					" and r.id = rah.requirement_id " +
					" and rah.approver_email_id = ? " +
					" and r.project_id = p.id " +
					" and p.archived = 0 " ;
				 
				 
				 if (projectId > 0 ){
					sql += " and r.project_id = " + projectId ;
				}
				 
				 
				 sql += " union ";
				 
				 sql += " SELECT distinct r.id, r.requirement_type_id, r.folder_id, " +
					" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
					" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_requirement_comments  rc, gr_projects p " +
					" where  r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.deleted = 0 " +
					" and r.version > 1 " + 
					" and datediff(curdate() , r.last_modified_dt) <  ? " +
					" and r.id = rc.requirement_id " +
					" and rc.commenter_email_id = ? " +
					" and r.project_id = p.id " +
					" and p.archived = 0 " ;
				  ;
				 
				 if (projectId > 0 ){
					sql += " and r.project_id = " + projectId ;
				}
				 
				 
				 sql += " order by project_id , last_modified_dt desc " +
							" limit 50 ";
			}
			else {
				
				
				// oracle
				
				sql = " SELECT distinct r.id, r.requirement_type_id, r.folder_id, " +
				" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
				" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
				" r.approvers ," + 
				" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
				" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
				" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
				" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
				" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f , gr_projects p " +
				" where  r.requirement_type_id = rt.id " +
				" and r.folder_id = f.id " +
				" and r.deleted = 0 " +
				" and r.version > 1 " + 
				" and (sysdate - r.last_modifed_dt) < ? " +
				" and r.owner = ? " +
				" and rownum < 50 " +
				" and r.project_id = p.id " +
				" and p.archived = 0 " ;
			 
				
				if (projectId > 0 ){
					sql += " and r.project_id = " + projectId ;
				}
				
				sql += " union "; 

				sql += " SELECT distinct r.id, r.requirement_type_id, r.folder_id, " +
				" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
				" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
				" r.approvers ," + 
				" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
				" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
				" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
				" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
				" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f, gr_requirement_approval_h rah, gr_projects p  " +
				" where  r.requirement_type_id = rt.id " +
				" and r.folder_id = f.id " +
				" and r.deleted = 0 " +
				" and r.version > 1 " + 
				" and (sysdate - r.last_modifed_dt) < ? " +
				" and r.id = rah.requirement_id " +
				" and rah.approver_email_id = ? " +
				" and rownum < 50  "+
				" and r.project_id = p.id " +
				" and p.archived = 0 " ;
			 
				
				if (projectId > 0 ){
					sql += " and r.project_id = " + projectId ;
				}
				
		
				sql += " union "; 

				sql += " SELECT distinct  r.id, r.requirement_type_id, r.folder_id, " +
				" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
				" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
				" r.approvers ," + 
				" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
				" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
				" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
				" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
				" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f, gr_requirement_comments rc " +
				" where  r.requirement_type_id = rt.id " +
				" and r.folder_id = f.id " +
				" and r.deleted = 0 " +
				" and r.version > 1 " + 
				" and (sysdate - r.last_modifed_dt) < ? " +
				" and r.id = rc.requirement_id " +
				" and rc.commenter_email_id = ? " +
				" and rownum < 50  "+
				" and r.project_id = p.id " +
				" and p.archived = 0 " ;
			 
				
				if (projectId > 0 ){
					sql += " and r.project_id = " + projectId ;
				}			
				
				
				
				sql += " order by r.project_id  , r.last_modified_dt desc  " ;
			}
			
			

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, changedSince);
			prepStmt.setString(2, owner);
			
			prepStmt.setInt(3, changedSince);
			prepStmt.setString(4, owner);
			
			prepStmt.setInt(5, changedSince);
			prepStmt.setString(6, owner);
			
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				int folderId = rs.getInt("folder_id");
				projectId = rs.getInt("project_id");
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
				//lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId,
					projectId, 
					requirementName, requirementDescription, requirementTag, requirementFullTag,
					version, approvedByAllDt, approvers ,
					requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
					requirementPctComplete, requirementExternalUrl , traceTo, traceFrom, 
					userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy, 
					requirementTypeName, createdDt);
		
				requirements.add(requirement);
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
		return (requirements);

	}
	


	public static ArrayList getRecentlyChangedRequriementsInFolder(int folderId, int changedSince, String databaseType, int projectId){
		
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			//
			// This sql gets the list of requirements of this type and puts them in the arrray list.
			// creates a requirement object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			
			// A stake holder is defined as one who has owned, commented on or is an approver of a requirement. 
			// So this is 3 sqls unioned.
			String sql = "";
			
				 
				 sql = " SELECT distinct r.id, r.requirement_type_id, r.folder_id, " +
					" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
					" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM gr_requirements r , gr_requirement_types rt , gr_folders f, gr_projects p  " +
					" where  r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.deleted = 0 " +
					//" and r.version > 1 " + 
					" and datediff(curdate() , r.last_modified_dt) <  ? " +
					" and r.folder_id = ? " +
					" and r.project_id = p.id " +
					" and p.archived = 0 " ;
				 
				 if (projectId > 0 ){
					sql += " and r.project_id = " + projectId ;
				}
				 
				 
				 
				 
				 
				 
				 
				 sql += " order by project_id , last_modified_dt desc " +
							" limit 50 ";
			
			

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, changedSince);
			prepStmt.setInt(2, folderId);
			
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				projectId = rs.getInt("project_id");
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
				//lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId,
					projectId, 
					requirementName, requirementDescription, requirementTag, requirementFullTag,
					version, approvedByAllDt, approvers ,
					requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
					requirementPctComplete, requirementExternalUrl , traceTo, traceFrom, 
					userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy, 
					requirementTypeName, createdDt);
		
				requirements.add(requirement);
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
		return (requirements);

	}
	

	public static ArrayList getRecentlyCommentedRequriementsInFolder2(int folderId, int commentedSince, String databaseType, int projectId){
		
		ArrayList commentedReqs = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			//
			// This sql gets the list of requirements of this type and puts them in the arrray list.
			// creates a requirement object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "";
				sql = " select distinct r.id " +
					" from gr_requirement_comments rc, gr_users u, gr_requirements r, gr_projects p " +
					" where rc.commenter_email_id = u.email_id " +
					" and rc.requirement_id = r.id " +
					" and r.project_id = p.id " +
					" and datediff(curdate(), comment_dt) < ?  " +
					" and r.deleted = 0 " +
					" and p.archived = 0 "
					+ " and r.folder_id = ? " ;
				
				if (projectId > 0 ){
						sql += " and r.project_id = " + projectId ;
					}
					
										
					
					
					sql += 	" order by r.project_id , r.tag_level1, r.tag_level2,r.tag_level3,  r.tag,  rc.comment_dt desc " +
								" limit 50 ";
			
			
			

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, commentedSince);
			prepStmt.setInt(2, folderId);
			
			
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				Integer requirementId = new Integer( rs.getInt("id"));
				
				
				
				commentedReqs.add((Integer)  requirementId);
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
		return (commentedReqs);

	}
	

	public static ArrayList getRecentlyCommentedRequriementsInFolder(int folderId, int commentedSince, String databaseType, int projectId){
		
		ArrayList enhancedComments = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			//
			// This sql gets the list of requirements of this type and puts them in the arrray list.
			// creates a requirement object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "";
				sql = " select distinct u.first_name, u.last_name, r.id, r.full_tag, r.name, p.short_name, rc.comment_note, rc.comment_dt " +
					" from gr_requirement_comments rc, gr_users u, gr_requirements r, gr_projects p " +
					" where rc.commenter_email_id = u.email_id " +
					" and rc.requirement_id = r.id " +
					" and r.project_id = p.id " +
					" and datediff(curdate(), comment_dt) < ?  " +
					" and r.deleted = 0 " +
					" and p.archived = 0 "
					+ " and r.folder_id = ? " ;
				
				if (projectId > 0 ){
						sql += " and r.project_id = " + projectId ;
					}
					
										
					
					
					sql += 	" order by r.project_id , rc.comment_dt desc " +
								" limit 50 ";
			
			
			

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, commentedSince);
			prepStmt.setInt(2, folderId);
			
			
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				int requirementId = rs.getInt("id");
				String fullTag = rs.getString("full_tag");
				String reqName = rs.getString("name");
				String projectPrefix = rs.getString("short_name");
				String commentNote = rs.getString("comment_note");
				String commentDt = rs.getString("comment_dt");
				
				
				String enhancedComment  = firstName + ":##:" + lastName + ":##:" + requirementId + ":##:" + 
				fullTag + ":##:" + reqName  + ":##:" + projectPrefix + ":##:" + commentNote + ":##:" + commentDt;
				enhancedComments.add(enhancedComment);
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
		return (enhancedComments);

	}
	
	
	public static ArrayList getMyRecentlyCommentedRequriements(String owner, int commentedSince, String databaseType, int projectId){
		
		ArrayList enhancedComments = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			//
			// This sql gets the list of requirements of this type and puts them in the arrray list.
			// creates a requirement object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " select distinct u.first_name, u.last_name, r.id, r.full_tag, r.name, p.short_name, rc.comment_note, rc.comment_dt " +
					" from gr_requirement_comments rc, gr_users u, gr_requirements r, gr_projects p " +
					" where rc.commenter_email_id = u.email_id " +
					" and rc.requirement_id = r.id " +
					" and r.project_id = p.id " +
					" and datediff(curdate(), comment_dt) < ?  " +
					" and r.deleted = 0 " +
					" and p.archived = 0 " +
						" and r.id in (" +
						"	select id from gr_requirements where owner = ? " +
						" 	union" +
						"	select requirement_id from gr_requirement_approval_h where approver_email_id = ? " +
						"	union" +
						"	select requirement_id from gr_requirement_comments where commenter_email_id = ? " +
						" ) " ;
					if (projectId > 0 ){
						sql += " and r.project_id = " + projectId ;
					}
					
										
					
					
					sql += 	" order by r.project_id , rc.comment_dt desc " +
								" limit 50 ";
			
			}
			else {
				
				sql = " select distinct u.first_name, u.last_name, r.id, r.full_tag, r.name, p.short_name, rc.comment_note, rc.comment_dt " +
						" from gr_requirement_comments rc, gr_users u, gr_requirements r, gr_projects p " +
						" where rc.commenter_email_id = u.email_id " +
						" and rc.requirement_id = r.id " +
						" and r.project_id = p.id " +
						" and (sysdate - rc.comment_dt) < ?    " +
						" and r.deleted = 0 " +
						" and p.archived = 0 " +
							" and r.id in (" +
							"	select id from gr_requirements where owner = ? " +
							" 	union" +
							"	select requirement_id from gr_requirement_approval_h where approver_email_id = ? " +
							"	union" +
							"	select requirement_id from gr_requirement_comments where commenter_email_id = ? " +
							" ) " ;
					if (projectId > 0 ){
						sql += " and r.project_id = " + projectId ;
					}
					
					sql += " order by r.project_id , rc.comment_dt desc " +
									" limit 50 ";

				}
			
			

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, commentedSince);
			prepStmt.setString(2, owner);
			prepStmt.setString(3, owner);
			prepStmt.setString(4, owner);
			
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				int requirementId = rs.getInt("id");
				String fullTag = rs.getString("full_tag");
				String reqName = rs.getString("name");
				String projectPrefix = rs.getString("short_name");
				String commentNote = rs.getString("comment_note");
				String commentDt = rs.getString("comment_dt");
				
				
				String enhancedComment  = firstName + ":##:" + lastName + ":##:" + requirementId + ":##:" + 
				fullTag + ":##:" + reqName  + ":##:" + projectPrefix + ":##:" + commentNote + ":##:" + commentDt;
				enhancedComments.add(enhancedComment);
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
		return (enhancedComments);

	}
	

	
	public static ArrayList getMyRecentlyCommentedRequriements2(String owner, int commentedSince, String databaseType, int projectId){
		
		ArrayList commentedReqs = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			//
			// This sql gets the list of requirements of this type and puts them in the arrray list.
			// creates a requirement object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "";
				sql = " select distinct r.id " +
					" from gr_requirement_comments rc, gr_users u, gr_requirements r, gr_projects p " +
					" where rc.commenter_email_id = u.email_id " +
					" and rc.requirement_id = r.id " +
					" and r.project_id = p.id " +
					" and datediff(curdate(), comment_dt) < ?  " +
					" and r.deleted = 0 " +
					" and p.archived = 0 " +
						" and r.id in (" +
						"	select id from gr_requirements where owner = ? " +
						" 	union" +
						"	select requirement_id from gr_requirement_approval_h where approver_email_id = ? " +
						"	union" +
						"	select requirement_id from gr_requirement_comments where commenter_email_id = ? " +
						" ) " ;
					if (projectId > 0 ){
						sql += " and r.project_id = " + projectId ;
					}
					
										
					
					
					sql += 	" order by r.project_id ,  r.tag_level1, r.tag_level2,r.tag_level3,  r.tag,  rc.comment_dt desc " +
								" limit 50 ";
			
			
			

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, commentedSince);
			prepStmt.setString(2, owner);
			prepStmt.setString(3, owner);
			prepStmt.setString(4, owner);
			
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				Integer requirementId = new Integer( rs.getInt("id"));
				commentedReqs.add(requirementId);
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
		return (commentedReqs);

	}
	

	
	public static int getSuspectDownStreamRequriementsCount(String owner, int projectId , String dashboardType, String ownedBy){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;

		int mysuspectDownStreamRequirements = 0; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			
			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					// i.e you can never have ALL Users calling All Projects.
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					String sql = " select count(*) 'mySuspectDown'  " +
							" from  gr_requirements r, gr_projects p " +
							" where r.deleted = 0 " +
							" and r.trace_from like '%(s)%' " +
							" and r.project_id = p.id " +
							" and p.archived = 0 " + 
							" and r.project_id = " + projectId ;
					prepStmt = con.prepareStatement(sql);

					rs = prepStmt.executeQuery();
					while (rs.next()){
						mysuspectDownStreamRequirements = rs.getInt("mySuspectDown");
					}
					rs.close();
					prepStmt.close();
				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					String sql = " select count(*) 'mySuspectDown'  " +
						" from  gr_requirements r, gr_projects p " +
						" where r.owner = ? " +
						" and r.deleted = 0 " +
						" and r.trace_from like '%(s)%' " +
						" and r.project_id = p.id " +
						" and p.archived = 0 ";
					if (projectId > 0 ){
						sql += " and r.project_id = " + projectId ;
					}
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, ownedBy);
					rs = prepStmt.executeQuery();
					while (rs.next()){
						mysuspectDownStreamRequirements = rs.getInt("mySuspectDown");
					}
					rs.close();
					prepStmt.close();

				}

			}


			
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
		return (mysuspectDownStreamRequirements);
	}
	
	
	public static ArrayList getSuspectDownRequriements(SecurityProfile securityProfile, String owner, String databaseType, int projectId , String dashboardType, String ownedBy){
		
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			
			//
			// This sql gets the list of requirements of this type and puts them in the arrray list.
			// creates a requirement object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "";
			
			
			if (dashboardType.equals("ProjectDashboard")){
				if (ownedBy.equals("All Users")){
					// SECURITY : The calling routine ensures that when projectId = 0 (all projects), then ownedBy is defaulted to the calling user. 
					
					// i.e you can never have ALL Users calling All Projects.
					
					// then we show all the requirements in this project, owned by ALL users.
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and r.trace_from like '%(s)%' " +
							" and r.project_id = p.id " +
							" and p.archived = 0 " + 
							" and r.project_id = " + projectId ;
						 sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
						 sql += " limit 5000 ";
						 
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f , gr_projects p " +
						" where  r.requirement_type_id = rt.id " +
						" and r.folder_id = f.id " +
						" and r.deleted = 0 " +
						" and r.trace_from like '%(s)%' " +
						" and rownum < 5000 " +
						" and r.project_id = p.id " +
						" and p.archived = 0 " + 
						" and r.project_id = " + projectId ;
						
						sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
					}
					
					prepStmt = con.prepareStatement(sql);
			
					
					
				}
				else {
					// this is a for a particular user. So we need to narrow the scope .
					// we are trying to find all requirements in this project (or across all projects)
					// that are pending approval by this user.
					if (databaseType.equals("mySQL")){
						 sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
							" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
							" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
							" r.approvers ," + 
							" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
							" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
							" r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
							" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
							" FROM gr_requirements r , gr_requirement_types rt , gr_folders f , gr_projects p " +
							" where  r.requirement_type_id = rt.id " +
							" and r.folder_id = f.id " +
							" and r.deleted = 0 " +
							" and r.trace_from like '%(s)%' " +
							" and r.owner = ? " +
							" and r.project_id = p.id " +
							" and p.archived = 0 ";
						 if (projectId > 0 ){
								sql += " and r.project_id = " + projectId ;
						}
						 sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
						 sql += " limit 5000 ";
						 
					}
					else {
						sql = " SELECT r.id, r.requirement_type_id, r.folder_id, " +
						" r.project_id, r.parent_full_tag, r.name, r.description, r.tag, r.full_tag, " +
						" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, " +
						" r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
						" FROM gr_requirements r , gr_requirement_types rt  , gr_folders f , gr_projects p " +
						" where  r.requirement_type_id = rt.id " +
						" and r.folder_id = f.id " +
						" and r.deleted = 0 " +
						" and r.trace_from like '%(s)%' " +
						" and r.owner = ? " +
						" and rownum < 5000 " +
						" and r.project_id = p.id " +
						" and p.archived = 0 ";
						if (projectId > 0 ){
							sql += " and r.project_id = " + projectId ;
						}
						sql += " order by r.project_id , r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4,  r.tag";
					}
					
					
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, ownedBy);

					
				}

			}
			
			

			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				int folderId = rs.getInt("folder_id");
				projectId = rs.getInt("project_id");
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
				//lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId,
					projectId, 
					requirementName, requirementDescription, requirementTag, requirementFullTag,
					version, approvedByAllDt, approvers ,
					requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
					requirementPctComplete, requirementExternalUrl , traceTo, traceFrom, 
					userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy, 
					requirementTypeName, createdDt);
		
				// if the user does not have read permissions on this requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}

				requirements.add(requirement);
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
		return (requirements);

	}
	


	
	public static int getNumberOfUsersByLicenseType(String databaseType, String licenseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;

		int numberOfUsers = 0; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// if licenseType is Trial, then we need to move the expired trial  users into the expired category.
			String sql = "";
			if (licenseType.equals("readWrite")){
				sql = "select count(*) 'numberOfUsers' from gr_users where user_type = 'readWrite' " ;
			}
			if (licenseType.equals("trial")){
				// users who are under the trial license adn within the trial period.
				if (databaseType.equals("mySQL")){
					sql = "select count(*) 'numberOfUsers' " +
							" from  gr_users u " + 
							" where user_type = 'trial' " +
							" and ifnull(datediff(u.account_expire_dt, now()),0) > 0 " ;
				}
				else {
					sql = "select count(*) 'numberOfUsers' " +
					" from  gr_users u " + 
					" where user_type = 'trial' " +
					" and nvl((u.account_expire_dt - sysdate),0) > 0 " ;
				}	
			}

			if (licenseType.equals("expired")){
				// user is either expired or a trial user who has out lived trial period.
				if (databaseType.equals("mySQL")){
					sql = "select count(*) 'numberOfUsers' " +
							" from  gr_users u " + 
							" where user_type = 'expired' " +
							" or " +
							" (user_type = 'trial' and ifnull(datediff(u.account_expire_dt, now()),0) <= 0 )" ;
				}
				else {
					sql = "select count(*) 'numberOfUsers' " +
						" from  gr_users u " + 
						" where user_type = 'expired' " +
						" or " +
						" (user_type = 'trial' and nvl((u.account_expire_dt - sysdate),0) <= 0) " ;
				}	
			}
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				numberOfUsers= rs.getInt("numberOfUsers");
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
		return (numberOfUsers);
	}
	


	
	public static String getFoldersEnabledForApprovalWorkFlow(int projectId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;

		String foldersEnabledForApprovalWorkFlow = ""; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// if licenseType is Trial, then we need to move the expired trial  users into the expired category.
			String sql = "	select f.id " +
					" from gr_projects p, gr_requirement_types rt, gr_folders f " +
					" where p.id = ? " +
					" and p.id = rt.project_id" +
					" and rt.id = f.requirement_type_id  " +
					" and  rt.enable_approval = 1 ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int folderId = rs.getInt("id");
				foldersEnabledForApprovalWorkFlow  += "#" + Integer.toString(folderId) + "#";
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
		return (foldersEnabledForApprovalWorkFlow);
	}
	


	

	



	
	public static void deleteWebForm(int webFormId){
		java.sql.Connection con =  null;
		PreparedStatement prepStmt = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// if licenseType is Trial, then we need to move the expired trial  users into the expired category.
			String sql = "	delete from gr_webforms  where id = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, webFormId);
			prepStmt.execute();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		return;
	}
	

	
	public static String removeHTML(String inputString){
		String outputString  = "";
		try {
			if (inputString != null) {
				inputString = inputString.replace("<br>","\n");
				inputString = inputString.replaceAll("\\<.*?>","");
				inputString = inputString.replaceAll("&nbsp;", " ");
			}
			outputString = inputString;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return (outputString);
	}
	
}
