package com.gloree.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;


import com.gloree.beans.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.security.Security;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.mail.PasswordAuthentication;
import javax.naming.InitialContext;



public class EmailUtil {

	

	public synchronized static void email_Deprecated(MessagePacket mP,
			String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort, final String emailUserId, final String emailPassword ) {
		
		
			try{

				
				

				
				Session session;
				if (smtpAuth.equals("true")){

					
					System.out.println("\n\n\nIn plain Emai . SMTP auth is true" );
						
						
						
					Properties props = new Properties();
					//props.put("mail.debug", "true");
					props.put("mail.smtp.auth", smtpAuth);
					props.put("mail.smtp.starttls.enable", "true");
					props.put("mail.smtp.host", mailHost);
					props.put("mail.smtp.port", smtpPort);
					props.put("mail.transport.protocol", transportProtocol);
					
					Authenticator authenticator = new Authenticator() {
						public PasswordAuthentication getPasswordAuthentication() {
							String username = emailUserId;
							String password = emailPassword;
							return new PasswordAuthentication(username, password);
						}
					};
					session = Session.getInstance(props, authenticator);
				}
				else {


					System.out.println("\n\n\nIn plain Emai . SMTP auth is true" );
						
					Properties props = new Properties();
					//props.put("mail.debug", "true");
					props.put("mail.smtp.host", mailHost);
					props.put("mail.smtp.port", smtpPort);
					
					session = Session.getInstance(props);
				}

				
				try {

					Message message = new MimeMessage(session);
					message.setFrom(new InternetAddress(emailUserId));
					
					message.setSubject(mP.getSubject());
					message.setContent(mP.getBody(),"text/plain");

		    		// lets get the to lists and add a recipient.
		            ArrayList to = mP.getTo();
		            if (to.size() > 0 ){
			            Iterator i = to.iterator();
			            InternetAddress[] addressTo = new InternetAddress[to.size()];
			            int counter = 0;
			            while (i.hasNext()) {
			            	addressTo[counter] = new InternetAddress((String) i.next()) ;
			            	counter++;
			            }
			            if (addressTo.length > 0){
			            	message.addRecipients(Message.RecipientType.TO, addressTo );
			            }
		            }
		           
		            // lets set the cc list.
		            ArrayList cc = mP.getCc();
		            if (cc.size() > 0 ){
			            Iterator i = cc.iterator();
			            InternetAddress[] addressCc = new InternetAddress[cc.size()];
			            int counter = 0;
			            while (i.hasNext()) {
			            	addressCc[counter] = new InternetAddress((String) i.next()) ;
			            	counter++;
			            }
			            if (addressCc.length > 0){
			            	message.addRecipients(Message.RecipientType.CC, addressCc );
			            }
		            } 
		            
					
					Transport transport = session.getTransport();
					transport.connect();

					Transport.send(message);
					System.out.println("debug : option 1: trying the prod version: Success");

				} catch (Exception e) {
					System.out.println("debug : option 1: trying the non ssl version : Failed");
					e.printStackTrace();
				}
	        }
			catch (Exception e){
				e.printStackTrace();
			}
		}

	// this is exact repica of email , but I am doing it as a separate rounte. Why? you may ask, when it can be a simple variable change?
	// there are a lot of diebold customizations whose signature will need to change, adn I don't want that head ache.
	
