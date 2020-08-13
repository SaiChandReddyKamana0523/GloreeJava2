package com.gloree.beans;

//GloreeJava2


public class RequirementVersion {

	
	private int versionId;
	private int requirementId;
	private int version;
	private String versionName;
	private String versionDescription;
	private String versionApprovers;	
	private String versionStatus;
	private String versionPriority;
	private String versionOwner;
	private int versionPctComplete;
	private String versionExternalURL;
	private String versionTraceTo;
	private String versionTraceFrom;
	private String versionUserDefinedAttributes;
	private String versionCreatedBy;
	private String versionCreatedDt;
	
	
	
	public RequirementVersion (int versionId, int requirementId, int version, String versionName,
		String versionDescription, String versionCreatedBy, String versionCreatedDt,
		String versionApprovers , String versionStatus, String versionPriority, String versionOwner,
		int versionPctComplete, String versionExternalURL , String versionTraceTo, 
		String versionTraceFrom, String versionUserDefinedAttributes ) {

		this.versionId = versionId ;
		this.requirementId = requirementId ;
		this.version = version ;
		this.versionName = versionName ;
		this.versionDescription = versionDescription ;
		this.versionCreatedBy = versionCreatedBy ;
		this.versionCreatedDt = versionCreatedDt ;
		this.versionApprovers = versionApprovers;	
		this.versionStatus = versionStatus;
		this.versionPriority = versionPriority;
		this.versionOwner = versionOwner;
		this.versionPctComplete = versionPctComplete;
		this.versionExternalURL = versionExternalURL;
		this.versionTraceTo = versionTraceTo;
		this.versionTraceFrom = versionTraceFrom;
		this.versionUserDefinedAttributes = versionUserDefinedAttributes;
	}
	
	
	public int getVersionId(){
		return this.versionId;
	}
	
	public int getRequirementId () {
		return this.requirementId;
	}

	public int getVersion(){
		return this.version;
	}	
	
	public String getVersionName(){
		return this.versionName;
	}
	
	public String getVersionDescription(){
		return this.versionDescription;
	}
	
	public String getVersionDescriptionNoHTML () {
		String versionDesciptionNoHTML = this.versionDescription;
		if (versionDescription != null) {
			versionDesciptionNoHTML = versionDesciptionNoHTML.replaceAll("\\<.*?>","");
			versionDesciptionNoHTML = versionDesciptionNoHTML.replaceAll("&nbsp;", " ");
		}
		else {
			versionDesciptionNoHTML = "";
		}
		return versionDesciptionNoHTML;
	}	
		
	public String getVersionCreatedBy(){
		return this.versionCreatedBy;
	}

	public String getVersionCreatedDt(){
		return this.versionCreatedDt;
	}
	
	public String getVersionApprovers(){
		return this.versionApprovers;
	}
	
	public String getVersionStatus(){
		return this.versionStatus;
	}
	
	public String getVersionPriority(){
		return this.versionPriority;
	}
	
	public String getVersionOwner(){
		return this.versionOwner;
	}
	public int getVersionPctComplete(){
		return this.versionPctComplete;
	}
	public String getVersionExternalURL(){
		return this.versionExternalURL;
	}
	
	public String getVersionTraceTo(){
		return this.versionTraceTo;
	}
	
	public String getVersionTraceFrom(){
		return this.versionTraceFrom;
	}
	public String getVersionUserDefinedAttributes(){
		return this.versionUserDefinedAttributes;
	}
	
	
}
