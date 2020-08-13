<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<%
	// authentication only
	String oPToolbarIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((oPToolbarIsLoggedIn == null) || (oPToolbarIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }%>


<table>
<tr>

<%
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User user = securityProfile.getUser();
	Project project = (Project)session.getAttribute("project");
	String hideFromHealthBar = project.getHideFromHealthBar();

	if (!hideFromHealthBar.contains("pendingYourApproval")){
		int myPendingApprovalRequirements = ProjectUtil.getRequirementsPendingApprovalByApproverCount(user.getEmailId(), project.getProjectId(), "ProjectDashboard", user.getEmailId() );
		if (myPendingApprovalRequirements == 0){
			%>
				<td align="center" 
					 title='Congratlations !!! There are no requirements pending your approval'
					>
					<span class="glyphicon glyphicon-heart " style=" color: green; font-size:1.5em " >
					</span>
				</td>
			<%
		}
		else {
			%>
				<td align="center" 
					 title='Your Approval Required : <%=myPendingApprovalRequirements%> requirements are pending your approval'
					onclick="myPendingApprovalDetailsForAProject('contentCenterF')" style="cursor: pointer; background: white;">
					<span class="glyphicon glyphicon-thumbs-up " style=" color: red; font-size:1.5em ">
					</span>
				</td>
			<%
		}
	}
		
		
	if (!hideFromHealthBar.contains("yourRejected")){
		int myRequirementsRejected = ProjectUtil.getReqsRejectedCount(user.getEmailId(), project.getProjectId(),  "ProjectDashboard", user.getEmailId());
		if (myRequirementsRejected == 0){
			%>
				<td align="center" 
					 title="Congratlations !!! None of your requirements have been rejected"
					>
					<span class="glyphicon glyphicon-heart " style=" color: green; font-size:1.5em " >
					</span>
				</td>
			<%
		}
		else {
			%>
				<td align="center" 
					 title='Your Objects Rejected : <%=myRequirementsRejected%> requirements owned by you have been rejected'
					onclick="myReqsRejectedDetailsForAProject('contentCenterF')" style="cursor: pointer; background: white;">
					<span class="glyphicon glyphicon-thumbs-down " style=" color: red; font-size:1.5em ">
					</span>
				</td>
			<%
		}			
		
	}	
		
		
		
		
	if (!hideFromHealthBar.contains("pendingOthersApproval")){		
		int myRequirementsPendingApproval = ProjectUtil.getReqsPendingApprovalCount(user.getEmailId(),  project.getProjectId() ,  "ProjectDashboard", user.getEmailId());	
		
		if (myRequirementsPendingApproval == 0){
			%>
				<td align="center" 
					 title="Congratlations !!! There are no requirements owned by you pending other's approval"
					>
					<span class="glyphicon glyphicon-heart " style=" color: green; font-size:1.5em " >
					</span>
				</td>
			<%
		}
		else {
			%>
				<td align="center" 
					 title='Your Objects Pending Others : <%=myRequirementsPendingApproval%> requirements owned by you are pending  approval from others'
					onclick="myReqsPendingApprovalDetailsForAProject('contentCenterF')" style="cursor: pointer; background: white;">
					<span class="glyphicon glyphicon-user " style=" color: red; font-size:1.5em ">
					</span>
				</td>
			<%
		}		
		
	}	
		
		%>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<%
		
	
		
	if (!hideFromHealthBar.contains("dangling")){
		int myDanglingRequirements = ProjectUtil.getDanglingRequriementsCount(user.getEmailId(), project.getProjectId(), "ProjectDashboard", user.getEmailId());	
		if (myDanglingRequirements == 0){
			%>
				<td align="center" 
					 title="Congratlations !!! There are no requirements owned by you that are dangling i.e without a downstream object"
					>
					<span class="glyphicon glyphicon-heart " style=" color: green; font-size:1.5em " >
					</span>
				</td>
			<%
		}
		else {
			%>
				<td align="center" 
					 title='Dangling: <%=myDanglingRequirements%> requirements owned by you are dangling. i.e without a downstream object'
					onclick="myDanglingReqsDetailsForAProject('contentCenterF')" style="cursor: pointer; background: white;">
					<b><font   color='red'>D</font></b>
				</td>
			<%
		}		
	}
		
	if (!hideFromHealthBar.contains("orphan")){
		int myOrphanRequirements = ProjectUtil.getOrphanRequriementsCount(user.getEmailId(), project.getProjectId(), "ProjectDashboard", user.getEmailId());
		if (myOrphanRequirements == 0){
			%>
				<td align="center" 
					 title="Congratlations !!! There are no requirements owned by you that are orphan. i.e Without an upstream object"
					>
					<span class="glyphicon glyphicon-heart " style=" color: green; font-size:1.5em " >
					</span>
				</td>
			<%
		}
		else {
			%>
				<td align="center" 
					 title='Orphan: <%=myOrphanRequirements%> requirements owned by you  are orphan. i.e Without an uptream object'
					onclick="myOrphanReqsDetailsForAProject('contentCenterF')" style="cursor: pointer; background: white;">
					<b><font   color='red'>O</font></b>
				</td>
			<%
		}
	}
		
		
		
		
		
		
		

		%>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<%
		
		
		
		
		
		
		
		
	if (!hideFromHealthBar.contains("suspectDown")){
		int mySuspectDownStreamRequirements = ProjectUtil.getSuspectDownStreamRequriementsCount(user.getEmailId(), project.getProjectId(),  "ProjectDashboard", user.getEmailId());

		if (mySuspectDownStreamRequirements == 0){
			%>
				<td align="center" 
					 title="Congratlations !!! There are no requirements owned by you that have a suspect downstream. i.e un-resolved downstream change"
					>
					<span class="glyphicon glyphicon-heart " style=" color: green; font-size:1.5em " >
					</span>
				</td>
			<%
		}
		else {
			%>
				<td align="center" 
					 title='Suspect Down Stream : <%=mySuspectDownStreamRequirements%> requirements owned by you have a suspect downstream trace. i.e un-resolved downstream change'
					onclick="mySuspectDownDetailsForAProject('contentCenterF')" style="cursor: pointer;  background-color:pink;">
					<img src="/GloreeJava2/images/arrow_down.png"> 
				</td>
			<%
		}
	}
		

		
	if (!hideFromHealthBar.contains("suspectUp")){
		int mySuspectUpStreamRequirements = ProjectUtil.getSuspectUpStreamRequriementsCount(user.getEmailId(), project.getProjectId(),  "ProjectDashboard", user.getEmailId());

		if (mySuspectUpStreamRequirements == 0){
			%>
				<td align="center" 
					 title="Congratlations !!! There are no requirements owned by you that have a suspect upstream. i.e un-resolved upstream change"
					>
					<span class="glyphicon glyphicon-heart " style=" color: green; font-size:1.5em " >
					</span>
				</td>
			<%	
		}
		else {
			%>
				<td align="center" 
					 title='Suspect Up Stream : <%=mySuspectUpStreamRequirements%> requirements owned by you have a suspect upstream trace. i.e un-resolved upstream change'
					onclick="mySuspectUpDetailsForAProject('contentCenterF')" style="cursor: pointer; background-color: pink; ">
					<img src="/GloreeJava2/images/arrow_up.png"> 

				</td>
			<%
		}
	}

		%>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<%
		
	
		
	if (!hideFromHealthBar.contains("testFailed")){
		int myTestFailedRequirements = ProjectUtil.getTestFailedRequriementsCount(user.getEmailId(), project.getProjectId(),   "ProjectDashboard", user.getEmailId());
		if (myTestFailedRequirements == 0){
			%>
				<td align="center" 
					 title="Congratlations !!! There are no requirements owned by you that have a  failed test downstream"
					>
					<span class="glyphicon glyphicon-heart " style=" color: green; font-size:1.5em " >
					</span>
				</td>
			<%	
		}
		else {
			%>
				<td align="center" 
					 title='Failed Tests : <%=myTestFailedRequirements%> requirements owned by you have a failed test downstream'
					onclick="myTestFailedReqsDetailsForAProject('contentCenterF')" style="cursor: pointer; background-color:pink; ">
					
					<img src="/GloreeJava2/images/testingFailed.png"> 
				</td>
			<%
		}
	}
		
		
	if (!hideFromHealthBar.contains("incomplete")){
		int myIncompleteRequirements = ProjectUtil.getIncompleteRequriementsCount(user.getEmailId(), project.getProjectId(),  "ProjectDashboard", user.getEmailId() );
		if (myIncompleteRequirements == 0){
			%>
				<td align="center" 
					 title="Congratlations !!! All requirements owned by you have been completed"
					>
					<span class="glyphicon glyphicon-heart " style=" color: green; font-size:1.5em " >
					</span>
				</td>
			<%	
		}
		else {
			%>
				<td align="center" 
					 title='<%=myIncompleteRequirements%> requirements owned by you are incomplete'
					onclick="myIncompleteReqsDetailsForAProject('contentCenterF')" style="cursor: pointer; background: white;">
					<b><font   color='red'>INC</font></b>
				</td>
			<%
		}
	}
		
		
%>



	
</tr>
</table>