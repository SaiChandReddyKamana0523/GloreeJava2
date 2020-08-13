package com.gloree.beans;

//GloreeJava2

import com.gloree.utils.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;

public class DefectStatus {

	
	private int defectStatusGroupId;
	private int projectId;
	private String defectStatus;
	private String defectStatusGroup;
	
	
	public DefectStatus (int defectStatusGroupId, int projectId, String defectStatus,String defectStatusGroup){
		this.defectStatusGroupId = defectStatusGroupId;
		this.projectId = projectId;
		this.defectStatus = defectStatus;
		this.defectStatusGroup = defectStatusGroup;
	}
	
	
	public int getDefectStatusGroupId(){
		return this.defectStatusGroupId;
	}
	
	public int getProjectId () {
		return this.projectId;
	}
	
	public String getDefectStatus(){
		return this.defectStatus;
	}
	
	
	public String getDefectStatusGroup() {
		return this.defectStatusGroup;
	}
	
	public void setDefectStatusGroup(String defectStatusGroup){
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " update gr_defect_status_grouping " +
				" set defect_status_group = ? " +
				" where id = ? ";
		
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, defectStatusGroup);
			prepStmt.setInt(2, this.defectStatusGroupId);
			prepStmt.execute();
			prepStmt.close();
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
	
}
