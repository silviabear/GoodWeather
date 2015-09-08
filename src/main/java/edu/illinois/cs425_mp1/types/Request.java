package edu.illinois.cs425_mp1.types;

import java.io.Serializable;

abstract public class Request extends Message implements Serializable {
	
	static final long serialVersionUID = 1L;
	
	protected static long idCounter = 0;
	
	private final long id;
	
	public Request(String body) {
		super(body);
		id = idCounter++;
	}
	
	public Request() {
		id = idCounter++;
	}
	
	public long getRequestId() {
		return id;
	}
	
}
