package batchJobs;



import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import java.util.ArrayList;

import javax.naming.InitialContext;



import com.gloree.utils.*;
import com.gloree.beans.MessagePacket;


public class BatchMailer {

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

			//remindApproversifRTIsSoConfigured(con, databaseType);
			
		    // this routine sends an email to every one who has been granted a license.
		    sendOutLicenseGrants(con, serverName,  mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, decryptedEmailPassword);
		    
		    // Notify userss goes to gr_messages and for each user who needs to be notified
		    // consolidates all messages in to one email and sends it out.
		    
		    // before we call notify users, lets call remindUserOfInvitations. This puts a row in the gr_messages table
		    // if the user has been sent an invitation, and hasn't yet accepted.
		    // to avoid spam, we plan to send 5 reminders to the user. Once every monday, till the user accepts it.
		    
		    
		    
		    String sendReminderDay  = "Monday";
		    remindUsersOfInvitations(con, sendReminderDay, serverName, databaseType);
		    System.out.println("Completed Remind Users of Invitations");
		    
		    System.out.println("Starting Notify Approvers");
		    notifyApprovers(con, databaseType, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, decryptedEmailPassword);
		    System.out.println("Completed Notify Approvers");
		    
		    
		    System.out.println("Started Notify Users");
		    notifyUsers(con, databaseType, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, decryptedEmailPassword);
		    System.out.println("Completed Notify Users");
		    
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


