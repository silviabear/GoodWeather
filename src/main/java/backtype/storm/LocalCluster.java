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
	
	private static final Listener inputListener = new Listener(incomingPort);
	private static P2PSender outputSender;
	
	private final Listener ackListener = new Listener(ackPort);
	private static Map<String, P2PSender> ackSenders;
	
	private final Thread ackListenerThread = new Thread() {
		@Override
		public void run() {
			log.debug("Start ack listener");
			ackListener.run();
		}
	};
	private final Thread inputListenerThread = new Thread() {
		@Override
		public void run() {
			log.debug("Tuple listener run on " + inputListener.getPort());
			inputListener.run();
		}
		
	};
	private Thread outputThread;
	
	private static boolean isSink;
	private static boolean isSource;
	
	public static Config config;
	
	private static IComponent comp;
	
	private StormTopology topology;
	
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
		this.topology = topology;
		if(outputIP == null) {
			isSink = true;
		}
	
		bootstrap();
	}
	
	public void start() {
		
		if(comp instanceof IRichSpout) {
			log.debug("Runing spout");
			isSource = true;
			runSpout((IRichSpout)comp);
		} else if(comp instanceof IRichBolt) {
			log.debug("Running bolt");
			runBolt((IRichBolt)comp);
		}
	}
	
	private void bootstrap() {
		inputListenerThread.start();
		ackListenerThread.start();
		if(!isSink) {
			outputSender = new P2PSender(topology.getOutputIP(localhost), incomingPort);
			outputSender.run();
		}
		
		if(!isSource) {
			ackSenders = new HashMap<String, P2PSender>();
			for(String inputIP : topology.getInputIPs(localhost)) {
				P2PSender sender = new P2PSender(inputIP, ackPort);
				sender.run();
				ackSenders.put(inputIP, sender);
			}
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
		outputThread = new Thread() {
			@Override
			public void run() {
				SpoutOutputCollector collector = input.getOutputCollector();
				outputSender.run();
				while(true) {
					try {
						ITuple tuple = collector.nextTuple();
						outputSender.send(tuple);
						log.debug("Send tuple " + tuple.id);
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
		
		final IRichBolt bolt = input;
		
		if(!isSink) {
			outputSender.run();
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
		log.debug("Receive tuple " + id);
		if(tuple instanceof Ack) {
			if(isSource) {
				toBeAckedQueue.remove(id);
			} else {
				backwardAck((Ack)tuple);
			}
		} else if(tuple instanceof Fin) {
			log.debug("Receive fin");
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
		log.debug("forward FIN to " + outputSender.getHost() + ": " + outputSender.getPort());
		outputSender.send(fin);
	}
	
	private static void backwardAck(Ack ack) {
		ackSenders.get(ack.id).send(ack);
	}
	
}
