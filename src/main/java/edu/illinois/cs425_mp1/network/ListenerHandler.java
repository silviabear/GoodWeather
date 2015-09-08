package edu.illinois.cs425_mp1.network;


import edu.illinois.cs425_mp1.types.Command;
import edu.illinois.cs425_mp1.types.Request;
import io.netty.channel.*;


/**
 * This Class is the listener handler class that handles all incoming messages
 * Created by Wesley on 8/31/15.
 */
@ChannelHandler.Sharable
public class ListenerHandler extends ChannelInboundHandlerAdapter {


        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception{
            // TODO: Log channel active time
            System.out.println("Connection setup! Ready to talk");
            ctx.flush();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            // TODO: Log msg received
            // If msg is a Request
            if (msg instanceof Request){
                Request req = (Request) msg;
                System.out.println("Msg received, Echo it back : " + req.getBody());
                // TODO: Exec this request and reply back
                ChannelFuture cf = ctx.write(msg);

                if(req.getCommand() == Command.SHUTDOWN)
                    cf.addListener(ChannelFutureListener.CLOSE);
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
