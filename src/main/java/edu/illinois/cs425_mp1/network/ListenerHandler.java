package edu.illinois.cs425_mp1.network;

import edu.illinois.cs425_mp1.parser.NetworkMessageParser;
import edu.illinois.cs425_mp1.types.Reply;
import edu.illinois.cs425_mp1.types.Request;
import edu.illinois.cs425_mp1.types.ShutdownRequest;

import io.netty.channel.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;


/**
 * This Class is the listener handler class that handles all incoming messages
 * Created by Wesley on 8/31/15.
 */
@ChannelHandler.Sharable
public class ListenerHandler extends ChannelInboundHandlerAdapter {

    static Logger log = LogManager.getLogger("networkLogger");

    /**
     * Will be called when channel is active
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        InetSocketAddress remote = (InetSocketAddress)ctx.channel().remoteAddress();
        log.trace("channel opens for address " + remote.getHostString());
        ctx.flush();
    }

    /**
     * Handles incoming msg
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.trace("message received at listener");
        if (msg instanceof Request) {
            Request req = (Request) msg;
            log.trace("receive request: " + req.toString());
            // TODO: @Wesley: should this also be called for reply?

            log.trace("parsing request and executing request");
            // TODO: change to asynchronized way and separate IO later
//                Reply rep = NetworkMessageParser.acceptNetworkRequest(req);
            // TODO: @Wesley add sender method later, I don't know

            log.trace("write message back");
            ChannelFuture cf = ctx.write(msg);

            if (req instanceof ShutdownRequest) {
                cf.addListener(ChannelFutureListener.CLOSE);
                return;
            }
        } else if (msg instanceof Reply) {
            Reply rpl = (Reply)msg;
            log.trace("receive reply:" + rpl.getBody());
            NetworkMessageParser.acceptNetworkReply(rpl);
        }
    }

    /**
     * This method will be called once the read completes
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * This method will be called when exception is caught
     * For the purpose of mp1, just print it and discard
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exception is caught in channel, details printed on console");
        cause.printStackTrace();
        log.error("closing current channel");
        ctx.close();
    }

    private void handleRequest(Request req) {

    }
}
