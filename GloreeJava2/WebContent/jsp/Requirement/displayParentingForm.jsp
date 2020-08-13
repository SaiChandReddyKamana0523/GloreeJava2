<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<% 

	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	int folderId = Integer.parseInt(request.getParameter("folderId"));
	Requirement requirement = new Requirement(requirementId, databaseType);
	ArrayList childRequirementIds = RequirementUtil.getImmediateChildrenRequirementIds( requirement.getProjectId(), requirement.getRequirementFullTag());
	User user = securityProfile.getUser();

	if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
			+ requirement.getFolderId()))){
		requirement.redact();
	}
	
	String parentFullTag = request.getParameter("parentFullTag");
	if (parentFullTag == null){
		parentFullTag = "";
	}

%>
	<div class='alert alert-success'>
		<div style="float: right;" id="closeCommentsDiv">
			<a onclick='document.getElementById("requirementPromptDiv").style.display="none"' href="#"> Close </a>
	 	</div>
	
	<b>Parent / Child Requirements</b>
	<br><br>
	<a href='#' onClick='displayCreateChildForm(<%=folderId%>,"<%=requirement.getRequirementFullTag()%>")'>Create a New Child </a>
	&nbsp;&nbsp;&nbsp;
	<a href='#' onClick='
		document.getElementById("changeParentDiv").style.display="block";
		document.getElementById("makeChildIndependentDiv").style.display="none";
		document.getElementById("makeAllChildrenIndependentDiv").style.display="none";		
	'>Move to a Different parent</a>

	<%if (requirement.getRequirementFullTag().contains(".")){ 
		// means this is a child req. hence can be made independent or child of another parent.
	%>
		&nbsp;&nbsp;&nbsp;
		<a href='#' onClick='
			document.getElementById("changeParentDiv").style.display="none";
			document.getElementById("makeChildIndependentDiv").style.display="block";
			document.getElementById("makeAllChildrenIndependentDiv").style.display="none";			
		'>Make Requirement Independent</a>
	<%} %>
	
	<%if (childRequirementIds.size() > 0){ 
		// means this is a child req. hence can be made independent or child of another parent.
	%>
		&nbsp;&nbsp;&nbsp;
		<a href='#' onClick='
			document.getElementById("makeChildIndependentDiv").style.display="none";
			document.getElementById("changeParentDiv").style.display="none";
			document.getElementById("makeAllChildrenIndependentDiv").style.display="block";
		'>Make All Children Independent</a>
		
		
	<%} %>
	
	
	<div id='requirementCreationErrorDiv' style='display:none'></div>
	
	
	
				
	
	<div id='makeChildIndependentDiv'  style='display:none'>
		<br><br>
		<div style='float:right'>
			<a href='#' onClick='document.getElementById("makeChildIndependentDiv").style.display="none";'>
			Close</a>
		</div>
		<br>
		<font color='red'>Please note that this will change the Id of this Requirement and it's children.</font>
		
		<%
		if (childRequirementIds.size() > 0 ){
		%>
			<br><br>
			Children's Future	&nbsp;&nbsp;&nbsp;
			<select name='childrensFutureForMakeIndependent' id='childrensFutureForMakeIndependent'>
				<option value='takeChildrenAlong'> Take Children along</option>
				<option value='assignToGrandParent'> Assign Children to Grand Parents</option>
			</select>
		<%}
		else {%>
			<input type='hidden' name='childrensFutureForMakeIndependentHidden' id='childrensFutureForMakeIndependentHidden' 
			value='takeChildrenAlong'></input>
		<%} %>
		<br><br>
		<input type='button' name='makeRequirementIndependent' id='makeRequirementIndependentButton' value='Make Requirement Independent'
			onclick='
				document.getElementById("makeRequirementIndependentButton").disabled = true;
				makeRequirementIndependent(<%=folderId%>,"<%=requirement.getRequirementId() %>")'>
	</div>
	
	








	
	<div id='makeAllChildrenIndependentDiv'  style='display:none'>
		<br><br>
		<div style='float:right'>
			<a href='#' onClick='document.getElementById("makeAllChildrenIndependentDiv").style.display="none";'>
			Close</a>
		</div>
		<br>
		<font color='red'>Please note that this will change the Id of all the children.</font>
	
		<br><br>
		<input type='button' name='makeAllChildrenIndependentButton' id='makeAllChildrenIndependentButton' 
		value='Make All Children Independent'
			onclick='
				document.getElementById("makeAllChildrenIndependentButton").disabled = true;
				makeAllChildrenIndependent(<%=folderId%>,"<%=requirement.getRequirementId() %>")'>
	</div>
	
	








	


	<div id='changeParentDiv'  style='display:none'>
		<br><br>
		<div style='float:right'>
			<a href='#' onClick='document.getElementById("changeParentDiv").style.display="none";'>
			Close</a>
		</div>
		<br>
		<font color='red'>Please note that this will change the Id of this Requirement and it's children.</font>
		
		<br><br>
		<div>
			<table>
				<tr>
					<td width='100'>
						<span class='normalText'>
							Parent Id	
						</span>
					</td>
					<td>
						<span class='normalText'>
						<input type="text"  name="parentFullTag" id="parentFullTag" size="20" maxlength="20"
						value='<%=parentFullTag%>'
						onBlur='validateParentTag(<%=folderId%>)'> 
						</span>
					</td>
					</tr>
			</table>
		</div>				





		<div id='parentInfoDiv' style='display:none;'></div>		
		<%
		if (childRequirementIds.size() > 0 ){
		%>
		<table>
			<tr>
				<td width='100'>
					<span class='normalText'>
						Children's Future
				</td>
				<td>						
					<select name='childrensFutureForChangeParent' id='childrensFutureForChangeParent'>
						<option value='takeChildrenAlong'> Take Children along</option>
						<option value='assignToGrandParent'> Assign Children to Grand Parents</option>
					</select>
				</td>
				</tr>
		</table>

		<%}
		else {%>
			<input type='hidden' name='childrensFutureForChangeParentHidden' id='childrensFutureForChangeParentHidden' 
			value='takeChildrenAlong'></input>
		<%} %>
		
		
		<br><br>
		<input type='button' class='btn btn-sm btn-primary' name='changeParent' id='changeParentButton' value='Move To a Different Parent'
			onclick='changeParent(<%=folderId%>,<%=requirement.getRequirementId() %>)'>
	</div>









	<br><br>	
	<div id='displayParentChildInfoDiv' >
		<table style='width:100%'>
			<tr>
				<td class='normalTableCell' align='center' style='width:40%'>
				<span class='sectionHeadingText'>Parent </span></td>		
				<td class='normalTableCell' align='center' style='width:20%'> 
				<span class='sectionHeadingText'>Requirement</span></td>		
				<td class='normalTableCell' align='center' style='width:40%'> <span class='sectionHeadingText'>Children</span></td>		
			</tr>
			<%
				Requirement parentRequirement = new Requirement(requirement.getParentFullTag(), requirement.getProjectId(), databaseType);
				// if the user does not have read permissions on this requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
						+ parentRequirement.getFolderId()))){
					parentRequirement.redact();
				}
			
				String displayRDInReportDiv = "displayRequirementInDiv" + parentRequirement.getRequirementId();
			%>			
			<tr>
					<%if ((parentRequirement == null ) || (parentRequirement.getRequirementId() == 0)){
					%>
						<td class='altTableCell' align='center' >
							<span class='normalText'>No Parent</span>
						</td>
					<% } 
					else {%>
						<td class='altTableCell'>
						<div>
						<span class='normalText'>
				 			<a href="#" onclick= 'displayRequirementDescription(<%=parentRequirement.getRequirementId()%>
					 			,"<%=displayRDInReportDiv%>")'> 
								<img src="/GloreeJava2/images/search16.png"  border="0">
								</a> 
						
								<a href="#" onClick='
									displayFolderInExplorer(<%=parentRequirement.getFolderId()%>);
									displayFolderContentCenterA(<%=parentRequirement.getFolderId() %>);
									displayFolderContentRight(<%=parentRequirement.getFolderId() %>);		 								
									displayRequirement(<%=parentRequirement.getRequirementId()%>)'>
									
								<img src="/GloreeJava2/images/puzzle16.gif" border="0">
								<% if (parentRequirement.getDeleted() == 1){ %>
								<font color='red'><b> Deleted </b></font>
								<%} %>
								&nbsp;<%=parentRequirement.getRequirementFullTag()%> : Ver-<%=parentRequirement.getVersion()%> :  <%=parentRequirement.getRequirementNameForHTML() %></a> 
							</span>
						</div>
						<div id = '<%=displayRDInReportDiv%>'> </div>
						</td>
					<%} %>
				<td class='altTableCell' align='center' > <span class='normalText'>&nbsp;</span></td>		
				<td class='altTableCell' align='center' > <span class='normalText'>&nbsp;</span></td>		
			</tr>
		
		
		
		
			
			
			<tr>
				<td class='normalTableCell' align='center' > <span class='normalText'>&nbsp;</span></td>		
				<td class='normalTableCell' align='center' > 
				<span class='normalText'>
				<b><%=requirement.getRequirementFullTag()%></b> 
				</span></td>		
				<td class='normalTableCell' align='center' > <span class='normalText'>&nbsp;</span></td>		
			</tr>
		
		
		
		
		
		
			<%

			ArrayList childRequirements = requirement.getImmediateChildRequirements(databaseType);
			if (childRequirements.size() == 0){
			%>
			<tr>
				<td class='normalTableCell' align='center' > <span class='normalText'>&nbsp;</span></td>		
				<td class='normalTableCell' align='center' > <span class='normalText'>&nbsp;</span></td>		
				<td class='normalTableCell' align='center' > <span class='normalText'>No Children</span></td>		
			</tr>

			<%
			}
			else {
				Iterator c = childRequirements.iterator();
				int j = 0;
				String cellStyle = "normalTableCell";
				while (c.hasNext()){
					Requirement childRequirement = (Requirement) c.next();
					// if the user does not have read permissions on this requirement,
					// lets redact it. i.e. remove all sensitive infor from it.
					if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
							+ childRequirement.getFolderId()))){
						childRequirement.redact();
					}
					j++;
					if ((j%2) == 0){
		    			cellStyle = "normalTableCell";
		    		}
		    		else {
		    			cellStyle = "altTableCell";	
		    		}
					displayRDInReportDiv = "displayRequirementInDiv" + childRequirement.getRequirementId();
					%>
					<tr>
						<td class='<%=cellStyle %>' align='center' > <span class='normalText'>&nbsp;</span></td>		
						<td class='<%=cellStyle %>' align='center' > <span class='normalText'>&nbsp;</span></td>
					<td class='<%=cellStyle%>' >
					<div>
					<span class='normalText'>
			 			<a href="#" onclick= 'displayRequirementDescription(<%=childRequirement.getRequirementId()%>
				 			,"<%=displayRDInReportDiv%>")'> 
							<img src="/GloreeJava2/images/search16.png"  border="0">
							</a> 
					
							<a href="#" onClick='
								displayFolderInExplorer(<%=childRequirement.getFolderId()%>);
								displayFolderContentCenterA(<%=childRequirement.getFolderId() %>);
								displayFolderContentRight(<%=childRequirement.getFolderId() %>);		 								
								displayRequirement(<%=childRequirement.getRequirementId()%>)'>
								
							<img src="/GloreeJava2/images/puzzle16.gif" border="0">
							<% if (childRequirement.getDeleted() == 1){ %>
							<font color='red'><b> Deleted </b></font>
							<%} %>
					
							&nbsp;<%=childRequirement.getRequirementFullTag()%> : Ver-<%=childRequirement.getVersion()%> :  <%=childRequirement.getRequirementNameForHTML() %></a> 
						</span>
					</div>
					<div id = '<%=displayRDInReportDiv%>'> </div>
						
					</tr>
					<%
				}
			}
			%>

			
		</table>
	</div>














		
	
	</div>
	
	
	
	
	