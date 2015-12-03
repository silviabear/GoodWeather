package backtype.storm.topology;

import backtype.storm.tuple.Tuple;

public abstract class IRichBolt extends IComponent {
	
	public void cleanup() {
		
	}
	
	abstract public void execute(Tuple tuple);
}
