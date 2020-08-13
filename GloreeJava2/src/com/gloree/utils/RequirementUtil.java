package com.gloree.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;

//GloreeJava2






import org.apache.commons.lang.StringEscapeUtils;

import com.gloree.beans.Comment;
import com.gloree.beans.Folder;
import com.gloree.beans.MessagePacket;
import com.gloree.beans.Project;
import com.gloree.beans.ProjectRelation;
import com.gloree.beans.RAttributeValue;
import com.gloree.beans.RTAttribute;
import com.gloree.beans.RTBaseline;
import com.gloree.beans.Requirement;
import com.gloree.beans.RequirementAttachment;
import com.gloree.beans.RequirementBaseline;
import com.gloree.beans.RequirementType;
import com.gloree.beans.RequirementVersion;
import com.gloree.beans.Role;
import com.gloree.beans.SecurityProfile;
import com.gloree.beans.Trace;
import com.gloree.beans.TraceTreeRow;
import com.gloree.beans.User;

public class RequirementUtil {
	//
	// This class is used to run static queries to get
	// 1. list of TraceTo requirements
	// 2. list of TraceFrom requirements.
	// 3. Delete a requirement in the db.
	//
	
	
	public static String getNextTag(String parentFullTag , int projectId, String databaseType) {
		String nextTag = "";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			// parentfulltag is like BR-13, parent tag is 13
			String parentTag = parentFullTag.substring(parentFullTag.indexOf("-")+1);
			// note we use the multiply by 1 trick to convert a text into a number
			String sql = "";
			sql = "select count(*) \"children\" " +
				" from gr_requirements " + 
				" where project_id = ? " +
				" and lower(parent_full_tag) = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, parentFullTag.trim().toLowerCase());
			rs = prepStmt.executeQuery();
			int children =0;
			while (rs.next()){
				children = rs.getInt("children");
			}
			prepStmt.close();
			rs.close();
			
			
			
			
			if (children == 0){
				nextTag = parentTag + ".1";

			}
			else{	
				
				sql = "select concat('" + parentTag + "', '.',max(replace(full_tag, concat(parent_full_tag,'.'),'')*1)+1) \"nextTag\" " +
					" from gr_requirements " + 
					" where project_id = ? " +
					" and lower(parent_full_tag) = ? ";
			
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.setString(2, parentFullTag.trim().toLowerCase());
				rs = prepStmt.executeQuery();
				while (rs.next()){
					nextTag = rs.getString("nextTag");
				}
				
				if ((nextTag == null) || (nextTag.equals(""))){
					nextTag = parentTag + ".1";
				}
				
			}
			prepStmt.close();
			rs.close();
			con.close();
			return nextTag;

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
		return nextTag;
	}

	
	
