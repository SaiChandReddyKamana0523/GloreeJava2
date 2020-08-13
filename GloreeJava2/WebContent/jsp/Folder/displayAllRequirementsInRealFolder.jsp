
	<!-- GloreeJava2 -->
	<!-- pageEncoding -->
	<%@page contentType="text/html;charset=UTF-8"%>
	
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	


	<%
		// authentication only
		String displayAllRequirementsInRealFIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
		if ((displayAllRequirementsInRealFIsLoggedIn  == null) || (displayAllRequirementsInRealFIsLoggedIn.equals(""))){
			// this means that the user is not logged in. So lets forward him to the 
			// log in page.
	%>
			<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
	<% }
		String databaseType = this.getServletContext().getInitParameter("databaseType");
		Project project= (Project) session.getAttribute("project");
		SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
		User user = securityProfile.getUser();
		String userEmailId = user.getEmailId();

		boolean isUserAnAdmin = false; 
		if (securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
			isUserAnAdmin = true;
		}
		
		
		// lets see if this user is a member of this project.
		// we are leaving this page open to member of this project (which includes admins also)
		boolean isMember = false;
		if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
			isMember = true;
		} 
		
		if (isMember){
	
		Folder folder = (Folder) request.getAttribute("folder");
		
			
			// if the folder object exists, it means a call was made to FolderAction with a request to create a folder.
			// the folder object now contains the data for the newly created folder.
		    if (folder == null) { 
		    	// This means that no new folders were created prior to this call.
		    	String folderIdString = request.getParameter("folderId");
		    	int folderId = Integer.parseInt(folderIdString);
		    	folder = new Folder(folderId);	
		    }
		    ArrayList<Role> approvalRoles = folder.getApproveRequirementRoles(folder.getProjectId());
		   
			
			// if project is of the resource type and req type is RES type, then lets forward to the other display jsp
			
			if (
					(project.getProjectType().equals("resource management") )
					&&
					(folder.getRequirementTypeShortName().equals("RES"))
				){
				%>
					<jsp:forward page="/jsp/Folder/displayAllRequirementsInRealFolderResourceManagement.jsp"/>
				<%
				
			}
			if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
					+ folder.getFolderId()))){
				%>
				<div class='alert alert-success'>	
						<span class='subSectionHeadingText'>
						You do not have READ permissions on this folder. 
						</span>
						</div>
				<%
				return;	
			}
		
			// Delete and Purge buttons are controlled by the 'Delete Requirement' privilege.
			boolean deleteDisabled = false;
			if (!(securityProfile.getPrivileges().contains("deleteRequirementsInFolder" 
					+ folder.getFolderId() ))){
				deleteDisabled = true;
			}
	
			
	
			boolean updateDisabled = false ;
			String updateRequirementsDisableString = "";
			if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
					+ folder.getFolderId()))){
			
				updateDisabled = true;

				updateRequirementsDisableString = "disabled='disabled'";
			}
			
			boolean canCreate = false;
			if ((securityProfile.getPrivileges().contains("createRequirementsInFolder" 
					+ folder.getFolderId()))){
			
				canCreate = true;
			}
			
			
			boolean percentageCompleteDisabled = false;
			int percentageCompletedDriverReqTypeId = project.getPercentageCompletedDriverReqTypeId();
			if (
				(percentageCompletedDriverReqTypeId > 0 ) 
				&&
				(project.getPercentageCompletedDriverReqTypeId() != folder.getRequirementTypeId())
			){
				// If this project has a percentageCompletedDriverReqTypeId value set (>0) 
				// and this requirement does not belong to the percentage complete driver
				// the update should be disabled. 
				percentageCompleteDisabled = true;
			}
			
			
			boolean canBeReportedDangling = folder.canBeReportedDangling();
			boolean canBeReportedOrphan = folder.canBeReportedOrphan();
			
			String sortBy = request.getParameter("sortBy");
			if (
					(sortBy == null) || (sortBy.equals("undefined"))
				)
					{sortBy = "";}
			
			
			// lets check the filter criteria
    		String filterType = request.getParameter("filterType");
    		String filterValue = request.getParameter("filterValue");
    
    		HashMap <String, Object> folderPreferences = FolderUtil.getUserFolderPreferences(user.getUserId(), folder.getFolderId());
    		
    		HashMap<String, String> folderFilters = new HashMap<String, String>();
    		ArrayList<String> showAttributes = new ArrayList<String>() ;
    		try {
    			folderFilters = (HashMap<String,String>) folderPreferences.get("folderFilters");
    			showAttributes =   (ArrayList<String>) folderPreferences.get("showAttributes");
    		}
    		catch (Exception e){
    			e.printStackTrace();
    		}
    		
    		
			
			if (folderFilters == null){
				folderFilters = new HashMap<String, String>();
			}
			if (showAttributes == null){
				showAttributes = new ArrayList<String>();
				// lets add DDA
				ArrayList<String> defaultDisplayAttributes = folder.getDefaultDisplayAttributes();
				showAttributes = defaultDisplayAttributes;
				showAttributes.add("description:##:description");
				showAttributes.add("status:##:status");
				
			}
			
			
			
    		if ((filterType != null) && (!filterType.equals(""))){
    			// since a filter has been sent in, lets do the following 
    			// Retrive the older folderFilters HashMap from session
    			// and add this new filter to that HashMap and put that back in session.
    			
    			
    			// since we want to force our filters to be unique, we will key off filterValue. if we were to key off filterType, then the folderFilters
    			// will only hold 2 objects. 
    			// if filterType == removeFilter, then remove the filterValue from the HshMap. Else add the filter to the hashMap
    			
    			if (filterType.equals("removeFilter")){
    				// remove filter.
    				folderFilters.remove(filterValue);
    			}
    			else {
    				// this must be a new add filter
    				folderFilters.put(filterValue, filterType);
    			}
    			//session.setAttribute( "folderFilters" + folder.getFolderId(), folderFilters);	
    				
    			// if a filterType is hideAttribute, lets remove it from displayedAttributes and add it to showAttributes
    			if (filterType.equals("hideAttribute")){
    				String attributeValue = filterValue;
    				showAttributes.remove(attributeValue);
    			}
    			if (filterType.equals("showAttribute")){
    				String attributeValue = filterValue;
    				showAttributes.add(attributeValue);
    			}
    			//session.setAttribute( "folderShowAttributes" + folder.getFolderId(), showAttributes);	
    			FolderUtil.setUserFolderPreferences(user.getUserId(), folder.getFolderId(), folderFilters, showAttributes);
    		}
    		
			ArrayList<Requirement> requirements = folder.getMyRequirementsSorted(project.getProjectId(), databaseType, sortBy, folderFilters);
			
			// lets get a list of currently displayed attributes and hidden attributes
			// we will use 2 arraylists. displayed attributes and hidden attributes
			
			
			RequirementType rT = new RequirementType(folder.getRequirementTypeId());
			ArrayList<RTAttribute> attributes = rT.getAllAttributesInRequirementType();
			
			ArrayList<String> displayedAttributes = new ArrayList<String>();
			displayedAttributes = showAttributes;
			
			ArrayList<String> missingAttributes  = new ArrayList<String>();
			for (RTAttribute rta : attributes ){
				String attributeString = rta.getAttributeId() + ":##:" + rta.getAttributeName();
				if (!(displayedAttributes.contains(attributeString))){
					missingAttributes.add(attributeString);
					
				}
			}
			if (!(displayedAttributes.contains("description:##:description"))){
				missingAttributes.add("description:##:description");
			}
			if (!(displayedAttributes.contains("status:##:status"))){
				missingAttributes.add("status:##:status");
			}
			
			
			

			 String displayDescriptionFlag = "block";
			 if (!(displayedAttributes.contains("description:##:description"))) { 
				 displayDescriptionFlag = "none";
			 }
			 String displayStatusBarFlag = "block";
			 if (!(displayedAttributes.contains("status:##:status"))) { 
				 displayStatusBarFlag = "none";
			 }
			
			// 
			// for pagination, lets set the pageSize.
			int pageSize = 20;
			
			try {
				// get the session object prefRowsPerPageInteger
				Integer prefRowsPerPageInteger = (Integer) session.getAttribute("prefRowsPerPage");
				
				if (prefRowsPerPageInteger == null ){
					// if null, go ahead get it from db and then set it in the session.
					int prefRowsPerPage = user.getPrefRowsPerPage();
					
					if (prefRowsPerPage == 0 ){
						// if the db brings back 0, then update db to a default 20 value.
						// lets update the db with some default pref value
						prefRowsPerPage = 20;
						user.setPrefRowsPerPage(prefRowsPerPage);
					}
					// lets set the session attribute.
					session.setAttribute("prefRowsPerPage", new Integer(prefRowsPerPage));
					
					pageSize = prefRowsPerPage;
				}
				else {
					// at this point the session attribute has some value.
					pageSize = prefRowsPerPageInteger.intValue();
				}
			}
			catch (Exception e){
				pageSize = 20;
				e.printStackTrace();
			}
			
			try {
				// lets see if we were sent in a newPreferenceFor rowsPerPage.
				int newRowsPerPagePref = 0;
				try {
					newRowsPerPagePref = Integer.parseInt(request.getParameter("newRowsPerPagePref"));
				}
				catch (Exception e){
					// do nothing.
					
				}
				if ((newRowsPerPagePref > 0 ) && (newRowsPerPagePref != pageSize )){
					// oh oh. we were sent in a new pref
				
					user.setPrefRowsPerPage(newRowsPerPagePref);
					
					session.setAttribute("prefRowsPerPage", new Integer(newRowsPerPagePref));
					pageSize = newRowsPerPagePref;
				}
				
			}
			catch (Exception e){
				e.printStackTrace();
			}
			
			// we add 1 to arraysize/pageSize because, int div truncates things.
			int numOfPages = (requirements.size() / pageSize) + 1 ; 	
			
					
			int pageToDisplay = 1;
			if (request.getParameter("page") != null){
				pageToDisplay = Integer.parseInt(request.getParameter("page"));	
			}
			int pageStartIndex = (pageToDisplay * pageSize) - pageSize;
			int pageEndIndex = pageStartIndex + pageSize;
			if (pageEndIndex > requirements.size()){
				pageEndIndex = requirements.size();
			}
			
			
			String pageString = "";
			for (int i=1;i<=numOfPages;i++){
				if (i == pageToDisplay){
					pageString += "<b>" +  i + "</b>&nbsp;&nbsp;";
				}
				else {
					pageString += "<a href='#' onclick='reportPagination(\"requirementsInRealFolder\"," 
						+ folder.getFolderId() + "," +
						i +  ", \"" + sortBy + "\")'> " + i + " </a>" ;
					pageString += "&nbsp;&nbsp;";	
				}
			}
			// drop the last nbsp;
			pageString = (String) pageString.subSequence(0,pageString.lastIndexOf("&nbsp;&nbsp;"));
			
			// lets handle the case of virtual folder parent where the folder does not exist.
			// we don't want to display NULL . so this work around.
			String folderName = "";
			folderName = folder.getFolderName();
			if (folderName == null){
				folderName = "";
			}
			
			
			int folderEnabledForApproval = folder.getIsFolderEnabledForApproval();
		%>
		
		
				
				<ul class="nav nav-tabs">
				  <li class="active">
				  	<a   data-toggle="tab" href="#listContentsDiv"
					 		onclick='
					 			document.getElementById("listContentsDiv").style.display="block";
								document.getElementById("metricsDiv").style.display="none";
								document.getElementById("commentsDiv").style.display="none";
								document.getElementById("changesDiv").style.display="none";
								document.getElementById("folderInfoDiv2").style.display="none";
								
							'
					>  
					<span class="glyphicon glyphicon-list " style=" color: blue; font-size:1.5em  " ></span> &nbsp;&nbsp; Folder Contents 
					</a></li>
					
					<%
					if (!(project.getProjectTags().toLowerCase().contains("hide_foldermetrics"))){
					%>	
					  <li style='display:block'>
					  	<a data-toggle="tab" href="#metricsDiv" 	
					  			onclick='
					 			document.getElementById("listContentsDiv").style.display="none";
								document.getElementById("metricsDiv").style.display="block";
								document.getElementById("commentsDiv").style.display="none";
								document.getElementById("changesDiv").style.display="none";
								document.getElementById("folderInfoDiv2").style.display="none";
								showMyStatusForAProject();
							'
					> <span class="glyphicon glyphicon-dashboard " style=" color: red; font-size:1.5em " ></span> Folder Metrics</a></li>
				  <%}%>
				  
				  
				 	 <li style='display:block'>
				  	<a data-toggle="tab" href="#commentsDiv" 	
				  			onclick='
					 			document.getElementById("listContentsDiv").style.display="none";
								document.getElementById("metricsDiv").style.display="none";
								document.getElementById("commentsDiv").style.display="block";
								document.getElementById("changesDiv").style.display="none";
								document.getElementById("folderInfoDiv2").style.display="none";
								fillRecentlyCommentedReqsForAFolder(<%=folder.getFolderId() %>);
							'
							
					> <span class="glyphicon glyphicon-comment " style=" color: green; font-size:1.5em " ></span> Recent Comments in Folder</a></li>
					
					<li style='display:block'>
				  	<a data-toggle="tab" href="#changesDiv" 	
				  			onclick='
					 			document.getElementById("listContentsDiv").style.display="none";
								document.getElementById("metricsDiv").style.display="none";
								document.getElementById("commentsDiv").style.display="none";
								document.getElementById("changesDiv").style.display="block";
								document.getElementById("folderInfoDiv2").style.display="none";
								fillRecentlyChangedReqsForAFolder(<%=folder.getFolderId() %>);
							'
							
					>  <span class="glyphicon glyphicon-pencil " style=" color: blue; font-size:1.5em " ></span> Recent Changes in Folder</a></li>
					
					<li style='display:block'>
				  	<a data-toggle="tab" href="#folderInfoDiv" 	
				  			onclick='
					 			document.getElementById("listContentsDiv").style.display="none";
								document.getElementById("metricsDiv").style.display="none";
								document.getElementById("commentsDiv").style.display="none";
								document.getElementById("changesDiv").style.display="none";
								document.getElementById("folderInfoDiv2").style.display="block";
								displayFolderCoreInTab(<%=folder.getFolderId() %>);
					
							'
							
					> <span class="glyphicon glyphicon-info-sign " style=" color: blue; font-size:1.5em " ></span>  Folder Info</a></li>
					
					
				  </ul>				
				
				
				
							
				
				<div id='listContentsDiv' class="tab-pane fade in active" > 
					<div id='displayAllRequirementsDiv' class='invisibleLevel1Box' >
							<% if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
									+ folder.getFolderId()))){
								%>
								<table class='paddedTable' >
								
								<tr>
									<td align='left' colspan='2'>				
										<div class='alert alert-success'>	
										<span class='subSectionHeadingText'>
										You do not have READ permissions on this folder. 
										</span>
										</div>
									</td>
								</tr>
								</table>
							<%}
							else {%>
							 
							<table class='paddedTable'   width=100% >
							   <tr>
							 			<td>
							 				<div class='alert alert-info' style='width:100%'>
							 				<%
							 				for (Map.Entry<String, String> entry : folderFilters.entrySet()) {
											    String fV = entry.getKey();
											    String fT = entry.getValue();
											    if ( (fT.equals("hideAttribute")) || (fT.equals("showAttribute"))){
											    	continue;
											    }
											    String unFormattdFilterValue = fV;
											    if (!(fV.equals("undefined") )){
											    	String filterSign = "";
											    	if (fT.equals("showMatching")){
											    		filterSign = " = ";
											    	}
											    	if (fT.equals("filterOut")){
											    		filterSign = " != ";
											    	}
											    	
											    	String[] fArray = fV.split(":#:");
											    	String attributeLabel = fArray[0]; 
											    	String attributeValue = " Empty ";
											    	if (fArray.length > 1){
											    		attributeValue =  fArray[1];
											    	}
											    	
											    %>
											   	 <span class="glyphicon glyphicon-remove " style=" color: red; cursor:pointer;" 
											   	 	onclick='displayAllRequirementsInRealFolder(<%=folder.getFolderId() %>,"","removeFilter","<%=unFormattdFilterValue%>");'>
											   	 
											   	 </span>
											   	 <%=attributeLabel%> <%=filterSign %> <%=attributeValue %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											    <%
											    }
											    
							 				}
							 				
							 				%>
							 				</div>
							 			</td>
							 		</tr>	
							   <tr>
									
									<td  > 
										<% if (requirements.size() > 0 ){ %>
										
										<div id = 'folderDetails' class='level2Box' style='float:left'>
											
													<div> 
														<table >
															<tr>
															<td>
													 					
													 					
													 					
																		
																		<div class="btn-group">
															
																		  <button type="button"
																		  class="btn btn-xs btn-outline-primary dropdown-toggle"  
																			style="border-color:black; color:black; background-color:white;"
																			 data-toggle="dropdown">
																		    Show <span class="caret"></span>
																		  </button>
																			  
																			 <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu">
																			 
															                  <li id='showCommentsMenu' style='display:block'>
															                  	<a tabindex="-1" href="#"
															                  		onClick="
																				 		document.getElementById('showCommentsMenu').style.display='none';
																						document.getElementById('hideCommentsMenu').style.display='block';
																				 		
																						var elements = document.getElementsByClassName('commentBox');
																						for(var i=0, l=elements.length; i<l; i++){
																						 elements[i].style.display = 'block';
																						}
																					"
																				>Comments</a></li>
																				
															                  
															                  <% if (missingAttributes.contains("status:##:status")) { %>
															                 		<li style='display:block'>
														                  			<a tabindex="-1" href="#"
														                  			onclick='displayAllRequirementsInRealFolder(<%=folder.getFolderId() %>,"","showAttribute","status:##:status");'>
														                  			Status Bar
														                  			</a></li>
															                 <%} %>
															                  <% if (missingAttributes.contains("description:##:description")) { %>
															                 		<li style='display:block'>
														                  			<a tabindex="-1" href="#"
														                  			onclick='displayAllRequirementsInRealFolder(<%=folder.getFolderId() %>,"","showAttribute","description:##:description");'>
														                  			Description 
														                  			</a></li>
															                 <%} %>
															                  
															                  <li class="divider"></li>
															                  
															                  
															                  <%
															                  	for (String attributeString : missingAttributes){
															                  		
																					String[] aStuff = attributeString.split(":##:");
																					String aId = aStuff[0];
																					String aName = aStuff[1];
															                  	%>
															                  		<li style='display:block'>
															                  		<a tabindex="-1" href="#"
															                  		onclick='displayAllRequirementsInRealFolder(<%=folder.getFolderId() %>,"","showAttribute","<%=attributeString%>");'>
															                  		<%=aName%></a></li>
															                  	<%
															                  	}
															                  %>
															                </ul>
															            </div>
															
	
	
																			
																		<div class="btn-group">
															
																		  <button type="button"
																		   class="btn btn-xs btn-outline-primary dropdown-toggle"  
																			style="border-color:black; color:black; background-color:white;"
																				 data-toggle="dropdown">
																		    Hide <span class="caret"></span>
																		  </button>
  
																			 <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu">
																			 <li id='hideCommentsMenu' style='display:none'>
															                  	<a tabindex="-1" href="#"
															                  		onClick="
																				 		document.getElementById('showCommentsMenu').style.display='block';
																				 		document.getElementById('hideCommentsMenu').style.display='none';
																				 		
																						var elements = document.getElementsByClassName('commentBox');
																						for(var i=0, l=elements.length; i<l; i++){
																						 elements[i].style.display = 'none';
																						}
																					"
															                  	
															                  	>Comments</a></li>
															                  
															                  
															                  <% if (displayedAttributes.contains("status:##:status")) { %>
															                 <li style='display:block'>
															                  			<a tabindex="-1" href="#"
															                  			onclick='displayAllRequirementsInRealFolder(<%=folder.getFolderId() %>,"","hideAttribute","status:##:status");'>
															                  			Status Bar 
															                  			</a></li>
															                 <%} %>
															                 <% if (displayedAttributes.contains("description:##:description")) { %>
															                 <li style='display:block'>
															                  			<a tabindex="-1" href="#"
															                  			onclick='displayAllRequirementsInRealFolder(<%=folder.getFolderId() %>,"","hideAttribute","description:##:description");'>
															                  			Description 
															                  			</a></li>
															                 <%} %>
															                <li class="divider"></li>
															                   <%
															                  	for (String attributeString : displayedAttributes){	
																					String[] aStuff = attributeString.split(":##:");
																					String aId = aStuff[0];
																					String aName = aStuff[1];
															                  	%>
															                  		<li style='display:block'>
															                  			<a tabindex="-1" href="#"
															                  			onclick='displayAllRequirementsInRealFolder(<%=folder.getFolderId() %>,"","hideAttribute","<%=attributeString%>");'>
															                  			<%=aName%>
															                  			</a></li>
															                  	<%
															                  	}
															                  %>
															                </ul>
															            </div>
															
																		
																		&nbsp;&nbsp;
																		
																</td>
																<td>
																	<span class='normalText'>
											
																		 <select id='newRowsPerPagePref' 
																		 	onChange='displayAllRequirementsInRealFolder(<%=folder.getFolderId() %>);'
																		 	style='height:25px;'
																		 >
																			<%
																			for(int i=1;i<11;i++){
																				int newRowsPerPagePref = i*10;
																					if (newRowsPerPagePref==pageSize){
																					%>
																						<option value='<%=newRowsPerPagePref%>' SELECTED><%=newRowsPerPagePref %> rows per page </option>
																			 		<%
																					}
																					else {
																					%>
																						<option value='<%=newRowsPerPagePref%>' ><%=newRowsPerPagePref %> rows per page </option>
																			 		<%
																					}
																			}
																			for(int i=2;i<6;i++){
																				int newRowsPerPagePref = i*100;
																					if (newRowsPerPagePref==pageSize){
																					%>
																						<option value='<%=newRowsPerPagePref%>' SELECTED><%=newRowsPerPagePref %> rows per page </option>
																			 		<%
																					}
																					else {
																					%>
																						<option value='<%=newRowsPerPagePref%>' ><%=newRowsPerPagePref %> rows per page </option>
																			 		<%
																					}
																			}
																			%>
																			</select>
																		</span>
																</td>
																<td>
																	Page <%=pageString%>
													 			 </td>
													 			<td>
													 				
																</td>
													 			
																
													 		</tr>	
													 			
														</table>
														  
														
														
													</div>
																		
											</span>
										</div>
										<%} %>
								 
									</td>				
									
								</tr>
							</table>
							<table class='table'   width=100% border='0'>
							
							
								<tr>
									<td colspan='3' style='border-top:none;'>
										<div id='displayRequirementsSectionInFolderDiv'>
										
										<table class='table ' id='requirementsTable' width='100%' border='0'>
								<%
								
								
								    if (requirements != null){
								    	%>
					    				<tr>
					    					
					    					<%if (!(project.getProjectTags().toLowerCase().contains("hide_statusbar"))){ %>
								    		<td style='min-width:150px; text-align:left; display:<%=displayStatusBarFlag%>'  > 
								    			<span class='sectionHeadingText'>Status</span>
								    		</td>
								    		<%} %>
								    		<td >
								    			
								    		</td>
								    		<%
								    			if (sortBy.equals("name")){
								    		%>
									    		<td class='info' style='min-width:600px; cursor:pointer; text-align:left' 
									    			onmouseout="this.style.backgroundColor='#d9edf7'"
									    			onclick="displayAllRequirementsInRealFolder(<%=folder.getFolderId() %> );"
									    		>
									    			<span class='sectionHeadingText'><%=folder.getRequirementTypeName()%> Name</span>
									    			
									    		</td>
								    		<%
								    			}
								    			else
								    			{
								    		%>
								    			<td style='min-width:600px; cursor:pointer; text-align:left' 
									    			
									    			onclick="displayAllRequirementsInRealFolder(<%=folder.getFolderId() %>, 'name' );"
									    		>
									    			<span class='sectionHeadingText'><%=folder.getRequirementTypeName()%> Name</span>
									    			<span class="glyphicon glyphicon-sort"></span>
									    		</td>
								    		
								    		<%	} %>
								    		<%
											int columnCount = 4 + displayedAttributes.size();
				
											if (folder.getRequirementTypeName().toLowerCase().equals("release")) {
												columnCount++;
											}
								    		if (folderEnabledForApproval == 1) {
												columnCount++;
								    		}
											%>
											
											
											<%
											if (folderEnabledForApproval == 1) {
								    			if (sortBy.equals("approvalStatus")){
								    		%>
									    		<td class='info' style='min-width:180px; cursor:pointer; text-align:left' 
									    			onmouseout="this.style.backgroundColor='#d9edf7'"
									    			onclick="displayAllRequirementsInRealFolder(<%=folder.getFolderId() %> );"
									    		>
									    			<span class='sectionHeadingText'>Approval Status</span>
									    		</td>
									    		
								    		
								    		<%
								    			}
								    			else
								    			{
								    		%>
								    			<td style='min-width:180px; cursor:pointer; text-align:left' 
									    			
									    			onclick="displayAllRequirementsInRealFolder(<%=folder.getFolderId() %> , 'approvalStatus' );"
									    		>
									    			<span class='sectionHeadingText'>Approval Status</span>
									    			<span class="glyphicon glyphicon-sort"></span> 
									    		</td>
									    		
								    		
								    		<%	} 
								    		}%>
								    		<%
				
											if (displayedAttributes.size() > 0 ){
												Iterator ddA  = displayedAttributes.iterator();
												while (ddA.hasNext()){
													String a = (String) ddA.next();
													String[] aStuff = a.split(":##:");
													String aId = aStuff[0];
													String aName = aStuff[1];
													
													String sortByAttribName = "CustomAttribute" + aName;
													
													if (aName.equals("status")){continue;}
													if (aName.equals("description")){continue;}
													
													 %>		
													<%
										    			if (sortBy.equals(sortByAttribName)){
										    		%>
											    		<td class='info' style='min-width:180px; cursor:pointer; text-align:left' 
											    			onmouseout="this.style.backgroundColor='#d9edf7'"
											    			onclick="displayAllRequirementsInRealFolder(<%=folder.getFolderId() %> );"
											    		>
											    			<span class='sectionHeadingText'><%=aName %></span>
											    		</td>
											    			
										    		<%
										    			}
										    			else
										    			{
										    		%>
										    			<td style='min-width:180px; cursor:pointer; text-align:left' 
											    			
											    			onclick="displayAllRequirementsInRealFolder(<%=folder.getFolderId() %> , '<%=sortByAttribName %>' );"
											    		>
											    			<span class='sectionHeadingText'><%=aName %></span>
											    			 <span class="glyphicon glyphicon-sort"></span> 
											    		</td>
											    		
										    		
										    		<%	} %>									 
								 					
												<%
												}
											}
											%>
								    		
								    		<%if (folder.getRequirementTypeName().toLowerCase().equals("release")) { %>
								    		<td style='min-width:200px; text-align:left' >
								    			<span class='sectionHeadingText'>Release Metrics</span>
								    		</td>
								    		<%} %>
								    		
								    	</tr>
								    	
								    	<%
					
								    	for (int i=pageStartIndex; i<pageEndIndex;i++){
								    		int j = i;
								    		Requirement r = (Requirement) requirements.get(i);
											
								    		
								    		
								    		
								    		
								    		String color = "white";
											if (r.getUserDefinedAttributes().toLowerCase().contains("color:#:")){
												color = r.getAttributeValue("color");
												
												if ((color == null ) || (color.trim().equals("") )){
													color = "white";
												}
											}
											String displayRDInFolderDiv = "displayRDInFolderDiv" + r.getRequirementId();
											String displayRDAlertInFolderDiv = "displayRDAlertInFolderDiv" + r.getRequirementId();
				
								    		String displayRequirementInFolderDiv = "displayRequirementInFolderDiv" + r.getRequirementId();
								    		
								 %>
								 				<tr bordercolor='red' >
								 					
								 				
								 				
													<%if (!(project.getProjectTags().toLowerCase().contains("hide_statusbar"))){ %>								 			
								 					<td  style='min-width:150px; text-align:left; display:<%=displayStatusBarFlag%> ' 
													>
															<div id='reqStatusBox<%=r.getRequirementId() %>'>
															<table class='paddedTable' border='1' style='background-color:<%=color%>' border=2 >
																<tr>
																
																	<td width='20px' align='center'>
																		<%if (!(r.getRequirementLockedBy().equals(""))){
																		// this requirement is locked. so lets display a lock icon.
																		%>
																			
																			<span class='normalText' title='Requirement locked by <%=r.getRequirementLockedBy()%>'> 
																				<img src="/GloreeJava2/images/lock16.png" border="0"> 
																			</span>
																			
																		<%
																		}
																		else {
																		%>
																			<span class='normalText' title='Requirement not locked'> 
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																			</span>
																			
																		
																		<%
																		}
																		%>
																	</td>											
																	
					
					
																	
																	
																	
																	<% if (r.getRequirementTraceTo().length() == 0 ) { 
																		if (canBeReportedOrphan){
																	%>
																		<td title='This requirement is an Orphan, i.e does not trace to Requirements upstream ' width='20px' align='center' style="background-color:lightgray">
																			<b><font size='4' color='red'>O</font></b>
																		</td>
																	<%
																		}
																		else {
																		%>
																		<td width='20px'></td>
																		<%	
																		}
																	}
																	else if(r.getRequirementTraceTo().contains("(s)")) { %>
																		<td title='There is a suspect upstream trace' width='20px' align='center' style="background-color:pink">
																			<img src="/GloreeJava2/images/arrow_up.png"> 
																		</td>
																	<%}
																	else { %>
																		<td title='All upstream traces are clear' width='20px' align='center' style="background-color:lightgreen">
																			<img src="/GloreeJava2/images/arrow_up.png"> 
																		</td>
																	
																	<%} %>
																	
																	
																	
																	<% if (r.getRequirementTraceFrom().length() == 0 ) { 
																		if (canBeReportedDangling) {%>
																		<td title='This requirement is a Dangling Requirement i.e does not have downstream traces' width='20px' align='center' style="background-color:lightgray">
																			<b><font  size='4' color='red'>D</font></d> 
																		</td>
																	<%
																		}
																		else {
																			%>
																			<td width='20px'></td>
																			<%
																		}
																	}
																		else if(r.getRequirementTraceFrom().contains("(s)")) { %>
																		<td title='There is a suspect downstream trace' width='20px' align='center' style="background-color:pink">
																			<img src="/GloreeJava2/images/arrow_down.png"> 
																		</td>
																	<%}
																		else {%>
																		<td title='All downstream traces are clear' width='20px' align='center' style="background-color:lightgreen">
																			<img src="/GloreeJava2/images/arrow_down.png"> 
																		</td>
																	
																	<%} %>
																	
																	
																	
																	<%if (r.getTestingStatus().equals("Pending")){ %>		
																		<td title='Testing is Pending' width='20px' align='center' style="background-color:lightgray">
																			&nbsp;&nbsp;&nbsp;&nbsp;
																		</td>
																	<%}
																	else if (r.getTestingStatus().equals("Pass")){ %>
																		<td title='Testing Passed' width='20px' align='center' style="background-color:lightgreen">
																			<img src="/GloreeJava2/images/testingPassed.png"> 
																		</td>
																	<%}
																	else {%>
																		<td title='Testing Failed' width='20px' align='center' style="background-color:pink">
																			<img src="/GloreeJava2/images/testingFailed.png"> 
																		</td>
																	
																	<%} %>
																	
																	
																	
																	
																	
																	<%if (r.getRequirementPctComplete() == 100){%>
																	
																		<td title='Percent of work completed'  align='center' style="background-color:lightgreen">
																			<%=r.getRequirementPctComplete()%>%
																		</td>
																	<%}
																	else if (r.getRequirementPctComplete() == 0){ %>
																		<td title='Percent of work completed'  align='center' style="background-color:lightgray">
																			&nbsp;&nbsp;&nbsp;<%=r.getRequirementPctComplete()%>%
																		</td>
																	
																	<%}
																	else {%>
																		<td title='Percent of work completed'  align='center'style="background-color:pink">
																			&nbsp;&nbsp;<%=r.getRequirementPctComplete()%>%
																		</td>
																	<%}%>												
																	
																	
																	<% if (project.getHidePriority() != 1){ %>
																		
																		<%if (r.getRequirementPriority().equals("High")){%>
																		
																			<td title='Requirement Priority is High'  align='center' style="background-color:lightgray">
																			<font color='red'><b>H</b></font>
																			</td>
																		<%}
																		else if (r.getRequirementPriority().equals("Medium")){%>
																			<td title='Requirement Priority is Medium'  align='center' style="background-color:lightgray">
																				<font color='blue'><b>M</b></font>
																			</td>
																		
																		<%}
																		else {%>
																			<td title='Requirement Priority is Low'  align='center'style="background-color:lightgray">
																				<font color='black'><b>L</b></font>
																			</td>
																		<%}%>												
																	<%} %>
																</tr>
															</table>
															</div>

													</td>
													<%} %>													
					
					
					
					
					
					
												<%
							        				int requirementCommentsCount = r.getRequirementCommentsCount(databaseType);
												%>
																			



													<td style='min-width:25px; text-align:left'  >
																
															<%String url = ProjectUtil.getURL(request,r.getRequirementId(),"requirement"); %>
															
															<div class="btn-group">
															
															  <button type="button" class="btn btn-info btn-sm dropdown-toggle" data-toggle="dropdown">
															   <span class="glyphicon glyphicon-cog " style=" color: white"></span> 
															  </button>
															  <ul class="dropdown-menu" role="menu">
															  	<%
															  	if (!(project.getProjectTags().toLowerCase().contains("school_project"))){
															  	%>
															    <li style='display:block'><a href="#" onClick="handleRequirementActionInDropDown(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,'Open');">Open</a></li>
															   	<li style='display:block'><a href="#" onClick='window.open ("<%=url%>");'>Open in a New Tab</a></li>
															    <%} %>
															    <li style='display:block'><a href="#"  data-toggle="modal" data-target="#myModal" onClick="handleRequirementActionInDropDown(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,'Preview');">Preview</a></li>
															    <li style='display:block'><a href="#" onClick="handleRequirementActionInDropDown(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,'Comment');">Comment</a></li>
															    <li class="divider"></li>
															      <li style='display:block'><a href="#"
															          onClick="displayDynamicApprovers(<%=r.getRequirementId()%>,'displayRDInFolderDiv<%=r.getRequirementId()%>');"
															    	  >Manage Dynamic Approvers</a></li>
									  
									  
															    <li class="divider"></li>
															      <li style='display:block'><a href="#"
															       onClick='
															       generateReqTemplateReport(<%=r.getRequirementId()%>, "displayReqWordTemplatesDiv<%=r.getRequirementId() %>")'>Generate Word Template Report</a></li>
									  
									  
									  
									  						    <li class="divider"></li>
										
															    <%if (canCreate){ %>
															    	<%
																  	if (!(project.getProjectTags().toLowerCase().contains("school_project"))){
																  	%>
															    	<li style='display:block'><a href="#" 
															    		onClick="displayCreateChildForm(<%=r.getFolderId()%>,'<%=r.getRequirementFullTag()%>');">Create a Child</a></li>
														    		<li style='display:block'><a href="#" 
														    		onClick="displayCreateChildForm(<%=r.getFolderId()%>,'<%=r.getParentFullTag()%>');">Create a Sibling</a></li>
														    		<%} %>
															    <%} %>
															    
															    
															    <li class="divider"></li>
															    
															    <li style='display:block'><a href="#" onClick="handleRequirementActionInDropDown(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,'CIA');">Change Impact</a></li>
																	<%
																	if (!(updateDisabled || percentageCompleteDisabled )) {
																		// this means that the user has update permissions on this req
																		// and this req type is eligible for setting percentage complete
																		// i.e. this is not driven by the system.
																		if (r.getRequirementPctComplete() == 100){
																			%>
																			<li style='display:block'><a href="#" onClick="handleRequirementActionInDropDown(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,'MarkInComplete');">Mark Incomplete</a></li>
																			<%	
																		}
																		else {
																			%>
																			<li style='display:block'><a href="#" onClick="handleRequirementActionInDropDown(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,'MarkComplete');">Mark Completed</a></li>
																			<%
																		}
																		
																	}
																	String traceTo = r.getRequirementTraceTo();
																	String traceFrom = r.getRequirementTraceFrom();
																	if (!(traceTo.equals(""))){
																		if(traceTo.contains("(s)")){ %>
																		<li style='display:block'><a href="#" onClick="handleRequirementActionInDropDown(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,'clearAllTracesTo');">Clear All Upstream Traces </a></li>
																	<% 
																		}
																	%>
																		<li style='display:block'><a href="#" onClick="handleRequirementActionInDropDown(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,'deleteAllTracesTo');">Delete All Upstream Traces </a></li>
																	<%
																	}
																	if (!(traceFrom.equals(""))){
																		if(traceFrom.contains("(s)")){ %>
																		<li style='display:block'><a href="#" onClick="handleRequirementActionInDropDown(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,'clearAllTracesFrom');">Clear All Downstream Traces </a></li>
																	<% 
																		}
																	%>
																		<li style='display:block'><a href="#" onClick="handleRequirementActionInDropDown(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,'deleteAllTracesFrom');">Delete All Downstream Traces </a></li>
																	<%
																	}
																	%>	
																	<option value=''></option>
																		
																	
																	<%if (!(deleteDisabled)){
																		// the Delete and Purge options show up only if the user is delete enabled.	
																	%>
																		<%
																	  	if (!(project.getProjectTags().toLowerCase().contains("school_project"))){
																	  	%>
																		<li style='display:block'><a href="#" onClick="handleRequirementActionInDropDown(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,'Delete');">Delete</a></li>
																		
																		<%
																			if (securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
																		%>
																			<li style='display:block'><a href="#" onClick="handleRequirementActionInDropDown(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,'Purge');">Purge</a></li>
																		<%} %>
																		<%} %>	
																	<%} %>
																	
																	<%if (!(updateDisabled)){
																		// the Delete and Purge options show up only if the user is delete enabled.	
																	%>
																		<li style='display:block'><a href="#" onClick="handleRequirementActionInDropDown(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,'Move');">Move</a></li>
																		
																	<%} %>
															    
															  </ul>
															</div>
															
															
														</td>
													

													
													<td 
													 	style='min-width:600px;  text-align:left;' 
														
								 					>
													
															



															
															
															
															
										 							
																	

											<div id='<%=displayRequirementInFolderDiv%>' >
														
													<div id='traceActionBar<%=r.getRequirementId() %>' style='display:none'></div>
									 						<%
									 						// lets put spacers here for child requirements.
									 						  String req = r.getRequirementFullTag();
									 					   	  int start = req.indexOf(".");
												    		  while (start != -1) {
												    	            start = req.indexOf(".", start+1);
																	out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");				    	         
												  	          }
									 						%>
						 			
															
															<span id="requirementNameDisplaySpan<%=r.getRequirementId() %>">
															 <ul  class="nav navbar-nav"  >
																<li class="dropdown">
																  <a href="#" class="dropdown-toggle" data-toggle="dropdown" 
																  style='color:black;'
																		
																	><b><%=r.getRequirementFullTag()%> </b>:  <%=r.getRequirementNameForHTML() %> </a>
																  <ul class="dropdown-menu">
																	
																	
																	<li style='display:block'>
																		
																		<a href='#'
																		
																		onclick='
																			document.getElementById("requirementNameTextBoxDiv<%=r.getRequirementId()%>").style.display="block";
																			document.getElementById("requirementNameTextBoxDiv<%=r.getRequirementId()%>").style.visibility="visible";
																			document.getElementById("requirementNameDisplaySpan<%=r.getRequirementId()%>").style.visibility="hidden";
																		'
																			>Edit Name
																		</a>
																		<a href='#'
																			onclick="
																				if (document.getElementById('traceActionBar<%=r.getRequirementId()%>').style.visibility == 'visible'){
																					document.getElementById('traceActionBar<%=r.getRequirementId()%>').style.visibility = 'hidden';
																				}
																				refreshTraceActionBar(<%=r.getRequirementId()%>)"
																			>Show & Create Traces
																		</a>
																		<a href='#'
																			onClick="
																				document.getElementById('contentCenterA').style.display='none';
																				document.getElementById('contentCenterF').style.display='none';
																				displayFolderTab(<%=r.getRequirementId() %>)
																				displayRequirement(<%=r.getRequirementId()%>,'List Folder Contents');
																			"
																			>Open Here
																		</a>
																		<a href="#" 
																			onClick='window.open ("<%=url%>");'
																			>Open in New Tab
																		</a>
																	</li>
																	
																	
																	
																	</ul>
																</li>
															  </ul>	
															  </span>
															  
															  
															  
															  
															
									 					</div>
														<div id='requirementNameTextBoxDiv<%=r.getRequirementId()%>' style='display:none; '>
																
															<span class='normalText'> 
																<textarea class="form-control" <%=updateRequirementsDisableString %> id="requirementNameBox<%=r.getRequirementId() %>"  rows='4' cols='100'
																><%=r.getRequirementName() %></textarea>
															</span>	
															
																<br>
																<input type='button' class='btn btn-sm btn-primary' 
																	<%=updateRequirementsDisableString %>
																	value='Update Name'
																	onclick='
																		setRequirementNameInList(<%=r.getRequirementId() %>, <%=r.getFolderId() %>);' >
																<input type='button' class='btn btn-sm btn-danger' value='Cancel'
																	onclick='
																		document.getElementById("requirementNameTextBoxDiv<%=r.getRequirementId()%>").style.display="none";
																		document.getElementById("requirementNameTextBoxDiv<%=r.getRequirementId()%>").style.visibility="hidden";
																		document.getElementById("requirementNameDisplaySpan<%=r.getRequirementId()%>").style.visibility="visible";
																	' 
																>
																		
													
											
														</div>	
														<%if (displayDescriptionFlag.equals("block")){ %>
															
															<div class='alert alert-info'
															 style='cursor:pointer; 
															 	font-size:80%; float:left; clear:left;'
																onClick="

																	document.getElementById('contentCenterF').style.display='none';
																	displayFolderTab(<%=r.getRequirementId() %>)
																	displayRequirement(<%=r.getRequirementId()%>,'List Folder Contents');
																"
																onmouseover="this.style.border ='2px solid #85C1E9';"
																onmouseout="this.style.border = 'none'" 
															>
															<%=r.getRequirementDescription() %>
															</div>
														 						 					
															<%} %>
									 					
									 					<%
									 					 String traceToPanel = "success";
									 					if(r.getRequirementTraceTo().contains("(s)")){
									 						traceToPanel = "danger";
									 					}
									 					 String traceFromPanel = "success";
									 					if(r.getRequirementTraceFrom().contains("(s)")){
									 						traceFromPanel = "danger";
									 					}
									 					
									 					
									 					 
									 					 if ((r.getRequirementTraceTo().length() == 0 ) &&  (canBeReportedOrphan)){
									 						traceToPanel = "warning";
									 					 }
									 					 String traceFromHeader  = "";
									 					 if ((r.getRequirementTraceFrom().length() == 0 ) &&  (canBeReportedDangling)){
									 						 traceFromHeader = r.getRequirementFullTag() + " is Dangling";
									 						traceFromPanel = "warning";
									 					 }
									 					 
									 					
									 					%>
									 					
									 				</td>
						 								
						 								<%
						 								if (folderEnabledForApproval == 1) {
						 									%>
						 									<td>
						 										<div id='approvalDiv<%=r.getRequirementId() %>' style='text-align:center'>
						 											<table>
						 											<tr>
									 									<%
																		if (r.getApprovalStatus().equals("Draft")){ 
																		
																			if (!updateDisabled){ 
													        				%>
													        					<td style='min-width:180px;  text-align:center'>
									 											<input type="button" 
									 											class="btn btn-xs btn-outline-primary"  
																				style="border-color:blue; color:blue;"
																				   name='Submit For Acceptance' value=' Submit For Acceptance '
																					onClick='
																						handleRequirementActionOther(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,"submitForApproval");
																					'>
																				</td> 	
																			<%
																			}
																			else {
																				%>
																				<td style='min-width:180px; background-color:#FFFF66' align='left'>
									 											<span class='normalText' >
																				&nbsp;&nbsp;Draft&nbsp;&nbsp;
																				</span>
																				</td>
																				<%		
																			}
																		}
																		
															
																	
																		if (r.getApprovalStatus().equals("In Approval WorkFlow")){ 
																			r.setDaysSinceSubmittedForApproval(databaseType);
																			int daysPending  = r.getDaysSinceSubmittedForApproval();
																			
																			if (r.getApprovers().contains("(P)" + userEmailId)){
																				// if the user hasn't acted on this requirement, show the 'Pending by You' button
																				%>
																					<td style='min-width:180px;   text-align:center'>
										 												<span class='normalText' >												
																								<input type="button" 
																								class="btn btn-xs btn-outline-primary"  
																								style="border-color:blue; color:blue;"
																								 value='Accept / Reject ' 
																								onClick='showApproveRejectInListViewDiv("approveRejectDiv<%=r.getRequirementId()%>",<%=r.getRequirementId()%>)'
																								>
																								<br>(<%=daysPending %> days)
																							 
																						</span>
																					</td>
																				<%
																			}
																			else if (r.getApprovers().contains("(R)" + user.getEmailId())){
																				// if the user has rejected the req, show the 'Approve' button.
																				// this might help if the user has additional info and can now approve it.
																				%>
																					
																					<td style='min-width:180px; background-color:#99ccff; text-align:center'>
									 												<span class='normalText' >												
																						<span class='normalText'>
																							<input type="button" 
																							class="btn btn-xs btn-outline-primary"  
																							style="border-color:blue; color:blue;"
																				 			 value='Rejected by you' 
																							onClick='showApproveRejectInListViewDiv("approveRejectDiv<%=r.getRequirementId()%>",<%=r.getRequirementId()%>)'
																							>
																						</span>
																						 
																					</span>
																					</td>
																				<%
																			}
																			else if (r.getApprovers().contains("(R)" + userEmailId)){
																				%>
																				<td style='min-width:180px;   text-align:center border-color:red; color:red;'
																				>
									 											<span class='normalText' >												
																					&nbsp;&nbsp;Rejected By Me&nbsp;&nbsp; 
																				</span>
																				</td>
																				<%
																			}
																			
																			else {
																			%>
																				<td style='min-width:180px;   text-align:center border-color:yellow; color:yellow;'>
									 											<span class='normalText'>												
																					&nbsp;&nbsp;Pending By Others <br>(<%=daysPending %> days old) 
																				</span>
																				<%	if (isUserAnAdmin){%>
																					<br>
																			 		<input type='button' 
																			 		class="btn btn-xs btn-outline-danger"  
																					style="border-color:red; color:red;"
																				 value='Bypass'
																			 		onclick="document.getElementById('fixRejectDiv<%=r.getRequirementId()%>').style.display = 'block';">
																				<%} %>
																				</td>
																				<%
																			}
																		
																		} 
																		if (r.getApprovalStatus().equals("Approved")){ 
																		
																		
																		%>
																			<td style='min-width:180px;   text-align:center'>
									 										<span style="border-color:green; color:green;"
									 										 class='normalText' >
																				&nbsp;&nbsp;Accepted By All&nbsp;&nbsp;
																			</span>
																			</td>
																		<%} 
																		if(r.getApprovalStatus().equals("Rejected")){
																			
																		 	
																			%>
																			<td style='min-width:180px;   text-align:center' >
									 										<span style="border-color:red; color:red;" 
									 											class='normalText'>
																				Rejected By All&nbsp;
																				
																			</span>
																			
																			<%
																				
																				if (r.getApprovers().contains("(R)" + user.getEmailId())){
																				// if the user has rejected the req, show the 'Cancel my rejection button' button.
																				// this might help if the user has additional info and can now approve it.
																				%>
																					&nbsp;&nbsp;<span class='normalText'>
																					<input type='button' 
																					class="btn btn-xs btn-outline-primary"  
																							style="border-color:blue; color:blue;"
																					 value='Cancel My Rejection' onclick='
																						 handleRequirementActionOther(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,"cancelMyRejectionInFolderList");
																					'></span>
																				<%
																				}
																			%>
																				<%
																				// if the req is rejected, and the user is an owner or admin
																			 	// lets him a chance to either resubit to the rejected user or 
																			 	// approve this rejecor by bypass, or approve the whole req by bypass
																			 	if (isUserAnAdmin){
																			 		%>
																			 		<input type='button' 
																			 		class="btn btn-xs btn-outline-danger"  
																					style="border-color:red; color:red;"
																				 value='Fix It'
																			 		onclick="document.getElementById('fixRejectDiv<%=r.getRequirementId()%>').style.display = 'block';">
																			 		  
																					  <%
																			 	}
																				
																				%>
																			</td>
																		<%}%>
																	</tr>
																	</table>
																</div>
															</td>
				
																	<%														
																	}%>
																
																<%
				
													if (displayedAttributes.size() > 0 ){
														Iterator ddA  = displayedAttributes.iterator();
														while (ddA.hasNext()){
															String a = (String) ddA.next();
															String[] aStuff = a.split(":##:");
															String aId = aStuff[0];
														String aName = aStuff[1];
															
															String uda = r.getUserDefinedAttributes();
															String[]   attribs = uda.split(":##:");
															
				
															for (int k=0; k<attribs.length; k++) {
																String[] attrib = attribs[k].split(":#:");
																String thisAttribLabel = attrib[0];
																// To avoid a array out of bounds exception where the attrib value wasn't filled in
																// we print the cell only if array has 2 items in it.
																String attribValue = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
																if (attrib.length ==2){
																	attribValue = attrib[1];
																}
																if (aName.trim().equals(thisAttribLabel.trim() )) {
																	String title = r.getRequirementFullTag() + "'s " + attrib[0];
																	
										 %>		
						
										 							<td style='min-width:200px; text-align:left; v-align:top'  >
											 							<div id='attributeDiv-<%=j %>-<%=r.getRequirementId()%>-<%=thisAttribLabel%>'  > 
																			<div>
												 								 <ul  class="nav navbar-nav" id='attribOptions-<%=j %>-<%=r.getRequirementId()%>-<%=thisAttribLabel%>' >
																			        <li class="dropdown">
																			          <a href="#" class="dropdown-toggle" data-toggle="dropdown"
																						onmouseover="this.style.border ='2px solid #85C1E9';this.style.width = '-moz-available'"
																						onmouseout="this.style.border = 'none'"
																						style='color:black;'
																						title='<%=r.getRequirementFullTag()%> :: -<%=thisAttribLabel%> :: <%=r.getRequirementName()%> ' 
																						> <%=attribValue %></a>
																			          <ul class="dropdown-menu">
																			            
																			            
																			            <li style='display:block'>
																			            	<a href="#"
																			            		onclick='displayAllRequirementsInRealFolder(<%=folder.getFolderId() %>,"","showMatching","<%=thisAttribLabel%>:#:<%=attribValue%>");'>Show Matching
																			            	</a>
																			            </li>
																			            
																			            
																			            <li style='display:block'>
																			            	<a href="#"
																			            		onclick='displayAllRequirementsInRealFolder(<%=folder.getFolderId() %>,"","filterOut","<%=thisAttribLabel%>:#:<%=attribValue%>");'>Filter Out
																			            	</a>
																			            </li>
																			            
																			           
																			             <li class="divider"></li>
																			             <li style='display:block'><a href="#"
																			              onclick='getAttributeEditForm(<%=j%>, <%=r.getRequirementId() %>,<%=r.getRequirementTypeId() %> ,"<%=thisAttribLabel%>");'>
																			             	Edit Value
																			             	</a>
																			             </li>
																						 
																						 <li class='divider'></li>
																						 <li style='display:block'>
																							 <a href='#'
																								onClick="

																									document.getElementById('contentCenterF').style.display='none';
																									displayFolderTab(<%=r.getRequirementId() %>)
																									displayRequirement(<%=r.getRequirementId()%>,'List Folder Contents');
																								"
																								>Open <%=r.getRequirementFullTag()%>  Here
																							</a>
																						</li>
																						<li style='display:block'>
																							<a href="#" 
																								onClick='window.open ("<%=url%>");'
																								>Open <%=r.getRequirementFullTag()%>  in New Tab
																							</a>
																						</li>
																			            </ul>
																			        </li>
																			      </ul>	
												 								
												 								
												 								
												 							</div>
											 							</div>
											 							
											 						</td>
										<%
																}
															}
										%>
										<%						
															
														}
													}
													%>
				
				
															
														<% if (r.getRequirementTypeName().toLowerCase().equals("release")) {%>
																<td  align='left' >
																<input type="button" class="btn btn-primary btn-sm"  
																	id='showReleaseMetricsButton<%=r.getRequirementId()%>' 
																	value=' Metrics for Release <%=r.getRequirementFullTag() %> ' 
																	onClick='
																		document.getElementById("showReleaseMetricsButton<%=r.getRequirementId()%>").style.visibility="hidden";
																		displayReleaseMetricsWithChart(<%=r.getRequirementId()%>);
																	'
																>
																</td>
														<%} %>
															
															
															
										
														
						 						</tr>
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							
						 							<tr>
						 								<td  colspan='4' style=' border-top: none '>
									 						<div id = 'displayReleaesMetricsDiv<%=r.getRequirementId()%>' class='alert alert-danger' style='display:none'> </div>
									 						<div id = 'displayReqWordTemplatesDiv<%=r.getRequirementId()%>' class='alert alert-info' style='display:none'> </div>
															
															<%if (requirementCommentsCount > 0 ) {%>
																<div id = '<%=displayRDInFolderDiv%>'  style='display:block'> 
																	<div id='displayRequirementComments<%=r.getRequirementId()%>' class=' commentBox'  style='display:none'> 
					
																		<a href='#' onClick='document.getElementById("addCommentBox<%=r.getRequirementId()%>").style.display="block";'>Add <img src="/GloreeJava2/images/comments16.png" border="0"></a>
																		<div id='addCommentBox<%=r.getRequirementId()%>' style='display:none'>
																		<table>
																			<tr>
																				<td align='left'>
																					<span class='normalText'>
																						<textarea name="comment_note<%=r.getRequirementId() %>" id="comment_note<%=r.getRequirementId() %>" rows="4" cols="100" ></textarea>
																					</span>
																				</td>	
																				<td align='left'>
																				
																					<span class='normalText'>
																						<input type='Button' class='btn btn-primary btn-sm'  name='addComment<%=r.getRequirementId() %>' id='addComment<%=r.getRequirementId() %>' value='  Add Comment ' 
																						onclick='addRequirementComment(<%=r.getRequirementId() %>,<%=r.getFolderId() %>,"requirementList" )'>
																					</span>
																															
																					
																					
																				</td>
															
																			</tr>
																		</table>
																		</div>
																		<%
																		ArrayList comments = RequirementUtil.getRequirementComments(r.getRequirementId(), databaseType);
																		%>
																			<div id='commentListDiv<%=r.getRequirementId() %>'>
																				
																					
																					<%
																				
																					    if (comments != null){
																					    	Iterator c = comments.iterator();
																					    	
																					    	while ( c.hasNext() ) {
																					    		
																					    	Comment commentObject = (Comment) c.next();
																							%>
																						 			<div class="bs-callout bs-callout-danger" style='min-width:100%'
																						 				<%
																							 			if (
																							 				(isUserAnAdmin)
																							 				||
																							 				(user.getEmailId().equals(commentObject.getCommenterEmailId()))
																							 			){
																							 			%>
																						 					onmouseover="document.getElementById('commentButton<%=commentObject.getId()%>').style.visibility='visible';" 
																							 				onmouseout="document.getElementById('commentButton<%=commentObject.getId()%>').style.visibility='hidden';"
																						 				<%} %>
																						 			>
																							 			<img src="/GloreeJava2/images/comment16.png" border="0">
																							 			<%=commentObject.getCommenterEmailId()%>
																							 			@ <%=commentObject.getCommentDate() %>
																							 			
																							 			
																							 			<br>
																							 			<%=commentObject.getHTMLFriendlyCommentNote() %>
																							 			<%
																							 			if (

																							 					(isUserAnAdmin == true)
																							 					||
																							 					(user.getEmailId().equals(commentObject.getCommenterEmailId()))
																							 				
																							 				){
																							 				
																							 				
																							 			%>
																							 			
																							 				<input type='button'
																							 					style='visibility:hidden' 
																							 					id='commentButton<%=commentObject.getId()%>'
																							 					class='btn btn-sm btn-danger' value='Delete This Comment' 
																							 					onClick='deleteComment(<%=commentObject.getId() %>, <%=commentObject.getRequirementId() %>)'>	
																							 			
																							 			<%} %> 																						 			
																						 			</div>
																							 <%
																					    	}
																					    }
																					%>
																				</div>
																		</div>
																</div>
															<% }
															else {%>
																<div id = '<%=displayRDInFolderDiv%>' style='display:none'> </div>
															<%} %>
															<div id = '<%=displayRDAlertInFolderDiv%>' style='display:none'> </div>
															
															<div id = 'requirementDetailsDiv<%=r.getRequirementId()%>' style='display:none'> </div>
															
									        				<div id='approveRejectDiv<%=r.getRequirementId()%>' class='alert alert-success' style='display:none'>
									        							
															</div>
														


									        				<div id='fixRejectDiv<%=r.getRequirementId()%>' class='alert alert-success' style='display:none'>
									        					<div class='alert alert-danger'>You are an Administrator of this project. You can Bypass approvals with the following options</div>
									        					<div style='float:right'>
									        						<a href='#' 
									        						onclick="document.getElementById('fixRejectDiv<%=r.getRequirementId()%>').style.display = 'none';">
									        						close
									        						</a>
									        					</div>
									        					<br>
									        					
							
									        					<table class='paddedTable'>
									        					<tr>
									        						<td colspan='2'>
											        					<div style='float: left;'>
											        						<span class='headingText'>  
											        							<b>Approver Status : </b> <%=r.getColorCodedApprovers() %>
											        						</span> 
											        					</div>		        						
									        						</td>
									        					</tr>
									        					
									        					
									        					
									        						<tr>
									        							<td>
									        								<span class='headingText'>
									        									Notes
									        								</span>
									        							</td>
									        							<td>
									        								<span class='normalText'>
																				<textarea name="fixRejectNote<%=r.getRequirementId()%>" id="fixRejectNote<%=r.getRequirementId()%>" rows="4" cols="100" ></textarea>
																			</span>
									        							</td>
									        						</tr>
									        						<tr>
									        							<td colspan='2' align='center'>
									        							<%if (r.getApprovers().contains("(R)")){
									        								// at least 1 Rejector exists. So, lets show the Bypass rejector button
									        								%>
									        							
									        								<span class='normalText'>
																				<input type="button" class="btn btn-primary btn-sm" 
																				 style='height:25px; min-width:180px'  
																				 name='requestApproval' value='Request Approval from Rejector'
																				onClick='
																				handleRequirementActionOther(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,"requestApprovalFromRejector");
																				'>
									        								</span>	
									        								&nbsp;&nbsp;
									        								
									        								<span class='normalText'>
																				<input type="button" class="btn btn-warning btn-sm" 
																				 style='height:25px; min-width:180px'  
																				 name='bypassRejector' value='Bypass Rejector to Next Approver'
																				onClick='
																				handleRequirementActionOther(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,"bypassRejector");
																				'
									        									>
									        								</span> 
									        								
									        								&nbsp;&nbsp;
									        								<%} %>
									        								
									        								
									        								
									        								
									        								
									        									
									        									&nbsp;
									        									<div class="btn-group">
									        									<button type="button" class="btn btn-danger btn-sm dropdown-toggle" data-toggle="dropdown">
																		    	Bypass an Approver <span class="caret"></span>
																		  		</button>
																		  		 <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu">
																			  	 <%
																			  	 String[] approvers = r.getApprovers().split(",");
																			  	 for (String approver:approvers){
																			  		 if (approver.contains("(P)")){
																			  		 String pendingApprover = approver;
																			  		 pendingApprover = pendingApprover.replace("(P)", "");
																			  		 %>
																			  		  <li  style='display:block; ttext-align:left' >
																	                  	<a  href="#"
																	                  		onClick='handleRequirementActionOther(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,"bypassAnApprover:#:<%=pendingApprover %>");'
																						><%=pendingApprover %></a>
																					  </li>
																			  		 <%
																			  		 }
																			  	 }
																			  	 
																			  	 %>
																			  	 </ul>
																				 </div>
																				
																				
																				
																				&nbsp;
									        									<div class="btn-group">
									        									<button type="button" class="btn btn-danger btn-sm dropdown-toggle" data-toggle="dropdown">
																		    	Bypass a Role <span class="caret"></span>
																		  		</button>
																		  		 <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu">
																			  	 <%
																			  	 
																			  	
																			  	 
																			  	 for (Role role:approvalRoles){
																			  		 %>
																			  		  <li  style='display:block; ttext-align:left' >
																	                  	<a  href="#"
																	                  		onClick='handleRequirementActionOther(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,"bypassAnApproverRole:#:<%=role.getRoleId() %>");'
																						><%=role.getRoleName() %></a>
																					  </li>
																			  		 <%
																			  	 }
																			  	 
																			  	 %>
																			  	 </ul>
																				 </div>
																				 
																				&nbsp;
																				
																				<input type="button" class="btn btn-danger btn-sm" 
																				 style='height:25px; min-width:180px'  
																				 name='bypassAll' value='Bypass All to Final Approval'
																				onClick='
																				handleRequirementActionOther(<%=r.getRequirementId()%>,<%=r.getFolderId()%>,"bypassAllApprovers");
																				'
									        									>
									        								
									        								
									        								
									        								
									        								
									        							</td>
									        						</tr>
									        					</table>
									        						
															</div>															
															
														</td>
														
														<td colspan='<%=columnCount-3%>' style=' border-top: none '>
														
													</tr>
													
												
								 					
													
													
													
													
						 						
					
								 <%
								    	}
								    }
									if (requirements.size() == 0) {
									
										boolean createRequirements = true;
										if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" + folder.getFolderId()))){
											createRequirements = false;
										}
										
										// lets get the Create Object Name
										
										String createObjectName = "Create ";
										String reqTypeName = folder.getRequirementTypeName();
										if (reqTypeName.contains("Requirement")){
											reqTypeName = reqTypeName.replace("Requirement", "Req");
										}
										
										if (reqTypeName.length() > 16){
											//reqTypeName = reqTypeName.substring(0, 15)  + "...";
											reqTypeName = folder.getRequirementTypeShortName() + " Objects";
										}
										if (reqTypeName.endsWith("s") || reqTypeName.endsWith("S")){
											//do nothing
										}
										else {
											reqTypeName += "s";
										}
										createObjectName  += reqTypeName;
					
									%>
												<td colspan='4'>
													<table>
														<tr>
															<td valign='top'>
																<table><tr><td>
																<span class='normalText'>
																	No requirements were found in this folder. 										
																</span>
																</td></tr></table>
															</td>
															<td valign='top'>
																<table>
																	<tr>
																		<td valign='top'>
																			
																			<a href='#' 
																				onClick='
																					document.getElementById("folderMenuNoReqsCreateNewDiv").style.display="block";'> 
																				 <%=createObjectName %>  &nbsp;<img height='12' width='12' src="/GloreeJava2/images/dropDown.jpg" border="0"> </a>
					
																		</td>
																	</tr>
																	<tr>
																		<td valign='top'>
																			<div  id='folderMenuNoReqsCreateNewDiv' class="folderMenuClass"  style='display:none; 	z-index: 32; background-color: lightblue; border-min-width: thin; border-color: blue; border-style:solid;'; >
																				
																					
																					<table width='100%' class='paddedTable' >
																					
																				   
																				   
																					<%if (createRequirements) { %>		
																						<tr><td onmouseover="this.style.backgroundColor='lightyellow';" onmouseout="style.backgroundColor='lightblue'">
																							
																							<a href='#'	onClick='
																								createRequirementForm("<%=folder.getFolderId()%>"); 
																								document.getElementById("folderMenuCreateNewDiv").style.display="none" '>
																								 <img src="/GloreeJava2/images/puzzle16.gif" border="0">
																								 Create a <%=folder.getRequirementTypeShortName() %> 
																						</a>
																						</td></tr>
																				  
																				  
																				  
																						<tr><td onmouseover="this.style.backgroundColor='lightyellow';" onmouseout="style.backgroundColor='lightblue'">
																							
																							<a href='#'	onClick='createBulkRequirementForm("<%=folder.getFolderId()%>"); document.getElementById("folderMenuCreateNewDiv").style.display="none" '>
																								 <img src="/GloreeJava2/images/table.png" border="0">
																								 Create multiple <%=folder.getRequirementTypeShortName() %> s
																						</a>
																						</td></tr>
																						
					
																						<tr><td onmouseover="this.style.backgroundColor='lightyellow';" onmouseout="style.backgroundColor='lightblue'">
																							
																							<a href='#'	onClick='importFromExcelForm("<%=folder.getFolderId()%>"); document.getElementById("folderMenuCreateNewDiv").style.display="none"'>
																								 <img src="/GloreeJava2/images/ExportExcel16.gif">
																								 Import <%=folder.getRequirementTypeShortName() %>s  from Excel</a>
																						</td></tr>
					
																						<tr><td onmouseover="this.style.backgroundColor='lightyellow';" onmouseout="style.backgroundColor='lightblue'">
																							
																							<a href='#'	onClick='createWordTemplateForm("<%=folder.getFolderId()%>"); document.getElementById("folderMenuCreateNewDiv").style.display="none"'>
																								 <img src="/GloreeJava2/images/ExportWord16.gif">
																								 Import <%=folder.getRequirementTypeShortName() %>s from Word 
																								 </a>
																							
																						</td></tr>
																						
																														
																					<%}%>								
																				
																					</table>
																				
																			</div>
					
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</td>
											</tr>
									
									
									<%
									}
								%>
										</table>
														</div>
									</td>
								</tr>
							</table>
							<%} %>
						</div>
									
									<table >
															<tr>
																
																<td>
																	Page <%=pageString%>
													 			 </td>
													 			<td>
													 			</tr>		
														</table>
														  
														
														
				
				</div>
				
				
				<div id='metricsDiv' class="tab-pane fade in " style='display:none'> 
					<div id = 'folderMetricsDataTableDiv' class='level2Box' >
					</div>
					
				
				</div>

				<div id='commentsDiv' class="tab-pane fade in " style='display:none' > 
					<div class='alert alert-info'>
						<span class='normalText'><img src="/GloreeJava2/images/comments16.png" border="0"> &nbsp;&nbsp; Show Comments made in the last  
							<input type='text' name='commentedSince' id='commentedSince' value='7' size='3' style="min-width:40px" 
							></input> days
						</span>	
						<input type='button' class='btn btn-sm btn-primary' value='Refresh' onclick='fillRecentlyCommentedReqsForAFolder(<%=folder.getFolderId() %>);'>

					</div> 
					<div id='recentlyCommentedReqsDiv'>
					</div>
				</div>

				<div id='changesDiv' class="tab-pane fade in " style='display:none' > 
					<div class='alert alert-info'>
						<span class='normalText'><img src="/GloreeJava2/images/comments16.png" border="0"> &nbsp;&nbsp; Show objects that changed in the last  
							<input type='text' name='changedSince' id='changedSince' value='7' size='3' style="min-width:40px" 
							></input> days
						</span>	
						<input type='button' class='btn btn-sm btn-primary' value='Refresh' onclick='fillRecentlyChangedReqsForAFolder(<%=folder.getFolderId() %>);'>


						&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;
						<input type='button'  title="View all changes made to project" 
						 value = 'Project Change Log' class='btn btn-sm btn-danger' onclick="displayChangeLogForm();">
													
					</div>
					<div id='recentlyChangedReqsDiv'>
					</div>
				</div>


				<div id='folderInfoDiv2' class="tab-pane fade in " style='display:block'> 
					<div id='folderInfoDetailsDiv'>
					
					</div>
					
				</div>
		
		
		   
		
	<%} %>			