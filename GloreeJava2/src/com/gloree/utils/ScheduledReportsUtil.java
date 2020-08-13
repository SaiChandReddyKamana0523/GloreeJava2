package com.gloree.utils;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashSet;

import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddressList;

import com.gloree.beans.Comment;
import com.gloree.beans.Folder;
import com.gloree.beans.MessagePacket;
import com.gloree.beans.Project;
import com.gloree.beans.RTAttribute;
import com.gloree.beans.Report;
import com.gloree.beans.Requirement;
import com.gloree.beans.RequirementAttachment;
import com.gloree.beans.RequirementType;
import com.gloree.beans.RequirementVersion;
import com.gloree.beans.ScheduledReport;
import com.gloree.beans.SecurityProfile;
import com.gloree.beans.TraceTreeRow;
import com.gloree.beans.User;
import com.lowagie.text.Anchor;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;



public class ScheduledReportsUtil {
	
	 public static void startScheduledReports(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String rootDataDirectory, String databaseType, 
		String mailHost, String transportProtocol, String smtpAuth, String smtpPort,String  smtpSocketFactoryPort,String  emailUserId,String  emailPassword,
		int maxRowsInTraceTree){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
		
			// lets find all scheduled reports that are supposed to run today
			// first find the day of the week for today.
			Calendar now = Calendar.getInstance();
			String[] strDays = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
			        "Friday", "Saturday" };
		    // Day_OF_WEEK starts from 1 while array index starts from 0
			String currentDay =  strDays[now.get(Calendar.DAY_OF_WEEK) - 1];
		    System.out.println("Today is a : " + currentDay);
		    
		    
		    
		    
			String sql = "select sr.id, sr.project_id, sr.report_id, sr.run_task_on, sr.owner, " +
			" r.name,  f.folder_path " +
			" from gr_scheduled_reports sr, gr_reports r , gr_folders f, gr_users u " +
			" where sr.report_id = r.id " +
			" and r.folder_id = f.id " +
			" and sr.owner = u.email_id " +
			" order by sr.id ";
			
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			// Only one row should be returned.
			while (rs.next()){				
				int scheduledReportId = rs.getInt("id");
				int projectId = rs.getInt("project_id");
				int reportId = rs.getInt("report_id");
				String runTaskOn =  rs.getString("run_task_on");
				String scheduledReportOwner =  rs.getString("owner");
				String reportName = rs.getString("name");
				String folderPath = rs.getString ("folder_path");
				
				
				System.out.println("\n\nReport Name :" + reportName );
				System.out.println("Folder Path :" + folderPath);
				System.out.println("Run Task On :" + runTaskOn);
				System.out.println("Current Day is :" + currentDay);
				

				// lets see if this report's run task on is for today
				if (runTaskOn.contains(currentDay)){
					System.out.println("This report is scheduled to run today" );
				
					// if the owner is not an active member of the project, lets not run the report
					boolean ownerIsActiveMemberOfProject = ProjectUtil.isOwnerActiveMemberOfProject(scheduledReportOwner, projectId);
					if (!(ownerIsActiveMemberOfProject)){
						System.out.println("Skipping this report as the owner '" + scheduledReportOwner  + "' is not an active member of the project" );
						continue;	
					}
					
					System.out.println("Starting report run");
					
					runReport(request, con, databaseType, scheduledReportOwner , scheduledReportId, projectId, reportId, rootDataDirectory, 
							mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword, 
							maxRowsInTraceTree);
					
				
					System.out.println("Ending report run");
					
					
				}
				else {
					System.out.println("Skipping this report as it's not scheduled to run today" );
					
				}
			}
		    
			
			
