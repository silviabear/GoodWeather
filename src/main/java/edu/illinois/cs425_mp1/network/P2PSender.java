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
 * This is the p2p message sender, one way communication. a proper close() should be called after every run()
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

        cf = b.connect(HOST, PORT).sync();
        channel = cf.channel();


    }

    /**
     * Tell the sender to send message
     * @param msg
     */
    public void send(Message msg) { cf = channel.writeAndFlush(msg); }

    /**
     * Tell the sender to shutdown
     * @throws Exception
     */
    public void close() throws Exception {
        try {
            // TODO: Log closing
            channel.closeFuture().sync();
            cf.channel().close().sync();

        } finally {
            group.shutdownGracefully();
        }
    }
}
