<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->    
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="java.sql.Date" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

	
<%
	//authorization
	// since we need authorization as well as authenticaiton we will use the 
	// security profile object.
	SecurityProfile mTSSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	if (mTSSecurityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%
	
	// authorization : since we are explicityly checking for and 
	// listing all the mTSProjects the user has access to
	// we are OK here. 
	}
	
try {

	String mTSdatabaseType = this.getServletContext().getInitParameter("databaseType");
	User mTSUser = mTSSecurityProfile.getUser();

	int mTSTargetProjectId = Integer.parseInt(request.getParameter("targetProjectId"));
	if (!(mTSSecurityProfile.getRoles().contains("MemberInProject" + mTSTargetProjectId))){
		// the user is not a member in this mTSProject. So lets get out.
		return;
	}
	String mTSOwnedBy = request.getParameter("ownedBy");
	String mTSDashboardType= request.getParameter("dashboardType");
	// in case the values didn't come in, lets default them.
	if ((mTSOwnedBy == null ) || (mTSOwnedBy.equals(""))){
		mTSOwnedBy = mTSUser.getEmailId();
	}
	if ((mTSDashboardType == null ) || (mTSDashboardType.equals(""))){
		mTSDashboardType = "ProjectDashboard";
	}
	
	Project mTSProject = new Project(mTSTargetProjectId, mTSdatabaseType);
%>
	
		<br></br>
		<span class='normalText'>
			Scope 
			&nbsp;&nbsp;
			<select name='dashboardType' id='dashboardType'>
				<%
				if (mTSDashboardType.equals("ProjectDashboard")){
				%>
					<option SELECTED value='ProjectDashboard'>Entire Project</option>
				<%
				}
				else {
				%>
					<option value='ProjectDashboard'>Entire Project</option>
				<%} %>
			</select>
			&nbsp;&nbsp;
			Tasks owned by 
			<select name='ownedBy' id='ownedBy'>
				<%
				if (mTSOwnedBy.equals(mTSUser.getEmailId())){
				%>
					<option SELECTED value='<%=mTSUser.getEmailId() %>'><%=mTSUser.getFirstName()%> <%=mTSUser.getLastName() %></option>
				<%
				}
				else {
				%>
					<option value='<%=mTSUser.getEmailId() %>'><%=mTSUser.getFirstName()%> <%=mTSUser.getLastName() %></option>
				<%} %>
			




				<%
				if (mTSOwnedBy.equals("All Users")){
				%>
					<option SELECTED value='All Users'>All Users</option>				
				<%
				}
				else {
				%>
					<option value='All Users'>All Users</option>
				<%} %>

			
			
			
				
				<option value='All Users'>------------------ </option>
				
				<%
				ArrayList members = mTSProject.getMembers();
				Iterator iM = members.iterator();
				while (iM.hasNext()){
					User m = (User) iM.next();
					if (m.getEmailId().equals(mTSUser.getEmailId() )){
						// we have already printed out this user, so skip
						continue;
					}
					else {
						%>
							<%
							if (mTSOwnedBy.equals(m.getEmailId())){
							%>
								<option SELECTED value='<%=m.getEmailId() %>'><%=m.getFirstName()%> <%=m.getLastName() %></option>				
							<%
							}
							else {
							%>
								<option value='<%=m.getEmailId() %>'><%=m.getFirstName()%> <%=m.getLastName() %></option>
							<%} %>
						<%
					}
				}
						
						
				%>
			</select>
			&nbsp;&nbsp;
		
			<input type='button' name='refreshMyTasks' id='refreshMyTasks' value='Refresh'
			onclick='fillMyTasks();'
			></input>	
		</span>

<%
}
catch (Exception e) {

}

%>
 















  