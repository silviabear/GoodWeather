package edu.illinois.cs425_mp1.types;

import java.io.Serializable;

public class Reply extends Message implements Serializable {
	
	static final long serialVersionUID = 1L;
	
	private long id;
	
	protected String replierAddress;
	
	public Reply(String body, long id, String replierAddress, Command c) {
		super(body, c);
		this.id = id;
		this.replierAddress = replierAddress;
	}
	
	public String getReplierAddress() {
		return this.replierAddress;
	}
	
}
