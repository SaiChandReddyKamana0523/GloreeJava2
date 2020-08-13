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
	}

try {	
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	int targetProjectId = Integer.parseInt(request.getParameter("targetProjectId"));
	User user = securityProfile.getUser();
	
	// if a specific projectId was sent in, lets make sure that the user is a valid member of this project.
	String ownedBy = "";
	if (targetProjectId > 0 ){
		if (!(securityProfile.getRoles().contains("MemberInProject" + targetProjectId))){
			// the user is not a member in this project. So lets get out.
			return;
		}
		ownedBy = request.getParameter("ownedBy");
	}
	else {
		// When targetProjectId == 0, that means all projects, lets force ownedby to be the user who is logged in.
		ownedBy = user.getEmailId();		
	}
	String dashboardType= request.getParameter("dashboardType");

	// in case the values didn't come in, lets default them.
	if ((ownedBy == null ) || (ownedBy.equals(""))){
		ownedBy = user.getEmailId();
	}
	if ((dashboardType == null ) || (dashboardType.equals(""))){
		dashboardType = "ProjectDashboard";
	}


	
	
	ArrayList myIncompleteRequirements = ProjectUtil.getIncompleteRequriements(securityProfile, user.getEmailId(), databaseType, targetProjectId , dashboardType, ownedBy);
%>

<table class='paddedTable'  width='100%'>
	<tr>
		<td colspan='7' align='center'>
			<%if (ownedBy.equals(user.getEmailId())) { %>
				<span class='normalText'><b>My Incomplete Items</b></span>
			<%}
			else {
			%>
				<span class='normalText'><b><%=ownedBy %>'s Incomplete Items</b></span>
			<%
			}%>
			
		</td>
	</tr>
	<tr>
		<td colspan='7' align='right'>
			 <a class='btn btn-danger btn-sm' href='/GloreeJava2/jsp/UserDashboard/myTasks.jsp?targetProjectId=<%=targetProjectId%>&dashboardType=<%=dashboardType%>&ownedBy=<%=ownedBy%>'>
				Return to My Tasks
			</a>
		</td>
	</tr>
	<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
		onmouseover=  "this.style.background='#E5EBFF';" onmouseout=  "this.style.background='white';">
		<td><span class='normalText' ><b> Status Bar </b></span></td>
		<td><span class='normalText'><b> Requirement </b></span></td>
		<td><span class='normalText'><b> Owner </b></span></td>
		<td><span class='normalText'><b> % Complete</b></span></td>
	</tr>
	
<%

	String oldProjectPrefix = "";
	String currentProjectPrefix = "";
	
	int  percentageCompletedDriverReqTypeIdForCurrentProject = 0;
	Iterator iR = myIncompleteRequirements.iterator();
	
	while (iR.hasNext()){
		Requirement r = (Requirement) iR.next();
		
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
			
			// since projectName has changed, lets get the int percentageCompletedDriverReqTypeIdForCurrentProject
			percentageCompletedDriverReqTypeIdForCurrentProject = ProjectUtil.getPercentageCompletedDriverReqTypeId(r.getProjectId());
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
	 		
			
	 			
					<a href="<%=url%>" target=_blank>
					<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %></a> 
					</span>
	 		</td>
	 		
	 		<td>
	 			<span class='normalText'>
	 				<%=r.getRequirementOwner() %>
	 			</span>
	 		</td>
			<td style='width:50px'>
	 			<div id='percentCompleteDiv<%=r.getRequirementId()%>'>
	 			<span class='normalText'>
	 			<%=r.getRequirementPctComplete()%>% 
	 			</span>
	 			</div>
	 		</td>
			<td >
				<%
				String traceFromThisRequirementDisableString = "";
				if (!(securityProfile.getPrivileges().contains("traceFromRequirementsInFolder" 
		    			+ r.getFolderId()))){
		    		traceFromThisRequirementDisableString = "disabled='disabled'";
		    	}
				%>
				<div id='percentCompleteActionDiv<%=r.getRequirementId()%>'>
					<% 
					if (
						(percentageCompletedDriverReqTypeIdForCurrentProject > 0 ) 
						&&
						(percentageCompletedDriverReqTypeIdForCurrentProject != r.getRequirementTypeId())
					){
						// If this project has a percentageCompletedDriverReqTypeId value set (>0) 
						// and this requirement does not belong to the percentage complete driver
						// the update should be disabled. 
						// in this case the setpercentcomplete is not an option
						
						
						// do nothing
					}
					else {
						
						%>
						<span class='normalText' >
							<input type='button' class='btn btn-success btn-sm' name='requirementPercentComplete<%=r.getRequirementId() %>' id='requirementPercentComplete<%=r.getRequirementId() %>' 
							value='Mark as Complete' 
							onclick='setRequirementPercentCompleteForMyTasks(<%=r.getRequirementId() %>)'></input>
							</span>
					<%}  %>
				</div>
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


  