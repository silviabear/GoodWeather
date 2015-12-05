package edu.illinois.cs425.g36.app1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.IRichSpout;
import backtype.storm.collector.SpoutOutputCollector;

public class PortReader extends IRichSpout {

	private FileReader fileReader;
	private boolean completed = false;
	
	@Override
	public void nextTuple() {
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
		open(LocalCluster.config, new SpoutOutputCollector());
		BufferedReader reader = new BufferedReader(fileReader);
		try{
			//Read all lines
			while((str = reader.readLine()) != null){
				/**
				 * By each line emit a new value with the line as a their
				 */
				this.collector.emit(str);
			}
			collector.finish();
		} catch(Exception e) {
			throw new RuntimeException("Error reading tuple",e);
		} finally {
			completed = true;
		}
		
	}
	
	public void open(Config conf, SpoutOutputCollector collector) {
		
		try {
			this.fileReader = new FileReader(conf.get("filename").toString());
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error reading file ["+conf.get("wordFile")+"]");
		}
		this.collector = collector;
	}

}
