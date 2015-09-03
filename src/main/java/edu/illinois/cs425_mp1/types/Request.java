package edu.illinois.cs425_mp1.types;

import java.io.Serializable;

public class Request extends Message implements Serializable {
	
	static final long serialVersionUID = 1L;
	
	private final Command c;
	
	public Request(Command c, String body) {
		super(body);
		this.c = c;
	}
	
	public Request() {
		c = null;
	}
	
	public Command getCommand() {
		return this.c;
	}
}
