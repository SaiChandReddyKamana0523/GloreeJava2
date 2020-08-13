package com.gloree.utils;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
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

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;










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

public class WordTemplateUtil {

	// parses the input string and returns an array list of requirements..
	// NOTE URL STRING can be like
	// "BR-1,BR-2,PR-3..PR-35,REPORTID-246,FS-1"
	// NOTE , we also support BR-2:name etc.. where :xxxx is the attribute name
	public static ArrayList getRequirementsFromURLString(SecurityProfile securityProfile, int projectId,
			String urlString, String databaseType) {
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {



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
	
					try {
						// at this point element can have BR-1 or BR-1..BR-25 or
						// REPORTID-346
						if (element.contains("REPORTID-")) {
							// this is a report.
							try {
								String[] reportInfo = element.split("-");
								int reportId = Integer.parseInt(reportInfo[1]);
								Report report = new Report(reportId);
								if (report.getProectId() != projectId) {
									// NOTE : CRITICAL .
									// this means that the report does not belong to
									// this
									// project, and is a major security hole. Hence we
									// do
									// not run this report.
									continue;
								}
		
								ArrayList genericReport = ReportUtil.runGenericReport(
										securityProfile, report, projectId,  databaseType);
								// now lets pump them in to the requirements arraylist.
								Iterator k = genericReport.iterator();
								while (k.hasNext()) {
									Requirement requirement = (Requirement) k.next();
									// lets discard this req if it has been deleted.
									if (requirement.getDeleted() != 1 ){
										requirements.add(requirement);
									}
								}
								
								// now that we have a list of reqs, lets sort them.
								
								//NEEL is SUPER COOL WOOT WOOT I LUVV U DADDDDD!!!
								ArrayList <Requirement> postFilteredSorted = new ArrayList<Requirement>();  
								postFilteredSorted = requirements;
								try{
									Folder folder = new Folder(report.getFolderId());
									// get sortBy and sortByType from reportDefinition
									String reportDefinition = report.getReportDefinition();
									String[] rD = reportDefinition.split(":###:");
									String sortBy = "";
									String sortByType = "";
									for (String rDString: rD){
										if (rDString.contains("sortBy:--:")){

											String[] sB = rDString.split(":--:");
											sortBy = sB[1];	
										}
										if (rDString.contains("sortByType:--:")){
											String[] sB = rDString.split(":--:");
											sortByType = sB[1];	
										}
									}
									
									if (sortBy.startsWith("CustomAttribute")){
										String customAttribute = sortBy.replace("CustomAttribute", "");
										customAttribute = customAttribute.replace(":#:", "");
										// if the custom attribute is of type number, then we have to sort the arraylist manually
										RTAttribute attribute = new RTAttribute(folder.getRequirementTypeId(),customAttribute);
										if (attribute.getAttributeType().equals("Number")){
											// this is an attribute of type number, and our alphanumeric sorting doesn't work.
											// So we will have to manually sort. 
											postFilteredSorted = FolderUtil.sortRequirementsInArrayNumerically(requirements,attribute,sortByType);
										}
									
										
									}
									// if we ge till here, then we sorted successfully, and will assign sorted onces to reqirements
									requirements = postFilteredSorted;
								}
								catch (Exception sortException){
									// if we hit a failure, do nothing. 
									sortException.printStackTrace();
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
								String requirementTypeName = reportInfo[1];
								RequirementType requirementType = new RequirementType(projectId, requirementTypeName);
								
								if (requirementType == null){
									// we could not find the req type with this name
									continue;
								}
								
								ArrayList requirementsInRequirementType = ProjectUtil.getAllRequirementsInRT(requirementType.getRequirementTypeId(), "active", databaseType);
								
								Iterator k = requirementsInRequirementType.iterator();
								while (k.hasNext()) {
									Requirement requirement = (Requirement) k.next();
									// lets discard this req if it has been deleted.
									if (requirement.getDeleted() != 1 ){
										requirements.add(requirement);
										
									}
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
		
								Folder folder = new Folder(folderPath, projectId);
								
								if (folder == null){
									// we could not find the folder with this name
									continue;
								}
								// deletedFlag = 0 is not deleted
								int deletedFlag = 0;
								ArrayList requirementsInFolder = FolderUtil.getRequirementsInFolderPath(projectId, folder.getFolderPath(), deletedFlag, databaseType);
								
								Iterator k = requirementsInFolder.iterator();
								while (k.hasNext()) {
									Requirement requirement = (Requirement) k.next();
									// lets discard this req if it has been deleted.
									if (requirement.getDeleted() != 1 ){
										requirements.add(requirement);
										
									}
								}
								continue;
							}
							catch (Exception e){
								e.printStackTrace();
							}
						} else if (element.contains("..")) {
							try {
							
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
								continue;
							}
							catch (Exception e){
								e.printStackTrace();
							}
						} else {
							try {
								// at this point the element can be something like BR-1 or BR-1:name.
								if ((element!= null) && (element.contains(":"))){
									// lets remove the :attribute from element.
									String [] tagAttribute = element.split(":");
									element = tagAttribute[0];
								}
								// this must be a requirement.
								int requirementId = RequirementUtil.getRequirementId(
										projectId, element);
								if (requirementId > 0) {
									Requirement requirement = new Requirement(
											requirementId,  databaseType);
									// lets discard this req if it has been deleted.
									if (requirement.getDeleted() != 1 ){
										requirements.add(requirement);
									}
								}
							}
							catch (Exception e){
								e.printStackTrace();
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

	
	// if you have a req like 'CISCORFI:REL-1' in your current project 'TCD', this query can 
	// look up related projects and find the req and bring it for you. 
	public static Requirement getReqFromRelatedProjects(Project project, String relatedReqFullTAg) {
		
		Requirement relatedRequirement = null;
		try {

			int traceToReqProjectId = project.getProjectId();
			if (relatedReqFullTAg.contains(":")){
				String[] tRT = relatedReqFullTAg.split(":");
				String traceToReqProjectString = tRT[0];
				relatedReqFullTAg = tRT[1];
				traceToReqProjectId = project.getRelatedProjectId(traceToReqProjectString);
				
			}
		relatedRequirement = new Requirement(relatedReqFullTAg ,traceToReqProjectId, "mySQL");
			
		
		} catch (Exception e) {
			
			e.printStackTrace();
		} 
		return relatedRequirement;
	}

	
	// updates the name and description and last modified details of a word
	// template
	public static void updateNameAndDescription(int templateId,
			String templateName, String templateVisibility, String templateDescription, User user, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "update gr_word_templates " + " set name = ? , visibility = ?,  "
					+ " description = ? , " + " last_modified_by = ? ,"
					+ " last_modified_dt = now() " + " where id = ?  ";
			}
			else {
				sql = "update gr_word_templates " + " set name = ? , visibility = ?,  "
				+ " description = ? , " + " last_modified_by = ? ,"
				+ " last_modified_dt = sysdate " + " where id = ?  ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, templateName);
			prepStmt.setString(2, templateVisibility);
			prepStmt.setString(3, templateDescription);
			prepStmt.setString(4, user.getEmailId());
			prepStmt.setInt(5, templateId);

			prepStmt.execute();

			prepStmt.close();
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
	}

	

	// updates the FilePath of the template object
	public static ArrayList <WordTemplate> getDefaultWordTemplates(int requirementId) {
		
		ArrayList <WordTemplate> defaultWordTemplates = new ArrayList<WordTemplate>();
		int wordTemplateId = 0;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			
			sql = "select wt.id from "
				+ " gr_word_templates wt, gr_requirements r "
				+ " where r.id = ? "
				+ " and r.project_id = wt.project_id "
				+ " and lower(wt.file_path) like  ? ";
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);

			prepStmt.setString(2, "%requirementtemplate.doc%");
			rs = prepStmt.executeQuery();
			
			while (rs.next()){
				wordTemplateId = rs.getInt("id");
				WordTemplate wordTemplate = new WordTemplate(wordTemplateId, "mySQL");

				
				defaultWordTemplates.add(wordTemplate);
			}

			rs.close();
			prepStmt.close();
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
		
		return (defaultWordTemplates);
	}
	// updates the FilePath of the template object
	public static void updateFilePath(int templateId,
			String templateFilePath, User user, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "update gr_word_templates " + " set file_path = ? , last_modified_by = ? ,"
					+ " last_modified_dt = now() " + " where id = ?  ";
			}
			else {
				sql = "update gr_word_templates " + " set file_path = ? ,  last_modified_by = ? ,"
				+ " last_modified_dt = sysdate " + " where id = ?  ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, templateFilePath);
			prepStmt.setString(2, user.getEmailId());
			prepStmt.setInt(3, templateId);

			prepStmt.execute();

			prepStmt.close();
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
	}
	
	// updates the name and description and last modified details of a word
	// template
	public static void deleteWordTemplate(int templateId, Project project,
			User user, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets delete the physical file.
			WordTemplate wordTemplate = new WordTemplate(templateId,  databaseType);
			// we need to delete the file and the folder it contains. 
			// Note : we keep only 1 file in each folder. so this is safe.
			File file = new File(wordTemplate.getTemplateFilePath());
			if (file != null){
				File dir = file.getParentFile();
				// lets drop the file.
				file.delete();
				
				if (dir != null) {
					dir.delete();
				}
			}
			
			
			
			// now lets take out the db entry.
			String sql = "delete from gr_word_templates" + " where id = ?  ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, templateId);

			prepStmt.execute();

			// prior to exiting, we need to make a log entry in the project
			// table.
			ProjectUtil.createProjectLog(project.getProjectId(), wordTemplate
					.getTemplateName(), "Delete", "Word Document Deleted", user
					.getEmailId(),  databaseType);

			prepStmt.close();
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
	}

	
	public static void generateEmptyWordTemplate(Project project,
			User user, HttpServletRequest request, HttpServletResponse response) {
		try {
			
			Document doc = new Document();
			DocumentBuilder builder = new DocumentBuilder(doc);
			Font font = builder.getFont();
			cleanUpFont(font);

			// lets put an intro blurb
			builder.writeln("\n\nPlease use this document to fill up your Requirements." +
				" If you need to create additional Requirements, feel free to copy / paste the Requirement Templates " +
				" of the Requirement Type you need. Also feel free to remove any unwanted Requirement Templates. " +
				" For best results, Please refrain from modifying the structure of the template itself.\n\n\n" +
				"Also note that the Required fields are labeled in Red\n\n\n");
			
			
			// now lets get the Requirement types chosen by the user.
			for (int i=1; i<=5; i++) {
				String requirementTypeLabel = "requirementType" + i;
				String numberOfRequirementsLabel = "numberOfRequirements" + i;
				
				
				int requirementTypeId = Integer.parseInt(request.getParameter(requirementTypeLabel));
				
				int numberOfRequirements = 0;
				try {
				String numberOfRequirementsString = request.getParameter(numberOfRequirementsLabel);
					if (numberOfRequirementsString != null) {
						numberOfRequirements = Integer.parseInt(numberOfRequirementsString);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
				
				String requirementTypeEligibleFolders = "";
				ArrayList myFolders = project.getMyFolders();
				Iterator f = myFolders.iterator();
				while (f.hasNext()){
					Folder folder = (Folder) f.next(); 
					if (folder.getRequirementTypeId() == requirementTypeId ) {
						requirementTypeEligibleFolders += folder.getFolderPath() + "\n";
					}
				}
				
				if (numberOfRequirements > 0 ) {
					// lets print a blurb about the Requirement Type.
					RequirementType requirementType = new RequirementType(requirementTypeId);
					builder.writeln("Requirement Type Prefix : " + requirementType.getRequirementTypeShortName());
					builder.writeln("Requirement Type Name : " + requirementType.getRequirementTypeName() );
					builder.writeln("Requirement Type Description : " + requirementType.getRequirementTypeDescription() );
					
					builder.writeln("\n\n");
					
					//now lets print the number of requirements the user wanted to see...
					for (int j=0; j<numberOfRequirements;j++){
						builder.startTable();

						builder.insertCell();
						cleanUpFont(font);						
						setUpCell(builder, 340);
						builder.getCellFormat().getShading().setBackgroundPatternColor(Color.LIGHT_GRAY);
						builder.write("Requirement Template");
						builder.getCellFormat().getShading().setBackgroundPatternColor(Color.WHITE);
						builder.endRow();

						builder.insertCell();
						cleanUpFont(font);
						setUpCell(builder, 60);
						font.setColor(Color.RED);
						builder.write("Type");
						builder.insertCell();
						cleanUpFont(font);
						setUpCell(builder, 400);
						builder.write(requirementType.getRequirementTypeName());
						builder.endRow();
						
						builder.insertCell();
						cleanUpFont(font);
						setUpCell(builder, 60);
						builder.write("Folder");
						builder.insertCell();
						cleanUpFont(font);
						setUpCell(builder, 400);
						builder.write( requirementTypeEligibleFolders );
						builder.endRow();
						
						builder.insertCell();
						cleanUpFont(font);
						setUpCell(builder, 60);
						builder.write("Owner");
						builder.insertCell();
						cleanUpFont(font);
						setUpCell(builder, 400);
						builder.write(user.getEmailId() );
						builder.endRow();
						
						builder.insertCell();
						cleanUpFont(font);
						setUpCell(builder, 60);
						builder.write("Priority");
						builder.insertCell();
						cleanUpFont(font);
						setUpCell(builder, 400);
						builder.write( "High, Medium, Low");
						builder.endRow();
						

						builder.insertCell();
						cleanUpFont(font);
						setUpCell(builder, 60);
						builder.write("External URL");
						builder.insertCell();
						cleanUpFont(font);
						setUpCell(builder, 400);
						builder.write( "");
						builder.endRow();
						
						
						builder.insertCell();
						cleanUpFont(font);
						setUpCell(builder, 60);
						font.setColor(Color.RED);
						builder.write("Name");
						builder.insertCell();
						cleanUpFont(font);
						setUpCell(builder, 400);
						builder.write( "" );
						builder.endRow();
						
						builder.insertCell();
						cleanUpFont(font);
						setUpCell(builder, 60);
						font.setColor(Color.RED);
						builder.write("Description");
						builder.insertCell();
						cleanUpFont(font);
						setUpCell(builder, 400);
						builder.write( "" );
						builder.endRow();
						
						
						builder.insertCell();
						cleanUpFont(font);
						setUpCell(builder, 60);
						builder.write("Trace To");
						builder.insertCell();
						cleanUpFont(font);
						setUpCell(builder, 400);
						builder.write( "" );
						builder.endRow();
						
						builder.insertCell();
						cleanUpFont(font);
						setUpCell(builder, 60);
						builder.write("Trace From");
						builder.insertCell();
						cleanUpFont(font);
						setUpCell(builder, 400);
						builder.write( "" );
						builder.endRow();

						// lets print the custom attributes.
						ArrayList attributes = ProjectUtil.getAllAttributes(requirementTypeId);
					    if ((attributes != null) && (attributes.size() > 0)){
					    	Iterator a = attributes.iterator();
					    	while (a.hasNext()) {
					    		RTAttribute rTAttribute = (RTAttribute) a.next();
								builder.insertCell();
								cleanUpFont(font);
								setUpCell(builder, 60);
								if (rTAttribute.getAttributeRequired() == 1) {
									font.setColor(Color.RED);			
								}
								builder.write(rTAttribute.getAttributeName());
								builder.insertCell();
								cleanUpFont(font);
								setUpCell(builder, 400);
								builder.write( rTAttribute.getAttributeDropDownOptions() );
								builder.endRow();					    		
					    	}
					    }

						builder.endTable();
						builder.writeln("");
						
					}
				}
				
			}
								
						
								
	
	
			ServletOutputStream out = response.getOutputStream();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
			Calendar cal = Calendar.getInstance();
			String today = sdf.format(cal.getTime());
			String filename = user.getFirstName() + " " + user.getLastName()  + " Empty Word Template" + today;
			filename.replace(' ', '_');


			// lets see what the output format requested is.
			String reportFormat = request.getParameter("reportFormat");


			
				filename += ".docx";
				response.setContentType("application/msword");
				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + filename + "\"");
				doc.save(out, SaveFormat.DOCX);
				return;
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	
	
	public static String generateReqTemplateReport(SecurityProfile securityProfile, Requirement r, int templateId, Project project,
			 User user, HttpServletRequest request,
			HttpServletResponse response,String rootDataDirectory,
			String databaseType,
			int maxColumnsOfExcelToEmbedInWord) {
		String fileName = "";
		String filePath = "";
		try {
			
			
			System.out.println("TM : inside wordUtil.generateReqTemplate Report  " );
			
			WordTemplate wordTemplate = new WordTemplate(templateId,  databaseType);

			Document doc = new Document(wordTemplate.getTemplateFilePath());
			DocumentBuilder builder = new DocumentBuilder(doc);

			
			// Hyperlinks in a Word documents are fields, select all field start
			// nodes so we can find the hyperlinks.
			NodeList fieldStarts = doc.selectNodes("//FieldStart");
			Iterator fs = fieldStarts.iterator();
			
			while (fs.hasNext()) {
				
				Node node = (Node) fs.next();
				FieldStart fieldStart = (FieldStart) node;
				
				if (fieldStart.getFieldType() == FieldType.FIELD_HYPERLINK) {
					// The field is a hyperlink field, use the "facade" class to
					// help to deal with the field.
					Hyperlink hyperlink = null;
					try {
						hyperlink = new Hyperlink(fieldStart);
					}
					catch (Exception e){
						// since we hit an exception, lets ignore this link
						e.printStackTrace();
						continue;
					}
					
					if (hyperlink == null){
						continue;
					}
					
					// Under some circumstances, the Target field is getting mucked up by Word
					// on the client's machine. while this is not strictly a tracecloud problem,
					// we are trying to see if the getName works better, as Word does not seem to 
					// be messing around with the name.
					//String urlString = hyperlink.getTarget();
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
					
					// find out what this url string. 
					builder.moveTo(node);
					
						
					Font font = builder.getFont();
					

					
						try {
							
							// If this was an embeded requirement attribute (like BR-1:name)
							// then we print this differently than the embedded Requirement (like BR-1)
							// this is beacuase embedded REqs are printed as tables where as with 
							// embedded attribs we just print the value.
							if ((urlString != null) &&(urlString.contains(":"))){
								// lets knock out the hyperlink so that it doesn't get displayed.
								
								hyperlink.setName("");
								hyperlink.setTarget("");
								// this is a single attribute print out.
								String [] display = urlString.split(":");
								String displayAttributes = display[1];
								
								
								String printableValue = r.getElement(displayAttributes);
								/*
								if (displayAttributes.trim().toLowerCase().equals("name")){
									// if we were asked to pring BR-1:name, then we will also print a link to the requirement
									String url = ProjectUtil.getURL(request, r.getRequirementId(),"requirement");
									cleanUpFont(font);
									font.setColor(Color.BLUE);
									font.setUnderline(1);
									builder.insertHyperlink(r.getRequirementName(), url, false);
									cleanUpFont(font);
									builder.write(" : ");
								

								}
								*/
								// if we are asked to print description, S some cases , there is HTML junk there
								// and we are better off priting it as html
								
								
								if (displayAttributes.trim().toLowerCase().equals("description")){
									builder.insertHtml(r.getRequirementDescription());
								}
								else if (displayAttributes.trim().toLowerCase().equals("tag")) {
									String url = ProjectUtil.getURL(request, r.getRequirementId(),"requirement");
									cleanUpFont(font);
									font.setColor(Color.BLUE);
									font.setUnderline(1);
									builder.insertHyperlink(r.getRequirementFullTag(), url, false);
								}
								else if (displayAttributes.trim().toLowerCase().equals("created date")) {
									
									cleanUpFont(font);
									builder.write(r.getCreatedDt());
								}
								else if (displayAttributes.trim().toLowerCase().equals("created by")) {
									
									cleanUpFont(font);
									builder.write(r.getCreatedBy() );
								}
								else if (displayAttributes.trim().toLowerCase().equals("approved date")) {
									
									cleanUpFont(font);
									builder.write(r.getApprovedByAllDt() );
								}
								else if (displayAttributes.trim().toLowerCase().equals("approvers")){
									
									// lets print the final approval status.
									builder.writeln("");
									builder.startTable();

									builder.insertCell();
									setUpCell(builder,150);
									builder.getCellFormat().getShading().setBackgroundPatternColor(Color.LIGHT_GRAY);
									builder.write("Current Approval Status "  );
									builder.insertCell();
									setUpCell(builder,380);
									if (r.getApprovalStatus().equals("Approved")){
										builder.getCellFormat().getShading().setBackgroundPatternColor(Color.GREEN);
									}
									if (r.getApprovalStatus().equals("Rejected")){
										builder.getCellFormat().getShading().setBackgroundPatternColor(Color.RED);
									}
									if (r.getApprovalStatus().equals("In Approval WorkFlow")){
										builder.getCellFormat().getShading().setBackgroundPatternColor(Color.YELLOW);
									}
									if (r.getApprovalStatus().equals("Draft")){
										builder.getCellFormat().getShading().setBackgroundPatternColor(Color.BLUE);
									}
									builder.write(r.getApprovalStatus()  );
									builder.endRow();
									builder.endTable();
									
									
									
									
									

									
									// lets print the final approval status.
									builder.writeln("");
									
									ArrayList<String> approversAndStatus = r.getApproversAndStatus();
									for (String aS : approversAndStatus){
										String[] approversArray = aS.split(":##:");
										
										 String roleName = "";
										 String approvalRank = "";
										 String approvalType = "";
										 String emailId = "";
										 String userName = "";
										 String status = "";
										 String date = "";
										 String note = "";
										 String approvedRoles = "";
										 String currentRoleApprovalNote = "";
										 String currentRoleApprovalDt = "";
										 try {
											 roleName =  approversArray[0];
											 approvalRank =  approversArray[1];
											 approvalType =  approversArray[2];
											 emailId =  approversArray[3];
											 userName =  approversArray[4];
											 status =  approversArray[5];
											 note =  approversArray[6];
											 date =  approversArray[7];
											 approvedRoles = approversArray[8];
										 }
										 catch (Exception e){}
									
										// loop through the approvedRoles, till you come across current role (for this role). 
										// parse that to get the approval note and approved date
										 try {
											if (approvedRoles.contains("#")){
												// lets split approvedRoles to get the approval date and note
												String[] approverDetails = approvedRoles.split(",");
												
												for (String aD :approverDetails ){
													if (aD.contains(roleName) ){
														// lets split aD by :#:
														String[] noteAndDate = aD.split(":#:");	
														currentRoleApprovalNote = noteAndDate[1];
														currentRoleApprovalDt = noteAndDate[2];
														
														note = currentRoleApprovalNote;
														date = currentRoleApprovalDt;
													}
												}
												
												
											}
										 }
										 catch (Exception e){}
									

										if (approvedRoles.contains(roleName)){
											status = "Approved";
										}
										// for every approver, lets create a table, a table cell, and end table row and table
										builder.writeln("");
										builder.startTable();

										

										builder.insertCell();
										setUpCell(builder,150);
										builder.getCellFormat().getShading().setBackgroundPatternColor(Color.LIGHT_GRAY);
										builder.write("Role / Rank / Type"  );
										builder.insertCell();
										setUpCell(builder,380);
										builder.write(roleName  + " : "  + approvalRank + " : " + approvalType );
										builder.endRow();
										


										builder.insertCell();
										setUpCell(builder,150);
										builder.getCellFormat().getShading().setBackgroundPatternColor(Color.LIGHT_GRAY);
										builder.write("Approver "  );
										builder.insertCell();
										setUpCell(builder,380);
										builder.write(userName );
										builder.endRow();
										
										
										
										builder.insertCell();
										setUpCell(builder,150);
										if (status.equals("Approved")){
											builder.getCellFormat().getShading().setBackgroundPatternColor(Color.GREEN);
										}
										if (status.equals("Rejected")){
											builder.getCellFormat().getShading().setBackgroundPatternColor(Color.RED);
										}
										if (status.equals("Pending")){
											builder.getCellFormat().getShading().setBackgroundPatternColor(Color.YELLOW);
										}
										
										builder.write("Response & Date"  );
										builder.insertCell();
										setUpCell(builder,380);
										
										builder.write(status + " : "  + date);
										builder.endRow();
										
										
										
										builder.insertCell();
										setUpCell(builder,150);
										builder.getCellFormat().getShading().setBackgroundPatternColor(Color.LIGHT_GRAY);										builder.write("Note "  );
										builder.insertCell();
										setUpCell(builder,380);
										builder.write(note );
										builder.endRow();
										
										
										
										
										
										builder.endTable();
										
									}
								
									
									
									
									
								}
								else{
									builder.write(printableValue);
								}
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}
				
					
					

				}
				
			}
	
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
			Calendar cal = Calendar.getInstance();
			String today = sdf.format(cal.getTime());
			fileName = r.getRequirementFullTag()  + " Word Template Report " + today + ".docx";
			fileName.replace(' ', '_');



    		// if rootDataDirectory/TraceCloud does not exist, lets create it.
    		File traceCloudRoot = new File (rootDataDirectory + File.separator +  "TraceCloud");
    		if (!(traceCloudRoot.exists() )){
    		    new File(rootDataDirectory + File.separator +  "TraceCloud").mkdir();
    		}

    		
    		// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
    		File tempFolderRoot  = new File (rootDataDirectory +File.separator +  "TraceCloud" + File.separator + "Temp");
    		if (!(tempFolderRoot.exists() )){
    			new File(rootDataDirectory + File.separator +  "TraceCloud" + File.separator + "Temp").mkdir();
    		}

    		filePath = rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  "Temp" + File.separator +  fileName;
    		FileOutputStream fileOut = new FileOutputStream(filePath);

    		doc.save(fileOut, SaveFormat.DOCX);
			fileOut.flush();
			fileOut.close();


			System.out.println("TM : inside wordUtil.generateReqTemplate Report  - saved file to filepath " + filePath);
				
		        
		} catch (Exception e) {

		}
		return filePath;
	}



	
	// goes through the document, and parses every hyperlink and 
	// fills up the document with the collateral about the requirements mentioned
	// in the hyperlink
	// This routine generates a separate table for each Requirement.
	public static String generateReqPerTableReport(SecurityProfile securityProfile, int templateId, Project project,
			String selectedDisplayAttributes, User user, HttpServletRequest request,
			HttpServletResponse response,String rootDataDirectory, String exportType, String databaseType,
			int maxColumnsOfExcelToEmbedInWord) {
		String filename = "";
		try {
			
			
			String formatBoldAttribute = request.getParameter("formatBoldAttribute");
			if (formatBoldAttribute==null){formatBoldAttribute="";}
			
			System.out.println("TM : inside generateReqPerTabe Report  - starting " );
				
			WordTemplate wordTemplate = new WordTemplate(templateId,  databaseType);

			Document doc = new Document(wordTemplate.getTemplateFilePath());
			DocumentBuilder builder = new DocumentBuilder(doc);

			
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

					
					
					// The field is a hyperlink field, use the "facade" class to
					// help to deal with the field.
					
					
					Hyperlink hyperlink = null;
					try {
						hyperlink = new Hyperlink(fieldStart);
					}
					catch (Exception e){
						// since we hit an exception, lets ignore this link
						e.printStackTrace();
						continue;
					}
					
					if (hyperlink == null){
						continue;
					}
					
					// Under some circumstances, the Target field is getting mucked up by Word
					// on the client's machine. while this is not strictly a tracecloud problem,
					// we are trying to see if the getName works better, as Word does not seem to 
					// be messing around with the name.
					//String urlString = hyperlink.getTarget();
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
					
					
					ArrayList requirements = new ArrayList();
					try {
						requirements = WordTemplateUtil.getRequirementsFromURLString(securityProfile, project.getProjectId(), urlString,  databaseType);
					}
					catch (Exception e){
						e.printStackTrace();
					}
					
					Report report = null;
					try {
						report = WordTemplateUtil.getReportFromURLString(securityProfile, project.getProjectId(), urlString,  databaseType);
					}
					catch (Exception e ){
						e.printStackTrace();
					}
					
					String reportDefinition = "";
					if (report != null){
						reportDefinition = report.getReportDefinition();
					}
					
					
					if (requirements.size() == 0) {
						// no requirements were found for this hyperlink.
						// so lets skip this one.
						continue;
					}

					builder.moveTo(node);
					
					
					
					
					
					// if this hyperlink had led to any requirements being generated, lets drop the link
					if (requirements.size() > 0 ){
						hyperlink.setName(" ");
						hyperlink.setTarget(" ");
					}
					
					
					
						
					Font font = builder.getFont();
					
					// for some reason, smallcaps are being set to On. So we are 
					// physically turnign them off.
					//font.setSmallCaps(false);
					

					Iterator i = requirements.iterator();
					
					while (i.hasNext()) {
						try {
							Requirement requirement = (Requirement) i.next();
							

							// If this was an embeded requirement attribute (like BR-1:name)
							// then we print this differently than the embedded Requirement (like BR-1)
							// this is beacuase embedded REqs are printed as tables where as with 
							// embedded attribs we just print the value.
							if ((urlString != null) &&(urlString.contains(":"))){
								// lets knock out the hyperlink so that it doesn't get displayed.
								hyperlink.setName("");
								hyperlink.setTarget("");
								// this is a single attribute print out.
								String [] display = urlString.split(":");
								displayAttributes = display[1];
								String printableValue = requirement.getElement(displayAttributes);
								// lets put a space after each ,for traceTo and traceFrom values
								
								if (displayAttributes.equals("traceto") || displayAttributes.equals("tracefrom")){
									if (printableValue == null){printableValue = "";}
									if (printableValue.contains(",")){
										printableValue = printableValue.replace(",", ", ");
									}
								}
								
								if (displayAttributes.trim().toLowerCase().equals("name")){
									// if we were asked to pring BR-1:name, then we will also print a link to the requirement
									String url = ProjectUtil.getURL(request, requirement.getRequirementId(),"requirement");
									cleanUpFont(font);
									font.setColor(Color.BLUE);
									font.setUnderline(1);
									builder.insertHyperlink(requirement.getRequirementFullTag(), url, false);
									cleanUpFont(font);
									builder.write(" : ");
																	
								}
								
								// if we are asked to print description, in some cases , there is HTML junk there
								// and we are better off priting it as html
								
								
								if (displayAttributes.trim().toLowerCase().equals("description")){
									if (formatBoldAttribute.toLowerCase().contains("description")){
										builder.setBold(true);
									}
									builder.insertHtml( requirement.getRequirementDescription().trim());
									
								}
								else{
									
									if (formatBoldAttribute.toLowerCase().contains("name")){
										builder.setBold(true);
									}
									builder.write(printableValue);
								}
							}
							else {
								// this is a regular Requirement. so lets print the 
								// display values picked by the user. 
								
								// if the user does not have read permissions on this requirement,
								// lets redact it. i.e. remove all sensitive infor from it.
								if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
										+ requirement.getFolderId()))){
									requirement.redact();
								}
								
								if (requirement.getDeleted() == 0 ){
									font = builder.getFont();
									
									builder.startTable();
									
									String url = ProjectUtil.getURL(request, requirement.getRequirementId(),"requirement");
									builder.insertCell();
									
									cleanUpFont(font);
									
									setUpCell(builder, 580);
									//builder.getCellFormat().getShading().setBackgroundPatternColor(Color.LIGHT_GRAY);
	
									font.setColor(Color.BLUE);
									font.setUnderline(1);
									builder.insertHyperlink(requirement.getRequirementFullTag(), url, false);
									font.setColor(Color.BLACK);
									font.setUnderline(0);
									
									
									if (formatBoldAttribute.toLowerCase().contains("name")){
										builder.setBold(true);
									}
									String requirementName = requirement.getRequirementName();
									
									builder.write(" : " + requirementName );
									builder.getRowFormat().setHeightRule(HeightRule.AUTO);
									builder.endRow();
									
									
									
									builder.getCellFormat().getShading().setBackgroundPatternColor(Color.WHITE);
									
									if ((displayAttributes != null) && (displayAttributes.contains("id"))) {
										// Insert the link.
										//builder.insertHyperlink(requirement.getRequirementFullTag(), url, false);
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.write("Id");
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
										builder.insertHyperlink(requirement.getRequirementFullTag(), url, false);
										builder.endRow();
										
									}
		
									
									if ((displayAttributes != null) && (displayAttributes.contains("type"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.write("Type");
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
										builder.write(requirement.getRequirementTypeName());
										builder.endRow();
										
									}
		
									if ((displayAttributes != null) && (displayAttributes.contains("approvalStatus"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.write("Approval Status");
										
										if (requirement.getApprovalStatus().equals("Draft")) {
											font.setHighlightColor(Color.MAGENTA);
										}
										if (requirement.getApprovalStatus().equals("In Approval WorkFlow")) {
											font.setHighlightColor(Color.blue);
										}
										if (requirement.getApprovalStatus().equals("Approved")) {
											font.setHighlightColor(Color.GREEN);
										}
										if (requirement.getApprovalStatus().equals("Rejected")) {
											font.setHighlightColor(Color.RED);
										}
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
										builder.write(requirement.getApprovalStatus());
										font.setHighlightColor(Color.WHITE);
										builder.endRow();
		
									}
									if ((displayAttributes != null) && (displayAttributes.contains("testingStatus"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.write("Testing Status");
										
										String testingStatus = requirement.getTestingStatus();
										if (testingStatus.equals("Pending")) {
											font.setHighlightColor(Color.YELLOW);
										}
										if (testingStatus.equals("Pass")) {
											font.setHighlightColor(Color.GREEN);
										}
										if (testingStatus.equals("Fail")) {
											font.setHighlightColor(Color.RED);
										}
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
										builder.write(testingStatus);
										font.setHighlightColor(Color.WHITE);
										builder.endRow();
		
									}
																	
									if ((displayAttributes != null)&& (displayAttributes.contains("version"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.write("Version");
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
										builder.write(Integer.toString(requirement.getVersion()));
										builder.endRow();
										
									}
									if ((displayAttributes != null) && (displayAttributes.contains("priority"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.write("Priority");
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
										builder.write(requirement.getRequirementPriority());
										builder.endRow();		
										
									}
		
									if ((displayAttributes != null) && (displayAttributes.contains("owner"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.write("Owner");
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
										builder.write(requirement.getRequirementOwner());
										builder.endRow();		
										
									}
		
									if ((displayAttributes != null) && (displayAttributes.contains("pctComplete"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.write("Percent Complete");
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
										builder.write(Integer.toString(requirement.getRequirementPctComplete()) + " %");
										builder.endRow();		
										
									}
		
									if ((displayAttributes != null) && (displayAttributes.contains("externalURL"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.write("External URL");
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
										builder.write(requirement.getRequirementExternalUrl());
										builder.endRow();		
										
									}
		
									if ((displayAttributes != null) && (displayAttributes.contains("approvers"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.write("Approvers");
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
										
										
										
										String approversString = requirement.getApprovers();
										if ((approversString != null)
												&& (approversString.contains(","))) {
											String[] approvers = approversString
													.split(",");
											for (int k = 0; k < approvers.length; k++) {
												String approver = approvers[k];
												if ((approver != null)
														&& (approver.contains("(P)"))) {
													font.setColor(Color.PINK);
												}
												if ((approver != null)
														&& (approver.contains("(A)"))) {
													font.setColor(Color.GREEN);
												}
												if ((approver != null)
														&& (approver.contains("(R)"))) {
													font.setColor(Color.RED);
												}
												builder.write(approver);
											}
										}
										builder.endRow();
									}
		
									if ((displayAttributes != null) && (displayAttributes.contains("approvalDate"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.write("Approval Date");
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
										builder.write(requirement.getApprovedByAllDt());
										builder.endRow();		
		
									}
		
									if ((displayAttributes != null) && (displayAttributes.contains("url"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.getCellFormat().setWidth(80);
										builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
										builder.getCellFormat().getBorders().setLineStyle(LineStyle.SINGLE);
										builder.write("URL");
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
										builder.getCellFormat().setWidth(500);
										builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
										builder.getCellFormat().getBorders().setLineStyle(LineStyle.SINGLE);
		
										
										
										font.setColor(Color.BLUE);
										font.setUnderline(Underline.SINGLE);								
										url = ProjectUtil.getURL(request,requirement.getRequirementId(),"requirement");
										// Insert the link.
										builder.insertHyperlink(url, url, false);
										// Revert to default formatting.
										font.setColor(Color.BLACK);
										font.setUnderline(0);
										builder.endRow();		
									}
		
									if ((displayAttributes != null) && (displayAttributes.contains("traceTo"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.write("Trace To");
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
		
										
										// lets color code the traceTo and traceFrom
										// values.
										String traceToString = requirement.getRequirementTraceTo();
										if (traceToString != null){
											if (traceToString.contains(",")) {
												// more than one trace object in the trace field , so lets split them
												String[] traces = traceToString.split(",");
												for (int j  = 0; j < traces.length; j++) {
													
													try {

														// if you can get the requirement object, then print more details. If you hit exception print what you have
														
														String traceToReqFullTag = traces[j].replace("(s)", "");
														Requirement traceToReq = WordTemplateUtil.getReqFromRelatedProjects(project, traceToReqFullTag);
														

														url = ProjectUtil.getURL(request, traceToReq.getRequirementId(),"requirement");
														cleanUpFont(font);
														
														if (traces[j].contains("(s)")) {
															font.setColor(Color.RED);
														} else {
															font.setColor(Color.GREEN);
														}
														
														builder.insertHyperlink(traceToReqFullTag, url, false);
														cleanUpFont(font);
														builder.writeln(" : " + traceToReq.getRequirementName() + " ");
													}
													catch (Exception e){
														// if you run into exception, do the simple way. 
														e.printStackTrace();
														if (traces[j].contains("(s)")) {
															font.setColor(Color.RED);
															builder.writeln(traces[j] + " ");
														} else {
															font.setColor(Color.GREEN);
															builder.writeln(traces[j] + " ");
														}
													}
												}
											}
											else {
												// single trace object . so no need to split.
												
												try {

													// if you can get the requirement object, then print more details. If you hit exception print what you have
													
													String traceToReqFullTag = traceToString.replace("(s)","");
													Requirement traceToReq = WordTemplateUtil.getReqFromRelatedProjects(project, traceToReqFullTag);
													
													
													url = ProjectUtil.getURL(request, traceToReq.getRequirementId(),"requirement");
													cleanUpFont(font);
													
													
													if (traceToString.contains("(s)")) {
														font.setColor(Color.RED);
													} else {
														font.setColor(Color.GREEN);
														
													}
													builder.insertHyperlink(traceToReq.getRequirementFullTag(), url, false);
													cleanUpFont(font);
													builder.writeln(" : "+ traceToReq.getRequirementName() + " ");
													
												}
												catch (Exception e){
													// if you run into exception, do the simple way.
													e.printStackTrace();
													if (traceToString.contains("(s)")) {
														font.setColor(Color.RED);
														builder.writeln(traceToString + " ");
													} else {
														font.setColor(Color.GREEN);
														builder.writeln(traceToString + " ");
													}
												}
												
											}
											
										}
										font.setColor(Color.BLACK);
										font.setUnderline(0);
										font.setBold(false);
										builder.endRow();		
		
									}
		
									if ((displayAttributes != null) && (displayAttributes.contains("traceFrom"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.write("Trace From");
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
		
										// lets color code the traceFrom and traceFrom
										// values.
										String traceFromString = requirement.getRequirementTraceFrom();
										
										if (traceFromString != null){
											if (traceFromString.contains(",")) {
												// more than one trace object in the trace field , so lets split them
												String[] traces = traceFromString.split(",");
												for (int j = 0; j < traces.length; j++) {
													
													
													try {

														// if you can get the requirement object, then print more details. If you hit exception print what you have
														
														String traceFromReqFullTag = traces[j].replace("(s)", "");
														Requirement traceFromReq = WordTemplateUtil.getReqFromRelatedProjects(project, traceFromReqFullTag);
														
														url = ProjectUtil.getURL(request, traceFromReq.getRequirementId(),"requirement");
														cleanUpFont(font);
														
														if (traces[j].contains("(s)")) {
															font.setColor(Color.RED);
															
														} else {
															font.setColor(Color.GREEN);
														}
														
														builder.insertHyperlink(traceFromReqFullTag, url, false);
														cleanUpFont(font);
														builder.writeln(" : " + traceFromReq.getRequirementName() + " ");
														
													}
													catch (Exception e){
														// if you run into exception, do the simple way. 
														e.printStackTrace();
														if (traces[j].contains("(s)")) {
															font.setColor(Color.RED);
															builder.writeln(traces[j] + " ");
														} else {
															font.setColor(Color.GREEN);
															builder.writeln(traces[j] + " ");
														}
													}
													
												}
											}
											else {
												// single trace object . so no need to split.
												
												try {
													// if you can get the requirement object, then print more details. If you hit exception print what you have
													
													
													String traceFromReqFullTag = traceFromString.replace("(s)","");
													Requirement traceFromReq = WordTemplateUtil.getReqFromRelatedProjects(project, traceFromReqFullTag);
													
													

													url = ProjectUtil.getURL(request, traceFromReq.getRequirementId(),"requirement");
													cleanUpFont(font);
													
													if (traceFromString.contains("(s)")) {
														font.setColor(Color.RED);
													} else {
														font.setColor(Color.GREEN);
													}
													
													builder.insertHyperlink(traceFromReqFullTag, url, false);
													cleanUpFont(font);
													builder.writeln(" : "+ traceFromReq.getRequirementName() + " ");
												}
												catch (Exception e){
													// if you run into exception, do the simple way. 
													e.printStackTrace();
													if (traceFromString.contains("(s)")) {
														font.setColor(Color.RED);
														builder.writeln(traceFromString + " ");
													} else {
														font.setColor(Color.GREEN);
														builder.writeln(traceFromString + " ");
													}
												}
											}
										}
										font.setColor(Color.BLACK);
										font.setUnderline(0);
										font.setBold(false);
										builder.endRow();		
										
									}
		
									
									
									if ((displayAttributes != null) && (displayAttributes.contains("requirementBaselines"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.write("Baselines");
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
										builder.write(RequirementUtil.getRequirementBaselineString(requirement.getRequirementId(),  databaseType));
										builder.endRow();		
										
									}	
									
									
									if ((displayAttributes != null) && (displayAttributes.contains("comments"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.write("Comments");
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
										
										// lets get the comments and print them
										ArrayList comments = requirement.getRequirementComments(databaseType);
										Iterator c = comments.iterator();
										while (c.hasNext()){
											Comment comment = (Comment) c.next();
											builder.write(comment.getCommenterEmailId()+ " : " + comment.getCommentDate() + 
													" : " + comment.getComment_note());
											builder.writeln("");
											builder.writeln("");
										}
										builder.endRow();		
										
									}							
									
									
									if ((displayAttributes != null) && (displayAttributes.contains("folderPath"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.write("Folder Path");
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
										builder.write(requirement.getFolderPath());
										builder.endRow();		
										
									}							
									
									if ((displayAttributes != null) && (displayAttributes.contains("customAttributes"))) {
										// a typical uda looks like this
										// Customer:#: SBI:##:Delivery
										// Estimate:#:01/01/12
								
										
										
										String uda = requirement.getUserDefinedAttributes();
		
										if (uda != null) {
											
											String[] attribs = null;
										
											// lets split uda and put it in the attribs array.
											if (uda.contains(":##:")) {
												attribs = uda.split(":##:");
											}
											else {
												attribs = new String[1];
												attribs[0] = uda;
											}
										
											
											for (int k = 0; k < attribs.length; k++) {
												if ((attribs[k] != null) && (attribs[k].contains(":#:"))) {
													String[] attrib = attribs[k].split(":#:");
													
													// if the user  selected 'customAttributesNonEmpty', then we print the attribute name value row
													// only if a value exists in the attribute. Else the row is skipped
													if (displayAttributes.contains("customAttributesNonEmpty")){
														// if attrib value doesn't exist, then skip this row
													
														String attributeValue = "";
														try {
															attributeValue = attrib[1];
														}
														catch (Exception e){
															e.printStackTrace();
														}
														
														if (attributeValue == ""){
															//skip row
															continue;
														}
														
													}
													
													if (attrib.length > 0) {
														// attrib[0] exists. so lets
														// call it.
														// if this is a REPORTID-XXX type of link , then we print only those custom attributes
														// that were in the report
														if (report != null){
															if (!(reportDefinition.contains(attrib[0] + ","))){
																// skip printing this attribute value
																continue;
															}
															
														}
														builder.insertCell();
														cleanUpFont(font);
														setUpCell(builder, 80);
														builder.write(attrib[0]);
													}
													builder.insertCell();
													cleanUpFont(font);
													setUpCell(builder, 500);
													builder.write(" ");
													if (attrib.length > 1) {
														// attrib[1] exists. so lets
														// call it.
														builder.write(attrib[1]);
													}
													builder.endRow();		
		
												}
											}
										} 
									}
									
		
									if ((displayAttributes != null) && (displayAttributes.contains("name"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.write("Name");
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
										if (formatBoldAttribute.toLowerCase().contains("name")){
											builder.setBold(true);
										}
										builder.write(requirement.getRequirementName());
										builder.endRow();	
										
									}
		
		
		
									if ((displayAttributes != null) && (displayAttributes.contains("description"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 80);
										builder.write("Description");
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 500);
										if (formatBoldAttribute.toLowerCase().contains("description")){
											builder.setBold(true);
										}
										builder.insertHtml(requirement.getRequirementDescription());
										builder.endRow();	
										
									}
									
									if ((displayAttributes != null) && (displayAttributes.contains("individuallySelectedCA_"))) {
										// lets split them by , and then find all the attribute names. 
										// at this point, the displayAttributes looks like this 
										// name,individuallySelectedCA_Agile Effort Remaining (hrs),individuallySelectedCA_Agile Sprint,individuallySelectedCA_Agile Task Status,individuallySelectedCA_Agile Task Weight,individuallySelectedCA_Agile Total Effort (hrs),individuallySelectedCA_Amdocs_Work_Estimate,
										String [] fieldsToDisplay = displayAttributes.split(",");
										for (String field : fieldsToDisplay ){
											if (field.contains("individuallySelectedCA_")){
												// selected custom attribute
												String selectedAttribute = field.replace("individuallySelectedCA_", "");
												if (requirement.getUserDefinedAttributes().contains(selectedAttribute + ":#:")){
													String selectedAttributeValue = requirement.getAttributeValue(selectedAttribute);
													
													
													builder.insertCell();
													cleanUpFont(font);
													setUpCell(builder, 80);
													builder.write(selectedAttribute);
													builder.insertCell();
													cleanUpFont(font);
													setUpCell(builder, 500);
													
													builder.write(selectedAttributeValue);
													builder.endRow();
												}
											}
										}
										
									}
									
									if ((displayAttributes != null) && (displayAttributes.contains("fileAttachments"))) {
										ArrayList attachments = requirement.getRequirementAttachments(databaseType);
										// lets iterate through all the attachments and print them out.
										Iterator a = attachments.iterator();
										while (a.hasNext()){
											RequirementAttachment attachment = (RequirementAttachment) a.next();
											builder.insertCell();
											cleanUpFont(font);
											setUpCell(builder, 80);
											builder.write("Attachment");
				
											Cell cell = builder.insertCell();
											builder.writeln(attachment.getTitle());
											builder.writeln("");
											cleanUpFont(font);
											setUpCell(builder, 500);
											if (
													(attachment.getFileName().toLowerCase().endsWith(".jpg"))
													||
													(attachment.getFileName().toLowerCase().endsWith(".jpeg"))
													||
													(attachment.getFileName().toLowerCase().endsWith(".jpe"))
													||
													(attachment.getFileName().toLowerCase().endsWith(".jfif"))
													||
													(attachment.getFileName().toLowerCase().endsWith(".gif"))
													||
													(attachment.getFileName().toLowerCase().endsWith(".tif"))
													||
													(attachment.getFileName().toLowerCase().endsWith(".tiff"))
													||
													(attachment.getFileName().toLowerCase().endsWith(".png"))
												){
												// this is an image file, so lets display the image
												
												FileInputStream input = new FileInputStream(attachment.getFilePath());
												
												Shape shape = builder.insertImage(input);
												double shapeWidth = shape.getWidth();
												double cellWidth = cell.getCellFormat().getWidth();
												
												if (shapeWidth > cellWidth){
													// if the image is larger than the cell, lets shrink its height and weight by a proportion .
													shape.setWidth(shape.getWidth() * (cellWidth / shapeWidth));
													shape.setHeight(shape.getHeight() * (cellWidth / shapeWidth));
												}
												else {
													shape.setWidth(cell.getCellFormat().getWidth());
												}
												builder.endRow();
											}
											else {
												// this is not an image file, so lets display a download link to the file.
												font.setColor(Color.BLUE);
												font.setUnderline(Underline.SINGLE);
												url = "https://" + request.getServerName() +  
									       				"/GloreeJava2/servlet/DisplayAction?dO=attachment&dAttachmentId=" + attachment.getRequirementAttachmentId() ;
												// Insert the link.
												builder.insertHyperlink(attachment.getFileName(), url, false);
												// Revert to default formatting.
												font.setColor(Color.BLACK);
												font.setUnderline(0);
												builder.endRow();
											}
	
											
										}
										
										
									}
									
									
									builder.endTable();
									builder.writeln("");
									// if there are any excel files, we want to display the contents as a table in word
									// sinc we don't want to embed them as part of another table, we are printing them out again.
									
									if ((displayAttributes != null) && (displayAttributes.contains("fileAttachments"))) {
										ArrayList attachments = requirement.getRequirementAttachments(databaseType);
										// lets iterate through all the attachments and print them out.
										Iterator a = attachments.iterator();
										while (a.hasNext()){
											RequirementAttachment attachment = (RequirementAttachment) a.next();
											cleanUpFont(font);
											
											if (attachment.getFileName().toLowerCase().endsWith(".xls")){
												// this is an Excel97-2003  file, so lets print the first sheet of data.
												builder.writeln("Attachment Title :" + attachment.getTitle());
												builder.writeln("Attachment Name :" + attachment.getFileName());
												
												embedXLSData(builder, attachment, maxColumnsOfExcelToEmbedInWord);
												builder.endRow();
											}
											else if (attachment.getFileName().toLowerCase().endsWith(".xlsx")){
												// this is an Excel 2003+ XML file, so lets print the first sheet of data.
												builder.writeln("Attachment Title :" + attachment.getTitle());
												builder.writeln("Attachment Name :" + attachment.getFileName());
												
												embedXLSXData(builder, attachment, maxColumnsOfExcelToEmbedInWord);
												builder.endRow();
											}											
											else {
												// do nothing
												continue;
											}
	
											
										}
										
										
									}
									
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				
					// if there were requirements generated from this tag, then lets remove this node from the report
					// if we are here, this means this link had data.

					// lets make an attempt to remove the node
					

				}
				
			}
			

			System.out.println("TM : inside generateReqPerTabe Report  - completed document " );
	
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
			Calendar cal = Calendar.getInstance();
			String today = sdf.format(cal.getTime());
			filename = user.getFirstName() + " " + user.getLastName()  + " Report " + today;
			filename.replace(' ', '_');


			// lets see what the output format requested is.
			String reportFormat = request.getParameter("reportFormat");
			
			// we used to support docx, html and pdf output formats
			// ran into trouble, and didn't see the value in debugging
			// so pulled them out. 
			// can re write that code later. User reportFormat param for that.
			if (reportFormat.contains("doc")) {
				filename += ".docx";
				

				System.out.println("TM : inside generateReqPerTabe Report  - fileName is  " + filename);
	    		if (exportType.equals("HTML")){
	    			ServletOutputStream out = response.getOutputStream();
	    			response.setHeader("Expires", "0");
	    			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
	    			response.setHeader("Pragma", "public");
					response.setContentType("application/msword");
					response.setHeader("Content-Disposition",
							"attachment; filename=\"" + filename + "\"");
					doc.save(out, SaveFormat.DOCX);
					
					

					System.out.println("TM : inside generateReqPerTabe Report  - expotType is HTML " );
	    		}
		    	if (exportType.equals("file")){
		    		// if rootDataDirectory/TraceCloud does not exist, lets create it.
		    		File traceCloudRoot = new File (rootDataDirectory + File.separator +  "TraceCloud");
		    		if (!(traceCloudRoot.exists() )){
		    		    new File(rootDataDirectory + File.separator +  "TraceCloud").mkdir();
		    		}

		    		// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
		    		File tempFolderRoot  = new File (rootDataDirectory +File.separator +  "TraceCloud" + File.separator + "Temp");
		    		if (!(tempFolderRoot.exists() )){
		    			new File(rootDataDirectory + File.separator +  "TraceCloud" + File.separator + "Temp").mkdir();
		    		}

		    		filename = rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  "Temp" + File.separator +  filename;
		    		FileOutputStream fileOut = new FileOutputStream(filename);

		    		doc.save(fileOut, SaveFormat.DOCX);
					fileOut.flush();
					fileOut.close();
					

					System.out.println("TM : inside generateReqPerTabe Report  - exportType is file  "  + filename);
		    	}

			}

		} catch (Exception e) {

		}
		

		System.out.println("TM : inside generateReqPerTabe Report  - returning filename  " + filename);
		return filename;
	}

	
	
	
	// goes through the document, and parses every hyperlink and 
	// fills up the document with the collateral about the requirements mentioned
	// in the hyperlink
	// This routine generates a separate table Row for each Requirement.
	public static String generateReqPerTableRowReport(SecurityProfile securityProfile, int templateId, Project project,
			String selectedDisplayAttributes, User user, HttpServletRequest request,
			HttpServletResponse response,String rootDataDirectory, String exportType, String databaseType) {
		String filename = "";
		try {
			
			String formatBoldAttribute = request.getParameter("formatBoldAttribute");
			if (formatBoldAttribute==null){formatBoldAttribute="";}
			
			WordTemplate wordTemplate = new WordTemplate(templateId,  databaseType);

			Document doc = new Document(wordTemplate.getTemplateFilePath());
			DocumentBuilder builder = new DocumentBuilder(doc);

			// Note : 
			// since different req types can exist in the same doc, and since each req types can have different custom attributes, 
			// we need to print different req types in different tables. 
			// The problem is we don't know when the req type can change. So we use lastReqTypePrinted to keep track of the last req type printed
			// and compare with any new req we are printing and if different , end the prev table and start a new one, with headers etc...
			
			
			// Hyperlinks in a Word documents are fields, select all field start
			// nodes so we can find the hyperlinks.
			NodeList fieldStarts = doc.selectNodes("//FieldStart");
			Iterator fs = fieldStarts.iterator();
			
			while (fs.hasNext()) {
				String displayAttributes = selectedDisplayAttributes;
				
				Node node = (Node) fs.next();
				FieldStart fieldStart = (FieldStart) node;
				
				if (fieldStart.getFieldType() == FieldType.FIELD_HYPERLINK) {
					
					
					// The field is a hyperlink field, use the "facade" class to
					// help to deal with the field.
					Hyperlink hyperlink = null;
					try {
						hyperlink = new Hyperlink(fieldStart);
						
					}
					catch (Exception e){
						// since we hit an exception, lets ignore this link
						e.printStackTrace();
						continue;
					}
					
					if (hyperlink == null){
						continue;
					}
					
					
					// Under some circumstances, the Target field is getting mucked up by Word
					// on the client's machine. while this is not strictly a tracecloud problem,
					// we are trying to see if the getName works better, as Word does not seem to 
					// be messing around with the name.
					//String urlString = hyperlink.getTarget();
					String urlString = hyperlink.getName();
					// NOTE : for some reason we are getting a weird character (Paragraph)
					// or DC4 when viewed in Notepad++
					// so if the length is > 0, then lets drop the first char.
					if ((urlString != null)&& (urlString.length() > 1)){
						urlString = urlString.substring(1);
					}
					
					
					// urlString contains the information we need to parse and
					// build out.
					if (urlString == null) {
						// if urlString is null, skip this row.
						continue;
					}
					ArrayList requirements = WordTemplateUtil.getRequirementsFromURLString(securityProfile, project.getProjectId(), urlString,  databaseType);
					
					

					Report report = null;
					try {
						report = WordTemplateUtil.getReportFromURLString(securityProfile, project.getProjectId(), urlString,  databaseType);
					}
					catch (Exception e ){
						e.printStackTrace();
					}
					
					String reportDefinition = "";
					if (report != null){
						reportDefinition = report.getReportDefinition();
					}
					
					
					
					if (requirements.size() == 0) {
						// no requirements were found for this hyperlink.
						// so lets skip this one.
						continue;
					}
					
					builder.moveTo(node);
					
					// if this hyperlink had led to any requirements being generated, lets drop the link
					if (requirements.size() > 0 ){
						hyperlink.setName(" ");
						hyperlink.setTarget(" ");
					}
					
					
					Font font = builder.getFont();
					// for some reason, smallcaps are being set to On. So we are 
					// physically turnign them off.
					//font.setSmallCaps(false);
					
					
					Iterator i = requirements.iterator();
					
					// we use the lastPrintedReqTypeNameInThisURL flag to tell us when a new URL requirements are starting.
					// if its empty, that means we are starting a new URL string .
					// if non empty, it means that we printed a req in the last iteration
					// we use it to compare with the current req's req type name, and if different , start printing a new table.
					String lastPrintedReqTypeNameInThisURL = "";
					
					// we use this to indicate if a table is currently open.
					
					
					while (i.hasNext()) {
						try {
							Requirement requirement = (Requirement) i.next();
							
							
							
							// If this was an embeded requirement attribute (like BR-1:name)
							// then we print this differently than the embedded Requirement (like BR-1)
							// this is beacuase embedded REqs are printed as tables where as with 
							// embedded attribs we just print the value.
							if ((urlString != null) &&(urlString.contains(":"))){
								
								// if the user does not have read permissions on this requirement,
								// lets redact it. i.e. remove all sensitive infor from it.
								if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
										+ requirement.getFolderId()))){
									requirement.redact();
								}
								
								
								// lets knock out the hyperlink so that it doesn't get displayed.
								hyperlink.setName("");
								hyperlink.setTarget("");
								// this is a single attribute print out.
								String [] display = urlString.split(":");
								displayAttributes = display[1];
								String printableValue = requirement.getElement(displayAttributes);
								// lets put a space after each ,for traceTo and traceFrom values
								if (displayAttributes.equals("traceto") || displayAttributes.equals("tracefrom")){
									if (printableValue == null){printableValue = "";}
									if (printableValue.contains(",")){
										printableValue = printableValue.replace(",", ", ");
									}
								}
								
								
								
								if (displayAttributes.trim().toLowerCase().equals("name")){
									// if we were asked to pring BR-1:name, then we will also print a link to the requirement
									String url = ProjectUtil.getURL(request, requirement.getRequirementId(),"requirement");
									cleanUpFont(font);
									font.setColor(Color.BLUE);
									font.setUnderline(1);
									builder.insertHyperlink(requirement.getRequirementFullTag(), url, false);
									cleanUpFont(font);
									builder.write(" : ");	
								}

								
								if (displayAttributes.trim().toLowerCase().equals("description")){
									if (formatBoldAttribute.toLowerCase().contains("description")){
										builder.setBold(true);
									}
									builder.insertHtml(requirement.getRequirementDescription());
								}
								else{

									if (formatBoldAttribute.toLowerCase().contains("name")){
										builder.setBold(true);
									}
									builder.write(printableValue);
								}
							}
							else {
								// this is a regular Requirement. so lets print the 
								// display values picked by the user. 
								
								if (requirement.getDeleted() == 0 ){
									font = builder.getFont();
									
									if (!(requirement.getRequirementTypeName().equals(lastPrintedReqTypeNameInThisURL))){
										// Our req type has changed. so lets reset the last req type name and start  a new table.
										
										
										if (lastPrintedReqTypeNameInThisURL.equals("")){
											// this means we are encountering the first req in this URL string. So we don't need to end the previous table.
											// lets start a new table
											builder.writeln("");
											try {
												builder.startTable();
											}
											catch (Exception e){
												e.printStackTrace();
											}
											
											
										}
										else {
											// this means that this is not the first req in this URL string and the req type has changed.
											// Hence we need to end the previous table.
										
											
											try {
												builder.endTable();
											}
											catch (Exception e){
												e.printStackTrace();
											}
											
											// lets start a new table
											builder.writeln("");
											try {
												builder.startTable();
											}
											catch (Exception e){
												e.printStackTrace();
											}
											
										}
										lastPrintedReqTypeNameInThisURL = requirement.getRequirementTypeName();
										
										// lets print the column header row.
										
										printColumnHeaderCell(font, 60, "Tag", builder );
										
										
										if ((displayAttributes != null) && (displayAttributes.contains("name"))) {
											printColumnHeaderCell(font, 200, "Name", builder );
										}
										
										if ((displayAttributes != null) && (displayAttributes.contains("description"))) {
											printColumnHeaderCell(font, 400, "Description", builder );
										}
										if ((displayAttributes != null) && (displayAttributes.contains("type"))) {
											printColumnHeaderCell(font, 60, "Type", builder );
											
										}
										
										
										if ((displayAttributes != null) && (displayAttributes.contains("approvalStatus"))) {
											printColumnHeaderCell(font, 60, "Approval Status", builder );
										}
										if ((displayAttributes != null) && (displayAttributes.contains("testingStatus"))) {
											printColumnHeaderCell(font, 60, "Testing Status", builder );
										}
										if ((displayAttributes != null)&& (displayAttributes.contains("version"))) {
											printColumnHeaderCell(font, 60, "Version", builder );
										}
										if ((displayAttributes != null) && (displayAttributes.contains("priority"))) {
											printColumnHeaderCell(font, 60, "Priority", builder );
											
										}
										if ((displayAttributes != null) && (displayAttributes.contains("owner"))) {
											printColumnHeaderCell(font, 60, "Owner", builder );
											
										}
										
										if ((displayAttributes != null) && (displayAttributes.contains("pctComplete"))) {
											printColumnHeaderCell(font, 60, "Percent Complete", builder );
											
										}
										
										if ((displayAttributes != null) && (displayAttributes.contains("externalURL"))) {
											printColumnHeaderCell(font, 60, "External URL", builder );
											
										}
										
										if ((displayAttributes != null) && (displayAttributes.contains("approvers"))) {
											printColumnHeaderCell(font, 60, "Approvers", builder );
											
										}
										
										if ((displayAttributes != null) && (displayAttributes.contains("approvalDate"))) {
											printColumnHeaderCell(font, 60, "Approval Date", builder );
											
										}
										
										if ((displayAttributes != null) && (displayAttributes.contains("url"))) {
											printColumnHeaderCell(font, 60, "URL", builder );
											
										}
										
										if ((displayAttributes != null) && (displayAttributes.contains("traceTo"))) {
											printColumnHeaderCell(font, 200, "Trace To", builder );
											
										}
										
										if ((displayAttributes != null) && (displayAttributes.contains("traceFrom"))) {
											printColumnHeaderCell(font, 200, "Trace From", builder );
											
										}
										
										if ((displayAttributes != null) && (displayAttributes.contains("requirementBaselines"))) {
											printColumnHeaderCell(font, 60, "Baselines", builder );
											
										}							
										
										if ((displayAttributes != null) && (displayAttributes.contains("comments"))) {
											printColumnHeaderCell(font, 60, "Comments", builder );
											
										}
										if ((displayAttributes != null) && (displayAttributes.contains("folderPath"))) {
											printColumnHeaderCell(font, 60, "Folder Path", builder );
											
										}							
										
										if ((displayAttributes != null) && (displayAttributes.contains("customAttributes"))) {
											// a typical uda looks like this
											// Customer:#: SBI:##:Delivery
											// Estimate:#:01/01/12
											
											
											
											String uda = requirement.getUserDefinedAttributes();
											
											if (uda != null) {
												
												String[] attribs = null;
												
												// lets split uda and put it in the attribs array.
												if (uda.contains(":##:")) {
													attribs = uda.split(":##:");
												}
												else {
													attribs = new String[1];
													attribs[0] = uda;
												}
												
												
												for (int k = 0; k < attribs.length; k++) {
													if ((attribs[k] != null) && (attribs[k].contains(":#:"))) {
														String[] attrib = attribs[k].split(":#:");
														String attribTitle = " ";
														if (attrib.length > 0) {
															// attrib[0] exists. so lets
															// call it.
															attribTitle = attrib[0];
														}
														
														// if this is a REPORTID-XXX type of link , then we print only those custom attributes
														// that were in the report
														if (report != null){
															if (!(reportDefinition.contains(attrib[0] + ","))){
																// skip printing this attribute value
																continue;
															}
															
														}
														printColumnHeaderCell(font, 60, attribTitle, builder );
														
													}
												}
											} 
										}
										
										if ((displayAttributes != null) && (displayAttributes.contains("fileAttachments"))) {
											printColumnHeaderCell(font, 150, "File Attachments", builder );
											
										}
										
										// Lets print the user selected custom attributes in the Display Attributes.
										
										
										
										if ((displayAttributes != null) && (displayAttributes.contains("individuallySelectedCA_"))) {
											// lets split them by , and then find all the attribute names. 
											// at this point, the displayAttributes looks like this 
											// name,individuallySelectedCA_Agile Effort Remaining (hrs),individuallySelectedCA_Agile Sprint,individuallySelectedCA_Agile Task Status,individuallySelectedCA_Agile Task Weight,individuallySelectedCA_Agile Total Effort (hrs),individuallySelectedCA_Amdocs_Work_Estimate,
											String [] fieldsToDisplay = displayAttributes.split(",");
											for (String field : fieldsToDisplay ){
												if (field.contains("individuallySelectedCA_")){
													// selected custom attribute
													String selectedAttribute = field.replace("individuallySelectedCA_", "");
													if (requirement.getUserDefinedAttributes().contains(selectedAttribute + ":#:")){
														printColumnHeaderCell(font, 60, selectedAttribute, builder );
													}
												}
											}
											
										}
										// now lets end the row.
										builder.endRow();
										// at this point, we have printed the header row for the new table.
									}
									
									
									lastPrintedReqTypeNameInThisURL = requirement.getRequirementTypeName();
									// now lets print the data rows.
									
									// if the user does not have read permissions on this requirement,
									// lets redact it. i.e. remove all sensitive infor from it.
									if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
											+ requirement.getFolderId()))){
										requirement.redact();
									}
									
									
									
									builder.insertCell();
									// prevents the row from breaking across pages
									builder.getRowFormat().setAllowBreakAcrossPages(false);
									
									
									
									
									cleanUpFont(font);
									setUpCell(builder, 60);
									//builder.write(requirement.getRequirementFullTag());
									
									// any time name is selected, lets print the URL to the req

									String url = ProjectUtil.getURL(request, requirement.getRequirementId(),"requirement");
									cleanUpFont(font);
									font.setColor(Color.BLUE);
									font.setUnderline(1);
									builder.insertHyperlink(requirement.getRequirementFullTag(), url, false);
									cleanUpFont(font);
									
									
									
									
									
									
									if ((displayAttributes != null) && (displayAttributes.contains("name"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 200);
										if (formatBoldAttribute.toLowerCase().contains("name")){
											builder.setBold(true);
										}
										builder.write(requirement.getRequirementName());
									}
									
									
									if ((displayAttributes != null) && (displayAttributes.contains("description"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 400);
										if (formatBoldAttribute.toLowerCase().contains("description")){
											builder.setBold(true);
										}
										Paragraph paragraph = builder.getCurrentParagraph();
										// insertHTML is adding a new line after the text. this may remove it.
										paragraph.getParagraphFormat().setSpaceAfterAuto(false);
										paragraph.getParagraphFormat().setSpaceAfter(0);
										builder.insertHtml(requirement.getRequirementDescription());
										// insertHTML is adding a new line after the text. this may remove it.
										paragraph.getParagraphFormat().setSpaceAfterAuto(false);
										paragraph.getParagraphFormat().setSpaceAfter(0);
									}
									
									
									
									if ((displayAttributes != null) && (displayAttributes.contains("type"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 60);
										builder.write(requirement.getRequirementTypeName());
									}
									
									
									
									if ((displayAttributes != null) && (displayAttributes.contains("approvalStatus"))) {
										if (requirement.getApprovalStatus().equals("Draft")) {
											font.setHighlightColor(Color.MAGENTA);
										}
										if (requirement.getApprovalStatus().equals("In Approval WorkFlow")) {
											font.setHighlightColor(Color.blue);
										}
										if (requirement.getApprovalStatus().equals("Approved")) {
											font.setHighlightColor(Color.GREEN);
										}
										if (requirement.getApprovalStatus().equals("Rejected")) {
											font.setHighlightColor(Color.RED);
										}
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 60);
										builder.write(requirement.getApprovalStatus());
										font.setHighlightColor(Color.WHITE);
									}
									if ((displayAttributes != null) && (displayAttributes.contains("testingStatus"))) {
										String testingStatus = requirement.getTestingStatus();
										if (testingStatus.equals("Pending")) {
											font.setHighlightColor(Color.YELLOW);
										}
										if (testingStatus.equals("Pass")) {
											font.setHighlightColor(Color.GREEN);
										}
										if (testingStatus.equals("Fail")) {
											font.setHighlightColor(Color.RED);
										}
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 60);
										builder.write(testingStatus);
										font.setHighlightColor(Color.WHITE);
									}
									
									if ((displayAttributes != null)&& (displayAttributes.contains("version"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 60);
										builder.write(Integer.toString(requirement.getVersion()));
									}
									if ((displayAttributes != null) && (displayAttributes.contains("priority"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 60);
										builder.write(requirement.getRequirementPriority());
									}
									
									if ((displayAttributes != null) && (displayAttributes.contains("owner"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 60);
										builder.write(requirement.getRequirementOwner());
									}
									
									if ((displayAttributes != null) && (displayAttributes.contains("pctComplete"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 60);
										builder.write(Integer.toString(requirement.getRequirementPctComplete()) + " %");
									}
									
									if ((displayAttributes != null) && (displayAttributes.contains("externalURL"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 60);
										builder.write(requirement.getRequirementExternalUrl());
									}
									
									if ((displayAttributes != null) && (displayAttributes.contains("approvers"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 60);
										String approversString = requirement.getApprovers();
										if ((approversString != null)
												&& (approversString.contains(","))) {
											String[] approvers = approversString
											.split(",");
											for (int k = 0; k < approvers.length; k++) {
												String approver = approvers[k];
												if ((approver != null)
														&& (approver.contains("(P)"))) {
													font.setColor(Color.PINK);
												}
												if ((approver != null)
														&& (approver.contains("(A)"))) {
													font.setColor(Color.GREEN);
												}
												if ((approver != null)
														&& (approver.contains("(R)"))) {
													font.setColor(Color.RED);
												}
												builder.write(approver + " ");
											}
										}
									}
									
									if ((displayAttributes != null) && (displayAttributes.contains("approvalDate"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 60);
										builder.write(requirement.getApprovedByAllDt());
									}
									
									if ((displayAttributes != null) && (displayAttributes.contains("url"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 60);
										builder.getCellFormat().setWidth(60);
										builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
										builder.getCellFormat().getBorders().setLineStyle(LineStyle.SINGLE);
										
										
										
										font.setColor(Color.BLUE);
										font.setUnderline(Underline.SINGLE);								
										url = ProjectUtil.getURL(request,requirement.getRequirementId(),"requirement");
										// Insert the link.
										builder.insertHyperlink(url, url, false);
										// Revert to default formatting.
										font.setColor(Color.BLACK);
										font.setUnderline(0);
									}
									
									if ((displayAttributes != null) && (displayAttributes.contains("traceTo"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 200);
										
										
										// lets color code the traceTo and traceFrom
										// values.
										String traceToString = requirement.getRequirementTraceTo();
										if (traceToString != null){
											if (traceToString.contains(",")) {
												// trace object has more than one value. so we need to split
												String[] traces = traceToString.split(",");
												for (int j = 0; j < traces.length; j++) {
													// lets find the requirement 
													// todo : this works only if the req is in same project
													try {

														// if you can get the requirement object, then print more details. If you hit exception print what you have
														Requirement traceToReq = new Requirement(traces[j].replace("(s)", "") , project.getProjectId(), databaseType);
														

														url = ProjectUtil.getURL(request, traceToReq.getRequirementId(),"requirement");
														cleanUpFont(font);
														
														if (traces[j].contains("(s)")) {
															font.setColor(Color.RED);
														} else {
															font.setColor(Color.GREEN);
														}
														
														builder.insertHyperlink(traceToReq.getRequirementFullTag(), url, false);
														cleanUpFont(font);
														builder.writeln(" : " + traceToReq.getRequirementName() + " ");
													}
													catch (Exception e){
														// if you run into exception, do the simple way. 
														e.printStackTrace();
														if (traces[j].contains("(s)")) {
															font.setColor(Color.RED);
															builder.writeln(traces[j] + " ");
														} else {
															font.setColor(Color.GREEN);
															builder.writeln(traces[j] + " ");
														}
													}
													
												}
											}
											else {
												// trace object has a single value. so no need to split.
												
												
												try {

													// if you can get the requirement object, then print more details. If you hit exception print what you have
													Requirement traceToReq = new Requirement(traceToString.replace("(s)","") , project.getProjectId(), databaseType);
													
													url = ProjectUtil.getURL(request, traceToReq.getRequirementId(),"requirement");
													cleanUpFont(font);
													
													
													if (traceToString.contains("(s)")) {
														font.setColor(Color.RED);
													} else {
														font.setColor(Color.GREEN);
														
													}
													builder.insertHyperlink(traceToReq.getRequirementFullTag(), url, false);
													cleanUpFont(font);
													builder.writeln(" : "+ traceToReq.getRequirementName() + " ");
													
												}
												catch (Exception e){
													// if you run into exception, do the simple way. 
													e.printStackTrace();
													if (traceToString.contains("(s)")) {
														font.setColor(Color.RED);
														builder.writeln(traceToString + " ");
													} else {
														font.setColor(Color.GREEN);
														builder.writeln(traceToString + " ");
													}
												}
												
												
											}
										}
										
										
										font.setColor(Color.BLACK);
										font.setUnderline(0);
										font.setBold(false);
									}
									
									if ((displayAttributes != null) && (displayAttributes.contains("traceFrom"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 200);
										
										// lets color code the traceFrom and traceFrom
										// values.
										String traceFromString = requirement.getRequirementTraceFrom();
										if (traceFromString != null){
											if (traceFromString.contains(",")) {
												// trace object has more than one value. so we need to split
												String[] traces = traceFromString.split(",");
												for (int j = 0; j < traces.length; j++) {
													try {

														// if you can get the requirement object, then print more details. If you hit exception print what you have
														Requirement traceFromReq = new Requirement(traces[j].replace("(s)","") , project.getProjectId(), databaseType);

														url = ProjectUtil.getURL(request, traceFromReq.getRequirementId(),"requirement");
														cleanUpFont(font);
														
														if (traces[j].contains("(s)")) {
															font.setColor(Color.RED);
															
														} else {
															font.setColor(Color.GREEN);
														}
														
														builder.insertHyperlink(traceFromReq.getRequirementFullTag(), url, false);
														cleanUpFont(font);
														builder.writeln(" : " + traceFromReq.getRequirementName() + " ");
														
													}
													catch (Exception e){
														// if you run into exception, do the simple way. 
														e.printStackTrace();
														if (traces[j].contains("(s)")) {
															font.setColor(Color.RED);
															builder.writeln(traces[j] + " ");
														} else {
															font.setColor(Color.GREEN);
															builder.writeln(traces[j] + " ");
														}
													}
													
												}
											}
											else {
												// trace object has a single value. so no need to split.
												
												
												try {
													// if you can get the requirement object, then print more details. If you hit exception print what you have
													Requirement traceFromReq = new Requirement(traceFromString.replace("(s)","") , project.getProjectId(), databaseType);
													

													url = ProjectUtil.getURL(request, traceFromReq.getRequirementId(),"requirement");
													cleanUpFont(font);
													
													if (traceFromString.contains("(s)")) {
														font.setColor(Color.RED);
													} else {
														font.setColor(Color.GREEN);
													}
													
													builder.insertHyperlink(traceFromReq.getRequirementFullTag(), url, false);
													cleanUpFont(font);
													builder.writeln(" : "+ traceFromReq.getRequirementName() + " ");
												}
												catch (Exception e){
													// if you run into exception, do the simple way. 
													e.printStackTrace();
													if (traceFromString.contains("(s)")) {
														font.setColor(Color.RED);
														builder.writeln(traceFromString + " ");
													} else {
														font.setColor(Color.GREEN);
														builder.writeln(traceFromString + " ");
													}
												}
												
											}
										}
										font.setColor(Color.BLACK);
										font.setUnderline(0);
										font.setBold(false);
									}
									
									
									
									if ((displayAttributes != null) && (displayAttributes.contains("requirementBaselines"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 60);
										builder.write(RequirementUtil.getRequirementBaselineString(requirement.getRequirementId(),  databaseType));
									}
									
									
									
									if ((displayAttributes != null) && (displayAttributes.contains("comments"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 60);
										// lets get the comments and print them
										ArrayList comments = requirement.getRequirementComments(databaseType);
										Iterator c = comments.iterator();
										while (c.hasNext()){
											Comment comment = (Comment) c.next();
											builder.write(comment.getCommenterEmailId()+ " : " + comment.getCommentDate() + 
													" : " + comment.getComment_note());
											builder.writeln("");
											builder.writeln("");
										}
									}
									
									if ((displayAttributes != null) && (displayAttributes.contains("folderPath"))) {
										builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 60);
										builder.write(requirement.getFolderPath());
									}							
									
									if ((displayAttributes != null) && (displayAttributes.contains("customAttributes"))) {
										// a typical uda looks like this
										// Customer:#: SBI:##:Delivery
										// Estimate:#:01/01/12
										
										
										
										String uda = requirement.getUserDefinedAttributes();
										
										if (uda != null) {
											
											String[] attribs = null;
											
											// lets split uda and put it in the attribs array.
											if (uda.contains(":##:")) {
												attribs = uda.split(":##:");
											}
											else {
												attribs = new String[1];
												attribs[0] = uda;
											}
											
											
											for (int k = 0; k < attribs.length; k++) {
												if ((attribs[k] != null) && (attribs[k].contains(":#:"))) {
													String[] attrib = attribs[k].split(":#:");
													// if this is a REPORTID-XXX type of link , then we print only those custom attributes
													// that were in the report
													if (report != null){
														if (!(reportDefinition.contains(attrib[0] + ","))){
															// skip printing this attribute value
															continue;
														}
														
													}
													builder.insertCell();
													cleanUpFont(font);
													setUpCell(builder, 60);
													builder.write(" ");
													if (attrib.length > 1) {
														// attrib[1] exists. so lets
														// call it.
														
														builder.write(attrib[1]);
													}
												}
											}
										} 
									}
									
									if ((displayAttributes != null) && (displayAttributes.contains("fileAttachments"))) {
										Cell cell = builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 150);
										ArrayList attachments = requirement.getRequirementAttachments(databaseType);
										// lets iterate through all the attachments and print them out.
										Iterator a = attachments.iterator();
										while (a.hasNext()){
											RequirementAttachment attachment = (RequirementAttachment) a.next();
											builder.writeln(attachment.getTitle());
											builder.writeln("");
											if (
													(attachment.getFileName().toLowerCase().endsWith(".jpg"))
													||
													(attachment.getFileName().toLowerCase().endsWith(".jpeg"))
													||
													(attachment.getFileName().toLowerCase().endsWith(".jpe"))
													||
													(attachment.getFileName().toLowerCase().endsWith(".jfif"))
													||
													(attachment.getFileName().toLowerCase().endsWith(".gif"))
													||
													(attachment.getFileName().toLowerCase().endsWith(".tif"))
													||
													(attachment.getFileName().toLowerCase().endsWith(".tiff"))
													||
													(attachment.getFileName().toLowerCase().endsWith(".png"))
												){
												// this is an image file, so lets display the image
												
												FileInputStream input = new FileInputStream(attachment.getFilePath());
												
												Shape shape = builder.insertImage(input);
												double shapeWidth = shape.getWidth();
												double cellWidth = cell.getCellFormat().getWidth();
												
												if (shapeWidth > cellWidth){
													// if the image is larger than the cell, lets shrink its height and weight by a proportion .
													shape.setWidth(shape.getWidth() * (cellWidth / shapeWidth));
													shape.setHeight(shape.getHeight() * (cellWidth / shapeWidth));
												}
												else {
													shape.setWidth(cell.getCellFormat().getWidth());
												}
												builder.writeln();
											}
											else {
												// this is not an image file, so lets display a download link to the file.
												font.setColor(Color.BLUE);
												font.setUnderline(Underline.SINGLE);
												url = "https://" + request.getServerName() +  
									       				"/GloreeJava2/servlet/DisplayAction?dO=attachment&dAttachmentId=" + attachment.getRequirementAttachmentId() ;
												// Insert the link.
												builder.insertHyperlink(attachment.getFileName(), url, false);
												// Revert to default formatting.
												font.setColor(Color.BLACK);
												font.setUnderline(0);
												builder.writeln();
											}
										}
									}	
									
									
									if ((displayAttributes != null) && (displayAttributes.contains("individuallySelectedCA_"))) {
										// lets split them by , and then find all the attribute names. 
										// at this point, the displayAttributes looks like this 
										// name,individuallySelectedCA_Agile Effort Remaining (hrs),individuallySelectedCA_Agile Sprint,individuallySelectedCA_Agile Task Status,individuallySelectedCA_Agile Task Weight,individuallySelectedCA_Agile Total Effort (hrs),individuallySelectedCA_Amdocs_Work_Estimate,
										String [] fieldsToDisplay = displayAttributes.split(",");
										for (String field : fieldsToDisplay ){
											if (field.contains("individuallySelectedCA_")){
												// selected custom attribute
												String selectedAttribute = field.replace("individuallySelectedCA_", "");
												String selectedAttributeValue = requirement.getAttributeValue(selectedAttribute);
												if (requirement.getUserDefinedAttributes().contains(selectedAttribute + ":#:")){
													builder.insertCell();
													cleanUpFont(font);
													setUpCell(builder, 60);
													builder.write(" ");
													builder.write(selectedAttributeValue);
												}
											}
										}
										
									}
									builder.endRow();	
								}
								
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// since we always start a table at the beginning of a URL , lets end it now.
					try {
						builder.endTable();
					}
					catch (Exception e){
						e.printStackTrace();
					}
					
					
				}

				
			}
	
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
			Calendar cal = Calendar.getInstance();
			String today = sdf.format(cal.getTime());
			filename = user.getFirstName() + " " + user.getLastName()  + " Report " + today;
			filename.replace(' ', '_');


			// lets see what the output format requested is.
			String reportFormat = request.getParameter("reportFormat");
			
			// we used to support docx, html and pdf output formats
			// ran into trouble, and didn't see the value in debugging
			// so pulled them out. 
			// can re write that code later. User reportFormat param for that.
			if (reportFormat.equals("doc")) {
				filename += ".doc";
	    		if (exportType.equals("HTML")){
	    			ServletOutputStream out = response.getOutputStream();
	    			
	    			response.setHeader("Expires", "0");
	    			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
	    			response.setHeader("Pragma", "public");
					response.setContentType("application/msword");
					response.setHeader("Content-Disposition",
							"attachment; filename=\"" + filename + "\"");
					doc.save(out, SaveFormat.DOCX);
					
	    		}
		    	if (exportType.equals("file")){
		    		// if rootDataDirectory/TraceCloud does not exist, lets create it.
		    		File traceCloudRoot = new File (rootDataDirectory + File.separator +  "TraceCloud");
		    		if (!(traceCloudRoot.exists() )){
		    		    new File(rootDataDirectory + File.separator +  "TraceCloud").mkdir();
		    		}

		    		// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
		    		File tempFolderRoot  = new File (rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  "Temp");
		    		if (!(tempFolderRoot.exists() )){
		    			new File(rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  "Temp").mkdir();
		    		}

		    		filename = rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  "Temp/" + filename;
		    		FileOutputStream fileOut = new FileOutputStream(filename);

		    		doc.save(fileOut, SaveFormat.DOCX);
					fileOut.flush();
					fileOut.close();
		    	}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return filename;
	}
	
	// used to validate all requirements using a location process (Table)
	// if there are any errors, the returned Arraylist will have the req text and error message.
	public static ArrayList validateRequirementsByTableMultipleReqs(int templateId,
			Project project, int folderId, HttpServletRequest request,
			HttpServletResponse response, String databaseType) {
		ArrayList erroredRequirements = new ArrayList();
		
		
		try {
			WordTemplate wordTemplate = new WordTemplate(templateId,  databaseType);
			Document doc = new Document(wordTemplate.getTemplateFilePath());

			// lets get all tables.
			// to do this , we get all sections in the doc. and for each
			// section we iterate for all tables in it.
			SectionCollection sections = doc.getSections();
			Iterator s = sections.iterator();
			while (s.hasNext() ){
				Section section = (Section) s.next();
				TableCollection tables = section.getBody().getTables();
				Iterator t = tables.iterator();
				while (t.hasNext()){
					Table table = (Table)t.next();
					// each table represents a Requirement.
					// A Table is a potential requirement, only if it has
					// 'Requirement' in the first row.
					// lets read the first row of this table
					String tableType = table.getFirstRow().getFirstCell().getText();
					String errorMessage = "";
					
					String type = "";
					String folder = "";
					
					String owner = "";
					String priority = "";
					String externalURL = "";
					
					String name = "";
					String description = "";
					
					String traceTo = "";
					String traceFrom = "";
					if(tableType.toLowerCase().contains("requirement template")){
						// this is a potenial Requirement. lets process it.
						RowCollection rows = table.getRows();
						Iterator r = rows.iterator();
						// iterate through all the rows of this table
						// and try to build the information required to 
						// create a Requirement
						
						
						// lets go through all the rows in the table and extract required info.
						while (r.hasNext()){
							Row row = (Row) r.next();
							CellCollection cells = row.getCells();
							Cell firstCell = (Cell) cells.get(0);
							Cell secondCell = (Cell) cells.get(1);
							
							if (firstCell.getText().trim().equals("Type")){
								type = secondCell.getText().trim();
							}
						
							if (firstCell.getText().trim().equals("Folder")){
								folder = secondCell.getText().trim();
							}
							
							if (firstCell.getText().trim().equals("Owner")){
								if (
									(secondCell.getText().toLowerCase().contains("hyperlink")) &&
									(secondCell.getText().toLowerCase().contains("mailto:"))
									)
								{
									// word tends to convert a@b.com into a mailto hyperlink.
									// the text of that looks like  hyperlink HYPERLINK "mailto:a@b.com" a@b.com
									// the following two lines will get the a@b.com out of that mess.
									owner = secondCell.getText().trim();
									owner = owner.substring(owner.indexOf("mailto:")+7);
									owner = owner.substring(0,owner.indexOf("\""));
								}
								else {
									owner = secondCell.getText().trim();
								}
							}
							
							if (firstCell.getText().trim().equals("Priority")){
								priority = secondCell.getText().trim();
							}
							
							if (firstCell.getText().trim().equals("External URL")){
								externalURL = secondCell.getText().trim();
							}
							
							if (firstCell.getText().trim().equals("Name")){
								name = secondCell.getText().trim();
							}
							if (firstCell.getText().trim().equals("Description")){
								description = secondCell.getText().trim();
							}
							
							
							if (firstCell.getText().trim().equals("Trace To")){
								traceTo = secondCell.getText().trim();
							}	
							if (firstCell.getText().trim().equals("Trace From")){
								traceFrom = secondCell.getText().trim();
							}							
						}
						// lets validate req type 
						if (type.equals("")){
							errorMessage += "Type is empty<br>";
							erroredRequirements.add(name + ":##:" + errorMessage);
							continue;
						}
						
						if (!(ProjectUtil.requirementTypeExits(project.getProjectId(), type))){
							errorMessage += type + " Type doen't exist in project<br>";
							erroredRequirements.add(name + ":##:" + errorMessage);
							continue;
						}
						
						// at this point, the req type is valid. so lets get a req type object.
						RequirementType requirementType = new RequirementType(project.getProjectId(), type);
						
						
						
						
						// lets validate folder. It can be empty, so validate, only if non empty
						if (!(folder.equals(""))){
							if (!(FolderUtil.isValidFolderPathForRequirementType(project.getProjectId(), folder, requirementType.getRequirementTypeId()))) {
								errorMessage += folder + " Folder doen't exist in the root folder for Requirement Type " +
								type +  "<br>";	
							}
						}
						
						// lets validate owner. It can be empty, so validate, only if non empty
						if (!(owner.equals(""))) {
							if (!(ProjectUtil.isValidUserInProject(owner, project))) {
								errorMessage += owner + " Owner is not a member of this project<br>";
							}
						}
						
						// lets validate priority. It can be empty, so validate, only if non empty
						if (!(priority.equals(""))) {
							if (
									(!(
										(priority.toLowerCase().contains("high"))
									|| (priority.toLowerCase().contains("medium"))
									|| (priority.toLowerCase().contains("low"))
									))
								) {
								errorMessage += priority + " Priority is not in High or Medium or Low<br>";
							}
						}
						
						// lets validate req name
						if (name.equals("")){
							errorMessage += "Name is empty<br>";
						}
						
						// lets validate req description
						if (description.equals("")){
							errorMessage += "Description is empty<br>";
						}
						
					
						// lets validate Trace To
						if (!(traceTo.equals(""))) {
							// if all req tags are valid , you get back an empty string. else
							// you get back a list of tags that don't exist in the system.
							String status = RequirementUtil.validateRequirementTags(traceTo,project.getProjectId());
							if (!(status.equals(""))) {
								errorMessage += traceTo + "  Following Requirement Tags do not exist in the system (Either deleted or purged)."
										+ " Trace To Column.<br>" + status;
							}
						}						

						// lets validate Trace From
						if (!(traceFrom.equals(""))) {
							// if all req tags are valid , you get back an empty string. else
							// you get back a list of tags that don't exist in the system.
							String status = RequirementUtil.validateRequirementTags(traceFrom,project.getProjectId());
							if (!(status.equals(""))) {
								errorMessage += traceFrom + "  Following Requirement Tags do not exist in the system (Either deleted or purged)."
										+ " Trace From Column.<br>" + status;
							}
						}						
						
						////////////////////////////////// 
						// 
						// Custom Attributes
						//
						//////////////////////////////////
						// to figure out which custom attributes exist, we need to get the ReqType,
						// figure out attributes of this req type and then look for those attributes.
						// Hence we need to make the second iteration through the table 
						// the first to get the Req Type / core info and the second  for custom attributes.
						ArrayList rTAttributes = ProjectUtil.getAllAttributes(requirementType.getRequirementTypeId());
						// lets iterate through all the attributes, 
						// and for those of the type Drop Down, lets see if there is a value in the table box
						// and see if its valid or not.
						Iterator a = rTAttributes.iterator();
						while (a.hasNext()) {
							RTAttribute rTAttribute = (RTAttribute) a.next();
							
								// lets iterate through the table, find a value for this
								// attribute label and confirm that its a valid value.
								r = table.getRows().iterator();
								
								String attributeOptions = rTAttribute.getAttributeDropDownOptions();
								String attributeLabel = rTAttribute.getAttributeName();
								String attributeValue = "";
								while (r.hasNext()){
									Row row = (Row) r.next();
									CellCollection cells = row.getCells();
									Cell firstCell = (Cell) cells.get(0);
									Cell secondCell = (Cell) cells.get(1);
									
									if (firstCell.getText().toLowerCase().trim().equals(attributeLabel.toLowerCase())){
										attributeValue = secondCell.getText().trim();
									}
									
								}
								
								// if its a required attribute, lets make sure there is a value 
								if (rTAttribute.getAttributeRequired() == 1) {
									if (attributeValue.equals("")){
										errorMessage += attributeLabel + " is a required field<br>";		
									}
								}
								// now that we have the label and value, if the value is not empty
								// and if the attribute is a dropdown lets make sure its valid.
								if (
										(rTAttribute.getAttributeType().equals("DropDown")) && 
										(!(attributeValue.equals("")))
									) {
									if (!(attributeOptions.toLowerCase().contains(attributeValue.toLowerCase()))){
										errorMessage += attributeLabel + "'s value '" +
											attributeValue + "' is not in the permitted list - " +
											attributeOptions + "<br>";
									}
								}
								
							
						}
						
						
					}
					
					if (!(errorMessage.equals(""))){
						// this means that there was a problem with this Requirement Table.
						erroredRequirements.add(name + ":##:" + errorMessage);
					}
					
					
				}
				
			}
			
		} catch (Exception e) {
			// do nothing.
			e.printStackTrace();
		}

		return (erroredRequirements);
	}


	
	// used to locate all requirements using a location process (Table)
	// prior to calling this routine, call the validateRequirmenetsByTable routine.
	// this ensures that we are dealing with a clean doc.
	public static ArrayList locateRequirementsByTableSingleReq(int templateId,
			Project project, int folderId, HttpServletRequest request,
			HttpServletResponse response, String databaseType) {
		ArrayList locatedRequirements = new ArrayList();
		
		
		try {
			WordTemplate wordTemplate = new WordTemplate(templateId,  databaseType);
			Document doc = new Document(wordTemplate.getTemplateFilePath());

			// lets get all tables.
			// to do this , we get all sections in the doc. and for each
			// section we iterate for all tables in it.
			SectionCollection sections = doc.getSections();
			Iterator s = sections.iterator();
			while (s.hasNext() ){
				Section section = (Section) s.next();
				TableCollection tables = section.getBody().getTables();
				Iterator t = tables.iterator();
				while (t.hasNext()){
					Table table = (Table)t.next();
					// each table represents a Requirement.
					// A Table is a potential requirement, only if it has
					// 'Requirement' in the first row.
					// lets read the first row of this table
					String tableType = table.getFirstRow().getFirstCell().getText();
					
					if(tableType.toLowerCase().contains("requirement")){
						// this is a potenial Requirement. lets process it.
						String type = "";
						String name = "";
						String folder = "";
						RowCollection rows = table.getRows();
						Iterator r = rows.iterator();
						// iterate through all the rows of this table and get the name
						while (r.hasNext()){
							Row row = (Row) r.next();
							CellCollection cells = row.getCells();
							Cell firstCell = (Cell) cells.get(0);
							Cell secondCell = (Cell) cells.get(1);
							
							if (firstCell.getText().trim().equals("Type")){
								type = secondCell.getText().trim();
							}
							
							if (firstCell.getText().trim().equals("Name")){
								name = secondCell.getText().trim();
							}
							
							if (firstCell.getText().trim().equals("Folder")){
								folder = secondCell.getText().trim();
							}

						}
						
						// at this point, the req type is valid. so lets get a req type object.
						RequirementType requirementType = new RequirementType(project.getProjectId(), type);

						   

						String targetFolderPath = "";
						String createPermissions = "createRequirements:false";
						
						SecurityProfile securityProfile = (SecurityProfile) request.getSession().getAttribute("securityProfile");
						// lets see if the user has wrive privs on this folder. It can be empty, so validate, only if non empty
						if (!(folder.equals(""))){
							targetFolderPath = folder;
							
							int targetFolderId = FolderUtil.getFolderId(project.getProjectId(), folder);
							if (securityProfile.getPrivileges().contains("createRequirementsInFolder" 
									+ targetFolderId)){
								createPermissions = "createRequirements:true";
							}
							 
						}
						else {
							// since there is no targetfolder, 
							// lets see if the user has write privs on the req types root folder.
							int targetFolderId = requirementType.getRootFolderId();
							Folder targetFolder = new Folder(targetFolderId);
							
							targetFolderPath = targetFolder.getFolderPath();
							if (securityProfile.getPrivileges().contains("createRequirementsInFolder" 
									+ targetFolderId)){
								createPermissions = "createRequirements:true";
							}
							
						}
						
						locatedRequirements.add(targetFolderPath + ":##:" + 
							createPermissions + ":##:" + name);
					}	
				}
				
			}
			
		} catch (Exception e) {
			// do nothing.
			e.printStackTrace();
		}
		return (locatedRequirements);
	}
	

	
	// used to locate all requirements using a location process (Table)
	// prior to calling this routine, call the validateRequirmenetsByTable routine.
	// this ensures that we are dealing with a clean doc.
	public static ArrayList locateRequirementsByTableMultipleReqs(int templateId,
			Project project, int currentFolderId, HttpServletRequest request,
			HttpServletResponse response, SecurityProfile securityProfile,String databaseType) {
		ArrayList locatedRequirements = new ArrayList();
		
		try {
			WordTemplate wordTemplate = new WordTemplate(templateId,  databaseType);
			Document doc = new Document(wordTemplate.getTemplateFilePath());
			Folder currentF = new Folder(currentFolderId);
			RequirementType currentRT = new RequirementType(currentF.getRequirementTypeId());
			Folder thisRowF = null;
			RequirementType thisRowRT = null;
			ArrayList thisRowRTAttributes = null;
			
			
			// lets get all tables.
			// to do this , we get all sections in the doc. and for each
			// section we iterate for all tables in it.
			SectionCollection sections = doc.getSections();
			Iterator s = sections.iterator();
			while (s.hasNext() ){
				Section section = (Section) s.next();
				TableCollection tables = section.getBody().getTables();
				Iterator t = tables.iterator();
				while (t.hasNext()){
					// attributeAvailableMap is essentially a map of column headers vs column numbers the table. it looks like
					// name:1
					// description:2
					// traceto:3
					// tracefrom:4
					// folderpath:5
					// customattrib1name:n etc...
					HashMap attributeAvailableMap = new HashMap();
					
					
					Table table = (Table)t.next();
					// A table can have requirements if the first row has name or description in it's cell title.
					Row firstRow = table.getFirstRow();
					CellCollection firstRowCells = firstRow.getCells();
					// lets loop through the cells and figure out which one has a name or description.
					Iterator frc = firstRowCells.iterator();
					
					
					int cellCount = 0;
					while (frc.hasNext()){
						cellCount++;
						Cell cell = (Cell) frc.next();
						String cellContent = cell.getText();
						if (cellContent != null) {
							attributeAvailableMap.put(cellContent.trim().toLowerCase(), new Integer(cellCount));
						}
					}
					
		
					if ((attributeAvailableMap.get("name") != null) || (attributeAvailableMap.get("description") != null)){
						// either name or description column is non empty.
						// hence a table that can be processed.
						// lets loop through each row and get the nameColumn and descriptionColumnValues.
						int rowCount = 0;
						Iterator r = table.getRows().iterator();
						while (r.hasNext()){
							// attributeValueMap is a map of rtattributeids vs values for each row
							// since each row can be a diff req type (because of the folder value)
							// we create a new attribute value map for each row
							HashMap attributeValueMap = new HashMap();
							
							Row row = (Row) r.next();
							rowCount++;
							// since the first row has the headers, lets skip it.
							if (rowCount == 1){ 
								continue;
							}
							// now lets iterator through all the cells in this row and
							// pick up name and description values.
							String name = "";
							String description = "";
							String traceTo = "";
							String traceFrom = "";
							String folderPath = "";
							Iterator c = row.getCells().iterator();
							cellCount = 0;
							// for each row validFolder is used to track whether we should process this row or now.
							boolean validRow = true;
							while (c.hasNext()){
								Cell cell = (Cell) c.next();
								cellCount++;
								try{
									if (attributeAvailableMap.get("name") != null){
										int nameColumnNumber = ((Integer) attributeAvailableMap.get("name")).intValue();
										if (cellCount == nameColumnNumber){
									
											// this cell has Name Value
											name = cell.getText();
											// we are getting a funny char at the end. lets drop it.
											// cludge, but seems to work.
											name = name.substring(0, name.length()-1).trim();
										}
									}
								}
								catch (Exception e){
									
								}
								
								try {
									if (attributeAvailableMap.get("description") != null){
										int descriptionColumnNumber = ((Integer) attributeAvailableMap.get("description")).intValue();
										if (cellCount == descriptionColumnNumber){
											// this cell has Description Value
											description = cell.getText();
											// we are getting a funny char at the end. lets drop it.
											// cludge, but seems to work.
											description = description.substring(0, description.length()-1).trim();
										}
									}
								}
								catch (Exception e){
									
								}
								
								try {
									if (attributeAvailableMap.get("traceto") != null){
										int traceToColumnNumber = ((Integer) attributeAvailableMap.get("traceto")).intValue();
										if (cellCount == traceToColumnNumber){
											// this cell has traceTo Value
											traceTo = cell.getText();
											// we are getting a funny char at the end. lets drop it.
											// cludge, but seems to work.
											traceTo = traceTo.substring(0, traceTo.length()-1);
										}
									}
								}
								catch (Exception e){
									
								}
								
								try {
									if (attributeAvailableMap.get("tracefrom") != null){
										int traceFromColumnNumber = ((Integer) attributeAvailableMap.get("tracefrom")).intValue();
										if (cellCount == traceFromColumnNumber){
											// this cell has traceFrom Value
											traceFrom = cell.getText();
											// we are getting a funny char at the end. lets drop it.
											// cludge, but seems to work.
											traceFrom = traceFrom.substring(0, traceFrom.length()-1);
										}
									}
								}
								catch (Exception e){
									
								}
								
								try {
									if (attributeAvailableMap.get("folderpath") != null){
										int folderPathColumnNumber = ((Integer) attributeAvailableMap.get("folderpath")).intValue();
										if (cellCount == folderPathColumnNumber){
											// this cell has folderPath Value
											folderPath = cell.getText();
											// we are getting a funny char at the end. lets drop it.
											// cludge, but seems to work.
											folderPath = folderPath.substring(0, folderPath.length()-1);
										}
									}
								}
								catch (Exception e){
									
								}
								
								// lets get the req type and rtatttributes from the given folder path.
								// if no folder path is given, lets use the current folder the user is in.
								if ((folderPath != null ) && (!folderPath.equals(""))){
									
									thisRowF = new Folder (folderPath, project.getProjectId() );
									if (( thisRowF == null) || ( thisRowF.getFolderId() == 0) ){
										name = "<font color='red'>Error : </font>" + name;
										name += "<br> <font color='red'> Can not locate this folder : </font>" + folderPath;
										locatedRequirements.add(name);
										validRow = false;
										break;
									}
									
									if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" + thisRowF.getFolderId()))){
										name = "<font color='red'>Error : </font>" + name;
										name += "<br> <font color='red'> You do not have create permissions on this folder : </font>" + folderPath;
										locatedRequirements.add(name);
										validRow = false;
										break;
									}
									
									thisRowRT = new RequirementType(thisRowF.getRequirementTypeId());
									thisRowRTAttributes = thisRowRT.getAllAttributesInRequirementType();
								}
								else {
									thisRowF = currentF;
									thisRowRT = currentRT;
									thisRowRTAttributes = currentRT.getAllAttributesInRequirementType();
								}
							
								
								
								// lets iterate through all the attribs for this rows req type.
								// for each attrib, get the name, and if this name exists in the attributeAvailableMap, get the column number.
								// if the current cell we are on is in this column, then put this rtattrib object and value in the attributeValueMap
								// for later processing / validation
								
								Iterator tRA = thisRowRTAttributes.iterator();
								while (tRA.hasNext()){
									RTAttribute rTAttribute = (RTAttribute) tRA.next();
									try {
										if (attributeAvailableMap.get(rTAttribute.getAttributeName().trim().toLowerCase()) != null){
											int attributeColumnNumber = ((Integer) attributeAvailableMap.get(rTAttribute.getAttributeName().trim().toLowerCase())).intValue();
											if (cellCount == attributeColumnNumber){
												// this cell has this rTAttributes  Value
												String attributeValue = cell.getText();
												// we are getting a funny char at the end. lets drop it.
												// cludge, but seems to work.
												attributeValue = attributeValue.substring(0, attributeValue.length()-1).trim();
												attributeValueMap.put(new Integer(rTAttribute.getAttributeId()), attributeValue);
											}
										}
									}
									catch (Exception e){
										
									}
								}
							}
							
							if (validRow == false){
								continue;
							}
							
							// if name is non empty and description is then lets copy name to desc
							// if desc is non empty and name is empty then, lets trunc desc and copy it to name.
							if (name == null){
								name = "";
							}
							if (description == null){
								description = "";
							}
							
							if ((!name.equals("")) && (description.equals(""))){
								description = name;
							}
							if ((!description.equals("")) && (name.equals(""))){
								if (description.length() > 99){
									name = description.substring(0,95) + "...";
								}
								else {
									name = description;
								}
							}
							
							
							
							if ((!description.equals("")) || (!name.equals(""))){		
								// if name or description are non empty, lets create a req.
								// lets add the attribute details to name
								name += "<br> Requirement Type : " + thisRowRT.getRequirementTypeName() + " (" + thisRowRT.getRequirementTypeShortName() + ")";
								name += "<br> Folder : " + thisRowF.getFolderPath();
								Collection attributes = attributeValueMap.keySet();
								Iterator a = attributes.iterator();
								while (a.hasNext()){
									Integer rTAttributeIdObject = (Integer) a.next();
									RTAttribute rTAttribute = new RTAttribute (rTAttributeIdObject.intValue());
									String attributeValue = (String) attributeValueMap.get(rTAttributeIdObject);
									name += "<br> " + rTAttribute.getAttributeName() + " : " + attributeValue;
								}
								
								boolean traceToCovered = true;
								if ((traceTo != null ) && (!traceTo.equals(""))){
									String status = RequirementUtil.validateRequirementTags(traceTo,project.getProjectId());
									if (!(status.equals(""))) {
										traceToCovered = false;
										name += "<br> <font color='red'> TraceTo : "
											+ status +" </font> ";
									}
									status = RequirementUtil.validatePotentialTraces(project, 
											traceTo, "", project.getProjectId(), securityProfile,  databaseType);
									if (!(status.equals(""))) {
										traceToCovered = false;
										name += " <font color='red'> "	+ status +" </font> ";
									}
								}
								
								boolean traceFromCovered = true;
								if ((traceFrom != null ) && (!traceFrom.equals(""))){
									String status = RequirementUtil.validateRequirementTags(traceFrom,project.getProjectId());
									if (!(status.equals(""))) {
										traceFromCovered = false;
										name += "<br> <font color='red'> TraceFrom  : "
											+ status +" </font> ";
									}
									
									status = RequirementUtil.validatePotentialTraces(project, 
											"", traceFrom, project.getProjectId(), securityProfile,  databaseType);
									if (!(status.equals(""))) {
										traceFromCovered = false;
										name += " <font color='red'>  "	+ status +" </font> ";
									}
								}
								// lets make sure that all mandatory attribs have been provided.
								// if a mandatory attrib (without  a default value)) is not provided, then its an error condition.
								boolean dropDownsCovered = true;
								boolean dateFormattingCovered = true;
								boolean mandatoryAttribsCovered = true;
								Iterator rTA = thisRowRTAttributes.iterator();
								while (rTA.hasNext()){
									RTAttribute rTAttribute = (RTAttribute) rTA.next();
									Integer rTAttributeIdObject = new Integer(rTAttribute.getAttributeId());
									String attributeValue = (String) attributeValueMap.get(rTAttributeIdObject);
									
									if ((rTAttribute.getAttributeRequired() == 1)
										&&
										(rTAttribute.getAttributeDefaultValue().equals(""))
									){
										
										// This is a required the attribute with out  A default value 
										
										
										
										if ((attributeValue == null ) || (attributeValue.equals(""))){
											mandatoryAttribsCovered = false;
											name += "<br> <font color='red'> " + rTAttribute.getAttributeName() + " is required </font> ";
										}
									}
									
									// if there are any date attribs lets make sure they are formatted correctly
									if (rTAttribute.getAttributeType().equals("Date")){
										
										try {
											if ((attributeValue != null ) && (!attributeValue.equals(""))){
												// lets try to convert this to a date just make sure its valid
												// we convert this to a date and back to string
												DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
												attributeValue = formatter.format(formatter.parse(attributeValue));
											}
										} catch (Exception e) {
											dateFormattingCovered = false;
											name += "<br> <font color='red'> " + rTAttribute.getAttributeName() + " should be in mm/dd/yyyy format </font> ";
										}
									}
									// if there are any dropdown attribs lets make sure its value is in drop down list
									if (rTAttribute.getAttributeType().equals("Drop Down")){
										
										
										if ((attributeValue != null ) && (!attributeValue.equals(""))){
											boolean permitted = ProjectUtil.isPermittedValueInAttribute(rTAttribute.getAttributeId(), attributeValue);
											if (!permitted){
												dropDownsCovered = false;
												name += "<br> <font color='red'> " + rTAttribute.getAttributeName() + " should be one of "+ rTAttribute.getAttributeDropDownOptions() +" </font> ";
											}
										}
									}
									
								}
								if (( dateFormattingCovered == false) || ( mandatoryAttribsCovered == false)  || ( dropDownsCovered == false)
										|| ( traceToCovered == false)	|| ( traceFromCovered == false)
								){
									name = "<font color='red'>Error : </font>" + name;
								}
								
								locatedRequirements.add(name);
							}
						}
					}
				}
				
			}
			
		} catch (Exception e) {
			// do nothing.
			e.printStackTrace();
		}
		return (locatedRequirements);
	}
	
	// used to locate all requirements using a location process (hyperlinks or
	// search strings)
	// used to confirm with the user before converting to real requirements.
	// returns an array list of strings that are hyperlink names whose target is
	// 'url'
	public static ArrayList locateRequirementsByHyperLink(int templateId,
			Project project, int folderId, HttpServletRequest request,
			HttpServletResponse response, String databaseType) {
		ArrayList locatedRequirements = new ArrayList();
		try {
			WordTemplate wordTemplate = new WordTemplate(templateId,  databaseType);
			Document doc = new Document(wordTemplate.getTemplateFilePath());

			// Hyperlinks in a Word documents are fields, select all field start
			// nodes so we can find the hyperlinks.
			NodeList fieldStarts = doc.selectNodes("//FieldStart");
			Iterator fs = fieldStarts.iterator();
			while (fs.hasNext()) {
				Node node = (Node) fs.next();
				FieldStart fieldStart = (FieldStart) node;

				if (fieldStart.getFieldType() == FieldType.FIELD_HYPERLINK) {

					// The field is a hyperlink field, use the "facade" class to
					// help to deal with the field.
					Hyperlink hyperlink = new Hyperlink(fieldStart);

					String urlName = hyperlink.getName();
					// word seems to be introducing some character at the
					// beginning
					// of the URL. so lets drop it.
					if ((urlName != null) && (urlName.length() > 0)) {
						urlName = urlName.substring(1);
					}

					String urlTarget = hyperlink.getTarget();
					if ((urlTarget != null)
							&& (urlTarget.equalsIgnoreCase("req"))
							&& (urlName != null)
							&& (!urlName.equalsIgnoreCase(""))

					) {
						locatedRequirements.add(urlName);
					}

				}
			}
		} catch (Exception e) {
			// do nothing.
			e.printStackTrace();
		}

		return (locatedRequirements);
	}

	// used to locate all requirements using a location process (Style)
	// used to confirm with the user before converting to real requirements.
	// returns an array list of strings that are hyperlink names whose target is
	// 'url'
	public static ArrayList locateRequirementsByStyle(int templateId,
			Project project, int folderId, String styleName,
			HttpServletRequest request, HttpServletResponse response, String databaseType) {
		ArrayList locatedRequirements = new ArrayList();
		try {
			WordTemplate wordTemplate = new WordTemplate(templateId,  databaseType);
			Document doc = new Document(wordTemplate.getTemplateFilePath());

			// Get all runs from the document.
			NodeCollection runs = doc.getChildNodes(NodeType.RUN, true);
			// Look through all paragraphs to find those with the specified
			// style.
			Iterator i = runs.iterator();
			String lastRunStyle = "";
			String lastRunText = "";
			while (i.hasNext()) {
				// we run into a problem where runs of the same style are split
				// into different nodes. so we have this logic that 
				// combines different adjacent runs of same style into one string. 
				Run run = (Run) i.next();
				String runText = run.getText();
				String runStyle = run.getFont().getStyleName();


				
				if (runStyle.equals(lastRunStyle)){
					// continueing the same old run style.
					lastRunText += runText;
				}
				else {
					// a new run style has begun.
					if (!(lastRunText.equals(""))){
						// since a new run style has started, lets process
						// the old run style and its text.
						if (lastRunStyle.equals(styleName)) {
							// the last run style matches the styleName chosen by user to
							// create the req.
							locatedRequirements.add(lastRunText);
						}
					}					
					// lets re-set the lastRunStyle / Text values to current run Style and text so that
					// we can catch all runs of this style.
					lastRunStyle = runStyle;
					lastRunText = runText;
				}
			}
			// since we always process the lastRunStyle , at this point, we haven't the value of the
			// last data point . i.e the last lastRunStyle hasnt' been processed yet. 
			// This is convoluted. Here is the example.
			// In the current run, we see if it's the same as last run, and if it has changed, then we process
			// the last run. However, this model leavs us with a situion where the last current run in the
			// while loop is never processed. This value has to be processed.
			// handling the last current run.
			if (!(lastRunText.equals(""))){
				// since a new run style has started, lets process
				// the old run style and its text.
				if (lastRunStyle.equals(styleName)) {
					// the last run style matches the styleName chosen by user to
					// create the req.
					locatedRequirements.add(lastRunText);
				}
			}					
			
			
		} 
		catch (Exception e) {
			// do nothing.
			e.printStackTrace();
		}
		return (locatedRequirements);
	}

	
	
	
	
	// used to locate all requirements using a location process
	// (ParagraphSearch)
	// used to confirm with the user before converting to real requirements.
	// returns an array list of strings that are hyperlink names whose target is
	// 'url'
	public static ArrayList locateRequirementsByParagraphSearch(int templateId,
			Project project, int folderId, String paragraphSearch,
			HttpServletRequest request, HttpServletResponse response, String databaseType) {
		ArrayList locatedRequirements = new ArrayList();
		try {
			WordTemplate wordTemplate = new WordTemplate(templateId,  databaseType);
			Document doc = new Document(wordTemplate.getTemplateFilePath());

			String[] searchWords = {};
			if ((paragraphSearch != null) && (paragraphSearch.contains(" "))) {
				paragraphSearch = paragraphSearch.replace(" ", "");
			}
			if (paragraphSearch.contains(",")) {

			} else {
				paragraphSearch = paragraphSearch + ",";
			}
			searchWords = paragraphSearch.split(",");

			// Get all runs from the document.
			NodeCollection paragraphs = doc.getChildNodes(NodeType.PARAGRAPH,true);
			// Look through all paragraphs to find those with the specified
			// search string.
			Iterator i = paragraphs.iterator();
			while (i.hasNext()) {
				Paragraph paragraph = (Paragraph) i.next();
				// lets see if the paragraph string contains the search words.
				String paragraphText = paragraph.getRange().getText();
				if ((paragraphText != null) && !(paragraphText.equals(""))) {
					
					// this para has some text. so lets go forward.
					if (searchWords.length > 0) {
						boolean match = false;
						for (int j = 0; j < searchWords.length; j++) {
							if (paragraphText.toLowerCase().contains(searchWords[j].toLowerCase())) {
								// this means that there is a match.
								match = true;
							}
						}
						if (match) {
							locatedRequirements.add(paragraph.getText());
						
						}
					}
				}
			}
		} catch (Exception e) {
			// do nothing.
			e.printStackTrace();
		}

		return (locatedRequirements);
	}

	// used to create all requirements using a location process (hyperlinks)
	// NOTE : we use the EXACT same location process as locateRequirements method
	// and then we apply the check box the use user has selected to create the requierments.	
	public static ArrayList createRequirementsFromWordTemplateByHyperLink(
			int templateId, Project project, int folderId,
			String locationNumberString, User user, HttpServletRequest request,
			HttpServletResponse response, String databaseType) {
		ArrayList createdRequirements = new ArrayList();
		try {
			Folder folder = new Folder(folderId);
			WordTemplate wordTemplate = new WordTemplate(templateId,  databaseType);

			Document doc = new Document(wordTemplate.getTemplateFilePath());
			DocumentBuilder builder = new DocumentBuilder(doc);

			// Hyperlinks in a Word documents are fields, select all field start
			// nodes so we can find the hyperlinks.
			NodeList fieldStarts = doc.selectNodes("//FieldStart");
			Iterator fs = fieldStarts.iterator();

			// lets set some default attribute values for creating the req.
			int requirementTypeId = folder.getRequirementTypeId();
			String requirementPriority = "Medium";
			String requirementOwner = user.getEmailId();
			int requirementPctComplete = 0;
			String requirementExternalUrl = "";

			// we don't need to create a req out of every single requirement we
			// find.
			// we need to do that only for the location number in the string .
			// this string was formed base on which checkboxes the user checked.
			// if he checked the 1st, 20th, and 31st box, then we get a string
			// like 1::20::31
			// we are going to check for this location number in every
			// iteration.
			// to avoid a scenario where 1 gets matched agains 11, we search for
			// ::1::
			// to make that happen we append :: to the string.
			locationNumberString = "::" + locationNumberString;

			int currentLocation = 0;
			while (fs.hasNext()) {
				Node node = (Node) fs.next();
				FieldStart fieldStart = (FieldStart) node;

				if (fieldStart.getFieldType() == FieldType.FIELD_HYPERLINK) {

					// The field is a hyperlink field, use the "facade" class to
					// help to deal with the field.
					Hyperlink hyperlink = new Hyperlink(fieldStart);

					String urlName = hyperlink.getName();
					// word seems to be introducing some character at the
					// beginning
					// of the URL. so lets drop it.
					if ((urlName != null) && (urlName.length() > 0)) {
						urlName = urlName.substring(1);
					}
					String urlTarget = hyperlink.getTarget();
					if ((urlTarget != null)
							&& (urlTarget.equalsIgnoreCase("req"))
							&& (urlName != null)
							&& (!urlName.equalsIgnoreCase(""))) {
						currentLocation++;
						String currentLocationString = "::" + currentLocation
								+ "::";
						if (!(locationNumberString
								.contains(currentLocationString))) {
							// the user chose to not create a req out of this
							// one. So
							// lets skip it.
							continue;
						}

						// we need to create a new requirement out of the URL
						// Name.
						// then add this req to the createdRequirements Array
						// list
						// and change the URL Name and String to the req full
						// tag.

						// first lets create the requirement.
						String requirementDescription = urlName;
						String requirementName = urlName;
						if (requirementName.length() > 94) {
							requirementName = requirementName.substring(0, 94)
									+ "...";
						}
						
						String requirementLockedBy = "";

						Requirement requirement = new Requirement("",
								requirementTypeId, folderId, project
										.getProjectId(), requirementName,
								requirementDescription, requirementPriority,
								requirementOwner, requirementLockedBy, requirementPctComplete,
								requirementExternalUrl, user.getEmailId(),  databaseType);

						// add it to the array list.
						createdRequirements.add(requirement);

						// modify the URL object in the document.
						builder.moveTo(node);
						String fullTag = requirement.getRequirementFullTag();
						hyperlink.setName("");
						hyperlink.setTarget("");
						builder.insertHyperlink(fullTag, fullTag, false);

					}

				}
			}

			// now that we have created the requirements and replaced the
			// req text with the new req full tags, lets
			// write the new word template to replace the old one
			// and update the db entry for this word template.
			doc.save(wordTemplate.getTemplateFilePath());
			// since we are saving in the same location where the file
			// was originally located, we don't need to update the db.
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (createdRequirements);
	}

	
	
	
	// goes through all the tables in the document and 
	// for those chosen by the user to be created , creates the requirement.
	// prior to comign here, the doc had been validated, so we should be good to go
	public static ArrayList createRequirementsFromWordTemplateByTableSingleReq(
			int templateId, Project project, int folderId,
			String locationNumberString, User user, HttpServletRequest request,
			HttpServletResponse response, String databaseType) {
		ArrayList createdRequirements = new ArrayList();
		ArrayList alertRows = new ArrayList();
		try {
			WordTemplate wordTemplate = new WordTemplate(templateId,  databaseType);

			
			Document doc = new Document(wordTemplate.getTemplateFilePath());
			DocumentBuilder builder = new DocumentBuilder(doc);

			// we don't need to create a req out of every single requirement we
			// find.
			// we need to do that only for the location number in the string .
			// this string was formed base on which checkboxes the user checked.
			// if he checked the 1st, 20th, and 31st box, then we get a string
			// like 1::20::31
			// we are going to check for this location number in every
			// iteration.
			// to avoid a scenario where 1 gets matched agains 11, we search for
			// ::1::
			// to make that happen we append :: to the string.
			locationNumberString = "::" + locationNumberString;

			int currentLocation = 0;


			
			// lets get all tables.
			// to do this , we get all sections in the doc. and for each
			// section we iterate for all tables in it.
			SectionCollection sections = doc.getSections();
			Iterator s = sections.iterator();
			while (s.hasNext() ){
				Section section = (Section) s.next();
				TableCollection tables = section.getBody().getTables();
				Iterator t = tables.iterator();
				while (t.hasNext()){
					
					Table table = (Table)t.next();
					// each table represents a Requirement.
					// A Table is a potential requirement, only if it has
					// 'Requirement' in the first row.
					// lets read the first row of this table
					String tableType = table.getFirstRow().getFirstCell().getText();
					
					String type = "";
					String folder = "";
					
					String owner = "";
					String priority = "";
					String externalURL = "";
					
					String name = "";
					String description = "";
					
					String traceTo = "";
					String traceFrom = "";
					if(tableType.toLowerCase().contains("requirement template")){
						// this is a potenial Requirement. lets process it.

						currentLocation++;
						String currentLocationString = "::" + currentLocation + "::";
						if (!(locationNumberString.contains(currentLocationString))) {
							// the user chose to not create a req out of this one. So
							// lets skip it.
							continue;
						}

						// at this point, we have a potential req, and th user chose to create it
						// so, lets create the Req first.
						
						// lets iterate through the table and get the core fields.
						RowCollection rows = table.getRows();
						Iterator r = rows.iterator();
						// iterate through all the rows of this table
						// and try to build the information required to 
						// create a Requirement
						
						
						// lets go through all the rows in the table and extract required info.
						while (r.hasNext()){
							Row row = (Row) r.next();
							CellCollection cells = row.getCells();
							Cell firstCell = (Cell) cells.get(0);
							Cell secondCell = (Cell) cells.get(1);
							
							if (firstCell.getText().trim().equals("Type")){
								type = secondCell.getText().trim();
							}
						
							if (firstCell.getText().trim().equals("Folder")){
								folder = secondCell.getText().trim();
							}
							
							if (firstCell.getText().trim().equals("Owner")){
								
								if (
									(secondCell.getText().toLowerCase().contains("hyperlink")) &&
									(secondCell.getText().toLowerCase().contains("mailto:"))
									)
								{
									// word tends to convert a@b.com into a mailto hyperlink.
									// the text of that looks like  hyperlink HYPERLINK "mailto:a@b.com" a@b.com
									// the following two lines will get the a@b.com out of that mess.
									owner = secondCell.getText().trim();
									owner = owner.substring(owner.indexOf("mailto:")+7);
									owner = owner.substring(0,owner.indexOf("\""));
								}
								else {
									owner = secondCell.getText().trim();
								}
							}
							
							if (firstCell.getText().trim().equals("Priority")){
								priority = secondCell.getText().trim();
							}
							
							if (firstCell.getText().trim().equals("External URL")){
								externalURL = secondCell.getText().trim();
							}
							
							if (firstCell.getText().trim().equals("Name")){
								name = secondCell.getText().trim();
							}
							if (firstCell.getText().trim().equals("Description")){
								description = secondCell.getText().trim();
							}
							
							
							if (firstCell.getText().trim().equals("Trace To")){
								traceTo = secondCell.getText().trim();
							}							
							if (firstCell.getText().trim().equals("Trace From")){
								traceFrom = secondCell.getText().trim();
							}							
						}

						// lets get the ids ,set the defaults etc...
						
						RequirementType requirementType = new RequirementType(project.getProjectId(), type);
						int requirementTypeId = requirementType.getRequirementTypeId();
						
						
						int targetFolderId = 0;
						if (!(folder.equals(""))){
							String targetFolderPath = folder;							
							targetFolderId = FolderUtil.getFolderId(project.getProjectId(), folder);

						}
						else {
							// since there is no targetfolder, 
							// lets see if the user has write privs on the req types root folder.
							targetFolderId = requirementType.getRootFolderId();
						}
						
						String requirementName = name;
						
						 
						String requirementDescription = description;
						
						String requirementPriority = "Medium";
						// requirement Priority
						// get the value from the excel only if the user gave us a
						// column to pick from.
						
						if ((priority.toUpperCase().trim().equals("HIGH"))) {
							requirementPriority = "High";
						}
						if ((priority.toUpperCase().trim().equals("MEDIUM"))) {
							requirementPriority = "Medium";
						}
						if ((priority.toUpperCase().trim().equals("LOW"))) {
							requirementPriority = "Low";
						}
						
						String requirementOwner = user.getEmailId();
						if (!(owner.equals(""))){
							requirementOwner =owner;
						}
						
						int requirementPctComplete = 0;
						
						String requirementExternalUrl = externalURL;
						String requirementLockedBy = "";
						Requirement requirement = new Requirement("",
								requirementTypeId, targetFolderId, project.getProjectId(), requirementName,
								requirementDescription, requirementPriority,
								requirementOwner, requirementLockedBy, requirementPctComplete,
								requirementExternalUrl, user.getEmailId(),  databaseType);

									
						// now that the Requirement is created, lets iterate through the 
						// the Requirement Template Table and process each custom attribute we find there.
						////////////////////////////////// 
						// 
						// Custom Attributes
						//
						//////////////////////////////////
						// to figure out which custom attributes exist, we need to get the ReqType,
						// figure out attributes of this req type and then look for those attributes.
						// Hence we need to make the second iteration through the table 
						// the first to get the Req Type / core info and the second  for custom attributes.
						ArrayList rTAttributes = ProjectUtil.getAllAttributes(requirementType.getRequirementTypeId());
						// lets iterate through all the attributes, 
						// and for those of the type Drop Down, lets see if there is a value in the table box
						// and see if its valid or not.
						Iterator a = rTAttributes.iterator();
						boolean customAttributesUpdated = false;
						while (a.hasNext()) {
							RTAttribute rTAttribute = (RTAttribute) a.next();
							
							// lets iterate through the table, find a value for this
							// attribute label and confirm that its a valid value.
							r = table.getRows().iterator();
							
							String attributeOptions = rTAttribute.getAttributeDropDownOptions();
							String attributeLabel = rTAttribute.getAttributeName();
							String attributeValue = "";
							while (r.hasNext()){
								Row row = (Row) r.next();
								CellCollection cells = row.getCells();
								Cell firstCell = (Cell) cells.get(0);
								Cell secondCell = (Cell) cells.get(1);
								
								if (firstCell.getText().toLowerCase().trim().equals(attributeLabel.toLowerCase())){
									attributeValue = secondCell.getText().trim();
								}
								
							}
							
							// now that we have a custom attribute label , value , lets create this rAttributeValue object.
							if (!(attributeValue.equals(""))) {
								RequirementUtil.updateRequirementAttribute(requirement.getRequirementId(),
									rTAttribute.getAttributeId(),attributeValue, user.getEmailId(),  databaseType);
								customAttributesUpdated = true;

							}
						}
						
						
						
						// if we updated any custom attributes, we will need to
						// update the UDA for this requirement.
						if (customAttributesUpdated) {
							RequirementUtil.setUserDefinedAttributes(requirement.getRequirementId(), user.getEmailId(),  databaseType);
						}
						
						
						
						// Traceability
						// lets updates the newly created Reqs tracebility . These
						// values may also
						// have been sent as part of the imported excel file.
						SecurityProfile securityProfile = (SecurityProfile) request.getSession().getAttribute("securityProfile");
						if ((traceTo != null) || (traceFrom != null)) {
							String status = RequirementUtil.createTraces(project, requirement.getRequirementId(), traceTo,
									traceFrom, project.getProjectId(),  securityProfile,  databaseType);
							
							
							if (!(status.equals(""))) {
								// the create traces method returned a non empty string.
								// means that there were some circular traces that were
								// not created.
								String inputString = requirement.getRequirementFullTag() + ":##:" +
									requirementName +  ":##:" + status;
								alertRows.add(inputString);
							}
						}

						// Since the requirement has undergone changes (i.e custom
						// attribs are created
						// lets refresh.

						requirement = new Requirement(requirement.getRequirementId(),  databaseType);
						createdRequirements.add(requirement);
						
						// now that we have created the requirement, lets drop the table
						// and create a hyperlink template to this requirement.
						builder.moveTo(table.getNextSibling());
						String fullTag = requirement.getRequirementFullTag();
						builder.insertHyperlink(fullTag, fullTag, false);
						table.remove();
					}

				}
			}

			// Now we need to add the createdRequirements and errorRows to the request.
			request.setAttribute("createdRequirements", createdRequirements);
			request.setAttribute("alertRows", alertRows);
			
			
			// now that we have created the requirements and replaced the
			// req text with the new req full tags, lets
			// write the new word template to replace the old one
			// and update the db entry for this word template.
			doc.save(wordTemplate.getTemplateFilePath());
			// since we are saving in the same location where the file
			// was originally located, we don't need to update the db.
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (createdRequirements);
	}


	

	// goes through all the tables in the document and 
	// for those chosen by the user to be created , creates the requirement.
	// prior to comign here, the doc had been validated, so we should be good to go
	public static ArrayList createRequirementsFromWordTemplateByTableMultipleReqs(
			int templateId, Project project, int currentFolderId,
			String locationNumberString, User user, HttpServletRequest request,
			HttpServletResponse response, SecurityProfile securityProfile, String databaseType) {
		ArrayList createdRequirements = new ArrayList();
		ArrayList alertRows = new ArrayList();
		Folder currentF = new Folder(currentFolderId);
		RequirementType currentRT = new RequirementType(currentF.getRequirementTypeId());
		ArrayList thisRowRTAttributes = null;
		Folder thisRowF = null;
		RequirementType thisRowRT = null;
		
		try {
			WordTemplate wordTemplate = new WordTemplate(templateId,  databaseType);
			
			
			Document doc = new Document(wordTemplate.getTemplateFilePath());
			DocumentBuilder builder = new DocumentBuilder(doc);

			// we don't need to create a req out of every single requirement we
			// find.
			// we need to do that only for the location number in the string .
			// this string was formed base on which checkboxes the user checked.
			// if he checked the 1st, 20th, and 31st box, then we get a string
			// like 1::20::31
			// we are going to check for this location number in every
			// iteration.
			// to avoid a scenario where 1 gets matched agains 11, we search for
			// ::1::
			// to make that happen we append :: to the string.
			locationNumberString = "::" + locationNumberString;

			int currentLocation = 0;
			
			
			// lets get all tables.
			// to do this , we get all sections in the doc. and for each
			// section we iterate for all tables in it.
			SectionCollection sections = doc.getSections();
			Iterator s = sections.iterator();
			while (s.hasNext() ){
				Section section = (Section) s.next();
				TableCollection tables = section.getBody().getTables();
				Iterator t = tables.iterator();
				while (t.hasNext()){
					// attributeAvailableMap is essentially a map of column headers vs column numbers the table. it looks like
					// name:1
					// description:2
					// traceto:3
					// tracefrom:4
					// folderpath:5
					// customattrib1name:n etc...
					HashMap attributeAvailableMap = new HashMap();
					
					Table table = (Table)t.next();
					// A table can have requirements if the first row has name or description in it's cell title.
					Row firstRow = table.getFirstRow();
					CellCollection firstRowCells = firstRow.getCells();
					// lets loop through the cells and figure out which one has a name or description.
					Iterator frc = firstRowCells.iterator();
					
					
					int cellCount = 0;
					while (frc.hasNext()){
						cellCount++;
						Cell cell = (Cell) frc.next();
						String cellContent = cell.getText();
						if (cellContent != null) {
							attributeAvailableMap.put(cellContent.trim().toLowerCase(), new Integer(cellCount));
						}
					}
					
		
					if ((attributeAvailableMap.get("name") != null) || (attributeAvailableMap.get("description") != null)){
						// either name or description column is non zero.
						// hence a table that can be processed.
						// lets loop through each row and get the nameColumn and descriptionColumnValues.
						int rowCount = 0;
						Iterator r = table.getRows().iterator();
						while (r.hasNext()){
							// attributeValueMap is a map of rtattributeids vs values for each row
							// since each row can be a diff req type (because of the folder value)
							// we create a new attribute value map for each row
							HashMap attributeValueMap = new HashMap();
							
							Row row = (Row) r.next();
							rowCount++;
							// since the first row has the headers, lets skip it.
							if (rowCount == 1){ 
								continue;
							}
							// now lets iterator through all the cells in this row and
							// pick up name and description values.
							String name = "";
							String description = "";
							String traceTo = "";
							String traceFrom = "";
							String folderPath = "";
							Iterator c = row.getCells().iterator();
							cellCount = 0;
							while (c.hasNext()){
								Cell cell = (Cell) c.next();
								cellCount++;
								
								try{
									if (attributeAvailableMap.get("name") != null){
										int nameColumnNumber = ((Integer) attributeAvailableMap.get("name")).intValue();
										if (cellCount == nameColumnNumber){
									
											// this cell has Name Value
											name = cell.getText();
											// we are getting a funny char at the end. lets drop it.
											// cludge, but seems to work.
											name = name.substring(0, name.length()-1).trim();
										}
									}
								}
								catch (Exception e){
									
								}
								try {
									if (attributeAvailableMap.get("description") != null){
										int descriptionColumnNumber = ((Integer) attributeAvailableMap.get("description")).intValue();
										if (cellCount == descriptionColumnNumber){
											// this cell has Description Value
											description = cell.getText();
											// we are getting a funny char at the end. lets drop it.
											// cludge, but seems to work.
											description = description.substring(0, description.length()-1).trim();
										}
									}
								}
								catch (Exception e){
									
								}
								try {
									if (attributeAvailableMap.get("traceto") != null){
										int traceToColumnNumber = ((Integer) attributeAvailableMap.get("traceto")).intValue();
										if (cellCount == traceToColumnNumber){
											// this cell has traceTo Value
											traceTo = cell.getText();
											// we are getting a funny char at the end. lets drop it.
											// cludge, but seems to work.
											traceTo = traceTo.substring(0, traceTo.length()-1);
										}
									}
								}
								catch (Exception e){
									
								}
								
								try {
									if (attributeAvailableMap.get("tracefrom") != null){
										int traceFromColumnNumber = ((Integer) attributeAvailableMap.get("tracefrom")).intValue();
										if (cellCount == traceFromColumnNumber){
											// this cell has traceFrom Value
											traceFrom = cell.getText();
											// we are getting a funny char at the end. lets drop it.
											// cludge, but seems to work.
											traceFrom = traceFrom.substring(0, traceFrom.length()-1);
										}
									}
								}
								catch (Exception e){
									
								}
								
								try {
									if (attributeAvailableMap.get("folderpath") != null){
										int folderPathColumnNumber = ((Integer) attributeAvailableMap.get("folderpath")).intValue();
										if (cellCount == folderPathColumnNumber){
											// this cell has folderPath Value
											folderPath = cell.getText();
											// we are getting a funny char at the end. lets drop it.
											// cludge, but seems to work.
											folderPath = folderPath.substring(0, folderPath.length()-1);
										}
									}
								}
								catch (Exception e){
									
								}
								
								// lets get the req type and rtatttributes from the given folder path.
								// if no folder path is given, lets use the current folder the user is in.
								if ((folderPath != null ) && (!folderPath.equals(""))){
									
									thisRowF = new Folder (folderPath, project.getProjectId() );
									thisRowRT = new RequirementType(thisRowF.getRequirementTypeId());
									thisRowRTAttributes = thisRowRT.getAllAttributesInRequirementType();
								}
								else {
									thisRowF = currentF;
									thisRowRT = currentRT;
									thisRowRTAttributes = currentRT.getAllAttributesInRequirementType();
								}
							
								// lets iterate through all the attribs for this rows req type.
								// for each attrib, get the name, and if this name exists in the attributeAvailableMap, get the column number.
								// if the current cell we are on is in this column, then put this rtattrib object and value in the attributeValueMap
								// for later processing / validation
								
								Iterator tRA = thisRowRTAttributes.iterator();
								while (tRA.hasNext()){
									RTAttribute rTAttribute = (RTAttribute) tRA.next();
									try {
										if (attributeAvailableMap.get(rTAttribute.getAttributeName().trim().toLowerCase()) != null){
											int attributeColumnNumber = ((Integer) attributeAvailableMap.get(rTAttribute.getAttributeName().trim().toLowerCase())).intValue();
											if (cellCount == attributeColumnNumber){
												// this cell has this rTAttributes  Value
												String attributeValue = cell.getText();
												// we are getting a funny char at the end. lets drop it.
												// cludge, but seems to work.
												attributeValue = attributeValue.substring(0, attributeValue.length()-1).trim();
												attributeValueMap.put(rTAttribute, attributeValue);
											}
										}
									}
									catch (Exception e){
										
									}
								}
							}
							
							// if name is non empty and description is then lets copy name to desc
							// if desc is non empty and name is empty then, lets trunc desc and copy it to name.
							
							if (name == null){
								name = "";
							}
							if (description == null){
								description = "";
							}
							
							if ((!name.equals("")) && (description.equals(""))){
								description = name;
							}
							if ((!description.equals("")) && (name.equals(""))){
								if (description.length() > 99){
									name = description.substring(0,95) + "...";
								}
								else {
									name = description;
								}
							}
							
							
							
							if ((!description.equals("")) || (!name.equals(""))){		
								// if name or description are non empty, lets create a req.
								
								currentLocation++;
								String currentLocationString = "::" + currentLocation + "::";
								if (!(locationNumberString.contains(currentLocationString))) {
									// the user chose to not create a req out of this one. So
									// lets skip it.
									continue;
								}

								String requirementPriority = "Medium";
								int requirementPctComplete = 0;
								String requirementExternalUrl = "";
								String requirementLockedBy = "";
								Requirement requirement = new Requirement("",
										thisRowRT.getRequirementTypeId(), thisRowF.getFolderId(), project.getProjectId(), name,
										description, requirementPriority,
										user.getEmailId(), requirementLockedBy, requirementPctComplete,
										requirementExternalUrl, user.getEmailId(),  databaseType);
								
								createdRequirements.add(requirement);
								if ( (!traceTo.equals("")) || (!traceFrom.equals(""))){
									// there is a traceto value. so lets do the tracing.f
									String status = RequirementUtil.createTraces(project, requirement.getRequirementId(), traceTo,
										traceFrom, project.getProjectId(), securityProfile,  databaseType );
								}
								
								// lets add the attribute values to the req
								Collection attributes = attributeValueMap.keySet();
								Iterator a = attributes.iterator();
								while (a.hasNext()){
									RTAttribute rTAttribute = (RTAttribute) a.next();
									String attributeValue = (String) attributeValueMap.get(rTAttribute);
									RequirementUtil.updateRequirementAttribute(requirement.getRequirementId(), rTAttribute.getAttributeId(), attributeValue.trim(),user.getEmailId(), databaseType);
									
								}
								
								RequirementUtil.setUserDefinedAttributes(requirement.getRequirementId(),user.getEmailId(), databaseType);
								// now that we have created the requirement, we need to 
								// empty the contents of this row and 
								// and create a hyperlink  to this requirement.
								
								
								String fullTag = requirement.getRequirementFullTag();
								// lets iterate through all the cells of this row again. this time
								// removing the text blurb and putting a URL shell instead.
								c = row.getCells().iterator();
								cellCount = 0;
								while (c.hasNext()){
									Cell cell = (Cell) c.next();
									cellCount++;
									
									
									if (attributeAvailableMap.get("name") != null){
										int nameColumnNumber = ((Integer) attributeAvailableMap.get("name")).intValue();
										if (cellCount == nameColumnNumber){
											// this cell has Name Value
											// empty the cell .
											cell.getChildNodes().clear();
											cell.appendChild(new Paragraph(doc));
											builder.moveTo(cell.getFirstParagraph());
											builder.insertHyperlink(fullTag+":name", fullTag+".name", false);
										}
										
									}
									if (attributeAvailableMap.get("description") != null){
										int descriptionColumnNumber = ((Integer) attributeAvailableMap.get("description")).intValue();
										if (cellCount == descriptionColumnNumber){
											// this cell has Description Value
											// empty the cell.
											cell.getChildNodes().clear();
											cell.appendChild(new Paragraph(doc));
											builder.moveTo(cell.getFirstParagraph());
											builder.insertHyperlink(fullTag+":description", fullTag+".description", false);
										}
									}
									if (attributeAvailableMap.get("traceto") != null){
										int traceToColumnNumber = ((Integer) attributeAvailableMap.get("traceto")).intValue();
										if (cellCount == traceToColumnNumber){
									
											// this cell has Description Value
											// empty the cell.
											cell.getChildNodes().clear();
											cell.appendChild(new Paragraph(doc));
											builder.moveTo(cell.getFirstParagraph());
											builder.insertHyperlink(fullTag+":traceto", fullTag+".traceto", false);
										}
									}
									if (attributeAvailableMap.get("tracefrom") != null){
										int traceFromColumnNumber = ((Integer) attributeAvailableMap.get("tracefrom")).intValue();
										if (cellCount == traceFromColumnNumber){
									
											// this cell has Description Value
											// empty the cell.
											cell.getChildNodes().clear();
											cell.appendChild(new Paragraph(doc));
											builder.moveTo(cell.getFirstParagraph());
											builder.insertHyperlink(fullTag+":tracefrom", fullTag+".tracefrom", false);
										}
									}
									if (attributeAvailableMap.get("folderpath") != null){
										int folderPathColumnNumber = ((Integer) attributeAvailableMap.get("folderpath")).intValue();
										if (cellCount == folderPathColumnNumber){
									
											// this cell has Description Value
											// empty the cell.
											cell.getChildNodes().clear();
											cell.appendChild(new Paragraph(doc));
											builder.moveTo(cell.getFirstParagraph());
											builder.insertHyperlink(fullTag+":folderpath", fullTag+".folderpath", false);
										}
									}
									
									

									// lets iterate through all the attribs for this rows req type.
									// for each attrib, that have a matching column
									// for each available attribute lets replace it with a watermark
									
									Iterator tRA = thisRowRTAttributes.iterator();
									while (tRA.hasNext()){
										RTAttribute rTAttribute = (RTAttribute) tRA.next();
										try {
											if (attributeAvailableMap.get(rTAttribute.getAttributeName().trim().toLowerCase()) != null){
												int attributeColumnNumber = ((Integer) attributeAvailableMap.get(rTAttribute.getAttributeName().trim().toLowerCase())).intValue();
												if (cellCount == attributeColumnNumber){
													// lets empty the cell and leave the watermark here
													cell.getChildNodes().clear();
													cell.appendChild(new Paragraph(doc));
													builder.moveTo(cell.getFirstParagraph());
													builder.insertHyperlink(fullTag+":"+rTAttribute.getAttributeName() , fullTag+"." +rTAttribute.getAttributeName(), false);
												}
											}
										}
										catch (Exception e){
											
										}
									}
								}
							}
						}
					}
				}
				
			}			
			// Now we need to add the createdRequirements and errorRows to the request.
			request.setAttribute("createdRequirements", createdRequirements);
			request.setAttribute("alertRows", alertRows);
			
			
			// now that we have created the requirements and replaced the
			// req text with the new req full tags, lets
			// write the new word template to replace the old one
			// and update the db entry for this word template.
			doc.save(wordTemplate.getTemplateFilePath());
			// since we are saving in the same location where the file
			// was originally located, we don't need to update the db.
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (createdRequirements);
	}

	
	
	
	// used to create all requirements using a location process (style)
	// NOTE : we use the EXACT same location process as locateRequirements method
	// and then we apply the check box the use user has selected to create the requierments.
	public static ArrayList createRequirementsFromWordTemplateByStyle(
			int templateId, Project project, int folderId, String styleName,
			String locationNumberString, User user, HttpServletRequest request,
			HttpServletResponse response, String databaseType) {
		ArrayList createdRequirements = new ArrayList();
		try {
			Folder folder = new Folder(folderId);
			WordTemplate wordTemplate = new WordTemplate(templateId,  databaseType);

			Document doc = new Document(wordTemplate.getTemplateFilePath());
			DocumentBuilder builder = new DocumentBuilder(doc);

			// Get all runs from the document.
			NodeCollection runs = doc.getChildNodes(NodeType.RUN, true);
			// Look through all paragraphs to find those with the specified
			// style.
			Iterator i = runs.iterator();

			// lets set some default attribute values for creating the req.
			int requirementTypeId = folder.getRequirementTypeId();
			String requirementPriority = "Medium";
			String requirementOwner = user.getEmailId();
			int requirementPctComplete = 0;
			String requirementExternalUrl = "";

			// we don't need to create a req out of every single requirement we
			// find.
			// we need to do that only for the location number in the string .
			// this string was formed base on which checkboxes the user checked.
			// if he checked the 1st, 20th, and 31st box, then we get a string
			// like 1::20::31
			// we are going to check for this location number in every
			// iteration.
			// to avoid a scenario where 1 gets matched agains 11, we search for
			// ::1::
			// to make that happen we append :: to the string.
			locationNumberString = "::" + locationNumberString;
			int currentLocation = 0;

			String lastRunStyle = "";
			String lastRunText = "";
			// this array list containts a list of Runs that comprise the lastRunStyle.
			// we will use them to empty out all the runs, if we decide that the lastRunStyle
			// was chosen by the user.
			ArrayList lastRuns = new ArrayList();
			
			
			// we declare the run variable outside the while loop
			// so that the value of the last current run is available
			// at the end of while loop for processing.
			Run run = null ;
			while (i.hasNext()) {
				// we run into a problem where runs of the same style are split
				// into different nodes. so we have this logic that 
				// combines different adjacent runs of same style into one string. 
				run = (Run) i.next();
				String runText = run.getText();
				String runStyle = run.getFont().getStyleName();

				
				if (runStyle.equals(lastRunStyle)){
					// continueing the same old run style.
					// so lets add the run text to lastRunText and capture this object
					// in the lastRun array list.
					// will come in handy when we decide to 
					lastRunText += runText;
					lastRuns.add(run);
				}
				else {
					// a new run style has begun.
					if (!(lastRunText.equals(""))){
						// since a new run style has started, lets process
						// the old run style and its text.
						if (lastRunStyle.equals(styleName)) {
							// the last run style matches the styleName chosen by user to
							// create the req.
							
							// a new run style has begun.
							currentLocation++;
							String currentLocationString = "::" + currentLocation + "::";
							
							// since a new run style has started, lets process
							// the old run style and its text.
		
							if (locationNumberString.contains(currentLocationString)){
								// Since the user has checked the box that indicates
								// that this is the req text we need to create a new req out of
		
								// we need to create a new requirement out of the req text here.
								// then add this req to the createdRequirements Array list
								// and change the text to the req full tag.
			
								// first lets create the requirement.
								// remember to create the req out of the lastRunText as that is the
								// one we have validated as wanting to create the req.
								
								
								String requirementDescription = lastRunText;
								String requirementName = lastRunText;
								if (requirementName.length() > 91) {
									requirementName = requirementName.substring(0, 90) + "...";
								}
		
								String requirementLockedBy = "";
								Requirement requirement = new Requirement("",
										requirementTypeId, folderId,
										project.getProjectId(), requirementName,
										requirementDescription, requirementPriority,
										requirementOwner, requirementLockedBy, requirementPctComplete,
										requirementExternalUrl, user.getEmailId(),  databaseType);
		
								// add it to the array list.
								createdRequirements.add(requirement);
		
								
								// also since we used up the last Run Style to create the req
								// lets locate all the lastRunObjects and empty them out in the word doc.
								Iterator k = lastRuns.iterator();
								while (k.hasNext()) {
									Run r = (Run) k.next();
									builder.moveTo(run);
									r.setText("");
									
								}
		
								// write the URL object to this req in the document.
								String fullTag = requirement.getRequirementFullTag();	
								builder.getFont().setSize(10);
								builder.getFont().setColor(Color.BLUE);
								builder.getFont().setUnderline(Underline.SINGLE);
								builder.insertHyperlink(fullTag, fullTag, false);
								builder.write(" ");
								
								
							}
						}
					}

					// lets re-set the lastRunStyle / Text values to current run Style and text so that
					// we can catch all runs of this style.
					lastRunStyle = runStyle;
					lastRunText = runText;
					lastRuns = new ArrayList();
					lastRuns.add(run);
				}
			}

			// since we always process the lastRunStyle , at this point, we haven't the value of the
			// last data point . i.e the last lastRunStyle hasnt' been processed yet. 
			// This is convoluted. Here is the example.
			// In the current run, we see if it's the same as last run, and if it has changed, then we process
			// the last run. However, this model leavs us with a situion where the last current run in the
			// while loop is never processed. This value has to be processed.
			// handling the last current run.
			currentLocation++;
			String currentLocationString = "::" + currentLocation + "::";

			
			if ( run != null &&
				!(lastRunText.equals("")) &&
				lastRunStyle.equals(styleName) && 
				locationNumberString.contains(currentLocationString) ) {
				// the last run style matches the styleName chosen by user to
				// create the req.
				// this is the req text we need to create a new req out of.


				// we need to create a new requirement out of the req text here.
				// then add this req to the createdRequirements Array list
				// and change the text to the req full tag.

				// first lets create the requirement.
				// remember to create the req out of the lastRunText as that is the
				// one we have validated as wanting to create the req.
				String requirementDescription = lastRunText;
				String requirementName = lastRunText;
				
				String requirementLockedBy = "";
				Requirement requirement = new Requirement("",
						requirementTypeId, folderId,
						project.getProjectId(), requirementName,
						requirementDescription, requirementPriority, 
						requirementOwner, requirementLockedBy, requirementPctComplete,
						requirementExternalUrl, user.getEmailId(),  databaseType);

				// add it to the array list.
				createdRequirements.add(requirement);

				// modify the URL object in the document.
				run.setText("");
				builder.moveTo(run);

				String fullTag = requirement.getRequirementFullTag();

				builder.getFont().setSize(10);
				builder.getFont().setColor(Color.BLUE);
				builder.getFont().setUnderline(Underline.SINGLE);
				builder.insertHyperlink(fullTag, fullTag, false);
				builder.writeln();
				
				// also since we used up the last Run Style to create the req
				// lets locate all the lastRunObjects and empty them out in the word doc.
				Iterator k = lastRuns.iterator();
				while (k.hasNext()) {
					Run r = (Run) k.next();
					r.setText("");
				}
				
			}

			
			
			// now that we have created the requirements and replaced the
			// req text with the new req full tags, lets
			// write the new word template to replace the old one
			// and update the db entry for this word template.
			doc.save(wordTemplate.getTemplateFilePath());
			// since we are saving in the same location where the file
			// was originally located, we don't need to update the db.
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (createdRequirements);
	}

	
	
	// Note : Same as createRequirementsFromWordTemplateByStyle. The difference is that
	// this mode, we leave the text in the word document, and add a watermark #BR-1# at the
	// beginning of the text. 
	//
	// used to create all requirements using a location process (style)
	// NOTE : we use the EXACT same location process as locateRequirements method
	// and then we apply the check box the use user has selected to create the requierments.
	public static ArrayList createRequirementsFromWordTemplateByStyleUpdatable(
			int templateId, Project project, int folderId, String styleName,
			String locationNumberString, User user, HttpServletRequest request,
			HttpServletResponse response, String databaseType) {
		ArrayList createdRequirements = new ArrayList();
		ArrayList updatedRequirements = new ArrayList();
		
		try {
			Folder folder = new Folder(folderId);
			WordTemplate wordTemplate = new WordTemplate(templateId,  databaseType);

			Document doc = new Document(wordTemplate.getTemplateFilePath());
			DocumentBuilder builder = new DocumentBuilder(doc);

			// Get all runs from the document.
			NodeCollection runs = doc.getChildNodes(NodeType.RUN, true);
			// Look through all paragraphs to find those with the specified
			// style.
			Iterator i = runs.iterator();

			// lets set some default attribute values for creating the req.
			int requirementTypeId = folder.getRequirementTypeId();
			String requirementPriority = "Medium";
			String requirementOwner = user.getEmailId();
			int requirementPctComplete = 0;
			String requirementExternalUrl = "";

			// we don't need to create a req out of every single requirement we
			// find.
			// we need to do that only for the location number in the string .
			// this string was formed base on which checkboxes the user checked.
			// if he checked the 1st, 20th, and 31st box, then we get a string
			// like 1::20::31
			// we are going to check for this location number in every
			// iteration.
			// to avoid a scenario where 1 gets matched agains 11, we search for
			// ::1::
			// to make that happen we append :: to the string.
			locationNumberString = "::" + locationNumberString;
			int currentLocation = 0;

			String lastRunStyle = "";
			String lastRunText = "";
			// this array list containts a list of Runs that comprise the lastRunStyle.
			// we will use them to empty out all the runs, if we decide that the lastRunStyle
			// was chosen by the user.
			ArrayList lastRuns = new ArrayList();
			
			
			// we declare the run variable outside the while loop
			// so that the value of the last current run is available
			// at the end of while loop for processing.
			// NOTE
			// NOTE
			// NOTE : we are no longer using the firstruninlaststyle and firstrunincurrentsytle
			// as we are getting lastRuns[0] to give us the first run object
			// NOTE
			// NOTE
			// NOTE
			Run run = null ;
			Run firstRunInCurrentStyle = null;
			Run firstRunInLastStyle = null;
			
			while (i.hasNext()) {
				// we run into a problem where runs of the same style are split
				// into different nodes. so we have this logic that 
				// combines different adjacent runs of same style into one string. 
				run = (Run) i.next();
				String runText = run.getText();
				String runStyle = run.getFont().getStyleName();

				
				if (runStyle.equals(lastRunStyle)){
					// continueing the same old run style.
					// so lets add the run text to lastRunText and capture this object
					// in the lastRun array list.
					// will come in handy when we decide to 
					lastRunText += runText;
					lastRuns.add(run);
					firstRunInLastStyle = firstRunInCurrentStyle;
				}
				else {
					// a new run style has begun.
					firstRunInCurrentStyle = run;
					
					if (!(lastRunText.equals(""))){
						// since a new run style has started, lets process
						// the old run style and its text.
						if (lastRunStyle.equals(styleName)) {
							// the last run style matches the styleName chosen by user to
							// create the req.
							
							// a new run style has begun.
							currentLocation++;
							String currentLocationString = "::" + currentLocation + "::";
							
							// since a new run style has started, lets process
							// the old run style and its text.
		
							if (locationNumberString.contains(currentLocationString)){
								// Since the user has checked the box that indicates
								// that this is the req text we need to create a new req out of
		
								// we need to create a new requirement out of the req text here.
								// then add this req to the createdRequirements Array list
								// and change the text to the req full tag.
			
								// first lets create the requirement.
								// remember to create the req out of the lastRunText as that is the
								// one we have validated as wanting to create the req.
								
								// lets see if this is an update of an existing req or a neq req.
								
								String requirementString = lastRunText;
								String requirementFullTag = "";
								// lets see if this requirement string has a water mark.
								Requirement requirement = null;
								try {
									if (requirementString != null){
										if (requirementString.trim().startsWith("##")){
											String[] reqParts = requirementString.split("##");
											
											if (reqParts.length  > 1){
												requirementFullTag = reqParts[1];
												requirement = new Requirement(requirementFullTag, project.getProjectId(), databaseType);
											}
											if (reqParts.length  > 2){
												requirementString = reqParts[2];
												
											}
										}
									}
								}
								catch (Exception e){
									e.printStackTrace();
								}
								
								
								if (requirement != null ){
									// this is an update request to an existing requirement.
									if (
											(!(requirement.getRequirementName().equals(requirementString)))
											||
											(!(requirement.getRequirementDescription().equals(requirementString)))
										){
									// either the name or the description is different , so lets update.
									requirement = new Requirement( requirement.getRequirementId(), requirementString,
										requirementString, 
										requirement.getRequirementPriority(), 
										requirement.getRequirementOwner(), 
										requirement.getRequirementPctComplete(), 
										requirement.getRequirementExternalUrl(), user.getEmailId() , request, databaseType);
									// once the requirement is update , lets refresh the object, 
									// so we get all the values (like folder path etc..)
									requirement = new Requirement(requirement.getRequirementId(), databaseType);
									updatedRequirements.add(requirement);
									}
								}
								else {
									String requirementDescription = lastRunText;
									
									String requirementName = lastRunText;
									
			
									String requirementLockedBy = "";
									requirement = new Requirement("",
											requirementTypeId, folderId,
											project.getProjectId(), requirementName,
											requirementDescription, requirementPriority,
											requirementOwner, requirementLockedBy, requirementPctComplete,
											requirementExternalUrl, user.getEmailId(),  databaseType);
			
									// add it to the array list.
									
									createdRequirements.add(requirement);
			
									if (firstRunInLastStyle != null){
										
										Run firstRun = (Run) lastRuns.get(0);
										if (firstRun != null ){
											builder.moveTo(firstRun);
											builder.write("##" + requirement.getRequirementFullTag() + "##" + " ");
										}
										
									}
									
								}
								
							}
						}
					}

					// lets re-set the lastRunStyle / Text values to current run Style and text so that
					// we can catch all runs of this style.
					lastRunStyle = runStyle;
					lastRunText = runText;
					lastRuns = new ArrayList();
					lastRuns.add(run);
				}
			}

			// since we always process the lastRunStyle , at this point, we haven't the value of the
			// last data point . i.e the last lastRunStyle hasnt' been processed yet. 
			// This is convoluted. Here is the example.
			// In the current run, we see if it's the same as last run, and if it has changed, then we process
			// the last run. However, this model leavs us with a situion where the last current run in the
			// while loop is never processed. This value has to be processed.
			// handling the last current run.
			currentLocation++;
			String currentLocationString = "::" + currentLocation + "::";

			
			if ( run != null &&
				!(lastRunText.equals("")) &&
				lastRunStyle.equals(styleName) && 
				locationNumberString.contains(currentLocationString) ) {
				// the last run style matches the styleName chosen by user to
				// create the req.
				// this is the req text we need to create a new req out of.


				// we need to create a new requirement out of the req text here.
				// then add this req to the createdRequirements Array list
				// and change the text to the req full tag.

				// first lets create the requirement.
				// remember to create the req out of the lastRunText as that is the
				// one we have validated as wanting to create the req.
				String requirementString = lastRunText;
				String requirementFullTag = "";
				// lets see if this requirement string has a water mark.
				Requirement requirement = null;
				try {
					if (requirementString != null){
						if (requirementString.trim().startsWith("##")){
							String[] reqParts = requirementString.split("##");
							
							if (reqParts.length  > 1){
								requirementFullTag = reqParts[1];
								requirement = new Requirement(requirementFullTag, project.getProjectId(), databaseType);
							}
							if (reqParts.length  > 2){
								requirementString = reqParts[2];
								
							}
						}
					}
				}
				catch (Exception e){
					e.printStackTrace();
				}
				
				
				if (requirement != null ){
					// this is an update request to an existing requirement.
					if (
							(!(requirement.getRequirementName().equals(requirementString)))
							||
							(!(requirement.getRequirementDescription().equals(requirementString)))
						){
						// either the name or the description is different , so lets update.
						requirement = new Requirement( requirement.getRequirementId(), requirementString,
							requirementString, 
							requirement.getRequirementPriority(), 
							requirement.getRequirementOwner(), 
							requirement.getRequirementPctComplete(), 
							requirement.getRequirementExternalUrl(), user.getEmailId() , request, databaseType);
						// once the requirement is update , lets refresh the object, 
						// so we get all the values (like folder path etc..)
						requirement = new Requirement(requirement.getRequirementId(), databaseType);
						
						updatedRequirements.add(requirement);
					}
				}
				else {
					String requirementDescription = lastRunText;
					String requirementName = lastRunText;
					
					String requirementLockedBy = "";
					requirement = new Requirement("",
							requirementTypeId, folderId,
							project.getProjectId(), requirementName,
							requirementDescription, requirementPriority, 
							requirementOwner, requirementLockedBy, requirementPctComplete,
							requirementExternalUrl, user.getEmailId(),  databaseType);
	
					// add it to the array list.
					
					createdRequirements.add(requirement);
	
					if (firstRunInCurrentStyle != null){
						builder.moveTo(firstRunInCurrentStyle);
						builder.write("##" + requirement.getRequirementFullTag() + "##" + " ");
					}
					
				}
			}

			
			
			// now that we have created the requirements and replaced the
			// req text with the new req full tags, lets
			// write the new word template to replace the old one
			// and update the db entry for this word template.
			doc.save(wordTemplate.getTemplateFilePath());
			// since we are saving in the same location where the file
			// was originally located, we don't need to update the db.
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("updatedRequirements", updatedRequirements);
		return (createdRequirements);
	}
	
	// used to locate all requirements using a location process (Paragraph
	// Search)
	// NOTE : we use the EXACT same location process as locateRequirements method
	// and then we apply the check box the use user has selected to create the requierments.
	
	public static ArrayList createRequirementsFromWordTemplateByParagraphSearch(
			int templateId, Project project, int folderId,
			String paragraphSearch, String locationNumberString, User user,
			HttpServletRequest request, HttpServletResponse response, String databaseType) {
		ArrayList createdRequirements = new ArrayList();
		try {
			Folder folder = new Folder(folderId);
			WordTemplate wordTemplate = new WordTemplate(templateId,  databaseType);

			Document doc = new Document(wordTemplate.getTemplateFilePath());
			DocumentBuilder builder = new DocumentBuilder(doc);

			String[] searchWords = {};
			if ((paragraphSearch != null) && (paragraphSearch.contains(" "))) {
				paragraphSearch = paragraphSearch.replace(" ", "");
			}
			if (paragraphSearch.contains(",")) {

			} else {
				paragraphSearch = paragraphSearch + ",";
			}
			searchWords = paragraphSearch.split(",");

			// Get all runs from the document.
			NodeCollection paragraphs = doc.getChildNodes(NodeType.PARAGRAPH,
					true);
			// Look through all paragraphs to find those with the specified
			// search string.
			Iterator i = paragraphs.iterator();

			// lets set some default attribute values for creating the req.
			int requirementTypeId = folder.getRequirementTypeId();
			String requirementPriority = "Medium";
			String requirementOwner = user.getEmailId();
			int requirementPctComplete = 0;
			String requirementExternalUrl = "";

			// we don't need to create a req out of every single requirement we
			// find.
			// we need to do that only for the location number in the string .
			// this string was formed base on which checkboxes the user checked.
			// if he checked the 1st, 20th, and 31st box, then we get a string
			// like 1::20::31
			// we are going to check for this location number in every
			// iteration.
			// to avoid a scenario where 1 gets matched agains 11, we search for
			// ::1::
			// to make that happen we append :: to the string.
			locationNumberString = "::" + locationNumberString;

			int currentLocation = 0;
			while (i.hasNext()) {
				Paragraph paragraph = (Paragraph) i.next();
				// lets see if the paragraph string contains the search words.
				String paragraphText = paragraph.getRange().getText();
				if ((paragraphText != null) && !(paragraphText.equals(""))) {
					// this para has some text. so lets go forward.
					if (searchWords.length > 0) {
						boolean match = false;
						for (int j = 0; j < searchWords.length; j++) {
							if (paragraphText.toLowerCase().contains(searchWords[j].toLowerCase())) { 
	
								// this means that there is a match.
								match = true;
							}
						}
						if (match) {
							// this paragraph is a match to the search string.
							currentLocation++;
							String currentLocationString = "::"
									+ currentLocation + "::";
							if (locationNumberString
									.contains(currentLocationString)) {
								// and the user decided to make this one a
								// requirement.
								// so lets create the requirement.

								// we need to create a new requirement out of
								// the URL Name.
								// then add this req to the createdRequirements
								// Array list
								// and change the URL Name and String to the req
								// full tag.

								// first lets create the requirement.
								String requirementDescription = paragraphText;
								String requirementName = paragraphText;
								if (requirementName.length() > 94) {
									requirementName = requirementName
											.substring(0, 94)
											+ "...";
								}

								String requirementLockedBy = "";
								Requirement requirement = new Requirement("",
										requirementTypeId, folderId, project
												.getProjectId(),
										requirementName,
										requirementDescription,
										requirementPriority, requirementOwner, requirementLockedBy,
										requirementPctComplete,
										requirementExternalUrl, user
												.getEmailId(),  databaseType);

								// add it to the array list.
								createdRequirements.add(requirement);

								// move to the para location, empty it
								
								//	builder.moveTo(paragraph.getNextSibling());
								//	paragraph.getRange().delete();
								
								String fullTag = requirement.getRequirementFullTag();

								builder.getCurrentParagraph().getParagraphFormat().setStyle(doc.getStyles().get("Title"));
								builder.writeln();
								builder.getFont().setSize(10);
								builder.getFont().setColor(Color.BLUE);
								builder.getFont().setUnderline(Underline.SINGLE);
								builder.insertHyperlink(fullTag, fullTag,false);
							}
						}
					}
				}
			}

			// now that we have created the requirements and replaced the
			// req text with the new req full tags, lets
			// write the new word template to replace the old one
			// and update the db entry for this word template.
			doc.save(wordTemplate.getTemplateFilePath());
			// since we are saving in the same location where the file
			// was originally located, we don't need to update the db.
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (createdRequirements);
	}

	// returns an array list of styles in the word document.
	public static ArrayList getWordTemplateStyles(int templateId, int folderId, String databaseType) {
		ArrayList styles = new ArrayList();
		try {
			WordTemplate wordTemplate = new WordTemplate(templateId,  databaseType);
			Document doc = new Document(wordTemplate.getTemplateFilePath());
			
			NodeCollection runs = doc.getChildNodes(NodeType.RUN, true);
			// Look through all paragraphs to find those with the specified
			// style.
			Iterator i = runs.iterator();
			while (i.hasNext()) {
				Run run = (Run) i.next();
				String runStyle = run.getFont().getStyleName();
				if (!(styles.contains(runStyle))) {
					if (!(runStyle.equals("Default Paragraph Font"))){
						// lets avoid addeing Default Paragraph Font , which happens
						// to be when no style is selected 
						styles.add(runStyle);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (styles);
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
	
	public static void embedXLSData(DocumentBuilder builder, RequirementAttachment attachment,
			int maxColumnsOfExcelToEmbedInWord){
		//
		// this is an old excel format
		//
		// we need to read the contents of the excel file, make a table inside this cell, so that
		// the contents can be printed as a table in the cell.
		// lets open the Excel File and start reading values...
		try {
			
			InputStream myxls = new FileInputStream(attachment.getFilePath());
			HSSFWorkbook wb = new HSSFWorkbook(myxls);
			HSSFSheet sheet = wb.getSheetAt(0); // first sheet
			
			// lets find the max column and max rows we want to print.
			// to do this , we iterate through all the cells from row 0 to row 500
			// and from col 0 to col maxColumnsOfExcelToEmbedInWord
			int maxCol= 0;
			int maxRow = 0;
			for (int i = 0; i < 500; i++) {
				HSSFRow currentExcelRow = sheet.getRow(i);
				if (currentExcelRow == null){
					continue;
				}
				for (int j = 0; j < maxColumnsOfExcelToEmbedInWord; j++){
					HSSFCell currentExcelCell = currentExcelRow.getCell(j);
					String currentExcelCellValue  = "";
					if (currentExcelCell == null){
						continue;
					}
					int cellType = currentExcelCell.getCellType();
					if ( cellType == HSSFCell.CELL_TYPE_STRING){
						// this is a string type
						currentExcelCellValue = currentExcelCell.getStringCellValue();
					}
					
					if ( cellType == HSSFCell.CELL_TYPE_NUMERIC){
						// number or date type.
						currentExcelCellValue = Double.toString(currentExcelCell.getNumericCellValue());
					}
					
					if ((currentExcelCellValue != null) && (!(currentExcelCellValue.equals("")))){
						// there is a cell value, lets see if we should move the column marker up
						if (j > maxCol){
							maxCol = j+1;
						}
						// lets see if we should move the max rows marker up.
						if (i > maxRow){
							maxRow = i+1;
						}
					}
					
					
				}
					
			}
				
				
			
			// lets create a table in word doc
			builder.startTable();
			for (int i = 0; i < maxRow; i++) {
				// lets get the row.
				HSSFRow currentExcelRow = sheet.getRow(i);
				if (currentExcelRow == null){
					continue;
				}
				
				
				for (int j = 0; j < maxCol; j++){
					try {
						HSSFCell currentExcelCell = currentExcelRow.getCell(j);
						
						String currentExcelCellValue  = "";
						if (currentExcelCell != null){
							int cellType = currentExcelCell.getCellType();
							if ( cellType == HSSFCell.CELL_TYPE_STRING){
								// this is a string type
								currentExcelCellValue = currentExcelCell.getStringCellValue();
							}
							
							if ( cellType == HSSFCell.CELL_TYPE_NUMERIC){
								// number or date type.
								currentExcelCellValue = Double.toString(currentExcelCell.getNumericCellValue());
							}
						}
						else {
							currentExcelCellValue = "";
						}
						
						
						// lets create a cell and print this value.
						builder.insertCell();
						setUpCell(builder,(580 / (maxCol + 1)));
						builder.write(currentExcelCellValue);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				// now that the row is over, lets end the table row and start a new row
				builder.endRow();
	
			}
			builder.endTable();
			builder.writeln("");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}


	public static void embedXLSXData(DocumentBuilder builder, RequirementAttachment attachment, 
			int maxColumnsOfExcelToEmbedInWord){
		//
		// this is an NEW excel format
		//
		// we need to read the contents of the excel file, make a table inside this cell, so that
		// the contents can be printed as a table in the cell.
		// lets open the Excel File and start reading values...
		try {
			
			InputStream myxls = new FileInputStream(attachment.getFilePath());
			
			Workbook wb = WorkbookFactory.create(myxls );

			org.apache.poi.ss.usermodel.Sheet sheet = wb.getSheetAt(0); // first sheet
			// lets find the max column and max rows we want to print.
			// to do this , we iterate through all the cells from row 0 to row 500
			// and from col 0 to col maxColumnsOfExcelToEmbedInWord
			int maxCol= 0;
			int maxRow = 0;
			for (int i = 0; i < 500; i++) {
				org.apache.poi.ss.usermodel.Row currentExcelRow = sheet.getRow(i);
				if (currentExcelRow == null){
					continue;
				}
				for (int j = 0; j < maxColumnsOfExcelToEmbedInWord; j++){
					org.apache.poi.ss.usermodel.Cell currentExcelCell = currentExcelRow.getCell(j);
					String currentExcelCellValue  = "";
					if (currentExcelCell == null){
						continue;
					}
					int cellType = currentExcelCell.getCellType();
					if ( cellType == HSSFCell.CELL_TYPE_STRING){
						// this is a string type
						currentExcelCellValue = currentExcelCell.getStringCellValue();
					}
					
					if ( cellType == HSSFCell.CELL_TYPE_NUMERIC){
						// number or date type.
						currentExcelCellValue = Double.toString(currentExcelCell.getNumericCellValue());
					}
					
					if ((currentExcelCellValue != null) && (!(currentExcelCellValue.equals("")))){
						// there is a cell value, lets see if we should move the column marker up
						if (j > maxCol){
							maxCol = j+1;
						}
						// lets see if we should move the max rows marker up.
						if (i > maxRow){
							maxRow = i+1;
						}
					}
					
					
				}
					
			}
			
			
			
			
			// lets create a table in word doc
			builder.startTable();
			for (int i = 0; i < maxRow; i++) {
				// lets get the row.
				org.apache.poi.ss.usermodel.Row currentExcelRow = sheet.getRow(i);
				if (currentExcelRow == null){
					continue;
				}
				
				
				for (int j = 0; j < maxCol; j++){
					try {

						org.apache.poi.ss.usermodel.Cell currentExcelCell = currentExcelRow.getCell(j);
						String currentExcelCellValue  = "";
						if (currentExcelCell != null){
							int cellType = currentExcelCell.getCellType();
							if ( cellType == HSSFCell.CELL_TYPE_STRING){
								// this is a string type
								currentExcelCellValue = currentExcelCell.getStringCellValue();
							}
							
							if ( cellType == HSSFCell.CELL_TYPE_NUMERIC){
								// number or date type.
								currentExcelCellValue = Double.toString(currentExcelCell.getNumericCellValue());
							}
						}
						else {
							currentExcelCellValue = "";
						}
						
						
						// lets create a cell and print this value.
						builder.insertCell();
						setUpCell(builder,(580 / (maxCol + 1)));
						builder.write(currentExcelCellValue);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				// now that the row is over, lets end the table row and start a new row
				builder.endRow();
	
			}
			builder.endTable();
			builder.writeln("");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
