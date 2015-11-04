package edu.illinois.cs425_mp1.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Wesley on 11/3/15.
 */
public class FileSenderHandler extends SimpleChannelInboundHandler<String> {

    static Logger log = LogManager.getLogger("networkLogger");
    /**
     * Handles incoming msg for string
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        log.trace("message received at listener");
        log.trace(msg);

    }

}
