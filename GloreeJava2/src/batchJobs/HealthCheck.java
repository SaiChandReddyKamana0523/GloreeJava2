package batchJobs;

import org.json.*;


import java.net.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.*; 

import org.apache.poi.util.IOUtils;

import com.gloree.beans.MessagePacket;
import com.gloree.utils.EmailUtil;



public class HealthCheck {

	public static void main(String[] args) {
		// this program expects some input parameters. if they are empty, then it can not run
		// eg are : healthCheck URL, email user id, password, smtp server etc..
		String healthCheckURL = "";
		String mailHost = "";
		String transportProtocol = "";
		String smtpAuth = "";
		String smtpPort = "";
		String smtpSocketFactoryPort = "";
		String emailUserId = "";
		String toEmailIds = "";
		String toSMSEmailIds = "";
	
		
		String ldapUserName = "";
        
        String encryptedEmailPassword = "";
        String encryptedLdapPassword = "";
		
		 for (int i = 0; i < args.length; i++){
	         String inputParam = args[i];
	         if (inputParam.contains("healthCheckURL:")){
	        	 healthCheckURL = inputParam.replace("healthCheckURL:", "");
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

	         if (inputParam.contains("toEmailIds:")){
	        	 toEmailIds = inputParam.replace("toEmailIds:", "");
	         }
	         if (inputParam.contains("toSMSEmailIds:")){
	        	 toSMSEmailIds = inputParam.replace("toSMSEmailIds:", "");
	         }

	         if (inputParam.contains("ldapUserName:")){
	        	 ldapUserName = inputParam.replace("ldapUserName:", "");
	         }
	         if (inputParam.contains("encryptedLdapPassword:")){
	        	 encryptedLdapPassword = inputParam.replace("encryptedLdapPassword:", "");
	         }

	         
	         
		 }
	 
		 
		 String correctSyntax = "HealthCheck healthCheckURL:https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQOJFILBFHILLHGFGGFCIWEUEOLSKNGGAQWEMBX "+
			 "mailHost:smtp.gmail.com "+
			 "transportProtocol:smtp "+
			 "smtpAuth:true "+
			 "smtpPort:465 "+
			 "smtpSocketFactoryPort:465 "+
			 "emailUserId:emailId@company.com "+
			 "encryptedEmailPassword:123-344-5454-434-5454 " +
			 "toEmailIds:nathan@tracecloud.com,sami@tracecloud.com " + 
			 "toSMSEmailIds:4084311024@txt.att.net" +
			 "ldapUserName:optioanlLdapUserId " +
			 "encryptedLdapPassword:1230-54-546-7676 " 
			 ;
		 if (healthCheckURL.equals("")){
			 System.out.println("Error : heatlthCheckURL is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
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
		 
		 if (toEmailIds.equals("")){
			 System.out.println("Error : toEmailIds is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
	
		 
		 if (encryptedEmailPassword.equals("")){
			 System.out.println("Error : encryptedEmailPassword is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		  
		 
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
		String decryptedEmailPassword = decrypted;
		System.out.println("Successfully Decrypted the encrypted Email Password");
		

		String decryptedLdapPassword = "";
		if (encryptedLdapPassword != null){
			System.out.println("Decrypting the encrypted LdapPassword");
			decrypted = "";
			// lets decrypt
			enA = encryptedLdapPassword.split("-");
			for (int j=0; j< enA.length; j++ ) {
				int charInt = Integer.parseInt(enA[j]);
				charInt = charInt / 129;
				charInt = charInt - 25;
				char c = (char) charInt;
				decrypted += c;
			}
			decryptedLdapPassword = decrypted;
			System.out.println("Successfully Decrypted the encrypted LdapPassword");
		}
		 
		
		
		 // kludge : wasn't able to send &action=getMyProjects as part of healthCheckURL in the command line. so adding it here
		healthCheckURL += "&action=getMyProjects";
		
        boolean trouble = false;
		String troubleMessage = "";
		String response = "";		
		try {
			URL u = new URL(healthCheckURL);
			
			URLConnection yc = u.openConnection();
	        

	        String input = ldapUserName + ":" + decryptedLdapPassword;
	        
	        if ((input != null) && (!(input.equals("")))){
		        String encoding = base64Encode(input);
		        yc.setRequestProperty("Authorization", "Basic "  + encoding);
	        }
	        
	        
			BufferedReader in = new BufferedReader(
	           new InputStreamReader(
	           yc.getInputStream()));
	        String inputLine = "";
	        while ((inputLine = in.readLine()) != null) 
	        	response += inputLine;
	        in.close();
	    } 
		catch (Exception e) {
			trouble = true;
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			troubleMessage  += "\n" + sw.toString();
			handleTrouble(troubleMessage , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, 
					emailUserId, decryptedEmailPassword, toEmailIds, toSMSEmailIds);
			return;
		}
		if (response.equals("")){
			// happens if the tomcat server is down and no response.
			trouble = true;
			troubleMessage  += "\n" + " empty response string" ;
			handleTrouble(troubleMessage , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort,
					emailUserId, decryptedEmailPassword, toEmailIds, toSMSEmailIds );
			return;
		}
		
		try {
	        JSONObject jsonResponse = new JSONObject(response);
			String errorMessage = (String) jsonResponse.get("errorMessage");
			// if everything is OK, we should have not null errorMessage string that is empty.
			if (!((errorMessage != null ) && (errorMessage.equals("")))){
				trouble = true;
				troubleMessage  += "\n" + " invalid api response" ;
				handleTrouble(troubleMessage , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort,
						emailUserId, decryptedLdapPassword, toEmailIds, toSMSEmailIds );
				return;
			}
		}
		catch (Exception e){
			trouble = true;
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			troubleMessage  += "\n" + sw.toString();
			handleTrouble(troubleMessage , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort,
					emailUserId, decryptedLdapPassword, toEmailIds, toSMSEmailIds);
			return;
		}
	
		
		System.out.println("All Quiet on the Western Front!!!  " + Calendar.getInstance().getTime());
	}
	
	public static void handleTrouble(String troubleMessage ,
			String mailHost, String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort,
			final String emailUserId, final String emailPassword , String toEmailIds, String toSMSEMailIds) {
		
		// lets send the email out to the toEmailId;
		if ((toEmailIds != null) && (!(toEmailIds.equals("")))){
			// if it's a comma sepearated list, lets split and add to the to array
			// else lets just add the string to the to array, as it must be a single emmail id
			ArrayList to = new ArrayList();
			if (toEmailIds.contains(",")){
				String [] toEmailIdsArray= toEmailIds.split(",");
				for (int j=0; j < toEmailIdsArray.length; j++ ){
					to.add(toEmailIdsArray[j]);
				}
			}
			else {
				to.add(toEmailIds);
			}
			ArrayList cc = new ArrayList();
			MessagePacket mP = new MessagePacket(to, cc, "Prod Server Down", troubleMessage ,"");
			EmailUtil.email(mP  , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
			
		}
		
		if ((toSMSEMailIds != null) && (!(toSMSEMailIds.equals("")))){
			// if it's a comma sepearated list, lets split and add to the to array
			// else lets just add the string to the to array, as it must be a single emmail id
			ArrayList to = new ArrayList();
			if (toSMSEMailIds.contains(",")){
				String [] toSMSEmailIdsArray= toSMSEMailIds.split(",");
				for (int j=0; j < toSMSEmailIdsArray.length; j++ ){
					to.add(toSMSEmailIdsArray[j]);
				}
			}
			else {
				to.add(toSMSEMailIds);
			}
			// lets do an sms
			ArrayList cc = new ArrayList();
			MessagePacket  mP = new MessagePacket(to, cc, "TraceCloud Prod Server Down", "TraceCloud Prod Server Down!!!","");
			EmailUtil.email(mP , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
			
		}
		
		// lets send an SMS to the toSMSIds
		
	}
	
	  public static String base64Encode(String s) {
		    ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		    Base64OutputStream out = new Base64OutputStream(bOut);
		    try {
		      out.write(s.getBytes());
		      out.flush();
		    } catch (IOException exception) {
		    }
		    return bOut.toString();
		  }
		}




