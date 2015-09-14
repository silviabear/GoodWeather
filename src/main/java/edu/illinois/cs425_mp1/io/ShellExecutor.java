package edu.illinois.cs425_mp1.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShellExecutor {
	private final static Logger log = LogManager.getLogger("shellLogger");
	public static String execute(String command) {
		StringBuffer output = new StringBuffer();
		Process p;
		try {
			log.trace("shell execute: " + command);
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			log.trace("shell created buffer");
			String line = "";
			log.trace("shell start appending to buffer");
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();
	}
}
