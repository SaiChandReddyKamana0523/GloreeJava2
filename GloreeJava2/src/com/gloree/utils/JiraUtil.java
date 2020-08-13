package com.gloree.utils;

//GloreeJava2


import java.util.ArrayList;
import java.util.Iterator;

import com.gloree.beans.*;


import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class JiraUtil {
	
	//
	// This class is used to run static queries to get 
	// 1. list of subfolders in a project
	// 2. list of requirements in a project. 
	// 3. Delete a folder in the db.
	//
	public static void pushJiraToTraceCloud( Requirement requirement, User user, 
			String JID, String JPROJECT, String  JTYPE, String  JPRIORITY, String  JLABELS, String  JSTATUS, String 
			JRESOLUTION, String  JAFFECTSV, String  JFIXV, String  JASSIGNEE, String  JREPORTER, String  
			JCREATED, String  JUPDATED, String  JURL, String  JTITLE, String  JDESCRIPTION,
			SecurityProfile securityProfile,
			HttpServletRequest request, HttpSession session,  String databaseType){
		java.sql.Connection con = null;
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			Project project = new Project(requirement.getProjectId(), databaseType);
			
			// get the Jira Req Type in this project. Create one if it doesn't exist.
			RequirementType jRT = getJiraRequirementTypeInProject(project.getProjectId(), project.getProjectName(), securityProfile,session, databaseType);
			ensureAllJiraAttributesExistInRequirementType(requirement, jRT, 
					 JID,   JTYPE,   JPRIORITY,   JLABELS,   JSTATUS,  
					JRESOLUTION,   JAFFECTSV,   JFIXV,   JASSIGNEE,   JREPORTER,   
					JCREATED,   JUPDATED,   JURL,   JTITLE,   JDESCRIPTION,
					user, databaseType);
			
			Folder jiraFolder = getJiraFolder(  requirement,  jRT,  project.getProjectId(),
					 JPROJECT,  user ,  securityProfile,  session, databaseType);
			

			
			
			// See if the Jira Proxy exists in this project
			// if not, create one
			// update the jira tr mapping entry
			String sql = " select requirement_id 'jiraProxyId' " +
					" from  jira_tc_mapping " +
					" where jira_id = ? " +
					" and jira_url = ? " +
					" and project_id = ?  ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, JID);
			prepStmt.setString(2, JURL);
			
			prepStmt.setInt(3, requirement.getProjectId());
			ResultSet rs = prepStmt.executeQuery();
			int jiraProxyId = 0 ;
			while (rs.next()){
				jiraProxyId = rs.getInt("jiraProxyId");
			}
			
			prepStmt.close();
			rs.close();
			

			Requirement jiraProxy = null;
			if (jiraProxyId == 0 ){
				// This Jira does not exist as a Jira Proxy in the target project. So, we will need to create a new one and update the mapping entry.

				try {
				System.out.println("srt req type id is " + jRT.getRequirementTypeId());
				System.out.println("srt folder id is " + jiraFolder.getFolderId());
				System.out.println("srt project  id is " + requirement.getProjectId());
				}
				catch (Exception e){
					e.printStackTrace();
				}
				
				jiraProxy = new Requirement("", jRT.getRequirementTypeId(), jiraFolder.getFolderId() , requirement.getProjectId(), 
						 JTITLE , JDESCRIPTION, 
						"Medium", user.getEmailId(), "", 0,
						"", user.getEmailId() , databaseType);
				
				// now that we created a new JiraProxy , lets map the relationship between the CTCID in CQ and TRProxy in TraceCloud
				sql = "insert into jira_tc_mapping(jira_id, jira_url, project_id, requirement_id, full_tag)" +
					" values(?,?,?,?,?) ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, JID);
				prepStmt.setString(2, JURL);
				prepStmt.setInt(3, jiraProxy.getProjectId());
				prepStmt.setInt(4, jiraProxy.getRequirementId());
				prepStmt.setString(5, jiraProxy.getRequirementFullTag());
				prepStmt.execute();
				prepStmt.close();
			}
			else {
				jiraProxy  = new Requirement (jiraProxyId, databaseType); 
				if (jiraProxy.getRequirementId() == 0){
					// this means that there is a mapping in our cq_tc_mapping table between a Jira Object in Jira and a JiraProxy object in TC
					// however we weren't able to create that req.  An indication that some one has deleted the Jira Object in TraceCloud
					// so we re-create the requirement and then add a new mapping.
					jiraProxy = new Requirement("", jRT.getRequirementTypeId(), jiraFolder.getFolderId() , requirement.getProjectId(), 
							 JTITLE, JDESCRIPTION, 
							"Medium", user.getEmailId(), "", 0,
							"", user.getEmailId() , databaseType);
					
					// now that we created a new TRProxy , lets map the relationship between the CTCID in CQ and TRProxy in TraceCloud
					sql = "insert into cq_tc_mapping(cq_id, cq_type, project_id, requirement_id, full_tag)" +
						" values(?,?,?,?,?) ";
					prepStmt =  con.prepareStatement(sql);
					prepStmt.setString(1, JID);
					prepStmt.setString(2, JURL);
					prepStmt.setInt(3, jiraProxy.getProjectId());
					prepStmt.setInt(4, jiraProxy.getRequirementId());
					prepStmt.setString(5, jiraProxy.getRequirementFullTag());
					prepStmt.execute();
					prepStmt.close();
					
				}
			}
			
			// At this point, we have a Jira Proxy requirement.
			updateJiraProxy(jiraProxy, jRT, jiraFolder, user, JID, JPROJECT, JTYPE, JPRIORITY, JLABELS, JSTATUS,
					JRESOLUTION, JAFFECTSV, JFIXV, JASSIGNEE, JREPORTER, JCREATED, JUPDATED, JURL, JTITLE, JDESCRIPTION,
					securityProfile, request, session, databaseType);
			
			// At this point the attributes of Jira are in sync
			
			
			
			
			// Create a trace from Jira Proxy to the Target Requirement
			String status2 = RequirementUtil.createTraces(project, requirement.getRequirementId(), 
					"", jiraProxy.getRequirementFullTag() , project.getProjectId(), securityProfile,  databaseType);
			
			// Clear a suspect if one exists.
			RequirementUtil.clearSuspect(jiraProxy.getRequirementId(), requirement.getRequirementId(), user.getEmailId(), securityProfile, databaseType);
			
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
	}	
	
	public static RequirementType getJiraRequirementTypeInProject(int projectId, String projectName, SecurityProfile securityProfile,HttpSession session, String databaseType){
		// lets get the Jira Req Type
		RequirementType jRT = null;
		try {
			
			
			
					
			ArrayList requirementTypes = ProjectUtil.getRequirementTypesInAProject(projectId);
			Iterator rT = requirementTypes.iterator();
			while (rT.hasNext()){
				RequirementType tempRT = (RequirementType) rT.next();
				if (tempRT.getRequirementTypeShortName().equals("JRA")){
					jRT = tempRT;
				}
			}
	
			
			if (jRT == null){
				// since Jira Proxy Req Type does not exist, lets create one.
				jRT = new RequirementType(projectId , projectName,
					"JRA", "Jira Proxy", "Jira Proxy",
					9, 0, 0, 1, 
					1, "", securityProfile.getUser().getEmailId(),  databaseType);
				
				// since we just created a new Req Type, and since this user is the admin and has
				// read / write privs on this folder / req type, we need to refresh his / her security profile
				// so that they can start working on this reqtype / folder.
				securityProfile = new SecurityProfile(securityProfile.getUser().getUserId(),databaseType);
				session.setAttribute("securityProfile", securityProfile);			
	
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		return (jRT);
	}
	

		
	
	public static void ensureAllJiraAttributesExistInRequirementType(Requirement requirement, RequirementType jRT,
			String JID, String  JTYPE, String  JPRIORITY, String  JLABELS, String  JSTATUS, String 
			JRESOLUTION, String  JAFFECTSV, String  JFIXV, String  JASSIGNEE, String  JREPORTER, String  
			JCREATED, String  JUPDATED, String  JURL, String  JTITLE, String  JDESCRIPTION,
			User user ,String databaseType){
		// lets make sure 'JID' attribute exists in the Jira Req Type. If doesn't then create it.
		int status = 0;
		if (!(JID.equals("#EMPTY#"))){
			status = ProjectUtil.isUniqueAttribute(jRT.getRequirementTypeId(), "Jira Id");
			if (status == 1){
				// means that the attribute JID does not exist in the Jira Proxy req type . So lets create it.
				RTAttribute jIDAttribute = new RTAttribute(requirement.getProjectId(),0, 0, jRT.getRequirementTypeId(), "Jira Id" , 
						"Text Box" , "zza",	0, "", 
						"", "Uniquely identifies a Jira object",
						0, 0, 0,
						user.getEmailId(), databaseType);
			}
		}
		// lets make sure 'JURL' attribute exists in the Jira Req Type. If doesn't then create it.
		if (!(JURL.equals("#EMPTY#"))){
			status = ProjectUtil.isUniqueAttribute(jRT.getRequirementTypeId(), "Jira URL");
			if (status == 1){
				// means that the attribute Jira URL does not exist in the TR req type . So lets create it.
				RTAttribute jIDAttribute = new RTAttribute(requirement.getProjectId(),0, 0, jRT.getRequirementTypeId(), "Jira URL" , 
						"URL" , "zzb",	0, "", 
						"", "URL to the Jira Object",
						0, 0, 0,
						user.getEmailId(), databaseType);
			}
		}
		// lets make sure 'JTYPE' attribute exists in the Jira Req Type. If doesn't then create it.
		if (!(JTYPE.equals("#EMPTY#"))){
			status = ProjectUtil.isUniqueAttribute(jRT.getRequirementTypeId(), "Jira Type");
			if (status == 1){
				// means that the attribute Jira URL does not exist in the TR req type . So lets create it.
				RTAttribute jIDAttribute = new RTAttribute(requirement.getProjectId(),0, 0, jRT.getRequirementTypeId(), "Jira Type" , 
						"Text Box" , "zzc",	0, "", 
						"", "Type of  Jira Object",
						0, 0, 0,
						user.getEmailId(), databaseType);
			}
		}
		// lets make sure 'JPRIORITY' attribute exists in the Jira Req Type. If doesn't then create it.
		if (!(JPRIORITY.equals("#EMPTY#"))){
			status = ProjectUtil.isUniqueAttribute(jRT.getRequirementTypeId(), "Jira Priority");
			if (status == 1){
				// means that the attribute Jira URL does not exist in the TR req type . So lets create it.
				RTAttribute jIDAttribute = new RTAttribute(requirement.getProjectId(),0, 0, jRT.getRequirementTypeId(), "Jira Priority" , 
						"Text Box" , "zzd",	0, "", 
						"", "Priority of the Jira Object",
						0, 0, 0,
						user.getEmailId(), databaseType);
			}
		}
		
		// lets make sure 'JLABELS' attribute exists in the Jira Req Type. If doesn't then create it.
		if (!(JLABELS.equals("#EMPTY#"))){
			status = ProjectUtil.isUniqueAttribute(jRT.getRequirementTypeId(), "Jira Labels");
			if (status == 1){
				// means that the attribute Jira URL does not exist in the TR req type . So lets create it.
				RTAttribute jIDAttribute = new RTAttribute(requirement.getProjectId(),0, 0, jRT.getRequirementTypeId(), "Jira Labels" , 
						"Text Box" , "zze",	0, "", 
						"", "Labels of the Jira Object",
						0, 0, 0,
						user.getEmailId(), databaseType);
			}
		}
		
		// lets make sure 'JSTATUS' attribute exists in the Jira Req Type. If doesn't then create it.
		if (!(JSTATUS.equals("#EMPTY#"))){
			status = ProjectUtil.isUniqueAttribute(jRT.getRequirementTypeId(), "Jira Status");
			if (status == 1){
				// means that the attribute Jira URL does not exist in the TR req type . So lets create it.
				RTAttribute jIDAttribute = new RTAttribute(requirement.getProjectId(),0, 0, jRT.getRequirementTypeId(), "Jira Status" , 
						"Text Box" , "zzf",	0, "", 
						"", "Status of the Jira Object",
						0, 0, 0,
						user.getEmailId(), databaseType);
			}
		}
		// lets make sure 'JRESOLUTION' attribute exists in the Jira Req Type. If doesn't then create it.
		if (!(JRESOLUTION.equals("#EMPTY#"))){
			status = ProjectUtil.isUniqueAttribute(jRT.getRequirementTypeId(), "Jira Resolution");
			if (status == 1){
				// means that the attribute Jira URL does not exist in the TR req type . So lets create it.
				RTAttribute jIDAttribute = new RTAttribute(requirement.getProjectId(),0, 0, jRT.getRequirementTypeId(), "Jira Resolution" , 
						"Text Box" , "zzg",	0, "", 
						"", "Resolution of the Jira Object",
						0, 0, 0,
						user.getEmailId(), databaseType);
			}
		}
		// lets make sure 'JAFFECTSV' attribute exists in the Jira Req Type. If doesn't then create it.
		if (!(JAFFECTSV.equals("#EMPTY#"))){
			status = ProjectUtil.isUniqueAttribute(jRT.getRequirementTypeId(), "Jira Affects Versions");
			if (status == 1){
				// means that the attribute Jira URL does not exist in the TR req type . So lets create it.
				RTAttribute jIDAttribute = new RTAttribute(requirement.getProjectId(),0, 0, jRT.getRequirementTypeId(), "Jira Affects Versions" , 
						"Text Box" , "zzh",	0, "", 
						"", "Version affected by this Jira Object",
						0, 0, 0,
						user.getEmailId(), databaseType);
			}			
		}
		// lets make sure 'JFIXV' attribute exists in the Jira Req Type. If doesn't then create it.
		if (!(JFIXV.equals("#EMPTY#"))){
			status = ProjectUtil.isUniqueAttribute(jRT.getRequirementTypeId(), "Jira Fix Version");
			if (status == 1){
				// means that the attribute Jira URL does not exist in the TR req type . So lets create it.
				RTAttribute jIDAttribute = new RTAttribute(requirement.getProjectId(),0, 0, jRT.getRequirementTypeId(), "Jira Fix Version" , 
						"Text Box" , "zzi",	0, "", 
						"", "Version where this Jira Issue if fixed",
						0, 0, 0,
						user.getEmailId(), databaseType);
			}			
		}
		if (!(JASSIGNEE.equals("#EMPTY#"))){
			// lets make sure 'JASSIGNEE' attribute exists in the Jira Req Type. If doesn't then create it.
			status = ProjectUtil.isUniqueAttribute(jRT.getRequirementTypeId(), "Jira Assigned To");
			if (status == 1){
				// means that the attribute Jira URL does not exist in the TR req type . So lets create it.
				RTAttribute jIDAttribute = new RTAttribute(requirement.getProjectId(),0, 0, jRT.getRequirementTypeId(), "Jira Assigned To" , 
						"Text Box" , "zzj",	0, "", 
						"", "User to whom the Jira Issue is Assigned to ",
						0, 0, 0,
						user.getEmailId(), databaseType);
			}
		}
		// lets make sure 'JREPORTER' attribute exists in the Jira Req Type. If doesn't then create it.
		if (!(JREPORTER.equals("#EMPTY#"))){
			status = ProjectUtil.isUniqueAttribute(jRT.getRequirementTypeId(), "Jira Reported By");
			if (status == 1){
				// means that the attribute Jira URL does not exist in the TR req type . So lets create it.
				RTAttribute jIDAttribute = new RTAttribute(requirement.getProjectId(),0, 0, jRT.getRequirementTypeId(), "Jira Reported By" , 
						"Text Box" , "zzk",	0, "", 
						"", "User who reported the Jira issue",
						0, 0, 0,
						user.getEmailId(), databaseType);
			}			
		}
		// lets make sure 'JCREATED' attribute exists in the Jira Req Type. If doesn't then create it.
		if (!(JCREATED.equals("#EMPTY#"))){
			status = ProjectUtil.isUniqueAttribute(jRT.getRequirementTypeId(), "Jira Created On");
			if (status == 1){
				// means that the attribute Jira URL does not exist in the TR req type . So lets create it.
				RTAttribute jIDAttribute = new RTAttribute(requirement.getProjectId(),0, 0, jRT.getRequirementTypeId(), "Jira Created On" , 
						"Text Box" , "zzl",	0, "", 
						"", "Date when Jira Issue was reported",
						0, 0, 0,
						user.getEmailId(), databaseType);
			}
		}
		// lets make sure 'JFIXV' attribute exists in the Jira Req Type. If doesn't then create it.
		if (!(JUPDATED.equals("#EMPTY#"))){
			status = ProjectUtil.isUniqueAttribute(jRT.getRequirementTypeId(), "Jira Updated On");
			if (status == 1){
				// means that the attribute Jira URL does not exist in the TR req type . So lets create it.
				RTAttribute jIDAttribute = new RTAttribute(requirement.getProjectId(),0, 0, jRT.getRequirementTypeId(), "Jira Updated On" , 
						"Text Box" , "zzm",	0, "", 
						"", "Date when Jira was last updated",
						0, 0, 0,
						user.getEmailId(), databaseType);
			}
		}
	}
	
	
	
	public static Folder getJiraFolder(Requirement requirement, RequirementType jRT, int projectId,
		String JPROJECT, User user , SecurityProfile securityProfile,
		 HttpSession session, String databaseType){

		Folder jiraFolder = null;
		// Lets ensure there is a folder called JPROJECT in the Jira Proxy req type. if it doesn't create one.
		if ((JPROJECT != null) && (!(JPROJECT.equals("")))){
			JPROJECT = JPROJECT.trim();
			Folder jiraRootFolder = new Folder (jRT.getRootFolderId());
			String jiraFolderPath = jiraRootFolder.getFolderPath() + "/" + JPROJECT;
			boolean jiraFolderExists = FolderUtil.doesThisFolderExistInProject(projectId, jiraFolderPath) ;
			
			if (jiraFolderExists){
				// This folder exists in the project. lets get it from db and create a folder object.
				jiraFolder = new Folder(jiraFolderPath, projectId);
			}
			else {
				// lets create a jira Folder
				// for all sub folders (ie non root level folders), we default the 
				// folderOrder to 0. Since we use the sorting by folder_order, folder_name,
				// we should be Ok
				jiraFolder = new Folder( jRT.getRootFolderId(), projectId, JPROJECT, 
						JPROJECT, 0, user.getEmailId(), databaseType);
				
				
				// Same with the security privs. we need to reset them in the session, so that this user
				// can work on these newly created folders. 
				
				securityProfile = new SecurityProfile(user.getUserId(),databaseType);
				session.setAttribute("securityProfile", securityProfile);
				
			}
		}
		else {
			// we make the root folder of Jira Proxy req type be the jira folder
			jiraFolder = new Folder(jRT.getRootFolderId());
		}
		return (jiraFolder);		

	}	
	public static ArrayList getProxies(String JID , String JURL, User user, SecurityProfile securityProfile, String databaseType){
		ArrayList proxies = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select requirement_id " +
					" from jira_tc_mapping " +
					" where jira_id = ? " +
					" and jira_url  = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, JID);
			prepStmt.setString(2, JURL);
			

			
			rs = prepStmt.executeQuery();
			
			String sql2 = "delete from jira_tc_mapping where requirement_id =  ? ";
			PreparedStatement prepStmt2 = con.prepareStatement(sql2);
			
			while (rs.next()){
				int requirementId = rs.getInt("requirement_id");
				Requirement requirement = new Requirement(requirementId, databaseType);
				
				if (requirement == null || requirement.getRequirementFullTag() == null || requirement.getRequirementFullTag().equals("")){
					// lets delete this from the jira_tc_mapping, as this req no longer exists in TC
					prepStmt2.setInt(1, requirementId);
					prepStmt2.execute();
				}
				
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" + requirement.getFolderId()))) {
					requirement.redact();
				}
				
				
				proxies.add(requirement);
			}
			prepStmt2.close();
			prepStmt.close();
			rs.close();
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
		return (proxies);
	}	
	
	public static Requirement getProxyInProject(String JID , String JURL, int projectId, User user, SecurityProfile securityProfile, String databaseType){
		Requirement jiraProxy = null;
				
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select requirement_id " +
					" from jira_tc_mapping " +
					" where jira_id = ? " +
					" and jira_url  = ? " +
					" and project_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, JID);
			prepStmt.setString(2, JURL);
			prepStmt.setInt(3, projectId);
			

			
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int requirementId = rs.getInt("requirement_id");
				jiraProxy = new Requirement(requirementId, databaseType);
				
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" + jiraProxy.getFolderId()))) {
					jiraProxy.redact();
				}
				
				
				
			}
			
			prepStmt.close();
			rs.close();
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
		return (jiraProxy);
	}	

	
	public static void updateJiraProxy(Requirement jiraProxy, RequirementType jRT, Folder jiraFolder, User user, 
			String JID, String JPROJECT, String  JTYPE, String  JPRIORITY, String  JLABELS, String  JSTATUS, String 
			JRESOLUTION, String  JAFFECTSV, String  JFIXV, String  JASSIGNEE, String  JREPORTER, String  
			JCREATED, String  JUPDATED, String  JURL, String  JTITLE, String  JDESCRIPTION,
			SecurityProfile securityProfile,
			HttpServletRequest request, HttpSession session,  String databaseType){
		try {

			
			// if this proxy is in a different folder than JPROJECt, then lets move it to the new folder location.
			if (jiraProxy.getFolderId() != jiraFolder.getFolderId()){
				// lets move jiraProxy to the correct folder. 
				RequirementUtil.moveRequirementToAnotherFolder(jiraProxy, jiraFolder.getFolderId(), user.getEmailId(), databaseType); 
			}
			
			// If the Jira Proxy name  / description  is different,  update it.
			if (
					(!(jiraProxy.getRequirementName().trim().equals(JTITLE.trim())))
					||
					(!(jiraProxy.getRequirementDescription().trim().equals(JDESCRIPTION.trim())))
				){
				// the name may have changed. lets update it in TraceCloud
				jiraProxy = new Requirement( jiraProxy.getRequirementId(),
						JTITLE, 
						JDESCRIPTION,
						jiraProxy.getRequirementPriority(), 
						jiraProxy.getRequirementOwner(), 
						jiraProxy.getRequirementPctComplete(), 
						jiraProxy.getRequirementExternalUrl(), user.getEmailId() , request, databaseType);
				
			}
			// At this point Jira Proxy exists and is in sync with Clear Quest.
			
			// lets check the attributes and update them.
			RTAttribute RTAttributeJID = null;
			RTAttribute RTAttributeJURL = null;
			RTAttribute RTAttributeJTYPE = null;
			RTAttribute RTAttributeJPRIORITY = null;
			RTAttribute RTAttributeJLABELS = null;
			RTAttribute RTAttributeJSTATUS = null;
			RTAttribute RTAttributeJRESOLUTION = null;
			RTAttribute RTAttributeJAFFECTSV = null;
			RTAttribute RTAttributeJFIXV = null;
			RTAttribute RTAttributeJASSIGNEE = null;
			RTAttribute RTAttributeJREPORTER = null;
			RTAttribute RTAttributeJCREATED = null;
			RTAttribute RTAttributeJUPDATED = null;
			
			ArrayList rTAttributes = jRT.getAllAttributesInRequirementType();
			Iterator rTA = rTAttributes.iterator();
			
			while (rTA.hasNext()){
				RTAttribute tempRTAttribute = (RTAttribute) rTA.next();
				if (tempRTAttribute.getAttributeName().equals("Jira Id")){
					RTAttributeJID = tempRTAttribute;
				}
				if (tempRTAttribute.getAttributeName().equals("Jira URL")){
					RTAttributeJURL = tempRTAttribute;
				}
				if (tempRTAttribute.getAttributeName().equals("Jira Type")){
					RTAttributeJTYPE = tempRTAttribute;
				}
				if (tempRTAttribute.getAttributeName().equals("Jira Priority")){
					RTAttributeJPRIORITY = tempRTAttribute;
				}
				if (tempRTAttribute.getAttributeName().equals("Jira Labels")){
					RTAttributeJLABELS = tempRTAttribute;
				}
				if (tempRTAttribute.getAttributeName().equals("Jira Status")){
					RTAttributeJSTATUS = tempRTAttribute;
				}
				if (tempRTAttribute.getAttributeName().equals("Jira Resolution")){
					RTAttributeJRESOLUTION = tempRTAttribute;
				}
				if (tempRTAttribute.getAttributeName().equals("Jira Affects Versions")){
					RTAttributeJAFFECTSV = tempRTAttribute;
				}
				if (tempRTAttribute.getAttributeName().equals("Jira Fix Version")){
					RTAttributeJFIXV = tempRTAttribute;
				}
				if (tempRTAttribute.getAttributeName().equals("Jira Assigned To")){
					RTAttributeJASSIGNEE = tempRTAttribute;
				}
				if (tempRTAttribute.getAttributeName().equals("Jira Reported By")){
					RTAttributeJREPORTER = tempRTAttribute;
				}
				if (tempRTAttribute.getAttributeName().equals("Jira Created On")){
					RTAttributeJCREATED = tempRTAttribute;
				}
				if (tempRTAttribute.getAttributeName().equals("Jira Updated On")){
					RTAttributeJUPDATED = tempRTAttribute;
				}
				
			}
			
			// If JID in Jira Proxy is different, update it
			if (!(JID.equals("#EMPTY#"))){
				String existingJID = jiraProxy.getAttributeValue("Jira Id");
				if (!(existingJID.equals(JID))){
					jiraProxy.setCustomAttributeValue(RTAttributeJID.getAttributeId(), JID, user, databaseType);
				}
			}
			// If JURL in Jira Proxy is different, update it
			if (!(JURL.equals("#EMPTY#"))){
				String existingJURL = jiraProxy.getAttributeValue("Jira URL");
				if (!(existingJURL.equals(JURL))){
					jiraProxy.setCustomAttributeValue(RTAttributeJURL.getAttributeId(), JURL, user, databaseType);
				}
			}
			// If JURL in Jira Proxy is different, update it
			if (!(JTYPE.equals("#EMPTY#"))){
				String existingJTYPE = jiraProxy.getAttributeValue("Jira Type");
				if (!(existingJTYPE.equals(JTYPE))){
					jiraProxy.setCustomAttributeValue(RTAttributeJTYPE.getAttributeId(), JTYPE, user, databaseType);
				}
			}
			// If JPRIORITY in Jira Proxy is different, update it
			if (!(JPRIORITY.equals("#EMPTY#"))){
				String existingJPRIORITY = jiraProxy.getAttributeValue("Jira Priority");
				if (!(existingJPRIORITY.equals(JPRIORITY))){
					jiraProxy.setCustomAttributeValue(RTAttributeJPRIORITY.getAttributeId(), JPRIORITY, user, databaseType);
				}
			}
			// If JLABELS in Jira Proxy is different, update it
			if (!(JLABELS.equals("#EMPTY#"))){
				String existingJLABELS = jiraProxy.getAttributeValue("Jira Labels");
				if (!(existingJLABELS.equals(JLABELS))){
					jiraProxy.setCustomAttributeValue(RTAttributeJLABELS.getAttributeId(), JLABELS, user, databaseType);
				}
			}
			// If JSTATUS in Jira Proxy is different, update it
			if (!(JSTATUS.equals("#EMPTY#"))){
				String existingJSTATUS = jiraProxy.getAttributeValue("Jira Status");
				if (!(existingJSTATUS.equals(JSTATUS))){
					jiraProxy.setCustomAttributeValue(RTAttributeJSTATUS.getAttributeId(), JSTATUS, user, databaseType);
				}
			}
			// If JRESOLUTION in Jira Proxy is different, update it
			if (!(JRESOLUTION.equals("#EMPTY#"))){
				String existingJRESOLUTION = jiraProxy.getAttributeValue("Jira Resolution");
				if (!(existingJRESOLUTION.equals(JRESOLUTION))){
					jiraProxy.setCustomAttributeValue(RTAttributeJRESOLUTION.getAttributeId(), JRESOLUTION, user, databaseType);
				}
			}
			// If JAFFECTSV in Jira Proxy is different, update it
			if (!(JAFFECTSV.equals("#EMPTY#"))){
				String existingJAFFECTSV = jiraProxy.getAttributeValue("Jira Affects Versions");
				if (!(existingJAFFECTSV.equals(JAFFECTSV))){
					jiraProxy.setCustomAttributeValue(RTAttributeJAFFECTSV.getAttributeId(), JAFFECTSV, user, databaseType);
				}
			}
			// If JFIXV in Jira Proxy is different, update it
			if (!(JFIXV.equals("#EMPTY#"))){
				String existingJFIXV = jiraProxy.getAttributeValue("Jira Fix Version");
				if (!(existingJFIXV.equals(JFIXV))){
					jiraProxy.setCustomAttributeValue(RTAttributeJFIXV.getAttributeId(), JFIXV, user, databaseType);
				}
			}
			// If JASSIGNEE in Jira Proxy is different, update it
			if (!(JASSIGNEE.equals("#EMPTY#"))){
				String existingJASSIGNEE = jiraProxy.getAttributeValue("Jira Assigned To");
				if (!(existingJASSIGNEE.equals(JFIXV))){
					jiraProxy.setCustomAttributeValue(RTAttributeJASSIGNEE.getAttributeId(), JASSIGNEE, user, databaseType);
				}
			}
			// If JREPORTEE in Jira Proxy is different, update it
			if (!(JREPORTER.equals("#EMPTY#"))){
				String existingJREPORTER = jiraProxy.getAttributeValue("Jira Reported By");
				if (!(existingJREPORTER.equals(JREPORTER))){
					jiraProxy.setCustomAttributeValue(RTAttributeJREPORTER.getAttributeId(), JREPORTER, user, databaseType);
				}
			}
			// If JCREATED in Jira Proxy is different, update it
			if (!(JCREATED.equals("#EMPTY#"))){
				String existingJCREATED = jiraProxy.getAttributeValue("Jira Created On");
				if (!(existingJCREATED.equals(JCREATED))){
					jiraProxy.setCustomAttributeValue(RTAttributeJCREATED.getAttributeId(), JCREATED, user, databaseType);
				}
			}
			// If JUPDATED in Jira Proxy is different, update it
			if (!(JUPDATED.equals("#EMPTY#"))){
				String existingJUPDATED = jiraProxy.getAttributeValue("Jira Updated On");
				if (!(existingJUPDATED.equals(JUPDATED))){
					jiraProxy.setCustomAttributeValue(RTAttributeJUPDATED.getAttributeId(), JUPDATED, user, databaseType);
				}
			}
			
			// At this point the attributes of Jira are in sync
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
