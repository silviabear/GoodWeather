package edu.illinois.cs425_mp1.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

<<<<<<< HEAD
import edu.illinois.cs425_mp1.adapter.Adapter;
import edu.illinois.cs425_mp1.monitor.HeartbeatAdapter;
import edu.illinois.cs425_mp1.monitor.HeartbeatBroadcaster;

import java.util.Objects;

=======
>>>>>>> 795e067f989e54386f27266b00ae87c62165929d
/**
 * This is the UDP message sender handler
 * Created by Wesley on 10/1/15.
 */
@ChannelHandler.Sharable
public class UDPSenderHandler extends ChannelInboundHandlerAdapter {
    static Logger log = LogManager.getLogger("networkLogger");

    private UDPSender sender;
    public UDPSenderHandler(UDPSender sender){
        this.sender = sender;
    }

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
<<<<<<< HEAD
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
=======
        log.trace("Sender UDP exception caught (connection timed-out)");
        log.trace("reinitilize sender");
        this.sender.close();
        this.sender.run();
//        cause.printStackTrace();
>>>>>>> 795e067f989e54386f27266b00ae87c62165929d
    }
}
