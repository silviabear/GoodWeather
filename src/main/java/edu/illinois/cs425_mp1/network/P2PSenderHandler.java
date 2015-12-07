package edu.illinois.cs425_mp1.network;

import edu.illinois.cs425_mp1.parser.NetworkMessageParser;
import edu.illinois.cs425_mp1.types.Reply;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

/**
 * This class handles all the events
 * Created by Wesley on 8/31/15.
 */
public class P2PSenderHandler extends ChannelInboundHandlerAdapter {
    static Logger log = LogManager.getLogger("networkLogger");

    /**
     * defines what will happen when channel opens
     *
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
     * Defines what will happen upon reading msg from server
     *
     * @param ctx
     * @param reply
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object reply){
        log.trace("message received at sender");
        assert(reply instanceof Reply);
        Reply rpl = (Reply) reply;
        log.trace("receive reply: " + rpl.toString());
        NetworkMessageParser.acceptNetworkReply(rpl);
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
    	System.out.println("FAILLLL");
    	cause.printStackTrace();
        ctx.close();
    }


}
