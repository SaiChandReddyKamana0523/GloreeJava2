<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="com.gloree.beans.*" %>

<%
	// authentication only
	String oPToolbarIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((oPToolbarIsLoggedIn == null) || (oPToolbarIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }%>



<%
	SecurityProfile oPTSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User oPTUser = oPTSecurityProfile.getUser();
	Project toolbarProject = (Project)session.getAttribute("project");
	
	
	
	String action = request.getParameter("action");
	if ((action != null) && (action.equals("administerProject"))) {
		// this is the Admin Toolbar  
		%>
		
		<table width='100%' style='background-color:white;'>
			<tr style='background-color:white;'>
				<td colspan='2'><br></td>
			</tr>
			<tr style='background-color:white;'>
				<td align="left" style='background-color:white;'>
					<span class='normalText'>
						<font color='blue'>
						&nbsp;&nbsp;&nbsp;Welcome <%=oPTUser.getFirstName() %> <%=oPTUser.getLastName() %>
						</font>
					</span>
				</td>
				<td align="right">
				
					&nbsp;&nbsp;<a  href="/GloreeJava2/servlet/UserAccountAction?action=signOut" >
					Log out</a>
		
					
					
					&nbsp;&nbsp;<a  href="/GloreeJava2/jsp/UserDashboard/userProjects.jsp" >
					My Projects<img src="/GloreeJava2/images/userDashboard.png" border="0" ></a>
					&nbsp;&nbsp;
					<a class='btn btn-sm btn-danger'  style='color:white' href="/GloreeJava2/servlet/ProjectAction?action=openProject&projectId=<%=toolbarProject.getProjectId()%>" 
					id ='returnToProject'>
					Return to Project</a>
					&nbsp;&nbsp;
				</td>
			</tr>
		</table>
		<%		
	}
	else {
		// this is the Project Toolbar
		String projectName = toolbarProject.getProjectName();
		if (projectName.length() > 60) {
			projectName = projectName.substring(0,60) + "...";
		}

%>
<table  width='100%'  border='0'
	onMouseOver='
		if (document.getElementById("folderMenuNoReqsCreateNewDiv") != null) {document.getElementById("folderMenuNoReqsCreateNewDiv").style.display="none"; }
		if (document.getElementById("requirementActionDiv") != null) {document.getElementById("requirementActionDiv").style.display="none"; }
		'>
	<tr>
		<td><br></td>
	</tr>
	<tr >
		<td align='right'>
			<table width='100%' style='background-color:white;' border='0' >
				<tr>
				
				
				<td style='width:150px;' align="left"  style='background-color:white;'>
						<a class="logo" href="/GloreeJava2/jsp/WebSite/TCHome.jsp">
							<img src="/GloreeJava2/jsp/WebSite/tracecloudlogo.png"  height='40' alt="tracecloud" title="TraceCloud"
							>
						</a>
					
				</td>
				
				<td align="left"  style='background-color:white;'>
					<span class='normalText'>
						<font color='white'>
						&nbsp;&nbsp;&nbsp;
						<a id='projectInfoLink' href='#' onclick='displayProjectInfo()' id='projectName' >
							<%=toolbarProject.getProjectName() %>
						</a>
						
						 						
						</font>
					</span>
				</td>
				<td align="center" style="background-color:white; ">
					<%
					if (!(toolbarProject.getProjectTags().toLowerCase().contains("hide_homepage"))){
					%>
							
						<a href="#" id='iWantTo' class="btn btn-xs btn-outline-success"  
						style="width:60px;border-color:green; color:green;"
						onclick="
							displayWizard();
							if (document.getElementById(&quot;createTracesDiv&quot;) != null){
								document.getElementById(&quot;createTracesDiv&quot;).style.display=&quot;none&quot;;
							}
							if (document.getElementById(&quot;attributeInfo&quot;) != null){
								document.getElementById(&quot;attributeInfo&quot;).style.display=&quot;block&quot;;
							}
							document.getElementById(&quot;requirementActionDiv&quot;).style.display=&quot;none&quot;
							
							  " title="Wizard (Beta)"
						> Home </a>
						
								
					<%}
					%>
					
				</td>
				
				
				
				<td class='icons' align="right" style='background-color:white;' >
					&nbsp;&nbsp;
				
	  		  	<a href="#" 
				class="btn btn-xs btn-outline-primary"  
				style="border-color:blue; color:blue;"
				onclick="
					document.getElementById('projectSearchDiv').style.display='block';
					document.getElementById('reqTabs').style.display = 'none';
					document.getElementById('contentCenterA').style.display='none';
					document.getElementById('contentCenterB').style.display='none';
					document.getElementById('contentCenterD').style.display='none';
					document.getElementById('contentCenterC').style.display='none';
					document.getElementById('contentCenterComments').style.display='none';
					document.getElementById('contentCenterAttachments').style.display='none';
					
					document.getElementById('contentCenterE').style.display='none';
					document.getElementById('contentCenterF').style.display='none';
				">
					<span class="glyphicon glyphicon-search"></span> Search
				</a>
	  
	  		  
					
					

					&nbsp;&nbsp;
					<a  href="/GloreeJava2/jsp/UserDashboard/userProjects.jsp" id='myProjects'>
					My Projects </a>
					&nbsp;&nbsp;
		    		
					
					
					  
					  
				</td>
				</tr>
			</table>
		</td>
	</tr>
</table>


		
		
<%	}
%>