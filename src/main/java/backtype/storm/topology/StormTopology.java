package backtype.storm.topology;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A Tree data structure using adjacent list representation
 * The root is always a spout
 * 
 * @author silvia
 *
 */
public class StormTopology {
	
	Map<String, Edge> topo = new HashMap<String, Edge>();
	
	public void addComponent(String inputIP, String outputIP, IComponent comp) {
		Edge e = new Edge(comp, outputIP);
		topo.put(inputIP, e);
	}
	
}
class Edge {
	
	private final String outputIP;
	private final IComponent comp;
	
	Edge(IComponent comp, String outputIP) {
		this.outputIP = outputIP;
		this.comp = comp;
	}
}
