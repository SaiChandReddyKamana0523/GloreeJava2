<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String dRTIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dRTIsLoggedIn == null) || (dRTIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is an admin of this project.
	boolean dRTIsAdmin = false;
	String powerUserSettings = project.getPowerUserSettings();
	SecurityProfile dRTSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (
			(dRTSecurityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())
			||
			(
				(dRTSecurityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
				&&
				(powerUserSettings.contains("Manage Requirement Types"))
				)
		)){
		dRTIsAdmin = true;
	}
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dRTIsMember = false;
	if (dRTSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		dRTIsMember = true;
	}
%>

<%if (dRTIsMember){ %>
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<% 
		RequirementType requirementType = (RequirementType) request.getAttribute("requirementType");
		// if the requirementType object exists, it means a call was made to RequirementTypeAction with a request to create a requirementType.
		// the folder object now contains the data for the newly created folder.
	    if (requirementType == null) {
	    	// This means that no new folders were created prior to this call.
	    	int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
	    	requirementType = new RequirementType(requirementTypeId);	
	    }
		
	%>
	
	<div id = 'requirementTypeInfoDiv' class='level1Box'>
	<table   align="center" width='100%'>
		<tr>
			<td colspan='2' align='left' bgcolor='#99CCFF'>
				<span class='subSectionHeadingText' title="Description : 
				<%=requirementType.getRequirementTypeDescription()%>">
				Requirement Type 
				<%=requirementType.getRequirementTypeShortName()%> :  
				<%=requirementType.getRequirementTypeName()%>
				</span>
				<div style='float:right'>
					<a href='/GloreeJava2/documentation/help/administerAProject.htm' target='_blank'>
					<img src="/GloreeJava2/images/page.png"   border="0">
					</a>
					&nbsp;&nbsp;	
				</div>
			</td>
		</tr>	
		<!--  Display requirementTypeAction buttons, only if the user is an admin. -->
		<%if(dRTIsAdmin){ %>

			<tr>
				<td colspan='2' align='left' valign='bottom'>
				<div id ='requirementTypeActions' class='level2Box'>
					<table>
						<tr>
							<td
								style="background: white; cursor: pointer;" 
								onmouseover="this.style.background='lightblue';" 
								onmouseout="this.style.background='white';" 
								onClick='updateRequirementTypeForm("<%=requirementType.getRequirementTypeId()%>")'
							> 
								<span class='normalText'>
									<font color='blue'>
										Edit Object Type
									</font>
								</span>
							</td>
							
							<td>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;</td>
							<td
								style="background: white; cursor: pointer;" 
								onmouseover="this.style.background='lightblue';" 
								onmouseout="this.style.background='white';" 
								onClick='createBaselineForm("<%=requirementType.getRequirementTypeId()%>")'
							> 
								<span class='normalText'>
									<font color='blue'>
										Create New Baseline (Snapshot)
									</font>
								</span>
							</td>
							
							<td>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;</td>
							<td
								id='createNewAttributeCell'
								style="background: white; cursor: pointer;" 
								onmouseover="this.style.background='lightblue';" 
								onmouseout="this.style.background='white';" 
								onClick='createAttributeForm("<%=requirementType.getRequirementTypeId()%>")'
								
							> 
								<span class='normalText'>
									<font color='blue'>
										Create New Attribute
									</font>
								</span>
							</td>
							<td>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;</td>
							<td
								style="background: white; cursor: pointer;" 
								onmouseover="this.style.background='lightblue';" 
								onmouseout="this.style.background='white';" 
								onClick='deleteRequirementTypeForm("<%=requirementType.getRequirementTypeId()%>")'
								
							> 
								<span class='normalText'>
									<font color='blue'>
										Delete this Object Type
									</font>
								</span>
							</td>
							<td>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;</td>
							<td
								style="background: white; cursor: pointer;" 
								onmouseover="this.style.background='lightblue';" 
								onmouseout="this.style.background='white';" 
								onClick='resetRequirementTypeSeqForm("<%=requirementType.getRequirementTypeId()%>")'
								
							> 
								<span class='normalText'>
									<font color='blue'>
										Reset Starting Sequence
									</font>
								</span>
							</td>
							
						</tr>
					</table>
			    </div>
				</td>
			</tr>
			
			<%} 
		else {%>
			
			<tr>
				<td colspan='2' align='center' valign='bottom'>
				<div id ='requirementTypeActions' class='level2Box'>
					<span class='headingText'>					
					<font color='gray'>
						Edit <img src="/GloreeJava2/images/puzzle16.gif" border="0"> Req Type
						&nbsp;&nbsp;|&nbsp;&nbsp;
						Create <img src="/GloreeJava2/images/rubyAttribute16.png" border="0"> Attribute
						&nbsp;&nbsp;|&nbsp;&nbsp;
				    	Delete <img src="/GloreeJava2/images/puzzle16.gif" border="0"> Req Type
				    	
					 </font>
					</span>     
		        </div>
				</td>
			</tr>
			
		
		<%} %>
		<tr>
			<td colspan='2' >
				<div id ='requirementTypeDisplayDiv'></div>
			</td>
		</tr>
		
	</table>
	</div>
<%}%>