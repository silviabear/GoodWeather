package edu.illinois.cs425_mp1.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


/**
 * This class is the main listener class that sets up the server required config
 *
 */
public class Listener {

    private final int port;
    private Channel channel;
    private ChannelFuture cf;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public Listener(int port) {
        this.port = port;
    }

    /**
     * Tell the listener to run
     * @throws Exception
     */
    public void run() throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                      @Override
                                      public void initChannel(SocketChannel ch) throws Exception {
                                          ChannelPipeline p = ch.pipeline();
                                          p.addLast(
                                                  new ObjectEncoder(),
                                                  new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                                  new LoggingHandler(LogLevel.INFO),
                                                  new ListenerHandler());
                                      }
                                  }
                    );

            // Bind and start to accept incoming connections.
            cf = b.bind(port).sync().channel().closeFuture().sync();
            channel = cf.channel();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * Forcefully shutdown
     * @throws Exception
     */
    public void close() throws Exception {
        // Wait until the server socket is closed.
        try {
            channel.close().sync();
            cf.sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
