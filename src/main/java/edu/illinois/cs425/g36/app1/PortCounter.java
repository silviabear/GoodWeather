package edu.illinois.cs425.g36.app1;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.Config;
import backtype.storm.collector.OutputCollector;
import backtype.storm.topology.IRichBolt;
import backtype.storm.tuple.Tuple;

public class PortCounter extends IRichBolt {
	
	Map<String, Integer> counters;
	
	/**
	* At the end of the spout (when the cluster is shutdown
	* We will show the word counters
	*
	*/
	@Override
	public void cleanup() {
		System.out.println("-- Port Counter Results --");
		for(Map.Entry<String, Integer> entry : counters.entrySet()){
			System.out.println(entry.getKey()+": "+entry.getValue());
		}
	}
	
	/**
	* On each word We will count
	*/
	@Override
	public void execute(String str) {
		
		/**
		 * If the word dosn't exist in the map we will create
		 * this, if not We will add 1
		 */
		if(!counters.containsKey(str)){
			counters.put(str, 1);
		}else{
			Integer c = counters.get(str) + 1;
			counters.put(str, c);
		}
	}
	
}