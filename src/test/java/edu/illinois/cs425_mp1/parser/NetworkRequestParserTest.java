package edu.illinois.cs425_mp1.parser;

import edu.illinois.cs425_mp1.types.Command;
import edu.illinois.cs425_mp1.types.Request;
import edu.illinois.cs425_mp1.types.Reply;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;

public class NetworkRequestParserTest {

	@Test
	public void testGrep() {
		DateTime before = new DateTime();
		Reply reply = NetworkRequestParser.acceptNetworkRequest(
				new Request(Command.GREP, "grep artifactId pom.xml"));
		long diff = reply.getTimeStamp().getMillis() - before.getMillis();
		assertTrue(diff < 1000);
		assertTrue(reply.getBody().contains("cs425-mp1"));
	}
}
