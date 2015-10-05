package edu.illinois.cs425_mp1.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.illinois.cs425_mp1.adapter.Adapter;
import edu.illinois.cs425_mp1.monitor.HeartbeatAdapter;
import edu.illinois.cs425_mp1.monitor.HeartbeatBroadcaster;

import java.util.Objects;

/**
 * This is the UDP message sender handler
 * Created by Wesley on 10/1/15.
 */
public class UDPSenderHandler extends ChannelInboundHandlerAdapter {
    static Logger log = LogManager.getLogger("networkLogger");

    /**
     * UDP sender should not receive any message through the network
     * @param ctx
     * @param msg
     */
//    @Override
//    public void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
//        log.trace("received msg UDP: " + msg.content().toString(CharsetUtil.UTF_8));
//        return;
//    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Sender Active : " + ctx.channel().remoteAddress());
        ctx.flush();
    }

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
        cause.printStackTrace();
        ctx.close();
        String info = ctx.channel().remoteAddress().toString();
        String addr = info.split(":")[0].substring(1);
        String[] neighbors = Adapter.getNeighbors();
        int nodeId = 0;
        for(int i = 0; i < neighbors.length; i++) {
        	if(neighbors[i].equals(addr)) {
        		nodeId = i;
        		break;
        	}
        }
        System.out.println("node " + nodeId + " FAIL");
        HeartbeatBroadcaster.senders[nodeId] = new UDPSender(addr, HeartbeatAdapter.port);
        HeartbeatBroadcaster.senders[nodeId].run();
    }
}
