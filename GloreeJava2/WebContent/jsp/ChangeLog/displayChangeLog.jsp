<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String dPCLIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dPCLIsLoggedIn == null) || (dPCLIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");

	// authorizatoin 
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is a member of this project.
	
	int changeFolderId = Integer.parseInt(request.getParameter("changeFolderId"));
	String actorEmailId = request.getParameter("actorEmailId");
	
	String sortBy = request.getParameter("sortBy");
	if (sortBy == null ){
		sortBy = "";
	}
	
	int changedSince = 9999;
	try {
		changedSince = Integer.parseInt(request.getParameter("changedSince"));
	}
	catch (Exception e){
		changedSince = 9999;
	}
	
	String changeType = request.getParameter("changeType");
	
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dPCLIsMember = false;
	SecurityProfile dPCLSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dPCLSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		dPCLIsMember = true;
	}
	
	
	
	String bulkChangeColor  = "#F5CFC4";
	String projectColor = "#F5EBC4";
	String commentColor = "#F3F5C4";
	String traceabilityColor = "#C7F04D";
	String approvalColor = "#A6F04D";
	
	String createdReqColor = "#C0F0DD";
	String updatedNameColor = "#66F2F0";
	String updatedDescriptionColor = "#66D8F2";
	String updatedAttributeColor = "#66B3F2";
	String fileColor = "#6680F3";
	String completionColor = "#9566F2";
	
	
	String ownerColor = "#BA66F2";
	String deletedColor = "#D666F2";
	String restoredColor = "#F266E7";
	String movedColor = "#F266C1";
	
	%>

