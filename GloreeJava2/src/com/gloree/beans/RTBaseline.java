package com.gloree.beans;

//GloreeJava2

import com.gloree.utils.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;

public class RTBaseline {

	
	private int baselineId;
	private int requirementTypeId;
	private String baselineName;
	private int locked;
	private String baselineDescription;
	private String createdBy;
	//private Date createdDt;
	private String lastModifiedBy;
	//private Date lastModifiedDt;
	
	
	
	// The following method is called when the Baseline core values are already known and the system is only
	// interested in creating a bean. . 
	public RTBaseline (int baselineId, int requirementTypeId, String baselineName,  int locked,
			String baselineDescription,String createdBy, String lastModifiedBy){
		
		this.baselineId = baselineId;
		this.requirementTypeId = requirementTypeId;
		this.baselineName = baselineName;
		this.locked =  locked;
		this.baselineDescription = baselineDescription;
		this.createdBy = createdBy;
		//this.createdDt = rs.getDate("created_dt");
		this.lastModifiedBy = lastModifiedBy;
		//this.lastModifiedDt = rs.getDate("last_modified_by");
	}
	
	
	// the following method is used when the system knows only the baselineId and wants this bean
	// to go and get details from the db to create the bean.
	public RTBaseline (int rTBaselineId) {

		java.sql.Connection con =  null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now we get the data from the database and populate the bean.
			String sql = "select id, requirement_type_id, name, locked, description,  " +
				" created_by, created_dt, last_modified_by , last_modified_dt " + 
				" from gr_rt_baselines " +
				" where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			ResultSet rs = prepStmt.executeQuery();
	
			if (rs.next()){
				this.baselineId = rs.getInt("id");
				this.requirementTypeId = rs.getInt("requirement_type_id");
				this.baselineName = rs.getString("name");
				this.locked = rs.getInt("locked");
				this.baselineDescription = rs.getString ("description");
				this.createdBy = rs.getString("created_by");
				//this.createdDt = rs.getDate("created_dt");
				this.lastModifiedBy = rs.getString("last_modified_by") ;
				//this.lastModifiedDt = rs.getDate("last_modified_by");
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

	
	
	// the following method is used when the system knows only the information required to create a bean in the db
	// the code will do the following :
	// 1. create the baseline
	// 2. set the bean baselines.
	
	public RTBaseline (String databaseType,int projectId, int requirementTypeId, String baselineName , int locked,
		String baselineDescription, String createdByEmailId) {
		java.sql.Connection con =  null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now insert the row in the database.
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_rt_baselines (requirement_type_id,name, locked, description," +
					" created_by, created_dt, " +
					" last_modified_by , last_modified_dt) " +
					" values (?, ?, ?, ?, ?, now(), ?, now())";
			}
			else{
				sql = " insert into gr_rt_baselines (requirement_type_id,name, locked, description," +
				" created_by, created_dt, " +
				" last_modified_by , last_modified_dt) " +
				" values (?, ?, ?, ?, ?, sysdate, ?, sysdate)";
			}
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			prepStmt.setString(2, baselineName);
			prepStmt.setInt(3, locked);
			prepStmt.setString(4, baselineDescription);
			prepStmt.setString(5, createdByEmailId);
			prepStmt.setString(6, createdByEmailId);
			prepStmt.execute();
			
			// at this point the baseline is created in the db.

			// get the id of the baseline we have just created. We are going to use this for creating baseline 
			// values for existing reqs of this req type.
			// since baseline name is unique for a req type id, we should be OK here
			// getting the id based on those 2 params.
			// Now we get the data from the database and populate the bean.
			sql = "select id, requirement_type_id, name, locked, description, " +
				" created_by, created_dt, last_modified_by , last_modified_dt " + 
				" from gr_rt_baselines " +
				" where requirement_type_id = ? " + 
				" and name = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			prepStmt.setString(2, baselineName);
			ResultSet rs = prepStmt.executeQuery();
	
			if (rs.next()){
				this.baselineId = rs.getInt("id");
				this.requirementTypeId = rs.getInt("requirement_type_id");
				this.baselineName = rs.getString("name");
				this.locked = rs.getInt("locked");
				this.baselineDescription = rs.getString ("description");
				this.createdBy = rs.getString("created_by");
				//this.createdDt = rs.getDate("created_dt");
				this.lastModifiedBy = rs.getString("last_modified_by") ;
				//this.lastModifiedDt = rs.getDate("last_modified_by");
			}
	
			
			// lets create a log entry in the project log.
			ProjectUtil.createProjectLog(projectId, baselineName, "Create", 
				"Created Baseline ", createdByEmailId,  databaseType);
			
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
	

	// the following method is used when the system needs to update it in the db
	// and then create the beam.
	// NOTE : if rTBaselineId exists in the constructor call, then we must be calling to update. Else , we must be calling 
	// to create it.
	// 1. update the baseline in the database
	// 2. create the bean objects. 
	
	// we should consider giving this a different name, as it is not a constructor.
	public void setNameValue(int rTBaselineId, String baselineName , int locked, 
		String baselineDescription, String modifiedBy, String databaseType) {

		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now insert the row in the database.
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "update gr_rt_baselines " +
					" set name = ? ," +
					" locked = ? , " +
					" description = ? , " +
					" last_modified_by = ?  , last_modified_dt = now() " +
					" where id = ? " ;
			}
			else {
				sql = "update gr_rt_baselines " +
				" set name = ? ," +
				" locked = ? , " +
				" description = ? , " +
				" last_modified_by = ?  , last_modified_dt = sysdate " +
				" where id = ? " ;
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, baselineName);
			prepStmt.setInt(2, locked);
			prepStmt.setString(3, baselineDescription);
			prepStmt.setString(4, modifiedBy);
			
			prepStmt.setInt(5, rTBaselineId);
			prepStmt.execute();
			
			// at this point the baseline is updated in the db.
			
			this.baselineId = rTBaselineId;
			this.baselineName = baselineName;
			this.baselineDescription = baselineDescription;
			this.lastModifiedBy = modifiedBy;

			
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}   finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
	}
	
	
	public int getBaselineId(){
		return this.baselineId;
	}
	
	public int getRequirementTypeId () {
		return this.requirementTypeId;
	}
	
	public String getBaselineName(){
		return this.baselineName;
	}
	
	public int getLocked(){
		return this.locked;
	}
	
	
	public String getBaselineDescription () {
		return this.baselineDescription;
	}
	
	
	
	public String getCreatedBy () {
		return this.createdBy;
	}
	
	//public Date getCreatedDt () {
	//	return this.createdDt;
	//}
	
	public String getLastModifiedBy () {
		return this.lastModifiedBy;
	}
	
	//public Date getLastModifiedDt () {
	//	return this.lastModifiedDt;
	//}
	
	public RequirementType getRequirementType () {
		RequirementType requirementType = new RequirementType (this.requirementTypeId );
		return requirementType;
	}
	
}
