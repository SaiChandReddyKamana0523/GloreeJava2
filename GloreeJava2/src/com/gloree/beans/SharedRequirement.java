package com.gloree.beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

import javax.naming.InitialContext;

import com.gloree.utils.ProjectUtil;

// this is a wrapper class of Requirement Type.
public class SharedRequirement {

	private int sourceRequirementId; 
	private String sourceRequirementName;
	private String sourceRequirementDescription;
	private int sourceRequirementVersion;
	private String sourceRequirementBaselineName;
	private int sourceRequirementBaselineId;

	private int targetRequirementId; 
	private String targetRequirementName;

	 // these are all custom attributes
	private String targetRequirementDescription;
	private int targetRequirementVersion;
	private String targetRequirementBaselineName;


	private String importedBy;
	private String importedDate;
	private String lastRefreshedBy;
	private String lastRefreshedDate;
	
	public SharedRequirement (	int sourceRequirementId, String sourceRequirementName,
	String sourceRequirementDescription,int sourceRequirementVersion,String sourceRequirementBaselineName,
	int sourceRequirementBaselineId, int targetRequirementId, 
	String targetRequirementName, String targetRequirementDescription, int targetRequirementVersion,
	String targetRequirementBaselineName, String importedBy,
	String importedDate,String lastRefreshedBy,String lastRefreshedDate
) {
		 this.sourceRequirementId = sourceRequirementId; 
		 this.sourceRequirementName = sourceRequirementName;
		 this.sourceRequirementDescription = sourceRequirementDescription;
		 this.sourceRequirementVersion = sourceRequirementVersion;
		 this.sourceRequirementBaselineName = sourceRequirementBaselineName;
		 this.sourceRequirementBaselineId = sourceRequirementBaselineId;

		 this.targetRequirementId= targetRequirementId; 
		 this.targetRequirementName = targetRequirementName;
		 this.targetRequirementDescription = targetRequirementDescription;
		 
		 // these are all custom attributes
		 // the following are the source version, baseline name, etc on the date of the import.
		 this.targetRequirementVersion = targetRequirementVersion;
		 this.targetRequirementBaselineName = targetRequirementBaselineName;

		 this.importedBy = importedBy;
		 this.importedDate = importedDate;
		 this.lastRefreshedBy = lastRefreshedBy;
		 this.lastRefreshedDate = lastRefreshedDate;
	}
	
	public SharedRequirement (int sharedRequirementId, int sRRTBaselineId, int targetProjectId, String databaseType) {

		java.sql.Connection con = null;

		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			this.sourceRequirementId = sharedRequirementId;
			
			String sql = "select v.name, v.description, v.version, " +
				" rb.name \"baseline_name\", rb.id \"baseline_id\" " +
				" from gr_requirements r , " +
				" gr_requirement_baselines b, gr_requirement_versions v, gr_rt_baselines rb " +
				" where r.id  = ? " +
				" and r.id = b.requirement_id " +
				" and  b.rt_baseline_id = ?  " +
				" and b.rt_baseline_id = rb.id " +
				" and b.version_id = v.id ";

			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sharedRequirementId);
			prepStmt.setInt(2, sRRTBaselineId);
			ResultSet rs = prepStmt.executeQuery();


			if (rs.next()){
				this.sourceRequirementName = rs.getString("name");
				this.sourceRequirementDescription = rs.getString("description");
				this.sourceRequirementVersion = rs.getInt("version");
				this.sourceRequirementBaselineName = rs.getString("baseline_name");
				this.sourceRequirementBaselineId = rs.getInt("baseline_id");
			}
	
			sql = "select id " +
				" from gr_requirements " +
				" where project_id = ? " +
				" and source_requirement_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, targetProjectId);
			prepStmt.setInt(2,sharedRequirementId);
			rs = prepStmt.executeQuery();
			int targetRequirementId = 0;
			while (rs.next()){
				targetRequirementId = rs.getInt("id");
			}
			
			this.targetRequirementId = targetRequirementId;
			Requirement targetRequirement = new Requirement(targetRequirementId,  databaseType);
			this.targetRequirementName = targetRequirement.getRequirementName();
			this.targetRequirementDescription = targetRequirement.getRequirementDescription();
			try {
				this.targetRequirementVersion = Integer.parseInt(targetRequirement.getAttributeValue("Source Version"));
			}
			catch (Exception e){
				this.targetRequirementVersion = 0;
			}
			this.targetRequirementBaselineName = targetRequirement.getAttributeValue("Source Baseline");

			this.importedBy = targetRequirement.getAttributeValue("Imported By");
			this.importedDate = targetRequirement.getAttributeValue("Imported Date");
			this.lastRefreshedBy = targetRequirement.getAttributeValue("Last Refreshed By");
			this.lastRefreshedDate = targetRequirement.getAttributeValue("Last Refreshed Dt");

			 
			 
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}    finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
	}
	
	
	public int getSourceRequirementId(){
		return this.sourceRequirementId;
	}
	public String getSourceRequirementName(){
		return this.sourceRequirementName;
	}
	public String getSourceRequirementDescription(){
		return this.sourceRequirementDescription;
	}
	public int getSourceRequirementVersion(){
		return this.sourceRequirementVersion;
	}
	public String getSourceRequirementBaselineName(){
		return this.sourceRequirementBaselineName;
	}
	public int getSourceRequirementBaselineId(){
		return this.sourceRequirementBaselineId;
	}

	public int getTargetRequirementId(){
		return this.targetRequirementId;
	}
	public String getTargetRequirementName(){
		if (this.targetRequirementName == null){
			this.targetRequirementName = "";
		}
		return this.targetRequirementName;
	}

	 // these are all custom attributes
	public String getTargetRequirementDescription(){
		if (this.targetRequirementDescription == null){
			this.targetRequirementDescription = "";
		}
		return this.targetRequirementDescription;
	}
	public int getTargetRequirementVersion(){
		return this.targetRequirementVersion;
	}
	public String getTargetRequirementBaselineName(){
		return this.targetRequirementBaselineName;
	}
	

	public String getImportedBy(){
		return this.importedBy;
	}
	public String getImportedDate(){
		return this.importedDate;
	}
	public String getLastRefreshedBy(){
		return this.lastRefreshedBy;
	}
	public String getLastRefreshedDate(){
		return this.lastRefreshedDate;
	}
	
	
}
