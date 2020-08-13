<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
    // This routine is always called with a requirementId parameter.
    int requirementId = Integer.parseInt(request.getParameter("requirementId"));
    Requirement r = new Requirement(requirementId, databaseType);
	
    int j = 0;
    try {
    	j= Integer.parseInt(request.getParameter("rowId"));
    }
    catch (Exception e){
    	
    }
    
    
    
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean readPermissions = true;
	if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" 
			+ r.getFolderId()))){
		readPermissions = false;
	}
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	// you need to be a member of this project and have read permissions before you can see this.
	if (isMember && readPermissions){
 
	String attributeLabel = request.getParameter("attributeLabel");
    RTAttribute rTAttribute = new RTAttribute(r.getRequirementTypeId(), attributeLabel );
    RAttributeValue a = new RAttributeValue(requirementId, rTAttribute.getAttributeId(), databaseType);
%>



	 <%if (j < 99999){
	            	// j = 99999 for attributes in a single requirement. otherwise we are in a list view. 
	  %>
	           

	<div>
			 <ul  class="nav navbar-nav">
	        <li class="dropdown">
	          <a href="#" class="dropdown-toggle" data-toggle="dropdown"> <%=a.getAttributeEnteredValue() %></a>
	          <ul class="dropdown-menu">
	            
	           
		            
		           <li>
		            	<a href="#"
		            		onclick='displayAllRequirementsInRealFolder(<%=r.getFolderId()%>,"","showMatching","<%=attributeLabel%>:#:<%=a.getAttributeEnteredValue() %>");'>Show Matching
		            	</a>
		            </li>
		            
		            
		            <li>
		            	<a href="#"
		            		onclick='displayAllRequirementsInRealFolder(<%=r.getFolderId() %>,"","filterOut","<%=attributeLabel%>:#:<%=a.getAttributeEnteredValue() %>");'>Filter Out
		            	</a>
		            </li>
		             <li class="divider"></li>
		             
	             <li><a href="#" onclick='getAttributeEditForm(<%=j%>, <%=r.getRequirementId() %>,<%=r.getRequirementTypeId() %> ,"<%=attributeLabel%>");'>
	             	Edit Value
	             	</a>
	             </li>
	            </ul>
	        </li>
	      </ul>
			
			
			
		</div>
	<%}
	 else{
	 	// single requirement view
	 	String formattedAttributeEnteredValue = a.getAttributeEnteredValue().replaceAll("\n","<br>");
    	if (formattedAttributeEnteredValue.length() < 5){
    		formattedAttributeEnteredValue = formattedAttributeEnteredValue +  "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" 
    		 + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" ;
    	} 
	 %>
		
		          <a href="#" 
		          style='color:black' 
	        	  onclick='
				 	getAttributeEditForm(99999, <%=r.getRequirementId() %>,<%=r.getRequirementTypeId() %> ,"<%=a.getAttributeName() %>");
				 '
					> <%=formattedAttributeEnteredValue%></a>
	      
	
	<%} %>

<%}%>
