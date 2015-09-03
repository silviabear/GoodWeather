package edu.illinois.cs425_mp1.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.Test;

import edu.illinois.cs425_mp1.types.Command;
import edu.illinois.cs425_mp1.types.Reply;
import edu.illinois.cs425_mp1.types.Request;

public class ObjectStringSerializerTest {

	@Test
	public void SerizalizeRequest() throws ClassNotFoundException, IOException {
		Request r = new Request(Command.GREP, "grep blah");
		String s = ObjectStringSerializer.objectToString(r);
		Request r2 = (Request)ObjectStringSerializer.stringToObject(s);
		assertEquals(Command.GREP, r2.getCommand());
		assertEquals("grep blah", r2.getBody());
	}
	
	@Test
	public void SerializeReply() throws ClassNotFoundException, IOException {
		DateTime d = new DateTime();
		Reply r = new Reply(d, "blah");
		String s = ObjectStringSerializer.objectToString(r);
		Reply r2 = (Reply)ObjectStringSerializer.stringToObject(s);
		assertEquals(d.getMillis(), r2.getTimeStamp().getMillis());
		assertEquals("blah", r2.getBody());
	}
}
