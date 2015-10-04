package edu.illinois.cs425_mp1.monitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.illinois.cs425_mp1.adapter.Adapter;
import edu.illinois.cs425_mp1.network.UDPListener;
import edu.illinois.cs425_mp1.types.MembershipList;
import edu.illinois.cs425_mp1.types.Node;
import edu.illinois.cs425_mp1.types.NodeStatus;

public class HeartbeatAdapter implements Runnable {
	
	final static MembershipList membershipList = new MembershipList();

	public final static int port = 6754;
	
	private Thread broadcasterThread = null;
	
	private HeartbeatBroadcaster broadcaster = null;
	
	private Thread examinerThread = null;
	
	private HeartbeatExaminer examiner = null;
	
	private Thread listenerThread = null;
	
	private UDPListener listener;
	
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
		broadcaster = new HeartbeatBroadcaster();
		examiner = new HeartbeatExaminer();
		broadcasterThread = new Thread(broadcaster);
		examinerThread = new Thread(examiner);
		listener = new UDPListener(port);
	}
	
	public void run() {
		log.trace("heartbeat adapter runing");
		listenerThread = new Thread() {
			@Override
			public void run() {
				log.trace("heartbeat listener starts");
				listener.run();
			}
		};
		broadcasterThread.start();
		examinerThread.start();
		listenerThread.start();
	}
	
	/**
	 * UDP listener for heartbeat should call this
	 * Use asynchronized IO to prevent blocking
	 * 
	 * @param membershipList The membership list sent by any other node
	 */
	//TODO: @Wesly: parse received membership list directly to this method
	public static void acceptHeartbeat(MembershipList update) {
		for(Integer nodeId : update) {
			HeartbeatAdapter.membershipList.updateNeighborInfo(nodeId, update.getNode(nodeId));
		}
	}
	
	public static MembershipList getMembershipList() {
		return membershipList;
	}
	
	public void leaveGroup() {
		broadcaster.broadcastLeave();
	}
	
	public void joinGroup() {
		broadcaster.broadcastJoin();
	}

}
