package edu.illinois.cs425.g36.app3;

import edu.illinois.cs425.g36.app1.PortReader;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

public class Main {

	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();
		Config conf = new Config();
		builder.addNode("172.22.151.52", "172.22.151.53", new PortReader());
		builder.addNode("172.22.151.53", null, new LineCounter());
		conf.put("filename", "data/data320mb");
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("Bear", conf, builder.createTopology());
		cluster.initiate();
	}
}

