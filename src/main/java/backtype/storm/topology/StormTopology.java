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
	
	protected IRichSpout source;
	
	private Map<IComponent, IComponent> edges = new HashMap<IComponent, IComponent>();
	
	private Map<String, IComponent> idToComponent = new HashMap<String, IComponent>();
	
	protected void setSource(String id, IRichSpout spout) {
		idToComponent.put(id, spout);
		source = spout;
	}
	
	protected void setOutput(String id, IComponent output) {
		edges.put(idToComponent.get(id), output);
	}
	
}
