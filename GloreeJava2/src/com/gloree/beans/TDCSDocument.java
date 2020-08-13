package com.gloree.beans;

//GloreeJava2

import com.gloree.utils.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.naming.InitialContext;

public class TDCSDocument {

	
	private int documentId;
	private int projectId;
	private int folderId;
	private String folderPath;
	private int currentVersionNumber;
	private String currentVersionAuthor;
	private int tag;
	private String fullTag;
	private String title;
	private String description;
	private int currentVersionId;
	private String currentVersionDocumentStatus;
	private String currentVersionApprovalStatus;
	private String currentVersionDocumentStatusBy;
	private String currentVersionNotes;
	private String currentVersionFileType;
	private String currentVersionFilePath;
	private String currentVersionSourceLog;
	private String currentVersionCreatedBy;
	private String currentVersionCreatedDt;
	private String currentVersionLastModifiedBy;
	private String currentVersionLastModifiedDt;
	
	
	
	// The following method is called when the Baseline core values are already known and the system is only
	// interested in creating a bean. . 
	public TDCSDocument (int documentId, int projectId, int folderId, String folderPath, int currentVersionNumber,
	String currentVersionAuthor, int tag, String fullTag, String title, String description,int currentVersionId,
	String currentVersionDocumentStatus, String currentVersionApprovalStatus,
	String currentVersionDocumentStatusBy,String currentVersionNotes, String currentVersionFileType,
	String currentVersionFilePath, String currentVersionSourceLog, String currentVersionCreatedBy,
	String currentVersionCreatedDt,
	String currentVersionLastModifiedBy,String currentVersionLastModifiedDt
	){
		this.documentId = documentId;
		this.projectId = projectId;
		this.folderId = folderId;
		this.folderPath  = folderPath;
		this.currentVersionNumber = currentVersionNumber;
		this.currentVersionAuthor = currentVersionAuthor;
		this.tag = tag;
		this.fullTag = fullTag;
		this.title = title;
		this.description = description;
		this.currentVersionId = currentVersionId;
		this.currentVersionDocumentStatus = currentVersionDocumentStatus;
		this.currentVersionApprovalStatus = currentVersionApprovalStatus;
		this.currentVersionDocumentStatusBy = currentVersionDocumentStatusBy;
		this.currentVersionNotes = currentVersionNotes;
		this.currentVersionFileType = currentVersionFileType;
		this.currentVersionFilePath = currentVersionFilePath;
		this.currentVersionSourceLog = currentVersionSourceLog;
		this.currentVersionCreatedBy = currentVersionCreatedBy;
		this.currentVersionCreatedDt = currentVersionCreatedDt;
		this.currentVersionLastModifiedBy = currentVersionLastModifiedBy;
		this.currentVersionLastModifiedDt = currentVersionLastModifiedDt;
	}
	
	
	// the following method is used when the system knows only the documentId and wants this bean
	// to go and get details from the db to create the bean.
	public TDCSDocument (int documentId, String databaseType) {

		java.sql.Connection con =  null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now we get the data from the database and populate the bean.
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path," +
					" d.current_version_number, " +
					" d.current_version_author, d.tag, d.full_tag, d.title, d.description, " +
					" dv.id \"current_version_id\", dv.document_status, dv.approval_status, " +
					" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log, " +
					" dv.created_by, date_format(dv.created_dt, '%d %M %Y %r ') \"created_dt\" ," +
					" dv.last_modified_by, date_format(dv.last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
					" from tdcs_documents d, tdcs_document_versions dv , gr_folders f " +
					" where d.id = ? " + 
					" and d.id = dv.document_id " +
					" and d.current_version_number = dv.version_number" +
					" and d.folder_id = f.id ";
			}
			else {
				sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path," +
				" d.current_version_number, " +
				" d.current_version_author, d.tag, d.full_tag, d.title, d.description, " +
				" dv.id \"current_version_id\", dv.document_status, dv.approval_status, " +
				" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log, " +
				" dv.created_by, to_char(dv.created_dt, 'DD MON YYYY') \"created_dt\" ," +
				" dv.last_modified_by, to_char(dv.last_modified_dt, 'DD MON YYYY') \"last_modified_dt\" " +
				" from tdcs_documents d, tdcs_document_versions dv , gr_folders f " +
				" where d.id = ? " + 
				" and d.id = dv.document_id " +
				" and d.current_version_number = dv.version_number" +
				" and d.folder_id = f.id ";
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, documentId);
			ResultSet rs = prepStmt.executeQuery();
	
