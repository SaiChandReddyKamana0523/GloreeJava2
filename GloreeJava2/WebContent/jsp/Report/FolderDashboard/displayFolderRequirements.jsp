<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>


<%
	// authentication only
	String dPSRIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dPSRIsLoggedIn == null) || (dPSRIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");

	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dPSRIsMember = false;
	Project project= (Project) session.getAttribute("project");
	SecurityProfile dPSRSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dPSRSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		dPSRIsMember = true;
	}
	User user = dPSRSecurityProfile.getUser();
%>

<%if (dPSRIsMember){ %>


	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	<%@ page import="java.text.SimpleDateFormat" %>
	<%@ page import="java.util.Calendar" %>
	
	
	
	<%
		
		int folderId = Integer.parseInt(request.getParameter("folderId"));
		Folder folder = new Folder(folderId);
	 
		String requirementTypeShortName = request.getParameter("requirementTypeShortName");
		String dataType = request.getParameter("dataType");
		// NOTE : if dataType == changedAfter then cutOffDate comes with a date string.
		String cutOffDate = request.getParameter("cutOffDate");
	
		////////////////////////////////////////SECURITY//////////////////////////
		//
		// We ensure that the project Id is used as a filter in the Release Metrics Util
		// routine. This project id comes from the user's session, hence the user is 
		// logged in and is a member of this project. 
		//
		////////////////////////////////////////SECURITY//////////////////////////
		
		// get an ArrayList of requirements. 
		ArrayList projectRequirements = ReleaseMetricsUtil.getRequirementsForFolder(dPSRSecurityProfile, 
			folderId,
			requirementTypeShortName, dataType , project.getProjectId(), cutOffDate, user, databaseType);
		// lets set the arraylist of results in session, so that we can re-use them
		// when we export to Excel.
		session.setAttribute("projectRequirements", projectRequirements);
		

	    String folderTitle = folder.getFolderName();
	    if (folderTitle.length()>50){
	    	folderTitle = folderTitle.substring(0,49);
	    }
		String reportTitle = "";
		if ((dataType != null) && (requirementTypeShortName != null)){
			if ((dataType.equals("all")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Folder " + folderTitle ; 
			}
			if ((dataType.equals("changedAfter")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Folder " + folderTitle+"  that changed after "
				+ cutOffDate ;
			}
			if ((dataType.equals("draft")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Draft Requirements in Folder " + folderTitle ; 
			}
			if ((dataType.equals("pending")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Pending Requirements in Folder " + folderTitle ;
			}
			if ((dataType.equals("rejected")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Rejected Requirements in Folder " + folderTitle ; 
			}
			if ((dataType.equals("approved")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Approved Requirements in Folder " + folderTitle ;
			}
			
			
			if ((dataType.equals("pendingBy")) && (requirementTypeShortName.equals("all"))){
				reportTitle = "All Requirements in Folder " + folderTitle+"  Pending Approval   ";
			}
			if ((dataType.equals("rejectedBy")) && (requirementTypeShortName.equals("all"))){
				reportTitle = "All Rejected Requirements in Folder " + folderTitle ; 
			}
			if ((dataType.equals("approvedBy")) && (requirementTypeShortName.equals("all"))){
				reportTitle = "All Approved Requirements in Folder " + folderTitle ;
			}
			
			
			
			
			if ((dataType.equals("completed")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Completed Requirements in Folder " + folderTitle ;
			}
			if ((dataType.equals("incomplete")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All InComplete Requirements in Folder " + folderTitle ;
			}
			if ((dataType.equals("orphan")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Orphan Requirements in Folder " + folderTitle ;
			}
			if ((dataType.equals("dangling")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Dangling Requirements in Folder " + folderTitle ;
			}
			if ((dataType.equals("suspectUpstream")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Folder " + folderTitle+"  with a Suspect Upstream   ";
			}
			if ((dataType.equals("suspectDownstream")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Folder " + folderTitle+"  with a Suspect Downstream  ";
			}
			
			
			
			if ((dataType.equals("failedTesting")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Folder " + folderTitle+"  that have Failed Testing  ";
			}
			if ((dataType.equals("passedTesting")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Folder " + folderTitle+"  that have Passed Testing   ";
			}
			if ((dataType.equals("pendingTesting")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Folder " + folderTitle+"  that are Pending Testing  ";
			}


			// lets build report tiles for links coming from Metrics table. These have a Req Type associated with them.
			if ((dataType.equals("all")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All " + requirementTypeShortName  +" Requirements in Folder " + folderTitle;
			}
			if ((dataType.equals("changedAfter")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All " + requirementTypeShortName  +" Requirements in Folder " + folderTitle;
			}
			if ((dataType.equals("draft")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Draft " + requirementTypeShortName  +" Requirements in Folder " + folderTitle;
			}
			
			if ((dataType.equals("pending")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Pending " + requirementTypeShortName  +" Requirements in Folder " + folderTitle;
			}
			if ((dataType.equals("rejected")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Rejected " + requirementTypeShortName  +" Requirements in Folder " + folderTitle;
			}
			if ((dataType.equals("approved")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Approved " + requirementTypeShortName  +" Requirements in Folder " + folderTitle;
			}
			
			
			
			
			
			
			if ((dataType.equals("completed")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Completed "+ requirementTypeShortName  +"  Requirements in Folder " + folderTitle ;
			}
			if ((dataType.equals("incomplete")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All InComplete "+ requirementTypeShortName  +"  Requirements in Folder " + folderTitle;
			}
			if ((dataType.equals("orphan")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Orphan "+ requirementTypeShortName  +"  Requirements in Folder " + folderTitle;
			}
			if ((dataType.equals("dangling")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Dangling "+ requirementTypeShortName  +"  Requirements in Folder " + folderTitle;
			}
			if ((dataType.equals("suspectUpstream")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All "+ requirementTypeShortName  +"  Requirements in Folder " + folderTitle;
			}
			if ((dataType.equals("suspectDownstream")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All " + requirementTypeShortName  +"  Requirements in Folder " + folderTitle+"  with a Suspect Downstream  ";
			}
			
			
			
			if ((dataType.equals("failedTesting")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All " +  requirementTypeShortName  +"  Requirements in Folder " + folderTitle+"  that have Failed Testing ";
			}
			if ((dataType.equals("passedTesting")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All " +  requirementTypeShortName  +"  Requirements in Folder " + folderTitle+"  that have Passed Testing ";
			}
			if ((dataType.equals("pendingTesting")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All " +  requirementTypeShortName  +"  Requirements in Folder " + folderTitle+"  that are Pending Testing ";
			}
		}


		%>
	 
	
	<div id = 'displayListReportDiv' class='level1Box'>
	<table class='paddedTable' width='100%'>
		<tr>
			<td align="left">				
				<span class="subSectionHeadingText">
					<b><%=reportTitle %></b>
					<a name="TopOfDisplayRequirements"></a>
				</span>
			</td>		
		</tr>

		<tr>
			<td>
			
				<div id ='requirementActions' class='level2Box'>
				<table align='left' width='100%'>
					<tr>
						<td class='icons'>
						    <a href='/GloreeJava2/servlet/ReportAction?action=exportProjectMetricsReportToExcel'
						     target='_blank' title='Download this as an Excel File'>
						    <img src="/GloreeJava2/images/ExportExcel16.gif"  border="0"></a>
			    		
							&nbsp;&nbsp;
		        			<span title='Email this data file as an attachment'>
			        		<a href='#' onClick='displayEmailExcelDiv("folderRequirements")'>
			        		<img src="/GloreeJava2/images/email16.png"  border="0"></a>
						    </span>
		        		</td>	
						<td align='right'>
							<a href='#' onClick='createNewListReport("<%=folder.getFolderId() %>"); '>Advanced Filters</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</td>						
			    	</tr>
				</table>
				</div>
			</td>
		</tr>		
		<tr>
       		<td >
       			<div id='emailExcelDiv' style="display:none;" class='alert alert-success'>
				</div>
       		</td>
       	</tr>			

		<tr>
			<td>
			
				<div id ='reportData' class='level2Box'>
				<table id = "Report">				
	
					<%
					    if (projectRequirements != null){
					    	if (projectRequirements.size() ==0){
				   	%>
						    		<tr>
						    			<td colspan='7'>
						    				<div class='alert alert-success'>
						    					<span class='normalText'> There are no requirements that match this criteria.
						    					</span>
						    				</div>
						    			</td>
						    		</tr>
						    	
				   	<%
						    	}
					    	Iterator i = projectRequirements.iterator();
					    	int j = 0;
					    	String cellStyle = "normalTableCell";
					    	while ( i.hasNext() ) {
					    		Requirement r = (Requirement) i.next();
					    		j++;
					    		if (j > 400) {
					 %>
						    		<tr>
						    			<td colspan='7'>
						    				<div class='alert alert-success'>
						    					<span class='normalText'> We are showing the first 400 requirements. To download the
						    					entire Requirement Set please click 
						    					<a href='/GloreeJava2/servlet/ReportAction?action=exportProjectMetricsReportToExcel' target='_blank'>
							    				<img src="/GloreeJava2/images/ExportExcel16.gif"  border="0"></a>
						    					</span>
						    				</div>
						    			</td>
						    		</tr>
					 <%
					 			break;
					    		}
					    		// for the first row, print the header and user defined columns etc..
					    		if (j == 1){
					 %>
									<tr>
										<td>
										</td>
										<td class='tableHeader' width='350'>
											<span class='sectionHeadingText'>
											Requirement 
											</span>
										</td>
										<td class='tableHeader'>
											<span class='sectionHeadingText'>
											Owner
											</span>
										 </td>
										
										<td class='tableHeader'> 
											<span class='sectionHeadingText'>
											Priority
											</span>
										</td>
										<td class='tableHeader'> 
											<span class='sectionHeadingText'>
											Approval Status 
											</span>
										</td>
										<td class='tableHeader'> 
											<span class='sectionHeadingText'>
											Testing Status 
											</span>
										</td>
										
									</tr>				 
					<%
					   		 			
					    		}
					    		
					    		// Now for each row in the array list, print the data out.
					    		if ((j%2) == 0){
					    			cellStyle = "normalTableCell";
					    		}
					    		else {
					    			cellStyle = "normalTableCell";	
					    		}
					    		
					    		
					    		String displayRDInReportDiv = "displayRDInReportDiv" + r.getRequirementId();
					 %>
				 				<tr  style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
								
								
									<td  align='left' valign='top' class='<%=cellStyle%>' width='150px'>
										<table class='paddedTable' border='1' bordercolor='white' >
											<tr>
											
												<td width='20px' align='center'>
													<%if (!(r.getRequirementLockedBy().equals(""))){
													// this requirement is locked. so lets display a lock icon.
													%>
														
														<span class='normalText' title='Requirement locked by <%=r.getRequirementLockedBy()%>'> 
															<img src="/GloreeJava2/images/lock16.png" border="0"> 
														</span>
														
													<%
													}
													else {
													%>
														<span class='normalText' title='Requirement not locked'> 
															&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
														</span>
														
													
													<%
													}
													%>
												</td>

												
												<td width='20px' align='center'>
													<a href="#" onclick= 'displayRequirementDescription(<%=r.getRequirementId()%>
														,"<%=displayRDInReportDiv%>")' title='Preview the Requirement'> 
													<img src="/GloreeJava2/images/search16.png"  border="0">
													</a>
													
												</td>
												
											

												

												
												
												
												<% if (r.getRequirementTraceTo().length() == 0 ) { %>
													<td title='This requirement is an Orphan, i.e does not trace to Requirements upstream ' width='20px' align='center' style="background-color:lightgray">
														<b><font size='4' color='red'>O</font></b>
													</td>
												<%}
												else if(r.getRequirementTraceTo().contains("(s)")) { %>
													<td title='There is a suspect upstream trace' width='20px' align='center' style="background-color:pink">
														<img src="/GloreeJava2/images/arrow_up.png"> 
													</td>
												<%}
												else { %>
													<td title='All upstream traces are clear' width='20px' align='center' style="background-color:lightgreen">
														<img src="/GloreeJava2/images/arrow_up.png"> 
													</td>
												
												<%} %>
												
												
												
												<% if (r.getRequirementTraceFrom().length() == 0 ) { %>
													<td title='This requirement is a Dangling Requirement i.e does not have downstream traces' width='20px' align='center' style="background-color:lightgray">
														<b><font  size='4' color='red'>D</font></d> 
													</td>
												<%}
													else if(r.getRequirementTraceFrom().contains("(s)")) { %>
													<td title='There is a suspect downstream trace' width='20px' align='center' style="background-color:pink">
														<img src="/GloreeJava2/images/arrow_down.png"> 
													</td>
												<%}
													else {%>
													<td title='All downstream traces are clear' width='20px' align='center' style="background-color:lightgreen">
														<img src="/GloreeJava2/images/arrow_down.png"> 
													</td>
												
												<%} %>
												
												
												
												<%if (r.getTestingStatus().equals("Pending")){ %>		
													<td title='Testing is Pending' width='20px' align='center' style="background-color:lightgray">
														&nbsp;&nbsp;&nbsp;&nbsp;
													</td>
												<%}
												else if (r.getTestingStatus().equals("Pass")){ %>
													<td title='Testing Passed' width='20px' align='center' style="background-color:lightgreen">
														<img src="/GloreeJava2/images/testingPassed.png"> 
													</td>
												<%}
												else {%>
													<td title='Testing Failed' width='20px' align='center' style="background-color:pink">
														<img src="/GloreeJava2/images/testingFailed.png"> 
													</td>
												
												<%} %>
												
												
												
												
												
												<%if (r.getRequirementPctComplete() == 100){%>
												
													<td title='Percent of work completed'  align='center' style="background-color:lightgreen">
														<%=r.getRequirementPctComplete()%>%
													</td>
												<%}
												else if (r.getRequirementPctComplete() == 0){ %>
													<td title='Percent of work completed'  align='center' style="background-color:lightgray">
														&nbsp;&nbsp;&nbsp;<%=r.getRequirementPctComplete()%>%
													</td>
												
												<%}
												else {%>
													<td title='Percent of work completed'  align='center'style="background-color:pink">
														&nbsp;&nbsp;<%=r.getRequirementPctComplete()%>%
													</td>
												<%}%>												
												
											</tr>
										</table>
									</td>
								
								





								
								
								
								
								
								
							 		<td class='<%=cellStyle%>'>
				 						<%
				 						// lets put spacers here for child requirements.
				 						  String req = r.getRequirementFullTag();
				 					   	  int start = req.indexOf(".");
							    		  while (start != -1) {
							    	            start = req.indexOf(".", start+1);
												out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");				    	         
							  	          }
				 						%>							 		
							 		
							 			<span>
							 		
		 								
		 								
										&nbsp;
										
		 								<a href="#" 
		 								onClick='
		 									document.getElementById("contentCenterF").style.display="none";
		 									displayRequirement(<%=r.getRequirementId()%>,"List Folder Contents");
											'>
		 								<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %></a> 
		 								</span>
							 		</td>
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<%=r.getRequirementOwner()%>
							 			</span>
							 		</td>
							 		
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<%=r.getRequirementPriority()%>
							 			</span>
							 		</td>
										<% if (r.getApprovalStatus().equals("Draft")){ %>
											<td bgcolor='#FFFF66''>
												<span class='normalText'>
													<%=r.getApprovalStatus() %>
												</span>
											</td>										
										<%} %>
										<% if (r.getApprovalStatus().equals("In Approval WorkFlow")){ %>
											<td bgcolor='#99ccff'>
												<span class='normalText'>												
													<%=r.getApprovalStatus() %>
												</span>
											</td>
										<%} %>
										<% if (r.getApprovalStatus().equals("Approved")){ %>
											<td bgcolor='#CCFF99''>
												<span class='normalText'>
													<%=r.getApprovalStatus()%>
												</span>
											</td>
										<%} %>
										<% if (r.getApprovalStatus().equals("Rejected")){ %>
											<td bgcolor='#FFA3AF'>
												<span class='normalText'>
													<%=r.getApprovalStatus()%>
												</span>
											</td>
										<%} %>		
										<% if (r.getTestingStatus().equals("Pending")){ %>
											<td bgcolor='#FFFF66'>
												<span class='normalText'>
													<%=r.getTestingStatus() %>
												</span>
											</td>										
										<%} %>
										<% if (r.getTestingStatus().equals("Pass")){ %>
											<td bgcolor='#CCFF99'>
												<span class='normalText'>												
													<%=r.getTestingStatus() %>
												</span>
											</td>
										<%} %>
										<% if (r.getTestingStatus().equals("Fail")){ %>
											<td bgcolor='#FFA3AF'>
												<span class='normalText'>
													<%=r.getTestingStatus()%>
												</span>
											</td>
										<%} %>
															 		
							 		
				 				</tr>
				 				<tr>
				 					<td  class='<%=cellStyle%>'  colspan='6'>
				 						<div id = '<%=displayRDInReportDiv%>'> </div>
				 					</td>
				 				</tr>				 				

					 <%
					    	}
					    }
					%>
				
				</table>
				</div>
			</td>
		</tr>
	</table>
	</div>
<%}%>