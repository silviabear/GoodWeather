package edu.illinois.cs425_mp1.monitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.illinois.cs425_mp1.adapter.Adapter;
import edu.illinois.cs425_mp1.types.MembershipList;
import edu.illinois.cs425_mp1.types.Node;
import edu.illinois.cs425_mp1.types.NodeStatus;

public class HeartbeatAdapter implements Runnable {
	
	final static MembershipList membershipList = new MembershipList();
	
	private Thread broadcaster = null;
	
	private Thread examiner = null;
	
	private Logger log = LogManager.getLogger("heartbeatLogger");
	
	static {
		// Inititate self status as ACTIVE, otherwise unknown
		int i = 0;
		for (String address : Adapter.getNeighbors()) {
			NodeStatus status = NodeStatus.NONE;
			if(Adapter.getLocalAddress().equals(address)) {
				status = NodeStatus.ACTIVE;
				membershipList.setSelfIndex(i);
			}
			membershipList.add(new Node(address, status), i);
			i++;
		}
	}

	public HeartbeatAdapter() {
		broadcaster = new Thread(new HeartbeatBroadcaster());
		examiner = new Thread(new HeartbeatExaminer());
	}
	
	public void run() {
		log.trace("heartbeat adapter runing");
		broadcaster.start();
		examiner.start();
	}
	
	/**
	 * UDP listener for heartbeat should call this
	 * Use asynchronized IO to prevent blocking
	 * 
	 * @param membershipList The membership list sent by any other node
	 */
	//TODO: @Wesly: parse received membership list directly to this method
	public void acceptHeartbeat(MembershipList update) {
		for(Integer nodeId : update) {
			HeartbeatAdapter.membershipList.updateNeighborInfo(nodeId, update.getNode(nodeId));
		}
	}
	
	public static MembershipList getMembershipList() {
		return membershipList;
	}
	
	public static void leaveGroup() {
		HeartbeatBroadcaster.broadcastLeave();
	}
	
	public static void joinGroup() {
		HeartbeatBroadcaster.broadcastJoin();
	}

}
