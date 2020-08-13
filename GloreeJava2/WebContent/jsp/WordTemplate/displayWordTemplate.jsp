<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String displayTemplateIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayTemplateIsLoggedIn == null) || (displayTemplateIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User user = securityProfile.getUser();
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	boolean isAdmin = false;
	if (securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
		isAdmin = true;
	}
	
	if (isMember){

		int folderId = Integer.parseInt(request.getParameter("folderId"));
		Folder folder = new Folder (folderId);
		int templateId = Integer.parseInt(request.getParameter("templateId"));
		WordTemplate wordTemplate = new WordTemplate(templateId, databaseType);
		
	
	%>
	<a name="TopOfDisplayRequirements"></a>
	<div id='templateInfoDiv' class='level1Box'>
			
			
			<fieldset id="templateCore">
				<table  class='paddedTable' width="100%" align="center"  >
					<tr>
						<td colspan='2' align='left' bgcolor='#99CCFF'>				
							<span class='subSectionHeadingText'>
							Word Document Reports : <%=wordTemplate.getTemplateName() %>
							</span>
						</td>
						<td colspan='2' align='right' bgcolor='#99CCFF'>
							<div style='float:right'>
							
								<span title='Creating Requirements from Word Documents Reference Manual'>
								<a href='/GloreeJava2/documentation/help/createRequirementsFromWord.htm' target='_blank'>
								<img src="/GloreeJava2/images/page.png"   border="0">
								</a>	
								</span>
								&nbsp;&nbsp;
								<span title='Regenerating Word Documents with Embedded Requirements Reference Manual'>
								<a href='/GloreeJava2/documentation/help/regeneratingWordDocumentsWithEmbeddedRequirements.htm' target='_blank'>
								<img src="/GloreeJava2/images/page.png"   border="0">
								</a>	
								</span>
								&nbsp;&nbsp;
								<span title='Creating Requirements from Word Documents Help Video'>
								<a target="_blank" href="http://www.youtube.com/watch?v=8Go1026a0-k">
								<img height="20" border="0" src="/GloreeJava2/images/television.png"/>
								</a>
								</span>
								&nbsp;&nbsp;
								<span title='Regenerating Word Documents with embedded Requirements Help Video'>
								<a target="_blank" href="http://www.youtube.com/watch?v=Qt1NuQAtkcs">
								<img height="20" border="0" src="/GloreeJava2/images/television.png"/>
								</a>
								</span>
							</div>							
						</td>
					</tr>									
					<tr>
						<td align='center' colspan='4'>
						
							<div id ='templateActions2' class='level2Box'>
								
								
								<%
								if ((securityProfile.getPrivileges().contains("updateRequirementsInFolder"  + folder.getFolderId())))	{
									// the logged in user has update permissions on this folder.
								%>		        			
		        					<a href='#' onClick='editWordTemplateForm(<%=folderId%>,<%=templateId%>)'>
			        					 Edit Template
		        					 </a>
									<span class='normalText'>&nbsp;|&nbsp;</span>
									
		        					 				
		        				<%}
								else {%>
									<span class='normalText'> 
										<font color='gray'> 
										Edit Template
										</font>
									</span>
									<span class='normalText'>&nbsp;|&nbsp;</span>
								<%
								}
								%>
								
								
								
								
								<%
								if ((securityProfile.getPrivileges().contains("deleteRequirementsInFolder"  + folder.getFolderId())))	{
										// the logged in user has delete permissions on this folder.
								%>		        			
		        					<a href='#' onClick='deleteWordTemplateForm(<%=templateId%>,<%=folderId%>)'>
			        					 Delete Template	  
		        					 </a>
		        					 <span class='normalText'>&nbsp;|&nbsp;</span>
		        					 				
		        				<%}
								else {%>
									<span class='normalText'> 
										<font color='gray'> 
										Delete Template
										</font>
									</span>
									<span class='normalText'>&nbsp;|&nbsp;</span>
									
									
								<%
								}
								%>
								
								
								<%
								if ((securityProfile.getPrivileges().contains("createRequirementsInFolder" 
										+ folder.getFolderId()))){
								%>
									<a href='#' onClick='createRequirementsFromWordTemplateMapForm(<%=templateId%>,<%=folderId%>)'>
			        					 Create Requirements 
			        				 </a>
									<span class='normalText'>&nbsp;|&nbsp;</span>								
								<%
								}
								else {%>
									<span class='normalText'> 
										<font color='gray'> 
										Create Requirements
										<img src="/GloreeJava2/images/puzzle16.gif" border="0">
										</font>
									</span>
									<span class='normalText'>&nbsp;|&nbsp;</span>									
								<%} %>
								
								
								


		        				<a href='/GloreeJava2/servlet/WordTemplateAction?action=downloadTemplate&folderId=<%=folderId%>&templateId=<%=templateId%>'
							     target='_blank'>
		        					 Download Template
		        				</a>
		        				
		        				<span class='normalText'>&nbsp;|&nbsp;</span>
								<a href='#' onClick='generateWordReportForm(<%=templateId%>,<%=folderId%>)'>		        				
		        					 Generate Word Report 
		        				</a>

				        		<% if (project.getEnableTDCS() == 1){
				        		%>
		        					<span class='normalText'>&nbsp;|&nbsp;</span>
								
				        			<span title='Add to TDCS (TraceCloud Document Control System)'>
					        		<a href='#' onClick='generateWordReportFormForTDCS(<%=templateId%>,<%=folderId%>)'>
					        		<img src="/GloreeJava2/images/database_refresh16.png"  border="0"> Push to TDCS</a>
								    </span>
				        		
						       <%} %>
		        				
		        				
							</div>  
							        								
						</td>
					</tr>
								
					<!--  lets get the template details displayed -->
					<tr>
						<td colspan=4>
						<div id = 'templatePromptDiv' class='level2Box'>
						</div>
						</td>
					</tr>
					<tr>
						<td align='left' colspan='4'>				
							<div id ='templateCoreDiv' class='level2Box'>
		        				<table class='paddedTable'>
									<tr>
										<th  >
											<span class='headingText'>
												Created By 
											</span>
										</th>
										<td align='left'>
											<span class='normalText'> 
												<%=wordTemplate.getCreatedBy() %>
											</span>
										</td>
									</tr>
									<tr>
										<th >
											<span class='headingText'>
												Visibility
											</span>
										</th>
										<td align='left'>
											<span class='normalText'> 
												<%=wordTemplate.getTemplateVisibility() %>
											</span>
										</td>
									</tr>

									<tr>
										<th >
											<span class='headingText'>
												Name 
											</span>
										</th>
										<td align='left'>
											<span class='normalText'> 
												<%=wordTemplate.getTemplateName() %>
											</span>
										</td>
									</tr>
									<tr>
										<th >
											<span class='headingText'>
												Description 
											</span>
										</th>
										<td align='left'>
											<span class='normalText'> 
												<%=wordTemplate.getTemplateDescription() %>
											</span>
										</td>
									</tr>
									
							</table>
							</div>
						</td>
					</tr>
				</table>
	
			</fieldset>
	</div>
<%}%>