package edu.illinois.cs425.g36.app1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

public class Main {
	
	public static void main(String[] args) {
		
		boolean parallelMode = args[0].equals("1");
		
		TopologyBuilder builder = new TopologyBuilder();
		Config conf = new Config();
		if(!parallelMode) {
			builder.addNode("172.22.151.52", "172.22.151.53", new PortReader());
			builder.addNode("172.22.151.53", "172.22.151.54", new PortNormalizer());
			builder.addNode("172.22.151.54", null, new PortCounterFinalizer());
		} else {
			IRichSpout portReader = new PortReader();
			builder.addNode("172.22.151.52", "172.22.151.53", portReader);
			builder.addNode("172.22.151.52", "172.22.151.54", portReader);
			builder.addNode("172.22.151.52", "172.22.151.55", portReader);
			builder.addNode("172.22.151.52", "172.22.151.56", portReader);
			builder.addNode("172.22.151.53", "172.22.151.57", new PortNormalizer());
			builder.addNode("172.22.151.54", "172.22.151.57", new PortNormalizer());
			builder.addNode("172.22.151.55", "172.22.151.57", new PortNormalizer());
			builder.addNode("172.22.151.56", "172.22.151.57", new PortNormalizer());
			builder.addNode("172.22.151.57", null, new PortCounterFinalizer());
		}
		conf.put("filename", "data/data20mb");
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("Getting-Started-Toplogie", conf, builder.createTopology());
		cluster.initiate();
	}
}
