package edu.illinois.cs425_mp1.types;

public abstract class Message {
	
	protected Command c;
	protected String body;
	public Message(String body) {
		this.body = body;
	}
	public String getBody() {
		return this.body;
	}

}
