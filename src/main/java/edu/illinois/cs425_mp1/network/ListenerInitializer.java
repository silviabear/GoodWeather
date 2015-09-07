package edu.illinois.cs425_mp1.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Change all the initializer parameter here
 * Created by Wesley on 9/3/15.
 */
public class ListenerInitializer extends ChannelInitializer<SocketChannel> {

    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();
    private static final ListenerHandler HANDLER = new ListenerHandler();

    private static final ObjectDecoder OBJDECODER = new ObjectDecoder(ClassResolvers.cacheDisabled(null));
    private static final ObjectEncoder OBJENCODER = new ObjectEncoder();

    @Override
    public void initChannel(SocketChannel ch){
        ChannelPipeline pipeline = ch.pipeline();

        // Add the text line codec combination first,
        // pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(OBJDECODER);
        pipeline.addLast(OBJENCODER);//not necessary, but nah..

        // and then business logic.
        pipeline.addLast(HANDLER);

    }
}
