package edu.illinois.cs425_mp1.types;

import java.io.Serializable;

public abstract class Message implements Serializable {

	static final long serialVersionUID = 1L;
	
	protected final String body;
	public Message(String body) {
		this.body = body;
	}
	public String getBody() {
		return this.body;
	}
	
	public Message() {
		body = null;
	}


}
