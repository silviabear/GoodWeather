package edu.illinois.cs425_mp1.parser;

import edu.illinois.cs425_mp1.io.ShellExecutor;
import edu.illinois.cs425_mp1.types.LogCommand;
import edu.illinois.cs425_mp1.types.LogReply;
import edu.illinois.cs425_mp1.types.LogRequest;
import edu.illinois.cs425_mp1.types.Reply;
import edu.illinois.cs425_mp1.types.Request;

import org.joda.time.DateTime;

/**
 * Receive the request from network and parse the execution results.
 * 
 * @author silvia
 * 
 */
public class NetworkRequestParser extends Parser {
	/**
	 * 
	 * @param request
	 * @return the wrapped reply with timestamp and reply message
	 */
	public static Reply acceptNetworkRequest(Request request) {
		if(request instanceof LogRequest) {
			return parseLogRequest((LogRequest)request);
		}
		return null;
	}
	
	public static void acceptNetworkReply(Reply reply) {
		
	}
	
	private static LogReply parseLogRequest(LogRequest request) {
		LogCommand c = request.getCommand();
		switch(c) {
		case GREP: return new LogReply(LogCommand.GREP, ShellExecutor.execute(request.getBody()));
		}
		return null;
	}
	
}
