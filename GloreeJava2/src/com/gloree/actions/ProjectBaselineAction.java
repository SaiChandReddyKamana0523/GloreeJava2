package com.gloree.actions;


import com.gloree.beans.*;
import com.gloree.utils.ProjectUtil;
import com.gloree.utils.SecurityUtil;

import java.util.ArrayList;
import java.util.Iterator;


import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



// Excel POI stuff.














import org.apache.poi.hssf.dev.HSSF;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


//
//	This servlet is used to create a project baseline. Which is nothing but an excel dump
/////////////////////////////////////////////Purpose ///////////////////////////////////////////
// of the entire project.
//
// NOTE : THIS IS A MISNOMER. IT IS IN NO WAY RELATED TO REQUIREMENTTYPE BASELINES . 
// IT'S A DUMP OF THE PROJECT DATA, AND IS DRIVEN BY THE PROJECT EXPORT LINK IN THE TOOLBAR.
///////////////////////////////////////////Purpose ///////////////////////////////////////////



public class ProjectBaselineAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ProjectBaselineAction() {
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
		
		
		
		///////////////////////////////SECURITY//////////////////////////////
		
		String action = request.getParameter("action");

		if (action.equals("exportBaselineToExcel")){
			// lets pick up the baseline object from session memory and print it out.
			exportBaselineToExcel(securityProfile, request, response, project, securityProfile.getUser(), databaseType);
		}
	}
	
	// This method uses the Apache POI module to print out XLS files.
    private void exportBaselineToExcel (SecurityProfile securityProfile, HttpServletRequest request,
            HttpServletResponse response, Project project, User user, String databaseType) throws ServletException, IOException {

    		response.setContentType("application/vnd.ms-excel");

    		// create a file name and set it to it.
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
    		String today =  sdf.format(cal.getTime());
    		String projectName = project.getProjectName();
    		if (projectName.length() > 20) {
    			projectName = projectName.substring(0,19);
    		}
    		String filename = projectName + " Baseline " + today + ".xlsx";
    		filename.replace(" ", "_");
    		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

    		XSSFWorkbook wb = new XSSFWorkbook();
    		XSSFCreationHelper createHelper = (XSSFCreationHelper) wb.getCreationHelper(); 
    		//cell style for hyperlinks
    	    //by default hyperlinks are blue and underlined
    	    XSSFCellStyle hlink_style = wb.createCellStyle();
    	    XSSFFont hlink_font = wb.createFont();
    	    hlink_font.setColor(HSSFColor.BLUE.index);
    	    hlink_font.setUnderline(XSSFFont.U_SINGLE);
    	    hlink_style.setFont(hlink_font);   		

    	    // header cell style
    		XSSFCellStyle headerStyle = wb.createCellStyle();
    		headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
    	    headerStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
    	    
    	    XSSFCellStyle pendingStyle = wb.createCellStyle();
    	    XSSFFont pendingFont = wb.createFont();
    	    pendingFont.setColor(HSSFColor.INDIGO.index);
    	    pendingStyle.setFont(pendingFont);   		
    	    pendingStyle.setWrapText(true);
    	    
    	    XSSFCellStyle approvedStyle = wb.createCellStyle();
    	    XSSFFont approvedFont = wb.createFont();
    	    approvedFont.setColor(HSSFColor.GREEN.index);
    	    approvedStyle.setFont(approvedFont);   		
    	    approvedStyle.setWrapText(true);
    	    
    	    XSSFCellStyle rejectedStyle = wb.createCellStyle();
    	    XSSFFont rejectedFont = wb.createFont();
    	    rejectedFont.setColor(HSSFColor.RED.index);
    	    rejectedStyle.setFont(rejectedFont);   		
    	    rejectedStyle.setWrapText(true);
    	    
    	    XSSFCellStyle wrappedStyle = wb.createCellStyle();
    	    wrappedStyle.setWrapText(true);
    	   
    	    XSSFCellStyle testingPendingStyle = wb.createCellStyle();
    	    testingPendingStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
    	    testingPendingStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
    	   
    	    XSSFCellStyle testingPassStyle = wb.createCellStyle();
    	    testingPassStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
    	    testingPassStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
     	   
    	    XSSFCellStyle testingFailStyle = wb.createCellStyle();
    	    testingFailStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
    	    testingFailStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
     	   
    	    /////////////////////////////////////////
    	    //
    	    // lets build the Report Cover Page.
    	    //
    	    /////////////////////////////////////////
    	    
    	    XSSFSheet sheet  = wb.createSheet("Report Info");
    	    // lets start on the 5th Row.
    	    int startRow = 5; 
    		XSSFRow row     = sheet.createRow((short)startRow++);

    		
    		XSSFCell cellA = row.createCell(2);
    		cellA.setCellValue(new XSSFRichTextString ("Report Title"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		XSSFCell cellB = row.createCell(4);
    		cellB.setCellValue(new XSSFRichTextString ("Project Export"));

    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new XSSFRichTextString ("Report Date"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new XSSFRichTextString (today));
    		
    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new XSSFRichTextString ("Report Generated By "));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new XSSFRichTextString (user.getEmailId()));

    		
    		// project Info.
    		startRow += 4;
    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new XSSFRichTextString ("Project Prefix"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new XSSFRichTextString (project.getShortName()));

    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new XSSFRichTextString ("Project Name"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new XSSFRichTextString (project.getProjectName()));

    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new XSSFRichTextString ("Project Description"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new XSSFRichTextString (project.getProjectDescription()));
    		
	    	try{
	    		// get all Requirement Types in this project.
	    		ArrayList requirementTypes = project.getMyRequirementTypes();
	    		Iterator rTI = requirementTypes.iterator();
	    		
	    		
	    		while (rTI.hasNext()){
	    			
	    			    			

		    		RequirementType rt = (RequirementType) rTI.next();
	    			//if  (rt.getRequirementTypeShortName().equals("FR")){
	    			//	System.out.println("srt skipping FRs " );
		    		//	continue;
	    			//}
	    			//For each requirement Type get an arryList of requirements in this Requirement Type. 
	    			ArrayList requirements = ProjectUtil.getAllRequirementsInRT(rt.getRequirementTypeId(), "all", databaseType);
	    			
	        	
	
	    			// lets create a new Excel page for each Requirement Type
	    			sheet = wb.createSheet(rt.getRequirementTypeName());
	                Iterator i = requirements.iterator();
	                
	                int j = 0;
	               
	                // for each requirement in this RT, create a new row of data.
	    	    	while ( i.hasNext() ) {
	    	    		Requirement r = (Requirement) i.next();
	    	    		
	    	    		 if (r == null){
	    	    			 continue;
	    	    		 }
	    	    		// lets redact it. i.e. remove all sensitive infor from it.
	    				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
	    						+ r.getFolderId()))){
	    					r.redact();
	    				}
	    				
	    				
	    	    		// a typical uda looks like this 
	    	    		// Customer:#: SBI:##:Delivery Estimate:#:01/01/12
	    	    		String uda = r.getUserDefinedAttributes();
	    				String[] attribs = uda.split(":##:");
	    				HashMap<String,String> attribsHashMap = ProjectUtil.getHashMapUDA(r.getUserDefinedAttributes());
	    				ArrayList<String> attributeNames = new ArrayList<String>(attribsHashMap.keySet());
	    				Collections.sort(attributeNames);
	    				
	    	    		// Create a row and put some cells in it. Rows are 0 based.
	    	    		j++;
	    	    		
	    	    		// for the first row, print the header and user defined columns headers. etc..
	    	    		if (j == 1){
	
	    	        		// Create a row and put some cells in it. Rows are 0 based.
	    	        		row    = sheet.createRow((short)0);
	    	        		
	
	
	    	        		
	    	        		// Print the header row for the excel file.
	    	        		int cellNum = 0;
	    	        		int column = 0;
	    	        		XSSFCell cell = row.createCell(cellNum);
	    	        		cell.setCellValue(new XSSFRichTextString ("Deleted Requirement?"));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		
	    	        		cell = row.createCell(++cellNum);
	    	        		cell.setCellValue(new XSSFRichTextString ("Tag     "));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum);
	    	        		cell.setCellValue(new XSSFRichTextString ("URL To Requirement                                                                                                "));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum);
	    	        		cell.setCellValue(new XSSFRichTextString ("Version"));
	    	        		cell.setCellStyle(headerStyle);    	        		
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("Name                                                                                                              "));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("Description (Plain)                                                                                                "));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("Description (HTML)                                                                                                 "));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		
	    	        		cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("Owner                                  "));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("Percent Complete"));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("Priority"));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("Approval Status      "));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("Approved Dt                             "));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("Pending Approval By                "));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("Approved By                        "));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("Rejected By                        "));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("Trace To                           "));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("Trace From                         "));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("External URL                                                                                            "));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("Folder Path                                                                                             "));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("Baselines                          "));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("Created Date                              "));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("Attachments                         "));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    	        		
	    	        		
	    					Iterator aN = attributeNames.iterator();
	    					while (aN.hasNext()){
	    						
	    						String attributeName = "";
	    						try{
		    						attributeName = (String) aN.next();
		    						if(attributeName == null){attributeName = " ";}
		    						
		    						
		    						cell = row.createCell(++cellNum); 
		    		        		cell.setCellValue(new XSSFRichTextString (attributeName + "             "));
		    						cell.setCellStyle(headerStyle);
		    		        		sheet.autoSizeColumn(column++);
	    						}
	    		        		catch(Exception aNameException){
	    							aNameException.printStackTrace();
	    						}
	    						
	    						
	    					}
	    					cell = row.createCell(++cellNum); 
	    	        		cell.setCellValue(new XSSFRichTextString ("Testing Status"));
	    	        		cell.setCellStyle(headerStyle);
	    	        		sheet.autoSizeColumn(column++);
	    	        		
	    				}
	
	    	    		// print the data rows now.
	    	    		row     = sheet.createRow(j);
	    	    		int cellNum = 0;
	    	    		
	
	    			    // Create a cell and put a value in it.
	    	    		if (r.getDeleted() == 0) {
	    	    			row.createCell(cellNum).setCellValue(new XSSFRichTextString ("No"));
	    	    		}
	    	    		else {
	    	    			row.createCell(cellNum).setCellValue(new XSSFRichTextString ("Yes"));
	    	    		}
	    			    
	
	    			    String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");
	    			    XSSFCell cell = row.createCell(++cellNum);
	    			    cell.setCellValue(new XSSFRichTextString (r.getRequirementFullTag()));
	    			    
	    			    // make cell0 a hyperlink
	    			    XSSFHyperlink link = createHelper.createHyperlink(XSSFHyperlink.LINK_URL);
	    			    link.setAddress(url);
	    			    cell.setHyperlink(link);
	    			    cell.setCellStyle(hlink_style);
	
	    			    cell = row.createCell(++cellNum);
	    			    cell.setCellValue(new XSSFRichTextString (url));
	    			    cell.setCellStyle(wrappedStyle);
	    			    
	    			    cell = row.createCell(++cellNum);
	    			    cell.setCellValue(new XSSFRichTextString (Integer.toString(r.getVersion())));
	    			    cell.setCellStyle(wrappedStyle);
	    			    
	    			    cell = row.createCell(++cellNum);
	    			    cell.setCellValue(new XSSFRichTextString (r.getRequirementName()));
	    			    cell.setCellStyle(wrappedStyle);
	    			    
	    			    cell = row.createCell(++cellNum);
	    			    cell.setCellValue(new XSSFRichTextString (r.getRequirementDescriptionNoHTML()));
	    			    cell.setCellStyle(wrappedStyle);
	    			    
	    			    cell = row.createCell(++cellNum);
	    			    cell.setCellValue(new XSSFRichTextString (r.getRequirementDescription()));
	    			    cell.setCellStyle(wrappedStyle);
	    			    
	    			    cell = row.createCell(++cellNum);
	    			    cell.setCellValue(new XSSFRichTextString (r.getRequirementOwner()));
	    			    cell.setCellStyle(wrappedStyle);
	    			    
	    			    cell = row.createCell(++cellNum);
	    			    cell.setCellValue(new XSSFRichTextString (r.getRequirementPctComplete() + ""));
	    			    cell.setCellStyle(wrappedStyle);
	    			    
	    			    cell = row.createCell(++cellNum);
	    			    cell.setCellValue(new XSSFRichTextString (r.getRequirementPriority()));
	    			    cell.setCellStyle(wrappedStyle);
	    			    
		        	    // lets set the status Cell color based on its value
		        	    cell = row.createCell(++cellNum);
		        	    cell.setCellValue(new XSSFRichTextString (r.getApprovalStatus()));
	
		        	    XSSFCellStyle statusStyle = wb.createCellStyle();
					    if (r.getApprovalStatus().equals("Draft")){
			        		statusStyle.setFillForegroundColor(HSSFColor.PINK.index);
			        	    statusStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			        	}
					    if (r.getApprovalStatus().equals("In Approval WorkFlow")){
			        		statusStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
			        	    statusStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			        	}
					    if (r.getApprovalStatus().equals("Approved")){
			        		statusStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			        	    statusStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			        	}
					    if (r.getApprovalStatus().equals("Rejected")){
			        		statusStyle.setFillForegroundColor(HSSFColor.RED.index);
			        	    statusStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			        	}			    
					    cell.setCellStyle(statusStyle);
		        		
		        		 
					    row.createCell(++cellNum).setCellValue(new XSSFRichTextString (r.getApprovedByAllDt() ));
	
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
					    
					    
					    XSSFCell  pendingCell = row.createCell(++cellNum);
					    pendingCell.setCellValue(new XSSFRichTextString (pendingApprovers ));
					    pendingCell.setCellStyle(pendingStyle);
	
					    
					    XSSFCell  approvedCell = row.createCell(++cellNum);
					    approvedCell.setCellValue(new XSSFRichTextString (approvedApprovers ));
					    approvedCell.setCellStyle(approvedStyle);
					    
					    XSSFCell  rejectedCell = row.createCell(++cellNum);
					    rejectedCell.setCellValue(new XSSFRichTextString (rejectedApprovers ));
					    rejectedCell.setCellStyle(rejectedStyle);
					    
	    			    cell = row.createCell(++cellNum);
	    			    cell.setCellValue(new XSSFRichTextString (r.getRequirementTraceTo() ));
	    			    // commenting this out, as this can be very very long for releases
	    			    //cell.setCellStyle(wrappedStyle);
	    			    
	    			    cell = row.createCell(++cellNum);
	    			    cell.setCellValue(new XSSFRichTextString (r.getRequirementTraceFrom() ));
	    			 // commenting this out, as this can be very very long for releases
	    			    //cell.setCellStyle(wrappedStyle);
	    			    
	    			    
	    			    if (!(r.getRequirementExternalUrl().equals(""))){
		    			    cell = row.createCell(++cellNum);
		    			    cell.setCellValue(new XSSFRichTextString (r.getRequirementExternalUrl()));
		    			    XSSFHyperlink link2 = createHelper.createHyperlink(XSSFHyperlink.LINK_URL);
		    			
		    			    link2.setAddress(r.getRequirementExternalUrl() );
		    			    cell.setHyperlink(link2);
		    			    cell.setCellStyle(hlink_style);
	    			    }
	    			    else{
	    			    	cell = row.createCell(++cellNum);
		    		    	cell.setCellValue(" ");
		    		    	cell.setCellStyle(wrappedStyle);
	    			    }
	    		    	
	    		    	cell = row.createCell(++cellNum);
	    		    	cell.setCellValue(r.getFolderPath());
	    		    	cell.setCellStyle(wrappedStyle);
	    		    	
	    			    cell = row.createCell(++cellNum);
	    			    cell.setCellValue( r.getRequirementBaselineString(databaseType));
	    			    cell.setCellStyle(wrappedStyle);
	    			    
	    			    cell = row.createCell(++cellNum);
	    			    cell.setCellValue(r.getCreatedDt());
	    			    cell.setCellStyle(wrappedStyle);
    			    
    			   
	    			    
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
				    	cell.setCellValue(new XSSFRichTextString (attachmentString ));
				    	cell.setCellStyle(wrappedStyle);
	    			    
	
    			    
	    			    
	    			    // now to print the custom values.
	    				
	    				Iterator aN = attributeNames.iterator();
	    				
    					while (aN.hasNext()){
    						
    						String attribValue = " ";
    						try{
    							String attributeName = (String) aN.next();
        						attribValue = (String) attribsHashMap.get(attributeName.trim());
    						
		    					cell = row.createCell(++cellNum); 
	    		        		cell.setCellValue(new XSSFRichTextString (attribValue));
	    		        		cell.setCellStyle(wrappedStyle);
    						}
        					catch(Exception aValException){
    							aValException.printStackTrace();
    						}
    						
    						
    					}
	    				

				    	cell = row.createCell(++cellNum);
	    			    cell.setCellValue(r.getTestingStatus() );
	    			   
					    if (r.getTestingStatus().equals("Pending")){
					    	cell.setCellStyle(testingPendingStyle);
			        	}
					    if (r.getTestingStatus().equals("Pass")){
					    	cell.setCellStyle(testingPassStyle);
			        	}
					    if (r.getTestingStatus().equals("Fail")){
					    	cell.setCellStyle(testingFailStyle);
			        	}
					   	
		        		
	    	    	}
	    		}
	    	}
    		catch (Exception e){
    			System.out.println("srt ran into error in ProjectBaselineAction");
    			e.printStackTrace();
    		}
    		// Write the output 
            OutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();
            
           
    }    
}
