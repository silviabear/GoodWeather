package edu.illinois.cs425.g06.app1;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

/**
 * This application runs on the proxy data, 
 * count the times of traffic from each port
 *  
 * @author silvia
 *
 */
public class Main {

	private final static long runningTime = 50000000;
	
	public static void main(String[] args) {
		
		TopologyBuilder builder = new TopologyBuilder();
		//Hardcode topology for crane
		/*
		builder.setSpout("port-reader",new PortReader());
		builder.setBolt("port-normalizer", new PortNormalizer()).shuffleGrouping("port-reader");
		builder.setBolt("port-counter", new PortCounter()).shuffleGrouping("port-normalizer");
		*/
		
		Config conf = new Config();
		conf.put("wordsFile", args[0]);

		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("Getting-Started-Toplogie", conf, builder.createTopology());
		try {
			Thread.sleep(runningTime);
		} catch (InterruptedException e) {
		}
		finally {
			cluster.shutdown();
		}
	}
}
