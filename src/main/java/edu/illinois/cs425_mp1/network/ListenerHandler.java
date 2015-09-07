package edu.illinois.cs425_mp1.network;


import edu.illinois.cs425_mp1.types.Request;
import io.netty.channel.*;

import java.net.InetAddress;
import java.util.Date;

/**
 * This Class is the listener handler class that handles all incoming messages
 * Created by Wesley on 8/31/15.
 */
@ChannelHandler.Sharable
public class ListenerHandler extends SimpleChannelInboundHandler<String> {


        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception{
            // TODO: How talk is done
            System.out.println("Connection setup! Ready to talk");
            ctx.write("You have connected to " + InetAddress.getLocalHost().getHostName() + "!\r\n");
            ctx.write("It is " + new Date() + " now.\r\n");
            ctx.flush();
        }

        @Override
        public void channelRead0(ChannelHandlerContext ctx, String msg) {
            System.out.println("Received msg : " + msg);
            String response;
            boolean close = false;
            if (msg.length() == 0) {
                response = "Please type something.\r\n";
            } else if ("bye".equals(msg.toLowerCase())) {
                response = "Have a good day!\r\n";
                close = true;
            } else {
                response = "Did you say '" + msg + "'?\r\n";
            }


            ChannelFuture future = ctx.writeAndFlush(response);

            if (close) {
                future.addListener(ChannelFutureListener.CLOSE);
            }

        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
}
