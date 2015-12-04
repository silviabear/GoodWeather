package backtype.storm.tuple;

import java.io.Serializable;
import java.util.List;

public class Values implements Serializable {

	private final List<String> values;
	
	public Values(List<String> values) {
		this.values = values;
	}
}
