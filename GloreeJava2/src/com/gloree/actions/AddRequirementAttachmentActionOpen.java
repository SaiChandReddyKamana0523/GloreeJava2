package com.gloree.actions;

/////////////////////////////////////////////Purpose ///////////////////////////////////////////
//
//	This servlet is used to import an excel file to either create requiremetns / update reqs.
//
///////////////////////////////////////////Purpose ///////////////////////////////////////////

import com.gloree.beans.*;
import com.gloree.utils.ProjectUtil;
import com.gloree.utils.RequirementUtil;
import com.gloree.utils.SecurityUtil;








import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

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


public class AddRequirementAttachmentActionOpen extends HttpServlet {
    
	public AddRequirementAttachmentActionOpen() {
        super();
    }

    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost (request,response);
    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String databaseType = this.getServletContext().getInitParameter("databaseType");
		request.setCharacterEncoding("UTF-8");
		

		///////////////////////////////SECURITY//////////////////////////////
		// Security  Note:
		// This file is open to non authenticated users
		// since we are letting non authenticated users create reqs
		// we put the following restrictions
		// 1. we make sure this req was created in the last 24 hours
		// 2. we also make sure that this req belongs to a folder with at least 1 webform
		// we create the attachement in the name of the req owner.
		// we add a log entry
		///////////////////////////////SECURITY//////////////////////////////
		
		
		// because we get the reqId as multipart data, 
		// we are forced to do the following
		// store the file in a temp location
		// get the reqId, figure out the project and then move the file to the corect location.
		
		
		
			// else we have to get the file.
			// lets get the root folder (to store the attachments) and the max allowed file size
			String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
			int maxRequirementAttachmentSize = Integer.parseInt(this.getServletContext().getInitParameter("maxRequirementAttachmentSize"));
	
			
			// The goal here is to get the file, store it in a location,
			// remember the path to this location, put the input variables / file path
			// in either request / session memory and forward to another JSP for proper handling.
			int folderId = 0;
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
			File tempRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator  + "Temp");
			if (!(tempRoot.exists() )){
			    new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator  + "Temp").mkdir();
			}
			
			String fileCode = UUID.randomUUID().toString();
			
			File tempDir  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator  + "Temp" + File.separator + fileCode);
			if (!(tempDir.exists() )){
			    new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator  + "Temp" + File.separator + fileCode).mkdir();
			}
			
			
		    int requirementId = 0 ;
		    String attachmentFileName = "";
		    String tempFilePath = "";
		    int webFormId = 0;
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
					  if (name.equals("webFormId")){
						  webFormId = Integer.parseInt(value);
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
						long size = filePart.writeTo(tempDir);
						tempFilePath = tempDir + File.separator + attachmentFileName; 
						
					  }
					}
				  }
			}
			catch (IOException oEx) {
				// if the file is larger than maxRequirementAttachmentSize  
				//( we are telling the user that the max file size is maxRequirementAttachmentSize MB
				// we reject the file.
				
				// lets delete the folder.
				if (tempDir != null) {
					tempDir.delete();
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
				String message = "You tried to upload file that is bigger than " + maxRequirementAttachmentSize / (1024 * 1024 )+  " MB. Please try again with a" +
						" smaller file";
				PrintWriter out = response.getWriter();
			    out.println("<h1>" + message + "</h1>");
			      
				
				return;
			}
			
			Requirement requirement = new Requirement(requirementId, "mySQL");
				
			// we process this file, only if this file was created today.
			if (requirement.getDaysSinceCreated() > 1 ){
				// as a security measure, we only allow files to be attached to requirements that are at most 1 day old
				return;
			}
			
			try{
			
				// if the user is trying at attach an existing file, then it's much simpler
				User user = new User(requirement.getRequirementOwner(), "mySQL");
				Project project = new Project(requirement.getProjectId(), "mySQL");
				
				System.out.println("tempFilePath is " + tempFilePath);
				
	
				
				// now that we have the req id, lets get the owner, project Id and create the final filePath and move the file there
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
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy-hhmm-ss");
				String today =  sdf.format(cal.getTime());
			    new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator  + project.getProjectId() + File.separator + "Attachment" + File.separator +  user.getUserId() + "-" + today ).mkdir();
			    
			    
			    String targetPath = rootDataDirectory + File.separator + "TraceCloud" + File.separator  + project.getProjectId() + File.separator + "Attachment" + File.separator +  user.getUserId() + "-" + today + File.separator + attachmentFileName ;	    
			    File target = new File (targetPath);
			    
			    
			    
			    System.out.println("trying to copy from " + tempFilePath + "    to " + target.toPath());
			    // I need to move the attachmentFileName (file) from attachmentFilePath (location) to the new locatino which is (dir)
			    File source = new File (tempFilePath);
			    Files.move(source.toPath(), target.toPath());
			    
			    
			    
				// lets create the attachment object.
				// if this is an 'add requirement attachment', lets add. If it's an update, then we will need to do something else. 
				// Add
				RequirementUtil.addRequirementAttachment(requirementId, attachmentFileName, targetPath, title, 
					user.getEmailId(), databaseType);
				request.setAttribute("targetPage", "displayRequirementCoreAfterAttachment");
				request.setAttribute("folderId", Integer.toString(folderId));
				request.setAttribute("requirementId", Integer.toString(requirementId));
				
			}
			catch (Exception e){
				e.printStackTrace();
			}
			
			
			response.sendRedirect("/GloreeJava2/jsp/Requirement/createWebFormRequirementConfirm.jsp?requirementId=" + requirementId + "&webFormId=" + webFormId);
			return;
			
		
	}
}
