package edu.illinois.cs425.g06.app1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import backtype.storm.Config;
import backtype.storm.topology.IRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.spout.SpoutOutputCollector;

public class PortReader extends IRichSpout {

	private SpoutOutputCollector collector;
	private FileReader fileReader;
	private boolean completed = false;
	
	@Override
	public void nextTuple() {
		// TODO Auto-generated method stub
		if(completed){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			//Do nothing
			}
			return;
		}
		String str;
		//Open the reader
		BufferedReader reader = new BufferedReader(fileReader);
		try{
			//Read all lines
			while((str = reader.readLine()) != null){
				/**
				 * By each line emit a new value with the line as a their
				 */
				this.collector.emit(new Values(str),str);
			}
		} catch(Exception e) {
			throw new RuntimeException("Error reading tuple",e);
		} finally {
			completed = true;
		}
	}
	
	public void open(Config conf, SpoutOutputCollector collector) {
		try {
			this.fileReader = new FileReader(conf.get("wordsFile").toString());
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error reading file ["+conf.get("wordFile")+"]");
		}
		this.collector = collector;
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("line"));
		}

}
