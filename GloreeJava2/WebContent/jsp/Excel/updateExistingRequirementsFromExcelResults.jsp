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
	ArrayList updatedRequirements = (ArrayList) request.getAttribute("updatedRequirements");
	ArrayList errorRows = (ArrayList) request.getAttribute("errorRows");
	
	// Lets see which custom attributes have been upgraded...
	// attributeIdString has all the attributes in this req type.
	String attributeIdString = request.getParameter("attributeIdString");
	ArrayList  mappedAttributeIds = new ArrayList();
	try {
		// remove the last # from attributeIdString.
		String[] attributeIds = {};
		
		if (attributeIdString.contains("::")) {
			attributeIdString = (String) attributeIdString.subSequence(0, attributeIdString.lastIndexOf("::"));
			attributeIds = attributeIdString.split("::");
			// lets iterate through all the attribute ids and see if any of them have been sent is  as a map.
			for (int k=0;k<attributeIds.length;k++){
				int attributeIdColumn = -1;
				try {
					attributeIdColumn = Integer.parseInt(request.getParameter(attributeIds[k]));
				} catch (Exception e) {
				}

				// do the validation only if the user chose to provide this
				// column of data
				if (attributeIdColumn > -1) {
				mappedAttributeIds.add(attributeIds[k]);								
				}
			}
		}
	} catch (Exception e) {
		// do nothing.
	}
	

%>


<div id='createRequirementsFromExcelResultsDiv' class='level1Box'>
<table class='paddedTable' width='100%'>
	<tr>
		<td align='left' bgcolor='#99CCFF'>				
			<span class='subSectionHeadingText'>
			Update Existing Requirements from Uploaded Excel File
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
					<tr><td colspan='2'>&nbsp;</td></tr>
					<tr> 
						<td colspan='2'>
							<div class='alert alert-success'>
								Here is a list of rows that had errors.
							</div> 
						</td>
					</tr>
					<tr><td colspan='2'>&nbsp;</td></tr>
					<tr>
						<td class='tableHeader'>
							<span class='sectionHeadingText'>
								Row Number
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
						String errorMessage = "";
						if (errorRow.length > 2){
							// the 3rd element exists
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

	<tr>
		<td>
		<div id='updatedRequirementsDiv' class='level2Box'>
		<table id="updatedRequirements">
			<%
				if (updatedRequirements != null) {
					Iterator i = updatedRequirements.iterator();
					int j = 0;
					String cellStyle = "normalTableCell";
					while (i.hasNext()) {
						Requirement r = (Requirement) i.next();
						// a typical uda looks like this 
						// Customer:#: SBI:##:Delivery Estimate:#:01/01/12
						String uda = r.getUserDefinedAttributes();
						String[] attribs = uda.split(":##:");
						j++;
						// for the first row, print the header and user defined columns etc..
						if (j == 1) {
							int headerColspan = 7 + attribs.length;
			%>
			<tr>
				<td class='tableHeader' width='350'>
					<span class='headingText'>
						Requirement
					</span>
				</td>
				<%if (request.getParameter("descriptionColumn") != null) { %>
					<td class='tableHeader'>
						<span class='headingText'>
							Description
						</span>
					</td>
				<%} %>
				<%if (request.getParameter("priorityColumn") != null) { %>
					<td class='tableHeader'>
						<span class='headingText'>
							Priority
						</span>
					</td>
				<%} %>
				<%if (request.getParameter("ownerColumn") != null) { %>
					<td class='tableHeader'>
						<span class='headingText'>
							Owner
						</span>
					</td>
				<%} %>
				<%if (request.getParameter("pctCompleteColumn") != null) { %>
					<td class='tableHeader'>
						<span class='headingText'>
							Percent Complete
						</span>
					</td>
				<%} %>
				<%if (request.getParameter("externalURLColumn") != null) { %>
					<td class='tableHeader'>
						<span class='headingText'>
							External URL
						</span>
					</td>
				<%} %>
				<%if (request.getParameter("traceToColumn") != null) { %>
					<td class='tableHeader'>
						<span class='headingText'>
							Trace To
						</span>
					</td>
				<%} %>
				<%if (request.getParameter("traceFromColumn") != null) { %>
					<td class='tableHeader'>
						<span class='headingText'>
							Trace From
						</span>
					</td>
				<%} %>
				<%if (request.getParameter("testingStatusColumn") != null) { %>
					<td class='tableHeader'>
						<span class='headingText'>
							Testing Status
						</span>
					</td>
				<%} %>
				<%
				// for each one of these attributes, lets print the header.
				Iterator m = mappedAttributeIds.iterator();
				while (m.hasNext()){
					int rTAttributeId = Integer.parseInt ((String) m.next());
					RTAttribute rTAttribute = new RTAttribute(rTAttributeId);
					%>
					<td class='tableHeader'>
						<span class='headingText'>
							<%=rTAttribute.getAttributeName() %>
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
				
				<td class='<%=cellStyle%>'>
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
				</td>
				<%if (request.getParameter("descriptionColumn") != null) { %>
					<td class='<%=cellStyle%>'>
					<span class='normalText'>
						<%=r.getRequirementDescription()  %>
					</span>
					</td>
				<%} %>
				<%if (request.getParameter("priorityColumn") != null) { %>
					<td class='<%=cellStyle%>'>
						<span class='normalText'>
							<%=r.getRequirementPriority() %> 
						</span>
					</td>
				<%} %>
				<%if (request.getParameter("ownerColumn") != null) { %>
					<td class='<%=cellStyle%>'>
						<span class='normalText'>
							<%=r.getRequirementOwner() %>
						</span>
					</td>
				<%} %>
				<%if (request.getParameter("pctCompleteColumn") != null) { %>
					<td class='<%=cellStyle%>'>
						<span class='normalText'>
							<%=r.getRequirementPctComplete() %>
						</span>
					</td>
				<%} %>
				<%if (request.getParameter("externalURLColumn") != null) { %>
					<td class='<%=cellStyle%>'>
						<span class='normalText'>
							<%=r.getRequirementExternalUrl() %>
						</span>
					</td>
				<%} %>
				<%if (request.getParameter("traceToColumn") != null) { %>				
					<td class='<%=cellStyle%>'>
						<span class='normalText'>
							<%=r.getRequirementTraceTo() %>
						</span>
					</td>
				<%} %>
				<%if (request.getParameter("traceFromColumn") != null) { %>				
					<td class='<%=cellStyle%>'>
						<span class='normalText'>
							<%=r.getRequirementTraceFrom() %>
						</span>
					</td>
				<%} %>
				<%if (request.getParameter("testingStatusColumn") != null) { %>				
					<td class='<%=cellStyle%>'>
						<span class='normalText'>
							<%=r.getTestingStatus() %>
						</span>
					</td>
				<%} %>
				<%
				// for each one of these attributes, lets print the header.
				Iterator m = mappedAttributeIds.iterator();
				while (m.hasNext()){
					int rTAttributeId = Integer.parseInt ((String) m.next());
					%>
					<td class='tableHeader'>
						<span class='headingText'>
							<%=r.getAttributeValue(rTAttributeId) %>
						</span>
					</td>	
					<%	
				}
				%>
				
			</tr>
			<%
					}
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