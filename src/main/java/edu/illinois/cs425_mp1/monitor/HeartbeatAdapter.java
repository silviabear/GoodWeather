package edu.illinois.cs425_mp1.monitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.illinois.cs425_mp1.adapter.Adapter;
import edu.illinois.cs425_mp1.network.UDPListener;
import edu.illinois.cs425_mp1.types.MembershipList;
import edu.illinois.cs425_mp1.types.Node;
import edu.illinois.cs425_mp1.types.NodeStatus;

import org.joda.time.DateTime;

public class HeartbeatAdapter implements Runnable {
	
	static MembershipList membershipList = new MembershipList();

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
		listenerThread.start();
		broadcasterThread.start();
		examinerThread.start();
	}
	
	/**
	 * UDP listener for heartbeat should call this
	 * Use asynchronized IO to prevent blocking
	 * 
	 * @param membershipList The membership list sent by any other node
	 */
	public static void acceptHeartbeat(MembershipList update) {
		for(Integer nodeId : update) {
			if(nodeId == HeartbeatAdapter.membershipList.getSelfId()) {
				continue;
			}
			Node oldStatus = HeartbeatAdapter.membershipList.getNode(nodeId);
			Node newStatus = update.getNode(nodeId);
			if(newStatus.getStatus() == NodeStatus.FAIL || newStatus.getTimeStamp() == null) {
				continue;
			}
			if(oldStatus.getTimeStamp() == null 
					|| oldStatus.getCounter() < newStatus.getCounter()) {
				HeartbeatAdapter.membershipList.getNode(nodeId).setTimeStamp(new DateTime());
				HeartbeatAdapter.membershipList.getNode(nodeId).setStatus(newStatus.getStatus());
			}
		}
	}
	
	public static MembershipList getMembershipList() {
		return membershipList;
	}
	
	public void leaveGroup() {
		broadcaster.broadcastLeave();
		broadcasterThread.interrupt();
	}
	
	public void joinGroup() {
		broadcaster.broadcastJoin();
		broadcasterThread = new Thread(broadcaster);
		broadcasterThread.start();
	}

}
