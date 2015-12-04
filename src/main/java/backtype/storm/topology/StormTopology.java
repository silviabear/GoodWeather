package backtype.storm.topology;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
	
	Map<String, Set<String>> inputs = new HashMap<String, Set<String>>();
	
	public void addComponent(String inputIP, String outputIP, IComponent comp) {
		Edge e = new Edge(comp, outputIP);
		topo.put(inputIP, e);
		if(!inputs.containsKey(outputIP)) {
			inputs.put(outputIP, new HashSet<String>());
		}
		inputs.get(outputIP).add(inputIP);
	}
	
	public IComponent getComponent(String inputIP) {
		return topo.get(inputIP).getComp();
	}
	
	public String getOutputIP(String inputIP) {
		return topo.get(inputIP).getOutputIP();
	}
	
	public Set<String> getInputIPs(String localhost) {
		return inputs.get(localhost);
	}
	
}
class Edge {
	
	private final String outputIP;
	private final IComponent comp;
	
	Edge(IComponent comp, String outputIP) {
		this.outputIP = outputIP;
		this.comp = comp;
	}
	
	IComponent getComp() {
		return comp;
	}
	
	String getOutputIP() {
		return outputIP;
	}
}
