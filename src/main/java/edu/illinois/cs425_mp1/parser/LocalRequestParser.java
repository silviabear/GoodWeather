package edu.illinois.cs425_mp1.parser;

import edu.illinois.cs425_mp1.types.LogCommand;
import edu.illinois.cs425_mp1.types.LogReply;
import edu.illinois.cs425_mp1.types.Reply;
import edu.illinois.cs425_mp1.types.Request;

import org.joda.time.DateTime;

/**
 * Receive the request from Ui and parse execution results
 * @author silvia
 *
 */
public class LocalRequestParser extends Parser {
	
	/**
	 * 
	 * @param request
	 * @return reply from network
	 */
	public static Reply acceptLocalRequest(Request request) {
		return new LogReply(LogCommand.GREP, "");
	}
}
