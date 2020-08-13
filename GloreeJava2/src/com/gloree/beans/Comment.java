package com.gloree.beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;

import com.gloree.utils.RequirementUtil;

public class Comment {

	private int id ;
	private int requirementId ;
	private int version ;
	private String commenterEmailId = "";
	private String comment_note = "";
	private String commentDate = "";
	
	
	public Comment (int id, int requirementId, int version, String commenterEmailId,
		String comment_note, String commentDate) {
		this.id = id;
		this.requirementId = requirementId;
		this.version = version;
		this.commenterEmailId = commenterEmailId;
		this.comment_note = comment_note;
		this.commentDate = commentDate;
	}
	
	public Comment (int commentId) {

		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
				sql = " SELECT id, requirement_id, version, commenter_email_id, comment_note, comment_dt "
						+ " from gr_requirement_comments "
						+ " where id = ? ";
				PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, commentId);
			ResultSet rs = prepStmt.executeQuery();
			
			if (rs.next()){
		
				this.id = rs.getInt("id");
				this.requirementId = rs.getInt("requirement_id");
				this.version = rs.getInt("version");;
				this.commenterEmailId = rs.getString("commenter_email_id");
				this.comment_note = rs.getString("comment_note");
				this.commentDate = rs.getString("comment_dt");
			}
			
			prepStmt.close();
			rs.close();
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


	public void deleteComment(){
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
				sql = "delete from gr_requirement_comments "
						+ " where id = ? ";
				PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.id);
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
	
	
	public int getId (){
		return this.id;
	}
	
	public int getRequirementId(){
		return this.requirementId;
	}
	
	public int getVersion(){
		return this.version;
	}
	
	public String getCommenterEmailId(){
		return this.commenterEmailId;
	}
	
	public String getComment_note(){
		return this.comment_note;
	}
	
	public String getHTMLFriendlyCommentNote(){
		String HTMLFriendlyCommentNote = this.comment_note;
		HTMLFriendlyCommentNote = HTMLFriendlyCommentNote.replace("\n", "<br>");
		return HTMLFriendlyCommentNote;
		
	}
	
	
	public String getCommentDate(){
		return this.commentDate;
	}
	
}
