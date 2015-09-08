package edu.illinois.cs425_mp1.network;

import edu.illinois.cs425_mp1.types.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by Wesley on 8/31/15.
 */
public class P2PSender implements Sender {

    String HOST;
    int PORT;

    private ChannelFuture cf;
    private Channel channel;
    private EventLoopGroup group;

    public P2PSender(String host, int port) {
        this.HOST = host;
        this.PORT = port;
    }

    public void run() throws Exception {
        group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new P2PSenderInitializer())
                .option(ChannelOption.TCP_NODELAY, true);


        // TODO: Log the connection
        // Start the sender
        System.out.println("Connecting " + HOST + " @" + PORT);
        cf = b.connect(HOST, PORT).sync();
        channel = cf.channel();


    }

    // NOTE: Channel will be closed after send msg, ie. this is a one time used channel
    public void send(Message msg) { cf = channel.writeAndFlush(msg); }

    public void send(String msg){
        cf = channel.writeAndFlush(msg);
    }

    public void send(Object msg) {
        cf = channel.writeAndFlush(msg);
    }

    public void close() throws Exception {
        try {
            // TODO: Log closing
            System.out.println("Closing current talk");
            channel.closeFuture().sync();
            cf.channel().close().sync();

        } finally {
            group.shutdownGracefully();
        }
    }
}
