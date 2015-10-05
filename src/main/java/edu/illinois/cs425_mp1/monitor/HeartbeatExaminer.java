package edu.illinois.cs425_mp1.monitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import edu.illinois.cs425_mp1.types.Node;
import edu.illinois.cs425_mp1.types.NodeStatus;

public class HeartbeatExaminer implements Runnable {

	//Time intervals in ms
	private final long sleepInterval = 100;
	private final long failInterval = 4000;
	
	private Logger log = LogManager.getLogger("heartbeatLogger");
	
	public void run() {
		log.trace("Examiner runs");
		while (true) {
			try {
				Thread.sleep(sleepInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			for (Integer index : HeartbeatAdapter.membershipList) {
				DateTime time = new DateTime();
				Node member = HeartbeatAdapter.membershipList.getNode(index);
				NodeStatus status = member.getStatus();
				if(status == NodeStatus.NONE) {
					HeartbeatAdapter.membershipList.aliveNeighbors.add(index);
				}
				if (status == NodeStatus.NONE
						|| index == HeartbeatAdapter.membershipList.getSelfId()) {
					continue;
				}
				if (time.getMillis() - member.getTimeStamp().getMillis() > failInterval) {
					log.trace(member.getAddress() + " seems failed");
					member.setStatus(NodeStatus.FAIL);
				} else {
					member.setStatus(NodeStatus.ACTIVE);
				}
				HeartbeatAdapter.membershipList.updateNeighborInfo(index,
						member);
			}

		}
	}

}
