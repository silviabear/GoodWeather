package edu.illinois.cs425_mp1.network;

import edu.illinois.cs425_mp1.adapter.Adapter;
import edu.illinois.cs425_mp1.types.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This is the p2p message sender, one way communication. a proper close() should be called after every run()
 * Created by Wesley on 8/31/15.
 */
public class FileSender implements Sender {

    static Logger log = LogManager.getLogger("networkLogger");

    String HOST;
    int PORT;

    private ChannelFuture cf;
    private Channel channel;
    private EventLoopGroup group;

    public FileSender(String host, int port) {
        this.HOST = host;
        this.PORT = port;
    }

    /**
     * Connecting the server(listener)
     */
    public void run() {
        log.trace("sender tries self-configuring on " + HOST + " @" + PORT);
        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(
                                new StringEncoder(CharsetUtil.UTF_8),
                                new LineBasedFrameDecoder(8192),
                                new StringDecoder(CharsetUtil.UTF_8),
                                new ChunkedWriteHandler());
                    }
                })
                .option(ChannelOption.TCP_NODELAY, true);

        try {
            log.trace("sender finished configuration, start connecting " + HOST + " @" + PORT);
            cf = b.connect(HOST, PORT).sync();
            channel = cf.channel();
        } catch (InterruptedException e) {
            log.error("connecting failed due to interruption");
        } catch (Exception e) {
            log.error("connecting failed");
        }
        log.trace("finish connecting to " + HOST);
    }

    /**
     * Tell the sender to send file
     * Note sending of file cannot be serialized
     *
     * @param localPath
     * @throws IOException (contains FileNotFoundException)
     */
    public void sendFile(String localPath, String tgrPath) throws IOException {
        log.trace("sender sends file: " + localPath);
        long length = -1;
        try {
            RandomAccessFile raf = new RandomAccessFile(localPath, "r");
            length = raf.length();
            if (length < 0 && raf != null)
                raf.close();
            cf = channel.write("OK:" + raf.length() + '\n');
            cf = channel.write("Path:" + tgrPath + '\n');
            cf = channel.write(new DefaultFileRegion(raf.getChannel(), 0, length));
            cf = channel.writeAndFlush("\nEof\n");
            log.trace("file length " + length);
        } catch (FileNotFoundException e){
            cf = channel.write("FileNotFound\n");
            cf = channel.writeAndFlush("\n");
        }
    }

    /**
     * Tell the sender to shutdown
     */
    public void close() {
        try {
            log.trace("sender tries to shutdown");
            cf.channel().close().sync();
        } catch (InterruptedException e) {
            log.error("exception caught during shutdown sender");
        } finally {
            group.shutdownGracefully();
        }
        log.trace("shutdown complete");
    }
}
