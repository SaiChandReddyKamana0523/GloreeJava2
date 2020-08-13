package com.gloree.utils;

//GloreeJava2


import java.util.ArrayList;
import java.util.Iterator;

import com.gloree.beans.*;


import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;

public class ClearQuestUtil {
	
	//
	// This class is used to run static queries to get 
	// 1. list of subfolders in a project
	// 2. list of requirements in a project. 
	// 3. Delete a folder in the db.
	//
	public static void pushToTraceCloud(Requirement requirement, User user, 
			String CTCID, String CTCHEADLINE, String TESTCASEID, String TESTCASEHEADLINE, String CTCWEBLINK,
			String RELATEDSCRID, String RELATEDSCRNAME, String SCRID, String SCRTITLE, String SCRWEBLINK, 
			SecurityProfile securityProfile,
			HttpServletRequest request, String databaseType){
		java.sql.Connection con = null;
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			Project project = new Project(requirement.getProjectId(), databaseType);

			if ((CTCHEADLINE != null) && (!(CTCHEADLINE.equals("")) ) && (!(CTCHEADLINE.toLowerCase().equals("null")) )  ){
				// means TR NAME was sent, so lets do the TR to TC to requirement mapping.

				// we won't use the CTCWEBLINK. Instead, we will use dynamic call back system.
				String serverName = request.getServerName();
				CTCWEBLINK = "http://" + serverName +  "/GloreeJava2/cgi-bin/CQRedirector.cgi?CQID=" + CTCID + 
						"&CQTYPE=Test Result";

				pushTCAndTRToTraceCloud(project, requirement, user, 
						CTCID, CTCHEADLINE, TESTCASEID, TESTCASEHEADLINE, CTCWEBLINK, 
						RELATEDSCRID, RELATEDSCRNAME, securityProfile, request, databaseType, con);
			}
			
			if ((SCRTITLE != null) && (!(SCRTITLE.equals("")) )  && (!(SCRTITLE.toLowerCase().equals("null")) ) ){
				// means SCR NAME was sent, so lets do the SCR to requirement mapping.
				
				// We won't use SCRWEBLINK any more. Instead we will use the dynamic call back system .
				String serverName = request.getServerName();
				SCRWEBLINK = "http://" + serverName +  "/GloreeJava2/cgi-bin/CQRedirector.cgi?CQID=" + SCRID + 
						"&CQTYPE=SCR";
				pushSCRToTraceCloud(project, requirement, user, SCRID, SCRTITLE,  SCRWEBLINK, securityProfile, request, databaseType, con);
			}
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
	}	
	
