package batchJobs;


import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;


/*
 * This is a cron job used to remind all approvers who are pending approval beyond
 * the permitted time.
 * 
 * 
 */
public class BatchRemindApprovers{

	public static void main(String[] args)  throws InterruptedException{
		java.sql.Connection con = null;
		
		try {
    		Calendar cal = Calendar.getInstance();
			
    		String[] strDays = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thusday",
			        "Friday", "Saturday" };
		    // Day_OF_WEEK starts from 1 while array index starts from 0
			String currentDay =  strDays[cal.get(Calendar.DAY_OF_WEEK) - 1];
		    System.out.println("Today is a : " + currentDay);
    		
    		// this program expects some input parameters. if they are empty, then it can not run
    		// eg : userName, passWord
    		String databaseType = "";
    		String serverName = "";
    		String userName = "";
    		String password = "";
    		
    		int staleAfter = 100;
    		String staleAfterString = "";

    		
    		for (int i = 0; i < args.length; i++){
		         String inputParam = args[i];
		         if (inputParam.contains("databaseType:")){
		        	 databaseType = inputParam.replace("databaseType:", "");
		         }
			    if (inputParam.contains("serverName:")){
		        	 serverName = inputParam.replace("serverName:", "");
		         }
		        
		         if (inputParam.contains("userName:")){
		        	 userName = inputParam.replace("userName:", "");
		         }
		         if (inputParam.contains("password:")){
		        	 password = inputParam.replace("password:", "");
		         }
		         
		         if (inputParam.contains("staleAfter:")){
		        	 staleAfterString = inputParam.replace("staleAfter:", "");
		         }
		         
		     }    	
    		
    		
    		
	   		String correctSyntax = "BatchRemindApprovers serverName:www.tracecloud.com databaseType:mySQL userName:dbuserId password:dbPassword \n or \n "+
			 "BatchRemindApprovers databaseType:oracle userName:dbuserId password:dbPassword " ;
	   		
		   		
	   		if (serverName.equals("")){
				 System.out.println("Error : serverName is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
				 return;
			 }
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
		    
		    
			System.out.println("srt staleAfter String is " + staleAfter);
			
	    	try {
	    		staleAfter = Integer.parseInt(staleAfterString);
	    	}
	    	catch (Exception e){
	    		staleAfter = 30;
	    	}
	    	System.out.println("srt staleAfter value is " + staleAfter);
			
		    // lets get the list of projects in the system.
		   
	
		    //  So, we will try to crunch ONLY for projects that 
		    // either have a valid project license or have at least one non-expired users. 
			String sql = "select id, name, billing_organization_id " + 
			" from gr_projects" ; 
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			ResultSet rs = prepStmt.executeQuery();
			
			int noProjects = 0;
			int yesProjects = 0;
		    while (rs.next()) {
		    	
	
		    	
		    	int projectId = rs.getInt("id");
		    	String projectName = rs.getString("name");
		    	int billingOrgId = rs.getInt("billing_organization_id");
		    	
		    	//System.out.println("Project id is " + projectId + " name is " + projectName );
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
			    	//System.out.println("NO " +  projectName + " : Has Neither a valid user nor a valid project license. Will not crunch metrics.");
			    	System.out.println("NO " + projectId + " " + projectName );
			    	
			    	noProjects++;
			    	continue;
			    }
			    else if (projectName.contains("Sample project")){
					// lets not send reminders for sample projects
					System.out.println("	THIS IS A SAMPLE PROJECT. WILL NOT SEND REMINDERS TO APPROVERS " + projectName);
					noProjects++;
					continue;
				}
			    else {
			    	yesProjects++;
			    	//System.out.println("YES " + projectName + " : Yes, we can crunch metrics. Valid Project License or Valid User exists");
			    	System.out.println("YES " + projectId  + " "  +   projectName );
			    	
			    }
			    
		    	System.out.println("\n\nStarting remind approvers gathering for project " + projectName + " at " + Calendar.getInstance().getTime());
		    	
		    	
		    	
		    	Thread.sleep(500);
		    	remindApprovers(con, serverName, projectId, projectName,currentDay, staleAfter,  databaseType);
	    		
		    	System.out.println("Finished remind approvers for project " + projectName + " at " + Calendar.getInstance().getTime());
		    }
			
		    System.out.println("Will not remind " + noProjects + " projects ");
		    System.out.println("Will  remind " + yesProjects + " projects ");
		    
		    rs.close();
		    prepStmt.close();
		   
		    con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
	}



