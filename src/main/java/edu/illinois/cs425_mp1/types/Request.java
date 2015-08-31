package edu.illinois.cs425_mp1.types;

public class Request extends Message {
	public Request(Command c, String body) {
		super(body);
	}
}
