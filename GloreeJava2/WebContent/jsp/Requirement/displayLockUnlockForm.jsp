<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<% 

	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	int folderId = Integer.parseInt(request.getParameter("folderId"));
	Requirement requirement = new Requirement(requirementId, databaseType);
	User user = securityProfile.getUser();
	
	if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder" 
			+ requirement.getFolderId()))){
		// the user does not have update permissions on this folder. He / she should not be here
		// do nothing and return.
		return;
	}
	
	
	
	
%>
	<div class='alert alert-success'>
		
	 	
	 	<%
		if (requirement.getRequirementLockedBy().equals("")){
  			// this is an unlocked requirement. So, we show Lock now message.
			%>
			<div id='deleteRequirementPromptDiv' >
				<div style='float:right'>
					<a href='#' onClick='document.getElementById("requirementPromptDiv").style.display = "none";'>Close </a>
				</div>
				
					
					<span class='normalText'>
					Please note that when you Lock a requirement, others can not modify the requirement or its attributes. Traceability to and from this requirement is not affected.
					<br></br>
					Only you or an 'Administrator' for this project can unlock this requirement.
					
					<br></br>
					
					<input type='button' name='Lock Requirement' value='Lock Requirement' 
						onClick='lockRequirement(<%=requirementId%>,"<%=folderId%>")'>
					</span>
					<br>
				
			</div>
			
			
			<%
  					
		}
  		else {
  			// this is a locked requirement. we can show the UNLOCK icon
  			// if this req was locked by this user, then we show the Unlock Now message
  			// else, we ask them to contact the person who locked it.
  			if (requirement.getRequirementLockedBy().equals(user.getEmailId())){
				// the logged in user is the one who locked the req. so he is the ONLY one who
				// gets to see the unlock screen.
  				%>
  				<div id='deleteRequirementPromptDiv' >
					<div style='float:right'>
					<a href='#' onClick='document.getElementById("requirementPromptDiv").style.display = "none";'>Close </a>
					</div>
							
					
						<span class='normalText'>
	  						Please note that when you Unlock a requirement, others can  modify the requirement and its attributes. 
	  						
	  						<br></br>
							
							<input type='button' name='Unlock Requirement' value='Unlock Requirement' 
								onClick='unlockRequirement(<%=requirementId%>,"<%=folderId%>")'>
	  					</span>
					<br>
				</div>
  				<%
  			}
  			
  			else if (securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
  				// this user is an admin in this project, so can unlock any requirement.
  				%>
  				<div id='deleteRequirementPromptDiv' >
				<div style='float:right'>
					<a href='#' onClick='document.getElementById("requirementPromptDiv").style.display = "none";'>Close </a>
					</div>
							
					
						<span class='normalText'>
							<font color='red'>
							This requirement has been locked by <%=requirement.getRequirementLockedBy() %>. As an Administrator you can unlock it.
							</font>
							<br></br>
	  						Please note that when you Unlock a requirement, others can  modify the requirement and its attributes. 
	  						
	  						<br></br>
							
							<input type='button' name='Unlock Requirement' value='Unlock Requirement' 
								onClick='unlockRequirement(<%=requirementId%>,"<%=folderId%>")'>
	  					</span>
					<br>
				</div>
				<%
  			}
        	else {
		       // this req was locked by some one else. So, will see the contact this user message
		       	Role administratorRole = new Role(project.getProjectId(), "Administrator");
		       
		       %>
		       <div id='deleteRequirementPromptDiv' >
					<div style='float:right'>
					<a href='#' onClick='document.getElementById("requirementPromptDiv").style.display = "none";'>Close </a>
					</div>
				
					
						<span class='normalText'>
				       	This requirement has been locked by <%=requirement.getRequirementLockedBy() %>. It can ONLy be locked by that person, or by the project administrator.
				       	<br></br>
				       	The project administrators who can unlock this requirement are
				       		<br></br>
				       		<div id='displayAllUsersInRole' class='invisibleLevel1Box'> 
								<table class='paddedTable' >
									<tr>
										<td colspan="2"> 
										<span class='sectionHeadingText'>
										<b>The project administrators who can unlock this requirement are
										</b>
										</span>
										</td>
									</tr>
									<%
										ArrayList users = RoleUtil.getAllUsersInRole(administratorRole.getRoleId(), databaseType);
											    if (users != null){
											    	Iterator i = users.iterator();
												    	while ( i.hasNext() ) {
												    		User u = (User) i.next();
													%>
												 	<tr>
												 		<td colspan=2 >
												 		<span class='normalText' title='<%=u.getEmailId() %>'">
												 		&nbsp;<%=u.getFirstName() %>  <%=u.getLastName() %>
												 		</span>
												 		</td>			
												 	</tr>
													 <%
													    	}
									    }
									%>
								</table>
							</div>
				       	
				       </span>			
						<br>
					
				</div>
		       
		       <%
        	}
  		}
		        				
		        				
		 %>
	
	</div>
	
	
	
	
	