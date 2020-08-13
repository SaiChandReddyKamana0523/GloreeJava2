<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<%
	// authentication only
	String displayProjectInfoIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayProjectInfoIsLoggedIn  == null) || (displayProjectInfoIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	ArrayList folders = project.getMyFolders();
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)

	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember &&
			(!(project.getProjectTags().toLowerCase().contains("hide_homepage")))	
		){
		
		User user = securityProfile.getUser();
	
	%>	
	
	
	
			<table width='100%' >
				<tr>
				
				<td  align="center" style='background-color:white;' >
					<%
					if (!(project.getProjectTags().toLowerCase().contains("hide_scrum"))){
					%>	
					<a href="#" id='scrumBoard'
					class="btn btn-xs btn-outline-primary"  
						style="width:60px;border-color:blue; color:blue;"
						onclick="
								displayAgileScrumHome();
								if (document.getElementById(&quot;createTracesDiv&quot;) != null){
									document.getElementById(&quot;createTracesDiv&quot;).style.display=&quot;none&quot;;
								}
								if (document.getElementById(&quot;attributeInfo&quot;) != null){
									document.getElementById(&quot;attributeInfo&quot;).style.display=&quot;block&quot;;
								}
								document.getElementById(&quot;requirementActionDiv&quot;).style.display=&quot;none&quot;
								
								  " title='Scrum Board to Manage Sprints'> Scrum</a>
					<%} %>
					
					&nbsp;&nbsp;&nbsp;
					<%
					if (!(project.getProjectTags().toLowerCase().contains("hide_tour"))){
					%>
						<a href="#" id='startTour' 
						class="btn btn-xs btn-outline-primary"  
						style="width:60px;border-color:blue; color:blue;"
						 onclick='startTraceCloudTour()' 
						>
						 Tour <a>
						</a>
					<%} %>
					
					
					<%
					if (
						(securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))
						||
						(securityProfile.getRoles().contains("PowerUserInProject" + project.getProjectId()))
						){
						// only admins get sthe administer link.
					%>
						&nbsp;&nbsp;&nbsp;
						<a href="#"
						 id='configureProject' 
						class="btn btn-xs btn-outline-danger"  
						style="width:90px;border-color:red; color:red;"
						onClick='administerProject()' value='Configure'> Configure </a>
					<%
					}
					%>
				</td>
				
				<td  align="left"  >
					<div id='personalizedStatusDiv' style='display:none;'></div>
				</td>
				</tr>
				<tr><td colspan='2'><br><hr></td></tr>
				
			</table>
			
	
			


				
				<div class="panel-body"> 	
				
				
				
				<ul class="nav nav-tabs">
				  <li class="active">
				  	<a id='iWantToTabLink'  data-toggle="tab" href="#iWantToDiv"
					 		onclick='
					 			document.getElementById("iWantToDiv").style.display="block";
								document.getElementById("myTasksTabDiv").style.display="none";
								document.getElementById("commentsDiv").style.display="none";
								document.getElementById("changesDiv").style.display="none";
								showIWantTo();
							'
					> <span class="glyphicon glyphicon-wrench " style=" color: blue; font-size:1.5em " ></span>  I Want To </a></li>
				  <li>
				  	<a id='myStatusTabLink' data-toggle="tab" href="#myTasksTabDiv" 	
				  			onclick='
					 			document.getElementById("iWantToDiv").style.display="none";
								document.getElementById("myTasksTabDiv").style.display="block";
								document.getElementById("commentsDiv").style.display="none";
								document.getElementById("changesDiv").style.display="none";
								showMyStatusForAProject();
							'
					><span class="glyphicon glyphicon-dashboard " style=" color: red; font-size:1.5em " ></span>  My Status</a></li>
				  
				 	 <li>
				  	<a id='recentCommentsTabLink' data-toggle="tab" href="#commentsDiv" 	
				  			onclick='
					 			document.getElementById("iWantToDiv").style.display="none";
								document.getElementById("myTasksTabDiv").style.display="none";
								document.getElementById("commentsDiv").style.display="block";
								document.getElementById("changesDiv").style.display="none";
								fillRecentlyCommentedReqsForAProject();
							'
							
					> <span class="glyphicon glyphicon-comment " style=" color: green; font-size:1.5em " ></span> Recent Comments in Project </a></li>
					
					
					 <li>
				  	<a id='recentChangesTabLink' data-toggle="tab" href="#changesDiv" 	
				  			onclick='
					 			document.getElementById("iWantToDiv").style.display="none";
								document.getElementById("myTasksTabDiv").style.display="none";
								document.getElementById("commentsDiv").style.display="none";
								document.getElementById("changesDiv").style.display="block";
								fillRecentlyChangedReqsForAProject();
							'
					>  <span class="glyphicon glyphicon-pencil " style=" color: blue; font-size:1.5em " ></span> Recent Changes in Project</a></li>
					
					
					
				  </ul>				
				
				
			
				
							
				<div id='iWantToDiv' class="tab-pane fade in active" > 
					
					<div class='alert alert-info'>
						<div class="btn-group" style='margin-left:100px'>
						  <button type="button"  
						  	class="btn btn-xs btn-outline-primary"  
							style="border-color:whitea; color:blue; background-color:white;"
						>Create</button>
						  <button type="button" 
						  	class="btn btn-xs  btn-outline-primary dropdown-toggle"  
							style="border-color:whitea; color:blue; background-color:white;"
							 data-toggle="dropdown"
						   aria-expanded="false" id='createObjectsDropDown'>
							<span class="caret"></span>
							<span class="sr-only">Toggle Dropdown</span>
						  </button>
						  <ul class="dropdown-menu" role="menu">
							<%
						  	Iterator f  = folders.iterator();
						  	while (f.hasNext()){
						  		Folder folder = (Folder) f.next();
						  		%>
						  		<li>
									<a href="#" 
										onclick='
											displayFolderInExplorer(<%=folder.getFolderId()%>); 
											displayFolderContentCenterA(<%=folder.getFolderId()%>) ;
											createRequirementForm(<%=folder.getFolderId()%>) ;
										'
									>
											a <%=folder.getRequirementTypeName() %> in <b>Folder</b>  <%=folder.getFolderPath() %> &nbsp; &nbsp;  (<%=folder.getCountOfRequirements() %>)
										</a></li>
						  		<%
						  	}
						  %>
						  </ul>
						</div>
					</div>




					<div class='alert alert-info'>
						<div class="btn-group" style='margin-left:100px'>
						  <button type="button"  
						  	class="btn btn-xs  btn-outline-primary"  
							style="border-color:whitea; color:blue; background-color:white;"
						 >Import </button>
						  <button type="button" 
						  	class="btn btn-xs  btn-outline-primary dropdown-toggle"  
							style="border-color:whitea; color:blue; background-color:white;"
							 data-toggle="dropdown" aria-expanded="false" id='importObjectsDropDown'>
							<span class="caret"></span>
							<span class="sr-only">Toggle Dropdown</span>
						  </button>
						  <ul class="dropdown-menu" role="menu">
							<%
						  	f  = folders.iterator();
						  	while (f.hasNext()){
						  		Folder folder = (Folder) f.next();
						  		%>
						  		<li>
									<a href="#" 
										onclick='
											displayFolderInExplorer(<%=folder.getFolderId()%>); 
											displayFolderContentCenterA(<%=folder.getFolderId()%>) ;
											importFromExcelForm(<%=folder.getFolderId()%>) ;
										'
									>
											an Excel in to  <b>Folder</b>  <%=folder.getFolderPath() %> &nbsp; &nbsp;  (<%=folder.getCountOfRequirements() %>)
										</a></li>
						  		<%
						  	}
						  %>
						  </ul>
						</div>
					</div>


					
					<div class='alert alert-info'>
						<div class="btn-group" style='margin-left:100px'>
						  <button type="button" 
						  	class="btn btn-xs  btn-outline-primary"  
							style="border-color:whitea; color:blue; background-color:white;"
						 	>Report</button>
						  <button type="button" 
						  	class="btn btn-xs  btn-outline-primary dropdown-toggle"  
							style="border-color:whitea; color:blue; background-color:white;"
							 data-toggle="dropdown" aria-expanded="false" id='reportDropDown'>
							<span class="caret"></span>
							<span class="sr-only">Toggle Dropdown</span>
						  </button>
						  <ul class="dropdown-menu" role="menu">
							<li>
								<a href="#" 
									onclick="
										document.getElementById('savedReportsDiv').style.visibility='visible'
										document.getElementById('createReportsDiv').style.visibility='hidden'
										document.getElementById('createTraceTreeDiv').style.visibility='hidden'
									"
								> Saved Reports</a></li>
							<li>
								<a href="#" 
									onclick="
										document.getElementById('savedReportsDiv').style.visibility='hidden'
										document.getElementById('createReportsDiv').style.visibility='visible'
										document.getElementById('createTraceTreeDiv').style.visibility='hidden'
									"
								> Create New Report</a></li>
							<li>
								<a href="#" 
									onclick="
										document.getElementById('savedReportsDiv').style.visibility='hidden'
										document.getElementById('createReportsDiv').style.visibility='hidden'
										document.getElementById('createTraceTreeDiv').style.visibility='visible'
									"
								> Create New Trace Tree</a></li>
							
						  </ul>
						</div>
						
						&nbsp;&nbsp;
						<div id='savedReportsDiv' style='visibility:hidden' class="btn-group" >
						  <button type="button"  
						  	class="btn btn-xs btn-outline-primary"  
							style="border-color:whitea; color:blue; background-color:white;"
						>Saved Reports</button>
						  <button type="button" 
						  	class="btn btn-xs btn-outline-primary dropdown-toggle"  
							style="border-color:whitea; color:blue; background-color:white;"
							 data-toggle="dropdown" aria-expanded="false">
							<span class="caret"></span>
							<span class="sr-only">Toggle Dropdown</span>
						  </button>
						  <ul class="dropdown-menu" role="menu">
							<%
							ArrayList reports = ProjectUtil.getAllReportsInProject(project.getProjectId());
							int rCount   = 0;
							if (reports != null){
						    	Iterator rI = reports.iterator();
						    	while ( rI.hasNext() ) {
						    		Report r = (Report) rI.next();
						    		if (
						    				!(r.getReportDescription().startsWith("Canned"))
						    				&&
						    				(r.getReportVisibility().equals("public"))
						    			)  
						    		{
						    			
						    			%>
						    			
							
										<%if (r.getReportType().equals("list")){ 
											rCount++;
										%>
											
											<li>
				 							<a href="#" title='Created by  <%=r.getCreatedByEmailId() %> '
				 							onClick="navigateToAndDisplayExistingReport(<%=r.getFolderId()%>, <%=r.getReportId()%>,'list')">
				 							<img src="/GloreeJava2/images/report16.png" border="0">
				 							&nbsp;<%=r.getReportName() %></a>
				 							</li>	
				 						<%} else { 
				 							rCount++;
				 						%>
				 							<li>
				 							<a href="#" onClick="navigateToAndDisplayExistingReport(<%=r.getFolderId()%>, <%=r.getReportId()%>,'traceTree')">
				 							<img src="/GloreeJava2/images/reportTraceTree.png" border="0">
				 							&nbsp;<%=r.getReportName() %></a>
				 							</li>
				 						<%} %>
						    			<%
						    			
						    		}
						    	}
						    	
							}
						    	
							ArrayList wordTemplates = ProjectUtil.getUserWordTemplates(project.getProjectId(), user, databaseType);
							if (wordTemplates != null){
						    	Iterator i = wordTemplates.iterator();
						    	while ( i.hasNext() ) {
						    		WordTemplate wordTemplate = (WordTemplate) i.next();
						    		String templateVisibility = wordTemplate.getTemplateVisibility();
									if (
											(templateVisibility != null) && 
											(templateVisibility.equals("private")) &&
											(wordTemplate.getCreatedBy().equals(user.getEmailId()))){
										rCount++;
						 %>
						 				<li>
				 							<a href="#" title="Description : <%=wordTemplate.getTemplateDescription() %>"
				 							onClick="navigateToAndDisplayWordTemplate(<%=wordTemplate.getFolderId()%>, <%=wordTemplate.getTemplateId() %>)">
				 							<img src="/GloreeJava2/images/ExportWord16.gif" border="0">
				 							&nbsp;<%=wordTemplate.getTemplateName()%></a> 
		 								</li>
						 			
						 <%
						    		}
						    	}
						    }
						  					    
						%>					
								  
						<%			  
							if (wordTemplates != null){
						    	Iterator i = wordTemplates.iterator();
						    	while ( i.hasNext() ) {
						    		WordTemplate wordTemplate = (WordTemplate) i.next();
						    		String templateVisibility = wordTemplate.getTemplateVisibility();
									if ((templateVisibility != null) && (templateVisibility.equals("public"))){
										rCount++;
						 %>
						 				<li>
				 							<a href="#" title="Description : <%=wordTemplate.getTemplateDescription() %>"
				 							onClick="navigateToAndDisplayWordTemplate(<%=wordTemplate.getFolderId()%>, <%=wordTemplate.getTemplateId() %>)">
				 							<img src="/GloreeJava2/images/ExportWord16.gif" border="0">
				 							&nbsp;<%=wordTemplate.getTemplateName()%></a> 
		 								</li>
						 			
						 			
						 			
						 <%
						    		}
						    	}
						    }
						   			    
						   if (rCount == 0 ){
							%>
								<li><a href="#">No reports found</a></li>										
							<%
							}
							%>

						  </ul>
						</div>									
						
						
						&nbsp;&nbsp;
						<div id='createReportsDiv' style='visibility:hidden'  class="btn-group" >
						  <button type="button"  
						  	class="btn btn-xs  btn-outline-primary"  
							style="border-color:whitea; color:blue; background-color:white;"
						  >Create New Report </button>
						  <button type="button" 
						  class="btn btn-xs btn-outline-primary dropdown-toggle"  
							style="border-color:whitea; color:blue; background-color:white;"
							 data-toggle="dropdown" aria-expanded="false">
							<span class="caret"></span>
							<span class="sr-only">Toggle Dropdown</span>
						  </button>
						  <ul class="dropdown-menu" role="menu">
							<%
						  	f  = folders.iterator();
						  	while (f.hasNext()){
						  		Folder folder = (Folder) f.next();
						  		%>
						  		<li>
									<a href="#" 
										onclick='
											displayFolderInExplorer(<%=folder.getFolderId()%>); 
											displayFolderContentCenterA(<%=folder.getFolderId()%>) ;
											createNewListReport(<%=folder.getFolderId()%>) ;
										'
									>
											for <b>Folder</b>  <%=folder.getFolderPath() %> &nbsp; &nbsp;  (<%=folder.getCountOfRequirements() %>)
										</a></li>
						  		<%
						  	}
						  %>
						  </ul>
						</div>
						
						
						&nbsp;&nbsp;
						<div id='createTraceTreeDiv' style='visibility:hidden'  class="btn-group" >
						  <button type="button"  
							class="btn btn-xs  btn-outline-primary"  
							style="border-color:whitea; color:blue; background-color:white;"
							>Create New Trace Tree </button>
						  <button type="button" 
						  	class="btn btn-xs btn-outline-primary dropdown-toggle"  
							style="border-color:whitea; color:blue; background-color:white;"
							data-toggle="dropdown" aria-expanded="false">
							<span class="caret"></span>
							<span class="sr-only">Toggle Dropdown</span>
						  </button>
						  <ul class="dropdown-menu" role="menu">
							<%
						  	f  = folders.iterator();
						  	while (f.hasNext()){
						  		Folder folder = (Folder) f.next();
						  		%>
						  		<li>
									<a href="#" 
										onclick='
											displayFolderInExplorer(<%=folder.getFolderId()%>); 
											displayFolderContentCenterA(<%=folder.getFolderId()%>) ;
											createNewTraceTreeReport(<%=folder.getFolderId()%>) ;
										'
									>
											for <b>Folder</b>  <%=folder.getFolderPath() %> &nbsp; &nbsp;  (<%=folder.getCountOfRequirements() %>)
										</a></li>
						  		<%
						  	}
						  %>
						  </ul>
						</div>

					</div>

					
					<div class='alert alert-info'>
						<div class="btn-group" style='margin-left:100px'>
						  <button type="button"   
						  	class="btn btn-xs  btn-outline-primary"  
							style="border-color:whitea; color:blue; background-color:white;"
							>Trace (Connect)</button>
						  <button type="button" 
						  	class="btn btn-xs btn-outline-primary dropdown-toggle"  
							style="border-color:whitea; color:blue; background-color:white;"
							data-toggle="dropdown" aria-expanded="false" id='traceDropDown'>
							<span class="caret"></span>
							<span class="sr-only">Toggle Dropdown</span>
						  </button>
						  <ul class="dropdown-menu" role="menu">
							<li><a href="#" title='Manage relationship between requirements' onclick='displayTracePanel();'>Trace Matrix</a></li>
							<li><a href="#" title='Another way to manage relationship between requirements' onclick='displayTraceMatrix();'>Tracer</a></li>
							
						  </ul>
						</div>
					</div>
					
					
						
				<div class='alert alert-info'>
					<div class="btn-group" style='margin-left:100px'>
					  <button type="button"   
					  	class="btn btn-xs  btn-outline-primary"  
						style="border-color:whitea; color:blue; background-color:white;"
						>View Dashboards</button>
					  <button type="button" 
					  		class="btn btn-xs btn-outline-primary dropdown-toggle"  
							style="border-color:whitea; color:blue; background-color:white;"
							data-toggle="dropdown" aria-expanded="false" id='dashboardDropDown'>
						<span class="caret"></span>
						<span class="sr-only">Toggle Dropdown</span>
					  </button>
					  <ul class="dropdown-menu" role="menu">
						<li><a href="#" title='<%=user.getFirstName()%> <%=user.getLastName() %> Dashboard' onclick='displayUserMetricsForm();'>My Dashboard</a></li>
						<li><a href="#" title='Status of Project' onclick='displayProjectMetricsForm();'>Project Dashboard</a></li>
						<li><a href="#" title='Status of Release' onclick='displayReleaseMetricsForm();'>Release Dashboard</a></li>
						<li><a href="#" title='Status of Baseline' onclick='displayBaselineMetricsForm();'>Baseline Dashboard</a></li>
					  </ul>
					</div>
				</div>
				
				
				<div class='alert alert-info'>
					<div class="btn-group" style='margin-left:100px'>
					  <button type="button"   
					  	class="btn btn-xs  btn-outline-primary"  
						style="border-color:whitea; color:blue; background-color:white;"
						>Use Tools </button>
					  <button type="button" 
					  		class="btn btn-xs btn-outline-primary dropdown-toggle"  
							style="border-color:whitea; color:blue; background-color:white;"
							data-toggle="dropdown" aria-expanded="false" id='toolsDropDown'>
						<span class="caret"></span>
						<span class="sr-only">Toggle Dropdown</span>
					  </button>
					  <ul class="dropdown-menu" role="menu">
						<li><a href="#" title='Manage relationship between requirements' onclick='displayTracePanel();'>Trace Matrix</a></li>
						<li><a href="#" title='Another way to manage relationship between requirements' onclick='displayTraceMatrix();'>Tracer</a></li>
						<li><a href="#" title='Snapshot of reports at a point in time' onclick='displayTDCSHome();'>Report Snapshots (TDCS)</a></li>
						<li><a href="#" title='Automatically execute and email reports' onclick='displayScheduler();'>Report Scheduler</a></li>
						<li><a href="#" title='Package and Share Requirements across projects' onclick='displaySharedRequirements();'>Shared Requirements</a></li>
						 <li role="presentation" class="divider"></li>
						<li><a href="#" title='View all changes made to project' onclick='displayChangeLogForm();'>Project Change Log</a></li>
						<li><a href="#" title='View all changes made to project' onclick="window.open('/GloreeJava2/servlet/ProjectBaselineAction?action=exportBaselineToExcel');">Export to Excel</a></li>
	
						<%
					
						String siteEnabledForAgile = this.getServletContext().getInitParameter("siteEnabledForAgile");
						
						if (siteEnabledForAgile.equals("true"))  {
							// we display the AgileScrum link only if this project is enabled for AgileScrum and the site is enabled for agile scrum
						%> 
							<li role="presentation" class="divider"></li>
							<li><a href="#" title='Scrum Board to Manage Sprints' 
								onclick='displayAgileScrumHome();
									if (document.getElementById("createTracesDiv") != null){
										document.getElementById("createTracesDiv").style.display="none";
									}
									if (document.getElementById("attributeInfo") != null){
										document.getElementById("attributeInfo").style.display="block";
									}
								'>Scrum Board</a></li>
						
						<%} %> 
					  </ul>
					</div>
				</div>
												
				</div>		
				
						
				<div id='myTasksTabDiv' class="tab-pane fade"> 
					
						<table  class='table' border=0>
						
								<tr>
									<td id='myTraceabilityRow' colspan=4 class='info' style='text-align:center; vertical-align:middle'>
										My Traceability
									</td>
								
								</tr>				
								<tr>
									
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
								<td  id='myApprovalRow' colspan=4 class='info' style='text-align:center; vertical-align:middle'>
									My Approval
								</td>
								</tr>
							<tr>
								
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
									title='Items that are pending my approval
									'
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
									<td  id='myCompletionRow' colspan=4 class='info' style='text-align:center; vertical-align:middle' >
										My Completion & Validation
									</td>
								</tr>				
								<tr>
									
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
								
						</table>
						<div id='myTasksDiv' class='alert alert-danger' style='display:none'></div>
											
				</div>		
				
				<div id='commentsDiv' class="tab-pane fade"> 
											<table style="width:100%; height:530px;" >
												<tr>
													<td align='left'>
														<div class='alert alert-info'>
														<span class='normalText'><img src="/GloreeJava2/images/comments16.png" border="0"> &nbsp;&nbsp; Comments in the last  
															<input type='text' name='commentedSince' id='commentedSince' value='7' size='3' style="width:40px" 
															></input> days
														</span>	
														
														&nbsp;&nbsp;&nbsp;&nbsp;
														<input id='recentlyCommentedBtn' type='button' class='btn btn-sm btn-primary' value='Go' onclick='fillRecentlyCommentedReqsForAProject();'>
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
				</div>	
				
				<div id='changesDiv' class="tab-pane fade"> 
											<table style="width:100%; height:530px;">
												<tr>
													<td align='left' >
														<div class='alert alert-info'>
															<span class='normalText'><img src="/GloreeJava2/images/userDashboard.png" border="0"> &nbsp;&nbsp; Changes in the last  
																<input type='text' name='changedSince' id='changedSince' 
																value='3' size='4' style="width:40px" ></input> days
															</span>		
															
															&nbsp;&nbsp;&nbsp;&nbsp;
															<input id='recentlyChangedBtn' type='button' class='btn btn-sm btn-primary' value='    Go    ' onclick='fillRecentlyChangedReqsForAProject();'>
															
															&nbsp;&nbsp;&nbsp;&nbsp;
															<input type='button'  title="View all changes made to project" 
															 value = 'Project Change Log' class='btn btn-sm btn-danger' onclick="displayChangeLogForm();">
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
				</div>	
									
				</div> 
	

	
	
	
	
	
	
	
	
	
	
	
	
	

<%}%>