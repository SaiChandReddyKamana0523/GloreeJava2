<!-- GloreeJava2 -->

<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>


<%
	// authentication only
	String displayListReportIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayListReportIsLoggedIn == null) || (displayListReportIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dRIsMember = false;
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile dRSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dRSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		dRIsMember = true;
	}
%>

<%if (dRIsMember){ %>


	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	
	<%
				int folderId = Integer.parseInt(request.getParameter("folderId"));
				
					Folder folder = new Folder(folderId);
					
					String defaultDisplayAttributes =  "";
					ArrayList defaultDisplayAttributesArray = folder.getDefaultDisplayAttributes();
					Iterator dDA = defaultDisplayAttributesArray.iterator();
					while (dDA.hasNext()){
						String temp = (String) dDA.next();
						defaultDisplayAttributes += ":#:" + temp;
					}

				

					String [] allChart = FolderMetricsUtil.getAllRequirementsInFolderURL(folderId);
					String [] approvedChart = FolderMetricsUtil.getApprovedInFolderURL(folderId);
					String [] rejectedChart = FolderMetricsUtil.getRejectedInFolderURL(folderId);
					String [] inApprovalWorkFlowChart = FolderMetricsUtil.getInApprovalWorkFlowInFolderURL(folderId);
					String [] danglingChart = FolderMetricsUtil.getDanglingInFolderURL(folderId,  databaseType);
					String [] orphanChart = FolderMetricsUtil.getOrphanInFolderURL(folderId,  databaseType);
					String[] suspectUpStreamChart = FolderMetricsUtil.getSuspectUpStreamInFolderURL(folderId,  databaseType);		
					String[] suspectDownStreamChart = FolderMetricsUtil.getSuspectDownStreamInFolderURL(folderId,  databaseType);
					String [] completionChart = FolderMetricsUtil.getCompletedInFolderURL(folderId);
					
					String [] allRequirementsByOwnerChart = FolderMetricsUtil.getAllRequirementsByOwnerInFolderURL(folderId);
					String [] inApprovalWorkFlowByOwnerChart = FolderMetricsUtil.getInApprovalWorkFlowByOwnerInFolderURL(folderId);
					String [] approvedByOwnerChart = FolderMetricsUtil.getApprovedByOwnerInFolderURL(folderId);
					String [] rejectedByOwnerChart = FolderMetricsUtil.getRejectedByOwnerInFolderURL(folderId);
					String [] danglingByOwnerChart = FolderMetricsUtil.getDanglingByOwnerInFolderURL(folderId,  databaseType);
					String [] orphanByOwnerChart = FolderMetricsUtil.getOrphanByOwnerInFolderURL(folderId,  databaseType);
					String [] suspectUpStreamByOwnerChart = FolderMetricsUtil.getSuspectUpStreamByOwnerInFolderURL(folderId,  databaseType);
					String [] suspectDownStreamByOwnerChart = FolderMetricsUtil.getSuspectDownStreamByOwnerInFolderURL(folderId,  databaseType);
					String completionByOwnerChart [] = FolderMetricsUtil.getCompletionByOwnerInFolderURL(folderId);
					String nonCompletionByOwnerChart [] = FolderMetricsUtil.getNonCompletionByOwnerInFolderURL(folderId);
					
					
					String allRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
					"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
					"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
					"includeSubFoldersSearch:--:no";
					
					String inApprovalWorkFlowRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
					"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
					"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
					"includeSubFoldersSearch:--:no:###:statusSearch:--:In Approval WorkFlow";
					
					String approvedRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
					"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
					"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
					"includeSubFoldersSearch:--:no:###:statusSearch:--:Approved";
					
					String rejectedRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
					"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
					"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
					"includeSubFoldersSearch:--:no:###:statusSearch:--:Rejected";
					
					
					String danglingRequirementsRD = "active:--:active:###:danglingSearch:--:danglingOnly:###:orphanSearch:--:all:###:" +
					"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
					"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
					"includeSubFoldersSearch:--:no";

					String orphanRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:orphanOnly:###:" +
					"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
					"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
					"includeSubFoldersSearch:--:no";

					String suspectUpStreamRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
					"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
					"suspectUpStreamSearch:--:suspectUpStreamOnly:###:suspectDownStreamSearch:--:all:###:"  +
					"includeSubFoldersSearch:--:no";

					String suspectDownStreamRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
					"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
					"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:suspectDownStreamOnly:###:"  +
					"includeSubFoldersSearch:--:no";

					String completedRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
					"completedSearch:--:completedOnly:###:incompleteSearch:--:all:###:" +
					"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
					"includeSubFoldersSearch:--:no";
					
					String incompleteRequirementsRD = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
					"completedSearch:--:all:###:incompleteSearch:--:incompleteOnly:###:" +
					"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
					"includeSubFoldersSearch:--:no";
			%>
	 
	
	<div id = 'displayMetricsDiv' class='level1Box'>
		<table class='paddedTable' width='100%' align='center'>
			<tr>
				<td align='left' bgcolor='#99CCFF'>				
					<span class='subSectionHeadingText'>
					Folder Metrics : <%=folder.getFolderName() %>
					</span>
				</td>
			</tr>	
			<tr><td>&nbsp;</td></tr>						
			<tr>
				<td align='left'>
				<a href='#' onclick="
					document.getElementById('allChartDiv').style.display = 'block';
					document.getElementById('allByOwnerChartDiv').style.display = 'block';
					document.getElementById('inApprovalWorkFlowChartDiv').style.display = 'block';
					document.getElementById('inApprovalWorkFlowByOwnerChartDiv').style.display = 'block';
					document.getElementById('approvedChartDiv').style.display = 'block';
					document.getElementById('approvedByOwnerChartDiv').style.display = 'block';
					document.getElementById('rejectedChartDiv').style.display = 'block';
					document.getElementById('rejectedByOwnerChartDiv').style.display = 'block';
					document.getElementById('danglingChartDiv').style.display = 'none';
					document.getElementById('danglingByOwnerChartDiv').style.display = 'none';
					document.getElementById('orphanChartDiv').style.display = 'none';
					document.getElementById('orphanByOwnerChartDiv').style.display = 'none';
					document.getElementById('suspectUpStreamChartDiv').style.display = 'none';
					document.getElementById('suspectUpStreamByOwnerChartDiv').style.display = 'none';
					document.getElementById('suspectDownStreamChartDiv').style.display = 'none';
					document.getElementById('suspectDownStreamByOwnerChartDiv').style.display = 'none';
					document.getElementById('completionChartDiv').style.display = 'none';
					document.getElementById('completionByOwnerChartDiv').style.display = 'none';
					document.getElementById('nonCompletionChartDiv').style.display = 'none';
					document.getElementById('nonCompletionByOwnerChartDiv').style.display = 'none';					
					">
					Approval WorkFlow
				</a>
				&nbsp;&nbsp;|&nbsp;&nbsp;
				<a href='#' onclick="
					document.getElementById('allChartDiv').style.display = 'block';
					document.getElementById('allByOwnerChartDiv').style.display = 'block';
					document.getElementById('inApprovalWorkFlowChartDiv').style.display = 'none';
					document.getElementById('inApprovalWorkFlowByOwnerChartDiv').style.display = 'none';
					document.getElementById('approvedChartDiv').style.display = 'none';
					document.getElementById('approvedByOwnerChartDiv').style.display = 'none';
					document.getElementById('rejectedChartDiv').style.display = 'none';
					document.getElementById('rejectedByOwnerChartDiv').style.display = 'none';
					document.getElementById('danglingChartDiv').style.display = 'block';
					document.getElementById('danglingByOwnerChartDiv').style.display = 'block';
					document.getElementById('orphanChartDiv').style.display = 'block';
					document.getElementById('orphanByOwnerChartDiv').style.display = 'block';
					document.getElementById('suspectUpStreamChartDiv').style.display = 'none';
					document.getElementById('suspectUpStreamByOwnerChartDiv').style.display = 'none';
					document.getElementById('suspectDownStreamChartDiv').style.display = 'none';
					document.getElementById('suspectDownStreamByOwnerChartDiv').style.display = 'none';
					document.getElementById('completionChartDiv').style.display = 'none';
					document.getElementById('completionByOwnerChartDiv').style.display = 'none';
					document.getElementById('nonCompletionChartDiv').style.display = 'none';
					document.getElementById('nonCompletionByOwnerChartDiv').style.display = 'none';										
					">
					Traceability
				</a>
				&nbsp;&nbsp;|&nbsp;&nbsp;
				<a href='#' onclick="
					document.getElementById('allChartDiv').style.display = 'block';
					document.getElementById('allByOwnerChartDiv').style.display = 'block';
					document.getElementById('inApprovalWorkFlowChartDiv').style.display = 'none';
					document.getElementById('inApprovalWorkFlowByOwnerChartDiv').style.display = 'none';
					document.getElementById('approvedChartDiv').style.display = 'none';
					document.getElementById('approvedByOwnerChartDiv').style.display = 'none';
					document.getElementById('rejectedChartDiv').style.display = 'none';
					document.getElementById('rejectedByOwnerChartDiv').style.display = 'none';
					document.getElementById('danglingChartDiv').style.display = 'none';
					document.getElementById('danglingByOwnerChartDiv').style.display = 'none';
					document.getElementById('orphanChartDiv').style.display = 'none';
					document.getElementById('orphanByOwnerChartDiv').style.display = 'none';
					document.getElementById('suspectUpStreamChartDiv').style.display = 'block';
					document.getElementById('suspectUpStreamByOwnerChartDiv').style.display = 'block';
					document.getElementById('suspectDownStreamChartDiv').style.display = 'block';
					document.getElementById('suspectDownStreamByOwnerChartDiv').style.display = 'block';
					document.getElementById('completionChartDiv').style.display = 'none';
					document.getElementById('completionByOwnerChartDiv').style.display = 'none';
					document.getElementById('nonCompletionChartDiv').style.display = 'none';
					document.getElementById('nonCompletionByOwnerChartDiv').style.display = 'none';										
					">
					Change Control
				</a>
				&nbsp;&nbsp;|&nbsp;&nbsp;		
				<a href='#' onclick="
					document.getElementById('allChartDiv').style.display = 'block';
					document.getElementById('allByOwnerChartDiv').style.display = 'block';
					document.getElementById('inApprovalWorkFlowChartDiv').style.display = 'none';
					document.getElementById('inApprovalWorkFlowByOwnerChartDiv').style.display = 'none';
					document.getElementById('approvedChartDiv').style.display = 'none';
					document.getElementById('approvedByOwnerChartDiv').style.display = 'none';
					document.getElementById('rejectedChartDiv').style.display = 'none';
					document.getElementById('rejectedByOwnerChartDiv').style.display = 'none';
					document.getElementById('danglingChartDiv').style.display = 'none';
					document.getElementById('danglingByOwnerChartDiv').style.display = 'none';
					document.getElementById('orphanChartDiv').style.display = 'none';
					document.getElementById('orphanByOwnerChartDiv').style.display = 'none';
					document.getElementById('suspectUpStreamChartDiv').style.display = 'none';
					document.getElementById('suspectUpStreamByOwnerChartDiv').style.display = 'none';
					document.getElementById('suspectDownStreamChartDiv').style.display = 'none';
					document.getElementById('suspectDownStreamByOwnerChartDiv').style.display = 'none';
					document.getElementById('completionChartDiv').style.display = 'block';
					document.getElementById('completionByOwnerChartDiv').style.display = 'block';
					document.getElementById('nonCompletionChartDiv').style.display = 'block';
					document.getElementById('nonCompletionByOwnerChartDiv').style.display = 'block';										
					">
					Completion
				</a>
				&nbsp;&nbsp;|&nbsp;&nbsp;			
				<a href='#' onclick="
					document.getElementById('allChartDiv').style.display = 'block';
					document.getElementById('allByOwnerChartDiv').style.display = 'block';
					document.getElementById('inApprovalWorkFlowChartDiv').style.display = 'block';
					document.getElementById('inApprovalWorkFlowByOwnerChartDiv').style.display = 'block';
					document.getElementById('approvedChartDiv').style.display = 'block';
					document.getElementById('approvedByOwnerChartDiv').style.display = 'block';
					document.getElementById('rejectedChartDiv').style.display = 'block';
					document.getElementById('rejectedByOwnerChartDiv').style.display = 'block';
					document.getElementById('danglingChartDiv').style.display = 'block';
					document.getElementById('danglingByOwnerChartDiv').style.display = 'block';
					document.getElementById('orphanChartDiv').style.display = 'block';
					document.getElementById('orphanByOwnerChartDiv').style.display = 'block';
					document.getElementById('suspectUpStreamChartDiv').style.display = 'block';
					document.getElementById('suspectUpStreamByOwnerChartDiv').style.display = 'block';
					document.getElementById('suspectDownStreamChartDiv').style.display = 'block';
					document.getElementById('suspectDownStreamByOwnerChartDiv').style.display = 'block';
					document.getElementById('completionChartDiv').style.display = 'block';
					document.getElementById('completionByOwnerChartDiv').style.display = 'block';
					document.getElementById('nonCompletionChartDiv').style.display = 'block';
					document.getElementById('nonCompletionByOwnerChartDiv').style.display = 'block';										
					">
					Show All
				</a>
				&nbsp;&nbsp;|&nbsp;&nbsp;	
				<a href='#' onclick="
					document.getElementById('allChartDiv').style.display = 'none';
					document.getElementById('allByOwnerChartDiv').style.display = 'none';
					document.getElementById('inApprovalWorkFlowChartDiv').style.display = 'none';
					document.getElementById('inApprovalWorkFlowByOwnerChartDiv').style.display = 'none';
					document.getElementById('approvedChartDiv').style.display = 'none';
					document.getElementById('approvedByOwnerChartDiv').style.display = 'none';
					document.getElementById('rejectedChartDiv').style.display = 'none';
					document.getElementById('rejectedByOwnerChartDiv').style.display = 'none';
					document.getElementById('danglingChartDiv').style.display = 'none';
					document.getElementById('danglingByOwnerChartDiv').style.display = 'none';
					document.getElementById('orphanChartDiv').style.display = 'none';
					document.getElementById('orphanByOwnerChartDiv').style.display = 'none';
					document.getElementById('suspectUpStreamChartDiv').style.display = 'none';
					document.getElementById('suspectUpStreamByOwnerChartDiv').style.display = 'none';
					document.getElementById('suspectDownStreamChartDiv').style.display = 'none';
					document.getElementById('suspectDownStreamByOwnerChartDiv').style.display = 'none';
					document.getElementById('completionChartDiv').style.display = 'none';
					document.getElementById('completionByOwnerChartDiv').style.display = 'none';
					document.getElementById('nonCompletionChartDiv').style.display = 'none';
					document.getElementById('nonCompletionByOwnerChartDiv').style.display = 'none';										
					">
					Hide All
				</a>
			</td>
		</tr>
	</table>
			
				
	<table width='100%' class='paddedTable'>
		<tr>
			<td>
				
				<div id ='allChartDiv' class='level2Box' style='display:none;'>
					<br> <span class='normalText'> 
					<b>Reqs in Folder '<%=folder.getFolderName()%>'</b></span>
					<br><iframe src='<%=allChart[0]%>' width='380', height='150' ></iframe>
					<br>
					<span class='normalText'> 
						<a href='#' 
						onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=allRequirementsRD%>")'>
						<%=allChart[1]%>
						</a>
					</span>  
				 
				
				</div>
			</td>
			<td>
				<div id ='allByOwnerChartDiv' class='level2Box' style='display:none;'>
					<br>
					<span class='normalText'>
						 <b>Reqs By Owner</b>
					</span>
					<br> <iframe src='<%=allRequirementsByOwnerChart[0]%>'  width='380', height='150'></iframe>
					<%
					// Now lets print all the owner names here.
					String ownersEmailId = allRequirementsByOwnerChart[1];
					if (ownersEmailId.contains(":##:")) {
						String [] ownerEmail = ownersEmailId.split(":##:");
						for (int i=0;i < ownerEmail.length; i++){
							String allRequirementsForOwnerRD = "active:--:active:###:" +
							"danglingSearch:--:all:###:orphanSearch:--:all:###:" +
							"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
							"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
							"includeSubFoldersSearch:--:no:###:nameSearch:--::###:descriptionSearch:--::###:" +
							"ownerSearch:--:" + ownerEmail[i];
							%>
							<span class='normalText'> 
								<a href='#' 
								onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=allRequirementsForOwnerRD%>")'>
								<%=ownerEmail[i]%>
								</a>
							</span> 
							<%
						}
					}
					
					%>				 
					
				</div>
			</td>
		</tr>
		<tr>
			<td>				
				<div id ='inApprovalWorkFlowChartDiv' class='level2Box' style='display:none;'>
					<br>
					<span class='normalText'>
					 	<b>Reqs In Approval WorkFlow</b>
					</span>
					<br><iframe src='<%=inApprovalWorkFlowChart[0]%>' width='380', height='150' ></iframe>
					<br>
					<span class='normalText'> 
						<a href='#' 
						onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=inApprovalWorkFlowRequirementsRD%>")'>
						<%=inApprovalWorkFlowChart[1] %>
						</a>
					</span>  
				
				</div>
			</td>
			<td>
				<div id ='inApprovalWorkFlowByOwnerChartDiv' class='level2Box' style='display:none;'>
					<br>
					<span class='normalText'> 
						<b>Pending Approval By</b>
					</span>
					<br> <iframe src='<%=inApprovalWorkFlowByOwnerChart[0]%>'  width='380', height='150'></iframe>
					<%
					// Now lets print all the owner names here.
					String pendingEmailId = inApprovalWorkFlowByOwnerChart[1];
					if (pendingEmailId.contains(":##:")) {
						String [] pendingEmail = pendingEmailId.split(":##:");
						for (int i=0;i < pendingEmail.length; i++){
							String pendingForOwnerRD = "active:--:active:###:" +
							"danglingSearch:--:all:###:orphanSearch:--:all:###:" +
							"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
							"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
							"includeSubFoldersSearch:--:no:###:nameSearch:--::###:descriptionSearch:--::###:"+
							"statusSearch:--:In Approval WorkFlow:###:" +
							"pendingBySearch:--:" + pendingEmail[i];
							%>
							<span class='normalText'> 
								<a href='#' 
								onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=pendingForOwnerRD%>")'>
								<%=pendingEmail[i]%>
								</a>&nbsp;&nbsp;
							</span> 
							<%
						}
					}
					
					%>				 
					
				</div>
			</td>
		</tr>

		<tr>
			<td>				
				<div id ='approvedChartDiv' class='level2Box' style='display:none;'>
					<br>
					<span class='normalText'>
					 	<b>Approved Reqs</b>
					</span>
					<br><iframe src='<%=approvedChart[0]%>' width='380', height='150' ></iframe>
					<br>
					<span class='normalText'> 
						<a href='#' 
						onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=approvedRequirementsRD%>")'>
						<%=approvedChart[1] %>
						</a>
					</span>  
				
				</div>
			</td>
			<td>
				<div id ='approvedByOwnerChartDiv' class='level2Box' style='display:none;'>
					<br>
					<span class='normalText'> 
						<b>Reqs Approved By</b>
					</span>
					<br> <iframe src='<%=approvedByOwnerChart[0]%>'  width='380', height='150'></iframe>
					<%
					// Now lets print all the owner names here.
					String approvedEmailId = approvedByOwnerChart[1];
					if (approvedEmailId.contains(":##:")) {
						String [] approvedEmail = approvedEmailId.split(":##:");
						for (int i=0;i < approvedEmail.length; i++){
							String approvedForOwnerRD = "active:--:active:###:" +
							"danglingSearch:--:all:###:orphanSearch:--:all:###:" +
							"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
							"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
							"includeSubFoldersSearch:--:no:###:nameSearch:--::###:descriptionSearch:--::###:" +
							"statusSearch:--:Approved:###:" +
							"approvedBySearch:--:" + approvedEmail[i];
							%>
							<span class='normalText'> 
								<a href='#' 
								onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=approvedForOwnerRD%>")'>
								<%=approvedEmail[i]%>
								</a>&nbsp;&nbsp;
							</span> 
							<%
						}
					}
					
					%>				 
					
				</div>
			</td>
		</tr>


		<tr>
			<td>				
				<div id ='rejectedChartDiv' class='level2Box' style='display:none;'>
					<br>
					<span class='normalText'>
					 	<b>Rejected Reqs</b>
					</span>
					<br><iframe src='<%=rejectedChart[0]%>' width='380', height='150' ></iframe>
					<br>
					<span class='normalText'> 
						<a href='#' 
						onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=rejectedRequirementsRD%>")'>
						<%=rejectedChart[1] %>
						</a>
					</span>  
				
				</div>
			</td>
			<td>
				<div id ='rejectedByOwnerChartDiv' class='level2Box' style='display:none;'>
					<br>
					<span class='normalText'> 
						<b>Reqs Rejected By</b>
					</span>
					<br> <iframe src='<%=rejectedByOwnerChart[0]%>'  width='380', height='150'></iframe>
					<%
					// Now lets print all the owner names here.
					String rejectedEmailId = rejectedByOwnerChart[1];
					if (rejectedEmailId.contains(":##:")) {
						String [] rejectedEmail = rejectedEmailId.split(":##:");
						for (int i=0;i < rejectedEmail.length; i++){
							String rejectedForOwnerRD = "active:--:active:###:" +
							"danglingSearch:--:all:###:orphanSearch:--:all:###:" +
							"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
							"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
							"includeSubFoldersSearch:--:no:###:nameSearch:--::###:descriptionSearch:--::###:" +
							"statusSearch:--:Rejected:###:" +
							"rejectedBySearch:--:" + rejectedEmail[i];
							%>
							<span class='normalText'> 
								<a href='#' 
								onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=rejectedForOwnerRD%>")'>
								<%=rejectedEmail[i]%>
								</a>&nbsp;&nbsp;
							</span> 
							<%
						}
					}
					
					%>				 
					
				</div>
			</td>
		</tr>
		
		<tr>
			<td>				
				<div id ='danglingChartDiv' class='level2Box' style='display:none;'>
					<br>
					<span class='normalText'>
					 	<b>Dangling Reqs</b>
					</span>
					<br><iframe src='<%=danglingChart[0]%>' width='380', height='150' ></iframe>
					<br>
					<span class='normalText'> 
						<a href='#' 
						onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=danglingRequirementsRD%>")'>
						<%=danglingChart[1] %>
						</a>
					</span>  
				
				</div>
			</td>
			<td>
				<div id ='danglingByOwnerChartDiv' class='level2Box' style='display:none;'>
					<br>
					<span class='normalText'> 
						<b>Dangling By Owner</b>
					</span>
					<br> <iframe src='<%=danglingByOwnerChart[0]%>'  width='380', height='150'></iframe>
					<%
					// Now lets print all the owner names here.
					ownersEmailId = danglingByOwnerChart[1];
					if (ownersEmailId.contains(":##:")) {
						String [] ownerEmail = ownersEmailId.split(":##:");
						for (int i=0;i < ownerEmail.length; i++){
							String danglingForOwnerRD = "active:--:active:###:" +
							"danglingSearch:--:danglingOnly:###:orphanSearch:--:all:###:" +
							"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
							"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
							"includeSubFoldersSearch:--:no:###:nameSearch:--::###:descriptionSearch:--::###:" +
							"ownerSearch:--:" + ownerEmail[i];
							%>
							<span class='normalText'> 
								<a href='#' 
								onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=danglingForOwnerRD%>")'>
								<%=ownerEmail[i]%>
								</a>
							</span> 
							<%
						}
					}
					
					%>				 
					
				</div>
			</td>
		</tr>
		<tr>
			<td>				
				<div id ='orphanChartDiv' class='level2Box' style='display:none;'>
					<br>
					<span class='normalText'> 
						<b>Orphan Reqs</b>
					</span>
					<br><iframe src='<%=orphanChart[0]%>' width='380', height='150' ></iframe>
					<br>
					<span class='normalText'> 
						<a href='#' 
						onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=orphanRequirementsRD%>")'>
						<%=orphanChart[1]%>
						</a>
					</span>  
				
				</div>
			</td>
			<td>
				<div id ='orphanByOwnerChartDiv' class='level2Box' style='display:none;'>
					<br>
					<span class='normalText'> 
						<b>Orphan By Owner</b>
					</span>
					<br> <iframe src='<%=orphanByOwnerChart[0]%>'  width='380', height='150'></iframe>
					<%
					// Now lets print all the owner names here.
					ownersEmailId = orphanByOwnerChart[1];
					if (ownersEmailId.contains(":##:")) {
						String [] ownerEmail = ownersEmailId.split(":##:");
						for (int i=0;i < ownerEmail.length; i++){
							String orphanForOwnerRD = "active:--:active:###:" +
							"danglingSearch:--:all:###:orphanSearch:--:orphanOnly:###:" +
							"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
							"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
							"includeSubFoldersSearch:--:no:###:nameSearch:--::###:descriptionSearch:--::###:" +
							"ownerSearch:--:" + ownerEmail[i];
							%>
							<span class='normalText'> 
								<a href='#' 
								onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=orphanForOwnerRD%>")'>
								<%=ownerEmail[i]%>
								</a>
							</span> 
							<%
						}
					}
					
					%>				 
					
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<div id ='suspectUpStreamChartDiv' class='level2Box' style='display:none;'>
					<br>
					<span class='normalText'> 
						<b>Reqs with Suspect Changes Upstream</b>
					</span>
					<br> <iframe src='<%=suspectUpStreamChart[0]%>' width='380', height='150'></iframe>
					
					<br>
					<span class='normalText'> 
						<a href='#' 
						onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=suspectUpStreamRequirementsRD%>")'>
						<%=suspectUpStreamChart[1]%>
						</a>
					</span>  
					
				</div>
			</td>
			<td>
				<div id ='suspectUpStreamByOwnerChartDiv' class='level2Box' style='display:none;'>
					<br>
					<span class='normalText'> 
						<b>Reqs with Suspect Changes UpStream By Owner</b>
					</span>
					<br> <iframe src='<%=suspectUpStreamByOwnerChart[0]%>'  width='380', height='150'></iframe>
					<%
						// Now lets print all the owner names here.
						ownersEmailId = suspectUpStreamByOwnerChart[1];
						if (ownersEmailId.contains(":##:")) {
							String [] ownerEmail = ownersEmailId.split(":##:");
							for (int i=0;i < ownerEmail.length; i++){
								String suspectUpStreamForOwnerRD = "active:--:active:###:" +
								"danglingSearch:--:all:###:orphanSearch:--:all:###:" +
								"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
								"suspectUpStreamSearch:--:suspectUpStreamOnly:###:suspectDownStreamSearch:--:all:###:"  +
								"includeSubFoldersSearch:--:no:###:nameSearch:--::###:descriptionSearch:--::###:" +
								"ownerSearch:--:" + ownerEmail[i];
								%>
								<span class='normalText'> 
									<a href='#' 
									onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=suspectUpStreamForOwnerRD%>")'>
									<%=ownerEmail[i]%>
									</a>
								</span> 
								<%
							}
						}
					
					%>				 
					
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<div id ='suspectDownStreamChartDiv' class='level2Box' style='display:none;'>
					<br> 
					<span class='normalText'> 
						<b>Reqs with Suspect Changes Downstream</b>
					</span>
					<br> <iframe src='<%=suspectDownStreamChart[0]%>' width='380', height='150' ></iframe>
	
					<br>
					<span class='normalText'> 
						<a href='#' 
						onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=suspectDownStreamRequirementsRD%>")'>
						<%=suspectDownStreamChart[1]%>
						</a>
					</span>  					
				</div>
			</td>
			<td>
				<div id ='suspectDownStreamByOwnerChartDiv' class='level2Box' style='display:none;'>
					<br>
					<span class='normalText'> 
						<b>Reqs with Suspect Changes DownStream By Owner</b>
					</span>
					<br> <iframe src='<%=suspectDownStreamByOwnerChart[0]%>'  width='380', height='150'></iframe>
					<%
						// Now lets print all the owner names here.
						ownersEmailId = suspectDownStreamByOwnerChart[1];
						if (ownersEmailId.contains(":##:")) {
							String [] ownerEmail = ownersEmailId.split(":##:");
							for (int i=0;i < ownerEmail.length; i++){
								String suspectDownStreamForOwnerRD = "active:--:active:###:" +
								"danglingSearch:--:all:###:orphanSearch:--:all:###:" +
								"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
								"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:suspectDownStreamOnly:###:"  +
								"includeSubFoldersSearch:--:no:###:nameSearch:--::###:descriptionSearch:--::###:" +
								"ownerSearch:--:" + ownerEmail[i];
								%>
								<span class='normalText'> 
									<a href='#' 
									onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=suspectDownStreamForOwnerRD%>")'>
									<%=ownerEmail[i]%>
									</a>
								</span> 
								<%
							}
						}
					
					%>				 
					
				</div>
			</td>
		</tr>
		<tr>
			<td >
				<div id ='completionChartDiv' class='level2Box' style='display:none;'>
					<br> 
					<span class='normalText'>
						<b> Completed Reqs</b>
					</span>
					<br> <iframe src='<%=completionChart[0]%>'  width='380', height='150'></iframe>
					
					<br>
					<span class='normalText'> 
						<a href='#' 
						onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=completedRequirementsRD%>")'>
						<%=completionChart[1] %>
						</a>
					</span>  
				</div>
			</td>
			<td>
				<div id ='completionByOwnerChartDiv' class='level2Box' style='display:none;'>
					<br>
					<span class='normalText'> 
						<b> Completed By Owner</b>
					</span>
					<br> <iframe src='<%=completionByOwnerChart[0]%>'  width='380', height='150'></iframe>
					<%
						// Now lets print all the owner names here.
						ownersEmailId = completionByOwnerChart[1];
						if (ownersEmailId.contains(":##:")) {
							String [] ownerEmail = ownersEmailId.split(":##:");
							for (int i=0;i < ownerEmail.length; i++){
								String completionForOwnerRD = "active:--:active:###:" +
								"danglingSearch:--:all:###:orphanSearch:--:all:###:" +
								"completedSearch:--:completedOnly:###:incompleteSearch:--:all:###:" +
								"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
								"includeSubFoldersSearch:--:no:###:nameSearch:--::###:descriptionSearch:--::###:" +
								"ownerSearch:--:" + ownerEmail[i];
								%>
								<span class='normalText'> 
									<a href='#' 
									onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=completionForOwnerRD%>")'>
									<%=ownerEmail[i]%>
									</a>
								</span> 
								<%
							}
						}
					
					%>				 
					
				</div>
			</td>
		</tr>
		<tr>
			<td >
				<div id ='nonCompletionChartDiv' class='level2Box' style='display:none;'>
					<br>
					<span class='normalText'>
						<b> Incomplete Reqs</b>
					</span>
					<br> <iframe src='<%=completionChart[0]%>'  width='380', height='150'></iframe>
				
					<br>	
					<span class='normalText'> 
						<a href='#' 
						onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=incompleteRequirementsRD%>")'>
						<%=completionChart[2] %>
						</a>
					</span>  
					
				</div>
			</td>
			<td>
				<div id ='nonCompletionByOwnerChartDiv' class='level2Box' style='display:none;'>
					<br>
					<span class='normalText'> 
						<b>Incomplete By Owner</b>
					</span>
					<br> <iframe src='<%=nonCompletionByOwnerChart[0]%>'  width='380', height='150'></iframe>
					<%
						// Now lets print all the owner names here.
						ownersEmailId = nonCompletionByOwnerChart[1];
						if (ownersEmailId.contains(":##:")) {
							String [] ownerEmail = ownersEmailId.split(":##:");
							for (int i=0;i < ownerEmail.length; i++){
								String nonCompletionForOwnerRD = "active:--:active:###:" +
								"danglingSearch:--:all:###:orphanSearch:--:all:###:" +
								"completedSearch:--:all:###:incompleteSearch:--:incompleteOnly:###:" +
								"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
								"includeSubFoldersSearch:--:no:###:nameSearch:--::###:descriptionSearch:--::###:" +
								"ownerSearch:--:" + ownerEmail[i];
								%>
								<span class='normalText'> 
									<a href='#' 
									onclick='displayDynamicReport("<%=defaultDisplayAttributes%>",<%=folderId%>,"list","<%=nonCompletionForOwnerRD%>")'>
									<%=ownerEmail[i]%>
									</a>
								</span> 
								<%
							}
						}
					
					%>				 
					
					
				</div>
			</td>
		</tr>
	</table>
	</div>
<%}%>

