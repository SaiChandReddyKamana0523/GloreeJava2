
<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*"%>
<%@ page import="com.gloree.beans.*"%>
<%@ page import="com.gloree.utils.*"%>

<%
	// authentication only
	String displayAllRequirementsInRealFIsLoggedIn = (String) session.getAttribute("isLoggedIn");
	if ((displayAllRequirementsInRealFIsLoggedIn == null)
			|| (displayAllRequirementsInRealFIsLoggedIn.equals(""))) {
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%> 
<jsp:forward page="/jsp/WebSite/startPage.jsp" />
<%
	}
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project = (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User user = securityProfile.getUser();
	String userEmailId = user.getEmailId();

	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())) {
		isMember = true;
	}

	int folderId = 0;
	try {
		folderId = Integer.parseInt(request.getParameter("folderId"));
	}
	catch (Exception e){
		// do nothing
	}
	
	if (folderId == 0){
		%>
		<div class='alert alert-danger'>
			<span class='normalText'>Please select a folder in the Explorer Tab</span>
		</div>
		<%
		
	}
	if (isMember && folderId !=0) {

		
		Folder folder = new Folder(folderId);


		boolean canCreate = false;
		if ((securityProfile.getPrivileges().contains("createRequirementsInFolder" 
				+ folder.getFolderId()))){
		
			canCreate = true;
		}
		
		
		boolean canBeReportedDangling = folder.canBeReportedDangling();
		boolean canBeReportedOrphan = folder.canBeReportedOrphan();

		HashMap<String, String> folderFilters = new HashMap<String, String>();
		ArrayList<Requirement> requirements = folder.getMyRequirementsSorted(project.getProjectId(), databaseType, "", folderFilters);

		// for pagination, lets set the pageSize.
		int pageSize = 200;

		// we add 1 to arraysize/pageSize because, int div truncates things.
		int numOfPages = (requirements.size() / pageSize) + 1;

		int pageToDisplay = 1;
		if (request.getParameter("page") != null) {
			pageToDisplay = Integer.parseInt(request.getParameter("page"));
		}
		int pageStartIndex = (pageToDisplay * pageSize) - pageSize;
		int pageEndIndex = pageStartIndex + pageSize;
		if (pageEndIndex > requirements.size()) {
			pageEndIndex = requirements.size();
		}

		String pageString = "";
		for (int i = 1; i <= numOfPages; i++) {
			if (i == pageToDisplay) {
				pageString += "<b>" + i + "</b>&nbsp;&nbsp;";
			} else {
				pageString += "<a href='#' onclick='reportPagination(\"requirementsInExplorer\","
						+ folder.getFolderId() + "," + i + ", \"\")'> " + i + " </a>";
				pageString += "&nbsp;&nbsp;";
			}
		}
		// drop the last nbsp;
		pageString = (String) pageString.subSequence(0, pageString.lastIndexOf("&nbsp;&nbsp;"));
%>







<%
	if ((securityProfile.getPrivileges().contains("readRequirementsInFolder" + folder.getFolderId()))) {
		String viewType = request.getParameter("viewType");
		
		
		if (
				(viewType == null)||
				(viewType.equals("")) ||
				(viewType.equals("compact")) ||
				(viewType.equals("undefined"))
			){
			viewType = "compact";
		}
		else {
			viewType = "detail";
		}
%>


<div class='alert alert-info' >
	
	<img height='16' width='16' src="/GloreeJava2/images/folder.png" border="0"> <b> <%=folder.getFolderPath() %> </b>
	<br>

	Page &nbsp;&nbsp;<%=pageString%>
	
</div>


<div id='displayRequirementsSectionInFolderDiv'>

	<%
		if (requirements != null) {
			for (int i = pageStartIndex; i < pageEndIndex; i++) {
				int j = i;
				Requirement r = (Requirement) requirements.get(i);

				String color = "white";

				String displayRDInFolderDiv = "displayRDInFolderDiv" + r.getRequirementId();
				String displayRDAlertInFolderDiv = "displayRDAlertInFolderDiv" + r.getRequirementId();

				String displayRequirementInFolderDiv = "displayRequirementInFolderDiv"
						+ r.getRequirementId();

				// lets put spacers here for child requirements.
				String req = r.getRequirementFullTag();
				int start = req.indexOf(".");
				int width = 1;
				
				while (start != -1) {
					width = width + 35;
					start = req.indexOf(".", start + 1);
					
				}
				
				String reqName = r.getRequirementName();
				if (viewType.equals("compact")) {
					if (reqName.length() > 50){
						reqName = reqName.substring(0,47) + "...";
					}
				}
				
				
				
	%>

	
<table   border='0'>
	<tr>
		<td style='min-width:<%=width%>px'>&nbsp; </td>
	
	<td>
		<div  class='objectInExplorer alert  <%=r.getRequirementFullTag()%>' id='objectInExplorer<%=r.getRequirementId() %>'
			style=' width: 260px; border: 2px dotted blue; border-radius: 10px; cursor: pointer;  
			padding: 0px 0px 0px 0px;'
		   	
		>
			
			 <ul  class="nav navbar-nav"  >
				<li class="dropdown">
				 <a href="#" class="dropdown-toggle" data-toggle="dropdown" 
				 style='color:black;'>
					<%=r.getRequirementFullTag()%> :  <%=reqName %> 
				 </a>
				  <ul class="dropdown-menu">
					<li style='display:block'>
						<a href='#'
							onClick="
								document.getElementById('contentCenterF').style.display='none';
								displayRequirement(<%=r.getRequirementId()%>,'List Folder Contents');
							"
							>Open
						</a>
					</li>
					<li class='divider'></li>
					<li style='display:block'>
						<a href='#'
							onclick="
							   	window.open('/GloreeJava2/jsp/Requirement/CIA/traceMap.jsp?requirementId=<%=r.getRequirementId() %>')"
							>Trace Map
						</a>
					</li>
					<li class='divider'></li>
					
					<li style='display:block'>
						<a href='#'
							onclick="
							   hideChildrenInExplorer('<%=r.getRequirementFullTag()%>')"
							>Hide Children
						</a>
					</li>
					<li style='display:block'>
						<a href='#'
							onclick="
							   showChildrenInExplorer('<%=r.getRequirementFullTag()%>')"
							>Show Children
						</a>
						
					</li>
					<%if (canCreate) { %>
						<li style='display:block'>
							<a href='#'
								onclick="
									displayCreateChildForm(<%=r.getFolderId()%>,'<%=r.getRequirementFullTag()%>');"
								>Create a Child
							</a>
							
						</li>
						<%
						if (r.getRequirementFullTag().contains(".")){
							%>
							<li style='display:block'>
								<a href='#'
									onclick="displayCreateChildForm(<%=r.getFolderId()%>,'<%=r.getParentFullTag()%>');"
									>Create a Sibling
								</a>
							</li>
						<%
						}
					}
					%>
					<li class='divider'></li>
					<li style='display:block'>
						<%if (viewType.equals("detail")){ %>
							<a href='#' onClick='displayFolderContentsInExplorerInBackground(<%=folderId%>, "compact")'>Compact View</a>
						<%}
						else {%>
							<a href='#' onClick='displayFolderContentsInExplorerInBackground(<%=folderId%>, "detail")'>Detail View</a>
						<%} %>
					</li>
					</ul>
				</li>
		  	</ul>	
			<br><br><br><br>
			
			<%if (
					(!(project.getProjectTags().toLowerCase().contains("hide_statusbar_in_explorer")))
					&&
					(viewType.equals("detail"))
					)
					{ %>
							
						<div id='requirementLabelDiv<%=r.getRequirementId()%>'
						style='padding: 0px 0px 10px 25px;'>
							
							<table class='paddedTable' border='1'
								style='background-color:<%=color%>' border=2>
								<tr>
				
									<td width='20px' align='center'>
										<%
											if (!(r.getRequirementLockedBy().equals(""))) {
																// this requirement is locked. so lets display a lock icon.
										%> <span class='normalText'
										title='Requirement locked by <%=r.getRequirementLockedBy()%>'>
											<img src="/GloreeJava2/images/lock16.png" border="0">
									</span> <%
									 	} else {
									 %> <span class='normalText' title='Requirement not locked'>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span> <%
									 	}
									 %>
									</td>
				
				
				
				
				
				
									<%
										if (r.getRequirementTraceTo().length() == 0) {
															if (canBeReportedOrphan) {
									%>
									<td
										title='This requirement is an Orphan, i.e does not trace to Requirements upstream '
										width='20px' align='center' style="background-color: lightgray">
										<b><font size='4' color='red'>O</font></b>
									</td>
									<%
										} else {
									%>
									<td width='20px'></td>
									<%
										}
														} else if (r.getRequirementTraceTo().contains("(s)")) {
									%>
									<td title='There is a suspect upstream trace' width='20px'
										align='center' style="background-color: pink"><img
										src="/GloreeJava2/images/arrow_up.png"></td>
									<%
										} else {
									%>
									<td title='All upstream traces are clear' width='20px'
										align='center' style="background-color: lightgreen"><img
										src="/GloreeJava2/images/arrow_up.png"></td>
				
									<%
										}
									%>
				
				
				
									<%
										if (r.getRequirementTraceFrom().length() == 0) {
															if (canBeReportedDangling) {
									%>
									<td
										title='This requirement is a Dangling Requirement i.e does not have downstream traces'
										width='20px' align='center' style="background-color: lightgray">
										<b><font size='4' color='red'>D</font>
										</d>
									</td>
									<%
										} else {
									%>
									<td width='20px'></td>
									<%
										}
														} else if (r.getRequirementTraceFrom().contains("(s)")) {
									%>
									<td title='There is a suspect downstream trace' width='20px'
										align='center' style="background-color: pink"><img
										src="/GloreeJava2/images/arrow_down.png"></td>
									<%
										} else {
									%>
									<td title='All downstream traces are clear' width='20px'
										align='center' style="background-color: lightgreen"><img
										src="/GloreeJava2/images/arrow_down.png"></td>
				
									<%
										}
									%>
				
				
				
									<%
										if (r.getTestingStatus().equals("Pending")) {
									%>
									<td title='Testing is Pending' width='20px' align='center'
										style="background-color: lightgray">&nbsp;&nbsp;&nbsp;&nbsp;
									</td>
									<%
										} else if (r.getTestingStatus().equals("Pass")) {
									%>
									<td title='Testing Passed' width='20px' align='center'
										style="background-color: lightgreen"><img
										src="/GloreeJava2/images/testingPassed.png"></td>
									<%
										} else {
									%>
									<td title='Testing Failed' width='20px' align='center'
										style="background-color: pink"><img
										src="/GloreeJava2/images/testingFailed.png"></td>
				
									<%
										}
									%>
				
				
				
				
				
									<%
										if (r.getRequirementPctComplete() == 100) {
									%>
				
									<td title='Percent of work completed' align='center'
										style="background-color: lightgreen"><%=r.getRequirementPctComplete()%>%
									</td>
									<%
										} else if (r.getRequirementPctComplete() == 0) {
									%>
									<td title='Percent of work completed' align='center'
										style="background-color: lightgray">&nbsp;&nbsp;&nbsp;<%=r.getRequirementPctComplete()%>%
									</td>
				
									<%
										} else {
									%>
									<td title='Percent of work completed' align='center'
										style="background-color: pink">&nbsp;&nbsp;<%=r.getRequirementPctComplete()%>%
									</td>
									<%
										}
									%>
				
				
									<%
										if (project.getHidePriority() != 1) {
									%>
				
									<%
										if (r.getRequirementPriority().equals("High")) {
									%>
				
									<td title='Requirement Priority is High' align='center'
										style="background-color: lightgray"><font color='red'><b>H</b></font>
									</td>
									<%
										} else if (r.getRequirementPriority().equals("Medium")) {
									%>
									<td title='Requirement Priority is Medium' align='center'
										style="background-color: lightgray"><font color='blue'><b>M</b></font>
									</td>
				
									<%
										} else {
									%>
									<td title='Requirement Priority is Low' align='center'
										style="background-color: lightgray"><font color='black'><b>L</b></font>
									</td>
									<%
										}
									%>
									<%
										}
									%>
								</tr>
							</table>
						</div>
				
			
			<%} %>
			
		
      			
		
      
    		</div>
    
   	 </td>
	</tr>

	</table>




	<%
		}
				}
	%>
	
</div>
<%
	}
%>

<br>

<div >

	Page &nbsp;&nbsp;<%=pageString%>

</div>








<%
	}
%>
