package com.gloree.beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;

import com.gloree.utils.ProjectUtil;

//GloreeJava2



// This class is used to store an object of Word Template.

public class WordTemplateSN {

	private int templateId;
	
	private String fileType;

	private String templateName;
	private String templateDescription;
	private String templateFilePath;
	
	private String outputFormat;
	private String displayAttributes;
	
	private String createdBy;
	private String createdDt;
	private String lastModifiedBy;
	private String lastModifiedDt;
	
	
	
	
	// The following method is called when the project core values are known and the system is only
	// interested in them. 
	public WordTemplateSN (int templateId, String fileType,   String templateName,
		String templateDescription, String templateFilePath, 
		 String outputFormat, String displayAttributes,
		String createdBy, String createdDt, String lastModifiedBy, String lastModifiedDt){
		
		this.fileType = fileType;
		this.templateId = templateId;
		this.templateName = templateName;
		this.templateDescription = templateDescription;
		this.templateFilePath = templateFilePath;
		
		this.outputFormat = outputFormat;
		this.displayAttributes = displayAttributes;
		
		this.createdBy = createdBy;
		this.createdDt = createdDt;
		this.lastModifiedBy = lastModifiedBy;
		this.lastModifiedDt = lastModifiedDt;
	}
	
	// this constructor creates the template in the db, gets the template Id and creates the bean 
	// with those values.

	public WordTemplateSN( String fileType,  String templateName,
			String templateDescription, String templateFilePath, 
			 String outputFormat, String displayAttributes,
			String createdBy, String lastModifiedBy){
		// Create a template
		 java.sql.Connection con = null; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

				
			String sql = "";
			
			sql = " insert into gr_sn_word_templates (file_type, name,  description, " +
			"  file_path,  output_format, display_attributes,"
			+ " created_by, created_dt, last_modified_by, last_modified_dt)" +
			" values (?, ?,?,?,"
			+ " ?, ? ,"
			+ " ? ,now(), ?, now())  ";
			
						
			PreparedStatement prepStmt = con.prepareStatement(sql);

			prepStmt.setString(1, fileType);
			prepStmt.setString(2, templateName);
			prepStmt.setString(3, templateDescription);
			prepStmt.setString(4, templateFilePath);
			
			prepStmt.setString(5, outputFormat);
			prepStmt.setString(6, displayAttributes);
			
			
			prepStmt.setString(7, createdBy);
			prepStmt.setString(8, lastModifiedBy);
			
			prepStmt.execute();
			prepStmt.close();
			
			
			// lets get the last inserted id
			sql = "select last_insert_id() as last_id";
			prepStmt = con.prepareStatement(sql);
			ResultSet rs = prepStmt.executeQuery();
			int lastId = 0;
			while (rs.next()){
				lastId = rs.getInt("last_id");
			}
			
			
			
			sql = "select id, file_type, name,  description,  file_path, "
					+ " output_format, display_attributes,"
					+ " created_by," +
				" date_format(created_dt, '%d %M %Y %r ') \"created_dt\" ," +
				" last_modified_by," +
				" date_format(last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
				" from gr_sn_word_templates " +
				" where id  = ? ";
		
						
			 prepStmt = con.prepareStatement(sql);
			 prepStmt.setInt(1, lastId);
			rs = prepStmt.executeQuery();
			
			while (rs.next()) {
				
				this.templateId = rs.getInt("id");
				
				this.fileType = rs.getString("file_type");
				this.templateName = rs.getString("name");
				this.templateDescription = rs.getString("name");
				this.templateFilePath = rs.getString("file_path");
				
				this.outputFormat = rs.getString("output_format");
				this.displayAttributes  = rs.getString("display_attributes");
				
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
	 


	// This constructors takes  templateId, goes to the db , gets the data
	// and creates the bean.
	public WordTemplateSN(int templateId  ){
		// Create a template
		 java.sql.Connection con = null; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			

			String sql = "select id,file_type,   name,  description,  file_path, "
					+ "   output_format, display_attributes,"
					+ " created_by," +
				" date_format(created_dt, '%d %M %Y %r ') \"created_dt\" ," +
				" last_modified_by," +
				" date_format(last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
				" from gr_sn_word_templates " +
				" where id  = ? ";
		
						
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, templateId);
			
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				
				this.templateId = rs.getInt("id");
				this.fileType = rs.getString("file_type");
				this.templateName = rs.getString("name");
				this.templateDescription = rs.getString("name");
				this.templateFilePath = rs.getString("file_path");
				
				this.outputFormat = rs.getString("output_format");
				this.displayAttributes  = rs.getString("display_attributes");
				
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
	
	
	
	
	
	
	
	
	
	
	
	
	public int getTemplateId(){
		return this.templateId;
	}
	
	

	public String getFileType(){
		return this.fileType;
	}
	
	public String getTemplateName(){
		return this.templateName;
	}
	
	
	public String getTemplateDescription () {
		return this.templateDescription;
	}

	public String getTemplateFilePath () {
		return this.templateFilePath;
	}
	
	
	
	public String getOutputFormat () {
		return this.outputFormat;
	}
	
	
	public String getDisplayAttributes () {
		return this.displayAttributes;
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
