package batchJobs.Temp;



import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;


public class testLDAP {

	public static void main(String[] args) {
		// this program expects some input parameters. if they are empty, then it can not run
		// eg are : healthCheck URL, email user id, emailPassword, smtp server etc..
		String ldapUserId = "";
		String ldapBase = "";
		String ldapURL = "";
		String security = "";
		String password = "";
		
		 for (int i = 0; i < args.length; i++){
	         String inputParam = args[i];
	         if (inputParam.contains("ldapUserId:")){
	        	 ldapUserId = inputParam.replace("ldapUserId:", "");
	         }
	         if (inputParam.contains("ldapBase:")){
	        	 ldapBase = inputParam.replace("ldapBase:", "");
	         }
	         if (inputParam.contains("ldapURL:")){
	        	 ldapURL = inputParam.replace("ldapURL:", "");
	         }
	         if (inputParam.contains("security:")){
	        	 security = inputParam.replace("security:", "");
	         }
	         if (inputParam.contains("password:")){
	        	 password = inputParam.replace("password:", "");
	         }


		 }
	 
		 
		 String correctSyntax = "testLDAP "+
			 "ldapUserId:prahala@diebold.com "+
			 "ldapBase:uid=prahala@diebold.com,dc=diebold,dc=com,cn=ccalbd,ou=system accounts,dc=ad,dc=diebold,dc=com,Rat10nal,ou=DieboldUsers,ldap_user_attribute=sAMAccountName "+
			 "ldapURL:ldap://LDAPDNA.ad.diebold.com:389 "+
			 "security:simple "+
			 "password:yourPassword "
			 ;

		 
		 if (ldapUserId.equals("")){
			 System.out.println("Error : ldapUserId is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 if (ldapBase.equals("")){
			 System.out.println("Error : ldapBase is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 if (ldapURL.equals("")){
			 System.out.println("Error : ldapURL is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 if (security.equals("")){
			 System.out.println("Error : security is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 if (password.equals("")){
			 System.out.println("Error : password is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }

			
			
			
		////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////
			System.out.println("debug : option 1: trying the original way");

			try {

		    	
				// lets do ldap authentication.
				Hashtable authEnv = new Hashtable(11);
		    	
		 
		    	authEnv.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		   		authEnv.put(Context.PROVIDER_URL, ldapURL);
		   		authEnv.put(Context.SECURITY_AUTHENTICATION, security);
		   		authEnv.put(Context.SECURITY_PRINCIPAL, ldapBase);
		   		authEnv.put(Context.SECURITY_CREDENTIALS, password);
		 
		   		
	    		DirContext authContext = new InitialDirContext(authEnv);
	    		
	    		
				System.out
						.println("debug : option 1: trying the prod version: Success");

			} catch (Exception e) {
				System.out
						.println("debug : option 1: trying the non ssl version : Failed");
				e.printStackTrace();
			}
 		
		////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////

		
		
		
		
	}
	
}



