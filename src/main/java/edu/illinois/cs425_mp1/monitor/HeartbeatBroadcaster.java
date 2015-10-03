package edu.illinois.cs425_mp1.monitor;

import java.util.List;

import org.joda.time.DateTime;

import edu.illinois.cs425_mp1.adapter.Adapter;
import edu.illinois.cs425_mp1.types.Node;

public class HeartbeatBroadcaster implements Runnable {
	
	private final static int totalNumNode = HeartbeatAdapter.membershipList.size();
	
	//Means among broadcastRate of neighbors, randomly pick one for broadcasting
	private final static int broadcastRate = 2; 
	
	//Broadcast frequency in ms
	private final static long sleepInterval = 200;
	
	public void run() {
		while(true) {
			try {
				Thread.sleep(sleepInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int index = HeartbeatAdapter.addressToNodeIndex(Adapter.getLocalAddress());
			Node self = HeartbeatAdapter.membershipList.get(index);
			self.setTimeStamp(new DateTime());
			synchronized(HeartbeatAdapter.membershipList) {
				//TODO @Wesley create UDP send interface
			}
		}
		
	}
	
	public static void broadCastLeave(List<Node> membership) {
		
	}
	
}
