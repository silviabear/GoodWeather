package edu.illinois.cs425.g36.app1;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.SortedMap;
import java.util.TreeMap;

import backtype.storm.topology.IRichBolt;

public class CountFinalizer extends IRichBolt {

	SortedMap<String, Integer> counter = new TreeMap<String, Integer>();
	
	@Override
	public void cleanup() {
		PrintWriter writer;
		try {
			writer = new PrintWriter("result", "UTF-8");
			writer.println("-- Port Counter Results --");
			for(String str : counter.keySet()){
				writer.println(str +": " + counter.get(str));
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	public void execute(String tuple) {
		String[] items = tuple.split(":");
		String port = items[0];
		int count = Integer.parseInt(items[1]);
		if(count == 0) {
			System.exit(1);
		}
		if(!counter.containsKey(port)) {
			counter.put(port, 0);
		}
		counter.put(port, counter.get(port) + count);
	}

}
