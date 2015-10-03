package edu.illinois.cs425_mp1.monitor;

import java.util.List;

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
		}
		
	}
	
	public static void broadCastLeave(List<Node> membership) {
		
	}
	
}
