package com.gloree.beans;

//GloreeJava2

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;

import com.gloree.utils.EmailUtil;
import com.gloree.utils.ProjectUtil;
import com.gloree.utils.RequirementUtil;

import org.jsoup.Jsoup;



public class Requirement {

	private int requirementId;
	private int sourceRequirementId;
	private int requirementTypeId;
	private int folderId;
	private int projectId;
	private String parentFullTag;
	private String requirementName;
	private String requirementDescription;
	private String requirementTag;
	private String requirementFullTag;
	private int version ;
	private String approvedByAllDt ;
	private String approvers ;
	private String requirementStatus;
	private String requirementPriority;
	private String requirementOwner;
	private String requirementLockedBy;
	private int requirementPctComplete;
	private String requirementExternalUrl;
	private String requirementTraceTo;
	private String requirementTraceFrom;
	private String userDefinedAttributes;
	private String glossary;
	private String testingStatus;
	private int deleted;
	private String folderPath;
	private String createdBy;
	private String createdDt;
	private String lastModifiedBy;
	private String lastModifiedDt;
	private int daysSinceSubmittedForApproval;
	private int daysSinceLastApprovalReminder;
	
	
	private String requirementTypeName;
	
	
	// The following method is called when the requrement's core values are already known and the system is only
	// interested in creating a bean. . 
	public Requirement (int requirementId, int requirementTypeId, int folderId, int projectId,
			String requirementName, String requirementDescription, 
			String requirementTag, String requirementFullTag,
			int version, String approvedByAllDt, String approvers,
			String requirementStatus, String requirementPriority, String requirementOwner, String requirementLockedBy,
			int requirementPctComplete, String requirementExternalUrl ,String requirementTraceTo, 
			String requirementTraceFrom , String userDefinedAttributes , String testingStatus, 
			int deleted, String folderPath, String createdBy, String lastModifiedBy, String requirementTypeName,
			String createdDt ){
		this.requirementId = requirementId;
		this.requirementTypeId = requirementTypeId;
		this.folderId = folderId;
		this.projectId = projectId;
		
		this.requirementName = requirementName;
		this.requirementDescription = RequirementUtil.removeWordCrap(requirementDescription);
		this.requirementTag = requirementTag;
		this.requirementFullTag = requirementFullTag;
		this.version = version;
		this.approvedByAllDt = approvedByAllDt;
		this.approvers  = approvers;
		this.requirementStatus = requirementStatus;
		this.requirementPriority = requirementPriority;
		this.requirementOwner = requirementOwner;
		this.requirementLockedBy = requirementLockedBy;
		this.requirementPctComplete = requirementPctComplete;
		this.requirementExternalUrl = requirementExternalUrl;
		this.requirementTraceTo = requirementTraceTo;
		this.requirementTraceFrom = requirementTraceFrom;
		this.userDefinedAttributes = userDefinedAttributes;
		this.testingStatus = testingStatus;
		this.deleted = deleted;
		this.folderPath = folderPath;
		this.createdBy = createdBy;
		this.createdDt = createdDt;
		this.lastModifiedBy = lastModifiedBy;
		//this.lastModifiedDt = lastModifiedDt;
		
		this.requirementTypeName = requirementTypeName;
	}

	// the following method is used when the system knows only the requirementId and wants this bean
	// to go and get details i.e. attributes, traceability , child reqs etc....
	public Requirement (int requirementId, String databaseType) {

		databaseType = "mySQL";
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
						" r.name, r.description, r.tag, r.full_tag, " +
						" r.version, date_format(r.approved_by_all_dt, '%d-%b-%Y') \"approved_by_all_dt\"," +
						" r.approvers ," + 
						" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
						" r.trace_to, r.trace_from, r.user_defined_attributes, r.glossary, r.testing_status, r.deleted," +
						" f.folder_path, r.created_by, " +
						" date_format(r.created_dt, '%d-%b-%Y') \"created_dt\", " +
						" r.last_modified_by, "
						+ " date_format(r.last_modified_dt, '$d-%b-%Y') \"last_modified_dt\" , "
						+ " rt.name \"requirement_type_name\"  " +
						" FROM gr_requirements r , gr_requirement_types rt, gr_folders f" +
						" where r.id = ?  and r.requirement_type_id = rt.id " +
						" and r.folder_id = f.id " ;
			}
			else {
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
				" r.name, r.description, r.tag, r.full_tag, " +
				" r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\"," +
				" r.approvers ," + 
				" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
				" r.trace_to, r.trace_from, r.user_defined_attributes, r.glossary ,  r.testing_status, r.deleted," +
				" f.folder_path, r.created_by, " +
				" to_char(r.created_dt, 'DD MON YYYY') \"created_dt\", " +
				" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\"  " +
				" FROM gr_requirements r , gr_requirement_types rt, gr_folders f" +
				" where r.id = ?  and r.requirement_type_id = rt.id " +
				" and r.folder_id = f.id " ;
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			ResultSet rs = prepStmt.executeQuery();
			
			if (rs.next()){
				this.requirementId = rs.getInt("id");
				this.requirementTypeId = rs.getInt("requirement_type_id");
				this.folderId = rs.getInt("folder_id");
				this.projectId = rs.getInt("project_id");
				this.parentFullTag = rs.getString("parent_full_tag");
				
				this.requirementName = rs.getString("name");
				this.requirementDescription = RequirementUtil.removeWordCrap(rs.getString ("description"));
				this.requirementTag = rs.getString("tag");		
				this.requirementFullTag = rs.getString("full_tag");
				this.version = rs.getInt("version");
				this.approvedByAllDt = rs.getString("approved_by_all_dt");
				this.approvers  = rs.getString("approvers");
				this.requirementStatus = rs.getString("status");
				this.requirementPriority = rs.getString("priority");
				this.requirementOwner = rs.getString("owner");
				this.requirementLockedBy = rs.getString("locked_by");
				this.requirementPctComplete = rs.getInt("pct_complete");
				this.requirementExternalUrl = rs.getString("external_url");
				this.requirementTraceTo = rs.getString("trace_to");
				this.requirementTraceFrom = rs.getString("trace_from");
				this.userDefinedAttributes = rs.getString("user_defined_attributes");
				this.glossary = rs.getString("glossary");
				this.testingStatus = rs.getString("testing_status");
				this.deleted = rs.getInt("deleted");
				this.folderPath = rs.getString("folder_path");
				this.createdBy = rs.getString("created_by");
				this.createdDt = rs.getString("created_dt");
				this.lastModifiedBy = rs.getString("last_modified_by") ;
				this.lastModifiedDt = rs.getString("last_modified_dt");
				
				this.requirementTypeName = rs.getString("requirement_type_name");
			}
			
			prepStmt.close();
			rs.close();
			con.close();
			
			this.setDaysSinceLastApprovalReminder(databaseType);
			this.setDaysSinceSubmittedForApproval(databaseType);
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		
	}


	// the following method is used when the system knows only the req full tag and the project id 
	// and we have to create the bean
	public Requirement (String fullTag, int projectId, String databaseType) {

		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			if (fullTag.contains(":")){
				// this req exists in an external project and fullTag is like Cisco:BR-1 
				String[] reqDetails = fullTag.split(":");
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.glossary,  r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, " +
					" date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\"  " +
					" FROM gr_requirements r , gr_requirement_types rt, gr_folders f, gr_projects p " +
					" where p.short_name = ?  and  r.project_id = p.id  and r.full_tag = ?  " +
					" and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " ;
			
				PreparedStatement prepStmt = con.prepareStatement(sql);
				
				prepStmt.setString(1, reqDetails[0]);
				prepStmt.setString(2, reqDetails[1]);
				ResultSet rs = prepStmt.executeQuery();
				
				if (rs.next()){
					this.requirementId = rs.getInt("id");
					this.requirementTypeId = rs.getInt("requirement_type_id");
					this.folderId = rs.getInt("folder_id");
					this.projectId = rs.getInt("project_id");
					this.parentFullTag = rs.getString("parent_full_tag");
					
					this.requirementName = rs.getString("name");
					this.requirementDescription = RequirementUtil.removeWordCrap(rs.getString ("description"));
					this.requirementTag = rs.getString("tag");		
					this.requirementFullTag = rs.getString("full_tag");
					this.version = rs.getInt("version");
					this.approvedByAllDt = rs.getString("approved_by_all_dt");
					this.approvers  = rs.getString("approvers");
					this.requirementStatus = rs.getString("status");
					this.requirementPriority = rs.getString("priority");
					this.requirementOwner = rs.getString("owner");
					this.requirementLockedBy = rs.getString("locked_by");
					this.requirementPctComplete = rs.getInt("pct_complete");
					this.requirementExternalUrl = rs.getString("external_url");
					this.requirementTraceTo = rs.getString("trace_to");
					this.requirementTraceFrom = rs.getString("trace_from");
					this.userDefinedAttributes = rs.getString("user_defined_attributes");
					this.glossary = rs.getString("glossary");
					this.testingStatus = rs.getString("testing_status");
					this.deleted = rs.getInt("deleted");
					this.folderPath = rs.getString("folder_path");
					this.createdBy = rs.getString("created_by");
					this.createdDt = rs.getString("created_dt");
					this.lastModifiedBy = rs.getString("last_modified_by") ;
					this.lastModifiedDt = rs.getString("last_modified_dt");
					
					this.requirementTypeName = rs.getString("requirement_type_name");
				}
			
				prepStmt.close();
				rs.close();
			}
			else {
				// this req exists in the current project
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, " +
					" r.trace_to, r.trace_from, r.user_defined_attributes, r.glossary,  r.testing_status, r.deleted," +
					" f.folder_path, r.created_by, " +
					" date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\", " +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\"  " +
					" FROM gr_requirements r , gr_requirement_types rt, gr_folders f" +
					" where  r.project_id = ?  and r.full_tag = ?  " +
					" and r.requirement_type_id = rt.id " +
					" and r.folder_id = f.id " ;
			
				PreparedStatement prepStmt = con.prepareStatement(sql);
				
				prepStmt.setInt(1, projectId);
				prepStmt.setString(2, fullTag);
				ResultSet rs = prepStmt.executeQuery();
				
				if (rs.next()){
					this.requirementId = rs.getInt("id");
					this.requirementTypeId = rs.getInt("requirement_type_id");
					this.folderId = rs.getInt("folder_id");
					this.projectId = rs.getInt("project_id");
					this.parentFullTag = rs.getString("parent_full_tag");
					
					this.requirementName = rs.getString("name");
					this.requirementDescription = RequirementUtil.removeWordCrap(rs.getString ("description"));
					this.requirementTag = rs.getString("tag");		
					this.requirementFullTag = rs.getString("full_tag");
					this.version = rs.getInt("version");
					this.approvedByAllDt = rs.getString("approved_by_all_dt");
					this.approvers  = rs.getString("approvers");
					this.requirementStatus = rs.getString("status");
					this.requirementPriority = rs.getString("priority");
					this.requirementOwner = rs.getString("owner");
					this.requirementLockedBy = rs.getString("locked_by");
					this.requirementPctComplete = rs.getInt("pct_complete");
					this.requirementExternalUrl = rs.getString("external_url");
					this.requirementTraceTo = rs.getString("trace_to");
					this.requirementTraceFrom = rs.getString("trace_from");
					this.userDefinedAttributes = rs.getString("user_defined_attributes");
					this.glossary = rs.getString("glossary");
					this.testingStatus = rs.getString("testing_status");
					this.deleted = rs.getInt("deleted");
					this.folderPath = rs.getString("folder_path");
					this.createdBy = rs.getString("created_by");
					this.createdDt = rs.getString("created_dt");
					this.lastModifiedBy = rs.getString("last_modified_by") ;
					this.lastModifiedDt = rs.getString("last_modified_dt");
					
					this.requirementTypeName = rs.getString("requirement_type_name");
				}
			
				prepStmt.close();
				rs.close();
			}
			
			
			
			
			con.close();
			

			this.setDaysSinceLastApprovalReminder(databaseType);
			this.setDaysSinceSubmittedForApproval(databaseType);
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		
	}

	

