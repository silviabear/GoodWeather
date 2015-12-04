package backtype.storm;

import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;

import edu.illinois.cs425_mp1.network.Listener;
import edu.illinois.cs425_mp1.network.P2PSender;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.topology.IComponent;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.StormTopology;
import backtype.storm.tuple.Tuple;

public class LocalCluster {
	
	public static String localhost;
	
	final public static int incomingPort = 43244;
	final public static int ackPort = 43245;
	
	final private long timeout = 3000;
	
	private Listener inputListener;
	private P2PSender outputSender;
	
	private Listener ackListener;
	private Listener ackSender;
	
	private Thread outputThread;
	
	private final ArrayBlockingQueue<Tuple> toBeAckedQueue = new ArrayBlockingQueue<Tuple>(Integer.MAX_VALUE);
	
	static {
        //Get localhost value
        try {
            localhost = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            System.out.println("fail to inititate local node");
            localhost = "127.0.0.1";
        } 
    }
	
	public void submitTopology(String topologyName, Config config, StormTopology topology) {
		
		IComponent comp = topology.getComponent(localhost);
		String outputIP = topology.getOutputIP(localhost);
		
		if(outputIP != null) { 
			outputSender = new P2PSender(outputIP, incomingPort);
		}
		if(comp instanceof IRichSpout) {
			runSpout((IRichSpout)comp);
		} else if(comp instanceof IRichBolt) {
			runBolt((IRichBolt)comp);
		}

	}
	
	private void runSpout(IRichSpout spout) {
		
		final IRichSpout input = spout;
		
		Thread collectorThread = new Thread() {
			@Override
			public void run() {
				input.nextTuple();
			}
		};
		
		collectorThread.start();
		
		outputThread = new Thread() {
			@Override
			public void run() {
				SpoutOutputCollector collector = input.getOutputCollector();
				while(true) {
					try {
						Tuple tuple = collector.nextTuple();
						outputSender.send(tuple);
						toBeAckedQueue.add(tuple);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		};
		outputThread.start();
	}
	
	private void runBolt(IRichBolt bolt) {
		
		inputListener = new Listener(incomingPort);
		inputListener.registerBolt(bolt);
		
		if(outputSender != null) {
			
		} else {
			
		}
	}
	
	public void shutdown() {
		
	}
	
	public static void requestAck(long id) {
		
	}
}
