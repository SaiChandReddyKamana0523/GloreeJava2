package com.gloree.actions;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



import com.gloree.utils.SecurityUtil;
import com.gloree.utils.TDCSUtil;
import com.gloree.utils.WordTemplateUtil;
import com.gloree.utils.WordTemplateUtilSN;
import com.gloree.beans.*;


public class WordTemplateAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public WordTemplateAction() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
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

		User user = securityProfile.getUser();
		
		int maxColumnsOfExcelToEmbedInWord = 0;
		try {
			maxColumnsOfExcelToEmbedInWord = Integer.parseInt(this.getServletContext().getInitParameter("maxColumnsOfExcelToEmbedInWord"));
		}
		catch (Exception e){
			maxColumnsOfExcelToEmbedInWord = 0;
		}
		
		// if the action is related to SNWordTemplates, then there is no TC project involved. 
		String action = request.getParameter("action");
		System.out.println("srt in wordTemplateAction action is  " + action);
		
		
		if (action.equals("generateReportSN")) {

			int templateId = Integer.parseInt(request.getParameter("templateId"));
			WordTemplateSN wordTemplateSN = new WordTemplateSN(templateId);
			
			String[] displayAttributesArray = request.getParameterValues("displayAttributes");
			String displayAttributes = "";
			if (displayAttributesArray != null) {
				for (int i=0; i<displayAttributesArray.length; i++) {
					displayAttributes +=  displayAttributesArray[i] + ",";
				}
			}
			

			String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
			
			// The users wants to see a separate table for each requirement.
			System.out.println("srt displayAttributes are " + displayAttributes);
			String filename = WordTemplateUtilSN.generateReqPerTableReportSN(session, securityProfile, templateId,
			displayAttributes, user, request, response, rootDataDirectory, "HTML",  databaseType, maxColumnsOfExcelToEmbedInWord);
			
			return;
		}
		if (action.equals("generateReportSNExcel")) {
		
			String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
			
			// The users wants to see a separate table for each requirement.
			String filename = WordTemplateUtilSN.generateReqPerTableReportSNExcel(session, securityProfile,
					user, request, response, rootDataDirectory, "HTML",  databaseType, maxColumnsOfExcelToEmbedInWord);
			
			return;
		}
		if (action.equals("generateSNExcel")) {

			String reportType = request.getParameter("reportType");
			String sourceName = request.getParameter("sourceName");
			System.out.println("srt in generateSNExcel reportType are " + reportType);
			System.out.println("srt in generateSNExcel sourceName are " + sourceName);

			

			String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
			
			// The users wants to see a separate table for each requirement.
			WordTemplateUtilSN.generateSNExcel(session, securityProfile, reportType, sourceName, 
				user, request, response, rootDataDirectory, "HTML",  databaseType);
			
			return;
		}
		
		
		if (action.equals("downloadTemplateSN")) {
			int sNTemplateId = Integer.parseInt(request.getParameter("sNTemplateId"));
			WordTemplateSN wordTemplateSN = new WordTemplateSN(sNTemplateId); 
			
			
			
			String filePath = wordTemplateSN.getTemplateFilePath();
			// we get the fileName by looking at the filePath's content after the \
			String fileName = (String) filePath.substring(filePath.lastIndexOf("\\")+1);
			
			fileName.replace(" ", "_");
			fileName.replace("-", "_");

			FileInputStream inputStream= new FileInputStream(filePath);
			
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setContentType("application/msword");
    		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
    		
	        ServletOutputStream out = response.getOutputStream();
	        byte buf[]=new byte[1024];
	        int len;
	        while((len=inputStream.read(buf))>0)
	        	out.write(buf,0,len);
	        
	        out.close();
	        inputStream.close();
			
			return;
		}
		
		
		
		
		
		/////////////////// AFTER THIS THE PROJECT NEEDS TO BE SELECTED ///////////////
		Project project = (Project) session.getAttribute("project");
		

		
		if (!(securityProfile.getRoles().contains("MemberInProject" + project.getProjectId()))){
			//User is NOT a member of this project. so do nothing and return.
			return;
		}
		
		///////////////////////////////SECURITY//////////////////////////////

		/*
		 * Lets do the Aspose licensing here. Note that the Aspose.Words.lic file
		 * has to be in the same location as Aspose.Words.jdk16 jar file
		 * 
		 */
		com.aspose.words.License license = new com.aspose.words.License();
		try {
			license.setLicense("Aspose.Words.lic");
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		
		if (action.equals("generateReport")) {

			int templateId = Integer.parseInt(request.getParameter("templateId"));
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			String requirementOutputFormat = request.getParameter("requirementOutputFormat");
			String formatBoldAttribute = request.getParameter("formatBoldAttribute");
			if (formatBoldAttribute==null){formatBoldAttribute="";}
			


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
			

			String[] displayAttributesArray = request.getParameterValues("displayAttributes");
			String displayAttributes = "";
			if (displayAttributesArray != null) {
				for (int i=0; i<displayAttributesArray.length; i++) {
					displayAttributes +=  displayAttributesArray[i] + ",";
				}
			}
			

			String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
			
			if (requirementOutputFormat.equals("reqPerTable")){

				
				// The users wants to see a separate table for each requirement.
				String filename = WordTemplateUtil.generateReqPerTableReport(securityProfile, templateId, project ,
				displayAttributes, user, request, response, rootDataDirectory, "HTML",  databaseType, maxColumnsOfExcelToEmbedInWord);
			}
			if (requirementOutputFormat.equals("reqPerTableRow")){

				// The users wants to see a separate table row for each requirement.
				String filename = WordTemplateUtil.generateReqPerTableRowReport(securityProfile, templateId, project ,
				displayAttributes, user, request, response, rootDataDirectory, "HTML",  databaseType);
			}
			return;
		}

		
		
		
		if (action.equals("generateReqTemplateReport")) {
			// shows the user the list of available documents to generate report from. 


			System.out.println("TM : in generate Req template report ");
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			Requirement r = new Requirement(requirementId, databaseType);
			String divId = request.getParameter("divId");
			
			
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// We check to make sure that the word template sent in as a  parameter
			// belongs to the same project that the user has logged into.
			//
			////////////////////////////////////////SECURITY//////////////////////////
			if (r.getProjectId() != project.getProjectId()) {
				return;
			}

		
			// lets see if we can find a RequirementTemplate.docx file 
			ArrayList <WordTemplate> wordTemplates = WordTemplateUtil.getDefaultWordTemplates(requirementId);
			if (wordTemplates.size() ==  0){
				// print error message
				PrintWriter out = response.getWriter();
			    out.println("<div class='alert alert-danger'><span class='normalText'> You need to have a file with <font color='red'>RequirementTemplate.docx</font>  in it's name"
			    		+ " in the same folder as this Requirement."
			    		+ " Please create this document and then try again. Here is a Sample File you can use."
			    		+ " <a href='/GloreeJava2/documentation/help/WordTemplate/sampleRequirementTemplate.docx'> Download </a> <br> "
			    		+ " <a href='/GloreeJava2/documentation/help/WordTemplate/reqWordTemplateReport.pdf'>How to Create A  Word Template Report </a> <br><br> " +
			    		" &nbsp;&nbsp;<a href='#' onClick='document.getElementById(\"" + divId+   "\").style.display=\"none\"'>Close</a> </span></div>");
			    out.close();
			}
			else {
				PrintWriter out = response.getWriter();
				out.println("<div class='alert alert-success'> ");
				out.println(" <div style='float:right'>" +
			    		" <a href='#' class='btn btn-xs btn-danger' style='color:white' onClick='document.getElementById(\"" 
			    		+ divId+   "\").style.display=\"none\"'>Close</a> </div>");
				out.println(" We found the following word templates. Please select one. .<br><br>  ");
				
				out.println("<div class='alert alert-info'>Reports in this Folder </div>");
				for (WordTemplate wT : wordTemplates){
					if (wT.getFolderId() == r.getFolderId()){
					out.println(" <a href='/GloreeJava2/servlet/WordTemplateAction?action=downloadGeneratedReqReport&requirementId=" 
							+ requirementId + "&templateId="  + wT.getTemplateId() + "' target='_blank'> "+ wT.getTemplateName() + " </a>  <br> ") ;
					}
				}
					
				out.println("<hr>");
				out.println("<div class='alert alert-info'>Reports in Other Folders </div>");
				for (WordTemplate wT : wordTemplates){
					if (wT.getFolderId() != r.getFolderId()){
					out.println("<a href='/GloreeJava2/servlet/WordTemplateAction?action=downloadGeneratedReqReport&requirementId=" 
							+ requirementId + "&templateId="  + wT.getTemplateId() + "' target='_blank'> "+ wT.getTemplateName() + " </a>  <br> ") ;
					}
				}
				out.println("<br><br></div>");
			   
			    out.close();
			}
			

			System.out.println("TM  completed generateretemplatereport");
			return;
		}
		if (action.equals("downloadGeneratedReqReport")) {


			System.out.println("TM : in downloadReqTEmplateReport ");
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			int wordTemplateId = Integer.parseInt(request.getParameter("templateId"));
			Requirement r = new Requirement(requirementId, databaseType);

			////////////////////////////////////////SECURITY//////////////////////////
			//
			// We check to make sure that the word template sent in as a  parameter
			// belongs to the same project that the user has logged into.
			//
			////////////////////////////////////////SECURITY//////////////////////////
			if (r.getProjectId() != project.getProjectId()) {
				return;
			}

			String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
		
			
			// generate word template and export it out.


			System.out.println("TM : About to make the call to wordUtil.generate ReqTemplate");
			String filePath = WordTemplateUtil.generateReqTemplateReport(securityProfile, r, 
			wordTemplateId, project ,
			user, request, response, rootDataDirectory,   
			databaseType, maxColumnsOfExcelToEmbedInWord);
			
			System.out.println("TM : completed call  to wordUtil.generate ReqTemplate: filePath is " + filePath);
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
			Calendar cal = Calendar.getInstance();
			String today = sdf.format(cal.getTime());
			String fileName = r.getRequirementFullTag()  + " Word Template Report " + today + ".docx";
			fileName.replace(' ', '_');

			
			// lets export the file out
			FileInputStream inputStream= new FileInputStream(filePath);
			
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setContentType("application/msword");
    		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
    		
	        ServletOutputStream out = response.getOutputStream();
	        byte buf[]=new byte[1024];
	        int len;
	        while((len=inputStream.read(buf))>0)
	        	out.write(buf,0,len);
	        
	        out.close();
	        inputStream.close();
				

			System.out.println("TM  : Completed download ReqTemplate report ");
			return;
		}

		if (action.equals("pushReportToTDCS")) {

			int templateId = Integer.parseInt(request.getParameter("templateId"));
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			String requirementOutputFormat = request.getParameter("requirementOutputFormat");
			
			TDCSDocument tDCSDocument = null;
			String tDCSAction = request.getParameter("tDCSAction");
			String description = request.getParameter("description");
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
			

			String title = "";
			if (tDCSAction.equals("new")){
				title = request.getParameter("title");
				// if the action is to create a new document, then the user has to have
				// write permissions on this folder
				if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" 
						+ folderId))){
					PrintWriter out = response.getWriter();
				    out.println("<div class='alert alert-success'><span class='normalText'> You do not have permissions to save TDCS documents in this Folder" +
				    		" &nbsp;&nbsp;<a href='#' onClick='document.getElementById(\"emailAttachmentDiv\").style.display=\"none\"'>Close</a> </span></div>");
				    out.close();
					return;
				}
			}
			if (tDCSAction.equals("existing")){
				

				title = request.getParameter("title");
				String tDCSDocumentFullTag = request.getParameter("tDCSDocumentFullTag");
				tDCSDocument = new TDCSDocument(tDCSDocumentFullTag, project.getProjectId(), databaseType);
				
				
				
				if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" + tDCSDocument.getFolderId() ))){
					PrintWriter out = response.getWriter();
				    out.println("<div class='alert alert-success'><span class='normalText'> You do not have Create permissions in the Folder where the Document resides. Please work with your administrator to " +
							" get Create Permissions or choose another document Id." +
				    		" &nbsp;&nbsp;<a href='#' onClick='document.getElementById(\"emailAttachmentDiv\").style.display=\"none\"'>Close</a> </span></div>");
				    out.close();
				    return;
				}
				else if (tDCSDocument.getCurrentVersionDocumentStatus().equals("locked")){
					// this is a locked document. lets get the user to get the document unlocked.
					if (tDCSDocument.getCurrentVersionDocumentStatusBy().equals(securityProfile.getUser().getEmailId() )){ 
						// this means that the lock was put in place by this user.		
						PrintWriter out = response.getWriter();
					    out.println("<div class='alert alert-success'><span class='normalText'>This document has been Locked by you on " +
					    	tDCSDocument.getCurrentVersionLastModifiedDt() + 
							" Please Unlock it at <a href='#' onClick='displayTDCSHome()'> " + 
							" <img src='/GloreeJava2/images/database_refresh16.png'> TDCS</a> "  + 
							" prior to adding a new version to it. </span></div>");
					    out.close();
					    return;
					} 
					else {
						PrintWriter out = response.getWriter();
					    out.println("<div class='alert alert-success'><span class='normalText'>This document has been Locked by " +
					    	tDCSDocument.getCurrentVersionDocumentStatusBy() + " on " +
					    	tDCSDocument.getCurrentVersionLastModifiedDt() + 
							"  Please work with this person or the project administrators to " + 
							" Unlock it at <a href='#' onClick='displayTDCSHome()'> " + 
							" <img src='/GloreeJava2/images/database_refresh16.png'> TDCS</a> "  + 
							" prior to adding a new version to it. </span></div>");
					    out.close();
					    return;
					}
				}


			}


			String[] displayAttributesArray = request.getParameterValues("displayAttributes");
			String displayAttributes = "";
			if (displayAttributesArray != null) {
				for (int i=0; i<displayAttributesArray.length; i++) {
					displayAttributes +=  displayAttributesArray[i] + ",";
				}
			}
			
			String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
			
			String filepath = "";
			if (requirementOutputFormat.equals("reqPerTable")){

				System.out.println("TM : inside wordTemplateAction Report  - about to call method to generate word doc " );
				// The users wants to see a separate table for each requirement.
				filepath = WordTemplateUtil.generateReqPerTableReport(securityProfile, templateId, project ,
						displayAttributes, user, request, response,rootDataDirectory, "file",  databaseType, maxColumnsOfExcelToEmbedInWord);
				

				System.out.println("TM : inside wordtemplateAction Report  - filepath is  "  + filepath);
			}
			if (requirementOutputFormat.equals("reqPerTableRow")){
				// The users wants to see a separate table row for each requirement.
				filepath = WordTemplateUtil.generateReqPerTableRowReport(securityProfile, templateId, project ,
						displayAttributes, user, request, response,rootDataDirectory, "file",  databaseType);
			}
			
			
			// lets move this file to a permanent location.
			

			System.out.println("TM : inside wordtemplateAction Report  - trying to store to traceCloudRoot "   + rootDataDirectory + File.separator + "TraceCloud" );
			// first let make sure that the E://TraceCloud/ProjectId/TDCS/unique directory exists.
			Calendar cal = Calendar.getInstance();
			// if rootDataDirectory/TraceCloud does not exist, lets create it.
			File traceCloudRoot = new File (rootDataDirectory + File.separator + "TraceCloud");
			if (!(traceCloudRoot.exists() )){
			    new File(rootDataDirectory + File.separator +  "TraceCloud").mkdir();
			}
			
			System.out.println("TM : inside wordtemplateAction Report  - completed folder creation traceCloudRoot "   + rootDataDirectory + File.separator + "TraceCloud" );

			// if rootDataDirectory/TraceCloud/ProjectId does not exist, lets create it.
			File projectRoot  = new File (rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  project.getProjectId());
			if (!(projectRoot.exists() )){
			    new File(rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  project.getProjectId()).mkdir();
			}
			
			// lets create a TDCS folder in the project.
			File projectAttachmentRoot  = new File (rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  project.getProjectId() + File.separator +  "TDCS");
			if (!(projectAttachmentRoot.exists() )){
			    new File(rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  project.getProjectId() + File.separator +  "TDCS").mkdir();
			}
			
			System.out.println("TM : inside wordtemplateAction Report  - completed trying to create folder 2 "   + rootDataDirectory + File.separator + "TraceCloud" );
			
			
			System.out.println("TM : inside wordtemplateAction Report  - trying to save file  "   + rootDataDirectory + File.separator + "TraceCloud" );
			
			// lets create a unique directory within the ProjectRoot to store
			// the file.
			String targetDirName; 
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy-hhmm-ss");
			String today =  sdf.format(cal.getTime());
		    targetDirName= rootDataDirectory + File.separator +  "TraceCloud" 
			+ File.separator + project.getProjectId() +  File.separator 
			+  "TDCS" + File.separator + user.getUserId() + "-" + today ;	    
		    new File(targetDirName).mkdir();

		    

			System.out.println("TM : inside wordtemplateAction Report  - trying to save file 3 " );
			
		    // now that we have the target directory, lets copy the file over.
		    // File (or directory) to be moved 
		    File file = new File(filepath); 
		    // Destination directory 
		    File dir = new File(targetDirName); 
		    // Move file to new directory 
		    boolean success = file.renameTo(new File(dir, file.getName())); 
		    

			System.out.println("TM : inside wordtemplateAction Report  - trying to save file 4" );
		    
			String targetFilePath = targetDirName + File.separator +  file.getName();
			if (tDCSAction.equals("new")){
				String uniqueTDCSFullTag = this.getServletContext().getInitParameter("uniqueTDCSFullTag");
				tDCSDocument = TDCSUtil.createNewTDCSDocument(uniqueTDCSFullTag, project.getProjectId() , folderId,title,
				description,"word",targetFilePath, user, databaseType);
	
				// since we just created a new TDCS entry for this template, lets update this info
				// inthe db for wordtemplate.
				wordTemplate.setTDCSDocumentId(tDCSDocument.getDocumentId());
				PrintWriter out = response.getWriter();
			    out.println("<div class='alert alert-success'><div style='float:right'>" +
			    		" 		<a href='#' onClick='document.getElementById(\"pushWordTemplateReportToTDCSResponse\").style.display=\"none\"'>Close</a> " +
			    		"	</div>" +
			    		"<span class='normalText'> A new document <b>"+ tDCSDocument.getFullTag() + "</b>" + 
			    		"  has been added to the TraceCloud Document Control System. You can access it at  " +
			    		" 	<a href='#' onClick='displayTDCSHome()'><img src='/GloreeJava2/images/database_refresh16.png'> TDCS</a> " +
			    		" &nbsp;&nbsp;</span></div>");
			    out.close();
			    return;
			}
			else if (tDCSAction.equals("existing")){
				TDCSUtil.updateExistingTDCSDocument(tDCSDocument, project.getProjectId() , folderId,
				description,"word",targetFilePath, user, databaseType);
				
				// since we have added a new version, lets refresh the tDCSDocument object.
				tDCSDocument = new TDCSDocument(tDCSDocument.getDocumentId(), databaseType);
				PrintWriter out = response.getWriter();
			    out.println("<div class='alert alert-success'><div style='float:right'>" +
			    		" 		<a href='#' onClick='document.getElementById(\"pushWordTemplateReportToTDCSResponse\").style.display=\"none\"'>Close</a> " +
			    		"	</div>" +
			    		"<span class='normalText'> A new version <b>"+ tDCSDocument.getCurrentVersionNumber()+
			    		"</b> has been added to the document <b>"+ tDCSDocument.getFullTag() + "</b>" + 
			    		"  in the TraceCloud Document Control System. You can access it at  " +
			    		" 	<a href='#' onClick='displayTDCSHome()'><img src='/GloreeJava2/images/database_refresh16.png'> TDCS</a> " +
			    		" &nbsp;&nbsp;</span></div>");
			    out.close();
				return;
			}
		}	
			
		
		
		if (action.equals("generateEmptyWordTemplate")) {
			WordTemplateUtil.generateEmptyWordTemplate(project , user, request, response);
			return;
		}
		
		
		else if (action.equals("deleteWordTemplate")){
			int templateId = Integer.parseInt(request.getParameter("templateId"));
			int folderId = Integer.parseInt(request.getParameter("folderId"));
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
			
			WordTemplateUtil.deleteWordTemplate(templateId, project, user,  databaseType);
			
			PrintWriter out=response.getWriter();	//  PrintWriter to write text to the response
			out.println("<div class='level1Box'><div class='alert alert-success'>" +
					"<span class='normalText'>Your Word Document has been successfully deleted.</span></div></div>");
			out.close();
			return;
		}
		else if (action.equals("downloadTemplate")) {
			int templateId = Integer.parseInt(request.getParameter("templateId"));
			int folderId = Integer.parseInt(request.getParameter("folderId"));
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
			
			
			String filePath = wordTemplate.getTemplateFilePath();
			// we get the fileName by looking at the filePath's content after the \
			String fileName = (String) filePath.substring(filePath.lastIndexOf("\\")+1);
			
			fileName.replace(" ", "_");
			fileName.replace("-", "_");

			FileInputStream inputStream= new FileInputStream(filePath);
			
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setContentType("application/msword");
    		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
    		
	        ServletOutputStream out = response.getOutputStream();
	        byte buf[]=new byte[1024];
	        int len;
	        while((len=inputStream.read(buf))>0)
	        	out.write(buf,0,len);
	        
	        out.close();
	        inputStream.close();
			
			return;
		}
		
		else if (action.equals("createRequirementsConfirm")) {
			int templateId = Integer.parseInt(request.getParameter("templateId"));
			int folderId = Integer.parseInt(request.getParameter("folderId"));
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
			
			String locateProcess = request.getParameter("locateProcess"); 
			ArrayList locatedRequirements  = new ArrayList();
			String styleName = "";
			String paragraphSearch = "";
			
			if (locateProcess.equals("tables-multipleReqs")) {
				// this means that the word document is flawless. So lets get the requirements.
				locatedRequirements = WordTemplateUtil.locateRequirementsByTableMultipleReqs(templateId, project,
					folderId, request, response, securityProfile, databaseType);
			}
			if (locateProcess.equals("tables-singleReq")) {
				// this means that the word document is flawless. So lets get the requirements.
				locatedRequirements = WordTemplateUtil.locateRequirementsByTableSingleReq(templateId, project,
					folderId, request, response,  databaseType);
				
			}
			if (locateProcess.equals("hyperlinks")) {
				locatedRequirements = WordTemplateUtil.locateRequirementsByHyperLink(templateId, project,
				folderId, request, response,  databaseType);
			}
			
			if (
					(locateProcess.equals("styles")) 
					||
					(locateProcess.equals("styles-updatable"))
				) {
				styleName = request.getParameter("styleName");
				locatedRequirements = WordTemplateUtil.locateRequirementsByStyle(templateId, project,
				folderId, styleName, request, response,  databaseType);
			}
			
			if (locateProcess.equals("paragraphs")) {
				 paragraphSearch = request.getParameter("paragraphSearch");
				locatedRequirements = WordTemplateUtil.locateRequirementsByParagraphSearch(templateId, project,
				folderId, paragraphSearch, request, response,  databaseType);
			}
			
			request.setAttribute("locatedRequirements", locatedRequirements);
			String url = "/jsp/WordTemplate/createRequirementsFromWordTemplateConfirm.jsp?folderId=" + folderId + 
				"&templateId=" + templateId + "&locateProcess=" + locateProcess +  "&styleName=" + styleName +
				"&paragraphSearch=" + paragraphSearch; 
			RequestDispatcher dispatcher =	request.getRequestDispatcher(url);
			dispatcher.forward(request, response);
			
			return;
		}		
		else if (action.equals("createRequirements")) {
			int templateId = Integer.parseInt(request.getParameter("templateId"));
			int folderId = Integer.parseInt(request.getParameter("folderId"));
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
			
			String locateProcess = request.getParameter("locateProcess");
			String locationNumberString = request.getParameter("locationNumberString");
			
			ArrayList createdRequirements = new ArrayList();
			
			if (locateProcess.equals("tables-singleReq")) {
				createdRequirements = WordTemplateUtil.createRequirementsFromWordTemplateByTableSingleReq(templateId, project,
				folderId, locationNumberString, user, request, response,  databaseType);
			}
			if (locateProcess.equals("tables-multipleReqs")) {
				createdRequirements = WordTemplateUtil.createRequirementsFromWordTemplateByTableMultipleReqs(templateId, project,
				folderId, locationNumberString, user, request, response, securityProfile,  databaseType);
			}
			
			if (locateProcess.equals("hyperlinks")) {
				createdRequirements = WordTemplateUtil.createRequirementsFromWordTemplateByHyperLink(templateId, project,
				folderId, locationNumberString, user, request, response,  databaseType);
			}

			if (locateProcess.equals("styles")) {
				String styleName = request.getParameter("styleName");
				createdRequirements = WordTemplateUtil.createRequirementsFromWordTemplateByStyle(templateId, project,
				folderId, styleName, locationNumberString, user, request, response,  databaseType);
			}
			
			if (locateProcess.equals("styles-updatable")) {
				String styleName = request.getParameter("styleName");
				createdRequirements = WordTemplateUtil.createRequirementsFromWordTemplateByStyleUpdatable(templateId, project,
				folderId, styleName, locationNumberString, user, request, response,  databaseType);
			}
			
			
			
			if (locateProcess.equals("paragraphs")) {
				String paragraphSearch = request.getParameter("paragraphSearch");
				createdRequirements = WordTemplateUtil.createRequirementsFromWordTemplateByParagraphSearch(templateId, project,
						folderId, paragraphSearch, locationNumberString, user, request, response,  databaseType);
			}			
						
			request.setAttribute("createdRequirements", createdRequirements);
			String url = "/jsp/WordTemplate/createRequirementsFromWordTemplateResults.jsp?folderId=" + folderId + 
				"&templateId=" + templateId + "&locateProcess=" + locateProcess; 
			RequestDispatcher dispatcher =	request.getRequestDispatcher(url);
			dispatcher.forward(request, response);
			
			return;
		}				
		
	}


}

