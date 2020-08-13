package com.gloree.utils;

//GloreeJava2


import java.util.ArrayList;
import java.util.Calendar;

import com.gloree.beans.*;


import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;

public class TDCSUtil {

	
	// creates a new document in the TDCS system.
	public static TDCSDocument createNewTDCSDocument(String uniqueTDCSFullTag, int projectId , int folderId, 
		String title, String description,
		String fileType, String filePath, User user, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		
		TDCSDocument tDCSDocument = null;
		try {
			
			Folder folder = new Folder(folderId);
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "insert into tdcs_documents(project_id, folder_id, current_version_number," +
				" current_version_author, full_tag," +
				" title, description) " +
				" values(?,?,?," +
				" ?,?," +
				" ?,?)";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
			prepStmt.setInt(3, 1);
			
			prepStmt.setString(4, user.getEmailId());
			prepStmt.setString(5, "TDCS");
			
			prepStmt.setString(6, title);
			prepStmt.setString(7,description);
			prepStmt.execute();
			prepStmt.close();

			// we set the document tag and full tag, slightly differently
			// based on an environment variable . 

			
			int documentId = 0;
			if (uniqueTDCSFullTag.equals("true")){
				// lets get the id of the document we just inserted. we need this for setting the
				// tag and for creating a new version.
				sql = "select max(id) \"id\" from tdcs_documents " +
					" where folder_id = ? " +
					" and title = ? " +
					" and description = ? ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, folderId);
				prepStmt.setString(2, title);
				prepStmt.setString(3, description);
				rs = prepStmt.executeQuery();
				while (rs.next()){
					documentId = rs.getInt("id");
				}
				rs.close();
				prepStmt.close();
				
				// lets set the document tag.
				sql = " update tdcs_documents set tag = ? , full_tag = ? where id = ? ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, documentId);
				prepStmt.setString(2, "TDCS-" + documentId);
				prepStmt.setInt(3, documentId);
				prepStmt.execute();
				prepStmt.close();
			}
			else {
				// lets get the id of the document we just inserted. we need this for setting the
				// tag and for creating a new version.
				sql = "select max(id) \"id\" from tdcs_documents " +
					" where folder_id = ? " +
					" and title = ? " +
					" and description = ? ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, folderId);
				prepStmt.setString(2, title);
				prepStmt.setString(3, description);
				rs = prepStmt.executeQuery();
				while (rs.next()){
					documentId = rs.getInt("id");
				}
				rs.close();
				prepStmt.close();
				
				
				// lets get the max (tag) for this document and bump it up by one. this will be the tag for the 
				// currently inserted document. 
				sql = "select max(tag)+1  \"nextTag\" from tdcs_documents " +
					" where project_id = ? ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				rs = prepStmt.executeQuery();
				int nextTag = 0;
				while (rs.next()){
					nextTag = rs.getInt("nextTag");
				}
				rs.close();
				prepStmt.close();
				

				// lets set the document tag.
				sql = " update tdcs_documents set tag = ? , full_tag = ? where id = ? ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, nextTag);
				prepStmt.setString(2, "TDCS-" + nextTag);
				prepStmt.setInt(3, documentId);
				prepStmt.execute();
				prepStmt.close();
			}

			// lets insert the version info.
			if (databaseType.equals("mySQL")){
				sql = "insert into tdcs_document_versions(document_id, version_number," +
					" author, document_status, approval_status, " +
					" document_status_by, notes, file_type," +
					" file_path, folder_path, source_log, " +
					" created_by, created_dt,  last_modified_by, last_modified_dt )" +
					" values (?,?," +
					" ?,?,?," +
					" ?,?,?," +
					" ?,?,?," +
					" ?,now() ,?,now())";
			}
			else {
				sql = "insert into tdcs_document_versions(document_id, version_number," +
				" author, document_status, approval_status, " +
				" document_status_by, notes, file_type," +
				" file_path, folder_path, source_log, " +
				" created_by, created_dt,  last_modified_by, last_modified_dt )" +
				" values (?,?," +
				" ?,?,?," +
				" ?,?,?," +
				" ?,?,?," +
				" ?,sysdate ,?,sysdate)";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, documentId);
			prepStmt.setInt(2, 1);
				
			prepStmt.setString(3, user.getEmailId());
			prepStmt.setString(4, "unlocked");
			prepStmt.setString(5, "draft");
			
			
			prepStmt.setString(6,user.getEmailId());
			prepStmt.setString(7, description);
			prepStmt.setString(8, fileType);
			
			prepStmt.setString(9,filePath);
			prepStmt.setString(10,folder.getFolderPath());
			prepStmt.setString(11, "Created by " + user.getEmailId() + 
				" on From TraceCloud Data on " + Calendar.getInstance().getTime());
			
			prepStmt.setString(12, user.getEmailId());
			prepStmt.setString(13, user.getEmailId());
			prepStmt.execute();
			
			tDCSDocument = new TDCSDocument(documentId,  databaseType);
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
		return tDCSDocument;
	}	


	// creates a new version of an existing TDCS document in the TDCS system.
	public static void updateExistingTDCSDocument(TDCSDocument tDCSDocument, int projectId , int folderId,
		String description,
		String fileType, String filePath, User user, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			Folder folder = new Folder(folderId);
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// lets set the document tag.
			String sql = " update tdcs_documents set current_version_number = ?," +
				" current_version_author = ? , description = ? where id = ? ";
			prepStmt = con.prepareStatement(sql);
			
			prepStmt.setInt(1, tDCSDocument.getCurrentVersionNumber() + 1);
			prepStmt.setString(2, user.getEmailId());
			prepStmt.setString(3, description);
			prepStmt.setInt(4, tDCSDocument.getDocumentId());
			prepStmt.execute();
			prepStmt.close();

			// lets insert the version info.
			if (databaseType.equals("mySQL")){
				sql = "insert into tdcs_document_versions(document_id, version_number," +
					" author, document_status, approval_status, " +
					" document_status_by, notes, file_type," +
					" file_path, folder_path, source_log, " +
					" created_by, created_dt,  last_modified_by, last_modified_dt )" +
					" values (?,?," +
					" ?,?,?," +
					" ?,?,?," +
					" ?,?,?," +
					" ?,now() ,?,now())";
			}
			else {
				sql = "insert into tdcs_document_versions(document_id, version_number," +
					" author, document_status, approval_status, " +
					" document_status_by, notes, file_type," +
					" file_path, folder_path, source_log, " +
					" created_by, created_dt,  last_modified_by, last_modified_dt )" +
					" values (?,?," +
					" ?,?,?," +
					" ?,?,?," +
					" ?,?,?," +
					" ?,sysdate ,?, sysdate)";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, tDCSDocument.getDocumentId());
			prepStmt.setInt(2, tDCSDocument.getCurrentVersionNumber() + 1);
			
			prepStmt.setString(3, user.getEmailId());
			prepStmt.setString(4, "unlocked");
			prepStmt.setString(5, "draft");
			
			
			prepStmt.setString(6,user.getEmailId());
			prepStmt.setString(7, description);
			prepStmt.setString(8, fileType);
			
			prepStmt.setString(9,filePath);
			prepStmt.setString(10, "Created by " + user.getEmailId() + 
				" on From TraceCloud Data on " + Calendar.getInstance().getTime());
			prepStmt.setString(11, folder.getFolderPath());
			
			prepStmt.setString(12, user.getEmailId());
			prepStmt.setString(13, user.getEmailId());
			prepStmt.execute();
			
			
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

	// creates a new document in the TDCS system.
	public static ArrayList getTDCSDocuments(String tDCSFilter,String tDCSFilterValue,String tDCSSortBy, int projectId, int maxSize, String databaseType) {
		ArrayList tDCSDocuments = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			

			if (tDCSFilter.equals("project")){

				if (databaseType.equals("mySQL")){
					sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path, d.current_version_number, " +
						" d.current_version_author, d.tag, d.full_tag, d.title, d.description, " +
						" dv.id \"current_version_id\", dv.document_status, dv.approval_status," +
						" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log , " +
						" dv.created_by, date_format(dv.created_dt, '%d %M %Y %r ') \"created_dt\" ," +
						" dv.last_modified_by, date_format(dv.last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
						" from tdcs_documents d, tdcs_document_versions dv, gr_folders f " +
						" where d.project_id = ? " + 
						" and d.id = dv.document_id " +
						" and d.folder_id = f.id " +
						" and d.current_version_number = dv.version_number";
				}
				else {
					sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path, d.current_version_number, " +
					" d.current_version_author, d.tag, d.full_tag, d.title, d.description, " +
					" dv.id \"current_version_id\", dv.document_status, dv.approval_status," +
					" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log , " +
					" dv.created_by, to_char(dv.created_dt, 'DD MON YYYY') \"created_dt\" ," +
					" dv.last_modified_by, to_char(dv.last_modified_dt, 'DD MON YYYY') \"last_modified_dt\" " +
					" from tdcs_documents d, tdcs_document_versions dv, gr_folders f " +
					" where d.project_id = ? " + 
					" and d.id = dv.document_id " +
					" and d.folder_id = f.id " +
					" and d.current_version_number = dv.version_number";
				}
				if (tDCSSortBy.equals("documentId")){
					sql += " order by d.id ";
				}
				if (tDCSSortBy.equals("title")){
					sql += " order by d.title ";
				}
				if (tDCSSortBy.equals("documentStatus")){
					sql += " order by dv.document_status ";
				}
				if (tDCSSortBy.equals("approvalStatus")){
					sql += " order by dv.approval_status ";
				}
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				rs = prepStmt.executeQuery();
			}
			
			if (tDCSFilter.equals("folder")){
				// in this case the tDCSFilterValue is actually the folderId
				int folderId = Integer.parseInt(tDCSFilterValue);
				if (databaseType.equals("mySQL")){
					sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path, d.current_version_number, " +
						" d.current_version_author, d.tag, d.full_tag, d.title, d.description, " +
						" dv.id \"current_version_id\", dv.document_status, dv.approval_status," +
						" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log, " +
						" dv.created_by, date_format(dv.created_dt, '%d %M %Y %r ') \"created_dt\" ," +
						" dv.last_modified_by, date_format(dv.last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
						" from tdcs_documents d, tdcs_document_versions dv, gr_folders f " +
						" where d.project_id = ? " +
						" and d.folder_id = ? " +
						" and d.id = dv.document_id " +
						" and d.folder_id = f.id" +
						" and d.current_version_number = dv.version_number";
				}
				else {
					sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path, d.current_version_number, " +
						" d.current_version_author, d.tag, d.full_tag, d.title, d.description, " +
						" dv.id \"current_version_id\", dv.document_status, dv.approval_status," +
						" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log, " +
						" dv.created_by, to_char(dv.created_dt, 'DD MON YYYY') \"created_dt\" ," +
						" dv.last_modified_by, to_char(dv.last_modified_dt, 'DD MON YYYY') \"last_modified_dt\" " +
						" from tdcs_documents d, tdcs_document_versions dv, gr_folders f " +
						" where d.project_id = ? " +
						" and d.folder_id = ? " +
						" and d.id = dv.document_id " +
						" and d.folder_id = f.id" +
						" and d.current_version_number = dv.version_number";
				}
				if (tDCSSortBy.equals("documentId")){
					sql += " order by d.id ";
				}
				if (tDCSSortBy.equals("title")){
					sql += " order by d.title ";
				}
				if (tDCSSortBy.equals("documentStatus")){
					sql += " order by dv.document_status ";
				}
				if (tDCSSortBy.equals("approvalStatus")){
					sql += " order by dv.approval_status ";
				}
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.setInt(2, folderId);
				rs = prepStmt.executeQuery();
			}
			
			if (tDCSFilter.equals("fileType")){
				if (databaseType.equals("mySQL")){
					sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path, d.current_version_number, " +
						" d.current_version_author, d.tag,  d.full_tag, d.title, d.description, " +
						" dv.id \"current_version_id\", dv.document_status, dv.approval_status," +
						" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log, " +
						" dv.created_by, date_format(dv.created_dt, '%d %M %Y %r ') \"created_dt\" ," +
						" dv.last_modified_by, date_format(dv.last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
						" from tdcs_documents d, tdcs_document_versions dv, gr_folders f " +
						" where d.project_id = ? " +
						" and dv.file_type = ? " +
						" and d.id = dv.document_id " +
						" and d.folder_id = f.id " +
						" and d.current_version_number = dv.version_number";
				}
				else {
					sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path, d.current_version_number, " +
					" d.current_version_author, d.tag,  d.full_tag, d.title, d.description, " +
					" dv.id \"current_version_id\", dv.document_status, dv.approval_status," +
					" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log, " +
					" dv.created_by, to_char(dv.created_dt, 'DD MON YYYY') \"created_dt\" ," +
					" dv.last_modified_by, to_char(dv.last_modified_dt, 'DD MON YYYY') \"last_modified_dt\" " +
					" from tdcs_documents d, tdcs_document_versions dv, gr_folders f " +
					" where d.project_id = ? " +
					" and dv.file_type = ? " +
					" and d.id = dv.document_id " +
					" and d.folder_id = f.id " +
					" and d.current_version_number = dv.version_number";
				}
				if (tDCSSortBy.equals("documentId")){
					sql += " order by d.id ";
				}
				if (tDCSSortBy.equals("title")){
					sql += " order by d.title ";
				}
				if (tDCSSortBy.equals("documentStatus")){
					sql += " order by dv.document_status ";
				}
				if (tDCSSortBy.equals("approvalStatus")){
					sql += " order by dv.approval_status ";
				}
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.setString(2, tDCSFilterValue);
				rs = prepStmt.executeQuery();
			}
			if (tDCSFilter.equals("documentStatus")){
				if (databaseType.equals("mySQL")){
					sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path , d.current_version_number, " +
						" d.current_version_author, d.tag, d.full_tag, d.title, d.description, " +
						" dv.id \"current_version_id\", dv.document_status, dv.approval_status," +
						" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log, " +
						" dv.created_by, date_format(dv.created_dt, '%d %M %Y %r ') \"created_dt\" ," +
						" dv.last_modified_by, date_format(dv.last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
						" from tdcs_documents d, tdcs_document_versions dv , gr_folders f " +
						" where d.project_id = ? " +
						" and dv.document_status = ? " +
						" and d.id = dv.document_id " +
						" and d.folder_id = f.id " +
						" and d.current_version_number = dv.version_number";
				}
				else {
					sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path , d.current_version_number, " +
					" d.current_version_author, d.tag, d.full_tag, d.title, d.description, " +
					" dv.id \"current_version_id\", dv.document_status, dv.approval_status," +
					" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log, " +
					" dv.created_by, to_char(dv.created_dt, 'DD MON YYYY') \"created_dt\" ," +
					" dv.last_modified_by, to_char(dv.last_modified_dt, 'DD MON YYYY') \"last_modified_dt\" " +
					" from tdcs_documents d, tdcs_document_versions dv , gr_folders f " +
					" where d.project_id = ? " +
					" and dv.document_status = ? " +
					" and d.id = dv.document_id " +
					" and d.folder_id = f.id " +
					" and d.current_version_number = dv.version_number";
				}
				if (tDCSSortBy.equals("documentId")){
					sql += " order by d.id ";
				}
				if (tDCSSortBy.equals("title")){
					sql += " order by d.title ";
				}
				if (tDCSSortBy.equals("documentStatus")){
					sql += " order by dv.document_status ";
				}
				if (tDCSSortBy.equals("approvalStatus")){
					sql += " order by dv.approval_status ";
				}
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.setString(2, tDCSFilterValue);
				rs = prepStmt.executeQuery();
			}
			
			if (tDCSFilter.equals("approvalStatus")){
				if (databaseType.equals("mySQL")){
					sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path, d.current_version_number, " +
						" d.current_version_author, d.tag, d.full_tag, d.title, d.description, " +
						" dv.id \"current_version_id\", dv.document_status, dv.approval_status," +
						" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log, " +
						" dv.created_by, date_format(dv.created_dt, '%d %M %Y %r ') \"created_dt\" ," +
						" dv.last_modified_by, date_format(dv.last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
						" from tdcs_documents d, tdcs_document_versions dv , gr_folders f" +
						" where d.project_id = ? " +
						" and dv.approval_status = ? " +
						" and d.id = dv.document_id " +
						" and d.folder_id = f.id " +
						" and d.current_version_number = dv.version_number";
				}
				else {
					sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path, d.current_version_number, " +
						" d.current_version_author, d.tag, d.full_tag, d.title, d.description, " +
						" dv.id \"current_version_id\", dv.document_status, dv.approval_status," +
						" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log, " +
						" dv.created_by, to_char(dv.created_dt, 'DD MON YYYY') \"created_dt\" ," +
						" dv.last_modified_by, to_char(dv.last_modified_dt, 'DD MON YYYY') \"last_modified_dt\" " +
						" from tdcs_documents d, tdcs_document_versions dv , gr_folders f" +
						" where d.project_id = ? " +
						" and dv.approval_status = ? " +
						" and d.id = dv.document_id " +
						" and d.folder_id = f.id " +
						" and d.current_version_number = dv.version_number";
				}
				if (tDCSSortBy.equals("documentId")){
					sql += " order by d.id ";
				}
				if (tDCSSortBy.equals("title")){
					sql += " order by d.title ";
				}
				if (tDCSSortBy.equals("documentStatus")){
					sql += " order by dv.document_status ";
				}
				if (tDCSSortBy.equals("approvalStatus")){
					sql += " order by dv.approval_status ";
				}
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.setString(2, tDCSFilterValue);
				rs = prepStmt.executeQuery();
			}
			
			if (tDCSFilter.equals("title")){
				if (databaseType.equals("mySQL")){
					sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path, d.current_version_number, " +
						" d.current_version_author, d.tag, d.full_tag, d.title, d.description, " +
						" dv.id \"current_version_id\", dv.document_status, dv.approval_status," +
						" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log, " +
						" dv.created_by, date_format(dv.created_dt, '%d %M %Y %r ') \"created_dt\" ," +
						" dv.last_modified_by, date_format(dv.last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
						" from tdcs_documents d, tdcs_document_versions dv, gr_folders f " +
						" where d.project_id = ? " +
						" and (" +
						"	(lower(d.title) like '%"+ tDCSFilterValue.toLowerCase()  +"%') " +
						" 	or " +
						"	(lower(dv.notes) like '%"+ tDCSFilterValue.toLowerCase()  +"%')" +
						" ) " +
						" and d.id = dv.document_id " +
						" and d.folder_id = f.id " +
						" and d.current_version_number = dv.version_number";
				}
				else {
					sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path, d.current_version_number, " +
					" d.current_version_author, d.tag, d.full_tag, d.title, d.description, " +
					" dv.id \"current_version_id\", dv.document_status, dv.approval_status," +
					" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log, " +
					" dv.created_by, to_char(dv.created_dt, 'DD MON YYYY') \"created_dt\" ," +
					" dv.last_modified_by, to_char(dv.last_modified_dt, 'DD MON YYYY') \"last_modified_dt\" " +
					" from tdcs_documents d, tdcs_document_versions dv, gr_folders f " +
					" where d.project_id = ? " +
					" and (" +
					"	(lower(d.title) like '%"+ tDCSFilterValue.toLowerCase()  +"%') " +
					" 	or " +
					"	(lower(dv.notes) like '%"+ tDCSFilterValue.toLowerCase()  +"%')" +
					" ) " +
					" and d.id = dv.document_id " +
					" and d.folder_id = f.id " +
					" and d.current_version_number = dv.version_number";
				}
				if (tDCSSortBy.equals("documentId")){
					sql += " order by d.id ";
				}
				if (tDCSSortBy.equals("title")){
					sql += " order by d.title ";
				}
				if (tDCSSortBy.equals("documentStatus")){
					sql += " order by dv.document_status ";
				}
				if (tDCSSortBy.equals("approvalStatus")){
					sql += " order by dv.approval_status ";
				}
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);

				rs = prepStmt.executeQuery();
			}
			
			if (tDCSFilter.equals("documentId")){
				if (databaseType.equals("mySQL")){
					sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path, d.current_version_number, " +
						" d.current_version_author, d.tag, d.full_tag, d.title, d.description, " +
						" dv.id \"current_version_id\", dv.document_status, dv.approval_status," +
						" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log, " +
						" dv.created_by, date_format(dv.created_dt, '%d %M %Y %r ') \"created_dt\" ," +
						" dv.last_modified_by, date_format(dv.last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
						" from tdcs_documents d, tdcs_document_versions dv , gr_folders f " +
						" where d.project_id = ? " +
						" and d.full_tag = ? " +
						" and d.id = dv.document_id " +
						" and d.folder_id = f.id " +
						" and d.current_version_number = dv.version_number";
				}
				else {
					sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path, d.current_version_number, " +
						" d.current_version_author, d.tag, d.full_tag, d.title, d.description, " +
						" dv.id \"current_version_id\", dv.document_status, dv.approval_status," +
						" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log, " +
						" dv.created_by, to_char(dv.created_dt, 'DD MON YYYY') \"created_dt\" ," +
						" dv.last_modified_by, to_char(dv.last_modified_dt, 'DD MON YYYY') \"last_modified_dt\" " +
						" from tdcs_documents d, tdcs_document_versions dv , gr_folders f " +
						" where d.project_id = ? " +
						" and d.full_tag = ? " +
						" and d.id = dv.document_id " +
						" and d.folder_id = f.id " +
						" and d.current_version_number = dv.version_number";
				}
				if (tDCSSortBy.equals("documentId")){
					sql += " order by d.id ";
				}
				if (tDCSSortBy.equals("title")){
					sql += " order by d.title ";
				}
				if (tDCSSortBy.equals("documentStatus")){
					sql += " order by dv.document_status ";
				}
				if (tDCSSortBy.equals("approvalStatus")){
					sql += " order by dv.approval_status ";
				}
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.setString(2, tDCSFilterValue);
				
				rs = prepStmt.executeQuery();
			}			
			int counter = 0;
			while (rs.next()){
				int documentId = rs.getInt("document_id");
				int folderId = rs.getInt("folder_id");
				String folderPath = rs.getString("folder_path");
				int currentVersionNumber = rs.getInt("current_version_number");
				String currentVersionAuthor = rs.getString("current_version_author");
				
				int tag = rs.getInt("tag");
				String fullTag =rs.getString("full_tag");
				String title = rs.getString("title");
				String description = rs.getString("description");
				
				int currentVersionId = rs.getInt("current_version_id");
				String currentVersionDocumentStatus = rs.getString("document_status");
				String currentVersionApprovalStatus = rs.getString("approval_status");
				
				
				String currentVersionDocumentStatusBy = rs.getString("document_status_by");
				String currentVersionNotes = rs.getString("notes");
				String currentVersionFileType = rs.getString("file_type");
				String currentVersionFilePath = rs.getString("file_path");
				String currentVersionSourceLog = rs.getString("source_log");
				
				String currentVersionCreatedBy = rs.getString("created_by");
				String currentVersionCreatedDt = rs.getString("created_dt");
				String currentVersionLastModifiedBy = rs.getString("last_modified_by");
				String currentVersionLastModifiedDt = rs.getString("last_modified_dt");
				
				TDCSDocument tDCSDocument = new TDCSDocument(documentId, projectId, folderId,folderPath, 
					currentVersionNumber,currentVersionAuthor,
					tag, fullTag, title, description,
					currentVersionId, 
					currentVersionDocumentStatus, currentVersionApprovalStatus, 
					currentVersionDocumentStatusBy, currentVersionNotes,
					currentVersionFileType, currentVersionFilePath, currentVersionSourceLog,
					currentVersionCreatedBy, currentVersionCreatedDt, currentVersionLastModifiedBy, currentVersionLastModifiedDt);
				tDCSDocuments.add(tDCSDocument);
				
				// lets restrict the returned documents to less than maxsize.
				if (counter++ >= maxSize){
					try {con.close();} catch (Exception e) {}
					return (tDCSDocuments);
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
		return (tDCSDocuments);
	}	



	// creates a new document in the TDCS system.
	public static ArrayList getPreviousVersionsOfDocument(int tDCSDocumentId, String databaseType) {
		ArrayList tDCSDocumentVersions = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			
			if (databaseType.equals("mySQL")){
				sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path, dv.version_number, " +
					" dv.author, d.tag, d.full_tag, d.title, d.description, " +
					" dv.id \"version_id\", dv.document_status, dv.approval_status," +
					" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log , " +
					" dv.created_by, date_format(dv.created_dt, '%d %M %Y %r ') \"created_dt\" ," +
					" dv.last_modified_by, date_format(dv.last_modified_dt, '%d %M %Y %r ') \"last_modified_dt\" " +
					" from tdcs_documents d, tdcs_document_versions dv , gr_folders f" +
					" where d.id = ? " + 
					" and d.id = dv.document_id " +
					" and d.folder_id = f.id " +
					" order by dv.version_number desc";
			}
			else {
				sql = "select d.id \"document_id\", d.project_id, d.folder_id, f.folder_path, dv.version_number, " +
				" dv.author, d.tag, d.full_tag, d.title, d.description, " +
				" dv.id \"version_id\", dv.document_status, dv.approval_status," +
				" dv.document_status_by, dv.notes, dv.file_type, dv.file_path, dv.source_log , " +
				" dv.created_by, to_char(dv.created_dt, 'DD MON YYYY') \"created_dt\" ," +
				" dv.last_modified_by, to_char(dv.last_modified_dt, 'DD MON YYYY') \"last_modified_dt\" " +
				" from tdcs_documents d, tdcs_document_versions dv , gr_folders f" +
				" where d.id = ? " + 
				" and d.id = dv.document_id " +
				" and d.folder_id = f.id " +
				" order by dv.version_number desc";
			}
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, tDCSDocumentId);
			rs = prepStmt.executeQuery();
			
			
			
			while (rs.next()){
				int documentId = rs.getInt("document_id");
				int projectId = rs.getInt("project_id");
				int folderId = rs.getInt("folder_id");
				String folderPath = rs.getString("folder_path");
				int versionNumber = rs.getInt("version_number");
				String author = rs.getString("author");
				
				int tag = rs.getInt("tag");
				String fullTag =rs.getString("full_tag");
				String title = rs.getString("title");
				String description = rs.getString("description");
				
				int versionId = rs.getInt("version_id");
				String versionDocumentStatus = rs.getString("document_status");
				String versionApprovalStatus = rs.getString("approval_status");
				
				
				String versionDocumentStatusBy = rs.getString("document_status_by");
				String versionNotes = rs.getString("notes");
				String versionFileType = rs.getString("file_type");
				String versionFilePath = rs.getString("file_path");
				String versionSourceLog = rs.getString("source_log");
				
				String versionCreatedBy = rs.getString("created_by");
				String versionCreatedDt = rs.getString("created_dt");
				String versionLastModifiedBy = rs.getString("last_modified_by");
				String versionLastModifiedDt = rs.getString("last_modified_dt");
				
				TDCSDocumentVersion tDCSDocumentVersion = new TDCSDocumentVersion(documentId, projectId, folderId, folderPath, 
					versionNumber,author,
					tag, fullTag, title, description,
					versionId, 
					versionDocumentStatus, versionApprovalStatus, 
					versionDocumentStatusBy, versionNotes,
					versionFileType, versionFilePath, versionSourceLog,
					versionCreatedBy, versionCreatedDt, versionLastModifiedBy, versionLastModifiedDt);
				tDCSDocumentVersions.add(tDCSDocumentVersion);
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
		return (tDCSDocumentVersions);
	}	


	// Locks the document under the user's name.
	public static void lockDocument(int tDCSDocumentId, int currentVersionNumber, User user, String databaseType) {

		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			

			String sql = "";
			if (databaseType.equals("mySQL")){
				 sql = "update tdcs_document_versions " +
					" set document_status ='locked' ," +
					" document_status_by= ? ," +
					" last_modified_by = ? ," +
					" last_modified_dt = now() " +
					" where document_id = ? " +
					" and version_number = ? ";
			}
			else {
				sql = "update tdcs_document_versions " +
				" set document_status ='locked' ," +
				" document_status_by= ? ," +
				" last_modified_by = ? ," +
				" last_modified_dt = sysdate " +
				" where document_id = ? " +
				" and version_number = ? ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, user.getEmailId());
			prepStmt.setString(2, user.getEmailId());
			prepStmt.setInt(3, tDCSDocumentId);
			prepStmt.setInt(4, currentVersionNumber);
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
		return;
	}	

	// UnLocks the document under the user's name.
	public static void unlockDocument(int tDCSDocumentId, int currentVersionNumber, User user, String databaseType) {

		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			

			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "update tdcs_document_versions " +
					" set document_status ='unlocked' ," +
					" document_status_by= ? ," +
					" last_modified_by = ? ," +
					" last_modified_dt = now() " +
					" where document_id = ? " +
					" and version_number = ? ";
			}
			else {
				sql = "update tdcs_document_versions " +
				" set document_status ='unlocked' ," +
				" document_status_by= ? ," +
				" last_modified_by = ? ," +
				" last_modified_dt = sysdate " +
				" where document_id = ? " +
				" and version_number = ? ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, user.getEmailId());
			prepStmt.setString(2, user.getEmailId());
			prepStmt.setInt(3, tDCSDocumentId);
			prepStmt.setInt(4, currentVersionNumber);
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
		return;
	}	


	// creates a an arraylist of folders that have at least 1 document in this project .
	public static ArrayList getFoldersWithDocuments(int projectId) {
		ArrayList folders = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
		
			String sql = "select distinct f.id, f.project_id, f.name, f.description, f.parent_folder_id," +
			" f.folder_level, f.folder_order, f.folder_path, rt.name \"requirement_type_name\", " +
			" rt.id \"requirement_type_id\", f.created_by, f.created_dt, f.last_modified_by , " +
			" f.last_modified_dt " +
			" from tdcs_documents d, gr_folders f, gr_requirement_types rt " +
			" where d.project_id = ? " +
			" and d.folder_id = f.id " +
			" and f.requirement_type_id = rt.id ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();
		
			
			while (rs.next()){
				
				int folderId = rs.getInt("id");
				String folderName = rs.getString("name");
				String folderDescription = rs.getString ("description");
				int parentFolderId = rs.getInt("parent_folder_id");
				int folderLevel = rs.getInt("folder_level");
				int folderOrder = rs.getInt("folder_order");
				String folderPath = rs.getString("folder_path");
				String requirementTypeName = rs.getString("requirement_type_name");
				int requirementTypeId = rs.getInt("requirement_type_id");
				String createdBy = rs.getString("created_by");
				//this.createdDt = rs.getDate("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by") ;
				//this.lastModifiedDt = rs.getDate("last_modified_by");
				
				Folder folder = new Folder (folderId, projectId, folderName, folderDescription, 
						parentFolderId, folderLevel,folderOrder,  
						folderPath, requirementTypeId , requirementTypeName,createdBy,
						lastModifiedBy);
				folders.add(folder);
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
		return (folders);
	}	

}
