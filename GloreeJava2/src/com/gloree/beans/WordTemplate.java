package com.gloree.beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;

import com.gloree.utils.ProjectUtil;

//GloreeJava2



// This class is used to store an object of Word Template.

public class WordTemplate {

	private int templateId;
	private int tDCSDocumentId;
	private int projectId;
	private int folderId;
	private String templateName;
	private String templateVisibility;
	private String templateDescription;
	private String templateFilePath;
	private String createdBy;
	private String createdDt;
	private String lastModifiedBy;
	private String lastModifiedDt;
	
	
	
	// The following method is called when the project core values are known and the system is only
	// interested in them. 
	public WordTemplate (int templateId, int tDCSDocumentId, int projectId, int folderId, String templateName,
		String templateVisibility, String templateDescription, String templateFilePath, 
		String createdBy, String createdDt, String lastModifiedBy, String lastModifiedDt){
		
		this.templateId = templateId;
		this.tDCSDocumentId = tDCSDocumentId;
		this.projectId = projectId;
		this.folderId = folderId;
		this.templateName = templateName;
		this.templateVisibility = templateVisibility;
		this.templateDescription = templateDescription;
		this.templateFilePath = templateFilePath;
		this.createdBy = createdBy;
		this.createdDt = createdDt;
		this.lastModifiedBy = lastModifiedBy;
		this.lastModifiedDt = lastModifiedDt;
	}
	
	// this constructor creates the template in the db, gets the template Id and creates the bean 
	// with those values.

