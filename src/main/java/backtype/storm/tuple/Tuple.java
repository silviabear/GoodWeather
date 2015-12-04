package backtype.storm.tuple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tuple implements Serializable {

	private final long id;
	
	private static long idCounter = 0;
	
	private final Values values;
	
	public Tuple(Values values) {
		idCounter++;
		id = idCounter;
		this.values = values;
	}
	
	public long getId() {
		return id;
	}
	
	public Values getValues() {
		return values;
	}
	
}
