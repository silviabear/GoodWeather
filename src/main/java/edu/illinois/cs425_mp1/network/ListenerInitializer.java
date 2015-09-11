package edu.illinois.cs425_mp1.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * Change all the initializer parameter here
 * Created by Wesley on 9/3/15.
 * NOTE: DO NOT USE THIS CLASS. IT WILL MAKE DECODER HANDLE NOT SHAREABLE! (I HAVE NO F**KING CLUE WHY)
 * DO NOT USE THIS!!
 */
public class ListenerInitializer extends ChannelInitializer<SocketChannel> {

    private static final ListenerHandler HANDLER = new ListenerHandler();


    private static final ObjectDecoder OBJDECODER = new ObjectDecoder(ClassResolvers.cacheDisabled(null));
    private static final ObjectEncoder OBJENCODER = new ObjectEncoder();

    @Override
    public void initChannel(SocketChannel ch){
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(OBJENCODER);//not necessary, but nah..
        pipeline.addLast(OBJDECODER);
        pipeline.addLast(HANDLER);

    }
}
