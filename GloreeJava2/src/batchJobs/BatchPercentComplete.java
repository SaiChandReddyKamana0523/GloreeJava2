package batchJobs;


import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

/*
 * 
 * It also crunches through all the data to figure out which Requiremetns have been tested.
 */
public class BatchPercentComplete{

	public static void main(String[] args) {
		java.sql.Connection con = null;
		
		// this program expects some input parameters. if they are empty, then it can not run
		// eg : userName, passWord
		String databaseType = "";
		String userName = "";
		String password = "";
		String debug = "no";
		
		
   		 for (int i = 0; i < args.length; i++){
	         String inputParam = args[i];
	         if (inputParam.contains("databaseType:")){
	        	 databaseType = inputParam.replace("databaseType:", "");
	         }
	         if (inputParam.contains("userName:")){
	        	 userName = inputParam.replace("userName:", "");
	         }
	         if (inputParam.contains("password:")){
	        	 password = inputParam.replace("password:", "");
	         }
	         
	         if (inputParam.contains("debug:")){
	        	 debug = inputParam.replace("debug:", "");
	         }
	         
	     } 
	    
   		 
   		 String correctSyntax = "BatchTestingStatus databaseType:mySQL userName:dbuserId password:dbPassword \n or \n "+
		 "BatchTestingStatus databaseType:oracle userName:dbuserId password:dbPassword " ;
   		
   		
		 if (databaseType.equals("")){
			 System.out.println("Error : databaseType is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 
		 if (userName.equals("")){
			 System.out.println("Error : userName is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 if (password.equals("")){
			 System.out.println("Error : password is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		
		 if (debug.toLowerCase().contains("yes")){
			 debug = "yes";
		 }
		 else {
			 debug = "no";
		 }

		System.out.println("debug is " + debug + ";");
		try {
    		Calendar cal = Calendar.getInstance();
		    // get a db connection.
			// options for this are mySQL , oracle
			try {
				if (databaseType.equals("mySQL")){
			       String url = "jdbc:mysql://localhost/gloree";
			       Class.forName ("com.mysql.jdbc.Driver").newInstance ();
			       con = DriverManager.getConnection (url, userName, password);
			       System.out.println ("Database connection established");
				}
				else{
			       String url = "jdbc:oracle:thin:@( DESCRIPTION= (ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521)) (CONNECT_DATA=(SERVICE_NAME=XE)) )";
			       Class.forName ("oracle.jdbc.driver.OracleDriver").newInstance ();
			       con = DriverManager.getConnection (url, userName, password);
			       System.out.println ("Database connection established");
				}
		    }
		    catch (Exception e) {
		    		e.printStackTrace();
		        	System.err.println ("Cannot connect to database server");
		    }

		    // lets see if the prev run is still going on. we don't want to start a new run, if the prev one hasn't completed.
		    String sql = " select value from gr_system where label ='BatchPercentCompleteRunning' ";
		    PreparedStatement prepStmt = con.prepareStatement(sql);
		    ResultSet rs = prepStmt.executeQuery();
		    String batchPercentCompleteRunningFlag = "";
		    while (rs.next()) {
		    	batchPercentCompleteRunningFlag = rs.getString("value");
		    }
		    rs.close();
		    prepStmt.close();
		    
		    if ((batchPercentCompleteRunningFlag != null ) && (!(batchPercentCompleteRunningFlag.equals("")))){
		    	// there is  value in batchTestingStatusRunningFlag i.e. the prev job is still running.
		    	System.out.println("The Previous job is still running. Hence exiting.");
		    	return;
		    }
		    
		    // other wise, the prev job is not running and we can kick off asap. lets get a lock.
		    sql = " insert into gr_system (label, value) values ('BatchPercentCompleteRunning','Locked') ";
		    prepStmt = con.prepareStatement(sql);
		    prepStmt.execute();
		    prepStmt.close();
		    
	    	// this job is not incremental. It clears the slate and sets the testing status in one shot. 
		    
		    // lets get the list of projects in the system.
		    sql = "select id, name, pct_complete_driver_requirement_type_id " + 
			" from gr_projects  " +
			" where pct_complete_driver_requirement_type_id > 0 "; 
			
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
		    while (rs.next()) {
		    	// for each project in the system, we will calculate the Project, Release, Baseline and Folder trends.

	    		
		    	int projectId = rs.getInt("id");
		    	String projectName = rs.getString("name");
		    	int percentCompleteDriverReqTypeId = rs.getInt("pct_complete_driver_requirement_type_id");
		    
		    	System.out.println("Starting fillUpTestingStatus for project " + projectName + " at " + cal.getTime());
		    	// lets fill up the Testing Status information 
		    	fillUpPercentComplete(con,projectId,percentCompleteDriverReqTypeId, databaseType, debug);
		    	System.out.println("Finished fillUpTestingStatus for project " + projectName + " at " + cal.getTime());
		    }
			
		    // lets delete the lock.
		    // other wise, the prev job is not running and we can kick off asap. lets get a lock.
		    sql = " delete from gr_system where label = 'BatchPercentCompleteRunning'";
		    prepStmt = con.prepareStatement(sql);
		    prepStmt.execute();
		    prepStmt.close();
		    
		    con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try {
				String sql = " delete from gr_system where label = 'BatchTestingStatusRunning'";
			    PreparedStatement prepStmt = con.prepareStatement(sql);
			    prepStmt.execute();
			    prepStmt.close();
				}
				catch (Exception e) {
					
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


		if (debug.equals("yes")){
			System.out.println("debug :  reqId  " +  requirementId + ":" + reqFullTag  + " average percent complete is " + avgPctComplete);
		}
		return;
	
	}
	
}
