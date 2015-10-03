package edu.illinois.cs425_mp1.monitor;

import java.util.List;

import edu.illinois.cs425_mp1.adapter.Adapter;
import edu.illinois.cs425_mp1.types.Node;

public class HeartbeatUpdater implements Runnable {

	public void run() {
		
		while(true) {
			List<Node> update = HeartbeatAdapter.membershipQueue.poll();
			for(Node node : update) {
				String addr = node.getAddress();
				if(!addr.equals(Adapter.getLocalAddress())) {
					int index = HeartbeatAdapter.addressToNodeIndex(addr);
					Node neighbor = HeartbeatAdapter.membershipList.get(index);
					if(neighbor.getTimeStamp().compareTo(node.getTimeStamp()) < 0) {
						HeartbeatAdapter.membershipList.set(index, node);
					}
				}
			}
		}
	}
}
