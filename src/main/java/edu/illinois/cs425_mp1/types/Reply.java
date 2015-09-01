package edu.illinois.cs425_mp1.types;

import org.joda.time.DateTime;

public class Reply extends Message {
	
	private final DateTime timestamp;
	
	public Reply(DateTime timeStamp, String body) {
		super(body);
		this.timestamp = timeStamp;
	}
	public DateTime getTimeStamp() {
		return this.timestamp;
	}

}
