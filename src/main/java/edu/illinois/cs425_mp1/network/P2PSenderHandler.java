package edu.illinois.cs425_mp1.network;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by Wesley on 8/31/15.
 */
public class P2PSenderHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Connection Setup! Ready to talk");
        ctx.flush();
    }


    @Override
    public void channelRead0(ChannelHandlerContext ctx, String reply) {
        // TODO: get the reply message, parse it and display on Shell
        System.err.println(reply);
    }
}
