package edu.illinois.cs425.g06.app1;

import java.util.ArrayList;
import java.util.List;

import backtype.storm.Config;
import backtype.storm.task.OutputCollector;
import backtype.storm.topology.IRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class PortNormalizer extends IRichBolt {
	private OutputCollector collector;
	
	@Override
	public void execute(Tuple input) {
		String sentence = input.getString(0);
		String[] words = sentence.split(" ");
		for(String word : words){
			word = word.trim();
			if(!word.isEmpty()){
				word = word.toLowerCase();
				//Emit the word
				List a = new ArrayList();
				a.add(input);
				collector.emit(a,new Values(word));
			}
		}
		// Acknowledge the tuple
		collector.ack(input);
	}
	
	public void prepare(Config stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("port"));
	}
}
