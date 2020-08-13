<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page import="com.gloree.utils.FolderUtil"%>
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="com.gloree.beans.*" %>

<%@ page import="com.gloree.utils.*" %>
<%
SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
String databaseType = this.getServletContext().getInitParameter("databaseType");
User user = securityProfile.getUser();
Project project= (Project) session.getAttribute("project");

// lets see if this user is a member of this project.
// we are leaving this page open to member of this project (which includes admins also)
boolean isMember = false;
if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
	isMember = true;
} 

if (isMember){

	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement r = new Requirement(requirementId, databaseType);
	boolean canBeReportedDangling = FolderUtil.canBeReportedDangling(r.getFolderId());
	boolean canBeReportedOrphan = FolderUtil.canBeReportedOrphan(r.getFolderId());
	
	String url = "";

	 String traceToPanel = "success";
	if(r.getRequirementTraceTo().contains("(s)")){
		traceToPanel = "danger";
	}
	 String traceFromPanel = "success";
	if(r.getRequirementTraceFrom().contains("(s)")){
		traceFromPanel = "danger";
	}
	
	
	 
	 if ((r.getRequirementTraceTo().length() == 0 ) &&  (canBeReportedOrphan)){
		traceToPanel = "warning";
	 }
	 String traceFromHeader  = "";
	 if ((r.getRequirementTraceFrom().length() == 0 ) &&  (canBeReportedDangling)){
		 traceFromHeader = r.getRequirementFullTag() + " is Dangling";
		traceFromPanel = "warning";
	 }
	 
									 					
%>



	<table class='table' style='width:100%'>
		<tr>
			<td class='<%=traceToPanel%>' style='width:50%'>Traces To
			
			&nbsp;&nbsp;
			
			<button type="button" class="btn btn-info btn-sm" data-toggle="modal" data-target="#myModal"
				onclick='fillCreateTracesModal(<%=r.getRequirementId() %>, <%=r.getProjectId() %>, "folderList");'
			>Create Traces</button>

			</td>
			<td class='<%=traceFromPanel%>' style='width:50%'>Trace From </td>
		</tr>
		<tr>
			<td 	
				onmouseover="
				var elements = document.getElementsByClassName('traceTo<%=r.getRequirementId() %>');
				for(var i=0, l=elements.length; i<l; i++){
				 elements[i].style.visibility = 'visible';
				}
				"
				onmouseout="
				var elements = document.getElementsByClassName('traceTo<%=r.getRequirementId() %>');
				for(var i=0, l=elements.length; i<l; i++){
				 elements[i].style.visibility = 'hidden';
				}
				"
			>
		<%

				 if ((r.getRequirementTraceTo().length() == 0 ) &&  (canBeReportedOrphan)){
					%>
					 <%=r.getRequirementFullTag()%> is an Orphan
					<%
				 }
				 else {
	  			String[] traces = r.getRequirementTraceTo().split(",");
	    		Requirement traceToReq = null;

	    		for (int l=0;l<traces.length;l++){
	    			
	    			
	    			try {
						// if you can get the requirement object, then print more details. If you hit exception print what you have
						traceToReq = new Requirement(traces[l].replace("(s)", "") , project.getProjectId(), databaseType);
						String tracePermission = "DISABLED='DISABLED'";
		    			if (
		    					(securityProfile.getPrivileges().contains("readRequirementsInFolder" + r.getFolderId()))
		    					&&	
		    					(securityProfile.getPrivileges().contains("readRequirementsInFolder" + traceToReq.getFolderId()))
		    				){
		    				tracePermission = "";
		    				
		    			}
						
						url = ProjectUtil.getURL(request, traceToReq.getRequirementId(),"requirement");
						
						
						
						if (traces[l].contains("(s)")){
							traces[l] = traces[l].replace("(s)", "");
							%>
						
						<ul  class="nav navbar-nav" >
				        <li class="dropdown">
				          <a href="#" class="dropdown-toggle" data-toggle="dropdown" style='color:red;cursor:pointer'
							onmouseover="this.style.border ='2px solid #85C1E9';this.style.width = '-moz-available'"
							onmouseout="this.style.border = 'none'"
							title='<%=traceToReq.getRequirementNameForHTML()%>'
							>  <%=traces[l]%>  </a>
				          <ul class="dropdown-menu">
				            
				            
				            <li style='display:block'>
				            	<a href="#"
				            		onclick='modifyTraceInListView("clearSuspect",<%=r.getRequirementId() %>, <%=r.getRequirementId() %>, <%=traceToReq.getRequirementId() %>)'
				            		 <%=tracePermission %>
				            	>
									Clear Suspect Trace
				            	</a>
				            </li>
							 <li style='display:block'>
				            	<a href="#"
				            		onclick='modifyTraceInListView("deleteTrace",<%=r.getRequirementId() %>, <%=r.getRequirementId() %>, <%=traceToReq.getRequirementId() %>)'
				            		 <%=tracePermission %>
				            	>
									 Delete Trace
				            	</a>
				            </li>
				            
							
							<li class='divider'></li>
								
				            <li style='display:block'>
				            	<a href="#" onClick="handleRequirementActionInDropDown(<%=traceToReq.getRequirementId()%>,<%=traceToReq.getFolderId()%>,'Open');">
				            		Open <%=traceToReq.getRequirementFullTag() %> Here
				            	</a>
				            </li>
				             <li style='display:block'>
				            	<a href='<%=url %>'
				            		target='_blank'
				            	>
									Open <%=traceToReq.getRequirementFullTag() %> in a New Tab
				            	</a>
				            </li>
				            
				          
				           
				            </ul>
				        </li>
				      </ul>	
				      <%} 
						else{
						%>
						<ul  class="nav navbar-nav" >
				        <li class="dropdown">
				          <a href="#" class="dropdown-toggle" data-toggle="dropdown" style='color:green;cursor:pointer'
							onmouseover="this.style.border ='2px solid #85C1E9';this.style.width = '-moz-available'"
							onmouseout="this.style.border = 'none'"
							title='<%=traceToReq.getRequirementNameForHTML()%>'
							>  <%=traces[l]%> </a>
				          <ul class="dropdown-menu">
				            
				          
				            <li style='display:block'>
				            	<a href="#"
				            		onclick='modifyTraceInListView("deleteTrace",<%=r.getRequirementId() %>, <%=r.getRequirementId() %>, <%=traceToReq.getRequirementId() %>)'
				            		 <%=tracePermission %>
				            	>
									Delete Trace
				            	</a>
				            </li>
							
							
							<li class='divider'></li>
								
								
				            <li style='display:block'>
				            	<a href="#" onClick="handleRequirementActionInDropDown(<%=traceToReq.getRequirementId()%>,<%=traceToReq.getFolderId()%>,'Open');">
				            		Open   <%=traceToReq.getRequirementFullTag() %> Here
				            	</a>
				            </li>
				             <li style='display:block'>
				            	<a href='<%=url %>'
				            		target='_blank'
				            	>
									Open  <%=traceToReq.getRequirementFullTag() %> in a New Tab
				            	</a>
				            </li>
				            
				            
				           
				            </ul>
				        </li>
				      </ul>							
						<%
						}
				      
						
					}
					catch (Exception e){
						
					}
	    		}
				 }
  		%>									 								
			</td>
			<td 
				onmouseover="
				var elements = document.getElementsByClassName('traceFrom<%=r.getRequirementId() %>');
				for(var i=0, l=elements.length; i<l; i++){
				 elements[i].style.visibility = 'visible';
				}
				"
				onmouseout="
				var elements = document.getElementsByClassName('traceFrom<%=r.getRequirementId() %>');
				for(var i=0, l=elements.length; i<l; i++){
				 elements[i].style.visibility = 'hidden';
				}
				"
			
			>
			<%
			if ((r.getRequirementTraceFrom().length() == 0 ) &&  (canBeReportedDangling)){
				 %>
					 <%=r.getRequirementFullTag()%> is Dangling
				<%
				 }
			else {
	  			String [] traces = r.getRequirementTraceFrom().split(",");
	    		Requirement traceFromReq = null;
	    		for (int l=0;l<traces.length;l++){
	    			
	    			
	    			try {
						// if you can get the requirement object, then print more details. If you hit exception print what you have
						traceFromReq = new Requirement(traces[l].replace("(s)", "") , project.getProjectId(), databaseType);
						String tracePermission = "DISABLED='DISABLED'";
		    			if (
		    					(securityProfile.getPrivileges().contains("readRequirementsInFolder" + r.getFolderId()))
		    					&&	
		    					(securityProfile.getPrivileges().contains("readRequirementsInFolder" + traceFromReq.getFolderId()))
		    				){
		    				tracePermission = "";
		    				
		    			}
						url = ProjectUtil.getURL(request, traceFromReq.getRequirementId(),"requirement");
						
						
						
						if (traces[l].contains("(s)")){
							traces[l] = traces[l].replace("(s)", "");
						%>
						<ul  class="nav navbar-nav" >
				        <li class="dropdown">
				          <a href="#" class="dropdown-toggle" data-toggle="dropdown" style='color:red;cursor:pointer'
							onmouseover="this.style.border ='2px solid #85C1E9';this.style.width = '-moz-available'"
							onmouseout="this.style.border = 'none'"
							title='<%=traceFromReq.getRequirementNameForHTML()%>'
							>  <%=traces[l]%>  </a>
				          <ul class="dropdown-menu">
				            
				            
				            <li style='display:block'>
				            	<a href="#"
				            		onclick='modifyTraceInListView("clearSuspect",<%=r.getRequirementId() %>, <%=traceFromReq.getRequirementId() %> ,<%=r.getRequirementId() %>)'
				            		 <%=tracePermission %>
				            	>
									Clear Suspect Trace
				            	</a>
				            </li>
							<li style='display:block'>
				            	<a href="#"
				            		onclick='modifyTraceInListView("deleteTrace",<%=r.getRequirementId() %>, <%=traceFromReq.getRequirementId() %> ,<%=r.getRequirementId() %>)'
				            		 <%=tracePermission %>
				            	>
									 Delete Trace
				            	</a>
				            </li>
							
							<li class='divider'></li>
				            
				            <li style='display:block'>
				            	<a href="#" onClick="handleRequirementActionInDropDown(<%=traceFromReq.getRequirementId()%>,<%=traceFromReq.getFolderId()%>,'Open');">
				            		Open <%=traceFromReq.getRequirementFullTag()%> Here
				            	</a>
				            </li>
				             <li style='display:block'>
				            	<a href='<%=url %>'
				            		target='_blank'
				            	>
									Open <%=traceFromReq.getRequirementFullTag()%> in a New Tab
				            	</a>
				            </li>
				            
				            
				           
				            </ul>
				        </li>
				      </ul>	
				      <%}
						else{
							%>
							<ul  class="nav navbar-nav" > 
					        <li class="dropdown">
					          <a href="#" class="dropdown-toggle" data-toggle="dropdown" style='color:green;cursor:pointer'
								onmouseover="this.style.border ='2px solid #85C1E9';this.style.width = '-moz-available'"
								onmouseout="this.style.border = 'none'"
								title='<%=traceFromReq.getRequirementNameForHTML()%>'
								>  <%=traces[l]%> </a>
					          <ul class="dropdown-menu">
					            
					            <li style='display:block'>
					            	<a href="#"
					            		onclick='modifyTraceInListView("deleteTrace",<%=r.getRequirementId() %>, <%=r.getRequirementId() %>, <%=traceFromReq.getRequirementId() %>)'
					            		 <%=tracePermission %>
					            	>
										Delete Trace
					            	</a>
					            </li>
					            
								<li class='divider'></li>
					            <li style='display:block'>
					            	<a href="#" onClick="handleRequirementActionInDropDown(<%=traceFromReq.getRequirementId()%>,<%=traceFromReq.getFolderId()%>,'Open');">
					            		Open <%=traceFromReq.getRequirementFullTag()%> Here
					            	</a>
					            </li>
					             <li style='display:block'>
					            	<a href='<%=url %>'
					            		target='_blank'
					            	>
										Open <%=traceFromReq.getRequirementFullTag()%> in a New Tab
					            	</a>
					            </li>
					            
					          
					           
					            </ul>
					        </li>
					      </ul>							
							<%
							}
						
						
						
					}
					catch (Exception e){
						
					}
	    			
	    		}
			}
	  		%>									 								
			</td>
		</tr>
	</table>


<%}%>