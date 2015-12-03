package backtype.storm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backtype.storm.topology.IComponent;

public class Config {

	private final Map<String, String> config = new HashMap<String, String>();
	
	private List<IComponent> compList;
	
	public void put(String key, String value) {
		config.put(key, value);
	}
	
	public String get(String key) {
		return config.get(key);
	}
	
	/**
	 * Default the first element in the list is spout,
	 * only one spout is allowed
	 * @param compList
	 */
	public void setComponentTopo(List<IComponent> compList) {
		this.compList = compList;
	}
	
	
}
