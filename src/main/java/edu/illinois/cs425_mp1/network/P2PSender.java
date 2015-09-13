package edu.illinois.cs425_mp1.network;

import edu.illinois.cs425_mp1.types.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is the p2p message sender, one way communication. a proper close() should be called after every run()
 * Created by Wesley on 8/31/15.
 */
public class P2PSender implements Sender {

    static Logger log = LogManager.getLogger("networkLogger");

    String HOST;
    int PORT;

    private ChannelFuture cf;
    private Channel channel;
    private EventLoopGroup group;

    public P2PSender(String host, int port) {
        this.HOST = host;
        this.PORT = port;
    }

    /**
     * Connecting the server(listener)
     */
    public void run(){
        log.trace("sender tries self-configuring on " + HOST + " @" + PORT);
        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new P2PSenderInitializer())
                .option(ChannelOption.TCP_NODELAY, true);
        try {
            log.trace("sender finished configuration, start connecting " + HOST + " @" + PORT);
            cf = b.connect(HOST, PORT).sync();
            channel = cf.channel();
            log.trace("blah2");
        } catch (InterruptedException e){
            log.error("connecting failed due to interruption");
        } catch (Exception e){
            log.error("connecting failed");
        }
        log.trace("finish connecting to " + HOST);
    }

    /**
     * Tell the sender to send message
     * @param msg
     */
    public void send(Message msg) {
        log.trace("sender request to sends msg of " + msg.toString());
        cf = channel.writeAndFlush(msg);
    }

    /**
     * Tell the sender to shutdown
     */
    public void close(){
        try {
            log.trace("sender tries to shutdown");
            channel.closeFuture().sync();
            cf.channel().close().sync();
        } catch (InterruptedException e){
            log.error("exception caught during shutdown sender");
        }
        finally {
            group.shutdownGracefully();
        }
        log.trace("shutdown complete");
    }
}
