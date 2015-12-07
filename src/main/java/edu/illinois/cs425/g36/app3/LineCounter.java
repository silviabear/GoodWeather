package edu.illinois.cs425.g36.app3;

import backtype.storm.topology.IRichBolt;

public class LineCounter extends IRichBolt {
	
	int count = 0;
	@Override
	public void execute(String tuple) {
		// TODO Auto-generated method stub
		count++;
	}
	
	@Override
	public void cleanup() {
		System.out.println("-- Result --");
		System.out.println(count);
	}

}
