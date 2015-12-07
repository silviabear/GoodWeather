package edu.illinois.cs425_mp1.network;
import edu.illinois.cs425_mp1.parser.NetworkMessageParser;
import edu.illinois.cs425_mp1.types.Command;
import edu.illinois.cs425_mp1.types.FileRequest;
import edu.illinois.cs425_mp1.types.Reply;
import edu.illinois.cs425_mp1.types.Request;
import io.netty.channel.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backtype.storm.LocalCluster;
import backtype.storm.topology.IRichBolt;
import backtype.storm.tuple.Ack;
import backtype.storm.tuple.Fin;
import backtype.storm.tuple.ITuple;
import backtype.storm.tuple.Tuple;

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
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InetSocketAddress remote = (InetSocketAddress) ctx.channel().remoteAddress();
        log.trace("channel opens for address " + remote.getHostString());
        ctx.flush();
    }

    /**
     * Handles incoming msg
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.trace("message received at listener");
        if (msg instanceof Reply) {
            Reply rpl = (Reply) msg;
            log.trace("receive reply: " + rpl.toString());
            NetworkMessageParser.acceptNetworkReply(rpl);
            return;
        }
        if (msg instanceof Request) {
            Request req = (Request) msg;
            if (msg instanceof FileRequest) {
                log.trace("receive file req: " + req.getCommand());
                FileRequest freq = (FileRequest) msg;
                NetworkMessageParser.acceptNetworkFileRequest(ctx, freq);
            } else {
                log.trace("receive request: " + req.toString());
                log.trace("parsing request and executing request");
                Reply rep = NetworkMessageParser.acceptNetworkRequest(req);
                log.trace("write message back: " + rep.toString());
                ChannelFuture cf = ctx.writeAndFlush(rep);

                if (rep.getCommand() == Command.SHUTDOWN) {
                    cf.addListener(ChannelFutureListener.CLOSE);
                    return;
                }
            }
            return;
        } else if(msg instanceof ITuple) {
        	handleTuple((ITuple)msg);
        }

    }

    private void handleTuple(ITuple tuple) {
    	LocalCluster.handleInput(tuple);
    }
    
    /**
     * This method will be called once the read completes
     *
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
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
        System.out.println(ctx.channel().remoteAddress().toString().split(":")[0].substring(1));
        if(LocalCluster.isSource) {
        	LocalCluster.fail(ctx.channel().remoteAddress().toString().split(":")[0].substring(1));
        }
        cause.printStackTrace();
        log.error("closing current channel");
        ctx.close();
    }
}
