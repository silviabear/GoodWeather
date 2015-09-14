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
	
	@Test
	public void testGrepCount() {
		Reply reply = NetworkMessageParser.acceptNetworkRequest(
				new Request(Command.GREP, "grep -c *.* pom.xml"));
		assertTrue(!reply.getBody().contains("#$@%42"));
	}
	
	@Test
	public void testGrepRegex() {
		Reply reply = NetworkMessageParser.acceptNetworkRequest(
				new Request(Command.GREP, "grep -c ^2015 logs/mp.log"));
		//assertTrue(reply.getBody().length() == 0);
	}
	
}
