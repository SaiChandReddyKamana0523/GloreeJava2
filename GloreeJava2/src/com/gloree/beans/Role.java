package com.gloree.beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;

import com.gloree.utils.ProjectUtil;

//GloreeJava2


// This class is used to store an object of Requirement Type.

public class Role {

	/**
	 * 
	 */
	private int roleId;
	private int projectId;
	private String roleName;
	private String roleDescription;
	private String approvalType;
	private int approvalRank;
	
	// The following method is called when the project core values are known and the system is only
	// interested in them. 
	public Role (int roleId, int projectId, String roleName, String roleDescription){
		this.roleId = roleId;
		this.projectId = projectId;
		this.roleName = roleName;
		this.roleDescription = roleDescription;
	}
	
	public Role (int roleId, int projectId, String roleName, String roleDescription, String approvalType, int approvalRank){
		this.roleId = roleId;
		this.projectId = projectId;
		this.roleName = roleName;
		this.roleDescription = roleDescription;
		this.approvalType = approvalType;
		this.approvalRank = approvalRank;
	}
	// this constructor creates the user role in the db, gets the roleId and creates the bean 
	// with those values.
	// after the role is created, we need to set the default role privs for this new role.
	// all roles get Read permissions. and no other permissions by default.
	 public Role(int projectId, String roleName, String roleDescription, String createdByEmailId, String databaseType){
		// Create a user role
		 java.sql.Connection con = null; 
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

				
			String sql = " insert into gr_roles (project_id, name,description)" +
				" values (?,?,?)  ";
						
			PreparedStatement prepStmt = con.prepareStatement(sql);

			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, roleName);
			prepStmt.setString(3, roleDescription);				
			prepStmt.execute();
			prepStmt.close();

