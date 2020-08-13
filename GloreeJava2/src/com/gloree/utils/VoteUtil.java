package com.gloree.utils;




import com.gloree.beans.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class VoteUtil {
		

	public static int getTotalVotingRightsForUser(int folderId, int userId ) {
		int totalVotingRights = 0;
		// for each role, get the voting rights for this folder.
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " select sum(voting_rights) 'total_voting_rights' "
					+ " from gr_roles rl, gr_user_roles ur, gr_role_privs rp "
					+ " where rl.id = ur.role_id "
					+ " and ur.user_id = ? "
					+ " and rl.id = rp.role_id "
					+ " and rp.folder_id = ? ;";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, userId);
			prepStmt.setInt(2, folderId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				totalVotingRights = rs.getInt("total_voting_rights");
			}
			prepStmt.close();
			rs.close();
			
			
			
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return (totalVotingRights);
	}

	
	public static int getVotesCast(int folderId, int userId ) {
		int votesCast = 0;
		// for each role, get the voting rights for this folder.
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " select sum(votes_cast) 'votes_cast' "
					+ " from gr_votes v "
					+ " where v.voter_id = ? "
					+ " and v.folder_id = ? ;";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, userId);
			prepStmt.setInt(2, folderId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				votesCast = rs.getInt("votes_cast");
			}
			prepStmt.close();
			rs.close();
			
			
			
			
			con.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return (votesCast);
	}

	
	public static ArrayList<Vote> getOtherVotes(int requirementId ) {
		ArrayList<Vote> votes = new ArrayList<Vote>();
		// for each role, get the voting rights for this folder.
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " select id, voter_email_id, requirement_id, folder_id, votes_cast, vote_date "
					+ " from gr_votes v "
					+ " where v.requirement_id = ? "
					+ " order by id desc ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();
			
			
			while (rs.next()){
				int id = rs.getInt("id");
				String voterEmailId = rs.getString("voter_email_id");
				int folderId = rs.getInt("folder_id");
				int votesCast = rs.getInt("votes_cast");
				String voteDate = rs.getString("vote_date");
				
				
				Vote vote = new Vote(id ,   voterEmailId,  requirementId,  folderId,  votesCast,  voteDate);
				votes.add(vote);
			}
			prepStmt.close();
			rs.close();
			
			
			
			
			con.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return (votes);
	}
	
	

	public static ArrayList<HashMap<String,String>> getVotedRequirements(Folder folder ) {
		ArrayList<HashMap<String, String>> votedRequirements = new ArrayList<HashMap<String,String>>();
		// for each role, get the voting rights for this folder.
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the attribute id of 'total votes cast' for this requirement type
			
			String sql = " select rta.id "
					+ " from gr_rt_attributes rta "
					+ " where rta.requirement_type_id = ? "
					+ " and lower(name) = 'total votes cast'";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folder.getRequirementTypeId());
			rs = prepStmt.executeQuery();
			
			int attributeId = 0;
			while (rs.next()){
				attributeId = rs.getInt("id");
			}
			prepStmt.close();
			rs.close();

			// now lets get the requirements sorted by this attribute value
			
			sql = "select r.full_tag, r.id, r.name, (rav.value*1) vote " + 
			" from gr_requirements r, gr_r_attribute_values rav  " +
			" where rav.requirement_id = r.id "+
			" and r.folder_id = ?  "+
			" and rav.attribute_id = ? " + 
			" order by (rav.value*1) desc ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folder.getFolderId());
			prepStmt.setInt(2, attributeId);
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				String fullTag = rs.getString("full_tag");
				int requirementId = rs.getInt("id");
				String name = rs.getString("name");
				int vote = rs.getInt("vote");
				HashMap<String, String> voteReq = new HashMap<String, String>();
				voteReq.put("fullTag", fullTag);
				voteReq.put("requirementId", Integer.toString(requirementId));
				voteReq.put("vote", Integer.toString(vote));
				voteReq.put("name", name);
				
				votedRequirements.add(voteReq);
			}
			prepStmt.close();
			rs.close();
		
			
			
			con.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return (votedRequirements);
	}
	

	
	
	public static int getVotesCastForARequirement(int requirementId ) {
		int votesCast = 0;
		// for each role, get the voting rights for this folder.
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " select sum(votes_cast) 'votes_cast' "
					+ " from gr_votes v "
					+ " where v.requirement_id = ? ;";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				votesCast = rs.getInt("votes_cast");
			}
			prepStmt.close();
			rs.close();
			
			
			
			
			con.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return (votesCast);
	}
	

	public static int getVotesCastForARequirementByAUser(int requirementId, int userId ) {
		int votesCast = 0;
		// for each role, get the voting rights for this folder.
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " select sum(votes_cast) 'votes_cast' "
					+ " from gr_votes v "
					+ " where v.requirement_id = ? "
					+ " and v.voter_id = ?  ;";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.setInt(2, userId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				votesCast = rs.getInt("votes_cast");
			}
			prepStmt.close();
			rs.close();
			
			
			
			
			con.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return (votesCast);
	}
	
	public static HashMap<Integer, Integer> getVotesCastByAUserInAFolder(int userId, int folderId ) {
		HashMap<Integer, Integer> votesCastInAFolder =  new HashMap<Integer, Integer>();
		// for each role, get the voting rights for this folder.
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = " select requirement_id, votes_cast "
					+ " from gr_votes v "
					+ " where v.voter_id = ? "
					+ " and v.folder_id = ? ;";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, userId);
			prepStmt.setInt(2, folderId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int requirementId = rs.getInt("requirement_id");
				int votesCast = rs.getInt("votes_cast");
				votesCastInAFolder.put(requirementId, votesCast);
			}
			prepStmt.close();
			rs.close();
			
			con.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return (votesCastInAFolder);
	}


	public static void castVote(User user, Requirement requirement, int vote ) {
		
		
		// delete any vote for this user / req combinition
		// insert vote for this user, req combination
		// Add a log to requirement about the user's vote.
		// call refresh requirement Count Votes function
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// delete old vote
			String sql = " delete from gr_votes "
					+ " where requirement_id = ? "
					+ " and voter_email_id = ?  ;";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirement.getRequirementId());
			prepStmt.setString(2, user.getEmailId());
			prepStmt.execute();
			
			prepStmt.close();
			
			// insert new vote
			sql = " insert into gr_votes(voter_id, voter_email_id, requirement_id, folder_id, votes_cast, vote_date, message )"
					+ " values (?, ?, ?, ?, ?, now(), ?) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,user.getUserId());
			prepStmt.setString(2, user.getEmailId());
			prepStmt.setInt(3, requirement.getRequirementId());
			prepStmt.setInt(4, requirement.getFolderId());
			prepStmt.setInt(5, vote);
			prepStmt.setString(6, "");
			prepStmt.execute();
			
			prepStmt.close();
			
			// total votes cast for this req
			int totalVotesCastForThisReq = VoteUtil.getVotesCastForARequirement(requirement.getRequirementId());
			
			
			// attribute value. we may need a different way to implement this.
		   RTAttribute rTAttribute = new RTAttribute(requirement.getRequirementTypeId(), "Total Votes Cast" );
		    
		   System.out.println("srt attribute is " + rTAttribute.getAttributeName());
		    RAttributeValue a = new RAttributeValue(requirement.getRequirementId(), rTAttribute.getAttributeId(), "mySQL");
			RAttributeValue rAV  = new RAttributeValue(a.getAttributeValueId(), totalVotesCastForThisReq + "", "mySQL", user.getEmailId());
			
			requirement.setUserDefinedAttributes(con, "mySQL");
			String log = user.getFirstName() + " " + user.getLastName() + 
					 " voted  " + vote  +" votes to this object";
			RequirementUtil.createRequirementLog(requirement.getRequirementId(), log, user.getEmailId(), "mySQL");
			
			
			
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return ;
	}

}
