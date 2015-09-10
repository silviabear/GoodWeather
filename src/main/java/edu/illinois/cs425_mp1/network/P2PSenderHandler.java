package edu.illinois.cs425_mp1.network;

import edu.illinois.cs425_mp1.types.Reply;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * This class handles all the events
 * Created by Wesley on 8/31/15.
 */
public class P2PSenderHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object reply) throws Exception {
        // TODO: Log returned msg
        if (reply instanceof Reply) {
            // TODO: print on shell and close current channel


            ctx.channel().close().sync();
        }


    }

}
