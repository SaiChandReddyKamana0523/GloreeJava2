package com.gloree.actions;

import com.gloree.beans.*;
import com.gloree.utils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;


public class TDCSAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public TDCSAction() {
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
		
		User user = securityProfile.getUser();
		///////////////////////////////SECURITY//////////////////////////////
		
		String action = request.getParameter("action");
		

		if (action.equals("downloadDocument")) {
			int tDCSDocumentId = Integer.parseInt(request.getParameter("tDCSDocumentId"));
			int versionNumber = Integer.parseInt(request.getParameter("versionNumber"));
			
			TDCSDocumentVersion tDCSDocumentVersion = new TDCSDocumentVersion(tDCSDocumentId, versionNumber, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (tDCSDocumentVersion.getProjectId() != project.getProjectId()) {
				return;
			}
			if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
					+ tDCSDocumentVersion.getFolderId()))){
				// the user does not have read permissions on this folder.
				return;
			}

			
			///////////////////////////////SECURITY CODE ////////////////////////////
			
			
			FileInputStream inputStream= new FileInputStream(tDCSDocumentVersion.getVersionFilePath());
			
			if (tDCSDocumentVersion.getVersionFileType().equals("excel")){
				response.setHeader("Expires", "0");
				response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
				response.setHeader("Pragma", "public");
				response.setContentType("application/vnd.ms-excel");
	    	}
			if (tDCSDocumentVersion.getVersionFileType().equals("word")){
				response.setHeader("Expires", "0");
				response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
				response.setHeader("Pragma", "public");
				response.setContentType("application/ms-word");
	    	}
			if (tDCSDocumentVersion.getVersionFileType().equals("pdf")){
				response.setHeader("Expires", "0");
				response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
				response.setHeader("Pragma", "public");
				response.setContentType("application/pdf");
	    	}
			
			
			response.setHeader("Content-Disposition", "attachment; filename=\"" + tDCSDocumentVersion.getVersionFileName() + "\"");
    		
	        ServletOutputStream out = response.getOutputStream();
	        byte buf[]=new byte[1024];
	        int len;
	        while((len=inputStream.read(buf))>0)
	        	out.write(buf,0,len);
	        
	        out.close();
	        inputStream.close();
			
			return;
		}
		else if (action.equals("showPreviousVersions")){
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/TDCS/displayPreviousVersionsOfDocument.jsp");
			dispatcher.forward(request, response);
			return;
		}
		else if (action.equals("lockDocument")){
			int tDCSDocumentId = Integer.parseInt(request.getParameter("tDCSDocumentId"));
			TDCSDocument tDCSDocument = new TDCSDocument(tDCSDocumentId, databaseType);
			if ((securityProfile.getPrivileges().contains("createRequirementsInFolder" 
					+ tDCSDocument.getFolderId()))){
				tDCSDocument.lockDocument(user,  databaseType);
			}
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/TDCS/displayFilteredTDCSDocuments.jsp");
			dispatcher.forward(request, response);
			return;
		}
		else if (action.equals("unlockDocument")){
			int tDCSDocumentId = Integer.parseInt(request.getParameter("tDCSDocumentId"));
			TDCSDocument tDCSDocument = new TDCSDocument(tDCSDocumentId, databaseType);
			if (											
					(securityProfile.getRoles().contains("AdministratorInProject" + tDCSDocument.getProjectId()))
					||
					(tDCSDocument.getCurrentVersionDocumentStatusBy().equals(user.getEmailId()))
				){
				// this user is either an admin on the project or was the one who put 
				// the lock in the first place.
				tDCSDocument.unlockDocument(user,  databaseType);
			}
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/TDCS/displayFilteredTDCSDocuments.jsp");
			dispatcher.forward(request, response);
			return;
		}
	}

}
