
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
		ArrayList requirementVersions = new ArrayList();
		if ((searchString != null) && !(searchString.equals(""))) {
			requirementVersions = ReportUtil.getOldVersionSearchReport(securityProfile, searchProjects, searchString,
					securityProfile.getUser(), databaseType, project.getProjectId(), targetRequirementTypeId, targetFolderId);

		}
%>

<table class='table table-striped' width='100%'>
	<!-- 
		<tr>
			<td colspan='8' align='center'>
				<span class='normalText'><b>Search String "<%=searchString%>"</b></span>
			</td>
		</tr>
		
		<tr>
			<td colspan='8' align='right'>
				<input type='button' class='btn btn-danger btn-sm'  onclick='displayMyTasksForAProject();' value=' Return to My Tasks '></input>
			</td>
		</tr>
	-->
	<tr>
		<td colspan='4' align='center'>
			<div class='alert alert-success'>
				<table class='table table-striped'>
					<tr>
						<td>
						<span class='normalText'>Search String
						</td>
						<td>
						<input
								type='text' style='width: 300px' name='searchString'
								id='searchString' value='<%=searchString%>'>
								
								 </input> 
						</td>
					</tr>
					<tr>
						<td>
							Object Type 
						</td>
						<td>
								
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
						</td>
					</tr>
					<tr>
						<td>
							Folder 
						</td>
						<td>	
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
							</select>
						</td>
					</tr>
					<tr>
						<td>
						
						
							<input class='btn btn-primary btn-sm' type='button'
							name='searchNow' id='searchNow' value='Search Current System'
							onclick='findItNowForAProject("current");'> </input>
							
							&nbsp;&nbsp;&nbsp;&nbsp;
							
							<input class='btn btn-primary btn-sm' type='button'
							name='searchNow' id='searchNow' value='Search Historical (Older Version) Data'
							onclick='findItNowForAProject("historical");'> </input>
							
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
				<%=requirementVersions.size()%>
				results <br></br> Please note that these results are from OLDER VERSIONS of the requirements.
			</div>
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
		<td>Folder</td>
	</tr>
	<%
		String oldProjectPrefix = "";
			String currentProjectPrefix = "";

			int percentageCompletedDriverReqTypeIdForCurrentProject = 0;
			Iterator dR = requirementVersions.iterator();

			while (dR.hasNext()) {
				RequirementVersion v = (RequirementVersion) dR.next();
				Requirement r = new Requirement(v.getRequirementId(), databaseType);

				String url = ProjectUtil.getURL(request, v.getRequirementId(), "requirement");

				String displayRDInReportDiv = "displayRDInReportDiv" + r.getRequirementId();

				
	%>
	<tr
		style="background-color: white; border-width: thin; border-style: solid; border-color: white"
		onmouseover="this.style.background='#E5EBFF';"
		onmouseout="this.style.background='white';">








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
 	String name = v.getVersionName() ;
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
				<%=r.getRequirementFullTag()%> : Ver-<%=v.getVersion() %> : <%=name%>
		</a>

			<div class='alert alert-info'>
				<%
					String description = v.getVersionDescription();
							description = description.replaceAll("(?i)" + searchString,
									"<span class='bg-danger'> <b><i><u>" + searchString + "</u></i></b></span>");
				%>
				<%=description%>
			</div>
			<div class='details' style='display: none'>


				<%
					String attributes = v.getVersionUserDefinedAttributes();
							attributes = attributes.replaceAll("(?i)" + searchString,
									"<span class='bg-danger'> <b><i><u>" + searchString + "</u></i></b></span>");
				%>
				<%=attributes%>



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




