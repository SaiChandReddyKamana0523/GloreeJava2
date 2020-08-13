package com.gloree.utils;




import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.naming.InitialContext;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;



public class TNUtil {

	

	
	public static void storeTNFile(String fileCode, String filePath){
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();			
			//
			// This sql inserts a message in to the db so that it can be emailed in bulk later.
			//
			String sql = "";
			
				sql = " insert into tn_files (file_code, file_path, created_dt)  " +
					" values (?,?, now())";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, fileCode);
			prepStmt.setString(2, filePath);
			
			prepStmt.execute();
			
			prepStmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
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
	
	
	
	public static String getTNFilePath(String fileCode){
		
		String filePath = "";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();			
			//
			// This sql inserts a message in to the db so that it can be emailed in bulk later.
			//
			String sql = "";
			
				sql = " select file_path from  tn_files where file_code = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, fileCode);
			
			
			rs = prepStmt.executeQuery();
			while (rs.next()){
				filePath = rs.getString("file_path");
			}
			rs.close();
			prepStmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
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
		
		return(filePath);
		
	}	
	
	
	public static JSONObject getJSONFromExcel(String excelFilePath){
		JSONObject data = new JSONObject() ;


		
		try {
			InputStream myxls = new FileInputStream(excelFilePath);
			HSSFWorkbook wb     = new HSSFWorkbook(myxls);
			
			HSSFSheet sheet = wb.getSheetAt(0);       // first sheet
			HSSFRow row     = sheet.getRow(0);        // first row
			
			JSONArray columns= new JSONArray();
			// we look for up to 100 cells to see if there a value
			HashMap<String, Integer> columnMap = new HashMap<String, Integer>();
			for (int i=0; i<100 ; i++) {
				
				// get Column Heading.
				String columnHeading = "";
				try{
					HSSFCell cell   = row.getCell(i); 
					if (cell != null){
						columnHeading = cell.getStringCellValue();
					}
				}
				catch (Exception e) {
					// if we run into any exception, set columnHeading to empty string.
					System.out.println("srt ran into error in getting colum Header");
					e.printStackTrace();
					columnHeading = "";
				}
				
				if (!columnHeading.equals("")) {
					System.out.println("srt adding column heading " + columnHeading);
					// if this is not an empty cell
					columns.put(columnHeading);
					columnMap.put(columnHeading, i);
				}
			}
			data.put("columns", columns);
			
			
			// lets get the data rows
			JSONArray reqs = new JSONArray();
			
			for (int i=1; i<20000 ; i++) {
				String excelRow = "";
				JSONObject req = new JSONObject() ;
				row     = sheet.getRow(i); 
				
				// lets loop through each element in HashMap columnMap
				Set<String> columnNames = columnMap.keySet();
				for (String columnName:columnNames){
					int columnNumber = columnMap.get(columnName);
					String columnValue = "";
					try{
						HSSFCell cell   = row.getCell(columnNumber);
						System.out.println("srt cell type for column " + columnName + " is " + cell.getCellType());
						if (cell.getCellType() == 0){
							// numeric
							columnValue = Double.toString(cell.getNumericCellValue());
						}
						if (cell.getCellType() == 1){
							// string
							columnValue = cell.getStringCellValue();
						}
						//columnValue = cell.getStringCellValue();
						req.put(columnName.trim(), columnValue.trim());
						excelRow += columnValue;
					}
					catch (Exception e) {
						// if we run into any exception, set columnHeading to empty string.
						System.out.println("srt ran into error in collecting column value " );
						e.printStackTrace();
						columnValue = "";
					}
				}
				
				if (excelRow.equals("")){
					// we hit an empty row in the excel file. Lets exit the loop
					break;
				}
				else {
					// add the req objecct to reqs Array
					reqs.put(req);
				}
				
			}
			data.put("reqs", reqs);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	
}