	// the following method is used when the system knows only the information required to create a bean in the db
	// the code will do the following :
	// 1. get the next tag value
	// 1.1 find out if this req type has approval work flow enabled. if yes, set status = draft.
	// else status = approved.
	// 2. create the requirement
	// 3. set the bean attributes.
	// 
	// 5. we also create a Requirement Log indicating that a requirement was created.
	// 6. We also make an entry in the requirement_version table for this version.
	// NOTE : since the user defined attributes will default to what the ReqType has defined, it won't come in as a param.
	// NOTE : since the traceTo and traceFrom will be empty at this time, it won't come in as a param.
	
	public Requirement (String parentFullTag, int requirementTypeId,int folderId,int projectId, String requirementName,
			 String requirementDescription,
			String requirementPriority, String requirementOwner, String requirementLockedBy,
			int requirementPctComplete, 
			String requirementExternalUrl, String actorEmailId, String databaseType) {

		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// get the next tag number we can use for this requirement.
			// Since MYSQL doesn't have a concept of seq numbers and since we don't want the system re using req tags,
			// we have created a table called gr_requirements_seq, which has the last tag used for any req type.
			// we update that by 1, read it and use it for our next req.
			String sql = "";
			PreparedStatement prepStmt;
			ResultSet rs;
			
			/*
			// lets replace any \n with <BR>
			if (requirementDescription.contains("\n")){
				requirementDescription = requirementDescription.replace("\n", "<br>");
			}
			*/
			
			// tells mysql that we are using utf8
			
			String nextTag = "";
			if ((parentFullTag != null ) && (!parentFullTag.equals(""))){
				// we have been asked to create a child requirement. so lets get the 
				// next tag according to who the next child is.
				nextTag = RequirementUtil.getNextTag(parentFullTag, projectId,  databaseType);
			}	
			
			if (nextTag.equals("") ||
				((parentFullTag == null) ||(parentFullTag.equals("")))
			)
			{
				// at this point, we were either unable to get the next children tag
				// or the parent tag wasn't given.
				// that means we are create another root level req, and we need to bump up the req number.
				sql = "update gr_requirements_seq set tag = tag + 1 where requirement_type_id = ?  ";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirementTypeId);
				prepStmt.execute();
				
				// now that we have updated the next tag value for this req type, lets retrieve it to create the req.
				sql = "select tag from gr_requirements_seq where requirement_type_id = ?";
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1,requirementTypeId );
				rs = prepStmt.executeQuery();
				if (rs.next()){
					nextTag =  rs.getString("tag");
				}
			}
			// get the Requirement Prefix, so that we can append it to the tag to get the full tag.
			String requirementTypeShortName = "";
			sql = "select short_name from gr_requirement_types where id = ?";			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,requirementTypeId );
			rs = prepStmt.executeQuery();
			if (rs.next()){
				requirementTypeShortName = rs.getString("short_name");
			}
			String fullTag = requirementTypeShortName + "-" + nextTag;
			
			// since we have the tag, lets calculate the level's of tagLevel1, tagLevel2, tagLevel3, tagLevel4
			int tagLevel1 = 0;
			int tagLevel2 = 0;
			int tagLevel3 = 0;
			int tagLevel4 = 0;
			int tagLevel5 = 0;
			int tagLevel6 = 0;
			
			try{
				if (nextTag.contains(".")){
					String [] tags = nextTag.split("\\.");
					if (tags.length > 0){
						tagLevel1 = Integer.parseInt(tags[0]);
					}
					if (tags.length > 1){
						tagLevel2 = Integer.parseInt(tags[1]);
					}
					if (tags.length > 2){
						tagLevel3 = Integer.parseInt(tags[2]);
					}
					if (tags.length > 3){
						tagLevel4 = Integer.parseInt(tags[3]);
					}
					if (tags.length > 4){
						tagLevel5 = Integer.parseInt(tags[4]);
					}
					if (tags.length > 5){
						tagLevel6 = Integer.parseInt(tags[5]);
					}
				}
				else {
					tagLevel1 = Integer.parseInt(nextTag);
				}
				
			}
			catch (Exception e){
				e.printStackTrace();
			}
			
			
			// ALL new requirements are in 'Draft' status , irrespective of the approval status for the req type.
			sql = "insert into gr_requirements (requirement_type_id, folder_id, project_id, parent_full_tag, name," +
				" description, tag, tag_level1, tag_level2, tag_level3, tag_level4, tag_level5, tag_level6, full_tag, " +
				" version, approved_by_all_dt, approvers , " +
				" status, priority, owner, locked_by, pct_complete, external_url, user_defined_attributes," +
				" deleted, created_by, " +
				" created_dt, last_modified_by , last_modified_dt) " +
				" values (?, ?, ?, ?, ?, " +
				" ?, ?, ?,?,?,?,?, ?, ?, " +
				" 1 , null ,'' ," +
				" 'Draft', ?, ?, ?, ? , ?, ''," +
				" 0, ?, " +
				" now(), ?, now())";
			
			// Now insert the row in the database.

		
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			prepStmt.setInt(2, folderId);
			prepStmt.setInt(3, projectId);
			prepStmt.setString(4,parentFullTag.toUpperCase());
			// lets limit the req name to 100 chars, as that's the size int the db,
			if (requirementName == null) {
				requirementName = "";
			}
			if (requirementName.length() > 3999){
				requirementName = requirementName.substring(0, 3990) + "...";
			}
		
			prepStmt.setString(5, requirementName);
			
			if ((requirementDescription == null) || (requirementDescription.equals(""))) {
				requirementDescription = requirementName;
			}
			
			requirementDescription = RequirementUtil.removeWordCrap(requirementDescription);
		

			prepStmt.setString(6, requirementDescription);
			prepStmt.setString(7, nextTag);
			prepStmt.setInt(8, tagLevel1);
			prepStmt.setInt(9, tagLevel2);
			prepStmt.setInt(10, tagLevel3);
			prepStmt.setInt(11, tagLevel4);
			
			prepStmt.setInt(12, tagLevel5);
			prepStmt.setInt(13, tagLevel6);
			
			
			prepStmt.setString(14, fullTag.toUpperCase());
			
			
			prepStmt.setString(15,requirementPriority);
			prepStmt.setString(16,requirementOwner);
			prepStmt.setString(17, requirementLockedBy);
			prepStmt.setInt(18,requirementPctComplete);
			prepStmt.setString(19,requirementExternalUrl);
			
			prepStmt.setString(20, actorEmailId);
			prepStmt.setString(21, actorEmailId);
			prepStmt.execute();
				
			// Now we get the data from the database and populate the bean.
			// note we have special code at the bottom of the method to populate the 
			// user Defined Attributes string.
			// Since TraceTo and TraceFrom will be empty at this point,we can simply set them to it.
			if (databaseType.equals("mySQL")){
				sql = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id, r.parent_full_tag, " +
					" r.name, r.description, r.tag, r.full_tag, " +
					" r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\"," +
					" r.approvers ," + 
					" r.status, r.priority, r.owner, r.locked_by, r.pct_complete, r.external_url, r.deleted, f.folder_path, " +
					" r.testing_status, " +
					" r.created_by," +
					" date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\" ," +
					" r.last_modified_by, r.last_modified_dt, rt.name \"requirement_type_name\" " +
					" FROM gr_requirements r , gr_requirement_types rt, gr_folders f" +
					" where r.requirement_type_id = rt.id  and  r.project_id = ? and r.full_tag = ? " +
					" and r.folder_id = f.id " ;
			}
			
			 


			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, fullTag.toUpperCase());
			rs = prepStmt.executeQuery();
	
			if (rs.next()){
				this.requirementId = rs.getInt("id");
				
				
				this.requirementTypeId = rs.getInt("requirement_type_id");
				this.folderId = rs.getInt("folder_id");
				this.projectId = rs.getInt("project_id");
				this.parentFullTag = rs.getString("parent_full_tag");
				
				this.requirementName = rs.getString("name");
				this.requirementDescription = RequirementUtil.removeWordCrap(rs.getString ("description"));
				this.requirementTag = rs.getString("tag");
				this.requirementFullTag = rs.getString("full_tag");
				this.version = rs.getInt("version");
				this.approvedByAllDt = rs.getString("approved_by_all_dt");
				this.approvers = rs.getString("approvers");

				this.requirementStatus = rs.getString("status");
				this.requirementPriority = rs.getString("priority");
				this.requirementOwner = rs.getString("owner");
				this.requirementLockedBy = rs.getString("locked_by");
				this.requirementPctComplete = rs.getInt("pct_complete");
				this.requirementTraceTo = "";
				this.requirementTraceFrom = "";
				this.requirementExternalUrl = rs.getString("external_url");
				this.deleted = rs.getInt("deleted");
				this.folderPath = rs.getString("folder_path");
				this.testingStatus = rs.getString("testing_status");
				this.createdBy = rs.getString("created_by");
				this.createdDt = rs.getString("created_dt");
				this.lastModifiedBy = rs.getString("last_modified_by") ;
				this.lastModifiedDt = rs.getString("last_modified_dt");
				
				this.requirementTypeName = rs.getString("requirement_type_name");
			}
	
			
			// Lets make a log entry for this action.
			String log = "Created Requirement ... " +
				"<br>Status: " + this.getApprovalStatus() + 
				"<br>Priority : " + this.getRequirementPriority() +
				"<br>Owner : " + this.getRequirementOwner() + 
				"<br>Pct Complete : " + this.getRequirementPctComplete() +
				"<br>External URL : " + this.getRequirementExternalUrl() +
				"<br>Version :" + this.getVersion()  +
				"<br>Approved By All Dt :" + this.getApprovedByAllDt()  +
				"<br>Approvers  :" + this.getApprovers()  +
				"<br>Name : " + this.requirementName + 
				"<br>Description : " + this.requirementDescription ;
			RequirementUtil.createRequirementLog(this.requirementId,log, actorEmailId,databaseType);
			
			// Once the requirement is created in the db, we also need to get a list of user defined attributes and their default values for this RT
			// and create them for this Requirement.
			
			// we have to wait till this point to do this, since we need the requirement Id.
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_r_attribute_values (requirement_id, attribute_id,value,created_by,created_dt,last_modified_by, last_modified_dt )" +
					" select '" + this.requirementId + "' , a.id , a.default_value , 'system',now(), 'system', now() " + 
					" from gr_rt_attributes a " + 
					" where a.requirement_type_id = ?";
			}
			else {
				sql = " insert into gr_r_attribute_values (requirement_id, attribute_id,value,created_by,created_dt,last_modified_by, last_modified_dt )" +
				" select '" + this.requirementId + "' , a.id , a.default_value , 'system', sysdate , 'system', sysdate " + 
				" from gr_rt_attributes a " + 
				" where a.requirement_type_id = ?";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			prepStmt.execute();

			// we have to wait till this point to set the user defined values attribute in this bean.
			// because the above step created the default values.
			// the following method take the this.requirementId goes to the db 
			// and set the userDefinedAttribute field in db and set this
			// beans this.userDefinedAttributes value.
			setUserDefinedAttributes(con, databaseType);

			// at this point, lets create an entry in the gr_requirement_version table
			RequirementUtil.createRequirementVersion(this.requirementId);

			
			// Once the requirement is created , lets refresh the Glossary list
			refreshRequirementGlossary();

			prepStmt.close();
			rs.close();
			con.close();
			

			this.setDaysSinceLastApprovalReminder(databaseType);
			this.setDaysSinceSubmittedForApproval(databaseType);
			
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		
	}

	// this signature is typically used to update the requirement 
	// object in the db, when called by editRequirement form.
	//
	// version will be updated in the system and doesn't have to be sent in
	// approvers will be calculatd in this method and doesnt' have to be sent in
	// status is a calculated value and should not be sent in.
	// we also create a new entry in the requirement version table if the version got bumped up.
	
	public Requirement (int requirementId, String newRequirementName,String newRequirementDescription,
			String newRequirementPriority, String newRequirementOwner,
			int newRequirementPctComplete, String newRequirementExternalUrl, String actorEmailId, 
			HttpServletRequest request, String databaseType) {
		java.sql.Connection con  = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			Requirement requirement = new Requirement(requirementId,databaseType);
			// if the req name / desc have changed, we bump up the version.
			// other wise, current version is the new version.
			boolean versionChanged = false;
			int newVersion = requirement.version;
			
			if (!
					(
						(requirement.getRequirementName().equals(newRequirementName)) 
						&& 
						(requirement.getRequirementDescription().equals(newRequirementDescription))
					)
				){
				// This means the req name / description have changed.
				versionChanged = true;
				
				// now we need to update the version, set the traces suspect and update the db 
				newVersion++;

				// a version change should lead to a new Version and Status should be reset to
				// Draft.
				// also the approvers need to be empty since we are talking about a new version
				// of the requirement. When this req is submitted for approval ,it will be
				// refilled with all the valid approvers.
				String sql = "update gr_requirements" +
				" set version = ? ," +
				" status = ?  ," +
				" approvers = null ," +
				" approved_by_all_dt = null " +
				" where id = ? "  ;
	
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1, newVersion);
				prepStmt2.setString(2, "Draft");
				prepStmt2.setInt(3, requirementId);
				prepStmt2.execute();
		

				// at this point cancel any pending requirement approval for previous versions.
				// this is because, we are creating a new version of this requirement and 
				// any pending approvals do not matter.
				RequirementUtil.cancelPendingRequirementApprovals(requirementId);
				Calendar cal = Calendar.getInstance();
				String traceDefinition = "Trace made suspect by  "  + actorEmailId + 
				 " at " + cal.getTime() + " by changing Name / Description for Requirement Name : " + newRequirementName;

				// Set all traces to / from this req to suspect. 
				sql = "update gr_traces " +
					" set description = ? , suspect=1 " +
					" where (from_requirement_id = ? or to_requirement_id = ?)" ;
		
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setString(1,traceDefinition);
				prepStmt2.setInt(2, requirementId);
				prepStmt2.setInt(3, requirementId);
				prepStmt2.execute();

				// NOTE : this is a little tricky
				// we need to set the 'traceTo and TraceFrom value for this req , 
				// since the change in Req is triggering a bunch of suspect traces.
				RequirementUtil.updateTraceInfoForRequirement(requirementId);
					
				// however the above action has also triggered suspect traces in reqs
				// that trace to and trace from this req. So we need to get a list 
				// of those reqs and call the same updateTraceInfo method on that.
				sql = "select id from gr_traces " +
					" where (from_requirement_id = ? or to_requirement_id = ?)" ;
		
				prepStmt2 = con.prepareStatement(sql);
				prepStmt2.setInt(1, requirementId);
				prepStmt2.setInt(2, requirementId);
				ResultSet rs2 = prepStmt2.executeQuery();
					
				while (rs2.next()){
					int traceId = rs2.getInt("id");
					RequirementUtil.updateTraceInfoForTrace(traceId, actorEmailId,  databaseType);						
				}

				rs2.close();
				
				// now lets notify all the stake holders that the requirement has changed.
				RequirementUtil.notifyRequirementStakeHolders(requirementId, "newVersion",request, databaseType, newRequirementName, newRequirementDescription);
				
			}
			
			String log = "";
			
			if (!(requirement.getRequirementName().equals(newRequirementName)) ){
				log += "Updated Name : " + newRequirementName + " ; ";
			}
			if (!(requirement.getRequirementDescription().equals(newRequirementDescription)) ){
				log += "Updated Description : " + newRequirementDescription + " ; ";
			}
			if (!(requirement.getRequirementPriority().equals(newRequirementPriority)) ){
				log += "Updated Priority : " + newRequirementPriority + " ; ";
			}
			if (!(requirement.getRequirementOwner().equals(newRequirementOwner)) ){
				log += "Updated Owner : " + newRequirementOwner + " ; ";
			}
			if (!(requirement.getRequirementPctComplete() == newRequirementPctComplete)) {
				log += "Updated Percent Complete : " + newRequirementPctComplete  + " ; ";
			}
			if (!(requirement.getRequirementExternalUrl().equals(newRequirementExternalUrl)) ){
				log += "Updated ExternalURL : " + newRequirementExternalUrl  + " ; ";
			}
			
			if (!log.equals("")){
				RequirementUtil.createRequirementLog(requirementId,log , actorEmailId, databaseType);

			}
		
			// Update the req with the new values.
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "update gr_requirements set name = ? , description = ? ," +
					" priority = ? , " +
					" owner = ?, pct_complete = ? , external_url = ? ," +
					" last_modified_by = ? , last_modified_dt = now() " +
					"where id = ? ";
			}
			else {
				sql = "update gr_requirements set name = ? , description = ? ," +
				" priority = ? , " +
				" owner = ?, pct_complete = ? , external_url = ? ," +
				" last_modified_by = ? , last_modified_dt = sysdate " +
				"where id = ? ";
			}
			PreparedStatement prepStmt2 =  con.prepareStatement(sql);

			if (newRequirementName == null) {
				newRequirementName = "";
			}
			if (newRequirementName.length() > 3999){
				newRequirementName = newRequirementName.substring(0, 3990) + "...";
			}

			
			prepStmt2.setString(1, newRequirementName);
			prepStmt2.setString(2, RequirementUtil.removeWordCrap(newRequirementDescription));

			prepStmt2.setString(3, newRequirementPriority);
			prepStmt2.setString(4, newRequirementOwner);
			prepStmt2.setInt(5, newRequirementPctComplete);
			prepStmt2.setString(6, newRequirementExternalUrl);
			prepStmt2.setString(7, actorEmailId);
				
			prepStmt2.setInt(8, requirementId);
			prepStmt2.execute();

			this.requirementId = requirementId;
			this.requirementName = newRequirementName;
			this.version = newVersion;
			this.requirementDescription = RequirementUtil.removeWordCrap(newRequirementDescription);
			this.requirementPriority = newRequirementPriority;
			this.requirementOwner = newRequirementOwner;
			this.requirementPctComplete = newRequirementPctComplete;
			this.requirementExternalUrl = newRequirementExternalUrl;	

			prepStmt2.close();
			
			
			if (versionChanged) {
				// at this point, lets create an entry in the gr_requirement_version table
				// we need to create a new version, only if the name /desc changed
				// which caused the version number to bump up.
				RequirementUtil.createRequirementVersion(requirementId);
			}
			
			refreshRequirementGlossary();
			

			this.setDaysSinceLastApprovalReminder(databaseType);
			this.setDaysSinceSubmittedForApproval(databaseType);
			
			
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
				
	}

		
	// setters
	
	

	
	public void refreshRequirementGlossary() {
		java.sql.Connection con  = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			// lets see if this project has any requirement types called glossary
			// if not, then we can just return
			
			if (this.projectId == 0 ){
				// there are times when the project id has not been set. so lets make a db call and get it
				sql = "select project_id from gr_requirements where id = ? ";

				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, this.requirementId);
				ResultSet rs = prepStmt.executeQuery();
				
				while (rs.next()){
					this.projectId  = rs.getInt("project_id");
				}
					
			}
			
			sql = "select id from gr_requirement_types " +
				" where project_id  = ? " + 
				" and lower(name) like 'glossary'";
			
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.projectId);
			ResultSet rs = prepStmt.executeQuery();
			int requirementTypeId = 0;
			
			while (rs.next()){
				requirementTypeId = rs.getInt("id");
			}
			
			if (requirementTypeId == 0 ){
				prepStmt.close();
				rs.close();
				return;
			}
			else {
				// NOTE : TODO : If this method of reading all glossary items for every requirement creation
				// doesn't scale, then we may need to consider caching the glossary terms in the project bean
				
				// lets iterate through all the glossary objects in this project. 
			
				// we want unique  glossary items (even if they are repeated)
				HashMap matchingGlossaryObjects = new HashMap();
				sql = " select r.id, r.name " +
						" from gr_requirements r " + 
						" where r.requirement_type_id = ? ";
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, requirementTypeId);
				rs = prepStmt.executeQuery();
				
				String lowerCaseReqName = " " + this.requirementName.toLowerCase() + " ";
				String lowerCaseReqDesc = " " + this.requirementDescription.toLowerCase() + " ";
				
				// lets iterate through all the glossary items defined in this project
				while (rs.next()){
					String requirementId = rs.getString("id");
					// we had situations where a glossery item called 'car' was picked up by a req name like 'carry out'
					// so adding space before and after to designate only full words should match
					String glossaryItem =  " " + rs.getString("name").toLowerCase() + " " ;
					if (
							(lowerCaseReqName.contains( glossaryItem ))
							||
							(lowerCaseReqDesc .contains( glossaryItem  )))
					{
						// we have a glossary match
						matchingGlossaryObjects.put(glossaryItem, requirementId);
					}
				}
				
				// lets iterate through the matchingGlossaryObjects and build a glossaryString
				String glossary = "";
				Iterator i = matchingGlossaryObjects.keySet().iterator();
				while (i.hasNext()){
					String name = (String) i.next();
					String requirementId = (String) matchingGlossaryObjects.get(name);
					glossary += name + ":##:" + requirementId + ":###:";

					
				}
				
				
				prepStmt.close();
				rs.close();
				
				
				if (!glossary.equals("")){

					
					// our glossary has some hits. so , lets set the requirement's glossary to this value.
					sql = " update gr_requirements set glossary = ? where id = ? ";
					
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, glossary);
					prepStmt.setInt(2, this.requirementId);
					prepStmt.execute();
					
					prepStmt.close();
					rs.close();
				}
				this.glossary = glossary;
			}
			
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
				
	}

	
	// This method gets the UDAs for this bean's req from the db and updates the UDA field for this req.
	// It also sets the this.UDA value.
	public void setUserDefinedAttributes (java.sql.Connection con, String databaseType ) {
		try {
			// lets get the UDA values and put them in a string.
			String sql = "";
			if (databaseType.equals("mySQL")){
				
				sql = "SELECT concat(a.name, \":#:\" , ifnull(v.value, '')) \"uda\" " +
					" FROM gr_rt_attributes a left join gr_r_attribute_values v on a.id = v.attribute_id" +
					" where v.requirement_id = ? " +
					" order by a.sort_order";
			}
			else {

				sql = "SELECT a.name ||  ':#:' ||  nvl(v.value,'') \"uda\" " +
					" FROM gr_rt_attributes a left join gr_r_attribute_values v on a.id = v.attribute_id" +
					" where v.requirement_id = ? " +
					" order by a.sort_order";
			}
	
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.requirementId);
			ResultSet rs = prepStmt.executeQuery();
	
			String userDefinedAttributes = "";
			
			while (rs.next()){
				userDefinedAttributes += rs.getString("uda") + ":##:"; 
			}
			
			if (!(userDefinedAttributes.equals(""))){
				// do this only if the uda is not empty. this prevents a null point exception.
				// Drop the last ":##:" from the string.
				userDefinedAttributes = (String) userDefinedAttributes.subSequence(0,userDefinedAttributes.lastIndexOf(":##:"));
				
				
				// update this req's userDefinedAttributes field with this value
				sql = " update gr_requirements set user_defined_attributes = ? where id = ? ";
				
				prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, userDefinedAttributes);
				prepStmt.setInt(2, this.requirementId);
				prepStmt.execute();
				
				prepStmt.close();
				rs.close();

			}
			
			// Now set this bean's attribute to the uda.
			this.userDefinedAttributes = userDefinedAttributes;
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}


	}
	
	
	// getters
	public int getRequirementId(){
		return this.requirementId;
	}
	
	public int getRequirementTypeId () {
		return this.requirementTypeId;
	}
	
	
	public int getFolderId(){
		java.sql.Connection con  = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select folder_id from gr_requirements where id = ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.requirementId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				this.folderId = rs.getInt("folder_id");
			}
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}

		
		
		return (folderId);
	}
	
	public int getProjectId(){
		return this.projectId;
	}

	public int getSourceRequirementId(){
		// we didn't have the bandwith to load source_requirement_id
		// into every requirement. Hence to get source_requirment_id
		// we have to query the db.
		java.sql.Connection con  = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select source_requirement_id from gr_requirements where id = ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.requirementId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				this.sourceRequirementId = rs.getInt("source_requirement_id");
			}
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return this.sourceRequirementId;
	}

	public String getParentFullTag(){
		// we didn't have the bandwith to load parent_full_tag
		// into every requirement. Hence to get parent_full_tag
		// we have to query the db.
		java.sql.Connection con  = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select parent_full_tag from gr_requirements where id = ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.requirementId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				this.parentFullTag = rs.getString("parent_full_tag");
			}
			
			if (this.parentFullTag == null){
				this.parentFullTag = "";
			}
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (this.parentFullTag);
	}
	
	public String getRequirementName(){
		return this.requirementName;
	}
	
	public String getRequirementNameForHTML(){
		String requirementNameForHTML =  this.requirementName;
		if (requirementNameForHTML == null ){
			requirementNameForHTML = "";
		}
		requirementNameForHTML = requirementNameForHTML.replaceAll("\n","<br>");
		return requirementNameForHTML;
	}
	
	
	public String getRequirementDescription () {
		return RequirementUtil.removeWordCrap(this.requirementDescription);
	}

	public String getRequirementDescriptionNoHTML () {
		
		String requirementDesciptionNoHTML = RequirementUtil.removeWordCrap(this.requirementDescription);
			
		if (requirementDescription != null) {
			requirementDesciptionNoHTML = requirementDesciptionNoHTML.replace("<br>","\n");
			
			requirementDesciptionNoHTML = requirementDesciptionNoHTML.replaceAll("\\<.*?>","");
			
			// start with < then any charcaters then new line and any numner of charcaters
			// then new line and any numner of characters (repeat)
			// and >
	
			requirementDesciptionNoHTML = requirementDesciptionNoHTML.replaceAll("\\<.*(\n.*)*.*>","");
			
	 		
			requirementDesciptionNoHTML = requirementDesciptionNoHTML.replaceAll("&nbsp;", " ");
			
			
			requirementDesciptionNoHTML = requirementDesciptionNoHTML.replaceAll("&lt;", " < ");
			
			requirementDesciptionNoHTML = requirementDesciptionNoHTML.replaceAll("&gt;", " > ");
			
		}
		else {
			requirementDesciptionNoHTML = "";
		}
		
		return requirementDesciptionNoHTML;
	}
	
	public String getRequirementDescriptionNoHTMLWithJSoup () {
		String requirementDesciptionNoHTML = "";
		if (this.requirementDescription != null) {
			requirementDesciptionNoHTML = RequirementUtil.removeWordCrap(this.requirementDescription);
			requirementDesciptionNoHTML = Jsoup.parse(requirementDesciptionNoHTML).text();
		}
		return requirementDesciptionNoHTML;
	}
	
	public String getRequirementDescriptionBRToNewLine () {
		String requirementDescriptionBRToNewLine = RequirementUtil.removeWordCrap(this.requirementDescription);
		if (requirementDescription != null) {
			
			// Yeah, I know there are more elegant ways to do this (ignore case, wild cards for white spaces etc..)
			// , but try writing code at 11 in the night as a second job and 
			// dealing with 2 kids. - srt
			requirementDescriptionBRToNewLine  = requirementDescriptionBRToNewLine.replace("<br>","\n");
			requirementDescriptionBRToNewLine  = requirementDescriptionBRToNewLine.replace("<br >","\n");
			
			requirementDescriptionBRToNewLine  = requirementDescriptionBRToNewLine.replace("<br/>","\n");
			requirementDescriptionBRToNewLine  = requirementDescriptionBRToNewLine.replace("<br />","\n");
			
			requirementDescriptionBRToNewLine  = requirementDescriptionBRToNewLine.replace("<BR>","\n");
			requirementDescriptionBRToNewLine  = requirementDescriptionBRToNewLine.replace("<BR >","\n");
			
			requirementDescriptionBRToNewLine  = requirementDescriptionBRToNewLine.replace("<BR/>","\n");
			requirementDescriptionBRToNewLine  = requirementDescriptionBRToNewLine.replace("<BR />","\n");
			
		}
		else {
			requirementDescriptionBRToNewLine  = "";
		}
		return requirementDescriptionBRToNewLine ;
	}
	
	

	public String getRequirementTag () {
		return this.requirementTag;
	}
	
	public String getRequirementFullTag () {
		if (this.requirementFullTag == null){
			// there are some scenarios where the full tag can be null
			// eg : when a requirement bean was created by sending in all the parameters (not in the db, just the bean)
			// in that case, we don't expect the client to send in the requirement_fulltag. 
			// instead of modifying every client bean, we are just modifying the getReqFullTag method.
			java.sql.Connection con = null;
			try {
				javax.naming.InitialContext context = new InitialContext();
				javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
				con = dataSource.getConnection();

				String sql = " select full_tag from gr_requirements where id = ? ";
				
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, this.requirementId);
				ResultSet rs = prepStmt.executeQuery();	
				while (rs.next()){
					this.requirementFullTag = rs.getString("full_tag");
				}
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (con != null) {
					try { con.close(); } catch (Exception e) { ; }
					con = null;
				}
			}	
		}
		return this.requirementFullTag;
		
	}

	public int getVersion() {
		return this.version;
	}


	public String getApprovedByAllDt() {
		if (this.approvedByAllDt == null){
			return "N/A";
		}
		else {
			return this.approvedByAllDt;
		}
	}
	
	//because these can be null, lets assign them an empty string.
	public String getApprovers() {
		if (this.approvers == null){
			this.approvers = "";
		}
		return this.approvers;
	}
	
	public String getColorCodedApprovers(){
		String colorCodedApprovers = "";
		
		String approversString = this.approvers;
		if (approversString == null){
			approversString ="";
		}
		if ((approversString != null) && (approversString.contains(","))){
			String [] approvers = approversString.split(",");
			String color="";
			for (int i=0;i<approvers.length;i++){
				if (approvers[i].contains("(P)")){
					color="purple";
				}
				if (approvers[i].contains("(A)")){
					color="green";
				}
				if (approvers[i].contains("(R)")){
					color="red";
				}
				colorCodedApprovers += "  <font color='" +
					color + "'>" + 
					approvers[i] + 
					"</font>,";
			}
			// drop the last ,
			colorCodedApprovers = (String) colorCodedApprovers.subSequence(0,colorCodedApprovers.lastIndexOf(","));
		}
		if (colorCodedApprovers.equals("")){
			colorCodedApprovers = "N/A";
		}
		return (colorCodedApprovers);

	}
	
	public String getApprovalStatus () {
		if (this.requirementStatus == null ){
			this.requirementStatus = "Draft";
		}
		return this.requirementStatus;
	}
	
	public String getRequirementPriority () {
		return this.requirementPriority ;
	}

	public String getRequirementOwner () {
		return this.requirementOwner ;
	}

	public String getRequirementLockedBy () {
		if (this.requirementLockedBy == null){
			this.requirementLockedBy = "";
		}
		return this.requirementLockedBy ;
	}

	
	public int getRequirementPctComplete () {
		return this.requirementPctComplete ;
	}

	public String getRequirementExternalUrl () {
		if (this.requirementExternalUrl == null){
			this.requirementExternalUrl = "";
		}
		return this.requirementExternalUrl ;
	}
	
	// because these can be null, lets assign them an empty string.
	public String getRequirementTraceTo () {
		if (this.requirementTraceTo == null){
			this.requirementTraceTo = "";
		}
		return this.requirementTraceTo ;
	}
	
	//because these can be null, lets assign them an empty string.
	public String getRequirementTraceFrom () {
		if (this.requirementTraceFrom == null){
			this.requirementTraceFrom = "";
		}
		return this.requirementTraceFrom ;
	}
	
	// Doesn't support external projects
	public ArrayList<String> getRequirementTraceFromArrayList () {
		ArrayList<String> traceFrom = new ArrayList<String>();
	
		if (this.requirementTraceFrom == null){
			this.requirementTraceFrom = "";
		}
		String[] rT = this.getRequirementTraceFrom().split(",");
		for (String t:rT){
			
			if (t.contains("(s)"))
			{
				t = t.replace("(s)","");
			}
			if ((t != null) && (!(t.equals("")))){
				traceFrom.add(this.getProjectShortName()+ ":" + t);
			}
			
		}
		return traceFrom ;
	}
	
	public String getUserDefinedAttributes () {
		if (this.userDefinedAttributes == null){
			this.userDefinedAttributes = "";
		}
		return this.userDefinedAttributes ;
	}
	
	public String getGlossary () {
		if (this.glossary == null){
			this.glossary = "";
		}
		return this.glossary ;
	}
	

	public ArrayList<String> getApproversAndStatus() {
		
		
		ArrayList<String> approversAndStatus = new ArrayList<String>();
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			/*String sql = " select req.name, req.approval_rank, req.approval_type, req.email_id, req.first_name, req.last_name ,  " +
					 "   rah.note, rah.response, rah.response_dt , rah.approved_roles , rah.approver_email_id   " +
					"  from (select r.id, r.version, rl.name, rl.approval_rank, rl.approval_type, u.email_id, u.first_name, u.last_name " + 
					"  from gr_requirements r , gr_folders f, gr_role_privs rp, gr_roles rl, gr_user_roles ur, gr_users u " +      
					  " where r.id =  ?    " + 
					  " and r.folder_id = f.id     " + 
					  " and f.id = rp.folder_id      " + 
					  " and rp.approve_requirement = 1 " +     
					  " and rp.role_id = rl.id     " + 
					 " and rl.id = ur.role_id     " + 
					"  and ur.user_id = u.id     " + 
					"  ) req left join gr_requirement_approval_h rah on " + 
					" 	 ( req.id = rah.requirement_id and req.version = rah.version and req.email_id = rah.approver_email_id) "
					+ " order by 2 , 1";
					  
			*/
			
			String sql = " (select req.name, req.approval_rank, req.approval_type, req.email_id, req.first_name, req.last_name ,  " +
					 "   rah.note, rah.response, rah.response_dt , rah.approved_roles , rah.approver_email_id   " +
					"  from (select r.id, r.version, rl.name, rl.approval_rank, rl.approval_type, u.email_id, u.first_name, u.last_name " + 
					"  from gr_requirements r , gr_folders f, gr_role_privs rp, gr_roles rl, gr_user_roles ur, gr_users u " +      
					  " where r.id =  ?    " + 
					  " and r.folder_id = f.id     " + 
					  " and f.id = rp.folder_id      " + 
					  " and rp.approve_requirement = 1 " +     
					  " and rp.role_id = rl.id     " + 
					 " and rl.id = ur.role_id     " + 
					"  and ur.user_id = u.id     " + 
					"  ) req left join gr_requirement_approval_h rah on " + 
					" 	 ( req.id = rah.requirement_id and req.version = rah.version and req.email_id = rah.approver_email_id)"
					+ ") "
					+ " union "
					+ " (  select req.name, req.approval_rank, req.approval_type, req.email_id, req.first_name, req.last_name ,  " +
					 "   rah.note, rah.response, rah.response_dt , rah.approved_roles , rah.approver_email_id   " +
					"  from (select r.id, r.version, rl.name, dr.approval_rank, rl.approval_type, u.email_id, u.first_name, u.last_name " + 
					"  from gr_dynamic_roles dr, gr_requirements r ,  gr_roles rl, gr_user_roles ur, gr_users u " +      
					  " where dr.requirement_id = ? "
					  + " and dr.requirement_id = r.id "
					  + " and dr.role_id = rl.id "
					  + " and rl.id = ur.role_id      "
					  + " and ur.user_id = u.id     " + 
					"  ) req left join gr_requirement_approval_h rah on " + 
					" 	 ( req.id = rah.requirement_id and req.version = rah.version and req.email_id = rah.approver_email_id) "
					+ " ) "
					+ " order by 2 , 1 "; 
			
			System.out.println("srt sql for getApproversAndStatus is " + sql + " for req id " + this.getRequirementId());
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.getRequirementId());
			prepStmt.setInt(2, this.getRequirementId());
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				
				String roleName = rs.getString("name");
				int approvalRank = rs.getInt("approval_rank");
				String approvalType = rs.getString("approval_type");
				String emailId = rs.getString("email_id");				
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String userName = firstName + " " + lastName;
				String approverEmailId = rs.getString("approver_email_id");
				if (approverEmailId==null){
					// there are situations where some Role members may not be in RAH.
					// for example, a role is of type 'approvalByAny' and some have anot approved. Then the pending approvers
					// get removed. This takes care of not reporting them. as its just noise.
					continue;
				}
				
				String note = rs.getString("note");
				if (note == null) { note = "";}
				String status = "";
				status = rs.getString("response");
				if ((status == null) || (status.equals(""))){
					status = "Pending";
				}
				
				String responseDt = rs.getString("response_dt");
				String approvedRoles = rs.getString("approved_roles");
				
				
				String roleString = roleName + ":##:" + approvalRank + ":##:" + approvalType + ":##:" 
				+ emailId +  ":##:" + userName + ":##:" + status + ":##:"  + note +  ":##:"  + responseDt +  ":##:"  + approvedRoles;
				
				
				approversAndStatus.add(roleString);
				
			}
			
			rs.close();
			prepStmt.close();
			
			/*
			// lets add the dynamic role users
			 sql = " select req.name, req.approval_rank, req.approval_type, req.email_id, req.first_name, req.last_name ,  " +
					 "   rah.note, rah.response, rah.response_dt , rah.approved_roles , rah.approver_email_id   " +
					"  from (select r.id, r.version, rl.name, rl.approval_rank, rl.approval_type, u.email_id, u.first_name, u.last_name " + 
					"  from gr_dynamic_roles dr, gr_requirements r ,  gr_roles rl, gr_user_roles ur, gr_users u " +      
					  " where dr.requirement_id = ? "
					  + " and dr.requirement_id = r.id "
					  + " and dr.role_id = rl.id "
					  + " and rl.id = ur.role_id      "
					  + " and ur.user_id = u.id     " + 
					"  ) req left join gr_requirement_approval_h rah on " + 
					" 	 ( req.id = rah.requirement_id and req.version = rah.version and req.email_id = rah.approver_email_id) "
					+ " order by 2 , 1";
					  
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.getRequirementId());

			rs = prepStmt.executeQuery();
		System.out.println("srt sql for dynamic roles is " + sql);
	
			while (rs.next()){
				
				String roleName = rs.getString("name");
				int approvalRank = rs.getInt("approval_rank");
				String approvalType = rs.getString("approval_type");
				String emailId = rs.getString("email_id");				
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String userName = firstName + " " + lastName;
				String approverEmailId = rs.getString("approver_email_id");
				
				System.out.println("srt dynamic approver is " + emailId);
				if (approverEmailId==null){
					// there are situations where some Role members may not be in RAH.
					// for example, a role is of type 'approvalByAny' and some have anot approved. Then the pending approvers
					// get removed. This takes care of not reporting them. as its just noise.
					continue;
				}
				
				String note = rs.getString("note");
				if (note == null) { note = "";}
				String status = "";
				status = rs.getString("response");
				if ((status == null) || (status.equals(""))){
					status = "Pending";
				}
				
				String responseDt = rs.getString("response_dt");
				String approvedRoles = rs.getString("approved_roles");
				
				
				String roleString = roleName + ":##:" + approvalRank + ":##:" + approvalType + ":##:" 
				+ emailId +  ":##:" + userName + ":##:" + status + ":##:"  + note +  ":##:"  + responseDt +  ":##:"  + approvedRoles;
				
				
				approversAndStatus.add(roleString);
				
			}
			prepStmt.close();
			*/
			con.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}		
		return approversAndStatus;
	}
	
	
