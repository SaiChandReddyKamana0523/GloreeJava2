package com.gloree.beans;

//GloreeJava2

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;

public class RequirementBaseline {

	
	private int requirementBaselineId;
	private String rTBaselineName;
	private int locked;
	private int requirementId;
	private int requirementVersionId;
	private int requirementBaselinedVersion;
	private String requirementBaselinedDt;
	private String requirementBaselinedName;
	private String requirementBaselinedDescription;
	private String requirementBaselinedApprovers;
	private String requirementBaselinedStatus;
	private String requirementBaselinedPriority;
	private String requirementBaselinedOwner;
	private int requirementBaselinedPctComplete;
	private String requirementBaselinedExternalURL;
	private String requirementBaselinedTraceTo;
	private String requirementBaselinedTraceFrom;
	private String requirementBaselinedUserDefinedAttributes;
	
	
	
	
	public RequirementBaseline (int requirementBaselineId, String rTBaselineName, int locked, int requirementId,
		int requirementVersionId, int requirementBaselinedVersion, String requirementBaselinedDt,
		String requirementBaselinedName, String requirementBaselinedDescription, 
		String requirementBaselinedApprovers, String requirementBaselinedStatus,  String requirementBaselinedPriority,
		String requirementBaselinedOwner , int requirementBaselinedPctComplete , String requirementBaselinedExternalURL, 
		String requirementBaselinedTraceTo , String requirementBaselinedTraceFrom , String requirementBaselinedUserDefinedAttributes ){
		
		this.requirementBaselineId = requirementBaselineId;
		this.rTBaselineName = rTBaselineName;
		this.locked = locked;
		this.requirementId = requirementId;
		this.requirementVersionId = requirementVersionId;
		this.requirementBaselinedVersion  = requirementBaselinedVersion;
		this.requirementBaselinedDt = requirementBaselinedDt;
		this.requirementBaselinedName = requirementBaselinedName;
		this.requirementBaselinedDescription = requirementBaselinedDescription;
		this.requirementBaselinedApprovers = requirementBaselinedApprovers;
		this.requirementBaselinedStatus = requirementBaselinedStatus;
		this.requirementBaselinedPriority = requirementBaselinedPriority;
		this.requirementBaselinedOwner = requirementBaselinedOwner;
		this.requirementBaselinedPctComplete = requirementBaselinedPctComplete;
		this.requirementBaselinedExternalURL = requirementBaselinedExternalURL;
		this.requirementBaselinedTraceTo = requirementBaselinedTraceTo;
		this.requirementBaselinedTraceFrom = requirementBaselinedTraceFrom;
		this.requirementBaselinedUserDefinedAttributes = requirementBaselinedUserDefinedAttributes;
		
		
	}
	
	// the following method is used when the system knows only the baselineId and wants this bean
	// to go and get details from the db to create the bean.
	public RequirementBaseline (int requirementBaselineId, String databaseType){

		java.sql.Connection con =  null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now we get the data from the database and populate the bean.
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " select rtb.name \"baseline_name\", rtb.locked,  " +
					" b.id, b.requirement_id, b.version_id, v.version, " + 
					" date_format(b.baselined_dt , '%d %M %Y %r ') \"baselined_dt\" , v.name, v.description, " +
					" v.approvers , v.status, v.priority, v.owner, v.pct_complete, v.external_url," +
					" v.trace_to, v.trace_from, v.user_defined_attributes " +
					" from gr_requirement_baselines b, gr_requirement_versions v , " +
					"	gr_rt_baselines rtb " +
					" where b.id = ? " +
					" and b.version_id = v.id " +
					" and b.rt_baseline_id = rtb.id ";
			}
			else {
				sql = " select rtb.name \"baseline_name\", rtb.locked," +
				" b.id, b.requirement_id, b.version_id, v.version, " + 
				" to_char(b.baselined_dt , 'DD MON YYYY') \"baselined_dt\" , v.name, v.description, " +
				" v.approvers , v.status, v.priority, v.owner, v.pct_complete, v.external_url," +
				" v.trace_to, v.trace_from, v.user_defined_attributes " +
				" from gr_requirement_baselines b, gr_requirement_versions v , " +
				"	gr_rt_baselines rtb " +
				" where b.id = ? " +
				" and b.version_id = v.id " +
				" and b.rt_baseline_id = rtb.id ";
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementBaselineId);
			ResultSet rs = prepStmt.executeQuery();
	
			if (rs.next()){
				this.rTBaselineName = rs.getString("baseline_name");
				this.locked = rs.getInt("locked");
				this.requirementBaselineId = rs.getInt("id");
				this.requirementId = rs.getInt("requirement_id");
				this.requirementVersionId = rs.getInt("version_id");
				this.requirementBaselinedVersion = rs.getInt("version");
				this.requirementBaselinedDt = rs.getString("baselined_dt");
				this.requirementBaselinedName = rs.getString("name");				
				this.requirementBaselinedDescription = rs.getString("description");
				this.requirementBaselinedApprovers = rs.getString("approvers");
				this.requirementBaselinedStatus = rs.getString("status");
				this.requirementBaselinedPriority = rs.getString("priority");
				this.requirementBaselinedOwner = rs.getString("owner");
				this.requirementBaselinedPctComplete = rs.getInt("pct_complete");
				this.requirementBaselinedExternalURL = rs.getString("external_url");
				this.requirementBaselinedTraceTo = rs.getString("trace_to");
				this.requirementBaselinedTraceFrom = rs.getString("trace_from");
				this.requirementBaselinedUserDefinedAttributes = rs.getString("user_defined_attributes");
				
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

	
		
	
	public int getRequirementBaselineId(){
		return this.requirementBaselineId;
	}
	
	public String getRTBaselineName(){
		return this.rTBaselineName;
	}
	
	public int getLocked () {
		return this.locked;
	} 
	
	public int getRequirementId () {
		return this.requirementId;
	}
	
	public int getVersionId(){
		return this.requirementVersionId;
	}
	
	public int getRequirementBaselinedVersion(){
		return this.requirementBaselinedVersion;
	}

	public String getRequirementBaselinedDt(){
		return this.requirementBaselinedDt;
	}

	public String getRequirementBaselinedName(){
		return this.requirementBaselinedName;
	}

	public String getRequirementBaselinedDescription(){
		return this.requirementBaselinedDescription;
	}

	public String getRequirementBaselinedApprovers(){
		return this.requirementBaselinedApprovers;
	}
	
	public String getRequirementBaselinedStatus(){
		return this.requirementBaselinedStatus;
	}
	
	public String getRequirementBaselinedPriority(){
		return this.requirementBaselinedPriority;
	}
	
	public String getRequirementBaselinedOwner(){
		return this.requirementBaselinedOwner;
	}
	
	public int getRequirementBaselinedPctComplete(){
		return this.requirementBaselinedPctComplete;
	}
	
	public String getRequirementBaselinedExternalURL(){
		return this.requirementBaselinedExternalURL;
	}
	
	public String getRequirementBaselinedTraceTo(){
		return this.requirementBaselinedTraceTo;
	}
	
	public String getRequirementBaselinedTraceFrom(){
		return this.requirementBaselinedTraceFrom;
	}
	
	public String getRequirementBaselinedUser(){
		return this.requirementBaselinedApprovers;
	}
	
	
	
	
	
	
	
	
}
