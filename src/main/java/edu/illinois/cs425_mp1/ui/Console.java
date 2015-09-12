package edu.illinois.cs425_mp1.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.illinois.cs425_mp1.exceptions.RemoteAddressClosedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.illinois.cs425_mp1.adapter.Adapter;
import edu.illinois.cs425_mp1.network.BroadcastSender;
import edu.illinois.cs425_mp1.network.P2PSender;
import edu.illinois.cs425_mp1.types.LogCommand;
import edu.illinois.cs425_mp1.types.LogRequest;

/**
 * This is the console
 * Created by Wesley on 8/30/15.
 */
public class Console {

	static Logger log = LogManager.getLogger("mainLogger");
	private static Adapter adapter;
	//port for listener is consistent universally
	private static final int port = 6753;
	
	public static void main(String[] args) {
		log.info("Start Console init...");
		Console c = new Console();
		adapter = new Adapter(port);
		adapter.registerUI(c);
		log.info("Console init finished");
		c.start();
	}
	
	private void start() {
		String line = null;
		while(true) {
			System.out.println("Choose the num of operation:");
			System.out.println("1. Query log. 0. Exit");
			line = read();
			int num = parseNum(line);
			switch(num) {
			case -1: System.out.println("Please enter a valid number.");
					continue;
			case 0: System.exit(0);
			case 1: logMenu();
					break;
			default: System.out.println("Invalid option");
					continue;
			}
		}
	}
	
	private String read() {
		try {
			BufferedReader br = 
                      new BufferedReader(new InputStreamReader(System.in));
			String input;
			if((input = br.readLine())!= null){
				return input;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Error";
	}
	
	private static int parseNum(String line) {
		try {
			int num = Integer.valueOf(line);
			return num;
		} catch (Exception e) {
			//System.out.println("Invalid num");
			return -1;
		}
	}

	private int logMenu() {
		System.out.println("Please enter the num of query:");
		System.out.println("1. grep 0. Return to main menu");
		String line = read();
		int num = parseNum(line);
		switch(num){
			case -1:System.out.println("Please enter a valid number");
					return logMenu();
			case 0: return 0;
			case 1: System.out.println("Please enter the argument after grep:");
					System.out.println("For example: \"-c blah\", no need to specify file name");
					line = read();
					String arg = line;
					System.out.println("Please enter ip address of operation, 0 if broadcast");
					line = read();
					if(parseNum(line) != -1) {
						handleLogRequest(LogCommand.GREP, arg, null);
					} else {
						handleLogRequest(LogCommand.GREP, arg, line);
					}
					return 0;
			default: return -1;
		}
	}
	
	private int handleLogRequest(LogCommand command, String request, String address) {
		LogRequest r = new LogRequest(command, request);
		if(address == null) {
			BroadcastSender sender = new BroadcastSender(adapter.getNeighbors(), port);
			sender.send(r);
		} else {
			P2PSender sender = new P2PSender(address, port);
			try {
				sender.run();
				sender.send(r);
			} catch (RemoteAddressClosedException e){
				// TODO: Re-init sender
			}
		}
		return 0;
	}
	
	/**
	 * Callback for adapter when new message received
	 * 
	 * @param s
	 */
	public void print(String s) {
		System.out.println(s);
	}
}
