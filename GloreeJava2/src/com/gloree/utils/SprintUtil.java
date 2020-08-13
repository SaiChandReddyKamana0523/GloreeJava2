package com.gloree.utils;

//GloreeJava2

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;

import com.gloree.beans.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;

public class SprintUtil {
	//
	// This class is used to do the Agile Scrum related tasks
	
	

	public static String addRequirementsToSprint(Project project, Sprint sprint,  String requirementsToAdd, SecurityProfile securityProfile, 
		String databaseType) {

		String status = "";
		try {

			User user = securityProfile.getUser();
			Calendar cal = Calendar.getInstance();
			


			// if requirementsToAdd has a space in it drop it.
			if ((requirementsToAdd != null) && (requirementsToAdd.contains(" "))) {
				requirementsToAdd = requirementsToAdd.replace(" ", "");
			}
			
		
			
						
			// split requirementsToAdd, for each entry there, get the req id, and then
			// add the task to the sprint
			if (!(requirementsToAdd.equals(""))){
				String[] requirements = requirementsToAdd.split(",");
				for (int i = 0; i < requirements.length; i++) {

							
					int requirementId =  RequirementUtil.getRequirementIdFromTag(project, requirements[i],  databaseType);					

					
					
					
					
					
					if (requirementId != 0){
					
						Requirement requirement = new Requirement(requirementId,  databaseType);
						RequirementType requrirementType = new RequirementType(requirement.getRequirementTypeId());
						if (requrirementType.getRequirementTypeEnableAgileScrum() == 0 ) {
							// this requirement type is not enabled for Agile, so can not be added. 
							status += "<br>The Requirement Type <b>'" + requrirementType.getRequirementTypeName()  + "'</b> is not enabled for Agile. Go to the Administration module and enable it."  ;
							continue;
						}
						

						if (requirements[i].contains(":")){
							// the user is trying to add a project in an external project to this sprint. Not allowed
							status += "<br>This Requirement is in an external project : " + requirement.getProjectShortName() + ":" + requirement.getRequirementFullTag();
							continue;
						}
						
						             
						if (!( securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
				    			+ requirement.getFolderId()))){
							status += "<br>You do not have permissions to update this requirement : " + requirement.getRequirementFullTag();
							continue;							
						}
						
						// if this requirement is locked and its locked by someone other than this user, then all updates to this req are disabled.
						if (
							(!(requirement.getRequirementLockedBy().equals("")))
							&&
							(!(requirement.getRequirementLockedBy().equals(user.getEmailId())))
							){
							
							status += "<br>This requirement : " + requirement.getRequirementFullTag() + " is locked by " + requirement.getRequirementLockedBy();
							
							continue;
						}
						// lets get the Req Type Attribute Id for 'agile sprint'.
						RTAttribute rTAttribute = new RTAttribute(requirement.getRequirementTypeId(), "Agile Sprint");
						// for this req, for the Agile Sprint Attribute  , lets set the value to this sprint.
						requirement.setCustomAttributeValue(rTAttribute.getAttributeId(), sprint.getSprintName(), user, databaseType);

						status += "<br>Requirement   " + requirement.getRequirementFullTag() + " has been added to sprint " + sprint.getSprintName();
						
						String log = "Added Requirement to Sprint : " + sprint.getSprintName() ;
						RequirementUtil.createRequirementLog(requirementId, log, user.getEmailId(), databaseType);
						
					} else {
						// this means that an  invalid requirementtag was sent in.
						status += "<br>This Requirement does not exist : " + requirements[i] ; 
					}
				}
			}

			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			
		}
		return status;
	}


	public static String moveRequirementsToSprint(Project project, Sprint sprint,  String typeOfTasks, Sprint targetSprint, SecurityProfile securityProfile, 
		String databaseType) {

		String status = "";
		try {

			User user = securityProfile.getUser();
			
			String taskStatus = "";
			if (typeOfTasks.equals("Not Started")){
				taskStatus = "notStarted";
			}
			if (typeOfTasks.equals("In Progress")){
				taskStatus = "inProgress";
			}
			if (typeOfTasks.equals("Blocked")){
				taskStatus = "blocked";
			}
			if (typeOfTasks.equals("Completed")){
				taskStatus = "completed";
			}
			ArrayList requirementsToMove = SprintUtil.getRequirementsInSprint(taskStatus, sprint, "", project, securityProfile, databaseType);
			
			Iterator rToMove = requirementsToMove.iterator();
			// lets iterate through all these reqs and move them to the next sprint.
			while (rToMove.hasNext()){
				Requirement requirement = (Requirement) rToMove.next();
				if (!( securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
		    			+ requirement.getFolderId()))){
					status += "<br>You do not have permissions to update this requirement : " + requirement.getRequirementFullTag();
					continue;							
				}
				
				// if this requirement is locked and its locked by someone other than this user, then all updates to this req are disabled.
				if (
					(!(requirement.getRequirementLockedBy().equals("")))
					&&
					(!(requirement.getRequirementLockedBy().equals(user.getEmailId())))
					){
					
					status += "<br>This requirement : " + requirement.getRequirementFullTag() + " is locked by " + requirement.getRequirementLockedBy();
					
					continue;
				}
				// lets get the Req Type Attribute Id for 'agile sprint'.
				RTAttribute rTAttribute = new RTAttribute(requirement.getRequirementTypeId(), "Agile Sprint");
				// for this req, for the Agile Sprint Attribute  , lets set the value to this sprint.
				requirement.setCustomAttributeValue(rTAttribute.getAttributeId(), targetSprint.getSprintName(), user, databaseType);

				status += "<br>Requirement   " + requirement.getRequirementFullTag() + " has been added to sprint " + targetSprint.getSprintName();
				
				String log = "Move Requirement from Spritn " + sprint.getSprintName() + " to Sprint : " + targetSprint.getSprintName() ;
				RequirementUtil.createRequirementLog(requirement.getRequirementId(), log, user.getEmailId(), databaseType);
				
			} 
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			
		}
		return status;
	}
	
	// returns an array list of all requirements in a sprint
	 public static ArrayList getRequirementsInSprint(String taskStatus, Sprint sprint, String showOnlyTasksOwnedBy, Project project, SecurityProfile securityProfile, 
		String databaseType){
		ArrayList requirements = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sprintClause = "Agile Sprint:#:" + sprint.getSprintName();
			
			String taskStatusClause = "";
			if (taskStatus.equals("notStarted")){
				taskStatusClause = "Agile Task Status:#:Not Started";
			}
			if (taskStatus.equals("inProgress")){
				taskStatusClause = "Agile Task Status:#:In Progress";
			}
			if (taskStatus.equals("blocked")){
				taskStatusClause = "Agile Task Status:#:Blocked";
			}
			if (taskStatus.equals("completed")){
				taskStatusClause = "Agile Task Status:#:Completed";
			}
			
			String ownerClause = "";
			if (
					(showOnlyTasksOwnedBy.equals("") || (showOnlyTasksOwnedBy.equals("all")) )	){
				// do nothing. Show all tasks.
			}
			else {
				ownerClause = " and r.owner = '"+ showOnlyTasksOwnedBy +"' ";
			}
			
			String sql = "";
			if (databaseType.equals("mySQL")){
				
				
				sql  = "  SELECT distinct  r.id, r.requirement_type_id, r.folder_id, r.project_id, r.name, "
					+ " r.description, r.tag, r.full_tag, "
					+ " r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\" ,"
					+ " r.approvers ,  "
					+ " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
					+ " r.external_url, r.trace_to, r.trace_from,  r.user_defined_attributes, r.testing_status, "
					+ " r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\",  r.last_modified_by, "
					+ " r.last_modified_dt, rt.name \"requirement_type_name\", "
					+ " r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 ,ltrim(r.name), ltrim(r.description) "
					+ " FROM gr_requirements r , gr_requirement_types rt, gr_folders f" 
					+ " where r.project_id = ? " + " and   r.folder_id = f.id "
					+ " and r.requirement_type_id = rt.id "
					+ " and r.deleted = 0  "
					+ " and r.user_defined_attributes like '%"+ sprintClause +"%'"
					+ ownerClause ;
				if (!(taskStatusClause.equals(""))){
					sql +=  " and r.user_defined_attributes like '%"+ taskStatusClause +"%'";
				}

			}
			else {
				sql  = " SELECT distinct r.id, r.requirement_type_id, r.folder_id, r.project_id, r.name, "
					+ "  substr(to_char(r.description),1,4000) \"description\", r.tag, r.full_tag, "
					+ " r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\" ,"
					+ " r.approvers ,  "
					+ " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
					+ " r.external_url,  substr(to_char(r.trace_to),1,4000) \"trace_to\"," +
						"  substr(to_char(r.trace_from),1,4000) \"trace_from\"," 
					+ "   substr(to_char(r.user_defined_attributes),1,4000) \"user_defined_attributes\", r.testing_status, "
					+ " r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\",  r.last_modified_by, "
					+ " r.last_modified_dt, rt.name \"requirement_type_name\" , "
					+ " r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4, ltrim(r.name), ltrim(to_char(r.description)) "
					+ " FROM gr_requirements r , gr_requirement_types rt, gr_folders f" 
					+ " where r.project_id = ? " + " and   r.folder_id = f.id "
					+ " and r.requirement_type_id = rt.id "
					+ " and r.deleted = 0  "
					+ " and r.user_defined_attributes like '%"+ sprintClause +"%'"
					+ ownerClause ;
				if (!(taskStatusClause.equals(""))){
					sql +=  " and r.user_defined_attributes like '%"+ taskStatusClause +"%'";
				}
					
			}

		
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, project.getProjectId());
			rs = prepStmt.executeQuery();

			int requirementId = 0;
			int requirementTypeId = 0;
			int projectId = 0;
			int folderId = 0;
			String requirementName = "";
			String requirementDescription = "";
			String requirementTag = "";
			String requirementFullTag = "";
			int version = 0;
			String approvedByAllDt = "";
			String approvers = "";
			String requirementStatus = "";
			String requirementPriority = "";
			String requirementOwner = "";
			String requirementLockedBy = "";
			int requirementPctComplete = 0;
			String requirementExternalUrl = "";
			String traceTo = "";
			String traceFrom = "";
			String userDefinedAttributes = "";
			String testingStatus = "";
			int deleted = 0;
			String folderPath = "";
			String createdBy = "";
			String createdDt = "";
			String lastModifiedBy ="";
			String requirementTypeName = "";
			
			Requirement requirement = null;
			
			
			
			while (rs.next()) {
				requirementId = rs.getInt("id");
				folderId = rs.getInt("folder_id");
				requirementTypeId = rs.getInt("requirement_type_id");
				projectId = rs.getInt("project_id");
				requirementName = rs.getString("name");
				requirementDescription = rs.getString("description");
				requirementTag = rs.getString("tag");
				requirementFullTag = rs.getString("full_tag");
				version = rs.getInt("version");
				approvedByAllDt = rs.getString("approved_by_all_dt");
				approvers = rs.getString("approvers");
				requirementStatus = rs.getString("status");
				requirementPriority = rs.getString("priority");
				requirementOwner = rs.getString("owner");
				requirementLockedBy = rs.getString("locked_by");
				requirementPctComplete = rs.getInt("pct_complete");
				requirementExternalUrl = rs.getString("external_url");
				traceTo = rs.getString("trace_to");
				traceFrom = rs.getString("trace_from");
				userDefinedAttributes = rs
						.getString("user_defined_attributes");
				testingStatus = rs.getString("testing_status");
				deleted = rs.getInt("deleted");
				folderPath = rs.getString("folder_path");
				createdBy = rs.getString("created_by");
				createdDt = rs.getString("created_dt");
				lastModifiedBy = rs.getString("last_modified_by");
				// lastModifiedDt = rs.getDate("last_modified_by");
				requirementTypeName = rs
						.getString("requirement_type_name");

				
				requirement = new Requirement(requirementId,
						requirementTypeId, folderId, projectId,
						requirementName, requirementDescription,
						requirementTag, requirementFullTag, version,
						approvedByAllDt, approvers, requirementStatus,
						requirementPriority, requirementOwner, requirementLockedBy,
						requirementPctComplete, requirementExternalUrl,
						traceTo, traceFrom, userDefinedAttributes, testingStatus, deleted, folderPath,
						createdBy, lastModifiedBy, requirementTypeName, createdDt);
				

				// if the user does not have read permissions on this requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ requirement.getFolderId()))){
					requirement.redact();
				}
				requirements.add(requirement);
				
			}
			
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

		return requirements;
	}
	 
	 

	
	
	
}
