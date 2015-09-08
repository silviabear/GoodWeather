package edu.illinois.cs425_mp1.network;

import edu.illinois.cs425_mp1.parser.NetworkMessageParser;
import edu.illinois.cs425_mp1.types.Reply;
import edu.illinois.cs425_mp1.types.Request;
import edu.illinois.cs425_mp1.types.ShutdownRequest;
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
            if (msg instanceof Request){
                Request req = (Request) msg;
                System.out.println("Msg received, Echo it back : " + req.getBody());
                // TODO: @Wesley: should this also be called for reply?
                ChannelFuture cf = ctx.write(msg);
                if(req instanceof ShutdownRequest) {
                    cf.addListener(ChannelFutureListener.CLOSE);
                    return;
                } 
                // TODO: change to asynchronized way and separate IO later
                Reply rep = NetworkMessageParser.acceptNetworkRequest(req);
                // TODO: @Wesley add sender method later, I don't know
                // if I should new a sender with certain address or 
                // it can directly reply
            } else if(msg instanceof Reply) {
            	NetworkMessageParser.acceptNetworkReply((Reply)msg);
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
        
        private void handleRequest(Request req) {
        	
        }
}
