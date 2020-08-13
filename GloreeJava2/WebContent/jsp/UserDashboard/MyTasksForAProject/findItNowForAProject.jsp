<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*"%>
<%@ page import="java.sql.Date"%>
<%@ page import="com.gloree.beans.*"%>
<%@ page import="com.gloree.utils.*"%>



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

	if (securityProfile == null) {
%>
<jsp:forward page="/jsp/WebSite/startPage.jsp" />
<%
	}

	try {


		int targetRequirementTypeId  = 0;
		int targetFolderId = 0;
		
		try {
			targetRequirementTypeId = Integer.parseInt(request.getParameter("targetRequirementTypeId"));
		}
		catch (Exception e){}
		
		try {
			targetFolderId = Integer.parseInt(request.getParameter("targetFolderId"));
		}
		catch (Exception e){}


		String databaseType = this.getServletContext().getInitParameter("databaseType");
		Project project = (Project) session.getAttribute("project");
		User user = securityProfile.getUser();
		String searchString = request.getParameter("searchString");
		String searchProjects = "";
		ArrayList requirements = new ArrayList();
		// if this was a 'save request', lets save the report and then display the report saved message. 
		if ((searchString != null) && !(searchString.equals(""))) {
			requirements = ReportUtil.getglobalSearchReport(securityProfile, searchProjects, searchString,
					securityProfile.getUser(), databaseType, project.getProjectId(), targetRequirementTypeId, targetFolderId);

		}
%>

<table class='table table-striped' width='100%'>
	
	<tr>
		<td colspan='4' align='center'>
			<div class='alert alert-info'>
				<table class='table '>
					<tr style='border-top:none'>
						<td style='border-top:none'>
						<span class='normalText'>Search String</span>
						</td>
						<td style='border-top:none'>
							<span class='normalText'>
							<input
								type='text' style='width: 300px' name='searchString'
								id='searchString' value='<%=searchString%>'>
								
								 </input> 
								 </span>
						</td>
					</tr>
					<tr style='border-top:none'>
						<td style='border-top:none'>
						<span class='normalText'>
							Object Type </span>
						</td>
						<td style='border-top:none'>
								<span class='normalText'>
								<select
								id='targetRequirementType' name='targetRequirementType'>
									<%
										if (targetRequirementTypeId == 0) {
									%>
									<option value='0'>All RequirementTypes</option>
									<%
										} else {
									%>
									<option value='0' selected>All RequirementTypes</option>
									<%
										}
											ArrayList requirementTypes = project.getMyRequirementTypes();
											Iterator rt = requirementTypes.iterator();
											while (rt.hasNext()) {
												RequirementType requirementType = (RequirementType) rt.next();
												if (requirementType.getRequirementTypeId() == targetRequirementTypeId) {
									%>
									<option value='<%=requirementType.getRequirementTypeId()%>'
										selected>
										<%=requirementType.getRequirementTypeShortName()%> :
										<%=requirementType.getRequirementTypeName()%>
									</option>
									<%
										} else {
									%>
									<option value='<%=requirementType.getRequirementTypeId()%>'>
										<%=requirementType.getRequirementTypeShortName()%> :
										<%=requirementType.getRequirementTypeName()%>
									</option>
									<%
										}
											}
									%>
							</select>
							</span>
						</td>
					</tr>
					<tr>
						<td style='width:100px; border-top:none'>
							<span class='normalText'>Folder 
							</span>
						</td>
						<td style='border-top:none'>	
							<span class='normalText'>
								<select
								id='targetFolder' name='targetFolder'>
									<%
										if (targetFolderId == 0) {
									%>
									<option value='0'>All Folders</option>
									<%
										} else {
									%>
									<option value='0' selected>All Folders</option>
									<%
										}
											ArrayList folders = project.getMyFolders();
											Iterator f = folders.iterator();
											while (f.hasNext()) {
												Folder folder = (Folder)f.next();
												if (folder.getFolderId() == targetFolderId) {
									%>
									<option value='<%=folder.getFolderId() %>'
										selected>
										<%=folder.getFolderPath() %>
									</option>
									<%
										} else {
											%>
											<option value='<%=folder.getFolderId() %>'
												>
												<%=folder.getFolderPath() %>
											</option>
											<%
										}
											}
									%>
							</select></span>
						</td>
					</tr>
					<tr >
						<td colspan='2' style='border-top:none'>
						
						
							<input class='btn btn-primary btn-xs' type='button'
							name='searchNow' id='searchNow' value='Search Current System'
							onclick='findItNowForAProject("current");'> </input>
							
							&nbsp;&nbsp;
							
							<input class='btn btn-primary btn-xs' type='button'
							name='searchNow' id='searchNow' value='Search Historical (Older Version) Data'
							onclick='findItNowForAProject("historical");'> </input>
							
							
							&nbsp;&nbsp;
							
							<!-- 
							<input class='btn btn-primary btn-xs' type='button'
							name='saveReportBtn' id='saveReportBtn' value='Save Report'
							onclick='
									document.getElementById("saveReportDiv").style.display="block";
									document.getElementById("saveReportBtn").style.display="none";
							'> </input>
							-->
							<div id='saveReportDiv' style='display:none'>
								<table class='table' border='0'>
									<tr> 
										<td style='border-top:none; width:100px'>
											<span class='normalText'>
											Report Title</span></td>
										<td style='border-top:none'><span class='normalText'>
											<input type='text' style='width:300px' name='reportTitle' id='reportTitle'></input></span>
										</td>
									</tr>
									<tr>
										<td colspan='2' style='border-top:none'>
											<input type='button' class='btn btn-primary btn-xs' value='Save Report' 
												onclick='saveSearchReport()'>
											<input type='button' class='btn btn-danger btn-xs' value='Cancel' 
												onclick='
													document.getElementById("saveReportDiv").style.display="none";
													document.getElementById("saveReportBtn").style.display="block";
											'>
										</input></td>
									</tr>
								</table>
							</div>
						</td>
					</tr>

				</table>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan='4'>
			<div class='alert alert-danger'>

				The search found
				<%=requirements.size()%>
				results &nbsp;&nbsp;
				
				<a href='/GloreeJava2/servlet/ReportAction?action=exportSearchReportToExcel&targetRequirementTypeId=<%=targetRequirementTypeId%>&targetFolderId=<%=targetFolderId%>&searchString=<%=searchString %>'
						     target='_blank'>
						    <img src="/GloreeJava2/images/ExportExcel16.gif"  border="0"></a>
						    
						    
			 <br></br> Please note that the 'Free Text Search' results
				are based on an index that's built every night at 1:30 AM PST. If
				you have made a lot of changes since then and are not seeing those
				changes reflected in your search results, you may need to re-build
				the project search index. 
				
				<input class='btn btn-danger btn-sm'
					type='button' name='rebuildIndex' id='rebuild'
					value='Rebuild Project Index Now' onclick='rebuildSearchIndex();'>
				</input>
				
			</div>
			<div id='rebuildIndexDiv' style='display: none;'></div>
		</td>

	</tr>
	<tr>
		<td colspan='4'><input class='btn btn-primary btn-sm'
			type='button' name='hideDetails' id='hideDetails'
			style='visibility: hidden' value='Hide Details'
			onclick="
					document.getElementById('hideDetails').style.visibility='hidden';
					document.getElementById('showDetails').style.visibility='visible';
					var elements = document.getElementsByClassName('details');
					for(var i=0, l=elements.length; i<l;
			i++){
						 elements[i].style.display='none';
						}
				">
		</input> <input class='btn btn-primary btn-sm' type='button' name='showDetails'
			id='showDetails' value='Show Details' style='visibility: visible'
			onClick="
					document.getElementById('hideDetails').style.visibility='visible';
					document.getElementById('showDetails').style.visibility='hidden';
					var elements = document.getElementsByClassName('details');
					for(var i=0, l=elements.length; i<l;
			i++){
						 elements[i].style.display='block';
						}
				">
		</input></td>
	</tr>
	<tr>
		<td>Status</td>
		<td>Action</td>
		<td>Object</td>
		<td>Folder</td>
	</tr>
	<%
		String oldProjectPrefix = "";
			String currentProjectPrefix = "";

			int percentageCompletedDriverReqTypeIdForCurrentProject = 0;
			Iterator dR = requirements.iterator();

			while (dR.hasNext()) {
				GlobalRequirement gr = (GlobalRequirement) dR.next();
				Requirement r = gr.getRequirement();
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" + r.getFolderId()))) {
					r.redact();
				}

				String url = ProjectUtil.getURL(request, r.getRequirementId(), "requirement");

				String displayRDInReportDiv = "displayRDInReportDiv" + r.getRequirementId();

				String projectTitle = gr.getProjectName();
				if (projectTitle != null) {
					if (projectTitle.contains("'")) {
						projectTitle = projectTitle.replace("'", " ");
					}
				}
	%>
	<tr
		style="background-color: white; border-width: thin; border-style: solid; border-color: white"
		onmouseover="this.style.background='#E5EBFF';"
		onmouseout="this.style.background='white';">




		<td align='left' valign='top' style='width: 150px'>
			<table class='paddedTable' border='0' bordercolor='white'>
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


					<td width='20px' align='center'><a href="#"
						onclick='displayRequirementDescription(<%=r.getRequirementId()%>
								,"<%=displayRDInReportDiv%>")'
						title='Preview the Requirement'> <img
							src="/GloreeJava2/images/search16.png" border="0"></a></td>







					<%
						if (r.getRequirementTraceTo().length() == 0) {
					%>
					<td
						title='This requirement is an Orphan, i.e does not trace to Requirements upstream '
						width='20px' align='center' style="background-color: lightgray">
						<b><font size='4' color='red'>O</font></b>
					</td>
					<%
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
					%>
					<td
						title='This requirement is a Dangling Requirement i.e does not have downstream traces'
						width='20px' align='center' style="background-color: lightgray">
						<b><font size='4' color='red'>D</font>
						</d>
					</td>
					<%
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

				</tr>
			</table>
		</td>






		<td style='width: 10px'>
			<a href='<%=url %>' class='btn btn-sm btn-primary' style='color:white' target='_blank'>Open</a>
		</td>


		<td style='min-width: 600px'>
			<%
				// lets put spacers here for child requirements.
						String req = r.getRequirementFullTag();
						int start = req.indexOf(".");
						while (start != -1) {
							start = req.indexOf(".", start + 1);
							out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
						}
			%> <%
 	if (r.getDeleted() == 1) {
 %>
			<div class='alert alert-danger'>
				This is a Deleted Object. Available in Recycle Bin and can
					be restored.
			</div> <%
 	} else {
 %> <%
 	}
 %> <%
 	String name = r.getRequirementNameForHTML();
 			name = name.replaceAll("(?i)" + searchString,
 					"<span class='bg-danger'> <b><i><u>" + searchString + "</u></i></b></span>");
 %> <a href="#"
			onclick='
						document.getElementById("contentCenterE").style.display = "none";
						document.getElementById("contentCenterF").style.display = "none";
					displayFolderInExplorer(<%=r.getFolderId()%>);
					displayFolderContentCenterA(<%=r.getFolderId()%>);
					displayRequirement(<%=r.getRequirementId()%>,"My Tasks", <%=r.getFolderId()%>);
				'>
				<%=r.getRequirementFullTag()%> : Ver-<%=r.getVersion()%> : <%=name%>
		</a>

			<div class='alert alert-info'>
				<%
					String description = r.getRequirementDescription();
							description = description.replaceAll("(?i)" + searchString,
									"<span class='bg-danger'> <b><i><u>" + searchString + "</u></i></b></span>");
				%>
				<%=description%>
			</div>
			<div class='details' style='display: none'>


				<%
					String attributes = r.getUserDefinedAttributesFormatted("HTMLNONEMPTY");
							attributes = attributes.replaceAll("(?i)" + searchString,
									"<span class='bg-danger'> <b><i><u>" + searchString + "</u></i></b></span>");
				%>
				<%=attributes%>


				<%
					ArrayList comments = RequirementUtil.getRequirementComments(r.getRequirementId(), databaseType);
				%>
				<div id='commentListDiv<%=r.getRequirementId()%>'>
					<table class='table table-striped' width='100%'>

						<%
							if (comments != null) {
										Iterator c = comments.iterator();

										while (c.hasNext()) {

											Comment commentObject = (Comment) c.next();
						%>
						<tr>
							<td><span class="normalText"> <img
									src="/GloreeJava2/images/comment16.png" border="0"></span></td>
							<td><span class="normalText"> <%
 	String commentNote = commentObject.getHTMLFriendlyCommentNote();
 					commentNote = commentNote.replaceAll("(?i)" + searchString,
 							"<span class='bg-danger'> <b><i><u>" + searchString + "</u></i></b></span>");
 %> <%=commentNote%>

							</span></td>
							<td><span class='normalText'> <%=commentObject.getCommenterEmailId()%>
							</span></td>
							<td><span class='normalText'> <%=commentObject.getCommentDate()%>
							</span></td>
						</tr>
						<%
							}
									}
						%>
					</table>
				</div>


			</div>

		</td>
		<td style='width:300px'><%=r.getFolderPath()%></td>
	</tr>
	<tr>
		<td colspan='4'>
			<div id='<%=displayRDInReportDiv%>'></div>
		</td>
	</tr>

	<%
		}
	%>

</table>


<%
	} catch (Exception e) {

	}
%>