	public synchronized static void emailAsHTML_Deprecated(MessagePacket mP,
			String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort, final String emailUserId, final String emailPassword ) {
		
		
			try{

				
		


				
				Session session;
				if (smtpAuth.equals("true")){
					
					System.out.println("\n\n\nIn Emai as HTML . SMTP auth is true" );
					Properties props = new Properties();
					//props.put("mail.debug", "true");
					props.put("mail.smtp.auth", smtpAuth);
					props.put("mail.smtp.starttls.enable", "true");
					props.put("mail.smtp.host", mailHost);
					props.put("mail.smtp.port", smtpPort);
					props.put("mail.transport.protocol", transportProtocol);
					
					Authenticator authenticator = new Authenticator() {
						public PasswordAuthentication getPasswordAuthentication() {
							String username = emailUserId;
							String password = emailPassword;
							return new PasswordAuthentication(username, password);
						}
					};
					session = Session.getInstance(props, authenticator);
				}
				else {
					

					System.out.println("\n\n\nIn Emai as HTML . SMTP auth is false" );
					
					
					Properties props = new Properties();
					//props.put("mail.debug", "true");
					props.put("mail.smtp.host", mailHost);
					props.put("mail.smtp.port", smtpPort);
					
					
					session = Session.getInstance(props);
					
					
					
					

					
				}

				try {

					Message message = new MimeMessage(session);
					message.setFrom(new InternetAddress(emailUserId));
					
					message.setSubject(mP.getSubject());
					message.setContent(mP.getBody(),"text/html; charset=utf-8");
					//messageBodyPart.setContent( messageContent, "text/html; charset=utf-8" ); // <---- 


		    		// lets get the to lists and add a recipient.
		            ArrayList to = mP.getTo();
		            if (to.size() > 0 ){
			            Iterator i = to.iterator();
			            InternetAddress[] addressTo = new InternetAddress[to.size()];
			            int counter = 0;
			            while (i.hasNext()) {
			            	addressTo[counter] = new InternetAddress((String) i.next()) ;
			            	counter++;
			            }
			            if (addressTo.length > 0){
			            	message.addRecipients(Message.RecipientType.TO, addressTo );
			            }
		            }
		           
		            // lets set the cc list.
		            ArrayList cc = mP.getCc();
		            if (cc.size() > 0 ){
			            Iterator i = cc.iterator();
			            InternetAddress[] addressCc = new InternetAddress[cc.size()];
			            int counter = 0;
			            while (i.hasNext()) {
			            	addressCc[counter] = new InternetAddress((String) i.next()) ;
			            	counter++;
			            }
			            if (addressCc.length > 0){
			            	message.addRecipients(Message.RecipientType.CC, addressCc );
			            }
		            } 
		            
					
					Transport transport = session.getTransport();
					transport.connect();

					Transport.send(message);
					System.out.println("debug : option 1: trying the prod version: Success");

				} catch (Exception e) {
					System.out.println("debug : option 1: trying the non ssl version : Failed");
					e.printStackTrace();
				}
	        }
			catch (Exception e){
				e.printStackTrace();
			}
		}
	
	
	
	public synchronized static void email(MessagePacket mP, 
			String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort, final String emailUserId,
			final String emailPassword ) {
		try{

			
			

			
			
			Session session;
			if (smtpAuth.equals("true")){
				
				
				
				Properties props = new Properties();

				//props.put("mail.debug", "true");
				props.put("mail.smtp.auth", smtpAuth);
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.host", mailHost);
				props.put("mail.smtp.port", smtpPort);
				props.put("mail.transport.protocol", transportProtocol);


				
				Authenticator authenticator = new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						String username = emailUserId;
						String password = emailPassword;
						return new PasswordAuthentication(username, password);
					}
				};
				session = Session.getInstance(props, authenticator);
			}
			else {

				Properties props = new Properties();

			//	props.put("mail.debug", "true");
				props.put("mail.smtp.host", mailHost);
				props.put("mail.smtp.port", smtpPort);


				
				session = Session.getInstance(props);
			}

			
    		MimeMessage message = new MimeMessage(session);
    		message.setFrom(new InternetAddress(emailUserId));
    		message.setSubject(mP.getSubject());
    				
    		// lets get the to lists and add a recipient.
            ArrayList to = mP.getTo();
            if (to.size() > 0 ){
	            Iterator i = to.iterator();
	            InternetAddress[] addressTo = new InternetAddress[to.size()];
	            int counter = 0;
	            while (i.hasNext()) {
	            	addressTo[counter] = new InternetAddress((String) i.next()) ;
	            	counter++;
	            }
	            if (addressTo.length > 0){
	            	message.addRecipients(Message.RecipientType.TO, addressTo );
	            }
            }
           
            // lets set the cc list.
            ArrayList cc = mP.getCc();
            if (cc.size() > 0 ){
	            Iterator i = cc.iterator();
	            InternetAddress[] addressCc = new InternetAddress[cc.size()];
	            int counter = 0;
	            while (i.hasNext()) {
	            	addressCc[counter] = new InternetAddress((String) i.next()) ;
	            	counter++;
	            }
	            if (addressCc.length > 0){
	            	message.addRecipients(Message.RecipientType.CC, addressCc );
	            }
            } 
            
            
	        // create the message part 
    	    MimeBodyPart htmlPart = new MimeBodyPart();

