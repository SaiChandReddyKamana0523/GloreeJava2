package com.gloree.actions;

/////////////////////////////////////////////Purpose ///////////////////////////////////////////
//
//	This servlet is used to import an excel file to either create requiremetns / update reqs.
//
///////////////////////////////////////////Purpose ///////////////////////////////////////////

import com.gloree.beans.*;
import com.gloree.utils.SecurityUtil;
import com.gloree.utils.WordTemplateUtil;


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


public class EditWordTemplateAction extends HttpServlet {
    
	public EditWordTemplateAction() {
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
		///////////////////////////////SECURITY//////////////////////////////

		// lets get the root folder (to store the attachments) and the max allowed file size
		String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
		int maxWordTemplateSize = Integer.parseInt(this.getServletContext().getInitParameter("maxWordTemplateSize"));

		
		// NOTE : Since this is a multi part form input, we can not get the attributes
		// the standard way :(
		// lets import the excel file and store it in a folder.

		// The goal here is to get the file, store it in a location,
		// remember the path to this location, put the input variables / file path
		// in either request / session memory and forward to another JSP for proper handling.
		int folderId = 0;
		int templateId = 0;
		
		
	    Calendar cal = Calendar.getInstance();
		// if rootDataDirectory/TraceCloud does not exist, lets create it.
		File traceCloudRoot = new File (rootDataDirectory + File.separator + "TraceCloud");
		if (!(traceCloudRoot.exists() )){
		    new File(rootDataDirectory + File.separator +  "TraceCloud").mkdir();
		}
		

		// if rootDataDirectory/TraceCloud/ProjectId does not exist, lets create it.
		File projectRoot  = new File (rootDataDirectory + File.separator +  "TraceCloud" + File.separator + project.getProjectId());
		if (!(projectRoot.exists() )){
		    new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator +  project.getProjectId()).mkdir();
		}

		// lets create a template folder in the project.
		File projectTemplateRoot  = new File (rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  project.getProjectId() +File.separator +  "Template");
		if (!(projectTemplateRoot.exists() )){
		    new File(rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  project.getProjectId() +File.separator +  "Template").mkdir();
		}
		
		
		
		// lets create a unique director within the ProjectRoot to store
		// the attachment.
		String dirName; 
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy-hhmm-ss");
		String today =  sdf.format(cal.getTime());
	    dirName= rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  project.getProjectId() + File.separator +  "Template" + File.separator +  user.getUserId() + "-" + today ;	    
	    new File(dirName).mkdir();
	    File dir = new File (dirName);
	    
	    int tDCSDocumentId = 0;
	    String templateVisibility = "";
	    String templateFilePath = "";
	    String templateName = "";
	    String templateDescription = "";
	    

		try {
			 	
			  MultipartParser mp = new MultipartParser(request, maxWordTemplateSize); // 3 MB
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
						if (name.equals("templateId")){
							templateId = Integer.parseInt(value);
						}
						if (name.equals("templateName")){
							templateName = value;
						}
						if (name.equals("templateVisibility")){
							templateVisibility = value;
						}
						if (name.equals("templateDescription")){
							templateDescription = value;
						}
				  }
				  else if (part.isFile()) {
					  // it's a file part
					  FilePart filePart = (FilePart) part;
					  String fileName = filePart.getFileName();
					  if (fileName != null) {
							// the part actually contained a file
							long size = filePart.writeTo(dir);		
							templateFilePath = dirName + File.separator +  fileName; 	
					  }
				  }
				
			  }
		}
		catch (IOException oEx) {
			// if the file is larger than maxWordTemplateSize  
			//( we are telling the user that the max file size is maxWordTemplateSize MB
			// we reject the file.
			
			// lets delete the folder.
			if (dir != null) {
				dir.delete();
			}
			// NOTE : IDEALLY WE WOULD LIKE TO TAKE THE USER TO THE FOLDER HE IS COMING FROM
			// HOWEVER, DUE TO THE NATURE OF MULTI PART FILE UPLOAD, YOU CAN NOT BE ASSURED THAT
			// YOU HAVE ALL THE PARAMS IN HAND, BEFORE THE FILE SIZE EXCEPTION FIRES :(
			// HENCE WE CAN NOT RELIABLY TAKE THE USER TO THE FOLDER HE STARTED OUT FROM
			// IN CASE OF A FILESIZE EXCEPTION.
			
			// NOTE : OUR DISPATCHER.FORWARD IS NOT WORKING. SO WE ARE TRYING REDIRECT.
			// forward to the yP.jsp page so that we display the correct page.
			// NOTE : since we didn't know how to handle multi part form data in AJAX / Javascript
			// submissions, we had to go through this work around.
			
			// we set a session variable for the user message.
			session.setAttribute("message", "You tried to upload file that is bigger than " + 
					maxWordTemplateSize / (1024 * 1024 )+  " MB. Please try again with a" +
				" smaller file");
			response.sendRedirect("/GloreeJava2/jsp/OpenProject/yP.jsp");
			return;
		}
		catch (Exception e ) {
			e.printStackTrace();
		}
		
		
		// At this point , we have to do a couple of things.

		////////////////////////////////////////SECURITY//////////////////////////
		//
		// We check to make sure that the word template sent in as a  parameter
		// belongs to the same project that the user has logged into.
		//
		////////////////////////////////////////SECURITY//////////////////////////
		WordTemplate wordTemplate = new WordTemplate(templateId, databaseType);
		if (wordTemplate.getProjectId() != project.getProjectId()) {
			return;
		}	

		// lets update the name, description and visibility etc...
		WordTemplateUtil.updateNameAndDescription(templateId,templateName,
				templateVisibility,templateDescription, user, databaseType);
		
		// now if the user has decided to replace the underlying template file with a new file,
		// then we need to remove the old file, and update the db path to the new location.
		
		// lets see if the user has uploaded a new file. 
		File newWordTemplateFile = new File(templateFilePath);
		if ((!(templateFilePath.equals(""))) && (newWordTemplateFile.length() > 0)){
			// this means the user has uploaded a file and we need to replace the template object's file path with this one.
			// lets locate and remove the old file.
			File oldWordTemplateFile = new File(wordTemplate.getTemplateFilePath());
			if (oldWordTemplateFile != null){
				File oldWordTemplateFileDirectory = oldWordTemplateFile.getParentFile();
				// lets drop the file.
				oldWordTemplateFile.delete();
				
				if (oldWordTemplateFileDirectory != null) {
					oldWordTemplateFileDirectory.delete();
				}
			}
			
			// Now that we have removed the old file, lets update the file path in the db for this template object.
			if (!(templateFilePath.equals(""))){
				// an input file was given.
				WordTemplateUtil.updateFilePath(templateId,templateFilePath, user, databaseType);
			}
		}
		
		
		
		
		
		
		
		// lets set the request attribute so that we can put focus on the right jsp page.
			// we are trying import from Excel to create New requirements
		request.setAttribute("wordTemplate", wordTemplate);
		request.setAttribute("folderId", Integer.toString((folderId)));
		request.setAttribute("targetPage", "displayWordTemplate");
		// forward to the yP.jsp page so that we display the correct page.
		// NOTE : since we didn't know how to handle multi part form data in AJAX / Javascript
		// submissions, we had to go through this work around.
		RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/OpenProject/yP.jsp");
		dispatcher.forward(request, response);
		return;

		


	}
}
