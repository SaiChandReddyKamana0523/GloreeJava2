package com.gloree.beans;


//GloreeJava2


// This class is used to store an object of Requirement Type.

public class RolePriv {

	/**
	 * 
	 */
	
	private int roleId ;
	private int folderId;
	private String folderPath;
	private int createRequirement ;
	private int readRequirement ;
	private int updateRequirement;
	private int deleteRequirement ;
	private int traceRequirement ;
	private int approveRequirement;
	private int votingRights;
	private String updateAttributes = "";
	
	
	
	
	// The following method is called when the project core values are known and the system is only
	// interested in them. 
	public RolePriv (int roleId , int folderId, String folderPath,
	 int createRequirement , int readRequirement , int updateRequirement,
	 int deleteRequirement , int traceRequirement , 
	 int approveRequirement, int votingRights, String updateAttributes){
	 
	 	this.roleId = roleId;
		this.folderId = folderId;
		this.folderPath = folderPath;
		this.createRequirement = createRequirement;
		this.readRequirement = readRequirement ;
		this.updateRequirement = updateRequirement;
		this.deleteRequirement  = deleteRequirement;
		this.traceRequirement = traceRequirement;
		this.approveRequirement = approveRequirement;
		this.votingRights = votingRights;
		this.updateAttributes = updateAttributes;
	}
	
	
	
	public int getRoleId(){
		return this.roleId;
	}
	
	
	public int getFolderId(){
		return this.folderId;
	}		
	
	
	public String getFolderPath(){
		return this.folderPath;
	}
	
	public int getCreateRequirement(){
		return this.createRequirement;
	}
	
	
	public int getReadRequirement(){
		return this.readRequirement;
	}
	
	
	public int getUpdateRequirement(){
		return this.updateRequirement;
	}
	
	
	public int getDeleteRequirement(){
		return this.deleteRequirement;
	}
	
	
	public int getTraceRequirement(){
		return this.traceRequirement;
	}
	
	public int getApproveRequirement(){
		return this.approveRequirement;
	}
	
	public int getVotingRights(){
		return this.votingRights;
	}
	
	public String getUpdateAttributes(){
		if (updateAttributes == null){
			updateAttributes = "";
		}
		return this.updateAttributes;
	}
	
}

