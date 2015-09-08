package edu.illinois.cs425_mp1.types;

import java.io.Serializable;

final public class LogRequest extends Request implements Serializable {
	
	private LogCommand c;
	
	public LogRequest(LogCommand c, String body) {
		super(body);
		this.c = c;
	}
	
	public LogCommand getCommand() {
		return c;
	}
}
