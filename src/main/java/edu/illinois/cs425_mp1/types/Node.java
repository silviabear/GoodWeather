package edu.illinois.cs425_mp1.types;

import java.io.Serializable;

import org.joda.time.DateTime;

public class Node implements Serializable {
	//Address of host
	final String host;
	//Latest update time
	private DateTime timeStamp;
	private NodeStatus status;
	public Node(String host, NodeStatus status) {
		this.host = host;
		this.status = status;
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
	
	public void setTimeStamp(DateTime timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public NodeStatus getStatus() {
		return status;
	}
	
}
