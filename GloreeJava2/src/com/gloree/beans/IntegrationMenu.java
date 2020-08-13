package com.gloree.beans;

import java.sql.PreparedStatement;

import javax.naming.InitialContext;

public class IntegrationMenu {

	private int id ;
	private int projectId ;
	private String menuType;
	private String menuLabel;
	private String menuValue;
	
	
	public IntegrationMenu (int id, int projectId, String menuType,
		String menuLabel , String menuValue) {
		this.id = id;
		this.projectId = projectId;
		this.menuType = menuType;
		this.menuLabel = menuLabel;
		this.menuValue = menuValue;
	}

	public IntegrationMenu (int projectId, String menuType,
			String menuLabel , String menuValue) {
		java.sql.Connection con =  null;
		
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			
			String sql = " insert into gr_project_integration_menu (project_id, menu_type, menu_label, menu_value)" +
				" values (?,?,?,? ) ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2,menuType);
			prepStmt.setString(3,menuLabel);
			prepStmt.setString(4,menuValue);
			
			prepStmt.execute();
			
			prepStmt.close();
			con.close();
			this.projectId = projectId;
			this.menuType = menuType;
			this.menuLabel = menuLabel;
			this.menuValue = menuValue;
			
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}

	
	
	}
		
	public int getId (){
		return this.id;
	}
	
	public int getProjectId(){
		return this.projectId;
	}
	

	public String getMenuType(){
		return this.menuType;
	}
	
	public String getMenuLabel(){
		return this.menuLabel;
	}
	
	public String getMenuValue(){
		return this.menuValue;
	}
	
}
