package edu.illinois.cs425_mp1.types;

public enum LogCommand {

	GREP("grep");

	String cmd;
	LogCommand(String stringcmd){
		this.cmd = stringcmd;
	}

	@Override
	public String toString(){
		return this.cmd;
	}
}
