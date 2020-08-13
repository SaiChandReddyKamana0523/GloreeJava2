package com.gloree.utils;

//GloreeJava2



import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.InitialContext;

public class BaselineMetricsUtil {
	//
	// This class is used to run static queries to get
	// URLs that can call to Google to get the charts.
	// 
	public static ArrayList<HashMap<String,Object>> getBaseLineReport(int projectId, int rTBaselineId){
		java.sql.Connection con = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		
		ArrayList<HashMap<String,Object>> baselineReport = new ArrayList<HashMap<String,Object>>();
		
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// This sql gets the all number of requirements in a baseline.

			
			String sql  = "select r.id, r.full_tag," +
				" rbv.version \"baseline_version\"  ," +
				" rbv.name \"baseline_name\"  , " +
				" rbv.description \"baseline_description\" , rbv.user_defined_attributes uda , " +
				" date_format(rb.baselined_dt, '%d %M %Y ') \"baselined_dt\" " + 
				" from gr_requirement_baselines rb, gr_requirements r, gr_requirement_versions rbv " +
				" where r.project_id = ? " +
				" and rb.rt_baseline_id = ? " +
				" and rb.requirement_id = r.id " +
				" and rb.version_id  = rbv.id";
			

			
			
    		prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, rTBaselineId);
			rs = prepStmt.executeQuery();

			while(rs.next() ){
				int reqId = rs.getInt("id");
				String fullTag = rs.getString("full_tag");
				int version = rs.getInt("baseline_version");
				String name = rs.getString("baseline_name");
				String description = rs.getString("baseline_description");
				String uda = rs.getString("uda");
				if (uda == null){uda = "";}
				
				String baselinedDt = rs.getString("baselined_dt");
				
				HashMap<String, Object> bR = new HashMap<String, Object>();
				
				bR.put("reqId", Integer.toString(reqId));
				bR.put("fullTag", fullTag);
				bR.put("version", Integer.toString(version));
				bR.put("name", name);
				bR.put("description", description);
				bR.put("uda", uda);
				bR.put("baselinedDt", baselinedDt);
				
				baselineReport.add(bR);
				
				
			}
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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

