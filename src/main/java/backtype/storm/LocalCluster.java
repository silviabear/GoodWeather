package backtype.storm;

import java.net.InetAddress;

import edu.illinois.cs425_mp1.network.UDPListener;
import backtype.storm.topology.IComponent;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.StormTopology;

public class LocalCluster {
	
	public static String localhost;
	
	final public static int incomingPort = 43244;
	final public static int ackPort = 43245;
	
	final private long timeout = 3000;
	
	private UDPListener incomingListener = new UDPListener(incomingPort);
	
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
		if(comp instanceof IRichSpout) {
			runSpout((IRichSpout)comp, outputIP);
		} else if(comp instanceof IRichBolt) {
			runBolt((IRichBolt)comp, outputIP);
		}
	}
	
	private void runSpout(IRichSpout spout, String outputIP) {
		
	}
	
	private void runBolt(IRichBolt bolt, String outputIP) {
		//Is sink
		if(outputIP == null) {
			
		} else {
			
		}
	}
	
	public void shutdown() {
		
	}
}
