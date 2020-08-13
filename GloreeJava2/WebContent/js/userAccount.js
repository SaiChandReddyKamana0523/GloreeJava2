

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


function createAnAccount(thisForm){

	// do some form validations.
	var ldapUserId = thisForm.ldapUserId;
	var firstName = thisForm.firstName;
	var lastName = thisForm.lastName;
	var emailId = thisForm.emailId;
	var password1 = thisForm.password1;
	var password2 = thisForm.password2;
	var petsName = thisForm.petsName;
	
	
	if (ldapUserId.value.length == 0) {
		alert ("Please enter your UserId");
		ldapUserId.focus();
		ldapUserId.style.backgroundColor="#FFCC99";
		return(0);
	}
	if (firstName.value.length == 0) {
		alert ("Please enter your First Name");
		firstName.focus();
		firstName.style.backgroundColor="#FFCC99";
		return(0);
	}
	/*if (lastName.value.length == 0) {
		alert ("Please enter your Last Name");
		lastName.focus();
		lastName.style.backgroundColor="#FFCC99";
		return(0);
	}
	*/
	if (emailId.value.length == 0) {
		alert ("Please enter your Email Id");
		emailId.focus();
		emailId.style.backgroundColor="#FFCC99";
		return(0);
	}
	
	if (echeck(emailId.value)==false){
		alert ("Your email id is not formatted correctly. Please fix it.");
		emailId.focus();
		emailId.style.backgroundColor="#FFCC99";
		return(0);
	}

	if (password1.value.length < 4 ) {
		alert ("For your security, please ensure that your password is at least 4 characters long");
		password1.focus();
		password1.style.backgroundColor="#FFCC99";
		return(0);
	}
	
	/*
	if (password1.value.length == 0) {
		alert ("Please enter your Password");
		password1.focus();
		password1.style.backgroundColor="#FFCC99";
		return(0);
	}
	
	
	if (password1.value != password2.value){
		alert ("Your password's did not match. Please re-enter them.");
		password1.value='';
		password2.value='';
		password1.focus();
		return(0);	
	}
	
	if (petsName.value.length == 0) {
		alert ("Please enter your Favorite Pet's Name. If you ever forget your password, we can use this" +
				"to verify you.");
		petsName.focus();
		petsName.style.backgroundColor="#FFCC99";
		return(0);
	}
*/
	
	// if the form validation passes do the form.submit
	if (document.getElementById("createMyAccountButton") != null) {
		document.getElementById("createMyAccountButton").disabled=true;
	}
	if (document.getElementById("cloneProjectMessageDiv") != null){
		document.getElementById("cloneProjectMessageDiv").style.display='block';
	}
	
	//firstName.value = firstName.value.replace(/\W/g,'');
	lastName.value = lastName.value.replace(/\W/g,'');
	
	
	
	thisForm.submit();

}

function resetPassWord(thisForm){

	// do some form validations.
	var emailId = thisForm.emailId;
	
	if (emailId.value.length == 0) {
		alert ("Please enter your Email Id");
		emailId.focus();
		emailId.style.backgroundColor="#FFCC99";
		return(0);
	}
	
	// if the form validation passes do the form.submit
	thisForm.submit();

}


function  handleSignInkeyPress(event, thisForm) {
	var keyCode = event.keyCode;
	if (keyCode == 13) {
		signIn(thisForm);
	}
}

function signIn_can_be_deleeted_srt(thisForm){

	// do some form validations.
	var ldapUserId = thisForm.ldapUserId;
	var emailId = thisForm.emailId;
	var password = thisForm.password;
	

	if (ldapUserId.value.length == 0) {
		alert ("Please enter your User Id");
		ldapUserId.focus();
		ldapUserId.style.backgroundColor="#FFCC99";
		return(0);
	}
	
	
	if (emailId.value.length == 0) {
		alert ("Please enter your Email Id");
		emailId.focus();
		emailId.style.backgroundColor="#FFCC99";
		return(0);
	}
	
	

	if (password.value.length == 0) {
		alert ("Please enter your Password");		
		password.focus();
		password.style.backgroundColor="#FFCC99";
		return(0);
	}
	
	
	// if the form validation passes do the form.submit
	thisForm.submit();

}




