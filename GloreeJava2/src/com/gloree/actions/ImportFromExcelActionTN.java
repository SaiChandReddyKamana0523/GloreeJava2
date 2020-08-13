package com.gloree.actions;

/////////////////////////////////////////////Purpose ///////////////////////////////////////////
//
//	This servlet is used to import an excel file to either create requiremetns / update reqs.
//
///////////////////////////////////////////Purpose ///////////////////////////////////////////

import com.gloree.beans.*;
import com.gloree.utils.TNUtil;



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


public class ImportFromExcelActionTN extends HttpServlet {
    
	public ImportFromExcelActionTN() {
        super();
    }

    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost (request,response);
    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		///////////////////////////////SECURITY//////////////////////////////
		// Security  Note:
		//  THIS IS TO BE USED BY TRACENOW USERS. SO NO SECURITY
		// WE JUST SAVE THE FILE AND GIVE THEM A TOCKEN
		///////////////////////////////SECURITY//////////////////////////////
		
		///////////////////////////////SECURITY//////////////////////////////

		
		String message = "";
		HttpSession session = request.getSession(true);
		/*
		String fileCode = session.getId().substring(0,10);
		
		Random randomGenerator = new Random();
	    int randomInt = randomGenerator.nextInt(10000);
	    fileCode += Integer.toString(randomInt);
		*/
		String fileCode = UUID.randomUUID().toString();
		
		System.out.println("srt file code is " + fileCode);
		// lets get the root folder (to store the attachments) and the max allowed file size
		String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
		int maxImportExcelFileSize = Integer.parseInt(this.getServletContext().getInitParameter("maxImportExcelFileSize"));

		// lets import the excel file and store it in a folder.

		// The goal here is to get the file, store it in a location,
		// remember the path to this location, get a unique key, and store the key and path 
		// in the database and return the key to the user. 
		// Later on when the user shows up at TraceNow to load the file, we ask for the key.
		
		Calendar cal = Calendar.getInstance();	
		// if rootDataDirectory/TraceCloud does not exist, lets create it.
		File traceCloudRoot = new File (rootDataDirectory + "/TraceCloud");
		if (!(traceCloudRoot.exists() )){
		    new File(rootDataDirectory + File.separator + "TraceCloud").mkdir();
		}
		
		
		File traceNowRoot  = new File (rootDataDirectory + "/TraceCloud/TraceNow") ;
		if (!(traceNowRoot.exists() )){
		    new File(rootDataDirectory + File.separator + "TraceCloud/TraceNow").mkdir();
		}

		// if rootDataDirectory/TraceCloud/TraceNow/Excel does not exist, lets create it.
		File excelRoot  = new File (rootDataDirectory + "/TraceCloud/TraceNow/Excel") ;
		if (!(excelRoot.exists() )){
		    new File(rootDataDirectory + File.separator + "TraceCloud/TraceNow/Excel").mkdir();
		}

		
		File fileCodeRoot  = new File (rootDataDirectory + "/TraceCloud/TraceNow/Excel/" + fileCode) ;
		if (!(fileCodeRoot.exists() )){
		    new File(rootDataDirectory + File.separator + "TraceCloud/TraceNow/Excel/" + fileCode).mkdir();
		}

		
		
		

		// lets create a unique director within the ProjectRoot to store
		// the attachment.
		String dirName; 
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy-hhmm-ss");
		String today =  sdf.format(cal.getTime());
	    dirName= rootDataDirectory + File.separator +  "TraceCloud/TraceNow/Excel/" + fileCode ;
	    	    
	   
	    File dir = new File (dirName);
	    String fileName = "";

	    
	    String filePath = "";
		try {
			 	
			  MultipartParser mp = new MultipartParser(request, maxImportExcelFileSize); // Max import file size
			  Part part;
			  while ((part = mp.readNextPart()) != null) {
				String name = part.getName();
				if (part.isParam()) {
				  // it's a parameter part
				  ParamPart paramPart = (ParamPart) part;
				  String value = paramPart.getStringValue();
				  /*
				  if (name.equals("folderId")){
					  folderId = Integer.parseInt(value);
				  }
				  if (name.equals("uploadAction")){
					  uploadAction = value;
				  }
				  */
				 
				}
				else if (part.isFile()) {
				  // it's a file part
				  FilePart filePart = (FilePart) part;
				  fileName = filePart.getFileName();
				  System.out.println("srt fileName is " + fileName);
				  if (fileName != null) {
					// the part actually contained a file
					long size = filePart.writeTo(dir);
					
					filePath = dirName + File.separator + fileName; 

					
				  }
				}
			  }
		}
		catch (Exception e) {
			e.printStackTrace();
			// if the file is larger than maxImportExcelFileSize  
			//( we are telling the user that the max file size is maxImportExcelFileSize MB
			// we reject the file.
			
			// lets delete the folder.
			if (dir != null) {
				dir.delete();
			}
			
			// we set a session variable for the user message.
			message =  "<div class='alert alert-danger'>You tried to upload file that is bigger than " + 
			maxImportExcelFileSize / (1024 * 1024 )+  " MB. Please try again with a" +
			" smaller file </div>";

			request.setAttribute("message", message);
			
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/TraceNow/importFromExcelConfirmation.jsp?");
			dispatcher.forward(request, response);
			return;
			
		}
		
		
		// Store the fileCode and filePath in database.
		TNUtil.storeTNFile(fileCode, filePath);
		
		
		if (!(fileName.endsWith(".xls"))){
			message =  "<div class='alert alert-danger'> Please upload ONLY .xls files. You can open any Excel file and save it as (1997-2004) format. It will save as .xls </div>";

			request.setAttribute("message", message);
			
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/TraceNow/importFromExcelConfirmation.jsp?");
			dispatcher.forward(request, response);
			return;
		}
		
		
		request.setAttribute("fileCode", fileCode);
		
		RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/TraceNow/importFromExcelConfirmation.jsp");
		dispatcher.forward(request, response);
		return;

	}


	
}
