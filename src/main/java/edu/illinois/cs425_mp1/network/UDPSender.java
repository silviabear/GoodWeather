package edu.illinois.cs425_mp1.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadFactory;

/**
 * Generic re-write of P2P sender use UDP
 * Created by Wesley on 9/23/15.
 */
public class UDPSender implements Sender {

    static Logger log = LogManager.getLogger("networkLogger");

    String HOST;
    int PORT;

    private ChannelFuture cf;
    private Channel channel;
    private EventLoopGroup group;
    private Bootstrap boot = new Bootstrap();
    final ThreadFactory connectFactory = new DefaultThreadFactory("connect");

    public UDPSender(String host, int port) {
        this.HOST = host;
        this.PORT = port;
    }

    /**
     * Connecting the UDP listener
     */
    public void run() {
        log.trace("sender tries self-configuring on " + HOST + " @" + PORT);
        group = new NioEventLoopGroup(1, connectFactory, NioUdtProvider.BYTE_PROVIDER);
        boot.group(group)
                .channelFactory(NioUdtProvider.BYTE_CONNECTOR)
                .handler(new ChannelInitializer<UdtChannel>() {
                    @Override
                    public void initChannel(final UdtChannel ch)
                            throws Exception {
                        ch.pipeline().addLast(
                                new ObjectEncoder(),
                                new ObjectDecoder(200000000, ClassResolvers.cacheDisabled(null)),
                                new UDPSenderHandler());
                    }
                });
        try {
            log.trace("sender finished configuration, start connecting " + HOST + " @" + PORT);
            cf = boot.connect(HOST, PORT).sync();
            channel = cf.channel();
        } catch (InterruptedException e) {
            log.error("connecting failed due to interruption");
        } catch (Exception e) {
            log.error("connecting failed");
        }
        log.trace("finish connecting to " + HOST);
    }


    /**
     * Tell the sender to send message
     *
     * @param obj
     */
    public void send(Object obj) {
        try {
            if (channel == null) {
                cf = boot.connect(HOST, PORT).sync();
                channel = cf.channel();
            }
            // Message will be ignored since remote is not ready
            if (channel != null) {
                cf = channel.writeAndFlush(obj);
//                log.trace("request sent" + channel.remoteAddress());
//                log.trace(obj);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            log.trace("node " + HOST + " failed, skip");
        }
    }


    /**
     * Tell the sender to shutdown
     */
    public void close() {
        try {
            log.trace("sender tries to shutdown");
//            if (!channel.closeFuture().await(5000)) {
//                log.trace("sender timed-out");
//            }
            channel.closeFuture().sync();
            cf.channel().close().sync();
        } catch (InterruptedException e) {
            log.error("exception caught during shutdown sender");
        } finally {
            group.shutdownGracefully();
        }
        log.trace("shutdown complete");
    }
}
