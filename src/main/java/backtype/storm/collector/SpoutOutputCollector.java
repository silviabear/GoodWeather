package backtype.storm.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fin;
import backtype.storm.tuple.ITuple;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class SpoutOutputCollector extends OutputCollector {

	
	@Override 
	public void finish() {
		if(cacheSize > 0) {
			dumpTuple();
		}
		queue.add(new Fin(-1));
	}
	
	

}
