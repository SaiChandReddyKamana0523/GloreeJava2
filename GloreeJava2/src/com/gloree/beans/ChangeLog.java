package com.gloree.beans;

public class ChangeLog {

	private int projectId ;
	private int folderId;
	private int requirementId ;
	private String logType;
	private String fullTag;
	private String actorEmailId;
	private String actionDt;
	private String description;
	
	
	public ChangeLog (int projectId ,  int folderId , int requirementId , String logType , String fullTag,  String actorEmailId, String actionDt,  String description) {
		this.projectId = projectId ;
		this.folderId = folderId;
		this.requirementId = requirementId;
		this.logType = logType;
		this.fullTag = fullTag;
		this.actorEmailId = actorEmailId;
		this.actionDt = actionDt;
		this.description  = description;
	}
	
	
	public int getProjectId(){
		return (this.projectId);
	}	
	
	public int getFolderId(){
		return (this.folderId);
	}	
	
	public int getRequirementId(){
		return (this.requirementId);
	}	
	
	public String getLogType(){
		return (this.logType);
	}
	
	public String getFullTag(){
		return (this.fullTag);
	}
	
	public String getActorEmailId(){
		return (this.actorEmailId );
	}
	
	public String getActionDt(){
		return (this.actionDt);
	}
	
	public String getDescription(){
		return (this.description);
	}
	
	
}