	// srt : DEAD code . No longer needed. will be covered by batchRemindUsers.
	private static void remindApproversifRTIsSoConfigured(java.sql.Connection con ,String databaseType) {
		try {
			// we don't want to spam users. so we need to do this only once a week , i.e on Monday.
			Calendar now = Calendar.getInstance();
			String[] strDays = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thusday",
			        "Friday", "Saturday" };
		    // Day_OF_WEEK starts from 1 while array index starts from 0
			String currentDay =  strDays[now.get(Calendar.DAY_OF_WEEK) - 1];
		    System.out.println("Today is a : " + currentDay);
		    
		    
		    
		    
		    // we just want to remind approvers, only where projects have either a valid license or at least 1 paying user in them.
		    // we are crunching metrics for projects that don't have any active users. 
		    // This just wastes processing time. So, we will try to crunch ONLY for projects that 
		    // either have a valid project license or have at least one non-expired users. 
			String sql = "select id, name, billing_organization_id from gr_projects" ; 
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			ResultSet rs = prepStmt.executeQuery();
		    while (rs.next()) {
	
		    	
		    	int projectId = rs.getInt("id");
		    	String projectName = rs.getString("name");
		    	int billingOrgId = rs.getInt("billing_organization_id");
		    	boolean projectHasProjectLicense = false;
		    	if (billingOrgId > 0 ){
		    		projectHasProjectLicense = true;
		    	}
		    	
		    	// lets see if this project has atleast one valid user
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
			    
			    rs2.close();
			    prepStmt2.close();
			    
			    
			    if (numberOfValidUsers > 0 ){
			    	projectHasAValidUser = true;
			    }
			    
			    if (!(projectHasAValidUser || projectHasProjectLicense)){
			    	//System.out.println( "DEAD PROJECT : " + projectName + " : Has Neither a valid user nor a valid project license. Will not remind approvers for this project .");
			    	continue;
			    }
			    else {
			    	System.out.println("LIVE PROJECT : " + projectName + " : Yes, we can remind approvers  Valid Project License or Valid User exists");
				    
				    // lets iterate through all the req types and find out their remindApproverFrequency
				    // find req types that have approval work flow enabled and have a frequency that is not once
				    
				    // to prevent crunching for projects that are dead, we do this only for projects where this at least one paying licence.
				    sql2 = "select  rt.id, rt.name , rt.remind_approvers " +
				    		" from gr_requirement_types rt" +
				    		" where rt.project_id = ? " +
				    		" and rt.enable_approval = 1 " +
				    		" order by rt.id ";
				    prepStmt2  = con.prepareStatement(sql2);
				    prepStmt2.setInt(1, projectId);
				    rs2 = prepStmt2.executeQuery();
				    while (rs2.next()){
				    	int requirementTypeId = rs2.getInt("id");
				    	String requirementTypeName = rs2.getString("name");
				    	String remindApprovers = rs2.getString("remind_approvers");
				    	
				    	if ((remindApprovers == null ) || (remindApprovers.equals("Only Once"))){
				    		System.out.println("NO REMINDERS :  Reminder frequency for " + requirementTypeName + " is set to 'Only Once'. So no reminders will be sent ");
				    		continue;
				    	}
				    	else {
				    		// lets find out how many days it has been since a reminder was sent this to req  type
				    		int daysSinceLastReminder = getDaysSinceLastReminderSent(con, requirementTypeId, databaseType);
				    		//System.out.println("REMINDERS EXIST :  Reminder frequency for " + requirementTypeName + " is set to " + remindApprovers + ". Valid Candidate. daysSinceLastSent is " + daysSinceLastReminder );
				    		
				    		if ((remindApprovers.equals("Once A Day")) && (daysSinceLastReminder > 1))  {
				    			// we should send reminders once a day and it has been more than a day since we sent the last reminder. Lets send one now
				    			System.out.println("VALID : Once A Day : we should send reminders once a day and it has been more than a day since we sent the last reminder to " + requirementTypeName + " . Lets send one now");
				    			sendReminders(con, requirementTypeId, requirementTypeName, databaseType);
				    			
				    		}
				    		
				    		if ((remindApprovers.equals("Once A Week")) && (daysSinceLastReminder > 6))  {
				    			// we should send reminders once a week and it has been more than 7 day since we sent the last reminder. Lets send one now
				    			System.out.println("VALID : Once A Week : we should send reminders once a week and it has been more than 7 day since we sent the last reminder to " + requirementTypeName + " . Lets send one now");
				    			sendReminders(con, requirementTypeId, requirementTypeName, databaseType);
				    		}
				    		
				    		if ((remindApprovers.equals("Once in 2 Weeks")) && (daysSinceLastReminder > 13))  {
				    			// we should send reminders once in 2 weeks and it has been more than 12 days since we sent the last reminder. Lets send one now
				    			System.out.println("VALID : Once in 2 Weeks :  we should send reminders once in 2 weeks and it has been more than 12 days since we sent last reminder to " + requirementTypeName + " . Lets send one now");
				    			sendReminders(con, requirementTypeId, requirementTypeName, databaseType);
				    		}
				    		
				    		if ((remindApprovers.equals("Once A Month")) && (daysSinceLastReminder > 29))  {
				    			// we should send reminders once a Month and it has been more than 30 days since we sent the last reminder. Lets send one now
				    			System.out.println("VALID : Once A Month : we should send reminders once a Month and it has been more than 30 days since we sent the last reminder to " + requirementTypeName + " . Lets send one now");
				    			sendReminders(con, requirementTypeId, requirementTypeName,  databaseType);
				    		}
				    	}
				    }
				    
				   
			    	
			    	
			    }
		    }
		}
		catch (Exception e){
			e.printStackTrace();
		}

	}


	// srt : DEAD code . No longer needed. will be covered by batchRemindUsers.
	private static void sendReminders(java.sql.Connection con , int requirementTypeId, String requirementTypeName, String databaseType){
		
		try {
		System.out.println("SEND REMINDERS : Sending reminders for req type " + requirementTypeName ); 
			String sql = "";
			PreparedStatement prepStmt = null;
			// for this req type, find all requirements that are in 'Approval Work Flow'
			sql = "select id, full_tag " +
				" from gr_requirements " +
				" where requirement_type_id = ? " +
				" and status = 'In Approval WorkFlow'";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				int requirementId  = rs.getInt("id");
				String requirementFullTag = rs.getString("name");
				
				System.out.println("Remind approvers for req " + requirementFullTag);
				// For each of the requirements, call the notifyPendingApprovers.
				//remindPendingApprovers(con, requirementId, databaseType , serverName) 
				
				
			}
			
			
			
			
			
			// once reminders are sent, lets update 'Last_reminder_sent_date
			if (databaseType.equals("mySQL")){
				sql = " update gr_requirement_types" +
					" set last_reminder_dt = curdate()  " +
					" where id = ? ";
			}
			else {
				sql = " update gr_requirement_types " +
					" set last_reminder_dt = sysdate " +
					" where id = ? ";
			}	
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			prepStmt.execute();
			prepStmt.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
	}
	


	// srt : DEAD code . No longer needed. will be covered by batchRemindUsers.
	private static int getDaysSinceLastReminderSent(java.sql.Connection con , int requirementTypeId, String databaseType){
		int daysSinceLastReminderSent = 0;

		String sql = "";
		if (databaseType.equals("mySQL")){
			sql = " select datediff (curdate(),last_reminder_sent_dt) 'diff' " +
				" from gr_requirement_types " +
				" where id = ? ";
		}
		else {
			sql = " select sysdate - last_reminder_sent_dt 'diff'" +
				" from gr_requirement_types " +
				" where id = ? ";
		}	
		try {
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			ResultSet rs = prepStmt.executeQuery();
		    while (rs.next()) {
		    	daysSinceLastReminderSent = rs.getInt("diff");
		    }
		}
		catch (Exception e){
			e.printStackTrace();
		}		
		return (daysSinceLastReminderSent);
	}
	

	// srt : DEAD code . No longer needed. will be covered by batchRemindUsers.
	// this is a copy of the routine in RequirementUtil.java
	public static void remindPendingApprovers(java.sql.Connection con, int requirementId, String databaseType , String  serverName) {
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
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
				
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
	
	// before we call notify users, lets call remindUserOfInvitations. This puts a row in the gr_messages table
    // if the user has been sent an invitation, and hasn't yet accepted.
    // to avoid spam, we plan to send 5 reminders to the user. Once every monday, till the user accepts it.
	private static void remindUsersOfInvitations(java.sql.Connection con , String sendReminderDay, String serverName, String databaseType) {
		try {
			// we don't want to spam users. so we need to do this only once a week , i.e on Monday.
			Calendar now = Calendar.getInstance();
			String[] strDays = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thusday",
			        "Friday", "Saturday" };
		    // Day_OF_WEEK starts from 1 while array index starts from 0
			String currentDay =  strDays[now.get(Calendar.DAY_OF_WEEK) - 1];
		    System.out.println("Today is a : " + currentDay);
		    if (currentDay.equals(sendReminderDay)){
		    	// this is Monday, so lets run this task.
		    	System.out.println("This is a " + sendReminderDay + " , so running remindUsersOfInvitations");
		    	
		    	// lets find users who still exist in gr_invitations whose emails_sent is less than 5
		    	// and we haven't sent an email already TODAY
		    	String sql = "";

				if (databaseType.equals("mySQL")){
				
					sql = " select i.id, i.invitee_email_id, p.name,  i.emails_sent " +
		    			" from gr_invitations i, gr_projects p  " +
		    			" where i.emails_sent < 5 " +
		    			" and DATE_FORMAT(last_email_sent_on, '%Y-%m-%d') != DATE_FORMAT(now(), '%Y-%m-%d')  " +
		    			" and i.project_id = p.id ";
				}
				else {
					sql = " select i.id, i.invitee_email_id, p.name,  i.emails_sent " +
			    			" from gr_invitations i, gr_projects p  " +
			    			" where i.emails_sent < 5 " +
			    			" and trunc(last_email_sent_on) != trunc (sysdate) " +
			    			" and i.project_id = p.id ";
				}
				PreparedStatement prepStmt = con.prepareStatement(sql);
		    	ResultSet rs = prepStmt.executeQuery();
		    	while (rs.next()){
		    		int invitationId = rs.getInt("id");
		    		String targetEmailId = rs.getString("invitee_email_id");
		    		String projectName = rs.getString("name");
		    		int emailsSent = rs.getInt("emails_sent");
		    		
		    		// lets increment the emailsSent number.
		    		emailsSent++;
		    		
		    		String emailsSentString = "";
		    		if (emailsSent == 2){
		    			emailsSentString = " Second ";
		    		}
		    		if (emailsSent == 3){
		    			emailsSentString = " Third ";
		    		}
		    		if (emailsSent == 4){
		    			emailsSentString = " Fourth ";
		    		}
		    		if (emailsSent == 5){
		    			emailsSentString = " Fifth  ";
		    		}
		    		
		    		// lets update the emailsSent for this invitation to the next number.
		    		
		    		String sql2 = "";
		    		if (databaseType.equals("mySQL")){
		    			sql2 = " update gr_invitations set emails_sent = ?, last_email_sent_on = now() where id = ? ";
		    		}
		    		else {
		    			sql2 = " update gr_invitations set emails_sent = ?, last_email_sent_on = sysdate where id = ? ";
		    		}
		    		
		    		PreparedStatement prepStmt2 = con.prepareStatement(sql2);
		    		prepStmt2.setInt(1, emailsSent);
		    		prepStmt2.setInt(2, invitationId);
		    		prepStmt2.execute();

					// now lets build the email to the user.
					String toEmailId = targetEmailId;
					String messageType = "newUserAddedToProject";
					String messageBody =  "\n\nYou have been granted access to project '" + projectName + "'." +
					" You can access this project by creating a FREE account at " + 
					"\n\n\thttp://" + serverName +  
					"\n\nNOTE : PLEASE REMEMBER TO USE '" + targetEmailId + 
					"' WHEN YOU CREATE YOUR TRACECLOUD ACCOUNT." + 
					"\n\n This will ensure that you will have access to this project." +
					"\n\n This is the " + emailsSentString + " of Five reminders";	
					
					
					// lets store the message in the gr_messages. This will be processed by BatchMailer's 
					// notifyUsers method.
					
					if (databaseType.equals("mySQL")){
						sql2 = " insert into gr_messages (project_name, to_email_id, message_type," +
							" message_body, message_created_dt ) " +
							" values (?,?,?,?, now())";
					}
					else {
						sql2 = " insert into gr_messages (project_name, to_email_id, message_type," +
						" message_body, message_created_dt ) " +
						" values (?,?,?,?, sysdate)";
					}
					prepStmt2 = con.prepareStatement(sql2);
					prepStmt2.setString(1, projectName);
					prepStmt2.setString(2, toEmailId);
					prepStmt2.setString(3, messageType);
					prepStmt2.setString(4, messageBody);

					prepStmt2.execute();
					
		    		
		    	}
		    	
		    	
		    	
		    	
		    	
		    	
		    	
				
		    	
		    }
		    else {
		    	System.out.println("This is not a " + sendReminderDay + ", so not running remindUsersOfInvitations");
		    }
		    
		    
			
		}
		catch (Exception e){
			e.printStackTrace();
		}

	}


	// sends out an email to all those who have been granted licenses
	// inviting them to open a tracecloud account.
	private static void sendOutLicenseGrants(java.sql.Connection con , String  serverName,
		String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort, final String emailUserId, final String emailPassword) throws SQLException{
		
		// lets get the list of users with pending emails.
		String sql = "select license_type, grantee_email_id, grantor_email_id 	" + 
			" from gr_license_grants" + 
			" where notification_sent = 0 and grant_state='pending'";
			
		PreparedStatement prepStmt = con.prepareStatement(sql);
		ResultSet rs = prepStmt.executeQuery();
		
		while (rs.next()){
			try {
				// each one of these users gets a seperate email. so it constitutes one email message.
				String licenseType = rs.getString("license_type");
				String grantorEmailId = rs.getString("grantor_email_id");
				String granteeEmailId = rs.getString("grantee_email_id");
				
				
				// drop any extra spaces at the beginning or end.
				if ((grantorEmailId != null) && (grantorEmailId.contains(" "))) {
					grantorEmailId = grantorEmailId.replace(" ", "");
				}
				if ((granteeEmailId != null) && (granteeEmailId.contains(" "))) {
					granteeEmailId = granteeEmailId.replace(" ", "");
				}
				
				
				// lets build the message body.
				String messageBody = "Hi, \n\n" +
					"You have been granted a " + licenseType + " License by " + grantorEmailId + 
					" for using the TraceCloud system. Please go to " + serverName +" and create an account to activate this license. " + 
					" \n\nPlease remember to use " + granteeEmailId + " as your account Id . \n" + 
					"\n\nBest Regards \n\nTrace Cloud System. ";
				
				
				// lets send the email out to the toEmailId;
				ArrayList to = new ArrayList();
				to.add(granteeEmailId);
				ArrayList cc = new ArrayList();
				cc.add(grantorEmailId);
				MessagePacket mP = new MessagePacket(to, cc, "A TraceCloud License has been assigned to you ", messageBody, "");
				EmailUtil.email(mP , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
				System.out.println("Granting license to " + granteeEmailId);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		prepStmt.close();
		rs.close();

		// now lets set the notification_sent  flag to a value , so that we won't process this row again.
		// once table gets big, we may consider, removing the rows that have been processed.
		sql = " update gr_license_grants  " +
			" set notification_sent = 1 " +
			" where notification_sent = 0 and grant_state='pending'";
		
		prepStmt = con.prepareStatement(sql);
		prepStmt.execute();
		prepStmt.close();

}

	// this routine sends a consolidated email to all users who need to approve a requirement.
	private static void notifyApprovers(java.sql.Connection con, String databaseType , 
			String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort, final String emailUserId, final String emailPassword) throws SQLException{
			
				// lets get the list of users with pending emails.
				String sql = "select distinct to_email_id " + 
					" from gr_messages m " + 
					" where m.message_sent_dt is null " +
					" and m.message_type = 'requirementApprovalNotification' ";
					
				PreparedStatement prepStmt = con.prepareStatement(sql);
				ResultSet rs = prepStmt.executeQuery();
				
				while (rs.next()){
					// each one of these users gets a seperate email. so it constitutes one email message.
					String toEmailId = rs.getString("to_email_id");
					// drop any extra spaces at the beginning or end.
					if ((toEmailId != null) && (toEmailId.contains(" "))) {
						toEmailId = toEmailId.replace(" ", "");
					}
					
					// now for each users, lets get the different type of email messages
					// and format the message body
					String messageBody = "";
					
					
					
					String requirementApprovalNotificationBlock = "";
					
					
					
					sql = " select  m.project_name, m.message_body " +
						" from gr_messages m " +
						" where m.to_email_id = ? " +
						" and m.message_type = 'requirementApprovalNotification' " +
						" and m.message_sent_dt is null " +
						" order by m.message_type, m.project_name ";
					
					PreparedStatement prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setString(1, toEmailId);
					ResultSet rs2 = prepStmt2.executeQuery();

					while (rs2.next()){
						String body = rs2.getString("message_body");
						// messageBody is ownerEmailId:##:ownerFirstname:##:ownerLastName:##:projectName:##:projectPrefix:##:projectName:##:fullTag:##:reqName:##:URLToReq:##:URLToApprove:##:URLtoReject
						String [] message = body.split(":##:");
								
						try {
						String ownerEmailId = message[0];
						String ownerFirstName = message[1];
						String ownerLastName = message[2];
								
						String projectName = message[3];
						String fullTag = message[4];
						String reqName = message[5];
						String URLToReq = message[6];
						String URLToApprove = message[7];
						String URLToReject  = message[8];
								
						
						requirementApprovalNotificationBlock += "<br><br><div ><table border='1' width='800'>"
								+ " <tr><td colspan=3> <b>Project</b> : " + projectName + " </td></tr> " 
								+ " <tr><td colspan=3><b>Requirement</b> : <a href='"+ URLToReq+ "'>" + fullTag + "</a> : " + reqName + " </td></tr>" 
								+ " <tr><td colspan=3><b>Owner</b> : " + ownerFirstName + " " + ownerLastName + "  (" + ownerEmailId +  ") </td></tr>" 
								+ " <tr> " 
									+ "<td align='center'><a href=' " + URLToReq + "'>  Preview </a></td> "
									+ "<td align='center'><a href=' " + URLToApprove + "'>  Approve </a></td> "
									+ " <td align='center'> <a href='" + URLToReject + "'>  Reject </a></td>" 
								+ "</tr>"
								+"</table><div>";
						
						}
						catch (Exception e){
							e.printStackTrace();
						}
					
						
					}
					prepStmt2.close();
					rs2.close();

					
					
					
					

					if (!(requirementApprovalNotificationBlock.equals(""))){
						messageBody += "<br> The Following requirements are waiting to be Approved " +
							"(or Rejected) by you.<br><br>";
						messageBody += requirementApprovalNotificationBlock;
					}
					
					
					
					
					if (messageBody.equals("")){
						continue;
					}
					// now lets send an email to the user.
					messageBody =   "<html><body>Hi, <br><br>" +
									"Here is a summary of your TraceCloud Requirements Activity. <br>" +
									messageBody +
									"<br><br>Best Regards <br><br>Trace Cloud System. </body></html>";
					
					
					
					// lets send the email out to the toEmailId;
					ArrayList to = new ArrayList();
					to.add(toEmailId);
					ArrayList cc = new ArrayList();
					MessagePacket mP = new MessagePacket(to, cc, " Please approve these requirements ", messageBody,"");
					System.out.println(" sending email to " + to + "\n\n  body is " + messageBody);
					EmailUtil.email(mP , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
					System.out.println(" sending email to " + to + "\n\n  body is " + messageBody);
					
					
					Calendar cal = Calendar.getInstance();
					
					// now lets set the email_sent_dt flag to a value , so that we won't process this row again.
					// once table gets big, we may consider, removing the rows that have been processed.
					
					if (databaseType.equals("mySQL")){
						sql = " update gr_messages m " +
						" set message_sent_dt = now() " +
						" where m.to_email_id like ? " +
						" and m.message_type ='requirementApprovalNotification' " +
						" and m.message_sent_dt is null";
					}
					else{
						// the db is oracle. so we use sysdate
						sql = " update gr_messages m " +
						" set message_sent_dt = sysdate " +
						" where m.to_email_id like ? " +
						" and m.message_type = 'requirementApprovalNotification' " +
						" and m.message_sent_dt is null";
					}
					
					
					prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setString(1, "%"+ toEmailId + "%");
					prepStmt2.execute();
					prepStmt2.close();
					
				}
				
				prepStmt.close();
				rs.close();
		}





	// this routine, get a list of users that need to be notified 
	// and for each user constructs 1 email with all the information they need to know
	// and shoots the email out.
	// once its done, it sends an message_sent_dt value for these records so
	// that they won't be emailed again.
	private static void notifyUsers(java.sql.Connection con, String databaseType , 
		String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort, final String emailUserId, final String emailPassword) throws SQLException{
		
			// lets get the list of users with pending emails.
			String sql = "select distinct to_email_id " + 
				" from gr_messages m " + 
				" where m.message_sent_dt is null " +
				" and m.message_type <> 'requirementApprovalNotification' " ;
				
			PreparedStatement prepStmt = con.prepareStatement(sql);
			ResultSet rs = prepStmt.executeQuery();
			
			while (rs.next()){
				// each one of these users gets a seperate email. so it constitutes one email message.
				String toEmailId = rs.getString("to_email_id");
				// drop any extra spaces at the beginning or end.
				if ((toEmailId != null) && (toEmailId.contains(" "))) {
					toEmailId = toEmailId.replace(" ", "");
				}
				
				// now for each users, lets get the different type of email messages
				// and format the message body
				String messageBody = "";
				
				
				String projectDeletedBlock = "";
				String newUserAddedToProjectBlock = "";
				String existingUserAddedToProject = "";
				
			
				String newCommentAddedToRequirementBlock = "";
				String requirementNameOrDescriptionChangedBlock = "";
				
				
				sql = " select m.message_type, m.project_name, m.message_body " +
					" from gr_messages m " +
					" where m.to_email_id = ? " +
					" and m.message_sent_dt is null " +
					" and m.message_type <> 'requirementApprovalNotification' " +
					" order by m.message_type, m.project_name ";
				
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setString(1, toEmailId);
				ResultSet rs2 = prepStmt2.executeQuery();

				while (rs2.next()){
					String messageType = rs2.getString("message_type");
					String block = rs2.getString("message_body");
					
					if (messageType.equals("projectDeleted")){
						projectDeletedBlock += block + "<br>";	
					}
					
					
					if (messageType.equals("newUserAddedToProject")){
						newUserAddedToProjectBlock += block + "<br>";	
					}

					if (messageType.equals("existingUserAddedToProject")){
						existingUserAddedToProject += block + "<br>";	
					}

					
					

					if (messageType.equals("newCommentAddedToRequirement")){
						newCommentAddedToRequirementBlock += block + "<br>";	
					}
					
					if (messageType.equals("requirementNameOrDescriptionChanged")){
						requirementNameOrDescriptionChangedBlock += block + "<br>";	
					}
					
				}
				prepStmt2.close();
				rs2.close();

				if (!(projectDeletedBlock.equals(""))){
					messageBody += projectDeletedBlock;
				}
				
				
				
				if (!(newUserAddedToProjectBlock.equals(""))){
					messageBody += newUserAddedToProjectBlock;
				}
				
				if (!(existingUserAddedToProject.equals(""))){
					messageBody += existingUserAddedToProject;
				}
				

				
				
				if (!(newCommentAddedToRequirementBlock.equals(""))){
					messageBody += "<br><table bgcolor='#d9edf7' width='900'><tr><td>&nbsp;</td></tr> <tr><td><b><font color='red'>Recent Comments " +
						"</font></b></td></tr> <tr><td>&nbsp;</td></tr>  </table><br><br>";
					messageBody += newCommentAddedToRequirementBlock;
				}
				
				if (!(requirementNameOrDescriptionChangedBlock.equals(""))){
					messageBody += "<br><table bgcolor='#d9edf7' width='900'> <tr><td>&nbsp;</td></tr>  <tr><td> <b><font color='red'>Recent Changes " +
						"</font></b></td></tr> <tr><td>&nbsp;</td></tr>  </table><br><br>";
					messageBody += requirementNameOrDescriptionChangedBlock;
				}
				
				
				if (messageBody.equals("")){
					continue;
				}
				// now lets send an email to the user.
				messageBody =   "Hi, <br><br>" +
								"Here is a summary of your TraceCloud Requirements Activity. <br>" +
								messageBody +
								"<br><br>Best Regards <br><br>Trace Cloud System. ";
				
				
				
				// lets send the email out to the toEmailId;
				ArrayList to = new ArrayList();
				to.add(toEmailId);
				ArrayList cc = new ArrayList();
				MessagePacket mP = new MessagePacket(to, cc, "Update on your TraceCloud Requirements", messageBody,"");
				EmailUtil.email(mP , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
				
				Calendar cal = Calendar.getInstance();
				
				// now lets set the email_sent_dt flag to a value , so that we won't process this row again.
				// once table gets big, we may consider, removing the rows that have been processed.
				
				if (databaseType.equals("mySQL")){
					sql = " update gr_messages m " +
					" set message_sent_dt = now() " +
					" where m.to_email_id like ? " +
					" and m.message_type <> 'requirementApprovalNotification' " +
					" and m.message_sent_dt is null";
				}
				else{
					// the db is oracle. so we use sysdate
					sql = " update gr_messages m " +
					" set message_sent_dt = sysdate " +
					" where m.to_email_id like ? " +
					" and m.message_type <> 'requirementApprovalNotification' " +
					" and m.message_sent_dt is null";
				}
				
				
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setString(1, "%"+ toEmailId + "%");
				prepStmt2.execute();
				prepStmt2.close();
				
			}
			
			prepStmt.close();
			rs.close();
	}





}