public ArrayList<HashMap<String, String>> getApproversRolesForUser(String userEmailId) {
		
		
	ArrayList<HashMap<String, String>>roles = new ArrayList<HashMap<String, String>>();
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = " select req.name, req.approval_rank, req.approval_type, req.email_id, req.first_name, req.last_name ,  " +
					 "   rah.note, rah.response, rah.response_dt , rah.approved_roles   " +
					"  from ( " + 
								" select r.id, r.version, rl.name, rl.approval_rank, rl.approval_type, u.email_id, u.first_name, u.last_name " + 
									"  from gr_requirements r , gr_folders f, gr_role_privs rp, gr_roles rl, gr_user_roles ur, gr_users u " +      
									  " where r.id =  ?    " + 
									  " and r.folder_id = f.id     " + 
									  " and f.id = rp.folder_id      " + 
									  " and rp.approve_requirement = 1 " +     
									  " and rp.role_id = rl.id     " + 
									 " and rl.id = ur.role_id     " + 
									"  and ur.user_id = u.id     " + 
								" union " + 
								" select r.id, r.version, rl.name, dr.approval_rank, rl.approval_type, u.email_id, u.first_name, u.last_name " + 
								"  from gr_requirements r , gr_dynamic_roles dr, gr_roles rl, gr_user_roles ur, gr_users u " +      
								  " where r.id =  ?    " + 
								  " and r.id = dr.requirement_id     " + 
								  " and dr.role_id = rl.id     " + 
								 " and rl.id = ur.role_id     " + 
								"  and ur.user_id = u.id     " + 	
					"  ) req left join gr_requirement_approval_h rah on " + 
					" 	 ( req.id = rah.requirement_id and req.version = rah.version and req.email_id = rah.approver_email_id) "
					+ " order by 2 ";
					  
			
			System.out.println("srt sql for getApproversRolesForUser is " + sql   );
			System.out.println("srt getRequirementId for getApproversRolesForUser is " + this.getRequirementId()   );

			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.getRequirementId());
			prepStmt.setInt(2, this.getRequirementId());
			
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				
				String roleName = rs.getString("name");
				int approvalRank = rs.getInt("approval_rank");
				String approvalType = rs.getString("approval_type");
				String emailId = rs.getString("email_id");	
				
				String approvedRoles = rs.getString("approved_roles");
				
			
				if (approvedRoles == null){approvedRoles = "";}
				
				
				
				if (emailId.equals(userEmailId)){
					String note = rs.getString("note");
					if (note == null) { note = "";}
					String status = "";
					status = rs.getString("response");
					if ((status == null) || (status.equals(""))){
						status = "Pending";
					}
					
					
					
					HashMap<String, String> role  = new HashMap<String, String>();
					role.put("roleName", roleName);
					role.put("approvalRank",Integer.toString(approvalRank));
					role.put("approvalType", approvalType);
					role.put("status", status);
					
					role.put("approvedRoles", approvedRoles);
					
					
					
					
					
					roles.add(role);
				}
			}
			prepStmt.close();
			
			con.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}		
		return roles;
	}