	public static String getRequirementName(String fullTag, int projectId) {
		
		String name = "";
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " select name " +
					" from gr_requirements " +
					" where  project_id = ? and full_tag = ?  ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, fullTag);
			rs = prepStmt.executeQuery();			
			while (rs.next()) {
				name = rs.getString("name");
				
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
		return name;
	}
	
	public static String getAPreviousVersion(int requirementId, int version ) {
		
		String prevVersion = "";
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " select name, description, user_defined_attributes " +
					" from gr_requirement_versions " +
					" where requirement_id = ? " +
					" and version = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.setInt(2, version);
			rs = prepStmt.executeQuery();			
			while (rs.next()) {
				String prevVersionName = rs.getString("name");
				String prevVersionDescription = rs.getString("description");
				String prevVersionUDA = rs.getString("user_defined_attributes");
				
				prevVersion = prevVersionName + ":##X##:" + prevVersionDescription + ":##X##:" + prevVersionUDA;
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
		return prevVersion;
	}
	
	
	
	public static ArrayList getTraceTo(int requirementId) {

		ArrayList traceToRequirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the list of requirments this req traces to . It
			// then creates concatenated string, pipe delimted,
			// stores them in an Arraylist
			// This array list is then returned to the caller.
			// TODO : change this SQL so that we return ONLY the project to
			// which the user has access.
			//
			String sql = "select t.id \"trace_id\", r.id \"trace_to_id\", r.full_tag \"trace_to_full_tag\"," +
					" t.suspect , r.name \"requirement_name\", t.description \"traceDescription\" , t.reason \"traceReason\", p.short_name "
					+ " from gr_traces t , gr_requirements r, gr_projects p "
					+ " where t.to_requirement_id = r.id "
					+ " and t.from_requirement_id = ? " +
					" and r.project_id = p.id "
					+ " order by r.id ";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();

			int traceId = 0;
			int traceToId = 0;
			String traceToFullTag = "";
			int suspect = 0;
			String requirementName = "";
			String traceDescription = "";
			String projectShortName = "";
			String traceReason = "";
			
			while (rs.next()) {
				traceId = rs.getInt("trace_id");
				traceToId = rs.getInt("trace_to_id");
				traceToFullTag = rs.getString("trace_to_full_tag");
				suspect = rs.getInt("suspect");
				requirementName = rs.getString("requirement_name");
				traceDescription = rs.getString("traceDescription");
				projectShortName = rs.getString("short_name");
				traceReason = rs.getString("traceReason");
				
				// creating the trace to string.
				traceToRequirements.add(traceId + ":#:" + traceToId + ":#:"
						+ traceToFullTag + ":#:" + suspect + ":#:"
						+ requirementName + ":#:" + traceDescription + ":#:" 
						+ projectShortName + ":#:"  + traceReason );
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

		return traceToRequirements;
	}

	// this is an enhanced version of getTraceTo . Instead of returning a hack like a concatentated string
	// it actually returns any Arraylist of Trace Objects. Bandwidth permitting retire getTraceTo.
	// It returns an array of traces To other requirements from the input parameter requirement
	public static ArrayList getTraceToObjects(int requirementId) {

		ArrayList traceToRequirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the list of requirments this req traces to . It
			// then creates concatenated string, pipe delimted,
			// stores them in an Arraylist
			// This array list is then returned to the caller.
			// TODO : change this SQL so that we return ONLY the project to
			// which the user has access.
			//
			String sql = "select t.id \"trace_id\", toR.id \"trace_to_id\", toR.full_tag \"trace_to_full_tag\"," +
					" t.suspect , toR.name \"requirement_name\", t.description \"traceDescription\" ," +
					" toP.short_name \"to_req_project_short_name\",  " +
					" fromP.short_name \"from_req_project_short_name\" "
					+ " from gr_traces t , gr_requirements toR, gr_projects toP , gr_requirements fromR, gr_projects fromP "
					+ " where t.to_requirement_id = toR.id "
					+ " and t.from_requirement_id = ? " +
					" and t.from_requirement_id = fromR.id" +
					" and fromR.project_id = fromP.id " +
					" and toR.project_id = toP.id "
					+ " order by toR.id";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();

			int traceId = 0;
			int traceToId = 0;
			int suspect = 0;
			String traceDescription = "";
			String toRequirementProjectShortName = "";
			String fromRequirementProjectShortName = "";
			
			while (rs.next()) {
				traceId = rs.getInt("trace_id");
				traceToId = rs.getInt("trace_to_id");
				suspect = rs.getInt("suspect");
				traceDescription = rs.getString("traceDescription");
				toRequirementProjectShortName = rs.getString("to_req_project_short_name");
				fromRequirementProjectShortName = rs.getString("from_req_project_short_name");
				
				Trace trace = new Trace(traceId, traceDescription, traceToId,toRequirementProjectShortName,
					requirementId, fromRequirementProjectShortName, suspect);
				// creating the trace to string.
				traceToRequirements.add(trace);
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

		return traceToRequirements;
	}

	// returns a String object of all the trace to a requirement being sent as a
	// param.
	// called whenever a req objects trace status changes, so that the
	// requirement's
	// TraceTo / Trace From values are changed in the Db.
	public static String getTraceToString(int requirementId) {

		String traceToRequirements = "";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the list of requirments this req traces to . It
			// then creates concatenated string, pipe delimted,
			// stores them in an Arraylist
			// This array list is then returned to the caller.
			// TODO : change this SQL so that we return ONLY the project to
			// which the user has access.
			//
			String sql = "select toR.full_tag \"trace_to_full_tag\", t.suspect ," +
					" toP.id \"to_project_id\", toP.short_name \"to_project_short_name\", " +
					" fromP.id \"from_project_id\", fromP.short_name \"from_project_short_name\" "
					+ " from gr_traces t , gr_requirements toR, gr_requirements fromR , gr_projects toP, gr_projects fromP"
					+ " where t.to_requirement_id = toR.id "
					+ " and t.from_requirement_id = ? " +
					" and t.from_requirement_id = fromR.id" +
					" and toR.project_id = toP.id" +
					" and fromR.project_id = fromP.id  "
					+ " order by toR.id";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();

			String traceToFullTag = "";
			int suspect = 0;

			while (rs.next()) {
				traceToFullTag = rs.getString("trace_to_full_tag");
				suspect = rs.getInt("suspect");

				int toProjectId = rs.getInt("to_project_id");
				String toProjectShortName = rs.getString("to_project_short_name");
				int fromProjectId = rs.getInt("from_project_id");
				String fromProjectShortName = rs.getString("from_project_short_name");
				// creating the trace to string.
				if (suspect == 1) {
					if (toProjectId != fromProjectId){
						// external project. so lets show the project prefix
						traceToRequirements += "(s)" + toProjectShortName + ":" + traceToFullTag + ",";
					}
					else {
						// same project. so no need to show prefix
						traceToRequirements += "(s)" + traceToFullTag + ",";
					}
				} else {
					if (toProjectId != fromProjectId){
						// external project. so lets show the project prefix
						traceToRequirements += toProjectShortName + ":" + traceToFullTag + ",";
					}
					else {
						// same project. so no need to show external prefix.
						traceToRequirements += traceToFullTag + ",";
						
					}
				}

			}
			// Drop the last ','
			if (traceToRequirements.contains(",")) {
				traceToRequirements = (String) traceToRequirements.subSequence(
						0, traceToRequirements.lastIndexOf(","));
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

		return traceToRequirements;
	}

	// this method returns the requirementId ,given a projectId and a
	// requirement full tag.
	// NOTE this does NOT work with prefix:fulltag .
	public static int getRequirementId(int projectId, String requirementTag) {
		int requirementId = 0;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			//
			String sql = "select id from gr_requirements where project_id = ? and UPPER(full_tag) = ?";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, requirementTag.toUpperCase());
			rs = prepStmt.executeQuery();

			while (rs.next()) {
				requirementId = rs.getInt("id");
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

		return requirementId;
	}

	public static ArrayList getTraceFrom(int requirementId) {

		ArrayList traceFromRequirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the list of requirements from which a trace is
			// coming to a given requirement id . It then creates concatenated
			// string, pipe delimited,
			// stores them in an Arraylist
			// This array list is then returned to the caller.
			// TODO : change this SQL so that we return ONLY the project to
			// which the user has access.
			//
			String sql = "select t.id \"trace_id\", r.id \"trace_from_id\", r.full_tag \"trace_from_full_tag\"," +
					" t.suspect, r.name \"requirement_name\", t.description \"traceDescription\", t.reason \"traceReason\" ,  p.short_name  "
					+ " from gr_traces t , gr_requirements r, gr_projects p  "
					+ " where t.from_requirement_id = r.id "
					+ " and t.to_requirement_id = ? " +
					" and r.project_id = p.id " 
					+ " order by r.id";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();

			int traceId = 0;
			int traceFromId = 0;
			String traceFromFullTag = "";
			int suspect = 0;
			String requirementName = "";
			String traceDescription = "";
			String traceReason = "";
			String projectShortName = "";
			
			while (rs.next()) {
				traceId = rs.getInt("trace_id");
				traceFromId = rs.getInt("trace_from_id");
				traceFromFullTag = rs.getString("trace_from_full_tag");
				suspect = rs.getInt("suspect");
				requirementName = rs.getString("requirement_name");
				traceDescription = rs.getString("traceDescription");
				traceReason = rs.getString("traceReason");
				projectShortName = rs.getString("short_name");
				// creating the trace to string.
				traceFromRequirements.add(traceId + ":#:" + traceFromId + ":#:"
						+ traceFromFullTag + ":#:" + suspect + ":#:"
						+ requirementName + ":#:" + traceDescription + ":#:" 
						+ projectShortName  + ":#:" + traceReason);
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

		return traceFromRequirements;
	}


	// this is an enhanced version of getTraceFrom. Instead of returning a hack like a concatentated string
	// it actually returns any Arraylist of Trace Objects. Bandwidth permitting retire getTraceFrom.
	// It returns an array of traces from other requirements from the input parameter requirement
	public static ArrayList getTraceFromObjects(int requirementId) {

		ArrayList traceFromRequirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the list of requirments this req traces to . It
			// then creates concatenated string, pipe delimted,
			// stores them in an Arraylist
			// This array list is then returned to the caller.
			// TODO : change this SQL so that we return ONLY the project to
			// which the user has access.
			//
			//
			// This sql gets the list of requirements from which a trace is
			// coming to a given requirement id . It then creates concatenated
			// string, pipe delimited,
			// stores them in an Arraylist
			// This array list is then returned to the caller.
			// TODO : change this SQL so that we return ONLY the project to
			// which the user has access.
			//
			String sql = "select t.id \"trace_id\", fromR.id \"trace_from_id\", fromR.full_tag \"trace_from_full_tag\"," +
					" t.suspect, fromR.name \"requirement_name\", t.description \"traceDescription\", " +
					" fromP.short_name \"from_req_project_short_name\" , " +
					" toP.short_name \"to_req_project_short_name\" "
					+ " from gr_traces t , gr_requirements fromR, gr_projects fromP , gr_requirements toR, gr_projects toP "
					+ " where t.from_requirement_id = fromR.id "
					+ " and t.to_requirement_id = ? " +
					" and t.to_requirement_id = toR.id" +
					" and toR.project_id = toP.id " +
					" and fromR.project_id = fromP.id " 
					+ " order by fromR.id";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();

			int traceId = 0;
			int traceFromId = 0;
			int suspect = 0;
			String traceDescription = "";
			String toRequirementProjectShortName = "";
			String fromRequirementProjectShortName = "";

			while (rs.next()) {
				traceId = rs.getInt("trace_id");
				traceFromId = rs.getInt("trace_from_id");
				suspect = rs.getInt("suspect");
				traceDescription = rs.getString("traceDescription");
				toRequirementProjectShortName = rs.getString("to_req_project_short_name");
				fromRequirementProjectShortName = rs.getString("from_req_project_short_name");
				Trace trace = new Trace(traceId, traceDescription, requirementId,toRequirementProjectShortName,
					traceFromId, fromRequirementProjectShortName, suspect);
				// creating the trace to string.
				traceFromRequirements.add(trace);
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

		return traceFromRequirements;
	}

	// returns a String object of all the trace from a requirement being sent as
	// a param.
	// called whenever a req objects trace status changes, so that the
	// requirement's
	// TraceTo / Trace From values are changed in the Db.

	public static String getTraceFromString(int requirementId) {

		String traceFromRequirements = "";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the list of requirements from which a trace is
			// coming to a given requirement id . It then creates concatenated
			// string, pipe delimited,
			// stores them in an Arraylist
			// This array list is then returned to the caller.
			// TODO : change this SQL so that we return ONLY the project to
			// which the user has access.
			//
			String sql = "select fromR.full_tag \"trace_from_full_tag\", t.suspect , " +
					" toP.id \"to_project_id\", toP.short_name \"to_project_short_name\", " +
					" fromP.id \"from_project_id\", fromP.short_name \"from_project_short_name\" "
					+ " from gr_traces t , gr_requirements fromR, gr_requirements toR, gr_projects toP, gr_projects fromP "
					+ " where t.from_requirement_id = fromR.id "
					+ " and t.to_requirement_id = ? " +
					" and t.to_requirement_id = toR.id " +
					" and fromR.project_id = fromP.id" +
					" and toR.project_id = toP.id " 
					+ " order by fromR.id";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();

			String traceFromFullTag = "";
			int suspect = 0;

			while (rs.next()) {
				traceFromFullTag = rs.getString("trace_from_full_tag");
				suspect = rs.getInt("suspect");

				int toProjectId = rs.getInt("to_project_id");
				String toProjectShortName = rs.getString("to_project_short_name");
				int fromProjectId = rs.getInt("from_project_id");
				String fromProjectShortName = rs.getString("from_project_short_name");
				
				// creating the trace to string.
				if (suspect == 1) {
					if (toProjectId != fromProjectId){
						// external Project. lets add the prefix
						traceFromRequirements += "(s)" + fromProjectShortName + ":" +traceFromFullTag + ",";
					}
					else{
						// same project. no need to show the prefix
						traceFromRequirements += "(s)" + traceFromFullTag + ",";
					}
				} else {
					if (toProjectId != fromProjectId){
						// external Project. lets add the prefix
						traceFromRequirements += fromProjectShortName + ":" + traceFromFullTag + ",";
					}
					else {
						// same project. no need to show the prefix.
						traceFromRequirements += traceFromFullTag + ",";
					}
				}
			}

			// Drop the last ','
			if (traceFromRequirements.contains(",")) {
				traceFromRequirements = (String) traceFromRequirements
						.subSequence(0, traceFromRequirements.lastIndexOf(","));
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

		return traceFromRequirements;
	}

	public static String validatePotentialTracesUnSecured(String createTraceTo, String createTraceFrom, int projectId, boolean canRead) {

		String status = "";
		try {

			String databaseType = "mySQL";
			Project project = new Project(projectId, databaseType);
			
			// if tracesTo has a space in it drop it.
			if ((createTraceTo != null) && (createTraceTo.contains(" "))) {
				createTraceTo = createTraceTo.replace(" ", "");
			}
			
			// if tracesFrom has a space in it drop it.
			if ((createTraceFrom != null) && (createTraceFrom.contains(" "))) {
				createTraceFrom = createTraceFrom.replace(" ", "");
			}
			
						
			// split Traceto, for each entry there, get the req id, and then
			// create a trace to it.
			if (!(createTraceTo.equals(""))){
				String[] traceTo = createTraceTo.split(",");
				for (int i = 0; i < traceTo.length; i++) {
					try{
						int toRequirementId =  RequirementUtil.getRequirementIdFromTag(project, traceTo[i],  databaseType);
						if (toRequirementId == 0){
							// this means that an  invalid requirement tag was sent in.
							status += "<div class='alert alert-danger'>  " +  traceTo[i] + " : Obect does not exist  " + "</div>";

						}
						else{
							
							Requirement r = new Requirement(toRequirementId, databaseType);
							
							String reqName = r.getRequirementName();
							
							if (reqName.length() > 100 ){
								reqName = reqName.substring(1,99);
							}
							

							if (canRead){
								status += "<div class='alert alert-success'>" + r.getRequirementFullTag() + " : " + 
									reqName + " </div> " ; 
							}
							else {
								status += "<div class='alert alert-success'>" + r.getRequirementFullTag() + " : Valid </div> " ; 
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			if (!(createTraceFrom.equals(""))){
				// split TraceFrom, for each entry there, get the req id, adn then
				// create a trace from it.
				String[] traceFrom = createTraceFrom.split(",");
				for (int i = 0; i < traceFrom.length; i++) {
					try{
						int fromRequirementId =  RequirementUtil.getRequirementIdFromTag(project, traceFrom[i],  databaseType);
						if (fromRequirementId == 0 ) {
							// the from req tag that was sent in was not valid
							status += "<div class='alert alert-danger'>  " +  traceFrom[i] + " : Obectt does not exist  " + "</div>";
						}
						else{
							Requirement r = new Requirement(fromRequirementId, databaseType);
							String reqName = r.getRequirementName();
							if (reqName.length() > 100 ){
								reqName = reqName.substring(1,99);
							}
							if (canRead){
								status += "<div class='alert alert-success'> " + r.getRequirementFullTag() + " : " + 
									reqName + "</div> " ;
							}
							else {
								status += "<div class='alert alert-success'> " + r.getRequirementFullTag() + " : Valid  </div> " ;
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
					
				}
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			
		}
		return status;
		

	}
	
	// called to validated Traceability feasibility .
	// this method is a clone of createTraces, the difference is that it doest not 
	// actually create the traceability, just validates it with the error messages.
	// NOTE : Critical : one other difference is that this validation will NOT check
	// for circular traceability.
	public static String validatePotentialTraces(Project project, String createTraceTo,
			String createTraceFrom, int projectId, SecurityProfile securityProfile, String databaseType) {

		String status = "";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			User user = securityProfile.getUser();
			Calendar cal = Calendar.getInstance();
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// if tracesTo has a space in it drop it.
			if ((createTraceTo != null) && (createTraceTo.contains(" "))) {
				createTraceTo = createTraceTo.replace(" ", "");
			}
			
			// if tracesFrom has a space in it drop it.
			if ((createTraceFrom != null) && (createTraceFrom.contains(" "))) {
				createTraceFrom = createTraceFrom.replace(" ", "");
			}
			
						
			// split Traceto, for each entry there, get the req id, and then
			// create a trace to it.
			if (!(createTraceTo.equals(""))){
				String[] traceTo = createTraceTo.split(",");
				for (int i = 0; i < traceTo.length; i++) {

					int toRequirementId =  RequirementUtil.getRequirementIdFromTag(project, traceTo[i],  databaseType);					

					String toProjectShortName = "";
					String toRequirementFullTag = "";
					
					if (traceTo[i].contains(":")){
						// this means that the req might be an external req
						// which was sent it as prefix:reqFullTag
						String[] temp = traceTo[i].split(":");
						toProjectShortName = temp[0].trim().toUpperCase();
						if (temp.length > 1){
							toRequirementFullTag = temp[1];
						}
					}
					else {
						toProjectShortName = project.getShortName();
						toRequirementFullTag = traceTo[i];
					}
					
					if (!(connectedProjects(project, toProjectShortName,  databaseType))){
						// the toRequirement is not in any of the connected projects list of this project.
						status += "<br>This Requirement is not in a Connected Project : " +toProjectShortName + ":" + toRequirementFullTag;
						continue;
					}
					
					if (toRequirementId != 0){
						
						Requirement toRequirement = new Requirement(toRequirementId,  databaseType);
						if (! permittedToCreateTraceTo(toRequirement, securityProfile)){
							status += "<br>You do not have permissions to Trace To : " +toProjectShortName + ":" + toRequirementFullTag;
							continue;							
						}
						
					} else {
						// this means that an  invalid requirement tag was sent in.
						status += "<br>This Requirement does not exist : " + traceTo[i] ; 
					}
				}
			}
			if (!(createTraceFrom.equals(""))){
				// split TraceFrom, for each entry there, get the req id, adn then
				// create a trace from it.
				String[] traceFrom = createTraceFrom.split(",");
				for (int i = 0; i < traceFrom.length; i++) {
					String sql = "select id from gr_requirements r, gr_projects p "	+ 
						" where r.full_tag = ? " +
						" and r.project_id = ? " +
						" and r.deleted = ? " +
						" and r.project_id = p.id" +
						" and lower(p.short_name) = ? ";

					int fromRequirementId =  RequirementUtil.getRequirementIdFromTag(project, traceFrom[i],  databaseType);					

					String fromProjectShortName = "";
					String fromRequirementFullTag = "";
					
					if (traceFrom[i].contains(":")){
						// this means that the req might be an external req
						// which was sent it as prefix:reqFullTag
						String[] temp = traceFrom[i].split(":");
						fromProjectShortName = temp[0].trim().toUpperCase();
						if (temp.length > 1){
							fromRequirementFullTag = temp[1];
						}
					}
					else {
						fromProjectShortName = project.getShortName();
						fromRequirementFullTag = traceFrom[i];
					}
					
					if (!(connectedProjects(project, fromProjectShortName,  databaseType))){
						// the toRequirement is not in any of the connected projects list of this project.
						status += "<br>This Requirement is not in a Connected Project : " + fromProjectShortName + ":" + fromRequirementFullTag;
						continue;
					}
					
					if (fromRequirementId != 0 ) {

						Requirement fromRequirement = new Requirement(fromRequirementId,  databaseType);
						if (! permittedToCreateTraceFrom(fromRequirement, securityProfile)){
							status += "<br>You do not have permissions to Trace From : " + fromProjectShortName + ":" + fromRequirementFullTag;
							continue;							
						}
					} else {
						// the from req tag that was sent in was not valid
						status += "<br>This Requirement does not exist : " + traceFrom[i];
					}
				}
			}
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
		return status;
		

	}
	
	// called when some one enters a string of traceTo and traceFrom values to
	// be traces to a Req.
	// since the traces have changed, we call the update trace info method with
	// this req id as a param

	public static String createTraces(Project project, int requirementId, String createTraceTo,
			String createTraceFrom, int projectId, SecurityProfile securityProfile, String databaseType) {

		String status = "";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			Requirement requirement = new Requirement(requirementId, databaseType);
			User user = securityProfile.getUser();
			Calendar cal = Calendar.getInstance();
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// if tracesTo has a space in it drop it.
			if ((createTraceTo != null) && (createTraceTo.contains(" "))) {
				createTraceTo = createTraceTo.replace(" ", "");
			}
			
			// if tracesFrom has a space in it drop it.
			if ((createTraceFrom != null) && (createTraceFrom.contains(" "))) {
				createTraceFrom = createTraceFrom.replace(" ", "");
			}
			
						
						// split Traceto, for each entry there, get the req id, and then
			// create a trace to it.
			if ((createTraceTo != null) && (!(createTraceTo.equals("")))){
				String[] traceTo = createTraceTo.split(",");
				for (int i = 0; i < traceTo.length; i++) {

					int toRequirementId =  RequirementUtil.getRequirementIdFromTag(project, traceTo[i],  databaseType);					

					String toProjectShortName = "";
					String toRequirementFullTag = "";
					
					if (traceTo[i].contains(":")){
						// this means that the req might be an external req
						// which was sent it as prefix:reqFullTag
						String[] temp = traceTo[i].split(":");
						toProjectShortName = temp[0].trim().toUpperCase();
						if (temp.length > 1){
							toRequirementFullTag = temp[1];
						}
					}
					else {
						toProjectShortName = project.getShortName();
						toRequirementFullTag = traceTo[i];
					}
					
					toRequirementFullTag = toRequirementFullTag.trim();
					if (!(connectedProjects(project, toProjectShortName,  databaseType))){
						// the toRequirement is not in any of the connected projects list of this project.
						status += "<br>This Requirement is not in a Connected Project : " +toProjectShortName + ":" + toRequirementFullTag;
						continue;
					}
					
					if (toRequirementId != 0){
						
						
						if (requirementId == toRequirementId){
							status += "<br>You can not trace a Requirement to it self " ;
							continue;
						}
						
						if (willCreateACircularTrace (requirementId, toRequirementId)){
							status += "<br>This will lead to a Circular Trace : " +toProjectShortName + ":" + toRequirementFullTag;
							continue;
						}
						
						Requirement toRequirement = new Requirement(toRequirementId,  databaseType);

						
						if (! permittedToCreateTraceTo(toRequirement, securityProfile)){
							status += "<br>You do not have permissions to Trace To : " +toProjectShortName + ":" + toRequirementFullTag;
							continue;							
						}
						
						// if this requirement's req type has a 'can not directly trace to' value that includes the ToRequirement Req Type,
						// then we can not create this trace.
						int sourceRequirementTypeId = requirement.getRequirementTypeId();
						RequirementType sourceRequirementType = new RequirementType(sourceRequirementTypeId);
						String canNotTraceTo = sourceRequirementType.getRequirementTypeCanNotTraceTo();
						int targetRequirementTypeId = toRequirement.getRequirementTypeId();
						RequirementType targetRequirementType = new RequirementType(targetRequirementTypeId);
						
						if (canNotTraceTo.contains(":#:" + targetRequirementType.getRequirementTypeShortName() )){
							status += "<br>  " + requirement.getRequirementFullTag() + " can not DIRECTLY trace to " + 
								toRequirement.getRequirementFullTag() ;
							continue;
						}
							
							
						try {
							String sql2 = "insert into gr_traces" +
								" (description, from_requirement_id, to_requirement_id, suspect) " +
								" values (?,?,?,0)";
							PreparedStatement prepStmt2 = con.prepareStatement(sql2);
							prepStmt2.setString(1,"Trace Created by " + user.getEmailId() + " at " + cal.getTime() );
							prepStmt2.setInt(2, requirementId);
							prepStmt2.setInt(3, toRequirementId);
							prepStmt2.execute();
							prepStmt2.close();
							
							String log = "Created Trace To : " + toProjectShortName +"." +  toRequirementFullTag;
							RequirementUtil.createRequirementLog(requirementId, log, user.getEmailId(), databaseType);
							
							log = "Created Trace From : " + requirement.getProjectShortName() +"." +  requirement.getRequirementFullTag();
							RequirementUtil.createRequirementLog(toRequirementId, log, user.getEmailId(), databaseType);
							
							
						}
						catch (Exception e) {
							// most likely failed because this trace already exists.
							status += "<br>This Trace To already exists : " + toRequirementFullTag;
							continue;
						}
							
						// now lets call the method to updateTraceInfo for the
						// requirement.
						// this will updated the traceTo and TraceFrom columns for
						// this req.
						// we need to call this for both the to and from
						// requirement.
						updateTraceInfoForRequirement(requirementId);
						updateTraceInfoForRequirement(toRequirementId);
					} else {
						// this means that an  invalid requirementtag was sent in.
						status += "<br>This Requirement does not exist : " + traceTo[i] ; 
					}
				}
			}
			if ((createTraceFrom != null) && (!(createTraceFrom.equals("")))){
				// split TraceFrom, for each entry there, get the req id, adn then
				// create a trace from it.
				String[] traceFrom = createTraceFrom.split(",");
				for (int i = 0; i < traceFrom.length; i++) {
					
			
					int fromRequirementId =  RequirementUtil.getRequirementIdFromTag(project, traceFrom[i],  databaseType);					

					String fromProjectShortName = "";
					String fromRequirementFullTag = "";
					
					if (traceFrom[i].contains(":")){
						// this means that the req might be an external req
						// which was sent it as prefix:reqFullTag
						String[] temp = traceFrom[i].split(":");
						fromProjectShortName = temp[0].trim().toUpperCase();
						if (temp.length > 1){
							fromRequirementFullTag = temp[1];
						}
					}
					else {
						fromProjectShortName = project.getShortName();
						fromRequirementFullTag = traceFrom[i];
					}
					
					fromRequirementFullTag = fromRequirementFullTag.trim();
					
					if (!(connectedProjects(project, fromProjectShortName,  databaseType))){
						// the toRequirement is not in any of the connected projects list of this project.
						status += "<br>This Requirement is not in a Connected Project : " + fromProjectShortName + ":" + fromRequirementFullTag;
						continue;
					}
					
					if (fromRequirementId != 0 ) {
						
						
						if (requirementId == fromRequirementId){
							status += "<br>You can not trace a Requirement to it self " ;
							continue;
						}
						
						
						if (willCreateACircularTrace (fromRequirementId, requirementId)){
							status += "<br> This will lead to a Circular Trace : " + fromProjectShortName + ":" + fromRequirementFullTag;
							continue;
						}

						Requirement fromRequirement = new Requirement(fromRequirementId,  databaseType);
						
						
						// if the source requirement's req type has a 'can not directly trace to' value that includes the TargetRequirement Req Type,
						// then we can not create this trace.
						int sourceRequirementTypeId = fromRequirement.getRequirementTypeId();
						RequirementType sourceRequirementType = new RequirementType(sourceRequirementTypeId);
						String canNotTraceTo = sourceRequirementType.getRequirementTypeCanNotTraceTo();
						int targetRequirementTypeId = requirement.getRequirementTypeId();
						RequirementType targetRequirementType = new RequirementType(targetRequirementTypeId);
						
						
						if (canNotTraceTo.contains(":#:" + targetRequirementType.getRequirementTypeShortName() )){
							status += "<br>  " + fromRequirement.getRequirementFullTag() + " can not DIRECTLY trace to " + 
								requirement.getRequirementFullTag() ;
							continue;
						}
						
						
						if (! permittedToCreateTraceFrom(fromRequirement, securityProfile)){
							status += "<br>You do not have permissions to Trace From : " + fromProjectShortName + ":" + fromRequirementFullTag;
							continue;							
						}
						
						
						try {
							String sql2 = "insert into gr_traces(" +
								" description, from_requirement_id, to_requirement_id, suspect) " +
								" values (?,?,?,0)";
							PreparedStatement prepStmt2 = con.prepareStatement(sql2);
							prepStmt2.setString(1,"Trace Created by " + user.getEmailId() + " at " + cal.getTime() );
							prepStmt2.setInt(2, fromRequirementId);
							prepStmt2.setInt(3, requirementId);
		
							prepStmt2.execute();
							prepStmt2.close();
							
							
							String log = "Created Trace From : " + fromProjectShortName +"." +  fromRequirementFullTag;
							RequirementUtil.createRequirementLog(requirementId, log, user.getEmailId(), databaseType);
							
							log = "Created Trace To : " + requirement.getProjectShortName() +"." +  requirement.getRequirementFullTag();
							RequirementUtil.createRequirementLog(fromRequirementId, log, user.getEmailId(), databaseType);
							
						}
						catch (Exception e) {
							// most likely failed because this trace already exists.
							
							status += "<br>This Trace From already exists : " + fromRequirementFullTag;
							continue;
						}
								
						// now lets call the method to updateTraceInfo for the
						// requirement.
						// this will updated the traceTo and TraceFrom columns for
						// this req.
						// we need to call this for both From req and To req.
						updateTraceInfoForRequirement(fromRequirementId);
						updateTraceInfoForRequirement(requirementId);
	
						
					} else {
						
						// the from req tag that was sent in was not valid
						status += "<br>This Requirement does not exist : " + traceFrom[i];
					}
					
				}
				
			}
			
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
		return status;
		

	}


	public static String deleteTraces(Project project, int requirementId, String deleteTraceTo,
			String deleteTraceFrom, int projectId, SecurityProfile securityProfile, String databaseType) {

		String status = "";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			Requirement requirement = new Requirement(requirementId, databaseType);
			User user = securityProfile.getUser();
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// if tracesTo has a space in it drop it.
			if ((deleteTraceTo != null) && (deleteTraceTo.contains(" "))) {
				deleteTraceTo = deleteTraceTo.replace(" ", "");
			}
			
			// if tracesFrom has a space in it drop it.
			if ((deleteTraceFrom != null) && (deleteTraceFrom.contains(" "))) {
				deleteTraceFrom = deleteTraceFrom.replace(" ", "");
			}
			
						
			// split Traceto, for each entry there, get the req id, and then
			// create a trace to it.
			if ((deleteTraceTo != null) && (!(deleteTraceTo.equals("")))){
				String[] traceTo = deleteTraceTo.split(",");
				for (int i = 0; i < traceTo.length; i++) {

					int toRequirementId =  RequirementUtil.getRequirementIdFromTag(project, traceTo[i],  databaseType);					

					String toProjectShortName = "";
					String toRequirementFullTag = "";
					
					if (traceTo[i].contains(":")){
						// this means that the req might be an external req
						// which was sent it as prefix:reqFullTag
						String[] temp = traceTo[i].split(":");
						toProjectShortName = temp[0].trim().toUpperCase();
						if (temp.length > 1){
							toRequirementFullTag = temp[1];
						}
					}
					else {
						toProjectShortName = project.getShortName();
						toRequirementFullTag = traceTo[i];
					}
					
					if (!(connectedProjects(project, toProjectShortName,  databaseType))){
						// the toRequirement is not in any of the connected projects list of this project.
						status += "<br>This Requirement is not in a Connected Project : " +toProjectShortName + ":" + toRequirementFullTag;
						continue;
					}
					
					if (toRequirementId != 0){
						Trace trace = new Trace (requirement.getRequirementId(), toRequirementId);
						status = RequirementUtil.deleteTrace(trace.getId(), user.getEmailId(), securityProfile,  databaseType);	
					} else {
						// this means that an  invalid requirementtag was sent in.
						status += "<br>This Requirement does not exist : " + traceTo[i] ; 
					}
				}
			}
			if ((deleteTraceFrom != null) && (!(deleteTraceFrom.equals("")))){
				// split TraceFrom, for each entry there, get the req id, adn then
				// create a trace from it.
				String[] traceFrom = deleteTraceFrom.split(",");
				for (int i = 0; i < traceFrom.length; i++) {
					
			
					int fromRequirementId =  RequirementUtil.getRequirementIdFromTag(project, traceFrom[i],  databaseType);					

					String fromProjectShortName = "";
					String fromRequirementFullTag = "";
					
					if (traceFrom[i].contains(":")){
						// this means that the req might be an external req
						// which was sent it as prefix:reqFullTag
						String[] temp = traceFrom[i].split(":");
						fromProjectShortName = temp[0].trim().toUpperCase();
						if (temp.length > 1){
							fromRequirementFullTag = temp[1];
						}
					}
					else {
						fromProjectShortName = project.getShortName();
						fromRequirementFullTag = traceFrom[i];
					}
					
					if (!(connectedProjects(project, fromProjectShortName,  databaseType))){
						// the toRequirement is not in any of the connected projects list of this project.
						status += "<br>This Requirement is not in a Connected Project : " + fromProjectShortName + ":" + fromRequirementFullTag;
						continue;
					}
					
					if (fromRequirementId != 0 ) {
						Trace trace = new Trace (fromRequirementId, requirement.getRequirementId());
						status = RequirementUtil.deleteTrace(trace.getId(), user.getEmailId(), securityProfile,  databaseType);	
					} else {
						
						// the from req tag that was sent in was not valid
						status += "<br>This Requirement does not exist : " + traceFrom[i];
					}
					
				}
				
			}
			
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
		return status;
		

	}

	public static String clearSuspectTraces(Project project, int requirementId, String clearTraceTo,
			String clearTraceFrom, int projectId, SecurityProfile securityProfile, String databaseType) {

		String status = "";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			Requirement requirement = new Requirement(requirementId, databaseType);
			User user = securityProfile.getUser();
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// if tracesTo has a space in it drop it.
			if ((clearTraceTo != null) && (clearTraceTo.contains(" "))) {
				clearTraceTo = clearTraceTo.replace(" ", "");
			}
			
			// if tracesFrom has a space in it drop it.
			if ((clearTraceFrom != null) && (clearTraceFrom.contains(" "))) {
				clearTraceFrom = clearTraceFrom.replace(" ", "");
			}
			
						
			// split Traceto, for each entry there, get the req id, and then
			// create a trace to it.
			if ((clearTraceTo != null) && (!(clearTraceTo.equals("")))){
				String[] traceTo = clearTraceTo.split(",");
				for (int i = 0; i < traceTo.length; i++) {

					int toRequirementId =  RequirementUtil.getRequirementIdFromTag(project, traceTo[i],  databaseType);					

					String toProjectShortName = "";
					String toRequirementFullTag = "";
					
					if (traceTo[i].contains(":")){
						// this means that the req might be an external req
						// which was sent it as prefix:reqFullTag
						String[] temp = traceTo[i].split(":");
						toProjectShortName = temp[0].trim().toUpperCase();
						if (temp.length > 1){
							toRequirementFullTag = temp[1];
						}
					}
					else {
						toProjectShortName = project.getShortName();
						toRequirementFullTag = traceTo[i];
					}
					
					if (!(connectedProjects(project, toProjectShortName,  databaseType))){
						// the toRequirement is not in any of the connected projects list of this project.
						status += "<br>This Requirement is not in a Connected Project : " +toProjectShortName + ":" + toRequirementFullTag;
						continue;
					}
					
					if (toRequirementId != 0){
						Trace trace = new Trace (requirement.getRequirementId(), toRequirementId);
						status = RequirementUtil.clearSuspect(trace.getId(), user.getEmailId(), securityProfile,  databaseType);	
					} else {
						// this means that an  invalid requirementtag was sent in.
						status += "<br>This Requirement does not exist : " + traceTo[i] ; 
					}
				}
			}
			if ((clearTraceFrom != null) && (!(clearTraceFrom.equals("")))){
				// split TraceFrom, for each entry there, get the req id, adn then
				// create a trace from it.
				String[] traceFrom = clearTraceFrom.split(",");
				for (int i = 0; i < traceFrom.length; i++) {
					
			
					int fromRequirementId =  RequirementUtil.getRequirementIdFromTag(project, traceFrom[i],  databaseType);					

					String fromProjectShortName = "";
					String fromRequirementFullTag = "";
					
					if (traceFrom[i].contains(":")){
						// this means that the req might be an external req
						// which was sent it as prefix:reqFullTag
						String[] temp = traceFrom[i].split(":");
						fromProjectShortName = temp[0].trim().toUpperCase();
						if (temp.length > 1){
							fromRequirementFullTag = temp[1];
						}
					}
					else {
						fromProjectShortName = project.getShortName();
						fromRequirementFullTag = traceFrom[i];
					}
					
					if (!(connectedProjects(project, fromProjectShortName,  databaseType))){
						// the toRequirement is not in any of the connected projects list of this project.
						status += "<br>This Requirement is not in a Connected Project : " + fromProjectShortName + ":" + fromRequirementFullTag;
						continue;
					}
					
					if (fromRequirementId != 0 ) {
						Trace trace = new Trace (fromRequirementId, requirement.getRequirementId());
						status = RequirementUtil.clearSuspect(trace.getId(), user.getEmailId(), securityProfile,  databaseType);	
					} else {
						
						// the from req tag that was sent in was not valid
						status += "<br>This Requirement does not exist : " + traceFrom[i];
					}
					
				}
				
			}
			
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
		return status;
		

	}

	public static String makeTraceSuspect(Project project, int requirementId, String makeSuspectTraceTo,
			String makeSuspectTraceFrom, int projectId, SecurityProfile securityProfile, String databaseType) {

		String status = "";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			Requirement requirement = new Requirement(requirementId, databaseType);
			User user = securityProfile.getUser();
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// if tracesTo has a space in it drop it.
			if ((makeSuspectTraceTo != null) && (makeSuspectTraceTo.contains(" "))) {
				makeSuspectTraceTo = makeSuspectTraceTo.replace(" ", "");
			}
			
			// if tracesFrom has a space in it drop it.
			if ((makeSuspectTraceFrom != null) && (makeSuspectTraceFrom.contains(" "))) {
				makeSuspectTraceFrom = makeSuspectTraceFrom.replace(" ", "");
			}
			
						
			// split Traceto, for each entry there, get the req id, and then
			// create a trace to it.
			if ((makeSuspectTraceTo != null) && (!(makeSuspectTraceTo.equals("")))){
				String[] traceTo = makeSuspectTraceTo.split(",");
				for (int i = 0; i < traceTo.length; i++) {

					int toRequirementId =  RequirementUtil.getRequirementIdFromTag(project, traceTo[i],  databaseType);					

					String toProjectShortName = "";
					String toRequirementFullTag = "";
					
					if (traceTo[i].contains(":")){
						// this means that the req might be an external req
						// which was sent it as prefix:reqFullTag
						String[] temp = traceTo[i].split(":");
						toProjectShortName = temp[0].trim().toUpperCase();
						if (temp.length > 1){
							toRequirementFullTag = temp[1];
						}
					}
					else {
						toProjectShortName = project.getShortName();
						toRequirementFullTag = traceTo[i];
					}
					
					if (!(connectedProjects(project, toProjectShortName,  databaseType))){
						// the toRequirement is not in any of the connected projects list of this project.
						status += "<br>This Requirement is not in a Connected Project : " +toProjectShortName + ":" + toRequirementFullTag;
						continue;
					}
					
					if (toRequirementId != 0){
						Trace trace = new Trace (requirement.getRequirementId(), toRequirementId);
						status = RequirementUtil.makeSuspect(trace.getId(), user.getEmailId(), securityProfile,  databaseType);	
					} else {
						// this means that an  invalid requirementtag was sent in.
						status += "<br>This Requirement does not exist : " + traceTo[i] ; 
					}
				}
			}
			if ((makeSuspectTraceFrom != null) && (!(makeSuspectTraceFrom.equals("")))){
				// split TraceFrom, for each entry there, get the req id, adn then
				// create a trace from it.
				String[] traceFrom = makeSuspectTraceFrom.split(",");
				for (int i = 0; i < traceFrom.length; i++) {
					
			
					int fromRequirementId =  RequirementUtil.getRequirementIdFromTag(project, traceFrom[i],  databaseType);					

					String fromProjectShortName = "";
					String fromRequirementFullTag = "";
					
					if (traceFrom[i].contains(":")){
						// this means that the req might be an external req
						// which was sent it as prefix:reqFullTag
						String[] temp = traceFrom[i].split(":");
						fromProjectShortName = temp[0].trim().toUpperCase();
						if (temp.length > 1){
							fromRequirementFullTag = temp[1];
						}
					}
					else {
						fromProjectShortName = project.getShortName();
						fromRequirementFullTag = traceFrom[i];
					}
					
					if (!(connectedProjects(project, fromProjectShortName,  databaseType))){
						// the toRequirement is not in any of the connected projects list of this project.
						status += "<br>This Requirement is not in a Connected Project : " + fromProjectShortName + ":" + fromRequirementFullTag;
						continue;
					}
					
					if (fromRequirementId != 0 ) {
						Trace trace = new Trace (fromRequirementId, requirement.getRequirementId());
						status = RequirementUtil.makeSuspect(trace.getId(), user.getEmailId(), securityProfile,  databaseType);	
					} else {
						
						// the from req tag that was sent in was not valid
						status += "<br>This Requirement does not exist : " + traceFrom[i];
					}
					
				}
				
			}
			
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
		return status;
		

	}
	
	
	// returns True is the user is permitted to a create a Trace To the requirement.
	public static boolean permittedToCreateTraceTo( Requirement toRequirement, SecurityProfile securityProfile) {
		boolean permitted = false;
		
		if ( securityProfile.getPrivileges().contains("traceToRequirementsInFolder" 
    			+ toRequirement.getFolderId())){
			permitted = true;
		}

		return permitted;
	}

	public static boolean connectedProjects( int projectId, Requirement toRequirement) {
		boolean connected = false;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			if (toRequirement.getProjectId() == projectId) {
				// this req is in same project. so its connected by definition.
				connected = true;
			}
			else {
				// lets see if this req is in a connected project.
				String sql = "select count(*) \"matches\" from gr_project_relations " + 
					" where project_id = ? " +
					" and related_project_id = ? ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.setInt(2, toRequirement.getProjectId());
				rs = prepStmt.executeQuery();
				int matches = 0;
				while (rs.next()) {
					matches = rs.getInt("matches");
				}
				if (matches > 0){
					connected = true;
				}
				else {
					connected = false;
				}
				rs.close();
				prepStmt.close();
			}
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
		return connected;
	}
	
	public static boolean requirementInLockedBaseline( int requirementId) {
		boolean inLockedBaseline = false;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// lets see if this req is in a connected project.
			String sql = "select count(*) \"matches\" " +
				" from gr_requirement_baselines b, gr_rt_baselines rtb " + 
				" where b.requirement_id = ? " +
				" and b.rt_baseline_id = rtb.id " +
				" and rtb.locked = 1 ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			
			rs = prepStmt.executeQuery();
			int matches = 0;
			while (rs.next()) {
				matches = rs.getInt("matches");
			}
			if (matches > 0){
				inLockedBaseline = true;
			}
			else {
				inLockedBaseline = false;
			}
			rs.close();
			prepStmt.close();
			
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
		return inLockedBaseline;
	}

	// this is same as above, except it take the project id and a connected project short name
	// and returns true if these two are connected.
	public static boolean connectedProjects( Project project, String connectedProjectShortName, String databaseType) {
		// lets iterate through all the related projects and see if the requirement's project is a valid one.
		boolean connected = false;
		if (project.getShortName().trim().toUpperCase().equals(connectedProjectShortName.trim().toUpperCase())) {
			// this req is in same project. so its connected by definition.
			connected = true;
		}
		else {
			ArrayList projectRelations = project.getProjectRelations(databaseType);
			Iterator i = projectRelations.iterator();
			while (i.hasNext()){
				ProjectRelation projectRelation  = (ProjectRelation) i.next();
				if (projectRelation.getRelatedProjectShortName().trim().toUpperCase().equals(connectedProjectShortName.trim().toUpperCase())){
					connected = true;
				}
			}
		}
		return connected;
	}
		

	// returns True is the user is permitted to a create a Trace from the requirement.
	public static boolean permittedToCreateTraceFrom( Requirement fromRequirement, SecurityProfile securityProfile) {
		boolean permitted = false;
		if ( securityProfile.getPrivileges().contains("traceFromRequirementsInFolder" 
    			+ fromRequirement.getFolderId())){
			permitted = true;
		}

		return permitted;
	}
	
	
	
	// returns an empty string if all tags exist (not deleted)
	// in this project. Else returns a list of invalid tags (comma separated).
	public static String validateRequirementTags(
			String requirementTags, int projectId) {

		String status = "";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// split Traceto, for each entry there, get the req id, and then
			// create a trace to it.
			if (!(requirementTags.equals(""))){
				String[] requirementTag = requirementTags.split(",");
				for (int i = 0; i < requirementTag.length; i++) {
					String sql = "select count(*) \"matches\" from gr_requirements " + 
						" where upper(full_tag) = ? " + 
						" and project_id = ? " +
						" and deleted = ? ";
					String requirementFullTag = requirementTag[i];
					// get the requiremetnId for this FullTag.
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, requirementFullTag.toUpperCase().trim() );
					prepStmt.setInt(2, projectId);
					prepStmt.setInt(3, 0);
					rs = prepStmt.executeQuery();
					int matches = 0;
					while (rs.next()) {
						matches = rs.getInt("matches");
					}
					if (matches == 0){
						status += requirementFullTag + ",";
					}
					rs.close();
					prepStmt.close();
				}
			}
			
			// lets drop the last ,
			if (status.contains(",")) {
				status = (String) status.subSequence(0, status.lastIndexOf(","));
			}
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
		return status;
		

	}


	// this routine will navigate up to 7 levels , going up the trace tree
	// to see if the fromRequirementId appears any where in the toRequirementId's
	// upstream.
	// we are limiting to 7 levels, because a) we have to stop somewhere
	// and b) our trace tree go down 7 levels only.
	public static boolean willCreateACircularTrace (int fromRequirementId, int toRequirementId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		boolean circularTrace = false;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// See if the fromReq exists in the toReqs
			// traceTo field.
			String sql = "select t.to_requirement_id " +
			" from   gr_traces t" +
			" where t.from_requirement_id = ? ";

			// level 1 start.
			PreparedStatement prepStmt1 = con.prepareStatement(sql);
			prepStmt1.setInt(1, toRequirementId);
			ResultSet rs1 = prepStmt1.executeQuery();
			while (rs1.next()) {

				// lets iterate through all the reqs that this toReq
				// trace up to, and make sure that our target fromReq 
				// doesn't appear in the chain.
				int toReq1 = rs1.getInt("to_requirement_id");
				if (toReq1 == fromRequirementId) {
					// if we find the fromReqId matching any of the
					// toReqs, we return with an error condition.
					return (true);
				}
				
				// for each of these toReqs, lets go up one more level
				// and see if fromReq exists there.

				// level 2 start.
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1, toReq1);
				ResultSet rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
	
					// lets iterate through all the reqs that this toReq
					// trace up to, and make sure that our target fromReq 
					// doesn't appear in the chain.
					int toReq2 = rs2.getInt("to_requirement_id");
					if (toReq2 == fromRequirementId) {
						// if we find the fromReqId matching any of the
						// toReqs, we return with an error condition.
						return (true);
					}
					
					// for each of these toReqs, lets go up one more level
					// and see if fromReq exists there.

					// level 3 start.
					PreparedStatement prepStmt3 = con.prepareStatement(sql);
					prepStmt3.setInt(1, toReq2);
					ResultSet rs3 = prepStmt3.executeQuery();
					while (rs3.next()) {
		
						// lets iterate through all the reqs that this toReq
						// trace up to, and make sure that our target fromReq 
						// doesn't appear in the chain.
						int toReq3 = rs3.getInt("to_requirement_id");
						if (toReq3 == fromRequirementId) {
							// if we find the fromReqId matching any of the
							// toReqs, we return with an error condition.
							return (true);
						}
						
						// for each of these toReqs, lets go up one more level
						// and see if fromReq exists there.

						// level 4 start.
						PreparedStatement prepStmt4 = con.prepareStatement(sql);
						prepStmt4.setInt(1, toReq3);
						ResultSet rs4 = prepStmt4.executeQuery();
						while (rs4.next()) {
			
							// lets iterate through all the reqs that this toReq
							// trace up to, and make sure that our target fromReq 
							// doesn't appear in the chain.
							int toReq4 = rs4.getInt("to_requirement_id");
							if (toReq4 == fromRequirementId) {
								// if we find the fromReqId matching any of the
								// toReqs, we return with an error condition.
								return (true);
							}
							
							// for each of these toReqs, lets go up one more level
							// and see if fromReq exists there.
							
							// level 5 start.
							PreparedStatement prepStmt5 = con.prepareStatement(sql);
							prepStmt5.setInt(1, toReq4);
							ResultSet rs5 = prepStmt5.executeQuery();
							while (rs5.next()) {
				
								// lets iterate through all the reqs that this toReq
								// trace up to, and make sure that our target fromReq 
								// doesn't appear in the chain.
								int toReq5 = rs5.getInt("to_requirement_id");
								if (toReq5 == fromRequirementId) {
									// if we find the fromReqId matching any of the
									// toReqs, we return with an error condition.
									return (true);
								}
								
								// for each of these toReqs, lets go up one more level
								// and see if fromReq exists there.
								
								// level 6 start.
								PreparedStatement prepStmt6 = con.prepareStatement(sql);
								prepStmt6.setInt(1, toReq5);
								ResultSet rs6 = prepStmt6.executeQuery();
								while (rs6.next()) {
					
									// lets iterate through all the reqs that this toReq
									// trace up to, and make sure that our target fromReq 
									// doesn't appear in the chain.
									int toReq6 = rs6.getInt("to_requirement_id");
									if (toReq6 == fromRequirementId) {
										// if we find the fromReqId matching any of the
										// toReqs, we return with an error condition.
										return (true);
									}
									
									// for each of these toReqs, lets go up one more level
									// and see if fromReq exists there.
									
									// level 7 start.
									PreparedStatement prepStmt7 = con.prepareStatement(sql);
									prepStmt7.setInt(1, toReq6);
									ResultSet rs7 = prepStmt7.executeQuery();
									while (rs7.next()) {
						
										// lets iterate through all the reqs that this toReq
										// trace up to, and make sure that our target fromReq 
										// doesn't appear in the chain.
										int toReq7 = rs7.getInt("to_requirement_id");
										if (toReq7 == fromRequirementId) {
											// if we find the fromReqId matching any of the
											// toReqs, we return with an error condition.
											return (true);
										}
										
										// for each of these toReqs, lets go up one more level
										// and see if fromReq exists there.
											
									}
									rs7.close();
									prepStmt7.close();
									// level 7 end.		
								}
								rs6.close();
								prepStmt6.close();
								// level 6 end.								
							}
							rs5.close();
							prepStmt5.close();
							// level 5 end.									
						}
						rs4.close();
						prepStmt4.close();
						// level 4 end.						
					}
					rs3.close();
					prepStmt3.close();
					// level 3 end.
						
				}
				rs2.close();
				prepStmt2.close();
				// level 2 end.
			}
			rs1.close();
			prepStmt1.close();
			// level 1 end.
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
		return circularTrace;
		
	}
	// called when someone selects to delete a trace .
	public static String deleteTrace(int traceId, String actorEmailId, SecurityProfile securityProfile, String databaseType) {
		String status = "";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// get the to and from reqs for this trace and store them in memory
			// delete the trace and then call the updateTraceInfo method.
			// NOTE : we can not use updateTraceInfoForTrace(traceId); as we
			// want the
			// trace to be deleted when we get the trace info and it's a chicken
			// / egg problem.
			String sql = " select from_requirement_id, to_requirement_id from gr_traces where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, traceId);
			rs = prepStmt.executeQuery();

			int fromRequirementId = 0;
			int toRequirementId = 0;
			if (rs.next()) {
				fromRequirementId = rs.getInt("from_requirement_id");
				toRequirementId = rs.getInt("to_requirement_id");
			}
			prepStmt.close();
			rs.close();
			
			
			
			
			// lets see if the user has 'Trace' permissions to To Req and From Req.
			// if he doesn't then we will need to return with an error message.
			Requirement toRequirement = new Requirement (toRequirementId,  databaseType);
			if (! permittedToCreateTraceTo(toRequirement, securityProfile)){
				status += "<br>You do not have permissions to delete Trace To : " + toRequirement.getRequirementFullTag();
				return (status);							
			}			
			
			Requirement fromRequirement = new Requirement (fromRequirementId,  databaseType);
			if (! permittedToCreateTraceFrom(fromRequirement, securityProfile)){
				status += "<br>You do not have permissions to delete Trace From : " + fromRequirement.getRequirementFullTag();
				return (status);							
			}			
			
			
			// Now delete the trace.
			sql = " delete from gr_traces where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, traceId);
			prepStmt.execute();

			prepStmt.close();
			

			// now lets call the method to updateTraceInfo for the requirement.
			// this will updated the traceTo and TraceFrom columns for this req.
			updateTraceInfoForRequirement(fromRequirementId);
			updateTraceInfoForRequirement(toRequirementId);

			// we need to log the fact that the fromRequirement and
			// toRequirements trace info
			// has changed.
			String log = "Deleted Trace To : " + toRequirement.getProjectShortName() +"." +  toRequirement.getRequirementFullTag();
			RequirementUtil.createRequirementLog(fromRequirementId, log, actorEmailId, databaseType);
			
			log = "Deleted Trace From : " + fromRequirement.getProjectShortName() +"." +  fromRequirement.getRequirementFullTag();
			RequirementUtil.createRequirementLog(toRequirementId, log, actorEmailId, databaseType);
			
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
		return(status);

	}

	// called when someone selects to clear a trace .
	public static String clearSuspect(int traceId, String actorEmailId, SecurityProfile securityProfile, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;

		String status = "";
		try {

			User user = securityProfile.getUser();
			Calendar cal = Calendar.getInstance();
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// get the to and from reqs for this trace and see if this user
			// has permissions to modify traces to / from these objects.
			
			String sql = " select from_requirement_id, to_requirement_id from gr_traces where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, traceId);
			rs = prepStmt.executeQuery();

			int fromRequirementId = 0;
			int toRequirementId = 0;
			if (rs.next()) {
				fromRequirementId = rs.getInt("from_requirement_id");
				toRequirementId = rs.getInt("to_requirement_id");
			}

			prepStmt.close();
			rs.close();
			
			// lets see if the user has 'Trace' permissions to To Req and From Req.
			// if he doesn't then we will need to return with an error message.
			Requirement toRequirement = new Requirement (toRequirementId,  databaseType);
			if (! permittedToCreateTraceTo(toRequirement, securityProfile)){
				status += "<br>You do not have permissions to modify Trace To : " + toRequirement.getRequirementFullTag();
				return (status);							
			}			
			
			Requirement fromRequirement = new Requirement (fromRequirementId,  databaseType);
			if (! permittedToCreateTraceFrom(fromRequirement, securityProfile)){
				status += "<br>You do not have permissions to modify Trace From : " + fromRequirement.getRequirementFullTag();
				return (status);							
			}			

			
			
			
			sql = " update gr_traces set description = ? , suspect=0 where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, "Trace cleared by " + user.getEmailId() + " at " + cal.getTime());
			prepStmt.setInt(2, traceId);
			prepStmt.execute();

			prepStmt.close();
			con.close();

			updateTraceInfoForTrace(traceId, actorEmailId,  databaseType);
			
			String log = "Cleared Suspect Trace To : " + toRequirement.getProjectShortName() +"." +  toRequirement.getRequirementFullTag();
			RequirementUtil.createRequirementLog(fromRequirementId, log, actorEmailId, databaseType);
			
			log = "Cleared Suspect Trace From : " + fromRequirement.getProjectShortName() +"." +  fromRequirement.getRequirementFullTag();
			RequirementUtil.createRequirementLog(toRequirementId, log, actorEmailId, databaseType);
			
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
		
		return(status);
	}

	
	
	
	public static String setTraceReason(String traceReason, int traceId, String actorEmailId, SecurityProfile securityProfile, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;

		String status = "";
		try {

			User user = securityProfile.getUser();
			Calendar cal = Calendar.getInstance();
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// get the to and from reqs for this trace and see if this user
			// has permissions to modify traces to / from these objects.
			
			String sql = " select from_requirement_id, to_requirement_id from gr_traces where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, traceId);
			rs = prepStmt.executeQuery();

			int fromRequirementId = 0;
			int toRequirementId = 0;
			if (rs.next()) {
				fromRequirementId = rs.getInt("from_requirement_id");
				toRequirementId = rs.getInt("to_requirement_id");
			}

			prepStmt.close();
			rs.close();
			
			// lets see if the user has 'Trace' permissions to To Req and From Req.
			// if he doesn't then we will need to return with an error message.
			Requirement toRequirement = new Requirement (toRequirementId,  databaseType);
			if (! permittedToCreateTraceTo(toRequirement, securityProfile)){
				status += "<br>You do not have permissions to modify Trace To : " + toRequirement.getRequirementFullTag();
				return (status);							
			}			
			
			Requirement fromRequirement = new Requirement (fromRequirementId,  databaseType);
			if (! permittedToCreateTraceFrom(fromRequirement, securityProfile)){
				status += "<br>You do not have permissions to modify Trace From : " + fromRequirement.getRequirementFullTag();
				return (status);							
			}			

			
			
		
			
			// lets get oldTraceReason
			
			sql = "select reason from gr_traces where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, traceId);
			rs = prepStmt.executeQuery();
			
			String oldTraceReason = "";
			
			while (rs.next()){
				oldTraceReason = rs.getString("reason");
			}
			
    	
    		
    		
			
			sql = " update gr_traces set reason = ? where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, traceReason);
			prepStmt.setInt(2, traceId);
			prepStmt.execute();

			prepStmt.close();
			con.close();

			
			String log = "Update Trace Reason  from : " + oldTraceReason + " to : " + traceReason 
					+ " for trace to " + toRequirement.getProjectShortName() +"." +  toRequirement.getRequirementFullTag();
			RequirementUtil.createRequirementLog(fromRequirementId, log, actorEmailId, databaseType);
			
			
			status += "	<div  style='border:2px dotted red; border-radius:10px; padding:10px 10px 10px 10px; '> ";
			status += traceReason;
			status += "</div>";
			
			status += "	<div id='addEditReasonDiv" + traceId+ "' style='display:none; border:2px dotted red; border-radius:10px; padding:10px 10px 10px 10px; '> ";
			
			status += "<input type='text' id='addEditReasonTextBox" + traceId + "' value='" + traceReason + "' placeholder='Justification for this Trace'>";
			status += "		<input type='button' class='btn btn-sm btn-primary' value='Go' onClick='updateTraceReason(" + traceId + ");'>";
			status += " </div>";
			status += "	</div>";
		
			
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
		
		return(status);
	}

	// called when someone selects to clear a trace .
	public static String clearSuspect(int fromRequirementId, int toRequirementId,
		String actorEmailId, SecurityProfile securityProfile, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;

		String status = "";
		try {

			User user = securityProfile.getUser();
			Calendar cal = Calendar.getInstance();
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the traceId.
			String sql = "select id from gr_traces" +
				" where from_requirement_id = ? " +
				" and to_requirement_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, fromRequirementId);
			prepStmt.setInt(2, toRequirementId);
			rs = prepStmt.executeQuery();
			int traceId = 0;
			while (rs.next()){
				traceId = rs.getInt("id");
			}
			prepStmt.close();
			rs.close();
			
			
			
			// lets see if the user has 'Trace' permissions to To Req and From Req.
			// if he doesn't then we will need to return with an error message.
			Requirement toRequirement = new Requirement (toRequirementId,  databaseType);
			if (! permittedToCreateTraceTo(toRequirement, securityProfile)){
				status += "<br>You do not have permissions to modify Trace To : " + toRequirement.getRequirementFullTag();
				return (status);							
			}			
			
			Requirement fromRequirement = new Requirement (fromRequirementId,  databaseType);
			if (! permittedToCreateTraceFrom(fromRequirement, securityProfile)){
				status += "<br>You do not have permissions to modify Trace From : " + fromRequirement.getRequirementFullTag();
				return (status);							
			}			

			
			sql = " update gr_traces set description = ? , suspect=0 where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, "Trace cleared by " + user.getEmailId() + " at " + cal.getTime());
			prepStmt.setInt(2, traceId);
			prepStmt.execute();

			prepStmt.close();
			con.close();

			updateTraceInfoForTrace(traceId, actorEmailId,  databaseType);
			
			String log = "Cleared Suspect Trace To : " + toRequirement.getProjectShortName() +"." +  toRequirement.getRequirementFullTag();
			RequirementUtil.createRequirementLog(fromRequirementId, log, actorEmailId, databaseType);
			
			log = "Cleared Suspect Trace From : " + fromRequirement.getProjectShortName() +"." +  fromRequirement.getRequirementFullTag();
			RequirementUtil.createRequirementLog(toRequirementId, log, actorEmailId, databaseType);
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
		
		return(status);
	}

	
	// called when someone selects to clear a trace and send in a  requirementid.
	public static void clearSuspectTraceTo(int requirementId, String actorEmailId,
			SecurityProfile securityProfile, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;

		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the traceids of the 'ToTraces' for this req
			// that are having a suspect relationship.
			String sql = " select id " +
				" from gr_traces where from_requirement_id = ? and suspect = 1 ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				int traceId = rs.getInt("id");
				RequirementUtil.clearSuspect(traceId, actorEmailId, securityProfile,  databaseType);
			}
			rs.close();
			prepStmt.close();
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
	}


	// given a project and a tag, it figures out the requirement it.
	// its tricky, because the tag can be like prefix1:BR-2 , where BR-2 can be in 
	// an external project called prefix1. We also need to make sure that prefix1 
	// is validly connected to this project.
	// NOTE : this works where fulltag has prefix:fulltag. that's how this is different
	// from get getRequirementId routine.
	
	public static int getRequirementIdFromTag(Project project, String requirementFullTag, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		int requirementId = 0;

		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			
			if (requirementFullTag.contains(":")){
				// this means that the req is an external req
				// which was sent it as prefix:reqFullTag
				String[] temp = requirementFullTag.split(":");
				String requirementsProjectShortName = temp[0].trim().toUpperCase();
				if (temp.length > 1){
					requirementFullTag = temp[1];
				}
				// we need to check for valid external project connection etc..
				// only if the req's project short name is different from this project's short name.
				if (!(requirementsProjectShortName.equals(project.getShortName().trim().toUpperCase()))){
					// this req is not in the current project.
					ArrayList projectRelations = project.getProjectRelations(  databaseType);
					// lets iterate through all the related projects and see if the requirement's project is a valid one.
					boolean validConnection = false;
					int connectedProjectId = 0;
					Iterator i = projectRelations.iterator();
					while (i.hasNext()){
						ProjectRelation projectRelation  = (ProjectRelation) i.next();
						if (projectRelation.getRelatedProjectShortName().trim().toUpperCase().equals(requirementsProjectShortName)){
							validConnection = true;
							connectedProjectId = projectRelation.getRelatedProjectId();
						}
					}
					if (!validConnection){
						// trying to access an invalid project's requirement.
						return 0;
					}
					else {
						// valid connection and we have the connected project Id.
						String sql = "select r.id from gr_requirements r " + 
						" where r.full_tag = ? " + 
						" and r.project_id = ? " +
						" and r.deleted = ? ";
						// get the requiremetnId for this FullTag.
						prepStmt = con.prepareStatement(sql);
						prepStmt.setString(1, requirementFullTag.toUpperCase());
						prepStmt.setInt(2, connectedProjectId);
						prepStmt.setInt(3, 0);
						rs = prepStmt.executeQuery();
						while (rs.next()) {
							requirementId = rs.getInt("id");
						}
						rs.close();
						prepStmt.close();
					}
					
					
				}
				else {
					// this requirement is in the same project. 
					String sql = "select r.id from gr_requirements r " + 
					" where r.full_tag = ? " + 
					" and r.project_id = ? " +
					" and r.deleted = ? ";
					// get the requiremetnId for this FullTag.
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, requirementFullTag.toUpperCase());
					prepStmt.setInt(2, project.getProjectId());
					prepStmt.setInt(3, 0);
					rs = prepStmt.executeQuery();
					while (rs.next()) {
						requirementId = rs.getInt("id");
					}
					rs.close();
					prepStmt.close();
				}
			}
			else {
				// no project prefix was given. So we assume that this is in the current project.
				String sql = "select r.id from gr_requirements r " + 
				" where r.full_tag = ? " + 
				" and r.project_id = ? " +
				" and r.deleted = ? ";
				// get the requiremetnId for this FullTag.
				prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, requirementFullTag.toUpperCase());
				prepStmt.setInt(2, project.getProjectId());
				prepStmt.setInt(3, 0);
				rs = prepStmt.executeQuery();
				while (rs.next()) {
					requirementId = rs.getInt("id");
				}
				rs.close();
				prepStmt.close();
				
			}
			
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
		return requirementId;
	}

	// called when someone selects to clear a trace from and send in a  requirementid.
	public static void clearSuspectTraceFrom(int requirementId, String actorEmailId,
		SecurityProfile securityProfile, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;

		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the trace ids of 'TraceFroms' to this requirement 
			// that are having a suspect relationship.
			String sql = " select id " +
				" from gr_traces where to_requirement_id = ? and suspect = 1";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				int traceId = rs.getInt("id");
				RequirementUtil.clearSuspect(traceId, actorEmailId, securityProfile,  databaseType);
			}
			rs.close();
			prepStmt.close();
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
	}
	

	// removes all the Trace goings from this requirement.
	// NOTE :ALL traces will to this req will be removed. not just the suspect ones.
	public static void deleteAllTraceTo(int requirementId, String actorEmailId, SecurityProfile securityProfile, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;

		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the traceids of the 'ToTraces' for this req
			// 
			String sql = " select id " +
				" from gr_traces where from_requirement_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				int traceId = rs.getInt("id");
				RequirementUtil.deleteTrace(traceId, actorEmailId, securityProfile,  databaseType);
			}
			rs.close();
			prepStmt.close();
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
	}

	
	// removes all the Trace coming to this requirement.
	// NOTE :ALL traces from this req will be removed. not just the suspect ones.
	public static void deleteAllTraceFrom(int requirementId, String actorEmailId,
		SecurityProfile securityProfile, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;

		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the traceids of the 'FromTraces' for this req
			// 
			String sql = " select id " +
				" from gr_traces where to_requirement_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				int traceId = rs.getInt("id");
				RequirementUtil.deleteTrace(traceId, actorEmailId, securityProfile,  databaseType);
			}
			rs.close();
			prepStmt.close();
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
	}
	

	// called when someone selects to make a trace suspect.
	public static String  makeSuspect(int traceId, String actorEmailId, 
		SecurityProfile securityProfile, String databaseType) {
		String status = "";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			User user = securityProfile.getUser();
			Calendar cal = Calendar.getInstance();
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// get the to and from reqs for this trace and see if this user
			// has permissions to modify traces to / from these objects.
			
			String sql = " select from_requirement_id, to_requirement_id from gr_traces where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, traceId);
			rs = prepStmt.executeQuery();

			int fromRequirementId = 0;
			int toRequirementId = 0;
			if (rs.next()) {
				fromRequirementId = rs.getInt("from_requirement_id");
				toRequirementId = rs.getInt("to_requirement_id");
			}

			prepStmt.close();
			rs.close();
			
			// lets see if the user has 'Trace' permissions to To Req and From Req.
			// if he doesn't then we will need to return with an error message.
			Requirement toRequirement = new Requirement (toRequirementId,  databaseType);
			if (! permittedToCreateTraceTo(toRequirement, securityProfile)){
				status += "<br>You do not have permissions to modify Trace To : " + toRequirement.getRequirementFullTag();
				return (status);							
			}			
			
			Requirement fromRequirement = new Requirement (fromRequirementId,  databaseType);
			if (! permittedToCreateTraceFrom(fromRequirement, securityProfile)){
				status += "<br>You do not have permissions to modify Trace From : " + fromRequirement.getRequirementFullTag();
				return (status);							
			}			

			
			
			
			sql = " update gr_traces set description = ? , suspect=1 where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, "Trace set suspect by " + user.getEmailId() + " at " + cal.getTime());
			prepStmt.setInt(2, traceId);
			prepStmt.execute();

			prepStmt.close();
			con.close();

			// now lets call the method to updateTraceInfo for the requirement.
			// this will updated the traceTo and TraceFrom columns for this req.
			updateTraceInfoForTrace(traceId, actorEmailId,  databaseType);
			
			String log = "Make Trace To Suspect : " + toRequirement.getProjectShortName() +"." +  toRequirement.getRequirementFullTag();
			RequirementUtil.createRequirementLog(fromRequirementId, log, actorEmailId, databaseType);
			
			log = "Make Trace From Suspect : " + fromRequirement.getProjectShortName() +"." +  fromRequirement.getRequirementFullTag();
			RequirementUtil.createRequirementLog(toRequirementId, log, actorEmailId, databaseType);

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
		
		return (status);
	}

	// this method takes a traceId , gets the from / to trace objects and
	// calls the updateTraceInfo method with these reqIds
	// That method inturn gets a string value for the trace info
	// and stores it in the Req's traceTo and TraceFrom columns.
	public static void updateTraceInfoForTrace(int traceId, String actorEmailId, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// get the to and from reqs for this trace and then call the
			// updateTraceInfo method.
			String sql = " select from_requirement_id, to_requirement_id from gr_traces where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, traceId);
			rs = prepStmt.executeQuery();

			int fromRequirementId = 0;
			int toRequirementId = 0;
			if (rs.next()) {
				fromRequirementId = rs.getInt("from_requirement_id");
				toRequirementId = rs.getInt("to_requirement_id");
			}

			// now lets call the method to updateTraceInfo for the requirement.
			// this will updated the traceTo and TraceFrom columns for this req.
			updateTraceInfoForRequirement(fromRequirementId);
			updateTraceInfoForRequirement(toRequirementId);			
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

	}

	// this method takes a requirementId , calls methods to get the from / to
	// trace objects strings
	// and updates the db row for this req with those strings..
	public static void updateTraceInfoForRequirement(int requirementId) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			// lets get the current trace_to and trace_from
			// if they are different , then we update the req
			
			String sql = " select trace_to, trace_from from gr_requirements where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs  = prepStmt.executeQuery();	
			String currentTraceTo  = "";
			String currentTraceFrom = "";
			while (rs.next()){
				currentTraceTo = rs.getString("trace_to");
				currentTraceFrom = rs.getString("trace_from");
			}
			rs.close();
			prepStmt.close();
			
			if (currentTraceTo == null){currentTraceTo = "";}
			if (currentTraceFrom == null){currentTraceFrom = "";}
			
			String traceToString = getTraceToString(requirementId);
			String traceFromString = getTraceFromString(requirementId);

			// get the to and from reqs for this trace and then call the
			// updateTraceInfo method.
			if ( !(
					(currentTraceTo.equals(traceToString))
					&&
					(currentTraceFrom.equals(traceFromString))
					)){
				
					sql = " update gr_requirements set trace_to = ? , trace_from= ? where id = ? ";
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, traceToString);
					prepStmt.setString(2, traceFromString);
					prepStmt.setInt(3, requirementId);
					prepStmt.execute();
			}
			else{	
				//System.out.println("NOT UPDATED requested to update trace info  " + requirementId);
			}
			
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

	}

	// called when something needs a hash table of all attributes in this
	// requirement and this selected values.
	public static ArrayList getAttributeValuesInRequirement(int requirementId) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList rAttributeValues = new ArrayList();

		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " select av.id \"attribute_value_id\", a.id \"attribute_id\", a.system_attribute, " +
				" a.requirement_type_id, av.requirement_id,a.name, a.description, a.type, a.options, " +
				" a.required, a.default_value, a.sort_order, av.value, " +
				" a.impacts_version, a.impacts_traceability, a.impacts_approval_workflow " +
				" from gr_rt_attributes a, gr_r_attribute_values av " + 
				" where  av.requirement_id = ? " + 
				" and av.attribute_id = a.id " + 
				" order by a.sort_order ";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();

			while (rs.next()) {
				int attributeValueId = rs.getInt("attribute_value_id");
				int attributeId = rs.getInt("attribute_id");
				int systemAttribute = rs.getInt("system_attribute");
				int requirementTypeId = rs.getInt("requirement_type_id");
				// we have the requirementId passed in as a parameter. we will
				// use that.
				String attributeName = rs.getString("name");
				String attributeDescription = rs.getString("description");
				String attributeType = rs.getString("type");
				String attributeDropDownOptions = rs.getString("options");
				int attributeRequired = rs.getInt("required");
				String attributeDefaultValue = rs.getString("default_value");
				String attributeSortOrder = rs.getString("sort_order");
				String attributeEnteredValue = rs.getString("value");

				int attributeImpactsVersion  = rs.getInt("impacts_version"); 
				int attributeImpactsTraceability = rs.getInt("impacts_traceability");
				int attributeImpactsApprovalWorkflow = rs.getInt("impacts_approval_workflow");

				
				RAttributeValue rAttributeValue = new RAttributeValue(
						attributeValueId, attributeId, systemAttribute, requirementTypeId,
						requirementId, attributeName, attributeDescription,
						attributeType, attributeDropDownOptions,
						attributeRequired, attributeDefaultValue,
						attributeSortOrder, attributeEnteredValue, 
						attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow  );
				
				rAttributeValues.add(rAttributeValue);
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

		return rAttributeValues;
	}

	// called when a req wants to move. We take in the req id as a param, and
	// return a list of folder objects
	// this a req of this type can move to. This will be displayed as a pull
	// down for the user to select from.
	public static ArrayList getEligibleFolderToMoveTo(int requirementId) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList eligibleFolders = new ArrayList();
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the list of folders that support the requirement
			// type of the requirement parameter.
			//
			String sql = "select f.id, f.project_id, f.name, f.description, f.parent_folder_id," +
					" f.folder_level, f.folder_order, "
					+ "f.folder_path, rt.name \"requirement_type_name\", "
					+ "rt.id \"requirement_type_id\", f.created_by, f.created_dt, f.last_modified_by, f.last_modified_dt "
					+ " from gr_requirements r, gr_folders f , gr_requirement_types rt  "
					+ " where r.id = ? "
					+ " and r.requirement_type_id = f.requirement_type_id"
					+ " and r.requirement_type_id =  rt.id "
					+ " order by f.folder_path  ";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();

			int projectId = 0;
			int folderId = 0;
			String folderName = "";
			String folderDescription = "";
			int parentFolderId = 0;
			int folderLevel = 0;
			int folderOrder = 0;
			String folderPath = "";
			int requirementTypeId = 0;
			String requirementTypeName = "";
			String createdBy = "";
			// Date createdDt;
			String lastModifiedBy = "";
			// Date lastModifiedDt;

			while (rs.next()) {
				folderId = rs.getInt("id");
				projectId = rs.getInt("project_id");
				folderName = rs.getString("name");
				folderDescription = rs.getString("description");
				parentFolderId = rs.getInt("parent_folder_id");
				folderLevel = rs.getInt("folder_level");
				folderOrder = rs.getInt("folder_order");
				folderPath = rs.getString("folder_path");
				requirementTypeId = rs.getInt("requirement_type_id");
				requirementTypeName = rs.getString("requirement_type_name");
				createdBy = rs.getString("created_by");
				// createdDt = rs.getDate("created_dt");
				lastModifiedBy = rs.getString("last_modified_by");
				// lastModifiedDt = rs.getDate("last_modified_by");

				// creating the folder bean.
				Folder folder = new Folder(folderId, projectId, folderName,
						folderDescription, parentFolderId, folderLevel, folderOrder,
						folderPath, requirementTypeId, requirementTypeName,
						createdBy, lastModifiedBy);
				eligibleFolders.add(folder);
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
		return (eligibleFolders);
	}


	// called when a req wants to move. We take in the requirement type id as a param, and
	// return a list of folder objects
	// this a req of this type can move to. This will be displayed as a pull
	// down for the user to select from. Used in Bulk edit.
	public static ArrayList getEligibleFoldersForRequirementType(int requirementTypeId) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList eligibleFolders = new ArrayList();
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the list of folders that support the requirement
			// type of the requirement parameter.
			//
			String sql = "select f.id, f.project_id, f.name, f.description, f.parent_folder_id," +
					" f.folder_level, f.folder_order," 
					+ "f.folder_path, rt.name \"requirement_type_name\", "
					+ "rt.id \"requirement_type_id\", f.created_by, f.created_dt, f.last_modified_by," 
					+ " f.last_modified_dt "
					+ " from  gr_folders f , gr_requirement_types rt  "
					+ " where rt.id = ? "
					+ " and rt.id = f.requirement_type_id " 
					+ " order by f.folder_path ";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			rs = prepStmt.executeQuery();

			int projectId = 0;
			int folderId = 0;
			String folderName = "";
			String folderDescription = "";
			int parentFolderId = 0;
			int folderLevel = 0;
			int folderOrder = 0;			
			String folderPath = "";
			String requirementTypeName = "";
			String createdBy = "";
			// Date createdDt;
			String lastModifiedBy = "";
			// Date lastModifiedDt;

			while (rs.next()) {
				folderId = rs.getInt("id");
				projectId = rs.getInt("project_id");
				folderName = rs.getString("name");
				folderDescription = rs.getString("description");
				parentFolderId = rs.getInt("parent_folder_id");
				folderLevel = rs.getInt("folder_level");
				folderPath = rs.getString("folder_path");
				requirementTypeId = rs.getInt("requirement_type_id");
				requirementTypeName = rs.getString("requirement_type_name");
				createdBy = rs.getString("created_by");
				// createdDt = rs.getDate("created_dt");
				lastModifiedBy = rs.getString("last_modified_by");
				// lastModifiedDt = rs.getDate("last_modified_by");

				// creating the folder bean.
				Folder folder = new Folder(folderId, projectId, folderName,
						folderDescription, parentFolderId, folderLevel, folderOrder,
						folderPath, requirementTypeId, requirementTypeName,
						createdBy, lastModifiedBy);
				eligibleFolders.add(folder);
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
		return (eligibleFolders);
	}
	
	
	// called when the user selects a folder to move a requirement to in the
	// 'Move' prompt of 'Requirement Core' page.
	public static void moveRequirementToAnotherFolder(Requirement requirement,
			int newFolderId, String actorEmailId, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// lets log this event.
			Folder f = new Folder (newFolderId);
			String log = "Moved Requirement to Folder ==>  :  <b>" + f.getFolderPath() + "</b>  From Folder : <b>" + requirement.getFolderPath() + "</b>";
			RequirementUtil.createRequirementLog(requirement.getRequirementId(), log,actorEmailId,  databaseType);

			
			
			String sql = " update gr_requirements set folder_id = ? where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, newFolderId);
			prepStmt.setInt(2, requirement.getRequirementId());

			prepStmt.execute();
			

			
			prepStmt.close();
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
	}

	// This method gets the UDAs for the input requirement from the db
	// and updates the UDA field for this req.
	// A flavor of this exists in the Requirement bean, and is used for similar
	// functionality when the requirement is created.
	// This method is called any time the attributes are changed alone.
	public static void setUserDefinedAttributes(int requirementId,
			String actorEmailId, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;

		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the UDA values and put them in a string.
			String sql = "";
			
			if (databaseType.equals("mySQL")){
				sql = "SELECT concat(a.name, ':#:' , ifnull(v.value,'')) \"uda\" "
					+ " FROM gr_rt_attributes a left join gr_r_attribute_values v on a.id = v.attribute_id"
					+ " where v.requirement_id = ? " + " order by a.sort_order";
			}
			else {
				sql = "SELECT a.name || ':#:' || nvl(v.value,'') \"uda\" "
					+ " FROM gr_rt_attributes a left join gr_r_attribute_values v on a.id = v.attribute_id"
					+ " where v.requirement_id = ? " + " order by a.sort_order";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();

			String userDefinedAttributes = "";

			while (rs.next()) {
				userDefinedAttributes += rs.getString("uda") + ":##:";
			}
			prepStmt.close();
			rs.close();
			
			
			
			// Drop the last ":##:" from the string.
			if (userDefinedAttributes.contains(":##:")) {
				userDefinedAttributes = (String) userDefinedAttributes.subSequence(0, userDefinedAttributes.lastIndexOf(":##:"));
			}

			// update this req's userDefinedAttributes field with this value
			sql = " update gr_requirements set user_defined_attributes = ? where id = ? ";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, userDefinedAttributes);
			prepStmt.setInt(2, requirementId);
			prepStmt.execute();


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
	}

	// The very long method name should explain everything.
	// called when we add a new attribute to the RT, so we need to go through
	// every req of this type
	// find it's UD attributes and update the user_defined_attribute field in
	// the db.
	// 
	// A flavor of this exists in the Requirement bean, and is used for similar
	// functionality when the requirement is created.
	// This method is called any time the attributes are changed alone.
	// NOTE : if you run into performance issues, remember this makes one db
	// call for every req in the
	// req type. we may consider moving this to be a stored proc.
	public static void setUserDefinedAttributesForAllRequirementsInRT(
			int requirementTypeId, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select id from gr_requirements where requirement_type_id = ?";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			rs = prepStmt.executeQuery();

			while (rs.next()) {
				int requirementId = rs.getInt("id");

				// lets get the UDA values and put them in a string.
				if (databaseType.equals("mySQL")){
					sql = "SELECT concat(a.name, ':#:' , ifnull(v.value,'')) \"uda\" "
						+ " FROM gr_rt_attributes a left join gr_r_attribute_values v on a.id = v.attribute_id"
						+ " where v.requirement_id = ? "
						+ " order by a.sort_order";
				}
				else {
					sql = "SELECT a.name || ':#:' || nvl(v.value,'') \"uda\" "
						+ " FROM gr_rt_attributes a left join gr_r_attribute_values v on a.id = v.attribute_id"
						+ " where v.requirement_id = ? "
						+ " order by a.sort_order";
				}
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1, requirementId);
				ResultSet rs2 = prepStmt2.executeQuery();

				String userDefinedAttributes = "";

				while (rs2.next()) {
					userDefinedAttributes += rs2.getString("uda") + ":##:";
				}
				// Drop the last ":##:" from the string.
				if (userDefinedAttributes.contains(":##:")){
					userDefinedAttributes = (String) userDefinedAttributes.subSequence(0, userDefinedAttributes.lastIndexOf(":##:"));
				}
				// update this req's userDefinedAttributes field with this value
				sql = " update gr_requirements set user_defined_attributes = ? where id = ? ";

				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setString(1, userDefinedAttributes);
				prepStmt2.setInt(2, requirementId);
				prepStmt2.execute();

				prepStmt2.close();
				rs2.close();
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
	}

	// this method creates an entry in the requirement log table.
	public static void createRequirementLog(int requirementId,
			String description, String actorEmailId, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " Insert into gr_requirement_log (requirement_id,  "
					+ " description, actor_email_id, action_dt)"
					+ "values (? , ? , ? ,now()) ";
			}
			else {
				sql = " Insert into gr_requirement_log (requirement_id,  "
					+ " description, actor_email_id, action_dt)"
					+ "values (? , ? , ? ,sysdate) ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.setString(2, description);
			prepStmt.setString(3, actorEmailId);
			prepStmt.execute();
			prepStmt.close();
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
	}
	public static void notifyOldOwner(User user, HttpServletRequest request, int requirementId, String oldOwnerEmailId,  String newOwnerEmailId, 
			String projectName, String requirementFullTag, String requirementName, 
			String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort, String emailUserId, String emailPassword) {
			try {
				
				// see if this req belongs to a type that wants to notify owners
				int notifyOnOwnerChange = getNotifyOwnerOnChange(requirementId);
				if (notifyOnOwnerChange == 0 ){
					return;
				}
				
				// lets email the new owner
				String url = ProjectUtil.getURL(request, requirementId,"requirement");
				
				String to = oldOwnerEmailId;
				String cc = "";
				String subject = requirementFullTag + " has been assigned to a new owner "  ;
				String message = "<br>" + 
				"<html><body>Hello, <br><br>" + 
				user.getFirstName() + " " + user.getLastName() + " has made " + newOwnerEmailId + " the Owner of this object : <br><br>" +
				" Object : <a href='"+ url  +"'>" + projectName + " : " + requirementFullTag + "</a><br>" + 
				" Name : " + requirementName + "<br><br>" +
				" From now on the metrics about this object will start showing up in the new owner's dashboard. <br><br> This will no longer show up in your metrics <br><br>" +
				" Best Regards <br><br>" + 
				" TraceCloud Administrator </body></html>";
				
				// users may enter email ids separated by space or semicolon. lets 
				// make them all comma separated
				if (to.trim().contains(" ")){
					to = to.replace(' ', ',');
				}
				if (to.trim().contains(";")){
					to = to.replace(';', ',');
				}
				if (cc.trim().contains(" ")){
					cc = cc.replace(' ', ',');
				}
				if (cc.trim().contains(";")){
					cc = cc.replace(';', ',');
				}
				

				// lets send the email out to the toEmailId;
				ArrayList toArrayList = new ArrayList();
				if (to != null){
					to = to.trim();
					if (!to.equals("")){
						if (to.contains(",")){
							String [] toEmails = to.split(",");
							for (int i=0; i < toEmails.length; i++ ){
								toArrayList.add(toEmails[i]);
							}
						}
						else {
							toArrayList.add(to);
						}
					}
				}
				
				ArrayList ccArrayList = new ArrayList();
				if (cc != null){
					cc = cc.trim();
					if (!cc.equals("")){
						if (cc.contains(",")){
							String [] ccEmails = cc.split(",");
							for (int i=0; i < ccEmails.length; i++ ){
								ccArrayList.add(ccEmails[i]);
							}
						}
						else {
							ccArrayList.add(cc);
						}
					}
				}
				MessagePacket mP = new MessagePacket(toArrayList, ccArrayList, subject, message, "");
				
				EmailUtil.email(mP, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword); 

			} catch (Exception e) {
				
				e.printStackTrace();
			} finally {
			}
		}
		

	public static void notifyNewOwner(User user, HttpServletRequest request, int requirementId, String emailId, String projectName, String requirementFullTag, String requirementName, 
		String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort, String emailUserId, String emailPassword) {
		try {
			// see if this req belongs to a type that wants to notify owners
			int notifyOnOwnerChange = getNotifyOwnerOnChange(requirementId);
			if (notifyOnOwnerChange == 0 ){
				return;
			}
			// lets email the new owner
			String url = ProjectUtil.getURL(request, requirementId,"requirement");
			
			String to = emailId;
			String cc = "";
			String subject = "You have been made the Owner of " + requirementFullTag;
			String message = "<br>" + 
			"<html><body>Hello, <br><br>" + 
			user.getFirstName() + " " + user.getLastName() + " has made you the Owner of this object : <br><br>" +
			" Object : <a href='"+ url  +"'>" + projectName + " : " + requirementFullTag + "</a><br>" + 
			" Name : " + requirementName + "<br><br>" +
			" From now on the metrics about this object will start showing up in your dashboard. <br><br>" +
			" Best Regards <br><br>" + 
			" TraceCloud Administrator </body></html>";
			
			// users may enter email ids separated by space or semicolon. lets 
			// make them all comma separated
			if (to.trim().contains(" ")){
				to = to.replace(' ', ',');
			}
			if (to.trim().contains(";")){
				to = to.replace(';', ',');
			}
			if (cc.trim().contains(" ")){
				cc = cc.replace(' ', ',');
			}
			if (cc.trim().contains(";")){
				cc = cc.replace(';', ',');
			}
			

			// lets send the email out to the toEmailId;
			ArrayList toArrayList = new ArrayList();
			if (to != null){
				to = to.trim();
				if (!to.equals("")){
					if (to.contains(",")){
						String [] toEmails = to.split(",");
						for (int i=0; i < toEmails.length; i++ ){
							toArrayList.add(toEmails[i]);
						}
					}
					else {
						toArrayList.add(to);
					}
				}
			}
			
			ArrayList ccArrayList = new ArrayList();
			if (cc != null){
				cc = cc.trim();
				if (!cc.equals("")){
					if (cc.contains(",")){
						String [] ccEmails = cc.split(",");
						for (int i=0; i < ccEmails.length; i++ ){
							ccArrayList.add(ccEmails[i]);
						}
					}
					else {
						ccArrayList.add(cc);
					}
				}
			}
			MessagePacket mP = new MessagePacket(toArrayList, ccArrayList, subject, message, "");
			
			EmailUtil.email(mP, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword); 

		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
		}
	}
	
	// this method creates an entry requirement_attachments table.
	public static void addRequirementAttachment(int requirementId, String attachmentFileName, 
		String attachmentFilePath, String title, String actorEmailId, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " Insert into gr_requirement_attachments (requirement_id,  "
					+ " file_name ,file_path, title, created_by, created_dt) "
					+ "values (? , ? , ? , ? , ? ,now()) ";
			}
			else {
				sql = " Insert into gr_requirement_attachments (requirement_id,  "
					+ " file_name ,file_path, title, created_by, created_dt) "
					+ "values (? , ? , ? , ? , ? , sysdate) ";

			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.setString(2, attachmentFileName);
			prepStmt.setString(3, attachmentFilePath);
			prepStmt.setString(4, title);
			prepStmt.setString(5, actorEmailId);
			prepStmt.execute();
			prepStmt.close();
			con.close();
			
			createRequirementLog(requirementId, "Attached file " + attachmentFileName + " to requirement ",
				actorEmailId,  databaseType);
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
	}

	public static void updateRequirementAttachment(int attachmentId, int requirementId, String attachmentFileName, 
			String attachmentFilePath, String actorEmailId, String databaseType) {
			PreparedStatement prepStmt = null;
			ResultSet rs = null;
			java.sql.Connection con = null;
			try {

				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource = (javax.sql.DataSource) context
						.lookup("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();

				String sql = "";
				sql = " update  gr_requirement_attachments set file_name = ? , file_path = ? ,  created_by = ? , created_dt = now() "
						+ " where id = ? ";				prepStmt = con.prepareStatement(sql);
				
				prepStmt.setString(1, attachmentFileName);
				prepStmt.setString(2, attachmentFilePath);
				prepStmt.setString(3, actorEmailId);
				prepStmt.setInt(4, attachmentId);
				
				prepStmt.execute();
				prepStmt.close();
				con.close();
				
				createRequirementLog(requirementId, "Update attachment .   file is " + attachmentFileName + " to requirement ",
					actorEmailId,  databaseType);
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
		}

	public static void updateRequirementAttachmentTitle(int attachmentId, int requirementId, String title, String actorEmailId, String databaseType) {
			PreparedStatement prepStmt = null;
			ResultSet rs = null;
			java.sql.Connection con = null;
			try {

				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource = (javax.sql.DataSource) context
						.lookup("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();

				String sql = "";
				sql = " update  gr_requirement_attachments set title = ?  "
						+ " where id = ? ";				
				prepStmt = con.prepareStatement(sql);
				
						
				prepStmt.setString(1, title);
				prepStmt.setInt(2, attachmentId);
				
				prepStmt.execute();
				prepStmt.close();
				con.close();
				
				createRequirementLog(requirementId, "Update attachment Title . " + title + " to requirement ",
					actorEmailId,  databaseType);
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
		}
	
	
	// this method creates an entry requirement_attachments table.
	public static void addExistingFileToRequirement(int requirementId, int attachmentId, String actorEmailId) {

		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			RequirementAttachment attachment = new RequirementAttachment(attachmentId, "mySQL");
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "insert into gr_requirement_attachments (requirement_id, file_name, file_path, title, created_by, created_dt) " + 
					" select ? , file_name, file_path, title, ? , now() from gr_requirement_attachments where id = ?";
				prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.setString(2, actorEmailId);
			prepStmt.setInt(3, attachmentId);
			prepStmt.execute();
			prepStmt.close();
			con.close();
			
			createRequirementLog(requirementId, "Attached file " + attachment.getFileName() + " to requirement ",
				actorEmailId,  "mySQL");
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
	}
	
	
	// this method remove the requirement attachment.
	public static void deleteRequirementAttachment(int requirementId, String attachmentFileName, 
		int attachmentId, String actorEmailId, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " delete from gr_requirement_attachments "
					+ " where id = ?  ";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, attachmentId);
			prepStmt.execute();
			prepStmt.close();
			con.close();
			
			createRequirementLog(requirementId, "Deleted file " + attachmentFileName + " from requirement ", 
				actorEmailId,  databaseType);
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
	}


	// when called with a requirementId, it returns an array list of Strings
	// containing the
	// requirement change logs.
	public static ArrayList getRequirementChangeLog(int requirementId, String databaseType) {

		ArrayList requirementChangeLog = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the list changelogs for this requirement.
			// it then creates a string objects and adds it to the arraylist.
			// 
			//
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "select concat(action_dt, ':--:',actor_email_id,':--:', description) \"log\" "
					+ " from gr_requirement_log "
					+ " where requirement_id = ? "
					+ " order by action_dt desc ";
			}
			else {
				sql = "select action_dt || ':--:' || actor_email_id || ':--:' || description \"log\" "
					+ " from gr_requirement_log "
					+ " where requirement_id = ? "
					+ " order by action_dt desc ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);

			rs = prepStmt.executeQuery();
			while (rs.next()) {
				String log = rs.getString("log");
				requirementChangeLog.add(log);
			}

			rs.close();
			prepStmt.close();
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
		return (requirementChangeLog);
	}
	
	
	public static ArrayList getRequirementChangeLogGMTDelta(int requirementId, String databaseType, Double gmtDelta) {

		ArrayList requirementChangeLog = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the list changelogs for this requirement.
			// it then creates a string objects and adds it to the arraylist.
			// 
			//
			String sql = "";
				sql = "select concat(date_format(date_add(action_dt, INTERVAL ? MINUTE ) , '%d %M %Y %h %i %p '), ':--:',actor_email_id,':--:', description) \"log\" "
					+ " from gr_requirement_log "
					+ " where requirement_id = ? "
					+ " order by action_dt desc ";
			
				prepStmt = con.prepareStatement(sql);
				prepStmt.setDouble(1, gmtDelta);
			prepStmt.setInt(2, requirementId);

			rs = prepStmt.executeQuery();
			while (rs.next()) {
				String log = rs.getString("log");
				requirementChangeLog.add(log);
			}

			rs.close();
			prepStmt.close();
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
		return (requirementChangeLog);
	}
	// when called with a requirementId, it returns an array list of Comment objects.
	public static ArrayList getRequirementComments(int requirementId, String databaseType) {

		ArrayList requirementComments = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the list of Comments for this requirement.
			// it then creates a Comment objects and adds it to the arraylist.
			// 
			//
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "select id,  version, commenter_email_id, comment_note," +
					" date_format(comment_dt, '%d %M %Y %r ') \"formatted_comment_dt\" " + 
					" from gr_requirement_comments " +
					" where requirement_id = ? " + 
					" order by version desc, comment_dt desc ";
			}
			else {
				sql = "select id,  version, commenter_email_id, comment_note," +
				" to_char(comment_dt, 'DD MON YYYY') \"formatted_comment_dt\" " + 
				" from gr_requirement_comments " +
				" where requirement_id = ? " + 
				" order by version desc, comment_dt desc ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);

			rs = prepStmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				int version = rs.getInt("version");
				String commenterEmailId = rs.getString("commenter_email_id");
				String comment_note = rs.getString("comment_note");
				String commentDate = rs.getString("formatted_comment_dt");
				
				Comment commentObject = new Comment(id,requirementId, version, commenterEmailId, comment_note, commentDate);
				requirementComments.add(commentObject);
			}

			rs.close();
			prepStmt.close();
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
		return (requirementComments);
	}
	
	
	public static ArrayList getRequirementCommentsGMTDelta(int requirementId, String databaseType, Double gmtDelta) {

		ArrayList requirementComments = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the list of Comments for this requirement.
			// it then creates a Comment objects and adds it to the arraylist.
			// 
			//
			String sql = "";
				sql = "select id,  version, commenter_email_id, comment_note," +
					" date_format(date_add(comment_dt, INTERVAL ? MINUTE ) , '%d %M %Y %h %i %p ') \"formatted_comment_dt\" " + 
					" from gr_requirement_comments " +
					" where requirement_id = ? " + 
					" order by version desc, comment_dt desc ";
			prepStmt = con.prepareStatement(sql);
			
			prepStmt.setDouble(1, gmtDelta);
			prepStmt.setInt(2, requirementId);

			rs = prepStmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				int version = rs.getInt("version");
				String commenterEmailId = rs.getString("commenter_email_id");
				String comment_note = rs.getString("comment_note");
				String commentDate = rs.getString("formatted_comment_dt");
				
				Comment commentObject = new Comment(id,requirementId, version, commenterEmailId, comment_note, commentDate);
				requirementComments.add(commentObject);
			}

			rs.close();
			prepStmt.close();
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
		return (requirementComments);
	}
	
	
	public static int getRequirementCommentsCount(int requirementId, String databaseType) {

		int requirementCommentsCount = 0;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the list of Comments for this requirement.
			// it then creates a Comment objects and adds it to the arraylist.
			// 
			//
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "select count(*) 'requirementCommentsCount'  " + 
					" from gr_requirement_comments " +
					" where requirement_id = ? ";
				
				
			}
			else {
				sql = "select count(*) 'requirementCommentsCount' " + 
				" from gr_requirement_comments " +
				" where requirement_id = ? ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);

			rs = prepStmt.executeQuery();
			while (rs.next()) {
				requirementCommentsCount = rs.getInt("requirementCommentsCount");
			}

			rs.close();
			prepStmt.close();
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
		return (requirementCommentsCount);
	}
	
	
	// sets the requirement attribute value for a requirementid/attribute id combinations.
	// a flavor of this exists in RTAttributeValue bean. The method below is used for bulk
	// updating.
	public static void updateRequirementAttribute(int requirementId, int attributeId, 
		String attributeValue, String updateEmailId, String databaseType) {
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
	
			// lets get the UDA values and put them in a string.
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " update gr_r_attribute_values set value = ? , created_by = ? , " +
					" created_dt = now(), last_modified_by = ? , last_modified_dt = now() " +
					" where requirement_id = ? " +
					" and attribute_id = ? ";
			}
			else {
				sql = " update gr_r_attribute_values set value = ? , created_by = ? , " +
				" created_dt = sysdate, last_modified_by = ? , last_modified_dt = sysdate " +
				" where requirement_id = ? " +
				" and attribute_id = ? ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, attributeValue);
			prepStmt.setString(2, updateEmailId);
			prepStmt.setString(3, updateEmailId);
			prepStmt.setInt(4, requirementId);
			prepStmt.setInt(5, attributeId);

			prepStmt.execute();

			prepStmt.close();
			con.close();
			
			RAttributeValue rAV  = new RAttributeValue(requirementId  , attributeId,  databaseType );
			String log = "Updated attribute" + rAV.getAttributeName() +" to " +  attributeValue;
			RequirementUtil.createRequirementLog(requirementId, log, updateEmailId, databaseType);
			
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
	}


	
	// creates a new entry in the version table for this req.
	public static void createRequirementVersion(int requirementId) {
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
	

			String sql = "insert into gr_requirement_versions " +
				" (requirement_id, version, name, description," +
				" approvers, status, priority, owner, pct_complete, external_url, trace_to, trace_from, " +
				" user_defined_attributes, created_by, created_dt) " +
				" select id, version, name, description, " +
				" approvers, status, priority, owner, pct_complete, external_url, trace_to, trace_from, " +
				" user_defined_attributes, last_modified_by, last_modified_dt " + 
				" from gr_requirements " +  
				" where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,requirementId);
			prepStmt.execute();


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
	}


	
	
	
	
	
	

	// returns true if approvers exist for this req, else false
	
	public static boolean approversForRequirementExist(int requirementId) {
		
		boolean approversExist = false;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql  = " select count(*) \"matches\"" +
				" from gr_requirements r, gr_role_privs rp, " +
				" gr_user_roles ur, gr_users u " +
				" where r.id = ?" +
				" and r.folder_id = rp.folder_id " +
				" and rp.approve_requirement = 1 " +
				" and rp.role_id = ur.role_id " +
				" and ur.user_id = u.id ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,requirementId);
			rs = prepStmt.executeQuery();
			
			int matches =  0;
			while (rs.next()) {
				matches = rs.getInt("matches");
			}
			prepStmt.close();
			con.close();
			
			if (matches > 0 ) {
				approversExist = true;
			}
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
		return (approversExist);
	}

	
	
	
	
	
	
	
	
	
	// this does a few things.
	// 1. removes all approvers who are not in the currently valid list of approvers. This takes care
	// of people who have dropped out of the approval list and keeps the list fresh. Self correcting.
	// 2. goes to role_privs, user_roles to figure out the list of approvers and add the ones that are not
	// currently in the list , to this requirement.
	// 3. updates requirement.approvers field with a summary of approval status for this req.
	//
	// ApprovalRank : 
	//	Roles have rank. We want too process the roles of lowest rank , before moving to roles of next rank
	//  we work through till we have processed all the roles. 
	// Step 1 : Remove all approvers who are not in the currently valid list of approvers
	// Step 2 : Find any newly added approvers to the eligible roles and add them to the approval list
	// Step 3 : Update requiremnts.approvers field.
	//  
	// or we can achieve all of this by a) Find the next role that needs to approve. 
	// b) ensure that they are the ONLY ones who are left in the approval pool for this object.

	/*
	 * 
	 * 	public static void refreshRequirementApprovalHistory(int requirementId, String databaseType) { 
		
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			Requirement requirement = new Requirement(requirementId,  databaseType);
			
			
			

			// if there are any rejects, we just remove the pending approvers and get out .
			int rejected = getRequirementApprovalRejects(con, requirement);

			if(rejected > 0 ){
				// we have at least 1 reject. so lets remove all pending 
				
				removeRequirementPendingAppovers(con, requirement);
			}
			else {

				
				// Lets simplify this.
				// 1. getNextSetOfApprovers
				// 2. Remove any one from RAH who is not in the nextSetOfApprovers
				// 3. Add any one who who is missing from nextSetOfApprovers in RAH.
				
				Role nextRole = requirement.getNextRoleToApprove();
				// IMPORTANT IMPORTANT IMPORTANT IMPORTANT
				// WHEN YOU CALL getNextRoleToApprove, it calls 
				// getApprovalByROle for all the role. Part of that code calls
				// RefreshRequirementApprovalHistory for every role. 
				// so this code keeps self correcting the approvers for any requirement.
				// IMPORTANT IMPORTANT IMPORTANT IMPORTANT
				
			}
			
			
			setRequirementApprovalStringBasedOnRAH(con,  requirement);
			
			
			

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
	}
	 */
	
	
	public static void refreshRequirementApprovalHistory(int requirementId, String databaseType) { 
		
		/*
		 * 
		 	 getStackedApprovalRoles
				 For each stacked Role
					 Insert new approvers from this role
					 Delete not existing approvers
			 setPendingWaiting
			 setRequirementApprovalStringBasedOnRAH

		 * 
		 */
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			Requirement requirement = new Requirement(requirementId,  databaseType);
	
			setRAHApproverListForAllRoles(requirement);
			setPendingWaiting(requirement);
			
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
	}

	
	public static void setRAHApproverListForAllRoles(Requirement requirement) {
		System.out.println("srt in setRAHApproverListForAllRoles for   " + requirement.getRequirementFullTag()  );


		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			
			
			
			
			
			// here is the logic:
			// delete from approval table, all approvers for this req / version, 
			// who are currently on the list but should not be
			
			String sql = "";

			
			// get all the roles that can approve this req. This includes static roles and dynamic ones.. 
			
			sql = " select rl.id, rl.name "
					+ " from gr_roles rl, gr_role_privs rp "
					+ " where rl.id = rp.role_id  "
					+ " and rp.approve_requirement = 1 "
					+ " and rp.folder_id = ?  "
					+ " union "
					+ "  select rl.id, rl.name "
					+ " from gr_dynamic_roles dr, gr_roles rl "
					+ " where dr.requirement_id = ? "
					+ " and dr.role_id = rl.id "
					;
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirement.getFolderId());
			prepStmt.setInt(2, requirement.getRequirementId());
			rs = prepStmt.executeQuery();
			String roleString = "";
			while (rs.next()){
				String roleName = rs.getString("name");
				System.out.println("srt approver role for req " + requirement.getRequirementFullTag() + " is " + roleName  );
				roleString += rs.getInt("id") + " ,";
			}
			rs.close();
			prepStmt.close();
			
			// lets drop the last , 
		    if (roleString.contains(",")){
		    	roleString = (String) roleString.subSequence(0,roleString.lastIndexOf(","));
		    }
			
			
			sql = 
					"		select rah.approver_email_id  " +
					" 		from gr_requirement_approval_h rah " +
					"		where rah.requirement_id = ? " +
					"		and rah.version = ? "+ 
					"		and rah.approver_email_id not in ( " +
						"		select u.email_id " +
						"		from  gr_user_roles ur, gr_users u " +
						" 		where ur.role_id in (" + roleString + ") " +  
						"		and ur.user_id = u.id" +
						"	)";
			prepStmt = con.prepareStatement(sql);
			
			System.out.println("srt sql for approvers in RAH but not in roles  " + sql  + "\n" );

			
			
			String sql2 = " delete from gr_requirement_approval_h " + 
				" where requirement_id = ? " +
				" and version  = ? " + 
				" and approver_email_id = ? ";
			PreparedStatement prepStmt2 = con.prepareStatement(sql2);
			
			prepStmt.setInt(1,requirement.getRequirementId() );
			prepStmt.setInt(2,requirement.getVersion());
			
			rs = prepStmt.executeQuery();
			while (rs.next()){
				String approverEmailId = rs.getString("approver_email_id");
				
				System.out.println("srt removing approver from RAH as he is not in current valid list   " + approverEmailId  + "\n" );

				// this approver is not longer valid. so so lets remove him.
				prepStmt2.setInt(1,requirement.getRequirementId());
				prepStmt2.setInt(2,requirement.getVersion());
				prepStmt2.setString(3,approverEmailId);
				prepStmt2.execute();
			}
			rs.close();
			prepStmt2.close();
			prepStmt.close();

			
			
			
			
		
			
			// list of email id that exist in valid approval list, but are not in the current approval list.
			// These will be added to the current list.
			sql = " insert into gr_requirement_approval_h  " +
			" 	(requirement_id, version, approver_email_id) " +
			"		select distinct "  + requirement.getRequirementId() + "," +  requirement.getVersion() + 	" , u.email_id " +
			"		from  gr_user_roles ur, gr_users u " +
			" 		where ur.role_id in (" + roleString + ") " + 			
			"		and ur.user_id = u.id  " +
			"		and u.email_id not in ( " +
				"		select rah.approver_email_id  " +
				"		from gr_requirement_approval_h rah " +	
				"		where rah.requirement_id = ? " +
				"		and rah.version = ? " +
				"	)  ";


			System.out.println("srt sql for inserting approvers in in roles but not in RAH " + sql  + "\n" );

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,requirement.getRequirementId());
			prepStmt.setInt(2,requirement.getVersion());
			prepStmt.execute();
	
			
			prepStmt.close();
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
	}

	
	

	// For this requirement, it removes from the RAH table any users who are NOT in the nextRole
	// and adds any users who are in nextRole to RAH
	public static void setRAHApproverListForARole(Requirement requirement, Role role) {
		

		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			
			
			
			
			
			// here is the logic:
			// delete from approval table, all approvers for this req / version, 
			// who are currently on the list but should not be
			// and haven't still made a decision
			
			// lets identify and loop through all approvers that need to be deleted.
			String sql = "";

			
			// lets see if there are any roles with same rank who have approval privileges on this folder. If so,lets make a roleString comprising all roles. 
			
			sql = " select rl.id "
					+ " from gr_roles rl, gr_role_privs rp "
					+ " where rl.id = rp.role_id  "
					+ " and rl.project_id = ? and rl.approval_rank = ? "
					+ " and rp.approve_requirement = 1 "
					+ " and rp.folder_id = ? "
					;
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, role.getProjectId());
			prepStmt.setInt(2, role.getApprovalRank() );
			prepStmt.setInt(3, requirement.getFolderId());
			rs = prepStmt.executeQuery();
			String roleString = "";
			while (rs.next()){
				roleString += rs.getInt("id") + " ,";
			}
			rs.close();
			prepStmt.close();
			
			// lets drop the last , 
		    if (roleString.contains(",")){
		    	roleString = (String) roleString.subSequence(0,roleString.lastIndexOf(","));
		    }
			
			
			sql = 
					"		select rah.approver_email_id  " +
					" 		from gr_requirement_approval_h rah " +
					"		where rah.requirement_id = ? " +
					"		and rah.version = ? "+ 
					" 		and rah.response = 'Pending'" +
					"		and rah.approver_email_id not in ( " +
						"		select u.email_id " +
						"		from  gr_user_roles ur, gr_users u " +
						" 		where ur.role_id in (" + roleString + ") " +  
						"		and ur.user_id = u.id" +
						"	)";
			prepStmt = con.prepareStatement(sql);
			
			
			
			String sql2 = " delete from gr_requirement_approval_h " + 
				" where requirement_id = ? " +
				" and version  = ? " + 
				" and approver_email_id = ? ";
			PreparedStatement prepStmt2 = con.prepareStatement(sql2);
			
			prepStmt.setInt(1,requirement.getRequirementId() );
			prepStmt.setInt(2,requirement.getVersion());
			
			rs = prepStmt.executeQuery();
			while (rs.next()){
				String approverEmailId = rs.getString("approver_email_id");
				
				
				// this approver is not longer valid. so so lets remove him.
				prepStmt2.setInt(1,requirement.getRequirementId());
				prepStmt2.setInt(2,requirement.getVersion());
				prepStmt2.setString(3,approverEmailId);
				prepStmt2.execute();
			}
			rs.close();
			prepStmt2.close();
			prepStmt.close();

			
			
			
			
		
			
			// list of email id that exist in valid approval list, but are not in the current approval list.
			// These will be added to the current list.
			sql = " insert into gr_requirement_approval_h  " +
			" 	(requirement_id, version, approver_email_id) " +
			"		select distinct "  + requirement.getRequirementId() + "," +  requirement.getVersion() + 	" , u.email_id " +
			"		from  gr_user_roles ur, gr_users u " +
			" 		where ur.role_id in (" + roleString + ") " + 			
			"		and ur.user_id = u.id  " +
			"		and u.email_id not in ( " +
				"		select rah.approver_email_id  " +
				"		from gr_requirement_approval_h rah " +	
				"		where rah.requirement_id = ? " +
				"		and rah.version = ? " +
				"	)  ";


			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,requirement.getRequirementId());
			prepStmt.setInt(2,requirement.getVersion());
			prepStmt.execute();
	
			
			prepStmt.close();
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
	}

	
	public static ArrayList<Role> getStackedApprovalRoles(Requirement requirement) {
		System.out.println("srt in getStackedApprovalRole for   " + requirement.getRequirementFullTag()  );


		ArrayList<Role> privilegedRoles = new ArrayList<Role>();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			
			// this is a 2 step process.
			// 1. get the stacked approval roles for this folder
			// 2. get the stacked approval roles for this req. 
			String sql = " select r.id, r.project_id, r.name, r.description, r.approval_type, r.approval_rank "  +
					" from gr_role_privs g, gr_roles r " +
					" where g.folder_id = ?  " + 
					" and g.role_id = r.id " +
					" and g.approve_requirement =1 " + 
					"  union " +
					" select rl.id, rl.project_id, rl.name, rl.description, rl.approval_type, dr.approval_rank " +    
					" from gr_dynamic_roles dr, gr_roles rl   "+
					" where dr.requirement_id  = ?    " +
					" and dr.role_id = rl.id  " +
					"  order by approval_rank " ;
				

				
				 prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirement.getFolderId());
				prepStmt.setInt(2, requirement.getRequirementId());
				 rs = prepStmt.executeQuery();
				while (rs.next()){

					int roleId = rs.getInt("id");
					int projectId = rs.getInt("project_id");
					String roleName = rs.getString("name");
					String roleDescription = rs.getString ("description");
					String approvalType = rs.getString("approval_type");
					int approvalRank = rs.getInt("approval_rank");
					
					Role role = new Role(roleId ,projectId, roleName,roleDescription, approvalType, approvalRank);
					privilegedRoles.add(role);
				}
				
				rs.close();
				prepStmt.close();
				con.close();
			
			return(privilegedRoles);

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
		return privilegedRoles;
	}

	public static void setPendingWaiting(Requirement requirement) {
		System.out.println("srt in setPendingWaiting for   " + requirement.getRequirementFullTag()  );


		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			
			
			/*
			 * 
			 * 	 getStackedApprovalRoles
					For each stacked Role
						 Find which role is pending
					 Any role above the pending role level , set approver status to Waiting

			 * 
			 */
			//Folder f = new Folder(requirement.getFolderId());
			//ArrayList<Role> approvalRoles = f.getStackedApprovalRolesForFolder();
			ArrayList<Role> approvalRoles = requirement.getStackedApprovalRoles();
			Role currentActiveApprovalRoleForReq = requirement.getNextRoleToApprove();
			// if currentapprovalroleforreq is null, just return. No more work to be done.
			// if currentApprovalRoleForReq is NOT null , then we have to make sure that all waiting for this req are set to pending
			// esp if they have not yet approed for this req.
			// 
			
			if (currentActiveApprovalRoleForReq==null){
				// return a null. There are no more roles that require approval
				System.out.println("srt in currentActiveApprovalRoleForReq is null . so nothing to do   "   );

				return;
			}
			
			
			String currentActiveRoleMembers = "";
			ArrayList<User> currentActiveRoleMembersArray =RoleUtil.getAllUsersInRole(currentActiveApprovalRoleForReq.getRoleId(), "mySQL");
			for (User u : currentActiveRoleMembersArray ){
				currentActiveRoleMembers += u.getEmailId() + ",";
			}
			
			// for each role, find out if the RoleApproval need is met
			// If Yes, go to the next role
			// If no, return this role.
			ArrayList<String> approversToBeSetWaiting = new ArrayList<String>();
			ArrayList<String> approversToBeSetPending = new ArrayList<String>();
			
			for (Role approvalRole : approvalRoles){

				if (approvalRole.getApprovalRank() == currentActiveApprovalRoleForReq.getApprovalRank() ){

					// find all members of this role who have not approved. i.e in waiting and not aproved yet and set them to pending
					String sql = "select rah.response , ifnull(rah.approved_roles,'') 'approved_roles' "
							+ " from gr_requirement_approval_h rah "
							+ " where rah.requirement_id = ? "
							+ " and rah.version = ? "
							+ " and rah.approver_email_id = ? "
							+ " and rah.response = 'Waiting'  ";
					
					prepStmt = con.prepareStatement(sql);
					
					ArrayList<User> members =RoleUtil.getAllUsersInRole(approvalRole.getRoleId(), "mySQL");
					
					for (User u: members){


						prepStmt.setInt(1, requirement.getRequirementId());
						prepStmt.setInt(2, requirement.getVersion());
						prepStmt.setString(3, u.getEmailId());
						
						rs = prepStmt.executeQuery();
						String response = "";
						String approvedRoles = "";
						while (rs.next()){
							response = rs.getString("response");
							approvedRoles = rs.getString("approved_roles");
							if (approvedRoles == null ) {approvedRoles = "";}
							if (!(approvedRoles.contains(currentActiveApprovalRoleForReq.getRoleName() ))){
								// this user has alrady approved for the currently active role, so let set him to Waiting
	
								approversToBeSetPending.add(u.getEmailId());
	
							}
						}						
					}
					prepStmt.close();
					
				}
				if (approvalRole.getApprovalRank() > currentActiveApprovalRoleForReq.getApprovalRank() ){
					// look at all the members of this role.
					// if they are in current role and have an entry in 'approved_roles for currentRole',
					// then they have approved the current role, so set them waiting
					// if they are not in current role, then they are in some future role, So set them to waiting
					ArrayList<User> members =RoleUtil.getAllUsersInRole(approvalRole.getRoleId(), "mySQL");
					String sql = "select rah.response , ifnull(rah.approved_roles,'') 'approved_roles' "
							+ " from gr_requirement_approval_h rah "
							+ " where rah.requirement_id = ? "
							+ " and rah.version = ? "
							+ " and rah.approver_email_id = ? ";
					
					prepStmt = con.prepareStatement(sql);

					for (User u: members){
						if (currentActiveRoleMembers.contains(u.getEmailId())){
							// if they have alreay approved for currentActiveRole, then lets add them to approversToBeSetWaiting
							
							prepStmt.setInt(1, requirement.getRequirementId());
							prepStmt.setInt(2, requirement.getVersion());
							prepStmt.setString(3, u.getEmailId());
							rs = prepStmt.executeQuery();
							String response = "";
							String approvedRoles = "";
							while (rs.next()){
								response = rs.getString("response");
								approvedRoles = rs.getString("approved_roles");
								if (approvedRoles.contains(currentActiveApprovalRoleForReq.getRoleName() )){
									// this user has alrady approved for the currently active role, so let set him to Waiting
	
									approversToBeSetWaiting.add(u.getEmailId());
	
								}
							}
						}
						else {
							// Not current active role. So can be made waiting
							approversToBeSetWaiting.add(u.getEmailId());
						}
					}
					prepStmt.close();
				}
			}

			// lets loop through all the RAH approvers for this req and version and set the status to Waiting.
			String sql = " update gr_requirement_approval_h rah "
					+ " set rah.response ='Waiting' "
					+ " where rah.requirement_id = ? "
					+ " and rah.version = ? "
					+ " and rah.approver_email_id = ? ";
			prepStmt = con.prepareStatement(sql);
			for (String uEmailAddress : approversToBeSetWaiting ){

				prepStmt.setInt(1, requirement.getRequirementId());
				prepStmt.setInt(2, requirement.getVersion());
				prepStmt.setString(3, uEmailAddress);
				prepStmt.execute();
			}
			prepStmt.close();
			
			

			// lets loop through all the RAH approvers for this req and version and set the status to Waiting.
			sql = " update gr_requirement_approval_h rah "
					+ " set rah.response ='Pending' "
					+ " where rah.requirement_id = ? "
					+ " and rah.version = ? "
					+ " and rah.approver_email_id = ? ";
			prepStmt = con.prepareStatement(sql);
			for (String uEmailAddress : approversToBeSetPending ){

				prepStmt.setInt(1, requirement.getRequirementId());
				prepStmt.setInt(2, requirement.getVersion());
				prepStmt.setString(3, uEmailAddress);
				prepStmt.execute();
			}
			prepStmt.close();
						
			setRequirementApprovalStringBasedOnRAH(con,  requirement);

			
			
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
	}

	
	public static Role getNextRoleToApprove(Requirement r) {
		
		Role nextRole = null;
		try {
	
			// Get All the roles that need to approve this folder, stacked by rank.
			//int folderId = r.getFolderId();
			//Folder f = new Folder(folderId);
			//ArrayList<Role> approvalRoles = f.getStackedApprovalRolesForFolder();
			ArrayList<Role> approvalRoles = r.getStackedApprovalRoles();
			
			// for each role, find out if the RoleApproval need is met
			// If Yes, go to the next role
			// If no, return this role.
			for (Role approvalRole : approvalRoles){
				String decision = r.getDecisionByRole(approvalRole);
				if (
						(decision.equals("approved"))
						||
						(decision.equals("rejected"))
					)
						{
					// has been decided by this Role, lets go to the next one
					
					// so do nothign and go to the next role.
				}
				else {
					// has not been approved by this Role completely. So, we return this role.
					// returns the first role that is not approved or rejected. This could be a Pending or Waiting role
				
					return(approvalRole);
				}
			}
			// we have iterated through all the roles, and apparently every role has approved 
			// return Null.
			return (nextRole);
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			
		}
		
		return (nextRole);
	}


	
	// This routine, changes the requirement status to 'In Approval WorkFlow' 
	// and calls the refresh requirement approval  history . 
	public static void submitRequirementForApproval(int requirementId, String actorEmailId, String databaseType, String serverName) {
		
		
		Requirement requirement = new Requirement(requirementId, "");
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// remove all approval history events for this requirement and version.
			clearRequirementApprovalForReSubmit(con,  requirement);
			
			// update status to 'In Approval WorkFlow'.
			String sql = "update gr_requirements" +
					" set status = 'In Approval WorkFlow' , submitted_for_approval_dt  = now() "+
					" where id = ? " 	;
				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,requirementId);
			prepStmt.execute();
			
			String log = "Submitting Requirement for Approval  " ;
			RequirementUtil.createRequirementLog(requirementId, log,actorEmailId,  databaseType);

			refreshRequirementApprovalHistory(requirementId,  databaseType);
 
			RequirementUtil.remindPendingApprovers(requirementId,  databaseType, serverName);
		
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
	}
	
	
	public static void remindPendingApproversImmediately(int requirementId, String serverName, HttpServletRequest request, 
			String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort,
			String emailUserId, String emailPassword) {
		
		// Find all the pending approvers for this requirement's current version.
		// See if an approval email was sent in the last 24 hours . 
		// if not, then send a please approve email 
		// make an entry in messages_sent with sentdate set to now. 
		Requirement requirement = new Requirement(requirementId, "");
		System.out.println("srt In remindPendingApproversImmediately for req  " + requirement.getRequirementFullTag());
		
		//String nextRoleNameForUser = requirement.getNextRoleToApproveForUser(approver);
		Role nextRoleToApproveForReq = requirement.getNextRoleToApprove();
		
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get pending approvers.
			String sql = "select rah.approver_email_id "
					+ " from gr_requirement_approval_h rah "
					+ " where rah.requirement_id = ? "
					+ " and rah.version = ? "
					+ " and rah.response = 'Pending' ";
		
			System.out.println("srt In sql is  " + sql);

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirement.getRequirementId());
			prepStmt.setInt(2, requirement.getVersion());
			rs = prepStmt.executeQuery();
			

			String checkSQL = "select count(*) 'matches'"
					+ "  from gr_messages "
					+ " where  message_type = 'requirementApprovalNotification'"
					+ " and to_email_id = ?   "
					+ " and message_body = ? "
					+ " and  datediff(now(),message_sent_dt) < 1 ";

			PreparedStatement checkStmt2 = con.prepareStatement(checkSQL);
			System.out.println("srt In checkSQL is  " + checkSQL);

			ArrayList<String> approversToNotify = new ArrayList<String>();
			while(rs.next()){
				String approver = rs.getString("approver_email_id");
				
				

				String checkString = "requirementId:#:" + requirement.getRequirementId() 
						+ ":##:version:#:" + requirement.getVersion()
						+ ":##:nextRoleForReq:#:" + nextRoleToApproveForReq.getRoleName() ;

				System.out.println("srt checkString is  " + checkString);
				// lets see if we had sent an email to this approver .
				checkStmt2.setString(1, approver);
				checkStmt2.setString(2,checkString);
				ResultSet checkRS = checkStmt2.executeQuery();
				System.out.println("srt approver is  " + approver);

				while (checkRS.next()){
					int matches = checkRS.getInt("matches");
					System.out.println("srt matches   " + matches);

					if (matches == 0 ){
						// no emails to this user
						// lets sent him an email and then update the gr_messages table.
						approversToNotify.add(approver);
						
						// lets store the message in db
						sql = " insert into gr_messages (project_name, to_email_id, message_type," +
								" message_body, message_created_dt, message_sent_dt ) " +
								" values (?,?,?,?, now(), now())";
						
						System.out.println("srt sql is   " + sql);

						PreparedStatement prepStmt3 = con.prepareStatement(sql);
						
						prepStmt3.setString(1, requirement.getProjectShortName());
						prepStmt3.setString(2, approver);
						prepStmt3.setString(3, "requirementApprovalNotification");
						prepStmt3.setString(4, checkString);			
						System.out.println("srt inserting for   " + approver);

						prepStmt3.execute();
					
						prepStmt3.close();
						
					}
				}
				checkRS.close();

				
			}
			checkStmt2.close();
			prepStmt.close();
			rs.close();
			// for every user in ArrayList , send the email and store the message sent date in the db.
			if (approversToNotify.size() > 0 ){
				notifyOnePendingApproverImmediately( requirement,  approversToNotify, nextRoleToApproveForReq.getRoleName(),
						 serverName, 
						request, 
					 mailHost,  transportProtocol,  smtpAuth,  smtpPort,  smtpSocketFactoryPort,  emailUserId,  emailPassword) ;
			


			}	
			
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
	}
	
	
	
	public static void notifyRejectorAndOwnerImmediately( Requirement requirement, String approvalNote, User user , 
			String serverName,HttpServletRequest request,
			String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort,
			String emailUserId, String emailPassword) {
			try {
				
				// lets email the new owner
				String url = ProjectUtil.getURL(request, requirement.getRequirementId(),"requirement");
				
				
				String subject = requirement.getRequirementFullTag() + " Version("+ requirement.getVersion() +") has just been rejected  "  ;
				
				String URLToReq = "https://"+ serverName +"/GloreeJava2/servlet/DisplayAction?dO=req&dReqId=" + requirement.getRequirementId() ;
				
				String approvalTableBlock = RequirementUtil.getApprovalTableInString(requirement);
				
				String requirementApprovalNotificationBlock = "<br><br><div ><table border='1' width='800'>"
						+ " <tr><td colspan=3> <b>Project</b> : " + requirement.getProjectShortName() + " </td></tr> " 
						+ " <tr><td colspan=3><b>Requirement</b> : <a href='"+ URLToReq+ "'>" + requirement.getRequirementFullTag() + "</a> : " + requirement.getRequirementName() + " </td></tr>" 
						+ " <tr><td colspan=3><b>Owner</b> : " + "  (" + requirement.getRequirementOwner() +  ") </td></tr>" 
						+ " <tr><td colspan=3 align='center'><a href=' " + URLToReq + "'>  Preview </a></td> </tr>"
						+"</table><div>";
				
				String commentsTableBlock = RequirementUtil.getRequirementCommentsTableInAString(requirement);
				
				String message = "<br>" + 
				"<html><body>Hello, <br><br>" + 
				" This requirement has just been rejected by " + user.getFirstName() + " " + user.getLastName()
				+" <br><br> Rejection Reason is :  " + approvalNote +
				approvalTableBlock + "<br><br>" +
				requirementApprovalNotificationBlock +  "<br><br>" + 
				commentsTableBlock + "<br><br>" + 
				"Best Regards<br>" + 
				" TraceCloud Administrator </body></html>";
				
				// lets send the email out to the toEmailId;
				ArrayList<String> toArrayList = new ArrayList<String>();
				toArrayList.add(requirement.getRequirementOwner());
				
				ArrayList<String> ccArrayList = new ArrayList<String>();
				ccArrayList.add(user.getEmailId());
				
				MessagePacket mP = new MessagePacket(toArrayList, ccArrayList, subject, message, "");
				
				EmailUtil.email(mP, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword); 

			} catch (Exception e) {
				
				e.printStackTrace();
			} finally {
			}
		}
		
		
	public static void notifyOnePendingApproverImmediately( Requirement requirement, ArrayList<String> approversToNotify,String roleName,
			
			 String serverName, 
			HttpServletRequest request,
			String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort,
			String emailUserId, String emailPassword) {
		
		
			// lets see if notifyOnApprovalChange is enabled. 
			int  notifyOnApprovalChange = getNotifyOnApprovalChange(requirement.getRequirementId());
			if (notifyOnApprovalChange == 0 ){
				return;
			}
			try {
				
				System.out.println("srt sending approval email now for : " + approversToNotify.toString());
				// lets email the new owner
				String url = ProjectUtil.getURL(request, requirement.getRequirementId(),"requirement");
				
				
				String subject = requirement.getRequirementFullTag() + " Version("+ requirement.getVersion() +") needs your approval for role :  " 
						+ roleName ;
				
				String URLToReq = "https://"+ serverName +"/GloreeJava2/servlet/DisplayAction?dO=req&dReqId=" + requirement.getRequirementId() ;
				String URLToApprove = "https://"+ serverName +"/GloreeJava2/jsp/Requirement/requirementApprovalAction.jsp?requirementId=" 
						+ requirement.getRequirementId() + "&approvalAction=approve";
				String URLToReject = "https://"+ serverName +"/GloreeJava2/jsp/Requirement/requirementApprovalAction.jsp?requirementId=" 
						+ requirement.getRequirementId() + "&approvalAction=reject";
				
				
				String approvalTableBlock = RequirementUtil.getApprovalTableInString(requirement);
				
				String requirementApprovalNotificationBlock = "<br><br><div ><table border='1' width='800'>"
						+ " <tr><td colspan=3> <b>Project</b> : " + requirement.getProjectShortName() + " </td></tr> " 
						+ " <tr><td colspan=3><b>Requirement</b> : <a href='"+ URLToReq+ "'>" + requirement.getRequirementFullTag() + "</a> : " + requirement.getRequirementName() + " </td></tr>" 
						+ " <tr><td colspan=3><b>Owner</b> : " + "  (" + requirement.getRequirementOwner() +  ") </td></tr>" 
						+ " <tr> " 
							+ "<td align='center'><a href=' " + URLToReq + "'>  Preview </a></td> "
							+ "<td align='center'><a href=' " + URLToApprove + "'>  Approve </a></td> "
							+ " <td align='center'> <a href='" + URLToReject + "'>  Reject </a></td>" 
						+ "</tr>"
						+"</table><div>";
				
				String commentsTableBlock = RequirementUtil.getRequirementCommentsTableInAString(requirement);
				
				String message = "<br>" + 
				"<html><body>Hello, <br><br>" + 
				" As you are a member of the role : <b>" + roleName + "</b> you will need to approve this requirement. <br><br> " + 
				approvalTableBlock + "<br><br>" +
				requirementApprovalNotificationBlock +  "<br><br>" + 
				commentsTableBlock + "<br><br>" + 
				"Best Regards<br>" + 
 				" TraceCloud Administrator </body></html>";
				
				System.out.println("Srt message is " + message);
				// lets send the email out to the toEmailId;
				ArrayList<String> toArrayList = approversToNotify;
				
				
				ArrayList<String> ccArrayList = new ArrayList<String>();
				
				MessagePacket mP = new MessagePacket(toArrayList, ccArrayList, subject, message, "");
				
				EmailUtil.email(mP, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword); 

			} catch (Exception e) {
				
				e.printStackTrace();
			} finally {
			}
		}
		
	
	public static String getApprovalTableInString(Requirement requirement ) {
		
		
		String table = "<table border='1'>  <tr bgcolor='#8fff8f'> <td colspan='6'> Current Approval Status </td></tr> <tr>  " + 
			"<td style='width:200px'>Role </td> " + 
			"<td style='width:100px'>Approval Rank</td> " + 
			"<td style='width:150px'>Role Type</td> " + 
			"<td style='width:200px'>Approver</td> " + 
			"<td style='width:100px'>Approval Status</td> " + 
			"<td style='width:150px'>Date</td> </tr>  ";
		
		
		ArrayList<String> approversAndStatus = requirement.getApproversAndStatus();
		
		for (String aS : approversAndStatus){
			
			String[] approversArray = aS.split(":##:");
			
			 String roleName = "";
			 String approvalRank = "";
			 String approvalType = "";
			 String emailId = "";
			 String userName = "";
			 String status = "";
			 String date = "";
			 String note = "";
			 String approvedRoles = "";
			 String currentRoleApprovalNote = "";
			 String currentRoleApprovalDt = "";
				
			 try {
				 roleName =  approversArray[0];
				 approvalRank =  approversArray[1];
				 approvalType =  approversArray[2];
				 emailId =  approversArray[3];
				 userName =  approversArray[4];
				 status =  approversArray[5];
				 note =  approversArray[6];
				 date =  approversArray[7];
				 approvedRoles = approversArray[8];
			 }
			 catch (Exception e){
				 e.printStackTrace();
			 }
			 	
			 
			// loop through the approvedRoles, till you come across current role (for this role). 
			// parse that to get the approval note and approved date
			 try {
				if (approvedRoles.contains("#")){
					// lets split approvedRoles to get the approval date and note
					String[] approverDetails = approvedRoles.split(",");
					
					for (String aD :approverDetails ){
						if (aD.contains(roleName) ){
							// lets split aD by :#:
							String[] noteAndDate = aD.split(":#:");	
							currentRoleApprovalNote = noteAndDate[1];
							currentRoleApprovalDt = noteAndDate[2];
							
							note = currentRoleApprovalNote;
							date = currentRoleApprovalDt;
						}
					}
					
					
				}
			 }
			 catch (Exception e){}
		 	String rowClass="";
			if (status.equals("Approved")){
				rowClass="success";
			}if (status.equals("Pending")){
				rowClass="warning";
				
			}
			if (status.equals("Rejected")){
				rowClass="danger";
			}
			if (status.equals("Waiting")){
				rowClass="info";
				status = "Waitng for Others";
			}

			if (approvedRoles.contains(roleName)){
				rowClass="success";
				status = "Approved";
			}
			
			String color="";
			if (rowClass.equals("success")){
				color="#8fff8f";
			}
			if (rowClass.equals("danger")){
				color="#e85e68";
			}
			if (rowClass.equals("info")){
				color="#568af2";
			}
			if (rowClass.equals("warning")){
				color="#ffa96b";
			}
			
			
			
			
			table += "<tr bgcolor='"+ color +"' > " + 
			 "<td style='width:200px'>" + roleName +  "</td> " +
			" <td style='width:100px'>" + approvalRank  +  " </td> " +
			" <td style='width:150px'>" + approvalType +   " </td> " +
			" <td style='width:200px'>" + userName +  " </td> " +
			" <td style='width:100px'>" + status +   "</td> " +
			" <td style='width:150px'>" + date  + "</td>  </tr> ";	
		
			if ((note!=null && (note.length() > 0 ))){ 
				table += "<tr > " + 
						" <td colspan='6'> " + 
						" <b>Note : </b> " + note + " </td> </tr>";
			}
		}
		
		table += "</table>";
		return(table);
		
	}
	
	
	public static String getRequirementCommentsTableInAString(Requirement requirement ) {
		
		
		String table = "<table border='1'> "
				+ " <tr bgcolor='#8fff8f'><td colspan='4'>Comments</td></tr> <tr bgcolor='#e85e68'>  " + 
			"<td style='width:200px'>Date </td> " + 
			"<td style='width:50px'>Version</td> " + 
			"<td style='width:100px'>Commenter</td> " + 
			"<td style='width:350px'>Comment</td> </tr>" ;
			
		
		
		ArrayList<Comment> comments = RequirementUtil.getRequirementComments(requirement.getRequirementId(), "mySQL");
		
		int i=0;
		for (Comment comment : comments){
			i++;
			String color="#ffffff";
			if (i%2==0){
				color="#b4e8f7";
			}
			table += "<tr bgcolor='"+ color + "'> " +
			" <td style='width:200px'> " + comment.getCommentDate() +" </td> " + 
			 "<td style='width:50px'> Version(" + comment.getVersion() +  ")</td> " +
			" <td style='width:100px'>" + comment.getCommenterEmailId()  +  " </td> " +
			" <td style='width:350px'>" + comment.getHTMLFriendlyCommentNote() +   " </td></tr> " ;
				
		
			
		}
		
		table += "</table>";
		return(table);
		
	}
	
	// This routine, changes the requirement status to 'In Approval WorkFlow' 
	// and calls the refresh requirement approval  history . 

	/*
	 * public static void submitRequirementForApproval(int requirementId, String actorEmailId, String databaseType, String serverName) {
	
		Requirement requirement = new Requirement(requirementId, "");
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// update status to 'In Approval WorkFlow'.
			String sql = "";
			
			// remove all approval history events for this requirement and version.
			clearRequirementApprovalForReSubmit(con,  requirement);
			
			if (databaseType.equals("mySQL")){
				sql = "update gr_requirements" +
						" set status = 'In Approval WorkFlow' , submitted_for_approval_dt  = now() "+
						" where id = ? " 	;
					
			}
			else {
				sql = "update gr_requirements " +
						" set status = 'In Approval WorkFlow' , submitted_for_approval_dt  = sysdate "+
						" where id = ? " ;
				
			}
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,requirementId);
			prepStmt.execute();
			
			String log = "Submitting Requirement for Approval  " ;
			RequirementUtil.createRequirementLog(requirementId, log,actorEmailId,  databaseType);

			refreshRequirementApprovalHistory(requirementId,  databaseType);
 
			RequirementUtil.remindPendingApprovers(requirementId,  databaseType, serverName);
			
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
	}

*/
	// this looks at the gr_requirement_approval_h table to determine who needs to be reminded
	// and then sends an approval note to them.
	// For this to be effective, generally a call to 			
	// refreshRequirementApprovalHistory(requirementId,  databaseType) is made before this

	
	public static void remindPendingApprovers(int requirementId, String databaseType , String  serverName) {
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
		String sql = "select p.name \"project_name\", r.owner, u.first_name, u.last_name, r.id \"requirement_id\", " +
			" r.full_tag, r.name \"requirement_name\", rah.approver_email_id " + 
			" from gr_requirement_approval_h rah, gr_requirements r, gr_projects p, gr_users u " +
			" where rah.response = 'Pending' " + 
			" and rah.requirement_id = r.id " +
			" and r.project_id = p.id " +
			" and rah.requirement_id = ? " +
			" and rah.version = r.version " +
			" and r.owner = u.email_id " + 
			" order by p.name, r.id ";

		prepStmt = con.prepareStatement(sql);
		prepStmt.setInt(1, requirementId);
		

	
		rs = prepStmt.executeQuery();
		while (rs.next()) {
			String approverEmailId = rs.getString("approver_email_id");
			String ownerEmailId = rs.getString("owner");
			String ownerFirstName = rs.getString("first_name");
			String ownerLastName = rs.getString("last_name");
			String projectName = rs.getString("project_name");
			String fullTag = rs.getString("full_tag");
			String requirementName = rs.getString("requirement_name");
			

			
			
			// messageBody is ownerEmailId:##:ownerFirstname:##:ownerLastName:##:projectName:##:projectPrefix:##:projectName:##:fullTag:##:reqName:##:URLToReq:##:URLToApprove:##:URLtoReject
			String messageBody =  ownerEmailId + ":##:" + ownerFirstName + ":##:" + ownerLastName + ":##:" + projectName + ":##:" + fullTag + ":##:" +  requirementName 
					+ ":##:"  +  "https://"+ serverName +"/GloreeJava2/servlet/DisplayAction?dO=req&dReqId=" + requirementId 
					+ ":##:"  +  "https://"+ serverName +"/GloreeJava2/jsp/Requirement/requirementApprovalAction.jsp?requirementId=" + requirementId + "&approvalAction=approve"
					+ ":##:"  +  "https://"+ serverName +"/GloreeJava2/jsp/Requirement/requirementApprovalAction.jsp?requirementId=" + requirementId + "&approvalAction=reject"
					;
			
			String toEmailId = approverEmailId;
			String messageType = "requirementApprovalNotification";
			
			EmailUtil.storeMessage(projectName, toEmailId, messageType, messageBody,  databaseType);
		}	
		rs.close();
		prepStmt.close();
		
		
		// lets update the Requirement's last_approval_reminder_sent_dt to now. 
		if (databaseType.equals("mySQL")){
			sql = " update gr_requirements " +
				" set last_approval_reminder_sent_dt = now() " +
				" where id = ?  ";
		}
		else {
			sql = " update gr_requirements " +
					" set last_approval_reminder_sent_dt = sysdate " +
					" where id = ?  ";
		}
		
		prepStmt = con.prepareStatement(sql);
		prepStmt.setInt(1, requirementId);
		prepStmt.execute();
		
		prepStmt.close();
				
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
	}
	
	// This routine, cancels any pending approvals for this requirement . 

	public static void cancelPendingRequirementApprovals(int requirementId) {
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// update status to 'In Approval WorkFlow'.
			String sql = "update gr_requirement_approval_h" +
				" set response = 'Cancelled'  " +
				" where requirement_id = ? " +
				" and response = ?  " + 
				 " and approved_roles not like '%:#:%' ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,requirementId);
			prepStmt.setString(2, "Pending");
			prepStmt.execute();

			prepStmt.close();
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
	}

	
	// This routine, 
	// 1. sets the approvers flag to either Approved or Declined along with the note for this 
	// req/ version combo.
	// 2. Add these approval notes to the comments table
	// 3. runs the refresh approval history method to see if any new approvers have been added.
	// 4. if all approvals are done, sets the req approval status flag to 'Approved'.
	// 5. If this user is expected to get any approval email , since he / she has taken the action, let's remove that email, as it's no longer required.
	// This routine's security model ensures that only the logged in users approval
	// actions are taken at any time.
	
	
	public static void approvalWorkFlowAction(int requirementId,String approvalAction,String approvalNote,
			User user, HttpServletRequest request, String databaseType){
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			Requirement requirement = new Requirement (requirementId,  databaseType);
			
			if (
					(approvalAction.equals("requestApprovalFromRejector"))
					||
					(approvalAction.equals("cancelMyRejection")	)						
				){
				requestApprovalFromRejector(con,  requirement,  user, approvalNote);
				// let the rest of the flow continue (update RAH, setFinalStatus etc..
			}
			

			if (approvalAction.equals("bypassRejector")){
				
				bypassRejector(con,  requirement,  user, approvalNote);
				// let the rest of the flow continue (update RAH, setFinalStatus etc..
				
			}
			
			if (approvalAction.equals("bypassAllApprovers")){
				bypassAllApprovers(con,  requirement,  user, approvalNote , request);
				
				return;
			}
			
			
			if (approvalAction.contains("bypassAnApprover")){
				// lets seee if this si a bypass an approverRole

				approvalNote = "Approval Bypassed by " + user.getEmailId() + ". Note is : " + approvalNote;
				
				if (approvalAction.contains("bypassAnApproverRole")){
					String [] aA = approvalAction.split(":#:");
					int roleId  = Integer.parseInt(aA[1]);
					Role role = new Role (roleId);
					ArrayList<User> usersInRole =  RoleUtil.getAllUsersInRole(roleId, "mySQL");
					for (User pendingApprover : usersInRole){

						System.out.println("srt about to bypass requirement " + requirement.getRequirementFullTag() 
								+ " for user" + pendingApprover.getEmailId() + " with note " + approvalNote);
						approveARequirement(con,  requirement,  pendingApprover, approvalNote , request, "bypass");
					}
				}
				else {
					
					String [] aA = approvalAction.split(":#:");
					String pendingApproverEmailId = aA[1];
					User pendingApprover = new User(pendingApproverEmailId, "mySQL");
					
					System.out.println("srt about to bypass requirement " + requirement.getRequirementFullTag() 
							+ " for user" + pendingApprover.getEmailId() + " with note " + approvalNote);
					approveARequirement(con,  requirement,  pendingApprover , approvalNote, request, "bypass");
				}
				// let the rest of the flow continue (update RAH, setFinalStatus etc..
			}
			if (approvalAction.equals("approve")){

				System.out.println("srt about to do regular approval for requirement " + requirement.getRequirementFullTag() 
						+ " for user" + user.getEmailId() + " with note " + approvalNote);
				approveARequirement(con,  requirement,  user , approvalNote, request, "self");
				
			}// end of approve
			
			if (approvalAction.equals("reject")){
				// update status to 'In Approval WorkFlow'.
				String sql = "";
					sql = "update gr_requirement_approval_h " +
						" set  note = ? , response = ?  , response_dt = now() " +
						" where requirement_id = ?  " +
						" and version = ? " +
						" and approver_email_id = ? ";
				
				prepStmt = con.prepareStatement(sql);
				
				prepStmt.setString(1,approvalNote);
				prepStmt.setString(2,"Rejected");
				prepStmt.setInt(3, requirement.getRequirementId());
				prepStmt.setInt(4, requirement.getVersion());
				prepStmt.setString(5, user.getEmailId());				
				prepStmt.execute();
				
				prepStmt.close();
				

				String serverName = request.getServerName();
				
				String mailHost = request.getServletContext().getInitParameter("mailHost");
				String transportProtocol = request.getServletContext().getInitParameter("transportProtocol");
				String smtpAuth = request.getServletContext().getInitParameter("smtpAuth");
				String smtpPort = request.getServletContext().getInitParameter("smtpPort");
				String smtpSocketFactoryPort = request.getServletContext().getInitParameter("smtpSocketFactoryPort");
				String emailUserId = request.getServletContext().getInitParameter("emailUserId");
				String emailPassword = request.getServletContext().getInitParameter("emailPassword");

				
				RequirementUtil.notifyRejectorAndOwnerImmediately( requirement,  approvalNote, user, serverName,request, 
						 mailHost,  transportProtocol,  smtpAuth,  smtpPort,  smtpSocketFactoryPort,  emailUserId,  emailPassword) ;
			
				 setRequirementApprovalStringBasedOnRAH( con,  requirement);
				 
				// lets make a log entry that states that this version of req is rejected
				String log = "Rejected Version #  " + requirement.getVersion();

				 
				RequirementUtil.createRequirementLog(requirementId, log, user.getEmailId(),  databaseType);
			}
			

			
			createComment(requirementId,user, approvalNote, request,  databaseType);
			setFinalApprovalStatus(requirement,  databaseType);
			
			// lets refresh the requirement approval history to see if any new approvers
			// have been added.
			// refresh is a resource intensive job, so commenting it out.
			//refreshRequirementApprovalHistory(requirementId,  databaseType);
			
			
			// lets remove requirementApprovalNotification from gr_messages for this user, as he has taken all the action he needs to take.
			// lets get the project name
			RequirementUtil.removeAllMessagesAboutARequirementApprovalForAUser(con, requirement, user);
			
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
	}

	

	public static void approveAnIndividualRole(java.sql.Connection con, Requirement requirement, User user, String approvalNote){
		
		
		PreparedStatement prepStmt = null;
		
		try {
			String sql = "update gr_requirement_approval_h " +
				" set note=?,  response = ?  , response_dt = now() " +
				" where requirement_id = ?  " +
				" and version = ? " +
				" and approver_email_id = ? ";
		
		
			prepStmt = con.prepareStatement(sql);
			
			prepStmt.setString(1,approvalNote);
			prepStmt.setString(2,"Approved");
			prepStmt.setInt(3, requirement.getRequirementId());
			prepStmt.setInt(4, requirement.getVersion());
			prepStmt.setString(5, user.getEmailId());				
			prepStmt.execute();
			
			prepStmt.close();
			
			// lets make a log entry that states that this version of req is approved
			String log = "Approved Version #  " + requirement.getVersion();
			RequirementUtil.createRequirementLog(requirement.getRequirementId(), log, user.getEmailId(),  "mySQL");
		
		}
		
		catch (Exception e){
			e.printStackTrace();
		}
		return;
	}

	

	public static void updateRequirementApprovalHistoryForARole(java.sql.Connection con, Requirement requirement, 
			User user, String newRoleName, String approvalNote, String response){
		
		
		PreparedStatement prepStmt = null;
		
		try {
			// get the current approved_roles, add the new role and update. 

			
			
			if (response.equals("Approved")){
				System.out.println("setting response to Approved  "   + newRoleName );

				String sql = "update gr_requirement_approval_h " +
						" set note=?,  response = 'Approved'  , response_dt = now() " +
						" where requirement_id = ?  " +
						" and version = ? " +
						" and approver_email_id = ? ";
				
				
					prepStmt = con.prepareStatement(sql);
					
					prepStmt.setString(1,approvalNote);
					
					prepStmt.setInt(2, requirement.getRequirementId());
					prepStmt.setInt(3, requirement.getVersion());
					prepStmt.setString(4, user.getEmailId());				
					prepStmt.execute();
					
					prepStmt.close();
					
					System.out.println("sql is   "   + sql );

					// lets make a log entry that states that this version of req is approved
					String log = "Approved Version #  " + requirement.getVersion() + " with approval note " + approvalNote;
					RequirementUtil.createRequirementLog(requirement.getRequirementId(), log, user.getEmailId(),  "mySQL");
				
			}
			if (response.equals("bypassApproved")){
				approvalNote = "Bypassing the pending approver  : " + approvalNote;
				
				
				// update status to 'In Approval WorkFlow'.
				String sql = "";
					sql = "update gr_requirement_approval_h " +
						" set note=?,  response = ?  , response_dt = now() " +
						" where requirement_id = ?  " +
						" and version = ? " +
						" and approver_email_id = ? ";
				
				
				prepStmt = con.prepareStatement(sql);
				
				prepStmt.setString(1,approvalNote);
				prepStmt.setString(2,"Approved");
				prepStmt.setInt(3, requirement.getRequirementId());
				prepStmt.setInt(4, requirement.getVersion());
				prepStmt.setString(5, user.getEmailId());				
				prepStmt.execute();
				
				prepStmt.close();
				
				// Make a log entry
				String log = "Bypassing the pending approver " + user.getEmailId() +"for vesion  " + requirement.getVersion() + " with note --> " + approvalNote;
				RequirementUtil.createRequirementLog(requirement.getRequirementId(), log, user.getEmailId(), "mySQL");
				
			}
			if (response.equals("Waiting")){
				System.out.println("setting response to waiting  "   + newRoleName );
				
				String sql = "";
				
				// lets get current date;
				sql = "select now() 'approvalDate' from dual;";
	
				prepStmt = con.prepareStatement(sql);
								
				ResultSet rs = prepStmt.executeQuery();
				String approvalDate  = ""; 
				while(rs.next()){
					approvalDate = rs.getString("approvalDate");
				}
				prepStmt.close();
				
				newRoleName = newRoleName + ":#:" + approvalNote + ":#:" + approvalDate;
				
				sql = "update gr_requirement_approval_h " +
					" set  approved_roles = concat(ifnull(approved_roles,''), ',', ?), response = ?   " +
					" where requirement_id = ?  " +
					" and version = ? " +
					" and approver_email_id = ? ";
			
				
	
				
				prepStmt = con.prepareStatement(sql);
				
				prepStmt.setString(1,newRoleName);
				prepStmt.setString(2, response);
				
				prepStmt.setInt(3, requirement.getRequirementId());
				prepStmt.setInt(4, requirement.getVersion());
				prepStmt.setString(5, user.getEmailId());				
				prepStmt.execute();
				
				prepStmt.close();
				

				// lets make a log entry that states that this version of req is approved
				String log = "Approved Version #  " + requirement.getVersion() + " for " + newRoleName + " with note " + approvalNote;
				RequirementUtil.createRequirementLog(requirement.getRequirementId(), log, user.getEmailId(),  "mySQL");
			
			}
			
			setPendingWaiting(requirement);

			
		}
		
		catch (Exception e){
			e.printStackTrace();
		}
		return;
	}


	public static void requestApprovalFromRejector(java.sql.Connection con, Requirement requirement, User user, String approvalNote){
		
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {
			// lets add the note to the log  / comment
			// set approval status of the req to 'inApprovalFlow'
			// set the requirement_approval_h response from the user to pending. 
			
			

			// lets make a log entry that states that this version of req is approved
			String log = "Re Requesting approval from the rejector for vesion  " + requirement.getVersion() + " with note --> " + approvalNote;
			RequirementUtil.createRequirementLog(requirement.getRequirementId(), log, user.getEmailId(),  "mySQL");
			
			
			String sql = "update gr_requirements" +
					" set status = 'In Approval WorkFlow'  "+
					" where id = ? " 	;

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirement.getRequirementId());
			prepStmt.execute();
			prepStmt.close();
			
			// for the rejector in RAH, lets set his response to pending, remove his note and response_dt
			sql = "update gr_requirement_approval_h " +
					" set note= null,  response = ?  , response_dt = null" +
					" where requirement_id = ?  " +
					" and version = ? " +
					" and response = ? ";
			
			
			prepStmt = con.prepareStatement(sql);
			
			prepStmt.setString(1,"Pending");
			prepStmt.setInt(2, requirement.getRequirementId());
			prepStmt.setInt(3, requirement.getVersion());
			prepStmt.setString(4,"Rejected");
							
			prepStmt.execute();
			
			prepStmt.close();
		
			
			// refresh RAH

			setRequirementApprovalStringBasedOnRAH(con,  requirement);
		}
		
		catch (Exception e){
			e.printStackTrace();
		}
		return;
	}
	
	
	

	public static void bypassRejector(java.sql.Connection con, Requirement requirement, User user, String approvalNote){
		
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {
			// Make a log entry
			// Make a comment
			// update the rejector's status to 'approved' and add to his note , his old note + bypassed by + bypassnote
			// remove any approval emails to this user
			// set req status to 'In Approval Work flow'
			// let the rest of the flow continue (update RAH, setFinalStatus etc..
			
			
			// Make a log entry
			String log = "Bypassing the rejector for vesion  " + requirement.getVersion() + " with note --> " + approvalNote;
			RequirementUtil.createRequirementLog(requirement.getRequirementId(), log, user.getEmailId(), "mySQL");
			

			
			// Make a comment
			approvalNote = "Bypassing the rejector : " + approvalNote;
			
			
			
			// update the rejector's status to 'approved' and add to his note , his old note + bypassed by + bypassnote
			String sql = "update gr_requirement_approval_h " +
					" set note=  concat( ? , note ) ,  response = ?  , response_dt = now()" +
					" where requirement_id = ?  " +
					" and version = ? " +
					" and response = ? ";
			
			
			prepStmt = con.prepareStatement(sql);
			

			prepStmt.setString(1,"Bypassed Approval by  : " + user.getEmailId() + "; Bypass Note : "  + approvalNote + " ; Original Rejectors's Note : ");
			prepStmt.setString(2,"Approved");
			prepStmt.setInt(3, requirement.getRequirementId());
			prepStmt.setInt(4, requirement.getVersion());
			prepStmt.setString(5,"Rejected");
							
			prepStmt.execute();
			prepStmt.close();
			
			// set req status to 'In Approval Work flow'
			sql = "update gr_requirements" +
					" set status = 'In Approval WorkFlow' "+
					" where id = ? " 	;
		
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,requirement.getRequirementId());
			prepStmt.execute();
			prepStmt.close();
			
			setPendingWaiting(requirement);

		
		}
		
		catch (Exception e){
			e.printStackTrace();
		}
		return;
	}
	
	public static void approveARequirement(java.sql.Connection con, Requirement requirement, User pendingApprover, 
			String approvalNote,  HttpServletRequest request, String selfOrBypass){
		
		System.out.println("\t\tsrt in approveARequriement for --> " + requirement.getRequirementFullTag() + 
				" approver is --->  " + pendingApprover.getEmailId() + " note is --->  " + approvalNote);
		try {
			String pendingApproverEmailId = pendingApprover.getEmailId();
			ArrayList<HashMap<String, String>> roles = requirement.getApproversRolesForUser(pendingApproverEmailId);

			
			if (roles.size() == 1){
				// simple story  . Just apprve the req and move on
				String nextRoleName = "";
				if (selfOrBypass.equals("self")){
					updateRequirementApprovalHistoryForARole(con, requirement,  pendingApprover,  nextRoleName, approvalNote, "Approved");
				}
				else {
					updateRequirementApprovalHistoryForARole(con, requirement,  pendingApprover,  nextRoleName, approvalNote, "bypassApproved");
				}
				createComment(requirement.getRequirementId(),pendingApprover, approvalNote, request,  "mySQL");
				setFinalApprovalStatus(requirement,  "mySQL");
				return;
			}
			else {
			
				// the user belonged to more than 1 role
				// see how many of the roles have been approved. If just one last role left, then set the status to approved
				// else approve the lowest role and leave status pending.
				int rolesYetToBeApproved = 0;
				// iterate through the roles and find how many are yet to be approved. 
				
				for (HashMap<String, String >role:roles){
					String approvedRoles =  role.get("approvedRoles");
					String roleName = role.get("roleName");
					if (!(approvedRoles.contains(roleName))){
						rolesYetToBeApproved++;
					}
					
				}
				System.out.println("srt rolesYetToBeApproved is  "  + rolesYetToBeApproved );
				
				String nextRoleName = requirement.getNextRoleToApproveForUser(pendingApprover.getEmailId());
				if (rolesYetToBeApproved == 1){
					// Final role for this user to approve
					if (selfOrBypass.equals("self")){
						updateRequirementApprovalHistoryForARole(con, requirement,  pendingApprover,  nextRoleName, approvalNote, "Approved");
					}
					else {
						updateRequirementApprovalHistoryForARole(con, requirement,  pendingApprover,  nextRoleName, approvalNote, "bypassApproved");
					}
					createComment(requirement.getRequirementId(),pendingApprover, approvalNote, request,  "mySQL");
					setFinalApprovalStatus(requirement,  "mySQL");
					return;
				}
				else{
					// has more roles to approve. Aprove for this role. DO NOT set finalApprovalStatus
					updateRequirementApprovalHistoryForARole(con, requirement,  pendingApprover,  nextRoleName, approvalNote, "Waiting");
					createComment(requirement.getRequirementId(), pendingApprover, approvalNote, request,  "mySQL");
					return;
				}
				
			}

		}
		
		catch (Exception e){
			e.printStackTrace();
		}
		return;
	}
	
	
	
	public static void bypassAllApprovers(java.sql.Connection con, Requirement requirement, User user, String approvalNote, HttpServletRequest request){
		
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {
			// DONE: Make a log entry
			// DONE: Make a comment
			// DONE: update ALL the rejector's status to 'approved' and add to his note , his old note + bypassed by + bypassnote
			// TODO: Add all the remaining approvers and set status to 'approved' and add to his note , his old note + bypassed by + bypassnote
			
			// DONE:  any approval emails to all users
			
			// DONE: set req approval info from RAH consolidation
			// DONE: set req status
			//  return from this method
			
			// NOTE: since we will RETURN from this method, we have to complete all the work by the end of the if loop 
			
			

			// Make a log entry
			String log = "Bypassing ALL THE APPROVERS for vesion  " + requirement.getVersion() + " with note --> " + approvalNote;
			RequirementUtil.createRequirementLog(requirement.getRequirementId(), log, user.getEmailId(),  "mySQL");
			

			
			// Make a comment
			approvalNote = "Bypassing ALL THE APPROVERS : " + approvalNote;
			createComment(requirement.getRequirementId(),user, approvalNote, request,  "mySQL");
			
			
				String sql = " update  gr_requirement_approval_h " +
					 " set response = ? , note = ? , response_dt = now() " +
					" where requirement_id = ?  " +
					" and version = ? "
					; 
			
			
				prepStmt = con.prepareStatement(sql);
				
				prepStmt.setString(1,"Approved");
				prepStmt.setString(2,"Bypassing ALL THE APPROVERS by  : " + user.getEmailId() + "; Bypass Note : "  + approvalNote + " ; Original  Note : ");
				
				
				prepStmt.setInt(3, requirement.getRequirementId());
				prepStmt.setInt(4, requirement.getVersion());

				
				
				prepStmt.execute();
				
			
			
			
			// set req approval info from RAH consolidation
			setRequirementApprovalStringBasedOnRAH(con,  requirement);

			// set req status to 'In Approval Work flow'
			setRequirementStatusAndLog(con, requirement, "Approved");
			
		
		}
		
		catch (Exception e){
			e.printStackTrace();
		}
		return;
	}
	
	
	// removes ALL messages about a Requirement 's approval from gr_messages

	public static void removeAllMessagesAboutARequirementApproval(java.sql.Connection con, Requirement requirement){
		
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {

			String sql = "select name from gr_projects where id = " + requirement.getProjectId();
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			String projectName = "";
			while (rs.next()){
				projectName = rs.getString("name");
			}
			
			sql = "delete from gr_messages "
					+ " where message_type = 'requirementApprovalNotification'"
					+ " and project_name =  ? "
					+ " and message_body like '%"+ requirement.getRequirementFullTag() +"%' ";
			prepStmt = con.prepareStatement(sql);
			
			prepStmt.setString(1, projectName);
			
			prepStmt.execute();
			
		
		}
		
		catch (Exception e){
			e.printStackTrace();
		}
		return;
	}
	
	
	// removes ALL messages about a Requirement 's approval from gr_messages FOR A USER

	public static void removeAllMessagesAboutARequirementApprovalForAUser(java.sql.Connection con, Requirement requirement, User user){
		
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {


			String sql = "select name from gr_projects where id = " + requirement.getProjectId();
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			String projectName = "";
			while (rs.next()){
				projectName = rs.getString("name");
			}
			
			sql = "delete from gr_messages "
					+ " where to_email_id = ? "
					+ " and message_type = 'requirementApprovalNotification'"
					+ " and project_name =  ? "
					+ " and message_body like '%"+ requirement.getRequirementFullTag() +"%' ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, user.getEmailId());
			prepStmt.setString(2, projectName);
			
			prepStmt.execute();
		
		}
		
		catch (Exception e){
			e.printStackTrace();
		}
		return;
	}
	
	
	

	
	public static void setRequirementStatusAndLog(java.sql.Connection con, Requirement requirement, String status){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {
			String sql = "update gr_requirements " +
				" set status = ?, " +
				" approved_by_all_dt = now() " +
				" where id = ? ";
		
			
			prepStmt = con.prepareStatement(sql);

			prepStmt.setString(1, status);
			prepStmt.setInt(2, requirement.getRequirementId());
			prepStmt.execute();	
			prepStmt.close();
			
			// lets make a log entry that states that this version of req is approved
			String log = "Version " + requirement.getVersion() + " has finally been " + status +  ". ";
			RequirementUtil.createRequirementLog(requirement.getRequirementId(), log, "System",  "mySQL");
		
		}
		
		catch (Exception e){
			e.printStackTrace();
		}
		return;
	}
	

	
	public static void addDynamicApprovalRoleShell( Requirement requirement, Role role, int approvalRank) {

		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			addDynamicApprovalRole(con, requirement, role , approvalRank);
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

	public static void addDynamicApprovalRole(java.sql.Connection con, Requirement requirement, Role role, int approvalRank){
		PreparedStatement prepStmt = null;
		//ResultSet rs = null;
		try {
			String sql = "insert into gr_dynamic_roles(requirement_id, role_id, approval_rank) values (?,?, ? ) ";
		
			
			prepStmt = con.prepareStatement(sql);

			prepStmt.setInt(1, requirement.getRequirementId());
			prepStmt.setInt(2, role.getRoleId());
			prepStmt.setInt(3, approvalRank);
			
			prepStmt.execute();	
			prepStmt.close();
			
			
			// lets make a log entry that states that this version of req is approved
			String log = "Role : " + role.getRoleName() + " has been added as a dynamic approver to Version " 
			+ requirement.getVersion() + " with rank " + approvalRank;
			RequirementUtil.createRequirementLog(requirement.getRequirementId(), log, "System",  "mySQL");
		
		}
		
		catch (Exception e){
			e.printStackTrace();
		}
		return;
	}
	
	public static void removeDynamicApprovalRoleShell(Requirement requirement, Role role) {

		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			removeDynamicApprovalRole(con, requirement, role);
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
	public static void removeDynamicApprovalRole(java.sql.Connection con, Requirement requirement, Role role){
		PreparedStatement prepStmt = null;
		//ResultSet rs = null;
		try {
			String sql = "delete from  gr_dynamic_roles where requirement_id = ? and role_id = ?  ";
		
			
			prepStmt = con.prepareStatement(sql);

			prepStmt.setInt(1, requirement.getRequirementId());
			prepStmt.setInt(2, role.getRoleId());
			prepStmt.execute();	
			prepStmt.close();
			
			
			// lets make a log entry that states that this version of req is approved
			String log = "Role : " + role.getRoleName() + " has been removed as a dynamic approver to Version " + requirement.getVersion();
			RequirementUtil.createRequirementLog(requirement.getRequirementId(), log, "System",  "mySQL");
		
		}
		
		catch (Exception e){
			e.printStackTrace();
		}
		return;
	}
	// when a req is resumbitted for approval, this will remove ALL existing approval events for this requirement and version
	public static void clearRequirementApprovalForReSubmit(java.sql.Connection con, Requirement requirement){
		PreparedStatement prepStmt = null;
		
		try {
			String sql = "delete from gr_requirement_approval_h " +
					"	where requirement_id = ? and version =  ? ";
		
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirement.getRequirementId());
			prepStmt.setInt(2, requirement.getVersion() );
			prepStmt.execute();
			prepStmt.close();
		}
		
		catch (Exception e){
			e.printStackTrace();
		}
		return;
	}

	// will remove ALL the pending approvers for a requirement
	public static void setRequirementApprovalStringBasedOnRAH(java.sql.Connection con, Requirement requirement){

		System.out.println("srt in setRequirementApprovalStringBasedOnRAH for " + requirement.getRequirementFullTag());
		PreparedStatement prepStmt = null;
		try {
			
			int requirementId = requirement.getRequirementId();
			
			// Now lets get the list of approvers and approval status
			// make it into a string, and update the requirements approvers field.
			String sql = "select rah.approver_email_id, response " +
				" from gr_requirement_approval_h rah " +
				" where rah.requirement_id = ? " +
				" and rah.version = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.setInt(2,requirement.getVersion());
			
			
			String approvers = "";
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				String approverEmailId = rs.getString("approver_email_id");
				String response = rs.getString("response");
				if (response.equals("Pending")) {
					approvers += "(P)" + approverEmailId + ",";
				}
				if (response.equals("Rejected")) {
					approvers += "(R)" + approverEmailId + ",";
				}

				if (response.equals("Approved")) {
					approvers += "(A)" + approverEmailId + ",";
				}
				
				if (response.equals("Waiting")) {
					approvers += "(W)" + approverEmailId + ",";
				}
				
				
			}
			rs.close();
			prepStmt.close();
			// lets drop the last ,
			// NOTE : we are deliberately not dropping the last , as 
			// a lot of our downstream display logic is based on splitting based on ,
			// and if for situations where theres is only one approvers, we still need
			// to have a comma .

			
			// Now lets update the reqs approvers field.
			sql = "update gr_requirements set approvers = ? where id = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, approvers);
			prepStmt.setInt(2, requirementId);
			prepStmt.execute();
			
			prepStmt.close();
			
		}
		
		catch (Exception e){
			e.printStackTrace();
		}
		return;
	}
	
	
	// will remove ALL the pending approvers for a requirement
	public static void removeRequirementPendingAppovers(java.sql.Connection con, Requirement requirement){
		
		
		PreparedStatement prepStmt = null;
		try {
			// :#: in the approved_roles field means that this user has made some decisions. 
			String sql = "delete from gr_requirement_approval_h " +
					"	where requirement_id = ? " + 
					"	and response in ('Pending', 'Waiting') " +
					" and approved_roles not like '%:#:%'  "; 
		
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirement.getRequirementId());
			prepStmt.execute();
			prepStmt.close();
			
			
			 setRequirementApprovalStringBasedOnRAH( con,  requirement);
			
		}
		
		catch (Exception e){
			e.printStackTrace();
		}
		
		return;
	}
	
