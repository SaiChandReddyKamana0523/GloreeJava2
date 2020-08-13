<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page import="com.gloree.utils.FolderUtil"%>
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="com.gloree.beans.*" %>
<%
SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
String databaseType = this.getServletContext().getInitParameter("databaseType");
User user = securityProfile.getUser();
Project project= (Project) session.getAttribute("project");

// lets see if this user is a member of this project.
// we are leaving this page open to member of this project (which includes admins also)
boolean isMember = false;
if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
	isMember = true;
} 

if (isMember){

	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement r = new Requirement(requirementId, databaseType);
	boolean canBeReportedDangling = FolderUtil.canBeReportedDangling(r.getFolderId());
	boolean canBeReportedOrphan = FolderUtil.canBeReportedOrphan(r.getFolderId());
	
	String displayRDInReportDiv = "displayRDInCIADiv" + r.getRequirementId();
	

%>
<table class='paddedTable' border='1'  >
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

			
			
		

			

			<% if (r.getRequirementTraceTo().length() == 0 ) { 
				if (canBeReportedOrphan){
			%>
				<td title='This requirement is an Orphan, i.e does not trace to Requirements upstream ' width='20px' align='center' style="background-color:lightgray">
					<b><font size='4' color='red'>O</font></b>
				</td>
			<%
				}
				else {
				%>
				<td width='20px'></td>
				<%	
				}
			}
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
			
			
			<% if (r.getRequirementTraceFrom().length() == 0 ) { 
				if (canBeReportedDangling) {%>
				<td title='This requirement is a Dangling Requirement i.e does not have downstream traces' width='20px' align='center' style="background-color:lightgray">
					<b><font  size='4' color='red'>D</font></d> 
				</td>
			<%
				}
				else {
					%>
					<td width='20px'></td>
					<%
				}
			}
			
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
			
			

			<% if (project.getHidePriority() != 1){ %>
				
				<%if (r.getRequirementPriority().equals("High")){%>
				
					<td title='Requirement Priority is High'  align='center' style="background-color:lightgray">
					<font color='red'><b>H</b></font>
					</td>
				<%}
				else if (r.getRequirementPriority().equals("Medium")){%>
					<td title='Requirement Priority is Medium'  align='center' style="background-color:lightgray">
						<font color='blue'><b>M</b></font>
					</td>
				
				<%}
				else {%>
					<td title='Requirement Priority is Low'  align='center'style="background-color:lightgray">
						<font color='black'><b>L</b></font>
					</td>
				<%}%>												
			<%} %>											
			
		</tr>
	</table>
						
<%}%>