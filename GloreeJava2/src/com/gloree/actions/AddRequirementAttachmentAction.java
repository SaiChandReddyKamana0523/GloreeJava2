package com.gloree.actions;

/////////////////////////////////////////////Purpose ///////////////////////////////////////////
//
//	This servlet is used to import an excel file to either create requiremetns / update reqs.
//
///////////////////////////////////////////Purpose ///////////////////////////////////////////

import com.gloree.beans.*;
import com.gloree.utils.RequirementUtil;
import com.gloree.utils.SecurityUtil;


import java.io.File;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.multipart.*;
import java.text.SimpleDateFormat;
import java.util.*;
//Excel POI stuff.


public class AddRequirementAttachmentAction extends HttpServlet {
    
	public AddRequirementAttachmentAction() {
        super();
    }

    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost (request,response);
    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String databaseType = this.getServletContext().getInitParameter("databaseType");
		
		
		// lets set the request's character encoding. Otherwise the polish and UTF 8 char's aren't coming through the 
		// Post process
		
		
		request.setCharacterEncoding("UTF-8");
		
		
		///////////////////////////////SECURITY//////////////////////////////
		// Security  Note:
		// user has to be logged in by the time he is here. 
		// And he needs to be an Member
		// of this project. Note : Admins are members too.
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
		
		// For Requirement related operations, if you are getting requirementId as a param
		// lets check to make sure that requirement.getProjectId == session.project.getProjectId.
		// this check will have to be made where requirement Id is read. 
		// THIS IS CRITICAL AS IT WILL CATCH SCENARIOS WHERE USERS ARE TRYING TO DO 
		// OPERATIONS ON REQUIREMENTS THEY DO NOT HAVE ACCESS TO by passing in requirement ids
		// that exist in different projects.
		
		///////////////////////////////SECURITY//////////////////////////////

		
		// if the user is trying at attach an existing file, then it's much simpler
		String actorEmailId = securityProfile.getUser().getEmailId();
		
		String addExistingFilesHidden = request.getParameter("addExistingFilesHidden");
		System.out.println("srt addExistingFilesHidden is " + addExistingFilesHidden);
		
		if ((addExistingFilesHidden != null) && (!(addExistingFilesHidden.equals("")))){
			
			
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			try {

				System.out.println("srt in side add existing" );
			
				String [] existingFiles = addExistingFilesHidden.split(",");
				for (int i=0; i<existingFiles.length; i++){
					String fileIdString =  existingFiles[i];
					int fileId = Integer.parseInt(fileIdString);
					
					RequirementUtil.addExistingFileToRequirement(requirementId, fileId, actorEmailId);
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		else {
			// else we have to get the file.
			// lets get the root folder (to store the attachments) and the max allowed file size
			String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
			int maxRequirementAttachmentSize = Integer.parseInt(this.getServletContext().getInitParameter("maxRequirementAttachmentSize"));
	
			
			// The goal here is to get the file, store it in a location,
			// remember the path to this location, put the input variables / file path
			// in either request / session memory and forward to another JSP for proper handling.
			int folderId = 0;
			int requirementId = 0;
			String title = "";
			String action = "";
			int attachmentId = 0;
	
		    Calendar cal = Calendar.getInstance();
			// if rootDataDirectory/TraceCloud does not exist, lets create it.
			File traceCloudRoot = new File (rootDataDirectory + File.separator + "TraceCloud");
			if (!(traceCloudRoot.exists() )){
			    new File(rootDataDirectory + File.separator + "TraceCloud").mkdir();
			}
			
	
			// if rootDataDirectory/TraceCloud/ProjectId does not exist, lets create it.
			File projectRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator  + project.getProjectId());
			if (!(projectRoot.exists() )){
			    new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator  + project.getProjectId()).mkdir();
			}
			
			// lets create a attachments folder in the project.
			File projectAttachmentRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator  + project.getProjectId() + File.separator +  "Attachment");
			if (!(projectAttachmentRoot.exists() )){
			    new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator  + project.getProjectId() + File.separator + "Attachment").mkdir();
			}
			
