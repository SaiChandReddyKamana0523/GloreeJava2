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
   	String folderIdString = request.getParameter("toFolderId");
   	int folderId = Integer.parseInt(folderIdString);
	Folder	folder = new Folder(folderId);	

	int toRequirementsRowsPerPage = 200;
	try {
		toRequirementsRowsPerPage = Integer.parseInt(request.getParameter("toRequirementsRowsPerPage"));
	}
	catch(Exception e){
		toRequirementsRowsPerPage = 200;
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
	else if (requirements.size() == 0){
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
	<%}
	
	if (requirements.size() > toRequirementsRowsPerPage) { 
		%>
		<tr>
				<td colspan='4'>
					<div class='alert alert-success'>
					<span class='normalText'>
					<font color='red'><b>This folder has <%=requirements.size()%> Requirements. We are showing the first <%=toRequirementsRowsPerPage %>.</b></font>
					</span>
					</div>
				</td>
			</tr>
		<%
	}	
	
	else { 
		String traceButtonDisableFlag = "";
	%>
		<table cellpadding=0 cellspacing=0>
		<%
		if (!(securityProfile.getPrivileges().contains("traceToRequirementsInFolder" + folder.getFolderId()))){
			// this means that the can not Trace Requirements from this folder. So lets disable the checkboxes
			// and print a message.
			traceButtonDisableFlag = "DISABLED=DISABLED";
			
			%>
			<tr>
				<td colspan='4'>
					<div class='alert alert-success'>	
					<span class='subSectionHeadingText'>
					You do not have permissions to create Traces to Requirements in this Folder. 
					</span>
					</div>
				</td></tr>	
			<% 
		}
		%>
		
			<tr>
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
					Trace From
					</span>
				</td>
				<td width='100px'>
					<span class='normalText'>
					Action
					</span>
				</td>
			</tr>
		
	<%
		int loopSize = 0;
		Iterator rI = requirements.iterator();			
		while (rI.hasNext()){
			loopSize++;
			
    		Requirement r = (Requirement) rI.next();
			String singleToRequirementDiv = "toRequirementDiv" + r.getRequirementId();
			String singleToRequirementTraceFromDiv = "toRequirementTraceFromDiv" + r.getRequirementId();
    		String displayRDInFolderDiv = "displayTraceToRDInFolderDiv" + r.getRequirementId();
    		String displayCIAInFolderDiv = "displayTraceToCIAInFolderDiv" + r.getRequirementId();
    		String displayRequirementInFolderDiv = "displayTraceToRequirementInFolderDiv" + r.getRequirementId();
			String displayTraceResultDiv = "createTracesResultDiv" + r.getRequirementId();    		
    		
			String displayCreateTracesMessageDiv = "displayCreateTracesMessageDiv" + r.getRequirementId();

	 %>

	 			<tr
	 			 	onmouseover="
	 			 		this.style.background='#E5EBFF';
	 					document.getElementById('openToReqButton<%=r.getRequirementId()%>').style.visibility='visible';
	 					document.getElementById('CreateTraces<%=r.getRequirementId()%>').style.visibility='visible';
	 				" 
 			 		onmouseout="
 			 			this.style.background='white';
 			 			document.getElementById('openToReqButton<%=r.getRequirementId()%>').style.visibility='hidden';
 			 			document.getElementById('CreateTraces<%=r.getRequirementId()%>').style.visibility='hidden';
	 				"
	 			>
	 				<td width='500px'  valign='top'>
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
			 						<%=r.getRequirementFullTag()%> :  <%=r.getRequirementNameForHTML() %></a> 
			 						</span>
			 						&nbsp;&nbsp;&nbsp;
			 						<div id='openToReqDiv<%=r.getRequirementId()%>' style='display:block'>
										<span class='normalText'>
				 						<input type='button' id ='openToReqButton<%=r.getRequirementId()%>' value='Open This Object' style='width:150px;visibility:hidden;'
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
						<div id='<%=singleToRequirementTraceFromDiv %>'>
						<table class='paddedTable'>
	 														<%
			    		// lets color code the traceTo and traceFrom values.
			    		String[] traces = r.getRequirementTraceFrom().split(",");
	 					for (int i=0;i<traces.length;i++){
			    			if (traces[i].contains("(s)")){
			    				%>
			    				<tr><td><span class='normalText'><font color='red'><%=traces[i]%></font></span></td></tr>
			    				<%
			    			}
			    			else {
			    				%>
			    				<tr><td><span class='normalText'><font color='green'><%=traces[i]%></font></span></td></tr>
			    				<%
			    			}
			    		}
			    		%>
			    		</table>
						</div>
					</td > 
					<td width='100px'  valign='top'>
						<span class='normalText'>
							<input type='button' <%=traceButtonDisableFlag%> name='CreateTraces<%=r.getRequirementId()%>' 
								id ='CreateTraces<%=r.getRequirementId()%>' 
								value =' Trace To This ' 
								Style = 'width:150px; visibility:hidden;'
								onClick='createTraceMatrixTraces(<%=r.getRequirementId() %>);'></input>
						</span>
					</td>
				</tr>
					<tr>
						<td colspan='4'>
							<div id='<%=displayCreateTracesMessageDiv%>' style='display:none'></div>
						</td>
					</tr>
				<tr>
					<td colspan='4'>
						<div id = '<%=displayTraceResultDiv%>' 
						style="display:none; overflow: auto; width: 650px; height: 600px; border-left: 1px white solid; border-bottom: 1px gray solid; "
						> </div>
						<div id = '<%=displayCIAInFolderDiv%>' 
						style="display:none; overflow: auto; width: 650px; height: 600px; border-left: 1px white solid; border-bottom: 1px gray solid; "
						> </div>
						<div id = '<%=displayRDInFolderDiv%>' 
						style="display:none; overflow: auto; width: 650px; height: 600px; border-left: 1px white solid; border-bottom: 1px gray solid; "
						> </div>
						
					</td>
				</tr>

			 <%
	    	}

			%>
		

			</table>
			<%
	    }
		%>
					
		
	