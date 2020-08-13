package com.gloree.actions;

import com.gloree.beans.*;
import com.gloree.utils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class SharedRequirementAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public SharedRequirementAction() {
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
		// And he needs to be an Member
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
		if (!(securityProfile.getRoles().contains("MemberInProject" + project.getProjectId()))){
			//User is NOT a member of this project. so do nothing and return.
			return;
		}
		
		User user = securityProfile.getUser();
		///////////////////////////////SECURITY//////////////////////////////
		
		String action = request.getParameter("action");
		
		if ( action.equals("updateSharedRequirementType")){
			
			int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
			String sRPublishStatus = request.getParameter("sRPublishStatus");
			String sRRTBaselineIds = request.getParameter("sRRTBaselineIds");
			int sRShareComments = Integer.parseInt(request.getParameter("sRShareComments"));
			String sRDomainAdministrators = request.getParameter("sRDomainAdministrators");
			int sRMandatoryNotification = Integer.parseInt(request.getParameter("sRMandatoryNotification")) ;
			String sRInstructions = request.getParameter("sRInstructions");
			
			RequirementType requirementType = new RequirementType(requirementTypeId);
			///////////////////////////////// SECURITY///////////////////////
			// if the user is not an admin in the project, then do nothing.
			//
			if (!(securityProfile.getRoles().contains("AdministratorInProject" + requirementType.getProjectId()))){
				return;
			}
			///////////////////////////////// SECURITY///////////////////////
			SharedRequirementType sRT = new SharedRequirementType(requirementTypeId, 
				sRPublishStatus, sRRTBaselineIds,sRShareComments, 
				sRDomainAdministrators, sRMandatoryNotification, sRInstructions);
			
			// now lets iterate through all the attributes and set their copyable and filterable options.
			ArrayList sRTAttributes = sRT.getAllSharedAttributesInRequirementType();
			Iterator s = sRTAttributes.iterator();
			while (s.hasNext()){
				SharedRequirementTypeAttribute sRTAttribute = (SharedRequirementTypeAttribute) s.next();
				// lets get the copyable , filterable paremeter sent in and update this attrib.
				int copyable = Integer.parseInt(request.getParameter("copyable" + sRTAttribute.getSRAId()));
				sRTAttribute.setSRACopyable(copyable);
				
				int filterable = Integer.parseInt(request.getParameter("filterable" + sRTAttribute.getSRAId()));
				sRTAttribute.setSRAFilterable(filterable);
				
				int displayable = Integer.parseInt(request.getParameter("displayable" + sRTAttribute.getSRAId()));
				sRTAttribute.setSRADisplayable(displayable);
				
				int editable = Integer.parseInt(request.getParameter("editable" + sRTAttribute.getSRAId()));
				sRTAttribute.setSRAEditable(editable);
				
			}
			request.setAttribute("updated", "true");
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/SharedRequirements/displaySharedRequirementType.jsp?sharedRequirementTypeId=" + requirementTypeId);
			dispatcher.forward(request, response);
			return;
		}

		if ( action.equals("filterSharedRequirements")){
			
			int sharedRequirementTypeId = Integer.parseInt(request.getParameter("sharedRequirementTypeId"));
			
			SharedRequirementType sRT = new SharedRequirementType(sharedRequirementTypeId);
			RequirementType rT = sRT.getRequirementType();
			
			int sRRTBaselineId = Integer.parseInt(request.getParameter("sRRTBaselineId"));
			
			// now lets iterate through all the attributes and set the custom attribute search string.
			String customAttributeSearch = "";
			ArrayList rTAttributes = rT.getAllAttributesInRequirementType();
			Iterator a = rTAttributes.iterator();
			while (a.hasNext()){
				RTAttribute rTAttribute = (RTAttribute) a.next();
				String attributeValue = "";
				try {
					attributeValue = request.getParameter("filterAttribute"+rTAttribute.getAttributeId());
				}
				catch (Exception e){
					e.printStackTrace();
					attributeValue = "";
				}
				
				if ((attributeValue != null) && (!(attributeValue.equals("")))){
					customAttributeSearch += rTAttribute.getAttributeName() + ":#:" +  attributeValue + ":--:";
				}
			}
			// drop the last :--:
			if (customAttributeSearch.contains(":--:")){
				customAttributeSearch = (String) customAttributeSearch.subSequence(0,customAttributeSearch.lastIndexOf(":--:"));
			}
			ArrayList filteredSharedRequirements = SharedRequirementUtil.getFilteredSharedRequirements(
				sRT,sRRTBaselineId, customAttributeSearch, project.getProjectId(),  databaseType);
				
			
			request.setAttribute("filteredSharedRequirements", filteredSharedRequirements);
			request.setAttribute("sharedRequirementType", sRT);
			request.setAttribute("requirementType", rT);
			
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/SharedRequirements/displayFilteredSharedRequirements.jsp?" );
			dispatcher.forward(request, response);
			return;
		}
		
		if ( action.equals("importUpdateSharedRequirements")){
			
			int sRRTBaselineId = Integer.parseInt(request.getParameter("sRRTBaselineId"));
			int sharedRequirementTypeId = Integer.parseInt(request.getParameter("sharedRequirementTypeId"));
			SharedRequirementType sRT = new SharedRequirementType(sharedRequirementTypeId);
			RequirementType rT = sRT.getRequirementType();
			Project sourceProject = new Project(rT.getProjectId(), databaseType);
			
			String sharedRequirementIds = request.getParameter("sharedRequirementIds");
			
			// if source Req type exists, update it, else create a new one.

			boolean newTargetReqType = false;
			String targetRequirementTypeShortName = sourceProject.getShortName() + "_" + project.getShortName() + "_"+ sRT.getRequirementType().getRequirementTypeShortName();
			if (targetRequirementTypeShortName.length() > 30){
				targetRequirementTypeShortName = targetRequirementTypeShortName.substring(0, 28);
			}
			// lets see if this req type already exists in the target project.
			int status = ProjectUtil.isUniqueRequirementType(
					project.getProjectId(), targetRequirementTypeShortName,sRT.getRequirementType().getRequirementTypeShortName());
			if (status != 0){
				// this means the RequirementTypeShortname does not exists
				newTargetReqType = true;
			}
				

			RequirementType targetRequirementType = SharedRequirementUtil.createOrUpdateRequirementType(sRT,project, user.getEmailId(), session,  databaseType);
			// Lets make sure that every source Folder and sub folder also appears in the Target Requirement with the same structre
			// this is tricky: We need to do the following synchronize 4 time. 
			
			SharedRequirementUtil.synchronizeTargetProjectFolderStructure(sourceProject, rT, project, targetRequirementType, user, databaseType);
			
			// Synchronize project folder structure , may create new folders. 
			//Once the folder is created, the project structure has changed and the project object in memory is no longer
			// valid.So, we need to create a new one and replace the one in the session memory.
			project = new Project(project.getProjectId(), databaseType);
			session.setAttribute("project", project);
			
			// Same with the security privs. we need to reset them in the session, so that this user
			// can work on these newly created folders. 
			
			securityProfile = new SecurityProfile(user.getUserId(),this.getServletContext().getInitParameter("databaseType"));
			session.setAttribute("securityProfile", securityProfile);
			
			
			ArrayList noUpdatePermissions = new ArrayList();
			ArrayList updatedRequirements = new ArrayList();
			ArrayList createdRequirements = new ArrayList();
			

			// lets iterate through all the input shared requirements and try to process each one.
			String [] sharedRequirementIdArray = sharedRequirementIds.split(":##:");
			for (int i=0; i < sharedRequirementIdArray.length; i++){
				String sharedRequirementIdString = sharedRequirementIdArray[i];
				if ((sharedRequirementIdString != null) && !(sharedRequirementIdString.trim().equals(""))){
					

					SharedRequirement sharedRequirement = new SharedRequirement(Integer.parseInt(sharedRequirementIdString), sRRTBaselineId, project.getProjectId(),  databaseType);

					int targetRequirementId = sharedRequirement.getTargetRequirementId();

					Requirement sourceRequirement = new Requirement(sharedRequirement.getSourceRequirementId(), databaseType);
					Requirement targetRequirement = null;
					
					if (targetRequirementId == 0){
						// this shared req doesn't exist in the target project. so we need to create it.
						
						String parentFullTag = "";
						try {
							// lets see if the parent for this requirement exists in the target project.
							String sourceRequirementParentFullTag = sourceRequirement.getParentFullTag();
							if ((sourceRequirementParentFullTag != null ) && !(sourceRequirementParentFullTag.equals(""))){
								int sourceRequirementParentId = RequirementUtil.getRequirementId(sourceRequirement.getProjectId(), sourceRequirementParentFullTag);							
								// lets see if this parent requirement exists in the target project.
								int targetParentRequirementId = SharedRequirementUtil.getTargetRequirementId(project.getProjectId(), sourceRequirementParentId);
								Requirement targetParentRequirement = new Requirement(targetParentRequirementId,databaseType);
								parentFullTag = targetParentRequirement.getRequirementFullTag();
							}
						}
						catch (Exception e){
							parentFullTag = "";
						}
						if (parentFullTag == null){
							parentFullTag = "";
						}
						
							

						// NOTE : Its CRITICAL that we use the shared requirement name and desc and not hte 
						// source req's name and desc. Source req's name and desc gives the latest name / desc of 
						// the source req. Shared req name and desc gives the published name / desc.
						String requirementLockedBy = "";
						targetRequirement = new Requirement(parentFullTag, targetRequirementType.getRequirementTypeId(),
							targetRequirementType.getRootFolderId(), project.getProjectId(), 
							sharedRequirement.getSourceRequirementName(), sharedRequirement.getSourceRequirementDescription(),
							sourceRequirement.getRequirementPriority(), user.getEmailId(), requirementLockedBy, sourceRequirement.getRequirementPctComplete(),
							sourceRequirement.getRequirementExternalUrl(), user.getEmailId(), databaseType);
				
						// we need to set the source req id next.
						targetRequirement.setSourceRequirementId(sourceRequirement.getRequirementId());
						
						SharedRequirementUtil.createTargetRequirement(sRT, 
							sharedRequirement, rT, 
							targetRequirementType, sourceProject, project, 
							sourceRequirement, targetRequirement, 
							user, securityProfile,  databaseType);

						// since the sharedreq has not changed (i.e has a target req), lets refresh it and add it to the arraylist
						// for display
						sharedRequirement = new SharedRequirement(sharedRequirement.getSourceRequirementId(), sRRTBaselineId, project.getProjectId(),  databaseType);
						createdRequirements.add(sharedRequirement);
					}
					else {
						// this req has already been imported. we need to update.
						targetRequirement = new Requirement(targetRequirementId, databaseType);
						System.out.println("srt : Target Requirement is " + targetRequirement.getRequirementFullTag());
						System.out.println("srt : Target Requirement Full path is is " + targetRequirement.getFolderPath() );
						
						// lets see if the user has permissions to update the req.
						if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
								+ targetRequirement.getFolderId()))){
							// the user does not have update permission on this requirement.
							noUpdatePermissions.add(sharedRequirement);
						}
						else {
							// lets update the requirement name, description and other standard values.

							// NOTE : Its CRITICAL that we use the shared requirement name and desc and not hte 
							// source req's name and desc. Source req's name and desc gives the latest name / desc of 
							// the source req. Shared req name and desc gives the published name / desc.
							String existingFolderPath = targetRequirement.getFolderPath();
							int existingFolderId = targetRequirement.getFolderId();
							
							targetRequirement = new Requirement(targetRequirement.getRequirementId(),
									sharedRequirement.getSourceRequirementName(),
									sharedRequirement.getSourceRequirementDescription(), 
									sourceRequirement.getRequirementPriority(), 
									user.getEmailId(), sourceRequirement.getRequirementPctComplete(),
									sourceRequirement.getRequirementExternalUrl(), user.getEmailId(),
									request,databaseType);
							targetRequirement.setFolderId(existingFolderId);
							targetRequirement.setFolderPath(existingFolderPath);
							
							
							// the user has update permission on the target Requirement.
							SharedRequirementUtil.updateTargetRequirement(sRT, 
									sharedRequirement, rT, 
									targetRequirementType, sourceProject, project, 
									sourceRequirement, targetRequirement, 
									user, securityProfile,  databaseType);
							
							sharedRequirement = new SharedRequirement(sharedRequirement.getSourceRequirementId(),sRRTBaselineId, project.getProjectId(),  databaseType);
							updatedRequirements.add(sharedRequirement);
						}
					}

					System.out.println("srt2 : Target Requirement is " + targetRequirement.getRequirementFullTag());
					System.out.println("srt 2: Target Requirement Full path is is " + targetRequirement.getFolderPath() );
					// lets ensure that the targetRequirement is in the correct folder. 
					// For example, after you import the requirement,  you realize that the 
					// source requirement has moved to a different folder.
					// we want to replicate that move here. 
					
					
					// lets find the correct folder for this target requirement
					String sourceFolderPath = sourceRequirement.getFolderPath();
					String targetFolderPath = targetRequirement.getFolderPath();
					// lets identify what the correctFolderPath should be.
					String correctFolderPath  = sourceFolderPath.replace(sRT.getRequirementType().getRequirementTypeName()  ,targetRequirementType.getRequirementTypeName());
					
					System.out.println("srt targetFolderPath is " + targetFolderPath );
					System.out.println("srt correctFolderPath is " + correctFolderPath );
					
					if (!(targetFolderPath.equals(correctFolderPath))){
						// we need to move this requirement to the correct folder path.
						Folder correctFolder = new Folder(correctFolderPath, targetRequirement.getProjectId() );
						System.out.println("srt . Trying to move " + targetRequirement.getRequirementFullTag() + " to correct folder " + correctFolderPath + " whose id is " + correctFolder.getFolderId() );  
						RequirementUtil.moveRequirementToAnotherFolder(targetRequirement, correctFolder.getFolderId(), user.getEmailId(), databaseType);
					}
					
				}
			}
	
    		String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
			SharedRequirementUtil.pushHistoryToTDCS(request, response,
				rootDataDirectory, sRT, targetRequirementType, createdRequirements, updatedRequirements, 
				project, user,  databaseType);

			session.setAttribute("sharedRequirementType", sRT);
			session.setAttribute("requirementType", rT);
			session.setAttribute("noUpdatePermissions", noUpdatePermissions);
			session.setAttribute("createdRequirements",createdRequirements);
			session.setAttribute("updatedRequirements", updatedRequirements);
		
			if (newTargetReqType){
				PrintWriter out = response.getWriter();
				// since we just created a new req type / folder, lets refresh the user's security profile
				// so he / she can access the folder.
				securityProfile = new SecurityProfile(user.getUserId(),this.getServletContext().getInitParameter("databaseType"));
				session.setAttribute("securityProfile", securityProfile);
	
				Folder newFolder = new Folder(targetRequirementType.getRootFolderId());
				out.println("{");
				out.println("\"folderId\" : \""+ newFolder.getFolderId() +"\",");
				out.println("\"folderName\" : \""+ newFolder.getFolderName() +"\",");
				out.println("\"folderDescription\" : \""+ newFolder.getFolderDescription() +"\",");
				out.println("\"parentFolderId\" : \""+ newFolder.getParentFolderId()+"\"");
				out.println("}");
			}
			return;
		}
	}
}
