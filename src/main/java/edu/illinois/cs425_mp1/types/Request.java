package edu.illinois.cs425_mp1.types;

import java.io.Serializable;

abstract public class Request extends Message implements Serializable {
	
	static final long serialVersionUID = 1L;
	
	public Request(String body) {
		super(body);
	}
	
	public Request() {
	}
	
}
