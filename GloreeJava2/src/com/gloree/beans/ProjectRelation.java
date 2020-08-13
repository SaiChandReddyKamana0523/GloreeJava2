package com.gloree.beans;

public class ProjectRelation {

	private int id ;
	private int projectId;
	private String projectShortName = "";
	private int relatedProjectId;
	private String relatedProjectShortName = "";
	private String relatedProjectName = "";
	private String relatedProjectDescription = "";
	private String relationMadeBy = "";
	private String relationMadeDt = "";
	private String relationDescription = "";
	private Project relatedProject;
	
	public ProjectRelation (int id, int projectId,String projectShortName, int relatedProjectId, String relatedProjectShortName, String relatedProjectName,
			String relatedProjectDescription,
			String relationMadeBy, String relationMadeDt, String relationDescription, Project relatedProject) {
		this.id = id;
		this.projectId  = projectId;
		this.projectShortName = projectShortName;
		this.relatedProjectId = relatedProjectId;
		this.relatedProjectShortName = relatedProjectShortName;
		this.relatedProjectDescription = relatedProjectDescription;
		this.relatedProjectName = relatedProjectName;
		this.relationMadeBy = relationMadeBy;
		this.relationMadeDt = relationMadeDt;
		this.relationDescription = relationDescription;
		this.relatedProject = relatedProject;
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
		
	
	public String getRelationMadeBy(){
		return this.relationMadeBy;
	}
	
	public String getRelationMadeDt(){
		return this.relationMadeDt;
	}
	
	public String getRelationDescription(){
		return this.relationDescription;
	}

	public Project getRelatedProject(){
		return this.relatedProject;
	}
}
