	// GloreeJava2
	////////////////////////////////////////////////////////////////
	//
	// common methods.
	//
	//////////////////////////////////////////////////////////////////


	
function startTraceCloudTour(){

	var tour = new Tour({
		template: "<div class='popover tour' style='border-color:red'><div class='arrow'></div><h3 class='popover-title'></h3> "
			+ "<div class='popover-content'></div>"
			+ "<div class='popover-navigation'> "
			+ "<button class='btn btn-primary btn-sm' data-role='prev'>« Prev</button>"
			+ "<span data-role='separator'>&nbsp;&nbsp;|&nbsp;&nbsp;</span><button class='btn btn-primary btn-sm' data-role='next'>Next »</button>"
			+"<button class='btn btn-danger btn-sm' data-role='end'>End tour</button></div></nav></div>",
		backdropPadding: 10,
		animation:true,
	  steps: [
	{
		
	    element: "#startTour",
	    title: "Step 1/30 : Tour of TraceCloud",
	    content: "This tour is designed to make you familier with TraceCloud . " +
	    "<br><br>You can End the tour any time, by clicking the <a href='#' class='btn btn-sm btn-danger' style='color:white'>End Tour </a> button"+ 
	    "<br><br><br><font color='red'><b>For the tour to work effectively, please perform ALL the actions in the tour. </b></font><br> "+
	    "<br><br>If you need a deeper walk through, please email support@tracecloud.com . We can schedule a web conference for you. <br><br>"+
	    "<br><br><b><font color='red'>Action </font></b>: Click on the " +
	    " <a href='#' class='btn btn-sm btn-primary' style='color:white'>Next </a> button below <br><hr>"
	},
	{
		
	    element: "#projectName",
	    title: "Step 2/30 : Current Project",
	    content: "This is the current project you are on . <br><br><b><font color='red'>Action </font></b>: Click on the Project Name to get more details about this project"
	},
	
	  {
		element: "#expandButton",
	    title: "Step 3/30 : Folder Structure",
	    content: "This is the folder structure for your project. Every Requirement Type has a root folder and you can create sub folders within in. " +
	    " <br><br><b><font color='red'>Action </font></b>: Click  <a href='#' class='btn btn-sm btn-primary' style='color:white'>Expand Folders </a> button to Expand your Folder & Sub Folder hierarchy " +
	    " <br><br><b><font color='red'>Action </font></b>: Click <a href='#' class='btn btn-sm btn-danger' style='color:white'>Collapse Folders </a> button to Collapse your Folder hierarchy " +
	    "<br><hr>" 
	  },

	  {
		element: "#treeDiv1",
	    title: "Step 4/30 : Folder Contents",
	    placement:"right",
	    content: "The number in () indicates the number of objects in this folder. " +
	    " <br><br><b><font color='red'>Action </font></b>: Click on a Folder to show  its contents in the right panel " + 
	    " <br><br><b><font color='red'>Action </font></b>: Right Click on Folder , to see the actions you can perform in this Folder. <br><br>" +
	    " <b><font color='red'>Action </font></b>:All these actions can also be performed by clicking the <button type='button' disabled='disabled' class='btn btn-danger btn-sm'>Actions</button> " +
				"<button type='button' class='btn btn-danger dropdown-toggle btn-sm' data-toggle='dropdown' aria-expanded='false'><span class='caret'></span>"+
				"<span class='sr-only'>Toggle Dropdown</span></button> drop down on the right. Click the Folder Action dropdown " 
	  },
	  {
		element: "#hideExplorer",
	    title: "Step 5/30 : Hide Explorer Panel",
	    content: "There are times you need more real estate. This button collapses the Explorer Panel. "+ 
	    " <br><br><b><font color='red'>Action </font></b>: Click <a href='#' class='btn btn-sm btn-success' style='color:white'>Hide Explorer </a> button to Collapse  the Folder Explorer Panel." +
	    "<br><hr>  " 
	  },
	  {
		element: "#showExplorer",
	    title: "Step 6/30 : Show Explorer Panel",
	    content: "Explorer gives a folder hierarchy view of the project. This button reveals the Explorer Panel. "+ 
	    " <br><br><b><font color='red'>Action </font></b>: Click <a href='#' class='btn btn-sm btn-success' style='color:white'>Show Explorer </a>  button to Expand the  Folder Explorer Panel" +
	    "<br><hr> " 
	  },
	  {
		element: "#searchButton",
	    title: "Step 7/30 : Search Within the Project",
	    content: "There are times you quickly want to find an object. Search by ID and Search by String are useful for this. "+ 
	    " <br><br><b><font color='red'>Action </font></b>: Click <a href='#' class='btn btn-sm btn-primary' style='color:white'>Search</a> button to open search options <br><hr>  " 
	  },
	  {
		element: "#searchByIdGo",
	    title: "Step 8/30 : Search by ID",
	    content: "If you know the object you are looking for (example BR-1 or a sequence BR-1,REL-2,FR-4) use this search box" + 
	    " <br><br><b><font color='red'>Action </font></b>: Enter 'REL-1,BR-2' (or some other Ids without quotes) and Click " +
	    " <a href='#' class='btn btn-sm btn-primary' style='color:white'>Go </a> . If you know the ID this is a quick way to find objects. " +
	    "<br><hr> " 
	  },
	  {
		element: "#searchByStringGo",
	    title: "Step 9/30 : Wild card search",
	    content: "If you don't have the exact id, just search by any string in the object's name, description, or comments" +
	    " <br><br><b><font color='red'>Action </font></b>: Enter some search string and click " +
	    " <a href='#' class='btn btn-sm btn-primary' style='color:white'>Go </a>. This will open advanced search options" +
	    "<br><hr> " 
	  },
		  
	 {
	    element: "#iWantTo",
	    title: "Step 10/30 : Home Page",
	    content: "This is the home page for your project and is the place that launches most of your actions" +
	    " <br><br><b><font color='red'>Action </font></b>: Click on <a href='#' class='btn btn-sm btn-success' style='color:white'> Home Page </a> button " +
	    " to open up your Home Page. <br><br>Think of this as the HOME BUTTON on  your I-Phone or Android phone <br><hr>" 
	  },
		  {
		    element: "#iWantToTabLink",
		    title: "Step 11/30 : I Want To...",
		    alignment:"right",
		    content: "This is the 'I Want To' tab of the Home Page. Almost all the actions you want to perform, can be found here. <br>" +
		    		"You can use this to launch feature to Create Requirements, Import from Excel / Word , Generate Reports, Do Traceability , View Dashboards, Get an Excel Dump of the project" +
		    		"  and launch many other useful tools" +
		    " <br><br><b><font color='red'>Action </font></b>: Click on each of the drop downs to see what they can do." +
		    "<br><hr>" 
		  },
	  
		  {
		    element: "#myStatusTabLink",
		    placement: "right",
		    title: "Step 12/30 : My Status",
		    content: "This is summary view of the objects  <font color='red'><b>YOU </b></font>are responsible for" +
		    " <br><br> <font color='red'><b>If any of these numbers are NOT Zero, then your action is needed. Please mouse over and click on each cell </b></font><br>" +
		    " <br><br><b><font color='red'>Action </font></b>: Click on the <a href='#'>My Status</a> tab" 
		  },
		  {
		    element: "#recentCommentsTabLink",
		    placement: "right",
		    title: "Step 13/30 : Recent Comments - Relevant to me",
		    content: "Here is a list of comments made on objects that are relevant to me " +
		    " <br><br><b><font color='red'>Action </font></b>: Click on the <a href='#'>Recent Comments In Project </a> tab" 
		  },
		  	{
			    element: "#recentlyCommentedBtn",
			    placement: "right",
			    title: "Step 14/30 : Show all the comments on the objects that are relevant to me",
			    content: "For all the objects  that I am interested in, show the collaboration and commenting. This is like Facebook for my Requirements.." +
			    " <br><br><b><font color='red'>Action </font></b>: Expand how many days back in time you want to go and click on GO button <br><hr>" 
			  },
		{
		    element: "#recentChangesTabLink",
		    placement: "right",
		    title: "Step 15/30 : Recent Changes - Relevant to me",
		    content: "Here is a list of objects that are relevant to me and have changed in the recent past " +
		    " <br><br><b><font color='red'>Action </font></b>: Click on the <a href='#'>Recent Changes In Project </a> tab" 
		  },
		  	{
			    element: "#recentlyChangedBtn",
			    placement: "right",
			    title: "Step 16/30 : Show all the objects that are relevant to me and have changed in the recent past",
			    content: "For all the objects  that I am interested in, show me the ones that have changed most recently." +
			    " <br><br><b><font color='red'>Action </font></b>: Expand how many days back in time you want to go and click on GO button <br><hr>" 
			  },		  
	  {
	    element: "#scrumBoard",
	    placement:"right",
	    title: "Step 17/30 : Agile Scrum Board",
	    content: "If you are interested in using Agile Scrum, and want to create a Sprint and track tasks, you can use the Scrum Board <br><br>" + 
	    " <b><font color='red'>Action </font></b>: Click  <a href='#' class='btn btn-info' style='color:white'>Scrum Board </a> button to start the Scrum work flow " 
	  },
	  {
	    element: "#configureProject",
	    placement:"left",
	    title: "Step 18/30 : Configure Project",
	    content: "This completes the USER Tour. <br><br>" +
	    		" To see how the project can be configured (Define our own Requirement Types, Attributes, Roles etc..) Please continue to the Configure module. <br><br>" +
	    		" <b><font color='red'>Action </font></b>: Click  <a href='#' class='btn btn-sm btn-danger' style='color:white'>Configure Project </a> button to start the " +
	    		" Administraton Module <br><hr>"
	  }	,
	  	{
		    element: "#mangeObjectTypesButton",
		    placement:"right",
		    title: "Step 19/30 : Create and Manage Requirement (Object) Types",
		    content: "TraceCloud gives you the flexibility to create your object types <br><br>" +
		    		" <b><font color='red'>Action </font></b>: Click  <a href='#' class='btn btn-sm btn-primary' style='color:white'>Manage Object Types </a> button" +
		    		" <br><hr>"
		  }	,
		  
		  		{
			    element: "#createNewObjectTypeButton",
			    placement:"right",
			    title: "Step 20/30 : Create a New Requirement (Object) Type",
			    content: "You can use this feature to make TraceCloud map to your existing process. For example, if you are making ships, you can have a type called 'Flotation Requirements' <br><br>" +
			    		" <b><font color='red'>Action </font></b>: Click  <a href='#' class='btn btn-sm btn-primary' style='color:white'>Create New Types </a> button" +
			    		" <br><hr>"
			  }	,
			  
			  {
			    element: "#objectTypesDiv",
			    placement:"right",
			    title: "Step 21/30 : Manage Existing Requirement (Object) Type",
			    content: "You can add Attributes to Requirement Types, Make the Requirement Type Approval work flow enabled, Define whether it can be repored as an Orphan / Dangling.<br><br>" +
			    " <b><font color='red'>Action </font></b>: Click any of the existing object types link on the left" +
			    		" <br><hr>"
			  }	,
			  {
			    element: "#createNewAttributeCell",
			    placement:"top",
			    title: "Step 22/30 : Create Additional Attributes",
			    content: "" +
			    		" <b><font color='red'>Action </font></b>: Click on the <a href='#'>Create New Attribute</a> Tab." +
			    		" <br><hr>"
			  }	,
		  {
		    element: "#manageRolesButton",
		    placement:"right",
		    title: "Step 23/30 : Create and Manage Roles and Permissions",
		    content: "TraceCloud gives you the flexibility to create Roles and manage permissions at a Folder Level for each of these roles <br><br>" +
		    		" <b><font color='red'>Action </font></b>: Click  <a href='#' class='btn btn-sm btn-primary' style='color:white'>Manage Roles </a> button" +
		    		" <br><hr>"
		  }	,
		  	{	
			    element: "#rolesDiv",
			    placement:"right",
			    title: "Step 24/30 : Manage Roles & Permissions",
			    content: " You can Create new roles or configure existing Role Users and Permissions. <br><br>" +
			    		" <b><font color='red'>Action </font></b>: Click  on the <a href='#'>Administrator </a> role" +
			    		" <br><hr>"
			  }	,
			  	{	
				    element: "#addUsersTab",
				    placement:"top",
				    title: "Step 25/30 : Add Users",
				    content: " Any member of this project can be added as member of this Role. You can also invite NEW users to this project by adding them to a role.  <br><br>" +
				    		" <b><font color='red'>Action </font></b>: Click  on the <a href='#'>Add Users </a> tab" +
				    		" <br><hr>"
				  }	,
				  {	
				    element: "#displayAllUsersInRole",
				    placement:"top",
				    title: "Step 26/30 : Members of a Role",
				    content: " Here are all the existing members of this role.  <br><br>" +
				    		" <b><font color='red'>Action </font></b>: If you find your name in this list, click on it" +
				    		" <br><hr>"
				  }	,
				  {	
				    element: "#editPrivilegesTab",
				    placement:"top",
				    title: "Step 27/30 : Role Privileges",
				    content: " This tab shows the privileges enjoyed by the members of this role. i.e what can they do on EACH Folder in this Project  <br><br>" +
				    		" <b><font color='red'>Action </font></b>: Click on the <a href=''>Edit Privileges </a> tab" +
				    		" <br><hr>"
				  }	,
		  {
		    element: "#projectCoreTD",
		    placement:"right",
		    title: "Step 28/30 : Project Core Configuration",
		    content: "You can configure core information about your project here.  <br><br>" +
		    		" <b><font color='red'>Action </font></b>: Click  <b><a href='#'  >Project Core Information </a></b> link" +
		    		" <br><hr>"
		  }	,
		  {
		    element: "#connectProjectsTD",
		    placement:"right",
		    title: "Step 29/30 : Connect Projects",
		    content: "There are times when you want to trace Requirements from one project, to Requirments in another project. You can use this option to connect projects to enable cross-project-traceability.  <br><br>" +
		    		" <b><font color='red'>Action </font></b>: Click <b> <a href='#' >Connect Projects </a></b> link" +
		    		" <br><hr>"
		  }	,
	  {
		    element: "#returnToProject",
		    placement:"left",
		    title: "Step 30/30 : End of Tour",
		    content: " This concludes the tour of TraceCloud. <br><br> If you need a deeper walk through, please email support@tracecloud.com . We can schedule a web conference for you. " +
		    "<br><br>You can click this link to exit the Administration Module and return to the project" + 
		    " <br><br><b><font color='red'>Action </font></b>: Click  <a href='#' class='btn btn-sm btn-danger' style='color:white'>Return to project </a> button<br><hr>" 
		  }	,
	  {
		
	    element: "#startTour",
	    title: "End of Tour of TraceCloud",
	    content: " This concludes the tour of TraceCloud. <br><br> If you need a deeper walk through, please email support@tracecloud.com . We can schedule a web conference for you. " +
	    "<br><br><b><font color='red'>Action </font></b>: Click on the " +
	    " <a href='#' class='btn btn-sm btn-primary' style='color:white'>End Tour </a> button below <br><hr>"
	},
				  
	]});

	// Initialize the tour
	tour.init();

	// Start the tour
	tour.restart();

}








function createNewSNTemplateFormOLD(){
	
	var url="/GloreeJava2/jsp/ServiceNow/createNewSNTemplateForm.jsp?";
	
	
	// lets confirm that instance starts with https:// and ends with .service-now.com
	var validEnd = instance.endsWith(".service-now.com");
	var validStart = instance.startsWith("https://");

	if (!validStart){
		alert("Plese ensure that your instance name start with 'https://'");
		return;
	}
	if (!validEnd){
		alert("Plese ensure that your instance name ends with '.service-now.com'");
		return;
	}
	url += "instance=" + instance;
	url += "&snuser=" + encodeURIComponent(snuser);
	url += "&snpwd=" + encodeURIComponent(snpwd);
	
	
	fillOPCenterGeneric(url, "sNTemplatesDiv");

}




function createNewSNTemplateForm(){
	
	var url="/GloreeJava2/jsp/ServiceNow/createNewSNTemplateForm.jsp?";	
	fillOPCenterGeneric(url, "sNTemplatesDiv");

}

function createNewSNDataFileForm(){
	
	var url="/GloreeJava2/jsp/ServiceNow/createNewSNDataFileForm.jsp?";	
	fillOPCenterGeneric(url, "sNTemplatesDiv");

}



function openSNWordTemplate(sNTemplateId){
	
	var url="/GloreeJava2/jsp/ServiceNow/openSNWordTemplate.jsp?";
		url += "sNTemplateId=" + sNTemplateId;
	fillOPCenterGeneric(url, "openSNWordTemplateDiv");

}

function createNewSNTemplateOld(thisForm ) {
	
	
	var sNProjectIdObject = document.getElementById("sNProjectId");
	var sNProjectId = sNProjectIdObject.options[sNProjectIdObject.selectedIndex].value;
	
	document.getElementById("sNProjectIdHidden").value=sNProjectId;
	
	thisForm.submit();
	
}


function createNewSNTemplate(thisForm ) {
	thisForm.submit();
	
}



function showExistingSNTemplatesOld(){
	
	var url="/GloreeJava2/jsp/ServiceNow/showExistingSNTemplates.jsp?";
	

	var instance = document.getElementById("instance" ).value;
	var snuser = document.getElementById("snuser" ).value;
	var snpwd = document.getElementById("snpwd" ).value
	
	
	
	// lets confirm that instance starts with https:// and ends with .service-now.com
	var validEnd = instance.endsWith(".service-now.com");
	var validStart = instance.startsWith("https://");

	if (!validStart){
		alert("Plese ensure that your instance name start with 'https://'");
		return;
	}
	if (!validEnd){
		alert("Plese ensure that your instance name ends with '.service-now.com'");
		return;
	}

	url += "instance=" + instance;
	url += "&snuser=" + encodeURIComponent(snuser);
	url += "&snpwd=" + encodeURIComponent(snpwd);
	
	
	fillOPCenterGeneric(url, "sNTemplatesDiv");

}


function showSNExcelReports(){
	
	var url="/GloreeJava2/jsp/ServiceNow/showSNExcelReports.jsp?";
	

	var instance = document.getElementById("instance" ).value;
	var snuser = document.getElementById("snuser" ).value;
	var snpwd = document.getElementById("snpwd" ).value
	
	
	
	// lets confirm that instance starts with https:// and ends with .service-now.com
	var validEnd = instance.endsWith(".service-now.com");
	var validStart = instance.startsWith("https://");

	if (!validStart){
		alert("Plese ensure that your instance name start with 'https://'");
		return;
	}
	if (!validEnd){
		alert("Plese ensure that your instance name ends with '.service-now.com'");
		return;
	}

	url += "instance=" + instance;
	url += "&snuser=" + encodeURIComponent(snuser);
	url += "&snpwd=" + encodeURIComponent(snpwd);
	
	
	fillOPCenterGeneric(url, "sNTemplatesDiv");

}



function getProjectSNTemplatesOld(){
	
	var url="/GloreeJava2/jsp/ServiceNow/getProjectSNTemplates.jsp?";
	
	var sNProjectIdObject = document.getElementById("sNProjectId");
	var sNProjectId = sNProjectIdObject.options[sNProjectIdObject.selectedIndex].value;



	url += "sNProjectId=" + sNProjectId;
	fillOPCenterGeneric(url, "getProjectSNTemplatesDiv");

}


function getSNTemplates(){
	
	var url="/GloreeJava2/jsp/ServiceNow/getSNTemplates.jsp?";
	fillOPCenterGeneric(url, "sNTemplatesDiv");

}

function scheduleMyDemo(source){
	
	

	var contactName = document.getElementById("contactName" ).value;
	
	var contactEmailId = document.getElementById("contactEmailId" ).value;
	
	if (echeck(contactEmailId)==false){
		alert ("Your email id " + contactEmailId + " is not formatted correctly. Please fix it.");
		document.getElementById("contactEmailId").focus();
		document.getElementById("contactEmailId").style.backgroundColor="#FFCC99";
		return(0);
	}

	var contactPhoneNumber = document.getElementById("contactPhoneNumber" ).value;
	var contactTime = document.getElementById("contactTime" ).value;
	var contactRequirements = document.getElementById("contactRequirements" ).value;
	
	var divName = "scheduleMyDemoDiv";
	
	var url="/GloreeJava2/servlet/UserAccountAction?";
	url += "action=scheduleMyDemo";
	url += "&contactName=" + encodeURIComponent(contactName);
	
	url += "&contactEmailId=" + encodeURIComponent(contactEmailId);
	
	
	url += "&contactPhoneNumber=" + encodeURIComponent(contactPhoneNumber);
	url += "&contactTime=" + encodeURIComponent(contactTime);
	url += "&contactRequirements=" + encodeURIComponent(contactRequirements);
	url += "&source=" + source;
	

	
	document.getElementById(divName).innerHTML= "<font color='red'>&nbsp;&nbsp;&nbsp;Working...</font>";
	
	// this is coming fron the list of requirements in a folder
	xmlHttpOPCenterB =GetXmlHttpObject();			
	xmlHttpOPCenterB.onreadystatechange=function() {
		if(xmlHttpOPCenterB.readyState==4){
			// do nothing

			document.getElementById(divName).innerHTML=xmlHttpOPCenterB.responseText;
			
		}
	}
	xmlHttpOPCenterB.open("GET",url,true);
	xmlHttpOPCenterB.send(null);

}
function notifyMarketingPageLoad(source){
	
	
	var url="/GloreeJava2/servlet/UserAccountAction?";
	url += "action=marketingPageLoad";
	url += "&source=" + source;
	
	
	// this is coming fron the list of requirements in a folder
	xmlHttpOPCenterB =GetXmlHttpObject();			
	xmlHttpOPCenterB.onreadystatechange=function() {
		if(xmlHttpOPCenterB.readyState==4){
			// do nothing
			
		}
	}
	xmlHttpOPCenterB.open("GET",url,true);
	xmlHttpOPCenterB.send(null);

}



function sRAAction(action,marketingUserId,divName) {

	var url="/GloreeJava2/jsp/UserDashboard/sRAAction.jsp?";
	

	url += "marketingUserId=" + marketingUserId;
	
	
	
	if (action == "deleteProject"){
		projectId = marketingUserId;
		url += "&action=" + encodeURIComponent(action);
		url += "&projectId=" + encodeURIComponent(projectId);
		
	}
	

	if (action == "setHelloCompleted"){
		url += "&action=" + encodeURIComponent(action);
	}
	
	if (action == "sendHelloEmail"){
		url += "&action=" + encodeURIComponent(action);
		
		// lets add extra fields
		var to = document.getElementById("helloTo" + marketingUserId).value;
		url += "&to=" + encodeURIComponent(to);
		
		var subject = document.getElementById("helloSubject" + marketingUserId).value;
		url += "&subject=" + encodeURIComponent(subject);
		
		var body = document.getElementById("helloBody" + marketingUserId).value;
		url += "&body=" + encodeURIComponent(body);
	}
	
	if (action == "sendHelloEmailOld"){
		url += "&action=" + encodeURIComponent("sendHelloEmail");
		
		// lets add extra fields
		var to = document.getElementById("helloToOld" + marketingUserId).value;
		url += "&to=" + encodeURIComponent(to);
		
		var subject = document.getElementById("helloSubjectOld" + marketingUserId).value;
		url += "&subject=" + encodeURIComponent(subject);
		
		var body = document.getElementById("helloBodyOld" + marketingUserId).value;
		url += "&body=" + encodeURIComponent(body);
		
	}
	
	if (action == "setNathanHelloCompleted"){
		url += "&action=" + encodeURIComponent(action);
	}
	
	if (action == "sendNathanHelloEmail"){
		url += "&action=" + encodeURIComponent(action);
		
		// lets add extra fields
		var to = document.getElementById("NathanHelloTo" + marketingUserId).value;
		url += "&to=" + encodeURIComponent(to);
		
		var subject = document.getElementById("NathanHelloSubject" + marketingUserId).value;
		url += "&subject=" + encodeURIComponent(subject);
		
		var body = document.getElementById("NathanHelloBody" + marketingUserId).value;
		url += "&body=" + encodeURIComponent(body);
	}
	
	

	document.getElementById(divName).style.display = "block";
	document.getElementById(divName).innerHTML= "<font color='red'>&nbsp;&nbsp;&nbsp;Working...</font>";
	
	
	fillOPCenterGeneric(url, divName);
}




function trim(str) {
        return str.replace(/^\s+|\s+$/g,"");
    }


function isBrowserMSIE() {

	var isMSIE = false;
	var ua = navigator.userAgent;
	
	if (
		(ua.indexOf("MSIE")!=-1)
		||
		(ua.indexOf("Trident")!=-1)
	){
		isMSIE = true;
	}

	return isMSIE;
}

function echeck(str) {

		var at="@"
		var dot="."
		var lat=str.indexOf(at)
		var lstr=str.length
		var ldot=str.indexOf(dot)
		if (str.indexOf(at)==-1){
		   return false
		}

		if (str.indexOf(at)==-1 || str.indexOf(at)==0 || str.indexOf(at)==lstr){
		   return false
		}

		if (str.indexOf(dot)==-1 || str.indexOf(dot)==0 || str.indexOf(dot)==lstr){
		    return false
		}

		 if (str.indexOf(at,(lat+1))!=-1){
		    return false
		 }

		 if (str.substring(lat-1,lat)==dot || str.substring(lat+1,lat+2)==dot){
		    return false
		 }

		 if (str.indexOf(dot,(lat+2))==-1){
		    return false
		 }
		
		 if (str.indexOf(" ")!=-1){
		    return false
		 }

 		 return true					
	}



	/**
	* DHTML date validation script. Courtesy of SmartWebby.com (http://www.smartwebby.com/dhtml/)
	*/
	//Declaring valid date character, minimum year and maximum year
	var dtCh= "/";
	var minYear=1900;
	var maxYear=2100;
	
	function isInteger(s){
		var i;
	   for (i = 0; i < s.length; i++){   
	       // Check that current character is number.
	       var c = s.charAt(i);
	       if (((c < "0") || (c > "9"))) return false;
	   }
	   // All characters are numbers.
	   return true;
	}
	
	function stripCharsInBag(s, bag){
		var i;
	   var returnString = "";
	   // Search through string's characters one by one.
	   // If character is not in bag, append to returnString.
	   for (i = 0; i < s.length; i++){   
	       var c = s.charAt(i);
	       if (bag.indexOf(c) == -1) returnString += c;
	   }
	   return returnString;
	}
	
	function daysInFebruary (year){
		// February has 29 days in any year evenly divisible by four,
	   // EXCEPT for centurial years which are not also divisible by 400.
	   return (((year % 4 == 0) && ( (!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28 );
	}
	function DaysArray(n) {
		for (var i = 1; i <= n; i++) {
			this[i] = 31
			if (i==4 || i==6 || i==9 || i==11) {this[i] = 30}
			if (i==2) {this[i] = 29}
	  } 
	  return this
	}
	
	function isValidDate(dtStr){
		var daysInMonth = DaysArray(12)
		var pos1=dtStr.indexOf(dtCh)
		var pos2=dtStr.indexOf(dtCh,pos1+1)
		var strMonth=dtStr.substring(0,pos1)
		var strDay=dtStr.substring(pos1+1,pos2)
		var strYear=dtStr.substring(pos2+1)
		strYr=strYear
		if (strDay.charAt(0)=="0" && strDay.length>1) strDay=strDay.substring(1)
		if (strMonth.charAt(0)=="0" && strMonth.length>1) strMonth=strMonth.substring(1)
		for (var i = 1; i <= 3; i++) {
			if (strYr.charAt(0)=="0" && strYr.length>1) strYr=strYr.substring(1)
		}
		month=parseInt(strMonth)
		day=parseInt(strDay)
		year=parseInt(strYr)
		if (pos1==-1 || pos2==-1){
			alert("The date format should be : mm/dd/yyyy")
			return false
		}
		if (strMonth.length<1 || month<1 || month>12){
			alert("Please enter a valid month")
			return false
		}
		if (strDay.length<1 || day<1 || day>31 || (month==2 && day>daysInFebruary(year)) || day > daysInMonth[month]){
			alert("Please enter a valid day")
			return false
		}
		if (strYear.length != 4 || year==0 || year<minYear || year>maxYear){
			alert("Please enter a valid 4 digit year between "+minYear+" and "+maxYear)
			return false
		}
		if (dtStr.indexOf(dtCh,pos2+1)!=-1 || isInteger(stripCharsInBag(dtStr, dtCh))==false){
			alert("Please enter a valid date")
			return false
		}
	return true
	}

	
	// called from the startpage (relogin) screen
	// closes the left, right, top layout regions.
	function closeLayout(){
		if (layout != null){
			if (layout.getUnitByPosition("top") != null){
				layout.getUnitByPosition("top").close();
			}
			if (layout.getUnitByPosition("right") != null){
				layout.getUnitByPosition("right").close();
			}
			if (layout.getUnitByPosition("left") != null){
				layout.getUnitByPosition("left").close();
			}
			if (document.getElementById("contentCenterA") != null){
				document.getElementById("contentCenterA").style.display="none";
			}
			if (document.getElementById("contentCenterC") != null){
				document.getElementById("contentCenterC").style.display="none";
			}
			if (document.getElementById("contentCenterD") != null){
				document.getElementById("contentCenterD").style.display="none";
			}
			if (document.getElementById("contentCenterE") != null){
				document.getElementById("contentCenterE").style.display="none";
			}
			if (document.getElementById("contentCenterF") != null){
				document.getElementById("contentCenterF").style.display="none";
			}
		}
	}
	function GetXmlHttpObject(){

		var xmlHttp=null;
		try  {
  		// Firefox, Opera 8.0+, Safari
  			xmlHttp= new XMLHttpRequest();
  		}
		catch (e)  {
  		// Internet Explorer
  			try {
    			xmlHttp=new ActiveXObject("Msxml2.XMLHTTP");
    		}
  			catch (e){
    			xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
    		}
  		}
		return xmlHttp;
	}

	function fillOPCenterGeneric(url, contentArea){
		// use bustcache to solve the IE caching the page and not refreshing the contents problem.
		url += "&bustcache=" + new Date().getTime() ; 
		var xmlHttp =GetXmlHttpObject();
		if (xmlHttp ==null) {
  			alert ("Your browser does not support AJAX!");
  			return;
  		} 
  		xmlHttp.onreadystatechange=function() {
			if(xmlHttp.readyState==4){

				// if this element is invisible, set it to visible.
				if (document.getElementById(contentArea) != null){
					document.getElementById(contentArea).style.display = 'block'; 
					document.getElementById(contentArea).innerHTML=xmlHttp.responseText;
				}
			}
		}
		xmlHttp.open("GET",url,true);
		xmlHttp.send(null);
	}
	function fillOPCenterGenericNoDivDisplay(url, contentArea){
		// use bustcache to solve the IE caching the page and not refreshing the contents problem.
		url += "&bustcache=" + new Date().getTime() ; 
		var xmlHttp =GetXmlHttpObject();
		if (xmlHttp ==null) {
  			alert ("Your browser does not support AJAX!");
  			return;
  		} 
  		xmlHttp.onreadystatechange=function() {
			if(xmlHttp.readyState==4){

				// if this element is invisible, set it to visible.
				if (document.getElementById(contentArea) != null){
					document.getElementById(contentArea).innerHTML=xmlHttp.responseText;
				}
			}
		}
		xmlHttp.open("GET",url,true);
		xmlHttp.send(null);
	}


	// same as fillOpCenterGeneric, but works when you want to call stuff in the parent window.
	function fillOPCenterGenericInParentWindow(url, contentArea){

		// NOTE : we don't want to show the working gif here, as it tends to blur the eye.
		// we will let the calling routine control where to show the gif.
		// document.getElementById(contentArea).innerHTML="&nbsp;&nbsp;&nbsp;&nbsp;.&nbsp;.&nbsp;.&nbsp;Processing";
		
	
		// use bustcache to solve the IE caching the page and not refreshing the contents problem.
		url += "&bustcache=" + new Date().getTime() ; 
		var xmlHttp =GetXmlHttpObject();
		if (xmlHttp ==null) {
  			alert ("Your browser does not support AJAX!");
  			return;
  		} 
  		
		xmlHttp.onreadystatechange=function() {
			if(xmlHttp.readyState==4){

				// if this element is invisible, set it to visible.
				opener.document.getElementById(contentArea).style.display = 'block'; 
				opener.document.getElementById(contentArea).innerHTML=xmlHttp.responseText;
			}
		}
		xmlHttp.open("GET",url,true);
		xmlHttp.send(null);
	}
	


	function genericLog(objectId, objectType, description){
		
	
		var url= "/GloreeJava2/servlet/ProjectAction?action=genericLog" ;
		url += "&bustcache=" + new Date().getTime() ;
		url += "&objectId=" + encodeURIComponent(objectId);
		url += "&objectType=" + encodeURIComponent(objectType);
		url += "&description=" + encodeURIComponent(description);
		

		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// do nothing
			}
		}
		
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);

	}

	

	function displayChangeLogForm(){
		
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		
		
		// Now fill contentCenterB with the folder details.
		document.getElementById("contentCenterF").style.display = "block";
		document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/ChangeLog/displayChangeLogForm.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "contentCenterF");
	}

	
	function displayChangeLog(sortBy){
		
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		var changeFolderIdObject = document.getElementById("changeFolderId");
		var changeFolderId = changeFolderIdObject.options[changeFolderIdObject.selectedIndex].value;

		var actorEmailIdObject = document.getElementById("actorEmailId");
		var actorEmailId = actorEmailIdObject.options[actorEmailIdObject.selectedIndex].value;
		
		var changedSince = document.getElementById("changedSince").value;
		
		var changeTypeObject = document.getElementById("changeType");
		var changeType = changeTypeObject.options[changeTypeObject.selectedIndex].value;

		
		// Now fill contentCenterB with the folder details.
		var url="/GloreeJava2/jsp/ChangeLog/displayChangeLog.jsp?";
		url = url + "changeFolderId=" + changeFolderId;
		url = url + "&actorEmailId=" + actorEmailId;
		url = url + "&changedSince=" + changedSince;
		url = url + "&changeType=" + changeType;
		
		url = url + "&sortBy=" + sortBy;
		
		
		
		document.getElementById("changeLogDiv").style.display = "block";
		document.getElementById("changeLogDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		fillOPCenterGeneric(url, "changeLogDiv");
	}
	
	
	
	function displayTracePanel(){
		
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		
		
		// Now fill contentCenterB with the folder details.
		document.getElementById("contentCenterF").style.display = "block";
		document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/TracePanel/displayTracePanel.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "contentCenterF");
	}
	

	function fillTracePanel(fromPage, toPage, type){
		
		// lets get the fromFolder id.
		var fromFolderIdObject = document.getElementById("fromFolderId");
		var fromFolderId = fromFolderIdObject.options[fromFolderIdObject.selectedIndex].value;
		if (fromFolderId == -1){
			alert ("Please select a folder where your From Requirements exist");
			fromFolderIdObject.focus();
			fromFolderId.style.backgroundColor="#FFCC99";
			return;
		}
	
		// lets get the toFolder id.
		var toFolderIdObject = document.getElementById("toFolderId");
		var toFolderId = toFolderIdObject.options[toFolderIdObject.selectedIndex].value;
		if (toFolderId == -1){
			alert ("Please select a folder where your To Requirements exist");
			toFolderIdObject.focus();
			toFolderId.style.backgroundColor="#FFCC99";
			return;
		}
	
		/*
		if (toFolderId == fromFolderId){
			alert ("Please select different From and To Folders");
			toFolderIdObject.focus();
			toFolderId.style.backgroundColor="#FFCC99";
			return;
		}
		*/
		var fromPageSize =  document.getElementById("fromPageSize").value;
		var toPageSize =  document.getElementById("toPageSize").value;
		
		// lets make sure that fromPageSize and toPageSize are numbers
		if (isNaN(fromPageSize)){
			alert ("Please enter a valid number for From Requirements Per Page");
			document.getElementById("fromPageSize").style.backgroundColor="#FFCC99";
			document.getElementById("fromPageSize").focus();
			return(0);
		}
		
		if (isNaN(toPageSize)){
			alert ("Please enter a valid number for To Requirements Per Page");
			document.getElementById("toPageSize").style.backgroundColor="#FFCC99";
			document.getElementById("toPageSize").focus();
			return(0);
		}
		
		var totalCells = fromPageSize * toPageSize;
		if ((type == "readWrite") && (totalCells > 400000)){
			alert ("You chose to display " + fromPageSize + " From Reqs * " + toPageSize + " To Reqs  = " + totalCells + " Trace Cells per page. " +
					" Please consider using Pagination or or changing the Column / Row size or the 'Read Only Trace Matrix Option' ");
			document.getElementById("fromPageSize").style.backgroundColor="#FFCC99";
			document.getElementById("fromPageSize").focus();
			return (0);
		}
		
		
		// lets get the FROM filter options.
		var danglingSearchFrom = "all";
		if (document.getElementById("danglingSearchFrom").checked == true) {
			danglingSearchFrom = "danglingOnly";
		}
		
		var orphanSearchFrom = "all";
		if (document.getElementById("orphanSearchFrom").checked == true) {
			orphanSearchFrom = "orphanOnly";
		}
		
		var completedSearchFrom = "all";
		if (document.getElementById("completedSearchFrom").checked == true) {
			completedSearchFrom = "completedOnly";
		}
		
		var incompleteSearchFrom = "all";
		if (document.getElementById("incompleteSearchFrom").checked == true) {
			incompleteSearchFrom = "incompleteOnly";
		}
		
		var suspectUpStreamSearchFrom = "all";
		if (document.getElementById("suspectUpStreamSearchFrom").checked == true) {
			suspectUpStreamSearchFrom = "suspectUpStreamOnly";
		}
		
		var suspectDownStreamSearchFrom = "all";
		if (document.getElementById("suspectDownStreamSearchFrom").checked == true) {
			suspectDownStreamSearchFrom = "suspectDownStreamOnly";
		}
		
		var lockedSearchFrom = "all";
		if (document.getElementById("lockedSearchFrom").checked == true) {
			lockedSearchFrom = "lockedOnly";
		}

		var includeSubFoldersSearchFrom = "no";
		if (document.getElementById("includeSubFoldersSearchFrom").checked == true) {
			includeSubFoldersSearchFrom = "includeSubFoldersOnly";
		}
		
		
		var inRTBaselineSearchObjectFrom = document.getElementById("inRTBaselineSearchFrom");
		var inRTBaselineSearchFrom;
		if (inRTBaselineSearchObjectFrom.selectedIndex > -1) {
			inRTBaselineSearchFrom = inRTBaselineSearchObjectFrom.options[inRTBaselineSearchObjectFrom.selectedIndex].value;
		}
		var changedAfterRTBaselineSearchObjectFrom = document.getElementById("changedAfterRTBaselineSearchFrom");
		var changedAfterRTBaselineSearchFrom;
		if (changedAfterRTBaselineSearchObjectFrom.selectedIndex > -1) {
			changedAfterRTBaselineSearchFrom = changedAfterRTBaselineSearchObjectFrom.options[changedAfterRTBaselineSearchObjectFrom.selectedIndex].value;
		}
		
		//since testingStatus is a multiple select, extra work to convert to string.
		var testingStatusSearchObjectFrom = document.getElementById("testingStatusSearchFrom");
		var testingStatusSearchFrom = "";
		for (var i = 0; i < testingStatusSearchObjectFrom.options.length; i++) {
			if (testingStatusSearchObjectFrom.options[i].selected) {
				testingStatusSearchFrom += testingStatusSearchObjectFrom.options[i].value + ','; 
			}
		}
		
		var nameSearchFrom = document.getElementById("nameSearchFrom").value;
		var descriptionSearchFrom = document.getElementById("descriptionSearchFrom").value;
		var ownerSearchFrom = document.getElementById("ownerSearchFrom").value;
		var externalURLSearchFrom = document.getElementById("externalURLSearchFrom").value;
		var approvedBySearchFrom = document.getElementById("approvedBySearchFrom").value;
		var rejectedBySearchFrom = document.getElementById("rejectedBySearchFrom").value;
		var pendingBySearchFrom = document.getElementById("pendingBySearchFrom").value;
		var traceToSearchFrom = document.getElementById("traceToSearchFrom").value;
		var traceFromSearchFrom = document.getElementById("traceFromSearchFrom").value;
		
		// since status is a multiple select, extra work to convert to
		// string.
		var statusSearchObjectFrom =  document.getElementById("statusSearchFrom");
		var statusSearchFrom = "";
		for (var i = 0; i < statusSearchObjectFrom.options.length; i++) {
			if (statusSearchObjectFrom.options[i].selected) {
				statusSearchFrom += statusSearchObjectFrom.options[i].value + ','; 
			}
		}
		
	
		//since priority is a multiple select, extra work to convert to string.
		var prioritySearchObjectFrom =  document.getElementById("prioritySearchFrom");
		var prioritySearchFrom = "";
		for (var i = 0; i < prioritySearchObjectFrom.options.length; i++) {
			if (prioritySearchObjectFrom.options[i].selected) {
				prioritySearchFrom += prioritySearchObjectFrom.options[i].value + ','; 
			}
		}
		
		//since pctComplete is a multiple select, extra work to convert to string.
		var pctCompleteSearchFrom =  document.getElementById("pctCompleteSearchFrom");
		if (isNaN(pctCompleteSearchFrom.value)){
			alert ("Please enter a valid number for percent complete");
			pctCompleteSearchFrom.style.backgroundColor="#FFCC99";
			pctCompleteSearchFrom.focus();
			return(0);
		}
		
		if ((pctCompleteSearchFrom.value < 0) || (pctCompleteSearchFrom.value > 100) ) {
			alert ("Please enter a valid number between 0 and 100 for percent complete");
			pctCompleteSearchFrom.style.backgroundColor="#FFCC99";
			pctCompleteSearchFrom.focus();
			return(0);
		}

		
		
		
		var url = "";
		if (type == "readWrite"){
			url="/GloreeJava2/jsp/TracePanel/fillTracePanel.jsp?fromPage=" + fromPage + "&toPage=" + toPage + "&fromFolderId=" + fromFolderId + "&toFolderId="  + toFolderId ;
		}
		if (type == "readOnly"){
			url="/GloreeJava2/jsp/TracePanel/fillTracePanelReadOnly.jsp?fromPage=" + fromPage + "&toPage=" + toPage + "&fromFolderId=" + fromFolderId + "&toFolderId="  + toFolderId ;
		}
		if (type == "export"){
			url="/GloreeJava2/jsp/TracePanel/fillTracePanelReadOnly.jsp?exportToExcel=true&fromPage=" + fromPage + "&toPage=" + toPage + "&fromFolderId=" + fromFolderId + "&toFolderId="  + toFolderId ;
		}
		
		url += "&fromPageSize=" + fromPageSize;
		url += "&toPageSize=" + toPageSize;
		
		url += "&danglingSearchFrom=" + encodeURIComponent(danglingSearchFrom);
		url += "&orphanSearchFrom=" + encodeURIComponent(orphanSearchFrom);
		url += "&completedSearchFrom=" + encodeURIComponent(completedSearchFrom);
		url += "&incompleteSearchFrom=" + encodeURIComponent(incompleteSearchFrom);
		url += "&suspectUpStreamSearchFrom=" + encodeURIComponent(suspectUpStreamSearchFrom);
		url += "&suspectDownStreamSearchFrom=" + encodeURIComponent(suspectDownStreamSearchFrom);
		url += "&lockedSearchFrom=" + encodeURIComponent(lockedSearchFrom);
		url += "&includeSubFoldersSearchFrom=" + encodeURIComponent(includeSubFoldersSearchFrom);

		url += "&inRTBaselineSearchFrom=" + encodeURIComponent(inRTBaselineSearchFrom);
		url += "&changedAfterRTBaselineSearchFrom=" + encodeURIComponent(changedAfterRTBaselineSearchFrom);
		url += "&testingStatusSearchFrom=" + encodeURIComponent(testingStatusSearchFrom);
		
		url += "&nameSearchFrom=" + encodeURIComponent(nameSearchFrom);
		url += "&descriptionSearchFrom=" + encodeURIComponent(descriptionSearchFrom);
		url += "&ownerSearchFrom=" + encodeURIComponent(ownerSearchFrom);
		url += "&externalURLSearchFrom=" + encodeURIComponent(externalURLSearchFrom);
		url += "&approvedBySearchFrom=" + encodeURIComponent(approvedBySearchFrom);
		url += "&rejectedBySearchFrom=" + encodeURIComponent(rejectedBySearchFrom);
		url += "&pendingBySearchFrom=" + encodeURIComponent(pendingBySearchFrom);
		url += "&traceToSearchFrom=" + encodeURIComponent(traceToSearchFrom);
		url += "&traceFromSearchFrom=" + encodeURIComponent(traceFromSearchFrom);
		
		url += "&statusSearchFrom=" + encodeURIComponent(statusSearchFrom);
		url += "&prioritySearchFrom=" + encodeURIComponent(prioritySearchFrom);
		url += "&pctCompleteSearchFrom=" + encodeURIComponent(pctCompleteSearchFrom.value);
		
		// Now, lets get the custom attributes.
		// we use the attributeIdString to split and figure out which
		// attributes
		// to look for.
		// for these attributes , we get the value, build the URL string and
		// .
		// we will use the same logic to get the attribute values and search
		// 
		var attributeIdStringFrom = document.getElementById("attributeIdStringFrom");
		

		attributeIds = attributeIdStringFrom.value.split('##');
		for (a in attributeIds){
			splitA = attributeIds[a].split('#');
			var id = splitA[0];
			var type = splitA[1];
		
			if (id) {
				var attribute = document.getElementById(id);
				if (type == 'DropDown'){
					// since this is a multi select, iterate through this to get selected values.
		
					var dropDownSearch = "";
					for (var i = 0; i < attribute.options.length; i++) {
						if (attribute.options[i].selected) {
							dropDownSearch += attribute.options[i].value + ':##:';
		
						}
					}
					// add the attributeid / concatenated selected values.
					url = url + '&' +  id + '='+  encodeURIComponent(dropDownSearch) ;
		
				}
				else {
					url = url + '&' + id + '='+  encodeURIComponent(attribute.value) ;
		
				}
			}
		}	
		
		// now lets add the attributeIdString, so that the server can break up the custom
		// values based on this.
		url += '&attributeIdStringFrom=' + encodeURIComponent(attributeIdStringFrom.value);

		
		
		
		// lets get the TO filter options.
		var danglingSearchTo = "all";
		if (document.getElementById("danglingSearchTo").checked == true) {
			danglingSearchTo = "danglingOnly";
		}
		
		var orphanSearchTo = "all";
		if (document.getElementById("orphanSearchTo").checked == true) {
			orphanSearchTo = "orphanOnly";
		}
		
		var completedSearchTo = "all";
		if (document.getElementById("completedSearchTo").checked == true) {
			completedSearchTo = "completedOnly";
		}
		
		var incompleteSearchTo = "all";
		if (document.getElementById("incompleteSearchTo").checked == true) {
			incompleteSearchTo = "incompleteOnly";
		}
		
		var suspectUpStreamSearchTo = "all";
		if (document.getElementById("suspectUpStreamSearchTo").checked == true) {
			suspectUpStreamSearchTo = "suspectUpStreamOnly";
		}
		
		var suspectDownStreamSearchTo = "all";
		if (document.getElementById("suspectDownStreamSearchTo").checked == true) {
			suspectDownStreamSearchTo = "suspectDownStreamOnly";
		}
		
		var lockedSearchTo = "all";
		if (document.getElementById("lockedSearchTo").checked == true) {
			lockedSearchTo = "lockedOnly";
		}

		var includeSubFoldersSearchTo = "no";
		if (document.getElementById("includeSubFoldersSearchTo").checked == true) {
			includeSubFoldersSearchTo = "includeSubFoldersOnly";
		}
		
		
		var inRTBaselineSearchObjectTo = document.getElementById("inRTBaselineSearchTo");
		var inRTBaselineSearchTo;
		if (inRTBaselineSearchObjectTo.selectedIndex > -1) {
			inRTBaselineSearchTo = inRTBaselineSearchObjectTo.options[inRTBaselineSearchObjectTo.selectedIndex].value;
		}
		var changedAfterRTBaselineSearchObjectTo = document.getElementById("changedAfterRTBaselineSearchTo");
		var changedAfterRTBaselineSearchTo;
		if (changedAfterRTBaselineSearchObjectTo.selectedIndex > -1) {
			changedAfterRTBaselineSearchTo = changedAfterRTBaselineSearchObjectTo.options[changedAfterRTBaselineSearchObjectTo.selectedIndex].value;
		}
		
		//since testingStatus is a multiple select, extra work to convert to string.
		var testingStatusSearchObjectTo = document.getElementById("testingStatusSearchTo");
		var testingStatusSearchTo = "";
		for (var i = 0; i < testingStatusSearchObjectTo.options.length; i++) {
			if (testingStatusSearchObjectTo.options[i].selected) {
				testingStatusSearchTo += testingStatusSearchObjectTo.options[i].value + ','; 
			}
		}
		
		var nameSearchTo = document.getElementById("nameSearchTo").value;
		var descriptionSearchTo = document.getElementById("descriptionSearchTo").value;
		var ownerSearchTo = document.getElementById("ownerSearchTo").value;
		var externalURLSearchTo = document.getElementById("externalURLSearchTo").value;
		var approvedBySearchTo = document.getElementById("approvedBySearchTo").value;
		var rejectedBySearchTo = document.getElementById("rejectedBySearchTo").value;
		var pendingBySearchTo = document.getElementById("pendingBySearchTo").value;
		var traceToSearchTo = document.getElementById("traceToSearchTo").value;
		var traceFromSearchTo = document.getElementById("traceFromSearchTo").value;
		
		// since status is a multiple select, extra work to convert to
		// string.
		var statusSearchObjectTo =  document.getElementById("statusSearchTo");
		var statusSearchTo = "";
		for (var i = 0; i < statusSearchObjectTo.options.length; i++) {
			if (statusSearchObjectTo.options[i].selected) {
				statusSearchTo += statusSearchObjectTo.options[i].value + ','; 
			}
		}
		
	
		//since priority is a multiple select, extra work to convert to string.
		var prioritySearchObjectTo =  document.getElementById("prioritySearchTo");
		var prioritySearchTo = "";
		for (var i = 0; i < prioritySearchObjectTo.options.length; i++) {
			if (prioritySearchObjectTo.options[i].selected) {
				prioritySearchTo += prioritySearchObjectTo.options[i].value + ','; 
			}
		}
		
		//since pctComplete is a multiple select, extra work to convert to string.
		var pctCompleteSearchTo =  document.getElementById("pctCompleteSearchTo");
		if (isNaN(pctCompleteSearchTo.value)){
			alert ("Please enter a valid number for percent complete");
			pctCompleteSearchTo.style.backgroundColor="#FFCC99";
			pctCompleteSearchTo.focus();
			return(0);
		}
		
		if ((pctCompleteSearchTo.value < 0) || (pctCompleteSearchTo.value > 100) ) {
			alert ("Please enter a valid number between 0 and 100 for percent complete");
			pctCompleteSearchTo.style.backgroundColor="#FFCC99";
			pctCompleteSearchTo.focus();
			return(0);
		}

		url += "&danglingSearchTo=" + encodeURIComponent(danglingSearchTo);
		url += "&orphanSearchTo=" + encodeURIComponent(orphanSearchTo);
		url += "&completedSearchTo=" + encodeURIComponent(completedSearchTo);
		url += "&incompleteSearchTo=" + encodeURIComponent(incompleteSearchTo);
		url += "&suspectUpStreamSearchTo=" + encodeURIComponent(suspectUpStreamSearchTo);
		url += "&suspectDownStreamSearchTo=" + encodeURIComponent(suspectDownStreamSearchTo);
		url += "&lockedSearchTo=" + encodeURIComponent(lockedSearchTo);
		url += "&includeSubFoldersSearchTo=" + encodeURIComponent(includeSubFoldersSearchTo);

		url += "&inRTBaselineSearchTo=" + encodeURIComponent(inRTBaselineSearchTo);
		url += "&changedAfterRTBaselineSearchTo=" + encodeURIComponent(changedAfterRTBaselineSearchTo);
		url += "&testingStatusSearchTo=" + encodeURIComponent(testingStatusSearchTo);
		
		url += "&nameSearchTo=" + encodeURIComponent(nameSearchTo);
		url += "&descriptionSearchTo=" + encodeURIComponent(descriptionSearchTo);
		url += "&ownerSearchTo=" + encodeURIComponent(ownerSearchTo);
		url += "&externalURLSearchTo=" + encodeURIComponent(externalURLSearchTo);
		url += "&approvedBySearchTo=" + encodeURIComponent(approvedBySearchTo);
		url += "&rejectedBySearchTo=" + encodeURIComponent(rejectedBySearchTo);
		url += "&pendingBySearchTo=" + encodeURIComponent(pendingBySearchTo);
		url += "&traceToSearchTo=" + encodeURIComponent(traceToSearchTo);
		url += "&traceFromSearchTo=" + encodeURIComponent(traceFromSearchTo);
		
		url += "&statusSearchTo=" + encodeURIComponent(statusSearchTo);
		url += "&prioritySearchTo=" + encodeURIComponent(prioritySearchTo);
		url += "&pctCompleteSearchTo=" + encodeURIComponent(pctCompleteSearchTo.value);
		
		// Now, lets get the custom attributes.
		// we use the attributeIdString to split and figure out which
		// attributes
		// to look for.
		// for these attributes , we get the value, build the URL string and
		// .
		// we will use the same logic to get the attribute values and search
		// 
		var attributeIdStringTo = document.getElementById("attributeIdStringTo");
		

		attributeIds = attributeIdStringTo.value.split('##');
		for (a in attributeIds){
			splitA = attributeIds[a].split('#');
			var id = splitA[0];
			var type = splitA[1];
		
			if (id) {
				var attribute = document.getElementById(id);
				if (type == 'DropDown'){
					// since this is a multi select, iterate through this to get selected values.
		
					var dropDownSearch = "";
					for (var i = 0; i < attribute.options.length; i++) {
						if (attribute.options[i].selected) {
							dropDownSearch += attribute.options[i].value + ':##:';
		
						}
					}
					// add the attributeid / concatenated selected values.
					url = url + '&' +  id + '='+  encodeURIComponent(dropDownSearch) ;
		
				}
				else {
					url = url + '&' + id + '='+  encodeURIComponent(attribute.value) ;
		
				}
			}
		}	
		
		// now lets add the attributeIdString, so that the server can break up the custom
		// values based on this.
		url += '&attributeIdStringTo=' + encodeURIComponent(attributeIdStringTo.value);

		

		var displayHeaderFromObject = document.getElementById("displayHeaderFrom");
		var displayHeaderFrom = displayHeaderFromObject.options[displayHeaderFromObject.selectedIndex].value;
		url += "&displayHeaderFrom=" + displayHeaderFrom;
		
		var displayHeaderToObject = document.getElementById("displayHeaderTo");
		var displayHeaderTo = displayHeaderToObject.options[displayHeaderToObject.selectedIndex].value;
		url += "&displayHeaderTo=" + displayHeaderTo;
		
		url += "&bustcache=" + new Date().getTime() ;		
		
		
		document.getElementById("tracePanelDiv").style.display = "block";
		document.getElementById("tracePanelDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		// lets collapse the left panel
		//layout.getUnitByPosition('left').collapse();
		document.getElementById("traePanelHomeDiv").style.display="none"
		document.getElementById("expandTracePanelHomeDiv").style.display="block";
		
		fillOPCenterGeneric(url, "tracePanelDiv");
	}
	
	function fillFilterDiv(filterType){
		if (filterType == "from"){
			var fromFolderIdObject = document.getElementById("fromFolderId");
			var fromFolderId = fromFolderIdObject.options[fromFolderIdObject.selectedIndex].value;
			if (fromFolderId == -1){
				return;
			}
			var url="/GloreeJava2/jsp/TracePanel/fillFilterDiv.jsp?fromFolderId=" + fromFolderId;
			url += "&bustcache=" + new Date().getTime() ;
			
			document.getElementById("fromFilterDiv").style.display = "block";
			document.getElementById("fromFilterDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			fillOPCenterGeneric(url, "fromFilterDiv");
		}
		if (filterType == "to"){
			var toFolderIdObject = document.getElementById("toFolderId");
			var toFolderId = toFolderIdObject.options[toFolderIdObject.selectedIndex].value;
			if (toFolderId == -1){
				return;
			}
			var url="/GloreeJava2/jsp/TracePanel/fillFilterDiv.jsp?toFolderId=" + toFolderId;
			url += "&bustcache=" + new Date().getTime() ;
			
			document.getElementById("toFilterDiv").style.display = "block";
			document.getElementById("toFilterDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			fillOPCenterGeneric(url, "toFilterDiv");
		}
	}
		
	

	// this function is used to add filter conditions
	// to both list report and trace tree reports.
	function addTraceMatrixFilterCondition(type) {
		var addAFilter = document.getElementById("addAFilter" + type);
		
		if (addAFilter.selectedIndex == 1) {
			document.getElementById('danglingSearch' + type).checked = true;
			document.getElementById('danglingFilterDiv' + type).style.display = 'block';
		}
		if (addAFilter.selectedIndex == 2) {
			document.getElementById('orphanSearch' + type).checked = true;
			document.getElementById('orphanFilterDiv' + type).style.display = 'block';
		}
		if (addAFilter.selectedIndex == 3) {
			document.getElementById('completedSearch' + type).checked = true;
			document.getElementById('completedFilterDiv' + type).style.display = 'block';
		}
		if (addAFilter.selectedIndex == 4) {
			document.getElementById('incompleteSearch' + type).checked = true;
			document.getElementById('incompleteFilterDiv' + type).style.display = 'block';
		}
		if (addAFilter.selectedIndex == 5) {
			document.getElementById('suspectUpStreamSearch' + type).checked = true;
			document.getElementById('suspectUpStreamFilterDiv' + type).style.display = 'block';
		}
		if (addAFilter.selectedIndex == 6) {
			document.getElementById('suspectDownStreamSearch' + type).checked = true;
			document.getElementById('suspectDownStreamFilterDiv' + type).style.display = 'block';
		}
		
		if (addAFilter.selectedIndex == 7) {
			document.getElementById('lockedSearch' + type).checked = true;
			document.getElementById('lockedFilterDiv' + type).style.display = 'block';
		}
		
		if (addAFilter.selectedIndex == 8) {
			document.getElementById('includeSubFoldersSearch' + type).checked = true;
			document.getElementById('includeSubFoldersFilterDiv' + type).style.display = 'block';
		}

		/////////////////////////////////////////////////////
		if (addAFilter.selectedIndex == 10) {
			document.getElementById('inRTBaselineFilterDiv' + type ).style.display = 'block';
		}

		if (addAFilter.selectedIndex == 11) {
			document.getElementById('changedAfterRTBaselineFilterDiv' + type ).style.display = 'block';
		}
		

		/////////////////////////////////////////////////////
		if (addAFilter.selectedIndex == 13) {
			document.getElementById('nameFilterDiv' + type ).style.display = 'block';
		}

		if (addAFilter.selectedIndex == 14) {
			document.getElementById('descriptionFilterDiv' + type ).style.display = 'block';
		}

		if (addAFilter.selectedIndex == 15) {
			document.getElementById('ownerFilterDiv' + type ).style.display = 'block';
		}

		if (addAFilter.selectedIndex == 16) {
			document.getElementById('externalURLFilterDiv' + type ).style.display = 'block';
		}

				
		/////////////////////////////////////////////////////
		if (addAFilter.selectedIndex == 18) {
			document.getElementById('pctCompleteFilterDiv' + type ).style.display = 'block';
		}

		if (addAFilter.selectedIndex == 19) {
			document.getElementById('statusFilterDiv' + type ).style.display = 'block';
		}

		if (addAFilter.selectedIndex == 20) {
			document.getElementById('priorityFilterDiv' + type ).style.display = 'block';
		}

		if (addAFilter.selectedIndex == 21) {
			document.getElementById('testingStatusFilterDiv' + type ).style.display = 'block';
		}

		
		/////////////////////////////////////////////////////
		if (addAFilter.selectedIndex == 23) {
			document.getElementById('approvedByFilterDiv' + type ).style.display = 'block';
		}

		if (addAFilter.selectedIndex == 24) {
			document.getElementById('rejectedByFilterDiv' + type ).style.display = 'block';
		}

		if (addAFilter.selectedIndex == 25) {
			document.getElementById('pendingByFilterDiv' + type ).style.display = 'block';
		}


		/////////////////////////////////////////////////////
		if (addAFilter.selectedIndex == 27) {
			document.getElementById('traceToFilterDiv' + type ).style.display = 'block';
		}

		if (addAFilter.selectedIndex == 28) {
			document.getElementById('traceFromFilterDiv' + type ).style.display = 'block';
		}
		
		// lets handle custom attributes.
		// custom attributes are dynamic in nature. So at the time of writing the  javascript
		// we won't know what to expect.
		// however, the addAFilter.selectedIndex.value will have the id of the div that 
		// we will need to unblock.
		var customAttributeDivName = addAFilter[addAFilter.selectedIndex].value
		// lets see if the option value picked up is custom attribute
		var pos= customAttributeDivName.indexOf("customA")
		if (pos>=0){
			// lets get the div and display it.
			document.getElementById(customAttributeDivName + type).style.display = 'block';
		}

	}
	function traceActionInTracePanel(fromRequirementId,toRequirementId, traceTo, traceCellId){
	
		var traceActionObject = document.getElementById("select" + traceCellId);
		
		var traceAction = '-1';
		try {
			traceAction = traceActionObject.options[traceActionObject.selectedIndex].value;
		}
		catch (err){
			// do nothing.
		}
		
		if (traceAction == '-1'){
			return;
		}
		
		// lets get the lastTraceObject and lastMenuItem object and clear their selections.
		var lastTraceObject = document.getElementById("lastTrace");
		
		if (document.getElementById(lastTraceObject.value) != null){
			document.getElementById(lastTraceObject.value).style.background="white";
		}
		lastTraceObject.value = "<%=traceCellId%>";
		

		var lastMenuItemObject = document.getElementById("lastMenuItem");

		if (document.getElementById(lastMenuItemObject.value) != null){
			//document.getElementById(lastMenuItemObject.value).style.display="none";
		}
		
		var traceFrom = "";
		
		var url="/GloreeJava2/servlet/RequirementAction?action=traceActionInTracePanel&fromRequirementId=" + fromRequirementId ;
		url +=  "&toRequirementId=" + toRequirementId  ;
		url +=  "&createTraceTo=" + traceTo  ;
		url += "&createTraceFrom=" + traceFrom;
		url += "&traceAction=" + traceAction;
		url += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById(traceCellId).style.display = "block";
		document.getElementById(traceCellId).innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		fillOPCenterGeneric(url, traceCellId);
	}

	
	
	function bulkTraceInTracePanel(drivingRequirementTag){
		
		var drivingRequirementObject = document.getElementById("select" + drivingRequirementTag);
		var traceAction = drivingRequirementObject.options[drivingRequirementObject.selectedIndex].value;

		if (traceAction == '-1'){
			return;
		}
		// lets iterate through all the div objects that have the same class as the drivingRequiremenTag.
		 var traceSelects  = getElementsByClassName(drivingRequirementTag , "select");
		 for (var i = traceSelects.length - 1; i >= 0; i--)
		 {
			 var selectObject = traceSelects[i];
			 selectObject.value=traceAction;
			 if (selectObject.onchange){
				 selectObject.onchange();
			 }
		 }
	}


	
	/*
	Developed by Robert Nyman, http://www.robertnyman.com
	Code/licensing: http://code.google.com/p/getelementsbyclassname/
*/	
var getElementsByClassName = function (className, tag, elm){
	if (document.getElementsByClassName) {
		getElementsByClassName = function (className, tag, elm) {
			elm = elm || document;
			var elements = elm.getElementsByClassName(className),
				nodeName = (tag)? new RegExp("\\b" + tag + "\\b", "i") : null,
				returnElements = [],
				current;
			for(var i=0, il=elements.length; i<il; i+=1){
				current = elements[i];
				if(!nodeName || nodeName.test(current.nodeName)) {
					returnElements.push(current);
				}
			}
			return returnElements;
		};
	}
	else if (document.evaluate) {
		getElementsByClassName = function (className, tag, elm) {
			tag = tag || "*";
			elm = elm || document;
			var classes = className.split(" "),
				classesToCheck = "",
				xhtmlNamespace = "http://www.w3.org/1999/xhtml",
				namespaceResolver = (document.documentElement.namespaceURI === xhtmlNamespace)? xhtmlNamespace : null,
				returnElements = [],
				elements,
				node;
			for(var j=0, jl=classes.length; j<jl; j+=1){
				classesToCheck += "[contains(concat(' ', @class, ' '), ' " + classes[j] + " ')]";
			}
			try	{
				elements = document.evaluate(".//" + tag + classesToCheck, elm, namespaceResolver, 0, null);
			}
			catch (e) {
				elements = document.evaluate(".//" + tag + classesToCheck, elm, null, 0, null);
			}
			while ((node = elements.iterateNext())) {
				returnElements.push(node);
			}
			return returnElements;
		};
	}
	else {
		getElementsByClassName = function (className, tag, elm) {
			tag = tag || "*";
			elm = elm || document;
			var classes = className.split(" "),
				classesToCheck = [],
				elements = (tag === "*" && elm.all)? elm.all : elm.getElementsByTagName(tag),
				current,
				returnElements = [],
				match;
			for(var k=0, kl=classes.length; k<kl; k+=1){
				classesToCheck.push(new RegExp("(^|\\s)" + classes[k] + "(\\s|$)"));
			}
			for(var l=0, ll=elements.length; l<ll; l+=1){
				current = elements[l];
				match = false;
				for(var m=0, ml=classesToCheck.length; m<ml; m+=1){
					match = classesToCheck[m].test(current.className);
					if (!match) {
						break;
					}
				}
				if (match) {
					returnElements.push(current);
				}
			}
			return returnElements;
		};
	}
	return getElementsByClassName(className, tag, elm);
};	


function displayScheduler(){
	
	// set the other content centers to empty.
	document.getElementById("contentCenterA").style.display = "none";
	document.getElementById("contentCenterB").style.display = "none";
	document.getElementById("contentCenterC").style.display = "none";
	document.getElementById("contentCenterD").style.display = "none";
	document.getElementById("contentCenterE").style.display = "none";
	
	
	
	// Now fill contentCenterB with the folder details.
	document.getElementById("contentCenterF").style.display = "block";
	document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
	var url="/GloreeJava2/jsp/Scheduler/displayScheduler.jsp?";
	url += "&bustcache=" + new Date().getTime() ;
	
	xmlHttpOPCenterB =GetXmlHttpObject();
	xmlHttpOPCenterB.onreadystatechange=function() {
		if(xmlHttpOPCenterB.readyState==4){
			document.getElementById("contentCenterF").innerHTML=xmlHttpOPCenterB.responseText;				
			
			// now lets fill the getAlreadyScheduledReportsdiv.
			var url="/GloreeJava2/jsp/Scheduler/displayPreviouslyScheduledReports.jsp?";
			url += "&bustcache=" + new Date().getTime() ;

			document.getElementById("previouslyScheduledReportsDiv").style.display = "block";
			document.getElementById("previouslyScheduledReportsDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";

			fillOPCenterGeneric(url, "previouslyScheduledReportsDiv");
		}
	}
	
	xmlHttpOPCenterB.open("GET",url,true);
	xmlHttpOPCenterB.send(null);

}

function displaySchedulerReportsInFolder(){

	var folderIdObject = document.getElementById("folderId");
	var folderId = folderIdObject.options[folderIdObject.selectedIndex].value;
	if (folderId == -1){
		alert ("Please select a folder where your To Requirements exist");
		folderIdObject.focus();
		folderIdObject.style.backgroundColor="#FFCC99";
		return;
	}
	
	
	// Now fill contentCenterB with the folder details.
	document.getElementById("schedulerReportsInFolderDiv").style.display = "block";
	document.getElementById("schedulerReportsInFolderDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
	var url="/GloreeJava2/jsp/Scheduler/displaySchedulerReportsInFolder.jsp?";
	url += "&folderId=" + folderId;
	url += "&bustcache=" + new Date().getTime() ;
	
	fillOPCenterGeneric(url, "schedulerReportsInFolderDiv");
}



function displaySchedulerReportInfo(){

	var reportIdObject = document.getElementById("reportId");
	var reportId = reportIdObject.options[reportIdObject.selectedIndex].value;
	if (reportId == -1){
		alert ("Please select a Report that needs to be scheduled");
		reportIdObject.focus();
		reportIdObject.style.backgroundColor="#FFCC99";
		return;
	}
	
	
	// Now fill contentCenterB with the folder details.
	document.getElementById("reportInfoDiv").style.display = "block";
	document.getElementById("reportInfoDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
	var url="/GloreeJava2/jsp/Scheduler/displaySchedulerReportInfo.jsp?";
	url += "&reportId=" + reportId;
	url += "&bustcache=" + new Date().getTime() ;
	
	fillOPCenterGeneric(url, "reportInfoDiv");
}

function displayScheduleANewReportForm(){
	
	// Now fill contentCenterB with the folder details.
	document.getElementById("schedulerHomeDiv").style.display = "block";
	document.getElementById("schedulerHomeDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
	var url="/GloreeJava2/jsp/Scheduler/displayScheduleANewReportForm.jsp?";
	url += "&bustcache=" + new Date().getTime() ;
	
	fillOPCenterGeneric(url, "schedulerHomeDiv");
}



function editASceduledReportForm(scheduledReportId){
	
	// Now fill contentCenterB with the folder details.
	document.getElementById("schedulerHomeDiv").style.display = "block";
	document.getElementById("schedulerHomeDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
	var url="/GloreeJava2/jsp/Scheduler/editAScheduledReportForm.jsp?";
	url += "scheduledReportId=" + scheduledReportId ;
	url += "&bustcache=" + new Date().getTime() ;
	
	fillOPCenterGeneric(url, "schedulerHomeDiv");
}


function scheduleReport(){

	var reportIdObject = document.getElementById("reportId");
	var reportId = reportIdObject.options[reportIdObject.selectedIndex].value;
	if (reportId == -1){
		alert ("Please select a Report to schedule");
		reportIdObject.focus();
		reportIdObject.style.backgroundColor="#FFCC99";
		return;
	}
	
	// lets get the To and CC Values.
	var to = document.getElementById("to").value ;
	// lets ensure that to has only valid email ids.
	// first replace any occurence of space or ; with , 
	if (to != null){ 
		to = to.replace(" ", ",");
		
		to = to.replace(";", ",");
		
		// some of the logic may create one or more of ,, . so we need to replace them with , . 
		
		to = to.replace("/,+/", ",");
		
		// now lets split each one of these email id and see if they are valid.
		toArray = to.split(',');
		
		for (t in toArray){
			var currentTo = toArray[t];
			currentTo = currentTo.replace(" ", "");
			if ((currentTo != null) && (currentTo.length > 0)){
				if (echeck(currentTo)==false){
					alert ("Your email id " + currentTo + " is not formatted correctly. Please fix it.");
					document.getElementById("to").focus();
					document.getElementById("to").style.backgroundColor="#FFCC99";
					return(0);
				}
			}
		}
	}
	
	var cc = document.getElementById("cc").value ;
	// lets ensure that cc has only valid email ids.
	// first replace any occurence of space or ; with , 
	if (cc != null){ 
		cc = cc.replace(" ", ",");
		cc = cc.replace(";", ",");
		
		// some of the logic may create one or more of ,, . so we need to replace them with , . 
		cc = cc.replace("/,+/", ",");
		
		// now lets split each one of these email id and see if they are valid.
		ccArray = cc.split(',');
		for (t in ccArray){
			var currentcc = ccArray[t];
			currentcc = currentcc.replace(" ", "");
			if ((currentcc != null) && (currentcc.length > 0)){
				currentcc = currentcc.replace(" ", "");
				if (echeck(currentcc)==false){
					alert ("Your email id " + currentcc + " is not formatted correctly. Please fix it.");
					document.getElementById("cc").focus();
					document.getElementById("cc").style.backgroundColor="#FFCC99";
					return(0);
				}
			}
		}
	}
	
	var attachmentObject = document.getElementById("attachmentType");
	var attachmentType = attachmentObject.options[attachmentObject.selectedIndex].value;
	subjectValue = document.getElementById("subject").value ;
	messageValue = document.getElementById("message").value;
	
	if ((to == "") && (cc == "")){
		alert ('Please provide at least one email address');
		document.getElementById("to").focus();
		document.getElementById("to").style.backgroundColor="#FFCC99"; 
		return(0);
	}
	
	if (subjectValue == ""){
		alert ('Email Subject can not be empty');
		document.getElementById("subject").focus();
		document.getElementById("subject").style.backgroundColor="#FFCC99"; 
		return(0);
	}
	if (messageValue == ""){
		alert ('Email Message can not be empty');
		document.getElementById("message").focus();
		document.getElementById("message").style.backgroundColor="#FFCC99";
		return(0);
	}

	// lets get the sendReportOn Value
	var runTaskOnObject = document.getElementById("runTaskOn");
	var runTaskOn = "";
	for (var i = 0; i < runTaskOnObject.options.length; i++) {
		if (runTaskOnObject.options[i].selected) {
			runTaskOn += runTaskOnObject.options[i].value + ','; 
		}
	}
	
	if (runTaskOn  == ""){
		alert ('Please select at least one day to schedule the report');
		document.getElementById("runTaskOn").focus();
		document.getElementById("runTaskOn").style.backgroundColor="#FFCC99";
		return(0);
	}

	// Now fill contentCenterB with the folder details.
	document.getElementById("reportInfoDiv").style.display = "block";
	document.getElementById("reportInfoDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
	var url="/GloreeJava2/jsp/Scheduler/scheduleReport.jsp?";
	url += "&reportId=" + reportId;
	url += "&toEmailAddresses=" + encodeURIComponent(to);
	url += "&ccEmailAddresses=" + encodeURIComponent(cc);
	url += "&attachmentType=" + encodeURIComponent(attachmentType);
	url += "&subjectValue=" + encodeURIComponent(subjectValue);
	url += "&messageValue=" + encodeURIComponent(messageValue);
	url += "&runTaskOn=" + encodeURIComponent(runTaskOn);
	url += "&bustcache=" + new Date().getTime() ;
	
	document.getElementById("reportInfoDiv").style.display = "block";
	document.getElementById("reportInfoDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";

	xmlHttpOPCenterB =GetXmlHttpObject();
	xmlHttpOPCenterB.onreadystatechange=function() {
		if(xmlHttpOPCenterB.readyState==4){
			document.getElementById("reportInfoDiv").innerHTML=xmlHttpOPCenterB.responseText;				
		}
	}
	
	xmlHttpOPCenterB.open("GET",url,true);
	xmlHttpOPCenterB.send(null);

}




function updateAScheduledReport(scheduledReportId, reportId){


	// lets get the To and CC Values.
	var to = document.getElementById("to").value ;
	// lets ensure that to has only valid email ids.
	// first replace any occurence of space or ; with , 
	if (to != null){ 
		to = to.replace(" ", ",");
		
		to = to.replace(";", ",");
		
		// some of the logic may create one or more of ,, . so we need to replace them with , . 
		
		to = to.replace("/,+/", ",");
		
		// now lets split each one of these email id and see if they are valid.
		toArray = to.split(',');
		
		for (t in toArray){
			var currentTo = toArray[t];
			currentTo = currentTo.replace(" ", "");
			if ((currentTo != null) && (currentTo.length > 0)){
				if (echeck(currentTo)==false){
					alert ("Your email id " + currentTo + " is not formatted correctly. Please fix it.");
					document.getElementById("to").focus();
					document.getElementById("to").style.backgroundColor="#FFCC99";
					return(0);
				}
			}
		}
	}
	
	var cc = document.getElementById("cc").value ;
	// lets ensure that cc has only valid email ids.
	// first replace any occurence of space or ; with , 
	if (cc != null){ 
		cc = cc.replace(" ", ",");
		cc = cc.replace(";", ",");
		
		// some of the logic may create one or more of ,, . so we need to replace them with , . 
		cc = cc.replace("/,+/", ",");
		
		// now lets split each one of these email id and see if they are valid.
		ccArray = cc.split(',');
		for (t in ccArray){
			var currentcc = ccArray[t];
			currentcc = currentcc.replace(" ", "");
			if ((currentcc != null) && (currentcc.length > 0)){
				currentcc = currentcc.replace(" ", "");
				if (echeck(currentcc)==false){
					alert ("Your email id " + currentcc + " is not formatted correctly. Please fix it.");
					document.getElementById("cc").focus();
					document.getElementById("cc").style.backgroundColor="#FFCC99";
					return(0);
				}
			}
		}
	}
	
	var attachmentObject = document.getElementById("attachmentType");
	var attachmentType = attachmentObject.options[attachmentObject.selectedIndex].value;
	subjectValue = document.getElementById("subject").value ;
	messageValue = document.getElementById("message").value;
	
	if ((to == "") && (cc == "")){
		alert ('Please provide at least one email address');
		document.getElementById("to").focus();
		document.getElementById("to").style.backgroundColor="#FFCC99"; 
		return(0);
	}
	
	if (subjectValue == ""){
		alert ('Email Subject can not be empty');
		document.getElementById("subject").focus();
		document.getElementById("subject").style.backgroundColor="#FFCC99"; 
		return(0);
	}
	if (messageValue == ""){
		alert ('Email Message can not be empty');
		document.getElementById("message").focus();
		document.getElementById("message").style.backgroundColor="#FFCC99";
		return(0);
	}

	// lets get the sendReportOn Value
	var runTaskOnObject = document.getElementById("runTaskOn");
	var runTaskOn = "";
	for (var i = 0; i < runTaskOnObject.options.length; i++) {
		if (runTaskOnObject.options[i].selected) {
			runTaskOn += runTaskOnObject.options[i].value + ','; 
		}
	}
	
	if (runTaskOn  == ""){
		alert ('Please select at least one day to schedule the report');
		document.getElementById("runTaskOn").focus();
		document.getElementById("runTaskOn").style.backgroundColor="#FFCC99";
		return(0);
	}

	// Now fill contentCenterB with the folder details.
	document.getElementById("reportInfoDiv").style.display = "block";
	document.getElementById("reportInfoDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
	var url="/GloreeJava2/jsp/Scheduler/editAScheduledReport.jsp?";
	url += "scheduledReportId=" + scheduledReportId;
	url += "&reportId=" + reportId;
	url += "&toEmailAddresses=" + encodeURIComponent(to);
	url += "&ccEmailAddresses=" + encodeURIComponent(cc);
	url += "&attachmentType=" + encodeURIComponent(attachmentType);
	url += "&subjectValue=" + encodeURIComponent(subjectValue);
	url += "&messageValue=" + encodeURIComponent(messageValue);
	url += "&runTaskOn=" + encodeURIComponent(runTaskOn);
	url += "&bustcache=" + new Date().getTime() ;
	
	document.getElementById("reportInfoDiv").style.display = "block";
	document.getElementById("reportInfoDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";

	xmlHttpOPCenterB =GetXmlHttpObject();
	xmlHttpOPCenterB.onreadystatechange=function() {
		if(xmlHttpOPCenterB.readyState==4){
			document.getElementById("reportInfoDiv").innerHTML=xmlHttpOPCenterB.responseText;				
		}
	}
	
	xmlHttpOPCenterB.open("GET",url,true);
	xmlHttpOPCenterB.send(null);

}



function deleteAScheduledReport(scheduledReportId, reportId){


	// Now fill contentCenterB with the folder details.
	document.getElementById("reportInfoDiv").style.display = "block";
	document.getElementById("reportInfoDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
	var url="/GloreeJava2/jsp/Scheduler/deleteAScheduledReport.jsp?";
	url += "scheduledReportId=" + scheduledReportId;
	url += "&reportId=" + reportId;
	url += "&bustcache=" + new Date().getTime() ;
	
	
	xmlHttpOPCenterB =GetXmlHttpObject();
	xmlHttpOPCenterB.onreadystatechange=function() {
		if(xmlHttpOPCenterB.readyState==4){
			document.getElementById("reportInfoDiv").innerHTML=xmlHttpOPCenterB.responseText;				
		}
	}
	
	xmlHttpOPCenterB.open("GET",url,true);
	xmlHttpOPCenterB.send(null);

}



function displayTraceMatrix(){
		
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
	
		// lets collapse the left panel
		//layout.getUnitByPosition('left').collapse();
		
		
		// Now fill contentCenterB with the folder details.
		document.getElementById("contentCenterF").style.display = "block";
		document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/TraceMatrix/displayTraceMatrix.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "contentCenterF");
	}
	

	function fillTraceMatrixFromRequirements(){
		
		// lets get the fromFolder id.
		var fromFolderIdObject = document.getElementById("fromFolderId");
		var fromFolderId = fromFolderIdObject.options[fromFolderIdObject.selectedIndex].value;
		if (fromFolderId == -1){
			alert ("Please select a folder where your From Requirements exist");
			fromFolderIdObject.focus();
			fromFolderId.style.backgroundColor="#FFCC99";
			return;
		}
		
		
		var fromRequirementsRowsPerPageObject = document.getElementById("fromRequirementsRowsPerPage");
		
		var fromRequirementsRowsPerPage = fromRequirementsRowsPerPageObject.value;
		
		
		var url="/GloreeJava2/jsp/TraceMatrix/displayFromRequirements.jsp?fromFolderId=" + fromFolderId  ;
		url += "&fromRequirementsRowsPerPage=" + fromRequirementsRowsPerPage;
		url += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById("fromRequirementsDiv").style.display = "block";
		document.getElementById("fromRequirementsDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "fromRequirementsDiv");
		
	}


	function fillTraceMatrixToRequirements(){
		
		// lets get the toFolder id.
		var toFolderIdObject = document.getElementById("toFolderId");
		var toFolderId = toFolderIdObject.options[toFolderIdObject.selectedIndex].value;
		if (toFolderId == -1){
			alert ("Please select a folder where your To Requirements exist");
			toFolderIdObject.focus();
			toFolderId.style.backgroundColor="#FFCC99";
			return;
		}
		
		var toRequirementsRowsPerPageObject = document.getElementById("toRequirementsRowsPerPage");
		var toRequirementsRowsPerPage = toRequirementsRowsPerPageObject.value;
		
		var url="/GloreeJava2/jsp/TraceMatrix/displayToRequirements.jsp?toFolderId=" + toFolderId  ;
		url += "&toRequirementsRowsPerPage=" + toRequirementsRowsPerPage;
		url += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById("toRequirementsDiv").style.display = "block";
		document.getElementById("toRequirementsDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "toRequirementsDiv");
		
	}	
	

	function displayRequirementCIA(requirementId, divId){
		
		
		var url="/GloreeJava2/jsp/TraceMatrix/displayCIAInTraceMatrix.jsp?";
		url += "&divId=" + encodeURIComponent(divId);
		url += "&bustcache=" + new Date().getTime() ;
		url += "&requirementId=" + requirementId ;
		fillOPCenterGeneric(url, divId);	
		
		document.getElementById(divId).style.display = "block";
		document.getElementById(divId).innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById(divId).innerHTML=xmlHttpOPCenterB.responseText;				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}	
	
	
	function createTraceMatrixTraces(requirementId){
		
		
		// lets disable the createTraces button to prevent accidental double clicking.
		document.getElementById("CreateTraces" + requirementId).disabled = true;

		var traceTo = "";
		var traceFrom = "";
		
		var thisForm = document.getElementById("displayFromRequirementsForm");
		
		// if only one req is displayed in data grid, the checkbox stops behaving like an array
		// and behaves like a single element. in this scenario checkbox.length craps out.
		// to catch this scenario we have to do this...
		if (thisForm.fromRequirementId.length == null){
			// only one requirement row in data grid.
			if (thisForm.fromRequirementId.checked == true ){
				
				// the checkbox has the value req full tag : req id. so we need split and take the first one.
				var temp = thisForm.fromRequirementId.value.split(':');
				var reqFullTag =temp[0];
				var fromRequirementId = temp[1];
				
				traceFrom += reqFullTag;

				// lets set the fromRequirementTraceToDiv to working...
				document.getElementById("fromRequirementTraceToDiv" + fromRequirementId).style.display = "block";
				document.getElementById("fromRequirementTraceToDiv" + fromRequirementId).innerHTML= "&nbsp;&nbsp;&nbsp;Working...";

			}
		}
		else {
			// multiple requirement rows in data grid.
			for (i=0; i<thisForm.fromRequirementId.length;i++){
				if (thisForm.fromRequirementId[i].checked == true ){
					// the checkbox has the value req full tag : req id. so we need split and take the first one.
					var temp = thisForm.fromRequirementId[i].value.split(':');
					var reqFullTag =temp[0];
					var fromRequirementId = temp[1];
					traceFrom += reqFullTag + ",";
					
					// lets set the fromRequirementTraceToDiv to working...
					document.getElementById("fromRequirementTraceToDiv" + fromRequirementId).style.display = "block";
					document.getElementById("fromRequirementTraceToDiv" + fromRequirementId).innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
				}
			}			
		}

	
		var url="/GloreeJava2/jsp/TraceMatrix/createTracesInTraceMatrix.jsp?requirementId=" + requirementId + "&createTraceTo=" + traceTo  ;
		url = url + "&createTraceFrom=" + traceFrom;
		url += "&bustcache=" + new Date().getTime() ;
		
		// lets set the To and From Requirement Div's to display and working...
		document.getElementById("toRequirementTraceFromDiv" + requirementId).style.display = "block";
		document.getElementById("toRequirementTraceFromDiv" + requirementId).innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		// Get the Ajax response from RequirementAction 'Traceto' , 'TraceFrom'
		// and populate the ContentCenterC
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// lets print the status of create traces message.
				document.getElementById("displayCreateTracesMessageDiv" + requirementId).style.display = "block";
				document.getElementById("displayCreateTracesMessageDiv" + requirementId).innerHTML =xmlHttpOPCenterB.responseText;
				
				// lets enable the create traces button
				document.getElementById("CreateTraces" + requirementId).disabled = false;

				// lets refresh the To Requirement Trace From section
				url="/GloreeJava2/jsp/TraceMatrix/displayToRequirementTraceFrom.jsp?requirementId=" + requirementId  ;
				url += "&bustcache=" + new Date().getTime() ;
				fillOPCenterGeneric(url, "toRequirementTraceFromDiv" + requirementId);
				
				// lets refresh the From Requirements
				// lets refresh the To Requirement
				
				if (thisForm.fromRequirementId.length == null){
					// only one requirement row in data grid.
					if (thisForm.fromRequirementId.checked == true ){
						
						
						// the checkbox has the value req full tag : req id. so we need split and take the first one.
						var temp = thisForm.fromRequirementId.value.split(':');
						var reqFullTag =temp[0];
						var fromRequirementId = temp[1];
						url="/GloreeJava2/jsp/TraceMatrix/displayFromRequirementTraceTo.jsp?requirementId=" + fromRequirementId  ;
						url += "&bustcache=" + new Date().getTime() ;
						fillOPCenterGeneric(url, "fromRequirementTraceToDiv" + fromRequirementId);
					}
				}
				else {
					// multiple requirement rows in data grid.
					for (i=0; i<thisForm.fromRequirementId.length;i++){
						if (thisForm.fromRequirementId[i].checked == true ){
							// the checkbox has the value req full tag : req id. so we need split and take the first one.
							var temp = thisForm.fromRequirementId[i].value.split(':');
							var reqFullTag =temp[0];
							var fromRequirementId = temp[1];

							
							url="/GloreeJava2/jsp/TraceMatrix/displayFromRequirementTraceTo.jsp?requirementId=" + fromRequirementId  ;
							url += "&bustcache=" + new Date().getTime() ;
							fillOPCenterGeneric(url, "fromRequirementTraceToDiv" + fromRequirementId);
						}
					}			
				}
				
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
	

	
	// this function is used to select all the checkboxes in the TraceMatrix From Requirements section
	function selectAllRequirementInTraceMatrix() {
		var thisForm = document.getElementById("displayFromRequirementsForm");
		
		// if only one req is displayed in data grid, the checkbox stops behaving like an array
		// and behaves like a single element. in this scenario checkbox.length craps out.
		// to catch this scenario we have to do this...
		if (thisForm.fromRequirementId.length == null){
			// only one requirement row in data grid.
			thisForm.fromRequirementId.checked = true;
		}
		else {
			// multiple requirement rows in data grid.
			for (i=0; i<thisForm.fromRequirementId.length;i++){
				thisForm.fromRequirementId[i].checked = true;
			}			
		}
	}

	// this function is used to DEselect all the checkboxes in the TraceMatrix From Requirements section
	function deSelectAllRequirementInTraceMatrix() {
		var thisForm = document.getElementById("displayFromRequirementsForm");
		// if only one req is displayed in data grid, the checkbox stops behaving like an array
		// and behaves like a single element. in this scenario checkbox.length craps out.
		// to catch this scenario we have to do this...
		if (thisForm.fromRequirementId.length == null){
			// only one requirement row in data grid.
			thisForm.fromRequirementId.checked = false;
		}
		else {
			// multiple requirement rows in data grid.
			for (i=0; i<thisForm.fromRequirementId.length;i++){
				thisForm.fromRequirementId[i].checked = false;
			}			
		}		
	}
	
	
	
	// called when someone chooses to take some action on a trace.
	function deleteTraceInTraceMatrix(traceId, requirementId, toRequirementId){
		
		var url="/GloreeJava2/servlet/TraceAction?action=deleteTraceInTraceMatrix" ;
		url = url + "&traceId=" + traceId;
		url = url + "&requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;

		document.getElementById("fromRequirementTraceToDiv" + requirementId).style.display = "block";
		document.getElementById("fromRequirementTraceToDiv" + requirementId).innerHTML = "&nbsp;&nbsp;&nbsp;Working...";

		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("fromRequirementTraceToDiv" + requirementId).innerHTML  = xmlHttpOPCenterB.responseText;	
				// once we delete a Trace, if the req this trace is going to is present in the 
				// ToRequiremnts secion, then lets refresh the 'ToRequirementTraceFromDiv'.
				var toRequirementTraceFromDiv = document.getElementById("toRequirementTraceFromDiv" + toRequirementId);
				if (toRequirementTraceFromDiv != null){
					toRequirementTraceFromDiv.style.display = "block";
					toRequirementTraceFromDiv.innerHTML = "&nbsp;&nbsp;&nbsp;Working...";
					url="/GloreeJava2/jsp/TraceMatrix/displayToRequirementTraceFrom.jsp?requirementId=" + toRequirementId  ;
					url += "&bustcache=" + new Date().getTime() ;
					fillOPCenterGeneric(url, "toRequirementTraceFromDiv" + toRequirementId);
				}
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
	
	function displayWizard(){
		
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none"; 
		
		document.getElementById("contentCenterComments").style.display = "none"; 
		document.getElementById("contentCenterAttachments").style.display = "none"; 
		document.getElementById("reqTabs").style.display = "none";
		
		
		
		
		// Now fill contentCenterB with the folder details.
		document.getElementById("contentCenterF").style.display = "block";
		document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/Wizard/displayWizard.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterF" ).innerHTML  = xmlHttpOPCenterB.responseText;	
				
				// lets populate the myTasksDashboard
				// we don't ned to populateMyTasks, as they get populated when users click on show my status tab
				//displayMyTasksForAProjectInWizard();
				
				// lets populate personalized wizard
				loadPersonalizedStatus();
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
	
	//////////////////////////////////////////////////////////////////////////
	// //
	// Agile Scrum //
	// //
	// ////////////////////////////////////////////////////////////////////////

	// called when someone requests to open the Agile scrum work flow
	function displayAgileScrumHome(){
		
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		
		// Now fill contentCenterB with the folder details.
		document.getElementById("contentCenterF").style.display = "block";
		document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/AgileScrum/displayAgileScrumHome.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "contentCenterF");
	}
	
	// this is used to display the Agile sprint screen with focus on 'Crete New Sprint'
	function displayCreateAgileSprint(){
		
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		
		// Now fill contentCenterB with the folder details.
		
		var url="/GloreeJava2/jsp/AgileScrum/displayCreateAgileSprint.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "agileSprintDiv");
	}

	
	// this is used to display the selected Agile sprint screen 
	function displaySelectedAgileSprint(projectId){
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		
		var sprintIdObject = document.getElementById("sprintId");
		var sprintId = sprintIdObject.options[sprintIdObject.selectedIndex].value;
		
		if (sprintId == '-1'){
			// the user did not select a sprint.
			return;
		}
		 
		displayAgileDailyScrum(projectId, sprintId);
	}

	// this is used to display the selected Agile sprint's daily scrum screen
	function displayAgileSprintInfo(projectId, sprintId){
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		
		 
		document.getElementById("agileSprintDiv").style.display='block';
		document.getElementById("agileSprintDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
	
		// Now fill contentCenterB with the folder details.
		
		var url="/GloreeJava2/jsp/AgileScrum/SprintInfo/displayAgileSprintInfo.jsp?sprintId=" + sprintId + "&projectId=" + projectId ;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "agileSprintDiv");
	}


	// this is used to display the selected Agile sprint's backlog
	function displayAgileBacklog(projectId, sprintId){
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		
		 
		document.getElementById("agileSprintDiv").style.display='block';
		document.getElementById("agileSprintDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
	
		// Now fill contentCenterB with the folder details.
		
		var url="/GloreeJava2/jsp/AgileScrum/Backlog/displayAgileBacklog.jsp?sprintId=" + sprintId + "&projectId=" + projectId ;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "agileSprintDiv");
		
	}
	

	function displayFoldersInAgileBacklog(projectId, sprintId){
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		
		 
		document.getElementById("foldersInAgileBacklogDiv").style.display='block';
		document.getElementById("foldersInAgileBacklogDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
	
		var backlogRequirementTypeObject = document.getElementById("backlogRequirementType");
		var requirementTypeId = backlogRequirementTypeObject.options[backlogRequirementTypeObject.selectedIndex].value;
		if (requirementTypeId == -1){
			return;
		}
		
		var url="/GloreeJava2/jsp/AgileScrum/Backlog/displayAgileFoldersBacklog.jsp?sprintId=" + sprintId + "&projectId=" + projectId ;
		url += "&requirementTypeId=" + requirementTypeId ;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "foldersInAgileBacklogDiv");
		
	}

	function displayRequirementsInAgileBacklog(projectId, sprintId){
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		
		 
		document.getElementById("requirementsInBacklogDiv").style.display='block';
		document.getElementById("requirementsInBacklogDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
	
		var backlogFolderIdObject = document.getElementById("backlogFolderId");
		var folderId = backlogFolderIdObject.options[backlogFolderIdObject.selectedIndex].value;
		if (folderId == -1){
			return;
		}
		
		var url="/GloreeJava2/jsp/AgileScrum/Backlog/displayAgileRequirementsBacklog.jsp?sprintId=" + sprintId + "&projectId=" + projectId ;
		url += "&folderId=" + folderId ;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "requirementsInBacklogDiv");
		
	}
	
		
	
	
	function  handleRequirementBacklogSearchkeyPress(event, searchType, sprintId) {
		var keyCode = event.keyCode;
		if (keyCode == 13) {
			RequirementBacklogSearch(searchType, sprintId);
		}
	}
	// Called when someone tries to search within the Requirement Search box.
	function RequirementBacklogSearch(searchType, sprintId){
		var searchString ;
		
		if (searchType == 'reqId') {
			searchString = document.getElementById("reqIdSearchString").value;
		}
		else {
			searchString = document.getElementById("googleSearchString").value;
		}
		
		
		document.getElementById("searchFormResultsDiv").style.display = "block";
		document.getElementById("searchFormResultsDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		 
		// Now display the search results.
		url = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayBacklogRequirementSearchResults.jsp?searchType=";
		url += searchType +  "&searchString=" + searchString;
		url +=   "&sprintId=" + sprintId;
		
		contentArea = 'searchFormResultsDiv';
		fillOPCenterGeneric(url,contentArea);

	}


	
	

	function addRequirementBacklogToSprint(projectId, folderId, sprintId, requirementId, displayRDInReportDiv,displayBacklogRequirementDiv){
		
		var url="/GloreeJava2/servlet/AgileScrumAction";
		
		var params; 
		params = "action=addRequirementBacklogToSprint&";
		params = params + "projectId=" + projectId + "&";
		params = params + "folderId=" + folderId + "&";
		params = params + "sprintId=" + sprintId + "&";
		params = params + "requirementId=" + requirementId;
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;

		document.getElementById(displayBacklogRequirementDiv).style.display='block';
		document.getElementById(displayBacklogRequirementDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
	

		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// once we get a response back, we can remove this row.
				var displayBacklogRequirementDivObject = document.getElementById(displayBacklogRequirementDiv);
				if(displayBacklogRequirementDivObject && displayBacklogRequirementDivObject.parentNode)displayBacklogRequirementDivObject.parentNode.removeChild(displayBacklogRequirementDivObject);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
	}



	function addRequirementToSprint(projectId, folderId, sprintId, requirementId){
		
		var url="/GloreeJava2/servlet/AgileScrumAction";
		
		var params; 
		params = "action=addRequirementBacklogToSprint&";
		params = params + "projectId=" + projectId + "&";
		params = params + "folderId=" + folderId + "&";
		params = params + "sprintId=" + sprintId + "&";
		params = params + "requirementId=" + requirementId;
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;

		
		document.getElementById("addToSprintButtonDiv"  + requirementId).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// once we get a response back, we can remove this row.
				// do nothing
				document.getElementById("addToSprintButtonDiv"  + requirementId).innerHTML= "<span class='normalText'><font color='red'>Done.Please Refresh Scrum Board</font></span>";
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
	}

	// This is same as addRequirementBacklogToSprint, except that it tries to refresh the parent window Sprint Details.
	function addRequirementBacklogToSprintInParentWindow(projectId, folderId, sprintId, requirementId, displayRDInReportDiv,displayBacklogRequirementDiv){
		
		var url="/GloreeJava2/servlet/AgileScrumAction";
		
		var params; 
		params = "action=addRequirementBacklogToSprint&";
		params = params + "projectId=" + projectId + "&";
		params = params + "folderId=" + folderId + "&";
		params = params + "sprintId=" + sprintId + "&";
		params = params + "requirementId=" + requirementId;
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;

		document.getElementById(displayBacklogRequirementDiv).style.display='block';
		document.getElementById(displayBacklogRequirementDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
	

		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// once we get a response back, we can remove this row.
				var displayBacklogRequirementDivObject = document.getElementById(displayBacklogRequirementDiv);
				if(displayBacklogRequirementDivObject && displayBacklogRequirementDivObject.parentNode)displayBacklogRequirementDivObject.parentNode.removeChild(displayBacklogRequirementDivObject);
				
				// since this whole things is a search window, lets try to refresh the parent window in the background.
				// Since we are referring to objects in the parent window, ITS CRITICAL THAT WE USE OPENER.DOCUMENT...
				var showOnlyTasksOwnedByObject = opener.document.getElementById("showOnlyTasksOwnedBy");
				var showOnlyTasksOwnedBy = showOnlyTasksOwnedByObject.options[showOnlyTasksOwnedByObject.selectedIndex].value;
			
				
				
				var notStartedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
					"&taskStatus=notStarted"+ "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy)  +"&bustcache=" + new Date().getTime() ;
				opener.document.getElementById("notStartedTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
				fillOPCenterGenericInParentWindow(notStartedURL, "notStartedTasksDiv");
				
				var inProgressURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
					"&taskStatus=inProgress" + "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy)  +"&bustcache=" + new Date().getTime() ;
				opener.document.getElementById("inProgressTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
				fillOPCenterGenericInParentWindow(inProgressURL, "inProgressTasksDiv");
				
				var blockedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
					"&taskStatus=blocked"+ "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy)  +"&bustcache=" + new Date().getTime() ;
				opener.document.getElementById("blockedTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
				fillOPCenterGenericInParentWindow(blockedURL, "blockedTasksDiv");
						
				var completedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
					"&taskStatus=completed" + "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy) + "&bustcache=" + new Date().getTime() ;
				opener.document.getElementById("completedTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
				fillOPCenterGenericInParentWindow(completedURL, "completedTasksDiv");
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
	}
	
	// this is used to display the selected Agile sprint's daily scrum screen
	function displayAgileDailyScrum(projectId, sprintId){
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		
		 
		document.getElementById("agileSprintDiv").style.display='block';
		document.getElementById("agileSprintDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
	
		// Now fill contentCenterB with the folder details.
		
		var url="/GloreeJava2/jsp/AgileScrum/DailyScrum/displayAgileDailyScrum.jsp?sprintId=" + sprintId + "&projectId=" + projectId ;
		url += "&bustcache=" + new Date().getTime() ;
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				
				document.getElementById('agileSprintDiv').innerHTML=xmlHttpOPCenterB.responseText;
				// after displaying the AgileSprintDiv secion,it will expose 4 different boxes that we will need to fill out
				// one each for not started, in progress, blocked and completed tasks.
				var showOnlyTasksOwnedByObject = document.getElementById("showOnlyTasksOwnedBy");
				var showOnlyTasksOwnedBy = showOnlyTasksOwnedByObject.options[showOnlyTasksOwnedByObject.selectedIndex].value;
			
				
				try {
					var storiesURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayStoriesInSprint.jsp?sprintId=" + sprintId +
						"&bustcache=" + new Date().getTime() ;
					document.getElementById("storiesDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(storiesURL, "storiesDiv");
				}
				catch (e) {
					// do nothing
				}
				
				
				
				
				var notStartedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
					"&taskStatus=notStarted"+ "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy)  +"&bustcache=" + new Date().getTime() ;
				document.getElementById("notStartedTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
				fillOPCenterGeneric(notStartedURL, "notStartedTasksDiv");
				
				var inProgressURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
					"&taskStatus=inProgress" + "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy)  +"&bustcache=" + new Date().getTime() ;
				document.getElementById("inProgressTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
				fillOPCenterGeneric(inProgressURL, "inProgressTasksDiv");
				
				var blockedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
					"&taskStatus=blocked"+ "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy)  +"&bustcache=" + new Date().getTime() ;
				document.getElementById("blockedTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
				fillOPCenterGeneric(blockedURL, "blockedTasksDiv");
						
				var completedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
					"&taskStatus=completed" + "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy) + "&bustcache=" + new Date().getTime() ;
				document.getElementById("completedTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
				fillOPCenterGeneric(completedURL, "completedTasksDiv");
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);	
	}
	
	// this is used to display the selected scrums' tasks, narrowed by the selected user.
	function displayNarrowedAgileDailyScrum(projectId, sprintId){

		var showOnlyTasksOwnedByObject = document.getElementById("showOnlyTasksOwnedBy");
		var showOnlyTasksOwnedBy = showOnlyTasksOwnedByObject.options[showOnlyTasksOwnedByObject.selectedIndex].value;
	
		if (showOnlyTasksOwnedBy == -1){
			return;
		}
		
		var notStartedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
			"&taskStatus=notStarted"+ "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy)  +"&bustcache=" + new Date().getTime() ;
		document.getElementById("notStartedTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(notStartedURL, "notStartedTasksDiv");
		
		var inProgressURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
			"&taskStatus=inProgress" + "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy)  +"&bustcache=" + new Date().getTime() ;
		document.getElementById("inProgressTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(inProgressURL, "inProgressTasksDiv");
		
		var blockedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
			"&taskStatus=blocked"+ "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy)  +"&bustcache=" + new Date().getTime() ;
		document.getElementById("blockedTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(blockedURL, "blockedTasksDiv");
				
		var completedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
			"&taskStatus=completed" + "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy) + "&bustcache=" + new Date().getTime() ;
		document.getElementById("completedTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(completedURL, "completedTasksDiv");
			
	}
	// this is used to display the selected Agile sprint's scrum notes
	function displayAgileScrumNotes(projectId, sprintId){
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		
		 
		document.getElementById("agileSprintDiv").style.display='block';
		document.getElementById("agileSprintDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
	
		// Now fill contentCenterB with the folder details.
		
		var url="/GloreeJava2/jsp/AgileScrum/ScrumNotes/displayAgileScrumNotes.jsp?sprintId=" + sprintId + "&projectId=" + projectId ;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				
				document.getElementById('agileSprintDiv').innerHTML=xmlHttpOPCenterB.responseText;
				// now that we have the scumnoteshistory box, lets fill the log
				// After that , lets refresh the scrum notes log display
				
				document.getElementById("scrumNotesHistoryDiv").style.display='block';
				document.getElementById("scrumNotesHistoryDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
				
				var url2="/GloreeJava2/jsp/AgileScrum/ScrumNotes/displayAgileScrumNotesHistory.jsp?sprintId=" + sprintId ;
				url2 += "&bustcache=" + new Date().getTime() ;
				fillOPCenterGeneric(url2, "scrumNotesHistoryDiv");
				
				
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
		
		
	}
	
	
	// this is used to display the selected Agile sprint's scrum notes
	function displayAgileScrumDashboard(projectId, sprintId){
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		 
		document.getElementById("agileSprintDiv").style.display='block';
		document.getElementById("agileSprintDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
	
		// Now fill contentCenterB with the folder details.
		
		var url="/GloreeJava2/jsp/AgileScrum/Dashboard/displayAgileScrumDashboard.jsp?sprintId=" + sprintId + "&projectId=" + projectId ;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				
				document.getElementById('agileSprintDiv').innerHTML=xmlHttpOPCenterB.responseText;
				
				// now that the dashboard page has been printed, lets populate the data table and metrics
				document.getElementById("agileScrumDataTableDiv").style.display = "block";
				document.getElementById("agileScrumDataTableDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
				url = "/GloreeJava2/jsp/Report/AgileScrumDashboard/displayAgileScrumMetricsDataTable.jsp?sprintId=" + sprintId;
				fillOPCenterGeneric(url, "agileScrumDataTableDiv");
				
				url = "/GloreeJava2/jsp/Report/AgileScrumDashboard/displayAgileScrumMetrics.jsp?sprintId=" + sprintId;
				document.getElementById("agileScrumMetricsDiv").style.display = "block";
				document.getElementById("agileScrumMetricsDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
				document.getElementById("agileScrumMetricsDiv").innerHTML= 
					"<iframe src='"+ url +"' width='800', height='400' ></iframe>";
				
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);	
		
		
			
			
	}
	
	
	
	// called by the displayCreateAgileSprint.jsp to create a new sprint
	function createAgileSprint(){	
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		var cRF = document.getElementById("createNewSprintForm");
		
		var projectId = cRF.projectId;
		var sprintName = cRF.sprintName;
		var sprintDescription = cRF.sprintDescription;
		var scrumMaster = cRF.scrumMaster;
		
		var sprintStartDt = cRF.sprintStartDt;
		var sprintEndDt = cRF.sprintEndDt;
		

		
		if (sprintName.value.length == 0) {
			alert ("Please enter a Sprint Name");
			sprintName.focus();
			sprintName.style.backgroundColor="#FFCC99";
			return;
			}
		if (sprintDescription.value.length == 0) {
			alert ("Please enter a Sprint Description");
			sprintDescription.focus();
			sprintDescription.style.backgroundColor="#FFCC99";
			return;
		}
		if (scrumMaster.value.length == 0) {
			alert ("Please enter the name of the Scrum Master");
			scrumMaster.focus();
			scrumMaster.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (sprintStartDt != null) {
			if (isValidDate(sprintStartDt.value)==false){
				sprintStartDt.focus();
				sprintStartDt.style.backgroundColor="#FFCC99";
				return(0);
			}
		}
		
		
		if (sprintEndDt != null) {
			if (isValidDate(sprintEndDt.value)==false){
				sprintEndDt.focus();
				sprintEndDt.style.backgroundColor="#FFCC99";
				return(0);
			}
		}
		// lets make sure that the sprints are at most 60 days long.
		var sprintStartDtValue = sprintStartDt.value;
		var pos1=sprintStartDtValue.indexOf(dtCh)
		var pos2=sprintStartDtValue.indexOf(dtCh,pos1+1)
		var strMonth=sprintStartDtValue.substring(0,pos1)
		var strDay=sprintStartDtValue.substring(pos1+1,pos2)
		var strYear=sprintStartDtValue.substring(pos2+1)
		
		var sprintStartDtObject = new Date (strYear, strMonth, strDay);
		
		var sprintEndDtValue = sprintEndDt.value;
		var pos1=sprintEndDtValue.indexOf(dtCh)
		var pos2=sprintEndDtValue.indexOf(dtCh,pos1+1)
		var strMonth=sprintEndDtValue.substring(0,pos1)
		var strDay=sprintEndDtValue.substring(pos1+1,pos2)
		var strYear=sprintEndDtValue.substring(pos2+1)
		
		var sprintEndDtObject = new Date (strYear, strMonth, strDay);
		
		var oneDay = 1000 * 60 * 60 * 24;
		var dateDiff = (sprintEndDtObject.getTime() - sprintStartDtObject.getTime()) / oneDay;
		Math.round(dateDiff);
		
		if (dateDiff > 360){
			alert ("Your current sprint is " + dateDiff + " days long long. Please ensure that the sprint is shorter than 360 days.");
			sprintEndDt.focus();
			sprintEndDt.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		var url="/GloreeJava2/servlet/AgileScrumAction";
		
		var params; 
		params = "action=createAgileSprint&";
		params = params + "projectId=" + projectId.value + "&";
		
		
		
		params = params + "sprintName=" + encodeURIComponent(sprintName.value) + "&";
		params = params + "sprintDescription=" + encodeURIComponent(sprintDescription.value) + "&";
		params = params + "scrumMaster=" + encodeURIComponent(scrumMaster.value) + "&" ;
		params = params + "sprintStartDt=" + encodeURIComponent(sprintStartDt.value) + "&";
		params = params + "sprintEndDt=" + encodeURIComponent(sprintEndDt.value) ;
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;

		
		document.getElementById("createAgileSprintMessage").style.display='block';
			
		document.getElementById("createAgileSprintMessage").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		document.getElementById("createSprintButton").disabled=true;
	
		// since we created a new sprint, we should display the AgileScrumHome page . This has a drop down of all sprints and need to be refreshed.
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterF').style.display = 'block'; 
				document.getElementById('contentCenterF').innerHTML=xmlHttpOPCenterB.responseText;
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
	
	

	// called by the displayCreateAgileSprint.jsp to create a new sprint
	function updateAgileSprint(){	
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		var cRF = document.getElementById("updateSprintForm");
		
		var projectId = cRF.projectId;
		var sprintId = cRF.sprintId;
		
		var sprintName = cRF.sprintName;
		var sprintDescription = cRF.sprintDescription;
		var scrumMaster = cRF.scrumMaster;
		
		var sprintStartDt = cRF.sprintStartDt;
		var sprintEndDt = cRF.sprintEndDt;
		

		
		if (sprintName.value.length == 0) {
			alert ("Please enter a Sprint Name");
			sprintName.focus();
			sprintName.style.backgroundColor="#FFCC99";
			return;
			}
		if (sprintDescription.value.length == 0) {
			alert ("Please enter a Sprint Description");
			sprintDescription.focus();
			sprintDescription.style.backgroundColor="#FFCC99";
			return;
		}
		if (scrumMaster.value.length == 0) {
			alert ("Please enter the name of the Scrum Master");
			scrumMaster.focus();
			scrumMaster.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (sprintStartDt != null) {
			if (isValidDate(sprintStartDt.value)==false){
				sprintStartDt.focus();
				sprintStartDt.style.backgroundColor="#FFCC99";
				return(0);
			}
		}

		if (sprintEndDt != null) {
			if (isValidDate(sprintEndDt.value)==false){
				sprintEndDt.focus();
				sprintEndDt.style.backgroundColor="#FFCC99";
				return(0);
			}
		}
		// lets make sure that the sprints are at most 60 days long.
		var sprintStartDtValue = sprintStartDt.value;
		var pos1=sprintStartDtValue.indexOf(dtCh)
		var pos2=sprintStartDtValue.indexOf(dtCh,pos1+1)
		var strMonth=sprintStartDtValue.substring(0,pos1)
		var strDay=sprintStartDtValue.substring(pos1+1,pos2)
		var strYear=sprintStartDtValue.substring(pos2+1)
		
		var sprintStartDtObject = new Date (strYear, strMonth, strDay);
		
		var sprintEndDtValue = sprintEndDt.value;
		var pos1=sprintEndDtValue.indexOf(dtCh)
		var pos2=sprintEndDtValue.indexOf(dtCh,pos1+1)
		var strMonth=sprintEndDtValue.substring(0,pos1)
		var strDay=sprintEndDtValue.substring(pos1+1,pos2)
		var strYear=sprintEndDtValue.substring(pos2+1)
		
		var sprintEndDtObject = new Date (strYear, strMonth, strDay);
		
		var oneDay = 1000 * 60 * 60 * 24;
		var dateDiff = (sprintEndDtObject.getTime() - sprintStartDtObject.getTime()) / oneDay
		dateDiff = Math.round(dateDiff);
		
		if (dateDiff > 360){
			alert ("Your current sprint is " + dateDiff + " days long. Please ensure that the sprint is shorter than 360 days.");
			sprintEndDt.focus();
			sprintEndDt.style.backgroundColor="#FFCC99";
			return(0);
		}
				
		var url="/GloreeJava2/servlet/AgileScrumAction";
		
		var params; 
		params = "action=updateAgileSprint&";
		params = params + "projectId=" + projectId.value + "&";
		params = params + "sprintId=" + sprintId.value + "&";
		
		
		
		params = params + "sprintName=" + encodeURIComponent(sprintName.value) + "&";
		params = params + "sprintDescription=" + encodeURIComponent(sprintDescription.value) + "&";
		params = params + "scrumMaster=" + encodeURIComponent(scrumMaster.value) + "&" ;
		params = params + "sprintStartDt=" + encodeURIComponent(sprintStartDt.value) + "&";
		params = params + "sprintEndDt=" + encodeURIComponent(sprintEndDt.value) ;
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;

		
		document.getElementById("updateAgileSprintMessage").style.display='block';
			
		document.getElementById("updateAgileSprintMessage").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		document.getElementById("updateAgileSprintButton").disabled=true;
		
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('updateAgileSprintMessage').style.display = 'block'; 
				document.getElementById('updateAgileSprintMessage').innerHTML=xmlHttpOPCenterB.responseText;
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}

	function deleteAgileSprint(){	
		// set the other content centers to empty.
		
		var cRF = document.getElementById("updateSprintForm");
		
		var sprintId = cRF.sprintId;
		
		var url="/GloreeJava2/servlet/AgileScrumAction";
		
		var params; 
		params = "action=deleteAgileSprint&";
		params = params + "sprintId=" + sprintId.value ;
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;

		
		document.getElementById("updateAgileSprintMessage").style.display='block';
			
		document.getElementById("updateAgileSprintMessage").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		document.getElementById("confirmDeleteAgileSprintButton").disabled=true;
		
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('agileSprintDiv').style.display = 'none'; 
				
				// lets refresh the agile home drop down with the deleted sprint removed.
				var url="/GloreeJava2/jsp/AgileScrum/displayAgileScrumHome.jsp?";
				url += "&bustcache=" + new Date().getTime() ;
				
				fillOPCenterGeneric(url, "contentCenterF");
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}

	function  addRequirementsToSprintKeyPress(event, projectId, sprintId) {
		var keyCode = event.keyCode;
		if (keyCode == 13) {
			addRequirementsToSprint(projectId, sprintId);
		}
	}
	// this is used to add tasks / requirements to Sprint
	function addRequirementsToSprint(projectId, sprintId){
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		
		
		requirementsToAddObject =  document.getElementById("requirementsToAdd");
		
		if (requirementsToAddObject.value.length == 0) {
			alert ("Please enter a comma seperated list of requirement ids (BR-1,BR-2 etc...) to add to this sprint");
			requirementsToAddObject.focus();
			requirementsToAddObject.style.backgroundColor="#FFCC99";
			return;
			}
		
		var url="/GloreeJava2/servlet/AgileScrumAction";
		
		var params; 
		params = "action=addRequirementsToSprint&";
		params = params + "projectId=" + projectId + "&";
		params = params + "sprintId=" + sprintId + "&";
		
		
		
		params = params + "requirementsToAdd=" + encodeURIComponent(requirementsToAddObject.value);
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;

		document.getElementById('addRequirementsToSprintMessageDiv').style.display = 'block'; 
		document.getElementById("addRequirementsToSprintMessageDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('addRequirementsToSprintMessageDiv').style.display = 'block'; 
				document.getElementById('addRequirementsToSprintMessageDiv').innerHTML=xmlHttpOPCenterB.responseText;
				
				var showOnlyTasksOwnedByObject = document.getElementById("showOnlyTasksOwnedBy");
				var showOnlyTasksOwnedBy = showOnlyTasksOwnedByObject.options[showOnlyTasksOwnedByObject.selectedIndex].value;

				
				var notStartedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
					"&taskStatus=notStarted&bustcache=" + new Date().getTime() ;
				document.getElementById("notStartedTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
				fillOPCenterGeneric(notStartedURL, "notStartedTasksDiv");
				
				var inProgressURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
					"&taskStatus=inProgress&bustcache=" + new Date().getTime() ;
				document.getElementById("inProgressTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
				fillOPCenterGeneric(inProgressURL, "inProgressTasksDiv");
				
				var blockedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
					"&taskStatus=blocked&bustcache=" + new Date().getTime() ;
				document.getElementById("blockedTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
				fillOPCenterGeneric(blockedURL, "blockedTasksDiv");
						
				var completedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
					"&taskStatus=completed&bustcache=" + new Date().getTime() ;
				document.getElementById("completedTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
				fillOPCenterGeneric(completedURL, "completedTasksDiv");
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
		
	}
	
	
	// this is used to move tasks / requirements from one Sprint to another
	function moveRequirementsToSprint(projectId, sprintId){
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		var typeOfTasksObject = document.getElementById("typeOfTasks");
		var typeOfTasks = typeOfTasksObject.options[typeOfTasksObject.selectedIndex].value;
		
		if (typeOfTasks == "-1") {
			alert ("Please select a category of Tasks to move");
			typeOfTasksObject.focus();
			typeOfTasksObject.style.backgroundColor="#FFCC99";
			return;
		}
		
		var targetSprintIdObject = document.getElementById("targetSprintId");
		var targetSprintId = targetSprintIdObject.options[targetSprintIdObject.selectedIndex].value;
		
		if (targetSprintId == "-1") {
			alert ("Please select a Target Sprint to move tasks to");
			targetSprintIdObject.focus();
			targetSprintIdObject.style.backgroundColor="#FFCC99";
			return;
		}
		
		
		var url="/GloreeJava2/servlet/AgileScrumAction";
		
		var params; 
		params = "action=moveRequirementsToSprint&";
		params = params + "projectId=" + projectId + "&";
		params = params + "sprintId=" + sprintId + "&";
		params = params + "typeOfTasks=" + encodeURIComponent(typeOfTasks) +  "&";
		params = params + "targetSprintId=" + targetSprintId ;
		
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;

		document.getElementById('moveRequirementsToSprintMessageDiv').style.display = 'block'; 
		document.getElementById("moveRequirementsToSprintMessageDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('moveRequirementsToSprintMessageDiv').style.display = 'block'; 
				document.getElementById('moveRequirementsToSprintMessageDiv').innerHTML=xmlHttpOPCenterB.responseText;
				
				var showOnlyTasksOwnedByObject = document.getElementById("showOnlyTasksOwnedBy");
				var showOnlyTasksOwnedBy = showOnlyTasksOwnedByObject.options[showOnlyTasksOwnedByObject.selectedIndex].value;

				
				if (typeOfTasks == "Not Started"){
					// lets refresh the 'Not Started' column of tasks
					var notStartedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
						"&taskStatus=notStarted&bustcache=" + new Date().getTime() ;
					document.getElementById("notStartedTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(notStartedURL, "notStartedTasksDiv");
				}
				
				if (typeOfTasks == "In Progress"){
					// lets refresh the 'Not Started' column of tasks				
					var inProgressURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
						"&taskStatus=inProgress&bustcache=" + new Date().getTime() ;
					document.getElementById("inProgressTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(inProgressURL, "inProgressTasksDiv");
				}
				
				if (typeOfTasks == "Blocked"){
					// lets refresh the 'Not Started' column of tasks
					var blockedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
						"&taskStatus=blocked&bustcache=" + new Date().getTime() ;
					document.getElementById("blockedTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(blockedURL, "blockedTasksDiv");
				}
				
				if (typeOfTasks == "Completed"){
					// lets refresh the 'Not Started' column of tasks
				var completedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
					"&taskStatus=completed&bustcache=" + new Date().getTime() ;
					document.getElementById("completedTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(completedURL, "completedTasksDiv");
				}
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
		
	}
	
	
	// used to perform Scrum related actions in a sprint on the tasks.
	function processScrumAction(taskStatus, projectId, sprintId, requirementId, targetURL){
		
		var moveToAnotherSprintSubDivName = "moveToAnotherSprintSubDiv" + requirementId;
		var totalEffortSubDivName = "totalEffortSubDiv" + requirementId;
		var effortRemainingSubDivName = "effortRemainingSubDiv" + requirementId;
		var taskWeightSubDivName = "taskWeightSubDiv" + requirementId;
		var actionSubDivName = "actionSubDiv" + requirementId;
		var requirementDivName = "requirementDiv" + requirementId;
		var scrumActionObjectId = "scrumAction" + requirementId; 
		
		var scrumActionObject = document.getElementById(scrumActionObjectId);
		var scrumAction = scrumActionObject.options[scrumActionObject.selectedIndex].value;
		
		var showOnlyTasksOwnedByObject = document.getElementById("showOnlyTasksOwnedBy");
		var showOnlyTasksOwnedBy = showOnlyTasksOwnedByObject.options[showOnlyTasksOwnedByObject.selectedIndex].value;
	
		
		if (scrumAction == "openInNewTab"){
			window.open(targetURL);
		}	
		
		if (scrumAction == "removeRequirementFromSprint"){
			// we need to remove this req from this sprint and refresh the correct screens
			var url="/GloreeJava2/servlet/AgileScrumAction";
			
			var params; 
			params = "action=removeRequirementFromSprint&";
			params = params + "projectId=" + projectId + "&";
			params = params + "sprintId=" + sprintId + "&";
			params = params + "showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy) + "&"; 
			params = params + "requirementId=" + requirementId + "&";
			params = params + "&bustcache=" + new Date().getTime() ;

			url = url + "?" + params;
			
			document.getElementById(requirementDivName).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
			xmlHttpOPCenterB =GetXmlHttpObject();		
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// once the req has been removed we will need remove this div from the screen, as this req is no longer part
					// of this sprint.
					var requirementDiv = document.getElementById(requirementDivName);
					if(requirementDiv && requirementDiv.parentNode)requirementDiv.parentNode.removeChild(requirementDiv);
					
					
					// since removing a req from a sprint has an impact on the header
					// for this column (Total Number of Hours), we need to refresh it.
					var displaySprintHeaderURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displaySprintHeaderDiv.jsp?sprintId=" + sprintId +
						"&taskStatus="+ taskStatus + "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy) +"&bustcache=" + new Date().getTime() ;
					
					if (taskStatus == "notStarted"){
						fillOPCenterGeneric(displaySprintHeaderURL, "notStartedHeaderDiv");
					}
					if (taskStatus == "inProgress"){
						fillOPCenterGeneric(displaySprintHeaderURL, "inProgressHeaderDiv");
					}
					if (taskStatus == "blocked"){
						fillOPCenterGeneric(displaySprintHeaderURL, "blockedHeaderDiv");
					}
					if (taskStatus == "completed"){
						fillOPCenterGeneric(displaySprintHeaderURL, "completedHeaderDiv");
					}
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);
		}
		 
		if ((scrumAction == "setStatusToNotStarted")
			||
			(scrumAction == "setStatusToInProgress")
			||
			(scrumAction == "setStatusToBlocked")
			||
			(scrumAction == "setStatusToCompleted")
		){
			// we need to change the status of this req, remove it from its current location and refresh the notStarted div.
			var url="/GloreeJava2/servlet/AgileScrumAction";
			
			var params; 
			params = "action=" + scrumAction + "&";
			params = params + "projectId=" + projectId + "&";
			params = params + "sprintId=" + sprintId + "&";
			params = params + "requirementId=" + requirementId;
			params = params + "&bustcache=" + new Date().getTime() ;

			url = url + "?" + params;
			
			var requirementDiv = document.getElementById(requirementDivName);
			if(requirementDiv && requirementDiv.parentNode)requirementDiv.parentNode.removeChild(requirementDiv);
			
			xmlHttpOPCenterB =GetXmlHttpObject();		
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// once the reqs status has been adjusted, we need to refresh the apropriate div
					
					if (scrumAction == "setStatusToNotStarted"){
						var notStartedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
							"&taskStatus=notStarted" + "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy) + "&bustcache=" + new Date().getTime() ;
						document.getElementById("notStartedTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
						fillOPCenterGeneric(notStartedURL, "notStartedTasksDiv");
					}
					
					if (scrumAction == "setStatusToInProgress"){
						var inProgressURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
							"&taskStatus=inProgress" + "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy) + "&bustcache=" + new Date().getTime() ;
						fillOPCenterGeneric(inProgressURL, "inProgressTasksDiv");
					}
					
					if (scrumAction == "setStatusToBlocked"){
						var blockedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
							"&taskStatus=blocked" + "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy) + "&bustcache=" + new Date().getTime() ;
						fillOPCenterGeneric(blockedURL, "blockedTasksDiv");
					}
					
					if (scrumAction == "setStatusToCompleted"){
						var completedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayTasksInSprint.jsp?sprintId=" + sprintId +
							"&taskStatus=completed" + "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy) + "&bustcache=" + new Date().getTime() ;
						fillOPCenterGeneric(completedURL, "completedTasksDiv");
					}
					
					// since removing a req from a sprint has an impact on the header
					// for this column (Total Number of Hours), we need to refresh it.
					var displaySprintHeaderURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displaySprintHeaderDiv.jsp?sprintId=" + sprintId +
						"&taskStatus="+ taskStatus + "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy) +"&bustcache=" + new Date().getTime() ;
					
					if (taskStatus == "notStarted"){
						fillOPCenterGeneric(displaySprintHeaderURL, "notStartedHeaderDiv");
					}
					if (taskStatus == "inProgress"){
						fillOPCenterGeneric(displaySprintHeaderURL, "inProgressHeaderDiv");
					}
					if (taskStatus == "blocked"){
						fillOPCenterGeneric(displaySprintHeaderURL, "blockedHeaderDiv");
					}
					if (taskStatus == "completed"){
						fillOPCenterGeneric(displaySprintHeaderURL, "completedHeaderDiv");
					}
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);
		}
		
		
		
		
		if (scrumAction == "displayAssignToNewOwner"){
			// lets hide the other two sub divs.
			document.getElementById(effortRemainingSubDivName).style.display = "none";
			document.getElementById(taskWeightSubDivName).style.display = "none";
			
			// for performance and scalability reason, we have decided to make this a new page. 
			// eg : if the page had 100's of reqs and each project had 100's of users, we didn't want
			// to take too long to load the displayTasksInSprint. so we are loading the user list only
			// as needed
			var completedURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displayAssignToNewOwner.jsp?requirementId=" + requirementId 
				+ "&sprintId=" + sprintId
				+ "&bustcache=" + new Date().getTime() ;
			fillOPCenterGeneric(completedURL, actionSubDivName);
		}
		

		if (scrumAction == "displayMoveToAnotherSprint"){
			// lets hide the other two sub divs.
			document.getElementById(actionSubDivName).style.display = "none";
			document.getElementById(moveToAnotherSprintSubDivName).style.display = "block";
			document.getElementById(taskWeightSubDivName).style.display = "none";
			document.getElementById(totalEffortSubDivName).style.display = "none";
			document.getElementById(effortRemainingSubDivName).style.display = "none";
		}
		if (scrumAction == "displaySetTotalEffort"){
			// lets hide the other two sub divs.
			document.getElementById(actionSubDivName).style.display = "none";
			document.getElementById(moveToAnotherSprintSubDivName).style.display = "none";
			document.getElementById(taskWeightSubDivName).style.display = "none";
			document.getElementById(totalEffortSubDivName).style.display = "block";
			document.getElementById(effortRemainingSubDivName).style.display = "none";
		}

		if (scrumAction == "displaySetEffortRemaining"){
			// lets hide the other two sub divs.
			document.getElementById(actionSubDivName).style.display = "none";
			document.getElementById(moveToAnotherSprintSubDivName).style.display = "none";
			document.getElementById(taskWeightSubDivName).style.display = "none";
			document.getElementById(totalEffortSubDivName).style.display = "none";
			document.getElementById(effortRemainingSubDivName).style.display = "block";
		}
		
		if (scrumAction == "displaySetTaskWeight"){
			// lets hide the other two sub divs.
			document.getElementById(actionSubDivName).style.display = "none";
			document.getElementById(moveToAnotherSprintSubDivName).style.display = "none";
			document.getElementById(totalEffortSubDivName).style.display = "none";
			document.getElementById(effortRemainingSubDivName).style.display = "none";
			document.getElementById(taskWeightSubDivName).style.display = "block";
		}
		if (scrumAction == "displayUpdateSubDiv"){
			// lets hide the other two sub divs.
			document.getElementById(actionSubDivName).style.display = "none";
			document.getElementById(moveToAnotherSprintSubDivName).style.display = "none";
			document.getElementById(totalEffortSubDivName).style.display = "none";
			document.getElementById(effortRemainingSubDivName).style.display = "none";
			document.getElementById(taskWeightSubDivName).style.display = "none";
			document.getElementById(updateSubDivName).style.display = "block";
		}
	}
	
	
	
	
	// Called from Daily Scrum page, to assign the requirement to another owner
	function assignRequirementToOwner( ownerDropDownName, requirementId, sprintId){
		
		var ownerDropDownObject = document.getElementById(ownerDropDownName);
		var owner = ownerDropDownObject.options[ownerDropDownObject.selectedIndex].value;
		
		
		var url="/GloreeJava2/servlet/AgileScrumAction";
		
		var params; 
		params = "action=assignRequirementToOwner&";
		params = params + "requirementId=" + requirementId + "&";
		params = params + "owner=" + encodeURIComponent(owner) ;
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;
		
		// lets set the requirementDiv object to 'Working'
		// we can create the reqDivName by adding requirementDiv + requirementId
		var requirementDivName = "requirementDiv" + requirementId;
		
		document.getElementById(requirementDivName).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// Once we get a response object back, we need to refresh the requirementDiv
				var displaySingleTaskURL =  "/GloreeJava2/jsp/AgileScrum/DailyScrum/displaySingleTaskInSprint.jsp?requirementId=" + requirementId 
				+ "&sprintId=" + sprintId
				+ "&bustcache=" + new Date().getTime() ;
				fillOPCenterGeneric(displaySingleTaskURL, requirementDivName);
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	
	}	
	
	
	// Called from Daily Scrum page, to move the requirement to a new sprint
	function moveToAnotherSprint(taskStatus, moveToAnotherSprintName, requirementId, sprintId){
		
		var moveToAnotherSprintObject = document.getElementById(moveToAnotherSprintName);
		var moveSprintId = moveToAnotherSprintObject.options[moveToAnotherSprintObject.selectedIndex].value;
		

		var url="/GloreeJava2/servlet/AgileScrumAction";
		
		var params; 
		params = "action=moveToAnotherSprint&";
		params = params + "requirementId=" + requirementId + "&";
		params = params + "moveSprintId=" + moveSprintId ;
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;
		
		// lets set the requirementDiv object to 'Working'
		// we can create the reqDivName by adding requirementDiv + requirementId
		var requirementDivName = "requirementDiv" + requirementId;
		
		document.getElementById(requirementDivName).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				var requirementDiv = document.getElementById(requirementDivName);
				if(requirementDiv && requirementDiv.parentNode)requirementDiv.parentNode.removeChild(requirementDiv);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
		
	
	
	
	// Called from Daily Scrum page, to set the requirement's total effort 
	function setRequirementTotalEffort(taskStatus, totalEffortName, requirementId, sprintId){
		
		var totalEffortObject = document.getElementById(totalEffortName);
		var totalEffort = totalEffortObject.value;
		
		if (isNaN(totalEffortObject.value)){
			alert ("Please enter a valid number in hours for Total Effort");
			totalEffortObject.style.backgroundColor="#FFCC99";
			totalEffortObject.focus();
			return(0);
		}
		
		var showOnlyTasksOwnedByObject = document.getElementById("showOnlyTasksOwnedBy");
		var showOnlyTasksOwnedBy = showOnlyTasksOwnedByObject.options[showOnlyTasksOwnedByObject.selectedIndex].value;
	
		
		var url="/GloreeJava2/servlet/AgileScrumAction";
		
		var params; 
		params = "action=setRequirementTotalEffort&";
		params = params + "requirementId=" + requirementId + "&";
		params = params + "totalEffort=" + totalEffort ;
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;
		
		// lets set the requirementDiv object to 'Working'
		// we can create the reqDivName by adding requirementDiv + requirementId
		var requirementDivName = "requirementDiv" + requirementId;
		
		document.getElementById(requirementDivName).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// Once we get a response object back, we need to refresh the requirementDiv
				var displaySingleTaskURL =  "/GloreeJava2/jsp/AgileScrum/DailyScrum/displaySingleTaskInSprint.jsp?requirementId=" + requirementId 
				+ "&sprintId=" + sprintId
				+ "&bustcache=" + new Date().getTime() ;
				fillOPCenterGeneric(displaySingleTaskURL, requirementDivName);
				
				// since a change in the requirement effort has an impact on the header
				// for this column (Total Number of Hours), we need to refresh it.
				var displaySprintHeaderURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displaySprintHeaderDiv.jsp?sprintId=" + sprintId +
					"&taskStatus="+ taskStatus + "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy) + "&bustcache=" + new Date().getTime() ;
				
				if (taskStatus == "notStarted"){
					fillOPCenterGeneric(displaySprintHeaderURL, "notStartedHeaderDiv");
				}
				if (taskStatus == "inProgress"){
					fillOPCenterGeneric(displaySprintHeaderURL, "inProgressHeaderDiv");
				}
				if (taskStatus == "blocked"){
					fillOPCenterGeneric(displaySprintHeaderURL, "blockedHeaderDiv");
				}
				if (taskStatus == "completed"){
					fillOPCenterGeneric(displaySprintHeaderURL, "completedHeaderDiv");
				}
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
		
	
	// Called from Daily Scrum page, to set the requirement effort remaining
	function setRequirementEffortRemaining(taskStatus, effortRemainingName, requirementId, sprintId){
		
		var effortRemainingObject = document.getElementById(effortRemainingName);
		var effortRemaining = effortRemainingObject.value;
		
		if (isNaN(effortRemainingObject.value)){
			alert ("Please enter a valid number in hours for Effort Remaining");
			effortRemainingObject.style.backgroundColor="#FFCC99";
			effortRemainingObject.focus();
			return(0);
		}
		
		var showOnlyTasksOwnedByObject = document.getElementById("showOnlyTasksOwnedBy");
		var showOnlyTasksOwnedBy = showOnlyTasksOwnedByObject.options[showOnlyTasksOwnedByObject.selectedIndex].value;
	
		var url="/GloreeJava2/servlet/AgileScrumAction";
		
		var params; 
		params = "action=setRequirementEffortRemaining&";
		params = params + "requirementId=" + requirementId + "&";
		params = params + "effortRemaining=" + effortRemaining ;
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;
		
		// lets set the requirementDiv object to 'Working'
		// we can create the reqDivName by adding requirementDiv + requirementId
		var requirementDivName = "requirementDiv" + requirementId;
		
		document.getElementById(requirementDivName).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// Once we get a response object back, we need to refresh the requirementDiv
				var displaySingleTaskURL =  "/GloreeJava2/jsp/AgileScrum/DailyScrum/displaySingleTaskInSprint.jsp?requirementId=" + requirementId 
				+ "&sprintId=" + sprintId
				+ "&bustcache=" + new Date().getTime() ;
				fillOPCenterGeneric(displaySingleTaskURL, requirementDivName);
				
				// since a change in the requirement effort has an impact on the header
				// for this column (Total Number of Hours), we need to refresh it.
				var displaySprintHeaderURL = "/GloreeJava2/jsp/AgileScrum/DailyScrum/displaySprintHeaderDiv.jsp?sprintId=" + sprintId +
					"&taskStatus="+ taskStatus + "&showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy) + "&bustcache=" + new Date().getTime() ;
				
				if (taskStatus == "notStarted"){
					fillOPCenterGeneric(displaySprintHeaderURL, "notStartedHeaderDiv");
				}
				if (taskStatus == "inProgress"){
					fillOPCenterGeneric(displaySprintHeaderURL, "inProgressHeaderDiv");
				}
				if (taskStatus == "blocked"){
					fillOPCenterGeneric(displaySprintHeaderURL, "blockedHeaderDiv");
				}
				if (taskStatus == "completed"){
					fillOPCenterGeneric(displaySprintHeaderURL, "completedHeaderDiv");
				}
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
	
	
	// Called from Daily Scrum page, to set the requirement task weight
	function setRequirementTaskWeight( taskStatus, taskWeightName, requirementId, sprintId){
		
		
		var taskWeightObject = document.getElementById(taskWeightName);
		var taskWeight = taskWeightObject.value;
		
		if (isNaN(taskWeightObject.value)){
			alert ("Please enter a valid number for Task weight (importance)");
			taskWeightName.style.backgroundColor="#FFCC99";
			taskWeightName.focus();
			return(0);
		}
		
		var showOnlyTasksOwnedByObject = document.getElementById("showOnlyTasksOwnedBy");
		var showOnlyTasksOwnedBy = showOnlyTasksOwnedByObject.options[showOnlyTasksOwnedByObject.selectedIndex].value;

		
		var url="/GloreeJava2/servlet/AgileScrumAction";
		
		var params; 
		params = "action=setRequirementTaskWeight&";
		params = params + "requirementId=" + requirementId + "&";
		params = params + "taskWeight=" + taskWeight + "&";
		params = params + "taskStatus=" + taskStatus + "&";
		params = params + "showOnlyTasksOwnedBy=" + encodeURIComponent(showOnlyTasksOwnedBy) + "&";
		params = params + "sprintId=" + sprintId ;
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;
		
		// lets set the requirementDiv object to 'Working'
		// we can create the reqDivName by adding requirementDiv + requirementId
		var requirementDivName = "requirementDiv" + requirementId;
		
		document.getElementById(requirementDivName).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		
		// we need to refresh the entire column of tasks, as the tasks get re-organized based on the task weight.
		// the most important tasks bubbling to the top.
		// based on the task status, we will figure out which column to refresh
		if (taskStatus == "notStarted"){
			fillOPCenterGeneric(url, "notStartedTasksDiv");
		}
		if (taskStatus == "inProgress"){
			fillOPCenterGeneric(url, "inProgressTasksDiv");
		}
		if (taskStatus == "blocked"){
			fillOPCenterGeneric(url, "blockedTasksDiv");
		}
		if (taskStatus == "completed"){
			fillOPCenterGeneric(url, "completedTasksDiv");
		}
	}
	

	
	//////////////////////////////////////////////////////////////////////////
	// //
	// TDCS //
	// //
	// ////////////////////////////////////////////////////////////////////////

	// called when someone requests to open the project info in the tool.
	function displayTDCSHome(){
		
		// set the other content centers to empty.
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		
		
		// Now fill contentCenterB with the folder details.
		document.getElementById("contentCenterA").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/TDCS/displayTDCSHome.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "contentCenterA");
	}


	// this is called when a user applies a filter ont he displayTDCSDocuments screen
	function filterTDCSDocuments(){
		var tDCSFilterObject = document.getElementById("tDCSFilter");
		var tDCSFilter = tDCSFilterObject.options[tDCSFilterObject.selectedIndex].value;
		
		// the way we get the filter value depends on the filterObject's value
		// we may have to get value from a drop down box or a text box .
		var tDCSFilterValue = "";
		if (tDCSFilter == "project"){
			tDCSFilterValue = "";
		}
		
		if (tDCSFilter == "folder") {
			// this is a drop down
			tDCSFilterValueObject = document.getElementById("tDCSFilterValue-Folder");
			tDCSFilterValue = tDCSFilterValueObject.options[tDCSFilterValueObject.selectedIndex].value;
		}
		if (tDCSFilter == "fileType") {
			// this is a drop down
			tDCSFilterValueObject = document.getElementById("tDCSFilterValue-FileType");
			tDCSFilterValue = tDCSFilterValueObject.options[tDCSFilterValueObject.selectedIndex].value;
		}
		if (tDCSFilter == "documentStatus") {
			// this is a drop down
			tDCSFilterValueObject = document.getElementById("tDCSFilterValue-DocumentStatus");
			tDCSFilterValue = tDCSFilterValueObject.options[tDCSFilterValueObject.selectedIndex].value;
		}
		if (tDCSFilter == "approvalStatus") {
			// this is a drop down
			tDCSFilterValueObject = document.getElementById("tDCSFilterValue-ApprovalStatus");
			tDCSFilterValue = tDCSFilterValueObject.options[tDCSFilterValueObject.selectedIndex].value;
		}
		if (tDCSFilter == "title") {
			// this is a text box
			tDCSFilterValue = document.getElementById("tDCSFilterValue-Title").value;
		}
		if (tDCSFilter == "documentId") {
			// this is a text box
			tDCSFilterValue = document.getElementById("tDCSFilterValue-DocumentId").value;
		}

		// lets get the display value.
		var tDCSDisplayObject = document.getElementById("tDCSDisplay");
		var tDCSDisplay = "";
		for (var i = 0; i < tDCSDisplayObject.options.length; i++) {
			if (tDCSDisplayObject.options[i].selected) {
				tDCSDisplay += tDCSDisplayObject.options[i].value + ','; 
			}
		}
		
		// lets get the sort value
		tDCSSortByObject = document.getElementById("tDCSSortBy");
		tDCSSortByValue = tDCSSortByObject.options[tDCSSortByObject.selectedIndex].value;
		
		

		var url="/GloreeJava2/jsp/TDCS/displayFilteredTDCSDocuments.jsp?";
		url += "&tDCSFilter=" + encodeURIComponent(tDCSFilter);
		url += "&tDCSFilterValue=" + encodeURIComponent(tDCSFilterValue);
		url += "&tDCSDisplay=" + encodeURIComponent(tDCSDisplay);
		url += "&tDCSSortBy=" + encodeURIComponent(tDCSSortByValue);
		url += "&bustcache=" + new Date().getTime() ;

			
		document.getElementById("tDCSDocumentsDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "tDCSDocumentsDiv");
	}
	

	function getTDCSDocumentInfo(tDCSDocumentFullTag){
		// set the other content centers to empty.
		document.getElementById("displayTDCSDocumentInfoDiv").style.display = "block";
		document.getElementById("displayTDCSDocumentInfoDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		var url="/GloreeJava2/jsp/TDCS/displayTDCSInfo.jsp?";
		url+= "&tDCSDocumentFullTag=" + tDCSDocumentFullTag;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "displayTDCSDocumentInfoDiv");
	}
	
	function processTDCSDocumentAction(tDCSDocumentId){
		
		var actionObject = document.getElementById('tDCSAction' + tDCSDocumentId);
		
		if (actionObject.selectedIndex == 0){
			// this is a not a valid option.
			return(0);
		}
		
		// set the other content centers to empty.
		var divName = "DocumentDetailsDiv" + tDCSDocumentId;
		document.getElementById(divName).style.display = "block";
		document.getElementById(divName).innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		
		
		var action = actionObject[actionObject.selectedIndex].value;
		
		// lets get the display value.
		var tDCSDisplayObject = document.getElementById("tDCSDisplay");
		var tDCSDisplay = "";
		for (var i = 0; i < tDCSDisplayObject.options.length; i++) {
			if (tDCSDisplayObject.options[i].selected) {
				tDCSDisplay += tDCSDisplayObject.options[i].value + ','; 
			}
		}
		// lets get the sort value
		tDCSSortByObject = document.getElementById("tDCSSortBy");
		tDCSSortByValue = tDCSSortByObject.options[tDCSSortByObject.selectedIndex].value;
		
		var tDCSFilterObject = document.getElementById("tDCSFilter");
		var tDCSFilter = tDCSFilterObject.options[tDCSFilterObject.selectedIndex].value;
		
		// the way we get the filter value depends on the filterObject's value
		// we may have to get value from a drop down box or a text box .
		var tDCSFilterValue = "";
		if (tDCSFilter == "project"){
			tDCSFilterValue = "";
		}
		if (tDCSFilter == "mine"){
			tDCSFilterValue = "";
		}
		if 	(tDCSFilter == "user"){
			// this is a text box
			tDCSFilterValue = document.getElementById("tDCSFilterValue-User").Value;
		}
		
		if (tDCSFilter == "folder") {
			// this is a drop down
			tDCSFilterValueObject = document.getElementById("tDCSFilterValue-Folder");
			tDCSFilterValue = tDCSFilterValueObject.options[tDCSFilterValueObject.selectedIndex].value;
		}
		if (tDCSFilter == "fileType") {
			// this is a drop down
			tDCSFilterValueObject = document.getElementById("tDCSFilterValue-FileType");
			tDCSFilterValue = tDCSFilterValueObject.options[tDCSFilterValueObject.selectedIndex].value;
		}
		if (tDCSFilter == "documentStatus") {
			// this is a drop down
			tDCSFilterValueObject = document.getElementById("tDCSFilterValue-DocumentStatus");
			tDCSFilterValue = tDCSFilterValueObject.options[tDCSFilterValueObject.selectedIndex].value;
		}
		if (tDCSFilter == "approvalStatus") {
			// this is a drop down
			tDCSFilterValueObject = document.getElementById("tDCSFilterValue-ApprovalStatus");
			tDCSFilterValue = tDCSFilterValueObject.options[tDCSFilterValueObject.selectedIndex].value;
		}
		if (tDCSFilter == "title") {
			// this is a text box

			tDCSFilterValue = document.getElementById("tDCSFilterValue-Title").value;
		}
		if (tDCSFilter == "documentId") {
			// this is a text box
			tDCSFilterValue = document.getElementById("tDCSFilterValue-DocumentId").value;
		}

		
		
		url = "/GloreeJava2/servlet/TDCSAction?tDCSDocumentId=" + tDCSDocumentId;
		url += "&action=" + encodeURIComponent(action) + "&";
		url += "&tDCSDisplay=" + encodeURIComponent(tDCSDisplay) + "&";
		url += "&tDCSSortBy=" + encodeURIComponent(tDCSSortByValue);
		url += "&tDCSFilter=" + encodeURIComponent(tDCSFilter);
		url += "&tDCSFilterValue=" + encodeURIComponent(tDCSFilterValue);
		url += "&bustcache=" + new Date().getTime() ;

		if(action == "showPreviousVersions"){ 
			fillOPCenterGeneric(url, divName);
		}
		if ((action == "lockDocument") || (action == "unlockDocument")){
			// in this case, we want to lock  / unlock the doc and then
			// re run the filter documents query.
			document.getElementById("tDCSDocumentsDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			fillOPCenterGeneric(url, "tDCSDocumentsDiv");
		}
	}
	
	function pushWordTemplateReportToTDCS(folderId, templateId, tDCSAction, tDCSDocumentFullTag, reportFormat){
		
		var title = "";
		var titleObject = document.getElementById("title");
		if (titleObject != null) {
			title = titleObject.value;
		}
		var description = "";
		var descriptionObject = document.getElementById("description");
		if (descriptionObject != null) {
			description = descriptionObject.value;
		}
	

		// lets get the display value.
		var displayAttributesObject = document.getElementById("displayAttributes");
		var displayAttributes = "";
		for (var i = 0; i < displayAttributesObject.options.length; i++) {
			if (displayAttributesObject.options[i].selected) {
				displayAttributes += displayAttributesObject.options[i].value + ','; 
			}
		}
		
		
		url = "/GloreeJava2/servlet/WordTemplateAction?" 
		url += "&action=" + encodeURIComponent("pushReportToTDCS") + "&";
		url += "&folderId=" + encodeURIComponent(folderId) + "&";
		url += "&templateId=" + encodeURIComponent(templateId) + "&";
		url += "&tDCSAction=" + encodeURIComponent(tDCSAction) + "&";
		url += "&tDCSDocumentFullTag=" + encodeURIComponent(tDCSDocumentFullTag) + "&";
		url += "&title=" + encodeURIComponent(title) + "&";
		url += "&reportFormat=" + encodeURIComponent(reportFormat) + "&";
		url += "&displayAttributes=" + encodeURIComponent(displayAttributes) + "&";
		url += "&description=" + encodeURIComponent(description) + "&";
		
		var requirementOutputFormatObject = document.getElementById("requirementOutputFormat");
		var requirementOutputFormatValue = requirementOutputFormatObject.options[requirementOutputFormatObject.selectedIndex].value ;
		
		url += "&requirementOutputFormat=" + encodeURIComponent(requirementOutputFormatValue) + "&";
		url += "&bustcache=" + new Date().getTime() ;

		document.getElementById("pushWordTemplateReportToTDCSResponse").style.display='block';
		document.getElementById("pushWordTemplateReportToTDCSResponse").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "pushWordTemplateReportToTDCSResponse");		
		
	}
	//////////////////////////////////////////////////////////////////////////
	// //
	// Shared Requirements//
	// //
	// ////////////////////////////////////////////////////////////////////////
	function displaySharedRequirements(){
		

		
		// set the other content centers to empty.
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		

		// Now fill contentCenterA with the SRMT Menu.
		document.getElementById("contentCenterA").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/SharedRequirements/displaySharedRequirementsMenu.jsp?";
		url += "&bustcache=" + new Date().getTime() ;

		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterA').style.display = 'block'; 
				document.getElementById('contentCenterA').innerHTML=xmlHttpOPCenterB.responseText;
				// the menu tab items are on the screen now. So lets adjust focus
				// Now adjust tabs focus
				var focusTab = "url(/GloreeJava2/images/focusTab.png)";
				var nonFocusTab = "url(/GloreeJava2/images/nonFocusTab.png)";
				
				var  menuPublishSharedRequirementsObject = document.getElementById("menuPublishSharedRequirements");
				if (menuPublishSharedRequirementsObject != null) {
					menuPublishSharedRequirementsObject.style.backgroundImage = focusTab;
				}
				var  menuImportSharedRequirementsObject = document.getElementById("menuImportSharedRequirements");
				if (menuImportSharedRequirementsObject != null) {
					menuImportSharedRequirementsObject.style.backgroundImage = nonFocusTab;
				}
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		


		document.getElementById("contentCenterB").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/SharedRequirements/publishSharedRequirements.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		fillOPCenterGeneric(url, "contentCenterB");
	}
	
	function displaySharedRequirementTypeForm(){

		var sharedRequirementTypeIdObject = document.getElementById("sharedRequirementTypeId");
		var sharedRequirementTypeId = sharedRequirementTypeIdObject[sharedRequirementTypeIdObject.selectedIndex].value;
		// Now fill contentCenterA with the SRMT Menu.
		document.getElementById("publishSharedRequirementTypeDiv").style.display = "block";
		document.getElementById("publishSharedRequirementTypeDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/SharedRequirements/displaySharedRequirementType.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&sharedRequirementTypeId=" + encodeURIComponent(sharedRequirementTypeId) + "&";
		fillOPCenterGeneric(url, "publishSharedRequirementTypeDiv");

	}
	
	function updateSharedRequirementTypeSettings(requirementTypeId, attributeIdString){
		
		var url="/GloreeJava2/servlet/SharedRequirementAction?action=updateSharedRequirementType";
		url += "&requirementTypeId=" + encodeURIComponent(requirementTypeId) ;
		url += "&bustcache=" + new Date().getTime() ;

		
		var sRPublishStatusValue = "";
		
		for (var i=0; i < document.publish.sRPublishStatus.length; i++) {
		   if (document.publish.sRPublishStatus[i].checked == true) {
			   sRPublishStatusValue = document.publish.sRPublishStatus[i].value;
		   }
		}


		url = url + '&sRPublishStatus='+  encodeURIComponent(sRPublishStatusValue) ;
		
		var sRRTBaselineIdObject = document.getElementById("sRRTBaselineId");
		var sRRTBaselineIds = "";
		for (var i = 0; i < sRRTBaselineIdObject.options.length; i++) {
			if (sRRTBaselineIdObject.options[i].selected) {
				sRRTBaselineIds += "rTBaselineId" + sRRTBaselineIdObject.options[i].value + ','; 
			}
		}
		url = url + '&sRRTBaselineIds='+  encodeURIComponent(sRRTBaselineIds) ;
		
		
		var sRShareCommentsObject = document.getElementById("sRShareComments");
		url = url + '&sRShareComments='+  encodeURIComponent(sRShareCommentsObject[sRShareCommentsObject.selectedIndex].value) ;

		// lets make sure domain Administrators is not empty.
		var domainAdministrators = document.getElementById("sRDomainAdministrators").value;
		if (domainAdministrators == ""){
			alert ('Pleaes enter a Domain Administrators Email Id');
			document.getElementById("sRDomainAdministrators").focus();
			document.getElementById("sRDomainAdministrators").style.backgroundColor="#FFCC99"; 
			return(0);
		}
		
		url = url + '&sRDomainAdministrators='+  encodeURIComponent(document.getElementById("sRDomainAdministrators").value) ;
		var sRMandatoryNotificationObject = document.getElementById("sRMandatoryNotification");
		url = url + '&sRMandatoryNotification='+  encodeURIComponent(sRMandatoryNotificationObject[sRMandatoryNotificationObject.selectedIndex].value) ;
		url = url + '&sRInstructions='+  encodeURIComponent(document.getElementById("sRInstructions").value) ;
		
		
		// we use the attributeIdString to split and figure out which attributes
		// to look for.
		// for these attributes , we get the value, build the URL string and .
		// we will use the same logic to get the attribute values and create
		// them in the db.
		attributeIds = attributeIdString.split('##');
		
		for (a in attributeIds){
			var rTAttributeId = attributeIds[a];
			var copySelectName = "copyable" + rTAttributeId;
			var copyableObject = document.getElementById(copySelectName);
			if (copyableObject != null){
				url = url + '&copyable' +  rTAttributeId + '='+  encodeURIComponent(copyableObject[copyableObject.selectedIndex].value) ;
			}
			
			var filterSelectName = "filterable" + rTAttributeId;
			var filterableObject = document.getElementById(filterSelectName);
			if (filterableObject != null){
				url = url + '&filterable' +  rTAttributeId + '='+  encodeURIComponent(filterableObject[filterableObject.selectedIndex].value) ;
			}

			var filterSelectName = "displayable" + rTAttributeId;
			var filterableObject = document.getElementById(filterSelectName);
			if (filterableObject != null){
				url = url + '&displayable' +  rTAttributeId + '='+  encodeURIComponent(filterableObject[filterableObject.selectedIndex].value) ;
			}

			var filterSelectName = "editable" + rTAttributeId;
			var filterableObject = document.getElementById(filterSelectName);
			if (filterableObject != null){
				url = url + '&editable' +  rTAttributeId + '='+  encodeURIComponent(filterableObject[filterableObject.selectedIndex].value) ;
			}

		}
		document.getElementById("updateSharedSettingsButton").disabled=true;
		fillOPCenterGeneric(url, "publishSharedRequirementTypeDiv");
	}
	
	
	function displayImportSharedRequirements(){
		

		
		// set the other content centers to empty.
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		

		// Now adjust tabs focus
		var focusTab = "url(/GloreeJava2/images/focusTab.png)";
		var nonFocusTab = "url(/GloreeJava2/images/nonFocusTab.png)";
		
		var  menuPublishSharedRequirementsObject = document.getElementById("menuPublishSharedRequirements");
		if (menuPublishSharedRequirementsObject != null) {
			menuPublishSharedRequirementsObject.style.backgroundImage = nonFocusTab;
		}
		var  menuImportSharedRequirementsObject = document.getElementById("menuImportSharedRequirements");
		if (menuImportSharedRequirementsObject != null) {
			menuImportSharedRequirementsObject.style.backgroundImage = focusTab;
		}
		

		document.getElementById("contentCenterB").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/SharedRequirements/importSharedRequirements.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		fillOPCenterGeneric(url, "contentCenterB");
	}
	
	function displayImportSharedRequirementTypeForm(){

		var sharedProjectIdObject = document.getElementById("sharedProjectId");
		var sharedProjectId = sharedProjectIdObject[sharedProjectIdObject.selectedIndex].value;
		// Now fill contentCenterA with the SRMT Menu.
		document.getElementById("importSharedRequirementTypeDiv").style.display = "block";
		document.getElementById("importSharedRequirementTypeDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/SharedRequirements/importSharedRequirements-RT.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&sharedProjectId=" + encodeURIComponent(sharedProjectId) + "&";
		fillOPCenterGeneric(url, "importSharedRequirementTypeDiv");
	}

	function displayImportSharedRequirementAttributeFilterForm(){

		var sharedRequirementTypeIdObject = document.getElementById("sharedRequirementTypeId");
		var sharedRequirementTypeId = sharedRequirementTypeIdObject[sharedRequirementTypeIdObject.selectedIndex].value;
		// Now fill contentCenterA with the SRMT Menu.
		document.getElementById("importSharedRequirementAttributeFilterDiv").style.display = "block";
		document.getElementById("importSharedRequirementAttributeFilterDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/SharedRequirements/importSharedRequirements-AF.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&sharedRequirementTypeId=" + encodeURIComponent(sharedRequirementTypeId) + "&";
		fillOPCenterGeneric(url, "importSharedRequirementAttributeFilterDiv");
	}

	function filterSharedRequirements(attributeIdStringDropDown,attributeIdStringTextBox){

		document.getElementById("sharedRequirementsFilterSectionDiv").style.display='none';
		var sharedRequirementTypeIdObject = document.getElementById("sharedRequirementTypeId");
		var sharedRequirementTypeId = sharedRequirementTypeIdObject[sharedRequirementTypeIdObject.selectedIndex].value;

		var sRRTBaselineIdObject = document.getElementById("sRRTBaselineId");
		var sRRTBaselineId = sRRTBaselineIdObject[sRRTBaselineIdObject.selectedIndex].value;
		
		var onlyNewOrChanged = 'no';
		if (document.getElementById("onlyNewOrChanged").checked){
			onlyNewOrChanged = 'yes';
		}
		else{
			onlyNewOrChanged = 'no';
		}
		

		var url="/GloreeJava2/servlet/SharedRequirementAction?action=filterSharedRequirements";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&sRRTBaselineId=" + sRRTBaselineId ;
		url += "&onlyNewOrChanged=" + onlyNewOrChanged ;
		url += "&sharedRequirementTypeId=" + encodeURIComponent(sharedRequirementTypeId) + "&";

		// we use the attributeIdString to split and figure out which attributes
		// to look for.
		// for these attributes , we get the value, build the URL string and .
		// we will use the same logic to get the attribute values and create
		// them in the db.
		attributeIds = attributeIdStringDropDown.split('##');
		
		for (a in attributeIds){
			var rTAttributeId = attributeIds[a];
			var filterName = "filterAttribute" + rTAttributeId;
			var filterObject = document.getElementById(filterName);
			if (filterObject != null){
				// since this is a multi select, iterate through this to get selected values.
				
				var dropDownSearch = "";
				for (var i = 0; i < filterObject.options.length; i++) {
					if (filterObject.options[i].selected) {
						dropDownSearch += filterObject.options[i].value + ':##:';
	
					}
				}
				// add the attributeid / concatenated selected values.
				url = url + '&filterAttribute' +  rTAttributeId + '='+  encodeURIComponent(dropDownSearch) ;
			}
		}

		attributeIds = attributeIdStringTextBox.split('##');
		
		for (a in attributeIds){
			var rTAttributeId = attributeIds[a];
			var filterName = "filterAttribute" + rTAttributeId;
			var filterObject = document.getElementById(filterName);
			if (filterObject != null){
				url = url + '&filterAttribute' +  rTAttributeId + '='+  encodeURIComponent(filterObject.value) ;
			}
		}
		
		// Now fill contentCenterA with the SRMT Menu.
		document.getElementById("displayFilteredSharedRequiremensDiv").style.display = "block";
		document.getElementById("displayFilteredSharedRequiremensDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "displayFilteredSharedRequiremensDiv");
	}
	
	// this function is used to select all the checkboxes in the form
	// used in Import Shared Requirments Filter form.
	function selectAllRequirementInImportSharedRequirements() {
		

		var thisForm = document.getElementById("importSharedRequirementsForm");
		// if only one req is displayed in data grid, the checkbox stops behaving like an array
		// and behaves like a single element. in this scenario checkbox.length craps out.
		// to catch this scenario we have to do this...
		if (thisForm.requirementId.length == null){
			// only one requirement row in data grid.
			thisForm.requirementId.checked = true;
		}
		else {
			// multiple requirement rows in data grid.
			for (i=0; i<thisForm.requirementId.length;i++){
				thisForm.requirementId[i].checked = true;
			}			
		}	
			
	}
	

	// this function is used to deselect all the checkboxes in the form
	// used in Import Shared Requirments Filter form.
	function deSelectAllRequirementInImportSharedRequirements() {
		
		
		var thisForm = document.getElementById("importSharedRequirementsForm");
		// if only one req is displayed in data grid, the checkbox stops behaving like an array
		// and behaves like a single element. in this scenario checkbox.length craps out.
		// to catch this scenario we have to do this...
		if (thisForm.requirementId.length == null){
			// only one requirement row in data grid.
			thisForm.requirementId.checked = false;
		}
		else {
			// multiple requirement rows in data grid.
			for (i=0; i<thisForm.requirementId.length;i++){
				thisForm.requirementId[i].checked = false;
			}			
		}
			
	}
	
		
	function importUpdateSharedRequirements(sharedRequirementTypeId, sRRTBaselineId){
		// lets loop through the check boxes and see which reqs have been selected.
		var sharedRequirementIds  = "";
		var importSharedRequirementsForm = document.getElementById("importSharedRequirementsForm"); 
		
		// now lets get target requirements. These are the selected reqs.
		// we will need to get all the reqs and concatenate them with :##:
		// if only one req is displayed in data grid, the checkbox stops behaving like an array
		// and behaves like a single element. in this scenario checkbox.length craps out.
		// to catch this scenario we have to do this...
		if (importSharedRequirementsForm.requirementId.length == null){
			// only one requirement row in data grid.
			if (importSharedRequirementsForm.requirementId.checked == true ){
				sharedRequirementIds += importSharedRequirementsForm.requirementId.value + ":##:";
			}
		}
		else {
			// multiple requirement rows in data grid.
			for (i=0; i<importSharedRequirementsForm.requirementId.length;i++){
				if (importSharedRequirementsForm.requirementId[i].checked == true ){
					sharedRequirementIds += importSharedRequirementsForm.requirementId[i].value + ":##:";
				}
			}		
			
		}
	
	
		// lets make sure that at least some requirements are selected.
		if (sharedRequirementIds == '') {
			document.getElementById("filterRequirementsMessageDiv").style.display = 'block';
			document.getElementById("filterRequirementsMessageDiv").innerHTML =
				"<div style='float:left' class='alert alert-success'>" +
				"<span class='normalText' >  Please select at least one requirement to Import/Update. " +
				"</span>" +
				"</div>" +
				"<div style='float: right;'>" +
				" <a href='#' onclick=\"document.getElementById('filterRequirementsMessageDiv').style.display='none'\"> " +
				" Close" +
				" </a>" +
				"</div> " ; 
			return(0);
 		}
		
		var importMessage = "<span class='normalText'>" +
			" During this import, the system will <br>" +
			" 1. Identify new source Requirements and create them in the Target Project.<br>" +
			" 2. Inentify changed source Requirements and update the previously copied Target Requirements. <br>" +
			" 3. Reset traceability to ensure there are no suspect traces. <br>" +
			" 4. Create an Excel file of this import effort and store it in the TDCS of the Target Project. and <br> " +
			" 5. Store a copy of the import effort in the Source Project. <br> " +
			" </span>";
		document.getElementById("displayFilteredSharedRequiremensDiv").style.display = "block";
		document.getElementById("displayFilteredSharedRequiremensDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		
		// lets build the url here.

		var url="/GloreeJava2/servlet/SharedRequirementAction";
		var params = "action=importUpdateSharedRequirements";
		params += "&bustcache=" + new Date().getTime() ;
		params += "&sRRTBaselineId=" + sRRTBaselineId;
		params += "&sharedRequirementIds=" + encodeURIComponent(sharedRequirementIds);
		params += "&sharedRequirementTypeId=" + encodeURIComponent(sharedRequirementTypeId) + "&";

		
		// since the number of selected reqs can be large and get has a limit
		// we will use post.
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.open("POST", url, true);

		// Send the proper header information along with the request
		xmlHttpOPCenterB.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xmlHttpOPCenterB.setRequestHeader("Content-length", params.length);
		xmlHttpOPCenterB.setRequestHeader("Connection", "close");

		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// since this might have created a new req type (folder)
				// lets make a call to refresh the explorer.
				var contentResponse = xmlHttpOPCenterB.responseText;
				if  (contentResponse.length > 0){
					// this means a neq req type / folder has been created
					// so lets add them to the tree and display them.
					var contentResponseObject = eval ("(" + contentResponse + ")" );
	
					var folderId = contentResponseObject.folderId;
					var folderName = contentResponseObject.folderName;
					var folderDescription = contentResponseObject.folderDescription;
					
					// Create the child node, add it to the parent, enter the values
					// in contextExlements so that
					// the contextMenu trigger can pick up the folder values and do
					// the same with folderNodeMap.
					var myNodeData = {  label: folderName,  title: folderDescription,  id:folderId }; 
					var tmpNode = new YAHOO.widget.TextNode(myNodeData, tree.getRoot() , false);
					tmpNode.labelStyle = 'icon-folder'; 
					contextElements.push(tmpNode.labelElId);
					folderNodeMap[tmpNode.labelElId] = folderId;
					tree.draw();

				}
			
				// now lets display the results.
				var url2 = "/GloreeJava2/jsp/SharedRequirements/displayImportUpdateResults.jsp?";
				fillOPCenterGeneric(url2, "displayFilteredSharedRequiremensDiv");
			}
		}
		xmlHttpOPCenterB.send(params);
	}
	//////////////////////////////////////////////////////////////////////////
	// //
	// Project //
	// //
	// ////////////////////////////////////////////////////////////////////////

	// called when someone requests to open the project info in the tool.
	function displayProjectInfo(){
		
		try {
		// set the other content centers to empty.
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		}
		catch (err) {console.log(err.message);}
		if (document.getElementById('createTracesDiv') != null){
			document.getElementById('createTracesDiv').style.display='none';
		}
		
		// Now fill contentCenterB with the folder details.
		document.getElementById("contentCenterA").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/Project/displayProjectInfo.jsp?";
		fillOPCenterGeneric(url, "contentCenterA");
	}

	// called when someone tries to search for projects in an onSite installation.
	function findProjects(){
		
		document.getElementById("findButton").disabled = true;
		document.getElementById("foundProjectsDiv").style.display = "block";
		
		// Now fill contentCenterB with the folder details.
		document.getElementById("foundProjectsDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/Project/displayFoundProjects.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&projectSearchString=" + encodeURIComponent(document.getElementById("projectSearchString").value) + "&";;

		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('foundProjectsDiv').style.display = 'block'; 
				document.getElementById('foundProjectsDiv').innerHTML=xmlHttpOPCenterB.responseText;
				document.getElementById("findButton").disabled = false;				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		

	}	
	
	function requestAccessToProject(projectId){
		
		document.getElementById("requestAccessDiv"+ projectId).style.display= 'block';
		document.getElementById("requestAccessDiv"+ projectId).innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		url= "/GloreeJava2/servlet/ProjectAction?action=requestAccessToProject&projectId=" + projectId;
		
		fillOPCenterGeneric(url, "requestAccessDiv"+ projectId);
	}
		
	
	function inviteOthersToProject(projectId, cancelInviteId){
		var inviteOthersDiv = "inviteOthersDiv"+ projectId;
		document.getElementById(inviteOthersDiv).style.display= 'block';
		document.getElementById(inviteOthersDiv).innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		url= "/GloreeJava2/jsp/Project/inviteOthersToProjects.jsp?action=displayOptions&projectId=" + projectId;
		
		url += "&cancelInviteId=" + cancelInviteId ;
		
		fillOPCenterGeneric(url, inviteOthersDiv);
	}	
	
	
	function withdrawInvitation(inviteId){
		document.getElementById("userInvitationsDiv").style.display= 'block';
		document.getElementById("userInvitationsDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		
		
		url= "/GloreeJava2/servlet/ProjectAction?action=withdrawInvitation&inviteId=" + inviteId;
		
		fillOPCenterGeneric(url, "userInvitationsDiv");
	}	
	
	

	function inviteOthersToProjectNow(projectId){
		
		
		// lets make sure that the email ids are valid.
		var emailIds = document.getElementById("inviteeEmailIds" + projectId);
		
		if (emailIds.value.length == 0) {
			alert ("Please enter Comma separated list of Email ids");
			emailIds.focus();
			emailIds.style.backgroundColor="#FFCC99";
			return(0);
		}

		// lets ensure that to has only valid email ids.
		// first replace any occurence of space or ; with ,
		
		var to = emailIds.value;
		if (to != null){ 
			to = to.replace(" ", ",");
			
			to = to.replace(";", ",");
			
			// some of the logic may create one or more of ,, . so we need to replace them with , . 
			
			to = to.replace("/,+/", ",");
			
			// now lets split each one of these email id and see if they are valid.
			toArray = to.split(',');
			
			for (t in toArray){
				var currentTo = toArray[t];
				currentTo = currentTo.replace(" ", "");
				if ((currentTo != null) && (currentTo.length > 0)){
					if (echeck(currentTo)==false){
						alert ("Your email id " + currentTo + " is not formatted correctly. Please fix it.");
						emailIds.focus();
						emailIds.style.backgroundColor="#FFCC99";
						return(0);
					}
				}
			}
		}
		
		// lets make sure that at least one role is selected. 
		var rolesObject = document.getElementById("roles" + projectId);
		
		var roles = "";
		for (var i = 0; i < rolesObject.options.length; i++) {
			if (rolesObject.options[i].selected) {
				roles += rolesObject.options[i].value + ','; 
			}
		}
		
		if (roles.length == 0) {
			alert ("Please select at least one Role");
			rolesObject.focus();
			rolesObject.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		// lets send the info to the server.
		var inviteOthersDiv = "inviteOthersDiv"+ projectId;
		document.getElementById(inviteOthersDiv).style.display= 'block';
		document.getElementById(inviteOthersDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		
		url= "/GloreeJava2/jsp/Project/inviteOthersToProjects.jsp?action=inviteOthersToProjectNow&projectId=" + projectId ;
		url += "&emailIds=" + encodeURIComponent(emailIds.value) + "&";
		url += "&roles=" + encodeURIComponent(roles) ;
		
		
		fillOPCenterGeneric(url, inviteOthersDiv);
	}	
	
	
	//////////////////////////////////////////////////////////////////////////
	// //
	// Folders //
	// //
	// ////////////////////////////////////////////////////////////////////////

	// called when someone requests to open a folder in oPExplorer.jsp
	function displayFolder(folderId){
		// set the other content centers to empty.
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		
		displayFolderInExplorer(folderId);
		displayFolderContentCenterA(folderId) ;
		displayFolderCore(folderId);
		//displayFolderContentRight(folderId);
		
	}

	// used to display the Content Right (expand tree and set focus on this folder object)
	function displayFolderInExplorer(folderId) {
		// lets do some magic to set focus on this folder object.
		// set the background of the current focus folder to blue
		
		
		// if the previous folderis not null, set it's background to gray.
		var prevFocusFolder = document.getElementById(prevFocusFolderId); 
		 if (prevFocusFolder != null){
			prevFocusFolder.style.backgroundColor = "white";
		 }
		 
		folderNode = tree.getNodeByProperty('id',folderId);
		currentFolderNodeId = folderNode.labelElId;
		// lets also set the prevFocusFolderId to this folder so that
		// when we jump to the next folder this can be set to light gray background.
		
		prevFocusFolderId = folderNode.labelElId;

		// put focus on the current Folder.
		currentFolderNode = document.getElementById(currentFolderNodeId);
		if (currentFolderNode != null) {
			currentFolderNode.style.backgroundColor = "lightpink";
		}
	}
	
	// Used to display only the Core of the the folder
	// ie the top tabs.
	function displayFolderCore(folderId){
		

		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";	
		document.getElementById("contentCenterF").style.display = "none";	
	
		// if folderId has -1 in it, then it's a virtual folders.
		// we need special handling for virtual folders
		
		// NOTE : Because Javascript is anal about strings and numbers
		// .IndexOf fails when pos is a number. So add "" to convert the number
		// to string.
		folderId = folderId + "";
		var pos	 = folderId.indexOf("-1");
		if (pos < 0){

			// This is a real folder.
			// Now fill contentCenterA with the folder details.
			var url="/GloreeJava2/jsp/Folder/displayRealFolderCore.jsp?folderId="+ folderId;
			fillOPCenterGeneric(url, "contentCenterB");
		}
		else {
			// This is a virtual folder.has -1:reqtype id in folderId param.
			// Now fill contentCenterA with the folder details.
			var url="/GloreeJava2/jsp/Folder/displayVirtualFolderCore.jsp?folderId="+ folderId;
			fillOPCenterGeneric(url, "contentCenterB");
		}
	}
	

	function displayFolderCoreInTab(folderId){
		
	
		// if folderId has -1 in it, then it's a virtual folders.
		// we need special handling for virtual folders
		
		// NOTE : Because Javascript is anal about strings and numbers
		// .IndexOf fails when pos is a number. So add "" to convert the number
		// to string.
		folderId = folderId + "";
		var pos	 = folderId.indexOf("-1");
		if (pos < 0){

			// This is a real folder.
			// Now fill contentCenterA with the folder details.
			var url="/GloreeJava2/jsp/Folder/displayRealFolderCore.jsp?folderId="+ folderId;
			fillOPCenterGeneric(url, "folderInfoDetailsDiv");
		}
		else {
			// This is a virtual folder.has -1:reqtype id in folderId param.
			// Now fill contentCenterA with the folder details.
			var url="/GloreeJava2/jsp/Folder/displayVirtualFolderCore.jsp?folderId="+ folderId;
			fillOPCenterGeneric(url, "folderInfoDetailsDiv");
		}
	}
	
	
	// Used to display only the ContentCenterA portionof the folder
	// ie the top tabs.
	function displayFolderContentCenterA(folderId){ 
		// lets scroll to the top of the page
		
		
		if (document.getElementById("createTracesDiv") != null){
			document.getElementById("createTracesDiv").style.display="none";
		}
		if (document.getElementById('attributeInfo') != null){
			document.getElementById('attributeInfo').style.display='block';
		}
		
		// any time we display FolderInContentCenterA, lets also display the Object Explorer
		displayFolderContentsInExplorerInBackground(folderId)
		
		// if folderId has -1 in it, then it's a virtual folders.
		// we need special handling for virtual folders
		
		// NOTE : Because Javascript is anal about strings and numbers
		// .IndexOf fails when pos is a number. So add "" to convert the number
		// to string.
		folderId = folderId + "";
		var pos	 = folderId.indexOf("-1");
		if (pos < 0){

			// This is a real folder.
			// Now fill contentCenterA with the folder details.
			var url="/GloreeJava2/jsp/Folder/displayRealFolder.jsp?folderId="+ folderId;
			fillOPCenterGeneric(url, "contentCenterA");
		}
		else {
			// This is a virtual folder.has -1:reqtype id in folderId param.
			// Now fill contentCenterA with the folder details.
			var url="/GloreeJava2/jsp/Folder/displayVirtualFolder.jsp?folderId="+ folderId;
			fillOPCenterGeneric(url, "contentCenterA");
		}
		
		
		
	}
	
	
	
	
	function displayAllRequirementsInRealFolder(folderId, sortBy, filterType, filterValue){
	
		

		genericLog(folderId,"Folder", " Display All Requirements in Folder");
		
		// set the other content centers to empty.
		document.getElementById("contentCenterB").style.display = "none";	
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";	
		document.getElementById("contentCenterF").style.display = "block";	
		document.getElementById("contentCenterG").style.display = "none";
		document.getElementById("contentCenterComments").style.display="none";
		document.getElementById("contentCenterAttachments").style.display="none";

		
		document.getElementById("reqTabs").style.display = "none";
		
	
		
		// if folderId has -1 in it, then it's a virtual folders.
		// we need special handling for virtual folders
		
		// NOTE : Because Javascript is anal about strings and numbers
		// .IndexOf fails when pos is a number. So add "" to convert the number
		// to string.
		folderId = folderId + "";
		var pos	 = folderId.indexOf("-1");
		if (pos < 0){

			// This is a real folder.
			// Now fill content Right with all the requirements inside this.
			
			var newRowsPerPagePref = 0;
			var newRowsPerPagePrefObject = document.getElementById("newRowsPerPagePref");
			if (newRowsPerPagePrefObject != null) {
				newRowsPerPagePref= newRowsPerPagePrefObject.options[newRowsPerPagePrefObject.selectedIndex].value;
			}

			
			document.getElementById("contentCenterF").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
			url="/GloreeJava2/jsp/Folder/displayAllRequirementsInRealFolder.jsp?folderId=" + folderId; 
			url += "&sortBy=" +encodeURIComponent(sortBy) ;
			url += "&filterType=" +encodeURIComponent(filterType) ;
			url += "&filterValue=" +encodeURIComponent(filterValue) ;
			url += "&newRowsPerPagePref=" + newRowsPerPagePref;
			url += "&bustcache=" + new Date().getTime() ;
			
			


			xmlHttpOPCenterB =GetXmlHttpObject();		
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					document.getElementById('contentCenterF').style.display = 'block'; 
					document.getElementById('contentCenterF').innerHTML=xmlHttpOPCenterB.responseText;
					

					// lets display folder metrics
						// in contentCenterB, display the folder metrics data table and graph
					var folderMetricsDataTableURL = "/GloreeJava2/jsp/Report/FolderDashboard/displayFolderMetricsDataTable.jsp?folderId=" + folderId + "&chart=yes";

					document.getElementById("folderMetricsDataTableDiv").style.display= "block";
					document.getElementById("folderMetricsDataTableDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
					fillOPCenterGeneric(folderMetricsDataTableURL, "folderMetricsDataTableDiv");
									
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);					

			
			
			
			
			

		}
		else {
			// This is a virtual folder. has -1:reqtype id in folderId param.
			// Now fill content Right with all the requirements inside this.
			document.getElementById("contentCenterF").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
			url="/GloreeJava2/jsp/Folder/displayAllRequirementsInVirtualFolder.jsp?folderId=" + folderId;
			fillOPCenterGeneric(url, "contentCenterF");

		}	
	}
	
		
	
	// for a folder Id.
	// srt : I think this function can be deprecated
	function displayFolderContentRight(folderId){
		
		//
		//
		//
		// DEPRECATED . NOTE : IF YOU PLAN TO REMOVE THE FOLDER, SEARCH FOR ALL INSTANACES IN CODE
		// WHERE A CALL IS MADE TO THIS AND REMOVE THAT CALL ALSO.OTHER WISE ISSUES WITH HTML
		//
		//
		//
		
		// if folderId has -1 in it, then it's a virtual folders.
		// we need special handling for virtual folders
		
		// NOTE : Because Javascript is anal about strings and numbers
		// .IndexOf fails when pos is a number. So add "" to convert the number
		// to string.
		/*
		folderId = folderId + "";
		var pos	 = folderId.indexOf("-1");
		if (pos < 0){

			// This is a real folder.
			// Now fill content Right with all the requirements inside this.
			document.getElementById("contentRight").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			url="/GloreeJava2/jsp/Folder/displayAllRequirementsInRealFolder.jsp?folderId=" + folderId;
			url += "&bustcache=" + new Date().getTime() ;


			xmlHttpOPCenterB =GetXmlHttpObject();		
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					document.getElementById('contentRight').style.display = 'block'; 
					document.getElementById('contentRight').innerHTML=xmlHttpOPCenterB.responseText;
					

					// lets display folder metrics
						// in contentCenterB, display the folder metrics data table and graph
					var folderMetricsDataTableURL = "/GloreeJava2/jsp/Report/FolderDashboard/displayFolderMetricsDataTable.jsp?folderId=" + folderId + "&chart=yes";

					document.getElementById("folderMetricsDataTableDiv").style.display= "block";
					document.getElementById("folderMetricsDataTableDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
					
					fillOPCenterGeneric(folderMetricsDataTableURL, "folderMetricsDataTableDiv");
									
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);					
			

		}
		else {
			// This is a virtual folder. has -1:reqtype id in folderId param.
			// Now fill content Right with all the requirements inside this.
			document.getElementById("contentRight").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			url="/GloreeJava2/jsp/Folder/displayAllRequirementsInVirtualFolder.jsp?folderId=" + folderId;
			fillOPCenterGeneric(url, "contentRight");

		}
		*/
	}
	
	
	function displayAllUsersInRole(roleId){
		
		url="/GloreeJava2/jsp/Folder/displayAllUsersInRoleInContentCenterE.jsp?roleId=" + roleId;
		fillOPCenterGeneric(url, "userRoleDiv");
	}	
	// called when someone clicks on to 'Create Sub Folder' button in contentCenterA 
	function createFolderForm(folderId){
		// set the other content centers to empty.
		
		document.getElementById("contentCenterC").style.display = 'none';
		document.getElementById("contentCenterD").style.display = 'none';
		document.getElementById("contentCenterE").style.display = 'none';
		document.getElementById("contentCenterF").style.display = 'none';

		// set contentCenterB with the createFolderForm s.
		url = "/GloreeJava2/jsp/Folder/createFolderForm.jsp?folderId=" + folderId;
		url += "&bustcache=" + new Date().getTime() ;
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterB').style.display = 'block'; 
				document.getElementById('contentCenterB').innerHTML=xmlHttpOPCenterB.responseText;
				
				/*
				 * srt commenting this out, because this is dead code
				// since we were having difficulty , getting the menu tab to show up
				// we have decided to wait till content center B is populated before
				// we attempt to set the menu items.

				// lets implement the tabbed look & feel of menu items.
				var focusTab = "url(/GloreeJava2/images/focusTab.png)";
				var nonFocusTab = "url(/GloreeJava2/images/nonFocusTab.png)";
				// the clicked item is set to gray, and the rest are set to the yellow color.

				
				document.getElementById("menuBulkCreateRequirement").style.backgroundImage = nonFocusTab;
				document.getElementById("menuCreateRequirement").style.backgroundImage = nonFocusTab;
				document.getElementById("menuCreateListReport").style.backgroundImage = nonFocusTab;
				document.getElementById("menuCreateTraceTreeReport").style.backgroundImage = nonFocusTab;
				document.getElementById("menuEditFolder").style.backgroundImage = focusTab;
				document.getElementById("menuImportFromExcel").style.backgroundImage = nonFocusTab;
				document.getElementById("menuCreateWordTemplate").style.backgroundImage = nonFocusTab;
				*/
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
	}
			
	
	// called when someone clicks on to 'Delete Sub Folder' button in contentCenterA 
	function deleteFolderForm(folderId){
		
		// set the other content centers to empty.
		document.getElementById("contentCenterC").style.display = 'none';
		document.getElementById("contentCenterD").style.display = 'none';
		document.getElementById("contentCenterE").style.display = 'none';
		document.getElementById("contentCenterF").style.display = 'none';
			
		var url = "/GloreeJava2/jsp/Folder/deleteFolderForm.jsp?folderId=" + folderId;
		url += "&bustcache=" + new Date().getTime() ;
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterB').style.display = 'block'; 
				document.getElementById('contentCenterB').innerHTML=xmlHttpOPCenterB.responseText;
				
				/*
				 * srt commenting this out, because this is dead code
				// since we were having difficulty , getting the menu tab to show up
				// we have decided to wait till content center B is populated before
				// we attempt to set the menu items.
				
				// lets implement the tabbed look & feel of menu items.
				var focusTab = "url(/GloreeJava2/images/focusTab.png)";
				var nonFocusTab = "url(/GloreeJava2/images/nonFocusTab.png)";
				// the clicked item is set to gray, and the rest are set to the yellow color.
				document.getElementById("menuBulkCreateRequirement").style.backgroundImage = nonFocusTab;
				document.getElementById("menuCreateRequirement").style.backgroundImage = nonFocusTab;
				document.getElementById("menuCreateListReport").style.backgroundImage = nonFocusTab;
				document.getElementById("menuCreateTraceTreeReport").style.backgroundImage = nonFocusTab;
				document.getElementById("menuEditFolder").style.backgroundImage = focusTab;
				document.getElementById("menuImportFromExcel").style.backgroundImage = nonFocusTab;
				document.getElementById("menuCreateWordTemplate").style.backgroundImage = nonFocusTab;		
				*/
			}
		}

		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
	}

	// called when someone clicks on 'Delete Folder' button . 
	function deleteFolder(folderId){
			
			// set the other content centers to empty.
			
			document.getElementById("contentCenterA").style.display = "none";
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			
			var url="/GloreeJava2/servlet/FolderAction?action=deleteFolder&folderId=" + folderId;
			url += "&bustcache=" + new Date().getTime() ;
	
			xmlHttpOPCenterB =GetXmlHttpObject();
	
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;

					// After the folder is deleted, we need to remove the folder
					// object from the tree view
					var deletedFolderNode =  tree.getNodeByProperty('id',folderId);
                    delete folderNodeMap[deletedFolderNode.labelElId];
					tree.removeNode(deletedFolderNode);
					tree.draw();
				}
			}

			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);
		}


	function createFolderDuringReqCreation(folderName, folderDescription, projectId, parentFolderId){
		
	
		
		var url="/GloreeJava2/servlet/FolderAction";
		
		
		var params; 
		params = "action=createFolder&";
		params = params + "parentFolderId=" + parentFolderId + "&";
		params = params + "projectId=" + projectId+ "&";
		params = params + "folderName=" + encodeURIComponent(folderName) + "&";
		params = params + "folderDescription=" + encodeURIComponent(folderDescription) ;
		url = url + "?" + params;
		url += "&bustcache=" + new Date().getTime() ;

		
		// set the other content centers to empty.
		document.getElementById("contentCenterB").style.display = "none";;
		document.getElementById("contentCenterC").style.display = "none";;
		document.getElementById("contentCenterD").style.display = "none";;
		
			
		// Get the Ajax response from Folder Creation and populate the
		// contentCenterA.
		// Note : this call has to be Synchronous , as otherwise the folder may
		// not be created
		// by the time the second line executes.
		
		xmlHttpOPCenterA =GetXmlHttpObject();
			
		xmlHttpOPCenterA.onreadystatechange=function() {
			if(xmlHttpOPCenterA.readyState==4){
				// Now we update the Explorer tree.
				var contentResponse = xmlHttpOPCenterA.responseText;
				var contentResponseObject = eval ("(" + contentResponse + ")" );

				var folderId = contentResponseObject.folderId;
				var folderName = contentResponseObject.folderName;
				var folderDescription = contentResponseObject.folderDescription;
				var parentFolderId = contentResponseObject.parentFolderId;
				
				// get the parentNode , and we can add the child node to this.
				parentNode = tree.getNodeByProperty('id',parentFolderId);
				
				// Create the child node, add it to the parent, enter the values
				// in contextExlements so that
				// the contextMenu trigger can pick up the folder values and do
				// the same with folderNodeMap.
				var myNodeData = {  label: folderName,  title: folderDescription,  id:folderId }; 
				var tmpNode = new YAHOO.widget.TextNode(myNodeData, parentNode , true);
				tmpNode.labelStyle = 'icon-folder'; 
				contextElements.push(tmpNode.labelElId);
				folderNodeMap[tmpNode.labelElId] = folderId;

				// Now expand the parent node so that the newly created folder
				// is visible
				parentNode.expand();
				tree.draw();
				 
				displayFolderInExplorer(folderId) ;
				// now lets display the folder object.
				url="/GloreeJava2/jsp/Folder/displayRealFolder.jsp?folderId="+ folderId;			
				fillOPCenterGeneric(url, "contentCenterA");

				// lets display the folder Core.
				displayFolderCore(folderId);
				
				
			}
		}

		xmlHttpOPCenterA.open("GET",url,true);
		xmlHttpOPCenterA.send(null);
		
	}
	// called by the create Sub Folder button .
	function createFolder(projectId, parentFolderId, type){
		
		var folderName = document.getElementById("folderName").value;
		var folderDescription = document.getElementById("folderDescription").value;


		if (folderName.length == 0) {
			alert ("Please enter a Folder Name");
			document.getElementById("folderName").focus();
			document.getElementById("folderName").style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (folderDescription.length == 0) {
			alert ("Please enter a Folder Description");
			document.getElementById("folderDescription").focus();
			document.getElementById("folderDescription").style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (folderDescription.length > 1000){
			alert(" Please ensure that your Descriptoin is not longer than 1000 characters. Your current message is " + folderDescription.value.length + " characters long");
			document.getElementById("folderDescription").focus();
			document.getElementById("folderDescription").style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (type=='single'){
			// check to make sure that there are no # in the name
			if (folderName.includes('#')){
				alert ("You are trying to create a single folder. A Folder Name can not contan a #");
				document.getElementById("folderName").focus();
				document.getElementById("folderName").style.backgroundColor="#FFCC99";
				return(0);
			}
		}
		
		if (type=='multiple'){
			// check to make sure that there are #  in the name
			if (!(folderName.includes('#'))){
				alert ("You are trying to create multiple folders. Folder Names should be # comma separated list");
				document.getElementById("folderName").focus();
				document.getElementById("folderName").style.backgroundColor="#FFCC99";
				return(0);
			}
		}
		
		if (type=='multiple'){
			folderNamesArray = folderName.split('#');
			folderDescriptionArray = folderDescription.split('#');
			
			if (folderNamesArray.length != folderDescriptionArray.length ){
				alert ("You are trying to create multiple folders. However you have entered " + folderNamesArray.length + " folder names and " + folderDescriptionArray.length +  " folder descriptions . Please correct the mismatch " );
				document.getElementById("folderName").focus();
				document.getElementById("folderName").style.backgroundColor="#FFCC99";
				document.getElementById("folderDescription").focus();
				document.getElementById("folderDescription").style.backgroundColor="#FFCC99";
				return(0);
			}
		
		}
		
		if (type=='multiple'){
			
			folderNamesArray = folderName.split('#');
			var countOfFolders = folderNamesArray.length;
			if (confirm(" Do you want to create " + countOfFolders + " folders ? " )){
				alert("Thank you " + countOfFolders + " Will be created ");
			}
			else {
				alert(" Operation Cancelled ");
				return(0);
			}
		
		}
		
		var url="/GloreeJava2/servlet/FolderAction";
		
		
		var params; 
		params = "action=createFolder&";
		params = params + "type=" + type+ "&";
		params = params + "parentFolderId=" + parentFolderId + "&";
		params = params + "projectId=" + projectId+ "&";
		params = params + "folderName=" + encodeURIComponent(folderName) + "&";
		params = params + "folderDescription=" + encodeURIComponent(folderDescription) ;
		url = url + "?" + params;
		url += "&bustcache=" + new Date().getTime() ;

		
		// set the other content centers to empty.
		document.getElementById("contentCenterB").style.display = "none";;
		document.getElementById("contentCenterC").style.display = "none";;
		document.getElementById("contentCenterD").style.display = "none";;
		
			
		// Get the Ajax response from Folder Creation and populate the
		// contentCenterA.
		// Note : this call has to be Synchronous , as otherwise the folder may
		// not be created
		// by the time the second line executes.
		
		xmlHttpOPCenterA =GetXmlHttpObject();
			
		xmlHttpOPCenterA.onreadystatechange=function() {
			if(xmlHttpOPCenterA.readyState==4){
				if (type=='single'){
					// Now we update the Explorer tree.
					var contentResponse = xmlHttpOPCenterA.responseText;
					var contentResponseObject = eval ("(" + contentResponse + ")" );
	
					var folderId = contentResponseObject.folderId;
					var folderName = contentResponseObject.folderName;
					var folderDescription = contentResponseObject.folderDescription;
					var parentFolderId = contentResponseObject.parentFolderId;
					
					// get the parentNode , and we can add the child node to this.
					parentNode = tree.getNodeByProperty('id',parentFolderId);
					
					// Create the child node, add it to the parent, enter the values
					// in contextExlements so that
					// the contextMenu trigger can pick up the folder values and do
					// the same with folderNodeMap.
					var myNodeData = {  label: folderName,  title: folderDescription,  id:folderId }; 
					var tmpNode = new YAHOO.widget.TextNode(myNodeData, parentNode , true);
					tmpNode.labelStyle = 'icon-folder'; 
					contextElements.push(tmpNode.labelElId);
					folderNodeMap[tmpNode.labelElId] = folderId;
	
					// Now expand the parent node so that the newly created folder
					// is visible
					parentNode.expand();
					tree.draw();
					 
					displayFolderInExplorer(folderId) ;
					// now lets display the folder object.
					url="/GloreeJava2/jsp/Folder/displayRealFolder.jsp?folderId="+ folderId;			
					fillOPCenterGeneric(url, "contentCenterA");
	
					// lets display the folder Core.
					displayFolderCore(folderId);
				}
				else {
					document.getElementById("contentCenterA").innerHTML="<div class='alert alert-info'>Congratulations. We have created your folders. Please close and re-open your project to see your folders</div>";
				}
				
			}
		}

		xmlHttpOPCenterA.open("GET",url,true);
		xmlHttpOPCenterA.send(null);
		
	}


	// called when someone requests to open a folder in oPExplorer.jsp
	function editFolderForm(folderId){
				
		
		// set the other content centers to empty.
		document.getElementById("contentCenterB").style.display = 'none';
		document.getElementById("contentCenterC").style.display = 'none';
		document.getElementById("contentCenterD").style.display = 'none';
		document.getElementById("contentCenterE").style.display = 'none';
		document.getElementById("contentCenterF").style.display = 'none';

			
		var url="/GloreeJava2/jsp/Folder/editFolderForm.jsp?folderId="+ folderId;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterB').style.display = 'block'; 
				document.getElementById('contentCenterB').innerHTML=xmlHttpOPCenterB.responseText;
				
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
	}

	

	// called by the editFolderForms UpdateFolder button .
	function editFolder(){
		
		document.getElementById("contentCenterC").style.display = 'none';
		document.getElementById("contentCenterD").style.display = 'none';
		document.getElementById("contentCenterE").style.display = 'none';
		document.getElementById("contentCenterF").style.display = 'none';
		
		
		var folderId = document.getElementById("folderId");
		var folderName = document.getElementById("newFolderName");
		var folderDescription = document.getElementById("newFolderDescription");

		if (folderName.value.length == 0) {
			alert ("Please enter a Folder Name");
			folderName.focus();
			folderName.style.backgroundColor="#FFCC99";
			return(0);
			}
		if (folderDescription.value.length == 0) {
			alert ("Please enter a Folder Description");
			folderDescription.focus();
			folderDescription.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (folderDescription.value.length > 1000){
			alert(" Please ensure that your Descriptoin is not longer than 1000 characters. Your current message is " + folderDescription.value.length + " characters long");
			folderDescription.focus();
			folderDescription.style.backgroundColor="#FFCC99";
			return(0);
		}

		
		
		var url="/GloreeJava2/servlet/FolderAction";
		
		var params; 
		params = "action=editFolder&";
		
		params = params + "folderId=" + folderId.value + "&";
		params = params + "folderName=" + encodeURIComponent(folderName.value) + "&";
		params = params + "folderDescription=" + encodeURIComponent(folderDescription.value) ;

		url = url + "?" + params;
		url += "&bustcache=" + new Date().getTime() ;
		
		
		// Make the AJAX call to update the db and populate ContentCenterA with
		// new name / desc.
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterB').style.display = 'block'; 
				document.getElementById('contentCenterB').innerHTML=xmlHttpOPCenterB.responseText;
				
				// Now we need to update the tree with the new name and description.
				thisFolderNode = tree.getNodeByProperty('id',folderId.value);
				thisFolderNode.getLabelEl().innerHTML = folderName.value;

				// lets update the tabs and content right. we do this because we 
				// are displaying the folder name there and we want it to refresh.
				displayFolderContentCenterA(folderId.value);
				//displayFolderContentRight(folderId.value)
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
	}


	//////////////////////////////////////////////////////////////////////////
	// //
	// Requirement //
	// //
	// ////////////////////////////////////////////////////////////////////////
	// NOTE : this is a critical global var that is used by displayRequirement
	// JS
	// It's used to keep track of which req the user was last focused on.
	// and it's set to gray color , so that we can set the color of the last req
	// the user focused on.
	var currentFocusRequirementId = '';
	
	function displayRequirementDetails(requirementId, folderId){
		// lets enable the display div and put the working message.
		var rDDivName = "displayRDInFolderDiv" + requirementId;
		
		document.getElementById(rDDivName).style.display='block';
		document.getElementById(rDDivName).innerHTML = "<span class='normalText'> &nbsp;&nbsp;&nbsp;Working...</span>";
		

		
		
		var url="/GloreeJava2/jsp/Requirement/displayRequirementDetailsInFolderList.jsp?requirementId="+ requirementId;
		url += "&bustcache=" + new Date().getTime() ;

		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById(rDDivName).style.display = 'block'; 
				document.getElementById(rDDivName).innerHTML=xmlHttpOPCenterB.responseText;

				//handleRequirementComment(requirementId, folderId)

			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
		
		
		
	}	


	function hideRequirementDetails(requirementId){
		// lets enable the display div and put the working message.
		document.getElementById('requirementDetailsDiv' + requirementId).style.display='none';
		

		document.getElementById('showDetailsButton' + requirementId ).style.visibility='visible';
		document.getElementById('hideDetailsButt	on' + requirementId ).style.visibility='hidden';
			
		
		document.getElementById('displayRDInFolderDiv' + requirementId).style.display='none';
	}	



	// called when someone requests to open a requirement
	function displayFolderTab(requirementId){

		$('#objectTab').trigger('click');
        $("#folderContentDiv").show(1000);
	}	

	// called when someone requests to open a requirement
	function displayFolderContentsInExplorerInBackground(folderId, viewType){
        //document.getElementById("folderContentDiv").style.display='block';
        //$("#folderContentDiv").show(1000);
	
		var url="/GloreeJava2/jsp/Folder/displayRequirementsInExplorer.jsp?folderId=" + folderId; 
		url += "&viewType=" + viewType ;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "folderContentDiv");
	}

	
	// called when someone requests to open a requirement
	function displayFolderContentsInExplorer(folderId){

		$('#objectTab').trigger('click');
		
        //document.getElementById("folderContentDiv").style.display='block';
        $("#folderContentDiv").show(1000);
	
		var url="/GloreeJava2/jsp/Folder/displayRequirementsInExplorer.jsp?folderId=" + folderId; 
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "folderContentDiv");
	}		
	
	// called when someone requests to open a requirement
	function displayRequirement(requirementId,calledFrom){
		//autoAdjustContentRight();
		
		// get hold of the previous req on which the focus was on
		var oldRequirement = document.getElementById(currentFocusRequirementId);

		// if the previous req is not null, set it's background to gray.
		 if (oldRequirement != null){
			oldRequirement.style.backgroundColor = "#EBE4F2";
		 }
		 // set the background of the current focus req to blue
		 var newRequirement = document.getElementById(requirementId);
		 if (newRequirement != null) {
			 newRequirement.style.backgroundColor = "#666666";
		 }
		 // now set the global variable so that the current req becomes the next previous req.
		currentFocusRequirementId = requirementId;
		document.getElementById("contentCenterE").style.display = "none";

		// Now fill contentCenterB with the Requirement Core Info
		displayRequirementCore(requirementId, calledFrom);
				
		// Now we build the traceability URL and redirect OPCenterC to it.

		url="/GloreeJava2/jsp/Requirement/displayRequirementTrace.jsp?requirementId=" + requirementId;
		fillOPCenterGenericNoDivDisplay(url, "contentCenterC");
		
		// Now we build the attribute URL and redirect OPCenterD to it.
		url="/GloreeJava2/jsp/Requirement/displayRequirementAttributeValue.jsp?requirementId=" + requirementId;
		fillOPCenterGenericNoDivDisplay(url, "contentCenterD");
		
		// lets display the requirement comments
		url = "/GloreeJava2/jsp/Requirement/displayRequirementComments.jsp?requirementId=" + requirementId;
		fillOPCenterGenericNoDivDisplay(url, "contentCenterComments");
		
		// lets display the attachments
		url = "/GloreeJava2/jsp/Requirement/displayRequirementAttachments.jsp?requirementId=" + requirementId;
		fillOPCenterGenericNoDivDisplay(url, "contentCenterAttachments");
		
		
		
		document.getElementById("reqTabs").style.display = "block";
		document.getElementById("contentCenterB").style.display="block";
		document.getElementById("contentCenterD").style.display="none";
		document.getElementById("contentCenterC").style.display="none";
		document.getElementById("contentCenterComments").style.display="none";
		document.getElementById("contentCenterAttachments").style.display="none";

		document.getElementById("contentCenterE").style.display="none";
		document.getElementById("contentCenterF").style.display="none";
		document.getElementById("projectSearchDiv").style.display="none";
		
		
		
		$("#coreTab").addClass('active');
		$("#attributesTab").removeClass('active');
		$("#traceabilityTab").removeClass('active');
		$("#commentsTab").removeClass('active');
		$("#attachmentsTab").removeClass('active');
		$("#allInOneTab").removeClass('active');
		
	}	

	
	function hideChildrenInExplorer(parentTag){
		//$( '.descriptionDiv' ).style.display = "none";
		var x = document.getElementsByClassName("objectInExplorer");
		var i;
		var counter = 0;
		for (i = 0; i < x.length; i++) {
		    
		    var objectClassName = x[i].className;
		    var pos	 = objectClassName.indexOf(parentTag + ".");
			if (pos < 0){
				// do nothing
			}
			else {
				 x[i].style.display = "none";
				 counter++;
			}
		}
		if (counter == 0 ){
			alert("There are no children for " + parentTag + " to hide")
		}
	}
	function showChildrenInExplorer(parentTag){
		//$( '.descriptionDiv' ).style.display = "none";
		var x = document.getElementsByClassName("objectInExplorer");
		var i;
		var counter = 0;
		for (i = 0; i < x.length; i++) {
		    
		    var objectClassName = x[i].className;
		    var pos	 = objectClassName.indexOf(parentTag + ".");
			if (pos < 0){
				// do nothing
			}
			else {
				 x[i].style.display = "block";
				 counter++;
			}
		}
		if (counter == 0 ){
			alert("There are no children for " + parentTag + " to show")
		}
	}
	function hideDescriptionInTraceMap(){
		//$( '.descriptionDiv' ).style.display = "none";

		document.getElementById("hideDescriptionBtn").style.display="none";
		document.getElementById("showDescriptionBtn").style.display="block";
		var x = document.getElementsByClassName("descriptionDiv");
		var i;
		for (i = 0; i < x.length; i++) {
		    x[i].style.display = "none";
		}
	}
	
	
	function showDescriptionInTraceMap(){

		document.getElementById("hideDescriptionBtn").style.display="block";
		document.getElementById("showDescriptionBtn").style.display="none";
		//$( '.descriptionDiv' ).style.display = "none";
		var x = document.getElementsByClassName("descriptionDiv");
		var i;
		for (i = 0; i < x.length; i++) {
		    x[i].style.display = "block";
		}
	}
	
	function hideAttributesInTraceMap(){
		//$( '.descriptionDiv' ).style.display = "none";

		document.getElementById("hideAttributesBtn").style.display="none";
		document.getElementById("showAttributesBtn").style.display="block";
		var x = document.getElementsByClassName("attributesDiv");
		var i;
		for (i = 0; i < x.length; i++) {
		    x[i].style.display = "none";
		}
	}
	
	
	function showAttributesInTraceMap(){

		document.getElementById("hideAttributesBtn").style.display="block";
		document.getElementById("showAttributesBtn").style.display="none";
		//$( '.descriptionDiv' ).style.display = "none";
		var x = document.getElementsByClassName("attributesDiv");
		var i;
		for (i = 0; i < x.length; i++) {
		    x[i].style.display = "block";
		}
	}	
	
	function hideCommentsInTraceMap(){
		//$( '.descriptionDiv' ).style.display = "none";

		document.getElementById("hideCommentsBtn").style.display="none";
		document.getElementById("showCommentsBtn").style.display="block";
		var x = document.getElementsByClassName("commentsDiv");
		var i;
		for (i = 0; i < x.length; i++) {
		    x[i].style.display = "none";
		}
	}
	
	
	function showCommentsInTraceMap(){

		document.getElementById("hideCommentsBtn").style.display="block";
		document.getElementById("showCommentsBtn").style.display="none";
		//$( '.descriptionDiv' ).style.display = "none";
		var x = document.getElementsByClassName("commentsDiv");
		var i;
		for (i = 0; i < x.length; i++) {
		    x[i].style.display = "block";
		}
	}		
	
	function focusOnObjectInExplorer(requirementId){
		
		try {
			
			// for every objet in class 'objectInExplorer' , set class to 'alert alert-info objectInExplorer'
			// $( '.objectInExplorer' ).attr('class', 'alert alert-info objectInExplorer');
			 
			 // for object whose id is #objectInExplorerReqId , set it to 'alert alert-warning objectInExplorer'
			 //$('#objectInExplorer' + requirementId).attr('class', 'alert alert-warning objectInExplorer');
			
			 
			 // change to make : 
			 
			 // for every object in class, remove class alert-warning. add class alert-info
			 $( '.objectInExplorer' ).removeClass('alert-warning');
			 $( '.objectInExplorer' ).addClass('alert-info');
			 
			 
			 // fo revery object whose id is objectInExplorerReqId , remove alert-info , add alert-warning
			 $('#objectInExplorer' + requirementId).removeClass('alert-info');
			 $('#objectInExplorer' + requirementId).addClass('alert-warning');
					
					var x = document.getElementById('objectInExplorer' + requirementId);
					var offset = x.offsetTop - 200 ;


					

					
					$("#folderContentDiv").animate({
						  scrollTop: offset
						}, 1000);
					

			
		}
		catch (e){
			console.log (e.message);
		}
	}
	// gets the id of the next requirement in the folder and calls 
	function displayNextRequirementInFolder(folderId , requirementId){
		if (document.getElementById("createTracesDiv") != null){
			document.getElementById("createTracesDiv").style.display="none";
		}
		if (document.getElementById('attributeInfo') != null){
			document.getElementById('attributeInfo').style.display='block';
		}
		
		var url="/GloreeJava2/servlet/RequirementAction?action=getNextRequirementInFolder&requirementId=" + requirementId ;
		url += "&folderId=" + folderId;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				var nextRequirementId = xmlHttpOPCenterB.responseText;
				// now that we have the nextRequirementId, lets display it. 
				displayRequirement(nextRequirementId,"");

			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
	

	// gets the id of the previous requirement in the folder and calls 
	function displayPreviousRequirementInFolder(folderId , requirementId){
		try{
			if (document.getElementById("createTracesDiv") != null){
				document.getElementById("createTracesDiv").style.display="none";
			}
			if (document.getElementById('attributeInfo') != null){
				document.getElementById('attributeInfo').style.display='block';
			}
			
			var url="/GloreeJava2/servlet/RequirementAction?action=getPreviousRequirementInFolder&requirementId=" + requirementId ;
			url += "&folderId=" + folderId;
			url += "&bustcache=" + new Date().getTime() ;
			
			xmlHttpOPCenterB =GetXmlHttpObject();		
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					var previousRequirementId = xmlHttpOPCenterB.responseText;
					// now that we have the previousRequirementId, lets display it. 
					displayRequirement(previousRequirementId,"");
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);
		}
		catch(err){
			console.log("found err " + err.message + " at stack " + err.stack);
		}
		
	}

	// srt : this can be deprecated
	// lets figure out if content right is correct width and adjust
	function autoAdjustContentRight(){
		// this adjusts the contentRigth size based on the window size.
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
		var currentContentRightSize = layout.getUnitByPosition('right').get('width');
		if (currentContentRightSize != contentRightSize) {
			currentContentRightSize = contentRightSize;
			layout.getUnitByPosition('right').set('width', contentRightSize);
		}
		

	}
	
	function displayRequirement(requirementId,calledFrom, calledFromFolderId){
		//autoAdjustContentRight();
		 
		// get hold of the previous req on which the focus was on
		var oldRequirement = document.getElementById(currentFocusRequirementId);

		// if the previous req is not null, set it's background to gray.
		 if (oldRequirement != null){
			oldRequirement.style.backgroundColor = "#EBE4F2";
		 }
		 // set the background of the current focus req to blue
		 var newRequirement = document.getElementById(requirementId);
		 if (newRequirement != null) {
			 newRequirement.style.backgroundColor = "#666666";
		 }
		 // now set the global variable so that the current req becomes the next previous req.
		currentFocusRequirementId = requirementId;
		document.getElementById("contentCenterE").style.display = "none";

		// Now fill contentCenterB with the Requirement Core Info
		displayRequirementCore(requirementId, calledFrom, calledFromFolderId);
				
		// Now we build the traceability URL and redirect OPCenterC to it.

		url="/GloreeJava2/jsp/Requirement/displayRequirementTrace.jsp?requirementId=" + requirementId;
		fillOPCenterGenericNoDivDisplay(url, "contentCenterC");
		
		// Now we build the attribute URL and redirect OPCenterD to it.
		url="/GloreeJava2/jsp/Requirement/displayRequirementAttributeValue.jsp?requirementId=" + requirementId;
		fillOPCenterGenericNoDivDisplay(url, "contentCenterD");

		// lets display the requirement comments
		url = "/GloreeJava2/jsp/Requirement/displayRequirementComments.jsp?requirementId=" + requirementId;
		fillOPCenterGenericNoDivDisplay(url, "contentCenterComments");

		// lets display the attachments
		url = "/GloreeJava2/jsp/Requirement/displayRequirementAttachments.jsp?requirementId=" + requirementId;
		fillOPCenterGenericNoDivDisplay(url, "contentCenterAttachments");
		
		
		document.getElementById("reqTabs").style.display = "block";
		document.getElementById("contentCenterB").style.display="block";
		document.getElementById("contentCenterD").style.display="none";
		document.getElementById("contentCenterC").style.display="none";
		document.getElementById("contentCenterComments").style.display="none";
		document.getElementById("contentCenterAttachments").style.display="none";
		
		document.getElementById("contentCenterE").style.display="none";
		document.getElementById("contentCenterF").style.display="none";
		document.getElementById("projectSearchDiv").style.display="none";
		
		$("#coreTab").addClass('active');
		$("#attributesTab").removeClass('active');
		$("#traceabilityTab").removeClass('active');
		$("#commentsTab").removeClass('active');
		$("#attachmentsTab").removeClass('active');
		$("#allInOneTab").removeClass('active');
		
		
		
		
	}
	
	
	
	
	function displayUpDown(requirementId, path, targetDiv){

		// lets display the LookUpAndDown
		// Now we build the attribute URL and redirect OPCenterD to it.
		var url="/GloreeJava2/jsp/Requirement/LookUpDown/displayRequirementUpDown.jsp?requirementId=" + requirementId;
		url+= "&path=" + path; 
		fillOPCenterGeneric(url, targetDiv);
		
		
	}
	
	
	
	function displayPrintableRequirement(requirementId, folderId, isGlossary) {
		

		
		
		

			// display the comments
			url = "/GloreeJava2/jsp/Requirement/displayRequirementComments.jsp?requirementId=" + requirementId;
			url += "&folderId=" + folderId;
			url += "&printable=yes"
			url += "&bustcache=" + new Date().getTime() ;
			document.getElementById("requirementCommentsDiv").innerHTML = "&nbsp;&nbsp;&nbsp;Working...";
			fillOPCenterGeneric(url, "requirementCommentsDiv");
	
			
			// display CIA
			var url="/GloreeJava2/jsp/Requirement/displayChangeImpactAnalysis.jsp?";
			url += "&printable=yes"
			url += "&bustcache=" + new Date().getTime() ;
			url += "&requirementId=" + requirementId ;
			document.getElementById("requirementCIADiv").innerHTML = "&nbsp;&nbsp;&nbsp;Working...";
			fillOPCenterGeneric(url, "requirementCIADiv");	
	
			// display version history
			var url = "/GloreeJava2/jsp/Requirement/displayRequirementVersionHistory.jsp?requirementId=" + requirementId;
			url += "&printable=yes"
			url += "&bustcache=" + new Date().getTime() ;
			document.getElementById("requirementVersionHistoryDiv").innerHTML = "&nbsp;&nbsp;&nbsp;Working...";
			fillOPCenterGeneric(url, "requirementVersionHistoryDiv");
	}
	
	function handleReturnToTracer(){
		document.getElementById("returnDiv").innerHTML= "";
		document.getElementById("returnDiv").style.display='none';
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "block";
		
		//layout.getUnitByPosition("left").collapse();
	}
	
	function handleReturnToTraceMatrix(){
		document.getElementById("returnDiv").innerHTML= "";
		document.getElementById("returnDiv").style.display='none';
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "block";
		

		//layout.getUnitByPosition("left").collapse();
	}
	
	function handleReturnToListFolderContents(){
		document.getElementById("returnDiv").innerHTML= "";
		document.getElementById("returnDiv").style.display='none';
		document.getElementById("contentCenterA").style.display = "block";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "block";
	}
	

	
	function handleReturnToUserDashboardRequirements(){
		document.getElementById("returnDiv").innerHTML= "";
		document.getElementById("returnDiv").style.display='none';
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "block";
		document.getElementById("contentCenterF").style.display = "block";
		
	}	
	
	function handleReturnToReportAndTraceTree(calledFromFolderId){
		document.getElementById("returnDiv").innerHTML= "";
		document.getElementById("returnDiv").style.display='none';
		
		
		document.getElementById("contentCenterA").style.display = "block";
		displayFolderContentCenterA(calledFromFolderId);
		displayFolderInExplorer(calledFromFolderId);
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "block";
		document.getElementById("contentCenterF").style.display = "block";
		
	}		

	
	function handleReturnToMyTasks(){
		document.getElementById("returnDiv").innerHTML= "";
		document.getElementById("returnDiv").style.display='none';
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "block";
	}
	
	
	function handleCalledFrom(calledFrom,calledFromFolderId){
		if (calledFrom =='Tracer') {
			document.getElementById("returnDiv").style.display='block';
			document.getElementById("returnDiv").innerHTML=' <a href="#" class="btn btn-sm btn-primary" style="color:white" onClick="handleReturnToTracer();"> Return to ' + calledFrom + ' </a>';
		}
		
		if (calledFrom =='Trace Matrix') {
			document.getElementById("returnDiv").style.display='block';
			document.getElementById("returnDiv").innerHTML=' <a href="#"  class="btn btn-sm btn-primary" style="color:white" onClick="handleReturnToTraceMatrix();"> Return to ' + calledFrom + ' </a>';
		}
		
		//if (calledFrom =='List Folder Contents') {
		//	document.getElementById("returnDiv").style.display='block';
		//	document.getElementById("returnDiv").innerHTML=' <a href="#"  class="btn btn-sm btn-primary" style="color:white" onClick="handleReturnToListFolderContents();"> Return to Folder </a>';
		//}
		
	
		
		if (calledFrom =='User Dashboard Requirements') {
			document.getElementById("returnDiv").style.display='block';
			document.getElementById("returnDiv").innerHTML=' <a href="#"  class="btn btn-sm btn-primary" style="color:white" onClick="handleReturnToUserDashboardRequirements();"> Return to ' + calledFrom + ' </a>';
		}
		
		if (
				(calledFrom == 'Report and Bulk Edit')
				||
				(calledFrom == 'Trace Tree')
			) {
			document.getElementById("returnDiv").style.display='block';
			document.getElementById("returnDiv").innerHTML=' <a href="#"  class="btn btn-sm btn-primary" style="color:white" onClick="handleReturnToReportAndTraceTree('+ calledFromFolderId +');"> Return to ' + calledFrom + ' </a>';
		}
		
	
		if (calledFrom =='My Tasks') {
			document.getElementById("returnDiv").style.display='block';
			document.getElementById("returnDiv").innerHTML=' <a href="#"   class="btn btn-sm btn-primary" style="color:white" onClick="handleReturnToMyTasks();"> Return to ' + calledFrom + ' </a>';
		}
		
	}
	
	// Lets log the view event of this requirement by this user.
	
	function logViewEvent(requirementId) {
		var url="/GloreeJava2/servlet/RequirementAction?action=logViewEvent&requirementId=" + requirementId ;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			//do nothing.
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}

	
	
	// any time we display REquirement core, we want to put focus
	// on some text object, other IE screws up. since we use this code
	// in many places, making a sepearte method out of it.
	function displayRequirementCore(requirementId, calledFrom) {
		// Now fill contentCenterB with the Requirement Core Info
		
		
		
		var url="/GloreeJava2/jsp/Requirement/displayRequirementCore.jsp?requirementId="+ requirementId;
		if (calledFrom != null){
			url += "&calledFrom=" + encodeURIComponent(calledFrom) ;
		}
		url += "&bustcache=" + new Date().getTime() ;
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterB').style.display = 'block'; 
				document.getElementById('contentCenterB').innerHTML=xmlHttpOPCenterB.responseText;

				handleCalledFrom(calledFrom);
				
				// once contentB is loaded, lets open the rich text editor page if the browser is not IE
				var isMSIE = isBrowserMSIE();
				if (!isMSIE){
					editRequirementDescriptionRichText();
				}
				
				//window.location.hash="TopOfContentCenterA";
				//$("#ContentCenter").animate({ scrollTop: 0}, 1000);
				document.getElementById("contentCenter").scrollTop = 0;
				
				document.getElementById("requirementName").focus();
				
				// Lets log the view event of this requirement by this user.
				logViewEvent(requirementId);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);

	}
/*
	function displayRequirementCoreForLargerRTE(requirementId, newRTEHeight) {
		// Now fill contentCenterB with the Requirement Core Info
		
		var url="/GloreeJava2/jsp/Requirement/displayRequirementCore.jsp?requirementId="+ requirementId;
		url += "&newRTEHeight=" + newRTEHeight;
		url += "&bustcache=" + new Date().getTime(); 
		
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				
				document.getElementById('contentCenterB').style.display = 'block'; 
				document.getElementById('contentCenterB').innerHTML=xmlHttpOPCenterB.responseText;


				// once contentB is loaded, lets open the rich text editor page if the browser is not IE
				var isMSIE = isBrowserMSIE();
				if (!isMSIE){
					editRequirementDescriptionRichText();
				}
				
				//window.location.hash="TopOfContentCenterA";
				//$("#ContentCenter").animate({ scrollTop: 0}, 1000);
				document.getElementById("contentCenter").scrollTop = 0;
				
				
				document.getElementById("requirementName").focus();
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);


	}
*/
	
	
	// any time we display REquirement core, we want to put focus
	// on some text object, other IE screws up. since we use this code
	// in many places, making a sepearte method out of it.
	// this is an enhancement for the displayRequirementCore, it takes an additional parameter called calledFromFolderId.
	function displayRequirementCore(requirementId, calledFrom, calledFromFolderId) {
		// Now fill contentCenterB with the Requirement Core Info

		var url="/GloreeJava2/jsp/Requirement/displayRequirementCore.jsp?requirementId="+ requirementId;
		if (calledFrom != null){
			url += "&calledFrom=" + encodeURIComponent(calledFrom) ;
		}
		url += "&calledFromFolderId=" + encodeURIComponent(calledFromFolderId) ;
		url += "&bustcache=" + new Date().getTime() ;
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterB').style.display = 'block'; 
				document.getElementById('contentCenterB').innerHTML=xmlHttpOPCenterB.responseText;
				
				handleCalledFrom(calledFrom,calledFromFolderId);
				
				// once contentB is loaded, lets open the rich text editor page
				// do this only if its not IE
				// once contentB is loaded, lets open the rich text editor page if the browser is not IE
				var isMSIE = isBrowserMSIE();
				if (!isMSIE){
					editRequirementDescriptionRichText();
				}
				
				window.location.hash="TopOfContentCenterA";
				window.location.hash="TopOfPage";

				// we need to put focus somewhere , so we have decided to put 
				// focus on the searchstring box.
				// this takes care of the situation where IE tends to lose the focus object.

				document.getElementById("requirementName").focus();
				
				// Lets log the view event of this requirement by this user.
				logViewEvent(requirementId);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);

	}	
	
	function  handleRequirementSearchkeyPress(event, searchType, searchProjectId, callingRequirementId, source) {
		if (source==null){source="Requirement";}
		var keyCode = event.keyCode;
		if (keyCode == 13) {
			RequirementSearch(searchType,searchProjectId, callingRequirementId, source);
		}
	}
	
	function  handleRequirementSearchQuickkeyPress(event, searchType, searchProjectId, callingRequirementId) {
		var keyCode = event.keyCode;
		if (keyCode == 13) {
			RequirementSearchQuick(searchType,searchProjectId, callingRequirementId);
		}
	}
	// Called when someone tries to search within the Requirement Search box.
	function RequirementSearch(searchType, searchProjectId, callingRequirementId, source){
		if (source==null){
			source="Requirement";
		}
		var searchString ;
		
		var folderId = 0;
		if (searchType == 'google') {
			searchString = document.getElementById("reqTraceSearchString").value;
			
		}
		if (searchType == 'reqId') {
			searchString = document.getElementById("reqTraceId").value;
			
		}
		if (searchType == 'folderId') {
			var searchFolderObject = document.getElementById("searchFolder");
			folderId = searchFolderObject[searchFolderObject.selectedIndex].value;
			if (folderId == 0){
				return(0);
			}
		}

		var searchRequirementTypeIdObject = document.getElementById("searchRequirementTypeId");
		var searchRequirementTypeIdValue = "";
		try {
			searchRequirementTypeIdValue = searchRequirementTypeIdObject[searchRequirementTypeIdObject.selectedIndex].value;
		}
		catch (e) {
			// do nothing
		}
		
		
		

		
		document.getElementById("searchFormResultsDiv").style.display = "block";
		document.getElementById("searchFormResultsDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		 
		// Now display the search results.
		url = "/GloreeJava2/jsp/Requirement/displayRequirementSearchResults.jsp?searchType=";
		url += searchType +  "&searchString=" + searchString;
		url += "&folderId=" + folderId;
		url += "&source=" + source;
		url +=   "&callingRequirementId=" + callingRequirementId;
		url +=   "&searchProjectId=" + searchProjectId;
		url +=   "&searchRequirementTypeId=" + searchRequirementTypeIdValue;
		url += "&bustcache=" + new Date().getTime() ;
		
		
		contentArea = 'searchFormResultsDiv';
		fillOPCenterGeneric(url,contentArea);

	}

	function RequirementSearchQuick(searchType, searchProjectId, callingRequirementId){
		var searchString ;
		
		var folderId = 0;
		
		searchString = document.getElementById("reqTraceIdQuick").value;
			
		var searchRequirementTypeIdValue = "";
		

		
		document.getElementById("searchFormResultsQuickDiv").style.display = "block";
		document.getElementById("searchFormResultsQuickDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		 
		// Now display the search results.
		url = "/GloreeJava2/jsp/Requirement/displayRequirementSearchResultsQuick.jsp?searchType=";
		url += searchType +  "&searchString=" + searchString;
		url += "&folderId=" + folderId;
		url +=   "&callingRequirementId=" + callingRequirementId;
		url +=   "&searchProjectId=" + searchProjectId;
		url +=   "&searchRequirementTypeId=" + searchRequirementTypeIdValue;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url,'searchFormResultsQuickDiv');

	}

	// this is called when a user clicks on a requirement link in the Requirement Search box and 
	// is used to set the 'TraceTo' or 'TraceFrom' box values.
	function setCallingRequirementTraceValue(sourceBox, selectedFullTag){
		if (sourceBox == "traceTo"){
			var traceToBoxOriginalValue = window.opener.document.getElementById("traceTo").value;
			if (traceToBoxOriginalValue.length > 0 ){
				window.opener.document.getElementById("traceTo").value =   traceToBoxOriginalValue + "," + selectedFullTag; 
			}
			else {
				window.opener.document.getElementById("traceTo").value =   selectedFullTag; 
			}
		}
		else {
			var traceFromBoxOriginalValue = window.opener.document.getElementById("traceFrom").value;
			if (traceFromBoxOriginalValue.length > 0 ){
				window.opener.document.getElementById("traceFrom").value = traceFromBoxOriginalValue  + "," + selectedFullTag; 
			}
			else {
				window.opener.document.getElementById("traceFrom").value =   selectedFullTag; 
			}
		}
		
		window.close();
		
		
	}

	
	// displays the change impact analysis for this requirement.
	function displayChangeImpactAnalysis(requirementId){
		var url="/GloreeJava2/jsp/Requirement/displayChangeImpactAnalysis.jsp?";
		url += "&requirementId=" + requirementId ;
		fillOPCenterGeneric(url, "requirementPromptDiv");	
	}
	
	function displayChangeImpactAnalysisAllRequirementsInFolder(rDDivName, requirementId){

		document.getElementById(rDDivName).style.display = 'block';
		document.getElementById(rDDivName).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		
		
		var url="/GloreeJava2/jsp/Requirement/displayChangeImpactAnalysis.jsp?";
		url += "&requirementId=" + requirementId ;
		url += "&folderView=yes";
		url += "&rDDivName=" + rDDivName;
		fillOPCenterGeneric(url, rDDivName);	
	}
	
		
	function displayChangeImpactAnalysisFlex(upStreamDepth, downStreamDepth, requirementId){
		var url="/GloreeJava2/jsp/Requirement/displayChangeImpactAnalysis.jsp?";
		url+= "&upStreamDepth=" + upStreamDepth;
		url+= "&downStreamDepth=" + downStreamDepth;
		url += "&requirementId=" + requirementId ;
		fillOPCenterGeneric(url, "requirementPromptDiv");	
	}
	
	function displayChangeImpactAnalysisFlexAllRequirementsInFolder(upStreamDepth, downStreamDepth,rDDivName, requirementId){
		var url="/GloreeJava2/jsp/Requirement/displayChangeImpactAnalysis.jsp?";
		url+= "&upStreamDepth=" + upStreamDepth;
		url+= "&downStreamDepth=" + downStreamDepth;
		url += "&requirementId=" + requirementId ;
		url += "&folderView=yes";
		url += "&rDDivName=" + rDDivName;
		
		fillOPCenterGeneric(url, rDDivName);	
	}
	
	
	// displays the change impact analysis for this requirement.
	function displayParentingForm(requirementId, folderId){
		var url="/GloreeJava2/jsp/Requirement/displayParentingForm.jsp?";
		url += "&requirementId=" + requirementId ;
		url += "&folderId=" + folderId ;

		document.getElementById("requirementPromptDiv").style.display = "block";
		document.getElementById("requirementPromptDiv").innerHTML = "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "requirementPromptDiv");	
	}


	function displayCreateChildForm(folderId, parentFullTag){
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";

		
		var url = "/GloreeJava2/jsp/Requirement/createRequirementForm.jsp?folderId=" + folderId;
		url += "&parentFullTag=" + parentFullTag ;
		url += "&bustcache=" + new Date().getTime() ;
		
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterB').style.display = 'block'; 
				document.getElementById('contentCenterB').innerHTML=xmlHttpOPCenterB.responseText;
				
				
				// after we load the form, lets activate the Rich Text Editor
				//createRequirementFormRichText();
				var isMSIE = isBrowserMSIE();
				if (!isMSIE){
					editRequirementDescriptionRichText();
				}
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
	}

	function makeRequirementIndependent(folderId, requirementId){
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";

		var childrensFutureObject = document.getElementById("childrensFutureForMakeIndependent");

		var url="/GloreeJava2/servlet/RequirementAction?action=makeRequirementIndependent";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&folderId=" + folderId ;
		url += "&requirementId=" + requirementId ;
		if (childrensFutureObject == null){
			url += "&childrensFuture=" + encodeURIComponent(document.getElementById("childrensFutureForMakeIndependentHidden").value) + "&";
		}
		else {
			url += "&childrensFuture=" + encodeURIComponent(childrensFutureObject[childrensFutureObject.selectedIndex].value) + "&";
		}
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// since we have renumbered the req, lets refresh all the content centers.
				var contentResponse = xmlHttpOPCenterB.responseText;
				var contentResponseObject = eval ("(" + contentResponse + ")" );
				var requirementId = contentResponseObject.requirementId;

				// Now fill contentCenterB with the Requirement Core Info
				displayRequirementCore(requirementId);
				
				
				// Now we build the traceability URL and redirect OPCenterC to
				// it.
				url="/GloreeJava2/jsp/Requirement/displayRequirementTrace.jsp?requirementId=" + requirementId;
				fillOPCenterGeneric(url, "contentCenterC");
				
				// Now we build the attribute URL and redirect OPCenterD to it.
				url="/GloreeJava2/jsp/Requirement/displayRequirementAttributeValue.jsp?requirementId=" + requirementId;
				fillOPCenterGeneric(url, "contentCenterD");
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);

	}

	function makeAllChildrenIndependent(folderId, requirementId){
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";

		var url="/GloreeJava2/servlet/RequirementAction?action=makeAllChildrenIndependent";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&folderId=" + folderId ;
		url += "&requirementId=" + requirementId ;
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){

				// Now fill contentCenterB with the Requirement Core Info
				displayRequirementCore(requirementId);
				
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);

	}
	
	function changeParent(folderId, requirementId){
	
		document.getElementById("changeParentButton").disabled = true;
		
		var childrensFutureObject = document.getElementById("childrensFutureForChangeParent");
		var parentFullTagObject = document.getElementById("parentFullTag");

		var url="/GloreeJava2/servlet/RequirementAction?action=changeParent";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&folderId=" + folderId ;
		url += "&requirementId=" + requirementId ;
		if (childrensFutureObject == null){
			url += "&childrensFuture=" + encodeURIComponent(document.getElementById("childrensFutureForChangeParentHidden").value) + "&";
		}
		else {
			url += "&childrensFuture=" + encodeURIComponent(childrensFutureObject[childrensFutureObject.selectedIndex].value) + "&";
		}
		url += "&parentFullTag=" + encodeURIComponent(parentFullTagObject.value) ;

		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// we get the requirement id as part of JSON response.
				var contentResponse = xmlHttpOPCenterB.responseText;
				// lets see if we got an error in response to creating the requirement.
				var pos= contentResponse.indexOf("Error : ");
				if (pos > 0){
					// means we got an error. lets display the error message.
					document.getElementById("requirementCreationErrorDiv").style.display='block';
					document.getElementById("requirementCreationErrorDiv").innerHTML= contentResponse;
					document.getElementById("changeParentButton").disabled=false;
				}
				else {
					var contentResponseObject = eval ("(" + contentResponse + ")" );
					var requirementId = contentResponseObject.requirementId;
	
					// Now fill contentCenterB with the Requirement Core Info
					displayRequirementCore(requirementId);
					
					
					
					// Now we build the traceability URL and redirect OPCenterC to
					// it.
					url="/GloreeJava2/jsp/Requirement/displayRequirementTrace.jsp?requirementId=" + requirementId;
					fillOPCenterGeneric(url, "contentCenterC");
					
					// Now we build the attribute URL and redirect OPCenterD to it.
					url="/GloreeJava2/jsp/Requirement/displayRequirementAttributeValue.jsp?requirementId=" + requirementId;
					fillOPCenterGeneric(url, "contentCenterD");
					
					// lets also refresh the requirement in explorer
					url ="/GloreeJava2/jsp/Folder/displayARequirementInExplorer.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					
					fillOPCenterGeneric(url, "objectInExplorer" + requirementId);
					
				}
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);

	}

	// displays the change impact analysis for this requirement.
	function displayLockUnlockForm(requirementId, folderId){
		var url="/GloreeJava2/jsp/Requirement/displayLockUnlockForm.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&requirementId=" + requirementId ;
		url += "&folderId=" + folderId ;

		document.getElementById("requirementPromptDiv").style.display = "block";
		document.getElementById("requirementPromptDiv").innerHTML = "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "requirementPromptDiv");	
	}
	
	function displayDynamicApprovers(requirementId, divId){
		var url="/GloreeJava2/jsp/Requirement/displayDynamicApprovers.jsp" +
			"?requirementId="+ requirementId + 
			"&divId=" + divId;
		url += "&bustcache=" + new Date().getTime() ;
		fillOPCenterGeneric(url, divId);
		
	}
	
	function displayRequirementDescription(requirementId, divId, currentSprintId){
		var url="/GloreeJava2/jsp/Requirement/displayRequirementDescription.jsp" +
			"?requirementId="+ requirementId + 
			"&divId=" + divId;
		url += "&bustcache=" + new Date().getTime() ;
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById(divId).style.display = "block";
				document.getElementById(divId).innerHTML=xmlHttpOPCenterB.responseText;			
				
				// lets show the CIA in the displayCIADivRequirementId div to a depth  of 1
				var ciaUrl="/GloreeJava2/jsp/Requirement/displayCIAInRequirementDescription.jsp?";
				ciaUrl += "&bustcache=" + new Date().getTime() ;
				ciaUrl += "&requirementId=" + requirementId ;
				ciaUrl += "&currentSprintId=" + currentSprintId ;
				
				var displayCIADiv = 'displayCIADiv' + requirementId;
				fillOPCenterGeneric(ciaUrl,displayCIADiv);
				
				// lets log the viewing of this requirement
				logViewEvent(requirementId);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}	
	
	
	function displayEditRequirementModal(requirementId){
		
		var divId = "modalBody" ;
		var url="/GloreeJava2/jsp/Requirement/displayEditRequirementModal.jsp" +
			"?requirementId="+ requirementId + 
			"&divId=" + divId;
		url += "&bustcache=" + new Date().getTime() ;
		fillOPCenterGeneric(url,divId);
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById(divId).style.display = "block";
				document.getElementById(divId).innerHTML=xmlHttpOPCenterB.responseText;		
				var isMSIE = isBrowserMSIE();
				if (!isMSIE){
					editRequirementDescriptionRichText();
				}
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}	
	
	
	// this is used in AgileScrum screens and is used to display only name and description.
	function displayRequirementDescriptionSubSet(requirementId, divId, hideString ){
		var url="/GloreeJava2/jsp/Requirement/displayRequirementDescription.jsp" +
			"?requirementId="+ requirementId + 
			"&divId=" + divId + "&hideString=" + encodeURIComponent(hideString);
		url += "&bustcache=" + new Date().getTime() ;
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById(divId).style.display = "block";
				document.getElementById(divId).innerHTML=xmlHttpOPCenterB.responseText;		
				
				// lets show the CIA in the displayCIADivRequirementId div to a depth  of 1
				var ciaUrl="/GloreeJava2/jsp/Requirement/displayCIAInRequirementDescription.jsp?";
				ciaUrl += "&bustcache=" + new Date().getTime() ;
				ciaUrl += "&requirementId=" + requirementId ;
				var displayCIADiv = 'displayCIADiv' + requirementId;
				fillOPCenterGeneric(ciaUrl,displayCIADiv);
				
				
				// lets log the viewing of this requirement
				logViewEvent(requirementId);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
	function lockRequirement(requirementId, folderId){
		// Get the Ajax response from Requirement Deletion and populate the
		// contentCenterB.
		// Note : this call has to be Synchronous , as otherwise the req may not
		// be locked
		// by the time the displaycore and displayAttributes happens
		var url="/GloreeJava2/servlet/RequirementAction?action=lockRequirement&requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();
			
		document.getElementById("requirementPromptDiv").style.display = "block";
		document.getElementById("requirementPromptDiv").innerHTML = "&nbsp;&nbsp;&nbsp;Working...";
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// once we get a response, lets refresh the core screen and requirement attributes screen.
				// Now fill contentCenterB with the Requirement Core Info
				displayRequirementCore(requirementId);
						
				// Now we build the attribute URL and redirect OPCenterD to it.
				url="/GloreeJava2/jsp/Requirement/displayRequirementAttributeValue.jsp?requirementId=" + requirementId;
				url += "&bustcache=" + new Date().getTime() ;
				fillOPCenterGeneric(url, "contentCenterD");
				
				// lets refresh the requirment div in the 'Requiremnts In Folder' (Explorer) section.
				var  displayRequirementInFolderDiv = "displayRequirementInFolderDiv" + requirementId;
				url="/GloreeJava2/jsp/Folder/displayARequirementInRealFolder.jsp?requirementId=" + requirementId;
				url += "&bustcache=" + new Date().getTime() ;
				document.getElementById(displayRequirementInFolderDiv).innerHTML = "&nbsp;&nbsp;&nbsp;Working...";
				fillOPCenterGeneric(url, displayRequirementInFolderDiv);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
	
	function unlockRequirement(requirementId, folderId){
		// Get the Ajax response from Requirement Deletion and populate the
		// contentCenterB.
		// Note : this call has to be Synchronous , as otherwise the req may not
		// be locked
		// by the time the displaycore and displayAttributes happens
		var url="/GloreeJava2/servlet/RequirementAction?action=unlockRequirement&requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();
			
		document.getElementById("requirementPromptDiv").style.display = "block";
		document.getElementById("requirementPromptDiv").innerHTML = "&nbsp;&nbsp;&nbsp;Working...";
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// once we get a response, lets refresh the core screen and requirement attributes screen.
				// Now fill contentCenterB with the Requirement Core Info
				displayRequirementCore(requirementId);
						
				// Now we build the attribute URL and redirect OPCenterD to it.
				url="/GloreeJava2/jsp/Requirement/displayRequirementAttributeValue.jsp?requirementId=" + requirementId;
				fillOPCenterGeneric(url, "contentCenterD");
				
				// lets refresh the requirment div in the 'Requiremnts In Folder' (Explorer) section.
				var  displayRequirementInFolderDiv = "displayRequirementInFolderDiv" + requirementId;
				url="/GloreeJava2/jsp/Folder/displayARequirementInRealFolder.jsp?requirementId=" + requirementId;
				url += "&bustcache=" + new Date().getTime() ;
				document.getElementById(displayRequirementInFolderDiv).innerHTML = "&nbsp;&nbsp;&nbsp;Working...";
				fillOPCenterGeneric(url, displayRequirementInFolderDiv);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
	
	function changeFolderAndDisplayBulkRequirementForm(){
		var folderIdObject = document.getElementById("folderId");
		var folderId = folderIdObject[folderIdObject.selectedIndex].value;
		displayFolderInExplorer(folderId);
		//displayFolderContentRight(folderId);
		displayFolderContentCenterA(folderId);
		// because we want to set menu tabs and call the createBulkRequirementsForm
		// lets do display contentcentera async
		var url="/GloreeJava2/jsp/Folder/displayRealFolder.jsp?folderId="+ folderId;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterA').style.display = 'block'; 
				document.getElementById('contentCenterA').innerHTML=xmlHttpOPCenterB.responseText;
				
								
				// now lets get the bulk create req form in contentcenterb
				createBulkRequirementForm(folderId);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		


	}
	// adds new columns to the dynamic table
	function createBulkRequirementsFormTable(requirementTypeId){
		var columnToDisplay = "";
		var selectColumnsToDisplayObject = document.getElementById("selectColumnsToDisplay");
		if (selectColumnsToDisplayObject != null){
			columnToDisplay = selectColumnsToDisplayObject[selectColumnsToDisplayObject.selectedIndex].value; 
		}
		
		var currentFolderId = document.getElementById("currentFolderId").value;
		
		// lets get the starting seq number for this column. This value is in the hidden input type.
		var columnNameSeqStart = document.getElementById(columnToDisplay + "SeqStart").value ;
		for (i = 0; i< 11; i++){
			// we have to make a separate call out to fill the dynamic cell
			// as otherwise xmlHttp objects tend to overlap and clash within a for loop
			fillDynamicTableCell(i, columnToDisplay, columnNameSeqStart, requirementTypeId, currentFolderId);
		}
		// lets remove the option from the drop down list.
		selectColumnsToDisplayObject.remove(selectColumnsToDisplayObject.selectedIndex);
		
	}
	
	function fillDynamicTableCell(rowNum, columnToDisplay, columnNameSeqStart, requirementTypeId, currentFolderId){
		
		
		var url = "/GloreeJava2/jsp/Requirement/BulkRequirements/createDynamicTable.jsp?columnToDisplay=" + encodeURIComponent(columnToDisplay);
		url += "&rowNum=" + rowNum ;
		url += "&columnNameSeqStart=" + columnNameSeqStart ;
		url += "&requirementTypeId=" + requirementTypeId ;
		url += "&currentFolderId=" + currentFolderId ;
		url += "&bustcache=" + new Date().getTime() ;
		
		var thisRow = document.getElementById("newRequirementsTable").rows[rowNum];
		var newCell = thisRow.insertCell(-1);
		newCell.innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		newCell.className = columnToDisplay;
		if (rowNum > 0){
			newCell.align="right";
		}
		
		var xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				newCell.innerHTML=xmlHttpOPCenterB.responseText;
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
							
	}

	// since html / browsers tend to keep deleted object in javascript memory
	// we have to create new tag ids any time we show hidden columns again.
	function increaseDisplaySequence(objectName){
		var hiddenObject = document.getElementById(objectName);
		hiddenObject.value = (hiddenObject.value * 1) + 10;
	}
	
	function copyDynamicCellValuesDown(valueType, columnName, rowNum){
		
		var currentValue = document.getElementById(columnName + rowNum).value;
		// we want to iterate from the current row to the max number of rows
		// to get the seq id of the max number of row, we get the 
		// current columnSeqStart number from the hidden param and add 10 to it.
		var columnNameSeqStart = document.getElementById(columnName + "SeqStart").value ;
		var columnNameSeqEnd = (columnNameSeqStart *1) + 10;
		
		if (valueType == "textBox"){
			for (i = rowNum; i<columnNameSeqEnd; i++){
				var tempObject = document.getElementById(columnName + i);
				tempObject.value = currentValue;
				
			}
		}
		if (valueType == "dropDown") {
			var currentValue = document.getElementById(columnName + rowNum).selectedIndex;
			for (i = rowNum; i<columnNameSeqEnd; i++){
				document.getElementById(columnName + i).selectedIndex = currentValue;
			}
		}
		if (valueType == "dropDownMultiple") {
			
			var sourceElement = document.getElementById(columnName + rowNum);
			for (var i = 0; i < sourceElement.options.length; i++) {
				if (sourceElement.options[i].selected) {
					
					for (j = rowNum; j<columnNameSeqEnd; j++){
						var targetElement = document.getElementById(columnName + j);
						targetElement.options[i].selected = true;
						
					}
				}
			}
			
		}

	}

	
	
	// called when someone clicks on to 'Create Bulk Requirement' button in contentCenterA 
	function createBulkRequirementForm(folderId){
		// set the other content centers to empty.
		document.getElementById("contentCenterC").style.display = 'none';
		document.getElementById("contentCenterD").style.display = 'none';
		document.getElementById("contentCenterE").style.display = 'none';
		document.getElementById("contentCenterF").style.display = 'none';

		
		var url = "/GloreeJava2/jsp/Requirement/BulkRequirements/createBulkRequirementForm.jsp?folderId=" + folderId;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterB').style.display = 'block'; 
				document.getElementById('contentCenterB').innerHTML=xmlHttpOPCenterB.responseText;
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
	}
	
	function validateParentTagInBulkEdit(parentTag, divName, folderId){
		document.getElementById(divName).style.display = 'block';
		document.getElementById(divName).innerHTML= "&nbsp;&nbsp;&nbsp;Validating...";
		
		var url="/GloreeJava2/servlet/RequirementAction?action=validateParentTag";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&parentFullTag=" + encodeURIComponent(parentTag) ;
		url += "&folderId=" + folderId ;
		
		fillOPCenterGeneric(url, divName);
		
	}
	
	function validateTraceToInBulkEdit(traceTo, divName){
		document.getElementById(divName).style.display = 'block';
		document.getElementById(divName).innerHTML= "&nbsp;&nbsp;&nbsp;Validating...";
		
		var url="/GloreeJava2/servlet/RequirementAction?action=validateTraceTo";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&traceTo=" + encodeURIComponent(traceTo) ;
		
		fillOPCenterGeneric(url, divName);
	}

	function validateTraceFromInBulkEdit(traceFrom, divName){
		document.getElementById(divName).style.display = 'block';
		document.getElementById(divName).innerHTML= "&nbsp;&nbsp;&nbsp;Validating...";
		
		var url="/GloreeJava2/servlet/RequirementAction?action=validateTraceFrom";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&traceFrom=" + encodeURIComponent(traceFrom) ;
		
		fillOPCenterGeneric(url, divName);
	}

	function validateTraceToInWebForm(projectId, webFormId){
		var divName = 'validateTraceToDiv';
		var traceTo = document.getElementById('traceTo').value;
		document.getElementById(divName).style.display = 'block';
		document.getElementById(divName).innerHTML= "&nbsp;&nbsp;&nbsp;Validating...";
		
		var url="/GloreeJava2/servlet/RequirementAction?action=validateTraceToInWebForm";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&traceTo=" + encodeURIComponent(traceTo) ;
		url += "&projectId=" + encodeURIComponent(projectId) ;
		url += "&webFormId=" + encodeURIComponent(webFormId) ;
		
		fillOPCenterGeneric(url, divName);
	}
	
	function validateTraceFromInWebForm(projectId, webFormId){
		var divName = 'validateTraceFromDiv';
		var traceFrom = document.getElementById('traceFrom').value;
		document.getElementById(divName).style.display = 'block';
		document.getElementById(divName).innerHTML= "&nbsp;&nbsp;&nbsp;Validating...";
		
		var url="/GloreeJava2/servlet/RequirementAction?action=validateTraceFromInWebForm";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&traceFrom=" + encodeURIComponent(traceFrom) ;
		url += "&projectId=" + encodeURIComponent(projectId) ;
		url += "&webFormId=" + encodeURIComponent(webFormId) ;
		
		fillOPCenterGeneric(url, divName);
	}
	function createBulkRequirements(thisForm,  mandatoryAttributeNames,mandatoryDateAttributeNames ) {
		//now that we are ready upload file, lets gray out the
		// submit button to prevent accidental resubmits.
		
		var numOfReqs = 0;
		// lets iterate to make sure mandatory attribs are set, if the req name has been entered
		for (i=0; i< 10; i++){
			var reqName = document.getElementById("requirementName"+i).value;
			if (reqName.length > 0 ){
				numOfReqs += 1;
				mandatoryArray = mandatoryAttributeNames.split(':#:');
				for (j=0; j< mandatoryArray.length; j++){
					
					var mandatoryLabel =  mandatoryArray[j] + i;
					var mandatoryObject = document.getElementById(mandatoryLabel);
					if (mandatoryObject != null) {
						
						if (mandatoryObject.value.length == 0){
							alert ("Please enter a value for Mandatory attribute " + mandatoryArray[j]);
							mandatoryObject.focus();
							mandatoryObject.style.backgroundColor="#FFCC99";
							return(0);
						}
					}
					
				}
				
				// lets make sure that all date attributes are formatted correctly
				mandatoryDateArray = mandatoryDateAttributeNames.split(':#:');
				for (j=0; j< mandatoryDateArray.length; j++){
					
					var mandatoryDateLabel =  mandatoryDateArray[j] + i;
					var mandatoryDateObject = document.getElementById(mandatoryDateLabel);
					if (mandatoryDateObject != null) {
						if (isValidDate(mandatoryDateObject.value)==false){
							mandatoryDateObject.focus();
							mandatoryDateObject.style.backgroundColor="#FFCC99";
							return(0);
						}
					}
				}
			}	
		}
		
		if (numOfReqs == 0){
			alert ("Please Enter at least one Requirement name" );
			return(0);
		}
		document.getElementById("createBulkRequirementsButton").disabled=true;
		thisForm.submit();
		document.getElementById("createBulkRequirementsFormDiv").innerHTML = "&nbsp;&nbsp;&nbsp;Working...";
	}
	
	
	function showVotingList(folderId){
		
		
		document.getElementById("contentCenterC").style.display = 'none';
		document.getElementById("contentCenterD").style.display = 'none';
		document.getElementById("contentCenterE").style.display = 'none';
		document.getElementById("contentCenterF").style.display = 'none';

		
		var url = "/GloreeJava2/jsp/Vote/showVotingList.jsp?folderId=" + folderId;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "contentCenterB");		
	}
	
	function showVotingRow(requirementId){
		
		
		var url = "/GloreeJava2/jsp/Vote/showVotingRow.jsp?requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "voteRow"+ requirementId);		
	}
	function showVotingStatus(requirementId){
		var url = "/GloreeJava2/jsp/Vote/showVotingStatus.jsp?requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;		
		fillOPCenterGeneric(url, "votingStatusDiv");		
	}

	
	function showOtherVotes(requirementId){
		var url = "/GloreeJava2/jsp/Vote/showOtherVotes.jsp?requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;		
		fillOPCenterGeneric(url, "otherVotes" + requirementId);		
	}

	function castVote(requirementId){
		
		var vote = document.getElementById("castVote" + requirementId).value;
		
		var url="/GloreeJava2/servlet/RequirementAction?action=castVote";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&vote=" + vote ;
		url += "&requirementId=" + requirementId ;
		

		document.getElementById('voteRow'+requirementId).style.display = 'block'; 
		document.getElementById('voteRow'+requirementId).innerHTML="&nbsp;&nbsp;&nbsp;Working...";

		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				//once we get the response back, lets refres the row
				showVotingRow(requirementId);
				showVotingStatus(requirementId);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
	}
	
	// called when someone clicks on to 'Create Requirement' button in contentCenterA 
	function createRequirementForm(folderId){
		
		// hide the CreateRequirementForm button. 
		document.getElementById("createRequirementFormButton").style.visibility='hidden';
		// set the other content centers to empty.
		
		document.getElementById("contentCenterC").style.display = 'none';
		document.getElementById("contentCenterD").style.display = 'none';
		document.getElementById("contentCenterE").style.display = 'none';
		document.getElementById("contentCenterF").style.display = 'none';

		
		var url = "/GloreeJava2/jsp/Requirement/createRequirementForm.jsp?folderId=" + folderId;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterB').style.display = 'block'; 
				document.getElementById('contentCenterB').innerHTML=xmlHttpOPCenterB.responseText;
				
				// after we load the form, lets activate the Rich Text Editor
				//createRequirementFormRichText();
				var isMSIE = isBrowserMSIE();
				if (!isMSIE){
					editRequirementDescriptionRichText();
				}
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
	}
	
	function validateParentTag(folderId){
		document.getElementById("parentInfoDiv").style.display = 'block';
		document.getElementById("parentInfoDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		var url="/GloreeJava2/servlet/RequirementAction?action=getParentInfo";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&parentFullTag=" + encodeURIComponent(document.getElementById("parentFullTag").value) ;
		url += "&folderId=" + folderId ;
		
		fillOPCenterGeneric(url, "parentInfoDiv");
	}
	
		
	// we are creating a global variable called createRequirementDescription.
	// this needs to be a global variable, as we will need to access it between
	// functions.
	var createRequirementDescriptionRTE;
	
	function createRequirementFormRichText(){
	    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;
    
	    var requirementDescription = document.getElementById("requirementDescription");
	    
	    var myConfig = {
	    		height: '300px',
	    		width: '900px',
	    		dompath: true, 
	    		animate : true
	    };


	    createRequirementDescriptionRTE = new YAHOO.widget.SimpleEditor(requirementDescription, myConfig);
	    createRequirementDescriptionRTE.render();


	    // put focus on req name so people can start creating reqs.
	    //var requirementName = document.getElementById("requirementName");
	    //requirementName.focus();
	    
	}

	function validateRequirementOwner(){
		var requirementOwner = document.getElementById("requirementOwner");
		document.getElementById("requirementOwnerValidateDiv").style.display = "block";
		document.getElementById("requirementOwnerValidateDiv").innerHTML = "&nbsp;&nbsp;&nbsp;Validating...";;

		var url="/GloreeJava2/servlet/RequirementAction?action=validateRequirementOwner&";
		url +=  "requirementOwner=" + encodeURIComponent(requirementOwner.value) + "&";
		url +=  "&bustcache=" + new Date().getTime() ;
		fillOPCenterGeneric(url, "requirementOwnerValidateDiv");
	}

	
	function createRequirement(attributeIdString, attributeRequiredIdString){
		
		try{
			document.getElementById("createRequirementFormButton").style.visibility='visible';
		}
		catch (e){}
		
		var cRF = document.getElementById("createRequirementForm");

		var folderId = cRF.folderId;
		var projectId = cRF.projectId;
		var requirementType = cRF.requirementType;
		var parentFullTag = cRF.parentFullTag;
		var requirementName = cRF.requirementName;

		var cloneParentAttributes = "";
		var cloneParentAttributesObject = document.getElementById("cloneParentAttributes");
		if (cloneParentAttributesObject != null){
			cloneParentAttributes = cloneParentAttributesObject[cloneParentAttributesObject.selectedIndex].value; 
		}
		

		var requirementTypeName = document.getElementById("requirementTypeName").value;
		
		// we are leveraging the global variable here.
		var requirementDescription = "";
		/*
		if (createRequirementDescriptionRTE != null ){
			requirementDescription = createRequirementDescriptionRTE.getEditorHTML()   ;
		}
		else {
			requirementDescription = cRF.requirementDescription.value;
		}
		*/
		
		var isMSIE = isBrowserMSIE();
		if (!isMSIE){
			requirementDescription = CKEDITOR.instances.requirementDescription.getData();
			
			//requirementDescription = editRequirementDescriptionRTE.getEditorHTML();
			// cleanHTML takes care of crap coming from word 
			// this doesn't seem to work for requirements that are already in the system.
			// we also have something on the server side that tries to clean up word crap
			requirementDescription = cleanHTML(requirementDescription);

		}
		else {
			requirementDescription = document.getElementById("requirementDescription").value;
		}
		
		
		var requirementPriority = "Medium";
		try {
			var requirementPriorityObject = cRF.requirementPriority;
			requirementPriority = requirementPriorityObject[requirementPriorityObject.selectedIndex].value;
		}
		catch (e) {
			// do nothing
		}
		
		
		
		var requirementOwner = "";
		try {
			var requirementOwnerObject = cRF.requirementOwner;
			requirementOwner = requirementOwnerObject[requirementOwnerObject.selectedIndex].value;
		}
		catch (e) {
			// do nothing
		}
		
		var requirementPctComplete = cRF.requirementPctComplete;
		var requirementExternalUrl = cRF.requirementExternalUrl;



		

		
		if (requirementName.value.length == 0) {
			alert ("Please enter a Requirement Name");
			requirementName.focus();
			requirementName.style.backgroundColor="#FFCC99";
			return(0);
			}
		
		
		
		if (isNaN(requirementPctComplete.value)){
			alert ("Please enter a valid number for percent complete");
			requirementPctComplete.style.backgroundColor="#FFCC99";
			requirementPctComplete.focus();
			return(0);
		}
		
		if ((requirementPctComplete.value < 0) || (requirementPctComplete.value > 100) ) {
			alert ("Please enter a valid number between 0 and 100 for percent complete");
			requirementPctComplete.style.backgroundColor="#FFCC99";
			requirementPctComplete.focus();
			return(0);
		}
		var folderIdValue = folderId.value;
		var url="/GloreeJava2/servlet/RequirementAction";
		var params; 
		params = "action=createRequirement&";
		params = params + "folderId=" + folderId.value + "&";
		params = params + "projectId=" + projectId.value + "&";
		params =  params +"requirementTypeId="+ requirementType.value + "&";
		params = params + "parentFullTag=" + encodeURIComponent(parentFullTag.value) + "&";
		params = params + "requirementName=" + encodeURIComponent(requirementName.value) + "&";
		params = params + "requirementDescription=" + encodeURIComponent(requirementDescription)+ "&" ;
		params = params + "requirementPriority=" + encodeURIComponent(requirementPriority) + "&";
		params = params + "requirementOwner=" + encodeURIComponent(requirementOwner) + "&";
		params = params + "requirementPctComplete=" + encodeURIComponent(requirementPctComplete.value) + "&";
		params = params + "requirementExternalUrl=" + encodeURIComponent(requirementExternalUrl.value) ;
		params = params + "&bustcache=" + new Date().getTime() ;
		params = params + "&cloneParentAttributes=" + encodeURIComponent(cloneParentAttributes) ;
		
		
		// lets check that the required attributes are entered.
		requiredAttributes = attributeRequiredIdString.split('##');
		for (r in requiredAttributes){
			
			var requiredAttributeString = requiredAttributes[r];
			if (requiredAttributeString != null) {
				var splitA = requiredAttributeString.split('#');
				var id = splitA[0];
				var name = splitA[1];
				var attributeType = splitA[2];
				 
				var requiredAttribute = document.getElementById(id);
				
				if (requiredAttribute != null) {
					// this above line takes care of situations where we have 
					// the last # and that gave us an extra null row.
					if (attributeType == "DropDown"){
						if (requiredAttribute.selectedIndex == 0) {
							alert ( name + " is a Required attribute. Please enter a value");
							requiredAttribute.focus();
							requiredAttribute.style.backgroundColor="#FFCC99";
							return(0);
						}
					}
					else {
						if ((requiredAttribute.value == null) || (requiredAttribute.value.length == 0)) {
							alert ( name + " is a Required attribute. Please enter a value");
							requiredAttribute.focus();
							requiredAttribute.style.backgroundColor="#FFCC99";
							return(0);
						}
					}
				}
			}
		}	

		// lets get the attribute values
		// we use the attributeIdString to split and figure out which attributes
		// to look for.
		// for these attributes , we get the value, build the URL string and .
		// we will use the same logic to get the attribute values and create
		// them in the db.
		attributeIds = attributeIdString.split('##');
		
		for (a in attributeIds){
			splitA = attributeIds[a].split('#');
			var id = splitA[0];
			var type = splitA[1];
			if (id) {
				var attribute = document.getElementById(id);
				// if this is a date type, lets run some java script validation to 
				// ensure that the mm/dd/yyyy format is maintained.
				if (type == 'Date'){
					var dateValue = attribute.value;
					if (dateValue != ""){
						// we do this validation only if some value has been entered in the date box.
						if (isValidDate(dateValue)==false){
							attribute.focus()
							attribute.style.backgroundColor="#FFCC99";
							return (0);
						}
					}
				}
				// if this is a number type, lets run some java script validation to 
				// ensure that the value is really a number.
				if (type == 'Number'){
					var numberValue = attribute.value;
					if (numberValue != ""){
						// we do this validation only if some value has been entered in the number box.
						if (isNaN(numberValue)){
							alert("Please enter a valid Number");
							attribute.focus()
							attribute.style.backgroundColor="#FFCC99";
							return (0);
						}
					}
				}
				
				
				
				
				// lets now add the values to the param string
				if (type == 'DropDown'){
					params = params + '&' +  id + '='+  encodeURIComponent(attribute[attribute.selectedIndex].value) ;
				}
				if (type == 'DropDownMultiple' ){
					
					var selectedMultipleDropDownValues = "";
					for (var i = 0; i < attribute.options.length; i++) {
						if (attribute.options[i].selected) {
							selectedMultipleDropDownValues += attribute.options[i].value + ','; 
							
						}
					}
					params = params + '&' +  id + '='+  encodeURIComponent(selectedMultipleDropDownValues) ;
				}
				else {
					params = params + '&' + id + '='+  encodeURIComponent(attribute.value) ;
				}
			}
		}	
		
		
		params = params + '&attributeIdString=' +  encodeURIComponent(attributeIdString) ;
		
		// cleanHTML takes care of crap coming from word 
		// this doesn't seem to work for requirements that are already in the system.
		// we also have something on the server side that tries to clean up word crap
		requirementDescription = cleanHTML(requirementDescription);
		
		
		// now that we are ready to create the req, lets gray out the
		// submit button to prevent accidentala resubmits.
		document.getElementById("createRequirementButton").disabled=true;
		
		
		
		xmlHttpOPCenterB = GetXmlHttpObject();
		xmlHttpOPCenterB.open("POST", url, true);

		// Send the proper header information along with the request
		xmlHttpOPCenterB.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xmlHttpOPCenterB.setRequestHeader("Content-length", params.length);
		xmlHttpOPCenterB.setRequestHeader("Connection", "close");


		
		// Get the Ajax response from Requirement Creation and populate the
		// contentCenterA.
		// Note : this call has to be Synchronous , as otherwise the folder may
		// not be created
		// by the time the fillExplorer happens..
		
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				

				// we get the requirement id as part of JSON response.
				var contentResponse = xmlHttpOPCenterB.responseText;
				// lets see if we got an error in response to creating the requirement.
				var pos= contentResponse.indexOf("Error : ");
				if (pos > 0){
					// means we got an error. lets display the error message.
					document.getElementById("requirementCreationErrorDiv").style.display='block';
					document.getElementById("requirementCreationErrorDiv").innerHTML= contentResponse;
					document.getElementById("createRequirementButton").disabled=false;
								
				}
				else {
					// means we created the req and the contentResponse 
					// had the req id.
					//////////////////////////////////////////
					
					displayFolderContentsInExplorer(folderIdValue)
					
					var contentResponseObject = eval ("(" + contentResponse + ")" );
					var requirementId = contentResponseObject.requirementId;
	
					// Now fill contentCenterB with the Requirement Core Info
					displayRequirementCore(requirementId);
					
					
					// Now we build the traceability URL and redirect OPCenterC to
					// it.
					url="/GloreeJava2/jsp/Requirement/displayRequirementTrace.jsp?requirementId=" + requirementId;
					fillOPCenterGeneric(url, "contentCenterC");
					
					// Now we build the attribute URL and redirect OPCenterD to it.
					url="/GloreeJava2/jsp/Requirement/displayRequirementAttributeValue.jsp?requirementId=" + requirementId;
					fillOPCenterGeneric(url, "contentCenterD");
				}
			}
		}

		xmlHttpOPCenterB.send(params);
		
	}


	
	function refreshExplorer(folderId){
		
		var url="/GloreeJava2/jsp/Folder/returnFolderId2.jsp?folderId=" + folderId;
		xmlHttpOPCenterExplorer =GetXmlHttpObject();		
		xmlHttpOPCenterExplorer.onreadystatechange=function() {
			if(xmlHttpOPCenterExplorer.readyState==4){
				var contentResponse = xmlHttpOPCenterExplorer.responseText;
				var contentResponseObject = eval ("(" + contentResponse + ")" );

				var folderId = contentResponseObject.folderId;
				var folderName = contentResponseObject.folderName;
				var folderDescription = contentResponseObject.folderDescription;
				var folderCount = contentResponseObject.folderCount;

				thisFolderNode = tree.getNodeByProperty('id',folderId);
				thisFolderNode.getLabelEl().innerHTML = folderName + " (" + folderCount + ") ";

			}
		}
		xmlHttpOPCenterExplorer.open("GET",url,true);
		xmlHttpOPCenterExplorer.send(null);
		
	}
	
	
	function createRequirementInWebForm(attributeIdString, attributeRequiredIdString){
		
		var cRF = document.getElementById("createRequirementForm");

		var folderId = cRF.folderId;
		var projectId = cRF.projectId;
		var requirementType = cRF.requirementType;
		var parentFullTag = cRF.parentFullTag;
		var requirementName = cRF.requirementName;
		
		var webFormId = cRF.webFormId;

		var cloneParentAttributes = "";
		var cloneParentAttributesObject = document.getElementById("cloneParentAttributes");
		if (cloneParentAttributesObject != null){
			cloneParentAttributes = cloneParentAttributesObject[cloneParentAttributesObject.selectedIndex].value; 
		}
		
		// we are leveraging the global variable here.
		var requirementDescription = "";
		if (createRequirementDescriptionRTE != null ){
			requirementDescription = createRequirementDescriptionRTE.getEditorHTML()   ;
		}
		else {
			requirementDescription = cRF.requirementDescription.value;
		}
		var requirementPriority = cRF.requirementPriority;
		var requirementOwner = cRF.requirementOwner;
		var requirementPctComplete = cRF.requirementPctComplete;
		var requirementExternalUrl = cRF.requirementExternalUrl;


		if (requirementOwner.value.length == 0) {
			alert ("Please enter a Requirement Owner");
			requirementOwner.focus();
			requirementOwner.style.backgroundColor="#FFCC99";
			return(0);
			}

		if (echeck(requirementOwner.value)==false){
			alert ("The Owner's email id " + requirementOwner.value + " is not formatted correctly. Please fix it.");
			requirementOwner.style.backgroundColor="#FFCC99";
			requirementOwner.focus();
			return(0);
		}

		
		if (requirementName.value.length == 0) {
			alert ("Please enter a Requirement Name");
			requirementName.focus();
			requirementName.style.backgroundColor="#FFCC99";
			return(0);
			}
		
		
		
		if (isNaN(requirementPctComplete.value)){
			alert ("Please enter a valid number for percent complete");
			requirementPctComplete.style.backgroundColor="#FFCC99";
			requirementPctComplete.focus();
			return(0);
		}
		
		if ((requirementPctComplete.value < 0) || (requirementPctComplete.value > 100) ) {
			alert ("Please enter a valid number between 0 and 100 for percent complete");
			requirementPctComplete.style.backgroundColor="#FFCC99";
			requirementPctComplete.focus();
			return(0);
		}
		
		var traceTo  = document.getElementById("traceTo").value;

		var traceFrom  = document.getElementById("traceFrom").value;
		
		var url="/GloreeJava2/servlet/RequirementAction";
		var params; 
		params = "action=createRequirementInWebForm&";
		params = params + "folderId=" + folderId.value + "&";
		params = params + "projectId=" + projectId.value + "&";
		params =  params +"requirementTypeId="+ requirementType.value + "&";
		params =  params +"webFormId="+ webFormId.value + "&";
		
		//params = params + "parentFullTag=" + encodeURIComponent(parentFullTag.value) + "&";
		params = params + "requirementName=" + encodeURIComponent(requirementName.value) + "&";
		params = params + "requirementDescription=" + encodeURIComponent(requirementDescription)+ "&" ;
		params = params + "requirementPriority=" + encodeURIComponent(requirementPriority[requirementPriority.selectedIndex].value) + "&";
		params = params + "requirementOwner=" + encodeURIComponent(requirementOwner.value) + "&";
		params = params + "requirementPctComplete=" + encodeURIComponent(requirementPctComplete.value) + "&";
		params = params + "requirementExternalUrl=" + encodeURIComponent(requirementExternalUrl.value) ;
		params = params + "&bustcache=" + new Date().getTime() ;
		params = params + "&cloneParentAttributes=" + encodeURIComponent(cloneParentAttributes) ;
		params = params + "&traceTo=" + encodeURIComponent(traceTo) ;
		params = params + "&traceFrom=" + encodeURIComponent(traceFrom) ;
		
		
		// lets check that the required attributes are entered.
		requiredAttributes = attributeRequiredIdString.split('##');
		for (r in requiredAttributes){
			
			var requiredAttributeString = requiredAttributes[r];
			if (requiredAttributeString != null) {
				var splitA = requiredAttributeString.split('#');
				var id = splitA[0];
				var name = splitA[1];
				var attributeType = splitA[2];
				 
				var requiredAttribute = document.getElementById(id);
				
				if (requiredAttribute != null) {
					// this above line takes care of situations where we have 
					// the last # and that gave us an extra null row.
					if (attributeType == "DropDown"){
						if (requiredAttribute.selectedIndex == 0) {
							alert ( name + " is a Required attribute. Please enter a value");
							requiredAttribute.focus();
							requiredAttribute.style.backgroundColor="#FFCC99";
							return(0);
						}
					}
					else {
						if ((requiredAttribute.value == null) || (requiredAttribute.value.length == 0)) {
							alert ( name + " is a Required attribute. Please enter a value");
							requiredAttribute.focus();
							requiredAttribute.style.backgroundColor="#FFCC99";
							return(0);
						}
					}
				}
			}
		}	

		// lets get the attribute values
		// we use the attributeIdString to split and figure out which attributes
		// to look for.
		// for these attributes , we get the value, build the URL string and .
		// we will use the same logic to get the attribute values and create
		// them in the db.
		attributeIds = attributeIdString.split('##');
		
		for (a in attributeIds){
			splitA = attributeIds[a].split('#');
			var id = splitA[0];
			var type = splitA[1];
			if (id) {
				var attribute = document.getElementById(id);
				// if this is a date type, lets run some java script validation to 
				// ensure that the mm/dd/yyyy format is maintained.
				if (type == 'Date'){
					var dateValue = attribute.value;
					if (dateValue != ""){
						// we do this validation only if some value has been entered in the date box.
						if (isValidDate(dateValue)==false){
							attribute.focus()
							attribute.style.backgroundColor="#FFCC99";
							return (0);
						}
					}
				}
				// if this is a number type, lets run some java script validation to 
				// ensure that the value is really a number.
				if (type == 'Number'){
					var numberValue = attribute.value;
					if (numberValue != ""){
						// we do this validation only if some value has been entered in the number box.
						if (isNaN(numberValue)){
							alert("Please enter a valid Number");
							attribute.focus()
							attribute.style.backgroundColor="#FFCC99";
							return (0);
						}
					}
				}
				
				
				
				
				// lets now add the values to the param string
				if (type == 'DropDown'){
					params = params + '&' +  id + '='+  encodeURIComponent(attribute[attribute.selectedIndex].value) ;
				}
				if (type == 'DropDownMultiple' ){
					
					var selectedMultipleDropDownValues = "";
					for (var i = 0; i < attribute.options.length; i++) {
						if (attribute.options[i].selected) {
							selectedMultipleDropDownValues += attribute.options[i].value + ','; 
							
						}
					}
					params = params + '&' +  id + '='+  encodeURIComponent(selectedMultipleDropDownValues) ;
				}
				else {
					params = params + '&' + id + '='+  encodeURIComponent(attribute.value) ;
				}
			}
		}	
		
		
		params = params + '&attributeIdString=' +  encodeURIComponent(attributeIdString) ;
		
		// cleanHTML takes care of crap coming from word 
		// this doesn't seem to work for requirements that are already in the system.
		// we also have something on the server side that tries to clean up word crap
		requirementDescription = cleanHTML(requirementDescription);
		
		
		// now that we are ready to create the req, lets gray out the
		// submit button to prevent accidentala resubmits.
		document.getElementById("createRequirementButton").disabled=true;
		
		
		xmlHttpOPCenterB = GetXmlHttpObject();
		xmlHttpOPCenterB.open("POST", url, true);

		// Send the proper header information along with the request
		xmlHttpOPCenterB.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xmlHttpOPCenterB.setRequestHeader("Content-length", params.length);
		xmlHttpOPCenterB.setRequestHeader("Connection", "close");


		
		// Get the Ajax response from Requirement Creation and populate the
		// contentCenterA.
		// Note : this call has to be Synchronous , as otherwise the folder may
		// not be created
		// by the time the fillExplorer happens..
		
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				
				
				// we get the requirement id as part of JSON response.
				var contentResponse = xmlHttpOPCenterB.responseText;
				// lets see if we got an error in response to creating the requirement.
				var pos= contentResponse.indexOf("Error : ");
				if (pos > 0){
					// means we got an error. lets display the error message.
					document.getElementById("createRequirementDiv").style.display='block';
					document.getElementById("createRequirementDiv").innerHTML= contentResponse;
								
				}
				else {
					document.getElementById("createRequirementDiv").style.display='block';
					document.getElementById("createRequirementDiv").innerHTML= contentResponse;
				
				}
			}
		}

		xmlHttpOPCenterB.send(params);
		
	}
	
	
	function editRequirementForm(requirementId, folderId){
		var url = "/GloreeJava2/jsp/Requirement/editRequirementForm.jsp?requirementId=" + requirementId + "&folderId=" + folderId;
		
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterB').style.display = 'block'; 
				document.getElementById('contentCenterB').innerHTML=xmlHttpOPCenterB.responseText;
				
				// after we load the form, lets activate the Rich Text Editor
				editRequirementFormRichText();
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
	}
	
	
	// we are creating a global variable called editRequirementDescriptionRTE.
	// this needs to be a gloabl variable, as we will need to access it between
	// functions.
	////////////////////////////////////////////////////////////////////////////
	//
	//					GLOBAL VARIABLES
	//
	////////////////////////////////////////////////////////////////////////////
	
	var editRequirementDescriptionRTE;
	
	function editRequirementFormRichText(){
	    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;
    
	    var requirementDescription = document.getElementById("requirementDescription");
	    
	    var myConfig = {
	    		titlebar: 'Rich Text Editor',
	    		height: '300px',
	    		width: '1200px',
	    		animate: true
	    };

	    editRequirementDescriptionRTE = new YAHOO.widget.SimpleEditor(requirementDescription, myConfig);
	    editRequirementDescriptionRTE.render();

	    
	    // lets put focus on the requirement name box.

	    var requirementName = document.getElementById("requirementName");
	    requirementName.focus();
	}

	function editRequirementDescriptionRichText(){
		
	    var rDEditor = CKEDITOR.replace( 'requirementDescription' , {
			// Define the toolbar: http://docs.ckeditor.com/#!/guide/dev_toolbar
			// The full preset from CDN which we used as a base provides more features than we need.
			// Also by default it comes with a 3-line toolbar. Here we put all buttons in a single row.
			toolbar: [
				{ name: 'document', items: [ 'Print' ] },
				{ name: 'clipboard', items: [ 'Undo', 'Redo' ] },
				{ name: 'styles', items: [ 'Format', 'Font', 'FontSize' ] },
				{ name: 'basicstyles', items: [ 'Bold', 'Italic', 'Underline', 'Strike', 'RemoveFormat', 'CopyFormatting' ] },
				{ name: 'colors', items: [ 'TextColor', 'BGColor' ] },
				{ name: 'align', items: [ 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock' ] },
				{ name: 'links', items: [ 'Link', 'Unlink' ] },
				{ name: 'paragraph', items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote' ] },
				{ name: 'insert', items: [ 'Image', 'Table' ] },
				{ name: 'tools', items: [ 'Maximize' ] },
				{ name: 'editing', items: [ 'Scayt' ] }
			],
			// Since we define all configuration options here, let's instruct CKEditor to not load config.js which it does by default.
			// One HTTP request less will result in a faster startup time.
			// For more information check http://docs.ckeditor.com/#!/api/CKEDITOR.config-cfg-customConfig
			customConfig: '',
			// Sometimes applications that convert HTML to PDF prefer setting image width through attributes instead of CSS styles.
			// For more information check:
			//  - About Advanced Content Filter: http://docs.ckeditor.com/#!/guide/dev_advanced_content_filter
			//  - About Disallowed Content: http://docs.ckeditor.com/#!/guide/dev_disallowed_content
			//  - About Allowed Content: http://docs.ckeditor.com/#!/guide/dev_allowed_content_rules
			disallowedContent: 'img{width,height,float}',
			extraAllowedContent: 'img[width,height,align]',
			// Enabling extra plugins, available in the full-all preset: http://ckeditor.com/presets-all
			extraPlugins: 'tableresize,uploadimage,uploadfile,autogrow',
			/*********************** File management support ***********************/
			// In order to turn on support for file uploads, CKEditor has to be configured to use some server side
			// solution with file upload/management capabilities, like for example CKFinder.
			// For more information see http://docs.ckeditor.com/#!/guide/dev_ckfinder_integration
			// Uncomment and correct these lines after you setup your local CKFinder instance.
			// filebrowserBrowseUrl: 'http://example.com/ckfinder/ckfinder.html',
			// filebrowserUploadUrl: 'http://example.com/ckfinder/core/connector/php/connector.php?command=QuickUpload&type=Files',
			/*********************** File management support ***********************/
			// Make the editing area bigger than default.
			//width:950,
			autoGrow_minHeight : 30,
			autoGrow_onStartup : true,
			// An array of stylesheets to style the WYSIWYG area.
			// Note: it is recommended to keep your own styles in a separate file in order to make future updates painless.
			contentsCss: [ 'https://cdn.ckeditor.com/4.6.1/full-all/contents.css', 'mystyles.css' ],
			// This is optional, but will let us define multiple different styles for multiple editors using the same CSS file.
			bodyClass: 'document-editor',
			// Reduce the list of block elements listed in the Format dropdown to the most commonly used.
			format_tags: 'p;h1;h2;h3;pre',
			// Simplify the Image and Link dialog windows. The "Advanced" tab is not needed in most cases.
			removeDialogTabs: 'image:advanced;link:advanced',
			// Define the list of styles which should be available in the Styles dropdown list.
			// If the "class" attribute is used to style an element, make sure to define the style for the class in "mystyles.css"
			// (and on your website so that it rendered in the same way).
			// Note: by default CKEditor looks for styles.js file. Defining stylesSet inline (as below) stops CKEditor from loading
			// that file, which means one HTTP request less (and a faster startup).
			// For more information see http://docs.ckeditor.com/#!/guide/dev_styles
			stylesSet: [
				/* Inline Styles */
				{ name: 'Marker', element: 'span', attributes: { 'class': 'marker' } },
				{ name: 'Cited Work', element: 'cite' },
				{ name: 'Inline Quotation', element: 'q' },
				/* Object Styles */
				{
					name: 'Special Container',
					element: 'div',
					styles: {
						padding: '5px 10px',
						background: '#eee',
						border: '1px solid #ccc'
					}
				},
				{
					name: 'Compact table',
					element: 'table',
					attributes: {
						cellpadding: '5',
						cellspacing: '0',
						border: '1',
						bordercolor: '#ccc'
					},
					styles: {
						'border-collapse': 'collapse'
					}
				},
				{ name: 'Borderless Table', element: 'table', styles: { 'border-style': 'hidden', 'background-color': '#E6E6FA' } },
				{ name: 'Square Bulleted List', element: 'ul', styles: { 'list-style-type': 'square' } }
			]
		} );
	    
	    rDEditor.on( 'change', function( evt ) {
	    	
	    	try {
	    		//document.getElementById("UpdateDescriptionBtn").style.visibility="visible";
	    		document.getElementById("requirementDescriptionUpdateBtnDiv").style.display="block";
	    		
	    	}
    		catch (e){
    			// do nothing.
    		}
		    
		 });
	    
	    
	}
	
	
	function editRequirement(){
		
		var cRF = document.getElementById("editRequirementForm");

		var requirementId = cRF.requirementId;
		var folderId = cRF.folderId;
		var requirementName = cRF.requirementName;
		var requirementDescription = editRequirementDescriptionRTE.getEditorHTML();
		var requirementStatus = cRF.requirementStatus;
		var requirementPriority = cRF.requirementPriority;
		var requirementOwner = cRF.requirementOwner;
		var requirementPctComplete = cRF.requirementPctComplete;
		var requirementExternalUrl = cRF.requirementExternalUrl;


		// cleanHTML takes care of crap coming from word 
		// this doesn't seem to work for requirements that are already in the system.
		// we also have something on the server side that tries to clean up word crap
		requirementDescription = cleanHTML(requirementDescription);
		
		
		
		if (requirementName.value.length == 0) {
			alert ("Please enter a Requirement Name");
			requirementName.focus();
			requirementName.style.backgroundColor="#FFCC99";
			return(0);
			}
		
		
		
		if (isNaN(requirementPctComplete.value)){
			alert ("Please enter a valid number for percent complete");
			requirementPctComplete.style.backgroundColor="#FFCC99";
			requirementPctComplete.focus();
			return(0);
		}
		
		if ((requirementPctComplete.value < 0) || (requirementPctComplete.value > 100) ) {
			alert ("Please enter a valid number between 0 and 100 for percent complete");
			requirementPctComplete.style.backgroundColor="#FFCC99";
			requirementPctComplete.focus();
			return(0);
		}

		// at this point we are ready to update the req.
		// so lets disable the submit button to prevent accidental double clicks.
		document.getElementById("updateRequirementButton").disabled = true;
		
		var url="/GloreeJava2/servlet/RequirementAction";
		var params; 
		params = "action=editRequirement&";
		params = params + "folderId=" + folderId.value + "&";
		params = params + "requirementId=" + requirementId.value + "&";
		params = params + "requirementName=" + encodeURIComponent(requirementName.value) + "&";
		params = params + "requirementDescription=" + encodeURIComponent(requirementDescription)+ "&" ;
		params = params + "requirementPriority=" + encodeURIComponent(requirementPriority[requirementPriority.selectedIndex].value) + "&";
		params = params + "requirementOwner=" + encodeURIComponent(requirementOwner[requirementOwner.selectedIndex].value) + "&";		
		params = params + "requirementPctComplete=" + encodeURIComponent(requirementPctComplete.value) + "&";
		params = params + "requirementExternalUrl=" + encodeURIComponent(requirementExternalUrl.value) ;
		params = params + "&bustcache=" + new Date().getTime() ;
		
		// Get the Ajax response from Requirement Creation and populate the
		// contentCenterA.
		// Note : this call has to be Synchronous , as otherwise the folder may
		// not be created
		// by the time the fillExplorer happens..

		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.open("POST", url, true);

		// Send the proper header information along with the request
		xmlHttpOPCenterB.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xmlHttpOPCenterB.setRequestHeader("Content-length", params.length);
		xmlHttpOPCenterB.setRequestHeader("Connection", "close");
 
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;
				

				// Editing a Req makes all traces to / from this req suspect. So
				// we refresh the traceability section.
				url="/GloreeJava2/jsp/Requirement/displayRequirementTrace.jsp?requirementId=" + requirementId.value;
				fillOPCenterGeneric(url, "contentCenterC");

				

				// Once ContentCenterB is loaded
				// lets scroll to the top of the page any time we display the requirement.
				window.location.hash="TopOfPage";
				// we need to put focus somewhere , so we have decided to put 
				// focus on the searchstring box.
				// this takes care of the situation where IE tends to lose the focus object.
				document.getElementById("googleSearchString").focus();
				
				
			}
		}
		xmlHttpOPCenterB.send(params);
	}

	function cancelEditRequirementForm(requirementId){
		
		// Now fill contentCenterB with the Requirement Core Info
		// since IE is bad aboout losing focus, we call the
		// displayRequirementCore method, which set focus on a text box.
		displayRequirementCore(requirementId);
	}
	
	
	// called when someone confirms the click on the 'Delete' button in the display requirement core page.
	function deleteRequirement(requirementId, folderId , source){
		
		document.getElementById("returnDiv").innerHTML= "";
		document.getElementById("returnDiv").style.display='none';
		
		// First we set the contentCenterC and D to empty.
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		
				
		// Get the Ajax response from Requirement Deletion and populate the
		// contentCenterB.
		// Note : this call has to be Synchronous , as otherwise the req may not
		// be deleted
		// by the time the fillRight happens..
		var url="/GloreeJava2/servlet/RequirementAction?action=deleteRequirement&requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				////////////////////////////////////////
				// lets refresh the explorer as the folder contents have changed
				/////////////////////////////////////////
				refreshExplorer(folderId);
				/////////////////////////////
				
				if (source == "requirementList" ){
					// this is coming fron the list of requirements in a folder
					
					 document.getElementById("displayRDInFolderDiv" + requirementId).style.display = "none"; 
					document.getElementById("displayRequirementInFolderDiv" + requirementId).innerHTML= "<span class='normalText'><font color='red'><b></b>This object has been deleted</font></span>";
				}
				else {
					// this is coming from a single requirement
					document.getElementById("contentCenterB").style.display = "block";
					document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;
	
					displayAllRequirementsInRealFolder(folderId);
				}
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}

	
	function setRequirementPercentComplete(requirementId){
		percentComplete = document.getElementById("requirementPercentComplete").value;
		url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
		url += "&targetAttribute=" + encodeURIComponent("percentComplete");
		url += "&targetValue=" + encodeURIComponent(percentComplete);
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("requirementPercentCompleteDiv").style.display = "block";
		document.getElementById("requirementPercentCompleteDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "requirementPercentCompleteDiv");
	}


	function createNewGlosaryItem(requirementId){
		var glossaryName = document.getElementById("glossaryName").value;
		var glossaryDescription = document.getElementById("glossaryDescription").value;

		url="/GloreeJava2/servlet/RequirementAction?requirementId=" + requirementId;
		url += "&action=" + encodeURIComponent("createNewGlossaryItem");
		url += "&glossaryName=" + encodeURIComponent(glossaryName);
		url += "&glossaryDescription=" + encodeURIComponent(glossaryDescription);
		url += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById("createGlossaryDiv").style.display = "block";
		document.getElementById("createGlossaryDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		
		xmlHttpOPCenterB =GetXmlHttpObject();
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// since a change to the name can cause the version number, approval status, approvers
				// and name to chnge, we need to refresh the core section.
				displayRequirementCore(requirementId);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
	

	
	function rollbackRequirement(requirementId, version){
		url="/GloreeJava2/servlet/RequirementAction?requirementId=" + requirementId;
		url += "&action=" + encodeURIComponent("rollbackVersion");
		url += "&targetVersion=" + encodeURIComponent(version);
		url += "&bustcache=" + new Date().getTime() ;
		
				
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("requirementNameDiv").innerHTML=xmlHttpOPCenterB.responseText;

				// since a change to the name can cause the version number, approval status, approvers
				// and name to chnge, we need to refresh the core section.
				displayRequirementCore(requirementId);
				 

				url="/GloreeJava2/jsp/Requirement/displayRequirementTrace.jsp?requirementId=" + requirementId;
				fillOPCenterGeneric(url, "contentCenterC");
				
				// Now we build the attribute URL and redirect OPCenterD to it.
				url="/GloreeJava2/jsp/Requirement/displayRequirementAttributeValue.jsp?requirementId=" + requirementId;
				fillOPCenterGeneric(url, "contentCenterD");
				

				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}

		
	
	function setRequirementDescription(requirementId, folderId, browser){
			
			var requirementDescription = "";
			if (browser == "MSIE"){
				requirementDescription = document.getElementById("requirementDescription").value;
			}
			else {
				requirementDescription = editRequirementDescriptionRTE.getEditorHTML();
				// cleanHTML takes care of crap coming from word 
				// this doesn't seem to work for requirements that are already in the system.
				// we also have something on the server side that tries to clean up word crap
				requirementDescription = cleanHTML(requirementDescription);
				
			}
			
			
			url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?";
			var params = "requirementId=" + requirementId;
			params += "&targetAttribute=" + encodeURIComponent("requirementDescription");
			params += "&targetValue=" + encodeURIComponent(requirementDescription);
			params  += "&bustcache=" + new Date().getTime() ;
			document.getElementById("requirementDescriptionDiv").style.display = "block";
			document.getElementById("requirementDescriptionDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.open("POST", url, true);

			// Send the proper header information along with the request
			xmlHttpOPCenterB.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			xmlHttpOPCenterB.setRequestHeader("Content-length", params.length);
			xmlHttpOPCenterB.setRequestHeader("Connection", "close");


			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					document.getElementById("requirementDescriptionDiv").innerHTML=xmlHttpOPCenterB.responseText;
	
					// since a change to the name can cause the version number, approval status, approvers
					// and name to chnge, we need to refresh the core section.
					displayRequirementCore(requirementId);
					 
					
					// Editing a Req makes all traces to / from this req suspect. So
					// we refresh the traceability section.
					url="/GloreeJava2/jsp/Requirement/displayRequirementTrace.jsp?requirementId=" + requirementId;
					fillOPCenterGeneric(url, "contentCenterC");
	
					
				}
			}
			xmlHttpOPCenterB.send(params);

	}


	function setRequirementNameInList(requirementId, folderId){

		var requirementName = document.getElementById("requirementNameBox" + requirementId).value;
		url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
		url += "&targetAttribute=" + encodeURIComponent("requirementNameInList");
		url += "&targetValue=" + encodeURIComponent(requirementName);
		url += "&bustcache=" + new Date().getTime() ;
		
		// lets hide the edit box
		document.getElementById("requirementNameTextBoxDiv" + requirementId).style.display = "none";
		
		
		
		var targetDiv = "requirementNameDisplaySpan" + requirementId;
		
		document.getElementById(targetDiv).style.visibility = "visible";
		
		document.getElementById(targetDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		
		xmlHttpOPCenterB =GetXmlHttpObject();
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){

				// refresh the whole row in the list
				document.getElementById(targetDiv).innerHTML= xmlHttpOPCenterB.responseText;
				
				// once the requirement is set to completed, lets refresh the
				// label
				// lets refresh the requirement label
				url ="/GloreeJava2/jsp/Requirement/displayARequirementLabel.jsp?requirementId=" + requirementId;
				url += "&bustcache=" + new Date().getTime() ;
				
				var requirementLabelDiv = "reqStatusBox" + requirementId;
				
				document.getElementById(requirementLabelDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working Hard...</span>";
				fillOPCenterGeneric(url, requirementLabelDiv);
				
				// lets also refresh the requirement in explorer
				url ="/GloreeJava2/jsp/Folder/displayARequirementInExplorer.jsp?requirementId=" + requirementId;
				url += "&bustcache=" + new Date().getTime() ;
				
				fillOPCenterGeneric(url, "objectInExplorer" + requirementId);
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}

	
	function setRequirementName(requirementId, folderId){
		var requirementName = document.getElementById("requirementName").value;
		url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
		url += "&targetAttribute=" + encodeURIComponent("requirementName");
		url += "&targetValue=" + encodeURIComponent(requirementName);
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("requirementNameDiv").style.display = "block";
		document.getElementById("requirementNameDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		
		xmlHttpOPCenterB =GetXmlHttpObject();
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("requirementNameDiv").innerHTML=xmlHttpOPCenterB.responseText;

				// since a change to the name can cause the version number, approval status, approvers
				// and name to chnge, we need to refresh the core section.
				displayRequirementCore(requirementId);
				 
				
				// Editing a Req makes all traces to / from this req suspect. So
				// we refresh the traceability section.
				url="/GloreeJava2/jsp/Requirement/displayRequirementTrace.jsp?requirementId=" + requirementId;
				fillOPCenterGeneric(url, "contentCenterC");
				
				// lets also refresh the requirement in explorer
				url ="/GloreeJava2/jsp/Folder/displayARequirementInExplorer.jsp?requirementId=" + requirementId;
				url += "&bustcache=" + new Date().getTime() ;
				
				fillOPCenterGeneric(url, "objectInExplorer" + requirementId);

				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}

	
	function setRequirementNameAndDescription(requirementId, folderId, browser){
		
		var requirementName = document.getElementById("requirementName").value;
		
		var requirementDescription = "";
		if (browser == "MSIE"){
			requirementDescription = document.getElementById("requirementDescription").value;
		}
		else {
		    requirementDescription = CKEDITOR.instances.requirementDescription.getData();

		 	//requirementDescription = editRequirementDescriptionRTE.getEditorHTML();
			// cleanHTML takes care of crap coming from word 
			// this doesn't seem to work for requirements that are already in the system.
			// we also have something on the server side that tries to clean up word crap
			requirementDescription = cleanHTML(requirementDescription);

		 	
		}
		
		
		url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?";
		var params = "requirementId=" + requirementId;
		params += "&targetAttribute=" + encodeURIComponent("requirementNameAndDescription");
		params += "&targetValue=" + encodeURIComponent(requirementDescription);
		params += "&requirementName=" + encodeURIComponent(requirementName);
		params  += "&bustcache=" + new Date().getTime() ;
		document.getElementById("requirementDescriptionDiv").style.display = "block";
		document.getElementById("requirementDescriptionDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.open("POST", url, true);

		// Send the proper header information along with the request
		xmlHttpOPCenterB.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xmlHttpOPCenterB.setRequestHeader("Content-length", params.length);
		xmlHttpOPCenterB.setRequestHeader("Connection", "close");


		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("requirementDescriptionDiv").innerHTML=xmlHttpOPCenterB.responseText;

				// since a change to the name can cause the version number, approval status, approvers
				// and name to chnge, we need to refresh the core section.
				displayRequirementCore(requirementId);
				 
				
				// Editing a Req makes all traces to / from this req suspect. So
				// we refresh the traceability section.
				url="/GloreeJava2/jsp/Requirement/displayRequirementTrace.jsp?requirementId=" + requirementId;
				fillOPCenterGeneric(url, "contentCenterC");

				
				// lets also refresh the requirement in explorer
				url ="/GloreeJava2/jsp/Folder/displayARequirementInExplorer.jsp?requirementId=" + requirementId;
				url += "&bustcache=" + new Date().getTime() ;
				
				fillOPCenterGeneric(url, "objectInExplorer" + requirementId);
				
			}
		}
		xmlHttpOPCenterB.send(params);

}

	
	function setRequirementNameAndDescriptionModal(requirementId){
		
		var requirementName = document.getElementById("requirementName").value;
		
		var requirementDescription = "";
		var isMSIE = isBrowserMSIE();
		
		if (isMSIE){
			requirementDescription = document.getElementById("requirementDescription").value;
		}
		else {
		    requirementDescription = CKEDITOR.instances.requirementDescription.getData();

		 	//requirementDescription = editRequirementDescriptionRTE.getEditorHTML();
			// cleanHTML takes care of crap coming from word 
			// this doesn't seem to work for requirements that are already in the system.
			// we also have something on the server side that tries to clean up word crap
			requirementDescription = cleanHTML(requirementDescription);

		 	
		}
		
		document.getElementById("editRequirementModalMessage").style.display="block";
		document.getElementById("editRequirementModalMessage").innerHTML="Working...";
		
		
		url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?";
		var params = "requirementId=" + requirementId;
		params += "&targetAttribute=" + encodeURIComponent("requirementNameAndDescription");
		params += "&targetValue=" + encodeURIComponent(requirementDescription);
		params += "&requirementName=" + encodeURIComponent(requirementName);
		params  += "&bustcache=" + new Date().getTime() ;
		document.getElementById("requirementDescriptionDiv").style.display = "block";
		document.getElementById("requirementDescriptionDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.open("POST", url, true);

		// Send the proper header information along with the request
		xmlHttpOPCenterB.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xmlHttpOPCenterB.setRequestHeader("Content-length", params.length);
		xmlHttpOPCenterB.setRequestHeader("Connection", "close");


		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("editRequirementModalMessage").innerHTML="Your changes have been applied. You may need to refresh the underlying report to see the changes.";

				
			}
		}
		xmlHttpOPCenterB.send(params);

}



	
	function setRequirementPriority(requirementId){
		var requirementPriorityObject = document.getElementById("requirementPriority");
		var priority = requirementPriorityObject[requirementPriorityObject.selectedIndex].value;

		url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
		url += "&targetAttribute=" + encodeURIComponent("priority");
		url += "&targetValue=" + encodeURIComponent(priority);
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("requirementPriorityDiv").style.display = "block";
		document.getElementById("requirementPriorityDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "requirementPriorityDiv");
	}
	
	
	function displayRequirementOwners(requirementId){
		url="/GloreeJava2/jsp/Requirement/displayRequirementOwners.jsp?requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("requirementOwnerDropDownDiv").style.display = "block";
		document.getElementById("requirementOwnerDropDownDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "requirementOwnerDropDownDiv");
	}	
	
	function setRequirementOwner(requirementId){
		var requirementOwnerObject = document.getElementById("requirementOwner");
		var owner = requirementOwnerObject[requirementOwnerObject.selectedIndex].value;

		url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
		url += "&targetAttribute=" + encodeURIComponent("owner");
		url += "&targetValue=" + encodeURIComponent(owner);
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("requirementOwnerDiv").style.display = "block";
		document.getElementById("requirementOwnerDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "requirementOwnerDiv");
	}	
	// called when someone clicks 'Delete' button on the requirement core page.
	function deleteRequirementForm(requirementId, folderId){
		url="/GloreeJava2/jsp/Requirement/deleteRequirementForm.jsp?requirementId=" + requirementId;
		url += "&folderId=" + folderId;
		url += "&source=singleRequirement";
		url += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById("requirementPromptDiv").style.display = "block";
		fillOPCenterGeneric(url, "requirementPromptDiv");
	
	}

	// purges all the previously deleted requirements in a requirement type.
	function purgeAllDeletedRequirementsInRequirementType(requirementTypeId){
		document.getElementById("contentCenterB").style.display = "none";
		
		var url="/GloreeJava2/servlet/RequirementTypeAction?action=purgeAllDeletedRequirementsInRequirementType&" +
			"requirementTypeId=" + requirementTypeId;
		url += "&bustcache=" + new Date().getTime() ;

		document.getElementById("contentCenterF").style.display = "none";
		
		// This is a virtual folder. has -1:reqtype id in folderId param.
		// Now fill content Right with all the requirements inside this.
		fillOPCenterGeneric(url, "contentCenterF");
		
		// Since we want to display a virtual folder, we convert the req type to virtual folder
		// by using -1:reqTypeId 
		// Now fill contentCenterA with the folder details.
		var url="/GloreeJava2/jsp/Folder/displayVirtualFolder.jsp?folderId=-1:"+ requirementTypeId;
		fillOPCenterGeneric(url, "contentCenterA");

	}

	
	// called when someone confirms the click on the 'Purge' button in the display requirement core page.
	function purgeRequirement(requirementId, folderId, source){
		
		try {
		document.getElementById("returnDiv").innerHTML= "";
		document.getElementById("returnDiv").style.display='none';
		}
		catch (e){
			// do nothing.
		}
		
		// First we set the contentCenterC and D to empty.
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		
				
		// Get the Ajax response from Requirement Deletion and populate the
		// contentCenterB.
		// Note : this call has to be Synchronous , as otherwise the req may not
		// be deleted
		// by the time the fillRight happens..
		var url="/GloreeJava2/servlet/RequirementAction?action=purgeRequirement&requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				////////////////////////////////////////
				// lets refresh the explorer as the folder contents have changed
				/////////////////////////////////////////
				refreshExplorer(folderId);
				/////////////////////////////
				
				if (source == "requirementList" ){
					// this is coming fron the list of requirements in a folder

					 document.getElementById("displayRDInFolderDiv" + requirementId).style.display = "none"; 
					
					document.getElementById("displayRequirementInFolderDiv" + requirementId).innerHTML= "<span class='normalText'><font color='red'><b></b>This object has been purged (permanently deleted)</font></span>";
				}
				else {
					// this is coming from a single requirement
					document.getElementById("contentCenterB").style.display = "block";
					document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;

					displayAllRequirementsInRealFolder(folderId);
				}
				
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
	
	// called when someone clicks 'Purge' button on the requirement core page.
	function purgeRequirementForm(requirementId, folderId){
		var targetDiv = "requirementPromptDiv" + requirementId;
		
		url="/GloreeJava2/jsp/Requirement/purgeRequirementForm.jsp?requirementId=" + requirementId;
		url += "&folderId=" + folderId;
		url += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById(targetDiv).style.display = "block";
		fillOPCenterGeneric(url, targetDiv);
	}
	
	
	
	function purgeRequirementForm2(requirementId, folderId){
		
		url="/GloreeJava2/jsp/Requirement/purgeRequirementForm.jsp?requirementId=" + requirementId;
		url += "&folderId=" + folderId;
		url += "&bustcache=" + new Date().getTime() ;
		
		
		document.getElementById("requirementPromptDiv").style.display = "block";
		fillOPCenterGeneric(url, "requirementPromptDiv");

	}
	
	
	// called when someone clicks 'Move' button on the requirement core page.
	function moveRequirementForm(requirementId, folderId){
		
		var url = "/GloreeJava2/jsp/Requirement/moveRequirementForm.jsp?requirementId=" + requirementId ;
		url += "&folderId=" + folderId;
		url += "&bustcache=" + new Date().getTime() ;
		fillOPCenterGeneric(url, "requirementPromptDiv");
		
	}

	// called when someone clicks 'Copy' button on the requirement core page.
	function copyRequirementForm(requirementId){
		
		var url = "/GloreeJava2/jsp/Requirement/CopyRequirement/copyRequirementForm.jsp?requirementId=" + requirementId ;
		url += "&bustcache=" + new Date().getTime() ;
		fillOPCenterGeneric(url, "requirementPromptDiv");
	}

	// called when someone clicks 'Copy' button report bulk edit page.
	function copyRequirementFormInBulkEdit(folderId){
		
		var url = "/GloreeJava2/jsp/Report/BulkEdit/copyRequirementFormInBulkEdit.jsp?";
		url += "&folderId=" + folderId ;
		url += "&bustcache=" + new Date().getTime() ;
		fillOPCenterGeneric(url, "copyRequirementInBulkEditPrompt");
	}

	function copyRequirementsToTargetProjectForm(requirementId){
		var targetProjectObject = document.getElementById("targetProject");
		var targetProjectId= targetProjectObject[targetProjectObject.selectedIndex].value;

		var url = "/GloreeJava2/jsp/Requirement/CopyRequirement/copyRequirementToTargetProjectForm.jsp?requirementId=" + requirementId ;
		url += "&targetProjectId=" + targetProjectId;
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("copyRequirementsToTargetProjectDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "copyRequirementsToTargetProjectDiv");
	}

	function copyRequirementsToTargetProjectFormInBulkEdit(folderId){
		var targetProjectObject = document.getElementById("targetProject");
		var targetProjectId= targetProjectObject[targetProjectObject.selectedIndex].value;

		var url = "/GloreeJava2/jsp/Report/BulkEdit/copyRequirementToTargetProjectFormInBulkEdit.jsp?";
		url += "&targetProjectId=" + targetProjectId;
		url += "&folderId=" + folderId;
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("copyRequirementsToTargetProjectDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "copyRequirementsToTargetProjectDiv");
	}

	// called when someone decides to copy a Requirement to a new location
	function copyRequirement(requirementId, targetProjectId){

		var copyFolderObject = document.getElementById("copyFolder");
		var copyFolderId= copyFolderObject[copyFolderObject.selectedIndex].value;
		if (copyFolderObject.selectedIndex == 0) {
			alert ("Please select a valid Target Folder");
			copyFolderObject.focus();
			copyFolderObject.style.backgroundColor="#FFCC99";
			return;
		}
		
		
		
		
		var createTraceToSource= "no";
		
		var createTraceToSourceObject = document.getElementById("createTraceToSource");
		if (createTraceToSourceObject != null ){
			createTraceToSource= createTraceToSourceObject[createTraceToSourceObject.selectedIndex].value;
		}
		
		var createTraceFromSource= "no";
		
		var createTraceFromSourceObject = document.getElementById("createTraceFromSource");
		if (createTraceFromSourceObject != null ){
			createTraceFromSource= createTraceFromSourceObject[createTraceFromSourceObject.selectedIndex].value;
		}
		
		if ((createTraceToSource == "yes") && (createTraceFromSource == "yes")){
			alert ("Creating Trace To and Trace From the new object will create a circular trace. This is not permitted");
			createTraceFromSourceObject.focus();
			createTraceFromSourceObject.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		var copyCommonAttributesObject = document.getElementById("copyCommonAttributes");
		var copyCommonAttributes= copyCommonAttributesObject[copyCommonAttributesObject.selectedIndex].value;
		
		
		

		var copyTraceabilityObject = document.getElementById("copyTraceability");
		var copyTraceability= copyTraceabilityObject[copyTraceabilityObject.selectedIndex].value;


		
		// build the url
		var url="/GloreeJava2/servlet/RequirementAction?action=copyRequirement&requirementId=" + requirementId;
		url += "&copyFolderId=" + copyFolderId;
		url += "&targetProjectId=" + targetProjectId;
		url += "&createTraceToSource=" + createTraceToSource;
		url += "&createTraceFromSource=" + createTraceFromSource;
		
		url += "&copyCommonAttributes=" + copyCommonAttributes;
		url += "&copyTraceability=" + copyTraceability;
		url += "&bustcache=" + new Date().getTime() ;

		
		// make the AJAX call
		document.getElementById("copyAcrossProjectDiv").style.display = "none";
		
		document.getElementById("copyRequirementsToTargetProjectDiv").style.display = "block";
		document.getElementById("copyRequirementsToTargetProjectDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// We set the prompt with the response value.
				document.getElementById("copyRequirementsToTargetProjectDiv").innerHTML = xmlHttpOPCenterB.responseText;
				document.getElementById("copyRequirementsToTargetProjectDiv").style.display = "block";
				
				
				// lets refresh the traceability section of the requirement.
				// Now we build the traceability URL and redirect OPCenterC to it.
				url="/GloreeJava2/jsp/Requirement/displayRequirementTrace.jsp?requirementId=" + requirementId;
				fillOPCenterGeneric(url, "contentCenterC");
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
	}

	
	// called when someone hits the 'Change Log' button in the Requirement Core.
	function displayRequirementChangeLog(requirementId){
		var url = "/GloreeJava2/jsp/Requirement/displayRequirementChangeLog.jsp?requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		fillOPCenterGeneric(url, "requirementPromptDiv");
	}

	// called when someone hits the 'Version' button in the Requirement Core.
	function displayRequirementVersionHistory(requirementId){
		var url = "/GloreeJava2/jsp/Requirement/displayRequirementVersionHistory.jsp?requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		fillOPCenterGeneric(url, "requirementPromptDiv");
	}


	// called when someone hits the 'Change Log' button in the Requirement Core.
	function displayRequirementComments(requirementId){
		var url = "/GloreeJava2/jsp/Requirement/displayRequirementComments.jsp?requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		fillOPCenterGeneric(url, "contentCenterComments");
		document.getElementById("contentCenterB").style.display="none";
		document.getElementById("contentCenterD").style.display="none";
		document.getElementById("contentCenterC").style.display="none";
		document.getElementById("contentCenterAttachments").style.display="none";
		
		$("#coreTab").removeClass('active');
		$("#commentsTab").addClass('active');
		
		
	}

	// called when someone hits the 'Enter' button in the addRequirementComments section.
	// we capture the enter key event and treat it as if some one pressed 'add comment' button.
	function  handleAddRequirementCommentkeyPress(event, requirementId, folderId) {
		var keyCode = event.keyCode;
		if (keyCode == 13) {
			addRequirementComment(requirementId, folderId);
			return(0);
		}
	}
	
	
	function  handleAddRequirementCommentkeyPress(event, requirementId, folderId, source ) {
		var keyCode = event.keyCode;
		if (keyCode == 13) {
			addRequirementComment(requirementId, folderId, source);
			return(0);
		}
	}
	
	
	// called when someone hits the 'Change Log' button in the Requirement Core.
	function addRequirementComment(requirementId, folderId, source){
		var commentNoteObject  = document.getElementById("comment_note" + requirementId);
		
		if (commentNoteObject.value.length == 0) {
			alert ("Please enter a Comment");
			commentNoteObject.focus();
			commentNoteObject.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (commentNoteObject.value.length > 3000){
			alert(" Please ensure that your Descriptoin is not longer than 1000 characters. Your current message is " + commentNoteObject.value.length + " characters long");
			commentNoteObject.focus();
			commentNoteObject.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		var comment_note = commentNoteObject.value;
		
		var url="/GloreeJava2/servlet/RequirementAction?";
		url += "action=addRequirementComment";
		url += "&requirementId=" + requirementId;
		url += "&folderId=" + folderId; 
		url += "&source=" + source;
		url += "&comment_note=" + encodeURIComponent(comment_note);
		
		document.getElementById("addComment" + requirementId).disabled = true;
		var commentListDivObject  = document.getElementById("commentListDiv" + requirementId);
		
		document.getElementById("commentListDiv" + requirementId).innerHTML= "<span class='normalText'> &nbsp;&nbsp;&nbsp;Working...</span>";
		
		if (source == "requirementList" ){
			// this is coming fron the list of requirements in a folder
			xmlHttpOPCenterB =GetXmlHttpObject();			
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					document.getElementById("displayRDInFolderDiv" + requirementId).style.display = "block";
					document.getElementById("displayRDInFolderDiv" + requirementId).innerHTML=xmlHttpOPCenterB.responseText;
					
					// lets enable the add comment button
					document.getElementById("addComment" + requirementId).disabled = false;
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);
		}
		else {
			// this is coming from a single requirement
			
			xmlHttpOPCenterB =GetXmlHttpObject();			
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// lets display the requirement comments
					url = "/GloreeJava2/jsp/Requirement/displayRequirementComments.jsp?requirementId=" + requirementId;
					fillOPCenterGenericNoDivDisplay(url, "contentCenterComments");
				
					/*document.getElementById("requirementPromptDiv" ).style.display = "block";
					document.getElementById("requirementPromptDiv" ).innerHTML=xmlHttpOPCenterB.responseText;
					// lets enable the add comment button
					document.getElementById("addComment" + requirementId).disabled = false;
					*/
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);
			
		}
		
	}

	function deleteComment(commentId, requirementId, source){
		
		var url="/GloreeJava2/servlet/RequirementAction?";
		url += "action=deleteComment";
		url += "&commentId=" + commentId;
		url += "&requirementId=" + requirementId;
		url += "&source=requirementList" ;
		
		if (source == "requirementList"){
			var commentListDivObject  = document.getElementById("commentListDiv" + requirementId);
			document.getElementById("commentListDiv" + requirementId).innerHTML= "<span class='normalText'> &nbsp;&nbsp;&nbsp;Working...</span>";
		}
		// this is coming fron the list of requirements in a folder
		xmlHttpOPCenterB =GetXmlHttpObject();			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				if (source == "requirementList"){
					document.getElementById("displayRDInFolderDiv" + requirementId).style.display = "block";
					document.getElementById("displayRDInFolderDiv" + requirementId).innerHTML=xmlHttpOPCenterB.responseText;
				}
				else {
					// lets display the requirement comments in the requirement page
					url = "/GloreeJava2/jsp/Requirement/displayRequirementComments.jsp?requirementId=" + requirementId;
					fillOPCenterGenericNoDivDisplay(url, "contentCenterComments");	
				}
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	
		
	}

	function resetAnotherUsersPassword(resetUserId, resetUserName){
		var newPassword = prompt("Please enter new password for " + resetUserName , "");
		if (newPassword == null) {
		    alert("Password not reset")
		}
		
		// lets make the call to reset the password for the user
		
		var url="/GloreeJava2/servlet/UserAccountAction?";
		url += "action=resetAnotherUsersPassword";
		url += "&resetUserId=" + resetUserId;
		url += "&newPassword=" + newPassword; 
		
		
		// this is coming fron the list of requirements in a folder
		xmlHttpOPCenterB =GetXmlHttpObject();			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				var responseText =xmlHttpOPCenterB.responseText;
				alert(responseText);
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	
	}

	
	// called when someone hits the 'attach files' link in the Requirement Core.
	function addRequirementAttachmentForm(requirementId, folderId){
		var url = "/GloreeJava2/jsp/Requirement/addRequirementAttachmentForm.jsp?requirementId=" + requirementId;
		url += "&folderId=" + folderId;
		fillOPCenterGeneric(url, "addRequirementAttachmentsDiv");
		
		$('#contentCenterAttachments').style.display = "block";
		$("#coreTab").removeClass('active');
		$("#attachmentsTab").addClass('active');
	}
	
	function addRequirementAttachmentFormOpen(requirementId, folderId, webFormId){
		var url = "/GloreeJava2/jsp/Requirement/addRequirementAttachmentFormOpen.jsp?requirementId=" + requirementId;
		url += "&folderId=" + folderId;
		url += "&webFormId=" + webFormId;
		fillOPCenterGeneric(url, "requirementPromptDiv");
	}
	
	// gets called when a user uploads a file in the add attachment screen.
	function addRequirementAttachment(thisForm) {
		var title = thisForm.title;
		if ((title.value == null) || (title.value.length == 0)){
			alert(" Please enter a Title for the attached file");
			title.focus();
			title.style.backgroundColor="#FFCC99";
			return(0);
		}
		var attachment = thisForm.attachment;
		
		if ((attachment.value == null) || (attachment.value.length == 0)){
			alert(" Please select a file to upload");
			attachment.focus();
			attachment.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		//now that we are ready upload file, lets gray out the
		// submit button to prevent accidental resubmits.
		document.getElementById("uploadFileButton").disabled=true;
		
		thisForm.submit();
	}

	function updateRequirementAttachmentForm(attachmentId, requirementId, folderId){
		var url = "/GloreeJava2/jsp/Requirement/updateRequirementAttachmentForm.jsp?requirementId=" + requirementId;
		url += "&attachmentId=" + attachmentId;
		url += "&folderId=" + folderId;
		var targetDiv = "updateAttachment" + attachmentId + "Div";
		fillOPCenterGeneric(url, targetDiv);
	}
	
	// gets called when a user uploads a file in the add attachment screen.
	function updateRequirementAttachment(thisForm) {
		// submit button to prevent accidental resubmits.
		document.getElementById("uploadFileButton").disabled=true;
		
		thisForm.submit();
	}

	function updateRequirementAttachmentDescription( requirementId, attachmentId){
		
		

		var url="/GloreeJava2/servlet/RequirementAction?action=updateRequirementAttachmentDescription&requirementId=" + requirementId;
		url += "&attachmentId=" + attachmentId;
		url += "&title=" + encodeURIComponent(document.getElementById("requirementAttachmentDescription").value);
		url += "&bustcache=" + new Date().getTime() ;
		// make the AJAX call
		var targetDiv = "updateAttachmentDescription"+ attachmentId +"Div";
		fillOPCenterGeneric(url, targetDiv);
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// lets refresh the core.
				displayRequirementCore(requirementId);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
	}
		
	function addExistingFilesToRequirement(thisForm) {
		// lets make sure at least 1 is selected
		
		var totalSelected = 0;
		var addExistingFilesString = "";
		var addExistingFiles =document.getElementById("addExistingFiles");
		  for (var i = 0; i < addExistingFiles.options.length; i++) {
		     if(addExistingFiles.options[i].selected ==true){
		         totalSelected++;
		         addExistingFilesString += addExistingFiles.options[i].value + ",";
		      }
		 }
		if (totalSelected === 0 ){
			alert(" Please select at least 1 existing file");
			addExistingFiles.focus();
			addExistingFiles.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		document.getElementById("addExistingFilesHidden").value= addExistingFilesString;
		
		thisForm.submit();
	}


	function deleteRequirementAttachment(requirementId, attachmentId){
		
		document.getElementById("contentCenterB").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		// build the url		
		var url="/GloreeJava2/servlet/RequirementAction?action=deleteRequirementAttachment&attachmentId=" + attachmentId;
		url += "&bustcache=" + new Date().getTime() ;
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				//displayRequirementCore(requirementId); 
				displayRequirement(requirementId,"",""); 
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
			
	}

	
	function displayURL(displayURL){
		
		var prompt = " " + 
			" <div id='displayURLDiv'  class='alert alert-success'> " + 
			"	<div style='float:right;'> " +
			"		<a href='#'	onclick= " + 
			"			'document.getElementById(\"displayURLDiv\").style.display = \"none\";'> " +
			"		Close</a> " +
			"	</div> " +
			"	<span class='normalText'> " +
			"	URL to this Requirement is : <br><br> <a href='"+ displayURL +"' target='_blank'>" + displayURL + "</a> "
			"	</span> " +
			" </div> ";	
		// We set the prompt with the appropriate values.
		document.getElementById("requirementPromptDiv").innerHTML = prompt;
		document.getElementById("requirementPromptDiv").style.display = "block";
		
	}
	

	// called when someone hits  the 'Move' button in the requirment move prompt page.
	function moveRequirement(cRF, source){
		
		
		// get the requirement id and the folder id it should move to.

		var requirementId = cRF.requirementId.value;
		var currentFolderId = cRF.currentFolderId.value;
		var moveFolder = cRF.moveFolder;
		var folderId = moveFolder[moveFolder.selectedIndex].value
		
		if (folderId == 0){
			// the user is trying to move requirements to a folder to which he / she doesn't have 
			alert ("You do not have CREATE REQUIREMENTS privileges on this folder. Hence you can not move objects to this folder. Please select another folder");
			moveFolder.focus();
			moveFolder.style.backgroundColor="#FFCC99";
			return;
		}
		
		

		// First we set the contentCenterC and D to empty.
		
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("moveButton" + requirementId).disabled=true;
		
		
		// build the url
		var url = "/GloreeJava2/jsp/Requirement/moveRequirementConfirmation.jsp?requirementId=" + requirementId;
		url += "&folderId=" + folderId;
		
		
		if (source == "requirementList" ){
			// this is coming fron the list of requirements in a folder

			document.getElementById("displayRDInFolderDiv" + requirementId).style.display = "none"; 
			
			fillOPCenterGeneric(url, "displayRequirementInFolderDiv" + requirementId);
		}
		else {
			// make the AJAX call
			xmlHttpOPCenterB =GetXmlHttpObject();
				
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					document.getElementById("contentCenterB").style.display = "block";
					document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;

					// Now that this requirement has been moved to a different
					// fodler
					// we need to refresh the contents of the current folder to
					// display the contents
					// minus this req.
					displayAllRequirementsInRealFolder(currentFolderId);

				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);
		}
		
		
	}

	// called when someone clicks the 'Restore' button on a displayRequirementCoreDel.jsp form. 
	function restoreRequirement(requirementId, folderId){
		
		// First we set the contentCenterC and D to empty.
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		
				
		// Get the Ajax response from Requirement Restore and populate the
		// contentCenterB.
		// Note : this call has to be Synchronous , as otherwise the req may not
		// be restored
		// by the time the fillRight happens..
		var url="/GloreeJava2/servlet/RequirementAction?action=restoreRequirement&requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterB").style.display = "block";
				document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;

				displayFolderInExplorer(folderId);
				
				

				// Display the folder , the restored req is in.
				url="/GloreeJava2/jsp/Folder/displayRealFolder.jsp?folderId="+ folderId;
				fillOPCenterGeneric(url, "contentCenterA");

				// Now we build the traceability URL and redirect OPCenterC to
				// it.
				url="/GloreeJava2/jsp/Requirement/displayRequirementTrace.jsp?requirementId=" + requirementId;
				fillOPCenterGeneric(url, "contentCenterC");
				
				// Now we build the attribute URL and redirect OPCenterD to it.
				url="/GloreeJava2/jsp/Requirement/displayRequirementAttributeValue.jsp?requirementId=" + requirementId;
				fillOPCenterGeneric(url, "contentCenterD");


			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}


	function updateRequirementTestingStatus(requirementId, createDefect){
		var manualTestingStatusObject = document.getElementById("manualTestingStatus");
		var manualTestingStatus = manualTestingStatusObject[manualTestingStatusObject.selectedIndex].value
		
		document.getElementById("contentCenterB").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		// build the url		
		var url="/GloreeJava2/servlet/RequirementAction?action=changeManualTestingStatus&requirementId=" + requirementId;
		url += "&manualTestingStatus=" + encodeURIComponent(manualTestingStatus) ;
		url += "&createDefect=" + encodeURIComponent(createDefect);
		url += "&bustcache=" + new Date().getTime() ;
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				displayRequirementCore(requirementId);
				if (createDefect == "yes"){
					// if the user has chosen to create a defect, this would 
					// create a defect and a trace to this req. So lets refresh
					// the traceability screen.
					traceUrl="/GloreeJava2/jsp/Requirement/displayRequirementTrace.jsp?requirementId=" + requirementId;
					fillOPCenterGeneric(traceUrl, "contentCenterC");
				}
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
			
	}

	// called when someone clicks the 'Submit Req for Approval' button 
	// on a req in 'Draft' Status.
	function submitRequirementForApproval(requirementId, folderId){
		 
		var url="/GloreeJava2/servlet/RequirementAction?action=submitRequirementForApproval&requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		// make the AJAX call
		xmlHttpOPCenterB =GetXmlHttpObject();
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				var response = xmlHttpOPCenterB.responseText;
				// lets see if the response had 'approversDoNotExist' in it.
				var pos= response.indexOf("approversDoNotExist");
				if (pos>=0){
					// if req was submitted for approval, and there are no 
					// approvers assigned to this folder, lets show a small message.
					var prompt= "<div id='approversDoNotExistDiv' class='alert alert-success'>";
					prompt += "<div style='float:right'> ";
					prompt += "<a href='#' onclick='document.getElementById(\"requirementPromptDiv\").style.display=\"none\";'> Close </a>";
					prompt += "</div><span class='headingText'><br><br>";
					prompt += "There are  no approvers assigned to this Folder. Please work with your Project " ; 
					prompt += " Administrator to have some Approvers assigned to this Folder prior to submitting " ;
					prompt += " this Requirement for approval."
					prompt += "</span></b>"
					prompt += "\n<br></div><br><br>"
					// We set the prompt with the appropriate values.
					document.getElementById("requirementPromptDiv").innerHTML = prompt;
					document.getElementById("requirementPromptDiv").style.display = "block";					
					
				}
				var pos= response.indexOf("approversExist");
				if (pos>=0){				
					// if req was submitted for approval, and there do exist approvers
					// then lets display the Req Core, as this will have the lastest
					// approval status etc..
					// forward to displayRequirementCore
					url="/GloreeJava2/jsp/Requirement/displayRequirementCore.jsp?requirementId="+ requirementId;
					fillOPCenterGeneric(url, "contentCenterB");
					
				}
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
	}
	

	function remindApprovers(requirementId){
		 
		// lets make a call to RequirementAction to remind approvers, and then refresh displayRequirementCore.
		
		var url="/GloreeJava2/servlet/RequirementAction?action=remindApprovers&requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		// make the AJAX call
		xmlHttpOPCenterB =GetXmlHttpObject();
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// now lets refresh the core. 
				displayRequirementCore(requirementId)
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
	}
	


	function remindApproversInFolderList(requirementId, folderId){
		 
		// lets make a call to RequirementAction to remind approvers, and then refresh displayRequirementCore.
		
		var url="/GloreeJava2/servlet/RequirementAction?action=remindApprovers&requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		// make the AJAX call
		xmlHttpOPCenterB =GetXmlHttpObject();
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// now lets refresh the requirement description. 
				displayRequirementDetails(requirementId,folderId);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
	}
	
	

	function remindApproversForMyTasks(requirementId){
		 
		// lets make a call to RequirementAction to remind approvers, and then refresh displayRequirementCore.
		
		var url="/GloreeJava2/servlet/RequirementAction?action=remindApprovers&requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;

		var daysSinceLastReminderDiv = document.getElementById('daysSinceLastReminder' +  requirementId + 'Div') ;
		daysSinceLastReminderDiv.innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		document.getElementById('remindApprovers' + requirementId + 'Button').disabled=true;

		// make the AJAX call
		xmlHttpOPCenterB =GetXmlHttpObject();
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// now lets refresh the div that holds the data. 
				daysSinceLastReminderDiv.innerHTML = "<span class='normalText' style='background-color:#99ccff' title='Last Reminder sent 0 days ago '>0 days </span>";
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
	}
	
	function displayApproversForThisRequirement(requirementId){
		 
		var url="/GloreeJava2/servlet/RequirementAction?action=displayApproversForThisRequirement&requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById("requirementPromptDiv").style.display = "block";					
		document.getElementById("requirementPromptDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";

		fillOPCenterGeneric(url, "requirementPromptDiv");		
	}
	
	
	// called when someone clicks the 'Approve or Reject' button on a displayRequirement form. 
	function approvalWorkFlowAction(approvalNoteValue,requirementId, approvalAction){		
		
		var approvalNoteObjectId = "approvalNote" + requirementId;
		if ( (approvalAction == 'reject' )
			&&
			(approvalNoteValue.length == 0) 
		){
			// to reject a requirement, you have to provide a note
			alert ("Please enter a reason for rejection");
			document.getElementById(approvalNoteObjectId).focus();
			document.getElementById(approvalNoteObjectId).style.backgroundColor="#FFCC99";
			return;
		}
		
		if ((approvalNoteValue.length > 0) ){
			if (approvalAction == 'reject' ) {
				approvalNoteValue = "Rejected : " + approvalNoteValue;
			}
			if (approvalAction == 'approve' ) {
				approvalNoteValue = "Approved : " + approvalNoteValue;
			}
			
		}
		// First we set the contentCenterC and D to empty.
		document.getElementById("contentCenterB").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";

		// Get the Ajax response from Requirement Action for approval and
		// populate the
		// contentCenterB.
		var url="/GloreeJava2/servlet/RequirementAction?action=approvalWorkFlowAction&requirementId=" + requirementId;
		url += "&approvalAction=" + encodeURIComponent(approvalAction) ;
		url += "&approvalNote=" + encodeURIComponent(approvalNoteValue) ;
		
		fillOPCenterGeneric(url, "contentCenterB");
	}

	
	// this is a variation of ApprovalWorkFlowAction. Just handles the post effects differently.
	function approvalWorkFlowActionForMyTasks(approvalNoteValue,requirementId, approvalAction){		
		
		var approvalNoteObjectId = "approvalNote" + requirementId;
		if ( (approvalAction == 'reject' )
			&&
			(approvalNoteValue.length == 0) 
		){
			// to reject a requirement, you have to provide a note
			alert ("Please enter a reason for rejection");
			document.getElementById(approvalNoteObjectId).focus();
			document.getElementById(approvalNoteObjectId).style.backgroundColor="#FFCC99";
			return;
		}
		
		if ((approvalNoteValue.length > 0) ){
			if (approvalAction == 'reject' ) {
				approvalNoteValue = "Rejected : " + approvalNoteValue;
			}
			if (approvalAction == 'approve' ) {
				approvalNoteValue = "Approved : " + approvalNoteValue;
			}
			
		}
		var approveRejectDiv = "approveReject" + requirementId + "Div";
		// First we set the contentCenterC and D to empty.
		document.getElementById(approveRejectDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";

		// Get the Ajax response from Requirement Action for approval and
		// populate the
		// contentCenterB.
		var url="/GloreeJava2/servlet/RequirementAction?action=approvalWorkFlowActionForMyTasks&requirementId=" + requirementId;
		url += "&approvalAction=" + encodeURIComponent(approvalAction) ;
		url += "&approvalNote=" + encodeURIComponent(approvalNoteValue) ;
		url += "&bustcache=" + new Date().getTime() ;
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// Once we hear back from approval action, lets remove the 'button'.
				document.getElementById("approveRejectButtonDiv"  + requirementId).innerHTML = xmlHttpOPCenterB.responseText;
				document.getElementById(approveRejectDiv).style.display = "none";
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
		
		
	}


	// this is a variation of ApprovalWorkFlowAction. Just handles the post effects differently.
	function approvalWorkFlowActionForListInFolder(approvalNoteValue,requirementId, approvalAction, folderId){		
		
		var approvalNoteObjectId = "approvalNoteInFolderList" + requirementId;
		if ( (approvalAction == 'reject' )
			&&
			(approvalNoteValue.length == 0) 
		){
			// to reject a requirement, you have to provide a note
			alert ("Please enter a reason for rejection");
			document.getElementById(approvalNoteObjectId).focus();
			document.getElementById(approvalNoteObjectId).style.backgroundColor="#FFCC99";
			return;
		}
		
		if ((approvalNoteValue.length > 0) ){
			if (approvalAction == 'reject' ) {
				approvalNoteValue = "Rejected : " + approvalNoteValue;
			}
			if (approvalAction == 'approve' ) {
				approvalNoteValue = "Approved : " + approvalNoteValue;
			}
			
		}
		var  displayRequirementInFolderDiv = "displayRequirementInFolderDiv" + requirementId;
		// First we set the contentCenterC and D to empty.
		document.getElementById(displayRequirementInFolderDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";

		// Get the Ajax response from Requirement Action for approval and
		// populate the
		// contentCenterB.
		var url="/GloreeJava2/servlet/RequirementAction?action=approvalWorkFlowActionForMyTasks&requirementId=" + requirementId;
		url += "&approvalAction=" + encodeURIComponent(approvalAction) ;
		url += "&approvalNote=" + encodeURIComponent(approvalNoteValue) ;
		url += "&bustcache=" + new Date().getTime() ;
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// once approval action is done, lets refresh the line item.
				url="/GloreeJava2/jsp/Folder/displayARequirementInRealFolder.jsp?requirementId=" + requirementId;
				url += "&bustcache=" + new Date().getTime() ;
				document.getElementById(displayRequirementInFolderDiv).innerHTML = "&nbsp;&nbsp;&nbsp;Working...";
				fillOPCenterGeneric(url, displayRequirementInFolderDiv);
			
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
		
		
	}


	function requirementApprovalAction(requirementId, approvalAction){		
		var approvalNoteValue = document.getElementById("approvalNote").value;
		if ( (approvalAction == 'reject' )
			&&
			(approvalNoteValue.length == 0) 
		){
			// to reject a requirement, you have to provide a note
			alert ("Please enter a reason for rejection");
			document.getElementById("approvalNote").focus();
			document.getElementById("approvalNote").style.backgroundColor="#FFCC99";
			return;
		}
		
		if ((approvalNoteValue.length > 0) ){
			if (approvalAction == 'reject' ) {
				approvalNoteValue = "Rejected : " + approvalNoteValue;
			}
		}
		// First we set the contentCenterC and D to empty.
		document.getElementById("requirementApprovalActionDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";

		// Get the Ajax response from Requirement Action for approval and
		// populate the
		// contentCenterB.
		  
		var url="/GloreeJava2/jsp/Requirement/requirementApprovalAction.jsp?requirementId=" + requirementId;
		url += "&approvalAction=" + encodeURIComponent(approvalAction) ;
		url += "&approvalNote=" + encodeURIComponent(approvalNoteValue) ;
		
		
		fillOPCenterGeneric(url, "requirementApprovalActionDiv");
	}
	
	// called when someone clicks 'Add To Baseline' button on the requirement core page.
	function addToBaselineForm(requirementId, folderId){
		
		var url = "/GloreeJava2/jsp/Requirement/addToBaselineForm.jsp?requirementId=" + requirementId ;
		url += "&folderId=" + folderId;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// We set the prompt with the response value.
				document.getElementById("requirementPromptDiv").innerHTML = xmlHttpOPCenterB.responseText;
				document.getElementById("requirementPromptDiv").style.display = "block";
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
	
	
	// called when someone hits  the 'Add To Baseline' button in the 'Add To Baseline Form' of req core.
	function addToBaseline(cRF){
		
		// get the requirement id and the folder id it should move to.

		var requirementId = cRF.requirementId.value;
		var folderId = cRF.folderId.value;
		var baseline = cRF.baseline;
		var baselineId = baseline[baseline.selectedIndex].value
		
		// build the url		
		var url="/GloreeJava2/servlet/RequirementAction?action=addRequirementToBaseline&requirementId=" + requirementId;
		url += "&folderId=" + folderId;
		url += "&rTBaselineId=" + baselineId;
		fillOPCenterGeneric(url, "contentCenterB");		
	}

	// called when someone clicks 'Add To Baseline' button on the requirement core page.
	function displayRequirementBaseline(baselineId, requirementId){
		
		var url = "/GloreeJava2/jsp/Requirement/displayRequirementBaseline.jsp?requirementId=" + requirementId ;
		url += "&baselineId=" + baselineId;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "displayRequiermentBaselineDiv");	
	}
	
	// called when someone clicks 'Add To Baseline' button on the requirement core page.
	function removeRequirementFromBaseline(requirementId, requirementBaselineId){
		var url="/GloreeJava2/servlet/RequirementAction?action=removeRequirementFromBaseline&requirementId=" + requirementId;
		url += "&requirementBaselineId=" + requirementBaselineId;
		fillOPCenterGeneric(url, "contentCenterB")
	}
	
	//////////////////////////////////////////////////////////////////////////
	// //
	// Traces //
	// //
	// ////////////////////////////////////////////////////////////////////////
	
	// called when someone hits the 'Enter' button in the Create Traces section.
	// we capture the enter key event and treat it as if some one pressed 'create traces' button.
	function  handleCreateTraceskeyPress(event, requirementId) {
		var keyCode = event.keyCode;
		if (keyCode == 13) {
			createTraces(requirementId);
			return(0);
		}
	}
	
	
	function createTraces(requirementId){
		var cRF = document.getElementById("requirementTrace");

		// lets disable the createTraces button to prevent accidental double clicking.
		document.getElementById("createTracesButton").disabled = true;
		
		var traceTo = cRF.traceTo;
		var traceFrom = cRF.traceFrom;
		
		var url="/GloreeJava2/servlet/RequirementAction?action=createTraces&requirementId=" + requirementId + "&createTraceTo=" + traceTo.value  ;
		url = url + "&createTraceFrom=" + traceFrom.value;
		
		// Get the Ajax response from RequirementAction 'Traceto' , 'TraceFrom'
		// and populate the ContentCenterC
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterC").style.display = "block";
				document.getElementById("contentCenterC").innerHTML=xmlHttpOPCenterB.responseText;
				// since traceabiity changes may impact defects displayed, we will refresh core.
				displayRequirementCore(requirementId);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
	
	function createTraceFromCallingRequirementId(callingRequirementId,messageDivId, traceToRequirementFullTag, source ){
	
		
		var url="/GloreeJava2/servlet/RequirementAction?action=createTraces2&requirementId=" + callingRequirementId 
			+ "&createTraceTo=" + traceToRequirementFullTag  
			+ "&bustcache=" + new Date().getTime() ;
		
		// Get the Ajax response from RequirementAction 'Traceto' , 'TraceFrom'
		// and populate the ContentCenterC
		
		// lets update the backend

		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				


				if (source=="Requirement"){
					// non modal

					

					var messageDiv = "messageDivId" + messageDivId;
					//document.getElementById(messageDiv).style.display = "block";					
					document.getElementById(messageDiv).innerHTML=xmlHttpOPCenterB.responseText;
					// since traceabiity changes may impact defects displayed, we will refresh core.
					//displayRequirementCore(callingRequirementId);				
					
					// lets display the requirement trace section
					url="/GloreeJava2/jsp/Requirement/displayRequirementTrace.jsp?requirementId=" + callingRequirementId;
					fillOPCenterGeneric(url, "contentCenterC");
				}
				else{
					// modal

					var messageDiv = "messageDivId" + messageDivId;
					
					document.getElementById(messageDiv).style.display = "block";					
					document.getElementById(messageDiv).innerHTML=xmlHttpOPCenterB.responseText;
					// lets refresh the trace bar
					refreshTraceActionBar(callingRequirementId)					
				}

		
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		



	}	

	function createTraceToCallingRequirementId(callingRequirementId,messageDivId, traceFromRequirementFullTag , source){
	
		
		var url="/GloreeJava2/servlet/RequirementAction?action=createTraces2&requirementId=" + callingRequirementId 
			+ "&createTraceFrom=" + traceFromRequirementFullTag    
			+ "&bustcache=" + new Date().getTime() ;
		
		// Get the Ajax response from RequirementAction 'Traceto' , 'TraceFrom'
		// and populate the ContentCenterC
		
		// lets update the backend

		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				

				
				
				
				if (source=="Requirement"){

					var messageDiv = "messageDivId" + messageDivId;

					//document.getElementById(messageDiv).style.display = "block";					
					document.getElementById(messageDiv).innerHTML=xmlHttpOPCenterB.responseText;
					// since traceabiity changes may impact defects displayed, we will refresh core.
					//displayRequirementCore(callingRequirementId);				
					
					// lets display the requirement trace section
					url="/GloreeJava2/jsp/Requirement/displayRequirementTrace.jsp?requirementId=" + callingRequirementId;
					fillOPCenterGeneric(url, "contentCenterC");	
				}
				else{
					// modal

					var messageDiv = "messageDivId" + messageDivId;
					document.getElementById(messageDiv).style.display = "block";					
					document.getElementById(messageDiv).innerHTML=xmlHttpOPCenterB.responseText;
					// lets refresh the trace bar
					refreshTraceActionBar(callingRequirementId)
				}


			
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		


	}	
	
	
	function loadCreateTrace2(callingRequirementId, source){
		var searchProjectIdObject = document.getElementById('searchProjectId');
		var searchProjectId =  searchProjectIdObject[searchProjectIdObject.selectedIndex].value	;
		createTraces2(callingRequirementId, searchProjectId, source);
	}
	
	function createTraces2(callingRequirementId, searchProjectId, source){
		// lets hide the 'create attributes' div
		
		
		var url="/GloreeJava2/jsp/Requirement/displayRequirementSearchForm.jsp?callingRequirementId=" 
			+ callingRequirementId ;
		url += "&searchProjectId=" + searchProjectId;
		url += "&source=" + source;
		 
		url += "&bustcache=" + new Date().getTime() ;
		
		

		//document.getElementById("contentCenterB").style.display='none';
		//document.getElementById("contentCenterD").style.display='none';
		/*
		$("#contentCenterB").hide(100);
		$("#contentCenterD").hide(100);
        
		
        $("#createTracesDiv").show(1000);
        document.getElementById("createTracesDiv").style.display='block';
		*/
		
		document.getElementById("createTracesDiv").innerHTML= "<span class='normalText'> &nbsp;&nbsp;&nbsp;Working...</span>";
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				/*if (document.getElementById("createTracesDiv").style.display=='block'){
					document.getElementById("createTracesDiv").innerHTML=xmlHttpOPCenterB.responseText;
				}
				
				*/

				document.getElementById("modalBody").innerHTML=xmlHttpOPCenterB.responseText;
				
				
				document.getElementById("reqTraceId").focus();
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);

	}	
	
	function createTracesQuick(callingRequirementId, searchProjectId){
		// lets hide the 'create attributes' div
		
		
		var url="/GloreeJava2/jsp/Requirement/displayRequirementSearchFormQuick.jsp?callingRequirementId=" 
			+ callingRequirementId ;
		url += "&searchProjectId=" + searchProjectId;
		url += "&bustcache=" + new Date().getTime() ;
		
		


		fillOPCenterGeneric(url, "quickTraceDiv");


	}	
		
	// called when someone chooses to take some action on a trace.
	function modifyTrace(traceActionObject,divId,traceId, requirementId){

		// lets disable the traceAction select object to prevent accidental double clicks.
		traceActionObject.disabled = true;
		
		var url="/GloreeJava2/servlet/TraceAction?action=" + traceActionObject[traceActionObject.selectedIndex].value;
		url = url + "&traceId=" + traceId;
		url = url + "&requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterC").style.display = "block";
				document.getElementById("contentCenterC").innerHTML=xmlHttpOPCenterB.responseText;
				// since traceabiity changes may impact defects displayed, we will refresh core.
				displayRequirementCore(requirementId);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
	
	function modifyTrace2(traceAction,divId,traceId, requirementId){

		
		var url="/GloreeJava2/servlet/TraceAction?action=" + traceAction;
		url = url + "&traceId=" + traceId;
		url = url + "&requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterC").style.display = "block";
				document.getElementById("contentCenterC").innerHTML=xmlHttpOPCenterB.responseText;
				// since traceabiity changes may impact defects displayed, we will refresh core.
				//displayRequirementCore(requirementId);
				
				// lets also refresh the requirement in explorer
				url ="/GloreeJava2/jsp/Folder/displayARequirementInExplorer.jsp?requirementId=" + requirementId;
				url += "&bustcache=" + new Date().getTime() ;
				
				fillOPCenterGeneric(url, "objectInExplorer" + requirementId);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
	
	
	function modifyTraceInListView(traceAction, requirementId, fromReqId, toReqId){

		
		var url="/GloreeJava2/servlet/TraceAction?action=" + traceAction;
		
		url = url + "&requirementId=" + requirementId;
		url = url + "&fromReqId=" + fromReqId;
		url = url + "&toReqId=" + toReqId;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// Lets refresh the requirement
				// lets fill the trace bar
				url ="/GloreeJava2/jsp/Requirement/displayARequirementTraceActionBar.jsp?requirementId=" + requirementId;
				url += "&bustcache=" + new Date().getTime() ;
				
				var traceBar = "traceActionBar" + requirementId;
				fillOPCenterGeneric(url, traceBar);
				
				// lets also refresh the requirement in explorer
				url ="/GloreeJava2/jsp/Folder/displayARequirementInExplorer.jsp?requirementId=" + requirementId;
				url += "&bustcache=" + new Date().getTime() ;
				
				fillOPCenterGeneric(url, "objectInExplorer" + requirementId);
				
				

			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
	
	
	function refreshTraceActionBar(requirementId){

		var url ="/GloreeJava2/jsp/Requirement/displayARequirementTraceActionBar.jsp?requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		var traceBar = "traceActionBar" + requirementId;
		fillOPCenterGeneric(url, traceBar);
		
	}
	
	
	
	
	function fillCreateTracesModal(requirementId, projectId, source){

		
		var url ="/GloreeJava2/jsp/Requirement/displayRequirementSearchForm.jsp?callingRequirementId=" + requirementId;
		url += "&searchProjectId=" + projectId ;
		url += "&source=" + source ;
		url += "&bustcache=" + new Date().getTime() ;

			
		var contentDiv = "modalBody" ;
		fillOPCenterGeneric(url, contentDiv);
		
	}
	
	

	
	

	function updateTraceReason(traceId , requirementId){

		var targetDiv = "traceReasonDiv" + traceId;
		var traceReasonBox = "addEditReasonTextBox" + traceId;
		var traceReason = document.getElementById(traceReasonBox).value;
		
		
		var url="/GloreeJava2/servlet/TraceAction?action=updateTraceReason" ;
		url += "&traceId=" + traceId;
		url += "&requirementId=" + requirementId;
		
		url += "&traceReason=" + encodeURIComponent(traceReason);
		url += "&bustcache=" + new Date().getTime() ;
		
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){

				// once reason is updated, lets refresh tracebox
				
				var traceURL="/GloreeJava2/jsp/Requirement/displayRequirementTrace.jsp?requirementId=" + requirementId;
				fillOPCenterGeneric(traceURL, "contentCenterC");
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
		
		
		
		
	
	}
	
	

	

	
	function getAttributeEditForm( rowId , requirementId, requirementTypeId, attributeLabel){

		var targetDiv = "attributeDiv" + "-"+ rowId + "-" + requirementId + "-" + attributeLabel;
		
		var url="/GloreeJava2/jsp/Requirement/Attribute/displayAttributeEditForm.jsp?requirementId="+ requirementId;
		url += "&rowId=" + rowId;
		url += "&requirementTypeId=" + requirementTypeId;
		url += "&attributeLabel=" + encodeURIComponent(attributeLabel);
		
		fillOPCenterGeneric(url, targetDiv);
	}
	
	function cancelAttributeEditForm(rowId, requirementId, attributeLabel){

		var targetDiv = "attributeDiv"  + "-"+ rowId + "-" + requirementId + "-" + attributeLabel;
		
		var url="/GloreeJava2/jsp/Requirement/Attribute/displayRequirementAttributeValueSingle.jsp?requirementId=" + requirementId;
		url += "&rowId=" + rowId;
		url += "&attributeLabel=" + encodeURIComponent(attributeLabel);
		
		
		fillOPCenterGeneric(url, targetDiv);
	}
	
	
	
	function modifyTraceInTraceTree(divName, traceLevel, action, traceId, requirementId){

		
		var url="/GloreeJava2/servlet/TraceAction?action=" + action;
		url = url + "&traceId=" + traceId;
		url = url + "&traceLevel=" + traceLevel;
		
		url = url + "&requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById(divName).style.display = "block";
		document.getElementById(divName).innerHTML="Working...........";
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){

				document.getElementById(divName).innerHTML=xmlHttpOPCenterB.responseText;
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}	
	// called when someone chooses to take some action on a trace.
	function modifyTracesInBulk_Deprecated(requirementId){

		// lets disable the traceAction select object to prevent accidental double clicks.
		var bulkTraceActionsObject = document.getElementById("bulkTraceActions");
		
		var url="/GloreeJava2/servlet/TraceAction?action=modifyTracesInBulk";
		url = url + "&bulkTraceAction=" + bulkTraceActionsObject[bulkTraceActionsObject.selectedIndex].value;
		url = url + "&requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterC").style.display = "block";
				document.getElementById("contentCenterC").innerHTML=xmlHttpOPCenterB.responseText;
				// since traceabiity changes may impact defects displayed, we will refresh core.
				displayRequirementCore(requirementId);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}	
	function modifyTracesInBulk(requirementId, bulkTraceAction){
		
		var url="/GloreeJava2/servlet/TraceAction?action=modifyTracesInBulk";
		url = url + "&bulkTraceAction=" + bulkTraceAction;
		url = url + "&requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterC").style.display = "block";
				document.getElementById("contentCenterC").innerHTML=xmlHttpOPCenterB.responseText;
				// since traceabiity changes may impact defects displayed, we will refresh core.
				//displayRequirementCore(requirementId);
				
				
				// lets also refresh the requirement in explorer
				url ="/GloreeJava2/jsp/Folder/displayARequirementInExplorer.jsp?requirementId=" + requirementId;
				url += "&bustcache=" + new Date().getTime() ;
				
				fillOPCenterGeneric(url, "objectInExplorer" + requirementId);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}	
	/*
 * we don't need this any more , as we are making this a pop open a new tab.
	function showRelatedProjects(projectId){
		var showRelatedProjecsDivId = "showRelatedProjectsDiv" + projectId;
		var showRelatedProjecsSuperDivId = "showRelatedProjectsSuperDiv" + projectId;
		
		// set the toolbar to display 'returnToProject'
		var url = "/GloreeJava2/jsp/UserDashboard/showRelatedProjects.jsp?projectId=" + projectId;
		url += "&bustcache=" + new Date().getTime() ;

		document.getElementById("closeShowRelatedProjectsDiv" + projectId).style.display = "block";
		
		document.getElementById(showRelatedProjecsSuperDivId).style.display = "block";
		document.getElementById(showRelatedProjecsDivId).style.display = "block";
		document.getElementById(showRelatedProjecsDivId).innerHTML= 
			"<iframe src='"+ url +"' width='800', height='200' ></iframe>"
	}

	function showFolderMetricsChart(folderId, chartURLParam){
		
		
		// set the toolbar to display 'returnToProject'
		var url = "/GloreeJava2/jsp/Report/FolderDashboard/displayFolderMetricsChart.jsp?folderId=" + folderId + chartURLParam;
		url += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById("showFolderMetricsChartDiv").innerHTML= 
			"";
		alert("done");
	}

*/		
	
	// called when someone clicks on 'Administer Project' button . 
	function administerProject(){

			if (document.getElementById("createTracesDiv") != null){
				document.getElementById("createTracesDiv").style.display="none";
			}
			
			// set the toolbar to display 'returnToProject'
			var url = "/GloreeJava2/jsp/OpenProject/oPToolbar.jsp?action=administerProject";
			fillOPCenterGeneric(url, "toolbar");
			
			// show the options available to administrator.
			url= "/GloreeJava2/jsp/AdministerProject/adminActions.jsp?";
			fillOPCenterGeneric(url, "contentCenterA");
			
			// show the core info.
			url= "/GloreeJava2/jsp/AdministerProject/CoreInfo/coreInfo.jsp?";
			fillOPCenterGeneric(url, "contentCenterB");
			
			// get the explorer to show the administer Project screen
			url= "/GloreeJava2/servlet/ProjectAction?action=administerProject";
			fillOPCenterGeneric(url, "explorer");
			
			// set all the other content centers to empty.
			document.getElementById("contentCenterB").style.display = "none";
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";
			document.getElementById("contentCenterF").style.display = "none";
			document.getElementById("newContentRight").style.display = "block";
			
		}


	function loadPersonalizedStatus(){
		// set the toolbar to display 'returnToProject'
		
		var url = "/GloreeJava2/jsp/OpenProject/personalizedStatus.jsp?action=openProject";
		fillOPCenterGeneric(url, "personalizedStatusDiv");
		
		
	}


	//////////////////////////////////////////////////////////////////////////
	// //
	// Requirement Types //
	// //
	// ////////////////////////////////////////////////////////////////////////
	
	// called when someone clicks on the RequirmentType icon in aPExplorer.jsp
	function displayRequirementType(requirementTypeId){
		
		// set the other content centers to empty.
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		document.getElementById("newContentRight").style.display = "block";
		
		
		
		// this routine resets all admin tabs.
		// Now fill content Right with all the attributes inside this RT

		var url="/GloreeJava2/jsp/AdministerProject/RequirementType/displayAllObjectsInRequirementType.jsp?requirementTypeId=" + requirementTypeId;
		fillOPCenterGeneric(url, "newContentRight");
		
		var url="/GloreeJava2/jsp/AdministerProject/RequirementType/displayRequirementType.jsp?requirementTypeId="+ requirementTypeId;
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterB").style.display = "block";
				document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;
				// once req type is loaded, requirementTypeDisplayDiv is available to load up with the
				// req type core form.
				var url2 = "/GloreeJava2/jsp/AdministerProject/RequirementType/updateRequirementTypeForm.jsp?requirementTypeId=" 
					+ requirementTypeId ;
				fillOPCenterGeneric(url2, "requirementTypeDisplayDiv");
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}

	
	// called when someone clicks on to 'Create Requirement Type' button in contentCenterA 
	function createRequirementTypeForm(){
			// set the other content centers to empty.
			
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";
					
			var url = "/GloreeJava2/jsp/AdministerProject/RequirementType/createRequirementTypeForm.jsp?" ;
			url += "&bustcache=" + new Date().getTime() ;
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					document.getElementById("contentCenterB").style.display = "block";
					document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);
	}

	
	// called when someone clicks on to 'Create Requirement Type' button in contentCenterA 
	function manageUsers(){
			// set the other content centers to empty.
			
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";
					
			var url = "/GloreeJava2/jsp/AdministerProject/Users/manageUsers.jsp?" ;
			url += "&bustcache=" + new Date().getTime() ;
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					document.getElementById("contentCenterB").style.display = "block";
					document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);
	}
	
	
	
		function setRequirementOwnersInProject(userEmailId, addRemoveAction){
				// set the other content centers to empty.
					
				var url = "/GloreeJava2/jsp/AdministerProject/Users/manageUsers.jsp?" ;
				url += "&action=setRequirementOwnersInProject"  ;
				url += "&userEmailId="  +  encodeURIComponent(userEmailId) ;
				url += "&addRemoveAction=" +  encodeURIComponent(addRemoveAction); ;
				
				
				url += "&bustcache=" + new Date().getTime() ;
				
				xmlHttpOPCenterB =GetXmlHttpObject();
				xmlHttpOPCenterB.onreadystatechange=function() {
					if(xmlHttpOPCenterB.readyState==4){
						document.getElementById("contentCenterB").style.display = "block";
						document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;
					}
				}
				xmlHttpOPCenterB.open("GET",url,true);
				xmlHttpOPCenterB.send(null);
		}		
			
			
	// called by the createRequirementTypeForm's createRequirementType button .
	function createRequirementType(){		
		var cRF = document.getElementById("createRequirementTypeForm");
		
		var projectId = cRF.projectId;
		var requirementTypeEnableApproval = cRF.requirementTypeEnableApproval;
		var requirementTypeCanBeOrphan = cRF.requirementTypeCanBeOrphan;
		var requirementTypeCanBeDangling = cRF.requirementTypeCanBeDangling;
		
		var notifyOnOwnerChange = cRF.notifyOnOwnerChange;
		var notifyOnApprovalChange = cRF.notifyOnApprovalChange;
		
		
		var requirementTypeShortName = cRF.requirementTypeShortName;
		var requirementTypeName = cRF.requirementTypeName;
		var requirementTypeDescription = cRF.requirementTypeDescription;
		var requirementTypeDisplaySequence = cRF.requirementTypeDisplaySequence;
		
		var requirementTypeCanNotTraceToString = "";
		var requirementTypeCanNotTraceToObject = document.getElementById("requirementTypeCanNotTraceTo" );
		if (requirementTypeCanNotTraceToObject != null){
			
			for (var i = 0; i < requirementTypeCanNotTraceToObject.options.length; i++) {
				if (requirementTypeCanNotTraceToObject.options[i].selected) {
					requirementTypeCanNotTraceToString += ":#:" +  requirementTypeCanNotTraceToObject.options[i].value ;
				}
			}
		}
		requirementTypeCanNotTraceToString = requirementTypeCanNotTraceToString + ":#:";
		
		

		if (isNaN(requirementTypeDisplaySequence.value)){
			alert ("Please enter a Number for Display Sequence");
			requirementTypeDisplaySequence.focus();
			requirementTypeDisplaySequence.style.backgroundColor="#FFCC99";
			return;
		}
		
		if (requirementTypeShortName.value.length == 0) {
			alert ("Please enter a Prefix");
			requirementTypeShortName.focus();
			requirementTypeShortName.style.backgroundColor="#FFCC99";
			return;
			}
		if (requirementTypeName.value.length == 0) {
			alert ("Please enter a Name");
			requirementTypeName.focus();
			requirementTypeName.style.backgroundColor="#FFCC99";
			return;
		}
		if (requirementTypeDescription.value.length == 0) {
			alert ("Please enter a Folder Description");
			requirementTypeDescription.focus();
			requirementTypeDescription.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (requirementTypeDescription.value.length > 1000){
			alert(" Please ensure that your Descriptoin is not longer than 1000 characters. Your current message is " + requirementTypeDescription.value.length + " characters long");
			requirementTypeDescription.focus();
			requirementTypeDescription.style.backgroundColor="#FFCC99";
			return(0);
		}

		var url="/GloreeJava2/servlet/RequirementTypeAction";
		
		var params; 
		params = "action=createRequirementType&";
		params = params + "projectId=" + projectId.value + "&";
		params = params + "requirementTypeEnableApproval=" + 
			encodeURIComponent(requirementTypeEnableApproval[requirementTypeEnableApproval.selectedIndex].value) +
			"&";
		
		params = params + "requirementTypeCanBeDangling=" + 
		encodeURIComponent(requirementTypeCanBeDangling[requirementTypeCanBeDangling.selectedIndex].value) +
		"&";
		
		params = params + "requirementTypeCanBeOrphan=" + 
		encodeURIComponent(requirementTypeCanBeOrphan[requirementTypeCanBeOrphan.selectedIndex].value) +
		"&";
		
		params = params + "notifyOnOwnerChange=" + 
		encodeURIComponent(notifyOnOwnerChange[notifyOnOwnerChange.selectedIndex].value) +
		"&";
		
		params = params + "notifyOnApprovalChange=" + 
		encodeURIComponent(notifyOnApprovalChange[notifyOnApprovalChange.selectedIndex].value) +
		"&";
		params = params + "requirementTypeCanNotTraceTo=" + encodeURIComponent(requirementTypeCanNotTraceToString)+"&";
		
		
		
		params = params + "requirementTypeShortName=" + encodeURIComponent(requirementTypeShortName.value) + "&";
		params = params + "requirementTypeName=" + encodeURIComponent(requirementTypeName.value) + "&";
		params = params + "requirementTypeDescription=" + encodeURIComponent(requirementTypeDescription.value) + "&" ;
		params = params + "requirementTypeDisplaySequence=" + encodeURIComponent(requirementTypeDisplaySequence.value) ;
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;

		// set the other content centers to empty.
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
	
			
		// Get the Ajax response from RT Creation and populate the
		// contentCenterA.
		// Note : this call has to be Synchronous , as otherwise the
		// requirementType may not be created
		// by the time the second line executes.
		
		xmlHttpOPCenterB =GetXmlHttpObject();
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;
				
				// Now we can refresh the explorer asynchronously
				var url2= "/GloreeJava2/jsp/AdministerProject/aPExplorer.jsp?";
				url2 += "&bustcache=" + new Date().getTime() ;
				fillOPCenterGeneric(url2, "explorer");
			}
		}

		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}


	// called when someone clicks on 'Delete Requirement Type' button in contentCenterA 
	function deleteRequirementTypeForm(requirementTypeId){
			// set the other content centers to empty.
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			
			var url = "/GloreeJava2/jsp/AdministerProject/RequirementType/deleteRequirementTypeForm.jsp?requirementTypeId=" + requirementTypeId;
			fillOPCenterGeneric(url, "requirementTypeDisplayDiv");
	}


	function resetRequirementTypeSeqForm(requirementTypeId){
		// set the other content centers to empty.
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		
		var url = "/GloreeJava2/jsp/AdministerProject/RequirementType/resetRequirementTypeSeqForm.jsp?requirementTypeId=" + requirementTypeId;
		fillOPCenterGeneric(url, "requirementTypeDisplayDiv");
}

	// called when someone clicks on 'Delete Requirement Type' button . 
	function deleteRequirementType(requirementTypeId){
			// set the other content centers to empty.
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";
			
			document.getElementById("newContentRight").style.display = "none";
			
			
		
			var url="/GloreeJava2/servlet/RequirementTypeAction?action=deleteRequirementType&requirementTypeId=" + requirementTypeId;
			url += "&bustcache=" + new Date().getTime() ;
			xmlHttpOPCenterB =GetXmlHttpObject();
			
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;
					
					// Now that the requirementType has been deleted, we can
					// refresh the explorer synchronously
					var url= "/GloreeJava2/jsp/AdministerProject/aPExplorer.jsp?";
					fillOPCenterGeneric(url, "explorer");

				}
			}

			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);
		}

	
	function resetRequirementTypeSeq(requirementTypeId){
		// set the other content centers to empty.
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		document.getElementById("newContentRight").style.display = "none";
		
		
	
		var url="/GloreeJava2/servlet/RequirementTypeAction?action=resetRequirementTypeSeq&requirementTypeId=" + requirementTypeId;
		url += "&bustcache=" + new Date().getTime() ;
		xmlHttpOPCenterB =GetXmlHttpObject();
		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;
				
				// Now that the requirementType has been deleted, we can
				// refresh the explorer synchronously
				var url= "/GloreeJava2/jsp/AdministerProject/aPExplorer.jsp?";
				fillOPCenterGeneric(url, "explorer");

			}
		}

		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}

	
	// called when someone clicks on to 'Create Requirement Type' button in contentCenterA 
	function updateRequirementTypeForm(requirementTypeId){
			// set the other content centers to empty.
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			
			var url = "/GloreeJava2/jsp/AdministerProject/RequirementType/updateRequirementTypeForm.jsp?requirementTypeId=" + requirementTypeId ;
			fillOPCenterGeneric(url, "requirementTypeDisplayDiv");
	}

	// called by the updateRequirementTypeForm's Update RequirementType button .
	// and then send to the user to a confirmation page,
	// we have decided against letting a user change a prefix because that can
	// lead to complications with
	// a. auditing , b. ensuring that the req full tags are unique.
	function updateRequirementType(projectId, requirementTypeId){
		
		
		var requirementTypeEnableApproval = document.getElementById("requirementTypeEnableApproval");
		var requirementTypeCanBeOrphan = document.getElementById("requirementTypeCanBeOrphan");
		var requirementTypeCanBeDangling = document.getElementById("requirementTypeCanBeDangling");
		
		var notifyOnOwnerChange = document.getElementById("notifyOnOwnerChange"); 
		var notifyOnApprovalChange =  document.getElementById("notifyOnApprovalChange");
		
		
		var requirementTypeName = document.getElementById("requirementTypeName");
		var requirementTypeDescription = document.getElementById("requirementTypeDescription");
		var requirementTypeDisplaySequence = document.getElementById("requirementTypeDisplaySequence");
		
		var requirementTypeCanNotTraceToString = "";
		var requirementTypeCanNotTraceToObject = document.getElementById("requirementTypeCanNotTraceTo" );
		if (requirementTypeCanNotTraceToObject != null){
			
			for (var i = 0; i < requirementTypeCanNotTraceToObject.options.length; i++) {
				if (requirementTypeCanNotTraceToObject.options[i].selected) {
					requirementTypeCanNotTraceToString += ":#:" +  requirementTypeCanNotTraceToObject.options[i].value ;
				}
			}
		}
		requirementTypeCanNotTraceToString = requirementTypeCanNotTraceToString + ":#:";


		if (isNaN(requirementTypeDisplaySequence.value)){
			alert ("Please enter a Number for Display Sequence");
			requirementTypeDisplaySequence.focus();
			requirementTypeDisplaySequence.style.backgroundColor="#FFCC99";
			return;
		}
		
		if (requirementTypeName.value.length == 0) {
			alert ("Please enter a Name");
			requirementTypeName.focus();
			requirementTypeName.style.backgroundColor="#FFCC99";
			return;
			}
		if (requirementTypeDescription.value.length == 0) {
			alert ("Please enter a Folder Description");
			requirementTypeDescription.focus();
			requirementTypeDescription.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (requirementTypeDescription.value.length > 1000){
			alert(" Please ensure that your Descriptoin is not longer than 1000 characters. Your current message is " + requirementTypeDescription.value.length + " characters long");
			requirementTypeDescription.focus();
			requirementTypeDescription.style.backgroundColor="#FFCC99";
			return(0);
		}

		// lets get the sendReportOn Value
		var remindApproversOnObject = document.getElementById("remindApproversOn");
		var remindApproversOn = "";
		for (var i = 0; i < remindApproversOnObject.options.length; i++) {
			if (remindApproversOnObject.options[i].selected) {
				remindApproversOn += remindApproversOnObject.options[i].value + ','; 
			}
		}
		
		
		var url="/GloreeJava2/servlet/RequirementTypeAction";
		
		var params; 
		params = "action=updateRequirementType&";
		params += "requirementTypeId=" + requirementTypeId + "&";
		params += "projectId=" + projectId + "&";
		params = params + "requirementTypeEnableApproval=" + 
			encodeURIComponent(requirementTypeEnableApproval[requirementTypeEnableApproval.selectedIndex].value) +
			"&";		
		
		params = params + "requirementTypeCanBeDangling=" + 
		encodeURIComponent(requirementTypeCanBeDangling[requirementTypeCanBeDangling.selectedIndex].value) +
		"&";
		
		params = params + "requirementTypeCanBeOrphan=" + 
		encodeURIComponent(requirementTypeCanBeOrphan[requirementTypeCanBeOrphan.selectedIndex].value) +
		"&";
		
		

		params = params + "notifyOnOwnerChange=" + 
		encodeURIComponent(notifyOnOwnerChange[notifyOnOwnerChange.selectedIndex].value) +
		"&";
		
		params = params + "notifyOnApprovalChange=" + 
		encodeURIComponent(notifyOnApprovalChange[notifyOnApprovalChange.selectedIndex].value) +
		"&";
		
		params = params + "requirementTypeCanNotTraceTo=" + encodeURIComponent(requirementTypeCanNotTraceToString)+"&";
		
		params += "requirementTypeName=" + encodeURIComponent(requirementTypeName.value) + "&";
		params += "requirementTypeDescription=" + encodeURIComponent(requirementTypeDescription.value) + "&" ;
		params += "requirementTypeDisplaySequence=" + encodeURIComponent(requirementTypeDisplaySequence.value) + "&" ;
		params += "remindApproversOn=" + encodeURIComponent(remindApproversOn) ;
		params += "&bustcache=" + new Date().getTime() ;
		
		url = url + "?" + params;

		// set the other content centers to empty.
		
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		
		// Get the Ajax response from RT Creation and populate the
		// contentCenterA.
		// Note : this call has to be Synchronous , as otherwise the
		// requirementType may not be created
		// by the time the second line executes.
		
		xmlHttpOPCenterE =GetXmlHttpObject();
			
		xmlHttpOPCenterE.onreadystatechange=function() {
			if(xmlHttpOPCenterE.readyState==4){
				document.getElementById("requirementTypeDisplayDiv").style.display = "block";
				document.getElementById("requirementTypeDisplayDiv").innerHTML=xmlHttpOPCenterE.responseText;
				
				// Now we can refresh the explorer asynchronously
				var url= "/GloreeJava2/jsp/AdministerProject/aPExplorer.jsp?";
				fillOPCenterGeneric(url, "explorer");
			}
		}

		xmlHttpOPCenterE.open("GET",url,true);
		xmlHttpOPCenterE.send(null);
	}

	//////////////////////////////////////////////////////////////////////////
	// //
	// User Roles //
	// //
	// ////////////////////////////////////////////////////////////////////////

	// called when someone clicks on to 'defect status group ' button in contentCenterA
	function mapDefectStatusGroupForm(){
			// set the other content centers to empty.
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";
					
			var url = "/GloreeJava2/jsp/AdministerProject/mapDefectStatusGroup.jsp?bustcache=" + new Date().getTime() ;
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					document.getElementById("contentCenterB").style.display = "block";
					document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;

					// since we were having difficulty , getting the menu tab to show up
					// we have decided to wait till content center B is populated before
					// we attempt to set the menu items.

					// lets implement the tabbed look & feel of menu items.
					// the clicked item is set to gray, and the rest are set to the yellow color.
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);
			
	}

	// called when someone clicks on to 'defect status group ' button in contentCenterA
	function mapDefectStatusGroup(thisForm, statusGroupIdString){
		// set the other content centers to empty.
		var url="/GloreeJava2/servlet/RequirementTypeAction?";
		url += "bustcache=" + new Date().getTime() + "&"; 
		url +=  "action=updateDefectStatusGroup&";
		

		statusGroupIds = statusGroupIdString.split(',');
		for (i=0; i< statusGroupIds.length; i++){
			
			var statusGroupLabel = "defectStatusGroupId" + statusGroupIds[i];
			var statusGroupIdObject = document.getElementById(statusGroupLabel);
			if (statusGroupIdObject != null) {
				url +=  statusGroupLabel + "=" + encodeURIComponent(statusGroupIdObject.value) + "&";
			}
		}
		fillOPCenterGeneric(url, "contentCenterB");
	}


	// called when someone clicks on to 'Create Role' button in contentCenterA
	function createRoleForm(){
			// set the other content centers to empty.
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";
					
			var url = "/GloreeJava2/jsp/AdministerProject/Role/createRoleForm.jsp?bustcache=" + new Date().getTime() ;
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					document.getElementById("contentCenterB").style.display = "block";
					document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;

					// since we were having difficulty , getting the menu tab to show up
					// we have decided to wait till content center B is populated before
					// we attempt to set the menu items.

				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);
			
	}

	
	function createRole(thisForm){
		

		var roleName = thisForm.roleName;
		var roleDescription = thisForm.roleDescription;
		

		var approvalType = thisForm.approvalType[thisForm.approvalType.selectedIndex].value;
		
		var approvalRank = thisForm.approvalRank;
		
		if (isNaN(approvalRank.value)){
			alert("Please enter a valid Number");
			approvalRank.focus()
			approvalRank.style.backgroundColor="#FFCC99";
			return (0);
		}
		
		if (roleName.value.length == 0) {
			alert ("Please enter a Role Name");
			roleName.focus();
			roleName.style.backgroundColor="#FFCC99";
			return(0);
			}
		if (roleDescription.value.length == 0) {
			alert ("Please enter a Role Description");
			roleDescription.focus();
			roleDescription.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (roleDescription.value.length > 1000){
			alert(" Please ensure that your Descriptoin is not longer than 1000 characters. Your current message is " + requirementTypeDescription.value.length + " characters long");
			roleDescription.focus();
			roleDescription.style.backgroundColor="#FFCC99";
			return(0);
		}

		var url="/GloreeJava2/servlet/RoleAction";
		
		var params; 
		params = "action=createRole&";
		params = params + "roleName=" + encodeURIComponent(roleName.value) + "&";
		params = params + "roleDescription=" + encodeURIComponent(roleDescription.value) + "&" ;
		params = params + "approvalType=" + encodeURIComponent(approvalType) + "&" ;
		params = params + "approvalRank=" + encodeURIComponent(approvalRank.value) ;
		
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;

		// set the other content centers to empty.
		
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("newContentRight").style.display = "none";
	
			
		// Get the Ajax response from RT Creation and populate the
		// contentCenterA.
		// Note : this call has to be Synchronous , as otherwise the
		// requirementType may not be created
		// by the time the second line executes.
		
		xmlHttpOPCenterB =GetXmlHttpObject();
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;
				
				// Now we can refresh the explorer asynchronously
				var url= "/GloreeJava2/jsp/AdministerProject/aPExplorer.jsp?";
				fillOPCenterGeneric(url, "explorer");

			}
		}

		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
	}


	function updateRole(thisForm){
		

		var roleName = thisForm.roleName;
		var roleDescription = thisForm.roleDescription;
		var roleId = thisForm.roleId.value;
		
		var approvalType = thisForm.approvalType[thisForm.approvalType.selectedIndex].value;
		
		var approvalRank = thisForm.approvalRank;
		
		if (isNaN(approvalRank.value)){
			alert("Please enter a valid Number");
			approvalRank.focus()
			approvalRank.style.backgroundColor="#FFCC99";
			return (0);
		}
		
		if (roleName.value.length == 0) {
			alert ("Please enter a Role Name");
			roleName.focus();
			roleName.style.backgroundColor="#FFCC99";
			return(0);
			}
		if (roleDescription.value.length == 0) {
			alert ("Please enter a Role Description");
			roleDescription.focus();
			roleDescription.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (roleDescription.value.length > 1000){
			alert(" Please ensure that your Descriptoin is not longer than 1000 characters. Your current message is " + requirementTypeDescription.value.length + " characters long");
			roleDescription.focus();
			roleDescription.style.backgroundColor="#FFCC99";
			return(0);
		}

		var url="/GloreeJava2/servlet/RoleAction";
		
		var params; 
		params = "action=updateRole&";
		params = params + "roleId=" + encodeURIComponent(roleId) + "&";
		params = params + "roleName=" + encodeURIComponent(roleName.value) + "&";
		params = params + "roleDescription=" + encodeURIComponent(roleDescription.value) + "&";
		params = params + "approvalType=" + encodeURIComponent(approvalType) + "&" ;
		params = params + "approvalRank=" + encodeURIComponent(approvalRank.value) ;
		
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;

		// set the other content centers to empty.
		
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("newContentRight").style.display = "none";
	
			
		// Get the Ajax response from RT Creation and populate the
		// contentCenterA.
		// Note : this call has to be Synchronous , as otherwise the
		// requirementType may not be created
		// by the time the second line executes.
		
		document.getElementById("roleDisplayDiv").innerHTML= "<span class='normalText'> &nbsp;&nbsp;&nbsp;Working...</span>";

		xmlHttpOPCenterB =GetXmlHttpObject();
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("roleDisplayDiv").innerHTML=xmlHttpOPCenterB.responseText;
				
				// Now we can refresh the explorer asynchronously. This is a must as the name / description of the role have
				// changed.
				var url= "/GloreeJava2/jsp/AdministerProject/aPExplorer.jsp?";
				fillOPCenterGeneric(url, "explorer");
			}
		}

		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
	}

	
	function displayRole(roleId){

		// set the other content centers to empty.
		document.getElementById("contentCenterB").innerHTML= ""
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		
		document.getElementById("newContentRight").style.display = "block";
		
		document.getElementById("newContentRight").innerHTML= ""
	
		
		
		// Now fill content Right with all the attributes inside this RT
		var url="/GloreeJava2/jsp/AdministerProject/Role/displayAllUsersInRole.jsp?roleId=" + roleId;
		fillOPCenterGeneric(url, "newContentRight");

		var url="/GloreeJava2/jsp/AdministerProject/Role/displayRole.jsp?roleId="+ roleId;
		var xmlHttpOPCenterE =GetXmlHttpObject();
		xmlHttpOPCenterE.onreadystatechange=function() {
			if(xmlHttpOPCenterE.readyState==4){

				document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterE.responseText;
				// lets display the default screen for role i.e edit role privs form.
				// we want to call edit Role Privs after we are sure that displayRole has been displayed
				// so this call has to be sync.
				var url = "/GloreeJava2/jsp/AdministerProject/Role/editRolePrivilegesForm.jsp?roleId=" + roleId ;
				fillOPCenterGeneric(url, "roleDisplayDiv");
			}
		}
		xmlHttpOPCenterE.open("GET",url,true);
		xmlHttpOPCenterE.send(null);
			
 	}

		
	function addUserToRoleForm(roleId){
		// set the other content centers to empty.
		
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		
		var url = "/GloreeJava2/jsp/AdministerProject/Role/addUserToRoleForm.jsp?roleId=" + roleId ;
		fillOPCenterGeneric(url, "roleDisplayDiv");
	}

	
	function addUsersToRole(thisForm){
		 
		var emailIds = thisForm.emailIds;
		var roleId = thisForm.roleId.value;
		
		if (emailIds.value.length == 0) {
			alert ("Please enter Comma separated list of Email ids");
			emailIds.focus();
			emailIds.style.backgroundColor="#FFCC99";
			return(0);
		}

		// lets ensure that to has only valid email ids.
		// first replace any occurence of space or ; with ,
		
		var to = emailIds.value;
		if (to != null){ 
			to = to.replace(" ", ",");
			
			to = to.replace(";", ",");
			
			// some of the logic may create one or more of ,, . so we need to replace them with , . 
			
			to = to.replace("/,+/", ",");
			
			// now lets split each one of these email id and see if they are valid.
			toArray = to.split(',');
			
			for (t in toArray){
				var currentTo = toArray[t];
				currentTo = currentTo.replace(" ", "");
				if ((currentTo != null) && (currentTo.length > 0)){
					if (echeck(currentTo)==false){
						alert ("Your email id " + currentTo + " is not formatted correctly. Please fix it.");
						emailIds.focus();
						emailIds.style.backgroundColor="#FFCC99";
						return(0);
					}
				}
			}
		}
		
		
		
		document.getElementById("contentCenterE").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/servlet/RoleAction";
		var params = "action=addUsers";
		params += "&emailIds=" + encodeURIComponent(emailIds.value) ;
		params += "&roleId=" + encodeURIComponent(roleId) ;
		params += "&bustcache=" + new Date().getTime() ;
		
		url = url + "?" + params;
		
		// Get the Ajax response from Attribute Creation and populate the
		// contentCenterA.
		// Note : this call has to be Synchronous , as otherwise the attribute
		// may not be created
		// by the time the fillExplorer happens..
		xmlHttpOPCenterE =GetXmlHttpObject();
			
		xmlHttpOPCenterE.onreadystatechange=function() {
			if(xmlHttpOPCenterE.readyState==4){

				document.getElementById("roleDisplayDiv").innerHTML=xmlHttpOPCenterE.responseText;
								
				// Since we plan to display All the attributes in ContentRight,
				// lets call that now.
				// Now fill content Right with all the attributes inside this RT

				url="/GloreeJava2/jsp/AdministerProject/Role/displayAllUsersInRole.jsp?roleId=" + roleId;
				fillOPCenterGeneric(url, "newContentRight");

			}
		}

		xmlHttpOPCenterE.open("GET",url,true);
		xmlHttpOPCenterE.send(null);
	}

	function updateRoleForm(roleId){
		// set the other content centers to empty.
		
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		
		var url = "/GloreeJava2/jsp/AdministerProject/Role/editRoleForm.jsp?roleId=" + roleId ;
		fillOPCenterGeneric(url, "roleDisplayDiv");
	}
	
	function editRolePrivilegesForm(roleId){
		// set the other content centers to empty.
		
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		
		var url = "/GloreeJava2/jsp/AdministerProject/Role/editRolePrivilegesForm.jsp?roleId=" + roleId ;
		fillOPCenterGeneric(url, "roleDisplayDiv");
	}

	// this function checks on or off all role permissions based on the header check box.
	function selectDeselectAllRoles(roleObject) {
		for (i=1; i<roleObject.length;i++){
			if (roleObject[0].checked == true){
				roleObject[i].checked = true;
			}
			else{
				roleObject[i].checked = false;
			}
		}			
	}


	// called when someone clicks on to 'Create Role' button in contentCenterA
	function createWebFormForm(){
			// set the other content centers to empty.
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		document.getElementById("newContentRight").style.display = "none";
				
		var url = "/GloreeJava2/jsp/AdministerProject/WebForm/createWebFormForm.jsp?bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterB").style.display = "block";
				document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;


			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
	}

	
	function createWebForm(thisForm){
		

		var folderId = thisForm.folderId[thisForm.folderId.selectedIndex].value;
		var name = thisForm.name.value;
		var description = thisForm.description.value;
		var introduction = thisForm.introduction.value;
		var owner = thisForm.owner[thisForm.owner.selectedIndex].value;
		var notifyOnCreation = thisForm.notifyOnCreation.value;
		var submitForApprovalOnCreation = thisForm.submitForApprovalOnCreation[thisForm.submitForApprovalOnCreation.selectedIndex].value;
		var enableLookup = thisForm.enableLookup[thisForm.enableLookup.selectedIndex].value;
		
		if (name.length == 0) {
			alert ("Please enter a Web Form Name");
			thisForm.name.focus();
			thisForm.name.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		var url="/GloreeJava2/servlet/RequirementTypeAction";
		
		var params; 
		params = "action=createWebForm&";
		params = params + "folderId=" + encodeURIComponent(folderId) + "&";
		params = params + "name=" + encodeURIComponent(name) + "&";
		params = params + "description=" + encodeURIComponent(description) + "&";
		params = params + "introduction=" + encodeURIComponent(introduction) + "&";
		params = params + "owner=" + encodeURIComponent(owner) + "&";
		params = params + "notifyOnCreation=" + encodeURIComponent(notifyOnCreation) + "&";
		params = params + "submitForApprovalOnCreation=" + encodeURIComponent(submitForApprovalOnCreation) + "&";
		params = params + "enableLookup=" + encodeURIComponent(enableLookup) ;
		
		
		
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;

		// set the other content centers to empty.
		
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("newContentRight").style.display = "none";
	
			
		// Get the Ajax response from RT Creation and populate the
		// contentCenterA.
		// Note : this call has to be Synchronous , as otherwise the
		// requirementType may not be created
		// by the time the second line executes.
		
		xmlHttpOPCenterB =GetXmlHttpObject();
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;
				
				// Now we can refresh the explorer asynchronously
				var url= "/GloreeJava2/jsp/AdministerProject/aPExplorer.jsp?";
				fillOPCenterGeneric(url, "explorer");

			}
		}

		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
	}
	
function updateWebForm(thisForm, webFormId){
		

		var folderId = thisForm.folderId[thisForm.folderId.selectedIndex].value;
		var name = thisForm.name.value;
		var description = thisForm.description.value;
		var introduction = thisForm.introduction.value;
		var owner = thisForm.owner[thisForm.owner.selectedIndex].value;
		var notifyOnCreation = thisForm.notifyOnCreation.value;
		var submitForApprovalOnCreation = thisForm.submitForApprovalOnCreation[thisForm.submitForApprovalOnCreation.selectedIndex].value;
		var enableLookup = thisForm.enableLookup[thisForm.enableLookup.selectedIndex].value;
		
		if (name.length == 0) {
			alert ("Please enter a Web Form Name");
			thisForm.name.focus();
			thisForm.name.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		var url="/GloreeJava2/servlet/RequirementTypeAction";
		
		var params; 
		params = "action=updateWebForm&";
		params = params + "webFormId=" + encodeURIComponent(webFormId) + "&";
		
		params = params + "folderId=" + encodeURIComponent(folderId) + "&";
		params = params + "name=" + encodeURIComponent(name) + "&";
		params = params + "description=" + encodeURIComponent(description) + "&";
		params = params + "introduction=" + encodeURIComponent(introduction) + "&";
		params = params + "owner=" + encodeURIComponent(owner) + "&";
		params = params + "notifyOnCreation=" + encodeURIComponent(notifyOnCreation) + "&";
		params = params + "submitForApprovalOnCreation=" + encodeURIComponent(submitForApprovalOnCreation) + "&";
		params = params + "enableLookup=" + encodeURIComponent(enableLookup) ;
		
		
		
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;

		// set the other content centers to empty.
		
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("newContentRight").style.display = "none";
	
			
		// Get the Ajax response from RT Creation and populate the
		// contentCenterA.
		// Note : this call has to be Synchronous , as otherwise the
		// requirementType may not be created
		// by the time the second line executes.
		
		xmlHttpOPCenterB =GetXmlHttpObject();
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;
				
				// Now we can refresh the explorer asynchronously
				var url= "/GloreeJava2/jsp/AdministerProject/aPExplorer.jsp?";
				fillOPCenterGeneric(url, "explorer");

			}
		}

		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
	}

	function deleteWebForm(webFormId){
		
		var url="/GloreeJava2/servlet/RequirementTypeAction";
		
		var params; 
		params = "action=deleteWebForm&";
		params = params + "webFormId=" + encodeURIComponent(webFormId) ;
		params = params + "&bustcache=" + new Date().getTime() ;

		url = url + "?" + params;

		// set the other content centers to empty.
		
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("newContentRight").style.display = "none";
	
			
		// Get the Ajax response from RT Creation and populate the
		// contentCenterA.
		// Note : this call has to be Synchronous , as otherwise the
		// requirementType may not be created
		// by the time the second line executes.
		
		xmlHttpOPCenterB =GetXmlHttpObject();
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;
				
				// Now we can refresh the explorer asynchronously
				var url= "/GloreeJava2/jsp/AdministerProject/aPExplorer.jsp?";
				fillOPCenterGeneric(url, "explorer");

			}
		}

		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
	}



	function displayWebForm (webFormId){

		// set the other content centers to empty.
		document.getElementById("contentCenterB").innerHTML= ""
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		document.getElementById("newContentRight").style.display = "none";
			
		
		var url="/GloreeJava2/jsp/AdministerProject/WebForm/displayWebForm.jsp?webFormId="+ webFormId;
		fillOPCenterGeneric(url, "contentCenterB");
					
 	}
	
	
	// this function iterates through all the folder's updateAttribute checkboxes and then 
	// based on whether the user chose to select or deselect ALl updateable attribute of 
	// all folders it does the needful.
	function selectDeselectAllUpdatableAttributes(thisForm) {
	
		updateAllAttributesCheckBox = document.getElementById("updateAllAttributes");
		var folderIdString = thisForm.folderIdString.value;
		if (folderIdString != null){
			folderIds = folderIdString.split('#');
			for (f in folderIds){
				var folderId = folderIds[f];
				if (folderId != null){
					if (updateAllAttributesCheckBox.checked == true){
						// lets set ALL the updateAttribute drop down values to selected
						// for all folders
						selectAllUpdateAttributes( folderId);
					}
					else {
						// lets set ALL the updateAttribute drop down values to NOT selected
						// for all folders
						deSelectAllUpdateAttributes( folderId);
					}
				}
			}
		}
	}

	// this function expands all the 'updateAttributes' drop downs for all folders for this role
	function expandAllUpdatableAttributes() {
		
		// lets open the collapse div and close the expand div.
		document.getElementById("expandAllUpdatableAttributesDiv").style.display = "none";
		document.getElementById("collapseAllUpdatableAttributesDiv").style.display = "block";
		
		
		var folderIdString = document.getElementById("folderIdString").value;
		if (folderIdString != null){
			folderIds = folderIdString.split('#');
			for (f in folderIds){
				var folderId = folderIds[f];
				if (folderId != null){
					 var updateAttributeObject = document.getElementById("updateAttributesDiv"  + folderId);
					 if (updateAttributeObject != null){
						 updateAttributeObject.style.display="block"; 
					 }
					 var updateAttributeMenuObject = document.getElementById("updateAttributesMenuOptionsDiv"  + folderId);
					 if (updateAttributeMenuObject != null){
						 updateAttributeMenuObject.style.display="block"; 
					 }
				}
			}
		}
	}
	
	// this function collapses all the 'updateAttributes' drop downs for all folders for this role
	function collapseAllUpdatableAttributes() {
		
		// lets close the collapse div and open the expand div.
		document.getElementById("expandAllUpdatableAttributesDiv").style.display = "block";
		document.getElementById("collapseAllUpdatableAttributesDiv").style.display = "none";
		
		
		var folderIdString = document.getElementById("folderIdString").value;
		if (folderIdString != null){
			folderIds = folderIdString.split('#');
			for (f in folderIds){
				var folderId = folderIds[f];
				if (folderId != null){
					 var updateAttributeObject = document.getElementById("updateAttributesDiv"  + folderId);
					 if (updateAttributeObject != null){
						 updateAttributeObject.style.display="none"; 
					 }
					 var updateAttributeMenuObject = document.getElementById("updateAttributesMenuOptionsDiv"  + folderId);
					 if (updateAttributeMenuObject != null){
						 updateAttributeMenuObject.style.display="none"; 
					 }
				}
			}
		}
	}	
	function deSelectAllUpdateAttributes( folderId){
		var dropDownObject = document.getElementById("updateAttributes" + folderId);
		if (dropDownObject  != null){
			for (var i = 0; i < dropDownObject.options.length; i++) {
				if (dropDownObject.options[i].selected) {
					dropDownObject.options[i].selected = false; 
				}
			}
		}
	}
	
	function selectAllUpdateAttributes( folderId){
		var dropDownObject = document.getElementById("updateAttributes" + folderId);
		if (dropDownObject  != null){
			for (var i = 0; i < dropDownObject.options.length; i++) {
				if (dropDownObject.options[i].selected == false) {
					dropDownObject.options[i].selected = true; 
				}
			}
		}
	}
	
	function editRolePrivileges(thisForm){
	
		var roleId = thisForm.roleId.value;
		
		
		var createRequirement = "";
		for (var i=0; i < thisForm.createRequirement.length; i++){
			if (thisForm.createRequirement[i].checked) {
				 createRequirement += thisForm.createRequirement[i].value + ':' ;
			}
		}
		
		var readRequirement = "";
		for (var i=0; i < thisForm.readRequirement.length; i++){
			if (thisForm.readRequirement[i].checked) {
				readRequirement  += thisForm.readRequirement[i].value + ':' ;
			}
		}
		
		var updateRequirement = "";
		for (var i=0; i < thisForm.updateRequirement.length; i++){
			if (thisForm.updateRequirement[i].checked) {
				updateRequirement  += thisForm.updateRequirement[i].value + ':';
			}
		}
		
		var deleteRequirement = "";
		for (var i=0; i < thisForm.deleteRequirement.length; i++){
			if (thisForm.deleteRequirement[i].checked) {
				deleteRequirement  += thisForm.deleteRequirement[i].value + ':';
			}
		}
		
		var traceRequirement = "";
		for (var i=0; i < thisForm.traceRequirement.length; i++){
			if (thisForm.traceRequirement[i].checked) {
				traceRequirement  += thisForm.traceRequirement[i].value + ':';
			}
		}
		
		var approveRequirement = "";
		for (var i=0; i < thisForm.approveRequirement.length; i++){
			if (thisForm.approveRequirement[i].checked) {
				approveRequirement  += thisForm.approveRequirement[i].value + ':';
			}
		}

		var votingRightsString = "";
		var folderIdString = thisForm.folderIdString.value;
		if (folderIdString != null){
			folderIds = folderIdString.split('#');
			for (f in folderIds){
				var folderId = folderIds[f];
				if (folderId != null){
					// we are starting a new folder object for this role (i.e a new rolepriv row)
					var folderUpdateAttributeString = "";
					votingRightsObject = document.getElementById("votingRights" + folderId);
					try {
						votingRightsString += folderId + "##" + votingRightsObject.value + "###" ;
					}
					catch (e){
						//do nothing
					}
					
					
				}
			}
		}
		
		// lets build the updateAtrributes string. we parse folderString, split by #
		// and for each folderId, look for an object caled 'updateAttributesFolderId' drop down object
		// and read the values to build the updateAttributes String.
		var folderIdString = thisForm.folderIdString.value;
		var updateAttributesString = "";
		if (folderIdString != null){
			folderIds = folderIdString.split('#');
			for (f in folderIds){
				var folderId = folderIds[f];
				if (folderId != null){
					// we are starting a new folder object for this role (i.e a new rolepriv row)
					var folderUpdateAttributeString = "";
					updateAttributeObject = document.getElementById("updateAttributes" + folderId);
					if (updateAttributeObject != null){
						
						for (var i = 0; i < updateAttributeObject.options.length; i++) {
							if (updateAttributeObject.options[i].selected) {
								folderUpdateAttributeString += ":#:" +  updateAttributeObject.options[i].value ;
							}
						}
					}
					if (folderUpdateAttributeString.length > 0){
						// there were some update attributes selected for this folder and role. so lets add it to string.
						updateAttributesString += folderId + "##" + folderUpdateAttributeString + ":#:" + "###" ;
					}
					
				}
			}
		}
		
		var url="/GloreeJava2/servlet/RoleAction";
		var params = "action=updateRolePrivs&";
		params += "roleId=" + roleId + "&";
		params += "createRequirement=" + createRequirement + "&";
		params += "readRequirement=" + readRequirement + "&";
		params += "updateRequirement=" + updateRequirement + "&";
		params += "deleteRequirement=" + deleteRequirement + "&";
		params += "traceRequirement=" + traceRequirement + "&";
		params += "approveRequirement=" + approveRequirement + "&";
		params += "votingRightsString=" + encodeURIComponent(votingRightsString) + "&";
		params += "updateAttributesString=" + encodeURIComponent(updateAttributesString) + "&";

		// since the number of selected reqs can be large and get has a limit
		// we will use post.
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.open("POST", url, true);

		// Send the proper header information along with the request
		xmlHttpOPCenterB.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xmlHttpOPCenterB.setRequestHeader("Content-length", params.length);
		xmlHttpOPCenterB.setRequestHeader("Connection", "close");

		
		document.getElementById("roleDisplayDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";	

		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("roleDisplayDiv").style.display = 'block';
				document.getElementById("roleDisplayDiv").innerHTML=xmlHttpOPCenterB.responseText;
			}
		}
		xmlHttpOPCenterB.send(params);
	}

	
	
	
	function analyzeThis(){
		
		var word = document.getElementById("word").value;
		var textToAnalyze = document.getElementById("textToAnalyze").value;
		
		var url = "/GloreeJava2/jsp/WebSite/Community/analyzeThis.jsp" ;
		
		var params = "word=" + encodeURIComponent(word) + "&";
		params += "textToAnalyze=" + encodeURIComponent(textToAnalyze) + "&";
		

		// since the number of selected reqs can be large and get has a limit
		// we will use post.
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.open("POST", url, true);

		// Send the proper header information along with the request
		xmlHttpOPCenterB.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xmlHttpOPCenterB.setRequestHeader("Content-length", params.length);
		xmlHttpOPCenterB.setRequestHeader("Connection", "close");

		
		document.getElementById("analyzedDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";	

		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("analyzedDiv").innerHTML=xmlHttpOPCenterB.responseText;
			}
		}
		xmlHttpOPCenterB.send(params);
	}

	function editUsersForm(roleId){
		// set the other content centers to empty.
		
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		
		var url = "/GloreeJava2/jsp/AdministerProject/Role/editUsersForm.jsp?roleId=" + roleId ;
		fillOPCenterGeneric(url, "roleDisplayDiv");
	}



	// removes users from a role.
	function deleteUsersFunction(thisForm){
		var roleId = thisForm.roleId.value;
	
		// read all the check box values and put them in a nice string.
		var deleteUsersString = "";
		for (var i=0; i < thisForm.editUsers.length; i++){
			if (thisForm.editUsers[i].checked) {
				deleteUsersString  += thisForm.editUsers[i].value + ':' ;
			}
		}
		
		var url="/GloreeJava2/servlet/RoleAction?";
		url += "action=deleteUsersFromRole&";
		url += "roleId=" + roleId + "&";
		url += "deleteUsers=" + deleteUsersString;
		url += "&bustcache=" + new Date().getTime() ;
		xmlHttpOPCenterE =GetXmlHttpObject();
		
		xmlHttpOPCenterE.onreadystatechange=function() {
			if(xmlHttpOPCenterE.readyState==4){

				document.getElementById("roleDisplayDiv").innerHTML=xmlHttpOPCenterE.responseText;
								
				// Since we plan to display All the attributes in ContentRight,
				// lets call that now.
				// Now fill content Right with all the attributes inside this RT
				url="/GloreeJava2/jsp/AdministerProject/Role/displayAllUsersInRole.jsp?roleId=" + roleId;
				fillOPCenterGeneric(url, "newContentRight");

			}
		}

		xmlHttpOPCenterE.open("GET",url,true);
		xmlHttpOPCenterE.send(null);
	}

	// moves users to a different role.
	function moveUsersFunction(thisForm){
		var roleId = thisForm.roleId.value;
		var moveRoleId = thisForm.moveRole[thisForm.moveRole.selectedIndex].value;
		
		// read all the check box values and put them in a nice string.
		var moveUsersString = "";
		for (var i=0; i < thisForm.editUsers.length; i++){
			if (thisForm.editUsers[i].checked) {
				moveUsersString  += thisForm.editUsers[i].value + ':' ;
			}
		}
		
		var url="/GloreeJava2/servlet/RoleAction?";
		url += "action=moveUsersToNewRole&";
		url += "roleId=" + roleId + "&";
		url += "moveRoleId=" + moveRoleId + "&";
		url += "moveUsers=" + moveUsersString;
		url += "&bustcache=" + new Date().getTime() ;
		

		xmlHttpOPCenterE =GetXmlHttpObject();
		
		xmlHttpOPCenterE.onreadystatechange=function() {
			if(xmlHttpOPCenterE.readyState==4){


				document.getElementById("roleDisplayDiv").innerHTML=xmlHttpOPCenterE.responseText;
								
				// Since we plan to display All the attributes in ContentRight,
				// lets call that now.
				// Now fill content Right with all the attributes inside this RT
				url="/GloreeJava2/jsp/AdministerProject/Role/displayAllUsersInRole.jsp?roleId=" + roleId;
				fillOPCenterGeneric(url, "newContentRight");

			}
		}

		xmlHttpOPCenterE.open("GET",url,true);
		xmlHttpOPCenterE.send(null);
	}

	function deleteRoleForm(roleId){
		// set the other content centers to empty.
		
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		
		var url = "/GloreeJava2/jsp/AdministerProject/Role/deleteRoleForm.jsp?roleId=" + roleId ;
		fillOPCenterGeneric(url, "roleDisplayDiv");
	}


	// Deletes the role from this project.
	function deleteRole(thisForm){
		document.getElementById("newContentRight").style.display = "none";
		var roleId = thisForm.roleId.value;
		
		var url="/GloreeJava2/servlet/RoleAction?";
		url += "action=deleteRole&";
		url += "roleId=" + roleId ;
		url += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){

				document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;

				// Now we can refresh the explorer asynchronously
				var url= "/GloreeJava2/jsp/AdministerProject/aPExplorer.jsp?";
				fillOPCenterGeneric(url, "explorer");
				
			}
		}

		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}

	//////////////////////////////////////////////////////////////////////////
	// //
	// Baselines (rTBaseline) //
	// //
	// ////////////////////////////////////////////////////////////////////////
	// called when someone clicks on to 'Create Attribute ' button in
	// contentCenterA
	
	function createBaselineForm(requirementTypeId){
			// set the other content centers to empty.
			
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			
			var url = "/GloreeJava2/jsp/AdministerProject/RTBaseline/createRTBaselineForm.jsp?requirementTypeId=" + requirementTypeId ;
			fillOPCenterGeneric(url, "requirementTypeDisplayDiv");
	}

	
	// Called when someone clicks the 'Create Baseline'
	// This is called when some one tries to create a new baseline for a
	// requirment type.
	// not to be confused with Baseline creation for a requirement.
	function createRTBaseline(requirementTypeId){
		
		var baselineName = document.getElementById("baselineName");
		var lockedObject = document.getElementById("locked");
		var baselineDescription = document.getElementById("baselineDescription");
		

		if (baselineName.value.length == 0) {
			alert ("Please enter a Baseline Name");
			baselineName.focus();
			baselineName.style.backgroundColor="#FFCC99";
			return(0);
		}

		if (baselineDescription.value.length == 0) {
			alert ("Please enter a Baseline Description");
			baselineDescription.focus();
			baselineDescription.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (baselineDescription.value.length > 1000){
			alert(" Please ensure that your Descriptoin is not longer than 1000 characters. Your current message is " + baselineDescription.value.length + " characters long");
			baselineDescription.focus();
			baselineDescription.style.backgroundColor="#FFCC99";
			return(0);
		}
		

		var url="/GloreeJava2/servlet/RequirementTypeAction";
		var params = "action=createBaseline&";
		params += "requirementTypeId=" + encodeURIComponent(requirementTypeId) + "&";
		params += "baselineName=" + encodeURIComponent(baselineName.value) + "&";
		params += "locked=" + encodeURIComponent(lockedObject[lockedObject.selectedIndex].value) +	"&";
		params += "baselineDescription=" + encodeURIComponent(baselineDescription.value);
		params += "&bustcache=" + new Date().getTime() ;
		
		url = url + "?" + params;
		
		// Get the Ajax response from Baseline Creation and populate the
		// contentCenterA.
		// Note : this call has to be Synchronous , as otherwise the baseline
		// may not be created
		// by the time the fillExplorer happens..

		xmlHttpOPCenterE =GetXmlHttpObject();
			
		xmlHttpOPCenterE.onreadystatechange=function() {
			if(xmlHttpOPCenterE.readyState==4){

				document.getElementById("requirementTypeDisplayDiv").innerHTML=xmlHttpOPCenterE.responseText;
								
				// Since we plan to display All the baselines in ContentRight,
				// lets call that now.

				
				url="/GloreeJava2/jsp/AdministerProject/RequirementType/displayAllObjectsInRequirementType.jsp?requirementTypeId=" + requirementTypeId;

				fillOPCenterGeneric(url, "newContentRight");
			}
		}

		xmlHttpOPCenterE.open("GET",url,true);
		xmlHttpOPCenterE.send(null);
	}
	

	// called when someone requests to open an baseline in contentRight
	function editRTBaselineForm(rTBaselineId, requirementTypeId){
		
		// set the other content centers to empty.
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
	
		
		var url="/GloreeJava2/jsp/AdministerProject/RTBaseline/editRTBaselineForm.jsp";
		url=url+"?rTBaselineId="+ rTBaselineId + "&requirementTypeId=" + requirementTypeId;
		fillOPCenterGeneric(url, "requirementTypeDisplayDiv");
	}
	
	
	// Called when someone clicks the 'Submit changes' button in the Create Baseline Form page.
	// not to be confused with Baseline creation for a requirement.
	function editRTBaseline(requirementTypeId){
		
		
		var a = document.getElementById("rTBaselineId");
		 
		var baselineName = document.getElementById("baselineName");
		var lockedObject = document.getElementById("locked");
		var baselineDescription = document.getElementById("baselineDescription");
		

		if (baselineName.value.length == 0) {
			alert ("Please enter a Baseline Name");
			baselineName.focus();
			baselineName.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (baselineDescription.value.length == 0) {
			alert ("Please enter a Baseline Description");
			baselineDescription.focus();
			baselineDescription.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (baselineDescription.value.length > 1000){
			alert(" Please ensure that your Descriptoin is not longer than 1000 characters. Your current message is " + baselineDescription.value.length + " characters long");
			baselineDescription.focus();
			baselineDescription.style.backgroundColor="#FFCC99";
			return(0);
		}
		

		
		
		var url="/GloreeJava2/servlet/RequirementTypeAction";
		var params = "action=editRTBaseline&";
		params += "rTBaselineId=" + a.value + "&";
		params += "requirementTypeId=" + encodeURIComponent(requirementTypeId) + "&";
		params += "baselineName=" + encodeURIComponent(baselineName.value) + "&";
		params += "locked=" + encodeURIComponent(lockedObject[lockedObject.selectedIndex].value) +	"&";
		params += "baselineDescription=" + encodeURIComponent(baselineDescription.value);
		params += "&bustcache=" + new Date().getTime() ;
		
		url = url + "?" + params;
		
		
		// Get the Ajax response from Baseline Creation and populate the
		// contentCenterA.
		// Note : this call has to be Synchronous , as otherwise the folder may
		// not be created
		// by the time the fillExplorer happens..
		xmlHttpOPCenterE =GetXmlHttpObject();
			
		xmlHttpOPCenterE.onreadystatechange=function() {
			if(xmlHttpOPCenterE.readyState==4){
				
				document.getElementById("requirementTypeDisplayDiv").innerHTML=xmlHttpOPCenterE.responseText;
								
				// Since we plan to display All the baselines in ContentRight,
				// lets call that now.
				url="/GloreeJava2/jsp/AdministerProject/RequirementType/displayAllObjectsInRequirementType.jsp?requirementTypeId=" + requirementTypeId;
				fillOPCenterGeneric(url, "newContentRight");
			}
		}

		xmlHttpOPCenterE.open("GET",url,true);
		xmlHttpOPCenterE.send(null);
	}

	// called when someone clicks on 'Delete Baseline' button in contentCenterB 
	function deleteRTBaselineForm(rTBaselineId, requirementTypeId){
			// set the other content centers to empty.
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			
			var url = "/GloreeJava2/jsp/AdministerProject/RTBaseline/deleteRTBaselineForm.jsp?rTBaselineId=" + rTBaselineId + "&requirementTypeId=" + requirementTypeId;
			fillOPCenterGeneric(url, "requirementTypeDisplayDiv");
	}
	

	// called when someone clicks on 'Delete Baseline' button in the 'Delete Baseline Form' screen. 
	function deleteRTBaseline(rTBaselineId, requirementTypeId){
			// set the other content centers to empty.
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";

			// at this point before submitting changes to server, lets disable the submit button
			// to prevent accidenatal double clicks.
			document.getElementById("deleteBaselineButton").disabled = true;
			 
			
			var url="/GloreeJava2/servlet/RequirementTypeAction?action=deleteRTBaseline&rTBaselineId=" + rTBaselineId;
			url += "&bustcache=" + new Date().getTime() ;
			
			xmlHttpOPCenterE =GetXmlHttpObject();
			xmlHttpOPCenterE.onreadystatechange=function() {
				if(xmlHttpOPCenterE.readyState==4){
					document.getElementById("requirementTypeDisplayDiv").innerHTML=xmlHttpOPCenterE.responseText;

					// Since we plan to display All the attributes / Baselines in
					// ContentRight, lets call that now.
					url = "/GloreeJava2/jsp/AdministerProject/RequirementType/displayAllObjectsInRequirementType.jsp?requirementTypeId=" + requirementTypeId;
					fillOPCenterGeneric(url, "newContentRight");
				}
			}

			xmlHttpOPCenterE.open("GET",url,true);
			xmlHttpOPCenterE.send(null);
	}


	
	//////////////////////////////////////////////////////////////////////////
	// //
	// Attributes (rTAttribute) //
	// //
	// ////////////////////////////////////////////////////////////////////////
	// called when someone clicks on to 'Create Attribute ' button in
	// contentCenterA
	
	function createAttributeForm(requirementTypeId){
			// set the other content centers to empty.
			
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			
			var url = "/GloreeJava2/jsp/AdministerProject/RTAttribute/createRTAttributeForm.jsp?requirementTypeId=" + requirementTypeId ;
			fillOPCenterGeneric(url, "requirementTypeDisplayDiv");
	}


	// Called when someone clicks the 'Create Attribute'
	// This is called when some one tries to create a new attribute for a
	// requirment type.
	// not to be confused with Attribute creation for a requirement.
	function createRTAttribute(requirementTypeId){
		
		var cRF = document.getElementById("createRTAttributeForm");
		 
		var attributeName = cRF.attributeName;
		var attributeType = cRF.attributeType;
		var attributeSortOrder = cRF.attributeSortOrder;
		var attributeDefaultValue = cRF.attributeDefaultValue;
		var attributeRequired = cRF.attributeRequired;
		var attributeDefaultDisplay = cRF.attributeDefaultDisplay;
		var attributeDescription = cRF.attributeDescription;
		var parentAttributeId = cRF.parentAttributeId;
		var attributeDropDownOptions = cRF.attributeDropDownOptions;
		var attributeImpactsVersion  = cRF.attributeImpactsVersion;
		var attributeImpactsTraceability = cRF.attributeImpactsTraceability;
		var attributeImpactsApprovalWorkflow  = cRF.attributeImpactsApprovalWorkflow;

		if (attributeName.value.length == 0) {
			alert ("Please enter a Attribute Name");
			attributeName.focus();
			attributeName.style.backgroundColor="#FFCC99";
			return(0);
		} 
		if (
				(trim(attributeName.value.toLowerCase())  == 'tag') ||
				(trim(attributeName.value.toLowerCase()) == 'version') ||
				(trim(attributeName.value.toLowerCase()) == 'name') ||
				(trim(attributeName.value.toLowerCase()) == 'description') ||
				(trim(attributeName.value.toLowerCase()) == 'owner') ||
				(trim(attributeName.value.toLowerCase()) == 'testing status') ||
				(trim(attributeName.value.toLowerCase()) == 'status') ||
				(trim(attributeName.value.toLowerCase()) == 'priority') ||
				(trim(attributeName.value.toLowerCase()) == 'percent complete') ||
				(trim(attributeName.value.toLowerCase()) == 'traceto') ||
				(trim(attributeName.value.toLowerCase()) == 'tracefrom')
			){
			alert ("Please note that the following Names are reserved for system attributes. 'Tag', 'Version', 'Name', 'Description', " +
					" 'Owner', 'Testing Status', 'Status', 'Priority', Percent Complete', 'TraceTo', 'TraceFrom'. Please " +
					" select a custom attribute name that does not conflict with these.");
			attributeName.focus();
			attributeName.style.backgroundColor="#FFCC99";
			return(0);
		}

		
		if ((attributeType.selectedIndex == 1) && (attributeDropDownOptions.value.length == 0))  {
			alert ("For Drop Down type attributes, you must enter a set of comma separated Drop Down Options.");
			attributeDropDownOptions.focus();
			attributeDropDownOptions.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if ((attributeType.selectedIndex == 2) && (attributeDropDownOptions.value.length == 0))  {
			alert ("For Drop Down - Multiple type attributes, you must enter a set of comma separated Drop Down Options.");
			attributeDropDownOptions.focus();
			attributeDropDownOptions.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		// for drop down type attribute, lets make sure that the default value is
		// in the selected list.
		if ((attributeType.selectedIndex == 1) && (attributeDefaultValue.value.length > 0))  {
			var pos= attributeDropDownOptions.value.indexOf(attributeDefaultValue.value);
			if (pos<0){
				alert ("The Default value you have chosen for this attribute is not one of the Drop Down Options");
				attributeDefaultValue.focus();
				attributeDefaultValue.style.backgroundColor="#FFCC99";
				return(0);
			}
		}
		
		// for drop down - Multiple type attribute, lets make sure that the default value is
		// in the selected list.
		if ((attributeType.selectedIndex == 2) && (attributeDefaultValue.value.length > 0))  {
			var pos= attributeDropDownOptions.value.indexOf(attributeDefaultValue.value);
			if (pos<0){
				alert ("The Default value you have chosen for this attribute is not one of the Drop Down - Multiple Options");
				attributeDefaultValue.focus();
				attributeDefaultValue.style.backgroundColor="#FFCC99";
				return(0);
			}
		}
		
		
		
		
		if (attributeSortOrder.value.length == 0) {
			alert ("Please enter a Attribute Sort Order value. This can be a number");
			attributeSortOrder.focus();
			attributeSortOrder.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (attributeDescription.value.length == 0) {
			alert ("Please enter a Attribute Description");
			attributeDescription.focus();
			attributeDescription.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (attributeDescription.value.length > 1000){
			alert(" Please ensure that your Descriptoin is not longer than 1000 characters. Your current message is " + attributeDescription.value.length + " characters long");
			attributeDescription.focus();
			attributeDescription.style.backgroundColor="#FFCC99";
			return(0);
		}
		

		// single quotes and doubel quotes are not permitted in dropdown options and default values.
		var attributeDropDownOptionsValue = attributeDropDownOptions.value;
		attributeDropDownOptionsValue = attributeDropDownOptionsValue.replace("'", " ");
		attributeDropDownOptionsValue = attributeDropDownOptionsValue.replace('"', ' ');
		
		var attributeDefaultValueValue = attributeDefaultValue.value;
		attributeDefaultValueValue = attributeDefaultValueValue.replace("'", " ");
		attributeDefaultValueValue = attributeDefaultValueValue.replace('"', ' ');
		
		
		// at this point before submitting changes to server, lets disable the submit button
		// to prevent accidenatal double clicks.
		document.getElementById("createAttributeButton").disabled = true;
		
		
		var url="/GloreeJava2/servlet/RequirementTypeAction";
		var params = "action=createAttribute&";
		params += "requirementTypeId=" + encodeURIComponent(requirementTypeId) + "&";
		params += "attributeName=" + encodeURIComponent(attributeName.value) + "&";
		params += "attributeType="+ encodeURIComponent(attributeType.value) + "&";
		params += "attributeSortOrder=" + encodeURIComponent(attributeSortOrder.value) + "&";
		params += "attributeRequired=" + encodeURIComponent(attributeRequired.value) + "&";
		params += "attributeDefaultDisplay=" + encodeURIComponent(attributeDefaultDisplay.value) + "&";
		params += "attributeDefaultValue=" + encodeURIComponent(attributeDefaultValueValue) + "&";
		params += "parentAttributeId=" + encodeURIComponent(parentAttributeId.value) + "&";
		params += "attributeDropDownOptions=" + encodeURIComponent(attributeDropDownOptionsValue) + "&";
		params += "attributeDescription=" + encodeURIComponent(attributeDescription.value)+ "&";
		params += "attributeImpactsVersion=" + encodeURIComponent(attributeImpactsVersion.value) + "&";
		params += "attributeImpactsTraceability=" + encodeURIComponent(attributeImpactsTraceability.value) + "&";
		params += "attributeImpactsApprovalWorkflow=" + encodeURIComponent(attributeImpactsApprovalWorkflow.value) + "&";
		params += "&bustcache=" + new Date().getTime() ;
		
		url = url + "?" + params;
		
		// Get the Ajax response from Attribute Creation and populate the
		// contentCenterA.
		// Note : this call has to be Synchronous , as otherwise the attribute
		// may not be created
		// by the time the fillExplorer happens..
/*
		xmlHttpOPCenterE =GetXmlHttpObject();
			
		xmlHttpOPCenterE.onreadystatechange=function() {
			if(xmlHttpOPCenterE.readyState==4){

				document.getElementById("requirementTypeDisplayDiv").innerHTML=xmlHttpOPCenterE.responseText;
								
				// Since we plan to display All the attributes in ContentRight,
				// lets call that now.

				url="/GloreeJava2/jsp/AdministerProject/RequirementType/displayAllObjectsInRequirementType.jsp?requirementTypeId=" + requirementTypeId;
				fillOPCenterGeneric(url, "newContentRight");
			}
		}

		xmlHttpOPCenterE.open("GET",url,true);
		xmlHttpOPCenterE.send(null);
		*/
		
		///// making a POST operation as we need to send a lot of data for dropdownoptions(10000 chars)
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.open("POST", url, true);

		// Send the proper header information along with the request
		xmlHttpOPCenterB.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xmlHttpOPCenterB.setRequestHeader("Content-length", params.length);
		xmlHttpOPCenterB.setRequestHeader("Connection", "close");
 
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){

				document.getElementById("requirementTypeDisplayDiv").innerHTML=xmlHttpOPCenterB.responseText;
								
				// Since we plan to display All the attributes in ContentRight,
				// lets call that now.

				url="/GloreeJava2/jsp/AdministerProject/RequirementType/displayAllObjectsInRequirementType.jsp?requirementTypeId=" + requirementTypeId;
				fillOPCenterGeneric(url, "newContentRight");
			}
		}
		xmlHttpOPCenterB.send(params);
		/////
	}
	
	
	
	// Called when someone clicks the 'Submit changes' button in the Create Attribute Form page.
	// not to be confused with Attribute creation for a requirement.
	function editRTAttribute(requirementTypeId){
		var cRF = document.getElementById("editRTAttributeForm");
		
		document.getElementById("attributeSubmitBtn").disabled = true;
		
		var a = cRF.rTAttributeId;
		 
		var attributeName = cRF.attributeName;
		var attributeType = cRF.attributeType;
		var attributeSortOrder = cRF.attributeSortOrder;
		var attributeDefaultValue = cRF.attributeDefaultValue;
		var attributeRequired = cRF.attributeRequired;
		var attributeDefaultDisplay = cRF.attributeDefaultDisplay;
		var attributeDescription = cRF.attributeDescription;
		var parentAttributeId = cRF.parentAttributeId;
		var attributeDropDownOptions = cRF.attributeDropDownOptions;
		var attributeImpactsVersion  = cRF.attributeImpactsVersion;
		var attributeImpactsTraceability = cRF.attributeImpactsTraceability;
		var attributeImpactsApprovalWorkflow  = cRF.attributeImpactsApprovalWorkflow;
		

		if (attributeName.value.length == 0) {
			alert ("Please enter a Attribute Name");
			attributeName.focus();
			attributeName.style.backgroundColor="#FFCC99";
			return(0);
		}


		if ((attributeType.selectedIndex == 1) && (attributeDropDownOptions.value.length == 0))  {
			alert ("For Drop Down type attributes, you must enter a set of comma separated Drop Down Options.");
			attributeDropDownOptions.focus();
			attributeDropDownOptions.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		// for drop down type attribute, lets make sure that the default value is
		// in the selected list.
		if ((attributeType.selectedIndex == 1) && (attributeDefaultValue.value.length > 0))  {
			var pos= attributeDropDownOptions.value.indexOf(attributeDefaultValue.value);
			if (pos<0){
				alert ("The Default value you have chosen for this attribute is not one of the Drop Down Options");
				attributeDefaultValue.focus();
				attributeDefaultValue.style.backgroundColor="#FFCC99";
				return(0);
			}
		}
		
		if (attributeSortOrder.value.length == 0) {
			alert ("Please enter a Attribute Sort Order value. This can be a number");
			attributeSortOrder.focus();
			attributeSortOrder.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (attributeDescription.value.length == 0) {
			alert ("Please enter a Attribute Description");
			attributeDescription.focus();
			attributeDescription.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (attributeDescription.value.length > 1000){
			alert(" Please ensure that your Descriptoin is not longer than 1000 characters. Your current message is " + attributeDescription.value.length + " characters long");
			attributeDescription.focus();
			attributeDescription.style.backgroundColor="#FFCC99";
			return(0);
		}
		

		// single quotes and doubel quotes are not permitted in dropdown options and default values.
		var attributeDropDownOptionsValue = attributeDropDownOptions.value;
		attributeDropDownOptionsValue = attributeDropDownOptionsValue.replace("'", " ");
		attributeDropDownOptionsValue = attributeDropDownOptionsValue.replace('"', ' ');
		
		var attributeDefaultValueValue = attributeDefaultValue.value;
		attributeDefaultValueValue = attributeDefaultValueValue.replace("'", " ");
		attributeDefaultValueValue = attributeDefaultValueValue.replace('"', ' ');
		
		

		
		
		var url="/GloreeJava2/servlet/RequirementTypeAction";
		var params = "action=editRTAttribute&";
		params += "rTAttributeId=" + a.value + "&";
		params += "requirementTypeId=" + encodeURIComponent(requirementTypeId) + "&";
		params += "attributeName=" + encodeURIComponent(attributeName.value) + "&";
		params += "attributeType="+ encodeURIComponent(attributeType.value) + "&";
		params += "attributeSortOrder=" + encodeURIComponent(attributeSortOrder.value) + "&";
		params += "attributeRequired=" + encodeURIComponent(attributeRequired.value) + "&";
		
		params += "attributeDefaultDisplay=" + encodeURIComponent(attributeDefaultDisplay.value) + "&";
		
		params += "attributeDefaultValue=" + encodeURIComponent(attributeDefaultValueValue) + "&";
		params += "parentAttributeId=" + encodeURIComponent(parentAttributeId.value) + "&";
		params += "attributeDropDownOptions=" + encodeURIComponent(attributeDropDownOptionsValue) + "&";
		params += "attributeDescription=" + encodeURIComponent(attributeDescription.value) + "&" ;
		params += "attributeImpactsVersion=" + encodeURIComponent(attributeImpactsVersion.value) + "&";
		params += "attributeImpactsTraceability=" + encodeURIComponent(attributeImpactsTraceability.value) + "&";
		params += "attributeImpactsApprovalWorkflow=" + encodeURIComponent(attributeImpactsApprovalWorkflow.value) + "&";
		params += "&bustcache=" + new Date().getTime() ;
		
		url = url + "?" + params;
		
		
		// Get the Ajax response from Attribute Creation and populate the
		// contentCenterA.
		// Note : this call has to be Synchronous , as otherwise the folder may
		// not be created
		// by the time the fillExplorer happens..
	/*	xmlHttpOPCenterE =GetXmlHttpObject();
			
		xmlHttpOPCenterE.onreadystatechange=function() {
			if(xmlHttpOPCenterE.readyState==4){
				
				document.getElementById("requirementTypeDisplayDiv").innerHTML=xmlHttpOPCenterE.responseText;
								
				// Since we plan to display All the attributes in ContentRight,
				// lets call that now.
				url="/GloreeJava2/jsp/AdministerProject/RequirementType/displayAllObjectsInRequirementType.jsp?requirementTypeId=" + requirementTypeId;
				fillOPCenterGeneric(url, "newContentRight");
			}
		}

		xmlHttpOPCenterE.open("GET",url,true);
		xmlHttpOPCenterE.send(null);
		*/
		
		
		
		///// making a POST operation as we need to send a lot of data for dropdownoptions(10000 chars)
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.open("POST", url, true);

		// Send the proper header information along with the request
		xmlHttpOPCenterB.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xmlHttpOPCenterB.setRequestHeader("Content-length", params.length);
		xmlHttpOPCenterB.setRequestHeader("Connection", "close");
 
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){

				document.getElementById("requirementTypeDisplayDiv").innerHTML=xmlHttpOPCenterE.responseText;
								
				// Since we plan to display All the attributes in ContentRight,
				// lets call that now.
				url="/GloreeJava2/jsp/AdministerProject/RequirementType/displayAllObjectsInRequirementType.jsp?requirementTypeId=" + requirementTypeId;
				fillOPCenterGeneric(url, "newContentRight");
			}
		}
		xmlHttpOPCenterB.send(params);
		/////
	}
	
	
	// called when someone requests to open an attribute in contentRight
	function editRTAttributeForm(rTAttributeId, requirementTypeId){
		
		// set the other content centers to empty.
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
	
		
		var url="/GloreeJava2/jsp/AdministerProject/RTAttribute/editRTAttributeForm.jsp";
		url=url+"?rTAttributeId="+ rTAttributeId + "&requirementTypeId=" + requirementTypeId;
		fillOPCenterGeneric(url, "requirementTypeDisplayDiv");
	}
	 

	// called when someone clicks on 'Delete Attribute' button in contentCenterB 
	function deleteRTAttributeForm(rTAttributeId, requirementTypeId){
			// set the other content centers to empty.
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			
			var url = "/GloreeJava2/jsp/AdministerProject/RTAttribute/deleteRTAttributeForm.jsp?rTAttributeId=" + rTAttributeId + "&requirementTypeId=" + requirementTypeId;
			fillOPCenterGeneric(url, "requirementTypeDisplayDiv");
	}
	

	// called when someone clicks on 'Delete Attribute' button in the 'Delete Attribute Form' screen. 
	function deleteRTAttribute(rTAttributeId, requirementTypeId){
			// set the other content centers to empty.
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";

			// at this point before submitting changes to server, lets disable the submit button
			// to prevent accidenatal double clicks.
			document.getElementById("deleteAttributeButton").disabled = true;
			
			
			var url="/GloreeJava2/servlet/RequirementTypeAction?action=deleteRTAttribute&rTAttributeId=" + rTAttributeId;
			url += "&bustcache=" + new Date().getTime() ;
			
			xmlHttpOPCenterE =GetXmlHttpObject();
			xmlHttpOPCenterE.onreadystatechange=function() {
				if(xmlHttpOPCenterE.readyState==4){
					document.getElementById("requirementTypeDisplayDiv").innerHTML=xmlHttpOPCenterE.responseText;

					// Since we plan to display All the attributes in
					// ContentRight, lets call that now.
					url = "/GloreeJava2/jsp/AdministerProject/RequirementType/displayAllObjectsInRequirementType.jsp?requirementTypeId=" + requirementTypeId;
					fillOPCenterGeneric(url, "newContentRight");
				}
			}

			xmlHttpOPCenterE.open("GET",url,true);
			xmlHttpOPCenterE.send(null);
	}


	//////////////////////////////////////////////////////////////////////////
	// //
	// AttributeValues (rAttributeValues) //
	// //
	// ////////////////////////////////////////////////////////////////////////
	
	

	// called when someone hits the 'Enter' button in the Create REquirement attributes section.
	// we capture the enter key event and treat it as if some one pressed 'create attributes' button.
	function  handleCreateAttributeValuesKeyPress(event, requirementId, attributeIdString, attributeRequiredIdString) {
		var keyCode = event.keyCode;
		if (keyCode == 13) {
			createRequirementAttributeValues(requirementId, attributeIdString, attributeRequiredIdString);
			return(0);
		}
	}

	
	
	

	// Called when someone clicks the 'Create Attribute' button in the Create
	// Requirement Attribute Form page.
	// This is called as part of Requirement creation process, not to be
	// confused with RequirementTypeAttribute
	//
	// NOTE
	// this is the GET. There is POST version of this method below
	//
	function createRequirementAttributeValues(requirementId, attributeIdString, attributeRequiredIdString){
		
		var url="/GloreeJava2/servlet/RequirementAction?action=createAttributeValues&requirementId=" + encodeURIComponent(requirementId) ;
		url += '&attributeIdString=' + encodeURIComponent(attributeIdString) ; 
		url += "&bustcache=" + new Date().getTime() ;
		
		
		
		// all the attributes in attributeRequiredIsString need to have some
		// value in them.
		
		requiredAttributes = attributeRequiredIdString.split('##');
		for (r in requiredAttributes){
			
			var requiredAttributeString = requiredAttributes[r];
			if (requiredAttributeString != null) {
				var splitA = requiredAttributeString.split('#');
				var id = splitA[0];
				var name = splitA[1];
				var attributeType = splitA[2];
				 
				var requiredAttribute = document.getElementById(id);
				
				if (requiredAttribute != null) {
					// this above line takes care of situations where we have 
					// the last # and that gave us an extra null row.
					if (attributeType == "DropDown"){
						if (requiredAttribute.selectedIndex == 0) {
							alert ( name + " is a Required attribute. Please enter a value");
							requiredAttribute.focus();
							requiredAttribute.style.backgroundColor="#FFCC99";
							return(0);
						}
					}
					else {
						if ((requiredAttribute.value == null) || (requiredAttribute.value.length == 0)) {
							alert ( name + " is a Required attribute. Please enter a value");
							requiredAttribute.focus();
							requiredAttribute.style.backgroundColor="#FFCC99";
							return(0);
						}
					}
				}
			}
		}	
		
		// we use the attributeIdString to split and figure out which attributes
		// to look for.
		// for these attributes , we get the value, build the URL string and .
		// we will use the same logic to get the attribute values and create
		// them in the db.
		attributeIds = attributeIdString.split('##');
		
		for (a in attributeIds){
			splitA = attributeIds[a].split('#');
			var id = splitA[0];
			var type = splitA[1];
			if (id) {
				var attribute = document.getElementById(id);
				// if this is a date type, lets run some java script validation to 
				// ensure that the mm/dd/yyyy format is maintained.
				if (type == 'Date'){
					var dateValue = attribute.value;
					if (dateValue != ""){
						// we do this validation only if some value has been entered in the date box.
						if (isValidDate(dateValue)==false){
							attribute.focus()
							attribute.style.backgroundColor="#FFCC99";
							return (0);
						}
					}
				}
				// if this is a number type, lets run some java script validation to 
				// ensure that the value is really a number.
				if (type == 'Number'){
					var numberValue = attribute.value;
					if (numberValue != ""){
						// we do this validation only if some value has been entered in the number box.
						if (isNaN(numberValue)){
							alert("Please enter a valid Number");
							attribute.focus()
							attribute.style.backgroundColor="#FFCC99";
							return (0);
						}
					}
				}
				if (type == 'DropDown'){
					url = url + '&' +  id + '='+  encodeURIComponent(attribute[attribute.selectedIndex].value) ;
				}
				if (type == 'DropDownMultiple' ){
					
					var selectedMultipleDropDownValues = "";
					for (var i = 0; i < attribute.options.length; i++) {
						if (attribute.options[i].selected) {
							selectedMultipleDropDownValues += attribute.options[i].value + ','; 
							
						}
					}
					url = url + '&' +  id + '='+  encodeURIComponent(selectedMultipleDropDownValues) ;
				}
				else {
					url = url + '&' + id + '='+  encodeURIComponent(attribute.value) ;
				}
			}
		}	
		
		// at this point before submitting changes to server, lets disable the submit button
		// to prevent accidenatal double clicks.
		document.getElementById("updateAttributesButton").disabled = true; 
		document.getElementById("contentCenterD").innerHTML= "<span class='normalText'> &nbsp;&nbsp;&nbsp;Working...</span>";	
		
		xmlHttpOPCenterE =GetXmlHttpObject();
		xmlHttpOPCenterE.onreadystatechange=function() {
			if(xmlHttpOPCenterE.readyState==4){
				
				
				document.getElementById("contentCenterD").style.display = 'block';
				document.getElementById("contentCenterD").innerHTML=xmlHttpOPCenterE.responseText;

				// since a change to the custom attributes could potentially update 
				// traceability and version and approval workflow, lets refresh those two screens.
				// Now fill contentCenterB with the Requirement Core Info
				displayRequirementCore(requirementId);
				
				
				// Now we build the traceability URL and redirect OPCenterC to it.
				url="/GloreeJava2/jsp/Requirement/displayRequirementTrace.jsp?requirementId=" + requirementId;
				fillOPCenterGeneric(url, "contentCenterC");				
				
			}
		}
		xmlHttpOPCenterE.open("GET",url,true);
		xmlHttpOPCenterE.send(null);
		
		
	}
	

	
	function createRequirementSingleAttributeValue(rowId, attributeLabel, requirementId, attributeIdString, attributeRequiredIdString){
		
		
		var targetDiv = "attributeDiv" + "-" + rowId + "-"+ requirementId + "-" + attributeLabel;
		
		
		var url="/GloreeJava2/servlet/RequirementAction?action=createAttributeValues&requirementId=" + encodeURIComponent(requirementId) ;
		url += '&attributeIdString=' + encodeURIComponent(attributeIdString) ; 
		url += '&singleAttribute=' + encodeURIComponent("true") ; 
		url += '&attributeLabel=' + encodeURIComponent(attributeLabel) ; 
		url += '&rowId=' + rowId ; 
		url += "&bustcache=" + new Date().getTime() ;
		
		
		
		// all the attributes in attributeRequiredIsString need to have some
		// value in them.
		
		requiredAttributes = attributeRequiredIdString.split('##');
		for (r in requiredAttributes){
			
			var requiredAttributeString = requiredAttributes[r];
			if (requiredAttributeString != null) {
				var splitA = requiredAttributeString.split('#');
				var id = splitA[0];
				var name = splitA[1];
				var attributeType = splitA[2];
				 
				var requiredAttribute = document.getElementById(requirementId + "-"+ id);
				
				if (requiredAttribute != null) {
					// this above line takes care of situations where we have 
					// the last # and that gave us an extra null row.
					if (attributeType == "DropDown"){
						if (requiredAttribute.selectedIndex == 0) {
							alert ( name + " is a Required attribute. Please enter a value");
							requiredAttribute.focus();
							requiredAttribute.style.backgroundColor="#FFCC99";
							return(0);
						}
					}
					else {
						if ((requiredAttribute.value == null) || (requiredAttribute.value.length == 0)) {
							alert ( name + " is a Required attribute. Please enter a value");
							requiredAttribute.focus();
							requiredAttribute.style.backgroundColor="#FFCC99";
							return(0);
						}
					}
				}
			}
		}	
		
		// we use the attributeIdString to split and figure out which attributes
		// to look for.
		// for these attributes , we get the value, build the URL string and .
		// we will use the same logic to get the attribute values and create
		// them in the db.
		attributeIds = attributeIdString.split('##');
		
		for (a in attributeIds){
			splitA = attributeIds[a].split('#');
			var id = splitA[0];
			var type = splitA[1];
			if (id) {
				var attribute = document.getElementById(requirementId + "-"+ id);
				// if this is a date type, lets run some java script validation to 
				// ensure that the mm/dd/yyyy format is maintained.
				if (type == 'Date'){
					var dateValue = attribute.value;
					if (dateValue != ""){
						// we do this validation only if some value has been entered in the date box.
						if (isValidDate(dateValue)==false){
							attribute.focus()
							attribute.style.backgroundColor="#FFCC99";
							return (0);
						}
					}
				}
				// if this is a number type, lets run some java script validation to 
				// ensure that the value is really a number.
				if (type == 'Number'){
					var numberValue = attribute.value;
					if (numberValue != ""){
						// we do this validation only if some value has been entered in the number box.
						if (isNaN(numberValue)){
							alert("Please enter a valid Number");
							attribute.focus()
							attribute.style.backgroundColor="#FFCC99";
							return (0);
						}
					}
				}
				if (type == 'DropDown'){
					url = url + '&' +  id + '='+  encodeURIComponent(attribute[attribute.selectedIndex].value) ;
				}
				if (type == 'DropDownMultiple' ){
					
					var selectedMultipleDropDownValues = "";
					for (var i = 0; i < attribute.options.length; i++) {
						if (attribute.options[i].selected) {
							selectedMultipleDropDownValues += attribute.options[i].value + ','; 
							
						}
					}
					url = url + '&' +  id + '='+  encodeURIComponent(selectedMultipleDropDownValues) ;
				}
				else {
					url = url + '&' + id + '='+  encodeURIComponent(attribute.value) ;
				}
			}
		}	 

		fillOPCenterGeneric(url, targetDiv);
		

		
		
	}
	
	
	//
	// NOTE
	// this is the POST. There is GET version of this method above
	//
	function createRequirementAttributeValuesUSINGPOST(requirementId, attributeIdString, attributeRequiredIdString){
		
		var url="/GloreeJava2/servlet/RequirementAction";
		
		var params = "requirementId=" + encodeURIComponent(requirementId) ;
		params += '&action=' + encodeURIComponent("createAttributeValues") ; 
		params += '&attributeIdString=' + encodeURIComponent(attributeIdString) ; 
		params += "&bustcache=" + new Date().getTime() ;
		
		// all the attributes in attributeRequiredIsString need to have some
		// value in them.
		
		requiredAttributes = attributeRequiredIdString.split('##');
		for (r in requiredAttributes){
			
			var requiredAttributeString = requiredAttributes[r];
			if (requiredAttributeString != null) {
				var splitA = requiredAttributeString.split('#');
				var id = splitA[0];
				var name = splitA[1];
				var attributeType = splitA[2];
				 
				var requiredAttribute = document.getElementById(id);
				
				if (requiredAttribute != null) {
					// this above line takes care of situations where we have 
					// the last # and that gave us an extra null row.
					if (attributeType == "DropDown"){
						if (requiredAttribute.selectedIndex == 0) {
							alert ( name + " is a Required attribute. Please enter a value");
							requiredAttribute.focus();
							requiredAttribute.style.backgroundColor="#FFCC99";
							return(0);
						}
					}
					else {
						if ((requiredAttribute.value == null) || (requiredAttribute.value.length == 0)) {
							alert ( name + " is a Required attribute. Please enter a value");
							requiredAttribute.focus();
							requiredAttribute.style.backgroundColor="#FFCC99";
							return(0);
						}
					}
				}
			}
		}	
		
		// we use the attributeIdString to split and figure out which attributes
		// to look for.
		// for these attributes , we get the value, build the URL string and .
		// we will use the same logic to get the attribute values and create
		// them in the db.
		attributeIds = attributeIdString.split('##');
		
		for (a in attributeIds){
			splitA = attributeIds[a].split('#');
			var id = splitA[0];
			var type = splitA[1];
			if (id) {
				var attribute = document.getElementById(id);
				// if this is a date type, lets run some java script validation to 
				// ensure that the mm/dd/yyyy format is maintained.
				if (type == 'Date'){
					var dateValue = attribute.value;
					if (dateValue != ""){
						// we do this validation only if some value has been entered in the date box.
						if (isValidDate(dateValue)==false){
							attribute.focus()
							attribute.style.backgroundColor="#FFCC99";
							return (0);
						}
					}
				}
				// if this is a number type, lets run some java script validation to 
				// ensure that the value is really a number.
				if (type == 'Number'){
					var numberValue = attribute.value;
					if (numberValue != ""){
						// we do this validation only if some value has been entered in the number box.
						if (isNaN(numberValue)){
							alert("Please enter a valid Number");
							attribute.focus()
							attribute.style.backgroundColor="#FFCC99";
							return (0);
						}
					}
				}
				if (type == 'DropDown'){
					params = params + '&' +  id + '='+  encodeURIComponent(attribute[attribute.selectedIndex].value) ;
				}
				if (type == 'DropDownMultiple' ){
					
					var selectedMultipleDropDownValues = "";
					for (var i = 0; i < attribute.options.length; i++) {
						if (attribute.options[i].selected) {
							selectedMultipleDropDownValues += attribute.options[i].value + ','; 
							
						}
					}
					params = params + '&' +  id + '='+  encodeURIComponent(selectedMultipleDropDownValues) ;
				}
				else {
					// srt uncomment the line below
					params = params + '&' + id + '='+  encodeURIComponent(attribute.value) ;
					//url = url + '&' + id + '='+  attribute.value ;
				}
			}
		}	
		
		// at this point before submitting changes to server, lets disable the submit button
		// to prevent accidenatal double clicks.
		document.getElementById("updateAttributesButton").disabled = true; 
		document.getElementById("contentCenterD").innerHTML= "<span class='normalText'> &nbsp;&nbsp;&nbsp;Working...</span>";	
		
		xmlHttpOPCenterE =GetXmlHttpObject();
		xmlHttpOPCenterE.open("POST", url, true);


		// Send the proper header information along with the request
		//xmlHttpOPCenterE.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xmlHttpOPCenterE.setRequestHeader('Content-Type','application/x-www-form-urlencoded; charset=UTF-8');  
		xmlHttpOPCenterE.setRequestHeader("Content-length", params.length);
		xmlHttpOPCenterE.setRequestHeader("Connection", "close");

		
		xmlHttpOPCenterE.onreadystatechange=function() {
			if(xmlHttpOPCenterE.readyState==4){
				
				
				document.getElementById("contentCenterD").style.display = 'block';
				document.getElementById("contentCenterD").innerHTML=xmlHttpOPCenterE.responseText;

				// since a change to the custom attributes could potentially update 
				// traceability and version and approval workflow, lets refresh those two screens.
				// Now fill contentCenterB with the Requirement Core Info
				displayRequirementCore(requirementId);
				
				
				// Now we build the traceability URL and redirect OPCenterC to it.
				url="/GloreeJava2/jsp/Requirement/displayRequirementTrace.jsp?requirementId=" + requirementId;
				fillOPCenterGeneric(url, "contentCenterC");				
				
			}
		}
		xmlHttpOPCenterE.send(params);
		
		
		
	}
	



	//////////////////////////////////////////////////////////////////////////
	// //
	// List Report / Bulk Edit
	// //
	// ////////////////////////////////////////////////////////////////////////
	
	
	// called when someone clicks on a Report that was saved in the past.
	function displayExistingReport(folderId, reportId, reportType){
		
			// set the other content centers to empty.
			
			document.getElementById("contentCenter").style.display = 'block';
			
			document.getElementById("contentCenterB").style.display = "none";
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";


			document.getElementById("contentCenterE").style.display = 'block';
			document.getElementById("contentCenterF").style.display = 'block';
			// lets display the right tabs.
			// lets implement the tabbed look & feel of menu items.
			//var focusTab = "url(/GloreeJava2/images/focusTab.png)";
			//var nonFocusTab = "url(/GloreeJava2/images/nonFocusTab.png)"; 
			// the clicked item is set to gray, and the rest are set to the yellow color.
			
			
			
			
			
			// ContentCenter E and F are the targets. E will hold the filter
			// F will hold the actual data. So lets put the working gif in both.
			document.getElementById("contentCenterE").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";	
			document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			

			// Now display the Report.
			// since we want to fill the report definition into the filter boxes
			// and execute the report, we will make an synch call.

			
			
			var url = "/GloreeJava2/servlet/ReportAction?folderId=" + encodeURIComponent(folderId);
			url += "&action=" + encodeURIComponent("displayExistingReport");
			url += "&reportId=" + encodeURIComponent(reportId);
			url += "&bustcache=" + new Date().getTime() ;
			
			
			xmlHttpOPCenterE =GetXmlHttpObject();
			xmlHttpOPCenterE.onreadystatechange=function() {
				if(xmlHttpOPCenterE.readyState==4){
					
					
					document.getElementById("contentCenterE").style.display = 'block';
					document.getElementById("contentCenterE").innerHTML=xmlHttpOPCenterE.responseText;
					// now that we have filled up the filters, lets call the run
					// report method.
					reportAction(folderId,'runReport', reportType);
				}
			}
			xmlHttpOPCenterE.open("GET",url,true);
			xmlHttpOPCenterE.send(null);

	}

	// called when someone clicks on a number in any of the metrics reports.
	// this is not a stored report, but a dynamic one we generate on the fly.
	function displayDynamicReport(defaultDisplayAttributes, folderId,  reportType, reportDefinition){
			
		
			// set the other content centers to empty.
			document.getElementById("contentCenterB").style.display = "none";
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";

			// ContentCenter E and F are the targets. E will hold the filter
			// F will hold the actual data. So lets put the working gif in both.
			document.getElementById("contentCenterE").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";	
			document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			
			// Now display the Report.
			// since we want to fill the report definition into the filter boxes
			// and execute the report, we will make an synch call.
			var url = "";
			if (reportType == "list") {
				url = "/GloreeJava2/servlet/ReportAction?folderId=" + encodeURIComponent(folderId);
				url += "&action=" + encodeURIComponent("displayDynamicReport");
				url += "&reportDefinition=" + encodeURIComponent(reportDefinition);
				url += "&reportType=" + encodeURIComponent("list");
				url += "&bustcache=" + new Date().getTime() ;
			}
			
			xmlHttpOPCenterE =GetXmlHttpObject();
			xmlHttpOPCenterE.onreadystatechange=function() {
				if(xmlHttpOPCenterE.readyState==4){
					document.getElementById("contentCenterE").style.display = 'block';
					document.getElementById("contentCenterE").innerHTML=xmlHttpOPCenterE.responseText;
					
					// lets work through all the displayDefaultAttributes 
					// and flag the custom attributes in the report to be displayed
					try {
						var customAttributesDisplay = document.getElementById("customAttributesDisplay");
						attributeIds = defaultDisplayAttributes.split(':#:');
						for (a in attributeIds){
							splitA = attributeIds[a].split(':##:');
							var id = splitA[0];
							var label = splitA[1];
						
							if (label) {
								// lets iterate through the attibute adn find an option with this value
								// and set it to selected
								for (var i = 0; i < customAttributesDisplay.options.length; i++){
									var currentOption = customAttributesDisplay.options[i];
									if (currentOption.value == label){
										currentOption.selected  = true;
									}
								}
							}
						}	

					}
					catch (e){
						alert (e.message);
					}
					// now that we have filled up the filters, lets call the run
					// report method.
					reportAction(folderId,'runReport', reportType);
				}
			}
			xmlHttpOPCenterE.open("GET",url,true);
			xmlHttpOPCenterE.send(null);

	}

	
	
	
	// called when someone clicks on 'Create New Report' button in the Folder
	// Display
	function createNewListReport(folderId){			
		// set the other content centers to empty.
		document.getElementById("contentCenterB").style.display = 'none';
		document.getElementById("contentCenterC").style.display = 'none';
		document.getElementById("contentCenterD").style.display = 'none';
		document.getElementById("contentCenterF").style.display = 'none';


		// First time, display the filter and the report
		var url="/GloreeJava2/jsp/Report/displayListReport.jsp?folderId=" + folderId;
		url += "&bustcache=" + new Date().getTime() ;
		xmlHttpOPCenterE =GetXmlHttpObject();		
		xmlHttpOPCenterE.onreadystatechange=function() {
			if(xmlHttpOPCenterE.readyState==4){
				document.getElementById('contentCenterE').style.display = 'block'; 
				document.getElementById('contentCenterE').innerHTML=xmlHttpOPCenterE.responseText;
			}
		}
		xmlHttpOPCenterE.open("GET",url,true);
		xmlHttpOPCenterE.send(null);		
	}
	
	
	// this function is used to select all the checkboxes in the form
	// used in BulkEdit form.
	function selectAllRequirementInDisplayReportData() {
		var thisForm = document.getElementById("displayListReportDataForm");
		
		// if only one req is displayed in data grid, the checkbox stops behaving like an array
		// and behaves like a single element. in this scenario checkbox.length craps out.
		// to catch this scenario we have to do this...
		if (thisForm.requirementId.length == null){
			// only one requirement row in data grid.
			thisForm.requirementId.checked = true;
		}
		else {
			// multiple requirement rows in data grid.
			for (i=0; i<thisForm.requirementId.length;i++){
				thisForm.requirementId[i].checked = true;
			}			
		}
	}

	// this function is used to DEselect all the checkboxes in the form
	// used in BulkEdit form.
	function deSelectAllRequirementInDisplayReportData() {
		var thisForm = document.getElementById("displayListReportDataForm");
		// if only one req is displayed in data grid, the checkbox stops behaving like an array
		// and behaves like a single element. in this scenario checkbox.length craps out.
		// to catch this scenario we have to do this...
		if (thisForm.requirementId.length == null){
			// only one requirement row in data grid.
			thisForm.requirementId.checked = false;
		}
		else {
			// multiple requirement rows in data grid.
			for (i=0; i<thisForm.requirementId.length;i++){
				thisForm.requirementId[i].checked = false;
			}			
		}		
	}

	// this function is used to add filter conditions
	// to both list report and trace tree reports.
	function addReportFilterCondition() {
		var addAFilter = document.getElementById("addAFilter");
		if (addAFilter.selectedIndex == 1) {
			document.getElementById('includeSubFoldersSearch').checked = true;
			document.getElementById('includeSubFoldersFilterDiv').style.display = 'block';
		}
		if (addAFilter.selectedIndex == 2) {
			document.getElementById('danglingSearch').checked = true;
			document.getElementById('danglingFilterDiv').style.display = 'block';
		}
		if (addAFilter.selectedIndex == 3) {
			document.getElementById('orphanSearch').checked = true;
			document.getElementById('orphanFilterDiv').style.display = 'block';
		}
		if (addAFilter.selectedIndex == 4) {
			document.getElementById('completedSearch').checked = true;
			document.getElementById('completedFilterDiv').style.display = 'block';
		}
		if (addAFilter.selectedIndex == 5) {
			document.getElementById('incompleteSearch').checked = true;
			document.getElementById('incompleteFilterDiv').style.display = 'block';
		}
		if (addAFilter.selectedIndex == 6) {
			document.getElementById('suspectUpStreamSearch').checked = true;
			document.getElementById('suspectUpStreamFilterDiv').style.display = 'block';
		}
		if (addAFilter.selectedIndex == 7) {
			document.getElementById('suspectDownStreamSearch').checked = true;
			document.getElementById('suspectDownStreamFilterDiv').style.display = 'block';
		}
		
		if (addAFilter.selectedIndex == 8) {
			document.getElementById('lockedSearch').checked = true;
			document.getElementById('lockedFilterDiv').style.display = 'block';
		}
		
		

		/////////////////////////////////////////////////////
		if (addAFilter.selectedIndex == 10) {
			document.getElementById('inRTBaselineFilterDiv').style.display = 'block';
		}

		if (addAFilter.selectedIndex == 11) {
			document.getElementById('changedAfterRTBaselineFilterDiv').style.display = 'block';
		}
		

		/////////////////////////////////////////////////////
		if (addAFilter.selectedIndex == 13) {
			document.getElementById('nameFilterDiv').style.display = 'block';
		}

		if (addAFilter.selectedIndex == 14) {
			document.getElementById('descriptionFilterDiv').style.display = 'block';
		}

		if (addAFilter.selectedIndex == 15) {
			document.getElementById('ownerFilterDiv').style.display = 'block';
		}

		if (addAFilter.selectedIndex == 16) {
			document.getElementById('externalURLFilterDiv').style.display = 'block';
		}

				
		/////////////////////////////////////////////////////
		if (addAFilter.selectedIndex == 18) {
			document.getElementById('pctCompleteFilterDiv').style.display = 'block';
		}

		if (addAFilter.selectedIndex == 19) {
			document.getElementById('statusFilterDiv').style.display = 'block';
		}

		if (addAFilter.selectedIndex == 20) {
			document.getElementById('priorityFilterDiv').style.display = 'block';
		}

		if (addAFilter.selectedIndex == 21) {
			document.getElementById('testingStatusFilterDiv').style.display = 'block';
		}

		
		/////////////////////////////////////////////////////
		if (addAFilter.selectedIndex == 23) {
			document.getElementById('approvedByFilterDiv').style.display = 'block';
		}

		if (addAFilter.selectedIndex == 24) {
			document.getElementById('rejectedByFilterDiv').style.display = 'block';
		}

		if (addAFilter.selectedIndex == 25) {
			document.getElementById('pendingByFilterDiv').style.display = 'block';
		}


		/////////////////////////////////////////////////////
		if (addAFilter.selectedIndex == 27) {
			document.getElementById('traceToFilterDiv').style.display = 'block';
		}

		if (addAFilter.selectedIndex == 28) {
			document.getElementById('traceFromFilterDiv').style.display = 'block';
		}
		
		// lets handle custom attributes.
		// custom attributes are dynamic in nature. So at the time of writing the  javascript
		// we won't know what to expect.
		// however, the addAFilter.selectedIndex.value will have the id of the div that 
		// we will need to unblock.
		var customAttributeDivName = addAFilter[addAFilter.selectedIndex].value
		// lets see if the option value picked up is custom attribute
		var pos= customAttributeDivName.indexOf("customA")
		if (pos>=0){
			// lets get the div and display it.
			document.getElementById(customAttributeDivName).style.display = 'block';
		}
		
		addAFilter.selectedIndex = 0;

	}
	// this function is used to clear all the user selections in 
	// reportFilter.
	function clearReportFilter() {
		
		var thisForm = document.getElementById("reportFilterForm");
		// lets empty the checkboxes.
		thisForm.danglingSearch.checked = false;
		thisForm.orphanSearch.checked = false;
		thisForm.completedSearch.checked = false;
		thisForm.incompleteSearch.checked = false;
		thisForm.suspectUpStreamSearch.checked = false;
		thisForm.suspectDownStreamSearch.checked = false;
		thisForm.lockedSearch.checked = false;
		thisForm.includeSubFoldersSearch.checked = false;

		// lets empty the text boxes.
		thisForm.nameSearch.value = '';
		thisForm.descriptionSearch.value = '' ;
		thisForm.ownerSearch.value = '' ;
		thisForm.externalURLSearch.value = '';
		thisForm.approvedBySearch.value = '';
		thisForm.rejectedBySearch.value = '';
		thisForm.pendingBySearch.value = '';
		thisForm.traceToSearch.value = '';
		thisForm.traceFromSearch.value = '';
		thisForm.pctCompleteSearch.value = '';
		
		// lets emtpy the baselines.
		thisForm.inRTBaselineSearch.selectedIndex = 0;
		thisForm.changedAfterRTBaselineSearch.selectedIndex = 0;
		
		
		// lets empty the multi select boxes.
		
		var testingStatusSearchObject = thisForm.testingStatusSearch;
		for (var i = 0; i < testingStatusSearchObject.options.length; i++) {
			if (testingStatusSearchObject.options[i].selected) {
				testingStatusSearchObject.options[i].selected = false; 
			}
		}
		
		var statusSearchObject = thisForm.statusSearch;;
		for (var i = 0; i < statusSearchObject.options.length; i++) {
			if (statusSearchObject.options[i].selected) {
				statusSearchObject.options[i].selected = false;
			}
		}
		
		

		var prioritySearchObject = thisForm.prioritySearch;
		for (var i = 0; i < prioritySearchObject.options.length; i++) {
			if (prioritySearchObject.options[i].selected) {
				prioritySearchObject.options[i].selected = false; 
			}
		}
		
		// lets empty the custom attributes.
		
		var attributeIdString = thisForm.attributeIdString;		
		attributeIds = attributeIdString.value.split('##');
		for (a in attributeIds){
			splitA = attributeIds[a].split('#');
			var id = splitA[0];
			var type = splitA[1];
		
			if (id) {
				var attribute = document.getElementById(id);
				if (type == 'DropDown'){
					// since this is a multi select, iterate through this to get selected values.
		
					var dropDownSearch = "";
					for (var i = 0; i < attribute.options.length; i++) {
						if (attribute.options[i].selected) {
							attribute.options[i].selected = false;
						}
					}
				}
				else {
					attribute.value = '' ;
				}
			}
		}	
	}

	// this is the method that handles the bulk update requests.
	// its a cousin of bulkEditActionForm but has been modified to suit user dashboard.
	function bulkEditActionFormForUserDashboard(setObject, dataType, requirementTypeShortName){
		var targetArtifact = "";
		var targetValue = "";
		var targetRequirements  = "";
		var displayListReportDataForm = document.getElementById("displayListReportDataForm"); 
		
		// now lets get target requirements. These are the selected reqs.
		// we will need to get all the reqs and concatenate them with :##:
		// if only one req is displayed in data grid, the checkbox stops behaving like an array
		// and behaves like a single element. in this scenario checkbox.length craps out.
		// to catch this scenario we have to do this...
		if (displayListReportDataForm.requirementId.length == null){
			// only one requirement row in data grid.
			if (displayListReportDataForm.requirementId.checked == true ){
				targetRequirements += displayListReportDataForm.requirementId.value + ":##:";
			}
		}
		else {
			// multiple requirement rows in data grid.
			for (i=0; i<displayListReportDataForm.requirementId.length;i++){
				if (displayListReportDataForm.requirementId[i].checked == true ){
					targetRequirements += displayListReportDataForm.requirementId[i].value + ":##:";
				}
			}		
			
		}
	
	
		// lets make sure that at least some requirements are selected.
		if (targetRequirements == '') {
			alert("Please select at least one Requirement to Update"); 
			return(0);
 		}
		
		
		if (setObject == 'submitRequirementForApproval'){
			targetValue = '';
			targetArtifact = 'submitRequirementForApproval';
		}

		if (setObject == 'approveRequirement'){
			targetValue = '';
			targetArtifact = 'approveRequirement';
		}


		if (setObject.indexOf("rejectRequirement") >= 0 ){
			// setObject has rejectRequirement:##:approvalNote
			// lets get the approvalNote and store it in the targetValue.
			var rejection = setObject.split(":##:");
			targetValue = rejection[1]; 
			targetArtifact = "rejectRequirement";
		}
		
		// lets empty the prompt div box
		document.getElementById("bulkEditActionResponse").innerHTML = "";
		
		
		// lets build the url here.
		var url = "/GloreeJava2/servlet/ReportAction";
		
		var params = "action=bulkEditForUserDashboard" ;
		params += "&targetArtifact=" + encodeURIComponent(targetArtifact);
		params += "&targetValue=" + encodeURIComponent(targetValue);
		params += "&targetRequirements=" + encodeURIComponent(targetRequirements);
		params += "&bustcache=" + new Date().getTime() ;
		
		// since the number of selected reqs can be large and get has a limit
		// we will use post.
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.open("POST", url, true);

		// Send the proper header information along with the request
		xmlHttpOPCenterB.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xmlHttpOPCenterB.setRequestHeader("Content-length", params.length);
		xmlHttpOPCenterB.setRequestHeader("Connection", "close");

		// ContentCenter F is the target.
		// F will hold the actual data. So lets put the working gif in it
		document.getElementById("bulkEditActionResponse").style.display = 'block';
		document.getElementById("bulkEditActionResponse").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("bulkEditActionResponse").style.display = 'block';
				document.getElementById("bulkEditActionResponse").innerHTML=xmlHttpOPCenterB.responseText;
				if (requirementTypeShortName == 'all'){
					displayUserRequirementsOfAllReqTypes(dataType);
				}
				else {
					displayUserRequirements(requirementTypeShortName, dataType);
				}
				
			}
		}
		xmlHttpOPCenterB.send(params);
		return(1);
	}
	
	function showCustomAttributeToUpdate(){
		try{
		
			var customAttributeDisplaySelectorObject = document.getElementById("customAttributeDisplaySelector");
			var customAttributeDisplayId = customAttributeDisplaySelectorObject[customAttributeDisplaySelectorObject.selectedIndex].value;
			
			var x  = document.getElementsByClassName("updateAttributesDiv");
			for (var i = 0; i < x.length; i++) {
			  	 x[i].style.display = "none";
			}
			
			document.getElementById("updateAttributeDiv" + customAttributeDisplayId).style.display="block";
		}
		catch(e){
			console.log(e);
		}
	}
	
	// this is the method that handles the bulk update requests.
	function bulkEditActionForm(setObject, folderId, traceTreeRootFolderId, traceTree){
		var targetArtifact = "";
		var targetValue = "";
		var targetRequirements  = "";
		var displayListReportDataForm = document.getElementById("displayListReportDataForm"); 
		
		// now lets get target requirements. These are the selected reqs.
		// we will need to get all the reqs and concatenate them with :##:
		// if only one req is displayed in data grid, the checkbox stops behaving like an array
		// and behaves like a single element. in this scenario checkbox.length craps out.
		// to catch this scenario we have to do this...
		if (displayListReportDataForm.requirementId.length == null){
			// only one requirement row in data grid.
			if (displayListReportDataForm.requirementId.checked == true ){
				targetRequirements += displayListReportDataForm.requirementId.value + ":##:";
			}
		}
		else {
			// multiple requirement rows in data grid.
			for (i=0; i<displayListReportDataForm.requirementId.length;i++){
				if (displayListReportDataForm.requirementId[i].checked == true ){
					targetRequirements += displayListReportDataForm.requirementId[i].value + ":##:";
				}
			}		
			
		}
	
	
		// lets make sure that at least some requirements are selected.
		if (targetRequirements == '') {
			alert("Please select at least one Requirement to Update"); 
			return(0);
 		}
		
		
		
		// first lets make the ContentCenter invisible. This will be reset
		// with data with Ajax comes back.
		// The Ajax UI principle here is set the target of AJAX to invisible
		// and when AJAX responds display it.
		// document.getElementById("bulkEditActionResponse").style.display =
		// 'none';
		// document.getElementById("contentCenterF").style.display = 'none';
		
		
		if (setObject == 'addComment'){
			// addRequirementToBaseline is a drop down, so some special handling.
			var bulkCommentNote = document.getElementById("bulkCommentNote");
			targetValue = bulkCommentNote.value;
			targetArtifact = 'addComment';
		}
		
		if (setObject == 'addRequirementToBaseline'){
			// addRequirementToBaseline is a drop down, so some special handling.
			var addRequirementToBaseline = document.getElementById("addRequirementToBaseline");
			targetValue = addRequirementToBaseline[addRequirementToBaseline.selectedIndex].value;
			targetArtifact = 'addRequirementToBaseline';
		}
		
		if (setObject == 'removeRequirementsFromBaseline'){
			// removeRequirementsFromBaseline is a drop down, so some special handling.
			var removeRequirementsFromBaseline = document.getElementById("removeRequirementsFromBaseline");
			targetValue = removeRequirementsFromBaseline[removeRequirementsFromBaseline.selectedIndex].value;
			targetArtifact = 'removeRequirementsFromBaseline';
		}		
		
		if (setObject == 'setOwner'){
			var setOwnerObject = document.getElementById("setOwner");
			
			var setOwner = setOwnerObject[setOwnerObject.selectedIndex].value;
			
			targetValue = setOwner;
			targetArtifact = 'owner';
		}
		
		
		if (setObject == 'setPriority'){
			priority = document.getElementById("setPriority");
			targetValue = priority[priority.selectedIndex].value;
			targetArtifact = 'priority';
		}

		if (setObject == 'setExternalURL'){
			externalURL = document.getElementById("setExternalURL");
			
			if (externalURL.value.length == 0){
				alert ("Please enter value for External URL");
				externalURL.style.backgroundColor="#FFCC99";
				externalURL.focus();
				return(0);
			}
			
			targetValue = externalURL.value;
			targetArtifact = 'externalURL';
		}
		
		if (setObject == 'setPctComplete'){
			pctComplete = document.getElementById("setPctComplete");
			
			if (isNaN(pctComplete.value)){
				alert ("Please enter a valid number for percent complete");
				pctComplete.style.backgroundColor="#FFCC99";
				pctComplete.focus();
				return(0);
			}
			
			if ((pctComplete.value < 0) || (pctComplete.value > 100) ) {
				alert ("Please enter a valid number between 0 and 100 for percent complete");
				pctComplete.style.backgroundColor="#FFCC99";
				pctComplete.focus();
				return(0);
			}
			
			targetValue = pctComplete.value;
			targetArtifact = 'pctComplete';
		}
		
		if (setObject == 'setFolder'){
			setFolder = document.getElementById("setFolder");
			targetValue = setFolder[setFolder.selectedIndex].value;
			targetArtifact = 'setFolder';
		}
		
		if (setObject == 'setTestingStatus'){
			testingStatus = document.getElementById("testingStatus");
			targetValue = testingStatus[testingStatus.selectedIndex].value;
			targetArtifact = 'setTestingStatus';
		}
		
		
		if (setObject == 'replaceText'){
			searchString = document.getElementById("searchString");
			
			if (searchString.value.length == 0){
				alert ("Please enter value for Search String to be replaced");
				searchString.style.backgroundColor="#FFCC99";
				searchString.focus();
				return(0);
			}
			
			replaceString = document.getElementById("replaceString");
			
			if (replaceString.value.length == 0){
				alert ("Please enter value for replace String to be replaced");
				replaceString.style.backgroundColor="#FFCC99";
				replaceString.focus();
				return(0);
			}
			
			var caseObject = document.getElementById("case");
			var replaceInObject = document.getElementById("replaceIn");
			
			var searchStringValue = searchString.value;
			var replaceStringValue = replaceString.value;
			var caseValue  = caseObject[caseObject.selectedIndex].value;
			var replaceInValue = replaceInObject[replaceInObject.selectedIndex].value;
			
			targetValue = searchStringValue + ":##:" + replaceStringValue + ":##:" + caseValue + ":##:" + replaceInValue ;
			
			targetArtifact = 'replaceText';
		}
		
		
		if (setObject == 'deleteRequirementsPrompt'){
			var prompt= "<div  class='alert alert-success'>";
			prompt += "<span class='headingText'>Are you sure you want to delete this requirement ?<br><br>";
			prompt += "Please note that this Requirement, it's Attributes and Traces will be Soft Deleted in the system, " +
					" so you can restore them later.<br><br>If you want to permanently delete the requirement, please " +
					" use the PURGE option.";
			prompt += "</span><br>"
			prompt += "\n<span class='normalText'><input type='button' name='Yes, Delete' value='Yes, Delete' onClick=" +
					" 'bulkEditActionForm(\"deleteRequirements\"," +  folderId + ");'>";
			prompt += "\n<input type='button' name='Cancel' value='Cancel' ";
			prompt += "onClick= 'document.getElementById(\"bulkDeleteRequirementsPrompt\").innerHTML = \"\";'>";
			prompt += "\n<br></span></div><br>"
			// We set the prompt with the appropriate values.
			document.getElementById("bulkDeleteRequirementsPrompt").innerHTML = prompt;
			return(0);
		}

		if (setObject == 'deleteRequirements'){
			// lets empty the alert prompt.
			document.getElementById("bulkDeleteRequirementsPrompt").innerHTML = "";
			
			targetValue = "delete";
			targetArtifact = 'deleteRequirements';
		}
		
		if (setObject == 'purgeRequirementsPrompt'){
			var prompt= "<div  class='alert alert-success'>";
			prompt += "<span class='headingText'>Are you sure you want to <font color='red'> Permanently </font> delete this requirement ?<br><br>";
			prompt += "Please note  that this will permanently remove the Requirement, it's Attributes and Traces.";
			prompt += "</span><br>"
			prompt += "\n<span class='normalText'><input type='button' name='Yes, Purge' value='Yes, Purge' onClick=" +
					" 'bulkEditActionForm(\"purgeRequirements\"," +  folderId + ");'>";
			prompt += "\n<input type='button' name='Cancel' value='Cancel' ";
			prompt += "onClick= 'document.getElementById(\"bulkPurgeRequirementsPrompt\").innerHTML = \"\";'>";
			prompt += "</span>\n<br></div><br>"
			// We set the prompt with the appropriate values.
			document.getElementById("bulkPurgeRequirementsPrompt").innerHTML = prompt;
			return(0);
		}

		if (setObject == 'purgeRequirements'){
			// lets empty the alert prompt.
			document.getElementById("bulkPurgeRequirementsPrompt").innerHTML = "";
			
			targetValue = "purge";
			targetArtifact = 'purgeRequirements';
		}
				
		if (setObject == 'copyRequirements'){
			
			var targetProjectObject = document.getElementById("targetProject");
			var targetProjectId = targetProjectObject[targetProjectObject.selectedIndex].value;

			
			var copyFolderObject = document.getElementById("copyFolder");
			var copyFolderId= copyFolderObject[copyFolderObject.selectedIndex].value;
			if (copyFolderObject.selectedIndex == 0) {
				alert ("Please select a valid Target Folder");
				copyFolderObject.focus();
				copyFolderObject.style.backgroundColor="#FFCC99";
				return;
			}
			
			document.getElementById("copyRequirementInBulkEditPrompt").style.display="none";
			
			var createTraceToSource= "no";
			var createTraceToSourceObject = document.getElementById("createTraceToSource");
			if (createTraceToSourceObject != null ){
				createTraceToSource= createTraceToSourceObject[createTraceToSourceObject.selectedIndex].value;
			}
			
			
			var copyCommonAttributesObject = document.getElementById("copyCommonAttributes");
			var copyCommonAttributes= copyCommonAttributesObject[copyCommonAttributesObject.selectedIndex].value;

			var copyTraceabilityObject = document.getElementById("copyTraceability");
			var copyTraceability= copyTraceabilityObject[copyTraceabilityObject.selectedIndex].value;

			var numOfCopies= 1;
			var numOfCopiesObject = document.getElementById("numOfCopies");
			if (numOfCopiesObject != null ){
				numOfCopies= numOfCopiesObject.value;
			}
			
			// we will split targetValue in the action servlet to get targetproject, copy folder and copy attribs and copy traceability options.
			targetValue = targetProjectId + "#" + copyFolderId + "#" + createTraceToSource + "#"
			+  copyCommonAttributes + "#" + copyTraceability + "#" + numOfCopies ;
			targetArtifact = 'copyRequirements';
		}
		
		if (setObject == 'lockRequirements'){
			targetValue = "lockRequirements";
			targetArtifact = 'lockRequirements';
		}
		
		if (setObject == 'unlockRequirements'){
			targetValue = "unlockRequirements";
			targetArtifact = 'unlockRequirements';
		}
		
		if (setObject == 'reGlossarize'){
			targetValue = "reGlossarize";
			targetArtifact = 'reGlossarize';
		}
		
		// lets get the custom attributes.

		var pos= setObject.indexOf(":##:")
		if (pos>=0){
			// means this is a custom attrib, and we need to handle it.
			custom = setObject.split(':##:');
			var customType = custom[0];
			var attributeId = custom[1];
			var customObject = document.getElementById('set' + attributeId);
			
			//if the object is of type drop down, we have to do some special work
			var type = customType.indexOf("DropDownSingle");
			if (type >=0){
				// is a Drop Down type.
				// if its a  drop down field and required, lets make sure it's not empty.
				if (customType.indexOf("Required") >= 0) {
					// means its a required field.
					if (customObject[customObject.selectedIndex].value == "") {
						alert ( " This is a Required attribute. Please enter a value");
						customObject.focus();
						customObject.style.backgroundColor="#FFCC99";
						return(0);
					}
				}
				targetValue = customObject[customObject.selectedIndex].value;
				targetArtifact = 'customAttribute:##:' + attributeId;  

			}
			//if the object is of type drop down Multiple, we have to do some special work
			var type = customType.indexOf("DropDownMultiple");
			if (type >=0){
				// is a Drop Down Multiple type.
				// if its a  drop down field and required, lets make sure it's not empty.
				
				// first lets calculate the targetValue
				var targetValue = "";
				for (var i = 0; i < customObject.options.length; i++) {
					if (customObject.options[i].selected) {
						targetValue += customObject.options[i].value + ','; 
						
					}
				}
				
				
				if (customType.indexOf("Required") >= 0) {
					// means its a required field.
					if (targetValue == "") {
						alert ( " This is a Required attribute. Please enter a value");
						customObject.focus();
						customObject.style.backgroundColor="#FFCC99";
						return(0);
					}
				}
				targetArtifact = 'customAttribute:##:' + attributeId;  

			}
			
			// if the object is addValueToCustomDropDownMultiple
			var type = customType.indexOf("addValueToCustomDDM");
			if (type >=0){
				// is a Drop Down Multiple type, where we are trying add a value to DDM attribute 
				
				// first lets calculate the targetValue
				customObject = document.getElementById('addValue' + attributeId);
				var  targetValue = customObject[customObject.selectedIndex].value;
				targetArtifact = 'addValuetoCustomDDM:##:' + attributeId;  
			}

			// if the object is removeValueFromCustomDropDownMultiple
			var type = customType.indexOf("removeValueFromCustomDDM");
			if (type >=0){
				// is a Drop Down Multiple type, where we are trying add a value to DDM attribute 
				
				// first lets calculate the targetValue
				customObject = document.getElementById('removeValue' + attributeId);
				var  targetValue = customObject[customObject.selectedIndex].value;
				targetArtifact = 'removeValueFromCustomDDM:##:' + attributeId;  
			}


			
			type = customType.indexOf("Date");
			if (type >= 0 ){
				// is a Date type.
				targetValue = document.getElementById('set' + attributeId).value;
				// lets see if the targetValue has the correct date format.
				if (isValidDate(targetValue)==false){
					customObject.focus();
					customObject.style.backgroundColor="#FFCC99";
					return (0);
				}

				targetArtifact = 'customAttribute:##:' + attributeId;  
			}
			
			
			type = customType.indexOf("Number");
			if (type >= 0 ){
				// is a Date type.
				targetValue = document.getElementById('set' + attributeId).value;
				// lets see if the targetValue has the correct date format.
				if (isNaN(targetValue)){
					alert("Please enter a valid number");
					customObject.focus();
					customObject.style.backgroundColor="#FFCC99";
					return (0);
				}

				targetArtifact = 'customAttribute:##:' + attributeId;  
			}
			
			
			type = customType.indexOf("setCustomText");
			if (type >= 0 ){
				// is a text type.
				// if its a  text field and required, lets make sure it's not empty.
				if (customType.indexOf("Required") >= 0) {
					// means its a required field.
					if ((customObject.value == null) || (customObject.value.length == 0)) {
						alert ( " This is a Required attribute. Please enter a value");
						customObject.focus();
						customObject.style.backgroundColor="#FFCC99";
						return(0);
					}
				}
				targetValue = document.getElementById('set' + attributeId).value;
				targetArtifact = 'customAttributeSetText:##:' + attributeId;  
			}
			
			type = customType.indexOf("appendCustomText");
			if (type >= 0 ){
				// is a text type.
				// first lets calculate the targetValue
				customObject = document.getElementById('append' + attributeId);
				var  targetValue = customObject.value;
				targetArtifact = 'customAttributeAppendText:##:' + attributeId; 
				
			}

			
			type = customType.indexOf("replaceCustomText");
			if (type >= 0 ){
				// is a text type.
				var replaceString = document.getElementById('replace' + attributeId).value;
				var replaceWithString = document.getElementById('replaceWith' + attributeId).value;
				
				var  targetValue = replaceString + ":##:" + replaceWithString;
				targetArtifact = 'customAttributeReplaceText:##:' + attributeId; 
				 
			}
		}
		
		// lets handle the Traceability section.
		if (setObject == 'setTraceTo'){
			targetValue = document.getElementById("setTraceTo").value;
			targetArtifact = 'traceTo';
		}
		
		if (setObject == 'setTraceFrom'){
			targetValue = document.getElementById("setTraceFrom").value;
			targetArtifact = 'traceFrom';
		}
		if (setObject == 'clearSuspectTraceTo'){
			targetValue = '';
			targetArtifact = 'clearSuspectTraceTo';
		}
		
		if (setObject == 'clearSuspectTraceFrom'){
			targetValue = '';
			targetArtifact = 'clearSuspectTraceFrom';
		}
		
		if (setObject == 'deleteAllTraceTo'){
			targetValue = '';
			targetArtifact = 'deleteAllTraceTo';
		}

		if (setObject == 'deleteAllTraceFrom'){
			targetValue = '';
			targetArtifact = 'deleteAllTraceFrom';
		}
		
		if (setObject == 'refreshApproverList'){
			targetValue = '';
			targetArtifact = 'refreshApproverList';
		}

		if (setObject == 'remindApprovers'){
			targetValue = '';
			targetArtifact = 'remindApprovers';
		}

		
		if (setObject == 'submitRequirementForApproval'){
			targetValue = '';
			targetArtifact = 'submitRequirementForApproval';
		}

		if (setObject == 'approveRequirement'){
			targetValue = '';
			targetArtifact = 'approveRequirement';
		}


		
		
		if (setObject.indexOf("rejectRequirement") >= 0 ){
			// setObject has rejectRequirement:##:approvalNote
			// lets get the approvalNote and store it in the targetValue.
			var rejection = setObject.split(":##:");
			targetValue = rejection[1]; 
			targetArtifact = "rejectRequirement";
		}
	
		if (setObject.indexOf("setDynamicApprover") >= 0 ){
			var dynamicApprovalRankObject = document.getElementById("dynamicApprovalRank");
			if  (
					(isNaN(dynamicApprovalRankObject.value)) ||
					(dynamicApprovalRankObject.value == 0)		
			
			){
				alert ("Please enter a valid number (>0) for Dynamic Approval Rank");
				dynamicApprovalRankObject.style.backgroundColor="#FFCC99";
				dynamicApprovalRankObject.focus();
				return(0);
			}
			
			var dynamicApprovalRoleObject = document.getElementById("setDynamicApprovalRole");
			
			var dynamicApprovalRole = dynamicApprovalRoleObject[dynamicApprovalRoleObject.selectedIndex].value;
			
			targetValue = dynamicApprovalRole + ":#:" + dynamicApprovalRankObject.value;
			targetArtifact = "setDynamicApprover";
		}
		if (setObject.indexOf("removeDynamicApprover") >= 0 ){
			var dynamicApprovalRoleObject = document.getElementById("removeDynamicApprovalRole");
			
			var dynamicApprovalRole = dynamicApprovalRoleObject[dynamicApprovalRoleObject.selectedIndex].value;
			
			targetValue = dynamicApprovalRole;
			targetArtifact = "removeDynamicApprover";
		}
		
		// lets empty the prompt div box
		document.getElementById("bulkEditActionResponse").innerHTML = "";
		
		

		
		
		
		
		// lets build the url here.
		var url = "/GloreeJava2/servlet/ReportAction";
		
		var params = "action=bulkEdit&folderId=" +folderId;
		params += "&targetArtifact=" + encodeURIComponent(targetArtifact);
		params += "&targetValue=" + encodeURIComponent(targetValue);
		params += "&targetRequirements=" + encodeURIComponent(targetRequirements);

		// since the number of selected reqs can be large and get has a limit
		// we will use post.
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.open("POST", url, true);

		// Send the proper header information along with the request
		xmlHttpOPCenterB.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xmlHttpOPCenterB.setRequestHeader("Content-length", params.length);
		xmlHttpOPCenterB.setRequestHeader("Connection", "close");

		// ContentCenter F is the target.
		// F will hold the actual data. So lets put the working gif in it
		document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("bulkEditActionResponse").style.display = 'block';
				document.getElementById("bulkEditActionResponse").innerHTML=xmlHttpOPCenterB.responseText;
				// once the bulk updates are done, we can simulate
				// the runReport method call", so that the users see the
				// refreshed data.
				
				if (traceTree == null ){
					reportAction(folderId,'runReport','list',targetRequirements);
				}
				if (traceTree == "traceTree"){
					reportAction(traceTreeRootFolderId,'runReport','traceTree', targetRequirements);
				}
				
			}
		}
		xmlHttpOPCenterB.send(params);
		return(1);
	}

	// displays the box that captures the email attachment info
	function displayEmailAttachmentDiv(folderId, reportType){
		var url="/GloreeJava2/jsp/Report/displayEmailAttachmentDiv.jsp?folderId=" + folderId;
		url += "&reportType=" + reportType ;
		url += "&bustcache=" + new Date().getTime() ;
		fillOPCenterGeneric(url, "emailAttachmentDiv");	
	}

	// displays the box that captures the email attachment info
	// this is a variation of displayEmailAttachmentDiv, but used when only with excel files as attachments.
	function displayEmailExcelDiv(dataType){
		var url="/GloreeJava2/jsp/Report/displayEmailExcelDiv.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&dataType=" + encodeURIComponent(dataType);
		fillOPCenterGeneric(url, "emailExcelDiv");	
	}

	
	// displays the box that captures the email URL info
	// this is a variation of displayEmailAttachmentDiv, but used when only with excel files as attachments.
	function displayEmailRequirementDiv(requirementId){
		var url="/GloreeJava2/jsp/Requirement/displayEmailRequirementDiv.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&requirementId=" + requirementId ;
		fillOPCenterGeneric(url, "requirementPromptDiv");	
	}
	
	
	
	
	
	
	// displays the box that captures the email attachment info
	function displayTDCSDiv(folderId, reportType){
		var url="/GloreeJava2/jsp/Report/displayTDCSDiv.jsp?folderId=" + folderId;
		url += "&reportType=" + reportType ;
		url += "&bustcache=" + new Date().getTime() ;
		fillOPCenterGeneric(url, "emailAttachmentDiv");	
	}
	

	function displayEasyAddEmailIds(){

		var roleIdObject = document.getElementById("roleId");
		var roleId = roleIdObject.options[roleIdObject.selectedIndex].value;
		
		if (roleId == -1){
			document.getElementById("displayEasyAddEmailIdsDiv").style.display= "none";
		}
		else{
			var url="/GloreeJava2/jsp/Report/displayEasyAddEmailIds.jsp?bustcache=" + new Date().getTime() ;
			url += "&roleId=" + roleId;
			document.getElementById("displayEasyAddEmailIdsDiv").style.display= "block";
			document.getElementById("displayEasyAddEmailIdsDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";		
			fillOPCenterGeneric(url, "displayEasyAddEmailIdsDiv");		
		}
		
	}

	
	// this function is called when a user clicks on the email link after running a report.
	function emailReportAsAttachment(folderId, reportType){
		var to = document.getElementById("to").value ;
		// lets ensure that to has only valid email ids.
		// first replace any occurence of space or ; with , 
		if (to != null){ 
			to = to.replace(" ", ",");
			
			to = to.replace(";", ",");
			
			// some of the logic may create one or more of ,, . so we need to replace them with , . 
			
			to = to.replace("/,+/", ",");
			
			// now lets split each one of these email id and see if they are valid.
			toArray = to.split(',');
			
			for (t in toArray){
				var currentTo = toArray[t];
				currentTo = currentTo.replace(" ", "");
				if ((currentTo != null) && (currentTo.length > 0)){
					if (echeck(currentTo)==false){
						alert ("Your email id " + currentTo + " is not formatted correctly. Please fix it.");
						document.getElementById("to").focus();
						document.getElementById("to").style.backgroundColor="#FFCC99";
						return(0);
					}
				}
			}
		}
		
		var cc = document.getElementById("cc").value ;
		// lets ensure that cc has only valid email ids.
		// first replace any occurence of space or ; with , 
		if (cc != null){ 
			cc = cc.replace(" ", ",");
			cc = cc.replace(";", ",");
			
			// some of the logic may create one or more of ,, . so we need to replace them with , . 
			cc = cc.replace("/,+/", ",");
			
			// now lets split each one of these email id and see if they are valid.
			ccArray = cc.split(',');
			for (t in ccArray){
				var currentcc = ccArray[t];
				currentcc = currentcc.replace(" ", "");
				if ((currentcc != null) && (currentcc.length > 0)){
					currentcc = currentcc.replace(" ", "");
					if (echeck(currentcc)==false){
						alert ("Your email id " + currentcc + " is not formatted correctly. Please fix it.");
						document.getElementById("cc").focus();
						document.getElementById("cc").style.backgroundColor="#FFCC99";
						return(0);
					}
				}
			}
		}
		
		var attachmentObject = document.getElementById("attachmentType");
		var attachmentType = attachmentObject.options[attachmentObject.selectedIndex].value;
		subjectValue = document.getElementById("subject").value ;
		messageValue = document.getElementById("message").value;
		
		if ((to == "") && (cc == "")){
			alert ('Please provide at least one email address');
			document.getElementById("to").focus();
			document.getElementById("to").style.backgroundColor="#FFCC99"; 
			return(0);
		}
		
		if (subjectValue == ""){
			alert ('Email Subject can not be empty');
			document.getElementById("subject").focus();
			document.getElementById("subject").style.backgroundColor="#FFCC99"; 
			return(0);
		}
		if (messageValue == ""){
			alert ('Email Message can not be empty');
			document.getElementById("message").focus();
			document.getElementById("message").style.backgroundColor="#FFCC99";
			return(0);
		}
		
		// if every thing is good, lets make a URL and call it.
		var url="/GloreeJava2/servlet/ReportAction?action=emailReportAsAttachment&folderId=" + folderId;
		url += "&reportType=" + encodeURIComponent(reportType);
		url += "&to=" + encodeURIComponent(to);
		url += "&cc=" + encodeURIComponent(cc);
		url += "&attachmentType=" + encodeURIComponent(attachmentType);
		url += "&subject=" + encodeURIComponent(subjectValue);
		url += "&message=" + encodeURIComponent(messageValue);
		url += "&bustcache=" + new Date().getTime() ;
	
		// if attachmentType is equal to ExcelVersionComments, lets add an extra parameter 
		// to signal that the user wants the excel version with comments and versions. etc..
		if (attachmentType == "excelVersionComments"){
			url += "&includeRevisionHistory=" + encodeURIComponent("yes");
		}
		
		document.getElementById("emailAttachmentDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "emailAttachmentDiv");
	}

	// this function is called when a user clicks on the email link after running a report.
	// this is similar to emailReportAsAttachment, but works only for excel file.
	function emailExcelAsAttachment(dataType){
		var to = document.getElementById("to").value ;
		// lets ensure that to has only valid email ids.
		// first replace any occurence of space or ; with , 
		if (to != null){ 
			to = to.replace(" ", ",");
			to = to.replace(";", ",");
			to = to.replace(",,", ",");
			// now lets split each one of these email id and see if they are valid.
			toArray = to.split(',');
			for (t in toArray){
				var currentTo = toArray[t];
				if ((currentTo != null) && (currentTo.length > 0)){
					if (echeck(currentTo)==false){
						alert ("Your email id " + currentTo + " is not formatted correctly. Please fix it.");
						document.getElementById("to").focus();
						document.getElementById("to").style.backgroundColor="#FFCC99";
						return(0);
					}
				}
			}
		}
		
		var cc = document.getElementById("cc").value ;
		// lets ensure that cc has only valid email ids.
		// first replace any occurence of space or ; with , 
		if (cc != null){ 
			cc = cc.replace(" ", ",");
			cc = cc.replace(";", ",");
			cc = cc.replace(",,", ",");
			// now lets split each one of these email id and see if they are valid.
			ccArray = cc.split(',');
			for (t in ccArray){
				var currentcc = ccArray[t];
				if ((currentcc != null) && (currentcc.length > 0)){
					if (echeck(currentcc)==false){
						alert ("Your email id " + currentcc + " is not formatted correctly. Please fix it.");
						document.getElementById("cc").focus();
						document.getElementById("cc").style.backgroundColor="#FFCC99";
						return(0);
					}
				}
			}
		}
				
		
		
		
		
		subjectValue = document.getElementById("subject").value ;
		messageValue = document.getElementById("message").value;
		
		if ((to == "") && (cc == "")){
			alert ('Please provide at least one email address');
			document.getElementById("to").focus();
			document.getElementById("to").style.backgroundColor="#FFCC99"; 
			return(0);
		}
		
		if (subjectValue == ""){
			alert ('Email Subject can not be empty');
			document.getElementById("subject").focus();
			document.getElementById("subject").style.backgroundColor="#FFCC99"; 
			return(0);
		}
		if (messageValue == ""){
			alert ('Email Message can not be empty');
			document.getElementById("message").focus();
			document.getElementById("message").style.backgroundColor="#FFCC99";
			return(0);
		}
		
		// if every thing is good, lets make a URL and call it.
		var url="/GloreeJava2/servlet/ReportAction?action=emailMetricsExcelAsAttachment";
		url += "&to=" + encodeURIComponent(to);
		url += "&cc=" + encodeURIComponent(cc);
		url += "&subject=" + encodeURIComponent(subjectValue);
		url += "&message=" + encodeURIComponent(messageValue);
		url += "&dataType=" + encodeURIComponent(dataType);
		url += "&bustcache=" + new Date().getTime() ;
	
		
		document.getElementById("emailExcelDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "emailExcelDiv");
	}


	// this function is called when a user clicks on the email link after running a report.
	// this is similar to emailReportAsAttachment, but works only for excel file.
	function emailRequirement(requirementId){
		var to = document.getElementById("to").value ;
		// lets ensure that to has only valid email ids.
		// first replace any occurence of space or ; with , 
		if (to != null){ 
			to = to.replace(" ", ",");
			to = to.replace(";", ",");
			to = to.replace(",,", ",");
			// now lets split each one of these email id and see if they are valid.
			toArray = to.split(',');
			for (t in toArray){
				var currentTo = toArray[t];
				if ((currentTo != null) && (currentTo.length > 0)){
					if (echeck(currentTo)==false){
						alert ("Your email id " + currentTo + " is not formatted correctly. Please fix it.");
						document.getElementById("to").focus();
						document.getElementById("to").style.backgroundColor="#FFCC99";
						return(0);
					}
				}
			}
		}
		
		var cc = document.getElementById("cc").value ;
		// lets ensure that cc has only valid email ids.
		// first replace any occurence of space or ; with , 
		if (cc != null){ 
			cc = cc.replace(" ", ",");
			cc = cc.replace(";", ",");
			cc = cc.replace(",,", ",");
			// now lets split each one of these email id and see if they are valid.
			ccArray = cc.split(',');
			for (t in ccArray){
				var currentcc = ccArray[t];
				if ((currentcc != null) && (currentcc.length > 0)){
					if (echeck(currentcc)==false){
						alert ("Your email id " + currentcc + " is not formatted correctly. Please fix it.");
						document.getElementById("cc").focus();
						document.getElementById("cc").style.backgroundColor="#FFCC99";
						return(0);
					}
				}
			}
		}
				
		
		
		
		
		subjectValue = document.getElementById("subject").value ;
		messageValue = document.getElementById("message").value;
		
		if ((to == "") && (cc == "")){
			alert ('Please provide at least one email address');
			document.getElementById("to").focus();
			document.getElementById("to").style.backgroundColor="#FFCC99"; 
			return(0);
		}
		
		if (subjectValue == ""){
			alert ('Email Subject can not be empty');
			document.getElementById("subject").focus();
			document.getElementById("subject").style.backgroundColor="#FFCC99"; 
			return(0);
		}
		if (messageValue == ""){
			alert ('Email Message can not be empty');
			document.getElementById("message").focus();
			document.getElementById("message").style.backgroundColor="#FFCC99";
			return(0);
		}
		
		// if every thing is good, lets make a URL and call it.
		var url="/GloreeJava2/servlet/RequirementAction?action=emailRequirement";
		url += "&requirementId=" + encodeURIComponent(requirementId);
		url += "&to=" + encodeURIComponent(to);
		url += "&cc=" + encodeURIComponent(cc);
		url += "&subject=" + encodeURIComponent(subjectValue);
		url += "&message=" + encodeURIComponent(messageValue);
		url += "&bustcache=" + new Date().getTime() ;
	
		
		document.getElementById("requirementPromptDiv").innerHTML= "<div class='alert alert-success'>&nbsp;&nbsp;&nbsp;Working...</div>";
		fillOPCenterGeneric(url, "requirementPromptDiv");
	}

	
	// this function is called when a user clicks on the save TDCS document link.
	function saveTDCSDocument(folderId, reportType){
		var url="/GloreeJava2/servlet/ReportAction?action=saveTDCSDocument&folderId=" + folderId;
		url += "&reportType=" + encodeURIComponent(reportType);
		url += "&bustcache=" + new Date().getTime() ;

		var tDCSActionObject = document.getElementById("tDCSAction");
		var tDCSAction = tDCSActionObject.options[tDCSActionObject.selectedIndex].value;
		url += "&tDCSAction=" + tDCSAction ;

		
		if (tDCSActionObject.selectedIndex == 0){
			tDCSActionObject.focus();
			tDCSActionObject.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (tDCSActionObject.selectedIndex == 1){
			// means that the user has chosen to 'add a new document'.
			// then title has to have a value
			var title = document.getElementById("title").value ;
			if (title == "") {
				alert ('Please enter the Title for this document');
				document.getElementById("title").focus();
				document.getElementById("title").style.backgroundColor="#FFCC99"; 
				return(0);
			}
			else {
				// doc id is valid. lets send as param.
				url += "&title=" + title;
			}
		}

		
		if (tDCSActionObject.selectedIndex == 2){
			// means that the user has chosen to 'add a new version to an existing document'.
			// then the document id has to be valid
			var tDCSDocumentFullTag = document.getElementById("tDCSDocumentFullTag").value ;
			if ((tDCSDocumentFullTag == "") || (tDCSDocumentFullTag == "TDCS-XXX")) {
				alert ('Please enter a valid TDCS Document Id');
				document.getElementById("tDCSDocumentFullTag").focus();
				document.getElementById("tDCSDocumentFullTag").style.backgroundColor="#FFCC99"; 
				return(0);
			}
			else {
				// doc id is valid. lets send as param.
				url += "&tDCSDocumentFullTag=" + tDCSDocumentFullTag;
			}
		}
		
		descriptionValue = document.getElementById("description").value;
		if (descriptionValue == ""){
			alert ('Please enter a Description for this document');
			document.getElementById("description").focus();
			document.getElementById("description").style.backgroundColor="#FFCC99"; 
			return(0);
		}
		
		var attachmentObject = document.getElementById("attachmentType");
		var attachmentType = attachmentObject.options[attachmentObject.selectedIndex].value;
		// if every thing is good, lets make a URL and call it.
		url += "&attachmentType=" + encodeURIComponent(attachmentType);
		url += "&description=" + encodeURIComponent(descriptionValue);
	
		// if attachmentType is equal to ExcelVersionComments, lets add an extra parameter 
		// to signal that the user wants the excel version with comments and versions. etc..
		if (attachmentType == "excelVersionComments"){
			url += "&includeRevisionHistory=" + encodeURIComponent("yes");
		}
		
		document.getElementById("emailAttachmentDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "emailAttachmentDiv");
	}
	
		
	// called when someone clicks on 'Run Report' or Save Report button in the display Report Report.
	// reportType is either 'list' or 'traceTree'
	function reportAction(folderId,action,reportType, targetRequirements){
			// collapse the right and left boxes, so that we have more real estate.
			// layout.getUnitByPosition('left').collapse();
			// layout.getUnitByPosition('right').collapse();
			
			
			var thisForm = document.getElementById("reportFilterForm");
			
			var inRelease = "";
			try {
				var inReleaseObject = document.getElementById("inRelease");
				inRelease = inReleaseObject.options[inReleaseObject.selectedIndex].value;
			}
			catch (e){
				
			}
			// if the action is save report and report name and desc are empty,
			// throw an alert.
			var reportName = '';
			var reportDescription = '';
			var reportVisibility = '';
			if (action == 'saveReport'){
				
				var reportVisibilityObject = document.getElementById("reportVisibility");
				reportVisibility = reportVisibilityObject.options[reportVisibilityObject.selectedIndex].value;
				reportName = document.getElementById("reportName").value ;
				reportDescription = document.getElementById("reportDesciption").value ;
				
				
				if (reportName == ""){
					alert ('Report Name can not be empty');
					document.getElementById("reportName").focus();
					document.getElementById("reportName").style.backgroundColor="#FFCC99"; 
					return(0);
				}
				if (reportDescription == ""){
					alert ('Report Description can not be empty');
					document.getElementById("reportDescription").focus();
					document.getElementById("reportDescription").style.backgroundColor="#FFCC99";
					return(0);
				}
				
			}
			
			var danglingSearch = "all";
			if (thisForm.danglingSearch.checked == true) {
				danglingSearch = "danglingOnly";
			}
			
			var orphanSearch = "all";
			if (thisForm.orphanSearch.checked == true) {
				orphanSearch = "orphanOnly";
			}
			
			var completedSearch = "all";
			if (thisForm.completedSearch.checked == true) {
				completedSearch = "completedOnly";
			}
			
			var incompleteSearch = "all";
			if (thisForm.incompleteSearch.checked == true) {
				incompleteSearch = "incompleteOnly";
			}
			
			var suspectUpStreamSearch = "all";
			if (thisForm.suspectUpStreamSearch.checked == true) {
				suspectUpStreamSearch = "suspectUpStreamOnly";
			}
			
			var suspectDownStreamSearch = "all";
			if (thisForm.suspectDownStreamSearch.checked == true) {
				suspectDownStreamSearch = "suspectDownStreamOnly";
			}
			
			var lockedSearch = "all";
			if (thisForm.lockedSearch != null){
				// this function may be called from Tracetree report and we haven't yet put in lockedSearch filter there. 
				// once that is put, we can remove this if check. 
				if (thisForm.lockedSearch.checked == true) {
					lockedSearch = "lockedOnly";
				}
			}
			var includeSubFoldersSearch = "no";
			if (thisForm.includeSubFoldersSearch.checked == true) {
				includeSubFoldersSearch = "includeSubFoldersOnly";
			}
			
			var inRTBaselineSearchObject = thisForm.inRTBaselineSearch;
			var inRTBaselineSearch;
			if (inRTBaselineSearchObject.selectedIndex > -1) {
				inRTBaselineSearch = inRTBaselineSearchObject.options[inRTBaselineSearchObject.selectedIndex].value;
			}
			var changedAfterRTBaselineSearchObject = thisForm.changedAfterRTBaselineSearch; 
			var changedRTBaselineSearch;
			if (changedAfterRTBaselineSearchObject.selectedIndex > -1) {
				changedAfterRTBaselineSearch = changedAfterRTBaselineSearchObject.options[changedAfterRTBaselineSearchObject.selectedIndex].value;
			}
			
			//since testingStatus is a multiple select, extra work to convert to string.
			var testingStatusSearchObject = thisForm.testingStatusSearch;
			var testingStatusSearch = "";
			for (var i = 0; i < testingStatusSearchObject.options.length; i++) {
				if (testingStatusSearchObject.options[i].selected) {
					testingStatusSearch += testingStatusSearchObject.options[i].value + ','; 
				}
			}
			
			var nameSearch = thisForm.nameSearch.value;
			var descriptionSearch = thisForm.descriptionSearch.value;
			var ownerSearch = thisForm.ownerSearch.value;
			var externalURLSearch = thisForm.externalURLSearch.value;
			var approvedBySearch = thisForm.approvedBySearch.value;
			var rejectedBySearch = thisForm.rejectedBySearch.value;
			var pendingBySearch = thisForm.pendingBySearch.value;
			var traceToSearch = thisForm.traceToSearch.value;
			var traceFromSearch = thisForm.traceFromSearch.value;
			
			// since status is a multiple select, extra work to convert to
			// string.
			var statusSearchObject = thisForm.statusSearch;;
			var statusSearch = "";
			for (var i = 0; i < statusSearchObject.options.length; i++) {
				if (statusSearchObject.options[i].selected) {
					statusSearch+= statusSearchObject.options[i].value + ','; 
				}
			}
			
		
			//since priority is a multiple select, extra work to convert to string.
			var prioritySearchObject = thisForm.prioritySearch;
			var prioritySearch = "";
			for (var i = 0; i < prioritySearchObject.options.length; i++) {
				if (prioritySearchObject.options[i].selected) {
					prioritySearch += prioritySearchObject.options[i].value + ','; 
				}
			}
			
			//since pctComplete is a multiple select, extra work to convert to string.
			var pctCompleteSearch = thisForm.pctCompleteSearch;
			if (isNaN(pctCompleteSearch.value)){
				alert ("Please enter a valid number for percent complete");
				pctCompleteSearch.style.backgroundColor="#FFCC99";
				pctCompleteSearch.focus();
				return(0);
			}
			
			if ((pctCompleteSearch.value < 0) || (pctCompleteSearch.value > 100) ) {
				alert ("Please enter a valid number between 0 and 100 for percent complete");
				pctCompleteSearch.style.backgroundColor="#FFCC99";
				pctCompleteSearch.focus();
				return(0);
			}

			// listReports don't need traceTreeDepth. we will still default it to 1
			// tracetreeReports need depth. The default for them is 3.
			var traceTreeDepth = 1;
			if (reportType == 'traceTree'){
				var traceTreeDepthObject = thisForm.traceTreeDepth;
				traceTreeDepth = traceTreeDepthObject.options[traceTreeDepthObject.selectedIndex].value; 
			}
			
			// lets get all the display check box values.
			
			// only TraceTree's have displayRequirementType object. 
			var displayRequirementType  = "";
			if (reportType == 'traceTree'){
				var displayRequirementTypeObject = thisForm.displayRequirementType;
				//displayRequirementType = displayRequirementTypeObject.options[displayRequirementTypeObject.selectedIndex].value; 
				
				if (displayRequirementTypeObject != null) {
					for (var i = 0; i < displayRequirementTypeObject.options.length; i++) {
						if (displayRequirementTypeObject.options[i].selected) {
							displayRequirementType += displayRequirementTypeObject.options[i].value + ','; 
						}
					}
				}
			}
			if (displayRequirementType==""){
				displayRequirementType = "all";
			}
			
			var standardDisplayObject = thisForm.standardDisplay;
			var standardDisplay= "";
			for (var i = 0; i < standardDisplayObject.options.length; i++) {
				if (standardDisplayObject.options[i].selected) {
					standardDisplay += standardDisplayObject.options[i].value + ','; 
				}
			}
			var customAttributesDisplayObject = thisForm.customAttributesDisplay;
			var customAttributesDisplay= "";
			if (customAttributesDisplayObject != null) {
				for (var i = 0; i < customAttributesDisplayObject.options.length; i++) {
					if (customAttributesDisplayObject.options[i].selected) {
						customAttributesDisplay += customAttributesDisplayObject.options[i].value + ','; 
					}
				}
			}

			// lets get the sortBy and sortByType
			var sortByObject = thisForm.sortBy;
			var sortBy;
			if (sortByObject != null){
				// sortBy applicable only for list report.
				if (sortByObject.selectedIndex > -1) {
					sortBy = sortByObject.options[sortByObject.selectedIndex].value;
				}
			}
			else {
				// for trace tree, lets dummy it up
				sortBy = "";
			}

			var sortByTypeObject = thisForm.sortByType;
			var sortByType;
			if (sortByTypeObject != null){
				// sortByTypeObject is applicable only for list report
				if (sortByTypeObject.selectedIndex > -1) {
					sortByType = sortByTypeObject.options[sortByTypeObject.selectedIndex].value;
				}
			}
			else {
				// for trace tree lets dummy it up
				sortByType = "";
			}

			var rowsPerPageObject = thisForm.rowsPerPage;
			var rowsPerPage = rowsPerPageObject.value;
			
			// Now display the Report.
			url = "/GloreeJava2/servlet/ReportAction?folderId=" + encodeURIComponent(folderId);
			url += "&inRelease=" + encodeURIComponent(inRelease);
			url += "&action=" + encodeURIComponent(action);
			url += "&inRelease=" + encodeURIComponent(inRelease);
			
			url += "&reportType=" + encodeURIComponent(reportType);
			url += "&danglingSearch=" + encodeURIComponent(danglingSearch);
			url += "&orphanSearch=" + encodeURIComponent(orphanSearch);
			url += "&completedSearch=" + encodeURIComponent(completedSearch);
			url += "&incompleteSearch=" + encodeURIComponent(incompleteSearch);
			url += "&suspectUpStreamSearch=" + encodeURIComponent(suspectUpStreamSearch);
			url += "&suspectDownStreamSearch=" + encodeURIComponent(suspectDownStreamSearch);
			url += "&lockedSearch=" + encodeURIComponent(lockedSearch);
			url += "&includeSubFoldersSearch=" + encodeURIComponent(includeSubFoldersSearch);
			
			url += "&inRTBaselineSearch=" + encodeURIComponent(inRTBaselineSearch);
			url += "&changedAfterRTBaselineSearch=" + encodeURIComponent(changedAfterRTBaselineSearch);
			url += "&testingStatusSearch=" + encodeURIComponent(testingStatusSearch);
			
			url += "&nameSearch=" + encodeURIComponent(nameSearch);
			url += "&descriptionSearch=" + encodeURIComponent(descriptionSearch);
			url += "&ownerSearch=" + encodeURIComponent(ownerSearch);
			url += "&externalURLSearch=" + encodeURIComponent(externalURLSearch);
			url += "&approvedBySearch=" + encodeURIComponent(approvedBySearch);
			url += "&rejectedBySearch=" + encodeURIComponent(rejectedBySearch);
			url += "&pendingBySearch=" + encodeURIComponent(pendingBySearch);
			url += "&traceToSearch=" + encodeURIComponent(traceToSearch);
			url += "&traceFromSearch=" + encodeURIComponent(traceFromSearch);
			
			url += "&statusSearch=" + encodeURIComponent(statusSearch);
			url += "&prioritySearch=" + encodeURIComponent(prioritySearch);
			url += "&pctCompleteSearch=" + encodeURIComponent(pctCompleteSearch.value);
			url += "&traceTreeDepth=" + encodeURIComponent(traceTreeDepth);
			url += "&displayRequirementType=" + encodeURIComponent(displayRequirementType);

			

			url += "&standardDisplay=" + encodeURIComponent(standardDisplay);
			url += "&customAttributesDisplay=" + encodeURIComponent(customAttributesDisplay);

			url += "&sortBy=" + encodeURIComponent(sortBy);
			url += "&sortByType=" + encodeURIComponent(sortByType);

			url += "&rowsPerPage=" + encodeURIComponent(rowsPerPage);
			
			// Now, lets get the custom attributes.
			// we use the attributeIdString to split and figure out which
			// attributes
			// to look for.
			// for these attributes , we get the value, build the URL string and
			// .
			// we will use the same logic to get the attribute values and search
			// 
			var attributeIdString = thisForm.attributeIdString;
			

			attributeIds = attributeIdString.value.split('##');
			for (a in attributeIds){
				splitA = attributeIds[a].split('#');
				var id = splitA[0];
				var type = splitA[1];
			
				if (id) {
					var attribute = document.getElementById(id);
					if (type == 'DropDown'){
						// since this is a multi select, iterate through this to get selected values.
			
						var dropDownSearch = "";
						for (var i = 0; i < attribute.options.length; i++) {
							if (attribute.options[i].selected) {
								dropDownSearch += attribute.options[i].value + ':##:';
			
							}
						}
						// add the attributeid / concatenated selected values.
						url = url + '&' +  id + '='+  encodeURIComponent(dropDownSearch) ;
			
					}
					else {
						url = url + '&' + id + '='+  encodeURIComponent(attribute.value) ;
			
					}
				}
			}	
			
			// now lets add the attributeIdString, so that the server can break up the custom
			// values based on this.
			url += '&attributeIdString=' + encodeURIComponent(attributeIdString.value);
			url += "&bustcache=" + new Date().getTime() ;
			
			// lets see if some resultset filter criteria was given. This works ONLY
			// for trace tree reports
			// hence the try catch
			try {
				var filterCondition = document.getElementById("filterCondition").value;
				url+= "&filterCondition=" + encodeURIComponent(filterCondition);
			}
			catch (e){}
			
			if (action == 'saveReport') {
				// for save report we send in the report name and description.
				url += "&reportVisibility=" + encodeURIComponent(reportVisibility);
				url += "&reportName=" + encodeURIComponent(reportName);
				url += "&reportDescription=" + encodeURIComponent(reportDescription);
				
				// we will show the results on the save Report in the
				// saveReportDiv box.
				document.getElementById("saveReportDiv").style.display = 'none';
				
				// after the report is saved , we want to refresh the folder
				// contents on right
				// to show the new report. Hence we have to do async.
				
				xmlHttpOPCenterE =GetXmlHttpObject();
				
				xmlHttpOPCenterE.onreadystatechange=function() {
					if(xmlHttpOPCenterE.readyState==4){
						document.getElementById("saveReportResultDiv").innerHTML=xmlHttpOPCenterE.responseText;
						document.getElementById("saveReportResultDiv").style.display = 'block';
						
					}
				}
				xmlHttpOPCenterE.open("GET",url,true);
				xmlHttpOPCenterE.send(null);
			}
			else{
				// for run reports, we don't need to send the report params.

				// any time run report happens, we need to show the bulk actions div box.
				// so that the users can take some actions on the data returned.
				// this is only for list reports.
				if (reportType == 'list'){
					var listReportBulkActionDiv = document.getElementById('listReportBulkActionDiv');
					if (listReportBulkActionDiv != null) {
						listReportBulkActionDiv.style.display = 'block';
					}
				}
				
				// ContentCenter F is the target.
				// F will hold the actual data. So lets put the working gif in
				// it
				document.getElementById("contentCenterF").style.display= "block";
				document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";

				// when you run the report, lets collapse the filter, so that more
				// real estate is visible to display data.
				// we will collapse the filter only if the 'keepFilterOpen' box is checked.
				if (thisForm.keepFilterOpen.checked == true) {
					// don't do any thing.
					document.getElementById('showFilterLinkDiv').style.display = 'none';
				}
				else {
					document.getElementById('showFilterLinkDiv').style.display = 'block';
					document.getElementById('filterDetailsDiv').style.display = 'none';
					document.getElementById('closeFilterDiv').style.display = 'none';
					document.getElementById('openFilterDiv').style.display = 'block';
				}

				xmlHttpOPCenterF =GetXmlHttpObject();				
				xmlHttpOPCenterF.onreadystatechange=function() {
					if(xmlHttpOPCenterF.readyState==4){
						document.getElementById("contentCenterF").innerHTML=xmlHttpOPCenterF.responseText;
						document.getElementById("contentCenterF").style.display = 'block';
						
						// If this was a 'Run Report' job and the targetRequirements (previously selected Requiremets) is not null
						// then lets pre select those requirements for the user after the report is run. 
						
						if (targetRequirements.length > 0 ){
						
							var displayListReportDataForm = document.getElementById("displayListReportDataForm"); 
						
							// if the testField is the first object in the testString , the pos is showing as 0. so lets add some crap to the front of
							// the targetRequiremetns
							targetRequirements = "someCrap:##:" + targetRequirements;
							
							if (displayListReportDataForm != null ){
								if (displayListReportDataForm.requirementId.length == null){
									var testField = displayListReportDataForm.requirementId.value + ":##:";
									var pos= targetRequirements.indexOf(testField)
									if (pos > 0){
										// this row was previously selected. so lets select it. 
										displayListReportDataForm.requirementId.checked = true;
									}
								}
								else {
									// multiple requirement rows in data grid.
									for (i=0; i<displayListReportDataForm.requirementId.length;i++){
										var testField = displayListReportDataForm.requirementId[i].value + ":##:";
										var pos= targetRequirements.indexOf(testField)
										if (pos > 0){
											// this row was previously selected. so lets select it. 
											displayListReportDataForm.requirementId[i].checked = true; 
										}
									}		
									
								}
								
							}
						
						}
					}
				}
				xmlHttpOPCenterF.open("GET",url,true);
				xmlHttpOPCenterF.send(null);				
			}
	}

	// Called when report owner or project admin click's on DeleteReport button.
	function deleteReport(folderId,reportId){
		document.getElementById("contentCenterB").innerHTML= '&nbsp;&nbsp;Working...';
		document.getElementById("contentCenterC").style.display = 'none';
		document.getElementById("contentCenterD").style.display = 'none';
		document.getElementById("contentCenterE").style.display = 'none';
		document.getElementById("contentCenterF").style.display = 'none';
		
		
		// Now delete the Report.
		url = "/GloreeJava2/servlet/ReportAction?folderId=" + encodeURIComponent(folderId);
		url += "&action=" + encodeURIComponent("deleteReport");
		url += "&reportId=" + encodeURIComponent(reportId);
		url += "&bustcache=" + new Date().getTime() ;

		
		// after the report is deleted, we want to refresh the folder contents
		// on right
		// to show the new report. Hence we have to do async.
		xmlHttpOPCenterE =GetXmlHttpObject();
			
		xmlHttpOPCenterE.onreadystatechange=function() {
			if(xmlHttpOPCenterE.readyState==4){
				document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterE.responseText;
				document.getElementById("contentCenterB").style.display = 'block';
				
			}
		}
		xmlHttpOPCenterE.open("GET",url,true);
		xmlHttpOPCenterE.send(null);
	}

	// called when someone clicks on 'Export to Excel' in 'View Req Report'  
	function exportReportToExcel (){
			
			// Now call the servlet with Export call.
			var url = "/GloreeJava2/servlet/ReportAction?action=exportReportToExcel";
			fillOPCenterGeneric(url, "contentCenterA");
	}
		
	// called when someone clicks on 'Export to Excel' in 'View Req Report'  
	function reportPagination (reportType, folderId, page, sortBy){
			
		var url = "";
		if (reportType == "traceTree") {
			url = "/GloreeJava2/jsp/Report/displayTraceTreeReport_data.jsp?folderId=" + folderId;
			url += "&page=" + page;
			document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			fillOPCenterGeneric(url, "contentCenterF");			
		}
		if (reportType == "list") {
			url = "/GloreeJava2/jsp/Report/displayListReport_data.jsp?folderId=" + folderId;
			url += "&page=" + page;
			document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			fillOPCenterGeneric(url, "contentCenterF");			
		}
		
		if (reportType == "requirementsInRealFolder") {
			
			url="/GloreeJava2/jsp/Folder/displayAllRequirementsInRealFolder.jsp?folderId=" + folderId;
			url += "&sortBy=" + encodeURIComponent(sortBy); 
			url += "&page=" + page;
			document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			fillOPCenterGeneric(url, "contentCenterF");
		}
		if (reportType == "requirementsInVirtualFolder") {
			url="/GloreeJava2/jsp/Folder/displayAllRequirementsInVirtualFolder.jsp?folderId="+ folderId;
			url += "&page=" + page;
			fillOPCenterGeneric(url, "contentCenterF");
		}
		
		if (reportType == "requirementsInExplorer") {
			
			url="/GloreeJava2/jsp/Folder/displayRequirementsInExplorer.jsp?folderId=" + folderId;
			url += "&page=" + page;
			document.getElementById("folderContentDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			fillOPCenterGeneric(url, "folderContentDiv");
		}

		
	}
	
	
	//////////////////////////////////////////////////////////////////////////
	// //
	// Trace Tree Report
	// //
	// ////////////////////////////////////////////////////////////////////////
	
	
	
	// called when someone clicks on 'Create New TraceTree Report' button in the
	// Folder
	// Display
	function createNewTraceTreeReport(folderId){
			
		// set the other content centers to empty.
		document.getElementById("contentCenterB").style.display = 'none';
		document.getElementById("contentCenterC").style.display = 'none';
		document.getElementById("contentCenterD").style.display = 'none';
		document.getElementById("contentCenterF").style.display = 'none';
		
		
		// First time, display the filter and the report
		var url="/GloreeJava2/jsp/Report/displayTraceTreeReport.jsp?folderId=" + folderId;
		url += "&bustcache=" + new Date().getTime() ;
		xmlHttpOPCenterE =GetXmlHttpObject();		
		xmlHttpOPCenterE.onreadystatechange=function() {
			if(xmlHttpOPCenterE.readyState==4){
				document.getElementById('contentCenterE').style.display = 'block'; 
				document.getElementById('contentCenterE').innerHTML=xmlHttpOPCenterE.responseText;
			}
		}
		xmlHttpOPCenterE.open("GET",url,true);
		xmlHttpOPCenterE.send(null);		
	}
	

	
	
	
	//////////////////////////////////////////////////////////////////////////
	// //
	// Reports //
	// //
	// ////////////////////////////////////////////////////////////////////////
	
	

	// called when someone clicks on a Report
	function displayFolderMetrics(folderId){
		
			
			// set the other content centers to empty.
			
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";	
			document.getElementById("contentCenterF").style.display = "none";	
				
			// Now display the Report.
			document.getElementById("contentCenterB").innerHTML= "&nbsp;&nbsp;Working..."
			url = "/GloreeJava2/jsp/Report/displayFolderMetrics.jsp?folderId=" + folderId;
			contentArea = 'contentCenterB';
			fillOPCenterGeneric(url,contentArea);
	}
	


	
	//////////////////////////////////////////////////////////////////////////
	// //
	// Change Logs //
	// //
	// ////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	// called when someone clicks on a Project CoreInfoin the admin screen
	function displayProjectCoreInfo(){
			
			
			// set the other content centers to empty.
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";	
			document.getElementById("contentCenterF").style.display = "none";	
			
			// Now display the Report.
			url = "/GloreeJava2/jsp/AdministerProject/CoreInfo/coreInfo.jsp?";
			url += "&bustcache=" + new Date().getTime() ;
			xmlHttpOPCenterB =GetXmlHttpObject();
			
			
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					document.getElementById("contentCenterB").style.display = "block";
					document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);
	}
	
	// called when someone clicks on Connect Projects link in the admin screen
	function connectProjectsForm(){
			
			
			// set the other content centers to empty.
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";	
			document.getElementById("contentCenterF").style.display = "none";	
							
			// Now display the Report.
			url = "/GloreeJava2/jsp/AdministerProject/ConnectProjects/connectProjects.jsp?";
			
			url += "&bustcache=" + new Date().getTime() ;
			fillOPCenterGeneric(url, "contentCenterB");
			
	}

	// called when some one clicks on the integrationMenu link in the admin screen.
	function integrationMenuForm(){
		
		
		// set the other content centers to empty.
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";	
		document.getElementById("contentCenterF").style.display = "none";	
		
		
		// Now display the Report.
		url = "/GloreeJava2/jsp/AdministerProject/IntegrationMenu/integrationMenu.jsp?";
		
		url += "&bustcache=" + new Date().getTime() ;
		fillOPCenterGeneric(url, "contentCenterB");
		
	}

	function updateIntegrationMenu(counter){		
			
		var url="/GloreeJava2/servlet/ProjectAction";		
		url += "?action=updateIntegrationMenu&";
		url +=  "&menuType=requirement";
		url +=  "&counter=" + counter;
		
		url += "&bustcache=" + new Date().getTime() ;
		for (var i=1; i <= counter; i++){
			var labelAttributeId = "menuLabel" + i;
			var valueAttributeId = "menuValue" + i;
			var label = document.getElementById(labelAttributeId).value;
			var value = document.getElementById(valueAttributeId).value;
			url +=  "&" + labelAttributeId + "=" + encodeURIComponent(label) ;
			url +=  "&" + valueAttributeId + "=" + encodeURIComponent(value) ;
		}
		fillOPCenterGeneric(url, "contentCenterB");
	}

	function connectProjects(){		
		var connectionDescriptionObject = document.getElementById("connectionDescription");
		var connectToProjectIdObject = document.getElementById("connectToProjectId");
		
		if (connectionDescriptionObject.value.length == 0) {
			alert ("Please enter a value for Connection Description");
			connectionDescriptionObject.focus();
			connectionDescriptionObject.style.backgroundColor="#FFCC99";
			return(0);
		}
			
		var url="/GloreeJava2/servlet/ProjectAction";
		
		url += "?action=connectToProject&";
		url += "connectToProjectId=" + connectToProjectIdObject.options[connectToProjectIdObject.selectedIndex].value + "&";
		url +=  "connectionDescription=" + encodeURIComponent(connectionDescriptionObject.value) ;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "contentCenterB");
	}
	
	function disconnectProjects(disconnectProjectId){		
		var url="/GloreeJava2/servlet/ProjectAction";
		
		url += "?action=disconnectFromProject&";
		url += "disconnectProjectId=" + disconnectProjectId;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "contentCenterB");
	}
	
	function updateCoreInfo(thisForm){		
		var projectName = thisForm.projectName;
		var shortName = thisForm.shortName;
		var projectDescription = thisForm.projectDescription;
		
		var projectOwner = thisForm.projectOwner;
		var projectWebsite = thisForm.projectWebsite;
		var projectOrganization = thisForm.projectOrganization;
		var projectTags = thisForm.projectTags;
		
		var projcetTimeZone = thisForm.projectTimeZone;
		var gmtDelta = thisForm.gmtDelta;
		
		var enableVotesRequirementTypesIdsObject = thisForm.enableVotesRequirementTypesIds;
		var enableVotesRequirementTypesIds = "";
		for (var i = 0; i < enableVotesRequirementTypesIdsObject.options.length; i++) {
			if (enableVotesRequirementTypesIdsObject.options[i].selected) {
				enableVotesRequirementTypesIds+= enableVotesRequirementTypesIdsObject.options[i].value + ','; 
			}
		}
		
		var enableAgileScrum = thisForm.enableAgileScrum;
		
		var enableAgileScrumRequirementTypeIdsObject = thisForm.enableAgileScrumRequirementTypeIds;
		var enableAgileScrumRequirementTypeIds = "";
		for (var i = 0; i < enableAgileScrumRequirementTypeIdsObject.options.length; i++) {
			if (enableAgileScrumRequirementTypeIdsObject.options[i].selected) {
				enableAgileScrumRequirementTypeIds+= enableAgileScrumRequirementTypeIdsObject.options[i].value + ','; 
			}
		}
		
		var percentageCompleteDriverObject = thisForm.percentCompleteDriver;
		var percentageCompleteDriver = percentageCompleteDriverObject.options[percentageCompleteDriverObject.selectedIndex].value;
		
		var hidePriorityObject = thisForm.hidePriority;
		var hidePriority = hidePriorityObject.options[hidePriorityObject.selectedIndex].value;
		
		
		var restrictedDomains = thisForm.restrictedDomains;
		
		
		var healthBarObject = thisForm.healthBar;
		var healthBarSettings = "";
		for (var i = 0; i < healthBarObject.options.length; i++) {
			if (healthBarObject.options[i].selected) {
				healthBarSettings+= healthBarObject.options[i].value + ','; 
			}
		}
		
		
		var powerUserSettingsObject = thisForm.powerUserSettings;
		var powerUserSettings = "";
		for (var i = 0; i < powerUserSettingsObject.options.length; i++) {
			if (powerUserSettingsObject.options[i].selected) {
				powerUserSettings+= powerUserSettingsObject.options[i].value + ','; 
			}
		}
		
		
		if (projectName.value.length == 0) {
			alert ("Please enter a value for Project Name");
			projectName.focus();
			projectName.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (shortName.value.length == 0) {
			alert ("Please enter a value for Project Prefix");
			shortName.focus();
			shortName.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (projectDescription.value.length == 0) {
			alert ("Please enter a value for Project Description");
			projectDescription.focus();
			projectDescription.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (projectDescription.value.length > 1000){
			alert(" Please ensure that your Descriptoin is not longer than 1000 characters. Your current message is " + projectDescription.value.length + " characters long");
			projectDescription.focus();
			projectDescription.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		
		// set the other content centers to empty.
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
			
		var url="/GloreeJava2/servlet/ProjectAction";
		
		url += "?action=updateCoreInfo&";
		url += "projectName=" + projectName.value + "&";
		url +=  "shortName=" + shortName.value + "&";
		url += "projectOwner=" + projectOwner.value + "&";
		url += "projectWebsite=" + projectWebsite.value + "&";
		url += "projectOrganization=" + projectOrganization.value + "&";
		url += "projectTags=" + projectTags.value + "&";
		
		url += "projcetTimeZone=" + projcetTimeZone.value + "&";
		url += "gmtDelta=" + gmtDelta.value + "&";
		
		url +=  "enableVotesRequirementTypesIds=" + encodeURIComponent(enableVotesRequirementTypesIds) + "&";
		
		if (enableAgileScrum == null){
			// note : the enableAgileScrum check box can be null if the site is not enabled for Agile. so we have to handle it.
			url += "enableAgileScrum=0&";
		}
		else {
			// note : the enableAgileScrum check box can be null if the site is not enabled for Agile. so we have to handle it.
			url += "enableAgileScrum=" + enableAgileScrum.options[enableAgileScrum.selectedIndex].value + "&";
		}
		url +=  "enableAgileScrumRequirementTypeIds=" + encodeURIComponent(enableAgileScrumRequirementTypeIds) + "&";
		
		
		
		url +=  "projectDescription=" + encodeURIComponent(projectDescription.value) + "&";
		
		url += "percentageCompleteDriver=" + encodeURIComponent(percentageCompleteDriver) + "&";
		url += "hidePriority=" + encodeURIComponent(hidePriority) + "&";
		
		url += "powerUserSettings=" + encodeURIComponent(powerUserSettings) + "&";
		url += "healthBarSettings=" + encodeURIComponent(healthBarSettings) + "&";
		
		url += "restrictedDomains=" + encodeURIComponent(restrictedDomains.value) + "&";
		
		// now that we are about to send the call to the server, lets disable to button to indicate submission
		document.getElementById("updateCoreInfoButton").disabled=true;		
		var message = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working..." + 
			"<br><br>We are updating the project settings. This may take a few minutes <br></span>";
		document.getElementById("userAlertMessageDiv").innerHTML= message;
		document.getElementById("userAlertMessageDiv").style.display = "block";
		
		fillOPCenterGeneric(url, "contentCenterB");
	}
	

	
	//////////////////////////////////////////////////////////////////////////
	// //
	// Context / Google Search //
	// //
	// ////////////////////////////////////////////////////////////////////////
	
	
	function  handleProjectSearchkeyPress(event, searchType) {
		var keyCode = event.keyCode;
		if (keyCode == 13) {
			projectSearch(searchType)
		}
	}
	
	
	// Called when someone tries to search within the project.
	function projectSearch(searchType){
		var searchString 
		
		if (searchType == 'reqId') {
			searchString = document.getElementById("reqIdSearchString").value;
			document.getElementById("contentCenterA").style.display = "none";
			document.getElementById("contentCenterB").style.display = "none";
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";	
			
			document.getElementById("contentCenterF").style.display = "block";
			document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			// if the previous folderis not null, set it's background to gray.
			var prevFocusFolder = document.getElementById(prevFocusFolderId); 
			 if (prevFocusFolder != null){
				prevFocusFolder.style.backgroundColor = "#EBE4F2";
			 }
			 
			
			// Now display the search results.
			url = "/GloreeJava2/jsp/Report/displayProjectSearchReport.jsp?searchType=";
			url += searchType +  "&searchString=" + searchString;
			contentArea = 'contentCenterF';
			fillOPCenterGeneric(url,contentArea);
		}
		else {
			searchString = document.getElementById("googleSearchString").value;
			// lets pass this to the fintItNowForAProject function.
			document.getElementById("contentCenterA").style.display = "none";
			document.getElementById("contentCenterB").style.display = "none";
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";	
			
			document.getElementById("contentCenterF").style.display = "block";
			document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			
			var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/findItNowForAProject.jsp?";		
			url += "&searchString=" + encodeURIComponent(searchString);
			url += "&targetRequirementTypeId=0" ;
			url += "&bustcache=" + new Date().getTime() ;
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					document.getElementById("contentCenterF").style.display = "block";
					document.getElementById("contentCenterF").innerHTML= "<div id='myTasksDiv'> " + xmlHttpOPCenterB.responseText + "  </div>";
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);
			


		}


	}

	function rebuildSearchIndex(){

		document.getElementById("rebuildIndexDiv").style.display = "block";
		document.getElementById("rebuildIndexDiv").innerHTML= "<span class='normalText'>Working...</span>";


			url= "/GloreeJava2/servlet/ProjectAction?action=rebuildSearchIndex";
			url += "&bustcache=" + new Date().getTime() ;
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					document.getElementById("rebuildIndexDiv").innerHTML= xmlHttpOPCenterB.responseText;
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);
			
	}



	//////////////////////////////////////////////////////////////////////////
	// //
	// Word Template//
	// //
	// ////////////////////////////////////////////////////////////////////////
	
	// called when someone clicks on to 'Import From Excel' button in
	// contentCenterA
	function createWordTemplateForm(folderId){
		// set the other content centers to empty.
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";				
		document.getElementById("contentCenterF").style.display = "none";
			

		// set contentCenterB with the createFolderForm s.
		url = "/GloreeJava2/jsp/WordTemplate/createWordTemplateForm.jsp?folderId=" + folderId;
		url += "&bustcache=" + new Date().getTime() ;
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterB').style.display = 'block'; 
				document.getElementById('contentCenterB').innerHTML=xmlHttpOPCenterB.responseText;
				
				// since we were having difficulty , getting the menu tab to show up
				// we have decided to wait till content center B is populated before
				// we attempt to set the menu items.

				// lets implement the tabbed look & feel of menu items.
				var focusTab = "url(/GloreeJava2/images/focusTab.png)";
				var nonFocusTab = "url(/GloreeJava2/images/nonFocusTab.png)";
				// the clicked item is set to gray, and the rest are set to the yellow color.
				document.getElementById("menuBulkCreateRequirement").style.backgroundImage = nonFocusTab;
				document.getElementById("menuCreateRequirement").style.backgroundImage = nonFocusTab
				document.getElementById("menuCreateListReport").style.backgroundImage = nonFocusTab
				document.getElementById("menuCreateTraceTreeReport").style.backgroundImage = nonFocusTab

				document.getElementById("menuEditFolder").style.backgroundImage = nonFocusTab
				document.getElementById("menuImportFromExcel").style.backgroundImage = nonFocusTab
				document.getElementById("menuCreateWordTemplate").style.backgroundImage=focusTab

			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
	}
	

	function generateEmptyWordTemplateForm(){
		// set contentCenterB with the createFolderForm s.
		url = "/GloreeJava2/jsp/WordTemplate/generateEmptyWordTemplateForm.jsp?";
		fillOPCenterGeneric(url, "contentCenterB");
	}

	
	function createWordTemplate(){
		var cRF = document.getElementById("createWordTemplateForm");

		var templateName = cRF.templateName;
		var templateDescription = cRF.templateDescription;
 
		if (templateName.value.length == 0) {
			alert ("Please enter a Template Name");
			templateName.focus();
			templateName.style.backgroundColor="#FFCC99";
			return;
		}
		if (templateDescription.value.length == 0) {
			alert ("Please enter a Template Description");
			templateDescription.focus();
			templateDescription.style.backgroundColor="#FFCC99";
			return;
		}
		
		//now that we are ready to upload the word tempalte file lets gray out the
		// submit button to prevent accidentala resubmits.
		document.getElementById("uploadWordTemplateButton").disabled=true;		
		
		cRF.submit();
	}	
	
	function editWordTemplate(){
		var cRF = document.getElementById("editWordTemplateForm");

		var templateName = cRF.templateName;
		var templateDescription = cRF.templateDescription;
 
		if (templateName.value.length == 0) {
			alert ("Please enter a Template Name");
			templateName.focus();
			templateName.style.backgroundColor="#FFCC99";
			return;
		}
		if (templateDescription.value.length == 0) {
			alert ("Please enter a Template Description");
			templateDescription.focus();
			templateDescription.style.backgroundColor="#FFCC99";
			return;
		}
		
		//now that we are ready to upload the word tempalte file lets gray out the
		// submit button to prevent accidentala resubmits.
		document.getElementById("updateWordTemplateButton").disabled=true;		
		
		cRF.submit();
	}
	// 
	// called when someone chooses to create a new word template, After it's
	// stored in filer
	// and a bean created, it's redirected to this page.
	function displayWordTemplateAfterCreation(folderId, templateId){

		
		// set the other content centers to empty.
		document.getElementById("contentCenterB").innerHTML= ""
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		// Now fill content Right with all the requirements inside this.
		url="/GloreeJava2/jsp/WordTemplate/displayWordTemplate.jsp?folderId=" + folderId;
		url += "&templateId=" + templateId;
		document.getElementById("contentCenterB").innerHTML= "&nbsp;&nbsp;Working..."
		fillOPCenterGeneric(url, "contentCenterB");
		
		// since we just created a new template, lets refresh contentCenterA (top row of menus) and the explorer page.
		// lets highlight the folder in explorer first.
		displayFolderInExplorer(folderId) ;
		displayFolderContentCenterA(folderId) ;
		//displayFolderContentRight(folderId);

	}
	



	function displayWordTemplate(folderId, templateId){
 
		
		// set the other content centers to empty.
		document.getElementById("contentCenterB").innerHTML= ""
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		// Now fill content Right with all the requirements inside this.
		url="/GloreeJava2/jsp/WordTemplate/displayWordTemplate.jsp?folderId=" + folderId;
		url += "&templateId=" + templateId;
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("contentCenterB").innerHTML= "&nbsp;&nbsp;Working..."
		
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterB').style.display = 'block'; 
				document.getElementById('contentCenterB').innerHTML=xmlHttpOPCenterB.responseText;
				// now lets put focus on the start of this page.
				window.location.hash="TopOfDisplayRequirements";
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
	}

	// called when someone clicks on a Report that was saved in the past, from the userDashboard
	// this first navigates to the correct folder and then opens up the report.
	function navigateToAndDisplayWordTemplate(folderId, templateId){
		// lets put the focus on the folder on the right.
		if (tree != null){
			tree.expandAll();
		}
		// lets put the focus on the folder on the right.
		displayFolderInExplorer(folderId) ;
		
		// display the folder contents in ContentRight
		//displayFolderContentRight(folderId);

		// lets display the folder menu in contentCenterA.
		var url="/GloreeJava2/jsp/Folder/displayRealFolder.jsp?folderId="+ folderId;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterA").style.display = "block";
				document.getElementById("contentCenterA").innerHTML=xmlHttpOPCenterB.responseText;
				// now lets call displayWordTemplate
				displayWordTemplate(folderId, templateId);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
			
	}

	
	function editWordTemplateForm(folderId, templateId){
		var url = "/GloreeJava2/jsp/WordTemplate/editWordTemplateForm.jsp?templateId=" + templateId + "&folderId=" + folderId;
		fillOPCenterGeneric(url, "templateCoreDiv");
	}

	
	// called when someone clicks 'Delete' button on the displayWordTemplate page.
	function deleteWordTemplateForm(templateId, folderId){
		
		var prompt= "<div id='deleteWordTemplatePromptDiv' class='alert alert-success'>";
		prompt += "<br><br><b> ";
		prompt += "<span class='headingText'>Are you sure you want to delete this Word Document?<br><br>";
		prompt += "Please note that this Word Document will be permanently deleted.";
		prompt += "</span></b><br><br>"
		prompt += "\n<span class='normalText'><input type='button' name='Delete' value='Delete' onClick='deleteWordTemplate(";
		prompt += templateId + "," + folderId + ")'>";
		prompt += "\n<input type='button' name='Cancel' value='Cancel' ";
		prompt += "onClick= 'document.getElementById(\"templatePromptDiv\").innerHTML = \"\";'>";
		prompt += "\n</span><br></div><br><br>"
		// We set the prompt with the appropriate values.
		document.getElementById("templatePromptDiv").innerHTML = prompt;
	}

	function deleteWordTemplate(templateId, folderId){

		
		// set the other content centers to empty.
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		
		var url="/GloreeJava2/servlet/WordTemplateAction?";
		url += "action=deleteWordTemplate";
		url += "&folderId=" + folderId;
		url += "&templateId=" + templateId;
		url += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById("contentCenterB").innerHTML= "&nbsp;&nbsp;Working..."
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;

			
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
	}
	
	function generateWordReportForm(templateId, folderId){
		var url = "/GloreeJava2/jsp/WordTemplate/generateWordReportForm.jsp?templateId=" + templateId + "&folderId=" + folderId;
		document.getElementById("templateCoreDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "templateCoreDiv");
	}

	
	function generateWordReportFormForTDCS(templateId, folderId){
		var url = "/GloreeJava2/jsp/WordTemplate/generateWordReportFormForTDCS.jsp?templateId=" + templateId + "&folderId=" + folderId;
		document.getElementById("templateCoreDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "templateCoreDiv");
	}


	// this method displays the screen where the user can 
	// select the Requirement Location process in the word doc
	// i.e. is it hyperlinks, or some form of search string.
	function createRequirementsFromWordTemplateMapForm(templateId, folderId){
		var url = "/GloreeJava2/jsp/WordTemplate/createRequirementsFromWordTemplateMapForm.jsp?templateId=" + templateId + "&folderId=" + folderId;
		document.getElementById("templateCoreDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "templateCoreDiv");
	}

	// based on which locationProcess the user chooses
	// we display further options.
	// for example, if he / she chooses, hyperlink, we empty out the further options.
	// if he chooses Styles then we display all the styles in the word doc.
	function displayLocationOptions(templateId, folderId) {
		var cRF = document.getElementById("createRequirementsFromWordTemplateMapForm");

		var locateProcessObject = cRF.locateProcess;
		var locateProcess = locateProcessObject.options[locateProcessObject.selectedIndex].value;
		
		if (locateProcess == 'tables') {
			document.getElementById("locationOptionsDiv").style.display = "none";
		}
		
		if (locateProcess == 'hyperlinks') {
			document.getElementById("locationOptionsDiv").style.display = "none";
		}
		if (
				(locateProcess == 'styles') 
				||
				(locateProcess == 'styles-updatable')
			){
			// lets diable the submit button, so that the user doesn't accidentally hit it
			cRF.TagRequirements.disabled = true;
			document.getElementById("locationOptionsDiv").style.display = "block";
			var url = "/GloreeJava2/jsp/WordTemplate/displayStylesInWordTemplate.jsp?templateId=" + templateId + "&folderId=" + folderId;
			document.getElementById("locationOptionsDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					document.getElementById("locationOptionsDiv").innerHTML=xmlHttpOPCenterB.responseText;
					// once we get the response back, lets re-activate the submit button.
					cRF.TagRequirements.disabled = false;

				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);		
			
		}
		if (locateProcess == 'paragraphs') {
						
			document.getElementById("locationOptionsDiv").style.display = "block";
			var content = "" +
				"	<table> " +  
				"		<tr> <td>"+ 
				"		<span class='normalText'> " + 
				"		Please enter the words you want to search for.</span>" + 
				"		</td> <td> " + 
				"		<span class='normalText'> " + 				
				"		<input type='text' name='paragraphSearch' id='paragraphSearch' size='100' " +
				"		value='Shall, Will, Must, May, Can, Should'>  </span> "	
				"		</td></tr> " + 
				"   </table> ";
			document.getElementById("locationOptionsDiv").innerHTML= content;
		}				
	}
	
	// once the user selects the Requirement Location process,
	// we call the WordTemplateAction to locate all the Reqs of this
	// type and show the confirmation screen..
	function createRequirementsFromWordTemplateConfirm(folderId, templateId){

		var cRF = document.getElementById("createRequirementsFromWordTemplateMapForm");

		var locateProcessObject = cRF.locateProcess;
		var locateProcess = locateProcessObject.options[locateProcessObject.selectedIndex].value;
		

			
		var url="/GloreeJava2/servlet/WordTemplateAction?";
		url += "action=createRequirementsConfirm";
		url += "&folderId=" + folderId;
		url += "&templateId=" + templateId;
		url += "&locateProcess=" + locateProcess;

		if (locateProcess == 'styles') {
			// lets add the style name the user selected to the URL prams.
			var stylesObject = cRF.styles;
			url += "&styleName=" +  stylesObject.options[stylesObject.selectedIndex].value;
		}
		
		if (locateProcess == 'styles-updatable') {
			// lets add the style name the user selected to the URL prams.
			var stylesObject = cRF.styles;
			url += "&styleName=" +  stylesObject.options[stylesObject.selectedIndex].value;
		}
		
		if (locateProcess == 'paragraphs') {
			// if paragraphs is empty throw an alert.
			var paragraphSearchObject = cRF.paragraphSearch;
			if (paragraphSearchObject.value.length == 0) {
				alert ("Please enter a search criteria for locating Requirements");
				paragraphSearchObject.focus();
				paragraphSearchObject.style.backgroundColor="#FFCC99";
				return;
			}			
			// lets add the paragraphSearch value the user selected to the URL prams.
			url += "&paragraphSearch=" +  paragraphSearchObject.value;
		}
		
		//now that we are ready to identify the reqs from the word template  lets gray out the
		// submit button to prevent accidentala resubmits.
		document.getElementById("identifyRequirementsInWordTemplateButton").disabled=true;

		
		document.getElementById("templateCoreDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "templateCoreDiv");
	}

	// once the user selects the Requirement Location process,
	// we call the WordTemplateAction to locate all the Reqs of this
	// type and show the confirmation screen..
	function createRequirementsFromWordTemplate(folderId, templateId, locateProcess, styleName, paragraphSearch){
		
		var cRF = document.getElementById("createRequirementsFromWordTemplateConfirm");
		
		var locationNumberString = "";
		// now lets get target location Numbers . These are the selected reqs.
		// we will need to get all the target location Numbers  and concatenate them with :##:
		// if only one req is displayed in data grid, the checkbox stops behaving like an array
		// and behaves like a single element. in this scenario checkbox.length craps out.
		// to catch this scenario we have to do this...
		if (cRF.locationNumber.length == null){
			// only one requirement row in data grid.
			locationNumberString += cRF.locationNumber.value + "::";
		}
		else {
			// multiple requirement rows in data grid.
			for (i=0; i< cRF.locationNumber.length;i++){
				if (cRF.locationNumber[i].checked == true ){
					locationNumberString += cRF.locationNumber[i].value + "::";
				}
			}		
			
		}
		
		//now that we are ready to create the reqs from the word template  lets gray out the
		// submit button to prevent accidentala resubmits.
		document.getElementById("createRequirementsFromWordTemplateButton").disabled=true;
		
		
		var url="/GloreeJava2/servlet/WordTemplateAction?";
		url += "action=createRequirements";
		url += "&folderId=" + folderId;
		url += "&templateId=" + templateId;
		url += "&locateProcess=" + locateProcess;	
		url += "&locationNumberString=" + locationNumberString;		
		url += "&styleName=" + styleName;
		url += "&paragraphSearch=" + paragraphSearch;
		url += "&bustcache=" + new Date().getTime() ;
		

		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("templateCoreDiv").innerHTML=xmlHttpOPCenterB.responseText;
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);			
		
		
	}
	
	
	function generateReqTemplateReportInReqCore(requirementId){
		
		var url="/GloreeJava2/servlet/WordTemplateAction?";
		url += "action=generateReqTemplateReport";
		url += "&requirementId=" + requirementId;
		url += "&divId=" + "0";
		url += "&reportFormat=doc" ;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "requirementPromptDiv");	
	}
	
	function generateReqTemplateReport(requirementId, divId){
		
		var url="/GloreeJava2/servlet/WordTemplateAction?";
		url += "action=generateReqTemplateReport";
		url += "&requirementId=" + requirementId;
		url += "&divId=" + divId;
		url += "&reportFormat=doc" ;
		url += "&bustcache=" + new Date().getTime() ;
		

		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById(divId).style.display = "block";
				document.getElementById(divId).innerHTML=xmlHttpOPCenterB.responseText;	
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);			
		
		
	}
	//////////////////////////////////////////////////////////////////////////
	// //
	// Import from Excel //
	// //
	// ////////////////////////////////////////////////////////////////////////
	
	function uploadExcel(thisForm) {
		var importFile = thisForm.importFile;
		
		
		if ((importFile.value == null) || (importFile.value.length == 0)){
			alert(" Please select a Microsft Excel 97-2003 file to upload");
			importFile.focus();
			importFile.style.backgroundColor="#FFCC99";
			return(0);
		}
		var importFileValue = importFile.value;
		var pos= importFileValue.indexOf(".xlsx")
		if (pos > 0){
			// means this is an excel 2007 file
			alert(" You are trying to uplaod an MS Excel 2007 file. Please convert this to an Excel 97-2003 version first");
			importFile.focus();
			importFile.style.backgroundColor="#FFCC99";
			return(0);
		}

		//now that we are ready upload file, lets gray out the
		// submit button to prevent accidentala resubmits.
		document.getElementById("uploadFileButton").disabled=true;
		
		thisForm.submit();
	}
	
	
	
	function generateJSONFromExcel(fileCode){
		

		// set contentCenterB with the createFolderForm s.
		url = "/GloreeJava2/jsp/TraceNow/getJSONFromExcel.jsp?fileCode=" + fileCode;
		url += "&bustcache=" + new Date().getTime() ;
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('jsonCode').value=xmlHttpOPCenterB.responseText;
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
	}

	// called when someone clicks on to 'Import From Excel' button in
	// contentCenterA
	function importFromExcelForm(folderId){
		// set the other content centers to empty.
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";				
		document.getElementById("contentCenterF").style.display = "none";
			

		// set contentCenterB with the createFolderForm s.
		url = "/GloreeJava2/jsp/Excel/importFromExcelForm.jsp?folderId=" + folderId;
		url += "&bustcache=" + new Date().getTime() ;
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterB').style.display = 'block'; 
				document.getElementById('contentCenterB').innerHTML=xmlHttpOPCenterB.responseText;
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
	}

	
	// called when someone has selected 'createNewRequirements' option in importFromExcel.
	// called after the file has been stored in the database.
	function createNewRequirementsFromExcelMapForm(folderId){
		
		// set the other content centers to empty.
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		// Now fill content Right with all the requirements inside this.
		url="/GloreeJava2/jsp/Excel/createNewRequirementsFromExcelMapForm.jsp?folderId=" + folderId;
		document.getElementById("contentCenterB").innerHTML= "&nbsp;&nbsp;Working..."
		fillOPCenterGeneric(url, "contentCenterB");
		
		// since we just created a new template, lets refresh contentCenterA (top row of menus) and the explorer page.
		// lets highlight the folder in explorer first.
		displayFolderInExplorer(folderId) ;
		//displayFolderContentRight(folderId);
		
		
		// since we were having difficulty , getting the menu tab to show up
		// we have decided to wait till content center A is populated before
		// we attempt to set the menu items.
		
		var url="/GloreeJava2/jsp/Folder/displayRealFolder.jsp?folderId="+ folderId;
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterA').style.display = 'block'; 
				document.getElementById('contentCenterA').innerHTML=xmlHttpOPCenterB.responseText;
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
	}
	
	
	// called when someone has selected 'updateExistingRequirements' option in importFromExcel.
	// called after the file has been stored in the database.
	function updateExistingRequirementsFromExcelMapForm(folderId){

		
		// set the other content centers to empty.
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		// Now fill content Right with all the requirements inside this.
		url="/GloreeJava2/jsp/Excel/updateExistingRequirementsFromExcelMapForm.jsp?folderId=" + folderId;
		document.getElementById("contentCenterB").innerHTML= "&nbsp;&nbsp;Working..."
		fillOPCenterGeneric(url, "contentCenterB");
		
		displayFolderInExplorer(folderId) ;
		//displayFolderContentRight(folderId);
		
		// since we were having difficulty , getting the menu tab to show up
		// we have decided to wait till content center A is populated before
		// we attempt to set the menu items.
		
		var url="/GloreeJava2/jsp/Folder/displayRealFolder.jsp?folderId="+ folderId;
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterA').style.display = 'block'; 
				document.getElementById('contentCenterA').innerHTML=xmlHttpOPCenterB.responseText;
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		

	}
	
	
	
	// this function is used to map Excel Columns to Requirement Type Attributes
	// in createNewRequirementsFromExcelMap.jsp
	function createNewRequirementsFromExcel(thisForm, folderId, mandatoryAttributeNames) {		
		

		var nameColumn = thisForm.nameColumn;
		var descriptionColumn = thisForm.descriptionColumn;
		var priorityColumn = thisForm.priorityColumn;
		var ownerColumn = thisForm.ownerColumn;
		var pctCompleteColumn = thisForm.pctCompleteColumn;
		var externalURLColumn = thisForm.externalURLColumn;
		var folderPathColumn = thisForm.folderPathColumn;
		var processSelectedColumn = thisForm.processSelectedColumn;

		var existingParentColumn = thisForm.existingParentColumn;
		var parentChildColumn = thisForm.parentChildColumn;

		
		var traceToColumn = thisForm.traceToColumn;
		var traceFromColumn = thisForm.traceFromColumn;
		var testingStatusColumn = thisForm.testingStatusColumn;
		
		
		if (nameColumn.selectedIndex == 0)   {
			alert ("Please Select a column for Requirement Name");
			nameColumn.focus();
			nameColumn.style.backgroundColor="#FFCC99";
			return(0);
		}
				
		if (descriptionColumn.selectedIndex == 0)   {
			alert ("Please Select a column for Requirement description");
			descriptionColumn.focus();
			descriptionColumn.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		// lets make sure that the mandatory attributes have been selected.
		// the mandatoryAttributeNames string has mandatory attributes in 
		// a :#: separated form with id :-: name structure
		mandatoryArray = mandatoryAttributeNames.split(':#:');
		for (j=0; j< mandatoryArray.length; j++){
			var labelArray = mandatoryArray[j].split(':-:') ;
			var mandatoryId =  labelArray[0];
			var mandatoryLabel =  labelArray[1];
			var mandatoryObject = document.getElementById(mandatoryId);
			if (mandatoryObject != null) {
				
				if (mandatoryObject.selectedIndex == 0){
					alert ("Please Select a column for Mandatory attribute " + mandatoryLabel);
					mandatoryObject.focus();
					mandatoryObject.style.backgroundColor="#FFCC99";
					return(0);
				}
			}
			
		}
		
		
		
		
		
		
		//now that we are ready create new reqs from file, lets gray out the
		// submit button to prevent accidental resubmits.
		document.getElementById("createNewRequirementsFromExcelButton").disabled=true;
		
		
		var url="/GloreeJava2/servlet/ImportFromExcelProcessAction";
		var params; 
		params = "action=createNewRequirements&";
		params = params + "folderId=" + folderId + "&";
		
		params = params + "nameColumn=" + encodeURIComponent(nameColumn[nameColumn.selectedIndex].value) + "&";
		params = params + "descriptionColumn=" + encodeURIComponent(descriptionColumn[descriptionColumn.selectedIndex].value) + "&";
		params = params + "priorityColumn=" + encodeURIComponent(priorityColumn[priorityColumn.selectedIndex].value) + "&";
		params = params + "ownerColumn=" + encodeURIComponent(ownerColumn[ownerColumn.selectedIndex].value) + "&";
		params = params + "pctCompleteColumn=" + encodeURIComponent(pctCompleteColumn[pctCompleteColumn.selectedIndex].value) + "&";
		params = params + "externalURLColumn=" + encodeURIComponent(externalURLColumn[externalURLColumn.selectedIndex].value) + "&";
		params = params + "folderPathColumn=" + encodeURIComponent(folderPathColumn[folderPathColumn.selectedIndex].value) + "&";
		params = params + "processSelectedColumn=" + encodeURIComponent(processSelectedColumn[processSelectedColumn.selectedIndex].value) + "&";
		
		params = params + "existingParentColumn=" + encodeURIComponent(existingParentColumn[existingParentColumn.selectedIndex].value) + "&";
		params = params + "parentChildColumn=" + encodeURIComponent(parentChildColumn[parentChildColumn.selectedIndex].value) + "&";
		
		
		params = params + "traceToColumn=" + encodeURIComponent(traceToColumn[traceToColumn.selectedIndex].value) + "&";
		params = params + "traceFromColumn=" + encodeURIComponent(traceFromColumn[traceFromColumn.selectedIndex].value) + "&";
		
		// testingStatus can be set only for Test Results requirement type.		
		if (testingStatusColumn != null){
			params = params + "testingStatusColumn=" + encodeURIComponent(testingStatusColumn[testingStatusColumn.selectedIndex].value) + "&";		
		}
		url = url + "?" + params;
		
		// we use the attributeIdString to split and figure out which attributes
		// to look for.
		// for these attributes , we get the value, build the URL string and .
		// we will use the same logic to get the attribute values and create
		// them in the db.
		
		
		attributeIdString = document.getElementById("attributeIdString");
		
		attributeIds = attributeIdString.value.split('::');
		
		for (a in attributeIds){
			
			var id = attributeIds[a];
			var attribute = document.getElementById(id);
			if (attribute) {
				if (attribute.selectedIndex != 0)   {
					// note : we are taking a conscious decision to not require that 
					// custom attributes be filled.
					url = url + id + '='+  encodeURIComponent(attribute[attribute.selectedIndex].value) + "&" ;
				}
			}
			
		}
		// add attributeIdString to the URL
		url += "attributeIdString=" + attributeIdString.value ; 
		url += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById("contentCenterB").innerHTML= "&nbsp;&nbsp;Working..."
			
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;

				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
	

	function colorCodeExcelUpdatMap(selectObjectName){
		// lets see if this select object's selectedv value is 0 or now. 
		var selectObject= document.getElementById(selectObjectName);
		if (selectObject.selectedIndex == 0 ){
			document.getElementById(selectObjectName+"Row").classList.remove("info");
		}
		else {
			document.getElementById(selectObjectName+"Row").classList.add("info");
		}
		
		
	}
	
	// this function is used to map Excel Columns to Requirement Type Attributes
	// in updateExistingRequirementsFromExcelMap.jsp
	function updateExistingRequirementsFromExcel(thisForm, folderId) {		
		

		var idColumn = thisForm.idColumn;
		var nameColumn = thisForm.nameColumn;
		var descriptionColumn = thisForm.descriptionColumn;
		var priorityColumn = thisForm.priorityColumn;
		var ownerColumn = thisForm.ownerColumn;
		var pctCompleteColumn = thisForm.pctCompleteColumn;
		var externalURLColumn = thisForm.externalURLColumn;
		
		var commentColumn = thisForm.commentColumn;
		var commentByColumn = thisForm.commentByColumn;
		
		var folderPathColumn = thisForm.folderPathColumn;
		var processSelectedColumn = thisForm.processSelectedColumn;

		var existingParentColumn = thisForm.existingParentColumn;

		var traceToColumn = thisForm.traceToColumn;
		var traceFromColumn = thisForm.traceFromColumn;
		var testingStatusColumn = thisForm.testingStatusColumn;

		
		var url="/GloreeJava2/servlet/ImportFromExcelProcessAction";
		var params = "action=updateExistingRequirements&";
		params = params + "folderId=" + folderId + "&";
		
		
		if (idColumn.selectedIndex == 0)   {
			alert ("Please Select a column for Requirement Id. The values for this should look like 'BR-12' etc...");
			idColumn.focus();
			idColumn.style.backgroundColor="#FFCC99";
			return(0);
		}
		else {
			params = params + "idColumn=" + encodeURIComponent(idColumn[idColumn.selectedIndex].value) + "&";
		}
		
		// see if at least 1 map is selected.
		var eligible = false;
		if (nameColumn.selectedIndex != 0)   {
			eligible = true;
			params = params + "nameColumn=" + encodeURIComponent(nameColumn[nameColumn.selectedIndex].value) + "&";
		}
		if (descriptionColumn.selectedIndex != 0)   {
			eligible = true;
			params = params + "descriptionColumn=" + encodeURIComponent(descriptionColumn[descriptionColumn.selectedIndex].value) + "&";
		}

		if (priorityColumn.selectedIndex != 0)   {
			eligible = true;
			params = params + "priorityColumn=" + encodeURIComponent(priorityColumn[priorityColumn.selectedIndex].value) + "&";
		}
		if (ownerColumn.selectedIndex != 0)   {
			eligible = true;
			params = params + "ownerColumn=" + encodeURIComponent(ownerColumn[ownerColumn.selectedIndex].value) + "&";
		}
		if (pctCompleteColumn.selectedIndex != 0)   {
			eligible = true;
			params = params + "pctCompleteColumn=" + encodeURIComponent(pctCompleteColumn[pctCompleteColumn.selectedIndex].value) + "&";
		}
		if (externalURLColumn.selectedIndex != 0)   {
			eligible = true;
			params = params + "externalURLColumn=" + encodeURIComponent(externalURLColumn[externalURLColumn.selectedIndex].value) + "&";
		}
		
		if (commentColumn.selectedIndex != 0)   {
			eligible = true;
			params = params + "commentColumn=" + encodeURIComponent(commentColumn[commentColumn.selectedIndex].value) + "&";
		}
		if (commentByColumn.selectedIndex != 0)   {
			eligible = true;
			params = params + "commentByColumn=" + encodeURIComponent(commentByColumn[commentByColumn.selectedIndex].value) + "&";
		}
		
		
		if (folderPathColumn.selectedIndex != 0)   {
			eligible = true;
			params = params + "folderPathColumn=" + encodeURIComponent(folderPathColumn[folderPathColumn.selectedIndex].value) + "&";
		}
		if (processSelectedColumn.selectedIndex != 0)   {
			eligible = true;
			params = params + "processSelectedColumn=" + encodeURIComponent(processSelectedColumn[processSelectedColumn.selectedIndex].value) + "&";
		}
		
		if (existingParentColumn.selectedIndex != 0)   {
			eligible = true;
			params = params + "existingParentColumn=" + encodeURIComponent(existingParentColumn[existingParentColumn.selectedIndex].value) + "&";
		}
		
		
		if (traceToColumn.selectedIndex != 0) {
			eligible = true;			
			params = params + "traceToColumn=" + encodeURIComponent(traceToColumn[traceToColumn.selectedIndex].value) + "&";
		}
		if (traceFromColumn.selectedIndex != 0) {
			eligible = true;
			params = params + "traceFromColumn=" + encodeURIComponent(traceFromColumn[traceFromColumn.selectedIndex].value) + "&";
		}
		// testingStatusColumn can be null if the req type is not TR
		if (testingStatusColumn != null ) {
			if (testingStatusColumn.selectedIndex != 0) {
				eligible = true;
				params = params + "testingStatusColumn=" + encodeURIComponent(testingStatusColumn[testingStatusColumn.selectedIndex].value) + "&";
			}
		}
		// see if any of the custom attributes are filled up.
		
		// we use the attributeIdString to split and figure out which attributes
		// to look for.
		// for these attributes , we get the value, build the URL string and .
		// we will use the same logic to get the attribute values and create
		// them in the db.
		
		attributeIdString = document.getElementById("attributeIdString");		
		attributeIds = attributeIdString.value.split('::');
		
		for (a in attributeIds){			
			var id = attributeIds[a];
			var attribute = document.getElementById(id);
			if (attribute) {
				if (attribute.selectedIndex != 0)   {
					eligible = true;
					params = params + id + '='+  encodeURIComponent(attribute[attribute.selectedIndex].value) + "&" ;
				
				
					// if this attribute is selected by the user and is a appendable attribute
					// lets make sure that the user has selected a value
					try{
						 var appendReplace = document.getElementById('appendReplace' + id);
						 
						 if (appendReplace != null){
							 if (appendReplace.selectedIndex == 0 ){
								 alert ("Please select your Append or Replace choice");
								 appendReplace.focus();
								 appendReplace.style.backgroundColor="#FFCC99";
								return(0); 
							 }
							 params = params + 'appendReplace' + id + '='+  encodeURIComponent(appendReplace[appendReplace.selectedIndex].value) + "&" ;
						 }
					}
					catch(err){
						
					}
				}
			}
			
		}

		if (eligible == false) {
			alert ("Please Select at least one attribute - column mapping to update");
			nameColumn.focus();
			nameColumn.style.backgroundColor="#FFCC99";
			return(0);
		}
				
		
		
		url = url + "?" + params;
		// add attributeIdString to the URL
		url += "attributeIdString=" + attributeIdString.value ; 
		url += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById("contentCenterB").innerHTML= "&nbsp;&nbsp;Working..."
			
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterB").style.display = "block";
				document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;

				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}
	
	
	//////////////////////////////////////////////////////////////////////////
	// //
	// Tabs //
	// //
	// ////////////////////////////////////////////////////////////////////////
	


	
	
	
	//////////////////////////////////////////////////////////////////////////
	// //
	// 	Release Metrics & Dashboards
	// //
	// ////////////////////////////////////////////////////////////////////////	
	// used to display all the release objects in a project for displaying trend data.
	function displayReleaseMetricsForm(){
		
		// set the other content centers to empty.
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		
		
		
		// lets remove focus from the folder in the explorer.
		// if the previous folderis not null, set it's background to gray.
		var prevFocusFolder = document.getElementById(prevFocusFolderId); 
		 if (prevFocusFolder != null){
			prevFocusFolder.style.backgroundColor = "#EBE4F2";
		 }
		 		

		 
		// Now fill content Right with all the requirements inside this.
		url="/GloreeJava2/jsp/Report/ReleaseDashboard/displayReleaseDashboard.jsp?";
		fillOPCenterGeneric(url, "contentCenterA");
		
	}
	
	function displayReleaseMetrics(){
		

		var releaseIdObject = document.getElementById("releaseId");
		var releaseId = releaseIdObject[releaseIdObject.selectedIndex].value
		if (releaseIdObject.selectedIndex == 0) {
			alert ("Please Select a Release to run reports for.");
			releaseIdObject.focus();
			releaseIdObject.style.backgroundColor="#FFCC99";
			return(0);
		}
					
		
		// set the other content centers to empty.
		
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";	
			
			
		
		// Now display the Report.
		url = "/GloreeJava2/jsp/Report/ReleaseDashboard/displayReleaseMetricsDataTable.jsp?releaseId=" + releaseId;
		fillOPCenterGeneric(url, "contentCenterB");
		
			
		
		var displayRequirementType  = "";
		
		var displayRequirementTypeObject = document.getElementById("displayRequirementType");
		if (displayRequirementTypeObject != null) {
			for (var i = 0; i < displayRequirementTypeObject.options.length; i++) {
				if (displayRequirementTypeObject.options[i].selected) {
					displayRequirementType += displayRequirementTypeObject.options[i].value + ','; 
				}
			}
		}
	
	
	
		/*

		releaseTrendsFilterDivObject = document.getElementById("releaseTrendsFilterDiv");
		if (releaseTrendsFilterDivObject != null ){
			releaseTrendsFilterDivObject.style.display = "block";
		}
		
		url = "/GloreeJava2/jsp/Report/ReleaseDashboard/displayReleaseTrends2.jsp?releaseId=" + releaseId;
		url += "&displayRequirementType=" + displayRequirementType;
		
		document.getElementById("contentCenterE").style.display = "block";
		document.getElementById("contentCenterE").innerHTML= 
			"<iframe src='"+ url +"' width='1200', height='12000' ></iframe>"
		*/
		
	}
	
	
function displayReleaseTrends(){
		

		var releaseIdObject = document.getElementById("releaseId");
		var releaseId = releaseIdObject[releaseIdObject.selectedIndex].value
		if (releaseIdObject.selectedIndex == 0) {
			alert ("Please Select a Release to run reports for.");
			releaseIdObject.focus();
			releaseIdObject.style.backgroundColor="#FFCC99";
			return(0);
		}
					
		
		// set the other content centers to empty.
		
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";	
			
			
		
		
		/*
		// Now display the Report.
		url = "/GloreeJava2/jsp/Report/ReleaseDashboard/displayReleaseMetricsDataTable.jsp?releaseId=" + releaseId;
		fillOPCenterGeneric(url, "contentCenterB");
		*/
			
		
		var displayRequirementType  = "";
		
		var displayRequirementTypeObject = document.getElementById("displayRequirementType");
		if (displayRequirementTypeObject != null) {
			for (var i = 0; i < displayRequirementTypeObject.options.length; i++) {
				if (displayRequirementTypeObject.options[i].selected) {
					displayRequirementType += displayRequirementTypeObject.options[i].value + ','; 
				}
			}
		}
	
	
	
		releaseTrendsFilterDivObject = document.getElementById("releaseTrendsFilterDiv");
		if (releaseTrendsFilterDivObject != null ){
			releaseTrendsFilterDivObject.style.display = "block";
		}
		
		
		// get fromDate and toDate.
	
		var fromDate = document.getElementById("fromDate");
		if ((fromDate != null) && (fromDate.value != "")) {
			if (isValidDate(fromDate.value)==false){
				fromDate.focus();
				fromDate.style.backgroundColor="#FFCC99";
				return(0);
			}
		}
		
		var toDate = document.getElementById("toDate");
		if ((toDate != null) && (toDate.value != "")) {
			if (isValidDate(toDate.value)==false){
				toDate.focus();
				toDate.style.backgroundColor="#FFCC99";
				return(0);
			}
		}
		
		
		url = "/GloreeJava2/jsp/Report/ReleaseDashboard/displayReleaseTrends2.jsp?releaseId=" + releaseId;
		url += "&displayRequirementType=" + displayRequirementType;
		url += "&fromDate=" + fromDate.value ;
		url += "&toDate=" + toDate.value ;
		
		document.getElementById("contentCenterE").style.display = "block";
		document.getElementById("contentCenterE").innerHTML= 
			"<iframe src='"+ url +"' width='1200', height='12000' ></iframe>"
		
		
	}
	function displayReleaseMetricsWithChart(releaseId){
		// Now display the Report.
		url = "/GloreeJava2/jsp/Report/ReleaseDashboard/displayReleaseMetricsDataTableWithChart.jsp?releaseId=" + releaseId;
		fillOPCenterGeneric(url, "displayReleaesMetricsDiv" + releaseId);
	}
	

	function refreshReleaseMetricsDataTable(releaseId){
					
		
		// set the other content centers to empty.
		var levelOfDetailObject = document.getElementById("levelOfDetail" + releaseId);
		var levelOfDetailValue = levelOfDetailObject.options[levelOfDetailObject.selectedIndex].value;
		
		var focusOnString = "";
		focusOnObject = document.getElementById("focusOn" + releaseId);
		if (focusOnObject != null){
			for (var i = 0; i < focusOnObject.options.length; i++) {
				if (focusOnObject.options[i].selected) {
					focusOnString += ":#:" +  focusOnObject.options[i].value ;
				}
			}
		}
		
			
			
		// Now display the Report.
		url = "/GloreeJava2/jsp/Report/ReleaseDashboard/displayReleaseMetricsDataTable.jsp?releaseId=" + releaseId;
		url += "&levelOfDetail=" + levelOfDetailValue;
		url += "&focusOn=" + encodeURIComponent(focusOnString);
		
		fillOPCenterGeneric(url, "contentCenterB");
	}


	function refreshReleaseMetricsDataTableWithChart(releaseId, reCalculateMetrics){
					
		
		// set the other content centers to empty.
		var levelOfDetailObject = document.getElementById("levelOfDetail" + releaseId);
		var levelOfDetailValue = levelOfDetailObject.options[levelOfDetailObject.selectedIndex].value;
		
			

		// Now display the Report.
		url = "/GloreeJava2/jsp/Report/ReleaseDashboard/displayReleaseMetricsDataTableWithChart.jsp?releaseId=" + releaseId;
		url += "&reCalculateMetrics=" + reCalculateMetrics;
		url += "&levelOfDetail=" + levelOfDetailValue;
		
		fillOPCenterGeneric(url, "displayReleaesMetricsDiv" + releaseId);
	}	
		
	function displayReleaseRequirements(releaseId, requirementTypeShortName, dataType, showReturn){
		// Now fill content Right with all the requirements inside this.
		url="/GloreeJava2/jsp/Report/ReleaseDashboard/displayReleaseRequirements.jsp?releaseId="  + releaseId;
		url += "&requirementTypeShortName=" + requirementTypeShortName;
		url += "&dataType=" + dataType;
		url += "&showReturn=" + showReturn;
		url += "&bustcache=" + new Date().getTime() ;
		
		
		// set the other content centers to empty.
		document.getElementById("contentCenterF").style.display = "none";
		fillOPCenterGeneric(url, "contentCenterG");
		
	}


	function displayReleaseRequirementsInFolder(releaseId, folderId, requirementTypeShortName, dataType, showReturn){
		// Now fill content Right with all the requirements inside this.
		url="/GloreeJava2/jsp/Report/ReleaseDashboard/displayReleaseRequirementsInFolder.jsp?releaseId="  + releaseId;
		url += "&folderId=" + folderId;
		url += "&requirementTypeShortName=" + requirementTypeShortName;
		url += "&dataType=" + dataType;
		url += "&showReturn=" + showReturn;
		url += "&bustcache=" + new Date().getTime() ;
		
		// set the other content centers to empty.
		document.getElementById("contentCenterF").style.display = "none";
		fillOPCenterGeneric(url, "contentCenterG");
				
	}	
	
	
	// used when someone clicks on the general reports in the release dashboard page.
	function displayReleaseRequirementsOfAllReqTypes(dataType){

		var releaseIdObject = document.getElementById("releaseId");
		var releaseId = releaseIdObject[releaseIdObject.selectedIndex].value
		if (releaseIdObject.selectedIndex == 0) {
			alert ("Please Select a Release to run reports for.");
			releaseIdObject.focus();
			releaseIdObject.style.backgroundColor="#FFCC99";
			return(0);
		}
					
		
		// set the other content centers to empty.
		
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
			
			
		// Now display the Report.
		url="/GloreeJava2/jsp/Report/ReleaseDashboard/displayReleaseRequirements.jsp?releaseId="  + releaseId;
		url += "&requirementTypeShortName=" + "all";
		url += "&dataType=" + dataType;
		url += "&bustcache=" + new Date().getTime() ;
		
		// if the dataType is changedAfter, we need to pick up the cutOffDate and add it as a param.
		if (dataType == "changedAfter") {
			var cutOffDate = document.getElementById("cutOffDate").value;
			url += "&cutOffDate=" + cutOffDate;
		}
		
		// if the dataType is defectStatusGroup, we need to get the defectStatusGroup value and add it
		// as a param
		if (dataType == "defectStatusGroup") {
			var defectStatusGroupObject = document.getElementById("defectStatusGroup");
			var defectStatusGroup = defectStatusGroupObject[defectStatusGroupObject.selectedIndex].value
			url += "&defectStatusGroup=" + defectStatusGroup;
		}
		
		
		document.getElementById("contentCenterF").style.display = "block";
		document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenter").style.display = "block";
				document.getElementById("contentCenterF").innerHTML=xmlHttpOPCenterB.responseText;

				// now lets put focus on the start of this page.
				window.location.hash="TopOfDisplayRequirements";  
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}

	
	
	//////////////////////////////////////////////////////////////////////////
	// //
	// 	Baseline Metrics & Dashboards
	// //
	// ////////////////////////////////////////////////////////////////////////	
	// used to display all the release objects in a project for displaying trend data.
	function displayBaselineMetricsForm(){
		
		// set the other content centers to empty.
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		
		
		
		// lets remove focus from the folder in the explorer.
		// if the previous folderis not null, set it's background to gray.
		var prevFocusFolder = document.getElementById(prevFocusFolderId); 
		 if (prevFocusFolder != null){
			prevFocusFolder.style.backgroundColor = "#EBE4F2";
		 }
		 		

		 
		// Now fill content Right with all the requirements inside this.
		url="/GloreeJava2/jsp/Report/BaselineDashboard/displayBaselineDashboard.jsp?";
		fillOPCenterGeneric(url, "contentCenterA");
		
	}

	
	function displayBaselineMetrics2(reportType){

		var baselineIdObject = document.getElementById("rTBaselineId");
		var baselineId = baselineIdObject[baselineIdObject.selectedIndex].value
		if (baselineIdObject.selectedIndex == 0) {
			alert ("Please Select a Baseline to run reports for.");
			baselineIdObject.focus();
			baselineIdObject.style.backgroundColor="#FFCC99";
			return(0);
		}
					
		
		// set the other content centers to empty.
		
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";	
			
			
		// Now display the Report.
		url = "/GloreeJava2/jsp/Report/BaselineDashboard/displayBaselineMetricsDataTable.jsp?rTBaselineId=" + baselineId;
		fillOPCenterGeneric(url, "contentCenterB");
		
		url = "/GloreeJava2/jsp/Report/BaselineDashboard/displayBaselineMetrics.jsp?rTBaselineId=" + baselineId;
		document.getElementById("contentCenterE").style.display = "block";
		document.getElementById("contentCenterE").innerHTML= 
			"<iframe src='"+ url +"' width='800', height='400' ></iframe>"
		

	}

	// used when someone clicks on the 'change comparision ' reports of baseline..
	function displayBaselineRequirementsChangeComparision(){

		var baselineIdObject = document.getElementById("rTBaselineId");
		var baselineId = baselineIdObject[baselineIdObject.selectedIndex].value
		if (baselineIdObject.selectedIndex == 0) {
			//document.getElementById("baselineChangeComparisionLink").href = "";
			alert ("Please Select a Baseline to run reports for.");
			baselineIdObject.focus();
			baselineIdObject.style.backgroundColor="#FFCC99";
			return(0);
		}

		// set the other content centers to empty.
		
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
			
		// Now display the Report.
		url="/GloreeJava2/servlet/BaselineMetricsAction?rTBaselineId="  + baselineId;
		url += "&action=" + "changeComparisionReportVsCurrent";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("baselineChangeComparisionLink").href = url;
	}


	// used when someone clicks on the 'change comparision ' reports of baseline..
	function displayBaselineRequirementsChangeComparisionVsAnotherBaseline(){

		var baselineIdObject = document.getElementById("rTBaselineId");
		var baselineId = baselineIdObject[baselineIdObject.selectedIndex].value
		if (baselineIdObject.selectedIndex == 0) {
			//document.getElementById("baselineChangeComparisionLink").href = "";
			alert ("Please Select a Baseline to run reports for.");
			baselineIdObject.focus();
			baselineIdObject.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		var compareAgainstBaselineIdObject = document.getElementById("compareAgainstRTBaselineId");
		var compareAgainstBaselineId = compareAgainstBaselineIdObject[compareAgainstBaselineIdObject.selectedIndex].value
		if (compareAgainstBaselineIdObject.selectedIndex == 0) {
			//document.getElementById("baselineChangeComparisionLink").href = "";
			alert ("Please Select a Baseline to run reports for.");
			compareAgainstBaselineIdObject.focus();
			compareAgainstBaselineIdObject.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		
		

		// set the other content centers to empty.
		
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
			
		// Now display the Report.
		url="/GloreeJava2/servlet/BaselineMetricsAction?rTBaselineId="  + baselineId;
		url += "&compareAgainstRTBaselineId="  + compareAgainstBaselineId;
		url += "&action=" + "changeComparisionReportVsAnotherBaseline";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("baselineChangeComparisionVsAnotherBaselineLink").href = url;
	}

	
	// used when someone clicks on the general reports in the baseline dashboard page.
	function displayBaselineRequirementsOfAllReqTypes(dataType){

		var baselineIdObject = document.getElementById("rTBaselineId");
		var baselineId = baselineIdObject[baselineIdObject.selectedIndex].value
		if (baselineIdObject.selectedIndex == 0) {
			alert ("Please Select a Baseline to run reports for.");
			baselineIdObject.focus();
			baselineIdObject.style.backgroundColor="#FFCC99";
			return(0);
		}
					
		
		// set the other content centers to empty.
		
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
			
			
		// Now display the Report.
		url="/GloreeJava2/jsp/Report/BaselineDashboard/displayBaselineRequirements.jsp?rTBaselineId="  + baselineId;
		url += "&requirementTypeShortName=" + "all";
		url += "&dataType=" + dataType;
		url += "&bustcache=" + new Date().getTime() ;
		
		// if the dataType is changedAfter, we need to pick up the cutOffDate and add it as a param.
		if (dataType == "changedAfter") {
			var cutOffDate = document.getElementById("cutOffDate").value;
			url += "&cutOffDate=" + cutOffDate;
		}
		
		// if the dataType is defectStatusGroup, we need to get the defectStatusGroup value and add it
		// as a param
		if (dataType == "defectStatusGroup") {
			var defectStatusGroupObject = document.getElementById("defectStatusGroup");
			var defectStatusGroup = defectStatusGroupObject[defectStatusGroupObject.selectedIndex].value
			url += "&defectStatusGroup=" + defectStatusGroup;
		}
		
		document.getElementById("contentCenterF").style.display = "block";
		document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenter").style.display = "block";
				document.getElementById("contentCenterF").innerHTML=xmlHttpOPCenterB.responseText;

				// now lets put focus on the start of this page.

				window.location.hash="TopOfDisplayRequirements";  
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
	}

	
	function displayBaselineReport(){

		var baselineIdObject = document.getElementById("rTBaselineId");
		var baselineId = baselineIdObject[baselineIdObject.selectedIndex].value
		if (baselineIdObject.selectedIndex == 0) {
			alert ("Please Select a Baseline to run reports for.");
			baselineIdObject.focus();
			baselineIdObject.style.backgroundColor="#FFCC99";
			return(0);
		}
					
		
		// set the other content centers to empty.
		
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
			
			
		// Now display the Report.
		url="/GloreeJava2/jsp/Report/BaselineDashboard/displayBaselineReport.jsp?rTBaselineId="  + baselineId;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "contentCenterF");
	}
	
	function displayBaselineRequirements(baselineId, requirementTypeShortName, dataType){
		// set the other content centers to empty.
		document.getElementById("contentCenterF").style.display = "block";
		document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		// Now fill content Right with all the requirements inside this.
		url="/GloreeJava2/jsp/Report/BaselineDashboard/displayBaselineRequirements.jsp?rTBaselineId="  + baselineId;
		url += "&requirementTypeShortName=" + requirementTypeShortName;
		url += "&dataType=" + dataType;
		url += "&bustcache=" + new Date().getTime() ;
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenter").style.display = "block";
				document.getElementById("contentCenterF").innerHTML=xmlHttpOPCenterB.responseText;

				// now lets put focus on the start of this page.

				window.location.hash="TopOfDisplayRequirements";  
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
	}


	// called when someone clicks on 'Bulk Edit' these requirmetns link. in the Baseline Dashboard
	// report.
	function displayBaselineReport2(folderId, baselineReportType, rTBaselineId){
		
				
			// Now display the Report.
			var reportDefinition = "";
			var reportType = "";
			
			if (baselineReportType == "reportAndBulkEditAllRequirementsInBaseline"){
	    		reportDefinition = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
	    		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
	    		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
	    		"includeSubFoldersSearch:--:yes";
	    		reportDefinition +=  ":###:" + "inRTBaselineSearch" + ":--:" + rTBaselineId ;
	    		
	    		reportType = "list";
			}

			if (baselineReportType == "traceTreeForAllRequirementsInBaseline"){
				
	    		reportDefinition = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
	    		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
	    		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
	    		"includeSubFoldersSearch:--:yes";
	    		
	    		reportDefinition +=  ":###:" + "inRTBaselineSearch" + ":--:" + rTBaselineId ;
	    		
	    		// this is the only traceTree report. so lets set the param.
	    		reportType = "traceTree";
			}

			
			if (baselineReportType == "completedRequirementsInBaseline"){
	    		reportDefinition = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
	    		"completedSearch:--:completedOnly:###:incompleteSearch:--:all:###:" +
	    		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
	    		"includeSubFoldersSearch:--:yes";
	    		reportDefinition +=  ":###:" + "inRTBaselineSearch" + ":--:" + rTBaselineId ;
	    		
	    		reportType = "list";
			}
			
			if (baselineReportType == "inCompleteRequirementsInBaseline"){
	    		reportDefinition = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
	    		"completedSearch:--:all:###:incompleteSearch:--:incompleteOnly:###:" +
	    		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
	    		"includeSubFoldersSearch:--:yes";
	    		reportDefinition +=  ":###:" + "inRTBaselineSearch" + ":--:" + rTBaselineId ;
	    		
	    		reportType = "list";
			}			

			if (baselineReportType == "orphanRequirementsInBaseline"){
	    		reportDefinition = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:orphanOnly:###:" +
	    		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
	    		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
	    		"includeSubFoldersSearch:--:yes";
	    		reportDefinition +=  ":###:" + "inRTBaselineSearch" + ":--:" + rTBaselineId ;
	    		
	    		reportType = "list";
			}

			if (baselineReportType == "danglingRequirementsInBaseline"){
	    		reportDefinition = "active:--:active:###:danglingSearch:--:danglingOnly:###:orphanSearch:--:all:###:" +
	    		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
	    		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
	    		"includeSubFoldersSearch:--:yes";
	    		reportDefinition +=  ":###:" + "inRTBaselineSearch" + ":--:" + rTBaselineId ;
	    		
	    		reportType = "list";
			}
			
			if (baselineReportType == "suspectUpStreamRequirementsInBaseline"){
	    		reportDefinition = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
	    		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
	    		"suspectUpStreamSearch:--:suspectUpStreamOnly:###:suspectDownStreamSearch:--:all:###:"  +
	    		"includeSubFoldersSearch:--:yes";
	    		reportDefinition +=  ":###:" + "inRTBaselineSearch" + ":--:" + rTBaselineId ;
	    		
	    		reportType = "list";
			}
			
			if (baselineReportType == "suspectDownStreamRequirementsInBaseline"){
	    		reportDefinition = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
	    		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
	    		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:suspectDownStreamOnly:###:"  +
	    		"includeSubFoldersSearch:--:yes";
	    		reportDefinition +=  ":###:" + "inRTBaselineSearch" + ":--:" + rTBaselineId ;
	    		
	    		reportType = "list";
			}			

			if (baselineReportType == "draftRequirementsInBaseline"){
	    		reportDefinition = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
	    		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
	    		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
	    		"includeSubFoldersSearch:--:yes";
	    		
	    		reportDefinition += ":###:statusSearch:--:Draft";
	    		reportDefinition +=  ":###:" + "inRTBaselineSearch" + ":--:" + rTBaselineId ;
	    		
	    		reportType = "list";
			}		
			
			if (baselineReportType == "pendingRequirementsInBaseline"){
	    		reportDefinition = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
	    		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
	    		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
	    		"includeSubFoldersSearch:--:yes";
	    		
	    		reportDefinition += ":###:statusSearch:--:In Approval WorkFlow";
	    		reportDefinition +=  ":###:" + "inRTBaselineSearch" + ":--:" + rTBaselineId ;
	    		
	    		reportType = "list";
			}		
			
			if (baselineReportType == "approvedRequirementsInBaseline"){
	    		reportDefinition = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
	    		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
	    		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
	    		"includeSubFoldersSearch:--:yes";
	    		
	    		reportDefinition += ":###:statusSearch:--:Approved";
	    		reportDefinition +=  ":###:" + "inRTBaselineSearch" + ":--:" + rTBaselineId ;
	    		
	    		reportType = "list";
			}		
			
			if (baselineReportType == "rejectedRequirementsInBaseline"){
	    		reportDefinition = "active:--:active:###:danglingSearch:--:all:###:orphanSearch:--:all:###:" +
	    		"completedSearch:--:all:###:incompleteSearch:--:all:###:" +
	    		"suspectUpStreamSearch:--:all:###:suspectDownStreamSearch:--:all:###:"  +
	    		"includeSubFoldersSearch:--:yes";
	    		
	    		reportDefinition += ":###:statusSearch:--:Rejected";
	    		reportDefinition +=  ":###:" + "inRTBaselineSearch" + ":--:" + rTBaselineId ;
	    		
	    		reportType = "list";
			}		
						
						
			// set the other content centers to empty.
			document.getElementById("contentCenterB").style.display = "none";
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";

			// lets put focus on the root folder of this req type
			displayFolderInExplorer(folderId) ;
			
			// lets display the tab sheet for root level folder of this req type
			// once the top level folders get displayed, we want to put focus
			// on 'Trace Tree Report' tab. So, we will have to go synchronous here.
			// displayFolderContentCenterA(folderId) ;
			var url="/GloreeJava2/jsp/Folder/displayRealFolder.jsp?folderId="+ folderId;
			xmlHttpOPCenterA =GetXmlHttpObject();
			xmlHttpOPCenterA.onreadystatechange=function() {
				if(xmlHttpOPCenterA.readyState==4){
					document.getElementById("contentCenterA").style.display = 'block';
					document.getElementById("contentCenterA").innerHTML=xmlHttpOPCenterA.responseText;
					// now that the tabs are available, lets locate them and set focus.
					var focusTab = "url(/GloreeJava2/images/focusTab.png)";
					var menuCreateListReport = document.getElementById("menuCreateListReport");
					if (menuCreateListReport != null) {
						menuCreateListReport.style.backgroundImage = focusTab;
					}
										
				}
			}
			xmlHttpOPCenterA.open("GET",url,true);
			xmlHttpOPCenterA.send(null);
			
			
			
			
			// lets display the contents of the root level folde in content right.
			//displayFolderContentRight(folderId);
			
			// ContentCenter E and F are the targets. E will hold the filter
			// F will hold the actual data. So lets put the working gif in both.
			document.getElementById("contentCenterE").style.display = "block";
			document.getElementById("contentCenterE").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			document.getElementById("contentCenterF").style.display = "block";
			document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			
			// Now display the Report.
			// since we want to fill the report definition into the filter boxes
			// and execute the report, we will make an synch call.
			var url = "";
			url = "/GloreeJava2/servlet/ReportAction?folderId=" + encodeURIComponent(folderId);
			url += "&action=" + encodeURIComponent("displayDynamicReport");
			url += "&reportDefinition=" + encodeURIComponent(reportDefinition);
			url += "&reportType=" + encodeURIComponent(reportType);
			url += "&bustcache=" + new Date().getTime() ;
			
			xmlHttpOPCenterE =GetXmlHttpObject();
			xmlHttpOPCenterE.onreadystatechange=function() {
				if(xmlHttpOPCenterE.readyState==4){
					document.getElementById("contentCenterE").style.display = 'block';
					document.getElementById("contentCenterE").innerHTML=xmlHttpOPCenterE.responseText;
					// now that we have filled up the filters, lets call the run
					// report method.
					reportAction(folderId,'runReport', reportType);
				}
			}
			xmlHttpOPCenterE.open("GET",url,true);
			xmlHttpOPCenterE.send(null);
			
			
			
			
			
	}
	
	//////////////////////////////////////////////////////////
	//
	//
	//		User Metrics / Dashboard 
	//
	//
	///////////////////////////////////////////////////////////
	
	
	function displayUserMetricsForm(){
		// set the other content centers to empty.
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		
		
		// lets remove focus from the folder in the explorer.
		// if the previous folderis not null, set it's background to gray.
		var prevFocusFolder = document.getElementById(prevFocusFolderId); 
		 if (prevFocusFolder != null){
			prevFocusFolder.style.backgroundColor = "#EBE4F2";
		 }
		 		
		
		// Now fill content Center A with all the dashboard reports.
		url="/GloreeJava2/jsp/Report/UserDashboard/displayUserDashboard.jsp?";
		fillOPCenterGeneric(url, "contentCenterE");
		
		
		
		
		url = "/GloreeJava2/jsp/Report/UserDashboard/displayUserMetricsDataTable.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		xmlHttpOPCenterB =GetXmlHttpObject();		
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById('contentCenterA').style.display = 'block'; 
				document.getElementById('contentCenterA').innerHTML=xmlHttpOPCenterB.responseText;

				// Once ContentCenterB is loaded
				// lets scroll to the top of the page any time we display the requirement.

				window.location.hash="TopOfMetrics";
				// we need to put focus somewhere , so we have decided to put 
				// focus on the searchstring box.
				// this takes care of the situation where IE tends to lose the focus object.
				document.getElementById("googleSearchString").focus();
				
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
	}
	
	



	// used when someone clicks on the general reports in the release dashboard page.
	function displayUserRequirementsOfAllReqTypes(dataType){

		// lets collapse the dashboards.
		var reportDataDiv = document.getElementById("reportData");
		if (reportDataDiv != null ){
			reportDataDiv.style.display='none';
		}
		var displayUserMetricsDiv = document.getElementById("displayUserMetricsDiv");
		if (displayUserMetricsDiv != null ){
			displayUserMetricsDiv.style.display='none';
		}
		var menuExpandDiv = document.getElementById("menuExpandDiv");
		if (menuExpandDiv != null ){
			menuExpandDiv.style.display="block";
		}
		
		// set the other content centers to empty.
		
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
			
			
		// Now display the Report.
		url="/GloreeJava2/jsp/Report/UserDashboard/displayUserRequirements.jsp?";
		url += "&requirementTypeShortName=" + "all";
		url += "&dataType=" + dataType;
		url += "&bustcache=" + new Date().getTime() ;
		
		// if the dataType is changedAfter, we need to pick up the cutOffDate and add it as a param.
		if (dataType == "changedAfter") {
			var cutOffDate = document.getElementById("cutOffDate").value;
			url += "&cutOffDate=" + cutOffDate;
		}
		
		// if the dataType is defectStatusGroup, we need to get the defectStatusGroup value and add it
		// as a param
		if (dataType == "defectStatusGroup") {
			var defectStatusGroupObject = document.getElementById("defectStatusGroup");
			var defectStatusGroup = defectStatusGroupObject[defectStatusGroupObject.selectedIndex].value
			url += "&defectStatusGroup=" + defectStatusGroup;
		}
		
		document.getElementById("contentCenterF").style.display = "block";
		document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenter").style.display = "block";
				document.getElementById("contentCenterF").innerHTML=xmlHttpOPCenterB.responseText;

				// now lets put focus on the start of this page.
				window.location.hash="TopOfDisplayRequirements";  
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
		
	}

	
	function displayUserRequirements(requirementTypeShortName, dataType){
		
		// lets collapse the dashboards.
		var reportDataDiv = document.getElementById("reportData");
		if (reportDataDiv != null ){
			reportDataDiv.style.display='none';
		}
		var displayUserMetricsDiv = document.getElementById("displayUserMetricsDiv");
		if (displayUserMetricsDiv != null ){
			displayUserMetricsDiv.style.display='none';
		}
		var menuExpandDiv = document.getElementById("menuExpandDiv");
		if (menuExpandDiv != null ){
			menuExpandDiv.style.display="block";
		}
		// set the other content centers to empty.
		document.getElementById("contentCenterF").style.display = "block";
		document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		// Now fill content Right with all the requirements inside this.
		url="/GloreeJava2/jsp/Report/UserDashboard/displayUserRequirements.jsp?";
		url += "&requirementTypeShortName=" + requirementTypeShortName;
		url += "&dataType=" + dataType;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenter").style.display = "block";
				document.getElementById("contentCenterF").innerHTML=xmlHttpOPCenterB.responseText;
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
		
	}	
	// called when someone clicks on a Report that was saved in the past, from the userDashboard
	// this first naviates to the correct folder and then opens up the report.
	function navigateToAndDisplayExistingReport(folderId, reportId, reportType){
		// lets put the focus on the folder on the right.
		if (tree != null){
			tree.expandAll();
		}
		displayFolderInExplorer(folderId) ;
		
		// display the folder contents in ContentRight
		//displayFolderContentRight(folderId);
		
		// lets display the folder menu in contentCenterA.
		var url="/GloreeJava2/jsp/Folder/displayRealFolder.jsp?folderId="+ folderId;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterA").style.display = "block";
				document.getElementById("contentCenterA").innerHTML=xmlHttpOPCenterB.responseText;
				// now , lets call the displayExistingReport function.
				displayExistingReport(folderId, reportId, reportType);
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
	}

	function deleteReportFromUserDashboard(folderId, reportId){
		// lets delete the folder.

		var url = "/GloreeJava2/servlet/ReportAction?folderId=" + encodeURIComponent(folderId);
		url += "&action=" + encodeURIComponent("deleteReport");
		url += "&reportId=" + encodeURIComponent(reportId);
		url += "&bustcache=" + new Date().getTime() ;

		
		// after the report is deleted, we want to refresh the User Dashboard page
		xmlHttpOPCenterE =GetXmlHttpObject();
			
		xmlHttpOPCenterE.onreadystatechange=function() {
			if(xmlHttpOPCenterE.readyState==4){
				// now that the report is deleted, lets display the user dashboard.
				// Now fill content Center A with all the dashboard reports.
				url="/GloreeJava2/jsp/Report/UserDashboard/displayUserDashboard.jsp?";
				fillOPCenterGeneric(url, "contentCenterA");
			}
		}
		xmlHttpOPCenterE.open("GET",url,true);
		xmlHttpOPCenterE.send(null);		
	}
	
	//////////////////////////////////////////////////////////
	//
	//
	//		Project Metrics / Dashboard 
	//
	//
	///////////////////////////////////////////////////////////
	function displayProjectMetricsForm(){
		
		// set the other content centers to empty.
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		
		
		
		// lets remove focus from the folder in the explorer.
		// if the previous folderis not null, set it's background to gray.
		var prevFocusFolder = document.getElementById(prevFocusFolderId); 
		 if (prevFocusFolder != null){
			prevFocusFolder.style.backgroundColor = "#EBE4F2";
		 }
		 		
		
		// Now fill content Right with all the dashboard reports.
		url="/GloreeJava2/jsp/Report/ProjectDashboard/displayProjectDashboard.jsp?";
		fillOPCenterGeneric(url, "contentCenterA");
		
		//url = "/GloreeJava2/jsp/Report/ProjectDashboard/displayProjectMetricsDataTable.jsp?";
		//fillOPCenterGeneric(url, "contentCenterB");
		
	}
	
	function displayProjectMetricsDataTable(){
		
		url = "/GloreeJava2/jsp/Report/ProjectDashboard/displayProjectMetricsDataTable.jsp?";
		fillOPCenterGeneric(url, "projectMetricsDiv");
		
	}
	
	
	
	function reCalculateProjectMetrics(){
		
		document.getElementById("reCalculateProjectMetricsDiv").style.display="block";
		document.getElementById("reCalculateProjectMetricsDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...<br>Please note this may take a few minutes.";
		
		url = "/GloreeJava2/jsp/Report/ProjectDashboard/displayProjectMetricsDataTable.jsp?reCalculateMetrics=yes";
		fillOPCenterGeneric(url, "projectMetricsDiv");
		
	}
		
	function displayProjectMetrics(reportType){

		// set the other content centers to empty.
		try{
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";	
		}
		catch(err){
		}
			
		
		
		var displayRequirementType  = "";
		
		var displayRequirementTypeObject = document.getElementById("displayRequirementType");
		if (displayRequirementTypeObject != null) {
			for (var i = 0; i < displayRequirementTypeObject.options.length; i++) {
				if (displayRequirementTypeObject.options[i].selected) {
					displayRequirementType += displayRequirementTypeObject.options[i].value + ','; 
				}
			}
		}
		
		
		// get fromDate and toDate.
		var fromDate = document.getElementById("fromDate");
		if ((fromDate != null) && (fromDate.value != "")) {
			if (isValidDate(fromDate.value)==false){
				fromDate.focus();
				fromDate.style.backgroundColor="#FFCC99";
				return(0);
			}
		}
		
		var toDate = document.getElementById("toDate");
		if ((toDate != null) && (toDate.value != "")) {
			if (isValidDate(toDate.value)==false){
				toDate.focus();
				toDate.style.backgroundColor="#FFCC99";
				return(0);
			}
		}
		url = "/GloreeJava2/jsp/Report/ProjectDashboard/displayProjectTrend2.jsp?";
		url += "displayRequirementType=" + displayRequirementType;
		url += "&fromDate=" + fromDate.value ;
		url += "&toDate=" + toDate.value ;
		
		//fillOPCenterGeneric(url, "projectMetricsDiv");
		
		document.getElementById("projectTrendsDiv").style.display = "block";
		document.getElementById("projectTrendsDiv").innerHTML= 
			"<iframe src='"+ url +"' width='1100', height='12000' ></iframe>"
			
			
			
	}
	
	function displayProjectTrend2(){

		// set the other content centers to empty.
		
			
		url = "/GloreeJava2/jsp/Report/ProjectDashboard/displayProjectTrend2.jsp?";
		document.getElementById("projectTrend2Div").style.display = "block";
		document.getElementById("projectTrend2Div").innerHTML= 
			"<iframe src='"+ url +"' width='1200', height='12000' ></iframe>"
	}
	
	
	
	function refreshProjectMetrics(){
		
		
		// set the other content centers to empty.
		var levelOfDetailObject = document.getElementById("levelOfDetail");
		var levelOfDetailValue = levelOfDetailObject.options[levelOfDetailObject.selectedIndex].value;
		
		var focusOnString = "";
		focusOnObject = document.getElementById("focusOn");
		if (focusOnObject != null){
			for (var i = 0; i < focusOnObject.options.length; i++) {
				if (focusOnObject.options[i].selected) {
					focusOnString += ":#:" +  focusOnObject.options[i].value ;
				}
			}
		}
		
		// Now display the Report.
		var url = "/GloreeJava2/jsp/Report/ProjectDashboard/displayProjectMetricsDataTable.jsp?levelOfDetail=" + levelOfDetailValue;
		url += "&focusOn=" + encodeURIComponent(focusOnString);
		
		document.getElementById("refreshProjectDsahboardButton").disabled=true;
		document.getElementById("projectMetricsDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "projectMetricsDiv");

	}
	
	
	
	function displayProjectRequirements(requirementTypeShortName, dataType){
		// set the other content centers to empty.
		document.getElementById("contentCenterF").style.display = "block";
		document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		// Now fill content Right with all the requirements inside this.
		url="/GloreeJava2/jsp/Report/ProjectDashboard/displayProjectRequirements.jsp?";
		url += "&requirementTypeShortName=" + requirementTypeShortName;
		url += "&dataType=" + dataType;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenter").style.display = "block";
				document.getElementById("contentCenterF").innerHTML=xmlHttpOPCenterB.responseText;

				// now lets put focus on the start of this page.
				window.location.hash="TopOfDisplayRequirements";  
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
		
	}	
	
	function displayAgileScrumRequirements(requirementTypeShortName,sprintId, dataType){
		// set the other content centers to empty.
		document.getElementById("agileScrumRequirementsDiv").style.display = "block";
		document.getElementById("agileScrumRequirementsDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		// Now fill content Right with all the requirements inside this.
		url="/GloreeJava2/jsp/Report/AgileScrumDashboard/displayAgileScrumRequirements.jsp?";
		url += "&requirementTypeShortName=" + requirementTypeShortName;
		url += "&sprintId=" + sprintId;
		url += "&dataType=" + dataType;
		url += "&bustcache=" + new Date().getTime() ;
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("agileScrumRequirementsDiv").style.display = "block";
				document.getElementById("agileScrumRequirementsDiv").innerHTML=xmlHttpOPCenterB.responseText;

				// now lets put focus on the start of this page.
				window.location.hash="TopOfDisplayRequirements";  
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
		
	}		
	

	function addScrumNotes(sprintId){
		// set the other content centers to empty.
		document.getElementById("scrumNotesHistoryDiv").style.display = "block";
		document.getElementById("scrumNotesHistoryDiv").innerHTML= "<span class='normalText'> &nbsp;&nbsp;&nbsp;Working... </span>";
	
		// lets make sure some description is entered.
		var scrumNotes = document.getElementById("scrumNotes");
		if (scrumNotes.value.length == 0) {
			alert ("Please enter a Note");
			scrumNotes.focus();
			scrumNotes.style.backgroundColor="#FFCC99";
			return;
		}
		
		var url="/GloreeJava2/servlet/AgileScrumAction";
		
		var params; 
		params = "action=addScrumNotes&";
		params = params + "sprintId=" + sprintId + "&";
		params = params + "scrumNotes=" + encodeURIComponent(scrumNotes.value)+ "&";
		params = params + "bustcache=" + new Date().getTime() ;

		url = url + "?" + params;

		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				// once we get a response, lets enable the button
				scrumNotes.value = "";
				document.getElementById("createSprintLogButton").disabled = false;
				
				// After that , lets refresh the scrum notes log display
				var url2="/GloreeJava2/jsp/AgileScrum/ScrumNotes/displayAgileScrumNotesHistory.jsp?sprintId=" + sprintId ;
				url2 += "&bustcache=" + new Date().getTime() ;
				fillOPCenterGeneric(url2, "scrumNotesHistoryDiv");
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
		
	}		
		
	// used when someone clicks on the general reports in the release dashboard page.
	function displayProjectRequirementsOfAllReqTypes(dataType){

		
		// set the other content centers to empty.
		
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		
			
			
		// Now display the Report.
		url="/GloreeJava2/jsp/Report/ProjectDashboard/displayProjectRequirements.jsp?";
		url += "&requirementTypeShortName=" + "all";
		url += "&dataType=" + dataType;
		url += "&bustcache=" + new Date().getTime() ;
		
		// if the dataType is changedAfter, we need to pick up the cutOffDate and add it as a param.
		if (dataType == "changedAfter") {
			var cutOffDate = document.getElementById("cutOffDate").value;
			url += "&cutOffDate=" + cutOffDate;
		}

		// if the dataType is defectStatusGroup, we need to get the defectStatusGroup value and add it
		// as a param
		if (dataType == "defectStatusGroup") {
			var defectStatusGroupObject = document.getElementById("defectStatusGroup");
			var defectStatusGroup = defectStatusGroupObject[defectStatusGroupObject.selectedIndex].value
			url += "&defectStatusGroup=" + defectStatusGroup;
		}
		
		document.getElementById("contentCenterF").style.display = "block";
		document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenter").style.display = "block";
				document.getElementById("contentCenterF").innerHTML=xmlHttpOPCenterB.responseText;

				// now lets put focus on the start of this page.
				window.location.hash="TopOfDisplayRequirements";  
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
		
	}

	
	//////////////////////////////////////////////////////////
	//
	//
	//		Folder Metrics / Dashboard 
	//
	//
	///////////////////////////////////////////////////////////
	function displayFolderMetricsForm(folderId){
	
		
		// set the other content centers to empty.
		
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";	
		document.getElementById("contentCenterF").style.display = "none";	
	
		// Now fill content Right with all the dashboard reports.
		url="/GloreeJava2/jsp/Report/FolderDashboard/displayFolderDashboard.jsp?&folderId=" + folderId;
		url += "&bustcache=" + new Date().getTime() ;
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterB").style.display = "block";
				document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;

				// once this is loaded, look for a div called folderMetricsDataTableDiv and fill it 
				// with the Folder Metrics Data Table.
				url = "/GloreeJava2/jsp/Report/FolderDashboard/displayFolderMetricsDataTable.jsp?folderId=" + folderId;
				fillOPCenterGeneric(url, "folderMetricsDataTableDiv");
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);	
	}
	
	function displaySavedReportsAndTemplates(folderId){
		
		// set the other content centers to empty.
		
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";	
		document.getElementById("contentCenterF").style.display = "none";	
	
		// Now fill content Right with all the dashboard reports.
		url="/GloreeJava2/jsp/Report/FolderDashboard/displayFolderDashboard.jsp?displaySavedReportsAndTemplates=true&folderId=" + folderId;
		url += "&bustcache=" + new Date().getTime() ;
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenterB").style.display = "block";
				document.getElementById("contentCenterB").innerHTML=xmlHttpOPCenterB.responseText;
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
	}	

	
	function displayFolderMetrics2(reportType, folderId){
			
			
		// Now display the Report.

		
		
		url = "/GloreeJava2/jsp/Report/FolderDashboard/displayFolderMetrics.jsp?folderId=" + folderId;
		document.getElementById("contentCenterE").style.display = "block";
		document.getElementById("contentCenterE").innerHTML= 
			"<iframe src='"+ url +"' width='800', height='400' ></iframe>"
	
	}

	
	
	
	
	
	
	function displayFolderRequirements(folderId,requirementTypeShortName, dataType){
		// set the other content centers to empty.
		document.getElementById("contentCenterF").style.display = "block";
		document.getElementById("contentCenterF").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		// Now fill content Right with all the requirements inside this.
		url="/GloreeJava2/jsp/Report/FolderDashboard/displayFolderRequirements.jsp?";
		url += "&folderId=" + folderId;
		url += "&requirementTypeShortName=" + requirementTypeShortName;
		url += "&dataType=" + dataType;
		url += "&bustcache=" + new Date().getTime() ;
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("contentCenter").style.display = "block";
				document.getElementById("contentCenterF").innerHTML=xmlHttpOPCenterB.responseText;

				// now lets put focus on the start of this page.
				window.location.hash="TopOfDisplayRequirements";  
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
				
	}	
	
	
	/////////////////////////////////////////////////
	//   These are myTasksForAProject functions
	////////////////////////////////////////////////


	function displayMyTasksForAProjectInWizard(){
		// set the other content centers to empty.
		 		
		
		// Now fill content Center A with all the dashboard reports.
		url="/GloreeJava2/jsp/UserDashboard/myTasksForAProject.jsp?"; 
		url += "&bustcache=" + new Date().getTime() ;
		


		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				

				document.getElementById("displayWizardMyTasksDiv").style.display = "block";
				document.getElementById("displayWizardMyTasksDiv").innerHTML=xmlHttpOPCenterB.responseText;


				fillMyTasksForAProject();

			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);	
	}
	
	


	
	function displayMyTasksForAProject(){
		displayMyTasksForAProjectInWizard();
		document.getElementById("iWantToDiv").style.display="none";
		document.getElementById("myTasksDiv").style.display="block";
		document.getElementById("commentsDiv").style.display="none";
		document.getElementById("changesDiv").style.display="none";
		showMyStatusForAProject();
		/*
		// set the other content centers to empty.
		document.getElementById("contentCenterA").style.display = "none";
		document.getElementById("contentCenterB").style.display = "none";
		document.getElementById("contentCenterC").style.display = "none";
		document.getElementById("contentCenterD").style.display = "none";
		document.getElementById("contentCenterE").style.display = "none";
		document.getElementById("contentCenterF").style.display = "none";
		
		
		// lets remove focus from the folder in the explorer.
		// if the previous folderis not null, set it's background to gray.
		var prevFocusFolder = document.getElementById(prevFocusFolderId); 
		 if (prevFocusFolder != null){
			prevFocusFolder.style.backgroundColor = "#EBE4F2";
		 }
		 		
		
		// Now fill content Center A with all the dashboard reports.
		url="/GloreeJava2/jsp/UserDashboard/myTasksForAProject.jsp?"; 
		url += "&bustcache=" + new Date().getTime() ;
		


		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				

				document.getElementById("contentCenterF").style.display = "block";
				document.getElementById("contentCenterF").innerHTML=xmlHttpOPCenterB.responseText;


				fillMyTasksForAProject();

			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);	
		*/
	}
	
	

	function fillMyTasksForAProject(){

		fillMyPendingApprovalForAProject();

		fillMyIncompleteReqsForAProject();

		fillMyTestFailedReqsForAProject();

		fillMyTestPendingReqsForAProject();

		fillRecentlyChangedReqsForAProject();

		fillRecentlyCommentedReqsForAProject();
		fillMyDanglingForAProject();
		fillMyOrphanForAProject();
		
		fillMyReqsPendingApprovalForAProject();
		fillMyReqsRejectedForAProject();
		
		fillMySuspectUpForAProject();
		fillMySuspectDownForAProject();

	}
	

	function showMyStatusForAProject(){

		fillMyPendingApprovalForAProject();

		fillMyIncompleteReqsForAProject();

		fillMyTestFailedReqsForAProject();

		fillMyTestPendingReqsForAProject();

		fillMyDanglingForAProject();
		fillMyOrphanForAProject();
		
		fillMyReqsPendingApprovalForAProject();
		fillMyReqsRejectedForAProject();
		
		fillMySuspectUpForAProject();
		fillMySuspectDownForAProject();

	}
	
	
	function fillMyPendingApprovalForAProject(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myPendingApprovalForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("pendingMyApprovalDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "pendingMyApprovalDiv");		
	}	
	
	
	function myPendingApprovalDetailsForAProject(targetDiv){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myPendingApprovalDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		if (targetDiv != null){
			document.getElementById("contentCenterA").style.display = "none";
			document.getElementById("contentCenterB").style.display = "none";
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";
			
		}
		else {
			targetDiv ="myTasksDiv"; 
		}
		document.getElementById(targetDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, targetDiv);
	}	
	
	
		
	function myPendingApprovalDetailsForAProject_directDisplay(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myPendingApprovalDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("contentCenterA").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "contentCenterA");		
	}	

	
	
	
	
	function fillMyIncompleteReqsForAProject(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myIncompleteReqsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("incompleteDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "incompleteDiv");
	}
	function myIncompleteReqsDetailsForAProject(targetDiv){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myIncompleteReqsDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		if (targetDiv != null){
			document.getElementById("contentCenterA").style.display = "none";
			document.getElementById("contentCenterB").style.display = "none";
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";
			
		}
		else {
			targetDiv ="myTasksDiv"; 
		}
		document.getElementById(targetDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, targetDiv);	
	}	
	function myIncompleteReqsDetailsForAProject_directDisplay(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myIncompleteReqsDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("contentCenterA").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "contentCenterA");	
	}	
	
	

	function fillMyTestFailedReqsForAProject(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myTestFailedReqsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("testFailedDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "testFailedDiv");	
	}		
	function myTestFailedReqsDetailsForAProject(targetDiv){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myTestFailedReqsDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		if (targetDiv != null){
			document.getElementById("contentCenterA").style.display = "none";
			document.getElementById("contentCenterB").style.display = "none";
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";
			
		}
		else {
			targetDiv ="myTasksDiv"; 
		}
		document.getElementById(targetDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, targetDiv);
	}
	function myTestFailedReqsDetailsForAProject_directDisplay(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myTestFailedReqsDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("contentCenterA").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "contentCenterA");
	}
	
	
	
	function fillMyTestPendingReqsForAProject(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myTestPendingReqsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("testPendingDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "testPendingDiv");	
	}	
	function myTestPendingReqsDetailsForAProject(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myTestPendingReqsDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("myTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myTasksDiv");	
	}
	function myTestPendingReqsDetailsForAProject_directDeisplay(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myTestPendingReqsDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("contentCenterA").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "contentCenterA");	
	}
	
	
	

	
	function fillRecentlyChangedReqsForAProject(){
		var changedSince = document.getElementById("changedSince").value;
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/recentlyChangedReqsForAProject.jsp?";
		url += "&changedSince=" + changedSince ;
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("recentlyChangedReqsDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "recentlyChangedReqsDiv");
		
	}	
	

	function fillRecentlyChangedReqsForAFolder(folderId){
		var changedSince = 7;
		try  {
			changedSince = document.getElementById("changedSince").value;
			
	  	}
		catch (e)  {}
		
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/recentlyChangedReqsForAProject.jsp?";
		url += "&folderId=" + folderId;
		url += "&changedSince=" + changedSince ;
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("recentlyChangedReqsDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "recentlyChangedReqsDiv");
		
	}	
	
	function findItNowForAProject(searchType){

		// lets ensure that the search and project is not empty. 
		var searchString = document.getElementById("searchString");

		if (searchString.value.length == 0) {
			alert ("Please enter a Search String ");
			searchString.focus();
			searchString.style.backgroundColor="#FFCC99";
			return;
		}
			
		var targetRequirementTypeId = 0;
		var targetRequirementTypeObject = document.getElementById("targetRequirementType");
		if (targetRequirementTypeObject != null) {
			var targetRequirementTypeId = targetRequirementTypeObject.options[targetRequirementTypeObject.selectedIndex].value;
		}

		var targetFolderId = 0;
		var targetFolderObject = document.getElementById("targetFolder");
		if (targetFolderObject != null) {
			var targetFolderId = targetFolderObject.options[targetFolderObject.selectedIndex].value;
		}

			
		// Now display the Report.
		if (searchType == "current"){
			var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/findItNowForAProject.jsp?";		
			url += "&searchString=" + encodeURIComponent(searchString.value);
			url += "&targetRequirementTypeId=" + targetRequirementTypeId;
			url += "&targetFolderId=" + targetFolderId;
			url += "&searchType=" + searchType;
			url += "&bustcache=" + new Date().getTime() ;
			document.getElementById("myTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
			fillOPCenterGeneric(url, "myTasksDiv");
		}
		
		
		if (searchType == "historical"){
			var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/findItNowForAProjectHistorical.jsp?";		
			url += "&searchString=" + encodeURIComponent(searchString.value);
			url += "&targetRequirementTypeId=" + targetRequirementTypeId;
			url += "&targetFolderId=" + targetFolderId;
			url += "&searchType=" + searchType;
			url += "&bustcache=" + new Date().getTime() ;
			document.getElementById("myTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
			fillOPCenterGeneric(url, "myTasksDiv");
		}
		
		
	}
	
	function saveSearchReport(){

		// lets ensure that the search and project is not empty. 
		var searchString = document.getElementById("searchString");

		if (searchString.value.length == 0) {
			alert ("Please enter a Search String ");
			searchString.focus();
			searchString.style.backgroundColor="#FFCC99";
			return;
		}
			
		var targetRequirementTypeId = 0;
		var targetRequirementTypeObject = document.getElementById("targetRequirementType");
		if (targetRequirementTypeObject != null) {
			var targetRequirementTypeId = targetRequirementTypeObject.options[targetRequirementTypeObject.selectedIndex].value;
		}

		var targetFolderId = 0;
		var targetFolderObject = document.getElementById("targetFolder");
		if (targetFolderObject != null) {
			var targetFolderId = targetFolderObject.options[targetFolderObject.selectedIndex].value;
		}

		
		var reportTitle = document.getElementById("reportTitle");

		if (reportTitle.value.length == 0) {
			alert ("Please enter a Report Title ");
			reportTitle.focus();
			reportTitle.style.backgroundColor="#FFCC99";
			return;
		}
		
		
		// Now display the Report.
	
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/saveSearchReport.jsp?";		
		url += "&searchString=" + encodeURIComponent(searchString.value);
		url += "&targetRequirementTypeId=" + targetRequirementTypeId;
		url += "&targetFolderId=" + targetFolderId;
		url += "&searchType=saveSearchReport";
		url += "&reportTitle=" + encodeURIComponent(reportTitle.value);
		
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("myTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myTasksDiv");
	
	}
	
	
	
	function fillRecentlyCommentedReqsForAProject(){
		var commentedSince = document.getElementById("commentedSince").value;
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/recentlyCommentedReqsForAProject2.jsp?";
		url += "&commentedSince=" + commentedSince ;
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("recentlyCommentedReqsDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "recentlyCommentedReqsDiv");
	}	

	function fillRecentlyCommentedReqsForAFolder(folderId){
		var commentedSince = 7;
		try  {
	  		commentedSince = document.getElementById("commentedSince").value;
			
	  		}
		catch (e)  {}
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/recentlyCommentedReqsForAProject2.jsp?";
		url += "&commentedSince=" + commentedSince ;
		url += "&folderId=" + folderId;
		
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("recentlyCommentedReqsDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "recentlyCommentedReqsDiv");
	}	
	
	
	function fillMyDanglingForAProject(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myDanglingForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("myDanglingDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myDanglingDiv");
	}	
	
	function myDanglingReqsDetailsForAProject(targetDiv){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myDanglingReqsDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		if (targetDiv != null){
			document.getElementById("contentCenterA").style.display = "none";
			document.getElementById("contentCenterB").style.display = "none";
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";
			
		}
		else {
			targetDiv ="myTasksDiv"; 
		}
		document.getElementById(targetDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, targetDiv);
	
	}	
	function myDanglingReqsDetailsForAProject_directDisplay(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myDanglingReqsDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("contentCenterA").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "contentCenterA");
	
	}	
		
	
	
	function fillMyOrphanForAProject(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myOrphanForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("myOrphanDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myOrphanDiv");
	}	
	
	function myOrphanReqsDetailsForAProject(targetDiv){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myOrphanReqsDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		if (targetDiv != null){
			document.getElementById("contentCenterA").style.display = "none";
			document.getElementById("contentCenterB").style.display = "none";
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";
			
		}
		else {
			targetDiv ="myTasksDiv"; 
		}
		document.getElementById(targetDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, targetDiv);
	}	
	function myOrphanReqsDetailsForAProject_directDisplay(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myOrphanReqsDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("contentCenterA").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "contentCenterA");
	}	
	
	
	
	
	function fillMyReqsPendingApprovalForAProject(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myReqsPendingApprovalForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("myReqsPendingApprovalDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myReqsPendingApprovalDiv");
	}	
	function myReqsPendingApprovalDetailsForAProject(targetDiv){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myReqsPendingApprovalDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		if (targetDiv != null){
			document.getElementById("contentCenterA").style.display = "none";
			document.getElementById("contentCenterB").style.display = "none";
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";
			
		}
		else {
			targetDiv ="myTasksDiv"; 
		}
		document.getElementById(targetDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, targetDiv);	
	}	
	function myReqsPendingApprovalDetailsForAProject_directDisplay(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myReqsPendingApprovalDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("contentCenterA").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "contentCenterA");	
	}	
	
	
	function fillMyReqsRejectedForAProject(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myReqsRejectedForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("myReqsRejectedlDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myReqsRejectedlDiv");
	}	
	function myReqsRejectedDetailsForAProject(targetDiv){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myReqsRejectedDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		if (targetDiv != null){
			document.getElementById("contentCenterA").style.display = "none";
			document.getElementById("contentCenterB").style.display = "none";
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";
			
		}
		else {
			targetDiv ="myTasksDiv"; 
		}
		document.getElementById(targetDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, targetDiv);
	}	
	function myReqsRejectedDetailsForAProject_directDisplay(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/myReqsRejectedDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("contentCenterA").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "contentCenterA");
	}	
	
	
	
	function fillMySuspectUpForAProject(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/mySuspectUpForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("suspectUpDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "suspectUpDiv");
		
	}
	function mySuspectUpDetailsForAProject(targetDiv){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/mySuspectUpDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		if (targetDiv != null){
			document.getElementById("contentCenterA").style.display = "none";
			document.getElementById("contentCenterB").style.display = "none";
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";
			
		}
		else {
			targetDiv ="myTasksDiv"; 
		}
		document.getElementById(targetDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, targetDiv);
	
	}		
	function mySuspectUpDetailsForAProject_directDisplay(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/mySuspectUpDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("contentCenterA").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "contentCenterA");
	
	}		

	
	
	
	
	function fillMySuspectDownForAProject(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/mySuspectDownForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("suspectDownDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "suspectDownDiv");
		
	}	
	
	function mySuspectDownDetailsForAProject(targetDiv){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/mySuspectDownDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		if (targetDiv != null){
			document.getElementById("contentCenterA").style.display = "none";
			document.getElementById("contentCenterB").style.display = "none";
			document.getElementById("contentCenterC").style.display = "none";
			document.getElementById("contentCenterD").style.display = "none";
			document.getElementById("contentCenterE").style.display = "none";
			
		}
		else {
			targetDiv ="myTasksDiv"; 
		}
		document.getElementById(targetDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, targetDiv);
	
	}	
	function mySuspectDownDetailsForAProject_directDisplay(){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasksForAProject/mySuspectDownDetailsForAProject.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("contentCenterA").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "contentCenterA");
	
	}		

		
	/////////////////////////////////////////////////
	//   These are myTasks for ALL projects functions
	////////////////////////////////////////////////	

	function fillMyTasks(){
		
		/*
		if (dashboardType == null ){
			dashboardType = "UserDashboard";
		}
		*/
		
		
		dashboardTypeObject = document.getElementById("dashboardType");
		var dashboardType = "" ;
		if (dashboardTypeObject != null){
			dashboardType = dashboardTypeObject.options[dashboardTypeObject.selectedIndex].value;
		}
	
		ownedByObject = document.getElementById("ownedBy");
		var ownedBy = "" ;
		if (ownedByObject != null ){
			ownedBy = ownedByObject.options[ownedByObject.selectedIndex].value;
		}
		
		var targetProjectObject = document.getElementById("targetProject");
		var targetProjectId = 0;
		if (targetProjectObject != null) {
			var targetProjectId = targetProjectObject.options[targetProjectObject.selectedIndex].value;

			if (targetProjectId > 0 ){
				// lets fill the scope box
				document.getElementById("myTasksScopeDiv").style.display="block";
				document.getElementById("myTasksScopeDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
				var url="/GloreeJava2/jsp/UserDashboard/myTasksScope.jsp?";
				url += "&targetProjectId=" + targetProjectId;
				url += "&dashboardType=" + dashboardType;
				url += "&ownedBy=" + ownedBy;
				url += "&bustcache=" + new Date().getTime() ;
				fillOPCenterGeneric(url, "myTasksScopeDiv");
			}
			else {
				// we don't have a targetProject. i.e diplaying all projects 
				// lets hide the MPRB DIV
				document.getElementById("myTasksScopeDiv").innerHTML= "";
				document.getElementById("myTasksScopeDiv").style.display="none";
			}
		}
		
		
		
		
		fillMyPendingApproval(targetProjectId, dashboardType, ownedBy);
		fillMyIncompleteReqs(targetProjectId, dashboardType, ownedBy);
		fillMyTestFailedReqs(targetProjectId, dashboardType, ownedBy);
		fillMyTestPendingReqs(targetProjectId, dashboardType, ownedBy);
		fillRecentlyChangedReqs();
		fillRecentlyCommentedReqs();
		
		fillMyDangling(targetProjectId, dashboardType, ownedBy);
		fillMyOrphan(targetProjectId, dashboardType, ownedBy);
		
		fillMyReqsPendingApproval(targetProjectId, dashboardType, ownedBy);
		fillMyReqsRejected(targetProjectId, dashboardType, ownedBy);
		
		fillMySuspectUp(targetProjectId, dashboardType, ownedBy);
		fillMySuspectDown(targetProjectId, dashboardType, ownedBy);
		
	}
	
		
	
	function fillMyPendingApproval(targetProjectId,dashboardType,ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/myPendingApproval.jsp?";
		url += "&targetProjectId=" + targetProjectId;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("pendingMyApprovalDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "pendingMyApprovalDiv");
		
	}

	function myPendingApprovalDetails(targetProjectId,dashboardType,ownedBy){
		
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/myPendingApprovalDetails.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&targetProjectId=" + targetProjectId;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		document.getElementById("myTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myTasksDiv");
		
	}	

	
	
	
	
	function fillMyIncompleteReqs(targetProjectId ,dashboardType,ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/myIncompleteReqs.jsp?";
		url += "&targetProjectId=" + targetProjectId;
		url += "&bustcache=" + new Date().getTime() ;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		document.getElementById("incompleteDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "incompleteDiv");
	}	
	
	function myIncompleteReqsDetails(targetProjectId,dashboardType,ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/myIncompleteReqsDetails.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&targetProjectId=" + targetProjectId;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		document.getElementById("myTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myTasksDiv");
	}	

	
	
	
	
	function fillMyTestFailedReqs(targetProjectId, dashboardType, ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/myTestFailedReqs.jsp?";
		url += "&targetProjectId=" + targetProjectId;
		url += "&bustcache=" + new Date().getTime() ;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		document.getElementById("testFailedDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "testFailedDiv");
		
	}		
	
	function myTestFailedReqsDetails(targetProjectId, dashboardType, ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/myTestFailedReqsDetails.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&targetProjectId=" + targetProjectId;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		document.getElementById("myTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myTasksDiv");
	
	}

	
	
	
	
	function fillMyTestPendingReqs(targetProjectId, dashboardType, ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/myTestPendingReqs.jsp?";
		url += "&targetProjectId=" + targetProjectId;
		url += "&bustcache=" + new Date().getTime() ;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		document.getElementById("testPendingDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "testPendingDiv");
		
	}		
	
	
	function myTestPendingReqsDetails(targetProjectId, dashboardType, ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/myTestPendingReqsDetails.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&targetProjectId=" + targetProjectId;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		document.getElementById("myTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myTasksDiv");
	
	}
	
	
	
	
	
	function fillRecentlyChangedReqs(){
		
		var targetProjectObject = document.getElementById("targetProject");
		var targetProjectId = 0;
		if (targetProjectObject != null) {
			var targetProjectId = targetProjectObject.options[targetProjectObject.selectedIndex].value;
		}
		
		
		var changedSince = document.getElementById("changedSince").value;
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/recentlyChangedReqs.jsp?";
		url += "&targetProjectId=" + targetProjectId;
		url += "&changedSince=" + changedSince ;
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("recentlyChangedReqsDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "recentlyChangedReqsDiv");
		
	}	
	

	
	
	
	function findItNow(){

		// lets ensure that the search and project is not empty. 
		var searchString = document.getElementById("searchString");

		if (searchString.value.length == 0) {
			alert ("Please enter a Search String ");
			searchString.focus();
			searchString.style.backgroundColor="#FFCC99";
			return;
		}
			
		
		var targetProjectObject = document.getElementById("targetProject");
		var targetProjectId = 0;
		if (targetProjectObject != null) {
			var targetProjectId = targetProjectObject.options[targetProjectObject.selectedIndex].value;
		}
			
		
		var targetRequirementTypeId = 0;
		var targetRequirementTypeObject = document.getElementById("targetRequirementType");
		if (targetRequirementTypeObject != null) {
			var targetRequirementTypeId = targetRequirementTypeObject.options[targetRequirementTypeObject.selectedIndex].value;
		}
		
		
		// Now display the Report.
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/findItNow.jsp?";		
		url += "&searchString=" + encodeURIComponent(searchString.value);
		url += "&targetProjectId=" + targetProjectId;
		url += "&targetRequirementTypeId=" + targetRequirementTypeId;
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("myTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myTasksDiv");
	}
	
	
	function findItNowProjectRequirementTypes(){
		var targetProjectObject = document.getElementById("targetProject");
		var targetProjectId = 0;
		if (targetProjectObject != null) {
			var targetProjectId = targetProjectObject.options[targetProjectObject.selectedIndex].value;
		}
			
		// Now display the Report.
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/findItNowProjectRequirementTypes.jsp?";		
		url += "&targetProjectId=" + targetProjectId;
		url += "&bustcache=" + new Date().getTime() ;
		 
		
		document.getElementById("reqTypesDiv").style.display = "block";
		document.getElementById("reqTypesDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "reqTypesDiv");
		
	}
	
	function fillRecentlyCommentedReqs(){
		var targetProjectObject = document.getElementById("targetProject");
		var targetProjectId = 0;
		if (targetProjectObject != null) {
			var targetProjectId = targetProjectObject.options[targetProjectObject.selectedIndex].value;
		}
		
		var commentedSince = document.getElementById("commentedSince").value;
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/recentlyCommentedReqs.jsp?";
		url += "&targetProjectId=" + targetProjectId;
		url += "&commentedSince=" + commentedSince ;
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("recentlyCommentedReqsDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "recentlyCommentedReqsDiv");
	}	
	
	
	
	function fillMyDangling(targetProjectId, dashboardType, ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/myDangling.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		url += "&targetProjectId=" + targetProjectId;
		document.getElementById("myDanglingDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myDanglingDiv");
	}	

	function danglingReqsDetails(targetProjectId , dashboardType, ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/myDanglingReqsDetails.jsp?";
		url += "&targetProjectId=" + targetProjectId;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("myTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myTasksDiv");
	
	}	
	
	
	
	
	
	
	function fillMyOrphan(targetProjectId , dashboardType, ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/myOrphan.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&targetProjectId=" + targetProjectId;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		document.getElementById("myOrphanDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myOrphanDiv");
	}	
	
	function myOrphanReqsDetails(targetProjectId , dashboardType, ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/myOrphanReqsDetails.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&targetProjectId=" + targetProjectId;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		document.getElementById("myTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myTasksDiv");
	}	

	
	
	
	
	
	function fillMyReqsPendingApproval(targetProjectId , dashboardType, ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/myReqsPendingApproval.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&targetProjectId=" + targetProjectId;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		document.getElementById("myReqsPendingApprovalDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myReqsPendingApprovalDiv");
	}	
	
	function myReqsPendingApprovalDetails(targetProjectId , dashboardType, ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/myReqsPendingApprovalDetails.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&targetProjectId=" + targetProjectId;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		document.getElementById("myTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myTasksDiv");
	
	}	

	
	
	
	
	
	function fillMyReqsRejected(targetProjectId, dashboardType, ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/myReqsRejected.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&targetProjectId=" + targetProjectId;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		document.getElementById("myReqsRejectedlDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myReqsRejectedlDiv");
	}	
	
	


	
	
	
	function myReqsRejectedDetails(targetProjectId, dashboardType, ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/myReqsRejectedDetails.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&targetProjectId=" + targetProjectId;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		document.getElementById("myTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myTasksDiv");
	}	
		
	
	
	function displayRequirementVersionHistoryForMyTasks(requirementId){
		var url = "/GloreeJava2/jsp/Requirement/displayRequirementVersionHistoryForMyTasks.jsp?requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		var displayRDInReportDiv = "displayRDInReportDiv" + requirementId;
		fillOPCenterGeneric(url, displayRDInReportDiv);
	}
	


	
	// called when someone hits the 'Change Log' button in the Requirement Core.
	function displayRequirementCommentsForMyTasks(requirementId){
		var url = "/GloreeJava2/jsp/Requirement/displayRequirementCommentsForMyTasks.jsp?requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		var displayCommentsDiv = "displayCommentsDiv" + requirementId;
		
		fillOPCenterGeneric(url, displayCommentsDiv);
	}
	
	 
	// called when someone hits the 'Change Log' button in the Requirement Core.
	function addRequirementCommentForMyTask(requirementId, folderId){
		
		var commentNoteObject  = document.getElementById("comment_note" + requirementId);
		
		if (commentNoteObject.value.length == 0) {
			alert ("Please enter a Comment");
			commentNoteObject.focus();
			commentNoteObject.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (commentNoteObject.value.length > 3000){
			alert(" Please ensure that your Descriptoin is not longer than 1000 characters. Your current message is " + commentNoteObject.value.length + " characters long");
			commentNoteObject.focus();
			commentNoteObject.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		var comment_note = commentNoteObject.value;
		
		var url="/GloreeJava2/servlet/RequirementAction?";
		url += "action=addRequirementCommentForMyTask";
		url += "&requirementId=" + requirementId;
		url += "&folderId=" + folderId;
		url += "&comment_note=" + encodeURIComponent(comment_note);
		
		var displayCommentsDiv = "displayCommentsDiv" + requirementId;
		
		
		
		document.getElementById("addComment" + requirementId).disabled = true;
		document.getElementById("commentListDiv" + requirementId).innerHTML= "<span class='normalText'> &nbsp;&nbsp;&nbsp;Working...</span>";
		
		
		// this is coming fron the list of requirements in a folder
		xmlHttpOPCenterB =GetXmlHttpObject();			
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById(displayCommentsDiv).style.display = "block";
				document.getElementById(displayCommentsDiv).innerHTML=xmlHttpOPCenterB.responseText;
				
				// lets enable the add comment button
				document.getElementById("addComment" + requirementId).disabled = false;
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
			
	}



	


	
	




	
	function setRequirementPercentCompleteForMyTasks(requirementId){
		percentComplete = "100";
		url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
		url += "&targetAttribute=" + encodeURIComponent("percentComplete");
		url += "&targetValue=" + encodeURIComponent(percentComplete);
		url += "&bustcache=" + new Date().getTime() ;
		
		var percentCompleteDiv = "percentCompleteDiv" + requirementId;
		var percentCompleteActionDiv = "percentCompleteActionDiv" + requirementId;
		
		document.getElementById(percentCompleteActionDiv).style.display = "block";
		document.getElementById(percentCompleteActionDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		document.getElementById(percentCompleteDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById(percentCompleteActionDiv).innerHTML= "<span class='normalText'><font color='green'><b>Completed</b></font></span>";
				document.getElementById(percentCompleteDiv).innerHTML= "<span class='normalText'>100 %</span>";
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);	
		
	}
	
	function fillMySuspectUp(targetProjectId, dashboardType, ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/mySuspectUp.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&targetProjectId=" + targetProjectId;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		document.getElementById("suspectUpDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "suspectUpDiv");
		
	}
	
	function mySuspectUpDetails(targetProjectId , dashboardType, ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/mySuspectUpDetails.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&targetProjectId=" + targetProjectId;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		document.getElementById("myTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myTasksDiv");
	
	}	
	
	function fillMySuspectDown(targetProjectId , dashboardType, ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/mySuspectDown.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&targetProjectId=" + targetProjectId;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		document.getElementById("suspectDownDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "suspectDownDiv");
		
	}	
	
	function mySuspectDownDetails(targetProjectId , dashboardType, ownedBy){
		var url="/GloreeJava2/jsp/UserDashboard/MyTasks/mySuspectDownDetails.jsp?";
		url += "&bustcache=" + new Date().getTime() ;
		url += "&targetProjectId=" + targetProjectId;
		url += "&dashboardType=" + dashboardType;
		url += "&ownedBy=" + ownedBy;
		document.getElementById("myTasksDiv").innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, "myTasksDiv");
	
	}	
	
	
	function clearSuspectsUpStreamForMyTasks(requirementId){		
		var clearSuspectUpStreamDiv = "clearSuspectUpStreamDiv" + requirementId ;
		document.getElementById(clearSuspectUpStreamDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";

		var url="/GloreeJava2/servlet/RequirementAction?action=clearAllSuspectUpStream&requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, clearSuspectUpStreamDiv);
	}

	
	function clearSuspectsDownStreamForMyTasks(requirementId){		
		var clearSuspectDownStreamDiv = "clearSuspectDownStreamDiv" + requirementId ;
		document.getElementById(clearSuspectDownStreamDiv).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";

		var url="/GloreeJava2/servlet/RequirementAction?action=clearAllSuspectDownStream&requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, clearSuspectDownStreamDiv);
	}

	
	//////////////////////////////////////////////////////////////////////////
	// //
	// Global Search //
	// //
	// ////////////////////////////////////////////////////////////////////////	
	
	

	// used when a user does a global Search from the dashboard page 
/*
	function DELETEDglobalSearch(){

		// lets ensure that the search and project is not empty. 
		var searchString = document.getElementById("searchString");

		if (searchString.value.length == 0) {
			alert ("Please enter a Search String ");
			searchString.focus();
			searchString.style.backgroundColor="#FFCC99";
			return;
		}
		
		// lets get the selected projects list.
		var searchProjectsObject = document.getElementById("searchProjects");
		var searchProjects= "";
		if (searchProjectsObject != null) {
			for (var i = 0; i < searchProjectsObject.options.length; i++) {
				if (searchProjectsObject.options[i].selected) {
					searchProjects += searchProjectsObject.options[i].value + ','; 
				}
			}
		}
		
		if (searchProjects == "") {
			alert ("Please select at least 1 Project to Search in ");
			searchProjectsObject.focus();
			searchProjectsObject.style.backgroundColor="#FFCC99";
			return;
		}
		
		// set the other content globalSearchResultsDiv to visibile .
		
		document.getElementById("globalSearchResultsDiv").style.display = "block";
		document.getElementById("globalSearchResultsDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
			
			
		// Now display the Report.
		var url="/GloreeJava2/jsp/UserDashboard/displayGlobalSearchReport.jsp?";		
		url += "&searchString=" + encodeURIComponent(searchString.value);
		url += "&searchProjects=" + encodeURIComponent(searchProjects);
		url += "&bustcache=" + new Date().getTime() ;
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("globalSearchResultsDiv").innerHTML=xmlHttpOPCenterB.responseText;
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);		
		
	}
*/
	
	function loadSiteMetrics(){
		
		// lets load the totals
		var url="/GloreeJava2/jsp/UserDashboard/siteMetricsDetails.jsp?action=totalMetrics";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("totalsDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "totalsDiv");
		
		// lets load the users
		var url="/GloreeJava2/jsp/UserDashboard/siteMetricsDetails.jsp?action=userMetrics";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("usersDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "usersDiv");
		
		
		// lets load the number of requirement views by month
		var url="/GloreeJava2/jsp/UserDashboard/siteMetricsDetails.jsp?action=activityByMonthMetrics";
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById("activityByMonthMetricsDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		fillOPCenterGeneric(url, "activityByMonthMetricsDiv");
		
	}
	

	// the following function will be deprecated adn replaced by handleRequirementActionInDropDown
	function handleRequirementAction(requirementId, folderId){
		var rDDivName = "displayRDInFolderDiv" + requirementId;
		var requirementActionObject = document.getElementById("requirementAction"+ requirementId);
		var requirementAction = requirementActionObject.options[requirementActionObject.selectedIndex].value;
		if (requirementAction == ""){
			// do nothin
			return;
		}
		



		if (requirementAction == "Open"){
			document.getElementById('contentCenterF').style.display='none';
			displayRequirement(requirementId,'List Folder Contents');
			return;	
		}
		if (requirementAction == "Preview"){
			var  displayRDInFolderDiv = "displayRDInFolderDiv" + requirementId;
			displayRequirementDescription(requirementId,displayRDInFolderDiv);
			return;	
		}

		
		
		if (requirementAction == "Comment"){
			handleRequirementComment(requirementId,folderId);
			return;	
		}

				
		
		if (
				(requirementAction == "clearAllTracesTo")
				||
				(requirementAction == "deleteAllTracesTo")
				||
				(requirementAction == "clearAllTracesFrom")
				||
				(requirementAction == "deleteAllTracesFrom")
			){
			
			var url="/GloreeJava2/servlet/TraceAction?action=modifyTracesInBulk";
			url = url + "&bulkTraceAction=" + requirementAction ;
			url = url + "&requirementId=" + requirementId;
			url = url + "&actionDropDown=actionDropDown";
			url += "&bustcache=" + new Date().getTime() ;
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					var responseStringToTraceAction = xmlHttpOPCenterB.responseText;
					
					// once the requirement is set to completed, lets refresh the
					// row (displayRequirementInFolderDiv
					url ="/GloreeJava2/jsp/Requirement/displayARequirementLabel.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					var requirementLabelDiv = "requirementLabelDiv" + requirementId;
					document.getElementById(requirementLabelDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					xmlHttpOPCenterC =GetXmlHttpObject();
					xmlHttpOPCenterC.onreadystatechange=function() {
						if(xmlHttpOPCenterC.readyState==4){
							document.getElementById(requirementLabelDiv).innerHTML = xmlHttpOPCenterC.responseText;
							document.getElementById("displayRDAlertInFolderDiv" + requirementId).style.display = "block";
							document.getElementById("displayRDAlertInFolderDiv" + requirementId).innerHTML  = "<font color='red'>" + responseStringToTraceAction + "</font>";
							
						}
						
						
					}
					xmlHttpOPCenterC.open("GET",url,true);
					xmlHttpOPCenterC.send(null);	
				
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		
		}
		
		if (requirementAction == "MarkComplete"){
			var percentComplete = "100";
			url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
			url += "&targetAttribute=" + encodeURIComponent("percentComplete");
			url += "&targetValue=" + encodeURIComponent(percentComplete);
			url += "&bustcache=" + new Date().getTime() ;
			
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// once the requirement is set to completed, lets refresh the
					// row (displayRequirementInFolderDiv
					url ="/GloreeJava2/jsp/Requirement/displayARequirementLabel.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					var requirementLabelDiv = "requirementLabelDiv" + requirementId;
					document.getElementById(requirementLabelDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(url, requirementLabelDiv);
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		}

		if (requirementAction == "MarkInComplete"){
			var percentComplete = "0";
			url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
			url += "&targetAttribute=" + encodeURIComponent("percentComplete");
			url += "&targetValue=" + encodeURIComponent(percentComplete);
			url += "&bustcache=" + new Date().getTime() ;
			
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// once the requirement is set to completed, lets refresh the
					// row (displayRequirementInFolderDiv
					url ="/GloreeJava2/jsp/Requirement/displayARequirementLabel.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					var requirementLabelDiv = "requirementLabelDiv" + requirementId;
					document.getElementById(requirementLabelDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(url, requirementLabelDiv);
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		}
		if (requirementAction == "CIA"){
			var rDDivName = "displayRDInFolderDiv" + requirementId;
			displayChangeImpactAnalysisAllRequirementsInFolder(rDDivName,requirementId);
		}
		
		if (requirementAction == "Delete"){
			// lets set the working message
			document.getElementById(rDDivName).style.display = 'block';
			document.getElementById(rDDivName).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
			var url="/GloreeJava2/jsp/Requirement/deleteRequirementForm.jsp?requirementId=" + requirementId;
			url += "&folderId=" + folderId;
			url += "&source=requirementList";
			url += "&bustcache=" + new Date().getTime() ;
			fillOPCenterGeneric(url, rDDivName);
			
		}
		if (requirementAction == "Purge"){
			// lets set the working message
			document.getElementById(rDDivName).style.display = 'block';
			document.getElementById(rDDivName).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
			var url="/GloreeJava2/jsp/Requirement/purgeRequirementForm.jsp?requirementId=" + requirementId;
			url += "&folderId=" + folderId;
			url += "&source=requirementList";
			url += "&bustcache=" + new Date().getTime() ;
			fillOPCenterGeneric(url, rDDivName);
		}
		if (requirementAction == "Move"){
			// lets set the working message
			var url = "/GloreeJava2/jsp/Requirement/moveRequirementForm.jsp?requirementId=" + requirementId ;
			url += "&folderId=" + folderId;
			url += "&source=requirementList";
			url += "&bustcache=" + new Date().getTime() ;
			fillOPCenterGeneric(url, rDDivName);
		}
		
			
	}

	
	function handleRequirementActionInDropDown(requirementId, folderId, requirementAction){
		var rDDivName = "displayRDInFolderDiv" + requirementId;
		



		if (requirementAction == "Open"){
			document.getElementById('contentCenterF').style.display='none';
			displayRequirement(requirementId,'List Folder Contents');
			return;	
		}
		if (requirementAction == "Preview"){
			//var  displayRDInFolderDiv = "displayRDInFolderDiv" + requirementId;
			//srt change this if needed
			displayRDInFolderDiv = "modalBody";
			displayRequirementDescription(requirementId,"modalBody");
			return;	
		}

		if (requirementAction == "Comment"){
			handleRequirementComment(requirementId,folderId);
			return;	
		}

				
		
		if (
				(requirementAction == "clearAllTracesTo")
				||
				(requirementAction == "deleteAllTracesTo")
				||
				(requirementAction == "clearAllTracesFrom")
				||
				(requirementAction == "deleteAllTracesFrom")
			){
			
			var url="/GloreeJava2/servlet/TraceAction?action=modifyTracesInBulk";
			url = url + "&bulkTraceAction=" + requirementAction ;
			url = url + "&requirementId=" + requirementId;
			url = url + "&actionDropDown=actionDropDown";
			url += "&bustcache=" + new Date().getTime() ;
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){

					// lets refresh the requirement label
					url ="/GloreeJava2/jsp/Requirement/displayARequirementLabel.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					
					var requirementLabelDiv = "reqStatusBox" + requirementId;
					
					document.getElementById(requirementLabelDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working Hard...</span>";
					fillOPCenterGeneric(url, requirementLabelDiv);
					
					// lets fill the trace bar
					url ="/GloreeJava2/jsp/Requirement/displayARequirementTraceActionBar.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					
					var traceBar = "traceActionBar" + requirementId;
					fillOPCenterGeneric(url, traceBar);
					
					// lets also refresh the requirement in explorer
					url ="/GloreeJava2/jsp/Folder/displayARequirementInExplorer.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					
					fillOPCenterGeneric(url, "objectInExplorer" + requirementId);

					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		
		}
		
		if (requirementAction == "MarkComplete"){
			var percentComplete = "100";
			url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
			url += "&targetAttribute=" + encodeURIComponent("percentComplete");
			url += "&targetValue=" + encodeURIComponent(percentComplete);
			url += "&bustcache=" + new Date().getTime() ;
			
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// once the requirement is set to completed, lets refresh the
					// row (displayRequirementInFolderDiv
					url ="/GloreeJava2/jsp/Requirement/displayARequirementLabel.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					
					var requirementLabelDiv = "reqStatusBox" + requirementId;
					document.getElementById(requirementLabelDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(url, requirementLabelDiv);
					
					// lets also refresh the requirement in explorer
					url ="/GloreeJava2/jsp/Folder/displayARequirementInExplorer.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					
					fillOPCenterGeneric(url, "objectInExplorer" + requirementId);
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		}

		if (requirementAction == "MarkInComplete"){
			var percentComplete = "0";
			url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
			url += "&targetAttribute=" + encodeURIComponent("percentComplete");
			url += "&targetValue=" + encodeURIComponent(percentComplete);
			url += "&bustcache=" + new Date().getTime() ;
			
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// once the requirement is set to completed, lets refresh the
					// row (displayRequirementInFolderDiv
					url ="/GloreeJava2/jsp/Requirement/displayARequirementLabel.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					
					var requirementLabelDiv = "reqStatusBox" + requirementId;
					document.getElementById(requirementLabelDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(url, requirementLabelDiv);
					
					// lets also refresh the requirement in explorer
					url ="/GloreeJava2/jsp/Folder/displayARequirementInExplorer.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					
					fillOPCenterGeneric(url, "objectInExplorer" + requirementId);
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		}
		if (requirementAction == "CIA"){
			var rDDivName = "displayRDInFolderDiv" + requirementId;
			displayChangeImpactAnalysisAllRequirementsInFolder(rDDivName,requirementId);
		}
		
		if (requirementAction == "Delete"){
			// lets set the working message
			document.getElementById(rDDivName).style.display = 'block';
			document.getElementById(rDDivName).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
			var url="/GloreeJava2/jsp/Requirement/deleteRequirementForm.jsp?requirementId=" + requirementId;
			url += "&folderId=" + folderId;
			url += "&source=requirementList";
			url += "&bustcache=" + new Date().getTime() ;
			fillOPCenterGeneric(url, rDDivName);
			
			// lets hide the requirement in the explorer
			//document.getElementById("objectInExplorer" + requirementId).innerHTML="<font color='red'><b>Obect has been deleted. It  can be found in the Recycle bin </b></font>";
			
		}
		if (requirementAction == "Purge"){
			// lets set the working message
			document.getElementById(rDDivName).style.display = 'block';
			document.getElementById(rDDivName).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
			var url="/GloreeJava2/jsp/Requirement/purgeRequirementForm.jsp?requirementId=" + requirementId;
			url += "&folderId=" + folderId;
			url += "&source=requirementList";
			url += "&bustcache=" + new Date().getTime() ;
			fillOPCenterGeneric(url, rDDivName);
			
			// lets hide the requirement in the explorer
			//document.getElementById("objectInExplorer" + requirementId).innerHTML="<font color='red'><b>Obect has been PERMANENTLY Deleted. </b></font>";
			
		}
		if (requirementAction == "Move"){
			// lets set the working message
			var url = "/GloreeJava2/jsp/Requirement/moveRequirementForm.jsp?requirementId=" + requirementId ;
			url += "&folderId=" + folderId;
			url += "&source=requirementList";
			url += "&bustcache=" + new Date().getTime() ;
			fillOPCenterGeneric(url, rDDivName);
			
			// lets hide the requirement in the explorer
			//document.getElementById("objectInExplorer" + requirementId).innerHTML="<font color='red'><b>Obect has been moved to a different folder.  </b></font>";
			
		}
		
			
	}

	
	function setDynamicApprover( requirementId){
		var divId =  "displayRDInFolderDiv" + requirementId;
		console.log("srt in setDynamicApprover for divId " + divId + " req " + requirementId);
		var dynamicApprovalRankObject = document.getElementById("dynamicApprovalRank" + requirementId);
		if  (
				(isNaN(dynamicApprovalRankObject.value)) ||
				(dynamicApprovalRankObject.value == 0)		
		
		){
			alert ("Please enter a valid number (>0) for Dynamic Approval Rank");
			dynamicApprovalRankObject.style.backgroundColor="#FFCC99";
			dynamicApprovalRankObject.focus();
			return(0);
		}
		
		var dynamicApprovalRoleObject = document.getElementById("setDynamicApprovalRole" + requirementId);
		
		var dynamicApprovalRole = dynamicApprovalRoleObject[dynamicApprovalRoleObject.selectedIndex].value;
		var dynamicApprovalRank = dynamicApprovalRankObject.value;
		
		url ="/GloreeJava2/jsp/Requirement/displayDynamicApprovers.jsp?requirementId=" + requirementId;
		url += "&action=setDynamicApprover"  ;
		url += "&dynamicApprovalRole=" + dynamicApprovalRole ;
		url += "&dynamicApprovalRank=" + dynamicApprovalRank ;
		
		url += "&bustcache=" + new Date().getTime() ;
		fillOPCenterGeneric(url, divId);
			
	}

	function removeDynamicApprover(  requirementId){
		
		var divId =  "displayRDInFolderDiv" + requirementId;
		var dynamicApprovalRoleObject = document.getElementById("removeDynamicApprovalRole" + requirementId);
		var dynamicApprovalRole = dynamicApprovalRoleObject[dynamicApprovalRoleObject.selectedIndex].value;
		
		
		url ="/GloreeJava2/jsp/Requirement/displayDynamicApprovers.jsp?requirementId=" + requirementId;
		url += "&dynamicApprovalRole=" + dynamicApprovalRole ;
		url += "&action=removeDynamicApprover"  ;
		url += "&bustcache=" + new Date().getTime() ;
		fillOPCenterGeneric(url, divId);
			
	}
	function showApproversTable( divId, requirementId){
		
		
		url ="/GloreeJava2/jsp/Requirement/displayApprovalTable.jsp?requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById(divId).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, divId);
			
	}
	
	
	function showApproveRejectDiv(divId, requirementId){
		
		
		url ="/GloreeJava2/jsp/Requirement/displayApproveRejectForm.jsp?requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;

		document.getElementById(divId).style.display = 'block'; 
		document.getElementById(divId).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		
		

		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				
				document.getElementById(divId).innerHTML=xmlHttpOPCenterB.responseText;
				
				// lets refresh the approversTableDivInApproveRejectForm
				showApproversTable("approversTableDivInApproveRejectForm", requirementId);
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
			
	}
	
	
	
	function showApproveRejectInListViewDiv(divId, requirementId){
		
		
		url ="/GloreeJava2/jsp/Requirement/displayApproveRejectFormInListView.jsp?requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		

		document.getElementById(divId).style.display = 'block'; 
		document.getElementById(divId).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		
		

		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				
				document.getElementById(divId).innerHTML=xmlHttpOPCenterB.responseText;
				
				// lets refresh the approversTableDivInApproveRejectForm
				showApproversTable("approversTableDivInApproveRejectForm" + requirementId , requirementId);
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
			
	}
			
	function fillNextLevelCIA(divId, requirementId, direction){
		
		
		url ="/GloreeJava2/jsp/Requirement/CIA/fillNextLevelCIA.jsp?requirementId=" + requirementId;
		
		url += "&direction=" + encodeURIComponent(direction);
		url += "&bustcache=" + new Date().getTime() ;
		document.getElementById(divId).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
		fillOPCenterGeneric(url, divId);
			
	}

	function handleRequirementActionInCIA2(requirementAction,URL,folderView, rDDivName, uniqueIdentifier, requirementId, folderId){
		
		

		if (requirementAction == "CIA"){
			if (folderView == "no"){
				displayChangeImpactAnalysis(requirementId);
			}
			else {
				displayChangeImpactAnalysisAllRequirementsInFolder(rDDivName ,requirementId);
			}
			return;
		}
		
		

		
		if (requirementAction == "OpenInNewTab"){
			window.open (URL);
		}
		
	
		
		
		if (requirementAction == "MarkComplete"){
			var percentComplete = "100";
			url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
			url += "&targetAttribute=" + encodeURIComponent("percentComplete");
			url += "&targetValue=" + encodeURIComponent(percentComplete);
			url += "&bustcache=" + new Date().getTime() ;
			
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// once the requirement is set to completed, lets refresh the
					// row (displayRequirementInFolderDiv
					var requirementLabelDiv = "requirementLabelDiv" +  uniqueIdentifier;
					url ="/GloreeJava2/jsp/Requirement/displayARequirementLabel.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					document.getElementById(requirementLabelDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(url, requirementLabelDiv);
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		}

		if (requirementAction == "MarkInComplete"){
			var percentComplete = "0";
			url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
			url += "&targetAttribute=" + encodeURIComponent("percentComplete");
			url += "&targetValue=" + encodeURIComponent(percentComplete);
			url += "&bustcache=" + new Date().getTime() ;
			
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// once the requirement is set to completed, lets refresh the
					// row (displayRequirementInFolderDiv
					var requirementLabelDiv = "requirementLabelDiv" +  uniqueIdentifier;
					url ="/GloreeJava2/jsp/Requirement/displayARequirementLabel.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					document.getElementById(requirementLabelDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(url, requirementLabelDiv);
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		}
		if (requirementAction == "CIA"){
			var rDDivName = "displayRDInFolderDiv" + requirementId;
			displayChangeImpactAnalysisAllRequirementsInFolder(rDDivName,requirementId);
		}
			
	}
	
	function handleRequirementActionInCIA(URL,folderView, rDDivName, uniqueIdentifier, requirementId, folderId){
		
		var requirementActionObjectId = "requirementAction" + uniqueIdentifier;
		var requirementActionObject = document.getElementById(requirementActionObjectId);
		var requirementAction = requirementActionObject.options[requirementActionObject.selectedIndex].value;
		if (requirementAction == ""){
			// do nothin
			return;
		}
		

		if (requirementAction == "CIA"){
			if (folderView == "no"){
				displayChangeImpactAnalysis(requirementId);
			}
			else {
				displayChangeImpactAnalysisAllRequirementsInFolder(rDDivName ,requirementId);
			}
			return;
		}
		
		

		
		if (requirementAction == "OpenInNewTab"){
			window.open (URL);
		}
		
	
		
		
		if (requirementAction == "MarkComplete"){
			var percentComplete = "100";
			url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
			url += "&targetAttribute=" + encodeURIComponent("percentComplete");
			url += "&targetValue=" + encodeURIComponent(percentComplete);
			url += "&bustcache=" + new Date().getTime() ;
			
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// once the requirement is set to completed, lets refresh the
					// row (displayRequirementInFolderDiv
					var requirementLabelDiv = "requirementLabelDiv" +  uniqueIdentifier;
					url ="/GloreeJava2/jsp/Requirement/displayARequirementLabel.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					document.getElementById(requirementLabelDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(url, requirementLabelDiv);
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		}

		if (requirementAction == "MarkInComplete"){
			var percentComplete = "0";
			url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
			url += "&targetAttribute=" + encodeURIComponent("percentComplete");
			url += "&targetValue=" + encodeURIComponent(percentComplete);
			url += "&bustcache=" + new Date().getTime() ;
			
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// once the requirement is set to completed, lets refresh the
					// row (displayRequirementInFolderDiv
					var requirementLabelDiv = "requirementLabelDiv" +  uniqueIdentifier;
					url ="/GloreeJava2/jsp/Requirement/displayARequirementLabel.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					document.getElementById(requirementLabelDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(url, requirementLabelDiv);
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		}
		if (requirementAction == "CIA"){
			var rDDivName = "displayRDInFolderDiv" + requirementId;
			displayChangeImpactAnalysisAllRequirementsInFolder(rDDivName,requirementId);
		}
			
	}
	
	
	function handleRequirementActionOther(requirementId, folderId, requirementAction){
		var rDDivName = "displayRDInFolderDiv" + requirementId;
		
		
		if (requirementAction == "submitForApproval"){
			url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
			url += "&targetAttribute=" + encodeURIComponent("submitForApproval");
			url += "&bustcache=" + new Date().getTime() ;
			
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// once the requirement is set to completed, lets refresh the
					// row (displayRequirementInFolderDiv
					var  displayRequirementInFolderDiv = "approvalDiv" + requirementId;
					url="/GloreeJava2/jsp/Requirement/displayRequirementStatus.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					document.getElementById(displayRequirementInFolderDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(url, displayRequirementInFolderDiv);
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		}

		if (requirementAction == "approve"){
		
			var approvalNoteValue = document.getElementById("approvalNote" + requirementId).value;
			url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
			url += "&targetAttribute=" + encodeURIComponent("approve");
			url += "&approvalNoteValue=" + encodeURIComponent(approvalNoteValue);
			url += "&bustcache=" + new Date().getTime() ;


			document.getElementById('approveRejectDiv' + requirementId ).style.display = 'none';
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// once the requirement is set to completed, lets refresh the
					// row (displayRequirementInFolderDiv
					var  displayRequirementInFolderDiv = "approvalDiv" + requirementId;
					url="/GloreeJava2/jsp/Requirement/displayRequirementStatus.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					document.getElementById(displayRequirementInFolderDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(url, displayRequirementInFolderDiv);
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		}
		
		if (requirementAction == "reject"){
			
			var approvalNoteValue = document.getElementById("approvalNote" + requirementId).value;
			
			if (approvalNoteValue.length == 0) 
				{
					// to reject a requirement, you have to provide a note
					alert ("Please enter a reason for rejection");
					document.getElementById("approvalNote" + requirementId).focus();
					document.getElementById("approvalNote" + requirementId).style.backgroundColor="#FFCC99";
					return;
				}
				
			
			
			url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
			url += "&targetAttribute=" + encodeURIComponent("reject");
			url += "&approvalNoteValue=" + encodeURIComponent(approvalNoteValue);
			url += "&bustcache=" + new Date().getTime() ;
			
			
			document.getElementById('approveRejectDiv' + requirementId ).style.display = 'none';
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// once the requirement is set to completed, lets refresh the
					// row (displayRequirementInFolderDiv
					var  displayRequirementInFolderDiv = "approvalDiv" + requirementId;
					url="/GloreeJava2/jsp/Requirement/displayRequirementStatus.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					document.getElementById(displayRequirementInFolderDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(url, displayRequirementInFolderDiv);
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		}
		
		if (requirementAction == "cancelMyRejection"){
			// this is similar to requestApprovalFromRejector, but the user is just requesting it from himself.
			
			var approvalNoteValue = " Cancel Rejection per user requst, so he can re-approve";
			

			var url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
			url += "&targetAttribute=" + encodeURIComponent(requirementAction);
			url += "&approvalNoteValue=" + encodeURIComponent(approvalNoteValue);
			url += "&bustcache=" + new Date().getTime() ;
			
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// once the cancelMyRejection is completed , then we refresh the requirement.

					// Now fill contentCenterB with the Requirement Core Info
					displayRequirementCore(requirementId, "");
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		}
		
		if (requirementAction == "cancelMyRejectionInFolderList"){
			// this is similar to requestApprovalFromRejector, but the user is just requesting it from himself.
			
			var approvalNoteValue = " Cancel Rejection per user requst, so he can re-approve";
			

			var url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
			url += "&targetAttribute=" + encodeURIComponent("cancelMyRejection");
			url += "&approvalNoteValue=" + encodeURIComponent(approvalNoteValue);
			url += "&bustcache=" + new Date().getTime() ;
			
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// once the cancelMyRejection is completed , then we refresh the requirement in folderlistView
					var  displayRequirementInFolderDiv = "approvalDiv" + requirementId;
					url="/GloreeJava2/jsp/Requirement/displayRequirementStatus.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					document.getElementById(displayRequirementInFolderDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(url, displayRequirementInFolderDiv);
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		}
		
		if (
				(requirementAction == "requestApprovalFromRejector")
				||
				(requirementAction == "bypassRejector")
				||
				(requirementAction == "bypassAllApprovers")
				||
				(requirementAction.includes("bypassAnApprover"))
			){
			
			var approvalNoteValue = document.getElementById("fixRejectNote" + requirementId).value;
			
			if (approvalNoteValue.length == 0) 
				{
					// to reject a requirement, you have to provide a note
					alert ("Please enter an explanation for this action");
					document.getElementById("approvalNote" + requirementId).focus();
					document.getElementById("approvalNote" + requirementId).style.backgroundColor="#FFCC99";
					return;
				}
				
			
			
			url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
			url += "&targetAttribute=" + encodeURIComponent(requirementAction);
			url += "&approvalNoteValue=" + encodeURIComponent(approvalNoteValue);
			url += "&bustcache=" + new Date().getTime() ;
			
			
			
			document.getElementById('fixRejectDiv' + requirementId ).style.display = 'none';
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// once the requirement is set to completed, lets refresh the
					// row (displayRequirementInFolderDiv
				
					var  displayRequirementInFolderDiv = "approvalDiv" + requirementId;
					url="/GloreeJava2/jsp/Requirement/displayRequirementStatus.jsp?requirementId=" + requirementId;
					url += "&bustcache=" + new Date().getTime() ;
					document.getElementById(displayRequirementInFolderDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(url, displayRequirementInFolderDiv);
					
					// lets also close the approve/reject form.
					document.getElementById('approveRejectDiv' + requirementId).style.display = 'none';
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		}	
	}
	
	
	function handleRequirementActionOtherInCIA(uniqueIdentifier, requirementId, folderId, requirementAction){
		var rDDivName = "displayRDInFolderDiv" + requirementId;
		var requirementApprovalStatusDiv =  "requirementApprovalStatusDiv" + uniqueIdentifier;
		
		if (requirementAction == "submitForApproval"){
			url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
			url += "&targetAttribute=" + encodeURIComponent("submitForApproval");
			url += "&bustcache=" + new Date().getTime() ;
			
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// once the requirement is set to completed, lets refresh the
					// row (displayRequirementInFolderDiv
					
					
					url="/GloreeJava2/jsp/Requirement/displayARequirementApprovalStatus.jsp?requirementId=" + requirementId;
					url += "&requirementApprovalStatusDiv=" + requirementApprovalStatusDiv;
					url += "&uniqueIdentifier=" + uniqueIdentifier;
					url += "&bustcache=" + new Date().getTime() ;
					document.getElementById(requirementApprovalStatusDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(url, requirementApprovalStatusDiv);
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		}

		if (requirementAction == "approve"){
		
			var approvalNoteValue = document.getElementById("approvalNote" + uniqueIdentifier).value;
			url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
			url += "&targetAttribute=" + encodeURIComponent("approve");
			url += "&approvalNoteValue=" + encodeURIComponent(approvalNoteValue);
			url += "&bustcache=" + new Date().getTime() ;
			
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					document.getElementById("approveRejectDiv" + uniqueIdentifier).style.display="none";
					
					// once the requirement is set to completed, lets refresh the
					// row (displayRequirementInFolderDiv
					url="/GloreeJava2/jsp/Requirement/displayARequirementApprovalStatus.jsp?requirementId=" + requirementId;
					url += "&requirementApprovalStatusDiv=" + requirementApprovalStatusDiv;
					url += "&uniqueIdentifier=" + uniqueIdentifier;
					url += "&bustcache=" + new Date().getTime() ;
					document.getElementById(requirementApprovalStatusDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(url, requirementApprovalStatusDiv);
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		}
		
		if (requirementAction == "reject"){
			
			var approvalNoteValue = document.getElementById("approvalNote" + uniqueIdentifier).value;
			if (approvalNoteValue.length == 0) 
				{
					// to reject a requirement, you have to provide a note
					alert ("Please enter a reason for rejection");	
					document.getElementById("approvalNote" + uniqueIdentifier).focus();
					document.getElementById("approvalNote" + uniqueIdentifier).style.backgroundColor="#FFCC99";
					return;
				}
				
			
			
			url="/GloreeJava2/jsp/Requirement/setRequirementValue.jsp?requirementId=" + requirementId;
			url += "&targetAttribute=" + encodeURIComponent("reject");
			url += "&approvalNoteValue=" + encodeURIComponent(approvalNoteValue);
			url += "&bustcache=" + new Date().getTime() ;
			
			
			xmlHttpOPCenterB =GetXmlHttpObject();
			xmlHttpOPCenterB.onreadystatechange=function() {
				if(xmlHttpOPCenterB.readyState==4){
					// once the requirement is set to completed, lets refresh the
					// row (displayRequirementInFolderDiv
					document.getElementById("approveRejectDiv" + uniqueIdentifier).style.display="none";
					
					
					url="/GloreeJava2/jsp/Requirement/displayARequirementApprovalStatus.jsp?requirementId=" + requirementId;
					url += "&requirementApprovalStatusDiv=" + requirementApprovalStatusDiv;
					url += "&uniqueIdentifier=" + uniqueIdentifier;
					url += "&bustcache=" + new Date().getTime() ;
					document.getElementById(requirementApprovalStatusDiv).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
					fillOPCenterGeneric(url, requirementApprovalStatusDiv);
					
				}
			}
			xmlHttpOPCenterB.open("GET",url,true);
			xmlHttpOPCenterB.send(null);	
		}
		
			
	}
	
	function handleRequirementComment(requirementId, folderId){
		var rDDivName = "displayRDInFolderDiv" + requirementId;
			// lets set the working message
			document.getElementById(rDDivName).style.display = 'block';
			document.getElementById(rDDivName).innerHTML= "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
			
			var url = "/GloreeJava2/jsp/Requirement/displayRequirementComments.jsp?requirementId=" + requirementId;
			url += "&folderId=" + folderId;
			url += "&source=requirementList";
			url += "&bustcache=" + new Date().getTime() ;
			fillOPCenterGeneric(url, rDDivName);
	}
	
	function cleanHTML(html) {

		// looks like all word html crap has LsdException in it. So we do the clean up only if the input string has
		// LsdException in it. 
		// otherwise, we risk removing all the formatting put in place by YUI editor
		if (html.indexOf("LsdException") > 0 ){
	    // Remove additional MS Word content
		    html = html.replace(/<(\/)*(\\?xml:|meta|link|span|font|del|ins|st1:|[ovwxp]:)((.|\s)*?)>/gi, ''); // Unwanted tags
		    html = html.replace(/(class|style|type|start)=("(.*?)"|(\w*))/gi, ''); // Unwanted sttributes
		    html = html.replace(/<style(.*?)style>/gi, '');   // Style tags
		    html = html.replace(/<script(.*?)script>/gi, ''); // Script tags
		    html = html.replace(/<!--(.*?)-->/gi, '');        // HTML comments
		}
	    return html;
	}

	/*
	function projectTrendShowOneDiv(visibleDiv) {

		// set all divs in nav boxes to hide, and then display only the visibleDiv
		console.log("srt in projectTrendShowOneDiv for vid " + visibleDiv);
		try{
		document.getElementById("allRequirements").style.display="none";
		document.getElementById("draftRequirements").style.display="none";
		document.getElementById("inApprovalWorkFlowRequirements").style.display="none";
		document.getElementById("rejectedRequirements").style.display="none";
		document.getElementById("approvedRequirements").style.display="none";
		
		document.getElementById("orphanRequirements").style.display="none";
		document.getElementById("danglingRequirements").style.display="none";
		document.getElementById("suspectUpRequirements").style.display="none";
		document.getElementById("suspectDownRequirements").style.display="none";
		
		document.getElementById("completedRequirements").style.display="none";
		document.getElementById("incompleteRequirements").style.display="none";
		
		document.getElementById("testPendingRequirements").style.display="none";
		document.getElementById("testFailRequirements").style.display="none";
		document.getElementById("testPassRequirements").style.display="none";
		
		document.getElementById(visibleDiv).style.display="block";
		}
		catch (err){
			console.log("err.message " + err.message + " at " + err.stack);
		}
	}
*/
	
	
	function loadLD(requirementId){
		
		url ="/GloreeJava2/jsp/Requirement/displayARequirementLabel.jsp?requirementId=" + requirementId;
		url += "&bustcache=" + new Date().getTime() ;
		
		var labelDivInRequirementHierarchy = "lDiv" + requirementId;

		if (document.getElementById(labelDivInRequirementHierarchy).style.display == 'none'){
		document.getElementById(labelDivInRequirementHierarchy).style.display = 'block';
			document.getElementById(labelDivInRequirementHierarchy).innerHTML = "<span class='normalText'>&nbsp;&nbsp;&nbsp;Working...</span>";
			fillOPCenterGeneric(url, labelDivInRequirementHierarchy);
		}
	}
	