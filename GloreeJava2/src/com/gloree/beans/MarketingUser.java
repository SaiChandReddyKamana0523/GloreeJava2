package com.gloree.beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;

import com.gloree.utils.RequirementUtil;

public class MarketingUser {

	private int id ;
	private int userId ;
	private String hello = "";
	private String needHelp = "";
	private String whitePaper = "";
	private String whyTraceCloud = "";

	private String invitee = "";
	private String referral = "";
	
/*	public Marketing (int id, int requirementId, int version, String commenterEmailId,
		String comment_note, String commentDate) {
		this.id = id;
		this.requirementId = requirementId;
		this.version = version;
		this.commenterEmailId = commenterEmailId;
		this.comment_note = comment_note;
		this.commentDate = commentDate;
	}
*/
	public MarketingUser (int userId) {

		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// first see if a row exists
			String sql = "";
				sql = " SELECT id, user_id, hello, need_help, white_paper, why_tracecloud, invitee, referral "
						+ " from gr_marketing "
						+ " where user_id = ? ";
				PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, userId);
			ResultSet rs = prepStmt.executeQuery();
			

			if (rs.next()){
		
				this.id = rs.getInt("id");
				this.userId = userId;
				this.hello = rs.getString("hello");;
				this.needHelp = rs.getString("need_help");
				this.whitePaper = rs.getString("white_paper");
				this.whyTraceCloud = rs.getString("why_tracecloud");
				this.invitee = rs.getString("invitee");
				this.referral = rs.getString("referral");
				
			}

			prepStmt.close();
			rs.close();
			// if this doesn't exist, lets create a new entry in db.
			
			if (this.id == 0){
				// we need make the entry in db
				sql = " insert into gr_marketing (user_id) values (?)";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, userId);
				prepStmt.execute();
				prepStmt.close();
				
				// first see if a row exists
				
				sql = " SELECT id, user_id, hello, need_help, white_paper, why_tracecloud, invitee, referral "
						+ " from gr_marketing "
						+ " where user_id = ? ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, userId);
				rs = prepStmt.executeQuery();
				

				if (rs.next()){
			
					this.id = rs.getInt("id");
					this.userId = userId;
					this.hello = rs.getString("hello");;
					this.needHelp = rs.getString("need_help");
					this.whitePaper = rs.getString("white_paper");
					this.whyTraceCloud = rs.getString("why_tracecloud");
					this.invitee = rs.getString("invitee");
					this.referral = rs.getString("referral");
					
				}

				prepStmt.close();
				rs.close();
			}
			
			
			con.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		
	}



	
	public int getId (){
		return this.id;
	}
	
	public int getUserId(){
		return this.userId;
	}
	
	public String getHello(){
		return this.hello;
	}
	
	public String getNeedHelp(){
		return this.needHelp;
	}
	
	public String getWhitePaper(){
		return this.whitePaper;
	}
	
	
	public String getWhyTraceCloud(){
		return this.whyTraceCloud;
	}
	public String getInvitee(){
		return this.invitee;
	}
	public String getReferral(){
		return this.referral;
	}
	
	


	public void setHello(String value){
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
				sql = " update gr_marketing set hello = ?  "
						+ " where user_id = ? ";
				PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, value);
			prepStmt.setInt(2, this.userId);
			prepStmt.execute();
			
			prepStmt.close();
			con.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
				
	}
	
	public void setNeedHelp(String value){
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
				sql = " update gr_marketing set need_help = ?  "
						+ " where user_id = ? ";
				PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, value);
			prepStmt.setInt(2, this.userId);
			prepStmt.execute();
			
			prepStmt.close();
			con.close();
			
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