function activateAnAccount(thisForm){

	// do some form validations.
	var ccNumber = thisForm.ccNumber;
	var ccType = thisForm.ccType;
	var ccExpireMonth = thisForm.ccExpireMonth;
	var ccExpireYear = thisForm.ccExpireYear;
	var ccFullName = thisForm.ccFullName;
	var ccVerificationNumber = thisForm.ccVerificationNumber;
	var ccBillingAddress = thisForm.ccBillingAddress;
	var ccBillingZipcode = thisForm.ccBillingZipcode;
	var ccBillingCountry = thisForm.ccBillingCountry;
	
	if (ccNumber.value.length == 0) {
		alert ("Please enter your Credit Card Number");
		ccNumber.focus();
		ccNumber.style.backgroundColor="#FFCC99";
		return(0);
	}
	
	
	if (ccType.selectedIndex == 0)   {
		alert ("Please enter your Credit Card Type.");
		ccType.focus();
		ccType.style.backgroundColor="#FFCC99";
		return(0);
	}	

	if (ccExpireMonth.selectedIndex == 0)   {
		alert ("Please enter your Credit Card Expire Month.");
		ccExpireMonth.focus();
		ccExpireMonth.style.backgroundColor="#FFCC99";
		return(0);
	}	
	
	if (ccExpireYear.selectedIndex == 0)   {
		alert ("Please enter your Credit Card Expire Year.");
		ccExpireYear.focus();
		ccExpireYear.style.backgroundColor="#FFCC99";
		return(0);
	}	

	if (ccFullName.value.length == 0)   {
		alert ("Please enter your Full Name as it appears on your Credit Card.");
		ccFullName.focus();
		ccFullName.style.backgroundColor="#FFCC99";
		return(0);
	}	
	
	if (ccVerificationNumber.value.length == 0)   {
		alert ("Please enter your Credit Card Verification Number (CCV) as it appears on your Credit Card.");
		ccVerificationNumber.focus();
		ccVerificationNumber.style.backgroundColor="#FFCC99";
		return(0);
	}	
		
	if (ccBillingAddress.value.length == 0)   {
		alert ("Please enter your Billing Address for this Credit Card.");
		ccBillingAddress.focus();
		ccBillingAddress.style.backgroundColor="#FFCC99";
		return(0);
	}	
	
	if (ccBillingZipcode.value.length == 0)   {
		alert ("Please enter your Billing Zip Code for this Credit Card.");
		ccBillingZipcode.focus();
		ccBillingZipcode.style.backgroundColor="#FFCC99";
		return(0);
	}	

	
	if ((ccBillingCountry.selectedIndex == 0) || (ccBillingCountry.selectedIndex == 7))  {
		alert ("Please select your Billing Country from this Credit Card.");
		ccBillingCountry.focus();
		ccBillingCountry.style.backgroundColor="#FFCC99";
		return(0);
	}	
	
	// if the form validation passes do the form.submit
	thisForm.submit();

}


