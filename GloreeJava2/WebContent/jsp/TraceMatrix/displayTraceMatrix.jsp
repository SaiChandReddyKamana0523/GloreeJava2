<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<%
	// authentication only
	String IsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((IsLoggedIn  == null) || (IsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile tMSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)

	boolean isMember = false;
	if (tMSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
		
		User tMUser = tMSecurityProfile.getUser();
		ArrayList folders = project.getMyFolders();

	%>	
	<div id = 'traeMatrixHomeDiv' class='level1Box'>

		<table   width="100%" align="center" class='paddedTable'>
			<tr>
				<td colspan='2'>
					<table class='paddedTable' width='100%'>
						<tr>
						<td align='left' bgcolor='#99CCFF'>				
							<span class='subSectionHeadingText'>
							Tracer
							</span>
						</td>
						</tr>
					</table>
				</td>
			</tr>

			<tr>
				<td colspan='2'>
					<table>
						<tr>
							<td valign='top' >
								<div id = 'fromRequirementDiv' class='level2Box'
								>
									<table class='paddedTable' width='750'>
										<tr>
											<td colspan='2' align='left' >				
												<span class='sectionHeadingText'>
												Trace From 
												</span>
											</td>
										</tr>
										
										<tr>
											<td>
												<span class='normalText'>
												Folder
												</span>
											</td>
											<td align='left' >				
												<span class='normalText'>
													<select name='fromFolderId' id='fromFolderId'
														onChange='fillTraceMatrixFromRequirements();'>
														<option value='-1'>Folder for From Requirements</option>
													<%
														Iterator i = folders.iterator();
														while (i.hasNext()){
															Folder folder = (Folder) i.next();
															
													%>
														<option value='<%=folder.getFolderId()%>'>
															<%=folder.getFolderPath()%>
														</option>
													<%	
														}
													%>
													</select>
												</span>
											</td>
										</tr>
										<tr>
											<td>
												<span class='normalText'>
												Show
												</span>
											</td>
											<td align='left' >				
												<span class='normalText'>
													<input type='text' size='3' name='fromRequirementsRowsPerPage' id='fromRequirementsRowsPerPage' value='200'
													onChange='fillTraceMatrixFromRequirements();'> Requirements
												</span> 
											</td>
										</tr>
										
										<tr>
											<td colspan='2'>
											<hr>
											</td>
										</tr>
										

										<tr>
											<td colspan='2'>
												<div id ='fromRequirementsDiv' class='level2Box'
												style="overflow: auto; width: 700px; height: 1500px; border-left: 1px white solid; border-bottom: 1px gray solid; "
												>
												</div>
											</td>
										</tr>
										
									</table>
								</div>
							</td>




							<td height="100%" style="background-color:#99CCFF" width="1px"></td>




							<td valign='top'>
								<div id = 'toRequirementDiv' class='level2Box'
									>
									<table class='paddedTable' width='700'>
										<tr>
											<td colspan='2' align='left' >				
												<span class='sectionHeadingText'>
												Trace To
												</span>
											</td>
										</tr>
										<tr>
											<td>
												<span class='normalText'>
												Folder
												</span>
											</td>
											<td align='left'>				
												<span class='normalText'>
													<select name='toFolderId' id='toFolderId'
														onChange='fillTraceMatrixToRequirements();'>
														<option value='-1'>Folder for To Requirements</option>
													<%
														i = folders.iterator();
														while (i.hasNext()){
															Folder folder = (Folder) i.next();
													%>
															<option value='<%=folder.getFolderId()%>'>
																<%=folder.getFolderPath()%>
															</option>
													<%	
														}
													%>
													</select>
												</span>
											</td>
										</tr>
										<tr>
											<td>
												<span class='normalText'>
												Show
												</span>
											</td>
											<td align='left' >				
												<span class='normalText'>
													<input type='text' size='3' name='toRequirementsRowsPerPage' id='toRequirementsRowsPerPage' value='200'
													onChange='fillTraceMatrixToRequirements();'> Requirements
												</span> 
											</td>
											
										</tr>
										
										<tr>
											<td colspan='2'>
											<hr>
											</td>
										</tr>
										
										<tr>
											<td colspan='2'>
												<div id ='toRequirementsDiv' class='level2Box'
												style="overflow: auto; width: 700px; height: 1500px; border-left: 1px white solid; border-bottom: 1px gray solid; "
												>
												</div>
											</td>
										</tr>

									</table>
								</div>
							</td>
						</tr>
					</table>
				</td>
			</tr>

						
		</table>
	</div>
<%}%>