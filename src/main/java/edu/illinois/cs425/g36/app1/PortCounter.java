package edu.illinois.cs425.g36.app1;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.topology.IRichBolt;

public class PortCounter extends IRichBolt {

	Map<String, Integer> counters = new HashMap<String, Integer>();
	
	@Override
	public void onFinish() {
		for(String port : counters.keySet()) {
			collector.emit(port + ":" + counters.get(port));
		}
	}
	
	@Override
	public void execute(String tuple) {
		/**
		 * If the word dosn't exist in the map we will create
		 * this, if not We will add 1
		 */
		if(!counters.containsKey(tuple)){
			counters.put(tuple, 1);
		}else{
			Integer c = counters.get(tuple) + 1;
			counters.put(tuple, c);
		}
		
	}

}
