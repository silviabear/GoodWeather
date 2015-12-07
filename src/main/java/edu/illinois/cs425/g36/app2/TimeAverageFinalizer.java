package edu.illinois.cs425.g36.app2;

import backtype.storm.topology.IRichBolt;

public class TimeAverageFinalizer extends IRichBolt {

	double sum = 0;
	int count = 0;
	
	@Override
	public void execute(String tuple) {
		// TODO Auto-generated method stub
		Double value = Double.parseDouble(tuple);
		count++;
		sum += value;
	}
	
	@Override
	public void cleanup() {
		System.out.println("-- Avg Results --");
		System.out.println(sum / count);
	}

}
