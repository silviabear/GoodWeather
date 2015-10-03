package edu.illinois.cs425_mp1.monitor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.illinois.cs425_mp1.adapter.Adapter;
import edu.illinois.cs425_mp1.types.MembershipList;
import edu.illinois.cs425_mp1.types.Node;
import edu.illinois.cs425_mp1.types.NodeStatus;

public class HeartbeatAdapter implements Runnable {
	
	final static MembershipList membershipList = new MembershipList();
	
	//Maximum take 1000 waiting membershipList to be updated,
	//Use FIFO queue
	final static BlockingQueue<MembershipList> membershipQueue = new LinkedBlockingQueue<MembershipList>(1000);
	
	private Runnable broadcaster = null;
	
	private Runnable examiner = null;

	private Runnable updater = null;
	
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
		updater = new HeartbeatUpdater();
	}
	
	public void run() {
		broadcaster.run();
		examiner.run();
		updater.run();
	}
	
	/**
	 * UDP listener for heartbeat should call this
	 * Use asynchronized IO to prevent blocking
	 * 
	 * @param membershipList The membership list sent by any other node
	 */
	//TODO: @Wesly: parse received membership list directly to this method
	public void acceptHeartbeat(MembershipList membershipList) {
		membershipQueue.offer(membershipList);
	}
	
	/**
	 * Translate ip addresses to node indexed, system dependent implementation
	 * 
	 * @param addr the address of a node
	 * @return the node index
	 */
	public static int addressToNodeIndex(String addr) {
		String[] nums = addr.split(".");
		return Integer.parseInt(nums[3]) - 51;
	}
	
	public static String nodeIndexToAddress(int index) {
		return "172.22.151." + (new Integer(52 + index));
	}
	
	public static MembershipList getMembershipList() {
		return membershipList;
	}

}
