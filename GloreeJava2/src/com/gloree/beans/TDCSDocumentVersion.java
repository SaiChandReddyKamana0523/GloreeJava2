package com.gloree.beans;

//GloreeJava2

import com.gloree.utils.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.naming.InitialContext;

public class TDCSDocumentVersion {

	
	private int documentId;
	private int projectId;
	private int folderId;
	private String folderPath;
	private int versionNumber;
	private String versionAuthor;
	private int tag;
	private String fullTag;
	private String title;
	private String description;
	private int versionId;
	private String versionDocumentStatus;
	private String versionApprovalStatus;
	private String versionDocumentStatusBy;
	private String versionNotes;
	private String versionFileType;
	private String versionFilePath;
	private String versionSourceLog;
	private String versionCreatedBy;
	private String versionCreatedDt;
	private String versionLastModifiedBy;
	private String versionLastModifiedDt;
	
	
	
	// The following method is called when the Baseline core values are already known and the system is only
	// interested in creating a bean. . 
	public TDCSDocumentVersion (int documentId, int projectId, int folderId, String folderPath,
	int versionNumber,
	String versionAuthor, int tag, String fullTag, String title, String description,int versionId,
	String versionDocumentStatus, String versionApprovalStatus,
	String versionDocumentStatusBy,String versionNotes, String versionFileType,
	String versionFilePath,  String versionSourceLog, String versionCreatedBy,
	String versionCreatedDt,
	String versionLastModifiedBy,String versionLastModifiedDt
	){
		this.documentId = documentId;
		this.projectId = projectId;
		this.folderId = folderId;
		this.folderPath = folderPath;
		this.versionNumber = versionNumber;
		this.versionAuthor = versionAuthor;
		this.tag = tag;
		this.fullTag = fullTag;
		this.title = title;
		this.description = description;
		this.versionId = versionId;
		this.versionDocumentStatus = versionDocumentStatus;
		this.versionApprovalStatus = versionApprovalStatus;
		this.versionDocumentStatusBy = versionDocumentStatusBy;
		this.versionNotes = versionNotes;
		this.versionFileType = versionFileType;
		this.versionFilePath = versionFilePath;
		this.versionSourceLog = versionSourceLog;
		this.versionCreatedBy = versionCreatedBy;
		this.versionCreatedDt = versionCreatedDt;
		this.versionLastModifiedBy = versionLastModifiedBy;
		this.versionLastModifiedDt = versionLastModifiedDt;
	}
	
	public TDCSDocumentVersion (int documentId, int versionNumber, String databaseType) {

		java.sql.Connection con =  null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now we get the data from the database and populate the bean.
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path, " +
					" dv.version_number, " +
					" dv.author, d.tag,  d.full_tag, d.title, d.description, " +
					" dv.id \"version_id\", dv.document_status, dv.approval_status, " +
					" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log, " +
					" dv.created_by, date_format(dv.created_dt, '%d %M %Y %r ') \"created_dt\" ," +
					" dv.last_modified_by, date_format(dv.last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
					" from tdcs_documents d, tdcs_document_versions dv , gr_folders f " +
					" where d.id = ? " + 
					" and d.id = dv.document_id " +
					" and dv.version_number = ? " +
					" and d.folder_id = f.id ";
			}
			else {
				sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path, " +
				" dv.version_number, " +
				" dv.author, d.tag,  d.full_tag, d.title, d.description, " +
				" dv.id \"version_id\", dv.document_status, dv.approval_status, " +
				" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log, " +
				" dv.created_by, to_char(dv.created_dt, 'DD MON YYYY') \"created_dt\" ," +
				" dv.last_modified_by, to_char(dv.last_modified_dt, 'DD MON YYYY') \"last_modified_dt\" " +
				" from tdcs_documents d, tdcs_document_versions dv , gr_folders f " +
				" where d.id = ? " + 
				" and d.id = dv.document_id " +
				" and dv.version_number = ? " +
				" and d.folder_id = f.id ";
			
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, documentId);
			prepStmt.setInt(2,versionNumber);
			ResultSet rs = prepStmt.executeQuery();
	
			if (rs.next()){
				this.documentId = rs.getInt("document_id");
				this.projectId = rs.getInt("project_id");
				this.folderId = rs.getInt("folder_id");
				this.folderPath = rs.getString("folder_path");
				this.versionNumber = rs.getInt("version_number");
				
				this.versionAuthor = rs.getString("author");
				this.tag = rs.getInt("tag");
				this.fullTag =rs.getString("full_tag");
				this.title = rs.getString("title");
				this.description = rs.getString("description");
				
				this.versionId = rs.getInt("version_id");
				this.versionDocumentStatus = rs.getString("document_status");
				this.versionApprovalStatus = rs.getString("approval_status");
				
				this.versionDocumentStatusBy = rs.getString("document_status_by");
				this.versionNotes = rs.getString("notes");
				this.versionFileType = rs.getString("file_type");
				this.versionFilePath = rs.getString("file_path");
				this.versionSourceLog = rs.getString("source_log");
				
				this.versionCreatedBy = rs.getString("created_by");
				this.versionCreatedDt = rs.getString("created_dt");
				this.versionLastModifiedBy = rs.getString("last_modified_by");
				this.versionLastModifiedDt = rs.getString("last_modified_dt");
				
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

	public String getFolderPath(){
		return this.folderPath;
	}
	
	public int getVersionNumber () {
		return this.versionNumber;
	}

	public String getVersionAuthor () {
		return this.versionAuthor;
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

	public int getVersionId () {
		return this.versionId;
	}

	public String getVersionDocumentStatus () {
		return this.versionDocumentStatus;
	}
	
	public String getVersionApprovalStatus () {
		return this.versionApprovalStatus;
	}
	
	public String getVersionDocumentStatusBy () {
		return this.versionDocumentStatusBy;
	}
	
	public String getVersionNotes () {
		return this.versionNotes;
	}
	
	public String getVersionFileType () {
		return this.versionFileType;
	}
	
	public String getVersionFilePath () {
		return this.versionFilePath;
	}
	
	public String getVersionFileName () {
		String filePath = this.versionFilePath;
		return (filePath.substring(filePath.lastIndexOf("/")+1));
	}
	
	public String getVersionSourceLog() {
		return this.versionSourceLog;
	}
	
	public String getVersionCreatedBy () {
		return this.versionCreatedBy;
	}
	
	public String getVersionCreatedDt () {
		return this.versionCreatedDt;
	}
	
	public String getVersionLastModifiedBy () {
		return this.versionLastModifiedBy;
	}
	
	public String getVersionLastModifiedDt() {
		return this.versionLastModifiedDt;
	}
	
	public ArrayList getPreviousVersions(String databaseType){
		ArrayList previousVersions = TDCSUtil.getPreviousVersionsOfDocument(this.documentId,  databaseType);
		return previousVersions;
	}
}
