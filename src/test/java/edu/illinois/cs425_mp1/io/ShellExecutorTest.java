package edu.illinois.cs425_mp1.io;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class ShellExecutorTest {
	
	@Test
	public void TestNoParam() {
		String result = ShellExecutor.execute("ls");
		assertTrue(result.contains("src"));
		assertTrue(result.contains("pom.xml"));
	}
	
	@Test
	public void TestParam() {
		String result = ShellExecutor.execute("grep artifactId pom.xml");
		assertTrue(result.contains("cs425-mp1"));
	}
	
	// Bug to be solved: cannot throw exception if not valid
	// as it is in a forked process
	@Test
	public void TestInvalidCommand() {
		String result = ShellExecutor.execute("blah");
	}
	
	// Bug to be solved: cannot detect invalid param
	@Test
	public void TestInvalidParam() {
		String result = ShellExecutor.execute("grep -p");
		assertTrue(result.length() == 0);
	}
}
