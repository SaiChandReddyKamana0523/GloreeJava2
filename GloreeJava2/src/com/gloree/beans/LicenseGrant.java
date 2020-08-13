package com.gloree.beans;


public class LicenseGrant {

	
	private int licenseGrantId;
	private String licenseType;
	private String grantorEmailId;
	private String granteeEmailId;
	private String grantDate;
	private int notificationSent;
	private String grantState;
	private int grantOrganizationId ; 
	private String grantOrganizationName;
	private String grantOrganizationDescription;
	private String grantOrganizationContactPhone;
	
	
	
	// The following method is called when the License Grant core values are already known and the system is only
	// interested in creating a bean. . 
	public LicenseGrant (int licenseGrantId,String licenseType,String grantorEmailId,String granteeEmailId,
	 String grantDate, int notificationSent, String grantState, int grantOrganizationId,  String grantOrganizationName,
	 String grantOrganizationDescription, String grantOrganizationContactPhone){

	  this.licenseGrantId = licenseGrantId ;
	  this.licenseType = licenseType;
	  this.grantorEmailId = grantorEmailId;
	  this.granteeEmailId = granteeEmailId;
	  this.grantDate = grantDate;
	  this.notificationSent = notificationSent;
	  this.grantState = grantState;
	  this.grantOrganizationId = grantOrganizationId;
	  this.grantOrganizationName = grantOrganizationName;
	  this.grantOrganizationDescription = grantOrganizationDescription;
	  this.grantOrganizationContactPhone = grantOrganizationContactPhone;
	}
	
	public int getLicenseGrantId(){
		return this.licenseGrantId;
	}
	
	public String getLicenseType(){
		return this.licenseType;
	}
	
	
	public String getGrantorEmailId () {
		return this.grantorEmailId;
	}
	
	
	
	public String getGranteeEmailId () {
		return this.granteeEmailId;
	}
	
	
	public String getGrantDate () {
		return this.grantDate;
	}
	
	public int getNotificationSent() {
		return this.notificationSent;
	}	
	
	public String getGrantState () {
		return this.grantState;
	}
	
	public int getGrantOrganizationId(){
		return this.grantOrganizationId;
	}
	public String getGrantOrganizationName () {
		return this.grantOrganizationName;
	}
	
	public String getGrantOrganizationDescription () {
		return this.grantOrganizationDescription;
	}
	
	public String getGrantOrganizationContactPhone () {
		return this.grantOrganizationContactPhone;
	}
	
}
