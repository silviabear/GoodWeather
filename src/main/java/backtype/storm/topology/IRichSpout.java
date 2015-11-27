package backtype.storm.topology;

public abstract class IRichSpout implements IComponent {
    
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
}
