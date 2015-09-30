package edu.illinois.cs425_mp1.types;

import org.joda.time.DateTime;

public class Node {
	final String host;
	private long counter;
	private DateTime timeStamp;
	private NodeStatus status;
	public Node(String host) {
		this.host = host;
	}
	
	public String getAddress() {
		return host;
	}
	
	public DateTime getTimeStamp() {
		return timeStamp;
	}
	
	public void setStatus(NodeStatus status) {
		this.status = status;
	}
	
	public NodeStatus getStatus() {
		return status;
	}
	
}
