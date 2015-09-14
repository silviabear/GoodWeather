package edu.illinois.cs425_mp1.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShellExecutor {
	private final static Logger log = LogManager.getLogger("shellLogger");
	public static String execute(String command) {
		Process p;
		String result = "";
		try {
			log.trace("shell execute: " + command);
			p = Runtime.getRuntime().exec(command);
			ShellStream s1 = new ShellStream(p.getInputStream ());
			ShellStream s2 = new ShellStream(p.getErrorStream ());
			s1.start();
			s2.start ();
			p.waitFor();
			s1.join();
			s2.join();
			log.trace("shell start appending to buffer");
			result = s1.output.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
class ShellStream extends Thread {
    
	InputStream is;
    Thread thread;
    StringBuffer output = new StringBuffer();
    public ShellStream(InputStream is) {
        this.is = is;
    }       
    
    public void run () {
        try {
            InputStreamReader isr = new InputStreamReader (is);
            BufferedReader br = new BufferedReader (isr);   
            while (true) {
                String line = br.readLine ();
                if (line == null) break;
                output.append(line + "\n");
            }
            is.close ();    
        } catch (Exception ex) {
            ex.printStackTrace ();
        }
    }
}