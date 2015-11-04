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
 * This Class is the listener handler class that handles all incoming messages
 * Created by Wesley on 8/31/15.
 */
@ChannelHandler.Sharable
public class ListenerHandler extends ChannelInboundHandlerAdapter {

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
            if (req.getCommand() == Command.WRITE) {
                log.trace("receive file write request to " + req.getBody());
                filePath = req.getBody();
                ctx.pipeline().addLast(STRING_ENCODER,
                                       LINE_DECODER,
                                       STRING_DECODER,
                                       CHUNKED_HANDLER);
                return;
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
        }
        if (msg instanceof DefaultFileRegion) {
            // Received a place to store file before
            log.trace("receive file body");
            if (filePath != null) {
                try {
                    RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
                    FileChannel rafChannel = raf.getChannel();
                    log.trace("write to " + filePath);
                    ((DefaultFileRegion) msg).transferTo(rafChannel, 0);
                    log.trace("finished write to " + filePath);
                } catch (FileNotFoundException e) {
                    //DO NOTHING!
                } catch (IOException e) {
                    log.error("cannot write to " + filePath);
                } finally {
                    // reset filePath to null
                    log.trace("finished write request");
                    ctx.pipeline().remove(STRING_ENCODER);
                    ctx.pipeline().remove(LINE_DECODER);
                    ctx.pipeline().remove(STRING_DECODER);
                    ctx.pipeline().remove(CHUNKED_HANDLER);
                    filePath = null;
                }
            } else {
                log.trace("no path specified, file body get dropped");
            }
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
