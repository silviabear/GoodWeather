package edu.illinois.cs425_mp1.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.Request;

import edu.illinois.cs425_mp1.types.LogCommand;
import edu.illinois.cs425_mp1.types.LogReply;
import edu.illinois.cs425_mp1.types.LogRequest;
import edu.illinois.cs425_mp1.types.Reply;

public class ObjectStringSerializerTest {

	@Test
	public void SerizalizeRequest() throws ClassNotFoundException, IOException {
		LogRequest r = new LogRequest(LogCommand.GREP, "grep blah");
		String s = ObjectStringSerializer.objectToString(r);
		LogRequest r2 = (LogRequest)ObjectStringSerializer.stringToObject(s);
		assertEquals(LogCommand.GREP, r2.getCommand());
		assertEquals("grep blah", r2.getBody());
	}
	
	@Test
	public void SerializeReply() throws ClassNotFoundException, IOException {
		LogReply r = new LogReply(LogCommand.GREP, "blah");
		String s = ObjectStringSerializer.objectToString(r);
		Reply r2 = (Reply)ObjectStringSerializer.stringToObject(s);
		assertEquals("blah", r2.getBody());
	}
}
