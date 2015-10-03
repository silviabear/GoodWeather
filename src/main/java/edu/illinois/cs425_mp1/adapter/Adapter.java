package edu.illinois.cs425_mp1.adapter;

import java.net.InetAddress;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.illinois.cs425_mp1.monitor.HeartbeatAdapter;
import edu.illinois.cs425_mp1.monitor.HeartbeatBroadcaster;
import edu.illinois.cs425_mp1.network.Listener;
import edu.illinois.cs425_mp1.network.P2PSender;
import edu.illinois.cs425_mp1.types.NodeStatus;
import edu.illinois.cs425_mp1.types.Request;
import edu.illinois.cs425_mp1.types.Node;
import edu.illinois.cs425_mp1.ui.Console;

/**
 * Now only support one-to-one UI and adapter
 * 
 * @author silvia
 *
 */
final public class Adapter {
	
	private final static Node self;

	private static String localhost;
	
	private Listener requestListener = null;
	
	private Thread mainLoop = null;
	
	private HeartbeatAdapter heartbeatAdaper = null;
	
	private static Console console = null;
	
	private Logger log = LogManager.getLogger("adapterLogger");
	
	// IP addresses of all neighbors
	private final static String[] addresses = new String[]{
		"172.22.151.53",
		"172.22.151.53",
		"172.22.151.54",
		"172.22.151.55",
		"172.22.151.56",
		"172.22.151.57",
		"172.22.151.58",
	};
	
	
	
	private static final P2PSender[] channels = new P2PSender[addresses.length];
	
	static {
		//Get localhost value
		try {
			localhost = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			System.out.println("fail to inititate local node");
			System.exit(1);
		}
		self = new Node(localhost, NodeStatus.ACTIVE);
	}
	
	/**
	 * Constructor method
	 * @param port the port local listener should bind to, better unified among nodes
	 */
	public Adapter(int port) {
		requestListener = new Listener(port);
		mainLoop = new Thread() {
			public synchronized void run() {
				log.trace("run");
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
	 * Broadcast a request to all alive neighbors
	 * 
	 * @param request the request to send
	 */
	public void sendBroadcastRequest(Request request) {
		for(int i = 0; i < addresses.length; i++) {
			sendP2PRequest(request, i + 1);
		}
	}
	
	/**
	 * Send the request to a specific node, if send to itself, 
	 * take no effect
	 * @param request the request to send
	 * @param node 	  the number of node, start from 1
	 */
	public void sendP2PRequest(Request request, int node) {
		if(channels[node - 1] == null) {
			channels[node - 1] = new P2PSender(addresses[node - 1], Console.port);
			channels[node - 1].run();
		}
		channels[node - 1].send(request);
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
	
	public static String[] getNeighbors() {
		return addresses;
	}
	
	public static String getLocalAddress() {
		return localhost;
	}
	
	public static Console getConsole() {
		return console;
	}
	
	public static P2PSender[] getChannels() {
		return channels;
	}
	
	public void leaveGroup() {
	}
	
	public void joinGtoup() {
		
	}
}
