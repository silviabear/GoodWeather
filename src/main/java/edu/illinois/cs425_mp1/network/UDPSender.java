package edu.illinois.cs425_mp1.network;

import edu.illinois.cs425_mp1.adapter.Adapter;
import edu.illinois.cs425_mp1.types.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
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

    public UDPSender(String host, int port) {
        this.HOST = host;
        this.PORT = port;
    }

    /**
     * Connecting the UDP listener
     */
    public void run() {
        log.trace("sender tries self-configuring on " + HOST + " @" + PORT);
        final ThreadFactory connectFactory = new DefaultThreadFactory("connect");
        group = new NioEventLoopGroup(1, connectFactory, NioUdtProvider.BYTE_PROVIDER);

//        group = new NioEventLoopGroup();
//        Bootstrap b = new Bootstrap();
//        b.group(group)
//                .channel(NioDatagramChannel.class)
//                .option(ChannelOption.SO_BROADCAST, true)
//                .handler(new ChannelInitializer<DatagramChannel>() {
//                    @Override
//                    public void initChannel(DatagramChannel ch) throws Exception {
//                        ChannelPipeline p = ch.pipeline();
//                        p.addLast(
//                                new ObjectEncoder(),
//                                new ObjectDecoder(200000000, ClassResolvers.cacheDisabled(null)),
//                                new UDPSenderHandler());
//                    }
//                });

        final Bootstrap boot = new Bootstrap();
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
            cf = boot.connect(HOST,PORT).sync();
            channel = cf.channel();
            log.trace("blah2");
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
     * @param msg
     */
    public void send(Message msg) {
        log.trace("udp sender sends msg: " + msg.toString());
        try {
//            cf = channel.writeAndFlush(new DatagramPacket(
//                    Unpooled.copiedBuffer("Hello?", CharsetUtil.UTF_8),
//                    new InetSocketAddress(HOST, PORT))).sync();
            cf = channel.writeAndFlush(msg);
            log.trace("request sent");
        } catch (Exception e) {
            log.trace("node " + HOST + "failed, skip");
            P2PSender[] channels = Adapter.getChannels();
            for (int i = 0; i < channels.length; i++) {
                if (channels[i] != null && channels[i].equals(HOST)) {
                    channels[i] = null;
                    break;
                }
            }
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
            cf.channel().close().sync();
        } catch (InterruptedException e) {
            log.error("exception caught during shutdown sender");
        } finally {
            group.shutdownGracefully();
        }
        log.trace("shutdown complete");
    }
}