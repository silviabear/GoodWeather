package edu.illinois.cs425_mp1.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by Wesley on 9/3/15.
 */
public class P2PSenderInitializer extends ChannelInitializer<SocketChannel> {
    private static final P2PSenderHandler HANDLER = new P2PSenderHandler();

    private static final ObjectDecoder DECODER = new ObjectDecoder(ClassResolvers.cacheDisabled(null));
    private static final ObjectEncoder ENCODER = new ObjectEncoder();

    /**
     * init channel parameters
     * @param ch
     */
    @Override
    public void initChannel(SocketChannel ch){
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(DECODER);//not necessary, but nah..
        pipeline.addLast(ENCODER);

//        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        pipeline.addLast(HANDLER);

    }
}
