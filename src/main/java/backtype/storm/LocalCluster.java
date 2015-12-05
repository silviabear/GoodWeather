package backtype.storm;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.illinois.cs425_mp1.network.Listener;
import edu.illinois.cs425_mp1.network.P2PSender;
import backtype.storm.collector.OutputCollector;
import backtype.storm.collector.SpoutOutputCollector;
import backtype.storm.topology.IComponent;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.StormTopology;
import backtype.storm.tuple.Ack;
import backtype.storm.tuple.Fin;
import backtype.storm.tuple.ITuple;
import backtype.storm.tuple.Tuple;

public class LocalCluster {
	
	public static String localhost;
	
	final public static int incomingPort = 43244;
	final public static int ackPort = 43245;
	
	final private long stablizingTime = 300000;
	
	private static Listener inputListener;
	private static P2PSender outputSender;
	
	private Listener ackListener;
	private static Map<String, P2PSender> ackSenders;
	
	private Thread outputThread;
	
	private static boolean isSink;
	private static boolean isSource;
	
	public static Config config;
	
	private static IComponent comp;
	
	private static final Map<Long, ITuple> toBeAckedQueue = Collections.synchronizedMap(new HashMap<Long, ITuple>());
	
	private static final Map<Long, String> idToInputIP = Collections.synchronizedMap(new HashMap<Long, String>());
	
	private static Logger log = LogManager.getLogger("clusterLogger");
	
	static {
        //Get localhost value
        try {
            localhost = InetAddress.getLocalHost().getHostAddress();
            log.debug("Current localhost" + localhost);
        } catch (Exception e) {
            System.out.println("fail to inititate local node");
            localhost = "127.0.0.1";
        } 
    }
	
	public void submitTopology(String topologyName, Config config, StormTopology topology) {
		
		comp = topology.getComponent(localhost);
		String outputIP = topology.getOutputIP(localhost);
		this.config = config;
		
		if(outputIP != null) { 
			outputSender = new P2PSender(outputIP, incomingPort);
		} else {
			isSink = true;
		}
	
		if(comp instanceof IRichSpout) {
			log.debug("Runing spout");
			isSource = true;
			runSpout((IRichSpout)comp);
		} else if(comp instanceof IRichBolt) {
			log.debug("Running bolt");
			ackSenders = new HashMap<String, P2PSender>();
			for(String inputIP : topology.getInputIPs(localhost)) {
				ackSenders.put(inputIP, new P2PSender(inputIP, ackPort));
			}
			runBolt((IRichBolt)comp);
		}

	}
	
	private void runSpout(IRichSpout spout) {
		
		final IRichSpout input = spout;
		
		Thread collectorThread = new Thread() {
			@Override
			public void run() {
				log.debug("Start collecting data");
				input.nextTuple();
			}
		};
		
		collectorThread.start();
		try {
			Thread.sleep(stablizingTime);
		} catch (InterruptedException e1) {
		}
		outputThread = new Thread() {
			@Override
			public void run() {
				SpoutOutputCollector collector = input.getOutputCollector();
				while(true) {
					try {
						ITuple tuple = collector.nextTuple();
						outputSender.send(tuple);
						toBeAckedQueue.put(tuple.id, tuple);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		};
		outputThread.start();
	}
	
	private void runBolt(IRichBolt input) {
		
		inputListener = new Listener(incomingPort);
		
		final IRichBolt bolt = input;
		if(!isSink) {
			Thread outputThread = new Thread() {
				@Override
				public void run() {
					OutputCollector collector = bolt.getOutputCollector();
					while(true) {
						try {
							ITuple tuple = collector.nextTuple();
							outputSender.send(tuple);
						} catch (InterruptedException e) {
							break;
						}
					}
				}
			};
			outputThread.start();
		} 
	}
	
	public static void handleInput(ITuple tuple) {
		long id = tuple.id;
		if(tuple instanceof Ack) {
			if(isSource) {
				toBeAckedQueue.remove(id);
			} else {
				backwardAck((Ack)tuple);
			}
		} else if(tuple instanceof Fin) {
			if(isSink) {
				((IRichBolt)comp).cleanup();
			} else {
				forwardFin((Fin)tuple);
			}
		} else if(tuple instanceof Tuple) {
			IRichBolt bolt = (IRichBolt)comp;
			for(String str : ((Tuple) tuple).getValues().values()) {
				bolt.execute(str);
			}
		}
	}
	
	private static void forwardFin(Fin fin) {
		outputSender.send(fin);
	}
	
	private static void backwardAck(Ack ack) {
		ackSenders.get(ack.id).send(ack);
	}
	
}
