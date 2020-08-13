package com.gloree.beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;


public class WebForm {

	
	
	private int id;
	private int projectId;
	private int folderId;
	private String name;
	private String description;
	private String introduction;
	private String defaultOwner;
	private String notifyOnCreation;
	private int submitForApprovalOnCreation;
	private int enableLookup;
	private String accessCode;
	
	
	
	// The following method is called when the License Grant core values are already known and the system is only
	// interested in creating a bean. . 
	public WebForm ( int id, int projectId, int folderId, String name, String description, String introduction, String defaultOwner,
	 String notifyOnCreation, int submitForApprovalOnCreation, String accessCode){
		this.id = id;
		this.projectId = projectId;
		this.folderId = folderId;
		this.name = name;
		this.description = description;
		this.introduction = introduction;
		this.defaultOwner = defaultOwner;
		this.notifyOnCreation = notifyOnCreation;
		this.submitForApprovalOnCreation = submitForApprovalOnCreation;
		this.accessCode = accessCode;

	}
	
	
	// create new web form
	public WebForm ( int projectId, int folderId, String name, String description, String introduction, String defaultOwner,
	 String notifyOnCreation, int submitForApprovalOnCreation, int enableLookup, String accessCode){
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " insert into gr_webforms(project_id, folder_id, " +
					" name, description, introduction, default_owner, " +
					" notify_on_creation, submit_for_approval_on_creation, enable_lookup, access_code)	" +
					" values (?,?," +
					" ?,?,?,?, " +
					" ?,?,?, ?) ";
		
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
			
			prepStmt.setString(3, name);
			prepStmt.setString(4, description);
			prepStmt.setString(5, introduction);
			prepStmt.setString(6, defaultOwner);
			
			prepStmt.setString(7, notifyOnCreation);
			prepStmt.setInt(8, submitForApprovalOnCreation);

			prepStmt.setInt(9, enableLookup);
			prepStmt.setString(10, accessCode);
			
			prepStmt.execute();
			
			
			
			// lets get this web form object.
			
			
			
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
	
	
	// update existing webform
	public WebForm ( int webFormId,int folderId, String name, String description, String introduction, String defaultOwner,
			 String notifyOnCreation, int submitForApprovalOnCreation, int enableLookup){
				java.sql.Connection con = null;
				try {
					
					javax.naming.InitialContext context = new InitialContext();
					javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
					con = dataSource.getConnection();

					String sql = " update gr_webforms "
							+ " set folder_id = ? , "
							+ " name = ? , "
							+ " description = ? , "
							+ " introduction = ? , "
							+ " default_owner = ? , "
							+ " notify_on_creation = ? , "
							+ " submit_for_approval_on_creation = ? , "
							+ " enable_lookup = ? "
							+ " where id = ? ";
					
						
				
					PreparedStatement prepStmt = con.prepareStatement(sql);
					prepStmt.setInt(1, folderId);
					
					prepStmt.setString(2, name);
					prepStmt.setString(3, description);
					prepStmt.setString(4, introduction);
					prepStmt.setString(5, defaultOwner);
					
					prepStmt.setString(6, notifyOnCreation);
					prepStmt.setInt(7, submitForApprovalOnCreation);

					prepStmt.setInt(8, enableLookup);
					prepStmt.setInt(9, webFormId);
					
					prepStmt.execute();
					
					
					
					// lets get this web form object.
					
					
					
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
			
	

	public WebForm ( int webFormId){
				java.sql.Connection con = null;
				try {
					
					javax.naming.InitialContext context = new InitialContext();
					javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
					con = dataSource.getConnection();

					String sql = " select id, project_id, folder_id, " +
							" name, description, introduction, default_owner, " +
							" notify_on_creation, submit_for_approval_on_creation, enable_lookup, access_code " +
							" from gr_webforms " +
							" where id = ?  ";
				
					PreparedStatement prepStmt = con.prepareStatement(sql);
					prepStmt.setInt(1, webFormId);
					ResultSet rs = prepStmt.executeQuery();
					
					while (rs.next()){
						// we already have project Id
						
						int id = rs.getInt("id");
						int projectId = rs.getInt("project_id");
						int folderId = rs.getInt("folder_id");
						String name = rs.getString("name");
						String description = rs.getString("description");
						String introduction = rs.getString("introduction");
						String defaultOwner = rs.getString("default_owner");
						String notifyOnCreation = rs.getString("notify_on_creation");
						int submitForApprovalOnCreation  = rs.getInt("submit_for_approval_on_creation");

						int enableLookup  = rs.getInt("enable_lookup");
						String accessCode = rs.getString("access_code");
						
						
						this.id = id;
						this.projectId = projectId;
						this.folderId = folderId;
						this.name = name;
						this.description = description;
						this.introduction = introduction;
						this.defaultOwner = defaultOwner;
						this.notifyOnCreation = notifyOnCreation;
						this.submitForApprovalOnCreation = submitForApprovalOnCreation;
						this.enableLookup = enableLookup;
						this.accessCode = accessCode;
						
						
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
			
	public WebForm ( int folderId, String accessCode){
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " select id, project_id, folder_id, " +
					" name, description, introduction, default_owner, " +
					" notify_on_creation, submit_for_approval_on_creation, access_code " +
					" from gr_webforms " +
					" where folder_id = ?  and access_code =  ?  ";
		
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId);
			prepStmt.setString(2, accessCode);
			
			ResultSet rs = prepStmt.executeQuery();
			
			while (rs.next()){
				// we already have project Id
				
				int id = rs.getInt("id");
				int projectId = rs.getInt("project_id");
				String name = rs.getString("name");
				String description = rs.getString("description");
				String introduction = rs.getString("introduction");
				String defaultOwner = rs.getString("default_owner");
				String notifyOnCreation = rs.getString("notify_on_creation");
				int submitForApprovalOnCreation  = rs.getInt("submit_for_approval_on_creation");
				
				
				this.id = id;
				this.projectId = projectId;
				this.folderId = folderId;
				this.name = name;
				this.description = description;
				this.introduction = introduction;
				this.defaultOwner = defaultOwner;
				this.notifyOnCreation = notifyOnCreation;
				this.submitForApprovalOnCreation = submitForApprovalOnCreation;
				this.accessCode = accessCode;
				
				
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
	

	public int getId(){
		return this.id;
	}
	
	public int getProjectId(){
		return this.projectId;
	}
	
	public int getFolderId(){
		return this.folderId;
	}

	public String getName(){
		return this.name;
	}
	
	
	public String getDescription() {
		return this.description ;
	}
	
	
	
	public String getIntroduction() {
		return this.introduction;
	}
	
	
	public String getDefaultOwner() {
		return this.defaultOwner ;
	}
	
	public String getNotifyOnCreation() {
		return this.notifyOnCreation ;
	}
	public int getSubmitForApprovalOnCreation() {
		return this.submitForApprovalOnCreation ;
	}	
	
	public int getEnableLookup() {
		return this.enableLookup ;
	}
	public String getAccessCode() {
		return this.accessCode;
	}
	
	public String getAccessURL(HttpServletRequest request){
		String serverName = request.getServerName();
		String accessURL = "https://" + serverName +  
   				"/GloreeJava2/jsp/Requirement/createWebFormRequirement.jsp?webFormId=" + this.id + "&accessCode=" +	this.accessCode;
		
		return accessURL;
	}
		
}
