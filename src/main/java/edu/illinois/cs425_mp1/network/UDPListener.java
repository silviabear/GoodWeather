package edu.illinois.cs425_mp1.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Wesley on 9/23/15.
 */
public class UDPListener {

    static Logger log = LogManager.getLogger("networkLogger");

    private final int port;

    public UDPListener(int port) {
        this.port = port;
    }

    public void run() {
        log.trace("listener tries self-configuring on localhost @" + port);
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                                 @Override
                                 public void initChannel(SocketChannel ch) throws Exception {
                                     ChannelPipeline p = ch.pipeline();
                                     p.addLast(
                                             new ObjectEncoder(),
                                             new ObjectDecoder(200000000, ClassResolvers.cacheDisabled(null)),
                                             new ListenerHandler());
                                 }
                             }
                    );

            b.bind(this.port).sync().channel().closeFuture().await();
        } catch (InterruptedException e) {
            log.error("binding failed due to interruption");
        } catch (Exception e) {
            log.error("binding failed (address maybe in use)");
        } finally {
            group.shutdownGracefully();
        }
    }
}
