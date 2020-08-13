<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>
	
<%
	// authentication only
	String dARTIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((dARTIsLoggedIn == null) || (dARTIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean dAAIsMember = false;
	SecurityProfile dAASecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (dAASecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		dAAIsMember = true;
	}%>

<%if(dAAIsMember){ %>

	
	<% 
	   	// Called when trying to delete a requirement type. This displays requirements of this type in the explorer window.
	   	int requirementTypeId = Integer.parseInt(request.getParameter("requirementTypeId"));
	   	RequirementType requirementType = new RequirementType(requirementTypeId);	
		
	%> 
	<div id='displayAllAttributesInRT' class='invisibleLevel1Box' style='width:300px;'> 
		<table class='paddedTable' >
			<tr>
				<td colspan="2"> 
				<span class='sectionHeadingText'>
				<b>Baselines (Snapshots) in  '<%=requirementType.getRequirementTypeName() %>'
				</b>
				</span>
				</td>
			</tr>
			<%
				ArrayList baselines = ProjectUtil.getAllBaselines(requirementTypeId);
			    if (baselines != null){

			    	if (baselines.size() == 0){
			  %>
		 	<tr>
		 		<td colspan=2 >
		 		<span class='normalText'> No Baselines have been defined. </span>
		 		</td>			
		 	</tr>
			  <%
			    	}
			    	Iterator i = baselines.iterator();
			    	while ( i.hasNext() ) {
			    		RTBaseline a = (RTBaseline) i.next();
			%>
		 	<tr>
		 		<td
		 			colspan='2'
					style="background: white; cursor: pointer;" 
					onmouseover="this.style.background='lightblue';" 
					onmouseout="this.style.background='white';" 
					onClick="editRTBaselineForm(<%=a.getBaselineId()%>, <%=requirementTypeId %>)"
				> 
			 		<span class='normalText'  title="Baseline Description : <%=a.getBaselineDescription()%>">
			 		<font color='blue'>
				 		<img src="/GloreeJava2/images/baseline16.png" border="0">
				 		&nbsp; <%=a.getBaselineName() %>
			 		</font> 
			 		</span>
		 		</td>			
		 	</tr>
			 <%
			    	}
			    }
			%>
			<tr>
				<td colspan="2"> 
				<span class='sectionHeadingText'>
				<b>Custom Attributes in  '<%=requirementType.getRequirementTypeName() %>'
				</b>
				</span>
				</td>
			</tr>
			<%
				ArrayList attributes = ProjectUtil.getAllAttributes(requirementTypeId);
			    if (attributes != null){
			    	if (attributes.size() == 0){
					
			%>
			 	<tr>
			 		<td colspan=2 >
			 		<span class='normalText'> No Attributes have been defined. </span>
			 		</td>			
			 	</tr>
				
			<%
			    	}
			    	Iterator i = attributes.iterator();
			    	while ( i.hasNext() ) {
			    		RTAttribute a = (RTAttribute) i.next();
			%>
		 	<tr>
		 		<td
		 			colspan='2'
					style="background: white; cursor: pointer;" 
					onmouseover="this.style.background='lightblue';" 
					onmouseout="this.style.background='white';" 
					onClick="editRTAttributeForm(<%=a.getAttributeId()%>, <%=requirementTypeId %>)"
				> 
			 		<span class='normalText'  title="Attribute Description : <%=a.getAttributeDescription()%>">
				 		<font color='blue'>
				 		<img src="/GloreeJava2/images/rubyAttribute16.png" border="0">
				 		&nbsp;<%=a.getAttributeType() %> : <%=a.getAttributeName() %>
				 		</font> 
			 		</span>
		 		</td>			
		 	</tr>
			 <%
			    	}
			    }
			%>
		</table>
	</div>
<%}%>