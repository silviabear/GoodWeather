package edu.illinois.cs425_mp1.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backtype.storm.tuple.Tuple;
import edu.illinois.cs425_mp1.monitor.HeartbeatAdapter;
import edu.illinois.cs425_mp1.types.MembershipList;


/**
 * Created by Wesley on 9/23/15.
 */
@ChannelHandler.Sharable
public class UDPListenerHandler extends ChannelInboundHandlerAdapter {

    static Logger log = LogManager.getLogger("networkLogger");
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
//        System.out.println("Channel active : " + ctx.channel().remoteAddress());
        log.trace("channel open for " + ctx.channel().remoteAddress());
        ctx.flush();
    }


    /**
     * Listener's action upon receive a message
     * @param ctx
     * @param reply
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object reply) {
        log.trace("obj received from " + ctx.channel().remoteAddress());
    	if(reply instanceof MembershipList) {
        	HeartbeatAdapter.acceptHeartbeat((MembershipList)reply);
        }
        return;
    }
    
    private void handleStream(Tuple reply) {
    	//TODO: handle the tuple
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * exception caught
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.trace("UDP Listener excpetion caught");
//        cause.printStackTrace();
    }
}
