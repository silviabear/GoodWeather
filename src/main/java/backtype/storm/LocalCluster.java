package backtype.storm;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import edu.illinois.cs425_mp1.network.Listener;
import edu.illinois.cs425_mp1.network.P2PSender;
import backtype.storm.collector.OutputCollector;
import backtype.storm.collector.SpoutOutputCollector;
import backtype.storm.topology.IComponent;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.StormTopology;
import backtype.storm.tuple.Ack;
import backtype.storm.tuple.Fail;
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
	private static int sinkFinCriteria;
	
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
	
	public static boolean isSink;
	public static boolean isSource;
	
	public static Config config;
	
	private static IComponent comp;
	
	private StormTopology topology;
	
	private static final Map<Long, ITuple> toBeAckedQueue = Collections.synchronizedMap(new HashMap<Long, ITuple>());
	
	private static final Map<Long, String> idToOutputIP = Collections.synchronizedMap(new HashMap<Long, String>());
	
	private static Logger log = LogManager.getLogger("clusterLogger");
	
	private final static long timeout = 5000;
	private static Map<String, DateTime> lastAck = Collections.synchronizedMap(new HashMap<String, DateTime>());
	
	private Thread examineTimeoutThread;
	
	private static ArrayBlockingQueue<Long> outputIdToUse = new ArrayBlockingQueue<Long>(100000);
	
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
				log.debug("Sender thread runs");
				SpoutOutputCollector collector = input.getOutputCollector();
				for(P2PSender outputSender : outputSenders) {
					outputSender.run();
				}
				int currentSender = 0;
				while(true) {
					try {
						ITuple tuple = collector.nextTuple();
						if(tuple instanceof Fail) {
							System.out.println("FAILLLLLL");
						}
						if(tuple instanceof Fin) {
							for(P2PSender sender : outputSenders) {
								sender.send(tuple);
							}
						} else {
							log.debug("Request send tuple " + tuple.id);
							synchronized(outputSenders) {
								int next = currentSender % outputSenders.size();
								outputSenders.get(next).send(tuple);
							}
							
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
		
		examineTimeoutThread = new Thread() {
			@Override
			public void run() {
				log.debug("examineTimeoutThread run");
				while(true) {
					try {
						Thread.sleep(100);
						DateTime time = new DateTime();
						for(String outputIP : lastAck.keySet()) {
							if(lastAck.get(outputIP) == null) {
								continue;
							}
							if(time.getMillis() - lastAck.get(outputIP).getMillis() > timeout) {
								int index = -1;
								for(int i = 0; i < outputSenders.size(); i++) {
									if(outputSenders.get(i).getHost().equals(outputIP)) {
										index = i;
										break;
									}
								}
								outputSenders.remove(index);
								((IRichSpout)comp).getOutputCollector().emit(new Fail(-2));
								for(ITuple tuple : toBeAckedQueue.values()) {
									((IRichSpout)comp).getOutputCollector().emit(tuple);
								}
							}
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		examineTimeoutThread.start();
	}
	
	public void startSenders() {
		
		if(comp instanceof IRichBolt) {
			ackSenders = new HashMap<String, P2PSender>();
			for(String inputIP : topology.getInputIPs(localhost)) {
				P2PSender sender = new P2PSender(inputIP, ackPort);
				sender.run();
				ackSenders.put(inputIP, sender);
				sinkFinCriteria++;
			}
		} 
		
		if(!isSink) {
			outputSenders = new ArrayList<P2PSender>();
			for(String outputIP : topology.getOutputIP(localhost)) {
				P2PSender outputSender = new P2PSender(outputIP, incomingPort);
				if(comp instanceof IRichSpout) {
					lastAck.put(outputIP, null);
				}
				outputSenders.add(outputSender);
				outputSender.run();
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
			System.out.println("Receive ack");
			if(isSource) {
				toBeAckedQueue.remove(id);
				lastAck.put(tuple.sourceAddr, new DateTime());
			} else {
				backwardAck((Ack)tuple);
			}
		} else if(tuple instanceof Fin) {
			log.debug("Receive fin");
			if(isSink) {
				sinkFinCounter++;
				System.out.println(sinkFinCounter + "  " + sinkFinCriteria);
				if(sinkFinCounter == ackSenders.size()) {
					log.debug("Finish and cleanup");
					((IRichBolt)comp).cleanup();
				}
			} else {
				forwardTuple(tuple);
			}
		} else if(tuple instanceof Tuple) {
			IRichBolt bolt = (IRichBolt)comp;
			outputIdToUse.add(id);
			for(String str : ((Tuple) tuple).getValues().values()) {
				bolt.execute(str);
			}
			if(isSink) {
				ackSenders.get(tuple.sourceAddr).send(new Ack(id));
			} else {
				idToOutputIP.put(tuple.id, tuple.sourceAddr);
			}
		} else if(tuple instanceof Fail) {
			log.debug("Receive fail");
			System.out.println("Receive fail");
			if(isSink) {
				sinkFinCriteria--;
			} else {
				forwardTuple(tuple);
			}
		}
	}
	
	private static void forwardTuple(ITuple tuple) {
		OutputCollector collector = ((IRichBolt)comp).getOutputCollector();
		collector.finish();
		collector.emit(tuple);
	}
	
	private static void backwardAck(Ack ack) {
		ackSenders.get(idToOutputIP.get(ack.id)).send(ack);
	}
	
	public static long getNextOutputId() throws InterruptedException {
		return outputIdToUse.take();
	}
	
}
