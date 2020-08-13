package com.gloree.utils;

//GloreeJava2


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import com.gloree.beans.*;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class MetricsUtil {
	
	public static void captureProjectMetrics( Project project,  String databaseType){
		java.sql.Connection con = null;
		try {
			
			Calendar cal = Calendar.getInstance();
			
			
			String projectName = project.getProjectName();
			int projectId = project.getProjectId();
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			System.out.println ("Database connection established");


			// check if entry exists in GR systems table . if yes, sleep for 1 minute. 
			while (isMetricsBusy(con, projectId, databaseType)){
				System.out.println("Going to sleep for 10 seconds Metrics is busy for project " + projectId);
				Thread.sleep(10000);
			}
			// if not, make an entry in the GR systems table
			String sql = " insert into gr_system (label,value) values (?,?)";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, "runningProjectMetrics");
			prepStmt.setInt(2, -1);
			prepStmt.execute();
			prepStmt.close();
			
			// purge old release_folder_metrics data, as we will calculate this fresh today.
			// TODO SRT : Why are we doing this line? becase I think : only one set of numbers need to exist
			// for  release folder metrics level. we are not showing trends data
			// if so, what happened to project_folder_metrics??? shouldn't we be doing the same here???
	    	sql = "delete from gr_release_folder_metrics where project_id = ?";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, project.getProjectId());
			prepStmt.execute();
			
			
			
			// lets get the data load time. we will use this timestamp for all data loads
			// in this run.
		    String dataLoadDt = "";
		    sql = "";
		    if (databaseType.equals("mySQL")){
		    	sql = "select  date_format(now(), '%d %m %y %h:%i:%s') 'dataLoadDt' from dual";
			}
			else{
				sql = "select  to_char(sysdate, 'DD MON YYYY HH MI SS') \"dataLoadDt\" from dual";
			}
		    
		    
		    prepStmt = con.prepareStatement(sql);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				dataLoadDt = rs.getString("dataLoadDt");
			}
		    rs.close();
		    prepStmt.close();
		    
		    String debug = "yes";
		    
		    System.out.println("\n\nStarting Metrics gathering for project " + projectName + " at " + Calendar.getInstance().getTime());

	    	System.out.println("Starting fillUpTestingStatus for project " + projectName + " at " + cal.getTime());
	    	// lets fill up the Testing Status information 
	    	fillUpTestingStatus(con,projectId, databaseType, debug);
	    	System.out.println("Finished fillUpTestingStatus for project " + projectName + " at " + cal.getTime());


		    sql = "select pct_complete_driver_requirement_type_id " + 
			" from gr_projects  " +
			" where id = ?  "; 
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, project.getProjectId());
			rs = prepStmt.executeQuery();
		    while (rs.next()) {
		    	// for each project in the system, we will calculate the Project, Release, Baseline and Folder trends.

	    		int percentCompleteDriverReqTypeId = rs.getInt("pct_complete_driver_requirement_type_id");
		    
		    	System.out.println("Starting fillUpTestingStatus for project " + projectName + " at " + cal.getTime());
		    	// lets fill up the Testing Status information
		    	if (percentCompleteDriverReqTypeId > 0 ){
		    		fillUpPercentComplete(con,projectId,percentCompleteDriverReqTypeId, databaseType, debug);
		    	}
		    	System.out.println("Finished fillUpTestingStatus for project " + projectName + " at " + cal.getTime());
		    }
	    	prepStmt.close();
	    	rs.close();
    		
	    	System.out.println("Starting PopulateProjectMetrics for project " + projectName + " at " + Calendar.getInstance().getTime());
	    	// lets fill up the project trend information in gr_project_metrics table.
	    	fillUpProjectMetrics(con,projectId,dataLoadDt, databaseType);
	    	System.out.println("Finished PopulateProjectMetrics for project " + projectName + " at " + Calendar.getInstance().getTime());
	    	
	    	System.out.println("Starting PopulateUserMetrics for project " + projectName + " at " + Calendar.getInstance().getTime());
	    	// lets fill up the user trend information in gr_user_metrics table.
	    	// due to the sheer volume of data that can be collected, we won't trend 
	    	// the user metrics, we will always remove the old data.
	    	// if we ever want to start trending user data, we can simply remove the purging logic.
	    	try{
	    	fillUpUserMetrics(con,projectId,dataLoadDt, databaseType);
	    	}
	    	catch (Exception uM){
	    		uM.printStackTrace();
	    	}
	    	System.out.println("Finished PopulateUserMetrics for project " + projectName + " at " + Calendar.getInstance().getTime());
	    	
	    	
	    	System.out.println("Starting PopulateReleaseMetrics for project " + projectName + " at " + Calendar.getInstance().getTime());		    	
	    	populateReleaseMetrics(con, projectId, dataLoadDt, databaseType);
	    	System.out.println("Finished PopulateReleaseMetrics for project " + projectName + " at " + Calendar.getInstance().getTime());
	    	
	    	

	    	System.out.println("Starting PopulateBaselineMetrics for project " + projectName + " at " + Calendar.getInstance().getTime());		    	
	    	fillUpBaselineMetrics(con, projectId, dataLoadDt, databaseType);
	    	System.out.println("Finished PopulateBaselineMetrics for project " + projectName + " at " + Calendar.getInstance().getTime());
	    	
	    	System.out.println("Starting PopulateFolderMetrics for project " + projectName + " at " + Calendar.getInstance().getTime());		    	
	    	fillUpFolderMetrics(con, projectId, dataLoadDt, databaseType);
	    	System.out.println("Finished PopulateFolderMetrics for project " + projectName + " at " + Calendar.getInstance().getTime());

	    	System.out.println("Starting PopulateSprintMetrics for project " + projectName + " at " + Calendar.getInstance().getTime());		    	
	    	fillUpSprintMetrics(con, projectId, dataLoadDt, databaseType);
	    	System.out.println("Finished PopulateSprintMetrics for project " + projectName + " at " + Calendar.getInstance().getTime());

		
	    	System.out.println("Starting PopulateDefectMetrics for project " + projectName + " at " + Calendar.getInstance().getTime());		    	
	    	fillUpDefectMetrics(con, projectId, dataLoadDt, databaseType);
	    	System.out.println("Finished PopulateDefectMetrics for project " + projectName + " at " + Calendar.getInstance().getTime());
			
	    	System.out.println("Finished Metrics gathering for project " + projectName + " at " + Calendar.getInstance().getTime());

	    	
	    	
	    	// delete entry in gr systems table.
			sql = " delete from gr_system where label = ? and value = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, "runningProjectMetrics");
			prepStmt.setInt(2, -1);
			prepStmt.execute();
			prepStmt.close();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (con != null) {
				// delete entry in gr systems table.
				try {
					String sql = " delete from gr_system where label = ? and value = ? ";
					PreparedStatement prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, "runningProjectMetrics");
					prepStmt.setInt(2, -1);
					prepStmt.execute();
					prepStmt.close();
				}
				catch (Exception e){
					e.printStackTrace();
				}
				
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
	}	
	
	
	// This routine 
	// sets the Testing Status of all non-TR requirenments to Pending
	// and it looks at all the Test Results 
	// and builds  a hiererachy and sets the status of the heirarchy working its way up.
	
	private static void fillUpTestingStatus(java.sql.Connection con, int projectId, String databaseType, String debug)
		throws SQLException{
		
		
		// lets drop the temp table we will use for data crunching. Since it may not exist, lets do it 
		// in  try / catch mode.
		// we could have used mySQL Temporary tables, but from debugging perspective these tables are better
		// as they will be available after the job is run.
		
		try {
			String sql = "drop table gr_testing_temp" + Integer.toString(projectId);
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
		}
		catch (Exception e) {
		
		}
		
		// lets also drop the gr_testing_temp_no_duplicates
		try {
			String sql = "drop table gr_testing_temp_no_duplicates" + Integer.toString(projectId);
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
		}
		catch (Exception e) {
			
		}
		
		
		// lets build the hierarchy tree of all requirements above TR
		// processing 1st tree_level (the TR tree_level) of the trace tree. We will continuously process a higher tree_level
		// for eight more tree_levels.
		// lets fill the gr_testing_temp table with all the TR's 
		String sql = "create table gr_testing_temp" + Integer.toString(projectId) + " as " +
		" select r.id \"requirement_id\", 1  \"tree_level\" " +
		" from gr_requirements r , gr_requirement_types rt" +
		" where  r.requirement_type_id = rt.id " +
		" and r.project_id =   " + projectId + 
		" and rt.name = 'Test Results' ";

		PreparedStatement prepStmt = con.prepareStatement(sql);
	
		prepStmt.execute();
		
		

		// lets get the second tree_level of Requirements in the tree. These are all Parent Requirements of Level 1 requirements.
		if (databaseType.equals("mySQL")){
			sql = "insert into gr_testing_temp" + Integer.toString(projectId) + " (requirement_id, tree_level)" +
				" select to_requirement_id , 2  \"tree_level\"" +
				" from gr_traces t, gr_testing_temp" +  Integer.toString(projectId) + " temp" +
				" where t.from_requirement_id = temp.requirement_id" +
				" and temp.tree_level = 1 ";
			}
		else{
				sql = "insert into gr_testing_temp" + Integer.toString(projectId) + " (\"requirement_id\", \"tree_level\")" +
				" select to_requirement_id , 2 \"tree_level\" " +
				" from gr_traces t, gr_testing_temp" +  Integer.toString(projectId) + " temp" +
				" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
				" and \"temp\".\"tree_level\" = 1 ";
		}
		
		prepStmt = con.prepareStatement(sql);
		prepStmt.execute();
		prepStmt.close();
		
		// lets get the third tree_level of Requirements in the tree. These are all Parent Requirements of Level 2 requirements.
		
		if (databaseType.equals("mySQL")){
			sql = "insert into gr_testing_temp" + Integer.toString(projectId) + " (requirement_id, tree_level)" +
				" select to_requirement_id , 3 " +
				" from gr_traces t, gr_testing_temp" +  Integer.toString(projectId) + " temp" +
				" where t.from_requirement_id = temp.requirement_id" +
				" and temp.tree_level = 2 ";
			}
		else{
				sql = "insert into gr_testing_temp"+  Integer.toString(projectId) +" (\"requirement_id\", \"tree_level\")" +
				" select to_requirement_id , 3 \"tree_level\" " +
				" from gr_traces t, gr_testing_temp" +  Integer.toString(projectId) + " temp" +
				" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
				" and \"temp\".\"tree_level\" = 2 ";
		}
		prepStmt = con.prepareStatement(sql);
		prepStmt.execute();
		prepStmt.close();
		
		
		// lets get the fourth tree_level of Requirements in the tree. These are all Parent Requirements of Level 3 requirements.
		if (databaseType.equals("mySQL")){
			sql = "insert into gr_testing_temp"+  Integer.toString(projectId) +" (requirement_id, tree_level)" +
				" select to_requirement_id , 4 " +
				" from gr_traces t, gr_testing_temp" +  Integer.toString(projectId) + " temp" +
				" where t.from_requirement_id = temp.requirement_id" +
				" and temp.tree_level = 3 ";
			}
		else{
				sql = "insert into gr_testing_temp"+  Integer.toString(projectId) +" (\"requirement_id\", \"tree_level\")" +
				" select to_requirement_id , 4 \"tree_level\" " +
				" from gr_traces t, gr_testing_temp" +  Integer.toString(projectId) + " temp" +
				" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
				" and \"temp\".\"tree_level\" = 3 ";
		}
		prepStmt = con.prepareStatement(sql);
		prepStmt.execute();
		prepStmt.close();
		
		
		// lets get the fifth tree_level of Requirements in the tree. These are all Parent Requirements of Level 4 requirements.
		if (databaseType.equals("mySQL")){
			sql = "insert into gr_testing_temp"+  Integer.toString(projectId) +" (requirement_id, tree_level)" +
				" select to_requirement_id , 5 " +
				" from gr_traces t, gr_testing_temp" +  Integer.toString(projectId) + " temp" +
				" where t.from_requirement_id = temp.requirement_id" +
				" and temp.tree_level = 4 ";
			}
		else{
				sql = "insert into gr_testing_temp"+  Integer.toString(projectId) +" (\"requirement_id\", \"tree_level\")" +
				" select to_requirement_id , 5 \"tree_level\" " +
				" from gr_traces t, gr_testing_temp" +  Integer.toString(projectId) + " temp" +
				" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
				" and \"temp\".\"tree_level\" = 4 ";
		}
		prepStmt = con.prepareStatement(sql);
		prepStmt.execute();
		prepStmt.close();

		
		// lets get the sixth tree_level of Requirements in the tree. These are all Parent Requirements of Level 5 requirements.
		if (databaseType.equals("mySQL")){
			sql = "insert into gr_testing_temp"+  Integer.toString(projectId) +" (requirement_id, tree_level)" +
				" select to_requirement_id , 6 " +
				" from gr_traces t, gr_testing_temp" +  Integer.toString(projectId) + " temp" +
				" where t.from_requirement_id = temp.requirement_id" +
				" and temp.tree_level = 5 ";
			}
		else{
				sql = "insert into gr_testing_temp"+  Integer.toString(projectId) +" (\"requirement_id\", \"tree_level\")" +
				" select to_requirement_id , 6 \"tree_level\" " +
				" from gr_traces t, gr_testing_temp" +  Integer.toString(projectId) + " temp" +
				" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
				" and \"temp\".\"tree_level\" = 5 ";
		}
		prepStmt = con.prepareStatement(sql);
		prepStmt.execute();
		prepStmt.close();
		
		
		// lets get the seventh tree_level of Requirements in the tree. These are all Parent Requirements of Level 6 requirements.
		if (databaseType.equals("mySQL")){
			sql = "insert into gr_testing_temp"+  Integer.toString(projectId) +" (requirement_id, tree_level)" +
				" select to_requirement_id , 7 " +
				" from gr_traces t, gr_testing_temp" +  Integer.toString(projectId) + " temp" +
				" where t.from_requirement_id = temp.requirement_id" +
				" and temp.tree_level = 6 ";
			}
		else{
				sql = "insert into gr_testing_temp"+  Integer.toString(projectId) +" (\"requirement_id\", \"tree_level\")" +
				" select to_requirement_id , 7 \"tree_level\" " +
				" from gr_traces t, gr_testing_temp" +  Integer.toString(projectId) + " temp" +
				" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
				" and \"temp\".\"tree_level\" = 6 ";
		}
		prepStmt = con.prepareStatement(sql);
		prepStmt.execute();
		prepStmt.close();

		// lets get the eighth tree_level of Requirements in the tree. These are all Parent Requirements of Level 7 requirements.
		if (databaseType.equals("mySQL")){
			sql = "insert into gr_testing_temp"+  Integer.toString(projectId) +" (requirement_id, tree_level)" +
				" select to_requirement_id , 8 " +
				" from gr_traces t, gr_testing_temp" +  Integer.toString(projectId) + " temp" +
				" where t.from_requirement_id = temp.requirement_id" +
				" and temp.tree_level = 7 ";
			}
		else{
				sql = "insert into gr_testing_temp"+  Integer.toString(projectId) +" (\"requirement_id\", \"tree_level\")" +
				" select to_requirement_id , 8 \"tree_level\" " +
				" from gr_traces t, gr_testing_temp" +  Integer.toString(projectId) + " temp" +
				" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
				" and \"temp\".\"tree_level\" = 7 ";
		}
		prepStmt = con.prepareStatement(sql);
		prepStmt.execute();
		prepStmt.close();
		
		// the above process gives us a lot of duplicate requirements at the same tree_level. So
		// to make it more efficient, we are going to eliminate the duplicates.
		
		if (databaseType.equals("mySQL")){
			sql = "create table gr_testing_temp_no_duplicates"+  Integer.toString(projectId) +"  as " +
				" select distinct requirement_id, tree_level from gr_testing_temp"+  Integer.toString(projectId) +"  ";
			}
		else{
			sql = "create table gr_testing_temp_no_duplicates"+  Integer.toString(projectId) +"  as " +
				" select distinct \"requirement_id\", \"tree_level\" from gr_testing_temp"+  Integer.toString(projectId) +"  ";
		}
		prepStmt = con.prepareStatement(sql);
		prepStmt.execute();
		prepStmt.close();
	
		/*
		 
		 NOTE : We ran into a bug : If Project X traces to Project Y, and Project Y has no test results, ans the batchTestingStatus 
		 crunches Project Y after Project X, then all the requirements in Project Y are set to pending, over writing the
		 changes caused by Test Results in Project X. 
		 
		 Hence we are commenting this out. Should be fine, as all requirements are Pending by default.
		 
		// Since this job is not incremental, its CRITICAL that we look at all NON TR requirements
		// and set their testing status to 'Pending'. So that after this when we roll up the TestingStatus
		// values based on TR values, things will be in order.
		// lets get the TR requirement type id in this project.
		
		sql = "select id from gr_requirement_types " +
			" where project_id = ? " +
			" and name='Test Results'";
		prepStmt = con.prepareStatement(sql);
		prepStmt.setInt(1, projectId);
		ResultSet rs = prepStmt.executeQuery();
		int trRequirementTypeId = 0;
		while (rs.next()) {
			trRequirementTypeId = rs.getInt("id");
		}
		prepStmt.close();
		rs.close();
		
		sql = "update gr_requirements set testing_status = 'Pending'" +
			" where project_id = ? and requirement_type_id <> ? ";
		prepStmt = con.prepareStatement(sql);
		prepStmt.setInt(1, projectId);
		prepStmt.setInt(2, trRequirementTypeId);
		prepStmt.execute();
		prepStmt.close();
		*/
	
		
		// At this point we have the entire tree hierarchy of all the Test Results in this project
		// its parents / and higher ups.
		
		// Now lets process each tree_level one at a time.
		// first tree_level (the TR's) are special. Their Test Status is already set by the QA engineer.
		// we need this code to calculate the testing status of all the higher tree_levels based
		// on the value that has been set for TRS.
		
		
		// Now lets process all the remaining tree_levels.
		// its CRITICAL that we process them in the order of tree_levels.
		// also NOTE that tree_level 1 has already been processed.
		if (databaseType.equals("mySQL")){
			sql = "select requirement_id, tree_level from gr_testing_temp_no_duplicates"+  Integer.toString(projectId) +"  where tree_level > 1 order by tree_level asc";

			}
		else{
			sql = "select \"requirement_id\", \"tree_level\" from gr_testing_temp_no_duplicates"+  Integer.toString(projectId) +"  where \"tree_level\" > 1 order by \"tree_level\" asc";

		}
		prepStmt = con.prepareStatement(sql);
		ResultSet rs = prepStmt.executeQuery();
		while (rs.next()) {
			processTestStatusOfRequirement(con, rs.getInt("requirement_id"), rs.getInt("tree_level"), debug);
		}
		rs.close();
		prepStmt.close();
		
		
		// now that we don't need the gr_temp_testing_projectId and gr_testing_temp_no_duplicated_projectId to save db space.

		try {
			sql = "drop table gr_testing_temp" + Integer.toString(projectId);
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
		}
		catch (Exception e) {
		
		}
		
		
		// lets also drop the gr_testing_temp_no_duplicates
		try {
			sql = "drop table gr_testing_temp_no_duplicates" + Integer.toString(projectId);
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
		}
		catch (Exception e) {
			
		}
		
		
		
		
	}

	
	// This take a requirementId and figures out all the 
	// children and their testing status and based on the logic below
	// Here is the logic.
	// 1. No children, -- Not possible. because, of definition.
	// 2. At least 1 failed child - Parent is failed.
	// 3. if not #2, At least 1 pending child - Parent is pending
	// 4. if not # 3 ,then set parent to Passed.
	private static void processTestStatusOfRequirement(java.sql.Connection con, int requirementId, int tree_level, String debug)
	throws SQLException{
	
		// see if any failed children.
		String sql = "select r.full_tag, p.name from gr_requirements r, gr_projects p  where r.project_id = p.id and r.id = ? ";
		PreparedStatement prepStmt = con.prepareStatement(sql);
		prepStmt.setInt(1, requirementId);
		ResultSet rs = prepStmt.executeQuery();
		String reqFullTag = "";
		String projectName = "";
		while (rs.next()) {
			reqFullTag = rs.getString("full_tag");
			projectName = rs.getString("name");
		}
		rs.close();
		prepStmt.close();
		
		
		// see if any failed children.
		sql = "select count(*) \"failed_children\" from gr_traces t, gr_requirements r " +
			" where t.to_requirement_id = ? " + 
			" and t.from_requirement_id = r.id " + 
			" and r.testing_status = 'Fail'";
		prepStmt = con.prepareStatement(sql);
		prepStmt.setInt(1, requirementId);
		rs = prepStmt.executeQuery();
		int failedChildren = 0;
		while (rs.next()) {
			failedChildren = rs.getInt("failed_children");
		}
		rs.close();
		prepStmt.close();
		if (failedChildren > 0 ) {
			// set the parent to 'Fail' and return.
			sql = "update gr_requirements set testing_status = 'Fail' where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.execute();
			prepStmt.close();
			
			if (debug.equals("yes")){
				System.out.println("debug :  " + projectName + ":" + reqFullTag  + " has FAILED testing") ;
			}
			return;
		}

		// This means that the req has not failed. Lest see if any pending children.
		sql = "select count(*) \"pending_children\" from gr_traces t, gr_requirements r " +
			" where t.to_requirement_id = ? " + 
			" and t.from_requirement_id = r.id " + 
			" and r.testing_status = 'Pending'";
		prepStmt = con.prepareStatement(sql);
		prepStmt.setInt(1, requirementId);
		rs = prepStmt.executeQuery();
		int pendingChildren = 0;
		while (rs.next()) {
			pendingChildren = rs.getInt("pending_children");
		}
		rs.close();
		prepStmt.close();
		if (pendingChildren > 0 ) {
			// set the parent to 'Fail' and return.
			sql = "update gr_requirements set testing_status = 'Pending' where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.execute();
			prepStmt.close();
			

			if (debug.equals("yes")){
				System.out.println("debug :  " + projectName + ":" + reqFullTag  + " is PENDING testing");
			}
			return;
		}
		
		// at this point no pending / failed children. So lets set it to Pass.
		sql = "update gr_requirements set testing_status = 'Pass' where id = ? ";
		prepStmt = con.prepareStatement(sql);
		prepStmt.setInt(1, requirementId);
		prepStmt.execute();
		prepStmt.close();


		if (debug.equals("yes")){
			System.out.println("debug :  " + projectName + ":" + reqFullTag  + " has PASSED testing");
		}
		return;
	
	}
	
	

	private static void fillUpPercentComplete(java.sql.Connection con, int projectId,
			int percentCompleteDriverReqTypeId, String databaseType, String debug)
			throws SQLException{
			
			
			// lets drop the temp table we will use for data crunching. Since it may not exist, lets do it 
			// in  try / catch mode.
			// we could have used mySQL Temporary tables, but from debugging perspective these tables are better
			// as they will be available after the job is run.
			
			try {
				String sql = "drop table gr_percent_complete_temp ";
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.execute();
				prepStmt.close();
			}
			catch (Exception e) {
			
			}
			
			// lets also drop the gr_percent_complete_temp_no_duplicates
			try {
				String sql = "drop table gr_percent_complete_temp_no_duplicates ";
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.execute();
				prepStmt.close();
			}
			catch (Exception e) {
				
			}
			
			
			// lets build the hierarchy tree of all requirements above Driving Req Type
			// processing 1st tree_level (the TR tree_level) of the trace tree. We will continuously process a higher tree_level
			// for eight more tree_levels.
			// lets fill the gr_percent_complete_temp table with all the TR's 
			String sql = "create table gr_percent_complete_temp as " +
			" select r.id \"requirement_id\", 1  \"tree_level\" " +
			" from gr_requirements r " +
			" where  r.requirement_type_id = ? " +
			" and r.project_id =   " + projectId ;

			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, percentCompleteDriverReqTypeId);
			
			prepStmt.execute();
			
			

			// lets get the second tree_level of Requirements in the tree. These are all Parent Requirements of Level 1 requirements.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_percent_complete_temp(requirement_id, tree_level)" +
					" select to_requirement_id , 2  \"tree_level\"" +
					" from gr_traces t, gr_percent_complete_temp temp" +
					" where t.from_requirement_id = temp.requirement_id" +
					" and temp.tree_level = 1 ";
				}
			else{
					sql = "insert into gr_percent_complete_temp(\"requirement_id\", \"tree_level\")" +
					" select to_requirement_id , 2 \"tree_level\" " +
					" from gr_traces t, gr_percent_complete_temp \"temp\"" +
					" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
					" and \"temp\".\"tree_level\" = 1 ";
			}
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			// lets get the third tree_level of Requirements in the tree. These are all Parent Requirements of Level 2 requirements.
			
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_percent_complete_temp(requirement_id, tree_level)" +
					" select to_requirement_id , 3 " +
					" from gr_traces t, gr_percent_complete_temp temp" +
					" where t.from_requirement_id = temp.requirement_id" +
					" and temp.tree_level = 2 ";
				}
			else{
					sql = "insert into gr_percent_complete_temp(\"requirement_id\", \"tree_level\")" +
					" select to_requirement_id , 3 \"tree_level\" " +
					" from gr_traces t, gr_percent_complete_temp \"temp\"" +
					" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
					" and \"temp\".\"tree_level\" = 2 ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			
			// lets get the fourth tree_level of Requirements in the tree. These are all Parent Requirements of Level 3 requirements.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_percent_complete_temp(requirement_id, tree_level)" +
					" select to_requirement_id , 4 " +
					" from gr_traces t, gr_percent_complete_temp temp" +
					" where t.from_requirement_id = temp.requirement_id" +
					" and temp.tree_level = 3 ";
				}
			else{
					sql = "insert into gr_percent_complete_temp(\"requirement_id\", \"tree_level\")" +
					" select to_requirement_id , 4 \"tree_level\" " +
					" from gr_traces t, gr_percent_complete_temp \"temp\"" +
					" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
					" and \"temp\".\"tree_level\" = 3 ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			
			// lets get the fifth tree_level of Requirements in the tree. These are all Parent Requirements of Level 4 requirements.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_percent_complete_temp(requirement_id, tree_level)" +
					" select to_requirement_id , 5 " +
					" from gr_traces t, gr_percent_complete_temp temp" +
					" where t.from_requirement_id = temp.requirement_id" +
					" and temp.tree_level = 4 ";
				}
			else{
					sql = "insert into gr_percent_complete_temp(\"requirement_id\", \"tree_level\")" +
					" select to_requirement_id , 5 \"tree_level\" " +
					" from gr_traces t, gr_percent_complete_temp \"temp\"" +
					" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
					" and \"temp\".\"tree_level\" = 4 ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();

			
			// lets get the sixth tree_level of Requirements in the tree. These are all Parent Requirements of Level 5 requirements.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_percent_complete_temp(requirement_id, tree_level)" +
					" select to_requirement_id , 6 " +
					" from gr_traces t, gr_percent_complete_temp temp" +
					" where t.from_requirement_id = temp.requirement_id" +
					" and temp.tree_level = 5 ";
				}
			else{
					sql = "insert into gr_percent_complete_temp(\"requirement_id\", \"tree_level\")" +
					" select to_requirement_id , 6 \"tree_level\" " +
					" from gr_traces t, gr_percent_complete_temp \"temp\"" +
					" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
					" and \"temp\".\"tree_level\" = 5 ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			
			// lets get the seventh tree_level of Requirements in the tree. These are all Parent Requirements of Level 6 requirements.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_percent_complete_temp(requirement_id, tree_level)" +
					" select to_requirement_id , 7 " +
					" from gr_traces t, gr_percent_complete_temp temp" +
					" where t.from_requirement_id = temp.requirement_id" +
					" and temp.tree_level = 6 ";
				}
			else{
					sql = "insert into gr_percent_complete_temp(\"requirement_id\", \"tree_level\")" +
					" select to_requirement_id , 7 \"tree_level\" " +
					" from gr_traces t, gr_percent_complete_temp \"temp\"" +
					" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
					" and \"temp\".\"tree_level\" = 6 ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();

			// lets get the eighth tree_level of Requirements in the tree. These are all Parent Requirements of Level 7 requirements.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_percent_complete_temp(requirement_id, tree_level)" +
					" select to_requirement_id , 8 " +
					" from gr_traces t, gr_percent_complete_temp temp" +
					" where t.from_requirement_id = temp.requirement_id" +
					" and temp.tree_level = 7 ";
				}
			else{
					sql = "insert into gr_percent_complete_temp(\"requirement_id\", \"tree_level\")" +
					" select to_requirement_id , 8 \"tree_level\" " +
					" from gr_traces t, gr_percent_complete_temp \"temp\"" +
					" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
					" and \"temp\".\"tree_level\" = 7 ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			// the above process gives us a lot of duplicate requirements at the same tree_level. So
			// to make it more efficient, we are going to eliminate the duplicates.
			
			if (databaseType.equals("mySQL")){
				sql = "create table gr_percent_complete_temp_no_duplicates as " +
					" select distinct requirement_id, tree_level from gr_percent_complete_temp ";
				}
			else{
				sql = "create table gr_percent_complete_temp_no_duplicates as " +
					" select distinct \"requirement_id\", \"tree_level\" from gr_percent_complete_temp ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
		
			// At this point we have the entire tree hierarchy of all the Test Results in this project
			// its parents / and higher ups.
			
			// Now lets process each tree_level one at a time.
			// first tree_level (the TR's) are special. Their Test Status is already set by the QA engineer.
			// we need this code to calculate the testing status of all the higher tree_levels based
			// on the value that has been set for TRS.
			
			
			// Now lets process all the remaining tree_levels.
			// its CRITICAL that we process them in the order of tree_levels.
			// also NOTE that tree_level 1 has already been processed.
			if (databaseType.equals("mySQL")){
				sql = "select requirement_id, tree_level from gr_percent_complete_temp_no_duplicates where tree_level > 1 order by tree_level asc";

				}
			else{
				sql = "select \"requirement_id\", \"tree_level\" from gr_percent_complete_temp_no_duplicates where \"tree_level\" > 1 order by \"tree_level\" asc";

			}
			prepStmt = con.prepareStatement(sql);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				processPercentCompleteOfRequirement(con, rs.getInt("requirement_id"), rs.getInt("tree_level"), debug);
			}
			rs.close();
			prepStmt.close();
			
		}

		
		// This take a requirementId and figures out all the 
		// children and their percent complete  status and based on the logic below
		// Here is the logic.
		// 1. No children, -- Not possible. because, of definition.
		// 2. Find the average of all the children's percent complete and that is the percent complete for this req.
		private static void processPercentCompleteOfRequirement(java.sql.Connection con, int requirementId, int tree_level, String debug)
		throws SQLException{
		
			
			
			// see if any failed children.
			String sql = "select toReq.full_tag  , round(avg(fromReq.pct_complete)) 'avg_pct_complete' " +
				" from gr_traces t, gr_requirements fromReq, gr_requirements toReq " +
				" where t.to_requirement_id = ? " +
				" and t.from_requirement_id = fromReq.id " +
				" and t.to_requirement_id = toReq.id ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			ResultSet rs = prepStmt.executeQuery();
			String reqFullTag = "";
			int avgPctComplete = 0;
			while (rs.next()) {
				reqFullTag = rs.getString("full_tag");
				avgPctComplete = rs.getInt("avg_pct_complete");
			}
			rs.close();
			prepStmt.close();
			
					
			// at this point no pending / failed children. So lets set it to Pass.
			sql = "update gr_requirements set pct_complete  = ? where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, avgPctComplete);
			prepStmt.setInt(2, requirementId);
			prepStmt.execute();
			prepStmt.close();


			
			return;
		
		}

	// for every project, it looks into the gr_requirements table to 
	// crunch the data and load into gr_project_metrics table.
	private static void fillUpProjectMetrics(java.sql.Connection con, int projectId, String dataLoadDt, String databaseType ) throws SQLException{

		
		// lets get the distinct requirement types in this release tree.
		String sql = "";
		sql = "select distinct rt.short_name \"requirement_type_short_name\", rt.id, rt.display_sequence" + 
			" from gr_requirement_types rt " +
			" where rt.project_id = ? " +
			" order by rt.display_sequence"; 
		
		PreparedStatement prepStmt = con.prepareStatement(sql);
		prepStmt.setInt(1, projectId);
		ResultSet rs = prepStmt.executeQuery();
		while(rs.next()) {
			String requirementTypeShortName = rs.getString("requirement_type_short_name");
			int requirementTypeId = rs.getInt("id");
			
			// for each project, requirement type combinations, lets get some metrics.
			
			// Total number of requirements.
			int numOfRequirements  = 0;
			sql = "select count(*) \"num_of_requirements\" " +
				" from  gr_requirements r" +
				" where r.project_id = ? " +
				" and  r.requirement_type_id = ? " +
				" and r.deleted = 0 ";
			PreparedStatement prepStmt2 = con.prepareStatement(sql);
			prepStmt2.setInt(1,projectId);
			prepStmt2.setInt(2,requirementTypeId);
			ResultSet rs2 = prepStmt2.executeQuery();
			while (rs2.next()) {
				numOfRequirements = rs2.getInt("num_of_requirements");
			}
			prepStmt2.close();
			rs2.close();
			
			
			// Total number of  requirements in that are in testing_status = Pending
			int numOfTestPendingRequirements  = 0;
			sql = "select count(*) \"num_of_test_pending_reqs\" " +
			" from  gr_requirements r" +
			" where r.project_id = ? " +
			" and  r.requirement_type_id = ? " +
			" and r.deleted = 0 " +
			" and r.testing_status = 'Pending'";			
			prepStmt2 = con.prepareStatement(sql);
			prepStmt2.setInt(1,projectId);
			prepStmt2.setInt(2,requirementTypeId);
			rs2 = prepStmt2.executeQuery();
			while (rs2.next()) {
				numOfTestPendingRequirements = rs2.getInt("num_of_test_pending_reqs");
			}
			prepStmt2.close();
			rs2.close();
			
			// Total number of  requirements in that are in testing_status = Pass
			int numOfTestPassRequirements  = 0;
			sql = "select count(*) \"num_of_test_pass_reqs\" " +
			" from  gr_requirements r" +
			" where r.project_id = ? " +
			" and  r.requirement_type_id = ? " +
			" and r.deleted = 0 " +
			" and r.testing_status = 'Pass'";			
			prepStmt2 = con.prepareStatement(sql);
			prepStmt2.setInt(1,projectId);
			prepStmt2.setInt(2,requirementTypeId);
			rs2 = prepStmt2.executeQuery();
			while (rs2.next()) {
				numOfTestPassRequirements = rs2.getInt("num_of_test_pass_reqs");
			}
			prepStmt2.close();
			rs2.close();
			
			
			// Total number of  requirements in that are in testing_status = Fail
			int numOfTestFailRequirements  = 0;
			sql = "select count(*) \"num_of_test_fail_reqs\" " +
			" from  gr_requirements r" +
			" where r.project_id = ? " +
			" and  r.requirement_type_id = ? " +
			" and r.deleted = 0 " +
			" and r.testing_status = 'Fail'";			
			prepStmt2 = con.prepareStatement(sql);
			prepStmt2.setInt(1,projectId);
			prepStmt2.setInt(2,requirementTypeId);
			rs2 = prepStmt2.executeQuery();
			while (rs2.next()) {
				numOfTestFailRequirements = rs2.getInt("num_of_test_fail_reqs");
			}
			prepStmt2.close();
			rs2.close();
			
			
			
			// Total number of  requirements in draft status.
			int numOfDraftRequirements  = 0;
			sql = "select count(*) \"num_of_draft_reqs\" " +
			" from  gr_requirements r" +
			" where r.project_id = ? " +
			" and  r.requirement_type_id = ? " +
			" and r.deleted = 0 " +
			" and r.status = 'Draft'";			
			prepStmt2 = con.prepareStatement(sql);
			prepStmt2.setInt(1,projectId);
			prepStmt2.setInt(2,requirementTypeId);
			rs2 = prepStmt2.executeQuery();
			while (rs2.next()) {
				numOfDraftRequirements = rs2.getInt("num_of_draft_reqs");
			}
			prepStmt2.close();
			rs2.close();
			
			
			
			// Total number of  requirements in InApprovalWorkflow status.
			int numOfInApprovalWorkflowRequirements  = 0;
			sql = "select count(*) \"num_of_in_workflow_reqs\" " +
			" from  gr_requirements r" +
			" where r.project_id = ? " +
			" and  r.requirement_type_id = ? " +
			" and r.deleted = 0 " +
			" and r.status = 'In Approval WorkFlow'";			
			prepStmt2 = con.prepareStatement(sql);
			prepStmt2.setInt(1,projectId);
			prepStmt2.setInt(2,requirementTypeId);
			rs2 = prepStmt2.executeQuery();
			while (rs2.next()) {
				numOfInApprovalWorkflowRequirements = rs2.getInt("num_of_in_workflow_reqs");
			}
			prepStmt2.close();
			rs2.close();
			
			

			
			// Total number of  requirements in rejected status.
			int numOfRejectedRequirements  = 0;
			sql = "select count(*) \"num_of_rejected_reqs\" " +
				" from  gr_requirements r" +
				" where r.project_id = ? " +
				" and  r.requirement_type_id = ? " +
				" and r.deleted = 0 " +
				" and r.status = 'Rejected'";
			prepStmt2 = con.prepareStatement(sql);
			prepStmt2.setInt(1,projectId);
			prepStmt2.setInt(2,requirementTypeId);
			rs2 = prepStmt2.executeQuery();
			while (rs2.next()) {
				numOfRejectedRequirements = rs2.getInt("num_of_rejected_reqs");
			}
			prepStmt2.close();
			rs2.close();
			
			
			
			// Total number of  requirements in approved status.
			int numOfApprovedRequirements  = 0;
			sql = "select count(*) \"num_of_approved_reqs\" " +
				" from  gr_requirements r" +
				" where r.project_id = ? " +
				" and  r.requirement_type_id = ? " +
				" and r.deleted = 0 " +
				" and r.status = 'Approved'";
			prepStmt2 = con.prepareStatement(sql);
			prepStmt2.setInt(1,projectId);
			prepStmt2.setInt(2,requirementTypeId);
			rs2 = prepStmt2.executeQuery();
			while (rs2.next()) {
				numOfApprovedRequirements = rs2.getInt("num_of_approved_reqs");
			}
			prepStmt2.close();
			rs2.close();
			
		
			// Total number of  requirements in dangling status.
			int numOfDanglingRequirements  = 0;
			
			if (databaseType.equals("mySQL")){
				sql = "select count(*) \"num_of_dangling_reqs\" " +
				" from  gr_requirements r, gr_requirement_types rt" +
				" where r.project_id = ? " +
				" and r.requirement_type_id = rt.id " +
				" and  r.requirement_type_id = ? " +
				" and r.deleted = 0 " +
				" and (r.trace_from is null or r.trace_from = '')" +
				" and rt.can_be_dangling = 1";
			}
			else{
				sql = "select count(*) \"num_of_dangling_reqs\" " +
				" from  gr_requirements r, gr_requirement_types rt" +
				" where r.project_id = ? " +
				" and r.requirement_type_id = rt.id " +
				" and  r.requirement_type_id = ? " +
				" and r.deleted = 0 " +
				" and (upper(to_char(r.trace_from)) is null or upper(to_char(r.trace_from)) = '')" +
				" and rt.can_be_dangling = 1";
			}
			
			prepStmt2 = con.prepareStatement(sql);
			prepStmt2.setInt(1,projectId);
			prepStmt2.setInt(2,requirementTypeId);
			rs2 = prepStmt2.executeQuery();
			while (rs2.next()) {
				numOfDanglingRequirements = rs2.getInt("num_of_dangling_reqs");
			}
			prepStmt2.close();
			rs2.close();
			
			// Total number of  requirements in orphan status.
			int numOfOrphanRequirements  = 0;
			
			if (databaseType.equals("mySQL")){
				sql = "select count(*) \"num_of_orphan_reqs\" " +
				" from  gr_requirements r, gr_requirement_types rt" +
				" where r.project_id = ? " +
				" and r.requirement_type_id = rt.id " +
				" and  r.requirement_type_id = ? " +
				" and r.deleted = 0 " +
				" and (r.trace_to is null or r.trace_to = '')" +
				" and rt.can_be_orphan = 1";
			}
			else{
				sql = "select count(*) \"num_of_orphan_reqs\" " +
				" from  gr_requirements r, gr_requirement_types rt" +
				" where r.project_id = ? " +
				" and r.requirement_type_id = rt.id " +
				" and  r.requirement_type_id = ? " +
				" and r.deleted = 0 " +
				" and (upper(to_char(r.trace_to)) is null or upper(to_char(r.trace_to)) = '')" +
				" and rt.can_be_orphan = 1";
			}
			prepStmt2 = con.prepareStatement(sql);
			prepStmt2.setInt(1,projectId);
			prepStmt2.setInt(2,requirementTypeId);
			rs2 = prepStmt2.executeQuery();
			while (rs2.next()) {
				numOfOrphanRequirements = rs2.getInt("num_of_orphan_reqs");
			}
			prepStmt2.close();
			rs2.close();
			
			
			

			// Total number of  requirements in SuspectUpstream status.
			int numOfSuspectUpstreamRequirements  = 0;
			
			if (databaseType.equals("mySQL")){
				sql = "select count(*) \"num_of_suspect_upstream_reqs\" " +
				" from  gr_requirements r" +
				" where r.project_id = ? " +
				" and  r.requirement_type_id = ? " +
				" and r.deleted = 0 " +
				" and upper(r.trace_to) like '%(S)%'" ;
			}
			else{
				sql = "select count(*) \"num_of_suspect_upstream_reqs\" " +
				" from  gr_requirements r" +
				" where r.project_id = ? " +
				" and  r.requirement_type_id = ? " +
				" and r.deleted = 0 " +
				" and upper(to_char(r.trace_to)) like '%(S)%'" ;
			}
			prepStmt2 = con.prepareStatement(sql);
			prepStmt2.setInt(1,projectId);
			prepStmt2.setInt(2,requirementTypeId);
			rs2 = prepStmt2.executeQuery();
			while (rs2.next()) {
				numOfSuspectUpstreamRequirements = rs2.getInt("num_of_suspect_upstream_reqs");
			}
			prepStmt2.close();
			rs2.close();

			// Total number of  requirements in SuspectDownstream status.
			int numOfSuspectDownstreamRequirements  = 0;
			if (databaseType.equals("mySQL")){
				sql = "select count(*) \"num_of_suspect_downstream_reqs\" " +
				" from  gr_requirements r" +
				" where r.project_id = ? " +
				" and  r.requirement_type_id = ? " +
				" and r.deleted = 0 " +
				" and upper(r.trace_from) like '%(S)%'" ;
			}
			else{
				sql = "select count(*) \"num_of_suspect_downstream_reqs\" " +
				" from  gr_requirements r" +
				" where r.project_id = ? " +
				" and  r.requirement_type_id = ? " +
				" and r.deleted = 0 " +
				" and upper(to_char(r.trace_from)) like '%(S)%' " ;
			}
			
			prepStmt2 = con.prepareStatement(sql);
			prepStmt2.setInt(1,projectId);
			prepStmt2.setInt(2,requirementTypeId);
			rs2 = prepStmt2.executeQuery();
			while (rs2.next()) {
				numOfSuspectDownstreamRequirements = rs2.getInt("num_of_suspect_downstream_reqs");
			}
			prepStmt2.close();
			rs2.close();			
			
			// Total number of  requirements in Completed status.
			int numOfCompletedRequirements  = 0;
			
			sql = "select count(*) \"num_of_completed_reqs\" " +
				" from  gr_requirements r" +
				" where r.project_id = ? " +
				" and  r.requirement_type_id = ? " +
				" and r.deleted = 0 " +
				" and r.pct_complete = 100 " ;
			prepStmt2 = con.prepareStatement(sql);
			prepStmt2.setInt(1,projectId);
			prepStmt2.setInt(2,requirementTypeId);
			rs2 = prepStmt2.executeQuery();
			while (rs2.next()) {
				numOfCompletedRequirements = rs2.getInt("num_of_completed_reqs");
			}
			prepStmt2.close();
			rs2.close();	
			
			
			
			// Total number of  requirements in InComplet status.
			int numOfIncompleteRequirements  = 0;
			sql = "select count(*) \"num_of_incomplete_reqs\" " +
				" from  gr_requirements r" +
				" where r.project_id = ? " +
				" and  r.requirement_type_id = ? " +
				" and r.deleted = 0 " +
				" and r.pct_complete <> 100 " ;
			prepStmt2 = con.prepareStatement(sql);
			prepStmt2.setInt(1,projectId);
			prepStmt2.setInt(2,requirementTypeId);
			rs2 = prepStmt2.executeQuery();
			while (rs2.next()) {
				numOfIncompleteRequirements = rs2.getInt("num_of_incomplete_reqs");
			}
			prepStmt2.close();
			rs2.close();				
			

			
			// now that we have all the values we need, lets insert them into the trend table.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_project_metrics (project_id, " +
				" requirement_type_short_name, data_load_dt, num_of_requirements, " +
				" num_of_test_pending_reqs, num_of_test_pass_reqs, num_of_test_fail_reqs, " +
				" num_of_draft_reqs, num_of_in_workflow_reqs, " +
				" num_of_rejected_reqs, num_of_approved_reqs, " +
				" num_of_dangling_reqs, num_of_orphan_reqs, " +
				" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
				" num_of_completed_reqs, num_of_incomplete_reqs) " + 
				" values (" +
				"?," +
				"?,str_to_date('"+ dataLoadDt +"','%d %m %y %h:%i:%s'),?," +
				"?,?,?," +
				"?,?," +
				"?,?," +
				"?,?," +
				"?,?," +
				"?,?)";
			}
			else{
				sql = "insert into gr_project_metrics (project_id, " +
				" requirement_type_short_name, data_load_dt, num_of_requirements, " +
				" num_of_test_pending_reqs, num_of_test_pass_reqs, num_of_test_fail_reqs, " +
				" num_of_draft_reqs, num_of_in_workflow_reqs, " +
				" num_of_rejected_reqs, num_of_approved_reqs, " +
				" num_of_dangling_reqs, num_of_orphan_reqs, " +
				" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
				" num_of_completed_reqs, num_of_incomplete_reqs) " + 
				" values (" +
				"?," +
				"?,to_date('"+ dataLoadDt +"','DD MON YYYY HH MI SS'),?," +
				"?,?,?," +
				"?,?," +
				"?,?," +
				"?,?," +
				"?,?," +
				"?,?)";
			}
			
			prepStmt2 = con.prepareStatement(sql);
			
			prepStmt2.setInt(1,projectId);
			
			prepStmt2.setString(2, requirementTypeShortName);
			prepStmt2.setInt(3,numOfRequirements);
			
			prepStmt2.setInt(4,numOfTestPendingRequirements);
			prepStmt2.setInt(5,numOfTestPassRequirements);
			prepStmt2.setInt(6,numOfTestFailRequirements);
			
			
			prepStmt2.setInt(7,numOfDraftRequirements);
			prepStmt2.setInt(8,numOfInApprovalWorkflowRequirements);
			
			prepStmt2.setInt(9,numOfRejectedRequirements);
			prepStmt2.setInt(10,numOfApprovedRequirements);
			
			prepStmt2.setInt(11,numOfDanglingRequirements);
			prepStmt2.setInt(12,numOfOrphanRequirements);
			
			prepStmt2.setInt(13,numOfSuspectUpstreamRequirements);
			prepStmt2.setInt(14,numOfSuspectDownstreamRequirements);
			
			prepStmt2.setInt(15,numOfCompletedRequirements);
			prepStmt2.setInt(16,numOfIncompleteRequirements);
			
			prepStmt2.execute();
			prepStmt2.close();
							
		
		}

	    prepStmt.close();
		rs.close();
	}
	
	private static void fillUpUserMetrics(java.sql.Connection con, int projectId, String dataLoadDt, String databaseType) throws SQLException{

		
		// lets remove all old data from gr_user_metrics table.
		// if we want to retain trending, we just need to turn this off.
		String sql = "delete from gr_user_metrics " + 
			" where project_id = ? "; 
		
		PreparedStatement prepStmt = con.prepareStatement(sql);
		prepStmt.setInt(1, projectId);
		prepStmt.execute();
		prepStmt.close();
		
	
		// NOTE : There is a much smarter way to write this . i.e instead of gettign a user / role combo and
		// crunching numbers ,you can write a series of group by queries, and use temp table to store
		// and re-combine the data. So, if you ever run into a situation where the cron is taking more 
		// than 30 mins to run, the consider rewriting this method. will give most bang for the buck.
		
		// lets get the list of owners of requirements in this project and for every user, we will crunch the numbers.
		sql = "select distinct r.owner " + 
			" from gr_requirements r " +
			" where r.project_id = ? ";
		
		PreparedStatement userPrepStmt = con.prepareStatement(sql);
		userPrepStmt.setInt(1, projectId);
		ResultSet userRS = userPrepStmt.executeQuery();
		while (userRS.next()) {
			String owner = userRS.getString("owner");
			
			// for each one of these users / Requirement Type combinations, lets get
			// the metrics.
			
			
			// lets get the distinct requirement types in this project.
			sql = "select distinct rt.short_name \"requirement_type_short_name\", rt.id" + 
				" from gr_requirement_types rt " +
				" where rt.project_id = ? "; 
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			ResultSet rs = prepStmt.executeQuery();
			while(rs.next()) {
				String requirementTypeShortName = rs.getString("requirement_type_short_name");
				int requirementTypeId = rs.getInt("id");
				
				// for each project, requirement type combinations, lets get some metrics.
				
				// Total number of requirements.
				int numOfRequirements  = 0;
				sql = "select count(*) \"num_of_requirements\" " +
					" from  gr_requirements r" +
					" where r.project_id = ? " +
					" and  r.requirement_type_id = ? " +
					" and r.owner = ? " + 
					" and r.deleted = 0 ";
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,projectId);
				prepStmt2.setInt(2,requirementTypeId);
				prepStmt2.setString(3, owner);
				ResultSet rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfRequirements = rs2.getInt("num_of_requirements");
				}
				prepStmt2.close();
				rs2.close();
				

				// Total number of  requirements with testing_status = Pending
				int numOfTestPendingRequirements  = 0;
				sql = "select count(*) \"num_of_test_pending_reqs\" " +
				" from  gr_requirements r" +
				" where r.project_id = ? " +
				" and  r.requirement_type_id = ? " +
				" and r.deleted = 0 " +
				" and r.owner = ? " + 
				" and r.testing_status = 'Pending'";			
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,projectId);
				prepStmt2.setInt(2,requirementTypeId);
				prepStmt2.setString(3, owner);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfTestPendingRequirements = rs2.getInt("num_of_test_pending_reqs");
				}
				prepStmt2.close();
				rs2.close();

				
				
				// Total number of  requirements with testing_status = Pass
				int numOfTestPassRequirements  = 0;
				sql = "select count(*) \"num_of_test_pass_reqs\" " +
				" from  gr_requirements r" +
				" where r.project_id = ? " +
				" and  r.requirement_type_id = ? " +
				" and r.deleted = 0 " +
				" and r.owner = ? " + 
				" and r.testing_status = 'Pass'";			
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,projectId);
				prepStmt2.setInt(2,requirementTypeId);
				prepStmt2.setString(3, owner);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfTestPassRequirements = rs2.getInt("num_of_test_pass_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				
				
				// Total number of  requirements with testing_status = Fail
				int numOfTestFailRequirements  = 0;
				sql = "select count(*) \"num_of_test_fail_reqs\" " +
				" from  gr_requirements r" +
				" where r.project_id = ? " +
				" and  r.requirement_type_id = ? " +
				" and r.deleted = 0 " +
				" and r.owner = ? " + 
				" and r.testing_status = 'Fail'";			
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,projectId);
				prepStmt2.setInt(2,requirementTypeId);
				prepStmt2.setString(3, owner);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfTestFailRequirements = rs2.getInt("num_of_test_fail_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				// Total number of  requirements in draft status.
				int numOfDraftRequirements  = 0;
				sql = "select count(*) \"num_of_draft_reqs\" " +
				" from  gr_requirements r" +
				" where r.project_id = ? " +
				" and  r.requirement_type_id = ? " +
				" and r.deleted = 0 " +
				" and r.owner = ? " + 
				" and r.status = 'Draft'";			
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,projectId);
				prepStmt2.setInt(2,requirementTypeId);
				prepStmt2.setString(3, owner);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfDraftRequirements = rs2.getInt("num_of_draft_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				
				
				// Total number of  requirements in InApprovalWorkflow status.
				int numOfInApprovalWorkflowRequirements  = 0;
				sql = "select count(*) \"num_of_in_workflow_reqs\" " +
				" from  gr_requirements r" +
				" where r.project_id = ? " +
				" and  r.requirement_type_id = ? " +
				" and r.deleted = 0 " +
				" and r.owner = ? " + 
				" and r.status = 'In Approval WorkFlow'";			
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,projectId);
				prepStmt2.setInt(2,requirementTypeId);
				prepStmt2.setString(3, owner);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfInApprovalWorkflowRequirements = rs2.getInt("num_of_in_workflow_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				
	
				
				// Total number of  requirements in rejected status.
				int numOfRejectedRequirements  = 0;
				sql = "select count(*) \"num_of_rejected_reqs\" " +
					" from  gr_requirements r" +
					" where r.project_id = ? " +
					" and  r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.owner = ? " + 					
					" and r.status = 'Rejected'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,projectId);
				prepStmt2.setInt(2,requirementTypeId);
				prepStmt2.setString(3, owner);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfRejectedRequirements = rs2.getInt("num_of_rejected_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				
				
				// Total number of  requirements in approved status.
				int numOfApprovedRequirements  = 0;
				sql = "select count(*) \"num_of_approved_reqs\" " +
					" from  gr_requirements r" +
					" where r.project_id = ? " +
					" and  r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.owner = ? " + 			
					" and r.status = 'Approved'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,projectId);
				prepStmt2.setInt(2,requirementTypeId);
				prepStmt2.setString(3, owner);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfApprovedRequirements = rs2.getInt("num_of_approved_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
			
				// Total number of  requirements in dangling status.
				int numOfDanglingRequirements  = 0;
				
				
				if (databaseType.equals("mySQL")){
					sql = "select count(*) \"num_of_dangling_reqs\" " +
					" from  gr_requirements r, gr_requirement_types rt" +
					" where r.project_id = ? " +
					" and r.requirement_type_id = rt.id " +
					" and  r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.owner = ? " + 			
					" and (r.trace_from is null or r.trace_from = '')" +
					" and rt.can_be_dangling = 1";
				}
				else{
					sql = "select count(*) \"num_of_dangling_reqs\" " +
					" from  gr_requirements r, gr_requirement_types rt" +
					" where r.project_id = ? " +
					" and r.requirement_type_id = rt.id " +
					" and  r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.owner = ? " + 			
					" and (upper(to_char(r.trace_from)) is null or upper(to_char(r.trace_from)) = '' )" +
					" and rt.can_be_dangling = 1";
				}
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,projectId);
				prepStmt2.setInt(2,requirementTypeId);
				prepStmt2.setString(3, owner);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfDanglingRequirements = rs2.getInt("num_of_dangling_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				// Total number of  requirements in orphan status.
				int numOfOrphanRequirements  = 0;
				
				if (databaseType.equals("mySQL")){
					sql = "select count(*) \"num_of_orphan_reqs\" " +
					" from  gr_requirements r, gr_requirement_types rt " +
					" where r.project_id = ? " +
					" and r.requirement_type_id = rt.id " +
					" and  r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.owner = ? " + 		
					" and (r.trace_to is null or r.trace_to = '')" +
					" and rt.can_be_orphan = 1 ";
				}
				else{
					sql = "select count(*) \"num_of_orphan_reqs\" " +
					" from  gr_requirements r, gr_requirement_types rt " +
					" where r.project_id = ? " +
					" and r.requirement_type_id = rt.id " +
					" and  r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.owner = ? " + 		
					" and (upper(to_char(r.trace_to)) is null or upper(to_char(r.trace_to)) = '' )" +
					" and rt.can_be_orphan = 1 ";
					
				}
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,projectId);
				prepStmt2.setInt(2,requirementTypeId);
				prepStmt2.setString(3, owner);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfOrphanRequirements = rs2.getInt("num_of_orphan_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				
				
	
				// Total number of  requirements in SuspectUpstream status.
				int numOfSuspectUpstreamRequirements  = 0;
				
				if (databaseType.equals("mySQL")){
					sql = "select count(*) \"num_of_suspect_upstream_reqs\" " +
					" from  gr_requirements r" +
					" where r.project_id = ? " +
					" and  r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.owner = ? " + 		
					" and upper(r.trace_to) like '%(S)%'" ;
				}
				else{
					sql = "select count(*) \"num_of_suspect_upstream_reqs\" " +
					" from  gr_requirements r" +
					" where r.project_id = ? " +
					" and  r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.owner = ? " + 		
					" and upper(to_char(r.trace_to)) like '%(S)%'" ;
					
				}
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,projectId);
				prepStmt2.setInt(2,requirementTypeId);
				prepStmt2.setString(3, owner);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfSuspectUpstreamRequirements = rs2.getInt("num_of_suspect_upstream_reqs");
				}
				prepStmt2.close();
				rs2.close();
	
				// Total number of  requirements in SuspectDownstream status.
				int numOfSuspectDownstreamRequirements  = 0;
				
				
				if (databaseType.equals("mySQL")){
					sql = "select count(*) \"num_of_suspect_downstream_reqs\" " +
					" from  gr_requirements r" +
					" where r.project_id = ? " +
					" and  r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.owner = ? " + 		
					" and upper(r.trace_from) like '%(S)%'" ;
				}
				else{
					sql = "select count(*) \"num_of_suspect_downstream_reqs\" " +
					" from  gr_requirements r" +
					" where r.project_id = ? " +
					" and  r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.owner = ? " + 		
					" and upper(to_char(r.trace_from)) like '%(S)%'" ;
					
				}
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,projectId);
				prepStmt2.setInt(2,requirementTypeId);
				prepStmt2.setString(3, owner);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfSuspectDownstreamRequirements = rs2.getInt("num_of_suspect_downstream_reqs");
				}
				prepStmt2.close();
				rs2.close();			
				
				// Total number of  requirements in Completed status.
				int numOfCompletedRequirements  = 0;
				sql = "select count(*) \"num_of_completed_reqs\" " +
					" from  gr_requirements r" +
					" where r.project_id = ? " +
					" and  r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.owner = ? " + 		
					" and r.pct_complete = 100 " ;
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,projectId);
				prepStmt2.setInt(2,requirementTypeId);
				prepStmt2.setString(3, owner);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfCompletedRequirements = rs2.getInt("num_of_completed_reqs");
				}
				prepStmt2.close();
				rs2.close();	
				
				
				
				// Total number of  requirements in InComplet status.
				int numOfIncompleteRequirements  = 0;
				sql = "select count(*) \"num_of_incomplete_reqs\" " +
					" from  gr_requirements r" +
					" where r.project_id = ? " +
					" and  r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.owner = ? " + 		
					" and r.pct_complete <> 100 " ;
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,projectId);
				prepStmt2.setInt(2,requirementTypeId);
				prepStmt2.setString(3, owner);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfIncompleteRequirements = rs2.getInt("num_of_incomplete_reqs");
				}
				prepStmt2.close();
				rs2.close();				
				
	
				// now that we have all the values we need, lets insert them into the trend table.
				
				
				if (databaseType.equals("mySQL")){
					sql = "insert into gr_user_metrics (project_id, owner, " +
					" requirement_type_short_name, data_load_dt, num_of_requirements, " +
					" num_of_test_pending_reqs, num_of_test_pass_reqs, num_of_test_fail_reqs, " +
					" num_of_draft_reqs, num_of_in_workflow_reqs, " +
					" num_of_rejected_reqs, num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs, " +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs) " + 
					" values (" +
					"?, ?, " +
					"?,str_to_date('"+ dataLoadDt +"','%d %m %y %h:%i:%s'),?," +
					"?,?,?," +
					"?,?," +
					"?,?," +
					"?,?," +
					"?,?," +
					"?,?)";
				}
				else{
					sql = "insert into gr_user_metrics (project_id, owner, " +
					" requirement_type_short_name, data_load_dt, num_of_requirements, " +
					" num_of_test_pending_reqs, num_of_test_pass_reqs, num_of_test_fail_reqs, " +
					" num_of_draft_reqs, num_of_in_workflow_reqs, " +
					" num_of_rejected_reqs, num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs, " +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs) " + 
					" values (" +
					"?, ?, " +
					"?,to_date('"+ dataLoadDt +"','DD MON YYYY HH MI SS'),?," +
					"?,?,?," +
					"?,?," +
					"?,?," +
					"?,?," +
					"?,?," +
					"?,?)";
					
				}
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,projectId);
				prepStmt2.setString(2, owner);
				prepStmt2.setString(3, requirementTypeShortName);
				prepStmt2.setInt(4,numOfRequirements);
				
				prepStmt2.setInt(5,numOfTestPendingRequirements);
				prepStmt2.setInt(6,numOfTestPassRequirements);
				prepStmt2.setInt(7,numOfTestFailRequirements);
				
				prepStmt2.setInt(8,numOfDraftRequirements);
				prepStmt2.setInt(9,numOfInApprovalWorkflowRequirements);
				
				prepStmt2.setInt(10,numOfRejectedRequirements);
				prepStmt2.setInt(11,numOfApprovedRequirements);
				
				prepStmt2.setInt(12,numOfDanglingRequirements);
				prepStmt2.setInt(13,numOfOrphanRequirements);
				
				prepStmt2.setInt(14,numOfSuspectUpstreamRequirements);
				prepStmt2.setInt(15,numOfSuspectDownstreamRequirements);
				
				prepStmt2.setInt(16,numOfCompletedRequirements);
				prepStmt2.setInt(17,numOfIncompleteRequirements);
				
				prepStmt2.execute();
				prepStmt2.close();
			}
		
		    prepStmt.close();
			rs.close();
		}
	}
	
	private static void populateReleaseMetrics(java.sql.Connection con, int projectId, String dataLoadDt, String databaseType) throws SQLException{

		
		// lets get the list of release objects in this project.
		String sql = " select r.id, r.full_tag " + 
			" from gr_requirements r, gr_requirement_types rt " +
			" where " + 
			" rt.project_id =  ? " +
			" and rt.short_name = 'REL' " +
			" and r.requirement_type_id = rt.id " +
			" order by rt.display_sequence"; 
		
		PreparedStatement prepStmt = con.prepareStatement(sql);
		prepStmt.setInt(1, projectId);
		ResultSet rs = prepStmt.executeQuery();
	    while (rs.next()) {
	    	int releaseId = rs.getInt("id");
	    	String releaseFullTag = rs.getString("full_tag");
	    	
	    	
	    	// lets empty out the gr_release_requirements.
	    	sql = " delete from gr_release_requirements where release_id = ? ";
	    	PreparedStatement prepStmt2 = con.prepareStatement(sql);
	    	prepStmt2.setInt(1, releaseId);
	    	prepStmt2.execute();
	    	prepStmt2.close();
	    	
	    	// lets fill up the gr_release_requirements table for this release.
	    	fillUpReleaseRequirements(con,projectId,releaseId, releaseFullTag, dataLoadDt, databaseType);
	    	// lets fill up the trend information in gr_release_metrics table.
	    	fillUpReleaseMetrics(con,projectId,releaseId,releaseFullTag,dataLoadDt, databaseType);
	    	
	    	// lets fill up the trend information in gr_release_folder_metrcs table.
	    	fillUpReleaseFolderMetrics(con,projectId,releaseId,releaseFullTag,dataLoadDt, databaseType);
	    	
	    }
		
		
	    prepStmt.close();
		rs.close();
	}

	private static void fillUpReleaseRequirements(java.sql.Connection con, int projectId, int releaseId,
			String releaseFullTag, String dataLoadDt, String databaseType)
		throws SQLException{
		
		
			// here is the sql that we will use to get reqs that trace to this
			// requirement.
			String traceTreeSQL = " SELECT r.id, r.full_tag, rt.short_name "
					+ " FROM gr_requirements r , gr_requirement_types rt, gr_traces t "
					+ " where t.to_requirement_id = ? "
					+ " and t.from_requirement_id = r.id "
					+ " and r.requirement_type_id = rt.id " 
					+ " and r.deleted= 0";
			
			//////////////////////////////////////////////////
			// lets process level 1 of the trace tree.
			//////////////////////////////////////////////////
			PreparedStatement prepStmt1 = con.prepareStatement(traceTreeSQL);
			prepStmt1.setInt(1, releaseId);
			ResultSet rs1 = prepStmt1.executeQuery();
			// we will build a trace tree for each one of these requirements.
			while (rs1.next()) {
				int requirementId = rs1.getInt("id");
				String requirementFullTag = rs1.getString("full_tag");
				String requirementTypeShortName= rs1.getString("short_name");
				createReleaseRequirementEntry(con,projectId,releaseId,releaseFullTag,requirementId,
						requirementFullTag,requirementTypeShortName, dataLoadDt, databaseType);

				
				//////////////////////////////////////////////////
				// lets process level 2 of the trace tree.
				//////////////////////////////////////////////////
				PreparedStatement prepStmt2 = con.prepareStatement(traceTreeSQL);
				prepStmt2.setInt(1, requirementId);
				ResultSet rs2 = prepStmt2.executeQuery();
				// we will build a trace tree for each one of these requirements.
				while (rs2.next()) {
					requirementId = rs2.getInt("id");
					requirementFullTag = rs2.getString("full_tag");
					requirementTypeShortName= rs2.getString("short_name");
					createReleaseRequirementEntry(con,projectId,releaseId,releaseFullTag,requirementId,
							requirementFullTag,requirementTypeShortName, dataLoadDt, databaseType);

					
					
					//////////////////////////////////////////////////
					// lets process level 3 of the trace tree.
					//////////////////////////////////////////////////
					PreparedStatement prepStmt3 = con.prepareStatement(traceTreeSQL);
					prepStmt3.setInt(1, requirementId);
					ResultSet rs3 = prepStmt3.executeQuery();
					// we will build a trace tree for each one of these requirements.
					while (rs3.next()) {
						requirementId = rs3.getInt("id");
						requirementFullTag = rs3.getString("full_tag");
						requirementTypeShortName= rs3.getString("short_name");
						createReleaseRequirementEntry(con,projectId,releaseId,releaseFullTag,requirementId,
								requirementFullTag,requirementTypeShortName , dataLoadDt, databaseType);

						

						
						//////////////////////////////////////////////////
						// lets process level 4 of the trace tree.
						//////////////////////////////////////////////////
						PreparedStatement prepStmt4 = con.prepareStatement(traceTreeSQL);
						prepStmt4.setInt(1, requirementId);
						ResultSet rs4 = prepStmt4.executeQuery();
						// we will build a trace tree for each one of these requirements.
						while (rs4.next()) {
							requirementId = rs4.getInt("id");
							requirementFullTag = rs4.getString("full_tag");
							requirementTypeShortName= rs4.getString("short_name");
							createReleaseRequirementEntry(con,projectId,releaseId,releaseFullTag,requirementId,
									requirementFullTag,requirementTypeShortName , dataLoadDt, databaseType);

							
							
							//////////////////////////////////////////////////
							// lets process level 5 of the trace tree.
							//////////////////////////////////////////////////
							PreparedStatement prepStmt5 = con.prepareStatement(traceTreeSQL);
							prepStmt5.setInt(1, requirementId);
							ResultSet rs5 = prepStmt5.executeQuery();
							// we will build a trace tree for each one of these requirements.
							while (rs5.next()) {
								requirementId = rs5.getInt("id");
								requirementFullTag = rs5.getString("full_tag");
								requirementTypeShortName= rs5.getString("short_name");
								createReleaseRequirementEntry(con,projectId,releaseId,releaseFullTag,requirementId,
										requirementFullTag,requirementTypeShortName, dataLoadDt, databaseType);

								
								
								//////////////////////////////////////////////////
								// lets process level 6 of the trace tree.
								//////////////////////////////////////////////////
								PreparedStatement prepStmt6 = con.prepareStatement(traceTreeSQL);
								prepStmt6.setInt(1, requirementId);
								ResultSet rs6 = prepStmt6.executeQuery();
								// we will build a trace tree for each one of these requirements.
								while (rs6.next()) {
									requirementId = rs6.getInt("id");
									requirementFullTag = rs6.getString("full_tag");
									requirementTypeShortName= rs6.getString("short_name");
									createReleaseRequirementEntry(con,projectId,releaseId,releaseFullTag,requirementId,
											requirementFullTag,requirementTypeShortName , dataLoadDt, databaseType);

									
									//////////////////////////////////////////////////
									// lets process level 7 of the trace tree.
									//////////////////////////////////////////////////
									PreparedStatement prepStmt7 = con.prepareStatement(traceTreeSQL);
									prepStmt7.setInt(1, requirementId);
									ResultSet rs7 = prepStmt7.executeQuery();
									// we will build a trace tree for each one of these requirements.
									while (rs7.next()) {
										requirementId = rs7.getInt("id");
										requirementFullTag = rs7.getString("full_tag");
										requirementTypeShortName= rs7.getString("short_name");
										createReleaseRequirementEntry(con,projectId,releaseId,releaseFullTag,requirementId,
												requirementFullTag,requirementTypeShortName, dataLoadDt, databaseType);
										
									}
									rs7.close();
									prepStmt7.close();
									//////////////////////////////////////////////////
									// end of level 7 trace tree.
									//////////////////////////////////////////////////
									
									
									
								}
								rs6.close();
								prepStmt6.close();
								//////////////////////////////////////////////////
								// end of level 6 trace tree.
								//////////////////////////////////////////////////
								
								
								
							}
							rs5.close();
							prepStmt5.close();
							//////////////////////////////////////////////////
							// end of level 5 trace tree.
							//////////////////////////////////////////////////
							
							
						}
						rs4.close();
						prepStmt4.close();
						//////////////////////////////////////////////////
						// end of level 4 trace tree.
						//////////////////////////////////////////////////
						
						
						
					}
					rs3.close();
					prepStmt3.close();
					//////////////////////////////////////////////////
					// end of level 3 trace tree.
					//////////////////////////////////////////////////
					
					
					
				}
				rs2.close();
				prepStmt2.close();
				//////////////////////////////////////////////////
				// end of level 2 trace tree.
				//////////////////////////////////////////////////
				
			}
			rs1.close();
			prepStmt1.close();
			//////////////////////////////////////////////////
			// end of level 1 trace tree.
			//////////////////////////////////////////////////
		}

	private static void createReleaseRequirementEntry(java.sql.Connection con, int projectId, int releaseId,
			String releaseFullTag, int requirementId, String requirementFullTag, String requirementTypeShortName,
			String dataLoadDt, String databaseType )
		throws SQLException{
		
			PreparedStatement prepStmt = null;
			try {
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_release_requirements (project_id, release_id, release_full_tag, " +
				" requirement_id, requirement_full_tag, requirement_type_short_name, data_load_dt )" + 
				" values (?, ?, ?, ?, ?, ?, str_to_date('"+ dataLoadDt +"','%d %m %y %h:%i:%s')) ";	
			}
			else{
				sql = " insert into gr_release_requirements (project_id, release_id, release_full_tag, " +
				" requirement_id, requirement_full_tag, requirement_type_short_name, data_load_dt )" + 
				" values (?, ?, ?, ?, ?, ?, to_date('"+ dataLoadDt +"','DD MON YYYY HH MI SS')) ";	
			}
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, releaseId);
			prepStmt.setString(3, releaseFullTag);
			prepStmt.setInt(4, requirementId);
			prepStmt.setString(5, requirementFullTag);
			prepStmt.setString(6, requirementTypeShortName );
			
			prepStmt.execute();
			prepStmt.close();
			}
			catch (Exception e) {
				// most likely the unique constraint on release-requirement . so ignore. 		
			}
			finally{
				//It is very critical That we close the prepared statement here otherwise oracle will run out off open cursors 
				prepStmt.close();
			}
			
		}
		


	
	private static void fillUpReleaseMetrics(java.sql.Connection con, int projectId,
			int releaseId,String releaseFullTag, String dataLoadDt, String databaseType) throws SQLException{

			
			// lets get the distinct requirement types in this release tree.
			String sql = "select distinct rr.requirement_type_short_name " + 
				" from gr_release_requirements rr " +
				" where rr.release_id = ? "; 
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, releaseId);
			ResultSet rs = prepStmt.executeQuery();
			while(rs.next()) {
				String requirementTypeShortName = rs.getString("requirement_type_short_name");
				
				// for each release , requirement type combinations, lets get some metrics.
				
				// Total number of requirements.
				int numOfRequirements  = 0;
				sql = "select count(*) \"num_of_requirements\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.deleted = 0 ";
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				ResultSet rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfRequirements = rs2.getInt("num_of_requirements");
				}
				prepStmt2.close();
				rs2.close();
				
				
				// Total number of requirements in Testing Status = Pending.
				int numOfTestPendingRequirements  = 0;
				sql = "select count(*) \"num_of_test_pending_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.deleted = 0 " + 
					" and r.testing_status = 'Pending'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfTestPendingRequirements = rs2.getInt("num_of_test_pending_reqs");
				}
				prepStmt2.close();
				rs2.close();
				

				// Total number of requirements in Testing Status = Pass.
				int numOfTestPassRequirements  = 0;
				sql = "select count(*) \"num_of_test_pass_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.deleted = 0 " + 
					" and r.testing_status = 'Pass'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfTestPassRequirements = rs2.getInt("num_of_test_pass_reqs");
				}
				prepStmt2.close();
				rs2.close();

				// Total number of requirements in Testing Status = Fail.
				int numOfTestFailRequirements  = 0;
				sql = "select count(*) \"num_of_test_fail_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.deleted = 0 " + 
					" and r.testing_status = 'Fail'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfTestFailRequirements = rs2.getInt("num_of_test_fail_reqs");
				}
				prepStmt2.close();
				rs2.close();

				
				// Total number of  requirements in draft status.
				int numOfDraftRequirements  = 0;
				sql = "select count(*) \"num_of_draft_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.deleted = 0 " +
					" and r.status = 'Draft'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfDraftRequirements = rs2.getInt("num_of_draft_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				
				
				// Total number of  requirements in InApprovalWorkflow status.
				int numOfInApprovalWorkflowRequirements  = 0;
				sql = "select count(*) \"num_of_in_workflow_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.deleted = 0 " +
					" and r.status = 'In Approval WorkFlow'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfInApprovalWorkflowRequirements = rs2.getInt("num_of_in_workflow_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				

				
				// Total number of  requirements in rejected status.
				int numOfRejectedRequirements  = 0;
				sql = "select count(*) \"num_of_rejected_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.deleted = 0 " +
					" and r.status = 'Rejected'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfRejectedRequirements = rs2.getInt("num_of_rejected_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				
				
				// Total number of  requirements in approved status.
				int numOfApprovedRequirements  = 0;
				sql = "select count(*) \"num_of_approved_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.deleted = 0 " +
					" and r.status = 'Approved'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfApprovedRequirements = rs2.getInt("num_of_approved_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
			
				// Total number of  requirements in dangling status.
				int numOfDanglingRequirements  = 0;
				
				if (databaseType.equals("mySQL")){
					sql = "select count(*) \"num_of_dangling_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r, gr_requirement_types rt" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.requirement_type_id = rt.id " +
					" and r.deleted = 0 " +
					" and (r.trace_from is null or r.trace_from = '')" +
					" and rt.can_be_dangling = 1";
				}
				else{
					sql = "select count(*) \"num_of_dangling_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r, gr_requirement_types rt" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.requirement_type_id = rt.id " +
					" and r.deleted = 0 " +
					" and (upper(to_char(r.trace_from)) is null or upper(to_char(r.trace_from)) = '')" +
					" and rt.can_be_dangling = 1";
				}
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfDanglingRequirements = rs2.getInt("num_of_dangling_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				// Total number of  requirements in orphan status.
				// this will always be 0 , as each requirement in  this list 
				// somehow traces up to a release,  hence can not be orphan.
				int numOfOrphanRequirements  = 0;
				
				

				// Total number of  requirements in SuspectUpstream status.
				int numOfSuspectUpstreamRequirements  = 0;
				
				if (databaseType.equals("mySQL")){
					sql = "select count(*) \"num_of_suspect_upstream_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.deleted = 0 " +
					" and upper(r.trace_to) like '%(S)%'" ;
				}
				else{
					sql = "select count(*) \"num_of_suspect_upstream_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.deleted = 0 " +
					" and upper(to_char(r.trace_to)) like '%(S)%'" ;
				}
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfSuspectUpstreamRequirements = rs2.getInt("num_of_suspect_upstream_reqs");
				}
				prepStmt2.close();
				rs2.close();

				// Total number of  requirements in SuspectDownstream status.
				int numOfSuspectDownstreamRequirements  = 0;
				
				if (databaseType.equals("mySQL")){
					sql = "select count(*) \"num_of_suspect_downstream_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.deleted = 0 " +
					" and upper(r.trace_from) like '%(S)%'" ;
				}
				else{
					sql = "select count(*) \"num_of_suspect_downstream_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.deleted = 0 " +
					" and upper(to_char(r.trace_from)) like '%(S)%'" ;
				}
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfSuspectDownstreamRequirements = rs2.getInt("num_of_suspect_downstream_reqs");
				}
				prepStmt2.close();
				rs2.close();			
				
				// Total number of  requirements in Completed status.
				int numOfCompletedRequirements  = 0;
				sql = "select count(*) \"num_of_completed_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.deleted = 0 " +
					" and r.pct_complete = 100 " ;
				
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfCompletedRequirements = rs2.getInt("num_of_completed_reqs");
				}
				prepStmt2.close();
				rs2.close();	
				
				
				
				// Total number of  requirements in InComplet status.
				int numOfIncompleteRequirements  = 0;
				sql = "select count(*) \"num_of_incomplete_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.deleted = 0 " +
					" and r.pct_complete <> 100 " ;
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfIncompleteRequirements = rs2.getInt("num_of_incomplete_reqs");
				}
				prepStmt2.close();
				rs2.close();				
				

				// now that we have all the values we need, lets insert them into the trend table.
				
				if (databaseType.equals("mySQL")){
					sql = "insert into gr_release_metrics (project_id, release_id, release_full_tag," +
					" requirement_type_short_name, data_load_dt, num_of_requirements, " +
					" num_of_test_pending_reqs, num_of_test_pass_reqs, num_of_test_fail_reqs, " + 
					" num_of_draft_reqs, num_of_in_workflow_reqs, " +
					" num_of_rejected_reqs, num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs, " +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs) " + 
					" values (" +
					"?,?,?," +
					"?,str_to_date('"+ dataLoadDt +"','%d %m %y %h:%i:%s'),?," +
					"?,?,?," +
					"?,?," +
					"?,?," +
					"?,?," +
					"?,?," +
					"?,?)";
				}
				else{
					sql = "insert into gr_release_metrics (project_id, release_id, release_full_tag," +
					" requirement_type_short_name, data_load_dt, num_of_requirements, " +
					" num_of_test_pending_reqs, num_of_test_pass_reqs, num_of_test_fail_reqs, " + 
					" num_of_draft_reqs, num_of_in_workflow_reqs, " +
					" num_of_rejected_reqs, num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs, " +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs) " + 
					" values (" +
					"?,?,?," +
					"?,to_date('"+ dataLoadDt +"','DD MON YYYY HH MI SS'),?," +
					"?,?,?," +
					"?,?," +
					"?,?," +
					"?,?," +
					"?,?," +
					"?,?)";
				}				
				
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,projectId);
				prepStmt2.setInt(2,releaseId);
				prepStmt2.setString(3, releaseFullTag);
				
				prepStmt2.setString(4, requirementTypeShortName);
				prepStmt2.setInt(5,numOfRequirements);
				
				prepStmt2.setInt(6,numOfTestPendingRequirements);
				prepStmt2.setInt(7,numOfTestPassRequirements);
				prepStmt2.setInt(8,numOfTestFailRequirements);
				
				prepStmt2.setInt(9,numOfDraftRequirements);
				prepStmt2.setInt(10,numOfInApprovalWorkflowRequirements);
				
				prepStmt2.setInt(11,numOfRejectedRequirements);
				prepStmt2.setInt(12,numOfApprovedRequirements);
				
				prepStmt2.setInt(13,numOfDanglingRequirements);
				prepStmt2.setInt(14,numOfOrphanRequirements);
				
				prepStmt2.setInt(15,numOfSuspectUpstreamRequirements);
				prepStmt2.setInt(16,numOfSuspectDownstreamRequirements);
				
				prepStmt2.setInt(17,numOfCompletedRequirements);
				prepStmt2.setInt(18,numOfIncompleteRequirements);
				
				prepStmt2.execute();
				prepStmt2.close();
								
			
			}

		    prepStmt.close();
			rs.close();
		}




		// for every relase object, it looks into the gr_release_requirements table to 
		// crunch the data and load into gr_release_metrics table.
		private static void fillUpReleaseFolderMetrics(java.sql.Connection con, int projectId,
			int releaseId,String releaseFullTag, String dataLoadDt, String databaseType) throws SQLException{

			// since we won't be using the releaseFolderMetrics data for trending , lets wipe out all the data and then do a fresh load.
			
			
			
			// lets get the distinct requirement types and folde Ids in this release tree.
			String sql = "select distinct rr.requirement_type_short_name, r.folder_id " +
				" from gr_release_requirements rr, gr_requirements r " +
				" where rr.release_id = ? " +
				" and rr.requirement_id = r.id " ;

			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, releaseId);
			ResultSet rs = prepStmt.executeQuery();
			while(rs.next()) {
				String requirementTypeShortName = rs.getString("requirement_type_short_name");
				int folderId = rs.getInt("folder_id");
				// for each release , requirement type combinations, lets get some metrics.
				
				// Total number of requirements.
				int numOfRequirements  = 0;
				sql = "select count(*) \"num_of_requirements\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.folder_id =  ? " +
					" and r.deleted = 0 ";
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				prepStmt2.setInt(3, folderId);
				ResultSet rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfRequirements = rs2.getInt("num_of_requirements");
				}
				prepStmt2.close();
				rs2.close();
				
				
				
				// Total number of requirements in Testing Status = Pending.
				int numOfTestPendingRequirements  = 0;
				sql = "select count(*) \"num_of_test_pending_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.folder_id =  ? " +
					" and r.deleted = 0 " + 
					" and r.testing_status = 'Pending'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				prepStmt2.setInt(3, folderId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfTestPendingRequirements = rs2.getInt("num_of_test_pending_reqs");
				}
				prepStmt2.close();
				rs2.close();
				

				// Total number of requirements in Testing Status = Pass.
				int numOfTestPassRequirements  = 0;
				sql = "select count(*) \"num_of_test_pass_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.folder_id =  ? " +
					" and r.deleted = 0 " + 
					" and r.testing_status = 'Pass'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				prepStmt2.setInt(3, folderId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfTestPassRequirements = rs2.getInt("num_of_test_pass_reqs");
				}
				prepStmt2.close();
				rs2.close();

				// Total number of requirements in Testing Status = Fail.
				int numOfTestFailRequirements  = 0;
				sql = "select count(*) \"num_of_test_fail_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.folder_id =  ? " +
					" and r.deleted = 0 " + 
					" and r.testing_status = 'Fail'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				prepStmt2.setInt(3, folderId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfTestFailRequirements = rs2.getInt("num_of_test_fail_reqs");
				}
				prepStmt2.close();
				rs2.close();

				
				// Total number of  requirements in draft status.
				int numOfDraftRequirements  = 0;
				sql = "select count(*) \"num_of_draft_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.folder_id =  ? " +
					" and r.deleted = 0 " +
					" and r.status = 'Draft'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				prepStmt2.setInt(3, folderId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfDraftRequirements = rs2.getInt("num_of_draft_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				
				
				// Total number of  requirements in InApprovalWorkflow status.
				int numOfInApprovalWorkflowRequirements  = 0;
				sql = "select count(*) \"num_of_in_workflow_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.folder_id =  ? " +
					" and r.deleted = 0 " +
					" and r.status = 'In Approval WorkFlow'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				prepStmt2.setInt(3, folderId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfInApprovalWorkflowRequirements = rs2.getInt("num_of_in_workflow_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				

				
				// Total number of  requirements in rejected status.
				int numOfRejectedRequirements  = 0;
				sql = "select count(*) \"num_of_rejected_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.folder_id =  ? " +
					" and r.deleted = 0 " +
					" and r.status = 'Rejected'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				prepStmt2.setInt(3, folderId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfRejectedRequirements = rs2.getInt("num_of_rejected_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				
				
				// Total number of  requirements in approved status.
				int numOfApprovedRequirements  = 0;
				sql = "select count(*) \"num_of_approved_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.folder_id =  ? " +
					" and r.deleted = 0 " +
					" and r.status = 'Approved'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				prepStmt2.setInt(3, folderId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfApprovedRequirements = rs2.getInt("num_of_approved_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
			
				// Total number of  requirements in dangling status.
				int numOfDanglingRequirements  = 0;
				
				if (databaseType.equals("mySQL")){
					sql = "select count(*) \"num_of_dangling_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r, gr_requirement_types rt" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.requirement_type_id = rt.id " +
					" and r.deleted = 0 " +
					" and r.folder_id =  ? " +
					" and (r.trace_from is null or r.trace_from = '')" +
					" and rt.can_be_dangling = 1";
				}
				else{
					sql = "select count(*) \"num_of_dangling_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r, gr_requirement_types rt" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.requirement_type_id = rt.id " +
					" and r.folder_id =  ? " +
					" and r.deleted = 0 " +
					" and (upper(to_char(r.trace_from)) is null or upper(to_char(r.trace_from)) = '')" +
					" and rt.can_be_dangling = 1";
				}
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				prepStmt2.setInt(3, folderId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfDanglingRequirements = rs2.getInt("num_of_dangling_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				// Total number of  requirements in orphan status.
				// this will always be 0 , as each requirement in  this list 
				// somehow traces up to a release,  hence can not be orphan.
				int numOfOrphanRequirements  = 0;
				
				

				// Total number of  requirements in SuspectUpstream status.
				int numOfSuspectUpstreamRequirements  = 0;
				
				if (databaseType.equals("mySQL")){
					sql = "select count(*) \"num_of_suspect_upstream_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.folder_id =  ? " +
					" and r.deleted = 0 " +
					" and upper(r.trace_to) like '%(S)%'" ;
				}
				else{
					sql = "select count(*) \"num_of_suspect_upstream_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.folder_id =  ? " +
					" and r.deleted = 0 " +
					" and upper(to_char(r.trace_to)) like '%(S)%'" ;
				}
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				prepStmt2.setInt(3, folderId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfSuspectUpstreamRequirements = rs2.getInt("num_of_suspect_upstream_reqs");
				}
				prepStmt2.close();
				rs2.close();

				// Total number of  requirements in SuspectDownstream status.
				int numOfSuspectDownstreamRequirements  = 0;
				
				if (databaseType.equals("mySQL")){
					sql = "select count(*) \"num_of_suspect_downstream_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.folder_id =  ? " +
					" and r.deleted = 0 " +
					" and upper(r.trace_from) like '%(S)%'" ;
				}
				else{
					sql = "select count(*) \"num_of_suspect_downstream_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.folder_id =  ? " +
					" and r.deleted = 0 " +
					" and upper(to_char(r.trace_from)) like '%(S)%'" ;
				}
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				prepStmt2.setInt(3, folderId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfSuspectDownstreamRequirements = rs2.getInt("num_of_suspect_downstream_reqs");
				}
				prepStmt2.close();
				rs2.close();			
				
				// Total number of  requirements in Completed status.
				int numOfCompletedRequirements  = 0;
				sql = "select count(*) \"num_of_completed_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.folder_id =  ? " +
					" and r.deleted = 0 " +
					" and r.pct_complete = 100 " ;
				
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				prepStmt2.setInt(3, folderId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfCompletedRequirements = rs2.getInt("num_of_completed_reqs");
				}
				prepStmt2.close();
				rs2.close();	
				
				
				
				// Total number of  requirements in InComplet status.
				int numOfIncompleteRequirements  = 0;
				sql = "select count(*) \"num_of_incomplete_reqs\" " +
					" from gr_release_requirements rr , gr_requirements r" +
					" where rr.release_id = ? " +
					" and rr.requirement_type_short_name = ? " +
					" and rr.requirement_id = r.id " +
					" and r.folder_id =  ? " +
					" and r.deleted = 0 " +
					" and r.pct_complete <> 100 " ;
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,releaseId);
				prepStmt2.setString(2, requirementTypeShortName);
				prepStmt2.setInt(3, folderId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfIncompleteRequirements = rs2.getInt("num_of_incomplete_reqs");
				}
				prepStmt2.close();
				rs2.close();				
				

				// now that we have all the values we need, lets insert them into the trend table.
				
				if (databaseType.equals("mySQL")){
					sql = "insert into gr_release_folder_metrics (project_id, release_id, folder_id, release_full_tag," +
					" requirement_type_short_name, data_load_dt, num_of_requirements, " +
					" num_of_test_pending_reqs, num_of_test_pass_reqs, num_of_test_fail_reqs, " + 
					" num_of_draft_reqs, num_of_in_workflow_reqs, " +
					" num_of_rejected_reqs, num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs, " +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs) " + 
					" values (" +
					"?,?,?,?," +
					"?,str_to_date('"+ dataLoadDt +"','%d %m %y %h:%i:%s'),?," +
					"?,?,?," +
					"?,?," +
					"?,?," +
					"?,?," +
					"?,?," +
					"?,?)";
				}
				else{
					sql = "insert into gr_release_folder_metrics (project_id, release_id, folder_id, release_full_tag," +
					" requirement_type_short_name, data_load_dt, num_of_requirements, " +
					" num_of_test_pending_reqs, num_of_test_pass_reqs, num_of_test_fail_reqs, " + 
					" num_of_draft_reqs, num_of_in_workflow_reqs, " +
					" num_of_rejected_reqs, num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs, " +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs) " + 
					" values (" +
					"?,?,?,?," +
					"?,to_date('"+ dataLoadDt +"','DD MON YYYY HH MI SS'),?," +
					"?,?,?," +
					"?,?," +
					"?,?," +
					"?,?," +
					"?,?," +
					"?,?)";
				}
				
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,projectId);
				prepStmt2.setInt(2,releaseId);
				prepStmt2.setInt(3,folderId);
				prepStmt2.setString(4, releaseFullTag);
				
				prepStmt2.setString(5, requirementTypeShortName);
				prepStmt2.setInt(6,numOfRequirements);
				
				prepStmt2.setInt(7,numOfTestPendingRequirements);
				prepStmt2.setInt(8,numOfTestPassRequirements);
				prepStmt2.setInt(9,numOfTestFailRequirements);
				
				prepStmt2.setInt(10,numOfDraftRequirements);
				prepStmt2.setInt(11,numOfInApprovalWorkflowRequirements);
				
				prepStmt2.setInt(12,numOfRejectedRequirements);
				prepStmt2.setInt(13,numOfApprovedRequirements);
				
				prepStmt2.setInt(14,numOfDanglingRequirements);
				prepStmt2.setInt(15,numOfOrphanRequirements);
				
				prepStmt2.setInt(16,numOfSuspectUpstreamRequirements);
				prepStmt2.setInt(17,numOfSuspectDownstreamRequirements);
				
				prepStmt2.setInt(18,numOfCompletedRequirements);
				prepStmt2.setInt(19,numOfIncompleteRequirements);
				
				prepStmt2.execute();
				prepStmt2.close();
								
			
			}

		    prepStmt.close();
			rs.close();
		}
	
		private static void fillUpBaselineMetrics(java.sql.Connection con, int projectId,
				 String dataLoadDt, String databaseType) throws SQLException{

				// lets gets a list of Requirement Types, and Baselines in this project
				// with data.
				
				// lets get the distinct requirement types in this baseline requirements..
				String sql = "select distinct rtb.id \"rt_baseline_id\" ,rt.id \"requirement_type_id\"," +
					" rt.short_name, rt.display_sequence "  +
					" from gr_requirement_baselines rb, gr_rt_baselines rtb, gr_requirement_types rt " +
					" where rb.rt_baseline_id= rtb.id " +
					" and rtb.requirement_type_id = rt.id " +
					" and rt.project_id = ? " +
					" order by rt.display_sequence"; 
					
				
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				ResultSet rs = prepStmt.executeQuery();
				while(rs.next()) {
					int rTBaselineId = rs.getInt("rt_baseline_id");
					int requirementTypeId  = rs.getInt("requirement_type_id");
					String requirementTypePrefix = rs.getString("short_name");
					
					// for each baseline, requirement type combinations, lets get some metrics.
					
					// Total number of requirements.
					int numOfRequirements  = 0;
					sql = "select count(*) \"num_of_requirements\" " +
						" from gr_requirement_baselines rb , gr_requirements r" +
						" where rb.rt_baseline_id = ? " +
						" and r.requirement_type_id = ? " +
						" and rb.requirement_id = r.id " +
						" and r.deleted = 0 ";
					PreparedStatement prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1,rTBaselineId);
					prepStmt2.setInt(2, requirementTypeId);
					ResultSet rs2 = prepStmt2.executeQuery();
					while (rs2.next()) {
						numOfRequirements = rs2.getInt("num_of_requirements");
					}
					prepStmt2.close();
					rs2.close();
					
					// Total number of  requirements with testing_status = Pending
					int numOfTestPendingRequirements  = 0;
					sql = "select count(*) \"num_of_test_pending_reqs\" " +
						" from gr_requirement_baselines rb , gr_requirements r" +
						" where rb.rt_baseline_id = ? " +
						" and r.requirement_type_id = ? " +
						" and rb.requirement_id = r.id " +
						" and r.deleted = 0 " +
						" and r.testing_status = 'Pending'";
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1,rTBaselineId);
					prepStmt2.setInt(2, requirementTypeId);
					rs2 = prepStmt2.executeQuery();
					while (rs2.next()) {
						numOfTestPendingRequirements = rs2.getInt("num_of_test_pending_reqs");
					}
					prepStmt2.close();
					rs2.close();
					

					// Total number of  requirements with testing_status = Pass
					int numOfTestPassRequirements  = 0;
					sql = "select count(*) \"num_of_test_pass_reqs\" " +
						" from gr_requirement_baselines rb , gr_requirements r" +
						" where rb.rt_baseline_id = ? " +
						" and r.requirement_type_id = ? " +
						" and rb.requirement_id = r.id " +
						" and r.deleted = 0 " +
						" and r.testing_status = 'Pass'";
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1,rTBaselineId);
					prepStmt2.setInt(2, requirementTypeId);
					rs2 = prepStmt2.executeQuery();
					while (rs2.next()) {
						numOfTestPassRequirements = rs2.getInt("num_of_test_pass_reqs");
					}
					prepStmt2.close();
					rs2.close();
					
					
					// Total number of  requirements with testing_status = Fail
					int numOfTestFailRequirements  = 0;
					sql = "select count(*) \"num_of_test_fail_reqs\" " +
						" from gr_requirement_baselines rb , gr_requirements r" +
						" where rb.rt_baseline_id = ? " +
						" and r.requirement_type_id = ? " +
						" and rb.requirement_id = r.id " +
						" and r.deleted = 0 " +
						" and r.testing_status = 'Fail'";
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1,rTBaselineId);
					prepStmt2.setInt(2, requirementTypeId);
					rs2 = prepStmt2.executeQuery();
					while (rs2.next()) {
						numOfTestFailRequirements = rs2.getInt("num_of_test_fail_reqs");
					}
					prepStmt2.close();
					rs2.close();

					
					// Total number of  requirements in draft status.
					int numOfDraftRequirements  = 0;
					sql = "select count(*) \"num_of_draft_reqs\" " +
						" from gr_requirement_baselines rb , gr_requirements r" +
						" where rb.rt_baseline_id = ? " +
						" and r.requirement_type_id = ? " +
						" and rb.requirement_id = r.id " +
						" and r.deleted = 0 " +
						" and r.status = 'Draft'";
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1,rTBaselineId);
					prepStmt2.setInt(2, requirementTypeId);
					rs2 = prepStmt2.executeQuery();
					while (rs2.next()) {
						numOfDraftRequirements = rs2.getInt("num_of_draft_reqs");
					}
					prepStmt2.close();
					rs2.close();
					
					
					// Total number of  requirements in InApprovalWorkflow status.
					int numOfInApprovalWorkflowRequirements  = 0;
					sql = "select count(*) \"num_of_in_workflow_reqs\" " +
						" from gr_requirement_baselines rb , gr_requirements r" +
						" where rb.rt_baseline_id = ? " +
						" and r.requirement_type_id = ? " +
						" and rb.requirement_id = r.id " +
						" and r.deleted = 0 " +
						" and r.status = 'In Approval WorkFlow'";
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1,rTBaselineId);
					prepStmt2.setInt(2, requirementTypeId);
					rs2 = prepStmt2.executeQuery();
					while (rs2.next()) {
						numOfInApprovalWorkflowRequirements = rs2.getInt("num_of_in_workflow_reqs");
					}
					prepStmt2.close();
					rs2.close();
					
					

					
					// Total number of  requirements in rejected status.
					int numOfRejectedRequirements  = 0;
					sql = "select count(*) \"num_of_rejected_reqs\" " +
						" from gr_requirement_baselines rb , gr_requirements r" +
						" where rb.rt_baseline_id = ? " +
						" and r.requirement_type_id = ? " +
						" and rb.requirement_id = r.id " +
						" and r.deleted = 0 " +
						" and r.status = 'Rejected'";
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1,rTBaselineId);
					prepStmt2.setInt(2, requirementTypeId);
					rs2 = prepStmt2.executeQuery();
					while (rs2.next()) {
						numOfRejectedRequirements = rs2.getInt("num_of_rejected_reqs");
					}
					prepStmt2.close();
					rs2.close();
					
					
					
					// Total number of  requirements in approved status.
					int numOfApprovedRequirements  = 0;
					sql = "select count(*) \"num_of_approved_reqs\" " +
						" from gr_requirement_baselines rb , gr_requirements r" +
						" where rb.rt_baseline_id = ? " +
						" and r.requirement_type_id = ? " +
						" and rb.requirement_id = r.id " +
						" and r.deleted = 0 " +
						" and r.status = 'Approved'";
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1,rTBaselineId);
					prepStmt2.setInt(2, requirementTypeId);
					rs2 = prepStmt2.executeQuery();
					while (rs2.next()) {
						numOfApprovedRequirements = rs2.getInt("num_of_approved_reqs");
					}
					prepStmt2.close();
					rs2.close();
					
				
					// Total number of  requirements in dangling status.
					int numOfDanglingRequirements  = 0;
					
					
					if (databaseType.equals("mySQL")){
						sql = "select count(*) \"num_of_dangling_reqs\" " +
						" from gr_requirement_baselines rb , gr_requirements r, gr_requirement_types rt" +
						" where rb.rt_baseline_id = ? " +
						" and r.requirement_type_id = ? " +
						" and rb.requirement_id = r.id " +
						" and r.requirement_type_id = rt.id " +
						" and r.deleted = 0 " +
						" and (r.trace_from is null or r.trace_from = '')" +
						" and rt.can_be_dangling = 1";
					}
					else{
						sql = "select count(*) \"num_of_dangling_reqs\" " +
						" from gr_requirement_baselines rb , gr_requirements r, gr_requirement_types rt" +
						" where rb.rt_baseline_id = ? " +
						" and r.requirement_type_id = ? " +
						" and rb.requirement_id = r.id " +
						" and r.requirement_type_id = rt.id " +
						" and r.deleted = 0 " +
						" and (upper(to_char(r.trace_from)) is null or upper(to_char(r.trace_from)) = '')" +
						" and rt.can_be_dangling = 1";
						
					}
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1,rTBaselineId);
					prepStmt2.setInt(2, requirementTypeId);
					rs2 = prepStmt2.executeQuery();
					while (rs2.next()) {
						numOfDanglingRequirements = rs2.getInt("num_of_dangling_reqs");
					}
					prepStmt2.close();
					rs2.close();
					
					// Total number of  requirements in orphan status.
					// this will always be 0 , as each requirement in  this list 
					// somehow traces up to a release,  hence can not be orphan.
					int numOfOrphanRequirements  = 0;
					
					if (databaseType.equals("mySQL")){
						sql = "select count(*) \"num_of_orphan_reqs\" " +
						" from gr_requirement_baselines rb , gr_requirements r, gr_requirement_types rt" +
						" where rb.rt_baseline_id = ? " +
						" and r.requirement_type_id = ? " +
						" and rb.requirement_id = r.id " +
						" and r.requirement_type_id = rt.id " +
						" and r.deleted = 0 " +
						" and (r.trace_to is null or r.trace_to = '')" +
						" and rt.can_be_orphan = 1 ";
					}
					else{
						sql = "select count(*) \"num_of_orphan_reqs\" " +
						" from gr_requirement_baselines rb , gr_requirements r, gr_requirement_types rt" +
						" where rb.rt_baseline_id = ? " +
						" and r.requirement_type_id = ? " +
						" and rb.requirement_id = r.id " +
						" and r.requirement_type_id = rt.id " +
						" and r.deleted = 0 " +
						" and (upper(to_char(r.trace_to)) is null or upper(to_char(r.trace_to)) = '')" +
						" and rt.can_be_orphan = 1 ";
						
					}
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1,rTBaselineId);
					prepStmt2.setInt(2, requirementTypeId);
					rs2 = prepStmt2.executeQuery();
					while (rs2.next()) {
						numOfOrphanRequirements = rs2.getInt("num_of_orphan_reqs");
					}
					prepStmt2.close();
					rs2.close();
					
					
					

					// Total number of  requirements in SuspectUpstream status.
					int numOfSuspectUpstreamRequirements  = 0;
					
					if (databaseType.equals("mySQL")){
						sql = "select count(*) \"num_of_suspect_upstream_reqs\" " +
						" from gr_requirement_baselines rb , gr_requirements r" +
						" where rb.rt_baseline_id = ? " +
						" and r.requirement_type_id = ? " +
						" and rb.requirement_id = r.id " +
						" and r.deleted = 0 " +
						" and upper(r.trace_to) like '%(S)%'" ;
					}
					else{
						sql = "select count(*) \"num_of_suspect_upstream_reqs\" " +
						" from gr_requirement_baselines rb , gr_requirements r" +
						" where rb.rt_baseline_id = ? " +
						" and r.requirement_type_id = ? " +
						" and rb.requirement_id = r.id " +
						" and r.deleted = 0 " +
						" and upper(to_char(r.trace_to)) like '%(S)%'" ;
						
					}
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1,rTBaselineId);
					prepStmt2.setInt(2, requirementTypeId);
					rs2 = prepStmt2.executeQuery();
					while (rs2.next()) {
						numOfSuspectUpstreamRequirements = rs2.getInt("num_of_suspect_upstream_reqs");
					}
					prepStmt2.close();
					rs2.close();

					// Total number of  requirements in SuspectDownstream status.
					int numOfSuspectDownstreamRequirements  = 0;
					if (databaseType.equals("mySQL")){
						sql = "select count(*) \"num_of_suspect_downstream_reqs\" " +
						" from gr_requirement_baselines rb , gr_requirements r" +
						" where rb.rt_baseline_id = ? " +
						" and r.requirement_type_id = ? " +
						" and rb.requirement_id = r.id " +
						" and r.deleted = 0 " +
						" and upper(r.trace_from) like '%(S)%'" ;
					}
					else{
						sql = "select count(*) \"num_of_suspect_downstream_reqs\" " +
						" from gr_requirement_baselines rb , gr_requirements r" +
						" where rb.rt_baseline_id = ? " +
						" and r.requirement_type_id = ? " +
						" and rb.requirement_id = r.id " +
						" and r.deleted = 0 " +
						" and upper(to_char(r.trace_from)) like '%(S)%'" ;
						
					}
					
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1,rTBaselineId);
					prepStmt2.setInt(2, requirementTypeId);
					rs2 = prepStmt2.executeQuery();
					while (rs2.next()) {
						numOfSuspectDownstreamRequirements = rs2.getInt("num_of_suspect_downstream_reqs");
					}
					prepStmt2.close();
					rs2.close();			
					
					// Total number of  requirements in Completed status.
					int numOfCompletedRequirements  = 0;
					sql = "select count(*) \"num_of_completed_reqs\" " +
						" from gr_requirement_baselines rb , gr_requirements r" +
						" where rb.rt_baseline_id = ? " +
						" and r.requirement_type_id = ? " +
						" and rb.requirement_id = r.id " +
						" and r.deleted = 0 " +
						" and r.pct_complete = 100 " ;
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1,rTBaselineId);
					prepStmt2.setInt(2, requirementTypeId);
					rs2 = prepStmt2.executeQuery();
					while (rs2.next()) {
						numOfCompletedRequirements = rs2.getInt("num_of_completed_reqs");
					}
					prepStmt2.close();
					rs2.close();	
					
					
					
					// Total number of  requirements in InComplet status.
					int numOfIncompleteRequirements  = 0;
					sql = "select count(*) \"num_of_incomplete_reqs\" " +
						" from gr_requirement_baselines rb , gr_requirements r" +
						" where rb.rt_baseline_id = ? " +
						" and r.requirement_type_id = ? " +
						" and rb.requirement_id = r.id " +
						" and r.deleted = 0 " +
						" and r.pct_complete <> 100 " ;
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1,rTBaselineId);
					prepStmt2.setInt(2, requirementTypeId);
					rs2 = prepStmt2.executeQuery();
					while (rs2.next()) {
						numOfIncompleteRequirements = rs2.getInt("num_of_incomplete_reqs");
					}
					prepStmt2.close();
					rs2.close();				
					

					// now that we have all the values we need, lets insert them into the trend table.
					if (databaseType.equals("mySQL")){
						sql = "insert into gr_baseline_metrics ( rt_baseline_id,project_id," +
						" requirement_type_short_name, data_load_dt, num_of_requirements, " +
						" num_of_test_pending_reqs, num_of_test_pass_reqs, num_of_test_fail_reqs, " +
						" num_of_draft_reqs, num_of_in_workflow_reqs, " +
						" num_of_rejected_reqs, num_of_approved_reqs, " +
						" num_of_dangling_reqs, num_of_orphan_reqs, " +
						" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
						" num_of_completed_reqs, num_of_incomplete_reqs) " + 
						" values (" +
						"?,?," +
						"?,str_to_date('"+ dataLoadDt +"','%d %m %y %h:%i:%s'),?," +
						"?,?,?," +
						"?,?," +
						"?,?," +
						"?,?," +
						"?,?," +
						"?,?)";
					}
					else{
						sql = "insert into gr_baseline_metrics ( rt_baseline_id,project_id," +
						" requirement_type_short_name, data_load_dt, num_of_requirements, " +
						" num_of_test_pending_reqs, num_of_test_pass_reqs, num_of_test_fail_reqs, " +
						" num_of_draft_reqs, num_of_in_workflow_reqs, " +
						" num_of_rejected_reqs, num_of_approved_reqs, " +
						" num_of_dangling_reqs, num_of_orphan_reqs, " +
						" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
						" num_of_completed_reqs, num_of_incomplete_reqs) " + 
						" values (" +
						"?,?," +
						"?,to_date('"+ dataLoadDt +"','DD MON YYYY HH MI SS'),?," +
						"?,?,?," +
						"?,?," +
						"?,?," +
						"?,?," +
						"?,?," +
						"?,?)";
						
					}
					
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setInt(1,rTBaselineId);
					prepStmt2.setInt(2,projectId);
					
					prepStmt2.setString(3, requirementTypePrefix);
					prepStmt2.setInt(4,numOfRequirements);
					
					prepStmt2.setInt(5,numOfTestPendingRequirements);
					prepStmt2.setInt(6,numOfTestPassRequirements);
					prepStmt2.setInt(7,numOfTestFailRequirements);
					
					prepStmt2.setInt(8,numOfDraftRequirements);
					prepStmt2.setInt(9,numOfInApprovalWorkflowRequirements);
					
					prepStmt2.setInt(10,numOfRejectedRequirements);
					prepStmt2.setInt(11,numOfApprovedRequirements);
					
					prepStmt2.setInt(12,numOfDanglingRequirements);
					prepStmt2.setInt(13,numOfOrphanRequirements);
					
					prepStmt2.setInt(14,numOfSuspectUpstreamRequirements);
					prepStmt2.setInt(15,numOfSuspectDownstreamRequirements);
					
					prepStmt2.setInt(16,numOfCompletedRequirements);
					prepStmt2.setInt(17,numOfIncompleteRequirements);
					
					prepStmt2.execute();
					prepStmt2.close();
									
				
				}

			    prepStmt.close();
				rs.close();
			}


		

		
		
		// for every folder object, it crunches the data and 
		// loads into gr_folder_metrics table.
		private static void fillUpFolderMetrics(java.sql.Connection con, int projectId,
			 String dataLoadDt,String databaseType) throws SQLException{

			
			// lets get the distinct folder Ids in this projects..
			String sql = "select distinct r.folder_id, rt.short_name , r.requirement_type_id, rt.display_sequence" + 
				" from gr_requirements r, gr_requirement_types rt " +
				" where r.project_id = ? " +
				" and r.requirement_type_id  = rt.id " + 
				" order by rt.display_sequence"; 
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			ResultSet rs = prepStmt.executeQuery();
			while(rs.next()) {
				int folderId = rs.getInt("folder_id");
				int requirementTypeId  = rs.getInt("requirement_type_id");
				String requirementTypePrefix = rs.getString("short_name");
				
				// for each baseline, requirement type combinations, lets get some metrics.
				
				// Total number of requirements.
				int numOfRequirements  = 0;
				sql = "select count(*) \"num_of_requirements\" " +
					" from gr_requirements r" +
					" where r.folder_id = ? " +
					" and r.requirement_type_id = ? " +
					" and r.deleted = 0 ";
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,folderId);
				prepStmt2.setInt(2, requirementTypeId);
				ResultSet rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfRequirements = rs2.getInt("num_of_requirements");
				}
				prepStmt2.close();
				rs2.close();
				

				// Total number of  requirements in test_status='Pending'
				int numOfTestPendingRequirements  = 0;
				sql = "select count(*) \"num_of_test_pending_reqs\" " +
					" from gr_requirements r" +
					" where r.folder_id = ? " +
					" and r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.testing_status = 'Pending'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,folderId);
				prepStmt2.setInt(2, requirementTypeId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfTestPendingRequirements = rs2.getInt("num_of_test_pending_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				
				// Total number of  requirements in test_status='Pass'
				int numOfTestPassRequirements  = 0;
				sql = "select count(*) \"num_of_test_pass_reqs\" " +
					" from gr_requirements r" +
					" where r.folder_id = ? " +
					" and r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.testing_status = 'Pass'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,folderId);
				prepStmt2.setInt(2, requirementTypeId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfTestPassRequirements = rs2.getInt("num_of_test_pass_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				// Total number of  requirements in test_status='Fail'
				int numOfTestFailRequirements  = 0;
				sql = "select count(*) \"num_of_test_fail_reqs\" " +
					" from gr_requirements r" +
					" where r.folder_id = ? " +
					" and r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.testing_status = 'Fail'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,folderId);
				prepStmt2.setInt(2, requirementTypeId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfTestFailRequirements = rs2.getInt("num_of_test_fail_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				
				// Total number of  requirements in draft status.
				int numOfDraftRequirements  = 0;
				sql = "select count(*) \"num_of_draft_reqs\" " +
					" from gr_requirements r" +
					" where r.folder_id = ? " +
					" and r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.status = 'Draft'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,folderId);
				prepStmt2.setInt(2, requirementTypeId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfDraftRequirements = rs2.getInt("num_of_draft_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				
				
				// Total number of  requirements in InApprovalWorkflow status.
				int numOfInApprovalWorkflowRequirements  = 0;
				sql = "select count(*) \"num_of_in_workflow_reqs\" " +
					" from gr_requirements r" +
					" where r.folder_id = ? " +
					" and r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.status = 'In Approval WorkFlow'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,folderId);
				prepStmt2.setInt(2, requirementTypeId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfInApprovalWorkflowRequirements = rs2.getInt("num_of_in_workflow_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				

				
				// Total number of  requirements in rejected status.
				int numOfRejectedRequirements  = 0;
				sql = "select count(*) \"num_of_rejected_reqs\" " +
					" from gr_requirements r" +
					" where r.folder_id = ? " +
					" and r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.status = 'Rejected'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,folderId);
				prepStmt2.setInt(2, requirementTypeId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfRejectedRequirements = rs2.getInt("num_of_rejected_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				
				
				// Total number of  requirements in approved status.
				int numOfApprovedRequirements  = 0;
				sql = "select count(*) \"num_of_approved_reqs\" " +
					" from gr_requirements r" +
					" where r.folder_id = ? " +
					" and r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.status = 'Approved'";
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,folderId);
				prepStmt2.setInt(2, requirementTypeId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfApprovedRequirements = rs2.getInt("num_of_approved_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
			
				// Total number of  requirements in dangling status.
				int numOfDanglingRequirements  = 0;
				
				if (databaseType.equals("mySQL")){
					sql = "select count(*) \"num_of_dangling_reqs\" " +
					" from gr_requirements r, gr_requirement_types rt" +
					" where r.folder_id = ? " +
					" and r.requirement_type_id = rt.id " +
					" and r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and (r.trace_from is null or r.trace_from = '')" +
					" and rt.can_be_dangling = 1";
				}
				else{
					sql = "select count(*) \"num_of_dangling_reqs\" " +
					" from gr_requirements r, gr_requirement_types rt" +
					" where r.folder_id = ? " +
					" and r.requirement_type_id = rt.id " +
					" and r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and (upper(to_char(r.trace_from)) is null or upper(to_char(r.trace_from)) = '')" +
					" and rt.can_be_dangling = 1";
					
				}
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,folderId);
				prepStmt2.setInt(2, requirementTypeId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfDanglingRequirements = rs2.getInt("num_of_dangling_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				// Total number of  requirements in orphan status.
				
				int numOfOrphanRequirements  = 0;
				
				
				
				if (databaseType.equals("mySQL")){
					sql = "select count(*) \"num_of_orphan_reqs\" " +
					" from gr_requirements r, gr_requirement_types rt" +
					" where r.folder_id = ? " +
					" and r.requirement_type_id = rt.id " +
					" and r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and (r.trace_to is null or r.trace_to = '')" +
					" and rt.can_be_orphan = 1";
				}
				else{
					sql = "select count(*) \"num_of_orphan_reqs\" " +
					" from gr_requirements r, gr_requirement_types rt" +
					" where r.folder_id = ? " +
					" and r.requirement_type_id = rt.id " +
					" and r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and (upper(to_char(r.trace_to)) is null or upper(to_char(r.trace_to)) = '')" +
					" and rt.can_be_orphan = 1";
					
				}
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,folderId);
				prepStmt2.setInt(2, requirementTypeId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfOrphanRequirements = rs2.getInt("num_of_orphan_reqs");
				}
				prepStmt2.close();
				rs2.close();
				
				

				// Total number of  requirements in SuspectUpstream status.
				int numOfSuspectUpstreamRequirements  = 0;
				
				if (databaseType.equals("mySQL")){
					sql = "select count(*) \"num_of_suspect_upstream_reqs\" " +
					" from gr_requirements r" +
					" where r.folder_id = ? " +
					" and r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and upper(r.trace_to) like '%(S)%'" ;
				}
				else{
					sql = "select count(*) \"num_of_suspect_upstream_reqs\" " +
					" from gr_requirements r" +
					" where r.folder_id = ? " +
					" and r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and upper(to_char(r.trace_to)) like '%(S)%'" ;				
				}
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,folderId);
				prepStmt2.setInt(2, requirementTypeId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfSuspectUpstreamRequirements = rs2.getInt("num_of_suspect_upstream_reqs");
				}
				prepStmt2.close();
				rs2.close();

				// Total number of  requirements in SuspectDownstream status.
				int numOfSuspectDownstreamRequirements  = 0;
				
				if (databaseType.equals("mySQL")){
					sql = "select count(*) \"num_of_suspect_downstream_reqs\" " +
					" from gr_requirements r" +
					" where r.folder_id = ? " +
					" and r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.trace_from like '%(S)%'" ;
				}
				else{
					sql = "select count(*) \"num_of_suspect_downstream_reqs\" " +
					" from gr_requirements r" +
					" where r.folder_id = ? " +
					" and r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and upper(to_char(r.trace_from)) like '%(S)%'" ;			
				}
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,folderId);
				prepStmt2.setInt(2, requirementTypeId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfSuspectDownstreamRequirements = rs2.getInt("num_of_suspect_downstream_reqs");
				}
				prepStmt2.close();
				rs2.close();			
				
				// Total number of  requirements in Completed status.
				int numOfCompletedRequirements  = 0;
				sql = "select count(*) \"num_of_completed_reqs\" " +
					" from gr_requirements r" +
					" where r.folder_id = ? " +
					" and r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.pct_complete = 100 " ;
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,folderId);
				prepStmt2.setInt(2, requirementTypeId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfCompletedRequirements = rs2.getInt("num_of_completed_reqs");
				}
				prepStmt2.close();
				rs2.close();	
				
				
				
				// Total number of  requirements in InComplet status.
				int numOfIncompleteRequirements  = 0;
				sql = "select count(*) \"num_of_incomplete_reqs\" " +
					" from gr_requirements r" +
					" where r.folder_id = ? " +
					" and r.requirement_type_id = ? " +
					" and r.deleted = 0 " +
					" and r.pct_complete <> 100 " ;
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,folderId);
				prepStmt2.setInt(2, requirementTypeId);
				rs2 = prepStmt2.executeQuery();
				while (rs2.next()) {
					numOfIncompleteRequirements = rs2.getInt("num_of_incomplete_reqs");
				}
				prepStmt2.close();
				rs2.close();				
				

				// now that we have all the values we need, lets insert them into the trend table.
				
				if (databaseType.equals("mySQL")){
					sql = "insert into gr_folder_metrics ( folder_id,project_id," +
					" requirement_type_short_name, data_load_dt, num_of_requirements, " +
					" num_of_test_pending_reqs, num_of_test_pass_reqs, num_of_test_fail_reqs, " +
					" num_of_draft_reqs, num_of_in_workflow_reqs, " +
					" num_of_rejected_reqs, num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs, " +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs) " + 
					" values (" +
					"?,?," +
					"?,str_to_date('"+ dataLoadDt +"','%d %m %y %h:%i:%s'),?," +
					"?,?,?," +
					"?,?," +
					"?,?," +
					"?,?," +
					"?,?," +
					"?,?)";
				}
				else{
					sql = "insert into gr_folder_metrics ( folder_id,project_id," +
					" requirement_type_short_name, data_load_dt, num_of_requirements, " +
					" num_of_test_pending_reqs, num_of_test_pass_reqs, num_of_test_fail_reqs, " +
					" num_of_draft_reqs, num_of_in_workflow_reqs, " +
					" num_of_rejected_reqs, num_of_approved_reqs, " +
					" num_of_dangling_reqs, num_of_orphan_reqs, " +
					" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
					" num_of_completed_reqs, num_of_incomplete_reqs) " + 
					" values (" +
					"?,?," +
					"?,to_date('"+ dataLoadDt +"','DD MON YYYY HH MI SS'),?," +
					"?,?,?," +
					"?,?," +
					"?,?," +
					"?,?," +
					"?,?," +
					"?,?)";			
				}
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1,folderId);
				prepStmt2.setInt(2,projectId);
				
				
				
				prepStmt2.setString(3, requirementTypePrefix);
				prepStmt2.setInt(4,numOfRequirements);
				
				prepStmt2.setInt(5,numOfTestPendingRequirements);
				prepStmt2.setInt(6,numOfTestPassRequirements);
				prepStmt2.setInt(7,numOfTestFailRequirements);
				
				
				prepStmt2.setInt(8,numOfDraftRequirements);
				prepStmt2.setInt(9,numOfInApprovalWorkflowRequirements);
				prepStmt2.setInt(10,numOfRejectedRequirements);
				prepStmt2.setInt(11,numOfApprovedRequirements);
				prepStmt2.setInt(12,numOfDanglingRequirements);
				prepStmt2.setInt(13,numOfOrphanRequirements);
				prepStmt2.setInt(14,numOfSuspectUpstreamRequirements);
				prepStmt2.setInt(15,numOfSuspectDownstreamRequirements);
				prepStmt2.setInt(16,numOfCompletedRequirements);
				prepStmt2.setInt(17,numOfIncompleteRequirements);
				
				prepStmt2.execute();
				prepStmt2.close();
					
			
			}

		    prepStmt.close();
			rs.close();
		}

		
		
		// for every folder object, it crunches the data and 
		// loads into gr_folder_metrics table.
		private static void fillUpSprintMetrics(java.sql.Connection con, int projectId,
			 String dataLoadDt,String databaseType) throws SQLException{

			
			
			// We want to pick up ONLY those sprints that are currenly in a SCRUM
			// i.e ONLy if today is between the sprint start date and sprint end date.
			// lets get the distinct folder Ids in this projects..
			
			// lets get the sprints which are currently in active scrum.
			String sql = "";
			
			if (databaseType.equals("mySQL")){
				sql = "select s.id, s.name" + 
				" from gr_sprints s " +
				" where s.project_id = ? " +
				" and s.start_dt < now() and now() < s.end_dt " + 
				" order by s.id "; 
			}
			else {
				sql = "select s.id, s.name" + 
				" from gr_sprints s " +
				" where s.project_id = ? " +
				" and s.start_dt < sysdate and sysdate < s.end_dt " + 
				" order by s.id "; 
			}
			
				
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			ResultSet rs = prepStmt.executeQuery();
			while(rs.next()) {
				int sprintId = rs.getInt("id");
				String sprintName  = rs.getString("name");
				// for each of these sprints, lets get all the requirement types that exist.
				// for each of the req types, we also need to get the rTAttributeId that holds the 'Agile Effort Remainign (hrs)' attribute.
				
					
				String sprintClause = "Agile Sprint:#:" + sprintName;
				sql  = "  SELECT distinct  rt.id, rt.short_name, rta.id 'effortRemainingRTAttributeId' "
					+ " FROM gr_requirements r , gr_requirement_types rt, gr_rt_attributes rta " 
					+ " where r.project_id = ? " 
					+ " and r.requirement_type_id = rt.id " 
					+ " and rt.id = rta.requirement_type_id	"
					+ " and rta.name = 'Agile Effort Remaining (hrs)'"
					+ " and r.deleted = 0  "
					+ " and r.user_defined_attributes like '%"+ sprintClause +"%'";
				
				// for each baseline, requirement type combinations, lets get some metrics.
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1, projectId);
				ResultSet rs2 = prepStmt2.executeQuery();
				while (rs2.next()){
					int requirementTypeId = rs2.getInt("id");
					String requirementTypePrefix = rs2.getString("short_name");
					int effortRemainingRTAttributeId = rs2.getInt("effortRemainingRTAttributeId");
					
					
					// for each of the sprint / req type combos, lets get the metrics and stord them in the sprintMetrics table.

					
					// Total number of requirements.
					int numOfRequirements  = 0;
					sql = "select count(*) \"num_of_requirements\" " +
						" from gr_requirements r" +
						" where  r.requirement_type_id = ? " +
						" and r.deleted = 0 " + 
						" and r.user_defined_attributes like '%"+ sprintClause +"%'";;
					PreparedStatement prepStmt3 = con.prepareStatement(sql);
					prepStmt3.setInt(1, requirementTypeId);
					ResultSet rs3 = prepStmt3.executeQuery();
					while (rs3.next()) {
						numOfRequirements = rs3.getInt("num_of_requirements");
					}
					prepStmt3.close();
					rs3.close();
				

					// Total number of  requirements in test_status='Pending'
					int numOfTestPendingRequirements  = 0;
					sql = "select count(*) \"num_of_pending_requirements\" " +
						" from gr_requirements r" +
						" where  r.requirement_type_id = ? " +
						" and r.deleted = 0 " + 
						" and r.user_defined_attributes like '%"+ sprintClause +"%'" +
						" and r.testing_status = 'Pending'";
					prepStmt3 = con.prepareStatement(sql);
					prepStmt3.setInt(1, requirementTypeId);
					rs3 = prepStmt3.executeQuery();
					while (rs3.next()) {
						numOfTestPendingRequirements = rs3.getInt("num_of_pending_requirements");
					}
					prepStmt3.close();
					rs3.close();			
					
				
				
				
				
					// Total number of  requirements in test_status='Pass'
					int numOfTestPassRequirements  = 0;
					sql = "select count(*) \"num_of_passed_requirements\" " +
						" from gr_requirements r" +
						" where  r.requirement_type_id = ? " +
						" and r.deleted = 0 " + 
						" and r.user_defined_attributes like '%"+ sprintClause +"%'" +
						" and r.testing_status = 'Pass'";
					prepStmt3 = con.prepareStatement(sql);
					prepStmt3.setInt(1, requirementTypeId);
					rs3 = prepStmt3.executeQuery();
					while (rs3.next()) {
						numOfTestPassRequirements = rs3.getInt("num_of_passed_requirements");
					}
					prepStmt3.close();
					rs3.close();			
					
					
					
					
					// Total number of  requirements in test_status='Fail'
					int numOfTestFailRequirements  = 0;
					sql = "select count(*) \"num_of_failed_requirements\" " +
						" from gr_requirements r" +
						" where  r.requirement_type_id = ? " +
						" and r.deleted = 0 " + 
						" and r.user_defined_attributes like '%"+ sprintClause +"%'" +
						" and r.testing_status = 'Fail'";
					prepStmt3 = con.prepareStatement(sql);
					prepStmt3.setInt(1, requirementTypeId);
					rs3 = prepStmt3.executeQuery();
					while (rs3.next()) {
						numOfTestFailRequirements = rs3.getInt("num_of_failed_requirements");
					}
					prepStmt3.close();
					rs3.close();			
					
					
					
					
					// Total number of  requirements in draft status.
					int numOfDraftRequirements  = 0;
					sql = "select count(*) \"num_of_draft_requirements\" " +
						" from gr_requirements r" +
						" where  r.requirement_type_id = ? " +
						" and r.deleted = 0 " + 
						" and r.user_defined_attributes like '%"+ sprintClause +"%'" +
						" and r.status = 'Draft'";
					prepStmt3 = con.prepareStatement(sql);
					prepStmt3.setInt(1, requirementTypeId);
					rs3 = prepStmt3.executeQuery();
					while (rs3.next()) {
						numOfDraftRequirements = rs3.getInt("num_of_draft_requirements");
					}
					prepStmt3.close();
					rs3.close();			
								
					// Total number of  requirements in InApprovalWorkflow status.
					int numOfInApprovalWorkflowRequirements  = 0;
					sql = "select count(*) \"num_of_inprogress_requirements\" " +
						" from gr_requirements r" +
						" where  r.requirement_type_id = ? " +
						" and r.deleted = 0 " + 
						" and r.user_defined_attributes like '%"+ sprintClause +"%'" +
						" and r.status = 'In Approval WorkFlow'";
					prepStmt3 = con.prepareStatement(sql);
					prepStmt3.setInt(1, requirementTypeId);
					rs3 = prepStmt3.executeQuery();
					while (rs3.next()) {
						numOfInApprovalWorkflowRequirements = rs3.getInt("num_of_inprogress_requirements");
					}
					prepStmt3.close();
					rs3.close();			
					

					
					// Total number of  requirements in rejected status.
					int numOfRejectedRequirements  = 0;
					sql = "select count(*) \"num_of_rejected_requirements\" " +
						" from gr_requirements r" +
						" where  r.requirement_type_id = ? " +
						" and r.deleted = 0 " + 
						" and r.user_defined_attributes like '%"+ sprintClause +"%'" +
						" and r.status = 'Rejected'";
					prepStmt3 = con.prepareStatement(sql);
					prepStmt3.setInt(1, requirementTypeId);
					rs3 = prepStmt3.executeQuery();
					while (rs3.next()) {
						numOfRejectedRequirements = rs3.getInt("num_of_rejected_requirements");
					}
					prepStmt3.close();
					rs3.close();			
					
					
					
					// Total number of  requirements in approved status.
					int numOfApprovedRequirements  = 0;
					sql = "select count(*) \"num_of_approved_requirements\" " +
						" from gr_requirements r" +
						" where  r.requirement_type_id = ? " +
						" and r.deleted = 0 " + 
						" and r.user_defined_attributes like '%"+ sprintClause +"%'" +
						" and r.status = 'Approved'";
					prepStmt3 = con.prepareStatement(sql);
					prepStmt3.setInt(1, requirementTypeId);
					rs3 = prepStmt3.executeQuery();
					while (rs3.next()) {
						numOfApprovedRequirements = rs3.getInt("num_of_approved_requirements");
					}
					prepStmt3.close();
					rs3.close();			
					
					
				
					// Total number of  requirements in dangling status.
					int numOfDanglingRequirements  = 0;
			
					if (databaseType.equals("mySQL")){
						sql = "select count(*) \"num_of_dangling_reqs\" " +
							" from gr_requirements r, gr_requirement_types rt " +
							" where  r.requirement_type_id = rt.id and r.requirement_type_id = ? " +
							" and r.deleted = 0 " + 
							" and r.user_defined_attributes like '%"+ sprintClause +"%'" +
							" and (r.trace_from is null or r.trace_from = '')" +
							" and rt.can_be_dangling = 1";
					}
					else {
						sql = "select count(*) \"num_of_dangling_reqs\" " +
						" from gr_requirements r , gr_requirement_types rt " +
						" where  r.requirement_type_id = rt.id and  r.requirement_type_id = ? " +
						" and r.deleted = 0 " + 
						" and r.user_defined_attributes like '%"+ sprintClause +"%'" +
						" and (upper(to_char(r.trace_from)) is null or upper(to_char(r.trace_from)) = '')" +
						" and rt.can_be_dangling = 1";
					}
					
					prepStmt3 = con.prepareStatement(sql);
					prepStmt3.setInt(1, requirementTypeId);
					rs3 = prepStmt3.executeQuery();
					while (rs3.next()) {
						numOfDanglingRequirements = rs3.getInt("num_of_dangling_reqs");
					}
					prepStmt3.close();
					rs3.close();			
					
					
					
					
					// Total number of  requirements in orphan status.
					
					int numOfOrphanRequirements  = 0;
		
					if (databaseType.equals("mySQL")){
						sql = "select count(*) \"num_of_orphan_reqs\" " +
							" from gr_requirements r , gr_requirement_types rt " +
							" where  r.requirement_type_id = rt.id and    r.requirement_type_id = ? " +
							" and r.deleted = 0 " + 
							" and r.user_defined_attributes like '%"+ sprintClause +"%'" +
							" and (r.trace_to is null or r.trace_to = '')" +
							" and rt.can_be_orphan = 1";
					}
					else {
						sql = "select count(*) \"num_of_orphan_reqs\" " +
						" from gr_requirements r , gr_requirement_types rt " +
						" where  r.requirement_type_id = rt.id and    r.requirement_type_id = ? " +
						" and r.deleted = 0 " + 
						" and r.user_defined_attributes like '%"+ sprintClause +"%'" +
						" and (upper(to_char(r.trace_to)) is null or upper(to_char(r.trace_to)) = '')" +
						" and rt.can_be_orphan = 1";
					}
					
					prepStmt3 = con.prepareStatement(sql);
					prepStmt3.setInt(1, requirementTypeId);
					rs3 = prepStmt3.executeQuery();
					while (rs3.next()) {
						numOfOrphanRequirements = rs3.getInt("num_of_orphan_reqs");
					}
					prepStmt3.close();
					rs3.close();			
					
					
		
					// Total number of  requirements in SuspectUpstream status.
					int numOfSuspectUpstreamRequirements  = 0;
					if (databaseType.equals("mySQL")){
						sql = "select count(*) \"num_of_suspect_upstream_reqs\" " +
							" from gr_requirements r" +
							" where  r.requirement_type_id = ? " +
							" and r.deleted = 0 " + 
							" and r.user_defined_attributes like '%"+ sprintClause +"%'" +
							" and upper(r.trace_to) like '%(S)%'" ;
					}
					else {
						sql = "select count(*) \"num_of_suspect_upstream_reqs\" " +
						" from gr_requirements r" +
						" where  r.requirement_type_id = ? " +
						" and r.deleted = 0 " + 
						" and r.user_defined_attributes like '%"+ sprintClause +"%'" +
						" and upper(to_char(r.trace_to)) like '%(S)%'" ;				
					}
					
					prepStmt3 = con.prepareStatement(sql);
					prepStmt3.setInt(1, requirementTypeId);
					rs3 = prepStmt3.executeQuery();
					while (rs3.next()) {
						numOfSuspectUpstreamRequirements = rs3.getInt("num_of_suspect_upstream_reqs");
					}
					prepStmt3.close();
					rs3.close();			
					
					
					
					
					
					// Total number of  requirements in SuspectDownstream status.
					int numOfSuspectDownstreamRequirements  = 0;
					if (databaseType.equals("mySQL")){
						sql = "select count(*) \"num_of_suspect_downstream_reqs\" " +
							" from gr_requirements r" +
							" where  r.requirement_type_id = ? " +
							" and r.deleted = 0 " + 
							" and r.user_defined_attributes like '%"+ sprintClause +"%'" +
							" and r.trace_from like '%(S)%'" ;
					}
					else {
						sql = "select count(*) \"num_of_suspect_downstream_reqs\" " +
						" from gr_requirements r" +
						" where  r.requirement_type_id = ? " +
						" and r.deleted = 0 " + 
						" and r.user_defined_attributes like '%"+ sprintClause +"%'" +
						" and upper(to_char(r.trace_from)) like '%(S)%'" ;			
					}
					
					prepStmt3 = con.prepareStatement(sql);
					prepStmt3.setInt(1, requirementTypeId);
					rs3 = prepStmt3.executeQuery();
					while (rs3.next()) {
						numOfSuspectDownstreamRequirements = rs3.getInt("num_of_suspect_downstream_reqs");
					}
					prepStmt3.close();
					rs3.close();				
					
					
					// Total number of  requirements in Completed status.
					int numOfCompletedRequirements  = 0;
					
					sql = "select count(*) \"num_of_completed_reqs\" " +
						" from gr_requirements r" +
						" where  r.requirement_type_id = ? " +
						" and r.deleted = 0 " + 
						" and r.user_defined_attributes like '%"+ sprintClause +"%'" +
						" and r.pct_complete = 100 " ;
					
					prepStmt3 = con.prepareStatement(sql);
					prepStmt3.setInt(1, requirementTypeId);
					rs3 = prepStmt3.executeQuery();
					while (rs3.next()) {
						numOfCompletedRequirements = rs3.getInt("num_of_completed_reqs");
					}
					prepStmt3.close();
					rs3.close();				
					
					
					// Total number of  requirements in InComplet status.
					int numOfIncompleteRequirements  = 0;
					sql = "select count(*) \"num_of_incomplete_reqs\" " +
						" from gr_requirements r" +
						" where  r.requirement_type_id = ? " +
						" and r.deleted = 0 " + 
						" and r.user_defined_attributes like '%"+ sprintClause +"%'" +
						" and r.pct_complete <> 100 " ;
					
					prepStmt3 = con.prepareStatement(sql);
					prepStmt3.setInt(1, requirementTypeId);
					rs3 = prepStmt3.executeQuery();
					while (rs3.next()) {
						numOfIncompleteRequirements = rs3.getInt("num_of_incomplete_reqs");
					}
					prepStmt3.close();
					rs3.close();				
		
					// Sum of effort remaining
					int sumOfTaskEffortRemaining = 0;
					sql = "select  rav.value 'effortRemaining'  " +
						" from gr_requirements r, gr_r_attribute_values rav" +
						" where  r.requirement_type_id = ? " +
						" and r.deleted = 0 " + 
						" and r.user_defined_attributes like '%"+ sprintClause +"%'" +
						" and r.id = rav.requirement_id " +
						" and rav.attribute_id = " + effortRemainingRTAttributeId;
					
					prepStmt3 = con.prepareStatement(sql);
					prepStmt3.setInt(1, requirementTypeId);
					rs3 = prepStmt3.executeQuery();
					
					while (rs3.next()) {
						String effortRemainingString = rs3.getString("effortRemaining");
						
						int effortRemaining = 0;
						try {
							if (effortRemainingString != null){
								effortRemaining = Integer.parseInt(effortRemainingString);
							}
						}
						catch (Exception e){
							effortRemaining = 0;
						}
						
						sumOfTaskEffortRemaining = sumOfTaskEffortRemaining + effortRemaining;
					}
					
					prepStmt3.close();
					rs3.close();	
					
					
					// now that we have all the values we need, lets insert them into the trend table.
					
					if (databaseType.equals("mySQL")){
						sql = "insert into gr_sprint_metrics ( sprint_id,project_id," +
						" requirement_type_short_name, data_load_dt, num_of_requirements, " +
						" num_of_test_pending_reqs, num_of_test_pass_reqs, num_of_test_fail_reqs, " +
						" num_of_draft_reqs, num_of_in_workflow_reqs, " +
						" num_of_rejected_reqs, num_of_approved_reqs, " +
						" num_of_dangling_reqs, num_of_orphan_reqs, " +
						" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
						" num_of_completed_reqs, num_of_incomplete_reqs, sum_of_task_effort_remaining) " + 
						" values (" +
						"?,?," +
						"?,str_to_date('"+ dataLoadDt +"','%d %m %y %h:%i:%s'),?," +
						"?,?,?," +
						"?,?," +
						"?,?," +
						"?,?," +
						"?,?," +
						"?,?, ?)";
					}
					else{
						sql = "insert into gr_sprint_metrics ( sprint_id,project_id," +
						" requirement_type_short_name, data_load_dt, num_of_requirements, " +
						" num_of_test_pending_reqs, num_of_test_pass_reqs, num_of_test_fail_reqs, " +
						" num_of_draft_reqs, num_of_in_workflow_reqs, " +
						" num_of_rejected_reqs, num_of_approved_reqs, " +
						" num_of_dangling_reqs, num_of_orphan_reqs, " +
						" num_of_suspect_upstream_reqs, num_of_suspect_downstream_reqs, " +
						" num_of_completed_reqs, num_of_incomplete_reqs, sum_of_task_effort_remaining) " + 
						" values (" +
						"?,?," +
						"?,to_date('"+ dataLoadDt +"','DD MON YYYY HH MI SS'),?," +
						"?,?,?," +
						"?,?," +
						"?,?," +
						"?,?," +
						"?,?," +
						"?,?, ? )";			
					}
					prepStmt3 = con.prepareStatement(sql);
					prepStmt3.setInt(1,sprintId);
					prepStmt3.setInt(2,projectId);
					
					
					
					prepStmt3.setString(3, requirementTypePrefix);
					prepStmt3.setInt(4,numOfRequirements);
					
					prepStmt3.setInt(5,numOfTestPendingRequirements);
					prepStmt3.setInt(6,numOfTestPassRequirements);
					prepStmt3.setInt(7,numOfTestFailRequirements);
					
					
					prepStmt3.setInt(8,numOfDraftRequirements);
					prepStmt3.setInt(9,numOfInApprovalWorkflowRequirements);
					prepStmt3.setInt(10,numOfRejectedRequirements);
					prepStmt3.setInt(11,numOfApprovedRequirements);
					prepStmt3.setInt(12,numOfDanglingRequirements);
					prepStmt3.setInt(13,numOfOrphanRequirements);
					prepStmt3.setInt(14,numOfSuspectUpstreamRequirements);
					prepStmt3.setInt(15,numOfSuspectDownstreamRequirements);
					prepStmt3.setInt(16,numOfCompletedRequirements);
					prepStmt3.setInt(17,numOfIncompleteRequirements);
					prepStmt3.setInt(18,sumOfTaskEffortRemaining);
					
					prepStmt3.execute();
					prepStmt3.close();
				
				}
				prepStmt2.close();
				rs2.close();
			}
		    prepStmt.close();
			rs.close();
		}

		
		
		// we populate metrics about defects in this project.
		// we normally crunch metrics based on a Defects Status Groups. 
		// since 'Defects Status' is a custom attribute of the Defects req type
		// we need to make sure there is a ReqType called Defects and that 
		// there is an attribute called DefectsStatus 
		private static void fillUpDefectMetrics(java.sql.Connection con, int projectId,
			 String dataLoadDt,String databaseType) throws SQLException{
			// we need to make sure that the defect status grouping is up to date with defect status
			// but we shouldn't lose any old mapping info.
			
			updateDefectStatusGrouping(con,projectId, databaseType);

			// lets get project defects metrics.
			String sql = "";
			
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_defect_metrics " +
				" (metrics_type, project_id, " +
				" data_load_dt, defect_status_group, num_of_requirements )" +
				" select \"project\", r.project_id, " +
				" str_to_date('"+ dataLoadDt + "','%d %m %y %h:%i:%s'), " +
				" dsg.defect_status_group, count(*) " +
				" from gr_requirements r, gr_r_attribute_values rav, gr_rt_attributes rta, gr_requirement_types rt, gr_defect_status_grouping dsg "+ 
				" where rav.requirement_id = r.id " +
				" and rav.attribute_id = rta.id " +
				" and rta.name ='Defect Status' "+
				" and rta.requirement_type_id = rt.id "+
				" and rt.name='Defects' "+
				" and rt.project_id = r.project_id "+
				" and r.project_id = ? " +
				" and dsg.project_id = r.project_id " +
				" and dsg.defect_status = rav.value " +
				" group by r.project_id, dsg.defect_status_group ";
			}
			else{
				sql = " insert into gr_defect_metrics " +
				" (metrics_type, project_id, " +
				" data_load_dt, defect_status_group, num_of_requirements )" +
				" select 'project', r.project_id, " +
				" to_date('"+ dataLoadDt + "','DD MON YYYY HH MI SS'), " +
				" dsg.defect_status_group, count(*) " +
				" from gr_requirements r, gr_r_attribute_values rav, gr_rt_attributes rta, gr_requirement_types rt, gr_defect_status_grouping dsg "+ 
				" where rav.requirement_id = r.id " +
				" and rav.attribute_id = rta.id " +
				" and rta.name ='Defect Status' "+
				" and rta.requirement_type_id = rt.id "+
				" and rt.name='Defects' "+
				" and rt.project_id = r.project_id "+
				" and r.project_id = ? " +
				" and dsg.project_id = r.project_id " +
				" and dsg.defect_status = rav.value " +
				" group by r.project_id, dsg.defect_status_group ";		
			}
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.execute();
			prepStmt.close();

			
			// lets get release defects metrics.
			
			
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_defect_metrics " +
				" (metrics_type, project_id,release_id, release_full_tag, " +
				" data_load_dt, defect_status_group, num_of_requirements )" +
				" select \"release\", r.project_id, rr.release_id, rr.release_full_tag, " +
				" str_to_date('"+ dataLoadDt + "','%d %m %y %h:%i:%s'), " +
				" dsg.defect_status_group, count(*) " +
				" from " +
				" gr_requirements r, gr_r_attribute_values rav, " +
				" gr_rt_attributes rta, gr_requirement_types rt, " +
				" gr_defect_status_grouping dsg , gr_release_requirements rr "+ 
				" where rav.requirement_id = r.id " +
				" and rav.attribute_id = rta.id " +
				" and rta.name ='Defect Status' "+
				" and rta.requirement_type_id = rt.id "+
				" and rt.name='Defects' "+
				" and rt.project_id = r.project_id "+
				" and r.project_id = ? " +
				" and r.id = rr.requirement_id " +
				" and dsg.project_id = r.project_id " +
				" and dsg.defect_status = rav.value " +
				" group by r.project_id, rr.release_id, rr.release_full_tag, dsg.defect_status_group ";
			}
			else{
				sql = " insert into gr_defect_metrics " +
				" (metrics_type, project_id,release_id, release_full_tag, " +
				" data_load_dt, defect_status_group, num_of_requirements )" +
				" select 'release', r.project_id, rr.release_id, rr.release_full_tag, " +
				" to_date('"+ dataLoadDt + "','DD MON YYYY HH MI SS'), " +
				" dsg.defect_status_group, count(*) " +
				" from " +
				" gr_requirements r, gr_r_attribute_values rav, " +
				" gr_rt_attributes rta, gr_requirement_types rt, " +
				" gr_defect_status_grouping dsg , gr_release_requirements rr "+ 
				" where rav.requirement_id = r.id " +
				" and rav.attribute_id = rta.id " +
				" and rta.name ='Defect Status' "+
				" and rta.requirement_type_id = rt.id "+
				" and rt.name='Defects' "+
				" and rt.project_id = r.project_id "+
				" and r.project_id = ? " +
				" and r.id = rr.requirement_id " +
				" and dsg.project_id = r.project_id " +
				" and dsg.defect_status = rav.value " +
				" group by r.project_id, rr.release_id, rr.release_full_tag, dsg.defect_status_group ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.execute();
			prepStmt.close();
		
			
			// lets get user defects metrics.
			
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_defect_metrics " +
				" (metrics_type, project_id, user_id, " +
				" data_load_dt, defect_status_group, num_of_requirements )" +
				" select \"user\", r.project_id, u.id, " +
				" str_to_date('"+ dataLoadDt + "','%d %m %y %h:%i:%s'), " +
				" dsg.defect_status_group, count(*) " +
				" from gr_requirements r, gr_r_attribute_values rav, " +
				" gr_rt_attributes rta, gr_requirement_types rt, " +
				" gr_defect_status_grouping dsg , gr_users u "+ 
				" where rav.requirement_id = r.id " +
				" and rav.attribute_id = rta.id " +
				" and rta.name ='Defect Status' "+
				" and rta.requirement_type_id = rt.id "+
				" and rt.name='Defects' "+
				" and rt.project_id = r.project_id "+
				" and r.project_id = ? " +
				" and r.owner = u.email_id " +
				" and dsg.project_id = r.project_id " +
				" and dsg.defect_status = rav.value " +
				" group by r.project_id, u.id, dsg.defect_status_group ";
			}
			else{
				sql = " insert into gr_defect_metrics " +
				" (metrics_type, project_id, user_id, " +
				" data_load_dt, defect_status_group, num_of_requirements )" +
				" select 'user', r.project_id, u.id, " +
				" to_date('"+ dataLoadDt + "','DD MON YYYY HH MI SS'), " +
				" dsg.defect_status_group, count(*) " +
				" from gr_requirements r, gr_r_attribute_values rav, " +
				" gr_rt_attributes rta, gr_requirement_types rt, " +
				" gr_defect_status_grouping dsg , gr_users u "+ 
				" where rav.requirement_id = r.id " +
				" and rav.attribute_id = rta.id " +
				" and rta.name ='Defect Status' "+
				" and rta.requirement_type_id = rt.id "+
				" and rt.name='Defects' "+
				" and rt.project_id = r.project_id "+
				" and r.project_id = ? " +
				" and r.owner = u.email_id " +
				" and dsg.project_id = r.project_id " +
				" and dsg.defect_status = rav.value " +
				" group by r.project_id, u.id, dsg.defect_status_group ";
			}
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.execute();
			prepStmt.close();

			
			// lets get baseline defects metrics.
			
			
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_defect_metrics " +
				" (metrics_type, project_id, rt_baseline_id , " +
				" data_load_dt, defect_status_group, num_of_requirements )" +
				" select \"baseline\", r.project_id, rb.rt_baseline_id,  " +
				" str_to_date('"+ dataLoadDt + "','%d %m %y %h:%i:%s'), " +
				" dsg.defect_status_group, count(*) " +
				" from gr_requirements r, gr_r_attribute_values rav, " +
				" gr_rt_attributes rta, gr_requirement_types rt, " +
				" gr_defect_status_grouping dsg , gr_requirement_baselines rb "+ 
				" where rav.requirement_id = r.id " +
				" and rav.attribute_id = rta.id " +
				" and rta.name ='Defect Status' "+
				" and rta.requirement_type_id = rt.id "+
				" and rt.name='Defects' "+
				" and rt.project_id = r.project_id "+
				" and r.project_id = ? " +
				" and r.id = rb.requirement_id " +
				" and dsg.project_id = r.project_id " +
				" and dsg.defect_status = rav.value " +
				" group by r.project_id, rb.rt_baseline_id , dsg.defect_status_group ";
			}
			else{
				sql = " insert into gr_defect_metrics " +
				" (metrics_type, project_id, rt_baseline_id , " +
				" data_load_dt, defect_status_group, num_of_requirements )" +
				" select 'baseline', r.project_id, rb.rt_baseline_id,  " +
				" to_date('"+ dataLoadDt + "','DD MON YYYY HH MI SS'), " +
				" dsg.defect_status_group, count(*) " +
				" from gr_requirements r, gr_r_attribute_values rav, " +
				" gr_rt_attributes rta, gr_requirement_types rt, " +
				" gr_defect_status_grouping dsg , gr_requirement_baselines rb "+ 
				" where rav.requirement_id = r.id " +
				" and rav.attribute_id = rta.id " +
				" and rta.name ='Defect Status' "+
				" and rta.requirement_type_id = rt.id "+
				" and rt.name='Defects' "+
				" and rt.project_id = r.project_id "+
				" and r.project_id = ? " +
				" and r.id = rb.requirement_id " +
				" and dsg.project_id = r.project_id " +
				" and dsg.defect_status = rav.value " +
				" group by r.project_id, rb.rt_baseline_id , dsg.defect_status_group ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.execute();
			prepStmt.close();

			// lets get folder defects metrics.
			
			
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_defect_metrics " +
				" (metrics_type, project_id, folder_id , " +
				" data_load_dt, defect_status_group, num_of_requirements )" +
				" select \"folder\", r.project_id, r.folder_id ,  " +
				" str_to_date('"+ dataLoadDt + "','%d %m %y %h:%i:%s'), " +
				" dsg.defect_status_group, count(*) " +
				" from gr_requirements r, gr_r_attribute_values rav, " +
				" gr_rt_attributes rta, gr_requirement_types rt, " +
				" gr_defect_status_grouping dsg "+ 
				" where rav.requirement_id = r.id " +
				" and rav.attribute_id = rta.id " +
				" and rta.name ='Defect Status' "+
				" and rta.requirement_type_id = rt.id "+
				" and rt.name='Defects' "+
				" and rt.project_id = r.project_id "+
				" and r.project_id = ? " +
				" and dsg.project_id = r.project_id " +
				" and dsg.defect_status = rav.value " +
				" group by r.project_id, r.folder_id , dsg.defect_status_group ";
			}
			else{
				sql = " insert into gr_defect_metrics " +
				" (metrics_type, project_id, folder_id , " +
				" data_load_dt, defect_status_group, num_of_requirements )" +
				" select 'folder', r.project_id, r.folder_id ,  " +
				" to_date('"+ dataLoadDt + "','DD MON YYYY HH MI SS'), " +
				" dsg.defect_status_group, count(*) " +
				" from gr_requirements r, gr_r_attribute_values rav, " +
				" gr_rt_attributes rta, gr_requirement_types rt, " +
				" gr_defect_status_grouping dsg "+ 
				" where rav.requirement_id = r.id " +
				" and rav.attribute_id = rta.id " +
				" and rta.name ='Defect Status' "+
				" and rta.requirement_type_id = rt.id "+
				" and rt.name='Defects' "+
				" and rt.project_id = r.project_id "+
				" and r.project_id = ? " +
				" and dsg.project_id = r.project_id " +
				" and dsg.defect_status = rav.value " +
				" group by r.project_id, r.folder_id , dsg.defect_status_group ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.execute();
			prepStmt.close();
				
			
		}





		// we normally crunch metrics based on a Defects Status Groups. 
		// since 'Defects Status' is a custom attribute of the Defects req type
		// we need to make sure there is a ReqType called Defects and that 
		// there is an attribute called DefectsStatus
		// NOTE : A clone of this exists in the projectUtils file
		// and is run every time someone tries to map the defectstatusgroupings in the admin tool.
		private static void updateDefectStatusGrouping(java.sql.Connection con, int projectId,String databaseType) 
			throws SQLException{
			// we need to make sure that the defect status grouping is up to date with defect status
			// but we shouldn't lose any old mapping info.
			
			// so step 1 is to build an array list of current status.
			String sql = "select options " +
				" from gr_rt_attributes rta, gr_requirement_types rt " +
				" where rta.requirement_type_id = rt.id " +
				" and rta.name = 'Defect Status' " +
				" and rt.name ='Defects' " +
				" and rt.project_id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			ResultSet rs = prepStmt.executeQuery();
			String defectStatusString = "";
			while (rs.next()){
				defectStatusString = rs.getString("options");
			}
			rs.close();
			prepStmt.close();
			
			
			if ((defectStatusString != null ) && (!defectStatusString.equals(""))){
				// this project has some defect status values. so lets crunch them.
				String[] status = null;
				if (defectStatusString.contains(",")){
					status = defectStatusString.split(",");
				}
				else {
					status = new String[1];
					status[0] = defectStatusString;
				}
				
				// at this point the Status string array has all the Defect Statuses.
				for (int i=0; i<status.length; i++) {
					String currentStatus = status[i];
					// if current status is not int this project's status groupings , lets add it.
					sql = "select count(*) \"matches\" " +
					 " from gr_defect_status_grouping " +
					 " where project_id = ?  and defect_status = ? ";
					prepStmt = con.prepareStatement(sql);
					prepStmt.setInt(1, projectId);
					prepStmt.setString(2, currentStatus);
					rs = prepStmt.executeQuery();
					int matches = 0;
					while (rs.next()){
						matches = rs.getInt("matches");
					}
					if (matches == 0){
						// this current defects status is not in the gr_defects_status table. 
						// so lets insert it
						sql = " insert into gr_defect_status_grouping " +
							" (project_id, defect_status,defect_status_group) " +
							" values (?,?,?) ";
						PreparedStatement prepStmt2 = con.prepareStatement(sql);
						prepStmt2.setInt(1, projectId);
						prepStmt2.setString(2, currentStatus);
						prepStmt2.setString(3, currentStatus);
						prepStmt2.execute();
						prepStmt2.close();
					}
					rs.close();
					prepStmt.close();
				}
				
				// at this point gr_defect_status_group has all the values in teh current defects status field
				// but the values that were there previously , but no longer there will need to be removed.
				String validStatusSQLString = "";
				for (int i=0; i<status.length; i++) {
					validStatusSQLString += "'" + status[i] + "',";
				}
				// lets drop the last ,
				if (validStatusSQLString.contains(",")){
					validStatusSQLString = (String) validStatusSQLString.subSequence(0,validStatusSQLString.lastIndexOf(","));
				}			
				// lets remove any entries in the gr_defect_status group that aren't in teh valid status sql string
				sql = "delete from gr_defect_status_grouping " +
					" where project_id = ?  " +
					" and defect_status not in ("+ validStatusSQLString  +")";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.execute();
			}
			else {
				// this project doesn't have a defects status defined. so lets drop them.
				sql = "delete from gr_defect_status_grouping where project_id = ? ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, projectId);
				prepStmt.execute();
				prepStmt.close();
			}
			
		}		
		
		private static boolean isMetricsBusy(java.sql.Connection con, int projectId,String databaseType) throws SQLException{
				boolean isBusy = false;
				
				System.out.println("srt checking isMetricsBusy at " + Calendar.getInstance().getTime() );
				try {
				String sql = "select count(*) 'matches' " +
					" from gr_system  " +
					" where label = ? and value = ?  ";
				
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, "runningProjectMetrics" );
				 
				prepStmt.setInt(2, -1 );
				
				ResultSet rs = prepStmt.executeQuery();
				int matches = 0;
				while (rs.next()){
					matches = rs.getInt("matches");
				}
				
				if (matches > 0 ){
					isBusy = true;
				}
				rs.close();
				prepStmt.close();
				}
				catch (Exception e){
					isBusy = true;
					e.printStackTrace();
				}
				
				return (isBusy);
			}		
			
    	
}
