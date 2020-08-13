package com.gloree.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.json.JSONArray;
import org.json.JSONObject;

import com.gloree.beans.DT_Task;
import com.gloree.beans.Folder;
import com.gloree.beans.GlobalRequirement;
import com.gloree.beans.MessagePacket;
import com.gloree.beans.Project;
import com.gloree.beans.RTAttribute;
import com.gloree.beans.Report;
import com.gloree.beans.Requirement;
import com.gloree.beans.RequirementType;
import com.gloree.beans.SecurityProfile;
import com.gloree.beans.Trace;
import com.gloree.beans.TraceTreeRow;
import com.gloree.beans.User;

public class RESTAPIUtil {

	// checks if the key is 
	// a) a valid key
	// b) within the daily limit for access count
	// returns a true / false value
	 public static boolean validateKey(HttpServletRequest request, PrintWriter out, String key, String databaseType){
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			
			if (key == null) {
				// this is an error condition.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "A valid key is required with an API call." +
						" Please work with  your TraceCloud administrator to get a valid key");
				out.print(json.toString(3));
				return false;
			}
			
			 
			String sql = "";
		
			if (databaseType.equals("mySQL")){
				sql = "select api_key, api_calls_allowed_daily, " +
				" date_format(now(), '%m %d %y') \"today\", " +
				" date_format(api_call_dt, '%m %d %y') \"api_call_dt\"  , api_call_count " +
				" from gr_users" +
				" where api_key = ?" +
				" and user_type = 'readWrite' ";
			}
			else {
				sql = "select api_key, api_calls_allowed_daily, " +
				" to_char(sysdate, 'DD MON YYYY') \"today\", " +
				" to_char(api_call_dt, 'DD MON YYYY') \"api_call_dt\"  , api_call_count " +
				" from gr_users" +
				" where api_key = ?" +
				" and user_type = 'readWrite' ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, key);
			rs = prepStmt.executeQuery();
			
			String aPIKey = "";
			int aPICallsAllowedDaily = 0;
			String today = "";
			String lastAPICallDt = "";
			int aPICallCount = 0;
			
			if (rs.next()){
				aPIKey = rs.getString("api_key");
				aPICallsAllowedDaily = rs.getInt("api_calls_allowed_daily");
				today = rs.getString("today");
				lastAPICallDt = rs.getString("api_call_dt");
				aPICallCount = rs.getInt("api_call_count");
			}
			
			if ((aPIKey == null ) || (aPIKey.equals(""))){
				// means that the key sent in did not match something in the db.
				// this is an error condition.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "A valid key is required with an API call." +
						" Please ensure that your account is active");
				out.print(json.toString(3));
				return false;				
				
			}
			
			
			if (
					(lastAPICallDt != null) &&
					(lastAPICallDt.equals(today)) &&
					(aPICallCount > aPICallsAllowedDaily)
				){
				// means that the user has made more calls than allowed TODAY. Lets throttle them.
				// this is an error condition.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "You have exceeded your daily API call limit of " + aPICallsAllowedDaily  );
				out.print(json.toString(3));
				return false;				
			}
			rs.close();
			prepStmt.close();
			
			
			// this means that we can proceed to making the api call
			// prior to making the call, lets update the apiCallCount
			
			if (databaseType.equals("mySQL")){
				sql = "update gr_users " +
				" set api_call_dt = now(), " +
				" api_call_count = ? " +
				" where api_key = ? ";
			}
			else {
				sql = "update gr_users " +
				" set api_call_dt = sysdate, " +
				" api_call_count = ? " +
				" where api_key = ? ";
			}
				
			prepStmt = con.prepareStatement(sql);
			if (
					(lastAPICallDt == null) ||
					(!(lastAPICallDt.equals(today)))
				){
				// this means that lastAPICall date field is empty or its not empty,but its not equal to today's date
				// in either case this is the first call today. so the call count should be set to 1.
				prepStmt.setInt(1, 1);	
			}
			else {
				// not the first call of the day. so lets update the count by 1.
				prepStmt.setInt(1, ++aPICallCount);
			}
			prepStmt.setString(2, key);
			prepStmt.execute();
			
