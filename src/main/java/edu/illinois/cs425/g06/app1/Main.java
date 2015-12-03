package edu.illinois.cs425.g06.app1;

import java.util.ArrayList;
import java.util.List;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.IComponent;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;

/**
 * This application runs on the proxy data, 
 * count the times of traffic from each port
 *  
 * @author silvia
 *
 */
/*
 * "172.22.151.52",
            "172.22.151.53",
            "172.22.151.54",
            "172.22.151.55",
            "172.22.151.56",
            "172.22.151.57",
            "172.22.151.58",
 */
public class Main {

	private final static long runningTime = 50000000;
	
	public static void main(String[] args) {
		
		TopologyBuilder builder = new TopologyBuilder();
		//Topology for storm
		/*
		builder.setSpout("port-reader",new PortReader());
		builder.setBolt("port-normalizer", new PortNormalizer()).shuffleGrouping("port-reader");
		builder.setBolt("port-counter", new PortCounter()).shuffleGrouping("port-normalizer");
		*/
		//Hardcode topology for crane
		Config conf = new Config();
		IRichSpout spout = new PortReader();
		builder.addNode("172.22.151.52", "172.22.151.53", spout);
		builder.addNode("172.22.151.52", "172.22.151.54", spout);
		builder.addNode("172.22.151.52", "172.22.151.55", spout);
		builder.addNode("172.22.151.53", "172.22.151.56", new PortNormalizer());
		builder.addNode("172.22.151.54", "172.22.151.57", new PortNormalizer());
		builder.addNode("172.22.151.55", "172.22.151.57", new PortNormalizer());
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
