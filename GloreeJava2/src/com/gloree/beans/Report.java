package com.gloree.beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;


//GloreeJava2


// This class is used to store an object of Report Type.

public class Report {

	/**
	 * 
	 */
	
	private int reportId ;
	private int projectId;
	private int folderId;
	private String reportName;
	private String reportDescription;
	private String reportDefinition;
	private String reportType;
	private String reportVisibility;	
	private int traceTreeDepth;
	private String reportSQL;
	private String createdByEmailId;
	
	
	// The following method is called when the project core values are known and the system is only
	// interested in them. 
	public Report (int reportId , int projectId, int folderId, String reportName,
	 	String reportDescription, String reportDefinition, String reportType, String reportVisibility,
	 	int traceTreeDepth, String reportSQL, 
	 	String createdByEmailId){

		this.reportId = reportId;
		this.projectId = projectId;
		this.folderId = folderId;
		this.reportName = reportName;
		this.reportDescription = reportDescription;
		this.reportDefinition = reportDefinition;
		this.reportType = reportType;
		this.reportVisibility = reportVisibility;
		this.traceTreeDepth = traceTreeDepth;
		this.reportSQL = reportSQL;
		this.createdByEmailId = createdByEmailId;
	
	}
	
	// the following constructor takes the report id and creates a report.
	public Report(int reportId) {
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " select r.id, r.project_id, r.folder_id, r.name, r.description," +
			" r.report_definition, r.report_type, r.visibility, r.trace_tree_depth, " + 
			" r.report_sql, r.created_by " + 
			" from gr_reports r " + 
			" where r.id = ?";
		
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, reportId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				// we will use the reportId we got as a param.
				//int reportId = rs.getInt("id");
				int projectId = rs.getInt("project_id");
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
	
				this.reportId = reportId;
				this.projectId = projectId;
				this.folderId = folderId;
				this.reportName = reportName;
				this.reportDescription = reportDescription;
				this.reportDefinition = reportDefinition;
				this.reportType = reportType;
				this.reportVisibility = reportVisibility;
				this.traceTreeDepth = traceTreeDepth;
				this.reportSQL = reportSQL;
				this.createdByEmailId = createdByEmailId;
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
	
	public int getReportId(){
		return this.reportId;
	}
	
	public int getProectId (){
		return this.projectId;
	}
	
	public int getFolderId(){
		return this.folderId;
	}
	
	public String getReportName(){
		return this.reportName;
	}
	
	public String getReportDescription(){
		return this.reportDescription;
	}
	
	public String getReportDefinition(){
		return this.reportDefinition;
	}
	
	
	public String getReportType(){
		return this.reportType;
	}
	
	public String getReportVisibility(){
		return this.reportVisibility;
	}
	
	
	public int getTraceTreeDepth(){
		return this.traceTreeDepth;
	}
	
	public String getReportSQL(){
		return this.reportSQL;
	}
	public String getCreatedByEmailId(){
		return this.createdByEmailId;
	}
	
		
}

