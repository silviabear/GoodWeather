package edu.illinois.cs425_mp1.types;

import java.io.Serializable;

abstract public class Reply extends Message implements Serializable {
	
	static final long serialVersionUID = 1L;
	
	public Reply(String body) {
		super(body);
	}
	
	public Reply() {
		
	}
}
