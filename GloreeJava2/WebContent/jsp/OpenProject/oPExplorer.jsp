<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String oPExplorerIsLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((oPExplorerIsLoggedIn == null) || (oPExplorerIsLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	Project oPEProject= (Project) session.getAttribute("project");
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean oPEIsMember = false;
	SecurityProfile oPESecurityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	if (oPESecurityProfile.getRoles().contains("MemberInProject" + oPEProject.getProjectId())){
		oPEIsMember = true;
	}
	
	User oPEUser = oPESecurityProfile.getUser();
	
	if (oPEIsMember){
			Project explorerProject= (Project) session.getAttribute("project"); 
			ArrayList myFolders = explorerProject.getMyFolders(); 
			Iterator i = myFolders.iterator();
		
			String tree = "\n var parentNode = '';";
			while (i.hasNext()){
				Folder folder = (Folder) i.next();
				if (folder.getFolderLevel() == 1 ){
				
					
					tree += "\n\n\n var myNodeData = { " +
							" label: '" + folder.getFolderName() + " ("+ folder.getCumulativeCountOfRequirements() +")', " +
							" title: ' Mouse Right Click to see options', " +
							" id:'" + folder.getFolderId() +"' }; ";
					tree += "\n var tmpNode = new YAHOO.widget.TextNode(myNodeData, tree.getRoot(), false);";
					tree += "\n tmpNode.labelStyle = 'icon-folder'; ";
				}
				else {
					
					tree += "\n\n var myNodeData = { " +
					" label: '" + folder.getFolderName() + " ("+ folder.getCumulativeCountOfRequirements() +")', " +
					" title: ' Mouse Right Click to see options', " +
					" id: '" + folder.getFolderId() +"' }; ";
					tree += "\n parentNode = tree.getNodeByProperty('id','"+ folder.getParentFolderId() +"');";
					tree += "\n var tmpNode = new YAHOO.widget.TextNode(myNodeData, parentNode , false);";
					tree += "\n tmpNode.labelStyle = 'icon-folder'; ";
				}
				
		        // save the element id for Tooltip
		        tree += "\n contextElements.push(tmpNode.labelElId);";
		        
		     	// we set the folderNodeMap to this element's labelElId, so that we can figure out
		        // what the folder id of the node is for right click menus.
		        tree += "\n folderNodeMap[tmpNode.labelElId] = '"+ folder.getFolderId()  +"';";
			}
			//Now we add the virtual Deleted Folders.
			// First we add the main deleted folder.
			// NOTE : we normally send the folderID as the id of the node. But if it's a virtual folder, it start with -1
			// and followed by the Requirement Type Id after :, instead of the usual folder id.
			tree += "\n\n\n var myNodeData = { " +
							" label: 'RECYCLE BIN', " +
							" title: 'All Soft Deleted Requirements that can still be restored', " +
							" id:'-1:0' }; ";
			tree += "\n var deletedRequirementsNode = new YAHOO.widget.TextNode(myNodeData, tree.getRoot(), false);";
			tree += "\n deletedRequirementsNode.labelStyle = 'icon-folder'; ";
		    // save the element id for Tooltip
		    tree += "\n contextElements.push(deletedRequirementsNode.labelElId);";
		 	// we set the folderNodeMap to this element's labelElId, so that we can figure out
		    // what the folder id of the node is for right click menus.
		    tree += "\n folderNodeMap[deletedRequirementsNode.labelElId] = '-1:0';";
		
			// Now for each requirement type we add a virtual folder.
			
			ArrayList myRequirementTypes = explorerProject.getMyRequirementTypes();
			Iterator j = myRequirementTypes.iterator();
		
			while (j.hasNext()){
				RequirementType rT = (RequirementType) j.next();
				tree += "\n\n\n var myNodeData = { " +
					" label: '" + rT.getRequirementTypeName()   + "', " +
				" title: '" + rT.getRequirementTypeDescription() + "', " +
				" id:'-1:" + rT.getRequirementTypeId() +"' }; ";
				tree += "\n var tmpNode = new YAHOO.widget.TextNode(myNodeData, deletedRequirementsNode, false);";
				tree += "\n tmpNode.labelStyle = 'icon-folder'; ";
				
				// save the element id for Tooltip
		        tree += "\n contextElements.push(tmpNode.labelElId);";
		        // we set the folderNodeMap to this element's labelElId, so that we can figure out
		        // what the folder id of the node is for right click menus.
		        tree += "\n folderNodeMap[tmpNode.labelElId] = '-1:" + rT.getRequirementTypeId() +"';";
			}
%>
	

	<style>
	#expandcontractdiv {border:1px dotted #dedede; background-color:#EBE4F2; margin:0 0 .5em 0; padding:0.4em;}
	#treeDiv1 { padding:1em; margin-top:1em; }
	
	
	#icon-folder {
		background:url(/GloreeJava2/images/folder.png);
	}
	
	
	</style>
	
	<div id='explorerDiv' class='invisibleLevel1Box'>
		<table onMouseOver='
			if (document.getElementById("folderMenuNoReqsCreateNewDiv") != null) {document.getElementById("folderMenuNoReqsCreateNewDiv").style.display="none"; }
			if (document.getElementById("requirementActionDiv") != null) {document.getElementById("requirementActionDiv").style.display="none"; }
			' style='width:100%'>
		
		<tr> 
			<td>
				
				
				<span class='normalText'>
				<div id="showFoldersDiv" style="background-color:#EBE4F2; display:none" onmouseout="this.style.background='#EBE4F2';" onmouseover="this.style.background='#E5EBFF';">	
					
					<table class="paddedTable"  border="0" width="370px"><tbody>
						<tr>
							<td  align="left">				
							<a onclick="
								document.getElementById('showFoldersDiv').style.display='none';
								document.getElementById('contentRight').style.display='none';
								document.getElementById('treeDiv1').style.display='block';
							" href="#" id="showFoldersDiv"> &nbsp; <img src="/GloreeJava2/images/folders_explorer.png"  border="0"> &nbsp;Show Folders</a>								
							</td>
						</tr></tbody>
					</table>
					
				</div>
				

				<div>
				
				<ul class="nav nav-tabs">
				  <li class="active">
				  	<a  id='folderTab' data-toggle="tab" href="#explorerTabDiv"
					 		onclick='
					 			document.getElementById("explorerTabDiv").style.display="block";
								document.getElementById("objectsTabDiv").style.display="none";
								
								document.getElementById("contentCenterB").style.display="none";
								document.getElementById("contentCenterC").style.display="none";
								document.getElementById("contentCenterD").style.display="none";
								document.getElementById("contentCenterE").style.display="none";
								document.getElementById("contentCenterF").style.display="block";
								document.getElementById("contentCenterComments").style.display="none";
								document.getElementById("contentCenterAttachments").style.display="none";
								document.getElementById("reqTabs").style.display = "none";
								
								
							'
					>Folders </a></li>
				  <li>
				  	<a id='objectTab' data-toggle="tab" href="#objectsTabDiv" 	
				  			onclick='
					 			document.getElementById("explorerTabDiv").style.display="none";
								document.getElementById("objectsTabDiv").style.display="block";
							'
					>Objects</a></li>
				  
				 <li>
				  
				</ul>				
				
							
					<div id='explorerTabDiv' class="tab-pane fade in active" >
						
						<div id='expandDiv' style='display:block;' >
							<br>
							<a  id='expandButton' href="#" 
								id="expand"
								onclick='expandFolders()'
							>Expand Folders</a>
						</div>
					
						
						<div id='collapseDiv' style='display:none'>
							<br>
							<a id="collapse" href="#" 
								onclick='collapseFolders()'
							> Collapse Folders </a>
						</div>
						<div id="treeDiv1"></div>
					 </div>
						
					<div id='objectsTabDiv' class="tab-pane fade in ">
						<div id='folderContentDiv' class='alert alert-div' style='display:none; overflow:scroll; '></div>
					 </div>
						
				</div>
				
					
				
				
				
				<!--  DO NOT DELTE BELOW DIV. USED TO GIVE SPACING FOR THE SCROLL BAR -->
				<div id="fillerExplorerDiv" style="background-color:white">
					<table width='100%'>
						<tr><td height='500px'> &nbsp; </td></tr>
					</table>
				</div>
				</span>
			</td>
		</tr>
		</table>
	</div>
	
	
	<script type="text/javascript">
	
		var tree; //will hold our TreeView instance
		var  contextElements = []; // will hold our tooltip object and an arry of context elements this tooltip will respond to.
	
	    var currentFolderNodeId = null; // at any point has the currentTextNode value. Set by the triggerListener.
		var folderNodeMap = {}; // has the list of all text nodes in this tree. Will be used by the context menu.
		
		function treeInit() {
			//instantiate the tree:
			
			document.getElementById("treeDiv1").innerHTML= "&nbsp;";
			tree = new YAHOO.widget.TreeView("treeDiv1");
			<%=tree%>
			//once it's all built out, we need to render
			//our TreeView instance:
			tree.draw();			
		
			
			
		
		}
		
		
		//When the DOM is done loading, we can initialize our TreeView
		//instance:
		//YAHOO.util.Event.onDOMReady(treeInit);
		YAHOO.util.Event.onContentReady("treeDiv1",treeInit());
	
	
		// context menu handling code.
		function expandFolders() {
			document.getElementById("expandDiv").style.display="none";
			document.getElementById("collapseDiv").style.display="block";
			
			tree.expandAll();
			YAHOO.util.Event.preventDefault(e);
		
		}	
	
		
		function collapseFolders() {
			document.getElementById("expandDiv").style.display="block";
			document.getElementById("collapseDiv").style.display="none";
			tree.collapseAll();
			YAHOO.util.Event.preventDefault(e);
		
		}	
			
		// preFocusFolderId is a global variable we use to track which folder the user last focused on
		// this is used to set it to a gray color. Used in the setFocusOnFolder method.
		var prevFocusFolderId = '';
		function setFocusOnFolder() {
			// if the previous folderis not null, set it's background to gray.
			var prevFocusFolder = document.getElementById(prevFocusFolderId); 
			 if (prevFocusFolder != null){
				prevFocusFolder.style.backgroundColor = "white";
			 }
			 // set the background of the current focus folder to blue
			 document.getElementById(currentFolderNodeId).style.backgroundColor = "lightpink";
			 // now set the global variable so that the current req becomes the next previous req.
			prevFocusFolderId = currentFolderNodeId;
			
		}
			
		function openFolderContextHandler() {
		
			// for best screen real estate management , collapse folder
			//document.getElementById('showFoldersDiv').style.display='block';
			//document.getElementById('contentRight').style.display='block';
			//document.getElementById('expandcontractdiv').style.display='block';
			//document.getElementById('treeDiv1').style.display='block';
		
			
			// at the beginning of any menu op, call the setFocusOnFolder to set / unset the focus color
			setFocusOnFolder();
			var folderId = folderNodeMap[currentFolderNodeId];
			
			// note : we are seeing this situation where occassionally, the folderId isn't coming through and the system blows up
			// so we are putting this condition. 
			if (folderId == 0) {
				return;
			}
			// if folderId has -1 in it, then it's a virtual folders.
			// you can only display and view report on Virtual folders.
			// NOTE : Because Javascript is anal about strings and numbers
			// .IndexOf fails when pos is a number. So add "" to convert the number to string.
			folderId = folderId + "";
			var pos=folderId.indexOf("-1");
			if (pos  < 0){
				// lets display the dashboards in contentCenterB
				//displayFolderMetricsForm(folderId);
				//
				/// note : earlier, we were not displaying
				//displayFolderMetricsDataTableOnly(folderId);
				// real folder
				displayFolderContentsInExplorerInBackground(folderId)
				displayAllRequirementsInRealFolder(folderId);
				displayFolderContentCenterA(folderId) ;
				
				refreshExplorer(folderId)

				$('#folderTab').trigger('click');
			}
			else {
				displayAllRequirementsInRealFolder(folderId);
				displayFolderContentCenterA(folderId) ;
			}
		}
	
		function editFolderContextHandler() {
			// at the beginning of any menu op, call the setFocusOnFolder to set / unset the focus color
			setFocusOnFolder();
			var folderId = folderNodeMap[currentFolderNodeId];
			// if folderId has -1 in it, then it's a virtual folders.
			// you can only display and view report on Virtual folders.
			// NOTE : Because Javascript is anal about strings and numbers
			// .IndexOf fails when pos is a number. So add "" to convert the number to string.
			folderId = folderId + "";
			var pos=folderId.indexOf("-1");
			if (pos  < 0){

				displayFolderInExplorer(folderId);
				displayFolderContentCenterA(folderId) ;
				//displayFolderContentRight(folderId);				
				editFolderForm(folderId);	
			}
			else {
				openFolderContextHandler();
			}
		}

		
		
		function savedContextHandler() {
			// at the beginning of any menu op, call the setFocusOnFolder to set / unset the focus color
			setFocusOnFolder();
			var folderId = folderNodeMap[currentFolderNodeId];
			// if folderId has -1 in it, then it's a virtual folders.
			// you can only display and view report on Virtual folders.
			// NOTE : Because Javascript is anal about strings and numbers
			// .IndexOf fails when pos is a number. So add "" to convert the number to string.
			folderId = folderId + "";
			var pos=folderId.indexOf("-1");
			if (pos  < 0){
				

				displayFolderInExplorer(folderId);
				displayFolderContentCenterA(folderId) ;
				//displayFolderContentRight(folderId);
				displaySavedReportsAndTemplates(folderId);
					
			}
			else {
				openFolderContextHandler();
			}
		}

		
		function folderPermissionsContextHandler() {
			// at the beginning of any menu op, call the setFocusOnFolder to set / unset the focus color
			setFocusOnFolder();
			var folderId = folderNodeMap[currentFolderNodeId];
			// if folderId has -1 in it, then it's a virtual folders.
			// you can only display and view report on Virtual folders.
			// NOTE : Because Javascript is anal about strings and numbers
			// .IndexOf fails when pos is a number. So add "" to convert the number to string.
			folderId = folderId + "";
			var pos=folderId.indexOf("-1");
			if (pos  < 0){

				displayFolderInExplorer(folderId);
				displayFolderContentCenterA(folderId) ;
				//displayFolderContentRight(folderId);				
				displayFolderCore(folderId);	
			}
			else {
				openFolderContextHandler();
			}
		}

		
		
		function folderDsahboardContextHandler() {
			// at the beginning of any menu op, call the setFocusOnFolder to set / unset the focus color
			setFocusOnFolder();
			var folderId = folderNodeMap[currentFolderNodeId];
			// if folderId has -1 in it, then it's a virtual folders.
			// you can only display and view report on Virtual folders.
			// NOTE : Because Javascript is anal about strings and numbers
			// .IndexOf fails when pos is a number. So add "" to convert the number to string.
			folderId = folderId + "";
			var pos=folderId.indexOf("-1");
			if (pos  < 0){

				displayFolderInExplorer(folderId);
				displayFolderContentCenterA(folderId) ;
				//displayFolderContentRight(folderId);				
				displayFolderMetrics(folderId);	
			}
			else {
				openFolderContextHandler();
			}
		}
		
		function deleteFolderFormContextHandler() {
			// at the beginning of any menu op, call the setFocusOnFolder to set / unset the focus color
			setFocusOnFolder();
			var folderId = folderNodeMap[currentFolderNodeId];
			// if folderId has -1 in it, then it's a virtual folders.
			// you can only display and view report on Virtual folders.
			
			// NOTE : Because Javascript is anal about strings and numbers
			// .IndexOf fails when pos is a number. So add "" to convert the number to string.
			folderId = folderId + "";
			var pos=folderId.indexOf("-1");
			if (pos < 0){

				displayFolderInExplorer(folderId);
				displayFolderContentCenterA(folderId) ;
				//displayFolderContentRight(folderId);
				deleteFolderForm(folderId);	
			}
			else {
				openFolderContextHandler();
			}
		}
	
		function createSubFolderContextHandler() {
			// at the beginning of any menu op, call the setFocusOnFolder to set / unset the focus color
			setFocusOnFolder();
			var folderId = folderNodeMap[currentFolderNodeId];
			// if folderId has -1 in it, then it's a virtual folders.
			// you can only display and view report on Virtual folders.
			
			// NOTE : Because Javascript is anal about strings and numbers
			// .IndexOf fails when pos is a number. So add "" to convert the number to string.
			folderId = folderId + "";
			var pos=folderId.indexOf("-1");
			if (pos < 0){

				displayFolderInExplorer(folderId);
				displayFolderContentCenterA(folderId) ;
				//displayFolderContentRight(folderId);				
				createFolderForm(folderId);
			}
			else {
				openFolderContextHandler();
			}
			
		}
	
		function voteContextHandler() {
			// at the beginning of any menu op, call the setFocusOnFolder to set / unset the focus color
			setFocusOnFolder();
			var folderId = folderNodeMap[currentFolderNodeId];
		
			// if folderId has -1 in it, then it's a virtual folders.
			// you can only display and view report on Virtual folders.
			
			// NOTE : Because Javascript is anal about strings and numbers
			// .IndexOf fails when pos is a number. So add "" to convert the number to string.
			folderId = folderId + "";
			
			var pos=folderId.indexOf("-1");
			if (pos < 0){
				
				displayFolderInExplorer(folderId);
				displayFolderContentCenterA(folderId) ;
				//displayFolderContentRight(folderId);
				showVotingList(folderId);
			}
			else {
				openFolderContextHandler();
			}
			
	
		}
		function createRequirementContextHandler() {
			// at the beginning of any menu op, call the setFocusOnFolder to set / unset the focus color
			setFocusOnFolder();
			var folderId = folderNodeMap[currentFolderNodeId];
		
			// if folderId has -1 in it, then it's a virtual folders.
			// you can only display and view report on Virtual folders.
			
			// NOTE : Because Javascript is anal about strings and numbers
			// .IndexOf fails when pos is a number. So add "" to convert the number to string.
			folderId = folderId + "";
			
			var pos=folderId.indexOf("-1");
			if (pos < 0){
				
				displayFolderInExplorer(folderId);
				displayFolderContentCenterA(folderId) ;
				//displayFolderContentRight(folderId);
				createRequirementForm(folderId);
			}
			else {
				openFolderContextHandler();
			}
			
	
		}
	
		function createListReportContextHandler() {
			// at the beginning of any menu op, call the setFocusOnFolder to set / unset the focus color
			setFocusOnFolder();
			var folderId = folderNodeMap[currentFolderNodeId];
		
			// if folderId has -1 in it, then it's a virtual folders.
			// you can only display and view report on Virtual folders.
			
			// NOTE : Because Javascript is anal about strings and numbers
			// .IndexOf fails when pos is a number. So add "" to convert the number to string.
			folderId = folderId + "";
			
			var pos=folderId.indexOf("-1");
			if (pos < 0){
				
				displayFolderInExplorer(folderId);
				displayFolderContentCenterA(folderId) ;
				//displayFolderContentRight(folderId);				
				createNewListReport(folderId);
			}
			else {
				openFolderContextHandler();
			}
		}

		function createTraceTreeReportContextHandler() {
			// at the beginning of any menu op, call the setFocusOnFolder to set / unset the focus color
			setFocusOnFolder();
			var folderId = folderNodeMap[currentFolderNodeId];
		
			// if folderId has -1 in it, then it's a virtual folders.
			// you can only display and view report on Virtual folders.
			
			// NOTE : Because Javascript is anal about strings and numbers
			// .IndexOf fails when pos is a number. So add "" to convert the number to string.
			folderId = folderId + "";
			
			var pos=folderId.indexOf("-1");
			if (pos < 0){
				
				displayFolderInExplorer(folderId);
				displayFolderContentCenterA(folderId) ;
				//displayFolderContentRight(folderId);
				createNewTraceTreeReport(folderId);
			}
			else {
				openFolderContextHandler();
			}
		}
		
		function createTraceMatrixContextHandler() {
			// at the beginning of any menu op, call the setFocusOnFolder to set / unset the focus color
			setFocusOnFolder();
			displayTracePanel();
		}
		
		
		function importFromExcelContextHandler() {
			// at the beginning of any menu op, call the setFocusOnFolder to set / unset the focus color
			setFocusOnFolder();
			var folderId = folderNodeMap[currentFolderNodeId];
		
			// if folderId has -1 in it, then it's a virtual folders.
			// you can only display and view report on Virtual folders.
			
			// NOTE : Because Javascript is anal about strings and numbers
			// .IndexOf fails when pos is a number. So add "" to convert the number to string.
			folderId = folderId + "";
			
			var pos=folderId.indexOf("-1");
			if (pos < 0){
				
				displayFolderInExplorer(folderId);
				displayFolderContentCenterA(folderId) ;
				//displayFolderContentRight(folderId);
				importFromExcelForm(folderId);
			}
			else {
				openFolderContextHandler();
			}
		}
		
		function createWordTemplateContextHandler() {
			// at the beginning of any menu op, call the setFocusOnFolder to set / unset the focus color
			setFocusOnFolder();
			var folderId = folderNodeMap[currentFolderNodeId];
		
			// if folderId has -1 in it, then it's a virtual folders.
			// you can only display and view report on Virtual folders.
			
			// NOTE : Because Javascript is anal about strings and numbers
			// .IndexOf fails when pos is a number. So add "" to convert the number to string.
			folderId = folderId + "";
			
			var pos=folderId.indexOf("-1");
			if (pos < 0){
				
				displayFolderInExplorer(folderId);
				displayFolderContentCenterA(folderId) ;
				//displayFolderContentRight(folderId);
				createWordTemplateForm(folderId);
			}
			else {
				openFolderContextHandler();
			}
			
	
		}
			
		// NOTE : Since we are using only one contextMenu for all the tree objects, we are using this logic...
		// All tree objects are in a big hash map called folderNodemap
		// we have the same context menu on all tree nodes
		// we listen to context event on any tree node
		// which triggers, onTriggerContextMenu
		// from this event, we get the calling node, and set it to a global variable called currentFolderId
		// that can be used by the individual functions.
		 
	    function onTriggerContextMenu(p_oEvent) {
			var oTarget = this.contextEventTarget;
			currentFolderNodeId = oTarget.id;
			
	    }
	
		
	    var oContextMenu = new YAHOO.widget.ContextMenu("mytreecontextmenu", {
	        trigger: "treeDiv1",
	        lazyload: true, 
	        itemdata: [
				{ text: "Open this Folder"  , onclick: { fn: openFolderContextHandler } },
				{ text: "  &nbsp;              ", onclick: {  } },
				{ text: "Vote ", onclick: { fn: voteContextHandler } },
				{ text: "  &nbsp;              ", onclick: {  } },
				{ text: "Create Object ", onclick: { fn: createRequirementContextHandler } },
				{ text: "Create Report", onclick: { fn: createListReportContextHandler } },
				{ text: "Create Trace Tree", onclick: { fn: createTraceTreeReportContextHandler } },

				{ text: "Create Trace Matrix", onclick: { fn: createTraceMatrixContextHandler } },
				{ text: "Create Word Template Report", onclick: { fn: createWordTemplateContextHandler } },
				{ text: "  &nbsp;              ", onclick: {  } },
				{ text: "Import from Excel", onclick: { fn: importFromExcelContextHandler } },
	            { text: "Import from Word", onclick: { fn: createWordTemplateContextHandler } },
	           { text: "  &nbsp;              ", onclick: {  } },
				{ text: "Create Folder", onclick: { fn: createSubFolderContextHandler } },
				{ text: "Delete this Folder", onclick: { fn: deleteFolderFormContextHandler } },
				{ text: "Edit this Folder", onclick: { fn: editFolderContextHandler } },
				{ text: "  &nbsp;              ", onclick: {  } },
				{ text: "Saved Reports", onclick: { fn: savedContextHandler } },
				{ text: "Folder Permissions", onclick: { fn: folderPermissionsContextHandler } },
				{ text: "Folder Dashboard", onclick: { fn: folderDsahboardContextHandler } }
				
		    ] });
	
	
		/*
		Subscribe to the "contextmenu" event for the element(s)
		specified as the "trigger" for the ContextMenu instance.
		*/
	
		oContextMenu.subscribe("triggerContextMenu", onTriggerContextMenu);
	
		// the followng method catche a user clicking on a tree node (left click) 
		// and calls the openFolderContextHandler, after settign the global variable currentFolderId.
		tree.subscribe("labelClick", function(node) {
			currentFolderNodeId = node.labelElId;
			openFolderContextHandler();
	      });
	
	
	</script>
<%}%>