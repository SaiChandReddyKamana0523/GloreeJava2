<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


<%
	// authentication only
	String IsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((IsLoggedIn  == null) || (IsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)

	int fromFolderId = 0;	
	try {
		fromFolderId = Integer.parseInt(request.getParameter("fromFolderId"));
	}
	catch (Exception e){
		
	}
	int toFolderId = 0;	
	try {
		toFolderId = Integer.parseInt(request.getParameter("toFolderId"));
	}
	catch (Exception e){
	}
			

	String type = "";
	ArrayList attributes = new ArrayList() ;
	ArrayList baselines = new ArrayList();
	if (fromFolderId > 0 ){
		Folder fromFolder = new Folder(fromFolderId);
		type = "From";
		attributes = (ArrayList) ProjectUtil.getAllAttributes(fromFolder.getRequirementTypeId());
		baselines = ProjectUtil.getEligibleBaselinesForRequirementType(fromFolder.getRequirementTypeId());
	}
	
	if (toFolderId > 0 ){
		Folder toFolder = new Folder(toFolderId);
		type = "To";
		attributes = (ArrayList) ProjectUtil.getAllAttributes(toFolder.getRequirementTypeId());
		baselines = ProjectUtil.getEligibleBaselinesForRequirementType(toFolder.getRequirementTypeId());
	}	
	
	%>	
	<table   width="100%" align="center" class='paddedTable'>
		<tr>
			<td>
				<span class='normalText'>
						Display Header
						
					<select id='displayHeader<%=type %>' name='displayHeader<%=type %>'
						>
						
						<option value='name'>Name</option>
						<option value='description'>Description</option>
						
						<%
						
						if (attributes.size() > 0){
						
							// lets print the option list for custom attributes.
							Iterator j = attributes.iterator();
							while (j.hasNext()){
								RTAttribute a = (RTAttribute) j.next();
						%>
								<option value='<%=a.getAttributeName() %>'> <%=a.getAttributeName() %></option>
						<%
							}
						}
						%>					
					</select>
				</span>
			</td>
		</tr>
		<tr>
			<td >
				<div>
				<span class='normalText'>
					Filter criteria
					<select id='addAFilter<%=type %>' name='addAFilterFrom<%=type %>'
						onChange='addTraceMatrixFilterCondition("<%=type %>");'>
						<option value='selectAFilter'> </option>
						<option value='danglingFilter'>Dangling Requirements</option>
						<option value='orphanFilter'>Orphan Requirements</option>
						<option value='completedFilter'>Completed Requirements</option>
						<option value='incompleteFilter'>Incomplete Requirements</option>
						<option value='suspectUpStreamFilter'>Suspect UpStream Requirements</option>
						<option value='suspectDownStreamFilter'>Suspect DownStream Requirements</option>
						<option value='lockedFilter'>Locked Requirements</option>
						<option value='includeSubFoldersFilter'>Include Requirements in Subfolders</option>
						<option value=''></option>
						<option value='inBaselineFilter'>In Baseline</option>
						<option value='changedAfterBaselineFilter'>Changed After Baseline</option>
						<option value=''></option>
						<option value='nameFilter'>Name like </option>
						<option value='descriptionFilter'>Description like </option>
						<option value='ownerFilter'>Owner Email id like </option>
						<option value='externalURLFilter'>External URL like </option>
						<option value=''></option>
						<option value='pctCompleteFilter'>Percent Complete less than</option>
						<option value='statusFilter'>Approval Status in </option>
						<option value='priorityFilter'>Priority in </option>
						<option value='testingStatusFilter'>Testing Status in </option>
						<option value=''></option>
						<option value='approvedByFilter'>Approved By</option>
						<option value='rejectedByFilter'>Rejected By</option>
						<option value='approvalPendingByFilter'>Approval Pending By</option>
						<option value=''></option>
						<option value='traceToFilter'>Trace To</option>
						<option value='traceFromFilter'>Traces From</option>
						<%
						
						if (attributes.size() > 0){
						%>
							<option value=''></option>
						<%
							// lets print the option list for custom attributes.
							Iterator j = attributes.iterator();
							while (j.hasNext()){
								RTAttribute a = (RTAttribute) j.next();
						%>
								<option value='customA<%=a.getAttributeId()%>Div'> <%=a.getAttributeName() %></option>
						<%
							}
						}
						%>					
					</select>
				</span>
				</div>
				
				
				
				<div id="danglingFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("danglingSearch<%=type %>").checked = false;
						document.getElementById("danglingFilterDiv<%=type %>").style.display="none";
						'>
	 				<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					<span class='headingText'> Dangling (No Downstream) Requirements</span>
					</td><td>
					<input type='checkbox' name='danglingSearch<%=type %>' id = 'danglingSearch<%=type %>' >
					</td></tr></table>
				</div>
				
				<div id="orphanFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("orphanSearch<%=type %>").checked = false;
						document.getElementById("orphanFilterDiv<%=type %>").style.display="none";
						'>
	 				<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					<span class='headingText'> Orphan (No Upstream) Requirements</span>
					</td><td>
					<input type='checkbox' name='orphanSearch<%=type %>' id = 'orphanSearch<%=type %>' >
					</td></tr></table>
				</div>
				
				<div id="completedFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("completedSearch<%=type %>").checked = false;
						document.getElementById("completedFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					<span class='headingText'> Completed Requirements</span>
					</td><td>
					<input type='checkbox' name='completedSearch<%=type %>' id = 'completedSearch<%=type %>' >
					</td></tr></table> 
				</div>

				<div id="incompleteFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("incompleteSearch<%=type %>").checked = false;
						document.getElementById("incompleteFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					<span class='headingText'> Incomplete Requirements</span>
					</td><td>
					<input type='checkbox' name='incompleteSearch<%=type %>' id = 'incompleteSearch<%=type %>'  >
					</td></tr></table> 	
				</div>
						
				<div id="suspectUpStreamFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("suspectUpStreamSearch<%=type %>").checked = false;
						document.getElementById("suspectUpStreamFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					<span class='headingText'> Requirements with Suspect Up Stream</span>
					</td><td>
					<input type='checkbox' name='suspectUpStreamSearch<%=type %>' id = 'suspectUpStreamSearch<%=type %>'>
					</td></tr></table>
				</div> 	
				
								
				<div id="suspectDownStreamFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("suspectDownStreamSearch<%=type %>").checked = false;
						document.getElementById("suspectDownStreamFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					<span class='headingText'> Requirements with Suspect  Down Stream </span>
					</td><td>
					<input type='checkbox' name='suspectDownStreamSearch<%=type %>' id = 'suspectDownStreamSearch<%=type %>'>
					</td></tr></table>
				</div> 	

				<div id="lockedFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("lockedSearch<%=type %>").checked = false;
						document.getElementById("lockedFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					<span class='headingText'> Show Only locked Requirements</span>
					</td><td>
					<input type='checkbox' name='lockedSearch<%=type %>' id = 'lockedSearch<%=type %>'>
					</td></tr></table>				
				</div>
													
				<div id="includeSubFoldersFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("includeSubFoldersSearch<%=type %>").checked = false;
						document.getElementById("includeSubFoldersFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					<span class='headingText'> Include Requirements in Sub Folders</span>
					</td><td>
					<input type='checkbox' name='includeSubFoldersSearch<%=type %>' id = 'includeSubFoldersSearch<%=type %>'>
					</td></tr></table>				
				</div>

				<div id="inRTBaselineFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("inRTBaselineSearch<%=type %>").selectedIndex= null;
						document.getElementById("inRTBaselineFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					<span class='headingText'> 
					Requirements In Baseline 
					<img src="/GloreeJava2/images/baseline16.png" border="0">
					</span>
					</td><td>
						<span class='headingText'> 
						<select name="inRTBaselineSearch<%=type %>" id='inRTBaselineSearch<%=type %>'>
							<option value='-1'></option>
							<%
							Iterator m = baselines.iterator();
							while (m.hasNext()){
								RTBaseline rTBaseline = (RTBaseline) m.next();
							%>
								<option value='<%=rTBaseline.getBaselineId()%>'  >
									<%=rTBaseline.getBaselineName() %>
									<%if (rTBaseline.getLocked() == 1 ){ %>
										(Locked)
									<%}
									else { %>
										(Unlocked)
									<%} %>
								</option>
							<%	
							}
							%>	
						</select>
						</span>
					</td></tr></table>
				</div>
				
				<div id="changedAfterRTBaselineFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("changedAfterRTBaselineSearch<%=type %>").selectedIndex= null;
						document.getElementById("changedAfterRTBaselineFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					<span class='headingText'> 
						Requirements changed After Baseline
						<img src="/GloreeJava2/images/baseline16.png" border="0">
					</span>
					</td><td>
					<span class='headingText'> 
						<select name="changedAfterRTBaselineSearch<%=type %>" id='changedAfterRTBaselineSearch<%=type %>'>
							<option value='-1'></option>
							<%
							Iterator n = baselines.iterator();
							while (n.hasNext()){
								RTBaseline rTBaseline = (RTBaseline) n.next();
								
							%>
								<option value='<%=rTBaseline.getBaselineId()%>'   >
									<%=rTBaseline.getBaselineName() %>
									<%if (rTBaseline.getLocked() == 1 ){ %>
										(Locked)
									<%}
									else { %>
										(Unlocked)
									<%} %>
								</option>
							<%	
							}
							%>	
						</select>
						</span>
					</td></tr></table>
				</div>



				<div id="nameFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("nameSearch<%=type %>").value= "";
						document.getElementById("nameFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					<span class='headingText'> 
					Name like
					</span>
					</td><td>
					<span class='headingText'> 
					<input type="text"  name="nameSearch<%=type %>" id="nameSearch<%=type %>" size="25"
				 			maxlength="100" >
					</span>
					</td></tr></table>
				</div>

				<div id="descriptionFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("descriptionSearch<%=type %>").value= "";
						document.getElementById("descriptionFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					<span class='headingText'> 
					Description like
					</span>
					</td><td>
					<span class='headingText'> 
					<input type="text"  name="descriptionSearch<%=type %>" id="descriptionSearch<%=type %>" size="25"
							 maxlength="100" > 				
					</span>
					</td></tr></table>
				</div>
				
				<div id="ownerFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("ownerSearch<%=type %>").value= "";
						document.getElementById("ownerFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					<span class='headingText'> Owner Email Id like</span>
					</td><td>
					<span class='headingText'> 
					<input type="text"   name="ownerSearch<%=type %>" id="ownerSearch<%=type %>" size="25"
							 maxlength="100" > 				
					</span>
					</td></tr></table>
				</div>
						
				<div id="externalURLFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("externalURLSearch<%=type %>").value= "";
						document.getElementById("externalURLFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					<span class='headingText'> External URL like</span>
					</td><td>
					<span class='headingText'> 
					<input type="text"  name="externalURLSearch<%=type %>" id="externalURLSearch<%=type %>" size="25"
							 maxlength="100" > 
					</span>
					</td></tr></table>
				</div>
	
					
				<div id="pctCompleteFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("pctCompleteSearch<%=type %>").value= "";
						document.getElementById("pctCompleteFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					<span class='headingText'> Completed less than</span>
					</td><td>
					<span class='headingText'> 
					<input type="text"  name="pctCompleteSearch<%=type %>" id="pctCompleteSearch<%=type %>" size="3" maxlength="3" > %
					</span>
					</td></tr></table>
				</div>
				
								
				<div id="statusFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("statusSearch<%=type %>").selectedIndex= "-1";
						document.getElementById("statusFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					
					<span class='headingText'> Approval Status in (Ctrl+Click to select)</span>
					</td><td>
					<span class='headingText'> 
					<select MULTIPLE SIZE='3' name="statusSearch<%=type %>" id="statusSearch<%=type %>">
						<option value="Draft"  >Draft</option>
						<option value="In Approval WorkFlow"    >In Approval WorkFlow</option>
						<option value="Approved"   >Approved</option>
						<option value="Rejected"   >Rejected</option>
					</select>
					</span>
					</td></tr></table>
				</div>
								
				<div id="priorityFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("prioritySearch<%=type %>").selectedIndex= "-1";
						document.getElementById("priorityFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>

					<span class='headingText'> Priority in (Ctrl+Click to select)</span>
					</td><td>
					<span class='headingText'> 
					<select MULTIPLE SIZE='3' name="prioritySearch<%=type %>" id="prioritySearch<%=type %>">
						<option value="High" >High</option>
						<option value="Medium" >Medium</option>
						<option value="Low" >Low </option>
					</select>
					</span>
					</td></tr></table>
				</div>
								
				<div id="testingStatusFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("testingStatusSearch<%=type %>").selectedIndex= "-1";
						document.getElementById("testingStatusFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>

					<span class='headingText'> Testing Status in (Ctrl+Click to select)</span>
					</td><td>
					<span class='headingText'> 
					<select MULTIPLE SIZE='3' name="testingStatusSearch<%=type %>" id="testingStatusSearch<%=type %>">
						<option value="Pending" >Pending</option>
						<option value="Pass" >Pass</option>
						<option value="Fail" >Fail</option>
					</select>
					</span>
					</td></tr></table>
				</div>
											
							
				<div id="approvedByFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("approvedBySearch<%=type %>").value= "";
						document.getElementById("approvedByFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>

					<span class='headingText'> Approved By (Email Id)</span>
					</td><td>
					<span class='headingText'> 
					<input type="text"  name="approvedBySearch<%=type %>" id="approvedBySearch<%=type %>" size="25"
							 maxlength="100" > 				
					</span>
					</td></tr></table>
				</div>

				<div id="rejectedByFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("rejectedBySearch<%=type %>").value= "";
						document.getElementById("rejectedByFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>

					<span class='headingText'> Rejected By (Email Id)</span>
					</td><td>
					<span class='headingText'> 
					<input type="text"  name="rejectedBySearch<%=type %>" id="rejectedBySearch<%=type %>" size="25"
							 maxlength="100" >
					</span>
					</td></tr></table> 			
				</div>
								
				<div id="pendingByFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("pendingBySearch<%=type %>").value= "";
						document.getElementById("pendingByFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>

					<span class='headingText'> Approval Pending By (Email Id)</span>
					</td><td>
					<span class='headingText'> 
					<input type="text"  name="pendingBySearch<%=type %>" id="pendingBySearch<%=type %>" size="25"
							 maxlength="100" > 	
					</span>
					</td></tr></table>
				</div>
								
				<div id="traceToFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("traceToSearch<%=type %>").value= "";
						document.getElementById("traceToFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>
	
					<span class='headingText'> Trace To (eg : BR-1)</span>
					</td><td>
					<span class='headingText'> 
					<input type="text"  name="traceToSearch<%=type %>" id="traceToSearch<%=type %>" size="25"
							 maxlength="100" > 	 				
					</span>
					</td></tr></table>
				</div>
								
				<div id="traceFromFilterDiv<%=type %>" style="display:none;">
					<table><tr> <td width='230'>
					<a href="#" 
					onClick='
						document.getElementById("traceFromSearch<%=type %>").value= "";
						document.getElementById("traceFromFilterDiv<%=type %>").style.display="none";
						'>
					<img src="/GloreeJava2/images/delete16.png" border="0"></a>
					
					<span class='headingText'> Trace From (eg : TR-1)</span>
					</td><td>
					<span class='headingText'> 
					<input type="text"  name="traceFromSearch<%=type %>" id="traceFromSearch<%=type %>" size="25"
							 maxlength="100" >
					</span>
					</td></tr></table> 	 			
				</div>
				
						
						
					<!--  Lets display the custom attributes for filtering. -->		
					<% 	 
				
					String attributeIdString = "";
					if (attributes != null) {
						Iterator i = attributes.iterator();
						while (i.hasNext()){
							RTAttribute a = (RTAttribute) i.next();
							String divId = "customA" + a.getAttributeId() + "Div";
							if (a.getAttributeType().equals("Drop Down")){
								attributeIdString += "customA" + a.getAttributeId() + type + "#DropDown##";
								%>
									<div id="<%=divId%><%=type %>" style="display:none;">
									<table><tr> <td width='230'>
									<a href="#" 
									onClick='
										document.getElementById("customA<%=a.getAttributeId()%><%=type %>").selectedIndex= "-1";
										document.getElementById("<%=divId%><%=type %>").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> <%=a.getAttributeName()%> <br>(Ctrl+Click to Select)</span> 
									</td>
									<td>
										<span class='headingText'> 
										<select MULTIPLE SIZE='3' name='<%=a.getAttributeId()%><%=type %>'  id='customA<%=a.getAttributeId()%><%=type %>'>
										<%
										
										int parentAttributeId = a.getParentAttributeId();
										String [] o = a.getAttributeDropDownOptions().split(",");
									
										for (int j=0 ; j < o.length; j++){
											String optionName = o[j];
											if (parentAttributeId > 0){
												// this is a child attribute and the attribute values are like Porsche:911,Porsche:Panamera,Porsche:Carrera
												// where Porsche is the parent attribute value and 911,Panamera and Carrera are the potential child values.
												// so we need to strip out the first portion .
												if ((optionName != null) && (optionName.contains(":"))){
													String [] oN = optionName.split(":");
													optionName = oN[1];
												}
											}

											%>
												<option value='<%=optionName%>' ><%=optionName%></option>
											<%
										}
										%>
										</select>
										</span>
									</td> </tr></table>
									</div>
								<%
							}
							else{
								attributeIdString += "customA" + a.getAttributeId() + type +  "#Text##";
								%>
										<div id="<%=divId%><%=type %>" style="display:none;">
									<table><tr> <td width='230'>
									<a href="#" 
									onClick='
										document.getElementById("customA<%=a.getAttributeId()%><%=type %>").value= "";
										document.getElementById("<%=divId%><%=type %>").style.display="none";
										'>
									<img src="/GloreeJava2/images/delete16.png" border="0"></a>
									<span class='headingText'> <%=a.getAttributeName()%></span> 
									</td>
									<td>
										<span class='headingText'> 
										<input type="text"  name="<%=a.getAttributeId()%><%=type %>" id="customA<%=a.getAttributeId()%><%=type %>" size="25"
										 maxlength="100" >
										 </span>
									</td></tr></table>
									</div>
								<%				
							}
								
						}
					}
						
					%>
					<input type='hidden' name='attributeIdString<%=type %>' id='attributeIdString<%=type %>'  value='<%=attributeIdString%>'>							
						



			</td>
		</tr>
	</table>
