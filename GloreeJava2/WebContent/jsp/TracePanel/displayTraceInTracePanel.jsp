<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
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
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	
	int fromRequirementId = Integer.parseInt(request.getParameter("fromRequirementId"));
	Requirement fromRequirement = new Requirement(fromRequirementId, databaseType);
	int toRequirementId = Integer.parseInt(request.getParameter("toRequirementId"));
	Requirement toRequirement = new Requirement(toRequirementId, databaseType);
	

	String disabledString = "";
	if (
			(!(securityProfile.getPrivileges().contains("traceFromRequirementsInFolder" 
			+ fromRequirement.getFolderId())))
			||
			(!(securityProfile.getPrivileges().contains("traceToRequirementsInFolder" 
					+ toRequirement.getFolderId())))
			)
	{
		disabledString = "disabled='disabled'";
	}
	
	
	String traceTitle = fromRequirement.getRequirementFullTag() + " : " + fromRequirement.getRequirementNameForHTML() +
			" => "+ toRequirement.getRequirementFullTag() + " : " + toRequirement.getRequirementNameForHTML() ;
	if (traceTitle.contains("'")){
		traceTitle = traceTitle.replace("'", " ");
	}
	
	if (
			(securityProfile.getPrivileges().contains("readRequirementsInFolder" + fromRequirement.getFolderId()))
			&&	
			(securityProfile.getPrivileges().contains("readRequirementsInFolder" + toRequirement.getFolderId()))
		){
		String traceCellId = Integer.toString(fromRequirement.getRequirementId()) + "-" + 
			Integer.toString(toRequirement.getRequirementId());
		String traceCellMenuId = "Menu" + traceCellId;		
		String fromRequirementTraceTo = fromRequirement.getRequirementTraceTo() + ",";
		
		
		if (fromRequirementTraceTo.toLowerCase().contains("(s)" + toRequirement.getRequirementFullTag().toLowerCase() + ",")){
			// There is a suspect trace, so lets print a suspect arrow.
		%>
		<td align='center' class='danger' style='border-top:none'>
			<div class='<%=fromRequirement.getRequirementFullTag()%> <%=toRequirement.getRequirementFullTag() %>'
				title='<%=traceTitle %>'  id='<%=fromRequirement.getRequirementId()%>-<%=toRequirement.getRequirementId()%>'
				onMouseOver=' 	
						var lastTraceObject = document.getElementById("lastTrace");
						if (document.getElementById(lastTraceObject.value) != null){
							document.getElementById(lastTraceObject.value).style.background="white";
						}
						lastTraceObject.value = "<%=fromRequirement.getRequirementId()%>-<%=toRequirement.getRequirementId()%>";
						
					
						var lastMenuItemObject = document.getElementById("lastMenuItem");
						if (document.getElementById(lastMenuItemObject.value) != null){
							document.getElementById(lastMenuItemObject.value).style.visibility="hidden";
						}
						lastMenuItemObject.value = "<%=traceCellMenuId%>";
						document.getElementById("<%=traceCellMenuId%>").style.visibility="visible";
						lastMenuItemObject.value = "<%=traceCellMenuId%>";
						document.getElementById("<%=traceCellMenuId%>").style.background="lightgray";
					'
			>
			<table class='table'><tr class='danger'><td align='center' class='danger' style='border-top:none'>

				<div >
					<span class='normalText' >
					<img src="/GloreeJava2/images/sTrace1-turned.png"  width="16" border="0">
					</span>
				</div>
				<table id='Menu<%=fromRequirement.getRequirementId()%>-<%=toRequirement.getRequirementId()%>' style="visibility:hidden">
					<tr>
						<td align='left'>
							<span class='normalText'>
							<select 
								class='<%=fromRequirement.getRequirementFullTag()%> <%=toRequirement.getRequirementFullTag() %>'
								<%=disabledString %> id='select<%=traceCellId %>' onChange='traceActionInTracePanel(<%=fromRequirement.getRequirementId() %>,<%=toRequirement.getRequirementId() %>, "<%=toRequirement.getRequirementFullTag() %>", "<%=traceCellId%>")'>
								<option value='-1'>Action</option>
								<option value='clearTrace'>Clear</option>
								<option value='deleteTrace'>Delete&nbsp;&nbsp;</option>
							</select>
							</span>
						</td>
					</tr>
				</table>							
			</td></tr></table>	

			</div>
			
		</td>
		<%
		}
		else if (fromRequirementTraceTo.toLowerCase().contains( toRequirement.getRequirementFullTag().toLowerCase() + ",")){
			// There is a not suspect trace, so lets print a clear trace image.
		%>
		<td align='center'  class='success' style='border-top:none'>
			<div class='<%=fromRequirement.getRequirementFullTag()%> <%=toRequirement.getRequirementFullTag() %>'
				title='<%=traceTitle %>'  id='<%=fromRequirement.getRequirementId()%>-<%=toRequirement.getRequirementId()%>'
				onMouseOver=' 	
						var lastTraceObject = document.getElementById("lastTrace");
						if (document.getElementById(lastTraceObject.value) != null){
							document.getElementById(lastTraceObject.value).style.background="white";
						}
						lastTraceObject.value = "<%=fromRequirement.getRequirementId()%>-<%=toRequirement.getRequirementId()%>";
						
					
						var lastMenuItemObject = document.getElementById("lastMenuItem");
						if (document.getElementById(lastMenuItemObject.value) != null){
							document.getElementById(lastMenuItemObject.value).style.visibility="hidden";
						}
						lastMenuItemObject.value = "<%=traceCellMenuId%>";
						document.getElementById("<%=traceCellMenuId%>").style.visibility="visible";
						lastMenuItemObject.value = "<%=traceCellMenuId%>";
						document.getElementById("<%=traceCellMenuId%>").style.background="lightgray";
					'
			>
			<table class='table'><tr class='success'><td align='center' class='success' style='border-top:none'>
			
				<div >
					<span class='normalText' >
					<img src="/GloreeJava2/images/cTrace1-turned.png"  width="16" border="0">
					</span>
				</div>
				<table id='Menu<%=fromRequirement.getRequirementId()%>-<%=toRequirement.getRequirementId()%>' style="visibility:hidden">
					<tr>
						<td align='left'>
							<span class='normalText'>
							<select 
								class='<%=fromRequirement.getRequirementFullTag()%> <%=toRequirement.getRequirementFullTag() %>'
								<%=disabledString %> id='select<%=traceCellId %>' onChange='traceActionInTracePanel(<%=fromRequirement.getRequirementId() %>,<%=toRequirement.getRequirementId() %>, "<%=toRequirement.getRequirementFullTag() %>", "<%=traceCellId%>")'>
								<option value='-1'>Action</option>
								<option value='suspectTrace'>Suspect</option>
								<option value='deleteTrace'>Delete</option>
							</select>
							</span>
						</td>
					</tr>
				</table>							
			</td></tr></table>	
			</div>
			
		</td>
	
		<%
		} 
		else {
			// There is a no existing trace
		%>
		<td  class='active'  align='center'  style='border-top:none'>
			<div  class='<%=fromRequirement.getRequirementFullTag()%> <%=toRequirement.getRequirementFullTag() %>'
				title='<%=traceTitle %>' id='<%=fromRequirement.getRequirementId()%>-<%=toRequirement.getRequirementId()%>'
				onMouseOver=' 	
						var lastTraceObject = document.getElementById("lastTrace");
						if (document.getElementById(lastTraceObject.value) != null){
							document.getElementById(lastTraceObject.value).style.background="white";
						}
						lastTraceObject.value = "<%=fromRequirement.getRequirementId()%>-<%=toRequirement.getRequirementId()%>";
						
					
						var lastMenuItemObject = document.getElementById("lastMenuItem");
						if (document.getElementById(lastMenuItemObject.value) != null){
							document.getElementById(lastMenuItemObject.value).style.visibility="hidden";
						}
						lastMenuItemObject.value = "<%=traceCellMenuId%>";
						document.getElementById("<%=traceCellMenuId%>").style.visibility="visible";
						lastMenuItemObject.value = "<%=traceCellMenuId%>";
						document.getElementById("<%=traceCellMenuId%>").style.background="lightgray";
					'
			>
			<table  bordercolor='lightgray' class='table'><tr class='active'><td align='center' class='active' style='border-top:none'>
				<div >
					<span class='normalText'>
					&nbsp;
					</span>
				</div>
				<table id='Menu<%=fromRequirement.getRequirementId()%>-<%=toRequirement.getRequirementId()%>' style="visibility:hidden">
					<tr>
						<td align='left'>
							<span class='normalText'>
							<select 
								class='<%=fromRequirement.getRequirementFullTag()%> <%=toRequirement.getRequirementFullTag() %>'
								<%=disabledString %> 
								id='select<%=traceCellId %>'
								onChange='traceActionInTracePanel(<%=fromRequirement.getRequirementId() %>,<%=toRequirement.getRequirementId() %>, "<%=toRequirement.getRequirementFullTag() %>", "<%=traceCellId%>")'>
								<option value='-1'>Action</option>
								<option value='createTrace'>Create&nbsp;&nbsp;</option>
							</select>
							</span>
						</td>
					</tr>
				</table>							
			</td></tr></table>	
			</div>
		</td>
		
		<%
		}
	}
	%>
	