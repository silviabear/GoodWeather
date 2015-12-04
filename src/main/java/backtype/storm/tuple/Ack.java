package backtype.storm.tuple;

import java.io.Serializable;

public class Ack extends ITuple implements Serializable {

	public Ack(long id) {
		super(id);
	}

}