public String getNextRoleToApproveForUser(String userEmailId) {
		
		
	String nextRoleToApprove = "";
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			/*
			  String sql = " select req.name, req.approval_rank, req.approval_type, req.email_id, req.first_name, req.last_name ,  " +
			 
					 "   rah.note, rah.response, rah.response_dt , rah.approved_roles   " +
					 "  from ("
					 		+ 	" select r.id, r.version, rl.name, rl.approval_rank, rl.approval_type, u.email_id, u.first_name, u.last_name " + 
					 			"  from gr_requirements r , gr_folders f, gr_role_privs rp, gr_roles rl, gr_user_roles ur, gr_users u " +      
					 			" where r.id =  ?    " + 
					 			" and r.folder_id = f.id     " + 
					 			" and f.id = rp.folder_id      " + 
					 			" and rp.approve_requirement = 1 " +     
					 			" and rp.role_id = rl.id     " + 
					 			" and rl.id = ur.role_id     " + 
					 			"  and ur.user_id = u.id     " + 
					"  ) req left join gr_requirement_approval_h rah "
					+ " on  ( req.id = rah.requirement_id and req.version = rah.version and req.email_id = rah.approver_email_id) "
					+ " order by 2 ";
			*/
			// lets add dynamic roles to the list of selected approval roles to check
			String sql = " select req.name, req.approval_rank, req.approval_type, req.email_id, req.first_name, req.last_name ,  " +
					 "   rah.note, rah.response, rah.response_dt , rah.approved_roles   " +
					 "  from ("
					 		+ 	" select r.id, r.version, rl.name, rl.approval_rank, rl.approval_type, u.email_id, u.first_name, u.last_name " + 
					 			"  from gr_requirements r , gr_folders f, gr_role_privs rp, gr_roles rl, gr_user_roles ur, gr_users u " +      
					 			" where r.id =  ?    " + 
					 			" and r.folder_id = f.id     " + 
					 			" and f.id = rp.folder_id      " + 
					 			" and rp.approve_requirement = 1 " +     
					 			" and rp.role_id = rl.id     " + 
					 			" and rl.id = ur.role_id     " + 
					 			"  and ur.user_id = u.id     " +
					 			" union " + 
								" select r.id, r.version, rl.name, dr.approval_rank, rl.approval_type, u.email_id, u.first_name, u.last_name " +    
					 			 " from gr_dynamic_roles dr, gr_requirements r, gr_roles rl, gr_user_roles ur, gr_users u " + 
								 " where dr.requirement_id =  ? " + 
								 " and dr.requirement_id = r.id   " +  
								 " and dr.role_id = rl.id   " + 
					 			 " and dr.role_id = ur.role_id " + 
								 " and ur.user_id = u.id  " + 
					"  ) req left join gr_requirement_approval_h rah "
					+ " on  ( req.id = rah.requirement_id and req.version = rah.version and req.email_id = rah.approver_email_id) "
					+ " order by 2 ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.getRequirementId());
			prepStmt.setInt(2, this.getRequirementId());
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				
				String roleName = rs.getString("name");
				int approvalRank = rs.getInt("approval_rank");
				String approvalType = rs.getString("approval_type");
				String emailId = rs.getString("email_id");	
				
				String approvedRoles = rs.getString("approved_roles");
				if (approvedRoles == null){approvedRoles = "";};
				
				if (emailId.equals(userEmailId)){
					if (approvedRoles.contains(roleName)){
						continue;
					}
					else{
						// this is the next role to approve for this user on this req. 
						nextRoleToApprove = roleName;
						break;
					}
				}
				else {
					System.out.println("srt skip as it belongs to some other user : " + emailId);
				}
			
			}
			prepStmt.close();
			
			con.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}	
		return nextRoleToApprove;
	}
	
	// drops the :#: and :##: from the user Defined attribs and pretties up
	public HashMap<String, String> getUserDefinedAttributesHashMap () {
		String userDefinedAttributesFormatted = this.userDefinedAttributes ;
		HashMap<String, String> aMap = new HashMap<String, String>();
		
		if ((userDefinedAttributesFormatted != null) && (userDefinedAttributesFormatted.contains(":##:"))){
			// lets split it into rows.
			String [] udaArray = userDefinedAttributesFormatted.split(":##:");
			for (int i=0; i< udaArray.length; i++){
				String udaRow = udaArray[i];
				if ((udaRow != null) && (udaRow.contains(":#:"))){
					String [] udaPair = udaRow.split(":#:");
					// if attrib value is null or empty string, then ignore this.
					
					String attribName = "";
					String attribValue = "";
					if (udaPair.length > 0) {
						attribName = udaPair[0];
						if (attribName == null){attribName = "";}
					}
					if (udaPair.length > 1) {
						attribValue = udaPair[1];
						if (attribValue == null){attribValue = "";}
					}
					aMap.put(attribName, attribValue);
				}
			}
		}
		return aMap;
	}
	
	public HashMap<String, String> getUserDefinedAttributesHashMapLowerCaseKey () {
		String userDefinedAttributesFormatted = this.userDefinedAttributes ;
		HashMap<String, String> aMap = new HashMap<String, String>();
		
		if ((userDefinedAttributesFormatted != null) && (userDefinedAttributesFormatted.contains(":##:"))){
			// lets split it into rows.
			String [] udaArray = userDefinedAttributesFormatted.split(":##:");
			for (int i=0; i< udaArray.length; i++){
				String udaRow = udaArray[i];
				if ((udaRow != null) && (udaRow.contains(":#:"))){
					String [] udaPair = udaRow.split(":#:");
					// if attrib value is null or empty string, then ignore this.
					
					String attribName = "";
					String attribValue = "";
					if (udaPair.length > 0) {
						attribName = udaPair[0];
						if (attribName == null){attribName = "";}
					}
					if (udaPair.length > 1) {
						attribValue = udaPair[1];
						if (attribValue == null){attribValue = "";}
					}
					aMap.put(attribName.toLowerCase().trim(), attribValue);
				}
			}
		}
		return aMap;
	}
	
	// drops the :#: and :##: from the user Defined attribs and pretties up
	public String getUserDefinedAttributesFormatted (String formatType) {
		String userDefinedAttributesFormatted = this.userDefinedAttributes ;
		
		if (formatType.equals("HTMLNONEMPTY")){
			String output = "<table border='1' >";
			if ((userDefinedAttributesFormatted != null) && (userDefinedAttributesFormatted.contains(":##:"))){
				// lets split it into rows.
				String [] udaArray = userDefinedAttributesFormatted.split(":##:");
				for (int i=0; i< udaArray.length; i++){
					String udaRow = udaArray[i];
					if ((udaRow != null) && (udaRow.contains(":#:"))){
						String [] udaPair = udaRow.split(":#:");
						// if attrib value is null or empty string, then ignore this.
						
						String attribName = "";
						String attribValue = "";
						if (udaPair.length > 0) {
							attribName = udaPair[0];
						}
						if (udaPair.length > 1) {
							attribValue = udaPair[1];
						}
						
						
						if ((attribValue != null) && !(attribValue.equals(""))){
							output += " <tr> <td style='padding:10px'> <span class='normalText'> " + attribName +  
									" </span> </td><td><span class='normalText'>" + 
									attribValue  +  "</span></td></tr>";
						}
					}
					
				}
			}
			output += "</table>";
			return (output);
		}
		
		if (formatType.equals("HTML")){
			if ((userDefinedAttributesFormatted != null) && (userDefinedAttributesFormatted.contains(":##:"))){
				userDefinedAttributesFormatted = userDefinedAttributesFormatted.replace(":##:", "<br>");
			}
		}
		if (formatType.equals("newLine")){
			if ((userDefinedAttributesFormatted != null) && (userDefinedAttributesFormatted.contains(":##:"))){
				userDefinedAttributesFormatted = userDefinedAttributesFormatted.replace(":##:", "\n");
			}
		}
		if (formatType.equals("semiColon")){
			if ((userDefinedAttributesFormatted != null) && (userDefinedAttributesFormatted.contains(":##:"))){
				userDefinedAttributesFormatted = userDefinedAttributesFormatted.replace(":##:", "; ");
			}
		}
		
		
		if ((userDefinedAttributesFormatted != null) && (userDefinedAttributesFormatted.contains(":#:"))){
			userDefinedAttributesFormatted = userDefinedAttributesFormatted.replace(":#:", " = ");
		}
		return userDefinedAttributesFormatted;
	}
	
	public ArrayList getUserDefinedAttributesArrayList(){
		return RequirementUtil.getAttributeValuesInRequirement(this.requirementId);
	}
	
	
	
	public ArrayList<Role> getDynamicApprovalRoles () {

		ArrayList<Role> dynamicRoles = new ArrayList<Role>();
	 	
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = " select r.id, r.project_id, r.name , r.description, r.approval_type, dr.approval_rank " +
					" from gr_dynamic_roles dr, gr_roles r  " +
					" where dr.requirement_id = ?"
					+ " and dr.role_id = r.id  ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.getRequirementId());
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				int roleId = rs.getInt("id");
				String name = rs.getString("name");
				int projectId = rs.getInt("project_id");
				String description = rs.getString("description");
				String approvalType = rs.getString("approval_type");
				int approvalRank = rs.getInt("approval_rank");
				
				Role role = new Role( roleId,  projectId,  name,  description, approvalType, approvalRank);
				dynamicRoles.add(role);
			}
			prepStmt.close();
			
			con.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (dynamicRoles);
	}
	
	public String getTestingStatus () {
			return this.testingStatus;
	}
	
	public int getDeleted () {
		return this.deleted;
	}

	public String getFolderPath () {
		return this.folderPath;
	}
	public String getCreatedBy () {
		return this.createdBy;
	}
	
	public String getCreatedDt () {
		return this.createdDt;
	}
	
	public String getLastModifiedBy () {
		return this.lastModifiedBy;
	}
	
	

	public void setLastModifiedDt (String newLastModifiedDt) {
		this.lastModifiedDt = newLastModifiedDt;
	}
	
	public String getLastModifiedDt () {
		
		if (lastModifiedDt == null){
			lastModifiedDt = "";
		}
		
		return this.lastModifiedDt;
		
	}
	
	//public Date getLastModifiedDt () {
	//	return this.lastModifiedDt;
	//}
	
	public String getRequirementTypeName () {
		return this.requirementTypeName;
	}
	
	public ArrayList getRequirementVersions (String databaseType){
		ArrayList requirementVersion = RequirementUtil.getRequirementVersions(this.requirementId,  databaseType);
		return requirementVersion;
	}
	
	public ArrayList getRequirementComments ( String databaseType){
		ArrayList requirementComments = RequirementUtil.getRequirementComments(this.requirementId,  databaseType);
		return requirementComments;
	}
	
	public ArrayList<Role> getStackedApprovalRoles (){
		ArrayList<Role> approvalRoles = RequirementUtil.getStackedApprovalRoles(this);
		return approvalRoles;
	}
	
	public String getRequirementCommentsTable ( String databaseType){
		ArrayList requirementComments = RequirementUtil.getRequirementComments(this.requirementId,  databaseType);
		String commentsTable  = "<table class='table table-striped table-bordered'>";
		
		Iterator rC = requirementComments.iterator();
		while (rC.hasNext()){
			Comment comment = (Comment) rC.next();
			commentsTable += "<tr>" + "<td>" + comment.getCommenterEmailId() + "</td>"
				+"<td>"  + comment.getCommentDate() + "</td></tr>"
				+ "<tr><td colspan='2'>" + comment.getComment_note() + "</td></tr>"  ;
		}
		
		commentsTable += "</table>";
		
		return commentsTable;
	}
	

	public String getRequirementCommentsTable2 ( String databaseType){
		ArrayList requirementComments = RequirementUtil.getRequirementComments(this.requirementId,  databaseType);
		String commentsTable  = "<table class='table table-striped table-bordered'>";
		
		Iterator rC = requirementComments.iterator();
		while (rC.hasNext()){
			Comment comment = (Comment) rC.next();
			commentsTable += "<tr>" + "<td><span class='normalText'>" + comment.getCommenterEmailId() + "<br>"  + comment.getCommentDate() + "<br>" 
			+ comment.getComment_note() + "</span></td></tr>"  ;
		}
		
		commentsTable += "</table>";
		
		return commentsTable;
	}
	
	public String getRequirementCommentsString ( String databaseType){
		ArrayList requirementComments = RequirementUtil.getRequirementComments(this.requirementId,  databaseType);
		String commentsTable  = "";
		
		Iterator rC = requirementComments.iterator();
		while (rC.hasNext()){
			Comment comment = (Comment) rC.next();
			commentsTable +=  comment.getCommenterEmailId() + "  ::  " + comment.getCommentDate() + "   ::   " 
					+ comment.getComment_note() + "\n"  ;
		}
		
		commentsTable += "  ";
		
		return commentsTable;
	}
	public int getRequirementCommentsCount ( String databaseType){
		int requirementCommentsCount = RequirementUtil.getRequirementCommentsCount(this.requirementId,  databaseType);
		return requirementCommentsCount;
	}	
	// returns a list of trace objects for all the traces from this requirement object.
	public ArrayList getRequirementTraceToObjects(){
		ArrayList traces = RequirementUtil.getTraceToObjects(this.requirementId);
		return traces;
	}
	
	// returns a list of trace objects for all the traces to this requirement object.
	// i. list of traces that come from other requiremetns to the input parameter requirement. 
	public ArrayList getRequirementTraceFromObjects(){
		ArrayList traces = RequirementUtil.getTraceFromObjects(this.requirementId);
		return traces;
	}
	
	// returns an ArrayList of baselines that this requirement belongs to
	public ArrayList getRequirementBaselines(String databaseType){
		ArrayList baselines = RequirementUtil.getRequirementBaselines(this.requirementId, databaseType);
		return baselines;
	}

	// returns an arraylist of Immediate Child Requirements for this Requirement
	public ArrayList getImmediateChildRequirements(String databaseType){
		ArrayList childRequirements = RequirementUtil.getImmediateChildRequirements(this.projectId, this.requirementFullTag,databaseType);
		return childRequirements;
	}

	
	// returns a concatenated string of all the baselines that this requirement belongs to.
	public String getRequirementBaselineString(String  databaseType) {
		String baselineString = RequirementUtil.getRequirementBaselineString(this.requirementId,  databaseType);
		return baselineString;
	}

	// returns an arraylist of Requriements that are in the upstream of this requirement
	public ArrayList getUpStreamCIARequirements(SecurityProfile securityProfile, int cIADepth, int maxResults, String databaseType){
		ArrayList cIARequirements = RequirementUtil.getUpStreamCIARequirements(securityProfile,
			this.requirementId, cIADepth, maxResults, databaseType);
		return cIARequirements;
	}
	
	// returns an arraylist of Requriements that are in the upstream of this requirement
	public ArrayList getDownStreamCIARequirements(SecurityProfile securityProfile, int cIADepth, int maxResults, String databaseType){
		ArrayList cIARequirements = RequirementUtil.getDownStreamCIARequirements(securityProfile,
				this.requirementId, cIADepth, maxResults, databaseType);
		return cIARequirements;
	}
	
	// Retuns an arraylist of attachments for this requirement
	public ArrayList getRequirementAttachments(String databaseType) {

		ArrayList requirementAttachments = new ArrayList();
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " select id , requirement_id, file_name, file_path, title, " + 
					" created_by, date_format(created_dt , '%d %M %Y %r ') \"created_dt\" " +
					" from gr_requirement_attachments  " +
					" where requirement_id = ? ";
			}
			else {
				sql = " select id , requirement_id, file_name, file_path, title, " + 
				" created_by, to_char(created_dt , 'DD MON YYYY') \"created_dt\" " +
				" from gr_requirement_attachments  " +
				" where requirement_id = ? ";
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.getRequirementId());
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				int requirementAttachmentId = rs.getInt("id");
				int requirementId = rs.getInt("requirement_id");
				String fileName = rs.getString("file_name");
				String filePath = rs.getString("file_path");
				String title = rs.getString("title");
				String createdBy = rs.getString("created_by");				
				String createdDt = rs.getString("created_dt");
				
				RequirementAttachment requirementAttachment = new RequirementAttachment(requirementAttachmentId,
					requirementId, fileName, filePath, title, createdBy, createdDt);
				requirementAttachments.add(requirementAttachment);
			}
			prepStmt.close();
			
			con.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (requirementAttachments);
	}

	// Retuns an arraylist of attachments for this requirement
	public ArrayList getRequirementApprovers() {

		ArrayList approvers = new ArrayList();
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql =  " select u.email_id, u.first_name, u.last_name " +
				"		from gr_requirements r, gr_role_privs rp, gr_user_roles ur, gr_users u " +
				" 		where r.id = ? " +
				"		and r.folder_id = rp.folder_id " +
				"		and rp.approve_requirement = 1 " +
				" 		and rp.role_id = ur.role_id " +
				"		and ur.user_id = u.id"  ;
		
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.getRequirementId());
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				String emailId = rs.getString("email_id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				approvers.add(emailId + ":##:" + firstName + ":##:" + lastName);
			}
			prepStmt.close();
			
			con.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (approvers);
	}
	
	
	public ArrayList<String> getRequirementApproverActions() {

		ArrayList<String> approverAction = new ArrayList();
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql =  " select approver_email_id, response, ifnull(note, \" \") \"note\", "
					+ " ifnull(response_dt, \" \") \"response_dt\"  " +
				" from  gr_requirement_approval_h rah  " +
				 " 		where rah.requirement_id = ?  and rah.version = ? " +
				"		order by rah.response_dt desc ";
		
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.getRequirementId());
			prepStmt.setInt(2, this.getVersion());
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				String approverEmailId = rs.getString("approver_email_id");
				String response = rs.getString("response");
				String note = rs.getString("note");
				String responseDt = rs.getString("response_dt");
				if (responseDt == null) {responseDt = "" ;}
				
				approverAction.add(approverEmailId + ":##:" + response + ":##:" + note +  ":##:" + responseDt);
			}
			prepStmt.close();
			
			con.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (approverAction);
	}
	
	// This is only used for TR requirement types 
	// where, the 'testingStatus' value is the value of the custom attribute 'Testing Status'
	// that the user has set. 

	// sets the testing status in the db to the value being sent.
	public void setTestingStatus(String testingStatus, String actorEmailId, String databaseType) {

		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " update gr_requirements set testing_status = ?, last_modified_dt = now() " +
					" where id = ? ";
			}
			else {
				sql = " update gr_requirements set testing_status = ?, last_modified_dt = sysdate " +
				" where id = ? ";
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1,testingStatus);
			prepStmt.setInt(2, requirementId);
			prepStmt.execute();
			
			prepStmt.close();			

			String log = " Changed the manual testing status to  " + testingStatus;
			RequirementUtil.createRequirementLog(this.requirementId,log, actorEmailId, databaseType);

			// If this is TR type requirement, and testing status is set to Pass, then percent complete should be set to 100
			// else 0.
			if (this.requirementFullTag.startsWith("TR-")){
				sql = " update gr_requirements set pct_complete = ?  " +
					" where id = ? ";
				prepStmt = con.prepareStatement(sql);
				int newPctComplete = 0;
				if (testingStatus.equals("Pass")){
					// if passed, then it should be set to 100
					newPctComplete  = 100;
				}
				else {
					// if pending or fail, then percent complete is 0
					newPctComplete = 0;
				}
	
				prepStmt.setInt(1, newPctComplete);
				prepStmt.setInt(2, requirementId);
				prepStmt.execute();
				
				prepStmt.close();			
			

				log = " Changed Percent Complete  to " + newPctComplete + " when testing status changed to " + testingStatus;
				RequirementUtil.createRequirementLog(this.requirementId,log, actorEmailId, databaseType);

			}
			
			con.close();
			
			this.testingStatus = testingStatus;

		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		
	}

	
	// get the value for an attribute for this requirement.
	public String getAttributeValue(int rTAttributeId) {

		String attributeValue = "";
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = " select value from gr_r_attribute_values  " +
					" where requirement_id = ?  and attribute_id =  ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.requirementId);
			prepStmt.setInt(2, rTAttributeId);
			ResultSet rs = prepStmt.executeQuery();			
			while (rs.next()) {
				attributeValue = rs.getString("value");
			}
			prepStmt.close();			
			con.close();
			
			
		
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		if (attributeValue == null){
			attributeValue = "";
		}
		return (attributeValue);
	}

	// gets the attribute values based on the attribute label
	public String getAttributeValue(String rTAttributeLabel) {
		RTAttribute rTAttribute = new RTAttribute(this.requirementTypeId, rTAttributeLabel);
		String attributeValue = this.getAttributeValue(rTAttribute.getAttributeId());
		if (attributeValue == null){
			attributeValue  = "";
		}
		return (attributeValue);
	}

	public String getAttributeValueFromUDA(String rTAttributeLabel) {
		String attributeValue = "";
		// lets split the UDA
		// go through each attrib label / value till we find a match
		// and return the value
		if (this.userDefinedAttributes == null){this.userDefinedAttributes = "";}
		String[] attribs =  this.userDefinedAttributes.split(":##:");
		for (int k=0; k<attribs.length; k++) {
			String[] attrib = attribs[k].split(":#:");
			
			// To avoid a array out of bounds exception where the attrib value wasn't filled in
			
			if (attrib.length ==2){
				if (attrib[0].trim().equals(rTAttributeLabel)){
					attributeValue = attrib[1];
				}
			}
		}
		
		
		return (attributeValue);
	}


	
	// we noticed a situation where a db entry for attribute label disappeared in one instance
	// March 16 2017 Angel Mediquire
	// so we are writing this method to make a db entry for attribute value from UDA
	public String setAttributeLabelFromUDA(int rTAttributeId, String attributeValue, String emailId) {

		String projectShortName = "";
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = "insert into gr_r_attribute_values (requirement_id, attribute_id, value, " +
					" created_by, created_dt, last_modified_by, last_modified_dt)" +
					" values ( ? , ? , ? ," +
					" ?, now(), ?, now() )";

			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,this.requirementId);
			prepStmt.setInt(2, rTAttributeId);
			prepStmt.setString(3, attributeValue);
			prepStmt.setString(4, emailId );
			prepStmt.setString(5, emailId );
			prepStmt.execute();
			prepStmt.close();
			con.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (projectShortName);
	}

	// returns the short name of the project.
	public String getProjectShortName() {

		String projectShortName = "";
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = " select short_name from gr_projects where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.projectId);
			ResultSet rs = prepStmt.executeQuery();			
			while (rs.next()) {
				projectShortName = rs.getString("short_name");
			}
			prepStmt.close();			
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return (projectShortName);
	}

	
	public void setSourceRequirementId(int sourceRequirementId){
		// we didn't have the bandwith to set  source_requirement_id
		// into every requirement. Hence to get source_requirment_id
		// we have to query the db.
		java.sql.Connection con  = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "update gr_requirements " +
				" set source_requirement_id = ? " +
				" where id = ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sourceRequirementId);
			prepStmt.setInt(2, this.requirementId);
			prepStmt.execute();
			prepStmt.close();
			
			this.sourceRequirementId = sourceRequirementId;
			
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
	}

	
	// sets the value for an attribute for this requirement.
	public void setCustomAttributeValue(int rTAttributeId, String attributeValue, User user, String databaseType) {

		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// first lets find if an this custom attribute already exists.
			// if it exists, then we do an update, else insert.
			
			String sql = " select count(*) \"matches\" from gr_r_attribute_values  " +
					" where requirement_id = ?  and attribute_id =  ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.requirementId);
			prepStmt.setInt(2, rTAttributeId);
			ResultSet rs = prepStmt.executeQuery();	
			int matches = 0;
			while (rs.next()) {
				matches = rs.getInt("matches");
			}
			prepStmt.close();			
			rs.close();
			
			if (matches > 0){
				// this attribute exists , lets do an update.
				sql = "update gr_r_attribute_values " +
					" set value = ? " +
					" where requirement_id = ? " +
					" and attribute_id = ? ";
				prepStmt.close();
				prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, attributeValue);
				prepStmt.setInt(2, this.requirementId);
				prepStmt.setInt(3, rTAttributeId);
				prepStmt.execute();
				prepStmt.close();
			}
			else {
				// this attribute does not exist. so lets create it.
				if (databaseType.equals("mySQL")){
					sql = "insert into gr_r_attribute_values (requirement_id, attribute_id, value, " +
						" created_by, created_dt, last_modified_by, last_modified_dt)" +
						" values ( ? , ? , ? ," +
						" ?, now(), ?, now() )";
				}
				else {
					sql = "insert into gr_r_attribute_values (requirement_id, attribute_id, value, " +
					" created_by, created_dt, last_modified_by, last_modified_dt)" +
					" values ( ? , ? , ? ," +
					" ?, sysdate, ?, sysdate )";
				}
				prepStmt.close();
				prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1,this.requirementId);
				prepStmt.setInt(2, rTAttributeId);
				prepStmt.setString(3, attributeValue);
				prepStmt.setString(4, user.getEmailId() );
				prepStmt.setString(5, user.getEmailId() );
				prepStmt.execute();
				prepStmt.close();
			}
			
			// since the user defined attribs have changed, let set the  'userDefinedAttribs' field in the req.
			this.setUserDefinedAttributes(con,  databaseType);
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
	}

	
	// sets the value for an attribute for this requirement.
	public void setOwner(HttpServletRequest request, String emailId , User user, String databaseType, String mailHost,
			String transportProtocol, String smtpAuth, String smtpPort, String smtpSocketFactoryPort, String emailUserId, String emailPassword) {

		java.sql.Connection con = null;
		try {
			
			String oldOwnerEmailId = this.requirementOwner;
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " update gr_requirements " +
				" set owner = ?   " +
				" where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, emailId);
			prepStmt.setInt(2, this.requirementId);
			prepStmt.execute();	
			
			con.close();
			
			RequirementUtil.createRequirementLog(this.requirementId,"Setting Requirement Owner to : " + emailId , user.getEmailId() ,databaseType);
			RequirementUtil.notifyNewOwner(user, request, this.requirementId, emailId, this.getProjectShortName(),  this.requirementFullTag, this.requirementName, 
					mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword );
			
			RequirementUtil.notifyOldOwner(user, request, this.requirementId, oldOwnerEmailId, emailId, this.getProjectShortName(),  this.requirementFullTag, this.requirementName, 
					mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword );
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
	}

	public void setPercentComplete(int pctComplete , User user, String databaseType) {

		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " update gr_requirements " +
				" set pct_complete = ?   " +
				" where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, pctComplete);
			prepStmt.setInt(2, this.requirementId);
			prepStmt.execute();	
			
			RequirementUtil.createRequirementLog(this.requirementId,"Setting Requirement Completed to " + pctComplete + " %", user.getEmailId() ,databaseType);
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
	}

	
	// sets the value for an Locked by for this requirement.
	public void setLockedBy(String emailId, String databaseType) {

		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " update gr_requirements " +
				" set locked_by = ?   " +
				" where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, emailId);
			prepStmt.setInt(2, this.requirementId);
			prepStmt.execute();	
			
			
			// Lets make a log entry for this action.
			RequirementUtil.createRequirementLog(this.requirementId,"Locked Requirement", emailId,databaseType);
			
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
	}

	// we want to clear the lock on this requirement.
	public void setUnlockedBy(String unlockedBy, String databaseType) {

		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " update gr_requirements " +
				" set locked_by = ?   " +
				" where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, "");
			prepStmt.setInt(2, this.requirementId);
			prepStmt.execute();	
			
			
			// Lets make a log entry for this action.
			RequirementUtil.createRequirementLog(this.requirementId,"Unlocked Requirement", unlockedBy,databaseType);
			
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
	}
	// sets all the sensitive info about this req to empty.
	// usually called when a user sees this req as part of a 
	// trace tree.
	public void redact () {
		this.requirementName = "No READ Permission";
		this.requirementDescription = "No READ Permission";
		this.approvedByAllDt  = "";
		this.approvers  = "";
		this.requirementStatus = "";
		this.requirementPriority = "";
		this.requirementOwner = "";
		this.requirementLockedBy = "";
		this.requirementPctComplete = 0;
		this.requirementExternalUrl = "";
		this.requirementTraceTo = "";
		this.requirementTraceFrom = "";
		this.userDefinedAttributes = "";
		this.testingStatus = "";
		this.folderPath = "";
		this.createdBy = "";
		this.createdDt = "";
		this.lastModifiedBy = "";
		// requirement type name can be derived from the req tag (eg BR-221 is a Business Req)
		//this.requirementTypeName = "";
	}
	
	public String getElement(String elementLabel){
		String returnValue = "";
		if (elementLabel.trim().toLowerCase().equals("name")){
			returnValue = this.requirementName;
		}
		else if (elementLabel.trim().toLowerCase().equals("description")){
			returnValue = this.requirementDescription;
		}
		else if (elementLabel.trim().toLowerCase().equals("fulltag")){
			returnValue = this.requirementFullTag;
		}
		else if (elementLabel.trim().toLowerCase().equals("version")){
			returnValue = Integer.toString(this.version);
		}
		else if (elementLabel.trim().toLowerCase().equals("approvedbyalldt")){
			returnValue = this.approvedByAllDt;
		}
		else if (elementLabel.trim().toLowerCase().equals("approvers")){
			returnValue = this.approvers;
		}
		else if (elementLabel.trim().toLowerCase().equals("status")){
			returnValue = this.requirementStatus;
		}
		else if (elementLabel.trim().toLowerCase().equals("priority")){
			returnValue = this.requirementPriority;
		}
		else if (elementLabel.trim().toLowerCase().equals("owner")){
			returnValue = this.requirementOwner;
		}
		else if (elementLabel.trim().toLowerCase().equals("pctcomplete")){ 
			returnValue = Integer.toString(this.requirementPctComplete);
		}
		else if (elementLabel.trim().toLowerCase().equals("externalurl")){
			returnValue = this.requirementExternalUrl;
		}
		else if (elementLabel.trim().toLowerCase().equals("traceto")){
			returnValue = this.requirementTraceTo;
		}
		else if (elementLabel.trim().toLowerCase().equals("tracefrom")){
			returnValue = this.requirementTraceFrom;
		}
		else if (elementLabel.trim().toLowerCase().equals("testingstatus")){
			returnValue = this.testingStatus;
		}
		else if (elementLabel.trim().toLowerCase().equals("folderpath")){
			returnValue = this.folderPath;
		}
		else {
			// this must be a custom attribute.
			String [] attribs = this.userDefinedAttributes.split(":##:");
			for (int i=0; i<attribs.length; i++){
				String[] valuePair = attribs[i].split(":#:");
				if (valuePair.length > 1) {
					if (valuePair[0].trim().toLowerCase().equals(elementLabel.trim().toLowerCase())){
						returnValue = valuePair[1];
					}
				}
			}
		}
		return returnValue;
	}

	// returns prevVersion = prevVersionName + ":##X##:" + prevVersionDescription + ":##X##:" + prevVersionUDA;
	public String getAPreviousVersion(int versionId) {
		String prevVersion = RequirementUtil.getAPreviousVersion(requirementId, versionId);
		return prevVersion ;
	}


	public void setDaysSinceSubmittedForApproval( String databaseType){
		daysSinceSubmittedForApproval = RequirementUtil.daysSinceSubmittedForApproval(requirementId, databaseType);
	}

	public void setDaysSinceSubmittedForApproval( int setValue){
		daysSinceSubmittedForApproval = setValue;
	}
	
	public int getDaysSinceSubmittedForApproval( ){
		return daysSinceSubmittedForApproval;
	}



	public void setDaysSinceLastApprovalReminder( String databaseType){
		daysSinceLastApprovalReminder = RequirementUtil.daysSinceLastApprovalReminder(requirementId, databaseType);
	}

	public void setDaysSinceLastApprovalReminder( int setValue){
		daysSinceLastApprovalReminder = setValue;
	}

	public int getDaysSinceLastApprovalReminder(){
		return daysSinceLastApprovalReminder;
	}

	public void setFolderPath( String folderPath ){
		this.folderPath = folderPath;
	}

	public void setFolderId( int folderId){
		this.folderId = folderId;
	}

	
	public Role getNextRoleToApprove() {
		Role nextRole = RequirementUtil.getNextRoleToApprove(this);
		return nextRole ;
	}
	
	
	
	
	
	public void removeAllMessagesAboutARequirementApproval(java.sql.Connection con ) {
		RequirementUtil.removeAllMessagesAboutARequirementApproval(con, this);
		return  ;
	}
	
	public void removeAllMessagesAboutARequirementApprovalForAUser(java.sql.Connection con , User user ) {
		RequirementUtil.removeAllMessagesAboutARequirementApprovalForAUser(con, this, user );
		return  ;
	}
	
	public void setRAHApproverListForARole(Role nextRole) {
		RequirementUtil.setRAHApproverListForARole(this,nextRole );
		return  ;
	}
	

	


	
	public String getDecisionByRole(Role role){
		String decision = RequirementUtil.getDecisionByRole(this, role);
		return decision;
	}
	
	
	public String getDecisionByRoleWithoutRefresh(Role role){
		String decision = RequirementUtil.getDecisionByRole(this, role);
		return decision;
	}
	
	
	public int getDaysSinceCreated() {

		java.sql.Connection con = null;
		int age = 0;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = " select datediff(curdate(), created_dt ) 'age' " +
				" from gr_requirements   " +
				" where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.requirementId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				age = rs.getInt("age");
			}
			
			
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}
		return(age);
	}
}
