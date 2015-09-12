package edu.illinois.cs425_mp1.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.illinois.cs425_mp1.types.Message;
import edu.illinois.cs425_mp1.types.Reply;
import edu.illinois.cs425_mp1.types.Request;

/**
 * Generic Wrapper of Broadcast Sender
 * Created by Wesley on 8/31/15.
 */
public class BroadcastSender implements Sender {
	
	private String[] neighbors;
	private int uniPort;
	
	static Logger logger = LogManager.getLogger("networkLogger");
	
	public BroadcastSender(String[] neighbors, int unitPort) {
		this.neighbors = neighbors;
		this.uniPort = uniPort;
	}
	
	public int send(Message msg) {
		Thread[] senders = new Thread[neighbors.length];
		final Message request = msg;
		for(int i = 0; i < senders.length; i++) {
			final String addr = neighbors[i];
			senders[i] = new Thread() {
				@Override
				public void run() {
					P2PSender sender = new P2PSender(addr, uniPort);
					sender.run();
					sender.send(request);
					sender.close();
				}
			};
		}
		for(int i = 0; i < senders.length; i++) {
			try {
				senders[i].join();
			} catch (InterruptedException e) {
				logger.trace("Sender " + neighbors[i] + "is interrupted");
				e.printStackTrace();
			}
		}
		return 0;
	}

}
