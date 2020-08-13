package com.gloree.utils;

//GloreeJava2

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;

import com.gloree.beans.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;

public class DT_Utils {
	//
	// Returns all tasks created by the currently logged in user.
	 public static ArrayList getTasks(SecurityProfile securityProfile, String createdBy, String owner, String priority, 
			 int completion, String tags, String work_status, String stakeholders,
			 String sort_by){
		ArrayList tasks = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String caller = securityProfile.getUser().getEmailId();
			
			String sql = "";
			
			
			// lets put a safety clause that the user sees only stuff where at least one of these three conditions are met
			// 1. user is a owner
			// 2. user is a creator
			// 3. user is a stake holder
			// this makes sure that despite the filter, the user gets to see only the stuff he / she has visibility to
			
							
				
			sql  = "  select "
					+ "	id, title, description, created_by,  "
					+ " created_on, owner, completion, priority, "
					+ " tags, completion_required_date, stakeholders, work_status "
					+ " from dt_tasks "
					+ " where ( created_by  = ? or owner = ? or stakeholders like '%" + caller + "%' ) ";
			
			
			if (!(createdBy.equals(""))){
				sql += " and created_by = '"+ createdBy +"' ";
			}
			

			if (!(owner.equals(""))){
				sql += " and owner = '"+ owner +"' ";
			}
			
			if (!(priority.equals(""))){
				sql += " and priority = '"+ priority +"' ";
			}
			 
			if (completion != -1){
				sql += " and completion = " + completion + " ";
			}
			
			if (!(tags.equals(""))){
				sql += " and tags = '"+ tags +"' ";
			}
			
			if (!(work_status.equals(""))){
				sql += " and work_status = '"+ work_status +"' ";
			}
			 
			
			if (!(stakeholders.equals(""))){
				sql += " and stakeholders like '%"+ stakeholders +"%' ";
			}
			
			
			
			
			
			// make the sort by the last thing you add to the sql

			if (!(sort_by.equals(""))){
				sql += "  order by   "+ sort_by +" ";
			}
			
			
			System.out.println("srt getTasks sql " + sql );
			
			
			prepStmt = con.prepareStatement(sql);

			prepStmt.setString(1, caller);
			prepStmt.setString(2, caller);
			rs = prepStmt.executeQuery();

			int id ;
			String title ;
			String description  ;
		
			Date createdOn ;
			Date completionRequiredDt ; 
			String workStatus ;

			
			while (rs.next()) {
				
				id = rs.getInt("id");
				title = rs.getString("title");
				description  = rs.getString("description");
				createdBy = rs.getString("created_by"); 
			
				createdOn = rs.getDate("created_on");
				owner = rs.getString("owner");
				completion = rs.getInt("completion"); 
				priority = rs.getString("priority");
				
				tags = rs.getString("tags"); 
				completionRequiredDt = rs.getDate("completion_required_date"); 
				stakeholders = rs.getString("stakeholders"); 
				workStatus = rs.getString("work_status");
				
				
				
				DT_Task task = new DT_Task(id, title, description , createdBy, 
						createdOn, owner, completion, priority,
						tags, completionRequiredDt, stakeholders, workStatus
					) ;
				
				tasks.add(task);
				
			}
			
			rs.close();
			prepStmt.close();
			con.close();
		} catch (Exception e) {
				
				e.printStackTrace();
		}   finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}

		return tasks;
	}
	 
	 

	
	 public static ArrayList getValues(SecurityProfile securityProfile, String type) {
		ArrayList values = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String caller = securityProfile.getUser().getEmailId();
			
			String sql = "";
			
			
							
			if (type.equals("tags")){
				sql  = "  select "
					+ "	distinct  upper(tags) 'tags'  "
					+ " from dt_tasks "
					+ " where ( created_by  = ? or owner = ? or stakeholders like '%" + caller + "%' ) "
					+ " and completion < 100 order by 1 ";
			}

			
			if (type.equals("owner")){
			sql  = "  select "
				+ "	distinct  owner  "
				+ " from dt_tasks "
				+ " where ( created_by  = ? or owner = ? or stakeholders like '%" + caller + "%' ) "
				+ " and completion < 100  order by 1 ";
			}

			if (type.equals("created_by")){
			sql  = "  select "
				+ "	distinct  created_by  "
				+ " from dt_tasks "
				+ " where ( created_by  = ? or owner = ? or stakeholders like '%" + caller + "%' ) "
				+ "  and completion < 100 order by 1 ";
			}

			prepStmt = con.prepareStatement(sql);

			prepStmt.setString(1, caller);
			prepStmt.setString(2, caller);
			rs = prepStmt.executeQuery();

			
			while (rs.next()) {
			
				if (type.equals("tags")){
					String tags = rs.getString("tags"); 
					values.add(tags);
				}
				
				if (type.equals("owner")){
					String owner = rs.getString("owner"); 
					values.add(owner);
				}

				if (type.equals("created_by")){
					String created_by = rs.getString("created_by"); 
					values.add(created_by);
				}
				
			}
			
			rs.close();
			prepStmt.close();
			con.close();
		} catch (Exception e) {
				
				e.printStackTrace();
		}   finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}

		return values;
	}
	 
	
	public static void update_DT_Task (int taskId, String taskTitle,  String taskDescription, String createdBy, String owner, 
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
			sql = " update dt_tasks set"
					+ " title = ? , "
					+ " description = ? , "
					+ " owner = ? , "
					+ " completion = ? , "
					+ " priority = ? , "
					+ " tags = ? , "
					+ " completion_required_date = ? , "
					+ " stakeholders = ?, "
					+ " work_status = ? "
					+ " where id = ? ";
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, taskTitle);
			prepStmt.setString(2, taskDescription);
			
			
			prepStmt.setString(3, owner);
			prepStmt.setInt(4, completion);
			prepStmt.setString(5, priority);
			
			prepStmt.setString(6, tags);
			prepStmt.setDate(7, completionRequiredDt);
			prepStmt.setString(8, stakeholders);
			prepStmt.setString(9, workStatus);
			
			prepStmt.setInt(10, taskId);
			
			
			
			prepStmt.execute();
		
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		
	}

	
}
