<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->    
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="java.sql.Date" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>



	<!--  Google Analytics Tracking  -->	
	<script type="text/javascript">
	
	  var _gaq = _gaq || []; 
	  _gaq.push(['_setAccount', 'UA-31449327-1']);
	  _gaq.push(['_trackPageview']);
	
	  (function() {
	    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
	    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	  })();
	
	</script>
	
	
	
<%
	//authorization
	// since we need authorization as well as authenticaiton we will use the 
	// security profile object.
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	if (securityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%
	
	// authorization : since we are explicityly checking for and 
	// listing all the projects the user has access to
	// we are OK here. 
	}

try {	
	int targetProjectId = Integer.parseInt(request.getParameter("targetProjectId"));
	int targetRequirementTypeId = Integer.parseInt(request.getParameter("targetRequirementTypeId"));
	int targetFolderId = 0;
			
			
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	securityProfile = new SecurityProfile(securityProfile.getUser().getUserId(),this.getServletContext().getInitParameter("databaseType"));
	User user = securityProfile.getUser();
	String searchString = request.getParameter("searchString");
	String searchProjects = "";
	ArrayList requirements = new ArrayList(); 
	if ((searchString != null ) && !(searchString.equals(""))){
		 requirements = ReportUtil.getglobalSearchReport(securityProfile, 
					searchProjects,searchString, securityProfile.getUser(), databaseType, targetProjectId,targetRequirementTypeId,
					targetFolderId);
			
	}
	
	
	
%>

<table class='paddedTable'  width='100%'>
	<tr>
		<td colspan='8' align='center'>
			<span class='normalText'><b>Search String "<%=searchString %>"</b></span>
		</td>
	</tr>
	<tr>
		<td colspan='8' align='right'>
			<a class='btn btn-danger btn-sm' href='/GloreeJava2/jsp/UserDashboard/myTasks.jsp'>Return to My Tasks </a>
		</td>
	</tr>
	<tr>
		<td colspan='8' align='center'>
			<div class='alert alert-success'>
			<table>
				<tr>
					<td> <span class='normalText'>Restrict search of </span>
						<span class='normalText'><input type='text' style='width:300px' name='searchString' id='searchString' value='<%=searchString%>'> </span>
					</td>
					
					<td> 
						<span class='normalText'> to Project 
						<select id='targetProject' name='targetProject'
						onchange='findItNowProjectRequirementTypes();' >
							<%if (targetProjectId == 0 ) { %>
								<option value='0' selected >All Projects </option>
							<%}
							else {%>
								<option value='0'>All Projects </option>
							<%} %>
								<%
								ArrayList targetProjects = securityProfile.getProjectObjects();
								Iterator t = targetProjects.iterator();
								while (t.hasNext()){
									Project targetProject = (Project) t.next();
									// lets not show archived projects
									if (targetProject.getArchived() == 1 ){
										continue;
									}
									if (targetProject.getProjectId() == targetProjectId) {
									%>
										<option value='<%=targetProject.getProjectId()%>' selected>
											<%=targetProject.getShortName()%> : <%=targetProject.getProjectName() %>
										</option>
									<%
									}
									else {
									%>
									<option value='<%=targetProject.getProjectId()%>'>
										<%=targetProject.getShortName()%> : <%=targetProject.getProjectName() %>
									</option>
									<%
									}
								}
								%>
							
						
						
						</select> </span>
					</td>
				</tr>
				<tr>
					<td colspan='2'> 
						<%
						if (targetProjectId > 0 ){
							Project project = new Project (targetProjectId, databaseType);
							
						%>
							<div id='reqTypesDiv'>
								<span class='normalText'> 
								and Requirement Type 
								<select id='targetRequirementType' name='targetRequirementType' >
									<%if (targetRequirementTypeId == 0 ){ %>
										<option value='0'>All RequirementTypes </option>
									<%
									}
									else {
									%>
										<option value='0' selected>All RequirementTypes </option>
									<%
									}
									ArrayList requirementTypes = project.getMyRequirementTypes();
									Iterator rt = requirementTypes.iterator();
									while (rt.hasNext()){
										RequirementType requirementType = (RequirementType) rt.next();
										if (requirementType.getRequirementTypeId() == targetRequirementTypeId){
										%>
											<option value='<%=requirementType.getRequirementTypeId() %>' selected >
												<%=requirementType.getRequirementTypeShortName() %> : <%=requirementType.getRequirementTypeName() %>
											</option>
										<%
										}
										else {
										%>
											<option value='<%=requirementType.getRequirementTypeId() %>'>
												<%=requirementType.getRequirementTypeShortName() %> : <%=requirementType.getRequirementTypeName() %>
											</option>
										<% 
										}
									}
								%>
								</select> </span>
							</div>
						  							
						<%
						}
						else {
						%>
							<div id='reqTypesDiv' style='display:none'></div>
						<%} %>							
					</td>
				</tr>
				<tr>
					<td colspan='2' align='center'> <span class='normalText'><input type='button' class='btn btn-primary btn-sm' name='searchNow' id='searchNow' value='Search Now' onclick='findItNow();'> </input></span></td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
<%

	String oldProjectPrefix = "";
	String currentProjectPrefix = "";
	
	int  percentageCompletedDriverReqTypeIdForCurrentProject = 0;
	Iterator dR = requirements.iterator();
	
	while (dR.hasNext()){
		GlobalRequirement gr = (GlobalRequirement) dR.next();
		Requirement r = gr.getRequirement();
		if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
				+ r.getFolderId()))){
			r.redact();
		}
		
		
		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");

		String displayRDInReportDiv = "displayRDInReportDiv" + r.getRequirementId();
		
		String projectTitle = gr.getProjectName();
	  	if (projectTitle != null){
	  		if (projectTitle.contains("'")){
	  			projectTitle = projectTitle.replace("'", " ");
	  		}
	  	}
	  	
		%>
			<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
		onmouseover=  "this.style.background='#E5EBFF';" onmouseout=  "this.style.background='white';">

											


			<td  align='left' valign='top'  width='150px'>
				<table class='paddedTable' border='0' bordercolor='white' >
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

						
						<td width='20px' align='center'>
							<a href="#" onclick= 'displayRequirementDescription(<%=r.getRequirementId()%>
								,"<%=displayRDInReportDiv%>")' title='Preview the Requirement'> 
							<img src="/GloreeJava2/images/search16.png"  border="0">
							</a>
							
						</td>
						

						

						
						
						
						<% if (r.getRequirementTraceTo().length() == 0 ) { %>
							<td title='This requirement is an Orphan, i.e does not trace to Requirements upstream ' width='20px' align='center' style="background-color:lightgray">
								<b><font size='4' color='red'>O</font></b>
							</td>
						<%}
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
						
						
						
						<% if (r.getRequirementTraceFrom().length() == 0 ) { %>
							<td title='This requirement is a Dangling Requirement i.e does not have downstream traces' width='20px' align='center' style="background-color:lightgray">
								<b><font  size='4' color='red'>D</font></d> 
							</td>
						<%}
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
						
					</tr>
				</table>
			</td>

			
			
			
			

			<td>
				<span class='normalText' title='<%=projectTitle%>'>Project : <%=gr.getProjectPrefix() %></span>
			</td>


			<td >
				<%
				if (r.getDeleted() == 1 ){
				%>
					<span class='normalText'><font color='red'>Deleted</font></span>
				<%
				}
				// lets put spacers here for child requirements.
					  String req = r.getRequirementFullTag();
				   	  int start = req.indexOf(".");
		    		  while (start != -1) {
		    	            start = req.indexOf(".", start+1);
							out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");				    	         
		  	          }
		    		  
	    		  
	    		  
				%>							 		
	 		
			

					<a href="<%=url%>" target=_blank>
					<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %></a> 
					
	 		</td>
			</tr>
			<tr>
				<td  colspan='7'>
					<div id = '<%=displayRDInReportDiv%>'> </div>
				</td>
			</tr>				 				

		<%
	}
%>

</table>
<%
} 

catch (Exception e) {
	
}
%>



  