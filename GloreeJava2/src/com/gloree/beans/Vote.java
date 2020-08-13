package com.gloree.beans;

public class Vote {

	private int id;
	private String voterEmailId;
	private int requirementId;
	private int folderId;
	private int votesCast;
	private String voteDate;
	
	public Vote (int id ,  String voterEmailId, int requirementId, int folderId, int votesCast, String voteDate) {
		this.id = id;
		this.voterEmailId = voterEmailId;
		this.requirementId = requirementId;
		this.folderId = folderId;
		this.votesCast = votesCast;
		this.voteDate = voteDate;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getVoterEmailId() {
		return voterEmailId;
	}
	public void setVoterEmailId(String voterEmailId) {
		this.voterEmailId = voterEmailId;
	}
	public int getRequirementId() {
		return requirementId;
	}
	public void setRequirementId(int requirementId) {
		this.requirementId = requirementId;
	}
	public int getFolderId() {
		return folderId;
	}
	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}
	public int getVotesCast() {
		return votesCast;
	}
	public void setVotesCast(int votesCast) {
		this.votesCast = votesCast;
	}
	public String getVoteDate() {
		return voteDate;
	}
	public void setVoteDate(String voteDate) {
		this.voteDate = voteDate;
	}
	
}
