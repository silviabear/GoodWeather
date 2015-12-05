package backtype.storm.topology;

import backtype.storm.collector.BoltOutputCollector;
import backtype.storm.collector.OutputCollector;

public abstract class IRichBolt extends IComponent {
	
	protected BoltOutputCollector collector = new BoltOutputCollector();
	
	public void cleanup() {
		
	}
	
	public OutputCollector getOutputCollector() {
		return collector;
	}
	
	abstract public void execute(String tuple);
}
