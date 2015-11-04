package edu.illinois.cs425_mp1.network;

import io.netty.channel.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.InetSocketAddress;


/**
 * This Class is the file listener handler class that handles all files transfer
 * Created by Wesley on 8/31/15.
 */
@ChannelHandler.Sharable
public class FileListenerHandler extends SimpleChannelInboundHandler<String> {

    static Logger log = LogManager.getLogger("networkLogger");

    /**
     * Will be called when channel is active
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InetSocketAddress remote = (InetSocketAddress) ctx.channel().remoteAddress();
        log.trace("file channel opens for address " + remote.getHostString());
        ctx.flush();
    }

    /**
     * Handles incoming msg for string
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        log.trace(msg);

    }

    /**
     * This method will be called once the read completes
     *
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.trace("read complete");
        ctx.flush();
    }

    /**
     * This method will be called when exception is caught
     * For the purpose of mp1, just print it and discard
     *
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
}
