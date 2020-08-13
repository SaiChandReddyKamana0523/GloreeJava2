package com.gloree.actions;


import com.gloree.beans.*;
import com.gloree.utils.EmailUtil;
import com.gloree.utils.SecurityUtil;
import com.gloree.utils.UserAccountUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/////////////////////////////////////////////Purpose ///////////////////////////////////////////
//
//	This servlet is used to do either create a user account or sign in to the system.
// This servlet does not have any security restrictions. Any one can call it.
//
///////////////////////////////////////////Purpose ///////////////////////////////////////////

public class UserAccountAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public UserAccountAction() {
        super();
    }

    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost (request,response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String databaseType = this.getServletContext().getInitParameter("databaseType");
		
		///////////////////////////////SECURITY//////////////////////////////
		// Security  Note:
		// When this servlet is called, a user either does not have an account
		// in our system (and is calling to create an account) or has an account
		// but has not signed in yet (and is trying to sign now). In either case
		// they do not have a security profile.
		// 
		// If they sign in successfully, they get a securityProfile created 
		// and assigned to their session in this servlet.
		///////////////////////////////SECURITY//////////////////////////////

		
		String action = request.getParameter("action");
		
		System.out.println("Srt action is " + action);
		HttpSession session = request.getSession(true);

		
		if ( action.equals("createUserAccount")){
				
			String ldapUserId = request.getParameter("ldapUserId");
			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");
			String emailId = request.getParameter("emailId");
			String password = request.getParameter("password1");
			String petsName = request.getParameter("petsName");
			String heardAboutTraceCloud = request.getParameter("heardAboutTraceCloud");
			
			
			// lets see if firstNameB, emailIdB, passwordB were sent.
			String firstNameB = request.getParameter("firstNameB");
			String emailIdB = request.getParameter("emailIdB");
			String passwordB = request.getParameter("password1B");
			
			if ((firstNameB != null) && (firstNameB.length()!=0)){
				firstName = firstNameB;
			}
			
			if ((emailIdB != null) && (emailIdB.length()!=0)){
				emailId = emailIdB;
			}
			
			if ((passwordB != null) && (passwordB.length()!=0)){
				password = passwordB;
			}
			
			boolean exists = UserAccountUtil.userExistsInTraceCloud(emailId);
			if (exists){
				//this means that the email is already taken. i.e another user already exists with this email id.
				request.setAttribute("emailIdNotAvailable", "true");
				RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/WebSite/TCCreateAnAccountForm.jsp");
				dispatcher.forward(request, response);
				return;
			}
			else{
				
				// just to ensure that we don't clone some one's prod project as a default sample project
				// we check for 3 values. project id, prefix, adn created By.
				int sampleProjectId = Integer.parseInt(this.getServletContext().getInitParameter("clonableSampleProjectId"));
				String sampleProjectPrefix = this.getServletContext().getInitParameter("clonableSampleProjectPrefix");
				String sampleProjectCreatedBy = this.getServletContext().getInitParameter("clonableSampleProjectCreatedBy");

				String installationType = this.getServletContext().getInitParameter("installationType");
				String authenticationType = this.getServletContext().getInitParameter("authenticationType");
 
				UserAccountUtil.createUser( sampleProjectId, sampleProjectPrefix, sampleProjectCreatedBy, 
 					ldapUserId,firstName,lastName,emailId,password, petsName, heardAboutTraceCloud,
 					installationType, authenticationType, databaseType);
			
				//we will try forwarding to start page where we will ask the user to log in.
				request.setAttribute("accountCreated", "true");
				if (authenticationType.equals("database")){ 
					// now that the account is created, and the user is already ldap authenticated
					// lets just build the security profile and go to the user Projects page.
					UserAccountUtil.signInToDatabase(emailId, password, request, databaseType);
					RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/UserDashboard/userProjects.jsp");
					dispatcher.forward(request, response);
					return;
				}
				else {
					// now that the account is created, and the user is already ldap authenticated
					// lets just build the security profile and go to the user Projects page.
					UserAccountUtil.signInToLdap(emailId, request,this.getServletContext().getInitParameter("databaseType"));
					RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/UserDashboard/userProjects.jsp");
					dispatcher.forward(request, response);
					return;
				}
			}
			
		}
		if ( action.equals("updateUserProfile")){
			// SECURITY
			// To updateUserProfile, the user needs to be logged in .

			// lets make sure they are logged in. else we will send them to
			// the log in module.
			// see if the user is logged in. If he is not, the method below will
			// redirect him to the log in page.

			if (!(SecurityUtil.authenticationPassed(request, response))){
				return;
			}
			
			SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
			User user = securityProfile.getUser();
			
			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");
			String emailId = request.getParameter("emailId");
			String password = request.getParameter("password1");
			String petsName = request.getParameter("petsName");
			
			// if the user's new requested email Id is different from the one he originally created the account
			// with, we need to ensure that the new email id, has not already been taken by someone else.
			// 	NOTE : WE NEED TO DO THIS TEST, ONLY IF THE NEW REQUESTED EMAIL ID IS DIFFERENT FROM
			// THE CURRENT USER'S EMAIL ID. OTHERWISE, IF WE DO THE TEST 'IS THE EMAIL iD AVAILABLE'
			// WE WILL INVARIABLY GET FALSE, AS IT HAS BEEN ALREADY TAKEN BY 'THIS' USER. 
			// CLEAR AS MUD ???
			if (!(user.getEmailId().equals(emailId))){
				boolean exists = UserAccountUtil.userExistsInTraceCloud(emailId);
				if (exists){
					//this means that the email is already taken. i.e another user already exists with this email id.
					request.setAttribute("emailIdNotAvailable", "true");
					RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/UserDashboard/userProfile.jsp");
					dispatcher.forward(request, response);
					return;
				}
			}
			
			
			UserAccountUtil.updateUserProfile(session, user, firstName,lastName,emailId,password, petsName, this.getServletContext().getInitParameter("databaseType"));
			
			//we will forward to the userAccountProfile page, with  a note that the update is successful.
			request.setAttribute("profileUpdated", "true");
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/UserDashboard/userProfile.jsp");
			dispatcher.forward(request, response);
			return;
			
			
		}
		
		if ( action.equals("updateBillingInfo")){
			
			// SECURITY
			// To updateBillingInfo, the user needs to be logged in .

			// lets make sure they are logged in. else we will send them to
			// the log in module.
			// see if the user is logged in. If he is not, the method below will
			// redirect him to the log in page.
			if (!(SecurityUtil.authenticationPassed(request, response))){
				return;
			}
			
			SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
			User user = securityProfile.getUser();
			
			
			String ccNumber = request.getParameter("ccNumber");
			String ccType = request.getParameter("ccType");
			String ccExpireMonth = request.getParameter("ccExpireMonth");
			String ccExpireYear = request.getParameter("ccExpireYear");
			String ccFullName = request.getParameter("ccFullName");
			String ccVerificationNumber = request.getParameter("ccVerificationNumber");
			String ccBillingAddress = request.getParameter("ccBillingAddress");
			String ccBillingZipcode = request.getParameter("ccBillingZipcode");
			String ccBillingCountry = request.getParameter("ccBillingCountry");
			

			
			UserAccountUtil.updateBillingInfo(session, user, ccNumber, ccType,ccExpireMonth,
				ccExpireYear, ccFullName, ccVerificationNumber, ccBillingAddress,
				ccBillingZipcode, ccBillingCountry);
			

			
			
			// lets forward the user to the userDashboard.
			RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/UserDashboard/userProjects.jsp");
			dispatcher.forward(request, response);
			return;
		}

		
		if ( action.equals("emailPreferences")){
			
			// SECURITY
			// To updateBillingInfo, the user needs to be logged in .

			// lets make sure they are logged in. else we will send them to
			// the log in module.
			// see if the user is logged in. If he is not, the method below will
			// redirect him to the log in page.
			if (!(SecurityUtil.authenticationPassed(request, response))){
				return;
			}
			
			SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
			User user = securityProfile.getUser();
			
			String prefHealthCheckDaysHidden = request.getParameter("prefHealthCheckDaysHidden");
			user.setPrefHealthCheckDays(prefHealthCheckDaysHidden);
			
			String prefHealthCheckHideProjectsHidden = request.getParameter("prefHealthCheckHideProjectsHidden");
			user.setPrefHealthCheckHideProjects(prefHealthCheckHideProjectsHidden);
			
			
			
			
			// lets forward the user to the userDashboard.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/UserDashboard/userProfile.jsp");
			dispatcher.forward(request, response);
			return;
		}
	
		if ( action.equals("updateOrganizationInfo")){
			
			// SECURITY
			// To updateOrgInfo, the user needs to be logged in .

			// lets make sure they are logged in. else we will send them to
			// the log in module.
			// see if the user is logged in. If he is not, the method below will
			// redirect him to the log in page.
			if (!(SecurityUtil.authenticationPassed(request, response))){
				return;
			}
			
			SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
			User user = securityProfile.getUser();
			
			
			String organizationName = request.getParameter("organizationName");
			String organizationDescription = request.getParameter("organizationDescription");
			String organizationPhone = request.getParameter("organizationPhone");
			String [] projectLicense = request.getParameterValues("projectLicense");
			int readWriteLicenses = 0;
			try {
				readWriteLicenses = Integer.parseInt(request.getParameter("readWriteLicenses"));
			}
			catch (Exception e) {
				// Do Nothing
			}
			int readOnlyLicenses = 0;
			try {
				readOnlyLicenses = Integer.parseInt(request.getParameter("readOnlyLicenses"));
			}
			catch (Exception e) {
				// Do Nothing
			}
			


			UserAccountUtil.updateOrganizationInfo(session, user, securityProfile, 
				organizationName , organizationDescription,
				organizationPhone, readWriteLicenses , readOnlyLicenses, projectLicense, databaseType);

			
			// lets send an email to  the TraceCloud support team for license administration.
			try {
				String to = "support@tracecloud.com";
				String subject = "License Usage Modification for Organization : " + organizationName ;
				String message = "Dear Support, \n\nTHIS IS AN AUTOMATED SYSTEM GENERATED EMAIL.\n\n We would like to modify our license usage as follows : ";
				message += "\n\nOrganization Name : " + organizationName;
				message += "\nOrganization Description : " + organizationDescription;
				message += "\nOrganization Phone: " + organizationPhone;
				
				message += "\n\nRead Write Licenses: " + readWriteLicenses;
				message += "\nRead Only Licenses : " + readOnlyLicenses;
				
				// project licenses is an array. so we have to hoop through it.
				// lets loop through all the projects in the projectLicense array
				// and grant them project license.
				if (projectLicense != null){
					message += "\nProject Licenses: " ;
					int numOfProjects = projectLicense.length;
					for (int j=0; j < numOfProjects; j++ ){					
						int projectId = Integer.parseInt(projectLicense[j]);
						message += " " + projectId + ", ";
					}  
				}
				
				message += "\n\nPlease approve our license enhancement request.";
				message += "\n\n\nRegards";
				message += "\n" + user.getFirstName() + " " + user.getLastName() ;
				message += "\n" + user.getEmailId();
				
				
				// lets send the email out to the toEmailId;
				ArrayList toArrayList = new ArrayList();
				toArrayList.add(to);
				
				ArrayList ccArrayList = new ArrayList();
	
				MessagePacket mP = new MessagePacket(toArrayList, ccArrayList, subject, message, "");
				
				String mailHost = this.getServletContext().getInitParameter("mailHost");
				String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
				String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
				String smtpPort = this.getServletContext().getInitParameter("smtpPort");
				String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
				String emailUserId = this.getServletContext().getInitParameter("emailUserId");
				String emailPassword = this.getServletContext().getInitParameter("emailPassword");
				
				EmailUtil.email(mP, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword );
			}
			catch (Exception e){
				e.printStackTrace();
			}
			// since the org info has been updated (and some projects may have been sponsored)
			// lets update the security profiel.
			securityProfile = new SecurityProfile(user.getUserId(),this.getServletContext().getInitParameter("databaseType"));
			session.setAttribute("securityProfile", securityProfile);
			

			// lets forward the user to the userDashboard.
			RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/UserDashboard/userOrganization.jsp");
			dispatcher.forward(request, response);
			return;
		}
		if ( action.equals("grantLicenses")){
			
			// SECURITY
			// To updateOrgInfo, the user needs to be logged in .

			// lets make sure they are logged in. else we will send them to
			// the log in module.
			// see if the user is logged in. If he is not, the method below will
			// redirect him to the log in page.
			if (!(SecurityUtil.authenticationPassed(request, response))){
				return;
			}
			
			SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
			User user = securityProfile.getUser();
			
			
			String readWriteInvitees = request.getParameter("readWriteInvitees");
			String readOnlyInvitees = request.getParameter("readOnlyInvitees");
			
	
			String serverName = request.getServerName();
			String mailHost = this.getServletContext().getInitParameter("mailHost");
			String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
			String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
			String smtpPort = this.getServletContext().getInitParameter("smtpPort");
			String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
			String emailUserId = this.getServletContext().getInitParameter("emailUserId");
			String emailPassword = this.getServletContext().getInitParameter("emailPassword");

			UserAccountUtil.grantLicenses( session, user, readWriteInvitees , readOnlyInvitees,databaseType, 
					serverName, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort,emailUserId, emailPassword
					);
			

			// lets forward the user to the userDashboard.
			RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/UserDashboard/userOrganization.jsp");
			dispatcher.forward(request, response);
			return;
		}

		if ( action.equals("revokeProjectLicense")){
			
			// SECURITY
			// To updateOrgInfo, the user needs to be logged in .

			// lets make sure they are logged in. else we will send them to
			// the log in module.
			// see if the user is logged in. If he is not, the method below will
			// redirect him to the log in page.
			if (!(SecurityUtil.authenticationPassed(request, response))){
				return;
			}
			
			SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
			User user = securityProfile.getUser();
			
			int licensedProjectId = Integer.parseInt(request.getParameter("licensedProjectId"));
			
			UserAccountUtil.revokeProjectLicense(session, user, licensedProjectId, databaseType);
			
			// lets forward the user to the userDashboard.
			RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/UserDashboard/userOrganization.jsp");
			dispatcher.forward(request, response);
			return;
		}
		
		if ( action.equals("revokeLicensesOffer")){
			
			// SECURITY
			// To updateOrgInfo, the user needs to be logged in .

			// lets make sure they are logged in. else we will send them to
			// the log in module.
			// see if the user is logged in. If he is not, the method below will
			// redirect him to the log in page.
			if (!(SecurityUtil.authenticationPassed(request, response))){
				return;
			}
			
			SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
			User user = securityProfile.getUser();
			
			int licenseGrantId = Integer.parseInt(request.getParameter("licenseGrantId"));
			
			UserAccountUtil.revokeLicensesOffer(session, user, licenseGrantId);
			
			// lets forward the user to the userDashboard.
			RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/UserDashboard/userOrganization.jsp");
			dispatcher.forward(request, response);
			return;
		}

		if ( action.equals("revokeGrantedLicense")){
			
			// SECURITY
			// To updateOrgInfo, the user needs to be logged in .

			// lets make sure they are logged in. else we will send them to
			// the log in module.
			// see if the user is logged in. If he is not, the method below will
			// redirect him to the log in page.
			if (!(SecurityUtil.authenticationPassed(request, response))){
				return;
			}
			
			SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
			User user = securityProfile.getUser();
			
			String granteeEmailId = request.getParameter("granteeEmailId");
			
			UserAccountUtil.revokeGrantedLicense(session, user, granteeEmailId);
			
			// lets forward the user to the userDashboard.
			RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/UserDashboard/userOrganization.jsp");
			dispatcher.forward(request, response);
			return;
		}

		
		if ( action.equals("signOut")){
			
			// SECURITY
			// To signout, the user needs to be logged in .

			// lets make sure they are logged in. else we will send them to
			// the log in module.
			// see if the user is logged in. If he is not, the method below will
			// redirect him to the log in page.
			if (!(SecurityUtil.authenticationPassed(request, response))){
				return;
			}
			session.setAttribute("isLoggedIn", "false");
			session.invalidate();
			
			// lets forward the user to the home page.
			RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/WebSite/TCHome.jsp");
			dispatcher.forward(request, response);
			return;
		}		
		
		if ( action.equals("resetPassWord")){
				
			String emailId = request.getParameter("emailId");
			// to prevent people from maliciously resetting other user's passwords, we 
			// force them to enter firstName and last Name and reset only if those two match
			// with the email Id.
			boolean exists = UserAccountUtil.userExistsInTraceCloud(emailId);
			if (exists){
				//this means that the email exists and we can reset it.
				
				String mailHost = this.getServletContext().getInitParameter("mailHost");
				String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
				String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
				String smtpPort = this.getServletContext().getInitParameter("smtpPort");
				String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
				String emailUserId = this.getServletContext().getInitParameter("emailUserId");
				String emailPassword = this.getServletContext().getInitParameter("emailPassword");
				
				UserAccountUtil.resetPassWord(emailId, request, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
				RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/WebSite/TCResetPasswordSuccess.jsp");
				dispatcher.forward(request, response);
				return;
			}
			else{
				// this means that this email id does not exist in the system. 
				// lets ask the user to select another one.
				
				//we will try forwarding to start page where we will ask the user to log in.
				request.setAttribute("passWordReset", "false");
				RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/WebSite/TCResetPassword.jsp");
				dispatcher.forward(request, response);
				return;
			}
			
		}	
		
		if ( action.equals("marketingPageLoad")){
			
			String source = request.getParameter("source");
			
				
				String mailHost = this.getServletContext().getInitParameter("mailHost");
				String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
				String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
				String smtpPort = this.getServletContext().getInitParameter("smtpPort");
				String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
				String emailUserId = this.getServletContext().getInitParameter("emailUserId");
				String emailPassword = this.getServletContext().getInitParameter("emailPassword");
				
				// lets build the message body.
				String messageBody = "Hi, <br><br>" +
					" A new user has reached the marketing page."
					+ " <br><br> Source is <font color='red'><b> "+ source +"</b></font> <br><br> Congratulations "
					+ "<br><br> Good Luck " + 
					"Trace Cloud System. ";
				
				
				// lets send the email out to the toEmailId;
				ArrayList to = new ArrayList();
				to.add("nathan@tracecloud.com");
				ArrayList cc = new ArrayList();
				
				MessagePacket mP = new MessagePacket(to, cc, "New Marketing Hit from " + source, messageBody, "");
				EmailUtil.email(mP , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
				
				return;
			
			
		}	

		if ( action.equals("scheduleMyDemo")){
			
			String source = request.getParameter("source");
			
				
			String mailHost = this.getServletContext().getInitParameter("mailHost");
			String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
			String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
			String smtpPort = this.getServletContext().getInitParameter("smtpPort");
			String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
			String emailUserId = this.getServletContext().getInitParameter("emailUserId");
			String emailPassword = this.getServletContext().getInitParameter("emailPassword");
			
			
			

			String contactName = request.getParameter("contactName");
			String contactEmailId = request.getParameter("contactEmailId");
			String contactPhoneNumber = request.getParameter("contactPhoneNumber");
			String contactTime = request.getParameter("contactTime");
			String contactRequirements = request.getParameter("contactRequirements");

			

			if (contactName == null){contactName = "";};
			if (contactEmailId == null){contactEmailId = "";};
			if (contactPhoneNumber == null){contactPhoneNumber = "";};
			if (contactTime == null){contactTime = "";};
			if (contactRequirements == null){contactRequirements = "";};
			contactRequirements = contactRequirements.replace("\n", "<br>");
			
			// lets build the message body.
			String messageBody = "Hello , <br><br>" +
				" I came across your website on " + source + " and would like to have a live demo of TraceCloud to understand how it can help our organization."
				+ " <br><br>  Here are some details : <br><br> "+
				" <table><tr><td> Contact Email </td><td>"+ contactEmailId+"</td></tr>"+
				"<tr><td colspan='2'><br></td></tr>"+
				" <tr><td> Contact Phone </td><td>"+ contactPhoneNumber +"</td></tr>"+
				"<tr><td colspan='2'><br></td></tr>"+
				" <tr><td> Best Time to Contact </td><td>"+ contactTime +"</td></tr>"+
				"<tr><td colspan='2'><br></td></tr>"+
				" <tr><td> Requirements </td><td>"+ contactRequirements +"</td></tr>"+
				" </table>"+
				"<br><br> Best Regards <br><br> " + contactName ;
			
			
			// lets send the email out to the toEmailId;
			ArrayList to = new ArrayList();
			to.add("support@tracecloud.com");
			ArrayList cc = new ArrayList();
			cc.add("nathan@tracecloud.com");
			if (!(contactEmailId.equals(""))){
				cc.add(contactEmailId);
			}
			
			MessagePacket mP = new MessagePacket(to, cc, "Requesting a live demo of TraceCloud", messageBody, "");
			EmailUtil.email(mP , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
			
			PrintWriter out = response.getWriter();
			out.println("<div class='alert alert-success'>Thank you for your interest. <br><br>We have sent your request to the TraceCloud team. They will reach out to  you shortly<br><br></div>");
			return;
		
			
		}			
		if ( action.equals("resetAnotherUsersPassword")){
			

			
			// we can only reset another user's password, if this is an on site install and the user is a site admin
			String installationType = this.getServletContext().getInitParameter("installationType");
			SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
			User user = securityProfile.getUser();
			
			
			String siteAdministrator = this.getServletContext().getInitParameter("siteAdministrator");
			if (siteAdministrator == null) {
				siteAdministrator = "";
			}
			if ((siteAdministrator.contains(user.getEmailId())
					&&
					(installationType.toLowerCase().equals("onsite"))
			)){															// this user is a site admin and this is an onsite installation
				// at this point : installationType = onSite and user who is doing this job is a siteAdmin
				int resetUserId = Integer.parseInt(request.getParameter("resetUserId"));
				String newPassword = request.getParameter("newPassword");
				if ((newPassword == null) || (newPassword.length() == 0 ) ){
					PrintWriter out = response.getWriter();
					out.println("New Password can not be an empty string. Password has not been changed");
				}
				
				User resetUser = new User(resetUserId, databaseType);
				
				String mailHost = this.getServletContext().getInitParameter("mailHost");
				String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
				String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
				String smtpPort = this.getServletContext().getInitParameter("smtpPort");
				String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
				String emailUserId = this.getServletContext().getInitParameter("emailUserId");
				String emailPassword = this.getServletContext().getInitParameter("emailPassword");
				
				UserAccountUtil.resetAnotherUsersPassword(resetUser.getEmailId(), newPassword, request, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
				
				PrintWriter out = response.getWriter();
				out.println(resetUser.getFirstName() + "  " + resetUser.getLastName() + "'s password has been changed to     ---->     " + newPassword );

				return;
			
			}
			else {
				return;
			}
			
		}	
				
		
	
		
		
		if ( action.equals("signIn")){
			
			String password = request.getParameter("password");
			if (password == null) {password="";}
			
			// if the signIn method works, then there would be a session object called
			// security profile. else not.
			String authenticationType = this.getServletContext().getInitParameter("authenticationType");

			
			if (authenticationType.equals("database")){
				String emailId = request.getParameter("emailId");
				if (emailId == null ) {emailId = "";}
				if ((emailId.equals("")) && (password.equals(""))) {
					return;
				}
				else {
					UserAccountUtil.signInToDatabase(emailId,password, request, databaseType);
				}
			}
			if (authenticationType.equals("ldap")){
				
				String emailId  = "";
				
				String ldapAuthenticationPattern = this.getServletContext().getInitParameter("ldapAuthenticationPattern");
				if (ldapAuthenticationPattern.equals("emailId")){
					emailId = request.getRemoteUser();
				}
				if (ldapAuthenticationPattern.equals("userId")){
					// this means that the user must have entered the userId (like aditya) in the  user id section
					// there fore, we must add the emailDomain to get the full email address
					String ldapAuthenticationEmailDomain = this.getServletContext().getInitParameter("ldapAuthenticationEmailDomain");
					emailId = request.getRemoteUser() + ldapAuthenticationEmailDomain;
				}
				
				UserAccountUtil.signInToLdap(emailId, request,this.getServletContext().getInitParameter("databaseType"));
			}
			
			// at this point, we have authenticated the user and have built the security profile and stored it in the session.
			SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
			
			if (securityProfile != null) {
				
				// we set a session attribute to indicate that the user is logged in.
				// so any jsp / servlet can query this string to figure out if the user
				// is valid or not.
				// if the jsp / servlet needs authorization / privilege info it needs to 
				// get the SecurityProfile object from session memory.
				session.setAttribute("isLoggedIn", "true");
			
				// lets log the fact that this user tried to log in.
				try {
					User user = securityProfile.getUser();
					UserAccountUtil.captureLogonAttempt(user.getEmailId(), databaseType);

					UserAccountUtil.genericLog(0, 0, "", "user sign in ", user.getEmailId());
					
				}
				catch (Exception e){
					e.printStackTrace();
				}
				
				String callBackURL = request.getParameter("callBackURL");
				if (callBackURL == null ){
					callBackURL = "";
				}
		    	String CTCID = request.getParameter("CTCID");
		    	if (CTCID == null){
		    		CTCID = "";
		    	}
		    	String TESTCASEID = request.getParameter("TESTCASEID");
		    	if (TESTCASEID == null){
		    		TESTCASEID = "";
		    	}
		    	String CTCHEADLINE = request.getParameter("CTCHEADLINE");
		    	if (CTCHEADLINE == null){
		    		CTCHEADLINE = "";
		    	}
		    	String TESTCASEHEADLINE = request.getParameter("TESTCASEHEADLINE");
		    	if (TESTCASEHEADLINE == null){
		    		TESTCASEHEADLINE = "";
		    	}
		    	String CTCWEBLINK = request.getParameter("CTCWEBLINK");
		    	if (CTCWEBLINK == null){
		    		CTCWEBLINK = "";
		    	}
		    	
		    	String RELATEDSCRID = request.getParameter("RELATEDSCRID");
		    	if (RELATEDSCRID == null){
		    		RELATEDSCRID = "";
		    	}
		    	String RELATEDSCRNAME = request.getParameter("RELATEDSCRNAME");
		    	if (RELATEDSCRNAME == null){
		    		RELATEDSCRNAME = "";
		    	}
		    	
		    	String SCRID = request.getParameter("SCRID");
		    	if (SCRID == null){
		    		SCRID = "";
		    	}
		    	String SCRTITLE = request.getParameter("SCRTITLE");
		    	if (SCRTITLE == null){
		    		SCRTITLE = "";
		    	}
		    	String SCRWEBLINK = request.getParameter("SCRWEBLINK");
		    	if (SCRWEBLINK == null){
		    		SCRWEBLINK = "";
		    	}
		    	
				if (callBackURL.equals("clearQuest")){
					// There was no redirect request. So lets to go UserDashboard page.
					RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/ClearQuest/cQHome.jsp?CTCID="+CTCID + 
							"&TESTCASEID=" + 	TESTCASEID + "&TESTCASEHEADLINE=" +
							URLEncoder.encode(TESTCASEHEADLINE,"UTF-8") + "&CTCHEADLINE=" + URLEncoder.encode(CTCHEADLINE,"UTF-8") +
							"&CTCWEBLINK=" + URLEncoder.encode(CTCWEBLINK,"UTF-8")  +
							"&RELATEDSCRID=" + RELATEDSCRID + 
							"&RELATEDSCRNAME=" + URLEncoder.encode(RELATEDSCRNAME,"UTF-8") +
							"&SCRID=" + 	SCRID + "&SCRTITLE=" +
							URLEncoder.encode(SCRTITLE,"UTF-8") + 
							"&SCRWEBLINK=" + URLEncoder.encode(SCRWEBLINK,"UTF-8") 
							
							);
					dispatcher.forward(request, response);
					return;
				}
				
				if (callBackURL.equals("jira")){

			    	// The following parameters are for Jira Integration users
		    		String JID = request.getParameter("JID");
		    		String JPROJECT = request.getParameter("JPROJECT");
					String JTYPE = request.getParameter("JTYPE");
					String JPRIORITY = request.getParameter("JPRIORITY");
					String JLABELS = request.getParameter("JLABELS");
					String JSTATUS = request.getParameter("JSTATUS");
					String JRESOLUTION = request.getParameter("JRESOLUTION");
					String JAFFECTSV = request.getParameter("JAFFECTSV");
					String JFIXV = request.getParameter("JFIXV");
					String JASSIGNEE = request.getParameter("JASSIGNEE");
					String JREPORTER = request.getParameter("JREPORTER");
					String JCREATED = request.getParameter("JCREATED");
					String JUPDATED = request.getParameter("JUPDATED");
					
					String JURL = request.getParameter("JURL");
					String JTITLE = request.getParameter("JTITLE");
					String JDESCRIPTION = request.getParameter("JDESCRIPTION");
					

					if (JID == null ) {JID = "";}
					if (JPROJECT == null ) {JPROJECT = "";}
					if (JTYPE == null ) {JTYPE = "";}
					if (JPRIORITY == null ) {JPRIORITY = "";}
					if (JLABELS == null) {JLABELS = "";}
					if (JSTATUS == null ) {JSTATUS = "";}
					if (JRESOLUTION == null ) {JRESOLUTION = "";}
					if (JAFFECTSV == null ) {JAFFECTSV = "";}
					if (JFIXV == null ) {JFIXV = "";}
					if (JASSIGNEE == null ) {JASSIGNEE = "";}
					if (JREPORTER == null ) {JREPORTER = "";}
					if (JCREATED == null ) {JCREATED = "";}
					if (JUPDATED == null ) {JUPDATED = "";}
					
					if (JURL == null ) {JURL = "";}
					if (JTITLE == null ) {JTITLE = "";}
					if (JDESCRIPTION == null ) {JDESCRIPTION = "";}

			    	
					// There was no redirect request. So lets to go UserDashboard page.
					
					
					RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/Jira/jiraHome.jsp?JID="+JID + 
							"&JPROJECT =" + URLEncoder.encode(JPROJECT,"UTF-8") + 
							"&JTYPE=" + 	JTYPE + 
							"&JPRIORITY=" + URLEncoder.encode(JPRIORITY,"UTF-8") + 
							"&JLABELS=" + URLEncoder.encode(JLABELS,"UTF-8") +
							"&JSTATUS=" + URLEncoder.encode(JSTATUS,"UTF-8")  +
							"&JRESOLUTION=" + JRESOLUTION + 
							"&JAFFECTSV=" + URLEncoder.encode(JAFFECTSV,"UTF-8") +
							"&JFIXV=" + 	JFIXV + "&JASSIGNEE=" +
							URLEncoder.encode(JASSIGNEE,"UTF-8") + 
							"&JREPORTER=" + URLEncoder.encode(JREPORTER,"UTF-8") + "&JCREATED=" +
									URLEncoder.encode(JCREATED,"UTF-8") + "&JUPDATED=" +
											URLEncoder.encode(JUPDATED,"UTF-8")+"&JURL=" +
													URLEncoder.encode(JURL,"UTF-8")+ "&JTITLE=" +
															URLEncoder.encode(JTITLE,"UTF-8") + "&JDESCRIPTION=" +
																	URLEncoder.encode(JDESCRIPTION,"UTF-8")
							
							);
					dispatcher.forward(request, response);
					return;
				}
				
				if (callBackURL.equals("dfssHomePage")){
					// There was no redirect request. So lets to go UserDashboard page.
					RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/Customizations/Diebold/Hardware/dFSSHomePage.jsp");
					dispatcher.forward(request, response);
					return;
				}
				
				// lets handle the scenario where the user has to go to the approval action page.
				String redirectAction = (String) session.getAttribute("redirectAction");
				if ((redirectAction != null ) && (redirectAction.equals("approvalAction"))){
					// the user came here becauase of approval action, and was forced to authenticate
					// now that the authentication is done, we need to send him / her to the correct location.
					String approvalAction = request.getParameter("approvalAction");
					int requirementId = Integer.parseInt(request.getParameter("requirementId"));
					RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp//Requirement/requirementApprovalAction.jsp?requirementId="  + requirementId + "&approvalAction=" + approvalAction);
					dispatcher.forward(request, response);
					return;
				}
				
				
				String redirectToDisplay = (String) session.getAttribute("redirectToDisplay");
				if ((redirectToDisplay == null ) || (redirectToDisplay.equals(""))){
					// There was no redirect request. So lets to go UserDashboard page.
					RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/UserDashboard/userProjects.jsp");
					dispatcher.forward(request, response);
					return;					
				}
				else {
					// we got here because the user wanted to display a Requirement 
					// then lets send him to the displayAction page.
					
					// since we don't need this session attrib any more, lets drop it.
					session.removeAttribute("redirectToDisplay");
					
					if (session.getAttribute("dO").equals("req")){
						RequestDispatcher dispatcher =	
							request.getRequestDispatcher("/servlet/DisplayAction?dReqId=" 
									+ session.getAttribute("displayRequirementId") 
									+ "&dO=" +  session.getAttribute("dO"));
						dispatcher.forward(request, response);
						
						return;
					}
					if (session.getAttribute("dO").equals("rep")){
						RequestDispatcher dispatcher =	
							request.getRequestDispatcher("/servlet/DisplayAction?dRepId=" 
								+ session.getAttribute("displayReportId") 
								+ "&dO=" +  session.getAttribute("dO"));
						dispatcher.forward(request, response);
						return;
					}
					if (session.getAttribute("dO").equals("attachment")){
						RequestDispatcher dispatcher =	
							request.getRequestDispatcher("/servlet/DisplayAction?dAttachmentId=" 
								+ session.getAttribute("displayAttachmentId") 
								+ "&dO=" +  session.getAttribute("dO"));
						dispatcher.forward(request, response);
						return;
					}
					if (session.getAttribute("dO").equals("healthCheck")){
						RequestDispatcher dispatcher =	
							request.getRequestDispatcher("/servlet/DisplayAction?displayProjectId=" 
								+ session.getAttribute("displayProjectId") 
								+ "&displayFunction=" + session.getAttribute("displayFunction") +  "&dO=" +  session.getAttribute("dO"));
						dispatcher.forward(request, response);
						return;
					}
				}
			}
			else {
				
				
				if (authenticationType.equals("database")){
					// redirect the user to the sign in page with an error message.
					request.setAttribute("signInFailed", "true");
					RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/WebSite/startPage.jsp");
					dispatcher.forward(request, response);
					return;
				}
				if (authenticationType.equals("ldap")){
					// if this an ldap user, he must have already authenticated, 
					// he just doesn't have a TraceCloud account yet. So, lets ask him to create one.
					request.setAttribute("signInFailed", "true");
					RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/WebSite/TCCreateAnAccountForm.jsp");
					dispatcher.forward(request, response);
					return;
					
				}
				
			}
		}
	}

}
