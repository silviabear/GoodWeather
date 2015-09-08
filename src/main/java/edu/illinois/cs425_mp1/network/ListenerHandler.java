package edu.illinois.cs425_mp1.network;

import edu.illinois.cs425_mp1.types.Message;
import edu.illinois.cs425_mp1.types.Request;
import edu.illinois.cs425_mp1.util.ObjectStringSerializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

/**
 * This Class is the listener handler class that handles all incoming messages
 * Created by Wesley on 8/31/15.
 */
class ListenerHandler extends SimpleChannelInboundHandler<String> {


        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception{
            // TODO: How talk is done?
            ctx.write("You have connected to " + InetAddress.getLocalHost().getHostName() + "!\r\n");
            ctx.write("It is " + new Date() + " now.\r\n");
            ctx.flush();
        }

        @Override
        public void channelRead0(ChannelHandlerContext ctx, String msg) {
            //TODO: Parse the msg, do the work and send it here
            ctx.write(msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
}
