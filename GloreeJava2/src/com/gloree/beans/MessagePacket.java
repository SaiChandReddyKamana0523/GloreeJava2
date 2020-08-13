package com.gloree.beans;

import java.util.ArrayList;

public class MessagePacket {

	private ArrayList to = new ArrayList ();
	private ArrayList cc = new ArrayList();
	private String subject = "";
	private String body = "";
	private String fileAttachmentPath = "";
	
	public MessagePacket (ArrayList to, ArrayList cc , String subject, String body, String fileAttachmentPath) {
		this.to = to;
		this.cc = cc;
		this.subject = subject;
		this.body = body;
		this.fileAttachmentPath = fileAttachmentPath;
	}
	
	public ArrayList getTo(){
		return this.to;
	}
	
	public ArrayList getCc(){
		return this.cc;
	}
	
	public String getSubject(){
		return this.subject;
	}
	
	public String getBody(){
		return this.body;
	}
	public String getFileAttachmentPath(){
		return this.fileAttachmentPath;
	}
	
}
