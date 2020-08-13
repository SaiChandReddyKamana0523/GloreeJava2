<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn  == null) || (isLoggedIn.equals(""))){
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
	else {
		return;
	}
   	String folderIdString = request.getParameter("fromFolderId");
   	int folderId = Integer.parseInt(folderIdString);
	Folder	folder = new Folder(folderId);	

	int fromRequirementsRowsPerPage = 200;
	try {
		fromRequirementsRowsPerPage = Integer.parseInt(request.getParameter("fromRequirementsRowsPerPage"));
	}
	catch(Exception e){
		fromRequirementsRowsPerPage = 200;
	}
	
	ArrayList requirements = folder.getMyRequirements(project.getProjectId(), databaseType);
	
	if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" + folder.getFolderId()))){
	%>
		<table class='paddedTable' >
			<tr>
				<td align='left' colspan='2'>				
					<div class='alert alert-success'>	
					<span class='subSectionHeadingText'>
					You do not have READ permissions on this folder. 
					</span>
					</div>
				</td>
			</tr>
		</table>
	<%}
	else if (requirements.size()==0){
	%>
		<table class='paddedTable' >
			<tr>
				<td align='left' colspan='2'>				
					<div class='alert alert-success'>	
					<span class='subSectionHeadingText'>
					There are no requirements in this folder. 
					</span>
					</div>
				</td>
			</tr>
		</table>
	<%		
	}
	else { 
		String traceCheckBoxDisableFlag = "";
	%>
		<form id='displayFromRequirementsForm' action='#' method='post'>
		<table cellpadding=0 cellspacing=0 width=100%>
		<%
		if (!(securityProfile.getPrivileges().contains("traceFromRequirementsInFolder" + folder.getFolderId()))){
			// this means that the can not Trace Requirements from this folder. So lets disable the checkboxes
			// and print a message.
			traceCheckBoxDisableFlag = "DISABLED=DISABLED";
			
			%>
			<tr>
				<td colspan='4'>
					<div class='alert alert-success'>	
					<span class='subSectionHeadingText'>
					You do not have permissions to create Traces from Requirements in this Folder. 
					</span>
					</div>
				</td></tr>	
			<% 
		}
		if (requirements.size() > fromRequirementsRowsPerPage) { 
			%>
			<tr>
 				<td colspan='4'>
 					<div class='alert alert-success'>
 					<span class='normalText'>
 					<font color='red'><b>This folder has <%=requirements.size()%> Requirements. We are showing the first <%=fromRequirementsRowsPerPage %>.</b></font>
 					</span>
 					</div>
					</td>
				</tr>
			<%
		}	
		%>
			<tr>
				<td width='20px'>
					<div id='selectAllRequirementsDiv'>
						<%
						if (!(securityProfile.getPrivileges().contains("traceFromRequirementsInFolder" + folder.getFolderId()))){
						%>
							<span class='sectionHeadingText'>
								Select 
							</span>
						<%}
						else {%>
							<span class='sectionHeadingText'>
							<a href='#' onClick="
								document.getElementById('deSelectAllRequirementsDiv').style.display = 'block';
								document.getElementById('selectAllRequirementsDiv').style.display = 'none';
								selectAllRequirementInTraceMatrix();">
							Select </a>
							</span>
						<%
						}
						%>
					</div>
					<div id='deSelectAllRequirementsDiv' style="display:none;">
						<span class='sectionHeadingText'>
						<a href='#' onClick="
							document.getElementById('deSelectAllRequirementsDiv').style.display = 'none';
							document.getElementById('selectAllRequirementsDiv').style.display = 'block';
							deSelectAllRequirementInTraceMatrix();">
						Deselect </a>
						</span>
					</div>					
				</td>
				<td width='500px'>
					<span class='normalText'>
					Requirement
					</span>
				</td>
				<td width='40px'>
					<span class='normalText'>
					Change Impact
					</span>
				</td>
				<td width='200px'>
					<span class='normalText'>
					&nbsp;Trace To
					</span>
				</td>
			</tr>
		<%
			int loopSize = 0;
			Iterator rI = requirements.iterator();
			
			while (rI.hasNext()){
				loopSize++;
				
	    		Requirement r = (Requirement) rI.next();
	    		
	    		String singleFromRequirementTraceToDiv = "fromRequirementTraceToDiv" + r.getRequirementId();
	    		String displayRDInFolderDiv = "displayTraceFromRDInFolderDiv" + r.getRequirementId();
	    		String displayCIAInFolderDiv = "displayTraceFromCIAInFolderDiv" + r.getRequirementId();
	    		String displayRequirementInFolderDiv = "displayTraceFromRequirementInFolderDiv" + r.getRequirementId();
	    		
		 %>
 			<tr
 			 onmouseover="
 			 	this.style.background='#E5EBFF';
 				document.getElementById('openFromReqButton<%=r.getRequirementId()%>').style.visibility='visible';
 			" 
 			 onmouseout="
 			 	this.style.background='white';
				document.getElementById('openFromReqButton<%=r.getRequirementId()%>').style.visibility='hidden';
 			"
 			>
				<td width='20px'  valign='top'>
					<input type='checkbox' <%=traceCheckBoxDisableFlag %> name='fromRequirementId' value='<%=r.getRequirementFullTag()%>:<%=r.getRequirementId()%>'>
				</td>
			
				<td width='500px' valign='top'>
					<div id='<%=displayRequirementInFolderDiv %>'>
						<table cellpadding=0 cellspacing=0>
			 				<tr>
			 					<td>		 						
									<%
			 						// lets put spacers here for child requirements.
			 						  String req = r.getRequirementFullTag();
			 					   	  int start = req.indexOf(".");
						    		  while (start != -1) {
						    	            start = req.indexOf(".", start+1);
											out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");				    	         
						  	          }
									%>
								</td>
								<td>
			 						<span class='normalText' >
			 						<a href="#" onclick= 'displayRequirementDescription(<%=r.getRequirementId()%>,"<%=displayRDInFolderDiv%>")'> 
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
			 						<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %> 
			 						</span>
			 						&nbsp;&nbsp;&nbsp;
			 						<div id='openFromReqDiv<%=r.getRequirementId()%>' style='display:block'>
										<span class='normalText'>
				 						<input type='button' id ='openFromReqButton<%=r.getRequirementId()%>' value='Open This Object' style='width:150px;visibility:hidden;'
				 						onClick='
					 						document.getElementById("contentCenterF").style.display = "none";
								
											displayFolderInExplorer(<%=r.getFolderId()%>);
											displayFolderContentCenterA(<%=r.getFolderId() %>);
											displayFolderContentRight(<%=r.getFolderId() %>);		 								
											displayRequirement(<%=r.getRequirementId()%>,"Tracer");
											// since we are showing the requirement, lets expand the layout to show content right
											//layout.getUnitByPosition("left").expand();
				 						'>
				 						</span>
			 						</div>
									</td>
							</tr>
						</table>
					</div>
				</td>
				<td width='40px'  valign='top'>
					<a href="#" onclick= 'displayRequirementCIA(<%=r.getRequirementId()%>,"<%=displayCIAInFolderDiv%>")'> 
						CIA
						</a> 
				</td>
				<td width='200px'  valign='top'>
					<div id='<%=singleFromRequirementTraceToDiv%>'>
						<table class='paddedTable'>
							<%
				    		// lets color code the traceTo and traceFrom values.
				    		ArrayList Traces = RequirementUtil.getTraceTo(r.getRequirementId() );
							Iterator iT = Traces.iterator();
							while (iT.hasNext()){
								String t = (String) iT.next();
				    			String [] traceDetails = t.split(":#:");
				    			String traceId = traceDetails[0];
				    			String traceToId = traceDetails[1];
				    			String traceToFullTag = traceDetails[2];
				    			String suspectString = traceDetails[3];
				    			
				    			// we have a crazy scenario where an empty string after : is not picked up
					    		// so we have to see if we have 5 elements in the array, before referrign to it.
					    		String requirementName = "";
					    		if (traceDetails.length > 4){
					    			requirementName = traceDetails[4];
					    			requirementName = requirementName.replace("'", " ");
					    			
					    		}
					    		String traceDescription = "";
				    			if (traceDetails.length > 5){
				    				traceDescription = traceDetails[5];
				    			}
				    			String projectShortName = "";
				    			if (traceDetails.length > 6){
				    				projectShortName = traceDetails[6];
				    			}
		
				    			int suspect = Integer.parseInt(suspectString);
								%>
								<tr>
			    					<td>
				    					<div id='fromRequirementTraceDiv<%=traceId%>'>
							    			<table class='paddedTable'>
												<%
												String traceColor = "green";
												if (suspect == 1){
													traceColor = "red";
												}
							    				%>
				 								<tr>
				 									<td>
				 									<%
														if (!(securityProfile.getPrivileges().contains("traceFromRequirementsInFolder" + folder.getFolderId()))){
															// do nothing, as the user can not delete the trace.
														%>
															
														<%}
														else {%>
						 									<a href='#' onClick='deleteTraceInTraceMatrix(<%=traceId%>,<%=r.getRequirementId()%>,<%=traceToId%>)'>
						 										<img src="/GloreeJava2/images/delete16.png" border="0" title='Delete this Trace'>
											   				</a>
										   				<%} %>
									   				</td>
				 									<td><span class='normalText' title='<%=requirementName %> '>
				 										<font color='<%=traceColor %>'><%=traceToFullTag%>
				 										</font></span>
				 									</td>
									   				
				 								</tr>
								    		</table>
										</div>
									</td>
								</tr>
							<%
							}
		    			%>
					</table>	
		 			</div>
		 		</td>
		 	</tr>
			<tr>
				<td colspan='4'>
					<div id = '<%=displayCIAInFolderDiv%>' style="display:none; overflow: auto; width: 650px; height: 600px; border-left: 1px white solid; border-bottom: 1px gray solid; "> </div>
					<div id = '<%=displayRDInFolderDiv%>' style="display:none; overflow: auto; width: 650px; height: 600px; border-left: 1px white solid; border-bottom: 1px gray solid; "> </div>
				</td>
			</tr>						
										
			 <%
	    	}
			%>
			</table>
			</form>
			<%
	    }
		%>
					
		
	