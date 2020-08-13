<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String dPCLIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dPCLIsLoggedIn == null) || (dPCLIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	// authorizatoin 
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dPCLIsMember = false;
	SecurityProfile dPCLSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dPCLSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		dPCLIsMember = true;
	}
	%>

<%if(dPCLIsMember){ 
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);

	///////////////////////////////SECURITY CODE ////////////////////////////
	// if the requirement worked on, doesn't belong to the project the user is 
	// currently logged into, then a user logged into project x is trying to 
	// hack into a req in project y by useing requirementId parameter.
	if (requirement.getProjectId() != project.getProjectId()) {
		return;
	}
	
	String timeZone = project.getProjectTimeZone();
	Double gmtDelta = project.getProjectGMTDelta();
	
	///////////////////////////////SECURITY CODE ////////////////////////////

	
	
%>
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<div id='displayChangeLog' class='alert alert-success'> 
	<b>Requirement Change Log</b>
		<table class='paddedTable' width='100%' >
			<tr>
				<td align='right'>
					<a href='#' 
						onclick='document.getElementById("displayChangeLog").style.display = "none"'
					> Close </a>
				</td>
			</tr>
			
			<%
				//ArrayList requirementChangeLog = RequirementUtil.getRequirementChangeLog(requirementId, databaseType);
			ArrayList requirementChangeLog = RequirementUtil.getRequirementChangeLogGMTDelta(requirementId, databaseType, gmtDelta);
		        
			if (requirementChangeLog != null){
			    	Iterator i = requirementChangeLog.iterator();
			    	int j = 0;
			    	
			    	while ( i.hasNext() ) {
			    		j++;
				    		
			    		String log = (String) i.next();
			    		String[] logAttribs = log.split(":--:");
			    		String changedOn = logAttribs[0];
			    		String changedBy = logAttribs[1];
			    		String changeDesc = logAttribs[2];
			    		String changeType = "";
			    		String change = "";
			    		try {
				    		if ((changeDesc!=null) && (changeDesc.contains(":"))){
				    			String [] cd = changeDesc.split(":");
				    			if (cd.length > 0){
				    				changeType = cd[0] ;
				    			}
				    			if (cd.length > 1){
				    				change = cd[1];
				    				
				    			}
				    		}
				    		else {
				    			change = changeDesc;
				    		}
			    		}
			    		catch (Exception e){
			    			e.printStackTrace();
			    			change = changeDesc;
			    		}
			    		%>
						<tr
							onMouseOver=  "this.style.background='#E5EBFF';" 
							onMouseOut=  "this.style.background='white';"
						>
							<td>
								<div>
								<table width='100%'>
								 	<tr>
								 		<td width='100'> 
											<span class='headingText'>
											<b>Changed On</b>
											</span>
										</td>
								 		<td >
								 			<span class='normalText'">
								 			<%=changedOn%>(<%=timeZone %>)
								 			</span>
								 		</td>
								 	</tr>
								 	<tr>
								 		<td width='100' > 
											<span class='headingText'>
											<b>Changed By</b>
											</span>
										</td>
								 		<td >
								 			<span class='normalText'">
								 			<%=changedBy%>
								 			</span>
								 		</td>
								 	</tr>
								 	<tr>	
								 		<td width='100'> 
											<span class='headingText'>
											<b>Change </b>
											</span>
										</td>	
								 		<td >
								 			<span class='normalText'>
								 				<font color='red'>
								 					<b><%=changeType %> </b> 
								 				</font>
								 				
								 			</span>
								 			<span class='normalText'>
								 			<% 
								 			String formattedChange = logAttribs[2];
								 			formattedChange = formattedChange.replace(changeType,"");
								 			formattedChange = formattedChange.replaceAll(":##:","<br>"); 
								 			
								 			if (changeType.equals("")){
								 				formattedChange = "<font color='red'><b> " + formattedChange + "</b></font>"; 
								 			}
								 			%>
								 			<%=formattedChange%>
								 			</span>
								 		</td>		
								 	</tr>
								 	<tr>
								 		<td colspan='2'>
								 		<hr>
								 		</td>
								 	</tr>
					 			</table>
					 		</div>
					 		</td>
					 	</tr>
						 <%
	    			}
	    		}
			%>
		</table>
	</div>
<%}%>