			if (rs.next()){
				this.documentId = rs.getInt("document_id");
				this.projectId = rs.getInt("project_id");
				this.folderId = rs.getInt("folder_id");
				this.folderPath = rs.getString("folder_path");
				this.currentVersionNumber = rs.getInt("current_version_number");
				
				this.currentVersionAuthor = rs.getString("current_version_author");
				this.tag = rs.getInt("tag");
				this.fullTag =rs.getString("full_tag");
				this.title = rs.getString("title");
				this.description = rs.getString("description");
				
				this.currentVersionId = rs.getInt("current_version_id");
				this.currentVersionDocumentStatus = rs.getString("document_status");
				this.currentVersionApprovalStatus = rs.getString("approval_status");
				
				this.currentVersionDocumentStatusBy = rs.getString("document_status_by");
				this.currentVersionNotes = rs.getString("notes");
				this.currentVersionFileType = rs.getString("file_type");
				this.currentVersionFilePath = rs.getString("file_path");
				this.currentVersionSourceLog = rs.getString("source_log");
				
				this.currentVersionCreatedBy = rs.getString("created_by");
				this.currentVersionCreatedDt = rs.getString("created_dt");
				this.currentVersionLastModifiedBy = rs.getString("last_modified_by");
				this.currentVersionLastModifiedDt = rs.getString("last_modified_dt");
				
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

	// the following method is used when the system knows only the documentId and wants this bean
	// to go and get details from the db to create the bean.
	public TDCSDocument (String tDCSDocumentFullTag, int projectId, String databaseType) {

		java.sql.Connection con =  null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now we get the data from the database and populate the bean.
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path, " +
					" d.current_version_number, " +
					" d.current_version_author, d.tag, d.full_tag, d.title, d.description, " +
					" dv.id \"current_version_id\", dv.document_status, dv.approval_status, " +
					" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log, " +
					" dv.created_by, date_format(dv.created_dt, '%d %M %Y %r ') \"created_dt\" ," +
					" dv.last_modified_by, date_format(dv.last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
					" from tdcs_documents d, tdcs_document_versions dv , gr_folders f" +
					" where d.full_tag = ? " +
					" and d.project_id = ? " + 
					" and d.id = dv.document_id " +
					" and d.current_version_number = dv.version_number" +
					" and d.folder_id = f.id ";
			}
			else {
				sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path, " +
				" d.current_version_number, " +
				" d.current_version_author, d.tag, d.full_tag, d.title, d.description, " +
				" dv.id \"current_version_id\", dv.document_status, dv.approval_status, " +
				" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log, " +
				" dv.created_by, to_char(dv.created_dt, 'DD MON YYYY') \"created_dt\" ," +
				" dv.last_modified_by, to_char(dv.last_modified_dt, 'DD MON YYYY') \"last_modified_dt\" " +
				" from tdcs_documents d, tdcs_document_versions dv , gr_folders f" +
				" where d.full_tag = ? " +
				" and d.project_id = ? " + 
				" and d.id = dv.document_id " +
				" and d.current_version_number = dv.version_number" +
				" and d.folder_id = f.id ";
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, tDCSDocumentFullTag);
			prepStmt.setInt(2,projectId);
			ResultSet rs = prepStmt.executeQuery();
	
			if (rs.next()){
				this.documentId = rs.getInt("document_id");
				this.projectId = rs.getInt("project_id");
				this.folderId = rs.getInt("folder_id");
				this.folderPath = rs.getString("folder_path");
				this.currentVersionNumber = rs.getInt("current_version_number");
				
				this.currentVersionAuthor = rs.getString("current_version_author");
				this.tag = rs.getInt("tag");
				this.fullTag =rs.getString("full_tag");
				this.title = rs.getString("title");
				this.description = rs.getString("description");
				
				this.currentVersionId = rs.getInt("current_version_id");
				this.currentVersionDocumentStatus = rs.getString("document_status");
				this.currentVersionApprovalStatus = rs.getString("approval_status");
				
				this.currentVersionDocumentStatusBy = rs.getString("document_status_by");
				this.currentVersionNotes = rs.getString("notes");
				this.currentVersionFileType = rs.getString("file_type");
				this.currentVersionFilePath = rs.getString("file_path");
				this.currentVersionSourceLog = rs.getString("source_log");
				
				this.currentVersionCreatedBy = rs.getString("created_by");
				this.currentVersionCreatedDt = rs.getString("created_dt");
				this.currentVersionLastModifiedBy = rs.getString("last_modified_by");
				this.currentVersionLastModifiedDt = rs.getString("last_modified_dt");
				
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

	public int getDocumentId () {
		return this.documentId;
	}

	public int getProjectId () {
		return this.projectId;
	}
	
	public int getFolderId () {
		return this.folderId;
	}

	public String getFolderPath () {
		return this.folderPath;
	}

	
	public int getCurrentVersionNumber () {
		return this.currentVersionNumber;
	}

	public String getCurrentVersionAuthor () {
		return this.currentVersionAuthor;
	}

	public int getTag(){
		return this.tag;
	}
	
	public String getFullTag() {
		return this.fullTag;
	}

	public String getTitle() {
		return this.title;
	}

	public String getDescription() {
		return this.description;
	}

	public int getCurrentVersionId () {
		return this.currentVersionId;
	}

	public String getCurrentVersionDocumentStatus () {
		return this.currentVersionDocumentStatus;
	}
	
	public String getCurrentVersionApprovalStatus () {
		return this.currentVersionApprovalStatus;
	}
	
	public String getCurrentVersionDocumentStatusBy () {
		return this.currentVersionDocumentStatusBy;
	}
	
	public String getCurrentVersionNotes () {
		return this.currentVersionNotes;
	}
	
	public String getCurrentVersionFileType () {
		return this.currentVersionFileType;
	}
	
	public String getCurrentVersionFilePath () {
		return this.currentVersionFilePath;
	}
	
	
	public String getCurrentVersionFileName () {
		String filePath = this.currentVersionFilePath;
		return (filePath.substring(filePath.lastIndexOf("/")+1));
	}
	
	public String getCurrentVersionSourceLog() {
		return this.currentVersionSourceLog;
	}
	
	public String getCurrentVersionCreatedBy () {
		return this.currentVersionCreatedBy;
	}
	
	public String getCurrentVersionCreatedDt () {
		return this.currentVersionCreatedDt;
	}
	
	public String getCurrentVersionLastModifiedBy () {
		return this.currentVersionLastModifiedBy;
	}
	
	public String getCurrentVersionLastModifiedDt() {
		return this.currentVersionLastModifiedDt;
	}
	
	public ArrayList getPreviousVersions( String databaseType){
		ArrayList previousVersions = TDCSUtil.getPreviousVersionsOfDocument(this.documentId,  databaseType);
		return previousVersions;
	}
	
	public void lockDocument(User user, String databaseType){
		TDCSUtil.lockDocument(this.documentId, this.currentVersionNumber, user,  databaseType);
	}
	public void unlockDocument(User user, String databaseType){
		TDCSUtil.unlockDocument(this.documentId, this.currentVersionNumber, user,  databaseType);
	}
}
