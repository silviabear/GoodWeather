package backtype.storm.topology;

import backtype.storm.tuple.Tuple;

public abstract class IRichBolt implements IComponent {
	
	public void cleanup() {
		
	}
	
	abstract public void execute(Tuple tuple);
}
