package edu.illinois.cs425_mp1.network;

import edu.illinois.cs425_mp1.types.Reply;
import edu.illinois.cs425_mp1.types.Request;
import io.netty.channel.*;

/**
 * Created by Wesley on 8/31/15.
 */
public class P2PSenderHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // TODO: Log channel active time
        ctx.flush();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object reply) throws Exception {
        // TODO: Log returned msg
        if (reply instanceof Reply) {
            // TODO: print on shell and close current channel


            System.out.println("Closing Channel");
            ctx.channel().close().sync();
        }

        Request req = (Request) reply;
        System.out.println("Get echo meg : " + req.getBody());

    }

}
