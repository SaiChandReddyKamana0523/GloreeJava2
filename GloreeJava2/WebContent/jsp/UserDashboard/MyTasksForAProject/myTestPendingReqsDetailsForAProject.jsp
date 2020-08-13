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

	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	if (securityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%

	}

try {	
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	User user = securityProfile.getUser();
	
	String dashboardType  = "ProjectDashboard";
	String ownedBy = user.getEmailId();

	ArrayList myTestPendingRequirements = ProjectUtil.getTestPendingRequriements(securityProfile, user.getEmailId(), databaseType, project.getProjectId(), dashboardType, ownedBy);
%>

<table class='paddedTable'  width='100%'>
	<tr>
		<td colspan='7' align='center'>
			<span class='normalText'><b>Items that are pending testing</b></span>
		</td>
	</tr>
	<tr>
		<td colspan='7' align='right'>
			<input type='button' class='btn btn-danger btn-sm' onclick='displayMyTasksForAProject();' value=' Return to My Tasks '></input>
		</td>
	</tr>
<%

	String oldProjectPrefix = "";
	String currentProjectPrefix = "";
	Iterator tPR = myTestPendingRequirements.iterator();
	
	while (tPR.hasNext()){
		Requirement r = (Requirement) tPR.next();
		
		currentProjectPrefix = r.getProjectShortName();
		if (!(currentProjectPrefix.equals(oldProjectPrefix))){
			// project name has changed.  lets print a nice heading
			%>
			<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
			onmouseover=  "this.style.background='#E5EBFF';" onmouseout=  "this.style.background='white';">
				<td colspan='7'>
					<span class='normalText'>
						<b>Project : <%=currentProjectPrefix %></b>
					
					</span>
				</td>
			</tr>
			
			<%
			
			oldProjectPrefix = currentProjectPrefix;
		}
		
		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");

		String displayRDInReportDiv = "displayRDInReportDiv" + r.getRequirementId();
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

			
			
			
			




		<td >
					<%
					// lets put spacers here for child requirements.
					  String req = r.getRequirementFullTag();
				   	  int start = req.indexOf(".");
	    		  while (start != -1) {
	    	            start = req.indexOf(".", start+1);
						out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");				    	         
	  	          }
					%>							 		
	 		
			
				<a href="#" 
	 				onclick='
						document.getElementById("contentCenterE").style.display = "none";
						document.getElementById("contentCenterF").style.display = "none";
					displayFolderInExplorer(<%=r.getFolderId()%>);
					displayFolderContentCenterA(<%=r.getFolderId() %>);
					displayRequirement(<%=r.getRequirementId()%>,"My Tasks", <%=r.getFolderId() %>);
				'		 									
					>
			<%=r.getRequirementFullTag()%> : Ver-<%=r.getVersion()%> :  <%=r.getRequirementNameForHTML() %></a> 		
	 		</td>
			</tr>
			<tr>
				<td  colspan='6'>
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
 
 



  