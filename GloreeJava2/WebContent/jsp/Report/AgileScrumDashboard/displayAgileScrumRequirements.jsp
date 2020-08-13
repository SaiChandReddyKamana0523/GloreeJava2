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
		// we use the same subroutines in releseMetricsUtil, but use releaseId ==0  to indicate project level
		// crunching.
		int releaseId = 0;

	
		String requirementTypeShortName = request.getParameter("requirementTypeShortName");
		String dataType = request.getParameter("dataType");
		int sprintId = Integer.parseInt(request.getParameter("sprintId"));
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
		ArrayList projectRequirements = null;
		String defectStatusGroup = "";

		projectRequirements = ReleaseMetricsUtil.getRequirementsForSprint(dPSRSecurityProfile, 
		sprintId,
		requirementTypeShortName, dataType , project.getProjectId(), cutOffDate, user, databaseType);
		
		
		// lets set the arraylist of results in session, so that we can re-use them
		// when we export to Excel.
		session.setAttribute("projectRequirements", projectRequirements);
		Date now = new Date();
	    SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm zzz"); 
	    String parsedDate = formatter.format(now); 
	    

		String reportTitle = "";
		if ((dataType != null) && (requirementTypeShortName != null)){
			if ((dataType.equals("all")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Sprint as of " 
				+ parsedDate ;
			}
			if ((dataType.equals("changedAfter")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Sprint that changed after "
				+ cutOffDate + " as of " + parsedDate;
			}
			if ((dataType.equals("draft")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Draft Requirements in Sprint as of "
				+ parsedDate ;
			}
			if ((dataType.equals("pending")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Pending Requirements in Sprint as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("rejected")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Rejected Requirements in Sprint as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("approved")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Approved Requirements in Sprint as of  "
				+ parsedDate ;
			}
			
			
			if ((dataType.equals("pendingBy")) && (requirementTypeShortName.equals("all"))){
				reportTitle = "All Requirements in Sprint Pending Approval as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("rejectedBy")) && (requirementTypeShortName.equals("all"))){
				reportTitle = "All Rejected Requirements in Sprint as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("approvedBy")) && (requirementTypeShortName.equals("all"))){
				reportTitle = "All Approved Requirements in Sprint as of  "
				+ parsedDate ;
			}
			
			
			
			
			if ((dataType.equals("defectStatusGroup")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All " + "Defects in Project with Status " + defectStatusGroup 
				+" as of  " + parsedDate ;
			}
			
			
			if ((dataType.equals("completed")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Completed Requirements in Sprint as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("incomplete")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All InComplete Requirements in Sprint as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("orphan")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Orphan Requirements in Sprint as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("dangling")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Dangling Requirements in Sprint  as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("suspectUpstream")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Sprint with a Suspect Upstream as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("suspectDownstream")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Sprint with a Suspect Downstream as of  "
				+ parsedDate ;
			}
			
			
			
			if ((dataType.equals("failedTesting")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Sprint that have Failed Testing as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("passedTesting")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Sprint that have Passed Testing as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("pendingTesting")) && (requirementTypeShortName.equals("all"))){
				reportTitle = " All Requirements in Sprint that are Pending Testing as of  "
				+ parsedDate ;
			}


			// lets build report tiles for links coming from Metrics table. These have a Req Type associated with them.
			if ((dataType.equals("all")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All " + requirementTypeShortName  +" Requirements in Sprint as of " 
				+ parsedDate ;
			}
			if ((dataType.equals("changedAfter")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All " + requirementTypeShortName  +" Requirements in Sprint that changed after"
				+ cutOffDate + " as of " + parsedDate;
			}
			if ((dataType.equals("draft")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Draft " + requirementTypeShortName  +" Requirements in Sprint as of "
				+ parsedDate ;
			}
			
			if ((dataType.equals("pending")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Pending " + requirementTypeShortName  +" Requirements in Sprint as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("rejected")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Rejected " + requirementTypeShortName  +" Requirements in Sprint  as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("approved")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Approved " + requirementTypeShortName  +" Requirements in Sprint as of  "
				+ parsedDate ;
			}
			
			
			
			
			
			
			if ((dataType.equals("completed")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Completed "+ requirementTypeShortName  +"  Requirements in Sprint as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("incomplete")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All InComplete "+ requirementTypeShortName  +"  Requirements in Sprint as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("orphan")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Orphan "+ requirementTypeShortName  +"  Requirements in Sprint as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("dangling")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All Dangling "+ requirementTypeShortName  +"  Requirements in Sprint as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("suspectUpstream")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All "+ requirementTypeShortName  +"  Requirements in Sprint with a Suspect Upstream as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("suspectDownstream")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All " + requirementTypeShortName  +"  Requirements in Sprint with a Suspect Downstream as of  "
				+ parsedDate ;
			}
			
			
			
			if ((dataType.equals("failedTesting")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All " +  requirementTypeShortName  +"  Requirements in Sprint that have Failed Testing as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("passedTesting")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All " +  requirementTypeShortName  +"  Requirements in Sprint that have Passed Testing as of  "
				+ parsedDate ;
			}
			if ((dataType.equals("pendingTesting")) && (!requirementTypeShortName.equals("all"))){
				reportTitle = " All " +  requirementTypeShortName  +"  Requirements in Sprint that are Pending Testing as of  "
				+ parsedDate ;
			}
		}
		
	%>
	 
	
	<div id = 'displayListReportDiv' class='level1Box'>
	<table class='paddedTable' width='100%'>
		<tr>
			<td bgcolor="#99ccff" align="left">				
				<span class="subSectionHeadingText">
					<%=reportTitle %> 
					<a name="TopOfDisplayRequirements"></a>
				</span>
			</td>		
		</tr>

		<tr>
			<td>
			
				<div id ='requirementActions' class='level2Box'>
				<table align='left'>
					<tr>
						<td class='icons'>
						    <a href='/GloreeJava2/servlet/ReportAction?action=exportProjectMetricsReportToExcel'
						     target='_blank'>
						    <img src="/GloreeJava2/images/ExportExcel16.gif"  border="0"></a>
			    		</td>
		        		<td class='icons'>
		        			<span title='Email this data file as an attachment'>
			        		<a href='#' onClick='displayEmailExcelDiv("projectRequirements")'>
			        		<img src="/GloreeJava2/images/email16.png"  border="0"></a>
						    </span>
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
											Percent Complete
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
										<td class='tableHeader'> 
											<span class='sectionHeadingText'>
											Folder
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
					    			cellStyle = "altTableCell";	
					    		}
					    		
					    		
					    		// lets get the folderId  based on whether the req is deleted or not.
					    	 	String folderId = "";
					    		if (r.getDeleted() == 0 ){
					    			// not deleted. Hence folderId is the realfolder id.
					    			folderId = Integer.toString(r.getFolderId());
					    		}
					    		else {
					    			// this is a deleted Req. here we create a virtual folderid
					    			// which is -1ReqTypeId.
					    			folderId = "-1:" + r.getRequirementTypeId();
					    		}
					    		
					    		String displayRDInReportDiv = "displayRDInReportDiv" + r.getRequirementId();
					 %>
				 				<tr>
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
							 			<a href="#" onclick= 'displayRequirementDescription(<%=r.getRequirementId()%>
							 				,"<%=displayRDInReportDiv%>")'> 
		 								<img src="/GloreeJava2/images/search16.png"  border="0">
		 								</a>
		 								
		 								<%if (!(r.getRequirementLockedBy().equals(""))){
											// this requirement is locked. so lets display a lock icon.
										%>
											<span class='normalText' title='Requirement locked by <%=r.getRequirementLockedBy()%>'> 
					        					<img src="/GloreeJava2/images/lock16.png" border="0"> 
					        				</span>	
										<%
										}
		 								%>
		 								&nbsp;
		 								
		 								<a href="#" 
		 								onClick='
		 									displayFolderInExplorer("<%=folderId %>");
		 									displayFolderContentCenterA("<%=folderId %>");
		 									displayRequirement(<%=r.getRequirementId()%>);'>
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
							 			<%=r.getRequirementPctComplete()%> %
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
										
										
										
																 		
							 		<td class='<%=cellStyle%>'>
							 			<span class='normalText'>
							 			<%=r.getFolderPath()%>
							 			</span>
							 		</td>
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