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
public class BatchTestingStatus{

	public static void main(String[] args) {
		java.sql.Connection con = null;
		
		// this program expects some input parameters. if they are empty, then it can not run
		// eg : userName, passWord
		String databaseType = "";
		String userName = "";
		String password = "";
		String debug = "no";
		
		// SRT remove
		//databaseType = "mySQL";
		//userName = "gloree";
		//password = "SpecialGloree1";
		// SRT remove
		
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
		    String sql = " select value from gr_system where label ='BatchTestingStatusRunning' ";
		    PreparedStatement prepStmt = con.prepareStatement(sql);
		    ResultSet rs = prepStmt.executeQuery();
		    String batchTestingStatusRunningFlag = "";
		    while (rs.next()) {
		    	batchTestingStatusRunningFlag = rs.getString("value");
		    }
		    rs.close();
		    prepStmt.close();
		    
		    if ((batchTestingStatusRunningFlag != null ) && (!(batchTestingStatusRunningFlag.equals("")))){
		    	// there is  value in batchTestingStatusRunningFlag i.e. the prev job is still running.
		    	System.out.println("The Previous job is still running. Hence exiting.");
		    	return;
		    }
		    
		    // other wise, the prev job is not running and we can kick off asap. lets get a lock.
		    sql = " insert into gr_system (label, value) values ('BatchTestingStatusRunning','Locked') ";
		    prepStmt = con.prepareStatement(sql);
		    prepStmt.execute();
		    prepStmt.close();
		    
	    	// this job is not incremental. It clears the slate and sets the testing status in one shot. 
		    
		    // lets get the list of projects in the system.
		    sql = "select id, name, billing_organization_id " + 
			" from gr_projects  "; 
			
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
		    while (rs.next()) {
		    	// for each project in the system, we will calculate the Project, Release, Baseline and Folder trends.

	    		
		    	int projectId = rs.getInt("id");
		    	String projectName = rs.getString("name");
		    	int billingOrgId = rs.getInt("billing_organization_id");

		    	boolean projectHasProjectLicense = false;
		    	if (billingOrgId > 0 ){
		    		projectHasProjectLicense = true;
		    	}
		    	
		    	// lets see if this project has at least one valid user
		    	boolean projectHasAValidUser = false;
		    	int numberOfValidUsers = 0;
		    	String sql2 = "";
		    	if (databaseType.equals("mySQL")){
		    		sql2 = "select count(*) 'number_of_valid_users' " +
			    			" from gr_projects p, gr_roles r, gr_user_roles ur, gr_users u " +
			    			" where p.id = ? " + 
			    			" and p.id = r.project_id " + 
			    			" and p.id = ur.project_id " +
			    			" and ur.user_id = u.id " +
			    			" and (u.user_type = 'readOnly' or u.user_type='readWrite' or (u.user_type='trial' and account_expire_dt > now()))";
			    	
				}
				else{
					sql2 = "select count(*) 'number_of_valid_users' " +
			    			" from gr_projects p, gr_roles r, gr_user_roles ur, gr_users u " +
			    			" where p.id = ? " + 
			    			" and p.id = r.project_id " + 
			    			" and p.id = ur.project_id " +
			    			" and ur.user_id = u.id " +
			    			" and (u.user_type = 'readOnly' or u.user_type='readWrite' or (u.user_type='trial' and account_expire_dt > sysdate))";
			    	
				}
		    	
		    	PreparedStatement prepStmt2 = con.prepareStatement(sql2);
		    	prepStmt2.setInt(1, projectId);
				ResultSet rs2 = prepStmt2.executeQuery();
			    while (rs2.next()) {
			    	numberOfValidUsers = rs2.getInt("number_of_valid_users");
			    }
			    
			    if (numberOfValidUsers > 0 ){
			    	projectHasAValidUser = true;
			    }
			    
			    if (!(projectHasAValidUser || projectHasProjectLicense)){
			    	//System.out.println( projectName + " : Has Neither a valid user nor a valid project license. Will not crunch metrics.");
			    	continue;
			    }
			    else {
			    	//System.out.println(projectName + " : Yes, we can crunch metrics. Valid Project License or Valid User exists");
			    	// lets make sure that in the last week, there are at least some changes to TR. 
			    	sql  = " select count(*) numberOfEvents "
			    			+ " from gr_requirement_log  rl, gr_requirements r, gr_requirement_types rt  "
			    			+ " where rl.action_dt > date_sub(now(), INTERVAL 7 DAY) "
			    			+ " and  rl.requirement_id = r.id "
			    			+ " and r.requirement_type_id = rt.id "
			    			+ " and rt.short_name = 'TR' "
			    			+ " and r.project_id = ? ";
			    	prepStmt = con.prepareStatement(sql);
			    	prepStmt.setInt(1, projectId);
				    ResultSet rs3 = prepStmt.executeQuery();
				    int numberOfEvents  = 0;
				    while (rs3.next()){
				    	numberOfEvents = rs3.getInt("numberOfEvents");
				    }
				    prepStmt.close();
				    rs3.close();
				    if (numberOfEvents==0){
				    	// No need to process as there are no Test Result events
				    	System.out.println( Calendar.getInstance().getTime() + "Don't Crunch. No events for project id " + projectId + " and Name " +  projectName );
				    	continue;
				    }
				    
			    	// srt : remove this continue button before go live.
			    }
			    
		    	Thread.sleep(50);	
		    
		    	System.out.println( Calendar.getInstance().getTime() + " Starting fillUpTestingStatus for project id " + projectId + " and Name " +  projectName );
		    	// lets fill up the Testing Status information 
		    	fillUpTestingStatus(con,projectId, databaseType, debug);
		    	System.out.println( Calendar.getInstance().getTime() + " Finished fillUpTestingStatus for project " + projectId + " and Name " + projectName );
		    }
			
		    // lets delete the lock.
		    // other wise, the prev job is not running and we can kick off asap. lets get a lock.
		    sql = " delete from gr_system where label = 'BatchTestingStatusRunning'";
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
	
	private static void fillUpTestingStatus(java.sql.Connection con, int projectId, String databaseType, String debug)
		throws SQLException{
		
		try {
			// lets drop the temp table we will use for data crunching. Since it may not exist, lets do it 
			// in  try / catch mode.
			// we could have used mySQL Temporary tables, but from debugging perspective these tables are better
			// as they will be available after the job is run.
			
			try {
				String sql = "drop table gr_testing_temp ";
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.execute();
				prepStmt.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			// lets also drop the gr_testing_temp_no_duplicates
			try {
				String sql = "drop table gr_testing_temp_no_duplicates ";
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.execute();
				prepStmt.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			
			// lets build the hierarchy tree of all requirements above TR
			// processing 1st tree_level (the TR tree_level) of the trace tree. We will continuously process a higher tree_level
			// for eight more tree_levels.
			// lets fill the gr_testing_temp table with all the TR's 
			String sql = "create table gr_testing_temp as " +
			" select r.id \"requirement_id\", 1  \"tree_level\" " +
			" from gr_requirements r , gr_requirement_types rt" +
			" where  r.requirement_type_id = rt.id " +
			" and r.project_id =   " + projectId + 
			" and rt.name = 'Test Results' ";
	
			PreparedStatement prepStmt = con.prepareStatement(sql);
		
			prepStmt.execute();
			
			
	
			// lets get the second tree_level of Requirements in the tree. These are all Parent Requirements of Level 1 requirements.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_testing_temp(requirement_id, tree_level)" +
					" select to_requirement_id , 2  \"tree_level\"" +
					" from gr_traces t, gr_testing_temp temp" +
					" where t.from_requirement_id = temp.requirement_id" +
					" and temp.tree_level = 1 ";
				}
			else{
					sql = "insert into gr_testing_temp(\"requirement_id\", \"tree_level\")" +
					" select to_requirement_id , 2 \"tree_level\" " +
					" from gr_traces t, gr_testing_temp \"temp\"" +
					" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
					" and \"temp\".\"tree_level\" = 1 ";
			}
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			// lets get the third tree_level of Requirements in the tree. These are all Parent Requirements of Level 2 requirements.
			
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_testing_temp(requirement_id, tree_level)" +
					" select to_requirement_id , 3 " +
					" from gr_traces t, gr_testing_temp temp" +
					" where t.from_requirement_id = temp.requirement_id" +
					" and temp.tree_level = 2 ";
				}
			else{
					sql = "insert into gr_testing_temp(\"requirement_id\", \"tree_level\")" +
					" select to_requirement_id , 3 \"tree_level\" " +
					" from gr_traces t, gr_testing_temp \"temp\"" +
					" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
					" and \"temp\".\"tree_level\" = 2 ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			
			// lets get the fourth tree_level of Requirements in the tree. These are all Parent Requirements of Level 3 requirements.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_testing_temp(requirement_id, tree_level)" +
					" select to_requirement_id , 4 " +
					" from gr_traces t, gr_testing_temp temp" +
					" where t.from_requirement_id = temp.requirement_id" +
					" and temp.tree_level = 3 ";
				}
			else{
					sql = "insert into gr_testing_temp(\"requirement_id\", \"tree_level\")" +
					" select to_requirement_id , 4 \"tree_level\" " +
					" from gr_traces t, gr_testing_temp \"temp\"" +
					" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
					" and \"temp\".\"tree_level\" = 3 ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			
			// lets get the fifth tree_level of Requirements in the tree. These are all Parent Requirements of Level 4 requirements.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_testing_temp(requirement_id, tree_level)" +
					" select to_requirement_id , 5 " +
					" from gr_traces t, gr_testing_temp temp" +
					" where t.from_requirement_id = temp.requirement_id" +
					" and temp.tree_level = 4 ";
				}
			else{
					sql = "insert into gr_testing_temp(\"requirement_id\", \"tree_level\")" +
					" select to_requirement_id , 5 \"tree_level\" " +
					" from gr_traces t, gr_testing_temp \"temp\"" +
					" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
					" and \"temp\".\"tree_level\" = 4 ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
	
			
			// lets get the sixth tree_level of Requirements in the tree. These are all Parent Requirements of Level 5 requirements.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_testing_temp(requirement_id, tree_level)" +
					" select to_requirement_id , 6 " +
					" from gr_traces t, gr_testing_temp temp" +
					" where t.from_requirement_id = temp.requirement_id" +
					" and temp.tree_level = 5 ";
				}
			else{
					sql = "insert into gr_testing_temp(\"requirement_id\", \"tree_level\")" +
					" select to_requirement_id , 6 \"tree_level\" " +
					" from gr_traces t, gr_testing_temp \"temp\"" +
					" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
					" and \"temp\".\"tree_level\" = 5 ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			
			// lets get the seventh tree_level of Requirements in the tree. These are all Parent Requirements of Level 6 requirements.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_testing_temp(requirement_id, tree_level)" +
					" select to_requirement_id , 7 " +
					" from gr_traces t, gr_testing_temp temp" +
					" where t.from_requirement_id = temp.requirement_id" +
					" and temp.tree_level = 6 ";
				}
			else{
					sql = "insert into gr_testing_temp(\"requirement_id\", \"tree_level\")" +
					" select to_requirement_id , 7 \"tree_level\" " +
					" from gr_traces t, gr_testing_temp \"temp\"" +
					" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
					" and \"temp\".\"tree_level\" = 6 ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
	
			// lets get the eighth tree_level of Requirements in the tree. These are all Parent Requirements of Level 7 requirements.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_testing_temp(requirement_id, tree_level)" +
					" select to_requirement_id , 8 " +
					" from gr_traces t, gr_testing_temp temp" +
					" where t.from_requirement_id = temp.requirement_id" +
					" and temp.tree_level = 7 ";
				}
			else{
					sql = "insert into gr_testing_temp(\"requirement_id\", \"tree_level\")" +
					" select to_requirement_id , 8 \"tree_level\" " +
					" from gr_traces t, gr_testing_temp \"temp\"" +
					" where t.from_requirement_id = \"temp\".\"requirement_id\"" +
					" and \"temp\".\"tree_level\" = 7 ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			// the above process gives us a lot of duplicate requirements at the same tree_level. So
			// to make it more efficient, we are going to eliminate the duplicates.
			
			if (databaseType.equals("mySQL")){
				sql = "create table gr_testing_temp_no_duplicates as " +
					" select distinct requirement_id, tree_level from gr_testing_temp ";
				}
			else{
				sql = "create table gr_testing_temp_no_duplicates as " +
					" select distinct \"requirement_id\", \"tree_level\" from gr_testing_temp ";
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
				sql = "select requirement_id, tree_level from gr_testing_temp_no_duplicates where tree_level > 1 order by tree_level asc";
	
				}
			else{
				sql = "select \"requirement_id\", \"tree_level\" from gr_testing_temp_no_duplicates where \"tree_level\" > 1 order by \"tree_level\" asc";
	
			}
			prepStmt = con.prepareStatement(sql);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				processTestStatusOfRequirement(con, rs.getInt("requirement_id"), rs.getInt("tree_level"), debug);
			}
			rs.close();
			prepStmt.close();
		}
		catch (Exception e){
			e.printStackTrace();
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
		try {
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
		catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
}
