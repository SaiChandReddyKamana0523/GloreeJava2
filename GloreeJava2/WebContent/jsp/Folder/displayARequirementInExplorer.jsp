
<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*"%>
<%@ page import="com.gloree.beans.*"%>
<%@ page import="com.gloree.utils.*"%>

<%
	// authentication only
	String isLoggedIn = (String) session.getAttribute("isLoggedIn");
	if ((isLoggedIn == null)
			|| (isLoggedIn.equals(""))) {
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
		int requirementId = Integer.parseInt(request.getParameter("requirementId"));
		Requirement r = new Requirement(requirementId, databaseType);
		String color = "white";
		Folder folder = new Folder(r.getFolderId());

		
		boolean canBeReportedDangling = folder.canBeReportedDangling();
		boolean canBeReportedOrphan = folder.canBeReportedOrphan();

		
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
		String viewType = "compact";
		String reqName = r.getRequirementName();
		if (viewType.equals("compact")) {
			if (reqName.length() > 50){
				reqName = reqName.substring(0,47) + "...";
			}
		}
		
		boolean canCreate = false;
		if ((securityProfile.getPrivileges().contains("createRequirementsInFolder" 
				+ folder.getFolderId()))){
		
			canCreate = true;
		}
		
		
		
	%>	
 
 
 <table><tr><td>
	
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
						<a href='#' onClick='displayFolderContentsInExplorerInBackground(<%=r.getFolderId() %>, "compact")'>Compact View</a>
					<%}
					else {%>
						<a href='#' onClick='displayFolderContentsInExplorerInBackground(<%=r.getFolderId() %>, "detail")'>Detail View</a>
					<%} %>
				</li>
				</ul>
			</li>
	  	</ul>
		
    </td></tr></table>
    	
	<%	
		
	}
	%>



