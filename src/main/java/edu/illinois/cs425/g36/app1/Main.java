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
		
		TopologyBuilder builder = new TopologyBuilder();
		Config conf = new Config();
		builder.addNode("172.22.151.52", "172.22.151.53", new PortReader());
		builder.addNode("172.22.151.53", "172.22.151.54", new PortNormalizer());
		builder.addNode("172.22.151.54", null, new PortCounter());
		conf.put("filename", "data/testdata1");
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("Getting-Started-Toplogie", conf, builder.createTopology());
		System.out.println("Enter anything to start the topology");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			br.readLine();
		} catch (IOException e) {
		}
		
	}
}
