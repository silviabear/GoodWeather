package edu.illinois.cs425_mp1.network;

import edu.illinois.cs425_mp1.types.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

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
                .channel(NioServerSocketChannel.class)
                .handler(new P2PSenderInitializer())
                .option(ChannelOption.TCP_NODELAY, true);


        // Start the sender
        cf = b.connect(HOST, PORT).sync();
        channel = cf.channel();


    }

    public void send(Message msg) {
        // TODO: What should be sent?
        cf = channel.writeAndFlush(msg.toString());
    }

    public void close() throws Exception {
        try {
            // Wait until the connection is closed
            channel.closeFuture().sync();

            // TODO: Test if cf.channel should be closed or not
            cf.channel().close();
            cf.sync();

        } finally {
            group.shutdownGracefully();
        }
    }
}
