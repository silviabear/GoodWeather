package edu.illinois.cs425_mp1.types;

import java.io.Serializable;

public final class LogReply extends Reply implements Serializable {
	
	static final long serialVersionUID = 1L;

	public LogReply(String body, long rid, String replierAddress) {
		super(body, rid, replierAddress);
	}
	
	public LogReply() {
		
	}
	
}