    	    //fill message
    	    //SRT TODO http://stackoverflow.com/questions/14744197/best-practices-sending-javamail-mime-multipart-emails-and-gmail
    	    //
    	    //  The code we used to make it work
    	    //
    	    //
    	    //
    	    //
    	    //
    	    //
    	    //
    	    htmlPart.setContent(mP.getBody(),"text/html; charset=utf-8");

    	    
    	    
    	    Multipart multipart = new MimeMultipart();
    	    multipart.addBodyPart(htmlPart);
    	    message.setContent(multipart);
    	    
    	    
    	    // Send the message
    	    Transport.send( message );
        }
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public synchronized static void emailWithAttachment(MessagePacket mP, 
			String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort, final String emailUserId, final String emailPassword ) {
		try{

			
			

			
			
			Session session;
			if (smtpAuth.equals("true")){
				
				System.out.println("\n\n\nIn Emai with attachment . SMTP auth is true" );
				
				
				
				Properties props = new Properties();

				//props.put("mail.debug", "true");
				props.put("mail.smtp.auth", smtpAuth);
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.host", mailHost);
				props.put("mail.smtp.port", smtpPort);
				props.put("mail.transport.protocol", transportProtocol);


				
				Authenticator authenticator = new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						String username = emailUserId;
						String password = emailPassword;
						return new PasswordAuthentication(username, password);
					}
				};
				session = Session.getInstance(props, authenticator);
			}
			else {

				System.out.println("\n\n\nIn Emai with attachment . SMTP auth is false" );
				Properties props = new Properties();

				//props.put("mail.debug", "true");
				props.put("mail.smtp.host", mailHost);
				props.put("mail.smtp.port", smtpPort);


				
				session = Session.getInstance(props);
			}

			
    		MimeMessage message = new MimeMessage(session);
    		message.setFrom(new InternetAddress(emailUserId));
    		message.setSubject(mP.getSubject());
    				
    		// lets get the to lists and add a recipient.
            ArrayList to = mP.getTo();
            if (to.size() > 0 ){
	            Iterator i = to.iterator();
	            InternetAddress[] addressTo = new InternetAddress[to.size()];
	            int counter = 0;
	            while (i.hasNext()) {
	            	addressTo[counter] = new InternetAddress((String) i.next()) ;
	            	counter++;
	            }
	            if (addressTo.length > 0){
	            	message.addRecipients(Message.RecipientType.TO, addressTo );
	            }
            }
           
            // lets set the cc list.
            ArrayList cc = mP.getCc();
            if (cc.size() > 0 ){
	            Iterator i = cc.iterator();
	            InternetAddress[] addressCc = new InternetAddress[cc.size()];
	            int counter = 0;
	            while (i.hasNext()) {
	            	addressCc[counter] = new InternetAddress((String) i.next()) ;
	            	counter++;
	            }
	            if (addressCc.length > 0){
	            	message.addRecipients(Message.RecipientType.CC, addressCc );
	            }
            } 
            
            
	        // create the message part 
    	    MimeBodyPart messageBodyPart = new MimeBodyPart();

    	    //fill message
    	    //SRT TODO http://stackoverflow.com/questions/14744197/best-practices-sending-javamail-mime-multipart-emails-and-gmail
    	    //
    	    //  USE THE URL HERE 
    	    //
    	    //
    	    //
    	   // messageBodyPart.setText(mP.getBody());
    	    messageBodyPart.setContent(mP.getBody(),"text/html; charset=utf-8");

    	    
    	    Multipart multipart = new MimeMultipart();
    	    multipart.addBodyPart(messageBodyPart);

    	    // Part two is attachment
    	    if ((mP.getFileAttachmentPath() != null) && (mP.getFileAttachmentPath()!= null)) {
	    	    messageBodyPart = new MimeBodyPart();
	    	    DataSource source = new FileDataSource(mP.getFileAttachmentPath());
	    	    messageBodyPart.setDataHandler(new DataHandler(source));
	    	    messageBodyPart.setFileName(mP.getFileAttachmentPath());
	    	    multipart.addBodyPart(messageBodyPart);
    	    }
    	    // Put parts in message
    	    message.setContent(multipart);
    	    


    	    // Send the message
    	    Transport.send( message );
        }
		catch (Exception e){
			e.printStackTrace();
		}
	}

	
	// stores a message in the db for sending a consolidated file at the end of the day,.
	public static void storeMessage(String projectName, String toEmailId, 
		String messageType, String messageBody, String databaseType){
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();			
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
		
	}	
}

