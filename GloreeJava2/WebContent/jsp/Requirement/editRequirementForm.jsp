<!-- Gloreejava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String editRequirementFormIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((editRequirementFormIsLoggedIn == null) || (editRequirementFormIsLoggedIn.equals(""))){
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
		// NOTE : this page can be called when some one tries to edit a requirement.
		
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
		
	
		
		
		String requirementPriority = "";
		if (requirement.getRequirementPriority().equals("High")){
			requirementPriority = "<option value='High' SELECTED>High </option><option value='Medium'>Medium</option><option value='Low'>Low</option>";
		}
		else if (requirement.getRequirementPriority().equals("Medium")){
			requirementPriority = "<option value='High'>High </option><option value='Medium' SELECTED>Medium</option><option value='Low'>Low</option>";
		}
		else {
			requirementPriority = "<option value='High'>High </option><option value='Medium'>Medium</option><option value='Low' SELECTED>Low</option>";
		}
		
		
	%>
	
	<div id='editRequirementDiv' class='level1Box'>
		<form method="post" id="editRequirementForm" action="">
			<input type="hidden" name="requirementId" value= <%=requirementId%>    >
			<input type="hidden" name="folderId" value=<%=folderId%> > 
		<table class='paddedTable' width='100%'>
			<tr>
				<td colspan='4' align='left' bgcolor='#99CCFF'>				
					<table width="100%" class="paddedTable">
							<tbody><tr>
							<td bgcolor="#99CCFF" align="left">	
								<span class="normalText">
								Edit Requirement : <%=requirement.getRequirementFullTag() %> &nbsp;&nbsp;&nbsp;&nbsp;
									Version (V-<%=requirement.getVersion() %>)
								</span>	
							</td>
							
							<td bgcolor="#99CCFF" align="right">	
									<span class="subSectionHeadingText">
										&nbsp;&nbsp;&nbsp;
											<%=requirement.getRequirementPctComplete() %>		
											% Completed	
										&nbsp;&nbsp;&nbsp;
										Testing Status : Pending
									</span>
							</td>
							</tr>
						</tbody></table>
				</td>
			</tr>			
			
			<tr> 
				<td> 
					<span class='headingText'> 
					Name
					</span>
					<sup><span style="color: #ff0000;">*</span></sup> 
				</td>
				<td colspan='3'> 
					<span class='normalText'> 
						<textarea id="requirementName" name="requirementName"  rows='4' cols='100'><%=requirement.getRequirementNameForHTML()%></textarea>
					</span>					
				</td>
				
			</tr>
			<tr> 
				<td> 
					<span class='headingText'> 
					Owner
					</span><sup><span style="color: #ff0000;">*</span></sup> 
				</td>
				<td colspan='3'>
					<%
					String ownerSelect = "<select name='requirementOwner' id='requirementOwner'>";
					ArrayList members = project.getMembers();
					Iterator i = members.iterator();
					while (i.hasNext()){
						User member = (User) i.next();
						if (member.getEmailId().equals(requirement.getRequirementOwner())){
							ownerSelect += "<option value='"+ member.getEmailId() +
								"' SELECTED>" +
								member.getEmailId() +
								" </option>";
						}
						else {
							ownerSelect += "<option value='"+ member.getEmailId() +
							"'>" +
							member.getEmailId() +
							" </option>";							
						}
					}
					ownerSelect += " </select> ";
					%>
					<span class='normalText'>
						<%=ownerSelect%>
					</span>
 
				</td>		
				
			</tr>
			<tr>
				<td> 
					<span class='headingText'> 
					Priority
					</span><sup><span style="color: #ff0000;">*</span></sup> 
				</td>
				<td colspan='3'>
					<span class='normalText'> 
					<select name="requirementPriority"><%=requirementPriority %></select>
					</span>
				</td>
			</tr>
			<tr>
				<td> 
					<span class='headingText'> 
					Complete
					</span><sup><span style="color: #ff0000;">*</span></sup> 
				</td>
				<td colspan='3'>
					<span class='normalText'> 
					<%
					int percentageCompletedDriverReqTypeId = project.getPercentageCompletedDriverReqTypeId();
					String disabledString = "";
					if (
							(percentageCompletedDriverReqTypeId > 0 ) 
							&&
							(project.getPercentageCompletedDriverReqTypeId() != requirement.getRequirementTypeId())
						){
							// If this project has a percentageCompletedDriverReqTypeId value set (>0) 
							// and this requirement does not belong to the percentage complete driver
							// the update should be disabled. 
							disabledString = "DISABLED='DISABLED'";
					} %>
					<input type="text" <%=disabledString %> name="requirementPctComplete" size="3" maxlength="3" 
					value="<%=requirement.getRequirementPctComplete()%>"> %
					</span>
				</td>		
			</tr>
			<tr> 
				<td> 
					<span class='headingText'> 
					External Url
					</span>
				</td>
				<td colspan="3">
					<span class='normalText'> 
					  <input type="text"  name="requirementExternalUrl" size="100" maxlength="1000" value="<%=requirement.getRequirementExternalUrl()%>">
					 </span> 
				</td>		
			</tr>
	
			<tr> 
				<td>
					<span class='headingText'> 
					Description
				</td>
				<td colspan="3">
					<span class='normalText'> 
					<textarea id="requirementDescription"  name="requirementDescription" rows="10" cols="80" ><%=requirement.getRequirementDescription()%></textarea>
					</span>
				</td>
			</tr>	
			<tr>
				<td colspan="4" align="center">
					<span class='normalText'>
					
					<input type="button" name="Update Requirement" id="updateRequirementButton" value="Update Requirement" 
					onClick="editRequirement()">
					<input type="button" name="Cancel" value="Cancel" 
					onClick="cancelEditRequirementForm('<%=requirementId%>')">
					</span>	
				</td>
			</tr> 	
		</table>
		</form>
	</div>
<%}%>