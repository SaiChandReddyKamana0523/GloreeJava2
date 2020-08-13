package com.gloree.beans;

public class ResourceTimeline {

	private int projectId ;
	private String resourceFullTag;
	private String allocationDate;
	private int percentAllocated;
	
	public ResourceTimeline (int projectId ,  String resourceFullTag, String allocationDate, int percentAllocated) {
		
		this.projectId = projectId ;
		this.resourceFullTag = resourceFullTag;
		this.allocationDate = allocationDate ;
		this.percentAllocated = percentAllocated;
	}
	
	
	public int getProjectId(){
		return (this.projectId);
	}	
	
	
	public String getResourceFullTag(){
		return (this.resourceFullTag);
	}
	
	public String getAllocationDate(){
		return (this.allocationDate);
	}
	
	public int getPercentAllocated(){
		return (this.percentAllocated);
	}	
	
	
	
}