			// now lets get the role id, by searching on projectId and role name (should be unique)
			sql = "select id from gr_roles where project_id = ? and name = ? ";
			 prepStmt = con.prepareStatement(sql);

			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, roleName);
			ResultSet rs = prepStmt.executeQuery();
			
			int roleId = 0;
			while (rs.next()) {
				roleId = rs.getInt("id");
			}
			
			// Now lets create the bean.
			this.roleId = roleId;
			this.projectId = projectId;
			this.roleName = roleName;
			this.roleDescription = roleDescription;

			// Now lets set the default privileges for this role. 
			sql = "insert into gr_role_privs (" +
			" role_id, folder_id, create_requirement, read_requirement, update_requirement," + 
			" delete_requirement, trace_requirement,approve_requirement) " +
			" select ? , id , 0, 1, 0,  0, 0, 0" + 
			" from gr_folders " +
			" where project_id = ?";
				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.roleId);
			prepStmt.setInt(2, this.projectId);
			
			prepStmt.execute();
			 
			
			// prior to exiting, we need to make a log entry in the project table.
			ProjectUtil.createProjectLog(projectId, roleName, "Create", 
				"Default Role Created as part of Project Creation", createdByEmailId,  databaseType);
			con.close();
			}
			catch (Exception e) {
				// 	TODO Auto-generated catch block
				e.printStackTrace();
			}  finally {
				if (con != null) {
					try {con.close();} catch (Exception e) {}
					con = null;
				}
			}
		}
	 

	
	// the following method is used when the system knows only the RoleId and wants this bean
	// to go and get details i.e. attributes etc..
	public Role (int roleId) {
	 
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = "select  project_id, name, description, approval_type, approval_rank " + 
			" from gr_roles " + 
			" where id = ?";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, roleId);
			ResultSet rs = prepStmt.executeQuery();
			
			if (rs.next()){
				this.roleId = roleId;
				this.projectId = rs.getShort("project_id");
				this.roleName = rs.getString("name");
				this.roleDescription = rs.getString("description");
				this.approvalType = rs.getString("approval_type");
				this.approvalRank = rs.getInt("approval_rank");
				
			}
			// TODO expand this to return the attributes and
			
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

	// creates the role bean when a project id and role name is sent in.
	public Role (int projectId, String roleName) {
		 
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Note : project_id, name uniquely identify a role.
			String sql = "select  id, project_id, name, description " + 
			" from gr_roles " + 
			" where project_id = ? and name = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, roleName);
			ResultSet rs = prepStmt.executeQuery();
			
			if (rs.next()){
				this.roleId = rs.getInt("id");
				this.projectId = rs.getShort("project_id");
				this.roleName = rs.getString("name");
				this.roleDescription = rs.getString("description");
			}
			// TODO expand this to return the attributes and
			
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

	
	public int getRoleId(){
		return this.roleId;
	}
	
	public int getProjectId(){
		return this.projectId;
	}
	
	
	public String getRoleName(){
		return this.roleName;
	}
	
	
	public String getRoleDescription () {
		return this.roleDescription;
	}
	
	

	public String getApprovalType(){
		String approvalType = "";
		if ((this.approvalType != null) && !(this.approvalType.equals(""))){
			approvalType = this.approvalType;
		}
		else {
			java.sql.Connection con = null;
			try {
				
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();
	
				// Note : project_id, name uniquely identify a role.
				String sql = "select approval_type from  gr_roles " + 
				" where id = ? ";
				
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.setInt (1, this.roleId);
				ResultSet rs = prepStmt.executeQuery();
				while (rs.next()){
					approvalType = rs.getString("approval_type");
				}
				
	
				rs.close();
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
		
		return (approvalType);
		
	}

	
	public int getApprovalRank(){
		int approvalRank = 0;
		if (this.approvalRank != 0){
			approvalRank = this.approvalRank;
		}
		else {
			java.sql.Connection con = null;
			try {
				
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();
	
				// Note : project_id, name uniquely identify a role.
				String sql = "select approval_rank from  gr_roles " + " where id = ? ";
				
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.setInt (1, this.roleId);
				ResultSet rs = prepStmt.executeQuery();
				while (rs.next()){
					approvalRank = rs.getInt("approval_rank");
				}
				
	
				rs.close();
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
		
		return (approvalRank);
		
	}

	
	public void setRoleName (String roleName , String actorEmailId, String databaseType) {
		 
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Note : project_id, name uniquely identify a role.
			String sql = "update  gr_roles set name = ? " + 
			" where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, roleName);
			prepStmt.setInt (2, this.roleId);
			prepStmt.execute();
			
			prepStmt.close();
			con.close();
			

			ProjectUtil.createProjectLog(projectId, this.roleName , "Update Role Name", 
					"New Name is " + roleName , actorEmailId, databaseType);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
	}

	
	public void setRoleApprovalTypeAndRank (String approvalType , int approvalRank, String actorEmailId, String databaseType) {
		 
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Note : project_id, name uniquely identify a role.
			String sql = "update  gr_roles set approval_type = ? , approval_rank = ? " + 
			" where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, approvalType);
			prepStmt.setInt(2, approvalRank);
			prepStmt.setInt (3, this.roleId);
			prepStmt.execute();
			
			prepStmt.close();
			con.close();
			

			ProjectUtil.createProjectLog(projectId, this.roleName , "Update ApprovalType", 
					"New type is " + approvalType + " approval rank is " + approvalRank , actorEmailId, databaseType);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
	}
	public void setRoleDescription (String roleDescription, String actorEmailId, String databaseType) {
		 
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Note : project_id, name uniquely identify a role.
			String sql = "update  gr_roles set description = ? " + 
			" where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, roleDescription);
			prepStmt.setInt (2, this.roleId);
			prepStmt.execute();
			
			prepStmt.close();
			con.close();
			
			ProjectUtil.createProjectLog(projectId, this.roleName , "Update Role Description", 
					"New Description is " + roleDescription , actorEmailId, databaseType);
			
			
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