function updateBillingInfo (thisForm){

	// do some form validations.
	var ccNumber = thisForm.ccNumber;
	var ccType = thisForm.ccType;
	var ccExpireMonth = thisForm.ccExpireMonth;
	var ccExpireYear = thisForm.ccExpireYear;
	var ccFullName = thisForm.ccFullName;
	var ccVerificationNumber = thisForm.ccVerificationNumber;
	var ccBillingAddress = thisForm.ccBillingAddress;
	var ccBillingZipcode = thisForm.ccBillingZipcode;
	var ccBillingCountry = thisForm.ccBillingCountry;
	
	if (ccNumber.value.length == 0) {
		alert ("Please enter your Credit Card Number");
		ccNumber.focus();
		ccNumber.style.backgroundColor="#FFCC99";
		return(0);
	}
	
	
	if (ccType.selectedIndex == 0)   {
		alert ("Please enter your Credit Card Type.");
		ccType.focus();
		ccType.style.backgroundColor="#FFCC99";
		return(0);
	}	

	if (ccExpireMonth.selectedIndex == 0)   {
		alert ("Please enter your Credit Card Expire Month.");
		ccExpireMonth.focus();
		ccExpireMonth.style.backgroundColor="#FFCC99";
		return(0);
	}	
	
	if (ccExpireYear.selectedIndex == 0)   {
		alert ("Please enter your Credit Card Expire Year.");
		ccExpireYear.focus();
		ccExpireYear.style.backgroundColor="#FFCC99";
		return(0);
	}	

	if (ccFullName.value.length == 0)   {
		alert ("Please enter your Full Name as it appears on your Credit Card.");
		ccFullName.focus();
		ccFullName.style.backgroundColor="#FFCC99";
		return(0);
	}	
	
	if (ccVerificationNumber.value.length == 0)   {
		alert ("Please enter your Credit Card Verification Number (CCV) as it appears on your Credit Card.");
		ccVerificationNumber.focus();
		ccVerificationNumber.style.backgroundColor="#FFCC99";
		return(0);
	}	
		
	if (ccBillingAddress.value.length == 0)   {
		alert ("Please enter your Billing Address for this Credit Card.");
		ccBillingAddress.focus();
		ccBillingAddress.style.backgroundColor="#FFCC99";
		return(0);
	}	
	
	if (ccBillingZipcode.value.length == 0)   {
		alert ("Please enter your Billing Zip Code for this Credit Card.");
		ccBillingZipcode.focus();
		ccBillingZipcode.style.backgroundColor="#FFCC99";
		return(0);
	}	

	
	if (ccBillingCountry.selectedIndex == 0)   {
		alert ("Please select your Billing Country from this Credit Card.");
		ccBillingCountry.focus();
		ccBillingCountry.style.backgroundColor="#FFCC99";
		return(0);
	}	
	
	// if the form validation passes do the form.submit
	thisForm.submit();

}

function updateEmailPref(thisForm){

	var prefHealthCheckDaysObject = document.getElementById("prefHealthCheckDays");
	var prefHealthCheckDays = "";
	for (var i = 0; i < prefHealthCheckDaysObject.options.length; i++) {
		if (prefHealthCheckDaysObject.options[i].selected) {
			prefHealthCheckDays += prefHealthCheckDaysObject.options[i].value + ','; 
		}
	}
	thisForm.prefHealthCheckDaysHidden.value = prefHealthCheckDays ;
	
	
	var prefHealthCheckHideProjectsObject = document.getElementById("prefHealthCheckHideProjects");
	var prefHealthCheckHideProjects = "";
	for (var i = 0; i < prefHealthCheckHideProjectsObject.options.length; i++) {
		if (prefHealthCheckHideProjectsObject.options[i].selected) {
			prefHealthCheckHideProjects += prefHealthCheckHideProjectsObject.options[i].value + ','; 
		}
	}
	thisForm.prefHealthCheckHideProjectsHidden.value = prefHealthCheckHideProjects ;
	
	
	
	
	// if the form validation passes do the form.submit
	thisForm.submit();

}

// called when a user chooses to update Organization Info in UseDashboard.
function updateOrganizationInfo(thisForm){
	
	
	var organizationName = document.getElementById("organizationName").value;
	var organizationDescription = document.getElementById("organizationDescription").value;
	var organizationPhone = document.getElementById("organizationPhone").value;
	var readWriteLicenses = document.getElementById("readWriteLicenses").value;
	var readOnlyLicenses = document.getElementById("readOnlyLicenses").value;
	
	if (organizationName.length == 0)   {
		alert ("Please enter your Organization Name.");
		document.getElementById("organizationName").focus();
		document.getElementById("organizationName").style.backgroundColor="#FFCC99";
		return(0);
	}
	
	if (organizationPhone.length == 0)   {
		alert ("Please enter your Organization's contact phone number.");
		document.getElementById("organizationPhone").focus();
		document.getElementById("organizationPhone").style.backgroundColor="#FFCC99";
		return(0);
	}		

	thisForm.action.value = "updateOrganizationInfo";
	document.getElementById("updateOrganization").disabled = true;
	thisForm.submit();
	
}


