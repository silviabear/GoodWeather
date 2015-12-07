package edu.illinois.cs425.g36.app2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.illinois.cs425.g36.app1.PortReader;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.Config;
import backtype.storm.LocalCluster;

public class Main {

	public static void main(String[] args) {
		boolean parallelMode = args.length > 0;
		TopologyBuilder builder = new TopologyBuilder();
		Config conf = new Config();
		builder.addNode("172.22.151.52", "172.22.151.53", new PortReader());
		builder.addNode("172.22.151.53", "172.22.151.54", new TimeNormalizer());
		builder.addNode("172.22.151.54", null, new TimeAverageFinalizer());
		conf.put("filename", "/home/xxu52/data20mb");
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("Ben", conf, builder.createTopology());
		cluster.initiate();
	}
}
