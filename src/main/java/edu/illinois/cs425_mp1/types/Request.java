package edu.illinois.cs425_mp1.types;

import java.io.Serializable;

public class Request extends Message implements Serializable {
	
	static final long serialVersionUID = 1L;
	
	protected static long idCounter = 0;
	
	private final long id;
	
	public Request(Command c, String body) {
		super(body, c);
		id = idCounter++;
	}
	
	public long getRequestId() {
		return id;
	}
	
}
