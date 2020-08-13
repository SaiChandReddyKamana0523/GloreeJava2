package com.gloree.beans;


public class TraceTreeRow {

	private int level ;
	private int tracesToSuspectRequirement;
	private int traceId;
	private String traceDescription;
	private Requirement requirement;
	private String traceFromFulllTag;
	private String traceToFullTag;
	
	// The following method is called when the project core values are known and the system is only
	// interested in them. 
	public TraceTreeRow (int level , int tracesToSuspectRequirement , int traceId, String traceDescription, Requirement requirement){
		
		this.level = level;
		this.tracesToSuspectRequirement = tracesToSuspectRequirement ;
		this.traceId = traceId;
		this.traceDescription = traceDescription;
		this.requirement = requirement;
		
	}
	

	public int getLevel(){
		return this.level;
	}

	public int getTracesToSuspectRequirement(){
		return this.tracesToSuspectRequirement;
	}
	
	
	
	public int getTraceId(){
		return this.traceId;
	}
	
	
	public String getTraceDescription(){
		return this.traceDescription;
	}

	public String getTraceDescriptionWithSafetyInSingleQuotes(){
		String traceDescriptionWithSafetyInSingleQuotes = this.traceDescription;
		if ((traceDescriptionWithSafetyInSingleQuotes != null) && (traceDescriptionWithSafetyInSingleQuotes.contains("'"))){
			traceDescriptionWithSafetyInSingleQuotes = traceDescriptionWithSafetyInSingleQuotes.replace("'", "\"");
		}
		return traceDescriptionWithSafetyInSingleQuotes;
	}

	public Requirement getRequirement(){
		return this.requirement;
	}
	
	
	public String getTraceFromFullTag(){
		return this.traceFromFulllTag;
	}
	
	

	public String getTraceToFullTag(){
		return this.traceToFullTag;
	}
}
