<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String editWordTemplateFormIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((editWordTemplateFormIsLoggedIn == null) || (editWordTemplateFormIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
%>
	
	
	<%
		// NOTE : this page can be called when some one tries to edit a wordTemplate.
		
		int templateId = Integer.parseInt(request.getParameter("templateId"));
		WordTemplate wordTemplate = new WordTemplate(templateId, databaseType);
		int folderId = Integer.parseInt(request.getParameter("folderId"));
		String locateProcess = request.getParameter("locateProcess");
		String styleName = request.getParameter("styleName");
		String paragraphSearch = request.getParameter("paragraphSearch");
		
		ArrayList locatedRequirements = (ArrayList) request.getAttribute("locatedRequirements");
		
		// NOTE : bacause we have extensive validation when users use Requirement Template
		// we have logic in locatedRequirements that we can  use to display extra info.
		// eg : locatedRequirements has 'folderpath:##:createRequirements:true:##:requirementName'
		
		if (locateProcess.equals("tables-singleReq")){
	%>
	
		<div id='createRequirementsFromTableDiv' >
			<form method="post" id="createRequirementsFromWordTemplateConfirm" action="">
			<table class='paddedTable' width='100%'>
				<tr>
					<td align="left" colspan='3'>
					 <span class='normalText'>				
					Create Requirements From Word Document
					</span>
					</td> 
				</tr>	
				<%			
				if (locatedRequirements.size() == 0){
				%>
				<tr>
					<td align="left" colspan='3'>				
					<div class='userAlertPrompt'>
						<span class='normalText'>
							<br>
							We were not able to locate any Requirements from this word document using the 
							locate process '<%=locateProcess%>'
							<br>
						</span>
					</div>
					</td> 
				</tr>				
				<%		
				}
				else {
				%>
				<tr>
					<td align="left" colspan='3'>				
					<div class='alert alert-success'>
						<span class='normalText'>
							<br>
							We have located the following Requirements in your word document, by parsing the 
							Requirement Template tables. If you want the system to create these Requirements, please
							click on 'Create Requirements' button below.
							<br>
							
							Pleas note that if you do not have 'Create Requirements' permission on a Folder
							the Requirements that were targetted for that folder may have been grayed out.
							<br> 
						</span>
					</div>
					</td> 
				</tr>		
				<tr><td colspan='3'>&nbsp;</td></tr>		
				
				<%	
					Iterator i = locatedRequirements.iterator();
			    	int j = 1;
			    	String cellStyle = "normalTableCell";	
			    	int locationNumber = 0;
					while (i.hasNext()){
						String requirementString = (String) i.next();
						// since the requirementString has folderpath:##:createREquirements:true:##:reqname in it
						// lets split it up
						String [] locatedInfo = requirementString.split(":##:");


						String folderPath = "";
						String createPermissions = "";
						String requirementName = "";
						

						if (locatedInfo.length > 0) {
							 folderPath = locatedInfo[0];
						}
						if (locatedInfo.length > 1) {
							 createPermissions = locatedInfo[1];
						}
						if (locatedInfo.length > 2) {
							 requirementName = locatedInfo[2];
						}
						
						
			    		if ((j%2) == 0){
			    			cellStyle = "normalTableCell";
			    		}
			    		else {
			    			cellStyle = "altTableCell";	
			    		}
			    		j++;
			    		locationNumber++;
						%>
						
						<tr>
							<td>
							<%
							if (createPermissions.contains("true")){
							%>
								<input type='checkbox' CHECKED name='locationNumber' value='<%=locationNumber%>'>
							<%}
							else {%>
								<input type='checkbox' DISABLED name='locationNumber' value='<%=locationNumber%>'>
							<%} %>
							</td>
							<td class='<%=cellStyle%>' align="left" >				
							<span class='normalText'>
								<%=folderPath%>
							</span>
							</td> 
							
							<td class='<%=cellStyle%>' align="left" >				
							<span class='normalText'>
								<%=requirementName %>
							</span>
							</td> 
						</tr>	
						<%
					}
				%>	
					<tr>
						<td colspan="2" align="left">
							<span class='normalText'>
								<input type="button" name="Create Requirements" value="Create Requirements" 
								id="createRequirementsFromWordTemplateButton"
								onClick="createRequirementsFromWordTemplate(<%=folderId%>,<%=templateId%>, '<%=locateProcess%>',
								'<%=styleName%>', '<%=paragraphSearch%>')">
								<input type="button" name="Cancel" value="Cancel" 
								onClick='document.getElementById("templateCoreDiv").innerHTML= ""'>
							</span>
						</td>
					</tr> 	
					
				<%	
				}
				%>
	
			 
			</table>
			
			</form>
		</div>	
	
	
	<%}
	else {%>
		<div id='editWordTemplateDiv' >
			<form method="post" id="createRequirementsFromWordTemplateConfirm" action="">
			<table class='paddedTable' width='100%'>
				<tr>
					<td align="left" colspan='3'>
					 <span class='normalText'>				
					Create Requirements From Word Document
					</span>
					</td> 
				</tr>	
				<%			
				if (locatedRequirements.size() == 0){
				%>
				<tr>
					<td align="left" colspan='3'>				
					<div class='userAlertPrompt'>
						<span class='normalText'>
							<br>
							We were not able to locate any Requirements from this word document using the 
							locate process '<%=locateProcess%>'
							<br>
						</span>
					</div>
					</td> 
				</tr>				
				<%		
				}
				else {
				%>
				<tr>
					<td align="left" colspan='3'>				
					<div class='alert alert-success'>
						<span class='normalText'>
							<br>
							We have located the following Requirements in your word document. 
							If you want the system to create these Requirements, please
							click on 'Create Requirements' button below.
							<br> 
						</span>
					</div>
					</td> 
				</tr>		
				<tr><td colspan='3'>&nbsp;</td></tr>		
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td  align="left" >	
					<span class='normalText'>			
						Requirement Name
					</span>
					</td> 
				</tr>	
				<%	
					Iterator i = locatedRequirements.iterator();
			    	int j = 1;
			    	String cellStyle = "normalTableCell";	
			    	int locationNumber = 0;
					while (i.hasNext()){
						String requirementString = (String) i.next();
						String requirementFullTag = "";
						// lets see if this requirement string has a water mark.
						Requirement requirement = null;
						try {
							if (requirementString != null){
								if (requirementString.trim().startsWith("##")){
									String[] reqParts = requirementString.split("##");
									
									if (reqParts.length  > 1){
										requirementFullTag = reqParts[1];
										requirement = new Requirement(requirementFullTag, project.getProjectId(), databaseType);
									}
									if (reqParts.length  > 2){
										requirementString = reqParts[2];
										
									}
								}
							}
						}
						catch (Exception e){
							e.printStackTrace();
						}
			    		if ((j%2) == 0){
			    			cellStyle = "normalTableCell";
			    		}
			    		else {
			    			cellStyle = "altTableCell";	
			    		}
			    		j++;
			    		locationNumber++;
			    		
			    
						%>
						
						<tr>
							<td style='width:200px'>
								
							<%
							if (requirement != null ){
								%>
				
								<%
								// check if the user has update permissions on this requirement
								if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
										+ requirement.getFolderId()))){
									// no update permissions. so let leave a note.
									%>
									<input type='checkbox' DISABLED name='locationNumber' value='<%=locationNumber%>'> No Update Permissions
									<%
								}
								else if (
										(requirement.getRequirementName().trim().equals(requirementString.trim()))
										&&
										(requirement.getRequirementDescription().trim().equals(requirementString.trim()))
									) {
									// this requirement has not changed
									%>
									<input type='checkbox' DISABLED name='locationNumber' value='<%=locationNumber%>'> Not Changed
									<% 
								}
								else {
									%>
									<input type='checkbox' CHECKED name='locationNumber' value='<%=locationNumber%>'>
									<%
								}
							}
							else {
								// check if the user has create new requirements permissions
								if (!(requirementString.contains("<font color='red'>Error : </font>"))) {%>
								<input type='checkbox' CHECKED name='locationNumber' value='<%=locationNumber%>'>
								<%}
								else{%>
									<input type='checkbox' DISABLED name='locationNumber' value='<%=locationNumber%>'>
								<%} %>
							<%}
							%>
							
							</td>
							<td style='width:200px'>
							<%
								if (requirement != null){
									String url = ProjectUtil.getURL(request,requirement.getRequirementId(),"requirement"); 
									if (
											(requirement.getRequirementName().equals(requirementString))
											&&
											(requirement.getRequirementDescription().equals(requirementString))
										) {
							%>	
										<span class='normalText'>
											Not Updating <a href='#' onclick='window.open ("<%=url%>")'> <%=requirement.getRequirementFullTag() %></a>
										</span>
										
							<%		}
									else {
									%>
										<span class='normalText'>
											Updating <a href='#' onclick='window.open ("<%=url%>")'> <%=requirement.getRequirementFullTag() %></a>
										</span>
										
							<%		}
							}
							else {%>
								<span class='normalText'>
									Creating New Requirement
								</span>
							<%} %>
							</td>
					
							<td class='<%=cellStyle%>' align="left" >				
							<span class='normalText'>
								<%=requirementString %>
							</span>
							</td> 
						</tr>	
						<%
					}
				%>	
					<tr>
						<td colspan="3" align="left">
							<span class='normalText'>
								<input type="button" style='width:180px; height:25px' name="Create Requirements" value="Create  / Update Requirements" 
								id="createRequirementsFromWordTemplateButton"
								onClick="createRequirementsFromWordTemplate(<%=folderId%>,<%=templateId%>, '<%=locateProcess%>',
								'<%=styleName%>', '<%=paragraphSearch%>')">
								<input type="button"  style='width:180px; height:25px' name="Cancel" value="Cancel" 
								onClick='document.getElementById("templateCoreDiv").innerHTML= ""'>
							</span>
						</td>
					</tr> 	
					
				<%	
				}
				%>
	
			 
			</table>
			
			</form>
		</div>
	<%} %>
<%}%>