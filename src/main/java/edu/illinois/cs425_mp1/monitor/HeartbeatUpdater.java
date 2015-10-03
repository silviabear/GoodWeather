package edu.illinois.cs425_mp1.monitor;

import java.util.List;

import edu.illinois.cs425_mp1.adapter.Adapter;
import edu.illinois.cs425_mp1.types.MembershipList;
import edu.illinois.cs425_mp1.types.Node;

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
