package edu.illinois.cs425_mp1.types;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.joda.time.DateTime;


public class MembershipList implements Iterable<Integer>{

	final Map<Integer, Node> membershipList = Collections.synchronizedMap(new HashMap<Integer, Node>());
	private int selfIndex;
	
	public void setSelfIndex(int selfIndex) {
		this.selfIndex = selfIndex;
	}
	
	public void updateSelfTimeStamp() {
		Node self = membershipList.get(selfIndex);
		self.setTimeStamp(new DateTime());
		membershipList.put(selfIndex, self);
	}
	
	public void updateSelfStatus(NodeStatus status) {
		Node self = membershipList.get(selfIndex);
		self.setStatus(status);
		membershipList.put(selfIndex, self);
	}
	
	/**
	 * Update a neighbor's status and timestamp, if nodeId == selfId, take no effect
	 * 
	 * @param nodeId 
	 * @param status
	 * @param timeStamp
	 */
	public void updateNeighborInfo(int nodeId, Node node) {
		if(nodeId == selfIndex) {
			return;
		}
		Node neighbor = membershipList.get(nodeId);
		DateTime newTimestamp = neighbor.getTimeStamp();
		DateTime oldTimestamp = membershipList.get(nodeId).getTimeStamp();
		if(newTimestamp.compareTo(oldTimestamp) < 0) {
			return;
		}
		membershipList.put(nodeId, node);
	}
	
	public synchronized void add(Node node, Integer nodeId) {
		membershipList.put(nodeId, node);
	}

	public Iterator<Integer> iterator() {
		return membershipList.keySet().iterator();
	}
	
	public int size() {
		return membershipList.size();
	}
	
	public Node getNode(int nodeId) {
		return membershipList.get(nodeId);
	}
	
	public int getSelfId() {
		return selfIndex;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Integer nodeId : membershipList.keySet()) {
			Node node = membershipList.get(nodeId);
			sb.append(nodeId);
			sb.append(" ");
			sb.append(node.getAddress());
			sb.append(" ");
			sb.append(node.getTimeStamp());
			sb.append(" ");
			sb.append(node.getStatus());
			sb.append("\n");
		}
		return sb.toString();
	}
	
}
