package backtype.storm.collector;

public class BoltOutputCollector extends OutputCollector {

	@Override
	public void finish() {
		log.debug("finish");
		if(cacheSize > 0) {
			dumpTuple();
		}
	}
}
