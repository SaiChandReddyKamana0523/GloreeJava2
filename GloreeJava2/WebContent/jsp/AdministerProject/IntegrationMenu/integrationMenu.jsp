<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
	<%@ page import="javax.servlet.http.HttpSession"  %>
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
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	Project project= (Project) session.getAttribute("project");
	ArrayList integrationMenus = project.getIntegrationMenus();
	
	String update = request.getParameter("update");
	
	String disabled = "DISABLED='DISABLED'";
	if (securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
		disabled = "";
	}
	
%>
	
<div id="integrationMenu" class="level1Box">

<table class='paddedTable' width='100%'>

	<tr>
		<td align='left' colspan='6' bgcolor='#99CCFF'>				
			<span class='subSectionHeadingText'>
			Integration Menu
			</span>
			<div style='float:right'>
				<a href='/GloreeJava2/documentation/help/administerAProject.htm' target='_blank'>
				<img src="/GloreeJava2/images/page.png"   border="0">
				</a>	
				&nbsp;&nbsp;
			</div>
		</td>
	</tr>
	<tr>
		<td align='left' colspan='6' >				
			<table>
				<%
				if ((update != null) && (update.equals("success"))){
				%>
				<tr>
					<td colspan='2'>
					<div class='alert alert-success'><span class='headingText'>
					Your changes have been updated in the system.
					</span></div>
					</td>
				</tr>
				
				<%
				}
				%>
				<tr>
					<td valign='top'>
						<span class='normalText' width='100'>Menu Label</span>
					</td>
					<td valign='top'>
						<span class='normalText' >Menu Value (URL)</span>
					</td>
				</tr>
				<% 
				int counter = 1;
				Iterator i = integrationMenus.iterator();
				while (i.hasNext()){
					IntegrationMenu integrationMenu = (IntegrationMenu) i.next();
					if (integrationMenu.getMenuType().equals("requirement")){
				%>
				<tr>
					<td valign='top'>
						<span class='normalText' width='100'>
							<input <%=disabled %> type='text' id='menuLabel<%=counter%>' size='30' maxlength='100' value='<%=integrationMenu.getMenuLabel()%>'></input>
						</span>
					</td>
					<td valign='top'>
						<span class='normalText' >
							<input <%=disabled %> type='text' id='menuValue<%=counter++%>'  size='80' maxlength='1000' value='<%=integrationMenu.getMenuValue()%>'></input>
						</span>
					</td>
				</tr>
				
				<%
					}
				}
				for (int j=0; j<5; j++){
				%>
				<tr>
					<td valign='top'>
						<span class='normalText' width='100'>
							<input <%=disabled %> type='text' id='menuLabel<%=counter%>' size='30' maxlength='100' value='' maxlength='100'></input>
						</span>
					</td>
					<td valign='top'>
						<span class='normalText' >
							<input <%=disabled %> type='text' id='menuValue<%=counter++%>' size='80' maxlength='1000' value='' maxlength='1000'></input>
						</span>
					</td>
				</tr>
				
				<%	
				}
				%>
				<tr>
					<td colspan='2' align='left'>
						<span class='normalText' >
							<input <%=disabled %> type='button' id='updateIntegrationMenuButton' value='Update Integration Menu' class='btn btn-sm btn-primary'
							onClick='
								document.getElementById("updateIntegrationMenuButton").disabled=true;
								updateIntegrationMenu(<%=counter-1%>);
							'>
						</span>
					</td>
				</tr>
				
			</table>
		</td>
	</tr>					

</table>
</div>

	