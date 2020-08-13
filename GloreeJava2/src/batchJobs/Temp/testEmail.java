package batchJobs.Temp;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import javax.mail.PasswordAuthentication;


public class testEmail {

	public static void main(String[] args) {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		
		System.out.println("\n\n--------------------------\n");
		System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
		System.out.println("\n--------------------------\n\n");
		
		// this program expects some input parameters. if they are empty, then it can not run
		// eg are : healthCheck URL, email user id, emailPassword, smtp server etc..
		String host = "";
		String sslPort = "";
		String nonSSLPort = "";
		String fromEmailUserId = "";
		String emailPassword = "";

		String toEmailUserId = "";
		
		
		 for (int i = 0; i < args.length; i++){
	         String inputParam = args[i];
	         if (inputParam.contains("host:")){
	        	 host = inputParam.replace("host:", "");
	         }
	         if (inputParam.contains("sslPort:")){
	        	 sslPort = inputParam.replace("sslPort:", "");
	         }
	         if (inputParam.contains("nonSSLPort:")){
	        	 nonSSLPort = inputParam.replace("nonSSLPort:", "");
	         }
	         if (inputParam.contains("fromEmailUserId:")){
	        	 fromEmailUserId = inputParam.replace("fromEmailUserId:", "");
	         }
	        
	         if (inputParam.contains("toEmailUserId:")){
	        	 toEmailUserId = inputParam.replace("toEmailUserId:", "");
	         }
	         
	         
	         if (inputParam.contains("emailPassword:")){
	        	 emailPassword = inputParam.replace("emailPassword:", "");
	         }


		 }
	 
		 
		 String correctSyntax = "testEmail "+
			 "host:smtp.gmail.com "+
			 "nonSSLPort:587 "+
			 "sslPort:465 "+
			 "fromEmailUserId:emailId@company.com "+
			 "toEmailUserId:emailId@company.com "+
			 "emailPassword:emailPassword "
			 ;

		 
		 if (host.equals("")){
			 System.out.println("Error : host is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 if (sslPort.equals("")){
			 System.out.println("Error : sslPort is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 if (nonSSLPort.equals("")){
			 System.out.println("Error : nonSSLPort is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 if (fromEmailUserId.equals("")){
			 System.out.println("Error : emailUserId is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 if (toEmailUserId.equals("")){
			 System.out.println("Error : emailUserId is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		  
		 if (emailPassword.equals("")){
			 System.out.println("Error : emailPassword is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }

			int nonSSLPortInt = 0;
			try {
				nonSSLPortInt = Integer.parseInt(nonSSLPort);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		
			int sslPortInt = 0;
			try {
				sslPortInt = Integer.parseInt(sslPort);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		
			final String finalEmailUserId  = fromEmailUserId;
			final String finalEmailPassword = emailPassword;
			
			
			Properties props = new Properties();
			Session session = null;
		////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////
			
			/*
			
			System.out.println("\n\ndebug : option 1: trying the non ssl version");

			
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", nonSSLPort);
			props.put("mail.transport.protocol", "smtp");

			Authenticator authenticator = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					String username = finalEmailUserId;
					String password = finalEmailPassword;
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(props, authenticator);

			try {

				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(finalEmailUserId));
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(toEmailUserId));
				message.setSubject("Testing Subject from TraceCloud : Option 1");
				message.setText("Dear Mail Crawler,"
						+ "\n\n No spam to my email, please!");

				Transport transport = session.getTransport();
				transport.connect();

				Transport.send(message);
				System.out
						.println("debug : option 1: trying the non SSL version: Success");

			} catch (Exception e) {
				System.out
						.println("debug : option 1: trying the non ssl version : Failed");
				e.printStackTrace();
			}
 		
		////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////
		System.out.println("\n\ndebug : option 2: trying the SSL version");
		
		
		props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.socketFactory.port", sslPort);
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", sslPort);
 
		session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(finalEmailUserId,finalEmailPassword);
				}
			});
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(finalEmailUserId));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(toEmailUserId));
			message.setSubject("Testing Subject from TraceCloud : Option 2");
			message.setText("Dear Mail Crawler," +
					"\n\n No spam to my email, please!");
 
			Transport.send(message);
			System.out.println("debug : option 2: trying the ssl version:Success");
		} catch (Exception e) {
			System.out.println("debug : option 2: trying the ssl version:Failed");
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////
		System.out.println("\n\ndebug : option 3: trying the third version");
		
		
		props = new Properties();
		
		
		props.put("mail.smtp.user", finalEmailUserId);
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", sslPort);
		props.put("mail.smtp.starttls.enable","true");
		props.put("mail.smtp.debug", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.port", sslPort);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");

		
		session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(finalEmailUserId,finalEmailPassword);
				}
			});
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(finalEmailUserId));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(toEmailUserId));
			message.setSubject("Testing Subject from TraceCloud : Option 3");
			message.setText("Dear Mail Crawler," +
					"\n\n No spam to my email, please!");

			Transport transport = session.getTransport("smtps");
			transport.connect(host, sslPortInt, finalEmailUserId, emailPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();

			System.out.println("debug : option 3: trying the third version:Success");
		} catch (Exception e) {
			System.out.println("debug : option 3: trying the third version:Failed");
			e.printStackTrace();
		}
		
		
	*/
		
		System.out.println("\n\ndebug : option 4: trying the fourth version");
		
		
		props = new Properties();
		
		
        
        props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", sslPort);
        props.put("mail.debug", "true");
		

		try {
 

			
			
			 session = Session.getDefaultInstance(props);
		        Message message = new MimeMessage(session);
		        message.setFrom(new InternetAddress(finalEmailUserId));
		        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmailUserId));
		        message.setSubject("Notification");
		        message.setText("Successful!"); // as "text/plain"
		        message.setSentDate(new Date());
		        Transport.send(message);
		        
		        
			System.out.println("debug : option 4: trying the fourth version:Success");
		} catch (Exception e) {
			System.out.println("debug : option 4: trying the fourth version:Failed");
			e.printStackTrace();
		}
		
		
		////////////////////////////////////////////////////////////////////////
		
		
		System.out.println("\n\ndebug : option 5: trying the fifth version");
		

		try {
 

		 
		      Properties properties = System.getProperties();

		      properties.setProperty("mail.smtp.host", host);
		      session = Session.getDefaultInstance(properties);
		      
		        MimeMessage message = new MimeMessage(session);

		         // Set From: header field of the header.
		         message.setFrom(new InternetAddress(finalEmailUserId));

		         // Set To: header field of the header.
		         message.addRecipient(Message.RecipientType.TO,new InternetAddress(toEmailUserId));
		         message.setSubject("This is the Subject Line!");
		         message.setText("This is actual message");

		         // Send message
		         Transport.send(message);
		         System.out.println("Sent message successfully....");

			
		        
		         System.out.println("debug : option 5: trying the fifth version:Success");
		} catch (Exception e) {
			System.out.println("debug : option 5: trying the fifth version:Failed");
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
	}
	
}



