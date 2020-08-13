<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->    
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="java.sql.Date" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>



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
	
	int changedSince = 7;
	try {
		changedSince = Integer.parseInt(request.getParameter("changedSince"));
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
	ArrayList recentlyChangedRequirements = new ArrayList();
	if (folderId == 0 ){
		recentlyChangedRequirements = ProjectUtil.getMyRecentlyChangedRequriements(user.getEmailId(), changedSince, databaseType, project.getProjectId());
	}
	else {
		recentlyChangedRequirements = ProjectUtil.getRecentlyChangedRequriementsInFolder(folderId, changedSince, databaseType, project.getProjectId());
	}
	
%>

<div style="overflow: auto; width: 100%;  border-left: 1px white solid; border-bottom: 1px gray solid; padding:0px; margin: 0px)">
<table class='table ' >
<%

	String oldProjectPrefix = "";
	String currentProjectPrefix = "";

	if (recentlyChangedRequirements.size() == 0){
	%>
			<tr class='info'>
				<td colspan='2'>
					<div class='alert alert-danger'>
						<%if (folderId == 0 ){ %>
						None of the objects in this project have changed in the last <b><%=changedSince %></b> days.
						<br></br>
						Please note this page reports ONLY on objects owned by you
						<%}else { %>
						
						None of the objects in this foder have changed in the last <b><%=changedSince %></b> days.
						<br></br>
						Please note this page reports ONLY on objects owned by you
						<%}%>
					</div>
				</td>
				
			</tr>		
	
	<%	
	}	
	Iterator rCR = recentlyChangedRequirements.iterator();
	while (rCR.hasNext()){
		Requirement r = (Requirement) rCR.next();
		int previousVersion = r.getVersion() -1 ;
		String prevVersion = r.getAPreviousVersion(previousVersion );
		String [] pV = prevVersion.split(":##X##:");
		
		String prevVersionName = "";
		String prevVersionDescription = "";
		String prevVersionUDA = "";
		
		try {
			prevVersionName = pV[0];
			prevVersionDescription = pV[1];
			prevVersionUDA = pV[2];
		}
		catch (Exception e){
			
		}
		
		
		String currentUDA  = r.getUserDefinedAttributes();
		if (currentUDA == null){
			currentUDA = "";
		}
		currentUDA = currentUDA.replace(":##:", "<br>");
		currentUDA = currentUDA.replace(":#:", "&nbsp;&nbsp;=&nbsp;&nbsp;");
	
		if (prevVersionUDA == null){
			prevVersionUDA = "";
		}
		prevVersionUDA = prevVersionUDA.replace(":##:", "<br>");
		prevVersionUDA = prevVersionUDA.replace(":#:", "&nbsp;&nbsp;=&nbsp;&nbsp;");
	
		
		String changedName = StringUtils.difference(prevVersionName, r.getRequirementName() );
		String changedDescription = r.getRequirementDescription() ;
		String changedUDA = StringUtils.difference(prevVersionUDA, currentUDA );
		

		
		
		currentProjectPrefix = r.getProjectShortName();
		if (!(currentProjectPrefix.equals(oldProjectPrefix))){
			// project name has changed.  lets print a nice heading
			%>
			
		
		
		<%
			oldProjectPrefix = currentProjectPrefix;
		}
		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");
		String displayRDInReportDiv = "displayRDInReportDiv" + r.getRequirementId();
		%>
			<tr class='info'>

											



			<td >
					<%
					// lets put spacers here for child requirements.
					  String req = r.getRequirementFullTag();
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
			<%=r.getRequirementFullTag()%> : Ver-<%=r.getVersion()%> :  <%=r.getRequirementNameForHTML() %></a> 
			
			
	 		</td>
			<td>
				<span class='normalText'>
					<input type='button' class='btn btn-primary btn-sm' name='showHistory' id='showHistory' value=' History ' onclick='displayRequirementVersionHistoryForMyTasks(<%=r.getRequirementId()%>)'>
					</input>
				</span>
			</td>
			</tr>

			<% 
			if (r.getVersion() == 1){
			%>
				<tr>
					<td colspan='2'>
						<div class='alert alert-danger'>
						New <%=r.getRequirementFullTag() %>  created
						</div>
						<table class='table'>
						<% if ((changedName != null) && !(changedName.equals(""))) {%>
							<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onmouseover=  "this.style.background='#E5EBFF';" onmouseout=  "this.style.background='white';">
								<td style='width:100px'>
									<span class='normalText'>Name </span>
								</td>
								<td >
									<span class='normalText'><%=changedName %> </span>
								</td>
							</tr>
						<%} %>
						<% if ((changedDescription != null) && !(changedDescription.equals(""))) {%>
							<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onmouseover=  "this.style.background='#E5EBFF';" onmouseout=  "this.style.background='white';">
								<td style='width:100px'>
									<span class='normalText'>Description </span>
								</td>
								<td >
									<span class='normalText'><%=changedDescription %> </span>
								</td>
							</tr>
						<%} %>

						
											
						</table>
					</td>
				</tr>			
			<%
			}
			else if (
					((changedName != null) && !(changedName.equals("")))
					||
					((changedDescription != null) && !(changedDescription.equals("")))
					||
					((changedUDA != null) && !(changedUDA.equals("")))
				)
			{
				// there are some changes. so lets display them.
				%>
				<tr>
					<td colspan='2'>
						<table class='table'>
						<% if ((changedName != null) && !(changedName.equals(""))) {%>
							<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onmouseover=  "this.style.background='#E5EBFF';" onmouseout=  "this.style.background='white';">
								<td style='width:100px'>
									<span class='normalText'>Changes to Name </span>
								</td>
								<td >
									<span class='normalText'><%=changedName %> </span>
								</td>
							</tr>
						<%} %>
						<% if ((changedDescription != null) && !(changedDescription.equals(""))) {%>
							<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onmouseover=  "this.style.background='#E5EBFF';" onmouseout=  "this.style.background='white';">
								<td style='width:100px'>
									<span class='normalText'>Changes to Description </span>
								</td>
								<td >
									<span class='normalText'><%=changedDescription %> </span>
								</td>
							</tr>
						<%} %>

						
											
						</table>
					</td>
				</tr>
			<%} %>

			<tr>
				<td  colspan='2'>
					<div id = '<%=displayRDInReportDiv%>'> </div>
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
 
  