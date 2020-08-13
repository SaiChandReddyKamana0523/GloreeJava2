package com.gloree.actions;

import com.gloree.beans.RTAttribute;
import com.gloree.beans.Requirement;
import com.gloree.utils.RequirementUtil;
import com.gloree.utils.SprintUtil;
import com.gloree.beans.*;
import com.gloree.utils.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/////////////////////////////////////////////Purpose ///////////////////////////////////////////
//
//	This servlet is used to create , delete and update both Requirement Type and its
// attributes. Access restricted to Admins only.
//
///////////////////////////////////////////Purpose ///////////////////////////////////////////


public class AgileScrumAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public AgileScrumAction() {
        super();
    }

    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost (request,response);
    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String databaseType = this.getServletContext().getInitParameter("databaseType");

		///////////////////////////////SECURITY//////////////////////////////
		// Security  Note:
		// user has to be logged in by the time he is here. 
		// And he needs to be an Administrator
		// of this project.
		///////////////////////////////SECURITY//////////////////////////////
		
		// see if the user is logged in. If he is not, the method below will
		// redirect him to the log in page.
		if (!(SecurityUtil.authenticationPassed(request, response))){
			return;
		}
		
		// now check if this users should be in this project.
		HttpSession session = request.getSession(true);
		SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
		Project project = (Project) session.getAttribute("project");
		if (!(securityProfile.getRoles().contains("MemberInProject" + project.getProjectId()))){
			//User is NOT a member of this project. so do nothing and return.
			return;
		}
		///////////////////////////////SECURITY//////////////////////////////

		User user = securityProfile.getUser();
		
		String action = request.getParameter("action");
		
		if ( action.equals("createAgileSprint")){
			
			int projectId = Integer.parseInt(request.getParameter("projectId"));

			String sprintName = request.getParameter("sprintName");
			String sprintDescription = request.getParameter("sprintDescription");
			String scrumMaster = request.getParameter("scrumMaster");
			String sprintStartDt = request.getParameter("sprintStartDt");
			String sprintEndDt = request.getParameter("sprintEndDt");
			
		
			
			Sprint sprint = new Sprint(  project, sprintName , sprintDescription,scrumMaster,sprintStartDt,sprintEndDt,
				databaseType);
			
			
			RequestDispatcher dispatcher =	request.getRequestDispatcher(
					"/jsp/AgileScrum/displayAgileScrumHome.jsp?sprintId=" +
					sprint.getSprintId());
			dispatcher.forward(request, response);
			
			return;
		}
		else if ( action.equals("updateAgileSprint")){
			
			int projectId = Integer.parseInt(request.getParameter("projectId"));
			int sprintId = Integer.parseInt(request.getParameter("sprintId"));
			Sprint oldSprint = new Sprint(sprintId, databaseType);

			String sprintName = request.getParameter("sprintName");
			String sprintDescription = request.getParameter("sprintDescription");
			String scrumMaster = request.getParameter("scrumMaster");
			String sprintStartDt = request.getParameter("sprintStartDt");
			String sprintEndDt = request.getParameter("sprintEndDt");
			
		
			
			String oldSprintName = oldSprint.getSprintName();
			String newSprintName = sprintName;
			
			// this is a little tricky
			// get all objects in sprint BEFORE you rename it
			// rename it (changes the agile sprint drop down values in req types)
			// then go through each object in the old sprint, and change the agile sprint value in the requirementt drop down)
			// follow the sequence or you get a bug
			
			
			
			// get all requirements in this sprint
			ArrayList requirements = SprintUtil.getRequirementsInSprint("", oldSprint, "", project, securityProfile, databaseType);
			
			
			// rename the sprint (i.e this modifies the sprint in db and modifies the req type's drop down values
			Sprint sprint = new Sprint( oldSprint,  project, sprintName , sprintDescription,scrumMaster,sprintStartDt,sprintEndDt,	databaseType);
			
			
			// now lets modify each req object's drop down agile sprint values
			if (!(newSprintName.equals(oldSprintName))){
				// for each element that was in thsi sprint, we have to change the 'Agile Sprint' attribute value to the new sprint name.
				Iterator r = requirements.iterator();
				while (r.hasNext()){
					Requirement requirement = (Requirement) r.next();
					// lets get the Req Type Attribute Id for 'agile sprint'.
					RTAttribute rTAttribute = new RTAttribute(requirement.getRequirementTypeId(), "Agile Sprint");
					// for this req, for the Agile Sprint Attribute  , lets set the value to this sprint.
					requirement.setCustomAttributeValue(rTAttribute.getAttributeId(), sprintName , user, databaseType);
					String log = "Renamed sprint : " + sprint.getSprintName() + " because the Sprint was renamed" ;
					RequirementUtil.createRequirementLog(requirement.getRequirementId(), log, user.getEmailId(), databaseType);
				}
				
				
			}
			
			PrintWriter out = response.getWriter();
			out.println("<span class='normalText'>Your changes have been applied.</span>");
			return;
		}

		else if (action.equals("deleteAgileSprint")){
			// we need to deleted the requirementType and refresh the project object in memory.
			int sprintId = Integer.parseInt(request.getParameter("sprintId"));
			Sprint sprint = new Sprint(sprintId, databaseType);
			if (
					(sprint.getScrumMaster().equals(user.getEmailId())
					||
					(securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())))
				) {
				// the user has permissions to delete  a sprint.
				
				// lets empty the 'Agile Sprint' value of All Requirements in this sprint.
				ArrayList requirements = SprintUtil.getRequirementsInSprint("", sprint, "", project, securityProfile, databaseType);
				Iterator r = requirements.iterator();
				while (r.hasNext()){
					Requirement requirement = (Requirement) r.next();
					// lets get the Req Type Attribute Id for 'agile sprint'.
					RTAttribute rTAttribute = new RTAttribute(requirement.getRequirementTypeId(), "Agile Sprint");
					// for this req, for the Agile Sprint Attribute  , lets set the value to this sprint.
					requirement.setCustomAttributeValue(rTAttribute.getAttributeId(), "", user, databaseType);
					String log = "Removed Requirement from Sprint : " + sprint.getSprintName() + " because the Sprint was deleted" ;
					RequirementUtil.createRequirementLog(requirement.getRequirementId(), log, user.getEmailId(), databaseType);
				}
				
				sprint.Destroy(databaseType);
				
				// once the sprint is deleted, all the Agile Scrum Enabled Req Types 'agile sprint' drop down will need to be refreshed.
				
				
				// now that the sprint is gone, we need to refresh the project object and put it back in the session.
				project = new Project(project.getProjectId(), databaseType);
				session.setAttribute("project", project);
			}
		}
		
		else if (action.equals("addRequirementsToSprint")){
			// this one is slightly different from the one called 'addRequirementsBacklog to sprint'.
			// in this one, the reqs are sennt as Br-1, BR-2...
			// in the backlong one, the req ids are sent in comma separated format.
			int projectId = Integer.parseInt(request.getParameter("projectId"));
			int sprintId = Integer.parseInt(request.getParameter("sprintId"));
			Sprint sprint = new Sprint(sprintId, databaseType);
			
			String requirementsToAdd = request.getParameter("requirementsToAdd");
			
		
			String status = "";
			
			if ((requirementsToAdd != null) && (!(requirementsToAdd.equals("")))){
				status = SprintUtil.addRequirementsToSprint(project, sprint,   requirementsToAdd, securityProfile, databaseType);
			}
			
			PrintWriter out = response.getWriter();
			if (status.equals("")){
				status = "Your changes have been applied. ";
			}
			
			out.println("<div  style='float: right; display:block'> " +
				" <a href='#' onclick='document.getElementById(\"addRequirementsToSprintMessageDiv\").style.display = \"none\";'>Close</a></div> " +
				 " <span class='normalText'>"+ status +"</span>");
			return;
		}
	
		else if (action.equals("moveRequirementsToSprint")){
			// this one is slightly different from the one called 'moveRequirementsSprint'.
			// we want to move all reqs in one sprint of a certain task status to another sprint.
			int projectId = Integer.parseInt(request.getParameter("projectId"));
			int sprintId = Integer.parseInt(request.getParameter("sprintId"));
			String typeOfTasks = request.getParameter("typeOfTasks");
			int targetSprintId = Integer.parseInt(request.getParameter("targetSprintId"));
			
			Sprint sprint = new Sprint(sprintId, databaseType);
			Sprint targetSprint = new Sprint(targetSprintId, databaseType);
		
			String status = SprintUtil.moveRequirementsToSprint(project, sprint,   typeOfTasks, targetSprint , securityProfile, databaseType);
			
			PrintWriter out = response.getWriter();
			if (status.equals("")){
				status = "Your changes have been applied. ";
			}
			
			out.println("<div  style='float: right; display:block'> " +
				" <a href='#' onclick='document.getElementById(\"moveRequirementsToSprintMessageDiv\").style.display = \"none\";'>Close</a></div> " +
				 " <span class='normalText'>"+ status +"</span>");
			return;
		}
			
		else if (action.equals("moveToAnotherSprint")){
			// in this one, we get one req id and we move this req to the new sprint.
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			int moveSprintId = Integer.parseInt(request.getParameter("moveSprintId"));
			Sprint sprint = new Sprint(moveSprintId, databaseType);
			
		
			Requirement requirement = new Requirement(requirementId, databaseType);
			
			// lets check to make sure the user has permissions to update this req.
			if ( securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
	    			+ requirement.getFolderId())){
				String status = SprintUtil.addRequirementsToSprint(project, sprint,   requirement.getRequirementFullTag() , securityProfile, databaseType);
			}
			
			
			return;
		}		
		else if (action.equals("removeRequirementFromSprint")){
			int projectId = Integer.parseInt(request.getParameter("projectId"));
			int sprintId = Integer.parseInt(request.getParameter("sprintId"));
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			Requirement requirement = new Requirement(requirementId, databaseType);
			
			// lets check to make sure the user has permissions to update this req.
			if ( securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
	    			+ requirement.getFolderId())){
				// lets get the Req Type Attribute Id for 'agile sprint'.
				RTAttribute rTAttribute = new RTAttribute(requirement.getRequirementTypeId(), "Agile Sprint");
				// for this req, for the Agile Sprint Attribute  , lets set the value to this sprint.
				requirement.setCustomAttributeValue(rTAttribute.getAttributeId(), "", user, databaseType);
					
			}
			
			return;
		}
		
		else if (action.equals("setStatusToNotStarted")){
			int projectId = Integer.parseInt(request.getParameter("projectId"));
			int sprintId = Integer.parseInt(request.getParameter("sprintId"));
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			Requirement requirement = new Requirement(requirementId, databaseType);
			
			// lets check to make sure the user has permissions to update this req.
			if ( securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
	    			+ requirement.getFolderId())){
				// lets get the Req Type Attribute Id for 'agile sprint'.
				RTAttribute rTAttribute = new RTAttribute(requirement.getRequirementTypeId(), "Agile Task Status");
				// for this req, for the Agile Sprint Attribute  , lets set the value to this sprint.
				requirement.setCustomAttributeValue(rTAttribute.getAttributeId(), "Not Started", user, databaseType);
			}
			return;
		}
		
		else if (action.equals("setStatusToInProgress")){
			int projectId = Integer.parseInt(request.getParameter("projectId"));
			int sprintId = Integer.parseInt(request.getParameter("sprintId"));
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			Requirement requirement = new Requirement(requirementId, databaseType);
			
			// lets check to make sure the user has permissions to update this req.
			if ( securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
	    			+ requirement.getFolderId())){
				// lets get the Req Type Attribute Id for 'agile sprint'.
				RTAttribute rTAttribute = new RTAttribute(requirement.getRequirementTypeId(), "Agile Task Status");
				// for this req, for the Agile Sprint Attribute  , lets set the value to this sprint.
				requirement.setCustomAttributeValue(rTAttribute.getAttributeId(), "In Progress", user, databaseType);
			}
			return;
		}		
		
		else if (action.equals("setStatusToBlocked")){
			int projectId = Integer.parseInt(request.getParameter("projectId"));
			int sprintId = Integer.parseInt(request.getParameter("sprintId"));
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			Requirement requirement = new Requirement(requirementId, databaseType);
			
			// lets check to make sure the user has permissions to update this req.
			if ( securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
	    			+ requirement.getFolderId())){
				// lets get the Req Type Attribute Id for 'agile sprint'.
				RTAttribute rTAttribute = new RTAttribute(requirement.getRequirementTypeId(), "Agile Task Status");
				// for this req, for the Agile Sprint Attribute  , lets set the value to this sprint.
				requirement.setCustomAttributeValue(rTAttribute.getAttributeId(), "Blocked", user, databaseType);
			}
			return;
		}	
		
		else if (action.equals("setStatusToCompleted")){
			int projectId = Integer.parseInt(request.getParameter("projectId"));
			int sprintId = Integer.parseInt(request.getParameter("sprintId"));
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			Requirement requirement = new Requirement(requirementId, databaseType);
			
			// lets check to make sure the user has permissions to update this req.
			if ( securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
	    			+ requirement.getFolderId())){
				// lets get the Req Type Attribute Id for 'agile sprint'.
				RTAttribute rTAttribute = new RTAttribute(requirement.getRequirementTypeId(), "Agile Task Status");
				// for this req, for the Agile Sprint Attribute  , lets set the value to this sprint.
				requirement.setCustomAttributeValue(rTAttribute.getAttributeId(), "Completed", user, databaseType);
				
				try {
					// lets try to set the work remaining to zero, as we are marking the requirement as completed.
					rTAttribute = new RTAttribute(requirement.getRequirementTypeId(), "Agile Effort Remaining (hrs)");
					// for this req, for the Agile Sprint Attribute  , lets set the value to this sprint.
					requirement.setCustomAttributeValue(rTAttribute.getAttributeId(), "0", user, databaseType);
						
				}
				catch (Exception e){
					e.printStackTrace();
				}
				//Since the req is completed, we will also need to set the percentage completed to 100
				requirement.setPercentComplete(100, user, databaseType);
			}
			return;
		}	
		
		else if (action.equals("assignRequirementToOwner")){
			String owner = request.getParameter("owner");
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			Requirement requirement = new Requirement(requirementId, databaseType);
			
			// lets check to make sure the user has permissions to update this req.
			if ( securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
	    			+ requirement.getFolderId())){

				String mailHost = this.getServletContext().getInitParameter("mailHost");
				String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
				String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
				String smtpPort = this.getServletContext().getInitParameter("smtpPort");
				String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
				String emailUserId = this.getServletContext().getInitParameter("emailUserId");
				String emailPassword = this.getServletContext().getInitParameter("emailPassword");
				
				requirement.setOwner(request, owner, user, databaseType, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
			}
			return;
		}		
		
		
		else if (action.equals("setRequirementEffortRemaining")){
			// javascript ensures that only numbers are sent in.
			String effortRemaining = request.getParameter("effortRemaining");

			
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			Requirement requirement = new Requirement(requirementId, databaseType);
			
			// lets check to make sure the user has permissions to update this req.
			if ( securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
	    			+ requirement.getFolderId())){
				// lets get the Req Type Attribute Id for 'Agile Effort Remaining'.
				RTAttribute rTAttribute = new RTAttribute(requirement.getRequirementTypeId(), "Agile Effort Remaining (hrs)");
				// for this req, for the Agile Sprint Attribute  , lets set the value to this sprint.
				requirement.setCustomAttributeValue(rTAttribute.getAttributeId(), effortRemaining , user, databaseType);
			}
			return;
		}	
		
		else if (action.equals("setRequirementTotalEffort")){
			// javascript ensures that only numbers are sent in.
			String totalEffort = request.getParameter("totalEffort");

			
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			Requirement requirement = new Requirement(requirementId, databaseType);
			
			// lets check to make sure the user has permissions to update this req.
			if ( securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
	    			+ requirement.getFolderId())){
				// lets get the Req Type Attribute Id for 'Agile Total Effort '.
				RTAttribute rTAttribute = new RTAttribute(requirement.getRequirementTypeId(), "Agile Total Effort (hrs)");
				// for this req, for the Agile Sprint Attribute  , lets set the value to this sprint.
				requirement.setCustomAttributeValue(rTAttribute.getAttributeId(), totalEffort , user, databaseType);
			}
			return;
		}
		else if (action.equals("setRequirementTaskWeight")){
			// javascript ensures that only numbers are sent in.
			String taskWeight = request.getParameter("taskWeight");
			String showOnlyTasksOwnedBy = request.getParameter("showOnlyTasksOwnedBy");
			
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			Requirement requirement = new Requirement(requirementId, databaseType);
			
			// lets check to make sure the user has permissions to update this req.
			if ( securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
	    			+ requirement.getFolderId())){
				// lets get the Req Type Attribute Id for 'Agile Effort Remaining'.
				RTAttribute rTAttribute = new RTAttribute(requirement.getRequirementTypeId(), "Agile Task Weight");
				// for this req, for the Agile Sprint Attribute  , lets set the value to this sprint.
				requirement.setCustomAttributeValue(rTAttribute.getAttributeId(), taskWeight , user, databaseType);
				
				// once taskWeight is set, we need to refresh the column .
				String taskStatus = request.getParameter("taskStatus");
				int sprintId = Integer.parseInt(request.getParameter("sprintId"));
				
				RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
				"&taskStatus="+ taskStatus + "&showOnlyTasksOwnedBy" + showOnlyTasksOwnedBy);
				dispatcher.forward(request, response);
				
			}
			
		}		
		
		else if (action.equals("addRequirementBacklogToSprint")){
			// javascript ensures that only numbers are sent in.
			
			int sprintId = Integer.parseInt(request.getParameter("sprintId"));
			Sprint sprint = new Sprint(sprintId, databaseType);
		
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			
		
			// first , lets create the requirement bean for the req id.
			Requirement requirement = new Requirement(requirementId,  databaseType);
			if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" + requirement.getFolderId()))) {
				return;
			}
			
			// if this requirement is locked and its locked by someone other than this user, then all updates to this req are disabled.
			if (
				(!(requirement.getRequirementLockedBy().equals("")))
				&&
				(!(requirement.getRequirementLockedBy().equals(user.getEmailId())))
				){
				return;
			}
			
			// at this point the user has write permissions on the req and it's not locked by some one else.
			// lets get the Req Type Attribute Id for 'agile sprint'.
			RTAttribute rTAttribute = new RTAttribute(requirement.getRequirementTypeId(), "Agile Sprint");
			// for this req, for the Agile Sprint Attribute  , lets set the value to this sprint.
			requirement.setCustomAttributeValue(rTAttribute.getAttributeId(), sprint.getSprintName(), user, databaseType);
	
			return;
			
		}	

		else if (action.equals("addScrumNotes")){
			
			int sprintId = Integer.parseInt(request.getParameter("sprintId"));
			Sprint sprint = new Sprint(sprintId, databaseType);
			String scrumNotes = request.getParameter("scrumNotes");
			sprint.addScrumNotes(user.getUserId(), scrumNotes, databaseType);
			return;
			
		}	
		
		
		else {
			// if nothing else works, forward to the Welcome screen. This should never happen.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/welcome.jsp");
			dispatcher.forward(request, response); 
		}
	}
}
