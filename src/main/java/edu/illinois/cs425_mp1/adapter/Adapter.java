package edu.illinois.cs425_mp1.adapter;

import java.net.InetAddress;

final public class Adapter {

	private static String localhost;
	
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
	
	public Adapter(int port) {
		
	}
	
	public String[] getNeighbors() {
		return addresses;
	}
	
	public String getLocalAddress() {
		return localhost;
	}
	
}
