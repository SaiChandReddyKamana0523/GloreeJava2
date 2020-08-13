<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="com.gloree.beans.*" %>
<%
	// authentication only
	String oPCenterIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((oPCenterIsLoggedIn == null) || (oPCenterIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }

	Project oPCProject= (Project) session.getAttribute("project");
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean oPCIsMember = false;
	SecurityProfile oPCSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (oPCSecurityProfile.getRoles().contains("MemberInProject" + oPCProject.getProjectId())){
		oPCIsMember = true;
	}
	
	User oPCUser = oPCSecurityProfile.getUser();
	String oPCTargetJSP = "";
	// lets see if the user got here because he / she wanted to import an excel file.
	String oPCTargetPage = (String) request.getAttribute("targetPage");
	if (!((oPCTargetPage == null) || (oPCTargetPage.equals("")))){
		if (oPCTargetPage.equals("importFromExcelAnalysis")){
			oPCTargetJSP  = "importFromExcelAnalysis.jsp";	
		}
	}
	
	String reqViewPreference = "tabbed";
%>

<%if (oPCIsMember){%>





	<div id="myModalRequirement" class="modal" role="dialog" >
	  <div class="modal-dialog">
	
	    <!-- Modal content-->
	    <div class="modal-content" style='width:1000px'>
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal">&times;</button>
	      </div>
	      <div class="modal-body" id='modalBodyRequirement'>
	        
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	      </div>
	    </div>
	
	  </div>
	</div>

	
			
	
	
<table id='contentCenterTable' class='table borderless' >
	<tr>
		<td >
		<table class='table borderless' >
			<tr style='border:none'>
				<td colspan="2" style='border:none'>
								  
				</td>
			</tr>
			<tr style='border:none'>
				<td colspan="2" style='border:none'>
						<a name="TopOfContentCenterA" id="TopOfContentCenterA"></a>
						<a name="TopOfPage" id="TopOfPage"></a>
						<div id='projectSearchDiv' style='display:none' class='alert alert-info'>
							<table class='table'  style='width:400px'>
								<tr>
									<td style='border-top:none'>
											<input type="text" class="input-small" placeholder="Search by Id . Eg: BR-1" maxlength="300" size='30' name="reqIdSearchString" 
											id="reqIdSearchString" onfocus="this.value='';" onkeypress=" handleProjectSearchkeyPress(event, 'reqId');">
									</td>
									<td style='border-top:none'>
											<input id='searchByIdGo' type="button" onclick=" projectSearch('reqId');" value="Search" 
												class="btn btn-xs btn-outline-primary"  
												style="width:60px;border-color:blue; background-color:white; color:blue;"
											>
									</td>
								</tr>
								<tr>
									<td style='border-top:none'>
											<input type='text'  class='input-small' placeholder='Free Text Search'  maxlength='300' size='30' 
											name='googleSearchString' id='googleSearchString' style='visibility:visible'
											onfocus="this.value='';" onkeypress=" handleProjectSearchkeyPress(event, 'google'); " >
									</td>
									<td style='border-top:none'>
											<input id='searchByStringGo' type='button' onclick=" projectSearch('google'); "  value='Search' 
												class="btn btn-xs btn-outline-primary"  
												style="width:60px;border-color:blue;  background-color:white; color:blue;"
												>
									</td>
								</tr>
								<tr>
									<td colspan='2'>
										<button type="button" class="btn btn-xs btn-outline-danger"  
												style="width:60px;border-color:red; background-color:white;"
										onclick='document.getElementById("projectSearchDiv").style.display="none";'
										>Close</button>
									</td>
								</tr>
							</table>
						</div>
						<div id="contentCenterA" style="display:none;">	 
						</div> 
					</td>
			</tr>
			<%if (reqViewPreference.equals("tabbed")) { %>
				<tr style='border:none'>
					<td colspan="2" style='border:none'>
						<ul class="nav nav-tabs" id='reqTabs' style='display:none;'>
						  <li id='coreTab' class="active">
						  	<a   data-toggle="tab" href="#listContentsDiv"
							 		onclick='
							 			document.getElementById("contentCenterB").style.display="block";
										document.getElementById("contentCenterD").style.display="none";
										document.getElementById("contentCenterC").style.display="none";
										document.getElementById("contentCenterComments").style.display="none";
										document.getElementById("contentCenterAttachments").style.display="none";
									'
							>  
								<span class="glyphicon glyphicon-ok-sign " style=" color: lightblue; font-size:1.5em  " ></span> 
									&nbsp;&nbsp; Core  
								</a>
							</li>
						
						 	<li id='attributesTab' style='display:block'>
							  	<a data-toggle="tab" href="contentCenterD" 	
							  			onclick='
								  			document.getElementById("contentCenterB").style.display="none";
											document.getElementById("contentCenterD").style.display="block";
											document.getElementById("contentCenterC").style.display="none";
											document.getElementById("contentCenterComments").style.display="none";
											document.getElementById("contentCenterAttachments").style.display="none";
										'
										
								> 
									<span class="glyphicon glyphicon-list-alt " style=" color: lightblue; font-size:1.5em " ></span>
									 Attributes
								</a>
							</li>
							
							<li id='traceabilityTab' style='display:block'>
								  	<a data-toggle="tab" href="contentCenterD" 	
								  			onclick='
									  			document.getElementById("contentCenterB").style.display="none";
												document.getElementById("contentCenterD").style.display="none";
												document.getElementById("contentCenterC").style.display="block";
												document.getElementById("contentCenterComments").style.display="none";
												document.getElementById("contentCenterAttachments").style.display="none";
											'
											
									> 
										 <span class="glyphicon glyphicon-sort " style=" color: lightblue; font-size:1.5em " ></span>
										Traceability
									</a>
								</li>
								<li id='commentsTab' style='display:block'>
								  	<a data-toggle="tab" href="contentCenterD" 	
								  			onclick='
									  			document.getElementById("contentCenterB").style.display="none";
												document.getElementById("contentCenterD").style.display="none";
												document.getElementById("contentCenterC").style.display="none";
												document.getElementById("contentCenterComments").style.display="block";
												document.getElementById("contentCenterAttachments").style.display="none";
											'
											
									> 
										<span class="glyphicon glyphicon-comment " style=" color: lightblue; font-size:1.5em " ></span>
										 Comments
									</a>
								</li>
								<li id='attachmentsTab' style='display:block'>
								  	<a data-toggle="tab" href="contentCenterD" 	
								  			onclick='
									  			document.getElementById("contentCenterB").style.display="none";
												document.getElementById("contentCenterD").style.display="none";
												document.getElementById("contentCenterC").style.display="none";
												document.getElementById("contentCenterComments").style.display="none";
												document.getElementById("contentCenterAttachments").style.display="block";
											'
											
									> 
										<span class="glyphicon glyphicon-paperclip " style=" color: lightblue; font-size:1.5em " ></span>
										 Attachments
									</a>
								</li>
								<li id='allInOneTab' style='display:block'>
								  	<a data-toggle="tab" href="contentCenterD" 	
								  			onclick='
									  			document.getElementById("contentCenterB").style.display="block";
												document.getElementById("contentCenterD").style.display="block";
												document.getElementById("contentCenterC").style.display="block";
												document.getElementById("contentCenterComments").style.display="block";
												document.getElementById("contentCenterAttachments").style.display="block";
											'
											
									> 
										<span class="glyphicon glyphicon-fullscreen " style=" color: lightblue; font-size:1.5em " ></span>
										
										 All in One
									</a>
								</li>
						
					  </ul>				
					
					
					
							<div id="contentCenterB"  class="tab-pane fade in active" ></div>
							<div id="contentCenterD" style="display:none;" class="tab-pane fade in active" ></div>
							<div id="contentCenterC" style="display:none;" class="tab-pane fade in active"></div>
							<div id="contentCenterAttachments" style="display:none;" class="tab-pane fade in active" ></div>
							<div id="contentCenterComments" style="display:none;" class="tab-pane fade in active" ></div>
							
							
							<div id='createTracesDiv' 
								class='alert alert-success'
								style='display:none;' >
							</div>
							
					</td>
				</tr>
			<%}
			else {%>
				<tr style='border:none'>
					<td colspan="2" style='border:none'>
						<div id="contentCenterB" ">
					</td>
				</tr>
				
				<tr style='border:none'>
					<td  colspan="2" style='border:none' >
						<table width='100%'  >
							
							<tr>
							  <td valign="top"   >
									<div id="contentCenterD" style="display:none; "> </div>
								</td>
								
								<td valign="top"  >
									<div id='createTracesDiv' 
										class='alert alert-success'
										style='display:none;' >
									</div>
									<div id="contentCenterC" style="display:none; "> </div>
								</td>
								
								
							</tr>
						</table>
					</td>		
				</tr>
					
			<%
			}%>
			<tr style='border:none'>
				<td colspan="2"  style='border:none'>
						<div id="contentCenterE" > 
						</div>
					
				</td>
			</tr>	
			
			<tr style='border:none' >
				<td colspan="2" width='100%' style='border:none' ><div id="contentCenterF" style="display:none;" > </div> </td>
			</tr>	
			<tr style='border:none'>
				<td colspan="2" width='100%' style='border:none'><div id="contentCenterG" style="display:none;" > </div> </td>
			</tr>			

			
		</table>
	</td>
	<td valign='top'>
		<div id="newContentRight" style="display:block; "  > </div>
	</td>
</tr>
</table>
<%}%>