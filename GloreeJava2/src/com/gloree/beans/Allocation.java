package com.gloree.beans;

public class Allocation {

	private int projectId ;
	private String allocationFullTag;
	private String allocationName;
	private String programName;
	private String programFullTag;
	private String resourceName;
	private String resourceFullTag;
	
	private int percentAllocated;
	
	public Allocation (int projectId ,  String allocationFullTag, String allocationName,
				String programName, String programFullTag, String resourceName, String resourceFullTag, int percentAllocated) {
		
		this.projectId = projectId ;
		this.allocationFullTag = allocationFullTag;
		this.allocationName = allocationName;
		this.programName = programName;
		this.programFullTag = programFullTag;
		this.resourceName = resourceName;
		this.resourceFullTag = resourceFullTag;
		
		
		this.percentAllocated = percentAllocated;
	}
	
	
	public int getProjectId(){
		return (this.projectId);
	}	
	
	
	public String getAllocationFullTag(){
		return (this.allocationFullTag);
	}
	
	public String getAllocationName(){
		return (this.allocationName);
	}
	
	
	public String getProgramFullTag(){
		return (this.programFullTag);
	}
	
	public String getProgramName(){
		return (this.programName);
	}	
		
	public String getResourceFullTag(){
		return (this.resourceFullTag);
	}
	
	public String getResourceName(){
		return (this.resourceName);
	}

	public int getPercentAllocated(){
		return (this.percentAllocated);
	}	
	
	
	
}
