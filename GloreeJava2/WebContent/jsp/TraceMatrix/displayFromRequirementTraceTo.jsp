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
   	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement r = new Requirement(requirementId, databaseType);
	
	if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" + r.getFolderId()))){
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
	else { %>
				<table class='paddedTable'>
					<%
					String status = (String) request.getAttribute("status");
					
					if ((status != null) && (!(status.equals("")))){
						%>
						<tr>
							<td colspan='4'>
								<div class='alert alert-success' id='displayDeleteTraceMesssageDiv<%=requirementId%>' >
									<div style="float:left">
										<a href='#' onClick='
											document.getElementById("displayDeleteTraceMesssageDiv<%=requirementId%>").style.display="none";'>
											Close
										</a>
									</div>
									<span class='normalText'><%=status%></span>								
								</div>
	 						</td>
	 					</tr>
			 			
						
						<%
					}
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
											if (!(securityProfile.getPrivileges().contains("traceFromRequirementsInFolder" + r.getFolderId()))){
												// do nothing as the user can not delete this trace
											%>
												
											<%}
											else {%>
			 									<a href='#' onClick='deleteTraceInTraceMatrix(<%=traceId%>,<%=r.getRequirementId()%>,<%=traceToId%>)'>
			 										<img src="/GloreeJava2/images/delete16.png" border="0" title='Delete this Trace'>
								   				</a>
							   				<%} %>
						   				</td>
	 									<td><span class='normalText' title='<%=requirementName %>'>
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
			<%
	    }
		%>
					
		
	