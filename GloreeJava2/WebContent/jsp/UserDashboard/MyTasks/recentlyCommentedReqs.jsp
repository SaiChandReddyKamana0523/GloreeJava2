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

try {	
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	int targetProjectId = Integer.parseInt(request.getParameter("targetProjectId"));
	User user = securityProfile.getUser();
	ArrayList recentlyCommentedRequirements = ProjectUtil.getMyRecentlyCommentedRequriements(user.getEmailId(), commentedSince, databaseType, targetProjectId);
%>

<div style="overflow: auto; width: 400px; height: 530px; border-left: 1px white solid; border-bottom: 1px gray solid; padding:0px; margin: 0px)">
<table class='paddedTable' width='100%'>
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
		
		
		
		String url = ProjectUtil.getURL(request,requirementId ,"requirement");

		String displayCommentsDiv = "displayCommentsDiv" + requirementId;
		%>
			<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
		onmouseover=  "this.style.background='#E5EBFF';" onmouseout=  "this.style.background='white';">

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
	 		
			
	 			
					<a href="<%=url%>" target=_blank title='<%=reqName%>'>
					<%=projectPrefix%> :  <%=fullTag%> </a> 
					</span>
	 		</td>
	 		<td>
	 			<span class='normalText'>
	 				<%=firstName %> <%=lastName %>
	 			</span>
	 		</td>
			<td align='center'>
				<span class='normalText'>
					<input type='button' class='btn btn-primary btn-sm' name='showDetails' id='showHistory' value=' Details ' 
					onclick='displayRequirementCommentsForMyTasks(<%=requirementId%>)'></input>
				</span>
			</td>
			</tr>
			<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
		onmouseover=  "this.style.background='#E5EBFF';" onmouseout=  "this.style.background='white';">
			
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
 