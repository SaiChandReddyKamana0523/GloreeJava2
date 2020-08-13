package com.gloree.beans;

//GloreeJava2

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;


public class RequirementAttachment {

	
	private int requirementAttachmentId;
	private int requirementId;
	private String fileName;
	private String filePath;
	private String title;
	private String createdBy;
	private String createdDt;
	
	
	public RequirementAttachment (int requirementAttachmentId, int requirementId,
		String fileName, String filePath, 
		String title, String createdBy,  String createdDt){
		
		this.requirementAttachmentId = requirementAttachmentId;
		this.requirementId = requirementId;
		this.fileName = fileName;
		this.filePath  = filePath;
		this.title = title;
		this.createdBy = createdBy;
		this.createdDt = createdDt;
	}
	
	// the following method is used when the system knows only the AttachmentId and wants this bean
	// to go and get details from the db to create the bean.
	public RequirementAttachment (int requirementAttachmentId, String databaseType){

		java.sql.Connection con =  null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now we get the data from the database and populate the bean.
			
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " select id , requirement_id, file_name, file_path, title, " + 
				" created_by, date_format(created_dt , '%d %M %Y %r ') \"created_dt\" " +
				" from gr_requirement_attachments  " +
				" where id = ? ";
			}
			else {
				sql = " select id , requirement_id, file_name, file_path, title, " + 
				" created_by, to_char(created_dt , 'DD MON YYYY') \"created_dt\" " +
				" from gr_requirement_attachments  " +
				" where id = ? ";
			}
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementAttachmentId);
			ResultSet rs = prepStmt.executeQuery();
	
			if (rs.next()){
				this.requirementAttachmentId = rs.getInt("id");
				this.requirementId = rs.getInt("requirement_id");
				this.fileName = rs.getString("file_name");
				this.filePath = rs.getString("file_path");
				this.title = rs.getString("title");
				this.createdBy = rs.getString("created_by");				
				this.createdDt = rs.getString("created_dt");
			}
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
	}
		
	
	public int getRequirementAttachmentId(){
		return this.requirementAttachmentId;
	}
	
	public int getRequirementId () {
		return this.requirementId;
	}
	
	public String getFileName(){
		return this.fileName;
	}

	public String getFilePath(){
		return this.filePath;
	}

	public String getTitle(){
		return this.title;
	}

	public String getCreatedBy(){
		return this.createdBy;
	}
	
	public String getCreatedDt(){
		return this.createdDt;
	}
	
}
