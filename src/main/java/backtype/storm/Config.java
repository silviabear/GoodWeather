package backtype.storm;

import java.util.HashMap;
import java.util.Map;

public class Config {

	private final Map<String, String> config = new HashMap<String, String>();
	
	public void put(String key, String value) {
		config.put(key, value);
	}
	
	public String get(String key) {
		return config.get(key);
	}
}
