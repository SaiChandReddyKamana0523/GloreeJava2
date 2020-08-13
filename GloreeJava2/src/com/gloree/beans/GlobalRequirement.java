package com.gloree.beans;


// this is a wrapper bean on Requirement, and stored the project info along with requirement object.
public class GlobalRequirement {

	private int projectId;
	private String projectName ;
	private String projectPrefix;
	private String projectDescription;
	private Requirement requirement;

	

	public GlobalRequirement (int projectId, String projectName, String projectPrefix,
			String projectDescription, Requirement requirement){
		
		this.projectId = projectId;
		this.projectName  = projectName;
		this.projectPrefix = projectPrefix;
		this.projectDescription = projectDescription;
		this.requirement = requirement;
	}
	

	public int getProjectId(){
		return this.projectId;
	}

	public String getProjectName(){
		return this.projectName;
	}
	
	public String getProjectPrefix(){
		return this.projectPrefix;
	}
	
	public String getProjectDescription(){
		return this.projectDescription;
	}
	
	public Requirement getRequirement(){
		return this.requirement;
	}
}
