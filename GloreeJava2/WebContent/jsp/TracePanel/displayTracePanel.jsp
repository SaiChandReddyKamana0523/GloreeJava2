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
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)

	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
		
		User user = securityProfile.getUser();
		ArrayList folders = project.getMyFolders();

	%>	
	<div id = 'expandTracePanelHomeDiv' class='level1Box' style='display:none'>
		<input type='button' style='width:150px' class='btn btn-sm btn-primary' value='Display Filters'
		onClick='
			document.getElementById("traePanelHomeDiv").style.display="block"
			document.getElementById("expandTracePanelHomeDiv").style.display="none";
		'>
		
		
		<input type='button'  class='btn btn-sm btn-primary'  style='width:250px' 
		name='Go' id='Go' value='  Export to Excel  ' 
		onClick='fillTracePanel(1,1,"export");'>
	</div>
	<div id = 'traePanelHomeDiv' class='level1Box'>

		<table   width="100%" align="center" class='paddedTable'>
			<tr>
				<td colspan='2'>
					<table class='paddedTable' width='100%'>
						<tr>
						<td align='left' bgcolor='#99CCFF'>				
							<span class='subSectionHeadingText'>
							Trace Matrix
							</span>
							<div style="float:right">
								<span title="Trace Matrix Help Video">
								<a target="_blank" href="http://www.youtube.com/watch?v=SB76qCBCGNI&hd=1">
								<img height="20" border="0" src="/GloreeJava2/images/television.png">
								</a>
								</span>
							</div>
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
								<div id = 'fromRequirementDiv' class='level2Box'>
									<table class='paddedTable' >
										<tr>
											<td align='left' >				
												<span class='sectionHeadingText'>
												Trace From (DownStream Objects)
												</span>
											</td>
										</tr>
										
										<tr>
											<td align='left' >				
												<span class='normalText'>
													<select name='fromFolderId' id='fromFolderId'
													onChange='fillFilterDiv("from");'>
														<option value='-1'>Select A Folder</option>
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
											<td align='left'>
												<span class='normalText'>
													&nbsp;&nbsp;Rows per page <input type='text' name='fromPageSize'  id='fromPageSize' size ='3' value='20' >
												</span>
											</td>
										</tr>
										<tr>
											<td align='left'>
												<div id='fromFilterDiv' style='display:none'>
												</div>
											</td>
										</tr>
										
									</table>
								</div>
							</td>





							<td valign='top'>
								<div id = 'toRequirementDiv' >
									<table class='paddedTable' >
										<tr>
											<td align='left' >				
												<span class='sectionHeadingText'>
												Trace To (Up Stream Objects)
												</span>
											</td>
										</tr>
										<tr>
											<td align='left'>				
												<span class='normalText'>
													<select name='toFolderId' id='toFolderId'
													onChange='fillFilterDiv("to");'>
														<option value='-1'>Select A Folder</option>
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
											<td align='left'>
												<span class='normalText'>
													&nbsp;&nbsp;Columns per page <input type='text' name='toPageSize' id='toPageSize' size='3' value='20' >
												</span>
											</td>
										</tr>
										<tr>
											<td align='left'>
												<div id='toFilterDiv' style='display:none'>
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
												
			<tr>
				<td colspan='2' align='left'>
					<input type='button' class='btn btn-sm btn-primary' style='width:250px' 
						name='Go' id='Go' value='  Editable Trace Matrix (Slower) ' 
						onClick='fillTracePanel(1,1, "readWrite");'>
					
	
					&nbsp;&nbsp;&nbsp;
					<input type='button'  class='btn btn-sm btn-primary'  style='width:250px' 
						name='Go' id='Go' value='  Read Only Trace Matrix (Faster)  ' 
						onClick='fillTracePanel(1,1,"readOnly");'>
						
					<hr>	
				</td>
			</tr>
			<tr>
				<td colspan='2' valign='center'>
				</td>
			</tr>						
		</table>
	</div>
	<div id='tracePanelDiv' style='display:none' class='level2Box'></div>
<%}%>