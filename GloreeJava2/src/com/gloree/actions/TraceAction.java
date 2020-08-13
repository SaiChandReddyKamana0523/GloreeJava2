package com.gloree.actions;

import com.gloree.beans.Project;
import com.gloree.beans.Requirement;
import com.gloree.beans.SecurityProfile;
import com.gloree.beans.Trace;
import com.gloree.beans.User;
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
//	This servlet is used to do trace action. i.e create, delete and mark suspect traces.
// Access controlled by security settings.
//
///////////////////////////////////////////Purpose ///////////////////////////////////////////



public class TraceAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public TraceAction() {
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
		// And he needs to be both a Member of Project and have trace permissions
		// to the target requirement.
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
		
		
		User user = securityProfile.getUser();
		// TODO : put in code to restrict access to only those users who have tracebilty
		// privs on this req type / folder.
		///////////////////////////////SECURITY//////////////////////////////


		
		
		String action = request.getParameter("action");
		

		
		
		
		
		int requirementId = Integer.parseInt(request.getParameter("requirementId"));

		String status = "";
		
		if ( action.equals("deleteTraceInTraceTree")){
			
			int traceId = Integer.parseInt(request.getParameter("traceId"));
			status = RequirementUtil.deleteTrace(traceId, user.getEmailId(), securityProfile,  databaseType);
		

 			if ( (status==null) || (status.length() == 0)){
 				status =  " <font color='red'><b>Trace has been deleted. Please refresh report to show updated downstream relationships </b></font>	 ";
 			}
			PrintWriter out = response.getWriter();
			out.println( status );
			return;
			
			
		}
		
		if ( action.equals("markSuspectTraceInTraceTree")){
			
			Requirement requirement = new Requirement(requirementId, databaseType);

			int traceId = Integer.parseInt(request.getParameter("traceId"));
			status =  RequirementUtil.makeSuspect(traceId, user.getEmailId(), securityProfile,  databaseType);	
			
			
			
			int traceLevel = Integer.parseInt(request.getParameter("traceLevel"));
 			String spacer = "";
 			if (traceLevel == 2) {
 				spacer = "&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				
 			
 			}
 			else if (traceLevel == 3) {
 				spacer = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;";
 				
 			
 			}
 			else if (traceLevel == 4) {
 				spacer = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;";
 				

 			}
 			else if (traceLevel == 5) {
 				spacer = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;";
 				
 			
 			}
 			else if (traceLevel == 6) {
 				spacer = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;";
 				
 			
 			}
 			else if (traceLevel == 7) {
 				spacer = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;";
 				

 			
 			}
 			

 			if ( (status==null) || (status.length() == 0)){
 				status = " <img src='/GloreeJava2/images/sTrace1.jpg' border='0' > " ;
 				
				if (requirement.getProjectId()== project.getProjectId()){

					status += "<font color='red'> " + requirement.getRequirementFullTag()+ "</font>";
 				}
				else {
					status += "<font color='red'> " + requirement.getProjectShortName() + ":" +  requirement.getRequirementFullTag()+ "</font>";
				}							   						
					
 				status +=  " <b> Trace has been made suspect </b>";
 			}
			PrintWriter out = response.getWriter();
			out.println(  spacer + status );
			return;
		}
		if ( action.equals("markClearTraceInTraceTree")){
			
			Requirement requirement = new Requirement(requirementId, databaseType);

			int traceId = Integer.parseInt(request.getParameter("traceId"));
			status = RequirementUtil.clearSuspect(traceId, user.getEmailId(), securityProfile,  databaseType);	
			
			
			int traceLevel = Integer.parseInt(request.getParameter("traceLevel"));
 			String spacer = "";
 			if (traceLevel == 2) {
 				spacer = "&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				
 			
 			}
 			else if (traceLevel == 3) {
 				spacer = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;";
 				
 			
 			}
 			else if (traceLevel == 4) {
 				spacer = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;";
 				

 			}
 			else if (traceLevel == 5) {
 				spacer = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;";
 				
 			
 			}
 			else if (traceLevel == 6) {
 				spacer = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;";
 				
 			
 			}
 			else if (traceLevel == 7) {
 				spacer = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
 				spacer += "&nbsp;&nbsp;&nbsp;&nbsp;";
 				

 			
 			}
 			
 			
 			
 			
 			if ( (status==null) || (status.length() == 0)){
 				status = " <img src='/GloreeJava2/images/cTrace1.jpg' border='0' > " ;
 				
				if (requirement.getProjectId()== project.getProjectId()){

					status += "<font color='green'> " + requirement.getRequirementFullTag()+ "</font>";
 				}
				else {
					status += "<font color='green'> " + requirement.getProjectShortName() + ":" +  requirement.getRequirementFullTag()+ "</font>";
				}							   						
					
 				status +=  " <b> Suspect Trace has been cleared </b>";
 			}
 			
 			
 			
			PrintWriter out = response.getWriter();
			out.println(  spacer + status );
			return;
		}
		
