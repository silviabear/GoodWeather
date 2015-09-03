package edu.illinois.cs425_mp1.types;

import java.io.Serializable;

import org.joda.time.DateTime;

public class Reply extends Message implements Serializable {
	
	static final long serialVersionUID = 1L;
	
	private final DateTime timestamp;
	
	public Reply(DateTime timeStamp, String body) {
		super(body);
		this.timestamp = timeStamp;
	}
	
	public Reply() {
		timestamp = new DateTime();
	}
	
	public DateTime getTimeStamp() {
		return this.timestamp;
	}

}
