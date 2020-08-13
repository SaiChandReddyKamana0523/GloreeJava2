<%@ page import="java.util.*" %>
<%@ page import="java.sql.Date" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>
	
<%
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	Project project= (Project) session.getAttribute("project");

	if (securityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%
	}


try {	
	User user = securityProfile.getUser();
	
%>
	
			<div id="myTasksDiv" class="level1Box" width='100%'>
			
				<table  class='table' border=0>ear
				
										
						<tr>
							<td class='danger' style='text-align:center; vertical-align:middle'>
								My Traceability
							</td>
						
							<td style='height:100px; width:16.7%' align='center' >
								<table border=1 width='100%'><tr><td align='center' 
								onmouseover=  "this.style.background='lightblue'; document.getElementById('myDanglingDiv').style.background='lightblue';" 
								onmouseout=  "this.style.background='white';  document.getElementById('myDanglingDiv').style.background='white';"
								title='My Items that are dangling i.e do not have any trace from a downstream item'
								onclick='myDanglingReqsDetailsForAProject()'
								Style="cursor:pointer"
								>
								
								<div id='myDanglingDiv' >
									<span class='normalText'>Dangling</span>
								</div>
								</td></tr></table>
							</td>
							<td style='height:100px; width:16.7% ' align='center' >
								<table border=1 width='100%'><tr><td align='center' 
								onmouseover=  "this.style.background='lightblue'; document.getElementById('myOrphanDiv').style.background='lightblue';" 
								onmouseout=  "this.style.background='white';  document.getElementById('myOrphanDiv').style.background='white';"
								title='My Items that are Orphan, i.e do not trace to any upstream item' 
								onclick='myOrphanReqsDetailsForAProject()'
								Style="cursor:pointer"
								>
								<div class="level1Box" id='myOrphanDiv'>
									<span class='normalText'> Orphan</span>
								</div>
								</td></tr></table>
							</td>
							<td style='height:100px; width:16.7% ' align='center' >
								<table border=1 width='100%'><tr><td align='center'  
								onmouseover=  "this.style.background='lightblue'; document.getElementById('suspectUpDiv').style.background='lightblue';" 
								onmouseout=  "this.style.background='white';  document.getElementById('suspectUpDiv').style.background='white';"
								title='My items with a suspect trace Downstream (Something has changed Upstream) '
								onclick='mySuspectUpDetailsForAProject()'
								Style="cursor:pointer"
								>
								<div class="level1Box" id='suspectUpDiv'>
									Suspect Up 
								</div>
								</td></tr></table>
							</td>
							<td style='height:100px; width:16.7% ' align='center'>
								<table border=1 width='100%'><tr><td align='center' 
								onmouseover=  "this.style.background='lightblue'; document.getElementById('suspectDownDiv').style.background='lightblue';" 
								onmouseout=  "this.style.background='white';  document.getElementById('suspectDownDiv').style.background='white';"
								title='My Items with a suspect trace Downstream (Something has changed Downstream)'
								onclick='mySuspectDownDetailsForAProject()'
								Style="cursor:pointer"
								>
								<div class="level1Box"  id ='suspectDownDiv' >
									Suspect Down
								</div>
								</td></tr></table>
							</td>
						</tr>
						
					<tr>
						<td class='danger' style='text-align:center; vertical-align:middle'>
							My Approval
						</td>
						<td style='height:100px; width:16.7%  ' align='center' >
							<table border=1 width='100%'><tr><td align='center' 
							onmouseover=  "this.style.background='lightblue'; document.getElementById('myReqsPendingApprovalDiv').style.background='lightblue';" 
							onmouseout=  "this.style.background='white';  document.getElementById('myReqsPendingApprovalDiv').style.background='white';"
							title="My items that are wainting for other's approval" 
							onclick="myReqsPendingApprovalDetailsForAProject()"
							Style="cursor:pointer"
							>
							<div class="level1Box" id='myReqsPendingApprovalDiv'>
								<span class='normalText'> Pending Approval</span>
							</div>
							</td></tr></table>
						</td>
						<td style='height:100px; width:16.7%  ' align='center' >
							<table border=1 width='100%'><tr><td align='center' 
							onmouseover=  "this.style.background='lightblue'; document.getElementById('myReqsRejectedlDiv').style.background='lightblue';" 
							onmouseout=  "this.style.background='white';  document.getElementById('myReqsRejectedlDiv').style.background='white';"
							title='My Items that have been rejected by other approvers'
							onclick='myReqsRejectedDetailsForAProject()'
							Style="cursor:pointer"
							>
							<div class="level1Box" id='myReqsRejectedlDiv'>
								<span class='normalText'> Rejected</span>
							</div>
							</td></tr></table>
						</td>

						<td style='height:70px; width:25%;' align='center' >
							<table border=1 width='100%'><tr><td align='center' 
							onmouseover=  "this.style.background='lightblue'; document.getElementById('pendingMyApprovalDiv').style.background='lightblue';" 
							onmouseout=  "this.style.background='white';  document.getElementById('pendingMyApprovalDiv').style.background='white';"
							title='Items that are pending my approval'
							onclick="myPendingApprovalDetailsForAProject()"
							Style="cursor:pointer"
							>
							<div class="level1Box" id='pendingMyApprovalDiv' >
								<span class='normalText'> Pending your approval</span>
							</div>
							</td></tr></table>
						</td>
						
					</tr>
					
										
						<tr>
							<td class='danger' style='text-align:center; vertical-align:middle' >
								My Completion & Validation
							</td>
							<td style='height:70px; width:25%;' align='center'>
								<table border=1 width='100%'><tr><td align='center'  
								onmouseover=  "this.style.background='lightblue'; document.getElementById('incompleteDiv').style.background='lightblue';" 
								onmouseout=  "this.style.background='white';  document.getElementById('incompleteDiv').style.background='white';"
								title='My Items that are Incomplete'
								onclick="myIncompleteReqsDetailsForAProject()"
								Style="cursor:pointer"
								>
								<div class="level1Box" id='incompleteDiv'>
									<span class='normalText'> Incomplete</span>
								</div>
								</td></tr></table>
							</td>
							<td style='height:70px; width:25%;' align='center' >
								<table border=1 width='100%'><tr><td align='center' 
								onmouseover=  "this.style.background='lightblue'; document.getElementById('testPendingDiv').style.background='lightblue';" 
								onmouseout=  "this.style.background='white';  document.getElementById('testPendingDiv').style.background='white';"
								title='My items that have not been tested'
								onclick="myTestPendingReqsDetailsForAProject()"
								Style="cursor:pointer"
								>
								<div class="level1Box" id='testPendingDiv'>
									<span class='normalText'> Test Pending </span>
								</div>
								</td></tr></table>
							</td>
							<td style='height:70px; width:25%;' align='center' >
								<table border=1 width='100%'><tr><td align='center' 
								onmouseover=  "this.style.background='lightblue'; document.getElementById('testFailedDiv').style.background='lightblue';" 
								onmouseout=  "this.style.background='white';  document.getElementById('testFailedDiv').style.background='white';"
								title='My Items that have Failed testing'
								onclick="myTestFailedReqsDetailsForAProject()"
								Style="cursor:pointer"
								>
								<div class="level1Box" id='testFailedDiv'>
									<span class='normalText'> Test Failed </span>
								</div>
								</td></tr></table>
							</td>
							<td>
							</td>
						</tr>
						
						<tr>
							<td colspan='5' style='height:540px'>
									<table style="width:100%; height:530px;" border='1'>
										<tr>
											<td align='left'>
												<div class='alert alert-info'>
												<span class='normalText'><img src="/GloreeJava2/images/comments16.png" border="0"> &nbsp;&nbsp; Comments in the last  
													<input type='text' name='commentedSince' id='commentedSince' value='7' size='3' style="width:40px" 
													onchange='fillRecentlyCommentedReqsForAProject();'></input> days
												</span>	
												</div>
											</td>
										</tr>
										<tr>
										
											<td >
												<div class="level1Box" id='recentlyCommentedReqsDiv'>
													
												</div>
											</td>
										</tr>
									</table>
								
							</td>
						</tr>


											
					<tr>
						<td colspan='5' style='height:540px'>
									<table style="width:100%; height:530px;" border='1'>
										<tr>
											<td align='left' >
												<div class='alert alert-info'>
												<span class='normalText'><img src="/GloreeJava2/images/userDashboard.png" border="0"> &nbsp;&nbsp; Changes in the last  
												<input type='text' name='changedSince' id='changedSince' 
												value='3' size='4' style="width:40px" 
												onchange='fillRecentlyChangedReqsForAProject();'></input> days
												</span>		
												</div>
											</td>
										</tr>
										<tr>
											<td >
												<div class="level1Box" id='recentlyChangedReqsDiv'>
													
												</div>
											</td>
										</tr>
									</table>
								
						</td>
					</tr>
						
						
					
															
				</table>
			</div>
		



<%
}
catch (Exception e) {

}

%>
 







