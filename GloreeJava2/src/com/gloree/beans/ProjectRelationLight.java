package com.gloree.beans;

public class ProjectRelationLight {

	private int id ;
	private int projectId;
	private String projectShortName = "";
	private int relatedProjectId;
	private String relatedProjectShortName = "";
	private String relatedProjectName = "";
	private String relatedProjectDescription = "";
	private String relatedProjectOwner = "";
	private String relationMadeBy = "";
	private String relationMadeDt = "";
	private String relationDescription = "";
	
	
	public ProjectRelationLight (int id, int projectId,String projectShortName, int relatedProjectId, String relatedProjectShortName, String relatedProjectName,
			String relatedProjectDescription, String relatedProjectOwner, 
			String relationMadeBy, String relationMadeDt, String relationDescription) {
		this.id = id;
		this.projectId  = projectId;
		this.projectShortName = projectShortName;
		this.relatedProjectId = relatedProjectId;
		this.relatedProjectShortName = relatedProjectShortName;
		this.relatedProjectDescription = relatedProjectDescription;
		this.relatedProjectOwner = relatedProjectOwner;
		this.relatedProjectName = relatedProjectName;
		this.relationMadeBy = relationMadeBy;
		this.relationMadeDt = relationMadeDt;
		this.relationDescription = relationDescription;
		
	}
	
	public int getId (){
		return this.id;
	}
	
	public int getProjectId(){
		return this.projectId;
	}

	
	
	public String getProjectShortName(){
		return this.projectShortName;
	}
	
	public int getRelatedProjectId(){
		return this.relatedProjectId;
	}
	
	public String getRelatedProjectShortName(){
		return this.relatedProjectShortName;
	}
	
	public String getRelatedProjectName(){
		return this.relatedProjectName;
	}

	public String getRelatedProjectDescription(){
		return this.relatedProjectDescription;
	}
		
	public String getRelatedProjectOwner(){
		return this.relatedProjectOwner;
	}
	
	public String getRelationMadeBy(){
		return this.relationMadeBy;
	}
	
	public String getRelationMadeDt(){
		return this.relationMadeDt;
	}
	
	public String getRelationDescription(){
		return this.relationDescription;
	}

	
}
