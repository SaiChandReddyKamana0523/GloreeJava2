	// GloreeJava2
	////////////////////////////////////////////////////////////////
	//
	// common methods for ClearQuest Integration
	//
	//////////////////////////////////////////////////////////////////
		


	function displayFoldersInCQHome(){
		var projectIdObject = document.getElementById("projectId");
		var projectId = projectIdObject.options[projectIdObject.selectedIndex].value;
		
		// Now fill contentCenterB with the folder details.
		document.getElementById("foldersInCQDiv").style.display = "block";
		document.getElementById("foldersInCQDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/ClearQuest/displayFoldersInCQHome.jsp?projectId=" + projectId ;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "foldersInCQDiv");
	}	
	
	
	
	function handleDisplayRequirementsInCQHomeKeyPress(event, projectId, searchType){
		var keyCode = event.keyCode;
		if (keyCode == 13) {
			displayRequirementsInCQHome(projectId, searchType);
		}
	}
	function displayRequirementsInCQHome(projectId, searchType){
		
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
		document.getElementById("displayRequirementsInCQHomeDiv").style.display = "block";
		document.getElementById("displayRequirementsInCQHomeDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/ClearQuest/displayRequirementsInCQHomeDiv.jsp?projectId=" + projectId + "&folderId=" + folderId ;
		url += "&searchType=" +  encodeURIComponent(searchType) ;
		url += "&searchString=" +  encodeURIComponent(searchString) ;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "displayRequirementsInCQHomeDiv");
	}	
	
	
	
	function pushToTraceCloud(projectId, requirementId, requirementFullTag){
		
		
		var CTCID = document.getElementById("CTCID").value;
		var CTCHEADLINE = document.getElementById("CTCHEADLINE").value;
		var TESTCASEID = document.getElementById("TESTCASEID").value;
		var TESTCASEHEADLINE = document.getElementById("TESTCASEHEADLINE").value;
		var CTCWEBLINK = document.getElementById("CTCWEBLINK").value;

		var RELATEDSCRID = document.getElementById("RELATEDSCRID").value;
		var RELATEDSCRNAME = document.getElementById("RELATEDSCRNAME").value;
		
		var SCRID = document.getElementById("SCRID").value;
		var SCRTITLE = document.getElementById("SCRTITLE").value;
		var SCRWEBLINK = document.getElementById("SCRWEBLINK").value;
		
			
		
		var url="/GloreeJava2/servlet/ClearQuestAction?action=pushToTraceCloud" ;
		url += "&CTCID=" +  encodeURIComponent(CTCID) ;
		url += "&CTCHEADLINE=" +  encodeURIComponent(CTCHEADLINE) ;
		url += "&TESTCASEID=" +  encodeURIComponent(TESTCASEID) ;
		url += "&TESTCASEHEADLINE=" +  encodeURIComponent(TESTCASEHEADLINE) ;
		url += "&CTCWEBLINK=" +  encodeURIComponent(CTCWEBLINK) ;
		url += "&SCRID=" +  encodeURIComponent(SCRID) ;
		url += "&SCRTITLE=" +  encodeURIComponent(SCRTITLE) ;
		url += "&SCRWEBLINK=" +  encodeURIComponent(SCRWEBLINK) ;
		
		url += "&RELATEDSCRID=" +  encodeURIComponent(RELATEDSCRID) ;
		url += "&RELATEDSCRNAME=" +  encodeURIComponent(RELATEDSCRNAME) ;
		
		
		url += "&projectId=" + projectId;
		url += "&requirementId=" +  encodeURIComponent(requirementId) ;
		url += "&requirementFullTag=" +  encodeURIComponent(requirementFullTag) ;
		
		url += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById("pushToTraceCloudDiv" + requirementId).style.display = "block";
		document.getElementById("pushToTraceCloudDiv" + requirementId).innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
	
		
		
		xmlHttpOPCenterB =GetXmlHttpObject();
		xmlHttpOPCenterB.onreadystatechange=function() {
			if(xmlHttpOPCenterB.readyState==4){
				document.getElementById("pushToTraceCloudDiv" + requirementId).innerHTML=xmlHttpOPCenterB.responseText;	
				// once we create the push to tracecloud, we may need to refresh the CIA report.
				
				document.getElementById("existingProxiesDiv" ).style.display = "block";
				document.getElementById("existingProxiesDiv" ).innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
				var refreshURL = "/GloreeJava2/jsp/ClearQuest/displayExistingProxies.jsp?";
				refreshURL += "CTCID=" +  encodeURIComponent(CTCID) ;
				refreshURL += "&SCRID=" +  encodeURIComponent(SCRID) ;
				refreshURL += "&bustcache=" + new Date().getTime() ;
				
				fillOPCenterGeneric(refreshURL, "existingProxiesDiv");
				
				
				
			}
		}
		xmlHttpOPCenterB.open("GET",url,true);
		xmlHttpOPCenterB.send(null);
		
		
	}	
		