			con.close();
		} catch (Exception e) {
				
				e.printStackTrace();

				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));
		}   finally {
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

		return true;
	}
	 
	 public static boolean validateKeyNOPRINTOUT(HttpServletRequest request,  String key, String databaseType){
			
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			
			if (key == null) {
				return false;
			}
			
			 
			String sql = "";
		
			if (databaseType.equals("mySQL")){
				sql = "select api_key, api_calls_allowed_daily, " +
				" date_format(now(), '%m %d %y') \"today\", " +
				" date_format(api_call_dt, '%m %d %y') \"api_call_dt\"  , api_call_count " +
				" from gr_users" +
				" where api_key = ?" +
				" and user_type = 'readWrite' ";
			}
			else {
				sql = "select api_key, api_calls_allowed_daily, " +
				" to_char(sysdate, 'DD MON YYYY') \"today\", " +
				" to_char(api_call_dt, 'DD MON YYYY') \"api_call_dt\"  , api_call_count " +
				" from gr_users" +
				" where api_key = ?" +
				" and user_type = 'readWrite' ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, key);
			rs = prepStmt.executeQuery();
			
			String aPIKey = "";
			int aPICallsAllowedDaily = 0;
			String today = "";
			String lastAPICallDt = "";
			int aPICallCount = 0;
			
			if (rs.next()){
				aPIKey = rs.getString("api_key");
				aPICallsAllowedDaily = rs.getInt("api_calls_allowed_daily");
				today = rs.getString("today");
				lastAPICallDt = rs.getString("api_call_dt");
				aPICallCount = rs.getInt("api_call_count");
			}
			
			if ((aPIKey == null ) || (aPIKey.equals(""))){
				// means that the key sent in did not match something in the db.
				// this is an error condition.
				return false;				
				
			}
			
			
			if (
					(lastAPICallDt != null) &&
					(lastAPICallDt.equals(today)) &&
					(aPICallCount > aPICallsAllowedDaily)
				){
				// means that the user has made more calls than allowed TODAY. Lets throttle them.
				// this is an error condition.
				return false;				
			}
			rs.close();
			prepStmt.close();
			
			
			// this means that we can proceed to making the api call
			// prior to making the call, lets update the apiCallCount
			
			if (databaseType.equals("mySQL")){
				sql = "update gr_users " +
				" set api_call_dt = now(), " +
				" api_call_count = ? " +
				" where api_key = ? ";
			}
			else {
				sql = "update gr_users " +
				" set api_call_dt = sysdate, " +
				" api_call_count = ? " +
				" where api_key = ? ";
			}
				
			prepStmt = con.prepareStatement(sql);
			if (
					(lastAPICallDt == null) ||
					(!(lastAPICallDt.equals(today)))
				){
				// this means that lastAPICall date field is empty or its not empty,but its not equal to today's date
				// in either case this is the first call today. so the call count should be set to 1.
				prepStmt.setInt(1, 1);	
			}
			else {
				// not the first call of the day. so lets update the count by 1.
				prepStmt.setInt(1, ++aPICallCount);
			}
			prepStmt.setString(2, key);
			prepStmt.execute();
			
			con.close();
		} catch (Exception e) {
				
				e.printStackTrace();
		}   finally {
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

		return true;
	}
	 

	 // returns the securityprofile object for the user whose key was used for this api call.
	 public static SecurityProfile getSecurityProfile(String key, String databaseType){
		 SecurityProfile securityProfile = null;
		 	PreparedStatement prepStmt = null;
			ResultSet rs = null;
			java.sql.Connection con = null;
			try {
				
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();
				
				
				String sql = "select id " +
					" from gr_users" +
					" where api_key = ?" +
					" and user_type = 'readWrite' ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, key);
				rs = prepStmt.executeQuery();
				
				int userId = 0;
				if (rs.next()){
					userId = rs.getInt("id");
				}
				
				securityProfile = new SecurityProfile(userId,databaseType);
				rs.close();
				prepStmt.close();
				con.close();
				
			} catch (Exception e) {	
					e.printStackTrace();
			}   finally {
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
			return (securityProfile);
		}
	 

	 public static void signIn(HttpServletRequest request, String databaseType, PrintWriter out){
		 	
		 	
			try {
			 	String inputEmail = request.getParameter("inputEmail");
			 	String inputPassword = request.getParameter("inputPassword");
			 	
			 	System.out.println("srt inputEmail is "  + inputEmail);
			 	System.out.println("srt inputPassword is "  + inputPassword);
			 	
			 	
			 	String accessKey = UserAccountUtil.getAccessKey(inputEmail,inputPassword, request, databaseType);
				

				System.out.println("srt1  after getAccessKey . key is " + accessKey);
			 	
				if (accessKey.equals("")){
					JSONObject json = new JSONObject();
					json.put("responseStatus", "error");
					json.put("errorMessage", "Invalid email id or password");
					out.print(json.toString(3));
				}
				else {
					JSONObject json = new JSONObject();
					json.put("responseStatus", "success");
					json.put("errorMessage", ""  );
					json.put("accessKey", accessKey);
					out.print(json.toString(3));
				}
				} catch (Exception e) {
						e.printStackTrace();
						JSONObject json = new JSONObject();
						json.put("responseStatus", "error");
						json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
						out.print(json.toString(3));					
				}   finally {
				}
		 	
	 }
		 
		 
	 ///////////////////////////////////////////////////////////////////////////////////
	 // Project API Calls
	 ///////////////////////////////////////////////////////////////////////////////////	 
	

	static String extractPostRequestBody(HttpServletRequest request) {
	    if ("POST".equalsIgnoreCase(request.getMethod())) {
	        Scanner s = null;
	        try {
	            s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return s.hasNext() ? s.next() : "";
	    }
	    return "";
	}


	 public static void genericEmailRequirementsReport(
			 ServletContext servletContext, HttpServletRequest request, HttpServletResponse response , PrintWriter out){
			try {
				
				
				JSONObject json = new JSONObject();
				

				String data = extractPostRequestBody(request);
			    
			    System.out.println("srt data sent in is " + data);
			    
			    

				String requestor = request.getParameter("requestor");
				String toAddress = request.getParameter("toAddress");
				String title = request.getParameter("title");
				String payload = request.getParameter("payload");
				
			    System.out.println("srt payload sent in is " + payload);


				String projectPrefix = request.getParameter("projectPrefix");
				String projectName = request.getParameter("projectName");
				String projectDescription = request.getParameter("projectDescription");
				

				ArrayList<String> toArrayList = new ArrayList<String>();
				toArrayList.add(toAddress);
				ArrayList<String> ccArrayList = new ArrayList<String>();
				String subject = "Requirements Report from TraceNow";
				String message = "Hello <br>Here is your TraceNow report <br>Best Regards<br>TraceNow Support";
				
				String filepath = makeRequirementsReportForTraceNOW(requestor, title, payload, 
						projectPrefix, projectName, projectDescription, 
						request, response, servletContext, data );

			
				MessagePacket mP = new MessagePacket(toArrayList, ccArrayList, subject, message, filepath);
				
				String mailHost = servletContext.getInitParameter("mailHost");
				String transportProtocol = servletContext.getInitParameter("transportProtocol");
				String smtpAuth = servletContext.getInitParameter("smtpAuth");
				String smtpPort = servletContext.getInitParameter("smtpPort");
				String smtpSocketFactoryPort = servletContext.getInitParameter("smtpSocketFactoryPort");
				String emailUserId = servletContext.getInitParameter("emailUserId");
				String emailPassword = servletContext.getInitParameter("emailPassword");
				
				EmailUtil.emailWithAttachment(mP, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword );

			
				
				json.put("responseStatus", "success");
				json.put("errorMessage", ""  );
				json.put("successMessage", "Successfully handled genericEmailRequiremensReport"  );
				
				
				out.print(json.toString(3));
				
				

				
				// now lets remove the temp file.
				File file = new File(filepath);
				if (file != null){
					// lets drop the file.
					//file.delete();
				}
				
				

				} catch (Exception e) {
						e.printStackTrace();
						JSONObject json = new JSONObject();
						json.put("responseStatus", "error");
						json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
						out.print(json.toString(3));					
				}   finally {
				}
			}
		 
	
	 
	 // Based on an input fileCode, we will get the Excel file at this location
	 // and convert the first page into JSON and return it. 
	 public static void genericGetExcelFile(
			 ServletContext servletContext, HttpServletRequest request, HttpServletResponse response , PrintWriter out){
			try {
				
				
				JSONObject json = new JSONObject();
				


				String fileCode = request.getParameter("fileCode");
			
			    System.out.println("srt fileCode sent in is " + fileCode);
			    String filePath  = TNUtil.getTNFilePath(fileCode);
			    
				
				json.put("responseStatus", "success");
				json.put("errorMessage", ""  );
				json.put("successMessage", "Successfully recovered Excel File"  );
				
				json.put("fileCode", fileCode );
				json.put("filePath", filePath );

				
				// Now that we have the filePath,  lets open it and parse the first sheet.
				JSONObject data = TNUtil.getJSONFromExcel(filePath);
				json.put("data", data);
				out.print(json.toString(3));
				
				


				} catch (Exception e) {
						e.printStackTrace();
						JSONObject json = new JSONObject();
						json.put("responseStatus", "error");
						json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
						out.print(json.toString(3));					
				}   finally {
				}
			}
		 
	 	public static String getJSONFromExcel( String fileCode){
	 		
	 		String jsonString = "";
			try {
				
				
				JSONObject json = new JSONObject();
				


			
			    System.out.println("srt fileCode sent in is " + fileCode);
			    String filePath  = TNUtil.getTNFilePath(fileCode);
			    
				
				json.put("responseStatus", "success");
				json.put("errorMessage", ""  );
				json.put("successMessage", "Successfully recovered Excel File"  );
				
				json.put("fileCode", fileCode );
				json.put("filePath", filePath );

				
				// Now that we have the filePath,  lets open it and parse the first sheet.
				JSONObject data = TNUtil.getJSONFromExcel(filePath);
				json.put("data", data);
				jsonString = json.toString(3);
				
				


				} catch (Exception e) {
						e.printStackTrace();
						JSONObject json = new JSONObject();
						json.put("responseStatus", "error");
						json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
						jsonString = json.toString(3);	
						
				}   finally {
					
				}
				return(jsonString);
			}
	
	   private static String makeRequirementsReportForTraceNOW(String requestor, String title, String payload, 
			   String projectPrefix, String projectName, String projectDescription,
			   HttpServletRequest request,
	            HttpServletResponse response,
	            ServletContext servletContext, String data ) 
	    		throws ServletException, IOException {
	    	String filename = "";
	    	
			// Get the session. It should have the last View Report in memory.
	    	HttpSession session = request.getSession(true);
	    	
	        
	        try {


	    		// create a file name and set it to it.
	        	Calendar cal = Calendar.getInstance();
	    		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
	    		String today =  sdf.format(cal.getTime());
	    		filename = requestor  +" Requirements Report " + today + ".xls";
	    		filename.replace(' ', '_');
	    		
			
	    		
	    		
	    		HSSFWorkbook wb = new HSSFWorkbook();
	    		HSSFCreationHelper createHelper = (HSSFCreationHelper) wb.getCreationHelper(); 

	    		HSSFCellStyle headerStyle = wb.createCellStyle();
	    		headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
	    	    headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    	    
	    	    HSSFCellStyle headerStyle2 = wb.createCellStyle();
	    		headerStyle2.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
	    	    headerStyle2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    	    

	    	    // lets pick the cell styles for dangling / orphan / dangling and orphan
	    		HSSFCellStyle suspectStyle = wb.createCellStyle();
	    		suspectStyle.setFillForegroundColor(HSSFColor.RED.index);
	    	    suspectStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    	    
	    		HSSFCellStyle clearStyle = wb.createCellStyle();
	    		clearStyle.setFillForegroundColor(HSSFColor.WHITE.index);
	    	    clearStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    	    
	    	    
	    	    
	    	    HSSFCellStyle wrappedStyle = wb.createCellStyle();
	    	    wrappedStyle.setWrapText(true);
	    	    
	    	    
	    	    // lets build all the sheets in this file.
	    	    HSSFSheet infoSheet  = wb.createSheet("Report Info");
	    	    HSSFSheet reportSheet = wb.createSheet("Requirements Report");
		    	

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
	    		cellA.setCellValue(new HSSFRichTextString ("Requirements Report Date"));
	    		cellA.setCellStyle(headerStyle);
	    		row.createCell(3).setCellStyle(headerStyle);
	    		HSSFCell cellB = row.createCell(4);
	    		cellB.setCellValue(new HSSFRichTextString (today));
	    		
	    		row     = infoSheet.createRow((short)startRow++);
	    		cellA = row.createCell(2);
	    		cellA.setCellValue(new HSSFRichTextString ("Requirements Report Generated By "));
	    		cellA.setCellStyle(headerStyle);
	    		row.createCell(3).setCellStyle(headerStyle);
	    		cellB = row.createCell(4);
	    		cellB.setCellValue(new HSSFRichTextString (requestor));

	    		startRow += 2;
	    		
	    	
	    		// if this is a saved report, then lets print the report info.
	    		// project Info.
	    		startRow += 4;
	    		row     = infoSheet.createRow((short)startRow++);
	    		cellA = row.createCell(2);
	    		cellA.setCellValue(new HSSFRichTextString ("Project Prefix"));
	    		cellA.setCellStyle(headerStyle);
	    		row.createCell(3).setCellStyle(headerStyle);
	    		cellB = row.createCell(4);
	    		cellB.setCellValue(new HSSFRichTextString (projectPrefix));

	    		row     = infoSheet.createRow((short)startRow++);
	    		cellA = row.createCell(2);
	    		cellA.setCellValue(new HSSFRichTextString ("Project Name"));
	    		cellA.setCellStyle(headerStyle);
	    		row.createCell(3).setCellStyle(headerStyle);
	    		cellB = row.createCell(4);
	    		cellB.setCellValue(new HSSFRichTextString (projectName));

	    		row     = infoSheet.createRow((short)startRow++);
	    		cellA = row.createCell(2);
	    		cellA.setCellValue(new HSSFRichTextString ("Project Description"));
	    		cellA.setCellStyle(headerStyle);
	    		row.createCell(3).setCellStyle(headerStyle);
	    		cellB = row.createCell(4);
	    		cellB.setCellValue(new HSSFRichTextString (projectDescription));
	    		
	    			
	    		
	    		
	    		////////////////////////////////////////srt traceNow rest begin /////

				//String data = extractPostRequestBody(request);
			    
			    System.out.println("srt data sent in is " + data);
			    
			    JSONObject dataObject =new JSONObject(data);
			    
			    int rowNum = 0;
			    int cellNum = 0;
			    try {
			        JSONArray reqs  = dataObject.getJSONArray("reqs");
			        
			        
			        
			        
			        // get a HashMap of all use defined column names.
			        // you get them by looking at all the reqs sent in and looking at each each userDefinedattribute columns
			        HashSet<String> uDASet = new HashSet<String>();
			        for (int i = 0; i < reqs.length(); i++) {
			        	
			            JSONObject req = reqs.getJSONObject(i);
		        		
			            try{
			        		String uDAString = req.getString("user_defined_attributes");
			        		if (uDAString == null) { uDAString = "";}
			        		
			        		JSONObject uDAObject = new JSONObject(uDAString);
			        		
			        		// lets loop through all the kesy of uDAObject and put them in uDASet
			        		Iterator<String> keys = uDAObject.keys();
	
			        		while( keys.hasNext() ) {
			        		    String key = keys.next();
			        		    uDASet.add(key);
			        		}
			            }
			            catch (Exception e){
			            	//e.printStackTrace();
			            }
			        	
			        }
			        
			        for (String uDAName : uDASet){
			        	System.out.println("SRT attribute sent in is   " + uDAName);
	        		}
			        
			        JSONArray columnNames  = dataObject.getJSONArray("columnNames");
			        
			        
			        HSSFCell cell ;
	        		
			        
			        
			        
			        
			        
			        
			        
			        
			        
			        
			        
	        		//lets print the column headers.
			        row     = reportSheet.createRow((short) rowNum++);
			        // lets print folder path, full tag, name, details . Then we print user defined attributes. Then 
			        // we print the rest of the columns.
			        cell = row.createCell(cellNum++);
	        		cell.setCellValue(new HSSFRichTextString ("  folder path  "));
	        		cell.setCellStyle(headerStyle2);
	        		reportSheet.autoSizeColumn(cellNum);
	        		
	        		cell = row.createCell(cellNum++);
	        		cell.setCellValue(new HSSFRichTextString ("  full tag  "));
	        		cell.setCellStyle(headerStyle2);
	        		reportSheet.autoSizeColumn(cellNum);
	        		
	        		cell = row.createCell(cellNum++);
	        		cell.setCellValue(new HSSFRichTextString ("  name  "));
	        		cell.setCellStyle(headerStyle2);
	        		reportSheet.autoSizeColumn(cellNum);
	        		
	        		cell = row.createCell(cellNum++);
	        		cell.setCellValue(new HSSFRichTextString ("  details  "));
	        		cell.setCellStyle(headerStyle2);
	        		reportSheet.autoSizeColumn(cellNum);
	        		
	        		 // lets print the uDA headers.
	        		for (String uDAName : uDASet){
	        			cell = row.createCell(cellNum++);
		        		cell.setCellValue(new HSSFRichTextString ("   " + uDAName  + "  "));
		        		cell.setCellStyle(headerStyle2);
		        		reportSheet.autoSizeColumn(cellNum);
	        		}
	        		
	        		
	        		// print the rest of the headers.
			        for (int j = 0; j < columnNames.length(); j++) {

			        	if (
			        			(columnNames.getString(j).trim().equals("folder path"))
			        			||
			        			(columnNames.getString(j).trim().equals("full tag"))
			        			||
			        			(columnNames.getString(j).trim().equals("name"))
			        			||
			        			(columnNames.getString(j).trim().equals("details"))
			        			||
			        			(columnNames.getString(j).trim().equals("user_defined_attributes"))
			        		){
			        		continue;
			        	}
			        	
		        		cell = row.createCell(cellNum++);
		        		cell.setCellValue(new HSSFRichTextString ("   " +columnNames.getString(j) + "  "));
		        		cell.setCellStyle(headerStyle2);
		        		reportSheet.autoSizeColumn(cellNum);
		        	
			        }
			       
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		// lets print the data rows 
			        for (int i = 0; i < reqs.length(); i++) {
			            JSONObject req = reqs.getJSONObject(i);
		        		row     = reportSheet.createRow((short) rowNum++);	
		        		
					    cellNum = 0;
					   
					    // lets print the first 4 standard cells
					    String columnValue = req.getString("folder path");
		        		cell = row.createCell(cellNum++);
		        		cell.setCellValue(new HSSFRichTextString ("   " + columnValue + "  "));
		        		cell.setCellStyle(clearStyle);
		        		reportSheet.autoSizeColumn(cellNum);
		        		
		        		columnValue = req.getString("full tag");
		        		cell = row.createCell(cellNum++);
		        		cell.setCellValue(new HSSFRichTextString ("   " + columnValue + "  "));
		        		cell.setCellStyle(clearStyle);
		        		reportSheet.autoSizeColumn(cellNum);
			    		
		        		columnValue = req.getString("name");
		        		cell = row.createCell(cellNum++);
		        		cell.setCellValue(new HSSFRichTextString ("   " + columnValue + "  "));
		        		cell.setCellStyle(clearStyle);
		        		reportSheet.autoSizeColumn(cellNum);
		        		
		        		columnValue = req.getString("details");
		        		cell = row.createCell(cellNum++);
		        		cell.setCellValue(new HSSFRichTextString ("   " + columnValue + "  "));
		        		cell.setCellStyle(clearStyle);
		        		reportSheet.autoSizeColumn(cellNum);
		        		
		        		// lets print the user defined attributes.
		        		try{
						       String uDAString = req.getString("user_defined_attributes");
						       if (uDAString == null){uDAString = "{}";}
						        JSONObject uDA = new JSONObject(uDAString);
						      
				        		for (String uDAName : uDASet){
				        			cell = row.createCell(cellNum++);
				        			
				        			String uDAValue = "";
				        			try{
				        				uDAValue = uDA.getString(uDAName);
				        			}
				        			catch(Exception e2){
				        				// do nothing
				        			}
				        			if (uDAValue == null){
				        				uDAValue = "";
				        			}
				        			
					        		cell.setCellValue(new HSSFRichTextString ("   " + uDAValue  + "  "));
					        		cell.setCellStyle(clearStyle);
					        		reportSheet.autoSizeColumn(cellNum);
				        		}
					        }
					        catch (Exception e){
					        	e.printStackTrace();
					     }
		        		
		        		// lets print the rest of the columns
				        for (int j = 0; j < columnNames.length(); j++) {
				        	
				        	if (
				        			(columnNames.getString(j).trim().equals("folder path"))
				        			||
				        			(columnNames.getString(j).trim().equals("full tag"))
				        			||
				        			(columnNames.getString(j).trim().equals("name"))
				        			||
				        			(columnNames.getString(j).trim().equals("details"))
				        			||
				        			(columnNames.getString(j).trim().equals("user_defined_attributes"))
				        		){
				        		continue;
				        	}
			        		
				        	columnValue = req.getString(columnNames.getString(j));
			        		cell = row.createCell(cellNum++);
			        		cell.setCellValue(new HSSFRichTextString ("   " + columnValue + "  "));
			        		cell.setCellStyle(clearStyle);
			        		reportSheet.autoSizeColumn(cellNum);
				        }
				        
				        
			        }

			    } catch (Exception ex) {
			        ex.printStackTrace();
			    }
			    	
			    //	////////////////////////////////////srt traceNow rest end /////
			    
	    		

		    	
				String rootDataDirectory = servletContext.getInitParameter("rootDataDirectory");
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


	 ///////////////////////////////////////////////////////////////////////////////////
	 // Project API Calls
	 ///////////////////////////////////////////////////////////////////////////////////	 
	 	 
	 
	 // returns a void
	 // however prints a JSON object of all the projects that this user has access to.
	 public static void getMyProjects(HttpServletRequest request, PrintWriter out, String key, 
		 SecurityProfile securityProfile){
		try {
			
			ArrayList projects = securityProfile.getProjectObjects();
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			
			
			User user = securityProfile.getUser();
			String prefHideProjects = user.getPrefHideProjects();
			
			
			// lets iterate through all the projects and create an array of Projects
			JSONArray jsonProjects = new JSONArray();
			Iterator p = projects.iterator();
			while (p.hasNext()) {
				Project project = (Project) p.next();
				
			
					
				
				JSONObject jsonProject = new JSONObject();
				jsonProject.put("projectId", project.getProjectId());
				jsonProject.put("prefix", project.getShortName());
				jsonProject.put("name", project.getProjectName());
				jsonProject.put("description", project.getProjectDescription());
				jsonProject.put("restrictedDomains", project.getRestrictedDomains());
				jsonProject.put("createdBy", project.getCreatedBy());
				jsonProject.put("lastModifiedBy", project.getLastModifiedBy());
				if(prefHideProjects.contains( project.getProjectId() + ":#:" + project.getShortName())){
					jsonProject.put("hideProject", "true");
				}
				else{
					jsonProject.put("hideProject", "false");
				}
				jsonProjects.put(jsonProject);
			}
			json.put("projects", jsonProjects);
			
			out.print(json.toString(3));

			} catch (Exception e) {
					e.printStackTrace();
					JSONObject json = new JSONObject();
					json.put("responseStatus", "error");
					json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
					out.print(json.toString(3));					
			}   finally {
			}
		}
	 
	 
	 
	 
	 // returns a void
	 // however prints a JSON object the project details of this projectId
	 public static void getProjectDetails(HttpServletRequest request, PrintWriter out, String key, 
		SecurityProfile securityProfile, String databaseType){
		try {
			String projectIdString = request.getParameter("projectId");
			if (projectIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "projectId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int projectId = Integer.parseInt(projectIdString);
			if (!(securityProfile.getRoles().contains("MemberInProject" + projectId))){
				// the user does not have access to this project. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "you do not have access to this project");
				out.print(json.toString(3));
				return;
			}
			
			Project project = new Project(projectId,  databaseType);
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			
			JSONObject jsonProject = new JSONObject();
			jsonProject.put("projectId", project.getProjectId());
			jsonProject.put("prefix", project.getShortName());
			jsonProject.put("name", project.getProjectName());
			jsonProject.put("description", project.getProjectDescription());
			jsonProject.put("restrictedDomains", project.getRestrictedDomains());
			jsonProject.put("createdBy", project.getCreatedBy());
			jsonProject.put("lastModifiedBy", project.getLastModifiedBy());
			json.put("projectDetails", jsonProject);
				
			out.print(json.toString(3));
		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}
	 
	 // returns a void
	 // however prints a JSON object that lists an array of Requirement Types in a project.
	 public static void getProjectRequirementTypes(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String projectIdString = request.getParameter("projectId");
			if (projectIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "projectId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int projectId = Integer.parseInt(projectIdString);
			if (!(securityProfile.getRoles().contains("MemberInProject" + projectId))){
				// the user does not have access to this project. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "you do not have access to this project");
				out.print(json.toString(3));
				return;
			}
			Project project = new Project(projectId,  databaseType);
			ArrayList requirementTypes = project.getMyRequirementTypes();
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			
			// lets iterate through all the requirement Types  and create an array of requirement Types 
			JSONArray jsonRequirementTypes= new JSONArray();
			Iterator rt = requirementTypes.iterator();
			while (rt.hasNext()) {
				RequirementType  requirementType = (RequirementType) rt.next();
				JSONObject jsonRequirementType = fillRequirementTypeJSON(requirementType);
				jsonRequirementTypes.put(jsonRequirementType);
			}
			json.put("requirementTypes", jsonRequirementTypes);
			
			out.print(json.toString(3));

		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}


	 // returns a void
	 // however prints a JSON object that lists an array of Folders in a project.
	 public static void getProjectFolders(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String projectIdString = request.getParameter("projectId");
			if (projectIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "projectId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int projectId = Integer.parseInt(projectIdString);
			if (!(securityProfile.getRoles().contains("MemberInProject" + projectId))){
				// the user does not have access to this project. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "you do not have access to this project");
				out.print(json.toString(3));
				return;
			}
			
			ArrayList folders = ProjectUtil.getFolderInAProjectLiteWithLevel(projectId);
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			
			// lets iterate through all the projects and create an array of Projects
			JSONArray jsonFolders = new JSONArray();
			Iterator f = folders.iterator();
			while (f.hasNext()) {
				String folderLite = (String) f.next();
				String[] fLArray = folderLite.split(":##:");
				
				int folderLevel = Integer.parseInt(fLArray[0]);
				int folderId = Integer.parseInt(fLArray[1]);
				String folderName = fLArray[2];
				String folderPath = fLArray[3];
				
				JSONObject jsonFolder = new JSONObject();
					
				jsonFolder.put("folderId", folderId);
				jsonFolder.put("projectId", projectId);
				jsonFolder.put("name", folderName);
				jsonFolder.put("folderLevel", folderLevel);
				jsonFolder.put("folderPath", folderPath);
				

				jsonFolders.put(jsonFolder);
			}
			json.put("folders", jsonFolders);
			
			out.print(json.toString(3));

		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}
	 

	 // returns a void
	 // however prints a JSON object that lists an array of Users in a project.
	 public static void getProjectUsers(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String projectIdString = request.getParameter("projectId");
			if (projectIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "projectId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int projectId = Integer.parseInt(projectIdString);
			if (!(securityProfile.getRoles().contains("MemberInProject" + projectId))){
				// the user does not have access to this project. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "you do not have access to this project");
				out.print(json.toString(3));
				return;
			}
			
			Project project = new Project(projectId,  databaseType);
			ArrayList users = project.getMembers();
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			
			// lets iterate through all the projects and create an array of Projects
			JSONArray jsonUsers = new JSONArray();
			Iterator u = users.iterator();
			while (u.hasNext()) {
				User user = (User) u.next();
				JSONObject jsonUser = new JSONObject();
				
				jsonUser.put("userId", user.getUserId());
				jsonUser.put("firstName", user.getFirstName());
				jsonUser.put("lastName", user.getLastName());
				jsonUser.put("emailId", user.getEmailId());
				jsonUser.put("userType", user.getUserType());
							
				jsonUsers.put(jsonUser);
			}
			json.put("users", jsonUsers);
			
			out.print(json.toString(3));

		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}
	 
	 // returns a void
	 // however prints a JSON object that lists an array of Reports in a project.
	 public static void getProjectReports(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String projectIdString = request.getParameter("projectId");
			if (projectIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "projectId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int projectId = Integer.parseInt(projectIdString);
			if (!(securityProfile.getRoles().contains("MemberInProject" + projectId))){
				// the user does not have access to this project. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "you do not have access to this project");
				out.print(json.toString(3));
				return;
			}
			
			String filter = request.getParameter("filter");
			if (filter == null){
				filter = "all";
			}
			Project project = new Project(projectId,  databaseType);
			ArrayList reports = ProjectUtil.getUserReports(project.getProjectId(), securityProfile.getUser());
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			
			// lets iterate through all the projects and create an array of Projects
			JSONArray jsonReports = new JSONArray();
			Iterator r = reports.iterator();
			while (r.hasNext()) {
				Report report = (Report) r.next();
				if (filter.equals("onlyUserCreated")){
					if (report.getReportDescription().startsWith("Canned")){
						continue;
					}
				}
				JSONObject jsonReport = new JSONObject();
				
				jsonReport.put("reportId", report.getReportId());
				jsonReport.put("projectId", report.getProectId());
				jsonReport.put("folderId", report.getFolderId());
				jsonReport.put("reportName", report.getReportName());
				jsonReport.put("reportDescription", report.getReportDescription());
				jsonReport.put("reportDefinition", report.getReportDefinition());
				jsonReport.put("reportType", report.getReportType());
				jsonReport.put("reportVisibility", report.getReportVisibility());
				jsonReport.put("traceTreeDepth", report.getTraceTreeDepth());
				jsonReport.put("createdBy", report.getCreatedByEmailId());
							
				jsonReports.put(jsonReport);
			}
			json.put("reports", jsonReports);
			
			out.print(json.toString(3));

		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}


	 // returns a void
	 // however creates a Requierment type and prints a JSON object that lists 
	 // information about the newly created REquirement type
	 public static void createRequirementType(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String projectIdString = request.getParameter("projectId");
			if (projectIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "projectId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int projectId = Integer.parseInt(projectIdString);
			if (!(securityProfile.getRoles().contains("AdministratorInProject" + projectId))){
				// the user does not have access to this project. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "Only project administrators can create Requirement Types ");
				out.print(json.toString(3));
				return;
			}
			
			String rTApprovalWorkflowString = request.getParameter("rTApprovalWorkflow");
			if (rTApprovalWorkflowString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "rTEnableApproval is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int rTEnableApproval = 0;
			if (rTApprovalWorkflowString.equals("enable")){
				rTEnableApproval = 1;
			}
			
			String rTPrefix = request.getParameter("rTPrefix");
			if (rTPrefix == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "rTPrefix is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			if (rTPrefix.length() > 3){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "Please reduce your rTPrefix '" + rTPrefix + "' to 3 or fewer characters.");
				out.print(json.toString(3));
				return;
			}
			
			String rTName = request.getParameter("rTName");
			if (rTName == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "rTName is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			
			String rTDescription = request.getParameter("rTDescription");
			if (rTDescription == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "rTDescription is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			
			int status = ProjectUtil.isUniqueRequirementType(projectId, rTPrefix, rTName);			
			if (status == 0){
				// this means the prefix is not unique.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "A Requirement Type with the Prefix '" + rTPrefix + "' already exists in this project");
				out.print(json.toString(3));
				return;
			}
			
			int rTDisplaySequence = 0;
			try {
				rTDisplaySequence = Integer.parseInt(request.getParameter("rTDisplaySequence"));
			}
			catch (Exception e) {
				rTDisplaySequence = 0;
			}
			
			Project project = new Project(projectId,  databaseType);
			int rTCanBeDangling = 1;
			int rTCanBeOrphan = 1;
			int rTEnableAgileScrum = 0;
			String requirementTypeCanNotTraceTo = "";
			
			RequirementType requirementType = new RequirementType(projectId,project.getProjectName(), 
				rTPrefix, rTName, rTDescription, rTDisplaySequence, rTEnableApproval,rTEnableAgileScrum,
				rTCanBeDangling, rTCanBeOrphan, requirementTypeCanNotTraceTo,
				securityProfile.getUser().getEmailId(),  databaseType);
			
			JSONObject jsonRequirementType  = fillRequirementTypeJSON(requirementType);
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			json.put("requirementTypeDetails", jsonRequirementType);
			
			out.print(json.toString(3));
			
		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}
	 
	 
	 
	 
	 // returns a void
	 // however creates a Folder and prints a JSON object that lists 
	 // information about the newly created Folder
	 public static void createFolder(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String parentFolderIdString = request.getParameter("parentFolderId");
			if (parentFolderIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "parentFolderId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			
			int parentFolderId = Integer.parseInt(parentFolderIdString);
			Folder parentFolder = new Folder(parentFolderId);
			if (!(securityProfile.getRoles().contains("MemberInProject" + parentFolder.getProjectId()))){
				// the user does not have access to this project. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "You are not a member of this project");
				out.print(json.toString(3));
				return;
			}
			
			//lets make sure that the user had edit privs on the parent folder
			// the user needs to have edit / create reqs on this folder to be able to create new sub folders.
			if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" 
					+ parentFolder.getFolderId()))){
				// the user does not have edit privs on this folder. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "You do not have edit Privileges on this folder");
				out.print(json.toString(3));
				return;
			}
			
			String folderName = request.getParameter("folderName");
			if (folderName == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "folderName is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			
			String folderDescription = request.getParameter("folderDescription");
			if (folderDescription == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "folderDescription is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			
			// NOTE : YUI has issues in explorer if the name  has ' or ". so replacing
			// them with ^.
			// Same with folderDescription.
			folderName = folderName.replace('\'', '^');
			folderName = folderName.replace('"', '^');
			folderName = folderName.replace("::", "--");
			
			folderDescription = folderDescription.replace('\'', '^');
			folderDescription = folderDescription.replace('"', '^');
			folderDescription = folderDescription.replace("::", "--");
			folderDescription = folderDescription.replace('\n', ' ');
			folderDescription = folderDescription.replace('\r', ' ');
			
			// lets see if this folderName is available
			if (FolderUtil.subFolderExists(parentFolderId,folderName)) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "Another folder with this name '"+ folderName + "' already exists");
				out.print(json.toString(3));
				return;
			}
			
			Folder folder = new Folder( parentFolderId, parentFolder.getProjectId(), folderName, 
					folderDescription, 0, securityProfile.getUser().getEmailId(),  databaseType);
			
			boolean includeMetrics = true;
			
			JSONObject jsonFolder = fillFolderJSON(folder, includeMetrics);
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			json.put("FolderDetails", jsonFolder);
			out.print(json.toString(3));
			
		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}
	 
	 ///////////////////////////////////////////////////////////////////////////////////
	 // Requirement Type API Calls
	 ///////////////////////////////////////////////////////////////////////////////////	 

	 // returns a void
	 // however prints a JSON object that lists details of a Requirement Type 
	 public static void getRequirementTypeDetails(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile){
		try {
			String requirementTypeIdString = request.getParameter("requirementTypeId");
			if (requirementTypeIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "requirementTypeId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int requirementTypeId = Integer.parseInt(requirementTypeIdString);
			RequirementType requirementType = new RequirementType(requirementTypeId);
			if (!(securityProfile.getRoles().contains("MemberInProject" + requirementType.getProjectId()))){
				// the user does not have access to this project. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "you do not have access to this project");
				out.print(json.toString(3));
				return;
			}
			
			JSONObject jsonRequirementType  = fillRequirementTypeJSON(requirementType);
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			json.put("requirementTypeDetails", jsonRequirementType);
			out.print(json.toString(3));

			
		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}

	 // returns a void
	 // however prints a JSON object that lists all the Requirements in a a Requirement Type 
	 public static void getRequirementsInRequirementType(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String requirementTypeIdString = request.getParameter("requirementTypeId");
			if (requirementTypeIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "requirementTypeId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int requirementTypeId = Integer.parseInt(requirementTypeIdString);
			RequirementType requirementType = new RequirementType(requirementTypeId);
			if (!(securityProfile.getRoles().contains("MemberInProject" + requirementType.getProjectId()))){
				// the user does not have access to this project. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "you do not have access to this project");
				out.print(json.toString(3));
				return;
			}
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );

			JSONObject jsonRequirementType = new JSONObject();
			jsonRequirementType.put("requirementTypeId", requirementType.getRequirementTypeId());
			jsonRequirementType.put("projectId", requirementType.getProjectId());
			jsonRequirementType.put("prefix", requirementType.getRequirementTypeShortName());
			jsonRequirementType.put("name", requirementType.getRequirementTypeName());
			jsonRequirementType.put("description", requirementType.getRequirementTypeDescription());
			jsonRequirementType.put("enableApproval", requirementType.getRequirementTypeEnableApproval());
			jsonRequirementType.put("createdBy", requirementType.getCreatedBy());
			jsonRequirementType.put("lastModifiedBy", requirementType.getLastModifiedBy());
			
			
			// lets get all the requirements in this requirementtype .
			ArrayList requirements = requirementType.getAllRequirementsInRequirementType(databaseType);
			// lets iterate through all the requiremetns and create an array of requirements
			JSONArray jsonRequirements = new JSONArray();
			Iterator r = requirements.iterator();
			while (r.hasNext()) {
				Requirement requirement = (Requirement) r.next();
			 	// if the user does not have read permissions on the folder where this req resides, lets
				// redact it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}

				JSONObject jsonRequirement = fillRequirementJSON(requirement);
				jsonRequirements.put(jsonRequirement);
			}
			jsonRequirementType.put("requirements", jsonRequirements);
			json.put("requirementTypeDetails", jsonRequirementType);
			
			out.print(json.toString(3));

		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}	 

	 // returns a void
	 // however creates a Requierment type and prints a JSON object that lists 
	 // information about the newly created REquirement type
	 public static void createAttribute(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile){
		try {
			String requirementTypeIdString = request.getParameter("requirementTypeId");
			if (requirementTypeIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "requirementTypeId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int requirementTypeId = Integer.parseInt(requirementTypeIdString);
			RequirementType requirementType = new RequirementType(requirementTypeId);
			
			if (!(securityProfile.getRoles().contains("AdministratorInProject" + requirementType.getProjectId()))){
				// the user does not have access to this project. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "Only project administrators can create new attributes in Requirement Types ");
				out.print(json.toString(3));
				return;
			}
			
			String aName = request.getParameter("aName");
			if (aName == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "aName is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			
			String aType = request.getParameter("aType");
			if (aType == null) {
				aType = "Text Box";
			}
			
			if (!(
				(aType.equals("Text Box")) ||
				(aType.equals("Drop Down")) ||
				(aType.equals("URL")) ||
				(aType.equals("Date"))
				)) {
				// attribute type has to be one of the 4 types.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "aType has to be one of 'Text Box', 'Drop Down', 'URL' or 'Date' " );
				out.print(json.toString(3));
				return;
			}
				
			String aSortOrder = request.getParameter("aSortOrder");
			if (aSortOrder == null) {
				aSortOrder= "a";
			}
			
			String aRequiredString = request.getParameter("aRequired");
			int aRequired = 0;
			if ((aRequiredString != null) && (aRequiredString.equals("1"))) {
				aRequired = 1;
			}
			
			String aDefaultValue = request.getParameter("aDefaultValue");
			if (aDefaultValue == null) {
				aDefaultValue = "";
			}
			
			String aDropDownOptions = request.getParameter("aDropDownOptions");
			if ((aType.equals("Drop Down") && (aDropDownOptions == null))) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "aDropDownOptions is a required parameter if aType equals 'Drop Down'");
				out.print(json.toString(3));
				return;
			}
			if (aDropDownOptions == null) {
				aDropDownOptions = "";
			}
			
			
			
			String aImpactsVersionString = request.getParameter("aImpactsVersion");
			int aImpactsVersion = 0;
			if ((aImpactsVersionString != null) && (aImpactsVersionString.equals("1"))) {
				aImpactsVersion = 1;
			}
			
			String aImpactsTraceabilityString = request.getParameter("aImpactsTraceability");
			int aImpactsTraceability = 0;
			if ((aImpactsTraceabilityString != null) && (aImpactsTraceabilityString.equals("1"))) {
				aImpactsTraceability = 1;
			}
			
			String aImpactsApprovalWorkflowString = request.getParameter("aImpactsApprovalWorkflow");
			int aImpactsApprovalWorkflow = 0;
			if ((aImpactsApprovalWorkflowString != null) && (aImpactsApprovalWorkflowString.equals("1"))) {
				aImpactsApprovalWorkflow = 1;
			}
			
			String aDescription = request.getParameter("aDescription");
			if (aDescription == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "aDescription is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			
			int status = ProjectUtil.isUniqueAttribute(requirementTypeId, aName);
			
			if (status == 0){
				// this means the attributeName is not unique within this requirementtype id
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An attribute with the name '" + aName + "' already exists in Requirement Type '" + 
						requirementType.getRequirementTypeName() + "'");
				out.print(json.toString(3));
				return;
			}
			
			
			
			int parentAttributeId = 0;
			RTAttribute rTAttribute = new RTAttribute(requirementType.getProjectId(),parentAttributeId, 0, requirementTypeId, aName , 
				aType , aSortOrder,	aRequired, aDefaultValue, 
				aDropDownOptions, aDescription,
				aImpactsVersion, aImpactsTraceability, aImpactsApprovalWorkflow,
				securityProfile.getUser().getEmailId(), "mySQL");
			
			// since the req type has changed i.e. new attribute got added, lets refresh the bean and print out the JSON
			requirementType = new RequirementType(requirementType.getRequirementTypeId());
			JSONObject jsonRequirementType  = fillRequirementTypeJSON(requirementType);
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			json.put("requirementTypeDetails", jsonRequirementType);
			
			out.print(json.toString(3));
			
		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}
	 
	 ///////////////////////////////////////////////////////////////////////////////////
	 // Folder API Calls
	 ///////////////////////////////////////////////////////////////////////////////////	 

	// returns a void
	 // however prints a JSON object that lists details of a Folder 
	 public static void getFolderDetails(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile){
		try {
			String folderIdString = request.getParameter("folderId");
			if (folderIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "folderId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int folderId = Integer.parseInt(folderIdString);
			Folder folder = new Folder(folderId);
			if (!(securityProfile.getRoles().contains("MemberInProject" + folder.getProjectId()))){
				// the user does not have access to this project. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "you do not have access to this project");
				out.print(json.toString(3));
				return;
			}
			
			boolean includeMetrics = true;
			JSONObject jsonFolder = fillFolderJSON(folder, includeMetrics);
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			json.put("FolderDetails", jsonFolder);
			out.print(json.toString(3));
			
			

		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}

	 
	 // returns a void
	 // however prints a JSON object that lists all the Requirements in a a Requirement Type 
	 public static void getRequirementsInFolder(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String folderIdString = request.getParameter("folderId");
			if (folderIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "folderId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int folderId = Integer.parseInt(folderIdString);
			Folder folder = new Folder(folderId);
			if (!(securityProfile.getRoles().contains("MemberInProject" + folder.getProjectId()))){
				// the user does not have access to this project. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "you do not have access to this project");
				out.print(json.toString(3));
				return;
			}
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );

			JSONObject jsonFolder = new JSONObject();
			
			jsonFolder.put("folderId", folder.getFolderId());
			jsonFolder.put("projectId", folder.getProjectId());
			jsonFolder.put("name", folder.getFolderName());
			jsonFolder.put("description", folder.getFolderDescription());
			jsonFolder.put("parentFolderId", folder.getParentFolderId());
			jsonFolder.put("folderLevel", folder.getFolderLevel());
			jsonFolder.put("folderOrder", folder.getFolderOrder());
			jsonFolder.put("folderPath", folder.getFolderPath());
			jsonFolder.put("requirementTypeId", folder.getRequirementTypeId());
			jsonFolder.put("requirementTypeName", folder.getRequirementTypeName());
			jsonFolder.put("createdBy", folder.getCreatedBy());
			jsonFolder.put("lastModifiedBy", folder.getLastModifiedBy());
			
			
			// lets get all the requirements in this requirementtype .
			ArrayList requirements = folder.getMyRequirements(folder.getProjectId(), databaseType);
			
			
			// lets apply the filter criteria. 
			
			String filter = request.getParameter("filter");
			if (filter == null ){
				filter = "all";
			}
			
			// lets iterate through all the requiremetns and create an array of requirements
			JSONArray jsonRequirements = new JSONArray();
			Iterator r = requirements.iterator();
			while (r.hasNext()) {
				Requirement requirement = (Requirement) r.next();
			 	// if the user does not have read permissions on the folder where this req resides, lets
				// redact it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}

				if (filter.equals("dangling")){
					if (!(folder.canBeReportedDangling() && (requirement.getRequirementTraceFrom().equals("")))){
						// the user asked for dangling requirement and this is not a dangling requriement. So skip this object.
						continue;
					}
				}
				
				if (filter.equals("orphan")){
					if (!(folder.canBeReportedOrphan() && (requirement.getRequirementTraceTo().equals("")))){
						// the user asked for dangling requirement and this is not a dangling requriement. So skip this object.
						continue;
					}
				}
				
				if (filter.equals("suspectUp")){
					if (!((requirement.getRequirementTraceTo().contains("(s)")))){
						// the user asked for dangling requirement and this is not a dangling requriement. So skip this object.
						continue;
					}
				}
				
				if (filter.equals("suspectDown")){
					if (!((requirement.getRequirementTraceFrom().contains("(s)")))){
						// the user asked for dangling requirement and this is not a dangling requriement. So skip this object.
						continue;
					}
				}
				
				if (filter.equals("completed")){
					int percentCompleted = requirement.getRequirementPctComplete();
					if (!(percentCompleted  == 100)){
						// the user asked for dangling requirement and this is not a dangling requriement. So skip this object.
						continue;
					}
				}
				
				if (filter.equals("incomplete")){
					int percentCompleted = requirement.getRequirementPctComplete();
					if (!(percentCompleted  != 100)){
						// the user asked for dangling requirement and this is not a dangling requriement. So skip this object.
						continue;
					}
				}
				
				if (filter.equals("draft")){
					if (!((requirement.getApprovalStatus().equals("Draft")))){
							// the user asked for dangling requirement and this is not a dangling requriement. So skip this object.
						continue;
					}
				}
				
				if (filter.equals("pending")){
					if (!((requirement.getApprovalStatus().equals("In Approval WorkFlow")))){
							// the user asked for dangling requirement and this is not a dangling requriement. So skip this object.
						continue;
					}
				}
				
				if (filter.equals("approved")){
					if (!((requirement.getApprovalStatus().equals("Approved")))){
							// the user asked for dangling requirement and this is not a dangling requriement. So skip this object.
						continue;
					}
				}
				
				if (filter.equals("rejected")){
					if (!((requirement.getApprovalStatus().equals("Rejected")))){
							// the user asked for dangling requirement and this is not a dangling requriement. So skip this object.
						continue;
					}
				}
	
				
				if (filter.equals("testPending")){
					if (!((requirement.getTestingStatus().equals("Pending") ))){
							// the user asked for dangling requirement and this is not a dangling requriement. So skip this object.
						continue;
					}
				}
				
				if (filter.equals("testPass")){
					if (!((requirement.getTestingStatus().equals("Pass") ))){
							// the user asked for dangling requirement and this is not a dangling requriement. So skip this object.
						continue;
					}
				}
				
				if (filter.equals("testFail")){
					if (!((requirement.getTestingStatus().equals("Fail") ))){
							// the user asked for dangling requirement and this is not a dangling requriement. So skip this object.
						continue;
					}
				}
				
				
				JSONObject jsonRequirement = fillRequirementJSON(requirement);
				jsonRequirements.put(jsonRequirement);
			}
			jsonFolder.put("requirements", jsonRequirements);
			json.put("folderDetails", jsonFolder);
			
			out.print(json.toString(3));

		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}	 

	 ///////////////////////////////////////////////////////////////////////////////////
	 // User API Calls
	 ///////////////////////////////////////////////////////////////////////////////////	 

	 // returns a void
	 // however prints a JSON object that lists all the Requirements Owned by a user
	 public static void getUsersRequirements(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String userIdString = request.getParameter("userId");
			if (userIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "userId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int userId = Integer.parseInt(userIdString);
			User user = new User(userId,  databaseType);
			

			String projectIdString = request.getParameter("projectId");
			if (projectIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "projectId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int projectId = Integer.parseInt(projectIdString);
			Project project = new Project(projectId,  databaseType);
			if (!(securityProfile.getRoles().contains("MemberInProject" + project.getProjectId()))){
				// the project does not have access to this project. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "you do not have access to this project");
				out.print(json.toString(3));
				return;
			}

			
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );

			JSONObject jsonUser = new JSONObject();
			
			jsonUser.put("userId", user.getUserId());
			jsonUser.put("firstName", user.getFirstName());
			jsonUser.put("lastName", user.getLastName());
			jsonUser.put("emailId", user.getEmailId());
			jsonUser.put("userType", user.getUserType());
			
			
			
			// lets get all the requirements owned by the user in this project
			ArrayList requirements = project.getUserRequirements(user,  databaseType); 
				
			// lets iterate through all the requiremetns and create an array of requirements
			JSONArray jsonRequirements = new JSONArray();
			Iterator r = requirements.iterator();
			while (r.hasNext()) {
				Requirement requirement = (Requirement) r.next();
				JSONObject jsonRequirement = fillRequirementJSON(requirement);
				jsonRequirements.put(jsonRequirement);
			}
			jsonUser.put("requirements", jsonRequirements);
			json.put("folderDetails", jsonUser);
			
			out.print(json.toString(3));

		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {
		}
	}	 	 

	 
	 
	 ///////////////////////////////////////////////////////////////////////////////////
	 // Report API Calls
	 ///////////////////////////////////////////////////////////////////////////////////	 

	 // returns a void
	 // however prints a JSON object that lists all the Requirements Owned by a user
	 public static void getReportRequirements(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			
			User user = securityProfile.getUser();
			
			String reportIdString = request.getParameter("reportId");
			if (reportIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "reportId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int reportId = Integer.parseInt(reportIdString);
			Report report = new Report(reportId);
			Project project = new Project(report.getProectId(),  databaseType);
			if (!(securityProfile.getRoles().contains("MemberInProject" + project.getProjectId()))){
				// the project does not have access to this project. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "you do not have access to this project");
				out.print(json.toString(3));
				return;
			}

			if (!(
				(report.getReportVisibility().equals("public")) ||
				(report.getCreatedByEmailId().equals(user.getEmailId()))
			)){
				// the report is not public and is not owned by the calling user. Hence error 
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "you do not have access to this report");
				out.print(json.toString(3));
				return;
			}
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );

			JSONObject jsonReport= new JSONObject();
			
			jsonReport.put("reportId", report.getReportId());
			jsonReport.put("projectId", report.getProectId());
			jsonReport.put("folderId", report.getFolderId());
			jsonReport.put("reportName", report.getReportName());
			jsonReport.put("reportDescription", report.getReportDescription());
			jsonReport.put("reportDefinition", report.getReportDefinition());
			jsonReport.put("reportType", report.getReportType());
			jsonReport.put("reportVisibility", report.getReportVisibility());
			jsonReport.put("traceTreeDepth", report.getTraceTreeDepth());
			jsonReport.put("createdBy", report.getCreatedByEmailId());
			
			
			
			// lets get all the requirements owned by the user in this project
			ArrayList requirements = ReportUtil.runGenericReport(securityProfile, report, report.getProectId(),  databaseType) ;
				
			// lets iterate through all the requiremetns and create an array of requirements
			JSONArray jsonRequirements = new JSONArray();
			Iterator r = requirements.iterator();
			while (r.hasNext()) {
				Requirement requirement = (Requirement) r.next();
			 	// if the user does not have read permissions on the folder where this req resides, lets
				// redact it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}

				JSONObject jsonRequirement = fillRequirementJSON(requirement);
				jsonRequirements.put(jsonRequirement);
			}
			jsonReport.put("requirements", jsonRequirements);
			json.put("reportDetails", jsonReport);
			
			out.print(json.toString(3));

		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {
		}
	}	 	 
	 
	 ///////////////////////////////////////////////////////////////////////////////////
	 // Search String API Calls
	 ///////////////////////////////////////////////////////////////////////////////////	 


	 
	 // returns a void
	 // however prints a JSON object that lists all the Requirements in a a Requirement Type 
	 public static void getRequirementBySearchString(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String searchString = request.getParameter("searchString");
			if ((searchString == null) || (searchString.equals(""))) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "searchString is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			
			String searchProjects = request.getParameter("searchProjects");
			if ((searchProjects == null) || (searchProjects.equals(""))){
				// if the user did not send in a comma separated list of projects
				// then we get the list of projects the user is a member of.
				ArrayList projects = securityProfile.getProjectObjects();
				searchProjects = "";
				Iterator p = projects.iterator();
				while (p.hasNext()) {
					Project project = (Project) p.next();
					searchProjects += project.getProjectId() + ",";
				}	
			}
			
			

			
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );

			JSONObject jsonSearch = new JSONObject();
			
			jsonSearch.put("searchString", searchString);
			jsonSearch.put("searchProjects", searchProjects);
			
			int targetProjectId = 0;
			int targetRequirementTypeId = 0;
			int targetFolderId = 0;
			// lets get all the requirements in this requirementtype .
			ArrayList globalSearchReport = ReportUtil.getglobalSearchReport(securityProfile, 
					searchProjects,searchString, securityProfile.getUser(),  databaseType,targetProjectId,
					targetRequirementTypeId, targetFolderId);
			// lets iterate through all the requiremetns and create an array of requirements
			JSONArray jsonRequirements = new JSONArray();
			Iterator gSR = globalSearchReport.iterator();
			while (gSR.hasNext()) {
				GlobalRequirement globalRequirement = (GlobalRequirement) gSR.next();
				Requirement requirement = globalRequirement.getRequirement();
			 	// if the user does not have read permissions on the folder where this req resides, lets
				// redact it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}

				JSONObject jsonRequirement = fillRequirementJSON(requirement);
				jsonRequirements.put(jsonRequirement);
			}
			jsonSearch.put("requirements", jsonRequirements);
			json.put("searchResults", jsonSearch);
			
			out.print(json.toString(3));

		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}	 

	 ///////////////////////////////////////////////////////////////////////////////////
	 // Requirement API Calls
	 ///////////////////////////////////////////////////////////////////////////////////	 

	 // returns a void
	 // however prints a JSON object that Requirements that matches this id
	 public static void getRequirementById(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			
			String requirementIdString = request.getParameter("requirementId");
			if (requirementIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "requirementId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int requirementId = Integer.parseInt(requirementIdString);
			Requirement requirement = new Requirement(requirementId,   databaseType);
		 	// if the user does not have read permissions on the folder where this req resides, lets
			// redact it.
			if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
					+ requirement.getFolderId()))){
				requirement.redact();
			}

			
			if (!(securityProfile.getRoles().contains("MemberInProject" + requirement.getProjectId()))){
				// the project does not have access to this project. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "you do not have access to this project");
				out.print(json.toString(3));
				return;
			}

			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			JSONObject jsonRequirement = fillRequirementJSON(requirement);
			json.put("requirementDetails", jsonRequirement);
			
			out.print(json.toString(3));

		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {
		}
	}	 	 
	 

	 
	 // returns a void
	 // however prints a JSON object that Requirements that matches this id
	 public static void getRequirementByFullTag(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			
			String projectIdString = request.getParameter("projectId");
			if (projectIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "projectId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int projectId = Integer.parseInt(projectIdString);
			
			String fullTag = request.getParameter("fullTag");
			if (fullTag  == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "fullTag is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			
			
			int requirementId = RequirementUtil.getRequirementId(projectId, fullTag);
			Requirement requirement = new Requirement(requirementId,  databaseType);
		 	// if the user does not have read permissions on the folder where this req resides, lets
			// redact it.
			if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
					+ requirement.getFolderId()))){
				requirement.redact();
			}

			if (!(securityProfile.getRoles().contains("MemberInProject" + requirement.getProjectId()))){
				// the project does not have access to this project. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "you do not have access to this project");
				out.print(json.toString(3));
				return;
			}

			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			
			JSONObject jsonRequirement = fillRequirementJSON(requirement);
			json.put("requirementDetails", jsonRequirement);
			
			out.print(json.toString(3));

		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {
		}
	}	 	 
	 
	 
	 // returns a void
	 // however prints a JSON array of requireemnts that the input parameter requirement traces to
	 public static void getRequirementTracesTo(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			
			String requirementIdString = request.getParameter("requirementId");
			if (requirementIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "requirementId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int requirementId = Integer.parseInt(requirementIdString);
			Requirement requirement = new Requirement(requirementId,  databaseType);
		 	// if the user does not have read permissions on the folder where this req resides, lets
			// redact it.
			if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
					+ requirement.getFolderId()))){
				requirement.redact();
			}

			if (!(securityProfile.getRoles().contains("MemberInProject" + requirement.getProjectId()))){
				// the project does not have access to this project. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "you do not have access to this project");
				out.print(json.toString(3));
				return;
			}

			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			
			JSONObject jsonRequirement = fillRequirementJSON(requirement);
			json.put("requirementDetails", jsonRequirement);

			ArrayList tracesTo = requirement.getRequirementTraceToObjects();
			Iterator t = tracesTo.iterator();
			JSONArray jsonTraceTos = new JSONArray();
			
			while(t.hasNext()) {
				Trace trace = (Trace) t.next();
				JSONObject jsonTraceTo = fillTraceToJSON(trace,  databaseType);
				jsonTraceTos.put(jsonTraceTo);
			}

			json.put("requirementTraceTo", jsonTraceTos);
			out.print(json.toString(3));

		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {
		}
	}	 	 
	 	 
	 

	 // returns a void
	 // however creates a Requierment and prints a JSON object that lists 
	 // information about the newly created Requirement
	 public static void createRequirement(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String projectIdString = request.getParameter("projectId");
			if (projectIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "projectId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int projectId = Integer.parseInt(projectIdString);
			Project project = new Project(projectId,  databaseType);
			
			String folderIdString = request.getParameter("folderId");
			if (folderIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "folderId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int folderId = Integer.parseInt(folderIdString);
			Folder folder = new Folder(folderId);
			
			String requirementName = request.getParameter("requirementName");
			String requirementDescription = request.getParameter("requirementDescription");
			if (
				((requirementName == null)||(requirementName.equals("")))	
				&&
				((requirementDescription == null)||(requirementDescription.equals("")))	
				){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "Either requirementName or requirementDescription is a required parameter for this method call");
				out.print(json.toString(3));
				return;
				
			}
			String requirementPriority = request.getParameter("requirementPriority");
			if ((requirementPriority == null) ||  (requirementPriority.equals(""))){
				requirementPriority = "Medium";
			}
			
			String requirementOwner = request.getParameter("requirementOwner");
			if ((requirementOwner == null) ||  (requirementOwner.equals(""))){
				// if no owner is given, lets make the api called the owner.
				requirementOwner = securityProfile.getUser().getEmailId();
			}
			else {
				if (!(ProjectUtil.isValidUserInProject(requirementOwner, project))){
					// the user is not a member of this project. so making the api called the owner.
					// if the given req owner is not a member of this project
					// we replace it with the creator of this req.
					requirementOwner = securityProfile.getUser().getEmailId();
				}
			}
			
			
			String requirementPctCompleteString = request.getParameter("requirementPctComplete");
			int requirementPctComplete = 0;
			try {
				requirementPctComplete = Integer.parseInt(requirementPctCompleteString);
			}
			catch (Exception e){
				requirementPctComplete = 0;
			}
			if (requirementPctComplete < 0 ){
				requirementPctComplete = 0;
			}
			if (requirementPctComplete > 100){
				requirementPctComplete = 100;
			}
			
			String requirementExternalURL = request.getParameter("requirementExternalURL");
			if ((requirementExternalURL == null) ||  (requirementExternalURL.equals(""))){
				requirementExternalURL = "";
			}
			

			String parentFullTag = request.getParameter("parentFullTag");
			if ((parentFullTag != null) && (!parentFullTag.equals(""))){
				// parent full tag is given. so lets make sure its valid.

				Requirement parent = new Requirement(parentFullTag, projectId,  databaseType);

				String errorOutput = "";
				if (parent.getRequirementId() == 0  ){
					errorOutput = "This Requirement " + parentFullTag +  " does not exist in this project.";
				}
				else if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
						+ parent.getFolderId()))){
					errorOutput = "You do not have Update permissions on " + 
						parentFullTag +	". Hence you can not create a child to this Requirement.";
					
				}
				else if (folder.getRequirementTypeId() != parent.getRequirementTypeId()){
					errorOutput =  parentFullTag + " belongs to a different Requirement Type." ;
				}
	
				if (!(errorOutput.equals(""))){
					JSONObject json = new JSONObject();
					json.put("responseStatus", "error");
					json.put("errorMessage", errorOutput);
					out.print(json.toString(3));
					return;
				}
			}
			if (parentFullTag == null){
				parentFullTag = "";
			}
			
			
			if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder"	+ folderId))){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "This user does not have permission to Create Requirements in this folder" + folder.getFolderPath());
				out.print(json.toString(3));
				return;
			}
			
			String requirementLockedBy = "";
			Requirement requirement = new Requirement(parentFullTag, folder.getRequirementTypeId(), folderId, projectId, 
					 requirementName, requirementDescription, 
					requirementPriority, requirementOwner, requirementLockedBy, requirementPctComplete,
					requirementExternalURL, securityProfile.getUser().getEmailId(),  databaseType);

			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			JSONObject jsonRequirement = fillRequirementJSON(requirement);
			json.put("requirementDetails", jsonRequirement);
			
			out.print(json.toString(3));
			
		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}
	 
	 

	 // returns a void
	 // however deletes a Requirerment and prints a JSON object that confirms the delete
	 
	 public static void deleteRequirement(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String requirementIdString = request.getParameter("requirementId");
			if ((requirementIdString == null) || (requirementIdString.equals(""))) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "requirementId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int requirementId = 0;
			try {
			requirementId = Integer.parseInt(requirementIdString);
			}
			catch (Exception e){
				requirementId = 0;
			}
			

			Requirement requirement = new Requirement(requirementId,  databaseType);
			
			if ((requirement == null ) || (requirement.getRequirementId() == 0)){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "Requirement could not be located for requirementId " + requirementIdString);
				out.print(json.toString(3));
				return;
			}
			
			
			if (!(securityProfile.getPrivileges().contains("deleteRequirementsInFolder"	+ requirement.getFolderId()))){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "This user does not have privileges to delete this Requirement." );
				out.print(json.toString(3));
				return;
			}

			ProjectUtil.deleteRequirement(requirementId, securityProfile.getUser().getEmailId(),  databaseType);
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			
			out.print(json.toString(3));
			
		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}
	 


	 // returns a void
	 // however purges a Requirerment and prints a JSON object that confirms the delete
	 
	 public static void purgeRequirement(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String requirementIdString = request.getParameter("requirementId");
			if ((requirementIdString == null) || (requirementIdString.equals(""))) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "requirementId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int requirementId = 0;
			try {
			requirementId = Integer.parseInt(requirementIdString);
			}
			catch (Exception e){
				requirementId = 0;
			}
			

			Requirement requirement = new Requirement(requirementId,  databaseType);
			
			if ((requirement == null ) || (requirement.getRequirementId() == 0)){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "Requirement could not be located for requirementId " + requirementIdString);
				out.print(json.toString(3));
				return;
			}
			
			
			if (!(securityProfile.getPrivileges().contains("deleteRequirementsInFolder"	+ requirement.getFolderId()))){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "This user does not have privileges to delete this Requirement." );
				out.print(json.toString(3));
				return;
			}

			ProjectUtil.purgeRequirement(requirementId,  databaseType);
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			
			out.print(json.toString(3));
			
		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}
	 

	 
	 

	 // returns a void
	 // however moves a Requirerment and prints a JSON object that confirms the delete
	 
	 public static void moveRequirement(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String requirementIdString = request.getParameter("requirementId");
			if ((requirementIdString == null) || (requirementIdString.equals(""))) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "requirementId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int requirementId = 0;
			try {
			requirementId = Integer.parseInt(requirementIdString);
			}
			catch (Exception e){
				requirementId = 0;
			}
			

			Requirement requirement = new Requirement(requirementId,  databaseType);
			
			if ((requirement == null ) || (requirement.getRequirementId() == 0)){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "Requirement could not be located for requirementId " + requirementIdString);
				out.print(json.toString(3));
				return;
			}
			
			
			if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder"	+ requirement.getFolderId()))){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "This user does not have privileges to update (move) this Requirement." );
				out.print(json.toString(3));
				return;
			}

			
			
			String folderIdString = request.getParameter("folderId");
			if (folderIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "folderId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int folderId = Integer.parseInt(folderIdString);
			Folder folder = new Folder(folderId);
			
			if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder"	+ folderId))){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "This user does not have privileges to move Requirements to this folder " + folder.getFolderPath() );
				out.print(json.toString(3));
				return;
			}

			if (requirement.getRequirementTypeId() != folder.getRequirementTypeId()){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", " The Target Folder belongs to a different Requirement Type than this Requirement "  );
				out.print(json.toString(3));
				return;
			}
			
			RequirementUtil.moveRequirementToAnotherFolder(requirement, folderId, securityProfile.getUser().getEmailId(),  databaseType);
			
			
			requirement = new Requirement(requirement.getRequirementId(),  databaseType);
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			JSONObject jsonRequirement = fillRequirementJSON(requirement);
			json.put("requirementDetails", jsonRequirement);
			
			out.print(json.toString(3));
			
			
		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}
	 
	 // returns a void
	 // however sets the attribute value of the requirement and prints a JSON object that confirms the delete
	 
	 public static void setAttribute(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String requirementIdString = request.getParameter("requirementId");
			if ((requirementIdString == null) || (requirementIdString.equals(""))) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "requirementId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int requirementId = 0;
			try {
			requirementId = Integer.parseInt(requirementIdString);
			}
			catch (Exception e){
				requirementId = 0;
			}
			

			Requirement requirement = new Requirement(requirementId,  databaseType);
			
			if ((requirement == null ) || (requirement.getRequirementId() == 0)){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "Requirement could not be located for requirementId " + requirementIdString);
				out.print(json.toString(3));
				return;
			}
			
			
			if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder"	+ requirement.getFolderId()))){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "This user does not have privileges to update this Requirement." );
				out.print(json.toString(3));
				return;
			}

			
			
			String attributeLabel = request.getParameter("attributeLabel");
			if ((attributeLabel != null) && (!attributeLabel.equals(""))){
				String attributeValue = request.getParameter("attributeValue");
				if (attributeLabel.equals("requirementName")){
					requirement = new Requirement( requirementId, attributeValue, 
					requirement.getRequirementDescription(), requirement.getRequirementPriority(), 
					requirement.getRequirementOwner(), 
					requirement.getRequirementPctComplete(),requirement.getRequirementExternalUrl(),
					securityProfile.getUser().getEmailId(), request,  databaseType);
				}
				else if (attributeLabel.equals("requirementDescription")){
					requirement = new Requirement( requirementId, requirement.getRequirementName(), 
					attributeValue, requirement.getRequirementPriority(), 
					requirement.getRequirementOwner(), 
					requirement.getRequirementPctComplete(),requirement.getRequirementExternalUrl(),
					securityProfile.getUser().getEmailId(), request,  databaseType);
				}
				else if (attributeLabel.equals("requirementPriority")){
					
					if (attributeValue.toLowerCase().trim().equals("high")){
						attributeValue = "High";
					}
					else if (attributeValue.toLowerCase().trim().equals("medium")){
						attributeValue = "Medium";
					}
					else if (attributeValue.toLowerCase().trim().equals("low")){
						attributeValue = "Low";
					}
					else {
						attributeValue = "Medium";
					}
					
					
					requirement = new Requirement( requirementId, requirement.getRequirementName(), 
					requirement.getRequirementDescription(), attributeValue, 
					requirement.getRequirementOwner(), 
					requirement.getRequirementPctComplete(),requirement.getRequirementExternalUrl(),
					securityProfile.getUser().getEmailId(), request,  databaseType);
				}
				else if (attributeLabel.equals("requirementOwner")){
					Project project  = new Project(requirement.getProjectId(),  databaseType);
					if ((attributeValue == null) ||  (attributeValue.equals(""))){
						// if no owner is given, lets make the api called the owner.
						attributeValue = securityProfile.getUser().getEmailId();
					}
					else {
						if (!(ProjectUtil.isValidUserInProject(attributeValue, project))){
							// the user is not a member of this project. so making the api called the owner.
							// if the given req owner is not a member of this project
							// we replace it with the creator of this req.
							attributeValue = securityProfile.getUser().getEmailId();
						}
					}
					
					requirement = new Requirement( requirementId, requirement.getRequirementName(), 
					requirement.getRequirementDescription(), requirement.getRequirementPriority(), 
					attributeValue, 
					requirement.getRequirementPctComplete(),requirement.getRequirementExternalUrl(),
					securityProfile.getUser().getEmailId(), request,  databaseType);
				}
				else if (attributeLabel.equals("requirementPctComplete")){
					int requirementPctComplete = 0;
					try {
						requirementPctComplete = Integer.parseInt(attributeValue);
					}
					catch (Exception e){
						requirementPctComplete = 0;
					}
					if (requirementPctComplete < 0 ){
						requirementPctComplete = 0;
					}
					if (requirementPctComplete > 100){
						requirementPctComplete = 100;
					}

					requirement = new Requirement( requirementId, requirement.getRequirementName(), 
					requirement.getRequirementDescription(), requirement.getRequirementPriority(), 
					requirement.getRequirementOwner(), 
					requirementPctComplete, requirement.getRequirementExternalUrl(),
					securityProfile.getUser().getEmailId(), request,  databaseType);
				}
				else if (attributeLabel.equals("requirementExternalURL")){
					requirement = new Requirement( requirementId, requirement.getRequirementName(), 
					requirement.getRequirementDescription(), requirement.getRequirementPriority(), 
					requirement.getRequirementOwner(), 
					requirement.getRequirementPctComplete(),attributeValue,
					securityProfile.getUser().getEmailId(), request,  databaseType);
				}
				else {
					// this means that this is a custom attribute.
					// lets get the RTAttribute object for this attribute
					RTAttribute rTAttribute = new RTAttribute(requirement.getRequirementTypeId(), attributeLabel);
					requirement.setCustomAttributeValue(rTAttribute.getAttributeId(), attributeValue, securityProfile.getUser(),  databaseType);
					
					// if this attribute has been flagged as impacts versioning , then
					// lets update the Req Version.
					if (rTAttribute.getAttributeImpactsVersion() == 1 ) {
						RequirementUtil.updateVersion(requirementId, request,  databaseType);
						// at this point, lets create an entry in the gr_requirement_version table
						RequirementUtil.createRequirementVersion(requirementId);

					}

					// if the attributes has been flagged as impacts traceability , then
					// lets update the Req Traceability.
					if (rTAttribute.getAttributeImpactsTraceability() == 1) {
						String traceDefinition = attributeLabel + ":" + attributeValue + "  ";
						RequirementUtil.updateTraceability(traceDefinition, requirementId, request, securityProfile.getUser().getEmailId(),  databaseType);
					}

					// if the attributes has been flagged as impacts traceability , then
					// lets update the Req ApprovalWorkFlow .
					if (rTAttribute.getAttributeImpactsApprovalWorkflow() == 1) {
						RequirementUtil.updateApprovalWorkflow(requirementId, request);
					}
				}
				
				requirement = new Requirement(requirementId,  databaseType);
				JSONObject json = new JSONObject();
				json.put("responseStatus", "success");
				json.put("errorMessage", ""  );
				JSONObject jsonRequirement = fillRequirementJSON(requirement);
				json.put("requirementDetails", jsonRequirement);
				
				out.print(json.toString(3));
			}
			
			
			
		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}
	 


	 
	 

	 // returns a void
	 // however creates traces to and from a requirement prints a JSON object that confirms the delete
	 
	 public static void createTrace(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String requirementIdString = request.getParameter("requirementId");
			if ((requirementIdString == null) || (requirementIdString.equals(""))) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "requirementId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int requirementId = 0;
			try {
			requirementId = Integer.parseInt(requirementIdString);
			}
			catch (Exception e){
				requirementId = 0;
			}
			

			Requirement requirement = new Requirement(requirementId,  databaseType);
			
			if ((requirement == null ) || (requirement.getRequirementId() == 0)){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "Requirement could not be located for requirementId " + requirementIdString);
				out.print(json.toString(3));
				return;
			}
			
			
			if (!(securityProfile.getPrivileges().contains("traceToRequirementsInFolder"	+ requirement.getFolderId()))){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "This user does not have privileges to Trace To / From this Requirement." );
				out.print(json.toString(3));
				return;
			}

			
			
			Project project = new Project(requirement.getProjectId(),  databaseType);
			String createTraceTo = request.getParameter("traceTo");
			String createTraceFrom = request.getParameter("traceFrom");

			// Call RequirementUtil.createTraces
			// Get the error / status message
			String status = RequirementUtil.createTraces(project, requirementId, 
				createTraceTo, createTraceFrom, project.getProjectId(), securityProfile,  databaseType);
			if (status != ""){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", status);
				out.print(json.toString(3));
				return;
			}
			else {
				
				requirement = new Requirement(requirement.getRequirementId(),  databaseType);
				JSONObject json = new JSONObject();
				json.put("responseStatus", "success");
				json.put("errorMessage", ""  );
				JSONObject jsonRequirement = fillRequirementJSON(requirement);
				json.put("requirementDetails", jsonRequirement);
				out.print(json.toString(3));
			}
		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}

	 
	 public static void deleteTrace(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String requirementIdString = request.getParameter("requirementId");
			if ((requirementIdString == null) || (requirementIdString.equals(""))) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "requirementId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int requirementId = 0;
			try {
			requirementId = Integer.parseInt(requirementIdString);
			}
			catch (Exception e){
				requirementId = 0;
			}
			

			Requirement requirement = new Requirement(requirementId,  databaseType);
			
			if ((requirement == null ) || (requirement.getRequirementId() == 0)){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "Requirement could not be located for requirementId " + requirementIdString);
				out.print(json.toString(3));
				return;
			}
			
			
			if (!(securityProfile.getRoles().contains("MemberInProject" + requirement.getProjectId()))){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "This user is not a member of the project where the requirment resides." );
				out.print(json.toString(3));
				return;
			}

			
			
			Project project = new Project(requirement.getProjectId(),  databaseType);
			String deleteTraceTo = request.getParameter("traceTo");
			String deleteTraceFrom = request.getParameter("traceFrom");

		
			// Call RequirementUtil.deleteTraces
			// Get the error / status message
			String status = RequirementUtil.deleteTraces(project, requirementId, 
				deleteTraceTo, deleteTraceFrom, project.getProjectId(), securityProfile,  databaseType);
			if (status != ""){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", status);
				out.print(json.toString(3));
				return;
			}
			else {
				
				requirement = new Requirement(requirement.getRequirementId(),  databaseType);
				JSONObject json = new JSONObject();
				json.put("responseStatus", "success");
				json.put("errorMessage", ""  );
				JSONObject jsonRequirement = fillRequirementJSON(requirement);
				json.put("requirementDetails", jsonRequirement);
				out.print(json.toString(3));
			}
		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}

	 
	 
	 public static void clearSuspectTrace(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String requirementIdString = request.getParameter("requirementId");
			if ((requirementIdString == null) || (requirementIdString.equals(""))) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "requirementId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int requirementId = 0;
			try {
			requirementId = Integer.parseInt(requirementIdString);
			}
			catch (Exception e){
				requirementId = 0;
			}
			

			Requirement requirement = new Requirement(requirementId,  databaseType);
			
			if ((requirement == null ) || (requirement.getRequirementId() == 0)){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "Requirement could not be located for requirementId " + requirementIdString);
				out.print(json.toString(3));
				return;
			}
			
			
			if (!(securityProfile.getRoles().contains("MemberInProject" + requirement.getProjectId()))){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "This user is not a member of the project where the requirment resides." );
				out.print(json.toString(3));
				return;
			}

			
			
			Project project = new Project(requirement.getProjectId(),  databaseType);
			String clearTraceTo = request.getParameter("traceTo");
			String clearTraceFrom = request.getParameter("traceFrom");

		
			// Call RequirementUtil.deleteTraces
			// Get the error / status message
			String status = RequirementUtil.clearSuspectTraces(project, requirementId, 
				clearTraceTo, clearTraceFrom, project.getProjectId(), securityProfile,  databaseType);
			if (status != ""){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", status);
				out.print(json.toString(3));
				return;
			}
			else {
				
				requirement = new Requirement(requirement.getRequirementId(),  databaseType);
				JSONObject json = new JSONObject();
				json.put("responseStatus", "success");
				json.put("errorMessage", ""  );
				JSONObject jsonRequirement = fillRequirementJSON(requirement);
				json.put("requirementDetails", jsonRequirement);
				out.print(json.toString(3));
			}
		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}

	 public static void makeTraceSuspect(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			String requirementIdString = request.getParameter("requirementId");
			if ((requirementIdString == null) || (requirementIdString.equals(""))) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "requirementId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int requirementId = 0;
			try {
			requirementId = Integer.parseInt(requirementIdString);
			}
			catch (Exception e){
				requirementId = 0;
			}
			

			Requirement requirement = new Requirement(requirementId,  databaseType);
			
			if ((requirement == null ) || (requirement.getRequirementId() == 0)){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "Requirement could not be located for requirementId " + requirementIdString);
				out.print(json.toString(3));
				return;
			}
			
			
			if (!(securityProfile.getRoles().contains("MemberInProject" + requirement.getProjectId()))){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "This user is not a member of the project where the requirment resides." );
				out.print(json.toString(3));
				return;
			}

			
			
			Project project = new Project(requirement.getProjectId(),  databaseType);
			String markSuspectTraceTo = request.getParameter("traceTo");
			String markSuspectTraceFrom = request.getParameter("traceFrom");

		
			// Call RequirementUtil.deleteTraces
			// Get the error / status message
			String status = RequirementUtil.makeTraceSuspect(project, requirementId, 
					markSuspectTraceTo, markSuspectTraceFrom, project.getProjectId(), securityProfile,  databaseType);
			if (status != ""){
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", status);
				out.print(json.toString(3));
				return;
			}
			else {
				
				requirement = new Requirement(requirement.getRequirementId(),  databaseType);
				JSONObject json = new JSONObject();
				json.put("responseStatus", "success");
				json.put("errorMessage", ""  );
				JSONObject jsonRequirement = fillRequirementJSON(requirement);
				json.put("requirementDetails", jsonRequirement);
				out.print(json.toString(3));
			}
		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}

	 public static void jiraUpdate(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			
			

			
			HttpSession session = request.getSession(true);
			User user = securityProfile.getUser();
			String JID = request.getParameter("JID");
			if ((JID == null) || (JID.equals(""))) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "JID is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			String JURL = request.getParameter("JURL");
			if ((JURL == null) || (JURL.equals(""))) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "JURL is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			
			String JPROJECT = request.getParameter("JPROJECT");
			
			String JTYPE = request.getParameter("JTYPE");
			String JPRIORITY = request.getParameter("JPRIORITY");
			String JLABELS = request.getParameter("JLABELS");
			String JSTATUS = request.getParameter("JSTATUS");
			String JRESOLUTION = request.getParameter("JRESOLUTION");
			String JAFFECTSV = request.getParameter("JAFFECTSV");
			String JFIXV = request.getParameter("JFIXV");
			String JASSIGNEE = request.getParameter("JASSIGNEE");
			String JREPORTER = request.getParameter("JREPORTER");
			String JCREATED = request.getParameter("JCREATED");
			String JUPDATED = request.getParameter("JUPDATED");
			
			String JTITLE = request.getParameter("JTITLE");
			String JDESCRIPTION = request.getParameter("JDESCRIPTION");
			
			
			if (JPROJECT == null ) {JPROJECT = "";}
			
			if (JTYPE == null ) {JTYPE = "";}
			if (JPRIORITY == null ) {JPRIORITY = "";}
			if (JLABELS == null) {JLABELS = "";}
			if (JSTATUS == null ) {JSTATUS = "";}
			if (JRESOLUTION == null ) {JRESOLUTION = "";}
			if (JAFFECTSV == null ) {JAFFECTSV = "";}
			if (JFIXV == null ) {JFIXV = "";}
			if (JASSIGNEE == null ) {JASSIGNEE = "";}
			if (JREPORTER == null ) {JREPORTER = "";}
			if (JCREATED == null ) {JCREATED = "";}
			if (JUPDATED == null ) {JUPDATED = "";}
			
			if (JTITLE == null ) {JTITLE = "";}
			if (JDESCRIPTION == null ) {JDESCRIPTION = "";}
			
			// lets get all jira proxy objects for this jira
			ArrayList jiraProxies = JiraUtil.getProxies(JID, JURL, securityProfile.getUser(), securityProfile, databaseType);
			
			// for each of the proxy objects, lets see which parameter was sent and then update it. 
			Iterator jPI = jiraProxies.iterator();
			
			JSONArray jsonJiraProxies = new JSONArray();

			while (jPI.hasNext()){
				Requirement jiraProxy = (Requirement) jPI.next();
				
				// there are times when jira proxy has been deleted in the TraceCloud system, but it still exists
				// in jira_tc_map table. in that case, the jiraProxy will be an empty object.
				if (jiraProxy == null || jiraProxy.getRequirementFullTag() == null || jiraProxy.getRequirementFullTag().equals("")){
					continue;
				}
				if (!(securityProfile.getRoles().contains("MemberInProject" + jiraProxy.getProjectId()))){
					// this user is not a member of the project where this TR Proxy exists. So, don't show it.
					continue;
				}
				
				RequirementType jRT = new RequirementType(jiraProxy.getRequirementTypeId());
				
				
				Folder jiraFolder = JiraUtil.getJiraFolder(  jiraProxy ,  jRT,  jiraProxy.getProjectId(),
						 JPROJECT,  user ,  securityProfile,  session, databaseType);
				
				// for this jira proxy, lets see what input was sent in and then update it.
				JiraUtil.updateJiraProxy(jiraProxy, jRT, jiraFolder, user, JID, JPROJECT, JTYPE, JPRIORITY, JLABELS, JSTATUS,
						JRESOLUTION, JAFFECTSV, JFIXV, JASSIGNEE, JREPORTER, JCREATED, JUPDATED, JURL, JTITLE, JDESCRIPTION,
						securityProfile, request, session, databaseType);
				// since we just updated the requirement, lets refresh it in memory
				jiraProxy = new Requirement(jiraProxy.getRequirementId(), databaseType);
			 	// if the user does not have read permissions on the folder where this req resides, lets
				// redact it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ jiraProxy.getFolderId()))){
					jiraProxy.redact();
				}
				JSONObject jsonJiraProxy = fillJiraProxyJSON(jiraProxy, securityProfile, databaseType);
				jsonJiraProxies.put(jsonJiraProxy);
				
			}
			
			json.put("Jira Proxies", jsonJiraProxies);
			out.print(json.toString(3));
			
		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}
	 
	 // returns a void
	 // however prints a JSON array of requireemnts that the input parameter requirement has traces from
	 public static void getRequirementTracesFrom(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			
			String requirementIdString = request.getParameter("requirementId");
			if (requirementIdString == null) {
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "requirementId is a required parameter for this method call");
				out.print(json.toString(3));
				return;
			}
			int requirementId = Integer.parseInt(requirementIdString);
			Requirement requirement = new Requirement(requirementId,  databaseType);
		 	// if the user does not have read permissions on the folder where this req resides, lets
			// redact it.
			if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
					+ requirement.getFolderId()))){
				requirement.redact();
			}

			if (!(securityProfile.getRoles().contains("MemberInProject" + requirement.getProjectId()))){
				// the project does not have access to this project. error out.
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "you do not have access to this project");
				out.print(json.toString(3));
				return;
			}

			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );
			
			JSONObject jsonRequirement = fillRequirementJSON(requirement);
			json.put("requirementDetails", jsonRequirement);

			ArrayList tracesFrom = requirement.getRequirementTraceFromObjects();
			Iterator t = tracesFrom.iterator();
			JSONArray jsonTraceFroms = new JSONArray();
			
			while(t.hasNext()) {
				Trace trace = (Trace) t.next();
				JSONObject jsonTraceFrom = fillTraceFromJSON(trace,  databaseType);
				jsonTraceFroms.put(jsonTraceFrom);
			}

			json.put("requirementTraceFrom", jsonTraceFroms);
			out.print(json.toString(3));

		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {
		}
	}	 	 
	 
	 
	 //    some common service methods for api methods.
	 
	 // fillRequirementTypeJSON. Takes a ReqType object and gets a JSON object of it.
	 public static JSONObject fillRequirementTypeJSON(RequirementType requirementType){
		 JSONObject jsonRequirementType = new JSONObject();
		try {			
			
			jsonRequirementType.put("requirementTypeId", requirementType.getRequirementTypeId());
			jsonRequirementType.put("projectId", requirementType.getProjectId());
			jsonRequirementType.put("prefix", requirementType.getRequirementTypeShortName());
			jsonRequirementType.put("name", requirementType.getRequirementTypeName());
			jsonRequirementType.put("description", requirementType.getRequirementTypeDescription());
			jsonRequirementType.put("enableApproval", requirementType.getRequirementTypeEnableApproval());
			jsonRequirementType.put("rootFolderId", requirementType.getRootFolderId());
			jsonRequirementType.put("createdBy", requirementType.getCreatedBy());
			jsonRequirementType.put("lastModifiedBy", requirementType.getLastModifiedBy());
			
			// lets iterate and get all the custom attributes of this requierment type.
			JSONArray jsonAttributes = new JSONArray();
			ArrayList attributes = requirementType.getAllAttributesInRequirementType();
			Iterator a = attributes.iterator();
			while (a.hasNext()) {
				RTAttribute attribute = (RTAttribute) a.next();
				JSONObject jsonAttribute = new JSONObject();
				jsonAttribute.put("attributeId", attribute.getAttributeId());
				jsonAttribute.put("requirementTypeId", attribute.getRequirementTypeId());
				jsonAttribute.put("attributeName", attribute.getAttributeName());
				jsonAttribute.put("attributeType", attribute.getAttributeType());
				jsonAttribute.put("attributeRequired", attribute.getAttributeRequired());
				jsonAttribute.put("attributeDefaultValue", attribute.getAttributeDefaultValue());
				jsonAttribute.put("attributeDropDownOptions", attribute.getAttributeDropDownOptions());
				jsonAttribute.put("attributeDescription", attribute.getAttributeDescription());
				jsonAttribute.put("attributeSortOrder", attribute.getAttributeSortOrder());
				jsonAttribute.put("attributeImpactsVersion", attribute.getAttributeImpactsVersion());
				jsonAttribute.put("attributeImpactsTraceability", attribute.getAttributeImpactsTraceability());
				jsonAttribute.put("attributeImpactsApprovalWorkflow", attribute.getAttributeImpactsApprovalWorkflow());
				jsonAttribute.put("createdBy", attribute.getCreatedBy());
				jsonAttribute.put("lastModifiedBy", attribute.getLastModifiedBy());
				jsonAttributes.put(jsonAttribute); 
			}
			jsonRequirementType.put("attributes", jsonAttributes);
			

		} catch (Exception e) {					
				e.printStackTrace();				
				jsonRequirementType.put("responseStatus", "error");
				jsonRequirementType.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");					
		}   finally {
		}
		return (jsonRequirementType);
	}

	 // . Takes a Folder object and gets a JSON object of it.
	public static JSONObject fillFolderJSON(Folder folder, boolean includeMetrics){
		JSONObject jsonFolder = new JSONObject();
		try {

			
			jsonFolder.put("folderId", folder.getFolderId());
			jsonFolder.put("projectId", folder.getProjectId());
			jsonFolder.put("name", folder.getFolderName());
			jsonFolder.put("description", folder.getFolderDescription());
			jsonFolder.put("parentFolderId", folder.getParentFolderId());
			jsonFolder.put("folderLevel", folder.getFolderLevel());
			jsonFolder.put("folderOrder", folder.getFolderOrder());
			jsonFolder.put("folderPath", folder.getFolderPath());
			jsonFolder.put("requirementTypeId", folder.getRequirementTypeId());
			jsonFolder.put("requirementTypeName", folder.getRequirementTypeName());
			jsonFolder.put("createdBy", folder.getCreatedBy());
			jsonFolder.put("lastModifiedBy", folder.getLastModifiedBy());
			
			
			if (includeMetrics){
				// lets put the metrics 
				int noOfReqsInThisFolder = folder.getCountOfRequirements();
				jsonFolder.put("isFolderEnabledForApproval", folder.getIsFolderEnabledForApproval());
				jsonFolder.put("totalObjects", noOfReqsInThisFolder);
				
				
				int noOfPending = 0; 
				int noOfApproved = 0 ;
				int noOfRejected = 0 ;
				int noOfDraft = 0;
				
				noOfPending = folder.getFolderMetric_NoOfApprovalPendingRequirements();
				noOfApproved = folder.getFolderMetric_NoOfApprovedRequirements();
				noOfRejected = folder.getFolderMetric_NoOfRejectedRequirements();
				noOfDraft = noOfReqsInThisFolder - (noOfPending + noOfRejected + noOfApproved);
					
				jsonFolder.put("pendingObjects", noOfPending);
				jsonFolder.put("approvedObjects",noOfApproved);
				jsonFolder.put("rejectedObjects",noOfRejected);
				jsonFolder.put("draftObjects", noOfDraft);
			
				
				
				int noOfDangling = 0;
				if (folder.canBeReportedDangling()){
					noOfDangling = folder.getFolderMetric_NoOfDanglingRequirements();
				}
				jsonFolder.put("danglingObjects", noOfDangling);
				
				
				int noOfOrphan = 0;
				if (folder.canBeReportedOrphan()){
					noOfOrphan = folder.getFolderMetric_NoOfOrphanRequirements();
				}
				jsonFolder.put("orphanObjects", noOfOrphan);
				
				
				jsonFolder.put("suspectUpstreamObjects", folder.getFolderMetric_NoOfSuspectUpstreamRequirements());
				jsonFolder.put("suspectDownstreamObjects", folder.getFolderMetric_NoOfSuspectDownstreamRequirements());
				int noOfCompletes = folder.getFolderMetric_NoOfCompletedRequirements();
				jsonFolder.put("completeObjects", noOfCompletes);
				
				int noOfIncompletes =  noOfReqsInThisFolder - noOfCompletes;
				jsonFolder.put("incompleteObjects", noOfIncompletes );
				
				int noOfTestPending = folder.getFolderMetric_NoOfTestPendingRequirements();
				jsonFolder.put("testPendingObjects", noOfTestPending );
				
				
				int noOfTestPass = folder.getFolderMetric_NoOfTestPassRequirements();
				jsonFolder.put("testPassObjects", noOfTestPass );
				
				
				
				int noOfTestFail = noOfReqsInThisFolder - (noOfTestPending + noOfTestPass );
				jsonFolder.put("testFailObjects", noOfTestFail );
			}
			else {
				// lets put the metrics 
				int noOfReqsInThisFolder = -1;
				jsonFolder.put("isFolderEnabledForApproval", -1);
				jsonFolder.put("totalObjects", noOfReqsInThisFolder);
				
				
				int noOfPending = -1; 
				int noOfApproved = -1 ;
				int noOfRejected = -1;
				int noOfDraft = -1;
				
				noOfPending = folder.getFolderMetric_NoOfApprovalPendingRequirements();
				noOfApproved = folder.getFolderMetric_NoOfApprovedRequirements();
				noOfRejected = folder.getFolderMetric_NoOfRejectedRequirements();
				noOfDraft = -1;
					
				jsonFolder.put("pendingObjects", noOfPending);
				jsonFolder.put("approvedObjects",noOfApproved);
				jsonFolder.put("rejectedObjects",noOfRejected);
				jsonFolder.put("draftObjects", noOfDraft);
			
				
				
				int noOfDangling = -1;
				jsonFolder.put("danglingObjects", noOfDangling);
				
				
				int noOfOrphan = -1;
				jsonFolder.put("orphanObjects", noOfOrphan);
				
				
				jsonFolder.put("suspectUpstreamObjects", -1);
				jsonFolder.put("suspectDownstreamObjects", -1);
				int noOfCompletes = -1;
				jsonFolder.put("completeObjects", noOfCompletes);
				
				int noOfIncompletes =  -1;
				jsonFolder.put("incompleteObjects", noOfIncompletes );
				
				int noOfTestPending = -1;
				jsonFolder.put("testPendingObjects", noOfTestPending );
				
				
				int noOfTestPass = -1;
				jsonFolder.put("testPassObjects", noOfTestPass );
				
				
				
				int noOfTestFail = -1;
				jsonFolder.put("testFailObjects", noOfTestFail );
			}

		} catch (Exception e) {					
				e.printStackTrace();
				jsonFolder.put("responseStatus", "error");
				jsonFolder.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");					
		}   finally {

		}
		return (jsonFolder);
	}

	 

	 // fillRequirementJSON. Takes a Requirement object and gets a JSON object of it.
	public static JSONObject fillRequirementJSON(Requirement requirement){
		JSONObject jsonRequirement = new JSONObject();
		try {
			jsonRequirement.put("requirementId", requirement.getRequirementId());
			jsonRequirement.put("folderId", requirement.getFolderId());
			jsonRequirement.put("folderId", requirement.getFolderId());
			jsonRequirement.put("projectId", requirement.getProjectId());
			jsonRequirement.put("name", requirement.getRequirementName());
			jsonRequirement.put("description", requirement.getRequirementDescription());
			jsonRequirement.put("tag", requirement.getRequirementTag());
			jsonRequirement.put("fullTag", requirement.getRequirementFullTag());
			jsonRequirement.put("version", requirement.getVersion());
			jsonRequirement.put("approvedByAllDt", requirement.getApprovedByAllDt());
			jsonRequirement.put("approvers", requirement.getApprovers());
			jsonRequirement.put("status", requirement.getApprovalStatus());
			jsonRequirement.put("testingStatus", requirement.getTestingStatus());
			
			jsonRequirement.put("priority", requirement.getRequirementPriority());
			jsonRequirement.put("owner", requirement.getRequirementOwner());
			jsonRequirement.put("pctComplete", requirement.getRequirementPctComplete());
			jsonRequirement.put("externalURL", requirement.getRequirementExternalUrl());
			jsonRequirement.put("traceTo", requirement.getRequirementTraceTo());
			jsonRequirement.put("traceFrom", requirement.getRequirementTraceFrom());
			jsonRequirement.put("userDefinedAttributes", requirement.getUserDefinedAttributes());
			jsonRequirement.put("deleted", requirement.getDeleted());
			jsonRequirement.put("folderPath", requirement.getFolderPath());
			jsonRequirement.put("createdBy", requirement.getCreatedBy());
			jsonRequirement.put("lastModifiedBy", requirement.getLastModifiedBy());
			jsonRequirement.put("requirementTypeName", requirement.getRequirementTypeName());
		} catch (Exception e) {					
				e.printStackTrace();
				jsonRequirement.put("responseStatus", "error");
				jsonRequirement.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");					
		}   finally {

		}
		return (jsonRequirement);
	}

	public static JSONObject fillJiraProxyJSON(Requirement jiraProxy,SecurityProfile securityProfile, String databaseType ){
		JSONObject jsonRequirement = new JSONObject();
		try {
			jsonRequirement.put("jiraProxyId", jiraProxy.getRequirementId());
			jsonRequirement.put("folderId", jiraProxy.getFolderId());
			jsonRequirement.put("folderId", jiraProxy.getFolderId());
			jsonRequirement.put("projectId", jiraProxy.getProjectId());
			jsonRequirement.put("name", jiraProxy.getRequirementName());
			jsonRequirement.put("description", jiraProxy.getRequirementDescription());
			jsonRequirement.put("tag", jiraProxy.getRequirementTag());
			jsonRequirement.put("fullTag", jiraProxy.getRequirementFullTag());
			jsonRequirement.put("version", jiraProxy.getVersion());
			jsonRequirement.put("approvedByAllDt", jiraProxy.getApprovedByAllDt());
			jsonRequirement.put("approvers", jiraProxy.getApprovers());
			jsonRequirement.put("status", jiraProxy.getApprovalStatus());
			jsonRequirement.put("priority", jiraProxy.getRequirementPriority());
			jsonRequirement.put("owner", jiraProxy.getRequirementOwner());
			jsonRequirement.put("pctComplete", jiraProxy.getRequirementPctComplete());
			jsonRequirement.put("externalURL", jiraProxy.getRequirementExternalUrl());
			jsonRequirement.put("traceTo", jiraProxy.getRequirementTraceTo());
			jsonRequirement.put("traceFrom", jiraProxy.getRequirementTraceFrom());
			jsonRequirement.put("userDefinedAttributes", jiraProxy.getUserDefinedAttributes());
			jsonRequirement.put("deleted", jiraProxy.getDeleted());
			jsonRequirement.put("folderPath", jiraProxy.getFolderPath());
			jsonRequirement.put("createdBy", jiraProxy.getCreatedBy());
			jsonRequirement.put("lastModifiedBy", jiraProxy.getLastModifiedBy());
			jsonRequirement.put("requiementTypeName", jiraProxy.getRequirementTypeName());
		} catch (Exception e) {					
				e.printStackTrace();
				jsonRequirement.put("responseStatus", "error");
				jsonRequirement.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");					
		}   finally {

		}
		
		// get traceToDetails and traceFromDetails
		ArrayList upStreamCIA = jiraProxy.getUpStreamCIARequirements(securityProfile, 1, 51, databaseType);
		// because upStreamCIA needs to be shown in  a nice trace tree format
		// and because it was built going up the chain, we need to reverse it
		// to get it in the right order.
		Collections.reverse(upStreamCIA);
		ArrayList downStreamCIA = jiraProxy.getDownStreamCIARequirements(securityProfile, 1, 51, databaseType);
		
		JSONArray upStreamJSONArray = new JSONArray();
		if (upStreamCIA.size() > 0){
			Iterator i = upStreamCIA.iterator();
			while (i.hasNext()){
				TraceTreeRow traceTreeRow = (TraceTreeRow) i.next();
				Requirement r = traceTreeRow.getRequirement();
				upStreamJSONArray.put(fillRequirementJSON(r));
			}
		}
		jsonRequirement.put("Up Stream Requirements", upStreamJSONArray);
		

		JSONArray downStreamJSONArray = new JSONArray();
		if (downStreamCIA.size() > 0){
			Iterator i = downStreamCIA.iterator();
			while (i.hasNext()){
				TraceTreeRow traceTreeRow = (TraceTreeRow) i.next();
				Requirement r = traceTreeRow.getRequirement();
				downStreamJSONArray.put(fillRequirementJSON(r));
			}
		}
		jsonRequirement.put("Down Stream Requirements", downStreamJSONArray);
		
		
		return (jsonRequirement);
	}
	
	
	/////////////////////////////////////
	// These are the Dip T functions  ///
	/////////////////////////////////////


	 public static void createUser(HttpServletRequest request, PrintWriter out,  String databaseType){
		try {
			
			
			String ldapUserId = "";
			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");
			String emailId = request.getParameter("emailId");
			String password = request.getParameter("password");
			String petsName = "DipT";
			String heardAboutTraceCloud = "DipT";
			
			
			boolean exists = UserAccountUtil.userExistsInTraceCloud(emailId);
			if (exists){
				//this means that the email is already taken. i.e another user already exists with this email id.
				// lets return the message that this user id is already taken
				

				JSONObject json = new JSONObject();
				json.put("responseStatus", "fail");
				json.put("errorMessage", "EmailId is already used"  );			
				out.print(json.toString(3));

			}
			else{
				
				// just to ensure that we don't clone some one's prod project as a default sample project
				// we check for 3 values. project id, prefix, adn created By.
				int sampleProjectId = 0;
				String sampleProjectPrefix = "";
				String sampleProjectCreatedBy =  "";
				
				String installationType = "onSite";
				String authenticationType = "database";
				
				UserAccountUtil.createUser( sampleProjectId, sampleProjectPrefix, sampleProjectCreatedBy, 
 					ldapUserId,firstName,lastName,emailId,password, petsName, heardAboutTraceCloud,
 					installationType, authenticationType, databaseType);
			
				
				JSONObject json = new JSONObject();
				json.put("responseStatus", "success");
				json.put("errorMessage", ""  );			
				out.print(json.toString(3));
	
				
			}

			
						
		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}
	 	 


	 // returns a void
	 // however creates a Requierment and prints a JSON object that lists 
	 // information about the newly created Requirement
	 public static void createTask(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			
			
			String taskTitle = request.getParameter("taskTitle");
			String taskDescription = request.getParameter("taskDescription");
			String createdBy = securityProfile.getUser().getEmailId();
			String owner = request.getParameter("owner");
			String completionString = request.getParameter("completion");
			int completion = 0;
			try {
				completion = Integer.parseInt(completionString);
			}
			catch (Exception e){
				e.printStackTrace();
			}
			String priority = request.getParameter("priority");
			String tags = request.getParameter("tags");
			String completionRequiredDtString = request.getParameter("completion_required_dt");
			
			java.sql.Date completionRequiredDt = null ;
			try {
				
				DateFormat  formatter = new SimpleDateFormat("MM/dd/yy"); ; 
				java.util.Date tempDate = formatter.parse(completionRequiredDtString);
				completionRequiredDt = new java.sql.Date(tempDate.getTime());
			}
			catch (Exception e){
				e.printStackTrace();
			}
			String stakeholders = request.getParameter("stakeholders");
			String workStatus = request.getParameter("work_status");
			
			
			
			DT_Task newTask = new DT_Task(taskTitle, taskDescription, createdBy,  owner, 
					completion , priority, tags, 
					completionRequiredDt, stakeholders, workStatus);
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );			
			JSONObject jsonTask = fillTaskJSON(newTask);
			json.put("newTask", jsonTask);

			out.print(json.toString(3));
			
		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}
	 	 
	 
	
	 public static void updateTask(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			
			
			int taskId = Integer.parseInt(request.getParameter("taskId"));
			String taskTitle = request.getParameter("taskTitle");
			String taskDescription = request.getParameter("taskDescription");
			String createdBy = securityProfile.getUser().getEmailId();
			String owner = request.getParameter("owner");
			String completionString = request.getParameter("completion");
			int completion = 0;
			try {
				completion = Integer.parseInt(completionString);
			}
			catch (Exception e){
				e.printStackTrace();
			}
			String priority = request.getParameter("priority");
			String tags = request.getParameter("tags");
			String completionRequiredDtString = request.getParameter("completion_required_dt");
			
			java.sql.Date completionRequiredDt = null ;
			try {
				
				DateFormat  formatter = new SimpleDateFormat("MM/dd/yy"); ; 
				java.util.Date tempDate = formatter.parse(completionRequiredDtString);
				completionRequiredDt = new java.sql.Date(tempDate.getTime());
			}
			catch (Exception e){
				e.printStackTrace();
			}
			String stakeholders = request.getParameter("stakeholders");
			String workStatus = request.getParameter("work_status");
			
			
			
			DT_Utils.update_DT_Task(taskId, taskTitle, taskDescription, createdBy,  owner, 
					completion , priority, tags, 
					completionRequiredDt, stakeholders, workStatus);
			
			DT_Task task = new DT_Task(taskId);
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );			
			JSONObject jsonTask = fillTaskJSON(task);
			json.put("newTask", jsonTask);

			out.print(json.toString(3));
			
		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}
	 
	 

	 // returns a void
	 // however prints a JSON object that lists all the Requirements in a a Requirement Type 
	 public static void getTasks(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );

			
			String createdBy = request.getParameter("createdBy");
			if (createdBy == null){
				createdBy = "";
			}
			
			String owner = request.getParameter("owner");
			if (owner == null){
				owner = "";
			}
			
			
			String priority = request.getParameter("priority");
			if (priority == null){
				priority = "";
			}
			
			int completion = -1;
			try {
				completion = Integer.parseInt(request.getParameter("completion"));
			}
			catch (Exception e){
				e.printStackTrace();
			}
			
			
			String tags = request.getParameter("tags");
			if (tags == null){
				tags = "";
			}
			

			String work_status = request.getParameter("work_status");
			if (work_status == null){
				work_status = "";
			}

			String stakeholders = request.getParameter("stakeholders");
			if (stakeholders == null){
				stakeholders = "";
			}
			

			String sort_by = request.getParameter("sort_by");
			if (sort_by == null){
				sort_by = " completion_required_date , priority asc";
			}
			
			
			ArrayList tasks = DT_Utils.getTasks(securityProfile, createdBy, owner, priority, 
					completion, tags, work_status , stakeholders,
					sort_by);
			
			
			// lets iterate through all the tasks and create an array of tasks in JSON
			JSONArray jsonTasks = new JSONArray();
			Iterator t = tasks.iterator();
			while (t.hasNext()) {
				DT_Task task = (DT_Task) t.next();
			 	
				//lets ignore the completed tasks
				if (task.getCompletion() == 100 ){
					continue;
				}
				JSONObject jsonTask = fillTaskJSON(task);
				jsonTasks.put(jsonTask);

				
			}
			json.put("tasks", jsonTasks);

			
			out.print(json.toString(3));

		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}	 



	 // returns a void
	 // however prints a JSON object that lists all the Requirements in a a Requirement Type 
	 public static void getValues(HttpServletRequest request, PrintWriter out, String key,
		SecurityProfile securityProfile, String databaseType){
		try {
			
			JSONObject json = new JSONObject();
			json.put("responseStatus", "success");
			json.put("errorMessage", ""  );

			
			String type = request.getParameter("type");
			
			ArrayList values = DT_Utils.getValues(securityProfile, type);
			
			
			// lets iterate through all the tasks and create an array of tasks in JSON
			JSONArray jsonTasks = new JSONArray();
			Iterator v = values.iterator();
			while (v.hasNext()) {
				String value = (String) v.next();
			 	jsonTasks.put(value);

				
			}
			json.put("values", jsonTasks);

			
			out.print(json.toString(3));

		} catch (Exception e) {					
				e.printStackTrace();
				JSONObject json = new JSONObject();
				json.put("responseStatus", "error");
				json.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");
				out.print(json.toString(3));					
		}   finally {

		}
	}	 
	

	 // fillRequirementJSON. Takes a Requirement object and gets a JSON object of it.
	public static JSONObject fillTaskJSON(DT_Task task ){
		JSONObject jsonTask = new JSONObject();
		try {
			
			jsonTask.put("id", task.getId() );
			jsonTask.put("title", task.getTitle() );
			jsonTask.put("description", task.getDescription() );
			jsonTask.put("createdBy", task.getCreatedBy());
			
			try {
			jsonTask.put("createdOn", task.getCreatedOn().toString());
			}
			catch (Exception e){
				e.printStackTrace();
			}
			jsonTask.put("owner", task.getOwner());
			jsonTask.put("completion", task.getCompletion());
			jsonTask.put("priority", task.getPriority() );
			
			jsonTask.put("tags", task.getTags() );
			try {
				jsonTask.put("completionRequiredDtae", task.getCompletionRequiredDt().toString());
			}
			catch (Exception e){
				e.printStackTrace();
			}
			jsonTask.put("stakeholders", task.getStakeholders());
			jsonTask.put("workStatus", task.getWorkStatus());
			
				
			System.out.println("putting title in jsonTask" + task.getTitle());
		} catch (Exception e) {					
				e.printStackTrace();
				jsonTask.put("responseStatus", "error");
				jsonTask.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");					
		}   finally {

		}
		return (jsonTask);
	}


	
	// fillTraceJSON. Takes a Trace object and gets a JSON object of it.
	public static JSONObject fillTraceToJSON(Trace trace, String databaseType){
		JSONObject jsonTrace = new JSONObject();
		try {
			jsonTrace.put("traceId", trace.getId());
			jsonTrace.put("traceDescription", trace.getDescription());
			jsonTrace.put("traceToRequirementId", trace.getToRequirementId());
			jsonTrace.put("traceFromRequirementId", trace.getFromRequirementId());
			jsonTrace.put("suspect", trace.getSuspect());
			
			Requirement toRequirement = new Requirement(trace.getToRequirementId(),  databaseType);
			JSONObject jsonRequirement = fillRequirementJSON(toRequirement);
			jsonTrace.put("requirementDetails", jsonRequirement);
			
		} catch (Exception e) {					
				e.printStackTrace();
				jsonTrace.put("responseStatus", "error");
				jsonTrace.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");					
		}   finally {

		}
		return (jsonTrace);
	}
	
	 // fillTraceJSON. Takes a Trace object and gets a JSON object of it.
	public static JSONObject fillTraceFromJSON(Trace trace, String databaseType){
		JSONObject jsonTrace = new JSONObject();
		try {
			jsonTrace.put("traceId", trace.getId());
			jsonTrace.put("traceDescription", trace.getDescription());
			jsonTrace.put("traceToRequirementId", trace.getToRequirementId());
			jsonTrace.put("traceFromRequirementId", trace.getFromRequirementId());
			jsonTrace.put("suspect", trace.getSuspect());
			
			Requirement fromRequirement = new Requirement(trace.getFromRequirementId(),  databaseType);
			JSONObject jsonRequirement = fillRequirementJSON(fromRequirement);
			jsonTrace.put("requirementDetails", jsonRequirement);
			
		} catch (Exception e) {					
				e.printStackTrace();
				jsonTrace.put("responseStatus", "error");
				jsonTrace.put("errorMessage", "An unknown error has occurred. Please reach out to the TraceCloud administrator");					
		}   finally {

		}
		return (jsonTrace);
	}
}
