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
	
	Map<String, IComponent> topo = new HashMap<String, IComponent>();
	
	private final Map<String, Set<String>> inputs = new HashMap<String, Set<String>>();
	private final Map<String, Set<String>> outputs = new HashMap<String, Set<String>>();
	
	public void addComponent(String inputIP, String outputIP, IComponent comp) {
		topo.put(inputIP, comp);
		
		if(!inputs.containsKey(outputIP)) {
			inputs.put(outputIP, new HashSet<String>());
		}
		inputs.get(outputIP).add(inputIP);
		
		if(!outputs.containsKey(inputIP)) {
			outputs.put(inputIP, new HashSet<String>());
		}
		outputs.get(inputIP).add(outputIP);
	
	}
	
	public IComponent getComponent(String inputIP) {
		return topo.get(inputIP);
	}
	
	public Set<String> getOutputIP(String inputIP) {
		return outputs.get(inputIP);
	}
	
	public Set<String> getInputIPs(String localhost) {
		return inputs.get(localhost);
	}
	
}