package backtype.storm.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import backtype.storm.LocalCluster;
import backtype.storm.tuple.ITuple;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class OutputCollector {

protected static final ArrayBlockingQueue<ITuple> queue = new ArrayBlockingQueue<ITuple>(Integer.MAX_VALUE, true);
	
	protected static final int maxPacketSize = 50;
	
	protected String[] cache = new String[maxPacketSize];
	
	protected int cacheSize = 0;
	
	public void emit(String str) {
		if(cacheSize < maxPacketSize) {
			cache[cacheSize] = str;
			cacheSize++;
		} else {
			dumpTuple();
			cacheSize = 0;
		}
	}
	
	protected void dumpTuple() {
		List<String> val = new ArrayList<String>(cacheSize);
		for(int i = 0; i < cacheSize; i++) {
			val.add(cache[i]);
		}
		Values value = new Values(val);
		Tuple tuple = new Tuple(value, LocalCluster.localhost);
		queue.add(tuple);
	}
	
	public ITuple nextTuple() throws InterruptedException {
		return queue.take();
	}
	
	public void finish() {
		
	}
	
}
