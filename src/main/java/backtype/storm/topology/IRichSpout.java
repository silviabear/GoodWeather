package backtype.storm.topology;

public abstract class IRichSpout extends IComponent {
    
	public void close() {
		
	}

    public void activate() {
    	
    }

    public void deactivate() {
    	
    }

    public void ack(Object msgId) {
    	
    }
    
    public void fail(Object msgId) {
    	
    }

    public abstract void nextTuple();
}
