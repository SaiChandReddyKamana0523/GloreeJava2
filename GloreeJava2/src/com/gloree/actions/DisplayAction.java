package com.gloree.actions;

/////////////////////////////////////////////Purpose ///////////////////////////////////////////
//
//	This servlet is used to display a hyperlink of a requirement , report, folder, doc etc...
//
///////////////////////////////////////////Purpose ///////////////////////////////////////////

import com.gloree.beans.*;
import com.gloree.utils.ProjectUtil;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DisplayAction extends HttpServlet {
    
	public DisplayAction() {
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
		// And he needs to be an Member
		// of the project this requirement belongs to.
		///////////////////////////////SECURITY//////////////////////////////
		
		
		System.out.println("srt in DisplayAction");
		HttpSession session = request.getSession(true);
		if (session == null ){
			System.out.println("srt session is null");
		}
		else {
			System.out.println("srt session is not null");
		}
		
		int displayProjectId = 0;
		int displayFolderId = 0;
		
		// put all the input info into the session.
		String dO = request.getParameter("dO");
		session.setAttribute("dO", dO);

		if (dO.equals("req" )){
			int displayRequirementId = Integer.parseInt(request.getParameter("dReqId"));
			String projectIdFolderId  = 
				ProjectUtil.getProjectIdFolderIdForRequirement(displayRequirementId);
			if (projectIdFolderId == null || projectIdFolderId.length()==0){
				// can't find the req. Show a proper error page. 
				RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Requirement/canNotFindRequiremet.jsp?requirementId=" + displayRequirementId);
				dispatcher.forward(request, response);
				return;
				
			}
			String [] pF = projectIdFolderId.split(":##:");
			displayProjectId = Integer.parseInt(pF[0]);
			displayFolderId = Integer.parseInt(pF[1]);
			session.setAttribute("displayProjectId", (Integer.toString(displayProjectId)));
			session.setAttribute("displayRequirementId", (Integer.toString(displayRequirementId)));
			session.setAttribute("displayFolderId", (Integer.toString(displayFolderId)));
		}
		if (dO.equals("rep" )){
			int displayReportId = Integer.parseInt(request.getParameter("dRepId"));
			Report report = new Report (displayReportId);
			displayProjectId = report.getProectId();
			displayFolderId = report.getFolderId();
			session.setAttribute("displayProjectId", (Integer.toString(displayProjectId)));
			session.setAttribute("displayReportId", (Integer.toString(displayReportId)));
			session.setAttribute("displayFolderId", (Integer.toString(displayFolderId)));
		}
		
		if (dO.equals("attachment" )){
			int displayAttachmentId = Integer.parseInt(request.getParameter("dAttachmentId"));
			session.setAttribute("displayAttachmentId", Integer.toString(displayAttachmentId));
		}
		if (dO.equals("healthCheck" )){
			displayProjectId = Integer.parseInt(request.getParameter("displayProjectId"));
			String displayFunction = request.getParameter("displayFunction");
			session.setAttribute("displayProjectId", (Integer.toString(displayProjectId)));
			session.setAttribute("displayFunction", displayFunction);
		}
		// see if the user is logged in. If he is not, the method below will
		// redirect him to the log in page.
		// since we want some special redirection logic, we are replicating the 
		// SecurityUtil.checkAuthentication code here.
		// SecurityUtil.checkAuthentication(request, response);
		// now check if this users should be in this project
		
		
		
		String authenticationType = this.getServletContext().getInitParameter("authenticationType");
		SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

		if ((authenticationType.equals("ldap")) && (securityProfile == null)){
			// this is an ldap user and not logged in. So, lets get his security profile
			session.setAttribute("redirectToDisplay", "redirectToDisplay");
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/servlet/UserAccountAction?action=signIn");
			dispatcher.forward(request, response);
			return;		
					
		}
		if (authenticationType.equals("database")){
			// if the user is not logged in, lets send him to the sign in page.
			
			String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
			System.out.println("srt is userLoggedin? " + isLoggedIn);
			
			if ((isLoggedIn == null) || (isLoggedIn.equals(""))){
				System.out.println("srt is not logged in. sending for re log in");
				
				// not logged in . so redirect.
				// we need to change the startPage logic so that after successful log in the user
				// get redirected to displayAction.
				session.setAttribute("redirectToDisplay", "redirectToDisplay");
				RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/WebSite/startPage.jsp");
				dispatcher.forward(request, response);
				return;
			}
			System.out.println("srt user is logged in. It should diplay the req  " );
			
		}
		

		// At this point the user is logged in. If the request is to download an attachment, 
		// and the user has permissions to get the file, lets download the file. 
		if (dO.equals("attachment" )){
			String displayAttachmentId = (String) session.getAttribute("displayAttachmentId");
			RequirementAttachment attachment = new RequirementAttachment(Integer.parseInt(displayAttachmentId), databaseType);
			Requirement requirement = new Requirement(attachment.getRequirementId(),databaseType);
			if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
					+ requirement.getFolderId()))){
				// the user does not have read permissions. so lets print an error message.
				PrintWriter out = response.getWriter();
				out.println("<div class='alert alert-success'><span class='normalText'>You do not have READ permissions on this requirement.</span></div>");
				return;

			}
			else {
				// the user had read permissions on the requirement to which this file is attached. So lets download the file.
				// the contentlength is needed for MSIE!!!
				
				try{
				    File                f        = new File(attachment.getFilePath());
			        int                 length   = 0;
			        ServletOutputStream op       = response.getOutputStream();
			        ServletContext      context  = getServletConfig().getServletContext();
			        String              mimetype = context.getMimeType( attachment.getFilePath() );

			        //
			        //  Set the response and go!
			        //
			        //
			        response.setContentType( (mimetype != null) ? mimetype : "application/octet-stream" );
			        response.setContentLength( (int)f.length() );
			        response.setHeader( "Content-Disposition", "attachment; filename=\"" + attachment.getFileName() + "\"" );

			        //
			        //  Stream to the requester.
			        //
			        byte[] bbuf = new byte[1000];
			        DataInputStream in = new DataInputStream(new FileInputStream(f));

			        while ((in != null) && ((length = in.read(bbuf)) != -1))
			        {
			            op.write(bbuf,0,length);
			        }

			        in.close();
			        op.flush();
			        op.close();

				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		
		// At this point, the user is logged in. Lets see if he should be getting 
		// access to this requirement / project.
		Project project = new Project(displayProjectId, databaseType);
		if (!(securityProfile.getRoles().contains("MemberInProject" + project.getProjectId()))){
			//User is NOT a member of this project. at this point we can redirect him to the UDB 
			// with a message..
			request.setAttribute("notAMemberOfProject", "notAMemberOfProject");
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/UserDashboard/userProjects.jsp");
			dispatcher.forward(request, response);
			return;
		}
		
		
		
		
		// At this point , the user is logged in and is a member of the project displayProjectId
		// so lets set a session variable for this project and forward to OpenProject (yP.jsp).
		session.setAttribute("project", project);
		RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/OpenProject/yP.jsp");
		dispatcher.forward(request, response);
		return;
		
		///////////////////////////////SECURITY//////////////////////////////
	}

}
