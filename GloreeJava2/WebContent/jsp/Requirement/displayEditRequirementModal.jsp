<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	
	String ua = request.getHeader( "User-Agent" );
	boolean isMSIE = false;
	if (
		( ua != null && ua.indexOf( "MSIE" ) != -1 )
		||
		( ua != null && ua.indexOf( "Trident" ) != -1 )
		){
		isMSIE = true;
	}
		
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);
	int projectId = requirement.getProjectId();
	
	// authentication only
	String displayRequirementCoreIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayRequirementCoreIsLoggedIn == null) || (displayRequirementCoreIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + projectId)){
		isMember = true;
	}
	
	if (isMember){
%>

	<% 
	   
	 	
	 	// if the user does not have read permissions on this requirement,
		// lets redact it. i.e. remove all sensitive infor from it.
		if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
				+ requirement.getFolderId()))){
			return;
		}
	
	int rowSize = 0;
	int colSize = 180;
	
	
	
	try {
		int rowLength = requirement.getRequirementName().length() ;
		if (rowLength < colSize){
			rowSize = 1 ;
		}
		else {
			rowSize = (requirement.getRequirementName().length() / colSize);
			rowSize = rowSize + 1; 	
		}
	}
	catch (Exception ex) {
		rowSize = 1;
		ex.printStackTrace();
	}
	// if <br> happens then, we want to add to rowSize.
	int brHappensCount = requirement.getRequirementNameForHTML().toLowerCase().split("<br>").length ;
	
    rowSize = rowSize + brHappensCount;
	
	String formattedName = requirement.getRequirementName();
	if ((formattedName != null ) && (formattedName.toLowerCase().contains("<br>"))){
		formattedName = formattedName.replace("<br>", "\n");
		formattedName = formattedName.replace("<BR>", "\n");
	}
		
	
	boolean canUpdate = false;
	if (securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
			+ requirement.getFolderId())){
	
		canUpdate = true;
	}
	
	int rTEHeight = 0;
	int newRTEHeight = 0;
	String newRTEHeightS = "";
	try {
		newRTEHeightS = request.getParameter("newRTEHeight");
		rTEHeight= Integer.parseInt(newRTEHeightS);
	}
	catch (Exception e){
		e.printStackTrace();														}
	if (rTEHeight == 0){
		rTEHeight = 100;
	}
	newRTEHeight = rTEHeight + 400;
	
	
	if (isMSIE){
	%>
		<span class='normalText'><font color='red'>If you need to use the Rich Text Editor , please use Chrome or Firefox </font></span>
		<br>
	<%}%>
	
	<div class='alert alert-success' >
	<table  class='table table-striped'  border='1' width='100%'>
	
	<tr>
		<td style="vertical-align:middle">
		<b>Name</b><br>
			<span class='normalText'> 
				<textarea class="form-control"
					id="requirementName" name="requirementName"  
					rows='<%=5 %>' cols='<%=100 %>'
				><%=formattedName%></textarea>
			</span>	
									
		</td>
	</tr>
	<tr > 
		<td>
		<b>Description</b><br>
		<div  id='requirementDescriptionDiv' >
			<div id='requirementDescriptionTextBoxDiv' style='display:block'	>
					<div  style='display:block;' id='requirementDescriptionSpan'>
						<textarea class="form-control" id="requirementDescription" 
						name="requirementDescription"  rows='15' cols='450' >
						<%=requirement.getRequirementDescription()  %></textarea>
					</div>							
			</div>											
		</div>
		

		</td>
	</tr>
	
	<tr>
		<td>
		<%if (canUpdate) { %>
			<input type='button' class='btn btn-xs btn-primary' value='Update Name & Description' 
			onclick='setRequirementNameAndDescriptionModal(<%=requirement.getRequirementId()%>)'>
		<%} 
		else {%>
			<div class='alert alert-danger'>You do not have permissions to update this req</div>
		<%} %>
		</td>
	</tr>
	
	</table>
	</div>
	
	<div class='alert alert-info' id='editRequirementModalMessage'></div>
<%}%>