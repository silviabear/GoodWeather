package backtype.storm.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backtype.storm.LocalCluster;
import backtype.storm.tuple.ITuple;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class OutputCollector {

	protected static final ArrayBlockingQueue<ITuple> queue = new ArrayBlockingQueue<ITuple>(1000000, true);
	
	protected static final int maxPacketSize = 50;
	
	protected String[] cache = new String[maxPacketSize];
	
	protected int cacheSize = 0;

	protected Logger log = LogManager.getLogger("collectorLogger");
	
	public void emit(String str) {
		log.debug("emit " + str);
		if(cacheSize < maxPacketSize) {
			cache[cacheSize] = str;
			cacheSize++;
		} else {
			dumpTuple();
			cacheSize = 0;
		}
	}
	
	public void emit(ITuple tuple) {
		queue.add(tuple);
	}
	
	protected void dumpTuple() {
		List<String> val = new ArrayList<String>(cacheSize);
		for(int i = 0; i < cacheSize; i++) {
			val.add(cache[i]);
		}
		Values value = new Values(val);
		Tuple tuple = null;
		if(LocalCluster.isSource) {
			tuple = new Tuple(value, LocalCluster.localhost);
		}
		else {
			try {
				tuple = new Tuple(value, LocalCluster.localhost, LocalCluster.getNextOutputId());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		queue.add(tuple);
	}
	
	public ITuple nextTuple() throws InterruptedException {
		return queue.take();
	}
	
	public void finish() {
		
	}
	
}
