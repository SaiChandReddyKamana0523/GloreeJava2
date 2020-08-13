package com.gloree.actions;

import com.gloree.utils.AssetUtil_tobe_deleted;
import com.gloree.beans.*;
import com.gloree.utils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class RequirementAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public RequirementAction() {
        super();
    
    }

    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost (request,response);
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String databaseType = this.getServletContext().getInitParameter("databaseType");
		String action = request.getParameter("action");
		

		
		// for this code , no security is required. let any one, without authentication create requirements in webform.
		if ( action.equals("createRequirementInWebForm")){
			
			///////////////////////////////////// SECURITY /////////////////////////////////
			// Any one can createRequirementsInWebForm, even without authentication, if the have
			// the correct URL. The idea is to let users who are not memebrs
			// of tracecloud come and create requirements in some special sub folders.
			//
			///////////////////////////////////// SECURITY /////////////////////////////////

			int webFormId = Integer.parseInt(request.getParameter("webFormId"));
			WebForm webForm = new WebForm(webFormId);
			
			

			int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			int projectId = Integer.parseInt(request.getParameter("projectId"));
			String parentFullTag = "";
			
			String requirementName = request.getParameter("requirementName").trim();
			String requirementDescription = request.getParameter("requirementDescription");
			String requirementPriority = request.getParameter("requirementPriority");
			String requirementOwner = request.getParameter("requirementOwner");
			String requirementPctCompleteString = request.getParameter("requirementPctComplete");
			

			String traceTo = request.getParameter("traceTo");

			String traceFrom = request.getParameter("traceFrom");
			
			// requirementDescription can not be empty, so if the richtext-stripped version is empty
			// lets use name for desc.
			String requirementDescriptionNoHTML = requirementDescription;
			if (requirementDescription != null) {
				requirementDescriptionNoHTML = requirementDescriptionNoHTML.replace("<br>","\n");
				requirementDescriptionNoHTML = requirementDescriptionNoHTML.replaceAll("\\<.*?>","");
				requirementDescriptionNoHTML = requirementDescriptionNoHTML.replaceAll("&nbsp;", " ");
			}
			else {
				requirementDescriptionNoHTML = "";
			}
			
			if (requirementDescriptionNoHTML.trim().equals("")){
				requirementDescription = requirementName;
			}
			
			 
			int requirementPctComplete = Integer.parseInt(requirementPctCompleteString);
			String requirementExternalUrl = request.getParameter("requirementExternalUrl");

			
			String requirementLockedBy = "";

			Requirement requirement = new Requirement(parentFullTag, requirementTypeId, folderId, projectId, 
				 requirementName, requirementDescription, 
				requirementPriority, requirementOwner, requirementLockedBy, requirementPctComplete,
				requirementExternalUrl, webForm.getDefaultOwner() , databaseType);
			
			
			
			// once the requirement is created, lets go and set the attributes. 
			int requirementId = requirement.getRequirementId();
			String attributeIdString = request.getParameter("attributeIdString");
			
			// attributeIdString has a string of attribute Ids and values in the following format.
			// id#value##id#value. eg : attributeIdString=2#DropDown##4#URL##3#Date##1#Text##
			// we need to get a list of attribute ids and get the request.getparameter values for these.
			// a typical URL looks like this : 
			//url is /GloreeJava2/servlet/RequirementAction?action=createAttributes&requirementId=1&attributeIdString=2#DropDown##4#URL
			// ##3#Date##1#Text##&2=good%20to%20have&4=external&3=datepromised&1=cost
			
			// we go through the list of RAttributeIds, get the RTAttribute for each of these
				
			String [] attributeStrings = attributeIdString.split("##");
			for (int i=0; i<attributeStrings.length; i++ ){
				
				String [] attribute = attributeStrings[i].split("#");
				// Note : id here is the id of the attribute in requirment type. we will be using it to create an attribute value.
				String id = attribute[0];
				
				
				if (id != null){
					String aValue = request.getParameter(id);
					if ((aValue != null ) && (!aValue.equals(""))){
						int attributeId = Integer.parseInt(id);
						
						// lets get the old Attribute Value object, and compare its value with the new value.
						// only if the value has changed, do we need to update the system and trigger
						// version, traceability and workflow.
						RAttributeValue oldAttributeValue = new RAttributeValue(requirementId, attributeId, databaseType);
						if (!(oldAttributeValue.getAttributeEnteredValue().trim().equals(aValue.trim()))) {
						
							// before we update, lets clean up by changing any ,, s to ,
							if (aValue.contains(",,")){
								aValue = aValue.replace(",," , ",");
							}
							if (aValue.endsWith(",")){
								aValue = (String) aValue.subSequence(0,aValue.lastIndexOf(","));
							}
							
							// NOTE : do not delete rAV . when you create this object, an entry is made in the db for this
							// attribute value. we may need a different way to implement this.
							RAttributeValue rAV  = new RAttributeValue(oldAttributeValue.getAttributeValueId(), aValue, databaseType, requirement.getRequirementOwner());
							
							String log = "Updated attribute " + rAV.getAttributeName() +" to " +  aValue;
							RequirementUtil.createRequirementLog(requirementId, log, webForm.getDefaultOwner(), databaseType);
						}
					}
				}
			}
			

			
			// NOTE : this is critical. After attributes are created / modified, call the setter
			// requirementUtil.setUserDefinedAttributes method.
			RequirementUtil.setUserDefinedAttributes(requirementId, webForm.getDefaultOwner(), databaseType);

			
			//if traceTo and traceFrom were sent in, lets create the traces
			if (traceTo == null){
				traceTo = "";
			}
			if (traceFrom == null){
				traceFrom = "";
			}
			User user = new User(requirementOwner, "mySQL");
			Project project = new Project(requirement.getProjectId(), "mySQL");
			SecurityProfile securityProfile = new SecurityProfile(user.getUserId(), "mySQL");

			String status = RequirementUtil.createTraces(project, requirement.getRequirementId(), 
				traceTo, traceFrom, project.getProjectId(), securityProfile,  databaseType);
			
			// if the webForm was configured to submit for approval,lets do so.

			RequirementType requirementType = new RequirementType(requirement.getRequirementTypeId());
			if (webForm.getSubmitForApprovalOnCreation() == 1){
				if (requirementType.getRequirementTypeEnableApproval() == 1){
					if (RequirementUtil.approversForRequirementExist(requirementId)){
						String serverName = request.getServerName();
	
						RequirementUtil.submitRequirementForApproval(requirementId,webForm.getDefaultOwner(),  databaseType, serverName);
					
					}
				}

			}
			response.sendRedirect("/GloreeJava2/jsp/Requirement/createWebFormRequirementConfirm.jsp?requirementId=" + requirementId + "&webFormId=" + webFormId);
			return;
		}

		
		
		// for this code , no security is required. let any one, without authentication create requirements in webform.
		if ( action.equals("validateTraceToInWebForm")){
			String output = "";
			int webFormId = Integer.parseInt(request.getParameter("webFormId"));
			WebForm webForm = new WebForm(webFormId);
			
			boolean canRead = false;
			HttpSession session = request.getSession(true);
			SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

			// if the user is logged or if the reader has read access to this folder,
			// then show validation.
			if ((webForm.getEnableLookup() == 1) 
					||
				(
					(securityProfile != null ) 
					&& 
					(securityProfile.getPrivileges().contains("readRequirementsInFolder" + webForm.getFolderId()))
				)
				){
							
				canRead = true;
			}

				
			try{
				int projectId = Integer.parseInt(request.getParameter("projectId"));
				
				String traceTo = request.getParameter("traceTo");
				String traceFrom = "";
				String status = RequirementUtil.validatePotentialTracesUnSecured(traceTo, traceFrom, projectId, canRead);
				
				if ((status != null) && (!status.equals(""))){
					output = status ;
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		
			
			PrintWriter out = response.getWriter();
		    out.println(output);
		    return;	
		}
		if ( action.equals("validateTraceFromInWebForm")){
			String output = "";
			int webFormId = Integer.parseInt(request.getParameter("webFormId"));
			WebForm webForm = new WebForm(webFormId);
			
			boolean canRead = false;
			HttpSession session = request.getSession(true);
			SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
			
			// if the user is logged or if the reader has read access to this folder,
			// then show validation. 
			if ((webForm.getEnableLookup() == 1) 
					||
				(
					(securityProfile != null ) 
					&& 
					(securityProfile.getPrivileges().contains("readRequirementsInFolder" + webForm.getFolderId()))
				)
				){
							
				canRead = true;
			}
			
			try{
				int projectId = Integer.parseInt(request.getParameter("projectId"));
				
				String traceFrom = request.getParameter("traceFrom");
				String traceTo = "";
				String status = RequirementUtil.validatePotentialTracesUnSecured(traceTo, traceFrom, projectId, canRead);
	
				if ((status != null) && (!status.equals(""))){
					output = status ;
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
			PrintWriter out = response.getWriter();
		    out.println(output);
		    return;	
		}
		if ( action.equals("createRequirementInMobileForm")){
			///////////////////////////////////// SECURITY /////////////////////////////////
			// Because this is an Mobile REST API call, and there will be a lot of them
			// and since we aren't holding security profile in a session, we don't want to 
			// create a security profile for each call. 
			// So, we want to have a minimal just to make sure that they can do the action they are supposed to. 
			//
			///////////////////////////////////// SECURITY /////////////////////////////////
			// Doe the rest key have the permissions to create a requirement in this folder???
			//
			
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			Folder folder = new Folder(folderId);
			
			String key = request.getParameter("key");
			// lets check if this key has create permissions on this folder.
			boolean canCreate = RoleUtil.canCreateObjects(key, folderId);
			if (!canCreate){
				String outputString = "You do not have permssions to create objects in this folder";
				
				 PrintWriter out = response.getWriter();
			     out.println(outputString);

				return;
			}
		
			User user = new User (key, true);
			int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
			int projectId = Integer.parseInt(request.getParameter("projectId"));
			String parentFullTag = "";
			
			String requirementName = request.getParameter("requirementName").trim();
			String requirementDescription = request.getParameter("requirementDescription");
			String requirementPriority = request.getParameter("requirementPriority");
			String requirementOwner = request.getParameter("requirementOwner");
			String requirementPctCompleteString = request.getParameter("requirementPctComplete");
			
			// requirementDescription can not be empty, so if the richtext-stripped version is empty
			// lets use name for desc.
			String requirementDescriptionNoHTML = requirementDescription;
			if (requirementDescription != null) {
				requirementDescriptionNoHTML = requirementDescriptionNoHTML.replace("<br>","\n");
				requirementDescriptionNoHTML = requirementDescriptionNoHTML.replaceAll("\\<.*?>","");
				requirementDescriptionNoHTML = requirementDescriptionNoHTML.replaceAll("&nbsp;", " ");
			}
			else {
				requirementDescriptionNoHTML = "";
			}
			
			if (requirementDescriptionNoHTML.trim().equals("")){
				requirementDescription = requirementName;
			}
			
			 
			int requirementPctComplete = Integer.parseInt(requirementPctCompleteString);
			String requirementExternalUrl = request.getParameter("requirementExternalUrl");

			
			String requirementLockedBy = "";

			Requirement requirement = new Requirement(parentFullTag, requirementTypeId, folderId, projectId, 
				 requirementName, requirementDescription, 
				requirementPriority, requirementOwner, requirementLockedBy, requirementPctComplete,
				requirementExternalUrl, user.getEmailId(), databaseType);
			
			
			
			// once the requirement is created, lets go and set the attributes. 
			int requirementId = requirement.getRequirementId();
			String attributeIdString = request.getParameter("attributeIdString");
			// attributeIdString has a string of attribute Ids and values in the following format.
			// id#value##id#value. eg : attributeIdString=2#DropDown##4#URL##3#Date##1#Text##
			// we need to get a list of attribute ids and get the request.getparameter values for these.
			// a typical URL looks like this : 
			//url is /GloreeJava2/servlet/RequirementAction?action=createAttributes&requirementId=1&attributeIdString=2#DropDown##4#URL
			// ##3#Date##1#Text##&2=good%20to%20have&4=external&3=datepromised&1=cost
			
			// we go through the list of RAttributeIds, get the RTAttribute for each of these
				
			String [] attributeStrings = attributeIdString.split("##");
			for (int i=0; i<attributeStrings.length; i++ ){
				
				String [] attribute = attributeStrings[i].split("#");
				// Note : id here is the id of the attribute in requirment type. we will be using it to create an attribute value.
				String id = attribute[0];
				
				
				if (id != null){
					String aValue = request.getParameter(id);
					if ((aValue != null ) && (!aValue.equals(""))){
						int attributeId = Integer.parseInt(id);
						
						// lets get the old Attribute Value object, and compare its value with the new value.
						// only if the value has changed, do we need to update the system and trigger
						// version, traceability and workflow.
						RAttributeValue oldAttributeValue = new RAttributeValue(requirementId, attributeId, databaseType);
						if (!(oldAttributeValue.getAttributeEnteredValue().trim().equals(aValue.trim()))) {
						
							// before we update, lets clean up by changing any ,, s to ,
							if (aValue.contains(",,")){
								aValue = aValue.replace(",," , ",");
							}
							if (aValue.endsWith(",")){
								aValue = (String) aValue.subSequence(0,aValue.lastIndexOf(","));
							}
							
							// NOTE : do not delete rAV . when you create this object, an entry is made in the db for this
							// attribute value. we may need a different way to implement this.
							RAttributeValue rAV  = new RAttributeValue(oldAttributeValue.getAttributeValueId(), aValue, databaseType, requirement.getRequirementOwner());
							
							String log = "Updated attribute " + rAV.getAttributeName() +" to " +  aValue;
							RequirementUtil.createRequirementLog(requirementId, log, user.getEmailId(), databaseType);
						}
					}
				}
			}
			

			
			// NOTE : this is critical. After attributes are created / modified, call the setter
			// requirementUtil.setUserDefinedAttributes method.
			RequirementUtil.setUserDefinedAttributes(requirementId, user.getEmailId(), databaseType);

			// if the webForm was configured to submit for approval,lets do so.

			String outputString = Integer.toString(requirement.getRequirementId());
					
			
			 PrintWriter out = response.getWriter();
		     out.println(outputString);
		     return;
		}
		
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
		
		
		
		
		
		
		
		// temp srt
		/*try {
	        response.setContentType("text/html;charset=UTF-8");
	        PrintWriter out1 = response.getWriter();
	        StringBuilder stBuffer = new StringBuilder();
	        long lStartTime = System.currentTimeMillis();
	        String strDebugString = request.getRemoteAddr();
	        if (request.getInputStream() == null ){
	        	System.out.println("Srt input stream is null ");
	        }
			BufferedReader bfReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String strLine = null;
			while((strLine = bfReader.readLine()) != null) {
				stBuffer.append(strLine);
				if(stBuffer.length() > 5000) {
					log(strDebugString + " received data more than 5000chars ");
					break;
				}
			}
			log("REQUEST RCVD:" + stBuffer);
			log(stBuffer.toString() + ",   URL DECODED=" + java.net.URLDecoder.decode(stBuffer.toString(),"UTF-8"));
			out1.write(stBuffer.toString() + ",   URL DECODED=" + java.net.URLDecoder.decode(stBuffer.toString(),"UTF-8"));		    
		    
			System.out.println("REQUEST RCVD:" + stBuffer);
				System.out.println(stBuffer.toString() + ",   URL DECODED=" + java.net.URLDecoder.decode(stBuffer.toString(),"UTF-8"));
			System.out.println(stBuffer.toString() + ",   URL DECODED=" + java.net.URLDecoder.decode(stBuffer.toString(),"UTF-8"));		    
		}
		catch (Exception e){
			e.printStackTrace();
		}
		*/
		// temp srt
		
		
		
		
		
		
		
		
		
		
		
		
		// now check if this users should be in this project.
		HttpSession session = request.getSession(true);
		
					

		SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
		
		User user = securityProfile.getUser();
		///////////////////////////////SECURITY//////////////////////////////
		
		
		if (action.equals("castVote")){
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));

			int vote = Integer.parseInt(request.getParameter("vote"));
			
			Requirement requirement = new Requirement(requirementId, databaseType);
			VoteUtil.castVote(user, requirement, vote);	
			
			return;
		}

		
		// For some actions (MyTasks etc..), where the user hasn't yet selected a project
		// we won't know the project. In this case we get the project from the input requirement.
		if (action.equals("approvalWorkFlowActionForMyTasks")){
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			
			Requirement requirement = new Requirement(requirementId, databaseType);
			// we do this work , ONLY if the user is a Pending approver or a rejected in the list.
			if ((requirement.getApprovers().contains( user.getEmailId()))){
				String approvalAction  = request.getParameter("approvalAction");
				String approvalNote = request.getParameter("approvalNote");
				RequirementUtil.approvalWorkFlowAction(requirementId, approvalAction, approvalNote, user, request, databaseType);
				
				if (approvalAction.equals("reject")){
					PrintWriter out = response.getWriter();
				   out.println("<span class='normalText'> <font color='red'><b> Rejected </b></font></span>");
				}
				else {
					PrintWriter out = response.getWriter();
				   out.println("<span class='normalText'> <font color='green'><b> Approved </b></font></span>");

				}
				
			}
			return;
		}

		
		if (action.equals("clearAllSuspectUpStream")){
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			Requirement requirement = new Requirement(requirementId, databaseType);
			// we do this work , ONLY if the user has TraceFrom permissions
			if (securityProfile.getPrivileges().contains("traceFromRequirementsInFolder" + requirement.getFolderId())){
				RequirementUtil.clearSuspectTraceTo(requirementId, user.getEmailId(), securityProfile,  databaseType);
				PrintWriter out = response.getWriter();
	    		out.println("<span class='normalText'> <font color='green'><b> Cleared all Upstream Traces </b></font></span>");
			}
			return;
		}
		
		if (action.equals("clearAllSuspectDownStream")){
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			Requirement requirement = new Requirement(requirementId, databaseType);
			// we do this work , ONLY if the user has TraceTo permissions
			if (securityProfile.getPrivileges().contains("traceToRequirementsInFolder" + requirement.getFolderId())){
				RequirementUtil.clearSuspectTraceFrom(requirementId, user.getEmailId(), securityProfile,  databaseType);
				PrintWriter out = response.getWriter();
	    		out.println("<span class='normalText'> <font color='green'><b> Cleared all Downstream Traces </b></font></span>");
			}
			return;
		}

		if (action.equals("addRequirementCommentForMyTask")){
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			
			Requirement requirement = new Requirement(requirementId, databaseType);
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			String comment_note = request.getParameter("comment_note");
			
			if (securityProfile.getPrivileges().contains("readRequirementsInFolder" + requirement.getFolderId())){
				RequirementUtil.createComment(requirementId,user,comment_note, request, databaseType);
				
				// forward to displayRequirementComment
				RequestDispatcher dispatcher =	request.getRequestDispatcher(
					"/jsp/Requirement/displayRequirementCommentsForMyTasks.jsp?requirementId=" + 
					+ requirementId + "&folderId=" + folderId);
					dispatcher.forward(request, response);
			}
			return;
		}			
		
		if (action.equals("remindApprovers")){
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			
			Requirement requirement = new Requirement(requirementId, databaseType);
			if (securityProfile.getPrivileges().contains("readRequirementsInFolder" + requirement.getFolderId())){
				String serverName = request.getServerName();
				RequirementUtil.refreshRequirementApprovalHistory(requirementId,  databaseType);
				RequirementUtil.remindPendingApprovers(requirementId,  databaseType, serverName);
			}
			return;
		}			
		
		
		////////////////////////////////////////////////////////////////////////
		// For actions below this line, it's expected that the 'project' is 
		// already loaded into the memory
		///////////////////////////////////////////////////////////////////////
		
		
		Project project = (Project) session.getAttribute("project");
		if (!(securityProfile.getRoles().contains("MemberInProject" + project.getProjectId()))){
			//User is NOT a member of this project. so do nothing and return.
			return;
		}

		
		
		if ( action.equals("validateRequirementOwner")){
			String requirementOwner = request.getParameter("requirementOwner");
			PrintWriter out = response.getWriter();
			if (!(ProjectUtil.isValidUserInProject(requirementOwner, project))){
			    out.println("<span class='normalText'> <font color='red'><b>Invalid User</b></font></span>");
			    return;
			}
			else 
			    out.println("<span class='normalText'> Valid User</span>");
		    return;
		}
		
		if ( action.equals("createRequirement")){
			

			int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			int projectId = Integer.parseInt(request.getParameter("projectId"));
			String parentFullTag = request.getParameter("parentFullTag");
			
			
			String requirementName = request.getParameter("requirementName").trim();
			String requirementDescription = request.getParameter("requirementDescription");
			String requirementPriority = request.getParameter("requirementPriority");
			String requirementOwner = request.getParameter("requirementOwner");
			String requirementPctCompleteString = request.getParameter("requirementPctComplete");
			
			/*
			// requirementDescription can not be empty, so if the richtext-stripped version is empty
			// lets use name for desc.
			String requirementDescriptionNoHTML = requirementDescription;
			if (requirementDescription != null) {
				requirementDescriptionNoHTML = requirementDescriptionNoHTML.replace("<br>","\n");
				requirementDescriptionNoHTML = requirementDescriptionNoHTML.replaceAll("\\<.*?>","");
				requirementDescriptionNoHTML = requirementDescriptionNoHTML.replaceAll("&nbsp;", " ");
			}
			else {
				requirementDescriptionNoHTML = "";
			}
			
			if (requirementDescriptionNoHTML.trim().equals("")){
				requirementDescription = requirementName;
			}
			*/
			if (requirementDescription==null){
				requirementDescription = "";
			}
			 
			int requirementPctComplete = Integer.parseInt(requirementPctCompleteString);
			String requirementExternalUrl = request.getParameter("requirementExternalUrl");
			if (!(ProjectUtil.isValidUserInProject(requirementOwner, project))){
				// if the given req owner is not a member of this project
				// we replace it with the creator of this req.
				requirementOwner = user.getEmailId();
			}

			
			Requirement parent = new Requirement(parentFullTag, project.getProjectId(), databaseType);

			
			if ((parentFullTag != null) && (!parentFullTag.equals(""))){
				// parent full tag is given. so lets make sure its valid.
				
				Folder folder = new Folder(folderId);
				String errorOutput = "";
				if (parent.getRequirementId() == 0  ){
					errorOutput = "<div class='alert alert-success'> <span class='normalText'><font color='red'>Error : This Requirement <b>"+ 
					parentFullTag + 
					"</b>does not exist in this project.</font></span></div>";
				}
				else if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
						+ parent.getFolderId()))){
					errorOutput = "<div class='alert alert-success'><span class='normalText'><font color='red'>Error : You do not have Update permissions on <b>"+ 
						parentFullTag + 
						"</b> . Hence you can not create a child to this Requirement.</span></div>";
					
				}
				else if (folder.getRequirementTypeId() != parent.getRequirementTypeId()){
					errorOutput = "<span class='normalText'><font color='red'>Error : <b>"+ parentFullTag +
						"</b> belongs to a different Requirement Type." +
					" </font></span>";
				}
	
				if (!(errorOutput.equals(""))){
					PrintWriter out = response.getWriter();
				    out.println(errorOutput);
				    return;
				}
				
				
			}
			String requirementLockedBy = "";
			if (parentFullTag == null ){
				parentFullTag = "";
			}
			
			
			Requirement requirement = new Requirement(parentFullTag, requirementTypeId, folderId, projectId, 
				 requirementName, requirementDescription, 
				requirementPriority, requirementOwner, requirementLockedBy, requirementPctComplete,
				requirementExternalUrl, user.getEmailId() , databaseType);
			
			String cloneParentAttributes = request.getParameter("cloneParentAttributes");
			if ((cloneParentAttributes != null) && (cloneParentAttributes.equals("yes"))){
				RequirementUtil.cloneParentAttributes(requirement,  databaseType);
			}
			
			
			
			
			
			
			
			
			
			
			// once the requirement is created, lets go and set the attributes. 
			int requirementId = requirement.getRequirementId();
			String attributeIdString = request.getParameter("attributeIdString");
			// attributeIdString has a string of attribute Ids and values in the following format.
			// id#value##id#value. eg : attributeIdString=2#DropDown##4#URL##3#Date##1#Text##
			// we need to get a list of attribute ids and get the request.getparameter values for these.
			// a typical URL looks like this : 
			//url is /GloreeJava2/servlet/RequirementAction?action=createAttributes&requirementId=1&attributeIdString=2#DropDown##4#URL
			// ##3#Date##1#Text##&2=good%20to%20have&4=external&3=datepromised&1=cost
			
			// we go through the list of RAttributeIds, get the RTAttribute for each of these
				
			String [] attributeStrings = attributeIdString.split("##");
			for (int i=0; i<attributeStrings.length; i++ ){
				
				String [] attribute = attributeStrings[i].split("#");
				// Note : id here is the id of the attribute in requirment type. we will be using it to create an attribute value.
				String id = attribute[0];
				
				
				if (id != null){
					String aValue = request.getParameter(id);
					if ((aValue != null ) && (!aValue.equals(""))){
						int attributeId = Integer.parseInt(id);
						
						// lets get the old Attribute Value object, and compare its value with the new value.
						// only if the value has changed, do we need to update the system and trigger
						// version, traceability and workflow.
						RAttributeValue oldAttributeValue = new RAttributeValue(requirementId, attributeId, databaseType);
						if (!(oldAttributeValue.getAttributeEnteredValue().trim().equals(aValue.trim()))) {
						
							// before we update, lets clean up by changing any ,, s to ,
							if (aValue.contains(",,")){
								aValue = aValue.replace(",," , ",");
							}
							if (aValue.endsWith(",")){
								aValue = (String) aValue.subSequence(0,aValue.lastIndexOf(","));
							}
							
							// NOTE : do not delete rAV . when you create this object, an entry is made in the db for this
							// attribute value. we may need a different way to implement this.
							RAttributeValue rAV  = new RAttributeValue(oldAttributeValue.getAttributeValueId(), aValue, databaseType, user.getEmailId());
							
							String log = "Updated attribute " + rAV.getAttributeName() +" to " +  aValue;
							RequirementUtil.createRequirementLog(requirementId, log, user.getEmailId(), databaseType);
						}
					}
				}
			}
			

			
			// NOTE : this is critical. After attributes are created / modified, call the setter
			// requirementUtil.setUserDefinedAttributes method.
			RequirementUtil.setUserDefinedAttributes(requirementId, user.getEmailId(), databaseType);

			
			
			

			// AssetJava2Customizations
			AssetUtil_tobe_deleted.setAssetJavaCustomizations(requirement, request, user);			
			
			
			

			
			request.setAttribute("requirement", requirement);
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Requirement/returnRequirementId.jsp");
			dispatcher.forward(request, response);
			return;
		}
		else if ( action.equals("createBulkRequirements")){
			ArrayList errorMessages = new ArrayList();
			ArrayList createdRequirements = new ArrayList();
			int currentFolderId = Integer.parseInt(request.getParameter("currentFolderId"));
			// lets get the input params.
			try {
				// lets get the info needed to create a req for each of these rows.
				int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
				RequirementType requirementType = new RequirementType(requirementTypeId);
				ArrayList rTAttributes = requirementType.getAllAttributesInRequirementType();
				int projectId = Integer.parseInt(request.getParameter("projectId"));
				
				int requirementNameSeqStart = 0;
				int requirementDescriptionSeqStart = Integer.parseInt(request.getParameter("requirementDescriptionSeqStart"));
				int requirementPrioritySeqStart = Integer.parseInt(request.getParameter("requirementPrioritySeqStart"));
				int requirementParentSeqStart = Integer.parseInt(request.getParameter("requirementParentSeqStart"));
				int requirementOwnerSeqStart = Integer.parseInt(request.getParameter("requirementOwnerSeqStart"));
				int requirementPctCompleteSeqStart = Integer.parseInt(request.getParameter("requirementPctCompleteSeqStart"));
				int requirementExternalURLSeqStart = Integer.parseInt(request.getParameter("requirementExternalURLSeqStart"));
				int requirementTraceToSeqStart = Integer.parseInt(request.getParameter("requirementTraceToSeqStart"));
				int requirementTraceFromSeqStart = Integer.parseInt(request.getParameter("requirementTraceFromSeqStart"));
				//int requirementFolderSeqStart = Integer.parseInt(request.getParameter("requirementFolderSeqStart"));
				
				
				// since the form can send only 10 rows, lets look for those attribs.
				for (int i=0; i<10; i++){
					String errorMessage = "";
					
					// lets get the requirement name.
					int nameRowId = requirementNameSeqStart + i;
					String requirementName = request.getParameter("requirementName" + nameRowId);
					if ((requirementName == null) || (requirementName.equals(""))){
						// we don't need to process this row.
						continue;
					}
							
					// lets get the folderId
					/*int folderRowId = requirementFolderSeqStart + i;
					int folderId ;
					try {
						folderId = Integer.parseInt(request.getParameter("requirementFolder" + folderRowId));
					}
					catch (Exception e){
						folderId = currentFolderId;
					}
					Folder folder = new Folder(folderId);
					*/
					int folderId = currentFolderId;
					Folder folder = new Folder(currentFolderId);
					// lets see if the user has permissions to create reqs in this folder.
					if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" 
							+ folderId))){
						errorMessage += "<br> You do not have Create Permissions on the folder " + folder.getFolderName() +
						 " for Requirement '" + requirementName + "' at Row #" + (i+1);
					}
					
					
					int descriptionRowId = requirementDescriptionSeqStart + i;
					String requirementDescription = request.getParameter("requirementDescription" + descriptionRowId);
					if ((requirementDescription == null) || (requirementDescription.equals(""))){
						requirementDescription = requirementName;
					}
						
					int priorityRowId = requirementPrioritySeqStart + i;
					String requirementPriority = request.getParameter("requirementPriority" + priorityRowId);
					if ((requirementPriority == null) || (requirementPriority.equals(""))){
						requirementPriority = "Medium";
					}
					

					int parentRowId = requirementParentSeqStart + i;
					String parentFullTag = request.getParameter("requirementParent" + parentRowId);
					String inputParentFullTag = parentFullTag;
					if ((parentFullTag != null) && (!parentFullTag.equals(""))){
						// this step validates that the req actually exists.
						try {
							Requirement parent = new Requirement(parentFullTag, project.getProjectId() , databaseType);
							if ((parent==null) || (parent.getRequirementFullTag() == null) || (parent.getRequirementFullTag().equals(""))){
								errorMessage += " This Requirement " + inputParentFullTag +
								 " does not exist in this Project. Hence creating '"+ requirementName + "' as an independent Requirement" ;
								
							}
							parentFullTag = parent.getRequirementFullTag();
							
							// if the parent req is in a different folder than the child req, not permitted.
							if ((parentFullTag != null) && (!parentFullTag.equals(""))){
								if (parent.getFolderId() != currentFolderId){
									errorMessage +=  parentFullTag +" belongs to a different Folder. " +
									"Hence creating '"+ requirementName + "' as an independent Requirement";
									
									parentFullTag = "";
								}
							}
						}
						catch (Exception e){
							parentFullTag = "";
						}
					}
					if (parentFullTag == null) {
						parentFullTag = "";
					}
					
					
					
					int ownerRowId = requirementOwnerSeqStart + i;
					String requirementOwner = request.getParameter("requirementOwner" + ownerRowId);
					if ((requirementOwner == null) || (requirementOwner.equals(""))){
						requirementOwner = user.getEmailId();
					}
					
					if (!(ProjectUtil.isValidUserInProject(requirementOwner, project))){
						// if the given req owner is not a member of this project
						// we replace it with the creator of this req.
						requirementOwner = user.getEmailId();
					}

					
					int pctCompleteRowId = requirementPctCompleteSeqStart + i;
					int requirementPctComplete ;
					try {
						requirementPctComplete = Integer.parseInt(request.getParameter("requirementPctComplete" + pctCompleteRowId));
					}
					catch (Exception e){
						requirementPctComplete = 0;
					}
					if ((requirementPctComplete < 0) || (requirementPctComplete > 100)){
						requirementPctComplete = 0;
					}
					


					
					int externalURLRowId = requirementExternalURLSeqStart + i;
					String requirementExternalURL = request.getParameter("requirementExternalURL" + externalURLRowId);
					
					String requirementLockedBy = "";
					Requirement requirement = new Requirement(parentFullTag, requirementTypeId, folderId, projectId, 
			 		 requirementName, requirementDescription, 
						requirementPriority, requirementOwner, requirementLockedBy, requirementPctComplete,
						requirementExternalURL, user.getEmailId() , databaseType);
					
					createdRequirements.add(requirement);
		
					
					// lets iterate through all the attributes of this req type and if any are given , lets set them
					// for this requirement.
					Iterator rTAs = rTAttributes.iterator();
					while (rTAs.hasNext()){
						RTAttribute rTAttribute = (RTAttribute) rTAs.next();
						
						String attributeSeqString = rTAttribute.getAttributeName() + "SeqStart" ;	
						int attributeSeqStart = Integer.parseInt(request.getParameter(attributeSeqString));
						int attributeRowId  = attributeSeqStart + i;
						
						String attributeValue = "";
						
						System.out.println("srt attribute type for attribute " + rTAttribute.getAttributeName()+ " is  " + rTAttribute.getAttributeType()); 
						try {
							if (rTAttribute.getAttributeType().equals("Drop Down Multiple")){
								String[] attribValues = request.getParameterValues(rTAttribute.getAttributeName() + attributeRowId);
								
								for(String aV : attribValues){
									attributeValue += aV + ",";
								}
							}
							else {
								 attributeValue = request.getParameter(rTAttribute.getAttributeName() + attributeRowId);
							}
						}
						catch (Exception e){
							e.printStackTrace();
						}
						System.out.println("srt about to custom attribute pram value . attribute is " + rTAttribute.getAttributeName() + attributeRowId + 
								" attribute value is " + attributeValue);
						if ((attributeValue != null) && (!attributeValue.equals(""))){
							System.out.println("srt about to set custom value for req " + requirement.getRequirementFullTag() + 
									" attribute " + rTAttribute.getAttributeName() +  "  to value " + attributeValue);
							requirement.setCustomAttributeValue(rTAttribute.getAttributeId(), attributeValue, user, databaseType);
						}
					}
					
					// lets see if there are any trace from and trace to strings.
					int traceToRowId = requirementTraceToSeqStart + i;
					String requirementTraceTo = request.getParameter("requirementTraceTo" + traceToRowId);
					if (requirementTraceTo == null){
						requirementTraceTo = "";
					}
					
					int traceFromRowId = requirementTraceFromSeqStart + i;
					String requirementTraceFrom = request.getParameter("requirementTraceFrom" + traceFromRowId);
					if (requirementTraceFrom == null){
						requirementTraceFrom = "";
					}
					
					String status = RequirementUtil.createTraces(project, requirement.getRequirementId(), 
						requirementTraceTo, requirementTraceFrom, project.getProjectId(), securityProfile,  databaseType);
					
					if ((status != null) && (!status.equals(""))){
						
						errorMessage += "<div><table><tr><td colspan='2'>Error in Tracing in Req "+ requirement.getRequirementFullTag() +
						" - </td></tr><tr><td width='30'>&nbsp;</td><td>" + status + "</td></tr></table></div>";
					}
					if ((errorMessage != null) && (!errorMessage.equals(""))){
						errorMessages.add(errorMessage);
					}
				}
				
			}
			catch (Exception e){
				e.printStackTrace();
			}
			session.setAttribute("createdRequirements", createdRequirements);
			session.setAttribute("errorMessages", errorMessages);
			request.setAttribute("folderId", Integer.toString(currentFolderId));
			request.setAttribute("targetPage", "createBulkRequirementsMessage");
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/OpenProject/yP.jsp");
			dispatcher.forward(request, response);
			return;
		}
		else if ( action.equals("editRequirement")){
			
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			Requirement requirement = new Requirement(requirementId, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////

			String requirementName = request.getParameter("requirementName");
			String requirementDescription = request.getParameter("requirementDescription");
			String requirementPriority = request.getParameter("requirementPriority");
			String requirementOwner = request.getParameter("requirementOwner");
			int requirementPctComplete = Integer.parseInt(request.getParameter("requirementPctComplete"));
			String requirementExternalUrl = request.getParameter("requirementExternalUrl");
			
			// requirementDescription can not be empty, so if the richtext-stripped version is empty
			// lets use name for desc.
			String requirementDescriptionNoHTML = requirementDescription;
			if (requirementDescription != null) {
				requirementDescriptionNoHTML = requirementDescriptionNoHTML.replace("<br>","\n");
				requirementDescriptionNoHTML = requirementDescriptionNoHTML.replaceAll("\\<.*?>","");
				requirementDescriptionNoHTML = requirementDescriptionNoHTML.replaceAll("&nbsp;", " ");
			}
			else {
				requirementDescriptionNoHTML = "";
			}
			
			if (requirementDescriptionNoHTML.trim().equals("")){
				requirementDescription = requirementName;
			}
			
			
			// At some point once we get user authentication, you may want to send in created by, last modified by.
			requirement = new Requirement( requirementId, requirementName, 
				requirementDescription,requirementPriority, requirementOwner, 
				requirementPctComplete, requirementExternalUrl, user.getEmailId() , request, databaseType);
			
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Requirement/displayRequirementCore.jsp");
			dispatcher.forward(request, response);
			return;
		}
		else if ( action.equals("createNewGlossaryItem")){
			
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			Requirement requirement = new Requirement(requirementId, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////

			String glossaryName = request.getParameter("glossaryName");
			String glossaryDescription = request.getParameter("glossaryDescription");
			
			String requirementPriority = "Medium";
			String requirementOwner = user.getEmailId();
			int requirementPctComplete = 0;
			String requirementExternalUrl = "";
			
			// requirementDescription can not be empty, so if the richtext-stripped version is empty
			// lets use name for desc.
			String glossaryDescriptionNoHTML = glossaryDescription;
			if (glossaryDescription != null) {
				glossaryDescriptionNoHTML = glossaryDescriptionNoHTML.replace("<br>","\n");
				glossaryDescriptionNoHTML = glossaryDescriptionNoHTML.replaceAll("\\<.*?>","");
				glossaryDescriptionNoHTML = glossaryDescriptionNoHTML.replaceAll("&nbsp;", " ");
			}
			else {
				glossaryDescriptionNoHTML = "";
			}
			
			if (glossaryDescriptionNoHTML.trim().equals("")){
				glossaryDescription = glossaryName;
			}
			
			String requirementLockedBy = "";
			String parentFullTag = "";
			
			RequirementType glossaryRT = new RequirementType(project.getProjectId(), "GL", user.getEmailId());
			// At some point once we get user authentication, you may want to send in created by, last modified by.
			Requirement glossary = new Requirement(parentFullTag, glossaryRT.getRequirementTypeId(), glossaryRT.getRootFolderId(), requirement.getProjectId(), 
					 glossaryName, glossaryDescription, 
					requirementPriority, requirementOwner, requirementLockedBy, requirementPctComplete,
					requirementExternalUrl, requirementOwner , databaseType);
		
			// once we create the glossary term, lets refreh this requirement's glossary
			requirement.refreshRequirementGlossary();
			return;
		}
		else if ( action.equals("rollbackVersion")){
			
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			Requirement requirement = new Requirement(requirementId, databaseType);
			
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			
			if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
					+ requirement.getFolderId()))){
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////


			int targetVersionNumber  = Integer.parseInt(request.getParameter("targetVersion"));
			RequirementVersion targetVersion = RequirementUtil.getRequirementVersion(requirementId, targetVersionNumber);
			
			
			String requirementName = targetVersion.getVersionName();
			String requirementDescription = targetVersion.getVersionDescription();
			String requirementPriority = targetVersion.getVersionPriority();
			String requirementOwner = targetVersion.getVersionOwner();
			int requirementPctComplete = targetVersion.getVersionPctComplete();
			String requirementExternalUrl = targetVersion.getVersionExternalURL();
			
			
			try {
				if (
						(requirement.getRequirementName()!= targetVersion.getVersionName() )
						||
						(requirement.getRequirementDescription() != targetVersion.getVersionDescription())
						){
							requirement = new Requirement( requirementId, requirementName, 
							requirementDescription,requirementPriority, requirementOwner, 
							requirementPctComplete, requirementExternalUrl, user.getEmailId() , request, databaseType);
						
				}
				// see if old attribute values are different from new attribute values
				// if so, split current attribute values and iterate through them
				// for each one that's different , set the attribute value.
				if(requirement.getUserDefinedAttributes() != targetVersion.getVersionUserDefinedAttributes() ){
					//lets iterate through , find the ones that are different and try to update.
					ArrayList uda = requirement.getUserDefinedAttributesArrayList();
					Iterator iUDA = uda.iterator();
					while (iUDA.hasNext()){
						RAttributeValue currentRAV = (RAttributeValue) iUDA.next();
						// lets see if the currentRAV is same as the Target user defined attributes
						String targetUDA = targetVersion.getVersionUserDefinedAttributes();
						if (targetUDA == null ) {
							targetUDA = "";
						}
						String[] attribs = targetUDA.split(":##:");
						for(int i=0; i<attribs.length; i++){
							String attribute = attribs[i];
							String[] valuePair = attribute.split(":#:");
								String label = valuePair[0];
								String value = valuePair[1];
						}
						
					}
				}
					
			}
			catch (Exception e){
				e.printStackTrace();
			}
			
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Requirement/displayRequirementCore.jsp");
			dispatcher.forward(request, response);
			return;
		}

		else if ( action.equals("purgeRequirement")){
			
			// when requirement gets purged, it's log entries also get deleted
			// so we need not log the purge effort.
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			
			Requirement requirement = new Requirement(requirementId, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////

			

			ProjectUtil.purgeRequirement(requirementId,  databaseType);
			ProjectUtil.createProjectLog(requirement.getProjectId(), requirement.getRequirementFullTag(), "Purge",
					"Purging Requirement : " + requirement.getRequirementFullTag() + " : " + requirement.getRequirementName(), user.getEmailId(),  databaseType);
			
			
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Requirement/requirementPurgedConfirmation.jsp");
			dispatcher.forward(request, response);
			return;
		}
		else if ( action.equals("deleteRequirement")){
			
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));

			Requirement requirement = new Requirement(requirementId, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////

			
			
			ProjectUtil.deleteRequirement(requirementId, user.getEmailId(), databaseType);
			
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Requirement/requirementDeletedConfirmation.jsp");
			dispatcher.forward(request, response);
			return;
		}
		else if ( action.equals("restoreRequirement")){
			
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			
			Requirement requirement = new Requirement(requirementId, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////

			ProjectUtil.restoreRequirement(requirementId, user.getEmailId(),  databaseType);
			
			// we want to display a successfully restored message on the displayRequirementCore page.
			// so setting an attribute.
			request.setAttribute("restored", "restored");
			
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Requirement/displayRequirementCore.jsp");
			dispatcher.forward(request, response);
			return;
		}
				
		else if ( action.equals("lockRequirement")){
			
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));

			Requirement requirement = new Requirement(requirementId, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			
			if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
					+ requirement.getFolderId()))){
				return;
			}
			
			if (
				(!(requirement.getRequirementLockedBy().equals("")))
				&&
				(!(requirement.getRequirementLockedBy().equals(user.getEmailId())))
				){
				
				// this requirement is locked by someone else already. so can not be locked.
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////

			requirement.setLockedBy(user.getEmailId(), databaseType);
			
			return;
		}			
		
		else if ( action.equals("unlockRequirement")){
			
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));

			Requirement requirement = new Requirement(requirementId, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			
			if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
					+ requirement.getFolderId()))){
				return;
			}
			
			// unlocking can only be done by an admin or by the person who locked the requirement.
			if (
					(securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))
					||
					(requirement.getRequirementLockedBy().equals(user.getEmailId()))
				)
					{
				
				requirement.setUnlockedBy(user.getEmailId(), databaseType);
				return;
				
			}
			///////////////////////////////SECURITY CODE ////////////////////////////
			return;
			
		}
		else if (action.equals("createTraces")){
			String createTraceTo = request.getParameter("createTraceTo");
			String createTraceFrom = request.getParameter("createTraceFrom");
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));

			Requirement requirement = new Requirement(requirementId, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////

			// Call RequirementUtil.createTraces
			// Get the error / status message
			String status = RequirementUtil.createTraces(project, requirementId, 
				createTraceTo, createTraceFrom, project.getProjectId(), securityProfile,  databaseType);
			if (status != ""){
				request.setAttribute("status", status);
			}
			
			
			
			// forward to displayRequirementTrace after setting the Status message in request.setParameter.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Requirement/displayRequirementTrace.jsp");
			dispatcher.forward(request, response);
		}
		else if (action.equals("createTraces2")){
			String createTraceTo = request.getParameter("createTraceTo");
			String createTraceFrom = request.getParameter("createTraceFrom");
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));

			Requirement requirement = new Requirement(requirementId, databaseType);
			
			// Call RequirementUtil.createTraces
			// Get the error / status message
			
			String status = RequirementUtil.createTraces(project, requirementId, 
				createTraceTo, createTraceFrom, project.getProjectId(), securityProfile,  databaseType);
			// lets print the status .
			if ((status == null) || (status.equals(""))){
				if ((createTraceTo!=null) && (!(createTraceTo.equals("")))){
					status += "<br><span class='normalText'>  " 
							+ requirement.getRequirementFullTag() +  " <b>Now Traces To</b> " + createTraceTo + "</span>";
				}
				if ((createTraceFrom!=null) && (!(createTraceFrom.equals("")))){
					status += "<br><span class='normalText'> " 
							+ createTraceFrom +  " <b>Now Traces To </b> " + requirement.getRequirementFullTag() + "</span>";
				}
			
			}
			else {
				status = "<br><div class='alert alert-success'><span class='normalText'> Trace <b>NOT</b> created. " + status + "</span></div>";
			}
			PrintWriter out = response.getWriter();
		    out.println(status);
		}
		else if (action.equals("traceActionInTracePanel")){
			String createTraceTo = request.getParameter("createTraceTo");
			String createTraceFrom = request.getParameter("createTraceFrom");
			int fromRequirementId = Integer.parseInt(request.getParameter("fromRequirementId"));
			int toRequirementId = Integer.parseInt(request.getParameter("toRequirementId"));
			String traceAction = request.getParameter("traceAction");
			
			Requirement fromRequirement = new Requirement(fromRequirementId, databaseType);
			if (!(securityProfile.getPrivileges().contains("traceFromRequirementsInFolder" 
					+ fromRequirement.getFolderId()))){
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////

			
			if (traceAction.equals("createTrace")){
				// Call RequirementUtil.createTraces
				// Get the error / status message
				String status = RequirementUtil.createTraces(project, fromRequirementId, 
					createTraceTo, createTraceFrom, project.getProjectId(), securityProfile,  databaseType);
				if (status == null){
					status = "";
				}
				if (status.contains("Circular")){
					status = "<span class='normalText'><font color='red'>" + status + "</font></span>";
					PrintWriter out = response.getWriter();
				    out.println(status);
				    return;
				}
								
			}
			if (traceAction.equals("deleteTrace")){
				Trace trace = new Trace (fromRequirementId, toRequirementId);
				String status = RequirementUtil.deleteTrace(trace.getId(), user.getEmailId(), securityProfile,  databaseType);	
				
			}
			if (traceAction.equals("suspectTrace")){
				Trace trace = new Trace (fromRequirementId, toRequirementId);
				String status = RequirementUtil.makeSuspect(trace.getId(), user.getEmailId(), securityProfile,  databaseType);	
			}
			if (traceAction.equals("clearTrace")){
				Trace trace = new Trace (fromRequirementId, toRequirementId);
				String status = RequirementUtil.clearSuspect(trace.getId(), user.getEmailId(), securityProfile,  databaseType);	
			}
			
		
			
			// forward to displayRequirementTrace after setting the Status message in request.setParameter.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/TracePanel/displayTraceInTracePanel.jsp?fromRequirementId=" + 
			fromRequirementId + "&toRequirementId=" + toRequirementId);
			dispatcher.forward(request, response);


		}
		
		else if (action.equals("createAttributeValues")){
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			
			Requirement requirement = new Requirement(requirementId, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////

			
			
			String attributeIdString = request.getParameter("attributeIdString");
			
			// attributeIdString has a string of attribute Ids and values in the following format.
			// id#value##id#value. eg : attributeIdString=2#DropDown##4#URL##3#Date##1#Text##
			// we need to get a list of attribute ids and get the request.getparameter values for these.
			// a typical URL looks like this : 
			//url is /GloreeJava2/servlet/RequirementAction?action=createAttributes&requirementId=1&attributeIdString=2#DropDown##4#URL
			// ##3#Date##1#Text##&2=good%20to%20have&4=external&3=datepromised&1=cost
			
			// we go through the list of RAttributeIds, get the RTAttribute for each of these
			// and see if any attributes have changed that can force us to update the Version, 
			// Traceability and Workflow.
			
			boolean impactsVersion = false;
			boolean impactsTraceability = false;
			boolean impactsApprovalWorkflow = false;
			String traceDefinition = "";
				
			String [] attributeStrings = attributeIdString.split("##");
			for (int i=0; i<attributeStrings.length; i++ ){
				
				String [] attribute = attributeStrings[i].split("#");
				// Note : id here is the id of the attribute in requirment type. we will be using it to create an attribute value.
				String id = attribute[0];
				
				
				if (id != null){
					String aValue = request.getParameter(id);
					int attributeValueId = Integer.parseInt(id);
					
					// lets get the old Attribute Value object, and compare its value with the new value.
					// only if the value has changed, do we need to update the system and trigger
					// version, traceability and workflow.
					
					if (aValue == null){
						aValue = "";
					}
					
					/*
					 there is no reason for this stupid line to exist. It was just removing every thing after the last , in the attib value
					  if (aValue.contains(",")){
						aValue = (String) aValue.subSequence(0,aValue.lastIndexOf(","));
					}			
					*/
					
					
					RAttributeValue oldAttributeValue = new RAttributeValue(attributeValueId, databaseType);
					if (!(oldAttributeValue.getAttributeEnteredValue().trim().equals(aValue.trim()))) {
					
						// before we update, lets clean up by changing any ,, s to ,
						if (aValue.contains(",,")){
							aValue = aValue.replace(",," , ",");
						}
						if (aValue.endsWith(",")){
							aValue = (String) aValue.subSequence(0,aValue.lastIndexOf(","));
						}
						
						// NOTE : do not delete rAV . when you create this object, an entry is made in the db for this
						// attribute value. we may need a different way to implement this.
						RAttributeValue rAV  = new RAttributeValue(attributeValueId, aValue, databaseType, user.getEmailId());
						
						String log = "Updated attribute " + rAV.getAttributeName() +" to " +  aValue;
						RequirementUtil.createRequirementLog(requirementId, log, user.getEmailId(), databaseType);
						
						
						System.out.println("srt temp updating " + rAV.getAttributeName());
						if (rAV.getAttributeImpactsVersion() == 1) {

							impactsVersion = true;
						}
						if (rAV.getAttributeImpactsTraceability() == 1) {
							impactsTraceability= true;
							traceDefinition += rAV.getAttributeName() + ":" + rAV.getAttributeEnteredValue() + "  "; 
						}
						if (rAV.getAttributeImpactsApprovalWorkflow() == 1) {
							impactsApprovalWorkflow = true;
						}
					}
				}
			}
			
			// if the attributes that have been flagged as impact versioning have changed, then
			// lets update the Req Version.
			if (impactsVersion) {
				RequirementUtil.updateVersion(requirementId, request,  databaseType);
				// at this point, lets create an entry in the gr_requirement_version table
				RequirementUtil.createRequirementVersion(requirementId);

			}

			// if the attributes that have been flagged as impact traceability have changed, then
			// lets update the Req Traceability.
			if (impactsTraceability) {
				RequirementUtil.updateTraceability(traceDefinition, requirementId, request, user.getEmailId(),  databaseType);
			}

			// if the attributes that have been flagged as impact traceability have changed, then
			// lets update the Req Traceability.
			if (impactsApprovalWorkflow) {
				RequirementUtil.updateApprovalWorkflow(requirementId, request);
			}

			
			// NOTE : this is critical. After attributes are created / modified, call the setter
			// requirementUtil.setUserDefinedAttributes method.
			RequirementUtil.setUserDefinedAttributes(requirementId, user.getEmailId(), databaseType);

			
			// forward to displayRequirementAttribute 
			request.setAttribute("updated", "yes");

			String singleAttribute = request.getParameter("singleAttribute");
			if (singleAttribute == null){
				// display all attributes
				RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Requirement/displayRequirementAttributeValue.jsp");
				dispatcher.forward(request, response);
			}
			else {
				// display Single attribute
				
				String attributeLabel = request.getParameter("attributeLabel");
					
				String encodedAttributeLabel = URLEncoder.encode(attributeLabel,"UTF-8");
				RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Requirement/Attribute/displayRequirementAttributeValueSingle.jsp?attributeLabel=" + encodedAttributeLabel);
				dispatcher.forward(request, response);
			}
		}
		else if (action.equals("submitRequirementForApproval")){
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));

			Requirement requirement = new Requirement(requirementId, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////

			
			// lets see if there are any approvers for the Req , before we submit it for approval
			if (RequirementUtil.approversForRequirementExist(requirementId)){
				String serverName = request.getServerName();

				RequirementUtil.submitRequirementForApproval(requirementId,user.getEmailId(),  databaseType, serverName);
				

				
				String mailHost = this.getServletContext().getInitParameter("mailHost");
				String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
				String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
				String smtpPort = this.getServletContext().getInitParameter("smtpPort");
				String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
				String emailUserId = this.getServletContext().getInitParameter("emailUserId");
				String emailPassword = this.getServletContext().getInitParameter("emailPassword");
				
				
				RequirementUtil.remindPendingApproversImmediately(requirementId,  serverName, request,
						mailHost,  transportProtocol,  smtpAuth,  smtpPort,  smtpSocketFactoryPort,  emailUserId,  emailPassword);
				
				PrintWriter out = response.getWriter();
			    out.println("approversExist");
			}
			else {
				PrintWriter out = response.getWriter();
			    out.println("approversDoNotExist");
			    return;
			}
		}
		else if (action.equals("displayApproversForThisRequirement")){
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));

			Requirement requirement = new Requirement(requirementId, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////

			ArrayList approvers = requirement.getRequirementApprovers();
			PrintWriter out = response.getWriter();
			out.println("<div class='alert alert-success'>");
		    out.println("<table width='970px'>");
		    
		    out.println("<tr><td colspan='2' align='right'><a href='#' onClick='document.getElementById(\"requirementPromptDiv\").style.display=\"none\";'>Close</a></td></tr>");
		    out.println("<tr><td colspan='2'><span class='normalText'><b>Approvers for this requirement are.. </span></td></tr>");
		    out.println("<tr><td colspan='2'>&nbsp;</td></tr>");
		    out.println("<tr>");
	    	out.println("<td><span class='normalText'> <b>Name</b> </td>");
	    	out.println("<td><span class='normalText'> <b>Email Address</b> </td>");
	    	out.println("</tr>");
		    Iterator a = approvers.iterator();
		    while (a.hasNext()){
		    	try {
		    	String approver = (String) a.next();
		    	String [] approverDetails = approver.split(":##:");
				String emailId = approverDetails[0];
				String name = approverDetails[1] + " " + approverDetails[2];
				out.println("<tr>");
		    	out.println("<td width='150px'><span class='normalText'>" + name + "</td>");
		    	out.println("<td><span class='normalText'>" + emailId + "</td>");
		    	out.println("</tr>");
		    	}
		    	catch (Exception e){
		    		
		    	}
		    }
		    out.println("</table>");
		    out.println("</div>");
		    
		    return;
		
		}
				
		else if (action.equals("changeManualTestingStatus")){
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			String createDefect = request.getParameter("createDefect");
			
			Requirement requirement = new Requirement(requirementId, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////

			
			String manualTestingStatus = request.getParameter("manualTestingStatus");
			if (!(requirement.getTestingStatus().equals(manualTestingStatus))){
				requirement.setTestingStatus(manualTestingStatus, user.getEmailId(), databaseType);
			}
			
			if (createDefect.equals("yes")){
				// the user has requested that we create a new defect object 
				// and trace it to this test result  
				RequirementType targetRequirementType = new RequirementType(project.getProjectId(),"Defects");
				Requirement defectRequirement  = RequirementUtil.cloneRequirement(
					requirement,targetRequirementType, project.getProjectId(), targetRequirementType.getRootFolderId(), 
					true, false, user, securityProfile ,  databaseType);
				// once the req is cloned, lets change the owner of the defect to the person 
				// failing the Test Result.
				
				String mailHost = this.getServletContext().getInitParameter("mailHost");
				String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
				String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
				String smtpPort = this.getServletContext().getInitParameter("smtpPort");
				String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
				String emailUserId = this.getServletContext().getInitParameter("emailUserId");
				String emailPassword = this.getServletContext().getInitParameter("emailPassword");
				
				defectRequirement.setOwner(request, user.getEmailId(), user, databaseType, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
				// Once the req is cloned into a new defect we trace it to the Test Result
				RequirementUtil.createTraces(project, defectRequirement.getRequirementId(), 
						requirement.getRequirementFullTag(), "", project.getProjectId(), securityProfile,  databaseType);
			}
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Requirement/displayRequirementCore.jsp?requirementId="
					+ requirementId);
				dispatcher.forward(request, response);

		}
		else if (action.equals("copyRequirement")){
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			int targetFolderId = Integer.parseInt(request.getParameter("copyFolderId"));
			int targetProjectId = Integer.parseInt(request.getParameter("targetProjectId"));
			
			String createTraceToSource = request.getParameter("createTraceToSource");

			String createTraceFromSource = request.getParameter("createTraceFromSource");
			String copyCommonAttributes = request.getParameter("copyCommonAttributes");
			String copyTraceability = request.getParameter("copyTraceability");
			
			
			Requirement requirement = new Requirement(requirementId, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the user does not have Read Permissions on this Requirement reject him.
			if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
					+ requirement.getFolderId()))){
				PrintWriter out = response.getWriter();
			    out.println("<span class='normalText'><font color='red'> You do not have Read permissions on this Requirement </font></span>");
			    out.close();
				return;
			}
			
			// if the user does not have create permissions on the Target Folder reject him.
			if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" 
					+ targetFolderId))){
				PrintWriter out = response.getWriter();
			    out.println("<span class='normalText'><font color='red'> You do not have permissions to create Requriements in the Target Folder</font></span>");
			    out.close();
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////

			
			Folder targetFolder = new Folder(targetFolderId);
			RequirementType targetRequirementType = new RequirementType(targetFolder.getRequirementTypeId());
			boolean cloneAttributes = false;
			if (copyCommonAttributes.equals("yes")){
				cloneAttributes = true;
			}
			boolean cloneTraceability = false;
			if (copyTraceability.equals("yes")){
				cloneTraceability = true;
			}
			Requirement targetRequirement  = RequirementUtil.cloneRequirement(
					requirement,targetRequirementType, targetProjectId, targetFolderId, 
					cloneAttributes, cloneTraceability, user, securityProfile,  databaseType );
			
			
			String status = "";
			if ((createTraceToSource != null ) && (createTraceToSource.equals("yes"))){
				status = RequirementUtil.createTraces(project, requirement.getRequirementId(), 
				"", targetRequirement.getRequirementFullTag(), project.getProjectId(), securityProfile,  databaseType);
				status = "A trace was created from " + targetRequirement.getRequirementFullTag() + " to " + requirement.getRequirementFullTag();
			}
			
			if ((createTraceFromSource != null ) && (createTraceFromSource.equals("yes"))){
				status = RequirementUtil.createTraces(project, targetRequirement.getRequirementId(), 
				"", requirement.getRequirementFullTag(), project.getProjectId(), securityProfile,  databaseType);
				status = "A trace was created from " + requirement.getRequirementFullTag() + " to " + targetRequirement.getRequirementFullTag();
			}
			
			
			PrintWriter out = response.getWriter();
			if (targetProjectId == project.getProjectId()){
			    out.println("<span class='normalText'>A new Requirement " + 
			    	" <span class='normalText' title='Requirement Name : "+ targetRequirement.getRequirementName() + "'>"+
			    	" <a href='#' onClick=' " +
					"	displayFolderInExplorer("+ targetRequirement.getFolderId() + "); " +
					"	displayFolderContentCenterA("+ targetRequirement.getFolderId() + "); "+
					"	displayFolderContentRight("+ targetRequirement.getFolderId() + "); "+									   							
					"	displayRequirement("+ targetRequirement.getRequirementId() + ")'> "+
					targetRequirement.getRequirementFullTag() +
					" </a> "+
					"</span>"+
			    	" has been created in the Folder " + targetFolder.getFolderPath()  +	" <br> " + status +  "</span><br><br>");
			}
			else {

			    out.println("<span class='normalText'>A new Requirement " + 
				    	" <span class='normalText' title='Requirement Name : " +targetRequirement.getRequirementName() + "'>"+
						targetRequirement.getRequirementFullTag() +
						"</span>"+
				    	" has been created in the Folder " + targetFolder.getFolderPath()  +	
				    	" in Project " + targetRequirement.getProjectShortName() +
				    	" </span><br>" + status   + "<br><br>" );
			}
		    out.close();
			return;

		}	

		else if (action.equals("approvalWorkFlowAction")){
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			
			Requirement requirement = new Requirement(requirementId, databaseType);
			// we do this work , ONLY if the user is a Pending approver or Rejected  in the list.
			if (
					(requirement.getApprovers().contains("(P)" + user.getEmailId()))
					||
					(requirement.getApprovers().contains("(R)" + user.getEmailId()))
					){
				String approvalAction  = request.getParameter("approvalAction");
				String approvalNote = request.getParameter("approvalNote");
				
				
				RequirementUtil.approvalWorkFlowAction(requirementId, approvalAction, approvalNote, user, request, databaseType);
				

				String serverName = request.getServerName();
				
				String mailHost = this.getServletContext().getInitParameter("mailHost");
				String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
				String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
				String smtpPort = this.getServletContext().getInitParameter("smtpPort");
				String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
				String emailUserId = this.getServletContext().getInitParameter("emailUserId");
				String emailPassword = this.getServletContext().getInitParameter("emailPassword");
				
				
				RequirementUtil.remindPendingApproversImmediately(requirementId,  serverName, request,
						mailHost,  transportProtocol,  smtpAuth,  smtpPort,  smtpSocketFactoryPort,  emailUserId,  emailPassword);
				// forward to displayRequirementCore
				RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Requirement/displayRequirementCore.jsp?requirementId="
					+ requirementId);
				dispatcher.forward(request, response);
			}
		}
		else if (action.equals("addRequirementComment")){
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			
			Requirement requirement = new Requirement(requirementId, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////

			
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			String comment_note = request.getParameter("comment_note");
			String source = request.getParameter("source");
			
			RequirementUtil.createComment(requirementId,user,comment_note, request, databaseType);
			
			// forward to displayRequirementComment
			RequestDispatcher dispatcher =	request.getRequestDispatcher(
					"/jsp/Requirement/displayRequirementComments.jsp?requirementId=" + 
					+ requirementId + "&folderId=" + folderId + "&source=" + source);
			dispatcher.forward(request, response);
			return;
		}				
		else if (action.equals("deleteComment")){
			int commentId = Integer.parseInt(request.getParameter("commentId"));
			
			Comment commentObject = new Comment(commentId);
			String source = request.getParameter("source");
			
			

			Requirement requirement = new Requirement(commentObject.getRequirementId() , databaseType);

			// make a requirement log entry
			String log = "Deleting Comment: Comment was made by  " + commentObject.getCommenterEmailId()  + " on " + commentObject.getCommentDate() + 
					" comment is '" + commentObject.getHTMLFriendlyCommentNote() + "'"  ;
			RequirementUtil.createRequirementLog(requirement.getRequirementId(), log, user.getEmailId(), databaseType);
			
			// delete the comment
			commentObject.deleteComment();
			
			// return 
			// forward to displayRequirementComment
			RequestDispatcher dispatcher =	request.getRequestDispatcher(
					"/jsp/Requirement/displayRequirementComments.jsp?requirementId=" + 
					+ requirement.getRequirementId() + "&folderId=" + requirement.getFolderId() + "&source=" + source);
			dispatcher.forward(request, response);
			return;
		}		
		else if (action.equals("addRequirementToBaseline")){
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			
			Requirement requirement = new Requirement(requirementId, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////

			int folderId = Integer.parseInt(request.getParameter("folderId"));
			int rTBaselineId = Integer.parseInt(request.getParameter("rTBaselineId"));
			RTBaseline rTBaseline = new RTBaseline(rTBaselineId);
			if (rTBaseline.getLocked() == 1) {
				request.setAttribute("requirementBaselineLocked", "true");
				RequestDispatcher dispatcher =	request.getRequestDispatcher(
						"/jsp/Requirement/displayRequirementCore.jsp?requirementId="
						+ requirementId);
				dispatcher.forward(request, response);				
				return;
			}
			
			boolean exists = RequirementUtil.requirementBaselineAlreadyExists(requirementId,rTBaselineId);
			if (exists) {
				request.setAttribute("requirementBaselineAlreadyExists", "true");
				RequestDispatcher dispatcher =	request.getRequestDispatcher(
						"/jsp/Requirement/displayRequirementCore.jsp?requirementId="
						+ requirementId);
				dispatcher.forward(request, response);				
				return;
			}
			else {
				RequirementUtil.addRequirementToBaseline(requirementId, rTBaselineId, user, request, databaseType);
				// forward to displayRequirementCore
				request.setAttribute("addedToBaseline", "true");
				RequestDispatcher dispatcher =	request.getRequestDispatcher(
					"/jsp/Requirement/displayRequirementCore.jsp?requirementId="
					+ requirementId);
				dispatcher.forward(request, response);
				return;
			}
		}
		else if (action.equals("removeRequirementFromBaseline")){
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			
			Requirement requirement = new Requirement(requirementId, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////

			int requirementBaselineId = Integer.parseInt(request.getParameter("requirementBaselineId"));
			
			RequirementUtil.removeRequirementFromBaseline(requirementId, requirementBaselineId, user, request,  databaseType);
			// forward to displayRequirementCore
			request.setAttribute("removedFromBaseline", "true");
			RequestDispatcher dispatcher =	request.getRequestDispatcher(
				"/jsp/Requirement/displayRequirementCore.jsp?requirementId="
				+ requirementId);
			dispatcher.forward(request, response);
		}
		else if (action.equals("downloadAttachment")) {
			int attachmentId = Integer.parseInt(request.getParameter("attachmentId"));
			RequirementAttachment attachment = new RequirementAttachment(attachmentId,  databaseType);
			Requirement requirement = new Requirement(attachment.getRequirementId(), databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////
			
			
			FileInputStream inputStream= new FileInputStream(attachment.getFilePath());
			
			//response.setContentType("application/msword");
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
    		response.setHeader("Content-Disposition", "attachment; filename=\"" + attachment.getFileName() + "\"");
    		
	        ServletOutputStream out = response.getOutputStream();
	        byte buf[]=new byte[1024];
	        int len;
	        while((len=inputStream.read(buf))>0)
	        	out.write(buf,0,len);
	        
	        out.close();
	        inputStream.close();
			
			return;
		}
		else if (action.equals("deleteRequirementAttachment")) {
			int attachmentId = Integer.parseInt(request.getParameter("attachmentId"));
			RequirementAttachment attachment = new RequirementAttachment(attachmentId,  databaseType);
			
			Requirement requirement = new Requirement(attachment.getRequirementId(), databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////
			
			// we need to delete the file and the folder it contains. 
			// Note : we keep only 1 file in each folder. so this is safe.
			File file = new File(attachment.getFilePath());
			if (file != null){
				File dir = file.getParentFile();
				// lets drop the file.
				file.delete();
				
				if (dir != null) {
					dir.delete();
				}
			}
			
			// now that the physical file is cleaned up, lets remove the attachmentid entry
			RequirementUtil.deleteRequirementAttachment(attachment.getRequirementId(), attachment.getFileName(),
				attachmentId, user.getEmailId(),  databaseType);
			return;
		}
		else if (action.equals("emailRequirement")) {
			String to = request.getParameter("to");
			String cc = request.getParameter("cc");
			String subject = request.getParameter("subject");
			String message = request.getParameter("message");
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			Requirement requirement = new Requirement(requirementId, databaseType);
		
			message  = message.replace("Requirement Details will be inserted here by the system", "");
			
			String url = ProjectUtil.getURL(request,requirementId,"requirement") ;
			message += "<br><br><br><table border='1'> "
					+ "<tr><td> Project </td> <td> " +  project.getProjectName() + "</td></tr>" 
					+ "<tr><td> Requirement Id   </td><td> " + requirement.getRequirementFullTag() + "</td></tr>" 
					+ "<tr><td> Requirement URL  </td><td> <a href='" + url + "'> " + url + "</a></td></tr>"  
					+ "<tr><td> Requirement Name </td><td> " + requirement.getRequirementNameForHTML() + "</td></tr>" 
					+ "<tr><td> Requirement Description </td><td> " + requirement.getRequirementDescription() + "</td></tr></table>" 
					+ "<br><br>Regards<br><br>" +  user.getFirstName() + " " + user.getLastName() + "<br><br>" +
			 user.getEmailId();
			
			// users may enter email ids separated by space or semicolon. lets 
			// make them all comma separated
			if (to.trim().contains(" ")){
				to = to.replace(' ', ',');
			}
			if (to.trim().contains(";")){
				to = to.replace(';', ',');
			}
			if (cc.trim().contains(" ")){
				cc = cc.replace(' ', ',');
			}
			if (cc.trim().contains(";")){
				cc = cc.replace(';', ',');
			}
			

			// lets send the email out to the toEmailId;
			ArrayList toArrayList = new ArrayList();
			if (to != null){
				to = to.trim();
				if (!to.equals("")){
					if (to.contains(",")){
						String [] toEmails = to.split(",");
						for (int i=0; i < toEmails.length; i++ ){
							toArrayList.add(toEmails[i]);
						}
					}
					else {
						toArrayList.add(to);
					}
				}
			}
			
			ArrayList ccArrayList = new ArrayList();
			if (cc != null){
				cc = cc.trim();
				if (!cc.equals("")){
					if (cc.contains(",")){
						String [] ccEmails = cc.split(",");
						for (int i=0; i < ccEmails.length; i++ ){
							ccArrayList.add(ccEmails[i]);
						}
					}
					else {
						ccArrayList.add(cc);
					}
				}
			}
			MessagePacket mP = new MessagePacket(toArrayList, ccArrayList, subject, message, "");
			
			String mailHost = this.getServletContext().getInitParameter("mailHost");
			String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
			String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
			String smtpPort = this.getServletContext().getInitParameter("smtpPort");
			String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
			String emailUserId = this.getServletContext().getInitParameter("emailUserId");
			String emailPassword = this.getServletContext().getInitParameter("emailPassword");
			

			EmailUtil.email(mP, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
			//EmailUtil.emailAsHTML(mP, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
			//EmailUtil.email(mP, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword );
			
		    PrintWriter out = response.getWriter();
		    out.println("<div class='alert alert-success'><span class='normalText'> Your email has been sent out." +
		    		" &nbsp;&nbsp;<a href='#' onClick='document.getElementById(\"requirementPromptDiv\").style.display=\"none\"'>Close</a> </span></div>");
		    out.close();
			
			return;
		}
		else if ( action.equals("validateParentTag")){
			String parentFullTag = request.getParameter("parentFullTag");
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			Requirement parent = new Requirement(parentFullTag, project.getProjectId(), databaseType);
			String output = "";


			
			if (parent.getRequirementId() == 0  ){
				
				output = "<span class='normalText' title='This Requirment "+ parentFullTag +" does not exist in this project' >" +						
						"<img src='/GloreeJava2/images/arrow.gif' border='0' height='20' width='30'></span>";
			}
			else if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
					+ parent.getFolderId()))){
				output = "<span class='normalText' title='You do not have Update permissions on "+
					parentFullTag +". Hence you can not create a child to this Requirement.'> " +
					"<img src='/GloreeJava2/images/arrow.gif' border='0' height='20' width='30'></span>";
				
			}
			else if (folderId != parent.getFolderId()){
				output = "<span class='normalText' title='"+ parentFullTag +" belongs to a different Folder. " +
				" Pleaes create the Child Requirement in the same folder as the Parent'> " +
				" <img src='/GloreeJava2/images/arrow.gif' border='0' height='20' width='30'>" +
				"</span>";
			}
			
			PrintWriter out = response.getWriter();
		    out.println(output);
		    return;
		}
		else if ( action.equals("validateTraceTo")){
			String traceTo = request.getParameter("traceTo");
			String traceFrom = "";
			String status = RequirementUtil.validatePotentialTraces(project, 
					traceTo, traceFrom, project.getProjectId(), securityProfile,  databaseType);

			if (status.contains("<br>")){
				status = status.replace("<br>", "  ");
			}
			if (status.contains("<BR>")){
				status = status.replace("<BR>", "  ");
			}

			String output = "";
			if ((status != null) && (!status.equals(""))){
				output = "<span class='normalText' title='"+ status +"'> " +
				" <img src='/GloreeJava2/images/arrow.gif' border='0' height='20' width='30'>" +
				"</span>";
			}
			PrintWriter out = response.getWriter();
		    out.println(output);
		    return;
		}

		else if ( action.equals("validateTraceFrom")){
			String traceTo = "";
			String traceFrom = request.getParameter("traceFrom");
			String status = RequirementUtil.validatePotentialTraces(project, 
					traceTo, traceFrom, project.getProjectId(), securityProfile,  databaseType);

			if (status.contains("<br>")){
				status = status.replace("<br>", "  ");
			}
			if (status.contains("<BR>")){
				status = status.replace("<BR>", "  ");
			}
			
			String output = "";
			if ((status != null) && (!status.equals(""))){
				output = "<span class='normalText' title='"+ status +"'> " +
				" <img src='/GloreeJava2/images/arrow.gif' border='0' height='20' width='30'>" +
				"</span>";
			}
			
			PrintWriter out = response.getWriter();
		    out.println(output);
		    return;
		}
		
		else if ( action.equals("getParentInfo")){
			String parentFullTag = request.getParameter("parentFullTag");
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			Folder folder = new Folder(folderId);
			Requirement parent = new Requirement(parentFullTag, project.getProjectId(), databaseType);
			String output = "";

			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (parent.getProjectId() != project.getProjectId()) {
				output = "<span class='normalText'><font color='red'>This Requirement <b>"+ 
				parentFullTag + 
				"</b>does not exist in this project.</font></span>";
				PrintWriter out = response.getWriter();
			    out.println(output);
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////

			
			if (parent.getRequirementId() == 0  ){
				
				output = "<span class='normalText'><font color='red'>This Requirement <b>"+ 
				parentFullTag + 
				"</b>does not exist in this project.</font></span>";
			}
			else if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
					+ parent.getFolderId()))){
				output = "<span class='normalText'><font color='red'>You do not have Update permissions on <b>"+ 
					parentFullTag + 
					"</b> . Hence you can not create a child to this Requirement.</span>";
				
			}
			else if (folder.getRequirementTypeId() != parent.getRequirementTypeId()){
				output = "<span class='normalText'><font color='red'><b>"+ parentFullTag +
					"</b> belongs to a different Requirement Type." +
				" </font></span>";
			}
			else {
				output = "<table><tr><td width='100'><span class='normalText'>Parent  Name</span></td>" +
					"<td> <span class='normalText'> "+ 
				parent.getRequirementName() + 
				"</span> </td></tr>" +
				"<tr><td width='100'><span class='normalText'>Copy Parent Attributes </span></td>" +
				"<td><span class='normalText'>" +
				"	<select name='cloneParentAttributes' id='cloneParentAttributes' >" +
				"		<option value='yes'>Yes</option>" +
				"		<option value='no'>No</option>" +
				"	</select></span></td></tr></table>";
			}
			PrintWriter out = response.getWriter();
		    out.println(output);
		    return;

		}
		else if ( action.equals("makeRequirementIndependent")){
			
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			String childrensFuture = request.getParameter("childrensFuture");
			
			Requirement requirement = new Requirement(requirementId, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////
			
			requirement = RequirementUtil.makeRequirementIndependent(requirement, childrensFuture, user.getEmailId(),  databaseType);
			
			request.setAttribute("requirement", requirement);
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Requirement/returnRequirementId.jsp");
			dispatcher.forward(request, response);
			return;
		}
		else if ( action.equals("makeAllChildrenIndependent")){
			
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			
			Requirement requirement = new Requirement(requirementId, databaseType);
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////
			
			ArrayList childRequirementIds = RequirementUtil.getImmediateChildrenRequirementIds(requirement.getProjectId(), requirement.getRequirementFullTag());
			Iterator c = childRequirementIds.iterator();
			while (c.hasNext()){
				Integer childRequirementIdInteger = (Integer) c.next();
				Requirement childRequirement = new Requirement(childRequirementIdInteger.intValue(), databaseType);
				RequirementUtil.makeRequirementIndependent(childRequirement, "takeChildrenAlong", user.getEmailId(),  databaseType);
			}
			return;
		}


		else if ( action.equals("changeParent")){
			
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			String childrensFuture = request.getParameter("childrensFuture");
			String newParentFullTag = request.getParameter("parentFullTag");
			
			Requirement requirement = new Requirement(requirementId, databaseType);
			Requirement parent = new Requirement(newParentFullTag, project.getProjectId(), databaseType);
			
			String oldParentFullTag = requirement.getParentFullTag();
			
			///////////////////////////////SECURITY CODE ////////////////////////////
			// if the requirement worked on, doesn't belong to the project the user is 
			// currently logged into, then a user logged into project x is trying to 
			// hack into a req in project y by useing requirementId parameter.
			if (requirement.getProjectId() != project.getProjectId()) {
				return;
			}
			///////////////////////////////SECURITY CODE ////////////////////////////
			
			
			if ((newParentFullTag != null) && (!newParentFullTag.equals(""))){
				// parent full tag is given. so lets make sure its valid.
				
				Folder folder = new Folder(folderId);
				String errorOutput = "";
				if (parent.getRequirementId() == 0  ){
					errorOutput = "<div class='alert alert-success'> <span class='normalText'><font color='red'>Error : This Requirement <b>"+ 
					newParentFullTag + 
					"</b>does not exist in this project.</font></span></div>";
				}
				else if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
						+ parent.getFolderId()))){
					errorOutput = "<div class='alert alert-success'><span class='normalText'><font color='red'>Error : You do not have Update permissions on <b>"+ 
						newParentFullTag + 
						"</b> . Hence you can not create a child to this Requirement.</span></div>";
					
				}
				else if (folder.getRequirementTypeId() != parent.getRequirementTypeId()){
					errorOutput = "<span class='normalText'><font color='red'>Error : <b>"+ newParentFullTag +
						"</b> belongs to a different Requirement Type." +
					" </font></span>";
				}
				else if (requirementId == parent.getRequirementId()){
					errorOutput = "<span class='normalText'><font color='red'>Error : " +
						"You can not make a Requirement a Child of it self." +
					" </font></span>";
				}
	
				if (!(errorOutput.equals(""))){
					PrintWriter out = response.getWriter();
				    out.println(errorOutput);
				    return;
				}
			}
			
			// lets see if the user is trying to make this req a child of on of the children in the family.
			ArrayList allChildrenInfamilyRequirementIds = RequirementUtil.getAllChildrenInFamilyRequirementIds(requirementId);
			Iterator allC = allChildrenInfamilyRequirementIds.iterator();
			while (allC.hasNext()){
				Integer child = (Integer) allC.next();
				if (parent.getRequirementId() == child.intValue()){
					// the parent we are trying to assign this req to, is actually a child of 
					// of this req. So, the user is crazy bastard
					String errorOutput = "<span class='normalText'><font color='red'>Error : " +
						"You can not make a Requirement a Child of its Children." +
						" </font></span>";
					PrintWriter out = response.getWriter();
				    out.println(errorOutput);
				    return;
					
				}
			}
			
			// Before we assign this req to a new parent, we have to take care of one thing.
			// if this req has child requirements and no parent req, and the request was to 
			// assign the children to the grand parent (since no grand parent exists) we
			// have to make the children independent.
			if (
					((requirement.getParentFullTag() == null) || (requirement.getParentFullTag().equals("")))
					&&
					(childrensFuture.equals("assignToGrandParent"))
				){
				// this req does not have a parent, and we have been asked to assign the children to grand parents.
				// there fore we make the children independent.
				ArrayList childRequirementIds = RequirementUtil.getImmediateChildrenRequirementIds(requirement.getProjectId(), requirement.getRequirementFullTag());
				Iterator c = childRequirementIds.iterator();
				while (c.hasNext()){
					Integer childRequirementIdInteger = (Integer) c.next();
					Requirement childRequirement = new Requirement(childRequirementIdInteger.intValue(), databaseType);
					RequirementUtil.makeRequirementIndependent(childRequirement, "takeChildrenAlong", user.getEmailId(),  databaseType);
				}
			}
			requirement = RequirementUtil.assignToNewParent(requirementId,oldParentFullTag, newParentFullTag,childrensFuture, user.getEmailId(),  databaseType); 

			request.setAttribute("requirement", requirement);
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Requirement/returnRequirementId.jsp");
			dispatcher.forward(request, response);
			return;
		}

		else if ( action.equals("logViewEvent")){
			String logViewEventEnabled = this.getServletContext().getInitParameter("logViewEventEnabled");
			// For performance reasons, lets log the view events, only if the env variable for logging is set to true.
			if ((logViewEventEnabled != null) && (logViewEventEnabled.toLowerCase().equals("yes"))){
				int requirementId = Integer.parseInt(request.getParameter("requirementId"));
				
				RequirementUtil.logViewEvent(requirementId, user.getUserId() , databaseType);
			}
			return;
		}
		

		else if ( action.equals("getNextRequirementInFolder")){
			
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			
			Folder folder = new Folder(folderId);
			ArrayList allRequirements = folder.getMyRequirements(project.getProjectId(), databaseType);
			
			Iterator aR = allRequirements.iterator();
			boolean reachedCurrentReq = false;
			while (aR.hasNext()){
				Requirement tempRequirement = (Requirement) aR.next();
				
				if (reachedCurrentReq == true ){
					// previous iteration was the match. so, this tempReq must be the nexgt req.
					
					PrintWriter out = response.getWriter();
					out.println(tempRequirement.getRequirementId());
					return;
				}
				if (tempRequirement.getRequirementId() == requirementId){
					reachedCurrentReq = true;
					// now that we have reached the current req, lets return the next req we find.
				}
			}
			// if we get till here (for example, there is only 1 req in the folder)
			// or some other crazy error condition, instead of crapping out, lets print the original req id.
			PrintWriter out = response.getWriter();
			out.println(requirementId);
			return;
		}

		else if ( action.equals("getPreviousRequirementInFolder")){
			
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			
			Folder folder = new Folder(folderId);
			ArrayList allRequirements = folder.getMyRequirements(project.getProjectId(), databaseType);
			
			Iterator aR = allRequirements.iterator();
			int previousRequirementId = 0;
			while (aR.hasNext()){
				Requirement tempRequirement = (Requirement) aR.next();
			
				if (tempRequirement.getRequirementId() == requirementId){
					// now that we have reached the targetRequirementId, lets get out of the iteration.
					// the previousReqId has the value we need.
					
					// if the first req of the folder is the targetrequirement, then the previousReqId will be 0 as it hasn't had a chance to get even one value
					if (previousRequirementId == 0 ){
						previousRequirementId = requirementId;
					}
					PrintWriter out = response.getWriter();
					
					out.println(previousRequirementId);
					return;
					
				}
				previousRequirementId = tempRequirement.getRequirementId();
				
			}
			// if we get till here (for example, there is only 1 req in the folder)
			// or some other crazy error condition, instead of crapping out, lets print the original req id.
			PrintWriter out = response.getWriter();
			out.println(requirementId);
			return;
		}


		
		else if ( action.equals("updateRequirementAttachmentDescription")){
			
			int requirementId = Integer.parseInt(request.getParameter("requirementId"));
			int attachmentId = Integer.parseInt(request.getParameter("attachmentId"));
			
			Requirement requirement = new Requirement(requirementId, databaseType);
			if (securityProfile.getPrivileges().contains("updateRequirementsInFolder" + requirement.getFolderId())){
				// TO DO SRT : Update this stuff
				String title = request.getParameter("title");
				RequirementUtil.updateRequirementAttachmentTitle(attachmentId, requirementId, title, user.getEmailId(), databaseType);
				// if we get till here (for example, there is only 1 req in the folder)
				// or some other crazy error condition, instead of crapping out, lets print the original req id.
				PrintWriter out = response.getWriter();
				out.println("Successfully update file descritpion");
			}
		
			return;
		}

		else {
			// if nothing else works, forward to the Welcome screen. This should never happen.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/welcome.jsp");
			dispatcher.forward(request, response);
		}
	
	}

}
