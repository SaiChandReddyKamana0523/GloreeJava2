<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>
<%@ page import="com.aspose.words.*" %>

<%
	// authentication only
	String displayTemplateIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((displayTemplateIsLoggedIn == null) || (displayTemplateIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	User user = securityProfile.getUser();
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	boolean isAdmin = false;
	if (securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
		isAdmin = true;
	}
	
	if (isMember){

		int folderId = Integer.parseInt(request.getParameter("folderId"));
		int templateId = Integer.parseInt(request.getParameter("templateId"));

		ArrayList styles = WordTemplateUtil.getWordTemplateStyles(templateId, folderId, databaseType);
	%>
	<table>
		<tr>
			<td>
			<span class='normalText'>
			We found the following styles in your Word Document. Please select a Style that your Requirement Text
			is in.
			<br> <br>
			Please Note that we support Word 'Character Styles'. If your document has any 'Character Styles' we will locate
			them and list them in the drop down box. Some examples of Word Character Styles are : Suble Emphasis, Emphasis,
			Intense Emphasis, Intense Reference etc... For more information about Word Character Styles, please refer to 
			your Microsoft Word Documentation.
			<br><br> 
			</span>
			</td>
		</tr>
		<tr>
			<td>
			<span class='normalText'>
				<select 
				style='height:25px;'
				id='styles' name='styles'>
				<%
					Iterator j = styles.iterator();
					while (j.hasNext()){
						String styleName = (String) j.next();
						%>
						<option value='<%=styleName%>'> <%=styleName%>
						<%
					}
				 %>
				</select>
			</span>
			</td>
		</tr>		
	</table>
<%}%>