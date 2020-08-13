package com.gloree.utils;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.naming.InitialContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpSession;


























import com.aspose.words.Cell;
import com.aspose.words.CellCollection;
import com.aspose.words.CellVerticalAlignment;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.FieldStart;
import com.aspose.words.FieldType;
import com.aspose.words.Font;
import com.aspose.words.HeightRule;
import com.aspose.words.LineStyle;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeList;
import com.aspose.words.NodeType;
import com.aspose.words.Paragraph;
import com.aspose.words.Row;
import com.aspose.words.RowCollection;
import com.aspose.words.Run;
import com.aspose.words.SaveFormat;
import com.aspose.words.Section;
import com.aspose.words.SectionCollection;
import com.aspose.words.Shape;
import com.aspose.words.Table;
import com.aspose.words.TableCollection;
import com.aspose.words.Underline; //import com.gloree.actions.Hyperlink;
import com.gloree.beans.*;
import com.oreilly.servlet.Base64Encoder;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;

public class WordTemplateUtilSN {

	
	public static String makeAPICallToSN(String urlString, String snuser, String snpwd) {
		String apiResponse = "";
		
		 try {

	         URL url = new URL(urlString);
	         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	         conn.setRequestMethod("GET");
	         conn.setRequestProperty("Accept", "application/json");
	         conn.setRequestProperty("Content-Type", "application/json");
	         
	         String userCredentials = snuser + ":" + snpwd;
	         String basicAuth = "Basic " + new String(Base64Encoder.encode(userCredentials.getBytes()));

	         conn.setRequestProperty("Authorization",
	         		basicAuth);
	         

	         if (conn.getResponseCode() != 200) {
	             throw new RuntimeException("Failed : HTTP error code : "
	                     + conn.getResponseCode());
	         }

	         BufferedReader br = new BufferedReader(new InputStreamReader(
	                 (conn.getInputStream())));

	         String output; 
	        
	         while ((output = br.readLine()) != null) {
	        	 apiResponse += output;
	         }

	         conn.disconnect();

	     } catch (Exception e) {
	         e.printStackTrace();
	     } 
		 if (apiResponse.equals("")){
			 apiResponse = "{}";
		 }
		 
		return apiResponse;
	}
	// parses the input string and returns an array list of requirements..
	// NOTE URL STRING can be like
	// "BR-1,BR-2,PR-3..PR-35,REPORTID-246,FS-1"
	// NOTE , we also support BR-2:name etc.. where :xxxx is the attribute name
	public static ArrayList<JSONObject> getRequirementsFromURLStringSN(
			HttpSession session,
			String sNProjectId,
			String urlString) {
		ArrayList<JSONObject> requirements = new ArrayList<JSONObject>();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {



			//System.out.println("srt 4 urlString in getREqsfromurlstring is " + urlString);
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets break the URL string into elements.
			String[] elements = { urlString };
			if (urlString.contains(",")) {
				elements = urlString.split(",");
			}

			// lets get out if elements is null or if elements size is 0
			if (elements == null || elements.length == 0 ){
				// do nothing
			}
			else {
				for (int i = 0; i < elements.length; i++) {
					String element = elements[i];
	
					//System.out.println("srt 5 element " + element);
					try {
						// at this point element can have BR-1 or BR-1..BR-25 or
						// REPORTID-346
						if (element.contains("REPORTID-")) {
							// this is a report.
							try {
								String[] reportInfo = element.split("-");
								String reportId = reportInfo[1];
		
								
								ArrayList<JSONObject> requirementsInRT = getRequirementFromSNByReportId(session, sNProjectId, reportId);
								
								for (JSONObject req:requirementsInRT){
									requirements.add(req);
								}
								continue;
							}
							catch (Exception e){
								e.printStackTrace();
							}
						} else if (element.contains("REQUIREMENTTYPE-")) {
							// this is a report.
							try {

								String[] reportInfo = element.split("-");
								String requirementType = reportInfo[1];
		
								
								ArrayList<JSONObject> requirementsInRT = getRequirementFromSNByRequirementType(session, sNProjectId, requirementType);
								
								for (JSONObject req:requirementsInRT){
									requirements.add(req);
								}
								continue;
							}
							catch (Exception e){
								e.printStackTrace();
							}
						} else if (element.contains("FOLDERPATH-")) {
							// this is a report.
							try {
								
								String[] reportInfo = element.split("-");
								String folderPath = reportInfo[1];
		
								
								ArrayList<JSONObject> requirementsInFolder = getRequirementFromSNByFolderPath(session, sNProjectId, folderPath);
								
								for (JSONObject req:requirementsInFolder){
									requirements.add(req);
								}
								
								continue;
							}
							catch (Exception e){
								e.printStackTrace();
							}
						} else if (element.contains("..")) {
							try {
							
								/*
								// lets get the start and end reqs.
								String[] range = element.split("\\.\\.");
								String startReq = range[0];
								String endReq = range[1];
		
								// at this point start req has BR-1 and end req has
								// BR-35
								String startTagReqPrefix = "";
								int startTag = 0;
								String endTagReqPrefix = "";
								int endTag = 0;
		
								if (startReq.contains("-")) {
									String[] startTagArray = startReq.split("-");
									startTagReqPrefix = startTagArray[0];
									startTag = Integer.parseInt(startTagArray[1]);
								}
		
								if (endReq.contains("-")) {
									String[] endTagArray = endReq.split("-");
									endTagReqPrefix = endTagArray[0];
		
									endTag = Integer.parseInt(endTagArray[1]);
								}
		
								// now lets loop from end start tag to end tag, getting
								// all the reqs.
								for (int j = startTag; j <= endTag; j++) {
									String requirementTag = startTagReqPrefix + "-" + j;
									int requirementId = RequirementUtil
											.getRequirementId(projectId, requirementTag);
									if (requirementId > 0) {
										Requirement requirement = new Requirement(
												requirementId,  databaseType);
										// lets discard this req if it has been deleted.
										if (requirement.getDeleted() != 1 ){
											requirements.add(requirement);
										}
										
									}
		
								}
								*/
								continue;
							}
							catch (Exception e){
								e.printStackTrace();
							}
						} else {
							// this is a simple reqId
							ArrayList<JSONObject>  reqsJSON = getRequirementFromSNByReqId(session, sNProjectId, element);

							for (JSONObject req:reqsJSON){
								requirements.add(req);
							}
						}
					} catch (Exception e) {
						// do nothing.
						e.printStackTrace();
					}
	
				}
			}
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
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
		return requirements;
	}


	public static ArrayList<JSONObject> getRequirementFromSNByReqId(HttpSession session,  String sNProjectId, String reqId){
		
		

		ArrayList<JSONObject> reqs = new ArrayList<JSONObject>();
		
		String instance = (String) session.getAttribute("instance");
		String snuser = (String) session.getAttribute("snuser");
		String snpwd = (String) session.getAttribute("snpwd");
		
		
		 String req = "";
		
		 try {

			 String urlString = instance + "/api/x_tracl_tracecloud/getrequirement?projectId=" + sNProjectId + "&reqFullTag=" + reqId;
	         URL url = new URL(urlString);
	    

	 		//System.out.println("srt url is " + urlString );
	         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	         conn.setRequestMethod("GET");
	         conn.setRequestProperty("Accept", "application/json");
	         conn.setRequestProperty("Content-Type", "application/json");
	         
	         String userCredentials = snuser + ":" + snpwd;
	         String basicAuth = "Basic " + new String(Base64Encoder.encode(userCredentials.getBytes()));

	         conn.setRequestProperty("Authorization",
	         		basicAuth);
	         

	         if (conn.getResponseCode() != 200) {
	             throw new RuntimeException("Failed : HTTP error code : "
	                     + conn.getResponseCode());
	         }

	         BufferedReader br = new BufferedReader(new InputStreamReader(
	                 (conn.getInputStream())));

	         String output; 
	        
	         while ((output = br.readLine()) != null) {
	             req += output;
	         }

	         conn.disconnect();

	     } catch (Exception e) {

	         e.printStackTrace();

	     } 
	    
		
		 if (req.equals("")){
			 req = "{}";
		 }
		 
		// System.out.println("srt response from SN for reqId  is " + req);
		 
		 
		 JSONArray projectArray  = new JSONArray();
		try{
			JSONObject reqJSON = new JSONObject(req);	
			reqs.add(reqJSON);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		
		 return(reqs);
	}

	public static ArrayList<JSONObject> getRequirementFromSNByFolderPath(HttpSession session,  String sNProjectId, String folderPath){
		
		
		String instance = (String) session.getAttribute("instance");
		String snuser = (String) session.getAttribute("snuser");
		String snpwd = (String) session.getAttribute("snpwd");
		
		JSONObject reqArrayObject = new JSONObject();
		ArrayList<JSONObject> reqs = new ArrayList<JSONObject>();
	    String responseString = "";
		 try {

			 folderPath =   URLEncoder.encode(folderPath,"UTF-8");
			 //https://ven02634.service-now.com/api/x_tracl_tracecloud/getrequirementsinfolder?projectId=499bedbbdb18270061e7034b8a961931&folderPath=Business%20Requirements
			 String urlString = instance + "/api/x_tracl_tracecloud/getrequirementsinfolder?projectId=" + sNProjectId + "&folderPath=" + folderPath;
			// urlString =  URLEncoder.encode(urlString,"UTF-8");
			 URL url = new URL(urlString);
	    

	 	     HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	         conn.setRequestMethod("GET");
	         conn.setRequestProperty("Accept", "application/json");
	         conn.setRequestProperty("Content-Type", "application/json");
	         
	         String userCredentials = snuser + ":" + snpwd;
	         String basicAuth = "Basic " + new String(Base64Encoder.encode(userCredentials.getBytes()));

	         conn.setRequestProperty("Authorization",     		basicAuth);
	         

	         if (conn.getResponseCode() != 200) {
	             throw new RuntimeException("Failed : HTTP error code : "
	                     + conn.getResponseCode());
	         }

	         BufferedReader br = new BufferedReader(new InputStreamReader(
	                 (conn.getInputStream())));

	         String output; 
	     
	        
	         while ((output = br.readLine()) != null) {
	        	 responseString += output;
	         }

	         conn.disconnect();

	     } catch (Exception e) {

	    	 System.out.println("srt ran into exception");
	    	 
	         e.printStackTrace();

	     } 
	    
		 if (responseString.equals("")){
			 responseString = "{}";
		 }
		 
		 

		try{
			JSONObject responseObject = new JSONObject(responseString);
			JSONObject resultObject = responseObject.getJSONObject("result");
			
			JSONArray reqFromSN = resultObject.getJSONArray("reqs");	
			for (int i = 0; i < reqFromSN.length(); i++) {
				 
	            JSONObject reqJSON = reqFromSN.getJSONObject(i);
				
	            reqs.add(reqJSON);
			}
			
			 
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		
		 return(reqs);
	}
	
public static ArrayList<JSONObject> getRequirementFromSNByRequirementType(HttpSession session,  String sNProjectId, String requirementType){
		
		
		String instance = (String) session.getAttribute("instance");
		String snuser = (String) session.getAttribute("snuser");
		String snpwd = (String) session.getAttribute("snpwd");
		
		JSONObject reqArrayObject = new JSONObject();
		ArrayList<JSONObject> reqs = new ArrayList<JSONObject>();
	    String responseString = "";
		 try {

			 requirementType =   URLEncoder.encode(requirementType,"UTF-8");
			 //https://ven02634.service-now.com/api/x_tracl_tracecloud/getrequirementsinfolder?projectId=499bedbbdb18270061e7034b8a961931&folderPath=Business%20Requirements
			 String urlString = instance + "/api/x_tracl_tracecloud/getrequirementsinreqtype?projectId=" + sNProjectId + "&reqTypeName=" + requirementType;
			// urlString =  URLEncoder.encode(urlString,"UTF-8");
			 URL url = new URL(urlString);
	    

	 	     HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	         conn.setRequestMethod("GET");
	         conn.setRequestProperty("Accept", "application/json");
	         conn.setRequestProperty("Content-Type", "application/json");
	         
	         String userCredentials = snuser + ":" + snpwd;
	         String basicAuth = "Basic " + new String(Base64Encoder.encode(userCredentials.getBytes()));

	         conn.setRequestProperty("Authorization",     		basicAuth);
	         

	         if (conn.getResponseCode() != 200) {
	             throw new RuntimeException("Failed : HTTP error code : "
	                     + conn.getResponseCode());
	         }

	         BufferedReader br = new BufferedReader(new InputStreamReader(
	                 (conn.getInputStream())));

	         String output; 
	     
	        
	         while ((output = br.readLine()) != null) {
	        	 responseString += output;
	         }

	         conn.disconnect();

	     } catch (Exception e) {

	    	 System.out.println("srt ran into exception");
	    	 
	         e.printStackTrace();

	     } 
	    
		 if (responseString.equals("")){
			 responseString = "{}";
		 }
		 
		 

		try{
			JSONObject responseObject = new JSONObject(responseString);
			JSONObject resultObject = responseObject.getJSONObject("result");
			
			JSONArray reqFromSN = resultObject.getJSONArray("reqs");	
			for (int i = 0; i < reqFromSN.length(); i++) {
				 
	            JSONObject reqJSON = reqFromSN.getJSONObject(i);
				
	            reqs.add(reqJSON);
			}
			
			 
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		
		 return(reqs);
	}


public static ArrayList<JSONObject> getRequirementFromSNByReportId(HttpSession session,  String sNProjectId, String reportId){
	
	
	String instance = (String) session.getAttribute("instance");
	String snuser = (String) session.getAttribute("snuser");
	String snpwd = (String) session.getAttribute("snpwd");
	
	JSONObject reqArrayObject = new JSONObject();
	ArrayList<JSONObject> reqs = new ArrayList<JSONObject>();
    String responseString = "";
	 try {

		
		 //https://ven02634.service-now.com/api/x_tracl_tracecloud/getrequirementsinfolder?projectId=499bedbbdb18270061e7034b8a961931&folderPath=Business%20Requirements
		 String urlString = instance + "/api/x_tracl_tracecloud/getrequirementsinreport?projectId=" + sNProjectId + "&reportId=" + reportId;
		// urlString =  URLEncoder.encode(urlString,"UTF-8");
		 URL url = new URL(urlString);
    

 	     HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("GET");
         conn.setRequestProperty("Accept", "application/json");
         conn.setRequestProperty("Content-Type", "application/json");
         
         String userCredentials = snuser + ":" + snpwd;
         String basicAuth = "Basic " + new String(Base64Encoder.encode(userCredentials.getBytes()));

         conn.setRequestProperty("Authorization",     		basicAuth);
         

         if (conn.getResponseCode() != 200) {
             throw new RuntimeException("Failed : HTTP error code : "
                     + conn.getResponseCode());
         }

         BufferedReader br = new BufferedReader(new InputStreamReader(
                 (conn.getInputStream())));

         String output; 
     
        
         while ((output = br.readLine()) != null) {
        	 responseString += output;
         }

         conn.disconnect();

     } catch (Exception e) {

    	 System.out.println("srt ran into exception");
    	 
         e.printStackTrace();

     } 
    
	 if (responseString.equals("")){
		 responseString = "{}";
	 }
	 
	 

	try{
		JSONObject responseObject = new JSONObject(responseString);
		JSONObject resultObject = responseObject.getJSONObject("result");
		
		JSONArray reqFromSN = resultObject.getJSONArray("reqs");	
		for (int i = 0; i < reqFromSN.length(); i++) {
			 
            JSONObject reqJSON = reqFromSN.getJSONObject(i);
			
            reqs.add(reqJSON);
		}
		
		 
	}
	catch (Exception e){
		e.printStackTrace();
	}
	
	
	 return(reqs);
}

	public static Report getReportFromURLString(SecurityProfile securityProfile, int projectId,
			String urlString, String databaseType) {
		
		Report report = null;
		try {
			
			// lets break the URL string into elements.
			String[] elements = { urlString };
			if (urlString.contains(",")) {
				elements = urlString.split(",");
			}

			for (int i = 0; i < elements.length; i++) {
				String element = elements[i];

				try {
					// at this point element can have BR-1 or BR-1..BR-25 or
					// REPORTID-346
					if (element.contains("REPORTID-")) {
						// this is a report.
						String[] reportInfo = element.split("-");
						int reportId = Integer.parseInt(reportInfo[1]);
						report = new Report(reportId);
											} 
				} catch (Exception e) {
					e.printStackTrace();
					// do nothing.
				}

			}
		
		} catch (Exception e) {
			
			e.printStackTrace();
		} 
		return report;
	}

	
	
	
	
	
	

	public static String generateReqPerTableReportSN(HttpSession session, SecurityProfile securityProfile, int templateId, 
			String selectedDisplayAttributes, User user, HttpServletRequest request,
			HttpServletResponse response,String rootDataDirectory, String exportType, String databaseType,
			int maxColumnsOfExcelToEmbedInWord) {
		String filename = "";
		try {
			
			String instance = (String) session.getAttribute("instance");
			
			System.out.println("TM : inside generateReqPerTableReportSN Report  - starting : for projectId " );
				
			WordTemplateSN wordTemplateSN = new WordTemplateSN(templateId);
			
			Document doc = new Document(wordTemplateSN.getTemplateFilePath());
			DocumentBuilder builder = new DocumentBuilder(doc);

			
			/*
			// Lets go through the doc and find all instances of req: start
			// Get all runs from the document.
			NodeCollection paragraphs = doc.getChildNodes(NodeType.PARAGRAPH,true);
			Iterator p = paragraphs.iterator();
			while (p.hasNext()) {
				Paragraph paragraph = (Paragraph) p.next();
				// lets see if the paragraph string contains the search words.
				String paragraphText = paragraph.getRange().getText();
				if ((paragraphText != null) && !(paragraphText.equals("")) && paragraphText.toLowerCase().contains("req:")) {
					// this para has some text. so lets go forward.
					
					for (int k = 0 ; k < 10 ; k++){
						builder.writeln();
						paragraphText = paragraphText.replace("req:", "newly replaced " +  k);
						builder.write(paragraphText);
					}
				}
			}
			// Lets go through the doc and find all instances of req: start
			*/
			///////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////
			//
			// SRT
			// 
			// THIS WILL WORK : SEE THE createRequirementsFromWordTemplateByStyleUpdatable code
			// this will show you how you can read a doc with all the text of a particular style
			// find a way to insert the run multiple times 
			// Then find if the run text has any req: and parse them and replace them with apropriate code
			//
			//
			///////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////

			// Hyperlinks in a Word documents are fields, select all field start
			// nodes so we can find the hyperlinks.
			NodeList fieldStarts = doc.selectNodes("//FieldStart");
			Iterator fs = fieldStarts.iterator();
			

			System.out.println("TM : inside generateReqPerTabe Report  - going through nodes " );
			
			while (fs.hasNext()) {
				String displayAttributes = selectedDisplayAttributes;
				
				Node node = (Node) fs.next();
				FieldStart fieldStart = (FieldStart) node;
				
				if (fieldStart.getFieldType() == FieldType.FIELD_HYPERLINK) {
					Hyperlink hyperlink = null;
					try {
						hyperlink = new Hyperlink(fieldStart);
					}
					catch (Exception e){
						// since we hit an exception, lets ignore this link
						e.printStackTrace();
						System.out.println("srt hit an exception in making a hyperlink, so we will skip this one" );
						continue;
					}
					
					if (hyperlink == null){
						continue;
					}
					String urlString = hyperlink.getName();
					// NOTE : for some reason we are getting a weird character (Paragraph)
					// or DC4 when viewed in Notepad++
					// so if the length is > 0, then lets drop the first char.
					if ((urlString != null)&& (urlString.length() > 1)){
						urlString = urlString.substring(1);
						urlString = java.net.URLDecoder.decode(urlString,"UTF-8");
					}
					
					
					// urlString contains the information we need to parse and
					// build out.
					if (urlString == null) {
						// if urlString is null, skip this row.
						continue;
					}
					
					
					ArrayList<JSONObject> requirements = new ArrayList<JSONObject>();
					try {
						//requirements = WordTemplateUtilSN.getRequirementsFromURLStringSN(session, wordTemplateSN.getSNProjectId(), urlString);
					}
					catch (Exception e){
						e.printStackTrace();
					}
					
					
					if (requirements.size() == 0) {
						// no requirements were found for this hyperlink.
						// so lets skip this one.
						continue;
					}

					
					builder.moveTo(node);
					
					// if this hyperlink had led to any requirements being generated, lets drop the link
					if (requirements.size() > 0 ){
						//hyperlink.setName(" ");
						//hyperlink.setTarget(" ");
					}
					
					
					
						
					Font font = builder.getFont();
					Iterator i = requirements.iterator();
					
					while (i.hasNext()) {
						JSONObject r = (JSONObject) i.next();
						System.out.println("srt JSONObject is " + r.toString());
						
						String fullTag = "";
						try{
							fullTag = r.getString("full_tag");
						}
						catch (Exception f){
							//f.printStackTrace();
						}
						
						if (!(fullTag.equals(""))){

							System.out.println("srt displayAttributes is " + displayAttributes);
							try {
								
								//String fullTag = r.getString("full_tag");
								font = builder.getFont();							
								builder.startTable();
	
								String name = "";
								try{
									name = r.getString("name");
								}
								catch (Exception d){}
								
								builder.insertCell();
								cleanUpFont(font);
								setUpCell(builder, 100);
								builder.write("Req Id");
								builder.insertCell();
								cleanUpFont(font);
								setUpCell(builder, 400);
								String reqSysId = r.getString("sys_id");
								String url =   instance + "/x_tracl_tracecloud_RequirementPage.do?sys_id=" + reqSysId ;
								font.setColor(Color.BLUE);
								font.setUnderline(1);
								builder.insertHyperlink(fullTag, url, false);
								
								cleanUpFont(font);
								builder.write("   (V - " + r.getString("version") + ") ");
								builder.write(" Completion: " + r.getString("completion") + "%");
								builder.endRow();
								
								
								if (displayAttributes.contains("name")){
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 100);
									builder.write("Name");
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 400);
									builder.write(name);
									builder.endRow();
								}
								
		
								if (displayAttributes.contains("description")){
									String details = "";
									try{
										details = r.getString("details");
									}
									catch (Exception d){}
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 100);
									builder.write("Details");
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 400);
									builder.insertHtml(java.net.URLDecoder.decode(details,"UTF-8")); 
									builder.endRow();
								}
								
								// Custom Attributes
								if (displayAttributes.contains("customAttributes")){
									try{
									String udaString = r.getString("uda");
									// lets replace / with space.
									udaString = udaString.replace("\\", "");
									
									JSONObject uda =  new JSONObject(udaString);
									JSONArray keys = uda.names();
									for (int j = 0; j < keys.length (); ++j) {
		
									   String key = keys.getString (j); 
									   String value = "";
										try{
										 value = uda.getString (key); 
										}
										catch (Exception d){
											d.printStackTrace();
										}
										
										if (displayAttributes.contains("customAttributesNonEmpty")){
											if (value.equals("")){
												continue;
											}
										}		
									   
									   builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 100);
										builder.write(key);
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 400);
										builder.insertHtml(java.net.URLDecoder.decode(value,"UTF-8")); 
										//builder.insertHtml(r.getString("details")); 
										builder.endRow();
		
									}
									}
									catch (Exception g){
										g.printStackTrace();
									}
								}
								
								// lets try to print the selected custom attributes.
								if (displayAttributes.contains("attribute=")){
									try{
										// lets get the uda object 
										String udaString = r.getString("uda");
										// lets replace / with space.
										udaString = udaString.replace("\\", "");
										
										JSONObject uda =  new JSONObject(udaString);
										JSONArray keys = uda.names();
										
										// lets get the attribute name that we need to process and print. 
										String[] attributeStrings = displayAttributes.split(",");
										for (String aS : attributeStrings){
											if (aS.contains("attribute=")){
												String attributeLabel = aS.replace("attribute=", "").trim();
												// now that we have the attribute label that was sent it, lets try to print it.
												for (int j = 0; j < keys.length (); ++j) {
													
													   String key = keys.getString (j); 
													   String value = "";
														try{
														 value = uda.getString (key); 
														}
														catch (Exception d){
															d.printStackTrace();
														}
														
																
													   if (key.equals(attributeLabel)){
														   builder.insertCell();
															cleanUpFont(font);
															setUpCell(builder, 100);
															builder.write(key);
															builder.insertCell();
															cleanUpFont(font);
															setUpCell(builder, 400);
															builder.insertHtml(java.net.URLDecoder.decode(value,"UTF-8")); 
															//builder.insertHtml(r.getString("details")); 
															builder.endRow();
													   }
						
													}
											}
										}
									
									
									
									}
									catch (Exception g){
										g.printStackTrace();
									}
								}
								if (displayAttributes.contains("owner")){
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 100);
									builder.write("Owner");
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 400);
									builder.write(r.getString("owner"));
									builder.endRow();
								}

								
								if (displayAttributes.contains("traceTo")){
									
									// lets get all the tracetoObjects.
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 100);
									builder.write("Traces To (Up) ");
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 400);
									try{
										String tTString = r.getString("trace_to_objects");
										JSONArray tTO = new JSONArray(tTString);
										
										
										for (int j = 0; j < tTO.length(); j++) {
										    
								            JSONObject t = tTO.getJSONObject(j);
										    String status = t.getString("Status");
										    
										    String tReqSysId =  t.getString("reqSysId");
											url =   instance + "/x_tracl_tracecloud_RequirementPage.do?sys_id=" + tReqSysId ;
											String tName =  t.getString("fulltag") + " : " +  t.getString("name");
											if (status.equals("Suspect")){
												font.setColor(Color.RED);
												builder.write("(Suspect) ");
												cleanUpFont(font);
											}
											else {
												font.setColor(Color.GREEN);
												builder.write("(Clear) ");
												cleanUpFont(font);
											}
											font.setUnderline(1);
											builder.insertHyperlink(tName, url, false);
											builder.writeln();
											
										}
										
									}
									catch(Exception traceToException){
										traceToException.printStackTrace();
									}
									builder.endRow();
								}	
								
								if (displayAttributes.contains("tracesFrom")){
									
									// lets get all the tracetoObjects.
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 100);
									builder.write("Traces From (Down) ");
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 400);
									try{
										String tFString = r.getString("trace_from_objects");
										JSONArray tFO = new JSONArray(tFString);
										for (int j = 0; j < tFO.length(); j++) {
										    JSONObject t = tFO.getJSONObject(j);
										    String status = t.getString("Status");
										    
										    String tReqSysId =  t.getString("reqSysId");
											url =   instance + "/x_tracl_tracecloud_RequirementPage.do?sys_id=" + tReqSysId ;
											String tName =  t.getString("fulltag") + " : " +  t.getString("name");
											if (status.equals("Suspect")){
												font.setColor(Color.RED);
												builder.write("(Suspect) ");
												cleanUpFont(font);
											}
											else {
												font.setColor(Color.GREEN);
												builder.write("(Clear) ");
												cleanUpFont(font);
											}
											font.setUnderline(1);
											builder.insertHyperlink(tName, url, false);
											builder.writeln();
										}
										
									}
									catch(Exception traceFromException){
										traceFromException.printStackTrace();
									}
									builder.endRow();
								}
								
								
								
								if (displayAttributes.contains("fileAttachments")){
									// attachments. 
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 100);
									builder.write("Attachments");
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 400);
									try{
										String attachmentsString = r.getString("attachment_details");
										JSONArray attachments = new JSONArray(attachmentsString);
										for (int j = 0; j < attachments.length(); j++) {
										    JSONObject attachment = attachments.getJSONObject(j);
										    builder.write(attachment.getString("attachment_name"));
								            builder.writeln();
										}
									}
									catch(Exception attachmentsE){
										attachmentsE.printStackTrace();
									}
									builder.endRow();
								}
								
								builder.endTable();
								builder.insertParagraph();
								//builder.writeln("");
								
								
							} catch (Exception e) {
								System.out.println("srt ran into error in write of builder");
								e.printStackTrace();
							}
						}
					}
					
					
				}
			}
			

			System.out.println("TM : inside generateReqPerTabe Report  - completed document " );
	
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
			Calendar cal = Calendar.getInstance();
			String today = sdf.format(cal.getTime());
			filename = wordTemplateSN.getTemplateName() + today + ".docx";

			filename.replace(' ', '_');

			

			System.out.println("TM : inside generateReqPerTabe Report  - fileName is  " + filename);
			ServletOutputStream out = response.getOutputStream();
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setContentType("application/msword");
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + filename + "\"");
			doc.save(out, SaveFormat.DOCX);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		System.out.println("TM : inside generateReqPerTabe Report  - returning filename  " + filename);
		return filename;
	}

	
	public static String generateReqPerTableReportSNExcel(HttpSession session, SecurityProfile securityProfile, 
			 User user, HttpServletRequest request,
			HttpServletResponse response,String rootDataDirectory, String exportType, String databaseType,
			int maxColumnsOfExcelToEmbedInWord) {
		String filename = "";
		try {
			System.out.println("TM : inside generateReqPerTableReportSN Report  - starting  " );
			
			int templateId = Integer.parseInt(request.getParameter("templateId"));
			WordTemplateSN wordTemplateSN = new WordTemplateSN(templateId);
			
			String[] displayAttributesArray = request.getParameterValues("displayAttributes");
			String displayAttributes = "";
			if (displayAttributesArray != null) {
				for (int i=0; i<displayAttributesArray.length; i++) {
					displayAttributes +=  displayAttributesArray[i] + ",";
				}
			}
			
			String[] dataFilesArray = request.getParameterValues("dataFiles");
			if (dataFilesArray != null) {
				for (int i=0; i<dataFilesArray.length; i++) {
					try {
					int dataFileId = Integer.parseInt(dataFilesArray[i]);
					WordTemplateSN dF = new WordTemplateSN(dataFileId);
					System.out.println("srt . the file is " + dF.getTemplateName() + " file path is " + dF.getTemplateFilePath());
					
					}
					catch (Exception dF){
						dF.printStackTrace();
					}
				}
			}
			
			
			/*
			
			Document doc = new Document(wordTemplateSN.getTemplateFilePath());
			DocumentBuilder builder = new DocumentBuilder(doc);

			
			
			// Lets go through the doc and find all instances of req: start
			// Get all runs from the document.
			NodeCollection paragraphs = doc.getChildNodes(NodeType.PARAGRAPH,true);
			Iterator p = paragraphs.iterator();
			while (p.hasNext()) {
				Paragraph paragraph = (Paragraph) p.next();
				// lets see if the paragraph string contains the search words.
				String paragraphText = paragraph.getRange().getText();
				if ((paragraphText != null) && !(paragraphText.equals("")) && paragraphText.toLowerCase().contains("req:")) {
					// this para has some text. so lets go forward.
					
					for (int k = 0 ; k < 10 ; k++){
						builder.writeln();
						paragraphText = paragraphText.replace("req:", "newly replaced " +  k);
						builder.write(paragraphText);
					}
				}
			}
			// Lets go through the doc and find all instances of req: start
			*/
			///////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////
			//
			// SRT
			// 
			// THIS WILL WORK : SEE THE createRequirementsFromWordTemplateByStyleUpdatable code
			// this will show you how you can read a doc with all the text of a particular style
			// find a way to insert the run multiple times 
			// Then find if the run text has any req: and parse them and replace them with apropriate code
			//
			//
			///////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////

			/*
			// Hyperlinks in a Word documents are fields, select all field start
			// nodes so we can find the hyperlinks.
			NodeList fieldStarts = doc.selectNodes("//FieldStart");
			Iterator fs = fieldStarts.iterator();
			

			System.out.println("TM : inside generateReqPerTabe Report  - going through nodes " );
			
			while (fs.hasNext()) {
				String displayAttributes = selectedDisplayAttributes;
				
				Node node = (Node) fs.next();
				FieldStart fieldStart = (FieldStart) node;
				
				if (fieldStart.getFieldType() == FieldType.FIELD_HYPERLINK) {
					Hyperlink hyperlink = null;
					try {
						hyperlink = new Hyperlink(fieldStart);
					}
					catch (Exception e){
						// since we hit an exception, lets ignore this link
						e.printStackTrace();
						System.out.println("srt hit an exception in making a hyperlink, so we will skip this one" );
						continue;
					}
					
					if (hyperlink == null){
						continue;
					}
					String urlString = hyperlink.getName();
					// NOTE : for some reason we are getting a weird character (Paragraph)
					// or DC4 when viewed in Notepad++
					// so if the length is > 0, then lets drop the first char.
					if ((urlString != null)&& (urlString.length() > 1)){
						urlString = urlString.substring(1);
						urlString = java.net.URLDecoder.decode(urlString,"UTF-8");
					}
					
					
					// urlString contains the information we need to parse and
					// build out.
					if (urlString == null) {
						// if urlString is null, skip this row.
						continue;
					}
					
					
					ArrayList<JSONObject> requirements = new ArrayList<JSONObject>();
					try {
						requirements = WordTemplateUtilSN.getRequirementsFromURLStringSN(session, wordTemplateSN.getSNProjectId(), urlString);
					}
					catch (Exception e){
						e.printStackTrace();
					}
					
					
					if (requirements.size() == 0) {
						// no requirements were found for this hyperlink.
						// so lets skip this one.
						continue;
					}

					
					builder.moveTo(node);
					
					// if this hyperlink had led to any requirements being generated, lets drop the link
					if (requirements.size() > 0 ){
						//hyperlink.setName(" ");
						//hyperlink.setTarget(" ");
					}
					
					
					
						
					Font font = builder.getFont();
					Iterator i = requirements.iterator();
					
					while (i.hasNext()) {
						JSONObject r = (JSONObject) i.next();
						System.out.println("srt JSONObject is " + r.toString());
						
						String fullTag = "";
						try{
							fullTag = r.getString("full_tag");
						}
						catch (Exception f){
							//f.printStackTrace();
						}
						
						if (!(fullTag.equals(""))){

							System.out.println("srt displayAttributes is " + displayAttributes);
							try {
								
								//String fullTag = r.getString("full_tag");
								font = builder.getFont();							
								builder.startTable();
	
								String name = "";
								try{
									name = r.getString("name");
								}
								catch (Exception d){}
								
								builder.insertCell();
								cleanUpFont(font);
								setUpCell(builder, 100);
								builder.write("Req Id");
								builder.insertCell();
								cleanUpFont(font);
								setUpCell(builder, 400);
								String reqSysId = r.getString("sys_id");
								String url =   instance + "/x_tracl_tracecloud_RequirementPage.do?sys_id=" + reqSysId ;
								font.setColor(Color.BLUE);
								font.setUnderline(1);
								builder.insertHyperlink(fullTag, url, false);
								
								cleanUpFont(font);
								builder.write("   (V - " + r.getString("version") + ") ");
								builder.write(" Completion: " + r.getString("completion") + "%");
								builder.endRow();
								
								
								if (displayAttributes.contains("name")){
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 100);
									builder.write("Name");
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 400);
									builder.write(name);
									builder.endRow();
								}
								
		
								if (displayAttributes.contains("description")){
									String details = "";
									try{
										details = r.getString("details");
									}
									catch (Exception d){}
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 100);
									builder.write("Details");
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 400);
									builder.insertHtml(java.net.URLDecoder.decode(details,"UTF-8")); 
									builder.endRow();
								}
								
								// Custom Attributes
								if (displayAttributes.contains("customAttributes")){
									try{
									String udaString = r.getString("uda");
									// lets replace / with space.
									udaString = udaString.replace("\\", "");
									
									JSONObject uda =  new JSONObject(udaString);
									JSONArray keys = uda.names();
									for (int j = 0; j < keys.length (); ++j) {
		
									   String key = keys.getString (j); 
									   String value = "";
										try{
										 value = uda.getString (key); 
										}
										catch (Exception d){
											d.printStackTrace();
										}
										
										if (displayAttributes.contains("customAttributesNonEmpty")){
											if (value.equals("")){
												continue;
											}
										}		
									   
									   builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 100);
										builder.write(key);
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 400);
										builder.insertHtml(java.net.URLDecoder.decode(value,"UTF-8")); 
										//builder.insertHtml(r.getString("details")); 
										builder.endRow();
		
									}
									}
									catch (Exception g){
										g.printStackTrace();
									}
								}
								
								// lets try to print the selected custom attributes.
								if (displayAttributes.contains("attribute=")){
									try{
										// lets get the uda object 
										String udaString = r.getString("uda");
										// lets replace / with space.
										udaString = udaString.replace("\\", "");
										
										JSONObject uda =  new JSONObject(udaString);
										JSONArray keys = uda.names();
										
										// lets get the attribute name that we need to process and print. 
										String[] attributeStrings = displayAttributes.split(",");
										for (String aS : attributeStrings){
											if (aS.contains("attribute=")){
												String attributeLabel = aS.replace("attribute=", "").trim();
												// now that we have the attribute label that was sent it, lets try to print it.
												for (int j = 0; j < keys.length (); ++j) {
													
													   String key = keys.getString (j); 
													   String value = "";
														try{
														 value = uda.getString (key); 
														}
														catch (Exception d){
															d.printStackTrace();
														}
														
																
													   if (key.equals(attributeLabel)){
														   builder.insertCell();
															cleanUpFont(font);
															setUpCell(builder, 100);
															builder.write(key);
															builder.insertCell();
															cleanUpFont(font);
															setUpCell(builder, 400);
															builder.insertHtml(java.net.URLDecoder.decode(value,"UTF-8")); 
															//builder.insertHtml(r.getString("details")); 
															builder.endRow();
													   }
						
													}
											}
										}
									
									
									
									}
									catch (Exception g){
										g.printStackTrace();
									}
								}
								if (displayAttributes.contains("owner")){
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 100);
									builder.write("Owner");
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 400);
									builder.write(r.getString("owner"));
									builder.endRow();
								}

								
								if (displayAttributes.contains("traceTo")){
									
									// lets get all the tracetoObjects.
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 100);
									builder.write("Traces To (Up) ");
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 400);
									try{
										String tTString = r.getString("trace_to_objects");
										JSONArray tTO = new JSONArray(tTString);
										
										
										for (int j = 0; j < tTO.length(); j++) {
										    
								            JSONObject t = tTO.getJSONObject(j);
										    String status = t.getString("Status");
										    
										    String tReqSysId =  t.getString("reqSysId");
											url =   instance + "/x_tracl_tracecloud_RequirementPage.do?sys_id=" + tReqSysId ;
											String tName =  t.getString("fulltag") + " : " +  t.getString("name");
											if (status.equals("Suspect")){
												font.setColor(Color.RED);
												builder.write("(Suspect) ");
												cleanUpFont(font);
											}
											else {
												font.setColor(Color.GREEN);
												builder.write("(Clear) ");
												cleanUpFont(font);
											}
											font.setUnderline(1);
											builder.insertHyperlink(tName, url, false);
											builder.writeln();
											
										}
										
									}
									catch(Exception traceToException){
										traceToException.printStackTrace();
									}
									builder.endRow();
								}	
								
								if (displayAttributes.contains("tracesFrom")){
									
									// lets get all the tracetoObjects.
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 100);
									builder.write("Traces From (Down) ");
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 400);
									try{
										String tFString = r.getString("trace_from_objects");
										JSONArray tFO = new JSONArray(tFString);
										for (int j = 0; j < tFO.length(); j++) {
										    JSONObject t = tFO.getJSONObject(j);
										    String status = t.getString("Status");
										    
										    String tReqSysId =  t.getString("reqSysId");
											url =   instance + "/x_tracl_tracecloud_RequirementPage.do?sys_id=" + tReqSysId ;
											String tName =  t.getString("fulltag") + " : " +  t.getString("name");
											if (status.equals("Suspect")){
												font.setColor(Color.RED);
												builder.write("(Suspect) ");
												cleanUpFont(font);
											}
											else {
												font.setColor(Color.GREEN);
												builder.write("(Clear) ");
												cleanUpFont(font);
											}
											font.setUnderline(1);
											builder.insertHyperlink(tName, url, false);
											builder.writeln();
										}
										
									}
									catch(Exception traceFromException){
										traceFromException.printStackTrace();
									}
									builder.endRow();
								}
								
								
								
								if (displayAttributes.contains("fileAttachments")){
									// attachments. 
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 100);
									builder.write("Attachments");
									builder.insertCell();
									cleanUpFont(font);
									setUpCell(builder, 400);
									try{
										String attachmentsString = r.getString("attachment_details");
										JSONArray attachments = new JSONArray(attachmentsString);
										for (int j = 0; j < attachments.length(); j++) {
										    JSONObject attachment = attachments.getJSONObject(j);
										    builder.write(attachment.getString("attachment_name"));
								            builder.writeln();
										}
									}
									catch(Exception attachmentsE){
										attachmentsE.printStackTrace();
									}
									builder.endRow();
								}
								
								builder.endTable();
								builder.insertParagraph();
								//builder.writeln("");
								
								
							} catch (Exception e) {
								System.out.println("srt ran into error in write of builder");
								e.printStackTrace();
							}
						}
					}
					
					
				}
			}
			

			System.out.println("TM : inside generateReqPerTabe Report  - completed document " );
	
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
			Calendar cal = Calendar.getInstance();
			String today = sdf.format(cal.getTime());
			filename = wordTemplateSN.getTemplateName() + today + ".docx";

			filename.replace(' ', '_');

			

			System.out.println("TM : inside generateReqPerTabe Report  - fileName is  " + filename);
			ServletOutputStream out = response.getOutputStream();
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setContentType("application/msword");
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + filename + "\"");
			doc.save(out, SaveFormat.DOCX);
		
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		System.out.println("TM : inside generateReqPerTabe Report  - returning filename  " + filename);
		return filename;
		
		
	}

	
	public static String generateSNExcelOriginal(HttpSession session, SecurityProfile securityProfile, String reportType,
			String sourceName, User user, HttpServletRequest request,
			HttpServletResponse response,String rootDataDirectory, String exportType, String databaseType
			) {
		String filename = "";
		try {
						
			String sNProjectId = request.getParameter("sNProjectId");
			String instance = (String) session.getAttribute("instance");
			
			// TODO : LETS MAKE SURE THAT THE USER IS REALLY A MEMBER OF THIS PROJECT
			
			System.out.println("TM : inside generateSNExcel Report  - starting for Project Id  " + sNProjectId );
			
			
			
			
					
			ArrayList<JSONObject> requirements = new ArrayList<JSONObject>();
			
			System.out.println("SRT report Type is    " + reportType + " sourceName is " + sourceName );
			// this is a report.
			try {
				if (reportType.equals("reportId")) {
					requirements = getRequirementFromSNByReportId(session, sNProjectId, sourceName);
				}
				if (reportType.equals("reqTypeName")) {
					requirements = getRequirementFromSNByRequirementType(session, sNProjectId, sourceName);
				}
				if (reportType.equals("folderPath")) {
					requirements = getRequirementFromSNByFolderPath(session, sNProjectId, sourceName);
				}
				
			}
			catch (Exception e){
				e.printStackTrace();
			}
		
			
			
		
			
			HSSFWorkbook wb = new HSSFWorkbook();
    		HSSFCreationHelper createHelper = (HSSFCreationHelper) wb.getCreationHelper(); 

    		HSSFCellStyle headerStyle = wb.createCellStyle();
    		headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
    	    headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    

    	    HSSFCellStyle wrappedStyle = wb.createCellStyle();
    	    wrappedStyle.setWrapText(true);
    	    
    	    
    	    // lets build all the sheets in this file.
    	    HSSFSheet reportSheet = wb.createSheet("List Report");
	    	
				
    	    int j = 0;
			Iterator i = requirements.iterator();
			while (i.hasNext()){
				j++;
				
	    		// for the first row, print the header and user defined columns headers. etc..
	    		if (j == 1){

	        		// Create a row and put some cells in it. Rows are 0 based.
	        		HSSFRow row     = reportSheet.createRow((short)0);	
	        		

	        		// Print the header row for the excel file.
	        		int cellNum = 0;
	        		int column = 0;
	        		
	        		HSSFCell cell = row.createCell(cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Tag          "));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(column++);
	        		
	        	    
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
	        		cell.setCellValue(new HSSFRichTextString ("Owner                                  "));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(column++);
        		
	        	}
	    		
	    		/*
				JSONObject r = (JSONObject) i.next();
				
				String fullTag = "";
				try{
					fullTag = r.getString("full_tag");
				}
				catch (Exception f){
					//f.printStackTrace();
				}
				
				System.out.println("SRT fullTag IS   " + fullTag );
				if (!(fullTag.equals(""))){
	
						
					String name = "";
					try{
						name = r.getString("name");
					}
					catch (Exception d){}
					
					System.out.println("sre req name is  " + fullTag +  " : " + name );

					
					String reqSysId = r.getString("sys_id");
					String url =   instance + "/x_tracl_tracecloud_RequirementPage.do?sys_id=" + reqSysId ;
					String version =  r.getString("version") ;
					
					String details = "";
					try{
						details = r.getString("details");
					}
					catch (Exception d){}
					
					try{
					String udaString = r.getString("uda");
					// lets replace / with space.
					udaString = udaString.replace("\\", "");
					
					JSONObject uda =  new JSONObject(udaString);
					JSONArray keys = uda.names();
					for ( j = 0; j < keys.length (); ++j) {
	
					   String key = keys.getString (j); 
					   String value = "";
						try{
						 value = uda.getString (key); 
						}
						catch (Exception d){
							d.printStackTrace();
						}
					}								}
					catch (Exception g){
						g.printStackTrace();
					}
				
					
					String owner = r.getString("owner");
					
					String tTString = r.getString("trace_to_objects");
					JSONArray tTO = new JSONArray(tTString);
					
					
					for ( j = 0; j < tTO.length(); j++) {
					    
			            JSONObject t = tTO.getJSONObject(j);
					    String status = t.getString("Status");
					    
					    String tReqSysId =  t.getString("reqSysId");
						url =   instance + "/x_tracl_tracecloud_RequirementPage.do?sys_id=" + tReqSysId ;
						String tName =  t.getString("fulltag") + " : " +  t.getString("name");
						
					}
						
					
					try{
						String tFString = r.getString("trace_from_objects");
						JSONArray tFO = new JSONArray(tFString);
						for ( j = 0; j < tFO.length(); j++) {
						    JSONObject t = tFO.getJSONObject(j);
						    String status = t.getString("Status");
						    
						    String tReqSysId =  t.getString("reqSysId");
							url =   instance + "/x_tracl_tracecloud_RequirementPage.do?sys_id=" + tReqSysId ;
							String tName =  t.getString("fulltag") + " : " +  t.getString("name");
							
							
						}
						
					}
					catch(Exception traceFromException){
						//traceFromException.printStackTrace();
					}
					
					
					
					
					try{
						String attachmentsString = r.getString("attachment_details");
						JSONArray attachments = new JSONArray(attachmentsString);
						for ( j = 0; j < attachments.length(); j++) {
						    JSONObject attachment = attachments.getJSONObject(j);
						   
						}
					}
					catch(Exception attachmentsE){
						//attachmentsE.printStackTrace();
					}
					
					// lets print the Excel Row
					System.out.println("srt excel row is " + j);
					HSSFRow row     = reportSheet.createRow(j);
		    		int cellNum = 0;
		    		
					HSSFCell cell = row.createCell(++cellNum);
				    cell.setCellValue(new HSSFRichTextString (fullTag));
				    cell.setCellStyle(wrappedStyle);

					System.out.println("srt excel cell is " + cellNum);
					
				    
				    cell = row.createCell(++cellNum);
				    cell.setCellValue(new HSSFRichTextString (url));
				    cell.setCellStyle(wrappedStyle);
				    System.out.println("srt excel cell is " + cellNum);
					
				    
				    cell = row.createCell(++cellNum);
				    cell.setCellValue(new HSSFRichTextString (version));
				    cell.setCellStyle(wrappedStyle);
				    System.out.println("srt excel cell is " + cellNum);
					
				    cell = row.createCell(++cellNum);
				    cell.setCellValue(new HSSFRichTextString (name));
				    cell.setCellStyle(wrappedStyle);
				    System.out.println("srt excel cell is " + cellNum);
					
				    cell = row.createCell(++cellNum);
				    cell.setCellValue(new HSSFRichTextString (details));
				    cell.setCellStyle(wrappedStyle);
				    System.out.println("srt excel cell is " + cellNum);
					
				}
				*/		
						
			}		
				
			

			System.out.println("TM : inside generateReqPerTabe Report  - completed document " );
			response.setContentType("application/vnd.ms-excel");
    		// create a file name and set it to it.
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
    		String today =  sdf.format(cal.getTime());
    		filename = reportType + "_" + sourceName +" Report " + today + ".xls";
    		filename.replace(' ', '_');
    		
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
			
			OutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();
            
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		System.out.println("TM : inside generateReqPerTabe Report  - returning filename  " + filename);
		return filename;
	}

	public static void generateSNExcel(HttpSession session, SecurityProfile securityProfile, String reportType,
			String sourceName, User user, HttpServletRequest request,
			HttpServletResponse response,String rootDataDirectory, String exportType, String databaseType
			) {
		String filename = "";
		try {
						
			String sNProjectId = request.getParameter("sNProjectId");
			String instance = (String) session.getAttribute("instance");
			
			// TODO : LETS MAKE SURE THAT THE USER IS REALLY A MEMBER OF THIS PROJECT
			
			System.out.println("TM : inside generateSNExcel Report  - starting for Project Id  " + sNProjectId );
			
			
			
			
					
			ArrayList<JSONObject> requirements = new ArrayList<JSONObject>();
			
			System.out.println("SRT report Type is    " + reportType + " sourceName is " + sourceName );
			// this is a report.
			try {
				if (reportType.equals("reportId")) {
					requirements = getRequirementFromSNByReportId(session, sNProjectId, sourceName);
				}
				if (reportType.equals("reqTypeName")) {
					requirements = getRequirementFromSNByRequirementType(session, sNProjectId, sourceName);
				}
				if (reportType.equals("folderPath")) {
					requirements = getRequirementFromSNByFolderPath(session, sNProjectId, sourceName);
				}
				
			}
			catch (Exception e){
				e.printStackTrace();
			}
		
			
			
		
			
			HSSFWorkbook wb = new HSSFWorkbook();
    		HSSFCreationHelper createHelper = (HSSFCreationHelper) wb.getCreationHelper(); 

    	    HSSFCellStyle wrappedStyle = wb.createCellStyle();
    	    wrappedStyle.setWrapText(true);
    	    
    	    

    	    
    	    // lets build all the sheets in this file.
    	    HSSFSheet reportSheet = wb.createSheet("List Report");
    	   
			System.out.println("srtx " );

			ArrayList<String> attributeLabels = new ArrayList<String>();
    	    
    	    int j = 0;
			Iterator i = requirements.iterator();
			while (i.hasNext()){
				try {
					j++;
					System.out.println("srtx j is " + j );
	
					String sysId = "";
					String fullTag = "";
					String version =  "Version" ;
					String url =   "URL" ;
					String name = "";
					String details = "";
					String owner = "";
					
					
					JSONObject r = (JSONObject) i.next();
					System.out.println("srt the row is \n\n" + r + "\n");
					
					String reqSysId = r.getString("sys_id");
					
					try{
						fullTag = r.getString("full_tag");
					}catch (Exception a){}
					
					
					try{
						version = r.getString("version");
					}catch (Exception b){}
					
					
					try{
						name = r.getString("name");
					}catch (Exception b){}
					
					
					try{
						details = r.getString("details");
					}catch (Exception d){}
					
					try{
						owner = r.getString("owner");
					}catch (Exception b){}
					
					
					System.out.println("srtx fullTag is " + fullTag );
					
		    		// for the first row, print the header and user defined columns headers. etc..
		    		if (j == 1){
	
		    			
		    			System.out.println("srtx priting first row " );
	
		        		// Create a row and put some cells in it. Rows are 0 based.
		        		HSSFRow row     = reportSheet.createRow((short)0);	
		        		
	
		        		// Print the header row for the excel file.
		        		int cellNum = 0;
		        		int column = 0;
		        		
		        		HSSFCell cell = row.createCell(cellNum);
		        		cell.setCellValue(new HSSFRichTextString ("Tag          "));
		        		reportSheet.autoSizeColumn(column++);
		        		
		        		cell = row.createCell(++cellNum);
		        		cell.setCellValue(new HSSFRichTextString ("Version"));
		        		reportSheet.autoSizeColumn(column++);
		        		
		        		
		        		cell = row.createCell(++cellNum);
		        		cell.setCellValue(new HSSFRichTextString ("URL To Requirement                                                                                                          "));
		        		reportSheet.autoSizeColumn(column++);
		        		
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Name                                                                                                              "));
		        		reportSheet.autoSizeColumn(column++);
		        		
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Details                                                                                                              "));
		        		reportSheet.autoSizeColumn(column++);
		        		
		        		
	        			cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Owner                                  "));
		        		reportSheet.autoSizeColumn(column++);
	        		
		        		
		        		

						try{
							String udaString = r.getString("uda");
							// lets replace / with space.
							udaString = udaString.replace("\\", "");
							
							JSONObject uda =  new JSONObject(udaString);
							JSONArray keys = uda.names();
							for (int k = 0; k < keys.length (); ++k) {
							   String key = keys.getString (k); 
							   cell = row.createCell(++cellNum); 
							   cell.setCellValue(new HSSFRichTextString (key));
							   reportSheet.autoSizeColumn(column++);
			        		
							   attributeLabels.add(key);
							}
						}
						catch (Exception g){
							g.printStackTrace();
						}
					
		        	}
		    		
		    		
		    		
		    		HSSFRow row     = reportSheet.createRow((short)j);	
	        		
	
	        		// Print the header row for the excel file.
	        		int cellNum = 0;
	        		
	        		HSSFCell cell = row.createCell(cellNum++);
	        		cell.setCellValue(new HSSFRichTextString (fullTag));
	        		reportSheet.autoSizeColumn(cellNum);
	        		
	        		cell = row.createCell(cellNum++);
	        		cell.setCellValue(new HSSFRichTextString (version));
	        		reportSheet.autoSizeColumn(cellNum);
	        		
	        		
	        		cell = row.createCell(cellNum++);
	        		cell.setCellValue(new HSSFRichTextString (url));
	        		reportSheet.autoSizeColumn(cellNum);
	        		
	        		
	        		
	        		cell = row.createCell(cellNum++);
	        		cell.setCellValue(new HSSFRichTextString (name));
	        		reportSheet.autoSizeColumn(cellNum);
	        		
	        		cell = row.createCell(cellNum++);
	        		cell.setCellValue(new HSSFRichTextString (details));
	        		reportSheet.autoSizeColumn(cellNum);
	        		
	        		
	        		cell = row.createCell(cellNum++);
	        		cell.setCellValue(new HSSFRichTextString (owner));
	        		reportSheet.autoSizeColumn(cellNum);
	        		
	        		
	        		try{
						String udaString = r.getString("uda");
						// lets replace / with space.
						udaString = udaString.replace("\\", "");
						
						JSONObject uda =  new JSONObject(udaString);
						// loop through the attributes and print the attribute value
						for (String attributeLabel : attributeLabels){
							String attributeValue = "";
							try{
								attributeValue = uda.getString (attributeLabel); 
							}
							catch (Exception d){
								d.printStackTrace();
							}
							
							cell = row.createCell(cellNum++);
			        		cell.setCellValue(new HSSFRichTextString (attributeValue));
			        		reportSheet.autoSizeColumn(cellNum);
			        		
						}
					}
					catch (Exception g){
						g.printStackTrace();
					}
				}
				catch (Exception printExcelRow){
					printExcelRow.printStackTrace();
				}  
	    		
			}
    		

    		
    		
    		
			

			System.out.println("TM : inside generateReqPerTabe Report  - completed document " );
			response.setContentType("application/vnd.ms-excel");
    		
			String fileNme =  "testing";
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
			
			OutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void cleanUpFont(Font font) {
		try {
			font.setSize(10);
			
			font.setStrikeThrough(false);
			font.setColor(Color.BLACK);
			font.setUnderline(0);
			font.setBold(false);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	public static void setUpCell(DocumentBuilder builder, int colWidth ){
		try {
			builder.getCellFormat().setWidth(colWidth);
			builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.TOP);
			builder.getCellFormat().getShading().setBackgroundPatternColor(Color.WHITE);
			

			builder.getCellFormat().getBorders().setLineStyle(LineStyle.SINGLE);
		} catch (Exception e) {
			
			e.printStackTrace();
		}		
	}
	

	public static void printColumnHeaderCell(Font font, int cellSize, String cellName, DocumentBuilder builder ){
		try {
			builder.insertCell();
			cleanUpFont(font);
			setUpCell(builder, cellSize);
			font.setColor(Color.BLUE);
			font.setBold(true);
			builder.write(cellName + " ");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
}
