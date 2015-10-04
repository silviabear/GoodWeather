package edu.illinois.cs425_mp1.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.illinois.cs425_mp1.monitor.HeartbeatAdapter;
import edu.illinois.cs425_mp1.types.MembershipList;


/**
 * Created by Wesley on 9/23/15.
 */
@ChannelHandler.Sharable
public class UDPListenerHandler extends ChannelInboundHandlerAdapter {

    static Logger log = LogManager.getLogger("networkLogger");
//    @Override
//    public void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg){
//        log.trace("msg received at UDP listener");
//        System.out.println(msg.content().toString(CharsetUtil.UTF_8));
//
//    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Channel active : " + ctx.channel().remoteAddress());
        ctx.flush();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object reply) {
    	if(reply instanceof MembershipList) {
    		log.trace(reply);
        	HeartbeatAdapter.acceptHeartbeat((MembershipList)reply);
        }
        return;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.trace("UDP excpetion caught");
        cause.printStackTrace();
        ctx.close();
    }
}
