package backtype.storm.spout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class SpoutOutputCollector {

	private static final ArrayBlockingQueue<Tuple> queue = new ArrayBlockingQueue<Tuple>(Integer.MAX_VALUE, true);
	
	private static final int maxPacketSize = 50;
	
	private String[] cache = new String[maxPacketSize];
	
	private int cacheSize = 0;
	
	public void emit(String str) {
		if(cacheSize < maxPacketSize) {
			cache[cacheSize] = str;
			cacheSize++;
		} else {
			dumpTuple();
			cacheSize = 0;
		}
	}
	
	public void finish() {
		if(cacheSize > 0) {
			dumpTuple();
		}
	}
	
	private void dumpTuple() {
		List<String> val = new ArrayList<String>(cacheSize);
		for(int i = 0; i < cacheSize; i++) {
			val.add(cache[i]);
		}
		Values value = new Values(val);
		Tuple tuple = new Tuple(value);
		queue.add(tuple);
	}
	
	public Tuple nextTuple() throws InterruptedException {
		return queue.take();
	}

}
