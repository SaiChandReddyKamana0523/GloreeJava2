package batchJobs;



import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Iterator;

import javax.naming.InitialContext;
















import com.gloree.utils.*;
import com.gloree.beans.MessagePacket;


public class BatchUserMetricsMailer {

	public static void main(String[] args) {
		java.sql.Connection con = null;

		// this program expects some input parameters. if they are empty, then it can not run
		// eg : userName, passWord
		String databaseType = "";
		String userName = "";
		String password = "";
		
		String serverName = "";
		String mailHost = "";
		String transportProtocol = "";
		String smtpAuth = "";
		String smtpPort = "";
		String smtpSocketFactoryPort = "";
		String emailUserId = "";
		String encryptedEmailPassword = "";
		
		

		//srt comment the following before go live
		/*
		databaseType = "mySQL";
		userName = "gloree";
		password = "SpecialGloree1";
		
		serverName = "www.tracecloud.com";
		mailHost = "smtp.gmail.com";
		transportProtocol = "smtp";
		smtpAuth = "true";
		smtpPort = "587";
		smtpSocketFactoryPort = "587";
		emailUserId = "admin@tracecloud.com";
		encryptedEmailPassword = "15996-9417-18060-18189-15996-9417-10191-9675-9675";
		*/
		//srt comment the above before go live
		
   		 for (int i = 0; i < args.length; i++){
	         String inputParam = args[i];
	         
	         if (inputParam.contains("serverName:")){
	        	 serverName = inputParam.replace("serverName:", "");
	         }
	         if (inputParam.contains("databaseType:")){
	        	 databaseType = inputParam.replace("databaseType:", "");
	         }
	         if (inputParam.contains("userName:")){
	        	 userName = inputParam.replace("userName:", "");
	         }
	         if (inputParam.contains("password:")){
	        	 password = inputParam.replace("password:", "");
	         }
	         
	         
	         if (inputParam.contains("mailHost:")){
	        	 mailHost = inputParam.replace("mailHost:", "");
	         }
	         if (inputParam.contains("transportProtocol:")){
	        	 transportProtocol = inputParam.replace("transportProtocol:", "");
	         }
	         if (inputParam.contains("smtpAuth:")){
	        	 smtpAuth = inputParam.replace("smtpAuth:", "");
	         }
	         if (inputParam.contains("smtpPort:")){
	        	 smtpPort = inputParam.replace("smtpPort:", "");
	         }
	         if (inputParam.contains("smtpSocketFactoryPort:")){
	        	 smtpSocketFactoryPort = inputParam.replace("smtpSocketFactoryPort:", "");
	         }
	         if (inputParam.contains("emailUserId:")){
	        	 emailUserId = inputParam.replace("emailUserId:", "");
	         }
	         if (inputParam.contains("encryptedEmailPassword:")){
	        	 encryptedEmailPassword = inputParam.replace("encryptedEmailPassword:", "");
	         }
	         
	         
	     }    	
   		
   		
   		String correctSyntax = "BatchMailer serverName:www.tracecloud.com databaseType:mySQL userName:dbuserId password:dbPassword  "+
		 "mailHost:smtp.gmail.com "+
		 "transportProtocol:smtp "+
		 "smtpAuth:true "+
		 "smtpPort:465 "+
		 "smtpSocketFactoryPort:465 "+
		 "emailUserId:emailId@company.com "+
		 "encryptedEmailPassword:123-433-6565-7676-434 "
		 ;
   		
   		
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
		
		 

		 if (mailHost.equals("")){
			 System.out.println("Error : mailHost is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 if (transportProtocol.equals("")){
			 System.out.println("Error : transportProtocol is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 if (smtpAuth.equals("")){
			 System.out.println("Error : smtpAuth is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 if (smtpPort.equals("")){
			 System.out.println("Error : smtpPort is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 if (smtpSocketFactoryPort.equals("")){
			 System.out.println("Error : smtpSocketFactoryPort is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 if (emailUserId.equals("")){
			 System.out.println("Error : emailUserId is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		
		 if (encryptedEmailPassword.equals("")){
			 System.out.println("Error : encryptedEmailPassword is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 
		 
		String decryptedEmailPassword = "";
		if (encryptedEmailPassword != null ){
			System.out.println("Decrypting the encrypted Email Password");
			String decrypted = "";
			// lets decrypt
			String[] enA = encryptedEmailPassword.split("-");
			for (int j=0; j< enA.length; j++ ) {
				int charInt = Integer.parseInt(enA[j]);
				charInt = charInt / 129;
				charInt = charInt - 25;
				char c = (char) charInt;
				decrypted += c;
			}
			decryptedEmailPassword = decrypted;
			System.out.println("Successfully Decrypted the encrypted Email Password");
		}

		
		
		
		try {

			// get a db connection.
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


			// lets get a list of users who should be notified.
			ArrayList users = getUsersToBeNotified(con);
			Iterator u = users.iterator();
			while (u.hasNext()){
				
				String userEmailAddress = (String) u.next();

				//srt fix the following before go live
				/*
				if (userEmailAddress.equals("nathan@tracecloud.com")){
					System.out.println("srt for nathan only");
				}
				else {
					System.out.println("skip for " + userEmailAddress);
					continue;
				}
				*/
				//srt fix the above before go live
				
				
				
				
				notifyUsers(userEmailAddress, con, databaseType , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, 
						emailUserId, decryptedEmailPassword , serverName);
				System.out.println("Notified user " + userEmailAddress);
		    	Thread.sleep(500);	
		    	
			}
		    
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

	

	private static ArrayList getUsersToBeNotified(java.sql.Connection con ) {
		ArrayList users  = new ArrayList();
		try {
				String sql = " select u.email_id " +
					" from gr_users u  " +
					" where (u.user_type in ('ReadWrite' , 'ReadOnly ')  " + 
					" 	or   " +
					"	(u.user_type = 'trial' and u.account_expire_dt > now() ))  " +
					" union  " +
					" select  distinct  " +
					" u.email_id from gr_projects p, gr_user_roles ur, gr_users u  " +
					" where p.billing_organization_id is not null  " +
					" and p.id = ur.project_id   " +
					" and ur.user_id = u.id"
					+ " and p.archived  = 0 ";

				PreparedStatement prepStmt = con.prepareStatement(sql);
		    	ResultSet rs = prepStmt.executeQuery();
		    	while (rs.next()){
		    		String userEmailId = rs.getString("email_id");
		    		users.add(userEmailId);				
		    		
		    	}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		return (users);

	}

	
	private static void notifyUsers(String userEmailAddress, java.sql.Connection con, String databaseType , 
		String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort,
		final String emailUserId, final String emailPassword, String serverName) throws SQLException{
		

		
		// for each user, lets get a list of projects that are not archived and hidden 
	
		// get the user's preferences regarding when to send the email and for which projects
		
		String sql = " select pref_healthcheck_days, pref_healthcheck_hide_projects"
				+ " from gr_users "
				+ " where email_id = '"+ userEmailAddress +"'";
		
		PreparedStatement prepStmt = con.prepareStatement(sql);
		ResultSet rs = prepStmt.executeQuery();
		String prefHealthCheckDays = "";
		String prefHealthCheckHideProjects = "";
		
		while (rs.next()){
			prefHealthCheckDays = rs.getString("pref_healthcheck_days");
			prefHealthCheckHideProjects = rs.getString("pref_healthcheck_hide_projects");
		}
		rs.close();
	

		// Did this user chose to receive health check emails today?
		Calendar now = Calendar.getInstance();
		
		
		String[] strDays = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
		        "Friday", "Saturday" };
	    // Day_OF_WEEK starts from 1 while array index starts from 0
		String currentDay =  strDays[now.get(Calendar.DAY_OF_WEEK) - 1];
	    System.out.println("Today is a : " + currentDay);

	    if (prefHealthCheckDays.contains(currentDay)){
	    	System.out.println("YES . "+ userEmailAddress +" WANTED TO BE NOTIFIED ON " + currentDay);
	    }
	    else {

	    	System.out.println("NO . "+ userEmailAddress +" DID NOT WANT TO BE NOTIFIED ON " + currentDay);
	    	return;
	    }
		
	    
	    
	    
		// for this user, lets get a list of 'DO NOT send' health check notification projects.
		
		
		sql = "select  distinct  p.id, p.short_name, p.name , u.pref_hide_projects " +  
				" from gr_projects p, gr_user_roles ur, gr_users u " + 
				" where p.id = ur.project_id  " + 
				" and ur.user_id = u.id " + 
				" and u.email_id = '"+ userEmailAddress + "'" + 
				" and  p.archived = 0 ";
		// for each project, lets build the table of data to send out
	
			
		prepStmt = con.prepareStatement(sql);
		rs = prepStmt.executeQuery();
		
		String mailBody = "";
		while (rs.next()){
			
			int projectId = rs.getInt("id");
			String projectPrefix = rs.getString("short_name");
			String projectName = rs.getString("name");
			String prefHideString = rs.getString("pref_hide_projects");
			if (prefHideString == null){
				prefHideString = "";
			}
			
			if (prefHideString.contains(projectId + ":#:" + projectPrefix)){
				System.out.println("This project was hidden by the user, so no stats sent out" + projectId + ":#:" + projectPrefix );
				continue;
			}
		
			if (prefHealthCheckHideProjects.contains(projectId + ":#:" + projectPrefix)){
				System.out.println("This user set preference to DO NOT HealthCheck this project, so no stats sent out" + projectId + ":#:" + projectPrefix );
				continue;
			}
		
			
			System.out.println("Send stats out for this project " + projectId + ":#:" + projectPrefix );
	
			
			String statString = getStatsForUserForProject(con, userEmailAddress, projectName, projectId, serverName);
			mailBody += statString;
		}

		prepStmt.close();
		rs.close();


		if (mailBody.length() > 0 ){
			// now for each users, lets get the different type of email messages
			// and format the message body
			// now lets send an email to the user.
			String messageBody =   "Hi, <br><br>" +
							"Here is a Health Check report for the objects you own in TraceCloud<br>" +
							"<br> Please note that you can customize the frequency and content of this email in the 'Account Profile' tab <br> " + 
 							mailBody +
							"<br><br>Best Regards <br><br>Trace Cloud System. ";
			
			
			
			// lets send the email out to the toEmailId;
			ArrayList to = new ArrayList();
			to.add(userEmailAddress);
			ArrayList cc = new ArrayList();
			MessagePacket mP = new MessagePacket(to, cc, "TraceCloud Health Check Report ", messageBody,"");
			EmailUtil.email(mP , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
		}

	}





	private static String getStatsForUserForProject(java.sql.Connection con, String userEmailAddress, String projectName, int projectId, String serverName) {
		String statString = "";
		
		
		int total = 0 ;
		int myDanglingRequirements = 0;
		int myOrphanRequirements = 0;
		int mysuspectUpStreamRequirements  = 0;
		int mysuspectDownStreamRequirements  = 0;
		
		
		int myReqsPendingApproval = 0;
		int myRejectedRequirements  = 0;
		int myPendingApprovalRequirements  = 0;
		
		
		int myIncompleteRequirements  = 0;
		int myTestPendingRequirements = 0;
		int myTestFailedRequirements = 0;
		
		try {
			String sql = " select count(*) 'total'  " +
					" from gr_requirements r, gr_requirement_types rt , gr_projects p " +
					" where r.owner  = ? " +
					" and r.requirement_type_id = rt.id " +
					" and r.deleted = 0 " +
					" and r.project_id = p.id " +
					" and p.archived = 0 ";
			if (projectId > 0 ){
				sql += " and r.project_id = " + projectId ;
			}
			
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, userEmailAddress);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				total = rs.getInt("total");
			}
			rs.close();
			prepStmt.close();							

			if (total == 0 ){
				// Then all other stats will be 0, so lets return with an empty string
				return(statString);
				
			}
	
			sql = " select count(*) 'myDanglingRequirements'  " +
				" from gr_requirements r, gr_requirement_types rt , gr_projects p " +
				" where r.owner  = ? " +
				" and r.requirement_type_id = rt.id " +
				" and rt.can_be_dangling = 1" + 
				" and (r.trace_from is null or r.trace_from = '')" +
				" and r.deleted = 0 " +
				" and r.project_id = p.id " +
				" and p.archived = 0 ";
			if (projectId > 0 ){
				sql += " and r.project_id = " + projectId ;
			}
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, userEmailAddress);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				myDanglingRequirements = rs.getInt("myDanglingRequirements");
			}
			rs.close();
			prepStmt.close();							
		
		
		

			sql = " select count(*) 'myOrphanRequirements'  " +
				" from gr_requirements r, gr_requirement_types rt, gr_projects p  " +
				" where r.owner  = ? " +
				" and r.requirement_type_id = rt.id " +
				" and rt.can_be_orphan = 1 " + 
				" and (r.trace_to is null or r.trace_to = '')" +
				" and r.deleted = 0 " +
				" and r.project_id = p.id " +
				" and p.archived = 0 ";
			if (projectId > 0 ){
				sql += " and r.project_id = " + projectId ;
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, userEmailAddress);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				myOrphanRequirements = rs.getInt("myOrphanRequirements");
			}
			rs.close();
			prepStmt.close();

			
			
			
			
			
			
			sql = " select count(*) 'mySuspectUp'  " +
				" from  gr_requirements r, gr_projects p " +
				" where r.owner = ? " +
				" and r.deleted = 0 " +
				" and r.trace_to like '%(s)%' " +
				" and r.project_id = p.id " +
				" and p.archived = 0 ";
			if (projectId > 0 ){
				sql += " and r.project_id = " + projectId ;
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, userEmailAddress);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				mysuspectUpStreamRequirements = rs.getInt("mySuspectUp");
			}
			rs.close();
			prepStmt.close();
			
			
			
			
			
			sql = " select count(*) 'mySuspectDown'  " +
				" from  gr_requirements r, gr_projects p " +
				" where r.owner = ? " +
				" and r.deleted = 0 " +
				" and r.trace_from like '%(s)%' " +
				" and r.project_id = p.id " +
				" and p.archived = 0 ";
			if (projectId > 0 ){
				sql += " and r.project_id = " + projectId ;
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, userEmailAddress);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				mysuspectDownStreamRequirements = rs.getInt("mySuspectDown");
			}
			rs.close();
			prepStmt.close();


			
			
			
			sql = " select count(*) 'myReqsPendingApproval'  " +
				" from gr_requirements r , gr_projects p " +
				" where r.owner  = ? " +
				" and r.status = ? " +
				" and r.deleted = 0  " +
				" and r.project_id = p.id " +
				" and p.archived = 0 ";
			if (projectId > 0 ){
				sql += " and r.project_id = " + projectId ;
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, userEmailAddress);
			prepStmt.setString(2,"In Approval WorkFlow");
			rs = prepStmt.executeQuery();
			while (rs.next()){
				myReqsPendingApproval = rs.getInt("myReqsPendingApproval");
			}
			rs.close();
			prepStmt.close();

			
			sql = " select count(*) 'myRejectedRequirements'  " +
				" from gr_requirements r, gr_projects p " +
				" where r.owner  = ? " +
				" and r.status = ? " +
				" and r.deleted = 0 " +
				" and r.project_id = p.id " +
				" and p.archived = 0 ";
			if (projectId > 0 ){
				sql += " and r.project_id = " + projectId ;
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, userEmailAddress);
			prepStmt.setString(2,"Rejected");
			rs = prepStmt.executeQuery();
			while (rs.next()){
				myRejectedRequirements = rs.getInt("myRejectedRequirements");
			}
			rs.close();
			prepStmt.close();

			
			sql = " select count(*) 'myPendingApprovalRequirements'  " +
					" from gr_requirement_approval_h rah , gr_requirements r, gr_projects p " +
					" where rah.requirement_id = r.id " +
					" and rah.version = r.version " +
					" and rah.approver_email_id = ? " +
					" and rah.response  = 'Pending' " +
					" and r.deleted = 0 " +
					" and r.project_id = p.id " +
					" and p.archived = 0 ";
			if (projectId > 0 ){
				sql += " and r.project_id = " + projectId ;
			}
			

			prepStmt = con.prepareStatement(sql);
			
			prepStmt.setString(1, userEmailAddress);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				myPendingApprovalRequirements = rs.getInt("myPendingApprovalRequirements");
			}
			
			
			
			sql = " select count(*) 'myIncompleteRequirements'  " +
				" from gr_requirements r, gr_projects p " +
				" where r.owner  = ? " +
				" and r.pct_complete < 100  " +
				" and r.deleted = 0 " +
				" and r.project_id = p.id " +
				" and p.archived  = 0 ";
			if (projectId > 0 ){
				sql += " and r.project_id = " + projectId ;
			}
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, userEmailAddress);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				myIncompleteRequirements = rs.getInt("myIncompleteRequirements");
			}
			rs.close();
			prepStmt.close();

			
			
			
			sql = " select count(*) 'myTestPendingRequirements'  " +
				" from gr_requirements r, gr_projects p" +
				" where r.owner  = ? " +
				" and r.testing_status = 'Pending' " +
				" and r.deleted = 0 " +
				" and r.project_id = p.id " +
				" and p.archived = 0 ";
			if (projectId > 0 ){
				sql += " and r.project_id = " + projectId ;
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, userEmailAddress);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				myTestPendingRequirements = rs.getInt("myTestPendingRequirements");
			}
			rs.close();
			prepStmt.close();

			
			
			sql = " select count(*) 'myTestFailedRequirements'  " +
				" from gr_requirements r, gr_projects p " +
				" where r.owner  = ? " +
				" and r.testing_status = 'Fail' " +
				" and r.deleted = 0 " +
				" and r.project_id = p.id " +
				" and p.archived = 0 ";
			if (projectId > 0 ){
				sql += " and r.project_id = " + projectId ;
			}
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, userEmailAddress);
			rs = prepStmt.executeQuery();
			while (rs.next()){
				myTestFailedRequirements = rs.getInt("myTestFailedRequirements");
			}
			rs.close();
			prepStmt.close();


			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		
		
		statString += "<br><br><table border='1' bgcolor='#d9edf7'>";
	 	statString += "<tr><td style='width:150px'>Project</td><td style='width:700px'>" + projectName + "</td></tr>";
	 	statString += "<tr><td style='width:150px'>Total</td><td style='width:700px' > You own " + total + " objects in the project</td></tr>";
	 	
	 	statString += "<tr><td colspan='2' bgcolor='#BCE8F1' align='center'>Traceability Stats </td></tr>";
	 	
	 	if (myDanglingRequirements > 0 ) {
	 		statString += "<tr><td>Dangling</td><td bgcolor='lightpink'>Trouble. You have "
	 				+  "<a href=https://"+ serverName +"/GloreeJava2/servlet/DisplayAction?dO=healthCheck&displayProjectId=" + projectId + "&displayFunction=myDangling>" 
	 				+  myDanglingRequirements + "</a>" 
	 				+ " objects with no downstream components</td></tr>";
	 	}
	 	else {
	 		statString += "<tr><td>Dangling</td><td ' bgcolor='lightgreen'>Good. All your TraceCloud objects have a downstream component</td></tr>";
		}
	 	
	 	if (myOrphanRequirements > 0 ) {
	 		statString += "<tr><td>Orphan</td><td bgcolor='lightpink'>Trouble. You have "
	 				+  "<a href=https://"+ serverName +"/GloreeJava2/servlet/DisplayAction?dO=healthCheck&displayProjectId=" + projectId + "&displayFunction=myOrphan>" 
	 				+ myOrphanRequirements + "</a>" +
	 				" objects with no up stream components</td></tr>";
	 	}
	 	else {
	 		statString += "<tr><td>Orphan</td><td bgcolor='lightgreen'>Good. All your TraceCloud objects have an up stream component</td></tr>";
		}
	 	
	 	if (mysuspectUpStreamRequirements > 0 ) {
	 		statString += "<tr><td>Suspect Upstream</td><td  bgcolor='lightpink'>Trouble. You have " 
	 				+  "<a href=https://"+ serverName +"/GloreeJava2/servlet/DisplayAction?dO=healthCheck&displayProjectId=" + projectId + "&displayFunction=mySuspectUp>" 
	 				+ mysuspectUpStreamRequirements + "</a>" 
	 				+" objects that have changed upstream</td></tr>";
	 	}
	 	else {
	 		statString += "<tr><td>Suspect Upstream</td><td  bgcolor='lightgreen'>Good. All your TraceCloud objects are in sync with up stream component</td></tr>";
		}
	 	
	 	
	 	if (mysuspectDownStreamRequirements > 0 ) {
	 		statString += "<tr><td>Suspect Downstream</td><td  bgcolor='lightpink'>Trouble. You have " 
	 				+  "<a href=https://"+ serverName +"/GloreeJava2/servlet/DisplayAction?dO=healthCheck&displayProjectId=" + projectId + "&displayFunction=mySuspectDown>" 
	 				+ mysuspectDownStreamRequirements + "</a>" 
	 				+ " objects that have changed down stream</td></tr>";
	 	}
	 	else {
	 		statString += "<tr><td>Suspect Downstream</td><td bgcolor='lightgreen'>Good. All your TraceCloud objects are in sync with down stream component</td></tr>";
		}
	 	
	 	
	 	
	 	
	 	statString += "<tr><td colspan='2' bgcolor='#BCE8F1' align='center'>Approval Work Flow Stats </td></tr>";
	 	

	 	if (myReqsPendingApproval > 0 ) {
	 		statString += "<tr><td>Pending Approval</td><td ' bgcolor='lightpink'>Trouble. You have "
	 				+  "<a href=https://"+ serverName +"/GloreeJava2/servlet/DisplayAction?dO=healthCheck&displayProjectId=" + projectId + "&displayFunction=myReqsPendingApproval>" 
	 				+ myReqsPendingApproval + "</a>"
	 				+ " objects that are pending approval from others</td></tr>";
	 	}
	 	else {
	 		statString += "<tr><td>Pending Approval</td><td bgcolor='lightgreen'>Good. None of your objects are pending approval. They have been either approved or rejected</td></tr>";
		}
	 	
	 	if (myRejectedRequirements > 0 ) {
	 		statString += "<tr><td>Rejected</td><td bgcolor='lightpink'>Trouble. You have "
	 				+  "<a href=https://"+ serverName +"/GloreeJava2/servlet/DisplayAction?dO=healthCheck&displayProjectId=" + projectId + "&displayFunction=myReqsRejected>" 
	 				+ myRejectedRequirements + "</a>" 
	 				+ " objects that have been rejected by others</td></tr>";
	 	}
	 	else {
	 		statString += "<tr><td>Rejected</td><td bgcolor='lightgreen'>Good. None of your objects have been rejected </td></tr>";
		}
	 	
	 	if (myPendingApprovalRequirements > 0 ) {
	 		statString += "<tr><td>Pending Your Approval</td><td bgcolor='lightpink'>Trouble. There are "
	 				+  "<a href=https://"+ serverName +"/GloreeJava2/servlet/DisplayAction?dO=healthCheck&displayProjectId=" + projectId + "&displayFunction=myPendingApproval>" 
	 				+ myPendingApprovalRequirements + "</a>" 
	 				+ " objects that you need to approve (or reject) </td></tr>";
	 	}
	 	else {
	 		statString += "<tr><td>Pending Your Approval</td><td bgcolor='lightgreen'>Good. There are no objects pending your approval (or rejection) action </td></tr>";
		}
	 	
	 	
	 	statString += "<tr><td colspan='2' bgcolor='#BCE8F1' align='center'>Completion Stats </td></tr>";
	 	
	 	if (myIncompleteRequirements > 0 ) {
	 		statString += "<tr><td>Completion</td><td bgcolor='lightpink'>Trouble. You have "
	 				+  "<a href=https://"+ serverName +"/GloreeJava2/servlet/DisplayAction?dO=healthCheck&displayProjectId=" + projectId + "&displayFunction=myIncomplete>" 
	 				+ myIncompleteRequirements + "</a>"
	 				+ " objects that are incomplete</td></tr>";
	 	}
	 	else {
	 		statString += "<tr><td>Completion </td><td  bgcolor='lightgreen'>Good. All your objects are completed</td></tr>";
		}
	 	
	 	
	 	if (myTestPendingRequirements > 0 ) {
	 		statString += "<tr><td>Test Pending</td><td  bgcolor='lightpink'>Trouble. You have "
	 				+  "<a href=https://"+ serverName +"/GloreeJava2/servlet/DisplayAction?dO=healthCheck&displayProjectId=" + projectId + "&displayFunction=myTestPending>" 
	 				+ myTestPendingRequirements + "</a>" 
	 				+ " objects that are yet to be tested</td></tr>";
	 	}
	 	else {
	 		statString += "<tr><td>Test Pending</td><td  bgcolor='lightgreen'>Good. You have no objects that are pending Testing. They have all Passed or Failed</td></tr>";
		}
	 	
	 	
	 	if (myTestFailedRequirements > 0 ) {
	 		statString += "<tr><td>Test Result</td><td bgcolor='lightpink'>Trouble. You have "
	 				+  "<a href=https://"+ serverName +"/GloreeJava2/servlet/DisplayAction?dO=healthCheck&displayProjectId=" + projectId + "&displayFunction=myTestFailed>" 
	 				+ myTestFailedRequirements + "</a>" 
	 				+ " objects that have failed testing </td></tr>";
	 	}
	 	else {
	 		statString += "<tr><td>Test Result</td><td bgcolor='lightgreen'>Good. You have no objects that have failed testing</td></tr>";
		}
	 	
	 	
	 	
	 	
		statString += "</table>";
			
		return (statString);

	}

}
