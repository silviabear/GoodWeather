package edu.illinois.cs425_mp1.monitor;

import java.util.List;
import java.util.Random;

import org.joda.time.DateTime;

import edu.illinois.cs425_mp1.adapter.Adapter;
import edu.illinois.cs425_mp1.types.Node;
import edu.illinois.cs425_mp1.types.NodeStatus;

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
			HeartbeatAdapter.membershipList.updateSelfTimeStamp();
			broadcast();
		}
		
	}
	
	private static void broadcast() {
		synchronized(HeartbeatAdapter.membershipList) {
			Random r = new Random();
			for(int i = 0; i < totalNumNode / broadcastRate; i++) {
				int index = r.nextInt() % totalNumNode;
				String addr = HeartbeatAdapter.nodeIndexToAddress(index);
				//TODO @Wesley create UDP send interface
			}
		}
	}
	
	public static void broadcastLeave() {
		HeartbeatAdapter.membershipList.updateSelfTimeStamp();
		HeartbeatAdapter.membershipList.updateSelfStatus(NodeStatus.LEAVE);
		broadcast();
	}
	
	public static void broadcastJoin() {
		HeartbeatAdapter.membershipList.updateSelfTimeStamp();
		HeartbeatAdapter.membershipList.updateSelfStatus(NodeStatus.ACTIVE);
		broadcast();
	}
	
}