	private static void remindApprovers(java.sql.Connection con, String serverName, int projectId, String projectName,
			String currentDay, int staleAfter, String databaseType) throws SQLException{

		
		System.out.println("	In remindApprovers for Project " + projectName);
		
		// Lets find requirement types that have workflow approval enabled.  
		// and remind_approvers_frequency > 0 .
		String sql = " select id, name, remind_approvers_on " +
				"	from gr_requirement_types " +
				"	where project_id = ? " +
				"  	and enable_approval = 1 " ;
		 
		
		PreparedStatement prepStmt = con.prepareStatement(sql);
		prepStmt.setInt(1, projectId);
		ResultSet rs  =  prepStmt.executeQuery();
		while (rs.next()){
			int requirementTypeId = rs.getInt("id");
			String requirementTypeName = rs.getString("name");
			String remindApproversOn = rs.getString("remind_approvers_on");
			// if today is one of the days we send reminders on, then we remind all the pending approvers of these requirements.
			if (remindApproversOn.contains(currentDay)){
				System.out.println("		" + requirementTypeName + " is scheduled for reminder today  " + currentDay);
			
				// Lets see if any requirements in this project / reqtypes 
				// are in 'In Approval WorkFlow'
				sql = "";
				
				
				if (databaseType.equals("mySQL")){
					sql = " select id, version, full_tag , datediff(now() , submitted_for_approval_dt) 'daysSinceSubmittedForApproval' " +
						" from gr_requirements " +
						" where requirement_type_id =  ? " +
						" and deleted = 0 and  status = 'In Approval WorkFlow'";
				}
				else {
					sql = " select id, version, full_tag , sysdate - submitted_for_approval_dt 'daysSinceSubmittedForApproval' " +
						" from gr_requirements " +
						" where requirement_type_id = ? " +
						" and deleted = 0 and status = 'In Approval WorkFlow' ";
					
				}
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1 , requirementTypeId);
				
				ResultSet rs2 = prepStmt.executeQuery();
				
				
				while (rs2.next()){
					int requirementId = rs2.getInt("id");
					int requirementVersion = rs2.getInt("version");
					String fullTag = rs2.getString("full_tag");
					int daysSinceSubmittedForApproval = rs2.getInt("daysSinceSubmittedForApproval");
					System.out.println( fullTag);
					// For each of these lets update the approver list and remind them
					if (daysSinceSubmittedForApproval > staleAfter){
						System.out.println("Not sending " + fullTag + " for approval as daysSinceSubmittedForApproval is  " + daysSinceSubmittedForApproval);
						continue;
					}
					else {
						System.out.println("Sending " + fullTag + " for approval as daysSinceSubmittedForApproval is  " + daysSinceSubmittedForApproval);
						
						// we are removing refreshRAH, as the system is generally self correcting. 
						// as each user is approving / rejecting, the system calculates RAH So No need to re do that.
						//refreshRequirementApprovalHistory(con, requirementId, requirementVersion,   databaseType);
						
						remindPendingApprovers(con, requirementId,  databaseType, serverName);
					}
				}
				rs2.close();
			}
			else {
				System.out.println("		" + requirementTypeName + " is NOT scheduled for reminder today  " + currentDay);
				
			}

			}
			
			
			
			
					
		rs.close();
		prepStmt.close();

	   
	}

/*
	
	*/


	
	// This is a copy of a method in RequirementUtils
	public static void remindPendingApprovers(java.sql.Connection con, int requirementId, String databaseType , String  serverName) {
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {
			
	/*
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
	*/
	
	 	String sql = "select p.name \"project_name\", u.email_id 'owner' , u.first_name, u.last_name, r.id \"requirement_id\", " +
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
			
			storeMessage(con, projectName, toEmailId, messageType, messageBody,  databaseType);
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
				
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
		}
	}

	
	
	// clone of a method in EmailUtil
	// stores a message in the db for sending a consolidated file at the end of the day,.
	public static void storeMessage(java.sql.Connection con, String projectName, String toEmailId, 
		String messageType, String messageBody, String databaseType){
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {			
			//
			// This sql inserts a message in to the db so that it can be emailed in bulk later.
			//
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_messages (project_name, to_email_id, message_type," +
					" message_body, message_created_dt ) " +
					" values (?,?,?,?, now())";
			}
			else {
				sql = " insert into gr_messages (project_name, to_email_id, message_type," +
				" message_body, message_created_dt ) " +
				" values (?,?,?,?, sysdate)";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, projectName);
			prepStmt.setString(2, toEmailId);
			prepStmt.setString(3, messageType);
			prepStmt.setString(4, messageBody);

			prepStmt.execute();
			
			prepStmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) {  
				try {rs.close();} catch (Exception e) {}
			} 
		}
		
	}	
	
}
