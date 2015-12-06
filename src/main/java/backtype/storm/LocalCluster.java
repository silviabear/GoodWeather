package backtype.storm;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private static List<P2PSender> outputSenders;
	
	private final Listener ackListener = new Listener(ackPort);
	private static Map<String, P2PSender> ackSenders;
	
	private static int sinkFinCounter = 0;
	
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
		Set<String> outputIP = topology.getOutputIP(localhost);
		this.config = config;
		this.topology = topology;
		if(outputIP.contains(null)) {
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
				for(P2PSender outputSender : outputSenders) {
					outputSender.run();
				}
				int currentSender = 0;
				while(true) {
					try {
						ITuple tuple = collector.nextTuple();
						if(tuple instanceof Fin) {
							for(P2PSender sender : outputSenders) {
								sender.send(tuple);
							}
						} else {
							outputSenders.get(currentSender % outputSenders.size()).send(tuple);
						}
						log.debug("Send tuple " + tuple.id);
						toBeAckedQueue.put(tuple.id, tuple);
						currentSender++;
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		};
		outputThread.start();
		
	}
	
	public void startSenders() {
		if(!isSink) {
			outputSenders = new ArrayList<P2PSender>();
			for(String outputIP : topology.getOutputIP(localhost)) {
				P2PSender outputSender = new P2PSender(outputIP, incomingPort);
				outputSenders.add(outputSender);
				outputSender.run();
			}
		}
		
		if(comp instanceof IRichBolt) {
			ackSenders = new HashMap<String, P2PSender>();
			for(String inputIP : topology.getInputIPs(localhost)) {
				P2PSender sender = new P2PSender(inputIP, ackPort);
				sender.run();
				ackSenders.put(inputIP, sender);
			}
		}
	}
	
	private void runBolt(IRichBolt input) {
		
		final IRichBolt bolt = input;
		
		if(!isSink) {
			Thread outputThread = new Thread() {
				@Override
				public void run() {
					log.debug("Tuple output collector run");
					OutputCollector collector = bolt.getOutputCollector();
					while(true) {
						try {
							ITuple tuple = collector.nextTuple();
							outputSenders.get(0).send(tuple);
						} catch (InterruptedException e) {
							break;
						}
					}
				}
			};
			outputThread.start();
		} 
		
	}
	
	public synchronized static void handleInput(ITuple tuple) {
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
				sinkFinCounter++;
				if(sinkFinCounter == ackSenders.size()) { 					
					((IRichBolt)comp).cleanup();
				}
			} else {
				if(comp instanceof IRichBolt) {
					((IRichBolt)comp).onFinish();
				}
				OutputCollector collector = ((IRichBolt)comp).getOutputCollector();
				collector.finish();
				collector.emit(tuple);
			}
		} else if(tuple instanceof Tuple) {
			IRichBolt bolt = (IRichBolt)comp;
			for(String str : ((Tuple) tuple).getValues().values()) {
				bolt.execute(str);
			}
		}
	}
	
	private static void backwardAck(Ack ack) {
		ackSenders.get(ack.id).send(ack);
	}
	
}