	public static void pushTCAndTRToTraceCloud(Project project, Requirement requirement, User user, 
			String CTCID, String CTCHEADLINE, String TESTCASEID, String TESTCASEHEADLINE, String CTCWEBLINK,
			String RELATEDSCRID, String RELATEDSCRNAME, SecurityProfile securityProfile,
			HttpServletRequest request, String databaseType , java.sql.Connection con){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {

			// lets get the TR and TC Req types
			RequirementType tRRT = null;
			RequirementType tCRT = null;
						
			
					
			ArrayList requirementTypes = project.getMyRequirementTypes();
			Iterator rT = requirementTypes.iterator();
			while (rT.hasNext()){
				RequirementType tempRT = (RequirementType) rT.next();
				if (tempRT.getRequirementTypeShortName().equals("TR")){
					tRRT = tempRT;
				}
				if (tempRT.getRequirementTypeShortName().equals("TC")){
					tCRT = tempRT;
				}
			
			}

			
			if (tRRT == null){
				// since Test Result Req Type does not exist, lets create one.
				tRRT = new RequirementType(project.getProjectId() ,project.getProjectName(), 
						"TR", "Test Results", "Test Results",
						9, 0, 0, 1, 
						1, "", user.getEmailId(),  databaseType);
				
			}
			if (tCRT == null){
				tCRT = new RequirementType(project.getProjectId() ,project.getProjectName(), 
						"TC", "Test Cases", "Test Cases",
						8, 0, 0, 1, 
						1, "", user.getEmailId(),  databaseType);
			}
			
			
			
			
			// lets make sure 'Clear Quest URL' attribute exists in the TR Req Type. If doesn't then create it.
			int status = ProjectUtil.isUniqueAttribute(tRRT.getRequirementTypeId(), "Clear Quest URL");
			if (status == 1){
				// means that the attribute Clear Quest URL does not exist in the TR req type . So lets create it.
				RTAttribute rTAttribute = new RTAttribute(project.getProjectId(),0, 0, tRRT.getRequirementTypeId(), "Clear Quest URL" , 
						"URL" , "zzy",	0, "", 
						"", "A link back to the source record in the Clear Quest System",
						0, 0, 0,
						user.getEmailId(), databaseType);
			}
			
			
			
			// See if the TR Proxy exists in this project
			// if not, create one
			// update the cq tr mapping entry
			// At this point TR Proxy exists
			String sql = " select requirement_id 'tRProxyId' " +
					" from  cq_tc_mapping " +
					" where cq_id = ? " +
					" and cq_type = 'Test Result' " +
					" and project_id = ?  ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, CTCID);
			prepStmt.setInt(2, requirement.getProjectId());
			rs = prepStmt.executeQuery();
			int tRProxyId = 0 ;
			while (rs.next()){
				tRProxyId = rs.getInt("tRProxyId");
			}
			
			prepStmt.close();
			rs.close();
			
			Requirement tRProxy = null;
			if (tRProxyId == 0 ){
				// This TR does not exist as a TR Proxy in the target project. So, we will need to create a new one and update the mapping entry.
				
				tRProxy = new Requirement("", tRRT.getRequirementTypeId(), tRRT.getRootFolderId() , requirement.getProjectId(), 
						 CTCHEADLINE, CTCHEADLINE, 
						"Medium", user.getEmailId(), "", 0,
						"", user.getEmailId() , databaseType);
				
				// now that we created a new TRProxy , lets map the relationship between the CTCID in CQ and TRProxy in TraceCloud
				sql = "insert into cq_tc_mapping(cq_id, cq_type, project_id, requirement_id, full_tag)" +
					" values(?,?,?,?,?) ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, CTCID);
				prepStmt.setString(2, "Test Result");
				prepStmt.setInt(3, tRProxy.getProjectId());
				prepStmt.setInt(4, tRProxy.getRequirementId());
				prepStmt.setString(5, tRProxy.getRequirementFullTag());
				prepStmt.execute();
			}
			else {
				tRProxy  = new Requirement (tRProxyId, databaseType); 
				if (tRProxy.getRequirementId() == 0){
					// this means that there is a mapping in our cq_tc_mapping table between a TR Object in CQ an a TRProxy object in TC
					// however we weren't able to create that req.  An indication that some one has deleted the TC Object in TraceCloud
					// so we re-create the requirement and then add a new mapping.
					tRProxy = new Requirement("", tRRT.getRequirementTypeId(), tRRT.getRootFolderId() , requirement.getProjectId(), 
							 CTCHEADLINE, CTCHEADLINE, 
							"Medium", user.getEmailId(), "", 0,
							"", user.getEmailId() , databaseType);
					
					// now that we created a new TRProxy , lets map the relationship between the CTCID in CQ and TRProxy in TraceCloud
					sql = "insert into cq_tc_mapping(cq_id, cq_type, project_id, requirement_id, full_tag)" +
						" values(?,?,?,?,?) ";
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, CTCID);
					prepStmt.setString(2, "Test Result");
					prepStmt.setInt(3, tRProxy.getProjectId());
					prepStmt.setInt(4, tRProxy.getRequirementId());
					prepStmt.setString(5, tRProxy.getRequirementFullTag());
					prepStmt.execute();
					
				}
			}
			
			// At this point, we have a TR Proxy requirement.
			
			
			// If the TR Proxy name  is different,  update it.
			if (!(tRProxy.getRequirementName().equals(CTCHEADLINE))){
				// the name may have changed. lets update it in TraceCloud
				tRProxy = new Requirement( tRProxy.getRequirementId(),
						CTCHEADLINE, 
						tRProxy.getRequirementDescription(),
						tRProxy.getRequirementPriority(), 
						tRProxy.getRequirementOwner(), 
						tRProxy.getRequirementPctComplete(), 
						tRProxy.getRequirementExternalUrl(), user.getEmailId() , request, databaseType);
				
			}
			// At this point TR Proxy exists and is in sync with Clear Quest.
			
			
			
			
			
			// If CQ URL in TR Proxy is different, update it
			
			String existingCTCWEBLINK = tRProxy.getAttributeValue("Clear Quest URL");
			if (existingCTCWEBLINK == null){
				existingCTCWEBLINK = "";
			}
			if (!(existingCTCWEBLINK.equals(CTCWEBLINK))){
				// the CTCWEBLINK may have changed. lets update it.
				ArrayList rTAttributes = tRRT.getAllAttributesInRequirementType();
				Iterator rTA = rTAttributes.iterator();
				while (rTA.hasNext()){
					RTAttribute rTAttribute = (RTAttribute) rTA.next();
					if (rTAttribute.getAttributeName().equals("Clear Quest URL")){
						tRProxy.setCustomAttributeValue(rTAttribute.getAttributeId(), CTCWEBLINK, user, databaseType);
					}
				}
			}
			// At this piont the attributes of TR are in sync
			
		
			
			
			
			// See if TC Proxy exists
			// if not, create one
			// update cq tr mapping entry
			// At this point TC Proxy exists
			sql = " select requirement_id 'tCProxyId' " +
					" from  cq_tc_mapping " +
					" where cq_id = ? " +
					" and cq_type = 'Test Case' " +
					" and project_id = ?  ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, TESTCASEID);
			prepStmt.setInt(2, requirement.getProjectId());
			rs = prepStmt.executeQuery();
			int tCProxyId = 0 ;
			while (rs.next()){
				tCProxyId = rs.getInt("tCProxyId");
			}
			
			prepStmt.close();
			rs.close();

			Requirement tCProxy = null;
			if (tCProxyId == 0 ){
				// This TC does not exist as a TC Proxy in the target project. So, we will need to create a new one and update the mapping entry.
			
				
				tCProxy = new Requirement("", tCRT.getRequirementTypeId(), tCRT.getRootFolderId() , requirement.getProjectId(), 
					 TESTCASEHEADLINE, TESTCASEHEADLINE, 
					"Medium", user.getEmailId(), "", 0,
					"", user.getEmailId() , databaseType);
				

				// now that we created a new TRProxy , lets map the relationship between the TESTCASEID in CQ and TRProxy in TraceCloud
				sql = "insert into cq_tc_mapping(cq_id,cq_type, project_id, requirement_id, full_tag)" +
					" values(?,?,?, ?,?) ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, TESTCASEID);
				prepStmt.setString(2, "Test Case");
				prepStmt.setInt(3, tCProxy.getProjectId());
				prepStmt.setInt(4, tCProxy.getRequirementId());
				prepStmt.setString(5, tCProxy.getRequirementFullTag());
				prepStmt.execute();

			}
			else {
				tCProxy  = new Requirement (tCProxyId, databaseType); 
				if (tCProxy.getRequirementId() == 0){
					// this means that there is a mapping in our cq_tc_mapping table between a TC Object in CQ an a TCProxy object in TC
					// however we weren't able to create that req.  An indication that some one has deleted the TC Object in TraceCloud
					// so we re-create the requirement and then add a new mapping.
					tCProxy = new Requirement("", tCRT.getRequirementTypeId(), tCRT.getRootFolderId() , requirement.getProjectId(), 
							 TESTCASEHEADLINE, TESTCASEHEADLINE, 
							"Medium", user.getEmailId(), "", 0,
							"", user.getEmailId() , databaseType);
						

						// now that we created a new TRProxy , lets map the relationship between the TESTCASEID in CQ and TRProxy in TraceCloud
						sql = "insert into cq_tc_mapping(cq_id,cq_type, project_id, requirement_id, full_tag)" +
							" values(?,?,?, ?,?) ";
						prepStmt = con.prepareStatement(sql);
						prepStmt.setString(1, TESTCASEID);
						prepStmt.setString(2, "Test Case");
						prepStmt.setInt(3, tCProxy.getProjectId());
						prepStmt.setInt(4, tCProxy.getRequirementId());
						prepStmt.setString(5, tCProxy.getRequirementFullTag());
						prepStmt.execute();
					}
			}
			
			// At this point, we have a TR Proxy requirement.
			
			// If the TCProxy name  is different,  update it.
			if (!(tCProxy.getRequirementName().equals(TESTCASEHEADLINE))){
				// the name may have changed. lets update it in TraceCloud
				tCProxy = new Requirement( tCProxy.getRequirementId(), 
						TESTCASEHEADLINE, 
						tCProxy.getRequirementDescription(),
						tCProxy.getRequirementPriority(), 
						tCProxy.getRequirementOwner(), 
						tCProxy.getRequirementPctComplete(), 
						tCProxy.getRequirementExternalUrl(), user.getEmailId() , request, databaseType);

			}
			// At this point TC Proxy is in sync
			
			// currently we don't support any attributes of TC.
			// if we ever decide to support and sync attributes between TC and CQ, this is where we do it.
			
			
			
			
			// If TR Proxy to TC Proxy a Trace Exists, make sure it's green
			// Otherwise create a trace
			
			// create a trace from TR proxy to TC Proxy
			String status2 = RequirementUtil.createTraces(project, tCProxy.getRequirementId(), 
					"", tRProxy.getRequirementFullTag() , project.getProjectId(), securityProfile,  databaseType);
		
			
			// Clear this suspect if one exists
			RequirementUtil.clearSuspect(tRProxy.getRequirementId(), tCProxy.getRequirementId(), user.getEmailId(), securityProfile, databaseType);
			
			// If TC Proxy  to Target Requirement trace exists make sure its green
			// Otherwise, create a trace.
		
			// Create a trace from TC Proxy to the Target Requirement
			status2 = RequirementUtil.createTraces(project, requirement.getRequirementId(), 
					"", tCProxy.getRequirementFullTag() , project.getProjectId(), securityProfile,  databaseType);
			
			// Clear a suspect if one exists.
			RequirementUtil.clearSuspect(tCProxy.getRequirementId(), requirement.getRequirementId(), user.getEmailId(), securityProfile, databaseType);
			
		
			// if a relatedscrid and relatedscrname were sent in, then we need to creat the scr proxy object
			// and trace it to the target requirement
			if ((RELATEDSCRID != null) && (!(RELATEDSCRID.equals(""))) && (!(RELATEDSCRID.equals("null"))) ){

				pushSCRToTraceCloud(project, requirement, user, RELATEDSCRID, RELATEDSCRNAME,  "" , securityProfile, request, databaseType, con);
			}
		
			
			prepStmt.close();
			rs.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
		}
	}	

	public static void pushSCRToTraceCloud(Project project, Requirement requirement, User user, String SCRID, String SCRTITLE,  String SCRWEBLINK,
			SecurityProfile securityProfile,
			HttpServletRequest request, String databaseType , java.sql.Connection con){
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {

	
			// lets get the TR and TC Req types
			RequirementType sCRRT = null;
			
					
			ArrayList requirementTypes = project.getMyRequirementTypes();
			Iterator rT = requirementTypes.iterator();
			while (rT.hasNext()){
				RequirementType tempRT = (RequirementType) rT.next();
				if (tempRT.getRequirementTypeShortName().equals("SCR")){
					sCRRT = tempRT;
				}
			}

			
			if (sCRRT == null){
				sCRRT = new RequirementType(project.getProjectId() ,project.getProjectName(), 
						"SCR", "SCR", "SCR",
						9, 0, 0, 1, 
						1, "", user.getEmailId(),  databaseType);
				
			}
			
			// lets make sure 'Clear Quest URL' attribute exists in the SCR Req Type. If doesn't then create it.
			int status = ProjectUtil.isUniqueAttribute(sCRRT.getRequirementTypeId(), "Clear Quest URL");
			if (status == 1){
				// means that the attribute Clear Quest URL does not exist in the TR req type . So lets create it.
				RTAttribute rTAttribute = new RTAttribute(project.getProjectId(),0, 0, sCRRT.getRequirementTypeId(), "Clear Quest URL" , 
						"URL" , "zzy",	0, "", 
						"", "A link back to the source record in the Clear Quest System",
						0, 0, 0,
						user.getEmailId(), databaseType);
			}
			
			
			
			// See if the SCR Proxy exists in this project
			// if not, create one
			// update the cq tr mapping entry
			// At this point TR Proxy exists
			String sql = " select requirement_id 'sCRProxyId' " +
					" from  cq_tc_mapping " +
					" where cq_id = ? " +
					" and cq_type = 'SCR' " +
					" and project_id = ?  ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, SCRID);
			prepStmt.setInt(2, requirement.getProjectId());
			rs = prepStmt.executeQuery();
			int sCRProxyId = 0 ;
			while (rs.next()){
				sCRProxyId = rs.getInt("sCRProxyId");
			}
			
			prepStmt.close();
			rs.close();
			
			Requirement sCRProxy = null;
			if (sCRProxyId == 0 ){
				// This SCR does not exist as a SCR Proxy in the target project. So, we will need to create a new one and update the mapping entry.
				
				sCRProxy = new Requirement("", sCRRT.getRequirementTypeId(), sCRRT.getRootFolderId() , requirement.getProjectId(), 
						 SCRTITLE, SCRTITLE, 
						"Medium", user.getEmailId(), "", 0,
						"", user.getEmailId() , databaseType);
				
				// now that we created a new TRProxy , lets map the relationship between the SCRID in CQ and SCRProxy in TraceCloud
				sql = "insert into cq_tc_mapping(cq_id, cq_type, project_id, requirement_id, full_tag)" +
					" values(?,?,?,?,?) ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, SCRID);
				prepStmt.setString(2, "SCR");
				prepStmt.setInt(3, sCRProxy.getProjectId());
				prepStmt.setInt(4, sCRProxy.getRequirementId());
				prepStmt.setString(5, sCRProxy.getRequirementFullTag());
				prepStmt.execute();
			}
			else {
				sCRProxy  = new Requirement (sCRProxyId, databaseType); 
				if (sCRProxy.getRequirementId() == 0){
					// this means that there is a mapping in our cq_tc_mapping table between a SCR Object in CQ an a SCRProxy object in TC
					// however we weren't able to create that req.  An indication that some one has deleted the SCR Object in TraceCloud
					// so we re-create the requirement and then add a new mapping.
					sCRProxy = new Requirement("", sCRRT.getRequirementTypeId(), sCRRT.getRootFolderId() , requirement.getProjectId(), 
							 SCRTITLE, SCRTITLE, 
							"Medium", user.getEmailId(), "", 0,
							"", user.getEmailId() , databaseType);
					
					// now that we created a new TRProxy , lets map the relationship between the SCRID in CQ and SCRProxy in TraceCloud
					sql = "insert into cq_tc_mapping(cq_id, cq_type, project_id, requirement_id, full_tag)" +
						" values(?,?,?,?,?) ";
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, SCRID);
					prepStmt.setString(2, "SCR");
					prepStmt.setInt(3, sCRProxy.getProjectId());
					prepStmt.setInt(4, sCRProxy.getRequirementId());
					prepStmt.setString(5, sCRProxy.getRequirementFullTag());
					prepStmt.execute();
					}
			}
			
			// At this point, we have a TR Proxy requirement.
			
			
			// If the TR Proxy name  is different,  update it.
			if (!(sCRProxy.getRequirementName().equals(SCRTITLE))){
				// the name may have changed. lets update it in TraceCloud
				sCRProxy = new Requirement( sCRProxy.getRequirementId(),
						SCRTITLE, 
						sCRProxy.getRequirementDescription(),
						sCRProxy.getRequirementPriority(), 
						sCRProxy.getRequirementOwner(), 
						sCRProxy.getRequirementPctComplete(), 
						sCRProxy.getRequirementExternalUrl(), user.getEmailId() , request, databaseType);
				
			}
			// At this point SCR Proxy exists and is in sync with Clear Quest.
			
			
			
			
			
			// If CQ URL in SCR Proxy is different, update it
			
			String existingCTCWEBLINK = sCRProxy.getAttributeValue("Clear Quest URL");
			if (existingCTCWEBLINK == null){
				existingCTCWEBLINK = "";
			}
			if (!(existingCTCWEBLINK.equals(SCRWEBLINK))){
				// the CTCWEBLINK may have changed. lets update it.
				ArrayList rTAttributes = sCRRT.getAllAttributesInRequirementType();
				Iterator rTA = rTAttributes.iterator();
				while (rTA.hasNext()){
					RTAttribute rTAttribute = (RTAttribute) rTA.next();
					if (rTAttribute.getAttributeName().equals("Clear Quest URL")){
						sCRProxy.setCustomAttributeValue(rTAttribute.getAttributeId(), SCRWEBLINK, user, databaseType);
					}
				}
			}
			// At this piont the attributes of SCR are in sync
			
		
			
				
			// If SCR Proxy  to Target Requirement trace exists make sure its green
			// Otherwise, create a trace.
		
			// Create a trace from SCR Proxy to the Target Requirement
			String status2 = RequirementUtil.createTraces(project, requirement.getRequirementId(), 
					"", sCRProxy.getRequirementFullTag() , project.getProjectId(), securityProfile,  databaseType);
			
			// Clear a suspect if one exists.
			RequirementUtil.clearSuspect(sCRProxy.getRequirementId(), requirement.getRequirementId(), user.getEmailId(), securityProfile, databaseType);
			
		
			prepStmt.close();
			rs.close();

		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
		}
	}	
	
	public static ArrayList getProxies(String cQId , String proxyType, User user, SecurityProfile securityProfile, String databaseType){
		ArrayList proxies = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select ct.requirement_id " +
					" from cq_tc_mapping ct" +
					" where ct.cq_id = ? " +
					" and ct.cq_type = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, cQId);
			prepStmt.setString(2, proxyType);
			

			
			rs = prepStmt.executeQuery();
			while (rs.next()){
				int requirementId = rs.getInt("requirement_id");
				Requirement requirement = new Requirement(requirementId, databaseType);
				
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" + requirement.getFolderId()))) {
					requirement.redact();
				}
				
				
				proxies.add(requirement);
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
		return (proxies);
	}	
	

}
