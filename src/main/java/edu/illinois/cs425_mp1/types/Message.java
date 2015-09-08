package edu.illinois.cs425_mp1.types;

import java.io.Serializable;

import org.joda.time.DateTime;

public abstract class Message implements Serializable {

	static final long serialVersionUID = 1L;
	
	protected final String body;
	protected final DateTime timeStamp;
	
	public Message(String body) {
		this.body = body;
		timeStamp = new DateTime();
	}
	public String getBody() {
		return this.body;
	}
	
	public Message() {
		body = null;
		timeStamp = null;
	}

}
