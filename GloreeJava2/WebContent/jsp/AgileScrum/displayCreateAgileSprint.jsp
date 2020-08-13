<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>

<%@ page import="java.text.SimpleDateFormat" %>

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
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User user = securityProfile.getUser();
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)

	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	if (isMember){
		
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy");
	    String sprintStartDate =  sdf.format(cal.getTime());
	    
	    cal.add(Calendar.DATE,21);
	    String sprintEndDate = sdf.format(cal.getTime() );


%>

	<table>
		<tr>
		<td>
		<table >
			<tr>
				<td  align='left'>
					<div >&nbsp;</div>
				</td>
			</tr>	

			<tr>
				<td  align='center' valign='center'  width='160' height='60'
					class = 'focusTab' >	
					<div >		 
		        		 <span class='normalText'>
		        		 Sprint Info
		        		 </span>
		        		 
		        	</div>
        		</td>

 				<td  align='center' valign='center'  width='160' height='60'
					class = 'nonFocusTab' >	
					<div >		 
		        		<img src="/GloreeJava2/images/calendar16.png" border="0">
			    		<span class='normalText'>
			    		 Daily Scrum
			    		 </span>
		        	</div>
        		</td>

 				<td  align='center' valign='center'  width='160' height='60'
					class = 'nonFocusTab' >	
					<div >		 
		        		<img src="/GloreeJava2/images/page.png" border="0">
		        		 <span class='normalText'>
		        		 Scrum Notes
		        		 </span>
		        	</div>
        		</td> 		
        		
        		 <td  align='center' valign='center'  width='160' height='60'
					class = 'nonFocusTab' >	
					<div >		 
		        		<img src="/GloreeJava2/images/color_swatch16.png" border="0">
		        		 <span class='normalText'>
		        		 Dashboard
		        		 </span>
		        	</div>
        		</td>		
			</tr>
		</table>
		</td>
		</tr>
		
		
		<tr>
		<td>
		<table>	
			<tr>
				<td >
					<div>
						<div id='createRequirementTypeDiv' class='level1Box'>
						
	
						<form method="post" id="createNewSprintForm" action="">
						<input type='hidden' name='projectId' id='projectId' value='<%=project.getProjectId()%>'></input>
							
						<table class='paddedTable' width='100%'>
							<tr>
								<td align='left' colspan='2' bgcolor='#99CCFF'>				
									<span class='subSectionHeadingText'>
									Create A New Sprint
									</span>
								</td>
							</tr>	
						
							<tr>
								<td ><div id='createAgileSprintMessage' class='alert alert-success' style='display:none'> ></div>
								
								
								
								</td>
							</tr>					
			
							<tr> 
								<td>
									<span class='headingText'>Sprint Name</span>
									<sup><span style="color: #ff0000;">*</span></sup> 
								</td>
								<td> 
									<span class='headingText'>
									<input type="text" name="sprintName" size="30" maxlength="100">
									</span> 
								</td>
							</tr>
							<tr> 
								<td>
									<span class='headingText'>Sprint Description</span>
									<sup><span style="color: #ff0000;">*</span></sup> 
								</td>
								<td>
									<span class='headingText'>
									<textarea name="sprintDescription" rows="10" cols="100" ></textarea>
									</span>
								</td>
							</tr>	
							
							<tr> 
								<td>
									<span class='headingText'>Scrum Master</span>
									<sup><span style="color: #ff0000;">*</span></sup> 
								</td>
								<td> 
									<span class='headingText'>
									<input type="text" name="scrumMaster" value="<%=user.getEmailId() %>" size="30" maxlength="100">
									</span> 
								</td>
							</tr>
							
							<tr> 
								<td>
									<span class='headingText'>Sprint Start Date</span>
									<sup><span style="color: #ff0000;">*</span></sup> 
								</td>
								<td> 
									<span class='headingText'>
									<input type="text" name="sprintStartDt"  size="10" maxlength="10" value="<%=sprintStartDate%>"> (MM/DD/YYYY)
									</span> 
								</td>
							</tr>
							
							
							<tr> 
								<td>
									<span class='headingText'>Sprint End Date</span>
									<sup><span style="color: #ff0000;">*</span></sup> 
								</td>
								<td> 
									<span class='headingText'>
									<input type="text" name="sprintEndDt"  size="10" maxlength="10" value="<%=sprintEndDate%>"> (MM/DD/YYYY)
									</span> 
								</td>
							</tr>
							<tr>
								<td colspan=2 align="left">
									<span class='normalText'>
										<input type="button"  class='btn btn-sm btn-primary' name="createSprintButton" id = "createSprintButton"
										value="  Create Sprint  " onClick="createAgileSprint()">
										
									</span>
								</td>
							</tr> 	
						 
						
						</table>
						
						</form>
						</div>
					</div>
				</td>
			</tr>				
		</table>
		</td>
		</tr>
		</table>
	
	
<%}%>