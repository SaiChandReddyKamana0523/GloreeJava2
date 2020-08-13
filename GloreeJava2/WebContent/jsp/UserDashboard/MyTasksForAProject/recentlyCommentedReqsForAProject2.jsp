<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->    
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="java.sql.Date" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>



	<!--  Google Analytics Tracking  -->	
	<script type="text/javascript">
	
	  var _gaq = _gaq || [];
	  _gaq.push(['_setAccount', 'UA-31449327-1']);
	  _gaq.push(['_trackPageview']);
	
	  (function() {
	    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
	    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	  })();
	
	</script>
	
	
	
<%

	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	if (securityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%

	}
	
	int commentedSince = 7;
	try {
		commentedSince = Integer.parseInt(request.getParameter("commentedSince"));
	}
	catch (Exception e) {
		
	}
	
	int folderId = 0;
	try {
		folderId = Integer.parseInt(request.getParameter("folderId"));
	}
	catch (Exception e) {
		
	}	

try {	
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	User user = securityProfile.getUser();
	ArrayList recentlyCommentedRequirements = new ArrayList();
	if (folderId == 0 ){
		recentlyCommentedRequirements = ProjectUtil.getMyRecentlyCommentedRequriements2(user.getEmailId(), commentedSince, databaseType, project.getProjectId());
	}
	else {
		recentlyCommentedRequirements = ProjectUtil.getRecentlyCommentedRequriementsInFolder2(folderId, commentedSince, databaseType, project.getProjectId());
	}
%>

<div style="overflow: auto; width: 100%;  border-left: 1px white solid; border-bottom: 1px gray solid; padding:0px; margin: 0px)">
<table class='table width='100%' >

<%

	String oldProjectPrefix = "";
	String currentProjectPrefix = "";
	if (recentlyCommentedRequirements.size() == 0){
	%>
			<tr class='info'>
				<td colspan='4'>
					<div class='alert alert-danger'>
						There were no comments made on objects in this folder in the last <b><%=commentedSince %></b> days.
						
					</div>
				</td>
				
			</tr>		
	
	<%	
	}
	Iterator rCR = recentlyCommentedRequirements.iterator();
	while (rCR.hasNext()){
		Integer requirementIdObj= (Integer) rCR.next();
		int requirementId = requirementIdObj.intValue();
		
		Requirement r = new Requirement(requirementId, databaseType);
		String url = ProjectUtil.getURL(request,requirementId ,"requirement");

		%>
			<tr class='info'>
				<td>
					<%=r.getRequirementFullTag() %>
				</td>
				<td colspan='2'>
					<%=r.getRequirementNameForHTML() %>
				</td>
				<td >
					<input type='button' class='btn btn-sm btn-primary' value='Open in New Tab ' onClick='window.open ("<%=url%>");' ></input>
				</td>
				
			</tr>		
		<% 
		String displayCommentsDiv = "displayCommentsDiv" + requirementId;
		ArrayList comments = RequirementUtil.getRequirementComments(r.getRequirementId(), databaseType);
		%>
					
					<%
				
					    if (comments != null){
					    	Iterator c = comments.iterator();
					    	
					    	while ( c.hasNext() ) {
					    		
					    	Comment commentObject = (Comment) c.next();
							%>

						 	<tr>
						 		<td >
						 			<span class="normalText">
						 			<img src="/GloreeJava2/images/comment16.png" border="0">
						 			</span>
						 		</td>
						 		<td>
						 			<span class="normalText">
						 			<%=commentObject.getHTMLFriendlyCommentNote() %> 
						 			</span>
						 		</td>
						 		<td>
						 			<span class='normalText'>
						 			<%=commentObject.getCommenterEmailId() %>
						 			</span>
						 		</td>
						 		<td >
						 			<span class='normalText'>
						 			<%=commentObject.getCommentDate() %>
						 			</span>
						 		</td>		
						 	</tr>
							 <%
					    	}
					    }
					%>

		<%
	}
%>
	</table>

</div>


<%
}
catch (Exception e) {

}

%>
 


  