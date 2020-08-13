<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn  == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User user = securityProfile.getUser();
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
		int sharedRequirementTypeId = Integer.parseInt(request.getParameter("sharedRequirementTypeId"));
		// note sRT is a superset of RT i.e it has  a few more attributes than the standard req type.
		SharedRequirementType sRT = new SharedRequirementType(sharedRequirementTypeId);
		RequirementType requirementType = sRT.getRequirementType();
		ArrayList baselines = requirementType.getAllBaselines();
		
		String updatedMessage = "";
		String updated = (String) request.getAttribute("updated");
		if ((updated != null) && (updated.equals("true")))	{
			updatedMessage = "" +
			" 	<div id='userPrompt' class='alert alert-success' align='left'> " +
			"	<span class='normalText'>Your changes have been applied. </span>" + 
			"	</div> " ; 
		}
		
		
%>
	
	
	<form name='publish'>	
	<div id='publishSharedRequirementsFormDiv' class='level1Box'>	
		<table class='paddedTable' width='100%'>
			<tr>
				<td colspan='2' align='left' >				
					<span class='normalText'>
					<b>Requirement Type : <%=requirementType.getRequirementTypeName() %></b>
					</span>
					<%=updatedMessage%>
				</td>
			</tr>		
			<tr> 
				<td width='200' valign='top'>
					<span class='normalText'>
					 Publish Status
					 </span>
				</td>
				<td> 
					<span class='normalText'>
					
					<% if (sRT.getSRPublishStatus().equals("published")){%>
						<input type="radio"  name="sRPublishStatus"  id="sRPublishStatus" value="published" CHECKED> Published
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="radio"  name="sRPublishStatus"  id="sRPublishStatus" value="notPublished"> Not Published
					<%}
					else {%>
						<input type="radio"  name="sRPublishStatus"  id="sRPublishStatus" value="published" > Published
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="radio"  name="sRPublishStatus"  id="sRPublishStatus" value="notPublished" CHECKED> Not Published
					<%} %>
					

					</span>
					&nbsp;&nbsp;
					<a href='#' onClick='document.getElementById("publishStatusMoreInfoDiv").style.display="block";'>More Info</a>
					<div id='publishStatusMoreInfoDiv' style='display:none'>
						<div style='float:right'>
						<a href='#' onClick='document.getElementById("publishStatusMoreInfoDiv").style.display="none";'>Close</a>
						</div>
						<br>
						<div>
						<span class='normalText'>
						To publish these Requirements for others to explore and import and execute on, set this value to
						'Published'. 
						<br>
						Please note that your Requirements are visible to others only when the following  conditions
						are met
						<br> 
						<br>1. You have set the Requirement Type to 'Published'
						<br>2. You have added some requirements to a Baseline and selected that Baseline to be published
						</span>
						</div>
					</div>
				</td>
			</tr>

			<tr> 
				<td width='200' valign='top'>
					<span class='normalText'>
					 Baselines
					 <br>(Ctrl+Select)
					 </span>
				</td>
				<td> 
					<span class='normalText'>
					<select id='sRRTBaselineId' MULTIPLE SIZE='4'>
					<% Iterator b = baselines.iterator();
					while (b.hasNext()){
						RTBaseline rTBaseline = (RTBaseline) b.next();
						if (sRT.getSRRTBaselineIDs().contains("rTBaselineId" + rTBaseline.getBaselineId() + ",") ){
						%>
							<option value='<%=rTBaseline.getBaselineId()%>' SELECTED><%=rTBaseline.getBaselineName()%> (Published)</option>
						<%
						}
						else {
						%>
							<option value='<%=rTBaseline.getBaselineId()%>' ><%=rTBaseline.getBaselineName()%> (UnPublished)</option>
						<%
						}
					}%>
					</select>
					</span>
					<a href='#' onClick='document.getElementById("baselineMoreInfoDiv").style.display="block";'>More Info</a>
						<div id='baselineMoreInfoDiv' style='display:none'>
						<div style='float:right'>
						<a href='#' onClick='document.getElementById("baselineMoreInfoDiv").style.display="none";'>Close</a>
						</div>
						<br>
						<div>
						<span class='normalText'>
						The Requirements you share, can change over time. For example, you have collected a set of Requirements
						and after a lot of discussion and back & forth, these Requirements have stabilized. You can now take a 
						snapshot of these Requirements in to a Baseline and publish this Baseline. Now, you can continue to improve 
						and modify your original set of Requirements, but your users who import your published Requirements will continue
						to get the stable , published Baseline of your Requirements. When  you feel that your Requirements have stabilized
						you can take a new snapshot  and publish the new Baseline.
						<br>
						Please note that your Requirements are visible to others only when the following 3 conditions
						are met
						<br> 
						<br>1. You have set the Requirement Type to 'Published'
						<br>2. You have added some requirements to a Baseline and selected that Baseline to be published
						<br>3. Your Requirements have been 'Approved' by all the approvers.
						</span>
						</div>
					</div>
				</td>
			</tr>

			<tr> 
				<td width='200' valign='top'>
					<span class='normalText'>
					 Export Discussions
					 </span>
				</td>
				<td> 
					<span class='normalText'>
					<select id='sRShareComments'>
					<%if (sRT.getSRShareComments() == 1){%>
						<option value='1' SELECTED>Yes</option>
						<option value='0' >No</option>
					<%
					}
					else {
					%>
						<option value='1' >Yes</option>
						<option value='0' SELECTED>No</option>
					<%}%>
					</select>
					</span>
					<a href='#' onClick='document.getElementById("discussionsMoreInfoDiv").style.display="block";'>More Info</a>
					<div id='discussionsMoreInfoDiv' style='display:none'>
					<div style='float:right'>
					<a href='#' onClick='document.getElementById("discussionsMoreInfoDiv").style.display="none";'>Close</a>
					</div>
					<br>
					<div>
					<span class='normalText'>
					Set this to 'Yes', if you want to copy all the Discussion collateral that is associated 
					with the Shared Requirement to the project that is importing these Requirements.
					</span>
					</div>
					</div>
					
				</td>
			</tr>
			
			<tr> 
				<td width='200' valign='top'>
					<span class='normalText'>
					 Domain Administrators
					 </span>
					 <sup><span style="color: #ff0000;">*</span></sup>
				</td>
				<td> 
					<span class='normalText'>
					<%String domainAdministrators = sRT.getSRDomainAdministrators();
					if ((domainAdministrators == null) || (domainAdministrators.trim().equals(""))){
						domainAdministrators = user.getEmailId(); 
					}%>
					
					<input type="text" id="sRDomainAdministrators" size="30" maxlength="1000" 
							value= '<%=domainAdministrators %>'>
					</span>
				
					<a href='#' onClick='document.getElementById("domainAdministratorsMoreInfoDiv").style.display="block";'>More Info</a>
					<div id='domainAdministratorsMoreInfoDiv' style='display:none'>
					<div style='float:right'>
					<a href='#' onClick='document.getElementById("domainAdministratorsMoreInfoDiv").style.display="none";'>Close</a>
					</div>
					<br>
					<div>
					<span class='normalText'>
					This is a comma separated list of users who are the 'Domain Owners' for this set of published Requirements.
					<br><br>Typically, these are a set of individuals who know the published Requirements very well and are
					interested in keeping track of projects / individuals copying these Requirements
					</span>
					</div>
					</div>
				</td>
			</tr>
			
			<tr> 
				<td width='200' valign='top'>
					<span class='normalText'>
					 Enforce Mandatory Req Import
					 </span>
				</td>
				<td> 
					<span class='normalText'>
					<select id='sRMandatoryNotification'>
					<%if (sRT.getSRMandatoryNotification() == 1){%>
						<option value='1' SELECTED>Yes</option>
						<option value='0' >No</option>
					<%
					}
					else {
					%>
						<option value='1' >Yes</option>
						<option value='0' SELECTED>No</option>
					<%}%>
					</select>
					</span>
				
					<a href='#' onClick='document.getElementById("mandatoryNotificationMoreInfoDiv").style.display="block";'>More Info</a>
					<div id='mandatoryNotificationMoreInfoDiv' style='display:none'>
					<div style='float:right'>
					<a href='#' onClick='document.getElementById("mandatoryNotificationMoreInfoDiv").style.display="none";'>Close</a>
					</div>
					<br>
					<div>
					<span class='normalText'>
					If your Shared Requirement has a custom attribute named 'Mandatory' and it's value is set to 'Yes',
					then the expectation is that this is Mandatory Requirements that won't be skipped during the import process.
					<br><br>
					This flag forces the user to download this Requirement.<br>
					</span>
					</div>
					</div>
				</td>
			</tr>
			

			<tr> 
				<td width='200' valign='top'>
					<span class='normalText'>
					 Publishing Instructions
					 </span>
				</td>
				<td valign='top'> 
					<span class='normalText'>
						<textarea id="sRInstructions"  
							 rows="5" cols="50" ><%=sRT.getSRInstructions() %></textarea>
					</span>
				
				
					<a href='#' onClick='document.getElementById("sRInstructionsMoreInfoDiv").style.display="block";'>More Info</a>
					<div id='sRInstructionsMoreInfoDiv' style='display:none'>
					<div style='float:right'>
					<a href='#' onClick='document.getElementById("sRInstructionsMoreInfoDiv").style.display="none";'>Close</a>
					</div>
					<br>
					<div>
					<span class='normalText'>
					Use this field to give instructions that will be displayed to users when they try to import your
					Requirements
					<br>
					HINT : You can enter HTML in this box and use HTML tags like href to show URLs.
					<br>
					</span>
					</div>
					</div>
				</td>
			</tr>
			
			
			<tr><td colspan='2'>&nbsp;</td></tr>
			<tr>
				<td colspan='2' align='left' >				
					<span class='normalText'>
					<b> Attribute Settings</b>
					</span>
				</td>
			</tr>		
			
			<tr><td colspan='2'>
				<table>
					<tr>
						<td><span class='normalText'>Attribute Name</span></td>
						<td><span class='normalText' >Filterable</span>
							<span class='normalText' title='When a user imports these Requirements, he will be able to narrow the set of Requirements by filtering on these attributes'>
							<font color='blue'> ? </font> </span></td>
						<td><span class='normalText' >Copyable</span>
							<span class='normalText' title='When a user imports these Requirements, this attribute will be copied to the target project'>
							<font color='blue'> ? </font></span></td>
						<td><span class='normalText' >Displayable</span>
							<span class='normalText' title='If set to Yes, this attribute will be displayed during the Import operation'>
							<font color='blue'> ? </font></span></td>
						<td><span class='normalText' >Editable</span>
							<span class='normalText' title='If set to Yes, this attribute can be edited in the Project that imports this Requirement'>
							<font color='blue'> ? </font></span></td>
							
					</tr>
					<%
					ArrayList sRTAttributes = sRT.getAllSharedAttributesInRequirementType();
					// we use attributeIdString to figure out which attribute are present in this req type
					// during JavaScript parsint fo check copyable , filterable check boxes.
					String attributeIdString = "";
					
					if (sRTAttributes.size() == 0){
					%>
					<tr>
						<td colspan='2'>
							<span class='normalText'>
							No Attributes have been defined for this Requirement Type
							</span>
						</td>
					</tr>
					<%	
					}
					Iterator s = sRTAttributes.iterator();
					while (s.hasNext()){
						SharedRequirementTypeAttribute sRTAttribute = (SharedRequirementTypeAttribute) s.next();
						RTAttribute rTAttribute = sRTAttribute.getRTAttribute();
						attributeIdString += rTAttribute.getAttributeId() + "##";
					%>
						<tr>
						<td> <span class='normalText'><%=rTAttribute.getAttributeName()%></span></td>	
						<td> <span class='normalText'>
							<select id='filterable<%=rTAttribute.getAttributeId()%>'>
								<%if (sRTAttribute.getSRAFilterable() == 1){ %>
								<option value='1' SELECTED>Yes</option>
								<option value='0' >No</option>
								<%}
								else {%>
								<option value='1' >Yes</option>
								<option value='0' SELECTED>No</option>
								<%} %>								
							</select>
						</span></td>
						<td> <span class='normalText'>
							<select id='copyable<%=rTAttribute.getAttributeId()%>'>
								<%if (sRTAttribute.getSRACopyable() == 1){ %>
								<option value='1' SELECTED>Yes</option>
								<option value='0' >No</option>
								<%}
								else {%>
								<option value='1' >Yes</option>
								<option value='0' SELECTED>No</option>
								<%} %>								
							</select>
						</span></td>

						<td> <span class='normalText'>
							<select id='displayable<%=rTAttribute.getAttributeId()%>'>
								<%if (sRTAttribute.getSRADisplayable() == 1){ %>
								<option value='1' SELECTED>Yes</option>
								<option value='0' >No</option>
								<%}
								else {%>
								<option value='1' >Yes</option>
								<option value='0' SELECTED>No</option>
								<%} %>								
							</select>
						</span></td>


						<td> <span class='normalText'>
							<select id='editable<%=rTAttribute.getAttributeId()%>'>
								<%if (sRTAttribute.getSRAEDitable() == 1){ %>
								<option value='1' SELECTED>Yes</option>
								<option value='0' >No</option>
								<%}
								else {%>
								<option value='1' >Yes</option>
								<option value='0' SELECTED>No</option>
								<%} %>								
							</select>
						</span></td>

						</tr>
					<%
					}
					
					%>					
				</table>
			</td></tr>
			
			
			<% 
			String updatePermission = "disabled=DISABLED";
			if (securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
				// Only Admin's can update the settings
				updatePermission="";
			}
			%>
			<tr> 
				<td colspan='2' align='left'>
				<span class='normalText'>
				<input type='button'  <%=updatePermission%> 
				name='Update Settings' id='updateSharedSettingsButton' value='Update Settings' 
				onClick='updateSharedRequirementTypeSettings(<%=sRT.getSRTId()%>,"<%=attributeIdString%>" )'>
				</span>
				</td>
			</tr>
			
			
		</table>
	</div>
	</form>
<%}%>