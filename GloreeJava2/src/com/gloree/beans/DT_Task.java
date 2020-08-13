package com.gloree.beans;

//GloreeJava2

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;

import com.gloree.utils.EmailUtil;
import com.gloree.utils.ProjectUtil;
import com.gloree.utils.RequirementUtil;

public class DT_Task {

	private int taskId;
	private String taskTitle;
	private String taskDescription;
	private String createdBy;
	
	private Date createdOn;
	private String owner;
	private int completion;
	private String priority;
	
	private String tags;
	private Date completionRequiredDt;
	private String stakeholders;
	private String workStatus;
	
	
	// since id is given , it is in the database. no need to create one in the database.
	
	public DT_Task (
			int taskId, String taskTitle, String taskDescription , String createdBy, 
			Date createdOn, String owner, int completion, String priority,
			String tags, Date completionRequiredDt, String stakeholders, String workStatus) {
		
		
		this.taskId = taskId;
		this.taskTitle= taskTitle;
		this.taskDescription = taskDescription;
		
		this.createdBy = createdBy;
		this.createdOn = createdOn;
		this.owner = owner;
		this.completion = completion;
		this.priority = priority;
		
		this.tags = tags;
		this.completionRequiredDt = completionRequiredDt;
		this.stakeholders = stakeholders;
		this.workStatus = workStatus;
		
		
	}

	public DT_Task (int taskId) {
		
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			
			// ALL new requirements are in 'Draft' status , irrespective of the approval status for the req type.
			String sql  = "  select "
					+ "	id, title, description, created_by,  "
					+ " created_on, owner, completion, priority, "
					+ " tags, completion_required_date, stakeholders, work_status "
					+ " from dt_tasks "
					+ " where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, taskId);

			ResultSet rs = prepStmt.executeQuery();

			
			while (rs.next()) {
				
				this.taskId = rs.getInt("id");
				this.taskTitle = rs.getString("title");
				this.taskDescription  = rs.getString("description");
				this.createdBy = rs.getString("created_by"); 
			
				this.createdOn= rs.getDate("created_on");
				this.owner = rs.getString("owner");
				this.completion = rs.getInt("completion"); 
				this.priority = rs.getString("priority");
				
				this.tags = rs.getString("tags"); 
				this.completionRequiredDt = rs.getDate("completion_required_date"); 
				this.stakeholders = rs.getString("stakeholders"); 
				this.workStatus = rs.getString("work_status");
				
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// If not id is given means it is not in the database, so lets create one in the database.
	public DT_Task (String taskTitle,  String taskDescription, String createdBy, String owner, 
			int completion , String priority, String tags, 
			Date completionRequiredDt, String stakeholders, String workStatus) {

		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			PreparedStatement prepStmt;
			
			
			// ALL new requirements are in 'Draft' status , irrespective of the approval status for the req type.
			sql = "insert into dt_tasks("
					+ "title, description,created_by, created_on, "
					+ "owner, completion, priority, "
					+ "tags, completion_required_date, stakeholders, work_status) " +
					" values ("
					+ "?, ?, ?, now(),"
					+ "?, ?, ?, "
					+ "?, ?, ?, ?)";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, taskTitle);
			prepStmt.setString(2, taskDescription);
			prepStmt.setString(3, createdBy);
			
			
			prepStmt.setString(4, owner);
			prepStmt.setInt(5, completion);
			prepStmt.setString(6, priority);
			
			prepStmt.setString(7, tags);
			prepStmt.setDate(8, completionRequiredDt);
			prepStmt.setString(9, stakeholders);
			prepStmt.setString(10, workStatus);
			
			
			
			
			prepStmt.execute();
		
			
			// lets get the last inserted id
			sql = "select last_insert_id() as last_id";
			prepStmt = con.prepareStatement(sql);
			ResultSet rs = prepStmt.executeQuery();
			int lastId = 0;
			while (rs.next()){
				lastId = rs.getInt("last_id");
			}
			
			this.taskId = lastId;
			this.taskTitle = taskTitle;
			this.taskDescription = taskDescription;
			this.createdBy = createdBy;
			
			this.owner = owner;
			this.completion = completion;
			this.priority = priority;
			
			this.tags = tags;
			this.completionRequiredDt = completionRequiredDt;
			this.stakeholders = stakeholders;
			this.workStatus = workStatus;
			
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		
	}

	
	public int getId(){
		return this.taskId;
	}

	
	public String getTitle() {
		return this.taskTitle;
	}
	
	public String getDescription() {
		return this.taskDescription;
	}

	
	public String getCreatedBy() {
		return this.createdBy;
	}
	
	public Date getCreatedOn() {
		return this.createdOn;
	}
	
	public String getOwner() {
		return this.owner;
	}
	
	public int getCompletion () {
		return this.completion;
	}
	
	public String getPriority() {
		return this.priority;
	}
	
	public String getTags() {
		return this.tags;
	}
	
	public Date getCompletionRequiredDt() {
		return this.completionRequiredDt;
	}
	
	public String getStakeholders() {
		return this.stakeholders;
	}
	
	public String getWorkStatus() {
		return this.workStatus;
	}
	
	
	
	
}
