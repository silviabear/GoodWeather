package backtype.storm.topology;

import java.util.Map;
import java.util.TreeMap;

public class TopologyBuilder {
	
	final private StormTopology topology = new StormTopology();
	
	public void setSpout(String id, IRichSpout spout) {
		topology.setSource(id, spout);
	}
	
	public BoltDeclarer setBolt(String id, IRichBolt bolt) {
		BoltDeclarer declarer = new BoltDeclarer(topology);
		return declarer;
	}
	
	public StormTopology createTopology() {
		return topology;
	}
	
}
