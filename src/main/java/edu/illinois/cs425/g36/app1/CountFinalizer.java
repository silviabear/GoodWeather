package edu.illinois.cs425.g36.app1;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.topology.IRichBolt;

public class CountFinalizer extends IRichBolt {

	Map<String, Integer> counter = new HashMap<String, Integer>();
	
	@Override
	public void cleanup() {
		System.out.println("-- Port Counter Results --");
		for(String port : counter.keySet()) {
			
			System.out.println(port + ": " + counter.get(port));
		}
	}
	
	@Override
	public void execute(String tuple) {
		String[] items = tuple.split(":");
		String port = items[0];
		int count = Integer.parseInt(items[1]);
		if(!counter.containsKey(port)) {
			counter.put(port, 0);
		}
		counter.put(port, counter.get(port) + 1);
	}

}
