package com.gloree.actions;

/////////////////////////////////////////////Purpose ///////////////////////////////////////////
//
//	This servlet is used to create and open projects. it's also used for some admin actions
// like create Requirement Types.
//
///////////////////////////////////////////Purpose ///////////////////////////////////////////

import com.gloree.beans.*;
import com.gloree.utils.CloneUtil;
import com.gloree.utils.EmailUtil;
import com.gloree.utils.ProjectCreationUtil;
import com.gloree.utils.ProjectUtil;
import com.gloree.utils.RoleUtil;
import com.gloree.utils.SecurityUtil;
import com.gloree.utils.UserAccountUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ProjectAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ProjectAction() {
        super();
    }

    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost (request,response);
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String databaseType = this.getServletContext().getInitParameter("databaseType");

		
		///////////////////////////////SECURITY//////////////////////////////
		// Security  Note:
		// user has to be logged in by the time he is here. And he needs to be a member
		// of this project. unless he is here to create the project for the first 
		// time.
		///////////////////////////////SECURITY//////////////////////////////

		// see if the user is logged in. If he is not, the method below will
		// redirect him to the log in page.
		if (!(SecurityUtil.authenticationPassed(request, response))){
			return;
		}
		HttpSession session = request.getSession();
		SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
		
		User user = securityProfile.getUser();
		// NOTE : normally, the project id is available as an attribute in session.
		// the only exception to this rule is when the user is trying to log into this
		// project for the first time or when he is creating the project .
		// in that situation, we need to create a new 
		// project object and put it in session before we run the authorization check
		// MemberInProjectProjectid.
		Project project = (Project) session.getAttribute("project");
		
		
		
		///////////////////////////////SECURITY//////////////////////////////

		
		String action = request.getParameter("action");
		
		// if the user comes here without an action, do nothing.
		if ((action == null) || (action.equals(""))){
			return;
		}
				
		

		if ( action.equals("genericLog")){
				
			int objectId = 0 ;
			String objectType = "" ;
			String description = "" ;
			
			try {
				objectId = Integer.parseInt(request.getParameter("objectId"));
			}
			catch (Exception e){};
			
			try {
				objectType = request.getParameter("objectType");
			}
			catch (Exception e){};
			
			try {
				description = request.getParameter("description");
			}
			catch (Exception e){};
			
			int projectId=0;
			try {
				projectId = project.getProjectId();
			}
			catch (Exception e ){}
			UserAccountUtil.genericLog( projectId, objectId, objectType , description, user.getEmailId());
				
		}	
		
				
		

		if (action.equals("withdrawInvitation")){
			
			int inviteId = Integer.parseInt(request.getParameter("inviteId"));
			String message = ProjectUtil.withdrawInvitation(user.getEmailId(), inviteId);

			if (message.equals("success")){
				// redirect to invitation page
				response.sendRedirect("/GloreeJava2/jsp/UserDashboard/userInvitations.jsp");
				return;
			}
			else {
				PrintWriter out2 = response.getWriter();
				out2.print(message);
			}
			
			
	} 
	
		if (action.equals("openProject")){
			// since we have been requested to open a new project
			// we need to create the project object and put in the session.			
			int projectId = Integer.parseInt(request.getParameter("projectId"));
			project = new Project(projectId, databaseType );
			session.setAttribute("project", project);		

			// Lets see if this user is a member of this project 
			// and can proceed
			if (!(securityProfile.getRoles().contains("MemberInProject" + project.getProjectId()))){
				//User is NOT a member of this project. so do nothing and return.
				return;
			}
			else{
				RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/OpenProject/yP.jsp");
				dispatcher.forward(request, response);
				return;
			}
		}
		else if (action.equals("hideProject")){
			
			int projectId = Integer.parseInt(request.getParameter("projectId"));
			// Lets see if this user is a member of this project 
			// and can proceed
			if (!(securityProfile.getRoles().contains("MemberInProject" + projectId))){
				//User is NOT a member of this project. so do nothing and return.
				return;
			}
			String projectPrefix = request.getParameter("projectPrefix");
			String prefHideProjects = user.getPrefHideProjects() + "," + projectId + ":#:" + projectPrefix ;
			user.setPrefHideProjects(prefHideProjects);
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/UserDashboard/userProjects.jsp");
			dispatcher.forward(request, response);			
		}
		else if (action.equals("unHideProject")){
			int projectId = Integer.parseInt(request.getParameter("projectId"));
			// Lets see if this user is a member of this project 
			// and can proceed
			if (!(securityProfile.getRoles().contains("MemberInProject" + projectId))){
				//User is NOT a member of this project. so do nothing and return.
				return;
			}
			String projectPrefix = request.getParameter("projectPrefix");
			String prefHideProjects = user.getPrefHideProjects().replace( projectId + ":#:" + projectPrefix , "");
			user.setPrefHideProjects(prefHideProjects);
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/UserDashboard/userProjects.jsp");
			dispatcher.forward(request, response);			
		}
		else if ( action.equals("requestAccessToProject")) {
			int targetProjectId = Integer.parseInt(request.getParameter("projectId"));
			Project targetProject = new Project(targetProjectId, databaseType);
			ArrayList administrators = ProjectUtil.getProjectAdministrators(targetProjectId);
			String to = "";
			Iterator a = administrators.iterator();
			while (a.hasNext()){
				String adminEmailId = (String) a.next();
				to += adminEmailId + ",";
			}
			String cc = user.getEmailId();
			String subject = "Request for project access";
			String message = "" +
					"\n\nTHIS IS A SYSTEM GENERATED EMAIL" +
					"\n\n\nHi, " +
					"\n\nI would like have access to a project. Here are the details." +
					"\n\nMy Name : " + user.getFirstName() + " " + user.getLastName() + 
					"\nMy Email Address :" + user.getEmailId() + 
					"\n\nHere are the project details  " +
					"\n\nProject Short Name  : " + targetProject.getShortName() +
					"\nProject Name        : " + targetProject.getProjectName() + 
					"\nProject Description : " + targetProject.getProjectDescription() + 
					"\n\nYou are recieving this email becuase you are an Administrator for this project. Please log into the system, " +
					"open the 'Administration Tool' for this project, and grant me access to this project." +
					"\n\nRegards" +
					"\n\n" + user.getFirstName() + " " + user.getLastName();
			
			
			// lets send the email out to the toEmailId;
			ArrayList toArrayList = new ArrayList();
			if (to != null){
				to = to.trim();
				if (!to.equals("")){
					if (to.contains(",")){
						String [] toEmails = to.split(",");
						for (int i=0; i < toEmails.length; i++ ){
							toArrayList.add(toEmails[i]);
						}
					}
					else {
						toArrayList.add(to);
					}
				}
			}
			
			ArrayList ccArrayList = new ArrayList();
			if (cc != null){
				cc = cc.trim();
				if (!cc.equals("")){
					if (cc.contains(",")){
						String [] ccEmails = cc.split(",");
						for (int i=0; i < ccEmails.length; i++ ){
							ccArrayList.add(ccEmails[i]);
						}
					}
					else {
						ccArrayList.add(cc);
					}
				}
			}
			MessagePacket mP = new MessagePacket(toArrayList, ccArrayList, subject, message, "");
			
			String mailHost = this.getServletContext().getInitParameter("mailHost");
			String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
			String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
			String smtpPort = this.getServletContext().getInitParameter("smtpPort");
			String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
			String emailUserId = this.getServletContext().getInitParameter("emailUserId");
			String emailPassword = this.getServletContext().getInitParameter("emailPassword");
			
			EmailUtil.email(mP, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword  );
			
			
		    PrintWriter out = response.getWriter();
		    out.println("<div><span class='normalText'> An email has been sent to the Project Administrators." +
		    		"You have been CC'd on this email. Please check your mail box." +
		    		" &nbsp;&nbsp;<a href='#' onClick='document.getElementById(\"requestAccessDiv" + targetProjectId + "\" ).style.display=\"none\"'>Close</a> </span></div>");
		    out.close();
			
		    return;

			
		}
		else if ( action.equals("administerProject")) {
			// Note : even Members can get to the administer project. 
			// however, they are limited to the actions they can take
			// once they get there.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/aPExplorer.jsp");
			dispatcher.forward(request, response);
		}
		else if ( action.equals("displayExplorer")) {
			// Note : even Members can get to the administer project. 
			// however, they are limited to the actions they can take
			// once they get there.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/OpenProject/oPExplorer.jsp");
			dispatcher.forward(request, response);
		}		
		else if (action.equals ("createProject")){
			// any logged in user can create a project.
			String createdByEmailId = user.getEmailId();	
			// get the parameters.
			String projectName = request.getParameter("projectName");
			String shortName = request.getParameter("shortName");
			String projectDescription = request.getParameter("projectDescription");
			
			String projectOwner = request.getParameter("projectOwner");
			String projectWebsite = request.getParameter("projectWebsite");
			String projectOrganization = request.getParameter("projectOrganization");
			String projectTags = request.getParameter("projectTags");
			
			// all the above variables (name, description, prefix, owner, website etc.. are free floating texts
			// and some users can insert junk in there, that can make sensitive info like session id pop up on the screen.
			// while this is not critical, some idiot security guy at Diebold made a big stink about this. So, sanitizing the inputs.
			projectName = ProjectUtil.removeHTML(projectName);
			shortName = ProjectUtil.removeHTML(shortName);
			projectDescription = ProjectUtil.removeHTML(projectDescription);
			projectOwner = ProjectUtil.removeHTML(projectOwner);
			projectWebsite = ProjectUtil.removeHTML(projectWebsite);
			projectOrganization = ProjectUtil.removeHTML(projectOrganization);
			projectTags = ProjectUtil.removeHTML(projectTags);
			
			
			String restrictedDomains = request.getParameter("restrictedDomains");
			
			String administrators = request.getParameter("administrators");
			String users = request.getParameter("users");
			String type = request.getParameter("type");
			
			int projectId = 0;
			
			if (type.equals("resourceManagement")){
				projectId = ProjectCreationUtil.crateNewProjectResourceManagement(createdByEmailId, projectName, 
						shortName,projectDescription,
						projectOwner, projectWebsite, projectOrganization, projectTags,
						restrictedDomains,databaseType);
			}
			else {
				projectId = ProjectCreationUtil.crateNewProject(createdByEmailId, projectName, 
					shortName,projectDescription,
					projectOwner, projectWebsite, projectOrganization, projectTags,
					restrictedDomains,databaseType);
			}
			
			
			// at this point the project is created. 
			project = new Project(projectId, databaseType);
			
			
			// Now we will add the admins to this project role.
			Role adminRole = new Role (projectId,"Administrator");

			// call addUsersToRole, which returns 3 # delimited strings incorrectDomain, 
			// addedUsers, invitedUsers . Each one of these strings is a , delimited
			// list of email Ids.
			// 
		
			String inCorrectDomainEmailIds = "";
			String successfullyAddedEmailIds = "";
			String invitedEmailIds = "";
			
			// lets make sure that the creator of the project is an admin.
			if ((administrators == null ) || (administrators.equals(""))){
				administrators = user.getEmailId();
			}
			
			if (((administrators != null) && !(administrators.equals("")))){
				String serverName = request.getServerName();

				String result = RoleUtil.addUsersToRole(project, adminRole.getRoleId(), administrators,
					user.getEmailId(), databaseType, serverName);
				
				
				if (result.contains("#")){
					String [] resultArray = result.split("#");
					
					
					// NOTE : for some reason, a split of #mkt@gmail.com# gives only 2 rows in teh array.
					// hence forcing a check.
					if (resultArray.length > 0) {
						// i.e we are sure 1st element is there . i.e item[0]
						inCorrectDomainEmailIds = resultArray[0];
					}
					if (resultArray.length > 1) {
						// i.e we are sure 2nd element is there . i.e item[1]
						successfullyAddedEmailIds = resultArray[1];
					}
					if (resultArray.length > 2) {
						// i.e we are sure 3rd element is there . i.e item[2]
						invitedEmailIds = resultArray[2];	
					}
				}
			}

			
				
			// Now we will add the users to this project role.
			Role userRole = new Role (projectId,"user");


			// call addUsersToRole, which returns 3 # delimited strings incorrectDomain, 
			// addedUsers, invitedUsers . Each one of these strings is a , delimited
			// list of email Ids.
			// 
			if (((users != null) && !(users.equals("")))){
				String serverName = request.getServerName();

				String result = RoleUtil.addUsersToRole(project, userRole.getRoleId(), users,
					user.getEmailId(), databaseType, serverName);
				
				
				if (result.contains("#")){
					String [] resultArray = result.split("#");
					
					
					// NOTE : for some reason, a split of #mkt@gmail.com# gives only 2 rows in teh array.
					// hence forcing a check.
					if (resultArray.length > 0) {
						// i.e we are sure 1st element is there . i.e item[0]
						inCorrectDomainEmailIds += "," + resultArray[0];
					}
					if (resultArray.length > 1) {
						// i.e we are sure 2nd element is there . i.e item[1]
						successfullyAddedEmailIds += "," + resultArray[1];
					}
					if (resultArray.length > 2) {
						// i.e we are sure 3rd element is there . i.e item[2]
						invitedEmailIds += "," + resultArray[2];	
					}
				}
			}
			request.setAttribute("inCorrectDomainEmailIds", inCorrectDomainEmailIds);
			request.setAttribute("successfullyAddedEmailIds", successfullyAddedEmailIds);
			request.setAttribute("invitedEmailIds", invitedEmailIds);
			
			request.setAttribute("projectCreated", "true");
		
			// now that the project has been created, lets refresh the users security profile
			// and set it in the session object.
			securityProfile = new SecurityProfile(user.getUserId(),this.getServletContext().getInitParameter("databaseType"));
			session.setAttribute("securityProfile", securityProfile);
			
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/UserDashboard/userProjects.jsp");
			dispatcher.forward(request, response);
			return;
		
		}
		
		else if (action.equals ("cloneProject")){
			java.sql.Connection con = null;
			try {
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();

				int sourceProjectId = Integer.parseInt(request.getParameter("sourceProjectId"));
				
				String cloneUsersString = request.getParameter("cloneUsers");
				String cloneRequirementsString = request.getParameter("cloneRequirements");
				String cloneTraceabilityString = request.getParameter("cloneTraceability");
				
				boolean cloneUsers = false ;
				boolean cloneRequirements = false;
				boolean cloneTraceability = false;
				if ((cloneUsersString != null) && (cloneUsersString.equals("cloneUsers"))) {
					cloneUsers = true;
				}
				if ((cloneRequirementsString != null) && (cloneRequirementsString.equals("cloneRequirements"))) {
					cloneRequirements = true;
				}
				if ((cloneTraceabilityString != null) && (cloneTraceabilityString.equals("cloneTraceability"))) {
					cloneTraceability = true;
				}
			

				boolean cloneMetrics = false;
				// note we never clone Metrics for regular projects. 
				// we use this option when we are cloing sample projects because, we want tusers to see
				// the metrics in a graph.
				System.out.println("srt source projectId is " + sourceProjectId);
				CloneUtil.cloneProject(con, sourceProjectId, cloneUsers, cloneRequirements, cloneTraceability,
						cloneMetrics, user.getEmailId(),  databaseType,  user);
				// at this point the project is created.
				
				

				// lets close the database connection
				con.close();
			} catch (Exception e) {
				
				e.printStackTrace();
			} finally {
				if (con != null) {
					try { con.close(); } catch (Exception e) { ; }
					con = null;
				}
			}
		
			request.setAttribute("projectCreated", "true");
		
			// now that the project has been created, lets refresh the users security profile
			// and set it in the session object.
			securityProfile = new SecurityProfile(user.getUserId(),this.getServletContext().getInitParameter("databaseType"));
			session.setAttribute("securityProfile", securityProfile);
			
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/UserDashboard/userProjects.jsp");
			dispatcher.forward(request, response);
			return;
		
		}		
		else if (action.equals ("updateCoreInfo")){
			// only administrators can update the core info.
			if (
					!(
					(securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))
					||
					(securityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
					)
				){
				//User is NOT a administrator or poweruse of this project. so do nothing and return.
				return;
			}
			
			String projctTimeZone = request.getParameter("projcetTimeZone");
			if (projctTimeZone == null || projctTimeZone.equals("")){
				projctTimeZone = "PST";
			}
			project.setProjectTimeZone(projctTimeZone);
			
			try{
				Double gmtDelta = Double.parseDouble(request.getParameter("gmtDelta"));
				project.setGMTDelta(gmtDelta);
			}	
			catch (Exception gmtFailure){
				
			}
			
			
			String updatedByEmailId = user.getEmailId();
			// get the parameters.
			String projectName = request.getParameter("projectName");
			String shortName = request.getParameter("shortName");
			String projectDescription = request.getParameter("projectDescription");

			String projectOwner = request.getParameter("projectOwner");
			String projectWebsite = request.getParameter("projectWebsite");
			String projectOrganization = request.getParameter("projectOrganization");
			String projectTags = request.getParameter("projectTags");
			String percentageCompleteDriver = request.getParameter("percentageCompleteDriver");
			String powerUserSettings = request.getParameter("powerUserSettings");
			String healthBarSettings = request.getParameter("healthBarSettings");
			
			int hidePriority = Integer.parseInt(request.getParameter("hidePriority"));
			
			
			
			
			int percentageCompleteDriverReqTypeId = 0;
			try {
				percentageCompleteDriverReqTypeId = Integer.parseInt(percentageCompleteDriver);
			}
			catch (Exception e){
				e.printStackTrace();
			}
					
			// lets take care of enableVotes
			
			String enableVotesRequirementTypesIds = request.getParameter("enableVotesRequirementTypesIds");
			
			 // lets drop the last ,
		    if (enableVotesRequirementTypesIds.contains(",")){
				
		    	enableVotesRequirementTypesIds = (String) enableVotesRequirementTypesIds.subSequence(0,enableVotesRequirementTypesIds.lastIndexOf(","));
				
		    }
		    if ((enableVotesRequirementTypesIds != null) && (!(enableVotesRequirementTypesIds.equals("")))){
		    	String[] reqTypeIds = enableVotesRequirementTypesIds.split(",");
				for (int i = 0; i < reqTypeIds.length; i++) {
					int requirementTypeId = Integer.parseInt(reqTypeIds[i]);
					RequirementType requirementType  = new RequirementType(requirementTypeId);
					requirementType.setRequirementTypeEnableVotes(1);
					ProjectUtil.setUpEnableVotesAttributesInReqType(project.getProjectId(), requirementTypeId, updatedByEmailId,databaseType);
				}
				// Note : this was a bug
				// for all the highlighted req types, we need to enable for agile scrum. However for all the unhighlighted ones, 
				// we need to disable for Agile Scrum.
				ArrayList requirementTypes = project.getMyRequirementTypes();
				Iterator rT = requirementTypes.iterator() ;
				
				while (rT.hasNext()){
					boolean reqTypeEnabledByUserInThisCall = false;
					RequirementType requirementType = (RequirementType) rT.next();
					// for all the req types in this project, lets iterate through he list sent by the user to figure out
					// if the user wanted this enabled or not.
					for (int i = 0; i < reqTypeIds.length; i++) {
						int requirementTypeId = Integer.parseInt(reqTypeIds[i]);
						if (requirementType.getRequirementTypeId() == requirementTypeId){
							reqTypeEnabledByUserInThisCall = true;
						}
					}
					// if reqTypeEnabledByUserInThisCall is still false , then this req type needs to be disabled.
					if (!(reqTypeEnabledByUserInThisCall)){
						requirementType.setRequirementTypeEnableVotes(0);
					}
				}
				
		    }
		    
		    
		    
		    
			
			// lets take care of enableAgileScrum
			int enableAgileScrum = Integer.parseInt(request.getParameter("enableAgileScrum"));
			String enableAgileScrumRequirementTypeIds = request.getParameter("enableAgileScrumRequirementTypeIds");
			 // lets drop the last ,
		    if (enableAgileScrumRequirementTypeIds.contains(",")){
				
		    	enableAgileScrumRequirementTypeIds = (String) enableAgileScrumRequirementTypeIds.subSequence(0,enableAgileScrumRequirementTypeIds.lastIndexOf(","));
				
		    }
			
			
		    if ((enableAgileScrumRequirementTypeIds != null) && (!(enableAgileScrumRequirementTypeIds.equals("")))){
		    	String[] reqTypeIds = enableAgileScrumRequirementTypeIds.split(",");
				for (int i = 0; i < reqTypeIds.length; i++) {
					int requirementTypeId = Integer.parseInt(reqTypeIds[i]);
					RequirementType requirementType  = new RequirementType(requirementTypeId);
					requirementType.setRequirementTypeEnableAgileScrum();
					ProjectUtil.setUpAgileScrumAttributesInReqType(project.getProjectId(), requirementTypeId, updatedByEmailId,databaseType);
				}
				// Note : this was a bug
				// for all the highlighted req types, we need to enable for agile scrum. However for all the unhighlighted ones, 
				// we need to disable for Agile Scrum.
				ArrayList requirementTypes = project.getMyRequirementTypes();
				Iterator rT = requirementTypes.iterator() ;
				
				while (rT.hasNext()){
					boolean reqTypeEnabledByUserInThisCall = false;
					RequirementType requirementType = (RequirementType) rT.next();
					// for all the req types in this project, lets iterate throught he list sent by the user to figure out
					// if the user wanted this enabled or not.
					for (int i = 0; i < reqTypeIds.length; i++) {
						int requirementTypeId = Integer.parseInt(reqTypeIds[i]);
						if (requirementType.getRequirementTypeId() == requirementTypeId){
							reqTypeEnabledByUserInThisCall = true;
						}
					}
					// if reqTypeEnabledByUserInThisCall is still false , then this req type needs to be disabled.
					if (!(reqTypeEnabledByUserInThisCall)){
						requirementType.setRequirementTypeEnableAgileScrumToDisabled();
					}
				}
				
		    }
		    else {
		    	// this means that enableAgileScurmReqType is empty, and there should be NO req types enabled for agile scrum.
		    	ArrayList requirementTypes = project.getMyRequirementTypes();
				Iterator rT = requirementTypes.iterator() ;
				
				while (rT.hasNext()){
					RequirementType requirementType = (RequirementType) rT.next();
					// if reqTypeEnabledByUserInThisCall is still false , then this req type needs to be disabled.
					if (requirementType.getRequirementTypeEnableAgileScrum() == 1){
						requirementType.setRequirementTypeEnableAgileScrumToDisabled();
					}
				}
		    }
			String restrictedDomains = request.getParameter("restrictedDomains");
			// if someone entered a space after comma, lets drop it.
			if ((restrictedDomains != null) && (restrictedDomains.contains(" "))){
				restrictedDomains = restrictedDomains.replace(" ", "");
			}
			
			// lets see if the new restrictedDomains is safe....
			// NOTE : we do the restricted domains test only if there is a value in this field.
			if ((restrictedDomains != null) && (!(restrictedDomains.equals("")))){
				ArrayList atRiskUsers = ProjectUtil.getAtRiskUsers(project.getProjectId(), restrictedDomains, databaseType); 
				
				if (atRiskUsers.size() > 0) {
					// this means there are users at risk if we restrict the domain.
					request.setAttribute("atRiskUsers", atRiskUsers);
					request.setAttribute("status", "atRiskUsers");
					RequestDispatcher dispatcher =	request.getRequestDispatcher
						("/jsp/AdministerProject/CoreInfo/coreInfo.jsp");
					dispatcher.forward(request, response);
					return;
				}
			}
			// this means that there is no risk of users being restricted.
			
			// if the user is trying to update the prefix, see if its unique or not.
			if (!(shortName.equals(project.getShortName()))) {
				boolean isPrefixAvailable = ProjectUtil.isPrefixAvailable(project, shortName);
				if (! (isPrefixAvailable)){
					// the prefix the user wants is already taken.
					request.setAttribute("status", "shortNameExists");
					RequestDispatcher dispatcher =	request.getRequestDispatcher
						("/jsp/AdministerProject/CoreInfo/coreInfo.jsp");
					dispatcher.forward(request, response);
					return;
				}
			}
			
			// if we got till here that means that short name (prefix) is still unique
			// and the list of users is within the restricted domains.
			
			ProjectUtil.updateProjectCoreInfo(project, updatedByEmailId, projectName, 
				shortName,projectDescription,
				projectOwner, projectWebsite, projectOrganization, projectTags, enableAgileScrum,
				powerUserSettings, percentageCompleteDriverReqTypeId, restrictedDomains, databaseType);
			
			ProjectUtil.updateHidePriority(project, hidePriority,updatedByEmailId, databaseType);
				

			ProjectUtil.updateHealthBarSettings(project, healthBarSettings,updatedByEmailId); 
				
			// now that the project has been updated, lets refresh the users security profile
			// and set it in the session object.
			securityProfile = new SecurityProfile(user.getUserId(),this.getServletContext().getInitParameter("databaseType"));
			session.setAttribute("securityProfile", securityProfile);
			
			// since the project core info has changed, we need to update the project object in session.
			int projectId = project.getProjectId();
			project = new Project(projectId, databaseType);
			session.setAttribute("project", project);

			request.setAttribute("updatedCoreInfo", "true");
			RequestDispatcher dispatcher =	request.getRequestDispatcher
				("/jsp/AdministerProject/CoreInfo/coreInfo.jsp");
			dispatcher.forward(request, response);
			return;
		}
		else if (action.equals ("deleteProject")){
			// only administrators can delete the project.
			if (!(securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))){
				//User is NOT a administrator of this project. so do nothing and return.
				return;
			}
			if ((securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))){
				ProjectUtil.deleteProject(project.getProjectId(), user, databaseType);
			}
			securityProfile = new SecurityProfile(user.getUserId(),this.getServletContext().getInitParameter("databaseType"));
			session.setAttribute("securityProfile", securityProfile);
			
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/UserDashboard/userProjects.jsp");
			dispatcher.forward(request, response);
			return;
		}
		else if (action.equals ("archiveProject")){
			// only administrators can delete the project.
			if (!(securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))){
				//User is NOT a administrator of this project. so do nothing and return.
				return;
			}
			if ((securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))){
				ProjectUtil.archiveProject(project, user, databaseType);
			}
			securityProfile = new SecurityProfile(user.getUserId(),this.getServletContext().getInitParameter("databaseType"));
			session.setAttribute("securityProfile", securityProfile);
			
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/UserDashboard/userProjects.jsp");
			dispatcher.forward(request, response);
			return;
		}

		else if (action.equals ("reActivateProject")){
			
			// since we have been requested to open a new project
			// we need to create the project object and put in the session.			
			int projectId = Integer.parseInt(request.getParameter("projectId"));
			project = new Project(projectId, databaseType );
			session.setAttribute("project", project);		
			
			// only administrators can delete the project.
			if (!(securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))){
				//User is NOT a administrator of this project. so do nothing and return.
				return;
			}
			if ((securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))){
				ProjectUtil.reActivateProject(project, user, databaseType);
			}
			securityProfile = new SecurityProfile(user.getUserId(),this.getServletContext().getInitParameter("databaseType"));
			session.setAttribute("securityProfile", securityProfile);
			
			
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/UserDashboard/userProjects.jsp");
			dispatcher.forward(request, response);
			return;
		}
		
		else if (action.equals ("connectToProject")){
			// only administrators can connect other projects  to this project.
			if (!(securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))){
				//User is NOT a administrator of this project. so do nothing and return.
				return;
			}
			String connectionDescription = request.getParameter("connectionDescription");
			int connectToProjectId = Integer.parseInt(request.getParameter("connectToProjectId"));
			// also the user needs to be an admin in the connecting project.
			if (!(securityProfile.getRoles().contains("AdministratorInProject" + connectToProjectId))){
				//User is NOT a administrator of this project. so do nothing and return.
				return;
			}
			
			ProjectUtil.relateProjects(project ,connectToProjectId,connectionDescription,user.getEmailId(), databaseType);
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/ConnectProjects/connectProjects.jsp");
			dispatcher.forward(request, response);
			return;
		}		
		else if (action.equals ("disconnectFromProject")){
			// only administrators can connect other projects  to this project.
			if (!(securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))){
				//User is NOT a administrator of this project. so do nothing and return.
				return;
			}
			int disconnectProjectId = Integer.parseInt(request.getParameter("disconnectProjectId"));
			// also the user needs to be an admin in the connecting project.
			if (!(securityProfile.getRoles().contains("AdministratorInProject" + disconnectProjectId))){
				//User is NOT a administrator of this project. so do nothing and return.
				return;
			}
			
			ProjectUtil.unrelateProjects(project ,disconnectProjectId,user.getEmailId(),  databaseType);
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/ConnectProjects/connectProjects.jsp");
			dispatcher.forward(request, response);
			return;
		}
		else if (action.equals ("rebuildSearchIndex")){
			// only administrators can connect other projects  to this project.
			if (!(securityProfile.getRoles().contains("MemberInProject" + project.getProjectId()))){
				//User is NOT a member of this project. so do nothing and return.
				return;
			}
			
			ProjectUtil.rebuildSearchIndex(project ,user.getEmailId());

		    PrintWriter out = response.getWriter();
		    out.println("<div class='alert alert-success'><span class='normalText'> The Project Search Index has been rebuilt  </span></div>");
		    out.close();

			return;
		}

		else if (action.equals ("updateIntegrationMenu")){
			// only administrators can update integration menu.
			if (!(securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))){
				//User is NOT a administrator of this project. so do nothing and return.
				return;
			}
			String menuType = request.getParameter("menuType");
			// cludge , but works. delete all integration menus of this type and recreate them as entered.
			ProjectUtil.deleteAllIntegrationMenus(project.getProjectId(), menuType);
			int counter = Integer.parseInt(request.getParameter("counter"));
			for (int i=1; i<= counter ;i++){
				String menuLabel = request.getParameter("menuLabel" + i);
				String menuValue = request.getParameter("menuValue" + i);
				// this created the object in the database.
				if ((menuLabel != null) && (!menuLabel.equals(""))
						&&
						(menuValue != null) && (!menuValue.equals(""))){
					// we create the object only if the values and labels aren't empty.
					IntegrationMenu integrationMenu = new IntegrationMenu(project.getProjectId(), menuType, menuLabel, menuValue );
				}
						
				
			}

			// since the project object has changed, lets refresh it and load it into session.
			project = new Project(project.getProjectId(), databaseType);
			session.setAttribute("project", project);
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/IntegrationMenu/integrationMenu.jsp?update=success");
			dispatcher.forward(request, response);
			return;

		}						
	} 
		

}
