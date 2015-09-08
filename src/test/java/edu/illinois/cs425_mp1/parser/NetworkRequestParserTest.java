package edu.illinois.cs425_mp1.parser;

import edu.illinois.cs425_mp1.types.LogCommand;
import edu.illinois.cs425_mp1.types.LogReply;
import edu.illinois.cs425_mp1.types.LogRequest;
import edu.illinois.cs425_mp1.types.Request;
import edu.illinois.cs425_mp1.types.Reply;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;

public class NetworkRequestParserTest {

	@Test
	public void testGrep() {
		Reply reply = NetworkRequestParser.acceptNetworkRequest(
				new LogRequest(LogCommand.GREP, "grep artifactId pom.xml"));
		assertTrue(reply instanceof LogReply);
		assertTrue(reply.getBody().contains("cs425-mp1"));
	}
}
