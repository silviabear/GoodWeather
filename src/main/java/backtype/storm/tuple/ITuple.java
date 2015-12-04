package backtype.storm.tuple;

import java.io.Serializable;

public abstract class ITuple implements Serializable {
	public final long id;
	
	public ITuple(long id) {
		this.id = id;
	}
	
}
