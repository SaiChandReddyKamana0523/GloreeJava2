package com.gloree.beans;

public class AllocationTimeline {

	private int projectId ;
	private String resourceFullTag;
	private String allocationFullTag;
	private String allocationName; 
	private String allocationDate;
	private int percentAllocated;
	
	public AllocationTimeline (int projectId ,  String resourceFullTag, String allocationFullTag, String allocationName, String allocationDate, int percentAllocated) {
		
		this.projectId = projectId ;
		this.resourceFullTag = resourceFullTag;
		this.allocationFullTag = allocationFullTag;
		this.allocationName = allocationName;
		this.allocationDate = allocationDate ;
		this.percentAllocated = percentAllocated;
	}
	
	
	public int getProjectId(){
		return (this.projectId);
	}	
	
	
	public String getResourceFullTag(){
		return (this.resourceFullTag);
	}
	
	
	
	
	
	public String getAllocationFullTag(){
		return (this.allocationFullTag);
	}
	
	

	public String getAllocationName(){
		return (this.allocationName);
	}
	
	
	public String getAllocationDate(){
		return (this.allocationDate);
	}
	
	public int getPercentAllocated(){
		return (this.percentAllocated);
	}	
	
	
	
}
