package com.gloree.actions;

import com.gloree.beans.*;
import com.gloree.utils.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/////////////////////////////////////////////Purpose ///////////////////////////////////////////
//
//	This servlet is used to create , delete and update both Requirement Type and its
// attributes. Access restricted to Admins only.
//
///////////////////////////////////////////Purpose ///////////////////////////////////////////


public class RequirementTypeAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public RequirementTypeAction() {
        super();
    }

    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost (request,response);
    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String databaseType = this.getServletContext().getInitParameter("databaseType");

		///////////////////////////////SECURITY//////////////////////////////
		// Security  Note:
		// user has to be logged in by the time he is here. 
		// And he needs to be an Administrator
		// of this project.
		///////////////////////////////SECURITY//////////////////////////////
		
		// see if the user is logged in. If he is not, the method below will
		// redirect him to the log in page.
		if (!(SecurityUtil.authenticationPassed(request, response))){
			return;
		}
		
		// now check if this users should be in this project.
		HttpSession session = request.getSession(true);
		SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
		Project project = (Project) session.getAttribute("project");
		if (!(
				(securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))
				||
				(securityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId())))
			)
		{
			//User is NOT a member of this project. so do nothing and return.
			return;
		}
		///////////////////////////////SECURITY//////////////////////////////

		User user = securityProfile.getUser();
		
		String action = request.getParameter("action");
		
		if ( action.equals("createRequirementType")){
			
			int projectId = Integer.parseInt(request.getParameter("projectId"));

			int requirementTypeEnableApproval = Integer.parseInt(
				request.getParameter("requirementTypeEnableApproval"));
			int requirementTypeEnableAgileScrum = 0;
			int requirementTypeCanBeDangling = Integer.parseInt(
					request.getParameter("requirementTypeCanBeDangling"));

			int requirementTypeCanBeOrphan = Integer.parseInt(
					request.getParameter("requirementTypeCanBeOrphan"));
			
			int notifyOnOwnerChange = Integer.parseInt(
					request.getParameter("notifyOnOwnerChange"));

			int notifyOnApprovalChange = Integer.parseInt(
					request.getParameter("notifyOnApprovalChange"));
			
			
			String requirementTypeCanNotTraceTo = request.getParameter("requirementTypeCanNotTraceTo");
			String requirementTypeShortName = request.getParameter("requirementTypeShortName");
			String requirementTypeName = request.getParameter("requirementTypeName");
			String requirementTypeDescription = request.getParameter("requirementTypeDescription");
			
			// NOTE : YUI has issues in explorer if the name  has ' or ". so replacing
			// them with ^.
			// Same with folderDescription.
			requirementTypeName = requirementTypeName.replace('\'', '^');
			requirementTypeName = requirementTypeName.replace('"', '^');
			requirementTypeName = requirementTypeName.replace("::", "--");
			
			requirementTypeDescription = requirementTypeDescription.replace('\'', '^');
			requirementTypeDescription = requirementTypeDescription.replace('"', '^');
			requirementTypeDescription = requirementTypeDescription.replace("::", "--");
			requirementTypeDescription = requirementTypeDescription.replace('\n', ' ');
			requirementTypeDescription = requirementTypeDescription.replace('\r', ' ');
			
			
			int requirementTypeDisplaySequence = 0;
			try {
				requirementTypeDisplaySequence = Integer.parseInt(request.getParameter("requirementTypeDisplaySequence"));
			}
			catch (Exception e) {
				requirementTypeDisplaySequence = 0;
			}
			
			int status = ProjectUtil.isUniqueRequirementType(projectId, requirementTypeShortName, requirementTypeName);
			
			if (status == 0){
				// this means the RequirementTypeShortname is not unique.
				request.setAttribute("status", "shortName already used");
				RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/RequirementType/createRequirementTypeForm.jsp");
				dispatcher.forward(request, response);
				return;
			}

			RequirementType requirementType = new RequirementType(projectId,project.getProjectName(), 
				requirementTypeShortName, requirementTypeName, requirementTypeDescription,
				requirementTypeDisplaySequence, requirementTypeEnableApproval, requirementTypeEnableAgileScrum, requirementTypeCanBeDangling, 
				requirementTypeCanBeOrphan, requirementTypeCanNotTraceTo, user.getEmailId(),  databaseType);
			request.setAttribute("requirementType", requirementType);
			
			requirementType.setNotifyOnOwnerChange(notifyOnOwnerChange);
			requirementType.setNotifyOnApprovalChange(notifyOnApprovalChange);
			
			
			
			
			// Once the requirementType is created, the project structure has changed and the project object in memory is no longer
			// valid. So, we need to create a new one and replace the one in the session memory.
			project = new Project(projectId, databaseType);
			session.setAttribute("project", project);
			
			// since we just created a new Req Type, and since this user is the admin and has
			// read / write privs on this folder / req type, we need to refresh his / her security profile
			// so that they can start working on this reqtype / folder.
			securityProfile = new SecurityProfile(user.getUserId(),this.getServletContext().getInitParameter("databaseType"));
			session.setAttribute("securityProfile", securityProfile);			

			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/RequirementType/displayRequirementType.jsp?requirementTypeId="+ requirementType.getRequirementTypeId());
			dispatcher.forward(request, response);
			return;
		}
		else if ( action.equals("updateRequirementType")){
			// the goal here is to redirect the user to a confirmation page, 
			
			int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
			RequirementType requirementType = new RequirementType(requirementTypeId);
			
			
			// lets set the remindapprovers on separately, as this function was built after
			// updateRequirements method.
			String remindApproversOn = request.getParameter("remindApproversOn");
			if (remindApproversOn == null ){
				remindApproversOn = "";
			}
			requirementType.setRemindApproversOn(remindApproversOn);
			
			
			int requirementTypeEnableApproval = Integer.parseInt(request.getParameter("requirementTypeEnableApproval"));
			
			
			int requirementTypeCanBeDangling = Integer.parseInt(
					request.getParameter("requirementTypeCanBeDangling"));

			int requirementTypeCanBeOrphan = Integer.parseInt(
					request.getParameter("requirementTypeCanBeOrphan"));

			String requirementTypeCanNotTraceTo = request.getParameter("requirementTypeCanNotTraceTo");
			
			int requirementTypeDisplaySequence = 0;
			try {
				requirementTypeDisplaySequence = Integer.parseInt(request.getParameter("requirementTypeDisplaySequence"));
			}
			catch (Exception e) {
				e.printStackTrace();
				requirementTypeDisplaySequence = requirementType.getRequirementTypeDisplaySequence();
			}
			
			
			String requirementTypeName = request.getParameter("requirementTypeName");
			String requirementTypeDescription = request.getParameter("requirementTypeDescription");
			
			// NOTE : YUI has issues in explorer if the name  has ' or ". so replacing
			// them with ^.
			// Same with folderDescription.
			requirementTypeName = requirementTypeName.replace('\'', '^');
			requirementTypeName = requirementTypeName.replace('"', '^');
			requirementTypeName = requirementTypeName.replace("::", "--");
			
			requirementTypeDescription = requirementTypeDescription.replace('\'', '^');
			requirementTypeDescription = requirementTypeDescription.replace('"', '^');
			requirementTypeDescription = requirementTypeDescription.replace("::", "--");
			requirementTypeDescription = requirementTypeDescription.replace('\n', ' ');
			requirementTypeDescription = requirementTypeDescription.replace('\r', ' ');

			// here we update name and description 
			// we have decided against letting a user change a prefix because that can lead to complications with 
			// a. auditing , b. ensuring that the req full tags are unique.
			
			ProjectUtil.updateRequirementType(project.getProjectId(), 
					requirementTypeId,requirementTypeName, 
					requirementTypeDescription, requirementTypeDisplaySequence, requirementTypeEnableApproval, 
					requirementTypeCanBeDangling, requirementTypeCanBeOrphan, 
					requirementTypeCanNotTraceTo, user.getEmailId(),  databaseType);
			
			
			int notifyOnOwnerChange = Integer.parseInt(
					request.getParameter("notifyOnOwnerChange"));

			int notifyOnApprovalChange = Integer.parseInt(
					request.getParameter("notifyOnApprovalChange"));
			
			
			requirementType.setNotifyOnOwnerChange(notifyOnOwnerChange);
			requirementType.setNotifyOnApprovalChange(notifyOnApprovalChange);
			

			// Once the requirementType is update, the project structure has changed and the project object in memory is no longer
			// valid. So, we need to create a new one and replace the one in the session memory.
			// First we get the project id of the stale project object.
			int projectId = project.getProjectId();
			
			// Next, we create a new one, and replace it in the session.
			project = new Project(projectId, databaseType);
			session.setAttribute("project", project);

			request.setAttribute("updatedRequirementType", "true");
			RequestDispatcher dispatcher =	request.getRequestDispatcher(
					"/jsp/AdministerProject/RequirementType/updateRequirementTypeForm.jsp?requirementTypeId=" +
					requirementTypeId);
			dispatcher.forward(request, response);
			return;
		}

		else if (action.equals("deleteRequirementType")){
			// we need to deleted the requirementType and refresh the project object in memory.
			
			
			int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
			
			ProjectUtil.deleteRequirementType(project.getProjectId(), requirementTypeId, user.getEmailId(),  databaseType);
			
			// Once the requirementType is deleted, the project structure has changed and the project object in memory is no longer
			// valid. So, we need to create a new one and replace the one in the session memory.
			// First we get the project id of the stale project object.
			int projectId = project.getProjectId();
			
			// Next, we create a new one, and replace it in the session.
			project = new Project(projectId, databaseType);
			session.setAttribute("project", project);
			
			// Now forward it to the dispatcher.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/RequirementType/deleteRequirementType.jsp");
			dispatcher.forward(request, response);
			return;
			
		}
		else if (action.equals("resetRequirementTypeSeq")){
			// we need to deleted the requirementType and refresh the project object in memory.
			
			
			int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
			RequirementType reqType = new RequirementType(requirementTypeId, databaseType);
			ProjectUtil.resetRequirementTypeSeq(reqType, project.getProjectId(), requirementTypeId, user.getEmailId(),  databaseType);
			
			// Once the requirementType is deleted, the project structure has changed and the project object in memory is no longer
			// valid. So, we need to create a new one and replace the one in the session memory.
			// First we get the project id of the stale project object.
			int projectId = project.getProjectId();
			
			// Next, we create a new one, and replace it in the session.
			project = new Project(projectId, databaseType);
			session.setAttribute("project", project);
			
			// Now forward it to the dispatcher.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/RequirementType/resetRequirementTypeSeq.jsp");
			dispatcher.forward(request, response);
			return;
			
		}		
		else if (action.equals("createBaseline")){
			// we need to add crate a new baseline , and then forward to rTBaselineForm page.	
			int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
			String baselineName = request.getParameter("baselineName");
			int locked = Integer.parseInt(request.getParameter("locked"));
			String baselineDescription = request.getParameter("baselineDescription");
			
			
			boolean isUnique = ProjectUtil.isUniqueBaseline(requirementTypeId, baselineName);
			
			if (!(isUnique)){
				// this means the baselineName is not unique within this requirementtype id
				request.setAttribute("status", "baselineName already used");
				RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/RTBaseline/createRTBaselineForm.jsp");
				dispatcher.forward(request, response);
				return;
			}
			
			RTBaseline rTBaseline = new RTBaseline(databaseType, project.getProjectId(), requirementTypeId, 
				baselineName , locked, 
				baselineDescription, user.getEmailId());
			
			request.setAttribute("rTBaseline", rTBaseline);
			// Now forward it to the dispatcher.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/RTBaseline/rTBaselineCreated.jsp");
			dispatcher.forward(request, response);
			return;
		}
		else if (action.equals("editRTBaseline")){
			// we need to add crate a new baseline , and then forward to rTBaselineForm page.	
			int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
			int rTBaselineId = Integer.parseInt(request.getParameter("rTBaselineId"));
			RTBaseline oldRTBaseline  = new RTBaseline(rTBaselineId);
			
			String baselineName = request.getParameter("baselineName");
			int locked = Integer.parseInt(request.getParameter("locked"));
			String baselineDescription = request.getParameter("baselineDescription");
			
			// first we have to see if the baseline Name has been changed. 
			// if the new name is same as the old one, we are OK. 
			// if it is not,we have to make sure that it's unique.
			if (oldRTBaseline.getBaselineName().equals(baselineName)){
				// this is the only case where the baseline name can be duplicate. Else
				// we have to make sure that the new baseline name being given as part of this edit
				// does not clash with another baseline name for this req.
				
			}
			else {
				// this means , in this round the user has changed the baseline name for this baseline.
				// lets make sure that this one is unique.
				boolean isUnique = ProjectUtil.isUniqueBaseline(requirementTypeId, baselineName);
				if (!(isUnique)){
					// this means the baselineName is not unique within this requirementtype id
					request.setAttribute("status", "baselineName already used");
					RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/RTBaseline/editRTBaselineForm.jsp");
					dispatcher.forward(request, response);
					return;
				}
			}
			// lets call RTBaseline() to update its settings.
			// Note, this does not create a new baseline row in the db, it just updates it.
			// the difference between this call and the previous call is that in this case , the baselineId exist
			// so the object constructor knows to update the value, as opposed to create a new baseline.
			
			// NOTE : do not remove this rTBaseline, eventhough Eclipse may say it's never read.
			// because, the creation process, updates the db.
			oldRTBaseline.setNameValue(rTBaselineId, baselineName, locked, baselineDescription, user.getEmailId(), databaseType);
			// Now forward it to the dispatcher.
			request.setAttribute("updatedBaseline", "true");
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/RTBaseline/editRTBaselineForm.jsp");
			dispatcher.forward(request, response);
			return;
		}
		else if (action.equals("createAttribute")){
			// we need to add crate a new attribute , and then forward to rTAttributeForm page.	
			int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
			String attributeName = request.getParameter("attributeName");
			String attributeType = request.getParameter("attributeType");
			String attributeSortOrder = request.getParameter("attributeSortOrder");
			int attributeRequired = Integer.parseInt(request.getParameter("attributeRequired"));
			int attributeDefaultDisplay = Integer.parseInt(request.getParameter("attributeDefaultDisplay"));
			
			String attributeDefaultValue = request.getParameter("attributeDefaultValue");
			// lets drop the ' in the attribute name
			if ((attributeName != null ) && (attributeName.contains("'"))){
				attributeName = attributeName.replace("'", " ");
			}
			
			if ((attributeName != null ) && (attributeName.contains("\""))){
				attributeName = attributeName.replace("\"", " ");
			}
			
			int parentAttributeId = 0;
			try {
				parentAttributeId = Integer.parseInt(request.getParameter("parentAttributeId"));
			}
			catch (Exception e){
				// do nothing.
			}
			String attributeDropDownOptions = request.getParameter("attributeDropDownOptions");
			String attributeDescription = request.getParameter("attributeDescription");
			int attributeImpactsVersion = Integer.parseInt(request.getParameter("attributeImpactsVersion"));
			int attributeImpactsTraceability = Integer.parseInt(request.getParameter("attributeImpactsTraceability"));
			int attributeImpactsApprovalWorkflow = Integer.parseInt(request.getParameter("attributeImpactsApprovalWorkflow"));

			
			// lets eliminate the spaces after the , in the attribute drop down options. This
			// reduces confusion during cloning when spaces cause problem between different req drop down attributes.
			// since the values themselves can have spaces, we don't want to lose them. we have to break up and 
			// recombine.
			String trimmedAttributeDropDownOptions = "";
			if ((attributeDropDownOptions!= null ) && (attributeDropDownOptions.contains(","))){
				String [] options = attributeDropDownOptions.split(",");
				for (int i=0; i< options.length; i++) {
					trimmedAttributeDropDownOptions += options[i].trim() + ",";
				}
				// lets drop the last ,
				if (trimmedAttributeDropDownOptions.contains(",")){
					trimmedAttributeDropDownOptions = (String) trimmedAttributeDropDownOptions.subSequence(0,trimmedAttributeDropDownOptions.lastIndexOf(","));
			    }			    
			}
			else {
				trimmedAttributeDropDownOptions = attributeDropDownOptions.trim();
			}
			attributeDropDownOptions = trimmedAttributeDropDownOptions;
				
			
			int status = ProjectUtil.isUniqueAttribute(requirementTypeId, attributeName);
			
			if (status == 0){
				// this means the attributeName is not unique within this requirementtype id
				request.setAttribute("status", "attributeName already used");
				RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/RTAttribute/createRTAttributeForm.jsp");
				dispatcher.forward(request, response);
				return;
			}
			
			
			RTAttribute rTAttribute = new RTAttribute(project.getProjectId(),parentAttributeId, 0, requirementTypeId, attributeName , 
				attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
				attributeDropDownOptions, attributeDescription,
				attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
				user.getEmailId(), databaseType);
			rTAttribute.setAttributeDefaultDisplay(attributeDefaultDisplay);
			
			// Any time a custom attribute is created, we want to give the admin the default update this attribute 
			// in all folders privilege.
			// To do this
			// 1. Itereate and find all folders in this req type
			// 2. for each folder, get the updateable attributes value for the Administrator role
			// 3. to each folder, for admin role, give update permissions on this new attribute.
			int administratorRoleId = ProjectUtil.getAdministratorRoleId(project.getProjectId());
			ProjectUtil.giveRoleUpdateCustomAttributesPrivilege(administratorRoleId,requirementTypeId, rTAttribute.getAttributeName(), databaseType);
			
			request.setAttribute("rTAttribute", rTAttribute);
			// Now forward it to the dispatcher.
			String url="/jsp/AdministerProject/RTAttribute/editRTAttributeForm.jsp";
			url += "?rTAttributeId="+ rTAttribute.getAttributeId() + "&requirementTypeId=" + requirementTypeId;
			request.setAttribute("createdAttribute", "true");

			RequestDispatcher dispatcher =	request.getRequestDispatcher(url);
			dispatcher.forward(request, response);
			return;
		}
		else if (action.equals("deleteRTAttribute")){
			// we need to delete an attribute , and then forward to rTAttributeDeleted page.
			// as part of the delete, we need to also delete the attribute values of all requirements that have this value.
			// also, for all reqs of this type, we need to reset the UserDefinedAttribute field.
			int rTAttributeId = Integer.parseInt(request.getParameter("rTAttributeId"));
			ProjectUtil.deleteRTAttribute(project.getProjectId(), rTAttributeId, user.getEmailId(), databaseType);
			
			// Now forward it to the dispatcher.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/RTAttribute/rTAttributeDeleted.jsp");
			dispatcher.forward(request, response);
			return;
			
		}
		else if (action.equals("purgeAllDeletedRequirementsInRequirementType")){
			//  lets get the requirementTypeId and then call projectUtil to purge all deleted 
			// requirements in this requirement type.
			int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
			ProjectUtil.purgeAllDeletedRequirementsInRequirementType(project.getProjectId(), requirementTypeId, user.getEmailId(),  databaseType);
			
			// Now forward it to the dispatcher.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Folder/displayAllRequirementsInVirtualFolder.jsp?folderId=-1:" + requirementTypeId);
			dispatcher.forward(request, response);
			return;
			
		}
		else if (action.equals("deleteRTBaseline")){
			// we need to delete a baseline, and then forward to rTBaselineDeleted page.
			// as part of the delete, we need to also delete the baseline associations of 
			// of all requirements that have this value.

			int rTBaselineId = Integer.parseInt(request.getParameter("rTBaselineId"));
			ProjectUtil.deleteRTBaseline(project.getProjectId(), rTBaselineId, user.getEmailId(),  databaseType);
			
			// Now forward it to the dispatcher.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/RTBaseline/rTBaselineDeleted.jsp");
			dispatcher.forward(request, response);
			return;
			
		}
		else if (action.equals("updateDefectStatusGroup")){
			// lets get the list of defect status groups in this project.
			ArrayList defectStatusGroups = project.getDefectStatusGroups();
			Iterator i = defectStatusGroups.iterator();
			while (i.hasNext()){
				DefectStatus defectStatus = (DefectStatus) i.next();
				String defectStatusGroup = request.getParameter("defectStatusGroupId" + defectStatus.getDefectStatusGroupId());
				defectStatus.setDefectStatusGroup(defectStatusGroup);
			}
			
			// Now forward it to the dispatcher.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/mapDefectStatusGroup.jsp?status=updateSuccessful");
			dispatcher.forward(request, response);
			return;
			
		}		

		else if (action.equals("editRTAttribute")){
			// we need to add crate a new attribute , and then forward to rTAttributeForm page.	
			int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
			int rTAttributeId = Integer.parseInt(request.getParameter("rTAttributeId"));
			RTAttribute oldRTAttribute  = new RTAttribute(rTAttributeId);
			
			String attributeName = request.getParameter("attributeName");
			String attributeType = request.getParameter("attributeType");
			String attributeSortOrder = request.getParameter("attributeSortOrder");
			int attributeRequired = Integer.parseInt(request.getParameter("attributeRequired"));
			int attributeDefaultDisplay = Integer.parseInt(request.getParameter("attributeDefaultDisplay"));
			String attributeDefaultValue = request.getParameter("attributeDefaultValue");
			int parentAttributeId = Integer.parseInt(request.getParameter("parentAttributeId"));
			String attributeDropDownOptions = request.getParameter("attributeDropDownOptions");
			String attributeDescription = request.getParameter("attributeDescription");
			int attributeImpactsVersion = Integer.parseInt(request.getParameter("attributeImpactsVersion"));
			int attributeImpactsTraceability = Integer.parseInt(request.getParameter("attributeImpactsTraceability"));
			int attributeImpactsApprovalWorkflow = Integer.parseInt(request.getParameter("attributeImpactsApprovalWorkflow"));

			// lets eliminate the spaces after the , in the attribute drop down options. This
			// reduces confusion during cloning when spaces cause problem between different req drop down attributes.
			// since the values themselves can have spaces, we don't want to lose them. we have to break up and 
			// recombine.
			String trimmedAttributeDropDownOptions = "";
			if ((attributeDropDownOptions!= null ) && (attributeDropDownOptions.contains(","))){
				String [] options = attributeDropDownOptions.split(",");
				for (int i=0; i< options.length; i++) {
					trimmedAttributeDropDownOptions += options[i].trim() + ",";
				}
				// lets drop the last ,
				if (trimmedAttributeDropDownOptions.contains(",")){
					trimmedAttributeDropDownOptions = (String) trimmedAttributeDropDownOptions.subSequence(0,trimmedAttributeDropDownOptions.lastIndexOf(","));
			    }			    
			}
			else {
				trimmedAttributeDropDownOptions = attributeDropDownOptions.trim();
			}
			attributeDropDownOptions = trimmedAttributeDropDownOptions;
				
			
			
			// first we have to see if the attribute Name has been changed. 
			// if the new name is same as the old one, we are OK. 
			// if it is not,we have to make sure that it's unique.
			if (oldRTAttribute.getAttributeName().equals(attributeName)){
				// this is the only case where the attribute name can be duplicate. Else
				// we have to make sure that the new attribute name being given as part of this edit
				// does not clash with another attribute name for this req.
				
			}
			else {
				// this means , in this round the user has changed the attribute name for this attribute.
				// lets make sure that this one is unique.
				int status = ProjectUtil.isUniqueAttribute(requirementTypeId, attributeName);
				if (status == 0){
					// this means the attributeName is not unique within this requirementtype id
					request.setAttribute("status", "attributeName already used");
					RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/RTAttribute/editRTAttributeForm.jsp");
					dispatcher.forward(request, response);
					return;
				}
			}
			// lets call RTAttribute() to update its settings.
			// Note, this does not create a new attribute row in the db, it just updates it.
			// the difference between this call and the previous call is that in this case , the attributeId exist
			// so the object constructor knows to update the value, as opposed to create a new attribute.
			
			// NOTE : do not remove this rTAttribute, eventhough Eclipse may say it's never read.
			// becuase, the creation process, updates the db.
			RTAttribute rTAttribute = new RTAttribute(rTAttributeId,parentAttributeId, 0, requirementTypeId,
					attributeName , attributeType , attributeSortOrder, 
					attributeRequired, attributeDropDownOptions,
					attributeDescription,attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow, databaseType);
			rTAttribute.setAttributeDefaultDisplay(attributeDefaultDisplay);
			rTAttribute.setAttributeDefaultValue(attributeDefaultValue);
			// Now forward it to the dispatcher.
			request.setAttribute("updatedAttribute", "true");
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/AdministerProject/RTAttribute/editRTAttributeForm.jsp");
			dispatcher.forward(request, response);
			return;
		}
		
		else if ( action.equals("createWebForm")){
			
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			String name = request.getParameter("name");
			String description = request.getParameter("description");
			String introduction  = request.getParameter("introduction");
			String owner = request.getParameter("owner");
			String notifyOnCreation  = request.getParameter("notifyOnCreation");
			int submitForApprovalOnCreation  = 0;
			
			try {
			 submitForApprovalOnCreation  = Integer.parseInt(request.getParameter("submitForApprovalOnCreation"));
			}
			catch (Exception e){
				submitForApprovalOnCreation = 0;
			}
			
			int enableLookup  = 0;
			
			try {
				enableLookup  = Integer.parseInt(request.getParameter("enableLookup"));
			}
			catch (Exception e){
				enableLookup = 0;
			}
			
			
			
			// NOTE : YUI has issues in explorer if the name  has ' or ". so replacing
			// them with ^.
			// Same with folderDescription.
			
			name = name.replace('\'', '^');
			name = name.replace('"', '^');
			name = name.replace("::", "--");
			name = name.replace('\n', ' ');
			name = name.replace('\r', ' ');
			
			String accessCode = session.getId().substring(0,8);
			

			WebForm webForm = new WebForm(project.getProjectId(), folderId,  name,  description,  introduction,  owner,
					  notifyOnCreation,  submitForApprovalOnCreation,enableLookup,  accessCode);
			
			webForm = new WebForm(folderId, accessCode);
			
			RequestDispatcher dispatcher =	request.getRequestDispatcher(
					"/jsp/AdministerProject/WebForm/displayWebForm.jsp?webFormId=" + webForm.getId()  );
			dispatcher.forward(request, response);
			return;
		}

		else if ( action.equals("updateWebForm")){
			
			int webFormId = Integer.parseInt(request.getParameter("webFormId"));
			
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			String name = request.getParameter("name");
			String description = request.getParameter("description");
			String introduction  = request.getParameter("introduction");
			String owner = request.getParameter("owner");
			String notifyOnCreation  = request.getParameter("notifyOnCreation");
			int submitForApprovalOnCreation  = 0;
			
			try {
			 submitForApprovalOnCreation  = Integer.parseInt(request.getParameter("submitForApprovalOnCreation"));
			}
			catch (Exception e){
				submitForApprovalOnCreation = 0;
			}
			
			int enableLookup  = 0;
			
			try {
				enableLookup  = Integer.parseInt(request.getParameter("enableLookup"));
			}
			catch (Exception e){
				enableLookup = 0;
			}
			
			
			
			// NOTE : YUI has issues in explorer if the name  has ' or ". so replacing
			// them with ^.
			// Same with folderDescription.
			
			name = name.replace('\'', '^');
			name = name.replace('"', '^');
			name = name.replace("::", "--");
			name = name.replace('\n', ' ');
			name = name.replace('\r', ' ');
			
			//String accessCode = session.getId().substring(0,8);
			

			WebForm webForm = new WebForm(webFormId,  folderId,  name,  description,  introduction,  owner,
					  notifyOnCreation,  submitForApprovalOnCreation,enableLookup);
			
			webForm = new WebForm(webFormId);
			
			RequestDispatcher dispatcher =	request.getRequestDispatcher(
					"/jsp/AdministerProject/WebForm/displayWebForm.jsp?webFormId=" + webForm.getId()  );
			dispatcher.forward(request, response);
			return;
		}
		
		

		else if ( action.equals("deleteWebForm")){
			
			int webFormId = Integer.parseInt(request.getParameter("webFormId"));
			
			ProjectUtil.deleteWebForm(webFormId);
			PrintWriter out = response.getWriter();
			out.println("<span class='normalText'>  This webform has been deleted from the system. </span>");
			return;
		}

		
		
		
		
		
		// if nothing else works, forward to the Welcome screen. This should never happen.
		RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/welcome.jsp");
		dispatcher.forward(request, response); 	
	}
}