			// lets create a unique director within the ProjectRoot to store
			// the attachment.
			String dirName; 
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy-hhmm-ss");
			String today =  sdf.format(cal.getTime());
		    new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator  + project.getProjectId() + File.separator + "Attachment" + File.separator +  user.getUserId() + "-" + today ).mkdir();
		    
		    
		    dirName= rootDataDirectory + File.separator + "TraceCloud" + File.separator  + project.getProjectId() + File.separator + "Attachment" + File.separator +  user.getUserId() + "-" + today ;	    
		    File dir = new File (dirName);
		    
		    String attachmentFileName = "";
		    String attachmentFilePath = "";
			try {
				 	
				  MultipartParser mp = new MultipartParser(request, maxRequirementAttachmentSize); 
				  Part part;
				  while ((part = mp.readNextPart()) != null) {
					String name = part.getName();
					if (part.isParam()) {
					  // it's a parameter part
					  ParamPart paramPart = (ParamPart) part;
					  String value = paramPart.getStringValue();
					  if (name.equals("folderId")){
						  folderId = Integer.parseInt(value);
					  }
					  if (name.equals("requirementId")){
						  requirementId = Integer.parseInt(value);
					  }
					  if (name.equals("title")){
						  title = value;
					  }
					  
					  if (name.equals("actionToDo")){
						  action = value;
					  }
					  if (name.equals("attachmentId")){
						  attachmentId = Integer.parseInt(value);
					  }
					 
					}
					else if (part.isFile()) {
					  // it's a file part
						
					  FilePart filePart = (FilePart) part;
					  attachmentFileName = filePart.getFileName();
					  if (attachmentFileName != null) {
						// the part actually contained a file
						long size = filePart.writeTo(dir);
						attachmentFilePath = dirName + File.separator + attachmentFileName; 
						
					  }
					}
				  }
			}
			catch (IOException oEx) {
				// if the file is larger than maxRequirementAttachmentSize  
				//( we are telling the user that the max file size is maxRequirementAttachmentSize MB
				// we reject the file.
				
				// lets delete the folder.
				if (dir != null) {
					dir.delete();
				}
				// NOTE : IDEALLY WE WOULD LIKE TO TAKE THE USER TO THE REQUIREMENT HE IS COMING FROM
				// HOWEVER, DUE TO THE NATURE OF MULTI PART FILE UPLOAD, YOU CAN NOT BE ASSURED THAT
				// YOU HAVE ALL THE PARAMS IN HAND, BEFORE THE FILE SIZE EXCEPTION FIRES :(
				// HENCE WE CAN NOT RELIABLY TAKE THE USER TO THE REQUIREMENT HE STARTED OUT FROM
				// IN CASE OF A FILESIZE EXCEPTION.
				
				// NOTE : OUR DISPATCHER.FORWARD IS NOT WORKING. SO WE ARE TRYING REDIRECT.
				// forward to the yP.jsp page so that we display the correct page.
				// NOTE : since we didn't know how to handle multi part form data in AJAX / Javascript
				// submissions, we had to go through this work around.
				
				// we set a session variable for the user message.
				session.setAttribute("message", "You tried to upload file that is bigger than " + maxRequirementAttachmentSize / (1024 * 1024 )+  " MB. Please try again with a" +
					" smaller file");
				response.sendRedirect("/GloreeJava2/jsp/OpenProject/yP.jsp");
				return;
			}
			
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			Requirement requirement = new Requirement(requirementId, databaseType);
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////
			
			// lets create the attachment object.
			// if this is an 'add requirement attachment', lets add. If it's an update, then we will need to do something else. 
			System.out.println("srt action in AddRequiremetnAttachmentForm is " + action);
			if (action.equals("addRequirementAttachment")) {
				// Add
				RequirementUtil.addRequirementAttachment(requirementId, attachmentFileName, attachmentFilePath, title, 
					user.getEmailId(), databaseType);
				request.setAttribute("targetPage", "displayRequirementCoreAfterAttachment");
				request.setAttribute("folderId", Integer.toString(folderId));
				request.setAttribute("requirementId", Integer.toString(requirementId));
				// we use the session variable to display the success message in display req core
				session.setAttribute("attachRequirementStatus", "success");
			}
			if (action.equals("updateRequirementAttachment")) {
				// Add
				
				if (attachmentFileName == null){
					// do nothing.
					/*
					// we just need to update the title of this attachment.
					RequirementUtil.updateRequirementAttachmentTitle(attachmentId, requirementId, title, 
							user.getEmailId(), databaseType);
						request.setAttribute("targetPage", "displayRequirementCoreAfterAttachment");
						request.setAttribute("folderId", Integer.toString(folderId));
						request.setAttribute("requirementId", Integer.toString(requirementId));
						// we use the session variable to display the success message in display req core
						session.setAttribute("attachRequirementStatus", "success");
					*/
				}
				else {
					// we need to update the file
					RequirementUtil.updateRequirementAttachment(attachmentId, requirementId, attachmentFileName, attachmentFilePath, 
						user.getEmailId(), databaseType);
					request.setAttribute("targetPage", "displayRequirementCoreAfterAttachment");
					request.setAttribute("folderId", Integer.toString(folderId));
					request.setAttribute("requirementId", Integer.toString(requirementId));
					// we use the session variable to display the success message in display req core
					session.setAttribute("attachRequirementStatus", "success");
				}
			}
			
			// forward to the yP.jsp page so that we display the correct page.
			// NOTE : since we didn't know how to handle multi part form data in AJAX / Javascript
			// submissions, we had to go through this work around.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/OpenProject/yP.jsp");
			dispatcher.forward(request, response);
			return;
	
		}
	}
}
