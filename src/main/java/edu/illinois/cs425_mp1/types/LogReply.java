package edu.illinois.cs425_mp1.types;

import java.io.Serializable;

public final class LogReply extends Reply implements Serializable {
	
	static final long serialVersionUID = 1L;
	
	private LogCommand c;
	
	public LogReply(LogCommand c, String body) {
		super(body);
		this.c = c;
	}
	
	public LogReply() {
		c = null;
	}
	
}
