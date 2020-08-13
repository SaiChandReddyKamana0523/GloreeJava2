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
		recentlyCommentedRequirements = ProjectUtil.getMyRecentlyCommentedRequriements(user.getEmailId(), commentedSince, databaseType, project.getProjectId());
	}
	else {
		recentlyCommentedRequirements = ProjectUtil.getRecentlyCommentedRequriementsInFolder(folderId, commentedSince, databaseType, project.getProjectId());
	}
%>

<div style="overflow: auto; width: 100%;  border-left: 1px white solid; border-bottom: 1px gray solid; padding:0px; margin: 0px)">
<table class='table' >
<%

	String oldProjectPrefix = "";
	String currentProjectPrefix = "";
	Iterator rCR = recentlyCommentedRequirements.iterator();
	while (rCR.hasNext()){
		String enhancedComment = (String) rCR.next();
		String [] eC = enhancedComment.split(":##:");
		String firstName = "";
		String lastName = "";
		int requirementId = 0;
		String fullTag = "";
		String reqName = "";
		String projectPrefix = "";
		String commentNote = "";
		String commentDt = "";
		
		try{
			firstName = eC[0];
			lastName = eC[1];
			requirementId = Integer.parseInt(eC[2]);
			fullTag = eC[3];
			reqName = eC[4];
			projectPrefix  = eC[5];
			commentNote = eC[6];
			commentDt = eC[7];
			
			
			// lets remove any ' from reqName, as we will be using this in title of span
			if (reqName.contains("'")){
				reqName = reqName.replace("'","");
			}
		}
		catch (Exception e){
			
		}
		
		
		Requirement r = new Requirement(requirementId, databaseType);
		String url = ProjectUtil.getURL(request,requirementId ,"requirement");

		String displayCommentsDiv = "displayCommentsDiv" + requirementId;
		%>
			<tr class='info'>

				<td >
						<%
						// lets put spacers here for child requirements.
						  String req = fullTag;
					   	  int start = req.indexOf(".");
		    		  while (start != -1) {
		    	            start = req.indexOf(".", start+1);
							out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");			    	         
		  	       	}
						%>							 		
		 		
		 			
						
						<a href="#" 
		 				onclick='
							document.getElementById("contentCenterE").style.display = "none";
							document.getElementById("contentCenterF").style.display = "none";
						displayFolderInExplorer(<%=r.getFolderId()%>);
						displayFolderContentCenterA(<%=r.getFolderId() %>);
						displayRequirement(<%=r.getRequirementId()%>,"My Tasks", <%=r.getFolderId() %>);
					'		 									
						>
						<%=projectPrefix%> :  <%=fullTag%> </a> 
				
		 		</td>
		 		<td>
		 			<span class='normalText'>
		 				<%=firstName %> <%=lastName %>
		 			</span>
		 		</td>
				<td align='center'>
					<span class='normalText'>
						<input type='button' class='btn btn-primary btn-sm'  name='showDetails' id='showHistory' value=' Details ' 
						onclick='displayRequirementCommentsForMyTasks(<%=requirementId%>)'>
					</span>
				</td>
			</tr>
			<tr >
			
			<td colspan='3'>
				<span class='normalText'>
	 			<img src="/GloreeJava2/images/comments16.png" border="0">&nbsp; <%=commentNote %>
	 			</span>
			</td>
			
			</tr>
			<tr>
				<td  colspan='4'>
					<div id = '<%=displayCommentsDiv%>'> </div>
				</td>
			</tr>				 				

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
 


  