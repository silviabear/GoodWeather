package edu.illinois.cs425_mp1.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.illinois.cs425_mp1.adapter.Adapter;
import edu.illinois.cs425_mp1.types.Node;
import edu.illinois.cs425_mp1.types.NodeStatus;

public class HeartbeatAdapter implements Runnable {
	
	final static List<Node> membershipList = Collections.synchronizedList(new ArrayList<Node>());
	
	//Maximum take 1000 waiting membershipList to be updated,
	//Use FIFO queue
	final static BlockingQueue<List<Node>> membershipQueue = new LinkedBlockingQueue<List<Node>>(1000);
	
	private Runnable broadcaster = null;
	
	private Runnable examiner = null;

	private Runnable updater = null;
	
	static {
		// Inititate self status as ACTIVE, otherwise unknown
		for (String address : Adapter.getNeighbors()) {
			NodeStatus status = Adapter.getLocalAddress().equals(address) ? 
					NodeStatus.ACTIVE: NodeStatus.NONE;
			membershipList.add(new Node(address, status));
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
	public void acceptHeartbeat(List<Node> membershipList) {
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

}