			prepStmt.close();
			rs.close();
			con.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
	 }

	 

		private static void runReport(HttpServletRequest request, java.sql.Connection con, String databaseType , 
			String scheduledReportOwnerEmailId, int scheduledReportId, int projectId, int reportId, String rootDataDirectory,
			String mailHost, String transportProtocol, String smtpAuth, String smtpPort,String  smtpSocketFactoryPort,String  emailUserId,String  emailPassword,
			int maxRowsInTraceTree) throws SQLException{
			
			ScheduledReport scheduledReport = new ScheduledReport(scheduledReportId);
			
			Report report = new Report(reportId);
			
			
			User scheduledReportOwner = new User(scheduledReportOwnerEmailId, databaseType);
			
			String filename = "";
			
			

			
				
				
			// for this report, lets get the report definition from the db and run the query  
			// to get the data
			String reportDefinition = report.getReportDefinition();
			
			String danglingSearch = "all";
			String orphanSearch = "all";
			String completedSearch = "all";
			String incompleteSearch = "all";
			String suspectUpStreamSearch = "all";
			String suspectDownStreamSearch = "all";
			String lockedSearch = "all";
			String includeSubFoldersSearch = "no";
			
			int inRTBaselineSearch = 0;
			int changedAfterRTBaselineSearch = 0;
			String testingStatusSearch = "";
			
			
			String nameSearch = "";
			String descriptionSearch = "";
			String ownerSearch = "";
			String externalURLSearch = "";
			String approvedBySearch = "";
			String rejectedBySearch = "";
			String pendingBySearch = "";
			String traceToSearch = "";
			String traceFromSearch = "";

			String pctCompleteSearch = "";
			
			String statusSearch = "";
			String prioritySearch = "";

			String sortBy = "";
			String sortByType =  "";
			
			String customAttributeSearch = "";
			
			String standardDisplay = "";
			String customAttributesDisplay = "";
			
			String displayRequirementType = "";
			if (!(reportDefinition.contains("danglingSearch:--:all"))){
				
				danglingSearch = "danglingOnly";
			}
			if (!(reportDefinition.contains("orphanSearch:--:all"))){
				orphanSearch = "orphanOnly";
			}
			if (!(reportDefinition.contains("completedSearch:--:all"))){
				completedSearch= "completedOnly";
			}
			if (!(reportDefinition.contains("incompleteSearch:--:all"))){
				incompleteSearch = "incompleteOnly";
			}
			if (!(reportDefinition.contains("suspectUpStreamSearch:--:all"))){
				suspectUpStreamSearch = "suspectUpStreamOnly";
			}
			if (!(reportDefinition.contains("suspectDownStreamSearch:--:all"))){
				suspectDownStreamSearch = "suspectDownStreamOnly";
			}
			if (reportDefinition.contains("lockedSearch:--:lockedOnly")){
				lockedSearch = "lockedOnly";
			}
			if (!(reportDefinition.contains("includeSubFoldersSearch:--:no"))){
				includeSubFoldersSearch = "includeSubFoldersOnly";
			}
		
			// lets get the text box values for standard attributes.
			String [] values = reportDefinition.split(":###:");
			for (int i=0; i<values.length;i++) {
				String value = "";
				value = values[i];
				
				if (value.contains("inRTBaselineSearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1  ){
						try {
						inRTBaselineSearch = Integer.parseInt(a[1]); 
						}
						catch (Exception e){
							
						}
					}
				}

				if (value.contains("changedAfterRTBaselineSearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1  ){
						try {
							changedAfterRTBaselineSearch = Integer.parseInt(a[1]); 
						}
						catch (Exception e){
							
						}
						
					}
				}				
				
				// handling the testing select box.
				if ((value.contains("testingStatusSearch:--:") && (value.contains("Pending")))) {
					testingStatusSearch += "Pending,";
				}
				if ((value.contains("testingStatusSearch:--:") && (value.contains("Pass")))) {
					testingStatusSearch += "Pass,"; 
				}
				if ((value.contains("testingStatusSearch:--:") && (value.contains("Fail")))) {
					testingStatusSearch += "Fail,";
				}

				
				if (value.contains("nameSearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1  ){
						nameSearch =  a[1] ;
					}
				}
				
				if (value.contains("descriptionSearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						descriptionSearch =  a[1]  ;
					}					
				}
				if (value.contains("ownerSearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						ownerSearch =  a[1] ;
					}
				}
				if (value.contains("externalURLSearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						externalURLSearch =  a[1] ;
					}
				}
				if (value.contains("pctCompleteSearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						pctCompleteSearch  =  a[1] ;
					}
				}
				if (value.contains("approvedBySearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						approvedBySearch =  a[1]  ;
					}
				}
				if (value.contains("rejectedBySearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						rejectedBySearch =  a[1];
					}
				}
				if (value.contains("pendingBySearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						pendingBySearch =  a[1] ;
					}
				}
				
				if (value.contains("traceToSearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						traceToSearch = a[1]  ;
					}
				}
				if (value.contains("traceFromSearch")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						traceFromSearch =  a[1] ;
					}
				}
				
				// handling the status select box.
				if ((value.contains("statusSearch:--:") && (value.contains("Draft")))) {
					statusSearch += "Draft,";
				}
				if ((value.contains("statusSearch:--:") && (value.contains("In Approval WorkFlow")))) {
					statusSearch += "In Approval WorkFlow,";
				}
				if ((value.contains("statusSearch:--:") && (value.contains("Approved")))) {
					statusSearch += "Approved,";
				}
				if ((value.contains("statusSearch:--:") && (value.contains("Rejected")))) {
					statusSearch += "Rejected,";
				}
				
				
				
				
				// handling the priority select box.
				if ((value.contains("prioritySearch:--:") && (value.contains("High")))) {
					prioritySearch += "High,";
				}
				if ((value.contains("prioritySearch:--:") && (value.contains("Medium")))) {
					prioritySearch += "Medium,";
				}
				if ((value.contains("prioritySearch:--:") && (value.contains("Low")))) {
					prioritySearch += "Low,";
				}
				
				
				// lets get the custom attribute search
				if (value.contains("customA")) {
					String [] a = value.split(":--:");
					
					if (a.length > 1  ){
						try {
							String id = a[0];
							String aValue = a[1];
							// id is typically in the format customA38 or customA39 where 38 adn 39 are
							// the custom Attribute Ids. so, we can drop off the customA to get the attribute id.
							int rTAttributeId = Integer.parseInt(id.replace("customA", ""));
							
							customAttributeSearch += ScheduledReportsUtil.getAttributeName(con,  rTAttributeId)+ ":#:" +  aValue + ":--:";
						}
						catch (Exception e){
							
						}
						
					}
				}	
				
				if (value.contains("sortBy:--:")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						sortBy =  a[1] ;
					}
				}
				
				if (value.contains("sortByType:--:")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						sortByType =  a[1] ;
					}
				}
				
				if (value.contains("standardDisplay:--:")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						standardDisplay =  a[1] ;
					}
				}
			
				
				if (value.contains("customAttributesDisplay:--:")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						customAttributesDisplay =  a[1] ;
					}
				}
				
				
				
				
				if (value.contains("displayRequirementType:--:")) {
					String [] a = value.split(":--:");
					if (a.length > 1 ){
						displayRequirementType =  a[1] ;
					}
				}
				
			}

			if (customAttributeSearch.contains(":--:")){
				customAttributeSearch = (String) customAttributeSearch.subSequence(0,customAttributeSearch.lastIndexOf(":--:"));
			}
			
			if (report.getReportType().equals("list")){
					
				ArrayList listReport = runListReport(con, scheduledReportOwner.getUserId(), 
					projectId, report.getFolderId() , "active",
					danglingSearch,orphanSearch,completedSearch,incompleteSearch,
					suspectUpStreamSearch, suspectDownStreamSearch,  lockedSearch, includeSubFoldersSearch,
					inRTBaselineSearch,changedAfterRTBaselineSearch, testingStatusSearch,
					nameSearch, descriptionSearch, ownerSearch, externalURLSearch,
					approvedBySearch, rejectedBySearch, pendingBySearch , 
					traceToSearch,
					traceFromSearch, statusSearch, prioritySearch, pctCompleteSearch, 
					customAttributeSearch, sortBy, sortByType,  databaseType);
				
				if (scheduledReport.getAttachmentType().equals("excel")){
					String includeRevisionHistory = "no";
					
					try {
						filename = exportListReportToExcel(request, con, listReport, 
							standardDisplay, customAttributesDisplay, scheduledReport, report, scheduledReportOwner , includeRevisionHistory, databaseType, rootDataDirectory);
					} catch (ServletException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println(" fileName is " + filename);
				}
				
				

				if (scheduledReport.getAttachmentType().equals("excelVersionComments")){
					String includeRevisionHistory = "yes";
					
					try {
						
						filename = exportListReportToExcel(request, con, listReport, 
							standardDisplay, customAttributesDisplay, scheduledReport, report, scheduledReportOwner , includeRevisionHistory, databaseType, rootDataDirectory);
					} catch (ServletException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println(" fileName is " + filename);
				}

				if (scheduledReport.getAttachmentType().equals("word")){
					
					
					try {
						
						filename = exportListReportToWord(request, con, listReport, 
							standardDisplay, customAttributesDisplay, scheduledReport, report, scheduledReportOwner ,  databaseType, rootDataDirectory);
					} catch (ServletException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println(" fileName is " + filename);
				}
	
				if (scheduledReport.getAttachmentType().equals("pdf")){
					
					
					try {
						
						filename = exportListReportToPDF(request, con, listReport, 
							standardDisplay, customAttributesDisplay, scheduledReport, report, scheduledReportOwner ,  databaseType, rootDataDirectory);
					} catch (ServletException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println(" fileName is " + filename);
				}				
				
			}
			
			
			if (report.getReportType().equals("traceTree")){
			
				
				ArrayList traceTreeReport = runTraceTreeReport(con, scheduledReportOwner.getUserId(), projectId, report.getFolderId(),
					"active", report.getTraceTreeDepth(),
					danglingSearch,orphanSearch,completedSearch,incompleteSearch,
					suspectUpStreamSearch, suspectDownStreamSearch,  lockedSearch, includeSubFoldersSearch, 
					inRTBaselineSearch,changedAfterRTBaselineSearch, testingStatusSearch,
					nameSearch, descriptionSearch, ownerSearch, externalURLSearch,
					approvedBySearch, rejectedBySearch, pendingBySearch ,
					traceToSearch,
					traceFromSearch, statusSearch, prioritySearch, pctCompleteSearch, 
					customAttributeSearch, databaseType, maxRowsInTraceTree,request, displayRequirementType );
		
				if (scheduledReport.getAttachmentType().equals("excel")){
					String includeRevisionHistory = "no";
					
					try {
						filename = exportTraceTreeReportToExcel(request, con, traceTreeReport, 
							standardDisplay, customAttributesDisplay, scheduledReport, report, scheduledReportOwner , includeRevisionHistory, databaseType, rootDataDirectory);
					} catch (ServletException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println(" fileName is " + filename);
				}
				
				if (scheduledReport.getAttachmentType().equals("excelVersionComments")){
					String includeRevisionHistory = "yes";
					
					try {
						filename = exportTraceTreeReportToExcel(request, con, traceTreeReport, 
							standardDisplay, customAttributesDisplay, scheduledReport, report, scheduledReportOwner , includeRevisionHistory, databaseType, rootDataDirectory);
					} catch (ServletException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println(" fileName is " + filename);
				}
				
				if (scheduledReport.getAttachmentType().equals("word")){
					
					try {
						filename = exportTraceTreeReportToWord(request, con, traceTreeReport, 
							standardDisplay, customAttributesDisplay, scheduledReport, report, scheduledReportOwner , databaseType, rootDataDirectory);
					} catch (ServletException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println(" fileName is " + filename);
				}		
				
				if (scheduledReport.getAttachmentType().equals("pdf")){
					
					try {
						filename = exportTraceTreeReportToPDF(request, con, traceTreeReport, 
							standardDisplay, customAttributesDisplay, scheduledReport, report, scheduledReportOwner, databaseType, rootDataDirectory);
					} catch (ServletException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println(" fileName is " + filename);
				}
				
			}
			

			// lets send the email out to the toEmailId;
			ArrayList toArrayList = new ArrayList();
			String to = scheduledReport.getToEmailAddresses();
			if (to != null){
				to = to.trim();
				if (!to.equals("")){
					if (to.contains(",")){
						String [] toEmails = to.split(",");
						for (int i=0; i < toEmails.length; i++ ){
							toArrayList.add(toEmails[i]);
						}
					}
					else {
						toArrayList.add(to);
					}
				}
			}
			
			
			ArrayList ccArrayList = new ArrayList();
			String cc = scheduledReport.getCcEmailAddresses();
			if (cc != null){
				cc = cc.trim();
				if (!cc.equals("")){
					if (cc.contains(",")){
						String [] ccEmails = cc.split(",");
						for (int i=0; i < ccEmails.length; i++ ){
							ccArrayList.add(ccEmails[i]);
						}
					}
					else {
						ccArrayList.add(cc);
					}
				}
			}
		
			MessagePacket mP = new MessagePacket(toArrayList, ccArrayList, scheduledReport.getSubjectValue(), scheduledReport.getMessageValue(), filename);
			
			
			
			EmailUtil.emailWithAttachment(mP, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword );

			
			// now lets remove the temp file.
			File file = new File(filename);
			if (file != null){
				// lets drop the file.
				file.delete();
			}
			
		}

	 
	
	// This method uses the Apache POI module to print out XLS files.
    private static String exportListReportToExcel (HttpServletRequest request,java.sql.Connection con, 
    		ArrayList reportArrayList , String standardDisplay, String customAttributesDisplay, 
    		ScheduledReport scheduledReport, Report report, 
    		User scheduledReportOwner ,String includeRevisionHistory, String databaseType, String rootDataDirectory) 
    		throws ServletException, IOException {
    	
    	
    	String filename = "";
    	Project project = new Project(report.getProectId(), databaseType);
    	Folder folder = new Folder(report.getFolderId());
    	RequirementType requirementType = new RequirementType(folder.getRequirementTypeId());
    	
		try {
        	// create a file name and set it to it.
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
    		String today =  sdf.format(cal.getTime());
    		filename = report.getReportName() +" " + scheduledReport.getScheduledReportId() + " " +  today + ".xls";
    		filename.replace(' ', '_');
    	
    		
    		HSSFWorkbook wb = new HSSFWorkbook();
    		HSSFCreationHelper createHelper = (HSSFCreationHelper) wb.getCreationHelper(); 

    		HSSFCellStyle headerStyle = wb.createCellStyle();
    		headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
    	    headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    

    	    HSSFCellStyle pendingStyle = wb.createCellStyle();
    	    HSSFFont pendingFont = wb.createFont();
    	    pendingFont.setColor(HSSFColor.INDIGO.index);
    	    pendingStyle.setFont(pendingFont);   		
    	    pendingStyle.setWrapText(true);
    	    
    	    HSSFCellStyle approvedStyle = wb.createCellStyle();
    	    HSSFFont approvedFont = wb.createFont();
    	    approvedFont.setColor(HSSFColor.GREEN.index);
    	    approvedStyle.setFont(approvedFont);   		
    	    approvedStyle.setWrapText(true);
    	    
    	    HSSFCellStyle rejectedStyle = wb.createCellStyle();
    	    HSSFFont rejectedFont = wb.createFont();
    	    rejectedFont.setColor(HSSFColor.RED.index);
    	    rejectedStyle.setFont(rejectedFont);   		
    	    rejectedStyle.setWrapText(true);
    	    

    		HSSFCellStyle danglingStyle = wb.createCellStyle();
    		danglingStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
    	    danglingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    danglingStyle.setWrapText(true);
    	    
    		HSSFCellStyle orphanStyle = wb.createCellStyle();
    		orphanStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
    	    orphanStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    orphanStyle.setWrapText(true);
    	    
    		HSSFCellStyle danglingAndOrphanStyle = wb.createCellStyle();
    		danglingAndOrphanStyle.setFillForegroundColor(HSSFColor.RED.index);
    	    danglingAndOrphanStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    danglingAndOrphanStyle.setWrapText(true);
    	    
    	    HSSFCellStyle wrappedStyle = wb.createCellStyle();
    	    wrappedStyle.setWrapText(true);
    	    
    	    
    	    // lets build all the sheets in this file.
    	    HSSFSheet infoSheet  = wb.createSheet("Report Info");
    	    HSSFSheet reportSheet = wb.createSheet("List Report");
	    	HSSFSheet revisionHistorySheet = null;
    	    HSSFSheet commentsSheet = null;

	    	if( (includeRevisionHistory != null ) && (includeRevisionHistory.equals("yes"))) {
	    		revisionHistorySheet = wb.createSheet("Requirement Version History");
	    	    commentsSheet = wb.createSheet("Requirement Comments");
	    	}
	    	HSSFSheet dataValidationSheet = wb.createSheet("Data Validation");

	    	
	    	/////////////////////////////////////////
    	    //
    	    // lets build the Report Cover Page.
    	    //
    	    /////////////////////////////////////////
    	    
    	    // lets start on the 5th Row.
    	    int startRow = 5; 
    		HSSFRow row     = infoSheet.createRow((short)startRow++);



    		row     = infoSheet.createRow((short)startRow++);
    		HSSFCell cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Excel Report Date"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		HSSFCell cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (today));
    		
    		

    		startRow += 2;
    		
    	
    		// if this is a saved report, then lets print the report info.
    		// note : reportIdString is available only for saved reports.
    			

        		row     = infoSheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report Id"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		cellB.setCellValue(new HSSFRichTextString (Integer.toString(report.getReportId())));
    			
        		row     = infoSheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report Created By"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		cellB.setCellValue(new HSSFRichTextString(scheduledReportOwner.getFirstName() + " " + scheduledReportOwner.getLastName()) );


        		row     = infoSheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report URL"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		String reportURLString = ProjectUtil.getURL(request, report.getReportId(), "report") ;
        		cellB.setCellValue(new HSSFRichTextString ( reportURLString ));
        		
			    
        		
        		row     = infoSheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report Name"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		cellB.setCellValue(new HSSFRichTextString (report.getReportName()));
        		
        		row     = infoSheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report Description"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		cellB.setCellValue(new HSSFRichTextString (report.getReportDescription() ));

    		
    		// project Info.
    		startRow += 4;
    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Prefix"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getShortName()));

    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Name"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getProjectName()));

    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Description"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getProjectDescription()));
    		
    			
    	    // lets build the list report.    	    


            
            Iterator i = reportArrayList.iterator();
            
            int j = 0;
            
            // since we will need to uda (to print the datavalidation) outside the while loop
            // lets define it outside
            String [] attribs = null;
	    	while ( i.hasNext() ) {
	    		Requirement r = (Requirement) i.next();
	    		// a typical uda looks like this 
	    		// Customer:#: SBI:##:Delivery Estimate:#:01/01/12
	    		String uda = r.getUserDefinedAttributes();
				attribs = uda.split(":##:");
	    		
	    		// Create a row and put some cells in it. Rows are 0 based.
	    		j++;
	    		
	    		// for the first row, print the header and user defined columns headers. etc..
	    		if (j == 1){

	        		// Create a row and put some cells in it. Rows are 0 based.
	        		row     = reportSheet.createRow((short)0);	
	        		

	        		// Print the header row for the excel file.
	        		int cellNum = 0;
	        		int column = 0;
	        		
	        		HSSFCell cell = row.createCell(cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Tag          "));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(column++);
	        		// lets add a comment to the tag describing the color coding.
	        		HSSFPatriarch patr = reportSheet.createDrawingPatriarch();
	        		HSSFRichTextString textString = createHelper.createRichTextString("Legend \n\n" +
	        				"Orphan (No UpStream Requirements) : Background Color Yellow\n" +
	        				"Dangling (No DownStream Requirements) : Background Color Orange\n" +
	        				"Orphan AND Dangling (No UpStream or DownStream Requirements) : Background Color Red\n\n" );
	        		HSSFClientAnchor anchor = createHelper.createClientAnchor();
	        		anchor.setAnchor((short)1, 1, 1,  1, (short)5,5, 5, 5);
	        		HSSFComment comment1 = patr.createComment( anchor );
	        		comment1.setString(textString);
	        		cell.setCellComment(comment1);
	        		
	        	    
	        	    
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("URL To Requirement                                                                                                          "));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Version"));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Name                                                                                                              "));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Description                                                                                                        "));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(column++);
	        		
	        		if (standardDisplay.contains("owner")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Owner                                  "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (standardDisplay.contains("testingStatus")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Testing Status                          "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
		        	}

	        		
	        		if (standardDisplay.contains("status")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Approval Status                "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		
	        		if (standardDisplay.contains("priority")) {
	            	    // lets add some data validation to this reportSheet.
	        			CellRangeAddressList addressList = new CellRangeAddressList(0, 5000, cellNum+1, cellNum+1);
	        			DVConstraint dvConstraint = DVConstraint.createExplicitListConstraint(new String[]{"High", "Medium", "Low"});
	        			HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
	        			dataValidation.setSuppressDropDownArrow(false);
	        			reportSheet.addValidationData(dataValidation);


		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Priority          "));
		        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}

	        		if (standardDisplay.contains("percentComplete")) {
	        			// lets add some data validation to this reportSheet.
	        			CellRangeAddressList addressList = new CellRangeAddressList(0, 5000, cellNum+1, cellNum+1);
	        			DVConstraint dvConstraint =  DVConstraint.createNumericConstraint(
	        				    DVConstraint.ValidationType.INTEGER,
	        				    DVConstraint.OperatorType.BETWEEN, "0", "100");
	        			HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
	        			dataValidation.setSuppressDropDownArrow(false);
	        			dataValidation.setErrorStyle(HSSFDataValidation.ErrorStyle.STOP);
	        			dataValidation.createErrorBox("Box Title", "Please ensure that you enter a number between 0 and 100");

	        			reportSheet.addValidationData(dataValidation);
	        			
	        			
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Percent Complete"));
		        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Approved Dt                            "));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(column++);
	        		
	        		if (standardDisplay.contains("lockedBy")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Locked By                          "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (standardDisplay.contains("pendingBy")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Pending Approval By                "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (standardDisplay.contains("approvedBy")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Approved By                        "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (standardDisplay.contains("rejectedBy")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Rejected By                        "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (standardDisplay.contains("traceTo")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Trace To                           "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (standardDisplay.contains("traceFrom")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Trace From                         "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (standardDisplay.contains("externalURL")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("External URL                                                                                        "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}

	        		if (standardDisplay.contains("folderPath")) {
	            	    // lets add some data validation to this reportSheet.
	        			CellRangeAddressList addressList = new CellRangeAddressList(0, 5000, cellNum+1, cellNum+1);
	        			HSSFName namedRange = wb.createName();
	        			namedRange.setNameName("folderPathDataValidation");
	        			namedRange.setRefersToFormula("'Data Validation'!$A$2:$A$"+ (project.getMyFolders().size() +1)  );
	        			DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint("folderPathDataValidation");
        			  
	        			HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
	        			dataValidation.setSuppressDropDownArrow(false);
	        			reportSheet.addValidationData(dataValidation);

		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Folder Path                                             "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (standardDisplay.contains("baselines")) {	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Baselines                          "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (standardDisplay.contains("createdDate")) {	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Created Date                              "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}

	        		if (standardDisplay.contains("attachments")) {	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Attachments                         "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		// now to print the custom labels.
	        		int dataValidationCount = project.getMyFolders().size()  + 3;
	        		
					for (int k=0; k<attribs.length; k++) {
						String[] attrib = attribs[k].split(":#:");
						// lets get the attribute object.
						// occasionally attrib[0] can be empty. For example if this req type does not have
						// custom attribs defined and the first req has uda = ""
						if ((attrib[0] == null) || (attrib[0].trim().equals(""))){
							continue;
						}
						RTAttribute rTAttribute = new RTAttribute(folder.getRequirementTypeId(), attrib[0].trim());
						
	
						if (customAttributesDisplay.contains(attrib[0]) ) {
							// lets display the custom attribute based on the display field value
							if (rTAttribute.getAttributeType().equals("Drop Down")){
								// NOTE	 : VERY IMPORTANT : WE ARE STORING ALL THE CUSTOM ATTRIBUTES VALUES IN 
								// A COLUMN IN A TAB CALLED DATA VALIDATION. SINCE WE DON'T KNOW HOW MANY CUSTOM
								// ATTRIBS THERE ARE AND HOW MANY VALUES THEY CAN HOLD, WE WARE FORCED TO USE
								// EXCEL CELLS TO DO THE VALIDATION
								// TO MAKE THE LONG STORY SHORT, MAKE SURE THAT YOU USE THE SAME LOGIC TO POPULATE
								// TEH DATA VALIDATION PAGE WITH VALUES AS YOU ARE USING TO READ THOSE VALUES BACK
								// FOR SETTING THE VALIDATION. OTHERWISE YOU ARE SCREWED.......
								// since this is a drop down, the drop down options will have a value.
								int countOfAttributeOptions = 0;
								String attributeOptionsString = rTAttribute.getAttributeDropDownOptions();
								if (attributeOptionsString != null){
									String [] attributeOptions = attributeOptionsString.split(",");
									countOfAttributeOptions = attributeOptions.length;
								}
								
								// now we know how many options are there , lets set the beginning and ending set
								// for the data validation for this attribute type.
								int validationStart = dataValidationCount;
								int validationEnd = dataValidationCount + countOfAttributeOptions;
								dataValidationCount += countOfAttributeOptions + 3;
								// this is a drop down type attribute.
								// lets add some data validation to this reportSheet.
			        			CellRangeAddressList addressList = new CellRangeAddressList(0, 5000, cellNum+1, cellNum+1);
			        			HSSFName namedRange = wb.createName();
			        			String rangeName = rTAttribute.getAttributeName() + "DataValidation";
			        			rangeName = rangeName.trim().replace(" ", "_");
			        			rangeName = rangeName.trim().replace("-", "_");
			        			namedRange.setNameName(rangeName);
			        			namedRange.setRefersToFormula("'Data Validation'!$A$"+ validationStart +":$A$"+ validationEnd  );
			        			DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(rangeName);
							
			        			HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
			        			dataValidation.setSuppressDropDownArrow(false);
			        			reportSheet.addValidationData(dataValidation);
							}
							cell = row.createCell(++cellNum); 
			        		cell.setCellValue(new HSSFRichTextString (attrib[0] + "             "));
    		        		cell.setCellStyle(headerStyle);
			        		reportSheet.autoSizeColumn(column++);
						}
					}		
	    		}

	    		// print the data rows now.
	    		row     = reportSheet.createRow(j);
			    
	    		// Create a cell and put a value in it.
	    		// we have decided to not make cell0 a hyperlink to source because Excel 97-03 was having trouble
	    		// saving these files.
	    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");

	    		int cellNum = 0;
			  
			    HSSFCell cell = row.createCell(cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
			    String requirementTraceTo = r.getRequirementTraceTo();
			    String requirementTraceFrom = r.getRequirementTraceFrom();
			    
			    if (requirementType.getRequirementTypeCanBeOrphan() == 1){
				    if ((requirementTraceTo == null)	||  (requirementTraceTo.trim().equals(""))){
				    	// req type can be orphan and the req is orphan
				    	cell.setCellStyle(orphanStyle);
				    }
			    }
			    if (requirementType.getRequirementTypeCanBeDangling() == 1) {
				    if ((requirementTraceFrom == null)	||  (requirementTraceFrom.trim().equals(""))){
				    	// req type can be dangling and the req is dangling
				    	cell.setCellStyle(danglingStyle);
				    }		
			    }
			    if ((requirementType.getRequirementTypeCanBeDangling() == 1)
			    		&&
			    	(requirementType.getRequirementTypeCanBeOrphan() == 1)
			    		){
				    if (
				    		((requirementTraceTo == null)	||  (requirementTraceTo.equals("")))
				    		&&
				    		((requirementTraceFrom == null)	||  (requirementTraceFrom.equals("")))
				    		){
				    	// req type can be dang and orph and req is dang and orphan
				    	cell.setCellStyle(danglingAndOrphanStyle);
				    }
			    }
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (url));
			    cell.setCellStyle(wrappedStyle);
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (Integer.toString(r.getVersion())));
			    cell.setCellStyle(wrappedStyle);
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getRequirementName()));
			    cell.setCellStyle(wrappedStyle);
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getRequirementDescriptionNoHTML()));
			    cell.setCellStyle(wrappedStyle);
			    
			    if (standardDisplay.contains("owner")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getRequirementOwner()));
			    	cell.setCellStyle(wrappedStyle);
			    }


			    if (standardDisplay.contains("testingStatus")) {
			    	HSSFCell testingStatusCell = row.createCell(++cellNum);
	        	    testingStatusCell.setCellValue(new HSSFRichTextString (r.getTestingStatus() ));
			    	
			    	HSSFCellStyle testingStatusStyle = wb.createCellStyle();
        			if (r.getTestingStatus().equals("Pending")){
		        		testingStatusStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		        	    testingStatusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		        	}
        			if (r.getTestingStatus().equals("Pass")){
		        		testingStatusStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		        	    testingStatusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		        	}
        			if (r.getTestingStatus().equals("Fail")){
		        		testingStatusStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		        	    testingStatusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		        	}
        			
        			testingStatusCell.setCellStyle(testingStatusStyle);
        		}
			    
			    
        		if (standardDisplay.contains("status")) {			    
	        	    HSSFCell statusCell = row.createCell(++cellNum);
	        	    statusCell.setCellValue(new HSSFRichTextString (r.getApprovalStatus()));
	
	        	    // lets set the status Cell color based on its value
	        	    HSSFCellStyle statusStyle = wb.createCellStyle();
				    if (r.getApprovalStatus().equals("Draft")){
		        		statusStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		        	}
				    if (r.getApprovalStatus().equals("In Approval WorkFlow")){
		        		statusStyle.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
		        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		        	}
				    if (r.getApprovalStatus().equals("Approved")){
		        		statusStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		        	}
				    if (r.getApprovalStatus().equals("Rejected")){
		        		statusStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		        	}
				    
				    
				    statusCell.setCellStyle(statusStyle);
        		}
        		if (standardDisplay.contains("priority")) {
        			cell = row.createCell(++cellNum);
        			cell.setCellValue(new HSSFRichTextString (r.getRequirementPriority()));
        			cell.setCellStyle(wrappedStyle);
    			    
        		}
        		if (standardDisplay.contains("percentComplete")) {
        			cell = row.createCell(++cellNum);
        			cell.setCellValue(new HSSFRichTextString (r.getRequirementPctComplete() + "" ));
        			cell.setCellStyle(wrappedStyle);
    			    
        		}
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getApprovedByAllDt() ));
			    cell.setCellStyle(wrappedStyle);
			    
        		if (standardDisplay.contains("lockedBy")) {
        			cell = row.createCell(++cellNum);
        			cell.setCellValue(new HSSFRichTextString (r.getRequirementLockedBy()));
        			cell.setCellStyle(pendingStyle);
    			    
        		}
			    
			    
			    // lets handle the approvers.
			    String pendingApprovers = "";
			    String approvedApprovers = "";
			    String rejectedApprovers = "";
			    
			    String[] approvers = new String[0] ;
			    if ((r.getApprovers()!= null) && (r.getApprovers().contains(","))){
			    	approvers = r.getApprovers().split(",");
			    }
			    
			    
			    for (int k=0;k<approvers.length;k++){
			    	if (approvers[k].contains("(P)")){
			    		pendingApprovers += approvers[k].replace("(P)","") + ", ";
			    	}
			    	
			    	if (approvers[k].contains("(A)")){
			    		approvedApprovers += approvers[k].replace("(A)","") + ", ";
			    	}
			    	if (approvers[k].contains("(R)")){
			    		rejectedApprovers += approvers[k].replace("(R)","") + ", ";
			    	}
			    }
			    
			    // lets drop the last ,
			    if (pendingApprovers.contains(",")){
			    	pendingApprovers = (String) pendingApprovers.subSequence(0,pendingApprovers.lastIndexOf(","));
			    }			    
			    if (approvedApprovers.contains(",")){
			    	approvedApprovers = (String) approvedApprovers.subSequence(0,approvedApprovers.lastIndexOf(","));
			    }
			    if (rejectedApprovers.contains(",")){
			    	rejectedApprovers = (String) rejectedApprovers.subSequence(0,rejectedApprovers.lastIndexOf(","));
			    }
			    
			    if (standardDisplay.contains("pendingBy")) {
			    	HSSFCell  pendingCell = row.createCell(++cellNum);
			    	pendingCell.setCellValue(new HSSFRichTextString (pendingApprovers ));
			    	pendingCell.setCellStyle(pendingStyle);
			    }
			    
			    if (standardDisplay.contains("approvedBy")) {
			    	HSSFCell  approvedCell = row.createCell(++cellNum);
			    	approvedCell.setCellValue(new HSSFRichTextString (approvedApprovers ));
			    	approvedCell.setCellStyle(approvedStyle);
			    }
			    
			    if (standardDisplay.contains("rejectedBy")) {
			    	HSSFCell  rejectedCell = row.createCell(++cellNum);
			    	rejectedCell.setCellValue(new HSSFRichTextString (rejectedApprovers ));
			    	rejectedCell.setCellStyle(rejectedStyle);
			    }
	
			    if (standardDisplay.contains("traceTo")) {
			    	row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getRequirementTraceTo() ));
			    	// since these can get very long we aren't wrapping them around.
			    	//cell.setCellStyle(wrappedStyle);
				    
			    }
			    if (standardDisplay.contains("traceFrom")) {
			    	row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getRequirementTraceFrom() ));
			    	// since these can get pretty long, we aren't wrapping them around
			    	cell.setCellStyle(wrappedStyle);
				    
			    }
			    
			    
			    if (standardDisplay.contains("externalURL")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getRequirementExternalUrl() ));
			    	cell.setCellStyle(wrappedStyle);
				    
			    }
			    
			    if (standardDisplay.contains("folderPath")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getFolderPath() ));
			    	cell.setCellStyle(wrappedStyle);
				    
			    }
			    
			    if (standardDisplay.contains("baselines")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getRequirementBaselineString(databaseType) ));
			    	cell.setCellStyle(wrappedStyle);
				    
			    }
			    
			    if (standardDisplay.contains("createdDate")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getCreatedDt() ));
			    	cell.setCellStyle(wrappedStyle);
				    
			    }
			    
			    if (standardDisplay.contains("attachments")) {
			    	String attachmentString = "";
					ArrayList attachments = r.getRequirementAttachments(databaseType);
					if (attachments.size() > 0){  
						Iterator atachmentIterator = attachments.iterator();
						while (atachmentIterator.hasNext()) {
							RequirementAttachment attachment = (RequirementAttachment) atachmentIterator.next();
							attachmentString += "File :  " + attachment.getFileName()  + " \n";
							attachmentString += "Title :  " + attachment.getTitle()  + " \n";
						}															
					} 
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (attachmentString ));
			    	cell.setCellStyle(wrappedStyle);
				    
			    }
			    
			    // now to print the custom values.
				for (int k=0; k<attribs.length; k++) {
					String[] attrib = attribs[k].split(":#:");
					// To avoid a array out of bounds exception where the attrib value wasn't filled in
					// we print the cell only if array has 2 items in it.
					String attribValue = "";
					if (attrib.length ==2){
						attribValue = attrib[1];
					}
					if (customAttributesDisplay.contains(attrib[0]) ) {
						cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString (attribValue));
		        		cell.setCellStyle(wrappedStyle);
					}					
				}
			}
	    	

	    	
	    	
	    	
	    	
	    	
	    	
	    	// lets see if the user  has chosen to print out the Revision History.
	    	// if yes, we will need to add another tab to the excel file and include revision history of every
	    	// requirement in this output.
	    	if( (includeRevisionHistory != null ) && (includeRevisionHistory.equals("yes"))) {
	    		// lets add a new revisionHistorySheet to the excel output.
	    	    // for the first row, print the header and user defined columns headers. etc..

	    	    int rowNum = 0;
        		row     = revisionHistorySheet.createRow(rowNum++);	
        		// Print the header row for the excel file.
        		int cellNum = 0;
        		int column = 0;
        		
        		HSSFCell cell = row.createCell(cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Tag     "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("URL To Requirement                                                                                                "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Version"));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Name                                                                                                              "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Description                                                                                                        "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Approvers                        "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
    		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Approval Status "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Priority"));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
    			cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Owner                                  "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
    		
        		
    			cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Percent Complete"));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        	
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("External URL                                                                                        "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Trace To                           "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Trace From                         "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Created By                        "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
    		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Created Date                              "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
    		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Custom Attributes                            "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
    		
	    	    i = reportArrayList.iterator();
	            
		    	while ( i.hasNext() ) {
		    		Requirement r = (Requirement) i.next();


		    		// for every Requirement in the result set, lets get an arraylist of
		    		// requirement versions.
		    		ArrayList requirementVersions = r.getRequirementVersions( databaseType);
		    		Iterator m = requirementVersions.iterator();
		    		
		    		while (m.hasNext()) {
		    			RequirementVersion v = (RequirementVersion) m.next();
		    			// for each req version, lets print a row.
			    		// print the data rows now.
			    		row     = revisionHistorySheet.createRow(rowNum++);
					

			    		// Create a cell and put a value in it.
						 // make cell0 a hyperlink
			    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");

			    		cellNum = 0;
					    
					    cell = row.createCell(cellNum);
					    cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (url));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (Integer.toString(v.getVersion())));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionName()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionDescriptionNoHTML()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionApprovers() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionStatus() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionPriority() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionOwner() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (Integer.toString(v.getVersionPctComplete())));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionExternalURL() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionTraceTo() ));
					    // we are not wrapping this, as it can get quiet big
					    //cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionTraceFrom() ));
					    // we are not wrapping this, as it can get quiet big
					    //cell.setCellStyle(wrappedStyle);
					    
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionCreatedBy()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionCreatedDt()));
					    cell.setCellStyle(wrappedStyle);
					    
					
					    String uDA = v.getVersionUserDefinedAttributes();
					    String formattedUDA = "";
					    if ((uDA != null) && (uDA.contains(":##:"))) {
					    	String [] uDAs = uDA.split(":##:") ;
					    	
					    	for (int u=0; u<uDAs.length; u++) {
					    		formattedUDA += uDAs[u] + "\n";
					    	}
					    }
					    cell = row.createCell(++cellNum);
			    		cell.setCellValue(new HSSFRichTextString (formattedUDA));
			    		cell.setCellStyle(wrappedStyle);
			    		
					    
		    		}
		    	}
		    	

		    	
		    	
	    		// for the first row, print the header and user defined columns headers. etc..

	    	    rowNum = 0;
        		row     = commentsSheet.createRow(rowNum++);	
        		// Print the header row for the excel file.
        		cellNum = 0;
        		column = 0;
        		
        		cell = row.createCell(cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Tag     "));
        		cell.setCellStyle(headerStyle);
        		commentsSheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("URL To Requirement                                                                                                "));
        		cell.setCellStyle(headerStyle);
        		commentsSheet.autoSizeColumn(column++);
        		
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Version"));
        		cell.setCellStyle(headerStyle);
        		commentsSheet.autoSizeColumn(column++);
        		
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Commenter                                    "));
        		cell.setCellStyle(headerStyle);
        		commentsSheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Comment                                                                                                           "));
        		cell.setCellStyle(headerStyle);
        		commentsSheet.autoSizeColumn(column++);
        		        

        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Date                              "));
        		cell.setCellStyle(headerStyle);
        		commentsSheet.autoSizeColumn(column++);
        			    
	    	    
	    	    i = reportArrayList.iterator();
	            
		    	while ( i.hasNext() ) {
		    		Requirement r = (Requirement) i.next();


		    		// for every Requirement in the result set, lets get an arraylist of
		    		// requirement comments.
		    		ArrayList requirementComments = r.getRequirementComments( databaseType);
		    		Iterator m = requirementComments.iterator();
		    		
		    		while (m.hasNext()) {
		    			Comment c = (Comment) m.next();
		    			// for each req comment, lets print a row.
			    		// print the data rows now.
			    		row     = commentsSheet.createRow(rowNum++);
					

			    		// Create a cell and put a value in it.
						 // make cell0 a hyperlink
			    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");

			    		cellNum = 0;
					    cell = row.createCell(cellNum);
					    cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (url));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (Integer.toString(c.getVersion())));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (c.getCommenterEmailId()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (c.getComment_note()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (c.getCommentDate()));
					    cell.setCellStyle(wrappedStyle);
					    
		    		}
		    	}
	    	}

	    	
    	    // lets build a sheet to hold data validation rows.
	    	int rowNum = 0;
    			
			ArrayList folders = project.getMyFolders();
			Iterator f = folders.iterator();
			
			// folder paths
			row     = dataValidationSheet.createRow((short)(rowNum++));	
			HSSFCell cell = row.createCell(0);
			cell.setCellValue(new HSSFRichTextString ("Valid Folder Paths                                                             "));
    		cell.setCellStyle(headerStyle);
    		dataValidationSheet.autoSizeColumn(0);

			while (f.hasNext()){
				Folder tempFolder = (Folder) f.next();
				row     = dataValidationSheet.createRow((short)(rowNum++));	
				cell = row.createCell(0);
				cell.setCellValue(new HSSFRichTextString (tempFolder.getFolderPath()));
			}
	    	
    		// now to print the custom labels.
    		int dataValidationCount = (project.getMyFolders().size()) + 2;
			for (int k=0; k<attribs.length; k++) {
				String[] attrib = attribs[k].split(":#:");
				// lets get the attribute object.
				// occasionally attrib[0] can be empty. For example if this req type does not have
				// custom attribs defined and the first req has uda = ""
				if ((attrib[0] == null) || (attrib[0].trim().equals(""))){
					continue;
				}
				RTAttribute rTAttribute = new RTAttribute(folder.getRequirementTypeId(), attrib[0].trim());
				
				
				if (customAttributesDisplay.contains(attrib[0]) ) {
					// lets display the custom attribute based on the display field value
					if (rTAttribute.getAttributeType().equals("Drop Down")){
						// NOTE	 : VERY IMPORTANT : WE ARE STORING ALL THE CUSTOM ATTRIBUTES VALUES IN 
						// A COLUMN IN A TAB CALLED DATA VALIDATION. SINCE WE DON'T KNOW HOW MANY CUSTOM
						// ATTRIBS THERE ARE AND HOW MANY VALUES THEY CAN HOLD, WE WARE FORCED TO USE
						// EXCEL CELLS TO DO THE VALIDATION
						// TO MAKE THE LONG STORY SHORT, MAKE SURE THAT YOU USE THE SAME LOGIC TO POPULATE
						// TEH DATA VALIDATION PAGE WITH VALUES AS YOU ARE USING TO READ THOSE VALUES BACK
						// FOR SETTING THE VALIDATION. OTHERWISE YOU ARE SCREWED.......
						// since this is a drop down, the drop down options will have a value.
						String attributeOptionsString = rTAttribute.getAttributeDropDownOptions();
						String [] attributeOptions = null;
						if (attributeOptionsString != null){
							attributeOptions = attributeOptionsString.split(",");
						}
						
						// now we know how many options are there , lets set the beginning and ending set
						// for the data validation for this attribute type.
				
						row     = dataValidationSheet.createRow((short)(dataValidationCount -1));	
						cell = row.createCell(0);
						cell.setCellValue(new HSSFRichTextString ( rTAttribute.getAttributeName()));
			    		cell.setCellStyle(headerStyle);
			    		dataValidationSheet.autoSizeColumn(0);

			    		int parentAttributeId = rTAttribute.getParentAttributeId();
						for (int m=0 ; m<attributeOptions.length; m++){
							String optionName = attributeOptions[m];
							if (parentAttributeId > 0){
								// this is a child attribute and the attribute values are like Porsche:911,Porsche:Panamera,Porsche:Carrera
								// where Porsche is the parent attribute value and 911,Panamera and Carrera are the potential child values.
								// so we need to strip out the first portion .
								if ((optionName != null) && (optionName.contains(":"))){
									String [] oN = optionName.split(":");
									optionName = oN[1];
								}
							}
							row     = dataValidationSheet.createRow((short)(dataValidationCount++));	
							cell = row.createCell(0);
							cell.setCellValue(new HSSFRichTextString (optionName));
						}
						dataValidationCount += 3;
					}
				}
			}

					
					

	    		// if rootDataDirectory/TraceCloud does not exist, lets create it.
	    		File traceCloudRoot = new File (rootDataDirectory + File.separator + "TraceCloud");
	    		if (!(traceCloudRoot.exists() )){
	    		    new File(rootDataDirectory + File.separator + "TraceCloud").mkdir();
	    		}

	    		// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
	    		File tempFolderRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp");
	    		if (!(tempFolderRoot.exists() )){
	    			new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp").mkdir();
	    		}

	    		filename = rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp" + File.separator + filename;
	    		FileOutputStream fileOut = new FileOutputStream(filename);
	    		wb.write(fileOut);
	    		fileOut.close();
        
        } catch (FileNotFoundException fnfe) {
            // It might not be possible to create the target file.
            fnfe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }

   

	
	public static ArrayList runListReport(java.sql.Connection con, int  userId, int projectId, int folderId,
			String filter, String danglingSearch, String orphanSearch,
			String completedSearch, String incompleteSearch,
			String suspectUpStreamSearch, String suspectDownStreamSearch,
			String lockedSearch, String includeSubFoldersSearch, 
			int inRTBaselineSearch, int changedAfterRTBaselineSearch, String testingStatusSearch,
			String nameSearch,
			String descriptionSearch, String ownerSearch,
			String externalURLSearch, String approvedBySearch,
			String rejectedBySearch, String pendingBySearch,
			String traceToSearch, String traceFromSearch, String statusSearch,
			String prioritySearch, String pctCompleteSearch,
			String customAttributeSearch, 
			String sortBy, String sortByType, String databaseType) {
		ArrayList report = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {
			
			
			
			
			LinkedHashSet privileges = getUserPrivileges(con, userId);
			String sql = ReportUtil.buildSQL(projectId, folderId, filter, danglingSearch,
					orphanSearch, completedSearch, incompleteSearch,
					suspectUpStreamSearch, suspectDownStreamSearch,
					 lockedSearch, includeSubFoldersSearch, inRTBaselineSearch, 
					changedAfterRTBaselineSearch, testingStatusSearch, nameSearch,
					descriptionSearch, ownerSearch, externalURLSearch,
					approvedBySearch, rejectedBySearch, pendingBySearch,
					traceToSearch, traceFromSearch, statusSearch,
					prioritySearch, pctCompleteSearch, customAttributeSearch,
					sortBy, sortByType,  databaseType);


			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();

			int requirementId = 0;
			int requirementTypeId = 0;
			projectId = 0;
			String requirementName = "";
			String requirementDescription = "";
			String requirementTag = "";
			String requirementFullTag = "";
			int version = 0;
			String approvedByAllDt = "";
			String approvers = "";
			String requirementStatus = "";
			String requirementPriority = "";
			String requirementOwner = "";
			int requirementPctComplete = 0;
			String requirementExternalUrl = "";
			String traceTo = "";
			String traceFrom = "";
			String userDefinedAttributes = "";
			String testingStatus = "";
			int deleted = 0;
			String folderPath = "";
			String createdBy = "";
			String createdDt = "";
			String lastModifiedBy ="";
			String requirementTypeName = "";
			
			//Requirement requirement = null;
			
			
			
			while (rs.next()) {
				requirementId = rs.getInt("id");
				folderId = rs.getInt("folder_id");
				requirementTypeId = rs.getInt("requirement_type_id");
				projectId = rs.getInt("project_id");
				requirementName = rs.getString("name");
				requirementDescription = rs.getString("description");
				requirementTag = rs.getString("tag");
				requirementFullTag = rs.getString("full_tag");
				version = rs.getInt("version");
				approvedByAllDt = rs.getString("approved_by_all_dt");
				approvers = rs.getString("approvers");
				requirementStatus = rs.getString("status");
				requirementPriority = rs.getString("priority");
				requirementOwner = rs.getString("owner");
				String requirementLockedBy = rs.getString("locked_by");
				requirementPctComplete = rs.getInt("pct_complete");
				requirementExternalUrl = rs.getString("external_url");
				traceTo = rs.getString("trace_to");
				traceFrom = rs.getString("trace_from");
				userDefinedAttributes = rs
						.getString("user_defined_attributes");
				testingStatus = rs.getString("testing_status");
				deleted = rs.getInt("deleted");
				folderPath = rs.getString("folder_path");
				createdBy = rs.getString("created_by");
				createdDt = rs.getString("created_dt");
				lastModifiedBy = rs.getString("last_modified_by");
				// lastModifiedDt = rs.getDate("last_modified_by");
				requirementTypeName = rs
						.getString("requirement_type_name");

				
				Requirement requirement = new Requirement(requirementId,
						requirementTypeId, folderId, projectId,
						requirementName, requirementDescription,
						requirementTag, requirementFullTag, version,
						approvedByAllDt, approvers, requirementStatus,
						requirementPriority, requirementOwner, requirementLockedBy,
						requirementPctComplete, requirementExternalUrl,
						traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
						createdBy, lastModifiedBy, requirementTypeName, createdDt);

				// if the user does not have read permissions on this requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(privileges.contains("readRequirementsInFolder" 
						+ folderId))){
					requirement.redact();
				}
				report.add(requirement);
				
				
			}


			rs.close();
			prepStmt.close();


		} catch (Exception e) {
			
			e.printStackTrace();
		} 
		return (report);
	}
	

	public static ArrayList runTraceTreeReport(java.sql.Connection con, int  userId, int projectId, int folderId,
			String filter, int traceTreeDepth, String danglingSearch,
			String orphanSearch, String completedSearch,
			String incompleteSearch, String suspectUpStreamSearch,
			String suspectDownStreamSearch, String lockedSearch, String includeSubFoldersSearch, 
			int inRTBaselineSearch, int changedAfterRTBaselineSearch, String testingStatusSearch,
			String nameSearch,
			String descriptionSearch, String ownerSearch,
			String externalURLSearch, String approvedBySearch,
			String rejectedBySearch, String pendingBySearch,
			String traceToSearch, String traceFromSearch, String statusSearch,
			String prioritySearch, String pctCompleteSearch,
			String customAttributeSearch, String databaseType, int maxRowsInTraceTree,
			HttpServletRequest request, String displayRequirementType) {
		
		if (displayRequirementType == null){
			displayRequirementType = "all";
		}

		ArrayList traceTreeRows = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// here is the sql that we will use to get reqs that trace to this
			// requirement.
			String traceTreeSQL = "";
			int rowsInTraceTree =0;
				traceTreeSQL = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id,"
					+ " r.name, "
					+ " r.description, r.tag, r.full_tag,"
					+ " r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\","
					+ " r.approvers  ,"
					+ " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
					+ " r.external_url, r.trace_to, r.trace_from,  r.user_defined_attributes, r.testing_status, "
					+ " r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\"," 
					+ " r.last_modified_by, "
					+ " r.last_modified_dt, rt.name \"requirement_type_name\", t.suspect, " 
					+ " t.description \"traceDescription\" , t.id \"traceId\" "
					+ " FROM gr_requirements r , gr_requirement_types rt, gr_traces t, gr_folders f "
					+ " where t.to_requirement_id = ? "
					+ " and t.from_requirement_id = r.id "
					+ " and r.requirement_type_id = rt.id " 
					+ " and r.folder_id = f.id "
					+ " and r.deleted= 0 " +
							" order by r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";


			String sortBy = "";
			String sortByType = "";
			
			LinkedHashSet privileges = getUserPrivileges(con, userId);
			String sql = ReportUtil.buildSQL(projectId, folderId, filter, danglingSearch,
				orphanSearch, completedSearch, incompleteSearch,
				suspectUpStreamSearch, suspectDownStreamSearch,
				 lockedSearch, includeSubFoldersSearch, inRTBaselineSearch,
				changedAfterRTBaselineSearch,  testingStatusSearch, nameSearch,
				descriptionSearch, ownerSearch, externalURLSearch,
				approvedBySearch, rejectedBySearch, pendingBySearch,
				traceToSearch, traceFromSearch, statusSearch,
				prioritySearch, pctCompleteSearch, customAttributeSearch,
				sortBy, sortByType,  databaseType);

			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();

			int requirementId = 0;
			int requirementTypeId = 0;
			projectId = 0;
			String requirementName = "";
			String requirementDescription = "";
			String requirementTag = "";
			String requirementFullTag = "";
			int version = 0;
			String approvedByAllDt = "";
			String approvers = "";
			String requirementStatus = "";
			String requirementPriority = "";
			String requirementOwner = "";
			String requirementLockedBy = "";
			int requirementPctComplete = 0;
			String requirementExternalUrl = "";
			String traceTo = "";
			String traceFrom = "";
			String userDefinedAttributes = "";
			String testingStatus = "";
			int deleted = 0;
			String folderPath = "";
			String createdBy = "";
			String createdDt = "";
			String lastModifiedBy ="";
			String requirementTypeName = "";
			
			Requirement requirement = null;
			Requirement requirement2 = null;
			Requirement requirement3 = null;
			Requirement requirement4 = null;
			Requirement requirement5 = null;
			Requirement requirement6 = null;
			Requirement requirement7 = null;
			
			TraceTreeRow traceTreeRow = null;
			TraceTreeRow traceTreeRow2 = null;
			TraceTreeRow traceTreeRow3 = null;
			TraceTreeRow traceTreeRow4 = null;
			TraceTreeRow traceTreeRow5 = null;
			TraceTreeRow traceTreeRow6 = null;
			TraceTreeRow traceTreeRow7 = null;
			
			PreparedStatement prepStmt2 = con.prepareStatement(traceTreeSQL);;
			ResultSet rs2 = null;
			PreparedStatement prepStmt3 = con.prepareStatement(traceTreeSQL);;
			ResultSet rs3 = null;
			PreparedStatement prepStmt4 = con.prepareStatement(traceTreeSQL);;
			ResultSet rs4 = null;
			PreparedStatement prepStmt5 = con.prepareStatement(traceTreeSQL);;
			ResultSet rs5 = null;
			PreparedStatement prepStmt6 = con.prepareStatement(traceTreeSQL);;
			ResultSet rs6 = null;
			PreparedStatement prepStmt7 = con.prepareStatement(traceTreeSQL);;
			ResultSet rs7 = null;
			
			// lets execute the filtered SQL to get the root level requirements.
			// we will build a trace tree for each one of these requirements.
			while (rs.next()) {
				requirementId = rs.getInt("id");
				folderId = rs.getInt("folder_id");
				 requirementTypeId = rs.getInt("requirement_type_id");
				projectId = rs.getInt("project_id");
				 requirementName = rs.getString("name");
				 requirementDescription = rs.getString("description");
				 requirementTag = rs.getString("tag");
				 requirementFullTag = rs.getString("full_tag");
				 version = rs.getInt("version");
				 approvedByAllDt = rs.getString("approved_by_all_dt");
				 approvers = rs.getString("approvers");
				 requirementStatus = rs.getString("status");
				 requirementPriority = rs.getString("priority");
				 requirementOwner = rs.getString("owner");
				 requirementLockedBy = rs.getString("locked_by");
				 requirementPctComplete = rs.getInt("pct_complete");
				 requirementExternalUrl = rs.getString("external_url");
				 traceTo = rs.getString("trace_to");
				 traceFrom = rs.getString("trace_from");
				 userDefinedAttributes = rs
						.getString("user_defined_attributes");
				 testingStatus = rs.getString("testing_status");
				 deleted = rs.getInt("deleted");
				 folderPath = rs.getString("folder_path");
				 createdBy = rs.getString("created_by");
				 createdDt = rs.getString("created_dt");
				 lastModifiedBy = rs.getString("last_modified_by");
				// lastModifiedDt = rs.getDate("last_modified_by");
				 requirementTypeName = rs
						.getString("requirement_type_name");
				// in the first row (first level of a trace tree) there are no trace objects.
				// hence this filed is empty. as you go further down the trace tree you start
				// seeing trace object.s
				String traceDescription = " ";
				int traceId = 0;
				
				requirement = new Requirement(requirementId,
					requirementTypeId, folderId, projectId,
					requirementName, requirementDescription,
					requirementTag, requirementFullTag, version,
					approvedByAllDt, approvers, requirementStatus,
					requirementPriority, requirementOwner, requirementLockedBy,
					requirementPctComplete, requirementExternalUrl,
					traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
					createdBy, lastModifiedBy, requirementTypeName, createdDt);
				

				// if the user does not have read permissions on this requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				
				if (!(privileges.contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}
				traceTreeRow = new TraceTreeRow(1, 0, traceId, traceDescription, requirement);
				// lets see if this TraceTree Requirements is one of the display Requirement Types.
		   		// i.e the user chose to either see 'all' requirements of the TraceTree or 
		   		// see only the ones he / she decided to see. 
				// we add only the resulting row to the arraylist only if the user choose to see ALL requirement types
				// or if this requirement fits the bill.
		   		if (displayRequirementType.equals("all")){
		   			traceTreeRows.add(traceTreeRow);
		   		}
		   		else {
		   			// means some display restrictions are in place
		   			if (requirement.getRequirementFullTag().startsWith(displayRequirementType)){
		   				// every req full tag is requriementtypeshortname-number. eg BR-11.
		   				// so if the user chose to see only Product Requirements, then the displayRequirementType will be PR
		   				// in this case, lets skip it.
		   				traceTreeRows.add(traceTreeRow);
		   			}
		   		}
		   		
				
				if (maxRowsInTraceTree <= ++rowsInTraceTree){
					rs.close();
					prepStmt.close();
					con.close();
					request.setAttribute("maxRowsInTraceTreeExceeded", "true");
					return (traceTreeRows);
				}
				if (traceTreeDepth > 1) {
					// This means that the user requested a depth > 1 .i.e 2 or
					// 3 or 4 ...7
					// showing 2nd level reqs.

					// Now for each of these requirements, lets get the
					// Requirements that trace up to them.
					// Second Level Trace Tree Data
					
					prepStmt2.setInt(1, requirement.getRequirementId());
					rs2 = prepStmt2.executeQuery();

					while (rs2.next()) {
						requirementId = rs2.getInt("id");
						folderId = rs2.getInt("folder_id");
						requirementTypeId = rs2.getInt("requirement_type_id");
						projectId = rs2.getInt("project_id");
						requirementName = rs2.getString("name");
						requirementDescription = rs2.getString("description");
						requirementTag = rs2.getString("tag");
						requirementFullTag = rs2.getString("full_tag");
						version = rs2.getInt("version");
						approvedByAllDt = rs2.getString("approved_by_all_dt");
						approvers = rs2.getString("approvers");
						requirementStatus = rs2.getString("status");
						requirementPriority = rs2.getString("priority");
						requirementOwner = rs2.getString("owner");
						requirementLockedBy = rs2.getString("locked_by");
						requirementPctComplete = rs2.getInt("pct_complete");
						requirementExternalUrl = rs2.getString("external_url");
						traceTo = rs2.getString("trace_to");
						traceFrom = rs2.getString("trace_from");
						userDefinedAttributes = rs2
								.getString("user_defined_attributes");
						testingStatus = rs2.getString("testing_status");
						deleted = rs2.getInt("deleted");
						folderPath = rs2.getString("folder_path");
						createdBy = rs2.getString("created_by");
						createdDt = rs2.getString("created_dt");
						lastModifiedBy = rs2.getString("last_modified_by");
						// lastModifiedDt = rs.getDate("last_modified_by");
						requirementTypeName = rs2
								.getString("requirement_type_name");
						traceDescription = rs2.getString("traceDescription");
						traceId = rs2.getInt("traceId");
						
						requirement2 = new Requirement(
								requirementId, requirementTypeId, folderId,
								projectId, requirementName,
								requirementDescription, requirementTag,
								requirementFullTag, version, approvedByAllDt,
								approvers, requirementStatus,
								requirementPriority, requirementOwner, requirementLockedBy,
								requirementPctComplete, requirementExternalUrl,
								traceTo, traceFrom, userDefinedAttributes, testingStatus,
								deleted, folderPath, createdBy, lastModifiedBy,
								requirementTypeName, createdDt);

						// if the user does not have read permissions on this requirement,
						// lets redact it. i.e. remove all sensitive infor from it.
						if (!(privileges.contains("readRequirementsInFolder" 
								+ requirement2.getFolderId()))){
							requirement2.redact();
						}
						
						traceTreeRow2 = new TraceTreeRow(2, rs2.getInt("suspect"), rs2.getInt("traceId"), traceDescription, requirement2);
						// lets see if this TraceTree Requirements is one of the display Requirement Types.
				   		// i.e the user chose to either see 'all' requirements of the TraceTree or 
				   		// see only the ones he / she decided to see. 
						// we add only the resulting row to the arraylist only if the user choose to see ALL requirement types
						// or if this requirement fits the bill.
				   		if (displayRequirementType.equals("all")){
				   			traceTreeRows.add(traceTreeRow2);
				   		}
				   		else {
				   			// means some display restrictions are in place
				   			if (requirement2.getRequirementFullTag().startsWith(displayRequirementType)){
				   				// every req full tag is requriementtypeshortname-number. eg BR-11.
				   				// so if the user chose to see only Product Requirements, then the displayRequirementType will be PR
				   				// in this case, lets skip it.
				   				traceTreeRows.add(traceTreeRow2);
				   			}
				   		}
						
						if (maxRowsInTraceTree <= ++rowsInTraceTree){
							rs.close();
							prepStmt.close();
							rs2.close();
							prepStmt2.close();
							con.close();
							request.setAttribute("maxRowsInTraceTreeExceeded", "true");
							return (traceTreeRows);
						}
						if (traceTreeDepth > 2) {
							// This means that the user requested a depth > 2
							// .i.e 3 or 4 ...7

							// Now for each of these requirements, lets get the
							// Requirements that trace up to them.
							// Third Level Trace Tree Data

							
							prepStmt3.setInt(1, requirement2.getRequirementId());
							rs3 = prepStmt3.executeQuery();

							while (rs3.next()) {
								requirementId = rs3.getInt("id");
								folderId = rs3.getInt("folder_id");
								requirementTypeId = rs3
										.getInt("requirement_type_id");
								projectId = rs3.getInt("project_id");
								requirementName = rs3.getString("name");
								requirementDescription = rs3
										.getString("description");
								requirementTag = rs3.getString("tag");
								requirementFullTag = rs3.getString("full_tag");
								version = rs3.getInt("version");
								approvedByAllDt = rs3
										.getString("approved_by_all_dt");
								approvers = rs3.getString("approvers");
								requirementStatus = rs3.getString("status");
								requirementPriority = rs3.getString("priority");
								requirementOwner = rs3.getString("owner");
								requirementLockedBy = rs3.getString("locked_by");
								requirementPctComplete = rs3
										.getInt("pct_complete");
								requirementExternalUrl = rs3
										.getString("external_url");
								traceTo = rs3.getString("trace_to");
								traceFrom = rs3.getString("trace_from");
								userDefinedAttributes = rs3
										.getString("user_defined_attributes");
								testingStatus = rs3.getString("testing_status");
								deleted = rs3.getInt("deleted");
								folderPath = rs3.getString("folder_path");
								createdBy = rs3.getString("created_by");
								createdDt = rs3.getString("created_dt");
								lastModifiedBy = rs3
										.getString("last_modified_by");
								// lastModifiedDt =
								// rs.getDate("last_modified_by");
								requirementTypeName = rs3
										.getString("requirement_type_name");
								traceDescription = rs3.getString("traceDescription");
								
								requirement3 = new Requirement(
										requirementId, requirementTypeId,
										folderId, projectId, requirementName,
										requirementDescription, requirementTag,
										requirementFullTag, version,
										approvedByAllDt, approvers,
										requirementStatus, requirementPriority,
										requirementOwner, requirementLockedBy,
										requirementPctComplete,
										requirementExternalUrl, traceTo,
										traceFrom, userDefinedAttributes, testingStatus,
										deleted,folderPath, createdBy, lastModifiedBy,
										requirementTypeName, createdDt);

								// if the user does not have read permissions on this requirement,
								// lets redact it. i.e. remove all sensitive infor from it.
								if (!(privileges.contains("readRequirementsInFolder" 
										+ requirement3.getFolderId()))){
									requirement3.redact();
								}
								
								traceTreeRow3 = new TraceTreeRow(
										3, rs3.getInt("suspect") , rs3.getInt("traceId"),  traceDescription, requirement3);
								// lets see if this TraceTree Requirements is one of the display Requirement Types.
						   		// i.e the user chose to either see 'all' requirements of the TraceTree or 
						   		// see only the ones he / she decided to see. 
								// we add only the resulting row to the arraylist only if the user choose to see ALL requirement types
								// or if this requirement fits the bill.
						   		if (displayRequirementType.equals("all")){
						   			traceTreeRows.add(traceTreeRow3);
						   		}
						   		else {
						   			// means some display restrictions are in place
						   			if (requirement3.getRequirementFullTag().startsWith(displayRequirementType)){
						   				// every req full tag is requriementtypeshortname-number. eg BR-11.
						   				// so if the user chose to see only Product Requirements, then the displayRequirementType will be PR
						   				// in this case, lets skip it.
						   				traceTreeRows.add(traceTreeRow3);
						   			}
						   		}
								
								if (maxRowsInTraceTree <= ++rowsInTraceTree){
									rs.close();
									prepStmt.close();
									rs2.close();
									prepStmt2.close();
									rs3.close();
									prepStmt3.close();
									con.close();
									request.setAttribute("maxRowsInTraceTreeExceeded", "true");
									return (traceTreeRows);
								}
								if (traceTreeDepth > 3) {
									// This means that the user requested a
									// depth > 3 .i.e 4 or 5 ...7

									// Now for each of these requirements, lets
									// get the Requirements that trace up to
									// them.
									// Fourth Level Trace Tree Data.
									
									prepStmt4.setInt(1, requirement3
											.getRequirementId());
									rs4 = prepStmt4.executeQuery();

									while (rs4.next()) {
										requirementId = rs4.getInt("id");
										folderId = rs4.getInt("folder_id");
										requirementTypeId = rs4
												.getInt("requirement_type_id");
										projectId = rs4.getInt("project_id");
										requirementName = rs4.getString("name");
										requirementDescription = rs4
												.getString("description");
										requirementTag = rs4.getString("tag");
										requirementFullTag = rs4
												.getString("full_tag");
										version = rs4.getInt("version");
										approvedByAllDt = rs4
												.getString("approved_by_all_dt");
										approvers = rs4.getString("approvers");
										requirementStatus = rs4
												.getString("status");
										requirementPriority = rs4
												.getString("priority");
										requirementOwner = rs4
												.getString("owner");
										requirementLockedBy = rs4.getString("locked_by");
										requirementPctComplete = rs4
												.getInt("pct_complete");
										requirementExternalUrl = rs4
												.getString("external_url");
										traceTo = rs4.getString("trace_to");
										traceFrom = rs4.getString("trace_from");
										userDefinedAttributes = rs4
												.getString("user_defined_attributes");
										testingStatus = rs4.getString("testing_status");
										deleted = rs4.getInt("deleted");
										folderPath = rs4.getString("folder_path");
										createdBy = rs4.getString("created_by");
										createdDt = rs4.getString("created_dt");
										lastModifiedBy = rs4
												.getString("last_modified_by");
										// lastModifiedDt =
										// rs.getDate("last_modified_by");
										requirementTypeName = rs4
												.getString("requirement_type_name");
										traceDescription = rs4.getString("traceDescription");
										
										requirement4 = new Requirement(
												requirementId,
												requirementTypeId, folderId,
												projectId, requirementName,
												requirementDescription,
												requirementTag,
												requirementFullTag, version,
												approvedByAllDt, approvers,
												requirementStatus,
												requirementPriority,
												requirementOwner, requirementLockedBy,
												requirementPctComplete,
												requirementExternalUrl,
												traceTo, traceFrom,
												userDefinedAttributes, testingStatus, deleted, folderPath,
												createdBy, lastModifiedBy,
												requirementTypeName, createdDt);
										
										// if the user does not have read permissions on this requirement,
										// lets redact it. i.e. remove all sensitive infor from it.
										if (!(privileges.contains("readRequirementsInFolder" 
												+ requirement4.getFolderId()))){
											requirement4.redact();
										}
										traceTreeRow4 = new TraceTreeRow(
												4, rs4.getInt("suspect") , rs4.getInt("traceId"), traceDescription,
												requirement4);
										// lets see if this TraceTree Requirements is one of the display Requirement Types.
								   		// i.e the user chose to either see 'all' requirements of the TraceTree or 
								   		// see only the ones he / she decided to see. 
										// we add only the resulting row to the arraylist only if the user choose to see ALL requirement types
										// or if this requirement fits the bill.
								   		if (displayRequirementType.equals("all")){
								   			traceTreeRows.add(traceTreeRow4);
								   		}
								   		else {
								   			// means some display restrictions are in place
								   			if (requirement4.getRequirementFullTag().startsWith(displayRequirementType)){
								   				// every req full tag is requriementtypeshortname-number. eg BR-11.
								   				// so if the user chose to see only Product Requirements, then the displayRequirementType will be PR
								   				// in this case, lets skip it.
								   				traceTreeRows.add(traceTreeRow4);
								   			}
								   		}
										
										if (maxRowsInTraceTree <= ++rowsInTraceTree){
											rs.close();
											prepStmt.close();
											rs2.close();
											prepStmt2.close();
											rs3.close();
											prepStmt3.close();
											rs4.close();
											prepStmt4.close();
											con.close();
											request.setAttribute("maxRowsInTraceTreeExceeded", "true");
											return (traceTreeRows);
										}
										if (traceTreeDepth > 4) {
											// This means that the user
											// requested a depth > 4 .i.e 5 or 6
											// or 7

											// Now for each of these
											// requirements, lets get the
											// Requirements that trace up to
											// them.
											// Fifth Level Trace Tree Data.
										
											prepStmt5.setInt(1, requirement4.getRequirementId());
											rs5 = prepStmt5.executeQuery();

											while (rs5.next()) {
												requirementId = rs5
														.getInt("id");
												folderId = rs5
														.getInt("folder_id");
												requirementTypeId = rs5
														.getInt("requirement_type_id");
												projectId = rs5
														.getInt("project_id");
												requirementName = rs5
														.getString("name");
												requirementDescription = rs5
														.getString("description");
												requirementTag = rs5
														.getString("tag");
												requirementFullTag = rs5
														.getString("full_tag");
												version = rs5.getInt("version");
												approvedByAllDt = rs5
														.getString("approved_by_all_dt");
												approvers = rs5
														.getString("approvers");
												requirementStatus = rs5
														.getString("status");
												requirementPriority = rs5
														.getString("priority");
												requirementOwner = rs5
														.getString("owner");
												requirementLockedBy = rs5.getString("locked_by");
												requirementPctComplete = rs5
														.getInt("pct_complete");
												requirementExternalUrl = rs5
														.getString("external_url");
												traceTo = rs5
														.getString("trace_to");
												traceFrom = rs5
														.getString("trace_from");
												userDefinedAttributes = rs5
														.getString("user_defined_attributes");
												testingStatus = rs5.getString("testing_status");
												deleted = rs5.getInt("deleted");
												folderPath = rs5.getString("folder_path");
												createdBy = rs5
														.getString("created_by");
												createdDt = rs5.getString("created_dt");
												lastModifiedBy = rs5
														.getString("last_modified_by");
												// lastModifiedDt =
												// rs.getDate("last_modified_by");
												requirementTypeName = rs5
														.getString("requirement_type_name");
												traceDescription = rs5.getString("traceDescription");
												
												requirement5 = new Requirement(
														requirementId,
														requirementTypeId,
														folderId, projectId,
														requirementName,
														requirementDescription,
														requirementTag,
														requirementFullTag,
														version,
														approvedByAllDt,
														approvers,
														requirementStatus,
														requirementPriority,
														requirementOwner,requirementLockedBy,
														requirementPctComplete,
														requirementExternalUrl,
														traceTo, traceFrom,
														userDefinedAttributes,
														testingStatus,
														deleted, folderPath, createdBy,
														lastModifiedBy,
														requirementTypeName, createdDt);
												
												// if the user does not have read permissions on this requirement,
												// lets redact it. i.e. remove all sensitive infor from it.
												if (!(privileges.contains("readRequirementsInFolder" 
														+ requirement5.getFolderId()))){
													requirement5.redact();
												}
												
												traceTreeRow5 = new TraceTreeRow(
														5,
														rs5.getInt("suspect") , rs5.getInt("traceId"), traceDescription, 
														requirement5);
												// lets see if this TraceTree Requirements is one of the display Requirement Types.
										   		// i.e the user chose to either see 'all' requirements of the TraceTree or 
										   		// see only the ones he / she decided to see. 
												// we add only the resulting row to the arraylist only if the user choose to see ALL requirement types
												// or if this requirement fits the bill.
										   		if (displayRequirementType.equals("all")){
										   			traceTreeRows.add(traceTreeRow5);
										   		}
										   		else {
										   			// means some display restrictions are in place
										   			if (requirement5.getRequirementFullTag().startsWith(displayRequirementType)){
										   				// every req full tag is requriementtypeshortname-number. eg BR-11.
										   				// so if the user chose to see only Product Requirements, then the displayRequirementType will be PR
										   				// in this case, lets skip it.
										   				traceTreeRows.add(traceTreeRow5);
										   			}
										   		}
												
												if (maxRowsInTraceTree <= ++rowsInTraceTree){
													rs.close();
													prepStmt.close();
													rs2.close();
													prepStmt2.close();
													rs3.close();
													prepStmt3.close();
													rs4.close();
													prepStmt4.close();
													rs5.close();
													prepStmt5.close();
													con.close();
													request.setAttribute("maxRowsInTraceTreeExceeded", "true");
													return (traceTreeRows);
												}
												if (traceTreeDepth > 5) {
													// This means that the user
													// requested a depth > 5
													// .i.e 6 or 7

													// Now for each of these
													// requirements, lets get
													// the Requirements that
													// trace up to them.
													// Sixth Level Trace Tree
													// Data.
													
													prepStmt6
															.setInt(
																	1,
																	requirement5
																			.getRequirementId());
													rs6 = prepStmt6
															.executeQuery();

													while (rs6.next()) {
														requirementId = rs6
																.getInt("id");
														folderId = rs6
																.getInt("folder_id");
														requirementTypeId = rs6
																.getInt("requirement_type_id");
														projectId = rs6
																.getInt("project_id");
														requirementName = rs6
																.getString("name");
														requirementDescription = rs6
																.getString("description");
														requirementTag = rs6
																.getString("tag");
														requirementFullTag = rs6
																.getString("full_tag");
														version = rs6
																.getInt("version");
														approvedByAllDt = rs6
																.getString("approved_by_all_dt");
														approvers = rs6
																.getString("approvers");
														requirementStatus = rs6
																.getString("status");
														requirementPriority = rs6
																.getString("priority");
														requirementOwner = rs6
																.getString("owner");
														requirementLockedBy = rs6.getString("locked_by");
														requirementPctComplete = rs6
																.getInt("pct_complete");
														requirementExternalUrl = rs6
																.getString("external_url");
														traceTo = rs6
																.getString("trace_to");
														traceFrom = rs6
																.getString("trace_from");
														userDefinedAttributes = rs6
																.getString("user_defined_attributes");
														testingStatus = rs6.getString("testing_status");
														deleted = rs6
																.getInt("deleted");
														folderPath = rs6.getString("folder_path");
														createdBy = rs6
																.getString("created_by");
														createdDt = rs6.getString("created_dt");
														lastModifiedBy = rs6
																.getString("last_modified_by");
														// lastModifiedDt =
														// rs.getDate("last_modified_by");
														requirementTypeName = rs6
																.getString("requirement_type_name");
														traceDescription = rs6.getString("traceDescription");
														
														requirement6 = new Requirement(
																requirementId,
																requirementTypeId,
																folderId,
																projectId,
																requirementName,
																requirementDescription,
																requirementTag,
																requirementFullTag,
																version,
																approvedByAllDt,
																approvers,
																requirementStatus,
																requirementPriority,
																requirementOwner, requirementLockedBy,
																requirementPctComplete,
																requirementExternalUrl,
																traceTo,
																traceFrom,
																userDefinedAttributes,
																testingStatus,
																deleted,
																folderPath,
																createdBy,
																lastModifiedBy,
																requirementTypeName, 
																createdDt);
														
														// if the user does not have read permissions on this requirement,
														// lets redact it. i.e. remove all sensitive infor from it.
														if (!(privileges.contains("readRequirementsInFolder" 
																+ requirement6.getFolderId()))){
															requirement6.redact();
														}
														traceTreeRow6 = new TraceTreeRow(
																6,
																rs6.getInt("suspect") , rs6.getInt("traceId"), traceDescription,
																requirement6);
														// lets see if this TraceTree Requirements is one of the display Requirement Types.
												   		// i.e the user chose to either see 'all' requirements of the TraceTree or 
												   		// see only the ones he / she decided to see. 
														// we add only the resulting row to the arraylist only if the user choose to see ALL requirement types
														// or if this requirement fits the bill.
												   		if (displayRequirementType.equals("all")){
												   			traceTreeRows.add(traceTreeRow6);
												   		}
												   		else {
												   			// means some display restrictions are in place
												   			if (requirement6.getRequirementFullTag().startsWith(displayRequirementType)){
												   				// every req full tag is requriementtypeshortname-number. eg BR-11.
												   				// so if the user chose to see only Product Requirements, then the displayRequirementType will be PR
												   				// in this case, lets skip it.
												   				traceTreeRows.add(traceTreeRow6);
												   			}
												   		}
														
														if (maxRowsInTraceTree <= ++rowsInTraceTree){
															rs.close();
															prepStmt.close();
															rs2.close();
															prepStmt2.close();
															rs3.close();
															prepStmt3.close();
															rs4.close();
															prepStmt4.close();
															rs5.close();
															prepStmt5.close();
															rs6.close();
															prepStmt6.close();
															con.close();
															request.setAttribute("maxRowsInTraceTreeExceeded", "true");
															return (traceTreeRows);
														}
														if (traceTreeDepth > 6) {
															// This means that
															// the user
															// requested a depth
															// > 6 .i.e 7

															// Now for each of
															// these
															// requirements,
															// lets get the
															// Requirements that
															// trace up to them.
															// Seventh Level
															// Trace Tree Data.
															
															prepStmt7
																	.setInt(
																			1,
																			requirement6
																					.getRequirementId());
															rs7 = prepStmt7
																	.executeQuery();

															while (rs7.next()) {
																requirementId = rs7
																		.getInt("id");
																folderId = rs7
																		.getInt("folder_id");
																requirementTypeId = rs7
																		.getInt("requirement_type_id");
																projectId = rs7
																		.getInt("project_id");
																requirementName = rs7
																		.getString("name");
																requirementDescription = rs7
																		.getString("description");
																requirementTag = rs7
																		.getString("tag");
																requirementFullTag = rs7
																		.getString("full_tag");
																version = rs7
																		.getInt("version");
																approvedByAllDt = rs7
																		.getString("approved_by_all_dt");
																approvers = rs7
																		.getString("approvers");
																requirementStatus = rs7
																		.getString("status");
																requirementPriority = rs7
																		.getString("priority");
																requirementOwner = rs7
																		.getString("owner");
																requirementLockedBy = rs7.getString("locked_by");
																requirementPctComplete = rs7
																		.getInt("pct_complete");
																requirementExternalUrl = rs7
																		.getString("external_url");
																traceTo = rs7
																		.getString("trace_to");
																traceFrom = rs7
																		.getString("trace_from");
																userDefinedAttributes = rs7
																		.getString("user_defined_attributes");
																testingStatus = rs7.getString("testing_status");
																deleted = rs7
																		.getInt("deleted");
																folderPath = rs7.getString("folder_path");
																createdBy = rs7
																		.getString("created_by");
																createdDt = rs7.getString("created_dt");
																lastModifiedBy = rs7
																		.getString("last_modified_by");
																// lastModifiedDt
																// =
																// rs.getDate("last_modified_by");
																requirementTypeName = rs7
																		.getString("requirement_type_name");
																traceDescription = rs7.getString("traceDescription");
																
																requirement7 = new Requirement(
																		requirementId,
																		requirementTypeId,
																		folderId,
																		projectId,
																		requirementName,
																		requirementDescription,
																		requirementTag,
																		requirementFullTag,
																		version,
																		approvedByAllDt,
																		approvers,
																		requirementStatus,
																		requirementPriority,
																		requirementOwner, requirementLockedBy,
																		requirementPctComplete,
																		requirementExternalUrl,
																		traceTo,
																		traceFrom,
																		userDefinedAttributes,
																		testingStatus,
																		deleted,
																		folderPath,
																		createdBy,
																		lastModifiedBy,
																		requirementTypeName, 
																		createdDt);
																
																// if the user does not have read permissions on this requirement,
																// lets redact it. i.e. remove all sensitive infor from it.
																if (!(privileges.contains("readRequirementsInFolder" 
																		+ requirement7.getFolderId()))){
																	requirement7.redact();
																}
																traceTreeRow7 = new TraceTreeRow(
																		7,
																		rs7.getInt("suspect") , rs7.getInt("traceId"), traceDescription,
																		requirement7);
																// lets see if this TraceTree Requirements is one of the display Requirement Types.
														   		// i.e the user chose to either see 'all' requirements of the TraceTree or 
														   		// see only the ones he / she decided to see. 
																// we add only the resulting row to the arraylist only if the user choose to see ALL requirement types
																// or if this requirement fits the bill.
														   		if (displayRequirementType.equals("all")){
														   			traceTreeRows.add(traceTreeRow7);
														   		}
														   		else {
														   			// means some display restrictions are in place
														   			if (requirement7.getRequirementFullTag().startsWith(displayRequirementType)){
														   				// every req full tag is requriementtypeshortname-number. eg BR-11.
														   				// so if the user chose to see only Product Requirements, then the displayRequirementType will be PR
														   				// in this case, lets skip it.
														   				traceTreeRows.add(traceTreeRow7);
														   			}
														   		}
																
																if (maxRowsInTraceTree <= ++rowsInTraceTree){
																	rs.close();
																	prepStmt.close();
																	rs2.close();
																	prepStmt2.close();
																	rs3.close();
																	prepStmt3.close();
																	rs4.close();
																	prepStmt4.close();
																	rs5.close();
																	prepStmt5.close();
																	rs6.close();
																	prepStmt6.close();
																	rs7.close();
																	prepStmt7.close();
																	con.close();
																	request.setAttribute("maxRowsInTraceTreeExceeded", "true");
																	return (traceTreeRows);
																}
															}
														}
													}
													// end of traceTreeDepth>6
													// i.e. showing Seventh
													// Level Trace Tree Data

												}
												// end of traceTreeDepth>5 i.e.
												// showing 6th Level Trace Tree
												// Data
											}
										}
										// end of traceTreeDepth>4 i.e. showing
										// Fifth Level Trace Tree Data
									}
									// end of traceTreeDepth>3 i.e. showing
									// Fourth Level Trace Tree Data
								}
							}
							// end of traceTreeDepth>2 i.e. showing Third Level
							// Trace Tree Data
						}
					}
					// end of traceTreeDepth>1 i.e showing SecondLevel Trace
					// Tree Data
				}
			}
			// end of First Level Trace Tree Data

			rs.close();
			prepStmt.close();
		
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
		}
		
		
		return (traceTreeRows);
	}


	public static String getAttributeName(java.sql.Connection con, int rTAttributeId){
		String attributeName = "";
		try {
			String sql = "select name  " + 
			" from gr_rt_attributes " +
			" where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTAttributeId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				attributeName = rs.getString("name");
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return (attributeName);
	}
	
	public  static LinkedHashSet getUserPrivileges(java.sql.Connection con, int userId){
		LinkedHashSet privileges = new LinkedHashSet();
		try {

			// get Privileges for this user.
			// we need to control this based on the user's account type.
			// if the account is 'expired', they get no permissions.
			// if the account is ' trial' or 'readWrite' they get permissions
			// per the settings in the database.
			// if the account is 'readOnly' they get ONLy read permissions, if such
			// permissions exist for this user in the db.
			String sql = "select u.user_type , p.billing_organization_id, rp.folder_id, rp.create_requirement, rp.read_requirement, " +
				" rp.update_requirement, rp.delete_requirement, " + 
				" rp.trace_requirement, " +
				" rp.approve_requirement , rp.update_attributes " + 
				" from gr_user_roles ur, gr_role_privs rp , gr_projects p, gr_users u" +
				" where ur.user_id= ? " + 
				" and ur.role_id = rp.role_id" +
				" and ur.project_id = p.id " +
				" and ur.user_id = u.id ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, userId);
			ResultSet rs = prepStmt.executeQuery();
		
			
			while (rs.next()){
				int folderId = rs.getInt("folder_id");
				int createRequirement = rs.getInt("create_requirement");
				int readRequirement = rs.getInt("read_requirement");
				int updateRequirement = rs.getInt("update_requirement");
				int deleteRequirement = rs.getInt("delete_requirement");
				int traceRequirement = rs.getInt("trace_requirement");
				int approveRequirement = rs.getInt("approve_requirement");
				String updateAttributes = rs.getString("update_attributes");
				
				String userType = rs.getString("user_type");
				int billingOrganizationId = rs.getInt("billing_organization_id");

				
				// if this is a Sponsored project, i.e some one  is paying for it every month
				// the we simply read the privs and add them to the list. 
				if (billingOrganizationId > 0 ){
					if (createRequirement == 1 ){
						privileges.add("createRequirementsInFolder" + folderId);
					}
					
					if (readRequirement == 1 ){
						privileges.add("readRequirementsInFolder" + folderId);
					}
					
					if (updateRequirement == 1 ){
						privileges.add("updateRequirementsInFolder" + folderId);
					}
					
					if (deleteRequirement == 1 ){
						privileges.add("deleteRequirementsInFolder" + folderId);
					}
					
					if (traceRequirement == 1 ){
						privileges.add("traceToRequirementsInFolder" + folderId);
						privileges.add("traceFromRequirementsInFolder" + folderId);
					}

	
					if (approveRequirement == 1 ){
						privileges.add("approveRequirementsInFolder" + folderId);
					}
					privileges.add(":#:updateAttributes" + updateAttributes + folderId + ":#:");
				}
				else {
					// for all non sponsored projects, we differentiate based on the type of license.
					// for read write users, set permissions as it states in the database..
					// trial users , during the trial period get the same settings as read/write users.
					// Once the trial period expires, they become 'expired' users, i. ie. user.getUserTyep
					// will return 'expired'.
					if ((userType.equals("readWrite")) || (userType.equals("trial"))){
						if (createRequirement == 1 ){
							privileges.add("createRequirementsInFolder" + folderId);
						}
						
						if (readRequirement == 1 ){
							privileges.add("readRequirementsInFolder" + folderId);
						}
						
						if (updateRequirement == 1 ){
							privileges.add("updateRequirementsInFolder" + folderId);
						}
						
						if (deleteRequirement == 1 ){
							privileges.add("deleteRequirementsInFolder" + folderId);
						}
						
						if (traceRequirement == 1 ){
							privileges.add("traceToRequirementsInFolder" + folderId);
							privileges.add("traceFromRequirementsInFolder" + folderId);
						}
	
						if (approveRequirement == 1 ){
							privileges.add("approveRequirementsInFolder" + folderId);
						}
						privileges.add(":#:updateAttributes" + updateAttributes + folderId + ":#:");
					}
					// for readOnly users, they can never become admins. so lets not give them the 
					// admin options.
					if (userType.equals("readOnly")){
						if (readRequirement == 1 ){
							privileges.add("readRequirementsInFolder" + folderId);
						}
					}
				}
			}
			prepStmt.close();
			rs.close();

			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return (privileges);
	}	
	
	
	

	   
    private static String exportListReportToWord
    	(HttpServletRequest request,java.sql.Connection con, 
        		ArrayList reportArrayList , String standardDisplay, String customAttributesDisplay, 
        		ScheduledReport scheduledReport, Report report, 
        		User scheduledReportOwner , String databaseType, String rootDataDirectory) 
    	throws ServletException, IOException {

    	String filename = "";
    	Project project = new Project(report.getProectId(), databaseType);
    	
    	try{
			// create a file name and set it to it.
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
    		String today =  sdf.format(cal.getTime());
    		filename = scheduledReportOwner.getFirstName() + " " + scheduledReportOwner.getLastName()  +" Report " + scheduledReport.getScheduledReportId() + " " + today + ".doc";    		
    		filename.replace(' ', '_');
    		
    		
	    	Document document = new Document();
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
			RtfWriter2.getInstance(document, baos);
			
			document.open();

			// lets add the heading page.
			
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			
			
			
			Chunk colon = new Chunk( " : " , new Font(Font.TIMES_ROMAN, 10));
			Chunk heading = new Chunk( "Report Title" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		Chunk value =  new Chunk( " List Report" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
    		
			heading = new Chunk( "Report Date" , new Font(Font.TIMES_ROMAN, 10));    		
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( today, new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
    		
			heading = new Chunk( "Report Generated By" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( scheduledReportOwner.getEmailId(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
			

			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

    		

    			heading = new Chunk( "Report Id" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(Integer.toString(report.getReportId()) , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
    			
    			heading = new Chunk( "Report Created By" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getCreatedByEmailId() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);


    			heading = new Chunk( "Report URL" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		String reportURLString = ProjectUtil.getURL(request, report.getReportId(), "report") ;
	    	    Anchor anchor1 = new Anchor(reportURLString, 
	    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
	    	    anchor1.setReference(reportURLString);
	    	    document.add(anchor1);
        		document.add(Chunk.NEWLINE);

    			heading = new Chunk( "Report Name" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getReportName() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
			    
    			heading = new Chunk( "Report Description" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getReportDescription() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
        		
    		
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

			
			heading = new Chunk( "Project Prefix" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getShortName(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
    		heading = new Chunk( "Project Name" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getProjectName(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
    		
    		heading = new Chunk( "Project Description" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getProjectDescription(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
			document.newPage();
			
	        Iterator i = reportArrayList.iterator();
	        while ( i.hasNext() ) {
	    		Requirement r = (Requirement) i.next();
	    		
	    		document.add(new Paragraph(" "));
	    		document.add(Chunk.NEWLINE);

	    		
	    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");
	    	    anchor1 = new Anchor(r.getRequirementFullTag(), 
	    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
	    	    anchor1.setReference(url);
	    	    document.add(anchor1);
	    	    
	    		document.add(Chunk.NEWLINE);
	    	    document.add(new Chunk("Version: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
	    		document.add(new Chunk( Integer.toString(r.getVersion()) , new Font(Font.TIMES_ROMAN, 10)));

	    		
	    		
	    		// lets customize the testingStatus color based on its value.
	    		if (standardDisplay.contains("testingStatus")) {
		    		document.add(Chunk.NEWLINE);
		    		document.add(new Chunk("Testing Status : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		Chunk testingStatusChunk = new Chunk( "   " + r.getTestingStatus() + "   " , new Font(Font.TIMES_ROMAN, 10));
		    		if (r.getTestingStatus().equals("Pending")){
		    			testingStatusChunk.setBackground(new Color(0xFF, 0xFF, 0x00));
		    		}
				    if (r.getTestingStatus().equals("Pass")){
		    			testingStatusChunk.setBackground(new Color(0x00, 0xFF, 0x00));			    	
		        	}
				    if (r.getTestingStatus().equals("Fail")){
		    			testingStatusChunk.setBackground(new Color(0xFF, 0x00, 0x00));
		        	}
		    		document.add(testingStatusChunk);
	    		}

	    		
	    		// lets customize the status color based on its value.
	    		if (standardDisplay.contains("status")) {
		    		document.add(Chunk.NEWLINE);
		    		document.add(new Chunk("Approval Status : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		Chunk statusChunk = new Chunk( "   " + r.getApprovalStatus() + "   " , new Font(Font.TIMES_ROMAN, 10));
		    		if (r.getApprovalStatus().equals("Draft")){
		    			statusChunk.setBackground(new Color(0xEE, 0x82, 0xEE));
		    		}
				    if (r.getApprovalStatus().equals("In Approval WorkFlow")){
		    			statusChunk.setBackground(new Color(0x99, 0xcc, 0xff));			    	
		        	}
				    if (r.getApprovalStatus().equals("Approved")){
		    			statusChunk.setBackground(new Color(0x57, 0xE9, 0x64));
		        	}
				    if (r.getApprovalStatus().equals("Rejected")){
		    			statusChunk.setBackground(new Color(0xFF, 0x00, 0x00));
		        	}			    
		    		document.add(statusChunk);
	    		}

	    		if (standardDisplay.contains("priority")) {
		    		document.add(Chunk.NEWLINE);
		    	    document.add(new Chunk("Priority: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementPriority() , new Font(Font.TIMES_ROMAN, 10)));
	    		}
	    		
	    		if (standardDisplay.contains("percentComplete")) {
		    		document.add(Chunk.NEWLINE);
		    		document.add(new Chunk("Percent Complete: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementPctComplete() + "%" , new Font(Font.TIMES_ROMAN, 10)));
		    		
	    		}
	    		
	    		document.add(Chunk.NEWLINE);
	    		document.add(new Chunk("Approved On: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
	    		document.add(new Chunk( r.getApprovedByAllDt() , new Font(Font.TIMES_ROMAN, 10)));

				
	    		if (standardDisplay.contains("lockedBy")) {
		    		document.add(Chunk.NEWLINE);
		    		document.add(new Chunk("Locked By: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementLockedBy() , new Font(Font.TIMES_ROMAN, 10)));
	    		}
				
				
			    // lets handle the approvers.
			    String pendingApprovers = "";
			    String approvedApprovers = "";
			    String rejectedApprovers = "";
			    
			    String[] approvers = new String[0] ;
			    if ((r.getApprovers()!= null) && (r.getApprovers().contains(","))){
			    	approvers = r.getApprovers().split(",");
			    }
			    
			    
			    for (int k=0;k<approvers.length;k++){
			    	if (approvers[k].contains("(P)")){
			    		pendingApprovers += approvers[k].replace("(P)","") + ", ";
			    	}	
			    	if (approvers[k].contains("(A)")){
			    		approvedApprovers += approvers[k].replace("(A)","") + ", ";
			    	}
			    	if (approvers[k].contains("(R)")){
			    		rejectedApprovers += approvers[k].replace("(R)","") + ", ";
			    	}
			    }
			    
			    // lets drop the last ,
			    if (pendingApprovers.contains(",")){
			    	pendingApprovers = (String) pendingApprovers.subSequence(0,pendingApprovers.lastIndexOf(","));
			    }			    
			    if (approvedApprovers.contains(",")){
			    	approvedApprovers = (String) approvedApprovers.subSequence(0,approvedApprovers.lastIndexOf(","));
			    }
			    if (rejectedApprovers.contains(",")){
			    	rejectedApprovers = (String) rejectedApprovers.subSequence(0,rejectedApprovers.lastIndexOf(","));
			    }

			    
			    if (standardDisplay.contains("approvedBy")) {
			    	document.add(Chunk.NEWLINE);
				    document.add(new Chunk("Approved By : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
				    document.add(new Chunk( approvedApprovers , new Font(Font.TIMES_ROMAN, 10)));			    
			    }
			    
			    if (standardDisplay.contains("rejectedBy")) {
			    	document.add(Chunk.NEWLINE);
				    document.add(new Chunk("Rejected By : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
				    document.add(new Chunk( rejectedApprovers , new Font(Font.TIMES_ROMAN, 10)));			    
			    }

			    if (standardDisplay.contains("pendingBy")) {
			    	document.add(Chunk.NEWLINE);
				    document.add(new Chunk("Pending By : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
				    document.add(new Chunk( pendingApprovers , new Font(Font.TIMES_ROMAN, 10)));			    
			    }

			
				
				if (standardDisplay.contains("owner")) {
					document.add(Chunk.NEWLINE);
					document.add(new Chunk("Owner : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementOwner() , new Font(Font.TIMES_ROMAN, 10)));
				}
	    		
	    		
	    		if (standardDisplay.contains("traceTo")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Trace To: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementTraceTo() , new Font(Font.TIMES_ROMAN, 10)));
	    		}
				
	    		if (standardDisplay.contains("traceFrom")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Trace From : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementTraceFrom() , new Font(Font.TIMES_ROMAN, 10)));
	    		}

	    		if (standardDisplay.contains("externalURL")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("External URL: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
					if ((r.getRequirementExternalUrl() !=null) && (!r.getRequirementExternalUrl().equals(""))){
			    	    anchor1 = new Anchor(r.getRequirementExternalUrl(), 
			    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
			    	    anchor1.setReference(r.getRequirementExternalUrl());
			    	    document.add(anchor1);
					}
	    		}

	    		if (standardDisplay.contains("folderPath")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Folder Path : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getFolderPath() , new Font(Font.TIMES_ROMAN, 10)));
	    		}
	    		
	    		if (standardDisplay.contains("baselines")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Baselines : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementBaselineString(databaseType) , new Font(Font.TIMES_ROMAN, 10)));
	    		}
	    		
	    		if (standardDisplay.contains("createdDate")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Created Date : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getCreatedDt() , new Font(Font.TIMES_ROMAN, 10)));
	    		}				
				
	    		if (standardDisplay.contains("attachments")) {
	    			document.add(Chunk.NEWLINE);
	    			document.add(new Chunk("Attachments : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
	    			ArrayList attachments = r.getRequirementAttachments(databaseType);
					if (attachments.size() > 0){  
						Iterator atachmentIterator = attachments.iterator();
						while (atachmentIterator.hasNext()) {
							RequirementAttachment attachment = (RequirementAttachment) atachmentIterator.next();
							document.add(Chunk.NEWLINE);
			    			document.add(new Chunk("        File Name : "  , new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
			    			document.add(new Chunk(attachment.getFileName() , new Font(Font.TIMES_ROMAN, 10)));
			    			document.add(Chunk.NEWLINE);
			    			document.add(new Chunk("        Title : " , new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
			    			document.add(new Chunk( attachment.getTitle(), new Font(Font.TIMES_ROMAN, 10)));
			    			document.add(Chunk.NEWLINE);
						}
					}
					
	    		}				
				
				// Printing custom attributes.
	    		// a typical uda looks like this 
	    		// Customer:#: SBI:##:Delivery Estimate:#:01/01/12				
	    		String uda = r.getUserDefinedAttributes();
				String[] attribs = uda.split(":##:");
				for (int k=0; k<attribs.length; k++) {
					String[] attrib = attribs[k].split(":#:");
					
					// To avoid a array out of bounds exception where the attrib value wasn't filled in
					// we print the cell only if array has 2 items in it.					
					if (attrib.length ==2){
						if (customAttributesDisplay.contains(attrib[0]) ) {
							document.add(Chunk.NEWLINE);
							document.add(new Chunk(attrib[0]+ " : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
				    		document.add(new Chunk( attrib[1] , new Font(Font.TIMES_ROMAN, 10)));
						}
					}					
				}

				document.add(Chunk.NEWLINE);
	    		document.add(new Chunk("Name : ", new Font(Font.TIMES_ROMAN, 10,Font.BOLDITALIC )));
	    		document.add(new Chunk( r.getRequirementName() , new Font(Font.TIMES_ROMAN, 10)));

	    		if (r.getRequirementDescription() != null) {
	    			document.add(Chunk.NEWLINE);
		    		document.add(new Chunk("Description : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementDescriptionNoHTML() , new Font(Font.TIMES_ROMAN, 10)));
	    		}
	        }
			
	        document.add(new Paragraph("  "));
			document.close();
			

    		// if rootDataDirectory/TraceCloud does not exist, lets create it.
    		File traceCloudRoot = new File (rootDataDirectory + File.separator +  "TraceCloud");
    		if (!(traceCloudRoot.exists() )){
    		    new File(rootDataDirectory + File.separator +  "TraceCloud").mkdir();
    		}

    		// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
    		File tempFolderRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp");
    		if (!(tempFolderRoot.exists() )){
    			new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp").mkdir();
    		}

    		filename = rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp" + File.separator + filename;
    		FileOutputStream fileOut = new FileOutputStream(filename);
			baos.writeTo(fileOut);
			fileOut.flush();
			fileOut.close();
        
        } catch (FileNotFoundException fnfe) {
            // It might not be possible to create the target file.
            fnfe.printStackTrace();
        } catch (DocumentException de) {
            // DocumentExceptions arise if you add content to the document before opening or
            // after closing the document.
            de.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }
    		

	
    
    private static String exportListReportToPDF (HttpServletRequest request,java.sql.Connection con, 
    		ArrayList reportArrayList , String standardDisplay, String customAttributesDisplay, 
    		ScheduledReport scheduledReport, Report report, 
    		User scheduledReportOwner , String databaseType, String rootDataDirectory) throws ServletException, IOException {

    	String filename = "";
    	Project project = new Project(report.getProectId(), databaseType);
    	
    	
    	try{
    		
    		// create a file name and set it to it.
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
    		String today =  sdf.format(cal.getTime());
    		filename = scheduledReportOwner.getFirstName() + " " + scheduledReportOwner.getLastName()  +" Report " + scheduledReport.getScheduledReportId() + " " + today + ".pdf";
    		filename.replace(' ', '_');
    		
    		
    		
    		Document document = new Document();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);
			document.open();
			

			// lets add the heading page.
			document.add(new Paragraph(" "));
			
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			
			
			Chunk colon = new Chunk( " : " , new Font(Font.TIMES_ROMAN, 10));
			Chunk heading = new Chunk( "Report Title" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		Chunk value =  new Chunk( " List Report" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
    		
			heading = new Chunk( "Report Date" , new Font(Font.TIMES_ROMAN, 10));    		
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( today, new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
    		
			heading = new Chunk( "Report Generated By" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( scheduledReportOwner.getEmailId(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			
			
 
    			heading = new Chunk( "Report Id" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(Integer.toString(report.getReportId()) , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
    			
    			heading = new Chunk( "Report Created By" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getCreatedByEmailId() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);


    			heading = new Chunk( "Report URL" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		String reportURLString = ProjectUtil.getURL(request, report.getReportId(), "report") ;
	    	    Anchor anchor1 = new Anchor(reportURLString, 
	    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
	    	    anchor1.setReference(reportURLString);
	    	    document.add(anchor1);
        		document.add(Chunk.NEWLINE);


    			heading = new Chunk( "Report Name" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getReportName() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
			    
    			heading = new Chunk( "Report Description" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getReportDescription() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
        		
    		
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

			
			heading = new Chunk( "Project Prefix" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getShortName(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
    		heading = new Chunk( "Project Name" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getProjectName(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
    		
    		heading = new Chunk( "Project Description" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getProjectDescription(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
			document.newPage();
			

    		Iterator i = reportArrayList.iterator();
            while ( i.hasNext() ) {
	    		Requirement r = (Requirement) i.next();

	    		document.add(new Paragraph(" "));
	    		document.add(Chunk.NEWLINE);

	    		
	    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");
	    	    anchor1 = new Anchor(r.getRequirementFullTag(), 
	    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
	    	    anchor1.setReference(url);
	    	    document.add(anchor1);
	    	    
	    	    document.add(Chunk.NEWLINE);
	    	    document.add(new Chunk("Version: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
	    		document.add(new Chunk( Integer.toString(r.getVersion()) , new Font(Font.TIMES_ROMAN, 10)));

	    		
	    		
	    		// lets customize the testingStatua color based on its value.
	    		if (standardDisplay.contains("testingStatus")) {
		    		document.add(Chunk.NEWLINE);
		    		document.add(new Chunk("Testing Status : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		Chunk testingStatusChunk = new Chunk( "   " + r.getTestingStatus() + "   " , new Font(Font.TIMES_ROMAN, 10));
		    		if (r.getTestingStatus().equals("Pending")){
		    			testingStatusChunk.setBackground(new Color(0xFF, 0xFF, 0x00));
		    		}
				    if (r.getTestingStatus().equals("Pass")){
		    			testingStatusChunk.setBackground(new Color(0x00, 0xFF, 0x00));			    	
		        	}
				    if (r.getTestingStatus().equals("Fail")){
		    			testingStatusChunk.setBackground(new Color(0xFF, 0x00, 0x00));
		        	}
		    		document.add(testingStatusChunk);
	    		}
	    		
	    		if (standardDisplay.contains("status")) {
	    			document.add(Chunk.NEWLINE);
		    		document.add(new Chunk("Approval Status : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		
		    		// lets customize the status color based on its value.
		    		Chunk statusChunk = new Chunk( "   " + r.getApprovalStatus() + "   " , new Font(Font.TIMES_ROMAN, 10));	    		
		    		if (r.getApprovalStatus().equals("Draft")){
		    			statusChunk.setBackground(new Color(0xEE, 0x82, 0xEE));
		    		}
				    if (r.getApprovalStatus().equals("In Approval WorkFlow")){
		    			statusChunk.setBackground(new Color(0x99, 0xcc, 0xff));			    	
		        	}
				    if (r.getApprovalStatus().equals("Approved")){
		    			statusChunk.setBackground(new Color(0x57, 0xE9, 0x64));
		        	}
				    if (r.getApprovalStatus().equals("Rejected")){
		    			statusChunk.setBackground(new Color(0xFF, 0x00, 0x00));
		        	}			    
		    		document.add(statusChunk);
	    		}

	    		if (standardDisplay.contains("priority")) {
	    			document.add(Chunk.NEWLINE);
		    	    document.add(new Chunk("Priority: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementPriority() , new Font(Font.TIMES_ROMAN, 10)));
	    		}

	    		if (standardDisplay.contains("percentComplete")) {
	    			document.add(Chunk.NEWLINE);
		    		document.add(new Chunk("Percent Complete: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementPctComplete() + "%" , new Font(Font.TIMES_ROMAN, 10)));
		    		
	    		}
	    		
	    		document.add(Chunk.NEWLINE);
	    	    document.add(new Chunk("Approved On: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
	    		document.add(new Chunk( r.getApprovedByAllDt() , new Font(Font.TIMES_ROMAN, 10)));

	    		if (standardDisplay.contains("lockedBy")) {
		    		document.add(Chunk.NEWLINE);
		    		document.add(new Chunk("Locked By: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementLockedBy() , new Font(Font.TIMES_ROMAN, 10)));
	    		}
				
	    		
			    // lets handle the approvers.
			    String pendingApprovers = "";
			    String approvedApprovers = "";
			    String rejectedApprovers = "";
			    
			    String[] approvers = new String[0] ;
			    if ((r.getApprovers()!= null) && (r.getApprovers().contains(","))){
			    	approvers = r.getApprovers().split(",");
			    }
			    
			    
			    for (int k=0;k<approvers.length;k++){
			    	if (approvers[k].contains("(P)")){
			    		pendingApprovers += approvers[k].replace("(P)","") + ", ";
			    	}	
			    	if (approvers[k].contains("(A)")){
			    		approvedApprovers += approvers[k].replace("(A)","") + ", ";
			    	}
			    	if (approvers[k].contains("(R)")){
			    		rejectedApprovers += approvers[k].replace("(R)","") + ", ";
			    	}
			    }
			    
			    // lets drop the last ,
			    if (pendingApprovers.contains(",")){
			    	pendingApprovers = (String) pendingApprovers.subSequence(0,pendingApprovers.lastIndexOf(","));
			    }			    
			    if (approvedApprovers.contains(",")){
			    	approvedApprovers = (String) approvedApprovers.subSequence(0,approvedApprovers.lastIndexOf(","));
			    }
			    if (rejectedApprovers.contains(",")){
			    	rejectedApprovers = (String) rejectedApprovers.subSequence(0,rejectedApprovers.lastIndexOf(","));
			    }

			    
			    if (standardDisplay.contains("approvedBy")) {
			    	document.add(Chunk.NEWLINE);
				    document.add(new Chunk("Approved By : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
				    document.add(new Chunk( approvedApprovers , new Font(Font.TIMES_ROMAN, 10)));			    
			    }
			    
			    if (standardDisplay.contains("rejectedBy")) {
			    	document.add(Chunk.NEWLINE);			    	
				    document.add(new Chunk("Rejected By : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
				    document.add(new Chunk( rejectedApprovers , new Font(Font.TIMES_ROMAN, 10)));			    
			    }

			    if (standardDisplay.contains("pendingBy")) {
			    	document.add(Chunk.NEWLINE);
				    document.add(new Chunk("Pending By : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
				    document.add(new Chunk( pendingApprovers , new Font(Font.TIMES_ROMAN, 10)));			    
			    }

			    
				if (standardDisplay.contains("owner")) {
					document.add(Chunk.NEWLINE);
					document.add(new Chunk("Owner : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementOwner() , new Font(Font.TIMES_ROMAN, 10)));
				}
	    		
	    		
	    		
	    		if (standardDisplay.contains("traceTo")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Trace To: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementTraceTo() , new Font(Font.TIMES_ROMAN, 10)));
	    		}
	    		
	    		if (standardDisplay.contains("traceFrom")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Trace From: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementTraceFrom(), new Font(Font.TIMES_ROMAN, 10)));
	    		}
	    		
	    		if (standardDisplay.contains("externalURL")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("External URL: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
					if ((r.getRequirementExternalUrl() !=null) && (!r.getRequirementExternalUrl().equals(""))){
			    	    anchor1 = new Anchor(r.getRequirementExternalUrl(), 
			    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
			    	    anchor1.setReference(r.getRequirementExternalUrl());
			    	    document.add(anchor1);
					}
	    		}


	    		if (standardDisplay.contains("folderPath")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Folder Path : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getFolderPath() , new Font(Font.TIMES_ROMAN, 10)));
	    		}
	    		
	    		if (standardDisplay.contains("baselines")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Baselines : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementBaselineString( databaseType) , new Font(Font.TIMES_ROMAN, 10)));
	    		}
	    		
	    		if (standardDisplay.contains("createdDate")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Created Date : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getCreatedDt() , new Font(Font.TIMES_ROMAN, 10)));
	    		}				
				
	    		if (standardDisplay.contains("attachments")) {
	    			document.add(Chunk.NEWLINE);
	    			document.add(new Chunk("Attachments : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
	    			ArrayList attachments = r.getRequirementAttachments(databaseType);
					if (attachments.size() > 0){  
						Iterator atachmentIterator = attachments.iterator();
						while (atachmentIterator.hasNext()) {
							RequirementAttachment attachment = (RequirementAttachment) atachmentIterator.next();
							document.add(Chunk.NEWLINE);
			    			document.add(new Chunk("        File Name : "  , new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
			    			document.add(new Chunk(attachment.getFileName() , new Font(Font.TIMES_ROMAN, 10)));
			    			document.add(Chunk.NEWLINE);
			    			document.add(new Chunk("        Title : " , new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
			    			document.add(new Chunk( attachment.getTitle(), new Font(Font.TIMES_ROMAN, 10)));
			    			document.add(Chunk.NEWLINE);
						}
					}
					
	    		}				
				
				// Printing custom attributes.
	    		// a typical uda looks like this 
	    		// Customer:#: SBI:##:Delivery Estimate:#:01/01/12				
	    		String uda = r.getUserDefinedAttributes();
				String[] attribs = uda.split(":##:");
				for (int k=0; k<attribs.length; k++) {
					String[] attrib = attribs[k].split(":#:");
					
					// To avoid a array out of bounds exception where the attrib value wasn't filled in
					// we print the cell only if array has 2 items in it.					
					if (attrib.length ==2){
						if (customAttributesDisplay.contains(attrib[0]) ) {
							document.add(Chunk.NEWLINE);
							document.add(new Chunk(attrib[0]+ " : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
				    		document.add(new Chunk( attrib[1] , new Font(Font.TIMES_ROMAN, 10)));
						}
					}					
				}

				document.add(Chunk.NEWLINE);
	    		document.add(new Chunk("Name : ", new Font(Font.TIMES_ROMAN, 10,Font.BOLDITALIC )));
	    		document.add(new Chunk( r.getRequirementName() , new Font(Font.TIMES_ROMAN, 10)));
				
	    		if (r.getRequirementDescription() != null) {
	    			document.add(Chunk.NEWLINE);
		    		document.add(new Chunk("Description : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementDescriptionNoHTML() , new Font(Font.TIMES_ROMAN, 10)));
	    		}
            }
			
            document.add(new Paragraph("   "));
			document.close();
			
       		// if rootDataDirectory/TraceCloud does not exist, lets create it.
    		File traceCloudRoot = new File (rootDataDirectory + File.separator + "TraceCloud");
    		if (!(traceCloudRoot.exists() )){
    		    new File(rootDataDirectory + File.separator + "TraceCloud").mkdir();
    		}

    		// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
    		File tempFolderRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp");
    		if (!(tempFolderRoot.exists() )){
    			new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp").mkdir();
    		}

    		filename = rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp" + File.separator + filename;
    		FileOutputStream fileOut = new FileOutputStream(filename);
			baos.writeTo(fileOut);
			fileOut.flush();
			fileOut.close();
	    	
        } catch (FileNotFoundException fnfe) {
            // It might not be possible to create the target file.
            fnfe.printStackTrace();
        } catch (DocumentException de) {
            // DocumentExceptions arise if you add content to the document before opening or
            // after closing the document.
            de.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }
    		
    

    private static String exportTraceTreeReportToExcel (HttpServletRequest request,java.sql.Connection con, 
    		ArrayList traceTreeReport , String standardDisplay, String customAttributesDisplay, 
    		ScheduledReport scheduledReport, Report report, 
    		User scheduledReportOwner ,String includeRevisionHistory, String databaseType, String rootDataDirectory)
    	throws ServletException, IOException {

    	String filename = "";
    	Project project = new Project(report.getProectId(), databaseType);
    	
    	try {
    		// create a file name and set it to it.
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
    		String today =  sdf.format(cal.getTime());
    		filename = scheduledReportOwner.getFirstName() + " " + scheduledReportOwner.getLastName()  +" TraceTreeReport " + scheduledReport.getScheduledReportId() + " "+ today + ".xls";
    		filename.replace(' ', '_');
    		

    		
    		HSSFWorkbook wb = new HSSFWorkbook();
    		HSSFCreationHelper createHelper = (HSSFCreationHelper) wb.getCreationHelper(); 
	    		
    		
    		// cell style for clear traces is Green
    		HSSFCellStyle clearTraceStyle = wb.createCellStyle();
    	    clearTraceStyle.setFillForegroundColor(HSSFColor.GREEN.index);
    	    clearTraceStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    
    		// cell style for suspect traces is red.
    	    HSSFCellStyle suspectTraceStyle = wb.createCellStyle();
    	    suspectTraceStyle.setFillForegroundColor(HSSFColor.RED.index);
    	    suspectTraceStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

    	    
    	    HSSFCellStyle pendingStyle = wb.createCellStyle();
    	    HSSFFont pendingFont = wb.createFont();
    	    pendingFont.setColor(HSSFColor.INDIGO.index);
    	    pendingStyle.setFont(pendingFont);   		
    	    
    	    HSSFCellStyle approvedStyle = wb.createCellStyle();
    	    HSSFFont approvedFont = wb.createFont();
    	    approvedFont.setColor(HSSFColor.GREEN.index);
    	    approvedStyle.setFont(approvedFont);   		
    	    
    	    HSSFCellStyle rejectedStyle = wb.createCellStyle();
    	    HSSFFont rejectedFont = wb.createFont();
    	    rejectedFont.setColor(HSSFColor.RED.index);
    	    rejectedStyle.setFont(rejectedFont);   		
    	    
    		HSSFCellStyle headerStyle = wb.createCellStyle();
    		headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
    	    headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    
    	   
    	    /////////////////////////////////////////
    	    //
    	    // lets build the Report Cover Page.
    	    //
    	    /////////////////////////////////////////
    	    
    	    HSSFSheet sheet  = wb.createSheet("Report Info");
    	    // lets start on the 5th Row.
    	    int startRow = 5; 
    		HSSFRow row     = sheet.createRow((short)startRow++);

    		
    		HSSFCell cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Report Title"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		HSSFCell cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString ("Trace Tree Report"));

    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Report Date"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (today));
    		
    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Report Generated By "));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (scheduledReportOwner.getEmailId()));

    		startRow += 2;

    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Trace Tree Depth  "));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (Integer.toString(report.getTraceTreeDepth())));

    		startRow += 2;

    		// if this is a saved report, then lets print the report info.
    		// note : reportIdString is available only for saved reports.
    			
        		row     = sheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report Id"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		cellB.setCellValue(new HSSFRichTextString (Integer.toString(report.getReportId())));
    			
        		row     = sheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report Created By"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		cellB.setCellValue(new HSSFRichTextString (report.getCreatedByEmailId() ));

        		row     = sheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report URL"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		String reportURLString = ProjectUtil.getURL(request, report.getReportId(), "report") ;
        		cellB.setCellValue(new HSSFRichTextString ( reportURLString ));
			    
			    
        		
        		row     = sheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report Name"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		cellB.setCellValue(new HSSFRichTextString (report.getReportName()));
        		
        		row     = sheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report Description"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		cellB.setCellValue(new HSSFRichTextString (report.getReportDescription() ));

    		
    		// project Info.
    		startRow += 4;
    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Prefix"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getShortName()));

    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Name"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getProjectName()));

    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Description"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getProjectDescription()));
    		

    	    
    	    // lets build the list report.
    	    sheet = wb.createSheet("Trace Tree Report");
    		
            Iterator i = traceTreeReport.iterator();
            
            int j = 0;
            
	    	while ( i.hasNext() ) {
	    		TraceTreeRow tTR = (TraceTreeRow) i.next();
	    		Requirement r = tTR.getRequirement();
	    		// since a trace tree can have different requirement types
	    		// and since we can't figure out how may attributes these
	    		// req Types can have, we will just display a UDA string.
	    		
	    		// Create a row and put some cells in it. Rows are 0 based.
	    		j++;
	    		
	    		// for the first row, print the header and user defined columns headers. etc..
	    		if (j == 1){

	        		// Create a row and put some cells in it. Rows are 0 based.
	        		row     = sheet.createRow((short)0);
	        
	        	    
	        		
	        		// Print the header row for the excel file.

	        		int cellNum = 0;
	        		int column = 0;
	        		
	        		HSSFCell cell = row.createCell(cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Level 1         "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Level 2         "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Level 3         "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Level 4         "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Level 5         "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Level 6         "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Level 7         "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Tag             "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("URL To Requirement                                                                                                              "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Version"));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Name                                                                                                                  "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
		    		// we can show the requirement description in excel
	        		// as the cell can easily be hidden in excel and will not
	        		// impede the effectiveness of the trace tree.
	    
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Description                                                                                                          "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		    
	        		if (standardDisplay.contains("owner")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Owner                                                           "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("testingStatus")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Testing Status"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("percentComplete")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Percent Complete"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("priority")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Priority          "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("status")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Approval Status                     "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}

	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Approved Dt                             "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		

	        		if (standardDisplay.contains("pendingBy")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Pending By                     "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("approvedBy")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Approved By                    "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("rejectedBy")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Rejected By                    "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("traceTo")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Trace To                       "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("traceFrom")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Trace From                     "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}

	        		
	        		if (standardDisplay.contains("externalURL")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("External URL                                                                                                 "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		

	        		if (standardDisplay.contains("folderPath")) {	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Folder Path                                                                                                        "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("baselines")) {	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Baselines                      "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("createdDate")) {	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Created Date                             "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("attachments")) {	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Attachments                    "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}

	        		if (standardDisplay.contains("customAttributes")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("User Defined Attributes                                          "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		       	}
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Trace Description                                                    "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	    		}

	    		// print the data rows now.
	    		row     = sheet.createRow(j);

	    		int cellNum = 0;
			    // Create a cell and put a value in it.
	    		
	    		
	    		if (tTR.getLevel() == 1){
	    			if (r.getProjectId()== project.getProjectId()){
	    				row.createCell(cellNum).setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
	    			}
	    			else {
	    				row.createCell(cellNum).setCellValue(new HSSFRichTextString (r.getProjectShortName() +":" + r.getRequirementFullTag()));
	    			}
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			
	    		}
 				else if (tTR.getLevel() == 2) {
 					if (tTR.getTracesToSuspectRequirement() == 0){ 
 						HSSFCell cell0 = row.createCell(cellNum);
 						cell0.setCellValue(new HSSFRichTextString ("T"));
 						cell0.setCellStyle(clearTraceStyle);
 						
 					}
 					else {
 						HSSFCell cell0 = row.createCell(cellNum);
 						cell0.setCellValue(new HSSFRichTextString ("ST"));
 						cell0.setCellStyle(suspectTraceStyle);
 					}
 					
	    			if (r.getProjectId()== project.getProjectId()){
	    				row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
	    			}
	    			else {
	    				row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getProjectShortName() +":" + r.getRequirementFullTag()));
	    			}
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));

 				}
 				else if (tTR.getLevel() == 3) {
 					row.createCell(cellNum).setCellValue(new HSSFRichTextString (""));
 					if (tTR.getTracesToSuspectRequirement() == 0){ 
 						HSSFCell cell1 = row.createCell(++cellNum);
 						cell1.setCellValue(new HSSFRichTextString ("T"));
 						cell1.setCellStyle(clearTraceStyle);
 					}
 					else {
 						HSSFCell cell1 = row.createCell(++cellNum);
 						cell1.setCellValue(new HSSFRichTextString ("ST"));
 						cell1.setCellStyle(suspectTraceStyle);
 					}
	    			if (r.getProjectId()== project.getProjectId()){
	    				row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
	    			}
	    			else {
	    				row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getProjectShortName() +":" + r.getRequirementFullTag()));
	    			}	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));

 				}
 				else if (tTR.getLevel() == 4) {
 					row.createCell(cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					if (tTR.getTracesToSuspectRequirement() == 0){ 
 						HSSFCell cell2 = row.createCell(++cellNum);
 						cell2.setCellValue(new HSSFRichTextString ("T"));
 						cell2.setCellStyle(clearTraceStyle);
 					}
 					else {
 						HSSFCell cell2 = row.createCell(++cellNum);
 						cell2.setCellValue(new HSSFRichTextString ("ST"));
 						cell2.setCellStyle(suspectTraceStyle);
 					}
	    			if (r.getProjectId()== project.getProjectId()){
	    				row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
	    			}
	    			else {
	    				row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getProjectShortName() +":" + r.getRequirementFullTag()));
	    			}	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));

 				}
 				else if (tTR.getLevel() == 5) {
 					row.createCell(cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					if (tTR.getTracesToSuspectRequirement() == 0){ 
 						HSSFCell cell3 = row.createCell(++cellNum);
 						cell3.setCellValue(new HSSFRichTextString ("T"));
 						cell3.setCellStyle(clearTraceStyle);
 					}
 					else {
 						HSSFCell cell3 = row.createCell(++cellNum);
 						cell3.setCellValue(new HSSFRichTextString ("ST"));
 						cell3.setCellStyle(suspectTraceStyle);
 					}
	    			if (r.getProjectId()== project.getProjectId()){
	    				row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
	    			}
	    			else {
	    				row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getProjectShortName() +":" + r.getRequirementFullTag()));
	    			} 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 				}
 				else if (tTR.getLevel() == 6) {
 					row.createCell(cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					if (tTR.getTracesToSuspectRequirement() == 0){ 
 						HSSFCell cell4 = row.createCell(++cellNum);
 						cell4.setCellValue(new HSSFRichTextString ("T"));
 						cell4.setCellStyle(clearTraceStyle);
 					}
 					else {
 						HSSFCell cell4 = row.createCell(++cellNum);
 						cell4.setCellValue(new HSSFRichTextString ("ST"));
 						cell4.setCellStyle(suspectTraceStyle);
 					}
	    			if (r.getProjectId()== project.getProjectId()){
	    				row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
	    			}
	    			else {
	    				row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getProjectShortName() +":" + r.getRequirementFullTag()));
	    			} 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 				}
 				else if (tTR.getLevel() == 7) {
 					row.createCell(cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					
 					if (tTR.getTracesToSuspectRequirement() == 0){ 
 						HSSFCell cell5 = row.createCell(++cellNum);
 						cell5.setCellValue(new HSSFRichTextString ("T"));
 						cell5.setCellStyle(clearTraceStyle);
 					}
 					else {
 						HSSFCell cell5 = row.createCell(++cellNum);
 						cell5.setCellValue(new HSSFRichTextString ("ST"));
 						cell5.setCellStyle(suspectTraceStyle);
 					}
	    			if (r.getProjectId()== project.getProjectId()){
	    				row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
	    			}
	    			else {
	    				row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getProjectShortName() +":" + r.getRequirementFullTag()));
	    			} 				}
	    		
	    		
	    		
			    // Create a cell and put a value in it.	    		
	    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");
    			if (r.getProjectId()== project.getProjectId()){
    				HSSFCell cell = row.createCell(++cellNum);
    			    cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
    			}
    			else {
    				HSSFCell cell = row.createCell(++cellNum);
    			    cell.setCellValue(new HSSFRichTextString (r.getProjectShortName() +":" + r.getRequirementFullTag()));
    			}
			    
			    HSSFCell cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (url));
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (Integer.toString(r.getVersion())));
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getRequirementName()));
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getRequirementDescriptionNoHTML()));
			    
			    if (standardDisplay.contains("owner")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getRequirementOwner()));
			    	
			    }
			    

			    if (standardDisplay.contains("testingStatus")) {
			    	HSSFCell testingStatusCell = row.createCell(++cellNum);
	        	    testingStatusCell.setCellValue(new HSSFRichTextString (r.getTestingStatus() ));
			    	
			    	HSSFCellStyle testingStatusStyle = wb.createCellStyle();
        			if (r.getTestingStatus().equals("Pending")){
		        		testingStatusStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		        	    testingStatusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		        	}
        			if (r.getTestingStatus().equals("Pass")){
		        		testingStatusStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		        	    testingStatusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		        	}
        			if (r.getTestingStatus().equals("Fail")){
		        		testingStatusStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		        	    testingStatusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		        	}
        			testingStatusCell.setCellStyle(testingStatusStyle);
        		}
			    
			    if (standardDisplay.contains("percentComplete")) {
			    	row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getRequirementPctComplete() + ""));
			    }
			    
			    if (standardDisplay.contains("priority")) {
			    	row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getRequirementPriority()));
			    }
			    
			    if (standardDisplay.contains("status")) {
	        	    HSSFCell statusCell = row.createCell(++cellNum);
	        	    statusCell.setCellValue(new HSSFRichTextString (r.getApprovalStatus()));
	
	        	    // lets set the approval work flow status Cell color based on its value
	        	    HSSFCellStyle statusStyle = wb.createCellStyle();
				    if (r.getApprovalStatus().equals("Draft")){
		        		statusStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		        	}
				    if (r.getApprovalStatus().equals("In Approval WorkFlow")){
		        		statusStyle.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
		        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		        	}
				    if (r.getApprovalStatus().equals("Approved")){
		        		statusStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		        	}
				    if (r.getApprovalStatus().equals("Rejected")){
		        		statusStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		        	}			    
				    statusCell.setCellStyle(statusStyle);
			    }
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getApprovedByAllDt() ));
			    
			    
			    // lets handle the approvers.
			    String pendingApprovers = "";
			    String approvedApprovers = "";
			    String rejectedApprovers = "";
			    
			    String[] approvers = new String[0] ;
			    if ((r.getApprovers()!= null) && (r.getApprovers().contains(","))){
			    	approvers = r.getApprovers().split(",");
			    }
			    
			    
			    for (int k=0;k<approvers.length;k++){
			    	if (approvers[k].contains("(P)")){
			    		pendingApprovers += approvers[k].replace("(P)","") + ", ";
			    	}
			    	
			    	if (approvers[k].contains("(A)")){
			    		approvedApprovers += approvers[k].replace("(A)","") + ", ";
			    	}
			    	if (approvers[k].contains("(R)")){
			    		rejectedApprovers += approvers[k].replace("(R)","") + ", ";
			    	}
			    }
			    
			    // lets drop the last ,
			    if (pendingApprovers.contains(",")){
			    	pendingApprovers = (String) pendingApprovers.subSequence(0,pendingApprovers.lastIndexOf(","));
			    }			    
			    if (approvedApprovers.contains(",")){
			    	approvedApprovers = (String) approvedApprovers.subSequence(0,approvedApprovers.lastIndexOf(","));
			    }
			    if (rejectedApprovers.contains(",")){
			    	rejectedApprovers = (String) rejectedApprovers.subSequence(0,rejectedApprovers.lastIndexOf(","));
			    }
			    
			    if (standardDisplay.contains("pendingBy")) {
				    HSSFCell  pendingCell = row.createCell(++cellNum);
				    pendingCell.setCellValue(new HSSFRichTextString (pendingApprovers ));
				    pendingCell.setCellStyle(pendingStyle);
			    }
			    
			    if (standardDisplay.contains("approvedBy")) {
				    HSSFCell  approvedCell = row.createCell(++cellNum);
				    approvedCell.setCellValue(new HSSFRichTextString (approvedApprovers ));
				    approvedCell.setCellStyle(approvedStyle);
			    }
			    
			    if (standardDisplay.contains("rejectedBy")) {
				    HSSFCell  rejectedCell = row.createCell(++cellNum);
				    rejectedCell.setCellValue(new HSSFRichTextString (rejectedApprovers ));
				    rejectedCell.setCellStyle(rejectedStyle);
			    }
			    
			    if (standardDisplay.contains("traceTo")) {
			    	row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getRequirementTraceTo() ));
			    	// we don't set these to wrapped
			    }
			    
			    if (standardDisplay.contains("traceFrom")) {
			    	row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getRequirementTraceFrom() ));
			    	// we don't set these to wrapped
			    }
			    
			    if (standardDisplay.contains("externalURL")) {
				   cell = row.createCell(++cellNum);
				   cell.setCellValue(new HSSFRichTextString (r.getRequirementExternalUrl() ));
				    
			    }
			    
			    if (standardDisplay.contains("folderPath")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getFolderPath() ));
			        
			    }
			    
			    if (standardDisplay.contains("baselines")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getRequirementBaselineString(databaseType) ));
			        
			    }
			    
			    if (standardDisplay.contains("createdDate")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getCreatedDt() ));
			        
			    }
			    

			    if (standardDisplay.contains("attachments")) {
			    	String attachmentString = "";
					ArrayList attachments = r.getRequirementAttachments(databaseType);
					if (attachments.size() > 0){  
						Iterator atachmentIterator = attachments.iterator();
						while (atachmentIterator.hasNext()) {
							RequirementAttachment attachment = (RequirementAttachment) atachmentIterator.next();
							attachmentString += "File :  " + attachment.getFileName()  + "; ";
							attachmentString += "Title :  " + attachment.getTitle()  + "; ";
						}															
					} 
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (attachmentString ));
			        
			    }
			    
			    if (standardDisplay.contains("customAttributes")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getUserDefinedAttributesFormatted("semiColon") ));
			    }
		    	
			    // trace description
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (tTR.getTraceDescription() ));
			    
	    	}
	    	
	    	
	    	//#########################################################################
	    	
	    	
	    	
	    	// lets see if the user  has chosen to print out the Revision History.
	    	// if yes, we will need to add another tab to the excel file and include revision history of every
	    	// requirement in this output.
	    	 HSSFCellStyle wrappedStyle = wb.createCellStyle();
	    	 wrappedStyle.setWrapText(true);
	    	    
	    	if( (includeRevisionHistory != null ) && (includeRevisionHistory.equals("yes"))) {
	    		// lets add a new sheet to the excel output.
	    	    sheet = wb.createSheet("Requirement Version History");
	    		// for the first row, print the header and user defined columns headers. etc..

	    	    int rowNum = 0;
        		
        		
		
        		
        		row     = sheet.createRow(rowNum++);	
        		// Print the header row for the excel file.
        		int cellNum = 0;
        		int column = 0;
        		
        		HSSFCell cell = row.createCell(cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Tag     "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("URL To Requirement                                                                                                "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Version"));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Name                                                                                                              "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Description                                                                                                       "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Approvers                        "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
    		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Approval Status "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Priority"));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
    			cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Owner                                  "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
    		
        		
    			cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Percent Complete"));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        	
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("External URL                                                                                            "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Trace To                           "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Trace From                         "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Created By                        "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
    		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Created Date                              "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);

        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Custom Attributes                            "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
    		
        		i = traceTreeReport.iterator();
    	    			            
		    	while ( i.hasNext() ) {
    	    		TraceTreeRow tTR = (TraceTreeRow) i.next();
    	    		Requirement r = tTR.getRequirement();

		    		// for every Requirement in the result set, lets get an arraylist of
		    		// requirement versions.
		    		ArrayList requirementVersions = r.getRequirementVersions( databaseType);
		    		Iterator m = requirementVersions.iterator();
		    		
		    		while (m.hasNext()) {
		    			RequirementVersion v = (RequirementVersion) m.next();
		    			// for each req version, lets print a row.
			    		// print the data rows now.
			    		row     = sheet.createRow(rowNum++);
					

			    		// Create a cell and put a value in it.
						 // make cell0 a hyperlink
			    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");

			    		cellNum = 0;
					    
					    cell = row.createCell(cellNum);
					    cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (url));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (Integer.toString(v.getVersion())));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionName()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionDescriptionNoHTML()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionApprovers() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionStatus() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionPriority() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionOwner() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (Integer.toString(v.getVersionPctComplete())));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionExternalURL() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionTraceTo() ));
					    // we are not wrapping this, as it can get quiet big
					    //cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionTraceFrom() ));
					    // we are not wrapping this, as it can get quiet big
					    //cell.setCellStyle(wrappedStyle);
					    
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionCreatedBy()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionCreatedDt()));
					    cell.setCellStyle(wrappedStyle);

					    
					    String uDA = v.getVersionUserDefinedAttributes();
					    String formattedUDA = "";
					    if ((uDA != null) && (uDA.contains(":##:"))) {
					    	String [] uDAs = uDA.split(":##:") ;
					    	
					    	for (int u=0; u<uDAs.length; u++) {
					    		formattedUDA += uDAs[u] + "\n";
					    	}
					    }
					    cell = row.createCell(++cellNum);
			    		cell.setCellValue(new HSSFRichTextString (formattedUDA));
			    		
		    		}
		    	}
		    	
	    		// lets create a new sheet and print all the comments made by people for all these requiremetns
	    	    sheet = wb.createSheet("Requirement Comments");
	    		// for the first row, print the header and user defined columns headers. etc..

	    	    rowNum = 0;
        		row     = sheet.createRow(rowNum++);	
        		// Print the header row for the excel file.
        		cellNum = 0;
        		column = 0;
        		
        		cell = row.createCell(cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Tag     "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("URL To Requirement                                                                                                "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Version"));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Commenter                                    "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Comment                                                                                                           "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		        

        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Date                                        "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
	    	    
	    	    i = traceTreeReport.iterator();
	            
		    	while ( i.hasNext() ) {
		    		TraceTreeRow tTR = (TraceTreeRow) i.next();
    	    		Requirement r = tTR.getRequirement();
		    		

		    		// for every Requirement in the result set, lets get an arraylist of
		    		// requirement comments.
		    		ArrayList requirementComments = r.getRequirementComments( databaseType);
		    		Iterator m = requirementComments.iterator();
		    		
		    		while (m.hasNext()) {
		    			Comment c = (Comment) m.next();
		    			// for each req comment, lets print a row.
			    		// print the data rows now.
			    		row     = sheet.createRow(rowNum++);
					

			    		// Create a cell and put a value in it.
						 // make cell0 a hyperlink
			    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");

			    		cellNum = 0;
					    
			    		cell = row.createCell(cellNum);
					    cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (url));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (Integer.toString(c.getVersion())));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (c.getCommenterEmailId()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (c.getComment_note()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (c.getCommentDate()));
					    cell.setCellStyle(wrappedStyle);
					}
		    	}	    		
	    	}

            // Write the output
	    	
	    		// if rootDataDirectory/TraceCloud does not exist, lets create it.
	    		File traceCloudRoot = new File (rootDataDirectory + File.separator +  "TraceCloud");
	    		if (!(traceCloudRoot.exists() )){
	    		    new File(rootDataDirectory + File.separator +  "TraceCloud").mkdir();
	    		}

	    		// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
	    		File tempFolderRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp");
	    		if (!(tempFolderRoot.exists() )){
	    			new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp").mkdir();
	    		}

	    		filename = rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp" + File.separator + filename;
	    		FileOutputStream fileOut = new FileOutputStream(filename);
	    		wb.write(fileOut);
	    		fileOut.close();
	    	
        } catch (FileNotFoundException fnfe) {
            // It might not be possible to create the target file.
            fnfe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }

    
    
    private static String exportTraceTreeReportToWord(HttpServletRequest request,java.sql.Connection con, 
    		ArrayList traceTreeReport , String standardDisplay, String customAttributesDisplay, 
    		ScheduledReport scheduledReport, Report report, 
    		User scheduledReportOwner ,String databaseType, String rootDataDirectory)	throws ServletException, IOException {

    	String filename = "";
    	Project project = new Project(report.getProectId(), databaseType);
    	
		try{
			
	    	
			
			// create a file name and set it to it.
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
			String today =  sdf.format(cal.getTime());
    		filename = scheduledReportOwner.getFirstName() + " " + scheduledReportOwner.getLastName()  +" TraceTreeReport " + scheduledReport.getScheduledReportId() + " " + today + ".doc";			
			filename.replace(' ', '_');
    		
			
			
			
	    	Document document = new Document(PageSize.A4.rotate());
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
			RtfWriter2.getInstance(document, baos);		
			document.open();
			
			StyleSheet styles = new StyleSheet();
			styles.loadTagStyle("ol", "leading", "16,0");
	
			// lets add the heading page.
			
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			
			
			
			Chunk colon = new Chunk( " : " , new Font(Font.TIMES_ROMAN, 10));
			Chunk heading = new Chunk( " Report Title" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		Chunk value =  new Chunk( " Trace Tree Report" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
    		
			heading = new Chunk( " Report Date" , new Font(Font.TIMES_ROMAN, 10));    		
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( today, new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
    		
			heading = new Chunk( " Report Generated By" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( scheduledReportOwner.getEmailId(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		
    		document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

    		// if this is a saved report, then lets print the report info.
    		// note : reportIdString is available only for saved reports.
    		

    			heading = new Chunk( "Report Id" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(Integer.toString(report.getReportId()) , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
    			
    			heading = new Chunk( "Report Created By" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getCreatedByEmailId() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);


    			heading = new Chunk( "Report URL" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		String reportURLString = ProjectUtil.getURL(request, report.getReportId(), "report") ;
	    	    Anchor anchor1 = new Anchor(reportURLString, 
	    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
	    	    anchor1.setReference(reportURLString);
	    	    document.add(anchor1);
        		document.add(Chunk.NEWLINE);

    			heading = new Chunk( "Report Name" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getReportName() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
			    
    			heading = new Chunk( "Report Description" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getReportDescription() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
        	
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);


			heading = new Chunk( " Trace Tree Depth " , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( Integer.toString(report.getTraceTreeDepth()), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);

			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

			
			heading = new Chunk( " Project Prefix" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getShortName(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
    		heading = new Chunk( " Project Name" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getProjectName(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
    		
    		heading = new Chunk( " Project Description" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getProjectDescription(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
			document.newPage();

			Iterator i = traceTreeReport.iterator();
	
	        
	        
	        while ( i.hasNext() ) {
	        	TraceTreeRow tTR = (TraceTreeRow) i.next();
	    		Requirement r = (Requirement) tTR.getRequirement();
	    		
	
	    		String traceSpacer = "";
        		if (tTR.getLevel() == 1){
        			traceSpacer = "     ";
        		}
    			else if (tTR.getLevel() == 2) {					
    					traceSpacer = 	"            ";
    			}
    			else if (tTR.getLevel() == 3) {
    					traceSpacer = 	"                    ";
    			}
    			else if (tTR.getLevel() == 4) {
    					traceSpacer = 	"                            ";
    			}
    			else if (tTR.getLevel() == 5) {
    					traceSpacer = 	"                                    ";
    			}
    			else if (tTR.getLevel() == 6) {
    					traceSpacer = 	"                                            ";
    			}
    			else if (tTR.getLevel() == 7) {
    					traceSpacer = 	"                                                ";
    			}
	    		String postTagSpacer = "  ";
	
	    		String requirementFullTag = "";
	    		if (r.getProjectId()== project.getProjectId()){
	    			requirementFullTag = r.getRequirementFullTag();
	    		}
	    		else {
	    			requirementFullTag = r.getProjectShortName() + ":" + r.getRequirementFullTag();
	    		}
	    		if (tTR.getLevel() == 1) {
	    			// for level 1 reqs, do not show green or red.
	    			document.add(new Chunk(traceSpacer + requirementFullTag + postTagSpacer ,
							new Font(Font.TIMES_ROMAN, 10, 
							Font.BOLDITALIC , new Color(0x00, 0x00, 0x00) )));
	    		}
	    		if ((tTR.getLevel() >1 ) && (tTR.getTracesToSuspectRequirement() == 0)) {
					document.add(new Chunk(traceSpacer + requirementFullTag + postTagSpacer ,
							new Font(Font.TIMES_ROMAN, 10, 
							Font.BOLDITALIC , new Color(0x00, 0xFF, 0x00))));
				}
				if ((tTR.getLevel() > 1 && (tTR.getTracesToSuspectRequirement() != 0))){
					document.add(new Chunk(traceSpacer + requirementFullTag + postTagSpacer ,
							new Font(Font.TIMES_ROMAN, 10, 
							Font.BOLDITALIC , new Color(0xFF, 0x00, 0x00))));
				}
	    		
	
	    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");
	    		String requirementName = r.getRequirementName();
	    		if ((requirementName != null) && (requirementName.length() > 80)){
	    			requirementName = requirementName.substring(0, 77) + "...";
	    		}
	    	    anchor1 = new Anchor(requirementName, 
	    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
	    	    anchor1.setReference(url);
	    	    document.add(anchor1);	
	    		document.add(Chunk.NEWLINE);
	          }
			
			document.close();
			

			
	    // Write the output
			// if rootDataDirectory/TraceCloud does not exist, lets create it.
			File traceCloudRoot = new File (rootDataDirectory + File.separator + "TraceCloud");
			if (!(traceCloudRoot.exists() )){
			    new File(rootDataDirectory + File.separator + "TraceCloud").mkdir();
			}
	
			// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
			File tempFolderRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp");
			if (!(tempFolderRoot.exists() )){
				new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp").mkdir();
			}
	
			filename = rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp" + File.separator + filename;
			FileOutputStream fileOut = new FileOutputStream(filename);
			baos.writeTo(fileOut);
			fileOut.flush();
			fileOut.close();
		
	
	} catch (FileNotFoundException fnfe) {
	    // It might not be possible to create the target file.
	    fnfe.printStackTrace();
	} catch (DocumentException de) {
	    // DocumentExceptions arise if you add content to the document before opening or
	    // after closing the document.
	    de.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return filename;
	}
   
    
    private static String exportTraceTreeReportToPDF(HttpServletRequest request,java.sql.Connection con, 
    		ArrayList traceTreeReport , String standardDisplay, String customAttributesDisplay, 
    		ScheduledReport scheduledReport, Report report, 
    		User scheduledReportOwner ,String databaseType, String rootDataDirectory) 
    	throws ServletException, IOException {

    	String filename = "";
    	Project project = new Project(report.getProectId(), databaseType);
    	
    	try{
    		
    		
			// create a file name and set it to it.
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
			String today =  sdf.format(cal.getTime());
    		filename = scheduledReportOwner.getFirstName() + " " + scheduledReportOwner.getLastName()  +" TraceTreeReport " + scheduledReport.getScheduledReportId() + " " + today + ".pdf";			
			filename.replace(' ', '_');
            
            
        	Document document = new Document(PageSize.A4.rotate());
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);
			document.open();

			StyleSheet styles = new StyleSheet();
			styles.loadTagStyle("ol", "leading", "16,0");

			// lets add the heading page.
			document.add(new Paragraph(" "));
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			
			
			
			Chunk colon = new Chunk( " : " , new Font(Font.TIMES_ROMAN, 10));
			Chunk heading = new Chunk( " Report Title" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		Chunk value =  new Chunk( " Trace Tree Report" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
    		
			heading = new Chunk( " Report Date" , new Font(Font.TIMES_ROMAN, 10));    		
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( today, new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
    		
			heading = new Chunk( " Report Generated By" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( scheduledReportOwner.getEmailId(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			
    	
    			heading = new Chunk( "Report Id" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(Integer.toString(report.getReportId()) , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
    			
    			heading = new Chunk( "Report Created By" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getCreatedByEmailId() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);


    			heading = new Chunk( "Report URL" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		String reportURLString = ProjectUtil.getURL(request, report.getReportId(), "report") ;
	    	    Anchor anchor1 = new Anchor(reportURLString, 
	    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
	    	    anchor1.setReference(reportURLString);
	    	    document.add(anchor1);
        		document.add(Chunk.NEWLINE);

    			heading = new Chunk( "Report Name" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getReportName() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
			    
    			heading = new Chunk( "Report Description" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getReportDescription() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
        		
    		
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);


			heading = new Chunk( " Trace Tree Depth " , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( Integer.toString(report.getTraceTreeDepth()), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

			
			heading = new Chunk( " Project Prefix" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getShortName(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
    		heading = new Chunk( " Project Name" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getProjectName(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
    		
    		heading = new Chunk( " Project Description" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getProjectDescription(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
			document.newPage();

			
    		Iterator i = traceTreeReport.iterator();

            

	        while ( i.hasNext() ) {
	        	TraceTreeRow tTR = (TraceTreeRow) i.next();
	    		Requirement r = (Requirement) tTR.getRequirement();
	    		
	
	    		String traceSpacer = "";
        		if (tTR.getLevel() == 1){
        			traceSpacer = "     ";
        		}
    			else if (tTR.getLevel() == 2) {					
    					traceSpacer = 	"            ";
    			}
    			else if (tTR.getLevel() == 3) {
    					traceSpacer = 	"                    ";
    			}
    			else if (tTR.getLevel() == 4) {
    					traceSpacer = 	"                            ";
    			}
    			else if (tTR.getLevel() == 5) {
    					traceSpacer = 	"                                    ";
    			}
    			else if (tTR.getLevel() == 6) {
    					traceSpacer = 	"                                            ";
    			}
    			else if (tTR.getLevel() == 7) {
    					traceSpacer = 	"                                                ";
    			}
	    		String postTagSpacer = "  ";
	
	    		String requirementFullTag = "";
	    		if (r.getProjectId()== project.getProjectId()){
	    			requirementFullTag = r.getRequirementFullTag();
	    		}
	    		else {
	    			requirementFullTag = r.getProjectShortName() + ":" + r.getRequirementFullTag();
	    		}
	    		
	    		if (tTR.getLevel() == 1) {
	    			// for level 1 reqs, do not show green or red.
	    			document.add(new Chunk(traceSpacer + requirementFullTag + postTagSpacer ,
							new Font(Font.TIMES_ROMAN, 10, 
							Font.BOLDITALIC , new Color(0x00, 0x00, 0x00) )));
	    		}
	    		if ((tTR.getLevel() >1 ) && (tTR.getTracesToSuspectRequirement() == 0)) {
					document.add(new Chunk(traceSpacer + requirementFullTag + postTagSpacer ,
							new Font(Font.TIMES_ROMAN, 10, 
							Font.BOLDITALIC , new Color(0x00, 0xFF, 0x00))));
				}
				if ((tTR.getLevel() > 1 && (tTR.getTracesToSuspectRequirement() != 0))){
					document.add(new Chunk(traceSpacer + requirementFullTag + postTagSpacer ,
							new Font(Font.TIMES_ROMAN, 10, 
							Font.BOLDITALIC , new Color(0xFF, 0x00, 0x00))));
				}
	    		
	
	    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");
	    		String requirementName = r.getRequirementName();
	    		if ((requirementName != null) && (requirementName.length() > 80)){
	    			requirementName = requirementName.substring(0, 77) + "...";
	    		}
	    	    anchor1 = new Anchor(requirementName, 
	    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
	    	    anchor1.setReference(url);
	    	    document.add(anchor1);	
	    		document.add(Chunk.NEWLINE);
	          }            
            document.close();
			
			
	   	// if rootDataDirectory/TraceCloud does not exist, lets create it.
			File traceCloudRoot = new File (rootDataDirectory + File.separator +  "TraceCloud");
			if (!(traceCloudRoot.exists() )){
			    new File(rootDataDirectory + File.separator +  "TraceCloud").mkdir();
			}
	
			// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
			File tempFolderRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp");
			if (!(tempFolderRoot.exists() )){
				new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp").mkdir();
			}
	
			filename = rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp" + File.separator + filename;
			FileOutputStream fileOut = new FileOutputStream(filename);
			baos.writeTo(fileOut);
			fileOut.flush();
			fileOut.close();
		
	} catch (FileNotFoundException fnfe) {
	    // It might not be possible to create the target file.
	    fnfe.printStackTrace();
	} catch (DocumentException de) {
	    // DocumentExceptions arise if you add content to the document before opening or
	    // after closing the document.
	    de.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return filename;
	}
		


}
