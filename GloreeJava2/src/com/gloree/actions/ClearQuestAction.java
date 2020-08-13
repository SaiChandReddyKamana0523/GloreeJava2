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


public class ClearQuestAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ClearQuestAction() {
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
		
		if (!(securityProfile.getRoles().contains("MemberInProject" + projectId))){
			//User is NOT a member of this project. so do nothing and return.
			return;
		}
		///////////////////////////////SECURITY//////////////////////////////

		User user = securityProfile.getUser();
		
		String action = request.getParameter("action");
		
		if ( action.equals("pushToTraceCloud")){
			
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			String requirementFullTag = request.getParameter("requirementFullTag");
			
			String CTCID = request.getParameter("CTCID");
			String CTCHEADLINE = request.getParameter("CTCHEADLINE");
			String TESTCASEID = request.getParameter("TESTCASEID");
			String TESTCASEHEADLINE = request.getParameter("TESTCASEHEADLINE");
			String CTCWEBLINK = request.getParameter("CTCWEBLINK");
			
			String RELATEDSCRID = request.getParameter("RELATEDSCRID");
			String RELATEDSCRNAME = request.getParameter("RELATEDSCRNAME");
			
			
			String SCRID = request.getParameter("SCRID");
			String SCRTITLE = request.getParameter("SCRTITLE");
			String SCRWEBLINK = request.getParameter("SCRWEBLINK");
			
			
			Requirement requirement = new Requirement(requirementId, databaseType);
			if (!(securityProfile.getPrivileges().contains("traceToRequirementsInFolder" 
	    			+ requirement.getFolderId()))){
				PrintWriter out = response.getWriter();
				out.println("You don't have permissions to Trace To " + requirementFullTag);
				  return;
			}
	    	
			ClearQuestUtil.pushToTraceCloud(requirement, user, CTCID, CTCHEADLINE, TESTCASEID, TESTCASEHEADLINE, CTCWEBLINK,
					RELATEDSCRID, RELATEDSCRNAME, SCRID, SCRTITLE, SCRWEBLINK, securityProfile, request, databaseType);
			
			return;
		}
		else {
			// if nothing else works, forward to the Welcome screen. This should never happen.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/welcome.jsp");
			dispatcher.forward(request, response); 
		}
	}
}