	public static int getRequirementApprovalRejects(java.sql.Connection con, Requirement requirement){
		int rejected = 0;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {
			// lets get the count of rejectors or pending approvers.
			String sql = "select  count(*) \"rejected\" " +
					"	from gr_requirement_approval_h " +
					"	where requirement_id = ? " + 
					"	and version =  ? " +
					"	and response = 'Rejected' " ;
				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirement.getRequirementId());
			prepStmt.setInt(2, requirement.getVersion());
			rs = prepStmt.executeQuery();

			while (rs.next()) {
				rejected = rs.getInt("rejected");
			}
			prepStmt.close();
			rs.close();
		}
		
		catch (Exception e){
			e.printStackTrace();
		}
		return(rejected);
	}
	// this goes throuh all the approvers and status for a requirement and sets the final status
	

	// There are some role types, where a single approval is sufficient for approval completion.
	// lets cycle through all the roles that need to approve this req
	// for each role, lets check  if approval fulfilment is done
	// 			for the ALL must approve type-  Have ALL the approvers for thsi role approved 
	//			for the ANY ONE must approve type - Have any one approved
	//	if all roles have been approved, lets remove any 'Pending' approvers, as this role req has completed cycle.
	//			
	
	
	public static void setFinalApprovalStatus(Requirement requirement,  String databaseType){
		

		
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			// The first check we make is to see 'if any 1 user has rejected'. 
			// If yes, then we remove all pending approvers and  set the status to  rejected.
			// this is because, no matter what the rest of the approvers say, this req will end up rejected.
			// If there are no rejects, then we go through the motions of finding all the remaining approval roles
			// checking if ANY/ALL have approved, then moving to the next role
			
			int rejected = getRequirementApprovalRejects(con, requirement);

			if(rejected > 0 ){
				// we have at least 1 reject. so lets remove all pending and 

				
				// DO NOT REMOVE PENDING APPROVERS AS PART OF SETFINAL STATUS IF THE REQ IS REJECTED
				// BECAUSE, WE ARE ADDING A NEW FEATURE CALLED 'CANCEL MY REJECTION'. THAT WON'T WORK
				// IF PENDING APPROVERS ARE REMOVED
				//removeRequirementPendingAppovers(con, requirement);
				setRequirementStatusAndLog(con, requirement, "Rejected");
				return;
			}
			
			// There are some role types, where a single approval is sufficient for approval completion.
			// lets cycle through all the roles that need to approve this req
			// for each role, lets check  if approval fulfilment is done
			// 			for the ALL must approve type-  Have ALL the approvers for thsi role approved 
			//			for the ANY ONE must approve type - Have any one approved
			//	if all roles have been approved, lets remove any 'Pending' approvers, as this role req has completed cycle.
			//	
			// step1 : Get all Roles that need to approve this requirement
			// step 2 : For each role, see if this is a Approval By ANY or Approval by ALL type
			// 		step 3 : Is this a ANY approval type , if so has any one has responded .
			//  	step 4 : Is this an ALL must approve type. if so has every one has responded. 
			// step 5 : if alll the roles have been approved, remove any Pending approvals.
			boolean respondedByAllRoles  = true;
			
			// step 1 : get roles that need to approve this requirement.
		/*	String sql = "select rl.id, rl.project_id, rl.name, rl.description, rl.approval_type " +
						"		from gr_requirements r, gr_role_privs rp, gr_roles rl " +
						" 		where r.id = ? " +
						"		and r.folder_id = rp.folder_id " +
						"		and rp.approve_requirement = 1 " +
						" 		and rp.role_id = rl.id  " ;
			
			*/
			// get all the roles that can approve this req. This includes static roles and dynamic ones.. 
			String sql = "select rl.id, rl.project_id, rl.name, rl.description, rl.approval_type " +
					"		from gr_requirements r, gr_role_privs rp, gr_roles rl " +
					" 		where r.id = ? " +
					"		and r.folder_id = rp.folder_id " +
					"		and rp.approve_requirement = 1 " +
					" 		and rp.role_id = rl.id  " + 
					" union " 			+ 
					"  select  rl.id, rl.project_id, rl.name, rl.description, rl.approval_type  " + 
					" from gr_dynamic_roles dr, gr_roles rl " + 
					" where dr.requirement_id = ? "	+
					" and dr.role_id = rl.id "
								;
						
						
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirement.getRequirementId());
			prepStmt.setInt(2, requirement.getRequirementId());
		
			rs = prepStmt.executeQuery();
			
			while (rs.next()) {	
				int roleId = rs.getInt("id");
				int projectId = rs.getInt("project_id");
				String roleName = rs.getString("name");
				String roleDescription = rs.getString("description");
				Role role = new Role( roleId,  projectId,  roleName,  roleDescription);
				
				// loop through all the roles. If you find any 1 role with 'pending' approval status, then set respondedByAllRoles to false
				String roleApprovalStatus = RequirementUtil.getDecisionByRole(requirement, role);
				System.out.println("srt in setFinalApprovalStatus . role name is " + roleName + "  approvalstatus for role is " + roleApprovalStatus);
				if (roleApprovalStatus.equals("pending")){
					respondedByAllRoles = false;
				}
				if (roleApprovalStatus.equals("waiting")){
					respondedByAllRoles = false;
				}
				
			}
			
			rs.close();
			prepStmt.close();
			
			// If all the required approvers have responded (for Any1Mayapprove at least 1, for AllMustApprove all members)
			// then delete pending approvers 
			if (respondedByAllRoles){

				
				// respondedByAll Roles. so delete any pending approvers.
				removeRequirementPendingAppovers(con, requirement);
				setRequirementStatusAndLog(con, requirement, "Approved");
			}
			else {
				// do nothing
			}
			rs.close();
			prepStmt.close();
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
	}

	

	public static String getDecisionByRole(Requirement requirement, Role role){
		// Iterate through all the approvers in RAH for a req , version and role-members combination
		// The goal is to count the number of Pending, Rejected and Approved.
		// For this role, even if it is 'Pending' , if the role's name  appears in the 'appoved-roles' value
		// for a req, version, approver combination, then do not count towards Pending. count towards 'approved'.
		
		String decision = "pending";
		
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			

			int count = 0;
			int approved = 0;
			int rejected = 0;
			int pending = 0;
			int waiting = 0;
			
			 String sql = " select rah.approver_email_id, rah.response, ifnull(rah.approved_roles, '') 'approved_roles' " + 
						" from gr_user_roles ur, gr_users u, gr_requirement_approval_h rah  " +
						" where ur.role_id =  ? " + 
						" and ur.user_id = u.id " +
						" and u.email_id = rah.approver_email_id " + 
						" and rah.requirement_id = ? and rah.version  = ?  ";
			 
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, role.getRoleId());
				prepStmt.setInt(2, requirement.getRequirementId());

				prepStmt.setInt(3, requirement.getVersion() );
				
				rs = prepStmt.executeQuery();
				
				String response = "";
				String approvedRoles = "";
				
				while (rs.next()){
					response = rs.getString("response");
					approvedRoles = rs.getString("approved_roles");
					if (response.equals("Pending")){
						
						// Even though this user's overall response is pending,
						// if the role is in approved_roles list, then it should count toward approved.	
						if (approvedRoles.contains(role.getRoleName())){
							approved++;
						}
						else {
							// if the role is not in the approved_roles list, then this row should count towards pending.
							pending++;
						}
						
						 
					}
					if (response.equals("Waiting")){
						// Even though this user's overall response is waiting,
						// if the role is in approved_roles list, then it should count toward approved.	
						if (approvedRoles.contains(role.getRoleName())){
							approved++;
						}
						else {
							// if the role is not in the approved_roles list, then this row should count towards waiting.
							waiting++;
						}
					}
					if (response.equals("Approved")){
						approved++;
					}
					if (response.equals("Rejected")){
						rejected++;
					}
				}
				rs.close();
				prepStmt.close();

				
			if (role.getApprovalType().equals("ApprovalByAny")){
				// If any 1 has rejected : return rejected
				// if 0 rejections and any 1 has approved : return approved
				//  if  > 0 pending : return pending
				// if 0 pending , 0 rejected, 0 approved, then return no approvers
				
				if (rejected > 0){
					decision = "rejected";
				}
				else if ((rejected == 0) && (approved > 0)){
					decision = "approved";
				}
				else if (pending > 0 ){
					decision = "pending";
				}
				else {
					decision = "waiting";
				}
				
				// SRT To do 
				// if this decision is approved, remove pending.
				if (decision.equals("approved")){
					// for all users that are in pending, set them to approved for that role. 
					// or if they
				
					System.out.println("SRT: This role " + role.getRoleName() + " has been approved, so deleting any pending approvers");
					// find all members of this role who have not approved. i.e in waiting and delete them
					
					sql  = "delete from gr_requirement_approval_h "						
							+ " where requirement_id = ? "
							+ " and version = ? "
							+ " and approver_email_id = ? " 
							+ "	and response in ('Pending') " ;
					ArrayList<User> members =RoleUtil.getAllUsersInRole(role.getRoleId(), "mySQL");
					prepStmt = con.prepareStatement(sql);
	
					for (User u: members){
						ArrayList<HashMap<String, String>> roles = requirement.getApproversRolesForUser(u.getEmailId());
						
						if (roles.size() == 1){
							// this user belongs to 1 approval role only . So we can delete him.

							prepStmt.setInt(1, requirement.getRequirementId());
							prepStmt.setInt(2, requirement.getVersion());
							prepStmt.setString(3, u.getEmailId());
							prepStmt.execute();
						}
					}
					prepStmt.close();
				
				}
				
			}
			if (role.getApprovalType().equals("ApprovalByAll")){
				// if pending  > 0, then return pending.
				// if 0 pending, and any 1 has rejected, then return rejected.
				// if 0 pending, 0 rejected, and any 1 has approved, then return approved
				// if 0 pending, 0 rejected and 0 approved, then return no approvers.
				
				if (pending > 0){
					decision = "pending";
				}
				else if ((pending == 0) && (rejected > 0)){
					decision = "rejected";
				}
				else if ((pending == 0) && (rejected ==0 ) && (approved > 0 )){
					decision = "approved";
				}
				else if (waiting > 0 ){
					decision = "waiting";
				}
				else {
					decision = "pending";
				}
			}
				
		
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
		
	
		
		return decision;
	}

	// take in requirementId, user object and comment and creates an entry in the
	// comment table.
	// as a last step, createComment also called notifyRequirementStakeHolders.
	public static void createComment(int requirementId,User user,String comment_note, 
		HttpServletRequest request, String databaseType) {
		
		if ((comment_note == null ) || (comment_note.equals(""))){
			return;
		}
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			Requirement requirement = new Requirement (requirementId,  databaseType);
			
			String sql = "";
			// now lets add the note to the comments table.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_requirement_comments " +
					" (requirement_id, version, commenter_email_id, comment_note, comment_dt) " +
					" values (?, ? , ? , ? , now())";
			}
			else {
				sql = "insert into gr_requirement_comments " +
				" (requirement_id, version, commenter_email_id, comment_note, comment_dt) " +
				" values (?, ? , ? , ? ,sysdate)";
		
			}
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirement.getRequirementId());
			prepStmt.setInt(2, requirement.getVersion());
			prepStmt.setString(3, user.getEmailId());				
			prepStmt.setString(4, comment_note);			
			prepStmt.execute();				

			String serverName = request.getServerName();

			RequirementUtil.notifyRequirementStakeHolders(requirementId, "newComment", request,  databaseType, "", "");
			
			prepStmt.close();
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
	}

	public static void createComment(int requirementId,String commenterEmailId ,String comment_note, 
			HttpServletRequest request, String databaseType) {
			
			if ((comment_note == null ) || (comment_note.equals(""))){
				return;
			}
			PreparedStatement prepStmt = null;
			ResultSet rs = null;
			java.sql.Connection con = null;
			try {
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();
				Requirement requirement = new Requirement (requirementId,  databaseType);
				
				String sql = "";
				// now lets add the note to the comments table.
				if (databaseType.equals("mySQL")){
					sql = "insert into gr_requirement_comments " +
						" (requirement_id, version, commenter_email_id, comment_note, comment_dt) " +
						" values (?, ? , ? , ? , now())";
				}
				else {
					sql = "insert into gr_requirement_comments " +
					" (requirement_id, version, commenter_email_id, comment_note, comment_dt) " +
					" values (?, ? , ? , ? ,sysdate)";
			
				}
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirement.getRequirementId());
				prepStmt.setInt(2, requirement.getVersion());
				prepStmt.setString(3, commenterEmailId);				
				prepStmt.setString(4, comment_note);			
				prepStmt.execute();				

				String serverName = request.getServerName();

				RequirementUtil.notifyRequirementStakeHolders(requirementId, "newComment", request,  databaseType, "", "");
				
				prepStmt.close();
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
		}


	// take in requirementId, and change Type and notifies all the stake holders.
	public static void notifyRequirementStakeHolders(int requirementId, String changeType, 
			HttpServletRequest request, String databaseType, String newRequirementName, String newRequirementDescription ) {
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			Requirement requirement = new Requirement (requirementId,  databaseType);
			Project project = new Project(requirement.getProjectId(),  databaseType);
			
			ArrayList to = RequirementUtil.getRequirementStakeHolders(requirement) ;
			

			
			if (changeType.equals("newComment")) {
				String sql = "";
				String comment_note = "";
				String commentDt = "";
				String commenter  = "";

				
				
				String projectName = project.getProjectName();
				String messageType = "newCommentAddedToRequirement";					
 
				
				String messageBody = "";
				messageBody += "<br><table border='0' bgcolor='#d9edf7' width='900'><tr><td> Project  </td><td> " + requirement.getProjectShortName() + "</td></tr>";
				String serverName = request.getServerName();
				messageBody += "<tr><td> <a href=https://"+ serverName +"/GloreeJava2/servlet/DisplayAction?dO=req&dReqId=" 
						+ requirementId + ">" + requirement.getRequirementFullTag() +"</a> </td>" 
						+ "<td> " + requirement.getRequirementName() +"</td></tr>";
				
				messageBody += "<tr><td>Description</td><td>" + requirement.getRequirementDescription() + "</td></tr>";
				
					messageBody += "<tr><td>Comments</td><td><table border='0' bgcolor='#f2dede' width='700'>";
					if (databaseType.equals("mySQL")){
						sql = " select rc1.commenter_email_id, rc1.comment_note," +
						 	" date_format(rc1.comment_dt, '%d %M %Y %r ') \"comment_dt\" , u.first_name, u.last_name " + 
						 	" from gr_requirement_comments rc1, gr_users u " + 
						 	" where rc1.requirement_id = ? " + 
						 	" and rc1.commenter_email_id = u.email_id " + 
						 	" order by rc1.id desc ; ";
						
						prepStmt = con.prepareStatement(sql);
						prepStmt.setInt(1, requirementId);
						rs = prepStmt.executeQuery();
						
						while (rs.next()) { 
							comment_note = rs.getString("comment_note");
							commentDt  = rs.getString("comment_dt");
							String firstName = rs.getString("first_name");
							String lastName = rs.getString("last_name");
							commenter = firstName  + " " + lastName;
							messageBody += 	"<tr><td colspan='2' bgcolor='#fcf8e3'>" + comment_note + "</td></tr><tr><td> "+ commenter + " </td><td>" +  commentDt + "</td></tr>";

						}
						rs.close();
						prepStmt.close();
					}
					messageBody += "</table></td></tr>";
					
				
				messageBody += "</table>";	
				

				
				Iterator i = to.iterator();
				while (i.hasNext()) {
					String toEmailId = (String) i.next();
					// to avoid duplicate emails going out for multiple comments, lets remove any previous messages to this user about this comment.
					// Delete from gr_messages where to_emaildId is i.next() and messagetype is newCommentAddedToRequirement and messageBody starts
					sql = "delete from gr_messages "
							+ " where to_email_id = ? "
							+ " and message_type = ? "
							+ " and message_sent_dt is null "
							+ " and message_body like '%>"+  requirement.getRequirementFullTag()  + "</a>%' ";

					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, toEmailId);
					prepStmt.setString(2, "newCommentAddedToRequirement");
					prepStmt.execute();
					
					EmailUtil.storeMessage(projectName, toEmailId, messageType, messageBody,  databaseType);
				}				
			}
			else if (changeType.equals("newVersion")) {

				// requirement has not been updated yet. 
				String projectName = project.getProjectName();
				String messageType = "requirementNameOrDescriptionChanged";			
				
				String serverName = request.getServerName();
				
				
				if (newRequirementName.equals("")){
					newRequirementName =requirement.getRequirementName() ; 
				}
				if (newRequirementDescription.equals("")){
					newRequirementDescription = requirement.getRequirementDescription();
				}
				
				int newVersion = requirement.getVersion();
				int oldVersion = newVersion - 1;
				String messageBody = "";
				messageBody += "<br><table border='0' bgcolor='#d9edf7' width='900'>";
				messageBody += "<tr><td width='200'> <a href=https://"+ serverName +"/GloreeJava2/servlet/DisplayAction?dO=req&dReqId=" 
						+ requirementId + ">" + requirement.getRequirementFullTag() + "(Version-"  + 
						newVersion +") </a> </td>" 
						+ "<td> </td></tr>";

				messageBody += "<tr><td> Project  </td><td> " + requirement.getProjectShortName() + "</td></tr>";

				messageBody += "<tr><td>Folder</td><td>" + requirement.getFolderPath() + "</td></tr>";
				messageBody += "<tr><td colspan=2>&nbsp;</td></tr>";
				
				messageBody += "<tr><td> Version-"+ newVersion +" : Name </td>" 
						+ "<td> " + newRequirementName +"</td></tr>";
				messageBody += "<tr><td> Version-"+ newVersion +" : Description </td>" 
						+ "<td> " + newRequirementDescription+"</td></tr>";

				messageBody += "<tr><td colspan=2>&nbsp;</td></tr>";

				messageBody += "<tr><td  bgcolor='#fcf8e3'>Version-" + oldVersion + " : Name </td><td  bgcolor='#fcf8e3'>" + requirement.getRequirementName()+ "</td></tr>";
				messageBody += "<tr><td  bgcolor='#fcf8e3'>Version-" + oldVersion + " : Description </td><td  bgcolor='#fcf8e3'>" + requirement.getRequirementDescription()+ "</td></tr>";
				
				messageBody += "</table>";	
				
				

				Iterator i = to.iterator();
				while (i.hasNext()) {
					String toEmailId = (String) i.next();
					EmailUtil.storeMessage(projectName, toEmailId, messageType, messageBody,  databaseType);
				}				
			}
			
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
	}

	// take in requirementId, and returns an ArrayList of stakeholder email ids.
	public static ArrayList getRequirementStakeHolders(Requirement requirement) {
		
		ArrayList stakeholders = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			int requirementId = requirement.getRequirementId();
			int folderId = requirement.getFolderId();
			
			// now lets get all the stake holders.
			
			// those who commented and those who have approval permissions
			String sql = " " + 
				" select approver_email_id \"stakeholder_email_id\" " + 
				" from gr_requirement_approval_h rah " +
				" where rah.requirement_id = ?  " + 
				" union " +
				" select commenter_email_id \"stakeholder_email_id\" " + 
				" from gr_requirement_comments rc " +
				" where rc.requirement_id = ? " +
				" union " + 
				" select owner \"stakeholder_email_id\" " + 
				" from gr_requirements r " + 
				" where r.id = ? ";
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.setInt(2, requirementId);
			prepStmt.setInt(3, requirementId);
			rs = prepStmt.executeQuery();				
			
			// we add this use to the stake holder lists, only if the user still has read permissions.
			// use case :  a user worked for company x, and had permissions, but has since changed roles / moved
			// to a different project, and doesn't have read permissions on the req. he / she should not 
			// get notified.

			sql = " select  count(*) \"rolesWithReadPermissions\" " + 
					" from gr_users  u , gr_user_roles ur , gr_role_privs rp " + 
					" where u.email_id = ? " +
					" and u.id = ur.user_id " +
					" and ur.role_id = rp.role_id " +					
					" and rp.read_requirement = 1 " +
					" and folder_id = ?";
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				
			while (rs.next()){
				String stakeHolderEmailId = rs.getString("stakeholder_email_id");
				// lets check to see if the stake holder still has read permissions on this req.
				prepStmt2.setString(1, stakeHolderEmailId);
				prepStmt2.setInt(2, folderId);
				ResultSet rs2 = prepStmt2.executeQuery();
				int rolesWithReadPermissions =  0;
				while (rs2.next()){
					rolesWithReadPermissions = rs2.getInt("rolesWithReadPermissions");
				}
				
				
				if (rolesWithReadPermissions > 0 ){
					// this stakeholder has read permission to this req, so he is a valid stake holder.
					stakeholders.add(stakeHolderEmailId);
				}
			}
			rs.close();
			prepStmt.close();
			
			
			// those whose email id's exist in Keep Me Informed field
			String keepMeInformedString = requirement.getAttributeValue("Keep Me Informed");
			if ((keepMeInformedString != null) && (!(keepMeInformedString.equals("")))){ 
				if (keepMeInformedString.contains(",")){
					// more than one value, split into array
					String [] keepMeInformed =  keepMeInformedString.split(",");
					for (int i=0; i<keepMeInformed.length; i++){
						String informedEmailId = keepMeInformed[i];
						informedEmailId = informedEmailId.trim();
						// lets add it to stakeholders
						stakeholders.add(informedEmailId);
					}
					
				}
				else {
					// only one value, so use as is
					String informedEmailId = keepMeInformedString;
					informedEmailId = informedEmailId.trim();
					// lets add it to stakeholders
					stakeholders.add(informedEmailId);
				}
				
			}
			
			
			
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
		return (stakeholders);
	}


	
	// called when a req needs to be added to a baseline. We take in the req id as a param, and
	// return a list of rTbaseline objects.
	// This will be displayed as a pull
	// down for the user to select from.
	public static ArrayList getEligibleBaselinesForRequirement(int requirementId) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList rTBaselines = new ArrayList();
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the list of folders that support the requirement
			// type of the requirement parameter.
			//
			
			String sql = "select b.id, b.requirement_type_id, b.name,b.locked, b.description,  " +
			" b.created_by, b.created_dt, b.last_modified_by , b.last_modified_dt " + 
			" from gr_rt_baselines b, gr_requirements r " +
			" where r.id= ? " +
			" and r.requirement_type_id = b.requirement_type_id " +
			" order by b.name";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();

			while (rs.next()) {
				int baselineId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				String baselineName = rs.getString("name");
				int locked = rs.getInt("locked");
				String baselineDescription = rs.getString ("description");
				String createdBy = rs.getString("created_by");
				//this.createdDt = rs.getDate("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by") ;
				//this.lastModifiedDt = rs.getDate("last_modified_by");
				
				RTBaseline rTBaseline = new RTBaseline(baselineId, requirementTypeId, 
					baselineName, locked, baselineDescription, createdBy, lastModifiedBy);
				rTBaselines.add(rTBaseline);
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
		return (rTBaselines);
	}

	
	// 	returns the path of the folder in which this requirement resides.
	public static String getRequirementFolderPath(int requirementId) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		String folderPath = "";
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the list of folders that support the requirement
			// type of the requirement parameter.
			//
			
			String sql = "select f.folder_path " + 
				" from gr_folders f, gr_requirements r " + 
				" where r.id = ? " + 
				" and r.folder_id = f.id"; 
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();

			while (rs.next()) {
				folderPath = rs.getString("folder_path");
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
		return (folderPath);
	}

	
	public static int getNotifyOwnerOnChange(int requirementId) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		int notify_on_owner_change = 0;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the list of folders that support the requirement
			// type of the requirement parameter.
			//
			
			String sql = "select rt.notify_on_owner_change " + 
				" from gr_requirement_types rt , gr_requirements r " + 
				" where r.id = ? " + 
				" and r.requirement_type_id = rt.id"; 
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();

			while (rs.next()) {
				notify_on_owner_change = rs.getInt("notify_on_owner_change");
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
		return (notify_on_owner_change);
	}

	
	public static int getNotifyOnApprovalChange(int requirementId) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		int notify_on_approval_change = 0;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the list of folders that support the requirement
			// type of the requirement parameter.
			//
			
			String sql = "select rt.notify_on_approval_change " + 
				" from gr_requirement_types rt , gr_requirements r " + 
				" where r.id = ? " + 
				" and r.requirement_type_id = rt.id"; 
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();

			while (rs.next()) {
				notify_on_approval_change = rs.getInt("notify_on_approval_change");
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
		return (notify_on_approval_change);
	}

	// called when someone clicks on 'Add Requirement To Baseline'.
	public static void addRequirementToBaseline(int requirementId, int rTBaselineId, User user,
			HttpServletRequest request, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			RTBaseline rTBaseline = new RTBaseline(rTBaselineId);
			// lets get the version id of the latest version of  requirement.
			String sql = " select max(id) \"version_id\" " +
					" from gr_requirement_versions " +
					" where requirement_id = ?  ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();
			
			int versionId = 0;
			while (rs.next()) {
				versionId = rs.getInt("version_id");
			}
			rs.close();
			prepStmt.close();
			
			// lets add an entry in the gr_requirement_baseline table.
			
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_requirement_baselines " +
					" (requirement_id, version_id, rt_baseline_id, baselined_dt) " +
					" values (?, ?, ? , now()) ";
			}
			else {
				sql = " insert into gr_requirement_baselines " +
				" (requirement_id, version_id, rt_baseline_id, baselined_dt) " +
				" values (?, ?, ? , sysdate) ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.setInt(2, versionId);
			prepStmt.setInt(3, rTBaselineId);
			prepStmt.execute();

			prepStmt.close();
			
			// lets make a log entry that states that this version of req is rejected
			String log = " Added this requirement to Baseline " + rTBaseline.getBaselineName();
			RequirementUtil.createRequirementLog(requirementId, log, user.getEmailId(),  databaseType);
			
			
						
			
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

	}

	
	// called when someone clicks on 'Remove Requirement From Baseline'.
	public static void removeRequirementFromBaseline(int requirementId, int requirementBaselineId, User user,
		HttpServletRequest request, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			RequirementBaseline requirementBaseline = new RequirementBaseline(requirementBaselineId,  databaseType);
			
			// lets add an entry in the gr_requirement_baseline table.

			String sql = " delete from  gr_requirement_baselines " +
				" where id = ?  ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementBaselineId);
			prepStmt.execute();

			prepStmt.close();
			
			// lets make a log entry that states that this version of req is rejected
			String log = " Removed this requirement from Baseline " + requirementBaseline.getRTBaselineName();
			RequirementUtil.createRequirementLog(requirementId, log, user.getEmailId(),  databaseType);
			
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

	}

	
	// Returns an arraylist of strings of baselines this requirement was part of
	// the string is version:##:baseline name.
	public static ArrayList getRequirementBaselines(int requirementId, String databaseType) {
		
		ArrayList requirementBaselines = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			// now lets get all the the requirement baselines and create an object for each one..
			
			
			
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " select rtb.name \"baseline_name\", rtb.locked," +
					" rb.id, rb.requirement_id, rb.version_id, v.version, " + 
					" date_format(rb.baselined_dt , '%d %M %Y %r ') \"baselined_dt\" , v.name, v.description, " +
					" v.approvers , v.status, v.priority, v.owner, v.pct_complete, v.external_url," +
					" v.trace_to, v.trace_from, v.user_defined_attributes " +
					" from gr_requirement_baselines rb, gr_requirement_versions v , " +
					"	gr_rt_baselines rtb " +
					" where rb.requirement_id = ? " +
					" and rb.version_id = v.id " +
					" and rb.rt_baseline_id = rtb.id ";
			}
			else {
				sql = " select rtb.name \"baseline_name\", rtb.locked, " +
					" rb.id, rb.requirement_id, rb.version_id, v.version, " + 
					" to_char(rb.baselined_dt , 'DD MON YYYY') \"baselined_dt\" , v.name, v.description, " +
					" v.approvers , v.status, v.priority, v.owner, v.pct_complete, v.external_url," +
					" v.trace_to, v.trace_from, v.user_defined_attributes " +
					" from gr_requirement_baselines rb, gr_requirement_versions v , " +
					"	gr_rt_baselines rtb " +
					" where rb.requirement_id = ? " +
					" and rb.version_id = v.id " +
					" and rb.rt_baseline_id = rtb.id ";
				
			}
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();				
			
			while (rs.next()){
				String rTBaselineName = rs.getString("baseline_name");
				int requirementBaselineId = rs.getInt("id");
				int locked = rs.getInt("locked");
				//this.requirementId = rs.getInt("requirement_id");
				int requirementVersionId = rs.getInt("version_id");
				int requirementBaselineVersion = rs.getInt("version");
				String requirementBaselinedDt = rs.getString("baselined_dt");
				String requirementBaselinedName = rs.getString("name");				
				String requirementBaselinedDescription = rs.getString("description");
				String requirementBaselinedApprovers = rs.getString("approvers");
				String requirementBaselinedStatus = rs.getString("status");
				String requirementBaselinedPriority = rs.getString("priority");
				String requirementBaselinedOwner = rs.getString("owner");
				int  requirementBaselinedPctComplete = rs.getInt("pct_complete");
				String requirementBaselinedExternalURL = rs.getString("external_url");
				String requirementBaselinedTraceTo = rs.getString("trace_to");
				String requirementBaselinedTraceFrom = rs.getString("trace_from");
				String requirementBaselinedUserDefinedAttributes = rs.getString("user_defined_attributes");

				
				RequirementBaseline requirementBaseline = new RequirementBaseline (requirementBaselineId,
					rTBaselineName,locked, requirementId, requirementVersionId, requirementBaselineVersion,
					requirementBaselinedDt, requirementBaselinedName, requirementBaselinedDescription, 
					requirementBaselinedApprovers, requirementBaselinedStatus,  requirementBaselinedPriority,
					requirementBaselinedOwner , requirementBaselinedPctComplete , requirementBaselinedExternalURL, 
					requirementBaselinedTraceTo , requirementBaselinedTraceFrom , requirementBaselinedUserDefinedAttributes );
						
				requirementBaselines.add(requirementBaseline);
			}
			rs.close();
			prepStmt.close();
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
		return (requirementBaselines);
	}

	// this is a wrapper on getRequirementBaselines. It returns 
	// the baseline a req is part of , in a string format.
	public static String  getRequirementBaselineString(int requirementId, String databaseType) {
		
	    String requirementBaselineString = "";
	    ArrayList requirementBaselines = RequirementUtil.getRequirementBaselines(requirementId,  databaseType);
	    if (requirementBaselines != null){
	    	Iterator m = requirementBaselines.iterator();
	    	while ( m.hasNext() ) {
	    		RequirementBaseline requirementBaseline = (RequirementBaseline) m.next();
	    		requirementBaselineString += requirementBaseline.getRTBaselineName() + 
					"(V-" + requirementBaseline.getRequirementBaselinedVersion() + "), ";
	    	}
	    }
	    // lets drop the last , 
	    if (requirementBaselineString.contains(",")){
	    	requirementBaselineString = (String) requirementBaselineString.subSequence(0,requirementBaselineString.lastIndexOf(","));
	    }			    
		return (requirementBaselineString);
	}

	
	
	// Returns an arraylist of strings of baselines this requirement was part of
	// the string is version:##:baseline name.
	public static boolean requirementBaselineAlreadyExists(int requirementId, int rTBaselineId) {
		
		boolean exists = false;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			// now lets get all the the count(*) of reqs with this rTaselineId
			
			String sql = " select count(*) \"matches\" " +
			" from gr_requirement_baselines rb " +
			" where rb.requirement_id = ? " +
			" and rb.rt_baseline_id = ?  ";
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.setInt(2, rTBaselineId);			
			rs = prepStmt.executeQuery();				

			while (rs.next()){
				int matches = rs.getInt("matches") ;
				if (matches > 0 ) {
					exists = true;
				}
			}
			rs.close();
			prepStmt.close();
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
		return (exists);
	}

	// returns the requirementBaselineId of the requiremetn / rTBaselineid combo.
	public static int getRequirementBaselineId(int requirementId, int rTBaselineId) {
		
		int requirementBaselineId = 0;
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			// now lets get all the the count(*) of reqs with this rTaselineId
			
			String sql = " select rb.id " +
			" from gr_requirement_baselines rb " +
			" where rb.requirement_id = ? " +
			" and rb.rt_baseline_id = ?  ";
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.setInt(2, rTBaselineId);			
			rs = prepStmt.executeQuery();				

			while (rs.next()){
				requirementBaselineId = rs.getInt("id") ;
			}
			rs.close();
			prepStmt.close();
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
		return (requirementBaselineId);
	}

	
	// returns an array list of Requirement Versions for this requirement.
	public static ArrayList getRequirementVersions(int requirementId, String databaseType) {
		
		ArrayList requirementVersions= new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			// now lets get all the the requirement baselines and create an object for each one..
			
			
			
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " select id, version, name, description, " +
					" approvers, priority, status, owner, pct_complete, external_url," +
					" trace_to, trace_from, user_defined_attributes, " +
					" created_by," +
					" date_format(created_dt, '%d %M %Y %r ') \"created_dt\"" +
					" from gr_requirement_versions where requirement_id = ? " +
					" order by version desc ";
			}
			else {
				sql = " select id, version, name, description, " +
				" approvers, priority, status, owner, pct_complete, external_url," +
				" trace_to, trace_from, user_defined_attributes, " +
				" created_by," +
				" to_char(created_dt, 'DD MON YYYY') \"created_dt\"" +
				" from gr_requirement_versions where requirement_id = ? " +
				" order by version desc ";
			}
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();				
			
			while (rs.next()){
				int versionId = rs.getInt("id");
				int version = rs.getInt("version");
				String versionName = rs.getString("name");
				String versionDescription = rs.getString("description");
				String versionCreatedBy = rs.getString("created_by");
				String versionCreatedDt = rs.getString("created_dt");
				String versionApprovers = rs.getString("approvers");
				String versionPriority= rs.getString("priority");
				String versionStatus = rs.getString("status");
				String versionOwner= rs.getString("owner");
				int versionPctComplete= rs.getInt("pct_complete");
				String versionExternalURL= rs.getString("external_url");
				String versionTraceTo = rs.getString("trace_to");
				String versionTraceFrom = rs.getString("trace_from");
				String versionUserDefinedAttributes = rs.getString("user_defined_attributes");
				
				RequirementVersion requirementVersion = new RequirementVersion(versionId,requirementId,
					version, versionName, versionDescription,
					versionCreatedBy, versionCreatedDt, 
					versionApprovers , versionStatus, versionPriority, versionOwner,
					versionPctComplete, versionExternalURL , versionTraceTo, 
					versionTraceFrom, versionUserDefinedAttributes );
						
				requirementVersions.add(requirementVersion);
			}
			rs.close();
			prepStmt.close();
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
		return (requirementVersions);
	}

	
	

		public static RequirementVersion getRequirementVersion(int requirementId, int version ) {

		RequirementVersion requirementVersion = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			// now lets get all the the requirement baselines and create an object for each one..
			
			
			
			String sql = "";
				sql = " select id, version, name, description, " +
					" approvers, priority, status, owner, pct_complete, external_url," +
					" trace_to, trace_from, user_defined_attributes, " +
					" created_by," +
					" date_format(created_dt, '%d %M %Y %r ') \"created_dt\"" +
					" from gr_requirement_versions where requirement_id = ? and version  = ? " +
					" order by version desc ";
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.setInt(2, version);
			rs = prepStmt.executeQuery();				
			
			while (rs.next()){
				int versionId = rs.getInt("id");
				String versionName = rs.getString("name");
				String versionDescription = rs.getString("description");
				String versionCreatedBy = rs.getString("created_by");
				String versionCreatedDt = rs.getString("created_dt");
				String versionApprovers = rs.getString("approvers");
				String versionPriority= rs.getString("priority");
				String versionStatus = rs.getString("status");
				String versionOwner= rs.getString("owner");
				int versionPctComplete= rs.getInt("pct_complete");
				String versionExternalURL= rs.getString("external_url");
				String versionTraceTo = rs.getString("trace_to");
				String versionTraceFrom = rs.getString("trace_from");
				String versionUserDefinedAttributes = rs.getString("user_defined_attributes");
				
				requirementVersion = new RequirementVersion(versionId,requirementId,
					version, versionName, versionDescription,
					versionCreatedBy, versionCreatedDt, 
					versionApprovers , versionStatus, versionPriority, versionOwner,
					versionPctComplete, versionExternalURL , versionTraceTo, 
					versionTraceFrom, versionUserDefinedAttributes );
						
				
			}
			rs.close();
			prepStmt.close();
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
		return (requirementVersion);
	}


	// returns an array list of Requirement in upstream of this req for given levels. Works for a max level of 3
	// stops the CIA Build when the max Results count is reached.
	public static ArrayList getUpStreamCIARequirements(SecurityProfile securityProfile, int requirementId, int cIADepth, int maxResults,
			String databaseType) {
		
		ArrayList cIARequirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			Requirement seedRequirement = new Requirement(requirementId, databaseType);
			int counter = 0;
			
			// this sql gets the list of requirements that this requirement traces up to.
			String traceUpSQL = "";
				traceUpSQL = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id,"
					+ " r.name, "
					+ " r.description, r.tag, r.full_tag,"
					+ " r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\","
					+ " r.approvers  ,"
					+ " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
					+ " r.external_url, r.trace_to, r.trace_from,  r.user_defined_attributes, r.testing_status, "
					+ " r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\"," 
					+ " r.last_modified_by, "
					+ " r.last_modified_dt, rt.name \"requirement_type_name\", t.suspect, " 
					+ " t.description \"traceDescription\" , t.id \"traceId\" "
					+ " FROM gr_requirements r , gr_requirement_types rt, gr_traces t, gr_folders f "
					+ " where t.from_requirement_id = ? "
					+ " and t.to_requirement_id = r.id "
					+ " and r.requirement_type_id = rt.id " 
					+ " and r.folder_id = f.id "
					+ " and r.deleted= 0";
			
			if (cIADepth > 0){
				prepStmt = con.prepareStatement(traceUpSQL);
				prepStmt.setInt(1, requirementId);
				rs = prepStmt.executeQuery();
	
				// lets execute the filtered SQL to get the root level requirements.
				// we will build a trace tree for each one of these requirements.
				while (rs.next()) {
					requirementId = rs.getInt("id");
					int folderId = rs.getInt("folder_id");
					int requirementTypeId = rs.getInt("requirement_type_id");
					int projectId = rs.getInt("project_id");
					String requirementName = rs.getString("name");
					String requirementDescription = rs.getString("description");
					String requirementTag = rs.getString("tag");
					String requirementFullTag = rs.getString("full_tag");
					int version = rs.getInt("version");
					String approvedByAllDt = rs.getString("approved_by_all_dt");
					String approvers = rs.getString("approvers");
					String requirementStatus = rs.getString("status");
					String requirementPriority = rs.getString("priority");
					String requirementOwner = rs.getString("owner");
					String requirementLockedBy = rs.getString("locked_by");
					int requirementPctComplete = rs.getInt("pct_complete");
					String requirementExternalUrl = rs.getString("external_url");
					String traceTo = rs.getString("trace_to");
					String traceFrom = rs.getString("trace_from");
					String userDefinedAttributes = rs
							.getString("user_defined_attributes");
					String testingStatus = rs.getString("testing_status");
					int deleted = rs.getInt("deleted");
					String folderPath = rs.getString("folder_path");
					String createdBy = rs.getString("created_by");
					String createdDt = rs.getString("created_dt");
					String lastModifiedBy = rs.getString("last_modified_by");
					// lastModifiedDt = rs.getDate("last_modified_by");
					String requirementTypeName = rs.getString("requirement_type_name");
					String traceDescription = rs.getString("traceDescription");
					int traceId = rs.getInt("traceId");
					
					Requirement requirement1 = new Requirement(requirementId,
							requirementTypeId, folderId, projectId,
							requirementName, requirementDescription,
							requirementTag, requirementFullTag, version,
							approvedByAllDt, approvers, requirementStatus,
							requirementPriority, requirementOwner, requirementLockedBy,
							requirementPctComplete, requirementExternalUrl,
							traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
							createdBy, lastModifiedBy, requirementTypeName, createdDt);
	
					// if the user does not have read permissions on this requirement,
					// lets redact it. i.e. remove all sensitive infor from it.
					if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
							+ requirement1.getFolderId()))){
						requirement1.redact();
					}
					
					String traceFromFullTag = seedRequirement.getRequirementFullTag();
					String traceToFulllTag = requirement1.getRequirementFullTag();
					
					TraceTreeRow traceTreeRow = new TraceTreeRow(1,rs.getInt("suspect") , traceId, traceDescription, requirement1);

					cIARequirements.add(traceTreeRow);
					if (counter++ > maxResults){
						con.close();
						return (cIARequirements);
					}
	
					if (cIADepth > 1) {
						PreparedStatement prepStmt2 = con.prepareStatement(traceUpSQL);
						prepStmt2.setInt(1, requirement1.getRequirementId());
						ResultSet rs2 = prepStmt2.executeQuery();
			
						// lets execute the filtered SQL to get the root level requirements.
						// we will build a trace tree for each one of these requirements.
						while (rs2.next()) {
							requirementId = rs2.getInt("id");
							folderId = rs2.getInt("folder_id");
							requirementTypeId = rs2.getInt("requirement_type_id");
							projectId = rs2.getInt("project_id");
							requirementName = rs2.getString("name");
							requirementDescription = rs2.getString("description");
							requirementTag = rs2.getString("tag");
							requirementFullTag = rs2.getString("full_tag");
							version = rs2.getInt("version");
							approvedByAllDt = rs2.getString("approved_by_all_dt");
							approvers = rs2.getString("approvers");
							requirementStatus = rs2.getString("status");
							requirementPriority = rs2.getString("priority");
							requirementOwner = rs2.getString("owner");
							requirementLockedBy = rs2.getString("locked_by");
							requirementPctComplete = rs2.getInt("pct_complete");
							requirementExternalUrl = rs2.getString("external_url");
							traceTo = rs2.getString("trace_to");
							traceFrom = rs2.getString("trace_from");
							userDefinedAttributes = rs2.getString("user_defined_attributes");
							testingStatus = rs2.getString("testing_status");
							deleted = rs2.getInt("deleted");
							folderPath = rs2.getString("folder_path");
							createdBy = rs2.getString("created_by");
							createdDt = rs2.getString("created_dt");
							lastModifiedBy = rs2.getString("last_modified_by");
							requirementTypeName = rs2.getString("requirement_type_name");
							traceDescription = rs2.getString("traceDescription");
							traceId = rs2.getInt("traceId");
							
							
							Requirement requirement2 = new Requirement(requirementId,
									requirementTypeId, folderId, projectId,
									requirementName, requirementDescription,
									requirementTag, requirementFullTag, version,
									approvedByAllDt, approvers, requirementStatus,
									requirementPriority, requirementOwner, requirementLockedBy,
									requirementPctComplete, requirementExternalUrl,
									traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
									createdBy, lastModifiedBy, requirementTypeName, createdDt);
			
							// if the user does not have read permissions on this requirement,
							// lets redact it. i.e. remove all sensitive infor from it.
							if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
									+ requirement2.getFolderId()))){
								requirement2.redact();
							}

							traceFromFullTag = requirement1.getRequirementFullTag();
							traceToFulllTag = requirement2.getRequirementFullTag();

							TraceTreeRow traceTreeRow2 = new TraceTreeRow(2, rs2.getInt("suspect") , traceId, traceDescription, requirement2);

							cIARequirements.add(traceTreeRow2);
							if (counter++ > maxResults){
								con.close();
								return (cIARequirements);
							}

							if (cIADepth > 2) {
								PreparedStatement prepStmt3 = con.prepareStatement(traceUpSQL);
								prepStmt3.setInt(1, requirement2.getRequirementId());
								ResultSet rs3 = prepStmt3.executeQuery();
					
								// lets execute the filtered SQL to get the root level requirements.
								// we will build a trace tree for each one of these requirements.
								while (rs3.next()) {
									requirementId = rs3.getInt("id");
									folderId = rs3.getInt("folder_id");
									requirementTypeId = rs3.getInt("requirement_type_id");
									projectId = rs3.getInt("project_id");
									requirementName = rs3.getString("name");
									requirementDescription = rs3.getString("description");
									requirementTag = rs3.getString("tag");
									requirementFullTag = rs3.getString("full_tag");
									version = rs3.getInt("version");
									approvedByAllDt = rs3.getString("approved_by_all_dt");
									approvers = rs3.getString("approvers");
									requirementStatus = rs3.getString("status");
									requirementPriority = rs3.getString("priority");
									requirementOwner = rs3.getString("owner");
									requirementLockedBy = rs3.getString("locked_by");
									requirementPctComplete = rs3.getInt("pct_complete");
									requirementExternalUrl = rs3.getString("external_url");
									traceTo = rs3.getString("trace_to");
									traceFrom = rs3.getString("trace_from");
									userDefinedAttributes = rs
											.getString("user_defined_attributes");
									testingStatus = rs3.getString("testing_status");
									deleted = rs3.getInt("deleted");
									folderPath = rs3.getString("folder_path");
									createdBy = rs3.getString("created_by");
									createdDt = rs3.getString("created_dt");
									lastModifiedBy = rs3.getString("last_modified_by");
									// lastModifiedDt = rs3.getDate("last_modified_by");
									requirementTypeName = rs3.getString("requirement_type_name");
									traceDescription = rs3.getString("traceDescription");
									traceId = rs3.getInt("traceId");
									
									
									Requirement requirement3 = new Requirement(requirementId,
											requirementTypeId, folderId, projectId,
											requirementName, requirementDescription,
											requirementTag, requirementFullTag, version,
											approvedByAllDt, approvers, requirementStatus,
											requirementPriority, requirementOwner, requirementLockedBy,
											requirementPctComplete, requirementExternalUrl,
											traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
											createdBy, lastModifiedBy, requirementTypeName, createdDt);
					
									// if the user does not have read permissions on this requirement,
									// lets redact it. i.e. remove all sensitive infor from it.
									if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
											+ requirement3.getFolderId()))){
										requirement3.redact();
									}
									traceFromFullTag = requirement2.getRequirementFullTag();
									traceToFulllTag = requirement3.getRequirementFullTag();

									TraceTreeRow traceTreeRow3 = new TraceTreeRow(3, rs3.getInt("suspect") ,traceId, traceDescription, requirement3);

									cIARequirements.add(traceTreeRow3);
									if (counter++ > maxResults){
										con.close();
										return (cIARequirements);
									}
								}
								rs3.close();
								prepStmt3.close();
							}

						}
						rs2.close();
						prepStmt2.close();
					}
				}
				rs.close();
				prepStmt.close();
			}
		
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
		return (cIARequirements);
	}
	
	

	
	// returns an array list of Requirement in downstream of this req for given levels.
	// stops the CIA build when maxResults is reached.
	public static ArrayList getDownStreamCIARequirements(SecurityProfile securityProfile, int requirementId, int cIADepth, int maxResults,
			String databaseType) {
		
		ArrayList cIARequirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		
		Requirement seedRequirement = new Requirement(requirementId, databaseType);
		
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			int counter = 0;

			// this sql gets the list of requirements that this requirement traces up to.
			String traceDownSQL = "";
				traceDownSQL = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id,"
					+ " r.name, "
					+ " r.description, r.tag, r.full_tag,"
					+ " r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\","
					+ " r.approvers  ,"
					+ " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
					+ " r.external_url, r.trace_to, r.trace_from,  r.user_defined_attributes, r.testing_status, "
					+ " r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\"," 
					+ " r.last_modified_by, "
					+ " r.last_modified_dt, rt.name \"requirement_type_name\", t.suspect, " 
					+ " t.description \"traceDescription\"  , t.id \"traceId\" "
					+ " FROM gr_requirements r , gr_requirement_types rt, gr_traces t, gr_folders f "
					+ " where t.to_requirement_id = ? "
					+ " and t.from_requirement_id = r.id "
					+ " and r.requirement_type_id = rt.id " 
					+ " and r.folder_id = f.id "
					+ " and r.deleted= 0";

			if (cIADepth > 0){
				prepStmt = con.prepareStatement(traceDownSQL);
				prepStmt.setInt(1, requirementId);
				rs = prepStmt.executeQuery();
	
				// lets execute the filtered SQL to get the root level requirements.
				// we will build a trace tree for each one of these requirements.
				while (rs.next()) {
					requirementId = rs.getInt("id");
					int folderId = rs.getInt("folder_id");
					int requirementTypeId = rs.getInt("requirement_type_id");
					int projectId = rs.getInt("project_id");
					String requirementName = rs.getString("name");
					String requirementDescription = rs.getString("description");
					String requirementTag = rs.getString("tag");
					String requirementFullTag = rs.getString("full_tag");
					int version = rs.getInt("version");
					String approvedByAllDt = rs.getString("approved_by_all_dt");
					String approvers = rs.getString("approvers");
					String requirementStatus = rs.getString("status");
					String requirementPriority = rs.getString("priority");
					String requirementOwner = rs.getString("owner");
					String requirementLockedBy = rs.getString("locked_by");
					int requirementPctComplete = rs.getInt("pct_complete");
					String requirementExternalUrl = rs.getString("external_url");
					String traceTo = rs.getString("trace_to");
					String traceFrom = rs.getString("trace_from");
					String userDefinedAttributes = rs
							.getString("user_defined_attributes");
					String testingStatus = rs.getString("testing_status");
					int deleted = rs.getInt("deleted");
					String folderPath = rs.getString("folder_path");
					String createdBy = rs.getString("created_by");
					String createdDt = rs.getString("created_dt");
					String lastModifiedBy = rs.getString("last_modified_by");
					// lastModifiedDt = rs.getDate("last_modified_by");
					String requirementTypeName = rs
							.getString("requirement_type_name");
					String traceDescription = rs.getString("traceDescription");
					int traceId = rs.getInt("traceId");
										
					Requirement requirement1 = new Requirement(requirementId,
							requirementTypeId, folderId, projectId,
							requirementName, requirementDescription,
							requirementTag, requirementFullTag, version,
							approvedByAllDt, approvers, requirementStatus,
							requirementPriority, requirementOwner, requirementLockedBy,
							requirementPctComplete, requirementExternalUrl,
							traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
							createdBy, lastModifiedBy, requirementTypeName, createdDt);
	
					// if the user does not have read permissions on this requirement,
					// lets redact it. i.e. remove all sensitive infor from it.
					if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
							+ requirement1.getFolderId()))){ 
						requirement1.redact();
					}
					String traceFromFullTag = requirement1.getRequirementFullTag();
					String traceToFulllTag = seedRequirement.getRequirementFullTag();
					
					TraceTreeRow traceTreeRow = new TraceTreeRow(1,rs.getInt("suspect") ,traceId, traceDescription, requirement1);
					cIARequirements.add(traceTreeRow);
					// max rows to return
					if (counter++ > maxResults){
						con.close();
						return (cIARequirements);
					}
					
					if (cIADepth > 1) {
						PreparedStatement prepStmt2 = con.prepareStatement(traceDownSQL);
						prepStmt2.setInt(1, requirement1.getRequirementId());
						ResultSet rs2 = prepStmt2.executeQuery();
			
						// lets execute the filtered SQL to get the root level requirements.
						// we will build a trace tree for each one of these requirements.
						while (rs2.next()) {
							requirementId = rs2.getInt("id");
							folderId = rs2.getInt("folder_id");
							requirementTypeId = rs2.getInt("requirement_type_id");
							projectId = rs2.getInt("project_id");
							requirementName = rs2.getString("name");
							requirementDescription = rs2.getString("description");
							requirementTag = rs2.getString("tag");
							requirementFullTag = rs2.getString("full_tag");
							version = rs2.getInt("version");
							approvedByAllDt = rs2.getString("approved_by_all_dt");
							approvers = rs2.getString("approvers");
							requirementStatus = rs2.getString("status");
							requirementPriority = rs2.getString("priority");
							requirementOwner = rs2.getString("owner");
							requirementLockedBy = rs2.getString("locked_by");
							requirementPctComplete = rs2.getInt("pct_complete");
							requirementExternalUrl = rs2.getString("external_url");
							traceTo = rs2.getString("trace_to");
							traceFrom = rs2.getString("trace_from");
							userDefinedAttributes = rs2.getString("user_defined_attributes");
							testingStatus = rs2.getString("testing_status");
							deleted = rs2.getInt("deleted");
							folderPath = rs2.getString("folder_path");
							createdBy = rs2.getString("created_by");
							createdDt = rs2.getString("created_dt");
							lastModifiedBy = rs2.getString("last_modified_by");
							// lastModifiedDt = rs2.getDate("last_modified_by");
							requirementTypeName = rs2.getString("requirement_type_name");
							traceDescription = rs2.getString("traceDescription");
							traceId = rs2.getInt("traceId");
							
							Requirement requirement2 = new Requirement(requirementId,
									requirementTypeId, folderId, projectId,
									requirementName, requirementDescription,
									requirementTag, requirementFullTag, version,
									approvedByAllDt, approvers, requirementStatus,
									requirementPriority, requirementOwner, requirementLockedBy,
									requirementPctComplete, requirementExternalUrl,
									traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
									createdBy, lastModifiedBy, requirementTypeName, createdDt);
			
							// if the user does not have read permissions on this requirement,
							// lets redact it. i.e. remove all sensitive infor from it.
							if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
									+ requirement2.getFolderId()))){
								requirement2.redact();
							}
					
							traceFromFullTag = requirement2.getRequirementFullTag();
							traceToFulllTag = requirement1.getRequirementFullTag();
							
							TraceTreeRow traceTreeRow2 = new TraceTreeRow(2, rs2.getInt("suspect"), traceId, traceDescription, requirement2 );
							cIARequirements.add(traceTreeRow2);
							if (counter++ > maxResults){
								con.close();
								return (cIARequirements);
							}

							if (cIADepth > 2) {
								PreparedStatement prepStmt3 = con.prepareStatement(traceDownSQL);
								prepStmt3.setInt(1, requirement2.getRequirementId());
								ResultSet rs3 = prepStmt3.executeQuery();
					
								// lets execute the filtered SQL to get the root level requirements.
								// we will build a trace tree for each one of these requirements.
								while (rs3.next()) {
									requirementId = rs3.getInt("id");
									folderId = rs3.getInt("folder_id");
									requirementTypeId = rs3.getInt("requirement_type_id");
									projectId = rs3.getInt("project_id");
									requirementName = rs3.getString("name");
									requirementDescription = rs3.getString("description");
									requirementTag = rs3.getString("tag");
									requirementFullTag = rs3.getString("full_tag");
									version = rs3.getInt("version");
									approvedByAllDt = rs3.getString("approved_by_all_dt");
									approvers = rs3.getString("approvers");
									requirementStatus = rs3.getString("status");
									requirementPriority = rs3.getString("priority");
									requirementOwner = rs3.getString("owner");
									requirementLockedBy = rs3.getString("locked_by");
									requirementPctComplete = rs3.getInt("pct_complete");
									requirementExternalUrl = rs3.getString("external_url");
									traceTo = rs3.getString("trace_to");
									traceFrom = rs3.getString("trace_from");
									userDefinedAttributes = rs3.getString("user_defined_attributes");
									testingStatus = rs3.getString("testing_status");
									deleted = rs3.getInt("deleted");
									folderPath = rs3.getString("folder_path");
									createdBy = rs3.getString("created_by");
									createdDt = rs3.getString("created_dt");
									lastModifiedBy = rs3.getString("last_modified_by");
									// lastModifiedDt = rs3.getDate("last_modified_by");
									requirementTypeName = rs3.getString("requirement_type_name");
									traceDescription = rs3.getString("traceDescription");
									traceId = rs3.getInt("traceId");
																		
									Requirement requirement3 = new Requirement(requirementId,
											requirementTypeId, folderId, projectId,
											requirementName, requirementDescription,
											requirementTag, requirementFullTag, version,
											approvedByAllDt, approvers, requirementStatus,
											requirementPriority, requirementOwner, requirementLockedBy,
											requirementPctComplete, requirementExternalUrl,
											traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
											createdBy, lastModifiedBy, requirementTypeName, createdDt);
					
									// if the user does not have read permissions on this requirement,
									// lets redact it. i.e. remove all sensitive infor from it.
									if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
											+ requirement3.getFolderId()))){
										requirement3.redact();
									}
									
									traceFromFullTag = requirement3.getRequirementFullTag();
									traceToFulllTag = requirement2.getRequirementFullTag();
									
									
									TraceTreeRow traceTreeRow3 = new TraceTreeRow(3, rs3.getInt("suspect"), traceId, traceDescription, requirement3);
																		cIARequirements.add(traceTreeRow3);
									if (counter++ > maxResults){
										con.close();
										return (cIARequirements);
									}
									if (cIADepth > 3) {
											PreparedStatement prepStmt4 = con.prepareStatement(traceDownSQL);
											prepStmt4.setInt(1, requirement3.getRequirementId());
											ResultSet rs4 = prepStmt4.executeQuery();
								
											// lets execute the filtered SQL to get the root level requirements.
											// we will build a trace tree for each one of these requirements.
											while (rs4.next()) {
												requirementId = rs4.getInt("id");
												folderId = rs4.getInt("folder_id");
												requirementTypeId = rs4.getInt("requirement_type_id");
												projectId = rs4.getInt("project_id");
												requirementName = rs4.getString("name");
												requirementDescription = rs4.getString("description");
												requirementTag = rs4.getString("tag");
												requirementFullTag = rs4.getString("full_tag");
												version = rs4.getInt("version");
												approvedByAllDt = rs4.getString("approved_by_all_dt");
												approvers = rs4.getString("approvers");
												requirementStatus = rs4.getString("status");
												requirementPriority = rs4.getString("priority");
												requirementOwner = rs4.getString("owner");
												requirementLockedBy = rs4.getString("locked_by");
												requirementPctComplete = rs4.getInt("pct_complete");
												requirementExternalUrl = rs4.getString("external_url");
												traceTo = rs4.getString("trace_to");
												traceFrom = rs4.getString("trace_from");
												userDefinedAttributes = rs4.getString("user_defined_attributes");
												testingStatus = rs4.getString("testing_status");
												deleted = rs4.getInt("deleted");
												folderPath = rs4.getString("folder_path");
												createdBy = rs4.getString("created_by");
												createdDt = rs4.getString("created_dt");
												lastModifiedBy = rs4.getString("last_modified_by");
												// lastModifiedDt = rs3.getDate("last_modified_by");
												requirementTypeName = rs4.getString("requirement_type_name");
												traceDescription = rs4.getString("traceDescription");
												traceId = rs4.getInt("traceId");
																					
												Requirement requirement4 = new Requirement(requirementId,
														requirementTypeId, folderId, projectId,
														requirementName, requirementDescription,
														requirementTag, requirementFullTag, version,
														approvedByAllDt, approvers, requirementStatus,
														requirementPriority, requirementOwner, requirementLockedBy,
														requirementPctComplete, requirementExternalUrl,
														traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
														createdBy, lastModifiedBy, requirementTypeName, createdDt);
								
												// if the user does not have read permissions on this requirement,
												// lets redact it. i.e. remove all sensitive infor from it.
												if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
														+ requirement4.getFolderId()))){
													requirement4.redact();
												}
												traceFromFullTag = requirement4.getRequirementFullTag();
												traceToFulllTag = requirement3.getRequirementFullTag();
												
												TraceTreeRow traceTreeRow4 = new TraceTreeRow(4, rs4.getInt("suspect"), traceId, traceDescription, requirement4);
												
												cIARequirements.add(traceTreeRow4);
												if (counter++ > maxResults){
													con.close();
													return (cIARequirements);
												}
												if (cIADepth > 4) {
													PreparedStatement prepStmt5 = con.prepareStatement(traceDownSQL);
													prepStmt5.setInt(1, requirement4.getRequirementId());
													ResultSet rs5 = prepStmt5.executeQuery();
										
													// lets execute the filtered SQL to get the root level requirements.
													// we will build a trace tree for each one of these requirements.
													while (rs5.next()) {
														requirementId = rs5.getInt("id");
														folderId = rs5.getInt("folder_id");
														requirementTypeId = rs5.getInt("requirement_type_id");
														projectId = rs5.getInt("project_id");
														requirementName = rs5.getString("name");
														requirementDescription = rs5.getString("description");
														requirementTag = rs5.getString("tag");
														requirementFullTag = rs5.getString("full_tag");
														version = rs5.getInt("version");
														approvedByAllDt = rs5.getString("approved_by_all_dt");
														approvers = rs5.getString("approvers");
														requirementStatus = rs5.getString("status");
														requirementPriority = rs5.getString("priority");
														requirementOwner = rs5.getString("owner");
														requirementLockedBy = rs5.getString("locked_by");
														requirementPctComplete = rs5.getInt("pct_complete");
														requirementExternalUrl = rs5.getString("external_url");
														traceTo = rs5.getString("trace_to");
														traceFrom = rs5.getString("trace_from");
														userDefinedAttributes = rs5.getString("user_defined_attributes");
														testingStatus = rs5.getString("testing_status");
														deleted = rs5.getInt("deleted");
														folderPath = rs5.getString("folder_path");
														createdBy = rs5.getString("created_by");
														createdDt = rs5.getString("created_dt");
														lastModifiedBy = rs5.getString("last_modified_by");
														// lastModifiedDt = rs3.getDate("last_modified_by");
														requirementTypeName = rs5.getString("requirement_type_name");
														traceDescription = rs5.getString("traceDescription");
														traceId = rs5.getInt("traceId");
																							
														Requirement requirement5 = new Requirement(requirementId,
																requirementTypeId, folderId, projectId,
																requirementName, requirementDescription,
																requirementTag, requirementFullTag, version,
																approvedByAllDt, approvers, requirementStatus,
																requirementPriority, requirementOwner, requirementLockedBy,
																requirementPctComplete, requirementExternalUrl,
																traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
																createdBy, lastModifiedBy, requirementTypeName, createdDt);
										
														// if the user does not have read permissions on this requirement,
														// lets redact it. i.e. remove all sensitive infor from it.
														if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
																+ requirement5.getFolderId()))){
															requirement5.redact();
														}
														
														traceFromFullTag = requirement5.getRequirementFullTag();
														traceToFulllTag = requirement4.getRequirementFullTag();
														
														TraceTreeRow traceTreeRow5 = new TraceTreeRow(5, rs5.getInt("suspect"), traceId, traceDescription, requirement5);
														
														cIARequirements.add(traceTreeRow5);
														if (counter++ > maxResults){
															con.close();
															return (cIARequirements);
														}
														if (cIADepth > 5) {
															PreparedStatement prepStmt6 = con.prepareStatement(traceDownSQL);
															prepStmt6.setInt(1, requirement5.getRequirementId());
															ResultSet rs6 = prepStmt6.executeQuery();
												
															// lets execute the filtered SQL to get the root level requirements.
															// we will build a trace tree for each one of these requirements.
															while (rs6.next()) {
																requirementId = rs6.getInt("id");
																folderId = rs6.getInt("folder_id");
																requirementTypeId = rs6.getInt("requirement_type_id");
																projectId = rs6.getInt("project_id");
																requirementName = rs6.getString("name");
																requirementDescription = rs6.getString("description");
																requirementTag = rs6.getString("tag");
																requirementFullTag = rs6.getString("full_tag");
																version = rs6.getInt("version");
																approvedByAllDt = rs6.getString("approved_by_all_dt");
																approvers = rs6.getString("approvers");
																requirementStatus = rs6.getString("status");
																requirementPriority = rs6.getString("priority");
																requirementOwner = rs6.getString("owner");
																requirementLockedBy = rs6.getString("locked_by");
																requirementPctComplete = rs6.getInt("pct_complete");
																requirementExternalUrl = rs6.getString("external_url");
																traceTo = rs6.getString("trace_to");
																traceFrom = rs6.getString("trace_from");
																userDefinedAttributes = rs6.getString("user_defined_attributes");
																testingStatus = rs6.getString("testing_status");
																deleted = rs6.getInt("deleted");
																folderPath = rs6.getString("folder_path");
																createdBy = rs6.getString("created_by");
																createdDt = rs6.getString("created_dt");
																lastModifiedBy = rs6.getString("last_modified_by");
																// lastModifiedDt = rs3.getDate("last_modified_by");
																requirementTypeName = rs6.getString("requirement_type_name");
																traceDescription = rs6.getString("traceDescription");
																traceId = rs6.getInt("traceId");
																									
																Requirement requirement6 = new Requirement(requirementId,
																		requirementTypeId, folderId, projectId,
																		requirementName, requirementDescription,
																		requirementTag, requirementFullTag, version,
																		approvedByAllDt, approvers, requirementStatus,
																		requirementPriority, requirementOwner, requirementLockedBy,
																		requirementPctComplete, requirementExternalUrl,
																		traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
																		createdBy, lastModifiedBy, requirementTypeName, createdDt);
												
																// if the user does not have read permissions on this requirement,
																// lets redact it. i.e. remove all sensitive infor from it.
																if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
																		+ requirement6.getFolderId()))){
																	requirement6.redact();
																}
													
																traceFromFullTag = requirement6.getRequirementFullTag();
																traceToFulllTag = requirement5.getRequirementFullTag();
																
																TraceTreeRow traceTreeRow6 = new TraceTreeRow(6, rs6.getInt("suspect"), traceId, traceDescription, requirement6);
																
																cIARequirements.add(traceTreeRow6);
																if (counter++ > maxResults){
																	con.close();
																	return (cIARequirements);
																}
																if (cIADepth > 6) {
																	PreparedStatement prepStmt7 = con.prepareStatement(traceDownSQL);
																	prepStmt7.setInt(1, requirement6.getRequirementId());
																	ResultSet rs7 = prepStmt7.executeQuery();
														
																	// lets execute the filtered SQL to get the root level requirements.
																	// we will build a trace tree for each one of these requirements.
																	while (rs7.next()) {
																		requirementId = rs7.getInt("id");
																		folderId = rs7.getInt("folder_id");
																		requirementTypeId = rs7.getInt("requirement_type_id");
																		projectId = rs7.getInt("project_id");
																		requirementName = rs7.getString("name");
																		requirementDescription = rs7.getString("description");
																		requirementTag = rs7.getString("tag");
																		requirementFullTag = rs7.getString("full_tag");
																		version = rs7.getInt("version");
																		approvedByAllDt = rs7.getString("approved_by_all_dt");
																		approvers = rs7.getString("approvers");
																		requirementStatus = rs7.getString("status");
																		requirementPriority = rs7.getString("priority");
																		requirementOwner = rs7.getString("owner");
																		requirementLockedBy = rs7.getString("locked_by");
																		requirementPctComplete = rs7.getInt("pct_complete");
																		requirementExternalUrl = rs7.getString("external_url");
																		traceTo = rs7.getString("trace_to");
																		traceFrom = rs7.getString("trace_from");
																		userDefinedAttributes = rs7.getString("user_defined_attributes");
																		testingStatus = rs7.getString("testing_status");
																		deleted = rs7.getInt("deleted");
																		folderPath = rs7.getString("folder_path");
																		createdBy = rs7.getString("created_by");
																		createdDt = rs7.getString("created_dt");
																		lastModifiedBy = rs7.getString("last_modified_by");
																		// lastModifiedDt = rs3.getDate("last_modified_by");
																		requirementTypeName = rs7.getString("requirement_type_name");
																		traceDescription = rs7.getString("traceDescription");
																		traceId = rs7.getInt("traceId");
																											
																		Requirement requirement7 = new Requirement(requirementId,
																				requirementTypeId, folderId, projectId,
																				requirementName, requirementDescription,
																				requirementTag, requirementFullTag, version,
																				approvedByAllDt, approvers, requirementStatus,
																				requirementPriority, requirementOwner, requirementLockedBy,
																				requirementPctComplete, requirementExternalUrl,
																				traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
																				createdBy, lastModifiedBy, requirementTypeName, createdDt);
														
																		// if the user does not have read permissions on this requirement,
																		// lets redact it. i.e. remove all sensitive infor from it.
																		if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
																				+ requirement7.getFolderId()))){
																			requirement7.redact();
																		}
																
																		traceFromFullTag = requirement7.getRequirementFullTag();
																		traceToFulllTag = requirement6.getRequirementFullTag();
																		
																		TraceTreeRow traceTreeRow7 = new TraceTreeRow(7, rs7.getInt("suspect"), traceId, traceDescription, requirement7);
																		
																		cIARequirements.add(traceTreeRow7);
																		if (counter++ > maxResults){
																			con.close();
																			return (cIARequirements);
																		}
																		if (cIADepth > 7) {
																			PreparedStatement prepStmt8 = con.prepareStatement(traceDownSQL);
																			prepStmt8.setInt(1, requirement7.getRequirementId());
																			ResultSet rs8 = prepStmt8.executeQuery();
																
																			// lets execute the filtered SQL to get the root level requirements.
																			// we will build a trace tree for each one of these requirements.
																			while (rs8.next()) {
																				requirementId = rs8.getInt("id");
																				folderId = rs8.getInt("folder_id");
																				requirementTypeId = rs8.getInt("requirement_type_id");
																				projectId = rs8.getInt("project_id");
																				requirementName = rs8.getString("name");
																				requirementDescription = rs8.getString("description");
																				requirementTag = rs8.getString("tag");
																				requirementFullTag = rs8.getString("full_tag");
																				version = rs8.getInt("version");
																				approvedByAllDt = rs8.getString("approved_by_all_dt");
																				approvers = rs8.getString("approvers");
																				requirementStatus = rs8.getString("status");
																				requirementPriority = rs8.getString("priority");
																				requirementOwner = rs8.getString("owner");
																				requirementLockedBy = rs8.getString("locked_by");
																				requirementPctComplete = rs8.getInt("pct_complete");
																				requirementExternalUrl = rs8.getString("external_url");
																				traceTo = rs8.getString("trace_to");
																				traceFrom = rs8.getString("trace_from");
																				userDefinedAttributes = rs8.getString("user_defined_attributes");
																				testingStatus = rs8.getString("testing_status");
																				deleted = rs8.getInt("deleted");
																				folderPath = rs8.getString("folder_path");
																				createdBy = rs8.getString("created_by");
																				createdDt = rs8.getString("created_dt");
																				lastModifiedBy = rs8.getString("last_modified_by");
																				// lastModifiedDt = rs3.getDate("last_modified_by");
																				requirementTypeName = rs8.getString("requirement_type_name");
																				traceDescription = rs8.getString("traceDescription");
																				traceId = rs8.getInt("traceId");
																													
																				Requirement requirement8 = new Requirement(requirementId,
																						requirementTypeId, folderId, projectId,
																						requirementName, requirementDescription,
																						requirementTag, requirementFullTag, version,
																						approvedByAllDt, approvers, requirementStatus,
																						requirementPriority, requirementOwner, requirementLockedBy,
																						requirementPctComplete, requirementExternalUrl,
																						traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
																						createdBy, lastModifiedBy, requirementTypeName, createdDt);
																
																				// if the user does not have read permissions on this requirement,
																				// lets redact it. i.e. remove all sensitive infor from it.
																				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
																						+ requirement8.getFolderId()))){
																					requirement8.redact();
																				}
																		
																				traceFromFullTag = requirement8.getRequirementFullTag();
																				traceToFulllTag = requirement7.getRequirementFullTag();
																				
																				TraceTreeRow traceTreeRow8 = new TraceTreeRow(8, rs8.getInt("suspect"), traceId, traceDescription, requirement8);
																				
																				cIARequirements.add(traceTreeRow8);
																				if (counter++ > maxResults){
																					con.close();
																					return (cIARequirements);
																				}
																			}
																			rs8.close();
																			prepStmt8.close();
																		}									

																		
																	}
																	rs7.close();
																	prepStmt7.close();
																}									
																
															}
															rs6.close();
															prepStmt6.close();
														}									

													}
													rs5.close();
													prepStmt5.close();
												}									

											}
											rs4.close();
											prepStmt4.close();
									}									
								}
								rs3.close();
								prepStmt3.close();
							}

						}
						rs2.close();
						prepStmt2.close();
					}
				}
				rs.close();
				prepStmt.close();
			}
		
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
		return (cIARequirements);
	}
	
	


	
	// this method bumps up the Requirement version and notifies all users that the version has changed.
	
	public static void updateVersion( int requirementId, HttpServletRequest request, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();		
			
			String sql = " update gr_requirements set version = version + 1 " +
				" where id = ? ";
		
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.execute();
			
			prepStmt.close();
			
			// now lets notify all the stake holders that the requirement has changed.
			RequirementUtil.notifyRequirementStakeHolders(requirementId, "newVersion",request,  databaseType, "", "");
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
		
	}	

	// this method 
	// set all traces to / from this req to suspect
	// updates the Requirement's TraceTo, TraceFrom fields to reflect to updates to traces to and trace from
	// for all the requirements this req traces to / traces from, it updates the traceTo, Trace from fields
	// to reflect the reality.
	
	public static void updateTraceability(String traceDefinition, int requirementId, 
		HttpServletRequest request, String actorEmailId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			Calendar cal = Calendar.getInstance();
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();		
			

			// Set all traces to / from this req to suspect. 
			String sql = "update gr_traces " +
				" set description = ? , suspect=1 " +
				" where (from_requirement_id = ? or to_requirement_id = ?)" ;
	
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, "Trace set suspect by " + actorEmailId + " at " + cal.getTime() +
				". Change is  " + traceDefinition);
			prepStmt.setInt(2, requirementId);
			prepStmt.setInt(3, requirementId);
			prepStmt.execute();
			prepStmt.close();
			
			
			// NOTE : this is a little tricky
			// we need to set the 'traceTo and TraceFrom value for this req , 
			// since the change in Req is triggering a bunch of suspect traces.
			RequirementUtil.updateTraceInfoForRequirement(requirementId);
				
			// however the above action has also triggered suspect traces in reqs
			// that trace to and trace from this req. So we need to get a list 
			// of those reqs and call the same updateTraceInfo method on that.
			sql = "select id from gr_traces " +
				" where (from_requirement_id = ? or to_requirement_id = ?)" ;
	
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.setInt(2, requirementId);
			rs = prepStmt.executeQuery();
				
			while (rs.next()){
				int traceId = rs.getInt("id");
				RequirementUtil.updateTraceInfoForTrace(traceId, actorEmailId,  databaseType);						
			}

			rs.close();
			prepStmt.close();
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
		
	}	
	
	
	
	// this method resets the status to draft, cancels pendig approvals, sets approved by all date to null.
	
	public static void updateApprovalWorkflow( int requirementId, HttpServletRequest request){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();		

			// Status should be reset to
			// Draft.
			// also the approvers will eb set to empty . When this req is submitted for approval ,it will be
			// refilled with all the valid approvers.
			String sql = "update gr_requirements" +
			" set status = ?  ," +
			" approvers = null ," +
			" approved_by_all_dt = null " +
			" where id = ? "  ;

			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, "Draft");
			prepStmt.setInt(2, requirementId);
			prepStmt.execute();
	
			prepStmt.close();

			// at this point cancel any pending requirement approval for previous versions.
			// this is because, we are resetting the approval work flow
			// any pending approvals do not matter.
			RequirementUtil.cancelPendingRequirementApprovals(requirementId);
			
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
		
	}	
	
	public static void cloneRequirementComment( int sourceRequirementId, int targetRequirementId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();		

			// Status should be reset to
			// Draft.
			// also the approvers will eb set to empty . When this req is submitted for approval ,it will be
			// refilled with all the valid approvers.
			String sql = "insert into gr_requirement_comments (requirement_id, version, commenter_email_id, comment_note, comment_dt) " +
					" select " + targetRequirementId + " , version, commenter_email_id,comment_note, comment_dt "
					+ " from gr_requirement_comments where requirement_id =  ? "  ;

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceRequirementId);
			prepStmt.execute();
	
			prepStmt.close();

			
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
		
	}	
	
	
	public static void cloneRequirementAttachments( int sourceRequirementId, int targetRequirementId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();		

			// Status should be reset to
			// Draft.
			// also the approvers will eb set to empty . When this req is submitted for approval ,it will be
			// refilled with all the valid approvers.
			String sql = "insert into gr_requirement_attachments (requirement_id, file_name, file_path, title, created_by, created_dt ) " +
					" select " + targetRequirementId + " , file_name, file_path, title, created_by, created_dt "
					+ " from gr_requirement_attachments where requirement_id =  ? "  ;

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceRequirementId);
			prepStmt.execute();
	
			prepStmt.close();

			
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
		
	}	
	
		
	public static void cloneRequirementVersions( int sourceRequirementId, int sourceRequirementVersion, int targetRequirementId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();		


			// lets delete the version1 of the target, as we will be overwriting them fresh here.
			
			String sql = "delete from gr_requirement_versions where requirement_id = ? "  ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, targetRequirementId);
			prepStmt.execute();
			prepStmt.close();
			
			sql = "insert into gr_requirement_versions (requirement_id, version, name, description, approvers, status, priority, owner, pct_complete, external_url, trace_to, trace_from, user_defined_attributes, created_by, created_dt) " +
					" select " + targetRequirementId + " , version, name, description, approvers, status, priority, owner, pct_complete, external_url, trace_to, trace_from, user_defined_attributes, created_by, created_dt "
					+ " from gr_requirement_versions where requirement_id =  ? "  ;

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceRequirementId);
			prepStmt.execute();
	
			prepStmt.close();

			// lets set the target's version to be the same as the source requireemnt's version
			sql = "update gr_requirements set version = ? where id = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceRequirementVersion);
			prepStmt.setInt(2, targetRequirementId);
			prepStmt.execute();
	
			prepStmt.close();
			
			
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
		
	}	
	
		
	
	// this method clones the requirement to a target requirement type.
	public static Requirement cloneRequirement( Requirement sourceRequirement , 
		RequirementType targetRequirementType, int targetProjectId, int targetFolderId, boolean cloneAttributes, boolean cloneTraces,
		User user, SecurityProfile securityProfile, String databaseType){
		Requirement targetRequirement = null;
		try {

			RequirementType sourceRequirementType = new RequirementType(sourceRequirement.getRequirementTypeId());		
			targetRequirement = new Requirement("",targetRequirementType.getRequirementTypeId(),
				targetFolderId, 
				targetProjectId, 
				sourceRequirement.getRequirementName(),
				sourceRequirement.getRequirementDescription(), 
				sourceRequirement.getRequirementPriority(), 
				sourceRequirement.getRequirementOwner(), sourceRequirement.getRequirementLockedBy(),
				sourceRequirement.getRequirementPctComplete(),
				sourceRequirement.getRequirementExternalUrl(), user.getEmailId(),  databaseType);

			
			
			// Copy the comments from the source requirement to the target requirement.
			cloneRequirementComment(sourceRequirement.getRequirementId(), targetRequirement.getRequirementId());
			cloneRequirementAttachments(sourceRequirement.getRequirementId(), targetRequirement.getRequirementId());
			cloneRequirementVersions(sourceRequirement.getRequirementId(), sourceRequirement.getVersion(), targetRequirement.getRequirementId());
			
			// now that we have created the core requirement, lets see if there are any common custom attributes
			// if they exist then we clone them too.
			// lets loop through all the custom attributes
			if (cloneAttributes){
				ArrayList sourceAttributes = sourceRequirementType.getAllAttributesInRequirementType();
				ArrayList targetAttributes = targetRequirementType.getAllAttributesInRequirementType();
				Iterator s = sourceAttributes.iterator();
				while (s.hasNext()){
					RTAttribute sourceAttribute = (RTAttribute) s.next();
					
					Iterator t = targetAttributes.iterator();
					while (t.hasNext()){
						RTAttribute targetAttribute = (RTAttribute) t.next();
						if (
								(sourceAttribute.getAttributeName().trim().toLowerCase().equals(
								targetAttribute.getAttributeName().trim().toLowerCase()))
								&&
								(sourceAttribute.getAttributeType().equals(targetAttribute.getAttributeType()))
							){
							
							
							// at this point both the source requirement and target requirement have 
							// an attribute of the same name. 
							// Now lets get this value and see if its an acceptable value in the target requirement
							String sourceAttributeValue = sourceRequirement.getAttributeValue(sourceAttribute.getAttributeId());
							// see if the sourceAttributeValue is peritted in the target req type.
							boolean permitted = ProjectUtil.isPermittedValueInAttribute(targetAttribute.getAttributeId(), sourceAttributeValue);
							if (permitted){
								// we are good to go. this is a permitted value. Lets copy it over
								// since there could be spaces at the end or beginning of the pull down value, its best that
								// we get the target requirements attribute drop down value.
								targetRequirement.setCustomAttributeValue(targetAttribute.getAttributeId(),
								sourceAttributeValue, user,  databaseType);
							}
						}
					}
				}
			}
			if (cloneTraces){
				Project targetRequirementProject = new Project(targetProjectId,  databaseType);
				// the user has asked us to clone traces from source requirement to target requirement.
				
				// lets get traceTo for the source req and do the same for target req.
				ArrayList traceTos = sourceRequirement.getRequirementTraceToObjects();
				Iterator tT = traceTos.iterator();
				while (tT.hasNext()){
					Trace trace = (Trace) tT.next();
					Requirement traceToRequirement = new Requirement(trace.getToRequirementId(),  databaseType);
					String traceToString = "";
					if (traceToRequirement.getProjectId() != targetProjectId){
						// this trace is going to an external project. so we need to get the tracestring
						// in the prefix:reqfulltag format.
						traceToString = traceToRequirement.getProjectShortName() + ":" + traceToRequirement.getRequirementFullTag();
					}
					else {
						traceToString = traceToRequirement.getRequirementFullTag();
					}
					RequirementUtil.createTraces(targetRequirementProject, targetRequirement.getRequirementId(), 
							traceToString, "", targetProjectId, securityProfile,  databaseType);
				}
				
				// lets get TraceFroms from the target req and set trace from that req to this.
				ArrayList traceFroms = sourceRequirement.getRequirementTraceFromObjects();
				Iterator tF = traceFroms.iterator();
				while (tF.hasNext()){
					Trace trace = (Trace) tF.next();
					Requirement traceFromRequirement = new Requirement(trace.getFromRequirementId(),  databaseType);
					String traceFromString = "";
					if (traceFromRequirement.getProjectId() != targetProjectId){
						// this trace is coming from  an external project. so we need to get the tracestring
						// in the prefix:reqfulltag format.
						traceFromString = traceFromRequirement.getProjectShortName() + ":" + traceFromRequirement.getRequirementFullTag();
					}
					else {
						traceFromString = traceFromRequirement.getRequirementFullTag();
					}
					RequirementUtil.createTraces(targetRequirementProject, targetRequirement.getRequirementId(), 
							"", traceFromString ,targetProjectId, securityProfile,  databaseType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return targetRequirement;
	}		
	

	
	// this method makes  a child requirement independent. the children's Future can be 'take along'
	// or leave with grand parent.
	public static Requirement makeRequirementIndependent( Requirement requirement , 
		String childrensFuture, String actorEmailId, String databaseType){
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();		
			// lets get the next tag for this requirement.
			// at this point, we were either unable to get the next children tag
			// or the parent tag wasn't given.
			String sql = "update gr_requirements_seq set tag = tag + 1 where requirement_type_id = ?  ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirement.getRequirementTypeId());
			prepStmt.execute();
			prepStmt.close();
			
			
			// now that we have updated the next tag value for this req type, lets retrieve it to create the req.
			sql = "select tag from gr_requirements_seq where requirement_type_id = ?";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,requirement.getRequirementTypeId());
			rs = prepStmt.executeQuery();
			String nextTag = "";
			if (rs.next()){
				nextTag =  rs.getString("tag");
			}


			RequirementType requirementType = new RequirementType(requirement.getRequirementTypeId());

			
			// lets deal with the children now.
			if (childrensFuture.equals("takeChildrenAlong")){
				String oldFullTag = requirement.getRequirementFullTag();
				String newFullTag = requirementType.getRequirementTypeShortName() + "-" + nextTag;
				
				String oldTag = requirement.getRequirementTag();
				String newTag = nextTag;
				
				// for all the requirements that have been impacted, lets iterate
				// through them and add the requirement log and re-set the cacheed 
				// trace values of the traceTo traceFrom targets.
				
				// NOTE : Critical : since the tag has changed for all these reqs
				// lets call setTagLevel routine, as tag levels determine the sorting.

				// Note : CRITICAL : remember to put the . to get all the children of any req.
				sql = "select id, full_tag from gr_requirements " +
				" where tag like  '" + oldTag + ".%'" +
				" and requirement_type_id = ? ";
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirement.getRequirementTypeId());
				rs = prepStmt.executeQuery();

				while (rs.next()){
					int childRequirementId = rs.getInt("id");
					String fullTag = rs.getString("full_tag");

					// lets simply replace the oldFullTag with new Full Tag in all full tags and parent tags of all
					// children in the hierarchy
					String sql2 = "update gr_requirements " +
						" set  full_tag = upper(replace(full_tag,?,?)) , " +
						" parent_full_tag = upper(replace(parent_full_tag ,?,?))  " +
						" where id = ? ";
					PreparedStatement prepStmt2 = con.prepareStatement(sql2);
					prepStmt2.setString(1, oldFullTag);
					prepStmt2.setString(2, newFullTag);
					prepStmt2.setString(3, oldFullTag);
					prepStmt2.setString(4, newFullTag);
					prepStmt2.setInt(5, childRequirementId);
					prepStmt2.execute();

					// now for all requirements in this req type, which were children of this requirement
					// lets set the re-set the tags too.
					sql2 = "update gr_requirements" +
						" set tag = replace(tag,?,?) " +
						" where id = ? ";
					
					prepStmt2 = con.prepareStatement(sql2);
					prepStmt2.setString(1, oldTag);
					prepStmt2.setString(2, newTag);
					prepStmt2.setInt(3, childRequirementId);
					prepStmt2.execute();

					
					
					RequirementUtil.setTagLevels(childRequirementId);
	
					RequirementUtil.createRequirementLog(childRequirementId,"Requirement Tag changed to " + 
					fullTag + " due to reparenting of " + requirement.getRequirementFullTag() ,
					actorEmailId,  databaseType);


					// Since the req tags have changed, lets find all the trace to / from 
					// this req and re-set their trace info
					sql = "select id from gr_traces " +
						" where (from_requirement_id = ? or to_requirement_id = ?)" ;
			
					
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1, childRequirementId);
					prepStmt2.setInt(2, childRequirementId);
					ResultSet rs2 = prepStmt2.executeQuery();
						
					
					while (rs2.next()){
						int traceId = rs2.getInt("id");

						RequirementUtil.updateTraceInfoForTrace(traceId, actorEmailId,  databaseType);						
					}
					rs2.close();
				}

				rs.close();
				prepStmt.close();
			}
			if (childrensFuture.equals("assignToGrandParent")){
				
				// This is tricky.
				// this is how we will try to address it.
				// Get all the children on this req (only children, not further down)
				// for each one of these, lets run the assign to a different parent routine.
				sql = "select id from gr_requirements " +
					" where project_id = ? " +
					" and parent_full_tag = ? ";
				
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirement.getProjectId());
				prepStmt.setString(2, requirement.getRequirementFullTag());
				rs = prepStmt.executeQuery();

				while (rs.next()){
					int childRequirementId = rs.getInt("id");
					// if the parentfulltag is empty or null, that means we need to make this req independent.
					if ((requirement.getParentFullTag() == null) || (requirement.getParentFullTag().equals(""))){
						Requirement childRequirement = new Requirement(childRequirementId,  databaseType);

						RequirementUtil.makeRequirementIndependent(childRequirement, "takeChildrenAlong", actorEmailId,  databaseType);
					}
					else {
						Requirement childRequirement = RequirementUtil.assignToNewParent(childRequirementId,requirement.getParentFullTag(),
								requirement.getParentFullTag(),"takeChildrenAlong", actorEmailId,  databaseType);
					}
				}
				
				rs.close();
				prepStmt.close();
			}		

			// lets update the tag and full tag of this requirement with the next tag info.
			sql = "update gr_requirements set tag = ? , full_tag = ? , parent_full_tag = null where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, nextTag);
			prepStmt.setString(2, requirementType.getRequirementTypeShortName() + "-" + nextTag);
			prepStmt.setInt(3, requirement.getRequirementId());
			prepStmt.execute();
			

			// NOTE : Critical : since the tag has changed for all these reqs
			// lets call setTagLevel routine, as tag levels determine the sorting.
			RequirementUtil.setTagLevels(requirement.getRequirementId());
			

			requirement = new Requirement(requirement.getRequirementId(),  databaseType);
			
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
			
		return requirement;
	}		
	
	
	// this method assigns a child to a new parent. supports children's future as 'take along' 
	public static Requirement assignToNewParent(int childRequirementId, String grandParentFullTag, String newParentFullTag, 
		String childrensFuture, String actorEmailId, String databaseType){
		
		// there are 3 players here. Child is the req we are working on. if asked to move the Grand Children to the Grand parend
		// we move the input child's children to the input child's parents.
		Requirement childRequirement = new Requirement(childRequirementId,  databaseType);
		Requirement parentRequirement = new Requirement(newParentFullTag, childRequirement.getProjectId(),  databaseType);
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();		

			
		
			String nextTag = RequirementUtil.getNextTag(newParentFullTag, childRequirement.getProjectId(),  databaseType);
			
			// lets update the tag and full tag of this requirement with the next tag info.
			RequirementType requirementType = new RequirementType(childRequirement.getRequirementTypeId());
			String sql = "update gr_requirements " +
				" set tag = ? ," +
				" full_tag = ? ," +
				" parent_full_tag = ?" +
				" where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, nextTag);
			prepStmt.setString(2, requirementType.getRequirementTypeShortName() + "-" + nextTag);
			prepStmt.setString(3, newParentFullTag.toUpperCase());
			prepStmt.setInt(4, childRequirement.getRequirementId());
			prepStmt.execute();
			prepStmt.close();
			
			// since the tag of the req has changed, lets set the tag levels.
			RequirementUtil.setTagLevels(childRequirement.getRequirementId());
			
			// lets deal with the children now.
			if (childrensFuture.equals("takeChildrenAlong")){
				String oldFullTag = childRequirement.getRequirementFullTag();
				String newFullTag = requirementType.getRequirementTypeShortName() + "-" + nextTag;
				
				String oldTag = childRequirement.getRequirementTag();
				String newTag = nextTag;

				// for all the requirements that have been impacted, lets iterate
				// through them and add the requirement log and re-set the cacheed 
				// trace values of the traceTo traceFrom targets.
				
				// NOTE : Critical : since the tag has changed for all these reqs
				// lets call setTagLevel routine, as tag levels determine the sorting.

				// Note : CRITICAL : remember to put the . to get all the children of any req.
				sql = "select id, full_tag from gr_requirements " +
				" where tag like  '" + oldTag + ".%'" +
				" and requirement_type_id = ? ";
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, childRequirement.getRequirementTypeId());
				rs = prepStmt.executeQuery();

				while (rs.next()){
					int grandChildRequirementId = rs.getInt("id");
					String grandChildRequirementFullTag = rs.getString("full_tag");

					// lets simply replace the oldFullTag with new Full Tag in all full tags and parent tags of all
					// children in the hierarchy
					String sql2 = "update gr_requirements " +
						" set  full_tag = upper(replace(full_tag,?,?)) , " +
						" parent_full_tag = upper(replace(parent_full_tag ,?,?))  " +
						" where id = ? ";
					PreparedStatement prepStmt2 = con.prepareStatement(sql2);
					prepStmt2.setString(1, oldFullTag);
					prepStmt2.setString(2, newFullTag);
					prepStmt2.setString(3, oldFullTag);
					prepStmt2.setString(4, newFullTag);
					prepStmt2.setInt(5, grandChildRequirementId);
					prepStmt2.execute();

					// now for all requirements in this req type, which were children of this requirement
					// lets set the re-set the tags too.
					sql2 = "update gr_requirements" +
						" set tag = replace(tag,?,?) " +
						" where id = ? ";
					
					prepStmt2 = con.prepareStatement(sql2);
					prepStmt2.setString(1, oldTag);
					prepStmt2.setString(2, newTag);
					prepStmt2.setInt(3, grandChildRequirementId);
					prepStmt2.execute();

					
					
					RequirementUtil.setTagLevels(grandChildRequirementId);
	
					RequirementUtil.createRequirementLog(childRequirementId,"Requirement Tag changed to " + 
						grandChildRequirementFullTag + " due to reparenting of " + childRequirement.getRequirementFullTag() ,
						actorEmailId,  databaseType);


					// Since the req tags have changed, lets find all the trace to / from 
					// this req and re-set their trace info
					sql = "select id from gr_traces " +
						" where (from_requirement_id = ? or to_requirement_id = ?)" ;
			
					
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1, grandChildRequirementId);
					prepStmt2.setInt(2, grandChildRequirementId);
					ResultSet rs2 = prepStmt2.executeQuery();
						
					
					while (rs2.next()){
						int traceId = rs2.getInt("id");

						RequirementUtil.updateTraceInfoForTrace(traceId, actorEmailId,  databaseType);						
					}
					rs2.close();
				}

				rs.close();
				prepStmt.close();

			}
			if (childrensFuture.equals("assignToGrandParent")){
				
				// This is tricky.
				// this is how we will try to address it.
				// Get all the children on this req (only children, not further down)
				// for each one of these, lets run the assign to a different parent routine.
				sql = "select id from gr_requirements " +
					" where project_id = ? " +
					" and parent_full_tag = ? ";
				
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, childRequirement.getProjectId());
				prepStmt.setString(2, childRequirement.getRequirementFullTag());
				rs = prepStmt.executeQuery();
				while (rs.next()){
					int grandChildRequirementId = rs.getInt("id");
					// if the parentfulltag is empty or null, that means we need to make this req independent.
					if ((newParentFullTag == null) || (newParentFullTag.equals(""))){
						Requirement grandChildRequirement = new Requirement(grandChildRequirementId,  databaseType);
						RequirementUtil.makeRequirementIndependent(grandChildRequirement, "takeChildrenAlong",actorEmailId,  databaseType);
					}
					else {
						Requirement grandChildRequirement = RequirementUtil.assignToNewParent(grandChildRequirementId,"",grandParentFullTag,
							"takeChildrenAlong", actorEmailId,  databaseType);
					}
				}
				
				rs.close();
				prepStmt.close();
			}		
			childRequirement = new Requirement(childRequirement.getRequirementId(),  databaseType);

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
			
		return childRequirement;
	}
	
	// this method clones all the attributes of a requirement parent to the Requirement. 
	public static void cloneParentAttributes (Requirement requirement, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		
		String parentFullTag = requirement.getParentFullTag();
		if ((parentFullTag == null) || (parentFullTag.equals(""))){
			// nothing to do here. lets return.
			return;
		}
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();		
			
			Requirement parent = new Requirement(parentFullTag,requirement.getProjectId(),  databaseType);
			
			// lets get all the attribute values for the parent req.
			String sql = "select attribute_id, value " +
				" from gr_r_attribute_values " +
				" where requirement_id = ? ";
			prepStmt  = con.prepareStatement(sql);
			prepStmt.setInt(1, parent.getRequirementId());
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int attributeId = rs.getInt("attribute_id");
				String attributeValue = rs.getString("value");
				
				String sql2 = "update gr_r_attribute_values " +
					" set value = ? " +
					" where requirement_id = ? " +
					" and attribute_id = ? ";
				PreparedStatement prepStmt2 = con.prepareCall(sql2);
				prepStmt2.setString(1, attributeValue);
				prepStmt2.setInt(2, requirement.getRequirementId());
				prepStmt2.setInt(3, attributeId);
				prepStmt2.execute();
				prepStmt2.close();
			}
			rs.close();
			prepStmt.close();

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
	}		


	
	// given a requirement id, it 
	// get the tag, splits it into level1, 2, 3 and 4 and updates these levesl in the db.
	public static void setTagLevels (int requirementId){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();		
			
			
			// lets get all the attribute values for the parent req.
			String sql = "select tag " +
				" from gr_requirements " +
				" where id  = ? ";
			prepStmt  = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();
			String tag = "";
			while (rs.next()){
				tag = rs.getString("tag");
			}
			rs.close();
			prepStmt.close();

			
			// since we have the tag, lets calculate the level's of tagLevel1, tagLevel2, tagLevel3, tagLevel4
			int tagLevel1 = 0;
			int tagLevel2 = 0;
			int tagLevel3 = 0;
			int tagLevel4 = 0;
			
			try{
				if (tag.contains(".")){
					String [] tags = tag.split("\\.");
					if (tags.length > 0){
						tagLevel1 = Integer.parseInt(tags[0]);
					}
					if (tags.length > 1){
						tagLevel2 = Integer.parseInt(tags[1]);
					}
					if (tags.length > 2){
						tagLevel3 = Integer.parseInt(tags[2]);
					}
					if (tags.length > 3){
						tagLevel4 = Integer.parseInt(tags[3]);
					}
				}
				else {
					tagLevel1 = Integer.parseInt(tag);
				}
				
			}
			catch (Exception e){
				e.printStackTrace();
			}
		
			sql = "update gr_requirements " +
				" set tag_level1 = ? , " +
				" tag_level2 = ? , " +
				" tag_level3 = ? , " +
				" tag_level4 = ? " +
				" where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, tagLevel1);
			prepStmt.setInt(2, tagLevel2);
			prepStmt.setInt(3, tagLevel3);
			prepStmt.setInt(4, tagLevel4);
			prepStmt.setInt(5, requirementId);
			prepStmt.execute();
			
			
			
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
	}		


	// when called with a requirementId, it returns an array list of child requirement ids.
	public static ArrayList getImmediateChildrenRequirementIds(int projectId, String parentFullTag) {

		ArrayList childRequirementIds = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select id from gr_requirements " +
				" where project_id = ? and  parent_full_tag = ?  ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, parentFullTag);

			rs = prepStmt.executeQuery();
			while (rs.next()) {
				int childRequirementId = rs.getInt("id");
				childRequirementIds.add(new Integer(childRequirementId));
			}

			rs.close();
			prepStmt.close();
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
		return (childRequirementIds);
	}

	public static boolean hasChildren(int projectId, String parentFullTag) {

		boolean hasChildren = false;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select count(*) 'numOfChildren' from gr_requirements " +
				" where project_id = ? and  parent_full_tag = ?  ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, parentFullTag);

		
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				int numOfChildren = rs.getInt("numOfChildren");
				if (numOfChildren > 0){
					hasChildren = true;
				}
			}

			rs.close();
			prepStmt.close();
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
		
		return (hasChildren);
	}


	// when called with a requirementId, it returns an array list of 
	// all children in the requirement familty
	public static ArrayList getAllChildrenInFamilyRequirementIds(int requirementId) {

		ArrayList childRequirementIds = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// get full tag first.
			String sql = "select requirement_type_id, tag from gr_requirements " +
				" where id = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);

			rs = prepStmt.executeQuery();
			String tag = "";
			int requirementTypeId = 0;
			while (rs.next()) {
				tag = rs.getString("tag");
				requirementTypeId = rs.getInt("requirement_type_id");
			}

			rs.close();
			prepStmt.close();
			
			// now lets get all reqs whose parent fulltag has this fulltag.
			// NOTE : CRITICAL : make sure you add a . to the fulltag
			// this prevents getting confused between BR-1 annd BR-100
			// Note : CRITICAL : remember to put the . to get all the children of any req.

			sql = "select id from gr_requirements " +
				" where requirement_type_id = ?" +
				" and tag like  '" + tag + ".%'";

			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
	
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				int childRequirementId = rs.getInt("id");
				childRequirementIds.add(new Integer(childRequirementId));
			}
	
			rs.close();
			prepStmt.close();
			
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
		return (childRequirementIds);
	}


	
	
	
	// takes a requirement Id as a param and returns an arraylist of Requirements
	public static ArrayList getImmediateChildRequirements(int projectId, String parentFullTag, 
			String databaseType) {
		
		ArrayList childRequirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null; 
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
						" r.name, r.description, r.tag, r.full_tag, " +
						" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\" ," +
						" r.approvers, " + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from , " +
						" r.user_defined_attributes, r.testing_status, r.deleted, f.folder_path, r.created_by," +
						" date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\" , " +
						" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
						" FROM gr_requirements r , gr_requirement_types rt, gr_folders f " +
						" where r.project_id = ? and r.deleted = 0 " +
						" and  r.parent_full_tag = ? " +
						" and r.requirement_type_id = rt.id  " +
						" and r.folder_id = f.id " +
						" order by tag_level1, tag_level2,tag_level3, tag_level4, r.tag " ;
			}
			else {
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
				" r.name, r.description, r.tag, r.full_tag, " +
				" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\" ," +
				" r.approvers, " + 
				" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
				" r.trace_to, r.trace_from , " +
				" r.user_defined_attributes, r.testing_status, r.deleted, f.folder_path, r.created_by," +
				" to_char(r.created_dt, 'DD MON YYYY') \"created_dt\" , " +
				" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
				" FROM gr_requirements r , gr_requirement_types rt, gr_folders f " +
				" where r.project_id = ?   and r.deleted = 0  " +
				" and  r.parent_full_tag = ? " +
				" and r.requirement_type_id = rt.id  " +
				" and r.folder_id = f.id " +
				" order by tag_level1, tag_level2,tag_level3, tag_level4, r.tag " ;
			}
			

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, parentFullTag);
			rs = prepStmt.executeQuery();

			while (rs.next()){
				//we use the folderId we got as a parameter to this constructor.
				int childRequirementId = rs.getInt("id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				int folderId = rs.getInt("folder_id");
				projectId = rs.getInt("project_id");
				parentFullTag = rs.getString("parent_full_tag");
				
				String requirementName = rs.getString("name");
				String requirementDescription = rs.getString ("description");
				String requirementTag = rs.getString("tag");
				String requirementFullTag = rs.getString("full_tag");
				int version = rs.getInt("version");
				String approvedByAllDt = rs.getString("approved_by_all_dt");
				String approvers = rs.getString("approvers");
				String requirementStatus = rs.getString("status");
				String requirementPriority = rs.getString("priority");
				String requirementOwner = rs.getString("owner");
				String requirementLockedBy = rs.getString("locked_by");
				int requirementPctComplete = rs.getInt("pct_complete");
				String requirementExternalUrl = rs.getString("external_url");
				String traceTo = rs.getString("trace_to");
				String traceFrom = rs.getString("trace_from");
				String userDefinedAttributes = rs.getString("user_defined_attributes");
				String testingStatus = rs.getString("testing_status");
				int deleted = rs.getInt("deleted");
				String folderPath = rs.getString("folder_path");
				String createdBy = rs.getString("created_by");
				String createdDt = rs.getString("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by") ;
				
				//	Date lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");
				
				Requirement childRequirement = new Requirement(childRequirementId, requirementTypeId, folderId, 
						projectId, requirementName, requirementDescription,	 requirementTag, 
						requirementFullTag, version, approvedByAllDt, approvers,  
						requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
						requirementPctComplete, requirementExternalUrl ,traceTo, traceFrom, 
						userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy,
						requirementTypeName, createdDt);

				childRequirements.add(childRequirement);
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
		return childRequirements;
	}

	
	
	public static void logViewEvent (int requirementId, int userId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();		
			
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_view_log (user_id, requirement_id, view_dt) values (?,?,now()) ";
			}
			else {
				sql = "insert into gr_view_log (user_id, requirement_id, view_dt) values (?,?, sysdate) ";
				
			}
			prepStmt  = con.prepareStatement(sql);
			prepStmt.setInt(1,userId);
			prepStmt.setInt(2, requirementId);
			
			prepStmt.execute();
			prepStmt.close();

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
	}		
	
	
	
	
	
	public static int  daysSinceSubmittedForApproval (int requirementId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		int daysSinceSubmittedForApproval = 0;
		
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();		
			
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " select datediff(now() , submitted_for_approval_dt) 'daysSinceSubmittedForApproval' " +
					" from gr_requirements where id = ? ";
			}
			else {
				sql = " select sysdate - submitted_for_approval_dt 'daysSinceSubmittedForApproval' " +
					" from gr_requirements where id = ? ";
				
			}
			prepStmt  = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				daysSinceSubmittedForApproval = rs.getInt("daysSinceSubmittedForApproval");
			}
			
			prepStmt.execute();
			prepStmt.close();

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
		
		return daysSinceSubmittedForApproval;
	}		
	
	
	public static int  daysSinceLastApprovalReminder (int requirementId, String databaseType){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		int daysSinceLastApprovalReminder = 0;
		
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();		
			
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " select datediff(now() , last_approval_reminder_sent_dt) 'daysSinceLastApprovalReminder' " +
					" from gr_requirements where id = ? ";
			}
			else {
				sql = " select sysdate - last_approval_reminder_sent_dt 'daysSinceLastApprovalReminder' " +
					" from gr_requirements where id = ? ";
				
			}
			prepStmt  = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				daysSinceLastApprovalReminder = rs.getInt("daysSinceLastApprovalReminder");
			}
			
			prepStmt.execute();
			prepStmt.close();

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
		
		return daysSinceLastApprovalReminder;
	}		
	
	
	public static String  removeWordCrap (String inputString){
		
		String outputString = "";
		if (inputString == null ){
			inputString = "";
		}
		try {
			
			if (inputString.toLowerCase().contains("lsdexception")){
				
				String pattern = "<w:[^>]*>(.*?)<\\/w:[^>]*>";
				inputString = inputString.toLowerCase().replaceAll( pattern, "" ) ;
				
				
				
				
				pattern = "<meta[^>]*";
				inputString = inputString.toLowerCase().replaceAll( pattern, "" ) ;
				
				
				
				pattern = "<link[^>]*";
				inputString = inputString.toLowerCase().replaceAll( pattern, "" ) ;
				
				pattern = "<style[^>]*>([\\w|\\W|\n]*?)<\\/style>";
				inputString = inputString.toLowerCase().replaceAll( pattern, "" ) ;
				
				
				pattern = "<\\!--([\\w|\\W|\\n]*?)-->";
				inputString = inputString.replaceAll( pattern, "" ) ;
				
				
				pattern = "<(\\/)*(\\\\?xml:|meta|link|span|font|del|ins|st1:|[ovwxp]:)((.|\\s)*?)>";
				inputString = inputString.replaceAll( pattern, "" ) ;
				
				
				
				// lets unescape and then re try.
				// this takes care of situations where < was escaped to &lt
				inputString = StringEscapeUtils.unescapeHtml(inputString);
				pattern = "<w:[^>]*>(.*?)<\\/w:[^>]*>";
				inputString = inputString.toLowerCase().replaceAll( pattern, "" ) ;
				
				
				
				pattern = "<meta[^>]*";
				inputString = inputString.toLowerCase().replaceAll( pattern, "" ) ;
				
				
				pattern = "<link[^>]*";
				inputString = inputString.toLowerCase().replaceAll( pattern, "" ) ;
				
				
				pattern = "<style[^>]*>([\\w|\\W|\n]*?)<\\/style>";
				inputString = inputString.toLowerCase().replaceAll( pattern, "" ) ;
				
				
				pattern = "<\\!--([\\w|\\W|\\n]*?)-->";
				inputString = inputString.replaceAll( pattern, "" ) ;
				
				
				pattern = "<(\\/)*(\\\\?xml:|meta|link|span|font|del|ins|st1:|[ovwxp]:)((.|\\s)*?)>";
				inputString = inputString.replaceAll( pattern, "" ) ;
				
				
				outputString = inputString;
				
			}
			else {
				outputString = inputString;
			}

		} catch (Exception e) {
			e.printStackTrace();
			outputString = inputString;
		}
		
		return outputString;
	}		
	
	

}
