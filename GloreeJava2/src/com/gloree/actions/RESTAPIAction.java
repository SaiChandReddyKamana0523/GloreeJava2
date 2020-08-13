package com.gloree.actions;

/////////////////////////////////////////////Purpose ///////////////////////////////////////////
//
//	This servlet is used to create and open projects. it's also used for some admin actions
// like create Requirement Types.
//
///////////////////////////////////////////Purpose ///////////////////////////////////////////


import com.gloree.beans.SecurityProfile;
import com.gloree.utils.RESTAPIUtil;
import com.gloree.utils.ScheduledReportsUtil;

import java.io.IOException;
import java.io.PrintWriter;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;


public class RESTAPIAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
        
    public RESTAPIAction() {
        super();
    }

    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost (request,response);
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 
		String databaseType = this.getServletContext().getInitParameter("databaseType");

		PrintWriter out = response.getWriter();
		String action = request.getParameter("action");
		
		
		if (action.startsWith("generic") ){
			// generic actions are coming from traceNow and don't need a sign in.
			// these are user agnostic actions , hence no sign in is required.
			
			System.out.println("Recceived generic action " + action);
			if (action.equals("genericEmailRequirementsReport")){
				RESTAPIUtil.genericEmailRequirementsReport(this.getServletContext(), request, response, out);
				return;
			}
			
			if (action.equals("genericGetExcelFile")){
				RESTAPIUtil.genericGetExcelFile(this.getServletContext(), request, response, out);
				return;
			}
		}
		
		if (action.equals("signIn")){
			RESTAPIUtil.signIn(request, databaseType, out);
			return;
		}
		
		if (action.equals("createUser")){
			RESTAPIUtil.createUser(request, out,   databaseType);
			return;
		}
		
		
		///////////////////////////////SECURITY//////////////////////////////
		// Security  Note:
		// this is driven completely bu APIKey. When we grant the API key we
		// need to ensure that its long (very very long) - use Flikr as an example
		// and generate it.
		// Every single API call must have it and we check to make sure that it
		// is in the system.
		///////////////////////////////SECURITY//////////////////////////////
		String key = request.getParameter("key");
		
		// call validateKey. This returns true if this key is valid.
		// we proceed only if this is true. At this point, we know that this is a valid user
		// and he / she is within her daily call limits.
		boolean validKey = RESTAPIUtil.validateKey(request, out, key, databaseType);
		if (!validKey) {
			return;
		}
		
		// lets get the security profile for this key.
		SecurityProfile securityProfile = RESTAPIUtil.getSecurityProfile(key, this.getServletContext().getInitParameter("databaseType"));
		
		if (action == null ) {
			JSONObject json = new JSONObject();
			json.put("status", "error");
			json.put("errorMessage", "You need to specify an action with the API call");
			out.print(json.toString(5));
			return;
		}
		////////////////////////////////
		// project api calls
		////////////////////////////////	
		
		
		
		if (action.equals("getMyProjects")){
			RESTAPIUtil.getMyProjects(request, out, key, securityProfile);
			return;
		}
		
		if (action.equals("getProjectDetails")){
			RESTAPIUtil.getProjectDetails(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("getProjectRequirementTypes")){
			RESTAPIUtil.getProjectRequirementTypes(request, out, key, securityProfile,  databaseType);
			return;
		}

		if (action.equals("getProjectFolders")){
			RESTAPIUtil.getProjectFolders(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("getProjectUsers")){
			RESTAPIUtil.getProjectUsers(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("getProjectReports")){
			RESTAPIUtil.getProjectReports(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("createRequirementType")){
			RESTAPIUtil.createRequirementType(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("createFolder")){
			RESTAPIUtil.createFolder(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		
		
		////////////////////////////////		
		// requirement type api calls 
		////////////////////////////////		
		
		if (action.equals("getRequirementTypeDetails")){
			RESTAPIUtil.getRequirementTypeDetails(request, out, key, securityProfile);
			return;
		}
		
		if (action.equals("getRequirementsInRequirementType")){
			RESTAPIUtil.getRequirementsInRequirementType(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("createAttribute")){
			RESTAPIUtil.createAttribute(request, out, key, securityProfile);
			return;
		}
		
		////////////////////////////////		
		// folder api calls 
		////////////////////////////////
		if (action.equals("getFolderDetails")){
			RESTAPIUtil.getFolderDetails(request, out, key, securityProfile);
			return;
		}
		
		if (action.equals("getRequirementsInFolder")){
			RESTAPIUtil.getRequirementsInFolder(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		
		////////////////////////////////		
		// user api calls 
		////////////////////////////////
		if (action.equals("getUsersRequirements")){
			RESTAPIUtil.getUsersRequirements(request, out, key, securityProfile,  databaseType);
			return;
		}
		

		////////////////////////////////		
		// report api calls 
		////////////////////////////////
		if (action.equals("getReportRequirements")){
			RESTAPIUtil.getReportRequirements(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		////////////////////////////////
		// requirement api calls 
		////////////////////////////////
		if (action.equals("getRequirementById")){
			RESTAPIUtil.getRequirementById(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("getRequirementByFullTag")){
			RESTAPIUtil.getRequirementByFullTag(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("getRequirementTraceTo")){
			RESTAPIUtil.getRequirementTracesTo(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("getRequirementTraceFrom")){
			RESTAPIUtil.getRequirementTracesFrom(request, out, key, securityProfile,  databaseType);
			return;
		}
		

		if (action.equals("createRequirement")){
			RESTAPIUtil.createRequirement(request, out, key, securityProfile,  databaseType);
			return;
		}

		if (action.equals("deleteRequirement")){
			RESTAPIUtil.deleteRequirement(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("purgeRequirement")){
			RESTAPIUtil.purgeRequirement(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("moveRequirement")){
			RESTAPIUtil.moveRequirement(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("setAttribute")){
			RESTAPIUtil.setAttribute(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("createTrace")){
			RESTAPIUtil.createTrace(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("deleteTrace")){
			RESTAPIUtil.deleteTrace(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("clearSuspectTrace")){
			RESTAPIUtil.clearSuspectTrace(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("makeTraceSuspect")){
			RESTAPIUtil.makeTraceSuspect(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		////////////////////////////////
		// Jira api calls 
		////////////////////////////////
		if (action.equals("jiraUpdate")){
		RESTAPIUtil.jiraUpdate(request, out, key, securityProfile,  databaseType);
		return;
		}

		////////////////////////////////
		// search api calls 
		////////////////////////////////
		if (action.equals("search")){
			RESTAPIUtil.getRequirementBySearchString(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		////////////////////////////////
		// scheduled reports execution 
		////////////////////////////////
		if (action.equals("runScheduledReports")){
			out.println("Starting the Scheduled Reports Run");
			String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");

			
			String mailHost = this.getServletContext().getInitParameter("mailHost");
			String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
			String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
			String smtpPort = this.getServletContext().getInitParameter("smtpPort");
			String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
			String emailUserId = this.getServletContext().getInitParameter("emailUserId");
			String emailPassword = this.getServletContext().getInitParameter("emailPassword");
			
			int  maxRowsInTraceTree = Integer.parseInt(this.getServletContext().getInitParameter("maxRowsInTraceTree"));

			
			ScheduledReportsUtil.startScheduledReports(request, out, key, securityProfile, rootDataDirectory, databaseType,
					mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword, maxRowsInTraceTree);
			out.println("Ending the Scheduled Reports Run");
			return;
		}


		////////////////////////////////
		// Dip T calls
		////////////////////////////////		
		
		
		
		
		
		if (action.equals("createTask")){
			RESTAPIUtil.createTask(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("updateTask")){
			RESTAPIUtil.updateTask(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("getTasks")){
			RESTAPIUtil.getTasks(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		if (action.equals("getValues")){
			RESTAPIUtil.getValues(request, out, key, securityProfile,  databaseType);
			return;
		}
		
		// if we haven't returned yet, that means that the action call was incorrect.
		JSONObject json = new JSONObject();
		json.put("status", "error");
		json.put("errorMessage", "Your action '" + action + "' is not supported by TraceCloud API");
		out.print(json.toString(5));
		return;
	}
		
}
