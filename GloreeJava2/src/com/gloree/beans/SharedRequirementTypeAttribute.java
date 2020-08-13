package com.gloree.beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.naming.InitialContext;

import com.gloree.utils.ProjectUtil;

public class SharedRequirementTypeAttribute {

	private int sRAId;
	private int sRAFilterable ;
	private int sRACopyable ;
	private int sRADisplayable;
	private int srAEditable;
	private RTAttribute rTAttribute;
	
	
	public SharedRequirementTypeAttribute (int sRAId, int sRAFilterable,int sRACopyable , 
		int sRADisplayable, int sRAEditable, RTAttribute rTAttribute){
		this.sRAId = sRAId;
		this.sRAFilterable = sRAFilterable;
		this.sRACopyable = sRACopyable;
		this.sRADisplayable = sRADisplayable;
		this.srAEditable = sRAEditable;
		this.rTAttribute = rTAttribute;
	}
	
	public SharedRequirementTypeAttribute (int sharedRequirementTypeAttributeId) {

		java.sql.Connection con = null;

		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			this.rTAttribute = new RTAttribute(sharedRequirementTypeAttributeId);
			this.sRAId = sharedRequirementTypeAttributeId;
			
			String sql = "select sr_filterable, sr_copyable , sr_displayable, sr_editable " +
				" from gr_rt_attributes " +
				" where id  = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sharedRequirementTypeAttributeId);
			ResultSet rs = prepStmt.executeQuery();


			if (rs.next()){
				this.sRACopyable = rs.getInt("sr_copyable");
				this.sRAFilterable = rs.getInt("sr_filterable");
				this.sRADisplayable = rs.getInt("sr_displayable");
				this.srAEditable = rs.getInt("sr_editable");
			}
			
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
	

	public int getSRAId(){
		return this.sRAId;
	}
	public int getSRACopyable (){
		return this.sRACopyable;
	}
	
	public int getSRAFilterable() {
		return this.sRAFilterable;
	}

	public int getSRADisplayable() {
		return this.sRADisplayable;
	}
	
	public int getSRAEDitable(){
		return this.srAEditable;
	}
	
	public RTAttribute getRTAttribute(){
		return this.rTAttribute;
	}

	public void setSRACopyable( int sRCopyable){
		java.sql.Connection con = null;

		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = "update gr_rt_attributes " +
				" set sr_copyable = ? " +
				" where id  = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sRCopyable);
			prepStmt.setInt(2, this.sRAId);
			prepStmt.execute();
			
			prepStmt.close();
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

	
	
	public void setSRAFilterable( int sRFilterable){
		java.sql.Connection con = null;

		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = "update gr_rt_attributes " +
				" set sr_filterable = ? " +
				" where id  = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sRFilterable);
			prepStmt.setInt(2, this.sRAId);
			prepStmt.execute();
			
			prepStmt.close();
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
	
	
	public void setSRADisplayable( int sRDisplayable){
		java.sql.Connection con = null;

		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = "update gr_rt_attributes " +
				" set sr_displayable = ? " +
				" where id  = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sRDisplayable);
			prepStmt.setInt(2, this.sRAId);
			prepStmt.execute();
			
			prepStmt.close();
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

	
	public void setSRAEditable( int sREditable){
		java.sql.Connection con = null;

		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = "update gr_rt_attributes " +
				" set sr_editable = ? " +
				" where id  = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sREditable);
			prepStmt.setInt(2, this.sRAId);
			prepStmt.execute();
			
			prepStmt.close();
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
	
	
}
