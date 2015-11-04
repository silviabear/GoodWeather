package edu.illinois.cs425_mp1.network;

import edu.illinois.cs425_mp1.parser.NetworkMessageParser;
import edu.illinois.cs425_mp1.types.Command;
import edu.illinois.cs425_mp1.types.Reply;
import edu.illinois.cs425_mp1.types.Request;
import io.netty.channel.*;

import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;


/**
 * This Class is the listener file handler class that handles all incoming files
 * Created by Wesley on 8/31/15.
 */
@ChannelHandler.Sharable
public class ListenerFileHandler extends SimpleChannelInboundHandler<String> {

    static Logger log = LogManager.getLogger("networkLogger");

    private String filePath = null;

    private StringEncoder STRING_ENCODER = new StringEncoder(CharsetUtil.UTF_8);
    private LineBasedFrameDecoder LINE_DECODER = new LineBasedFrameDecoder(8192);
    private StringDecoder STRING_DECODER = new StringDecoder(CharsetUtil.UTF_8);
    private ChunkedWriteHandler CHUNKED_HANDLER = new ChunkedWriteHandler();

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
     * Handles incoming msg for string
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        log.trace("message received at listener");
        System.out.println(msg);
        log.trace(msg);

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
