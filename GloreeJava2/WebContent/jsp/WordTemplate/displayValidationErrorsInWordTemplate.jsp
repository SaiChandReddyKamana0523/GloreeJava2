<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*"%>
<%@ page import="com.gloree.beans.*"%>
<%@ page import="com.gloree.utils.*"%>


<%
	// authentication only
	String isLoggedIn = (String) session.getAttribute("isLoggedIn");
	if ((isLoggedIn == null) || (isLoggedIn.equals(""))) {
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
<jsp:forward page="/jsp/WebSite/startPage.jsp" />
<%
	}
	Project project = (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())) {
		isMember = true;
	}

	if (isMember) {
%>




<%
	ArrayList erroredRequirements = (ArrayList) request.getAttribute("erroredRequirements");
	
%>

<div id='createRequirementsFromExcelResultsDiv' class='level1Box'>
<table class='paddedTable' width='100%'>
	<tr>
		<td  align='left' bgcolor='#99CCFF'>				
			<span class='subSectionHeadingText'>
			Word Document failed validation
			</span>
		</td>
	</tr>
	<tr>
		<td>
		
		<%
		if ((erroredRequirements != null)&& (erroredRequirements.size() > 0)) { 
		%>
			<div id='alertRowsDiv' class='level2Box'>
				<table id="alertRowsTable">
					<tr><td colspan='2'>&nbsp;</td></tr>
					<tr> 
						<td colspan='2'>
							<div class='alert alert-success'>
								The Requirements in your word template had some errors.
								Please fix these errors and try re-uploading. 
							</div> 
						</td>
					</tr>
					
					<tr>
						<td class='tableHeader'>
							<span class='sectionHeadingText'>
							Requirement Name
							</span>
						</td>
						<td class='tableHeader'>
							<span class='sectionHeadingText'>
							Error Message
							</span>
						</td>
					</tr>
					<%				
					Iterator i = erroredRequirements.iterator();
					int j = 0;
					String cellStyle = "normalTableCell";
					while (i.hasNext()) {
						String errorString = (String) i.next();
						// a typical alertSTring looks like this 
						// rowNum:##:input:##: alertMessage
						String[] errorRow = errorString.split(":##:");
						// split breaks down if last element is empty.
						// hense special logic
						String requirementName = errorRow[0];
						String errorMessage = "";
						if (errorRow.length > 1){
							//i.e there are 2 elements
							// so lets get the 2nd element.
							errorMessage = errorRow[1];
						}
						
						j++;
						// Now for each row in the array list, print the data out.
						if ((j % 2) == 1) {
							cellStyle = "normalTableCell";
						} else {
							cellStyle = "altTableCell";
						}
					%>
					<tr>
						<td class='<%=cellStyle%>'>
							<span class='normalText'>
								<%=requirementName%>
							</span>
						</td>
						<td class='<%=cellStyle%>'>
							<span class='normalText'>
								<%=errorMessage%>
							</span>
						</td>

					</tr>
					<%
					}
					%>
				</table>
			</div>			
		<%
		}
		%>

		</td>
	</tr>		
</table>
</div>
<%
	}
%>