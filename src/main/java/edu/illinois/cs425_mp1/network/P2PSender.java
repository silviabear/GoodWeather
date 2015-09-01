package edu.illinois.cs425_mp1.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by Wesley on 8/31/15.
 */
public class P2PSender implements Sender {

    String HOST;
    int PORT;
    public P2PSender(String host, int port){
        this.HOST = host;
        this.PORT = port;
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new P2PSenderHandler());
                        }
                    })
                    .option(ChannelOption.TCP_NODELAY, true);


            // Start the client
            ChannelFuture f = b.connect(HOST, PORT).sync();

            // Wait until the connection is closed
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();

        }
    }
}
