package edu.illinois.cs425_mp1.network;

import edu.illinois.cs425_mp1.adapter.Adapter;
import io.netty.channel.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;


/**
 * This Class is the file listener handler class that handles all files transfer
 * Created by Wesley on 8/31/15.
 */
@ChannelHandler.Sharable
public class FileListenerHandler extends SimpleChannelInboundHandler<String> {

    static Logger log = LogManager.getLogger("networkLogger");

    String tgrPath = null;

    BufferedWriter writer = null;

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
     * Handles incoming line of file
     * Notice some prefix stuff is HARD-CODED
     * So it will not work in general case
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        try {
            if (msg.startsWith("OK:")) {
                //File Transmit starts
                log.trace("transmission starts");
                tgrPath = null;
                writer = null;
            } else if (msg.startsWith("Path:")) {
                tgrPath = Adapter.getDFSLocation() + msg.substring(5);
                writer = new BufferedWriter(new FileWriter(tgrPath));
            } else if (msg.equals("Eof")) {
                log.trace("tranmission ends");
                writer.flush();
                writer.close();
            } else {
                writer.append(msg);
                writer.newLine();
            }
        } catch (IOException e) {
            log.error("error during write to " + tgrPath);
        }
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
        cause.printStackTrace();
        log.error("closing current channel");
        ctx.close();
    }
}
