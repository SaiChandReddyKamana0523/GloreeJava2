<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>


<%
	// authentication only
	String pLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((pLoggedIn == null) || (pLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean pMember = false;
	Project project= (Project) session.getAttribute("project");
	SecurityProfile dPSRSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dPSRSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		pMember = true;
	}
	
	User user = dPSRSecurityProfile.getUser();

if (pMember){ 
%>
	
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	<%@ page import="java.text.SimpleDateFormat" %> 
	<%@ page import="java.util.Calendar" %>
	<%@ page import="java.sql.*;" %>
	
	
	
	<%
		int rTBaselineId = Integer.parseInt(request.getParameter("rTBaselineId"));
		RTBaseline rTBaseline = new RTBaseline(rTBaselineId);
		
		
		
		// for forwardign to bulk edit, we need some special params.
		RequirementType requirementType = new RequirementType(rTBaseline.getRequirementTypeId());

		
		
		
		
		////////////////////////////////////////SECURITY//////////////////////////
		//
		// We ensure that the project Id is used as a filter in the Release Metrics Util
		// routine. This project id comes from the user's session, hence the user is 
		// logged in and is a member of this project. 
		//
		////////////////////////////////////////SECURITY//////////////////////////
				
		// get an ArrayList of requirements. 
   		ArrayList<HashMap<String,Object>> baselineReport = BaselineMetricsUtil.getBaseLineReport(requirementType.getProjectId() , rTBaselineId );
    	
   		
    			%>
    			<div id = 'displayListReportDiv' class='level1Box'>
					<table class='table table-striped' width='100%'>
						<tr class='info'>
							<td colspan='4' align="left">				
								<span class="subSectionHeadingText">
									Baseline Report  
									
								</span>
							</td>		
						</tr>
						<tr>
							<td width='100'>
								<b><span class='headingText'>Id </span></b>
								
							</td>
							<td width='100'>
								<b><span class='headingText'>
								Baseline Dt
								</span></b>
							 </td>
							<td width='500'> 
								<b><span class='headingText'>
								Name & Description
								</span></b>
							</td>
							
							<td > 
								<b><span class='headingText'>
								Attributes 
								</span></b>
							</td>
							
						</tr>	
    			<%
    			for(HashMap<String,Object> bR : baselineReport){
    				String reqIdString = (String) bR.get("reqId");
    				int reqId = Integer.parseInt(reqIdString);
    				
    				String fullTag = (String) bR.get("fullTag");
    				String version = (String) bR.get("version");
    				String baselinedDt = (String) bR.get("baselinedDt");
    				String name = (String) bR.get("name");
    				String description = (String) bR.get("description");
    				String uda = (String) bR.get("uda");
    				
    				%>
    					<tr>
							<td >
								<span class='headingText'><%=fullTag %> (V-<%=version %>)</span>
								
							</td>
							<td >
								<span class='headingText'><%=baselinedDt %></span>
								
							 </td>
							<td> 
								<span class='headingText'><%=name %>
								<hr></hr>
								<%=description %>
								</span>
								
							</td>
							
							<td > 
								<%
								// lets parse the UDA
								try{
								if (uda.contains(":##:")){
									String[] attributes = uda.split(":##:");
									for (String a:attributes){
										// lets parse each attribute
										if (a.contains(":#:")){
											String[] aSplit = a.split(":#:");
											String aLabel = aSplit[0];
											String aValue = aSplit[1];
											%>
											<b><%=aLabel%>  </b> : <%=aValue %>
											<br>
											<%
										}
									}
								}
								}
								catch(Exception attribParse){
								}
								%>
							
								
							</td>
							
						</tr>
    				<%
    			}
    			%>
    				</table>
    			</div>
    			<%
    		
    
}
		%>
	 
