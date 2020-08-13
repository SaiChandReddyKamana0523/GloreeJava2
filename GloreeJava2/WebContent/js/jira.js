	// GloreeJava2
	////////////////////////////////////////////////////////////////
	//
	// common methods for ClearQuest Integration
	//
	//////////////////////////////////////////////////////////////////
		


	function displayFoldersInJiraHome(){
		var projectIdObject = document.getElementById("projectId");
		var projectId = projectIdObject.options[projectIdObject.selectedIndex].value;
		
		// Now fill contentCenterB with the folder details.
		document.getElementById("foldersInJiraDiv").style.display = "block";
		document.getElementById("foldersInJiraDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/Jira/displayFoldersInJiraHome.jsp?projectId=" + projectId ;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "foldersInJiraDiv");
	}	
	
	
	
	function handleDisplayRequirementsInJiraHomeKeyPress(event, projectId, searchType){
		var keyCode = event.keyCode;
		if (keyCode == 13) {
			displayRequirementsInJiraHome(projectId, searchType);
		}
	}
	function displayRequirementsInJiraHome(projectId, searchType){
		
		// lets get the JID and JURL values and send them across
		var JID = document.getElementById("JID").value;
		var JURL = document.getElementById("JURL").value;
		
		var folderIdObject = document.getElementById("folderId");
		var folderId = folderIdObject.options[folderIdObject.selectedIndex].value;
		
		var searchString = '';
		if (searchType == 'reqId'){
			searchString = document.getElementById("reqIdSearchString").value;
			if (searchString.length == 0){
				alert ("Please enter a value for Req Id Search String like FR-1");
				document.getElementById("reqIdSearchString").focus();
				document.getElementById("reqIdSearchString").style.backgroundColor="#FFCC99";
				return(0);
			}
		}
		if (searchType == 'keyword'){
			searchString = document.getElementById("googleSearchString").value;
			if (searchString.length == 0){
				alert ("Please enter some keywords for google like search");
				document.getElementById("googleSearchString").focus();
				document.getElementById("googleSearchString").style.backgroundColor="#FFCC99";
				return(0);
			}			
		}
		


		
		
		
		
		// Now fill contentCenterB with the folder details.
		document.getElementById("displayRequirementsInJiraHomeDiv").style.display = "block";
		document.getElementById("displayRequirementsInJiraHomeDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/Jira/displayRequirementsInJiraHomeDiv.jsp?projectId=" + projectId + "&folderId=" + folderId ;
		url += "&searchType=" +  encodeURIComponent(searchType) ;
		url += "&searchString=" +  encodeURIComponent(searchString) ;
		url += "&JID=" +  encodeURIComponent(JID) ;
		url += "&JURL=" +  encodeURIComponent(JURL) ;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "displayRequirementsInJiraHomeDiv");
	}	
	
	
	function displayExistingProxies(JID, JURL){
		document.getElementById("existingProxiesDiv" ).style.display = "block";
		document.getElementById("existingProxiesDiv" ).innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var refreshURL = "/GloreeJava2/jsp/Jira/displayExistingProxies.jsp?";
		refreshURL += "JID=" +  encodeURIComponent(JID) ;
		refreshURL += "&JURL=" +  encodeURIComponent(JURL) ;
		refreshURL += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(refreshURL, "existingProxiesDiv");
	}
	
	function pushJiraToTraceCloud(projectId, requirementId, requirementFullTag){
		
		
		var JID = document.getElementById("JID").value;
		var JPROJECT = document.getElementById("JPROJECT").value;
		
		var JTYPE = document.getElementById("JTYPE").value;
		var JPRIORITY = document.getElementById("JPRIORITY").value;
		var JLABELS = document.getElementById("JLABELS").value;
		var JSTATUS = document.getElementById("JSTATUS").value;

		var JRESOLUTION = document.getElementById("JRESOLUTION").value;
		var JAFFECTSV = document.getElementById("JAFFECTSV").value;
		
		var JFIXV = document.getElementById("JFIXV").value;
		var JASSIGNEE = document.getElementById("JASSIGNEE").value;
		var JREPORTER = document.getElementById("JREPORTER").value;
		
		var JCREATED = document.getElementById("JCREATED").value;
		var JUPDATED = document.getElementById("JUPDATED").value;
		var JURL = document.getElementById("JURL").value;
		var JTITLE = document.getElementById("JTITLE").value;
		var JDESCRIPTION = document.getElementById("JDESCRIPTION").value;
		
		var url="/GloreeJava2/servlet/JiraAction?action=pushJiraToTraceCloud" ;
		url += "&JID=" +  encodeURIComponent(JID) ;
		url += "&JPROJECT=" +  encodeURIComponent(JPROJECT) ;
		url += "&JTYPE=" +  encodeURIComponent(JTYPE) ;
		url += "&JPRIORITY=" +  encodeURIComponent(JPRIORITY) ;
		url += "&JLABELS=" +  encodeURIComponent(JLABELS) ;
		url += "&JSTATUS=" +  encodeURIComponent(JSTATUS) ;
		url += "&JRESOLUTION=" +  encodeURIComponent(JRESOLUTION) ;
		url += "&JAFFECTSV=" +  encodeURIComponent(JAFFECTSV) ;
		url += "&JFIXV=" +  encodeURIComponent(JFIXV) ;
		
		url += "&JASSIGNEE=" +  encodeURIComponent(JASSIGNEE) ;
		url += "&JREPORTER=" +  encodeURIComponent(JREPORTER) ;
		
		url += "&JCREATED=" +  encodeURIComponent(JCREATED) ;
		url += "&JUPDATED=" +  encodeURIComponent(JUPDATED) ;
		url += "&JURL=" +  encodeURIComponent(JURL) ;
		url += "&JTITLE=" +  encodeURIComponent(JTITLE) ;
		url += "&JDESCRIPTION=" +  encodeURIComponent(JDESCRIPTION) ;
		
		url += "&projectId=" + projectId;
		url += "&requirementId=" +  encodeURIComponent(requirementId) ;
		url += "&requirementFullTag=" +  encodeURIComponent(requirementFullTag) ;
		
		url += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById("pushJiraToTraceCloudDiv" + requirementId).style.display = "block";
		document.getElementById("pushJiraToTraceCloudDiv" + requirementId).innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
	
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("pushJiraToTraceCloudDiv" + requirementId).innerHTML=xmlHttpOPCenterB.responseText;	
				// lets refesh the display existing proxies page.
				displayExistingProxies(JID,JURL);
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
		
	}	
	
	
	function displayJiraDashboard(){
		var refreshURL = "/GloreeJava2/jsp/Jira/displayJiraDashboard.jsp?";
		refreshURL += "&bustcache=" + new Date().getTime() ;
		

		document.getElementById("newProxiesDiv").style.display = "none";
		document.getElementById("existingProxiesDiv").style.display = "none";
		fillOPCenterGeneric(refreshURL, "jiraDashboardDiv");
	}	
		
	

	function displayJiraProjectFolders(){
		
		var projectIdObject = document.getElementById("traceCloudProjectId");
		var projectId = projectIdObject.options[projectIdObject.selectedIndex].value;
		// Now fill contentCenterB with the folder details.
		var url="/GloreeJava2/jsp/Jira/displayJiraProjectFolders.jsp?projectId=" + projectId ;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "jiraProjectFoldersDiv");
	}	
	

	function displayRequirementsInJiraDashboard(projectId, searchType){
		
		
		var folderIdObject = document.getElementById("folderId");
		var folderId = folderIdObject.options[folderIdObject.selectedIndex].value;
		
		
		// Now fill contentCenterB with the folder details.
		document.getElementById("displayRequirementsInJiraDashboardDiv").style.display = "block";
		document.getElementById("displayRequirementsInJiraDashboardDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/Jira/displayRequirementsInJiraDashboard.jsp?projectId=" + projectId + "&folderId=" + folderId ;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "displayRequirementsInJiraDashboardDiv");
	}	
