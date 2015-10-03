package edu.illinois.cs425_mp1.monitor;

import edu.illinois.cs425_mp1.types.MembershipList;

public class HeartbeatUpdater implements Runnable {

	public void run() {
		
		while(true) {
			MembershipList update = HeartbeatAdapter.membershipQueue.poll();
			for(Integer nodeId : update) {
				HeartbeatAdapter.membershipList.updateNeighborInfo(nodeId, update.getNode(nodeId));
			}
		}
	}
}
