package com.gloree.actions;


import com.gloree.beans.*;
import com.gloree.utils.ProjectUtil;
import com.gloree.utils.SecurityUtil;

import java.sql.ResultSet;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;


/////////////////////////////////////////////Purpose ///////////////////////////////////////////
//
//	This servlet is used to create , delete and edit a folder.
//
///////////////////////////////////////////Purpose ///////////////////////////////////////////


public class BaselineMetricsAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public BaselineMetricsAction() {
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
		// We are taking a conscious decision to let any user be able to run baseline metrics reports.
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
		int rTBaselineId = Integer.parseInt(request.getParameter("rTBaselineId"));
		if ( action.equals("changeComparisionReportVsCurrent")){
			exportBaselineChangeComparisionReportVsCurrentToExcel( request, response, action, project,rTBaselineId, user, databaseType);
			return;
		}
		if ( action.equals("changeComparisionReportVsAnotherBaseline")){
			exportBaselineChangeComparisionReportVsAnotherBaselineToExcel( request, response, action, project,rTBaselineId, user, databaseType);
			return;
		}

		if ( action.equals("changeAfterADateReport")){
			exportBaselineChangeComparisionReportVsCurrentToExcel( request, response, action, project,rTBaselineId, user, databaseType);
			return;
		}

	}

	
	// This method uses the Apache POI module to print out XLS files.
    private void exportBaselineChangeComparisionReportVsCurrentToExcel (HttpServletRequest request,
        HttpServletResponse response, String action, Project project, int rTBaselineId, User user , String databaseType) 
    	throws ServletException, IOException {


    		RTBaseline rTBaseline = new RTBaseline(rTBaselineId);
    		String cutOffDate = "";
    		if (action.equals("changeAfterADateReport")){
    			cutOffDate = request.getParameter("cutOffDate");
    		}

    		// create a file name and set it to it.
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
    		String today =  sdf.format(cal.getTime());
    		String filename = project.getProjectName() + " Baseline " + today + ".xls";
    		filename.replace(" ", "_");

    		response.setContentType("application/vnd.ms-excel");
    		response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

    		
    		HSSFWorkbook wb = new HSSFWorkbook();
    		HSSFCreationHelper createHelper = (HSSFCreationHelper) wb.getCreationHelper(); 
    		//cell style for hyperlinks
    	    //by default hyperlinks are blue and underlined
    	    HSSFCellStyle hlink_style = wb.createCellStyle();
    	    HSSFFont hlink_font = wb.createFont();
    	    hlink_font.setColor(HSSFColor.BLUE.index);
    	    hlink_font.setUnderline(HSSFFont.U_SINGLE);
    	    hlink_style.setFont(hlink_font);   		

    	    // header cell style
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


    		if (action.equals("changeComparisionReport")) {
	    		HSSFCell cellA = row.createCell(2);
	    		cellA.setCellValue(new HSSFRichTextString ("Report Title"));
	    		cellA.setCellStyle(headerStyle);
	    		row.createCell(3).setCellStyle(headerStyle);
	    		HSSFCell cellB = row.createCell(4);
	    		cellB.setCellValue(new HSSFRichTextString ("Change Comparision Report: Baseline Vs Current"));
    		}
    		
    		if (action.equals("changeAfterADateReport")) {
	    		HSSFCell cellA = row.createCell(2);
	    		cellA.setCellValue(new HSSFRichTextString ("Report Title"));
	    		cellA.setCellStyle(headerStyle);
	    		row.createCell(3).setCellStyle(headerStyle);
	    		HSSFCell cellB = row.createCell(4);
	    		cellB.setCellValue(new HSSFRichTextString ("Change to Baseline after " + cutOffDate + " Report"));
    		}
    		
    		row   = sheet.createRow((short)startRow++);
    		HSSFCell  cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Baseline Name"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		HSSFCell  cellB = row.createCell(4);
    		cellB.setCellValue(rTBaseline.getBaselineName());

    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Baseline Description"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(rTBaseline.getBaselineDescription() );
    		
    		
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
    		cellB.setCellValue(new HSSFRichTextString (user.getEmailId()));

    		
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
    		
    	    
    		// get all Requirement Types in this project.
			// 
    		// lets run a SQL to get all the requirements in this rTBaselineId.
    		java.sql.Connection con = null;
    		try {
    			
    			javax.naming.InitialContext context = new InitialContext();
    			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
    			con = dataSource.getConnection();

    	
    			
    			String sql = "";
    			if (databaseType.equals("mySQL")){
    				sql = "select r.id, r.deleted, r.full_tag," +
    				" rbv.version \"baseline_version\" , r.version \"current_version\" ," +
    				" rbv.name \"baseline_name\" , r.name \"current_name\" , " +
    				" rbv.description \"baseline_description\" ," +
    				" r.description \"current_description\" ," +
    				" date_format(r.last_modified_dt, '%d %M %Y ') \"last_modified_dt\" " + 
    				" from gr_requirement_baselines rb, gr_requirements r, gr_requirement_versions rbv " +
    				" where r.project_id = ? " +
    				" and rb.rt_baseline_id = ? " +
    				" and rb.requirement_id = r.id " +
    				" and rb.version_id  = rbv.id";
    				
    			}
    			else {
    				 sql = "select r.id, r.deleted, r.full_tag," +
     				" rbv.version \"baseline_version\" , r.version \"current_version\" ," +
     				" rbv.name \"baseline_name\" , r.name \"current_name\" , " +
     				" rbv.description \"baseline_description\" ," +
     				" r.description \"current_description\" ," +
     				" to_char(r.last_modified_dt, 'DD MON YYYY') \"last_modified_dt\" " + 
     				" from gr_requirement_baselines rb, gr_requirements r, gr_requirement_versions rbv " +
     				" where r.project_id = ? " +
     				" and rb.rt_baseline_id = ? " +
     				" and rb.requirement_id = r.id " +
     				" and rb.version_id  = rbv.id" ;
    			}
        		if (action.equals("changeAfterADateReport")) {
        			// lets add the SQL that restricts the result to only those
        			// that have been modified after the cutOffDate.
        			if (databaseType.equals("mySQL")){
        				sql += " and  r.last_modified_dt > str_to_date(? , '%m/%d/%Y');" ;
        			}
        			else{
        				sql += " and  r.last_modified_dt > to_date(? , 'DD MON YYYY');" ;
        			}
        			
        		}
        		
    			java.sql.PreparedStatement prepStmt = con.prepareStatement(sql);
    			prepStmt.setInt(1, project.getProjectId());
    			prepStmt.setInt(2, rTBaselineId);
    			if (action.equals("changeAfterADateReport")) {
        			// lets add the SQL that restricts the result to only those
        			// that have been modified after the cutOffDate.
    				prepStmt.setString(3, cutOffDate);
    			}
    			ResultSet rs = prepStmt.executeQuery();
    			
				// lets create a new Excel page with the baseline name
				sheet = wb.createSheet(rTBaseline.getBaselineName() );
    	   		// Create a row and put some cells in it. Rows are 0 based.
        		row    = sheet.createRow((short)0);
        		// Print the header row for the excel file.
        		int column = 0;
        		HSSFCell cell0 = row.createCell(0);
        		cell0.setCellValue(new HSSFRichTextString ("Differs from Baseline?"));
        		cell0.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		HSSFCell cell1 = row.createCell(1);
        		cell1.setCellValue(new HSSFRichTextString ("Tag     "));
        		cell1.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		HSSFCell cell2 = row.createCell(2);
        		cell2.setCellValue(new HSSFRichTextString ("Version in Baseline"));
        		cell2.setCellStyle(headerStyle);    	        		
        		sheet.autoSizeColumn(column++);
        		
        		HSSFCell cell3 = row.createCell(3); 
        		cell3.setCellValue(new HSSFRichTextString ("Current Version"));
        		cell3.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		HSSFCell cell4 = row.createCell(4);
        		cell4.setCellValue(new HSSFRichTextString ("Name in Baseline                                     "));
        		cell4.setCellStyle(headerStyle);    	        		
        		sheet.autoSizeColumn(column++);
        		
        		HSSFCell cell5 = row.createCell(5); 
        		cell5.setCellValue(new HSSFRichTextString ("Current Name                                         "));
        		cell5.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		HSSFCell cell6 = row.createCell(6); 
        		cell6.setCellValue(new HSSFRichTextString ("Change Since Baseline (Name)                         "));
        		cell6.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		HSSFCell cell7 = row.createCell(7); 
        		cell7.setCellValue(new HSSFRichTextString ("Description in Baseline                              "));
        		cell7.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		HSSFCell cell8 = row.createCell(8); 
        		cell8.setCellValue(new HSSFRichTextString ("Current Description                                  "));
        		cell8.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		HSSFCell cell9 = row.createCell(9); 
        		cell9.setCellValue(new HSSFRichTextString ("Change Since Baselien (Description)                  "));
        		cell9.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		HSSFCell cell10 = row.createCell(10); 
        		cell10.setCellValue(new HSSFRichTextString ("Last Modified Date"));
        		cell10.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);    	
				
	            int j = 0;
	            
	            // for each requirement in this RT, create a new row of data.
		    	while (rs.next()) {
		    		// Create a row and put some cells in it. Rows are 0 based.
		    		j++;
		    		

		    		int requirementId = rs.getInt("id");
		    		String requirementFullTag = rs.getString("full_tag");

		    		int deleted = rs.getInt("deleted");
		    		int baselineVersion = rs.getInt("baseline_version");
		    		int currentVersion = rs.getInt("current_version");
		    		
		    		String baselineName = rs.getString("baseline_name");
		    		String currentName = rs.getString("current_name");
		    		
		    		
		    		String baselineDescription = rs.getString("baseline_description");
		    		String currentDescription = rs.getString("current_description");


		    		HSSFCellStyle cellStyle = wb.createCellStyle();
		    	    cellStyle.setWrapText(true);

		    		if (deleted == 1){
		    			currentName = "This object has been deleted";
		    			currentDescription = "This object has been deleted";
		    			currentVersion = -1;

			    	    HSSFFont changedFont = wb.createFont();
			    	    changedFont.setColor(HSSFColor.RED.index);
			    	    cellStyle.setFont(changedFont);   		

		    		}
		
		    		if (baselineVersion == currentVersion){

			    	    HSSFFont changedFont = wb.createFont();
		    		    changedFont.setColor(HSSFColor.GREEN.index);
			    	    cellStyle.setFont(changedFont);   		

		    		}
		    		if (
		    				(deleted == 0 )
		    				&&
		    				(baselineVersion != currentVersion)
		    			){
		    			HSSFFont changedFont = wb.createFont();
		    		    changedFont.setColor(HSSFColor.BLUE.index);
			    	    cellStyle.setFont(changedFont);   		

		    		}
		    	    
		    	    
		    	    
		    		String lastModifiedDt = rs.getString("last_modified_dt");
		    		
		    		// print the data rows now.
		    		row     = sheet.createRow(j);
		    		column = 0;
		    		
		    		
				    // Create a cell and put a value in it for Changed or not.
		    		if (baselineVersion != currentVersion) {
		    			cell0 = row.createCell(0);
		    			cell0.setCellValue(new HSSFRichTextString ("Yes"));
		    			cell0.setCellStyle(cellStyle);
		    		}
		    		else {
		    			cell0 = row.createCell(0);
		    			cell0.setCellValue(new HSSFRichTextString ("No"));
		    			cell0.setCellStyle(cellStyle);
		    		}
		    		
				    // Create a cell and put a hyperlink to the req tag in it.		    		
				    String url = ProjectUtil.getURL(request,requirementId ,"requirement");
				    cell1 = row.createCell(1);
				    cell1.setCellValue(new HSSFRichTextString (requirementFullTag));
				    
				    // make cell1 a hyperlink
				    HSSFHyperlink link = createHelper.createHyperlink(HSSFHyperlink.LINK_URL);
				    link.setAddress(url);
				    cell1.setHyperlink(link);
				    cell1.setCellStyle(hlink_style);
				    
				    cell2 = row.createCell(2);
				    cell2.setCellValue(new HSSFRichTextString ("V-" + baselineVersion));
				    cell2.setCellStyle(cellStyle);
				    
				    cell3 = row.createCell(3);
				    if (deleted == 1){
				    	cell3.setCellValue(new HSSFRichTextString ("Deleted"));
					    
				    }
				    else {
				    	cell3.setCellValue(new HSSFRichTextString ("V-" + currentVersion));
				    }
				    cell3.setCellStyle(cellStyle);
				    
				    cell4 = row.createCell(4);
				    cell4.setCellValue(new HSSFRichTextString (baselineName));
				    cell4.setCellStyle(cellStyle);
		    		
				    
				    cell5 = row.createCell(5);
				    cell5.setCellValue(new HSSFRichTextString (currentName));
				    cell5.setCellStyle(cellStyle);
				    
				    String changeInName = StringUtils.difference(baselineName, currentName);
				    cell6 = row.createCell(6);
				    cell6.setCellValue(new HSSFRichTextString (changeInName));
				    cell6.setCellStyle(cellStyle);
				    
				    String baselineDescriptionNoHTML = "";
				    if (baselineDescription != null) {
				    	baselineDescriptionNoHTML = baselineDescription.replaceAll("\\<.*?>","");
				    	baselineDescriptionNoHTML = baselineDescriptionNoHTML.replaceAll("&nbsp;", " ");
				    }
				    cell7 = row.createCell(7);
				    cell7.setCellValue(new HSSFRichTextString (baselineDescriptionNoHTML));
				    cell7.setCellStyle(cellStyle);
				    
				    String currentDescriptionNoHTML = "";
				    if (currentDescription != null) {
				    	currentDescriptionNoHTML = currentDescription.replaceAll("\\<.*?>","");
				    	currentDescriptionNoHTML = currentDescriptionNoHTML.replaceAll("&nbsp;", " ");
				    }
				    cell8 = row.createCell(8);
				    cell8.setCellValue(new HSSFRichTextString (currentDescriptionNoHTML));
				    cell8.setCellStyle(cellStyle);
				    
				    String changeInDescription = StringUtils.difference(baselineDescriptionNoHTML, currentDescriptionNoHTML);
				    cell9 = row.createCell(9);
				    cell9.setCellValue(new HSSFRichTextString (changeInDescription));
				    cell9.setCellStyle(cellStyle);
				    
				    cell10 = row.createCell(10);
				    cell10.setCellValue(new HSSFRichTextString (lastModifiedDt));
				    cell10.setCellStyle(cellStyle);
		    	}
		    	// lets close the db connection.
		    	con.close();
    		} catch (Exception e) {
				
				e.printStackTrace();
			}   finally {
				if (con != null) {
					try {con.close();} catch (Exception e) {}
					con = null;
				}
			}
		    
            // Write the output 
            OutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();
    }    
	
	
	
	
	// This method uses the Apache POI module to print out XLS files.
    private void exportBaselineChangeComparisionReportVsAnotherBaselineToExcel (HttpServletRequest request,
        HttpServletResponse response, String action, Project project, int rTBaselineId, User user, String databaseType) 
    	throws ServletException, IOException {


    		RTBaseline rTBaseline = new RTBaseline(rTBaselineId);
    		int compareAgainstRTBaselineId = Integer.parseInt(request.getParameter("compareAgainstRTBaselineId"));
    		RTBaseline compareAgainstRTBaseline = new RTBaseline(compareAgainstRTBaselineId); 
    		
    		
    		// create a file name and set it to it.
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
    		String today =  sdf.format(cal.getTime());
    		String filename = project.getProjectName() + " BaselineVsBaseline " + today + ".xls";
    		filename.replace(" ", "_");
    		
    		response.setContentType("application/vnd.ms-excel");
    		response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

			
    		
    		HSSFWorkbook wb = new HSSFWorkbook();
    		HSSFCreationHelper createHelper = (HSSFCreationHelper) wb.getCreationHelper(); 
    		//cell style for hyperlinks
    	    //by default hyperlinks are blue and underlined
    	    HSSFCellStyle hlink_style = wb.createCellStyle();
    	    HSSFFont hlink_font = wb.createFont();
    	    hlink_font.setColor(HSSFColor.BLUE.index);
    	    hlink_font.setUnderline(HSSFFont.U_SINGLE);
    	    hlink_style.setFont(hlink_font);   		

    	    // header cell style
    		HSSFCellStyle headerStyle = wb.createCellStyle();
    		headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
    	    headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    

    	    HSSFCellStyle notChangedStyle = wb.createCellStyle();
    	    HSSFFont notChangedFont = wb.createFont();
    	    notChangedFont.setColor(HSSFColor.GREEN.index);
    	    notChangedStyle.setFont(notChangedFont);   		

    	    HSSFCellStyle changedStyle = wb.createCellStyle();
    	    HSSFFont changedFont = wb.createFont();
    	    changedFont.setColor(HSSFColor.RED.index);
    	    changedStyle.setFont(changedFont);   		

    	    HSSFCellStyle wrappedStyle = wb.createCellStyle();
    	    wrappedStyle.setWrapText(true);
    	        	    
    	    
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
    		cellB.setCellValue(new HSSFRichTextString ("Change Comparision Report: Baseline Vs Baseline"));
    		
    		row   = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Baseline1 Name"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(rTBaseline.getBaselineName());
    		
    		
    		
    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Baseline1 Description"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(rTBaseline.getBaselineDescription() );
    		
    		row   = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Baseline2 Name"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(compareAgainstRTBaseline.getBaselineName());

    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Baseline2 Description"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(rTBaseline.getBaselineDescription() );

    		
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
    		cellB.setCellValue(new HSSFRichTextString (user.getEmailId()));

    		
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
    		
    	    
			// 
    		// lets run a SQL to get all the requirements in this rTBaselineId.
    		java.sql.Connection con = null;
    		try {
    			
    			javax.naming.InitialContext context = new InitialContext();
    			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
    			con = dataSource.getConnection();

    			// Note : 
    			// Sorry for the complex sql, but once you understand it , its quite simple and efficient
    			// The goal is to get all the reqs in 2 base lines .
    			// The core is the sql that gets all the reqs in a baseline and the baseline version, req name and description.
    			// once we have that, we left join the contents in baseline 1 with the contents in baseline2 
    			// this gets all the reqs in Baseline 1. 
    			// Now we do a right join of baseline 1 with Baseline 2. This get ALL the reqs in Baseline 2
    			// Then we do a union to get rid of the duplicates.
    			String sql = "";
    			if (databaseType.equals("mySQL")){
    				sql = 
        				"select " +
        					" b1.requirement_type_id, b1.tag_level1, b1.tag_level2, b1.tag_level3, b1.tag_level4, " +
        					" b1.id, b1.full_tag, b1.version \"b1_baseline_version\", b2.version \"b2_baseline_version\", "+
        					" b1.name \"b1_baseline_name\" , b2.name \"b2_baseline_name\", " +
        					" b1.description \"b1_baseline_description\" ,b2.description \"b2_baseline_description\" " +
        				" from ( " +
        					" select " +
        						" r.requirement_type_id, r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4, " +
        						" r.id, r.full_tag, rbv.version   , " +
        						" rbv.name  , "+
        						" rbv.description  " +
        					" from gr_requirement_baselines rb, gr_requirements r, gr_requirement_versions rbv " +
        					" where r.project_id = ? " +
        						" and rb.rt_baseline_id = ? " + 
        						" and rb.requirement_id = r.id " +
        						" and rb.version_id  = rbv.id " + 
        						" and r.deleted=0) b1" +
        					" left join ( " +
    	    					" select " +
    	    						" r.id,rbv.version  , " +
    	    						" rbv.name , "+
    	    						" rbv.description " +
    	    					" from gr_requirement_baselines rb, gr_requirements r, gr_requirement_versions rbv " +
    	    					" where r.project_id = ? " +
    	    					" and rb.rt_baseline_id = ? " +
    	    					" and rb.requirement_id = r.id " +
    	    					" and rb.version_id  = rbv.id " +
    	    					" and r.deleted=0 " + 
    	    					" ) b2 " +
    	    				" on b1.id = b2.id " +
        				" union " +
        				"select " +
        					" b2.requirement_type_id, b2.tag_level1, b2.tag_level2, b2.tag_level3, b2.tag_level4, " +
        					" b2.id, b2.full_tag, b1.version \"b1_baseline_version\", b2.version \"b2_baseline_version\", "+
    						" b1.name \"b1_baseline_name\" , b2.name \"b2_baseline_name\", " +
    						" b1.description \"b1_baseline_description\" ,b2.description \"b2_baseline_description\" " +
    					" from ( " +
    						" select " +
    							" r.id,rbv.version  , " +
    							" rbv.name  , "+
    							" rbv.description " +
    						" from gr_requirement_baselines rb, gr_requirements r, gr_requirement_versions rbv " +
    						" where r.project_id = ? " +
    							" and rb.rt_baseline_id = ? " + 
    							" and rb.requirement_id = r.id " +
    							" and rb.version_id  = rbv.id " + 
    							" and r.deleted=0) b1" +
    						" right join ( " +
    							" select " +
    								" r.requirement_type_id, r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4, " +
    								" r.id,r.full_tag, rbv.version  , " +
    								" rbv.name  , "+
    								" rbv.description  " +
    							" from gr_requirement_baselines rb, gr_requirements r, gr_requirement_versions rbv " +
    							" where r.project_id = ? " +
    							" and rb.rt_baseline_id = ? " +
    							" and rb.requirement_id = r.id " +
    							" and rb.version_id  = rbv.id " +
    							" and r.deleted=0 " + 
    							" ) b2 " +
    						" on b1.id = b2.id " +
    					" order by requirement_type_id, tag_level1, tag_level2, tag_level3, tag_level4 " ;
    			}
    			else {
    				sql = 
        				"select " +
        					" b1.requirement_type_id, b1.tag_level1, b1.tag_level2, b1.tag_level3, b1.tag_level4, " +
        					" b1.id, b1.full_tag, b1.version \"b1_baseline_version\", b2.version \"b2_baseline_version\", "+
        					" b1.name \"b1_baseline_name\" , b2.name \"b2_baseline_name\", " +
        					" to_char(b1.description) \"b1_baseline_description\" , to_char(b2.description) \"b2_baseline_description\" " +
        				" from ( " +
        					" select " +
        						" r.requirement_type_id, r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4, " +
        						" r.id, r.full_tag, rbv.version   , " +
        						" rbv.name  , "+
        						" rbv.description  " +
        					" from gr_requirement_baselines rb, gr_requirements r, gr_requirement_versions rbv " +
        					" where r.project_id = ? " +
        						" and rb.rt_baseline_id = ? " + 
        						" and rb.requirement_id = r.id " +
        						" and rb.version_id  = rbv.id " + 
        						" and r.deleted=0) b1" +
        					" left join ( " +
    	    					" select " +
    	    						" r.id,rbv.version  , " +
    	    						" rbv.name , "+
    	    						" rbv.description " +
    	    					" from gr_requirement_baselines rb, gr_requirements r, gr_requirement_versions rbv " +
    	    					" where r.project_id = ? " +
    	    					" and rb.rt_baseline_id = ? " +
    	    					" and rb.requirement_id = r.id " +
    	    					" and rb.version_id  = rbv.id " +
    	    					" and r.deleted=0 " + 
    	    					" ) b2 " +
    	    				" on b1.id = b2.id " +
        				" union " +
        				"select " +
        					" b2.requirement_type_id, b2.tag_level1, b2.tag_level2, b2.tag_level3, b2.tag_level4, " +
        					" b2.id, b2.full_tag, b1.version \"b1_baseline_version\", b2.version \"b2_baseline_version\", "+
    						" b1.name \"b1_baseline_name\" , b2.name \"b2_baseline_name\", " +
    						" to_char(b1.description) \"b1_baseline_description\" , to_char(b2.description) \"b2_baseline_description\" " +
    					" from ( " +
    						" select " +
    							" r.id,rbv.version  , " +
    							" rbv.name  , "+
    							" rbv.description " +
    						" from gr_requirement_baselines rb, gr_requirements r, gr_requirement_versions rbv " +
    						" where r.project_id = ? " +
    							" and rb.rt_baseline_id = ? " + 
    							" and rb.requirement_id = r.id " +
    							" and rb.version_id  = rbv.id " + 
    							" and r.deleted=0) b1" +
    						" right join ( " +
    							" select " +
    								" r.requirement_type_id, r.tag_level1, r.tag_level2, r.tag_level3, r.tag_level4, " +
    								" r.id,r.full_tag, rbv.version  , " +
    								" rbv.name  , "+
    								" rbv.description  " +
    							" from gr_requirement_baselines rb, gr_requirements r, gr_requirement_versions rbv " +
    							" where r.project_id = ? " +
    							" and rb.rt_baseline_id = ? " +
    							" and rb.requirement_id = r.id " +
    							" and rb.version_id  = rbv.id " +
    							" and r.deleted=0 " + 
    							" ) b2 " +
    						" on b1.id = b2.id " +
    					" order by requirement_type_id, tag_level1, tag_level2, tag_level3, tag_level4 " ;
    			}
    			
    			
    			
    			java.sql.PreparedStatement prepStmt = con.prepareStatement(sql);
    			prepStmt.setInt(1, project.getProjectId());
    			prepStmt.setInt(2, rTBaselineId);
    			prepStmt.setInt(3, project.getProjectId());
    			prepStmt.setInt(4, compareAgainstRTBaselineId);
    			prepStmt.setInt(5, project.getProjectId());
    			prepStmt.setInt(6, rTBaselineId);
    			prepStmt.setInt(7, project.getProjectId());
    			prepStmt.setInt(8, compareAgainstRTBaselineId);
    			
    			
    			
    			ResultSet rs = prepStmt.executeQuery();
    			
				// lets create a new Excel page with the baseline name
				sheet = wb.createSheet("Change Comparision between 2 Baselines" );
    	   		// Create a row and put some cells in it. Rows are 0 based.
        		row    = sheet.createRow((short)0);
        		// Print the header row for the excel file.
        		int column = 0;
        		
        		HSSFCell cell = row.createCell(column);
        		cell.setCellValue(new HSSFRichTextString ("Tag     "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		
        		cell = row.createCell(column);
        		cell.setCellValue(new HSSFRichTextString ("Baseline 1 Version"));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(column);
        		cell.setCellValue(new HSSFRichTextString ("Baseline 2 Version"));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(column);
        		cell.setCellValue(new HSSFRichTextString ("Baseline 1 Name                                      "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(column);
        		cell.setCellValue(new HSSFRichTextString ("Baseline 2 Name                                      "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(column);
        		cell.setCellValue(new HSSFRichTextString ("Change in Name                                      "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(column);
        		cell.setCellValue(new HSSFRichTextString ("Baseline 1 Description                               "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(column);
        		cell.setCellValue(new HSSFRichTextString ("Baseline 2 Description                               "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(column);
        		cell.setCellValue(new HSSFRichTextString ("Change in Description                                "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		
	            int j = 0;
	            
	            // for each requirement in this RT, create a new row of data.
		    	while (rs.next()) {
		    		// Create a row and put some cells in it. Rows are 0 based.
		    		j++;
		    		

		    		int requirementId = rs.getInt("id");
		    		String requirementFullTag = rs.getString("full_tag");
		    		
		    		int b1BaselineVersion = rs.getInt("b1_baseline_version");
		    		int b2BaselineVersion = rs.getInt("b2_baseline_version");
		    		
		    		String b1BaselineName = rs.getString("b1_baseline_name");
		    		String b2BaselineName = rs.getString("b2_baseline_name");

		    		String b1BaselineDescription = rs.getString("b1_baseline_description");
		    		String b2BaselineDescription = rs.getString("b2_baseline_description");
		    		
		    				    		
		    		// print the data rows now.
		    		row     = sheet.createRow(j);
		    		column = 0;
		    		
		    		
				    // Create a cell and put a hyperlink to the req tag in it.		    		
				    String url = ProjectUtil.getURL(request,requirementId ,"requirement");
				    cell = row.createCell(column++);
				    cell.setCellValue(new HSSFRichTextString (requirementFullTag));
				    
				    // make cell a hyperlink
				    HSSFHyperlink link = createHelper.createHyperlink(HSSFHyperlink.LINK_URL);
				    link.setAddress(url);
				    cell.setHyperlink(link);
				    cell.setCellStyle(hlink_style);
				    
				    cell = row.createCell(column++);
				    cell.setCellValue(new HSSFRichTextString ("V-" + b1BaselineVersion));
				    cell.setCellStyle(wrappedStyle);
				    
				    cell = row.createCell(column++);
				    cell.setCellValue(new HSSFRichTextString ("V-" + b2BaselineVersion));
				    cell.setCellStyle(wrappedStyle);
				    
				    cell = row.createCell(column++);
				    cell.setCellValue(new HSSFRichTextString (b1BaselineName));
				    cell.setCellStyle(wrappedStyle);
		    		
				    
				    cell = row.createCell(column++);
				    cell.setCellValue(new HSSFRichTextString (b2BaselineName));
				    cell.setCellStyle(wrappedStyle);
				    
				    String changeInName = StringUtils.difference(b1BaselineName, b2BaselineName);
				    cell = row.createCell(column++);
				    cell.setCellValue(new HSSFRichTextString (changeInName));
				    cell.setCellStyle(wrappedStyle);
				    
				    String b1BaselineDescriptionNoHTML = "";
				    if (b1BaselineDescription != null) {
				    	b1BaselineDescriptionNoHTML = b1BaselineDescription.replaceAll("\\<.*?>","");
				    	b1BaselineDescriptionNoHTML = b1BaselineDescriptionNoHTML.replaceAll("&nbsp;", " ");
				    }
				    cell = row.createCell(column++);
				    cell.setCellValue(new HSSFRichTextString (b1BaselineDescriptionNoHTML));
				    cell.setCellStyle(wrappedStyle);
				    
				    String b2BaselineDescriptionNoHTML = "";
				    if (b2BaselineDescription != null) {
				    	b2BaselineDescriptionNoHTML = b2BaselineDescription.replaceAll("\\<.*?>","");
				    	b2BaselineDescriptionNoHTML = b2BaselineDescriptionNoHTML.replaceAll("&nbsp;", " ");
				    }
				    cell = row.createCell(column++);
				    cell.setCellValue(new HSSFRichTextString (b2BaselineDescriptionNoHTML));
				    cell.setCellStyle(wrappedStyle);
				    
				    String changeInDescription = StringUtils.difference(b1BaselineDescriptionNoHTML, b2BaselineDescriptionNoHTML);
				    cell = row.createCell(column++);
				    cell.setCellValue(new HSSFRichTextString (changeInDescription));
				    cell.setCellStyle(wrappedStyle);
				    
		    	}
		    	// lets close the db connection.
		    	con.close();
    		} catch (Exception e) {
				
				e.printStackTrace();
			}   finally {
				if (con != null) {
					try {con.close();} catch (Exception e) {}
					con = null;
				}
			}
		    
            // Write the output 
            OutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();
    }	
	
}
