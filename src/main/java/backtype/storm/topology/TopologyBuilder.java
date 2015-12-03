package backtype.storm.topology;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TopologyBuilder {
	
	final private StormTopology topology = new StormTopology();
	
	public void addNode(String inputIP, String outputIP, IComponent comp) {
		topology.addComponent(inputIP, outputIP, comp);
	}
	
	public StormTopology createTopology() {
		return topology;
	}
	
}
