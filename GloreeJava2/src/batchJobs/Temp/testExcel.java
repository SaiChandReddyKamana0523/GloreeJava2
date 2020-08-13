package batchJobs.Temp;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.gloree.beans.Project;


import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class testExcel {

	public static void main(String[] args) {
	

			String seedFilePath = "C:/Users/Sreenath2/Desktop/TrendData.xlsx";
			String outputFilePath = "C:/Users/Sreenath2/Desktop/tempTrendData.xlsx";
			
			try {

				// make  a copy of the seed file
				File seedFile = new File (seedFilePath);
				File outputFile = new File (outputFilePath);
				FileUtils.copyFile(seedFile, outputFile);
				
				// now lets get the data we need. 
	            java.sql.Connection con = null;

	    		String userName = "gloree";
	    		String password = "Sunm1cr2";
	    	
		       String url = "jdbc:mysql://localhost/gloree";
		       Class.forName ("com.mysql.jdbc.Driver").newInstance ();
		       con = DriverManager.getConnection (url, userName, password);
		       System.out.println ("Database connection established");
			    
				String sql = " select date_format(data_load_dt, '%d %M %Y') 'dataLoadDt' " +
						" from gr_project_metrics " +
						" where project_id = 19 " +
						" order by data_load_dt asc";
								
				
				PreparedStatement prepStmt = con.prepareStatement(sql);
				ResultSet rs = prepStmt.executeQuery();
				

			
				// lets open the excel file
				InputStream inp = new FileInputStream(outputFilePath);
				
			    Workbook wb = WorkbookFactory.create(inp);
			    // this is for All Requirements.
			    Sheet sheet = wb.getSheetAt(0);
			
			    // Iterate through the db results and print date column
			    int rowNum = 0;
				while (rs.next()) {
					String dataLoadDt = rs.getString("dataLoadDt");
					//String reqType = rs.getString("requirement_type_short_name");
					//int allRequirements = rs.getInt("num_of_requirements");
					++rowNum;
					
					Row row = sheet.getRow(rowNum);
				    if (row == null){
				    	row = sheet.createRow(rowNum);
				    }
				    
					Cell cell = row.getCell(0);
				    if (cell == null){
				    	cell = row.createCell(0);
				    }
					cell.setCellValue(dataLoadDt);
				}
				
				// lets get all req types in this project and then iterate through them, printing a column for each req type
				sql = "select short_name from  gr_requirement_types where project_id = 19";
				prepStmt = con.prepareStatement(sql);
				rs   = prepStmt.executeQuery();
				int colNum = 0;
				
				while (rs.next()){
					// Iterate through the db results and print the metrics for this req type
					colNum++;
					
					String reqType = rs.getString("short_name");
					// set data column heading to req type.
					Row row = sheet.getRow(0);
				    if (row == null){
				    	row = sheet.createRow(rowNum);
				    }
				    
					Cell cell = row.getCell(colNum);
				    if (cell == null){
				    	cell = row.createCell(colNum);
				    }
					cell.setCellValue(reqType);
					
					
					
					
					
					sql = " select num_of_requirements " +
							" from gr_project_metrics " +
							" where project_id = 19 " +
							" and requirement_type_short_name = ?" +
							" order by data_load_dt asc";
									
					
					PreparedStatement prepStmt2 = con.prepareStatement(sql);
					prepStmt2.setString(1, reqType);
					ResultSet rs2 = prepStmt2.executeQuery();
					rowNum = 0;
					while (rs2.next()){
							
						int allRequirements = rs2.getInt("num_of_requirements");
						++rowNum;
						
						row = sheet.getRow(rowNum);
					    if (row == null){
					    	row = sheet.createRow(rowNum);
					    }
					    
					    cell = row.getCell(colNum);
					    if (cell == null){
					    	cell = row.createCell(colNum);
					    }
					    cell.setCellValue(allRequirements);
					}
					
					
					
				}

				
				// clean  up filler data.
				// we have put in some filler data in the seed file. 
				// lets iterate rows from 0 to 
				for (int i = 0; i< 100; i++){
					Row row = sheet.getRow(i);
				    if (row == null){
				    	continue;
				    }
				    
				    if (i == 0 ){
				    	// for first row, lets check up to 15 columns.
					    for (int j = 0; j< 15; j++){
						    Cell cell = row.getCell(j);
						    if (cell == null){
						    	continue;
						    }
						    
					    	// we are in the header row, and this might have a 'Filler' in the value
					    	try {
					    		
						    	if (cell.getStringCellValue().equals("Filler")){
						    
						    		row.removeCell(cell);
						    	}
					    	}
					    	catch (Exception e){
					    		e.printStackTrace();
					    	}
					    }
					}
				    else {
				    	// for every other row, see if column 0 has date
				    	for (int j = 0; j< 15; j++){
						    Cell cell = row.getCell(j);
						    if (cell == null){
						    	continue;
						    }
						    
					    	// we are in the header row, and this might have a 'Filler' in the value
					    	try {
					    		
						    	if (cell.getNumericCellValue() == -1 ){
						    
						    		row.removeCell(cell);
						    	}
					    	}
					    	catch (Exception e){
					    		e.printStackTrace();
					    	}
					    }

				    }
				    
				}
				
				 // write output and close the file
			    FileOutputStream fileOut = new FileOutputStream(outputFilePath);
			    wb.write(fileOut);
			    fileOut.close();

				    
				    
				/*
			    
	            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(excelFilePath));
	            HSSFWorkbook wb = new HSSFWorkbook(fs);
	            HSSFSheet sheet = wb.getSheet("trySheet");
	            
	            
	            java.sql.Connection con = null;

	    		String userName = "gloree";
	    		String password = "Sunm1cr2";
	    	
			       String url = "jdbc:mysql://localhost/gloree";
			       Class.forName ("com.mysql.jdbc.Driver").newInstance ();
			       con = DriverManager.getConnection (url, userName, password);
			       System.out.println ("Database connection established");
				    
					String sql = "select  p.name 'projectName' , rt.name 'reqTypeName' ,  count(*) 'countOfRows' " + 
					" from gr_requirements r, gr_requirement_types rt, gr_projects p" +
					" where p.id = rt.project_id " +
					" and p.name not like '100%' " +
					" and r.requirement_type_id = rt.id " +
					" group by p.name, rt.name" +
					" order by 1,2 " ; 
					
					PreparedStatement prepStmt = con.prepareStatement(sql);
					ResultSet rs = prepStmt.executeQuery();
					int rowNum = 0;
				    while (rs.next()) {
				    	if (rowNum > 20){
				    		continue;
				    	}
				    	String projectName = rs.getString("projectName");
				    	String reqTypeName = rs.getString("reqTypeName");
				    	int countOfRows = rs.getInt("countOfRows");
				    	rowNum++;
				    	
				    	HSSFRow row = sheet.getRow(rowNum);
				   		
				    	HSSFCell projectNameCell = row.createCell(0);
				   		projectNameCell.setCellValue(new HSSFRichTextString (projectName));
			    		
				   		HSSFCell reqTypeCell = row.createCell(1);
				   		reqTypeCell.setCellValue(reqTypeName);
				    	
				   		HSSFCell countOfRowsCell = row.createCell(2);
				   		countOfRowsCell.setCellValue(countOfRows);
				    	
				    }
	            

	            FileOutputStream fileOut = new FileOutputStream(excelFilePath);
	            wb.write(fileOut);
	            fileOut.close(); 
	           
			*/	
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}

			
		
		
	}
	



