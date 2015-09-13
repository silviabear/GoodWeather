package edu.illinois.cs425_mp1.types;

import java.io.Serializable;

import org.joda.time.DateTime;

public abstract class Message implements Serializable {

	static final long serialVersionUID = 1L;
	
	protected final String body;
	protected final DateTime timeStamp;
	protected final Command command;
	
	public Message(String body, Command command) {
		this.body = body;
		this.command = command;
		timeStamp = new DateTime();
	}
	public String getBody() {
		return this.body;
	}
	
	public DateTime getTimeStamp() {
		return this.timeStamp;
	}
	
	public Command getCommand() {
		return this.command;
	}


}
