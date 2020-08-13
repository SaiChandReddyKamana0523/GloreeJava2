<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--  Security Enabled-->    
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="java.sql.Date" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>


<!-- Get the list of my projects by calling the util. -->


<%
	//authorization
	// since we need authorization as well as authenticaiton we will use the 
	// security profile object.
	SecurityProfile userProjectsSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	if (userProjectsSecurityProfile == null){
%>
	<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<%
	}
	
	if (this.getServletContext().getInitParameter("installationType").equals("onSite")) {
	
		String action = request.getParameter("action");
		String databaseType = this.getServletContext().getInitParameter("databaseType");

		

		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		String dataString = "";
		
		try {
		
			
			javax.naming.Context context =  new javax.naming.InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "";
			
			
			
			
			
			
			
			
			if (action.equals("totalMetrics")){
				%>
				<table width='100%'>
					<tr>
						<td colspan='2' bgcolor='#99CCFF'> <span class='normalText'>Totals </span> </td>
					</tr>
					
				<%
				sql = "select count(*) 'users' from gr_users; "		 ;
				prepStmt = con.prepareStatement(sql);
				rs = prepStmt.executeQuery();
				while (rs.next()){
					int users = rs.getInt("users");
					%>
					<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';> 
						<td width='200px' align='left'> <span class='normalText'>Number of Users</span></td>
						<td align='left'> <span class='normalText'><%=users %></span></td>
					</tr>
					<%
				}
				prepStmt.close();
				rs.close();
				
				
				
				
				sql = "select count(*) 'projects' from gr_projects; "		 ;
				prepStmt = con.prepareStatement(sql);
				rs = prepStmt.executeQuery();
				while (rs.next()){
					int projects = rs.getInt("projects");
					%>
					<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
						<td width='200px' align='left'> <span class='normalText'>Number of Projects</span></td>
						<td align='left'> <span class='normalText'><%=projects %></span></td>
					</tr>
					<%
				}
				prepStmt.close();
				rs.close();
				
				
				
				
				sql = "select count(*) 'requirements' from gr_requirements; "		 ;
				prepStmt = con.prepareStatement(sql);
				rs = prepStmt.executeQuery();
				while (rs.next()){
					int requirements = rs.getInt("requirements");
					%>
					<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
						<td width='200px' align='left'> <span class='normalText'>Number of Requirements</span></td>
						<td align='left'> <span class='normalText'><%=requirements %></span></td>
					</tr>
					<%
				}
				prepStmt.close();
				rs.close();
				
				
				
				
				sql = "select count(*) 'logs' from gr_requirement_log; "		 ;
				prepStmt = con.prepareStatement(sql);
				rs = prepStmt.executeQuery();
				while (rs.next()){
					int logs = rs.getInt("logs");
					%>
					<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
						<td width='200px' align='left'> <span class='normalText'>Number of Requirement Changes</span></td>
						<td align='left'> <span class='normalText'><%=logs %></span></td>
					</tr>
					<%
				}
				prepStmt.close();
				rs.close();
				
				
				
				
				
				sql = "select count(*) 'views' from gr_view_log; "		 ;
				prepStmt = con.prepareStatement(sql);
				rs = prepStmt.executeQuery();
				while (rs.next()){
					int views = rs.getInt("views");
					%>
					<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
						<td width='200px' align='left'> <span class='normalText'>Number of Requirement Views (From May 2012)</span></font></td>
						<td align='left'> <span class='normalText'><%=views %></span></td>
					</tr>
					<%
				}
				prepStmt.close();
				rs.close();
				
				
				
				
				
				sql = "select count(*) 'traces' from gr_traces; "		 ;
				prepStmt = con.prepareStatement(sql);
				rs = prepStmt.executeQuery();
				while (rs.next()){
					int traces = rs.getInt("traces");
					%>
					<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
						<td width='200px' align='left'> <span class='normalText'>Number of Traces</span></td>
						<td align='left'> <span class='normalText'><%=traces %></span></td>
					</tr>
					<%
				}
				prepStmt.close();
				rs.close();
				
				
				
				sql = "select count(*) 'orphans' from gr_requirements where (trace_to is  null or trace_to = '') ; "		 ;
				prepStmt = con.prepareStatement(sql);
				rs = prepStmt.executeQuery();
				while (rs.next()){
					int orphans = rs.getInt("orphans");
					%>
					<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
						<td width='200px' align='left'> <span class='normalText'>Number of Orphan Requirements</span></td>
						<td align='left'> <span class='normalText'><%=orphans %></span></td>
					</tr>
					<%
				}
				prepStmt.close();
				rs.close();

				
				
				sql = "select count(*) 'danglings' from gr_requirements where (trace_from is  null or trace_from = '') ; "		 ;
				prepStmt = con.prepareStatement(sql);
				rs = prepStmt.executeQuery();
				while (rs.next()){
					int danglings = rs.getInt("danglings");
					%>
					<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
						<td width='200px' align='left'> <span class='normalText'>Number of Dangling Requirements</span></td>
						<td align='left'> <span class='normalText'><%=danglings %></span></td>
					</tr>
					<%
				}
				prepStmt.close();
				rs.close();				
				
				
				
				%>
				</table>
				<%
			}
			
			
			
			
			
			
			
			if (action.equals("userMetrics")){
				%>
				<table width='100%'>
					<tr>
						<td colspan='2' bgcolor='#99CCFF'> <span class='normalText'>Users by License Type </span> </td>
					</tr>
					<tr>
						<td width='150px'> <span class='normalText'><b>License Type</b></span></td>
						<td align='left'> <span class='normalText'><b>Users</b></span></td>
					</tr>
					<tr>
						<td width='200' align='left'> 
							<span class='normalText'>
								Permitted Read Write Licenses 
							</span>
						</td>
						<td align='left'> 
							<span class='normalText'>
								<%
								int permittedReadWriteLicenses = 0;
								try{
									permittedReadWriteLicenses = Integer.parseInt(this.getServletContext().getInitParameter("readWriteLicenses"));
								}
								catch (Exception e){
									permittedReadWriteLicenses = 0;
								}
								
								%>
								<%=permittedReadWriteLicenses%>
							</span> 
						</td>												
					</tr>
					<tr>
						<td width='200' align='left'> 
							<span class='normalText'>
								Users with Read Write Licenses 
							</span>
						</td>
						<td align='left'> 
							<span class='normalText'>
							<%=ProjectUtil.getNumberOfUsersByLicenseType(databaseType, "readWrite") %>
							</span> 
						</td>												
					</tr>			
					<tr>
						<td width='200' align='left'> 
							<span class='normalText'>
								Users with Trial Licenses 
							</span>
						</td>
						<td align='left'> 
							<span class='normalText'>
							<%=ProjectUtil.getNumberOfUsersByLicenseType(databaseType, "trial") %>
							</span> 
						</td>												
					</tr>			
					<tr>
						<td width='200' align='left'> 
							<span class='normalText'>
								Users with Expired Licenses 
							</span>
						</td>
						<td align='left'> 
							<span class='normalText'>
							<%=ProjectUtil.getNumberOfUsersByLicenseType(databaseType, "expired") %>
							</span> 
						</td>												
					</tr>			
				</table>
				<%
			}
			
			
			
			
			

			
			

						
		
			

					
			
		
			
			

			
			
			
			if (action.equals("activityByMonthMetrics")){
				%>
				
				
				
				
				<table width='100%'>
					<tr>
						<td colspan='9' bgcolor='#99CCFF'> <span class='normalText'>Activity by Month</span> </td>
					</tr>
					
					<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
						<td width='150px'> <span class='normalText'><b>&nbsp;</b></span></td>
						<td colspan='2' bgcolor='#E5EBFF'> <span class='normalText'><b>Projects Created</b></span></td>
						<td colspan='2' bgcolor='#E5FFEC' > <span class='normalText'><b>Requirements Created</b></span></td>
						<td colspan='2' bgcolor='#E5EBFF'> <span class='normalText'><b>Requirements Changed</b></span></td>
						<td colspan='2' bgcolor='#E5FFEC'> <span class='normalText'><b>Requirements Viewed</b></span></td>
					</tr>
					
					<tr style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>
						<td width='150px'> <span class='normalText'><b>Month</b></span></td>
						<td bgcolor='#E5EBFF'> <span class='normalText'><b>This Month</b></span></td>
						<td bgcolor='#E5EBFF'> <span class='normalText'><b>Till Date</b></span></td>
						<td bgcolor='#E5FFEC'> <span class='normalText'><b>This Month</b></span></td>
						<td bgcolor='#E5FFEC'> <span class='normalText'><b>Till Date</b></span></td>
						<td bgcolor='#E5EBFF'> <span class='normalText'><b>This Month</b></span></td>
						<td bgcolor='#E5EBFF'> <span class='normalText'><b>Till Date</b></span></td>
						<td bgcolor='#E5FFEC'> <span class='normalText'><b>This Month</b></span></td>
						<td bgcolor='#E5FFEC'> <span class='normalText'><b>Till Date</b></span></td>
					</tr>
				<%
				HashMap projectsCreated = new HashMap();
				HashMap requirementsCreated = new HashMap();
				HashMap requirementsChanged = new HashMap();
				HashMap requirementsViewed = new HashMap();
				
				
				//
				// lets fill the projectsCreated Hashmap
				//
				if (databaseType.equals("mySQL")) {
					sql = " select date_format(created_dt,'%Y %m') 'month', count(*) 'numberOfProjects' "+
						" from gr_projects " +
						"	group by date_format(created_dt,'%Y %m') " + 
						"	order by 1 "		 ;
				}
				else {
					sql = " select to_char(created_dt,'YYYY MM') 'month', count(*) 'numberOfProjects' "+
							" from gr_projects " +
							"	group by to_char(created_dt,'YYYY MM') " + 
							"	order by 1 "		 ;
					
				}
				prepStmt = con.prepareStatement(sql);
				rs = prepStmt.executeQuery();
				while (rs.next()){
					String month = rs.getString("month");
					int numberOfProjects = rs.getInt("numberOfProjects");
					projectsCreated.put(month, new Integer(numberOfProjects));
				}
				
				//
				// lets fill the requirementsCreated Hashmap
				//
				if (databaseType.equals("mySQL")) {
					sql = " select date_format(created_dt,'%Y %m') 'month', count(*) 'numberOfRequirements' "+
						" from gr_requirements " +
						"	group by date_format(created_dt,'%Y %m') " + 
						"	order by 1 "		 ;
				}
				else {
					sql = " select to_char(created_dt,'YYYY MM') 'month', count(*) 'numberOfRequirements' "+
							" from gr_requirements " +
							"	group by to_char(created_dt,'YYYY MM') " + 
							"	order by 1 "		 ;
					
				}
				prepStmt = con.prepareStatement(sql);
				rs = prepStmt.executeQuery();
				while (rs.next()){
					String month = rs.getString("month");
					int numberOfRequirements = rs.getInt("numberOfRequirements");
					requirementsCreated.put(month, new Integer(numberOfRequirements));
				}
					
				//
				// lets fill the requirementsChanged Hashmap
				//
				if (databaseType.equals("mySQL")) {
					sql = " select date_format(action_dt,'%Y %m') 'month', count(*) 'numberOfRequirementChanges' "+
						" from gr_requirement_log " +
						"	group by date_format(action_dt,'%Y %m') " + 
						"	order by 1 "		 ;
				}
				else {
					sql = " select to_char(action_dt,'YYYY MM') 'month', count(*) 'numberOfRequirementChanges' "+
							" from gr_requirement_log " +
							"	group by to_char(action_dt,'YYYY MM') " + 
							"	order by 1 "		 ;
					
				}
				prepStmt = con.prepareStatement(sql);
				rs = prepStmt.executeQuery();
				while (rs.next()){
					String month = rs.getString("month");
					int numberOfRequirementChanges = rs.getInt("numberOfRequirementChanges");
					requirementsChanged.put(month, new Integer(numberOfRequirementChanges));
				}
				

				//
				// lets fill the requirementsViewed Hashmap
				//
				if (databaseType.equals("mySQL")) {
					sql = " select date_format(view_dt,'%Y %m') 'month', count(*) 'numberOfRequirementViews' "+
						" from gr_view_log " +
						"	group by date_format(view_dt,'%Y %m') " + 
						"	order by 1 "		 ;
				}
				else {
					sql = " select to_char(view_dt,'YYYY MM') 'month', count(*) 'numberOfRequirementViews' "+
							" from gr_view_log " +
							"	group by to_char(view_dt,'YYYY MM') " + 
							"	order by 1 "		 ;
					
				}
				prepStmt = con.prepareStatement(sql);
				rs = prepStmt.executeQuery();
				while (rs.next()){
					String month = rs.getString("month");
					int numberOfRequirementViews = rs.getInt("numberOfRequirementViews");
					requirementsViewed.put(month, new Integer(numberOfRequirementViews));
				}

				// lets get all the months, so we can start printing out the table.
				if (databaseType.equals("mySQL")) {
					sql = " select date_format(created_dt,'%Y %m') 'month', count(*) 'numberOfRequirements' "+
						" from gr_requirements " +
						"	group by date_format(created_dt,'%Y %m') " + 
						"	order by 1 "		 ;
				}
				else {
					sql = " select to_char(created_dt,'YYYY MM') 'month', count(*) 'numberOfRequirements' "+
							" from gr_requirements " +
							"	group by to_char(created_dt,'YYYY MM') " + 
							"	order by 1 "		 ;
					
				}
				prepStmt = con.prepareStatement(sql);
				rs = prepStmt.executeQuery();
				
				int cumProjectsCreated = 0; 
				int cumRequirementsCreated = 0; 
				int cumRequirementsChanged = 0; 
				int cumRequirementsViewed = 0; 
				
				
				while (rs.next()){
					String month = rs.getString("month");
					
					int projectsCreatedThisMonth = 0 ;
					int requirementsCreatedThisMonth = 0 ;
					int requirementsChangedThisMonth = 0 ;
					int requirementsViewedThisMonth = 0 ;
					
					try {
						projectsCreatedThisMonth = Integer.parseInt(projectsCreated.get(month).toString());
					}
					catch (Exception e){
						// do nothing.
					}
					cumProjectsCreated += projectsCreatedThisMonth ;
					
					
					try {
						requirementsCreatedThisMonth = Integer.parseInt(requirementsCreated.get(month).toString());
					}
					catch (Exception e){
						// do nothing.
					}
					cumRequirementsCreated += requirementsCreatedThisMonth ;
					
					try {
						requirementsChangedThisMonth = Integer.parseInt(requirementsChanged.get(month).toString());
					}
					catch (Exception e){
						// do nothing.
					}
					cumRequirementsChanged += requirementsChangedThisMonth ;
					
					
					try {
						requirementsViewedThisMonth = Integer.parseInt(requirementsViewed.get(month).toString());
					}
					catch (Exception e){
						// do nothing.
					}
					cumRequirementsViewed += requirementsViewedThisMonth ;
					
					
					%>
						<tr  style="background-color:white; border-width:thin; border-style:solid; border-color:white"
								onMouseOver=  this.style.background='#E5EBFF'; onMouseOut=  this.style.background='white';>

						<td width='150px'> <span class='normalText'><b><%=month %></b></span></td>
						
						<td> <span class='normalText'><%=projectsCreatedThisMonth%> </span></td>
						<td> <span class='normalText'><%=cumProjectsCreated %></span></td>
						
						<td> <span class='normalText'><%=requirementsCreatedThisMonth%> </span></td>
						<td> <span class='normalText'><%=cumRequirementsCreated %></span></td>
						
						<td> <span class='normalText'><%=requirementsChangedThisMonth%> </span></td>
						<td> <span class='normalText'><%=cumRequirementsChanged %></span></td>
						
						<td> <span class='normalText'><%=requirementsViewedThisMonth%> </span></td>
						<td> <span class='normalText'><%=cumRequirementsViewed %></span></td>
						
						</tr>
					<%
				}
				%>
				</table>
				
				<%
				
			}			
			
			
			
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}  finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
		
		
		
		
		
} %>
















 