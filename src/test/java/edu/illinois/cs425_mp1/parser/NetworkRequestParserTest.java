package edu.illinois.cs425_mp1.parser;

import edu.illinois.cs425_mp1.types.Command;
import edu.illinois.cs425_mp1.types.Request;
import edu.illinois.cs425_mp1.types.Reply;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class NetworkRequestParserTest {

	@Test
	public void testGrep() {
		Reply reply = NetworkMessageParser.acceptNetworkRequest(
				new Request(Command.GREP, "grep artifactId pom.xml"));
		assertTrue(reply.getBody().contains("cs425-mp1"));
	}
}
