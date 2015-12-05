package edu.illinois.cs425.g36.app1;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import backtype.storm.topology.IRichBolt;

public class PortCounterFinalizer extends IRichBolt {
	
	SortedMap<String, Integer> counters = new TreeMap<String, Integer>();
	
	/**
	* At the end of the spout (when the cluster is shutdown
	* We will show the word counters
	*
	*/
	@Override
	public void cleanup() {
		PrintWriter writer;
		try {
			writer = new PrintWriter("result", "UTF-8");
			writer.println("-- Port Counter Results --");
			for(Map.Entry<String, Integer> entry : counters.entrySet()){
				writer.println(entry.getKey()+": "+entry.getValue());
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
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