		if ( action.equals("deleteTrace")){
			int traceId = 0;
			try {
				traceId = Integer.parseInt(request.getParameter("traceId"));
			}
			catch (Exception e) {
				// if traceId = 0, we were probably sent from req id and to req id
				
					int fromReqId = Integer.parseInt(request.getParameter("fromReqId"));
					int toReqId = Integer.parseInt(request.getParameter("toReqId"));
					Trace trace = new Trace(fromReqId, toReqId);
					traceId = trace.getId();
				
			}
			status = RequirementUtil.deleteTrace(traceId, user.getEmailId(), securityProfile,  databaseType);			
		}
		else if ( action.equals("deleteTraceInTraceMatrix")){
			int traceId = Integer.parseInt(request.getParameter("traceId"));
			status = RequirementUtil.deleteTrace(traceId, user.getEmailId(), securityProfile,  databaseType);		
			
			if (status != ""){
				request.setAttribute("status", status);
			}
			
			RequestDispatcher dispatcher =	
				request.getRequestDispatcher("/jsp/TraceMatrix/displayFromRequirementTraceTo.jsp?requirementId="
					+ requirementId);
			dispatcher.forward(request, response);
			return;
		}
		else if (action.equals("clearSuspect")){
			int traceId = 0;
			try {
				traceId = Integer.parseInt(request.getParameter("traceId"));
			}
			catch (Exception e) {
				// if traceId = 0, we were probably sent from req id and to req id
				
					int fromReqId = Integer.parseInt(request.getParameter("fromReqId"));
					int toReqId = Integer.parseInt(request.getParameter("toReqId"));
					Trace trace = new Trace(fromReqId, toReqId);
					traceId = trace.getId();
				
			}
			status = RequirementUtil.clearSuspect(traceId, user.getEmailId(), securityProfile,  databaseType);	
		}
		else if (action.equals("updateTraceReason")){
			int traceId = Integer.parseInt(request.getParameter("traceId"));
			String traceReason = request.getParameter("traceReason");
			status = RequirementUtil.setTraceReason(traceReason, traceId, user.getEmailId(), securityProfile,  databaseType);
			// lets print the traceBlock
			
			
			PrintWriter out = response.getWriter();
			out.println(status);
			
			return;
		}
		else if (action.equals("makeSuspect")){
			int traceId = Integer.parseInt(request.getParameter("traceId"));
			status =  RequirementUtil.makeSuspect(traceId, user.getEmailId(), securityProfile,  databaseType);	
		}
		else if (action.equals("modifyTracesInBulk")){
			// lets find out the specific action and then work on it. 
			String bulkTraceAction = request.getParameter("bulkTraceAction");
			Requirement requirement = new Requirement(requirementId, databaseType);

			if (bulkTraceAction.equals("clearAllTracesTo")){
				// lets clear all Traces to
				ArrayList traceToObjects = requirement.getRequirementTraceToObjects();
				Iterator tTOIterator = traceToObjects.iterator();
				while (tTOIterator.hasNext()){
					Trace trace = (Trace) tTOIterator.next();
					// for each of these traces, lets clear the trace.
					status +=  RequirementUtil.clearSuspect(trace.getId(), user.getEmailId(), securityProfile,  databaseType) ;
				}
			}
			if (bulkTraceAction.equals("makeSuspectAllTracesTo")){
				// lets make suspect  all Traces to 
				ArrayList traceToObjects = requirement.getRequirementTraceToObjects();
				Iterator tTOIterator = traceToObjects.iterator();
				while (tTOIterator.hasNext()){
					Trace trace = (Trace) tTOIterator.next();
					// for each of these traces, lets make suspect the trace.
					status +=  RequirementUtil.makeSuspect(trace.getId(), user.getEmailId(), securityProfile,  databaseType) ;
				}
			}
			if (bulkTraceAction.equals("deleteAllTracesTo")){
				// lets delete  all Traces to 
				ArrayList traceToObjects = requirement.getRequirementTraceToObjects();
				Iterator tTOIterator = traceToObjects.iterator();
				while (tTOIterator.hasNext()){
					Trace trace = (Trace) tTOIterator.next();
					// for each of these traces, lets delete the trace.
					status +=  RequirementUtil.deleteTrace(trace.getId(), user.getEmailId(), securityProfile,  databaseType) ;
				}
			}
			
			
			
			
			
			if (bulkTraceAction.equals("clearAllTracesFrom")){
				// lets clear all Traces from
				ArrayList traceFromObjects = requirement.getRequirementTraceFromObjects();
				Iterator tFOIterator = traceFromObjects.iterator();
				while (tFOIterator.hasNext()){
					Trace trace = (Trace) tFOIterator.next();
					// for each of these traces, lets clear the trace.
					status +=  RequirementUtil.clearSuspect(trace.getId(), user.getEmailId(), securityProfile,  databaseType) ;
					
				}
			}
			if (bulkTraceAction.equals("makeSuspectAllTracesFrom")){
				// lets make suspect all Traces from
				ArrayList traceFromObjects = requirement.getRequirementTraceFromObjects();
				Iterator tFOIterator = traceFromObjects.iterator();
				while (tFOIterator.hasNext()){
					Trace trace = (Trace) tFOIterator.next();
					// for each of these traces, lets make suspect the trace.
					status +=  RequirementUtil.makeSuspect(trace.getId(), user.getEmailId(), securityProfile,  databaseType) ;
				}
			}
			if (bulkTraceAction.equals("deleteAllTracesFrom")){
				// lets delete all Traces from
				ArrayList traceFromObjects = requirement.getRequirementTraceFromObjects();
				Iterator tFOIterator = traceFromObjects.iterator();
				while (tFOIterator.hasNext()){
					Trace trace = (Trace) tFOIterator.next();
					// for each of these traces, lets delete the trace.
					status +=  RequirementUtil.deleteTrace(trace.getId(), user.getEmailId(), securityProfile,  databaseType) ;
				}
			}
		}
		
	
		if (status != ""){
			request.setAttribute("status", status);
		}
		
		String actionDropDown = request.getParameter("actionDropDown");
		if (actionDropDown == null){
			actionDropDown = "";
		}
		
		if (actionDropDown.equals("actionDropDown")){
			// this was called from the action drop down 
			if (status != ""){
				PrintWriter out = response.getWriter();
				out.println(status);
			}
			return;
		}
		
		RequestDispatcher dispatcher =	
			request.getRequestDispatcher("/jsp/Requirement/displayRequirementTrace.jsp?requirementId="
				+ requirementId);
		dispatcher.forward(request, response);
	}

}
