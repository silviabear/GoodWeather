package edu.illinois.cs425_mp1.network;

import edu.illinois.cs425_mp1.types.Reply;
import edu.illinois.cs425_mp1.types.Request;

/**
 * Generic Wrapper of Broadcast Sender
 * Created by Wesley on 8/31/15.
 */
public class BroadcastSender implements Sender {
	
	private String[] neighbors;
	private String selfAddress;
	private int uniPort;
	
	public BroadcastSender(String[] neighbors, String selfAddress, int unitPort) {
		
	}

}
