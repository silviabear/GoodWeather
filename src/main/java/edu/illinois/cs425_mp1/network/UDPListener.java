package edu.illinois.cs425_mp1.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backtype.storm.topology.IRichBolt;

import java.util.concurrent.ThreadFactory;

/**
 * This is the UDP Listener part
 * Created by Wesley on 9/23/15.
 */
public class UDPListener {

    static Logger log = LogManager.getLogger("networkLogger");

    private final int port;

    public UDPListener(int port) {
        this.port = port;
    }
    
    /**
     * Ask the listener to run
     */
    public void run() {
        log.trace("listener tries self-configuring on localhost @" + port);
        final ThreadFactory acceptFactory = new DefaultThreadFactory("accept");
        final ThreadFactory connectFactory = new DefaultThreadFactory("connect");
        final NioEventLoopGroup acceptGroup = new NioEventLoopGroup(1, acceptFactory, NioUdtProvider.BYTE_PROVIDER);
        final NioEventLoopGroup connectGroup = new NioEventLoopGroup(1, connectFactory, NioUdtProvider.BYTE_PROVIDER);
        try {
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(acceptGroup, connectGroup)
                    .channelFactory(NioUdtProvider.BYTE_ACCEPTOR)
                    .option(ChannelOption.SO_BACKLOG, 10)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<UdtChannel>() {
                        @Override
                        public void initChannel(final UdtChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(200000000, ClassResolvers.cacheDisabled(null)),
                                    new UDPListenerHandler());
                        }
                    });


            boot.bind(this.port).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("binding failed due to interruption");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("binding failed (address maybe in use)");
        } finally {
            acceptGroup.shutdownGracefully();
            connectGroup.shutdownGracefully();
        }
    }
    
}
