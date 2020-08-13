package batchJobs;

import org.json.*;


import java.net.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.*; 

import org.apache.poi.util.IOUtils;

import com.gloree.beans.MessagePacket;
import com.gloree.utils.EmailUtil;



public class BatchScheduledReports {

	public static void main(String[] args)  {
		// this program expects some input parameters. if they are empty, then it can not run
		// eg are : healthCheck URL, email user id, password, smtp server etc..
		String startScheduledReportURL = "";
		
		
		String ldapUserName = "";
        String encryptedLdapPassword = "";
        
      
		 for (int i = 0; i < args.length; i++){
	         String inputParam = args[i];
	         if (inputParam.contains("startScheduledReportURL:")){
	        	 startScheduledReportURL = inputParam.replace("startScheduledReportURL:", "");
	         }
	         
	         if (inputParam.contains("ldapUserName:")){
	        	 ldapUserName = inputParam.replace("ldapUserName:", "");
	         }

	       
	         if (inputParam.contains("encryptedLdapPassword:")){
	        	 encryptedLdapPassword = inputParam.replace("encryptedLdapPassword:", "");
	         }
	         

		 }
	 
		 
		 String correctSyntax = "BatchScheduledReports startScheduledReportURL:https://www.tracecloud.com/GloreeJava2/RESTAPI?key=HIDLIZJKQOJFILBFHILLHGFGGFCIWEUEOLSKNGGAQWEMBX "+
			 "ldapUserName:optioanlLdapUserId " +
			 "encryptedLdapPassword:123-445-656-776-8787"
			 ;
		 if (startScheduledReportURL.equals("")){
			 System.out.println("Error : startScheduledReportURL is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 
		 if (encryptedLdapPassword.equals("")){
			 System.out.println("Error : encryptedLdapPassword is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 
		 String decryptedLdapPassword = "";
		 
		if (encryptedLdapPassword != null){
			System.out.println("Decrypting the encrypted LdapPassword");
			String decrypted = "";
			// lets decrypt
			String[] enA = encryptedLdapPassword.split("-");
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

		 // kludge : wasn't able to send action=runScheduledReports as part of startScheduledReportURL in the command line. so adding it here
		 startScheduledReportURL += "&action=runScheduledReports";
		
        boolean trouble = false;
		String troubleMessage = "";
		String response = "";		
		try {
			URL u = new URL(startScheduledReportURL);
			
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
			e.printStackTrace();
			return;
		}
		System.out.println("Started the Scheduled Report Run at  " + Calendar.getInstance().getTime());
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




