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
        assert(msg instanceof Request);
        Request req = (Request) msg;
        log.trace("receive request: " + req.toString());
        log.trace("parsing request and executing request");
        Reply rep = NetworkMessageParser.acceptNetworkRequest(req);
        ChannelFuture cf = ctx.write(rep);
        log.trace("write reply back: " + rep.toString());
        if (req instanceof ShutdownRequest) {
            cf.addListener(ChannelFutureListener.CLOSE);
            return;
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
        log.error("closing current channel");
        ctx.close();
    }
}
