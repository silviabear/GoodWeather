package backtype.storm.tuple;

import java.io.Serializable;

import backtype.storm.LocalCluster;

public abstract class ITuple implements Serializable {
	
	public long id;
	
	public String sourceAddr;
	
	public ITuple(long id) {
		this.id = id;
		sourceAddr = LocalCluster.localhost;
	}
	
}
