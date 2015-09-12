package edu.illinois.cs425_mp1.parser;

import edu.illinois.cs425_mp1.adapter.Adapter;
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
public class NetworkMessageParser extends Parser {
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
		if(reply instanceof LogReply) {
			Adapter.getConsole().print("QUERY RESULT FROM: " + reply.getReplierAddress());
			Adapter.getConsole().print("FETCH TIME: " + reply.getTimeStamp());
			Adapter.getConsole().print("LOG: " + reply.getBody());
		}
	}
	
	private static LogReply parseLogRequest(LogRequest request) {
		LogCommand c = request.getCommand();
		switch(c) {
		case GREP: return new LogReply(ShellExecutor.execute("grep " + request.getBody()),
				request.getRequestId(),
				Adapter.getLocalAddress());
		}
		return null;
	}
	
}
