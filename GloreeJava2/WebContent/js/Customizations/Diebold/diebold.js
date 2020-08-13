	// GloreeJava2
	////////////////////////////////////////////////////////////////
	//
	// common methods for Diebold Customization.
	//
	//////////////////////////////////////////////////////////////////
		



	function displayCorrelationFilters(){
		var projectIdObject = document.getElementById("projectId");
		var projectId = projectIdObject.options[projectIdObject.selectedIndex].value;
		
		// Now fill contentCenterB with the folder details.
		document.getElementById("contentDiv").style.display = "block";
		document.getElementById("contentDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/Customizations/Diebold/Hardware/displayCorrelationFilters.jsp?projectId=" + projectId ;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "contentDiv");
	}


	function displayCriticalParameterDataFilters(){
		var projectIdObject = document.getElementById("projectId");
		var projectId = projectIdObject.options[projectIdObject.selectedIndex].value;
		
		// Now fill contentCenterB with the folder details.
		document.getElementById("contentDiv").style.display = "block";
		document.getElementById("contentDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/Customizations/Diebold/Hardware/displayCriticalParameterDataFilters.jsp?projectId=" + projectId ;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "contentDiv");
	}	
	
	
	function displayCriticalParameterData(projectId){
		
		var requirementTypeIdObject = document.getElementById("requirementTypeId");
		var requirementTypeId = requirementTypeIdObject.options[requirementTypeIdObject.selectedIndex].value;
		
		if (requirementTypeIdObject.selectedIndex == 0) {
			alert ("Please select a valid House of Quality");
			requirementTypeIdObject.focus();
			requirementTypeIdObject.style.backgroundColor="#FFCC99";
			return;
		}
		
		
		var implementationTypeObject = document.getElementById("implementationType");
		var implementationType = implementationTypeObject.options[implementationTypeObject.selectedIndex].value;
		
		
		// Now fill contentCenterB with the folder details.
		document.getElementById("criticalParameterDataDiv").style.display = "block";
		document.getElementById("criticalParameterDataDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/Customizations/Diebold/Hardware/displayCriticalParameterData.jsp?projectId=" + projectId + "&requirementTypeId=" + requirementTypeId ;
		url += "&implementationType=" + implementationType;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "criticalParameterDataDiv");
	}	
	
	

	function displayCriticalParameterTrendFilters(){
		var projectIdObject = document.getElementById("projectId");
		var projectId = projectIdObject.options[projectIdObject.selectedIndex].value;
		
		// Now fill contentCenterB with the folder details.
		document.getElementById("contentDiv").style.display = "block";
		document.getElementById("contentDiv").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		var url="/GloreeJava2/jsp/Customizations/Diebold/Hardware/displayCriticalParameterTrendFilters.jsp?projectId=" + projectId ;
		url += "&bustcache=" + new Date().getTime() ;
		
		fillOPCenterGeneric(url, "contentDiv");
	}		
	
	
	function displayCriticalParameterTrends(projectId){
		
		var fromDateObject = document.getElementById("fromDate");
		var fromDate = fromDateObject.value;
		
		var toDateObject = document.getElementById("toDate");
		var toDate = toDateObject.value;		
		
		if (isValidDate(fromDate)==false){
			fromDateObject.focus();
			fromDateObject.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		if (isValidDate(toDate)==false){
			toDateObject.focus();
			toDateObject.style.backgroundColor="#FFCC99";
			return(0);
		}
		
		
		
		document.getElementById("cPVTrendsForExcelDownloadDiv").style.display = "block";
		
		// Lets put each of the HOQ graphs.
		var hoq1URL="/GloreeJava2/jsp/Customizations/Diebold/Hardware/displayCriticalParameterTrends.jsp?projectId=" + projectId ;
		hoq1URL += "&hoq=" +  encodeURIComponent("HQ1") ;
		hoq1URL += "&fromDate=" +  encodeURIComponent(fromDate) ;
		hoq1URL += "&toDate=" +  encodeURIComponent(toDate) ;
		hoq1URL += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById("cPVTrendsForHOQ1Div").style.display = "block";
		document.getElementById("cPVTrendsForHOQ1Div").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		document.getElementById("cPVTrendsForHOQ1Div").innerHTML= 
			"<iframe src='"+ hoq1URL+"' width='900', height='400' ></iframe>";
		
		
		// Lets put each of the HOQ graphs.
		var hoq2URL="/GloreeJava2/jsp/Customizations/Diebold/Hardware/displayCriticalParameterTrends.jsp?projectId=" + projectId ;
		hoq2URL += "&hoq=" +  encodeURIComponent("HQ2") ;
		hoq2URL += "&fromDate=" +  encodeURIComponent(fromDate) ;
		hoq2URL += "&toDate=" +  encodeURIComponent(toDate) ;
		hoq2URL += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById("cPVTrendsForHOQ2Div").style.display = "block";
		document.getElementById("cPVTrendsForHOQ2Div").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		document.getElementById("cPVTrendsForHOQ2Div").innerHTML= 
			"<iframe src='"+ hoq2URL+"' width='900', height='400' ></iframe>";
		
		// Lets put each of the HOQ graphs.
		var hoq3URL="/GloreeJava2/jsp/Customizations/Diebold/Hardware/displayCriticalParameterTrends.jsp?projectId=" + projectId ;
		hoq3URL += "&hoq=" +  encodeURIComponent("HQ3") ;
		hoq3URL += "&fromDate=" +  encodeURIComponent(fromDate) ;
		hoq3URL += "&toDate=" +  encodeURIComponent(toDate) ;
		hoq3URL += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById("cPVTrendsForHOQ3Div").style.display = "block";
		document.getElementById("cPVTrendsForHOQ3Div").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		document.getElementById("cPVTrendsForHOQ3Div").innerHTML= 
			"<iframe src='"+ hoq3URL+"' width='900', height='400' ></iframe>";
		
		// Lets put each of the HOQ graphs.
		var hoq4URL="/GloreeJava2/jsp/Customizations/Diebold/Hardware/displayCriticalParameterTrends.jsp?projectId=" + projectId ;
		hoq4URL += "&hoq=" +  encodeURIComponent("HQ4") ;
		hoq4URL += "&fromDate=" +  encodeURIComponent(fromDate) ;
		hoq4URL += "&toDate=" +  encodeURIComponent(toDate) ;
		hoq4URL += "&bustcache=" + new Date().getTime() ;
		
		document.getElementById("cPVTrendsForHOQ4Div").style.display = "block";
		document.getElementById("cPVTrendsForHOQ4Div").innerHTML= "&nbsp;&nbsp;&nbsp;Working...";
		document.getElementById("cPVTrendsForHOQ4Div").innerHTML= 
			"<iframe src='"+ hoq4URL+"' width='900', height='400' ></iframe>";
		
		
		
	}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	