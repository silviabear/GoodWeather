package edu.illinois.cs425.g36.app2;

import backtype.storm.topology.IRichBolt;

public class TimeNormalizer extends IRichBolt {

	@Override
	public void execute(String tuple) {
		// TODO Auto-generated method stub
		collector.emit(tuple.split("\\s+")[1]);
	}

}
