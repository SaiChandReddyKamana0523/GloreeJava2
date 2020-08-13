 package com.gloree.beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;

// This class is used to store a Organization object

public class ScheduledReport {

	/**
	 * 
	 */
	private int scheduledReportId;
	private int projectId;
	private int reportId;
	private String attachmentType;
	private String toEmailAddresses;
	private String ccEmailAddresses;
	private String subjectValue;
	private String messageValue;
	private String runTaskOn;
	private String owner;
		
	
	// The following method is called when the Org core values are known and the system is only
	// interested in them. 
	public ScheduledReport (int scheduledReportId, int projectId, int reportId, String attachmentType, String toEmailAddresses,
	String ccEmailAddresses,String subjectValue,	String messageValue,String runTaskOn,	String owner){
		if (
			(!(attachmentType.equals(this.attachmentType)))
			||
			(!(toEmailAddresses.equals(this.toEmailAddresses)))
			||
			(!(ccEmailAddresses.equals(this.ccEmailAddresses)))
			||
			(!(subjectValue.equals(this.subjectValue)))
			||
			(!(messageValue.equals(this.messageValue)))
			||
			(!(runTaskOn.equals(this.runTaskOn)))
			
			){
			// this means that the underlying bean in the db needs to be updated.
			java.sql.Connection con =  null;
			try {
				
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();

				
				String sql = " update gr_scheduled_reports " +
						" set   attachment_type  = ? , " +
						" to_email_addresses = ? , " +
						" cc_email_addresses = ? ," +
						" subject_value = ? , " +
						" message_value = ? , " +
						" run_task_on = ? " +
						" where id = ? ";
				
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1,attachmentType);
				prepStmt.setString(2,toEmailAddresses);
				prepStmt.setString(3,ccEmailAddresses);
				prepStmt.setString(4,subjectValue);
				prepStmt.setString(5,messageValue);
				prepStmt.setString(6,runTaskOn);
				prepStmt.setInt(7,scheduledReportId);
			
				prepStmt.execute();
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
		
		this.scheduledReportId = scheduledReportId;
		this.projectId = projectId;
		this.reportId = reportId;
		this.attachmentType = attachmentType;
		this.toEmailAddresses = toEmailAddresses;
		this.ccEmailAddresses = ccEmailAddresses;
		this.subjectValue = subjectValue;
		this.messageValue = messageValue;
		this.runTaskOn =  runTaskOn;
		this.owner =  owner;
	}
	
	public ScheduledReport (int scheduledReportId){
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql creates a organization account.
			//
			String sql = "select id, project_id, report_id, attachment_type, to_email_addresses, cc_email_addresses," +
				" subject_value, message_value, run_task_on, owner " +
				" from gr_scheduled_reports where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,scheduledReportId);
			ResultSet rs = prepStmt.executeQuery();
			// Only one row should be returned.
			while (rs.next()){				
				this.scheduledReportId = rs.getInt("id");
				this.projectId = rs.getInt("project_id");
				this.reportId = rs.getInt("report_id");
				this.attachmentType = rs.getString("attachment_type");
				this.toEmailAddresses = rs.getString("to_email_addresses");
				this.ccEmailAddresses = rs.getString("cc_email_addresses");
				this.subjectValue = rs.getString("subject_value");
				this.messageValue = rs.getString("message_value");
				this.runTaskOn =  rs.getString("run_task_on");
				this.owner =  rs.getString("owner");
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

	public ScheduledReport (int projectId, int reportId, String attachmentType, String toEmailAddresses,
			String ccEmailAddresses,String subjectValue,	String messageValue,String runTaskOn,	String owner){
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql creates a organization account.
			//
			String sql = "insert into gr_scheduled_reports (project_id, report_id, attachment_type, to_email_addresses, cc_email_addresses," +
				" subject_value, message_value, run_task_on, owner) " +
				" values (?, ?, ?, ?, ? ," +
				" ?, ? , ? , ? )";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2,reportId);
			prepStmt.setString(3, attachmentType);
			prepStmt.setString(4, toEmailAddresses);
			prepStmt.setString(5, ccEmailAddresses);
			
			prepStmt.setString(6, subjectValue);
			prepStmt.setString(7, messageValue);
			prepStmt.setString(8, runTaskOn);
			prepStmt.setString(9, owner);
			
			prepStmt.execute();
	

			this.projectId = projectId; 
			this.reportId = reportId;
			this.attachmentType = attachmentType;
			this.toEmailAddresses = toEmailAddresses;
			this.ccEmailAddresses = ccEmailAddresses;
			this.subjectValue = subjectValue;
			this.messageValue = messageValue;
			this.runTaskOn =  runTaskOn;
			this.owner =  owner;
		
			// since we don't have the scheduledReportid, lets get the id of the report we just created.
			sql = "select max(id) 'scheduledReportId' " +
				" from gr_scheduled_reports " +
				" where owner = ? " +
				" and report_id = ? " +
				" and project_id = ? ";
				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1,owner);
			prepStmt.setInt(2, reportId);
			prepStmt.setInt(3, projectId);
			ResultSet rs = prepStmt.executeQuery();
			// Only one row should be returned.
			while (rs.next()){				
				this.scheduledReportId = rs.getInt("scheduledReportId");
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


	public int getScheduledReportId(){
		return this.scheduledReportId;
	}

	public int getProjectId(){
		return this.projectId;
	}
	
	public int getReportId(){
		return this.reportId;
	}
	
	public String getAttachmentType() {
		return this.attachmentType;
	}
	
	public String getToEmailAddresses() {
		if ((this.toEmailAddresses == null ) || (this.toEmailAddresses == "null")){
			this.toEmailAddresses = "";
		}
		return this.toEmailAddresses;
	}
	
	public String getCcEmailAddresses() {
		if ((this.ccEmailAddresses == null ) || (this.ccEmailAddresses == "null")){
			this.ccEmailAddresses = "";
		}
		return this.ccEmailAddresses;
	}
	
	public String getSubjectValue() {
		return this.subjectValue;
	}
	
	public String getMessageValue () {
		return this.messageValue;
	}
	
	public String getRunTaskOn() {
		return this.runTaskOn;
	}
	
	public String getOwner(){
		return this.owner;
	}
	
	public void destroy (){
		java.sql.Connection con =  null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "delete from  gr_scheduled_reports " +
				" where id = ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.scheduledReportId);
			prepStmt.execute();
	
			
			this.scheduledReportId = 0;
			this.projectId = 0; 
			this.reportId = 0;
			this.attachmentType = "";
			this.toEmailAddresses = "";
			this.ccEmailAddresses = "";
			this.subjectValue = "";
			this.messageValue = "";
			this.runTaskOn =  "";
			this.owner =  "";
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


	
}