<%if(dPCLIsMember){ %>
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	
	<div id='legendDiv'>
		<table class='paddedTable' width='100%'>
			<tr>
				<td style="width: 150px; background-color:<%=bulkChangeColor%>;  text-align:center; vertical-align:middle;" >
					<span class='normalText'>  
					<input type='button' id='hideBulkChangesButton' style=' width:150px, height:25px '  value='Hide Bulk Changes ' 
						onclick="
							document.getElementById('hideBulkChangesButton').style.display='none';
							document.getElementById('showBulkChangesButton').style.display='block';
									
							var elements = document.getElementsByClassName('bulkChangeDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'none';
							}
						"
					>
					
					<input type='button' id='showBulkChangesButton' style=' width:150px; height:25px; display:none'  value='Show Bulk Changes ' 
						onclick="
							document.getElementById('hideBulkChangesButton').style.display='block';
							document.getElementById('showBulkChangesButton').style.display='none';
						
							var elements = document.getElementsByClassName('bulkChangeDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'block';
							}
						"
					>
					</span>				
				</td>	
			

				<td style="width: 150px; background-color:<%=projectColor%>;  text-align:center; vertical-align:middle; " >
					<span class='normalText'>  
					<input type='button' id='hideProjectButton' style=' width:150px, height:25px '  value='Hide Project ' 
						onclick="
							document.getElementById('hideProjectButton').style.display='none';
							document.getElementById('showProjectButton').style.display='block';
									
							var elements = document.getElementsByClassName('projectDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'none';
							}
						"
					>
					
					<input type='button' id='showProjectButton' style=' width:150px; height:25px; display:none'  value='Show Project ' 
						onclick="
							document.getElementById('hideProjectButton').style.display='block';
							document.getElementById('showProjectButton').style.display='none';
						
							var elements = document.getElementsByClassName('projectDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'block';
							}
						"
					>
					</span>				
				</td>	

			
				<td style="width: 150px; background-color:<%=commentColor%>;  text-align:center; vertical-align:middle; " >
				
					<span class='normalText'> 
					<input type='button' id='hideCommentsButton' style=' width:150px, height:25px '  value='Hide Comments ' 
						onclick="
							document.getElementById('hideCommentsButton').style.display='none';
							document.getElementById('showCommentsButton').style.display='block';
									
							var elements = document.getElementsByClassName('commentsDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'none';
							}
						"
					>
					
					<input type='button' id='showCommentsButton' style=' width:150px; height:25px; display:none'  value='Show Comments ' 
						onclick="
							document.getElementById('hideCommentsButton').style.display='block';
							document.getElementById('showCommentsButton').style.display='none';
						
							var elements = document.getElementsByClassName('commentsDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'block';
							}
						"
					>
					</span>
				</td>







				<td style="width: 150px; background-color:<%=traceabilityColor%>;  text-align:center; vertical-align:middle; " >
				
					<span class='normalText'> 
					<input type='button' id='hideTraceabilityButton' style=' width:150px, height:25px '  value='Hide Traceability ' 
						onclick="
							document.getElementById('hideTraceabilityButton').style.display='none';
							document.getElementById('showTraceabilityButton').style.display='block';
									
							var elements = document.getElementsByClassName('traceabilityDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'none';
							}
						"
					>
					
					<input type='button' id='showTraceabilityButton' style=' width:150px; height:25px; display:none'  value='Show Traceability ' 
						onclick="
							document.getElementById('hideTraceabilityButton').style.display='block';
							document.getElementById('showTraceabilityButton').style.display='none';
						
							var elements = document.getElementsByClassName('traceabilityDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'block';
							}
						"
					>
					</span>
				</td>

				<td style="width: 150px; background-color:<%=approvalColor%>;  text-align:center; vertical-align:middle; " >
					<span class='normalText'>  
					<input type='button' id='hideApprovalButton' style=' width:150px, height:25px '  value='Hide Approval ' 
						onclick="
							document.getElementById('hideApprovalButton').style.display='none';
							document.getElementById('showApprovalButton').style.display='block';
									
							var elements = document.getElementsByClassName('approvalDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'none';
							}
						"
					>
					
					<input type='button' id='showApprovalButton' style=' width:150px; height:25px; display:none'  value='Show Approval ' 
						onclick="
							document.getElementById('hideApprovalButton').style.display='block';
							document.getElementById('showApprovalButton').style.display='none';
						
							var elements = document.getElementsByClassName('approvalDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'block';
							}
						"
					>
					</span>				
				</td>	
			</tr>
			
			
			
			
			
			
			
			
			
			<tr>


				<td style="width: 150px; background-color:<%=updatedNameColor%>;  text-align:center; vertical-align:middle; " >
				
					<span class='normalText'> 
					<input type='button' id='hideUpdatedNameButton' style=' width:150px, height:25px '  value='Hide Updated Name ' 
						onclick="
							document.getElementById('hideUpdatedNameButton').style.display='none';
							document.getElementById('showUpdatedNameButton').style.display='block';
									
							var elements = document.getElementsByClassName('updatedNameDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'none';
							}
						"
					>
					
					<input type='button' id='showUpdatedNameButton' style=' width:150px; height:25px; display:none'  value='Show Updated Name ' 
						onclick="
							document.getElementById('hideUpdatedNameButton').style.display='block';
							document.getElementById('showUpdatedNameButton').style.display='none';
						
							var elements = document.getElementsByClassName('updatedNameDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'block';
							}
						"
					>
					</span>
				</td>


			<td style="width: 150px; background-color:<%=updatedDescriptionColor%>;  text-align:center; vertical-align:middle; " >
				
					<span class='normalText'> 
					<input type='button' id='hideUpdatedDescriptionButton' style=' width:150px, height:25px '  value='Hide Updated Description ' 
						onclick="
							document.getElementById('hideUpdatedDescriptionButton').style.display='none';
							document.getElementById('showUpdatedDescriptionButton').style.display='block';
									
							var elements = document.getElementsByClassName('updatedDescriptionDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'none';
							}
						"
					>
					
					<input type='button' id='showUpdatedDescriptionButton' style=' width:150px; height:25px; display:none'  value='Show Updated Description ' 
						onclick="
							document.getElementById('hideUpdatedDescriptionButton').style.display='block';
							document.getElementById('showUpdatedDescriptionButton').style.display='none';
						
							var elements = document.getElementsByClassName('updatedDescriptionDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'block';
							}
						"
					>
					</span>
				</td>


				<td style="width: 150px; background-color:<%=updatedAttributeColor%>;  text-align:center; vertical-align:middle; " >
				
					<span class='normalText'> 
					<input type='button' id='hideUpdatedAttributeButton' style=' width:150px, height:25px '  value='Hide Updated Attribute ' 
						onclick="
							document.getElementById('hideUpdatedAttributeButton').style.display='none';
							document.getElementById('showUpdatedAttributeButton').style.display='block';
									
							var elements = document.getElementsByClassName('updatedAttributeDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'none';
							}
						"
					>
					
					<input type='button' id='showUpdatedAttributeButton' style=' width:150px; height:25px; display:none'  value='Show Updated Attribute ' 
						onclick="
							document.getElementById('hideUpdatedAttributeButton').style.display='block';
							document.getElementById('showUpdatedAttributeButton').style.display='none';
						
							var elements = document.getElementsByClassName('updatedAttributeDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'block';
							}
						"
					>
					</span>
				</td>

				<td style="width: 150px; background-color:<%=fileColor%>;  text-align:center; vertical-align:middle; " >
				
					<span class='normalText'> 
					<input type='button' id='hideFileButton' style=' width:150px, height:25px '  value='Hide File ' 
						onclick="
							document.getElementById('hideFileButton').style.display='none';
							document.getElementById('showFileButton').style.display='block';
									
							var elements = document.getElementsByClassName('fileDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'none';
							}
						"
					>
					
					<input type='button' id='showFileButton' style=' width:150px; height:25px; display:none'  value='Show File ' 
						onclick="
							document.getElementById('hideFileButton').style.display='block';
							document.getElementById('showFileButton').style.display='none';
						
							var elements = document.getElementsByClassName('fileDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'block';
							}
						"
					>
					</span>
				</td>

				<td style="width: 150px; background-color:<%=completionColor%>;  text-align:center; vertical-align:middle; " >
				
					<span class='normalText'> 
					<input type='button' id='hideCompletionButton' style=' width:150px, height:25px '  value='Hide Completion ' 
						onclick="
							document.getElementById('hideCompletionButton').style.display='none';
							document.getElementById('showCompletionButton').style.display='block';
									
							var elements = document.getElementsByClassName('completionDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'none';
							}
						"
					>
					
					<input type='button' id='showCompletionButton' style=' width:150px; height:25px; display:none'  value='Show Completion ' 
						onclick="
							document.getElementById('hideCompletionButton').style.display='block';
							document.getElementById('showCompletionButton').style.display='none';
						
							var elements = document.getElementsByClassName('completionDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'block';
							}
						"
					>
					</span>
				</td>

			</tr>
			
			





			<tr>
				<td style="width: 150px; background-color:<%=ownerColor%>;  text-align:center; vertical-align:middle; " >
				
					<span class='normalText'> 
					<input type='button' id='hideOwnerButton' style=' width:150px, height:25px '  value='Hide Owner ' 
						onclick="
							document.getElementById('hideOwnerButton').style.display='none';
							document.getElementById('showOwnerButton').style.display='block';
									
							var elements = document.getElementsByClassName('ownerDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'none';
							}
						"
					>
					
					<input type='button' id='showOwnerButton' style=' width:150px; height:25px; display:none'  value='Show Owner ' 
						onclick="
							document.getElementById('hideOwnerButton').style.display='block';
							document.getElementById('showOwnerButton').style.display='none';
						
							var elements = document.getElementsByClassName('ownerDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'block';
							}
						"
					>
					</span>
				</td>

				<td style="width: 150px; background-color:<%=createdReqColor%>;  text-align:center; vertical-align:middle; " >
				
					<span class='normalText'> 
					<input type='button' id='hideCreatedReqButton' style=' width:150px, height:25px '  value='Hide Created ' 
						onclick="
							document.getElementById('hideCreatedReqButton').style.display='none';
							document.getElementById('showCreatedReqButton').style.display='block';
									
							var elements = document.getElementsByClassName('createdReqDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'none';
							}
						"
					>
					
					<input type='button' id='showCreatedReqButton' style=' width:150px; height:25px; display:none'  value='Show Created ' 
						onclick="
							document.getElementById('hideCreatedReqButton').style.display='block';
							document.getElementById('showCreatedReqButton').style.display='none';
						
							var elements = document.getElementsByClassName('createdReqDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'block';
							}
						"
					>
					</span>
				</td>

				<td style="width: 150px; background-color:<%=deletedColor%>;  text-align:center; vertical-align:middle; " >
				
					<span class='normalText'> 
					<input type='button' id='hideDeletedButton' style=' width:150px, height:25px '  value='Hide Deleted  ' 
						onclick="
							document.getElementById('hideDeletedButton').style.display='none';
							document.getElementById('showDeletedButton').style.display='block';
									
							var elements = document.getElementsByClassName('deletedDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'none';
							}
						"
					>
					
					<input type='button' id='showDeletedButton' style=' width:150px; height:25px; display:none'  value='Show Deleted ' 
						onclick="
							document.getElementById('hideDeletedButton').style.display='block';
							document.getElementById('showDeletedButton').style.display='none';
						
							var elements = document.getElementsByClassName('deletedDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'block';
							}
						"
					>
					</span>
				</td>
				
				<td style="width: 150px; background-color:<%=restoredColor%>;  text-align:center; vertical-align:middle; " >
				
					<span class='normalText'> 
					<input type='button' id='hideRestoredButton' style=' width:150px, height:25px '  value='Hide Restored ' 
						onclick="
							document.getElementById('hideRestoredButton').style.display='none';
							document.getElementById('showRestoredButton').style.display='block';
									
							var elements = document.getElementsByClassName('restoredDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'none';
							}
						"
					>
					
					<input type='button' id='showRestoredButton' style=' width:150px; height:25px; display:none'  value='Show Restored ' 
						onclick="
							document.getElementById('hideRestoredButton').style.display='block';
							document.getElementById('showRestoredButton').style.display='none';
						
							var elements = document.getElementsByClassName('restoredDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'block';
							}
						"
					>
					</span>
				</td>
				
				<td style="width: 150px; background-color:<%=movedColor%>;  text-align:center; vertical-align:middle; " >
				
					<span class='normalText'> 
					<input type='button' id='hideMovedButton' style=' width:150px, height:25px '  value='Hide Moved ' 
						onclick="
							document.getElementById('hideMovedButton').style.display='none';
							document.getElementById('showMovedButton').style.display='block';
									
							var elements = document.getElementsByClassName('movedDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'none';
							}
						"
					>
					
					<input type='button' id='showMovedButton' style=' width:150px; height:25px; display:none'  value='Show Moved ' 
						onclick="
							document.getElementById('hideMovedButton').style.display='block';
							document.getElementById('showMovedButton').style.display='none';
						
							var elements = document.getElementsByClassName('movedDiv');
							for(var i=0, l=elements.length; i<l; i++){
							 elements[i].style.display = 'block';
							}
						"
					>
					</span>
				</td>

			</tr>
			
			<tr>
				<td colspan='4'>&nbsp;</td>
			</tr>
		</table>
	</div>
	
	<div id='displayChangeLog' class='level1Box' 
		style="overflow: auto; width: 1400px; height: 20px; 
						border-left: 1px white solid; ; 
						padding:0px; margin: 0px"> 
	
		<table class='paddedTable' width='100%'>
			<tr>
				<%if (sortBy.equals("changedDtUp")){ %>
					<td align="left" style="width: 180px; cursor:pointer; background-color:#99CCFF; "
			 		 onmouseout="style.backgroundColor='#99CCFF'" onmouseover="style.backgroundColor='pink';"
			 		 onclick='displayChangeLog("changedDtDown");'
			 		 title='Sort by Changed Date Ascending'
			 		> 
						<span class='sectionHeadingText'>
						<b>Change Dt</b> (Sorted Up)
						</span>
					</td>
				<%} 
				else if (sortBy.equals("changedDtDown")){ %>
					<td align="left" style="width: 180px; cursor:pointer; background-color:#99CCFF; "
			 		 onmouseout="style.backgroundColor=''#99CCFF''" onmouseover="style.backgroundColor='pink';"
			 		 onclick='displayChangeLog("changedDtUp");'
			 		 title='Sort by Changed Date Descending'
			 		> 
						<span class='sectionHeadingText'>
						<b>Change Dt</b> (Sorted Down)
						</span>
					</td>
				<%} 
				else {
				%>
				<td align="left" style="width: 180px; cursor:pointer; background-color:#99CCFF; "
			 		 onmouseout="style.backgroundColor='#99CCFF'" onmouseover="style.backgroundColor='pink';"
			 		 onclick='displayChangeLog("changedDtDown");'
			 			title='Sort by Changed Date Descending'
			 		> 
						<span class='sectionHeadingText'>
						<b>Change Dt</b> (Sorted Up)
						</span>
					</td>
				<%} %>
				



				<%if (sortBy.equals("changedByUp")){ %>
					<td align="left" style="width: 180px; cursor:pointer; background-color:#99CCFF "
			 		 onmouseout="style.backgroundColor='#99CCFF'" onmouseover="style.backgroundColor='pink';"
			 		 onclick='displayChangeLog("changedByDown");'
			 		 title='Sort by Changed By Descending'
			 		> 
						<span class='sectionHeadingText'>
						<b>Changed By</b> (Sorted Up)
						</span>
					</td>
				<%} 
				else if (sortBy.equals("changedByDown")){ %>
					<td align="left" style="width: 180px; cursor:pointer; background-color:#99CCFF; "
			 		 onmouseout="style.backgroundColor='#99CCFF'" onmouseover="style.backgroundColor='pink';"
			 		 onclick='displayChangeLog("changedByUp");'
			 			title='Sort by Changed By Ascending'
			 		> 
						<span class='sectionHeadingText'>
						<b>Change By</b> (Sorted Down)
						</span>
					</td>
				<%} 
				else {
				%>
				<td align="left" style="width: 180px; cursor:pointer; background-color: #99CCFF; "
			 		 onmouseout="style.backgroundColor='#99CCFF'" onmouseover="style.backgroundColor='pink';"
			 		 onclick='displayChangeLog("changedByUp");'
			 			title='Sort by Changed By Ascending'
			 		> 
						<span class='sectionHeadingText'>
						<b>Changed By</b>
						</span>
					</td>
				<%} %>




				
				<%if (sortBy.equals("changedObjectUp")){ %>
					<td align="left" style="width: 180px; cursor:pointer; background-color: #99CCFF; "
			 		 onmouseout="style.backgroundColor='#99CCFF'" onmouseover="style.backgroundColor='pink';"
			 		 onclick='displayChangeLog("changedObjectDown");'
			 			title='Sort by Changed Object Descending'
			 		> 
						<span class='sectionHeadingText'>
						<b>Changed Object</b> (Sorted Up)
						</span>
					</td>
				<%} 
				else if (sortBy.equals("changedObjectDown")){ %>
					<td align="left" style="width: 180px; cursor:pointer; background-color: #99CCFF; "
			 		 onmouseout="style.backgroundColor='#99CCFF'" onmouseover="style.backgroundColor='pink';"
			 		 onclick='displayChangeLog("changedObjectUp");'
			 			title='Sort by Changed Object Ascending'
			 		> 
						<span class='sectionHeadingText'>
						<b>Changed Object</b> (Sorted Down)
						</span>
					</td>
				<%} 
				else {
				%>
				<td align="left" style="width: 180px; cursor:pointer; background-color: #99CCFF; "
			 		 onmouseout="style.backgroundColor='#99CCFF'" onmouseover="style.backgroundColor='pink';"
			 		 onclick='displayChangeLog("changedObjectUp");'
			 			title='Sort by Changed Object Ascending'
			 		> 
						<span class='sectionHeadingText'>
						<b>Changed Object</b> 
						</span>
					</td>
				<%} %>
				
				<td align="left" style="background-color: #99CCFF; "
			 		title='Change Description'> 
				
					<span class='sectionHeadingText'>
					<b>Change Description</b>
					</span>
				</td>								
			</tr>
		</table>
	</div>
			
	<div id='displayChangeLogDetails' class='level1Box' 
		style="overflow: auto; width: 1400px; height: 700px; 
		border-left: 1px white solid; border-bottom: 1px gray solid; 
		padding:0px; margin: 0px"> 

			<table class='paddedTable' width='100%'>

			
			<%
				ArrayList projectChangeLog = ProjectUtil.getProjectCompleteChangeLog(project.getProjectId(), databaseType, changeFolderId, actorEmailId, changedSince, changeType, sortBy);
			    if (projectChangeLog != null){
			    	// lets get the actorEmailId + timestamp frequency
			    	HashMap frequencyMap = new HashMap();
			    	Iterator i = projectChangeLog.iterator();
			    	while (i.hasNext()) {
			    		ChangeLog changeLog = (ChangeLog) i.next();
			    		String fKey = changeLog.getActorEmailId() + changeLog.getActionDt();
			    		if (frequencyMap.containsKey(fKey)){
			    			Integer currentValue =  (Integer) frequencyMap.get(fKey);
			    			Integer newValue = new Integer( currentValue.intValue() + 1);
			    			frequencyMap.put(fKey, newValue);
			    		}
			    		else {
			    			frequencyMap.put(fKey, new Integer(1));
			    		}
			    	}
			    	
			    	
			    	i = projectChangeLog.iterator();
			    	
			    	while ( i.hasNext() ) {
			    		ChangeLog changeLog = (ChangeLog) i.next();
			    		
			    		
			    		String fKey = changeLog.getActorEmailId() + changeLog.getActionDt();
			    		Integer numberOfTimes  =  (Integer) frequencyMap.get(fKey);
		    			boolean bulkChange  = false;
		    			if (numberOfTimes.intValue() > 1) {
		    				bulkChange = true;
		    			}
			    		
		    			String backgroundColor = "white";
		    			String divClass = "generic";
					    if (bulkChange){
					    	divClass = "bulkChangeDiv";
					    	backgroundColor = bulkChangeColor;
					    }
					    else if (changeLog.getFullTag().equals("NA")){
					     	divClass = "projectDiv";
					     	backgroundColor = projectColor;
					    }
					    else if (
					    		(changeLog.getDescription().contains("Submitting Requirement for Approval"))
					    		||
					    		(changeLog.getDescription().contains("Approved Version"))
					    		||
					    		(changeLog.getDescription().contains("Rejected Version"))
					    		||
					    		(changeLog.getDescription().contains("has finally been Approved"))
					    		||
					    		(changeLog.getDescription().contains("has finally been Rejected"))
					    ){
					     	divClass = "approvalDiv";
					     	backgroundColor = approvalColor;
					    }
					    else if (changeLog.getLogType().equals("Comment")) {
					     	divClass = "commentsDiv";
					     	backgroundColor = commentColor;
					    }
					    else if (changeLog.getDescription().contains("Updated Name")) {
					     	divClass = "updatedNameDiv";
					     	backgroundColor = updatedNameColor;
					    }
					    else if (changeLog.getDescription().contains("Updated Description")) {
					     	divClass = "updatedDescriptionDiv";
					     	backgroundColor = updatedDescriptionColor;
					    }
					    else if (changeLog.getDescription().contains("Updated attribute")) {
					     	divClass = "updatedAttributeDiv";
					     	backgroundColor = updatedAttributeColor;
					    }
					    else if (
					    		(changeLog.getDescription().contains("Attached file"))
					    		||
					    		(changeLog.getDescription().contains("Deleted file"))
					    		){
					     	divClass = "fileDiv";
					     	backgroundColor = fileColor;
					    }
					    else if (changeLog.getDescription().contains("Setting Requirement Completed")) {
					     	divClass = "completionDiv";
					     	backgroundColor = completionColor;
					    }
					    else if (changeLog.getDescription().contains("Created Requirement ...")) {
					     	divClass = "createdReqDiv";
					     	backgroundColor = createdReqColor;
					    }
					    else if (changeLog.getDescription().contains("Trace")) {
					     	divClass = "traceabilityDiv";
					     	backgroundColor = traceabilityColor;
					    }
					    else if (changeLog.getDescription().contains("Setting Requirement Owner to")) {
					     	divClass = "ownerDiv";
					     	backgroundColor = ownerColor;
					    }
					    else if (changeLog.getDescription().contains("Requirement Deleted")) {
					     	divClass = "deletedDiv";
					     	backgroundColor = deletedColor;
					    }
					    else if (changeLog.getDescription().contains("Requirement Restored")) {
					     	divClass = "restoredDiv";
					     	backgroundColor = restoredColor;
					    }
					    else if (changeLog.getDescription().contains("Moved Requirement to")) {
					     	divClass = "movedDiv";
					     	backgroundColor = movedColor;
					    }
					    
			%>
			<tr  class='<%=divClass%>' style='background-color:<%=backgroundColor%>'>
			 	<td>
				<div>
					<table width='100%'>
						<tr>
					 		<td style="width: 180px;">
					 			<span class='normalText'>
					 			<%=changeLog.getActionDt() %>
					 			</span>
					 		</td>
					 		<td  style="width: 180px;">
					 			<span class='normalText'>
					 			<%=changeLog.getActorEmailId() %>
					 			</span>
					 		</td>	
					 		
					 		<%
					 		if (changeLog.getFullTag().equals("NA")){
							%>
					 			<td align="left" style="width: 180px; align:center">
						 		 <span class='normalText'>NA</span>
						 		 </td>
					 		<%} 
					 		else {
					 			String url = ProjectUtil.getURL(request,changeLog.getRequirementId(),"requirement");
					 		
					 		%>
					 			<td align="left" style="width: 180px; align:center">
						 		 <span class='normalText'>
						 		 	<a target="_blank" href="<%=url%>"><%=changeLog.getFullTag() %></a>
						 		 </span>
						 		 </td>
					 		
					 		<%} %>
					 							
					 		
					 		
					 				
							<td >
					 			<span class='normalText'>
					 			<% if (changeLog.getLogType().equals("Comment")) {%>
					 			
					 			<img src="/GloreeJava2/images/comments16.png" border="0">
					 			
					 			<%} %>
					 			
					 			<%=changeLog.getDescription() %>
					 			</span>
					 		</td>	
			 				</tr>
			 			</table>
			 		</div>
			 		</td>	
				</tr>
			 <%
			    	}
			    }
			%>
		</table>
	</div>
<%}%>