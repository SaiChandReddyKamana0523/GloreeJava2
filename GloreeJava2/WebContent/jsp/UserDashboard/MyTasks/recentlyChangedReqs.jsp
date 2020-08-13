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

try {	
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	int targetProjectId = Integer.parseInt(request.getParameter("targetProjectId"));
	User user = securityProfile.getUser();
	ArrayList recentlyChangedRequirements = ProjectUtil.getMyRecentlyChangedRequriements(user.getEmailId(), changedSince, databaseType, targetProjectId);

%>

<div style="overflow: auto; width: 550px; height: 530px; border-left: 1px white solid; border-bottom: 1px gray solid; padding:0px; margin: 0px)">
<table class='paddedTable'>
<%

	String oldProjectPrefix = "";
	String currentProjectPrefix = "";
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
		String changedDescription = StringUtils.difference(prevVersionDescription, r.getRequirementDescription() );
		String changedUDA = StringUtils.difference(prevVersionUDA, currentUDA );
		
		
		
		currentProjectPrefix = r.getProjectShortName();
		if (!(currentProjectPrefix.equals(oldProjectPrefix))){
			// project name has changed.  lets print a nice heading
			%>
			<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
			onmouseover=  "this.style.background='#E5EBFF';" onmouseout=  "this.style.background='white';">
				<td colspan='7'>
					<span class='normalText'>
						<b>Project : <%=currentProjectPrefix %></b>
					
					</span>
				</td>
			</tr>
			
			<%
			
			oldProjectPrefix = currentProjectPrefix;
		}
		
		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");

		String displayRDInReportDiv = "displayRDInReportDiv" + r.getRequirementId();
		%>
			<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
		onmouseover=  "this.style.background='#E5EBFF';" onmouseout=  "this.style.background='white';">

											



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
	 		
			
	 			
					<a href="<%=url%>" target=_blank>
					<%=r.getRequirementFullTag()%> (V-<%=r.getVersion()%>) :  <%=r.getRequirementNameForHTML() %></a> 
					</span>
	 		</td>
			<td>
				<span class='normalText'>
					<input type='button' class='btn btn-primary btn-sm' name='showHistory' id='showHistory' value=' History ' onclick='displayRequirementVersionHistoryForMyTasks(<%=r.getRequirementId()%>)'>
				</span>
			</td>
			</tr>
			
			<% 
			if (
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
						<table>
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

						<% if ((changedUDA != null) && !(changedUDA.equals(""))) {
							// srt : we aren't able to disply this elegantly , so 
							// commenting this out.
						%>
							<!--  
								<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
									onmouseover=  "this.style.background='#E5EBFF';" onmouseout=  "this.style.background='white';">
									<td style='width:100px'>
										<span class='normalText'>Changes to Attributes </span>
									</td>
									<td >
										<span class='normalText'><%=changedUDA %> </span>
									</td>
								</tr>
							 -->
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
<%}
catch (Exception e){	
}%>


  