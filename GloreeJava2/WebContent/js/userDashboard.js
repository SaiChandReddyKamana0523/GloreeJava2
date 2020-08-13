
	
	function createNewProjectWizard(thisForm, creatorEmailId){		
		var projectName = thisForm.projectName;
		var shortName = thisForm.shortName;
		var projectDescription = thisForm.projectDescription;
		
		var projectOwner = thisForm.projectOwner;
		var projectWebsite = thisForm.projectWebsite;
		var projectOrganization = thisForm.projectOrganization;
		var projectTags = thisForm.projectTags;	
		
		
		var restrictedDomains = thisForm.restrictedDomains;
		var administrators = thisForm.administrators;
		var users = thisForm.users;
		
		
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
		
		// lets make sure that the email ids are part of the restricted list.
		var invalidAdministrators = '';
		var invalidUsers = '';
	/*	if (restrictedDomains.value.length > 0){
			// we do this only if there is a value in the restrictedDomains box.
			restrictedDomainsArray = restrictedDomains.value.split(',');
			
			// lets work through the admins
			administratorsArray = administrators.value.split(',');
			for (a in administratorsArray){
				var currentAdmin = administratorsArray[a];
				if ((currentAdmin != null) && (currentAdmin.length > 0)){
					var validAdmin = 0 ;
					for (d in restrictedDomainsArray){
						var currentDomain = trim(restrictedDomainsArray[d]);
						var pos	 = currentAdmin.indexOf(currentDomain);
						if (pos > 0) {
							validAdmin = 1;
						}
					}
					if (validAdmin != 1 ){
						invalidAdministrators += currentAdmin + ", ";
					}
				}
			}
			
			
			// lets work through the users
			usersArray = users.value.split(',');
			for (a in usersArray){
				var currentUser = usersArray[a];
				if ((currentUser != null) && (currentUser.length > 0)){
					var validUser = 0 ;
					for (d in restrictedDomainsArray){
						var currentDomain = trim(restrictedDomainsArray[d]);
						var pos	 = currentUser.indexOf(currentDomain);
						if (pos > 0) {
							validUser= 1;
						}
					}
					if (validUser!= 1 ){
						invalidUsers += currentUser+ ", ";
					}
				}
			}
			
			
			// lets make sure that the creator's (i.e. the logged in user's) emailid
			// is in the restricted list.
			validUser = 0 ;
			for (d in restrictedDomainsArray){
				var currentDomain = trim(restrictedDomainsArray[d]);
				var pos	 = creatorEmailId.indexOf(currentDomain);
				if (pos > 0) {
					validUser= 1;
				}
			}
			if (validUser!= 1 ){
				invalidUsers += creatorEmailId + ", ";
			}
		}

		
		
		
		if (invalidUsers.length > 0){
			alert(" Your User Email Ids  do not seem to be in the permitted email domain : " + 
				invalidUsers );
			users.focus();
			users.style.backgroundColor="#FFCC99";
			restrictedDomains.style.backgroundColor="#FFCC99"; 
			return(0);
		}
		
		if (invalidAdministrators.length > 0 ){
			alert(" Your Administrator Email Ids  do not seem to be in the permitted email domain :  " + 
			invalidAdministrators);
			administrators.focus();
			administrators.style.backgroundColor="#FFCC99";
			restrictedDomains.style.backgroundColor="#FFCC99";
			return(0);
		}
		*/
		document.getElementById("createNewProjectButton").disabled=true;
		thisForm.submit();
	}

	function cloneProject(thisForm){

		sourceProjectIdObject = document.getElementById("sourceProjectId");
		if (sourceProjectIdObject.selectedIndex == 0)   {
			alert ("Please Select a Project to Clone");
			sourceProjectIdObject.focus();
			sourceProjectIdObject.style.backgroundColor="#FFCC99";
			return(0);
		}
		else {
			document.getElementById("cloneProjectButton").disabled=true;
			document.getElementById("cloneProjectMessageDiv").style.display='block';
			thisForm.action.value = 'cloneProject';
			thisForm.submit();
		}
	}
	// called when someone clicks on 'Open Project' button on user dahsboard.
	function openProject(projectIdParam){
		var thisForm = document.getElementById("form1");
		thisForm.action.value = 'openProject';
		thisForm.projectId.value = projectIdParam;
		genericLog(projectIdParam,"Project", " Opening a project");
		thisForm.submit();
	}
	
	function hideProject(projectIdParam , projectPrefix){
		var thisForm = document.getElementById("form1");
		thisForm.action.value = 'hideProject';
		thisForm.projectId.value = projectIdParam;
		thisForm.projectPrefix.value=projectPrefix;
		thisForm.submit();
	}	
	
	function unHideProject(projectIdParam , projectPrefix){
		var thisForm = document.getElementById("form1");
		thisForm.action.value = 'unHideProject';
		thisForm.projectId.value = projectIdParam;
		thisForm.projectPrefix.value=projectPrefix;
		thisForm.submit();
	}	
