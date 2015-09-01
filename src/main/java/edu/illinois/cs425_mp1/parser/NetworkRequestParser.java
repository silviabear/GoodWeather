package edu.illinois.cs425_mp1.parser;

import edu.illinois.cs425_mp1.io.ShellExecutor;
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
		String replyContent = null;
		switch(request.getCommand()) {
		case GREP: replyContent = ShellExecutor.execute(request.getBody());
					break;
		}
		return new Reply(new DateTime(), replyContent);
	}
	
}
