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
	ArrayList createdRequirements = (ArrayList) request.getAttribute("createdRequirements");
	ArrayList alertRows = (ArrayList) request.getAttribute("alertRows");
%>

<div id='createRequirementsFromExcelResultsDiv' class='level1Box'>
<table class='paddedTable' width='100%'>
	<tr>
		<td  align='left' bgcolor='#99CCFF'>				
			<span class='subSectionHeadingText'>
			Create New Requirements from Uploaded Excel File - Requirements Created
			</span>
		</td>
	</tr>
	<tr>
		<td>
		
		<%
		if ((alertRows != null)&& (alertRows.size() > 0)) { 
		%>
			<div id='alertRowsDiv' class='level2Box'>
				<table id="alertRowsTable">
					<tr><td colspan='3'>&nbsp;</td></tr>
					<tr> 
						<td colspan='3'>
							<div class='alert alert-success'>
								Your Excel Import had some alerts. Please see below . 
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
					Iterator i = alertRows.iterator();
					int j = 0;
					String cellStyle = "normalTableCell";
					while (i.hasNext()) {
						String alertString = (String) i.next();
						// a typical alertSTring looks like this 
						// rowNum:##:input:##: alertMessage
						String[] alertRow = alertString.split(":##:");
						// split breaks down if last element is empty.
						// hense special logic
						String alertMessage = "";
						if (alertRow.length > 2){
							//i.e there are 3 elements
							// so lets get the 3rd element.
							alertMessage = alertRow[2];
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
								<%=alertRow[0]%>
							</span>
						</td>
						<td class='<%=cellStyle%>'>
							<span class='normalText'>
								<%=alertRow[1]%>
							</span>
						</td>
						<td class='<%=cellStyle%>'>
							<span class='normalText'>
								<font color='red'><%=alertMessage%></font>
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
	<tr>
		<td>
		<div id='createdRequirementsDiv' class='level2Box'>
		<table id="createdRequirements">
			<%
				if (createdRequirements.size() > 0) {
					Iterator i = createdRequirements.iterator();
					int j = 0;
					String cellStyle = "normalTableCell";
					while (i.hasNext()) {
						Requirement r = (Requirement) i.next();
						// a typical uda looks like this 
						// Customer:#: SBI:##:Delivery Estimate:#:01/01/12
						String uda = r.getUserDefinedAttributes();
						String[] attribs = uda.split(":##:");
						j++;
						if (j > 200){
							int headerColspan = 7 + attribs.length;
			%>
							<tr><td colspan='<%=headerColspan%>'>&nbsp;</td></tr>
							<tr><td colspan='<%=headerColspan%>'>
								<span class='normalText'>
									Your import created <%=createdRequirements.size()%> Requirements. 
									Showing only the first 200 Requirements.
								</span>
							</td></tr>
							</table>
							</div></td></tr></table></div>
						
			<%
							return;
						}
						// for the first row, print the header and user defined columns etc..
						if (j == 1) {
							int headerColspan = 7 + attribs.length;
			%>
			<tr><td colspan='<%=headerColspan%>'>&nbsp;</td></tr>
			<tr>
				<td  colspan='<%=headerColspan%>'>
					<div class='alert alert-success'>
						<span class='normalText'>
						Here is a list of requirements that were created from the Excel Import.
						</span>
					</div>
				</td>
			</tr>
			<tr><td colspan='<%=headerColspan%>'>&nbsp;</td></tr>
			<tr>
				<td class='tableHeader' width='350'>
					<span class='headingText'>
						Requirement
					</span>
				</td>
				<td class='tableHeader'>
					<span class='headingText'>
						Owner
					</span>
				</td>
				<td class='tableHeader'>
					<span class='headingText'>
						Percent Complete
					</span>
				</td>
				<td class='tableHeader'>
					<span class='headingText'>
						Priority
					</span>
				</td>
				<td class='tableHeader'>
					<span class='headingText'>
						Status
					</span>
				</td>
				<td class='tableHeader'>
					<span class='headingText'>
						Trace To
					</span>
				</td>
				<td class='tableHeader'>
					<span class='headingText'>
						Trace From
					</span>
				</td>

				<%
					// now to print the custom labels.
							for (int k = 0; k < attribs.length; k++) {
								String[] attrib = attribs[k].split(":#:");
				%>

				<td class='tableHeader'>
					<span class='headingText'>
						<%=attrib[0]%>
					</span>
				</td>
				<%
							}
				%>
			</tr>
			<%
						}

						// Now for each row in the array list, print the data out.
						if ((j % 2) == 1) {
							cellStyle = "normalTableCell";
						} else {
							cellStyle = "altTableCell";
						}
			%>
			<tr>
				
				<td class='<%=cellStyle%>'><span> 
				<%
					// lets put spacers here for child requirements.
					  String req = r.getRequirementFullTag();
				   	  int start = req.indexOf(".");
		    		  while (start != -1) {
		    	            start = req.indexOf(".", start+1);
							out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");				    	         
		  	          }
				%>
				<a
					href="#" onClick="
					displayFolderInExplorer(<%=r.getFolderId()%>);
		 			displayFolderContentCenterA(<%=r.getFolderId() %>);
		 			displayFolderContentRight(<%=r.getFolderId() %>);
					displayRequirement(<%=r.getRequirementId()%>)">
				<img src="/GloreeJava2/images/puzzle16.gif" border="0">
				&nbsp;<%=r.getRequirementFullTag()%> : <%=r.getRequirementNameForHTML()%></a>
				</span></td>
				<td class='<%=cellStyle%>'>
					<span class='normalText'>
						<%=r.getRequirementOwner()%>
					</td>
				<td class='<%=cellStyle%>'>
					<span class='normalText'>
						<%=r.getRequirementPctComplete()%> %
					</span>
				</td>
				<td class='<%=cellStyle%>'>
					<span class='normalText'>
						<%=r.getRequirementPriority()%>
					</span>
				</td>
				<td class='<%=cellStyle%>'>
					<span class='normalText'>
						<%=r.getApprovalStatus()%>
					</span>
				</td>
				<td class='<%=cellStyle%>'>
					<span class='normalText'>
						<%=r.getRequirementTraceTo()%>
					</span>
				</td>
				<td class='<%=cellStyle%>'>
					<span class='normalText'>
						<%=r.getRequirementTraceFrom()%>
					</span>
				</td>
				<%
						for (int k = 0; k < attribs.length; k++) {
							String[] attrib = attribs[k].split(":#:");
							// To avoid a array out of bounds exception where the attrib value wasn't filled in
							// we print the cell only if array has 2 items in it.
							String attribValue = "";
							if (attrib.length == 2) {
								attribValue = attrib[1];
							}
				%>
				<td class='<%=cellStyle%>'>
					<span class='normalText'>
						<%=attribValue%>
					</span>
				</td>
				<%
						}
				%>

			</tr>
			<%
					}
				}
				else {
			%>
			
				<tr><td >
					<span class='normalText'>
						Your import did not create any Requirements in the system. 
					</span>
				</td></tr>
			<%
			}
			%>

		</table>
		</div>
		</td>
	</tr>
</table>
</div>
<%
	}
%>