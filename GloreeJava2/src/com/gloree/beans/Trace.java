package com.gloree.beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;


public class Trace {

	private int id;
	private String description;
	private int toRequirementId;
	private String toRequirementProjectShortName;
	private int fromRequirementId;
	private String fromRequirementProjectShortName;
	private int suspect;
	
	
	// The following method is called when the trace core values are known and the system is only
	// interested in them. 
	public Trace(int id, String description, int toRequirementId, String toRequirementProjectShortName,
		int fromRequirementId, String fromRequirementProjectShortName, int suspect){
		this.id = id;
		this.description = description;
		this.toRequirementId = toRequirementId;
		this.toRequirementProjectShortName = toRequirementProjectShortName;
		this.fromRequirementId = fromRequirementId;
		this.fromRequirementProjectShortName = fromRequirementProjectShortName;
		this.suspect = suspect;
	
	}
	
	// takes the traceId and creates the bean from the values in the db.
	public Trace(int id){
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select t.id, t.description, t.to_requirement_id, t.from_requirement_id, t.suspect," +
				" toP.short_name \"to_requirement_project_short_name\", " +
				" fromP.short_name \"from_requirement_project_short_name\" " +
				" from gr_traces t , gr_requirements toR, gr_projects toP, gr_requirements fromR, gr_projects fromP" +
				" where t.id = ? " +
				" and t.to_requirement_id = toR.id" +
				" and toR.project_id = toP.id" +
				" and t.from_requirement_id = fromR.id" +
				" and fromR.project_id = fromP.id ";
			
			PreparedStatement prepStmt= con.prepareStatement(sql);
			prepStmt.setInt(1, id);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				this.id = rs.getInt("id");
				this.description = rs.getString("description");
				this.toRequirementId = rs.getInt("to_requirement_id");
				this.fromRequirementId = rs.getInt("from_requirement_id");
				this.suspect = rs.getInt(suspect);
				this.toRequirementProjectShortName = rs.getString("to_requirement_project_short_name");
				this.fromRequirementProjectShortName = rs.getString("from_requirement_project_short_name");
			}
			rs.close();
			prepStmt.close();
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

	
	// takes the traceId and creates the bean from the values in the db.
	public Trace(int inputFromRequirmentId, int inputToRequirementId){
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select t.id, t.description, t.to_requirement_id, t.from_requirement_id, t.suspect," +
				" toP.short_name \"to_requirement_project_short_name\", " +
				" fromP.short_name \"from_requirement_project_short_name\" " +
				" from gr_traces t , gr_requirements toR, gr_projects toP, gr_requirements fromR, gr_projects fromP" +
				" where t.from_requirement_id = ? and t.to_requirement_id = ?  " +
				" and t.to_requirement_id = toR.id" +
				" and toR.project_id = toP.id" +
				" and t.from_requirement_id = fromR.id" +
				" and fromR.project_id = fromP.id ";
			
			PreparedStatement prepStmt= con.prepareStatement(sql);
			prepStmt.setInt(1, inputFromRequirmentId);
			prepStmt.setInt(2, inputToRequirementId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				this.id = rs.getInt("id");
				this.description = rs.getString("description");
				this.toRequirementId = rs.getInt("to_requirement_id");
				this.fromRequirementId = rs.getInt("from_requirement_id");
				this.suspect = rs.getInt(suspect);
				this.toRequirementProjectShortName = rs.getString("to_requirement_project_short_name");
				this.fromRequirementProjectShortName = rs.getString("from_requirement_project_short_name");
			}
			rs.close();
			prepStmt.close();
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

	
	public int getId(){
		return this.id;
	}

	public String getDescription(){
		return this.description;
	}
	public int getToRequirementId(){
		return this.toRequirementId;
	}
	
	public String getToRequirementProjectShortName(){
		return this.toRequirementProjectShortName;
	}
	
	public int getFromRequirementId(){
		return this.fromRequirementId;
	}

	public String getFromRequirementProjectShortName(){
		return this.fromRequirementProjectShortName;
	}
	
	public int getSuspect(){
		return this.suspect;
	}
}
