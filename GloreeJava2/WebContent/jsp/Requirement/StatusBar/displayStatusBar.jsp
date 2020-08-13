<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="com.gloree.beans.*" %>

<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>
<%@ page import="java.util.*" %>




<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	int requirementId = Integer.parseInt((request.getParameter("requirementId")));
	Requirement statusBarR = null;
	// lets see if we can get the req from session
	statusBarR = (Requirement) session.getAttribute(Integer.toString(requirementId));
	if (statusBarR == null){
		statusBarR = new Requirement(requirementId, databaseType);
		System.out.println("SRT Missed cache hit on requirement");	
	}
	else {
		System.out.println("SRT Found cache hit on requirement id " + requirementId);
	}
	
	
%>

		<div >
									<table class='table' border='1' >
											<tr>
											
												<td width='20px' align='center'>
													<%if (!(statusBarR.getRequirementLockedBy().equals(""))){
													// this requirement is locked. so lets display a lock icon.
													%>
														
														<span class='normalText' title='Requirement locked by <%=statusBarR.getRequirementLockedBy()%>'> 
															<img src="/GloreeJava2/images/lock16.png" border="0"> 
														</span>
														
													<%
													}
													else {
													%>
														<span class='normalText' title='Requirement not locked'> 
															&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
														</span>
														
													
													<%
													}
													%>
												</td>

												
												
											

												

												
												
												
												<% if (statusBarR.getRequirementTraceTo().length() == 0 ) { %>
													<td title='This requirement is an Orphan, i.e does not trace to Requirements upstream ' width='20px' align='center' style="background-color:lightgray">
														<b><font size='4' color='red'>O</font></b>
													</td>
												<%}
												else if(statusBarR.getRequirementTraceTo().contains("(s)")) { %>
													<td title='There is a suspect upstream trace' width='20px' align='center' style="background-color:pink">
														<img src="/GloreeJava2/images/arrow_up.png"> 
													</td>
												<%}
												else { %>
													<td title='All upstream traces are clear' width='20px' align='center' style="background-color:lightgreen">
														<img src="/GloreeJava2/images/arrow_up.png"> 
													</td>
												
												<%} %>
												
												
												
												<% if (statusBarR.getRequirementTraceFrom().length() == 0 ) { %>
													<td title='This requirement is a Dangling Requirement i.e does not have downstream traces' width='20px' align='center' style="background-color:lightgray">
														<b><font  size='4' color='red'>D</font></d> 
													</td>
												<%}
													else if(statusBarR.getRequirementTraceFrom().contains("(s)")) { %>
													<td title='There is a suspect downstream trace' width='20px' align='center' style="background-color:pink">
														<img src="/GloreeJava2/images/arrow_down.png"> 
													</td>
												<%}
													else {%>
													<td title='All downstream traces are clear' width='20px' align='center' style="background-color:lightgreen">
														<img src="/GloreeJava2/images/arrow_down.png"> 
													</td>
												
												<%} %>
												
												
												
												<%if (statusBarR.getTestingStatus().equals("Pending")){ %>		
													<td title='Testing is Pending' width='20px' align='center' style="background-color:lightgray">
														&nbsp;&nbsp;&nbsp;&nbsp; 
													</td>
												<%}
												else if (statusBarR.getTestingStatus().equals("Pass")){ %>
													<td title='Testing Passed' width='20px' align='center' style="background-color:lightgreen">
														<img src="/GloreeJava2/images/testingPassed.png"> 
													</td>
												<%}
												else {%>
													<td title='Testing Failed' width='20px' align='center' style="background-color:pink">
														<img src="/GloreeJava2/images/testingFailed.png"> 
													</td>
												
												<%} %>
												
												
												
												
												
												<%if (statusBarR.getRequirementPctComplete() == 100){%>
												
													<td title='Percent of work completed'  width='40px' align='center' style="background-color:lightgreen">
														<%=statusBarR.getRequirementPctComplete()%>%
													</td>
												<%}
												else if (statusBarR.getRequirementPctComplete() == 0){ %>
													<td title='Percent of work completed'  width='40px' align='center' style="background-color:lightgray">
														&nbsp;&nbsp;&nbsp;<%=statusBarR.getRequirementPctComplete()%>%
													</td>
												
												<%}
												else {%>
													<td title='Percent of work completed'  width='40px' align='center'style="background-color:pink">
														&nbsp;&nbsp;<%=statusBarR.getRequirementPctComplete()%>%
													</td>
												<%}%>												
												
											</tr>
										</table>
								</div>
						



