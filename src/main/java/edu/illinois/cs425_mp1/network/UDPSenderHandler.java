package edu.illinois.cs425_mp1.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is the UDP message sender handler
 * Created by Wesley on 10/1/15.
 */
public class UDPSenderHandler extends ChannelInboundHandlerAdapter {
    static Logger log = LogManager.getLogger("networkLogger");

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.trace("sender active");
        ctx.flush();
    }

    /**
     * Sender should not receive anything from remote
     * @param ctx
     * @param reply
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object reply){
        return;
    }

    /**
     * Caught excpetion
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.trace("UDP excpetion caught");
//        cause.printStackTrace();
        ctx.close();
    }
}
