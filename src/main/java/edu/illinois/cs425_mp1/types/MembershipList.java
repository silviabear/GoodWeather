package edu.illinois.cs425_mp1.types;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;


public class MembershipList implements Iterable<Integer>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final Hashtable<Integer, Node> membershipList = new Hashtable<Integer, Node>();
	
	private int selfIndex;
	
	public void setSelfIndex(int selfIndex) {
		this.selfIndex = selfIndex;
	}
	
	/**
	 * Update current node's timestamp to current time
	 */
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
	 */
	public void updateNeighborInfo(int nodeId, Node node) {
		if(nodeId == selfIndex) {
			return;
		}
		membershipList.put(nodeId, node);
	}
	
	public synchronized void add(Node node, Integer nodeId) {
		membershipList.put(nodeId, node);
	}

	/**
	 * Get the iterator of membershipList, 
	 * when iterating the membershiplist object should be synchronized
	 */
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
