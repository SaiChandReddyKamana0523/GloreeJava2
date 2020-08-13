package com.gloree.utils;


import java.util.ArrayList;
import com.gloree.beans.*;

import java.util.HashMap;


import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;

public class ReleaseMetricsUtil {
	
	// returns an ArrayList of Strings of distinct Req Type ShortNames in a release.
	// if release == 0, we crunch for project.
	public static ArrayList  getRequirementTypesForReleaseOrProject(int releaseId, int projectId, String displayRequirementType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList reqTypeShortNames = new ArrayList();
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// This sql gets the total number of requirement types.
			String sql = "";
			if (releaseId ==0) {
				sql = "select distinct requirement_type_short_name " +
				" from gr_project_metrics " +
				" where project_id = " + projectId + " order by requirement_type_short_name " ;

			}
			else {
				
				sql = "select distinct requirement_type_short_name " +
				" from gr_release_metrics rm " +
				" where rm.project_id =  " + projectId + 
				" and rm.release_id =  " + releaseId
				+ " order by requirement_type_short_name " ;
				
			}
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				String rTPrefix = rs.getString("requirement_type_short_name");
				if (!(displayRequirementType.equals(""))){
					// show only in displayReqType
					if (displayRequirementType.contains(rTPrefix + ",")){
						reqTypeShortNames.add(rTPrefix);
					}
				}
				else{
					reqTypeShortNames.add(rTPrefix);
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
		
		return (reqTypeShortNames);
	}
	
	public static ArrayList  getRequirementTypesForAgileSprint(int sprintId, int projectId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList reqTypeShortNames = new ArrayList();
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// This sql gets the total number of requirement types.
			String sql = "";

			sql = "select distinct requirement_type_short_name " +
			" from gr_sprint_metrics rm " +
			" where rm.project_id =  " + projectId + 
			" and rm.sprint_id =  " + sprintId;
			
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				reqTypeShortNames.add(rs.getString("requirement_type_short_name"));
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
		
		return (reqTypeShortNames);
	}
	// returns an ArrayList of Strings of distinct Req Type ShortNames in a baseline.

	public static ArrayList  getRequirementTypesForBaseline(int rTBaselineId, int projectId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList reqTypeShortNames = new ArrayList();
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// This sql gets the total number of requirement types.

			String	sql = "select distinct requirement_type_short_name " +
				" from gr_baseline_metrics bm " +
				" where bm.project_id =  " + projectId + 
				" and bm.rt_baseline_id=  " + rTBaselineId;
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				reqTypeShortNames.add(rs.getString("requirement_type_short_name"));
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
		
		return (reqTypeShortNames);
	}
	
	// returns an ArrayList of Strings of distinct Req Type ShortNames in a folder
	// in the trend table

	public static ArrayList  getRequirementTypesForFolder(int folderId, int projectId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList reqTypeShortNames = new ArrayList();
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// This sql gets the total number of requirement types.
			String	sql = "select distinct requirement_type_short_name " +
				" from gr_folder_metrics fm " +
				" where fm.project_id =  " + projectId + 
				" and fm.folder_id=  " + folderId;
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				reqTypeShortNames.add(rs.getString("requirement_type_short_name"));
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
		
		return (reqTypeShortNames);
	}

	// returns an array string for printing a data table for each req type inthe release or project.
	// if release == 0, we crunch for project.	
	// if release == -1 , we crunch for user.
	public static ArrayList  getReleaseDataTableArrayForReleaseOrProject
		(int releaseId, int projectId, User user, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList dataTableArrayList = new ArrayList();
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "";

			// lets get the max data_load_dt from the gr_release_metrics tabel for this release.
			// This sql gets the total number of requirements in a folder.
			if (releaseId ==0) {
				// crunch for project.
				if (databaseType.equals("mySQL")){
					sql = "select date_format(max(data_load_dt), '%d %m %y %h:%i:%s') \"data_load_dt\"" +
						" from gr_project_metrics rm " +
						" where rm.project_id =? ";
				}
				else {
					sql = "select to_char(max(data_load_dt), 'DD MON YYYY HH MI SS') \"data_load_dt\"" +
					" from gr_project_metrics rm " +
					" where rm.project_id =? ";
				}
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);			
			}
			else if (releaseId == -1 ) {
				// crunch for user.
				if (databaseType.equals("mySQL")){
					sql = "select date_format(max(data_load_dt), '%d %m %y %h:%i:%s') \"data_load_dt\"" +
						" from gr_user_metrics rm " +
						" where rm.project_id =? ";
				}
				else {
					sql = "select to_char(max(data_load_dt), 'DD MON YYYY HH MI SS') \"data_load_dt\"" +
					" from gr_user_metrics rm " +
					" where rm.project_id =? ";
				}
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
			}
			else {
				// crunch for release.
				if (databaseType.equals("mySQL")){
					sql = "select date_format(max(data_load_dt), '%d %m %y %h:%i:%s') \"data_load_dt\"" +
						" from gr_release_metrics rm " +
						" where rm.project_id =? " +
						" and rm.release_id = ? ";
				}
				else {
					sql = "select to_char(max(data_load_dt), 'DD MON YYYY HH MI SS') \"data_load_dt\"" +
					" from gr_release_metrics rm " +
					" where rm.project_id =? " +
					" and rm.release_id = ? ";
				}
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.setInt(2, releaseId);
				
			}
			rs = prepStmt.executeQuery();
			
			String maxDataLoadDt = "";
			while (rs.next()){
				maxDataLoadDt = rs.getString("data_load_dt");
			}
			
			// This sql gets the total number of requirements in a folder.
			if (releaseId ==0) {
				// crunch for project.
				if (databaseType.equals("mySQL")){
					sql = "select date_format(data_load_dt, '%d %m %y %h:%i:%s') \"data_load_dt\"," +
						" requirement_type_short_name, " +
						" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs, " +
						" num_of_rejected_reqs, num_of_approved_reqs, " +
						" num_of_dangling_reqs, num_of_orphan_reqs, " +
						" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
						" num_of_completed_reqs, num_of_incomplete_reqs, " +
						" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs " +
						" from gr_project_metrics rm " +
						" where rm.project_id =? " +
						" and rm.data_load_dt = str_to_date('"+ maxDataLoadDt+ "','%d %m %y %h:%i:%s') ";
				}
				else {
					sql = "select to_char(data_load_dt, 'DD MON YYYY HH MI SS') \"data_load_dt\"," +
					" requirement_type_short_name, " +
					" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs, " +
					" num_of_rejected_reqs, num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs, " +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs, " +
					" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs " +
					" from gr_project_metrics rm " +
					" where rm.project_id =? " +
					" and rm.data_load_dt = to_date('"+ maxDataLoadDt+ "','DD MON YYYY HH MI SS') ";
				}
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);			
			}
			else if (releaseId == -1 ) {
				// crunch for user.
				if (databaseType.equals("mySQL")){
					sql = "select date_format(data_load_dt, '%d %m %y %h:%i:%s') \"data_load_dt\"," +
						" requirement_type_short_name, " +
						" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs, " +
						" num_of_rejected_reqs, num_of_approved_reqs, " +
						" num_of_dangling_reqs, num_of_orphan_reqs, " +
						" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
						" num_of_completed_reqs, num_of_incomplete_reqs, " +
						" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs " +
						" from gr_user_metrics rm " +
						" where rm.project_id =? " +
						" and rm.data_load_dt = str_to_date('"+ maxDataLoadDt+ "','%d %m %y %h:%i:%s') " +
						" and owner = '" + user.getEmailId() + "'" ;
				}
				else {
					sql = "select to_char(data_load_dt, 'DD MON YYYY HH MI SS') \"data_load_dt\"," +
					" requirement_type_short_name, " +
					" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs, " +
					" num_of_rejected_reqs, num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs, " +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs, " +
					" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs " +
					" from gr_user_metrics rm " +
					" where rm.project_id =? " +
					" and rm.data_load_dt = to_date('"+ maxDataLoadDt+ "','DD MON YYYY HH MI SS') " +
					" and owner = '" + user.getEmailId() + "'" ;
				}
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);				
			}
			else {
				// crunch for release.
				if (databaseType.equals("mySQL")){
					sql = "select date_format(data_load_dt, '%d %m %y %h:%i:%s') \"data_load_dt\"," +
						" requirement_type_short_name, " +
						" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs, " +
						" num_of_rejected_reqs, num_of_approved_reqs, " +
						" num_of_dangling_reqs, num_of_orphan_reqs, " +
						" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
						" num_of_completed_reqs, num_of_incomplete_reqs, " +
						" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs " +
						" from gr_release_metrics rm " +
						" where rm.project_id =? " +
						" and rm.release_id = ? " +
						" and rm.data_load_dt = str_to_date('"+ maxDataLoadDt+ "','%d %m %y %h:%i:%s')";
				}
				else {
					sql = "select to_char(data_load_dt, 'DD MON YYYY HH MI SS') \"data_load_dt\"," +
					" requirement_type_short_name, " +
					" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs, " +
					" num_of_rejected_reqs, num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs, " +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs, " +
					" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs " +
					" from gr_release_metrics rm " +
					" where rm.project_id =? " +
					" and rm.release_id = ? " +
					" and rm.data_load_dt = to_date('"+ maxDataLoadDt+ "','DD MON YYYY HH MI SS')";
				}
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.setInt(2, releaseId);
			}
			rs = prepStmt.executeQuery();
			
			
			while (rs.next()){
				String dataLoadDt = rs.getString("data_load_dt");
				String requirementTypeShortName = rs.getString("requirement_type_short_name");
				int numOfRequirements = rs.getInt("num_of_requirements");
				int numOfDraftRequirements = rs.getInt("num_of_draft_reqs");
				int numOfInApprovalWorkflowRequirements = rs.getInt("num_of_in_workflow_reqs");
				int numOfRejectedRequirements = rs.getInt("num_of_rejected_reqs");
				int numOfApprovedRequirements = rs.getInt("num_of_approved_reqs");
				int numOfDanglingRequirements = rs.getInt("num_of_dangling_reqs");
				int numOfOrphanRequirements = rs.getInt("num_of_orphan_reqs");
				int numOfSuspectUpstreamRequirements = rs.getInt("num_of_suspect_upstream_reqs");
				int numOfSuspectDownstreamRequirements = rs.getInt("num_of_suspect_downstream_reqs");
				int numOfCompletedRequirements = rs.getInt("num_of_completed_reqs");
				int numOfIncompleteRequirements = rs.getInt("num_of_incomplete_reqs");
				int numOfTestPendingRequirements = rs.getInt("num_of_test_pending_reqs");
				int numOfTestPassRequirements = rs.getInt("num_of_test_pass_reqs");
				int numOfTestFailRequirements = rs.getInt("num_of_test_fail_reqs");
				
				String row = dataLoadDt + ":##:" + 
					requirementTypeShortName + ":##:" + 
					numOfRequirements + ":##:" + 
					numOfDraftRequirements + ":##:" + 
					numOfInApprovalWorkflowRequirements + ":##:" + 
					numOfRejectedRequirements + ":##:" + 
					numOfApprovedRequirements + ":##:" + 
					numOfDanglingRequirements + ":##:" + 
					numOfOrphanRequirements + ":##:" + 
					numOfSuspectUpstreamRequirements + ":##:" + 
					numOfSuspectDownstreamRequirements + ":##:" + 
					numOfCompletedRequirements + ":##:" + 
					numOfIncompleteRequirements + ":##:" +
					numOfTestPendingRequirements + ":##:" +
					numOfTestPassRequirements + ":##:" +
					numOfTestFailRequirements ;
				dataTableArrayList.add(row);
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
		
		return (dataTableArrayList);
	}
	
	
	public static ArrayList  getReleaseDataTableArrayByFolder(int releaseId, int projectId, User user, String databaseType){
	PreparedStatement prepStmt = null;
	ResultSet rs = null;
	java.sql.Connection con = null;
	ArrayList dataTableArrayList = new ArrayList();
	try {

		
		javax.naming.InitialContext context = new InitialContext();
		javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
		con = dataSource.getConnection();
		
		String sql = "";

		
		// This sql gets the total number of requirements in a folder.
		// crunch for release.
		if (databaseType.equals("mySQL")){
			sql = "select date_format(data_load_dt, '%d %m %y %h:%i:%s') \"data_load_dt\"," +
				" requirement_type_short_name, f.folder_path, " +
				" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs, " +
				" num_of_rejected_reqs, num_of_approved_reqs, " +
				" num_of_dangling_reqs, num_of_orphan_reqs, " +
				" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
				" num_of_completed_reqs, num_of_incomplete_reqs, " +
				" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs, f.id 'folder_id' " +
				" from gr_release_folder_metrics rm, gr_folders f " +
				" where rm.project_id =? " +
				" and rm.release_id = ? " +
				" and rm.folder_id = f.id " +
				" order by rm.requirement_type_short_name, f.folder_path " ;
		}
		else {
			sql = "select to_char(data_load_dt, 'DD MON YYYY HH MI SS') \"data_load_dt\"," +
			" requirement_type_short_name, f.folder_path, " +
			" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs, " +
			" num_of_rejected_reqs, num_of_approved_reqs, " +
			" num_of_dangling_reqs, num_of_orphan_reqs, " +
			" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
			" num_of_completed_reqs, num_of_incomplete_reqs, " +
			" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs, f.id 'folder_id' " +
			" from gr_release_folder_metrics rm , gr_folders f" +
			" where rm.project_id =? " +
			" and rm.release_id = ? " +
			" and rm.folder_id = f.id " +
			" order by rm.requirement_type_short_name, f.folder_path";
		}
		prepStmt = con.prepareStatement(sql);
		prepStmt.setInt(1, projectId);
		prepStmt.setInt(2, releaseId);
	
		rs = prepStmt.executeQuery();
		
		
		while (rs.next()){
			String dataLoadDt = rs.getString("data_load_dt");
			String requirementTypeShortName = rs.getString("requirement_type_short_name");
			String folderPath = rs.getString("folder_path");
			int numOfRequirements = rs.getInt("num_of_requirements");
			int numOfDraftRequirements = rs.getInt("num_of_draft_reqs");
			int numOfInApprovalWorkflowRequirements = rs.getInt("num_of_in_workflow_reqs");
			int numOfRejectedRequirements = rs.getInt("num_of_rejected_reqs");
			int numOfApprovedRequirements = rs.getInt("num_of_approved_reqs");
			int numOfDanglingRequirements = rs.getInt("num_of_dangling_reqs");
			int numOfOrphanRequirements = rs.getInt("num_of_orphan_reqs");
			int numOfSuspectUpstreamRequirements = rs.getInt("num_of_suspect_upstream_reqs");
			int numOfSuspectDownstreamRequirements = rs.getInt("num_of_suspect_downstream_reqs");
			int numOfCompletedRequirements = rs.getInt("num_of_completed_reqs");
			int numOfIncompleteRequirements = rs.getInt("num_of_incomplete_reqs");
			int numOfTestPendingRequirements = rs.getInt("num_of_test_pending_reqs");
			int numOfTestPassRequirements = rs.getInt("num_of_test_pass_reqs");
			int numOfTestFailRequirements = rs.getInt("num_of_test_fail_reqs");
			int folderId = rs.getInt("folder_id");
			
			String row = dataLoadDt + ":##:" + 
				folderPath + ":##:" +	
				numOfRequirements + ":##:" + 
				numOfDraftRequirements + ":##:" + 
				numOfInApprovalWorkflowRequirements + ":##:" + 
				numOfRejectedRequirements + ":##:" + 
				numOfApprovedRequirements + ":##:" + 
				numOfDanglingRequirements + ":##:" + 
				numOfOrphanRequirements + ":##:" + 
				numOfSuspectUpstreamRequirements + ":##:" + 
				numOfSuspectDownstreamRequirements + ":##:" + 
				numOfCompletedRequirements + ":##:" + 
				numOfIncompleteRequirements + ":##:" +
				numOfTestPendingRequirements + ":##:" +
				numOfTestPassRequirements + ":##:" +
				numOfTestFailRequirements + ":##:" +
				folderId ;
			dataTableArrayList.add(row);
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
	
	return (dataTableArrayList);
}

	// returns an array string for printing a data table for each req type inthe baseline.

	public static ArrayList  getReleaseDataTableArrayForAgileSprint(int sprintId, int projectId, User user, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList dataTableArrayList = new ArrayList();
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "";

			// lets get the max data_load_dt from the gr_release_metrics tabel for this release.
			// This sql gets the total number of requirements in a folder.
			
			// crunch for baseline.
			if (databaseType.equals("mySQL")){
				sql = "select date_format(max(data_load_dt), '%d %m %y %h:%i:%s') \"data_load_dt\"" +
					" from gr_sprint_metrics bm " +
					" where bm.project_id =? " +
					" and bm.sprint_id = ? ";
			}
			else {
				sql = "select to_char(max(data_load_dt), 'DD MON YYYY HH MI SS') \"data_load_dt\"" +
				" from gr_sprint_metrics bm " +
				" where bm.project_id =? " +
				" and bm.sprint_id = ? ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, sprintId);
		
			rs = prepStmt.executeQuery();
			
			String maxDataLoadDt = "";
			while (rs.next()){
				maxDataLoadDt = rs.getString("data_load_dt");
			}
			
			// This sql gets the total number of requirements in a folder.
			
			// crunch for baseline.
			if (databaseType.equals("mySQL")){
				sql = "select date_format(data_load_dt, '%d %m %y %h:%i:%s') \"data_load_dt\"," +
					" requirement_type_short_name, " +
					" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs, " +
					" num_of_rejected_reqs, num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs, " +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs, " +
					" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs " +
					" from gr_sprint_metrics bm " +
					" where bm.project_id =? " +
					" and bm.sprint_id = ? " +
					" and bm.data_load_dt = str_to_date('"+ maxDataLoadDt+ "','%d %m %y %h:%i:%s')";
			}
			else {
				sql = "select to_char(data_load_dt, 'DD MON YYYY HH MI SS') \"data_load_dt\"," +
				" requirement_type_short_name, " +
				" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs, " +
				" num_of_rejected_reqs, num_of_approved_reqs, " +
				" num_of_dangling_reqs, num_of_orphan_reqs, " +
				" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
				" num_of_completed_reqs, num_of_incomplete_reqs, " +
				" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs " +
				" from gr_sprint_metrics bm " +
				" where bm.project_id =? " +
				" and bm.sprint_id = ? " +
				" and bm.data_load_dt = to_date('"+ maxDataLoadDt+ "','DD MON YYYY HH MI SS')";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, sprintId);
		
			rs = prepStmt.executeQuery();
			
			
			while (rs.next()){
				String dataLoadDt = rs.getString("data_load_dt");
				String requirementTypeShortName = rs.getString("requirement_type_short_name");
				int numOfRequirements = rs.getInt("num_of_requirements");
				int numOfDraftRequirements = rs.getInt("num_of_draft_reqs");
				int numOfInApprovalWorkflowRequirements = rs.getInt("num_of_in_workflow_reqs");
				int numOfRejectedRequirements = rs.getInt("num_of_rejected_reqs");
				int numOfApprovedRequirements = rs.getInt("num_of_approved_reqs");
				int numOfDanglingRequirements = rs.getInt("num_of_dangling_reqs");
				int numOfOrphanRequirements = rs.getInt("num_of_orphan_reqs");
				int numOfSuspectUpstreamRequirements = rs.getInt("num_of_suspect_upstream_reqs");
				int numOfSuspectDownstreamRequirements = rs.getInt("num_of_suspect_downstream_reqs");
				int numOfCompletedRequirements = rs.getInt("num_of_completed_reqs");
				int numOfIncompleteRequirements = rs.getInt("num_of_incomplete_reqs");
				int numOfTestPendingRequirements = rs.getInt("num_of_test_pending_reqs");
				int numOfTestPassRequirements = rs.getInt("num_of_test_pass_reqs");
				int numOfTestFailRequirements = rs.getInt("num_of_test_fail_reqs");
				
				
				String row = dataLoadDt + ":##:" + 
					requirementTypeShortName + ":##:" + 
					numOfRequirements + ":##:" + 
					numOfDraftRequirements + ":##:" + 
					numOfInApprovalWorkflowRequirements + ":##:" + 
					numOfRejectedRequirements + ":##:" + 
					numOfApprovedRequirements + ":##:" + 
					numOfDanglingRequirements + ":##:" + 
					numOfOrphanRequirements + ":##:" + 
					numOfSuspectUpstreamRequirements + ":##:" + 
					numOfSuspectDownstreamRequirements + ":##:" + 
					numOfCompletedRequirements + ":##:" + 
					numOfIncompleteRequirements  + ":##:" +
					numOfTestPendingRequirements + ":##:" +
					numOfTestPassRequirements + ":##:" +
					numOfTestFailRequirements ;
;
				dataTableArrayList.add(row);
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
		
		return (dataTableArrayList);
	}

	// returns an array string for printing a data table for each req type inthe baseline.

	public static ArrayList  getReleaseDataTableArrayForBaseline(int rTBaselineId, int projectId, User user, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList dataTableArrayList = new ArrayList();
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "";

			// lets get the max data_load_dt from the gr_release_metrics tabel for this release.
			// This sql gets the total number of requirements in a folder.
			
			// crunch for baseline.
			if (databaseType.equals("mySQL")){
				sql = "select date_format(max(data_load_dt), '%d %m %y %h:%i:%s') \"data_load_dt\"" +
					" from gr_baseline_metrics bm " +
					" where bm.project_id =? " +
					" and bm.rt_baseline_id = ? ";
			}
			else {
				sql = "select to_char(max(data_load_dt), 'DD MON YYYY HH MI SS') \"data_load_dt\"" +
				" from gr_baseline_metrics bm " +
				" where bm.project_id =? " +
				" and bm.rt_baseline_id = ? ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, rTBaselineId);
		
			rs = prepStmt.executeQuery();
			
			String maxDataLoadDt = "";
			while (rs.next()){
				maxDataLoadDt = rs.getString("data_load_dt");
			}
			
			// This sql gets the total number of requirements in a folder.
			
			// crunch for baseline.
			if (databaseType.equals("mySQL")){
				sql = "select date_format(data_load_dt, '%d %m %y %h:%i:%s') \"data_load_dt\"," +
					" requirement_type_short_name, " +
					" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs, " +
					" num_of_rejected_reqs, num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs, " +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs, " +
					" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs " +
					" from gr_baseline_metrics bm " +
					" where bm.project_id =? " +
					" and bm.rt_baseline_id = ? " +
					" and bm.data_load_dt = str_to_date('"+ maxDataLoadDt+ "','%d %m %y %h:%i:%s')";
			}
			else {
				sql = "select to_char(data_load_dt, 'DD MON YYYY HH MI SS') \"data_load_dt\"," +
				" requirement_type_short_name, " +
				" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs, " +
				" num_of_rejected_reqs, num_of_approved_reqs, " +
				" num_of_dangling_reqs, num_of_orphan_reqs, " +
				" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
				" num_of_completed_reqs, num_of_incomplete_reqs, " +
				" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs " +
				" from gr_baseline_metrics bm " +
				" where bm.project_id =? " +
				" and bm.rt_baseline_id = ? " +
				" and bm.data_load_dt = to_date('"+ maxDataLoadDt+ "','DD MON YYYY HH MI SS')";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, rTBaselineId);
		
			rs = prepStmt.executeQuery();
			
			
			while (rs.next()){
				String dataLoadDt = rs.getString("data_load_dt");
				String requirementTypeShortName = rs.getString("requirement_type_short_name");
				int numOfRequirements = rs.getInt("num_of_requirements");
				int numOfDraftRequirements = rs.getInt("num_of_draft_reqs");
				int numOfInApprovalWorkflowRequirements = rs.getInt("num_of_in_workflow_reqs");
				int numOfRejectedRequirements = rs.getInt("num_of_rejected_reqs");
				int numOfApprovedRequirements = rs.getInt("num_of_approved_reqs");
				int numOfDanglingRequirements = rs.getInt("num_of_dangling_reqs");
				int numOfOrphanRequirements = rs.getInt("num_of_orphan_reqs");
				int numOfSuspectUpstreamRequirements = rs.getInt("num_of_suspect_upstream_reqs");
				int numOfSuspectDownstreamRequirements = rs.getInt("num_of_suspect_downstream_reqs");
				int numOfCompletedRequirements = rs.getInt("num_of_completed_reqs");
				int numOfIncompleteRequirements = rs.getInt("num_of_incomplete_reqs");
				int numOfTestPendingRequirements = rs.getInt("num_of_test_pending_reqs");
				int numOfTestPassRequirements = rs.getInt("num_of_test_pass_reqs");
				int numOfTestFailRequirements = rs.getInt("num_of_test_fail_reqs");
				
				
				String row = dataLoadDt + ":##:" + 
					requirementTypeShortName + ":##:" + 
					numOfRequirements + ":##:" + 
					numOfDraftRequirements + ":##:" + 
					numOfInApprovalWorkflowRequirements + ":##:" + 
					numOfRejectedRequirements + ":##:" + 
					numOfApprovedRequirements + ":##:" + 
					numOfDanglingRequirements + ":##:" + 
					numOfOrphanRequirements + ":##:" + 
					numOfSuspectUpstreamRequirements + ":##:" + 
					numOfSuspectDownstreamRequirements + ":##:" + 
					numOfCompletedRequirements + ":##:" + 
					numOfIncompleteRequirements  + ":##:" +
					numOfTestPendingRequirements + ":##:" +
					numOfTestPassRequirements + ":##:" +
					numOfTestFailRequirements ;
;
				dataTableArrayList.add(row);
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
		
		return (dataTableArrayList);
	}
	
	
	// returns an array string for printing a data table for each req type inthe folder.

	/*
	public static ArrayList  getReleaseDataTableArrayForFolder(int folderId, int projectId, User user, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList dataTableArrayList = new ArrayList();
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "";

			// lets get the max data_load_dt from the gr_release_metrics tabel for this release.
			// This sql gets the total number of requirements in a folder.
			
			// crunch for baseline.
			if (databaseType.equals("mySQL")){
				sql = "select date_format(max(data_load_dt), '%d %m %y %h:%i:%s') \"data_load_dt\"" +
					" from gr_folder_metrics fm " +
					" where fm.project_id =? " +
					" and fm.folder_id = ? ";
			}
			else {
				sql = "select to_char(max(data_load_dt), 'DD MON YYYY HH MI SS') \"data_load_dt\"" +
				" from gr_folder_metrics fm " +
				" where fm.project_id =? " +
				" and fm.folder_id = ? ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
		
			rs = prepStmt.executeQuery();
			
			String maxDataLoadDt = "";
			while (rs.next()){
				maxDataLoadDt = rs.getString("data_load_dt");
			}
			
			// This sql gets the total number of requirements in a folder.
			
			// crunch for baseline.
			if (databaseType.equals("mySQL")){
				sql = "select date_format(data_load_dt, '%d %m %y %h:%i:%s') \"data_load_dt\"," +
					" requirement_type_short_name, " +
					" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs, " +
					" num_of_rejected_reqs, num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs, " +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs , " +
					" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs " +
					" from gr_folder_metrics fm " +
					" where fm.project_id =? " +
					" and fm.folder_id = ? " +
					" and fm.data_load_dt = str_to_date('"+ maxDataLoadDt+ "','%d %m %y %h:%i:%s')";
			}
			else {
				sql = "select to_char(data_load_dt, 'DD MON YYYY HH MI SS') \"data_load_dt\"," +
				" requirement_type_short_name, " +
				" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs, " +
				" num_of_rejected_reqs, num_of_approved_reqs, " +
				" num_of_dangling_reqs, num_of_orphan_reqs, " +
				" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
				" num_of_completed_reqs, num_of_incomplete_reqs , " +
				" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs " +
				" from gr_folder_metrics fm " +
				" where fm.project_id =? " +
				" and fm.folder_id = ? " +
				" and fm.data_load_dt = to_date('"+ maxDataLoadDt+ "','DD MON YYYY HH MI SS')";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
		
			rs = prepStmt.executeQuery();
			
			
			while (rs.next()){
				String dataLoadDt = rs.getString("data_load_dt");
				String requirementTypeShortName = rs.getString("requirement_type_short_name");
				int numOfRequirements = rs.getInt("num_of_requirements");
				int numOfDraftRequirements = rs.getInt("num_of_draft_reqs");
				int numOfInApprovalWorkflowRequirements = rs.getInt("num_of_in_workflow_reqs");
				int numOfRejectedRequirements = rs.getInt("num_of_rejected_reqs");
				int numOfApprovedRequirements = rs.getInt("num_of_approved_reqs");
				int numOfDanglingRequirements = rs.getInt("num_of_dangling_reqs");
				int numOfOrphanRequirements = rs.getInt("num_of_orphan_reqs");
				int numOfSuspectUpstreamRequirements = rs.getInt("num_of_suspect_upstream_reqs");
				int numOfSuspectDownstreamRequirements = rs.getInt("num_of_suspect_downstream_reqs");
				int numOfCompletedRequirements = rs.getInt("num_of_completed_reqs");
				int numOfIncompleteRequirements = rs.getInt("num_of_incomplete_reqs");
				int numOfTestPendingRequirements = rs.getInt("num_of_test_pending_reqs");
				int numOfTestPassRequirements = rs.getInt("num_of_test_pass_reqs");
				int numOfTestFailRequirements = rs.getInt("num_of_test_fail_reqs");
				
				
				String row = dataLoadDt + ":##:" + 
					requirementTypeShortName + ":##:" + 
					numOfRequirements + ":##:" + 
					numOfDraftRequirements + ":##:" + 
					numOfInApprovalWorkflowRequirements + ":##:" + 
					numOfRejectedRequirements + ":##:" + 
					numOfApprovedRequirements + ":##:" + 
					numOfDanglingRequirements + ":##:" + 
					numOfOrphanRequirements + ":##:" + 
					numOfSuspectUpstreamRequirements + ":##:" + 
					numOfSuspectDownstreamRequirements + ":##:" + 
					numOfCompletedRequirements + ":##:" + 
					numOfIncompleteRequirements + ":##:" + 
					numOfTestPendingRequirements + ":##:" +
					numOfTestPassRequirements + ":##:" +
					numOfTestFailRequirements ;
;
				dataTableArrayList.add(row);
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
		
		return (dataTableArrayList);
	}
	*/
	
	
	// returns an array string for printing a data table for each req type inthe folder.

	public static ArrayList  getDataTableArrayForProjectByFolders(int projectId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList dataTableArrayList = new ArrayList();
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "";

			// lets get the max data_load_dt from the gr_release_metrics tabel for this release.
			// This sql gets the total number of requirements in a folder.
			
			// crunch for baseline.
			if (databaseType.equals("mySQL")){
				sql = "select date_format(max(data_load_dt), '%d %m %y %h:%i:%s') \"data_load_dt\"" +
					" from gr_folder_metrics fm " +
					" where fm.project_id =? ";
			}
			else {
				sql = "select to_char(max(data_load_dt), 'DD MON YYYY HH MI SS') \"data_load_dt\"" +
				" from gr_folder_metrics fm " +
				" where fm.project_id =? " ;
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
		
			rs = prepStmt.executeQuery();
			
			String maxDataLoadDt = "";
			while (rs.next()){
				maxDataLoadDt = rs.getString("data_load_dt");
			}
			
			// This sql gets the total number of requirements in a folder.
			
			// crunch for baseline.
			if (databaseType.equals("mySQL")){
				sql = "select date_format(data_load_dt, '%d %m %y %h:%i:%s') \"data_load_dt\"," +
					" f.folder_path, " +
					" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs, " +
					" num_of_rejected_reqs, num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs, " +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs , " +
					" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs, f.id " +
					" from gr_folder_metrics fm, gr_folders f, gr_requirement_types rt " +
					" where fm.project_id =? " +
					" and fm.folder_id = f.id " +
					" and f.requirement_type_id = rt.id " + 
					" and fm.data_load_dt = str_to_date('"+ maxDataLoadDt+ "','%d %m %y %h:%i:%s')" + 
					" order by rt.display_sequence, f.folder_path";
			}
			else {
				sql = "select to_char(data_load_dt, 'DD MON YYYY HH MI SS') \"data_load_dt\"," +
				" f.folder_path, " +
				" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs, " +
				" num_of_rejected_reqs, num_of_approved_reqs, " +
				" num_of_dangling_reqs, num_of_orphan_reqs, " +
				" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
				" num_of_completed_reqs, num_of_incomplete_reqs , " +
				" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs, f.id " +
				" from gr_folder_metrics fm , gr_folders f , gr_requirement_types rt " +
				" where fm.project_id =? " +
				" fm.folder_id = f.id " + 
				" and f.requirement_type_id = rt.id " + 
				" and fm.data_load_dt = to_date('"+ maxDataLoadDt+ "','DD MON YYYY HH MI SS')" + 
				" order by rt.display_sequence,  f.folder_path";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
		
			rs = prepStmt.executeQuery();
			
			
			while (rs.next()){
				String dataLoadDt = rs.getString("data_load_dt");
				String folderPath = rs.getString("folder_path");
				int numOfRequirements = rs.getInt("num_of_requirements");
				int numOfDraftRequirements = rs.getInt("num_of_draft_reqs");
				int numOfInApprovalWorkflowRequirements = rs.getInt("num_of_in_workflow_reqs");
				int numOfRejectedRequirements = rs.getInt("num_of_rejected_reqs");
				int numOfApprovedRequirements = rs.getInt("num_of_approved_reqs");
				int numOfDanglingRequirements = rs.getInt("num_of_dangling_reqs");
				int numOfOrphanRequirements = rs.getInt("num_of_orphan_reqs");
				int numOfSuspectUpstreamRequirements = rs.getInt("num_of_suspect_upstream_reqs");
				int numOfSuspectDownstreamRequirements = rs.getInt("num_of_suspect_downstream_reqs");
				int numOfCompletedRequirements = rs.getInt("num_of_completed_reqs");
				int numOfIncompleteRequirements = rs.getInt("num_of_incomplete_reqs");
				int numOfTestPendingRequirements = rs.getInt("num_of_test_pending_reqs");
				int numOfTestPassRequirements = rs.getInt("num_of_test_pass_reqs");
				int numOfTestFailRequirements = rs.getInt("num_of_test_fail_reqs");
				
				int folderId = rs.getInt("id");
				
				
				String row = dataLoadDt + ":##:" + 
					folderPath  + ":##:" + 
					numOfRequirements + ":##:" + 
					numOfDraftRequirements + ":##:" + 
					numOfInApprovalWorkflowRequirements + ":##:" + 
					numOfRejectedRequirements + ":##:" + 
					numOfApprovedRequirements + ":##:" + 
					numOfDanglingRequirements + ":##:" + 
					numOfOrphanRequirements + ":##:" + 
					numOfSuspectUpstreamRequirements + ":##:" + 
					numOfSuspectDownstreamRequirements + ":##:" + 
					numOfCompletedRequirements + ":##:" + 
					numOfIncompleteRequirements + ":##:" + 
					numOfTestPendingRequirements + ":##:" +
					numOfTestPassRequirements + ":##:" +
					numOfTestFailRequirements + ":##:" + folderId;
;
				dataTableArrayList.add(row);
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
		
		return (dataTableArrayList);
	}
	
	
	// returns a YUI chartables datastring for allRequirements in different Req Types in a release.
	// used in trending release metrics
	// if release == 0, we crunch for project.
	public static String  getTrendDataStringForReleaseOrProject
		(int releaseId, int projectId, String dataType, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		String dataString = "";
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "";
			
			
			// first lets get the total number of requirements on any given dataloaddt and store it in a hash map.
			if (releaseId ==0 ) {
				// this is a project level search.
				if (databaseType.equals("mySQL")){
					sql = "select  date_format(data_load_dt, '%d %M %y') \"data_load_dt\", sum(num_of_requirements) \"total_reqs\" " +
						 " from gr_project_metrics pm " +
						 " where pm.project_id = ? " +
						 " group by data_load_dt ";
				}
				else {
					sql = "select  to_char(data_load_dt, 'DD MON YYYY') \"data_load_dt\", sum(num_of_requirements) \"total_reqs\" " +
					 " from gr_project_metrics pm " +
					 " where pm.project_id = ? " +
					 " group by data_load_dt ";
				}
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
			}
			else {
				// this is s release level search.
				if (databaseType.equals("mySQL")){
					sql = "select  date_format(data_load_dt, '%d %M %y') \"data_load_dt\", sum(num_of_requirements) \"total_reqs\" " +
						 " from gr_release_metrics rm " +
						 " where rm.project_id = ? " +
						 " and rm.release_id = ? " +
						 " group by data_load_dt ";
				}
				else {
					sql = "select  to_char(data_load_dt, 'DD MON YYYY') \"data_load_dt\", sum(num_of_requirements) \"total_reqs\" " +
					 " from gr_release_metrics rm " +
					 " where rm.project_id = ? " +
					 " and rm.release_id = ? " +
					 " group by data_load_dt ";
				}
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.setInt(2, releaseId);
			}
			
			rs = prepStmt.executeQuery();
			HashMap totalReqsMap = new HashMap(); 
			while(rs.next()) {
				totalReqsMap.put( rs.getString("data_load_dt"), new Integer(rs.getInt("total_reqs")) );
			}
			prepStmt.close();
			rs.close();

			/* NOTE :
			 * We could have done this a lot more easily by quering for distinct dates and for each date
			 * running a seperate sql. howver that would have been very db intensive. so coding this convoluted
			 * logic in to Java. Feel free to change if you can. (without putting bugs in it of course).
			*/
			// This sql gets the total number of requirements in a folder.
			if (releaseId ==0 ) {
				// this is a project level search.
				if (databaseType.equals("mySQL")){
					sql = "select data_load_dt, date_format(data_load_dt, '%d %M %y') \"formatted_data_load_dt\"," +
						" requirement_type_short_name, " +
						" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs," +
						" num_of_rejected_reqs,  num_of_approved_reqs, " +
						" num_of_dangling_reqs, num_of_orphan_reqs," +
						" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
						" num_of_completed_reqs, num_of_incomplete_reqs, " +
						" num_of_test_pending_reqs,  num_of_test_pass_reqs,  num_of_test_fail_reqs " + 
						" from gr_project_metrics rm " +
						" where rm.project_id =? " +
						" order by data_load_dt asc, requirement_type_short_name";
				}
				else {
					sql = "select data_load_dt, to_char(data_load_dt, 'DD MON YYYY') \"formatted_data_load_dt\"," +
					" requirement_type_short_name, " +
					" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs," +
					" num_of_rejected_reqs,  num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs," +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs, " +
					" num_of_test_pending_reqs,  num_of_test_pass_reqs,  num_of_test_fail_reqs " + 
					" from gr_project_metrics rm " +
					" where rm.project_id =? " +
					" order by data_load_dt asc, requirement_type_short_name";
				}
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);

			}
			else {
				// this is s release level search.
				if (databaseType.equals("mySQL")){
					sql = "select data_load_dt, date_format(data_load_dt, '%d %M %y') \"formatted_data_load_dt\"," +
						" requirement_type_short_name, " +
						" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs," +
						" num_of_rejected_reqs,  num_of_approved_reqs, " +
						" num_of_dangling_reqs, num_of_orphan_reqs," +
						" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
						" num_of_completed_reqs, num_of_incomplete_reqs, " +
						" num_of_test_pending_reqs,  num_of_test_pass_reqs,  num_of_test_fail_reqs " + 
						" from gr_release_metrics rm " +
						" where rm.project_id =? " +
						" and rm.release_id = ? " +
						" order by data_load_dt asc, requirement_type_short_name";
				}
				else {
					sql = "select data_load_dt, to_char(data_load_dt, 'DD MON YYYY') \"formatted_data_load_dt\"," +
					" requirement_type_short_name, " +
					" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs," +
					" num_of_rejected_reqs,  num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs," +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs, " +
					" num_of_test_pending_reqs,  num_of_test_pass_reqs,  num_of_test_fail_reqs " + 
					" from gr_release_metrics rm " +
					" where rm.project_id =? " +
					" and rm.release_id = ? " +
					" order by data_load_dt asc, requirement_type_short_name";
				}
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.setInt(2, releaseId);
			
			}
			
			rs = prepStmt.executeQuery();

			dataString = "[";
			String dataRow = "";
			String dataLoadDt = "";
			String oldDataLoadDt = "";
			while (rs.next()){
				dataLoadDt = rs.getString("formatted_data_load_dt");
				String requirementTypeShortName = rs.getString("requirement_type_short_name");
				int numOfRequirements = rs.getInt("num_of_requirements");
				int numOfDraftRequirements = rs.getInt("num_of_draft_reqs");
				int numOfInApprovalWorkflowRequirements = rs.getInt("num_of_in_workflow_reqs");
				int numOfRejectedRequirements = rs.getInt("num_of_rejected_reqs");
				int numOfApprovedRequirements = rs.getInt("num_of_approved_reqs");
				int numOfDanglingRequirements = rs.getInt("num_of_dangling_reqs");
				int numOfOrphanRequirements = rs.getInt("num_of_orphan_reqs");
				int numOfSuspectUpstreamRequirements = rs.getInt("num_of_suspect_upstream_reqs");
				int numOfSuspectDownstreamRequirements = rs.getInt("num_of_suspect_downstream_reqs");
				int numOfCompletedRequirements = rs.getInt("num_of_completed_reqs");
				int numOfIncompleteRequirements = rs.getInt("num_of_incomplete_reqs");
				int numOfTestPendingRequirements = rs.getInt("num_of_test_pending_reqs");
				int numOfTestPassRequirements = rs.getInt("num_of_test_pass_reqs");
				int numOfTestFailRequirements = rs.getInt("num_of_test_fail_reqs");
				
				int dataNumber = 0;
				if (dataType.equals("numOfRequirements")) {
					dataNumber = numOfRequirements;
				}
				if (dataType.equals("numOfDraftRequirements")) {
					dataNumber = numOfDraftRequirements;
				}
				if (dataType.equals("numOfInApprovalWorkflowRequirements")) {
					dataNumber = numOfInApprovalWorkflowRequirements;
				}
				if (dataType.equals("numOfRejectedRequirements")) {
					dataNumber = numOfRejectedRequirements;
				}
				if (dataType.equals("numOfApprovedRequirements")) {
					dataNumber = numOfApprovedRequirements;
				}
				if (dataType.equals("numOfDanglingRequirements")) {
					dataNumber = numOfDanglingRequirements;
				}
				if (dataType.equals("numOfOrphanRequirements")) {
					dataNumber = numOfOrphanRequirements;
				}
				if (dataType.equals("numOfSuspectUpstreamRequirements")) {
					dataNumber = numOfSuspectUpstreamRequirements;
				}
				if (dataType.equals("numOfSuspectDownstreamRequirements")) {
					dataNumber = numOfSuspectDownstreamRequirements;
				}
				if (dataType.equals("numOfCompletedRequirements")) {
					dataNumber = numOfCompletedRequirements;
				}
				if (dataType.equals("numOfIncompleteRequirements")) {
					dataNumber = numOfIncompleteRequirements;
				}
				if (dataType.equals("numOfTestPendingRequirements")) {
					dataNumber = numOfTestPendingRequirements;
				}		
				if (dataType.equals("numOfTestPassRequirements")) {
					dataNumber = numOfTestPassRequirements;
				}		
				if (dataType.equals("numOfTestFailRequirements")) {
					dataNumber = numOfTestFailRequirements;
				}		
				
				
				if (dataLoadDt.equals(oldDataLoadDt)) {
					// still running the old sequence. add it to current dataRow
					dataRow +=  requirementTypeShortName + ":" +  dataNumber +  "," ;
				}
				else {
					// a new data load dt has started. 
					// lets process and reset the dataRow.
					// drop the last , in row
					if (!dataRow.equals("")) {
						if (dataRow.contains(",")){
							dataRow = (String) dataRow.subSequence(0,dataRow.lastIndexOf(","));
						}			
						// lets get the total number of reqs on the oldDataLoadDt.
						int totalReqs = 0;
						Integer totalReqsInteger = (Integer) totalReqsMap.get(oldDataLoadDt);
						if (totalReqsInteger != null) {
							totalReqs = totalReqsInteger.intValue();
						}
						dataString += "\n{" +
					 		"dataLoadDt:\"" +  oldDataLoadDt +  "\", totalReqs:" + totalReqs + ", " + dataRow + "},";
					
					}
					//so lets reset the old Data Load to new value and add the current req type short name and num
					// of requirements to the new dataRow.
					oldDataLoadDt = dataLoadDt;
					dataRow =  requirementTypeShortName + ":" +  dataNumber +  "," ;
				}
				
			}

			// the above logic leaves out the last data row. so lets add it to the allRequirements Data String.
			if (!dataRow.equals("")) {
				if (dataRow.contains(",")){
					dataRow = (String) dataRow.subSequence(0,dataRow.lastIndexOf(","));
				}		
				// lets get the total number of reqs on the dataLoadDt.
				int totalReqs = 0;
				Integer totalReqsInteger = (Integer) totalReqsMap.get(dataLoadDt);
				if (totalReqsInteger != null) {
					totalReqs = totalReqsInteger.intValue();
				}
				dataString += "\n{" +
					"dataLoadDt:\"" +  dataLoadDt + "\", totalReqs:" + totalReqs + ", " + dataRow + "},";

			}
			
			if (dataString.contains(",")) {
				dataString = (String) dataString.subSequence(0,dataString.lastIndexOf(","));
			}
			// lets add the closing ]
			dataString += "]  ";
			
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
		
		return (dataString);
	}
	
	public static String  getTrendDataStringForReleaseOrProject2(int releaseId, Project project, String dataType,
			String displayRequirementTypes, String fromDate, String toDate){
	PreparedStatement prepStmt = null;
	ResultSet rs = null;
	java.sql.Connection con = null;
	String newDataString = "";
	
	ArrayList<String> allDates = new ArrayList<String>();
	//ArrayList<RequirementType> reqTypes = project.getMyRequirementTypes();
	ArrayList<String> reqTypes = ReleaseMetricsUtil.getRequirementTypesForReleaseOrProject(releaseId,project.getProjectId(),displayRequirementTypes);
			
	HashMap<String, Integer> reqCount = new HashMap<String, Integer>();
	
	try {

		
		javax.naming.InitialContext context = new InitialContext();
		javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
		con = dataSource.getConnection();
		
		String sql = "";
		
		
		// first lets get the total number of requirements on any given dataloaddt and store it in a hash map.
		if (releaseId ==0 ) {
			// this is a project level search.
			sql = "select  date_format(data_load_dt, '%Y,%m,%d') \"data_load_dt\", sum(num_of_requirements) \"total_reqs\" " +
				 " from gr_project_metrics pm " +
				 " where pm.project_id = ? " ;
			
			if (!(fromDate.equals(""))){
				sql += " and data_load_dt >= STR_TO_DATE('"+ fromDate +"','%m/%d/%Y') ";
			}
			if (!(toDate.equals(""))){
				sql += " and data_load_dt <= STR_TO_DATE('"+ toDate +"','%m/%d/%Y') ";
			}
			sql += " group by data_load_dt ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, project.getProjectId());
		}
		else {
			// this is s release level search.
			sql = "select  date_format(data_load_dt, '%Y,%m,%d') \"data_load_dt\", sum(num_of_requirements) \"total_reqs\" " +
				 " from gr_release_metrics rm " +
				 " where rm.project_id = ? " +
				 " and rm.release_id = ? " ;

			
			if (!(fromDate.equals(""))){
				sql += " and data_load_dt >= STR_TO_DATE('"+ fromDate +"','%m/%d/%Y') ";
			}
			if (!(toDate.equals(""))){
				sql += " and data_load_dt <= STR_TO_DATE('"+ toDate +"','%m/%d/%Y') ";
			}
			sql += " group by data_load_dt ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, project.getProjectId());
			prepStmt.setInt(2, releaseId);
		}
		
		rs = prepStmt.executeQuery();
		HashMap totalReqsMap = new HashMap(); 
		while(rs.next()) {
			totalReqsMap.put( rs.getString("data_load_dt"), new Integer(rs.getInt("total_reqs")) );
			allDates.add( rs.getString("data_load_dt"));
		}
		prepStmt.close();
		rs.close();

		/* NOTE :
		 * We could have done this a lot more easily by quering for distinct dates and for each date
		 * running a seperate sql. howver that would have been very db intensive. so coding this convoluted
		 * logic in to Java. Feel free to change if you can. (without putting bugs in it of course).
		*/
		// This sql gets the total number of requirements in a folder.
		if (releaseId ==0 ) {
			// this is a project level search.
			sql = "select data_load_dt, date_format(data_load_dt, '%Y,%m,%d') \"formatted_data_load_dt\"," +
				" requirement_type_short_name, " +
				" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs," +
				" num_of_rejected_reqs,  num_of_approved_reqs, " +
				" num_of_dangling_reqs, num_of_orphan_reqs," +
				" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
				" num_of_completed_reqs, num_of_incomplete_reqs, " +
				" num_of_test_pending_reqs,  num_of_test_pass_reqs,  num_of_test_fail_reqs " + 
				" from gr_project_metrics rm " +
				" where rm.project_id =? " ;
			

			
			if (!(fromDate.equals(""))){
				sql += " and data_load_dt >= STR_TO_DATE('"+ fromDate +"','%m/%d/%Y') ";
			}
			if (!(toDate.equals(""))){
				sql += " and data_load_dt <= STR_TO_DATE('"+ toDate +"','%m/%d/%Y') ";
			}
			sql += " order by data_load_dt asc, requirement_type_short_name";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, project.getProjectId());

		}
		else {
			// this is s release level search.
			sql = "select data_load_dt, date_format(data_load_dt, '%Y,%m,%d') \"formatted_data_load_dt\"," +
				" requirement_type_short_name, " +
				" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs," +
				" num_of_rejected_reqs,  num_of_approved_reqs, " +
				" num_of_dangling_reqs, num_of_orphan_reqs," +
				" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
				" num_of_completed_reqs, num_of_incomplete_reqs, " +
				" num_of_test_pending_reqs,  num_of_test_pass_reqs,  num_of_test_fail_reqs " + 
				" from gr_release_metrics rm " +
				" where rm.project_id =? " +
				" and rm.release_id = ? "; 

			if (!(fromDate.equals(""))){
				sql += " and data_load_dt >= STR_TO_DATE('"+ fromDate +"','%m/%d/%Y') ";
			}
			if (!(toDate.equals(""))){
				sql += " and data_load_dt <= STR_TO_DATE('"+ toDate +"','%m/%d/%Y') ";
			}
			sql += " order by data_load_dt asc, requirement_type_short_name";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, project.getProjectId());
			prepStmt.setInt(2, releaseId);
		
			
		}
		
		System.out.println("srt in sbux : sql is " + sql + " projectId is " + project.getProjectId() + " rel is " + releaseId);
		rs = prepStmt.executeQuery();
		String dataLoadDt = "";
		while (rs.next()){
			dataLoadDt = rs.getString("formatted_data_load_dt");
			String requirementTypeShortName = rs.getString("requirement_type_short_name");
			int numOfRequirements = rs.getInt("num_of_requirements");
			int numOfDraftRequirements = rs.getInt("num_of_draft_reqs");
			int numOfInApprovalWorkflowRequirements = rs.getInt("num_of_in_workflow_reqs");
			int numOfRejectedRequirements = rs.getInt("num_of_rejected_reqs");
			int numOfApprovedRequirements = rs.getInt("num_of_approved_reqs");
			int numOfDanglingRequirements = rs.getInt("num_of_dangling_reqs");
			int numOfOrphanRequirements = rs.getInt("num_of_orphan_reqs");
			int numOfSuspectUpstreamRequirements = rs.getInt("num_of_suspect_upstream_reqs");
			int numOfSuspectDownstreamRequirements = rs.getInt("num_of_suspect_downstream_reqs");
			int numOfCompletedRequirements = rs.getInt("num_of_completed_reqs");
			int numOfIncompleteRequirements = rs.getInt("num_of_incomplete_reqs");
			int numOfTestPendingRequirements = rs.getInt("num_of_test_pending_reqs");
			int numOfTestPassRequirements = rs.getInt("num_of_test_pass_reqs");
			int numOfTestFailRequirements = rs.getInt("num_of_test_fail_reqs");
			
			
			System.out.println("srt in sbux : numOfIncompleteRequirements is " + numOfIncompleteRequirements + " dataType is " + dataType);
			int dataNumber = 0;
			if (dataType.equals("numOfRequirements")) {
				dataNumber = numOfRequirements;
			}
			if (dataType.equals("numOfDraftRequirements")) {
				dataNumber = numOfDraftRequirements;
			}
			if (dataType.equals("numOfInApprovalWorkflowRequirements")) {
				dataNumber = numOfInApprovalWorkflowRequirements;
			}
			if (dataType.equals("numOfRejectedRequirements")) {
				dataNumber = numOfRejectedRequirements;
			}
			if (dataType.equals("numOfApprovedRequirements")) {
				dataNumber = numOfApprovedRequirements;
			}
			if (dataType.equals("numOfDanglingRequirements")) {
				dataNumber = numOfDanglingRequirements;
			}
			if (dataType.equals("numOfOrphanRequirements")) {
				dataNumber = numOfOrphanRequirements;
			}
			if (dataType.equals("numOfSuspectUpstreamRequirements")) {
				dataNumber = numOfSuspectUpstreamRequirements;
			}
			if (dataType.equals("numOfSuspectDownstreamRequirements")) {
				dataNumber = numOfSuspectDownstreamRequirements;
			}
			if (dataType.equals("numOfCompletedRequirements")) {
				dataNumber = numOfCompletedRequirements;
				
			}
			if (dataType.equals("numOfIncompleteRequirements")) {
				dataNumber = numOfIncompleteRequirements;
			}
			if (dataType.equals("numOfTestPendingRequirements")) {
				dataNumber = numOfTestPendingRequirements;
			}		
			if (dataType.equals("numOfTestPassRequirements")) {
				dataNumber = numOfTestPassRequirements;
			}		
			if (dataType.equals("numOfTestFailRequirements")) {
				dataNumber = numOfTestFailRequirements;
			}		
			
			
			String hashKey = dataLoadDt + ":" + requirementTypeShortName;
			
			String rTPrefix = requirementTypeShortName;
			if (!(displayRequirementTypes.equals(""))){
				// show only in displayReqType
				if (displayRequirementTypes.contains(rTPrefix + ",")){
					reqCount.put(hashKey, dataNumber);
				}
			}
			else{
				reqCount.put(hashKey, dataNumber);
			}
			
			
			
			
			
		}
		
		prepStmt.close();
		rs.close();
		con.close();
		
		
		newDataString = "[";
			// add row 1
			newDataString += "['Date',";
			for (String rT : reqTypes){
				newDataString += "'" + rT + "',";
			}
			if (newDataString.contains(",")) {
				newDataString = (String) newDataString.subSequence(0,newDataString.lastIndexOf(","));
			}
			newDataString += "],";
			
			
			
			// add remaining rows.  One row per date
			for (String runDate : allDates){
				newDataString += "[" + " new Date("  + runDate + ") ,";
				for (String rT : reqTypes){
					String hashKey = runDate + ":" + rT;
					Integer data = 0;
					
					try {
						data = reqCount.get(hashKey);
						newDataString += data.toString() + ",";
					}
					catch (Exception reqTypeNotInRelException ){
						// do nothing
						newDataString +=   "0,";
					}
					
					
				}
				if (newDataString.contains(",")) {
					newDataString = (String) newDataString.subSequence(0,newDataString.lastIndexOf(","));
				}
				newDataString += "]" + ",";
			}
			
			
			if (newDataString.contains(",")) {
				newDataString = (String) newDataString.subSequence(0,newDataString.lastIndexOf(","));
			}
			
		newDataString += "]";
	
		
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
	
	return (newDataString);
}

	public static String  getTrendDataStringForAgileSprint
	(int sprintId, int projectId, String dataType, String databaseType){
	PreparedStatement prepStmt = null;
	ResultSet rs = null;
	java.sql.Connection con = null;
	String dataString = "";
	try {

		
		javax.naming.InitialContext context = new InitialContext();
		javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
		con = dataSource.getConnection();
		
		String sql = "";
		
		
		// first lets get the total number of requirements on any given dataloaddt and store it in a hash map.
		// this is a project level search.
		if (databaseType.equals("mySQL")){
			sql = "select  date_format(data_load_dt, '%d %M %y') \"data_load_dt\", sum(num_of_requirements) \"total_reqs\" " +
				 " from gr_sprint_metrics pm " +
				 " where pm.project_id = ? and pm.sprint_id = ? " +
				 " group by data_load_dt ";
		}
		else {
			sql = "select  to_char(data_load_dt, 'DD MON YYYY') \"data_load_dt\", sum(num_of_requirements) \"total_reqs\" " +
			 " from gr_sprint_metrics pm " +
			 " where pm.project_id = ?  and pm.sprint_id = ? " +
			 " group by data_load_dt ";
		}
		prepStmt = con.prepareStatement(sql);
		prepStmt.setInt(1, projectId);
		prepStmt.setInt(2, sprintId) ; 

		
		rs = prepStmt.executeQuery();
		HashMap totalReqsMap = new HashMap(); 
		while(rs.next()) {
			totalReqsMap.put( rs.getString("data_load_dt"), new Integer(rs.getInt("total_reqs")) );
		}
		prepStmt.close();
		rs.close();

		/* NOTE :
		 * We could have done this a lot more easily by querying for distinct dates and for each date
		 * running a seperate sql. howver that would have been very db intensive. so coding this convoluted
		 * logic in to Java. Feel free to change if you can. (without putting bugs in it of course).
		*/
		// This sql gets the total number of requirements in a folder.
		
		// this is a project level search.
		if (databaseType.equals("mySQL")){
			sql = "select data_load_dt, date_format(data_load_dt, '%d %M %y') \"formatted_data_load_dt\"," +
				" requirement_type_short_name, " +
				" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs," +
				" num_of_rejected_reqs,  num_of_approved_reqs, " +
				" num_of_dangling_reqs, num_of_orphan_reqs," +
				" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
				" num_of_completed_reqs, num_of_incomplete_reqs, " +
				" num_of_test_pending_reqs,  num_of_test_pass_reqs,  num_of_test_fail_reqs " + 
				" from gr_sprint_metrics rm " +
				" where rm.project_id =? and rm.sprint_id = ? " +
				" order by data_load_dt asc, requirement_type_short_name";
		}
		else {
			sql = "select data_load_dt, to_char(data_load_dt, 'DD MON YYYY') \"formatted_data_load_dt\"," +
			" requirement_type_short_name, " +
			" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs," +
			" num_of_rejected_reqs,  num_of_approved_reqs, " +
			" num_of_dangling_reqs, num_of_orphan_reqs," +
			" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
			" num_of_completed_reqs, num_of_incomplete_reqs, " +
			" num_of_test_pending_reqs,  num_of_test_pass_reqs,  num_of_test_fail_reqs " + 
			" from gr_sprint_metrics rm " +
			" where rm.project_id =? and rm.sprint_id = ? " +
			" order by data_load_dt asc, requirement_type_short_name";
		}
		prepStmt = con.prepareStatement(sql);
		prepStmt.setInt(1, projectId);
		prepStmt.setInt(2, sprintId);

	
		rs = prepStmt.executeQuery();

		dataString = "[";
		String dataRow = "";
		String dataLoadDt = "";
		String oldDataLoadDt = "";
		while (rs.next()){
			dataLoadDt = rs.getString("formatted_data_load_dt");
			String requirementTypeShortName = rs.getString("requirement_type_short_name");
			int numOfRequirements = rs.getInt("num_of_requirements");
			int numOfDraftRequirements = rs.getInt("num_of_draft_reqs");
			int numOfInApprovalWorkflowRequirements = rs.getInt("num_of_in_workflow_reqs");
			int numOfRejectedRequirements = rs.getInt("num_of_rejected_reqs");
			int numOfApprovedRequirements = rs.getInt("num_of_approved_reqs");
			int numOfDanglingRequirements = rs.getInt("num_of_dangling_reqs");
			int numOfOrphanRequirements = rs.getInt("num_of_orphan_reqs");
			int numOfSuspectUpstreamRequirements = rs.getInt("num_of_suspect_upstream_reqs");
			int numOfSuspectDownstreamRequirements = rs.getInt("num_of_suspect_downstream_reqs");
			int numOfCompletedRequirements = rs.getInt("num_of_completed_reqs");
			int numOfIncompleteRequirements = rs.getInt("num_of_incomplete_reqs");
			int numOfTestPendingRequirements = rs.getInt("num_of_test_pending_reqs");
			int numOfTestPassRequirements = rs.getInt("num_of_test_pass_reqs");
			int numOfTestFailRequirements = rs.getInt("num_of_test_fail_reqs");
			
			int dataNumber = 0;
			if (dataType.equals("numOfRequirements")) {
				dataNumber = numOfRequirements;
			}
			if (dataType.equals("numOfDraftRequirements")) {
				dataNumber = numOfDraftRequirements;
			}
			if (dataType.equals("numOfInApprovalWorkflowRequirements")) {
				dataNumber = numOfInApprovalWorkflowRequirements;
			}
			if (dataType.equals("numOfRejectedRequirements")) {
				dataNumber = numOfRejectedRequirements;
			}
			if (dataType.equals("numOfApprovedRequirements")) {
				dataNumber = numOfApprovedRequirements;
			}
			if (dataType.equals("numOfDanglingRequirements")) {
				dataNumber = numOfDanglingRequirements;
			}
			if (dataType.equals("numOfOrphanRequirements")) {
				dataNumber = numOfOrphanRequirements;
			}
			if (dataType.equals("numOfSuspectUpstreamRequirements")) {
				dataNumber = numOfSuspectUpstreamRequirements;
			}
			if (dataType.equals("numOfSuspectDownstreamRequirements")) {
				dataNumber = numOfSuspectDownstreamRequirements;
			}
			if (dataType.equals("numOfCompletedRequirements")) {
				dataNumber = numOfCompletedRequirements;
			}
			if (dataType.equals("numOfIncompleteRequirements")) {
				dataNumber = numOfIncompleteRequirements;
			}
			if (dataType.equals("numOfTestPendingRequirements")) {
				dataNumber = numOfTestPendingRequirements;
			}		
			if (dataType.equals("numOfTestPassRequirements")) {
				dataNumber = numOfTestPassRequirements;
			}		
			if (dataType.equals("numOfTestFailRequirements")) {
				dataNumber = numOfTestFailRequirements;
			}		
			
			
			if (dataLoadDt.equals(oldDataLoadDt)) {
				// still running the old sequence. add it to current dataRow
				dataRow +=  requirementTypeShortName + ":" +  dataNumber +  "," ;
			}
			else {
				// a new data load dt has started. 
				// lets process and reset the dataRow.
				// drop the last , in row
				if (!dataRow.equals("")) {
					if (dataRow.contains(",")){
						dataRow = (String) dataRow.subSequence(0,dataRow.lastIndexOf(","));
					}			
					// lets get the total number of reqs on the oldDataLoadDt.
					int totalReqs = 0;
					Integer totalReqsInteger = (Integer) totalReqsMap.get(oldDataLoadDt);
					if (totalReqsInteger != null) {
						totalReqs = totalReqsInteger.intValue();
					}
					dataString += "\n{" +
				 		"dataLoadDt:\"" +  oldDataLoadDt +  "\", totalReqs:" + totalReqs + ", " + dataRow + "},";
				
				}
				//so lets reset the old Data Load to new value and add the current req type short name and num
				// of requirements to the new dataRow.
				oldDataLoadDt = dataLoadDt;
				dataRow =  requirementTypeShortName + ":" +  dataNumber +  "," ;
			}
			
		}

		// the above logic leaves out the last data row. so lets add it to the allRequirements Data String.
		if (!dataRow.equals("")) {
			if (dataRow.contains(",")){
				dataRow = (String) dataRow.subSequence(0,dataRow.lastIndexOf(","));
			}		
			// lets get the total number of reqs on the dataLoadDt.
			int totalReqs = 0;
			Integer totalReqsInteger = (Integer) totalReqsMap.get(dataLoadDt);
			if (totalReqsInteger != null) {
				totalReqs = totalReqsInteger.intValue();
			}
			dataString += "\n{" +
				"dataLoadDt:\"" +  dataLoadDt + "\", totalReqs:" + totalReqs + ", " + dataRow + "},";

		}
		
		if (dataString.contains(",")) {
			dataString = (String) dataString.subSequence(0,dataString.lastIndexOf(","));
		}
		// lets add the closing ]
		dataString += "]  ";
		
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
	
	return (dataString);
}

	
	
	
	
	// returns a YUI chartables datastring for all defects status in a project or release.
	
	public static String  getDefectTrendDataString (String filterType, int filterObjectId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		String dataString = "[";
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			


			String sql1 = "";
			String sql2 = "";
			
			if (filterType.equals("project")){
				// lets get the list of dates for which data points exist
				// and for each date we will get all the status and values.
				if (databaseType.equals("mySQL")){
					sql1 = "select distinct data_load_dt ,date_format(data_load_dt, '%d %M %y') \"formatted_data_load_dt\"" +
						" from gr_defect_metrics " +
						" where metrics_type = 'project'" +
						" and project_id = ? ";
				}
				else {
					sql1 = "select distinct data_load_dt ,to_char(data_load_dt, 'DD MON YYYY') \"formatted_data_load_dt\"" +
					" from gr_defect_metrics " +
					" where metrics_type = 'project'" +
					" and project_id = ? ";
			
				}
				sql2 = "select defect_status_group, num_of_requirements " +
				" from gr_defect_metrics" +
				" where metrics_type = 'project'" +
				" and project_id = ? " +
				" and data_load_dt = ? ";
			}
			
			if (filterType.equals("release")){
				// lets get the list of dates for which data points exist
				// and for each date we will get all the status and values.
				if (databaseType.equals("mySQL")){
					sql1 = "select distinct data_load_dt ,date_format(data_load_dt, '%d %M %y') \"formatted_data_load_dt\"" +
						" from gr_defect_metrics " +
						" where metrics_type = 'release'" +
						" and release_id = ? ";
				}
				else {
					sql1 = "select distinct data_load_dt ,to_char(data_load_dt, 'DD MON YYYY') \"formatted_data_load_dt\"" +
					" from gr_defect_metrics " +
					" where metrics_type = 'release'" +
					" and release_id = ? ";
			
				}
				sql2 = "select defect_status_group, num_of_requirements " +
				" from gr_defect_metrics" +
				" where metrics_type = 'release'" +
				" and release_id = ? " +
				" and data_load_dt = ? ";
			}

			if (filterType.equals("baseline")){
				// lets get the list of dates for which data points exist
				// and for each date we will get all the status and values.
				if (databaseType.equals("mySQL")){
					sql1 = "select distinct data_load_dt ,date_format(data_load_dt, '%d %M %y') \"formatted_data_load_dt\"" +
						" from gr_defect_metrics " +
						" where metrics_type = 'baseline'" +
						" and rt_baseline_id = ? ";
				}
				else {
					sql1 = "select distinct data_load_dt ,to_char(data_load_dt, 'DD MON YYYY') \"formatted_data_load_dt\"" +
					" from gr_defect_metrics " +
					" where metrics_type = 'baseline'" +
					" and rt_baseline_id = ? ";
				}
				sql2 = "select defect_status_group, num_of_requirements " +
				" from gr_defect_metrics" +
				" where metrics_type = 'baseline'" +
				" and rt_baseline_id = ? " +
				" and data_load_dt = ? ";
			}

			if (filterType.equals("user")){
				// lets get the list of dates for which data points exist
				// and for each date we will get all the status and values.
				if (databaseType.equals("mySQL")){
					sql1 = "select distinct data_load_dt ,date_format(data_load_dt, '%d %M %y') \"formatted_data_load_dt\"" +
						" from gr_defect_metrics " +
						" where metrics_type = 'user'" +
						" and user_id = ? ";
				}
				else {
					sql1 = "select distinct data_load_dt ,to_char(data_load_dt, 'DD MON YYYY') \"formatted_data_load_dt\"" +
					" from gr_defect_metrics " +
					" where metrics_type = 'user'" +
					" and user_id = ? ";
			
				}
				sql2 = "select defect_status_group, num_of_requirements " +
				" from gr_defect_metrics" +
				" where metrics_type = 'user'" +
				" and user_id = ? " +
				" and data_load_dt = ? ";
			}

			if (filterType.equals("folder")){
				// lets get the list of dates for which data points exist
				// and for each date we will get all the status and values.
				if (databaseType.equals("mySQL")){
					sql1 = "select distinct data_load_dt ,date_format(data_load_dt, '%d %M %y') \"formatted_data_load_dt\"" +
						" from gr_defect_metrics " +
						" where metrics_type = 'folder'" +
						" and folder_id = ? ";
				}
				else {
					sql1 = "select distinct data_load_dt ,to_char(data_load_dt, 'DD MON YYYY') \"formatted_data_load_dt\"" +
					" from gr_defect_metrics " +
					" where metrics_type = 'folder'" +
					" and folder_id = ? ";
			
				}
				sql2 = "select defect_status_group, num_of_requirements " +
				" from gr_defect_metrics" +
				" where metrics_type = 'folder'" +
				" and folder_id = ? " +
				" and data_load_dt = ? ";
			}

			// lets get the list of dates for which data points exist
			// and for each date we will get all the status and values.
			prepStmt = con.prepareStatement(sql1);
			prepStmt.setInt(1, filterObjectId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				String dataLoadDt = rs.getString("data_load_dt");
				String formattedDataLoadDt = rs.getString("formatted_data_load_dt");
				
				dataString += " {dataLoadDt: \""+ formattedDataLoadDt + "\", ";
		
				
				PreparedStatement prepStmt2 = con.prepareStatement(sql2);
				prepStmt2.setInt(1, filterObjectId);
				prepStmt2.setString(2, dataLoadDt );
				ResultSet rs2 = prepStmt2.executeQuery();
				while (rs2.next()){
					String defectStatusGroup = rs2.getString("defect_status_group");
					int numOfRequirements = rs2.getInt("num_of_requirements");
					
					dataString +=  "\"" + defectStatusGroup + "\" : " +  numOfRequirements  + " ,";
					
				}
				rs2.close();
				prepStmt2.close();
				// lets drop the last , in the row and add a closing }
				if (dataString.contains(",")){
					dataString = (String) dataString.subSequence(0,dataString.lastIndexOf(","));
				}
				dataString += "},";
			}
			rs.close();
			prepStmt.close();
			// lets drop the last , 
			if (dataString.contains(",")){
				dataString = (String) dataString.subSequence(0,dataString.lastIndexOf(","));
			}		
			dataString += "]";
			
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
		return (dataString);
	}
	
	// returns an array list of defect status groups in the project metrics data set.
	public static ArrayList getDefectStatusGroupsInMetrics (String filterType, int filterObjectId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList defectStatusGroups = new ArrayList();
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql1 = "";
	
			if (filterType.equals("project")){
				// lets get the list of dates for which data points exist
				// and for each date we will get all the status and values.
				sql1 = "select distinct defect_status_group" +
					" from gr_defect_metrics " +
					" where metrics_type = 'project'" +
					" and project_id = ? ";
			}
			
			if (filterType.equals("release")){
				// lets get the list of dates for which data points exist
				// and for each date we will get all the status and values.
				sql1 = "select distinct defect_status_group" +
					" from gr_defect_metrics " +
					" where metrics_type = 'release'" +
					" and release_id = ? ";
			}
	
			if (filterType.equals("baseline")){
				// lets get the list of dates for which data points exist
				// and for each date we will get all the status and values.
				sql1 = "select distinct defect_status_group" +
					" from gr_defect_metrics " +
					" where metrics_type = 'baseline'" +
					" and rt_baseline_id = ? ";
			}
	
			if (filterType.equals("user")){
				// lets get the list of dates for which data points exist
				// and for each date we will get all the status and values.
				sql1 = "select distinct defect_status_group" +
					" from gr_defect_metrics " +
					" where metrics_type = 'user'" +
					" and user_id = ? ";
			}
	
			if (filterType.equals("folder")){
				// lets get the list of dates for which data points exist
				// and for each date we will get all the status and values.
				sql1 = "select distinct defect_status_group" +
					" from gr_defect_metrics " +
					" where metrics_type = 'folder'" +
					" and folder_id = ? ";
			}
	
			
			prepStmt = con.prepareStatement(sql1);
			prepStmt.setInt(1, filterObjectId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				defectStatusGroups.add(rs.getString("defect_status_group"));
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
		return (defectStatusGroups);
	}


	// returns an array list of defect status groups in the project data set.
	public static ArrayList getCurrentDefectStatusGroupsInProject(int projectId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
	ArrayList defectStatusGroups = new ArrayList();
	try {
		javax.naming.InitialContext context = new InitialContext();
		javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
		con = dataSource.getConnection();
		
		String sql1 = "";

		// lets get the list of dates for which data points exist
		// and for each date we will get all the status and values.
		sql1 = "select distinct defect_status_group" +
			" from gr_defect_status_grouping " +
			" where project_id = ? ";
		
		prepStmt = con.prepareStatement(sql1);
		prepStmt.setInt(1, projectId);
		rs = prepStmt.executeQuery();
		while (rs.next()){
			defectStatusGroups.add(rs.getString("defect_status_group"));
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
	return (defectStatusGroups);
}
	
	// returns an array of requirements that match the dataType in that release.

	// if dataType == changedAfter, then cutOffDate has a value in it. 
	// otherwise its null.
	// if release == 0, we crunch for project.
	// if release == -1, we crunch for user.
	public static ArrayList  getRequirementsForReleaseOrProject(SecurityProfile securityProfile, 
		int releaseId, String requirementTypeShortName,
		String dataType, int projectId, String cutOffDate, User user, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList releaseRequirements = new ArrayList();
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			
			
			if (releaseId == 0) {
				// crunch for project.
				if (databaseType.equals("mySQL")){
					sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f" +
					" where " +
					" r.project_id =? " ;
				}
				else {
					sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f" +
					" where " +
					" r.project_id =? " ;
				}

			}
			else  if (releaseId == -1) {
				// crunch for user.
				if (databaseType.equals("mySQL")){
					sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f" +
					" where " +
					" r.project_id =? " + 
					" and r.owner = '"+  user.getEmailId() + "'" ;				
				}
				else {
					sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f" +
					" where " +
					" r.project_id =? " + 
					" and r.owner = '"+  user.getEmailId() + "'" ;				
				}
			}
			
			else {
				// crunch for release.
				if (databaseType.equals("mySQL")){
					sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM gr_release_requirements rr, gr_requirements r , gr_requirement_types rt, gr_folders f" +
					" where " +
					" rr.project_id =? " +
					" and rr.release_id = "  + releaseId + " " + 
					" and rr.requirement_id = r.id" ;
				}
				else {
					sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM gr_release_requirements rr, gr_requirements r , gr_requirement_types rt, gr_folders f" +
					" where " +
					" rr.project_id =? " +
					" and rr.release_id = "  + releaseId + " " + 
					" and rr.requirement_id = r.id" ;
					
				}
			}
			
			if (!(requirementTypeShortName.equals("all"))) {
				sql += " and rt.short_name = '" + requirementTypeShortName + "'"; 
			}
				
				
			sql += 	" and r.requirement_type_id = rt.id " +
				" and r.folder_id = f.id " +
				" and r.deleted = 0 ";
			
			// note: even if a requirement is dangling, it can only re reported as dangling
			// if this requirement type has the 'can be reported as dangling' set to yes
			// this is to catch scenarios where TR req types can not have any traces
			// going in to them, but are not technically dangling requirements.

			if ((dataType != null) && (dataType.equals("dangling"))){
				

				if (databaseType.equals("mySQL")){
					sql += " and (" +
					" (r.trace_from is null or r.trace_from = '') " +
					" and " +
					" (rt.can_be_dangling = 1) " +
					" )";
				}
				else {
					sql += " and (" +
					" (upper(to_char(r.trace_from)) is null or upper(to_char(r.trace_from)) = '') " +
					" and " +
					" (rt.can_be_dangling = 1) " +
					" )";
					
				}
			}
			
			// note: even if a requirement is orphan, it can only re reported as orphan
			// if this requirement type has the 'can be reported as orphan' set to yes
			// this is to catch scenarios where REL req types can not have any traces
			// going out of them, but are not technically orphan requirements.
			if ((dataType != null) && (dataType.equals("orphan"))){				
				
				if (databaseType.equals("mySQL")){
					sql += " and (" +
					" (r.trace_to is null or r.trace_to = '')" +
					" and" +
					" (rt.can_be_orphan = 1)" +
					" ) ";
				}
				else {
					sql += " and (" +
					" (upper(to_char(r.trace_to)) is null or upper(to_char(r.trace_to)) = '')" +
					" and" +
					" (rt.can_be_orphan = 1)" +
					" ) ";
					
				}
			}
			
			if ((dataType != null) && (dataType.equals("completed"))){
				sql += " and r.pct_complete = 100 " ;
			}
			
			if ((dataType != null) && (dataType.equals("incomplete"))){
				sql += " and r.pct_complete <> 100 " ;
			}
			
			
			if ((dataType != null) && (dataType.equals("draft"))){
				sql += " and r.status = 'Draft'" ;
				// By default all requirements are in Draft status, even if the requirement types aren't enable for approval _work flow . This bring up 
				// requirement like Test Cases, that aren't enabled for approval as showing up in Draft status 
				// so , to fix this, we are putting the restriction that if you filter on 'approval status = draft', you mean
				// only those reqs types who have enable approval work flow  =1 
				//
				// we don't have to worry about similar logic for rejected , approved , in approval work flow, as those status 
				// can only be reached in the req type is in enable approval.
				sql += " and rt.enable_approval = 1 "; 
			}
			
			if ((dataType != null) && (dataType.equals("pending"))){
				sql += " and r.status = 'In Approval WorkFlow'";
			}
						
			if ((dataType != null) && (dataType.equals("rejected"))){
				sql += " and r.status = 'Rejected'";
			}
			
			if ((dataType != null) && (dataType.equals("approved"))){
				sql += " and r.status = 'Approved'";
			}
						
			if ((dataType != null) && (dataType.equals("suspectUpstream"))){
				sql += " and r.trace_to like '%(s)%'" ;
			}
			
			if ((dataType != null) && (dataType.equals("suspectDownstream"))){
				sql += " and r.trace_from like '%(s)%'" ;
			}
			
			if ((dataType != null) && (dataType.equals("failedTesting"))){
				sql += " and r.testing_status = 'Fail'" ;
			}
			
			if ((dataType != null) && (dataType.equals("passedTesting"))){
				sql += " and r.testing_status = 'Pass'" ;
			}
			
			if ((dataType != null) && (dataType.equals("pendingTesting"))){
				sql += " and r.testing_status = 'Pending'" ;
			}
			// if dataType == changedAfter, then cutOffDate has a value in it. 
			// otherwise its null.
			if ((dataType != null) && 
				(dataType.equals("changedAfter")) &&
				(cutOffDate != null)){
				
				
				if (databaseType.equals("mySQL")){
					sql += " and  r.last_modified_dt > str_to_date('"+ cutOffDate +"' , '%m/%d/%Y') " ;
				}
				else {
					sql += " and  r.last_modified_dt > to_date('"+ cutOffDate +"' , 'MM DD YYYY') " ;
				}
			}
			
			
			// the following conditions only apply for User Dashboard reports. 
			// ie. (Pending By, Rejected By, and Approved By )
			// these are special catergories. we want all requirements pending by , rejected by, approved by
			// irrespective of who owned the requrirements. So lets build Fresh SQLs for these.
			if ((dataType != null) && (dataType.equals("pendingBy"))){
				if (databaseType.equals("mySQL")){
					sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f" +
					" where " +
					" r.project_id =? " +
					" and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.deleted = 0 " +				
					" and r.approvers like '%(P)"+ user.getEmailId()  +"%'" ;
				}
				else {
					sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f" +
					" where " +
					" r.project_id =? " +
					" and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.deleted = 0 " +				
					" and r.approvers like '%(P)"+ user.getEmailId()  +"%'" ;
				}
			}
			if ((dataType != null) && (dataType.equals("rejectedBy"))){
				if (databaseType.equals("mySQL")){
					sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f" +
					" where " +
					" r.project_id =? " +	
					" and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.deleted = 0 " +				
					" and r.approvers like '%(R)"+ user.getEmailId()  +"%'" ;
				}
				else {
					sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f" +
					" where " +
					" r.project_id =? " +	
					" and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.deleted = 0 " +				
					" and r.approvers like '%(R)"+ user.getEmailId()  +"%'" ;
				}
				
			}
			if ((dataType != null) && (dataType.equals("approvedBy"))){
				if (databaseType.equals("mySQL")){
					sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f" +
					" where " +
					" r.project_id =? " +	
					" and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.deleted = 0 " +				
					" and r.approvers like '%(A)"+ user.getEmailId()  +"%'" ;
				}
				else {
					sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f" +
					" where " +
					" r.project_id =? " +	
					" and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.deleted = 0 " +				
					" and upper(to_char(r.approvers)) like '%(A)"+ user.getEmailId().toUpperCase()  +"%'" ;
				}
			}
			
			
			
			sql += " order by r.requirement_type_id, r.tag " ; 
			

			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();
			
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				int folderId = rs.getInt("folder_id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				projectId = rs.getInt("project_id");
				String parentFullTag = rs.getString("parent_full_tag");
				
				String requirementName = rs.getString("name");
				String requirementDescription = rs.getString("description");
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
				String lastModifiedBy = rs.getString("last_modified_by");
				// lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");

				Requirement requirement = new Requirement(requirementId,
						requirementTypeId, folderId, projectId,
						requirementName, requirementDescription,
						requirementTag, requirementFullTag, version,
						approvedByAllDt, approvers, requirementStatus,
						requirementPriority, requirementOwner, requirementLockedBy,
						requirementPctComplete, requirementExternalUrl,
						traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
						createdBy, lastModifiedBy, requirementTypeName, createdDt);
				// if the user does not have read permissions on this requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}
				
				releaseRequirements.add(requirement);
				
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
		
		return (releaseRequirements);
	}
	

	
	// returns an array of requirements that match the dataType in that release.
	public static ArrayList  getRequirementsForDefectStatusGroup(
		SecurityProfile securityProfile, String defectStatusGroup,
		String filterType, int filterObjectId,
		int projectId, User user, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList releaseRequirements = new ArrayList();
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			
			// This sql can potentially mess up performance as it deals with some of the largest tables in the 
			// system. One easy way to tune this would be to modify it so that the filters start kicking in early
			// before the table joins happen. Keep an eye open for performance.
			
			if (filterType.equals("project")){
				if (databaseType.equals("mySQL")){
					sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f, " +
					" gr_r_attribute_values rav, gr_rt_attributes rta, gr_defect_status_grouping dsg  " +
					" where " +
					" r.deleted = 0 " +
					" and r.project_id =? " +	
					" and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.id = rav.requirement_id  " +
					" and rav.attribute_id = rta.id " +
					" and dsg.defect_status = rav.value " +
					" and dsg.defect_status_group = ? " + 
					" and dsg.project_id = r.project_id " + 
					" and rta.name ='Defect Status' "+
					" and rta.requirement_type_id = rt.id "+
					" and rt.name='Defects' ";			
				}
				else {
					sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f, " +
					" gr_r_attribute_values rav, gr_rt_attributes rta, gr_defect_status_grouping dsg  " +
					" where " +
					" r.deleted = 0 " +
					" and r.project_id =? " +	
					" and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.id = rav.requirement_id  " +
					" and rav.attribute_id = rta.id " +
					" and dsg.defect_status = rav.value " +
					" and dsg.defect_status_group = ? " + 
					" and dsg.project_id = r.project_id " + 
					" and rta.name ='Defect Status' "+
					" and rta.requirement_type_id = rt.id "+
					" and rt.name='Defects' ";			
					
				}
				sql += " order by r.requirement_type_id, r.tag " ;
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.setString(2, defectStatusGroup);
			}
			
			if (filterType.equals("release")){
				if (databaseType.equals("mySQL")){
					sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f, " +
					" gr_r_attribute_values rav, gr_rt_attributes rta, gr_defect_status_grouping dsg ," +
					" gr_release_requirements rr " +
					" where " +
					" r.deleted = 0 " +
					" and r.project_id =? " +	
					" and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.id = rav.requirement_id  " +
					" and rav.attribute_id = rta.id " +
					" and dsg.defect_status = rav.value " +
					" and dsg.defect_status_group = ? " + 
					" and dsg.project_id = r.project_id " + 
					" and rta.name ='Defect Status' "+
					" and rta.requirement_type_id = rt.id "+
					" and rt.name='Defects' " +
					" and r.id = rr.requirement_id" +
					" and rr.release_id = ? ";			
				}
				else {
					sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f, " +
					" gr_r_attribute_values rav, gr_rt_attributes rta, gr_defect_status_grouping dsg ," +
					" gr_release_requirements rr " +
					" where " +
					" r.deleted = 0 " +
					" and r.project_id =? " +	
					" and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.id = rav.requirement_id  " +
					" and rav.attribute_id = rta.id " +
					" and dsg.defect_status = rav.value " +
					" and dsg.defect_status_group = ? " + 
					" and dsg.project_id = r.project_id " + 
					" and rta.name ='Defect Status' "+
					" and rta.requirement_type_id = rt.id "+
					" and rt.name='Defects' " +
					" and r.id = rr.requirement_id" +
					" and rr.release_id = ? ";			
					
				}
				sql += " order by r.requirement_type_id, r.tag " ;
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.setString(2, defectStatusGroup);
				prepStmt.setInt(3, filterObjectId);
			}
			
			if (filterType.equals("baseline")){
				if (databaseType.equals("mySQL")){
					sql = " SELECT distinct r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f, " +
					" gr_r_attribute_values rav, gr_rt_attributes rta, gr_defect_status_grouping dsg ," +
					" gr_requirement_baselines rb " +
					" where " +
					" r.deleted = 0 " +
					" and r.project_id =? " +	
					" and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.id = rav.requirement_id  " +
					" and rav.attribute_id = rta.id " +
					" and dsg.defect_status = rav.value " +
					" and dsg.defect_status_group = ? " + 
					" and dsg.project_id = r.project_id " + 
					" and rta.name ='Defect Status' "+
					" and rta.requirement_type_id = rt.id "+
					" and rt.name='Defects' " +
					" and r.id = rb.requirement_id" +
					" and rb.rt_baseline_id = ? ";			
				}
				else {
					sql = " SELECT distinct r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f, " +
					" gr_r_attribute_values rav, gr_rt_attributes rta, gr_defect_status_grouping dsg ," +
					" gr_requirement_baselines rb " +
					" where " +
					" r.deleted = 0 " +
					" and r.project_id =? " +	
					" and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.id = rav.requirement_id  " +
					" and rav.attribute_id = rta.id " +
					" and dsg.defect_status = rav.value " +
					" and dsg.defect_status_group = ? " + 
					" and dsg.project_id = r.project_id " + 
					" and rta.name ='Defect Status' "+
					" and rta.requirement_type_id = rt.id "+
					" and rt.name='Defects' " +
					" and r.id = rb.requirement_id" +
					" and rb.rt_baseline_id = ? ";			
					
				}
				sql += " order by r.requirement_type_id, r.tag " ;
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.setString(2, defectStatusGroup);
				prepStmt.setInt(3, filterObjectId);
			}
			
			if (filterType.equals("user")){
				if (databaseType.equals("mySQL")){
					sql = " SELECT distinct r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f, " +
					" gr_r_attribute_values rav, gr_rt_attributes rta, gr_defect_status_grouping dsg " +
					" where " +
					" r.deleted = 0 " +
					" and r.project_id =? " +	
					" and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.id = rav.requirement_id  " +
					" and rav.attribute_id = rta.id " +
					" and dsg.defect_status = rav.value " +
					" and dsg.defect_status_group = ? " + 
					" and dsg.project_id = r.project_id " + 
					" and rta.name ='Defect Status' "+
					" and rta.requirement_type_id = rt.id "+
					" and rt.name='Defects' " +
					" and r.owner = ? ";
				}
				else {
					sql = " SELECT distinct r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f, " +
					" gr_r_attribute_values rav, gr_rt_attributes rta, gr_defect_status_grouping dsg " +
					" where " +
					" r.deleted = 0 " +
					" and r.project_id =? " +	
					" and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " +
					" and r.id = rav.requirement_id  " +
					" and rav.attribute_id = rta.id " +
					" and dsg.defect_status = rav.value " +
					" and dsg.defect_status_group = ? " + 
					" and dsg.project_id = r.project_id " + 
					" and rta.name ='Defect Status' "+
					" and rta.requirement_type_id = rt.id "+
					" and rt.name='Defects' " +
					" and r.owner = ? ";
				}
				sql += " order by r.requirement_type_id, r.tag " ;
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.setString(2, defectStatusGroup);
				prepStmt.setString(3, user.getEmailId());
			}
			
			rs = prepStmt.executeQuery();
			
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				int folderId = rs.getInt("folder_id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				projectId = rs.getInt("project_id");
				String parentFullTag = rs.getString("parent_full_tag");
				
				String requirementName = rs.getString("name");
				String requirementDescription = rs.getString("description");
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
				String lastModifiedBy = rs.getString("last_modified_by");
				// lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");

				Requirement requirement = new Requirement(requirementId,
						requirementTypeId, folderId, projectId,
						requirementName, requirementDescription,
						requirementTag, requirementFullTag, version,
						approvedByAllDt, approvers, requirementStatus,
						requirementPriority, requirementOwner, requirementLockedBy,
						requirementPctComplete, requirementExternalUrl,
						traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
						createdBy, lastModifiedBy, requirementTypeName, createdDt);
				// if the user does not have read permissions on this requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}
				releaseRequirements.add(requirement);
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
		
		return (releaseRequirements);
	}

	
	// returns an array of requirements that match the dataType in that release.
	// NOTE : This is very similar to getRequirementsForReleaseOrProject. just that
	// its only for baselines. 

	// if dataType == changedAfter, then cutOffDate has a value in it. 
	// otherwise its null.

	public static ArrayList  getRequirementsForSprint(SecurityProfile securityProfile, 
		int sprintId, String requirementTypeShortName,
		String dataType, int projectId, String cutOffDate, User user, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList baselineRequirements = new ArrayList();
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			
			Sprint sprint = new Sprint(sprintId, databaseType);
			String sprintClause = "Agile Sprint:#:" + sprint.getSprintName();
			
			// crunch for baseline
			if (databaseType.equals("mySQL")){
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, " +
				" r.name, r.description, r.tag, r.full_tag, " +
				" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
				" r.approvers ," + 
				" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
				" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.deleted," +
				" f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
				" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
				" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f " +
				" where " +
				" r.project_id =? " +
				" and r.requirement_type_id = rt.id " +
				" and r.folder_id = f.id " +
				" and r.deleted = 0 " + 
				" and r.user_defined_attributes like '%"+ sprintClause +"%'";
			}
			else {
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, " +
				" r.name, r.description, r.tag, r.full_tag, " +
				" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
				" r.approvers ," + 
				" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
				" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.deleted," +
				" f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
				" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
				" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f " +
				" where " +
				" r.project_id =? " +
				" and r.requirement_type_id = rt.id " +
				" and r.folder_id = f.id " +
				" and r.deleted = 0 " +
				" and r.user_defined_attributes like '%"+ sprintClause +"%'";
				
			}
			if (!(requirementTypeShortName.equals("all"))) {
				sql += " and rt.short_name = '" + requirementTypeShortName + "'"; 
			}
				
			
			
			// note: even if a requirement is dangling, it can only re reported as dangling
			// if this requirement type has the 'can be reported as dangling' set to yes
			// this is to catch scenarios where TR req types can not have any traces
			// going in to them, but are not technically dangling requirements.

			if ((dataType != null) && (dataType.equals("dangling"))){
				

				if (databaseType.equals("mySQL")){
					sql += " and (" +
					" (r.trace_from is null or r.trace_from = '') " +
					" and " +
					" (rt.can_be_dangling = 1) " +
					" )";
				}
				else {
					sql += " and (" +
					" (upper(to_char(r.trace_from)) is null or upper(to_char(r.trace_from)) = '') " +
					" and " +
					" (rt.can_be_dangling = 1) " +
					" )";
					
				}
			}
			
			// note: even if a requirement is orphan, it can only re reported as orphan
			// if this requirement type has the 'can be reported as orphan' set to yes
			// this is to catch scenarios where REL req types can not have any traces
			// going out of them, but are not technically orphan requirements.
			if ((dataType != null) && (dataType.equals("orphan"))){				
				

				if (databaseType.equals("mySQL")){
					sql += " and (" +
					" (r.trace_to is null or r.trace_to = '')" +
					" and" +
					" (rt.can_be_orphan = 1)" +
					" ) ";
				}
				else {
					sql += " and (" +
					" (upper(to_char(r.trace_to)) is null or upper(to_char(r.trace_to)) = '')" +
					" and" +
					" (rt.can_be_orphan = 1)" +
					" ) ";
					
				}
			}

			if ((dataType != null) && (dataType.equals("completed"))){
				sql += " and r.pct_complete = 100 " ;
			}
			
			if ((dataType != null) && (dataType.equals("incomplete"))){
				sql += " and r.pct_complete <> 100 " ;
			}
			
			
			if ((dataType != null) && (dataType.equals("draft"))){
				sql += " and r.status = 'Draft'";
			}
			
			if ((dataType != null) && (dataType.equals("pending"))){
				sql += " and r.status = 'In Approval WorkFlow'";
			}
						
			if ((dataType != null) && (dataType.equals("rejected"))){
				sql += " and r.status = 'Rejected'";
			}
			
			if ((dataType != null) && (dataType.equals("approved"))){
				sql += " and r.status = 'Approved'";
			}
						
			if ((dataType != null) && (dataType.equals("suspectUpstream"))){
				
				if (databaseType.equals("mySQL")){
					sql += " and r.trace_to like '%(s)%'" ;
				}
				else {
					sql += " and upper(to_char(r.trace_to)) like '%(S)%'" ;
					
				}
			}
			
			if ((dataType != null) && (dataType.equals("suspectDownstream"))){
				
				if (databaseType.equals("mySQL")){
					sql += " and r.trace_from like '%(s)%'" ;
				}
				else {
					sql += " and upper(to_char(r.trace_from)) like '%(S)%'" ;
					
				}
			}
			
			if ((dataType != null) && (dataType.equals("failedTesting"))){
				sql += " and r.testing_status = 'Fail'" ;
			}
			
			if ((dataType != null) && (dataType.equals("passedTesting"))){
				sql += " and r.testing_status = 'Pass'" ;
			}
			
			if ((dataType != null) && (dataType.equals("pendingTesting"))){
				sql += " and r.testing_status = 'Pending'" ;
			}
			
			// if dataType == changedAfter, then cutOffDate has a value in it. 
			// otherwise its null.
			if ((dataType != null) && 
				(dataType.equals("changedAfter")) &&
				(cutOffDate != null)){
				
				
				if (databaseType.equals("mySQL")){
					sql += " and  r.last_modified_dt > str_to_date('"+ cutOffDate +"' , '%m/%d/%Y') " ;
				}
				else {
					sql += " and  r.last_modified_dt > to_date('"+ cutOffDate +"' , 'DD MON YYYY') " ;
				}
			}
			
			
			// the following conditions only apply for User Dashboard reports. 
			// ie. (Pending By, Rejected By, and Approved By )
			if ((dataType != null) && (dataType.equals("pendingBy"))){
				sql += " and r.approvers like '%(P)"+ user.getEmailId()  +"%'" ;
			}
			if ((dataType != null) && (dataType.equals("rejectedBy"))){
				sql += " and r.approvers like '%(R)"+ user.getEmailId()  +"%'" ;
			}
			if ((dataType != null) && (dataType.equals("approvedBy"))){
				sql += " and r.approvers like '%(A)"+ user.getEmailId()  +"%'" ;
			}
			
			
			
			sql += " order by r.requirement_type_id, r.tag " ; 
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			
			rs = prepStmt.executeQuery();
			
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				int folderId = rs.getInt("folder_id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				projectId = rs.getInt("project_id");
				String requirementName = rs.getString("name");
				String requirementDescription = rs.getString("description");
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
				String lastModifiedBy = rs.getString("last_modified_by");
				// lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");

				Requirement requirement = new Requirement(requirementId,
						requirementTypeId, folderId, projectId,
						requirementName, requirementDescription,
						requirementTag, requirementFullTag, version,
						approvedByAllDt, approvers, requirementStatus,
						requirementPriority, requirementOwner, requirementLockedBy,
						requirementPctComplete, requirementExternalUrl,
						traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
						createdBy, lastModifiedBy, requirementTypeName, createdDt);
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}
				
				baselineRequirements.add(requirement);
				
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
		
		return (baselineRequirements);
	}
		
	
	// returns an array of requirements that match the dataType in that release.
	// NOTE : This is very similar to getRequirementsForReleaseOrProject. just that
	// its only for baselines. 

	// if dataType == changedAfter, then cutOffDate has a value in it. 
	// otherwise its null.

	public static ArrayList  getRequirementsForBaseline(SecurityProfile securityProfile, 
		int baselineId, String requirementTypeShortName,
		String dataType, int projectId, String cutOffDate, User user, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList baselineRequirements = new ArrayList();
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			
			
			
			// crunch for baseline
			if (databaseType.equals("mySQL")){
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, " +
				" r.name, r.description, r.tag, r.full_tag, " +
				" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
				" r.approvers ," + 
				" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
				" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.deleted," +
				" f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
				" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
				" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f, " +
				"	gr_requirement_baselines rb " +
				" where " +
				" r.project_id =? " +
				" and r.id  =  rb.requirement_id " + 
				" and rb.rt_baseline_id = ? " +
				" and r.requirement_type_id = rt.id " +
				" and r.folder_id = f.id " +
				" and r.deleted = 0 ";
			}
			else {
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, " +
				" r.name, r.description, r.tag, r.full_tag, " +
				" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
				" r.approvers ," + 
				" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
				" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.deleted," +
				" f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
				" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
				" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f, " +
				"	gr_requirement_baselines rb " +
				" where " +
				" r.project_id =? " +
				" and r.id  =  rb.requirement_id " + 
				" and rb.rt_baseline_id = ? " +
				" and r.requirement_type_id = rt.id " +
				" and r.folder_id = f.id " +
				" and r.deleted = 0 ";
				
			}
			if (!(requirementTypeShortName.equals("all"))) {
				sql += " and rt.short_name = '" + requirementTypeShortName + "'"; 
			}
				
			
			
			// note: even if a requirement is dangling, it can only re reported as dangling
			// if this requirement type has the 'can be reported as dangling' set to yes
			// this is to catch scenarios where TR req types can not have any traces
			// going in to them, but are not technically dangling requirements.

			if ((dataType != null) && (dataType.equals("dangling"))){
				

				if (databaseType.equals("mySQL")){
					sql += " and (" +
					" (r.trace_from is null or r.trace_from = '') " +
					" and " +
					" (rt.can_be_dangling = 1) " +
					" )";
				}
				else {
					sql += " and (" +
					" (upper(to_char(r.trace_from)) is null or upper(to_char(r.trace_from)) = '') " +
					" and " +
					" (rt.can_be_dangling = 1) " +
					" )";
					
				}
			}
			
			// note: even if a requirement is orphan, it can only re reported as orphan
			// if this requirement type has the 'can be reported as orphan' set to yes
			// this is to catch scenarios where REL req types can not have any traces
			// going out of them, but are not technically orphan requirements.
			if ((dataType != null) && (dataType.equals("orphan"))){				
				

				if (databaseType.equals("mySQL")){
					sql += " and (" +
					" (r.trace_to is null or r.trace_to = '')" +
					" and" +
					" (rt.can_be_orphan = 1)" +
					" ) ";
				}
				else {
					sql += " and (" +
					" (upper(to_char(r.trace_to)) is null or upper(to_char(r.trace_to)) = '')" +
					" and" +
					" (rt.can_be_orphan = 1)" +
					" ) ";
					
				}
			}

			if ((dataType != null) && (dataType.equals("completed"))){
				sql += " and r.pct_complete = 100 " ;
			}
			
			if ((dataType != null) && (dataType.equals("incomplete"))){
				sql += " and r.pct_complete <> 100 " ;
			}
			
			
			if ((dataType != null) && (dataType.equals("draft"))){
				sql += " and r.status = 'Draft'";
			}
			
			if ((dataType != null) && (dataType.equals("pending"))){
				sql += " and r.status = 'In Approval WorkFlow'";
			}
						
			if ((dataType != null) && (dataType.equals("rejected"))){
				sql += " and r.status = 'Rejected'";
			}
			
			if ((dataType != null) && (dataType.equals("approved"))){
				sql += " and r.status = 'Approved'";
			}
						
			if ((dataType != null) && (dataType.equals("suspectUpstream"))){
				
				if (databaseType.equals("mySQL")){
					sql += " and r.trace_to like '%(s)%'" ;
				}
				else {
					sql += " and upper(to_char(r.trace_to)) like '%(S)%'" ;
					
				}
			}
			
			if ((dataType != null) && (dataType.equals("suspectDownstream"))){
				
				if (databaseType.equals("mySQL")){
					sql += " and r.trace_from like '%(s)%'" ;
				}
				else {
					sql += " and upper(to_char(r.trace_from)) like '%(S)%'" ;
					
				}
			}
			
			if ((dataType != null) && (dataType.equals("failedTesting"))){
				sql += " and r.testing_status = 'Fail'" ;
			}
			
			if ((dataType != null) && (dataType.equals("passedTesting"))){
				sql += " and r.testing_status = 'Pass'" ;
			}
			
			if ((dataType != null) && (dataType.equals("pendingTesting"))){
				sql += " and r.testing_status = 'Pending'" ;
			}
			
			// if dataType == changedAfter, then cutOffDate has a value in it. 
			// otherwise its null.
			if ((dataType != null) && 
				(dataType.equals("changedAfter")) &&
				(cutOffDate != null)){
				
				
				if (databaseType.equals("mySQL")){
					sql += " and  r.last_modified_dt > str_to_date('"+ cutOffDate +"' , '%m/%d/%Y') " ;
				}
				else {
					sql += " and  r.last_modified_dt > to_date('"+ cutOffDate +"' , 'DD MON YYYY') " ;
				}
			}
			
			
			// the following conditions only apply for User Dashboard reports. 
			// ie. (Pending By, Rejected By, and Approved By )
			if ((dataType != null) && (dataType.equals("pendingBy"))){
				sql += " and r.approvers like '%(P)"+ user.getEmailId()  +"%'" ;
			}
			if ((dataType != null) && (dataType.equals("rejectedBy"))){
				sql += " and r.approvers like '%(R)"+ user.getEmailId()  +"%'" ;
			}
			if ((dataType != null) && (dataType.equals("approvedBy"))){
				sql += " and r.approvers like '%(A)"+ user.getEmailId()  +"%'" ;
			}
			
			
			
			sql += " order by r.requirement_type_id, r.tag " ; 
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, baselineId);
			rs = prepStmt.executeQuery();
			
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				int folderId = rs.getInt("folder_id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				projectId = rs.getInt("project_id");
				String requirementName = rs.getString("name");
				String requirementDescription = rs.getString("description");
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
				String lastModifiedBy = rs.getString("last_modified_by");
				// lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");

				Requirement requirement = new Requirement(requirementId,
						requirementTypeId, folderId, projectId,
						requirementName, requirementDescription,
						requirementTag, requirementFullTag, version,
						approvedByAllDt, approvers, requirementStatus,
						requirementPriority, requirementOwner, requirementLockedBy,
						requirementPctComplete, requirementExternalUrl,
						traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
						createdBy, lastModifiedBy, requirementTypeName, createdDt);
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}
				
				baselineRequirements.add(requirement);
				
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
		
		return (baselineRequirements);
	}
	
	

	// returns an array of requirements that match the dataType in that release.
	// NOTE : This is very similar to getRequirementsForReleaseOrProject. just that
	// its only for baselines. 

	// if dataType == changedAfter, then cutOffDate has a value in it. 
	// otherwise its null.

	public static ArrayList  getRequirementsForFolder(SecurityProfile securityProfile, 
		int folderId, String requirementTypeShortName,
		String dataType, int projectId, String cutOffDate, User user, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList folderRequirements = new ArrayList();
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			
			
			
			// crunch for folder
			if (databaseType.equals("mySQL")){
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, " +
				" r.name, r.description, r.tag, r.full_tag, " +
				" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
				" r.approvers ," + 
				" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
				" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.deleted," +
				" f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
				" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" ," +
				" r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 " +
				" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f " +
				" where " +
				" r.project_id =? " + 
				" and r.folder_id = ? " +
				" and r.requirement_type_id = rt.id " +
				" and r.folder_id = f.id " +
				" and r.deleted = 0 ";
			}
			else {
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, " +
				" r.name, r.description, r.tag, r.full_tag, " +
				" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
				" r.approvers ," + 
				" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
				" r.trace_to, r.trace_from, r.user_defined_attributes, r.testing_status, r.deleted," +
				" f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
				" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\", " +
				" r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4  " +
				" FROM  gr_requirements r , gr_requirement_types rt, gr_folders f " +
				" where " +
				" r.project_id =? " + 
				" and r.folder_id = ? " +
				" and r.requirement_type_id = rt.id " +
				" and r.folder_id = f.id " +
				" and r.deleted = 0 ";
				
			}
			if (!(requirementTypeShortName.equals("all"))) {
				sql += " and rt.short_name = '" + requirementTypeShortName + "'"; 
			}
				
			
			
			
			// note: even if a requirement is dangling, it can only re reported as dangling
			// if this requirement type has the 'can be reported as dangling' set to yes
			// this is to catch scenarios where TR req types can not have any traces
			// going in to them, but are not technically dangling requirements.

			if ((dataType != null) && (dataType.equals("dangling"))){
				sql += " and (" +
				" (r.trace_from is null or r.trace_from = '') " +
				" and " +
				" (rt.can_be_dangling = 1) " +
				" )";

			}
			
			// note: even if a requirement is orphan, it can only re reported as orphan
			// if this requirement type has the 'can be reported as orphan' set to yes
			// this is to catch scenarios where REL req types can not have any traces
			// going out of them, but are not technically orphan requirements.
			if ((dataType != null) && (dataType.equals("orphan"))){				
				sql += " and (" +
				" (r.trace_to is null or r.trace_to = '')" +
				" and" +
				" (rt.can_be_orphan = 1)" +
				" ) ";

			}

			if ((dataType != null) && (dataType.equals("completed"))){
				sql += " and r.pct_complete = 100 " ;
			}
			
			if ((dataType != null) && (dataType.equals("incomplete"))){
				sql += " and r.pct_complete <> 100 " ;
			}
			
			
			if ((dataType != null) && (dataType.equals("draft"))){
				sql += " and r.status = 'Draft'";
			}
			
			if ((dataType != null) && (dataType.equals("pending"))){
				sql += " and r.status = 'In Approval WorkFlow'";
			}
						
			if ((dataType != null) && (dataType.equals("rejected"))){
				sql += " and r.status = 'Rejected'";
			}
			
			if ((dataType != null) && (dataType.equals("approved"))){
				sql += " and r.status = 'Approved'";
			}
						
			if ((dataType != null) && (dataType.equals("suspectUpstream"))){
				
				if (databaseType.equals("mySQL")){
					sql += " and r.trace_to like '%(s)%'" ;
				}
				else {
					sql += " and upper(to_char(r.trace_to)) like '%(S)%'" ;
					
				}
			}
			
			if ((dataType != null) && (dataType.equals("suspectDownstream"))){
				
				if (databaseType.equals("mySQL")){
					sql += " and r.trace_from like '%(s)%'" ;
				}
				else {
					sql += " and upper(to_char(r.trace_from)) like '%(S)%'" ;
					
				}
			}

			if ((dataType != null) && (dataType.equals("failedTesting"))){
				sql += " and r.testing_status = 'Fail'" ;
			}
			
			if ((dataType != null) && (dataType.equals("passedTesting"))){
				sql += " and r.testing_status = 'Pass'" ;
			}
			
			if ((dataType != null) && (dataType.equals("pendingTesting"))){
				sql += " and r.testing_status = 'Pending'" ;
			}

			// if dataType == changedAfter, then cutOffDate has a value in it. 
			// otherwise its null.
			if ((dataType != null) && 
				(dataType.equals("changedAfter")) &&
				(cutOffDate != null)){
				
				
				if (databaseType.equals("mySQL")){
					sql += " and  r.last_modified_dt > str_to_date('"+ cutOffDate +"' , '%m/%d/%Y') " ;
				}
				else {
					sql += " and  r.last_modified_dt > to_date('"+ cutOffDate +"' , 'DD MON YYYY') " ;
				}
			}
			
			
			// the following conditions only apply for User Dashboard reports. 
			// ie. (Pending By, Rejected By, and Approved By )
			if ((dataType != null) && (dataType.equals("pendingBy"))){
				sql += " and r.approvers like '%(P)"+ user.getEmailId()  +"%'" ;
			}
			if ((dataType != null) && (dataType.equals("rejectedBy"))){
				sql += " and r.approvers like '%(R)"+ user.getEmailId()  +"%'" ;
			}
			if ((dataType != null) && (dataType.equals("approvedBy"))){
				sql += " and r.approvers like '%(A)"+ user.getEmailId()  +"%'" ;
			}
			
			
			
			sql += " order by r.requirement_type_id, r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag " ;
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
			rs = prepStmt.executeQuery();
			
			
			while (rs.next()){
				int requirementId = rs.getInt("id");
				//int folderId = rs.getInt("folder_id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				projectId = rs.getInt("project_id");
				String requirementName = rs.getString("name");
				String requirementDescription = rs.getString("description");
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
				String lastModifiedBy = rs.getString("last_modified_by");
				// lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");

				Requirement requirement = new Requirement(requirementId,
						requirementTypeId, folderId, projectId,
						requirementName, requirementDescription,
						requirementTag, requirementFullTag, version,
						approvedByAllDt, approvers, requirementStatus,
						requirementPriority, requirementOwner, requirementLockedBy,
						requirementPctComplete, requirementExternalUrl,
						traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
						createdBy, lastModifiedBy, requirementTypeName, createdDt);
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}
				folderRequirements.add(requirement);
				
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
		
		return (folderRequirements);
	}
	
	

	
	// returns a YUI chartables datastring for allRequirements in different Req Types in a baseline
	// used in trending release metrics
	// NOTE : This is very similar to getRequirementsForReleaseOrProject. just that
	// its only for baselines. 

	public static String  getTrendDataStringForBaseline(int rTBaselineId, int projectId, String dataType, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		String dataString = "";
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "";
			
			
			// first lets get the total number of requiremetns on any given dataloaddt and store it in a hash map.
		
			// this is s baseline level search.
			if (databaseType.equals("mySQL")){
				sql = "select  date_format(data_load_dt, '%d %M %y') \"data_load_dt\", sum(num_of_requirements) \"total_reqs\" " +
					 " from gr_baseline_metrics bm " +
					 " where bm.project_id = ? " +
					 " and bm.rt_baseline_id = ? " +
					 " group by data_load_dt ";
			}
			else {
				sql = "select  to_char(data_load_dt, 'DD MON YYYY') \"data_load_dt\", sum(num_of_requirements) \"total_reqs\" " +
				 " from gr_baseline_metrics bm " +
				 " where bm.project_id = ? " +
				 " and bm.rt_baseline_id = ? " +
				 " group by data_load_dt ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, rTBaselineId);
		
			rs = prepStmt.executeQuery();
			HashMap totalReqsMap = new HashMap(); 
			while(rs.next()) {
				totalReqsMap.put( rs.getString("data_load_dt"), new Integer(rs.getInt("total_reqs")) );
			}
			prepStmt.close();
			rs.close();

			/* NOTE :
			 * We could have done this a lot more easily by quering for distinct dates and for each date
			 * running a seperate sql. howver that would have been very db intensive. so coding this convoluted
			 * logic in to Java. Feel free to change if you can. (without putting bugs in it of course).
			*/
			// This sql gets the total number of requirements in a folder.
			if (databaseType.equals("mySQL")){
				sql = "select data_load_dt, date_format(data_load_dt, '%d %M %y') \"formatted_data_load_dt\"," +
					" requirement_type_short_name, " +
					" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs," +
					" num_of_rejected_reqs,  num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs," +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs, " +
					" num_of_test_pending_reqs,  " +
					" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs " +				
					" from gr_baseline_metrics bm " +
					" where bm.project_id =? " +
					" and bm.rt_baseline_id = ? " +
					" order by data_load_dt asc, requirement_type_short_name";
			}
			else {
				sql = "select data_load_dt, to_char(data_load_dt, 'DD MON YYYY') \"formatted_data_load_dt\"," +
				" requirement_type_short_name, " +
				" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs," +
				" num_of_rejected_reqs,  num_of_approved_reqs, " +
				" num_of_dangling_reqs, num_of_orphan_reqs," +
				" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
				" num_of_completed_reqs, num_of_incomplete_reqs, " +
				" num_of_test_pending_reqs,  " +
				" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs " +				
				" from gr_baseline_metrics bm " +
				" where bm.project_id =? " +
				" and bm.rt_baseline_id = ? " +
				" order by data_load_dt asc, requirement_type_short_name";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, rTBaselineId);
		
			
			rs = prepStmt.executeQuery();

			dataString = "[";
			String dataRow = "";
			String dataLoadDt = "";
			String oldDataLoadDt = "";
			while (rs.next()){
				dataLoadDt = rs.getString("formatted_data_load_dt");
				String requirementTypeShortName = rs.getString("requirement_type_short_name");
				int numOfRequirements = rs.getInt("num_of_requirements");
				int numOfDraftRequirements = rs.getInt("num_of_draft_reqs");
				int numOfInApprovalWorkflowRequirements = rs.getInt("num_of_in_workflow_reqs");
				int numOfRejectedRequirements = rs.getInt("num_of_rejected_reqs");
				int numOfApprovedRequirements = rs.getInt("num_of_approved_reqs");
				int numOfDanglingRequirements = rs.getInt("num_of_dangling_reqs");
				int numOfOrphanRequirements = rs.getInt("num_of_orphan_reqs");
				int numOfSuspectUpstreamRequirements = rs.getInt("num_of_suspect_upstream_reqs");
				int numOfSuspectDownstreamRequirements = rs.getInt("num_of_suspect_downstream_reqs");
				int numOfCompletedRequirements = rs.getInt("num_of_completed_reqs");
				int numOfIncompleteRequirements = rs.getInt("num_of_incomplete_reqs");
				int numOfTestPendingRequirements = rs.getInt("num_of_test_pending_reqs");
				int numOfTestPassRequirements = rs.getInt("num_of_test_pass_reqs");
				int numOfTestFailRequirements = rs.getInt("num_of_test_fail_reqs");


				
				int dataNumber = 0;
				if (dataType.equals("numOfRequirements")) {
					dataNumber = numOfRequirements;
				}
				if (dataType.equals("numOfDraftRequirements")) {
					dataNumber = numOfDraftRequirements;
				}
				if (dataType.equals("numOfInApprovalWorkflowRequirements")) {
					dataNumber = numOfInApprovalWorkflowRequirements;
				}
				if (dataType.equals("numOfRejectedRequirements")) {
					dataNumber = numOfRejectedRequirements;
				}
				if (dataType.equals("numOfApprovedRequirements")) {
					dataNumber = numOfApprovedRequirements;
				}
				if (dataType.equals("numOfDanglingRequirements")) {
					dataNumber = numOfDanglingRequirements;
				}
				if (dataType.equals("numOfOrphanRequirements")) {
					dataNumber = numOfOrphanRequirements;
				}
				if (dataType.equals("numOfSuspectUpstreamRequirements")) {
					dataNumber = numOfSuspectUpstreamRequirements;
				}
				if (dataType.equals("numOfSuspectDownstreamRequirements")) {
					dataNumber = numOfSuspectDownstreamRequirements;
				}
				if (dataType.equals("numOfCompletedRequirements")) {
					dataNumber = numOfCompletedRequirements;
				}
				if (dataType.equals("numOfIncompleteRequirements")) {
					dataNumber = numOfIncompleteRequirements;
				}
				if (dataType.equals("numOfTestPendingRequirements")) {
					dataNumber = numOfTestPendingRequirements;
				}		
				if (dataType.equals("numOfTestPassRequirements")) {
					dataNumber = numOfTestPassRequirements;
				}		
				if (dataType.equals("numOfTestFailRequirements")) {
					dataNumber = numOfTestFailRequirements;
				}		
				
				if (dataLoadDt.equals(oldDataLoadDt)) {
					// still running the old sequence. add it to current dataRow
					dataRow +=  requirementTypeShortName + ":" +  dataNumber +  "," ;
				}
				else {
					// a new data load dt has started. 
					// lets process and reset the dataRow.
					// drop the last , in row
					if (!dataRow.equals("")) {
						if (dataRow.contains(",")){
							dataRow = (String) dataRow.subSequence(0,dataRow.lastIndexOf(","));
						}			
						// lets get the total number of reqs on the oldDataLoadDt.
						int totalReqs = 0;
						Integer totalReqsInteger = (Integer) totalReqsMap.get(oldDataLoadDt);
						if (totalReqsInteger != null) {
							totalReqs = totalReqsInteger.intValue();
						}
						dataString += "\n{" +
					 		"dataLoadDt:\"" +  oldDataLoadDt +  "\", totalReqs:" + totalReqs + ", " + dataRow + "},";
					
					}
					//so lets reset the old Data Load to new value and add the current req type short name and num
					// of requirements to the new dataRow.
					oldDataLoadDt = dataLoadDt;
					dataRow =  requirementTypeShortName + ":" +  dataNumber +  "," ;
				}
				
			}

			// the above logic leaves out the last data row. so lets add it to the allRequirements Data String.
			if (!dataRow.equals("")) {
				if (dataRow.contains(",")){
					dataRow = (String) dataRow.subSequence(0,dataRow.lastIndexOf(","));
				}		
				// lets get the total number of reqs on the dataLoadDt.
				int totalReqs = 0;
				Integer totalReqsInteger = (Integer) totalReqsMap.get(dataLoadDt);
				if (totalReqsInteger != null) {
					totalReqs = totalReqsInteger.intValue();
				}
				dataString += "\n{" +
					"dataLoadDt:\"" +  dataLoadDt + "\", totalReqs:" + totalReqs + ", " + dataRow + "},";

			}
			
			if (dataString.contains(",")) {
				dataString = (String) dataString.subSequence(0,dataString.lastIndexOf(","));
			}
			// lets add the closign ]
			dataString += "]  ";
			
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
		
		return (dataString);
	}
	
	
	
	// returns a YUI chartables datastring for allRequirements in different Req Types in a baseline
	// used in trending release metrics
	// NOTE : This is very similar to getRequirementsForReleaseOrProject. just that
	// its only for folder. 

	public static String  getTrendDataStringForFolder(int folderId, int projectId, String dataType, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		String dataString = "";
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "";
			
			
			// first lets get the total number of requiremetns on any given dataloaddt and store it in a hash map.
		
			// this is s folder level search.
			if (databaseType.equals("mySQL")){
				sql = "select  date_format(data_load_dt, '%d %M %y') \"data_load_dt\", sum(num_of_requirements) \"total_reqs\" " +
					 " from gr_folder_metrics fm " +
					 " where fm.project_id = ? " +
					 " and fm.folder_id = ? " +
					 " group by data_load_dt ";
			}
			else {
				sql = "select  to_char(data_load_dt, 'DD MON YYYY') \"data_load_dt\", sum(num_of_requirements) \"total_reqs\" " +
				 " from gr_folder_metrics fm " +
				 " where fm.project_id = ? " +
				 " and fm.folder_id = ? " +
				 " group by data_load_dt ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
		
			rs = prepStmt.executeQuery();
			HashMap totalReqsMap = new HashMap(); 
			while(rs.next()) {
				totalReqsMap.put( rs.getString("data_load_dt"), new Integer(rs.getInt("total_reqs")) );
			}
			prepStmt.close();
			rs.close();

			/* NOTE :
			 * We could have done this a lot more easily by quering for distinct dates and for each date
			 * running a seperate sql. howver that would have been very db intensive. so coding this convoluted
			 * logic in to Java. Feel free to change if you can. (without putting bugs in it of course).
			*/
			// This sql gets the total number of requirements in a folder.
			if (databaseType.equals("mySQL")){
				sql = "select data_load_dt, date_format(data_load_dt, '%d %M %y') \"formatted_data_load_dt\"," +
					" requirement_type_short_name, " +
					" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs," +
					" num_of_rejected_reqs,  num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs," +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs, " +
					" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs " +
					" from gr_folder_metrics fm " +
					" where fm.project_id =? " +
					" and fm.folder_id = ? " +
					" order by data_load_dt asc, requirement_type_short_name";
			}
			else {
				sql = "select data_load_dt, to_char(data_load_dt, 'DD MON YYYY') \"formatted_data_load_dt\"," +
				" requirement_type_short_name, " +
				" num_of_requirements, num_of_draft_reqs, num_of_in_workflow_reqs," +
				" num_of_rejected_reqs,  num_of_approved_reqs, " +
				" num_of_dangling_reqs, num_of_orphan_reqs," +
				" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
				" num_of_completed_reqs, num_of_incomplete_reqs, " +
				" num_of_test_pending_reqs , num_of_test_pass_reqs, num_of_test_fail_reqs " +
				" from gr_folder_metrics fm " +
				" where fm.project_id =? " +
				" and fm.folder_id = ? " +
				" order by data_load_dt asc, requirement_type_short_name";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
		
			
			rs = prepStmt.executeQuery();

			dataString = "[";
			String dataRow = "";
			String dataLoadDt = "";
			String oldDataLoadDt = "";
			while (rs.next()){
				dataLoadDt = rs.getString("formatted_data_load_dt");
				String requirementTypeShortName = rs.getString("requirement_type_short_name");
				int numOfRequirements = rs.getInt("num_of_requirements");
				int numOfDraftRequirements = rs.getInt("num_of_draft_reqs");
				int numOfInApprovalWorkflowRequirements = rs.getInt("num_of_in_workflow_reqs");
				int numOfRejectedRequirements = rs.getInt("num_of_rejected_reqs");
				int numOfApprovedRequirements = rs.getInt("num_of_approved_reqs");
				int numOfDanglingRequirements = rs.getInt("num_of_dangling_reqs");
				int numOfOrphanRequirements = rs.getInt("num_of_orphan_reqs");
				int numOfSuspectUpstreamRequirements = rs.getInt("num_of_suspect_upstream_reqs");
				int numOfSuspectDownstreamRequirements = rs.getInt("num_of_suspect_downstream_reqs");
				int numOfCompletedRequirements = rs.getInt("num_of_completed_reqs");
				int numOfIncompleteRequirements = rs.getInt("num_of_incomplete_reqs");
				int numOfTestPendingRequirements = rs.getInt("num_of_test_pending_reqs");
				int numOfTestPassRequirements = rs.getInt("num_of_test_pass_reqs");
				int numOfTestFailRequirements = rs.getInt("num_of_test_fail_reqs");
				
				

				
				int dataNumber = 0;
				if (dataType.equals("numOfRequirements")) {
					dataNumber = numOfRequirements;
				}
				if (dataType.equals("numOfDraftRequirements")) {
					dataNumber = numOfDraftRequirements;
				}
				if (dataType.equals("numOfInApprovalWorkflowRequirements")) {
					dataNumber = numOfInApprovalWorkflowRequirements;
				}
				if (dataType.equals("numOfRejectedRequirements")) {
					dataNumber = numOfRejectedRequirements;
				}
				if (dataType.equals("numOfApprovedRequirements")) {
					dataNumber = numOfApprovedRequirements;
				}
				if (dataType.equals("numOfDanglingRequirements")) {
					dataNumber = numOfDanglingRequirements;
				}
				if (dataType.equals("numOfOrphanRequirements")) {
					dataNumber = numOfOrphanRequirements;
				}
				if (dataType.equals("numOfSuspectUpstreamRequirements")) {
					dataNumber = numOfSuspectUpstreamRequirements;
				}
				if (dataType.equals("numOfSuspectDownstreamRequirements")) {
					dataNumber = numOfSuspectDownstreamRequirements;
				}
				if (dataType.equals("numOfCompletedRequirements")) {
					dataNumber = numOfCompletedRequirements;
				}
				if (dataType.equals("numOfIncompleteRequirements")) {
					dataNumber = numOfIncompleteRequirements;
				}
				if (dataType.equals("numOfTestPendingRequirements")) {
					dataNumber = numOfTestPendingRequirements;
				}		
				if (dataType.equals("numOfTestPassRequirements")) {
					dataNumber = numOfTestPassRequirements;
				}		
				if (dataType.equals("numOfTestFailRequirements")) {
					dataNumber = numOfTestFailRequirements;
				}		
		
				
				if (dataLoadDt.equals(oldDataLoadDt)) {
					// still running the old sequence. add it to current dataRow
					dataRow +=  requirementTypeShortName + ":" +  dataNumber +  "," ;
				}
				else {
					// a new data load dt has started. 
					// lets process and reset the dataRow.
					// drop the last , in row
					if (!dataRow.equals("")) {
						if (dataRow.contains(",")){
							dataRow = (String) dataRow.subSequence(0,dataRow.lastIndexOf(","));
						}			
						// lets get the total number of reqs on the oldDataLoadDt.
						int totalReqs = 0;
						Integer totalReqsInteger = (Integer) totalReqsMap.get(oldDataLoadDt);
						if (totalReqsInteger != null) {
							totalReqs = totalReqsInteger.intValue();
						}
						dataString += "\n{" +
					 		"dataLoadDt:\"" +  oldDataLoadDt +  "\", totalReqs:" + totalReqs + ", " + dataRow + "},";
					
					}
					//so lets reset the old Data Load to new value and add the current req type short name and num
					// of requirements to the new dataRow.
					oldDataLoadDt = dataLoadDt;
					dataRow =  requirementTypeShortName + ":" +  dataNumber +  "," ;
				}
				
			}

			// the above logic leaves out the last data row. so lets add it to the allRequirements Data String.
			if (!dataRow.equals("")) {
				if (dataRow.contains(",")){
					dataRow = (String) dataRow.subSequence(0,dataRow.lastIndexOf(","));
				}		
				// lets get the total number of reqs on the dataLoadDt.
				int totalReqs = 0;
				Integer totalReqsInteger = (Integer) totalReqsMap.get(dataLoadDt);
				if (totalReqsInteger != null) {
					totalReqs = totalReqsInteger.intValue();
				}
				dataString += "\n{" +
					"dataLoadDt:\"" +  dataLoadDt + "\", totalReqs:" + totalReqs + ", " + dataRow + "},";

			}
			
			if (dataString.contains(",")) {
				dataString = (String) dataString.subSequence(0,dataString.lastIndexOf(","));
			}
			// lets add the closign ]
			dataString += "]  ";
			
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
		
		return (dataString);
	}
	
	
	
	// returns the most recent data run date.
	// if release == 0, we return the late data run date for project
	// if release == -1 we return the late data run date for user
	public static String  getLastDataLoadDtForReleaseOrProject(int releaseId, int projectId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		String  lastDataLoadDt = "";
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the last data_load_dt from the gr_release_metrics tabel for this release.
			// This sql gets the total number of requirements in a folder.
			String sql = "";
			if (releaseId == 0) {
				if (databaseType.equals("mySQL")){
					sql = "select date_format(max(data_load_dt), '%d %M %Y %r ')  \"data_load_dt\"" +
						" from gr_project_metrics rm " +
						" where rm.project_id =? " ;
				}
				else {
					sql = "select to_char(max(data_load_dt), 'DD MON YYYY')  \"data_load_dt\"" +
					" from gr_project_metrics rm " +
					" where rm.project_id =? " ;
				}
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
			}
			else if (releaseId == -1) {
				if (databaseType.equals("mySQL")){
					sql = "select date_format(max(data_load_dt), '%d %M %Y %r ')  \"data_load_dt\"" +
						" from gr_user_metrics rm " +
						" where rm.project_id =? " ;
				}
				else {
					sql = "select to_char(max(data_load_dt), 'DD MON YYYY')  \"data_load_dt\"" +
					" from gr_user_metrics rm " +
					" where rm.project_id =? " ;
				}
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);				
			}
			else {
				if (databaseType.equals("mySQL")){
					sql = "select date_format(max(data_load_dt), '%d %M %Y %r ')  \"data_load_dt\"" +
						" from gr_release_metrics rm " +
						" where rm.project_id =? " +
						" and rm.release_id = ? ";
				}
				else {
					sql = "select to_char(max(data_load_dt), 'DD MON YYYY')  \"data_load_dt\"" +
					" from gr_release_metrics rm " +
					" where rm.project_id =? " +
					" and rm.release_id = ? ";
				}
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.setInt(2, releaseId);				
			}
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				lastDataLoadDt = rs.getString("data_load_dt");
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
		return (lastDataLoadDt);
	}
	
	public static String  getLastDataLoadDtForAgileSprint(int sprintId, int projectId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		String  lastDataLoadDt = "";
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the last data_load_dt from the gr_release_metrics tabel for this release.
			// This sql gets the total number of requirements in a folder.
			String sql = "";
			
			if (databaseType.equals("mySQL")){
				sql = "select date_format(max(data_load_dt), '%d %M %Y %r ')  \"data_load_dt\"" +
					" from gr_sprint_metrics rm " +
					" where rm.project_id =? " ;
			}
			else {
				sql = "select to_char(max(data_load_dt), 'DD MON YYYY')  \"data_load_dt\"" +
				" from gr_sprint_metrics rm " +
				" where rm.project_id =? " ;
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
		
	
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				lastDataLoadDt = rs.getString("data_load_dt");
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
		return (lastDataLoadDt);
	}

	// returns the most recent data run date.
	public static String  getLastDataLoadDtForBaseline(int rTBaselineId, int projectId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		String  lastDataLoadDt = "";
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the last data_load_dt from the gr_release_metrics tabel for this release.
			// This sql gets the total number of requirements in a folder.
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "select date_format(max(data_load_dt), '%d %M %Y %r ')  \"data_load_dt\"" +
					" from gr_baseline_metrics bm " +
					" where bm.project_id =? " +
					" and bm.rt_baseline_id = ? ";
			}
			else {
				sql = "select to_char(max(data_load_dt), 'DD MON YYYY')  \"data_load_dt\"" +
				" from gr_baseline_metrics bm " +
				" where bm.project_id =? " +
				" and bm.rt_baseline_id = ? ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, rTBaselineId);		
			
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				lastDataLoadDt = rs.getString("data_load_dt");
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
		return (lastDataLoadDt);
	}
	
	
	// returns the most recent data run date.
	public static String  getLastDataLoadDtForFolder(int folderId, int projectId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		String  lastDataLoadDt = "";
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the last data_load_dt from the gr_release_metrics tabel for this release.
			// This sql gets the total number of requirements in a folder.
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "select date_format(max(data_load_dt), '%d %M %Y %r ')  \"data_load_dt\"" +
					" from gr_folder_metrics fm " +
					" where fm.project_id =? " +
					" and fm.folder_id = ? ";
			}
			else {
				sql = "select to_char(max(data_load_dt), 'DD MON YYYY')  \"data_load_dt\"" +
				" from gr_folder_metrics fm " +
				" where fm.project_id =? " +
				" and fm.folder_id = ? ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);		
			
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				lastDataLoadDt = rs.getString("data_load_dt");
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
		return (lastDataLoadDt);
	}
	

	
}
