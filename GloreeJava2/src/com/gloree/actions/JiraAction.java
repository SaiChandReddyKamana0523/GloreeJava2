package com.gloree.actions;

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


public class JiraAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public JiraAction() {
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
		int projectId = Integer.parseInt(request.getParameter("projectId"));
		int requirementId = Integer.parseInt(request.getParameter("requirementId"));
		Requirement requirement = new Requirement(requirementId, databaseType);
		
		if (!(securityProfile.getRoles().contains("MemberInProject" + requirement.getProjectId()))){
			//User is NOT a member of this project. so do nothing and return.
			return;
		}
		///////////////////////////////SECURITY//////////////////////////////

		User user = securityProfile.getUser();
		
		String action = request.getParameter("action");
		
		if ( action.equals("pushJiraToTraceCloud")){
			
			
			String requirementFullTag = request.getParameter("requirementFullTag");
			
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
			
			
			
			if (!(securityProfile.getPrivileges().contains("traceToRequirementsInFolder" 
	    			+ requirement.getFolderId()))){
				PrintWriter out = response.getWriter();
				out.println("You don't have permissions to Trace To " + requirementFullTag);
				  return;
			}
	    	
			
			
			JiraUtil.pushJiraToTraceCloud(requirement, user, 
					JID, JPROJECT, JTYPE, JPRIORITY, JLABELS, JSTATUS,
					JRESOLUTION, JAFFECTSV, JFIXV, JASSIGNEE, JREPORTER, 
					JCREATED, JUPDATED, JURL, JTITLE, JDESCRIPTION,
					securityProfile, request, session, databaseType);
			
			return;
		}
		else {
			// if nothing else works, forward to the Welcome screen. This should never happen.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/welcome.jsp");
			dispatcher.forward(request, response); 
		}
	}
}
