package backtype.storm.topology;

import backtype.storm.topology.base.ISpout;

public abstract class IRichSpout implements ISpout{
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
