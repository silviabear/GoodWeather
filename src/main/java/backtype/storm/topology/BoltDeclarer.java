package backtype.storm.topology;

public class BoltDeclarer {

	private final StormTopology topology;
	
	private final IRichBolt bolt;
	
	public BoltDeclarer(StormTopology topology, IRichBolt bolt) {
		this.topology = topology;
		this.bolt = bolt;
	}
	
	public void shuffleGrouping(String id) {
		topology.setOutput(id, bolt);
	}
}