	public WordTemplate(int tDCSDocumentId, int projectId, int folderId, String templateName, String templateVisibility,
			String templateDescription, String templateFilePath, String createdBy,  
			String lastModifiedBy , String databaseType){
		// Create a template
		 java.sql.Connection con = null; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

				
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_word_templates (tdcs_document_id, project_id, folder_id, name, visibility, description, " +
				"file_path, created_by, created_dt, last_modified_by, last_modified_dt)" +
				" values (?,?,?,?, ?, ? , ? ,? ,now(), ?, now())  ";
			}
			else {
				sql = " insert into gr_word_templates (tdcs_document_id, project_id, folder_id, name, visibility, description, " +
				"file_path, created_by, created_dt, last_modified_by, last_modified_dt)" +
				" values (?,?,?,?, ?, ? , ? ,? ,sysdate, ?,sysdate)  ";
			}
						
			PreparedStatement prepStmt = con.prepareStatement(sql);

			prepStmt.setInt(1,tDCSDocumentId);
			prepStmt.setInt(2, projectId);
			prepStmt.setInt(3, folderId);
			prepStmt.setString(4, templateName);
			prepStmt.setString(5, templateVisibility);
			prepStmt.setString(6, templateDescription);
			prepStmt.setString(7, templateFilePath);
			prepStmt.setString(8, createdBy);
			prepStmt.setString(9, lastModifiedBy);
			
			prepStmt.execute();
			prepStmt.close();

			// now lets get the template id, and populate the bean.
			// we are trying to locate the template id, by matching filepath, folder id and name.
			// there is  a .00001% chance that there may be more than one row, but manageable risk.
			// now lets get the role id, by searching on projectId and role name (should be unique)
			if (databaseType.equals("mySQL")){
				sql = "select id, tdcs_document_id, project_id, folder_id, name, visibility, description, file_path, created_by," +
					" date_format(created_dt, '%d %M %Y %r ') \"created_dt\" ," +
					" last_modified_by," +
					" date_format(last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
					" from gr_word_templates " +
					" where project_id = ? " +
					" and folder_id = ? " +
					" and name = ? " +
					" and description = ? " +
					" and file_path = ? " +
					" and created_by = ? ";
			}
			else {
				sql = "select id, tdcs_document_id, project_id, folder_id, name, visibility, description, file_path, created_by," +
				" to_char(created_dt, 'DD MON YYYY') \"created_dt\" ," +
				" last_modified_by," +
				" to_char(last_modified_dt, 'DD MON YYYY') \"last_modified_dt\" " +
				" from gr_word_templates " +
				" where project_id = ? " +
				" and folder_id = ? " +
				" and name = ? " +
				" and description = ? " +
				" and file_path = ? " +
				" and created_by = ? ";
			}
						
			 prepStmt = con.prepareStatement(sql);

			 prepStmt.setInt(1, projectId);
			 prepStmt.setInt(2, folderId);
			 prepStmt.setString(3, templateName);
			 prepStmt.setString(4, templateDescription);
			 prepStmt.setString(5, templateFilePath);
			 prepStmt.setString(6, createdBy);
			 ResultSet rs = prepStmt.executeQuery();
			
			while (rs.next()) {
				
				this.templateId = rs.getInt("id");
				this.tDCSDocumentId = rs.getInt("tdcs_document_id");
				this.projectId = rs.getInt("project_id");
				this.folderId = rs.getInt("folder_id");
				this.templateName = rs.getString("name");
				this.templateVisibility = rs.getString("visibility");
				this.templateDescription = rs.getString("name");
				this.templateFilePath = rs.getString("file_path");
				this.createdBy = rs.getString("created_by");
				this.createdDt = rs.getString("created_dt");
				this.lastModifiedBy = rs.getString("last_modified_by");
				this.lastModifiedDt = rs.getString("last_modified_dt");	
			}
			
			// prior to exiting, we need to make a log entry in the project table.
			ProjectUtil.createProjectLog(projectId, templateName, "Create", 
				"Word Document Created", createdBy,  databaseType);
			
			rs.close();
			prepStmt.close();
			con.close();
			}
			catch (Exception e) {
				// 	TODO Auto-generated catch block
				e.printStackTrace();
			}  finally {
				if (con != null) {
					try {con.close();} catch (Exception e) {}
					con = null;
				}
			}
		}
	 


	// This constructors takes  templateId, goes to the db , gets the data
	// and creates the bean.
	public WordTemplate(int templateId  , String databaseType){
		// Create a template
		 java.sql.Connection con = null; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "select id, tdcs_document_id, project_id, folder_id, name, visibility, description, file_path, created_by," +
				" date_format(created_dt, '%d %M %Y %r ') \"created_dt\" ," +
				" last_modified_by," +
				" date_format(last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
				" from gr_word_templates " +
				" where id = ? ";
			}
			else {
				sql = "select id, tdcs_document_id, project_id, folder_id, name, visibility, description, file_path, created_by," +
				" to_char(created_dt, 'DD MON YYYY') \"created_dt\" ," +
				" last_modified_by," +
				" to_char(last_modified_dt, 'DD MON YYYY') \"last_modified_dt\" " +
				" from gr_word_templates " +
				" where id = ? ";
			}
			

			 PreparedStatement prepStmt = con.prepareStatement(sql);

			 prepStmt.setInt(1, templateId);
			 ResultSet rs = prepStmt.executeQuery();
			
			while (rs.next()) {
				this.templateId = rs.getInt("id");
				this.tDCSDocumentId = rs.getInt("tdcs_document_id");
				this.projectId = rs.getInt("project_id");
				this.folderId = rs.getInt("folder_id");
				this.templateName = rs.getString("name");
				this.templateVisibility = rs.getString("visibility");
				this.templateDescription = rs.getString("description");
				this.templateFilePath = rs.getString("file_path");
				this.createdBy = rs.getString("created_by");
				this.createdDt = rs.getString("created_dt");
				this.lastModifiedBy = rs.getString("last_modified_by");
				this.lastModifiedDt = rs.getString("last_modified_dt");	
			}
			
			rs.close();
			prepStmt.close();
			con.close();
		}
		catch (Exception e) {
			// 	TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
	}
	
	
	
	
	
	
	
	
	public void setTDCSDocumentId(int tDCSDocumentId){
		// Create a template
		 java.sql.Connection con = null; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "update gr_word_templates set tdcs_document_id = ? where id = ? ";
						PreparedStatement prepStmt = con.prepareStatement(sql);

			prepStmt.setInt(1, tDCSDocumentId);
			prepStmt.setInt(2, this.templateId);
			prepStmt.execute();
			
			prepStmt.close();
			con.close();
		}
		catch (Exception e) {
			// 	TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
	}
	
	
	
	public int getTemplateId(){
		return this.templateId;
	}
	
	public int getTDCSDocumentId(){
		return this.tDCSDocumentId;
	}
	public int getProjectId(){
		return this.projectId;
	}
	
	public int getFolderId(){
		return this.folderId;
	}
	
	
	public String getTemplateName(){
		return this.templateName;
	}
	
	public String getTemplateVisibility(){
		return this.templateVisibility;
	}
	
	public String getTemplateDescription () {
		return this.templateDescription;
	}

	public String getTemplateFilePath () {
		return this.templateFilePath;
	}
	
	public String getCreatedBy () {
		return this.createdBy;
	}
	
	public String getCreatedDt () {
		return this.createdDt;
	}

	public String getLastModifiedBy () {
		return this.lastModifiedBy;
	}
	
	public String getLastModifiedDt () {
		return this.lastModifiedDt;
	}
	
}
