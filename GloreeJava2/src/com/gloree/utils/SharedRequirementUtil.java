package com.gloree.utils;

//GloreeJava2

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import com.gloree.beans.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;




import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;

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

public class SharedRequirementUtil {

	public static ArrayList getSharedProjects(){

		ArrayList sharedProjects = new ArrayList();
		java.sql.Connection con = null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the core project info for all projects int the system,
			// creates a project object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "select distinct p.id, p.short_name, p.name, p.project_type, p.description," +
				" p.owner, p.website, p.organization, p.tags,  p.restricted_domains, p.enable_tdcs, p.enable_agile_scrum, " +
				" p.billing_organization_id, " +
				" p.number_of_requirements, p.created_by, p.created_dt, p.last_modified_by, p.last_modified_dt , p.archived, p.hide_priority " +
				" from gr_projects p, gr_requirement_types rt" +
				" where rt.sr_rt_baseline_ids  like 'rTBaselineId%' " +
				" and rt.sr_publish_status = 'published'" +
				" and rt.project_id = p.id  ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			ResultSet rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int projectId = rs.getInt("id");
				String shortName = rs.getString("short_name");
				String projectName = rs.getString("name");

				String projectType = rs.getString("project_type");
				String projectDescription = rs.getString ("description");
				
				String projectOwner = rs.getString("owner");
				String projectWebsite = rs.getString("website");
				String projectOrganization= rs.getString("organization");
				String projectTags = rs.getString("tags");
				
				
				String restrictedDomains = rs.getString("restricted_domains");
				int enableTDCS = rs.getInt("enable_tdcs");
				int enableAgileScrum = rs.getInt("enable_agile_scrum");
				int billingOrganizationId = rs.getInt("billing_organization_id");
				int numberOfRequirements = rs.getInt("number_of_requirements");
				String createdBy = rs.getString("created_by");
				//Date createdDt = rs.getDate("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by") ;
				//Date lastModifiedDt = rs.getDate("last_modified_by");
				int archived = rs.getInt("archived");
				int hidePriority = rs.getInt("hide_priority");
				
				
				
				//TODO : at some point see how we can make DATE fields works.
				Project project = new Project(projectId, shortName, projectName	, projectType,
					projectDescription, 
					projectOwner, projectWebsite, projectOrganization, projectTags, 
					restrictedDomains, enableTDCS,enableAgileScrum, 
					billingOrganizationId,
					numberOfRequirements, createdBy, lastModifiedBy, archived, hidePriority);
				sharedProjects.add(project);
			}
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		return (sharedProjects);
	}
	// Lets make sure that every source Folder and sub folder also appears in the Target Requirement with the same structre
				;

		public static void synchronizeTargetProjectFolderStructure(Project sourceProject, RequirementType sRT, Project targetProject, RequirementType tRT,
				User user, String databaseType){

			java.sql.Connection con = null;
			try {
				// lets get all the folders in the source requirement type
				
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();

				ArrayList sourceFolders = ProjectUtil.getFolderInAProject(sourceProject.getProjectId());
				ArrayList targetFolders = ProjectUtil.getFolderInAProject(targetProject.getProjectId());
				
				
				Iterator sF = sourceFolders.iterator();
				while (sF.hasNext()){
					Folder sFolder = (Folder) sF.next();
					System.out.println("srt sFolder is " + sFolder.getFolderPath());
					// if source Parent Folder doesn't exist, then the source folder was a root level folder 
					// this means the target folder was also a root level folder
					// Root level folders are created when the Req Type was created. So we need to do nothing.
					if (sFolder.getParentFolderId() == 0 ){
						continue;
					}	
					Folder sParentFolder = (Folder) new Folder(sFolder.getParentFolderId());
					

					
					String sParentFolderPath = sParentFolder.getFolderPath();
					String tParentFolderPath = sParentFolderPath.replaceFirst(sRT.getRequirementTypeName(), tRT.getRequirementTypeName());

					// we need to get the Target Parent Folder Id.
					// to do that, we first need to find the Target Parent Folder path
					// We can deduce that from the sourceParentFolderPath, after we replace the root folder name with target root folder name.
					
					
					Folder tParentFolder = new Folder(tParentFolderPath, targetProject.getProjectId() );
					
							
							
					if (sFolder.getRequirementTypeId() == sRT.getRequirementTypeId()){
						// this is a source Req Type folder.
						// lets see if this source folder exists in the destination
						// we want to compare folder paths. However, the root folder name is different in both , so a) we need to check only if the folder level is > 1
						// and then remove the root folder name from the string.
						if (sFolder.getFolderLevel() == 1 ){
							// root folder . skip
							
						}
						else {
							// not a root folder. so lets make sure that this guy is in.
							String sourceFolderPath = sFolder.getFolderPath();
							// lets remove the root folder name from this
							sourceFolderPath = sourceFolderPath.replaceFirst(sRT.getRequirementTypeName(), "");
							
							
							boolean folderExists = false;
							Iterator tF  = targetFolders.iterator();
							while (tF.hasNext()){
								Folder tFolder = (Folder) tF.next();
								if (tFolder.getRequirementTypeId() != tRT.getRequirementTypeId()){
									// since this folder is in a different req type, ignore.
									continue;
								}
								String targetFolderPath = tFolder.getFolderPath();
								// lets remove the target root folder name from the folder path
							
								targetFolderPath = targetFolderPath.replaceFirst(tRT.getRequirementTypeName(), "");
								if (sourceFolderPath.equals(targetFolderPath)){
									folderExists = true;
								}
								
							}
							if (folderExists == false){
								// lets create this folder.
								
								Folder folder = new Folder( tParentFolder.getFolderId(), targetProject.getProjectId(), sFolder.getFolderName() , 
										sFolder.getFolderDescription(), sFolder.getFolderOrder(), user.getEmailId(), databaseType);
								
								
							}
							
						}
					}
				}
				
				con.close();
			} catch (Exception e) {
				
				e.printStackTrace();
			}  finally {
				if (con != null) {
					try {con.close();} catch (Exception e) {}
					con = null;
				}
			}
			
		}


	public static int getTargetRequirementId(int projectId, int sourceRequirementId){

		int targetRequirementId = 0;
		java.sql.Connection con = null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			//
			// This sql gets the core project info for all projects int the system,
			// creates a project object for each row and puts them the array list.
			// TODO : change this SQL so that we return ONLY the project to which the user has access.
			//
			String sql = "select id from gr_requirements " +
				" where project_id = ? " +
				" and source_requirement_id = ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, sourceRequirementId);
			ResultSet rs = prepStmt.executeQuery();
			
			while (rs.next()){
				targetRequirementId = rs.getInt("id");
			}
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		return (targetRequirementId);
	}

	public static ArrayList getSharedRequirementTypes(int sharedProjectId){

		ArrayList sharedRequirementTypes = new ArrayList();
		java.sql.Connection con = null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select rt.id " + 
				" from gr_requirement_types rt" +
				" where rt.sr_rt_baseline_ids like 'rTBaselineId%'  " +
				" and rt.sr_publish_status = 'published'" +
				" and rt.project_id = ? " ;
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sharedProjectId);
			ResultSet rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int requirementTypeId = rs.getInt("id");
				SharedRequirementType sRT = new SharedRequirementType(requirementTypeId);
				sharedRequirementTypes.add(sRT);
			}
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		return (sharedRequirementTypes);
	}

	public static ArrayList getFilteredSharedRequirements(SharedRequirementType sRT,
			int sRRTBaselineId, String customAttributeSearch, 
			int targetProjectId, String databaseType){

		ArrayList sharedRequirements = new ArrayList();
		java.sql.Connection con = null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " SELECT distinct r.id, r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag "
				+ " FROM gr_requirements r , gr_requirement_types rt, gr_folders f , gr_requirement_baselines rb "
				+ " where rt.id = ?  " +
					" and rb.rt_baseline_id = ? " +
					" and rb.requirement_id = r.id " 
				+ " and   r.folder_id = f.id "
				+ " and r.requirement_type_id = rt.id ";

			// Now, lets handle custom attributes.
			// customAttributeSearch will have
			// avalue1:--:avalu2sel1:##:avalu2sel1:##:avalu2sel2:--:avalue3

			String[] customAttributeSearches = customAttributeSearch.split(":--:");
			for (int i = 0; i < customAttributeSearches.length; i++) {
				String cAS = customAttributeSearches[i];
				if ((cAS != null) && (!(cAS.equals("")))) {
					if (cAS.contains(":##:")) {
						// this is a drop down with multiple selection.
						// needs special handling.
						String[] options = cAS.split(":##:");

						String orSQL = "";
						for (int j = 0; j < options.length; j++) {
							
							if (databaseType.equals("mySQL")){
								orSQL += " r.user_defined_attributes like  ('%"	+ options[j] + "%') " + "or";
							}
							else{
								orSQL += " upper(to_char(r.user_defined_attributes)) like  ('%"	+ options[j].toUpperCase() + "%') " + "or";
							}
						}
						// drop the last 'or'
						if (orSQL.contains("or")) {
							orSQL = (String) orSQL.subSequence(0, orSQL
									.lastIndexOf("or"));
						}
						sql += " and (" + orSQL + ") ";
					} else {
						// this is regular text box.
						if (cAS.contains(":#:")){
							// cAS has something like Customer:#:IBM
							String [] customAttribute = cAS.split(":#:");
							String customLabel = customAttribute[0];
							String customValue = customAttribute[1];
							String customSearchString =  "%" + customLabel + ":#:" + "%" + customValue + "%";
							
							if (databaseType.equals("mySQL")){
								sql += " and r.user_defined_attributes like  ('" + customSearchString.trim() + "') ";
							}
							else{
								sql += " and upper(to_char(r.user_defined_attributes)) like  ('" + customSearchString.trim().toUpperCase() + "') ";
							}
						}
						
					}
				}
			}

			sql += "  order by r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag";

		
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sRT.getSRTId());
			prepStmt.setInt(2, sRRTBaselineId);
			ResultSet rs = prepStmt.executeQuery();
			
			while (rs.next()){
				int sharedRequirementId = rs.getInt("id");
				
				SharedRequirement sR = new SharedRequirement(sharedRequirementId, sRRTBaselineId, targetProjectId,   databaseType);
				sharedRequirements.add(sR);
			}
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		return (sharedRequirements);
	}
	
	// this routine is called after an shared req import and does the following.
	// 1. creates an excel file with the imported data
	// 2. pushes  a copy of TDCS of the source project
	// 3. pushes a copy of TDCS to the target project.
	// it tries to make a TDCS entry for every req type in the target project
	// and a entry for every imported project in the source project.
	public static void pushHistoryToTDCS(HttpServletRequest request,  HttpServletResponse response,
			String rootDataDirectory, SharedRequirementType sRT, RequirementType targetRequirementType, 
			ArrayList createdRequirements, ArrayList updatedRequirements, 
			Project project, User user, String databaseType){
			
			RequirementType sourceRequirementType = sRT.getRequirementType();
			Project sourceProject = new Project(sourceRequirementType.getProjectId(),   databaseType);
			String fileName = createExcelFileOfImportEffort(request, response,
					rootDataDirectory, sRT, createdRequirements, updatedRequirements, 
					project, user,  databaseType);
			
			

			// lets move this file to a permanent location in the target project.
			
			// first let make sure that the E://TraceCloud/ProjectId/TDCS/unique directory exists.
			Calendar cal = Calendar.getInstance();
			// if rootDataDirectory/TraceCloud does not exist, lets create it.
			File traceCloudRoot = new File (rootDataDirectory + File.separator +  "TraceCloud");
			if (!(traceCloudRoot.exists() )){
			    new File(rootDataDirectory + File.separator +  "TraceCloud").mkdir();
			}
			

			// if rootDataDirectory/TraceCloud/ProjectId does not exist, lets create it.
			File projectRoot  = new File (rootDataDirectory + File.separator +  "TraceCloud" + File.separator + project.getProjectId());
			if (!(projectRoot.exists() )){
			    new File(rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  project.getProjectId()).mkdir();
			}
			
			// lets create a TDCS folder in the project.
			File projectAttachmentRoot  = new File (rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  project.getProjectId() + File.separator +  "TDCS");
			if (!(projectAttachmentRoot.exists() )){
			    new File(rootDataDirectory +  File.separator +  "TraceCloud" + File.separator +  project.getProjectId() + File.separator +  "TDCS").mkdir();
			}
			
			// lets create a unique directory within the ProjectRoot to store
			// the file.
			String targetDirName; 
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy-hhmm-ss");
			String today =  sdf.format(cal.getTime());
		    targetDirName= rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  project.getProjectId() + File.separator +  "TDCS" + File.separator +  user.getUserId() + "-" + today ;	    
		    new File(targetDirName).mkdir();

		    // now that we have the target directory, lets copy the file over.
		    // File (or directory) to be moved 
		    File file = new File(fileName); 
		    // Destination directory 
		    File dir = new File(targetDirName); 
		    // Move file to new directory 
		    boolean success = file.renameTo(new File(dir, file.getName())); 
		    
		    String attachmentType = "excel";
			String targetFilePath = targetDirName + File.separator +  file.getName();
			String title= "'" + sourceRequirementType.getRequirementTypeName()  +  "' Imported from Project '" +
				sourceProject.getProjectName() + "' on " + Calendar.getInstance().getTime().toString();
			String description = "'" + sourceRequirementType.getRequirementTypeName()  +  "' Requirements from Project '" +
			sourceProject.getProjectName() + "' imported by " + user.getEmailId() + " on " + 
			Calendar.getInstance().getTime().toString();
			
			// since sharedReq is a onSite only feature, we can set uniqueTDCSFullTag to true.
			String uniqueTDCSFullTag = "true";
		
			TDCSDocument targetTDCSDocument = TDCSUtil.createNewTDCSDocument(uniqueTDCSFullTag, project.getProjectId() ,
			targetRequirementType.getRootFolderId(),title,
			description,attachmentType,targetFilePath, user,   databaseType);
				
			
			// Get the session. It should have the last View Report in memory.
	    	HttpSession session = request.getSession(true);
	    	session.setAttribute("targetTDCSDocument", targetTDCSDocument);
	    	
	    	// lets create a document in the source project also.
	    	// first lets get the new location for this file.
			// first let make sure that the E://TraceCloud/ProjectId/TDCS/unique directory exists.

			// if rootDataDirectory/TraceCloud/ProjectId does not exist, lets create it.
			projectRoot  = new File (rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  sourceProject.getProjectId());
			if (!(projectRoot.exists() )){
			    new File(rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  sourceProject.getProjectId()).mkdir();
			}
			
			// lets create a TDCS folder in the project.
			projectAttachmentRoot  = new File (rootDataDirectory + "/TraceCloud/" + sourceProject.getProjectId() + "/TDCS");
			if (!(projectAttachmentRoot.exists() )){
			    new File(rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  sourceProject.getProjectId() + File.separator +  "TDCS").mkdir();
			}
			
			// lets create a unique directory within the ProjectRoot to store
			// the file.
			sdf = new SimpleDateFormat("dd-MM-yy-hhmm-ss");
			today =  sdf.format(cal.getTime());
		    targetDirName= rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  sourceProject.getProjectId() + File.separator +  "TDCS" +File.separator +  user.getUserId() + "-" + today ;	    
		    new File(targetDirName).mkdir();
	    	

		    // lets make a copy of the file in the TargetProject in the source project TDCS.
		    String oldFilePath = targetTDCSDocument.getCurrentVersionFilePath();
		    String newFilePath = targetDirName  +File.separator +  file.getName(); 
		    
		    try {
			    File in = new File(oldFilePath);
			    File out  = new File (newFilePath);
			    FileChannel inChannel = new FileInputStream(in).getChannel();
		        FileChannel outChannel = new FileOutputStream(out).getChannel();
		        try {
		            inChannel.transferTo(0, inChannel.size(),
		                    outChannel);
		        } 
		        catch (IOException e) {
		           e.printStackTrace();
		        }
		        finally {
		            if (inChannel != null) inChannel.close();
		            if (outChannel != null) outChannel.close();
		        }
		    }
		    catch (Exception e){
		    	e.printStackTrace();
		    }
		    attachmentType = "excel";
			title= "'" + sourceRequirementType.getRequirementTypeName()  +  "' Imported from Project '" +
				sourceProject.getProjectName() + "' to Project '"+ project.getProjectName()+ "' on " + Calendar.getInstance().getTime().toString();
			description = "'" + sourceRequirementType.getRequirementTypeName()  +  "' Imported from Project '" +
			sourceProject.getProjectName() + "' to Project '"+ project.getProjectName() +"' imported by " + user.getEmailId() + " on " + 
			Calendar.getInstance().getTime().toString();
			
			// since sharedReq is a onSite only feature, we can set uniqueTDCSFullTag to true.
			uniqueTDCSFullTag = "true";
		
			TDCSDocument sourceTDCSDocument = TDCSUtil.createNewTDCSDocument(uniqueTDCSFullTag, sourceProject.getProjectId() ,
			sourceRequirementType.getRootFolderId(),title,
			description,attachmentType,newFilePath, user,   databaseType);
				
			session.setAttribute("sourceTDCSDocument", sourceTDCSDocument);
	    	
	}
	
	
	// creates a temp excel file of the import effort.
	public static String createExcelFileOfImportEffort(HttpServletRequest request,  HttpServletResponse response,
		String rootDataDirectory, SharedRequirementType sRT, 
		ArrayList createdRequirements, ArrayList updatedRequirements, 
		Project project, User user, String databaseType){
		
		String fileName = "";
		// Get the session. It should have the last View Report in memory.
    	HttpSession session = request.getSession(true);
    	
    	try {
    		
    		ArrayList sharedAttributes = sRT.getAllSharedAttributesInRequirementType();

    		HSSFWorkbook wb = new HSSFWorkbook();
    	
    		HSSFCellStyle headerStyle = wb.createCellStyle();
    		headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
    	    headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    
    	    HSSFCellStyle wrappedStyle = wb.createCellStyle();
    	    wrappedStyle.setWrapText(true);
    	    
    	    
    	    // lets build all the sheets in this file.
    	    HSSFSheet infoSheet  = wb.createSheet("Report Info");
    	    HSSFSheet reportSheet = wb.createSheet("List Report");


	    	
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
    		cellA.setCellValue(new HSSFRichTextString ("Shared Requirements Import Date"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		HSSFCell cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (Calendar.getInstance().getTime().toString()));
    		
    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Imported By "));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (user.getEmailId()));

    		startRow += 2;
    		
    		// project Info.
    		startRow += 4;
    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Import Project Prefix"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getShortName()));

    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Import Project Name"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getProjectName()));

    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Import Project Description"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getProjectDescription()));
    		
    		
    		int j = 0;
    		
            
            // lets create the header row.
    		row     = reportSheet.createRow((short)0);	
    		    		
    		// Print the header row for the excel file.
    		int cellNum = 0;
    		
    		HSSFCell cell = row.createCell(cellNum);
    		cell.setCellValue(new HSSFRichTextString ("Import Status    "));
    		cell.setCellStyle(headerStyle);
    		reportSheet.autoSizeColumn(cellNum++);

    		cell = row.createCell(cellNum);
    		cell.setCellValue(new HSSFRichTextString ("Source Requirement Tag"));
    		cell.setCellStyle(headerStyle);
    		reportSheet.autoSizeColumn(cellNum++);

    		cell = row.createCell(cellNum);
    		cell.setCellValue(new HSSFRichTextString ("Source Requirement Version"));
    		cell.setCellStyle(headerStyle);
    		reportSheet.autoSizeColumn(cellNum++);
    		
    		cell = row.createCell(cellNum);
    		cell.setCellValue(new HSSFRichTextString ("Source Requirement Name                                "));
    		cell.setCellStyle(headerStyle);
    		reportSheet.autoSizeColumn(cellNum++);
    		
    		
    		cell = row.createCell(cellNum);
    		cell.setCellValue(new HSSFRichTextString ("Source Baseline                                        "));
    		cell.setCellStyle(headerStyle);
    		reportSheet.autoSizeColumn(cellNum++);

    	    
    		cell = row.createCell(cellNum);
    		cell.setCellValue(new HSSFRichTextString ("URL To Source Requirement                                                                                                          "));
    		cell.setCellStyle(headerStyle);
    		reportSheet.autoSizeColumn(cellNum++);
    		
    		
    		cell = row.createCell(cellNum);
    		cell.setCellValue(new HSSFRichTextString ("Target Requirement Tag"));
    		cell.setCellStyle(headerStyle);
    		reportSheet.autoSizeColumn(cellNum++);

    		cell = row.createCell(cellNum);
    		cell.setCellValue(new HSSFRichTextString ("Target Requirement Version"));
    		cell.setCellStyle(headerStyle);
    		reportSheet.autoSizeColumn(cellNum++);
    		
    		cell = row.createCell(cellNum);
    		cell.setCellValue(new HSSFRichTextString ("Target Requirement Name                                "));
    		cell.setCellStyle(headerStyle);
    		reportSheet.autoSizeColumn(cellNum++);
    		

    		cell = row.createCell(cellNum);
    		cell.setCellValue(new HSSFRichTextString ("Target Baseline                                        "));
    		cell.setCellStyle(headerStyle);
    		reportSheet.autoSizeColumn(cellNum++);

    		cell = row.createCell(cellNum);
    		cell.setCellValue(new HSSFRichTextString ("URL To Target Requirement                                                                                                          "));
    		cell.setCellStyle(headerStyle);
    		reportSheet.autoSizeColumn(cellNum++);
    		
			Iterator s = sharedAttributes.iterator();
			while (s.hasNext()){
				SharedRequirementTypeAttribute sA = (SharedRequirementTypeAttribute) s.next();
				if (sA.getSRACopyable() == 1){
	        		cell = row.createCell(cellNum);
	        		cell.setCellValue(new HSSFRichTextString (sA.getRTAttribute().getAttributeName() + "                         "));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(cellNum++);
				}
			}
            
			// lets print the created requirements
			Iterator i = createdRequirements.iterator();
            while ( i.hasNext() ) {
	    		SharedRequirement sharedRequirement = (SharedRequirement) i.next();
				Requirement sourceRequirement = new Requirement(sharedRequirement.getSourceRequirementId(),   databaseType);
				Requirement targetRequirement = new Requirement(sharedRequirement.getTargetRequirementId(),   databaseType);
				
				
	    		// Create a row and put some cells in it. Rows are 0 based.
	    		j++;
	    		
	    		// for the first row, print the header and user defined columns headers. etc..
	    		if (j == 1){

	    		}

	    		// print the data rows now.
	    		row     = reportSheet.createRow(j);
			    
	    		
	    		// Create a cell and put a value in it.
	    		// we have decided to not make cell0 a hyperlink to source because Excel 97-03 was having trouble
	    		// saving these files.
	    		
	    		cellNum = 0;
			  
	    		cell = row.createCell(cellNum);
			    cell.setCellValue(new HSSFRichTextString ("Created"));
			    cell.setCellStyle(wrappedStyle);
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (sourceRequirement.getRequirementFullTag()));
			    cell.setCellStyle(wrappedStyle);
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (Integer.toString(sharedRequirement.getSourceRequirementVersion())));
			    cell.setCellStyle(wrappedStyle);

			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (sharedRequirement.getSourceRequirementName()));
			    cell.setCellStyle(wrappedStyle);

			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (sharedRequirement.getSourceRequirementBaselineName() ));
			    cell.setCellStyle(wrappedStyle);
			    
			    String sourceRequiremntURL = ProjectUtil.getURL(request,sourceRequirement.getRequirementId() ,"requirement");
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (sourceRequiremntURL));
			    cell.setCellStyle(wrappedStyle);
			    

			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (targetRequirement.getRequirementFullTag()));
			    cell.setCellStyle(wrappedStyle);
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (Integer.toString(sharedRequirement.getTargetRequirementVersion())));
			    cell.setCellStyle(wrappedStyle);

			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (sharedRequirement.getTargetRequirementName()));
			    cell.setCellStyle(wrappedStyle);

			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (sharedRequirement.getTargetRequirementBaselineName() ));
			    cell.setCellStyle(wrappedStyle);
			    
			    String targetRequiremntURL = ProjectUtil.getURL(request,targetRequirement.getRequirementId() ,"requirement");
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (targetRequiremntURL));
			    cell.setCellStyle(wrappedStyle);

				s = sharedAttributes.iterator();
				while (s.hasNext()){
					SharedRequirementTypeAttribute sA = (SharedRequirementTypeAttribute) s.next();
					if (sA.getSRACopyable() == 1){
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (sourceRequirement.getAttributeValue(sA.getRTAttribute().getAttributeId())));
					    cell.setCellStyle(wrappedStyle);
					}
				}
			    
	    	}

            
            // lets print the updated requirements.
            i = updatedRequirements.iterator();
            while ( i.hasNext() ) {
	    		SharedRequirement sharedRequirement = (SharedRequirement) i.next();
				Requirement sourceRequirement = new Requirement(sharedRequirement.getSourceRequirementId(),   databaseType);
				Requirement targetRequirement = new Requirement(sharedRequirement.getTargetRequirementId(),   databaseType);
				
				
	    		// Create a row and put some cells in it. Rows are 0 based.
	    		j++;
	    		
	    		// for the first row, print the header and user defined columns headers. etc..
	    		if (j == 1){

	    		}

	    		// print the data rows now.
	    		row     = reportSheet.createRow(j);
			    
	    		
	    		// Create a cell and put a value in it.
	    		// we have decided to not make cell0 a hyperlink to source because Excel 97-03 was having trouble
	    		// saving these files.
	    		
	    		cellNum = 0;
			  
	    		cell = row.createCell(cellNum);
			    cell.setCellValue(new HSSFRichTextString ("Updated"));
			    cell.setCellStyle(wrappedStyle);
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (sourceRequirement.getRequirementFullTag()));
			    cell.setCellStyle(wrappedStyle);
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (Integer.toString(sharedRequirement.getSourceRequirementVersion())));
			    cell.setCellStyle(wrappedStyle);

			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (sharedRequirement.getSourceRequirementName()));
			    cell.setCellStyle(wrappedStyle);

			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (sharedRequirement.getSourceRequirementBaselineName() ));
			    cell.setCellStyle(wrappedStyle);
			    
			    String sourceRequiremntURL = ProjectUtil.getURL(request,sourceRequirement.getRequirementId() ,"requirement");
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (sourceRequiremntURL));
			    cell.setCellStyle(wrappedStyle);
			    

			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (targetRequirement.getRequirementFullTag()));
			    cell.setCellStyle(wrappedStyle);
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (Integer.toString(sharedRequirement.getTargetRequirementVersion())));
			    cell.setCellStyle(wrappedStyle);

			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (sharedRequirement.getTargetRequirementName()));
			    cell.setCellStyle(wrappedStyle);

			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (sharedRequirement.getTargetRequirementBaselineName() ));
			    cell.setCellStyle(wrappedStyle);
			    
			    String targetRequiremntURL = ProjectUtil.getURL(request,targetRequirement.getRequirementId() ,"requirement");
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (targetRequiremntURL));
			    cell.setCellStyle(wrappedStyle);

				s = sharedAttributes.iterator();
				while (s.hasNext()){
					SharedRequirementTypeAttribute sA = (SharedRequirementTypeAttribute) s.next();
					if (sA.getSRACopyable() == 1){
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (sourceRequirement.getAttributeValue(sA.getRTAttribute().getAttributeId())));
					    cell.setCellStyle(wrappedStyle);
					}
				}
			    
	    	}

    		// if rootDataDirectory/TraceCloud does not exist, lets create it.
    		File traceCloudRoot = new File (rootDataDirectory + "/TraceCloud");
    		if (!(traceCloudRoot.exists() )){
    		    new File(rootDataDirectory + "/TraceCloud").mkdir();
    		}

    		// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
    		File tempFolderRoot  = new File (rootDataDirectory + "/TraceCloud/Temp");
    		if (!(tempFolderRoot.exists() )){
    			new File(rootDataDirectory + "/TraceCloud/Temp").mkdir();
    		}

        	// create a file Name
    		Calendar cal = Calendar.getInstance();
    		String today =  cal.getTime().toString().replace(":", "-");
    		
    		fileName = "Import of Shared Requirements by " + user.getFirstName() + " " + user.getLastName()  +" on " + today + ".xls";
    		fileName.replace(' ', '_');

    		fileName = rootDataDirectory + "/TraceCloud/Temp/" + fileName;
    		FileOutputStream fileOut = new FileOutputStream(fileName);
    		wb.write(fileOut);
    		fileOut.close();
        
        } catch (FileNotFoundException fnfe) {
            // It might not be possible to create the target file.
            fnfe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	
		return fileName;
	}
	// this routine does the following:
	// if source req type is not in target project, it creates it
	// if it already exists, it tries to make sure all the copyable attributes are in the target req type.
	public static RequirementType createOrUpdateRequirementType(SharedRequirementType sRT, Project targetProject,
		String actorEmailId, HttpSession session, String databaseType){

		RequirementType targetRequirementType = null;
		java.sql.Connection con = null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			RequirementType sourceRequirementType = sRT.getRequirementType();
			Project sourceProject = new Project(sourceRequirementType.getProjectId(),   databaseType);
			// lets build the name of the target Req Type.
			String targetRequirementTypeName = "Imported_" + sourceProject.getShortName() + "_" + sourceRequirementType.getRequirementTypeName();
			if (targetRequirementTypeName.length() > 99){
				targetRequirementTypeName = targetRequirementTypeName.substring(0, 98);
			}
			
			String targetRequirementTypeDescription = sourceRequirementType.getRequirementTypeDescription();
			String targetRequirementTypeShortName = sourceProject.getShortName() + "_" + targetProject.getShortName() + "_"+ sourceRequirementType.getRequirementTypeShortName();
			
			if (targetRequirementTypeShortName.length() > 30){
				targetRequirementTypeShortName = targetRequirementTypeShortName.substring(0, 28);
			}
			
			// lets get targetDisplaySequence. This is the smaller than the smallest targetDisplaySequence.
			
			String sql = "select min(display_sequence) \"display_sequence\"" +
				" from gr_requirement_types " +
				" where project_id = ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, targetProject.getProjectId());
			ResultSet rs = prepStmt.executeQuery();
			int targetRequirementTypeDisplaySequence = 0;
			while (rs.next()){
				targetRequirementTypeDisplaySequence = rs.getInt("display_sequence") - 1;
			}
			// lets see if this req type already exists in the target project.
			int status = ProjectUtil.isUniqueRequirementType(targetProject.getProjectId(), targetRequirementTypeShortName,
					targetRequirementTypeName);
			
			if (status != 0){
				// this means the RequirementTypeShortname does not exists
				String requirementTypeCanNotTraceTo = "";
				targetRequirementType = new RequirementType(targetProject.getProjectId(),targetProject.getProjectName(), 
					targetRequirementTypeShortName, targetRequirementTypeName, targetRequirementTypeDescription,
					targetRequirementTypeDisplaySequence, 1, 1, 1,
					1, requirementTypeCanNotTraceTo, actorEmailId,   databaseType);
				
				//since we just created a new req type, and have changed the project / folder structure
				// lets updated the project object in session.
				Project project = new Project(targetProject.getProjectId(),   databaseType);
				session.setAttribute("project", project);
			}
			else {
				// this req type exists.
				sql = "select id " +
					" from gr_requirement_types " +
					" where project_id = ? " +
					" and short_name = ? ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, targetProject.getProjectId());
				prepStmt.setString(2, targetRequirementTypeShortName);
				rs = prepStmt.executeQuery();
				int targetRequirementTypeId = 0;
				while (rs.next()){
					targetRequirementTypeId= rs.getInt("id") ;
				}
				targetRequirementType = new RequirementType(targetRequirementTypeId);
			}
	
			// at this point we have targetRequirementType
			// now lets go through all the custom attributes in the source req type 
			// and make sure they are in the target req type.
			// source version
			
			status = ProjectUtil.isUniqueAttribute(targetRequirementType.getRequirementTypeId(), "Source Version");
			
			int parentAttributeId  = 0 ;
			if (status != 0){
				// this means the attributeName does not exist in this req type.
				// we can create this custom attribute.
				
				RTAttribute rTAttribute = new RTAttribute(targetProject.getProjectId(),parentAttributeId, 1,
					targetRequirementType.getRequirementTypeId(), "Source Version" , 
					"Text Box" , "z" ,	0, "", 
					"", "Version of the Source Requirement when it was copied",
					1, 1, 1,
					actorEmailId, databaseType);
				
			}
			
			// source Baseline
			status = ProjectUtil.isUniqueAttribute(targetRequirementType.getRequirementTypeId(), "Source Baseline");
			if (status != 0){
				// this means the attributeName does not exist in this req type.
				// we can create this custom attribute.
				RTAttribute rTAttribute = new RTAttribute(targetProject.getProjectId(), parentAttributeId, 1,
					targetRequirementType.getRequirementTypeId(), "Source Baseline" , 
					"Text Box" , "z" ,	0, "", 
					"", "Version of the Source Requirement when it was copied",
					1, 1, 1,
					actorEmailId, databaseType);
			}

			
			// Imported By
			status = ProjectUtil.isUniqueAttribute(targetRequirementType.getRequirementTypeId(), "Imported By");
			if (status != 0){
				// this means the attributeName does not exist in this req type.
				// we can create this custom attribute.
				RTAttribute rTAttribute = new RTAttribute(targetProject.getProjectId(), parentAttributeId, 1,
					targetRequirementType.getRequirementTypeId(), "Imported By" , 
					"Text Box" , "z" ,	0, "", 
					"", "Version of the Source Requirement when it was copied",
					1, 1, 1,
					actorEmailId, databaseType);
			}

			// Imported Dt
			status = ProjectUtil.isUniqueAttribute(targetRequirementType.getRequirementTypeId(), "Imported Date");
			if (status != 0){
				// this means the attributeName does not exist in this req type.
				// we can create this custom attribute.
				RTAttribute rTAttribute = new RTAttribute(targetProject.getProjectId(), parentAttributeId, 1,
					targetRequirementType.getRequirementTypeId(), "Imported Date" , 
					"Text Box" , "z" ,	0, "", 
					"", "Version of the Source Requirement when it was copied",
					1, 1, 1,
					actorEmailId, databaseType);
			}

			// Last Refreshed By
			status = ProjectUtil.isUniqueAttribute(targetRequirementType.getRequirementTypeId(), "Last Refreshed By");
			if (status != 0){
				// this means the attributeName does not exist in this req type.
				// we can create this custom attribute.
				RTAttribute rTAttribute = new RTAttribute(targetProject.getProjectId(), parentAttributeId, 1,
					targetRequirementType.getRequirementTypeId(), "Last Refreshed By" , 
					"Text Box" , "z" ,	0, "", 
					"", "Version of the Source Requirement when it was copied",
					1, 1, 1,
					actorEmailId, databaseType);
			}
			
			// Last Modified Dt
			status = ProjectUtil.isUniqueAttribute(targetRequirementType.getRequirementTypeId(), "Last Refreshed Date");
			if (status != 0){
				// this means the attributeName does not exist in this req type.
				// we can create this custom attribute.
				RTAttribute rTAttribute = new RTAttribute(targetProject.getProjectId(),parentAttributeId, 1,
					targetRequirementType.getRequirementTypeId(), "Last Refreshed Date" , 
					"Text Box" , "z" ,	0, "", 
					"", "Version of the Source Requirement when it was copied",
					1, 1, 1,
					actorEmailId, databaseType);
			}

			// now lets iterate through all the 'Copyable' attributes in the Shared RT
			// and if they don't exist in the target project, lets create them.
			// Also, if this attribute has been set to 'Editable' , lets make the change
			// in the target Requirement Type.
			ArrayList sharedAttributes = sRT.getAllSharedAttributesInRequirementType();
			Iterator sA = sharedAttributes.iterator();
			while (sA.hasNext()){
				SharedRequirementTypeAttribute sharedAttribute = (SharedRequirementTypeAttribute) sA.next();
				if (sharedAttribute.getSRACopyable() != 1){
					// we want to deal only with the attributes marked as copyable
					continue;
				}
				RTAttribute sRTAttribute = sharedAttribute.getRTAttribute();
				status = ProjectUtil.isUniqueAttribute(targetRequirementType.getRequirementTypeId(),sRTAttribute.getAttributeName() );
				if (status != 0){
					// this means the attributeName does not exist in this req type.
					// we can create this custom attribute.
					// we don't want to make any of the copied shared attributes mandatory as they will never be updated by the local project
					RTAttribute rTAttribute = new RTAttribute(targetProject.getProjectId(),parentAttributeId, 1,
						targetRequirementType.getRequirementTypeId(), sRTAttribute.getAttributeName() , 
						sRTAttribute.getAttributeType() , sRTAttribute.getAttributeSortOrder() ,
						0, sRTAttribute.getAttributeDefaultValue(), 
						sRTAttribute.getAttributeDropDownOptions(), sRTAttribute.getAttributeDescription(),
						sRTAttribute.getAttributeImpactsVersion(), sRTAttribute.getAttributeImpactsTraceability(), sRTAttribute.getAttributeImpactsApprovalWorkflow(),
						actorEmailId, databaseType);
				}
				
				
				
			
				if (sharedAttribute.getSRAEDitable() == 1){
					// lets iterate through all the target req type attributes
					// and for any that are not set to editable, lets set them so.
					SharedRequirementUtil.setTargetRequirementAttributeEditable(targetRequirementType.getRequirementTypeId(), sharedAttribute.getRTAttribute().getAttributeName());
				
				}
			}
			
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		return (targetRequirementType);
	}
	
	
	
	// this does not really create the target req. by this time the target req is created
	// it just does some actions that we need to do after a req has just been created.
	public static void  createTargetRequirement(SharedRequirementType sRT, SharedRequirement sharedRequirement,
		RequirementType sourceRequriementType, RequirementType targetRequirementType, 
		Project sourceProject,Project targetProject,
		Requirement sourceRequirement,Requirement targetRequirement,
		User user, SecurityProfile securityProfile, String databaseType){

			try {
				// lets set the system attributes.
				// like Source Version, Baseline, Imported By, Imported Date, etc...
				// for this we will iterate through all the attributes of the target req type
				ArrayList attributes = targetRequirementType.getAllAttributesInRequirementType();
				Iterator a = attributes.iterator();
				while (a.hasNext()){
					RTAttribute rTAttribute = (RTAttribute) a.next();
					if (rTAttribute.getAttributeName().equals("Source Version")){
						targetRequirement.setCustomAttributeValue(rTAttribute.getAttributeId(), Integer.toString(sharedRequirement.getSourceRequirementVersion()), user,  databaseType);
					}
					
					if (rTAttribute.getAttributeName().equals("Source Baseline")){
						targetRequirement.setCustomAttributeValue(rTAttribute.getAttributeId(), sharedRequirement.getSourceRequirementBaselineName(), user,  databaseType);
					}
					
					if (rTAttribute.getAttributeName().equals("Imported By")){
						targetRequirement.setCustomAttributeValue(rTAttribute.getAttributeId(), user.getEmailId(), user,  databaseType);
					}
					
					if (rTAttribute.getAttributeName().equals("Imported Date")){
						// lets set the source version.
						targetRequirement.setCustomAttributeValue(rTAttribute.getAttributeId(), Calendar.getInstance().getTime().toString() , user,  databaseType);
					}

					if (rTAttribute.getAttributeName().equals("Last Refreshed By")){
						targetRequirement.setCustomAttributeValue(rTAttribute.getAttributeId(), user.getEmailId(), user,  databaseType);
					}
					
					if (rTAttribute.getAttributeName().equals("Last Refreshed Date")){
						// lets set the source version.
						targetRequirement.setCustomAttributeValue(rTAttribute.getAttributeId(), Calendar.getInstance().getTime().toString() , user,  databaseType);
					}
					
					// If this attrib is a 'copyable' attrib in the shared RT, then we need
					// to get the value from the shared req and pump it into the target project.
					ArrayList sharedAttributes = sRT.getAllSharedAttributesInRequirementType();
					Iterator sA = sharedAttributes.iterator();
					while (sA.hasNext()){
						SharedRequirementTypeAttribute sharedAttribute = (SharedRequirementTypeAttribute) sA.next();
						if (sharedAttribute.getSRACopyable() != 1){
							// we want to deal only with the attributes marked as copyable
							continue;
						}
						
						RTAttribute sRTAttribute = sharedAttribute.getRTAttribute();
						if (rTAttribute.getAttributeName().equals(sRTAttribute.getAttributeName())){
							// we now have a shared attribute , that is set to copyable 
							// lets update its value in the target project.
							targetRequirement.setCustomAttributeValue(
								rTAttribute.getAttributeId(), sourceRequirement.getAttributeValue(sRTAttribute.getAttributeName()),user,  databaseType);
						}
					}

				}

				
				// if the two projects aren't connected, lets make a connection between them.
				if (!(RequirementUtil.connectedProjects(targetProject, sourceProject.getShortName(),  databaseType))){
					ProjectUtil.relateProjects(targetProject ,sourceProject.getProjectId(),"Projects connected to facilitate Shared Requirements Import by " +
						user.getEmailId() + " on " + Calendar.getInstance().getTime().toString() ,user.getEmailId(),  databaseType);
				}
				// now lets create the trace
				String status = RequirementUtil.createTraces(targetProject, targetRequirement.getRequirementId(), 
					sourceProject.getShortName()+":"+sourceRequirement.getRequirementFullTag(), "",
					targetProject.getProjectId(), securityProfile,  databaseType);
				
				// lets copy all the discussion from the source requirement to the target requirement
				// if export discussion is set to yes.
				if (sRT.getSRShareComments() == 1){
					SharedRequirementUtil.copySharedRequirementDiscussions(sourceRequirement.getRequirementId(), 
						targetRequirement.getRequirementId(), user.getEmailId(),  databaseType);
				}
			} catch (Exception e) {
				
				e.printStackTrace();
			}  finally {
			}
		}

	// this is very similar to createTargetRequirement, but does things sligthly differently
	public static void  updateTargetRequirement(SharedRequirementType sRT, SharedRequirement sharedRequirement,
		RequirementType sourceRequriementType, RequirementType targetRequirementType, 
		Project sourceProject,Project targetProject,
		Requirement sourceRequirement,Requirement targetRequirement,
		User user, SecurityProfile securityProfile, String databaseType){

			try {
				// lets set the system attributes.
				// like Source Version, Baseline, Imported By, Imported Date, etc...
				// for this we will iterate through all the attributes of the target req type
				ArrayList attributes = targetRequirementType.getAllAttributesInRequirementType();
				Iterator a = attributes.iterator();
				while (a.hasNext()){
					RTAttribute rTAttribute = (RTAttribute) a.next();
					if (rTAttribute.getAttributeName().equals("Source Version")){
						targetRequirement.setCustomAttributeValue(rTAttribute.getAttributeId(), Integer.toString(sharedRequirement.getSourceRequirementVersion()), user,  databaseType);
					}
					
					if (rTAttribute.getAttributeName().equals("Source Baseline")){
						targetRequirement.setCustomAttributeValue(rTAttribute.getAttributeId(), sharedRequirement.getSourceRequirementBaselineName(), user,  databaseType);
					}
					
					if (rTAttribute.getAttributeName().equals("Last Refreshed By")){
						targetRequirement.setCustomAttributeValue(rTAttribute.getAttributeId(), user.getEmailId(), user,  databaseType);
					}
					
					if (rTAttribute.getAttributeName().equals("Last Refreshed Date")){
						// lets set the source version.
						targetRequirement.setCustomAttributeValue(rTAttribute.getAttributeId(), Calendar.getInstance().getTime().toString() , user,  databaseType);
					}

					// If this attrib is a 'copyable' attrib in the shared RT, then we need
					// to get the value from the shared req and pump it into the target project.
					ArrayList sharedAttributes = sRT.getAllSharedAttributesInRequirementType();
					Iterator sA = sharedAttributes.iterator();
					while (sA.hasNext()){
						SharedRequirementTypeAttribute sharedAttribute = (SharedRequirementTypeAttribute) sA.next();
						if (sharedAttribute.getSRACopyable() != 1){
							// we want to deal only with the attributes marked as copyable
							continue;
						}
						
						RTAttribute sRTAttribute = sharedAttribute.getRTAttribute();
						if (rTAttribute.getAttributeName().equals(sRTAttribute.getAttributeName())){
							// we now have a shared attribute , that is set to copyable 
							// lets update its value in the target project.
							targetRequirement.setCustomAttributeValue(
								rTAttribute.getAttributeId(), sourceRequirement.getAttributeValue(sRTAttribute.getAttributeName()),user,  databaseType);
						}
					}
					
				}
				
				
				
				// if the two projects aren't connected, lets make a connection between them.
				if (!(RequirementUtil.connectedProjects(targetProject, sourceProject.getShortName(),  databaseType))){
					ProjectUtil.relateProjects(targetProject ,sourceProject.getProjectId(),"Projects connected to facilitate Shared Requirements Import by " +
						user.getEmailId() + " on " + Calendar.getInstance().getTime().toString() ,user.getEmailId(),  databaseType);
				}
				// now lets create the trace
				String status = RequirementUtil.createTraces(targetProject, targetRequirement.getRequirementId(), 
					sourceProject.getShortName()+":"+sourceRequirement.getRequirementFullTag(), "",
					targetProject.getProjectId(), securityProfile,  databaseType);
				
				status = RequirementUtil.clearSuspect(targetRequirement.getRequirementId(), sourceRequirement.getRequirementId(),
					user.getEmailId(), securityProfile,  databaseType);
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}  finally {
			}
		}


	public static void setTargetRequirementAttributeEditable(int targetRequirementTypeId, String attributeName){
		
		java.sql.Connection con = null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "update gr_rt_attributes" +
				" set system_attribute = 0" +
				" where requirement_type_id = ? " +
				" and name = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,targetRequirementTypeId);
			prepStmt.setString(2, attributeName);
			
			prepStmt.execute();
			prepStmt.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
	}

	public static void copySharedRequirementDiscussions(int sourceRequirementId , int targetRequirementId, String actorEmailId, String databaseType){
		
		java.sql.Connection con = null;
		try {
		
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_requirement_comments (" +
					" requirement_id, version, commenter_email_id, comment_note, comment_dt)" +
					" select ?, version, ?, concat (commenter_email_id, ' ---> ', comment_note ), comment_dt" +
					" from gr_requirement_comments " +
					" where requirement_id = ?  ";
			}
			else {
				sql = " insert into gr_requirement_comments (" +
				" requirement_id, version, commenter_email_id, comment_note, comment_dt)" +
				" select ?, version, ?, commenter_email_id || ' ---> ' || comment_note , comment_dt" +
				" from gr_requirement_comments " +
				" where requirement_id = ?  ";
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,targetRequirementId);
			prepStmt.setString(2, actorEmailId);
			prepStmt.setInt(3, sourceRequirementId);
			
			prepStmt.execute();
			prepStmt.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
	}
	
}
