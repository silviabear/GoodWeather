package edu.illinois.cs425_mp1.parser;

import edu.illinois.cs425_mp1.adapter.Adapter;
import edu.illinois.cs425_mp1.io.ShellExecutor;
import edu.illinois.cs425_mp1.types.Command;
import edu.illinois.cs425_mp1.types.Reply;
import edu.illinois.cs425_mp1.types.Request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Receive the request from network and parse the execution results.
 * 
 * @author silvia
 * 
 */
public class NetworkMessageParser {
	/**
	 * 
	 * @param request
	 * @return the wrapped reply with timestamp and reply message
	 */
	private static Logger log = LogManager.getLogger("parserLogger");
	public static Reply acceptNetworkRequest(Request request) {
		Command c = request.getCommand();
		if(c == Command.GREP) {
			log.trace("receive grep request");
			return new Reply(ShellExecutor.execute(request.getBody()),
					request.getRequestId(),
					Adapter.getLocalAddress(),
					Command.GREP
					);
		}
		return null;
	}
	
	public static void acceptNetworkReply(Reply reply) {
		Command c = reply.getCommand();
		if(c == Command.GREP) {
			Adapter.getConsole().print("QUERY RESULT FROM: " + reply.getReplierAddress());
			Adapter.getConsole().print("FETCH TIME: " + reply.getTimeStamp());
			Adapter.getConsole().print("LOG: " + reply.getBody());
		}
	}
	
}
