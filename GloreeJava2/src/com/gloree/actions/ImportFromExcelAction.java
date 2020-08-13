package com.gloree.actions;

/////////////////////////////////////////////Purpose ///////////////////////////////////////////
//
//	This servlet is used to import an excel file to either create requiremetns / update reqs.
//
///////////////////////////////////////////Purpose ///////////////////////////////////////////

import com.gloree.beans.*;
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
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
//Excel POI stuff.
import org.apache.poi.hssf.usermodel.*;


public class ImportFromExcelAction extends HttpServlet {
    
	public ImportFromExcelAction() {
        super();
    }

    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost (request,response);
    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		int maxImportExcelFileSize = Integer.parseInt(this.getServletContext().getInitParameter("maxImportExcelFileSize"));

		// lets import the excel file and store it in a folder.

		// The goal here is to get the file, store it in a location,
		// remember the path to this location, put the input variables / file path
		// in either request / session memory and forward to another JSP for proper handling.
		int folderId = 0;
		String uploadAction = "";
		
		Calendar cal = Calendar.getInstance();	
		// if rootDataDirectory/TraceCloud does not exist, lets create it.
		File traceCloudRoot = new File (rootDataDirectory + "/TraceCloud");
		if (!(traceCloudRoot.exists() )){
		    new File(rootDataDirectory + File.separator + "TraceCloud").mkdir();
		}
		

		// if rootDataDirectory/TraceCloud/ProjectId does not exist, lets create it.
		File projectRoot  = new File (rootDataDirectory + "/TraceCloud/" + project.getProjectId());
		if (!(projectRoot.exists() )){
		    new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator + project.getProjectId()).mkdir();
		}

		// lets create a temp folder in the project.
		File projectTempRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator + project.getProjectId() + File.separator + "Temp");
		if (!(projectTempRoot.exists() )){
		    new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator + project.getProjectId() + File.separator + "Temp").mkdir();
		}
		
		

		// lets create a unique director within the ProjectRoot to store
		// the attachment.
		String dirName; 
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy-hhmm-ss");
		String today =  sdf.format(cal.getTime());
	    dirName= rootDataDirectory + File.separator +  "TraceCloud" + File.separator + project.getProjectId() +  File.separator + "Temp" + File.separator + user.getUserId() + "-" + today ;	    
	    new File(dirName).mkdir();
	    File dir = new File (dirName);
	    
	    String excelFilePath = "";
		try {
			 	
			  MultipartParser mp = new MultipartParser(request, maxImportExcelFileSize); // Max import file size
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
				  if (name.equals("uploadAction")){
					  uploadAction = value;
				  }
				 
				}
				else if (part.isFile()) {
				  // it's a file part
				  FilePart filePart = (FilePart) part;
				  String fileName = filePart.getFileName();
				  if (fileName != null) {
					// the part actually contained a file
					long size = filePart.writeTo(dir);
					
					excelFilePath = dirName + File.separator + fileName; 
					
				  }
				}
			  }
		}
		catch (IOException oEx) {
			// if the file is larger than maxImportExcelFileSize  
			//( we are telling the user that the max file size is maxImportExcelFileSize MB
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
				maxImportExcelFileSize / (1024 * 1024 )+  " MB. Please try again with a" +
				" smaller file");
			response.sendRedirect("/GloreeJava2/jsp/OpenProject/yP.jsp");
			return;
		}
		
		// now get the first row of this excel file, build an array list of values / cell locations.
		
		ArrayList columnNames = getExcelColumnNames(excelFilePath);
		
		session.setAttribute("columnNames", columnNames);
		session.setAttribute("excelFilePath", excelFilePath);
		request.setAttribute("folderId", Integer.toString(folderId));
		request.setAttribute("uploadAction", uploadAction);

		// lets set the request attribute so that we can put focus on the right jsp page.
		if (uploadAction.equals("createNewRequirements")) {
			// we are trying import from Excel to create New requirements
			request.setAttribute("targetPage", "createNewRequirementsFromExcelMapForm");
		}
		else {
			// we are trying to import from Excel to Update Existing requirements.
			request.setAttribute("targetPage", "updateExistingRequirementsFromExcelMapForm");			
		}
		// forward to the yP.jsp page so that we display the correct page.
		// NOTE : since we didn't know how to handle multi part form data in AJAX / Javascript
		// submissions, we had to go through this work around.
		RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/OpenProject/yP.jsp");
		dispatcher.forward(request, response);
		return;

	}

	private ArrayList getExcelColumnNames(String excelFilePath){
		ArrayList columnNames = new ArrayList();

		try {
			InputStream myxls = new FileInputStream(excelFilePath);
			HSSFWorkbook wb     = new HSSFWorkbook(myxls);
			
			HSSFSheet sheet = wb.getSheetAt(0);       // first sheet
			HSSFRow row     = sheet.getRow(0);        // first row
			
			// we look for up to 100 cells to see if there a value
			for (int i=0; i<100 ; i++) {

				// get Column Heading.
				String columnHeading = "";
				try{
					HSSFCell cell   = row.getCell(i);  	
					columnHeading = cell.getStringCellValue();
					
				}
				catch (Exception e) {
					// if we run into any exception, set columnHeading to empty string.
					columnHeading = "";
				}
				
				if (!columnHeading.equals("")) {
					// if this is not an empty cell
					columnNames.add(columnHeading + ":##:" + i);

					
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return columnNames;
	}
	
}
