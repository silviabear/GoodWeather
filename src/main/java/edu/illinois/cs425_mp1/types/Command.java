package edu.illinois.cs425_mp1.types;

public enum Command {

	GREP("grep"), SHUTDOWN("shutdown"), WRITE("write"), READ("read");

	String cmd;
	Command(String stringcmd){
		this.cmd = stringcmd;
	}

	@Override
	public String toString(){
		return this.cmd;
	}
}
