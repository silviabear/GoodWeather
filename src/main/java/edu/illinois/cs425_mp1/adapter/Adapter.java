package edu.illinois.cs425_mp1.adapter;

import java.net.InetAddress;

import edu.illinois.cs425_mp1.network.Listener;
import edu.illinois.cs425_mp1.parser.LocalMessageParser;
import edu.illinois.cs425_mp1.types.Request;
import edu.illinois.cs425_mp1.ui.Console;

/**
 * Now only support one-to-one UI and adapter
 * 
 * @author silvia
 *
 */
final public class Adapter {

	private static String localhost;
	
	private Listener requestListener = null;
	
	private Thread mainLoop = null;
	
	private static Console console = null;
	
	static {
		try {
			InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			System.out.println("fail to inititate local node");
			System.exit(1);
		}
	}
	
	private final static String[] addresses = new String[]{
		"172.22.151.53",
	};
	
	/**
	 * Constructor method
	 * @param port the port local listener should bind to, better unified among nodes
	 */
	public Adapter(int port) {
		requestListener = new Listener(port);
		mainLoop = new Thread() {
			@Override 
			public void run() {
				try {
					requestListener.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		mainLoop.start();
	}
	
	/**
	 * Broadcast a request to all alive neighbor
	 * 
	 * @param request the request to send
	 */
	public void sendBroadcastRequest(Request request) {
		LocalMessageParser.acceptBroadcastRequest(request);
	}
	
	/**
	 * Client should call this when stop adapter
	 */
	public void close() {
		try {
			requestListener.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mainLoop.interrupt();
	}
	
	/**
	 * Register the user interface to be notified when new reply received
	 * @param c the Console instance to be registered
	 */
	public void registerUI(Console c) {
		console = c;
	}
	
	public String[] getNeighbors() {
		return addresses;
	}
	
	public static String getLocalAddress() {
		return localhost;
	}
	
	public static Console getConsole() {
		return console;
	}
	
}
