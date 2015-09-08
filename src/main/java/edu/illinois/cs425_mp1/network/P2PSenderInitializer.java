package edu.illinois.cs425_mp1.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * Created by Wesley on 9/3/15.
 */
public class P2PSenderInitializer extends ChannelInitializer<SocketChannel> {
//    private static final StringDecoder DECODER = new StringDecoder();
//    private static final StringEncoder ENCODER = new StringEncoder();
    private static final P2PSenderHandler HANDLER = new P2PSenderHandler();

    private static final ObjectDecoder DECODER = new ObjectDecoder(ClassResolvers.cacheDisabled(null));
    private static final ObjectEncoder ENCODER = new ObjectEncoder();

    @Override
    public void initChannel(SocketChannel ch){
        ChannelPipeline pipeline = ch.pipeline();

        // Add the text line codec combination first,
        // pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));

        pipeline.addLast(DECODER);//not necessary, but nah..
        pipeline.addLast(ENCODER);

        // and then business logic.
        pipeline.addLast(HANDLER);

    }
}
