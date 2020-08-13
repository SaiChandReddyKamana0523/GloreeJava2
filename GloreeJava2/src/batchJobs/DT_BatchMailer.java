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


public class DT_BatchMailer {

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
   		
   		
   		String correctSyntax = "DT_BatchMailer serverName:www.tracecloud.com databaseType:mySQL userName:dbuserId password:dbPassword  "+
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
		    
		    
		    
		    System.out.println("Starting Notify Approvers");
		    sendReminders(con, databaseType, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, decryptedEmailPassword);
		    System.out.println("Completed Notify Approvers");
		    
		    
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


	// this routine sends a consolidated email to all users who need to approve a requirement.
	private static void sendReminders(java.sql.Connection con, String databaseType , 
			String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort, final String emailUserId, final String emailPassword) throws SQLException{
			
		
		
				// Step1 . lets get the list of all the users who should get an email. This is the distinct list of 'Created_by' and 'Owner'
		
				// Step 2 . for each user, lets send 1 email.
		
				// Step 3 : List all the tasks where is the owner or creator.
		
		
		
		
		
				// Step1 . lets get the list of all the users who should get an email. This is the distinct list of 'Created_by' and 'Owner'
		
			
				// lets get the list of users with pending emails.
				String sql = "select distinct created_by 'to_email_id' from dt_tasks  where completion < 100 "  +
						" union distinct " +
						" select distinct owner 'to_email_id' from dt_tasks where completion < 100  ";
					
				PreparedStatement prepStmt = con.prepareStatement(sql);
				ResultSet rs = prepStmt.executeQuery();
				
				while (rs.next()){
					// each one of these users gets a seperate email. so it constitutes one email message.
					
					
					String toEmailId = rs.getString("to_email_id");
					// drop any extra spaces at the beginning or end.
					if ((toEmailId != null) && (toEmailId.contains(" "))) {
						toEmailId = toEmailId.replace(" ", "");
					}


					// lets hard code , so that only nathan@tracecloud.com gets emails
					if (!(toEmailId.equals("nathan@tracecloud.com"))){
						System.out.println("SRT Not Nathan. So skipping " + toEmailId);
						continue;
					}


					System.out.println("srt lets build a message for email id " + toEmailId);

					
					
					// Step 2 . for each user, lets send 1 email.
					String messageBody = "";
					
					
					// lets get all distinct topics in this email
					
					// for each topic, lets get all the open tasks.
					sql = " select distinct tags, count(*) 'numberOfTasks'  "
							+ " from dt_tasks "
							+ " where ( created_by = ? or owner = ? ) "
							+ " and completion < 100 "
							+ " group by tags ";


					PreparedStatement prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setString(1, toEmailId);
					prepStmt2.setString(2, toEmailId);

					String tagsMessageBody = "<br>";

					ResultSet rs2 = prepStmt2.executeQuery();
					while (rs2.next()){
						String tags = rs2.getString("tags");
						int numberOfTasks = rs2.getInt("numberOfTasks");
						
						if (numberOfTasks == 0 ){
							System.out.println("No open tasks for topic " + tags + " so lets skip ");
							continue;
						}
						// for this tag , lets get all the tasks
						tagsMessageBody += "<br><table border='2'> <tr><td style='width:100px'><b> Topic </td><td style='width:600px'> <b>"+ tags +"</b></td></tr></table><br>";

						tagsMessageBody += "<br><div >";
							
						
							sql = " select title, description, created_by, owner, completion, priority, tags "
									+ " from dt_tasks "
									+ " where (created_by = ? or  owner = ? ) "
									+ " and tags = ? "
									+ " and completion < 100 "
									+ " order by priority  ";
							
							PreparedStatement prepStmt3 = con.prepareStatement(sql);
							prepStmt3.setString(1, toEmailId);
							prepStmt3.setString(2, toEmailId);
							prepStmt3.setString(3, tags);
					
							


							ResultSet rs3 = prepStmt3.executeQuery();

							
							while (rs3.next()){
								String title = rs3.getString("title");
								String description = rs3.getString("description");
								String createdBy = rs3.getString("created_by");
								int completion = rs3.getInt("completion");
								int priority  = rs3.getInt("priority");
								String owner = rs3.getString("owner");
								
								String priorityString = "Medium";
								String borderStyle = "bgcolor='#ffbf7f'";
								
								if (priority == 1){
									priorityString = "High";
									borderStyle="bgcolor='#FF7F7F'";
								}

								if (priority == 3){
									priorityString = "Low";
									borderStyle = "bgcolor='white'";
									
								}	
								
								
								String ownerStyle = "";
								if ((owner !=null) && (owner.equals(toEmailId))){
									ownerStyle = "bgcolor='red'";
								}
								
								try {
								
								tagsMessageBody +=  " <br><br><table  "+ borderStyle +"  > "
										+ " <tr> <td align='left' style='width:100px'> Title </td> " 
											+ "<td align='left' style='width:600px'>"+ title + "</td> </tr> "
										+ " <tr> <td align='left'> Description </td> " 
											+ "<td align='left'>"+ description + "</td> </tr>"
										+ " <tr> <td align='left'> Created By </td> " 
											+ "<td align='left'>"+ createdBy + "</td> </tr> "
										+ " <tr "+ ownerStyle +"> <td align='left'> Owner </td> " 
											+ "<td align='left'>"+ owner + "</td> "
										+ " <tr> <td align='left'> Priority </td> " 
											+ "<td align='left'>"+ priorityString + "</td> "	
										+ " <tr> <td align='left'> Completion </td> " 
											+ "<td align='left'>"+ completion + "</td> </tr></table> ";
								}
								catch (Exception e){
									e.printStackTrace();
								}
							
								
							}
							

							tagsMessageBody += "<br><br></div >";
								
							prepStmt3.close();
							rs3.close();

							
					}
					
												
					
					
					
					
					
					
					
					
											
												
										
					

					
					
					messageBody += tagsMessageBody;
					
					if (messageBody.equals("")){
						continue;
					}
					// now lets send an email to the user.
					messageBody =   "<html><body>Hi, <br><br>" +
									"Here is a summary of all Work <br>" +
									messageBody +
									"<br><br>Best Regards <br><br>DipT Team (Do it Please , Thank you)  </body></html>";
					
					
					
					// lets send the email out to the toEmailId;
					ArrayList to = new ArrayList();
					to.add(toEmailId);
					ArrayList cc = new ArrayList();
					MessagePacket mP = new MessagePacket(to, cc, " Your DIP T tasks  ", messageBody,"");
					System.out.println(" sending email to " + to + "\n\n  body is " + messageBody);
					EmailUtil.email(mP , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
					System.out.println(" sent email to " + to + "\n\n  body is " + messageBody);
					
					
				}
				
				prepStmt.close();
				rs.close();
		}






}
