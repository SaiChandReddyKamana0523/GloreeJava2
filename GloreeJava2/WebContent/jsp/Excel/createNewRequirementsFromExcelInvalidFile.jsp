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
	SecurityProfile securityProfile = (SecurityProfile) session
			.getAttribute("securityProfile");

	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains(
			"MemberInProject" + project.getProjectId())) {
		isMember = true;
	}

	if (isMember) {
%>




<%
	ArrayList errorRows = (ArrayList) request.getAttribute("errorRows");
%>


<div id='invalidCreateRequirementsFromExcelFileDiv' class='level1Box'>
<table class='paddedTable' width='100%'>
	<tr>
		<td  align='left' bgcolor='#99CCFF'>				
			<span class='subSectionHeadingText'>
			Create New Requirements from Uploaded Excel File - Validation Errors
			</span>
		</td>
	</tr>	
	<tr>
		<td>
		
		<%
		if ((errorRows != null)&& (errorRows.size() > 0)) { 
		%>
			<div id='errorRowsDiv' class='level2Box'>
				<table id="errorRowsTable">
					<tr><td colspan='3'>&nbsp;</td></tr>
					<tr> 
						<td colspan='3'>
							<div class='alert alert-success'>
								Your Excel file had some errors. Please fix the errors and try this operation again.
							</div> 
						</td>
					</tr>
					<tr><td colspan='3'>&nbsp;</td></tr>
					<tr>
						<td class='tableHeader'>
							<span class='sectionHeadingText'>
							Row Number
							</span>
						</td>
						<td class='tableHeader'>
							<span class='sectionHeadingText'>
							Input Data
							</span>
						</td>
						<td class='tableHeader'>
							<span class='sectionHeadingText'>
							Error Message
							</span>
						</td>
					</tr>
					<%				
					Iterator i = errorRows.iterator();
					int j = 0;
					String cellStyle = "normalTableCell";
					while (i.hasNext()) {
						String errorString = (String) i.next();
						// a typical errorSTring looks like this 
						// rowNum:##:input:##: errorMessage
						String[] errorRow = errorString.split(":##:");
						// split breaks down if last element is empty.
						// hense special logic
						String errorMessage = "";
						if (errorRow.length > 2){
							//i.e there are 3 elements
							// so lets get the 3rd element.
							errorMessage = errorRow[2];
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
								<%=errorRow[0]%>
							</span>
						</td>
						<td class='<%=cellStyle%>'>
							<span class='normalText'>
								<%=errorRow[1]%>
							</span>
						</td>
						<td class='<%=cellStyle%>'>
							<span class='normalText'>
								<font color='red'><%=errorMessage%></font>
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