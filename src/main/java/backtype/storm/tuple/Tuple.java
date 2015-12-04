package backtype.storm.tuple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tuple extends ITuple implements Serializable {
	
	private static long idCounter = 0;
	
	private final Values values;
	
	public final String inputIP;
	
	public Tuple(Values values, String inputIP) {
		super(idCounter);
		idCounter++;
		this.values = values;
		this.inputIP = inputIP;
	}
	
	public Values getValues() {
		return values;
	}
	
}