		return (baselineReport);
	}
	
	public static int getAllRequirementsInBaseline(int rTBaselineId){
		java.sql.Connection con = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		
		int allReqs = 0;
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// This sql gets the all number of requirements in a baseline.
			String sql = "select count(*) \"allReqs\" " +
				" from gr_requirement_baselines rb, gr_requirements r " +
				" where rb.requirement_id = r.id " +
				" and rb.rt_baseline_id = ? " +
				" and r.deleted = 0";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();

			while (rs.next()){
				allReqs = rs.getInt("allReqs"); 
			}
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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

		return (allReqs);
	}
	

	
	// returns a google charts query that displays allChart requirements in this baseline.
	public static String[] getAllRequirementsInBaselineURL(int rTBaselineId, int allReqs){
		String[] allChart  = new String [2];
		allChart[0] = "http://chart.apis.google.com/chart?cht=p&chd=t:" +
			allReqs + 
			"&chs=350x90&chl=Total Requirements(" + allReqs + ")&chco=&chco=8BFEA8,FF0000";
			
		allChart[1] = "All Requirements (" + allReqs + ")";

		return (allChart);
	}
	
	

	// returns a google charts query that displays inApprovalWorkFlow requirements in this baseline.
	public static String[] getInApprovalWorkFlowInBaselineURL(int rTBaselineId, int allReqs){
		java.sql.Connection con = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String [] inApprovalWorkFlowChart = new String [2];
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// This sql gets the dangling Reqs.
			String sql = "select count(*) \"inApprovalWorkFlowReqs\" " +
			" from gr_requirement_baselines rb, gr_requirements r " +
			" where rb.requirement_id = r.id " +
			" and rb.rt_baseline_id = ? " +
			" and r.deleted = 0" +
			" and r.status = 'In Approval WorkFlow'";			

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();
			int inApprovalWorkFlowReqs = 0;
			while (rs.next()){
				inApprovalWorkFlowReqs = rs.getInt("inApprovalWorkFlowReqs"); 
			}

			int nonApprovalWorkFlowReqs = allReqs - inApprovalWorkFlowReqs;
			inApprovalWorkFlowChart[0] = "http://chart.apis.google.com/chart?cht=p&chd=t:" +
				nonApprovalWorkFlowReqs + "," + inApprovalWorkFlowReqs + 
				"&chs=350x90&chl=Not In WorkFlow (" + nonApprovalWorkFlowReqs + ")|In Approval WorkFlow(" +
				+ inApprovalWorkFlowReqs + ")&chco=&chco=8BFEA8,FF0000";
			
			inApprovalWorkFlowChart[1] = "In Approval WorkFlow (" + inApprovalWorkFlowReqs + ")";
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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

		return (inApprovalWorkFlowChart);
	}
	

	// returns a google charts query that displays approved requirements in this baseline.
	public static String[] getApprovedInBaselineURL(int rTBaselineId, int allReqs){
		java.sql.Connection con = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String [] approvedChart = new String [2];
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// This sql gets the dangling Reqs.
			String sql = "select count(*) \"approvedReqs\" " +
			" from gr_requirement_baselines rb, gr_requirements r " +
			" where rb.requirement_id = r.id " +
			" and rb.rt_baseline_id = ? " +
			" and r.deleted = 0" + 
			" and r.status = 'Approved'";			
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();
			int approvedReqs = 0;
			while (rs.next()){
				approvedReqs = rs.getInt("approvedReqs"); 
			}

			int nonApprovedReqs = allReqs - approvedReqs;
			approvedChart[0] = "http://chart.apis.google.com/chart?cht=p&chd=t:" +
				nonApprovedReqs + "," + approvedReqs + 
				"&chs=350x90&chl=Others (" + nonApprovedReqs + ")|Approved(" +
				+ approvedReqs + ")&chco=&chco=8BFEA8,FF0000";
			
			approvedChart[1] = "Approved (" + approvedReqs + ")";
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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

		return (approvedChart);
	}

	
	// returns a google charts query that displays rejected requirements in this baseline.
	public static String[] getRejectedInBaselineURL(int rTBaselineId, int allReqs){
		java.sql.Connection con = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String [] rejectedChart = new String [2];
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// This sql gets the dangling Reqs.
			String sql = "select count(*) \"rejectedReqs\" " +
			" from gr_requirement_baselines rb, gr_requirements r " +
			" where rb.requirement_id = r.id " +
			" and rb.rt_baseline_id = ? " +
			" and r.deleted = 0" +
			" and r.status = 'Rejected'";			

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();
			int rejectedReqs = 0;
			while (rs.next()){
				rejectedReqs = rs.getInt("rejectedReqs"); 
			}

			int nonRejectedReqs = allReqs - rejectedReqs;
			rejectedChart[0] = "http://chart.apis.google.com/chart?cht=p&chd=t:" +
				nonRejectedReqs + "," + rejectedReqs + 
				"&chs=350x90&chl=Others (" + nonRejectedReqs + ")|Rejected(" +
				+ rejectedReqs + ")&chco=&chco=8BFEA8,FF0000";
			
			rejectedChart[1] = "Rejected (" + rejectedReqs + ")";
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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

		return (rejectedChart);
	}
	
	// returns a google charts query that displays dangling requirements in this baseline.
	public static String[] getDanglingInBaselineURL(int rTBaselineId, int allReqs){
		java.sql.Connection con = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String [] danglingChart = new String [2];
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = "select count(*) \"danglingReqs\" " +
			" from gr_requirement_baselines rb, gr_requirements r " +
			" where rb.requirement_id = r.id " +
			" and rb.rt_baseline_id = ? " +
			" and r.deleted = 0" + 
			" and (r.trace_from is null or r.trace_from = '')";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();
			int danglingReqs = 0;
			while (rs.next()){
				danglingReqs = rs.getInt("danglingReqs"); 
			}

			int nonDanglingReqs = allReqs - danglingReqs;
			danglingChart[0] = "http://chart.apis.google.com/chart?cht=p&chd=t:" +
				nonDanglingReqs + "," + danglingReqs + 
				"&chs=350x90&chl=Non Dangling(" + nonDanglingReqs + ")|Dangling(" +
				+ danglingReqs + ")&chco=&chco=8BFEA8,FF0000";
			
			danglingChart[1] = "Dangling Requirements (" + danglingReqs + ")";
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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

		return (danglingChart);
	}

	
	// returns a google charts query that displays orphan requirements in this folder.
	public static String [] getOrphanInBaselineURL(int rTBaselineId, int allReqs){
		java.sql.Connection con =  null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String [] orphanChart = new String [2];
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// This sql gets the Orphan Reqs.
			String sql = "select count(*) \"OrphanReqs\" " +
			" from gr_requirement_baselines rb, gr_requirements r " +
			" where rb.requirement_id = r.id " +
			" and rb.rt_baseline_id = ? " +
			" and r.deleted = 0" + 
			" and (r.trace_to is null or r.trace_to = '')";	

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();
			int orphanReqs = 0;
			while (rs.next()){
				orphanReqs = rs.getInt("OrphanReqs"); 
			}

			int nonOrphanReqs = allReqs - orphanReqs;
			orphanChart[0] = "http://chart.apis.google.com/chart?cht=p&chd=t:" +
				nonOrphanReqs + "," + orphanReqs + 
				"&chs=350x90&chl=Non Orphan(" + nonOrphanReqs + ")|Orphan(" +
				+ orphanReqs + ")&chco=&chco=8BFEA8,FF0000";
				
			orphanChart[1] = "Orphan Requirements (" + orphanReqs + ")";
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();	
		}  finally {
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
		return (orphanChart);
	}		

	// returns a google charts query that displays requirements with upstream suspects in this baseline.
	public static String[] getSuspectUpStreamInBaselineURL(int rTBaselineId, int allReqs){
		java.sql.Connection con =  null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String [] suspectUpStreamChart = new String[2];
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();


			// This sql gets the SuspectUpStream Reqs.
			String sql = "select count(*) \"SuspectUpStreamReqs\" " +
			" from gr_requirement_baselines rb, gr_requirements r " +
			" where rb.requirement_id = r.id " +
			" and rb.rt_baseline_id = ? " +
			" and r.deleted = 0" +	
			" and r.trace_to like '%(s)%'" ;
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();
			int suspectUpStreamReqs = 0;
			while (rs.next()){
				suspectUpStreamReqs = rs.getInt("SuspectUpStreamReqs"); 
			}

			int nonSuspectUpStreamReqs = allReqs - suspectUpStreamReqs;
			suspectUpStreamChart[0] = "http://chart.apis.google.com/chart?cht=p&chd=t:" +
				nonSuspectUpStreamReqs + "," + suspectUpStreamReqs + 
				"&chs=350x90&chl=Clear Up Stream(" + nonSuspectUpStreamReqs + ")|Suspect Up Stream(" +
				+ suspectUpStreamReqs + ")&chco=8BFEA8,FF0000";
				
			suspectUpStreamChart[1] = "Suspect UpStream Requirements (" + suspectUpStreamReqs + ")";
			
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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
		return (suspectUpStreamChart);
	}		

	// returns a google charts query that displays requirements with downstream suspects in this baseline.
	public static String[] getSuspectDownStreamInBaselineURL(int rTBaselineId, int allReqs){
		java.sql.Connection con =  null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String [] suspectDownStreamChart = new String[2];
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();


			// This sql gets the SuspectDownStream Reqs.
			String sql = "select count(*) \"SuspectDownStreamReqs\" " +
			" from gr_requirement_baselines rb, gr_requirements r " +
			" where rb.requirement_id = r.id " +
			" and rb.rt_baseline_id = ? " +
			" and r.deleted = 0" +	
			" and r.trace_from like '%(s)%'" ;
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();
			int suspectDownStreamReqs = 0;
			while (rs.next()){
				suspectDownStreamReqs = rs.getInt("SuspectDownStreamReqs"); 
			}

			int nonSuspectDownStreamReqs = allReqs - suspectDownStreamReqs;
			suspectDownStreamChart[0] = "http://chart.apis.google.com/chart?cht=p&chd=t:" +
				nonSuspectDownStreamReqs + "," + suspectDownStreamReqs + 
				"&chs=350x90&chl=Clear Down Stream(" + nonSuspectDownStreamReqs + ")|SuspectDown Stream(" +
				+ suspectDownStreamReqs + ")&chco=8BFEA8,FF0000";
				
			suspectDownStreamChart[1] = "Suspect DownStream Requirements (" + suspectDownStreamReqs + ")";
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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
		return (suspectDownStreamChart);
	}		
	

	// returns a google charts query that displays Completed requirements in this folder.
	public static String[] getCompletedInBaselineURL(int rTBaselineId, int allReqs){
		java.sql.Connection con = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String[] completedChart = new String[3];
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			

			// This sql gets the Completed Reqs.
			String sql = "select count(*) \"CompletedReqs\" " +
			" from gr_requirement_baselines rb, gr_requirements r " +
			" where rb.requirement_id = r.id " +
			" and rb.rt_baseline_id = ? " +
			" and r.deleted = 0" +	
			" and r.pct_complete = 100 " ;
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();
			int completedReqs = 0;
			while (rs.next()){
				completedReqs = rs.getInt("CompletedReqs"); 
			}

			int nonCompletedReqs = allReqs - completedReqs;
			completedChart[0] = "http://chart.apis.google.com/chart?cht=p&chd=t:" +
				completedReqs + "," + nonCompletedReqs + 
				"&chs=350x90&chl= Completed (" + completedReqs + ")| Incomplete(" +
				+ nonCompletedReqs + ")&chco=8BFEA8,FF0000";
			
			completedChart[1] = "Completed Requirements (" + completedReqs + ")";
			completedChart[2] = "Incomplete Requirements (" + nonCompletedReqs + ")";
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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
		return (completedChart);
	}		
	
	
	//////////////////////////////////////////////////////////////////////////////
	//
	//
	//		By Owner
	//
	//
	//////////////////////////////////////////////////////////////////////////////
	

	// returns a google charts query that displays AllRequirements requirements by Owner in this folder.
	public static String[] getAllRequirementsByOwnerInBaselineURL(int rTBaselineId, int allReqs){
		java.sql.Connection con = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String[] allRequirementsByOwnerChart = new String[2];
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			

			// This sql gets the owner, all reqs and allRequirements Reqs for a folder.
			String sql = " select substr(r.owner,1,20) \"owner\", count(*) \"allRequirementsReqs\" " +
			" from gr_requirement_baselines rb, gr_requirements r " +
			" where rb.requirement_id = r.id " +
			" and rb.rt_baseline_id = ? " +
			" and r.deleted = 0" +	
			" group by r.owner " +
			" order by allRequirementsReqs " ;

			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();

			String allRequirementsString = "";
			String ownersString = "";
			String ownersEmailId = "";
			
			while (rs.next()){
				 
				ownersString += rs.getString("owner") +
					" (" + rs.getInt("allRequirementsReqs")  + ") |";
				ownersEmailId += rs.getString("owner") + ":##:";
				
				int ownerAllRequirementsReqs = rs.getInt("allRequirementsReqs");
				allRequirementsString += ownerAllRequirementsReqs + ",";
			}
			
			// drop the last |
			if (allRequirementsString.contains(",")){
				allRequirementsString = (String) allRequirementsString.subSequence(
					0,allRequirementsString.lastIndexOf(","));
			}
			if (ownersString.contains("|")){
				ownersString = (String) ownersString.subSequence(0,ownersString.lastIndexOf("|"));
			}
			if (ownersEmailId.contains(":##:")){
				ownersEmailId = (String) ownersEmailId.subSequence(0,ownersEmailId.lastIndexOf(":##:"));
			}

			// lets handle the case where only one value exists and we just dropped the last separator.
			
			if ((ownersEmailId != null ) && !(ownersEmailId.contains(":##:"))){
				ownersEmailId += ":##:";
			}

			
			allRequirementsByOwnerChart[0] = "http://chart.apis.google.com/chart?cht=p&chs=350x90" +  
				"&chd=t:" + allRequirementsString + 
				"&chl=" + ownersString  ; 
			
			allRequirementsByOwnerChart[1] = ownersEmailId;
			
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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
		return (allRequirementsByOwnerChart);
	}


	
	// returns a google charts query that displays AllRequirements requirements by Owner in this folder.
	public static String[] getInApprovalWorkFlowByOwnerInBaselineURL(int rTBaselineId, int allReqs){
		java.sql.Connection con = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String[]  inApprovalWorkFlowByOwnerChart = new String[2];
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			

			// This sql gets the owner, all reqs and inApprovalWorkFlow Reqs for a folder.

			
			String sql = "select substr(rah.approver_email_id,1,15) \"owner\" , count(*) \"inApprovalWorkFlowReqs\" " + 
				" from gr_requirement_approval_h rah, gr_requirements r, gr_requirement_baselines rb " +  
				" where rah.requirement_id = r.id " +
				" and rb.requirement_id = r.id " +
				" and rb.rt_baseline_id = ? " +
				" and r.deleted = 0 " + 
				" and r.status = 'In Approval WorkFlow' " +
				" and rah.response = 'Pending' " +
				" group by rah.approver_email_id" ; 
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();

			String inApprovalWorkFlowRequirementsString = "";
			String ownersString = "";
			String ownersEmailId  = "";
			
			while (rs.next()){
				 
				ownersString += rs.getString("owner") +
					" (" + rs.getInt("inApprovalWorkFlowReqs")  + ") |";
				ownersEmailId += rs.getString("owner") + ":##:";
				int ownerinApprovalWorkFlowReqs = rs.getInt("inApprovalWorkFlowReqs");
				inApprovalWorkFlowRequirementsString += ownerinApprovalWorkFlowReqs + ",";
			}
			
			// drop the last |
			if (inApprovalWorkFlowRequirementsString.contains(",")){
				inApprovalWorkFlowRequirementsString = (String) inApprovalWorkFlowRequirementsString.subSequence(
					0,inApprovalWorkFlowRequirementsString.lastIndexOf(","));
			}
			if (ownersString.contains("|")){
				ownersString = (String) ownersString.subSequence(0,ownersString.lastIndexOf("|"));
			}
			if (ownersEmailId.contains(":##:")){
				ownersEmailId = (String) ownersEmailId.subSequence(0,ownersEmailId.lastIndexOf(":##:"));
			}
			
			// lets handle the case where only one value exists and we just dropped the last separator.
			
			if ((ownersEmailId != null ) && !(ownersEmailId.contains(":##:"))){
				ownersEmailId += ":##:";
			}
			
			
			inApprovalWorkFlowByOwnerChart[0] = "http://chart.apis.google.com/chart?cht=p&chs=350x90" +  
				"&chd=t:" + inApprovalWorkFlowRequirementsString + 
				"&chl=" + ownersString  ; 
			inApprovalWorkFlowByOwnerChart[1] = ownersEmailId;
			
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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
		return (inApprovalWorkFlowByOwnerChart);
	}
	

	

	
	// returns a google charts query that displays AllRequirements requirements approved 
	// by the approver in this folder.
	public static String[] getApprovedByOwnerInBaselineURL(int rTBaselineId, int allReqs){
		java.sql.Connection con = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String[]  approvedByOwnerChart = new String[2];
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			

			// This sql gets the owner, all reqs and approved Reqs for a folder.
			String sql = "select substr(rah.approver_email_id,1,15) \"owner\" , count(*) \"approvedReqs\" " + 
			" from gr_requirement_approval_h rah, gr_requirements r, gr_requirement_baselines rb " +  
			" where rah.requirement_id = r.id " +
			" and rb.requirement_id = r.id " +
			" and rb.rt_baseline_id = ? " +
			" and r.deleted = 0 " + 
			" and rah.response = 'Approved' " +
			" group by rah.approver_email_id" ; 
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();

			String approvedRequirementsString = "";
			String ownersString = "";
			String ownersEmailId  = "";
			
			while (rs.next()){
				 
				ownersString += rs.getString("owner") +
					" (" + rs.getInt("approvedReqs")  + ") |";
				ownersEmailId += rs.getString("owner") + ":##:";
				int ownerapprovedReqs = rs.getInt("approvedReqs");
				approvedRequirementsString += ownerapprovedReqs + ",";
			}
			
			// drop the last |
			if (approvedRequirementsString.contains(",")){
				approvedRequirementsString = (String) approvedRequirementsString.subSequence(
					0,approvedRequirementsString.lastIndexOf(","));
			}
			if (ownersString.contains("|")){
				ownersString = (String) ownersString.subSequence(0,ownersString.lastIndexOf("|"));
			}
			
			if (ownersEmailId.contains(":##:")){
				ownersEmailId = (String) ownersEmailId.subSequence(0,ownersEmailId.lastIndexOf(":##:"));
			}
			// lets handle the case where only one value exists and we just dropped the last separator.
			
			if ((ownersEmailId != null ) && !(ownersEmailId.contains(":##:"))){
				ownersEmailId += ":##:";
			}
			
			
			approvedByOwnerChart[0] = "http://chart.apis.google.com/chart?cht=p&chs=350x90" +  
				"&chd=t:" + approvedRequirementsString + 
				"&chl=" + ownersString  ; 
			approvedByOwnerChart[1] = ownersEmailId;
			
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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
		return (approvedByOwnerChart);
	}
	
	// returns a google charts query that displays AllRequirements requirements rejected 
	// by the approver in this folder.
	public static String[] getRejectedByOwnerInBaselineURL(int rTBaselineId, int allReqs){
		java.sql.Connection con = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String[]  rejectedByOwnerChart = new String[2];
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			

			// This sql gets the owner, all reqs and rejected Reqs for a baseline.

			String sql = "select substr(rah.approver_email_id,1,15) \"owner\" , count(*) \"rejectedReqs\" " + 
			" from gr_requirement_approval_h rah, gr_requirements r, gr_requirement_baselines rb " +  
			" where rah.requirement_id = r.id " +
			" and rb.requirement_id = r.id " +
			" and rb.rt_baseline_id = ? " +
			" and r.deleted = 0 " + 
			" and r.status = 'In Approval WorkFlow' " +
			" and rah.response = 'Rejected' " +
			" group by rah.approver_email_id" ; 
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();

			String rejectedRequirementsString = "";
			String ownersString = "";
			String ownersEmailId  = "";
			
			while (rs.next()){
				 
				ownersString += rs.getString("owner") +
					" (" + rs.getInt("rejectedReqs")  + ") |";
				ownersEmailId += rs.getString("owner") + ":##:";
				int ownerrejectedReqs = rs.getInt("rejectedReqs");
				rejectedRequirementsString += ownerrejectedReqs + ",";
			}
			
			// drop the last |
			if (rejectedRequirementsString.contains(",")){
				rejectedRequirementsString = (String) rejectedRequirementsString.subSequence(
					0,rejectedRequirementsString.lastIndexOf(","));
			}
			if (ownersString.contains("|")){
				ownersString = (String) ownersString.subSequence(0,ownersString.lastIndexOf("|"));
			}
			if (ownersEmailId.contains(":##:")){
				ownersEmailId = (String) ownersEmailId.subSequence(0,ownersEmailId.lastIndexOf(":##:"));
			}
			
			// lets handle the case where only one value exists and we just dropped the last separator.
			
			if ((ownersEmailId != null ) && !(ownersEmailId.contains(":##:"))){
				ownersEmailId += ":##:";
			}

			
			rejectedByOwnerChart[0] = "http://chart.apis.google.com/chart?cht=p&chs=350x90" +  
				"&chd=t:" + rejectedRequirementsString + 
				"&chl=" + ownersString  ; 
			rejectedByOwnerChart[1] = ownersEmailId;
			
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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
		return (rejectedByOwnerChart);
	}
	
	
	
	

	// returns a google charts query that displays AllRequirements requirements by Owner in this folder.
	public static String[] getDanglingByOwnerInBaselineURL(int rTBaselineId, int allReqs){
		java.sql.Connection con = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String[]  danglingByOwnerChart = new String[2];
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			

			// This sql gets the owner, all reqs and dangling Reqs for a baseline

			String sql = " select substr(r.owner,1,20) \"owner\", count(*) \"danglingReqs\" " + 
				" from gr_requirement_baselines rb, gr_requirements r " +
				" where rb.requirement_id = r.id " +
				" and rb.rt_baseline_id = ? " +
				" and r.deleted = 0 " + 
				" and (r.trace_from is null or r.trace_from = '') "  +
				" group by r.owner " +
				" order by danglingReqs " ;
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();

			String danglingRequirementsString = "";
			String ownersString = "";
			String ownersEmailId  = "";
			
			while (rs.next()){
				 
				ownersString += rs.getString("owner") +
					" (" + rs.getInt("danglingReqs")  + ") |";
				ownersEmailId += rs.getString("owner") + ":##:";
				int ownerDanglingReqs = rs.getInt("danglingReqs");
				danglingRequirementsString += ownerDanglingReqs + ",";
			}
			
			// drop the last |
			if (danglingRequirementsString.contains(",")){
				danglingRequirementsString = (String) danglingRequirementsString.subSequence(
					0,danglingRequirementsString.lastIndexOf(","));
			}
			if (ownersString.contains("|")){
				ownersString = (String) ownersString.subSequence(0,ownersString.lastIndexOf("|"));
			}
			if (ownersEmailId.contains(":##:")){
				ownersEmailId = (String) ownersEmailId.subSequence(0,ownersEmailId.lastIndexOf(":##:"));
			}
			
			// lets handle the case where only one value exists and we just dropped the last separator.
			
			if ((ownersEmailId != null ) && !(ownersEmailId.contains(":##:"))){
				ownersEmailId += ":##:";
			}

			
			danglingByOwnerChart[0] = "http://chart.apis.google.com/chart?cht=p&chs=350x90" +  
				"&chd=t:" + danglingRequirementsString + 
				"&chl=" + ownersString  ; 
			danglingByOwnerChart[1] = ownersEmailId;
			
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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
		return (danglingByOwnerChart);
	}
	

	
	
	// returns a google charts query that displays Orphan requirements by Owner in this folder.
	public static String[] getOrphanByOwnerInBaselineURL(int rTBaselineId, int allReqs){
		java.sql.Connection con = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String [] orphanByOwnerChart = new String[2];
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			

			// This sql gets the owner, all reqs and orphan Reqs for a folder.
			String sql = " select substr(r.owner,1,20) \"owner\", count(*) \"orphanReqs\" " + 
			" from gr_requirement_baselines rb, gr_requirements r " +
			" where rb.requirement_id = r.id " +
			" and rb.rt_baseline_id = ? " +
			" and r.deleted = 0 " + 
			" and (r.trace_to is null or r.trace_to = '') "  +
			" group by r.owner " +
			" order by orphanReqs " ;

			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();

			String orphanRequirementsString = "";
			String ownersString = "";
			String ownersEmailId = "";
			
			while (rs.next()){
				 
				ownersString += rs.getString("owner") +
					" (" + rs.getInt("orphanReqs")  + ") |";
				ownersEmailId += rs.getString("owner") + ":##:";
				int ownerOrphanReqs = rs.getInt("orphanReqs");
				orphanRequirementsString += ownerOrphanReqs + ",";
			}
			
			// drop the last |
			if (orphanRequirementsString.contains(",")){
				orphanRequirementsString = (String) orphanRequirementsString.subSequence(
					0,orphanRequirementsString.lastIndexOf(","));
			}
			if (ownersString.contains("|")){
				ownersString = (String) ownersString.subSequence(0,ownersString.lastIndexOf("|"));
			}
			if (ownersEmailId.contains(":##:")){
				ownersEmailId = (String) ownersEmailId.subSequence(0,ownersEmailId.lastIndexOf(":##:"));
			}
			
			// lets handle the case where only one value exists and we just dropped the last separator.
			
			if ((ownersEmailId != null ) && !(ownersEmailId.contains(":##:"))){
				ownersEmailId += ":##:";
			}

			
			orphanByOwnerChart[0] = "http://chart.apis.google.com/chart?cht=p&chs=350x90" +  
				"&chd=t:" + orphanRequirementsString + 
				"&chl=" + ownersString  ; 
			orphanByOwnerChart[1] = ownersEmailId;
			
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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
		return (orphanByOwnerChart);
	}
	

	// returns a google charts query that displays SuspectUpStream requirements by Owner in this folder.
	public static String [] getSuspectUpStreamByOwnerInBaselineURL(int rTBaselineId, int allReqs){
		java.sql.Connection con = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String [] suspectUpStreamByOwnerChart = new String[2];
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			

			// This sql gets the owner, all reqs and suspectUpStream Reqs for a folder.
			String sql = " select substr(r.owner,1,20) \"owner\", count(*) \"suspectUpStreamReqs\" " + 
			" from gr_requirement_baselines rb, gr_requirements r " +
			" where rb.requirement_id = r.id " +
			" and rb.rt_baseline_id = ? " +
			" and r.deleted = 0 " + 
			" and r.trace_to like '%(s)%'" +
			" group by r.owner " +
			" order by suspectUpStreamReqs " ;
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();

			String suspectUpStreamRequirementsString = "";
			String ownersString = "";
			String ownersEmailId  = "";
			
			while (rs.next()){
				 
				ownersString += rs.getString("owner") +
					" (" + rs.getInt("suspectUpStreamReqs")  + ") |";
				ownersEmailId += rs.getString("owner") + ":##:";
				int ownerSuspectUpStreamReqs = rs.getInt("suspectUpStreamReqs");
				suspectUpStreamRequirementsString += ownerSuspectUpStreamReqs + ",";
			}
			
			// drop the last |
			if (suspectUpStreamRequirementsString.contains(",")){
				suspectUpStreamRequirementsString = (String) suspectUpStreamRequirementsString.subSequence(
					0,suspectUpStreamRequirementsString.lastIndexOf(","));
			}
			if (ownersString.contains("|")){
				ownersString = (String) ownersString.subSequence(0,ownersString.lastIndexOf("|"));
			}
			if (ownersEmailId.contains(":##:")){
				ownersEmailId = (String) ownersEmailId.subSequence(0,ownersEmailId.lastIndexOf(":##:"));
			}
			
			// lets handle the case where only one value exists and we just dropped the last separator.
			
			if ((ownersEmailId != null ) && !(ownersEmailId.contains(":##:"))){
				ownersEmailId += ":##:";
			}

			
			suspectUpStreamByOwnerChart[0] = "http://chart.apis.google.com/chart?cht=p&chs=350x90" +  
				"&chd=t:" + suspectUpStreamRequirementsString + 
				"&chl=" + ownersString  ; 
			suspectUpStreamByOwnerChart[1] = ownersEmailId; 
			
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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
		return (suspectUpStreamByOwnerChart);
	}

	

	// returns a google charts query that displays SuspectDownStream requirements by Owner in this folder.
	public static String [] getSuspectDownStreamByOwnerInBaselineURL(int rTBaselineId, int allReqs){
		java.sql.Connection con = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String [] suspectDownStreamByOwnerChart = new String[2];
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			

			// This sql gets the owner, all reqs and suspectDownStream Reqs for a folder.
			String sql = " select substr(r.owner,1,20) \"owner\", count(*) \"suspectDownStreamReqs\" " + 
			" from gr_requirement_baselines rb, gr_requirements r " +
			" where rb.requirement_id = r.id " +
			" and rb.rt_baseline_id = ? " +
			" and r.deleted = 0 " + 
			" and r.trace_from like '%(s)%'" + 
			" group by r.owner " +
			" order by suspectDownStreamReqs " ;
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();

			String suspectDownStreamRequirementsString = "";
			String ownersString = "";
			String ownersEmailId  = "";
			
			while (rs.next()){
				 
				ownersString += rs.getString("owner") +
					" (" + rs.getInt("suspectDownStreamReqs")  + ") |";
				ownersEmailId += rs.getString("owner") + ":##:";
				int ownerSuspectDownStreamReqs = rs.getInt("suspectDownStreamReqs");
				suspectDownStreamRequirementsString += ownerSuspectDownStreamReqs + ",";
			}
			
			// drop the last |
			if (suspectDownStreamRequirementsString.contains(",")){
				suspectDownStreamRequirementsString = (String) suspectDownStreamRequirementsString.subSequence(
					0,suspectDownStreamRequirementsString.lastIndexOf(","));
			}
			if (ownersString.contains("|")){
				ownersString = (String) ownersString.subSequence(0,ownersString.lastIndexOf("|"));
			}
			if (ownersEmailId.contains(":##:")){
				ownersEmailId = (String) ownersEmailId.subSequence(0,ownersEmailId.lastIndexOf(":##:"));
			}
			
			// lets handle the case where only one value exists and we just dropped the last separator.
			
			if ((ownersEmailId != null ) && !(ownersEmailId.contains(":##:"))){
				ownersEmailId += ":##:";
			}

			
			suspectDownStreamByOwnerChart[0] = "http://chart.apis.google.com/chart?cht=p&chs=350x90" +  
				"&chd=t:" + suspectDownStreamRequirementsString + 
				"&chl=" + ownersString  ; 
			
			suspectDownStreamByOwnerChart[1] = ownersEmailId;
			
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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
		return (suspectDownStreamByOwnerChart);
	}

	

	// returns a google charts query that displays Completion requirements by Owner in this folder.
	public static String [] getCompletionByOwnerInBaselineURL(int rTBaselineId, int allReqs){
		java.sql.Connection con = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String [] completionByOwnerChart = new String[2];
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			

			// This sql gets the owner, all reqs and completion Reqs for a baseline
			String sql = " select substr(r.owner,1,20) \"owner\", count(*) \"completionReqs\" " + 
			" from gr_requirement_baselines rb, gr_requirements r " +
			" where rb.requirement_id = r.id " +
			" and rb.rt_baseline_id = ? " +
			" and r.deleted = 0 " + 
			" and r.pct_complete = 100 " + 
			" group by r.owner " +
			" order by completionReqs " ;
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();

			String completionRequirementsString = "";
			String ownersString = "";
			String ownersEmailId = "";
			
			while (rs.next()){
				 
				ownersString += rs.getString("owner") +
					" (" + rs.getInt("completionReqs")  + ") |";
				ownersEmailId += rs.getString("owner") + ":##:";
				int ownerCompletionReqs = rs.getInt("completionReqs");
				completionRequirementsString += ownerCompletionReqs + ",";
			}
			
			// drop the last |
			if (completionRequirementsString.contains(",")){
				completionRequirementsString = (String) completionRequirementsString.subSequence(
					0,completionRequirementsString.lastIndexOf(","));
			}
			if (ownersString.contains("|")){
				ownersString = (String) ownersString.subSequence(0,ownersString.lastIndexOf("|"));
			}
			if (ownersEmailId.contains(":##:")){
				ownersEmailId = (String) ownersEmailId.subSequence(0,ownersEmailId.lastIndexOf(":##:"));
			}
			
			// lets handle the case where only one value exists and we just dropped the last separator.
			
			if ((ownersEmailId != null ) && !(ownersEmailId.contains(":##:"))){
				ownersEmailId += ":##:";
			}

			completionByOwnerChart[0] = "http://chart.apis.google.com/chart?cht=p&chs=350x90" +  
				"&chd=t:" + completionRequirementsString + 
				"&chl=" + ownersString  ; 
			completionByOwnerChart[1] = ownersEmailId;
			
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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
		return (completionByOwnerChart);
	}
	

	// returns a google charts query that displays NonCompletion requirements by Owner in this folder.
	public static String [] getNonCompletionByOwnerInBaselineURL(int rTBaselineId, int allReqs){
		java.sql.Connection con = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String [] nonCompletionByOwnerChart = new String [2];
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			

			// This sql gets the owner, all reqs and nonCompletion Reqs for a folder.
			String sql = " select substr(r.owner,1,20) \"owner\", count(*) \"nonCompletionReqs\" " + 
			" from gr_requirement_baselines rb, gr_requirements r " +
			" where rb.requirement_id = r.id " +
			" and rb.rt_baseline_id = ? " +
			" and r.deleted = 0 " + 
			" and r.pct_complete <> 100 " +  
			" group by r.owner " +
			" order by nonCompletionReqs " ;
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTBaselineId);
			rs = prepStmt.executeQuery();

			String nonCompletionRequirementsString = "";
			String ownersString = "";
			String ownersEmailId = "";
			
			while (rs.next()){
				 
				ownersString += rs.getString("owner") +
					" (" + rs.getInt("nonCompletionReqs")  + ") |";
				ownersEmailId += rs.getString("owner") + ":##:";
				int ownerNonCompletionReqs = rs.getInt("nonCompletionReqs");
				nonCompletionRequirementsString += ownerNonCompletionReqs + ",";
			}
			
			// drop the last |
			if (nonCompletionRequirementsString.contains(",")){
				nonCompletionRequirementsString = (String) nonCompletionRequirementsString.subSequence(
					0,nonCompletionRequirementsString.lastIndexOf(","));
			}
			if (ownersString.contains("|")){
				ownersString = (String) ownersString.subSequence(0,ownersString.lastIndexOf("|"));
			}
			if (ownersEmailId.contains(":##:")){
				ownersEmailId = (String) ownersEmailId.subSequence(0,ownersEmailId.lastIndexOf(":##:"));
			}
			
			// lets handle the case where only one value exists and we just dropped the last separator.
			
			if ((ownersEmailId != null ) && !(ownersEmailId.contains(":##:"))){
				ownersEmailId += ":##:";
			}

			
			nonCompletionByOwnerChart[0] = "http://chart.apis.google.com/chart?cht=p&chs=350x90" +  
				"&chd=t:" + nonCompletionRequirementsString + 
				"&chl=" + ownersString  ; 
			nonCompletionByOwnerChart[1] = ownersEmailId; 
			
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
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
		return (nonCompletionByOwnerChart);
	}
	
}