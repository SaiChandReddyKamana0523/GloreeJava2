<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%String serverName = request.getServerName(); %>
		<div class="tabs">
			<table width='100%'>
			<tr>
				<td>
					<a class="logo" href="/GloreeJava2/jsp/WebSite/TCHome.jsp"><img src="/GloreeJava2/jsp/WebSite/tracecloudlogo.png" width="300" alt="tracecloud" title="TraceCloud"></a>
				</td>
				<td>
					<table width='100%'>
					
						<tr><td>
							<div style='float:right'>
								<table class='paddedTable'><tr>
									<td align='center' 
										style='width:100px; cursor:pointer' 
										onmouseover="style.backgroundColor='pink';" onmouseout="style.backgroundColor='white'"
										onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/WebSite/TCFeatures.jsp";'
									>
										<span  style='font-size:16px; color:blue'> Features</span>
									
									<!-- 
									<td   align='center' 
										style='width:100px; cursor:pointer' 
										onmouseover="style.backgroundColor='pink';" onmouseout="style.backgroundColor='white'"
										onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/WebSite/TCCompare.jsp";'
									>
										<span style='font-size:16px; color:blue'>Compare</span>
									</td>
									-->	
									
									<td   align='center' 
										style='width:100px; cursor:pointer' 
										onmouseover="style.backgroundColor='pink';" onmouseout="style.backgroundColor='white'"
										onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/WebSite/TCPricing.jsp";'
									>
										<span style='font-size:16px; color:blue'>Pricing</span>
									</td>
																		
									
									<td   align='center' 
										style='width:100px; cursor:pointer' 
										onmouseover="style.backgroundColor='pink';" onmouseout="style.backgroundColor='white'"
										onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/WebSite/TCDocumentation.jsp";'
									>
										<span style='font-size:16px; color:blue'>Documentation</span>
									</td>
									
									<td   align='center' 
										style='width:100px; cursor:pointer' 
										onmouseover="style.backgroundColor='pink';" onmouseout="style.backgroundColor='white'"
										onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/WebSite/TCJiraIntegration.jsp";'
									>
										<span style='font-size:16px; color:blue'>Jira</span>
									</td>
									
									
									<!-- 
									<td   align='center' 
										style='width:100px; cursor:pointer' 
										onmouseover="style.backgroundColor='pink';" onmouseout="style.backgroundColor='white'"
										onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/WebSite/TCCompany.jsp";'
									>
										<span style='font-size:16px; color:blue'>About Us</span>
									</td>
									-->
									
									<%
									SecurityProfile userProjectsSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
									if (userProjectsSecurityProfile == null){
										// means that the user is not logged in. so show the log in button
									%>
										
										<td   align='center' 
										style='width:100px; cursor:pointer' 
										onmouseover="style.backgroundColor='pink';" onmouseout="style.backgroundColor='white'"
										onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/WebSite/TCLogIn.jsp";'
										>
											<span style='font-size:16px; color:blue'>Log In</span>
										</td>
									<%
									}
									else{
										// means that the user is logged in. So, show the 'Dashboard' and 'Log out buttons'.
									%>
										<td   align='center' 
											style='width:120px; cursor:pointer' 
											onmouseover="style.backgroundColor='pink';" onmouseout="style.backgroundColor='white'"
											onclick='window.location="https://<%=serverName%>/GloreeJava2/jsp/UserDashboard/userProjects.jsp";'
											>
											<span style='font-size:16px; color:blue'>My Projects</span>
										</td>
										
										<td   align='center' 
											style='width:120px; cursor:pointer' 
											onmouseover="style.backgroundColor='pink';" onmouseout="style.backgroundColor='white'"
											onclick='window.location="https://<%=serverName%>/GloreeJava2/servlet/UserAccountAction?action=signOut";'
											>
											<span style='font-size:16px; color:blue'>Log Out</span>
										</td>
									<%}%>
									
								</tr></table>
								
							</div>
						
						</td></tr>
					</table>
				</td>
			</tr>
			</table>
		</div>
		
		