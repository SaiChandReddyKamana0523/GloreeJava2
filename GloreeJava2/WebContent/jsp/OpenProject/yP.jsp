<%@page contentType="text/html;charset=UTF-8"%>

<%
	// authentication only
	String yPIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((yPIsLoggedIn  == null) || (yPIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project project= (Project) session.getAttribute("project");
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean yPIsMember = false;
	SecurityProfile yPSecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (yPSecurityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		yPIsMember = true;
	}
	
	User user = yPSecurityProfile.getUser();
	
%>

<%
if (this.getServletContext().getInitParameter("installationType").equals("onSite")) {
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	String licenseString = this.getServletContext().getInitParameter("licenseString") ;
	if (licenseString == null){
		licenseString  = "";
	}
	boolean validLicenseString = ProjectUtil.isLicenseValid(licenseString);
	int daysSinceInstallation = ProjectUtil.daysSinceInstallation(databaseType);
	if (!(validLicenseString)){
		// this is an onsite instalaltion and does not have a valid license string
		if (daysSinceInstallation > 240){
			return;
		}
	}
}


%>


<%if (yPIsMember){
	
	// lets see if this user got here because he wanted to go directly to a requirement.
	String openFolder = "";
	String openFunction = "";
	String openObject = "";
	String displayTargetPageJSMethod = "";
	
	String dO = (String) session.getAttribute("dO");
	
	if (!((dO == null) || (dO.equals("")))){
		// this means that dO has a value , and we got here becase of a 
		// display Request.
		String displayRequirementIdString = (String) session.getAttribute("displayRequirementId");
	

		if (!((displayRequirementIdString == null) || (displayRequirementIdString.equals("")))){
			// this means that there is a displayRequirementId request.
			int displayRequirementId = Integer.parseInt((String) session.getAttribute("displayRequirementId"));
			int displayFolderId = Integer.parseInt( (String) session.getAttribute("displayFolderId"));
			int displayProjectId = Integer.parseInt( (String) session.getAttribute("displayProjectId"));
	
			
			openFolder =  "" +
				" 		tree.expandAll(); " +
				"		displayFolderInExplorer(" + displayFolderId + ") ; " +			
				" 		displayFolderContentCenterA(" + displayFolderId + ") ; " + 
				"		displayFolderContentRight(" + displayFolderId + "); "; 
				
			openObject = " displayRequirement("+ displayRequirementId  + ");  ";
			
			
			// remove the display Objects from the sesion.
			session.removeAttribute("displayRequirementId");
			session.removeAttribute("displayFolderId");
			session.removeAttribute("displayFolderId");
			session.removeAttribute("displayRequirementId");
		}
		
		
		String displayReportIdString = (String) session.getAttribute("displayReportId");
		
		if (!((displayReportIdString == null) || (displayReportIdString.equals("")))){
			// this means that there is a displayReportId request.
			int displayReportId = Integer.parseInt((String) session.getAttribute("displayReportId"));
			int displayFolderId = Integer.parseInt( (String) session.getAttribute("displayFolderId"));
			int displayProjectId = Integer.parseInt( (String) session.getAttribute("displayProjectId"));
			Report report = new Report(displayReportId);
	
			openFolder =  "" +
				" 		tree.expandAll(); " +
				" 		displayFolderInExplorer(" + displayFolderId + ") ; " +			
				" 		displayFolderContentCenterA(" + displayFolderId + ") ; " + 
				"		displayFolderContentRight(" + displayFolderId + "); "; 

			
			openObject = " displayExistingReport("+ displayFolderId + "," + displayReportId  + ",'" + report.getReportType()  +  "' );  ";
			
			//openObject = "displayFolderMetricsForm(" + displayFolderId + ");";				
			
			// remove the display Objects from the sesion.
			session.removeAttribute("displayRequirementId");
			session.removeAttribute("displayFolderId");
			session.removeAttribute("displayFolderId");
			session.removeAttribute("displayReportId");
		}
		
		
		String displayFunction = (String) session.getAttribute("displayFunction");
		
		
		if (!((displayFunction== null) || (displayFunction.equals("")))){
			if (displayFunction.equals("myDangling")){
				openFunction = "  myDanglingReqsDetailsForAProject_directDisplay();  ";
			}
			
			if (displayFunction.equals("myOrphan")){
				openFunction = " myOrphanReqsDetailsForAProject_directDisplay(); ";
			}
			
			if (displayFunction.equals("mySuspectUp")){
				openFunction = " mySuspectUpDetailsForAProject_directDisplay();; ";
			}	
			
			if (displayFunction.equals("mySuspectDown")){
				openFunction = " mySuspectDownDetailsForAProject_directDisplay();; ";
			}
			

			
			if (displayFunction.equals("myReqsPendingApproval")){
				openFunction = " myReqsPendingApprovalDetailsForAProject_directDisplay();; ";
			}
			if (displayFunction.equals("myReqsRejected")){
				openFunction = " myReqsRejectedDetailsForAProject_directDisplay();; ";
			}
			if (displayFunction.equals("myPendingApproval")){
				openFunction = " myPendingApprovalDetailsForAProject_directDisplay();; ";
			}
			
			if (displayFunction.equals("myIncomplete")){
				openFunction = " myIncompleteReqsDetailsForAProject_directDisplay();; ";
			}
			if (displayFunction.equals("myTestPending")){
				openFunction = " myTestPendingReqsDetailsForAProject_directDeisplay();; ";
			}
			if (displayFunction.equals("myTestFailed")){
				openFunction = " myTestFailedReqsDetailsForAProject_directDisplay();; ";
			}
			
			session.removeAttribute("displayProjectId");
			session.removeAttribute("displayFunction");
			
		}

	}	
	// lets see if the user got here because he / she wanted to import an excel file.
	String targetPage = (String) request.getAttribute("targetPage");
	if (!((targetPage == null) || (targetPage.equals("")))){
		int folderId = Integer.parseInt( (String) request.getAttribute("folderId"));
		
		if (targetPage.equals("createNewRequirementsFromExcelMapForm")){
			// if the request was to create new requierements.
			displayTargetPageJSMethod = " createNewRequirementsFromExcelMapForm("+ folderId  + ");  ";
		}
		else if (targetPage.equals("updateExistingRequirementsFromExcelMapForm")){
			// if the request was to update existing requierements.
			displayTargetPageJSMethod = " updateExistingRequirementsFromExcelMapForm("+ folderId  + ");  ";
		}
		else if (targetPage.equals("displayWordTemplate")){
			// if the request was to create Word Document, we point them to display Word Document
			// after it has been created in the db.
			WordTemplate wordTemplate = (WordTemplate) request.getAttribute("wordTemplate");
			displayTargetPageJSMethod = " displayWordTemplateAfterCreation("+ folderId  + "," + wordTemplate.getTemplateId() + ");  ";
		}
		else if (targetPage.equals("displayRequirementCoreAfterAttachment")){
			// if the request was to add an attachment to a requirement...
			// at this point we have the requirement id too.
			int requirementId = Integer.parseInt( (String) request.getAttribute("requirementId"));

			openFolder =  "" +
				" 		tree.expandAll(); " +	
				" 		displayFolderInExplorer(" + folderId + ") ; " +			
				" 		displayFolderContentCenterA(" + folderId + ") ; " + 
				"		displayFolderContentRight(" + folderId + "); "; 
				
			openObject = " displayRequirement("+ requirementId  + ");  ";
			
		}
		else if (targetPage.equals("createBulkRequirementsMessage")){
			openFolder =  "" +
			" 		tree.expandAll(); " +	
			" 		displayFolderInExplorer(" + folderId + ") ; " +			
			" 		displayFolderContentCenterA(" + folderId + ") ; " + 
			"		displayFolderContentRight(" + folderId + "); " +
			"   	createBulkRequirementForm(" + folderId + ");  ";			
		}
		
	}
	
	
	
	
	
	String defaultAction = "";
	if (
			(openFolder.equals(""))
			&&
			(openObject.equals(""))
			&&
			(displayTargetPageJSMethod.equals(""))
			&&
			(openFunction.equals(""))
		){
		// This is a default landing page. Example : Open a Project , or Return to the project
		defaultAction = "displayWizard();";
	}
			
	

%>
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

	<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
	<html>
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>TraceCloud - SAAS Agile Scrum Requirements Management - Collaborate, Define, Manage and Deliver your Customer Requirements</title>
 	 <meta name="description" content="Collaboration tools to define, manage and deliver your customer requirements on time and within budget. Significantly improves customer satisfaction ">
	<meta name="keywords" content="free requirements management, saas requirements management tool, online requirements management, doors, requisitepro, customer requirements, shared requirements, tl9000, project management, project requirements, agile, agile requirements management.">

	
	
	<!-- Individual YUI CSS files--> 
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/autocomplete/assets/skins/sam/autocomplete.css">	
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/reset-fonts-grids/reset-fonts-grids.css"> 
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/resize/assets/skins/sam/resize.css"> 
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/layout/assets/skins/sam/layout.css">
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/button/assets/skins/sam/button.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/menu/assets/skins/sam/menu.css"> 
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/treeview/assets/skins/sam/treeview.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/container/assets/skins/sam/container.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/editor/assets/skins/sam/simpleeditor.css" />
	
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/calendar/assets/skins/sam/calendar.css" />
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/js/yui.2.7.0/build/editor/assets/skins/sam/editor.css" />
	
	



	<!-- Individual YUI JS files --> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/yahoo-dom-event/yahoo-dom-event.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/animation/animation-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/dragdrop/dragdrop-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/element/element-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/button/button-min.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/resize/resize-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/layout/layout-min.js"></script> 
	
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/treeview/treeview-min.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/container/container-min.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/container/container_core-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/menu/menu-min.js"></script> 
	
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/utilities/utilities.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/calendar/calendar-min.js"></script>
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/editor/simpleeditor-min.js"></script>

	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/datasource/datasource-min.js"></script> 
	<script type="text/javascript" src="/GloreeJava2/js/yui.2.7.0/build/autocomplete/autocomplete-min.js"></script> 

	
	<!-- Gloree JS and CSS files -->
	
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/css/common.css"> 
	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	
	<script src="/GloreeJava2/js/userAccount.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userDashboard.js?v=20200630"></script>\
	
	
	<!--  Bootstratp  JS and CSS files -->

	

 	 <script src="/GloreeJava2/js/jquery-3.1.1.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap-tour-standalone.min.js"></script>
	
	 <link href="/GloreeJava2/css/bootstrap.min.css" rel="stylesheet" media="screen">
	
	 
	<link href="/GloreeJava2/css/bootstrap-tour-standalone.min.css" rel="stylesheet">
	 
	 
	 
	<!--  cdn for ckeditor 
	 <script src="https://cdn.ckeditor.com/4.6.2/standard/ckeditor.js"></script>
	 -->
	 <script src="https://cdn.ckeditor.com/4.6.2/full-all/ckeditor.js"></script>
    
	
	</head>
	
	<body class=" yui-skin-sam" style='background-color:white'> 

			
		
				<div id="myModal" class="modal" role="dialog"  >
				  <div class="modal-dialog">
				
				    <!-- Modal content-->
				    <div class="modal-content" style='width:1000px;'>
				      <div class="modal-header">
				        <button type="button" class="close" data-dismiss="modal">&times;</button>
				      </div>
				      <div class="modal-body" id='modalBody'>
				        
				      </div>
				      <div class="modal-footer">
				        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				      </div>
				    </div>
				
				  </div>
				</div>
			
				
						
				
								
		<div id="toolbar" style="background-color:white; height:\30px" >
			<%@ include file="oPToolbar.jsp" %>
		</div>
		
		<div id="explorer" style='background-color:white'>
			<%@ include file="oPExplorer.jsp" %>
		</div>
		<div id="contentCenter"  style='background-color:white'>
			<%@ include file="oPCenter.jsp" %>
		</div>
		<div id='Collapse'  style=' position: fixed;  bottom: 0;width: 300px;'>
	
			<a id='hideExplorer' href="#" class='btn  btn-info btn-xs' 
				style='color:white;'
					onclick="
						document.getElementById('hideExplorer').style.display='none';
						document.getElementById('showExplorer').style.display='block'
						layout.getUnitByPosition('left').collapse();
					"
				> Hide Explorer </a> 
				
				
		
			<a id='showExplorer' href="#" class='btn  btn-info btn-xs' 
				style='color:white; display:none'
					onclick="
						document.getElementById('hideExplorer').style.display='block';
						document.getElementById('showExplorer').style.display='none';
						layout.getUnitByPosition('left').expand();
					"
				> Show Explorer </a>
		</div>
	<script>



	
	//NOTE:  layout is a global variable that we will use to expand and collapse the lay out.
	var layout;
	(function() {
	    var Dom = YAHOO.util.Dom,
	        Event = YAHOO.util.Event;
	
	    Event.onDOMReady(function() {
		
	    	
	    	// lets make contentright small on projectopening.
	    	var contentRightSize  = 0;
			
	    	/*
	    	// lets figure out how big the content right should be.
			// first get the window size.
			
			var winW = 630, winH = 460;
			
			if (document.body && document.body.offsetWidth) {
			 winW = document.body.offsetWidth;
			 winH = document.body.offsetHeight;
			}
			if (document.compatMode=='CSS1Compat' &&
				document.documentElement &&
				document.documentElement.offsetWidth ) {
			 winW = document.documentElement.offsetWidth;
			 winH = document.documentElement.offsetHeight;
			}
			if (window.innerWidth && window.innerHeight) {
			 winW = window.innerWidth;
			 winH = window.innerHeight;
			}
			
			var contentRightSize = winW - 1100;
			if (contentRightSize < 250){
				contentRightSize = 250;
			}
			*/
		
	        layout = new YAHOO.widget.Layout({
	            units: [
	                { position: 'top', height: '70px', body: 'toolbar'},
	                { position: 'right',  width: contentRightSize, resize: true, gutter: '0px', footer: '', collapse: true, scroll: true, body: 'contentRight', animate: true },
	                { position: 'left',  width: 350, resize: true, body: 'explorer', gutter: '0px', collapse: true, scroll: true, animate: true },
	                { position: 'center', body: 'contentCenter', scroll:true }
	             ]
	        });
	        layout.on('render', function() {
	            layout.getUnitByPosition('left').on('close', function() {
	                closeLeft();
	            });
	        });
	        layout.render();


	        <%=openFunction%>
	        <%=openFolder%>
	        <%=openObject%>

	        <%=displayTargetPageJSMethod%>
	        <%=defaultAction%>
	        
	    });
	})();


		</script>
	</body>
	</html>

<%}%>