//called when a user chooses to update Organization Info in UseDashboard.
function grantLicensesForm(thisForm, availableReadWriteLicenses, availableReadOnlyLicenses){
	
	
	
	var readWriteInvitees = document.getElementById("readWriteInvitees").value;
	var readOnlyInvitees = document.getElementById("readOnlyInvitees").value;
	
	// lets iterate through the readWriteInvitees and ensure that 
	// a) the number of invitees is same or less than available invitees.
	// b) the emailIds are valid.

	if (readWriteInvitees != ""){
		// lets validate to make sure that these values are valid	
		var readWriteInviteesArray = readWriteInvitees.split(',');
		if (readWriteInviteesArray.length > availableReadWriteLicenses ) {
			alert ("You have " +  availableReadWriteLicenses + " Read Write licenses available. "
				+ " Please reduce the number of licenses granted or purchase more licenses" );
			document.getElementById("readWriteInvitees").focus();
			document.getElementById("readWriteInvitees").style.backgroundColor="#FFCC99";
			return(0);
		}
			
		
		for (invitee in readWriteInviteesArray){
			var inviteeEmailId  = trim(readWriteInviteesArray[invitee]);
			if (echeck(inviteeEmailId)==false){
				alert ("The email id '" + inviteeEmailId +  "'  is not formatted correctly. Please fix it.");
				document.getElementById("readWriteInvitees").focus();
				document.getElementById("readWriteInvitees").style.backgroundColor="#FFCC99";
				return(0);
			}
		}
	}	
	
	if (readOnlyInvitees != ""){	
		// lets validate to make sure that these values are valid
		var readOnlyInviteesArray = readOnlyInvitees.split(',');
		if (readOnlyInviteesArray.length > availableReadOnlyLicenses ) {
			alert ("You have " +  availableReadOnlyLicenses + " Read Only licenses available. "
				+ " Please reduce the number of licenses granted or purchase more licenses" );
			document.getElementById("readOnlyInvitees").focus();
			document.getElementById("readOnlyInvitees").style.backgroundColor="#FFCC99";
			return(0);
		}
			
		
		for (invitee in readOnlyInviteesArray){
			var inviteeEmailId  = trim(readOnlyInviteesArray[invitee]);
			if (echeck(inviteeEmailId)==false){
				alert ("The email id '" + inviteeEmailId +  "'  is not formatted correctly. Please fix it.");
				document.getElementById("readOnlyInvitees").focus();
				document.getElementById("readOnlyInvitees").style.backgroundColor="#FFCC99";
				return(0);
			}
		}
	}

	
	if ((readWriteInvitees != "") || (readOnlyInvitees != "")) {
		document.getElementById("grantLicenses").disabled = true;		
		thisForm.action.value = "grantLicenses";
		thisForm.submit();
	}
}


//called when a user chooses to revoke Project License.
function revokeProjectLicense(thisForm, projectId){
	document.getElementById("revokeProjectLicenseButton").disabled = true;
	thisForm.licensedProjectId.value = projectId;
	thisForm.action.value = "revokeProjectLicense";
	thisForm.submit();
}


//called when a user chooses to revoke a license offer that is pending.
function revokeLicenseOffer(thisForm, licenseGrantId){
	document.getElementById("revokeOfferButton").disabled = true;
	thisForm.licenseGrantId.value = licenseGrantId;
	thisForm.action.value = "revokeLicensesOffer";
	thisForm.submit();
}

//called when a user chooses to revoke an accepted license 
function revokeGrantedLicense(thisForm, granteeEmailId){
	document.getElementById("revokeGrantedLicenseButton").disabled = true;
	thisForm.granteeEmailId.value = granteeEmailId;
	thisForm.action.value = "revokeGrantedLicense";
	thisForm.submit();
}
